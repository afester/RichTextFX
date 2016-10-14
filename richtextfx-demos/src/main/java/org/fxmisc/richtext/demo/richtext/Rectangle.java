package org.fxmisc.richtext.demo.richtext;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import javafx.scene.Node;

import org.fxmisc.richtext.model.Codec;


/**
 * A custom object which contains a file path to an image file.
 * When rendered in the rich text editor, the image is loaded from the
 * specified file.
 */
public class Rectangle<S> {

    public static <S> Codec<Rectangle<S>> codec(Codec<S> styleCodec) {
        return new Codec<Rectangle<S>>() {

            @Override
            public String getName() {
                return "Rectangle<" + styleCodec.getName() + ">";
            }

            @Override
            public void encode(DataOutputStream os, Rectangle<S> i) throws IOException {
                Codec.STRING_CODEC.encode(os, Double.toString(i.getWidth()));
                Codec.STRING_CODEC.encode(os, Double.toString(i.getHeight()));
                styleCodec.encode(os, i.style);
            }

            @Override
            public Rectangle<S> decode(DataInputStream is) throws IOException {
                double width = Double.parseDouble(Codec.STRING_CODEC.decode(is));
                double height = Double.parseDouble(Codec.STRING_CODEC.decode(is));
                S style = styleCodec.decode(is);
                return new Rectangle<>(width, height, style);
            }

        };
    }

    private final double width;
    private final double height;
    private final S style;

    public Rectangle(double width, double height, S style) {
        this.width = width;
        this.height = height;
        this.style = style;
    }

    public Rectangle<S> setStyle(S style) {
        return new Rectangle<>(width, height, style);
    }

    public double getWidth() {
        return width;

    }

    public double getHeight() {
        return height;
    }

    public S getStyle() {
        return style;
    }


    @Override
    public String toString() {
        return String.format("Rectangle[width=%s, hwight=%s]", width, height);
    }

    public Node createNode() {
        javafx.scene.shape.Rectangle result = new javafx.scene.shape.Rectangle(width, height); 
        return result;
    }
}
