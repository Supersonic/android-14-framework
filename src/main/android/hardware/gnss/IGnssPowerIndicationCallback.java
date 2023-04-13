package android.hardware.gnss;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes.dex */
public interface IGnssPowerIndicationCallback extends IInterface {
    public static final int CAPABILITY_MULTIBAND_ACQUISITION = 16;
    public static final int CAPABILITY_MULTIBAND_TRACKING = 4;
    public static final int CAPABILITY_OTHER_MODES = 32;
    public static final int CAPABILITY_SINGLEBAND_ACQUISITION = 8;
    public static final int CAPABILITY_SINGLEBAND_TRACKING = 2;
    public static final int CAPABILITY_TOTAL = 1;
    public static final String DESCRIPTOR = "android$hardware$gnss$IGnssPowerIndicationCallback".replace('$', '.');
    public static final String HASH = "fc957f1d3d261d065ff5e5415f2d21caa79c310f";
    public static final int VERSION = 2;

    String getInterfaceHash() throws RemoteException;

    int getInterfaceVersion() throws RemoteException;

    void gnssPowerStatsCb(GnssPowerStats gnssPowerStats) throws RemoteException;

    void setCapabilitiesCb(int i) throws RemoteException;

    /* loaded from: classes.dex */
    public static class Default implements IGnssPowerIndicationCallback {
        @Override // android.hardware.gnss.IGnssPowerIndicationCallback
        public void setCapabilitiesCb(int capabilities) throws RemoteException {
        }

        @Override // android.hardware.gnss.IGnssPowerIndicationCallback
        public void gnssPowerStatsCb(GnssPowerStats gnssPowerStats) throws RemoteException {
        }

        @Override // android.hardware.gnss.IGnssPowerIndicationCallback
        public int getInterfaceVersion() {
            return 0;
        }

        @Override // android.hardware.gnss.IGnssPowerIndicationCallback
        public String getInterfaceHash() {
            return "";
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements IGnssPowerIndicationCallback {
        static final int TRANSACTION_getInterfaceHash = 16777214;
        static final int TRANSACTION_getInterfaceVersion = 16777215;
        static final int TRANSACTION_gnssPowerStatsCb = 2;
        static final int TRANSACTION_setCapabilitiesCb = 1;

        public Stub() {
            markVintfStability();
            attachInterface(this, DESCRIPTOR);
        }

        public static IGnssPowerIndicationCallback asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IGnssPowerIndicationCallback)) {
                return (IGnssPowerIndicationCallback) iin;
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
                    return "setCapabilitiesCb";
                case 2:
                    return "gnssPowerStatsCb";
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
                            data.enforceNoDataAvail();
                            setCapabilitiesCb(_arg0);
                            reply.writeNoException();
                            break;
                        case 2:
                            GnssPowerStats _arg02 = (GnssPowerStats) data.readTypedObject(GnssPowerStats.CREATOR);
                            data.enforceNoDataAvail();
                            gnssPowerStatsCb(_arg02);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes.dex */
        private static class Proxy implements IGnssPowerIndicationCallback {
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

            @Override // android.hardware.gnss.IGnssPowerIndicationCallback
            public void setCapabilitiesCb(int capabilities) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(capabilities);
                    boolean _status = this.mRemote.transact(1, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method setCapabilitiesCb is unimplemented.");
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.gnss.IGnssPowerIndicationCallback
            public void gnssPowerStatsCb(GnssPowerStats gnssPowerStats) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(gnssPowerStats, 0);
                    boolean _status = this.mRemote.transact(2, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method gnssPowerStatsCb is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.gnss.IGnssPowerIndicationCallback
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

            @Override // android.hardware.gnss.IGnssPowerIndicationCallback
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
