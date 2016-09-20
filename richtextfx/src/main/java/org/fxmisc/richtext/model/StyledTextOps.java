package org.fxmisc.richtext.model;

import java.util.Objects;

public class StyledTextOps<S> implements SegmentOps<StyledText<S>, S> {

    @Override
    public int length(StyledText<S> seg) {
        return seg.text.length();
    }

    @Override
    public char charAt(StyledText<S> seg, int index) {
        return seg.text.charAt(index);
    }

    @Override
    public String getText(StyledText<S> seg) {
        return seg.text;
    }

    @Override
    public StyledText<S> subSequence(StyledText<S> seg, int start, int end) {
        return new StyledText<>(seg.text.substring(start, end), seg.style);
    }

    @Override
    public StyledText<S> subSequence(StyledText<S> seg, int start) {
        return new StyledText<>(seg.text.substring(start), seg.style);
    }

    @Override
    public StyledText<S> append(StyledText<S> seg, String str) {
        return new StyledText<>(seg.text + str, seg.style);
    }

    @Override
    public StyledText<S> spliced(StyledText<S> seg, int from, int to, CharSequence replacement) {
        String left = seg.text.substring(0, from);
        String right = seg.text.substring(to);
        return new StyledText<>(left + replacement + right, seg.style);
    }

    @Override
    public S getStyle(StyledText<S> seg) {
        return seg.style;
    }

    @Override
    public StyledText<S> create(String text, S style) {
        return new StyledText<>(text, style);
    }
}
