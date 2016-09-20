package org.fxmisc.richtext.model;


/**
 * Defines the operations which are supported on a specific segment type.
 *
 * @param <SEG>
 * @param <S>
 */
public interface SegmentOps<SEG, S> {
    public int length(SEG seg);

    public char charAt(SEG seg, int index);

    public String getText(SEG seg);

    public SEG subSequence(SEG seg, int start, int end);

    public SEG subSequence(SEG seg, int start);

    public SEG append(SEG seg, String str);

    public SEG spliced(SEG seg, int from, int to, CharSequence replacement);

    public S getStyle(SEG seg);

    public SEG create(String text, S style);
}
