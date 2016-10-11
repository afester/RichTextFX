package org.fxmisc.richtext.model;

import static org.junit.Assert.*;
import org.junit.Test;

public class ParagraphTest {

    // Tests that when concatenating two paragraphs,
    // the style of the first one is used for the result.
    // This relates to merging text changes and issue #216.
    @Test
    public void concatEmptyParagraphsTest() {
        SegmentOps<StyledText<Boolean>, Boolean> segOps = new StyledTextOps<>();
        Paragraph<Void, StyledText<Boolean>, Boolean> p1 = new Paragraph<>(null, segOps, "", true);
        Paragraph<Void, StyledText<Boolean>, Boolean> p2 = new Paragraph<>(null, segOps, "", false);

        Paragraph<Void, StyledText<Boolean>, Boolean> p = p1.concat(p2);

        assertEquals(Boolean.TRUE, p.getStyleAtPosition(0));
    }

}
