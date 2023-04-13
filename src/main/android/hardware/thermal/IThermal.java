package android.hardware.thermal;

import android.hardware.thermal.IThermalChangedCallback;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes2.dex */
public interface IThermal extends IInterface {
    public static final String DESCRIPTOR = "android$hardware$thermal$IThermal".replace('$', '.');
    public static final String HASH = "notfrozen";
    public static final int VERSION = 1;

    CoolingDevice[] getCoolingDevices() throws RemoteException;

    CoolingDevice[] getCoolingDevicesWithType(int i) throws RemoteException;

    String getInterfaceHash() throws RemoteException;

    int getInterfaceVersion() throws RemoteException;

    TemperatureThreshold[] getTemperatureThresholds() throws RemoteException;

    TemperatureThreshold[] getTemperatureThresholdsWithType(int i) throws RemoteException;

    Temperature[] getTemperatures() throws RemoteException;

    Temperature[] getTemperaturesWithType(int i) throws RemoteException;

    void registerThermalChangedCallback(IThermalChangedCallback iThermalChangedCallback) throws RemoteException;

    void registerThermalChangedCallbackWithType(IThermalChangedCallback iThermalChangedCallback, int i) throws RemoteException;

    void unregisterThermalChangedCallback(IThermalChangedCallback iThermalChangedCallback) throws RemoteException;

    /* loaded from: classes2.dex */
    public static class Default implements IThermal {
        @Override // android.hardware.thermal.IThermal
        public CoolingDevice[] getCoolingDevices() throws RemoteException {
            return null;
        }

        @Override // android.hardware.thermal.IThermal
        public CoolingDevice[] getCoolingDevicesWithType(int type) throws RemoteException {
            return null;
        }

        @Override // android.hardware.thermal.IThermal
        public Temperature[] getTemperatures() throws RemoteException {
            return null;
        }

        @Override // android.hardware.thermal.IThermal
        public Temperature[] getTemperaturesWithType(int type) throws RemoteException {
            return null;
        }

        @Override // android.hardware.thermal.IThermal
        public TemperatureThreshold[] getTemperatureThresholds() throws RemoteException {
            return null;
        }

        @Override // android.hardware.thermal.IThermal
        public TemperatureThreshold[] getTemperatureThresholdsWithType(int type) throws RemoteException {
            return null;
        }

        @Override // android.hardware.thermal.IThermal
        public void registerThermalChangedCallback(IThermalChangedCallback callback) throws RemoteException {
        }

        @Override // android.hardware.thermal.IThermal
        public void registerThermalChangedCallbackWithType(IThermalChangedCallback callback, int type) throws RemoteException {
        }

        @Override // android.hardware.thermal.IThermal
        public void unregisterThermalChangedCallback(IThermalChangedCallback callback) throws RemoteException {
        }

        @Override // android.hardware.thermal.IThermal
        public int getInterfaceVersion() {
            return 0;
        }

