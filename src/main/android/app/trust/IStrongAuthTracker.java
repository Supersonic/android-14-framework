package android.app.trust;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes.dex */
public interface IStrongAuthTracker extends IInterface {
    void onIsNonStrongBiometricAllowedChanged(boolean z, int i) throws RemoteException;

    void onStrongAuthRequiredChanged(int i, int i2) throws RemoteException;

    /* loaded from: classes.dex */
    public static class Default implements IStrongAuthTracker {
        @Override // android.app.trust.IStrongAuthTracker
        public void onStrongAuthRequiredChanged(int strongAuthRequired, int userId) throws RemoteException {
        }

        @Override // android.app.trust.IStrongAuthTracker
        public void onIsNonStrongBiometricAllowedChanged(boolean allowed, int userId) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements IStrongAuthTracker {
        public static final String DESCRIPTOR = "android.app.trust.IStrongAuthTracker";
        static final int TRANSACTION_onIsNonStrongBiometricAllowedChanged = 2;
        static final int TRANSACTION_onStrongAuthRequiredChanged = 1;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IStrongAuthTracker asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IStrongAuthTracker)) {
                return (IStrongAuthTracker) iin;
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
                    return "onStrongAuthRequiredChanged";
                case 2:
                    return "onIsNonStrongBiometricAllowedChanged";
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
                            int _arg0 = data.readInt();
                            int _arg1 = data.readInt();
                            data.enforceNoDataAvail();
                            onStrongAuthRequiredChanged(_arg0, _arg1);
                            break;
                        case 2:
                            boolean _arg02 = data.readBoolean();
                            int _arg12 = data.readInt();
                            data.enforceNoDataAvail();
                            onIsNonStrongBiometricAllowedChanged(_arg02, _arg12);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes.dex */
        private static class Proxy implements IStrongAuthTracker {
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

            @Override // android.app.trust.IStrongAuthTracker
            public void onStrongAuthRequiredChanged(int strongAuthRequired, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(strongAuthRequired);
                    _data.writeInt(userId);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.app.trust.IStrongAuthTracker
            public void onIsNonStrongBiometricAllowedChanged(boolean allowed, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeBoolean(allowed);
                    _data.writeInt(userId);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
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
