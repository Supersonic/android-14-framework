package android.net;

import android.net.IPacProxyInstalledListener;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes2.dex */
public interface IPacProxyManager extends IInterface {
    public static final String DESCRIPTOR = "android.net.IPacProxyManager";

    void addListener(IPacProxyInstalledListener iPacProxyInstalledListener) throws RemoteException;

    void removeListener(IPacProxyInstalledListener iPacProxyInstalledListener) throws RemoteException;

    void setCurrentProxyScriptUrl(ProxyInfo proxyInfo) throws RemoteException;

    /* loaded from: classes2.dex */
    public static class Default implements IPacProxyManager {
        @Override // android.net.IPacProxyManager
        public void addListener(IPacProxyInstalledListener listener) throws RemoteException {
        }

        @Override // android.net.IPacProxyManager
        public void removeListener(IPacProxyInstalledListener listener) throws RemoteException {
        }

        @Override // android.net.IPacProxyManager
        public void setCurrentProxyScriptUrl(ProxyInfo proxyInfo) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements IPacProxyManager {
        static final int TRANSACTION_addListener = 1;
        static final int TRANSACTION_removeListener = 2;
        static final int TRANSACTION_setCurrentProxyScriptUrl = 3;

        public Stub() {
            attachInterface(this, IPacProxyManager.DESCRIPTOR);
        }

        public static IPacProxyManager asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IPacProxyManager.DESCRIPTOR);
            if (iin != null && (iin instanceof IPacProxyManager)) {
                return (IPacProxyManager) iin;
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
                    return "addListener";
                case 2:
                    return "removeListener";
                case 3:
                    return "setCurrentProxyScriptUrl";
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
                data.enforceInterface(IPacProxyManager.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IPacProxyManager.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            IPacProxyInstalledListener _arg0 = IPacProxyInstalledListener.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            addListener(_arg0);
                            reply.writeNoException();
                            break;
                        case 2:
                            IPacProxyInstalledListener _arg02 = IPacProxyInstalledListener.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            removeListener(_arg02);
                            reply.writeNoException();
                            break;
                        case 3:
                            ProxyInfo _arg03 = (ProxyInfo) data.readTypedObject(ProxyInfo.CREATOR);
                            data.enforceNoDataAvail();
                            setCurrentProxyScriptUrl(_arg03);
                            reply.writeNoException();
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes2.dex */
        private static class Proxy implements IPacProxyManager {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IPacProxyManager.DESCRIPTOR;
            }

            @Override // android.net.IPacProxyManager
            public void addListener(IPacProxyInstalledListener listener) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IPacProxyManager.DESCRIPTOR);
                    _data.writeStrongInterface(listener);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.net.IPacProxyManager
            public void removeListener(IPacProxyInstalledListener listener) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IPacProxyManager.DESCRIPTOR);
                    _data.writeStrongInterface(listener);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.net.IPacProxyManager
            public void setCurrentProxyScriptUrl(ProxyInfo proxyInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IPacProxyManager.DESCRIPTOR);
                    _data.writeTypedObject(proxyInfo, 0);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
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
