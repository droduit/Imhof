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

public class SVGCanvas implements Canvas {
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

    public SVGCanvas (Point bottomLeft, Point topRight, int width, int height, int dpi, Color bgColor) throws ParserConfigurationException {
        pica = dpi / 72.0;

        this.width = width;
        this.height = height;

        this.lineStyles = new HashSet<>();
        this.polygonStyles = new HashSet<>();

        Point canvasBottomLeft = new Point(0, height / pica);
        Point canvasTopRight   = new Point(width / pica, 0);
        this.transform = Point.alignedCoordinateChange(bottomLeft, canvasBottomLeft, topRight, canvasTopRight);
        
        this.doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        this.root = this.doc.createElement("svg");
        this.root.setAttribute("xmlns", "http://www.w3.org/2000/svg");
        this.root.setAttribute("version", "1.1");
        this.root.setAttribute("width", Integer.toString(width));
        this.root.setAttribute("height", Integer.toString(height));
        this.doc.appendChild(this.root);

        this.style = this.doc.createElement("style");
        this.style.setAttribute("type", "text/css");
        this.root.appendChild(this.style);

        this.defs = this.doc.createElement("defs");
        this.root.appendChild(this.defs);

        Element background = this.doc.createElement("rect");
        background.setAttribute("width", Integer.toString(width));
        background.setAttribute("height", Integer.toString(height));
        background.setAttribute("fill", bgColor.toHex());
        this.root.appendChild(background);
    }

    private Element getPath (PolyLine polyline) {
        Element path = this.doc.createElement("path");
        String pathData = polyline.points()
                .stream()
                .map(this.transform)
                .map(p -> String.format("%f %f", p.x(), p.y()))
                .collect(Collectors.joining(" L ", "M", (polyline.isClosed()) ? " Z" : ""));

        path.setAttribute("d", pathData);

        return path;
    }

    public void drawPolyline(PolyLine polyline, LineStyle style) {
        Element path = this.getPath(polyline);
        path.setAttribute("class", "c" + style.hashCode());
        this.root.appendChild(path);

        this.lineStyles.add(style);
    }

    public void drawPolygon(Polygon p, Color c) {
        Element path = this.getPath(p.shell());
        path.setAttribute("class", "c" + c.hashCode());
        path.setAttribute("mask", "url(#m" + p.hashCode() + ")");

        if (p.holes().size() > 0) {
            Element mask = this.doc.createElement("mask");
            mask.setAttribute("id", "m" + p.hashCode());
            mask.setAttribute("width", Integer.toString(this.width));
            mask.setAttribute("height", Integer.toString(this.height));

            Element background = this.doc.createElement("rect");
            background.setAttribute("width", Integer.toString(this.width));
            background.setAttribute("height", Integer.toString(this.height));
            mask.appendChild(background);

            for (ClosedPolyLine hole : p.holes())
                mask.appendChild(this.getPath(hole));

            this.defs.appendChild(mask);

            path.setAttribute("mask", "url(#m" + p.hashCode() + ")");
        }

        this.root.appendChild(path);

        this.polygonStyles.add(c);
    }

    public void addRelief (BufferedImage reliefImage) throws IOException {
        ByteArrayOutputStream reliefData = new ByteArrayOutputStream();
        OutputStream encoder = Base64.getEncoder().wrap(reliefData);

        ImageIO.write(reliefImage, "png", encoder);

        encoder.close();

        Element relief = this.doc.createElement("image");
        relief.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:xlink", "http://www.w3.org/1999/xlink");
        relief.setAttribute("id", "relief");
        relief.setAttribute("x", "0");
        relief.setAttribute("y", "0");
        relief.setAttribute("width", Integer.toString((int)(this.width / this.pica)));
        relief.setAttribute("height", Integer.toString((int)(this.height / this.pica)));
        relief.setAttribute("xlink:href", "data:image/png;base64," + reliefData.toString());

        this.root.appendChild(relief);
    }

    public void svg (String filepath) throws TransformerConfigurationException, TransformerException {
        StringBuilder styleBuilder = new StringBuilder();
        styleBuilder.append(String.format("* { transform: scale(%f, %f); }", pica, pica));
        styleBuilder.append("mask rect { fill: white; stroke: none; }\n");
        styleBuilder.append("mask path { fill: black; stroke: none; }\n");
        styleBuilder.append("#relief { mix-blend-mode: multiply; }\n");

        for (LineStyle style : this.lineStyles)
            styleBuilder.append(style.toCSS());

        for (Color color : this.polygonStyles)
            styleBuilder.append(color.toCSS());

        this.style.appendChild(this.doc.createTextNode(styleBuilder.toString()));

        Transformer t = TransformerFactory.newInstance().newTransformer();
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new File(filepath));

        t.transform(source, result);
    }
}
