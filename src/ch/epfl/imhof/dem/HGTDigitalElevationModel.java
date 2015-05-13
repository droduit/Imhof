package ch.epfl.imhof.dem;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ShortBuffer;
import java.nio.channels.FileChannel.MapMode;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ch.epfl.imhof.PointGeo;
import ch.epfl.imhof.Vector3;

/**
 * Représentation d'un fichier HGT en MNT
 * @author Thierry Treyer (235116)
 * @author Dominique Roduit (234868)
 *
 */
public class HGTDigitalElevationModel implements DigitalElevationModel {
    private static final double ARC = Math.toRadians(1);

    private final double delta;
    private final long sideSize;
    private final PointGeo origin;
    private final FileInputStream input;

    private ShortBuffer buffer;

    public PointGeo bottomLeft () { return this.origin; }
    public PointGeo topRight () {
        return new PointGeo(
                this.origin.longitude() + ARC,
                this.origin.latitude()  + ARC);
    }
    
    public HGTDigitalElevationModel(File file) throws IOException {
        long length = file.length();
        long pointsCount = length / 2;

        this.sideSize = (long)Math.sqrt(pointsCount);

        if (2 * sideSize * sideSize != length)
           throw new IllegalArgumentException("La taille du fichier n'est pas valide");
        
        this.delta = Math.toRadians(1d / (sideSize - 1));
        
        Matcher m = Pattern
            .compile("^([NS]{1})(\\d{2})([EW]{1})(\\d{3})\\.hgt$")
            .matcher(file.getName());
        
        if (!m.matches())
            throw new IllegalArgumentException("La convention de nommage n'est pas respectée");
         
        int lat = Integer.parseInt(m.group(2));
        if (m.group(1).equals("S"))
            lat = -lat;

        int lon = Integer.parseInt(m.group(4));
        if (m.group(3).equals("W"))
            lon = -lon;

        this.origin = new PointGeo(
                Math.toRadians(lon),
                Math.toRadians(lat));

        this.input = new FileInputStream(file);
        this.buffer = this.input
            .getChannel()
            .map(MapMode.READ_ONLY, 0, length)
            .asShortBuffer();
    }
    
    @Override
    public void close() throws IOException {
        this.buffer = null;
        this.input.close();
    }

    private boolean isInside (PointGeo p) {
        return
            p.latitude() >= this.origin.latitude() &&
            p.latitude() <= this.origin.latitude() + ARC &&
            p.longitude() >= this.origin.longitude() &&
            p.longitude() <= this.origin.longitude() + ARC;
    }

    @Override
    public Vector3 normalAt(PointGeo point) {
        if (!this.isInside(point))
            throw new IllegalArgumentException(
                    String.format("Le point (%.4f, %.4f) est en dehors de cette zone MNT [%d, %d] [%d, %d]",
                        Math.toDegrees(point.longitude()), Math.toDegrees(point.latitude()),
                        (int)Math.toDegrees(this.origin.longitude()), (int)Math.toDegrees(this.origin.longitude()) + 1,
                        (int)Math.toDegrees(this.origin.latitude()), (int)Math.toDegrees(this.origin.latitude()) + 1
                        ));

        int ss = (int)this.sideSize;
        int px =      (int)((point.longitude() - this.origin.longitude()) / this.delta);
        int py = ss - (int)Math.ceil((point.latitude()  - this.origin.latitude())  / this.delta);

        double z1 = this.buffer.get(ss * py + px);
        double z2 = this.buffer.get(ss * py + px + 1);
        double z3 = this.buffer.get(ss * (py - 1) + px);
        double z4 = this.buffer.get(ss * (py - 1) + px + 1);

        double dza = z2 - z1;
        double dzb = z3 - z1;
        double dzc = z3 - z4;
        double dzd = z2 - z4;
        
        double s = this.delta * Earth.RADIUS;
        return new Vector3(
            0.5 * s * (dzc - dza),
            0.5 * s * (dzd - dzb),
            s * s
        ).normalized();
    }
}
