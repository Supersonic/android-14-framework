package android.hardware.gnss;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes.dex */
public interface IGnssGeofenceCallback extends IInterface {
    public static final int AVAILABLE = 2;
    public static final String DESCRIPTOR = "android$hardware$gnss$IGnssGeofenceCallback".replace('$', '.');
    public static final int ENTERED = 1;
    public static final int ERROR_GENERIC = -149;
    public static final int ERROR_ID_EXISTS = -101;
    public static final int ERROR_ID_UNKNOWN = -102;
    public static final int ERROR_INVALID_TRANSITION = -103;
    public static final int ERROR_TOO_MANY_GEOFENCES = -100;
    public static final int EXITED = 2;
    public static final String HASH = "fc957f1d3d261d065ff5e5415f2d21caa79c310f";
    public static final int OPERATION_SUCCESS = 0;
    public static final int UNAVAILABLE = 1;
    public static final int UNCERTAIN = 4;
    public static final int VERSION = 2;

    String getInterfaceHash() throws RemoteException;

    int getInterfaceVersion() throws RemoteException;

    void gnssGeofenceAddCb(int i, int i2) throws RemoteException;

    void gnssGeofencePauseCb(int i, int i2) throws RemoteException;

    void gnssGeofenceRemoveCb(int i, int i2) throws RemoteException;

    void gnssGeofenceResumeCb(int i, int i2) throws RemoteException;

    void gnssGeofenceStatusCb(int i, GnssLocation gnssLocation) throws RemoteException;

    void gnssGeofenceTransitionCb(int i, GnssLocation gnssLocation, int i2, long j) throws RemoteException;

    /* loaded from: classes.dex */
    public static class Default implements IGnssGeofenceCallback {
        @Override // android.hardware.gnss.IGnssGeofenceCallback
        public void gnssGeofenceTransitionCb(int geofenceId, GnssLocation location, int transition, long timestampMillis) throws RemoteException {
        }

        @Override // android.hardware.gnss.IGnssGeofenceCallback
        public void gnssGeofenceStatusCb(int availability, GnssLocation lastLocation) throws RemoteException {
        }

        @Override // android.hardware.gnss.IGnssGeofenceCallback
        public void gnssGeofenceAddCb(int geofenceId, int status) throws RemoteException {
        }

        @Override // android.hardware.gnss.IGnssGeofenceCallback
        public void gnssGeofenceRemoveCb(int geofenceId, int status) throws RemoteException {
        }

        @Override // android.hardware.gnss.IGnssGeofenceCallback
        public void gnssGeofencePauseCb(int geofenceId, int status) throws RemoteException {
        }

        @Override // android.hardware.gnss.IGnssGeofenceCallback
        public void gnssGeofenceResumeCb(int geofenceId, int status) throws RemoteException {
        }

        @Override // android.hardware.gnss.IGnssGeofenceCallback
        public int getInterfaceVersion() {
            return 0;
        }

