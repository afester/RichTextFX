package org.fxmisc.richtext.style;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.fxmisc.flowless.Cell;
import org.fxmisc.flowless.VirtualFlow;
import org.fxmisc.richtext.InlineCssTextAreaAppTest;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.nitorcreations.junit.runners.NestedRunner;

import javafx.scene.Node;
import javafx.scene.layout.Region;
import javafx.scene.shape.Path;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

@RunWith(NestedRunner.class)
public class StylingTests extends InlineCssTextAreaAppTest {

    private final static String firstWord = "Hello ";
    private final static String styledWord = "World";
    private final static String moreText = " and also the ";
    private final static String styledWord2 = "Sun";
    private final static String remainingLine = " and Moon";

    /**
     * @return A list of text nodes which render the current text.
     */
    private List<Node> getTextNodes() {
        VirtualFlow flow = (VirtualFlow) area.getChildrenUnmodifiable().get(0);
        Cell gsa = (Cell) flow.getCell(0);
        Region pgb = (Region) gsa.getNode();
        TextFlow tf = (TextFlow) pgb.getChildrenUnmodifiable().get(0);
        
        // get the ParagraphText (protected subclass of TextFlow) 
        return tf.getChildrenUnmodifiable().filtered(n -> n instanceof Text);
    }

    /**
     * @return A list of nodes which render the underlines for the current text.
     */
    private List<Path> getUnderlinePaths() {
        VirtualFlow flow = (VirtualFlow) area.getChildrenUnmodifiable().get(0);
        Cell gsa = (Cell) flow.getCell(0);
        Region pgb = (Region) gsa.getNode();
        
        // get the ParagraphText (protected subclass of TextFlow) 
        TextFlow tf = (TextFlow) pgb.getChildrenUnmodifiable().get(0);

        List<Path> result = new ArrayList<>();
        tf.getChildrenUnmodifiable().filtered(n -> n instanceof Path).forEach(n -> result.add((Path) n));
        return result.subList(2, result.size());
    }


    @Test
    public void simpleStyling() {
        // setup
        interact(() -> {
            area.replaceText(firstWord + styledWord + remainingLine);
        });

        // expected: one text node which contains the complete text
        List<Node> textNodes = getTextNodes();
        assertEquals(1, textNodes.size());

        // Set word "World" to bold
        interact(() -> {
            area.setStyle(firstWord.length(), firstWord.length() + styledWord.length(), "-fx-font-weight: bold;");
        });

        // expected: three text nodes
        textNodes = getTextNodes();
        assertEquals(3, textNodes.size());

        Text first = (Text) textNodes.get(0);
        assertEquals("Hello ", first.getText());
        assertEquals("Regular", first.getFont().getStyle());

        Text second = (Text) textNodes.get(1);
        assertEquals("World", second.getText());
        assertEquals("Bold", second.getFont().getStyle());

        Text third = (Text) textNodes.get(2);
        assertEquals(" and Moon", third.getText());
        assertEquals("Regular", third.getFont().getStyle());
    }


    @Test
    public void underlineStyling() {
        // setup
        interact(() -> {
            area.replaceText(firstWord + styledWord + moreText + styledWord2 + remainingLine);
            //               "Hello World and also the Sun and Moon"
        });

        // expected: one text node which contains the complete text
        List<Node> textNodes = getTextNodes();
        assertEquals(1, textNodes.size());

        interact(() -> {
            area.setStyle(firstWord.length(), firstWord.length() + styledWord.length(),
                          "-rtfx-underline-color: red; -rtfx-underline-dash-array: 2 2; -rtfx-underline-width: 1; -rtfx-underline-cap: butt;");

            area.setStyle(25, 28, // firstWord.length(), firstWord.length() + styledWord.length(),
                          "-rtfx-underline-color: red; -rtfx-underline-dash-array: 2 2; -rtfx-underline-width: 1; -rtfx-underline-cap: butt;");

            //               "Hello World and also the Sun and Moon"
            //                      ^^^^^              ^^^
        });

        // expected: five text nodes
        textNodes = getTextNodes();
        assertEquals(5, textNodes.size());

        Text first = (Text) textNodes.get(0);
        assertEquals("Hello ", first.getText());

        Text second = (Text) textNodes.get(1);
        assertEquals("World", second.getText());

        Text third = (Text) textNodes.get(2);
        assertEquals(" and also the ", third.getText());

        Text fourth = (Text) textNodes.get(3);
        assertEquals("Sun", fourth.getText());

        Text fifth = (Text) textNodes.get(4);
        assertEquals(" and Moon", fifth.getText());

        // determine the underline paths
        List<Path> underlineNodes = getUnderlinePaths();
        assertEquals(2, underlineNodes.size());

        underlineNodes.forEach(e -> System.err.println(e));
    }

}
