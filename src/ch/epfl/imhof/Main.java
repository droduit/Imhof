package ch.epfl.imhof;

import java.io.File;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

import ch.epfl.imhof.geometry.Point;
import ch.epfl.imhof.osm.OSMMap;
import ch.epfl.imhof.osm.OSMMapReader;
import ch.epfl.imhof.osm.OSMToGeoTransformer;
import ch.epfl.imhof.painting.*;
import ch.epfl.imhof.projection.*;
import ch.epfl.imhof.dem.*;

/**
 * Classe principale du projet Imhof
 * @author Thierry Treyer (235116)
 * @author Dominique Roduit (234868)
 *
 */
public class Main {
    private final static Projection PROJECTION = new CH1903Projection();
    private final static Vector3 LIGHT_DIRECTION = new Vector3(-1, 1, 1);
    private final static float GAUSS_FACTOR = 0.0017f;

    private static void usage () {
        System.out.println("imhof osm hgt BL_lon BL_lat TR_lon TR_lat dpi out [format]");
        System.out.println("       osm : Chemin du fichier OSM");
        System.out.println("       hgt : Chemin du fichier HGT");
        System.out.println("    BL_lon : Longitude du point en bas à gauche de la carte");
        System.out.println("    BL_lat : Latitude du point en bas à gauche de la carte");
        System.out.println("    TR_lon : Longitude du point en haut à droite de la carte");
        System.out.println("    TR_lat : Latitude du point en haut à droite de la carte");
        System.out.println("       dpi : Résolution de l'image");
        System.out.println("       out : Chemin où la carte sera enregistrée");
        System.out.println("    format : Format d'image (PNG, SVG,...) [Facultatif]");
    }

    /**
     * Conversion de dpi (points par pouces) vers dpm (points par metres (une invention personnelle))
     * @param dpi Résolution en dpi a convertir en points par metres
     * @return Nombre de points par metres
     */
    private static int dpiToDpm (int dpi) {
        return (int)Math.round((dpi / 2.54) * 100);
    }

    /**
     * Superpose les deux images (la carte et le relief)
     * @param back Image arrière (la carte)
     * @param front  Image superposée à front (le relief)
     * @return Image résultante de la fusion des deux images
     */
    private static BufferedImage multiplyImages (BufferedImage back, BufferedImage front) {
        int width = back.getWidth();
        int height = back.getHeight();

        if (width != front.getWidth() || height != front.getHeight())
            throw new IllegalArgumentException("Les images n'ont pas la même taille");

        BufferedImage composed = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Color backColor = Color.rgb(back.getRGB(x, y));
                Color frontColor = Color.rgb(front.getRGB(x, y));

                composed.setRGB(x, y, backColor.multiplyWith(frontColor).getRGB());
            }
        }

        return composed;
    }
    
    public static void main(String[] args) throws Exception {
        if (args.length < 8) {
            usage();
            System.exit(1);
        }

        /* Récupérations des paramètres */
        File osmFile = new File(args[0]);
        File demFile = new File(args[1]);
        PointGeo wgsBottomLeft = new PointGeo(
            Math.toRadians(Double.parseDouble(args[2])),
            Math.toRadians(Double.parseDouble(args[3])));
        PointGeo wgsTopRight = new PointGeo(
            Math.toRadians(Double.parseDouble(args[4])),
            Math.toRadians(Double.parseDouble(args[5])));
        int dpi = Integer.parseInt(args[6]);
        File outFile = new File(args[7]);

        /* Paramètre suplémentaires */
        String outFormat = "png";
        if (args.length >= 9)
            outFormat = args[8];
        
        OSMMap osmMap = OSMMapReader.readOSMFile(osmFile.getPath(), true);
        OSMToGeoTransformer transformer = new OSMToGeoTransformer(PROJECTION);

        Map map = transformer.transform(osmMap);
        Painter painter = SwissPainter.painter();

        /* Calculs des dimensions */
        Point chBottomLeft = PROJECTION.project(wgsBottomLeft);
        Point chTopRight = PROJECTION.project(wgsTopRight);

        int dpm = dpiToDpm(dpi);
        int height = (int)Math.round((double)Earth.RADIUS * (double)dpm * (wgsTopRight.latitude() - wgsBottomLeft.latitude()) / 25000d);
        int width = (int)Math.round((double)height * (chTopRight.x() - chBottomLeft.x()) / (chTopRight.y() - chBottomLeft.y()));
        float gaussRadius = dpm * GAUSS_FACTOR;

        DigitalElevationModel dem = new HGTDigitalElevationModel(demFile);
        ReliefShader reliefShader = new ReliefShader(PROJECTION, dem, LIGHT_DIRECTION);
        BufferedImage relief = reliefShader.shadedRelief(chBottomLeft, chTopRight, width, height, gaussRadius);

        if (outFormat.equals("svg")) {
            SVGCanvas canvas = new SVGCanvas(chBottomLeft, chTopRight, width, height, dpi, Color.WHITE);

            painter.drawMap(map, canvas);

            canvas.setRelief(relief);

            canvas.write(outFile.getPath());
        } else {
            Java2DCanvas canvas = new Java2DCanvas(chBottomLeft, chTopRight, width, height, dpi, Color.WHITE);

            painter.drawMap(map, canvas);

            BufferedImage out = multiplyImages(relief, canvas.image());

            ImageIO.write(out, outFormat, outFile);
        }
    }
}
