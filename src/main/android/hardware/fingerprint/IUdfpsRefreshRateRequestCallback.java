package android.hardware.fingerprint;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes.dex */
public interface IUdfpsRefreshRateRequestCallback extends IInterface {
    public static final String DESCRIPTOR = "android.hardware.fingerprint.IUdfpsRefreshRateRequestCallback";

    void onAuthenticationPossible(int i, boolean z) throws RemoteException;

    void onRequestDisabled(int i) throws RemoteException;

    void onRequestEnabled(int i) throws RemoteException;

    /* loaded from: classes.dex */
    public static class Default implements IUdfpsRefreshRateRequestCallback {
        @Override // android.hardware.fingerprint.IUdfpsRefreshRateRequestCallback
        public void onRequestEnabled(int displayId) throws RemoteException {
        }

        @Override // android.hardware.fingerprint.IUdfpsRefreshRateRequestCallback
        public void onRequestDisabled(int displayId) throws RemoteException {
        }

        @Override // android.hardware.fingerprint.IUdfpsRefreshRateRequestCallback
        public void onAuthenticationPossible(int displayId, boolean isPossible) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements IUdfpsRefreshRateRequestCallback {
        static final int TRANSACTION_onAuthenticationPossible = 3;
        static final int TRANSACTION_onRequestDisabled = 2;
        static final int TRANSACTION_onRequestEnabled = 1;

        public Stub() {
            attachInterface(this, IUdfpsRefreshRateRequestCallback.DESCRIPTOR);
        }

        public static IUdfpsRefreshRateRequestCallback asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IUdfpsRefreshRateRequestCallback.DESCRIPTOR);
            if (iin != null && (iin instanceof IUdfpsRefreshRateRequestCallback)) {
                return (IUdfpsRefreshRateRequestCallback) iin;
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
                    return "onRequestEnabled";
                case 2:
                    return "onRequestDisabled";
                case 3:
                    return "onAuthenticationPossible";
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
                data.enforceInterface(IUdfpsRefreshRateRequestCallback.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IUdfpsRefreshRateRequestCallback.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            int _arg0 = data.readInt();
                            data.enforceNoDataAvail();
                            onRequestEnabled(_arg0);
                            break;
                        case 2:
                            int _arg02 = data.readInt();
                            data.enforceNoDataAvail();
                            onRequestDisabled(_arg02);
                            break;
                        case 3:
                            int _arg03 = data.readInt();
                            boolean _arg1 = data.readBoolean();
                            data.enforceNoDataAvail();
                            onAuthenticationPossible(_arg03, _arg1);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes.dex */
        private static class Proxy implements IUdfpsRefreshRateRequestCallback {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IUdfpsRefreshRateRequestCallback.DESCRIPTOR;
            }

            @Override // android.hardware.fingerprint.IUdfpsRefreshRateRequestCallback
            public void onRequestEnabled(int displayId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IUdfpsRefreshRateRequestCallback.DESCRIPTOR);
                    _data.writeInt(displayId);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.fingerprint.IUdfpsRefreshRateRequestCallback
            public void onRequestDisabled(int displayId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IUdfpsRefreshRateRequestCallback.DESCRIPTOR);
                    _data.writeInt(displayId);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.fingerprint.IUdfpsRefreshRateRequestCallback
            public void onAuthenticationPossible(int displayId, boolean isPossible) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IUdfpsRefreshRateRequestCallback.DESCRIPTOR);
                    _data.writeInt(displayId);
                    _data.writeBoolean(isPossible);
                    this.mRemote.transact(3, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 2;
        }
    }
}
