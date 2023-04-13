package android.net.wifi.sharedconnectivity.service;

import android.net.wifi.sharedconnectivity.app.HotspotNetwork;
import android.net.wifi.sharedconnectivity.app.HotspotNetworkConnectionStatus;
import android.net.wifi.sharedconnectivity.app.KnownNetwork;
import android.net.wifi.sharedconnectivity.app.KnownNetworkConnectionStatus;
import android.net.wifi.sharedconnectivity.app.SharedConnectivitySettingsState;
import android.net.wifi.sharedconnectivity.service.ISharedConnectivityCallback;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import java.util.List;
/* loaded from: classes2.dex */
public interface ISharedConnectivityService extends IInterface {
    public static final String DESCRIPTOR = "android.net.wifi.sharedconnectivity.service.ISharedConnectivityService";

    void connectHotspotNetwork(HotspotNetwork hotspotNetwork) throws RemoteException;

    void connectKnownNetwork(KnownNetwork knownNetwork) throws RemoteException;

    void disconnectHotspotNetwork(HotspotNetwork hotspotNetwork) throws RemoteException;

    void forgetKnownNetwork(KnownNetwork knownNetwork) throws RemoteException;

    HotspotNetworkConnectionStatus getHotspotNetworkConnectionStatus() throws RemoteException;

    List<HotspotNetwork> getHotspotNetworks() throws RemoteException;

    KnownNetworkConnectionStatus getKnownNetworkConnectionStatus() throws RemoteException;

    List<KnownNetwork> getKnownNetworks() throws RemoteException;

    SharedConnectivitySettingsState getSettingsState() throws RemoteException;

    void registerCallback(ISharedConnectivityCallback iSharedConnectivityCallback) throws RemoteException;

    void unregisterCallback(ISharedConnectivityCallback iSharedConnectivityCallback) throws RemoteException;

    /* loaded from: classes2.dex */
    public static class Default implements ISharedConnectivityService {
        @Override // android.net.wifi.sharedconnectivity.service.ISharedConnectivityService
        public void registerCallback(ISharedConnectivityCallback callback) throws RemoteException {
        }

        @Override // android.net.wifi.sharedconnectivity.service.ISharedConnectivityService
        public void unregisterCallback(ISharedConnectivityCallback callback) throws RemoteException {
        }

        @Override // android.net.wifi.sharedconnectivity.service.ISharedConnectivityService
        public void connectHotspotNetwork(HotspotNetwork network) throws RemoteException {
        }

        @Override // android.net.wifi.sharedconnectivity.service.ISharedConnectivityService
        public void disconnectHotspotNetwork(HotspotNetwork network) throws RemoteException {
        }

        @Override // android.net.wifi.sharedconnectivity.service.ISharedConnectivityService
        public void connectKnownNetwork(KnownNetwork network) throws RemoteException {
        }

        @Override // android.net.wifi.sharedconnectivity.service.ISharedConnectivityService
        public void forgetKnownNetwork(KnownNetwork network) throws RemoteException {
        }

        @Override // android.net.wifi.sharedconnectivity.service.ISharedConnectivityService
        public List<HotspotNetwork> getHotspotNetworks() throws RemoteException {
            return null;
        }

        @Override // android.net.wifi.sharedconnectivity.service.ISharedConnectivityService
        public List<KnownNetwork> getKnownNetworks() throws RemoteException {
            return null;
        }

        @Override // android.net.wifi.sharedconnectivity.service.ISharedConnectivityService
        public SharedConnectivitySettingsState getSettingsState() throws RemoteException {
            return null;
        }

        @Override // android.net.wifi.sharedconnectivity.service.ISharedConnectivityService
        public HotspotNetworkConnectionStatus getHotspotNetworkConnectionStatus() throws RemoteException {
            return null;
        }

