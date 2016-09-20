package org.fxmisc.richtext.model;

import java.util.Objects;

public class StyledText<S> {
    final String text;
    final S style;


    public StyledText(String text, S style) {
      this.text = text;
      this.style = style;
   }


    @Override
    public boolean equals(Object obj) {
        if(obj instanceof StyledText) {
            StyledText<?> that = (StyledText<?>) obj;
            return Objects.equals(text, that.text)
                && Objects.equals(style, that.style);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(text, style);
    }
    
    @Override
    public String toString() {
        return '"' + text + '"' + ":" + style;
    }

}
