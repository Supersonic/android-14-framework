package com.android.internal.inputmethod;

import android.graphics.Rect;
import android.p008os.Binder;
import android.p008os.Bundle;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import android.view.inputmethod.CompletionInfo;
import android.view.inputmethod.CursorAnchorInfo;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.ExtractedText;
import com.android.internal.inputmethod.IRemoteInputConnection;
/* loaded from: classes4.dex */
public interface IInputMethodSession extends IInterface {
    public static final String DESCRIPTOR = "com.android.internal.inputmethod.IInputMethodSession";

    void appPrivateCommand(String str, Bundle bundle) throws RemoteException;

    void displayCompletions(CompletionInfo[] completionInfoArr) throws RemoteException;

    void finishInput() throws RemoteException;

    void finishSession() throws RemoteException;

    void invalidateInput(EditorInfo editorInfo, IRemoteInputConnection iRemoteInputConnection, int i) throws RemoteException;

    void removeImeSurface() throws RemoteException;

    void updateCursor(Rect rect) throws RemoteException;

    void updateCursorAnchorInfo(CursorAnchorInfo cursorAnchorInfo) throws RemoteException;

    void updateExtractedText(int i, ExtractedText extractedText) throws RemoteException;

    void updateSelection(int i, int i2, int i3, int i4, int i5, int i6) throws RemoteException;

    void viewClicked(boolean z) throws RemoteException;

    /* loaded from: classes4.dex */
    public static class Default implements IInputMethodSession {
        @Override // com.android.internal.inputmethod.IInputMethodSession
        public void updateExtractedText(int token, ExtractedText text) throws RemoteException {
        }

        @Override // com.android.internal.inputmethod.IInputMethodSession
        public void updateSelection(int oldSelStart, int oldSelEnd, int newSelStart, int newSelEnd, int candidatesStart, int candidatesEnd) throws RemoteException {
        }

        @Override // com.android.internal.inputmethod.IInputMethodSession
        public void viewClicked(boolean focusChanged) throws RemoteException {
        }

        @Override // com.android.internal.inputmethod.IInputMethodSession
        public void updateCursor(Rect newCursor) throws RemoteException {
        }

        @Override // com.android.internal.inputmethod.IInputMethodSession
        public void displayCompletions(CompletionInfo[] completions) throws RemoteException {
        }

        @Override // com.android.internal.inputmethod.IInputMethodSession
        public void appPrivateCommand(String action, Bundle data) throws RemoteException {
        }

        @Override // com.android.internal.inputmethod.IInputMethodSession
        public void finishSession() throws RemoteException {
        }

        @Override // com.android.internal.inputmethod.IInputMethodSession
        public void updateCursorAnchorInfo(CursorAnchorInfo cursorAnchorInfo) throws RemoteException {
        }

        @Override // com.android.internal.inputmethod.IInputMethodSession
        public void removeImeSurface() throws RemoteException {
        }

        @Override // com.android.internal.inputmethod.IInputMethodSession
        public void finishInput() throws RemoteException {
        }

        @Override // com.android.internal.inputmethod.IInputMethodSession
        public void invalidateInput(EditorInfo editorInfo, IRemoteInputConnection inputConnection, int sessionId) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes4.dex */
    public static abstract class Stub extends Binder implements IInputMethodSession {
        static final int TRANSACTION_appPrivateCommand = 6;
        static final int TRANSACTION_displayCompletions = 5;
        static final int TRANSACTION_finishInput = 10;
        static final int TRANSACTION_finishSession = 7;
        static final int TRANSACTION_invalidateInput = 11;
        static final int TRANSACTION_removeImeSurface = 9;
        static final int TRANSACTION_updateCursor = 4;
        static final int TRANSACTION_updateCursorAnchorInfo = 8;
        static final int TRANSACTION_updateExtractedText = 1;
        static final int TRANSACTION_updateSelection = 2;
        static final int TRANSACTION_viewClicked = 3;

        public Stub() {
            attachInterface(this, IInputMethodSession.DESCRIPTOR);
        }

