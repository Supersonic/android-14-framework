package android.hardware.radio.modem;

import android.hardware.radio.modem.IRadioModemIndication;
import android.hardware.radio.modem.IRadioModemResponse;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes2.dex */
public interface IRadioModem extends IInterface {
    public static final String DESCRIPTOR = "android$hardware$radio$modem$IRadioModem".replace('$', '.');
    public static final String HASH = "notfrozen";
    public static final int VERSION = 2;

    void enableModem(int i, boolean z) throws RemoteException;

    void getBasebandVersion(int i) throws RemoteException;

    @Deprecated
    void getDeviceIdentity(int i) throws RemoteException;

    void getHardwareConfig(int i) throws RemoteException;

    void getImei(int i) throws RemoteException;

    String getInterfaceHash() throws RemoteException;

    int getInterfaceVersion() throws RemoteException;

    void getModemActivityInfo(int i) throws RemoteException;

    void getModemStackStatus(int i) throws RemoteException;

    void getRadioCapability(int i) throws RemoteException;

    @Deprecated
    void nvReadItem(int i, int i2) throws RemoteException;

    void nvResetConfig(int i, int i2) throws RemoteException;

    @Deprecated
    void nvWriteCdmaPrl(int i, byte[] bArr) throws RemoteException;

    @Deprecated
    void nvWriteItem(int i, NvWriteItem nvWriteItem) throws RemoteException;

    void requestShutdown(int i) throws RemoteException;

    void responseAcknowledgement() throws RemoteException;

    void sendDeviceState(int i, int i2, boolean z) throws RemoteException;

    void setRadioCapability(int i, RadioCapability radioCapability) throws RemoteException;

    void setRadioPower(int i, boolean z, boolean z2, boolean z3) throws RemoteException;

    void setResponseFunctions(IRadioModemResponse iRadioModemResponse, IRadioModemIndication iRadioModemIndication) throws RemoteException;

    /* loaded from: classes2.dex */
    public static class Default implements IRadioModem {
        @Override // android.hardware.radio.modem.IRadioModem
        public void enableModem(int serial, boolean on) throws RemoteException {
        }

        @Override // android.hardware.radio.modem.IRadioModem
        public void getBasebandVersion(int serial) throws RemoteException {
        }

        @Override // android.hardware.radio.modem.IRadioModem
        public void getDeviceIdentity(int serial) throws RemoteException {
        }

        @Override // android.hardware.radio.modem.IRadioModem
        public void getHardwareConfig(int serial) throws RemoteException {
        }

        @Override // android.hardware.radio.modem.IRadioModem
        public void getModemActivityInfo(int serial) throws RemoteException {
        }

        @Override // android.hardware.radio.modem.IRadioModem
        public void getModemStackStatus(int serial) throws RemoteException {
        }

        @Override // android.hardware.radio.modem.IRadioModem
        public void getRadioCapability(int serial) throws RemoteException {
        }

        @Override // android.hardware.radio.modem.IRadioModem
        public void nvReadItem(int serial, int itemId) throws RemoteException {
        }

        @Override // android.hardware.radio.modem.IRadioModem
        public void nvResetConfig(int serial, int resetType) throws RemoteException {
        }

        @Override // android.hardware.radio.modem.IRadioModem
        public void nvWriteCdmaPrl(int serial, byte[] prl) throws RemoteException {
        }

        @Override // android.hardware.radio.modem.IRadioModem
        public void nvWriteItem(int serial, NvWriteItem item) throws RemoteException {
        }

        @Override // android.hardware.radio.modem.IRadioModem
        public void requestShutdown(int serial) throws RemoteException {
        }

        @Override // android.hardware.radio.modem.IRadioModem
        public void responseAcknowledgement() throws RemoteException {
        }

        @Override // android.hardware.radio.modem.IRadioModem
        public void sendDeviceState(int serial, int deviceStateType, boolean state) throws RemoteException {
        }

        @Override // android.hardware.radio.modem.IRadioModem
        public void setRadioCapability(int serial, RadioCapability rc) throws RemoteException {
        }

        @Override // android.hardware.radio.modem.IRadioModem
        public void setRadioPower(int serial, boolean powerOn, boolean forEmergencyCall, boolean preferredForEmergencyCall) throws RemoteException {
        }

        @Override // android.hardware.radio.modem.IRadioModem
        public void setResponseFunctions(IRadioModemResponse radioModemResponse, IRadioModemIndication radioModemIndication) throws RemoteException {
        }

