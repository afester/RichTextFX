package org.fxmisc.richtext.demo.richtext;

import java.util.ArrayList;
import java.util.List;

import org.fxmisc.richtext.GenericStyledArea;
import org.fxmisc.richtext.model.Paragraph;
import org.fxmisc.richtext.model.StyledText;
import org.reactfx.util.Either;

import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

public class WhiteSpaceOverlayFactory extends OverlayFactory {

    private Text createTextNode(WhiteSpaceType type, TextStyle style, int start, int end) {
        WhiteSpaceNode t = new WhiteSpaceNode(type); // Text(text);
        t.setTextOrigin(VPos.TOP);
        t.getStyleClass().add("text");
        t.setOpacity(0.7);
        //t.getStyleClass().addAll(style.toCss());
        t.setStyle(style.toCss());
        t.setUserData(new Range(start, end));
        return t;
    }


    // @Override
    public List<Node> createOverlayNodes(GenericStyledArea<ParStyle, Either<StyledText<TextStyle>, LinkedImage<TextStyle>>, TextStyle> area,
                                         int paragraphIndex) {
        List<Node> nodes = new ArrayList<>();

        Paragraph<ParStyle, Either<StyledText<TextStyle>, LinkedImage<TextStyle>>, TextStyle> paragraph = 
                area.getParagraph(paragraphIndex);
        int segStart = 0;

        for (Either<StyledText<TextStyle>, LinkedImage<TextStyle>> seg : paragraph.getSegments()) {
            if (seg.isLeft()) {

//Whitespace algorithm
                final String text = seg.getLeft().getText();
                final int textLength = text.length();
                for (int i = 0; i < textLength; i++) {
                    char ch = text.charAt(i);
                    if (ch != ' ' && ch != '\t')
                        continue;

                    Rectangle rect = new Rectangle(10, 10);
                    rect.setFill(null);
                    rect.setStroke(Color.RED);
                    rect.setUserData(new Range(segStart + i, segStart + i + 1));
                    nodes.add(createTextNode(
                            (ch == ' ') ? WhiteSpaceType.SPACE : WhiteSpaceType.TAB,
                                    seg.getLeft().getStyle(),segStart + i, segStart + i + 1)); 
                }
                segStart += textLength;
            }
        }

        // System.err.printf("%s / %s%n", para, area.getParagraphs().size());
        // TODO: does not work yet for newly created empty lines!
       // if (para < area.getParagraphs().size() - 1) {
            System.err.println("CREATING EOL NODE!");
            nodes.add(createTextNode(WhiteSpaceType.EOL,
                    paragraph.getStyleAtPosition(segStart),
                    segStart - 1, segStart));
        //}
/*
// Use case: border around each segment for debugging purposes.
// Issue: needs a Path since the shape might be non rectangular if the segment is wrapped.
// Means, we can not easily create the nodes here ....
// the WhiteSpace usecase is simpler, since it is one character only (but, might be beneficial to 
// combine more than one white space character into a single Text node)

        for (Either<StyledText<TextStyle>, LinkedImage<TextStyle>> seg : paragraph.getSegments()) {
            Rectangle rect = new Rectangle();
            if (seg.isLeft()) {
                rect.setFill(null);
                rect.setStroke(Color.RED);
                int length = seg.getLeft().getText().length();
                int to = from + length - 1;
                System.err.printf("%s - %s%n", from, to);
                rect.setUserData(new Range(from, to));
                from = from + length;
            } else {
                rect.setFill(Color.RED);
                rect.setStroke(Color.BLUE);
                int length = 1; // seg.getRight().g .getText().length();
                int to = from + length - 1;
                System.err.printf("%s - %s%n", from, to);
                rect.setUserData(new Range(from, to));
                from = from + length;
            }
            // todo: store children in a list so that they can be layouted later
            // OR: create separate overlay pane which overrides layoutChildren()
            result.getChildren().add(rect);
        }
*/
            return nodes;
    }
    
    
//    @Override
//    void layoutOverlayNodes(int paragraphIndex, List<Node> nodes) {
//    }
}
