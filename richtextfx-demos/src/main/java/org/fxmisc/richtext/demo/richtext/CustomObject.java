package org.fxmisc.richtext.demo.richtext;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.fxmisc.richtext.model.Codec;

import javafx.scene.Node;

public interface CustomObject<S> {

    S getStyle();

    CustomObject<S> setStyle(S style);

    void decode(DataInputStream is, Codec<S> styleCodec) throws IOException;

    void encode(DataOutputStream os, Codec<S> styleCodec) throws IOException;

    Node createNode();

}
