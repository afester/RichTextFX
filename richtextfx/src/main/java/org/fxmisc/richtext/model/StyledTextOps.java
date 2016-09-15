package org.fxmisc.richtext.model;

import java.util.Objects;

public interface StyledTextOps<SEG> {
    public int length(SEG seg);

    public char charAt(SEG seg, int index);

    public String getText(SEG seg);

    public SEG subSequence(SEG seg, int start, int end);

    public SEG subSequence(SEG seg, int start);

    public SEG append(SEG seg, String str);

    public SEG spliced(SEG seg, int from, int to, CharSequence replacement);

    public S getStyle();

//    @Override
//    public String toString();
//
//    @Override
//    public boolean equals(Object obj);
//
//    @Override
//    public int hashCode();
}
