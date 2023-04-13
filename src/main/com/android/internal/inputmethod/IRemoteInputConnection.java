package com.android.internal.inputmethod;

import android.graphics.RectF;
import android.p008os.Binder;
import android.p008os.Bundle;
import android.p008os.IBinder;
import android.p008os.ICancellationSignal;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import android.p008os.ResultReceiver;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.inputmethod.CompletionInfo;
import android.view.inputmethod.CorrectionInfo;
import android.view.inputmethod.ExtractedTextRequest;
import android.view.inputmethod.InputContentInfo;
import android.view.inputmethod.ParcelableHandwritingGesture;
import android.view.inputmethod.TextAttribute;
import com.android.internal.infra.AndroidFuture;
/* loaded from: classes4.dex */
public interface IRemoteInputConnection extends IInterface {
    public static final String DESCRIPTOR = "com.android.internal.inputmethod.IRemoteInputConnection";

    void beginBatchEdit(InputConnectionCommandHeader inputConnectionCommandHeader) throws RemoteException;

    void clearMetaKeyStates(InputConnectionCommandHeader inputConnectionCommandHeader, int i) throws RemoteException;

    void commitCompletion(InputConnectionCommandHeader inputConnectionCommandHeader, CompletionInfo completionInfo) throws RemoteException;

    void commitContent(InputConnectionCommandHeader inputConnectionCommandHeader, InputContentInfo inputContentInfo, int i, Bundle bundle, AndroidFuture androidFuture) throws RemoteException;

    void commitCorrection(InputConnectionCommandHeader inputConnectionCommandHeader, CorrectionInfo correctionInfo) throws RemoteException;

    void commitText(InputConnectionCommandHeader inputConnectionCommandHeader, CharSequence charSequence, int i) throws RemoteException;

    void commitTextWithTextAttribute(InputConnectionCommandHeader inputConnectionCommandHeader, CharSequence charSequence, int i, TextAttribute textAttribute) throws RemoteException;

    void deleteSurroundingText(InputConnectionCommandHeader inputConnectionCommandHeader, int i, int i2) throws RemoteException;

    void deleteSurroundingTextInCodePoints(InputConnectionCommandHeader inputConnectionCommandHeader, int i, int i2) throws RemoteException;

    void endBatchEdit(InputConnectionCommandHeader inputConnectionCommandHeader) throws RemoteException;

    void finishComposingText(InputConnectionCommandHeader inputConnectionCommandHeader) throws RemoteException;

    void getCursorCapsMode(InputConnectionCommandHeader inputConnectionCommandHeader, int i, AndroidFuture androidFuture) throws RemoteException;

    void getExtractedText(InputConnectionCommandHeader inputConnectionCommandHeader, ExtractedTextRequest extractedTextRequest, int i, AndroidFuture androidFuture) throws RemoteException;

    void getSelectedText(InputConnectionCommandHeader inputConnectionCommandHeader, int i, AndroidFuture androidFuture) throws RemoteException;

    void getSurroundingText(InputConnectionCommandHeader inputConnectionCommandHeader, int i, int i2, int i3, AndroidFuture androidFuture) throws RemoteException;

    void getTextAfterCursor(InputConnectionCommandHeader inputConnectionCommandHeader, int i, int i2, AndroidFuture androidFuture) throws RemoteException;

    void getTextBeforeCursor(InputConnectionCommandHeader inputConnectionCommandHeader, int i, int i2, AndroidFuture androidFuture) throws RemoteException;

    void performContextMenuAction(InputConnectionCommandHeader inputConnectionCommandHeader, int i) throws RemoteException;

    void performEditorAction(InputConnectionCommandHeader inputConnectionCommandHeader, int i) throws RemoteException;

    void performHandwritingGesture(InputConnectionCommandHeader inputConnectionCommandHeader, ParcelableHandwritingGesture parcelableHandwritingGesture, ResultReceiver resultReceiver) throws RemoteException;

    void performPrivateCommand(InputConnectionCommandHeader inputConnectionCommandHeader, String str, Bundle bundle) throws RemoteException;

    void performSpellCheck(InputConnectionCommandHeader inputConnectionCommandHeader) throws RemoteException;

    void previewHandwritingGesture(InputConnectionCommandHeader inputConnectionCommandHeader, ParcelableHandwritingGesture parcelableHandwritingGesture, ICancellationSignal iCancellationSignal) throws RemoteException;

    void replaceText(InputConnectionCommandHeader inputConnectionCommandHeader, int i, int i2, CharSequence charSequence, int i3, TextAttribute textAttribute) throws RemoteException;

    void requestCursorUpdates(InputConnectionCommandHeader inputConnectionCommandHeader, int i, int i2, AndroidFuture androidFuture) throws RemoteException;

    void requestCursorUpdatesWithFilter(InputConnectionCommandHeader inputConnectionCommandHeader, int i, int i2, int i3, AndroidFuture androidFuture) throws RemoteException;

    void requestTextBoundsInfo(InputConnectionCommandHeader inputConnectionCommandHeader, RectF rectF, ResultReceiver resultReceiver) throws RemoteException;

    void sendKeyEvent(InputConnectionCommandHeader inputConnectionCommandHeader, KeyEvent keyEvent) throws RemoteException;

    void setComposingRegion(InputConnectionCommandHeader inputConnectionCommandHeader, int i, int i2) throws RemoteException;

    void setComposingRegionWithTextAttribute(InputConnectionCommandHeader inputConnectionCommandHeader, int i, int i2, TextAttribute textAttribute) throws RemoteException;

    void setComposingText(InputConnectionCommandHeader inputConnectionCommandHeader, CharSequence charSequence, int i) throws RemoteException;

    void setComposingTextWithTextAttribute(InputConnectionCommandHeader inputConnectionCommandHeader, CharSequence charSequence, int i, TextAttribute textAttribute) throws RemoteException;

