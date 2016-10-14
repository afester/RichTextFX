package org.fxmisc.richtext.demo.richtext;

import java.util.Optional;

import org.fxmisc.richtext.model.SegmentOps;

public class RectangleOps<S> implements SegmentOps<Rectangle<S>, S> {

    @Override
    public int length(Rectangle<S> seg) {
        return 1;
    }

    @Override
    public char charAt(Rectangle<S> seg, int index) {
        return '\ufffc';
    }

    @Override
    public String getText(Rectangle<S> seg) {
        return "\ufffc";
    }

    @Override
    public Rectangle<S> subSequence(Rectangle<S> seg, int start, int end) {
        return seg;
    }

    @Override
    public Rectangle<S> subSequence(Rectangle<S> seg, int start) {
        return seg;
    }

    @Override
    public S getStyle(Rectangle<S> seg) {
        return seg.getStyle();
    }

    @Override
    public Rectangle<S> setStyle(Rectangle<S> seg, S style) {
        return seg.setStyle(style);
    }

    @Override
    public Optional<Rectangle<S>> join(Rectangle<S> currentSeg, Rectangle<S> nextSeg) {
        return Optional.empty();
    }

}
