package com.android.internal.policy;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes4.dex */
public interface IKeyguardStateCallback extends IInterface {
    void onInputRestrictedStateChanged(boolean z) throws RemoteException;

    void onShowingStateChanged(boolean z, int i) throws RemoteException;

    void onSimSecureStateChanged(boolean z) throws RemoteException;

    void onTrustedChanged(boolean z) throws RemoteException;

    /* loaded from: classes4.dex */
    public static class Default implements IKeyguardStateCallback {
        @Override // com.android.internal.policy.IKeyguardStateCallback
        public void onShowingStateChanged(boolean showing, int userId) throws RemoteException {
        }

        @Override // com.android.internal.policy.IKeyguardStateCallback
        public void onSimSecureStateChanged(boolean simSecure) throws RemoteException {
        }

        @Override // com.android.internal.policy.IKeyguardStateCallback
        public void onInputRestrictedStateChanged(boolean inputRestricted) throws RemoteException {
        }

        @Override // com.android.internal.policy.IKeyguardStateCallback
        public void onTrustedChanged(boolean trusted) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes4.dex */
    public static abstract class Stub extends Binder implements IKeyguardStateCallback {
        public static final String DESCRIPTOR = "com.android.internal.policy.IKeyguardStateCallback";
        static final int TRANSACTION_onInputRestrictedStateChanged = 3;
        static final int TRANSACTION_onShowingStateChanged = 1;
        static final int TRANSACTION_onSimSecureStateChanged = 2;
        static final int TRANSACTION_onTrustedChanged = 4;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IKeyguardStateCallback asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IKeyguardStateCallback)) {
                return (IKeyguardStateCallback) iin;
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
                    return "onShowingStateChanged";
                case 2:
                    return "onSimSecureStateChanged";
                case 3:
                    return "onInputRestrictedStateChanged";
                case 4:
                    return "onTrustedChanged";
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
                data.enforceInterface(DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            boolean _arg0 = data.readBoolean();
                            int _arg1 = data.readInt();
                            data.enforceNoDataAvail();
                            onShowingStateChanged(_arg0, _arg1);
                            reply.writeNoException();
                            break;
                        case 2:
                            boolean _arg02 = data.readBoolean();
                            data.enforceNoDataAvail();
                            onSimSecureStateChanged(_arg02);
                            reply.writeNoException();
                            break;
                        case 3:
                            boolean _arg03 = data.readBoolean();
                            data.enforceNoDataAvail();
                            onInputRestrictedStateChanged(_arg03);
                            reply.writeNoException();
                            break;
                        case 4:
                            boolean _arg04 = data.readBoolean();
                            data.enforceNoDataAvail();
                            onTrustedChanged(_arg04);
                            reply.writeNoException();
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes4.dex */
        private static class Proxy implements IKeyguardStateCallback {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return Stub.DESCRIPTOR;
            }

            @Override // com.android.internal.policy.IKeyguardStateCallback
            public void onShowingStateChanged(boolean showing, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeBoolean(showing);
                    _data.writeInt(userId);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.policy.IKeyguardStateCallback
            public void onSimSecureStateChanged(boolean simSecure) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeBoolean(simSecure);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.policy.IKeyguardStateCallback
            public void onInputRestrictedStateChanged(boolean inputRestricted) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeBoolean(inputRestricted);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // com.android.internal.policy.IKeyguardStateCallback
            public void onTrustedChanged(boolean trusted) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeBoolean(trusted);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
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
