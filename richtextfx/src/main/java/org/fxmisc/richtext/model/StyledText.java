package org.fxmisc.richtext.model;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

public class StyledText<S>  {

    public static <S> TextOps<StyledText<S>, S> textOps(S defaultStyle) {
        return new TextOps<StyledText<S>, S>() {

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
            public Optional<StyledText<S>> subSequence(StyledText<S> styledText, int start, int end) {
                return Optional.of(new StyledText<>(styledText.getText().substring(start, end), styledText.getStyle()));
            }

            @Override
            public Optional<StyledText<S>> subSequence(StyledText<S> styledText, int start) {
                 return Optional.of(new StyledText<>(styledText.getText().substring(start), styledText.getStyle()));
            }

            @Override
            public S defaultStyle() {
                return defaultStyle;
            }

            @Override
            public S getStyle(StyledText<S> styledText) {
                return styledText.getStyle();
            }

            @Override
            public StyledText<S> setStyle(StyledText<S> seg, S style) {
                return seg.setStyle(style);
            }

            @Override
            public Optional<StyledText<S>> join(StyledText<S> left, StyledText<S> right) {
                return Objects.equals(left.getStyle(), right.getStyle())
                        ? Optional.of(new StyledText<>(left.getText() + right.getText(), left.getStyle()))
                        : Optional.empty();
            }

            @Override
            public StyledText<S> create(String text, S style) {
                return new StyledText<>(text, style);
            }
        };
    }

    public static <S> Codec<StyledText<S>> codec(Codec<S> styleCodec) {
        return new Codec<StyledText<S>>() {

            @Override
            public String getName() {
                return "styled-text";
            }

            @Override
            public void encode(DataOutputStream os, StyledText<S> t) throws IOException {
                Codec.STRING_CODEC.encode(os, t.text);
                styleCodec.encode(os, t.style);
            }

            @Override
            public StyledText<S> decode(DataInputStream is) throws IOException {
                String text = Codec.STRING_CODEC.decode(is);
                S style = styleCodec.decode(is);
                return new StyledText<>(text, style);
            }

        };
    }


    private final String text;

    public String getText() { return text; }

    private final S style;
    public S getStyle() { return style; }

    public StyledText<S> setStyle(S style) {
        return new StyledText<>(text, style);
    }

    public StyledText(String text, S style) {
        this.text = text;
        this.style = style;
    }

    @Override
    public String toString() {
        return String.format("StyledText[text=\"%s\", style=%s]", text, style);
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
