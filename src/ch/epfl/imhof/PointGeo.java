package ch.epfl.imhof;

/**
 * Un point à la surface de la Terre, en coordonnées sphériques.
 *
 * @author Thierry Treyer (235116)
 * @author Dominique Roduit (234868)
 */
public final class PointGeo {
    private final double longitude;
    private final double latitude;

    /**
     * Construit un point avec la longitude et la latitude données.
     *
     * @param longitude
     *            La longitude du point, en radians
     * @param latitude
     *            La latitude du point, en radians
     * @throws IllegalArgumentException
     *             Si la longitude est hors de l'intervalle valide [-π;+π]
     * @throws IllegalArgumentException
     *             Si la latitude est hors de l'intervalle valide [-π/2;+π/2]
     */
    public PointGeo(double longitude, double latitude) {
        if (!isValidLongitude(longitude))
            throw new IllegalArgumentException(String.format(
                    "Longitude %.4f out of bound [-π;+π]", longitude));

        if (!isValidLatitude(latitude))
            throw new IllegalArgumentException(String.format(
                    "Latitude %.4f out of bound [-π/2;+π/2]", latitude));

        this.longitude = longitude;
        this.latitude = latitude;
    }

    /**
     * Retourne la longitude du point.
     *
     * @return La longitude du point
     */
    public double longitude() {
        return this.longitude;
    }

    /**
     * Retourne la latitude du point.
     *
     * @return La latitude du point
     */
    public double latitude() {
        return this.latitude;
    }

    /**
     * Contrôle que la longitude est dans l'intervalle valide [-π;+π].
     *
     * @param longitude
     *            La longitude à controller
     *
     * @return true : si la longitude est dans l'intervalle valide [-π;+π]
     */
    private static boolean isValidLongitude(final double longitude) {
        return (longitude >= -Math.PI && longitude <= Math.PI);
    }

    /**
     * Contrôle que la latitude est dans l'intervalle valide [-π/2;+π/2].
     *
     * @param latitude
     *            La latitude à controller
     *
     * @return true : si la latitude est dans l'intervalle valide [-π/2;+π/2]
     */
    private static boolean isValidLatitude(final double latitude) {
        return (latitude >= -(Math.PI / 2) && latitude <= (Math.PI / 2));
    }
}
