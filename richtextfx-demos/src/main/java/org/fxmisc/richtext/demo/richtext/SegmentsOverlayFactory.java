package org.fxmisc.richtext.demo.richtext;

import java.util.ArrayList;
import java.util.List;

import org.fxmisc.richtext.GenericStyledArea;
import org.fxmisc.richtext.OverlayFactory;
import org.fxmisc.richtext.TextFlowExt;
import org.fxmisc.richtext.model.Paragraph;
import org.fxmisc.richtext.model.StyledText;
import org.reactfx.util.Either;

import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;


public class SegmentsOverlayFactory extends OverlayFactory<ParStyle, Either<StyledText<TextStyle>, LinkedImage<TextStyle>>, TextStyle> {

    public SegmentsOverlayFactory(GenericStyledArea<ParStyle, Either<StyledText<TextStyle>, LinkedImage<TextStyle>>, TextStyle> area) {
        super(1, (n, s) -> {});
        this.area = area;
    }
    
    
    private GenericStyledArea<ParStyle, Either<StyledText<TextStyle>, LinkedImage<TextStyle>>, TextStyle> area;

    private final static Color[] shapeColor = { Color.RED, Color.BLUE };

    @Override
    public List<Node> createOverlayNodes(int paragraphIndex) {
        List<Node> nodes = new ArrayList<>();
        
        Paragraph<ParStyle, Either<StyledText<TextStyle>, LinkedImage<TextStyle>>, TextStyle> paragraph = 
                area.getParagraph(paragraphIndex);
        int segStart = 0;

        Color col = shapeColor[paragraphIndex % 2];
        for (Either<StyledText<TextStyle>, LinkedImage<TextStyle>> seg : paragraph.getSegments()) {
            if (seg.isLeft()) {

                final String text = seg.getLeft().getText();
                final int textLength = text.length();

                Path rect = new Path();
                rect.setFill(null);
                rect.setStroke(col);
                rect.setUserData(new Range(segStart, segStart + textLength - 1));
                rect.getStrokeDashArray().addAll(3d, 3d);
                nodes.add(rect);

                segStart += textLength;
            } else {    // LinkedImage
                Path rect = new Path();
                rect.setFill(null);
                rect.setStroke(col);
                rect.setUserData(new Range(segStart, segStart));
                rect.getStrokeDashArray().addAll(3d, 3d);
                nodes.add(rect);

                segStart++;
            }
        }

        return nodes;
    }

    @Override
    public void layoutOverlayNodes(TextFlowExt parent, double offset, List<? extends Node> nodes) {
        double leftInsets = parent.getInsets().getLeft();
        nodes.forEach(node -> {

            Range range = (Range) node.getUserData();
            PathElement[] shape = parent.getRangeShape(range.start, range.end + 1);
            for (PathElement pe : shape) {
                System.err.println("   " + pe);
                if (pe instanceof MoveTo) {
                  MoveTo moveTo = (MoveTo) pe;
                  if (moveTo.getY() < 1.0) {
                      moveTo.setY(1.0);
                  } else {
                      moveTo.setY(moveTo.getY() - 1.0);    
                  }
                  
                } else if (pe instanceof LineTo) {
                  LineTo lineTo = (LineTo) pe;
                  if (lineTo.getY() < 1.0) {
                      lineTo.setY(1.0);
                  } else {
                      lineTo.setY(lineTo.getY() - 1.0);    
                  }
                }
            }
            Path path = (Path) node;
            path.getElements().setAll(shape);

            path.setLayoutX(leftInsets);
        });
    }
}