        @Override // android.hardware.thermal.IThermal
        public String getInterfaceHash() {
            return "";
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements IThermal {
        static final int TRANSACTION_getCoolingDevices = 1;
        static final int TRANSACTION_getCoolingDevicesWithType = 2;
        static final int TRANSACTION_getInterfaceHash = 16777214;
        static final int TRANSACTION_getInterfaceVersion = 16777215;
        static final int TRANSACTION_getTemperatureThresholds = 5;
        static final int TRANSACTION_getTemperatureThresholdsWithType = 6;
        static final int TRANSACTION_getTemperatures = 3;
        static final int TRANSACTION_getTemperaturesWithType = 4;
        static final int TRANSACTION_registerThermalChangedCallback = 7;
        static final int TRANSACTION_registerThermalChangedCallbackWithType = 8;
        static final int TRANSACTION_unregisterThermalChangedCallback = 9;

        public Stub() {
            markVintfStability();
            attachInterface(this, DESCRIPTOR);
        }

        public static IThermal asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IThermal)) {
                return (IThermal) iin;
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
                    return "getCoolingDevices";
                case 2:
                    return "getCoolingDevicesWithType";
                case 3:
                    return "getTemperatures";
                case 4:
                    return "getTemperaturesWithType";
                case 5:
                    return "getTemperatureThresholds";
                case 6:
                    return "getTemperatureThresholdsWithType";
                case 7:
                    return "registerThermalChangedCallback";
                case 8:
                    return "registerThermalChangedCallbackWithType";
                case 9:
                    return "unregisterThermalChangedCallback";
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
                            CoolingDevice[] _result = getCoolingDevices();
                            reply.writeNoException();
                            reply.writeTypedArray(_result, 1);
                            break;
                        case 2:
                            int _arg0 = data.readInt();
                            data.enforceNoDataAvail();
                            CoolingDevice[] _result2 = getCoolingDevicesWithType(_arg0);
                            reply.writeNoException();
                            reply.writeTypedArray(_result2, 1);
                            break;
                        case 3:
                            Temperature[] _result3 = getTemperatures();
                            reply.writeNoException();
                            reply.writeTypedArray(_result3, 1);
                            break;
                        case 4:
                            int _arg02 = data.readInt();
                            data.enforceNoDataAvail();
                            Temperature[] _result4 = getTemperaturesWithType(_arg02);
                            reply.writeNoException();
                            reply.writeTypedArray(_result4, 1);
                            break;
                        case 5:
                            TemperatureThreshold[] _result5 = getTemperatureThresholds();
                            reply.writeNoException();
                            reply.writeTypedArray(_result5, 1);
                            break;
                        case 6:
                            int _arg03 = data.readInt();
                            data.enforceNoDataAvail();
                            TemperatureThreshold[] _result6 = getTemperatureThresholdsWithType(_arg03);
                            reply.writeNoException();
                            reply.writeTypedArray(_result6, 1);
                            break;
                        case 7:
                            IThermalChangedCallback _arg04 = IThermalChangedCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            registerThermalChangedCallback(_arg04);
                            reply.writeNoException();
                            break;
                        case 8:
                            IThermalChangedCallback _arg05 = IThermalChangedCallback.Stub.asInterface(data.readStrongBinder());
                            int _arg1 = data.readInt();
                            data.enforceNoDataAvail();
                            registerThermalChangedCallbackWithType(_arg05, _arg1);
                            reply.writeNoException();
                            break;
                        case 9:
                            IThermalChangedCallback _arg06 = IThermalChangedCallback.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            unregisterThermalChangedCallback(_arg06);
                            reply.writeNoException();
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes2.dex */
        private static class Proxy implements IThermal {
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

            @Override // android.hardware.thermal.IThermal
            public CoolingDevice[] getCoolingDevices() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    boolean _status = this.mRemote.transact(1, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method getCoolingDevices is unimplemented.");
                    }
                    _reply.readException();
                    CoolingDevice[] _result = (CoolingDevice[]) _reply.createTypedArray(CoolingDevice.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.thermal.IThermal
            public CoolingDevice[] getCoolingDevicesWithType(int type) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(type);
                    boolean _status = this.mRemote.transact(2, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method getCoolingDevicesWithType is unimplemented.");
                    }
                    _reply.readException();
                    CoolingDevice[] _result = (CoolingDevice[]) _reply.createTypedArray(CoolingDevice.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.thermal.IThermal
            public Temperature[] getTemperatures() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    boolean _status = this.mRemote.transact(3, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method getTemperatures is unimplemented.");
                    }
                    _reply.readException();
                    Temperature[] _result = (Temperature[]) _reply.createTypedArray(Temperature.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.thermal.IThermal
            public Temperature[] getTemperaturesWithType(int type) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(type);
                    boolean _status = this.mRemote.transact(4, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method getTemperaturesWithType is unimplemented.");
                    }
                    _reply.readException();
                    Temperature[] _result = (Temperature[]) _reply.createTypedArray(Temperature.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.thermal.IThermal
            public TemperatureThreshold[] getTemperatureThresholds() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    boolean _status = this.mRemote.transact(5, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method getTemperatureThresholds is unimplemented.");
                    }
                    _reply.readException();
                    TemperatureThreshold[] _result = (TemperatureThreshold[]) _reply.createTypedArray(TemperatureThreshold.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.thermal.IThermal
            public TemperatureThreshold[] getTemperatureThresholdsWithType(int type) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(type);
                    boolean _status = this.mRemote.transact(6, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method getTemperatureThresholdsWithType is unimplemented.");
                    }
                    _reply.readException();
                    TemperatureThreshold[] _result = (TemperatureThreshold[]) _reply.createTypedArray(TemperatureThreshold.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.thermal.IThermal
            public void registerThermalChangedCallback(IThermalChangedCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeStrongInterface(callback);
                    boolean _status = this.mRemote.transact(7, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method registerThermalChangedCallback is unimplemented.");
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.thermal.IThermal
            public void registerThermalChangedCallbackWithType(IThermalChangedCallback callback, int type) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeStrongInterface(callback);
                    _data.writeInt(type);
                    boolean _status = this.mRemote.transact(8, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method registerThermalChangedCallbackWithType is unimplemented.");
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.thermal.IThermal
            public void unregisterThermalChangedCallback(IThermalChangedCallback callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeStrongInterface(callback);
                    boolean _status = this.mRemote.transact(9, _data, _reply, 0);
                    if (!_status) {
                        throw new RemoteException("Method unregisterThermalChangedCallback is unimplemented.");
                    }
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.hardware.thermal.IThermal
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

            @Override // android.hardware.thermal.IThermal
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
