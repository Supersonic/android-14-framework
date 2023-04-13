package android.hardware.gnss;

import android.p008os.BadParcelableException;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.p008os.RemoteException;
/* loaded from: classes.dex */
public interface IGnssCallback extends IInterface {
    public static final int CAPABILITY_ANTENNA_INFO = 2048;
    public static final int CAPABILITY_CORRELATION_VECTOR = 4096;
    public static final int CAPABILITY_GEOFENCING = 32;
    public static final int CAPABILITY_LOW_POWER_MODE = 256;
    public static final int CAPABILITY_MEASUREMENTS = 64;
    public static final int CAPABILITY_MEASUREMENT_CORRECTIONS = 1024;
    public static final int CAPABILITY_MEASUREMENT_CORRECTIONS_FOR_DRIVING = 16384;
    public static final int CAPABILITY_MSA = 4;
    public static final int CAPABILITY_MSB = 2;
    public static final int CAPABILITY_NAV_MESSAGES = 128;
    public static final int CAPABILITY_ON_DEMAND_TIME = 16;
    public static final int CAPABILITY_SATELLITE_BLOCKLIST = 512;
    public static final int CAPABILITY_SATELLITE_PVT = 8192;
    public static final int CAPABILITY_SCHEDULING = 1;
    public static final int CAPABILITY_SINGLE_SHOT = 8;
    public static final String DESCRIPTOR = "android$hardware$gnss$IGnssCallback".replace('$', '.');
    public static final String HASH = "fc957f1d3d261d065ff5e5415f2d21caa79c310f";
    public static final int VERSION = 2;

    /* loaded from: classes.dex */
    public @interface GnssStatusValue {
        public static final int ENGINE_OFF = 4;
        public static final int ENGINE_ON = 3;
        public static final int NONE = 0;
        public static final int SESSION_BEGIN = 1;
        public static final int SESSION_END = 2;
    }

    /* loaded from: classes.dex */
    public @interface GnssSvFlags {
        public static final int HAS_ALMANAC_DATA = 2;
        public static final int HAS_CARRIER_FREQUENCY = 8;
        public static final int HAS_EPHEMERIS_DATA = 1;
        public static final int NONE = 0;
        public static final int USED_IN_FIX = 4;
    }

    String getInterfaceHash() throws RemoteException;

    int getInterfaceVersion() throws RemoteException;

    void gnssAcquireWakelockCb() throws RemoteException;

    void gnssLocationCb(GnssLocation gnssLocation) throws RemoteException;

    void gnssNmeaCb(long j, String str) throws RemoteException;

    void gnssReleaseWakelockCb() throws RemoteException;

    void gnssRequestLocationCb(boolean z, boolean z2) throws RemoteException;

    void gnssRequestTimeCb() throws RemoteException;

    void gnssSetCapabilitiesCb(int i) throws RemoteException;

    void gnssSetSystemInfoCb(GnssSystemInfo gnssSystemInfo) throws RemoteException;

    void gnssStatusCb(int i) throws RemoteException;

    void gnssSvStatusCb(GnssSvInfo[] gnssSvInfoArr) throws RemoteException;

    /* loaded from: classes.dex */
    public static class Default implements IGnssCallback {
        @Override // android.hardware.gnss.IGnssCallback
        public void gnssSetCapabilitiesCb(int capabilities) throws RemoteException {
        }

        @Override // android.hardware.gnss.IGnssCallback
        public void gnssStatusCb(int status) throws RemoteException {
        }

        @Override // android.hardware.gnss.IGnssCallback
        public void gnssSvStatusCb(GnssSvInfo[] svInfoList) throws RemoteException {
        }

        @Override // android.hardware.gnss.IGnssCallback
        public void gnssLocationCb(GnssLocation location) throws RemoteException {
        }

        @Override // android.hardware.gnss.IGnssCallback
        public void gnssNmeaCb(long timestamp, String nmea) throws RemoteException {
        }

        @Override // android.hardware.gnss.IGnssCallback
        public void gnssAcquireWakelockCb() throws RemoteException {
        }

        @Override // android.hardware.gnss.IGnssCallback
        public void gnssReleaseWakelockCb() throws RemoteException {
        }

        @Override // android.hardware.gnss.IGnssCallback
        public void gnssSetSystemInfoCb(GnssSystemInfo info) throws RemoteException {
        }

