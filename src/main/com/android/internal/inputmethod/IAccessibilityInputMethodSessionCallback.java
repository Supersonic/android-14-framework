package com.android.internal.inputmethod;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import com.android.internal.inputmethod.IAccessibilityInputMethodSession;
/* loaded from: classes4.dex */
public interface IAccessibilityInputMethodSessionCallback extends IInterface {
    public static final String DESCRIPTOR = "com.android.internal.inputmethod.IAccessibilityInputMethodSessionCallback";

    void sessionCreated(IAccessibilityInputMethodSession iAccessibilityInputMethodSession, int i) throws RemoteException;

    /* loaded from: classes4.dex */
    public static class Default implements IAccessibilityInputMethodSessionCallback {
        @Override // com.android.internal.inputmethod.IAccessibilityInputMethodSessionCallback
        public void sessionCreated(IAccessibilityInputMethodSession session, int id) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes4.dex */
    public static abstract class Stub extends Binder implements IAccessibilityInputMethodSessionCallback {
        static final int TRANSACTION_sessionCreated = 1;

        public Stub() {
            attachInterface(this, IAccessibilityInputMethodSessionCallback.DESCRIPTOR);
        }

        public static IAccessibilityInputMethodSessionCallback asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IAccessibilityInputMethodSessionCallback.DESCRIPTOR);
            if (iin != null && (iin instanceof IAccessibilityInputMethodSessionCallback)) {
                return (IAccessibilityInputMethodSessionCallback) iin;
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
                    return "sessionCreated";
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
                data.enforceInterface(IAccessibilityInputMethodSessionCallback.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IAccessibilityInputMethodSessionCallback.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            IAccessibilityInputMethodSession _arg0 = IAccessibilityInputMethodSession.Stub.asInterface(data.readStrongBinder());
                            int _arg1 = data.readInt();
                            data.enforceNoDataAvail();
                            sessionCreated(_arg0, _arg1);
                            return true;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
            }
        }

        /* loaded from: classes4.dex */
        private static class Proxy implements IAccessibilityInputMethodSessionCallback {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IAccessibilityInputMethodSessionCallback.DESCRIPTOR;
            }

            @Override // com.android.internal.inputmethod.IAccessibilityInputMethodSessionCallback
            public void sessionCreated(IAccessibilityInputMethodSession session, int id) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IAccessibilityInputMethodSessionCallback.DESCRIPTOR);
                    _data.writeStrongInterface(session);
                    _data.writeInt(id);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 0;
        }
    }
}
