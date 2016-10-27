package org.fxmisc.richtext.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.hamcrest.core.IsInstanceOf.*;
import static org.hamcrest.MatcherAssert.*;

import org.junit.Test;
import org.reactfx.util.Either;

public class ParagraphTest {

    // Tests that when concatenating two paragraphs,
    // the style of the first one is used for the result.
    // This relates to merging text changes and issue #216.
    @Test
    public void concatEmptyParagraphsTest() {
        TextOps<StyledText<Boolean>, Boolean> segOps = StyledText.textOps();
        Paragraph<Void, StyledText<Boolean>, Boolean> p1 = new Paragraph<>(null, segOps, segOps.create("", true));
        Paragraph<Void, StyledText<Boolean>, Boolean> p2 = new Paragraph<>(null, segOps, segOps.create("", false));

        Paragraph<Void, StyledText<Boolean>, Boolean> p = p1.concat(p2);

        assertEquals(Boolean.TRUE, p.getStyleAtPosition(0));
    }


    @Test
    public void assureOneSegment() {
        // Assemble the supported operations (ops) as Either Text- or CustomSegmentMock operations
        TextOps<StyledText<Boolean>, Boolean> textOps = StyledText.textOps();
        SegmentOps<CustomSegmentMock<Boolean>, Boolean> customOps = CustomSegmentMock.segmentOps();
        TextOps<Either<StyledText<Boolean>, CustomSegmentMock<Boolean>>, Boolean> ops = textOps._or(customOps);

        // Create a new Paragraph with exactly one segment of type CustomSegmentMock 
        Paragraph<Boolean, Either<StyledText<Boolean>, CustomSegmentMock<Boolean>>, Boolean> p1 = 
                new Paragraph<>(null, ops, Either.right(new CustomSegmentMock<>(true)));

        assertEquals(1, p1.getSegments().size());
        assertTrue(p1.getSegments().get(0).isRight());
        assertThat(p1.getSegments().get(0).getRight(), instanceOf(CustomSegmentMock.class));

        // Create a new (empty) Paragraph as a sub-Paragraph of p1 starting after the first (and only) 
        // character in the first paragraph 
        Paragraph<Boolean, Either<StyledText<Boolean>, CustomSegmentMock<Boolean>>, Boolean> p2 =
                p1.subSequence(1);

        // Even this new, empty paragraph needs to contain one (empty) segment
        assertEquals(1, p2.getSegments().size());
    }
}
