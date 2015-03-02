package ch.epfl.imhof.osm;

import ch.epfl.imhof.Attributes;

/**
 * Sert de classe mère à toutes les classes représentant les entités OSM.
 * 
 * @author Dominique Roduit (234868)
 * @author Thierry Treyer (235116)
 *
 */
public abstract class OSMEntity {
    private final long id;
    private final Attributes attr;

    /**
     * Sert de classe mère à toutes les classes de bâtisseurs d'entités OSM.
     * 
     * @author Dominique Roduit (234868)
     *
     */
    public static abstract class Builder {
        private boolean isIncomplete = false;
        private long id;
        private Attributes.Builder attrBuilder = new Attributes.Builder();

        /**
         * Construit un bâtisseur pour une entité OSM identifiée par l'entier
         * donné.
         * 
         * @param id
         *            L'identifiant unique associé à l'entité
         */
        public Builder(long id) {
            this.id = id;
        }

        /**
         * Ajoute l'association (clef, valeur) donnée à l'ensemble d'attributs
         * de l'entité en cours de construction. Si un attribut de même nom
         * avait déjà été ajouté précédemment, sa valeur est remplacée par celle
         * donnée.
         * 
         * @param key
         *            Clef de l'attribut à définir pour l'entité en cours de
         *            construction
         * @param value
         *            Valeur associée à l'attribut donné par "key"
         */
        public void setAttribute(String key, String value) {
            attrBuilder.put(key, value);
        }

        /**
         * Déclare que l'entité en cours de construction est incomplète.
         */
        public void setIncomplete() {
            this.isIncomplete = true;
        }

        /**
         * Retourne vrai ssi l'entité en cours de construction est incomplète,
         * c-à-d si la méthode setIncomplete a été appelée au moins une fois sur
         * ce bâtisseur depuis sa création.
         * 
         * @return true : si l'entité en cours de construction est incomplète
         */
        public boolean isIncomplete() {
            return this.isIncomplete;
        }

        /**
         * Retourne l'identifiant unique de l'entité.
         * 
         * @return Identifiant unique de l'entité
         */
        public long id() {
            return this.id;
        }

        /**
         * Retourne les attributs de l'entité
         * 
         * @return Attributs de l'entité
         */
        public Attributes attributes() {
            return this.attrBuilder.build();
        }

    }

    /**
     * Construit une entité OSM dotée de l'identifiant unique et des attributs
     * donnés.
     * 
     * @param id
     *            Identifiant unique pour l'entité
     * @param attributes
     *            Attributs attachés à l'entité
     * @throws NullPointerException
     *             Lorsque l'objet contenant les attribut est null
     */
    public OSMEntity(long id, Attributes attributes) {
        if (attributes == null)
            throw new NullPointerException(
                    "L'objet attributes ne doit pas être null");

        this.id = id;
        this.attr = attributes;
    }

    /**
     * Retourne l'identifiant unique de l'entité.
     * 
     * @return Identifiant unique de l'entité
     */
    public long id() {
        return this.id;
    }

    /**
     * Retourne les attributs de l'entité
     * 
     * @return Attributs de l'entité
     */
    public Attributes attributes() {
        return this.attr;
    }

    /**
     * Retourne vrai ssi l'entité possède l'attribut passé en argument.
     * 
     * @param key
     *            Clé correspondant a l'attribut
     * @return true : si l'entité possède l'attribut
     */
    public boolean hasAttribute(String key) {
        return attr.contains(key);
    }

    /**
     * Retourne la valeur de l'attribut donné, ou null si celui-ci n'existe pas.
     * 
     * @param key
     *            Clé de l'attribut dont on veut la valeur
     * @return Valeur de l'attribut
     */
    public String attributeValue(String key) {
        return this.attr.get(key);
    }

}
