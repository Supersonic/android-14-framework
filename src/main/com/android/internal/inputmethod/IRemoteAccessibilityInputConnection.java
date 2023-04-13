package com.android.internal.inputmethod;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.inputmethod.TextAttribute;
import com.android.internal.infra.AndroidFuture;
/* loaded from: classes4.dex */
public interface IRemoteAccessibilityInputConnection extends IInterface {
    public static final String DESCRIPTOR = "com.android.internal.inputmethod.IRemoteAccessibilityInputConnection";

    void clearMetaKeyStates(InputConnectionCommandHeader inputConnectionCommandHeader, int i) throws RemoteException;

    void commitText(InputConnectionCommandHeader inputConnectionCommandHeader, CharSequence charSequence, int i, TextAttribute textAttribute) throws RemoteException;

    void deleteSurroundingText(InputConnectionCommandHeader inputConnectionCommandHeader, int i, int i2) throws RemoteException;

    void getCursorCapsMode(InputConnectionCommandHeader inputConnectionCommandHeader, int i, AndroidFuture androidFuture) throws RemoteException;

    void getSurroundingText(InputConnectionCommandHeader inputConnectionCommandHeader, int i, int i2, int i3, AndroidFuture androidFuture) throws RemoteException;

    void performContextMenuAction(InputConnectionCommandHeader inputConnectionCommandHeader, int i) throws RemoteException;

    void performEditorAction(InputConnectionCommandHeader inputConnectionCommandHeader, int i) throws RemoteException;

    void sendKeyEvent(InputConnectionCommandHeader inputConnectionCommandHeader, KeyEvent keyEvent) throws RemoteException;

    void setSelection(InputConnectionCommandHeader inputConnectionCommandHeader, int i, int i2) throws RemoteException;

    /* loaded from: classes4.dex */
    public static class Default implements IRemoteAccessibilityInputConnection {
        @Override // com.android.internal.inputmethod.IRemoteAccessibilityInputConnection
        public void commitText(InputConnectionCommandHeader header, CharSequence text, int newCursorPosition, TextAttribute textAttribute) throws RemoteException {
        }

        @Override // com.android.internal.inputmethod.IRemoteAccessibilityInputConnection
        public void setSelection(InputConnectionCommandHeader header, int start, int end) throws RemoteException {
        }

        @Override // com.android.internal.inputmethod.IRemoteAccessibilityInputConnection
        public void getSurroundingText(InputConnectionCommandHeader header, int beforeLength, int afterLength, int flags, AndroidFuture future) throws RemoteException {
        }

        @Override // com.android.internal.inputmethod.IRemoteAccessibilityInputConnection
        public void deleteSurroundingText(InputConnectionCommandHeader header, int beforeLength, int afterLength) throws RemoteException {
        }

        @Override // com.android.internal.inputmethod.IRemoteAccessibilityInputConnection
        public void sendKeyEvent(InputConnectionCommandHeader header, KeyEvent event) throws RemoteException {
        }

        @Override // com.android.internal.inputmethod.IRemoteAccessibilityInputConnection
        public void performEditorAction(InputConnectionCommandHeader header, int actionCode) throws RemoteException {
        }

        @Override // com.android.internal.inputmethod.IRemoteAccessibilityInputConnection
        public void performContextMenuAction(InputConnectionCommandHeader header, int id) throws RemoteException {
        }

        @Override // com.android.internal.inputmethod.IRemoteAccessibilityInputConnection
        public void getCursorCapsMode(InputConnectionCommandHeader header, int reqModes, AndroidFuture future) throws RemoteException {
        }

        @Override // com.android.internal.inputmethod.IRemoteAccessibilityInputConnection
        public void clearMetaKeyStates(InputConnectionCommandHeader header, int states) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes4.dex */
    public static abstract class Stub extends Binder implements IRemoteAccessibilityInputConnection {
        static final int TRANSACTION_clearMetaKeyStates = 9;
        static final int TRANSACTION_commitText = 1;
        static final int TRANSACTION_deleteSurroundingText = 4;
        static final int TRANSACTION_getCursorCapsMode = 8;
        static final int TRANSACTION_getSurroundingText = 3;
        static final int TRANSACTION_performContextMenuAction = 7;
        static final int TRANSACTION_performEditorAction = 6;
        static final int TRANSACTION_sendKeyEvent = 5;
        static final int TRANSACTION_setSelection = 2;

