import java.io.File;
import java.awt.image.BufferedImage;

/* A node for the QuadTree data structure
 *  -- Analogous to the TreeNode in a BinaryTree */
public class Node implements Comparable<Node> {
    String imgName;
    Point upperLeft;
    Point lowerRight;
    int depth;
    final int TILE_SIZE = 256;
    double density;
    Node NW, NE, SE, SW;
    Node[] children;
    String[] childrenNames;
    File imgFile;
    BufferedImage img;

    Node(String inputImg, Point ul, Point lr) {
        imgName = inputImg;
        imgFile = new File("img/" + imgName + ".png");
        img = null;
        if (imgName.equals("root")) {
            depth = 0;
        } else {
            depth = imgName.length();
        }
        upperLeft = ul;
        lowerRight = lr;
        density = Math.abs(upperLeft.longitude - lowerRight.longitude) / 256;
        children = new Node[4];
        childrenNames = new String[4];
    }

    public void split() {
        if (depth == 7) {
            return;
        } else {
            if (imgName.equals("root")) {
                NW = new Node("1", upperLeft,
                        new Point((upperLeft.longitude + lowerRight.longitude) / 2,
                                (upperLeft.latitude + lowerRight.latitude) / 2));
                NW.split();
                NE = new Node("2", new Point((upperLeft.longitude + lowerRight.longitude) / 2,
                        upperLeft.latitude), new Point(lowerRight.longitude,
                        (upperLeft.latitude + lowerRight.latitude) / 2));
                NE.split();
                SW = new Node("3", new Point(upperLeft.longitude,
                        (upperLeft.latitude + lowerRight.latitude) / 2),
                        new Point((upperLeft.longitude + lowerRight.longitude) / 2,
                                lowerRight.latitude));
                SW.split();
                SE = new Node("4", new Point((upperLeft.longitude + lowerRight.longitude) / 2,
                        (upperLeft.latitude + lowerRight.latitude) / 2), lowerRight);
                SE.split();
            } else {
                NW = new Node(imgName + "1", upperLeft,
                        new Point((upperLeft.longitude + lowerRight.longitude) / 2,
                                (upperLeft.latitude + lowerRight.latitude) / 2));
                NW.split();
                NE = new Node(imgName + "2",
                        new Point((upperLeft.longitude + lowerRight.longitude) / 2,
                                upperLeft.latitude),
                        new Point(lowerRight.longitude,
                                (upperLeft.latitude + lowerRight.latitude) / 2));
                NE.split();
                SW = new Node(imgName + "3", new Point(upperLeft.longitude,
                        (upperLeft.latitude + lowerRight.latitude) / 2),
                        new Point((upperLeft.longitude + lowerRight.longitude) / 2,
                                lowerRight.latitude));
                SW.split();
                SE = new Node(imgName + "4",
                        new Point((upperLeft.longitude + lowerRight.longitude) / 2,
                                (upperLeft.latitude + lowerRight.latitude) / 2), lowerRight);
                SE.split();
            }
            children[0] = NW;
            children[1] = NE;
            children[2] = SW;
            children[3] = SE;
        }
    }

    @Override
    public int compareTo(Node o) {
        if (upperLeft.longitude == o.upperLeft.longitude
                && upperLeft.latitude == o.upperLeft.latitude) {
            return 0;
        } else {
            if (upperLeft.latitude == o.upperLeft.latitude) {
                if (upperLeft.longitude < o.upperLeft.longitude) {
                    return -1;
                } else {
                    return 1;
                }
            } else if (upperLeft.latitude > o.upperLeft.latitude) {
                return -1;
            } else {
                return 1;
            }
        }
    }

    public boolean intersectsTile(Node other) {
        return !(other.upperLeft.longitude > lowerRight.longitude
                || other.lowerRight.latitude > upperLeft.latitude
                || other.lowerRight.longitude < upperLeft.longitude
                || lowerRight.latitude > other.upperLeft.latitude);
    }

    public boolean satisfiesDepthOrIsLeaf(double queryDepth) {
        return (queryDepth == depth) || isLeaf();
    }

    public boolean isLeaf() {
        return depth == 7;
    }

    @Override
    public String toString() {
        return "img/"  + imgName + ".png";
    }
}
