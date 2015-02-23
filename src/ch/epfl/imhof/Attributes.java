package ch.epfl.imhof;

import java.util.Map;
import java.util.Set;

import org.w3c.dom.Attr;

/**
 * Représente un ensemble d'attributs et la valeur qui leur est associée.
 * Les attributs et leur valeur sont des chaînes de caractères.
 * Cette classe n'est donc rien d'autre qu'une table associative immuable
 * dont les clefs et les valeurs sont des chaînes de caractères
 * @author Thierry Treyer (235116)
 * @author Dominique Roduit ()
 *
 */
public final class Attributes {
    private final Map<String, String> attr;
    
    /**
     * Construit un ensemble d'attributs avec les paires clef/valeur présentes dans la table associative donnée.
     * @param attributes
     */
    public Attributes(Map<String, String> attributes) {
        this.attr = attributes;
    } 
    
    public static final class Builder {
        private Map<String, String> a;
        /**
         * Ajoute l'association (clef, valeur) donnée à l'ensemble d'attributs en cours de construction.
         * Si un attribut de même nom avait déjà été ajouté précédemment à l'ensemble, sa valeur est remplacée par celle donnée.
         * @param key 
         * @param value
         */
        public void put(String key, String value) {
            a.put(key, value);
        }
        /**
         * Construit un ensemble d'attributs contenant les associations clef/valeur ajoutées jusqu'à présent
         * @return
         */
        public Attributes build() {
            return new Attributes(a);
        }
    }
    
    /**
     * Retourne vrai si et seulement si l'ensemble d'attributs est vide.
     * @return
     */
    public boolean isEmpty() {
        return (attr.isEmpty());
    }
    
    /**
     * Retourne vrai si l'ensemble d'attributs contient la clef donnée.
     * @param key
     * @return
     */
    public boolean contains(String key) {
        return attr.containsKey(key);
    }
   
    /**
     * Retourne la valeur associée à la clef donnée, ou null si la clef n'existe pas.
     * @param key
     * @return
     */
    public String get(String key){
        return contains(key) ? attr.get(key) : null;
    }
   
    /**
     * Retourne la valeur associée à la clef donnée, ou la valeur par défaut donnée si aucune valeur ne lui est associée.
     * @param key
     * @param defaultValue
     * @return
     */
    public String get(String key, String defaultValue) {
        return contains(key) ? get(key) : defaultValue;
    }
   
    /**
    * Retourne l'entier associé à la clef donnée, ou la valeur par défaut donnée si aucune valeur ne lui est associée,
    * ou si cette valeur n'est pas un entier valide.
    * @param key
    * @param defaultValue
    * @return
    * @throws
    */
    public int get(String key, int defaultValue) throws NumberFormatException {
        // TODO gestion exception : (si cette valeur n'est pas un entier valide -> defaultValue)
        return contains(key) ? Integer.parseInt(get(key)) : defaultValue;
    }
   
    /**
     * Retourne une version filtrée des attributs ne contenant que ceux dont le nom figure dans l'ensemble passé.
     * @param keysToKeep
     * @return
     */
    public Attributes keepOnlyKeys(Set<String> keysToKeep) {
       // TODO
        return null;
    }
   

}