        public Stub() {
            attachInterface(this, IRemoteAccessibilityInputConnection.DESCRIPTOR);
        }

        public static IRemoteAccessibilityInputConnection asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IRemoteAccessibilityInputConnection.DESCRIPTOR);
            if (iin != null && (iin instanceof IRemoteAccessibilityInputConnection)) {
                return (IRemoteAccessibilityInputConnection) iin;
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
                    return "commitText";
                case 2:
                    return "setSelection";
                case 3:
                    return "getSurroundingText";
                case 4:
                    return "deleteSurroundingText";
                case 5:
                    return "sendKeyEvent";
                case 6:
                    return "performEditorAction";
                case 7:
                    return "performContextMenuAction";
                case 8:
                    return "getCursorCapsMode";
                case 9:
                    return "clearMetaKeyStates";
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
                data.enforceInterface(IRemoteAccessibilityInputConnection.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IRemoteAccessibilityInputConnection.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            InputConnectionCommandHeader _arg0 = (InputConnectionCommandHeader) data.readTypedObject(InputConnectionCommandHeader.CREATOR);
                            CharSequence _arg1 = (CharSequence) data.readTypedObject(TextUtils.CHAR_SEQUENCE_CREATOR);
                            int _arg2 = data.readInt();
                            TextAttribute _arg3 = (TextAttribute) data.readTypedObject(TextAttribute.CREATOR);
                            data.enforceNoDataAvail();
                            commitText(_arg0, _arg1, _arg2, _arg3);
                            break;
                        case 2:
                            InputConnectionCommandHeader _arg02 = (InputConnectionCommandHeader) data.readTypedObject(InputConnectionCommandHeader.CREATOR);
                            int _arg12 = data.readInt();
                            int _arg22 = data.readInt();
                            data.enforceNoDataAvail();
                            setSelection(_arg02, _arg12, _arg22);
                            break;
                        case 3:
                            InputConnectionCommandHeader _arg03 = (InputConnectionCommandHeader) data.readTypedObject(InputConnectionCommandHeader.CREATOR);
                            int _arg13 = data.readInt();
                            int _arg23 = data.readInt();
                            int _arg32 = data.readInt();
                            AndroidFuture _arg4 = (AndroidFuture) data.readTypedObject(AndroidFuture.CREATOR);
                            data.enforceNoDataAvail();
                            getSurroundingText(_arg03, _arg13, _arg23, _arg32, _arg4);
                            break;
                        case 4:
                            InputConnectionCommandHeader _arg04 = (InputConnectionCommandHeader) data.readTypedObject(InputConnectionCommandHeader.CREATOR);
                            int _arg14 = data.readInt();
                            int _arg24 = data.readInt();
                            data.enforceNoDataAvail();
                            deleteSurroundingText(_arg04, _arg14, _arg24);
                            break;
                        case 5:
                            InputConnectionCommandHeader _arg05 = (InputConnectionCommandHeader) data.readTypedObject(InputConnectionCommandHeader.CREATOR);
                            KeyEvent _arg15 = (KeyEvent) data.readTypedObject(KeyEvent.CREATOR);
                            data.enforceNoDataAvail();
                            sendKeyEvent(_arg05, _arg15);
                            break;
                        case 6:
                            InputConnectionCommandHeader _arg06 = (InputConnectionCommandHeader) data.readTypedObject(InputConnectionCommandHeader.CREATOR);
                            int _arg16 = data.readInt();
                            data.enforceNoDataAvail();
                            performEditorAction(_arg06, _arg16);
                            break;
                        case 7:
                            InputConnectionCommandHeader _arg07 = (InputConnectionCommandHeader) data.readTypedObject(InputConnectionCommandHeader.CREATOR);
                            int _arg17 = data.readInt();
                            data.enforceNoDataAvail();
                            performContextMenuAction(_arg07, _arg17);
                            break;
                        case 8:
                            InputConnectionCommandHeader _arg08 = (InputConnectionCommandHeader) data.readTypedObject(InputConnectionCommandHeader.CREATOR);
                            int _arg18 = data.readInt();
                            AndroidFuture _arg25 = (AndroidFuture) data.readTypedObject(AndroidFuture.CREATOR);
                            data.enforceNoDataAvail();
                            getCursorCapsMode(_arg08, _arg18, _arg25);
                            break;
                        case 9:
                            InputConnectionCommandHeader _arg09 = (InputConnectionCommandHeader) data.readTypedObject(InputConnectionCommandHeader.CREATOR);
                            int _arg19 = data.readInt();
                            data.enforceNoDataAvail();
                            clearMetaKeyStates(_arg09, _arg19);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes4.dex */
        private static class Proxy implements IRemoteAccessibilityInputConnection {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IRemoteAccessibilityInputConnection.DESCRIPTOR;
            }

            @Override // com.android.internal.inputmethod.IRemoteAccessibilityInputConnection
            public void commitText(InputConnectionCommandHeader header, CharSequence text, int newCursorPosition, TextAttribute textAttribute) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IRemoteAccessibilityInputConnection.DESCRIPTOR);
                    _data.writeTypedObject(header, 0);
                    if (text != null) {
                        _data.writeInt(1);
                        TextUtils.writeToParcel(text, _data, 0);
                    } else {
                        _data.writeInt(0);
                    }
                    _data.writeInt(newCursorPosition);
                    _data.writeTypedObject(textAttribute, 0);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.inputmethod.IRemoteAccessibilityInputConnection
            public void setSelection(InputConnectionCommandHeader header, int start, int end) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IRemoteAccessibilityInputConnection.DESCRIPTOR);
                    _data.writeTypedObject(header, 0);
                    _data.writeInt(start);
                    _data.writeInt(end);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.inputmethod.IRemoteAccessibilityInputConnection
            public void getSurroundingText(InputConnectionCommandHeader header, int beforeLength, int afterLength, int flags, AndroidFuture future) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IRemoteAccessibilityInputConnection.DESCRIPTOR);
                    _data.writeTypedObject(header, 0);
                    _data.writeInt(beforeLength);
                    _data.writeInt(afterLength);
                    _data.writeInt(flags);
                    _data.writeTypedObject(future, 0);
                    this.mRemote.transact(3, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.inputmethod.IRemoteAccessibilityInputConnection
            public void deleteSurroundingText(InputConnectionCommandHeader header, int beforeLength, int afterLength) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IRemoteAccessibilityInputConnection.DESCRIPTOR);
                    _data.writeTypedObject(header, 0);
                    _data.writeInt(beforeLength);
                    _data.writeInt(afterLength);
                    this.mRemote.transact(4, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.inputmethod.IRemoteAccessibilityInputConnection
            public void sendKeyEvent(InputConnectionCommandHeader header, KeyEvent event) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IRemoteAccessibilityInputConnection.DESCRIPTOR);
                    _data.writeTypedObject(header, 0);
                    _data.writeTypedObject(event, 0);
                    this.mRemote.transact(5, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.inputmethod.IRemoteAccessibilityInputConnection
            public void performEditorAction(InputConnectionCommandHeader header, int actionCode) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IRemoteAccessibilityInputConnection.DESCRIPTOR);
                    _data.writeTypedObject(header, 0);
                    _data.writeInt(actionCode);
                    this.mRemote.transact(6, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.inputmethod.IRemoteAccessibilityInputConnection
            public void performContextMenuAction(InputConnectionCommandHeader header, int id) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IRemoteAccessibilityInputConnection.DESCRIPTOR);
                    _data.writeTypedObject(header, 0);
                    _data.writeInt(id);
                    this.mRemote.transact(7, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.inputmethod.IRemoteAccessibilityInputConnection
            public void getCursorCapsMode(InputConnectionCommandHeader header, int reqModes, AndroidFuture future) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IRemoteAccessibilityInputConnection.DESCRIPTOR);
                    _data.writeTypedObject(header, 0);
                    _data.writeInt(reqModes);
                    _data.writeTypedObject(future, 0);
                    this.mRemote.transact(8, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.inputmethod.IRemoteAccessibilityInputConnection
            public void clearMetaKeyStates(InputConnectionCommandHeader header, int states) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IRemoteAccessibilityInputConnection.DESCRIPTOR);
                    _data.writeTypedObject(header, 0);
                    _data.writeInt(states);
                    this.mRemote.transact(9, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 8;
        }
    }
}
