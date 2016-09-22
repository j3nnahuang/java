package editor;

import javafx.application.Application;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.geometry.Orientation;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import java.util.List;
import java.lang.Character;
import javafx.event.EventHandler;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.control.ScrollBar;
import javafx.util.Duration;
import javafx.event.EventType;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.File;

/* PROGRESS:
   Cursor left & right works!
        Need to implement clicking, up & down arrows
   Word wrapping almost done -- need to deal with whitespace
   Newlines work
   Array of newLines done
   Backspace fasho
   Window resize
        Listener changes the size, but text won't re-render :(
   Changing font size done

 */

public class Editor extends Application {
    private static int WINDOW_WIDTH = 500;
    private static int WINDOW_HEIGHT = 500;
    private static final int MARGIN = 5;
    private static final int STARTING_FONT_SIZE = 12;
    private static final int STARTING_TEXT_POSITION_X = MARGIN;
    private static final int STARTING_TEXT_POSITION_Y = 0;
    private static int textPositionX = STARTING_TEXT_POSITION_X;
    private static int textPositionY = STARTING_TEXT_POSITION_Y;
    private Text displayText = new Text(STARTING_TEXT_POSITION_X,
                                        STARTING_TEXT_POSITION_Y, "");
    private String fontName = "Verdana";
    private static int fontSize = STARTING_FONT_SIZE;

    private Group root;
    private Group textRoot;
    private Scene scene;
    private DblLinkedList letters = new DblLinkedList();
    private ArrayDeque newLines = new ArrayDeque();
    private int currentLine;
    private String newChar;
    private Text toAdd;
    private Text toRemove;
    private String fileName;
    private final Rectangle cursor;
    private ScrollBar scrollBar;


