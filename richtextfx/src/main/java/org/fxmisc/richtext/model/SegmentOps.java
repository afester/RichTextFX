package org.fxmisc.richtext.model;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;


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

    public SEG setStyle(SEG seg, S style);

    public String toString(SEG seg);

    public SEG create(/*Class<?> clazz, */String text, S style);

    public void encode(SEG seg, DataOutputStream os, Codec<S> styleCodec) throws IOException;

    public SEG decode(DataInputStream is, Codec<S> styleCodec) throws IOException;

    public boolean canJoin(SEG currentSeg, SEG nextSeg);
}
