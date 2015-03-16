package ch.epfl.imhof.osm;

import java.util.List;
import java.util.LinkedList;
import java.util.Set;
import java.util.HashSet;
import java.util.HashMap;

import ch.epfl.imhof.Attributed;
import ch.epfl.imhof.Attributes;
import ch.epfl.imhof.Map;
import ch.epfl.imhof.geometry.ClosedPolyLine;
import ch.epfl.imhof.projection.Projection;
import ch.epfl.imhof.geometry.*;

/**
 * Représente un convertisseur de données OSM en carte
 * @author Thierry Treyer (235116)
 * @author Dominique Roduit (234868)
 *
 */
public final class OSMToGeoTransformer {
	private final Projection projection;

	private final String AREA_KEY = "area";

	private final Set<String> AREA_VALUES = new HashSet<String>() {{
		add("yes"); add("1"); add("true");
	}};

	private final Set<String> AREA_ATTRS = new HashSet<String>() {{
		add("aeroway"); add("amenity"); add("building"); add("harbour");
		add("historic"); add("landuse"); add("leisure"); add("man_made");
		add("military"); add("natural"); add("office"); add("place");
		add("power"); add("public_transport"); add("shop"); add("sport");
		add("tourism"); add("water"); add("waterway"); add("wetland");
	}};

	private java.util.Map<Long, Attributed<Polygon>> polygons = new HashMap<Long, Attributed<Polygon>>();
	private java.util.Map<Long, Attributed<OpenPolyLine>> lines = new HashMap<Long, Attributed<OpenPolyLine>>();

    /**
     * Construit un convertisseur OSM en géométrie qui utilise la projection donnée
     * @param projection
     */
    public OSMToGeoTransformer (Projection projection) {
		this.projection = projection;
    }

    /**
     * Convertit une carte OSM en une carte géométrique projetée
     * @param map
     * @return
     */
    public Map transform (OSMMap map) {
		this.buildWays(map.ways());

		return null;
    }

	private boolean isArea (OSMWay way) {
		return AREA_VALUES.contains(way.attributeValue(AREA_KEY))
			|| !way.attributes().keepOnlyKeys(AREA_ATTRS).isEmpty();
	}

	private void buildWays (List<OSMWay> ways) {
		for (OSMWay way : ways) {
			PolyLine.Builder builder = new PolyLine.Builder();

			for (OSMNode node : way.nodes())
				builder.addPoint( this.projection.project( node.position() ) );

			if (this.isArea(way)) {
				Polygon polygon = new Polygon(builder.buildClosed());

				this.polygons.put( way.id(), new Attributed<Polygon>( polygon, way.attributes() ) );
			} else {
				OpenPolyLine line = builder.buildOpen();

				this.lines.put( way.id(), new Attributed<OpenPolyLine>( line, way.attributes() ) );
			}
		}
	}

    /**
     * Calcule et retourne l'ensemble des anneaux de la relation donnée ayant le rôle spécifié.
     * Cette méthode retourne une liste vide si le calcul des anneaux échoue.
     * @param relation
     * @param role
     * @return
     */
    private List<ClosedPolyLine> ringsForRole(OSMRelation relation, String role) {
        return null;
    }

    /**
     * Calcule et retourne la liste des polygones attribués de la relation donnée, en leur attachant les attributs donnés.
     * @param relation
     * @param attributes
     * @return
     */
    private List<Attributed<Polygon>> assemblePolygon(OSMRelation relation, Attributes attributes) {
        return null;
    }
}
