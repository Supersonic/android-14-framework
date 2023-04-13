package android.debug;

import android.debug.IAdbCallback;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes.dex */
public interface IAdbManager extends IInterface {
    public static final String DESCRIPTOR = "android.debug.IAdbManager";

    void allowDebugging(boolean z, String str) throws RemoteException;

    void allowWirelessDebugging(boolean z, String str) throws RemoteException;

    void clearDebuggingKeys() throws RemoteException;

    void denyDebugging() throws RemoteException;

    void denyWirelessDebugging() throws RemoteException;

    void disablePairing() throws RemoteException;

    void enablePairingByPairingCode() throws RemoteException;

    void enablePairingByQrCode(String str, String str2) throws RemoteException;

    int getAdbWirelessPort() throws RemoteException;

    FingerprintAndPairDevice[] getPairedDevices() throws RemoteException;

    boolean isAdbWifiQrSupported() throws RemoteException;

    boolean isAdbWifiSupported() throws RemoteException;

    void registerCallback(IAdbCallback iAdbCallback) throws RemoteException;

    void unpairDevice(String str) throws RemoteException;

    void unregisterCallback(IAdbCallback iAdbCallback) throws RemoteException;

    /* loaded from: classes.dex */
    public static class Default implements IAdbManager {
        @Override // android.debug.IAdbManager
        public void allowDebugging(boolean alwaysAllow, String publicKey) throws RemoteException {
        }

        @Override // android.debug.IAdbManager
        public void denyDebugging() throws RemoteException {
        }

        @Override // android.debug.IAdbManager
        public void clearDebuggingKeys() throws RemoteException {
        }

        @Override // android.debug.IAdbManager
        public void allowWirelessDebugging(boolean alwaysAllow, String bssid) throws RemoteException {
        }

        @Override // android.debug.IAdbManager
        public void denyWirelessDebugging() throws RemoteException {
        }

        @Override // android.debug.IAdbManager
        public FingerprintAndPairDevice[] getPairedDevices() throws RemoteException {
            return null;
        }

        @Override // android.debug.IAdbManager
        public void unpairDevice(String fingerprint) throws RemoteException {
        }

        @Override // android.debug.IAdbManager
        public void enablePairingByPairingCode() throws RemoteException {
        }

        @Override // android.debug.IAdbManager
        public void enablePairingByQrCode(String serviceName, String password) throws RemoteException {
        }

        @Override // android.debug.IAdbManager
        public int getAdbWirelessPort() throws RemoteException {
            return 0;
        }

        @Override // android.debug.IAdbManager
        public void disablePairing() throws RemoteException {
        }

        @Override // android.debug.IAdbManager
        public boolean isAdbWifiSupported() throws RemoteException {
            return false;
        }

        @Override // android.debug.IAdbManager
        public boolean isAdbWifiQrSupported() throws RemoteException {
            return false;
        }

        @Override // android.debug.IAdbManager
        public void registerCallback(IAdbCallback callback) throws RemoteException {
        }

