import java.util.HashSet;

// A class to represent each node in the OSM file
public class GraphNode {
    long id;
    double lon, lat;
    String name;
    HashSet<GraphNode> neighbors;

    public GraphNode(long identity, double longitude, double latitude) {
        id = identity;
        lon = longitude;
        lat = latitude;
        neighbors = new HashSet<>();
        name = null;
    }

    public boolean hasName() {
        return name != null;
    }

    public String getName() {
        return name;
    }

    public void setName(String newName) {
        name = newName;
    }

    public void addNeighbor(GraphNode newNeighbor) {
        neighbors.add(newNeighbor);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        GraphNode graphNode = (GraphNode) o;

        return id == graphNode.id;

    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }

    @Override
    public String toString() {
        if (this == null) {
            return "Null";
        } else {
            return "GraphNode{" + "id=" + id + '}';
        }
    }
}
