package org.fxmisc.richtext.style;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
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
    private final static String styledWord1 = "World";
    private final static String moreText = " and also the ";
    private final static String styledWord2 = "Sun";
    private final static String remainingLine = " and Moon";

    
    /**
     * 
     * @param index The index of the desired paragraph box
     * @return The paragraph box for the paragraph at the specified index
     */
    private Region getParagraphBox(int index) {
        @SuppressWarnings("unchecked")
        VirtualFlow<String, Cell<String, Node>> flow = (VirtualFlow<String, Cell<String, Node>>) area.getChildrenUnmodifiable().get(index);
        Cell<String, Node> gsa = flow.getCell(0);

        // get the ParagraphBox (protected subclass of Region) 
        return (Region) gsa.getNode();
    }
    

    /**
     * @return A list of text nodes which render the current text.
     */
    private List<Text> getTextNodes() {
        // get the ParagraphBox (protected subclass of Region) 
        Region paragraphBox = getParagraphBox(0);

        // get the ParagraphText (protected subclass of TextFlow) 
        TextFlow tf = (TextFlow) paragraphBox.getChildrenUnmodifiable().get(0);
        
        List<Text> result = new ArrayList<>();
        tf.getChildrenUnmodifiable().filtered(n -> n instanceof Text).forEach(n -> result.add((Text) n));
        return result;
    }

    /**
     * @return A list of nodes which render the underlines for the current text.
     */
    private List<Path> getUnderlinePaths() {
        // get the ParagraphBox (protected subclass of Region) 
        Region paragraphBox = getParagraphBox(0);
        
        // get the ParagraphText (protected subclass of TextFlow) 
        TextFlow tf = (TextFlow) paragraphBox.getChildrenUnmodifiable().get(0);

        List<Path> result = new ArrayList<>();
        tf.getChildrenUnmodifiable().filtered(n -> n instanceof Path).forEach(n -> result.add((Path) n));
        return result.subList(2, result.size());
    }


    @Test
    public void simpleStyling() {
        // setup
        interact(() -> {
            area.replaceText(firstWord + styledWord1 + remainingLine);
        });

        // expected: one text node which contains the complete text
        List<Text> textNodes = getTextNodes();
        assertEquals(1, textNodes.size());

        // Set word "World" to bold
        interact(() -> {
            area.setStyle(firstWord.length(), firstWord.length() + styledWord1.length(), "-fx-font-weight: bold;");
        });

        // expected: three text nodes
        textNodes = getTextNodes();
        assertEquals(3, textNodes.size());

        Text first = textNodes.get(0);
        assertEquals("Hello ", first.getText());
        assertEquals("Regular", first.getFont().getStyle());

        Text second = textNodes.get(1);
        assertEquals("World", second.getText());
        assertEquals("Bold", second.getFont().getStyle());

        Text third = textNodes.get(2);
        assertEquals(" and Moon", third.getText());
        assertEquals("Regular", third.getFont().getStyle());
    }


    @Test
    public void underlineStyling() {
        // setup
        interact(() -> {
            area.replaceText(firstWord + styledWord1 + moreText + styledWord2 + remainingLine);
            //               "Hello World and also the Sun and Moon"
        });

        // expected: one text node which contains the complete text
        List<Text> textNodes = getTextNodes();
        assertEquals(1, textNodes.size());
        assertEquals(firstWord + styledWord1 + moreText + styledWord2 + remainingLine, 
                     textNodes.get(0).getText());

        interact(() -> {
            final int start1 = firstWord.length();
            final int end1 = start1 + styledWord1.length();
            area.setStyle(start1, end1,
                          "-rtfx-underline-color: red; -rtfx-underline-dash-array: 2 2; -rtfx-underline-width: 1; -rtfx-underline-cap: butt;");

            final int start2 = end1 + moreText.length();
            final int end2 = start2 + styledWord2.length();
            area.setStyle(start2, end2,
                          "-rtfx-underline-color: red; -rtfx-underline-dash-array: 2 2; -rtfx-underline-width: 1; -rtfx-underline-cap: butt;");

            //               "Hello World and also the Sun and Moon"
            //                      -----              ---
        });

        // expected: five text nodes
        textNodes = getTextNodes();
        assertEquals(5, textNodes.size());

        Text first = textNodes.get(0);
        assertEquals(firstWord, first.getText());
        Text second = textNodes.get(1);
        assertEquals(styledWord1, second.getText());
        Text third = textNodes.get(2);
        assertEquals(moreText, third.getText());
        Text fourth = textNodes.get(3);
        assertEquals(styledWord2, fourth.getText());
        Text fifth = textNodes.get(4);
        assertEquals(remainingLine, fifth.getText());

        // determine the underline paths - need to be two of them!
        List<Path> underlineNodes = getUnderlinePaths();
        assertEquals(2, underlineNodes.size());
    }
}
