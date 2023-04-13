package android.hardware.gnss;

import android.hardware.gnss.IAGnss;
import android.hardware.gnss.IAGnssRil;
import android.hardware.gnss.IGnssAntennaInfo;
import android.hardware.gnss.IGnssBatching;
import android.hardware.gnss.IGnssCallback;
import android.hardware.gnss.IGnssConfiguration;
import android.hardware.gnss.IGnssDebug;
import android.hardware.gnss.IGnssGeofence;
import android.hardware.gnss.IGnssMeasurementInterface;
import android.hardware.gnss.IGnssNavigationMessageInterface;
import android.hardware.gnss.IGnssPowerIndication;
import android.hardware.gnss.IGnssPsds;
import android.hardware.gnss.measurement_corrections.IMeasurementCorrectionsInterface;
import android.hardware.gnss.visibility_control.IGnssVisibilityControl;
import android.p008os.BadParcelableException;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.p008os.RemoteException;
/* loaded from: classes.dex */
public interface IGnss extends IInterface {
    public static final String DESCRIPTOR = "android$hardware$gnss$IGnss".replace('$', '.');
    public static final int ERROR_ALREADY_INIT = 2;
    public static final int ERROR_GENERIC = 3;
    public static final int ERROR_INVALID_ARGUMENT = 1;
    public static final String HASH = "fc957f1d3d261d065ff5e5415f2d21caa79c310f";
    public static final int VERSION = 2;

    /* loaded from: classes.dex */
    public @interface GnssAidingData {
        public static final int ALL = 65535;
        public static final int ALMANAC = 2;
        public static final int CELLDB_INFO = 32768;
        public static final int EPHEMERIS = 1;
        public static final int HEALTH = 64;
        public static final int IONO = 16;
        public static final int POSITION = 4;
        public static final int RTI = 1024;
        public static final int SADATA = 512;
        public static final int SVDIR = 128;
        public static final int SVSTEER = 256;
        public static final int TIME = 8;
        public static final int UTC = 32;
    }

    /* loaded from: classes.dex */
    public @interface GnssPositionMode {
        public static final int MS_ASSISTED = 2;
        public static final int MS_BASED = 1;
        public static final int STANDALONE = 0;
    }

    /* loaded from: classes.dex */
    public @interface GnssPositionRecurrence {
        public static final int RECURRENCE_PERIODIC = 0;
        public static final int RECURRENCE_SINGLE = 1;
    }

    void close() throws RemoteException;

    void deleteAidingData(int i) throws RemoteException;

    IAGnss getExtensionAGnss() throws RemoteException;

    IAGnssRil getExtensionAGnssRil() throws RemoteException;

    IGnssAntennaInfo getExtensionGnssAntennaInfo() throws RemoteException;

    IGnssBatching getExtensionGnssBatching() throws RemoteException;

    IGnssConfiguration getExtensionGnssConfiguration() throws RemoteException;

    IGnssDebug getExtensionGnssDebug() throws RemoteException;

    IGnssGeofence getExtensionGnssGeofence() throws RemoteException;

    IGnssMeasurementInterface getExtensionGnssMeasurement() throws RemoteException;

    IGnssNavigationMessageInterface getExtensionGnssNavigationMessage() throws RemoteException;

    IGnssPowerIndication getExtensionGnssPowerIndication() throws RemoteException;

    IGnssVisibilityControl getExtensionGnssVisibilityControl() throws RemoteException;

    IMeasurementCorrectionsInterface getExtensionMeasurementCorrections() throws RemoteException;

    IGnssPsds getExtensionPsds() throws RemoteException;

    String getInterfaceHash() throws RemoteException;

    int getInterfaceVersion() throws RemoteException;

    void injectBestLocation(GnssLocation gnssLocation) throws RemoteException;

    void injectLocation(GnssLocation gnssLocation) throws RemoteException;

    void injectTime(long j, long j2, int i) throws RemoteException;

    void setCallback(IGnssCallback iGnssCallback) throws RemoteException;

    void setPositionMode(PositionModeOptions positionModeOptions) throws RemoteException;

    void start() throws RemoteException;

    void startNmea() throws RemoteException;

    void startSvStatus() throws RemoteException;

    void stop() throws RemoteException;

    void stopNmea() throws RemoteException;

