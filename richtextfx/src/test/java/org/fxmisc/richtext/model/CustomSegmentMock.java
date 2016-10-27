package org.fxmisc.richtext.model;

import java.util.Optional;



/**
 * A mock for a custom segment type which can be used in unit tests.
 */
public class CustomSegmentMock<S> {

    
    public static <S> SegmentOps<CustomSegmentMock<S>, S> segmentOps() {

        return new SegmentOps<CustomSegmentMock<S>, S>() {

            @Override
            public int length(CustomSegmentMock<S> seg) {
                return 1;
            }

            @Override
            public char charAt(CustomSegmentMock<S> seg, int index) {
                return 'x';
            }

            @Override
            public String getText(CustomSegmentMock<S> seg) {
                return "x";
            }

            @Override
            public Optional<CustomSegmentMock<S>> subSequence(CustomSegmentMock<S> seg, int start, int end) {
                return start < length(seg) && end > 0
                        ? Optional.of(seg)
                        : Optional.empty();
            }

            @Override
            public Optional<CustomSegmentMock<S>> subSequence(CustomSegmentMock<S> seg, int start) {
                return start < length(seg)
                        ? Optional.of(seg)
                        : Optional.empty();
            }

            @Override
            public S getStyle(CustomSegmentMock<S> seg) {
                return seg.style;
            }

            @Override
            public CustomSegmentMock<S> setStyle(CustomSegmentMock<S> seg, S style) {
                return new CustomSegmentMock<>(style);
            }

            @Override
            public Optional<CustomSegmentMock<S>> join(CustomSegmentMock<S> currentSeg, CustomSegmentMock<S> nextSeg) {
                return Optional.empty();
            }
        };
    }

    private S style;

    public CustomSegmentMock(S style) {
        this.style = style;
    }
}
