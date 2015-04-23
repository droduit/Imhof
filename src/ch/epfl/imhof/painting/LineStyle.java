package ch.epfl.imhof.painting;

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
    public static enum LineCap {
        Butt(BasicStroke.CAP_BUTT, "butt"),
        Round(BasicStroke.CAP_ROUND, "round"),
        Square(BasicStroke.CAP_SQUARE, "square");

        private final int awtCap;
        private final String svgCap;
        private LineCap (int awtCap, String svgCap) {
            this.awtCap = awtCap;
            this.svgCap = svgCap;
        }

        public int toAWTCap () { return this.awtCap; }
        public String toSVGCap () { return this.svgCap; }
    }
    /**
     * Types de jointures des segments
     */
    public static enum LineJoin {
        Bevel(BasicStroke.JOIN_BEVEL, "bevel"),
        Miter(BasicStroke.JOIN_MITER, "miter"),
        Round(BasicStroke.JOIN_ROUND, "round");

        private final int awtJoin;
        private final String svgJoin;
        private LineJoin (int awtJoin, String svgJoin) {
            this.awtJoin = awtJoin;
            this.svgJoin = svgJoin;
        }

        public int toAWTJoin () { return this.awtJoin; }
        public String toSVGJoin () { return this.svgJoin; }
    }
   
    /** Type de terminaison de la ligne **/
    private final LineCap lineCap;
    /** Type de jointure **/
    private final LineJoin lineJoin;
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
    public LineStyle(LineCap lc, LineJoin lj, Color c, float thickness, float[] dashing) {
        if (thickness < 0)
            throw new IllegalArgumentException("La largeur du trait ne doit pas être négative");

        if (dashing != null) {
            for (int i = 0; i < dashing.length; ++i) {
                if (dashing[i] <= 0)
                    throw new IllegalArgumentException("L'un des éléments de la séquence d'alternance des segments est négatif ou nul");
            }
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
        this(LineCap.Butt, LineJoin.Miter, c, thickness, null);
    }
    
    /** ====== ACCESSEURS ======= */
    
    /**
     * @return Type de terminaison du style défini
     */
    public LineCap getLineCap() { return lineCap; }
    /**
     * @return Type de jointure des segments du style défini
     */
    public LineJoin getLineJoin() { return lineJoin; }
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
    public LineStyle withLineCap(LineCap lineCap) {
        return new LineStyle(lineCap, this.lineJoin, this.color, this.width, this.dashingPattern);
    }
    
    /**
     * Retourne une style dérivé de ce style ayant un type de jointure différent
     * @param lj Type de jointure
     * @return Style dérivé dont seul le type de jointure change
     */
    public LineStyle withLineJoin(LineJoin lineJoin) {
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

    public String toCSS () {
        return String.format(
            new StringBuilder()
                .append(".c%d { ")
                .append("fill: none; ")
                .append("stroke: %s; ")
                .append("stroke-width: %f; ")
                .append("stroke-linecap: %s; ")
                .append("stroke-linejoin: %s; ")
                .append("}\n").toString(),
            this.hashCode(),
            this.color.toHex(),
            this.width,
            this.lineCap.toSVGCap(),
            this.lineJoin.toSVGJoin()
        );
    }
}
