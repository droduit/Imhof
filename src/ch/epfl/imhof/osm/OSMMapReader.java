package ch.epfl.imhof.osm;

import java.io.*;
import java.util.zip.GZIPInputStream;
import java.util.Deque;
import java.util.LinkedList;
import java.net.URL;

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
    private static OSMMap.Builder mapBuilder;

    /**
     * Gestionnaire de contenu pour interpéter le contenu du fichier XML
     * et instancier les différentes entités.
     * 
     * @author Thierry Treyer (235116)
     * @author Dominique Roduit (234868)
     */
	public static final class OSMMapReaderHandler extends DefaultHandler {
		/** Différents types possibles d'entités **/
	    public static enum Type {
			NODE, WAY, ND, RELATION, MEMBER, TAG, UNKNOWN
		}

		/** Une entité du document XML */
		private class Entity {
			private final Type type;
			private final OSMEntity.Builder builder;

			public Type type () { return this.type; }
			public OSMEntity.Builder builder () { return this.builder; }

			public Entity (Type type, OSMEntity.Builder builder) {
				this.type = type;
				this.builder = builder;
			}
		}

		/** Le stack des entités parcourus lors du parse */
		Deque<Entity> entities = new LinkedList<Entity>();

		/**
		 * Callback lorsqu'une balise ouvrante est rencontrée
		 */
		public void startElement (String uri, String lName, String qName, org.xml.sax.Attributes attr) throws SAXException {
			switch (lName) {
				case "node":
					this.addNode(attr);
					break;
				case "way":
					this.addWay(attr);
					break;
				case "nd":
					this.addNodeRef(attr);
					break;
				case "tag":
					this.addTag(attr);
					break;
				case "relation":
				    this.addRelation(attr);
				    break;
				case "member":
				    this.addRelationMember(attr);
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
			Entity entity = this.entities.removeLast();

			OSMEntity.Builder builder = entity.builder();

			/* Ignorer les builder incomplets */
			if (builder == null || builder.isIncomplete())
				return;

			switch (entity.type()) {
				case NODE:
				    mapBuilder.addNode( ( (OSMNode.Builder)builder ).build() );
					break;
				case WAY:
					mapBuilder.addWay( ( (OSMWay.Builder)builder ).build() );
					break;
				case RELATION:
				    mapBuilder.addRelation( ( (OSMRelation.Builder)builder ).build() );
					break;
				case ND:
				case MEMBER:
				case TAG:
				default:
					break;
			}
		}

		/**
		 * Ajout d'un nœud dans le deque.
		 *
		 * @param attr Attributs attachés au nœud
		 */
		private void addNode (org.xml.sax.Attributes attr) {
			long id = Long.parseLong(attr.getValue("id"));
			double lon = Math.toRadians(Double.parseDouble(attr.getValue("lon")));
			double lat = Math.toRadians(Double.parseDouble(attr.getValue("lat")));

			OSMNode.Builder nb = new OSMNode.Builder(id, new PointGeo(lon, lat));

			this.entities.addLast( new Entity(Type.NODE, nb) );
		}

		/**
		 * Ajout d'un chemin dans le deque.
		 *
		 * @param attr Attributs attachés au chemin
		 */
		private void addWay (org.xml.sax.Attributes attr) {
			long id = Long.parseLong(attr.getValue("id"));

			OSMWay.Builder wb = new OSMWay.Builder(id);

			this.entities.addLast( new Entity(Type.WAY, wb) );
		}

		/**
		 * Ajout d'un noeud au bâtisseur du dernier chemin dans le deque.
		 *
		 * @param attr Attributs attachés à la référence du nœud
		 */
		private void addNodeRef (org.xml.sax.Attributes attr) {
			long ref = Long.parseLong(attr.getValue("ref"));

			OSMNode node = mapBuilder.nodeForId(ref);
			Entity parent = this.entities.getLast();

			/* Contrôle du type du bâtisseur parent */
			if (parent.type() != Type.WAY)
				throw new IllegalStateException("Le bâtisseur parent doit être du type WAY, et non " + parent.type());

			OSMWay.Builder builder = (OSMWay.Builder)parent.builder();

			if (node == null)
				builder.setIncomplete();
			else
				builder.addNode(node);

			this.entities.addLast( new Entity(Type.ND, null) );
		}

		/**
		 * Ajout d'un attribut à la dernière entité dans le deque.
		 *
		 * @param attr Attributs attachés à l'élément
		 */
		private void addTag (org.xml.sax.Attributes attr) {
			String key = attr.getValue("k");
			String value = attr.getValue("v");

			this.entities.getLast().builder().setAttribute(key, value);

			this.entities.addLast(new Entity(Type.TAG, null) );
		}
		
		/**
		 * Ajout d'une relation dans le deque.
		 *
		 * @param attr Attributs attachés à la relation
		 */
		private void addRelation (org.xml.sax.Attributes attr) {
		    long id = Long.parseLong(attr.getValue("id"));
		    
		    OSMRelation.Builder rel = new OSMRelation.Builder(id);
		  
			this.entities.addLast( new Entity(Type.RELATION, rel) );
		}
		
		/**
		 * Ajout d'un membre à la dernière relation dans le deque.
		 *
		 * @param attr Attributs attachés au membre
		 */
		private void addRelationMember (org.xml.sax.Attributes attr) {
		    long ref = Long.parseLong(attr.getValue("ref"));
		    OSMRelation.Member.Type type = OSMRelation.Member.Type.valueOf(attr.getValue("type").toUpperCase());
		    String role = attr.getValue("role");
		    
		    OSMEntity member = null;
		    switch (type) {
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

			/* Contrôle du type du bâtisseur parent */
			if (parent.type() != Type.RELATION)
				throw new IllegalStateException("Le bâtisseur parent doit être du type RELATION, et non " + parent.type());

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
        mapBuilder = new OSMMap.Builder();
        
        URL fileURL = OSMMapReader.class.getResource(fileName);

		if (fileURL == null)
			throw new FileNotFoundException("Fichier introuvable: " + fileName);

		String filePath = fileURL.getFile();

		try (InputStream file = new FileInputStream(filePath)) {
			InputStream input = (unGZip == false) ? file : new GZIPInputStream(file);

			XMLReader reader = XMLReaderFactory.createXMLReader();
			reader.setContentHandler(new OSMMapReaderHandler());
			reader.parse(new InputSource(input));
		}

		return mapBuilder.build();
    }

}
