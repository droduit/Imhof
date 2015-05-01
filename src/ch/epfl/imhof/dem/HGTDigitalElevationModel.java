package ch.epfl.imhof.dem;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ShortBuffer;
import java.nio.channels.FileChannel.MapMode;
import java.util.Objects;
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

    private int lat_sw;
    private int long_sw;
    
    private FileInputStream stream;
    private ShortBuffer buffer;
    private final Long length;
    private final double delta = 1;
    
    public HGTDigitalElevationModel(File file) throws Exception {
        String filename = Objects.requireNonNull(file.getName());
        
        length = file.length();
        Long sqrt_res = (new Double(Math.sqrt(length/2))).longValue();
        
        if(2*Math.pow(sqrt_res, 2)==length)
           throw new IllegalArgumentException("La taille en octet n'a pas une racine carrée entière et paire");
        
        Pattern convention = Pattern.compile("^([NS]{1})(\\d{2})([EW]{1})(\\d{3})(\\.hgt)$");
        Matcher m = convention.matcher(filename);
        
        if(!m.matches())
            throw new IllegalArgumentException("La convention de nommage n'est pas respectée");
         
        int lat_sw = Integer.parseInt(m.group(2));
        if(m.group(1).equals("S"))
            lat_sw = -lat_sw;
        
        int long_sw = Integer.parseInt(m.group(4));
        if(m.group(3).equals("W"))
            long_sw = -long_sw;
        
        try {
            stream = new FileInputStream(file);
            buffer = stream.getChannel()
                    .map(MapMode.READ_ONLY, 0, length)
                    .asShortBuffer();
            
            // TODO calcul du delta (résolution angulaire du fichier HGT en radians)
        } finally {
            close();
        }
    }
    
    @Override
    public void close() throws Exception {
        buffer = null;
        stream.close();
    }

    @Override
    public Vector3 normalAt(PointGeo point) {
        if(point.longitude()>long_sw+1 || point.longitude()<long_sw ||
           point.latitude()>lat_sw+1 || point.latitude()<lat_sw)
            throw new IllegalArgumentException("Le point pour lequel le vecteur normal est demandé se trouve en dehors de la zone couverte par le MNT");
        
        double s = Earth.RADIUS*delta;
        
        // TODO Calcul des deltas avec les 4 voisins du point donné
        double dZa = 0;
        double dZb = 0;
        double dZc = 0;
        double dZd = 0;
        
        return new Vector3(0.5*s*(dZc-dZa), 0.5*s*(dZd-dZb), s*s);
    }

}
