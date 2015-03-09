package ch.epfl.imhof.osm;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;


public class OSMMapReaderTest {

    @Before
    public void init() {

    }

    @Test(expected=NullPointerException.class) 
    public void IOExceptionTest() {
        OSMMap m1;
        try {
            m1 = OSMMapReader.readOSMFile("foo.osm", false);
            m1.ways();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SAXException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
