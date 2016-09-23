package org.fxmisc.richtext.demo;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.StyleClassedTextArea;
import org.fxmisc.richtext.model.Codec;
import org.fxmisc.richtext.model.CustomObject;
import org.fxmisc.richtext.model.LinkedImage;
import org.fxmisc.richtext.model.ReadOnlyStyledDocument;

import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;


//enum CustomSegmentTypes implements SegmentType {
//    CIRCLE("Circle"), RECTANGLE("Rectangle");
//
//    private String theName;
//
//    private CustomSegmentTypes(String typeName) {
//        theName = typeName;
//    }
//
//    @Override
//    public String getName() {
//        return theName;
//    }
//}


/**
 * A custom object which represents a rectangle.
 */
class RectangleObject extends CustomObject<Collection<String>> {

    private double width;
    private double height;

    public RectangleObject() {}

    public RectangleObject(double width, double height) {
        super(new ArrayList<String>()); // , CustomSegmentTypes.RECTANGLE);
        this.width = width;
        this.height = height;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    @Override
    public void encode(DataOutputStream os, Codec<Collection<String>> styleCodec) throws IOException {
        Codec.STRING_CODEC.encode(os, Double.toString(width));
        Codec.STRING_CODEC.encode(os, Double.toString(height));
    }

    @Override
    public void decode(DataInputStream is, Codec<Collection<String>> styleCodec) throws IOException {
        try {
            width = Double.parseDouble(Codec.STRING_CODEC.decode(is));
            height = Double.parseDouble(Codec.STRING_CODEC.decode(is));
//            Collection<String> style = styleCodec.decode(is);   // Should be encapsulated by CustomObject!
            //return new RectangleObject(width, height);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // return null;
    }

    @Override
    public Node createNode() {
        Rectangle result = new Rectangle(getWidth(), getHeight());
        return result;
    }
}


/**
 * A custom object which represents a circle.
 */
class CircleObject extends CustomObject<Collection<String>> {

    private double radius;

    public CircleObject() {}

    public CircleObject(double radius) {
        super(new ArrayList<String>()); // , CustomSegmentTypes.CIRCLE);
        this.radius = radius;
    }

    public double getRadius() {
        return radius;
    }

    @Override
    public void encode(DataOutputStream os, Codec<Collection<String>> styleCodec) throws IOException {
        Codec.STRING_CODEC.encode(os, Double.toString(radius));
    }

//    public static Segment<Collection<String>> decode(DataInputStream is, Codec<Collection<String>> styleCodec) {
//        try {
//            double radius = Double.parseDouble(Codec.STRING_CODEC.decode(is));
//            Collection<String> style = styleCodec.decode(is);   // Should be encapsulated by CustomObject!
//            return new CircleObject(radius);
//        } catch (NumberFormatException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        return null;
//    }
//    

    @Override
    public void decode(DataInputStream is, Codec<Collection<String>> styleCodec) throws IOException {
        try {
            radius = Double.parseDouble(Codec.STRING_CODEC.decode(is));
            //Collection<String> style = styleCodec.decode(is);   // Should be encapsulated by CustomObject!
            //return new CircleObject(radius);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Node createNode() {
        Circle result = new Circle(getRadius());
        return result;
    }

}


/**
 * This demo shows how to register custom objects with the RichTextFX editor.
 * It creates a sample document with some text, a custom node with a circle, a custom node
 * with a rectangle and also adds an image to show that images are supported without 
 * explicitly implementing and registering them as custom objects.
 */
public class CustomObjectDemo extends Application {

    public static void main(String[] args) {
        launch(args);
    }

//    /**
//     * Factory method to create a Circle node.
//     * @param segment The segment which represents the circle.
//     * @return A Circle shape object.
//     */
//    private Node createCircleNode(Segment<Collection<String>> segment) {
//    }
//
//    /**
//     * Factory method to create a Rectangle node.
//     * @param segment The segment which represents the rectangle.
//     * @return A Rectangle shape object.
//     */
//    private Node createRectangleNode(Segment<Collection<String>> segment) {
//        RectangleObject rectData = (RectangleObject) segment;
//        Rectangle result = new Rectangle(rectData.getWidth(), rectData.getHeight());
//        return result;
//    }

    
    @Override
    public void start(Stage primaryStage) {
        StyleClassedTextArea textArea = new StyleClassedTextArea();
        textArea.setWrapText(true);

//        // Register custom object types
//        textArea.registerFactory(CustomSegmentTypes.CIRCLE,    this::createCircleNode,    CircleObject::decode);
//        textArea.registerFactory(CustomSegmentTypes.RECTANGLE, this::createRectangleNode, RectangleObject::decode);

        // create the sample document
        textArea.replaceText(0, 0, "This example shows how to add custom nodes, for example Rectangles ");
        textArea.append(
                ReadOnlyStyledDocument.createObject(new RectangleObject(20, 10), 
                                                    new ArrayList<String>(), new ArrayList<String>()));
        textArea.appendText(" or Circles ");
        textArea.append(
                ReadOnlyStyledDocument.createObject(new CircleObject(5), 
                                                    new ArrayList<String>(), new ArrayList<String>()));
        textArea.appendText("\nImages are supported by default (no need to register them): ");
        textArea.append(
                ReadOnlyStyledDocument.createObject(new LinkedImage<Collection<String>>("sample.png", new ArrayList<String>()), 
                                                    new ArrayList<String>(), new ArrayList<String>()));
        textArea.appendText("\nNow, select some text from above (including one or more of the custom objects) using CTRL-C, and paste it somewhere in the document with CTRL-V.");

        Scene scene = new Scene(new StackPane(new VirtualizedScrollPane<>(textArea)), 600, 400);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Custom Object demo");
        primaryStage.show();
    }
}
