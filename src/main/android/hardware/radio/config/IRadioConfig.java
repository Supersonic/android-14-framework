package android.hardware.radio.config;

import android.hardware.radio.config.IRadioConfigIndication;
import android.hardware.radio.config.IRadioConfigResponse;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes2.dex */
public interface IRadioConfig extends IInterface {
    public static final String DESCRIPTOR = "android$hardware$radio$config$IRadioConfig".replace('$', '.');
    public static final String HASH = "notfrozen";
    public static final int VERSION = 2;

    void getHalDeviceCapabilities(int i) throws RemoteException;

    String getInterfaceHash() throws RemoteException;

    int getInterfaceVersion() throws RemoteException;

    void getNumOfLiveModems(int i) throws RemoteException;

    void getPhoneCapability(int i) throws RemoteException;

    void getSimSlotsStatus(int i) throws RemoteException;

    void setNumOfLiveModems(int i, byte b) throws RemoteException;

    void setPreferredDataModem(int i, byte b) throws RemoteException;

    void setResponseFunctions(IRadioConfigResponse iRadioConfigResponse, IRadioConfigIndication iRadioConfigIndication) throws RemoteException;

    void setSimSlotsMapping(int i, SlotPortMapping[] slotPortMappingArr) throws RemoteException;

    /* loaded from: classes2.dex */
    public static class Default implements IRadioConfig {
        @Override // android.hardware.radio.config.IRadioConfig
        public void getHalDeviceCapabilities(int serial) throws RemoteException {
        }

        @Override // android.hardware.radio.config.IRadioConfig
        public void getNumOfLiveModems(int serial) throws RemoteException {
        }

        @Override // android.hardware.radio.config.IRadioConfig
        public void getPhoneCapability(int serial) throws RemoteException {
        }

        @Override // android.hardware.radio.config.IRadioConfig
        public void getSimSlotsStatus(int serial) throws RemoteException {
        }

        @Override // android.hardware.radio.config.IRadioConfig
        public void setNumOfLiveModems(int serial, byte numOfLiveModems) throws RemoteException {
        }

        @Override // android.hardware.radio.config.IRadioConfig
        public void setPreferredDataModem(int serial, byte modemId) throws RemoteException {
        }

        @Override // android.hardware.radio.config.IRadioConfig
        public void setResponseFunctions(IRadioConfigResponse radioConfigResponse, IRadioConfigIndication radioConfigIndication) throws RemoteException {
        }

        @Override // android.hardware.radio.config.IRadioConfig
        public void setSimSlotsMapping(int serial, SlotPortMapping[] slotMap) throws RemoteException {
        }

        @Override // android.hardware.radio.config.IRadioConfig
        public int getInterfaceVersion() {
            return 0;
        }

        @Override // android.hardware.radio.config.IRadioConfig
        public String getInterfaceHash() {
            return "";
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements IRadioConfig {
        static final int TRANSACTION_getHalDeviceCapabilities = 1;
        static final int TRANSACTION_getInterfaceHash = 16777214;
        static final int TRANSACTION_getInterfaceVersion = 16777215;
        static final int TRANSACTION_getNumOfLiveModems = 2;
        static final int TRANSACTION_getPhoneCapability = 3;
        static final int TRANSACTION_getSimSlotsStatus = 4;
        static final int TRANSACTION_setNumOfLiveModems = 5;
        static final int TRANSACTION_setPreferredDataModem = 6;
        static final int TRANSACTION_setResponseFunctions = 7;
        static final int TRANSACTION_setSimSlotsMapping = 8;

        public Stub() {
            markVintfStability();
            attachInterface(this, DESCRIPTOR);
        }

        public static IRadioConfig asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IRadioConfig)) {
                return (IRadioConfig) iin;
            }
            return new Proxy(obj);
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return this;
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
                            getHalDeviceCapabilities(_arg0);
                            break;
                        case 2:
                            int _arg02 = data.readInt();
                            data.enforceNoDataAvail();
                            getNumOfLiveModems(_arg02);
                            break;
                        case 3:
                            int _arg03 = data.readInt();
                            data.enforceNoDataAvail();
                            getPhoneCapability(_arg03);
                            break;
                        case 4:
                            int _arg04 = data.readInt();
                            data.enforceNoDataAvail();
                            getSimSlotsStatus(_arg04);
                            break;
                        case 5:
                            int _arg05 = data.readInt();
                            byte _arg1 = data.readByte();
                            data.enforceNoDataAvail();
                            setNumOfLiveModems(_arg05, _arg1);
                            break;
                        case 6:
                            int _arg06 = data.readInt();
                            byte _arg12 = data.readByte();
                            data.enforceNoDataAvail();
                            setPreferredDataModem(_arg06, _arg12);
                            break;
                        case 7:
                            IRadioConfigResponse _arg07 = IRadioConfigResponse.Stub.asInterface(data.readStrongBinder());
                            IRadioConfigIndication _arg13 = IRadioConfigIndication.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            setResponseFunctions(_arg07, _arg13);
                            break;
                        case 8:
                            int _arg08 = data.readInt();
                            SlotPortMapping[] _arg14 = (SlotPortMapping[]) data.createTypedArray(SlotPortMapping.CREATOR);
                            data.enforceNoDataAvail();
                            setSimSlotsMapping(_arg08, _arg14);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes2.dex */
        private static class Proxy implements IRadioConfig {
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

            @Override // android.hardware.radio.config.IRadioConfig
            public void getHalDeviceCapabilities(int serial) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    boolean _status = this.mRemote.transact(1, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method getHalDeviceCapabilities is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.config.IRadioConfig
            public void getNumOfLiveModems(int serial) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    boolean _status = this.mRemote.transact(2, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method getNumOfLiveModems is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.config.IRadioConfig
            public void getPhoneCapability(int serial) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    boolean _status = this.mRemote.transact(3, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method getPhoneCapability is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.config.IRadioConfig
            public void getSimSlotsStatus(int serial) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    boolean _status = this.mRemote.transact(4, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method getSimSlotsStatus is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.config.IRadioConfig
            public void setNumOfLiveModems(int serial, byte numOfLiveModems) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    _data.writeByte(numOfLiveModems);
                    boolean _status = this.mRemote.transact(5, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method setNumOfLiveModems is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.config.IRadioConfig
            public void setPreferredDataModem(int serial, byte modemId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    _data.writeByte(modemId);
                    boolean _status = this.mRemote.transact(6, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method setPreferredDataModem is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.config.IRadioConfig
            public void setResponseFunctions(IRadioConfigResponse radioConfigResponse, IRadioConfigIndication radioConfigIndication) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeStrongInterface(radioConfigResponse);
                    _data.writeStrongInterface(radioConfigIndication);
                    boolean _status = this.mRemote.transact(7, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method setResponseFunctions is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.config.IRadioConfig
            public void setSimSlotsMapping(int serial, SlotPortMapping[] slotMap) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    _data.writeTypedArray(slotMap, 0);
                    boolean _status = this.mRemote.transact(8, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method setSimSlotsMapping is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.config.IRadioConfig
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

            @Override // android.hardware.radio.config.IRadioConfig
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
    }
}
