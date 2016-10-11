package org.fxmisc.richtext.demo.richtext;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.fxmisc.richtext.model.Codec;

import javafx.scene.Node;

public abstract class CustomObject<S> {

    S style;

    protected CustomObject() {}

    public CustomObject(S style) {
        this.style = style;
    }


    public abstract void decode(DataInputStream is, Codec<S> styleCodec) throws IOException;

    public abstract void encode(DataOutputStream os, Codec<S> styleCodec) throws IOException;

    public abstract Node createNode();
}
