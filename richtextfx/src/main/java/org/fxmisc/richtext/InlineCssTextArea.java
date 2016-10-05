package org.fxmisc.richtext;


import org.fxmisc.richtext.model.Codec;
import org.fxmisc.richtext.model.EditableStyledDocument;
import org.fxmisc.richtext.model.SegmentOps;
import org.fxmisc.richtext.model.SimpleEditableStyledDocument;

import javafx.scene.text.TextFlow;
import org.omg.CORBA.Object;

/**
 * Text area that uses inline css to define style of text segments and paragraph segments.
 */
public class InlineCssTextArea extends StyledTextArea<String, Object, String> {

    public InlineCssTextArea(SegmentOps<Object, String> segOps) {
        this(new SimpleEditableStyledDocument<>("", "", segOps));
    }

    public InlineCssTextArea(EditableStyledDocument<String, Object, String> document) {
        super(
                "", TextFlow::setStyle,
                "", TextExt::setStyle,
                document,
                true
        );
    }

    /**
     * Creates a text area with initial text content.
     * Initial caret position is set at the beginning of text content.
     *
     * @param text Initial text content.
     */
    public InlineCssTextArea(String text, SegmentOps<Object, String> segOps) {
        this(segOps);

        replaceText(0, 0, text);
        getUndoManager().forgetHistory();
        getUndoManager().mark();

        setStyleCodecs(Codec.STRING_CODEC, Codec.STRING_CODEC);

        // position the caret at the beginning
        selectRange(0, 0);
    }
}
