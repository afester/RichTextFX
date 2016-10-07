package org.fxmisc.richtext;

import java.util.function.BiConsumer;

import org.fxmisc.richtext.model.StyledText;

import javafx.geometry.VPos;
import javafx.scene.Node;

public class StyledTextNodeFactory<S> {

    public Node createNode(StyledText<S> seg, BiConsumer<? super TextExt, S> applyStyle) {

        TextExt t = new TextExt(seg.getText());
        t.setTextOrigin(VPos.TOP);
        t.getStyleClass().add("text");
        applyStyle.accept(t, seg.getStyle());

        // XXX: binding selectionFill to textFill,
        // see the note at highlightTextFill
        t.impl_selectionFillProperty().bind(t.fillProperty());
        return t;
    }
}
