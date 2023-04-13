package android.p008os;

import android.p008os.IThermalEventListener;
import android.p008os.IThermalStatusListener;
/* renamed from: android.os.IThermalService */
/* loaded from: classes3.dex */
public interface IThermalService extends IInterface {
    CoolingDevice[] getCurrentCoolingDevices() throws RemoteException;

    CoolingDevice[] getCurrentCoolingDevicesWithType(int i) throws RemoteException;

    Temperature[] getCurrentTemperatures() throws RemoteException;

    Temperature[] getCurrentTemperaturesWithType(int i) throws RemoteException;

    int getCurrentThermalStatus() throws RemoteException;

    float getThermalHeadroom(int i) throws RemoteException;

    boolean registerThermalEventListener(IThermalEventListener iThermalEventListener) throws RemoteException;

    boolean registerThermalEventListenerWithType(IThermalEventListener iThermalEventListener, int i) throws RemoteException;

    boolean registerThermalStatusListener(IThermalStatusListener iThermalStatusListener) throws RemoteException;

    boolean unregisterThermalEventListener(IThermalEventListener iThermalEventListener) throws RemoteException;

    boolean unregisterThermalStatusListener(IThermalStatusListener iThermalStatusListener) throws RemoteException;

    /* renamed from: android.os.IThermalService$Default */
    /* loaded from: classes3.dex */
    public static class Default implements IThermalService {
        @Override // android.p008os.IThermalService
        public boolean registerThermalEventListener(IThermalEventListener listener) throws RemoteException {
            return false;
        }

        @Override // android.p008os.IThermalService
        public boolean registerThermalEventListenerWithType(IThermalEventListener listener, int type) throws RemoteException {
            return false;
        }

        @Override // android.p008os.IThermalService
        public boolean unregisterThermalEventListener(IThermalEventListener listener) throws RemoteException {
            return false;
        }

        @Override // android.p008os.IThermalService
        public Temperature[] getCurrentTemperatures() throws RemoteException {
            return null;
        }

        @Override // android.p008os.IThermalService
        public Temperature[] getCurrentTemperaturesWithType(int type) throws RemoteException {
            return null;
        }

        @Override // android.p008os.IThermalService
        public boolean registerThermalStatusListener(IThermalStatusListener listener) throws RemoteException {
            return false;
        }

        @Override // android.p008os.IThermalService
        public boolean unregisterThermalStatusListener(IThermalStatusListener listener) throws RemoteException {
            return false;
        }

        @Override // android.p008os.IThermalService
        public int getCurrentThermalStatus() throws RemoteException {
            return 0;
        }

        @Override // android.p008os.IThermalService
        public CoolingDevice[] getCurrentCoolingDevices() throws RemoteException {
            return null;
        }

        @Override // android.p008os.IThermalService
        public CoolingDevice[] getCurrentCoolingDevicesWithType(int type) throws RemoteException {
            return null;
        }

