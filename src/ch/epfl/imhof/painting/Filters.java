package ch.epfl.imhof.painting;

import java.util.Arrays;
import java.util.Set;
import java.util.HashSet;
import java.util.function.Predicate;

import ch.epfl.imhof.Attributed;

/**
 * Filtres permettant de déterminer, étant donnée une entité attribuée,
 * si elle doit être gardée ou non.
 * 
 * Les filtres sont représentés au moyen de prédicats .
 * 
 * @author Thierry Treyer (235116)
 * @author Dominique Roduit (234868)
 *
 */
public final class Filters {
    
    private Filters() {}
    
    /**
     * Retourne un prédicat qui n'est vrai que si la valeur attribuée à laquelle
     * on applique le filtre possède l'attribut désigné par le paramètre attr, 
     * indépendemment de sa valeur.
     * 
     * @param attr Attribut dont on veut vérifier s'il est attribué à l'entité sur laquelle le filtre est appliqué
     * @return Prédicat (cf. description de méthode)
     */
    public static Predicate<Attributed<?>> tagged(String attr) {
        return (x) -> x.hasAttribute(attr);
    }
    
    /**
     * Retourne un prédicat qui n'est vrai que si la valeur attribuée
     * à laquelle on applique le filtre possède l'attribut désigné par attr
     * et si la valeur associée à cet attribut fait partie de celles spécifiées par val
     * 
     * @param attr Attribut dont on veut vérifier s'il est attribué à l'entité sur laquelle le filtre est appliqué
     * @param val Valeurs à tester si l'une est associée à l'attribut attr de l'entité.
     * @return Prédicat (cf. description de méthode)
     */
    public static Predicate<Attributed<?>> tagged(String attr, String... values) {
        Set<String> vals = new HashSet<>(Arrays.asList(values));
            
        return (x) -> x.hasAttribute(attr) && vals.contains(x.attributeValue(attr));
    }
    
    /**
     * Retourne un prédicat qui n'est vrai que lorsqu'on l'applique à une entitée
     * attribuée appartenant à la couche spécifiée par le paramètre layer
     * @param layer Couche à laquelle doit appartenir l'entité pour que le prédicat soit vrai.
     * @return Prédicat (cf. description de méthode)
     */
    public static Predicate<Attributed<?>> onLayer(int layer) {
        return (x) -> x.attributeValue("layer", 0) == layer;
    }
}
