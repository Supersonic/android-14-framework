package android.inputmethodservice;

import android.graphics.RectF;
import android.p008os.Bundle;
import android.p008os.CancellationSignal;
import android.p008os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.CompletionInfo;
import android.view.inputmethod.CorrectionInfo;
import android.view.inputmethod.ExtractedText;
import android.view.inputmethod.ExtractedTextRequest;
import android.view.inputmethod.HandwritingGesture;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputContentInfo;
import android.view.inputmethod.ParcelableHandwritingGesture;
import android.view.inputmethod.PreviewableHandwritingGesture;
import android.view.inputmethod.SurroundingText;
import android.view.inputmethod.TextAttribute;
import android.view.inputmethod.TextBoundsInfoResult;
import com.android.internal.inputmethod.CancellationGroup;
import com.android.internal.inputmethod.CompletableFutureUtil;
import com.android.internal.inputmethod.IRemoteInputConnection;
import com.android.internal.inputmethod.ImeTracing;
import com.android.internal.inputmethod.InputConnectionProtoDumper;
import java.lang.ref.WeakReference;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
/* loaded from: classes2.dex */
final class RemoteInputConnection implements InputConnection {
    private static final int MAX_WAIT_TIME_MILLIS = 2000;
    private static final String TAG = "RemoteInputConnection";
    private final CancellationGroup mCancellationGroup;
    private final InputMethodServiceInternalHolder mImsInternal;
    private final IRemoteInputConnectionInvoker mInvoker;

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes2.dex */
    public static final class InputMethodServiceInternalHolder {
        private final WeakReference<InputMethodServiceInternal> mServiceRef;

        private InputMethodServiceInternalHolder(WeakReference<InputMethodServiceInternal> ims) {
            this.mServiceRef = ims;
        }

