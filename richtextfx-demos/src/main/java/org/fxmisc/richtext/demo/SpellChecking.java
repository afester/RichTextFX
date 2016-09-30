package org.fxmisc.richtext.demo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.BreakIterator;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.StyleClassedTextArea;
import org.fxmisc.richtext.model.SegmentOps;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class SpellChecking extends Application {

    private static final Set<String> dictionary = new HashSet<String>();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        StyleClassedTextArea textArea = new StyleClassedTextArea(new SegmentOps<String, Collection<String>>() {
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
        textArea.setWrapText(true);

        textArea.richChanges()
                .filter(ch -> !ch.getInserted().equals(ch.getRemoved())) // XXX
                .subscribe(change -> {
                    textArea.setStyleSpans(0, computeHighlighting(textArea.getText()));
                });

        // load the dictionary
        try (InputStream input = getClass().getResourceAsStream("spellchecking.dict");
             BufferedReader br = new BufferedReader(new InputStreamReader(input))) {
            String line;
            while ((line = br.readLine()) != null) {
                dictionary.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // load the sample document
        InputStream input2 = getClass().getResourceAsStream("spellchecking.txt");
        try(java.util.Scanner s = new java.util.Scanner(input2)) { 
            String document = s.useDelimiter("\\A").hasNext() ? s.next() : "";
            textArea.replaceText(0, 0, document);
        }

        Scene scene = new Scene(new StackPane(new VirtualizedScrollPane<>(textArea)), 600, 400);
        scene.getStylesheets().add(getClass().getResource("spellchecking.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setTitle("Spell Checking Demo");
        primaryStage.show();
    }


    private static StyleSpans<Collection<String>> computeHighlighting(String text) {
        
        StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();
        
        BreakIterator wb = BreakIterator.getWordInstance();
        wb.setText(text);

        int lastIndex = wb.first();
        int lastKwEnd = 0;
        while(lastIndex != BreakIterator.DONE) {
            int firstIndex = lastIndex;
            lastIndex = wb.next();

            if (lastIndex != BreakIterator.DONE
                && Character.isLetterOrDigit(text.charAt(firstIndex))) {
                String word = text.substring(firstIndex, lastIndex).toLowerCase();
                if (!dictionary.contains(word)) {
                    spansBuilder.add(Collections.emptyList(), firstIndex - lastKwEnd);
                    spansBuilder.add(Collections.singleton("underlined"), lastIndex - firstIndex);
                    lastKwEnd = lastIndex;
                }
                System.err.println();
            }
        }
        spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);

        return spansBuilder.create();
    }
}
