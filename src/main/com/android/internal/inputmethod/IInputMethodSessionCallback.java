package com.android.internal.inputmethod;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import com.android.internal.inputmethod.IInputMethodSession;
/* loaded from: classes4.dex */
public interface IInputMethodSessionCallback extends IInterface {
    public static final String DESCRIPTOR = "com.android.internal.inputmethod.IInputMethodSessionCallback";

    void sessionCreated(IInputMethodSession iInputMethodSession) throws RemoteException;

    /* loaded from: classes4.dex */
    public static class Default implements IInputMethodSessionCallback {
        @Override // com.android.internal.inputmethod.IInputMethodSessionCallback
        public void sessionCreated(IInputMethodSession session) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes4.dex */
    public static abstract class Stub extends Binder implements IInputMethodSessionCallback {
        static final int TRANSACTION_sessionCreated = 1;

        public Stub() {
            attachInterface(this, IInputMethodSessionCallback.DESCRIPTOR);
        }

        public static IInputMethodSessionCallback asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IInputMethodSessionCallback.DESCRIPTOR);
            if (iin != null && (iin instanceof IInputMethodSessionCallback)) {
                return (IInputMethodSessionCallback) iin;
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
                data.enforceInterface(IInputMethodSessionCallback.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IInputMethodSessionCallback.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            IInputMethodSession _arg0 = IInputMethodSession.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            sessionCreated(_arg0);
                            return true;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
            }
        }

        /* loaded from: classes4.dex */
        private static class Proxy implements IInputMethodSessionCallback {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IInputMethodSessionCallback.DESCRIPTOR;
            }

            @Override // com.android.internal.inputmethod.IInputMethodSessionCallback
            public void sessionCreated(IInputMethodSession session) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IInputMethodSessionCallback.DESCRIPTOR);
                    _data.writeStrongInterface(session);
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
