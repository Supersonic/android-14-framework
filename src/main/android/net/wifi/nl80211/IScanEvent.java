package android.net.wifi.nl80211;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes2.dex */
public interface IScanEvent extends IInterface {
    public static final String DESCRIPTOR = "android.net.wifi.nl80211.IScanEvent";

    void OnScanFailed() throws RemoteException;

    void OnScanRequestFailed(int i) throws RemoteException;

    void OnScanResultReady() throws RemoteException;

    /* loaded from: classes2.dex */
    public static class Default implements IScanEvent {
        @Override // android.net.wifi.nl80211.IScanEvent
        public void OnScanResultReady() throws RemoteException {
        }

        @Override // android.net.wifi.nl80211.IScanEvent
        public void OnScanFailed() throws RemoteException {
        }

        @Override // android.net.wifi.nl80211.IScanEvent
        public void OnScanRequestFailed(int errorCode) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements IScanEvent {
        static final int TRANSACTION_OnScanFailed = 2;
        static final int TRANSACTION_OnScanRequestFailed = 3;
        static final int TRANSACTION_OnScanResultReady = 1;

        public Stub() {
            attachInterface(this, IScanEvent.DESCRIPTOR);
        }

        public static IScanEvent asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IScanEvent.DESCRIPTOR);
            if (iin != null && (iin instanceof IScanEvent)) {
                return (IScanEvent) iin;
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
                    return "OnScanResultReady";
                case 2:
                    return "OnScanFailed";
                case 3:
                    return "OnScanRequestFailed";
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
                data.enforceInterface(IScanEvent.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IScanEvent.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            OnScanResultReady();
                            break;
                        case 2:
                            OnScanFailed();
                            break;
                        case 3:
                            int _arg0 = data.readInt();
                            data.enforceNoDataAvail();
                            OnScanRequestFailed(_arg0);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes2.dex */
        private static class Proxy implements IScanEvent {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IScanEvent.DESCRIPTOR;
            }

            @Override // android.net.wifi.nl80211.IScanEvent
            public void OnScanResultReady() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IScanEvent.DESCRIPTOR);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.net.wifi.nl80211.IScanEvent
            public void OnScanFailed() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IScanEvent.DESCRIPTOR);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.net.wifi.nl80211.IScanEvent
            public void OnScanRequestFailed(int errorCode) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IScanEvent.DESCRIPTOR);
                    _data.writeInt(errorCode);
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