    void stopSvStatus() throws RemoteException;

    /* loaded from: classes.dex */
    public static class Default implements IGnss {
        @Override // android.hardware.gnss.IGnss
        public void setCallback(IGnssCallback callback) throws RemoteException {
        }

        @Override // android.hardware.gnss.IGnss
        public void close() throws RemoteException {
        }

        @Override // android.hardware.gnss.IGnss
        public IGnssPsds getExtensionPsds() throws RemoteException {
            return null;
        }

        @Override // android.hardware.gnss.IGnss
        public IGnssConfiguration getExtensionGnssConfiguration() throws RemoteException {
            return null;
        }

        @Override // android.hardware.gnss.IGnss
        public IGnssMeasurementInterface getExtensionGnssMeasurement() throws RemoteException {
            return null;
        }

        @Override // android.hardware.gnss.IGnss
        public IGnssPowerIndication getExtensionGnssPowerIndication() throws RemoteException {
            return null;
        }

        @Override // android.hardware.gnss.IGnss
        public IGnssBatching getExtensionGnssBatching() throws RemoteException {
            return null;
        }

        @Override // android.hardware.gnss.IGnss
        public IGnssGeofence getExtensionGnssGeofence() throws RemoteException {
            return null;
        }

        @Override // android.hardware.gnss.IGnss
        public IGnssNavigationMessageInterface getExtensionGnssNavigationMessage() throws RemoteException {
            return null;
        }

        @Override // android.hardware.gnss.IGnss
        public IAGnss getExtensionAGnss() throws RemoteException {
            return null;
        }

        @Override // android.hardware.gnss.IGnss
        public IAGnssRil getExtensionAGnssRil() throws RemoteException {
            return null;
        }

        @Override // android.hardware.gnss.IGnss
        public IGnssDebug getExtensionGnssDebug() throws RemoteException {
            return null;
        }

        @Override // android.hardware.gnss.IGnss
        public IGnssVisibilityControl getExtensionGnssVisibilityControl() throws RemoteException {
            return null;
        }

        @Override // android.hardware.gnss.IGnss
        public void start() throws RemoteException {
        }

        @Override // android.hardware.gnss.IGnss
        public void stop() throws RemoteException {
        }

        @Override // android.hardware.gnss.IGnss
        public void injectTime(long timeMs, long timeReferenceMs, int uncertaintyMs) throws RemoteException {
        }

        @Override // android.hardware.gnss.IGnss
        public void injectLocation(GnssLocation location) throws RemoteException {
        }

        @Override // android.hardware.gnss.IGnss
        public void injectBestLocation(GnssLocation location) throws RemoteException {
        }

        @Override // android.hardware.gnss.IGnss
        public void deleteAidingData(int aidingDataFlags) throws RemoteException {
        }

        @Override // android.hardware.gnss.IGnss
        public void setPositionMode(PositionModeOptions options) throws RemoteException {
        }

        @Override // android.hardware.gnss.IGnss
        public IGnssAntennaInfo getExtensionGnssAntennaInfo() throws RemoteException {
            return null;
        }

        @Override // android.hardware.gnss.IGnss
        public IMeasurementCorrectionsInterface getExtensionMeasurementCorrections() throws RemoteException {
            return null;
        }

        @Override // android.hardware.gnss.IGnss
        public void startSvStatus() throws RemoteException {
        }

        @Override // android.hardware.gnss.IGnss
        public void stopSvStatus() throws RemoteException {
        }

        @Override // android.hardware.gnss.IGnss
        public void startNmea() throws RemoteException {
        }

        @Override // android.hardware.gnss.IGnss
        public void stopNmea() throws RemoteException {
        }

        @Override // android.hardware.gnss.IGnss
        public int getInterfaceVersion() {
            return 0;
        }

