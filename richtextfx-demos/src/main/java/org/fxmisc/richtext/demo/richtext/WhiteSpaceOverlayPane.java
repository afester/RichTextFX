package org.fxmisc.richtext.demo.richtext;

import org.fxmisc.richtext.TextFlowExt;

import javafx.geometry.Rectangle2D;
import javafx.scene.Node;

public
class WhiteSpaceOverlayPane extends OverlayPane {

    private boolean last;
    
    public WhiteSpaceOverlayPane(boolean last) {
        this.last = last;

//        parentProperty().addListener((observable, oldParent, newParent) -> {
//            // this node also "need layout" if parent "needs layout"
//            if (newParent != null) {
//                newParent.needsLayoutProperty().addListener((ob, o, n) -> {
//                    if (n)
//                        setNeedsLayout(true);
//                });
//            }
//        });
    }

    @Override
    protected void layoutChildren() {
        System.err.println("layoutChildren()");

        TextFlowExt flow = (TextFlowExt) getParent().lookup(".paragraph-text");

        for (Node node :  getChildren()) {
            WhiteSpaceNode wsn = (WhiteSpaceNode) node;

            System.err.println("  " + node);
            Range range = (Range) node.getUserData();
            System.err.println("  RANGE: " + range);
            Rectangle2D bounds = getBounds(flow, range.start, range.end + 1);
            System.err.println("  =>BOUNDS: " + bounds);
            
            //if (eolNode.isVisible() != showEOL)
            //    eolNode.setVisible(showEOL);

            System.err.println("WSN:" + wsn);

            //boolean showEOL = (paragraphIndex < getTextArea().getParagraphs().size() - 1);

            if (wsn.getType() == WhiteSpaceType.EOL) {
                System.err.println(last);
                if (last) {
                    wsn.setVisible(false);
                } else {
                    wsn.setVisible(true);
                node.setLayoutX(/* leftInsets + */ bounds.getMaxX());
                node.setLayoutY(/* topInsets + */ bounds.getMinY());
                }
            } else if (wsn.getType() == WhiteSpaceType.TAB) {
                node.setLayoutX(/* leftInsets + */ (bounds.getMinX() + bounds.getMaxX()) / 2);  // TODO: calculate properly
                node.setLayoutY(/* topInsets + */ bounds.getMinY());
            } else {
                node.setLayoutX(/* leftInsets + */ bounds.getMinX());
                node.setLayoutY(/* topInsets + */ bounds.getMinY());
            }
        }
    }
}
