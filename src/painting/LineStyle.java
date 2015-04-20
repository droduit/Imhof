package painting;

import java.awt.Stroke;
import java.awt.BasicStroke;

/**
 * Regroupe tous les paramètres de style utiles au dessin d'une ligne.
 * 
 * @author Thierry Treyer (235116)
 * @author Dominique Roduit (234868)
 *
 */
public final class LineStyle {
    /**
     * Types de terminaisons des polylignes
     */
    public static enum LINE_CAP {
        BUTT(BasicStroke.CAP_BUTT),
        ROUND(BasicStroke.CAP_ROUND),
        SQUARE(BasicStroke.CAP_SQUARE);

        private final int awtCap;
        private LINE_CAP (int awtCap) { this.awtCap = awtCap; }

        public int toAWTCap () { return this.awtCap; }
    }
    /**
     * Types de jointures des segments
     */
    public static enum LINE_JOIN {
        BEVEL(BasicStroke.JOIN_BEVEL),
        MITER(BasicStroke.JOIN_MITER),
        ROUND(BasicStroke.JOIN_ROUND);

        private final int awtJoin;
        private LINE_JOIN (int awtJoin) { this.awtJoin = awtJoin; }

        public int toAWTJoin () { return this.awtJoin; }
    }
   
    /** Type de terminaison de la ligne **/
    private final LINE_CAP lineCap;
    /** Type de jointure **/
    private final LINE_JOIN lineJoin;
    /** Couleur du trait **/
    private final Color color;
    /** Epaisseur du trait */
    private final float width;
    /**
     * Séquence d'alternance des sections opaques et transparentes,
     * pour le dessin en traitillés des segments
     */
    private final float[] dashingPattern;
    
    /**
     * Construit un style de trait sur la base des arguments fournis.
     * @param lc Type de terminaison de la ligne
     * @param lj Type de jointure
     * @param c Couleur du trait
     * @param thickness Epaisseur du trait
     * @param dashing Séquence d'alternance pour le dessin en traitillés des segments
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
        
        this.lineCap = lc;
        this.lineJoin = lj;
        this.color = c;
        this.width = thickness;
        this.dashingPattern = dashing;
    }
    
    /**
     * Construit un style de trait avec l'épaisseur et la couleur spécifiée.
     * Les autres paramètres du style ont une valeur par défaut :
     * - Terminaison des lignes : BUTT.
     * - Jointure des segments : MITER
     * - Trait continu (pas de séquence d'alternance des segments opaques et transparents)
     * @param thickness Epaisseur du trait
     * @param c Couleur du trait
     */
    public LineStyle(float thickness, Color c) {
        this(LINE_CAP.BUTT, LINE_JOIN.MITER, c, thickness, new float[0]);
    }
    
    /** ====== ACCESSEURS ======= */
    
    /**
     * @return Type de terminaison du style défini
     */
    public LINE_CAP getLineCap() { return lineCap; }
    /**
     * @return Type de jointure des segments du style défini
     */
    public LINE_JOIN getLineJoin() { return lineJoin; }
    /**
     * @return Couleur des traits du style défini
     */
    public Color getColor() { return color; }
    /**
     * @return Largeur des traits du style défini
     */
    public float getWidth() { return width; }
    /**
     * @return Séquence d'alternance pour le dessin en traitillés
     */
    public float[] getDashingPattern() { return dashingPattern; }

    /** ====== METHODES PERMETTANT D'OBTENIR UN STYLE DERIVE DE CELUI-CI ======= */
    
    /**
     * Retourne une style dérivé de ce style ayant une largeur de trait différente
     * @param width Largeur du trait
     * @return Style dérivé dont seul la largeur du trait change
     */
    public LineStyle withWidth(float width) {
        return new LineStyle(this.lineCap, this.lineJoin, this.color, width, this.dashingPattern);
    }
    
    /**
     * Retourne une style dérivé de ce style ayant un type de terminaison différent
     * @param lc Type de terminaison
     * @return Style dérivé dont seul le type de terminaison change
     */
    public LineStyle withLineCap(LINE_CAP lineCap) {
        return new LineStyle(lineCap, this.lineJoin, this.color, this.width, this.dashingPattern);
    }
    
    /**
     * Retourne une style dérivé de ce style ayant un type de jointure différent
     * @param lj Type de jointure
     * @return Style dérivé dont seul le type de jointure change
     */
    public LineStyle withLineJoin(LINE_JOIN lineJoin) {
        return new LineStyle(this.lineCap, lineJoin, this.color, this.width, this.dashingPattern);
    }
    
    /**
     * Retourne une style dérivé de ce style ayant une couleur différente
     * @param c Couleur du trait
     * @return Style dérivé dont seul la couleur change
     */
    public LineStyle withColor(Color color) {
        return new LineStyle(this.lineCap, this.lineJoin, color, this.width, this.dashingPattern);
    }
    
    /**
     * Retourne une style dérivé de ce style ayant une séquence d'alternance différente pour le dessin traitillé
     * @param pattern Séquence d'alternance pour le dessin en traitillé
     * @return Style dérivé dont seul la séquence d'alternance pour le dessin en traitillé change
     */
    public LineStyle withDashingPattern(float[] dashingPattern) {
        return new LineStyle(this.lineCap, this.lineJoin, this.color, this.width, dashingPattern);
    }

    public Stroke toAWTStroke () {
        return new BasicStroke(this.width, this.lineCap.toAWTCap(), this.lineJoin.toAWTJoin(), 10f, this.dashingPattern, 0f);
    }
}
