package ch.epfl.imhof.painting;

import ch.epfl.imhof.geometry.Point;
import ch.epfl.imhof.geometry.PolyLine;
import ch.epfl.imhof.geometry.ClosedPolyLine;
import ch.epfl.imhof.geometry.Polygon;

import java.util.function.Function;
import java.util.stream.Collectors;

import java.util.Set;
import java.util.HashSet;

import java.util.Base64;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.File;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.stream.StreamResult;

/**
 * Mise en oeuvre concrète de toile qui dessine les primitives
 * demandées dans une image vectorielle.
 *
 * Note:
 * =====
 *   Pas toutes les chaîne litérales ont été placées en static final, le code en serait plus
 *   alourdi qu'autre chose. Ainsi, les noms de balises et d'attributs ont été laissés tel quel.
 *
 *   Ici, le choix a été fait de manipuler le DOM pour créer l'image vectorielle.
 *   Une autre solution serait d'utiliser un système de template pour générer le SVG.
 *
 * @author Thierry Treyer (235116)
 * @author Dominique Roduit (234868)
 */
public class SVGCanvas implements Canvas {
    private final static String RELIEF_FORMAT = "png";
    private final static String XMLNS_URL = "http://www.w3.org/2000/xmlns/";
    private final static String XMLNS_SVG_URL = "http://www.w3.org/2000/svg";
    private final static String XMLNS_SVG_VERSION = "1.1";
    private final static String XMLNS_XLINK_URL = "http://www.w3.org/1999/xlink";
    private final static String BASE64_DATA_HEAD = "data:image/png;base64,";

    private final Document doc;
    private final Element root;
    private final Element style;
    private final Element defs;

    private final int width;
    private final int height;
    private final double pica;

    private final Function<Point, Point> transform;

    private final Set<LineStyle> lineStyles;
    private final Set<Color> polygonStyles;

    /**
     * Construit une image de la toile
     * @param bottomLeft Coin bas-gauche de la toile
     * @param topRight Coin haut-droite de la toile
     * @param width Largeur de l'image de la toile (en pixels)
     * @param height Hauteur de l'image de la toile (en pixels)
     * @param dpi Résolution de l'image de la toile (en points par pouce, dpi)
     * @param bgColor Couleur de fond de la toile
     */
    public SVGCanvas (Point bottomLeft, Point topRight, int width, int height, int dpi, Color bgColor) throws ParserConfigurationException {
        pica = dpi / 72.0;

        this.width = width;
        this.height = height;

        this.lineStyles = new HashSet<>();
        this.polygonStyles = new HashSet<>();

        Point canvasBottomLeft = new Point(0, height / pica);
        Point canvasTopRight   = new Point(width / pica, 0);
        this.transform = Point.alignedCoordinateChange(bottomLeft, canvasBottomLeft, topRight, canvasTopRight);
        
        // Création du document
        this.doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        this.root = this.doc.createElement("svg");
        this.root.setAttribute("xmlns", XMLNS_SVG_URL);
        this.root.setAttribute("version", XMLNS_SVG_VERSION);
        this.root.setAttribute("width", Integer.toString(width));
        this.root.setAttribute("height", Integer.toString(height));
        this.doc.appendChild(this.root);

        // Création de la balise qui contiendra le style
        this.style = this.doc.createElement("style");
        this.style.setAttribute("type", "text/css");
        this.root.appendChild(this.style);

        // Création de la balise qui contiendra les masques
        this.defs = this.doc.createElement("defs");
        this.root.appendChild(this.defs);

        // Création du background
        Element background = this.doc.createElement("rect");
        background.setAttribute("width", Integer.toString(width));
        background.setAttribute("height", Integer.toString(height));
        background.setAttribute("fill", bgColor.toHex());
        this.root.appendChild(background);
    }

