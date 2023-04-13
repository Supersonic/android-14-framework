package com.android.internal.inputmethod;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import android.view.inputmethod.EditorInfo;
import com.android.internal.inputmethod.IRemoteAccessibilityInputConnection;
/* loaded from: classes4.dex */
public interface IAccessibilityInputMethodSession extends IInterface {
    public static final String DESCRIPTOR = "com.android.internal.inputmethod.IAccessibilityInputMethodSession";

    void finishInput() throws RemoteException;

    void finishSession() throws RemoteException;

    void invalidateInput(EditorInfo editorInfo, IRemoteAccessibilityInputConnection iRemoteAccessibilityInputConnection, int i) throws RemoteException;

    void updateSelection(int i, int i2, int i3, int i4, int i5, int i6) throws RemoteException;

    /* loaded from: classes4.dex */
    public static class Default implements IAccessibilityInputMethodSession {
        @Override // com.android.internal.inputmethod.IAccessibilityInputMethodSession
        public void updateSelection(int oldSelStart, int oldSelEnd, int newSelStart, int newSelEnd, int candidatesStart, int candidatesEnd) throws RemoteException {
        }

        @Override // com.android.internal.inputmethod.IAccessibilityInputMethodSession
        public void finishInput() throws RemoteException {
        }

        @Override // com.android.internal.inputmethod.IAccessibilityInputMethodSession
        public void finishSession() throws RemoteException {
        }

        @Override // com.android.internal.inputmethod.IAccessibilityInputMethodSession
        public void invalidateInput(EditorInfo editorInfo, IRemoteAccessibilityInputConnection connection, int sessionId) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes4.dex */
    public static abstract class Stub extends Binder implements IAccessibilityInputMethodSession {
        static final int TRANSACTION_finishInput = 2;
        static final int TRANSACTION_finishSession = 3;
        static final int TRANSACTION_invalidateInput = 4;
        static final int TRANSACTION_updateSelection = 1;

        public Stub() {
            attachInterface(this, IAccessibilityInputMethodSession.DESCRIPTOR);
        }

        public static IAccessibilityInputMethodSession asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IAccessibilityInputMethodSession.DESCRIPTOR);
            if (iin != null && (iin instanceof IAccessibilityInputMethodSession)) {
                return (IAccessibilityInputMethodSession) iin;
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
                    return "updateSelection";
                case 2:
                    return "finishInput";
                case 3:
                    return "finishSession";
                case 4:
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
                data.enforceInterface(IAccessibilityInputMethodSession.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IAccessibilityInputMethodSession.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            int _arg0 = data.readInt();
                            int _arg1 = data.readInt();
                            int _arg2 = data.readInt();
                            int _arg3 = data.readInt();
                            int _arg4 = data.readInt();
                            int _arg5 = data.readInt();
                            data.enforceNoDataAvail();
                            updateSelection(_arg0, _arg1, _arg2, _arg3, _arg4, _arg5);
                            break;
                        case 2:
                            finishInput();
                            break;
                        case 3:
                            finishSession();
                            break;
                        case 4:
                            EditorInfo _arg02 = (EditorInfo) data.readTypedObject(EditorInfo.CREATOR);
                            IRemoteAccessibilityInputConnection _arg12 = IRemoteAccessibilityInputConnection.Stub.asInterface(data.readStrongBinder());
                            int _arg22 = data.readInt();
                            data.enforceNoDataAvail();
                            invalidateInput(_arg02, _arg12, _arg22);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes4.dex */
        public static class Proxy implements IAccessibilityInputMethodSession {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IAccessibilityInputMethodSession.DESCRIPTOR;
            }

            @Override // com.android.internal.inputmethod.IAccessibilityInputMethodSession
            public void updateSelection(int oldSelStart, int oldSelEnd, int newSelStart, int newSelEnd, int candidatesStart, int candidatesEnd) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IAccessibilityInputMethodSession.DESCRIPTOR);
                    _data.writeInt(oldSelStart);
                    _data.writeInt(oldSelEnd);
                    _data.writeInt(newSelStart);
                    _data.writeInt(newSelEnd);
                    _data.writeInt(candidatesStart);
                    _data.writeInt(candidatesEnd);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.inputmethod.IAccessibilityInputMethodSession
            public void finishInput() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IAccessibilityInputMethodSession.DESCRIPTOR);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.inputmethod.IAccessibilityInputMethodSession
            public void finishSession() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IAccessibilityInputMethodSession.DESCRIPTOR);
                    this.mRemote.transact(3, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.inputmethod.IAccessibilityInputMethodSession
            public void invalidateInput(EditorInfo editorInfo, IRemoteAccessibilityInputConnection connection, int sessionId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IAccessibilityInputMethodSession.DESCRIPTOR);
                    _data.writeTypedObject(editorInfo, 0);
                    _data.writeStrongInterface(connection);
                    _data.writeInt(sessionId);
                    this.mRemote.transact(4, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 3;
        }
    }
}
