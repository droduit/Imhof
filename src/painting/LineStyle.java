package painting;

/**
 * Regroupe tous les paramètres de style utiles au dessin d'une ligne.
 * @author Thierry Treyer (235116)
 * @author Dominique Roduit (234868)
 *
 */
public final class LineStyle {
    /**
     * Terminaisons des polylignes
     */
    public static enum LINE_CAP {
        BUTT, ROUND, SQUARE
    }
    /**
     * Jointures des segments
     */
    public static enum LINE_JOIN {
        BEVEL, MITER, ROUND
    }
   
    private final LINE_CAP line_cap;
    private final LINE_JOIN line_join;
    /** Couleur du trait **/
    private final Color color;
    /** Epaisseur du trait */
    private final float thickness;
    /**
     * Séquence d'alternance des sections opaques et transparentes,
     * pour le dessin en traitillés des segments
     */
    private final float[] dashing_pattern;
    
    /**
     * 
     * @param lc
     * @param lj
     * @param c
     * @param thickness
     * @param dashing
     * @throws IllegalArgumentException Si la largeur du trait est négative ou
     * si l'un des éléments de la séquence d'alernance des segments est négatif ou nul
     */
    public LineStyle(LINE_CAP lc, LINE_JOIN lj, Color c, float thickness, float[] dashing) {
        if(thickness<0)
            throw new IllegalArgumentException("La largeur du trait ne doit pas être négative");
        for(int i=0; i<dashing.length; ++i) {
            if(dashing[i]<=0)
                throw new IllegalArgumentException("L'un des éléments de la séquence d'alternance des segments est négatif ou nul");
        }
        
        this.line_cap = lc;
        this.line_join = lj;
        this.color = c;
        this.thickness = thickness;
        this.dashing_pattern = dashing;
    }
    
    public LineStyle(float thickness, Color c) {
        this(LINE_CAP.BUTT, LINE_JOIN.MITER, c, thickness, new float[0]);
    }
    
    public LINE_CAP getLineCap() { return line_cap; }
    public LINE_JOIN getLineJoin() { return line_join; }
    public Color getColor() { return color; }
    public float getThickness() { return thickness; }
    public float[] getDashingPattern() { return dashing_pattern; }
    
    
}
