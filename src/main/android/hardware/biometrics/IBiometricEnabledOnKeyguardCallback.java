package android.hardware.biometrics;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes.dex */
public interface IBiometricEnabledOnKeyguardCallback extends IInterface {
    public static final String DESCRIPTOR = "android.hardware.biometrics.IBiometricEnabledOnKeyguardCallback";

    void onChanged(boolean z, int i) throws RemoteException;

    /* loaded from: classes.dex */
    public static class Default implements IBiometricEnabledOnKeyguardCallback {
        @Override // android.hardware.biometrics.IBiometricEnabledOnKeyguardCallback
        public void onChanged(boolean enabled, int userId) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements IBiometricEnabledOnKeyguardCallback {
        static final int TRANSACTION_onChanged = 1;

        public Stub() {
            attachInterface(this, IBiometricEnabledOnKeyguardCallback.DESCRIPTOR);
        }

        public static IBiometricEnabledOnKeyguardCallback asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IBiometricEnabledOnKeyguardCallback.DESCRIPTOR);
            if (iin != null && (iin instanceof IBiometricEnabledOnKeyguardCallback)) {
                return (IBiometricEnabledOnKeyguardCallback) iin;
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
                    return "onChanged";
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
                data.enforceInterface(IBiometricEnabledOnKeyguardCallback.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IBiometricEnabledOnKeyguardCallback.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            boolean _arg0 = data.readBoolean();
                            int _arg1 = data.readInt();
                            data.enforceNoDataAvail();
                            onChanged(_arg0, _arg1);
                            return true;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
            }
        }

        /* loaded from: classes.dex */
        private static class Proxy implements IBiometricEnabledOnKeyguardCallback {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IBiometricEnabledOnKeyguardCallback.DESCRIPTOR;
            }

            @Override // android.hardware.biometrics.IBiometricEnabledOnKeyguardCallback
            public void onChanged(boolean enabled, int userId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IBiometricEnabledOnKeyguardCallback.DESCRIPTOR);
                    _data.writeBoolean(enabled);
                    _data.writeInt(userId);
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
