package org.fxmisc.richtext.demo.richtext;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import org.fxmisc.richtext.GenericStyledArea;
import org.fxmisc.richtext.OverlayFactory;
import org.fxmisc.richtext.TextFlowExt;
import org.fxmisc.richtext.model.Paragraph;
import org.fxmisc.richtext.model.SegmentOps;

import javafx.geometry.Rectangle2D;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.PathElement;
import javafx.scene.text.Text;


public class WhiteSpaceOverlayFactory<PS, SEG, S> extends OverlayFactory<PS, SEG, S> {

    public WhiteSpaceOverlayFactory(GenericStyledArea<PS, SEG, S> area, BiConsumer<? super Node, S> applyStyle) {
        super(0, applyStyle);
        this.area = area;
    }

    private GenericStyledArea<PS, SEG, S> area;

    /**
     * @param textFlow The text flow which contains the segment.
     * @param start The start index of the text segment.
     * @param end  The last index of the text segment. 
     * @return The bounding box of the text segment. This also includes wrapped text segments which 
     *         span more than one line.  
     */
    protected Rectangle2D getBounds(TextFlowExt textFlow, int start, int end) {
        
        // special case for empty lines - there, getRangeShape() would not return a useful shape
        if (start == -1) {
            return new Rectangle2D(0, 0, 0, textFlow.getHeight() + 1);
        }
       
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
    
    

    private Text createTextNode(WhiteSpaceType type, S style, int start, int end) {
        WhiteSpaceNode t = new WhiteSpaceNode(type);
        
        // the base line is the bottom of the text - otherwise, lines which include images
        // or other custom objects would not be properly rendered
        t.setTextOrigin(VPos.BOTTOM);

        t.getStyleClass().add("text");
        t.setOpacity(0.7);
        applyStyle.accept(t, style);
        t.setUserData(new Range(start, end));
        return t;
    }


    @Override
    public List<Node> createOverlayNodes(int paragraphIndex) {
        List<Node> nodes = new ArrayList<>();

        Paragraph<PS, SEG, S> paragraph = area.getParagraph(paragraphIndex);
        int segStart = 0;

        SegmentOps<SEG, S> segOps = area.getSegOps();
        for (SEG seg : paragraph.getSegments()) {

//Whitespace algorithm
            final String text = area.getSegOps().getText(seg);
            final int textLength = text.length();
            for (int i = 0; i < textLength; i++) {
                char ch = text.charAt(i);
                if (ch != ' ' && ch != '\t')
                    continue;

                nodes.add(createTextNode(
                        (ch == ' ') ? WhiteSpaceType.SPACE : WhiteSpaceType.TAB,
                                segOps.getStyle(seg), segStart + i, segStart + i + 1)); 
            }

            segStart += segOps.length(seg);
        }

        // System.err.printf("%s / %s%n", para, area.getParagraphs().size());
        // TODO: does not work yet for newly created empty lines!
       // if (para < area.getParagraphs().size() - 1) {
        System.err.println("CREATING EOL NODE!");
        nodes.add(createTextNode(WhiteSpaceType.EOL,
                paragraph.getStyleAtPosition(segStart),
                segStart - 1, segStart));

        return nodes;
    }
    

    @Override
    public void layoutOverlayNodes(TextFlowExt parent, double offset, List<? extends Node> nodes) { // int paragraphIndex, List<Node> nodes) {
        double leftInsets = parent.getInsets().getLeft();
        double topInsets = parent.getInsets().getTop();
        nodes.forEach(node -> {
            WhiteSpaceNode wsn = (WhiteSpaceNode) node;
            Range range = (Range) node.getUserData();

            Rectangle2D bounds2 = getBounds(parent, range.start, range.end + 1);
    
            System.err.println("H:" + parent.getHeight());
            //if (eolNode.isVisible() != showEOL)
            //    eolNode.setVisible(showEOL);
    
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
            node.setLayoutY(topInsets + bounds2.getMaxY());
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
