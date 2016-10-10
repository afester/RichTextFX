package org.fxmisc.richtext.model;

import static org.fxmisc.richtext.model.ReadOnlyStyledDocument.*;
import static org.junit.Assert.*;

import java.util.List;
import org.junit.Test;

public class ReadOnlyStyledDocumentTest {

    @Test
    public void testUndo() {
        SegmentOps<StyledText<String>, String> segOps = new StyledTextOps<>();
        ReadOnlyStyledDocument<String, StyledText<String>, String> doc0 = fromString("", "X", "X", segOps);

        doc0.replace(0, 0, fromString("abcd", "Y", "Y", segOps)).exec((doc1, chng1, pchng1) -> {
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
        SegmentOps<StyledText<Void>, Void> segOps = new StyledTextOps<>();
        ReadOnlyStyledDocument<Void, StyledText<Void>, Void> doc0 = fromString("Foo\nBar", null, null, segOps);
        doc0.replace(3, 4, fromString("", null, null, segOps)).exec((doc1, ch, pch) -> {
            List<? extends Paragraph<Void, StyledText<Void>, Void>> removed = pch.getRemoved();
            List<? extends Paragraph<Void, StyledText<Void>, Void>> added = pch.getAdded();
            assertEquals(2, removed.size());
            assertEquals(new Paragraph<Void, StyledText<Void>, Void>(null, segOps, "Foo", null), removed.get(0));
            assertEquals(new Paragraph<Void, StyledText<Void>, Void>(null, segOps, "Bar", null), removed.get(1));
            assertEquals(1, added.size());
            assertEquals(new Paragraph<Void, StyledText<Void>, Void>(null, segOps, "FooBar", null), added.get(0));
        });
    }

}
