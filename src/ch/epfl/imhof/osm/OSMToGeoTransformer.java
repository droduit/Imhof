package ch.epfl.imhof.osm;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.LinkedList;
import java.util.Set;
import java.util.HashSet;
import java.util.HashMap;

import ch.epfl.imhof.Attributed;
import ch.epfl.imhof.Attributes;
import ch.epfl.imhof.Map;
import ch.epfl.imhof.Graph;
import ch.epfl.imhof.geometry.ClosedPolyLine;
import ch.epfl.imhof.projection.*;
import ch.epfl.imhof.geometry.*;

/**
 * Représente un convertisseur de données OSM en carte
 * 
 * @author Thierry Treyer (235116)
 * @author Dominique Roduit (234868)
 *
 */
public final class OSMToGeoTransformer {
    private final Projection projection;

    private static final String TYPE_KEY = "type";
    private static final String TYPE_MULTIPOLYGON = "multipolygon";

    private static final String AREA_KEY = "area";
    private static final Set<String> AREA_VALUES = new HashSet<>(Arrays.asList("yes",
            "1", "true"));

    private static final Set<String> AREA_ATTRS = new HashSet<>(Arrays.asList(
            "aeroway", "amenity", "building", "harbour", "historic", "landuse",
            "leisure", "man_made", "military", "natural", "office", "place",
            "power", "public_transport", "shop", "sport", "tourism", "water",
            "waterway", "wetland"));

    private static final Set<String> FILTER_POLYLINE_ATTRS = new HashSet<>(
            Arrays.asList("bridge", "highway", "layer", "man_made", "railway",
                    "tunnel", "waterway"));

    private static final Set<String> FILTER_POLYGONE_ATTRS = new HashSet<>(
            Arrays.asList("building", "landuse", "layer", "leisure", "natural",
                    "waterway"));

    private Map.Builder mapBuilder;

    /**
     * Construit un convertisseur d'entités OSM en entités géométriques
     * utilisant la projection donnée.
     * 
     * @param projection
     *            Type de la projection à utiliser
     */
    public OSMToGeoTransformer(Projection projection) {
        this.projection = projection;
    }

    private boolean isMultipolygon(OSMRelation relation) {
        return relation.attributeValue(TYPE_KEY) != null
                && relation.attributeValue(TYPE_KEY).equals(TYPE_MULTIPOLYGON);
    }

    /**
     * Convertit une carte OSM en une carte géométrique projetée
     * 
     * @param map
     *            Carte OSM à convertur en carte géométrique projetée
     * @return Carte géométrique projetée
     */
    public Map transform(OSMMap map) {
        this.mapBuilder = new Map.Builder();

        for (OSMWay way : map.ways())
            this.buildWay(way);

        for (OSMRelation relation : map.relations()) {
            if (isMultipolygon(relation)) {
                List<Attributed<Polygon>> polygons = this.assemblePolygon(
                        relation, relation.attributes());

                for (Attributed<Polygon> polygon : polygons)
                    this.mapBuilder.addPolygon(polygon);
            }
        }

        return this.mapBuilder.build();
    }

    /**
     * Détermine si un chemin est de type area
     * 
     * @param way
     *            Le chemin dont on veut savoir s'il est de type area
     * @return true si le chemin est de type area
     */
    private boolean isArea(OSMWay way) {
        return way.isClosed()
                && (AREA_VALUES.contains(way.attributeValue(AREA_KEY)) || !way
                        .attributes().keepOnlyKeys(AREA_ATTRS).isEmpty());
    }

    /**
     * Construit le PolyLine ou Polygone associé au chemin donné et y attache
     * les attributs fitrés.
     * 
     * @param way
     *            Le chemin OSM à transformer en entité géométrique
     */
    private void buildWay(OSMWay way) {
        PolyLine.Builder builder = new PolyLine.Builder();

        for (OSMNode node : way.nonRepeatingNodes())
            builder.addPoint(this.projection.project(node.position()));

        if (this.isArea(way)) {
            Polygon polygon = new Polygon(builder.buildClosed());
            Attributes attr = way.attributes().keepOnlyKeys(
                    FILTER_POLYGONE_ATTRS);

            if (!attr.isEmpty())
                this.mapBuilder.addPolygon(new Attributed<Polygon>(polygon,
                        attr));
        } else {
            PolyLine poly = (way.isClosed()) ? builder.buildClosed() : builder
                    .buildOpen();
            Attributes attr = way.attributes().keepOnlyKeys(
                    FILTER_POLYLINE_ATTRS);

            if (!attr.isEmpty())
                this.mapBuilder
                        .addPolyLine(new Attributed<PolyLine>(poly, attr));
        }
    }

