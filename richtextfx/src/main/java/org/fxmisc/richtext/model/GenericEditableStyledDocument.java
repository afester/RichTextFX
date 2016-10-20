package org.fxmisc.richtext.model;

public final class GenericEditableStyledDocument<PS, SEG, S> extends GenericEditableStyledDocumentBase<PS, SEG, S>
        implements EditableStyledDocument<PS, SEG, S> {

    public GenericEditableStyledDocument(PS initialParagraphStyle, S initialStyle, TextOps<SEG, S> segmentOps) {
        super(initialParagraphStyle, initialStyle, segmentOps);
    }

}
