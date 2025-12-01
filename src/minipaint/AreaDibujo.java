package minipaint;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import javax.swing.JComponent;

public class AreaDibujo extends JComponent {

    private BufferedImage imagen;

    private ArrayList<Trazo> trazos;

    public AreaDibujo() {
        trazos = new ArrayList<>();
    }

    public void agregarTrazo(Path2D trazo, Color color, float anchoTrazo, boolean relleno) {
        trazos.add(new Trazo(trazo, color, anchoTrazo, relleno));
        repaint();
    }

    public void borrarTrazos() {
        trazos.clear();
        repaint();
    }

    public void borrarUltimoTrazo() {
        if (trazos.size() > 0)
            trazos.remove(trazos.size() - 1);
        repaint();
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        // Comprobamos si se ha establecido alguna imagen de fondo
        // En caso afirmativo, la pintamos
        if (imagen != null) {
            g2.drawImage(imagen, 0, 0, this);
        }

        for (Trazo trazoActual : trazos) {
            g2.setColor(trazoActual.color);
            g2.setStroke(new BasicStroke(trazoActual.anchoTrazo));

            if (trazoActual.relleno) {
                g2.fill(trazoActual.trazo);
            } else {
                g2.draw(trazoActual.trazo);
            }

        }
    }

    /**
     * Metodo para pintar una imagen ya guardada
     * @param imagen Objeto BufferedImage para abrir
     */
    public void setImagenFondo(BufferedImage imagen) {
        // Establecemos la imagen de Fondo, según la imagen recibida por parámetros
        this.imagen = imagen;
        repaint();
    }

    private class Trazo {

        private Path2D trazo;
        private Color color;
        private float anchoTrazo;
        private boolean relleno; // Para verificar si está relleno o no

        public Trazo(Path2D trazo, Color color, float anchoTrazo,  boolean relleno) {
            this.trazo = trazo;
            this.color = color;
            this.anchoTrazo = anchoTrazo;
            this.relleno = relleno;
        }

    }
}