        @Override // android.hardware.gnss.IGnssCallback
        public void gnssRequestTimeCb() throws RemoteException {
        }

        @Override // android.hardware.gnss.IGnssCallback
        public void gnssRequestLocationCb(boolean independentFromGnss, boolean isUserEmergency) throws RemoteException {
        }

        @Override // android.hardware.gnss.IGnssCallback
        public int getInterfaceVersion() {
            return 0;
        }

        @Override // android.hardware.gnss.IGnssCallback
        public String getInterfaceHash() {
            return "";
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements IGnssCallback {
        static final int TRANSACTION_getInterfaceHash = 16777214;
        static final int TRANSACTION_getInterfaceVersion = 16777215;
        static final int TRANSACTION_gnssAcquireWakelockCb = 6;
        static final int TRANSACTION_gnssLocationCb = 4;
        static final int TRANSACTION_gnssNmeaCb = 5;
        static final int TRANSACTION_gnssReleaseWakelockCb = 7;
        static final int TRANSACTION_gnssRequestLocationCb = 10;
        static final int TRANSACTION_gnssRequestTimeCb = 9;
        static final int TRANSACTION_gnssSetCapabilitiesCb = 1;
        static final int TRANSACTION_gnssSetSystemInfoCb = 8;
        static final int TRANSACTION_gnssStatusCb = 2;
        static final int TRANSACTION_gnssSvStatusCb = 3;

        public Stub() {
            markVintfStability();
            attachInterface(this, DESCRIPTOR);
        }

        public static IGnssCallback asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IGnssCallback)) {
                return (IGnssCallback) iin;
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
                    return "gnssSetCapabilitiesCb";
                case 2:
                    return "gnssStatusCb";
                case 3:
                    return "gnssSvStatusCb";
                case 4:
                    return "gnssLocationCb";
                case 5:
                    return "gnssNmeaCb";
                case 6:
                    return "gnssAcquireWakelockCb";
                case 7:
                    return "gnssReleaseWakelockCb";
                case 8:
                    return "gnssSetSystemInfoCb";
                case 9:
                    return "gnssRequestTimeCb";
                case 10:
                    return "gnssRequestLocationCb";
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
                            gnssSetCapabilitiesCb(_arg0);
                            reply.writeNoException();
                            break;
                        case 2:
                            int _arg02 = data.readInt();
                            data.enforceNoDataAvail();
                            gnssStatusCb(_arg02);
                            reply.writeNoException();
                            break;
                        case 3:
                            GnssSvInfo[] _arg03 = (GnssSvInfo[]) data.createTypedArray(GnssSvInfo.CREATOR);
                            data.enforceNoDataAvail();
                            gnssSvStatusCb(_arg03);
                            reply.writeNoException();
                            break;
                        case 4:
                            GnssLocation _arg04 = (GnssLocation) data.readTypedObject(GnssLocation.CREATOR);
                            data.enforceNoDataAvail();
                            gnssLocationCb(_arg04);
                            reply.writeNoException();
                            break;
                        case 5:
                            long _arg05 = data.readLong();
                            String _arg1 = data.readString();
                            data.enforceNoDataAvail();
                            gnssNmeaCb(_arg05, _arg1);
                            reply.writeNoException();
                            break;
                        case 6:
                            gnssAcquireWakelockCb();
                            reply.writeNoException();
                            break;
                        case 7:
                            gnssReleaseWakelockCb();
                            reply.writeNoException();
                            break;
                        case 8:
                            GnssSystemInfo _arg06 = (GnssSystemInfo) data.readTypedObject(GnssSystemInfo.CREATOR);
                            data.enforceNoDataAvail();
                            gnssSetSystemInfoCb(_arg06);
                            reply.writeNoException();
                            break;
                        case 9:
                            gnssRequestTimeCb();
                            reply.writeNoException();
                            break;
                        case 10:
                            boolean _arg07 = data.readBoolean();
                            boolean _arg12 = data.readBoolean();
                            data.enforceNoDataAvail();
                            gnssRequestLocationCb(_arg07, _arg12);
                            reply.writeNoException();
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes.dex */
        private static class Proxy implements IGnssCallback {
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

            @Override // android.hardware.gnss.IGnssCallback
            public void gnssSetCapabilitiesCb(int capabilities) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(capabilities);
                    boolean _status = this.mRemote.transact(1, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method gnssSetCapabilitiesCb is unimplemented.");
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.gnss.IGnssCallback
            public void gnssStatusCb(int status) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(status);
                    boolean _status = this.mRemote.transact(2, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method gnssStatusCb is unimplemented.");
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.gnss.IGnssCallback
            public void gnssSvStatusCb(GnssSvInfo[] svInfoList) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedArray(svInfoList, 0);
                    boolean _status = this.mRemote.transact(3, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method gnssSvStatusCb is unimplemented.");
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.gnss.IGnssCallback
            public void gnssLocationCb(GnssLocation location) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(location, 0);
                    boolean _status = this.mRemote.transact(4, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method gnssLocationCb is unimplemented.");
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.gnss.IGnssCallback
            public void gnssNmeaCb(long timestamp, String nmea) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeLong(timestamp);
                    _data.writeString(nmea);
                    boolean _status = this.mRemote.transact(5, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method gnssNmeaCb is unimplemented.");
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.gnss.IGnssCallback
            public void gnssAcquireWakelockCb() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    boolean _status = this.mRemote.transact(6, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method gnssAcquireWakelockCb is unimplemented.");
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.gnss.IGnssCallback
            public void gnssReleaseWakelockCb() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    boolean _status = this.mRemote.transact(7, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method gnssReleaseWakelockCb is unimplemented.");
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.gnss.IGnssCallback
            public void gnssSetSystemInfoCb(GnssSystemInfo info) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    boolean _status = this.mRemote.transact(8, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method gnssSetSystemInfoCb is unimplemented.");
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.gnss.IGnssCallback
            public void gnssRequestTimeCb() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    boolean _status = this.mRemote.transact(9, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method gnssRequestTimeCb is unimplemented.");
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.gnss.IGnssCallback
            public void gnssRequestLocationCb(boolean independentFromGnss, boolean isUserEmergency) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeBoolean(independentFromGnss);
                    _data.writeBoolean(isUserEmergency);
                    boolean _status = this.mRemote.transact(10, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method gnssRequestLocationCb is unimplemented.");
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.gnss.IGnssCallback
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

            @Override // android.hardware.gnss.IGnssCallback
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

    /* loaded from: classes.dex */
    public static class GnssSvInfo implements Parcelable {
        public static final Parcelable.Creator<GnssSvInfo> CREATOR = new Parcelable.Creator<GnssSvInfo>() { // from class: android.hardware.gnss.IGnssCallback.GnssSvInfo.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public GnssSvInfo createFromParcel(Parcel _aidl_source) {
                GnssSvInfo _aidl_out = new GnssSvInfo();
                _aidl_out.readFromParcel(_aidl_source);
                return _aidl_out;
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public GnssSvInfo[] newArray(int _aidl_size) {
                return new GnssSvInfo[_aidl_size];
            }
        };
        public int constellation;
        public int svid = 0;
        public float cN0Dbhz = 0.0f;
        public float basebandCN0DbHz = 0.0f;
        public float elevationDegrees = 0.0f;
        public float azimuthDegrees = 0.0f;
        public long carrierFrequencyHz = 0;
        public int svFlag = 0;

        @Override // android.p008os.Parcelable
        public final int getStability() {
            return 1;
        }

        @Override // android.p008os.Parcelable
        public final void writeToParcel(Parcel _aidl_parcel, int _aidl_flag) {
            int _aidl_start_pos = _aidl_parcel.dataPosition();
            _aidl_parcel.writeInt(0);
            _aidl_parcel.writeInt(this.svid);
            _aidl_parcel.writeInt(this.constellation);
            _aidl_parcel.writeFloat(this.cN0Dbhz);
            _aidl_parcel.writeFloat(this.basebandCN0DbHz);
            _aidl_parcel.writeFloat(this.elevationDegrees);
            _aidl_parcel.writeFloat(this.azimuthDegrees);
            _aidl_parcel.writeLong(this.carrierFrequencyHz);
            _aidl_parcel.writeInt(this.svFlag);
            int _aidl_end_pos = _aidl_parcel.dataPosition();
            _aidl_parcel.setDataPosition(_aidl_start_pos);
            _aidl_parcel.writeInt(_aidl_end_pos - _aidl_start_pos);
            _aidl_parcel.setDataPosition(_aidl_end_pos);
        }

        public final void readFromParcel(Parcel _aidl_parcel) {
            int _aidl_start_pos = _aidl_parcel.dataPosition();
            int _aidl_parcelable_size = _aidl_parcel.readInt();
            try {
                if (_aidl_parcelable_size < 4) {
                    throw new BadParcelableException("Parcelable too small");
                }
                if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                    if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                        throw new BadParcelableException("Overflow in the size of parcelable");
                    }
                    _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                    return;
                }
                this.svid = _aidl_parcel.readInt();
                if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                    if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                        throw new BadParcelableException("Overflow in the size of parcelable");
                    }
                    _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                    return;
                }
                this.constellation = _aidl_parcel.readInt();
                if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                    if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                        throw new BadParcelableException("Overflow in the size of parcelable");
                    }
                    _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                    return;
                }
                this.cN0Dbhz = _aidl_parcel.readFloat();
                if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                    if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                        throw new BadParcelableException("Overflow in the size of parcelable");
                    }
                    _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                    return;
                }
                this.basebandCN0DbHz = _aidl_parcel.readFloat();
                if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                    if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                        throw new BadParcelableException("Overflow in the size of parcelable");
                    }
                    _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                    return;
                }
                this.elevationDegrees = _aidl_parcel.readFloat();
                if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                    if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                        throw new BadParcelableException("Overflow in the size of parcelable");
                    }
                    _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                    return;
                }
                this.azimuthDegrees = _aidl_parcel.readFloat();
                if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                    if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                        throw new BadParcelableException("Overflow in the size of parcelable");
                    }
                    _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                    return;
                }
                this.carrierFrequencyHz = _aidl_parcel.readLong();
                if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                    if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                        throw new BadParcelableException("Overflow in the size of parcelable");
                    }
                    _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                    return;
                }
                this.svFlag = _aidl_parcel.readInt();
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
            } catch (Throwable th) {
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                throw th;
            }
        }

        @Override // android.p008os.Parcelable
        public int describeContents() {
            return 0;
        }
    }

    /* loaded from: classes.dex */
    public static class GnssSystemInfo implements Parcelable {
        public static final Parcelable.Creator<GnssSystemInfo> CREATOR = new Parcelable.Creator<GnssSystemInfo>() { // from class: android.hardware.gnss.IGnssCallback.GnssSystemInfo.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public GnssSystemInfo createFromParcel(Parcel _aidl_source) {
                GnssSystemInfo _aidl_out = new GnssSystemInfo();
                _aidl_out.readFromParcel(_aidl_source);
                return _aidl_out;
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public GnssSystemInfo[] newArray(int _aidl_size) {
                return new GnssSystemInfo[_aidl_size];
            }
        };
        public String name;
        public int yearOfHw = 0;

        @Override // android.p008os.Parcelable
        public final int getStability() {
            return 1;
        }

        @Override // android.p008os.Parcelable
        public final void writeToParcel(Parcel _aidl_parcel, int _aidl_flag) {
            int _aidl_start_pos = _aidl_parcel.dataPosition();
            _aidl_parcel.writeInt(0);
            _aidl_parcel.writeInt(this.yearOfHw);
            _aidl_parcel.writeString(this.name);
            int _aidl_end_pos = _aidl_parcel.dataPosition();
            _aidl_parcel.setDataPosition(_aidl_start_pos);
            _aidl_parcel.writeInt(_aidl_end_pos - _aidl_start_pos);
            _aidl_parcel.setDataPosition(_aidl_end_pos);
        }

        public final void readFromParcel(Parcel _aidl_parcel) {
            int _aidl_start_pos = _aidl_parcel.dataPosition();
            int _aidl_parcelable_size = _aidl_parcel.readInt();
            try {
                if (_aidl_parcelable_size < 4) {
                    throw new BadParcelableException("Parcelable too small");
                }
                if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                    if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                        throw new BadParcelableException("Overflow in the size of parcelable");
                    }
                    _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                    return;
                }
                this.yearOfHw = _aidl_parcel.readInt();
                if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                    if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                        throw new BadParcelableException("Overflow in the size of parcelable");
                    }
                    _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                    return;
                }
                this.name = _aidl_parcel.readString();
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
            } catch (Throwable th) {
                if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                    throw new BadParcelableException("Overflow in the size of parcelable");
                }
                _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                throw th;
            }
        }

        @Override // android.p008os.Parcelable
        public int describeContents() {
            return 0;
        }
    }
}