    void setImeConsumesInput(InputConnectionCommandHeader inputConnectionCommandHeader, boolean z) throws RemoteException;

    void setSelection(InputConnectionCommandHeader inputConnectionCommandHeader, int i, int i2) throws RemoteException;

    /* loaded from: classes4.dex */
    public static class Default implements IRemoteInputConnection {
        @Override // com.android.internal.inputmethod.IRemoteInputConnection
        public void getTextBeforeCursor(InputConnectionCommandHeader header, int length, int flags, AndroidFuture future) throws RemoteException {
        }

        @Override // com.android.internal.inputmethod.IRemoteInputConnection
        public void getTextAfterCursor(InputConnectionCommandHeader header, int length, int flags, AndroidFuture future) throws RemoteException {
        }

        @Override // com.android.internal.inputmethod.IRemoteInputConnection
        public void getCursorCapsMode(InputConnectionCommandHeader header, int reqModes, AndroidFuture future) throws RemoteException {
        }

        @Override // com.android.internal.inputmethod.IRemoteInputConnection
        public void getExtractedText(InputConnectionCommandHeader header, ExtractedTextRequest request, int flags, AndroidFuture future) throws RemoteException {
        }

        @Override // com.android.internal.inputmethod.IRemoteInputConnection
        public void deleteSurroundingText(InputConnectionCommandHeader header, int beforeLength, int afterLength) throws RemoteException {
        }

        @Override // com.android.internal.inputmethod.IRemoteInputConnection
        public void deleteSurroundingTextInCodePoints(InputConnectionCommandHeader header, int beforeLength, int afterLength) throws RemoteException {
        }

        @Override // com.android.internal.inputmethod.IRemoteInputConnection
        public void setComposingText(InputConnectionCommandHeader header, CharSequence text, int newCursorPosition) throws RemoteException {
        }

        @Override // com.android.internal.inputmethod.IRemoteInputConnection
        public void setComposingTextWithTextAttribute(InputConnectionCommandHeader header, CharSequence text, int newCursorPosition, TextAttribute textAttribute) throws RemoteException {
        }

        @Override // com.android.internal.inputmethod.IRemoteInputConnection
        public void finishComposingText(InputConnectionCommandHeader header) throws RemoteException {
        }

        @Override // com.android.internal.inputmethod.IRemoteInputConnection
        public void commitText(InputConnectionCommandHeader header, CharSequence text, int newCursorPosition) throws RemoteException {
        }

        @Override // com.android.internal.inputmethod.IRemoteInputConnection
        public void commitTextWithTextAttribute(InputConnectionCommandHeader header, CharSequence text, int newCursorPosition, TextAttribute textAttribute) throws RemoteException {
        }

        @Override // com.android.internal.inputmethod.IRemoteInputConnection
        public void commitCompletion(InputConnectionCommandHeader header, CompletionInfo completion) throws RemoteException {
        }

        @Override // com.android.internal.inputmethod.IRemoteInputConnection
        public void commitCorrection(InputConnectionCommandHeader header, CorrectionInfo correction) throws RemoteException {
        }

        @Override // com.android.internal.inputmethod.IRemoteInputConnection
        public void setSelection(InputConnectionCommandHeader header, int start, int end) throws RemoteException {
        }

        @Override // com.android.internal.inputmethod.IRemoteInputConnection
        public void performEditorAction(InputConnectionCommandHeader header, int actionCode) throws RemoteException {
        }

        @Override // com.android.internal.inputmethod.IRemoteInputConnection
        public void performContextMenuAction(InputConnectionCommandHeader header, int id) throws RemoteException {
        }

        @Override // com.android.internal.inputmethod.IRemoteInputConnection
        public void beginBatchEdit(InputConnectionCommandHeader header) throws RemoteException {
        }

        @Override // com.android.internal.inputmethod.IRemoteInputConnection
        public void endBatchEdit(InputConnectionCommandHeader header) throws RemoteException {
        }

        @Override // com.android.internal.inputmethod.IRemoteInputConnection
        public void sendKeyEvent(InputConnectionCommandHeader header, KeyEvent event) throws RemoteException {
        }

        @Override // com.android.internal.inputmethod.IRemoteInputConnection
        public void clearMetaKeyStates(InputConnectionCommandHeader header, int states) throws RemoteException {
        }

        @Override // com.android.internal.inputmethod.IRemoteInputConnection
        public void performSpellCheck(InputConnectionCommandHeader header) throws RemoteException {
        }

        @Override // com.android.internal.inputmethod.IRemoteInputConnection
        public void performPrivateCommand(InputConnectionCommandHeader header, String action, Bundle data) throws RemoteException {
        }

        @Override // com.android.internal.inputmethod.IRemoteInputConnection
        public void performHandwritingGesture(InputConnectionCommandHeader header, ParcelableHandwritingGesture gesture, ResultReceiver resultReceiver) throws RemoteException {
        }

        @Override // com.android.internal.inputmethod.IRemoteInputConnection
        public void previewHandwritingGesture(InputConnectionCommandHeader header, ParcelableHandwritingGesture gesture, ICancellationSignal transport) throws RemoteException {
        }

        @Override // com.android.internal.inputmethod.IRemoteInputConnection
        public void setComposingRegion(InputConnectionCommandHeader header, int start, int end) throws RemoteException {
        }

        @Override // com.android.internal.inputmethod.IRemoteInputConnection
        public void setComposingRegionWithTextAttribute(InputConnectionCommandHeader header, int start, int end, TextAttribute textAttribute) throws RemoteException {
        }

        @Override // com.android.internal.inputmethod.IRemoteInputConnection
        public void getSelectedText(InputConnectionCommandHeader header, int flags, AndroidFuture future) throws RemoteException {
        }

