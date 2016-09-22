import java.awt.Graphics;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.PriorityQueue;

/* QuadTree equivalent to a BinaryTree but
 * with four children instead of two */
public class QuadTree {
    Node root;
    ArrayList<Node> rasteredImgs;
    PriorityQueue<Node> nodeHeap;
    int rasteredImgsWidth, rasteredImgsHeight;

    public QuadTree(Point rootUpperLeft, Point rootLowerRight) {
        root = new Node("root", rootUpperLeft, rootLowerRight);
        root.split();
        rasteredImgs = new ArrayList<>();
        nodeHeap = new PriorityQueue<>();
        rasteredImgsWidth = 0;
        rasteredImgsHeight = 0;
    }

    public void clearImage() {
        rasteredImgs.clear();
    }

    public void findRasterImages(Node start, QueryWindow thisQuery, int queryDepth) {
        if (start.intersectsTile(thisQuery.queryNode)) {
            if (start.satisfiesDepthOrIsLeaf(queryDepth)) {
                rasteredImgs.add(start);
                nodeHeap.add(start);
            } else {
                for (Node child : start.children) {
                    findRasterImages(child, thisQuery, queryDepth);
                }
            }
        }
    }

    public int findRasteredImgsWidth() {
        double check = rasteredImgs.get(0).upperLeft.latitude;
        int count = 0;
        for (Node x : rasteredImgs) {
            if (check == x.upperLeft.latitude) {
                count += 1;
            }
        }
        rasteredImgsWidth = count * MapServer.TILE_SIZE;
        return rasteredImgsWidth;
    }

    public int findRasteredImgsHeight() {
        double check = rasteredImgs.get(0).upperLeft.longitude;
        int count = 0;
        for (Node x : rasteredImgs) {
            if (check == x.upperLeft.longitude) {
                count += 1;
            }
        }
        rasteredImgsHeight = count * MapServer.TILE_SIZE;
        return rasteredImgsHeight;
    }

    public BufferedImage rasterizeImage() {
        int imgWidth = findRasteredImgsWidth();
        int imgHeight = findRasteredImgsHeight();
        BufferedImage result = new BufferedImage(
                imgWidth, imgHeight,
                BufferedImage.TYPE_INT_RGB);
        Graphics g = result.getGraphics();
        BufferedImage bi;
        try {
            int x = 0;
            int y = 0;
            for (Node image : rasteredImgs) {
                if (image.img != null) {
                    bi = image.img;
                } else {
                    bi = ImageIO.read(image.imgFile);
                    image.img = bi;
                }
                g.drawImage(bi, x, y, null);
                x += 256;
                if (x >= result.getWidth()) {
                    x = 0;
                    y += bi.getHeight();
                }
            }
        } catch (IOException e) {
            System.out.println("Can't Retrieve Image");
        }
        return result;
    }
}