        @Override // android.net.wifi.sharedconnectivity.service.ISharedConnectivityService
        public KnownNetworkConnectionStatus getKnownNetworkConnectionStatus() throws RemoteException {
            return null;
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements ISharedConnectivityService {
        static final int TRANSACTION_connectHotspotNetwork = 3;
        static final int TRANSACTION_connectKnownNetwork = 5;
        static final int TRANSACTION_disconnectHotspotNetwork = 4;
        static final int TRANSACTION_forgetKnownNetwork = 6;
        static final int TRANSACTION_getHotspotNetworkConnectionStatus = 10;
        static final int TRANSACTION_getHotspotNetworks = 7;
        static final int TRANSACTION_getKnownNetworkConnectionStatus = 11;
        static final int TRANSACTION_getKnownNetworks = 8;
        static final int TRANSACTION_getSettingsState = 9;
        static final int TRANSACTION_registerCallback = 1;
        static final int TRANSACTION_unregisterCallback = 2;

        public Stub() {
            attachInterface(this, ISharedConnectivityService.DESCRIPTOR);
        }

        public static ISharedConnectivityService asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(ISharedConnectivityService.DESCRIPTOR);
            if (iin != null && (iin instanceof ISharedConnectivityService)) {
                return (ISharedConnectivityService) iin;
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
                    return "registerCallback";
                case 2:
                    return "unregisterCallback";
                case 3:
                    return "connectHotspotNetwork";
                case 4:
                    return "disconnectHotspotNetwork";
                case 5:
                    return "connectKnownNetwork";
                case 6:
                    return "forgetKnownNetwork";
                case 7:
                    return "getHotspotNetworks";
                case 8:
                    return "getKnownNetworks";
                case 9:
                    return "getSettingsState";
                case 10:
                    return "getHotspotNetworkConnectionStatus";
                case 11:
                    return "getKnownNetworkConnectionStatus";
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
                data.enforceInterface(ISharedConnectivityService.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(ISharedConnectivityService.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            ISharedConnectivityCallback _arg0 = ISharedConnectivityCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            registerCallback(_arg0);
                            reply.writeNoException();
                            break;
                        case 2:
                            ISharedConnectivityCallback _arg02 = ISharedConnectivityCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            unregisterCallback(_arg02);
                            reply.writeNoException();
                            break;
                        case 3:
                            HotspotNetwork _arg03 = (HotspotNetwork) data.readTypedObject(HotspotNetwork.CREATOR);
                            data.enforceNoDataAvail();
                            connectHotspotNetwork(_arg03);
                            reply.writeNoException();
                            break;
                        case 4:
                            HotspotNetwork _arg04 = (HotspotNetwork) data.readTypedObject(HotspotNetwork.CREATOR);
                            data.enforceNoDataAvail();
                            disconnectHotspotNetwork(_arg04);
                            reply.writeNoException();
                            break;
                        case 5:
                            KnownNetwork _arg05 = (KnownNetwork) data.readTypedObject(KnownNetwork.CREATOR);
                            data.enforceNoDataAvail();
                            connectKnownNetwork(_arg05);
                            reply.writeNoException();
                            break;
                        case 6:
                            KnownNetwork _arg06 = (KnownNetwork) data.readTypedObject(KnownNetwork.CREATOR);
                            data.enforceNoDataAvail();
                            forgetKnownNetwork(_arg06);
                            reply.writeNoException();
                            break;
                        case 7:
                            List<HotspotNetwork> _result = getHotspotNetworks();
                            reply.writeNoException();
                            reply.writeTypedList(_result, 1);
                            break;
                        case 8:
                            List<KnownNetwork> _result2 = getKnownNetworks();
                            reply.writeNoException();
                            reply.writeTypedList(_result2, 1);
                            break;
                        case 9:
                            SharedConnectivitySettingsState _result3 = getSettingsState();
                            reply.writeNoException();
                            reply.writeTypedObject(_result3, 1);
                            break;
                        case 10:
                            HotspotNetworkConnectionStatus _result4 = getHotspotNetworkConnectionStatus();
                            reply.writeNoException();
                            reply.writeTypedObject(_result4, 1);
                            break;
                        case 11:
                            KnownNetworkConnectionStatus _result5 = getKnownNetworkConnectionStatus();
                            reply.writeNoException();
                            reply.writeTypedObject(_result5, 1);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes2.dex */
        private static class Proxy implements ISharedConnectivityService {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return ISharedConnectivityService.DESCRIPTOR;
            }

            @Override // android.net.wifi.sharedconnectivity.service.ISharedConnectivityService
            public void registerCallback(ISharedConnectivityCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ISharedConnectivityService.DESCRIPTOR);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.net.wifi.sharedconnectivity.service.ISharedConnectivityService
            public void unregisterCallback(ISharedConnectivityCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ISharedConnectivityService.DESCRIPTOR);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.net.wifi.sharedconnectivity.service.ISharedConnectivityService
            public void connectHotspotNetwork(HotspotNetwork network) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ISharedConnectivityService.DESCRIPTOR);
                    _data.writeTypedObject(network, 0);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.net.wifi.sharedconnectivity.service.ISharedConnectivityService
            public void disconnectHotspotNetwork(HotspotNetwork network) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ISharedConnectivityService.DESCRIPTOR);
                    _data.writeTypedObject(network, 0);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.net.wifi.sharedconnectivity.service.ISharedConnectivityService
            public void connectKnownNetwork(KnownNetwork network) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ISharedConnectivityService.DESCRIPTOR);
                    _data.writeTypedObject(network, 0);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.net.wifi.sharedconnectivity.service.ISharedConnectivityService
            public void forgetKnownNetwork(KnownNetwork network) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ISharedConnectivityService.DESCRIPTOR);
                    _data.writeTypedObject(network, 0);
                    this.mRemote.transact(6, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.net.wifi.sharedconnectivity.service.ISharedConnectivityService
            public List<HotspotNetwork> getHotspotNetworks() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ISharedConnectivityService.DESCRIPTOR);
                    this.mRemote.transact(7, _data, _reply, 0);
                    _reply.readException();
                    List<HotspotNetwork> _result = _reply.createTypedArrayList(HotspotNetwork.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.net.wifi.sharedconnectivity.service.ISharedConnectivityService
            public List<KnownNetwork> getKnownNetworks() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ISharedConnectivityService.DESCRIPTOR);
                    this.mRemote.transact(8, _data, _reply, 0);
                    _reply.readException();
                    List<KnownNetwork> _result = _reply.createTypedArrayList(KnownNetwork.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.net.wifi.sharedconnectivity.service.ISharedConnectivityService
            public SharedConnectivitySettingsState getSettingsState() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ISharedConnectivityService.DESCRIPTOR);
                    this.mRemote.transact(9, _data, _reply, 0);
                    _reply.readException();
                    SharedConnectivitySettingsState _result = (SharedConnectivitySettingsState) _reply.readTypedObject(SharedConnectivitySettingsState.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.net.wifi.sharedconnectivity.service.ISharedConnectivityService
            public HotspotNetworkConnectionStatus getHotspotNetworkConnectionStatus() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ISharedConnectivityService.DESCRIPTOR);
                    this.mRemote.transact(10, _data, _reply, 0);
                    _reply.readException();
                    HotspotNetworkConnectionStatus _result = (HotspotNetworkConnectionStatus) _reply.readTypedObject(HotspotNetworkConnectionStatus.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.net.wifi.sharedconnectivity.service.ISharedConnectivityService
            public KnownNetworkConnectionStatus getKnownNetworkConnectionStatus() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(ISharedConnectivityService.DESCRIPTOR);
                    this.mRemote.transact(11, _data, _reply, 0);
                    _reply.readException();
                    KnownNetworkConnectionStatus _result = (KnownNetworkConnectionStatus) _reply.readTypedObject(KnownNetworkConnectionStatus.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 10;
        }
    }
}