    public Editor() {
        root = new Group();
        textRoot = new Group();
        root.getChildren().add(textRoot);
        scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT, Color.WHITE);
        newLines.addLast(letters.currentNode);
        cursor = new Rectangle(STARTING_TEXT_POSITION_X, 0,
                1, displayText.getLayoutBounds().getHeight());
        scrollBar = new ScrollBar();
        scrollBar.setOrientation(Orientation.VERTICAL);
        scrollBar.setPrefHeight(WINDOW_HEIGHT);
        scrollBar.setMin(0);
        scrollBar.setMax(newLines.totalHeight());
        scrollBar.setLayoutX(WINDOW_WIDTH - scrollBar.getLayoutBounds().getWidth());
    }


    private class KeyEventHandler implements EventHandler<KeyEvent> {

        KeyEventHandler(final Group root, int windowWidth, int windowHeight) {
            displayText = new Text(STARTING_TEXT_POSITION_X, STARTING_TEXT_POSITION_Y, "");
            displayText.setTextOrigin(VPos.TOP);
            displayText.setFont(Font.font(fontName, STARTING_FONT_SIZE));
            textRoot.getChildren().add(displayText);
        }

        @Override
        public void handle(KeyEvent keyEvent) {
            if (keyEvent.getEventType() == KeyEvent.KEY_TYPED && !keyEvent.isShortcutDown()) {
                newChar = keyEvent.getCharacter();
                if (newChar.length() > 0 && newChar.charAt(0) != 8) {
                    if (newChar.equals("\r")) {
                        toAdd = new Text("\n");
                    }
                    else {
                        toAdd = new Text(newChar);

                    }
                    letters.add(toAdd);
                    currentLine = TextBuffer.render(letters, newLines, WINDOW_WIDTH, WINDOW_HEIGHT, textPositionX, textPositionY, fontSize, cursor, scrollBar);
                    textRoot.getChildren().add(toAdd);
                    keyEvent.consume();
                }
            } else if (keyEvent.getEventType() == KeyEvent.KEY_PRESSED) {
                KeyCode code = keyEvent.getCode();
                if (keyEvent.isShortcutDown()) {
                    if (code == KeyCode.PLUS || code == KeyCode.EQUALS) {
                        fontSize += 4;
                        currentLine = TextBuffer.render(letters, newLines, WINDOW_WIDTH, WINDOW_HEIGHT, textPositionX, textPositionY, fontSize, cursor, scrollBar);
                        keyEvent.consume();
                    } else if (code == KeyCode.MINUS) {
                        if (fontSize <= 4) {
                            keyEvent.consume();
                        } else {
                            fontSize -= 4;
                            currentLine = TextBuffer.render(letters, newLines, WINDOW_WIDTH, WINDOW_HEIGHT, textPositionX, textPositionY, fontSize, cursor, scrollBar);
                            keyEvent.consume();
                        }
                    } else if (code == KeyCode.P) {
                        System.out.println(Math.round(cursor.getX()) + ", " + Math.round(cursor.getY()));
                        keyEvent.consume();
                    } else if (code == KeyCode.S) {
                        try {
                            if (!fileName.substring(fileName.length() - 4, fileName.length()).equals(".txt")) {
                                System.out.println("NOT A VALID TEXT FILE NAME!!!");
                                System.exit(1);
                            } else {
                                File saveFile = new File(fileName);
                                FileWriter saveWriter = new FileWriter(saveFile);
                                for (DblLinkedList.Node curr : letters) {
                                    saveWriter.write(curr.content.getText());
                                }
                                System.out.println("Successfully saved this file!");
                                saveWriter.close();
                            }
                        } catch (IOException ioException) {
                            System.exit(1);
                        }
                    }
                }
                if (code == KeyCode.BACK_SPACE) {
                    if (letters.isEmpty()) {
                        keyEvent.consume();
                    } else {
                        toRemove = letters.delete();
                        textPositionX -= toRemove.getLayoutBounds().getWidth();
                        currentLine = TextBuffer.render(letters, newLines, WINDOW_WIDTH, WINDOW_HEIGHT, textPositionX, textPositionY, fontSize, cursor, scrollBar);
                        textRoot.getChildren().remove(toRemove);
                        keyEvent.consume();
                    }
                } else if (code == KeyCode.LEFT) {
                    if (letters.currentNode.equals(letters.frontSentinel)) {
                        keyEvent.consume();
                    } else {
                        letters.currentNode = letters.currentNode.prev;
                        letters.currentPos -= 1;
                        currentLine = TextBuffer.render(letters, newLines, WINDOW_WIDTH, WINDOW_HEIGHT, textPositionX, textPositionY, fontSize, cursor, scrollBar);
                        keyEvent.consume();
                    }
                } else if (code == KeyCode.RIGHT) {
                    if (letters.currentNode.next == letters.frontSentinel) {
                        keyEvent.consume();
                    } else {
                        letters.currentNode = letters.currentNode.next;
                        letters.currentPos += 1;
                        currentLine = TextBuffer.render(letters, newLines, WINDOW_WIDTH, WINDOW_HEIGHT, textPositionX, textPositionY, fontSize, cursor, scrollBar);
                        keyEvent.consume();
                    }
                } else if (code == KeyCode.UP) {
                    if (currentLine <= 0 && (!TextBuffer.isNewLine(letters.currentNode.content))) {
                        keyEvent.consume();
                    } else if ((TextBuffer.isNewLine(letters.currentNode.content)
                                    && (TextBuffer.isNewLine(letters.currentNode.prev.content)))) {
                        letters.currentNode = letters.currentNode.prev;
                        currentLine = TextBuffer.render(letters, newLines, WINDOW_WIDTH, WINDOW_HEIGHT, textPositionX, textPositionY, fontSize, cursor, scrollBar);
                        keyEvent.consume();
                    } else {
                        if (TextBuffer.isNewLine(letters.currentNode.content)) {
                            currentLine += 1;
                        }
                        currentLine -= 1;
                        int xPos = (int) Math.round(cursor.getX());
                        DblLinkedList.Node firstCharOfLine = newLines.get(currentLine);
                        DblLinkedList.Node firstCharOfNextLine;
                        if (currentLine == newLines.lastLine() - 1) {
                            if (TextBuffer.isNewLine(letters.currentNode.content)) {
                                firstCharOfNextLine = letters.currentNode;
                            } else {
                                firstCharOfNextLine = newLines.get(currentLine);
                            }
                        } else if (TextBuffer.isNewLine(newLines.get(currentLine + 1).prev.content)) {
                            firstCharOfNextLine = newLines.get(currentLine + 1).prev;
                        } else {
                            firstCharOfNextLine = newLines.get(currentLine + 1);
                        }
                        firstCharOfNextLine = letters.closestNode(firstCharOfLine, firstCharOfNextLine, xPos);
                        if (newLines.lineLength(currentLine) <= xPos) {
                            if (TextBuffer.isNewLine(firstCharOfLine.prev.content) ||
                                    (TextBuffer.isWhiteSpace(firstCharOfLine.prev.content))) {
                                letters.currentNode = firstCharOfLine.prev.prev;
                            } else {
                                letters.currentNode = firstCharOfLine.prev;
                            }
                        }
                        currentLine = TextBuffer.render(letters, newLines, WINDOW_WIDTH, WINDOW_HEIGHT, textPositionX, textPositionY, fontSize, cursor, scrollBar);
                    }
                } else if (code == KeyCode.DOWN) {
                    if ((currentLine == newLines.lastLine() - 1) && (!TextBuffer.isWhiteSpace(letters.currentNode.content))
                            && (!TextBuffer.isNewLine(letters.frontSentinel.prev.content))) {
                        keyEvent.consume();
                    } else if ((TextBuffer.isNewLine(letters.currentNode.content))
                                    && (TextBuffer.isNewLine(letters.currentNode.next.content))) {
                        letters.currentNode = letters.currentNode.next;
                        currentLine = TextBuffer.render(letters, newLines, WINDOW_WIDTH, WINDOW_HEIGHT, textPositionX, textPositionY, fontSize, cursor, scrollBar);
                    } else if (TextBuffer.isNewLine(letters.frontSentinel.prev.content)
                            && (currentLine == newLines.lastLine() - 1)) {
                        letters.currentNode = letters.frontSentinel.prev;
                        currentLine = TextBuffer.render(letters, newLines, WINDOW_WIDTH, WINDOW_HEIGHT, textPositionX, textPositionY, fontSize, cursor, scrollBar);
                    } else if ((TextBuffer.isNewLine(letters.currentNode.content))
                            && (TextBuffer.isNewLine(letters.frontSentinel.prev.content)
                            && (currentLine + 1 == newLines.lastLine() - 1))) {
                        letters.currentNode = letters.frontSentinel.prev;
                        currentLine = TextBuffer.render(letters, newLines, WINDOW_WIDTH, WINDOW_HEIGHT, textPositionX, textPositionY, fontSize, cursor, scrollBar);
                    } else {
                        if (TextBuffer.isNewLine(letters.currentNode.content)) {
                            if (currentLine + 2 == newLines.lastLine()) {
                                currentLine += 1;
                            } else {
                                currentLine += 2;
                            }
                        } else {
                            currentLine += 1;
                        }
                        int xPos = (int) Math.round(cursor.getX());
                        DblLinkedList.Node firstCharOfLine = newLines.get(currentLine);
                        DblLinkedList.Node firstCharOfNextLine;
                        /* Don't need to worry about this case in KeyCode.UP because
                         * the next line will never be empty as that was where
                         * our original cursor was. */
                        if (currentLine == newLines.lastLine() - 1) {
                            firstCharOfNextLine = letters.frontSentinel;
                        } else if (TextBuffer.isNewLine(newLines.get(currentLine + 1).prev.content)) {
                            firstCharOfNextLine = newLines.get(currentLine + 1).prev;
                        } else {
                            firstCharOfNextLine = newLines.get(currentLine + 1);
                        }
                        firstCharOfNextLine = letters.closestNode(firstCharOfLine, firstCharOfNextLine, xPos);
                        if (newLines.lineLength(currentLine) <= xPos) {
                            if (TextBuffer.isWhiteSpace(firstCharOfNextLine.prev.content)) {
                                letters.currentNode = firstCharOfNextLine.prev.prev;
                            } else {
                                letters.currentNode = firstCharOfNextLine.prev;
                            }
                        }
                        currentLine = TextBuffer.render(letters, newLines, WINDOW_WIDTH, WINDOW_HEIGHT, textPositionX, textPositionY, fontSize, cursor, scrollBar);
                    }
                }

            }
        }
    }


    private class MouseClickedEventHandler implements EventHandler<MouseEvent> {

        /** A Text object that will be used to print the current mouse position. */
        Text positionText;
        Stage windowStage;

        MouseClickedEventHandler(Group root, Stage stage) {
            // For now, since there's no mouse position yet, just create an empty Text object.
            positionText = new Text("");
            // We want the text to show up immediately above the position, so set the origin to be
            // VPos.BOTTOM (so the x-position we assign will be the position of the bottom of the
            // text).
            positionText.setTextOrigin(VPos.BOTTOM);
            windowStage = stage;
            // Add the positionText to root, so that it will be displayed on the screen.
            root.getChildren().add(positionText);
        }


        @Override
        public void handle(MouseEvent mouseEvent) {
            double mousePressedX = mouseEvent.getX();
            double mousePressedY = mouseEvent.getY();

            if (mousePressedX >= scrollBar.getLayoutX() && mousePressedX < WINDOW_WIDTH) {
                scrollBar.valueProperty().addListener(new ChangeListener<Number>() {
                    @Override
                    public void changed(
                            ObservableValue<? extends Number>  observableValue,
                            Number oldValue,
                            Number newValue) {
                        if (newValue != oldValue) {
                            textRoot.setLayoutY(newValue.intValue() - oldValue.intValue());
                        }
                    }
                });
            } else {
                double lineHeight = letters.frontSentinel.next.content.getLayoutBounds().getHeight();
                double maximumHeight = Math.round(newLines.getLast().content.getY()
                        + newLines.getLast().content.getLayoutBounds().getHeight());
                int newCurrentLine = (int) Math.floor(mousePressedY / lineHeight);
                DblLinkedList.Node firstCharOfLine;
                DblLinkedList.Node firstCharOfNextLine;

                if ((mousePressedY < letters.frontSentinel.content.getY()) ||
                        (mousePressedX < letters.frontSentinel.content.getX() && newCurrentLine == 0)) {
                    letters.currentNode = letters.frontSentinel;
                } else if (mousePressedY > maximumHeight) {
                    letters.currentNode = letters.frontSentinel.prev;
                } else {
                    if (newCurrentLine == 0 && newCurrentLine == newLines.lastLine() - 1) {
                        firstCharOfLine = newLines.get(newCurrentLine).next;
                    } else {
                        firstCharOfLine = newLines.get(newCurrentLine);
                    }
                    if (newCurrentLine == newLines.lastLine() - 1) {
                        firstCharOfNextLine = letters.frontSentinel;
                    } else {
                        firstCharOfNextLine = newLines.get(newCurrentLine + 1);
                    }
                    // Should still work even when clicking on first and only line
                    firstCharOfLine = letters.closestNode(firstCharOfLine, firstCharOfNextLine, mousePressedX);
                    if (newLines.lineLength(newCurrentLine) <= mousePressedX) {
                        if (TextBuffer.isNewLine(firstCharOfLine.prev.content) ||
                                (TextBuffer.isWhiteSpace(firstCharOfLine.prev.content))) {
                            letters.currentNode = firstCharOfLine.prev.prev;
                        } else {
                            letters.currentNode = firstCharOfLine.prev;
                        }
                    }
                }
            }
            currentLine = TextBuffer.render(letters, newLines, WINDOW_WIDTH, WINDOW_HEIGHT, textPositionX, textPositionY, fontSize, cursor, scrollBar);
        }
    }

    private class MouseDraggedEventHandler implements EventHandler<MouseEvent> {

        Stage windowStage;
        MouseDraggedEventHandler(Group root, Stage stage) {
            windowStage = stage;
        }


        @Override
        public void handle(MouseEvent mouseEvent) {
            double mousePressedX = mouseEvent.getX();
            double mousePressedY = mouseEvent.getY();
            if (mousePressedX >= scrollBar.getLayoutX() && mousePressedX < WINDOW_WIDTH) {
                scrollBar.valueProperty().addListener(new ChangeListener<Number>() {
                    @Override
                    public void changed(
                            ObservableValue<? extends Number>  observableValue,
                            Number oldValue,
                            Number newValue) {
                        if (newValue != oldValue) {
                            textRoot.setLayoutY(newValue.intValue() - oldValue.intValue());
                        }
                    }
                });
            } else if ((mousePressedX == WINDOW_WIDTH) || (mousePressedY == WINDOW_HEIGHT)) {
                scene.widthProperty().addListener(new ChangeListener<Number>() {
                    @Override
                    public void changed(
                            ObservableValue<? extends Number> observableValue,
                            Number oldScreenWidth,
                            Number newScreenWidth) {
                        WINDOW_WIDTH = newScreenWidth.intValue();
                        System.out.print(WINDOW_WIDTH);
                        windowStage.setWidth(WINDOW_WIDTH);
                        scrollBar.setLayoutX(WINDOW_WIDTH - scrollBar.getLayoutBounds().getWidth());
                        currentLine = TextBuffer.render(letters, newLines, WINDOW_WIDTH, WINDOW_HEIGHT, textPositionX, textPositionY, fontSize, cursor, scrollBar);
                    }

                });

                scene.heightProperty().addListener(new ChangeListener<Number>() {
                    @Override
                    public void changed(
                           ObservableValue<? extends Number> observableValue,
                           Number oldScreenHeight,
                           Number newScreenHeight) {
                        WINDOW_HEIGHT = newScreenHeight.intValue();
                        System.out.print(WINDOW_HEIGHT);
                        windowStage.setHeight(WINDOW_HEIGHT);
                        scrollBar.setPrefHeight(WINDOW_HEIGHT);
                        currentLine = TextBuffer.render(letters, newLines, WINDOW_WIDTH, WINDOW_HEIGHT, textPositionX, textPositionY, fontSize, cursor, scrollBar);
                    }
                });
            }

        }

    }

    private class CursorBlinkEventHandler implements EventHandler<ActionEvent> {

        CursorBlinkEventHandler() {
            // Set the color to be the first color in the list.
            cursorBlink();
        }

        private void cursorBlink() {
            cursor.setVisible(!cursor.isVisible());
        }

        @Override
        public void handle(ActionEvent event) {
            cursorBlink();
        }
    }

    /** Makes the text bounding box change color periodically. */
    public void makeCursorBlink() {
        // Create a Timeline that will call the "handle" function of RectangleBlinkEventHandler
        // every 1 second.
        final Timeline timeline = new Timeline();
        // The rectangle should continue blinking forever.
        timeline.setCycleCount(Timeline.INDEFINITE);
        CursorBlinkEventHandler cursorChange = new CursorBlinkEventHandler();
        KeyFrame keyFrame = new KeyFrame(Duration.seconds(1), cursorChange);
        timeline.getKeyFrames().add(keyFrame);
        timeline.play();
    }


    @Override
    public void start(Stage primaryStage) {

        EventHandler<KeyEvent> keyEventHandler =
                new KeyEventHandler(root, WINDOW_WIDTH, WINDOW_HEIGHT);

        EventHandler<MouseEvent> mouseClickedEventHandler =
                new MouseClickedEventHandler(root, primaryStage);

        EventHandler<MouseEvent> mouseDraggedEventHandler =
                new MouseDraggedEventHandler(root, primaryStage);

        scene.setOnKeyTyped(keyEventHandler);
        scene.setOnKeyPressed(keyEventHandler);
        scene.setOnMouseClicked(mouseClickedEventHandler);
        // scene.setOnMousePressed(mouseDraggedEventHandler);
        scene.setOnMouseDragged(mouseDraggedEventHandler);

        root.getChildren().add(cursor);
        root.getChildren().add(scrollBar);
        makeCursorBlink();

        /*
        List<String> commandLineArgs = getParameters().getRaw();
        if (commandLineArgs.size() == 0) {
            System.out.println("No file inputted");
            System.exit(1);

        } else if (commandLineArgs.size() >= 1) {
            fileName = commandLineArgs.get(0);
            if (commandLineArgs.size() == 2) {
                if (commandLineArgs.get(1).equals("debug")) {
                    System.out.println("Debugging..");
                    System.out.println("Cursor position: (" + cursor.getX() + ", " + cursor.getY() + ")");
                }
            }
            try {
                File inputFile = new File(fileName);
                // Check to make sure that the input file exists!
                    FileReader reader = new FileReader(inputFile);
                    BufferedReader bufferedReader = new BufferedReader(reader);

                    int intRead = -1;
                    while ((intRead = bufferedReader.read()) != -1) {
                        char charRead = (char) intRead;
                        String currChar = Character.toString(charRead);
                        Text currentChar = new Text(currChar);
                        letters.add(currentChar);
                        TextBuffer.render(letters, newLines, WINDOW_WIDTH, WINDOW_HEIGHT, textPositionX, textPositionY, fontSize, cursor, scrollBar);
                        textRoot.getChildren().add(currentChar);
                    }
                    bufferedReader.close();
                    cursor.setX(STARTING_TEXT_POSITION_X);
                    cursor.setY(0);
                    letters.currentNode = letters.frontSentinel;

            } catch (FileNotFoundException fileNotFoundException) {
                System.out.println("File not found! " + fileName + " is a directory.");
                System.exit(1);
            } catch (IOException ioException) {
                System.out.println("I honestly don't know what this error is for..");
            }
        }

        */

        primaryStage.setTitle(fileName);

        // This is boilerplate, necessary to setup the window where things are displayed.
        primaryStage.setScene(scene);
        primaryStage.sizeToScene();
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch("proj2.txt");
    }
}
