package android.inputmethodservice;

import android.graphics.RectF;
import android.p008os.Bundle;
import android.p008os.CancellationSignal;
import android.p008os.Handler;
import android.p008os.RemoteException;
import android.p008os.ResultReceiver;
import android.view.KeyEvent;
import android.view.inputmethod.CompletionInfo;
import android.view.inputmethod.CorrectionInfo;
import android.view.inputmethod.ExtractedText;
import android.view.inputmethod.ExtractedTextRequest;
import android.view.inputmethod.InputContentInfo;
import android.view.inputmethod.ParcelableHandwritingGesture;
import android.view.inputmethod.SurroundingText;
import android.view.inputmethod.TextAttribute;
import android.view.inputmethod.TextBoundsInfo;
import android.view.inputmethod.TextBoundsInfoResult;
import com.android.internal.infra.AndroidFuture;
import com.android.internal.inputmethod.IRemoteInputConnection;
import com.android.internal.inputmethod.InputConnectionCommandHeader;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
/* loaded from: classes2.dex */
final class IRemoteInputConnectionInvoker {
    private final IRemoteInputConnection mConnection;
    private final int mSessionId;

    private IRemoteInputConnectionInvoker(IRemoteInputConnection inputConnection, int sessionId) {
        this.mConnection = inputConnection;
        this.mSessionId = sessionId;
    }

    /* loaded from: classes2.dex */
    private static abstract class OnceResultReceiver<C> extends ResultReceiver {
        private C mConsumer;
        private Executor mExecutor;

        protected abstract void dispatch(Executor executor, C c, int i, Bundle bundle);

        protected OnceResultReceiver(Executor executor, C consumer) {
            super((Handler) null);
            Objects.requireNonNull(executor);
            Objects.requireNonNull(consumer);
            this.mExecutor = executor;
            this.mConsumer = consumer;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.p008os.ResultReceiver
        public final void onReceiveResult(int resultCode, Bundle resultData) {
            Executor executor;
            C consumer;
            synchronized (this) {
                executor = this.mExecutor;
                consumer = this.mConsumer;
                this.mExecutor = null;
                this.mConsumer = null;
            }
            if (executor != null && consumer != null) {
                dispatch(executor, consumer, resultCode, resultData);
            }
        }
    }

    /* loaded from: classes2.dex */
    private static final class IntResultReceiver extends OnceResultReceiver<IntConsumer> {
        IntResultReceiver(Executor executor, IntConsumer consumer) {
            super(executor, consumer);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.inputmethodservice.IRemoteInputConnectionInvoker.OnceResultReceiver
        public void dispatch(Executor executor, final IntConsumer consumer, final int code, Bundle data) {
            executor.execute(new Runnable() { // from class: android.inputmethodservice.IRemoteInputConnectionInvoker$IntResultReceiver$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    consumer.accept(code);
                }
            });
        }
    }

    /* loaded from: classes2.dex */
    private static final class TextBoundsInfoResultReceiver extends OnceResultReceiver<Consumer<TextBoundsInfoResult>> {
        TextBoundsInfoResultReceiver(Executor executor, Consumer<TextBoundsInfoResult> consumer) {
            super(executor, consumer);
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.inputmethodservice.IRemoteInputConnectionInvoker.OnceResultReceiver
        public void dispatch(Executor executor, final Consumer<TextBoundsInfoResult> consumer, int code, Bundle data) {
            final TextBoundsInfoResult textBoundsInfoResult = new TextBoundsInfoResult(code, TextBoundsInfo.createFromBundle(data));
            executor.execute(new Runnable() { // from class: android.inputmethodservice.IRemoteInputConnectionInvoker$TextBoundsInfoResultReceiver$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    consumer.accept(textBoundsInfoResult);
                }
            });
        }
    }

    public static IRemoteInputConnectionInvoker create(IRemoteInputConnection connection) {
        Objects.requireNonNull(connection);
        return new IRemoteInputConnectionInvoker(connection, 0);
    }

    public IRemoteInputConnectionInvoker cloneWithSessionId(int sessionId) {
        return new IRemoteInputConnectionInvoker(this.mConnection, sessionId);
    }

    public boolean isSameConnection(IRemoteInputConnection connection) {
        return connection != null && this.mConnection.asBinder() == connection.asBinder();
    }

    InputConnectionCommandHeader createHeader() {
        return new InputConnectionCommandHeader(this.mSessionId);
    }

    public AndroidFuture<CharSequence> getTextAfterCursor(int length, int flags) {
        AndroidFuture<CharSequence> future = new AndroidFuture<>();
        try {
            this.mConnection.getTextAfterCursor(createHeader(), length, flags, future);
        } catch (RemoteException e) {
            future.completeExceptionally(e);
        }
        return future;
    }

