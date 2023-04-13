package android.view.inputmethod;

import android.graphics.RectF;
import android.p008os.Bundle;
import android.p008os.CancellationSignal;
import android.p008os.Handler;
import android.text.TextUtils;
import android.view.KeyEvent;
import com.android.internal.util.Preconditions;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
/* loaded from: classes4.dex */
public interface InputConnection {
    public static final int CURSOR_UPDATE_FILTER_CHARACTER_BOUNDS = 8;
    public static final int CURSOR_UPDATE_FILTER_EDITOR_BOUNDS = 4;
    public static final int CURSOR_UPDATE_FILTER_INSERTION_MARKER = 16;
    public static final int CURSOR_UPDATE_FILTER_TEXT_APPEARANCE = 64;
    public static final int CURSOR_UPDATE_FILTER_VISIBLE_LINE_BOUNDS = 32;
    public static final int CURSOR_UPDATE_IMMEDIATE = 1;
    public static final int CURSOR_UPDATE_MONITOR = 2;
    public static final int GET_EXTRACTED_TEXT_MONITOR = 1;
    public static final int GET_TEXT_WITH_STYLES = 1;
    public static final int HANDWRITING_GESTURE_RESULT_CANCELLED = 4;
    public static final int HANDWRITING_GESTURE_RESULT_FAILED = 3;
    public static final int HANDWRITING_GESTURE_RESULT_FALLBACK = 5;
    public static final int HANDWRITING_GESTURE_RESULT_SUCCESS = 1;
    public static final int HANDWRITING_GESTURE_RESULT_UNKNOWN = 0;
    public static final int HANDWRITING_GESTURE_RESULT_UNSUPPORTED = 2;
    public static final int INPUT_CONTENT_GRANT_READ_URI_PERMISSION = 1;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes4.dex */
    public @interface CursorUpdateFilter {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes4.dex */
    public @interface CursorUpdateMode {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes4.dex */
    public @interface GetTextType {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes4.dex */
    public @interface HandwritingGestureResult {
    }

    boolean beginBatchEdit();

    boolean clearMetaKeyStates(int i);

    void closeConnection();

    boolean commitCompletion(CompletionInfo completionInfo);

    boolean commitContent(InputContentInfo inputContentInfo, int i, Bundle bundle);

    boolean commitCorrection(CorrectionInfo correctionInfo);

    boolean commitText(CharSequence charSequence, int i);

    boolean deleteSurroundingText(int i, int i2);

    boolean deleteSurroundingTextInCodePoints(int i, int i2);

    boolean endBatchEdit();

    boolean finishComposingText();

    int getCursorCapsMode(int i);

    ExtractedText getExtractedText(ExtractedTextRequest extractedTextRequest, int i);

    Handler getHandler();

    CharSequence getSelectedText(int i);

    CharSequence getTextAfterCursor(int i, int i2);

    CharSequence getTextBeforeCursor(int i, int i2);

    boolean performContextMenuAction(int i);

    boolean performEditorAction(int i);

    boolean performPrivateCommand(String str, Bundle bundle);

    boolean reportFullscreenMode(boolean z);

    boolean requestCursorUpdates(int i);

    boolean sendKeyEvent(KeyEvent keyEvent);

    boolean setComposingRegion(int i, int i2);

    boolean setComposingText(CharSequence charSequence, int i);

    boolean setSelection(int i, int i2);

    default SurroundingText getSurroundingText(int beforeLength, int afterLength, int flags) {
        CharSequence textAfterCursor;
        Preconditions.checkArgumentNonnegative(beforeLength);
        Preconditions.checkArgumentNonnegative(afterLength);
        CharSequence textBeforeCursor = getTextBeforeCursor(beforeLength, flags);
        if (textBeforeCursor == null || (textAfterCursor = getTextAfterCursor(afterLength, flags)) == null) {
            return null;
        }
        CharSequence selectedText = getSelectedText(flags);
        if (selectedText == null) {
            selectedText = "";
        }
        CharSequence surroundingText = TextUtils.concat(textBeforeCursor, selectedText, textAfterCursor);
        return new SurroundingText(surroundingText, textBeforeCursor.length(), textBeforeCursor.length() + selectedText.length(), -1);
    }

    default boolean setComposingText(CharSequence text, int newCursorPosition, TextAttribute textAttribute) {
        return setComposingText(text, newCursorPosition);
    }

    default boolean setComposingRegion(int start, int end, TextAttribute textAttribute) {
        return setComposingRegion(start, end);
    }

    default boolean commitText(CharSequence text, int newCursorPosition, TextAttribute textAttribute) {
        return commitText(text, newCursorPosition);
    }

    default boolean performSpellCheck() {
        return false;
    }

    default void performHandwritingGesture(HandwritingGesture gesture, Executor executor, final IntConsumer consumer) {
        if (executor != null && consumer != null) {
            executor.execute(new Runnable() { // from class: android.view.inputmethod.InputConnection$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    consumer.accept(2);
                }
            });
        }
    }

    default boolean previewHandwritingGesture(PreviewableHandwritingGesture gesture, CancellationSignal cancellationSignal) {
        return false;
    }

    default boolean requestCursorUpdates(int cursorUpdateMode, int cursorUpdateFilter) {
        if (cursorUpdateFilter == 0) {
            return requestCursorUpdates(cursorUpdateMode);
        }
        return false;
    }

    default void requestTextBoundsInfo(RectF bounds, Executor executor, final Consumer<TextBoundsInfoResult> consumer) {
        Objects.requireNonNull(executor);
        Objects.requireNonNull(consumer);
        executor.execute(new Runnable() { // from class: android.view.inputmethod.InputConnection$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                consumer.accept(new TextBoundsInfoResult(0));
            }
        });
    }

    default boolean setImeConsumesInput(boolean imeConsumesInput) {
        return false;
    }

    default TextSnapshot takeSnapshot() {
        return null;
    }

    default boolean replaceText(int start, int end, CharSequence text, int newCursorPosition, TextAttribute textAttribute) {
        Preconditions.checkArgumentNonnegative(start);
        Preconditions.checkArgumentNonnegative(end);
        beginBatchEdit();
        finishComposingText();
        setSelection(start, end);
        commitText(text, newCursorPosition, textAttribute);
        endBatchEdit();
        return true;
    }
}
