/* A wrapper class for search nodes when trying
 * to find the shortest route */
public class SearchNode implements Comparable<SearchNode> {
    GraphNode node;
    double priority;

    public SearchNode(GraphNode curr, GraphNode target, double distanceTraveled) {
        node = curr;
        priority = distanceTraveled + h(node, target);
    }

    private double h(GraphNode node1, GraphNode node2) {
        return Math.sqrt(Math.pow((node2.lon - node1.lon), 2)
                + Math.pow((node2.lat - node1.lat), 2));
    }

    @Override
    public int compareTo(SearchNode other) {
        return Double.compare(this.priority, other.priority);
    }
}
