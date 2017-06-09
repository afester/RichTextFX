package org.fxmisc.richtext;

import static org.reactfx.util.Tuples.*;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.IntFunction;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.IndexRange;
import javafx.scene.layout.Region;
import javafx.scene.paint.Paint;
import javafx.scene.text.TextFlow;

import org.fxmisc.richtext.model.Paragraph;
import org.fxmisc.richtext.util.MouseStationaryHelper;
import org.reactfx.EventStream;
import org.reactfx.util.Either;
import org.reactfx.util.Tuple2;
import org.reactfx.value.Val;
import org.reactfx.value.Var;

class ParagraphBox<PS, SEG, S> extends Region {

    /**
     * An opaque class representing horizontal caret offset.
     * Although it is just a wrapper around double, its purpose is to increase
     * type safety.
     */
    public static class CaretOffsetX {
        private final double value;

        private CaretOffsetX(double value) {
            this.value = value;
        }
    }

    private final ParagraphText<PS, SEG, S> text;

    // A factory to produce a node which shall be rendered in front of the paragraph
    private final ObjectProperty<IntFunction<? extends Node>> graphicFactory
            = new SimpleObjectProperty<>(null);
    public ObjectProperty<IntFunction<? extends Node>> graphicFactoryProperty() {
        return graphicFactory;
    }

    private final Val<Node> graphic;

    
    
    //private final ObservableList<OverlayFactory<PS, SEG, S>> paragraphOverlayFactories = FXCollections.observableArrayList(); // SimpleObjectProperty<>(null);
    // public void setParagraphOverlayFactory(OverlayFactory<PS, SEG, S> factory) { paragraphOverlayFactory.set(factory); }
    // public OverlayFactory<PS, SEG, S> getParagraphOverlayFactory() { return paragraphOverlayFactory.get(); }
    //public ObservableList<OverlayFactory<PS, SEG, S>> overlayFactoriesProperty() { return paragraphOverlayFactories; }

    
    // A factory to create and layout nodes which shall be rendered on top of a paragraph
    //private Map<OverlayFactory<PS, SEG, S>, OverlayFactory.ParagraphOverlay> paragraphOverlays = new HashMap<>();
    
//    private final Map<Val<OverlayFactory<PS, SEG, S>>, Val<List<? extends Node>>> overlayFactories
//        = new HashMap<>(); //SimpleObjectProperty<>(null);
//    public ObjectProperty<OverlayFactory<PS, SEG, S>> overlayFactoryProperty() {
//        return overlayFactory;
//    }

    
    private Var<List<OverlayFactory<PS, SEG, S>>> paragraphOverlayFactories = Var.newSimpleVar(null);

    // an overlay node for the paragraph
    //private final Val<List<? extends Node>> overlay;

    
    
    
    final DoubleProperty graphicOffset = new SimpleDoubleProperty(0.0);

    private final BooleanProperty wrapText = new SimpleBooleanProperty(false);
    public BooleanProperty wrapTextProperty() { return wrapText; }
    {
        wrapText.addListener((obs, old, w) -> requestLayout());
    }

    private final Var<Integer> index;
    public Val<Integer> indexProperty() { return index; }
    public void setIndex(int index) { this.index.setValue(index); }
    public int getIndex() { return index.getValue(); }

    private class NodeContainer {
        public NodeContainer(OverlayFactory<PS, SEG, S> ovl2, List<? extends Node> createdNodes) {
            this.ovl = ovl2;
            this.nodes = createdNodes;
        }
        OverlayFactory<PS, SEG, S> ovl;
        List<? extends Node> nodes;
    }
    private List<NodeContainer> overlayMap = new ArrayList<>();

