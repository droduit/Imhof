package ch.epfl.imhof.osm;

import static org.junit.Assert.*;
import static ch.epfl.imhof.TestUtil.*;

import java.util.List;

import org.junit.Test;

import ch.epfl.imhof.Attributes;

public class OSMWayTest {

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
    
    @Test
    public void OSMWay() {
      
    }

    @Test
    public void nodesCount() {
       
    }

    @Test
    public void nodes() {
        
    }

    @Test
    public void nonRepeatingNodes() {
        
    }

    @Test
    public void firstNode() {
        
    }

    @Test
    public void lastNode() {
        
    }

    @Test
    public void isClosed() {
        
    }


}
