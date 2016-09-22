package editor;

/**
 * Created by jennahuang on 3/1/16.
 */

import javafx.scene.text.Text;
import java.util.Iterator;

// Should I make this class only specific to text? //
public class DblLinkedList implements Iterable<DblLinkedList.Node> {

    public Node frontSentinel;
    public int currentPos;
    public Node currentNode;
    public Node newWord;
    private boolean newWordExists = false;
    private int size;

    public class Node { // Node inspiration from Josh Hug, 61B Lecture 5. //

        Node prev;
        Text content;
        Node next;

        /* Nonempty Node. */
        public Node(Text i) {
            content = i;
        }

        /* Empty Node, Every value is null. */
        public Node() {
            content = new Text(5, 0, "");
        }


    }

    /* Creates an empty linked list.
     * Circular nodes. Front Sentinel is connected from the back and front to
     * the back sentinel.
     * Back sentinel is connected from the back and front to the front sentinel. */
    public DblLinkedList() {
        frontSentinel = new Node();
        frontSentinel.prev = frontSentinel;
        frontSentinel.next = frontSentinel;
        currentNode = frontSentinel;
        newWord = frontSentinel;
        currentPos = 0;
        size = 0;
    }

    /* Creates and adds a new node containing Text x after currentNode.
     * currentNode is updated to this new node, and currentPos is also updated. */
    public void add(Text x) {
        Node newNode = new Node(x);
        newNode.next = currentNode.next;
        newNode.prev = currentNode;
        if (newNode.content.getText().equals(" ")) {
            newWord = newNode;
            newWordExists = true;
        }
        currentNode.next.prev = newNode;
        currentNode.next = newNode;
        currentNode = newNode;
        currentPos += 1;
        size += 1;
    }

    /* Stores the currentNode, deletes it from the list, updates currentPos and
     * currentNode to previous Node then finally returns the stored Node */
    public Text delete() {
        Text toReturn = currentNode.content;
        Node newCurrentNode = currentNode.prev;
        newCurrentNode.next = currentNode.next;
        currentNode.next.prev = newCurrentNode;
        currentNode = newCurrentNode;
        currentPos -= 1;
        size -= 1;
        return toReturn;
    }


    public boolean newWordExists() {
        return newWordExists;
    }

    public Node currentWord() {
        return newWord;
    }

    public Node closerNode(Node current, double xPos) {
        double currentNodeDistance = Math.abs(current.content.getX() - xPos);
        double nextNodeDistance = Math.abs(current.next.content.getX() - xPos);
        if (currentNodeDistance < nextNodeDistance) {
            return current.prev;
        } else if (currentNodeDistance == nextNodeDistance) {
            return current.prev;
        }
        return current;
    }

    public Node closestNode(Node firstCharOfCurrLine, Node firstCharNextLine, double x) {
        while (!firstCharOfCurrLine.equals(firstCharNextLine)) {
            if (firstCharOfCurrLine.content.getX() == x) {
                if (firstCharOfCurrLine == frontSentinel) {
                    currentNode = firstCharOfCurrLine;
                } else {
                    currentNode = firstCharOfCurrLine.prev;
                }
                firstCharOfCurrLine = firstCharNextLine;
            } else if (firstCharOfCurrLine.next.content.getX() > x) {
                currentNode = closerNode(firstCharOfCurrLine, x);
                firstCharOfCurrLine = firstCharNextLine;
            } else if (firstCharOfCurrLine.next == frontSentinel) {
                currentNode = firstCharOfCurrLine;
                firstCharOfCurrLine = firstCharNextLine;
            } else {
                firstCharOfCurrLine = firstCharOfCurrLine.next;
            }
        }
        return firstCharNextLine;
    }

    public int currentPos() {
        return currentPos;
    }

    public Text get(int index) {
        if (size == 0 || index > size - 1) {
            return null;
        }
        Node currentNode = frontSentinel.next;
        Text TextIndexed = frontSentinel.next.content;
        int counter = 0;
        while (counter < index && TextIndexed != null) {
            currentNode = currentNode.next;
            TextIndexed = currentNode.content;
            counter += 1;
        }
        return TextIndexed;

    }

    /* Returns true if deque is empty, false otherwise. */
    public boolean isEmpty() {
        if (frontSentinel.next.content == null) {
            return true;
        }
        return false;
    }

    /* Returns the number of Texts in the Deque. */
    public int size() {
        return size;
    }

    @Override
    public Iterator<Node> iterator() {
        return new DblLinkedListIterator();
    }

    public class DblLinkedListIterator implements Iterator<Node> {
        private Node curr;

        public DblLinkedListIterator() {
            curr = frontSentinel.next;
        }

        public boolean hasNext() {
            return curr != frontSentinel;
        }

        public Node next() {
            Node toReturn = curr;
            curr = curr.next;
            return toReturn;
        }
    }


    /* Adds an Text to the front of
     the Deque. No looping or recursion.
    public void addFirst(Text object) {
        Node newNode = new Node(object);
        Node currentFirst = frontSentinel.next;
        frontSentinel.next = newNode;
        currentFirst.prev = newNode;
        newNode.prev = frontSentinel;
        newNode.next = currentFirst;
        size += 1;
    }

    /* Adds an Text to the back of the Deque.
    public void addLast(Text object) {
        Node newNode = new Node(object);
        Node currentLast = backSentinel.prev;
        backSentinel.prev = newNode;
        currentLast.next = newNode;
        newNode.prev = currentLast;
        newNode.next = backSentinel;
        size += 1;
    }

    public void add(Text object) {

    }

    /* Prints the Texts in the Deque from the first to
     * last, separate by space. */
    public void printDeque() {
        if (this.isEmpty()) {
            return;
        }
        Node currentNode = frontSentinel.next;
        Text firstText = currentNode.content;
        while (firstText != null) {
            System.out.print(firstText + " ");
            currentNode = currentNode.next;
            firstText = currentNode.content;
        }
        System.out.println();

    }

    /* Removes and returns the Text at the front of the Deque.
     * If no such Text exists, returns null.
     * No looping or recursion.
    public Text removeFirst() {
        if (this.isEmpty()) {
            return null;
        }
        Node currentFirst = frontSentinel.next;
        Node newFirst = currentFirst.next;
        frontSentinel.next = newFirst;
        newFirst.prev = frontSentinel;
        // newFirst.next = currentFirst.next;
        // newFirst.next.prev = newFirst;
        size -= 1;
        return currentFirst.content;
    } */

    /* Removes and returns the Text at the back of the Deque. If no
     * such Text exists, returns null.
     * No looping or recursion.
    public Text removeLast() {
        if (this.isEmpty()) {
            return null;
        }
        Node currentLast = backSentinel.prev;
        Text TextRemoved = currentLast.content;
        backSentinel.prev = currentLast.prev;
        currentLast.prev.next = backSentinel;
        size -=1;
        return TextRemoved;

    } */

    /*
    private Text helperGet(Node p, int index) {
        if (index == size) {
            return null;
        }
        if (index == 0) {
            return p.content;
        }
        return helperGet(p.next, index - 1);


    } */

    /* Same as get, uses recursion.
    public Text getRecursive(int index) {
        Text indexed  = helperGet(frontSentinel.next, index);
        return indexed;
    } */

}
