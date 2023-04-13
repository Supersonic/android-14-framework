package android.hardware.gnss;

import android.hardware.gnss.IAGnssCallback;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes.dex */
public interface IAGnss extends IInterface {
    public static final String DESCRIPTOR = "android$hardware$gnss$IAGnss".replace('$', '.');
    public static final String HASH = "fc957f1d3d261d065ff5e5415f2d21caa79c310f";
    public static final int VERSION = 2;

    /* loaded from: classes.dex */
    public @interface ApnIpType {
        public static final int INVALID = 0;
        public static final int IPV4 = 1;
        public static final int IPV4V6 = 3;
        public static final int IPV6 = 2;
    }

    void dataConnClosed() throws RemoteException;

    void dataConnFailed() throws RemoteException;

    void dataConnOpen(long j, String str, int i) throws RemoteException;

    String getInterfaceHash() throws RemoteException;

    int getInterfaceVersion() throws RemoteException;

    void setCallback(IAGnssCallback iAGnssCallback) throws RemoteException;

    void setServer(int i, String str, int i2) throws RemoteException;

    /* loaded from: classes.dex */
    public static class Default implements IAGnss {
        @Override // android.hardware.gnss.IAGnss
        public void setCallback(IAGnssCallback callback) throws RemoteException {
        }

        @Override // android.hardware.gnss.IAGnss
        public void dataConnClosed() throws RemoteException {
        }

        @Override // android.hardware.gnss.IAGnss
        public void dataConnFailed() throws RemoteException {
        }

        @Override // android.hardware.gnss.IAGnss
        public void setServer(int type, String hostname, int port) throws RemoteException {
        }

        @Override // android.hardware.gnss.IAGnss
        public void dataConnOpen(long networkHandle, String apn, int apnIpType) throws RemoteException {
        }

        @Override // android.hardware.gnss.IAGnss
        public int getInterfaceVersion() {
            return 0;
        }

        @Override // android.hardware.gnss.IAGnss
        public String getInterfaceHash() {
            return "";
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements IAGnss {
        static final int TRANSACTION_dataConnClosed = 2;
        static final int TRANSACTION_dataConnFailed = 3;
        static final int TRANSACTION_dataConnOpen = 5;
        static final int TRANSACTION_getInterfaceHash = 16777214;
        static final int TRANSACTION_getInterfaceVersion = 16777215;
        static final int TRANSACTION_setCallback = 1;
        static final int TRANSACTION_setServer = 4;

        public Stub() {
            markVintfStability();
            attachInterface(this, DESCRIPTOR);
        }

        public static IAGnss asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IAGnss)) {
                return (IAGnss) iin;
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
                    return "setCallback";
                case 2:
                    return "dataConnClosed";
                case 3:
                    return "dataConnFailed";
                case 4:
                    return "setServer";
                case 5:
                    return "dataConnOpen";
                case 16777214:
                    return "getInterfaceHash";
                case 16777215:
                    return "getInterfaceVersion";
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
            String descriptor = DESCRIPTOR;
            if (code >= 1 && code <= 16777215) {
                data.enforceInterface(descriptor);
            }
            switch (code) {
                case 16777214:
                    reply.writeNoException();
                    reply.writeString(getInterfaceHash());
                    return true;
                case 16777215:
                    reply.writeNoException();
                    reply.writeInt(getInterfaceVersion());
                    return true;
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(descriptor);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            IAGnssCallback _arg0 = IAGnssCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            setCallback(_arg0);
                            reply.writeNoException();
                            break;
                        case 2:
                            dataConnClosed();
                            reply.writeNoException();
                            break;
                        case 3:
                            dataConnFailed();
                            reply.writeNoException();
                            break;
                        case 4:
                            int _arg02 = data.readInt();
                            String _arg1 = data.readString();
                            int _arg2 = data.readInt();
                            data.enforceNoDataAvail();
                            setServer(_arg02, _arg1, _arg2);
                            reply.writeNoException();
                            break;
                        case 5:
                            long _arg03 = data.readLong();
                            String _arg12 = data.readString();
                            int _arg22 = data.readInt();
                            data.enforceNoDataAvail();
                            dataConnOpen(_arg03, _arg12, _arg22);
                            reply.writeNoException();
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes.dex */
        private static class Proxy implements IAGnss {
            private IBinder mRemote;
            private int mCachedVersion = -1;
            private String mCachedHash = "-1";

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return DESCRIPTOR;
            }

            @Override // android.hardware.gnss.IAGnss
            public void setCallback(IAGnssCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeStrongInterface(callback);
                    boolean _status = this.mRemote.transact(1, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method setCallback is unimplemented.");
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.gnss.IAGnss
            public void dataConnClosed() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    boolean _status = this.mRemote.transact(2, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method dataConnClosed is unimplemented.");
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.gnss.IAGnss
            public void dataConnFailed() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    boolean _status = this.mRemote.transact(3, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method dataConnFailed is unimplemented.");
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.gnss.IAGnss
            public void setServer(int type, String hostname, int port) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(type);
                    _data.writeString(hostname);
                    _data.writeInt(port);
                    boolean _status = this.mRemote.transact(4, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method setServer is unimplemented.");
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.gnss.IAGnss
            public void dataConnOpen(long networkHandle, String apn, int apnIpType) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeLong(networkHandle);
                    _data.writeString(apn);
                    _data.writeInt(apnIpType);
                    boolean _status = this.mRemote.transact(5, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method dataConnOpen is unimplemented.");
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.gnss.IAGnss
            public int getInterfaceVersion() throws RemoteException {
                if (this.mCachedVersion == -1) {
                    Parcel data = Parcel.obtain(asBinder());
                    Parcel reply = Parcel.obtain();
                    try {
                        data.writeInterfaceToken(DESCRIPTOR);
                        this.mRemote.transact(16777215, data, reply, 0);
                        reply.readException();
                        this.mCachedVersion = reply.readInt();
                    } finally {
                        reply.recycle();
                        data.recycle();
                    }
                }
                return this.mCachedVersion;
            }

            @Override // android.hardware.gnss.IAGnss
            public synchronized String getInterfaceHash() throws RemoteException {
                if ("-1".equals(this.mCachedHash)) {
                    Parcel data = Parcel.obtain(asBinder());
                    Parcel reply = Parcel.obtain();
                    data.writeInterfaceToken(DESCRIPTOR);
                    this.mRemote.transact(16777214, data, reply, 0);
                    reply.readException();
                    this.mCachedHash = reply.readString();
                    reply.recycle();
                    data.recycle();
                }
                return this.mCachedHash;
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 16777214;
        }
    }
}
