package ch.epfl.imhof.osm;

import java.io.*;
import java.util.zip.GZIPInputStream;
import java.util.Deque;
import java.util.LinkedList;

import org.xml.sax.*;
import org.xml.sax.helpers.*;

import ch.epfl.imhof.*;
import ch.epfl.imhof.osm.OSMRelation.Member;

/**
 * Permet de construire une carte OpenStreetMap à partir de données stockées
 * dans un fichier au format OSM
 * 
 * @author Thierry Treyer (235116)
 * @author Dominique Roduit (234868)
 *
 */
public final class OSMMapReader {
    private static OSMMap.Builder mapBuilder = new OSMMap.Builder();

    /**
     * Gestionnaire de contenu pour interpéter le contenu du fichier XML
     * et instancier les différentes entités.
     * 
     * @author Thierry Treyer (235116)
     * @author Dominique Roduit (234868)
     *
     */
	public static final class OSMMapReaderHandler extends DefaultHandler {
		/** Différents types possibles d'entités **/
	    public static enum Type {
			NODE, WAY, ND, RELATION, MEMBER, TAG, UNKNOWN
		}

		private class Entity {
			private Type type;
			private OSMEntity.Builder builder;

			public Type type () { return this.type; }
			public OSMEntity.Builder builder () { return this.builder; }

			public Entity (Type type, OSMEntity.Builder builder) {
				this.type = type;
				this.builder = builder;
			}
		}

		//OSMMap.Builder mapBuilder = new OSMMap.Builder();
	    
		Deque<Entity> entities = new LinkedList<Entity>();

