/* A QueryWindow that takes in the parameters
 * given by the front end to be processed */
public class QueryWindow {
    Node queryNode;
    double density, width, height;

    public QueryWindow(Point ul, Point lr, double w, double h) {
        queryNode = new Node("query", ul, lr);
        width = w;
        height = h;
        density = (lr.longitude - ul.longitude) / width;
    }

    public int findDepth(double rootLRLON, double rootULLON, double queryDensity) {
        int depth = 0;
        double tDPP = (rootLRLON - rootULLON) / (Math.pow(2, depth) * MapServer.TILE_SIZE);
        while (tDPP > queryDensity && depth < 7) {
            depth += 1;
            tDPP = tDPP / 2;
        }
        return depth;
    }
}
