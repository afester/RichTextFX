package org.fxmisc.richtext;

import java.util.Collection;
import java.util.Collections;

import org.fxmisc.richtext.model.Codec;
import org.fxmisc.richtext.model.EditableStyledDocument;
import org.fxmisc.richtext.model.SegmentOps;
import org.fxmisc.richtext.model.SimpleEditableStyledDocument;
import org.fxmisc.richtext.model.StyledText;

import javafx.geometry.VPos;

/**
 * Text area that uses style classes to define style of text segments and paragraph segments.
 */
public class StyleClassedTextArea extends StyledTextArea<Collection<String>, StyledText<Collection<String>>, Collection<String>> {

    public StyleClassedTextArea(EditableStyledDocument<Collection<String>, StyledText<Collection<String>>, Collection<String>> document, boolean preserveStyle) {
        super(Collections.<String>emptyList(),                                              // default paragraph style
              (paragraph, styleClasses) -> paragraph.getStyleClass().addAll(styleClasses),   // paragraph style setter
              Collections.<String>emptyList(),                                            // default text style
              document, preserveStyle,
              seg -> { 
                  TextExt t = new TextExt(seg.getText());
                  t.setTextOrigin(VPos.TOP);
                  t.getStyleClass().add("text");
                  
                  t.getStyleClass().addAll(styleClasses);
                  // applyStyle.accept(t, seg.getStyle());

                  // XXX: binding selectionFill to textFill,
                  // see the note at highlightTextFill
                  t.impl_selectionFillProperty().bind(t.fillProperty());
                  return t;
              } );//,(text, styleClasses) -> text.getStyleClass().addAll(styleClasses),          //
                //StyledTextNodeFactory::createNode);                                         // Segment node creator and text style setter

        setStyleCodecs(
                Codec.collectionCodec(Codec.STRING_CODEC),
                Codec.collectionCodec(Codec.STRING_CODEC)
        );
    }
    
    public StyleClassedTextArea(SegmentOps<String, Collection<String>> segOps, boolean preserveStyle) {
        this(
                new SimpleEditableStyledDocument<>(
                    Collections.<String>emptyList(), Collections.<String>emptyList(), segOps
                ), preserveStyle);
    }

    /**
     * Creates a text area with empty text content.
     */
    public StyleClassedTextArea(SegmentOps<String, Collection<String>> segOps) {
        this(segOps, true);
    }

    /**
     * Convenient method to assign a single style class.
     */
    public void setStyleClass(int from, int to, String styleClass) {
        setStyle(from, to, Collections.singletonList(styleClass));
    }
}