		/**
		 * Lorsqu'une balise ouvrante est rencontrée
		 */
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
				case "relation":
				    this.addRelation(uri, lName, qName, attr);
				    break;
				case "member":
				    this.addRelationMember(uri, lName, qName, attr);
				    break;
				default:
					System.out.println("Unknown element: " + lName);
					this.entities.addLast( new Entity(Type.UNKNOWN, null) );
					break;
			}
		}

		/**
		 * Lorsqu'une balise fermante est rencontrée
		 */
		public void endElement (String uri, String lName, String qName) throws SAXException {
			System.out.println("Ending element: " + lName);

			Entity entity = this.entities.removeLast();

			Type type = entity.type();
			OSMEntity.Builder builder = entity.builder();

			if (builder == null || builder.isIncomplete())
				return;

			switch (type) {
				case NODE:
				    mapBuilder.addNode( ( (OSMNode.Builder)builder ).build() );
					break;
				case WAY:
					mapBuilder.addWay( ( (OSMWay.Builder)builder ).build() );
					break;
				case RELATION:
				    mapBuilder.addRelation( ((OSMRelation.Builder)builder).build() );
				case ND:
				case TAG:
				default:
					break;
			}
		}

		/**
		 * Ajout d'un noeud dans le deque
		 * @param uri 
		 * @param lName
		 * @param qName
		 * @param attr Attributs attachés à l'élément
		 */
		private void addNode (String uri, String lName, String qName, org.xml.sax.Attributes attr) {
			long id = Long.parseLong(attr.getValue("id"));
			double lon = Math.toRadians(Double.parseDouble(attr.getValue("lon")));
			double lat = Math.toRadians(Double.parseDouble(attr.getValue("lat")));

			OSMNode.Builder nb = new OSMNode.Builder(id, new PointGeo(lon, lat));

			this.entities.addLast( new Entity(Type.NODE, nb) );
		}

		/**
		 * Ajout d'un chemin dans le deque
		 * @param uri
		 * @param lName
		 * @param qName
		 * @param attr Attributs attachés à l'élément
		 */
		private void addWay (String uri, String lName, String qName, org.xml.sax.Attributes attr) {
			long id = Long.parseLong(attr.getValue("id"));

			OSMWay.Builder wb = new OSMWay.Builder(id);

			this.entities.addLast( new Entity(Type.WAY, wb) );
		}

		/**
		 * Ajout d'un noeud au bâtisseur du dernier chemin dans le deque
		 * @param uri
		 * @param lName
		 * @param qName
		 * @param attr Attributs attachés à l'élément
		 */
		private void addNodeRef (String uri, String lName, String qName, org.xml.sax.Attributes attr) {
			long ref = Long.parseLong(attr.getValue("ref"));

			OSMNode node = mapBuilder.nodeForId(ref);
			Entity parent = this.entities.getLast();

			if (parent.type() != Type.WAY)
				throw new IllegalStateException("Parent must be of type WAY, not " + parent.type());

			OSMWay.Builder builder = (OSMWay.Builder)parent.builder();

			if (node == null)
				builder.setIncomplete();
			else
				builder.addNode(node);

			this.entities.addLast( new Entity(Type.ND, null) );
		}

		/**
		 * Ajout d'un attribut à la dernière entité dans le deque
		 * @param uri
		 * @param lName
		 * @param qName
		 * @param attr Attributs attachés à l'élément
		 */
		private void addTag (String uri, String lName, String qName, org.xml.sax.Attributes attr) {
			String key = attr.getValue("k");
			String value = attr.getValue("v");

			this.entities.getLast().builder().setAttribute(key, value);

			this.entities.addLast(new Entity(Type.TAG, null) );
		}
		
		/**
		 * Ajout d'une relation dans le deque
		 * @param uri
		 * @param lName
		 * @param qName
		 * @param attr Attributs attachés à l'élément
		 */
		private void addRelation(String uri, String lName, String qName, org.xml.sax.Attributes attr) {
		    long id = Long.parseLong(attr.getValue("id"));
		    
		    OSMRelation.Builder rel = new OSMRelation.Builder(id);
		  
			this.entities.addLast( new Entity(Type.RELATION, rel) );
		}
		
		/**
		 * Ajout d'un membre à la dernière relation dans le deque
		 * @param uri
		 * @param lName
		 * @param qName
		 * @param attr Attributs attachés à l'élément
		 */
		private void addRelationMember(String uri, String lName, String qName, org.xml.sax.Attributes attr) {
		    long ref = Long.parseLong(attr.getValue("ref"));
		    OSMRelation.Member.Type type = OSMRelation.Member.Type.valueOf(attr.getValue("type").toUpperCase());
		    String role = attr.getValue("role");
		    
		    OSMEntity member = null;
		    switch(type) {
    		    case NODE:
    		        member = mapBuilder.nodeForId(ref);
    		        break;
    		    case WAY :
    		        member = mapBuilder.wayForId(ref);
    		        break;
    		    case RELATION:
    		        member = mapBuilder.relationForId(ref);
    		        break;
    		    default:
    		        break;
		    }
		    
			Entity parent = this.entities.getLast();

			if (parent.type() != Type.RELATION)
				throw new IllegalStateException("Parent must be of type RELATION, not " + parent.type());

		    OSMRelation.Builder builder = (OSMRelation.Builder)this.entities.getLast().builder();
		    
		    if (member == null)
		        builder.setIncomplete();
		    else
		        builder.addMember(type, role, member);
		    
			this.entities.addLast( new Entity(Type.MEMBER, null) );
		}
	}

    /**
     * Constructeur vide non-instanciable
     */
    private OSMMapReader() {}

    /**
     * Lit la carte OSM contenue dans le fichier de nom donné,
     * en le décompressant avec gzip ssi le second argument est vrai.
     * 
     * @param fileName Nom du fichier OSM à parser
     * @param unGZip true si le fichier est compressé
     * @return Carte OpenStreetMap à partir de données stockées dans un fichier au format OSM.
     * @throws IOException En cas d'erreur d'entrée/sortie, par exemple si le fichier n'existe pas
     * @throws SAXException En cas d'erreur dans le format du fichier XML contenant la carte
     */
    public static OSMMap readOSMFile (String fileName, boolean unGZip) throws IOException, SAXException {
		String filePath = OSMMapReader.class.getResource(fileName).getFile();

		try (InputStream file = new FileInputStream(filePath)) {
			InputStream input = (unGZip == false) ? file : new GZIPInputStream(file);

			XMLReader reader = XMLReaderFactory.createXMLReader();
			reader.setContentHandler(new OSMMapReaderHandler());
			reader.parse(new InputSource(input));
		}

		return mapBuilder.build();
    }

	public static void main (String args[]) throws Exception {
		System.out.println("Begin parsing...");
		OSMMap map = OSMMapReader.readOSMFile("/lc.osm", false);
		System.out.println("End parsing!");
		
		
		/*
		// Chemins
		for(OSMWay w : map.ways()) {
		    System.out.println("---- Way : "+w.id());
		    
		    System.out.println("Nodes : ");
		    for(OSMNode n : w.nodes()) {
		        System.out.println(n.id()+" : lat: "+n.position().latitude()+" , long :"+n.position().longitude());
		    }
		    System.out.println(w.attributeValue("building"));
		}
		*/
		
		// Relations
		for(OSMRelation r : map.relations()) {    
		    System.out.println("----- Relation : "+r.id());
		    
		    
		    System.out.println("Membres de la relation ("+r.members().size()+") : ----");
		    for(Member m : r.members()) {
		        System.out.println(m.member().id()+" - "+m.type()+" - "+m.role());
		    }
		    
		    System.out.println(r.attributeValue("building"));
	          System.out.println(r.attributeValue("layer"));
	          System.out.println(r.attributeValue("name"));
	          System.out.println(r.attributeValue("ref"));
	          System.out.println(r.attributeValue("type"));
	          
		}
		
	}
}