        @Override // android.hardware.radio.modem.IRadioModem
        public void getImei(int serial) throws RemoteException {
        }

        @Override // android.hardware.radio.modem.IRadioModem
        public int getInterfaceVersion() {
            return 0;
        }

        @Override // android.hardware.radio.modem.IRadioModem
        public String getInterfaceHash() {
            return "";
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements IRadioModem {
        static final int TRANSACTION_enableModem = 1;
        static final int TRANSACTION_getBasebandVersion = 2;
        static final int TRANSACTION_getDeviceIdentity = 3;
        static final int TRANSACTION_getHardwareConfig = 4;
        static final int TRANSACTION_getImei = 18;
        static final int TRANSACTION_getInterfaceHash = 16777214;
        static final int TRANSACTION_getInterfaceVersion = 16777215;
        static final int TRANSACTION_getModemActivityInfo = 5;
        static final int TRANSACTION_getModemStackStatus = 6;
        static final int TRANSACTION_getRadioCapability = 7;
        static final int TRANSACTION_nvReadItem = 8;
        static final int TRANSACTION_nvResetConfig = 9;
        static final int TRANSACTION_nvWriteCdmaPrl = 10;
        static final int TRANSACTION_nvWriteItem = 11;
        static final int TRANSACTION_requestShutdown = 12;
        static final int TRANSACTION_responseAcknowledgement = 13;
        static final int TRANSACTION_sendDeviceState = 14;
        static final int TRANSACTION_setRadioCapability = 15;
        static final int TRANSACTION_setRadioPower = 16;
        static final int TRANSACTION_setResponseFunctions = 17;

        public Stub() {
            markVintfStability();
            attachInterface(this, DESCRIPTOR);
        }

        public static IRadioModem asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IRadioModem)) {
                return (IRadioModem) iin;
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
                            boolean _arg1 = data.readBoolean();
                            data.enforceNoDataAvail();
                            enableModem(_arg0, _arg1);
                            break;
                        case 2:
                            int _arg02 = data.readInt();
                            data.enforceNoDataAvail();
                            getBasebandVersion(_arg02);
                            break;
                        case 3:
                            int _arg03 = data.readInt();
                            data.enforceNoDataAvail();
                            getDeviceIdentity(_arg03);
                            break;
                        case 4:
                            int _arg04 = data.readInt();
                            data.enforceNoDataAvail();
                            getHardwareConfig(_arg04);
                            break;
                        case 5:
                            int _arg05 = data.readInt();
                            data.enforceNoDataAvail();
                            getModemActivityInfo(_arg05);
                            break;
                        case 6:
                            int _arg06 = data.readInt();
                            data.enforceNoDataAvail();
                            getModemStackStatus(_arg06);
                            break;
                        case 7:
                            int _arg07 = data.readInt();
                            data.enforceNoDataAvail();
                            getRadioCapability(_arg07);
                            break;
                        case 8:
                            int _arg08 = data.readInt();
                            int _arg12 = data.readInt();
                            data.enforceNoDataAvail();
                            nvReadItem(_arg08, _arg12);
                            break;
                        case 9:
                            int _arg09 = data.readInt();
                            int _arg13 = data.readInt();
                            data.enforceNoDataAvail();
                            nvResetConfig(_arg09, _arg13);
                            break;
                        case 10:
                            int _arg010 = data.readInt();
                            byte[] _arg14 = data.createByteArray();
                            data.enforceNoDataAvail();
                            nvWriteCdmaPrl(_arg010, _arg14);
                            break;
                        case 11:
                            int _arg011 = data.readInt();
                            NvWriteItem _arg15 = (NvWriteItem) data.readTypedObject(NvWriteItem.CREATOR);
                            data.enforceNoDataAvail();
                            nvWriteItem(_arg011, _arg15);
                            break;
                        case 12:
                            int _arg012 = data.readInt();
                            data.enforceNoDataAvail();
                            requestShutdown(_arg012);
                            break;
                        case 13:
                            responseAcknowledgement();
                            break;
                        case 14:
                            int _arg013 = data.readInt();
                            int _arg16 = data.readInt();
                            boolean _arg2 = data.readBoolean();
                            data.enforceNoDataAvail();
                            sendDeviceState(_arg013, _arg16, _arg2);
                            break;
                        case 15:
                            int _arg014 = data.readInt();
                            RadioCapability _arg17 = (RadioCapability) data.readTypedObject(RadioCapability.CREATOR);
                            data.enforceNoDataAvail();
                            setRadioCapability(_arg014, _arg17);
                            break;
                        case 16:
                            int _arg015 = data.readInt();
                            boolean _arg18 = data.readBoolean();
                            boolean _arg22 = data.readBoolean();
                            boolean _arg3 = data.readBoolean();
                            data.enforceNoDataAvail();
                            setRadioPower(_arg015, _arg18, _arg22, _arg3);
                            break;
                        case 17:
                            IRadioModemResponse _arg016 = IRadioModemResponse.Stub.asInterface(data.readStrongBinder());
                            IRadioModemIndication _arg19 = IRadioModemIndication.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            setResponseFunctions(_arg016, _arg19);
                            break;
                        case 18:
                            int _arg017 = data.readInt();
                            data.enforceNoDataAvail();
                            getImei(_arg017);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes2.dex */
        private static class Proxy implements IRadioModem {
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

            @Override // android.hardware.radio.modem.IRadioModem
            public void enableModem(int serial, boolean on) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    _data.writeBoolean(on);
                    boolean _status = this.mRemote.transact(1, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method enableModem is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.modem.IRadioModem
            public void getBasebandVersion(int serial) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    boolean _status = this.mRemote.transact(2, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method getBasebandVersion is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.modem.IRadioModem
            public void getDeviceIdentity(int serial) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    boolean _status = this.mRemote.transact(3, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method getDeviceIdentity is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.modem.IRadioModem
            public void getHardwareConfig(int serial) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    boolean _status = this.mRemote.transact(4, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method getHardwareConfig is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.modem.IRadioModem
            public void getModemActivityInfo(int serial) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    boolean _status = this.mRemote.transact(5, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method getModemActivityInfo is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.modem.IRadioModem
            public void getModemStackStatus(int serial) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    boolean _status = this.mRemote.transact(6, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method getModemStackStatus is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.modem.IRadioModem
            public void getRadioCapability(int serial) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    boolean _status = this.mRemote.transact(7, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method getRadioCapability is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.modem.IRadioModem
            public void nvReadItem(int serial, int itemId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    _data.writeInt(itemId);
                    boolean _status = this.mRemote.transact(8, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method nvReadItem is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.modem.IRadioModem
            public void nvResetConfig(int serial, int resetType) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    _data.writeInt(resetType);
                    boolean _status = this.mRemote.transact(9, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method nvResetConfig is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.modem.IRadioModem
            public void nvWriteCdmaPrl(int serial, byte[] prl) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    _data.writeByteArray(prl);
                    boolean _status = this.mRemote.transact(10, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method nvWriteCdmaPrl is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.modem.IRadioModem
            public void nvWriteItem(int serial, NvWriteItem item) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    _data.writeTypedObject(item, 0);
                    boolean _status = this.mRemote.transact(11, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method nvWriteItem is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.modem.IRadioModem
            public void requestShutdown(int serial) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    boolean _status = this.mRemote.transact(12, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method requestShutdown is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.modem.IRadioModem
            public void responseAcknowledgement() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    boolean _status = this.mRemote.transact(13, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method responseAcknowledgement is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.modem.IRadioModem
            public void sendDeviceState(int serial, int deviceStateType, boolean state) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    _data.writeInt(deviceStateType);
                    _data.writeBoolean(state);
                    boolean _status = this.mRemote.transact(14, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method sendDeviceState is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.modem.IRadioModem
            public void setRadioCapability(int serial, RadioCapability rc) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    _data.writeTypedObject(rc, 0);
                    boolean _status = this.mRemote.transact(15, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method setRadioCapability is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.modem.IRadioModem
            public void setRadioPower(int serial, boolean powerOn, boolean forEmergencyCall, boolean preferredForEmergencyCall) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    _data.writeBoolean(powerOn);
                    _data.writeBoolean(forEmergencyCall);
                    _data.writeBoolean(preferredForEmergencyCall);
                    boolean _status = this.mRemote.transact(16, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method setRadioPower is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.modem.IRadioModem
            public void setResponseFunctions(IRadioModemResponse radioModemResponse, IRadioModemIndication radioModemIndication) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeStrongInterface(radioModemResponse);
                    _data.writeStrongInterface(radioModemIndication);
                    boolean _status = this.mRemote.transact(17, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method setResponseFunctions is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.modem.IRadioModem
            public void getImei(int serial) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    boolean _status = this.mRemote.transact(18, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method getImei is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.modem.IRadioModem
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

            @Override // android.hardware.radio.modem.IRadioModem
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
