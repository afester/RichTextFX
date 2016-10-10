package org.fxmisc.richtext.demo.richtext;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;

import org.fxmisc.richtext.model.Codec;

import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;


/**
 * A custom object which contains a file path to an image file.
 * When rendered in the rich text editor, the image is loaded from the 
 * specified file.  
 */
public class LinkedImage<S> extends CustomObject<S> {

    private String imagePath;

    LinkedImage() {}

    /**
     * Creates a new linked image object.
     *
     * @param imagePath The path to the image file.
     * @param style The text style to apply to the corresponding segment.
     */
    public LinkedImage(String imagePath, S style) {
        super(style);

        // if the image is below the current working directory,
        // then store as relative path name.
        String currentDir = System.getProperty("user.dir") + File.separatorChar;
        if (imagePath.startsWith(currentDir)) {
            imagePath = imagePath.substring(currentDir.length());
        }

        this.imagePath = imagePath;
    }


    /**
     * @return The path of the image to render.
     */
    public String getImagePath() {
        return imagePath;
    }


    @Override
    public void encode(DataOutputStream os, Codec<S> styleCodec) throws IOException {
        Codec.STRING_CODEC.encode(os, imagePath);
        styleCodec.encode(os, style);
    }

    
    @Override
    public void decode(DataInputStream is, Codec<S> styleCodec) throws IOException {
        imagePath = Codec.STRING_CODEC.decode(is);
        style = styleCodec.decode(is);
    }

    
    @Override
    public String toString() {
        return String.format("LinkedImage[path=%s]", imagePath);
    }

    @Override
    public Node createNode() {
        Image image = new Image("file:" + imagePath); // XXX: No need to create new Image objects each time -
                                                      // could be cached in the model layer
        ImageView result = new ImageView(image);
        return result;
    }
}
