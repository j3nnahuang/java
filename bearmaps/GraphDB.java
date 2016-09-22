import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import java.util.HashMap;
import java.util.Set;
import java.util.Iterator;

// A Graph handler to represent the map given by the OSM file
public class GraphDB {
    /**
     * Example constructor shows how to create and start an XML parser.
     * @param dbPath Path to the XML file to be parsed.
     */

    HashMap<String, GraphNode> graphMap;
    HashMap<String, GraphNode> locations;
    Trie locationNames;

    public GraphDB(String dbPath) {
        graphMap = new HashMap<>();
        locations = new HashMap<>();
        try {
            File inputFile = new File(dbPath);
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            MapDBHandler maphandler = new MapDBHandler(this);
            saxParser.parse(inputFile, maphandler);
            graphMap = maphandler.nodeStorage;
            createLocations();
            locationNames = buildTrieForLocations();
            clean();
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Helper to process strings into their "cleaned" form, ignoring punctuation and capitalization.
     * @param s Input string.
     * @return Cleaned string.
     */
    static String cleanString(String s) {
        return s.replaceAll("[^a-zA-Z ]", "").toLowerCase();
    }

    /**
     *  Remove nodes with no connections from the graph.
     *  While this does not guarantee that any two nodes in the remaining graph are connected,
     *  we can reasonably assume this since typically roads are connected.
     */
    private void clean() {
        Iterator<HashMap.Entry<String, GraphNode>> iter = graphMap.entrySet().iterator();
        while (iter.hasNext()) {
            HashMap.Entry<String, GraphNode> entry = iter.next();
            if (entry.getValue().neighbors.size() == 0) {
                iter.remove();
            }
        }
    }

    private void createLocations() {
        Iterator<HashMap.Entry<String, GraphNode>> iter = graphMap.entrySet().iterator();
        while (iter.hasNext()) {
            HashMap.Entry<String, GraphNode> entry = iter.next();
            if (entry.getValue().hasName()) {
                locations.put(entry.getValue().getName(), entry.getValue());
            }
        }
    }

    public double euDistance(GraphNode node1, GraphNode node2) {
        return Math.sqrt(Math.pow((node2.lon - node1.lon), 2)
                + Math.pow((node2.lat - node1.lat), 2));
    }

    public GraphNode findClosest(double lon, double lat) {
        GraphNode minimum = null;
        double minDistance = Double.MAX_VALUE;
        for (String key : graphMap.keySet()) {
            double currNodeLon = graphMap.get(key).lon;
            double currNodeLat = graphMap.get(key).lat;
            double distance = Math.sqrt(Math.pow((lon - currNodeLon), 2)
                    + Math.pow((lat - currNodeLat), 2));
            if (distance < minDistance) {
                minimum = graphMap.get(key);
                minDistance = distance;
            }
        }
        return minimum;
    }

    public Trie buildTrieForLocations() {
        Set<String> names = locations.keySet();
        Trie toReturn = new Trie();
        for (String n : names) {
            toReturn.insert(n);
        }
        return toReturn;
    }
}
