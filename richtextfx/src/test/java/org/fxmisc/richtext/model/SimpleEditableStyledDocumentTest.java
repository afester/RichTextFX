package org.fxmisc.richtext.model;

import static org.junit.Assert.*;
import org.junit.Test;

public class SimpleEditableStyledDocumentTest {

    /**
     * The style of the inserted text will be the style at position
     * {@code start} in the current document.
     * @param ops 
     */
    private <PS, SEG, S> void replaceText(EditableStyledDocument<PS, SEG, S> doc, int start, int end, String text, SegmentOps<SEG, S> ops) {
        StyledDocument<PS, SEG, S> styledDoc = ReadOnlyStyledDocument.fromString(
                text, doc.getParagraphStyleAtPosition(start), doc.getStyleAtPosition(start), ops);
        doc.replace(start, end, styledDoc);
    }

    @Test
    public void testConsistencyOfTextWithLength() {
        final StyledTextOps<String> ops = new StyledTextOps<>();

        SimpleEditableStyledDocument<String, StyledText<String>, String> document = new SimpleEditableStyledDocument<>("", "", ops);
        document.getText(); // enforce evaluation of text property
        document.getLength(); // enforce evaluation of length property

        document.lengthProperty().addListener(obs -> {
            int length = document.getLength();
            int textLength = document.getText().length();
            assertEquals(length, textLength);
        });

        replaceText(document, 0, 0, "A", ops);
    }

    @Test
    public void testConsistencyOfLengthWithText() {
        final StyledTextOps<String> ops = new StyledTextOps<>();

        SimpleEditableStyledDocument<String, StyledText<String>, String> document = new SimpleEditableStyledDocument<>("", "", ops);
        document.getText(); // enforce evaluation of text property
        document.getLength(); // enforce evaluation of length property

        document.textProperty().addListener(obs -> {
            int textLength = document.getText().length();
            int length = document.getLength();
            assertEquals(textLength, length);
        });

        replaceText(document, 0, 0, "A", ops);
    }

    @Test
    public void testUnixParagraphCount() {
        final StyledTextOps<String> ops = new StyledTextOps<>();

        SimpleEditableStyledDocument<String, StyledText<String>, String> document = new SimpleEditableStyledDocument<>("", "", ops);
        String text = "X\nY";
        replaceText(document, 0, 0, text, ops);
        assertEquals(2, document.getParagraphs().size());
    }

    @Test
    public void testMacParagraphCount() {
        final StyledTextOps<String> ops = new StyledTextOps<>();

        SimpleEditableStyledDocument<String, StyledText<String>, String> document = new SimpleEditableStyledDocument<>("", "", ops);
        String text = "X\rY";
        replaceText(document, 0, 0, text, ops);
        assertEquals(2, document.getParagraphs().size());
    }

    @Test
    public void testWinParagraphCount() {
        final StyledTextOps<String> ops = new StyledTextOps<>();

        SimpleEditableStyledDocument<String, StyledText<String>, String> document = new SimpleEditableStyledDocument<>("", "", ops);
        String text = "X\r\nY";
        replaceText(document, 0, 0, text, ops);
        assertEquals(2, document.getParagraphs().size());
    }

    @Test
    public void testGetTextWithEndAfterNewline() {
        final StyledTextOps<String> ops = new StyledTextOps<>();

        SimpleEditableStyledDocument<Boolean, StyledText<String>, String> doc = new SimpleEditableStyledDocument<>(true, "", ops);

        replaceText(doc, 0, 0, "123\n", ops);
        String txt1 = doc.getText(0, 4);
        assertEquals(4, txt1.length());

        replaceText(doc, 4, 4, "567", ops);
        String txt2 = doc.getText(2, 4);
        assertEquals(2, txt2.length());

        replaceText(doc, 4, 4, "\n", ops);
        String txt3 = doc.getText(2, 4);
        assertEquals(2, txt3.length());
    }

    @Test
    public void testWinDocumentLength() {
        final StyledTextOps<String> ops = new StyledTextOps<>();

        SimpleEditableStyledDocument<String, StyledText<String>, String> document = new SimpleEditableStyledDocument<>("", "", ops);
        replaceText(document, 0, 0, "X\r\nY", ops);
        assertEquals(document.getText().length(), document.getLength());
    }
}
