package org.fxmisc.richtext.style;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.fxmisc.flowless.Cell;
import org.fxmisc.flowless.VirtualFlow;
import org.fxmisc.richtext.InlineCssTextAreaAppTest;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.nitorcreations.junit.runners.NestedRunner;

import javafx.scene.Node;
import javafx.scene.layout.Region;
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
        return tf.getChildrenUnmodifiable().filtered(n -> n instanceof Text);
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

        // select the word "World" and set it to bold
        interact(() -> {
            area.selectRange(firstWord.length(), firstWord.length() + styledWord.length());
            area.setStyle(firstWord.length(), firstWord.length() + styledWord.length(), "-fx-font-weight: bold;");
        });

        // expected: three text nodes
        textNodes = getTextNodes();
        assertEquals(3, textNodes.size());

        Text first = (Text) textNodes.get(0);
        Text second = (Text) textNodes.get(1);
        Text third = (Text) textNodes.get(2);
        assertEquals("Hello ", first.getText());
        assertEquals("World", second.getText());
        assertEquals(" and Moon", third.getText());
    }


    @Test
    public void underlineStyling() {
        // setup
        interact(() -> {
            area.replaceText(firstWord + styledWord + moreText + styledWord2 + remainingLine);
        });

        // expected: one text node which contains the complete text
        List<Node> textNodes = getTextNodes();
        assertEquals(1, textNodes.size());

        // select the word "World" and set it to bold
        interact(() -> {
            //area.selectRange(firstWord.length(), firstWord.length() + styledWord.length());
            area.setStyle(firstWord.length(), firstWord.length() + styledWord.length(),
                          "-rtfx-underline-color: red; -rtfx-underline-dash-array: 2 2; -rtfx-underline-width: 1; -rtfx-underline-cap: butt;");

            // area.selectRange(26, 28); // firstWord.length(), firstWord.length() + styledWord.length());
            area.setStyle(26, 28, // firstWord.length(), firstWord.length() + styledWord.length(),
                          "-rtfx-underline-color: red; -rtfx-underline-dash-array: 2 2; -rtfx-underline-width: 1; -rtfx-underline-cap: butt;");
        });

        sleep(5000);
        // expected: three text nodes
        textNodes = getTextNodes();
        assertEquals(3, textNodes.size());

        Text first = (Text) textNodes.get(0);
        Text second = (Text) textNodes.get(1);
        Text third = (Text) textNodes.get(2);
        assertEquals("Hello ", first.getText());
        assertEquals("World", second.getText());
        assertEquals(" and Moon", third.getText());
    }

}