    /**
     * Construction du graphe pour les membres d'une relation ayant le rôle
     * donné.
     * 
     * @param relation
     *            Relation contenant les membres avec lesquels on veut
     *            construire le graphe
     * @param role
     *            Rôle des membres de la relation dont on veut construire le
     *            graphe
     * @return Le graphe pour les membres de la relation ayant le rôle donné
     */
    private Graph<OSMNode> buildGraphForRole(OSMRelation relation, String role) {
        Graph.Builder<OSMNode> graphBuilder = new Graph.Builder<OSMNode>();

        /* Construction du graph */
        for (OSMRelation.Member member : relation.members()) {
            if (member.type() != OSMRelation.Member.Type.WAY)
                continue;
            if (!member.role().equals(role))
                continue;

            OSMWay way = (OSMWay) member.member();
            List<OSMNode> nodes = way.nodes();

            for (int i = 1, l = nodes.size(); i < l; i++) {
                OSMNode n1 = nodes.get(i - 1);
                OSMNode n2 = nodes.get(i);

                graphBuilder.addNode(n1);
                graphBuilder.addNode(n2);

                graphBuilder.addEdge(n1, n2);
            }
        }

        Graph<OSMNode> graph = graphBuilder.build();

        /* Contrôle du nombre de voisins */
        for (OSMNode node : graph.nodes()) {
            if (graph.neighborsOf(node).size() != 2)
                return new Graph<>(new HashMap<>()); // Un graph vide
        }

        return graph;
    }

    /**
     * Sélectionne un noeud encore non visité parmi l'ensemble des noeuds et le
     * marque comme visité.
     * 
     * @param nodes
     *            Ensemble des noeuds
     * @param visited
     *            Noeuds déjà visités
     * @return Noeud encore non visité qui se trouve dans l'ensemble node privé
     *         de l'ensemble visited ou null si aucun noeud n'a pas déjà été
     *         visité.
     */
    private OSMNode pickUnvisitedNode(Set<OSMNode> nodes, Set<OSMNode> visited) {
        for (OSMNode node : nodes) {
            if (!visited.contains(node)) {
                visited.add(node);

                return node;
            }
        }

        return null;
    }

    /**
     * Calcule et retourne l'ensemble des anneaux de la relation donnée ayant le
     * rôle spécifié. Cette méthode retourne une liste vide si le calcul des
     * anneaux échoue.
     * 
     * @param relation
     *            Relation contenant les anneaux à récupérer
     * @param role
     *            Role des membres de la relation pour lesquels ont veut
     *            récupérer les anneaux
     * @return Liste des anneaux de la relation ayant le role spécifié ou liste
     *         vide si le calcul des anneaux échoue
     */
    private List<ClosedPolyLine> ringsForRole(OSMRelation relation, String role) {
        Graph<OSMNode> graph = buildGraphForRole(relation, role);

        Set<OSMNode> nodesSet = graph.nodes();
        Set<OSMNode> visitedNodes = new HashSet<>();

        List<ClosedPolyLine> rings = new LinkedList<>();

        OSMNode current = null;
        while ((current = this.pickUnvisitedNode(nodesSet, visitedNodes)) != null) {
            /* ^ On débute un anneau en sélectionnant un noeud non visité... */
            PolyLine.Builder builder = new PolyLine.Builder();

            do {
                builder.addPoint(this.projection.project(current.position()));
            } while ((current = this.pickUnvisitedNode(
                    graph.neighborsOf(current), visitedNodes)) != null);
            /* ^ On parcours successivement les noeuds de l'anneau en
             * sélectionnant un voisin non-visité du noeud courant
             */

            rings.add(builder.buildClosed());
        }

        return rings;
    }

    /**
     * Contrôle que la polyligne fermée inner soit contenu dans la polyligne
     * fermée outer
     * 
     * @param inner
     *            Polyligne dont on veut contrôler si elle est contenue dans
     *            outer
     * @param outer
     *            Polyligne dont on veut contrôler si elle contient inner
     * @return true si la polyligne inner est contenue dans la polyligne outer
     */
    private boolean isInside(ClosedPolyLine inner, ClosedPolyLine outer) {
        for (Point p : inner.points()) {
            if (!outer.containsPoint(p))
                return false;
        }

        return true;
    }

    /**
     * Calcule et retourne la liste des polygones attribués de la relation
     * donnée, en leur attachant les attributs donnés.
     * 
     * @param relation
     *            Relation pour laquelle on veut récupérer les polygones
     *            attribués
     * @param attributes
     *            Attributs à attacher aux polygones de la relation
     * @return Liste des polygones attribués de la relation
     */
    private List<Attributed<Polygon>> assemblePolygon(OSMRelation relation,
            Attributes attributes) {
        List<Attributed<Polygon>> polygons = new LinkedList<>();
        Attributes attr = attributes.keepOnlyKeys(FILTER_POLYGONE_ATTRS);

        /* Pas de travail si notre relation n'est pas un polygone valide */
        if (attr.isEmpty())
            return polygons;

        List<ClosedPolyLine> inners = this.ringsForRole(relation, "inner");
        List<ClosedPolyLine> outers = this.ringsForRole(relation, "outer");

        java.util.Map<ClosedPolyLine, List<ClosedPolyLine>> rawPolygons = new HashMap<>();
        for (ClosedPolyLine outer : outers)
            rawPolygons.put(outer, new LinkedList<>());

        Collections.sort(outers, (p1, p2) -> Double.compare(p1.area(), p2.area()) );

        for (ClosedPolyLine inner : inners) {
            ClosedPolyLine container = null;

            for (ClosedPolyLine outer : outers) {
                if (isInside(inner, outer)) {
                    container = outer;
                    break;
                }
            }

            if (container != null)
                rawPolygons.get(container).add(inner);
        }

        for (java.util.Map.Entry<ClosedPolyLine, List<ClosedPolyLine>> rawPoly : rawPolygons
                .entrySet()) {
            Polygon poly = new Polygon(rawPoly.getKey(), rawPoly.getValue());

            polygons.add(new Attributed<Polygon>(poly, attr));
        }

        return polygons;
    }
}
