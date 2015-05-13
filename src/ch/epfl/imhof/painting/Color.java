package ch.epfl.imhof.painting;

/**
 * 
 * Une couleur représentée par ses composantes rouges, vertes et bleues.
 *
 * @author Thierry Treyer (235116)
 * @author Dominique Roduit (234868)
 *
 */
public final class Color {
    private final double r,g,b;
    
    /**
     * La couleur « rouge » (pur).
     */
    public final static Color RED = new Color(1,0,0);
    /**
     * La couleur « vert » (pur).
     */
    public final static Color GREEN = new Color(0,1,0);
    /**
     * La couleur « bleu » (pur).
     */
    public final static Color BLUE = new Color(0,0,1);
    /**
     * La couleur « noir ».
     */
    public final static Color BLACK = new Color(0,0,0);
    /**
     * La couleur « blanc ».
     */
    public final static Color WHITE = new Color(1,1,1);
    
    /**
     * Construit une nouvelle couleur à l'aide des trois composantes rouges vertes
     * et bleues qui doivent etre dans l'intervalle [0;1]
     * @param r Composante Rouge
     * @param g Composante Verte
     * @param b Composante Bleue
     * @throws IllegalArgumentException Si l'une des composantes est hors de l'intervalle [0;1].
     */
    private Color(double r, double g, double b) {
        if(!validParam(r))
            throw new IllegalArgumentException("Composante rouge invalide");
        if(!validParam(g))
            throw new IllegalArgumentException("Composante verte invalide");
        if(!validParam(b))
            throw new IllegalArgumentException("Composante bleue invalide");
        
        this.r = r;
        this.g = g;
        this.b = b;
    }

    public static Color random () {
        return new Color(Math.random(), Math.random(), Math.random());
    }
    
    /**
     * Construit une couleur grise d'intensité variable
     * @param v Composante définissant le niveau de gris
     * @return Couleur grise
     */
    public static Color gray(double v) {
        return new Color(v, v, v);
    }
    
    /**
     * Construit la couleur correspondant aux composantes rgb données
     * @param r Composante rouge [0;1]
     * @param g Composante Verte [0;1]
     * @param b Composante Bleue [0;1]
     * @return Couleur correspondante aux paramètres rgb passés
     */
    public static Color rgb(double r, double g, double b) {
        return new Color(r,g,b);
    }
    
    /**
     * Construit une couleur sur la base de la valeur rgb passée
     * @param rgb Composantes "empaquetées" contenant les 3 composantes indifiduelles
     * @return Couleur correspondant au paramètre rgb reçu
     */
    public static Color rgb(int rgb) {
        double r = ((rgb & 0xFF0000) >> 16)/255d;
        double g = ((rgb & 0x00FF00) >>  8)/255d;
        double b = ((rgb & 0x0000FF) >>  0)/255d;

        return new Color(r, g, b);
    }
    
    /**
     * Contrôle de la validité des composantes de la couleur qui doivent être dans une plage de [0;1]
     * @param p Composante de couleur
     * @return vrai si la composante est dans une plage valide
     */
    private static boolean validParam(double p) {
        return p>=0.0 && p<=1.0;
    }
    
    /**
     * Retourne la composante rouge de la couleur, comprise entre 0 et 1.
     *
     * @return la composante rouge de la couleur.
     */
    public double r() { return r; }

    /**
     * Retourne la composante verte de la couleur, comprise entre 0 et 1.
     *
     * @return la composante verte de la couleur.
     */
    public double g() { return g; }

    /**
     * Retourne la composante bleue de la couleur, comprise entre 0 et 1.
     *
     * @return la composante bleue de la couleur.
     */
    public double b() { return b; }
    
    /**
     * Multiplication de deux couleurs entre elles
     * @param that Deuxième couleur
     * @return Nouvelle couleur résultant de la multiplication des 2 couleurs
     */
    public Color multiplyWith(Color that) {
        return new Color(this.r*that.r, this.g*that.g, this.b*that.b);
    }
    
    /**
     * Convertit la couleur en une couleur AWT.
     * @return La couleur AWT correspondant à la couleur réceptrice.
     */
    public java.awt.Color toAWTColor() {
        return new java.awt.Color((float)r, (float)g, (float)b);
    }

    public int getRGB () {
        int r = (int)(this.r * 255d);
        int g = (int)(this.g * 255d);
        int b = (int)(this.b * 255d);

        return (r << 16) | (g << 8) | (b << 0);
    }
    
    public String toHex () {
        return String.format("#%02X%02X%02X", (int)(this.r * 255), (int)(this.g * 255), (int)(this.b * 255));
    }

    public String toCSS () {
        return String.format(".c%d { fill: %s; }\n", this.hashCode(), this.toHex());
    }
}
