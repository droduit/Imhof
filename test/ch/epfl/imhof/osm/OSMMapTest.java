package ch.epfl.imhof.osm;

import static org.junit.Assert.*;
import static ch.epfl.imhof.TestUtil.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class OSMMapTest {
    OSMMap.Builder builder;
    List<Long> idsNodes = new ArrayList<>(), idsWays = new ArrayList<>(), idsRelations = new ArrayList<>();
    List<OSMNode> nodeList= new ArrayList<>();
    List<OSMWay> wayList= new ArrayList<>();
    List<OSMRelation> relationList= new ArrayList<>();
    
    @Before
    public void init() {
        builder = new OSMMap.Builder();
        
        for(int i = 0; i<5; i++) {
            OSMNode node = newOSMNode();
            builder.addNode(node);
            idsNodes.add(node.id());
            nodeList.add(builder.nodeForId(idsNodes.get(i)));
        }
        
        for(int i=0; i<4; i++) {
            OSMWay way = new OSMWay(newLongId(), nodeList, newAttributes());
            wayList.add(way);
            idsWays.add(way.id());
            builder.addWay(wayList.get(i));
        }
        
        for(int i=0; i<5; i++) {
            OSMRelation relation = new OSMRelation(newLongId(), newMemberList(), newAttributes());
            relationList.add(relation);
            idsRelations.add(relation.id());
            builder.addRelation(relation);
        }
    }
    
    @Test(expected=NullPointerException.class)
    public void testAddWayNullPointerException() {
        builder.addWay(null);
    }
    
    @Test(expected=NullPointerException.class)
    public void testAddNodeNullPointerException() {
        builder.addNode(null);
    }
    
    @Test(expected=NullPointerException.class)
    public void testAddRelationNullPointerException() {
        builder.addRelation(null);
    }
    
    @Test
    public void testFailBuild() {
        OSMMap.Builder build = new OSMMap.Builder();
        assertNotNull(build.build());
    }
    
    @Test
    public void testRightGetters() {
        assertNodeEquals(nodeList.get(0), builder.nodeForId(idsNodes.get(0)));
        assertWayEquals(wayList.get(0), builder.wayForId(idsWays.get(0))); 
        assertRelationEquals(relationList.get(0), builder.relationForId(idsRelations.get(0)));
        
        OSMMap.Builder build = new OSMMap.Builder();
        assertNull(build.relationForId(123));
        assertNull(build.nodeForId(1));
        assertNull(build.wayForId(8));
        
        build.addNode(null);
        build.addRelation(null);
        build.addWay(null);
    }
    
    @Test(expected=UnsupportedOperationException.class)
    public void testImmuabilityWays() {
       OSMMap map = builder.build();
       map.ways().set(0, newOSMWay());
    }
    
    @Test(expected=UnsupportedOperationException.class)
    public void testImmuabilityRelations() {
       OSMMap map = builder.build();
       map.relations().set(0, newSimpleOSMRelation());
    }
    
    @Test(expected=NullPointerException.class)
    public void testNullPointerExceptionConstructor() {
        OSMMap map = new OSMMap(null, null);
        map.relations();
    }
    
    @Test
    public void testWays() {
        OSMMap map = builder.build();
        for(int i=0; i<map.ways().size(); i++)
            assertWayEquals(wayList.get(i), map.ways().get(i));
    }

}
