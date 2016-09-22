package editor;

import javafx.geometry.VPos;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.scene.control.ScrollBar;

/**
 * Created by jennahuang on 3/5/16.
 */
public class TextBuffer {

    private static final int MARGIN = 5;
    private static final int STARTING_FONT_SIZE = 12;
    private static final int STARTING_TEXT_POSITION_X = MARGIN;
    private static final int STARTING_TEXT_POSITION_Y = 0;
    private static int fontSize = STARTING_FONT_SIZE;
    private static String fontName = "Verdana";

    static DblLinkedList.Node newWord;
    static DblLinkedList.Node frontOfNewWord;
    static boolean newWordExists;
    static int currentWordSum;
    static boolean alreadyAdded = false;

    public static boolean isNewLine(Text i) {
        return (i.getText().equals("\n") || i.getText().equals("\r\n"));
    }

    public static boolean isWhiteSpace(Text i) {
        return (i.getText().equals(" "));
    }

    public static int render(DblLinkedList textInfo, ArrayDeque lineBreaks, int windowWidth, int windowHeight,
                             int textPositionX, int textPositionY, int fontSize, Rectangle curs, ScrollBar scroll) {
        for (DblLinkedList.Node t : textInfo) {
            currentWordSum = MARGIN;
            t.content.setTextOrigin(VPos.TOP);
            t.content.setFont(Font.font(fontName, fontSize));
            if (isNewLine(t.content)) {
                if (!lineBreaks.alreadyExists(t.next)) {
                    lineBreaks.addLast(t.next);
                }
                textPositionX = STARTING_TEXT_POSITION_X;
                t.content.setX(STARTING_TEXT_POSITION_X);
                if (isNewLine(t.prev.content)) {
                    t.content.setY(Math.round(t.prev.prev.content.getY()) + t.prev.content.getLayoutBounds().getHeight());
                } else {
                    t.content.setY(Math.round(t.prev.content.getY() + t.prev.content.getLayoutBounds().getHeight()));
                }
                textPositionY = (int) Math.round(t.content.getY());
            } else {
                if (isWhiteSpace(t.content)) {
                    newWord = t;
                    newWordExists = true;
                }
                if (textPositionX + Math.round(t.content.getLayoutBounds().getWidth())
                        + scroll.getLayoutBounds().getWidth() + MARGIN >= windowWidth) {
                    if (newWordExists) {
                        if (isWhiteSpace(t.content) && t.next == textInfo.frontSentinel) {
                            if (!lineBreaks.alreadyExists(t.next)) {
                                lineBreaks.addLast(t.next);
                            }
                            t.content.setY(Math.round(t.prev.content.getY() + t.prev.content.getLayoutBounds().getHeight()));
                            t.content.setX(0);
                        } else {
                            frontOfNewWord = newWord.next;
                            frontOfNewWord.content.setX(STARTING_TEXT_POSITION_X);
                            currentWordSum += Math.round(frontOfNewWord.content.getLayoutBounds().getWidth());
                            frontOfNewWord.content.setY(Math.round(t.prev.content.getY() + t.prev.content.getLayoutBounds().getHeight()));
                            if (!lineBreaks.alreadyExists(frontOfNewWord)) {
                                lineBreaks.addLast(frontOfNewWord);
                            }
                            frontOfNewWord = frontOfNewWord.next;
                            while (!isWhiteSpace(frontOfNewWord.content) && frontOfNewWord != textInfo.frontSentinel) {
                                frontOfNewWord.content.setX(Math.round(frontOfNewWord.prev.content.getX() + frontOfNewWord.prev.content.getLayoutBounds().getWidth()));
                                frontOfNewWord.content.setY(Math.round(frontOfNewWord.prev.content.getY()));
                                currentWordSum += Math.round(frontOfNewWord.content.getLayoutBounds().getWidth());
                                frontOfNewWord = frontOfNewWord.next;
                            }
                            if (currentWordSum + scroll.getLayoutBounds().getWidth()
                                    + MARGIN >= windowWidth) {
                                lineBreaks.removeLast();
                                frontOfNewWord = newWord.next;
                                textPositionX = (int) Math.round(newWord.content.getX() + newWord.content.getLayoutBounds().getWidth());
                                while (frontOfNewWord != textInfo.frontSentinel) {
                                    if (textPositionX +scroll.getLayoutBounds().getWidth() + Math.round(frontOfNewWord.content.getLayoutBounds().getWidth()) + MARGIN >= windowWidth) {
                                        textPositionX = STARTING_TEXT_POSITION_X;
                                        frontOfNewWord.content.setX(textPositionX);
                                        frontOfNewWord.content.setY(Math.round(frontOfNewWord.prev.content.getY() + frontOfNewWord.prev.content.getLayoutBounds().getHeight()));
                                        textPositionY = (int) Math.round(frontOfNewWord.content.getY());
                                        if (!lineBreaks.alreadyExists(frontOfNewWord)) {
                                            lineBreaks.addLast(frontOfNewWord);
                                        }
                                    } else {
                                        frontOfNewWord.content.setX(textPositionX);
                                        frontOfNewWord.content.setY(Math.round(frontOfNewWord.prev.content.getY()));
                                    }
                                    textPositionX = (int) Math.round(frontOfNewWord.content.getX() + Math.round(frontOfNewWord.content.getLayoutBounds().getWidth()));
                                    frontOfNewWord = frontOfNewWord.next;
                                }
                                currentWordSum = MARGIN;
                            }
                            //t.content.setX(Math.round(t.prev.content.getX() + t.prev.content.getLayoutBounds().getWidth()));
                            // t.content.setY(Math.round(t.prev.content.getY()));
                        }
                    } else {
                        t.content.setX(STARTING_TEXT_POSITION_X);
                        t.content.setY(Math.round(t.prev.content.getLayoutBounds().getHeight()));
                        if (!lineBreaks.alreadyExists(t)) {
                            lineBreaks.addLast(t);
                        }
                    }
                } else {
                    t.content.setX(Math.round(t.prev.content.getX() + t.prev.content.getLayoutBounds().getWidth()));
                    t.content.setY(Math.round(t.prev.content.getY()));
                }
            }
            textPositionX = (int) Math.round(t.content.getX() + t.content.getLayoutBounds().getWidth());
            textPositionY = (int) Math.round(t.content.getY());
        }
        if ((isWhiteSpace(textInfo.currentNode.content) ||
                (isNewLine(textInfo.currentNode.content)))
                && lineBreaks.alreadyExists(textInfo.currentNode.next)) {
            if (textInfo.currentNode.next == textInfo.frontSentinel) {
                curs.setX(Math.round(textInfo.currentNode.content.getX() +
                        textInfo.currentNode.content.getLayoutBounds().getWidth()));
                curs.setY(Math.round(textInfo.currentNode.content.getY()));
            } else if (isNewLine(textInfo.currentNode.next.content)) { //
                curs.setX(Math.round(textInfo.currentNode.content.getX()
                        + textInfo.currentNode.content.getLayoutBounds().getWidth()));
                curs.setY(Math.round(textInfo.currentNode.content.getY()));
            } else {
                curs.setX(Math.round(textInfo.currentNode.next.content.getX()));
                curs.setY(Math.round(textInfo.currentNode.next.content.getY()));
            }
        } else if (lineBreaks.alreadyExists(textInfo.currentNode)) {
            if (isNewLine(textInfo.currentNode.content)) {
                if (textInfo.currentNode.next == textInfo.frontSentinel) {
                    curs.setY(Math.round(textInfo.currentNode.prev.content.getY()
                            + textInfo.currentNode.prev.content.getLayoutBounds().getHeight()));
                } else {
                    curs.setY(Math.round(textInfo.currentNode.next.content.getY()
                            + textInfo.currentNode.next.content.getLayoutBounds().getHeight()));
                }
            } else {
                curs.setX(Math.round(textInfo.currentNode.content.getX()
                        + textInfo.currentNode.content.getLayoutBounds().getWidth()));
                curs.setY(Math.round(textInfo.currentNode.content.getY()));
            }
        /* if next node is a line break */
        } else if (isNewLine(textInfo.currentNode.next.content)) {
            curs.setX(Math.round(textInfo.currentNode.content.getX()
                    + textInfo.currentNode.content.getLayoutBounds().getWidth()));
            curs.setY(Math.round(textInfo.currentNode.content.getY()));
        } else {
            if (textInfo.currentNode.next == textInfo.frontSentinel) {
                curs.setX(Math.round(textInfo.currentNode.content.getX() + textInfo.currentNode.content.getLayoutBounds().getWidth()));
            } else {
                curs.setX(Math.round(textInfo.currentNode.next.content.getX()));
            }
            curs.setY(Math.round(textInfo.currentNode.content.getY()));
        }
        if (isNewLine(textInfo.currentNode.content)) {
            // don't change height; height of a linebreak is double height of any other char
        } else if (textInfo.currentNode == textInfo.frontSentinel) {
            curs.setHeight(Math.round(textInfo.currentNode.next.content.getLayoutBounds().getHeight()));
        } else {
            curs.setHeight(Math.round(textInfo.currentNode.content.getLayoutBounds().getHeight()));
        }
        scroll.setMax(lineBreaks.totalHeight());
        return lineBreaks.currentLine(textInfo);
    }
}