        @Override // android.hardware.gnss.IGnssGeofenceCallback
        public String getInterfaceHash() {
            return "";
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements IGnssGeofenceCallback {
        static final int TRANSACTION_getInterfaceHash = 16777214;
        static final int TRANSACTION_getInterfaceVersion = 16777215;
        static final int TRANSACTION_gnssGeofenceAddCb = 3;
        static final int TRANSACTION_gnssGeofencePauseCb = 5;
        static final int TRANSACTION_gnssGeofenceRemoveCb = 4;
        static final int TRANSACTION_gnssGeofenceResumeCb = 6;
        static final int TRANSACTION_gnssGeofenceStatusCb = 2;
        static final int TRANSACTION_gnssGeofenceTransitionCb = 1;

        public Stub() {
            markVintfStability();
            attachInterface(this, DESCRIPTOR);
        }

        public static IGnssGeofenceCallback asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IGnssGeofenceCallback)) {
                return (IGnssGeofenceCallback) iin;
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
                    return "gnssGeofenceTransitionCb";
                case 2:
                    return "gnssGeofenceStatusCb";
                case 3:
                    return "gnssGeofenceAddCb";
                case 4:
                    return "gnssGeofenceRemoveCb";
                case 5:
                    return "gnssGeofencePauseCb";
                case 6:
                    return "gnssGeofenceResumeCb";
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
                            int _arg0 = data.readInt();
                            GnssLocation _arg1 = (GnssLocation) data.readTypedObject(GnssLocation.CREATOR);
                            int _arg2 = data.readInt();
                            long _arg3 = data.readLong();
                            data.enforceNoDataAvail();
                            gnssGeofenceTransitionCb(_arg0, _arg1, _arg2, _arg3);
                            reply.writeNoException();
                            break;
                        case 2:
                            int _arg02 = data.readInt();
                            GnssLocation _arg12 = (GnssLocation) data.readTypedObject(GnssLocation.CREATOR);
                            data.enforceNoDataAvail();
                            gnssGeofenceStatusCb(_arg02, _arg12);
                            reply.writeNoException();
                            break;
                        case 3:
                            int _arg03 = data.readInt();
                            int _arg13 = data.readInt();
                            data.enforceNoDataAvail();
                            gnssGeofenceAddCb(_arg03, _arg13);
                            reply.writeNoException();
                            break;
                        case 4:
                            int _arg04 = data.readInt();
                            int _arg14 = data.readInt();
                            data.enforceNoDataAvail();
                            gnssGeofenceRemoveCb(_arg04, _arg14);
                            reply.writeNoException();
                            break;
                        case 5:
                            int _arg05 = data.readInt();
                            int _arg15 = data.readInt();
                            data.enforceNoDataAvail();
                            gnssGeofencePauseCb(_arg05, _arg15);
                            reply.writeNoException();
                            break;
                        case 6:
                            int _arg06 = data.readInt();
                            int _arg16 = data.readInt();
                            data.enforceNoDataAvail();
                            gnssGeofenceResumeCb(_arg06, _arg16);
                            reply.writeNoException();
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes.dex */
        private static class Proxy implements IGnssGeofenceCallback {
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

            @Override // android.hardware.gnss.IGnssGeofenceCallback
            public void gnssGeofenceTransitionCb(int geofenceId, GnssLocation location, int transition, long timestampMillis) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(geofenceId);
                    _data.writeTypedObject(location, 0);
                    _data.writeInt(transition);
                    _data.writeLong(timestampMillis);
                    boolean _status = this.mRemote.transact(1, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method gnssGeofenceTransitionCb is unimplemented.");
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.gnss.IGnssGeofenceCallback
            public void gnssGeofenceStatusCb(int availability, GnssLocation lastLocation) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(availability);
                    _data.writeTypedObject(lastLocation, 0);
                    boolean _status = this.mRemote.transact(2, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method gnssGeofenceStatusCb is unimplemented.");
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.gnss.IGnssGeofenceCallback
            public void gnssGeofenceAddCb(int geofenceId, int status) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(geofenceId);
                    _data.writeInt(status);
                    boolean _status = this.mRemote.transact(3, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method gnssGeofenceAddCb is unimplemented.");
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.gnss.IGnssGeofenceCallback
            public void gnssGeofenceRemoveCb(int geofenceId, int status) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(geofenceId);
                    _data.writeInt(status);
                    boolean _status = this.mRemote.transact(4, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method gnssGeofenceRemoveCb is unimplemented.");
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.gnss.IGnssGeofenceCallback
            public void gnssGeofencePauseCb(int geofenceId, int status) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(geofenceId);
                    _data.writeInt(status);
                    boolean _status = this.mRemote.transact(5, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method gnssGeofencePauseCb is unimplemented.");
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.gnss.IGnssGeofenceCallback
            public void gnssGeofenceResumeCb(int geofenceId, int status) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(geofenceId);
                    _data.writeInt(status);
                    boolean _status = this.mRemote.transact(6, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method gnssGeofenceResumeCb is unimplemented.");
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.gnss.IGnssGeofenceCallback
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

            @Override // android.hardware.gnss.IGnssGeofenceCallback
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
