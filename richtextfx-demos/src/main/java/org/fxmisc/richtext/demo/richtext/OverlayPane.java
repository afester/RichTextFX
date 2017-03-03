package org.fxmisc.richtext.demo.richtext;

import org.fxmisc.richtext.TextFlowExt;

import javafx.geometry.Rectangle2D;
import javafx.scene.layout.Pane;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.PathElement;

//This is the parent of all nodes which shall be rendered on top of a specific paragraph.
public abstract class OverlayPane extends Pane {
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
}