package ch.epfl.imhof;

import static org.junit.Assert.*;

import org.junit.Test;

public class MapTest {

    @Test(expected=UnsupportedOperationException.class)
    public void testImmuability() {
        
    }
    
    @Test(expected=NullPointerException.class)
    public void testNPEConstructor() {
        
    }

    @Test(expected=NullPointerException.class)
    public void testNPEAddPolygon() {
        
    }
    
    @Test(expected=NullPointerException.class)
    public void testNPEAddPolyLigne() {
        
    }
}
