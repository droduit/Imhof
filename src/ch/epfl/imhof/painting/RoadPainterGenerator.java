package ch.epfl.imhof.painting;

import java.util.function.Predicate;

import ch.epfl.imhof.Attributed;

/**
 * 
 * @author Thierry Treyer (235116)
 * @author Dominique Roduit (234868)
 *
 */
public final class RoadPainterGenerator {
    
    private RoadPainterGenerator() { 
    }
    
    /**
     * Retourne un peintre pour le reseau routier spécifié par le filtre
     * @param specs Spécifications de route décrivant le dessin des types de routes données
     * @return Peintre pour le réseau routier correspondant au filtre
     */
    public static Painter painterForRoads(RoadSpec... specs) {
        // TODO - Je ne comprend RIEN ce que doit faire cette méthode desolé...
        Painter p = null;
        for(RoadSpec s : specs)
            p.above(Painter.line(.6f, Color.RED).when(s.filter));
        
        return p;
    }
    
    /**
     * Spécification de route qui décrit le dessin d'un type de route donné.
     * @author Thierry Treyer (235116)
     * @author Dominique Roduit (234868)
     *
     */
    public static final class RoadSpec {
        private final Predicate<Attributed<?>> filter;
        private final double wi;
        private final Color ci;
        private final double wc;
        private final Color cc;

        /**
         * Construit une spécification de route
         * @param filter Filtre permettant de sélectionner le type de route donné
         * @param wi Largeur du trait de l'interieur
         * @param ci Couleur du trait de l'intérieur
         * @param wc Largeur du trait de la bordure
         * @param cc Couleur du trait de la bordure
         */
        public RoadSpec(Predicate<Attributed<?>> filter, float wi, Color ci, float wc, Color cc) {
            this.filter = filter;
            this.wi = wi;
            this.ci = ci;
            this.wc = wc;
            this.cc = cc;
        }
    }
}
