package org.fxmisc.richtext.demo.richtext;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.fxmisc.richtext.model.Codec;
import org.fxmisc.richtext.model.SegmentOps;

public class CustomObjectOps<S> implements SegmentOps<CustomObject<S>, S> {

    @Override
    public int length(CustomObject<S> seg) {
        return 1;
    }

    @Override
    public char charAt(CustomObject<S> seg, int index) {
        return '\ufffc';
    }

    @Override
    public String getText(CustomObject<S> seg) {
        return "\ufffc";
    }

    @Override
    public CustomObject<S> subSequence(CustomObject<S> seg, int start, int end) {
        return seg;
    }

    @Override
    public CustomObject<S> subSequence(CustomObject<S> seg, int start) {
        return seg;
    }

    @Override
    public CustomObject<S> append(CustomObject<S> seg, String str) {
        throw new UnsupportedOperationException();
    }

    @Override
    public CustomObject<S> spliced(CustomObject<S> seg, int from, int to, CharSequence replacement) {
        throw new UnsupportedOperationException();
    }

    @Override
    public S getStyle(CustomObject<S> seg) {
        return seg.style;
    }

    @Override
    public String toString(CustomObject<S> seg) {
        return seg.toString();
    }

    @Override
    public CustomObject<S> create(String text, S style) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void encode(CustomObject<S> seg, DataOutputStream os, Codec<S> styleCodec) throws IOException {
        seg.encode(os, styleCodec);
    }

    @Override
    public CustomObject<S> decode(DataInputStream is, Codec<S> styleCodec) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setStyle(CustomObject<S> seg, S style) {
    }

    @Override
    public boolean canJoin(CustomObject<S> currentSeg, CustomObject<S> nextSeg) {
        return false;
    }


// CustomObject specific operations
    
    public CustomObject<S> decode(Class<?> segmentClass, DataInputStream is, Codec<S> styleCodec) throws IOException {
        try {
            @SuppressWarnings("unchecked")
            CustomObject<S> result = (CustomObject<S>) segmentClass.newInstance();
            result.decode(is, styleCodec);
            return result;
        } catch (InstantiationException | IllegalAccessException e) {
            throw new IOException(e);
        }
    }

}
