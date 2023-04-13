package android.net.wifi.nl80211;

import android.net.wifi.nl80211.IApInterface;
import android.net.wifi.nl80211.IClientInterface;
import android.net.wifi.nl80211.IInterfaceEventCallback;
import android.net.wifi.nl80211.IWificondEventCallback;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import java.util.List;
/* loaded from: classes2.dex */
public interface IWificond extends IInterface {
    public static final String DESCRIPTOR = "android.net.wifi.nl80211.IWificond";

    List<IBinder> GetApInterfaces() throws RemoteException;

    List<IBinder> GetClientInterfaces() throws RemoteException;

    void RegisterCallback(IInterfaceEventCallback iInterfaceEventCallback) throws RemoteException;

    void UnregisterCallback(IInterfaceEventCallback iInterfaceEventCallback) throws RemoteException;

    IApInterface createApInterface(String str) throws RemoteException;

    IClientInterface createClientInterface(String str) throws RemoteException;

    int[] getAvailable2gChannels() throws RemoteException;

    int[] getAvailable5gNonDFSChannels() throws RemoteException;

    int[] getAvailable60gChannels() throws RemoteException;

    int[] getAvailable6gChannels() throws RemoteException;

    int[] getAvailableDFSChannels() throws RemoteException;

    DeviceWiphyCapabilities getDeviceWiphyCapabilities(String str) throws RemoteException;

    void notifyCountryCodeChanged() throws RemoteException;

    void registerWificondEventCallback(IWificondEventCallback iWificondEventCallback) throws RemoteException;

    boolean tearDownApInterface(String str) throws RemoteException;

    boolean tearDownClientInterface(String str) throws RemoteException;

    void tearDownInterfaces() throws RemoteException;

    void unregisterWificondEventCallback(IWificondEventCallback iWificondEventCallback) throws RemoteException;

    /* loaded from: classes2.dex */
    public static class Default implements IWificond {
        @Override // android.net.wifi.nl80211.IWificond
        public IApInterface createApInterface(String iface_name) throws RemoteException {
            return null;
        }

        @Override // android.net.wifi.nl80211.IWificond
        public IClientInterface createClientInterface(String iface_name) throws RemoteException {
            return null;
        }

        @Override // android.net.wifi.nl80211.IWificond
        public boolean tearDownApInterface(String iface_name) throws RemoteException {
            return false;
        }

        @Override // android.net.wifi.nl80211.IWificond
        public boolean tearDownClientInterface(String iface_name) throws RemoteException {
            return false;
        }

        @Override // android.net.wifi.nl80211.IWificond
        public void tearDownInterfaces() throws RemoteException {
        }

        @Override // android.net.wifi.nl80211.IWificond
        public List<IBinder> GetClientInterfaces() throws RemoteException {
            return null;
        }

        @Override // android.net.wifi.nl80211.IWificond
        public List<IBinder> GetApInterfaces() throws RemoteException {
            return null;
        }

        @Override // android.net.wifi.nl80211.IWificond
        public int[] getAvailable2gChannels() throws RemoteException {
            return null;
        }

        @Override // android.net.wifi.nl80211.IWificond
        public int[] getAvailable5gNonDFSChannels() throws RemoteException {
            return null;
        }

        @Override // android.net.wifi.nl80211.IWificond
        public int[] getAvailableDFSChannels() throws RemoteException {
            return null;
        }

        @Override // android.net.wifi.nl80211.IWificond
        public int[] getAvailable6gChannels() throws RemoteException {
            return null;
        }

        @Override // android.net.wifi.nl80211.IWificond
        public int[] getAvailable60gChannels() throws RemoteException {
            return null;
        }

        @Override // android.net.wifi.nl80211.IWificond
        public void RegisterCallback(IInterfaceEventCallback callback) throws RemoteException {
        }

        @Override // android.net.wifi.nl80211.IWificond
        public void UnregisterCallback(IInterfaceEventCallback callback) throws RemoteException {
        }

        @Override // android.net.wifi.nl80211.IWificond
        public void registerWificondEventCallback(IWificondEventCallback callback) throws RemoteException {
        }

        @Override // android.net.wifi.nl80211.IWificond
        public void unregisterWificondEventCallback(IWificondEventCallback callback) throws RemoteException {
        }

        @Override // android.net.wifi.nl80211.IWificond
        public DeviceWiphyCapabilities getDeviceWiphyCapabilities(String iface_name) throws RemoteException {
            return null;
        }

