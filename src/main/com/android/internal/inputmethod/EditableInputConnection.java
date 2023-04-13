package com.android.internal.inputmethod;

import android.graphics.RectF;
import android.p008os.Bundle;
import android.p008os.CancellationSignal;
import android.text.Editable;
import android.text.Selection;
import android.text.method.KeyListener;
import android.util.proto.ProtoOutputStream;
import android.view.View;
import android.view.inputmethod.BaseInputConnection;
import android.view.inputmethod.CompletionInfo;
import android.view.inputmethod.CorrectionInfo;
import android.view.inputmethod.DeleteGesture;
import android.view.inputmethod.DeleteRangeGesture;
import android.view.inputmethod.DumpableInputConnection;
import android.view.inputmethod.ExtractedText;
import android.view.inputmethod.ExtractedTextRequest;
import android.view.inputmethod.HandwritingGesture;
import android.view.inputmethod.InsertGesture;
import android.view.inputmethod.InsertModeGesture;
import android.view.inputmethod.JoinOrSplitGesture;
import android.view.inputmethod.PreviewableHandwritingGesture;
import android.view.inputmethod.RemoveSpaceGesture;
import android.view.inputmethod.SelectGesture;
import android.view.inputmethod.SelectRangeGesture;
import android.view.inputmethod.TextBoundsInfo;
import android.view.inputmethod.TextBoundsInfoResult;
import android.widget.TextView;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
/* loaded from: classes4.dex */
public final class EditableInputConnection extends BaseInputConnection implements DumpableInputConnection {
    private static final boolean DEBUG = false;
    private static final String TAG = "EditableInputConnection";
    private int mBatchEditNesting;
    private final TextView mTextView;

    public EditableInputConnection(TextView textview) {
        super((View) textview, true);
        this.mTextView = textview;
    }

    @Override // android.view.inputmethod.BaseInputConnection
    public Editable getEditable() {
        TextView tv = this.mTextView;
        if (tv != null) {
            return tv.getEditableText();
        }
        return null;
    }

    @Override // android.view.inputmethod.BaseInputConnection, android.view.inputmethod.InputConnection
    public boolean beginBatchEdit() {
        synchronized (this) {
            if (this.mBatchEditNesting >= 0) {
                this.mTextView.beginBatchEdit();
                this.mBatchEditNesting++;
                return true;
            }
            return false;
        }
    }

    @Override // android.view.inputmethod.BaseInputConnection, android.view.inputmethod.InputConnection
    public boolean endBatchEdit() {
        synchronized (this) {
            if (this.mBatchEditNesting > 0) {
                this.mTextView.endBatchEdit();
                int i = this.mBatchEditNesting - 1;
                this.mBatchEditNesting = i;
                return i > 0;
            }
            return false;
        }
    }

    @Override // android.view.inputmethod.BaseInputConnection
    public void endComposingRegionEditInternal() {
        this.mTextView.notifyContentCaptureTextChanged();
    }

    @Override // android.view.inputmethod.BaseInputConnection, android.view.inputmethod.InputConnection
    public void closeConnection() {
        super.closeConnection();
        synchronized (this) {
            while (this.mBatchEditNesting > 0) {
                endBatchEdit();
            }
            this.mBatchEditNesting = -1;
        }
    }

    @Override // android.view.inputmethod.BaseInputConnection, android.view.inputmethod.InputConnection
    public boolean clearMetaKeyStates(int states) {
        Editable content = getEditable();
        if (content == null) {
            return false;
        }
        KeyListener kl = this.mTextView.getKeyListener();
        if (kl != null) {
            try {
                kl.clearMetaKeyState(this.mTextView, content, states);
                return true;
            } catch (AbstractMethodError e) {
                return true;
            }
        }
        return true;
    }

    @Override // android.view.inputmethod.BaseInputConnection, android.view.inputmethod.InputConnection
    public boolean commitCompletion(CompletionInfo text) {
        this.mTextView.beginBatchEdit();
        this.mTextView.onCommitCompletion(text);
        this.mTextView.endBatchEdit();
        return true;
    }

    @Override // android.view.inputmethod.BaseInputConnection, android.view.inputmethod.InputConnection
    public boolean commitCorrection(CorrectionInfo correctionInfo) {
        this.mTextView.beginBatchEdit();
        this.mTextView.onCommitCorrection(correctionInfo);
        this.mTextView.endBatchEdit();
        return true;
    }

    @Override // android.view.inputmethod.BaseInputConnection, android.view.inputmethod.InputConnection
    public boolean performEditorAction(int actionCode) {
        this.mTextView.onEditorAction(actionCode);
        return true;
    }

    @Override // android.view.inputmethod.BaseInputConnection, android.view.inputmethod.InputConnection
    public boolean performContextMenuAction(int id) {
        this.mTextView.beginBatchEdit();
        this.mTextView.onTextContextMenuItem(id);
        this.mTextView.endBatchEdit();
        return true;
    }

    @Override // android.view.inputmethod.BaseInputConnection, android.view.inputmethod.InputConnection
    public ExtractedText getExtractedText(ExtractedTextRequest request, int flags) {
        if (this.mTextView != null) {
            ExtractedText et = new ExtractedText();
            if (this.mTextView.extractText(request, et)) {
                if ((flags & 1) != 0) {
                    this.mTextView.setExtracting(request);
                }
                return et;
            }
            return null;
        }
        return null;
    }

