package org.fxmisc.richtext.demo.richtext;

import javafx.scene.text.Text;

public class WhiteSpaceNode extends Text {
    private WhiteSpaceType type;

    WhiteSpaceNode(WhiteSpaceType type) {
        super(type.getText());
        this.type = type;
    }

    public WhiteSpaceType getType() {
        return type;
    }
}