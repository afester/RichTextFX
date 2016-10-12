package org.fxmisc.richtext.model;



/**
 * A list of paragraphs.
 * Each paragraph in the list is indented by a specific indent as specified by its list format.
 * Paragraphs in the list do not need to be adjacent - paragraphs with deeper indent
 * would be part of yet another list.
 */
public class ParagraphList {

    private ParagraphListFormat theFormat;
    
    public ParagraphListFormat getFormat() {
        return theFormat;
    }
}
