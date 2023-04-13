package com.android.internal.inputmethod;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes4.dex */
public interface IInputContentUriToken extends IInterface {
    public static final String DESCRIPTOR = "com.android.internal.inputmethod.IInputContentUriToken";

    void release() throws RemoteException;

    void take() throws RemoteException;

    /* loaded from: classes4.dex */
    public static class Default implements IInputContentUriToken {
        @Override // com.android.internal.inputmethod.IInputContentUriToken
        public void take() throws RemoteException {
        }

        @Override // com.android.internal.inputmethod.IInputContentUriToken
        public void release() throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes4.dex */
    public static abstract class Stub extends Binder implements IInputContentUriToken {
        static final int TRANSACTION_release = 2;
        static final int TRANSACTION_take = 1;

        public Stub() {
            attachInterface(this, IInputContentUriToken.DESCRIPTOR);
        }

        public static IInputContentUriToken asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IInputContentUriToken.DESCRIPTOR);
            if (iin != null && (iin instanceof IInputContentUriToken)) {
                return (IInputContentUriToken) iin;
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
                    return "take";
                case 2:
                    return "release";
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
                data.enforceInterface(IInputContentUriToken.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IInputContentUriToken.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            take();
                            reply.writeNoException();
                            break;
                        case 2:
                            release();
                            reply.writeNoException();
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes4.dex */
        private static class Proxy implements IInputContentUriToken {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IInputContentUriToken.DESCRIPTOR;
            }

            @Override // com.android.internal.inputmethod.IInputContentUriToken
            public void take() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IInputContentUriToken.DESCRIPTOR);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.inputmethod.IInputContentUriToken
            public void release() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IInputContentUriToken.DESCRIPTOR);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 1;
        }
    }
}
