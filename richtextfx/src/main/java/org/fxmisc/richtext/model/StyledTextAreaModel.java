package org.fxmisc.richtext.model;


/**
 * Model for {@link org.fxmisc.richtext.StyledTextArea}
 *
 * @param <S> type of style that can be applied to text.
 * @param <PS> type of style that can be applied to Paragraph
 */
public class StyledTextAreaModel<PS, S> extends GenericStyledTextAreaModel<PS, StyledText<S>, S> {

    public StyledTextAreaModel(PS initialParagraphStyle, S initialTextStyle,
            EditableStyledDocument<PS, StyledText<S>, S> document, TextOps<StyledText<S>, S> textOps,
            boolean preserveStyle) {
        super(initialParagraphStyle, initialTextStyle, document, textOps, preserveStyle);
    }

    public StyledTextAreaModel(PS initialParagraphStyle, S initialTextStyle,
            EditableStyledDocument<PS, StyledText<S>, S> document, TextOps<StyledText<S>, S> textOps) {
        super(initialParagraphStyle, initialTextStyle, document, textOps);
    }

    public StyledTextAreaModel(PS initialParagraphStyle, S initialTextStyle, TextOps<StyledText<S>, S> segmentOps,
            boolean preserveStyle) {
        super(initialParagraphStyle, initialTextStyle, segmentOps, preserveStyle);
    }

    public StyledTextAreaModel(PS initialParagraphStyle, S initialTextStyle, TextOps<StyledText<S>, S> segmentOps) {
        super(initialParagraphStyle, initialTextStyle, segmentOps);
    }  
}
