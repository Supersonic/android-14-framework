package android.view.inputmethod;

import java.util.Objects;
/* loaded from: classes4.dex */
public final class TextSnapshot {
    private final int mCompositionEnd;
    private final int mCompositionStart;
    private final int mCursorCapsMode;
    private final SurroundingText mSurroundingText;

    public TextSnapshot(SurroundingText surroundingText, int compositionStart, int compositionEnd, int cursorCapsMode) {
        Objects.requireNonNull(surroundingText);
        this.mSurroundingText = surroundingText;
        if (compositionStart < -1) {
            throw new IllegalArgumentException("compositionStart must be -1 or higher but was " + compositionStart);
        }
        if (compositionEnd < -1) {
            throw new IllegalArgumentException("compositionEnd must be -1 or higher but was " + compositionEnd);
        }
        if (compositionStart == -1 && compositionEnd != -1) {
            throw new IllegalArgumentException("compositionEnd must be -1 if compositionStart is -1 but was " + compositionEnd);
        }
        if (compositionStart != -1 && compositionEnd == -1) {
            throw new IllegalArgumentException("compositionStart must be -1 if compositionEnd is -1 but was " + compositionStart);
        }
        if (compositionStart > compositionEnd) {
            throw new IllegalArgumentException("compositionStart=" + compositionStart + " must be equal to or greater than compositionEnd=" + compositionEnd);
        }
        this.mCompositionStart = compositionStart;
        this.mCompositionEnd = compositionEnd;
        this.mCursorCapsMode = cursorCapsMode;
    }

    public SurroundingText getSurroundingText() {
        return this.mSurroundingText;
    }

    public int getSelectionStart() {
        if (this.mSurroundingText.getOffset() < 0) {
            return -1;
        }
        return this.mSurroundingText.getSelectionStart() + this.mSurroundingText.getOffset();
    }

    public int getSelectionEnd() {
        if (this.mSurroundingText.getOffset() < 0) {
            return -1;
        }
        return this.mSurroundingText.getSelectionEnd() + this.mSurroundingText.getOffset();
    }

    public int getCompositionStart() {
        return this.mCompositionStart;
    }

    public int getCompositionEnd() {
        return this.mCompositionEnd;
    }

    public int getCursorCapsMode() {
        return this.mCursorCapsMode;
    }
}
