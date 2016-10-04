package org.fxmisc.richtext.model;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public final class StyledTextOps<S> implements SegmentOps<StyledText<S>, S> {

    public StyledTextOps() {}

    @Override
    public int length(StyledText<S> styledText) {
        return styledText.getText().length();
    }

    @Override
    public char charAt(StyledText<S> styledText, int index) {
        return styledText.getText().charAt(index);
    }

    @Override
    public String getText(StyledText<S> styledText) {
        return styledText.getText();
    }

    @Override
    public StyledText<S> subSequence(StyledText<S> styledText, int start, int end) {
        return create(styledText.getText().substring(start, end), styledText.getStyle());
    }

    @Override
    public StyledText<S> subSequence(StyledText<S> styledText, int start) {
        return create(styledText.getText().substring(start), styledText.getStyle());
    }

    @Override
    public StyledText<S> append(StyledText<S> styledText, String str) {
        return create(styledText.getText() + str, styledText.getStyle());
    }

    @Override
    public StyledText<S> spliced(StyledText<S> styledText, int from, int to, CharSequence replacement) {
        String text = styledText.getText();
        String left = text.substring(0, from);
        String right = text.substring(to);
        return create(left + replacement + right, styledText.getStyle());
    }

    @Override
    public S getStyle(StyledText<S> styledText) {
        return styledText.getStyle();
    }

    @Override
    public StyledText<S> create(String text, S style) {
        return new StyledText<S>(text, style);
    }

    @Override
    public String toString(StyledText<S> styledText) {
        return styledText.toString();
    }

    @Override
    public void encode(DataOutputStream os, StyledText<S> t, Codec<S> styleCodec) throws IOException {
        Codec.STRING_CODEC.encode(os, getText(t));
        styleCodec.encode(os, getStyle(t));
    }

    @Override
    public StyledText<S> decode(DataInputStream is, Codec<S> styleCodec) throws IOException {
        String text = Codec.STRING_CODEC.decode(is);
        S style = styleCodec.decode(is);
        return create(text, style);
    }
}
