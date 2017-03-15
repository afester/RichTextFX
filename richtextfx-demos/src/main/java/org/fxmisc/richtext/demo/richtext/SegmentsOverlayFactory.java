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


public class SegmentsOverlayFactory extends OverlayFactory<ParStyle, Either<StyledText<TextStyle>, LinkedImage<TextStyle>>, TextStyle> {

    public SegmentsOverlayFactory(GenericStyledArea<ParStyle, Either<StyledText<TextStyle>, LinkedImage<TextStyle>>, TextStyle> area) {
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

        return nodes;
    }
    

    @Override
    public void layoutOverlayNodes(TextFlowExt parent, double offset, List<? extends Node> nodes) {
        System.err.println("LAYOUTING SEGMENT OVERLAY NODES ...");
    }
}
