package ch.epfl.imhof.osm;

import java.io.*;
import java.util.zip.GZIPInputStream;
import java.util.Deque;
import java.util.LinkedList;
import org.xml.sax.*;
import org.xml.sax.helpers.*;

import ch.epfl.imhof.*;

/**
 * Permet de construire une carte OpenStreetMap à partir de données stockées
 * dans un fichier au format OSM
 * 
 * @author Thierry Treyer (235116)
 * @author Dominique Roduit (234868)
 *
 */
public final class OSMMapReader {
	public static final class OSMMapReaderHandler extends DefaultHandler {
		public static enum Type {
			NODE, WAY, ND, RELATION, MEMBER, TAG, UNKNOWN
		}

		OSMMap.Builder mapBuilder = new OSMMap.Builder();

		Deque<Type> entitiesType = new LinkedList<Type>();
		Deque<OSMEntity.Builder> entities = new LinkedList<OSMEntity.Builder>();

		public void startElement (String uri, String lName, String qName, org.xml.sax.Attributes attr) throws SAXException {
			System.out.println("Starting element: " + lName);

			switch (lName) {
				case "node":
					this.addNode(uri, lName, qName, attr);
					break;
				case "way":
					this.addWay(uri, lName, qName, attr);
					break;
				case "nd":
					this.addNodeRef(uri, lName, qName, attr);
					break;
				case "tag":
					this.addTag(uri, lName, qName, attr);
					break;
				default:
					System.out.println("Unknown element: " + lName);
					this.entitiesType.addLast(Type.UNKNOWN);
					this.entities.addLast(null);
					break;
			}
		}

		public void endElement (String uri, String lName, String qName) throws SAXException {
			System.out.println("Ending element: " + lName);

			Type type = this.entitiesType.removeLast();
			OSMEntity.Builder builder = this.entities.removeLast();

			if (builder == null || builder.isIncomplete())
				return;

			switch (type) {
				case NODE:
					this.mapBuilder.addNode( ( (OSMNode.Builder)builder ).build() );
					break;
				case WAY:
					this.mapBuilder.addWay( ( (OSMWay.Builder)builder ).build() );
					break;
				case ND:
				case TAG:
				default:
					break;
			}
		}

		private void addNode (String uri, String lName, String qName, org.xml.sax.Attributes attr) {
			long id = Long.parseLong(attr.getValue("id"));
			double lon = Math.toRadians(Double.parseDouble(attr.getValue("lon")));
			double lat = Math.toRadians(Double.parseDouble(attr.getValue("lat")));

			OSMNode.Builder nb = new OSMNode.Builder(id, new PointGeo(lon, lat));

			this.entitiesType.addLast(Type.NODE);
			this.entities.addLast(nb);
		}

		private void addWay (String uri, String lName, String qName, org.xml.sax.Attributes attr) {
			long id = Long.parseLong(attr.getValue("id"));

			OSMWay.Builder wb = new OSMWay.Builder(id);

			this.entitiesType.addLast(Type.WAY);
			this.entities.addLast(wb);
		}

		private void addNodeRef (String uri, String lName, String qName, org.xml.sax.Attributes attr) {
			long ref = Long.parseLong(attr.getValue("ref"));

			OSMNode node = this.mapBuilder.nodeForId(ref);
			OSMWay.Builder builder = (OSMWay.Builder)this.entities.getLast();

			if (node == null)
				builder.setIncomplete();
			else
				builder.addNode(node);

			this.entitiesType.addLast(Type.ND);
			this.entities.addLast(null);
		}

		private void addTag (String uri, String lName, String qName, org.xml.sax.Attributes attr) {
			String key = attr.getValue("k");
			String value = attr.getValue("v");

			this.entities.getLast().setAttribute(key, value);

			this.entitiesType.addLast(Type.TAG);
			this.entities.addLast(null);
		}
	}

    /**
     * Constructeur vide non-instanciable
     */
    private OSMMapReader() {}

    /**
     * Lit la carte OSM contenue dans le fichier de nom donné, en le décompressant avec gzip ssi le second argument est vrai.
     * Lève l'exception SAXException en cas d'erreur dans le format du fichier XML contenant la carte,
     * ou l'exception IOException en cas d'autre erreur d'entrée/sortie, p.ex. si le fichier n'existe pas.
     * @param fileName
     * @param unGZip
     * @return
     */
    public static OSMMap readOSMFile (String fileName, boolean unGZip) throws IOException, SAXException {
		String filePath = OSMMapReader.class.getResource(fileName).getFile();

		try (InputStream file = new FileInputStream(filePath)) {
			InputStream input = (unGZip == false) ? file : new GZIPInputStream(file);

			XMLReader reader = XMLReaderFactory.createXMLReader();
			reader.setContentHandler(new OSMMapReaderHandler());
			reader.parse(new InputSource(input));
		}

		return null;
    }

	public static void main (String args[]) throws Exception {
		System.out.println("Begin parsing...");
		OSMMapReader.readOSMFile("/bc.osm", false);
		System.out.println("End parsing!");
	}
}
