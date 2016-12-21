package org.fxmisc.richtext.model;

public class ListItem {

    private int indent = 0;
    
    public ListItem(int indent) {
        this.indent = indent;
    }

    public int getLevel() {
        return indent;
    }
    
    @Override
    public String toString() {
        return String.format("ListItem[indent=%s]", indent);
    }

}