        @Override // android.hardware.gnss.IGnss
        public String getInterfaceHash() {
            return "";
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes.dex */
    public static abstract class Stub extends Binder implements IGnss {
        static final int TRANSACTION_close = 2;
        static final int TRANSACTION_deleteAidingData = 19;
        static final int TRANSACTION_getExtensionAGnss = 10;
        static final int TRANSACTION_getExtensionAGnssRil = 11;
        static final int TRANSACTION_getExtensionGnssAntennaInfo = 21;
        static final int TRANSACTION_getExtensionGnssBatching = 7;
        static final int TRANSACTION_getExtensionGnssConfiguration = 4;
        static final int TRANSACTION_getExtensionGnssDebug = 12;
        static final int TRANSACTION_getExtensionGnssGeofence = 8;
        static final int TRANSACTION_getExtensionGnssMeasurement = 5;
        static final int TRANSACTION_getExtensionGnssNavigationMessage = 9;
        static final int TRANSACTION_getExtensionGnssPowerIndication = 6;
        static final int TRANSACTION_getExtensionGnssVisibilityControl = 13;
        static final int TRANSACTION_getExtensionMeasurementCorrections = 22;
        static final int TRANSACTION_getExtensionPsds = 3;
        static final int TRANSACTION_getInterfaceHash = 16777214;
        static final int TRANSACTION_getInterfaceVersion = 16777215;
        static final int TRANSACTION_injectBestLocation = 18;
        static final int TRANSACTION_injectLocation = 17;
        static final int TRANSACTION_injectTime = 16;
        static final int TRANSACTION_setCallback = 1;
        static final int TRANSACTION_setPositionMode = 20;
        static final int TRANSACTION_start = 14;
        static final int TRANSACTION_startNmea = 25;
        static final int TRANSACTION_startSvStatus = 23;
        static final int TRANSACTION_stop = 15;
        static final int TRANSACTION_stopNmea = 26;
        static final int TRANSACTION_stopSvStatus = 24;

        public Stub() {
            markVintfStability();
            attachInterface(this, DESCRIPTOR);
        }

        public static IGnss asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IGnss)) {
                return (IGnss) iin;
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
                    return "close";
                case 3:
                    return "getExtensionPsds";
                case 4:
                    return "getExtensionGnssConfiguration";
                case 5:
                    return "getExtensionGnssMeasurement";
                case 6:
                    return "getExtensionGnssPowerIndication";
                case 7:
                    return "getExtensionGnssBatching";
                case 8:
                    return "getExtensionGnssGeofence";
                case 9:
                    return "getExtensionGnssNavigationMessage";
                case 10:
                    return "getExtensionAGnss";
                case 11:
                    return "getExtensionAGnssRil";
                case 12:
                    return "getExtensionGnssDebug";
                case 13:
                    return "getExtensionGnssVisibilityControl";
                case 14:
                    return "start";
                case 15:
                    return "stop";
                case 16:
                    return "injectTime";
                case 17:
                    return "injectLocation";
                case 18:
                    return "injectBestLocation";
                case 19:
                    return "deleteAidingData";
                case 20:
                    return "setPositionMode";
                case 21:
                    return "getExtensionGnssAntennaInfo";
                case 22:
                    return "getExtensionMeasurementCorrections";
                case 23:
                    return "startSvStatus";
                case 24:
                    return "stopSvStatus";
                case 25:
                    return "startNmea";
                case 26:
                    return "stopNmea";
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
                            IGnssCallback _arg0 = IGnssCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            setCallback(_arg0);
                            reply.writeNoException();
                            break;
                        case 2:
                            close();
                            reply.writeNoException();
                            break;
                        case 3:
                            IGnssPsds _result = getExtensionPsds();
                            reply.writeNoException();
                            reply.writeStrongInterface(_result);
                            break;
                        case 4:
                            IGnssConfiguration _result2 = getExtensionGnssConfiguration();
                            reply.writeNoException();
                            reply.writeStrongInterface(_result2);
                            break;
                        case 5:
                            IGnssMeasurementInterface _result3 = getExtensionGnssMeasurement();
                            reply.writeNoException();
                            reply.writeStrongInterface(_result3);
                            break;
                        case 6:
                            IGnssPowerIndication _result4 = getExtensionGnssPowerIndication();
                            reply.writeNoException();
                            reply.writeStrongInterface(_result4);
                            break;
                        case 7:
                            IGnssBatching _result5 = getExtensionGnssBatching();
                            reply.writeNoException();
                            reply.writeStrongInterface(_result5);
                            break;
                        case 8:
                            IGnssGeofence _result6 = getExtensionGnssGeofence();
                            reply.writeNoException();
                            reply.writeStrongInterface(_result6);
                            break;
                        case 9:
                            IGnssNavigationMessageInterface _result7 = getExtensionGnssNavigationMessage();
                            reply.writeNoException();
                            reply.writeStrongInterface(_result7);
                            break;
                        case 10:
                            IAGnss _result8 = getExtensionAGnss();
                            reply.writeNoException();
                            reply.writeStrongInterface(_result8);
                            break;
                        case 11:
                            IAGnssRil _result9 = getExtensionAGnssRil();
                            reply.writeNoException();
                            reply.writeStrongInterface(_result9);
                            break;
                        case 12:
                            IGnssDebug _result10 = getExtensionGnssDebug();
                            reply.writeNoException();
                            reply.writeStrongInterface(_result10);
                            break;
                        case 13:
                            IGnssVisibilityControl _result11 = getExtensionGnssVisibilityControl();
                            reply.writeNoException();
                            reply.writeStrongInterface(_result11);
                            break;
                        case 14:
                            start();
                            reply.writeNoException();
                            break;
                        case 15:
                            stop();
                            reply.writeNoException();
                            break;
                        case 16:
                            long _arg02 = data.readLong();
                            long _arg1 = data.readLong();
                            int _arg2 = data.readInt();
                            data.enforceNoDataAvail();
                            injectTime(_arg02, _arg1, _arg2);
                            reply.writeNoException();
                            break;
                        case 17:
                            GnssLocation _arg03 = (GnssLocation) data.readTypedObject(GnssLocation.CREATOR);
                            data.enforceNoDataAvail();
                            injectLocation(_arg03);
                            reply.writeNoException();
                            break;
                        case 18:
                            GnssLocation _arg04 = (GnssLocation) data.readTypedObject(GnssLocation.CREATOR);
                            data.enforceNoDataAvail();
                            injectBestLocation(_arg04);
                            reply.writeNoException();
                            break;
                        case 19:
                            int _arg05 = data.readInt();
                            data.enforceNoDataAvail();
                            deleteAidingData(_arg05);
                            reply.writeNoException();
                            break;
                        case 20:
                            PositionModeOptions _arg06 = (PositionModeOptions) data.readTypedObject(PositionModeOptions.CREATOR);
                            data.enforceNoDataAvail();
                            setPositionMode(_arg06);
                            reply.writeNoException();
                            break;
                        case 21:
                            IGnssAntennaInfo _result12 = getExtensionGnssAntennaInfo();
                            reply.writeNoException();
                            reply.writeStrongInterface(_result12);
                            break;
                        case 22:
                            IMeasurementCorrectionsInterface _result13 = getExtensionMeasurementCorrections();
                            reply.writeNoException();
                            reply.writeStrongInterface(_result13);
                            break;
                        case 23:
                            startSvStatus();
                            reply.writeNoException();
                            break;
                        case 24:
                            stopSvStatus();
                            reply.writeNoException();
                            break;
                        case 25:
                            startNmea();
                            reply.writeNoException();
                            break;
                        case 26:
                            stopNmea();
                            reply.writeNoException();
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes.dex */
        private static class Proxy implements IGnss {
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

            @Override // android.hardware.gnss.IGnss
            public void setCallback(IGnssCallback callback) throws RemoteException {
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

            @Override // android.hardware.gnss.IGnss
            public void close() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    boolean _status = this.mRemote.transact(2, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method close is unimplemented.");
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.gnss.IGnss
            public IGnssPsds getExtensionPsds() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    boolean _status = this.mRemote.transact(3, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method getExtensionPsds is unimplemented.");
                    }
                    _reply.readException();
                    IGnssPsds _result = IGnssPsds.Stub.asInterface(_reply.readStrongBinder());
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.gnss.IGnss
            public IGnssConfiguration getExtensionGnssConfiguration() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    boolean _status = this.mRemote.transact(4, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method getExtensionGnssConfiguration is unimplemented.");
                    }
                    _reply.readException();
                    IGnssConfiguration _result = IGnssConfiguration.Stub.asInterface(_reply.readStrongBinder());
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.gnss.IGnss
            public IGnssMeasurementInterface getExtensionGnssMeasurement() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    boolean _status = this.mRemote.transact(5, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method getExtensionGnssMeasurement is unimplemented.");
                    }
                    _reply.readException();
                    IGnssMeasurementInterface _result = IGnssMeasurementInterface.Stub.asInterface(_reply.readStrongBinder());
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.gnss.IGnss
            public IGnssPowerIndication getExtensionGnssPowerIndication() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    boolean _status = this.mRemote.transact(6, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method getExtensionGnssPowerIndication is unimplemented.");
                    }
                    _reply.readException();
                    IGnssPowerIndication _result = IGnssPowerIndication.Stub.asInterface(_reply.readStrongBinder());
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.gnss.IGnss
            public IGnssBatching getExtensionGnssBatching() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    boolean _status = this.mRemote.transact(7, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method getExtensionGnssBatching is unimplemented.");
                    }
                    _reply.readException();
                    IGnssBatching _result = IGnssBatching.Stub.asInterface(_reply.readStrongBinder());
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.gnss.IGnss
            public IGnssGeofence getExtensionGnssGeofence() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    boolean _status = this.mRemote.transact(8, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method getExtensionGnssGeofence is unimplemented.");
                    }
                    _reply.readException();
                    IGnssGeofence _result = IGnssGeofence.Stub.asInterface(_reply.readStrongBinder());
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.gnss.IGnss
            public IGnssNavigationMessageInterface getExtensionGnssNavigationMessage() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    boolean _status = this.mRemote.transact(9, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method getExtensionGnssNavigationMessage is unimplemented.");
                    }
                    _reply.readException();
                    IGnssNavigationMessageInterface _result = IGnssNavigationMessageInterface.Stub.asInterface(_reply.readStrongBinder());
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.gnss.IGnss
            public IAGnss getExtensionAGnss() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    boolean _status = this.mRemote.transact(10, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method getExtensionAGnss is unimplemented.");
                    }
                    _reply.readException();
                    IAGnss _result = IAGnss.Stub.asInterface(_reply.readStrongBinder());
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.gnss.IGnss
            public IAGnssRil getExtensionAGnssRil() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    boolean _status = this.mRemote.transact(11, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method getExtensionAGnssRil is unimplemented.");
                    }
                    _reply.readException();
                    IAGnssRil _result = IAGnssRil.Stub.asInterface(_reply.readStrongBinder());
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.gnss.IGnss
            public IGnssDebug getExtensionGnssDebug() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    boolean _status = this.mRemote.transact(12, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method getExtensionGnssDebug is unimplemented.");
                    }
                    _reply.readException();
                    IGnssDebug _result = IGnssDebug.Stub.asInterface(_reply.readStrongBinder());
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.gnss.IGnss
            public IGnssVisibilityControl getExtensionGnssVisibilityControl() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    boolean _status = this.mRemote.transact(13, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method getExtensionGnssVisibilityControl is unimplemented.");
                    }
                    _reply.readException();
                    IGnssVisibilityControl _result = IGnssVisibilityControl.Stub.asInterface(_reply.readStrongBinder());
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.gnss.IGnss
            public void start() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    boolean _status = this.mRemote.transact(14, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method start is unimplemented.");
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.gnss.IGnss
            public void stop() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    boolean _status = this.mRemote.transact(15, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method stop is unimplemented.");
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.gnss.IGnss
            public void injectTime(long timeMs, long timeReferenceMs, int uncertaintyMs) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeLong(timeMs);
                    _data.writeLong(timeReferenceMs);
                    _data.writeInt(uncertaintyMs);
                    boolean _status = this.mRemote.transact(16, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method injectTime is unimplemented.");
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.gnss.IGnss
            public void injectLocation(GnssLocation location) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(location, 0);
                    boolean _status = this.mRemote.transact(17, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method injectLocation is unimplemented.");
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.gnss.IGnss
            public void injectBestLocation(GnssLocation location) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(location, 0);
                    boolean _status = this.mRemote.transact(18, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method injectBestLocation is unimplemented.");
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.gnss.IGnss
            public void deleteAidingData(int aidingDataFlags) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(aidingDataFlags);
                    boolean _status = this.mRemote.transact(19, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method deleteAidingData is unimplemented.");
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.gnss.IGnss
            public void setPositionMode(PositionModeOptions options) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(options, 0);
                    boolean _status = this.mRemote.transact(20, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method setPositionMode is unimplemented.");
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.gnss.IGnss
            public IGnssAntennaInfo getExtensionGnssAntennaInfo() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    boolean _status = this.mRemote.transact(21, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method getExtensionGnssAntennaInfo is unimplemented.");
                    }
                    _reply.readException();
                    IGnssAntennaInfo _result = IGnssAntennaInfo.Stub.asInterface(_reply.readStrongBinder());
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.gnss.IGnss
            public IMeasurementCorrectionsInterface getExtensionMeasurementCorrections() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    boolean _status = this.mRemote.transact(22, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method getExtensionMeasurementCorrections is unimplemented.");
                    }
                    _reply.readException();
                    IMeasurementCorrectionsInterface _result = IMeasurementCorrectionsInterface.Stub.asInterface(_reply.readStrongBinder());
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.gnss.IGnss
            public void startSvStatus() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    boolean _status = this.mRemote.transact(23, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method startSvStatus is unimplemented.");
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.gnss.IGnss
            public void stopSvStatus() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    boolean _status = this.mRemote.transact(24, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method stopSvStatus is unimplemented.");
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.gnss.IGnss
            public void startNmea() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    boolean _status = this.mRemote.transact(25, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method startNmea is unimplemented.");
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.gnss.IGnss
            public void stopNmea() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    boolean _status = this.mRemote.transact(26, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method stopNmea is unimplemented.");
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.gnss.IGnss
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

            @Override // android.hardware.gnss.IGnss
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
    public static class PositionModeOptions implements Parcelable {
        public static final Parcelable.Creator<PositionModeOptions> CREATOR = new Parcelable.Creator<PositionModeOptions>() { // from class: android.hardware.gnss.IGnss.PositionModeOptions.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public PositionModeOptions createFromParcel(Parcel _aidl_source) {
                PositionModeOptions _aidl_out = new PositionModeOptions();
                _aidl_out.readFromParcel(_aidl_source);
                return _aidl_out;
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public PositionModeOptions[] newArray(int _aidl_size) {
                return new PositionModeOptions[_aidl_size];
            }
        };
        public int mode;
        public int recurrence;
        public int minIntervalMs = 0;
        public int preferredAccuracyMeters = 0;
        public int preferredTimeMs = 0;
        public boolean lowPowerMode = false;

        @Override // android.p008os.Parcelable
        public final int getStability() {
            return 1;
        }

        @Override // android.p008os.Parcelable
        public final void writeToParcel(Parcel _aidl_parcel, int _aidl_flag) {
            int _aidl_start_pos = _aidl_parcel.dataPosition();
            _aidl_parcel.writeInt(0);
            _aidl_parcel.writeInt(this.mode);
            _aidl_parcel.writeInt(this.recurrence);
            _aidl_parcel.writeInt(this.minIntervalMs);
            _aidl_parcel.writeInt(this.preferredAccuracyMeters);
            _aidl_parcel.writeInt(this.preferredTimeMs);
            _aidl_parcel.writeBoolean(this.lowPowerMode);
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
                this.mode = _aidl_parcel.readInt();
                if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                    if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                        throw new BadParcelableException("Overflow in the size of parcelable");
                    }
                    _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                    return;
                }
                this.recurrence = _aidl_parcel.readInt();
                if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                    if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                        throw new BadParcelableException("Overflow in the size of parcelable");
                    }
                    _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                    return;
                }
                this.minIntervalMs = _aidl_parcel.readInt();
                if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                    if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                        throw new BadParcelableException("Overflow in the size of parcelable");
                    }
                    _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                    return;
                }
                this.preferredAccuracyMeters = _aidl_parcel.readInt();
                if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                    if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                        throw new BadParcelableException("Overflow in the size of parcelable");
                    }
                    _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                    return;
                }
                this.preferredTimeMs = _aidl_parcel.readInt();
                if (_aidl_parcel.dataPosition() - _aidl_start_pos >= _aidl_parcelable_size) {
                    if (_aidl_start_pos > Integer.MAX_VALUE - _aidl_parcelable_size) {
                        throw new BadParcelableException("Overflow in the size of parcelable");
                    }
                    _aidl_parcel.setDataPosition(_aidl_start_pos + _aidl_parcelable_size);
                    return;
                }
                this.lowPowerMode = _aidl_parcel.readBoolean();
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
