package org.fxmisc.richtext.model;

import java.util.Objects;

public class StyledText<S>  {
    private final String text;
    public String getText() { return text; }

    private final S style;
    public S getStyle() { return style; }

    public StyledText(String text, S style) {
        this.text = text;
        this.style = style;
    }

    @Override
    public String toString() {
        return '"' + text + '"' + ":" + style;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof StyledText) {
            StyledText<?> that = (StyledText<?>) obj;
            return Objects.equals(this.text, that.text)
                && Objects.equals(this.style, that.style);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(text, style);
    }
}
