package org.fxmisc.richtext.model;

public class ListItem {

    private final int indent;
    private final boolean showBullet;
    
    public ListItem(int indent, boolean showBullet) {
        this.indent = indent;
        this.showBullet = showBullet;
    }

    public int getLevel() {
        return indent;
    }

    public boolean isShowBullet() {
        return showBullet;
    }

    @Override
    public String toString() {
        return String.format("ListItem[indent=%s, showBullet=%s]", indent, showBullet);
    }

}
