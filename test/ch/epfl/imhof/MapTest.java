package ch.epfl.imhof;

import org.junit.Test;
import static ch.epfl.imhof.TestUtil.*;

public class MapTest {

    @Test(expected=UnsupportedOperationException.class)
    public void testImmuability1() {
        Map map = new Map(newAttributedPolyLineList(), newAttributedPolygoneList());
        map.polygons().add(null);
    }
    
    @Test(expected=UnsupportedOperationException.class)
    public void testImmuability2() {
        Map map = new Map(newAttributedPolyLineList(), newAttributedPolygoneList());
        map.polyLines().add(null);
    }
    
    @Test(expected=NullPointerException.class)
    public void testNPEConstructor() {
       new Map(null, null); 
    }

    @Test(expected=NullPointerException.class)
    public void testNPEAddPolygon() {
        Map.Builder builder = new Map.Builder();
        builder.addPolygon(null);
    }
    
    @Test(expected=NullPointerException.class)
    public void testNPEAddPolyLigne() {
        Map.Builder builder = new Map.Builder();
        builder.addPolyLine(null);
    }
}