    ParagraphBox(Paragraph<PS, SEG, S> par, BiConsumer<TextFlow, PS> applyParagraphStyle,
                 Function<SEG, Node> nodeFactory) {
        this.getStyleClass().add("paragraph-box");
        this.text = new ParagraphText<>(par, nodeFactory);
        applyParagraphStyle.accept(this.text, par.getParagraphStyle());
        this.index = Var.newSimpleVar(0);
        getChildren().add(text);

        // setup graphic node to display in front of the paragraph
        // note that the nodes are created at a later time, when the factory is set!!
        graphic = Val.combine(
                graphicFactory,
                this.index,
                (f, i) -> f != null ? f.apply(i) : null);
        graphic.addListener((obs, oldG, newG) -> {
            if(oldG != null) {
                getChildren().remove(oldG);
            }
            if(newG != null) {
                getChildren().add(newG);
            }
        });
        graphicOffset.addListener(obs -> requestLayout());

        System.err.println("SETUP: " + objId(this));
        // create a combination of index and list of factories
        Val<List<Node>> res = Val.combine(paragraphOverlayFactories,
                this.index,
                (p, i) -> {
                    List<Node> result = new ArrayList<>();
                    overlayMap = new ArrayList<>();
                    for (OverlayFactory<PS, SEG, S> ovl : p) {
                        
                        
                     // PENDING: We need to remember which factory has created which set of nodes!!!!!
                     // Otherwise we can not let the factory layout the respective nodes later.
                        List<? extends Node> createdNodes = ovl.createOverlayNodes(i);

                        overlayMap.add(new NodeContainer(ovl, createdNodes));

                        // ovl;
                        // ovl.createOverlayNodes(i);

                        result.addAll(createdNodes); // ovl.createOverlayNodes(i));
                    }

                    // System.err.println("INDEX OR FACTORIES CHANGED!");
                    return result;
               //     return "RES: " + p + "/" + i; 
               } );
        

        res.addListener((obs, oldN, newN) -> {
            if(oldN != null) {
            	System.err.printf("%s: REMOVING OLD CHILDREN! (%s)%n", objId(this), this.index.getValue());
                getChildren().removeAll(oldN);
            }
            if(newN != null) {
            	System.err.printf("%s: ADDING NEW CHILDREN! (%s): %s%n", objId(this), this.index.getValue(), newN);
                getChildren().addAll(newN);
                // ((Parent) getParent()).setNeedsLayout(true);
                //setNeedsLayout(true);
                //requestLayout();
            }
        System.err.printf("  %s -> %s%n", oldN, newN); });
    }

    private static String objId(Object obj) {
    	return String.format("0x%x (%s)",  System.identityHashCode(obj), obj.getClass().getName());
	}
	@Override
    public String toString() {
        return graphic.isPresent()
                ? "[#|" + text.getParagraph() + "]"
                : "["   + text.getParagraph() + "]";
    }

    public Property<Boolean> caretVisibleProperty() { return text.caretVisibleProperty(); }

    public Property<Paint> highlightFillProperty() { return text.highlightFillProperty(); }

    public Property<Paint> highlightTextFillProperty() { return text.highlightTextFillProperty(); }

    public Var<Integer> caretPositionProperty() { return text.caretPositionProperty(); }

    public Property<IndexRange> selectionProperty() { return text.selectionProperty(); }

    Paragraph<PS, SEG, S> getParagraph() {
        return text.getParagraph();
    }

    public EventStream<Either<Tuple2<Point2D, Integer>, Object>> stationaryIndices(Duration delay) {
        EventStream<Either<Point2D, Void>> stationaryEvents = new MouseStationaryHelper(this).events(delay);
        EventStream<Tuple2<Point2D, Integer>> hits = stationaryEvents.filterMap(Either::asLeft)
                .filterMap(p -> {
                    OptionalInt charIdx = hit(p).getCharacterIndex();
                    if(charIdx.isPresent()) {
                        return Optional.of(t(p, charIdx.getAsInt()));
                    } else {
                        return Optional.empty();
                    }
                });
        EventStream<?> stops = stationaryEvents.filter(Either::isRight).map(Either::getRight);
        return hits.or(stops);
    }

    public CharacterHit hit(Point2D pos) {
        return hit(pos.getX(), pos.getY());
    }

    public CharacterHit hit(double x, double y) {
        Point2D onScreen = this.localToScreen(x, y);
        Point2D inText = text.screenToLocal(onScreen);
        Insets textInsets = text.getInsets();
        return text.hit(inText.getX() - textInsets.getLeft(), inText.getY() - textInsets.getTop());
    }

