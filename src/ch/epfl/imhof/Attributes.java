package ch.epfl.imhof;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Représente un ensemble d'attributs et la valeur qui leur est associée. Les
 * attributs et leur valeur sont des chaînes de caractères. Cette classe n'est
 * donc rien d'autre qu'une table associative immuable dont les clefs et les
 * valeurs sont des chaînes de caractères.
 *
 * @author Thierry Treyer (235116)
 * @author Dominique Roduit (234868)
 */
public final class Attributes {
    /** L'ensemble des attributs */
    private final Map<String, String> attr;

    /**
     * Le builder associé à la classe Attributes
     * @author Thierry Treyer (235116)
     * @author Dominique Roduit (234868)
     *
     */
    public final static class Builder {
        /** Le hash modifiable servant à la construction */
        private Map<String, String> attr = new HashMap<String, String>();

        /**
         * Ajoute l'association (clef, valeur) donnée à l'ensemble d'attributs
         * en cours de construction. Si un attribut de même nom avait déjà été
         * ajouté précédemment à l'ensemble, sa valeur est remplacée par celle
         * donnée.
         *
         * @param key
         *            La clef de l'association
         * @param value
         *            La valeur de l'association
         */
        public Builder put (String key, String value) {
            this.attr.put(key, value);
            return this;
        }

        /**
         * Construit un ensemble d'attributs contenant les associations
         * clef/valeur ajoutées jusqu'à présent
         *
         * @return L'ensemble d'attributs associé
         */
        public Attributes build () {
            return new Attributes(this.attr);
        }
    }

    /**
     * Construit un ensemble immuable d'attributs avec les paires clef/valeur
     * présentes dans la table associative donnée.
     *
     * @param attributes
     *            L'ensemble des attributs
     */
    public Attributes (Map<String, String> attributes) {
        this.attr = Collections.unmodifiableMap(new HashMap<String, String>(attributes));
    }

    /**
     * Retourne le nombre d'éléments de l'ensembles d'attributs.
     *
     * @return Le nombre de pair clef/valeur
     */
    public int size () {
        return this.attr.keySet().size();
    }

    /**
     * Retourne vrai si et seulement si l'ensemble d'attributs est vide.
     *
     * @return Vrai, si l'ensemble des attributs est vide
     */
    public boolean isEmpty () {
        return this.attr.isEmpty();
    }

    /**
     * Retourne vrai si l'ensemble d'attributs contient la clef donnée.
     *
     * @param key
     *            La clef dont on veut contrôler la présence
     *
     * @return Vrai, si l'ensemble d'attributs contient la clef
     */
    public boolean contains (String key) {
        return this.attr.containsKey(key);
    }

    /**
     * Retourne la valeur associée à la clef donnée, ou null si la clef n'existe
     * pas.
     *
     * @param key
     *            La clef dont on veut récupérer la valeur
     *
     * @return La valeur associée à la clef ou null si la clef n'existe pas
     */
    public String get (String key) {
        return this.attr.get(key);
    }

    /**
     * Retourne la valeur associée à la clef donnée, ou la valeur par défaut
     * donnée si aucune valeur ne lui est associée.
     *
     * @param key
     *            La clef dont on veut récupérer la valeur
     * @param defaultValue
     *            La valeur à retourner si la clef n'existe pas
     *
     * @return La valeur associée à la clef ou la valeur par défaut si la clef
     *         est absente
     */
    public String get (String key, String defaultValue) {
        return this.attr.getOrDefault(key, defaultValue);
    }

    /**
     * Retourne l'entier associé à la clef donnée, ou la valeur par défaut
     * donnée si aucune valeur ne lui est associée, ou si cette valeur n'est pas
     * un entier valide.
     *
     * @param key
     *            La clef dont on veut récupérer la valeur
     * @param defaultValue
     *            La valeur à retourner si la clef n'existe pas
     * @throws NumberFormatException
     *            Si la valeur de l'attribut n'est pas un entier
     * @return La valeur associée à la clef ou la valeur par défaut si la clef
     *         est absente
     */
    public int get (String key, int defaultValue) {
        try {
            return Integer.parseInt(this.attr.get(key));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * Retourne une version filtrée des attributs ne contenant que ceux dont le
     * nom figure dans l'ensemble passé.
     *
     * @param keysToKeep
     *            Un set des clefs que l'on veut récuperer
     *
     * @return Un nouvel ensemble Attributes contenant les clef demandées
     */
    public Attributes keepOnlyKeys (Set<String> keysToKeep) {
        Builder attrBuilder = new Builder();

        for(String key : keysToKeep) {
            if(this.contains(key))
                attrBuilder.put(key, this.attr.get(key));
        }

        return attrBuilder.build();
    }
}