        @Override // android.net.wifi.nl80211.IWificond
        public void notifyCountryCodeChanged() throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements IWificond {
        static final int TRANSACTION_GetApInterfaces = 7;
        static final int TRANSACTION_GetClientInterfaces = 6;
        static final int TRANSACTION_RegisterCallback = 13;
        static final int TRANSACTION_UnregisterCallback = 14;
        static final int TRANSACTION_createApInterface = 1;
        static final int TRANSACTION_createClientInterface = 2;
        static final int TRANSACTION_getAvailable2gChannels = 8;
        static final int TRANSACTION_getAvailable5gNonDFSChannels = 9;
        static final int TRANSACTION_getAvailable60gChannels = 12;
        static final int TRANSACTION_getAvailable6gChannels = 11;
        static final int TRANSACTION_getAvailableDFSChannels = 10;
        static final int TRANSACTION_getDeviceWiphyCapabilities = 17;
        static final int TRANSACTION_notifyCountryCodeChanged = 18;
        static final int TRANSACTION_registerWificondEventCallback = 15;
        static final int TRANSACTION_tearDownApInterface = 3;
        static final int TRANSACTION_tearDownClientInterface = 4;
        static final int TRANSACTION_tearDownInterfaces = 5;
        static final int TRANSACTION_unregisterWificondEventCallback = 16;

        public Stub() {
            attachInterface(this, IWificond.DESCRIPTOR);
        }

        public static IWificond asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IWificond.DESCRIPTOR);
            if (iin != null && (iin instanceof IWificond)) {
                return (IWificond) iin;
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
                    return "createApInterface";
                case 2:
                    return "createClientInterface";
                case 3:
                    return "tearDownApInterface";
                case 4:
                    return "tearDownClientInterface";
                case 5:
                    return "tearDownInterfaces";
                case 6:
                    return "GetClientInterfaces";
                case 7:
                    return "GetApInterfaces";
                case 8:
                    return "getAvailable2gChannels";
                case 9:
                    return "getAvailable5gNonDFSChannels";
                case 10:
                    return "getAvailableDFSChannels";
                case 11:
                    return "getAvailable6gChannels";
                case 12:
                    return "getAvailable60gChannels";
                case 13:
                    return "RegisterCallback";
                case 14:
                    return "UnregisterCallback";
                case 15:
                    return "registerWificondEventCallback";
                case 16:
                    return "unregisterWificondEventCallback";
                case 17:
                    return "getDeviceWiphyCapabilities";
                case 18:
                    return "notifyCountryCodeChanged";
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
                data.enforceInterface(IWificond.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IWificond.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            String _arg0 = data.readString();
                            data.enforceNoDataAvail();
                            IApInterface _result = createApInterface(_arg0);
                            reply.writeNoException();
                            reply.writeStrongInterface(_result);
                            break;
                        case 2:
                            String _arg02 = data.readString();
                            data.enforceNoDataAvail();
                            IClientInterface _result2 = createClientInterface(_arg02);
                            reply.writeNoException();
                            reply.writeStrongInterface(_result2);
                            break;
                        case 3:
                            String _arg03 = data.readString();
                            data.enforceNoDataAvail();
                            boolean _result3 = tearDownApInterface(_arg03);
                            reply.writeNoException();
                            reply.writeBoolean(_result3);
                            break;
                        case 4:
                            String _arg04 = data.readString();
                            data.enforceNoDataAvail();
                            boolean _result4 = tearDownClientInterface(_arg04);
                            reply.writeNoException();
                            reply.writeBoolean(_result4);
                            break;
                        case 5:
                            tearDownInterfaces();
                            reply.writeNoException();
                            break;
                        case 6:
                            List<IBinder> _result5 = GetClientInterfaces();
                            reply.writeNoException();
                            reply.writeBinderList(_result5);
                            break;
                        case 7:
                            List<IBinder> _result6 = GetApInterfaces();
                            reply.writeNoException();
                            reply.writeBinderList(_result6);
                            break;
                        case 8:
                            int[] _result7 = getAvailable2gChannels();
                            reply.writeNoException();
                            reply.writeIntArray(_result7);
                            break;
                        case 9:
                            int[] _result8 = getAvailable5gNonDFSChannels();
                            reply.writeNoException();
                            reply.writeIntArray(_result8);
                            break;
                        case 10:
                            int[] _result9 = getAvailableDFSChannels();
                            reply.writeNoException();
                            reply.writeIntArray(_result9);
                            break;
                        case 11:
                            int[] _result10 = getAvailable6gChannels();
                            reply.writeNoException();
                            reply.writeIntArray(_result10);
                            break;
                        case 12:
                            int[] _result11 = getAvailable60gChannels();
                            reply.writeNoException();
                            reply.writeIntArray(_result11);
                            break;
                        case 13:
                            IInterfaceEventCallback _arg05 = IInterfaceEventCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            RegisterCallback(_arg05);
                            break;
                        case 14:
                            IInterfaceEventCallback _arg06 = IInterfaceEventCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            UnregisterCallback(_arg06);
                            break;
                        case 15:
                            IWificondEventCallback _arg07 = IWificondEventCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            registerWificondEventCallback(_arg07);
                            break;
                        case 16:
                            IWificondEventCallback _arg08 = IWificondEventCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            unregisterWificondEventCallback(_arg08);
                            break;
                        case 17:
                            String _arg09 = data.readString();
                            data.enforceNoDataAvail();
                            DeviceWiphyCapabilities _result12 = getDeviceWiphyCapabilities(_arg09);
                            reply.writeNoException();
                            reply.writeTypedObject(_result12, 1);
                            break;
                        case 18:
                            notifyCountryCodeChanged();
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes2.dex */
        private static class Proxy implements IWificond {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IWificond.DESCRIPTOR;
            }

            @Override // android.net.wifi.nl80211.IWificond
            public IApInterface createApInterface(String iface_name) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IWificond.DESCRIPTOR);
                    _data.writeString(iface_name);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                    IApInterface _result = IApInterface.Stub.asInterface(_reply.readStrongBinder());
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.net.wifi.nl80211.IWificond
            public IClientInterface createClientInterface(String iface_name) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IWificond.DESCRIPTOR);
                    _data.writeString(iface_name);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                    IClientInterface _result = IClientInterface.Stub.asInterface(_reply.readStrongBinder());
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.net.wifi.nl80211.IWificond
            public boolean tearDownApInterface(String iface_name) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IWificond.DESCRIPTOR);
                    _data.writeString(iface_name);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.net.wifi.nl80211.IWificond
            public boolean tearDownClientInterface(String iface_name) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IWificond.DESCRIPTOR);
                    _data.writeString(iface_name);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.net.wifi.nl80211.IWificond
            public void tearDownInterfaces() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IWificond.DESCRIPTOR);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.net.wifi.nl80211.IWificond
            public List<IBinder> GetClientInterfaces() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IWificond.DESCRIPTOR);
                    this.mRemote.transact(6, _data, _reply, 0);
                    _reply.readException();
                    List<IBinder> _result = _reply.createBinderArrayList();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.net.wifi.nl80211.IWificond
            public List<IBinder> GetApInterfaces() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IWificond.DESCRIPTOR);
                    this.mRemote.transact(7, _data, _reply, 0);
                    _reply.readException();
                    List<IBinder> _result = _reply.createBinderArrayList();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.net.wifi.nl80211.IWificond
            public int[] getAvailable2gChannels() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IWificond.DESCRIPTOR);
                    this.mRemote.transact(8, _data, _reply, 0);
                    _reply.readException();
                    int[] _result = _reply.createIntArray();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.net.wifi.nl80211.IWificond
            public int[] getAvailable5gNonDFSChannels() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IWificond.DESCRIPTOR);
                    this.mRemote.transact(9, _data, _reply, 0);
                    _reply.readException();
                    int[] _result = _reply.createIntArray();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.net.wifi.nl80211.IWificond
            public int[] getAvailableDFSChannels() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IWificond.DESCRIPTOR);
                    this.mRemote.transact(10, _data, _reply, 0);
                    _reply.readException();
                    int[] _result = _reply.createIntArray();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.net.wifi.nl80211.IWificond
            public int[] getAvailable6gChannels() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IWificond.DESCRIPTOR);
                    this.mRemote.transact(11, _data, _reply, 0);
                    _reply.readException();
                    int[] _result = _reply.createIntArray();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.net.wifi.nl80211.IWificond
            public int[] getAvailable60gChannels() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IWificond.DESCRIPTOR);
                    this.mRemote.transact(12, _data, _reply, 0);
                    _reply.readException();
                    int[] _result = _reply.createIntArray();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.net.wifi.nl80211.IWificond
            public void RegisterCallback(IInterfaceEventCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IWificond.DESCRIPTOR);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(13, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.net.wifi.nl80211.IWificond
            public void UnregisterCallback(IInterfaceEventCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IWificond.DESCRIPTOR);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(14, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.net.wifi.nl80211.IWificond
            public void registerWificondEventCallback(IWificondEventCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IWificond.DESCRIPTOR);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(15, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.net.wifi.nl80211.IWificond
            public void unregisterWificondEventCallback(IWificondEventCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IWificond.DESCRIPTOR);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(16, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.net.wifi.nl80211.IWificond
            public DeviceWiphyCapabilities getDeviceWiphyCapabilities(String iface_name) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IWificond.DESCRIPTOR);
                    _data.writeString(iface_name);
                    this.mRemote.transact(17, _data, _reply, 0);
                    _reply.readException();
                    DeviceWiphyCapabilities _result = (DeviceWiphyCapabilities) _reply.readTypedObject(DeviceWiphyCapabilities.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.net.wifi.nl80211.IWificond
            public void notifyCountryCodeChanged() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(IWificond.DESCRIPTOR);
                    this.mRemote.transact(18, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 17;
        }
    }
}