        @Override // android.debug.IAdbManager
        public void unregisterCallback(IAdbCallback callback) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements IAdbManager {
        static final int TRANSACTION_allowDebugging = 1;
        static final int TRANSACTION_allowWirelessDebugging = 4;
        static final int TRANSACTION_clearDebuggingKeys = 3;
        static final int TRANSACTION_denyDebugging = 2;
        static final int TRANSACTION_denyWirelessDebugging = 5;
        static final int TRANSACTION_disablePairing = 11;
        static final int TRANSACTION_enablePairingByPairingCode = 8;
        static final int TRANSACTION_enablePairingByQrCode = 9;
        static final int TRANSACTION_getAdbWirelessPort = 10;
        static final int TRANSACTION_getPairedDevices = 6;
        static final int TRANSACTION_isAdbWifiQrSupported = 13;
        static final int TRANSACTION_isAdbWifiSupported = 12;
        static final int TRANSACTION_registerCallback = 14;
        static final int TRANSACTION_unpairDevice = 7;
        static final int TRANSACTION_unregisterCallback = 15;

        public Stub() {
            attachInterface(this, IAdbManager.DESCRIPTOR);
        }

        public static IAdbManager asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(IAdbManager.DESCRIPTOR);
            if (iin != null && (iin instanceof IAdbManager)) {
                return (IAdbManager) iin;
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
                    return "allowDebugging";
                case 2:
                    return "denyDebugging";
                case 3:
                    return "clearDebuggingKeys";
                case 4:
                    return "allowWirelessDebugging";
                case 5:
                    return "denyWirelessDebugging";
                case 6:
                    return "getPairedDevices";
                case 7:
                    return "unpairDevice";
                case 8:
                    return "enablePairingByPairingCode";
                case 9:
                    return "enablePairingByQrCode";
                case 10:
                    return "getAdbWirelessPort";
                case 11:
                    return "disablePairing";
                case 12:
                    return "isAdbWifiSupported";
                case 13:
                    return "isAdbWifiQrSupported";
                case 14:
                    return "registerCallback";
                case 15:
                    return "unregisterCallback";
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
                data.enforceInterface(IAdbManager.DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(IAdbManager.DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            boolean _arg0 = data.readBoolean();
                            String _arg1 = data.readString();
                            data.enforceNoDataAvail();
                            allowDebugging(_arg0, _arg1);
                            reply.writeNoException();
                            break;
                        case 2:
                            denyDebugging();
                            reply.writeNoException();
                            break;
                        case 3:
                            clearDebuggingKeys();
                            reply.writeNoException();
                            break;
                        case 4:
                            boolean _arg02 = data.readBoolean();
                            String _arg12 = data.readString();
                            data.enforceNoDataAvail();
                            allowWirelessDebugging(_arg02, _arg12);
                            reply.writeNoException();
                            break;
                        case 5:
                            denyWirelessDebugging();
                            reply.writeNoException();
                            break;
                        case 6:
                            FingerprintAndPairDevice[] _result = getPairedDevices();
                            reply.writeNoException();
                            reply.writeTypedArray(_result, 1);
                            break;
                        case 7:
                            String _arg03 = data.readString();
                            data.enforceNoDataAvail();
                            unpairDevice(_arg03);
                            reply.writeNoException();
                            break;
                        case 8:
                            enablePairingByPairingCode();
                            reply.writeNoException();
                            break;
                        case 9:
                            String _arg04 = data.readString();
                            String _arg13 = data.readString();
                            data.enforceNoDataAvail();
                            enablePairingByQrCode(_arg04, _arg13);
                            reply.writeNoException();
                            break;
                        case 10:
                            int _result2 = getAdbWirelessPort();
                            reply.writeNoException();
                            reply.writeInt(_result2);
                            break;
                        case 11:
                            disablePairing();
                            reply.writeNoException();
                            break;
                        case 12:
                            boolean _result3 = isAdbWifiSupported();
                            reply.writeNoException();
                            reply.writeBoolean(_result3);
                            break;
                        case 13:
                            boolean _result4 = isAdbWifiQrSupported();
                            reply.writeNoException();
                            reply.writeBoolean(_result4);
                            break;
                        case 14:
                            IAdbCallback _arg05 = IAdbCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            registerCallback(_arg05);
                            reply.writeNoException();
                            break;
                        case 15:
                            IAdbCallback _arg06 = IAdbCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            unregisterCallback(_arg06);
                            reply.writeNoException();
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes.dex */
        private static class Proxy implements IAdbManager {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return IAdbManager.DESCRIPTOR;
            }

            @Override // android.debug.IAdbManager
            public void allowDebugging(boolean alwaysAllow, String publicKey) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IAdbManager.DESCRIPTOR);
                    _data.writeBoolean(alwaysAllow);
                    _data.writeString(publicKey);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.debug.IAdbManager
            public void denyDebugging() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IAdbManager.DESCRIPTOR);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.debug.IAdbManager
            public void clearDebuggingKeys() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IAdbManager.DESCRIPTOR);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.debug.IAdbManager
            public void allowWirelessDebugging(boolean alwaysAllow, String bssid) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IAdbManager.DESCRIPTOR);
                    _data.writeBoolean(alwaysAllow);
                    _data.writeString(bssid);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.debug.IAdbManager
            public void denyWirelessDebugging() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IAdbManager.DESCRIPTOR);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.debug.IAdbManager
            public FingerprintAndPairDevice[] getPairedDevices() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IAdbManager.DESCRIPTOR);
                    this.mRemote.transact(6, _data, _reply, 0);
                    _reply.readException();
                    FingerprintAndPairDevice[] _result = (FingerprintAndPairDevice[]) _reply.createTypedArray(FingerprintAndPairDevice.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.debug.IAdbManager
            public void unpairDevice(String fingerprint) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IAdbManager.DESCRIPTOR);
                    _data.writeString(fingerprint);
                    this.mRemote.transact(7, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.debug.IAdbManager
            public void enablePairingByPairingCode() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IAdbManager.DESCRIPTOR);
                    this.mRemote.transact(8, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.debug.IAdbManager
            public void enablePairingByQrCode(String serviceName, String password) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IAdbManager.DESCRIPTOR);
                    _data.writeString(serviceName);
                    _data.writeString(password);
                    this.mRemote.transact(9, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.debug.IAdbManager
            public int getAdbWirelessPort() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IAdbManager.DESCRIPTOR);
                    this.mRemote.transact(10, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.debug.IAdbManager
            public void disablePairing() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IAdbManager.DESCRIPTOR);
                    this.mRemote.transact(11, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.debug.IAdbManager
            public boolean isAdbWifiSupported() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IAdbManager.DESCRIPTOR);
                    this.mRemote.transact(12, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.debug.IAdbManager
            public boolean isAdbWifiQrSupported() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IAdbManager.DESCRIPTOR);
                    this.mRemote.transact(13, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.debug.IAdbManager
            public void registerCallback(IAdbCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IAdbManager.DESCRIPTOR);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(14, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.debug.IAdbManager
            public void unregisterCallback(IAdbCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(IAdbManager.DESCRIPTOR);
                    _data.writeStrongInterface(callback);
                    this.mRemote.transact(15, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 14;
        }
    }
}
