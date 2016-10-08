package org.fxmisc.richtext;

import org.fxmisc.richtext.model.SegmentOps;

import java.util.function.Function;

/**
 * Text area that uses inline css derived from the style info to define
 * style of text segments.
 *
 * @param <S> type of style information.
 * @deprecated
 */
@Deprecated
public class InlineStyleTextArea<PS, SEG, S> extends StyledTextArea<PS, SEG, S> {

    /**
     *
     * @param initialStyle style to use for text ranges where no other
     *     style is set via {@code setStyle(...)} methods.
     * @param styleToCss function that converts an instance of {@code S}
     *     to a CSS string.
     */
    public InlineStyleTextArea(PS initialParagraphStyle, Function<PS, String> paragraphStyleToCss, 
    						   S initialStyle, SegmentOps<SEG, S> segOps, Function<S, String> styleToCss) {
        super(initialParagraphStyle,                                                        // initial paragraph style
              (paragraph, style) -> paragraph.setStyle(paragraphStyleToCss.apply(style)),   // paragraph style setter
              initialStyle,                                                                 // initial segment style
              //(text, style) -> text.setStyle(styleToCss.apply(style)),
              segOps, 
              seg -> createStyledTextNode(seg, 
            		  					  segOps, 
            		  					  (text, style) -> text.setStyle(styleToCss.apply(style)))
              //                       // Segment node creator and segment style setter 
        );
    }
}
