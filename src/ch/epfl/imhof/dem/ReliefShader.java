package ch.epfl.imhof.dem;

import java.util.function.Function;

import java.awt.Color;
import java.awt.image.Kernel;
import java.awt.image.ConvolveOp;
import java.awt.image.BufferedImage;

import ch.epfl.imhof.Vector3;
import ch.epfl.imhof.geometry.Point;
import ch.epfl.imhof.projection.Projection;

/**
 * Permet de dessiner un relief ombré coloré
 * @author Thierry Treyer (235116)
 * @author Dominique Roduit (234868)
 *
 */
public final class ReliefShader {
    private final Projection projection;
    private final DigitalElevationModel data;
    private final Vector3 lightDirection;

    /**
     * Construit une instance d'un relief ombré
     * @param projection Projection à utiliser
     * @param data Modèle numérique du terrain
     * @param lightDirection Vecteur pointant dans la direction de la source lumineuse
     */
    public ReliefShader (Projection projection, DigitalElevationModel data, Vector3 lightDirection) {
        this.projection = projection;
        this.data = data;
        this.lightDirection = lightDirection.normalized();
    }

    /**
     * Permet de calculer le noyau du flou gaussien étant donné son rayon
     * @param radius Rayon du flou gaussien
     * @return Données du noyau
     */
    private float[] buildKernelData (float radius) {
        int o = (int)Math.ceil(radius);
        int n = 2 * o + 1;

        float s = radius / 3;
        float ds = 2 * s * s;

        float sum = 0f;
        float[] data = new float[n];
        for (int p = 0; p < n; p++) {
            int d = o - p;

            data[p] = (float)Math.exp(- d * d / ds);
            sum += data[p];
        }

        for (int p = 0; p < n; p++)
            data[p] /= sum;

        return data;
    }

    /**
     * Permet de dessiner un relief ombré brut (sans floutage)
     * @param bottomLeft  Coin bas-gauche du relief à dessiner
     * @param topRight Coin haut-droit du relief à dessiner
     * @param width Largeur de l'image
     * @param height Hauteur de l'image
     * @param blurOffset Décalage du flou comme on ne floute pas les bords
     * @return Image du relief ombré brut
     */
    private BufferedImage drawRawRelief (Point bottomLeft, Point topRight, int width, int height, int blurOffset) {
        int rawWidth = width + 2 * blurOffset;
        int rawHeight = height + 2 * blurOffset;
        BufferedImage relief = new BufferedImage(rawWidth, rawHeight, BufferedImage.TYPE_INT_RGB);

        Point canvasBottomLeft = new Point(blurOffset, height + blurOffset);
        Point canvasTopRight   = new Point(width + blurOffset, blurOffset);
        Function<Point, Point> transform = Point.alignedCoordinateChange(canvasBottomLeft, bottomLeft, canvasTopRight, topRight);

        for (int x = 0; x < rawWidth; x++) {
            for (int y = 0; y < rawHeight; y++) {
                Point p = transform.apply(new Point(x, y));
                Vector3 n = this.data.normalAt( this.projection.inverse(p) );
                double ca = this.lightDirection.scalarProduct(n);

                float r = (float)(0.5d * (ca + 1d));
                float g = (float)(0.5d * (ca + 1d));
                float b = (float)(0.5d * (0.7d * ca + 1d));

                relief.setRGB(x, y, new Color(r, g, b).getRGB());
            }
        }

        return relief;
    }

    /**
     * Dessine le relief et applique le floutage demandé.
     * 
     * @param bottomLeft Coin bas-gauche du relief à dessiner en coordonnées du plan
     * @param topRight Coin haut-droit du relief à dessiner en coordonnées du plan
     * @param width Largeur de l'image à dessiner (px)
     * @param height Hauteur de l'image à dessiner (px)
     * @param gaussRadius Rayon de floutage
     * @return Image du relief flouté
     */
    public BufferedImage shadedRelief (Point bottomLeft, Point topRight, int width, int height, float gaussRadius) {
        int pixelRadius = (int)Math.ceil(gaussRadius);

        BufferedImage rawRelief = this.drawRawRelief(bottomLeft, topRight, width, height, pixelRadius);
        BufferedImage finalRelief = rawRelief;

        if (gaussRadius > 0) {
            int n = 2 * pixelRadius + 1;
            float[] kernelData = this.buildKernelData(gaussRadius);

            ConvolveOp horizontalBlur = new ConvolveOp(new Kernel(n, 1, kernelData), ConvolveOp.EDGE_NO_OP, null);
            ConvolveOp verticalBlur = new ConvolveOp(new Kernel(1, n, kernelData), ConvolveOp.EDGE_NO_OP, null);

            rawRelief = horizontalBlur.filter(rawRelief, null);
            rawRelief = verticalBlur.filter(rawRelief, null);
            finalRelief = rawRelief.getSubimage(pixelRadius, pixelRadius, width, height);
        }

        return finalRelief;
    }
}
