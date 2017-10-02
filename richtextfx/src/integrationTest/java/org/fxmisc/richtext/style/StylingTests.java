package org.fxmisc.richtext.style;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.fxmisc.flowless.Cell;
import org.fxmisc.flowless.VirtualFlow;
import org.fxmisc.richtext.InlineCssTextAreaAppTest;
import org.junit.Test;

import javafx.scene.Node;
import javafx.scene.layout.Region;
import javafx.scene.shape.Path;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;


abstract class RenderingTests extends InlineCssTextAreaAppTest {

    /**
     * 
     * @param index The index of the desired paragraph box
     * @return The paragraph box for the paragraph at the specified index
     */
    protected Region getParagraphBox(int index) {
        @SuppressWarnings("unchecked")
        VirtualFlow<String, Cell<String, Node>> flow = (VirtualFlow<String, Cell<String, Node>>) area.getChildrenUnmodifiable().get(index);
        Cell<String, Node> gsa = flow.getCell(0);

        // get the ParagraphBox (protected subclass of Region) 
        return (Region) gsa.getNode();
    }
    

    /**
     * @return A list of text nodes which render the current text.
     */
    protected List<Text> getTextNodes() {
        // get the ParagraphBox (protected subclass of Region) 
        Region paragraphBox = getParagraphBox(0);

        // get the ParagraphText (protected subclass of TextFlow)
        TextFlow tf = (TextFlow) paragraphBox.getChildrenUnmodifiable().stream().filter(n -> n instanceof TextFlow)
                                 .findFirst().orElse(null);
        assertNotNull("No TextFlow node found in rich text area", tf);

        List<Text> result = new ArrayList<>();
        tf.getChildrenUnmodifiable().filtered(n -> n instanceof Text).forEach(n -> result.add((Text) n));
        return result;
    }

    /**
     * @return A list of nodes which render the underlines for the current text.
     */
    protected List<Path> getUnderlinePaths() {
        // get the ParagraphBox (protected subclass of Region) 
        Region paragraphBox = getParagraphBox(0);
        
        // get the ParagraphText (protected subclass of TextFlow) 
        TextFlow tf = (TextFlow) paragraphBox.getChildrenUnmodifiable().get(0);

        List<Path> result = new ArrayList<>();
        tf.getChildrenUnmodifiable().filtered(n -> n instanceof Path).forEach(n -> result.add((Path) n));
        
        result.forEach(n -> System.err.println(n.getClass()));
        
        return result.subList(2, result.size());
    }
}


public class StylingTests extends RenderingTests {

    private final static String HELLO = "Hello ";
    private final static String WORLD = "World";
    private final static String AND_ALSO_THE = " and also the ";
    private final static String SUN = "Sun";
    private final static String AND_MOON = " and Moon";

    @Test
    public void simpleStyling() {
        // setup
        interact(() -> {
            area.replaceText(HELLO + WORLD + AND_MOON);
        });

        // expected: one text node which contains the complete text
        List<Text> textNodes = getTextNodes();
        assertEquals(1, textNodes.size());

        interact(() -> {
            area.setStyle(HELLO.length(), HELLO.length() + WORLD.length(), "-fx-font-weight: bold;");
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

        final String underlineStyle = "-rtfx-underline-color: red; -rtfx-underline-dash-array: 2 2; -rtfx-underline-width: 1; -rtfx-underline-cap: butt;";
        
        // setup
        interact(() -> {
            area.replaceText(HELLO + WORLD + AND_ALSO_THE + SUN + AND_MOON);
        });

        // expected: one text node which contains the complete text
        List<Text> textNodes = getTextNodes();
        assertEquals(1, textNodes.size());
        assertEquals(HELLO + WORLD + AND_ALSO_THE + SUN + AND_MOON, 
                     textNodes.get(0).getText());

        interact(() -> {
            final int start1 = HELLO.length();
            final int end1 = start1 + WORLD.length();
            area.setStyle(start1, end1, underlineStyle);

            final int start2 = end1 + AND_ALSO_THE.length();
            final int end2 = start2 + SUN.length();
            area.setStyle(start2, end2, underlineStyle);
        });

        // expected: five text nodes
        textNodes = getTextNodes();
        assertEquals(5, textNodes.size());

        Text first = textNodes.get(0);
        assertEquals(HELLO, first.getText());
        Text second = textNodes.get(1);
        assertEquals(WORLD, second.getText());
        Text third = textNodes.get(2);
        assertEquals(AND_ALSO_THE, third.getText());
        Text fourth = textNodes.get(3);
        assertEquals(SUN, fourth.getText());
        Text fifth = textNodes.get(4);
        assertEquals(AND_MOON, fifth.getText());

        // determine the underline paths - need to be two of them!
        List<Path> underlineNodes = getUnderlinePaths();
        assertEquals(2, underlineNodes.size());
    }
}