    private Element getPath (PolyLine polyline) {
        Element path = this.doc.createElement("path");

        // Création d'un chemin SVG
        String pathData = polyline.points()
                .stream()
                .map(this.transform)
                .map(p -> String.format("%f %f", p.x(), p.y()))
                .collect(Collectors.joining(" L ", "M", (polyline.isClosed()) ? " Z" : ""));

        path.setAttribute("d", pathData);

        return path;
    }

    @Override
    public void drawPolyline(PolyLine polyline, LineStyle style) {
        Element path = this.getPath(polyline);
        path.setAttribute("class", "c" + style.hashCode());
        this.root.appendChild(path);

        this.lineStyles.add(style);
    }

    @Override
    public void drawPolygon(Polygon p, Color c) {
        Element path = this.getPath(p.shell());
        path.setAttribute("class", "c" + c.hashCode());
        path.setAttribute("mask", "url(#m" + p.hashCode() + ")");

        if (p.holes().size() > 0) {
            // Création du masque des trous
            Element mask = this.doc.createElement("mask");
            mask.setAttribute("id", "m" + p.hashCode());
            mask.setAttribute("width", Integer.toString(this.width));
            mask.setAttribute("height", Integer.toString(this.height));

            // Initialisation du masque à "tout visible"
            Element background = this.doc.createElement("rect");
            background.setAttribute("width", Integer.toString(this.width));
            background.setAttribute("height", Integer.toString(this.height));
            mask.appendChild(background);

            // Ajout des trous dans le masque
            for (ClosedPolyLine hole : p.holes())
                mask.appendChild(this.getPath(hole));

            this.defs.appendChild(mask);

            path.setAttribute("mask", "url(#m" + p.hashCode() + ")");
        }

        this.root.appendChild(path);

        this.polygonStyles.add(c);
    }

    /**
     * Prend l'image discrète d'un relief et l'inclu dans le dessin vectoriel.
     *
     * @param reliefImage L'image discrète du relief
     */
    public void addRelief (BufferedImage reliefImage) throws IOException {
        // Conversion de l'image en Base64
        ByteArrayOutputStream reliefData = new ByteArrayOutputStream();
        OutputStream encoder = Base64.getEncoder().wrap(reliefData);

        ImageIO.write(reliefImage, RELIEF_FORMAT, encoder);

        encoder.close();

        // Création de la balise contenant l'image du relief
        Element relief = this.doc.createElement("image");
        relief.setAttributeNS(XMLNS_URL, "xmlns:xlink", XMLNS_XLINK_URL);
        relief.setAttribute("id", "relief");
        relief.setAttribute("x", "0");
        relief.setAttribute("y", "0");
        relief.setAttribute("width", Integer.toString((int)(this.width / this.pica)));
        relief.setAttribute("height", Integer.toString((int)(this.height / this.pica)));
        relief.setAttribute("xlink:href", BASE64_DATA_HEAD + reliefData.toString());

        this.root.appendChild(relief);
    }

    /**
     * Écrit l'image vectorielle dans le fichier donné.
     *
     * @param filepath Le chemin du fichier en sortie
     */
    public void write (String filepath) throws TransformerConfigurationException, TransformerException {
        // Ajout des styles de base
        StringBuilder styleBuilder = new StringBuilder();
        styleBuilder.append(String.format("* { transform: scale(%f, %f); }", pica, pica));
        styleBuilder.append("mask rect { fill: white; stroke: none; }\n");
        styleBuilder.append("mask path { fill: black; stroke: none; }\n");
        styleBuilder.append("#relief { mix-blend-mode: multiply; }\n");

        // Ajout des styles de lignes
        for (LineStyle style : this.lineStyles)
            styleBuilder.append(style.toCSS());

        // Ajout des styles de polygones
        for (Color color : this.polygonStyles)
            styleBuilder.append(color.toCSS());

        this.style.appendChild(this.doc.createTextNode(styleBuilder.toString()));

        // Transformation du DOM en document XML
        Transformer t = TransformerFactory.newInstance().newTransformer();
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new File(filepath));

        t.transform(source, result);
    }
}
