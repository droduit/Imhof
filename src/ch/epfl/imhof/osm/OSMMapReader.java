package ch.epfl.imhof.osm;

/**
 * Permet de construire une carte OpenStreetMap à partir de données stockées
 * dans un fichier au format OSM
 * 
 * @author Thierry Treyer (235116)
 * @author Dominique Roduit (234868)
 *
 */
public final class OSMMapReader {
    /**
     * Constructeur vide non-instanciable
     */
    private OSMMapReader() {}
    

    /**
     * Lit la carte OSM contenue dans le fichier de nom donné, en le décompressant avec gzip ssi le second argument est vrai.
     * Lève l'exception SAXException en cas d'erreur dans le format du fichier XML contenant la carte,
     * ou l'exception IOException en cas d'autre erreur d'entrée/sortie, p.ex. si le fichier n'existe pas.
     * @param fileName
     * @param unGZip
     * @return
     */
    public static OSMMap readOSMFile(String fileName, boolean unGZip) {
        return null;
    }

}
