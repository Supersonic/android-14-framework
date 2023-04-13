package android.net.wifi.nl80211;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes2.dex */
public interface IWificondEventCallback extends IInterface {
    public static final String DESCRIPTOR = "android.net.wifi.nl80211.IWificondEventCallback";

    void OnRegDomainChanged(String str) throws RemoteException;

    /* loaded from: classes2.dex */
    public static class Default implements IWificondEventCallback {
        @Override // android.net.wifi.nl80211.IWificondEventCallback
        public void OnRegDomainChanged(String countryCode) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements IWificondEventCallback {
        static final int TRANSACTION_OnRegDomainChanged = 1;

        public Stub() {
            attachInterface(this, IWificondEventCallback.DESCRIPTOR);
        }

        public static IWificondEventCallback asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IWificondEventCallback.DESCRIPTOR);
            if (iin != null && (iin instanceof IWificondEventCallback)) {
                return (IWificondEventCallback) iin;
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
                    return "OnRegDomainChanged";
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
                data.enforceInterface(IWificondEventCallback.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IWificondEventCallback.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            String _arg0 = data.readString();
                            data.enforceNoDataAvail();
                            OnRegDomainChanged(_arg0);
                            return true;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
            }
        }

        /* loaded from: classes2.dex */
        private static class Proxy implements IWificondEventCallback {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IWificondEventCallback.DESCRIPTOR;
            }

            @Override // android.net.wifi.nl80211.IWificondEventCallback
            public void OnRegDomainChanged(String countryCode) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IWificondEventCallback.DESCRIPTOR);
                    _data.writeString(countryCode);
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
