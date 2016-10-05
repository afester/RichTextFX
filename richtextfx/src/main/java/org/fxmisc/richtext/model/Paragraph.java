package org.fxmisc.richtext.model;

import static org.fxmisc.richtext.model.TwoDimensional.Bias.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import javafx.scene.control.IndexRange;

import org.fxmisc.richtext.model.TwoDimensional.Position;

public final class Paragraph<PS, SEG, S> {

    @SafeVarargs
    private static <T> List<T> list(T head, T... tail) {
        if(tail.length == 0) {
            return Collections.singletonList(head);
        } else {
            ArrayList<T> list = new ArrayList<>(1 + tail.length);
            list.add(head);
            Collections.addAll(list, tail);
            return list;
        }
    }

    private final List<SEG> segments;
    private final TwoLevelNavigator navigator;
    private final PS paragraphStyle;

    private final SegmentOps<SEG, S> segmentOps;

    public Paragraph(PS paragraphStyle, SegmentOps<SEG, S> segmentOps, String text, S style) {
        this(paragraphStyle, segmentOps, segmentOps.create(text, style)); // new StyledText<>(text, style));
    }

    @SafeVarargs
    public Paragraph(PS paragraphStyle, SegmentOps<SEG, S> segmentOps, SEG text, SEG... texts) {
        this(paragraphStyle, segmentOps, list(text, texts));
    }

    Paragraph(PS paragraphStyle, SegmentOps<SEG, S> segmentOps, List<SEG> segments) {
        assert !segments.isEmpty();
        
        this.segmentOps = segmentOps;
        this.segments = segments;
        this.paragraphStyle = paragraphStyle;
        navigator = new TwoLevelNavigator(segments::size,
                i -> segmentOps.length(segments.get(i)));
    }

    public List<SEG> getSegments() {
        return Collections.unmodifiableList(segments);
    }

    public PS getParagraphStyle() {
        return paragraphStyle;
    }

    private int length = -1;
    public int length() {
        if(length == -1) {
            length = segments.stream().mapToInt(segmentOps::length).sum();
        }
        return length;
    }

    public char charAt(int index) {
        Position pos = navigator.offsetToPosition(index, Forward);
        return segmentOps.charAt(segments.get(pos.getMajor()), pos.getMinor());
    }

    public String substring(int from, int to) {
        return getText().substring(from, Math.min(to, length()));
    }

    public String substring(int from) {
        return getText().substring(from);
    }

    /**
     * Concatenates this paragraph with the given paragraph {@code p}.
     * The paragraph style of the result will be that of this paragraph,
     * unless this paragraph is empty and {@code p} is non-empty, in which
     * case the paragraph style of the result will be that of {@code p}.
     */
    public Paragraph<PS, SEG, S> concat(Paragraph<PS, SEG, S> p) {
        if(p.length() == 0) {
            return this;
        }

        if(length() == 0) {
            return p;
        }

        SEG left = segments.get(segments.size() - 1);
        SEG right = p.segments.get(0);
        if(Objects.equals(segmentOps.getStyle(left), segmentOps.getStyle(right))) {
            SEG segment = segmentOps.append(left, segmentOps.getText(right));
            List<SEG> segs = new ArrayList<>(segments.size() + p.segments.size() - 1);
            segs.addAll(segments.subList(0, segments.size()-1));
            segs.add(segment);
            segs.addAll(p.segments.subList(1, p.segments.size()));
            return new Paragraph<>(paragraphStyle, segmentOps, segs);
        } else {
            List<SEG> segs = new ArrayList<>(segments.size() + p.segments.size());
            segs.addAll(segments);
            segs.addAll(p.segments);
            return new Paragraph<>(paragraphStyle, segmentOps, segs);
        }
    }

    /**
     * Similar to {@link #concat(Paragraph)}, except in case both paragraphs
     * are empty, the result's paragraph style will be that of the argument.
     */
    Paragraph<PS, SEG, S> concatR(Paragraph<PS, SEG, S> that) {
        return this.length() == 0 && that.length() == 0
            ? that
            : concat(that);
    }

