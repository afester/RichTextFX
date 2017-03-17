package org.fxmisc.richtext.demo.richtext;

import java.util.ArrayList;
import java.util.List;

import org.fxmisc.richtext.GenericStyledArea;
import org.fxmisc.richtext.OverlayFactory;
import org.fxmisc.richtext.TextFlowExt;
import org.fxmisc.richtext.model.Paragraph;
import org.fxmisc.richtext.model.StyledText;
import org.reactfx.util.Either;

import javafx.geometry.Rectangle2D;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.PathElement;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;


public class WhiteSpaceOverlayFactory extends OverlayFactory<ParStyle, Either<StyledText<TextStyle>, LinkedImage<TextStyle>>, TextStyle> {

    public WhiteSpaceOverlayFactory(GenericStyledArea<ParStyle, Either<StyledText<TextStyle>, LinkedImage<TextStyle>>, TextStyle> area) {
        super(0);
        this.area = area;
    }

    
    private GenericStyledArea<ParStyle, Either<StyledText<TextStyle>, LinkedImage<TextStyle>>, TextStyle> area;

    

    protected Rectangle2D getBounds(TextFlowExt textFlow, int start, int end) {
        PathElement[] shape = textFlow.getRangeShape(start, end);
        double minX = 0, minY = 0, maxX = 0, maxY = 0;
        for (PathElement pathElement : shape) {
            if (pathElement instanceof MoveTo) {
                MoveTo moveTo = (MoveTo) pathElement;
                minX = maxX = moveTo.getX();
                minY = maxY = moveTo.getY();
            } else if (pathElement instanceof LineTo) {
                LineTo lineTo = (LineTo) pathElement;
                double x = lineTo.getX();
                double y = lineTo.getY();
                minX = Math.min(minX, x);
                minY = Math.min(minY, y);
                maxX = Math.max(maxX, x);
                maxY = Math.max(maxY, y);
            }
        }
        return new Rectangle2D(minX, minY, maxX - minX, maxY - minY);
    }
    
    
    private Text createTextNode(WhiteSpaceType type, TextStyle style, int start, int end) {
        WhiteSpaceNode t = new WhiteSpaceNode(type);
        //t.setTextOrigin(VPos.BOTTOM);
        t.setTextOrigin(VPos.TOP);
        t.getStyleClass().add("text");
        t.setOpacity(0.7);
        t.setStyle(style.toCss() + ";color=\"red\"");
        t.setUserData(new Range(start, end));
        return t;
    }


    @Override
    public List<Node> createOverlayNodes(int paragraphIndex) {
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

                    nodes.add(createTextNode(
                            (ch == ' ') ? WhiteSpaceType.SPACE : WhiteSpaceType.TAB,
                                    seg.getLeft().getStyle(),segStart + i, segStart + i + 1)); 
                }
                segStart += textLength;
            } else {
                segStart++;
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
    

    @Override
    public void layoutOverlayNodes(TextFlowExt parent, double offset, List<? extends Node> nodes) { // int paragraphIndex, List<Node> nodes) {
        double leftInsets = parent.getInsets().getLeft();
        double topInsets = parent.getInsets().getTop();
        nodes.forEach(node -> {
            System.err.println("  OVERLAY:" + node);
            WhiteSpaceNode wsn = (WhiteSpaceNode) node;
    
            System.err.println("  " + node);
            Range range = (Range) node.getUserData();
            System.err.println("  RANGE: " + range);
            Rectangle2D bounds2 = getBounds(parent, range.start, range.end + 1);
            System.err.println("  =>BOUNDS: " + bounds2);
    
            //if (eolNode.isVisible() != showEOL)
            //    eolNode.setVisible(showEOL);
    
            System.err.println("WSN:" + wsn);
    
            //boolean showEOL = (paragraphIndex < getTextArea().getParagraphs().size() - 1);
    
            if (wsn.getType() == WhiteSpaceType.EOL) {
    //        System.err.println(last);
    //        if (last) {
    //             wsn.setVisible(false);
    //        } else {
                wsn.setVisible(true);
                node.setLayoutX(leftInsets + bounds2.getMaxX() + offset);
    //            }
            } else if (wsn.getType() == WhiteSpaceType.TAB) {
                node.setLayoutX(leftInsets + offset + (bounds2.getMinX())); //  + bounds2.getMaxX()) / 2);  // TODO: calculate properly
            } else {
                node.setLayoutX(leftInsets + offset + bounds2.getMinX());
            }
            //node.setLayoutY(topInsets + bounds2.getMaxY());
            node.setLayoutY(topInsets + bounds2.getMinY());
        });
    }
    
    
    
    private static class WhiteSpaceNode extends Text {
        private WhiteSpaceType type;

        public WhiteSpaceNode(WhiteSpaceType type) {
            super(type.getText());
            this.type = type;
        }

        public WhiteSpaceType getType() {
            return type;
        }
    }


    private static enum WhiteSpaceType {
        SPACE("\u00B7"),
        TAB("\u00BB"),   // "\u2192"; // would need addtl. computation to clip the arrow!
        EOL("\u00B6");

        final String character;

        WhiteSpaceType(String character) {
            this.character = character;
        }

        String getText() {
            return character;
        }
    }
}
