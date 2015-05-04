package ch.epfl.imhof;

/**
 * Représente un vecteur tridimensionnel
 * @author Thierry Treyer (235116)
 * @author Dominique Roduit (234868)
 *
 */
public final class Vector3 {
    private final double x, y, z;

    public double x () { return this.x; };
    public double y () { return this.y; };
    public double z () { return this.z; };
    
    /**
     * Construit en vecteur tridimensionnel etant donné ses 3 composantes
     * @param x Composante x (abscisse)
     * @param y Composante y (ordonnée)
     * @param z Composante z (cote)
     */
    public Vector3(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    /**
     * @return La norme du vecteur
     */
    public double norm() {
        return Math.sqrt(x*x+y*y+z*z);
    }
    
    /**
     * @return Un vecteur normalisé (unitaire de même direction)
     */
    public Vector3 normalized() {
        double n = norm();
        return new Vector3(x/n, y/n ,z/n);
    }
    
    /**
     * Retourne le produit scalaire entre le récepteur et le vecteur passé en argument
     * @param that Second vecteur
     * @return Produit scalaire entre le vecteur courant et celui passé en argument
     */
    public double scalarProduct(Vector3 that) {
        return this.x*that.x+this.y*that.y+this.z*that.z;
    } 
}
