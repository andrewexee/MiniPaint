package minipaint;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Clase principal que contiene la ventana, junto con sus componentes
 * @author Andrés Iglesias Camacho
 */
public class MiniPaint {

    private final String version = "1.0";
    private Modos modoActual = Modos.PINCEL;
    private int xInicio, yInicio;

    private JFrame ventana;
    private AreaDibujo areaDibujo;
    private Path2D.Float trazoActual;

    private Color colorActual = Color.BLACK;
    private float anchoTrazoActual = 1f;

    public MiniPaint() throws IOException {

        // region ----- Codigo Default de Santi -----
        ventana = new JFrame("Mini Paint Desarrollo Interfaces");
        ventana.setSize(720, 480);
        ventana.setIconImage(ImageIO.read(new File("./src/iconos/paleta.png")));
        areaDibujo = new AreaDibujo();



        areaDibujo.addMouseListener(new MouseAdapter() {

            //Evento al hacer clic
            public void mousePressed(MouseEvent e) {
                //Si es clic izquierdo, trazo
                if (SwingUtilities.isLeftMouseButton(e)){

                    // Comprobamos el modo de Dibujo actual
                    // Si es Pincel; creamos un trazo y lo agregamos al area de dibujo
                    if (modoActual == Modos.PINCEL){
                        trazoActual = new Path2D.Float();
                        trazoActual.moveTo(e.getX(), e.getY());
                        areaDibujo.agregarTrazo(trazoActual, colorActual, anchoTrazoActual, false);

                    }
                    // Si es Rectangulo; almacenamos la posicion inicial al presionar
                    else if (modoActual == Modos.RECTANGULO || modoActual == Modos.RECTANGULO_RELLENO) {
                        // Guardamos Punto inicial del rectángulo
                        xInicio = e.getX();
                        yInicio = e.getY();
                    }
                }
            }

            // Evento al levantar
            public void mouseReleased(MouseEvent e) {

                // Capturamos cuando se suelta el clic en caso de que el modo sea RECTANGULO
                if (SwingUtilities.isLeftMouseButton(e)){
                    if (modoActual == Modos.RECTANGULO ||  modoActual == Modos.RECTANGULO_RELLENO) {
                        // Creamos el rectángulo cuando se suelta el raton
                        trazoActual = new Path2D.Float();

                        // Calculamos las cordenadas y dimensiones
                        int x = Math.min(xInicio, e.getX());
                        int y = Math.min(yInicio, e.getY());
                        int ancho = Math.abs(e.getX() - xInicio);
                        int alto = Math.abs(e.getY() - yInicio);

                        // Dibujamos el rectangulo
                        trazoActual.moveTo(x, y);
                        trazoActual.lineTo(x + ancho, y);
                        trazoActual.lineTo(x + ancho, y + alto);
                        trazoActual.lineTo(x, y + alto);
                        trazoActual.closePath();

                        boolean conRelleno = (modoActual == Modos.RECTANGULO_RELLENO) ? true : false;
                        areaDibujo.agregarTrazo(trazoActual, colorActual, anchoTrazoActual, conRelleno);
                    }
                }
            }
        });

        //Evento al arrastrar
        areaDibujo.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                //Si mantenemos clic izquierdo, añadimos puntos al trazo actual
                if (!SwingUtilities.isRightMouseButton(e) &&  modoActual == Modos.PINCEL) {
                    trazoActual.lineTo(e.getX(), e.getY());
                    areaDibujo.repaint();
                }
            }
        });

        //endregion

        // region ----- Componentes de la ventana -----

        JMenuBar menuBar1 = new JMenuBar();
        JMenu archivo = new JMenu("Archivo");
        JMenuItem abrir = new JMenuItem("Abrir");
        JMenuItem guardar = new JMenuItem("Guardar");
        JMenu ayuda = new JMenu("Ayuda");
        JMenuItem acercaDe = new JMenuItem("Acerca de...");

        // ToolBar con "0" == Horientada en Horizontal
        JToolBar toolBar1 = new JToolBar(0);
        JButton retroceder = new JButton(getIcono("./src/iconos/undo.png"));
        JButton borrar = new JButton(getIcono("./src/iconos/delete.png"));
        JButton pincel = new JButton(getIcono("./src/iconos/pincel.png"));
        JButton cuadrado = new JButton(getIcono("./src/iconos/cuadrado.png"));
        JButton cuadradoFilled = new JButton(getIcono("./src/iconos/cuadradoFilled.png"));
        JButton color  = new JButton(getIcono("./src/iconos/circulo-de-color.png"));

        // ToolBar con "1" == Horientada en Vertical
        JToolBar toolBar2 = new JToolBar(1);
        JSlider sliderGrosor = new JSlider(JSlider.VERTICAL, 0, 20, (int) anchoTrazoActual);
        JLabel labelGrosor = new JLabel("1px");
        sliderGrosor.setMajorTickSpacing(5); // Establecemos marcas Grandes cada 5 Ticks
        sliderGrosor.setMinorTickSpacing(1); // Establecemos marcas Pequeñas cada Tick
        sliderGrosor.setPaintTicks(true); // Mostramos las marcas
        sliderGrosor.setPaintLabels(true); // Mostramos los numeros
        sliderGrosor.setMaximumSize(new Dimension(100, 300)); // Ajustamos el tamaño del Slider

        JPopupMenu popup = new JPopupMenu();
        JMenuItem borrarTodo = new JMenuItem(getIcono("./src/iconos/delete.png"));
        JMenuItem retrocederPaso = new JMenuItem(getIcono("./src/iconos/undo.png"));



        //endregion

        // region ----- Accion de los Botones -----

        //region >> Boton Abrir
        abrir.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Cargamos un FileChooser para seleccionar el archivo
                JFileChooser fc = new JFileChooser();

                // Establecemos un filtro de extensiones para mostrar los archivos con la extensión deseada
                fc.setFileFilter(new FileNameExtensionFilter("*png, *jpg, *jpeg", "png", "jpg", "jpeg"));

                // Si se selecciona un archivo, lo cargamos en un objeto File
                if (fc.showOpenDialog(ventana) == JFileChooser.APPROVE_OPTION) {
                    File archivo = fc.getSelectedFile();
                    try {
                        // Cargamos un BufferedImage con la imagen seleccionada (el objeto File)
                        BufferedImage image = ImageIO.read(archivo);

                        // Limpiamos el area de dibujo
                        areaDibujo.borrarTrazos();

                        // Utilizamos un metodo para establecer en la clase AreaDibujo la imagen de Fondo
                        areaDibujo.setImagenFondo(image);

                        JOptionPane.showMessageDialog(ventana, "Imagen Cargada con Exito", "Abrir", JOptionPane.INFORMATION_MESSAGE);

                    } catch (IOException ioe) {
                        JOptionPane.showMessageDialog(ventana, ioe.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
        //endregion

        //region >> Boton Guardar
        guardar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fc = new JFileChooser();

                // Establecemos un filtro de extensiones para mostrar los archivos con la extensión deseada
                fc.setFileFilter(new FileNameExtensionFilter("*png", "png"));

                if (fc.showSaveDialog(ventana) == JFileChooser.APPROVE_OPTION) {
                    File archivo = fc.getSelectedFile();
                    String ruta = archivo.getAbsolutePath();
                    if (!ruta.endsWith(".png")) {
                        archivo = new File(ruta + ".png");
                    }

                    try {
                        // Creamos un nuevo BufferedImage con las dimensiones del área de dibujo
                        BufferedImage image = new BufferedImage(areaDibujo.getWidth(), areaDibujo.getHeight(), BufferedImage.TYPE_INT_RGB);

                        // Creamos un objeto de la clase Graphics con el BufferedImage
                        // Para poder guardar en un archivo PNG el dibujo creado
                        Graphics2D g2 = image.createGraphics();

                        // Establecemos un fondo que ocupe el total del area de dibujo de un color blanco
                        // Para evitar que se guarde en el archivo una imagen con el fondo Negro
                        g2.setColor(Color.WHITE);
                        g2.fillRect(0, 0, areaDibujo.getWidth(), areaDibujo.getHeight());

                        // Almacenamose en el Objeto g2 el contenido del area de dibujo
                        areaDibujo.paintComponent(g2);
                        g2.dispose();

                        // Y guardamos la imagen creada en el archivo seleccionado
                        ImageIO.write(image, "png", archivo);

                        JOptionPane.showMessageDialog(ventana, "Imagen Guardada con Exito", "Guardar", JOptionPane.INFORMATION_MESSAGE);
                    } catch (IOException ioe) {
                        JOptionPane.showMessageDialog(ventana, ioe.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
        //endregion

        //region >> Boton AcercaDe
        acercaDe.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(ventana, "Desarrollado por: \nAndrés Iglesias Camacho\nVersion: " + version, "Ayuda", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        //endregion

        //region >> Boton Retroceder
        retroceder.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                areaDibujo.borrarUltimoTrazo();
            }
        });
        //endregion

        //region >> Boton Borrar
        borrar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                areaDibujo.borrarTrazos();
            }
        });
        //endregion

        //region >> Boton Pincel
        pincel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                modoActual = Modos.PINCEL;
            }
        });
        //endregion

        //region >> Boton Cuadrado
        cuadrado.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                modoActual = Modos.RECTANGULO;
            }
        });
        //endregion

        //region >> Boton Cuadrado Relleno
        cuadradoFilled.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                modoActual =  Modos.RECTANGULO_RELLENO;
            }
        });
        //endregion

        //region >> Boton Color
        color.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                colorActual =JColorChooser.showDialog(ventana, "Color", colorActual);
            }
        });
        //endregion

        //region >> Slider ToolBar2
        sliderGrosor.addChangeListener(e -> {
            anchoTrazoActual = sliderGrosor.getValue();
            labelGrosor.setText(String.format("%dpx", (int)anchoTrazoActual));
        });
        //endregion

        //region >>Botones Popup Menu
        borrarTodo.addActionListener(e -> {areaDibujo.borrarTrazos();});
        retrocederPaso.addActionListener(e -> {areaDibujo.borrarUltimoTrazo();});
        //endregion

        // endregion

        // region ----- Añadimos los componentes a la ventana -----

        //region <Barra de Menu>
        menuBar1.add(archivo);
        menuBar1.add(ayuda);
        archivo.add(abrir);
        archivo.add(guardar);
        ayuda.add(acercaDe);
        ventana.setJMenuBar(menuBar1);
        //endregion

        //region <Barra de Herramientas 1>
        toolBar1.addSeparator();
        toolBar1.add(retroceder);
        toolBar1.add(borrar);
        toolBar1.addSeparator();
        toolBar1.add(pincel);
        toolBar1.add(cuadrado);
        toolBar1.add(cuadradoFilled);
        toolBar1.addSeparator();
        toolBar1.add(color);
        ventana.add(toolBar1, BorderLayout.NORTH);
        //endregion

        //region <Barra de Herramientas 2>
        toolBar2.addSeparator();
        toolBar2.add(sliderGrosor);
        toolBar2.addSeparator();
        toolBar2.add(labelGrosor);
        ventana.add(toolBar2, BorderLayout.EAST);
        //endregion

        popup.add(retrocederPaso);
        popup.add(borrarTodo);
        areaDibujo.setComponentPopupMenu(popup);
        ventana.add(areaDibujo, BorderLayout.CENTER);

        //endregion


        //Visibilidad y cierre
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ventana.setLocationRelativeTo(null);
        ventana.setVisible(true);
    }

    /**
     * Metodo privado para poder insertar iconos
     * @param ruta ruta donde se encuentra el icono
     * @return un objeto ImageIcon para insertar en el componente deseado
     */
    private ImageIcon getIcono(String ruta) {
        BufferedImage imagenBase = null;
        try {
            imagenBase = ImageIO.read(new File(ruta));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Image imagenEscalada = imagenBase.getScaledInstance(16, 16, Image.SCALE_FAST);
        return new ImageIcon(imagenEscalada);
    }

    public static void main(String[] args) throws IOException {
        new MiniPaint();
    }
}