        public static IInputMethodSession asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IInputMethodSession.DESCRIPTOR);
            if (iin != null && (iin instanceof IInputMethodSession)) {
                return (IInputMethodSession) iin;
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
                    return "updateExtractedText";
                case 2:
                    return "updateSelection";
                case 3:
                    return "viewClicked";
                case 4:
                    return "updateCursor";
                case 5:
                    return "displayCompletions";
                case 6:
                    return "appPrivateCommand";
                case 7:
                    return "finishSession";
                case 8:
                    return "updateCursorAnchorInfo";
                case 9:
                    return "removeImeSurface";
                case 10:
                    return "finishInput";
                case 11:
                    return "invalidateInput";
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
                data.enforceInterface(IInputMethodSession.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IInputMethodSession.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            int _arg0 = data.readInt();
                            ExtractedText _arg1 = (ExtractedText) data.readTypedObject(ExtractedText.CREATOR);
                            data.enforceNoDataAvail();
                            updateExtractedText(_arg0, _arg1);
                            break;
                        case 2:
                            int _arg02 = data.readInt();
                            int _arg12 = data.readInt();
                            int _arg2 = data.readInt();
                            int _arg3 = data.readInt();
                            int _arg4 = data.readInt();
                            int _arg5 = data.readInt();
                            data.enforceNoDataAvail();
                            updateSelection(_arg02, _arg12, _arg2, _arg3, _arg4, _arg5);
                            break;
                        case 3:
                            boolean _arg03 = data.readBoolean();
                            data.enforceNoDataAvail();
                            viewClicked(_arg03);
                            break;
                        case 4:
                            Rect _arg04 = (Rect) data.readTypedObject(Rect.CREATOR);
                            data.enforceNoDataAvail();
                            updateCursor(_arg04);
                            break;
                        case 5:
                            CompletionInfo[] _arg05 = (CompletionInfo[]) data.createTypedArray(CompletionInfo.CREATOR);
                            data.enforceNoDataAvail();
                            displayCompletions(_arg05);
                            break;
                        case 6:
                            String _arg06 = data.readString();
                            Bundle _arg13 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            data.enforceNoDataAvail();
                            appPrivateCommand(_arg06, _arg13);
                            break;
                        case 7:
                            finishSession();
                            break;
                        case 8:
                            CursorAnchorInfo _arg07 = (CursorAnchorInfo) data.readTypedObject(CursorAnchorInfo.CREATOR);
                            data.enforceNoDataAvail();
                            updateCursorAnchorInfo(_arg07);
                            break;
                        case 9:
                            removeImeSurface();
                            break;
                        case 10:
                            finishInput();
                            break;
                        case 11:
                            EditorInfo _arg08 = (EditorInfo) data.readTypedObject(EditorInfo.CREATOR);
                            IRemoteInputConnection _arg14 = IRemoteInputConnection.Stub.asInterface(data.readStrongBinder());
                            int _arg22 = data.readInt();
                            data.enforceNoDataAvail();
                            invalidateInput(_arg08, _arg14, _arg22);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes4.dex */
        public static class Proxy implements IInputMethodSession {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IInputMethodSession.DESCRIPTOR;
            }

            @Override // com.android.internal.inputmethod.IInputMethodSession
            public void updateExtractedText(int token, ExtractedText text) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IInputMethodSession.DESCRIPTOR);
                    _data.writeInt(token);
                    _data.writeTypedObject(text, 0);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.inputmethod.IInputMethodSession
            public void updateSelection(int oldSelStart, int oldSelEnd, int newSelStart, int newSelEnd, int candidatesStart, int candidatesEnd) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IInputMethodSession.DESCRIPTOR);
                    _data.writeInt(oldSelStart);
                    _data.writeInt(oldSelEnd);
                    _data.writeInt(newSelStart);
                    _data.writeInt(newSelEnd);
                    _data.writeInt(candidatesStart);
                    _data.writeInt(candidatesEnd);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.inputmethod.IInputMethodSession
            public void viewClicked(boolean focusChanged) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IInputMethodSession.DESCRIPTOR);
                    _data.writeBoolean(focusChanged);
                    this.mRemote.transact(3, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.inputmethod.IInputMethodSession
            public void updateCursor(Rect newCursor) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IInputMethodSession.DESCRIPTOR);
                    _data.writeTypedObject(newCursor, 0);
                    this.mRemote.transact(4, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.inputmethod.IInputMethodSession
            public void displayCompletions(CompletionInfo[] completions) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IInputMethodSession.DESCRIPTOR);
                    _data.writeTypedArray(completions, 0);
                    this.mRemote.transact(5, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.inputmethod.IInputMethodSession
            public void appPrivateCommand(String action, Bundle data) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IInputMethodSession.DESCRIPTOR);
                    _data.writeString(action);
                    _data.writeTypedObject(data, 0);
                    this.mRemote.transact(6, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.inputmethod.IInputMethodSession
            public void finishSession() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IInputMethodSession.DESCRIPTOR);
                    this.mRemote.transact(7, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.inputmethod.IInputMethodSession
            public void updateCursorAnchorInfo(CursorAnchorInfo cursorAnchorInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IInputMethodSession.DESCRIPTOR);
                    _data.writeTypedObject(cursorAnchorInfo, 0);
                    this.mRemote.transact(8, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.inputmethod.IInputMethodSession
            public void removeImeSurface() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IInputMethodSession.DESCRIPTOR);
                    this.mRemote.transact(9, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.inputmethod.IInputMethodSession
            public void finishInput() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IInputMethodSession.DESCRIPTOR);
                    this.mRemote.transact(10, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.inputmethod.IInputMethodSession
            public void invalidateInput(EditorInfo editorInfo, IRemoteInputConnection inputConnection, int sessionId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IInputMethodSession.DESCRIPTOR);
                    _data.writeTypedObject(editorInfo, 0);
                    _data.writeStrongInterface(inputConnection);
                    _data.writeInt(sessionId);
                    this.mRemote.transact(11, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 10;
        }
    }
}