        public InputMethodServiceInternal getAndWarnIfNull() {
            InputMethodServiceInternal ims = this.mServiceRef.get();
            if (ims == null) {
                Log.m109e(RemoteInputConnection.TAG, "InputMethodService is already destroyed.  InputConnection instances cannot be used beyond InputMethodService lifetime.", new Throwable());
            }
            return ims;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public RemoteInputConnection(WeakReference<InputMethodServiceInternal> inputMethodService, IRemoteInputConnection inputConnection, CancellationGroup cancellationGroup) {
        this.mImsInternal = new InputMethodServiceInternalHolder(inputMethodService);
        this.mInvoker = IRemoteInputConnectionInvoker.create(inputConnection);
        this.mCancellationGroup = cancellationGroup;
    }

    public boolean isSameConnection(IRemoteInputConnection inputConnection) {
        return this.mInvoker.isSameConnection(inputConnection);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public RemoteInputConnection(RemoteInputConnection original, int sessionId) {
        this.mImsInternal = original.mImsInternal;
        this.mInvoker = original.mInvoker.cloneWithSessionId(sessionId);
        this.mCancellationGroup = original.mCancellationGroup;
    }

    @Override // android.view.inputmethod.InputConnection
    public CharSequence getTextAfterCursor(int length, int flags) {
        if (length < 0) {
            Log.m110e(TAG, "length=" + length + " is invalid and always results in null result.");
        }
        if (this.mCancellationGroup.isCanceled()) {
            return null;
        }
        CompletableFuture<CharSequence> value = this.mInvoker.getTextAfterCursor(length, flags);
        CharSequence result = (CharSequence) CompletableFutureUtil.getResultOrNull(value, TAG, "getTextAfterCursor()", this.mCancellationGroup, 2000L);
        InputMethodServiceInternal imsInternal = this.mImsInternal.getAndWarnIfNull();
        if (imsInternal != null && ImeTracing.getInstance().isEnabled()) {
            byte[] icProto = InputConnectionProtoDumper.buildGetTextAfterCursorProto(length, flags, result);
            imsInternal.triggerServiceDump("RemoteInputConnection#getTextAfterCursor", icProto);
        }
        return result;
    }

    @Override // android.view.inputmethod.InputConnection
    public CharSequence getTextBeforeCursor(int length, int flags) {
        if (length < 0) {
            Log.m110e(TAG, "length=" + length + " is invalid and always results in null result.");
        }
        if (this.mCancellationGroup.isCanceled()) {
            return null;
        }
        CompletableFuture<CharSequence> value = this.mInvoker.getTextBeforeCursor(length, flags);
        CharSequence result = (CharSequence) CompletableFutureUtil.getResultOrNull(value, TAG, "getTextBeforeCursor()", this.mCancellationGroup, 2000L);
        InputMethodServiceInternal imsInternal = this.mImsInternal.getAndWarnIfNull();
        if (imsInternal != null && ImeTracing.getInstance().isEnabled()) {
            byte[] icProto = InputConnectionProtoDumper.buildGetTextBeforeCursorProto(length, flags, result);
            imsInternal.triggerServiceDump("RemoteInputConnection#getTextBeforeCursor", icProto);
        }
        return result;
    }

    @Override // android.view.inputmethod.InputConnection
    public CharSequence getSelectedText(int flags) {
        if (this.mCancellationGroup.isCanceled()) {
            return null;
        }
        CompletableFuture<CharSequence> value = this.mInvoker.getSelectedText(flags);
        CharSequence result = (CharSequence) CompletableFutureUtil.getResultOrNull(value, TAG, "getSelectedText()", this.mCancellationGroup, 2000L);
        InputMethodServiceInternal imsInternal = this.mImsInternal.getAndWarnIfNull();
        if (imsInternal != null && ImeTracing.getInstance().isEnabled()) {
            byte[] icProto = InputConnectionProtoDumper.buildGetSelectedTextProto(flags, result);
            imsInternal.triggerServiceDump("RemoteInputConnection#getSelectedText", icProto);
        }
        return result;
    }

    @Override // android.view.inputmethod.InputConnection
    public SurroundingText getSurroundingText(int beforeLength, int afterLength, int flags) {
        if (beforeLength < 0) {
            Log.m110e(TAG, "beforeLength=" + beforeLength + " is invalid and always results in null result.");
        }
        if (afterLength < 0) {
            Log.m110e(TAG, "afterLength=" + afterLength + " is invalid and always results in null result.");
        }
        if (this.mCancellationGroup.isCanceled()) {
            return null;
        }
        CompletableFuture<SurroundingText> value = this.mInvoker.getSurroundingText(beforeLength, afterLength, flags);
        SurroundingText result = (SurroundingText) CompletableFutureUtil.getResultOrNull(value, TAG, "getSurroundingText()", this.mCancellationGroup, 2000L);
        InputMethodServiceInternal imsInternal = this.mImsInternal.getAndWarnIfNull();
        if (imsInternal != null && ImeTracing.getInstance().isEnabled()) {
            byte[] icProto = InputConnectionProtoDumper.buildGetSurroundingTextProto(beforeLength, afterLength, flags, result);
            imsInternal.triggerServiceDump("RemoteInputConnection#getSurroundingText", icProto);
        }
        return result;
    }

    @Override // android.view.inputmethod.InputConnection
    public int getCursorCapsMode(int reqModes) {
        if (this.mCancellationGroup.isCanceled()) {
            return 0;
        }
        CompletableFuture<Integer> value = this.mInvoker.getCursorCapsMode(reqModes);
        int result = CompletableFutureUtil.getResultOrZero(value, TAG, "getCursorCapsMode()", this.mCancellationGroup, 2000L);
        InputMethodServiceInternal imsInternal = this.mImsInternal.getAndWarnIfNull();
        if (imsInternal != null && ImeTracing.getInstance().isEnabled()) {
            byte[] icProto = InputConnectionProtoDumper.buildGetCursorCapsModeProto(reqModes, result);
            imsInternal.triggerServiceDump("RemoteInputConnection#getCursorCapsMode", icProto);
        }
        return result;
    }

    @Override // android.view.inputmethod.InputConnection
    public ExtractedText getExtractedText(ExtractedTextRequest request, int flags) {
        if (this.mCancellationGroup.isCanceled()) {
            return null;
        }
        CompletableFuture<ExtractedText> value = this.mInvoker.getExtractedText(request, flags);
        ExtractedText result = (ExtractedText) CompletableFutureUtil.getResultOrNull(value, TAG, "getExtractedText()", this.mCancellationGroup, 2000L);
        InputMethodServiceInternal imsInternal = this.mImsInternal.getAndWarnIfNull();
        if (imsInternal != null && ImeTracing.getInstance().isEnabled()) {
            byte[] icProto = InputConnectionProtoDumper.buildGetExtractedTextProto(request, flags, result);
            imsInternal.triggerServiceDump("RemoteInputConnection#getExtractedText", icProto);
        }
        return result;
    }

    @Override // android.view.inputmethod.InputConnection
    public boolean commitText(CharSequence text, int newCursorPosition) {
        boolean handled = this.mInvoker.commitText(text, newCursorPosition);
        if (handled) {
            notifyUserActionIfNecessary();
        }
        return handled;
    }

    @Override // android.view.inputmethod.InputConnection
    public boolean commitText(CharSequence text, int newCursorPosition, TextAttribute textAttribute) {
        boolean handled = this.mInvoker.commitText(text, newCursorPosition, textAttribute);
        if (handled) {
            notifyUserActionIfNecessary();
        }
        return handled;
    }

    private void notifyUserActionIfNecessary() {
        InputMethodServiceInternal imsInternal = this.mImsInternal.getAndWarnIfNull();
        if (imsInternal == null) {
            return;
        }
        imsInternal.notifyUserActionIfNecessary();
    }

    @Override // android.view.inputmethod.InputConnection
    public boolean commitCompletion(CompletionInfo text) {
        return this.mInvoker.commitCompletion(text);
    }

    @Override // android.view.inputmethod.InputConnection
    public boolean commitCorrection(CorrectionInfo correctionInfo) {
        return this.mInvoker.commitCorrection(correctionInfo);
    }

    @Override // android.view.inputmethod.InputConnection
    public boolean setSelection(int start, int end) {
        return this.mInvoker.setSelection(start, end);
    }

    @Override // android.view.inputmethod.InputConnection
    public boolean performEditorAction(int actionCode) {
        return this.mInvoker.performEditorAction(actionCode);
    }

    @Override // android.view.inputmethod.InputConnection
    public boolean performContextMenuAction(int id) {
        return this.mInvoker.performContextMenuAction(id);
    }

    @Override // android.view.inputmethod.InputConnection
    public boolean setComposingRegion(int start, int end) {
        return this.mInvoker.setComposingRegion(start, end);
    }

    @Override // android.view.inputmethod.InputConnection
    public boolean setComposingRegion(int start, int end, TextAttribute textAttribute) {
        return this.mInvoker.setComposingRegion(start, end, textAttribute);
    }

    @Override // android.view.inputmethod.InputConnection
    public boolean setComposingText(CharSequence text, int newCursorPosition) {
        boolean handled = this.mInvoker.setComposingText(text, newCursorPosition);
        if (handled) {
            notifyUserActionIfNecessary();
        }
        return handled;
    }

    @Override // android.view.inputmethod.InputConnection
    public boolean setComposingText(CharSequence text, int newCursorPosition, TextAttribute textAttribute) {
        boolean handled = this.mInvoker.setComposingText(text, newCursorPosition, textAttribute);
        if (handled) {
            notifyUserActionIfNecessary();
        }
        return handled;
    }

    @Override // android.view.inputmethod.InputConnection
    public boolean finishComposingText() {
        return this.mInvoker.finishComposingText();
    }

    @Override // android.view.inputmethod.InputConnection
    public boolean beginBatchEdit() {
        return this.mInvoker.beginBatchEdit();
    }

    @Override // android.view.inputmethod.InputConnection
    public boolean endBatchEdit() {
        return this.mInvoker.endBatchEdit();
    }

    @Override // android.view.inputmethod.InputConnection
    public boolean sendKeyEvent(KeyEvent event) {
        boolean handled = this.mInvoker.sendKeyEvent(event);
        if (handled) {
            notifyUserActionIfNecessary();
        }
        return handled;
    }

    @Override // android.view.inputmethod.InputConnection
    public boolean clearMetaKeyStates(int states) {
        return this.mInvoker.clearMetaKeyStates(states);
    }

    @Override // android.view.inputmethod.InputConnection
    public boolean deleteSurroundingText(int beforeLength, int afterLength) {
        return this.mInvoker.deleteSurroundingText(beforeLength, afterLength);
    }

    @Override // android.view.inputmethod.InputConnection
    public boolean deleteSurroundingTextInCodePoints(int beforeLength, int afterLength) {
        return this.mInvoker.deleteSurroundingTextInCodePoints(beforeLength, afterLength);
    }

    @Override // android.view.inputmethod.InputConnection
    public boolean reportFullscreenMode(boolean enabled) {
        return false;
    }

    @Override // android.view.inputmethod.InputConnection
    public boolean performSpellCheck() {
        return this.mInvoker.performSpellCheck();
    }

    @Override // android.view.inputmethod.InputConnection
    public boolean performPrivateCommand(String action, Bundle data) {
        return this.mInvoker.performPrivateCommand(action, data);
    }

    @Override // android.view.inputmethod.InputConnection
    public void performHandwritingGesture(HandwritingGesture gesture, Executor executor, IntConsumer consumer) {
        this.mInvoker.performHandwritingGesture(ParcelableHandwritingGesture.m80of(gesture), executor, consumer);
    }

    @Override // android.view.inputmethod.InputConnection
    public boolean previewHandwritingGesture(PreviewableHandwritingGesture gesture, CancellationSignal cancellationSignal) {
        return this.mInvoker.previewHandwritingGesture(ParcelableHandwritingGesture.m80of(gesture), cancellationSignal);
    }

    @Override // android.view.inputmethod.InputConnection
    public boolean requestCursorUpdates(int cursorUpdateMode) {
        InputMethodServiceInternal ims;
        if (this.mCancellationGroup.isCanceled() || (ims = this.mImsInternal.getAndWarnIfNull()) == null) {
            return false;
        }
        int displayId = ims.getContext().getDisplayId();
        CompletableFuture<Boolean> value = this.mInvoker.requestCursorUpdates(cursorUpdateMode, displayId);
        return CompletableFutureUtil.getResultOrFalse(value, TAG, "requestCursorUpdates()", this.mCancellationGroup, 2000L);
    }

    @Override // android.view.inputmethod.InputConnection
    public boolean requestCursorUpdates(int cursorUpdateMode, int cursorUpdateFilter) {
        InputMethodServiceInternal ims;
        if (this.mCancellationGroup.isCanceled() || (ims = this.mImsInternal.getAndWarnIfNull()) == null) {
            return false;
        }
        int displayId = ims.getContext().getDisplayId();
        CompletableFuture<Boolean> value = this.mInvoker.requestCursorUpdates(cursorUpdateMode, cursorUpdateFilter, displayId);
        return CompletableFutureUtil.getResultOrFalse(value, TAG, "requestCursorUpdates()", this.mCancellationGroup, 2000L);
    }

    @Override // android.view.inputmethod.InputConnection
    public void requestTextBoundsInfo(RectF bounds, Executor executor, Consumer<TextBoundsInfoResult> consumer) {
        this.mInvoker.requestTextBoundsInfo(bounds, executor, consumer);
    }

    @Override // android.view.inputmethod.InputConnection
    public Handler getHandler() {
        return null;
    }

    @Override // android.view.inputmethod.InputConnection
    public void closeConnection() {
    }

    @Override // android.view.inputmethod.InputConnection
    public boolean commitContent(InputContentInfo inputContentInfo, int flags, Bundle opts) {
        if (this.mCancellationGroup.isCanceled()) {
            return false;
        }
        if ((flags & 1) != 0) {
            InputMethodServiceInternal imsInternal = this.mImsInternal.getAndWarnIfNull();
            if (imsInternal == null) {
                return false;
            }
            imsInternal.exposeContent(inputContentInfo, this);
        }
        CompletableFuture<Boolean> value = this.mInvoker.commitContent(inputContentInfo, flags, opts);
        return CompletableFutureUtil.getResultOrFalse(value, TAG, "commitContent()", this.mCancellationGroup, 2000L);
    }

    @Override // android.view.inputmethod.InputConnection
    public boolean setImeConsumesInput(boolean imeConsumesInput) {
        return this.mInvoker.setImeConsumesInput(imeConsumesInput);
    }

    @Override // android.view.inputmethod.InputConnection
    public boolean replaceText(int start, int end, CharSequence text, int newCursorPosition, TextAttribute textAttribute) {
        return this.mInvoker.replaceText(start, end, text, newCursorPosition, textAttribute);
    }

    public String toString() {
        return "RemoteInputConnection{idHash=#" + Integer.toHexString(System.identityHashCode(this)) + "}";
    }
}
