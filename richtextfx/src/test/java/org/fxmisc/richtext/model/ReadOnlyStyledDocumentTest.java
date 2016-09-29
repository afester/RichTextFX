package org.fxmisc.richtext.model;

import static org.fxmisc.richtext.model.ReadOnlyStyledDocument.*;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.*;

import java.util.List;
import org.junit.Test;

public class ReadOnlyStyledDocumentTest {

    @Test
    public void testUndo() {
        ReadOnlyStyledDocument<String, String> doc0 = fromString("", "X", "X");

        doc0.replace(0, 0, fromString("abcd", "Y", "Y")).exec((doc1, chng1, pchng1) -> {
            // undo chng1
            doc1.replace(chng1.getPosition(), chng1.getInsertionEnd(), from(chng1.getRemoved())).exec((doc2, chng2, pchng2) -> {
                // we should have arrived at the original document
                assertEquals(doc0, doc2);

                // chng2 should be the inverse of chng1
                assertEquals(chng1.invert(), chng2);
            });
        });
    }

    @Test
    public void deleteNewlineTest() {
        ReadOnlyStyledDocument<Void, Void> doc0 = fromString("Foo\nBar", null, null);
        doc0.replace(3, 4, fromString("", null, null)).exec((doc1, ch, pch) -> {
            List<? extends Paragraph<Void, Void>> removed = pch.getRemoved();
            List<? extends Paragraph<Void, Void>> added = pch.getAdded();
            assertEquals(2, removed.size());
            assertEquals(new Paragraph<Void, Void>(null, "Foo", null), removed.get(0));
            assertEquals(new Paragraph<Void, Void>(null, "Bar", null), removed.get(1));
            assertEquals(1, added.size());
            assertEquals(new Paragraph<Void, Void>(null, "FooBar", null), added.get(0));
        });
    }

    @Test
    public void testRestyle() {
        final String fooBar = "Foo Bar";
        final String and = " and ";
        final String helloWorld = "Hello World";
        
        SimpleEditableStyledDocument<String, String> doc0 = new SimpleEditableStyledDocument<>("", ""); //fromString(fooBar, "", "bold");

        ReadOnlyStyledDocument<String, String> text = fromString(fooBar, "", "bold");
        doc0.replace(doc0.getLength(),  doc0.getLength(), text);

        text = fromString(and, "", "");
        doc0.replace(doc0.getLength(),  doc0.getLength(), text);

        text = fromString(helloWorld, "", "bold");
        doc0.replace(doc0.getLength(),  doc0.getLength(), text);

        System.err.println("####");
        for (Paragraph<String, String> par : doc0.getParagraphs()) {
          for (Segment<String> seg : par.getSegments()) {
            System.err.println(seg);
          }
        }
        System.err.println("####\n");

        StyleSpans<String> styles = doc0.getStyleSpans(4,  17);
        assertThat("Invalid number of Spans", styles.getSpanCount(), equalTo(3));
        System.err.println(styles);

        StyleSpans<String> newStyles = styles.mapStyles(style -> "italic");
        System.err.println(newStyles);

        doc0.setStyleSpans(4, newStyles);

        System.err.println("####");
        for (Paragraph<String, String> par : doc0.getParagraphs()) {
          for (Segment<String> seg : par.getSegments()) {
            System.err.println(seg);
          }
        }
        System.err.println("####\n");
    }
    
}
