package painting;

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
        BUTT, ROUND, SQUARE
    }
    /**
     * Types de jointures des segments
     */
    public static enum LINE_JOIN {
        BEVEL, MITER, ROUND
    }
   
    /** Type de terminaison de la ligne **/
    private final LINE_CAP line_cap;
    /** Type de jointure **/
    private final LINE_JOIN line_join;
    /** Couleur du trait **/
    private final Color color;
    /** Epaisseur du trait */
    private final float width;
    /**
     * Séquence d'alternance des sections opaques et transparentes,
     * pour le dessin en traitillés des segments
     */
    private final float[] dashing_pattern;
    
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
        
        this.line_cap = lc;
        this.line_join = lj;
        this.color = c;
        this.width = thickness;
        this.dashing_pattern = dashing;
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
    public LINE_CAP getLineCap() { return line_cap; }
    /**
     * @return Type de jointure des segments du style défini
     */
    public LINE_JOIN getLineJoin() { return line_join; }
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
    public float[] getDashingPattern() { return dashing_pattern; }

    /** ====== METHODES PERMETTANT D'OBTENIR UN STYLE DERIVE DE CELUI-CI ======= */
    
    /**
     * Retourne une style dérivé de ce style ayant une largeur de trait différente
     * @param width Largeur du trait
     * @return Style dérivé dont seul la largeur du trait change
     */
    public LineStyle withWidth(float width) {
        return new LineStyle(getLineCap(), getLineJoin(), getColor(), width, getDashingPattern());
    }
    
    /**
     * Retourne une style dérivé de ce style ayant un type de terminaison différent
     * @param lc Type de terminaison
     * @return Style dérivé dont seul le type de terminaison change
     */
    public LineStyle withLineCap(LINE_CAP lc) {
        return new LineStyle(lc, getLineJoin(), getColor(), getWidth(), getDashingPattern());
    }
    
    /**
     * Retourne une style dérivé de ce style ayant un type de jointure différent
     * @param lj Type de jointure
     * @return Style dérivé dont seul le type de jointure change
     */
    public LineStyle withLineJoin(LINE_JOIN lj) {
        return new LineStyle(getLineCap(), lj, getColor(), getWidth(), getDashingPattern());
    }
    
    /**
     * Retourne une style dérivé de ce style ayant une couleur différente
     * @param c Couleur du trait
     * @return Style dérivé dont seul la couleur change
     */
    public LineStyle withColor(Color c) {
        return new LineStyle(getLineCap(), getLineJoin(), c, getWidth(), getDashingPattern());
    }
    
    /**
     * Retourne une style dérivé de ce style ayant une séquence d'alternance différente pour le dessin traitillé
     * @param pattern Séquence d'alternance pour le dessin en traitillé
     * @return Style dérivé dont seul la séquence d'alternance pour le dessin en traitillé change
     */
    public LineStyle withDashingPattern(float[] pattern) {
        return new LineStyle(getLineCap(), getLineJoin(), getColor(), getWidth(), pattern);
    }
}
