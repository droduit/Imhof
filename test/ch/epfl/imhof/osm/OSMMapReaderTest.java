package ch.epfl.imhof.osm;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;


public class OSMMapReaderTest {

    @Before
    public void init() {

    }

    // test IOException
    @Test
    public void IOExceptionTest() {
        OSMMap m1;
        try {
            m1 = OSMMapReader.readOSMFile("foo.osm", false);
       
        } catch (IOException e) {

        } catch (SAXException e) {
        }
    }
    
    // test FileNotFoundException
    
    // test MalformedByteSequenceException lorsqu'un fichier compressé n'est pas décompressé avant le parsing
    
    // test ZipException lorsqu'on tente de décompresser un xml qui n'est pas compressé
    
    // test SAXException lorsqu'on a un fichier XML non valide
    
    // test IllegalStateException
    
}
