package editor;

/**
 * Created by jennahuang on 3/5/16.
 */
public class ArrayDeque {

    private DblLinkedList.Node[] lineBreaks;
    private final int nextFront;
    private int nextBack;
    private int size;
    private static int rFactor = 2;
    private int currentLine;

    /* Constructor for an ArrayDeque.
     * nextFront is the index of where the front index will nextBe. // Josh Hug project Slides.
     * nextBack is the index of where the back index will nextBe. */
    public ArrayDeque() {
        lineBreaks = new DblLinkedList.Node[200];
        nextBack = 0;
        nextFront = 0;
        size = 0;
    }

    /* Adds an item to the back of the Deque.
    * First checks if there is space for the item in the array.
      If there isn't, the array is resized.
    * If there is space, the item is added at nextBack.
    * nextBack is incremented by one.
    * nextBack = 7 --> addLast --> nextBack = 0. */
    public void addLast(DblLinkedList.Node a) {
        checkResize();
        lineBreaks[nextBack] = a;
        nextBack = plusOne(nextBack);
        size += 1;
    }

    public DblLinkedList.Node getLast() {
        if (size == 0) {
            return null;
        }
        return lineBreaks[minusOne(nextBack)];
    }

    public int lastLine() {
        return nextBack;
    }

    public int currentLine(DblLinkedList textDeque) {
        for (int i = nextFront; i < nextBack; i = plusOne(i)) {
            DblLinkedList.Node firstNodeOfNextLine = lineBreaks[i];
            if (i+1 == nextBack) {
                currentLine = i;
            } else {
                while ((i + 1 != nextBack) && (firstNodeOfNextLine != lineBreaks[i + 1])) {
                    if (firstNodeOfNextLine == textDeque.currentNode) {
                        currentLine = i;
                        return currentLine;
                    }
                    firstNodeOfNextLine = firstNodeOfNextLine.next;
                }
            }
        }
        return currentLine;
    }

    public int lineLength(int i) {
        DblLinkedList.Node thisLineChar = get(i);
        DblLinkedList.Node nextLine;
        int currentLineLength = 5;
        if (i + 1 == size) {
            nextLine = get(0);
        } else {
            nextLine = get(i + 1);
        }
        while (!thisLineChar.equals(nextLine)) {
            currentLineLength += thisLineChar.content.getLayoutBounds().getWidth();
            thisLineChar = thisLineChar.next;
        }
        return currentLineLength;
    }

    public int totalHeight() {
        /* int sumHeight = 0;
        for (int i = nextFront; i < nextBack; i = plusOne(i)) {
            sumHeight += Math.round(lineBreaks[i].content.getLayoutBounds().getHeight());
        }
        return sumHeight; */
        int lineHeight = (int) Math.round(lineBreaks[nextFront].next.content.getLayoutBounds().getHeight());
        return lineHeight * size;
    }
    /* removes and returns the item at the back of the Deque.
     * toReturn stores the item at the back.
     * the back index is made null.
     * size is reduced.
     * checkResize() makes sure that the array is at the allowed memory. */
    public DblLinkedList.Node removeLast() {
        if (size == 0) {
            return null;
        }
        DblLinkedList.Node toReturn = lineBreaks[minusOne(nextBack)];
        lineBreaks[minusOne(nextBack)] = null;
        size -= 1;
        nextBack = minusOne(nextBack);
        checkResize();
        return toReturn;
    }
    public void clear() {
        for (int i = nextFront + 1; i < nextBack; i++) {
            lineBreaks[i] = null;
        }
        size = 1;
        nextBack = 0;
    }

    /* Resizes the underlying array data structure.
     * Array size is determined by number of items and rFactor.
       The rFactor doubles the array, so that is double the amount of items.
     * this allows for the 25 percent rule to be followed.
     * indexOne is the value of the front indice. (arrayDeque.get(0))
     * indexOne is incremented with the plusOne method, depending on the number
      of items.
     * resizedArray and items are matched up
     * nextFront points to the beginning of the new list.
     * nextBack points to the end of the new list. */
    private void resize(int capacity) {
        DblLinkedList.Node[] newArray = new DblLinkedList.Node[capacity];
        int currentFront = plusOne(nextFront);
        int counter = 0;
        while (counter < size) {
            newArray[counter] = lineBreaks[currentFront];
            currentFront = plusOne(currentFront);
            counter += 1;
        }
        lineBreaks = newArray;
        nextBack = size;
    }

    /* Calls a helper function to resize.
     * checks if the array is atleast 25 percent filled. If its not, resize.
     * Otherwise don't do anything. */
    private void checkResize() {
        double ratio = (double) size / lineBreaks.length;
        if (size == lineBreaks.length) {
            resize(size * 2);
        }
    }

    /* Returns the size of the Array Deque. */
    public int size() {
        return size;
    }

    /* Gets item at the given index where 0 is the front. if no such item
    exists, returns null. Must not alter the deque.
     * If the index is greater than the number of items - 1 >> null.
     * if it is 0, currFront points to the very front of the Deque.
     * counter refers to index 1 of the ArrayDeque.
     * toReturn refers to arrayDeque.get(1)
     * The loop makes sure that the index is greater than 1, and that counter doesn't
     point to the front. */
    public DblLinkedList.Node get(int index) {
        if (index > size - 1){
            return null;
        }
        int currFront = nextFront;
        if (index == 0) {
            return lineBreaks[currFront];
        }
        int counter = plusOne(currFront);
        DblLinkedList.Node toReturn = lineBreaks[counter];
        while(index > 1) {
            counter = plusOne(counter);
            toReturn = lineBreaks[counter];
            index -= 1;
        }
        return toReturn;

    }

    public boolean alreadyExists(DblLinkedList.Node a) {
        for (int i = nextFront; i < nextBack; i = plusOne(i)) {
            if (lineBreaks[i].equals(a)) {
                return true;
            }
        }
        return false;
    }

    public void printDeque() {
        if (size == 0) {
            return;
        }
        int index = 0;
        while (index < size) {
            String item = this.get(index).content.getText();
            System.out.print(item + " ");
            index += 1;
        }
        System.out.println();
    }


    /* Incremenets the index by 1. If the index is at the last
     * index in the array, makes the index 0. */
    private int plusOne(int index) {
        if (index == lineBreaks.length - 1) {
            return 0;
        }
        return index + 1;
    }

    /* Subtract the index by 1. If the index is at 0,
     * places the index at last index of the array. */
    private int minusOne(int index) {
        if (index == 0) {
            return lineBreaks.length - 1;
        }
        return index - 1;
    }

}