    public CaretOffsetX getCaretOffsetX() {
        layout(); // ensure layout, is a no-op if not dirty
        return new CaretOffsetX(text.getCaretOffsetX());
    }

    public int getCurrentLineStartPosition() {
        layout(); // ensure layout, is a no-op if not dirty
        return text.getCurrentLineStartPosition();
    }

    public int getCurrentLineEndPosition() {
        layout(); // ensure layout, is a no-op if not dirty
        return text.getCurrentLineEndPosition();
    }

    public int getLineCount() {
        layout(); // ensure layout, is a no-op if not dirty
        return text.getLineCount();
    }

    public int getCurrentLineIndex() {
        layout(); // ensure layout, is a no-op if not dirty
        return text.currentLineIndex();
    }

    public Bounds getCaretBounds() {
        layout(); // ensure layout, is a no-op if not dirty
        Bounds b = text.getCaretBounds();
        return text.localToParent(b);
    }

    public Bounds getCaretBoundsOnScreen() {
        layout(); // ensure layout, is a no-op if not dirty
        return text.getCaretBoundsOnScreen();
    }

    public Optional<Bounds> getSelectionBoundsOnScreen() {
        layout(); // ensure layout, is a no-op if not dirty
        return text.getSelectionBoundsOnScreen();
    }

    @Override
    protected double computeMinWidth(double ignoredHeight) {
        return computePrefWidth(-1);
    }

    @Override
    protected double computePrefWidth(double ignoredHeight) {
        Insets insets = getInsets();
        return wrapText.get()
                ? 0 // return 0, VirtualFlow will size it to its width anyway
                : getGraphicPrefWidth() + text.prefWidth(-1) + insets.getLeft() + insets.getRight();
    }

    @Override
    protected double computePrefHeight(double width) {
        Insets insets = getInsets();
        double overhead = getGraphicPrefWidth() - insets.getLeft() - insets.getRight();
        return text.prefHeight(width - overhead) + insets.getTop() + insets.getBottom();
    }

    @Override
    protected
    void layoutChildren() {
        System.err.printf("%s: ParagraphBox.layoutChildren(%s)!!!!!!!!!%n", objId(this), this.index.getValue());

        Bounds bounds = getLayoutBounds();
        double w = bounds.getWidth();
        double h = bounds.getHeight();
        double graphicWidth = getGraphicPrefWidth();

        text.resizeRelocate(graphicWidth, 0, w - graphicWidth, h);
        graphic.ifPresent(g -> g.resizeRelocate(graphicOffset.get(), 0, graphicWidth, h));

        this.overlayMap.forEach(entry -> entry.ovl.layoutOverlayNodes(text, graphicWidth, entry.nodes));
    }

    double getGraphicPrefWidth() {
        if(graphic.isPresent()) {
            return graphic.getValue().prefWidth(-1);
        } else {
            return 0.0;
        }
    }

    /**
     * Hits the embedded TextFlow at the given line and x offset.
     *
     * @param x x coordinate relative to the embedded TextFlow.
     * @param line index of the line in the embedded TextFlow.
     * @return hit info for the given line and x coordinate
     */
    CharacterHit hitTextLine(CaretOffsetX x, int line) {
        return text.hitLine(x.value, line);
    }

    /**
     * Hits the embedded TextFlow at the given x and y offset.
     *
     * @return hit info for the given x and y coordinates
     */
    CharacterHit hitText(CaretOffsetX x, double y) {
        return text.hit(x.value, y);
    }

    public void updateParagraphOverlayFactories(List<OverlayFactory<PS, SEG, S>> overlayFactories) {
        List<OverlayFactory<PS, SEG, S>> cloned = new ArrayList<>(overlayFactories);
        System.err.printf("%s: OVERLAY FACTORIES NOW: %s%n", objId(this), cloned);
        paragraphOverlayFactories.setValue(cloned);
        // setNeedsLayout(true);
    }
}
