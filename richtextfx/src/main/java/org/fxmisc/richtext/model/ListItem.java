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


    public ListItem prevLevel() {
        ListItem newItem = null;

        int level = indent - 1;
        if (level != 0) {
            newItem = new ListItem(level, showBullet);
        }

        return newItem;
    }

    /**
     * 
     * @return
     */
    public ListItem nextLevel() {
        return new ListItem(indent + 1, showBullet);
    }
}
