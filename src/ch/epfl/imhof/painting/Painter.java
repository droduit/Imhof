package ch.epfl.imhof.painting;

import java.util.function.Predicate;
import java.util.stream.IntStream;

import ch.epfl.imhof.Attributed;
import ch.epfl.imhof.Map;
import ch.epfl.imhof.geometry.ClosedPolyLine;
import ch.epfl.imhof.geometry.PolyLine;
import ch.epfl.imhof.geometry.Polygon;
import ch.epfl.imhof.painting.LineStyle.LineCap;
import ch.epfl.imhof.painting.LineStyle.LineJoin;


/**
 * Représente un peintre de base
 * @author Thierry Treyer (235116)
 * @author Dominique Roduit (234868)
 *
 */
public interface Painter {
    /**
     * Dessine la carte sur la toile
     * @param map Carte
     * @param canvas Toile
     */
    public void drawMap(Map map, Canvas canvas);
    
    /**
     * Retourne un peintre dessinant l'intérieur de tous les polygones de la carte qu'il reçoit avec la couleur spécifiée
     * @param color Couleur de remplissage des polygones
     * @return Peintre de base (cf. description de la méthode)
     */
    public static Painter polygon(Color color) {
        return (map, canvas) -> {
            for(Attributed<Polygon> p : map.polygons())
                canvas.drawPolygon(p.value(), color);
        };
    }
    
    /**
     * Retourne un peintre dessinant toutes les lignes de la carte qu'on lui fournis avec le style correspondant
     * @param lc Type de terminaison 
     * @param lj Type de jointure
     * @param color Couleur des traits
     * @param width Largeur des traits
     * @param dashed Séquence d'alternance pour le dessin en traitillés des segments
     * @return Peintre de base (cf. description de la méthode)
     */
    public static Painter line(LineCap lc, LineJoin lj, Color color, float width, float[] dashed) {
        return (map, canvas) -> {
            for(Attributed<PolyLine> p : map.polyLines()) 
                canvas.drawPolyline(p.value(), new LineStyle(lc, lj, color, width, dashed));  
        };
    }
    
    /**
     * Retourne un peintre dessinant toutes les lignes de la carte qu'on lui fournis avec le style correpondant
     * @param width Largeur des traits
     * @param color Couleur des traits
     * @return Peintre de base (cf. description de la méthode)
     */
    public static Painter line(float width, Color color) {
        LineStyle style = new LineStyle(width, color);
        return (map, canvas) -> {
          for(Attributed<PolyLine> p : map.polyLines()) 
              canvas.drawPolyline(p.value(), style);
        };
    }
    
    /**
     * Retourne un peintre dessinant les pourtours de l'enveloppe
     * et des trous de tous les polygons de la carte avec le style donné
     * @param style Style de traits
     * @return Peintre de base (cf. description de la méthode)
     */
    public static Painter outline(LineStyle style) {
        return (map, canvas) -> {
            for(Attributed<Polygon> p : map.polygons()) {
                for(ClosedPolyLine h : p.value().holes())
                    canvas.drawPolyline(h, style);
                
                canvas.drawPolyline(p.value().shell(), style);
            }
        };
    }
    
    /**
     * Retourne un peintre dessinant les pourtours de l'enveloppe
     * et des trous de tous les polygones de la carte qu'on lui fournit
     * @param lc Type de terminaison 
     * @param lj Type de jointure
     * @param color Couleur des traits
     * @param width Largeur des traits
     * @param dashed Séquence d'alternance pour le dessin en traitillés des segments
     * @return Peintre de base (cf. description de la méthode)
     */
    public static Painter outline(LineCap lc, LineJoin lj, Color color, float width, float[] dashed) {
        return outline(new LineStyle(lc, lj, color, width, dashed));
    }
    
    /**
     * Retourne un peintre dessinant les pourtours de l'enveloppe
     * et des trous de tous les polygones de la carte qu'on lui fournit
     * @param width Largeur des traits
     * @param color Couleur des traits
     * @return Peintre de base (cf. description de la méthode)
     */
    public static Painter outline(float width, Color color) {
        return outline(new LineStyle(width, color));
    }
    
    /**
     * Retourne un peintre se comportant comme celui auquel on l'applique, 
     * si ce n'est qu'il ne considère que les éléments de la carte satisfaisant le prédicat.
     * @param p Prédicat dont les éléments qui sont satisfaits sont ajouté à la carte
     * @return Peintre de base (cf. description de la méthode)
     */
    public default Painter when(Predicate<Attributed<?>> p) {
        return (map, canvas) -> {
            Map.Builder mb = new Map.Builder();

            map.polyLines().stream().filter(p).forEach(mb::addPolyLine);
            map.polygons().stream().filter(p).forEach(mb::addPolygon);

            this.drawMap(mb.build(), canvas);
        };
    }
    
    /**
     * Retourne un peintre dessinant d'abord la carte produite par le 2e peintre pris en argument
     * puis, par dessus, la carte produite par le premier peintre.
     * @param p 2e peintre
     * @return Peintre de base (cf. description de la méthode)
     */
    public default Painter above(Painter p) {
        return (map, canvas) -> {
            p.drawMap(map, canvas);
            this.drawMap(map, canvas);
        };
    }
    
    /**
     * Retourne un peintre utilisant l'attribut layer attaché aux entités 
     * de la carte pour la dessiner par couches, càd en dessinant d'abord toutes
     * les entités de la couche -5, -4 et ainsi de suite jusqu'à la couche +5
     * @return Peintre de base (cf. description de la méthode)
     */
    public default Painter layered() {
        return (map, canvas) -> {
            IntStream.iterate(5, i -> i - 1)
                .limit(11)
                .mapToObj( layer -> this.when(Filters.onLayer(layer)) )
                .reduce( (la, lb) -> la.above(lb) )
                .ifPresent( painter -> painter.drawMap(map, canvas) );
        };
    }
    
}
