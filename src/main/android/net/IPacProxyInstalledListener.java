package android.net;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes2.dex */
public interface IPacProxyInstalledListener extends IInterface {
    public static final String DESCRIPTOR = "android.net.IPacProxyInstalledListener";

    void onPacProxyInstalled(Network network, ProxyInfo proxyInfo) throws RemoteException;

    /* loaded from: classes2.dex */
    public static class Default implements IPacProxyInstalledListener {
        @Override // android.net.IPacProxyInstalledListener
        public void onPacProxyInstalled(Network network, ProxyInfo proxy) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements IPacProxyInstalledListener {
        static final int TRANSACTION_onPacProxyInstalled = 1;

        public Stub() {
            attachInterface(this, IPacProxyInstalledListener.DESCRIPTOR);
        }

        public static IPacProxyInstalledListener asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IPacProxyInstalledListener.DESCRIPTOR);
            if (iin != null && (iin instanceof IPacProxyInstalledListener)) {
                return (IPacProxyInstalledListener) iin;
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
                    return "onPacProxyInstalled";
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
                data.enforceInterface(IPacProxyInstalledListener.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IPacProxyInstalledListener.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            Network _arg0 = (Network) data.readTypedObject(Network.CREATOR);
                            ProxyInfo _arg1 = (ProxyInfo) data.readTypedObject(ProxyInfo.CREATOR);
                            data.enforceNoDataAvail();
                            onPacProxyInstalled(_arg0, _arg1);
                            return true;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
            }
        }

        /* loaded from: classes2.dex */
        private static class Proxy implements IPacProxyInstalledListener {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IPacProxyInstalledListener.DESCRIPTOR;
            }

            @Override // android.net.IPacProxyInstalledListener
            public void onPacProxyInstalled(Network network, ProxyInfo proxy) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IPacProxyInstalledListener.DESCRIPTOR);
                    _data.writeTypedObject(network, 0);
                    _data.writeTypedObject(proxy, 0);
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
