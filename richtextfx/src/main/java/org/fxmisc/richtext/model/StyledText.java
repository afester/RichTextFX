package org.fxmisc.richtext.model;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.Objects;

public class StyledText<S>  {
    private String text;
    public String getText() { return text; }

    private S style;
    public S getStyle() { return style; }

    StyledText() { }

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

    public void decode(DataInputStream is, Codec<S> styleCodec) throws IOException {
          text = Codec.STRING_CODEC.decode(is);
          style = styleCodec.decode(is);
    }
}
