package android.hardware.biometrics;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes.dex */
public interface IBiometricContextListener extends IInterface {
    public static final String DESCRIPTOR = "android.hardware.biometrics.IBiometricContextListener";

    /* loaded from: classes.dex */
    public @interface FoldState {
        public static final int FULLY_CLOSED = 3;
        public static final int FULLY_OPENED = 2;
        public static final int HALF_OPENED = 1;
        public static final int UNKNOWN = 0;
    }

    void onDozeChanged(boolean z, boolean z2) throws RemoteException;

    void onFoldChanged(int i) throws RemoteException;

    /* loaded from: classes.dex */
    public static class Default implements IBiometricContextListener {
        @Override // android.hardware.biometrics.IBiometricContextListener
        public void onDozeChanged(boolean isDozing, boolean isAwake) throws RemoteException {
        }

        @Override // android.hardware.biometrics.IBiometricContextListener
        public void onFoldChanged(int FoldState) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements IBiometricContextListener {
        static final int TRANSACTION_onDozeChanged = 1;
        static final int TRANSACTION_onFoldChanged = 2;

        public Stub() {
            attachInterface(this, IBiometricContextListener.DESCRIPTOR);
        }

        public static IBiometricContextListener asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IBiometricContextListener.DESCRIPTOR);
            if (iin != null && (iin instanceof IBiometricContextListener)) {
                return (IBiometricContextListener) iin;
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
                    return "onDozeChanged";
                case 2:
                    return "onFoldChanged";
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
                data.enforceInterface(IBiometricContextListener.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IBiometricContextListener.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            boolean _arg0 = data.readBoolean();
                            boolean _arg1 = data.readBoolean();
                            data.enforceNoDataAvail();
                            onDozeChanged(_arg0, _arg1);
                            break;
                        case 2:
                            int _arg02 = data.readInt();
                            data.enforceNoDataAvail();
                            onFoldChanged(_arg02);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes.dex */
        private static class Proxy implements IBiometricContextListener {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IBiometricContextListener.DESCRIPTOR;
            }

            @Override // android.hardware.biometrics.IBiometricContextListener
            public void onDozeChanged(boolean isDozing, boolean isAwake) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IBiometricContextListener.DESCRIPTOR);
                    _data.writeBoolean(isDozing);
                    _data.writeBoolean(isAwake);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.biometrics.IBiometricContextListener
            public void onFoldChanged(int FoldState) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IBiometricContextListener.DESCRIPTOR);
                    _data.writeInt(FoldState);
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
