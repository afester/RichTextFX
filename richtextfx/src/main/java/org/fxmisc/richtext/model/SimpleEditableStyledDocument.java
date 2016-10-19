package org.fxmisc.richtext.model;

public final class SimpleEditableStyledDocument<PS, S> extends GenericEditableStyledDocument<PS, StyledText<S>, S> {

    public SimpleEditableStyledDocument(PS initialParagraphStyle, S initialStyle) {
        super(initialParagraphStyle, initialStyle, StyledText.textOps());
    }
}
