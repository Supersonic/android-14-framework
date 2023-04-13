package android.hardware.gnss;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes.dex */
public interface IGnssConfiguration extends IInterface {
    public static final String DESCRIPTOR = "android$hardware$gnss$IGnssConfiguration".replace('$', '.');
    public static final int GLONASS_POS_PROTOCOL_LPP_UPLANE = 4;
    public static final int GLONASS_POS_PROTOCOL_RRC_CPLANE = 1;
    public static final int GLONASS_POS_PROTOCOL_RRLP_UPLANE = 2;
    public static final String HASH = "fc957f1d3d261d065ff5e5415f2d21caa79c310f";
    public static final int LPP_PROFILE_CONTROL_PLANE = 2;
    public static final int LPP_PROFILE_USER_PLANE = 1;
    public static final int SUPL_MODE_MSA = 2;
    public static final int SUPL_MODE_MSB = 1;
    public static final int VERSION = 2;

    String getInterfaceHash() throws RemoteException;

    int getInterfaceVersion() throws RemoteException;

    void setBlocklist(BlocklistedSource[] blocklistedSourceArr) throws RemoteException;

    void setEmergencySuplPdn(boolean z) throws RemoteException;

    void setEsExtensionSec(int i) throws RemoteException;

    void setGlonassPositioningProtocol(int i) throws RemoteException;

    void setLppProfile(int i) throws RemoteException;

    void setSuplMode(int i) throws RemoteException;

    void setSuplVersion(int i) throws RemoteException;

    /* loaded from: classes.dex */
    public static class Default implements IGnssConfiguration {
        @Override // android.hardware.gnss.IGnssConfiguration
        public void setSuplVersion(int version) throws RemoteException {
        }

        @Override // android.hardware.gnss.IGnssConfiguration
        public void setSuplMode(int mode) throws RemoteException {
        }

        @Override // android.hardware.gnss.IGnssConfiguration
        public void setLppProfile(int lppProfile) throws RemoteException {
        }

        @Override // android.hardware.gnss.IGnssConfiguration
        public void setGlonassPositioningProtocol(int protocol) throws RemoteException {
        }

        @Override // android.hardware.gnss.IGnssConfiguration
        public void setEmergencySuplPdn(boolean enable) throws RemoteException {
        }

        @Override // android.hardware.gnss.IGnssConfiguration
        public void setEsExtensionSec(int emergencyExtensionSeconds) throws RemoteException {
        }

        @Override // android.hardware.gnss.IGnssConfiguration
        public void setBlocklist(BlocklistedSource[] blocklist) throws RemoteException {
        }

        @Override // android.hardware.gnss.IGnssConfiguration
        public int getInterfaceVersion() {
            return 0;
        }

        @Override // android.hardware.gnss.IGnssConfiguration
        public String getInterfaceHash() {
            return "";
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements IGnssConfiguration {
        static final int TRANSACTION_getInterfaceHash = 16777214;
        static final int TRANSACTION_getInterfaceVersion = 16777215;
        static final int TRANSACTION_setBlocklist = 7;
        static final int TRANSACTION_setEmergencySuplPdn = 5;
        static final int TRANSACTION_setEsExtensionSec = 6;
        static final int TRANSACTION_setGlonassPositioningProtocol = 4;
        static final int TRANSACTION_setLppProfile = 3;
        static final int TRANSACTION_setSuplMode = 2;
        static final int TRANSACTION_setSuplVersion = 1;

        public Stub() {
            markVintfStability();
            attachInterface(this, DESCRIPTOR);
        }

        public static IGnssConfiguration asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IGnssConfiguration)) {
                return (IGnssConfiguration) iin;
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
                    return "setSuplVersion";
                case 2:
                    return "setSuplMode";
                case 3:
                    return "setLppProfile";
                case 4:
                    return "setGlonassPositioningProtocol";
                case 5:
                    return "setEmergencySuplPdn";
                case 6:
                    return "setEsExtensionSec";
                case 7:
                    return "setBlocklist";
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
                            setSuplVersion(_arg0);
                            reply.writeNoException();
                            break;
                        case 2:
                            int _arg02 = data.readInt();
                            data.enforceNoDataAvail();
                            setSuplMode(_arg02);
                            reply.writeNoException();
                            break;
                        case 3:
                            int _arg03 = data.readInt();
                            data.enforceNoDataAvail();
                            setLppProfile(_arg03);
                            reply.writeNoException();
                            break;
                        case 4:
                            int _arg04 = data.readInt();
                            data.enforceNoDataAvail();
                            setGlonassPositioningProtocol(_arg04);
                            reply.writeNoException();
                            break;
                        case 5:
                            boolean _arg05 = data.readBoolean();
                            data.enforceNoDataAvail();
                            setEmergencySuplPdn(_arg05);
                            reply.writeNoException();
                            break;
                        case 6:
                            int _arg06 = data.readInt();
                            data.enforceNoDataAvail();
                            setEsExtensionSec(_arg06);
                            reply.writeNoException();
                            break;
                        case 7:
                            BlocklistedSource[] _arg07 = (BlocklistedSource[]) data.createTypedArray(BlocklistedSource.CREATOR);
                            data.enforceNoDataAvail();
                            setBlocklist(_arg07);
                            reply.writeNoException();
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes.dex */
        private static class Proxy implements IGnssConfiguration {
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

            @Override // android.hardware.gnss.IGnssConfiguration
            public void setSuplVersion(int version) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(version);
                    boolean _status = this.mRemote.transact(1, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method setSuplVersion is unimplemented.");
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.gnss.IGnssConfiguration
            public void setSuplMode(int mode) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(mode);
                    boolean _status = this.mRemote.transact(2, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method setSuplMode is unimplemented.");
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.gnss.IGnssConfiguration
            public void setLppProfile(int lppProfile) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(lppProfile);
                    boolean _status = this.mRemote.transact(3, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method setLppProfile is unimplemented.");
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.gnss.IGnssConfiguration
            public void setGlonassPositioningProtocol(int protocol) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(protocol);
                    boolean _status = this.mRemote.transact(4, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method setGlonassPositioningProtocol is unimplemented.");
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.gnss.IGnssConfiguration
            public void setEmergencySuplPdn(boolean enable) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeBoolean(enable);
                    boolean _status = this.mRemote.transact(5, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method setEmergencySuplPdn is unimplemented.");
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.gnss.IGnssConfiguration
            public void setEsExtensionSec(int emergencyExtensionSeconds) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(emergencyExtensionSeconds);
                    boolean _status = this.mRemote.transact(6, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method setEsExtensionSec is unimplemented.");
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.gnss.IGnssConfiguration
            public void setBlocklist(BlocklistedSource[] blocklist) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedArray(blocklist, 0);
                    boolean _status = this.mRemote.transact(7, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method setBlocklist is unimplemented.");
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.gnss.IGnssConfiguration
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

            @Override // android.hardware.gnss.IGnssConfiguration
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
