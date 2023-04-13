package android.text.method;

import android.icu.lang.UCharacter;
import android.icu.text.BreakIterator;
import android.icu.util.ULocale;
import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.mtp.MtpConstants;
import android.text.CharSequenceCharacterIterator;
import android.text.Selection;
import android.text.TextUtils;
import java.util.Locale;
/* loaded from: classes3.dex */
public class WordIterator implements Selection.PositionIterator {
    private static final int WINDOW_WIDTH = 50;
    private CharSequence mCharSeq;
    private int mEnd;
    private final BreakIterator mIterator;
    private int mStart;

    public WordIterator() {
        this(Locale.getDefault());
    }

    public WordIterator(Locale locale) {
        this.mIterator = BreakIterator.getWordInstance(locale);
    }

    public WordIterator(ULocale locale) {
        this.mIterator = BreakIterator.getWordInstance(locale);
    }

    public void setCharSequence(CharSequence charSequence, int start, int end) {
        if (start >= 0 && end <= charSequence.length()) {
            this.mCharSeq = charSequence;
            this.mStart = Math.max(0, start - 50);
            int min = Math.min(charSequence.length(), end + 50);
            this.mEnd = min;
            this.mIterator.setText(new CharSequenceCharacterIterator(charSequence, this.mStart, min));
            return;
        }
        throw new IndexOutOfBoundsException("input indexes are outside the CharSequence");
    }

    @Override // android.text.Selection.PositionIterator
    public int preceding(int offset) {
        checkOffsetIsValid(offset);
        do {
            offset = this.mIterator.preceding(offset);
            if (offset == -1) {
                break;
            }
        } while (!isOnLetterOrDigit(offset));
        return offset;
    }

    @Override // android.text.Selection.PositionIterator
    public int following(int offset) {
        checkOffsetIsValid(offset);
        do {
            offset = this.mIterator.following(offset);
            if (offset == -1) {
                break;
            }
        } while (!isAfterLetterOrDigit(offset));
        return offset;
    }

    public boolean isBoundary(int offset) {
        checkOffsetIsValid(offset);
        return this.mIterator.isBoundary(offset);
    }

    public int nextBoundary(int offset) {
        checkOffsetIsValid(offset);
        return this.mIterator.following(offset);
    }

    public int prevBoundary(int offset) {
        checkOffsetIsValid(offset);
        return this.mIterator.preceding(offset);
    }

    public int getBeginning(int offset) {
        return getBeginning(offset, false);
    }

    public int getEnd(int offset) {
        return getEnd(offset, false);
    }

    public int getPrevWordBeginningOnTwoWordsBoundary(int offset) {
        return getBeginning(offset, true);
    }

    public int getNextWordEndOnTwoWordBoundary(int offset) {
        return getEnd(offset, true);
    }

    private int getBeginning(int offset, boolean getPrevWordBeginningOnTwoWordsBoundary) {
        checkOffsetIsValid(offset);
        if (isOnLetterOrDigit(offset)) {
            if (this.mIterator.isBoundary(offset) && (!isAfterLetterOrDigit(offset) || !getPrevWordBeginningOnTwoWordsBoundary)) {
                return offset;
            }
            return this.mIterator.preceding(offset);
        } else if (isAfterLetterOrDigit(offset)) {
            return this.mIterator.preceding(offset);
        } else {
            return -1;
        }
    }

    private int getEnd(int offset, boolean getNextWordEndOnTwoWordBoundary) {
        checkOffsetIsValid(offset);
        if (isAfterLetterOrDigit(offset)) {
            if (this.mIterator.isBoundary(offset) && (!isOnLetterOrDigit(offset) || !getNextWordEndOnTwoWordBoundary)) {
                return offset;
            }
            return this.mIterator.following(offset);
        } else if (isOnLetterOrDigit(offset)) {
            return this.mIterator.following(offset);
        } else {
            return -1;
        }
    }

    public int getPunctuationBeginning(int offset) {
        checkOffsetIsValid(offset);
        while (offset != -1 && !isPunctuationStartBoundary(offset)) {
            offset = prevBoundary(offset);
        }
        return offset;
    }

    public int getPunctuationEnd(int offset) {
        checkOffsetIsValid(offset);
        while (offset != -1 && !isPunctuationEndBoundary(offset)) {
            offset = nextBoundary(offset);
        }
        return offset;
    }

    public boolean isAfterPunctuation(int offset) {
        if (this.mStart < offset && offset <= this.mEnd) {
            int codePoint = Character.codePointBefore(this.mCharSeq, offset);
            return TextUtils.isPunctuation(codePoint);
        }
        return false;
    }

    public boolean isOnPunctuation(int offset) {
        if (this.mStart <= offset && offset < this.mEnd) {
            int codePoint = Character.codePointAt(this.mCharSeq, offset);
            return TextUtils.isPunctuation(codePoint);
        }
        return false;
    }

    public static boolean isMidWordPunctuation(Locale locale, int codePoint) {
        int wb = UCharacter.getIntPropertyValue(codePoint, MtpConstants.OPERATION_GET_DEVICE_PROP_DESC);
        return wb == 4 || wb == 11 || wb == 15;
    }

    private boolean isPunctuationStartBoundary(int offset) {
        return isOnPunctuation(offset) && !isAfterPunctuation(offset);
    }

    private boolean isPunctuationEndBoundary(int offset) {
        return !isOnPunctuation(offset) && isAfterPunctuation(offset);
    }

    private boolean isAfterLetterOrDigit(int offset) {
        if (this.mStart < offset && offset <= this.mEnd) {
            int codePoint = Character.codePointBefore(this.mCharSeq, offset);
            return Character.isLetterOrDigit(codePoint);
        }
        return false;
    }

    private boolean isOnLetterOrDigit(int offset) {
        if (this.mStart <= offset && offset < this.mEnd) {
            int codePoint = Character.codePointAt(this.mCharSeq, offset);
            return Character.isLetterOrDigit(codePoint);
        }
        return false;
    }

    private void checkOffsetIsValid(int offset) {
        if (this.mStart > offset || offset > this.mEnd) {
            throw new IllegalArgumentException("Invalid offset: " + offset + ". Valid range is [" + this.mStart + ", " + this.mEnd + NavigationBarInflaterView.SIZE_MOD_END);
        }
    }
}