        @Override // android.p008os.IThermalService
        public float getThermalHeadroom(int forecastSeconds) throws RemoteException {
            return 0.0f;
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* renamed from: android.os.IThermalService$Stub */
    /* loaded from: classes3.dex */
    public static abstract class Stub extends Binder implements IThermalService {
        public static final String DESCRIPTOR = "android.os.IThermalService";
        static final int TRANSACTION_getCurrentCoolingDevices = 9;
        static final int TRANSACTION_getCurrentCoolingDevicesWithType = 10;
        static final int TRANSACTION_getCurrentTemperatures = 4;
        static final int TRANSACTION_getCurrentTemperaturesWithType = 5;
        static final int TRANSACTION_getCurrentThermalStatus = 8;
        static final int TRANSACTION_getThermalHeadroom = 11;
        static final int TRANSACTION_registerThermalEventListener = 1;
        static final int TRANSACTION_registerThermalEventListenerWithType = 2;
        static final int TRANSACTION_registerThermalStatusListener = 6;
        static final int TRANSACTION_unregisterThermalEventListener = 3;
        static final int TRANSACTION_unregisterThermalStatusListener = 7;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IThermalService asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IThermalService)) {
                return (IThermalService) iin;
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
                    return "registerThermalEventListener";
                case 2:
                    return "registerThermalEventListenerWithType";
                case 3:
                    return "unregisterThermalEventListener";
                case 4:
                    return "getCurrentTemperatures";
                case 5:
                    return "getCurrentTemperaturesWithType";
                case 6:
                    return "registerThermalStatusListener";
                case 7:
                    return "unregisterThermalStatusListener";
                case 8:
                    return "getCurrentThermalStatus";
                case 9:
                    return "getCurrentCoolingDevices";
                case 10:
                    return "getCurrentCoolingDevicesWithType";
                case 11:
                    return "getThermalHeadroom";
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
                data.enforceInterface(DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            IThermalEventListener _arg0 = IThermalEventListener.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            boolean _result = registerThermalEventListener(_arg0);
                            reply.writeNoException();
                            reply.writeBoolean(_result);
                            break;
                        case 2:
                            IThermalEventListener _arg02 = IThermalEventListener.Stub.asInterface(data.readStrongBinder());
                            int _arg1 = data.readInt();
                            data.enforceNoDataAvail();
                            boolean _result2 = registerThermalEventListenerWithType(_arg02, _arg1);
                            reply.writeNoException();
                            reply.writeBoolean(_result2);
                            break;
                        case 3:
                            IThermalEventListener _arg03 = IThermalEventListener.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            boolean _result3 = unregisterThermalEventListener(_arg03);
                            reply.writeNoException();
                            reply.writeBoolean(_result3);
                            break;
                        case 4:
                            Temperature[] _result4 = getCurrentTemperatures();
                            reply.writeNoException();
                            reply.writeTypedArray(_result4, 1);
                            break;
                        case 5:
                            int _arg04 = data.readInt();
                            data.enforceNoDataAvail();
                            Temperature[] _result5 = getCurrentTemperaturesWithType(_arg04);
                            reply.writeNoException();
                            reply.writeTypedArray(_result5, 1);
                            break;
                        case 6:
                            IThermalStatusListener _arg05 = IThermalStatusListener.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            boolean _result6 = registerThermalStatusListener(_arg05);
                            reply.writeNoException();
                            reply.writeBoolean(_result6);
                            break;
                        case 7:
                            IThermalStatusListener _arg06 = IThermalStatusListener.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            boolean _result7 = unregisterThermalStatusListener(_arg06);
                            reply.writeNoException();
                            reply.writeBoolean(_result7);
                            break;
                        case 8:
                            int _result8 = getCurrentThermalStatus();
                            reply.writeNoException();
                            reply.writeInt(_result8);
                            break;
                        case 9:
                            CoolingDevice[] _result9 = getCurrentCoolingDevices();
                            reply.writeNoException();
                            reply.writeTypedArray(_result9, 1);
                            break;
                        case 10:
                            int _arg07 = data.readInt();
                            data.enforceNoDataAvail();
                            CoolingDevice[] _result10 = getCurrentCoolingDevicesWithType(_arg07);
                            reply.writeNoException();
                            reply.writeTypedArray(_result10, 1);
                            break;
                        case 11:
                            int _arg08 = data.readInt();
                            data.enforceNoDataAvail();
                            float _result11 = getThermalHeadroom(_arg08);
                            reply.writeNoException();
                            reply.writeFloat(_result11);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* renamed from: android.os.IThermalService$Stub$Proxy */
        /* loaded from: classes3.dex */
        private static class Proxy implements IThermalService {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return Stub.DESCRIPTOR;
            }

            @Override // android.p008os.IThermalService
            public boolean registerThermalEventListener(IThermalEventListener listener) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(listener);
                    this.mRemote.transact(1, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IThermalService
            public boolean registerThermalEventListenerWithType(IThermalEventListener listener, int type) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(listener);
                    _data.writeInt(type);
                    this.mRemote.transact(2, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IThermalService
            public boolean unregisterThermalEventListener(IThermalEventListener listener) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(listener);
                    this.mRemote.transact(3, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IThermalService
            public Temperature[] getCurrentTemperatures() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(4, _data, _reply, 0);
                    _reply.readException();
                    Temperature[] _result = (Temperature[]) _reply.createTypedArray(Temperature.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IThermalService
            public Temperature[] getCurrentTemperaturesWithType(int type) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(type);
                    this.mRemote.transact(5, _data, _reply, 0);
                    _reply.readException();
                    Temperature[] _result = (Temperature[]) _reply.createTypedArray(Temperature.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IThermalService
            public boolean registerThermalStatusListener(IThermalStatusListener listener) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(listener);
                    this.mRemote.transact(6, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IThermalService
            public boolean unregisterThermalStatusListener(IThermalStatusListener listener) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(listener);
                    this.mRemote.transact(7, _data, _reply, 0);
                    _reply.readException();
                    boolean _result = _reply.readBoolean();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IThermalService
            public int getCurrentThermalStatus() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(8, _data, _reply, 0);
                    _reply.readException();
                    int _result = _reply.readInt();
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IThermalService
            public CoolingDevice[] getCurrentCoolingDevices() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(9, _data, _reply, 0);
                    _reply.readException();
                    CoolingDevice[] _result = (CoolingDevice[]) _reply.createTypedArray(CoolingDevice.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IThermalService
            public CoolingDevice[] getCurrentCoolingDevicesWithType(int type) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(type);
                    this.mRemote.transact(10, _data, _reply, 0);
                    _reply.readException();
                    CoolingDevice[] _result = (CoolingDevice[]) _reply.createTypedArray(CoolingDevice.CREATOR);
                    return _result;
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            @Override // android.p008os.IThermalService
            public float getThermalHeadroom(int forecastSeconds) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(forecastSeconds);
                    this.mRemote.transact(11, _data, _reply, 0);
                    _reply.readException();
                    float _result = _reply.readFloat();
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
