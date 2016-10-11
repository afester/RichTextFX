package org.fxmisc.richtext.model;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Objects;

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
        return new StyledText<>(styledText.getText().substring(start, end), styledText.getStyle());
    }

    @Override
    public StyledText<S> subSequence(StyledText<S> styledText, int start) {
        return new StyledText<>(styledText.getText().substring(start), styledText.getStyle());
    }

    @Override
    public StyledText<S> append(StyledText<S> styledText, String str) {
        return new StyledText<>(styledText.getText() + str, styledText.getStyle());
    }

    @Override
    public StyledText<S> spliced(StyledText<S> styledText, int from, int to, CharSequence replacement) {
        String text = styledText.getText();
        String left = text.substring(0, from);
        String right = text.substring(to);
        return new StyledText<>(left + replacement + right, styledText.getStyle());
    }

    @Override
    public S getStyle(StyledText<S> styledText) {
        return styledText.getStyle();
    }

    @Override
    public String toString(StyledText<S> styledText) {
        return styledText.toString();
    }

    @Override
    public StyledText<S> create(String text, S style) {
        return new StyledText<>(text, style);
    }

    @Override
    public void encode(StyledText<S> seg, DataOutputStream os, Codec<S> styleCodec) throws IOException {
        Codec.STRING_CODEC.encode(os, getText(seg));
        styleCodec.encode(os, getStyle(seg));
    }

    @Override
    public StyledText<S> decode(DataInputStream is, Codec<S> styleCodec) throws IOException {
        StyledText<S> result = new StyledText<>();
        result.decode(is, styleCodec);
        return result;
    }

    @Override
    public void setStyle(StyledText<S> seg, S style) {
        seg.setStyle(style);
    }

    @Override
    public boolean canJoin(StyledText<S> left, StyledText<S> right) {
        return Objects.equals(left.getStyle(), right.getStyle());
    }
}
