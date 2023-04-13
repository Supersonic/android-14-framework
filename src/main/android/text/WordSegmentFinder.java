package android.text;

import android.icu.util.ULocale;
import android.text.method.WordIterator;
/* loaded from: classes3.dex */
public class WordSegmentFinder extends SegmentFinder {
    private final CharSequence mText;
    private final WordIterator mWordIterator;

    public WordSegmentFinder(CharSequence text, ULocale locale) {
        this.mText = text;
        WordIterator wordIterator = new WordIterator(locale);
        this.mWordIterator = wordIterator;
        wordIterator.setCharSequence(text, 0, text.length());
    }

    public WordSegmentFinder(CharSequence text, WordIterator wordIterator) {
        this.mText = text;
        this.mWordIterator = wordIterator;
    }

    @Override // android.text.SegmentFinder
    public int previousStartBoundary(int offset) {
        int boundary = offset;
        do {
            boundary = this.mWordIterator.prevBoundary(boundary);
            if (boundary == -1) {
                return -1;
            }
        } while (Character.isWhitespace(this.mText.charAt(boundary)));
        return boundary;
    }

    @Override // android.text.SegmentFinder
    public int previousEndBoundary(int offset) {
        int boundary = offset;
        do {
            boundary = this.mWordIterator.prevBoundary(boundary);
            if (boundary == -1 || boundary == 0) {
                return -1;
            }
        } while (Character.isWhitespace(this.mText.charAt(boundary - 1)));
        return boundary;
    }

    @Override // android.text.SegmentFinder
    public int nextStartBoundary(int offset) {
        int boundary = offset;
        do {
            boundary = this.mWordIterator.nextBoundary(boundary);
            if (boundary == -1 || boundary == this.mText.length()) {
                return -1;
            }
        } while (Character.isWhitespace(this.mText.charAt(boundary)));
        return boundary;
    }

    @Override // android.text.SegmentFinder
    public int nextEndBoundary(int offset) {
        int boundary = offset;
        do {
            boundary = this.mWordIterator.nextBoundary(boundary);
            if (boundary == -1) {
                return -1;
            }
        } while (Character.isWhitespace(this.mText.charAt(boundary - 1)));
        return boundary;
    }
}
