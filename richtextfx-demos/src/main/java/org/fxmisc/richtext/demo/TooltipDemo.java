package org.fxmisc.richtext.demo;

import java.time.Duration;
import java.util.Collection;

import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Popup;
import javafx.stage.Stage;

import org.fxmisc.richtext.MouseOverTextEvent;
import org.fxmisc.richtext.StyleClassedTextArea;
import org.fxmisc.richtext.model.SegmentOps;

public class TooltipDemo extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        StyleClassedTextArea area = new StyleClassedTextArea(new SegmentOps<String, Collection<String>>() {
            @Override
            public int length(String s) {
                return 0;
            }

            @Override
            public char charAt(String s, int index) {
                return 0;
            }

            @Override
            public String getText(String s) {
                return null;
            }

            @Override
            public String subSequence(String s, int start, int end) {
                return null;
            }

            @Override
            public String subSequence(String s, int start) {
                return null;
            }

            @Override
            public String append(String s, String str) {
                return null;
            }

            @Override
            public String spliced(String s, int from, int to, CharSequence replacement) {
                return null;
            }

            @Override
            public Collection<String> getStyle(String s) {
                return null;
            }

            @Override
            public String create(String text, Collection<String> style) {
                return null;
            }

            @Override
            public String toString(String s) {
                return null;
            }
        });
        area.setWrapText(true);
        area.appendText("Pause the mouse over the text for 1 second.");

        Popup popup = new Popup();
        Label popupMsg = new Label();
        popupMsg.setStyle(
                "-fx-background-color: black;" +
                "-fx-text-fill: white;" +
                "-fx-padding: 5;");
        popup.getContent().add(popupMsg);

        area.setMouseOverTextDelay(Duration.ofSeconds(1));
        area.addEventHandler(MouseOverTextEvent.MOUSE_OVER_TEXT_BEGIN, e -> {
            int chIdx = e.getCharacterIndex();
            Point2D pos = e.getScreenPosition();
            popupMsg.setText("Character '" + area.getText(chIdx, chIdx+1) + "' at " + pos);
            popup.show(area, pos.getX(), pos.getY() + 10);
        });
        area.addEventHandler(MouseOverTextEvent.MOUSE_OVER_TEXT_END, e -> {
            popup.hide();
        });

        Scene scene = new Scene(area, 600, 400);
        stage.setScene(scene);
        stage.setTitle("Tooltip Demo");
        stage.show();
    }
}
