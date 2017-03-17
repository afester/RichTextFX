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
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.PathElement;
import javafx.scene.shape.Rectangle;


public class SegmentsOverlayFactory extends OverlayFactory<ParStyle, Either<StyledText<TextStyle>, LinkedImage<TextStyle>>, TextStyle> {

    public SegmentsOverlayFactory(GenericStyledArea<ParStyle, Either<StyledText<TextStyle>, LinkedImage<TextStyle>>, TextStyle> area) {
        super(1);
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
    
    
    @Override
    public List<Node> createOverlayNodes(int paragraphIndex) {
        List<Node> nodes = new ArrayList<>();

        System.err.println("CREATING SEGMENT OVERLAY NODES ...");
        
        Paragraph<ParStyle, Either<StyledText<TextStyle>, LinkedImage<TextStyle>>, TextStyle> paragraph = 
                area.getParagraph(paragraphIndex);
        int segStart = 0;

        for (Either<StyledText<TextStyle>, LinkedImage<TextStyle>> seg : paragraph.getSegments()) {
            if (seg.isLeft()) {

                final String text = seg.getLeft().getText();
                final int textLength = text.length();

                Rectangle rect = new Rectangle(10, 10);
                rect.setFill(null);
                rect.setStroke(Color.RED);
                rect.setUserData(new Range(segStart, segStart + textLength - 1));
                rect.getStrokeDashArray().addAll(3d, 3d);
                nodes.add(rect);
                
                segStart += textLength;
            } else {    // LinkedImage
                Rectangle rect = new Rectangle(10, 10);
                rect.setFill(null);
                rect.setStroke(Color.BLUE);
                rect.setUserData(new Range(segStart, segStart + 1));
                nodes.add(rect);

                segStart++;
            }
        }

        

        return nodes;
    }

    @Override
    public void layoutOverlayNodes(TextFlowExt parent, double offset, List<? extends Node> nodes) {
        double leftInsets = parent.getInsets().getLeft();
        double topInsets = parent.getInsets().getTop();
        System.err.println("LAYOUTING SEGMENT OVERLAY NODES ...");
        nodes.forEach(node -> {
            System.err.println("  OVERLAY:" + node);
            Rectangle wsn = (Rectangle) node;
    
            System.err.println("  " + node);
            Range range = (Range) node.getUserData();
            System.err.println("  RANGE: " + range);
            Rectangle2D bounds2 = getBounds(parent, range.start, range.end + 1);
            System.err.println("  =>BOUNDS: " + bounds2);
            
            wsn.setLayoutX( 1 + bounds2.getMinX() + offset + leftInsets);
            wsn.setLayoutY( 1 + bounds2.getMinY() + topInsets);
            wsn.setHeight(bounds2.getHeight() - 2);
            wsn.setWidth(bounds2.getWidth() - 2);
        });
    }
}