    @Override // android.view.inputmethod.InputConnection
    public boolean performSpellCheck() {
        this.mTextView.onPerformSpellCheck();
        return true;
    }

    @Override // android.view.inputmethod.BaseInputConnection, android.view.inputmethod.InputConnection
    public boolean performPrivateCommand(String action, Bundle data) {
        this.mTextView.onPrivateIMECommand(action, data);
        return true;
    }

    @Override // android.view.inputmethod.BaseInputConnection, android.view.inputmethod.InputConnection
    public boolean commitText(CharSequence text, int newCursorPosition) {
        TextView textView = this.mTextView;
        if (textView == null) {
            return super.commitText(text, newCursorPosition);
        }
        textView.resetErrorChangedFlag();
        boolean success = super.commitText(text, newCursorPosition);
        this.mTextView.hideErrorIfUnchanged();
        return success;
    }

    @Override // android.view.inputmethod.InputConnection
    public boolean requestCursorUpdates(int cursorUpdateMode, int cursorUpdateFilter) {
        return requestCursorUpdates(cursorUpdateMode | cursorUpdateFilter);
    }

    @Override // android.view.inputmethod.BaseInputConnection, android.view.inputmethod.InputConnection
    public boolean requestCursorUpdates(int cursorUpdateMode) {
        int unknownFlags = cursorUpdateMode & (-128);
        if (unknownFlags != 0 || this.mIMM == null) {
            return false;
        }
        this.mIMM.setUpdateCursorAnchorInfoMode(cursorUpdateMode);
        TextView textView = this.mTextView;
        if (textView != null) {
            textView.onRequestCursorUpdatesInternal(cursorUpdateMode & 3, cursorUpdateMode & 124);
            return true;
        }
        return true;
    }

    @Override // android.view.inputmethod.InputConnection
    public void requestTextBoundsInfo(RectF bounds, Executor executor, final Consumer<TextBoundsInfoResult> consumer) {
        int resultCode;
        TextBoundsInfo textBoundsInfo = this.mTextView.getTextBoundsInfo(bounds);
        if (textBoundsInfo != null) {
            resultCode = 1;
        } else {
            resultCode = 2;
        }
        final TextBoundsInfoResult textBoundsInfoResult = new TextBoundsInfoResult(resultCode, textBoundsInfo);
        executor.execute(new Runnable() { // from class: com.android.internal.inputmethod.EditableInputConnection$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                consumer.accept(textBoundsInfoResult);
            }
        });
    }

    @Override // android.view.inputmethod.InputConnection
    public boolean setImeConsumesInput(boolean imeConsumesInput) {
        TextView textView = this.mTextView;
        if (textView == null) {
            return super.setImeConsumesInput(imeConsumesInput);
        }
        textView.setImeConsumesInput(imeConsumesInput);
        return true;
    }

    @Override // android.view.inputmethod.InputConnection
    public void performHandwritingGesture(HandwritingGesture gesture, Executor executor, final IntConsumer consumer) {
        final int result;
        if (gesture instanceof SelectGesture) {
            result = this.mTextView.performHandwritingSelectGesture((SelectGesture) gesture);
        } else if (gesture instanceof SelectRangeGesture) {
            result = this.mTextView.performHandwritingSelectRangeGesture((SelectRangeGesture) gesture);
        } else if (gesture instanceof DeleteGesture) {
            result = this.mTextView.performHandwritingDeleteGesture((DeleteGesture) gesture);
        } else if (gesture instanceof DeleteRangeGesture) {
            result = this.mTextView.performHandwritingDeleteRangeGesture((DeleteRangeGesture) gesture);
        } else if (gesture instanceof InsertGesture) {
            result = this.mTextView.performHandwritingInsertGesture((InsertGesture) gesture);
        } else if (gesture instanceof RemoveSpaceGesture) {
            result = this.mTextView.performHandwritingRemoveSpaceGesture((RemoveSpaceGesture) gesture);
        } else if (gesture instanceof JoinOrSplitGesture) {
            result = this.mTextView.performHandwritingJoinOrSplitGesture((JoinOrSplitGesture) gesture);
        } else if (gesture instanceof InsertModeGesture) {
            result = this.mTextView.performHandwritingInsertModeGesture((InsertModeGesture) gesture);
        } else {
            result = 2;
        }
        if (executor != null && consumer != null) {
            executor.execute(new Runnable() { // from class: com.android.internal.inputmethod.EditableInputConnection$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    consumer.accept(result);
                }
            });
        }
    }

    @Override // android.view.inputmethod.InputConnection
    public boolean previewHandwritingGesture(PreviewableHandwritingGesture gesture, CancellationSignal cancellationSignal) {
        return this.mTextView.previewHandwritingGesture(gesture, cancellationSignal);
    }

    @Override // android.view.inputmethod.DumpableInputConnection
    public void dumpDebug(ProtoOutputStream proto, long fieldId) {
        long token = proto.start(fieldId);
        this.mTextView.getText();
        getSelectedText(0);
        Editable content = getEditable();
        if (content != null) {
            int start = Selection.getSelectionStart(content);
            int end = Selection.getSelectionEnd(content);
            proto.write(1120986464259L, start);
            proto.write(1120986464260L, end);
        }
        proto.write(1120986464261L, getCursorCapsMode(0));
        proto.end(token);
    }
}
