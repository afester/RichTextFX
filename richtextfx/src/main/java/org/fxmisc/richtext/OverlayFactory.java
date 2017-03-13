package org.fxmisc.richtext;

import java.util.List;

import javafx.scene.Node;

//Remember: OverlayFactory requires a create() method and a layout() method! 
//Create creates the required nodes from the model.
//Layout positions the previously created nodes during the layout phase.
public abstract class OverlayFactory<PS, SEG, S> {

    public abstract List<? extends Node> createOverlayNodes(/*GenericStyledArea<PS, SEG, S> area, */int paragraphIndex);
    
    public abstract void layoutOverlayNodes(TextFlowExt parent, double offset, List<? extends Node> nodes); //  { // int paragraphIndex, List<Node> nodes) {
    

    static class ParagraphOverlay {
        public ParagraphOverlay(OverlayFactory /*<PS, SEG, S>*/ fac) {
            // TODO Auto-generated constructor stub
            this.factory = fac;
        }
        public OverlayFactory factory;
        public List<? extends Node> nodes;
        public void createNodes(int idx) {
            // TODO Auto-generated method stub
            nodes = factory.createOverlayNodes(idx);
        }

        public void layoutNodes(TextFlowExt parent, double offset) {
            System.err.println("***Layouting...");
            factory.layoutOverlayNodes(parent, offset, nodes);
        }
    }
}
