package org.fxmisc.richtext.demo.richtext;

public enum WhiteSpaceType {
    SPACE("\u00B7"),
    TAB("\u00BB"),   // "\u2192"; // needs addtl. computation to clip the arrow!
    EOL("\u00B6");

    final String character;

    WhiteSpaceType(String character) {
        this.character = character;
    }

    String getText() {
        return character;
    }
}
