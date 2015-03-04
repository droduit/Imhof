package ch.epfl.imhof.osm;

import static org.junit.Assert.*;
import static ch.epfl.imhof.TestUtil.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import ch.epfl.imhof.Attributes;

public class OSMWayTest {

    private OSMNode firstNode, lastNode;
    private List<OSMNode> listNode = new ArrayList<>();
    private List<OSMNode> nodeList = new ArrayList<>();
    private OSMWay wayInit, wayInRepeatingMode;
    
    
    @Test(expected=IllegalArgumentException.class)
    public void illegalArgumentException() {
        List<OSMNode> list = newOSMNodeList();
        list.clear();
        OSMWay way = new OSMWay(newId(), list, newAttributes());
    }
    
    @Test(expected=IllegalStateException.class) 
    public void IllegalStateException() {
        OSMWay.Builder way = new OSMWay.Builder(newId());
        way.build();
    }
    
    @Test (expected=UnsupportedOperationException.class)
    public void immuability() {
        OSMWay way = newOSMWay();
        way.nodes().add(newOSMNode());
    }

    // Tests du Builder
    
    @Test
    public void testConstructor () {
        OSMWay way = newOSMWay();
        assertNotNull(way);
    }

    @Test (expected = NullPointerException.class)
    public void testConstructorPositionException () {
        new OSMWay(newId(), null, newAttributes());
    }

    @Test (expected = NullPointerException.class)
    public void testConstructorAttributesException () {
        new OSMWay(newLongId(), newOSMNodeList(), null);
    }
   
    // Tests de OSMWay
    @Before
    public void init() {
        firstNode = newOSMNode();
        lastNode = newOSMNode();
        
        
        listNode.add(firstNode);
        listNode.add(newOSMNode());
        listNode.add(lastNode);
        
        wayInit = new OSMWay(newLongId(), listNode, newAttributes());
        
        
        nodeList = new ArrayList<>(listNode);
        nodeList.add(firstNode);
        wayInRepeatingMode = new OSMWay(newId(), nodeList, newAttributes());
    }
    
    @Test
    public void nodesCount() {
       assertEquals(3, wayInit.nodesCount());
    }

    @Test
    public void nodes() {
        assertNotNull(wayInit.nodes());
        assertEquals("bar", wayInit.nodes().get(0).attributes().get("foo"));
        assertNull(wayInit.nodes().get(1).attributeValue("lalalere"));
    }

    @Test
    public void nonRepeatingNodes() {
        List<OSMNode> expectedList = new ArrayList<>(nodeList);
        expectedList.remove(expectedList.size()-1);
        
        List<OSMNode> repeatingList = wayInRepeatingMode.nonRepeatingNodes();
        for(int i=0; i< repeatingList.size(); i++) 
            assertNodeEquals(expectedList.get(i), repeatingList.get(i));
    }

    @Test
    public void firstNode() {
        assertNodeEquals(firstNode, wayInit.nodes().get(0));
    }

    @Test
    public void lastNode() {
        assertNodeEquals(lastNode, wayInit.nodes().get(wayInit.nodesCount()-1));
    }

    @Test
    public void isClosed() {
        assertTrue(wayInRepeatingMode.isClosed());
    }


}