    public Paragraph<PS, SEG, S> append(String str) {
        if(str.length() == 0) {
            return this;
        }

        List<SEG> segs = new ArrayList<>(segments);
        int lastIdx = segments.size() - 1;
        segs.set(lastIdx, segmentOps.append(segments.get(lastIdx), str));
        return new Paragraph<>(paragraphStyle, segmentOps, segs);
    }

    public Paragraph<PS, SEG, S> insert(int offset, CharSequence str) {
        if(offset < 0 || offset > length()) {
            throw new IndexOutOfBoundsException(String.valueOf(offset));
        }

        Position pos = navigator.offsetToPosition(offset, Backward);
        int segIdx = pos.getMajor();
        int segPos = pos.getMinor();
        SEG seg = segments.get(segIdx);
        SEG replacement = segmentOps.spliced(seg, segPos, segPos, str);
        List<SEG> segs = new ArrayList<>(segments);
        segs.set(segIdx, replacement);
        return new Paragraph<>(paragraphStyle, segmentOps, segs);
    }

    public Paragraph<PS, SEG, S> subSequence(int start, int end) {
        return trim(end).subSequence(start);
    }

    public Paragraph<PS, SEG, S> trim(int length) {
        if(length >= length()) {
            return this;
        } else {
            Position pos = navigator.offsetToPosition(length, Backward);
            int segIdx = pos.getMajor();
            List<SEG> segs = new ArrayList<>(segIdx + 1);
            segs.addAll(segments.subList(0, segIdx));
            segs.add(segmentOps.subSequence(segments.get(segIdx), 0, pos.getMinor()));
            return new Paragraph<>(paragraphStyle, segmentOps, segs);
        }
    }

    public Paragraph<PS, SEG, S> subSequence(int start) {
        if(start < 0) {
            throw new IllegalArgumentException("start must not be negative (was: " + start + ")");
        } else if(start == 0) {
            return this;
        } else if(start <= length()) {
            Position pos = navigator.offsetToPosition(start, Forward);
            int segIdx = pos.getMajor();
            List<SEG> segs = new ArrayList<>(segments.size() - segIdx);
            segs.add(segmentOps.subSequence(segments.get(segIdx), pos.getMinor()));
            segs.addAll(segments.subList(segIdx + 1, segments.size()));
            return new Paragraph<>(paragraphStyle, segmentOps, segs);
        } else {
            throw new IndexOutOfBoundsException(start + " not in [0, " + length() + "]");
        }
    }

    public Paragraph<PS, SEG, S> delete(int start, int end) {
        return trim(start).concat(subSequence(end));
    }

    public Paragraph<PS, SEG, S> restyle(S style) {
        return new Paragraph<>(paragraphStyle, segmentOps, getText(), style);
    }

    public Paragraph<PS, SEG, S> restyle(int from, int to, S style) {
        if(from >= length()) {
            return this;
        } else {
            to = Math.min(to, length());
            Paragraph<PS, SEG, S> left = subSequence(0, from);
            Paragraph<PS, SEG, S> middle = new Paragraph<>(paragraphStyle, segmentOps, substring(from, to), style);
            Paragraph<PS, SEG, S> right = subSequence(to);
            return left.concat(middle).concat(right);
        }
    }

    public Paragraph<PS, SEG, S> restyle(int from, StyleSpans<? extends S> styleSpans) {
        int len = styleSpans.length();
        if(styleSpans.equals(getStyleSpans(from, from + len))) {
            return this;
        }

        Paragraph<PS, SEG, S> left = trim(from);
        Paragraph<PS, SEG, S> right = subSequence(from + len);

        String middleString = substring(from, from + len);
        List<SEG> middleSegs = new ArrayList<>(styleSpans.getSpanCount());
        int offset = 0;
        for(StyleSpan<? extends S> span: styleSpans) {
            int end = offset + span.getLength();
            String text = middleString.substring(offset, end);
            middleSegs.add(segmentOps.create(text, span.getStyle()));
            offset = end;
        }
        Paragraph<PS, SEG, S> middle = new Paragraph<>(paragraphStyle, segmentOps, middleSegs);

        return left.concat(middle).concat(right);
    }

    public Paragraph<PS, SEG, S> setParagraphStyle(PS paragraphStyle) {
        return new Paragraph<>(paragraphStyle, segmentOps, segments);
    }

