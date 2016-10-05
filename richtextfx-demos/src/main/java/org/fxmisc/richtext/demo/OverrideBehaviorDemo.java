package org.fxmisc.richtext.demo;

import static javafx.scene.input.KeyCode.*;
import static javafx.scene.input.KeyCombination.*;
import static org.fxmisc.wellbehaved.event.EventPattern.*;

import javafx.application.Application;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import org.fxmisc.richtext.InlineCssTextArea;
import org.fxmisc.richtext.model.SegmentOps;
import org.fxmisc.wellbehaved.event.InputMap;
import org.fxmisc.wellbehaved.event.Nodes;
import org.omg.CORBA.Object;

/**
 * This demo shows how to override the default behavior of a StyledTextArea via an InputMap.
 * It also demonstrates the bugs that can arise if one adds a handler to one of the
 * {@code on[EventType]Property}.
 */
public class OverrideBehaviorDemo extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        InlineCssTextArea area = new InlineCssTextArea(new SegmentOps<Object, String>() {
            @Override
            public int length(Object object) {
                return 0;
            }

            @Override
            public char charAt(Object object, int index) {
                return 0;
            }

            @Override
            public String getText(Object object) {
                return null;
            }

            @Override
            public Object subSequence(Object object, int start, int end) {
                return null;
            }

            @Override
            public Object subSequence(Object object, int start) {
                return null;
            }

            @Override
            public Object append(Object object, String str) {
                return null;
            }

            @Override
            public Object spliced(Object object, int from, int to, CharSequence replacement) {
                return null;
            }

            @Override
            public String getStyle(Object object) {
                return null;
            }

            @Override
            public Object create(String text, String style) {
                return null;
            }

            @Override
            public String toString(Object object) {
                return null;
            }
        });

        InputMap<Event> preventSelectionOrRightArrowNavigation = InputMap.consume(
                anyOf(
                        // prevent selection via (CTRL + ) SHIFT + [LEFT, UP, DOWN]
                        keyPressed(LEFT,    SHIFT_DOWN, SHORTCUT_ANY),
                        keyPressed(KP_LEFT, SHIFT_DOWN, SHORTCUT_ANY),
                        keyPressed(UP,    SHIFT_DOWN, SHORTCUT_ANY),
                        keyPressed(KP_UP, SHIFT_DOWN, SHORTCUT_ANY),
                        keyPressed(DOWN,    SHIFT_DOWN, SHORTCUT_ANY),
                        keyPressed(KP_DOWN, SHIFT_DOWN, SHORTCUT_ANY),

                        // prevent selection via mouse events
                        eventType(MouseEvent.MOUSE_DRAGGED),
                        eventType(MouseEvent.DRAG_DETECTED),
                        mousePressed().unless(e -> e.getClickCount() == 1 && !e.isShiftDown()),

                        // prevent any right arrow movement, regardless of modifiers
                        keyPressed(RIGHT,     SHORTCUT_ANY, SHIFT_ANY),
                        keyPressed(KP_RIGHT,  SHORTCUT_ANY, SHIFT_ANY)
                )
        );
        Nodes.addInputMap(area, preventSelectionOrRightArrowNavigation);

        area.replaceText(String.join("\n",
                "You can't move the caret to the right via the RIGHT arrow key in this area.",
                "Additionally, you cannot select anything either",
                "",
                ":-p"
        ));
        area.moveTo(0);

        CheckBox addExtraEnterHandlerCheckBox = new CheckBox("Temporarily add an EventHandler to area's `onKeyPressedProperty`?");
        addExtraEnterHandlerCheckBox.setStyle("-fx-font-weight: bold;");

        Label checkBoxExplanation = new Label(String.join("\n",
                "The added handler will insert a newline character at the caret's position when [Enter] is pressed.",
                "If checked, the default behavior and added handler will both occur: ",
                "\tthus, two newline characters should be inserted when user presses [Enter].",
                "When unchecked, the handler will be removed."
        ));
        checkBoxExplanation.setWrapText(true);

        EventHandler<KeyEvent> insertNewlineChar = e -> {
            if (e.getCode().equals(ENTER)) {
                area.insertText(area.getCaretPosition(), "\n");
                e.consume();
            }
        };
        addExtraEnterHandlerCheckBox.selectedProperty().addListener(
                (obs, ov, isSelected) -> area.setOnKeyPressed( isSelected ? insertNewlineChar : null)
        );

        VBox vbox = new VBox(area, addExtraEnterHandlerCheckBox, checkBoxExplanation);
        vbox.setSpacing(10);
        vbox.setPadding(new Insets(10));

        primaryStage.setScene(new Scene(vbox, 700, 350));
        primaryStage.show();
        primaryStage.setTitle("An area whose behavior has been overridden permanently and temporarily!");
    }

}
