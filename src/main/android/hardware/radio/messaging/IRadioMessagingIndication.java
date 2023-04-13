package android.hardware.radio.messaging;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes2.dex */
public interface IRadioMessagingIndication extends IInterface {
    public static final String DESCRIPTOR = "android$hardware$radio$messaging$IRadioMessagingIndication".replace('$', '.');
    public static final String HASH = "notfrozen";
    public static final int VERSION = 2;

    void cdmaNewSms(int i, CdmaSmsMessage cdmaSmsMessage) throws RemoteException;

    void cdmaRuimSmsStorageFull(int i) throws RemoteException;

    String getInterfaceHash() throws RemoteException;

    int getInterfaceVersion() throws RemoteException;

    void newBroadcastSms(int i, byte[] bArr) throws RemoteException;

    void newSms(int i, byte[] bArr) throws RemoteException;

    void newSmsOnSim(int i, int i2) throws RemoteException;

    void newSmsStatusReport(int i, byte[] bArr) throws RemoteException;

    void simSmsStorageFull(int i) throws RemoteException;

    /* loaded from: classes2.dex */
    public static class Default implements IRadioMessagingIndication {
        @Override // android.hardware.radio.messaging.IRadioMessagingIndication
        public void cdmaNewSms(int type, CdmaSmsMessage msg) throws RemoteException {
        }

        @Override // android.hardware.radio.messaging.IRadioMessagingIndication
        public void cdmaRuimSmsStorageFull(int type) throws RemoteException {
        }

        @Override // android.hardware.radio.messaging.IRadioMessagingIndication
        public void newBroadcastSms(int type, byte[] data) throws RemoteException {
        }

        @Override // android.hardware.radio.messaging.IRadioMessagingIndication
        public void newSms(int type, byte[] pdu) throws RemoteException {
        }

        @Override // android.hardware.radio.messaging.IRadioMessagingIndication
        public void newSmsOnSim(int type, int recordNumber) throws RemoteException {
        }

        @Override // android.hardware.radio.messaging.IRadioMessagingIndication
        public void newSmsStatusReport(int type, byte[] pdu) throws RemoteException {
        }

        @Override // android.hardware.radio.messaging.IRadioMessagingIndication
        public void simSmsStorageFull(int type) throws RemoteException {
        }

        @Override // android.hardware.radio.messaging.IRadioMessagingIndication
        public int getInterfaceVersion() {
            return 0;
        }

        @Override // android.hardware.radio.messaging.IRadioMessagingIndication
        public String getInterfaceHash() {
            return "";
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements IRadioMessagingIndication {
        static final int TRANSACTION_cdmaNewSms = 1;
        static final int TRANSACTION_cdmaRuimSmsStorageFull = 2;
        static final int TRANSACTION_getInterfaceHash = 16777214;
        static final int TRANSACTION_getInterfaceVersion = 16777215;
        static final int TRANSACTION_newBroadcastSms = 3;
        static final int TRANSACTION_newSms = 4;
        static final int TRANSACTION_newSmsOnSim = 5;
        static final int TRANSACTION_newSmsStatusReport = 6;
        static final int TRANSACTION_simSmsStorageFull = 7;

        public Stub() {
            markVintfStability();
            attachInterface(this, DESCRIPTOR);
        }

        public static IRadioMessagingIndication asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IRadioMessagingIndication)) {
                return (IRadioMessagingIndication) iin;
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
                            CdmaSmsMessage _arg1 = (CdmaSmsMessage) data.readTypedObject(CdmaSmsMessage.CREATOR);
                            data.enforceNoDataAvail();
                            cdmaNewSms(_arg0, _arg1);
                            break;
                        case 2:
                            int _arg02 = data.readInt();
                            data.enforceNoDataAvail();
                            cdmaRuimSmsStorageFull(_arg02);
                            break;
                        case 3:
                            int _arg03 = data.readInt();
                            byte[] _arg12 = data.createByteArray();
                            data.enforceNoDataAvail();
                            newBroadcastSms(_arg03, _arg12);
                            break;
                        case 4:
                            int _arg04 = data.readInt();
                            byte[] _arg13 = data.createByteArray();
                            data.enforceNoDataAvail();
                            newSms(_arg04, _arg13);
                            break;
                        case 5:
                            int _arg05 = data.readInt();
                            int _arg14 = data.readInt();
                            data.enforceNoDataAvail();
                            newSmsOnSim(_arg05, _arg14);
                            break;
                        case 6:
                            int _arg06 = data.readInt();
                            byte[] _arg15 = data.createByteArray();
                            data.enforceNoDataAvail();
                            newSmsStatusReport(_arg06, _arg15);
                            break;
                        case 7:
                            int _arg07 = data.readInt();
                            data.enforceNoDataAvail();
                            simSmsStorageFull(_arg07);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes2.dex */
        private static class Proxy implements IRadioMessagingIndication {
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

            @Override // android.hardware.radio.messaging.IRadioMessagingIndication
            public void cdmaNewSms(int type, CdmaSmsMessage msg) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(type);
                    _data.writeTypedObject(msg, 0);
                    boolean _status = this.mRemote.transact(1, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method cdmaNewSms is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.messaging.IRadioMessagingIndication
            public void cdmaRuimSmsStorageFull(int type) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(type);
                    boolean _status = this.mRemote.transact(2, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method cdmaRuimSmsStorageFull is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.messaging.IRadioMessagingIndication
            public void newBroadcastSms(int type, byte[] data) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(type);
                    _data.writeByteArray(data);
                    boolean _status = this.mRemote.transact(3, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method newBroadcastSms is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.messaging.IRadioMessagingIndication
            public void newSms(int type, byte[] pdu) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(type);
                    _data.writeByteArray(pdu);
                    boolean _status = this.mRemote.transact(4, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method newSms is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.messaging.IRadioMessagingIndication
            public void newSmsOnSim(int type, int recordNumber) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(type);
                    _data.writeInt(recordNumber);
                    boolean _status = this.mRemote.transact(5, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method newSmsOnSim is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.messaging.IRadioMessagingIndication
            public void newSmsStatusReport(int type, byte[] pdu) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(type);
                    _data.writeByteArray(pdu);
                    boolean _status = this.mRemote.transact(6, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method newSmsStatusReport is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.messaging.IRadioMessagingIndication
            public void simSmsStorageFull(int type) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(type);
                    boolean _status = this.mRemote.transact(7, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method simSmsStorageFull is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.messaging.IRadioMessagingIndication
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

            @Override // android.hardware.radio.messaging.IRadioMessagingIndication
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
