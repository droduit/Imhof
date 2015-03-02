package ch.epfl.imhof.osm;

import ch.epfl.imhof.Attributes;
import java.util.Collections;

public final class OSMWay extends OSMEntity {

    /**
     * Construit un chemin étant donnés son identifiant unique,
     * ses nœuds et ses attributs. Lève l'exception IllegalArgumentException
     * si la liste de nœuds possède moins de deux éléments.
     * @param id
     * @param nodes
     * @param attributes
     */
    public OSMWay(long id, List<OSMNode> nodes, Attributes attributes) {
        if(nodes)
        super(id, attributes);
        
    }

}
