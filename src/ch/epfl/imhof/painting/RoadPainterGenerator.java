package ch.epfl.imhof.painting;

import java.util.List;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.function.Predicate;

import ch.epfl.imhof.Attributed;
import static ch.epfl.imhof.painting.Filters.*;
import static ch.epfl.imhof.painting.LineStyle.LineCap;
import static ch.epfl.imhof.painting.LineStyle.LineJoin;

/**
 * Cette classe permet de générer un peintre pour le réseau routier (les routes, les ponts, les tunnels)
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
        List<List<Painter>> painters = new ArrayList<>(5);
        for (int i = 0; i < 5; i++)
            painters.add(new LinkedList<>());

        for (int i = 0; i < specs.length; i++) {
            RoadSpec spec = specs[i];

            painters.get(0).add(spec.innerBridgePainter());
            painters.get(1).add(spec.castingBridgePainter());

            painters.get(2).add(spec.innerRoadPainter());
            painters.get(3).add(spec.castingRoadPainter());

            painters.get(4).add(spec.tunnelPainter());
        }

        return painters
            .stream()
            .map(
                ls ->
                  ls.stream()
                    .reduce(Painter::above)
                    .get() )
            .reduce(Painter::above)
            .get();
    }

    /**
     * Spécification de route qui décrit le dessin d'un type de route donné.
     * @author Thierry Treyer (235116)
     * @author Dominique Roduit (234868)
     *
     */
    public static final class RoadSpec {
        private final Predicate<Attributed<?>> filter;

        private final float innerWidth;
        private final Color innerColor;

        private final float castingWidth;
        private final Color castingColor;

        /**
         * Construit une spécification de route
         * @param filter Filtre permettant de sélectionner le type de route donné
         * @param innerWidth Largeur du trait de l'interieur
         * @param innerColor Couleur du trait de l'intérieur
         * @param castingWidth Largeur du trait de la bordure
         * @param castingColor Couleur du trait de la bordure
         */
        public RoadSpec(Predicate<Attributed<?>> filter, float innerWidth, Color innerColor, float castingWidth, Color castingColor) {
            this.filter = filter;

            this.innerWidth = innerWidth;
            this.innerColor = innerColor;

            this.castingWidth = castingWidth;
            this.castingColor = castingColor;
        }

        private float outlineWidth () { return this.innerWidth + 2f * this.castingWidth; }
        private float tunnelWidth () { return this.innerWidth / 2f; }
        private float[] tunnelPattern () { return new float[]{ 2f * this.innerWidth, 2f * this.innerWidth }; }

        /**
         * @return Peintre pour l'intérieur des ponts 
         */
        public Painter innerBridgePainter () {
            return Painter.line(
                    new LineStyle(LineCap.Round, LineJoin.Round, this.innerColor, this.innerWidth, null))
                .when(this.filter.and(tagged("bridge")));
        }

        /**
         * @return Peintre pour la bordure des ponts
         */
        public Painter castingBridgePainter () {
            return Painter.line(
                    new LineStyle(LineCap.Butt, LineJoin.Round, this.castingColor, this.outlineWidth(), null))
                .when(this.filter.and(tagged("bridge")));
        }

        /**
         * @return Peintre pour l'intérieur des routes
         */
        public Painter innerRoadPainter () {
            return Painter.line(
                    new LineStyle(LineCap.Round, LineJoin.Round, this.innerColor, this.innerWidth, null))
                .when(this.filter.and(notTagged("bridge")).and(notTagged("tunnel")));
        }

        /**
         * @return Peintre pour le bord des routes
         */
        public Painter castingRoadPainter () {
            return Painter.line(
                    new LineStyle(LineCap.Round, LineJoin.Round, this.castingColor, this.outlineWidth(), null))
                .when(this.filter.and(notTagged("bridge")).and(notTagged("tunnel")));
        }

        /**
         * @return Peintre pour les tunnels
         */
        public Painter tunnelPainter () {
            return Painter.line(
                    new LineStyle(LineCap.Butt, LineJoin.Round, this.castingColor, this.tunnelWidth(), this.tunnelPattern()))
                .when(this.filter.and(tagged("tunnel")));
        }
    }
}