    /**
     * Returns the style of character with the given index.
     * If {@code charIdx < 0}, returns the style at the beginning of this paragraph.
     * If {@code charIdx >= this.length()}, returns the style at the end of this paragraph.
     */
    public S getStyleOfChar(int charIdx) {
        if(charIdx < 0) {
            return segmentOps.getStyle(segments.get(0));
        }

        Position pos = navigator.offsetToPosition(charIdx, Forward);
        return segmentOps.getStyle(segments.get(pos.getMajor()));
    }

    /**
     * Returns the style at the given position. That is the style of the
     * character immediately preceding {@code position}. If {@code position}
     * is 0, then the style of the first character (index 0) in this paragraph
     * is returned. If this paragraph is empty, then some style previously used
     * in this paragraph is returned.
     * If {@code position > this.length()}, then it is equivalent to
     * {@code position == this.length()}.
     *
     * <p>In other words, {@code getStyleAtPosition(p)} is equivalent to
     * {@code getStyleOfChar(p-1)}.
     */
    public S getStyleAtPosition(int position) {
        if(position < 0) {
            throw new IllegalArgumentException("Paragraph position cannot be negative (" + position + ")");
        }

        Position pos = navigator.offsetToPosition(position, Backward);
        return segmentOps.getStyle(segments.get(pos.getMajor()));
    }

    /**
     * Returns the range of homogeneous style that includes the given position.
     * If {@code position} points to a boundary between two styled ranges,
     * then the range preceding {@code position} is returned.
     */
    public IndexRange getStyleRangeAtPosition(int position) {
        Position pos = navigator.offsetToPosition(position, Backward);
        int start = position - pos.getMinor();
        int end = start + segmentOps.length(segments.get(pos.getMajor()));
        return new IndexRange(start, end);
    }

    public StyleSpans<S> getStyleSpans() {
        StyleSpansBuilder<S> builder = new StyleSpansBuilder<>(segments.size());
        for(SEG seg: segments) {
            builder.add(segmentOps.getStyle(seg), 
                        segmentOps.length(seg));
        }
        return builder.create();
    }

    public StyleSpans<S> getStyleSpans(int from, int to) {
        Position start = navigator.offsetToPosition(from, Forward);
        Position end = to == from
                ? start
                : start.offsetBy(to - from, Backward);
        int startSegIdx = start.getMajor();
        int endSegIdx = end.getMajor();

        int n = endSegIdx - startSegIdx + 1;
        StyleSpansBuilder<S> builder = new StyleSpansBuilder<>(n);

        if(startSegIdx == endSegIdx) {
            SEG seg = segments.get(startSegIdx);
            builder.add(segmentOps.getStyle(seg), to - from);
        } else {
            SEG startSeg = segments.get(startSegIdx);
            builder.add(segmentOps.getStyle(startSeg), segmentOps.length(startSeg) - start.getMinor());

            for(int i = startSegIdx + 1; i < endSegIdx; ++i) {
                SEG seg = segments.get(i);
                builder.add(segmentOps.getStyle(seg), 
                            segmentOps.length(seg));
            }

            SEG endSeg = segments.get(endSegIdx);
            builder.add(segmentOps.getStyle(endSeg), end.getMinor());
        }

        return builder.create();
    }

    private String text = null;
    /**
     * Returns the plain text content of this paragraph,
     * not including the line terminator.
     */
    public String getText() {
        if(text == null) {
            StringBuilder sb = new StringBuilder(length());
            for(SEG seg: segments)
                sb.append(segmentOps.getText(seg));
            text = sb.toString();
        }
        return text;
    }

    @Override
    public String toString() {
        return
                "Par[" + paragraphStyle  + "; " +
                segments.stream().map(segmentOps::toString)
                        .reduce((s1, s2) -> s1 + "," + s2).orElse("") +
                "]";
    }

    @Override
    public boolean equals(Object other) {
        if(other instanceof Paragraph) {
            Paragraph<?, ?, ?> that = (Paragraph<?, ?, ?>) other;
            return Objects.equals(this.paragraphStyle, that.paragraphStyle)
                && Objects.equals(this.segments, that.segments);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(paragraphStyle, segments);
    }

}
