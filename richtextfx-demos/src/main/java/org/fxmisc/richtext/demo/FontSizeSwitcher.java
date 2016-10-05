package org.fxmisc.richtext.demo;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.StyleClassedTextArea;
import org.fxmisc.richtext.model.SegmentOps;

import java.util.Collection;

public class FontSizeSwitcher extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
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
        for(int i = 0; i < 10; ++i) {
            area.appendText("Lorem ipsum dolor sit amet, consectetur adipiscing elit.\n");
        }

        HBox panel = new HBox();
        for(int size: new int[]{8, 10, 12, 14, 16, 18, 20, 24, 28, 32, 36, 40, 48, 56, 64, 72}) {
            Button b = new Button(Integer.toString(size));
            b.setOnAction(ae -> area.setStyle("-fx-font-size:" + size));
            panel.getChildren().add(b);
        }

        VBox vbox = new VBox();
        VirtualizedScrollPane<StyleClassedTextArea> vsPane = new VirtualizedScrollPane<>(area);
        VBox.setVgrow(vsPane, Priority.ALWAYS);
        vbox.getChildren().addAll(panel, vsPane);

        Scene scene = new Scene(vbox, 600, 400);
        primaryStage.setScene(scene);
        area.requestFocus();
        primaryStage.setTitle("Font Size Switching Test");
        primaryStage.show();
    }
}