    public AndroidFuture<CharSequence> getTextBeforeCursor(int length, int flags) {
        AndroidFuture<CharSequence> future = new AndroidFuture<>();
        try {
            this.mConnection.getTextBeforeCursor(createHeader(), length, flags, future);
        } catch (RemoteException e) {
            future.completeExceptionally(e);
        }
        return future;
    }

    public AndroidFuture<CharSequence> getSelectedText(int flags) {
        AndroidFuture<CharSequence> future = new AndroidFuture<>();
        try {
            this.mConnection.getSelectedText(createHeader(), flags, future);
        } catch (RemoteException e) {
            future.completeExceptionally(e);
        }
        return future;
    }

    public AndroidFuture<SurroundingText> getSurroundingText(int beforeLength, int afterLength, int flags) {
        AndroidFuture<SurroundingText> future = new AndroidFuture<>();
        try {
            this.mConnection.getSurroundingText(createHeader(), beforeLength, afterLength, flags, future);
        } catch (RemoteException e) {
            future.completeExceptionally(e);
        }
        return future;
    }

    public AndroidFuture<Integer> getCursorCapsMode(int reqModes) {
        AndroidFuture<Integer> future = new AndroidFuture<>();
        try {
            this.mConnection.getCursorCapsMode(createHeader(), reqModes, future);
        } catch (RemoteException e) {
            future.completeExceptionally(e);
        }
        return future;
    }

    public AndroidFuture<ExtractedText> getExtractedText(ExtractedTextRequest request, int flags) {
        AndroidFuture<ExtractedText> future = new AndroidFuture<>();
        try {
            this.mConnection.getExtractedText(createHeader(), request, flags, future);
        } catch (RemoteException e) {
            future.completeExceptionally(e);
        }
        return future;
    }

    public boolean commitText(CharSequence text, int newCursorPosition) {
        try {
            this.mConnection.commitText(createHeader(), text, newCursorPosition);
            return true;
        } catch (RemoteException e) {
            return false;
        }
    }

    public boolean commitText(CharSequence text, int newCursorPosition, TextAttribute textAttribute) {
        try {
            this.mConnection.commitTextWithTextAttribute(createHeader(), text, newCursorPosition, textAttribute);
            return true;
        } catch (RemoteException e) {
            return false;
        }
    }

    public boolean commitCompletion(CompletionInfo text) {
        try {
            this.mConnection.commitCompletion(createHeader(), text);
            return true;
        } catch (RemoteException e) {
            return false;
        }
    }

    public boolean commitCorrection(CorrectionInfo correctionInfo) {
        try {
            this.mConnection.commitCorrection(createHeader(), correctionInfo);
            return true;
        } catch (RemoteException e) {
            return false;
        }
    }

    public boolean setSelection(int start, int end) {
        try {
            this.mConnection.setSelection(createHeader(), start, end);
            return true;
        } catch (RemoteException e) {
            return false;
        }
    }

    public boolean performEditorAction(int actionCode) {
        try {
            this.mConnection.performEditorAction(createHeader(), actionCode);
            return true;
        } catch (RemoteException e) {
            return false;
        }
    }

    public boolean performContextMenuAction(int id) {
        try {
            this.mConnection.performContextMenuAction(createHeader(), id);
            return true;
        } catch (RemoteException e) {
            return false;
        }
    }

    public boolean setComposingRegion(int start, int end) {
        try {
            this.mConnection.setComposingRegion(createHeader(), start, end);
            return true;
        } catch (RemoteException e) {
            return false;
        }
    }

    public boolean setComposingRegion(int start, int end, TextAttribute textAttribute) {
        try {
            this.mConnection.setComposingRegionWithTextAttribute(createHeader(), start, end, textAttribute);
            return true;
        } catch (RemoteException e) {
            return false;
        }
    }

    public boolean setComposingText(CharSequence text, int newCursorPosition) {
        try {
            this.mConnection.setComposingText(createHeader(), text, newCursorPosition);
            return true;
        } catch (RemoteException e) {
            return false;
        }
    }

    public boolean setComposingText(CharSequence text, int newCursorPosition, TextAttribute textAttribute) {
        try {
            this.mConnection.setComposingTextWithTextAttribute(createHeader(), text, newCursorPosition, textAttribute);
            return true;
        } catch (RemoteException e) {
            return false;
        }
    }

    public boolean finishComposingText() {
        try {
            this.mConnection.finishComposingText(createHeader());
            return true;
        } catch (RemoteException e) {
            return false;
        }
    }

    public boolean beginBatchEdit() {
        try {
            this.mConnection.beginBatchEdit(createHeader());
            return true;
        } catch (RemoteException e) {
            return false;
        }
    }

    public boolean endBatchEdit() {
        try {
            this.mConnection.endBatchEdit(createHeader());
            return true;
        } catch (RemoteException e) {
            return false;
        }
    }

    public boolean sendKeyEvent(KeyEvent event) {
        try {
            this.mConnection.sendKeyEvent(createHeader(), event);
            return true;
        } catch (RemoteException e) {
            return false;
        }
    }