        @Override // com.android.internal.inputmethod.IRemoteInputConnection
        public void requestCursorUpdates(InputConnectionCommandHeader header, int cursorUpdateMode, int imeDisplayId, AndroidFuture future) throws RemoteException {
        }

        @Override // com.android.internal.inputmethod.IRemoteInputConnection
        public void requestCursorUpdatesWithFilter(InputConnectionCommandHeader header, int cursorUpdateMode, int cursorUpdateFilter, int imeDisplayId, AndroidFuture future) throws RemoteException {
        }

        @Override // com.android.internal.inputmethod.IRemoteInputConnection
        public void requestTextBoundsInfo(InputConnectionCommandHeader header, RectF bounds, ResultReceiver resultReceiver) throws RemoteException {
        }

        @Override // com.android.internal.inputmethod.IRemoteInputConnection
        public void commitContent(InputConnectionCommandHeader header, InputContentInfo inputContentInfo, int flags, Bundle opts, AndroidFuture future) throws RemoteException {
        }

        @Override // com.android.internal.inputmethod.IRemoteInputConnection
        public void getSurroundingText(InputConnectionCommandHeader header, int beforeLength, int afterLength, int flags, AndroidFuture future) throws RemoteException {
        }

        @Override // com.android.internal.inputmethod.IRemoteInputConnection
        public void setImeConsumesInput(InputConnectionCommandHeader header, boolean imeConsumesInput) throws RemoteException {
        }

        @Override // com.android.internal.inputmethod.IRemoteInputConnection
        public void replaceText(InputConnectionCommandHeader header, int start, int end, CharSequence text, int newCursorPosition, TextAttribute textAttribute) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes4.dex */
    public static abstract class Stub extends Binder implements IRemoteInputConnection {
        static final int TRANSACTION_beginBatchEdit = 17;
        static final int TRANSACTION_clearMetaKeyStates = 20;
        static final int TRANSACTION_commitCompletion = 12;
        static final int TRANSACTION_commitContent = 31;
        static final int TRANSACTION_commitCorrection = 13;
        static final int TRANSACTION_commitText = 10;
        static final int TRANSACTION_commitTextWithTextAttribute = 11;
        static final int TRANSACTION_deleteSurroundingText = 5;
        static final int TRANSACTION_deleteSurroundingTextInCodePoints = 6;
        static final int TRANSACTION_endBatchEdit = 18;
        static final int TRANSACTION_finishComposingText = 9;
        static final int TRANSACTION_getCursorCapsMode = 3;
        static final int TRANSACTION_getExtractedText = 4;
        static final int TRANSACTION_getSelectedText = 27;
        static final int TRANSACTION_getSurroundingText = 32;
        static final int TRANSACTION_getTextAfterCursor = 2;
        static final int TRANSACTION_getTextBeforeCursor = 1;
        static final int TRANSACTION_performContextMenuAction = 16;
        static final int TRANSACTION_performEditorAction = 15;
        static final int TRANSACTION_performHandwritingGesture = 23;
        static final int TRANSACTION_performPrivateCommand = 22;
        static final int TRANSACTION_performSpellCheck = 21;
        static final int TRANSACTION_previewHandwritingGesture = 24;
        static final int TRANSACTION_replaceText = 34;
        static final int TRANSACTION_requestCursorUpdates = 28;
        static final int TRANSACTION_requestCursorUpdatesWithFilter = 29;
        static final int TRANSACTION_requestTextBoundsInfo = 30;
        static final int TRANSACTION_sendKeyEvent = 19;
        static final int TRANSACTION_setComposingRegion = 25;
        static final int TRANSACTION_setComposingRegionWithTextAttribute = 26;
        static final int TRANSACTION_setComposingText = 7;
        static final int TRANSACTION_setComposingTextWithTextAttribute = 8;
        static final int TRANSACTION_setImeConsumesInput = 33;
        static final int TRANSACTION_setSelection = 14;

        public Stub() {
            attachInterface(this, IRemoteInputConnection.DESCRIPTOR);
        }

        public static IRemoteInputConnection asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IRemoteInputConnection.DESCRIPTOR);
            if (iin != null && (iin instanceof IRemoteInputConnection)) {
                return (IRemoteInputConnection) iin;
            }
            return new Proxy(obj);
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return this;
        }

        public static String getDefaultTransactionName(int transactionCode) {
            switch (transactionCode) {
                case 1:
                    return "getTextBeforeCursor";
                case 2:
                    return "getTextAfterCursor";
                case 3:
                    return "getCursorCapsMode";
                case 4:
                    return "getExtractedText";
                case 5:
                    return "deleteSurroundingText";
                case 6:
                    return "deleteSurroundingTextInCodePoints";
                case 7:
                    return "setComposingText";
                case 8:
                    return "setComposingTextWithTextAttribute";
                case 9:
                    return "finishComposingText";
                case 10:
                    return "commitText";
                case 11:
                    return "commitTextWithTextAttribute";
                case 12:
                    return "commitCompletion";
                case 13:
                    return "commitCorrection";
                case 14:
                    return "setSelection";
                case 15:
                    return "performEditorAction";
                case 16:
                    return "performContextMenuAction";
                case 17:
                    return "beginBatchEdit";
                case 18:
                    return "endBatchEdit";
                case 19:
                    return "sendKeyEvent";
                case 20:
                    return "clearMetaKeyStates";
                case 21:
                    return "performSpellCheck";
                case 22:
                    return "performPrivateCommand";
                case 23:
                    return "performHandwritingGesture";
                case 24:
                    return "previewHandwritingGesture";
                case 25:
                    return "setComposingRegion";
                case 26:
                    return "setComposingRegionWithTextAttribute";
                case 27:
                    return "getSelectedText";
                case 28:
                    return "requestCursorUpdates";
                case 29:
                    return "requestCursorUpdatesWithFilter";
                case 30:
                    return "requestTextBoundsInfo";
                case 31:
                    return "commitContent";
                case 32:
                    return "getSurroundingText";
                case 33:
                    return "setImeConsumesInput";
                case 34:
                    return "replaceText";
                default:
                    return null;
            }
        }

