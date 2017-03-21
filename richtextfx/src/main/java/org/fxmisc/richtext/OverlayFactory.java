package org.fxmisc.richtext;

import java.util.List;
import java.util.function.BiConsumer;

import javafx.scene.Node;

//Remember: OverlayFactory requires a create() method and a layout() method! 
//Create creates the required nodes from the model.
//Layout positions the previously created nodes during the layout phase.
public abstract class OverlayFactory<PS, SEG, S> {

    private final int layer;
    protected BiConsumer<? super Node, S> applyStyle;


    public OverlayFactory(int layer, BiConsumer<? super Node, S> textStyle) {
        this.layer = layer;
        this.applyStyle = textStyle;
    }

    public abstract List<? extends Node> createOverlayNodes(/*GenericStyledArea<PS, SEG, S> area, */int paragraphIndex);
    
    public abstract void layoutOverlayNodes(TextFlowExt parent, double offset, List<? extends Node> nodes); //  { // int paragraphIndex, List<Node> nodes) {

    public final int getLayer() {
        return layer;
    }

    
    @Override
    public String toString() {
        return String.format("OverlayFactory[layer=%s]", layer);
    }
}
