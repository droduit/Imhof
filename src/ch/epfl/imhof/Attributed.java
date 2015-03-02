package ch.epfl.imhof;


/**
 * Représente une entité de type T dotée d'attributs.
 * 
 * @author Dominique Roduit (234868)
 *
 * @param <T>
 *            Type de l'objet value auquel on va attacher les attributs passés
 *            en arguments dans le constructeur.
 */
public final class Attributed<T> {
    
    /** Objet auquel on attache les attributs */
    private final T value;
    /** Attributs attachés à l'objet value */
    private final Attributes attributes;

    /**
     * Construit une valeur attribuée dont la valeur et les attributs sont ceux
     * donnés
     * 
     * @param value
     *            Objet auquel on attache les attributs
     * @param attributes
     *            Attributs attachés à l'objet value
     */
    public Attributed(T value, Attributes attributes) {
        if(value==null || attributes==null) 
            throw new NullPointerException("Les objets passés en paramètres ne doivent pas être null");
        
        this.value = value;
        this.attributes = attributes;
    }

    /**
     * Retourne l'objet auquel les attributs sont attachés
     * 
     * @return Objet auquel les attributs sont attachés
     */
    public T value() {
        return value;
    }

    /**
     * Retourne les attributs attachés à l'objet value
     * 
     * @return Attributs attachés à l'objet value
     */
    public Attributes attributes() {
        return attributes;
    }

    /**
     * Retourne vrai si et seulement si les attributs incluent celui dont le nom
     * est passé en argument.
     * 
     * Correspond à la méthode contains de Attributes.
     * 
     * @param attributeName
     *            Nom de l'attribut dont on veut contrôler l'existence dans la
     *            liste des attributs.
     * @return true si la liste des attributs contient celui passé en argument
     */
    public boolean hasAttribute(String attributeName) {
        return attributes.contains(attributeName);
    }

    /**
     * Retourne la valeur associée à l'attribut donné, ou null si celui-ci
     * n'existe pas.
     * 
     * Correspond à l'une des méthode get de la classe Attributes.
     * 
     * @param attributeName
     *            Nom de l'attribut à récupérer
     * @return Valeur correspondant au nom de l'attribut passé en argument
     */
    public String attributeValue(String attributeName) {
        return attributes.get(attributeName);
    }

    /**
     * Retourne la valeur associée à l'attribut donné, ou la valeur par défaut
     * donnée si celui-ci n'existe pas.
     * 
     * Correspond à l'une des méthodes get de la classe Attributes.
     * 
     * @param attributeName
     *            Nom de l'attribut à récupérer
     * @param defaultValue
     *            Valeur par défaut de l'attribut si aucune valeur ne lui est
     *            associée
     * @return Valeur correspondant au nom de l'attribut passé en argument
     */
    public String attributeValue(String attributeName, String defaultValue) {
        return attributes.get(attributeName, defaultValue);
    }

    /**
     * Retourne la valeur entière associée à l'attribut donné, ou la valeur par
     * défaut si celui-ci n'existe pas ou si la valeur qui lui est associée
     * n'est pas un entier valide.
     * 
     * Correspond à l'une des méthodes get de la classe Attributes.
     * 
     * @param attributeName
     *            Nom de l'attribut à récupérer
     * @param defaultValue
     *            Valeur par défaut de l'attribut si aucune valeur ne lui est
     *            associée ou si la valeur n'est pas un entier valide
     * @return Valeur correspondant au nom de l'attribut passé en argument
     */
    public int attributeValue(String attributeName, int defaultValue) {
        return attributes.get(attributeName, defaultValue);
    }
}
