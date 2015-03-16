package ch.epfl.imhof.osm;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.zip.ZipException;

import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

import com.sun.org.apache.xerces.internal.impl.io.MalformedByteSequenceException;

public class OSMMapReaderTest {

    // test FileNotFoundException lorsqu'on veut atteindre un fichier qui n'existe pas
    @Test(expected=FileNotFoundException.class)
    public void FNFE() throws IOException, SAXException {
        OSMMap map = OSMMapReader.readOSMFile("foo.osm", false);
    }
    
    // test MalformedByteSequenceException lorsqu'un fichier compressé n'est pas décompressé avant le parsing
    @Test(expected=MalformedByteSequenceException.class)
    public void MBSE() throws IOException, SAXException {
        OSMMap map = OSMMapReader.readOSMFile("/lausanne.osm.gz", false);
        
    }
    
    // test ZipException lorsqu'on tente de décompresser un xml qui n'est pas compressé
    @Test(expected=ZipException.class)
    public void ZE() throws IOException, SAXException {
        OSMMap map = OSMMapReader.readOSMFile("/lc.osm", true);
    }
    
    // test SAXException lorsqu'on a un fichier XML non valide
    @Test(expected=SAXException.class)
    public void SAXE() throws IOException, SAXException {
        OSMMap map = OSMMapReader.readOSMFile("/nonvalidxml.osm", false);
    }
    
    // test IllegalStateException
    @Test(expected=IllegalStateException.class)
    public void IllegalStateExTest() throws IOException, SAXException {
        OSMMap map = OSMMapReader.readOSMFile("/illegalStateException.osm", false);
    }
    
    @Test
    public void testExistenceAndQuantity() throws IOException, SAXException {
        OSMMap map = OSMMapReader.readOSMFile("/test1.osm", false);
        
        // On doit avoir 2 chemins seulement car le 3e ne possède qu'une reference
        // vers un noeud qui n'existe pas donc on ne le construit pas.
       assertEquals(2, map.ways().size());
       
       // On test que le chemin dont l'id est 111626833 contient bien 2 noeuds
       OSMWay way = null;
       if(map.ways().get(0).id()==111626833) {
           way = map.ways().get(0);
       } else {
           way = map.ways().get(1);
       }
       assertEquals(2, way.nodesCount());
       
       // On controle qu'on ait tous les attributs pour le chemin 111626833
       assertEquals(2, way.attributes().size());
       assertEquals("1", way.attributeValue("layer"));
       assertEquals("Plan EPFL", way.attributeValue("source"));
       
       
       // Controles sur la relation
       assertEquals(1, map.relations().size());
       assertEquals(2, map.relations().get(0).members().size());
       for(OSMRelation.Member m : map.relations().get(0).members()) {
           assertEquals(OSMRelation.Member.Type.WAY.toString(), m.type().toString());
           assertEquals("outer", m.role());
       }

       OSMRelation rel = map.relations().get(0);
       assertEquals("yes", rel.attributeValue("building"));
       assertEquals("0", rel.attributeValue("layer"));
       assertEquals("Rolex Learning Center", rel.attributeValue("name"));
       assertEquals("RLC", rel.attributeValue("ref"));
       assertEquals("multipolygon", rel.attributeValue("type"));
    }
}
