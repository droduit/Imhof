package ch.epfl.imhof.osm;

import java.io.*;
import java.util.zip.GZIPInputStream;
import java.util.Deque;
import java.util.ArrayDeque;
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
    private static OSMMap.Builder mapBuilder;

    /**
     * Gestionnaire de contenu pour interpéter le contenu du fichier XML et
     * instancier les différentes entités.
     * 
     * @author Thierry Treyer (235116)
     * @author Dominique Roduit (234868)
     */
    public static final class OSMMapReaderHandler extends DefaultHandler {
        private Deque<Entity> entities = new LinkedList<>();

        private static class Entity {
            public static enum Type {
                NODE, WAY, ND, RELATION, MEMBER, TAG, UNKNOWN
            }

            private final Type type;
            private final OSMEntity.Builder builder;

            /**
             * Construit une entitée avec son type et son bâtisseur
             * @param type Type de l'entité (@see {@link OSMMapReader.OSMMapReaderHandler.Type})
             * @param builder Bâtisseur de l'entité
             */
            public Entity (Type type, OSMEntity.Builder builder) {
                this.type = type;
                this.builder = builder;
            }
            
            /**
             * Retourne le type de l'entité OSM
             * @return Type de l'entité
             */
            public Type type () {
                return this.type;
            }

            /**
             * Retourne le builder de l'entitée OSM
             * @return Builder de l'entitée
             */
            public OSMEntity.Builder builder () {
                return this.builder;
            }
        
        }

        /**
         * Callback lorsqu'une balise ouvrante est rencontrée
         */
        public void startElement (String uri, String lName, String qName,
                org.xml.sax.Attributes attr) throws SAXException {
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
                    this.entities.addLast(new Entity(Entity.Type.UNKNOWN, null));
                    break;
            }
        }

        /**
         * Callback lorsqu'une balise fermante est rencontrée
         */
        public void endElement (String uri, String lName, String qName) throws SAXException {
            Entity entity = this.entities.removeLast();

            OSMEntity.Builder builder = entity.builder();

            /* Ignorer les builder incomplets */
            if (builder == null || builder.isIncomplete())
                return;

            switch (entity.type()) {
                case NODE:
                    mapBuilder.addNode(((OSMNode.Builder) builder).build());
                    break;
                case WAY:
                    mapBuilder.addWay(((OSMWay.Builder) builder).build());
                    break;
                case RELATION:
                    mapBuilder.addRelation(((OSMRelation.Builder) builder).build());
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
         * @param attr
         *            Attributs attachés au nœud
         */
        private void addNode (org.xml.sax.Attributes attr) {
            long id = Long.parseLong(attr.getValue("id"));
            double lon = Math.toRadians(Double.parseDouble(attr.getValue("lon")));
            double lat = Math.toRadians(Double.parseDouble(attr.getValue("lat")));

            OSMNode.Builder nb = new OSMNode.Builder(id, new PointGeo(lon, lat));

            this.entities.addLast(new Entity(Entity.Type.NODE, nb));
        }

        /**
         * Ajout d'un chemin dans le deque.
         *
         * @param attr
         *            Attributs attachés au chemin
         */
        private void addWay (org.xml.sax.Attributes attr) {
            long id = Long.parseLong(attr.getValue("id"));

            OSMWay.Builder wb = new OSMWay.Builder(id);

            this.entities.addLast(new Entity(Entity.Type.WAY, wb));
        }

        /**
         * Ajout d'un noeud au bâtisseur du dernier chemin dans le deque.
         *
         * @param attr
         *            Attributs attachés à la référence du nœud
         * @throws IllegalStateException
         *            Si le bâtisseur parent n'est pas du type WAY
         */
        private void addNodeRef (org.xml.sax.Attributes attr) {
            long ref = Long.parseLong(attr.getValue("ref"));

            OSMNode node = mapBuilder.nodeForId(ref);
            Entity parent = this.entities.getLast();

            /* Contrôle du type du bâtisseur parent */
            if (parent.type() != Entity.Type.WAY)
                throw new IllegalStateException(
                        "Le bâtisseur parent doit être du type WAY, et non " + parent.type());

            OSMWay.Builder builder = (OSMWay.Builder) parent.builder();

            if (node == null)
                builder.setIncomplete();
            else
                builder.addNode(node);

            this.entities.addLast(new Entity(Entity.Type.ND, null));
        }

        /**
         * Ajout d'un attribut à la dernière entité dans le deque.
         *
         * @param attr
         *            Attributs attachés à l'élément
         */
        private void addTag (org.xml.sax.Attributes attr) {
            String key = attr.getValue("k");
            String value = attr.getValue("v");

            this.entities.getLast().builder().setAttribute(key, value);

            this.entities.addLast(new Entity(Entity.Type.TAG, null));
        }

        /**
         * Ajout d'une relation dans le deque.
         *
         * @param attr
         *            Attributs attachés à la relation
         */
        private void addRelation (org.xml.sax.Attributes attr) {
            long id = Long.parseLong(attr.getValue("id"));

            OSMRelation.Builder rel = new OSMRelation.Builder(id);

            this.entities.addLast(new Entity(Entity.Type.RELATION, rel));
        }

        /**
         * Ajout d'un membre à la dernière relation dans le deque.
         *
         * @param attr
         *            Attributs attachés au membre
         * @throws IllegalStateException
         *            Si le bâtisseur parent n'est pas du type RELATION
         */
        private void addRelationMember (org.xml.sax.Attributes attr) {
            long ref = Long.parseLong(attr.getValue("ref"));
            OSMRelation.Member.Type type = OSMRelation.Member.Type.valueOf(attr.getValue("type")
                    .toUpperCase());
            String role = attr.getValue("role");

            OSMEntity member = null;
            switch (type) {
                case NODE:
                    member = mapBuilder.nodeForId(ref);
                    break;
                case WAY:
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
            if (parent.type() != Entity.Type.RELATION)
                throw new IllegalStateException(
                        "Le bâtisseur parent doit être du type RELATION, et non " + parent.type());

            OSMRelation.Builder builder = (OSMRelation.Builder) this.entities.getLast().builder();

            if (member == null)
                builder.setIncomplete();
            else
                builder.addMember(type, role, member);

            this.entities.addLast(new Entity(Entity.Type.MEMBER, null));
        }
    }

    /**
     * Constructeur vide non-instanciable
     */
    private OSMMapReader () {
    }

    /**
     * Lit la carte OSM contenue dans le fichier de nom donné, en le
     * décompressant avec gzip ssi le second argument est vrai.
     * 
     * @param fileName
     *            Nom du fichier OSM à parser
     * @param unGZip
     *            true si le fichier est compressé
     * @return Carte OpenStreetMap à partir de données stockées dans un fichier
     *         au format OSM.
     * @throws IOException
     *             En cas d'erreur d'entrée/sortie, par exemple si le fichier
     *             n'existe pas
     * @throws SAXException
     *             En cas d'erreur dans le format du fichier XML contenant la
     *             carte
     */
    public static OSMMap readOSMFile (String fileName, boolean unGZip) throws IOException,
            SAXException {
        mapBuilder = new OSMMap.Builder();

        try (InputStream file = new BufferedInputStream(new FileInputStream(fileName))) {
            InputStream input = (unGZip == false) ? file : new GZIPInputStream(file);

            XMLReader reader = XMLReaderFactory.createXMLReader();
            reader.setErrorHandler(null); // On gère nous même les exceptions
            reader.setContentHandler(new OSMMapReaderHandler());
            reader.parse(new InputSource(input));
        }

        return mapBuilder.build();
    }
}
