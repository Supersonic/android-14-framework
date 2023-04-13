package android.hardware.radio.modem;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes2.dex */
public interface IRadioModemIndication extends IInterface {
    public static final String DESCRIPTOR = "android$hardware$radio$modem$IRadioModemIndication".replace('$', '.');
    public static final String HASH = "notfrozen";
    public static final int VERSION = 2;

    String getInterfaceHash() throws RemoteException;

    int getInterfaceVersion() throws RemoteException;

    void hardwareConfigChanged(int i, HardwareConfig[] hardwareConfigArr) throws RemoteException;

    void modemReset(int i, String str) throws RemoteException;

    void radioCapabilityIndication(int i, RadioCapability radioCapability) throws RemoteException;

    void radioStateChanged(int i, int i2) throws RemoteException;

    void rilConnected(int i) throws RemoteException;

    /* loaded from: classes2.dex */
    public static class Default implements IRadioModemIndication {
        @Override // android.hardware.radio.modem.IRadioModemIndication
        public void hardwareConfigChanged(int type, HardwareConfig[] configs) throws RemoteException {
        }

        @Override // android.hardware.radio.modem.IRadioModemIndication
        public void modemReset(int type, String reason) throws RemoteException {
        }

        @Override // android.hardware.radio.modem.IRadioModemIndication
        public void radioCapabilityIndication(int type, RadioCapability rc) throws RemoteException {
        }

        @Override // android.hardware.radio.modem.IRadioModemIndication
        public void radioStateChanged(int type, int radioState) throws RemoteException {
        }

        @Override // android.hardware.radio.modem.IRadioModemIndication
        public void rilConnected(int type) throws RemoteException {
        }

        @Override // android.hardware.radio.modem.IRadioModemIndication
        public int getInterfaceVersion() {
            return 0;
        }

        @Override // android.hardware.radio.modem.IRadioModemIndication
        public String getInterfaceHash() {
            return "";
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements IRadioModemIndication {
        static final int TRANSACTION_getInterfaceHash = 16777214;
        static final int TRANSACTION_getInterfaceVersion = 16777215;
        static final int TRANSACTION_hardwareConfigChanged = 1;
        static final int TRANSACTION_modemReset = 2;
        static final int TRANSACTION_radioCapabilityIndication = 3;
        static final int TRANSACTION_radioStateChanged = 4;
        static final int TRANSACTION_rilConnected = 5;

        public Stub() {
            markVintfStability();
            attachInterface(this, DESCRIPTOR);
        }

        public static IRadioModemIndication asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IRadioModemIndication)) {
                return (IRadioModemIndication) iin;
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
                            HardwareConfig[] _arg1 = (HardwareConfig[]) data.createTypedArray(HardwareConfig.CREATOR);
                            data.enforceNoDataAvail();
                            hardwareConfigChanged(_arg0, _arg1);
                            break;
                        case 2:
                            int _arg02 = data.readInt();
                            String _arg12 = data.readString();
                            data.enforceNoDataAvail();
                            modemReset(_arg02, _arg12);
                            break;
                        case 3:
                            int _arg03 = data.readInt();
                            RadioCapability _arg13 = (RadioCapability) data.readTypedObject(RadioCapability.CREATOR);
                            data.enforceNoDataAvail();
                            radioCapabilityIndication(_arg03, _arg13);
                            break;
                        case 4:
                            int _arg04 = data.readInt();
                            int _arg14 = data.readInt();
                            data.enforceNoDataAvail();
                            radioStateChanged(_arg04, _arg14);
                            break;
                        case 5:
                            int _arg05 = data.readInt();
                            data.enforceNoDataAvail();
                            rilConnected(_arg05);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes2.dex */
        private static class Proxy implements IRadioModemIndication {
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

            @Override // android.hardware.radio.modem.IRadioModemIndication
            public void hardwareConfigChanged(int type, HardwareConfig[] configs) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(type);
                    _data.writeTypedArray(configs, 0);
                    boolean _status = this.mRemote.transact(1, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method hardwareConfigChanged is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.modem.IRadioModemIndication
            public void modemReset(int type, String reason) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(type);
                    _data.writeString(reason);
                    boolean _status = this.mRemote.transact(2, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method modemReset is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.modem.IRadioModemIndication
            public void radioCapabilityIndication(int type, RadioCapability rc) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(type);
                    _data.writeTypedObject(rc, 0);
                    boolean _status = this.mRemote.transact(3, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method radioCapabilityIndication is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.modem.IRadioModemIndication
            public void radioStateChanged(int type, int radioState) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(type);
                    _data.writeInt(radioState);
                    boolean _status = this.mRemote.transact(4, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method radioStateChanged is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.modem.IRadioModemIndication
            public void rilConnected(int type) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(type);
                    boolean _status = this.mRemote.transact(5, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method rilConnected is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.modem.IRadioModemIndication
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

            @Override // android.hardware.radio.modem.IRadioModemIndication
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
