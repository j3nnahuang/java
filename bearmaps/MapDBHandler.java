import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.LinkedList;
import java.util.HashMap;


// A program to parse the OSM file


/**
 *  Parses OSM XML files using an XML SAX parser. Used to construct the graph of roads for
 *  pathfinding, under some constraints.
 *  See OSM documentation on
 *  <a href="http://wiki.openstreetmap.org/wiki/Key:highway">the highway tag</a>,
 *  <a href="http://wiki.openstreetmap.org/wiki/Way">the way XML element</a>,
 *  <a href="http://wiki.openstreetmap.org/wiki/Node">the node XML element</a>,
 *  and the java
 *  <a href="https://docs.oracle.com/javase/tutorial/jaxp/sax/parsing.html">SAX parser tutorial</a>.
 *  @author Alan Yao
 */
public class MapDBHandler extends DefaultHandler {
    /**
     * Only allow for non-service roads; this prevents going on pedestrian streets as much as
     * possible. Note that in Berkeley, many of the campus roads are tagged as motor vehicle
     * roads, but in practice we walk all over them with such impunity that we forget cars can
     * actually drive on them.
     */
    private static final Set<String> ALLOWED_HIGHWAY_TYPES = new HashSet<>(Arrays.asList
            ("motorway", "trunk", "primary", "secondary", "tertiary", "unclassified",
                    "residential", "living_street", "motorway_link", "trunk_link", "primary_link",
                    "secondary_link", "tertiary_link"));
    private String activeState = "";
    private final GraphDB g;
    HashMap<String, GraphNode> nodeStorage;
    LinkedList<GraphNode> wayNodes;
    boolean buildConnections = false;
    private GraphNode lastNode;

    public MapDBHandler(GraphDB g) {
        this.g = g;
        nodeStorage = new HashMap<>();
        wayNodes = new LinkedList<>();
        lastNode = null;
    }

    /**
     * Called at the beginning of an element. Typically, you will want to handle each element in
     * here, and you may want to track the parent element.
     * @param uri The Namespace URI, or the empty string if the element has no Namespace URI or
     *            if Namespace processing is not being performed.
     * @param localName The local name (without prefix), or the empty string if Namespace
     *                  processing is not being performed.
     * @param qName The qualified name (with prefix), or the empty string if qualified names are
     *              not available. This tells us which element we're looking at.
     * @param attributes The attributes attached to the element. If there are no attributes, it
     *                   shall be an empty Attributes object.
     * @throws SAXException Any SAX exception, possibly wrapping another exception.
     * @see Attributes
     */
    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes)
            throws SAXException {
        if (qName.equals("node")) {
            activeState = "node";
            GraphNode currNode = new GraphNode(Long.parseLong(attributes.getValue("id")),
                    Double.parseDouble(attributes.getValue("lon")),
                    Double.parseDouble(attributes.getValue("lat")));
            lastNode = currNode;
            nodeStorage.put(attributes.getValue("id"), currNode);
        } else if (qName.equals("way")) {
            activeState = "way";
        } else if (activeState.equals("node") && qName.equals("tag")) {
            String k = attributes.getValue("k");
            String v = attributes.getValue("v");
            if (k.equals("name")) {
                /* Might need to change this, only store location
                 * names if they are not deleted in nodeStorage.clear() */
                lastNode.setName(v);
            }
        } else if (activeState.equals("way") && qName.equals("nd")) {
            GraphNode wayNode = nodeStorage.get(attributes.getValue("ref"));
            lastNode = wayNode;
            wayNodes.add(wayNode);
        } else if (activeState.equals("way") && qName.equals("tag")) {
            // Implement to include locations w/ tag.key == "name"
            String k = attributes.getValue("k");
            String v = attributes.getValue("v");
            if (k.equals("name")) {
                lastNode.setName(v);
            } else if (k.equals("highway")) {
                if (ALLOWED_HIGHWAY_TYPES.contains(v)) {
                    buildConnections = true;
                }
            }
        }
    }

    /**
     * Receive notification of the end of an element. You may want to take specific terminating
     * actions here, like finalizing vertices or edges found.
     * @param uri The Namespace URI, or the empty string if the element has no Namespace URI or
     *            if Namespace processing is not being performed.
     * @param localName The local name (without prefix), or the empty string if Namespace
     *                  processing is not being performed.
     * @param qName The qualified name (with prefix), or the empty string if qualified names are
     *              not available.
     * @throws SAXException  Any SAX exception, possibly wrapping another exception.
     */
    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (qName.equals("way")) {
            if (!wayNodes.isEmpty()) {
                if (buildConnections) {
                    for (int i = 0; i < wayNodes.size() - 1; i++) {
                        wayNodes.get(i).addNeighbor(wayNodes.get(i + 1));
                        wayNodes.get(i + 1).addNeighbor(wayNodes.get(i));
                    }
                }
                buildConnections = false;
                wayNodes.clear();
            }
        }
    }
}