    public boolean clearMetaKeyStates(int states) {
        try {
            this.mConnection.clearMetaKeyStates(createHeader(), states);
            return true;
        } catch (RemoteException e) {
            return false;
        }
    }

    public boolean deleteSurroundingText(int beforeLength, int afterLength) {
        try {
            this.mConnection.deleteSurroundingText(createHeader(), beforeLength, afterLength);
            return true;
        } catch (RemoteException e) {
            return false;
        }
    }

    public boolean deleteSurroundingTextInCodePoints(int beforeLength, int afterLength) {
        try {
            this.mConnection.deleteSurroundingTextInCodePoints(createHeader(), beforeLength, afterLength);
            return true;
        } catch (RemoteException e) {
            return false;
        }
    }

    public boolean performSpellCheck() {
        try {
            this.mConnection.performSpellCheck(createHeader());
            return true;
        } catch (RemoteException e) {
            return false;
        }
    }

    public boolean performPrivateCommand(String action, Bundle data) {
        try {
            this.mConnection.performPrivateCommand(createHeader(), action, data);
            return true;
        } catch (RemoteException e) {
            return false;
        }
    }

    public void performHandwritingGesture(ParcelableHandwritingGesture gesture, Executor executor, final IntConsumer consumer) {
        ResultReceiver resultReceiver = null;
        if (consumer != null) {
            Objects.requireNonNull(executor);
            resultReceiver = new IntResultReceiver(executor, consumer);
        }
        try {
            this.mConnection.performHandwritingGesture(createHeader(), gesture, resultReceiver);
        } catch (RemoteException e) {
            if (consumer != null && executor != null) {
                executor.execute(new Runnable() { // from class: android.inputmethodservice.IRemoteInputConnectionInvoker$$ExternalSyntheticLambda1
                    @Override // java.lang.Runnable
                    public final void run() {
                        consumer.accept(4);
                    }
                });
            }
        }
    }

    public boolean previewHandwritingGesture(ParcelableHandwritingGesture gesture, CancellationSignal cancellationSignal) {
        if (cancellationSignal != null && cancellationSignal.isCanceled()) {
            return false;
        }
        try {
            this.mConnection.previewHandwritingGesture(createHeader(), gesture, null);
            return true;
        } catch (RemoteException e) {
            return false;
        }
    }

    public AndroidFuture<Boolean> requestCursorUpdates(int cursorUpdateMode, int imeDisplayId) {
        AndroidFuture<Boolean> future = new AndroidFuture<>();
        try {
            this.mConnection.requestCursorUpdates(createHeader(), cursorUpdateMode, imeDisplayId, future);
        } catch (RemoteException e) {
            future.completeExceptionally(e);
        }
        return future;
    }

    public AndroidFuture<Boolean> requestCursorUpdates(int cursorUpdateMode, int cursorUpdateFilter, int imeDisplayId) {
        AndroidFuture<Boolean> future = new AndroidFuture<>();
        try {
            this.mConnection.requestCursorUpdatesWithFilter(createHeader(), cursorUpdateMode, cursorUpdateFilter, imeDisplayId, future);
        } catch (RemoteException e) {
            future.completeExceptionally(e);
        }
        return future;
    }

    public void requestTextBoundsInfo(RectF bounds, Executor executor, final Consumer<TextBoundsInfoResult> consumer) {
        Objects.requireNonNull(executor);
        Objects.requireNonNull(consumer);
        ResultReceiver resultReceiver = new TextBoundsInfoResultReceiver(executor, consumer);
        try {
            this.mConnection.requestTextBoundsInfo(createHeader(), bounds, resultReceiver);
        } catch (RemoteException e) {
            executor.execute(new Runnable() { // from class: android.inputmethodservice.IRemoteInputConnectionInvoker$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    consumer.accept(new TextBoundsInfoResult(3));
                }
            });
        }
    }

    public AndroidFuture<Boolean> commitContent(InputContentInfo inputContentInfo, int flags, Bundle opts) {
        AndroidFuture<Boolean> future = new AndroidFuture<>();
        try {
            this.mConnection.commitContent(createHeader(), inputContentInfo, flags, opts, future);
        } catch (RemoteException e) {
            future.completeExceptionally(e);
        }
        return future;
    }

    public boolean setImeConsumesInput(boolean imeConsumesInput) {
        try {
            this.mConnection.setImeConsumesInput(createHeader(), imeConsumesInput);
            return true;
        } catch (RemoteException e) {
            return false;
        }
    }

    public boolean replaceText(int start, int end, CharSequence text, int newCursorPosition, TextAttribute textAttribute) {
        try {
            this.mConnection.replaceText(createHeader(), start, end, text, newCursorPosition, textAttribute);
            return true;
        } catch (RemoteException e) {
            return false;
        }
    }
}
