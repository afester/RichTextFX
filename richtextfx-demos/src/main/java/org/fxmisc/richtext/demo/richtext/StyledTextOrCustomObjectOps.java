package org.fxmisc.richtext.demo.richtext;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.fxmisc.richtext.model.Codec;
import org.fxmisc.richtext.model.SegmentOps;
import org.fxmisc.richtext.model.StyledText;
import org.fxmisc.richtext.model.StyledTextOps;
import org.reactfx.util.Either;

public class StyledTextOrCustomObjectOps {

    public static <L, R, S> SegmentOps<Either<StyledText<S>, CustomObject<S>>, S> eitherOps(StyledTextOps<S> lOps, CustomObjectOps<S> rOps) {
        return new SegmentOps<Either<StyledText<S>, CustomObject<S>>, S>() {

            @Override
            public int length(Either<StyledText<S>, CustomObject<S>> seg) {
                return seg.isLeft() ? lOps.length(seg.getLeft()) 
                                    : rOps.length(seg.getRight());
            }

            @Override
            public char charAt(Either<StyledText<S>, CustomObject<S>> seg, int index) {
                return seg.isLeft() ? lOps.charAt(seg.getLeft(), index) 
                                    : rOps.charAt(seg.getRight(), index);
            }

            @Override
            public String getText(Either<StyledText<S>, CustomObject<S>> seg) {
                return seg.isLeft() ? lOps.getText(seg.getLeft()) 
                                    : rOps.getText(seg.getRight());
            }

            @Override
            public Either<StyledText<S>, CustomObject<S>> subSequence(Either<StyledText<S>, CustomObject<S>> seg, int start, int end) {
                if (seg.isLeft()) {
                    return Either.left(lOps.subSequence(seg.getLeft(), start, end)); 
                }
                
                if (start == 0 && end == 1) {
                    return Either.right(rOps.subSequence(seg.getRight(), start, end));
                }
                
                return Either.left(lOps.subSequence(seg.getLeft(), start, end));
            }

            @Override
            public Either<StyledText<S>, CustomObject<S>> subSequence(Either<StyledText<S>, CustomObject<S>> seg, int start) {
                if (seg.isLeft()) {
                    return Either.left(lOps.subSequence(seg.getLeft(), start)); 
                }

                if (start == 1) {
                    return Either.left(lOps.subSequence(seg.getLeft(), start));
                }
                return Either.right(rOps.subSequence(seg.getRight(), start));
            }

            @Override
            public Either<StyledText<S>, CustomObject<S>> append(Either<StyledText<S>, CustomObject<S>> seg, String str) {
                return seg.isLeft() ? Either.left(lOps.append(seg.getLeft(), str))
                                    : Either.right(rOps.append(seg.getRight(), str));
            }

            @Override
            public Either<StyledText<S>, CustomObject<S>> spliced(Either<StyledText<S>, CustomObject<S>> seg, int from, int to, CharSequence replacement) {
                return seg.isLeft() ? Either.left(lOps.spliced(seg.getLeft(), from, to, replacement))
                                    : Either.right(rOps.spliced(seg.getRight(), from, to, replacement));
            }

            @Override
            public S getStyle(Either<StyledText<S>, CustomObject<S>> seg) {
                return seg.isLeft() ? lOps.getStyle(seg.getLeft()) 
                                    : rOps.getStyle(seg.getRight());
            }


            @Override
            public String toString(Either<StyledText<S>, CustomObject<S>> seg) {
                return seg.isLeft() ? lOps.toString(seg.getLeft()) 
                                    : rOps.toString(seg.getRight());
            }


            @Override
            public Either<StyledText<S>, CustomObject<S>> create(String text, S style) {
                return Either.left(lOps.create(text, style));
            }


            @Override
            public void encode(Either<StyledText<S>, CustomObject<S>> seg, DataOutputStream os, Codec<S> styleCodec) throws IOException {
                // we need a type id to be able to recreate the object later
                if (seg.isLeft()) {
                    Codec.STRING_CODEC.encode(os, seg.getLeft().getClass().getName());
                    lOps.encode(seg.getLeft(), os, styleCodec);
                } else {
                    Codec.STRING_CODEC.encode(os, seg.getRight().getClass().getName());
                    rOps.encode(seg.getRight(), os, styleCodec);
                }
            }

            @Override
            public Either<StyledText<S>, CustomObject<S>> decode(DataInputStream is, Codec<S> styleCodec) throws IOException {
                String segmentType = Codec.STRING_CODEC.decode(is);
                try {
                    Class<?> segmentClass = Class.forName(segmentType);
                    if (segmentClass.isAssignableFrom(StyledText.class)) {
                        return Either.left(lOps.decode(is, styleCodec));
                    }

                    return Either.right(rOps.decode(segmentClass, is, styleCodec));
                } catch (ClassNotFoundException e) {
                    throw new IOException("", e);
                }
            }

            @Override
            public void setStyle(Either<StyledText<S>, CustomObject<S>> seg, S style) {
                if (seg.isLeft()) {
                    lOps.setStyle(seg.getLeft(), style);
                } else {
                    rOps.setStyle(seg.getRight(), style);
                }
            }

            @Override
            public boolean canJoin(Either<StyledText<S>, CustomObject<S>> left,
                                   Either<StyledText<S>, CustomObject<S>> right) {
                if (left.isRight() || right.isRight()) {
                    return false;
                }

                return lOps.canJoin(left.getLeft(), right.getLeft());
            }
        };
    }
}
