package android.net;

import android.net.INetdEventCallback;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes2.dex */
public interface IIpConnectivityMetrics extends IInterface {
    boolean addNetdEventCallback(int i, INetdEventCallback iNetdEventCallback) throws RemoteException;

    void logDefaultNetworkEvent(Network network, int i, boolean z, LinkProperties linkProperties, NetworkCapabilities networkCapabilities, Network network2, int i2, LinkProperties linkProperties2, NetworkCapabilities networkCapabilities2) throws RemoteException;

    void logDefaultNetworkValidity(boolean z) throws RemoteException;

    int logEvent(ConnectivityMetricsEvent connectivityMetricsEvent) throws RemoteException;

    boolean removeNetdEventCallback(int i) throws RemoteException;

    /* loaded from: classes2.dex */
    public static class Default implements IIpConnectivityMetrics {
        @Override // android.net.IIpConnectivityMetrics
        public int logEvent(ConnectivityMetricsEvent event) throws RemoteException {
            return 0;
        }

        @Override // android.net.IIpConnectivityMetrics
        public void logDefaultNetworkValidity(boolean valid) throws RemoteException {
        }

        @Override // android.net.IIpConnectivityMetrics
        public void logDefaultNetworkEvent(Network defaultNetwork, int score, boolean validated, LinkProperties lp, NetworkCapabilities nc, Network previousDefaultNetwork, int previousScore, LinkProperties previousLp, NetworkCapabilities previousNc) throws RemoteException {
        }

        @Override // android.net.IIpConnectivityMetrics
        public boolean addNetdEventCallback(int callerType, INetdEventCallback callback) throws RemoteException {
            return false;
        }

        @Override // android.net.IIpConnectivityMetrics
        public boolean removeNetdEventCallback(int callerType) throws RemoteException {
            return false;
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements IIpConnectivityMetrics {
        public static final String DESCRIPTOR = "android.net.IIpConnectivityMetrics";
        static final int TRANSACTION_addNetdEventCallback = 4;
        static final int TRANSACTION_logDefaultNetworkEvent = 3;
        static final int TRANSACTION_logDefaultNetworkValidity = 2;
        static final int TRANSACTION_logEvent = 1;
        static final int TRANSACTION_removeNetdEventCallback = 5;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IIpConnectivityMetrics asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IIpConnectivityMetrics)) {
                return (IIpConnectivityMetrics) iin;
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
                    return "logEvent";
                case 2:
                    return "logDefaultNetworkValidity";
                case 3:
                    return "logDefaultNetworkEvent";
                case 4:
                    return "addNetdEventCallback";
                case 5:
                    return "removeNetdEventCallback";
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
                            ConnectivityMetricsEvent _arg0 = (ConnectivityMetricsEvent) data.readTypedObject(ConnectivityMetricsEvent.CREATOR);
                            data.enforceNoDataAvail();
                            int _result = logEvent(_arg0);
                            reply.writeNoException();
                            reply.writeInt(_result);
                            break;
                        case 2:
                            boolean _arg02 = data.readBoolean();
                            data.enforceNoDataAvail();
                            logDefaultNetworkValidity(_arg02);
                            reply.writeNoException();
                            break;
                        case 3:
                            Network _arg03 = (Network) data.readTypedObject(Network.CREATOR);
                            int _arg1 = data.readInt();
                            boolean _arg2 = data.readBoolean();
                            LinkProperties _arg3 = (LinkProperties) data.readTypedObject(LinkProperties.CREATOR);
                            NetworkCapabilities _arg4 = (NetworkCapabilities) data.readTypedObject(NetworkCapabilities.CREATOR);
                            Network _arg5 = (Network) data.readTypedObject(Network.CREATOR);
                            int _arg6 = data.readInt();
                            LinkProperties _arg7 = (LinkProperties) data.readTypedObject(LinkProperties.CREATOR);
                            NetworkCapabilities _arg8 = (NetworkCapabilities) data.readTypedObject(NetworkCapabilities.CREATOR);
                            data.enforceNoDataAvail();
                            logDefaultNetworkEvent(_arg03, _arg1, _arg2, _arg3, _arg4, _arg5, _arg6, _arg7, _arg8);
                            reply.writeNoException();
                            break;
                        case 4:
                            int _arg04 = data.readInt();
                            INetdEventCallback _arg12 = INetdEventCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            boolean _result2 = addNetdEventCallback(_arg04, _arg12);
                            reply.writeNoException();
                            reply.writeBoolean(_result2);
                            break;
                        case 5:
                            int _arg05 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result3 = removeNetdEventCallback(_arg05);
                            reply.writeNoException();
                            reply.writeBoolean(_result3);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes2.dex */
        public static class Proxy implements IIpConnectivityMetrics {
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

            @Override // android.net.IIpConnectivityMetrics
            public int logEvent(ConnectivityMetricsEvent event) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(event, 0);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.net.IIpConnectivityMetrics
            public void logDefaultNetworkValidity(boolean valid) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeBoolean(valid);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.net.IIpConnectivityMetrics
            public void logDefaultNetworkEvent(Network defaultNetwork, int score, boolean validated, LinkProperties lp, NetworkCapabilities nc, Network previousDefaultNetwork, int previousScore, LinkProperties previousLp, NetworkCapabilities previousNc) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(defaultNetwork, 0);
                    _data.writeInt(score);
                    _data.writeBoolean(validated);
                    _data.writeTypedObject(lp, 0);
                    _data.writeTypedObject(nc, 0);
                    _data.writeTypedObject(previousDefaultNetwork, 0);
                    _data.writeInt(previousScore);
                    _data.writeTypedObject(previousLp, 0);
                    _data.writeTypedObject(previousNc, 0);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.net.IIpConnectivityMetrics
            public boolean addNetdEventCallback(int callerType, INetdEventCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(callerType);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.net.IIpConnectivityMetrics
            public boolean removeNetdEventCallback(int callerType) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(callerType);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 4;
        }
    }
}
