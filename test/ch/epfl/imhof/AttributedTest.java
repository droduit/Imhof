package ch.epfl.imhof;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import ch.epfl.imhof.geometry.ClosedPolyLine;
import ch.epfl.imhof.geometry.Point;
import ch.epfl.imhof.geometry.Polygon;

/**
 * Tests Pour la class Attributed
 * @author Dominique Roduit (234868)
 *
 */
public class AttributedTest {

    private Attributed<Polygon> ap;
    
    public void init() {
        Attributes.Builder b = new Attributes.Builder();
        b.put("natural", "water");
        b.put("name", "Lac Léman");
        b.put("ele", "372");
        Attributes a = b.build();

        List<Point> points = Arrays.asList(new Point(0,50), new Point(90,80), new Point(-5,-10));
        
        Polygon p = new Polygon(new ClosedPolyLine(points));
        ap = new Attributed<>(p, a);

    }
    
    @Test
    public void testRightValues() {
        init();
        assertEquals("Doit retourner 372", ap.attributeValue("ele", 0), 372);
        attributeValueTest();
        
        // test hasAttributes
        assertTrue(ap.hasAttribute("ele"));
        assertFalse(ap.hasAttribute("test"));
        assertFalse(ap.hasAttribute(null));
        
       
        // ----------------------------------------------------
        // On attribue mnt rien a un objet null mais on veut tout de meme que les erreurs soient evitées.
        ap = new Attributed<>(null, null);
        attributeValueTest();
        assertNotNull("Ne doit pas etre null", ap.attributes());
        
        // test hasAttributes
        assertFalse(ap.hasAttribute("ele"));
        
    }
    
    // Test de la methode attribute value
    public void attributeValueTest() {
        assertEquals("Doit retourner 0", ap.attributeValue("ele0", 0), 0);
        assertNull("Doit retourner null", ap.attributeValue("ele0"));
        assertEquals("Doit retourner non", ap.attributeValue("ele0", "non"), "non");
        // Normal que ca retourne pas la valeur attendue pour l'instant mais quand Thierry aura fini la class Attributes ca devra être bon !
        assertEquals("Doit retourner 1", ap.attributeValue("name", 1), 1);
        
    }
    
    
}