        @Override // android.p008os.Binder
        public String getTransactionName(int transactionCode) {
            return getDefaultTransactionName(transactionCode);
        }

        @Override // android.p008os.Binder
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            if (code >= 1 && code <= 16777215) {
                data.enforceInterface(IRemoteInputConnection.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IRemoteInputConnection.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            InputConnectionCommandHeader _arg0 = (InputConnectionCommandHeader) data.readTypedObject(InputConnectionCommandHeader.CREATOR);
                            int _arg1 = data.readInt();
                            int _arg2 = data.readInt();
                            AndroidFuture _arg3 = (AndroidFuture) data.readTypedObject(AndroidFuture.CREATOR);
                            data.enforceNoDataAvail();
                            getTextBeforeCursor(_arg0, _arg1, _arg2, _arg3);
                            break;
                        case 2:
                            InputConnectionCommandHeader _arg02 = (InputConnectionCommandHeader) data.readTypedObject(InputConnectionCommandHeader.CREATOR);
                            int _arg12 = data.readInt();
                            int _arg22 = data.readInt();
                            AndroidFuture _arg32 = (AndroidFuture) data.readTypedObject(AndroidFuture.CREATOR);
                            data.enforceNoDataAvail();
                            getTextAfterCursor(_arg02, _arg12, _arg22, _arg32);
                            break;
                        case 3:
                            InputConnectionCommandHeader _arg03 = (InputConnectionCommandHeader) data.readTypedObject(InputConnectionCommandHeader.CREATOR);
                            int _arg13 = data.readInt();
                            AndroidFuture _arg23 = (AndroidFuture) data.readTypedObject(AndroidFuture.CREATOR);
                            data.enforceNoDataAvail();
                            getCursorCapsMode(_arg03, _arg13, _arg23);
                            break;
                        case 4:
                            InputConnectionCommandHeader _arg04 = (InputConnectionCommandHeader) data.readTypedObject(InputConnectionCommandHeader.CREATOR);
                            ExtractedTextRequest _arg14 = (ExtractedTextRequest) data.readTypedObject(ExtractedTextRequest.CREATOR);
                            int _arg24 = data.readInt();
                            AndroidFuture _arg33 = (AndroidFuture) data.readTypedObject(AndroidFuture.CREATOR);
                            data.enforceNoDataAvail();
                            getExtractedText(_arg04, _arg14, _arg24, _arg33);
                            break;
                        case 5:
                            InputConnectionCommandHeader _arg05 = (InputConnectionCommandHeader) data.readTypedObject(InputConnectionCommandHeader.CREATOR);
                            int _arg15 = data.readInt();
                            int _arg25 = data.readInt();
                            data.enforceNoDataAvail();
                            deleteSurroundingText(_arg05, _arg15, _arg25);
                            break;
                        case 6:
                            InputConnectionCommandHeader _arg06 = (InputConnectionCommandHeader) data.readTypedObject(InputConnectionCommandHeader.CREATOR);
                            int _arg16 = data.readInt();
                            int _arg26 = data.readInt();
                            data.enforceNoDataAvail();
                            deleteSurroundingTextInCodePoints(_arg06, _arg16, _arg26);
                            break;
                        case 7:
                            InputConnectionCommandHeader _arg07 = (InputConnectionCommandHeader) data.readTypedObject(InputConnectionCommandHeader.CREATOR);
                            CharSequence _arg17 = (CharSequence) data.readTypedObject(TextUtils.CHAR_SEQUENCE_CREATOR);
                            int _arg27 = data.readInt();
                            data.enforceNoDataAvail();
                            setComposingText(_arg07, _arg17, _arg27);
                            break;
                        case 8:
                            InputConnectionCommandHeader _arg08 = (InputConnectionCommandHeader) data.readTypedObject(InputConnectionCommandHeader.CREATOR);
                            CharSequence _arg18 = (CharSequence) data.readTypedObject(TextUtils.CHAR_SEQUENCE_CREATOR);
                            int _arg28 = data.readInt();
                            TextAttribute _arg34 = (TextAttribute) data.readTypedObject(TextAttribute.CREATOR);
                            data.enforceNoDataAvail();
                            setComposingTextWithTextAttribute(_arg08, _arg18, _arg28, _arg34);
                            break;
                        case 9:
                            InputConnectionCommandHeader _arg09 = (InputConnectionCommandHeader) data.readTypedObject(InputConnectionCommandHeader.CREATOR);
                            data.enforceNoDataAvail();
                            finishComposingText(_arg09);
                            break;
                        case 10:
                            InputConnectionCommandHeader _arg010 = (InputConnectionCommandHeader) data.readTypedObject(InputConnectionCommandHeader.CREATOR);
                            CharSequence _arg19 = (CharSequence) data.readTypedObject(TextUtils.CHAR_SEQUENCE_CREATOR);
                            int _arg29 = data.readInt();
                            data.enforceNoDataAvail();
                            commitText(_arg010, _arg19, _arg29);
                            break;
                        case 11:
                            InputConnectionCommandHeader _arg011 = (InputConnectionCommandHeader) data.readTypedObject(InputConnectionCommandHeader.CREATOR);
                            CharSequence _arg110 = (CharSequence) data.readTypedObject(TextUtils.CHAR_SEQUENCE_CREATOR);
                            int _arg210 = data.readInt();
                            TextAttribute _arg35 = (TextAttribute) data.readTypedObject(TextAttribute.CREATOR);
                            data.enforceNoDataAvail();
                            commitTextWithTextAttribute(_arg011, _arg110, _arg210, _arg35);
                            break;
                        case 12:
                            InputConnectionCommandHeader _arg012 = (InputConnectionCommandHeader) data.readTypedObject(InputConnectionCommandHeader.CREATOR);
                            CompletionInfo _arg111 = (CompletionInfo) data.readTypedObject(CompletionInfo.CREATOR);
                            data.enforceNoDataAvail();
                            commitCompletion(_arg012, _arg111);
                            break;
                        case 13:
                            InputConnectionCommandHeader _arg013 = (InputConnectionCommandHeader) data.readTypedObject(InputConnectionCommandHeader.CREATOR);
                            CorrectionInfo _arg112 = (CorrectionInfo) data.readTypedObject(CorrectionInfo.CREATOR);
                            data.enforceNoDataAvail();
                            commitCorrection(_arg013, _arg112);
                            break;
                        case 14:
                            InputConnectionCommandHeader _arg014 = (InputConnectionCommandHeader) data.readTypedObject(InputConnectionCommandHeader.CREATOR);
                            int _arg113 = data.readInt();
                            int _arg211 = data.readInt();
                            data.enforceNoDataAvail();
                            setSelection(_arg014, _arg113, _arg211);
                            break;
                        case 15:
                            InputConnectionCommandHeader _arg015 = (InputConnectionCommandHeader) data.readTypedObject(InputConnectionCommandHeader.CREATOR);
                            int _arg114 = data.readInt();
                            data.enforceNoDataAvail();
                            performEditorAction(_arg015, _arg114);
                            break;
                        case 16:
                            InputConnectionCommandHeader _arg016 = (InputConnectionCommandHeader) data.readTypedObject(InputConnectionCommandHeader.CREATOR);
                            int _arg115 = data.readInt();
                            data.enforceNoDataAvail();
                            performContextMenuAction(_arg016, _arg115);
                            break;
                        case 17:
                            InputConnectionCommandHeader _arg017 = (InputConnectionCommandHeader) data.readTypedObject(InputConnectionCommandHeader.CREATOR);
                            data.enforceNoDataAvail();
                            beginBatchEdit(_arg017);
                            break;
                        case 18:
                            InputConnectionCommandHeader _arg018 = (InputConnectionCommandHeader) data.readTypedObject(InputConnectionCommandHeader.CREATOR);
                            data.enforceNoDataAvail();
                            endBatchEdit(_arg018);
                            break;
                        case 19:
                            InputConnectionCommandHeader _arg019 = (InputConnectionCommandHeader) data.readTypedObject(InputConnectionCommandHeader.CREATOR);
                            KeyEvent _arg116 = (KeyEvent) data.readTypedObject(KeyEvent.CREATOR);
                            data.enforceNoDataAvail();
                            sendKeyEvent(_arg019, _arg116);
                            break;
                        case 20:
                            InputConnectionCommandHeader _arg020 = (InputConnectionCommandHeader) data.readTypedObject(InputConnectionCommandHeader.CREATOR);
                            int _arg117 = data.readInt();
                            data.enforceNoDataAvail();
                            clearMetaKeyStates(_arg020, _arg117);
                            break;
                        case 21:
                            InputConnectionCommandHeader _arg021 = (InputConnectionCommandHeader) data.readTypedObject(InputConnectionCommandHeader.CREATOR);
                            data.enforceNoDataAvail();
                            performSpellCheck(_arg021);
                            break;
                        case 22:
                            InputConnectionCommandHeader _arg022 = (InputConnectionCommandHeader) data.readTypedObject(InputConnectionCommandHeader.CREATOR);
                            String _arg118 = data.readString();
                            Bundle _arg212 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            data.enforceNoDataAvail();
                            performPrivateCommand(_arg022, _arg118, _arg212);
                            break;
                        case 23:
                            InputConnectionCommandHeader _arg023 = (InputConnectionCommandHeader) data.readTypedObject(InputConnectionCommandHeader.CREATOR);
                            ParcelableHandwritingGesture _arg119 = (ParcelableHandwritingGesture) data.readTypedObject(ParcelableHandwritingGesture.CREATOR);
                            ResultReceiver _arg213 = (ResultReceiver) data.readTypedObject(ResultReceiver.CREATOR);
                            data.enforceNoDataAvail();
                            performHandwritingGesture(_arg023, _arg119, _arg213);
                            break;
                        case 24:
                            InputConnectionCommandHeader _arg024 = (InputConnectionCommandHeader) data.readTypedObject(InputConnectionCommandHeader.CREATOR);
                            ParcelableHandwritingGesture _arg120 = (ParcelableHandwritingGesture) data.readTypedObject(ParcelableHandwritingGesture.CREATOR);
                            ICancellationSignal _arg214 = ICancellationSignal.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            previewHandwritingGesture(_arg024, _arg120, _arg214);
                            break;
                        case 25:
                            InputConnectionCommandHeader _arg025 = (InputConnectionCommandHeader) data.readTypedObject(InputConnectionCommandHeader.CREATOR);
                            int _arg121 = data.readInt();
                            int _arg215 = data.readInt();
                            data.enforceNoDataAvail();
                            setComposingRegion(_arg025, _arg121, _arg215);
                            break;
                        case 26:
                            InputConnectionCommandHeader _arg026 = (InputConnectionCommandHeader) data.readTypedObject(InputConnectionCommandHeader.CREATOR);
                            int _arg122 = data.readInt();
                            int _arg216 = data.readInt();
                            TextAttribute _arg36 = (TextAttribute) data.readTypedObject(TextAttribute.CREATOR);
                            data.enforceNoDataAvail();
                            setComposingRegionWithTextAttribute(_arg026, _arg122, _arg216, _arg36);
                            break;
                        case 27:
                            InputConnectionCommandHeader _arg027 = (InputConnectionCommandHeader) data.readTypedObject(InputConnectionCommandHeader.CREATOR);
                            int _arg123 = data.readInt();
                            AndroidFuture _arg217 = (AndroidFuture) data.readTypedObject(AndroidFuture.CREATOR);
                            data.enforceNoDataAvail();
                            getSelectedText(_arg027, _arg123, _arg217);
                            break;
                        case 28:
                            InputConnectionCommandHeader _arg028 = (InputConnectionCommandHeader) data.readTypedObject(InputConnectionCommandHeader.CREATOR);
                            int _arg124 = data.readInt();
                            int _arg218 = data.readInt();
                            AndroidFuture _arg37 = (AndroidFuture) data.readTypedObject(AndroidFuture.CREATOR);
                            data.enforceNoDataAvail();
                            requestCursorUpdates(_arg028, _arg124, _arg218, _arg37);
                            break;
                        case 29:
                            InputConnectionCommandHeader _arg029 = (InputConnectionCommandHeader) data.readTypedObject(InputConnectionCommandHeader.CREATOR);
                            int _arg125 = data.readInt();
                            int _arg219 = data.readInt();
                            int _arg38 = data.readInt();
                            AndroidFuture _arg4 = (AndroidFuture) data.readTypedObject(AndroidFuture.CREATOR);
                            data.enforceNoDataAvail();
                            requestCursorUpdatesWithFilter(_arg029, _arg125, _arg219, _arg38, _arg4);
                            break;
                        case 30:
                            InputConnectionCommandHeader _arg030 = (InputConnectionCommandHeader) data.readTypedObject(InputConnectionCommandHeader.CREATOR);
                            RectF _arg126 = (RectF) data.readTypedObject(RectF.CREATOR);
                            ResultReceiver _arg220 = (ResultReceiver) data.readTypedObject(ResultReceiver.CREATOR);
                            data.enforceNoDataAvail();
                            requestTextBoundsInfo(_arg030, _arg126, _arg220);
                            break;
                        case 31:
                            InputConnectionCommandHeader _arg031 = (InputConnectionCommandHeader) data.readTypedObject(InputConnectionCommandHeader.CREATOR);
                            InputContentInfo _arg127 = (InputContentInfo) data.readTypedObject(InputContentInfo.CREATOR);
                            int _arg221 = data.readInt();
                            Bundle _arg39 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            AndroidFuture _arg42 = (AndroidFuture) data.readTypedObject(AndroidFuture.CREATOR);
                            data.enforceNoDataAvail();
                            commitContent(_arg031, _arg127, _arg221, _arg39, _arg42);
                            break;
                        case 32:
                            InputConnectionCommandHeader _arg032 = (InputConnectionCommandHeader) data.readTypedObject(InputConnectionCommandHeader.CREATOR);
                            int _arg128 = data.readInt();
                            int _arg222 = data.readInt();
                            int _arg310 = data.readInt();
                            AndroidFuture _arg43 = (AndroidFuture) data.readTypedObject(AndroidFuture.CREATOR);
                            data.enforceNoDataAvail();
                            getSurroundingText(_arg032, _arg128, _arg222, _arg310, _arg43);
                            break;
                        case 33:
                            InputConnectionCommandHeader _arg033 = (InputConnectionCommandHeader) data.readTypedObject(InputConnectionCommandHeader.CREATOR);
                            boolean _arg129 = data.readBoolean();
                            data.enforceNoDataAvail();
                            setImeConsumesInput(_arg033, _arg129);
                            break;
                        case 34:
                            InputConnectionCommandHeader _arg034 = (InputConnectionCommandHeader) data.readTypedObject(InputConnectionCommandHeader.CREATOR);
                            int _arg130 = data.readInt();
                            int _arg223 = data.readInt();
                            CharSequence _arg311 = (CharSequence) data.readTypedObject(TextUtils.CHAR_SEQUENCE_CREATOR);
                            int _arg44 = data.readInt();
                            TextAttribute _arg5 = (TextAttribute) data.readTypedObject(TextAttribute.CREATOR);
                            data.enforceNoDataAvail();
                            replaceText(_arg034, _arg130, _arg223, _arg311, _arg44, _arg5);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes4.dex */
        private static class Proxy implements IRemoteInputConnection {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IRemoteInputConnection.DESCRIPTOR;
            }

            @Override // com.android.internal.inputmethod.IRemoteInputConnection
            public void getTextBeforeCursor(InputConnectionCommandHeader header, int length, int flags, AndroidFuture future) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IRemoteInputConnection.DESCRIPTOR);
                    _data.writeTypedObject(header, 0);
                    _data.writeInt(length);
                    _data.writeInt(flags);
                    _data.writeTypedObject(future, 0);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.inputmethod.IRemoteInputConnection
            public void getTextAfterCursor(InputConnectionCommandHeader header, int length, int flags, AndroidFuture future) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IRemoteInputConnection.DESCRIPTOR);
                    _data.writeTypedObject(header, 0);
                    _data.writeInt(length);
                    _data.writeInt(flags);
                    _data.writeTypedObject(future, 0);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.inputmethod.IRemoteInputConnection
            public void getCursorCapsMode(InputConnectionCommandHeader header, int reqModes, AndroidFuture future) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IRemoteInputConnection.DESCRIPTOR);
                    _data.writeTypedObject(header, 0);
                    _data.writeInt(reqModes);
                    _data.writeTypedObject(future, 0);
                    this.mRemote.transact(3, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.inputmethod.IRemoteInputConnection
            public void getExtractedText(InputConnectionCommandHeader header, ExtractedTextRequest request, int flags, AndroidFuture future) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IRemoteInputConnection.DESCRIPTOR);
                    _data.writeTypedObject(header, 0);
                    _data.writeTypedObject(request, 0);
                    _data.writeInt(flags);
                    _data.writeTypedObject(future, 0);
                    this.mRemote.transact(4, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.inputmethod.IRemoteInputConnection
            public void deleteSurroundingText(InputConnectionCommandHeader header, int beforeLength, int afterLength) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IRemoteInputConnection.DESCRIPTOR);
                    _data.writeTypedObject(header, 0);
                    _data.writeInt(beforeLength);
                    _data.writeInt(afterLength);
                    this.mRemote.transact(5, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.inputmethod.IRemoteInputConnection
            public void deleteSurroundingTextInCodePoints(InputConnectionCommandHeader header, int beforeLength, int afterLength) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IRemoteInputConnection.DESCRIPTOR);
                    _data.writeTypedObject(header, 0);
                    _data.writeInt(beforeLength);
                    _data.writeInt(afterLength);
                    this.mRemote.transact(6, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.inputmethod.IRemoteInputConnection
            public void setComposingText(InputConnectionCommandHeader header, CharSequence text, int newCursorPosition) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IRemoteInputConnection.DESCRIPTOR);
                    _data.writeTypedObject(header, 0);
                    if (text != null) {
                        _data.writeInt(1);
                        TextUtils.writeToParcel(text, _data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(newCursorPosition);
                    this.mRemote.transact(7, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.inputmethod.IRemoteInputConnection
            public void setComposingTextWithTextAttribute(InputConnectionCommandHeader header, CharSequence text, int newCursorPosition, TextAttribute textAttribute) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IRemoteInputConnection.DESCRIPTOR);
                    _data.writeTypedObject(header, 0);
                    if (text != null) {
                        _data.writeInt(1);
                        TextUtils.writeToParcel(text, _data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(newCursorPosition);
                    _data.writeTypedObject(textAttribute, 0);
                    this.mRemote.transact(8, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.inputmethod.IRemoteInputConnection
            public void finishComposingText(InputConnectionCommandHeader header) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IRemoteInputConnection.DESCRIPTOR);
                    _data.writeTypedObject(header, 0);
                    this.mRemote.transact(9, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.inputmethod.IRemoteInputConnection
            public void commitText(InputConnectionCommandHeader header, CharSequence text, int newCursorPosition) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IRemoteInputConnection.DESCRIPTOR);
                    _data.writeTypedObject(header, 0);
                    if (text != null) {
                        _data.writeInt(1);
                        TextUtils.writeToParcel(text, _data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(newCursorPosition);
                    this.mRemote.transact(10, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.inputmethod.IRemoteInputConnection
            public void commitTextWithTextAttribute(InputConnectionCommandHeader header, CharSequence text, int newCursorPosition, TextAttribute textAttribute) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IRemoteInputConnection.DESCRIPTOR);
                    _data.writeTypedObject(header, 0);
                    if (text != null) {
                        _data.writeInt(1);
                        TextUtils.writeToParcel(text, _data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(newCursorPosition);
                    _data.writeTypedObject(textAttribute, 0);
                    this.mRemote.transact(11, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.inputmethod.IRemoteInputConnection
            public void commitCompletion(InputConnectionCommandHeader header, CompletionInfo completion) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IRemoteInputConnection.DESCRIPTOR);
                    _data.writeTypedObject(header, 0);
                    _data.writeTypedObject(completion, 0);
                    this.mRemote.transact(12, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.inputmethod.IRemoteInputConnection
            public void commitCorrection(InputConnectionCommandHeader header, CorrectionInfo correction) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IRemoteInputConnection.DESCRIPTOR);
                    _data.writeTypedObject(header, 0);
                    _data.writeTypedObject(correction, 0);
                    this.mRemote.transact(13, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.inputmethod.IRemoteInputConnection
            public void setSelection(InputConnectionCommandHeader header, int start, int end) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IRemoteInputConnection.DESCRIPTOR);
                    _data.writeTypedObject(header, 0);
                    _data.writeInt(start);
                    _data.writeInt(end);
                    this.mRemote.transact(14, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.inputmethod.IRemoteInputConnection
            public void performEditorAction(InputConnectionCommandHeader header, int actionCode) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IRemoteInputConnection.DESCRIPTOR);
                    _data.writeTypedObject(header, 0);
                    _data.writeInt(actionCode);
                    this.mRemote.transact(15, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.inputmethod.IRemoteInputConnection
            public void performContextMenuAction(InputConnectionCommandHeader header, int id) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IRemoteInputConnection.DESCRIPTOR);
                    _data.writeTypedObject(header, 0);
                    _data.writeInt(id);
                    this.mRemote.transact(16, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.inputmethod.IRemoteInputConnection
            public void beginBatchEdit(InputConnectionCommandHeader header) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IRemoteInputConnection.DESCRIPTOR);
                    _data.writeTypedObject(header, 0);
                    this.mRemote.transact(17, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.inputmethod.IRemoteInputConnection
            public void endBatchEdit(InputConnectionCommandHeader header) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IRemoteInputConnection.DESCRIPTOR);
                    _data.writeTypedObject(header, 0);
                    this.mRemote.transact(18, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.inputmethod.IRemoteInputConnection
            public void sendKeyEvent(InputConnectionCommandHeader header, KeyEvent event) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IRemoteInputConnection.DESCRIPTOR);
                    _data.writeTypedObject(header, 0);
                    _data.writeTypedObject(event, 0);
                    this.mRemote.transact(19, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.inputmethod.IRemoteInputConnection
            public void clearMetaKeyStates(InputConnectionCommandHeader header, int states) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IRemoteInputConnection.DESCRIPTOR);
                    _data.writeTypedObject(header, 0);
                    _data.writeInt(states);
                    this.mRemote.transact(20, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.inputmethod.IRemoteInputConnection
            public void performSpellCheck(InputConnectionCommandHeader header) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IRemoteInputConnection.DESCRIPTOR);
                    _data.writeTypedObject(header, 0);
                    this.mRemote.transact(21, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.inputmethod.IRemoteInputConnection
            public void performPrivateCommand(InputConnectionCommandHeader header, String action, Bundle data) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IRemoteInputConnection.DESCRIPTOR);
                    _data.writeTypedObject(header, 0);
                    _data.writeString(action);
                    _data.writeTypedObject(data, 0);
                    this.mRemote.transact(22, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.inputmethod.IRemoteInputConnection
            public void performHandwritingGesture(InputConnectionCommandHeader header, ParcelableHandwritingGesture gesture, ResultReceiver resultReceiver) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IRemoteInputConnection.DESCRIPTOR);
                    _data.writeTypedObject(header, 0);
                    _data.writeTypedObject(gesture, 0);
                    _data.writeTypedObject(resultReceiver, 0);
                    this.mRemote.transact(23, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.inputmethod.IRemoteInputConnection
            public void previewHandwritingGesture(InputConnectionCommandHeader header, ParcelableHandwritingGesture gesture, ICancellationSignal transport) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IRemoteInputConnection.DESCRIPTOR);
                    _data.writeTypedObject(header, 0);
                    _data.writeTypedObject(gesture, 0);
                    _data.writeStrongInterface(transport);
                    this.mRemote.transact(24, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.inputmethod.IRemoteInputConnection
            public void setComposingRegion(InputConnectionCommandHeader header, int start, int end) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IRemoteInputConnection.DESCRIPTOR);
                    _data.writeTypedObject(header, 0);
                    _data.writeInt(start);
                    _data.writeInt(end);
                    this.mRemote.transact(25, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.inputmethod.IRemoteInputConnection
            public void setComposingRegionWithTextAttribute(InputConnectionCommandHeader header, int start, int end, TextAttribute textAttribute) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IRemoteInputConnection.DESCRIPTOR);
                    _data.writeTypedObject(header, 0);
                    _data.writeInt(start);
                    _data.writeInt(end);
                    _data.writeTypedObject(textAttribute, 0);
                    this.mRemote.transact(26, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.inputmethod.IRemoteInputConnection
            public void getSelectedText(InputConnectionCommandHeader header, int flags, AndroidFuture future) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IRemoteInputConnection.DESCRIPTOR);
                    _data.writeTypedObject(header, 0);
                    _data.writeInt(flags);
                    _data.writeTypedObject(future, 0);
                    this.mRemote.transact(27, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.inputmethod.IRemoteInputConnection
            public void requestCursorUpdates(InputConnectionCommandHeader header, int cursorUpdateMode, int imeDisplayId, AndroidFuture future) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IRemoteInputConnection.DESCRIPTOR);
                    _data.writeTypedObject(header, 0);
                    _data.writeInt(cursorUpdateMode);
                    _data.writeInt(imeDisplayId);
                    _data.writeTypedObject(future, 0);
                    this.mRemote.transact(28, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.inputmethod.IRemoteInputConnection
            public void requestCursorUpdatesWithFilter(InputConnectionCommandHeader header, int cursorUpdateMode, int cursorUpdateFilter, int imeDisplayId, AndroidFuture future) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IRemoteInputConnection.DESCRIPTOR);
                    _data.writeTypedObject(header, 0);
                    _data.writeInt(cursorUpdateMode);
                    _data.writeInt(cursorUpdateFilter);
                    _data.writeInt(imeDisplayId);
                    _data.writeTypedObject(future, 0);
                    this.mRemote.transact(29, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.inputmethod.IRemoteInputConnection
            public void requestTextBoundsInfo(InputConnectionCommandHeader header, RectF bounds, ResultReceiver resultReceiver) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IRemoteInputConnection.DESCRIPTOR);
                    _data.writeTypedObject(header, 0);
                    _data.writeTypedObject(bounds, 0);
                    _data.writeTypedObject(resultReceiver, 0);
                    this.mRemote.transact(30, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.inputmethod.IRemoteInputConnection
            public void commitContent(InputConnectionCommandHeader header, InputContentInfo inputContentInfo, int flags, Bundle opts, AndroidFuture future) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IRemoteInputConnection.DESCRIPTOR);
                    _data.writeTypedObject(header, 0);
                    _data.writeTypedObject(inputContentInfo, 0);
                    _data.writeInt(flags);
                    _data.writeTypedObject(opts, 0);
                    _data.writeTypedObject(future, 0);
                    this.mRemote.transact(31, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.inputmethod.IRemoteInputConnection
            public void getSurroundingText(InputConnectionCommandHeader header, int beforeLength, int afterLength, int flags, AndroidFuture future) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IRemoteInputConnection.DESCRIPTOR);
                    _data.writeTypedObject(header, 0);
                    _data.writeInt(beforeLength);
                    _data.writeInt(afterLength);
                    _data.writeInt(flags);
                    _data.writeTypedObject(future, 0);
                    this.mRemote.transact(32, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.inputmethod.IRemoteInputConnection
            public void setImeConsumesInput(InputConnectionCommandHeader header, boolean imeConsumesInput) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IRemoteInputConnection.DESCRIPTOR);
                    _data.writeTypedObject(header, 0);
                    _data.writeBoolean(imeConsumesInput);
                    this.mRemote.transact(33, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.inputmethod.IRemoteInputConnection
            public void replaceText(InputConnectionCommandHeader header, int start, int end, CharSequence text, int newCursorPosition, TextAttribute textAttribute) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IRemoteInputConnection.DESCRIPTOR);
                    _data.writeTypedObject(header, 0);
                    _data.writeInt(start);
                    _data.writeInt(end);
                    if (text != null) {
                        _data.writeInt(1);
                        TextUtils.writeToParcel(text, _data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(newCursorPosition);
                    _data.writeTypedObject(textAttribute, 0);
                    this.mRemote.transact(34, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 33;
        }
    }
}
