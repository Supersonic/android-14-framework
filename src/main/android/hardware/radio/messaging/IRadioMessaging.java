package android.hardware.radio.messaging;

import android.hardware.radio.messaging.IRadioMessagingIndication;
import android.hardware.radio.messaging.IRadioMessagingResponse;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes2.dex */
public interface IRadioMessaging extends IInterface {
    public static final String DESCRIPTOR = "android$hardware$radio$messaging$IRadioMessaging".replace('$', '.');
    public static final String HASH = "notfrozen";
    public static final int VERSION = 2;

    void acknowledgeIncomingGsmSmsWithPdu(int i, boolean z, String str) throws RemoteException;

    void acknowledgeLastIncomingCdmaSms(int i, CdmaSmsAck cdmaSmsAck) throws RemoteException;

    void acknowledgeLastIncomingGsmSms(int i, boolean z, int i2) throws RemoteException;

    void deleteSmsOnRuim(int i, int i2) throws RemoteException;

    void deleteSmsOnSim(int i, int i2) throws RemoteException;

    void getCdmaBroadcastConfig(int i) throws RemoteException;

    void getGsmBroadcastConfig(int i) throws RemoteException;

    String getInterfaceHash() throws RemoteException;

    int getInterfaceVersion() throws RemoteException;

    void getSmscAddress(int i) throws RemoteException;

    void reportSmsMemoryStatus(int i, boolean z) throws RemoteException;

    void responseAcknowledgement() throws RemoteException;

    void sendCdmaSms(int i, CdmaSmsMessage cdmaSmsMessage) throws RemoteException;

    void sendCdmaSmsExpectMore(int i, CdmaSmsMessage cdmaSmsMessage) throws RemoteException;

    void sendImsSms(int i, ImsSmsMessage imsSmsMessage) throws RemoteException;

    void sendSms(int i, GsmSmsMessage gsmSmsMessage) throws RemoteException;

    void sendSmsExpectMore(int i, GsmSmsMessage gsmSmsMessage) throws RemoteException;

    void setCdmaBroadcastActivation(int i, boolean z) throws RemoteException;

    void setCdmaBroadcastConfig(int i, CdmaBroadcastSmsConfigInfo[] cdmaBroadcastSmsConfigInfoArr) throws RemoteException;

    void setGsmBroadcastActivation(int i, boolean z) throws RemoteException;

    void setGsmBroadcastConfig(int i, GsmBroadcastSmsConfigInfo[] gsmBroadcastSmsConfigInfoArr) throws RemoteException;

    void setResponseFunctions(IRadioMessagingResponse iRadioMessagingResponse, IRadioMessagingIndication iRadioMessagingIndication) throws RemoteException;

    void setSmscAddress(int i, String str) throws RemoteException;

    void writeSmsToRuim(int i, CdmaSmsWriteArgs cdmaSmsWriteArgs) throws RemoteException;

    void writeSmsToSim(int i, SmsWriteArgs smsWriteArgs) throws RemoteException;

    /* loaded from: classes2.dex */
    public static class Default implements IRadioMessaging {
        @Override // android.hardware.radio.messaging.IRadioMessaging
        public void acknowledgeIncomingGsmSmsWithPdu(int serial, boolean success, String ackPdu) throws RemoteException {
        }

        @Override // android.hardware.radio.messaging.IRadioMessaging
        public void acknowledgeLastIncomingCdmaSms(int serial, CdmaSmsAck smsAck) throws RemoteException {
        }

        @Override // android.hardware.radio.messaging.IRadioMessaging
        public void acknowledgeLastIncomingGsmSms(int serial, boolean success, int cause) throws RemoteException {
        }

        @Override // android.hardware.radio.messaging.IRadioMessaging
        public void deleteSmsOnRuim(int serial, int index) throws RemoteException {
        }

        @Override // android.hardware.radio.messaging.IRadioMessaging
        public void deleteSmsOnSim(int serial, int index) throws RemoteException {
        }

        @Override // android.hardware.radio.messaging.IRadioMessaging
        public void getCdmaBroadcastConfig(int serial) throws RemoteException {
        }

        @Override // android.hardware.radio.messaging.IRadioMessaging
        public void getGsmBroadcastConfig(int serial) throws RemoteException {
        }

        @Override // android.hardware.radio.messaging.IRadioMessaging
        public void getSmscAddress(int serial) throws RemoteException {
        }

        @Override // android.hardware.radio.messaging.IRadioMessaging
        public void reportSmsMemoryStatus(int serial, boolean available) throws RemoteException {
        }

        @Override // android.hardware.radio.messaging.IRadioMessaging
        public void responseAcknowledgement() throws RemoteException {
        }

        @Override // android.hardware.radio.messaging.IRadioMessaging
        public void sendCdmaSms(int serial, CdmaSmsMessage sms) throws RemoteException {
        }

        @Override // android.hardware.radio.messaging.IRadioMessaging
        public void sendCdmaSmsExpectMore(int serial, CdmaSmsMessage sms) throws RemoteException {
        }

        @Override // android.hardware.radio.messaging.IRadioMessaging
        public void sendImsSms(int serial, ImsSmsMessage message) throws RemoteException {
        }

        @Override // android.hardware.radio.messaging.IRadioMessaging
        public void sendSms(int serial, GsmSmsMessage message) throws RemoteException {
        }

        @Override // android.hardware.radio.messaging.IRadioMessaging
        public void sendSmsExpectMore(int serial, GsmSmsMessage message) throws RemoteException {
        }

        @Override // android.hardware.radio.messaging.IRadioMessaging
        public void setCdmaBroadcastActivation(int serial, boolean activate) throws RemoteException {
        }

        @Override // android.hardware.radio.messaging.IRadioMessaging
        public void setCdmaBroadcastConfig(int serial, CdmaBroadcastSmsConfigInfo[] configInfo) throws RemoteException {
        }

        @Override // android.hardware.radio.messaging.IRadioMessaging
        public void setGsmBroadcastActivation(int serial, boolean activate) throws RemoteException {
        }

        @Override // android.hardware.radio.messaging.IRadioMessaging
        public void setGsmBroadcastConfig(int serial, GsmBroadcastSmsConfigInfo[] configInfo) throws RemoteException {
        }

        @Override // android.hardware.radio.messaging.IRadioMessaging
        public void setResponseFunctions(IRadioMessagingResponse radioMessagingResponse, IRadioMessagingIndication radioMessagingIndication) throws RemoteException {
        }

        @Override // android.hardware.radio.messaging.IRadioMessaging
        public void setSmscAddress(int serial, String smsc) throws RemoteException {
        }

        @Override // android.hardware.radio.messaging.IRadioMessaging
        public void writeSmsToRuim(int serial, CdmaSmsWriteArgs cdmaSms) throws RemoteException {
        }

        @Override // android.hardware.radio.messaging.IRadioMessaging
        public void writeSmsToSim(int serial, SmsWriteArgs smsWriteArgs) throws RemoteException {
        }

        @Override // android.hardware.radio.messaging.IRadioMessaging
        public int getInterfaceVersion() {
            return 0;
        }

        @Override // android.hardware.radio.messaging.IRadioMessaging
        public String getInterfaceHash() {
            return "";
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements IRadioMessaging {
        static final int TRANSACTION_acknowledgeIncomingGsmSmsWithPdu = 1;
        static final int TRANSACTION_acknowledgeLastIncomingCdmaSms = 2;
        static final int TRANSACTION_acknowledgeLastIncomingGsmSms = 3;
        static final int TRANSACTION_deleteSmsOnRuim = 4;
        static final int TRANSACTION_deleteSmsOnSim = 5;
        static final int TRANSACTION_getCdmaBroadcastConfig = 6;
        static final int TRANSACTION_getGsmBroadcastConfig = 7;
        static final int TRANSACTION_getInterfaceHash = 16777214;
        static final int TRANSACTION_getInterfaceVersion = 16777215;
        static final int TRANSACTION_getSmscAddress = 8;
        static final int TRANSACTION_reportSmsMemoryStatus = 9;
        static final int TRANSACTION_responseAcknowledgement = 10;
        static final int TRANSACTION_sendCdmaSms = 11;
        static final int TRANSACTION_sendCdmaSmsExpectMore = 12;
        static final int TRANSACTION_sendImsSms = 13;
        static final int TRANSACTION_sendSms = 14;
        static final int TRANSACTION_sendSmsExpectMore = 15;
        static final int TRANSACTION_setCdmaBroadcastActivation = 16;
        static final int TRANSACTION_setCdmaBroadcastConfig = 17;
        static final int TRANSACTION_setGsmBroadcastActivation = 18;
        static final int TRANSACTION_setGsmBroadcastConfig = 19;
        static final int TRANSACTION_setResponseFunctions = 20;
        static final int TRANSACTION_setSmscAddress = 21;
        static final int TRANSACTION_writeSmsToRuim = 22;
        static final int TRANSACTION_writeSmsToSim = 23;

        public Stub() {
            markVintfStability();
            attachInterface(this, DESCRIPTOR);
        }

        public static IRadioMessaging asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IRadioMessaging)) {
                return (IRadioMessaging) iin;
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
                            String _arg2 = data.readString();
                            data.enforceNoDataAvail();
                            acknowledgeIncomingGsmSmsWithPdu(_arg0, _arg1, _arg2);
                            break;
                        case 2:
                            int _arg02 = data.readInt();
                            CdmaSmsAck _arg12 = (CdmaSmsAck) data.readTypedObject(CdmaSmsAck.CREATOR);
                            data.enforceNoDataAvail();
                            acknowledgeLastIncomingCdmaSms(_arg02, _arg12);
                            break;
                        case 3:
                            int _arg03 = data.readInt();
                            boolean _arg13 = data.readBoolean();
                            int _arg22 = data.readInt();
                            data.enforceNoDataAvail();
                            acknowledgeLastIncomingGsmSms(_arg03, _arg13, _arg22);
                            break;
                        case 4:
                            int _arg04 = data.readInt();
                            int _arg14 = data.readInt();
                            data.enforceNoDataAvail();
                            deleteSmsOnRuim(_arg04, _arg14);
                            break;
                        case 5:
                            int _arg05 = data.readInt();
                            int _arg15 = data.readInt();
                            data.enforceNoDataAvail();
                            deleteSmsOnSim(_arg05, _arg15);
                            break;
                        case 6:
                            int _arg06 = data.readInt();
                            data.enforceNoDataAvail();
                            getCdmaBroadcastConfig(_arg06);
                            break;
                        case 7:
                            int _arg07 = data.readInt();
                            data.enforceNoDataAvail();
                            getGsmBroadcastConfig(_arg07);
                            break;
                        case 8:
                            int _arg08 = data.readInt();
                            data.enforceNoDataAvail();
                            getSmscAddress(_arg08);
                            break;
                        case 9:
                            int _arg09 = data.readInt();
                            boolean _arg16 = data.readBoolean();
                            data.enforceNoDataAvail();
                            reportSmsMemoryStatus(_arg09, _arg16);
                            break;
                        case 10:
                            responseAcknowledgement();
                            break;
                        case 11:
                            int _arg010 = data.readInt();
                            CdmaSmsMessage _arg17 = (CdmaSmsMessage) data.readTypedObject(CdmaSmsMessage.CREATOR);
                            data.enforceNoDataAvail();
                            sendCdmaSms(_arg010, _arg17);
                            break;
                        case 12:
                            int _arg011 = data.readInt();
                            CdmaSmsMessage _arg18 = (CdmaSmsMessage) data.readTypedObject(CdmaSmsMessage.CREATOR);
                            data.enforceNoDataAvail();
                            sendCdmaSmsExpectMore(_arg011, _arg18);
                            break;
                        case 13:
                            int _arg012 = data.readInt();
                            ImsSmsMessage _arg19 = (ImsSmsMessage) data.readTypedObject(ImsSmsMessage.CREATOR);
                            data.enforceNoDataAvail();
                            sendImsSms(_arg012, _arg19);
                            break;
                        case 14:
                            int _arg013 = data.readInt();
                            GsmSmsMessage _arg110 = (GsmSmsMessage) data.readTypedObject(GsmSmsMessage.CREATOR);
                            data.enforceNoDataAvail();
                            sendSms(_arg013, _arg110);
                            break;
                        case 15:
                            int _arg014 = data.readInt();
                            GsmSmsMessage _arg111 = (GsmSmsMessage) data.readTypedObject(GsmSmsMessage.CREATOR);
                            data.enforceNoDataAvail();
                            sendSmsExpectMore(_arg014, _arg111);
                            break;
                        case 16:
                            int _arg015 = data.readInt();
                            boolean _arg112 = data.readBoolean();
                            data.enforceNoDataAvail();
                            setCdmaBroadcastActivation(_arg015, _arg112);
                            break;
                        case 17:
                            int _arg016 = data.readInt();
                            CdmaBroadcastSmsConfigInfo[] _arg113 = (CdmaBroadcastSmsConfigInfo[]) data.createTypedArray(CdmaBroadcastSmsConfigInfo.CREATOR);
                            data.enforceNoDataAvail();
                            setCdmaBroadcastConfig(_arg016, _arg113);
                            break;
                        case 18:
                            int _arg017 = data.readInt();
                            boolean _arg114 = data.readBoolean();
                            data.enforceNoDataAvail();
                            setGsmBroadcastActivation(_arg017, _arg114);
                            break;
                        case 19:
                            int _arg018 = data.readInt();
                            GsmBroadcastSmsConfigInfo[] _arg115 = (GsmBroadcastSmsConfigInfo[]) data.createTypedArray(GsmBroadcastSmsConfigInfo.CREATOR);
                            data.enforceNoDataAvail();
                            setGsmBroadcastConfig(_arg018, _arg115);
                            break;
                        case 20:
                            IRadioMessagingResponse _arg019 = IRadioMessagingResponse.Stub.asInterface(data.readStrongBinder());
                            IRadioMessagingIndication _arg116 = IRadioMessagingIndication.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            setResponseFunctions(_arg019, _arg116);
                            break;
                        case 21:
                            int _arg020 = data.readInt();
                            String _arg117 = data.readString();
                            data.enforceNoDataAvail();
                            setSmscAddress(_arg020, _arg117);
                            break;
                        case 22:
                            int _arg021 = data.readInt();
                            CdmaSmsWriteArgs _arg118 = (CdmaSmsWriteArgs) data.readTypedObject(CdmaSmsWriteArgs.CREATOR);
                            data.enforceNoDataAvail();
                            writeSmsToRuim(_arg021, _arg118);
                            break;
                        case 23:
                            int _arg022 = data.readInt();
                            SmsWriteArgs _arg119 = (SmsWriteArgs) data.readTypedObject(SmsWriteArgs.CREATOR);
                            data.enforceNoDataAvail();
                            writeSmsToSim(_arg022, _arg119);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes2.dex */
        private static class Proxy implements IRadioMessaging {
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

            @Override // android.hardware.radio.messaging.IRadioMessaging
            public void acknowledgeIncomingGsmSmsWithPdu(int serial, boolean success, String ackPdu) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    _data.writeBoolean(success);
                    _data.writeString(ackPdu);
                    boolean _status = this.mRemote.transact(1, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method acknowledgeIncomingGsmSmsWithPdu is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.messaging.IRadioMessaging
            public void acknowledgeLastIncomingCdmaSms(int serial, CdmaSmsAck smsAck) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    _data.writeTypedObject(smsAck, 0);
                    boolean _status = this.mRemote.transact(2, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method acknowledgeLastIncomingCdmaSms is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.messaging.IRadioMessaging
            public void acknowledgeLastIncomingGsmSms(int serial, boolean success, int cause) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    _data.writeBoolean(success);
                    _data.writeInt(cause);
                    boolean _status = this.mRemote.transact(3, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method acknowledgeLastIncomingGsmSms is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.messaging.IRadioMessaging
            public void deleteSmsOnRuim(int serial, int index) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    _data.writeInt(index);
                    boolean _status = this.mRemote.transact(4, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method deleteSmsOnRuim is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.messaging.IRadioMessaging
            public void deleteSmsOnSim(int serial, int index) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    _data.writeInt(index);
                    boolean _status = this.mRemote.transact(5, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method deleteSmsOnSim is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.messaging.IRadioMessaging
            public void getCdmaBroadcastConfig(int serial) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    boolean _status = this.mRemote.transact(6, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method getCdmaBroadcastConfig is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.messaging.IRadioMessaging
            public void getGsmBroadcastConfig(int serial) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    boolean _status = this.mRemote.transact(7, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method getGsmBroadcastConfig is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.messaging.IRadioMessaging
            public void getSmscAddress(int serial) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    boolean _status = this.mRemote.transact(8, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method getSmscAddress is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.messaging.IRadioMessaging
            public void reportSmsMemoryStatus(int serial, boolean available) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    _data.writeBoolean(available);
                    boolean _status = this.mRemote.transact(9, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method reportSmsMemoryStatus is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.messaging.IRadioMessaging
            public void responseAcknowledgement() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    boolean _status = this.mRemote.transact(10, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method responseAcknowledgement is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.messaging.IRadioMessaging
            public void sendCdmaSms(int serial, CdmaSmsMessage sms) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    _data.writeTypedObject(sms, 0);
                    boolean _status = this.mRemote.transact(11, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method sendCdmaSms is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.messaging.IRadioMessaging
            public void sendCdmaSmsExpectMore(int serial, CdmaSmsMessage sms) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    _data.writeTypedObject(sms, 0);
                    boolean _status = this.mRemote.transact(12, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method sendCdmaSmsExpectMore is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.messaging.IRadioMessaging
            public void sendImsSms(int serial, ImsSmsMessage message) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    _data.writeTypedObject(message, 0);
                    boolean _status = this.mRemote.transact(13, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method sendImsSms is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.messaging.IRadioMessaging
            public void sendSms(int serial, GsmSmsMessage message) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    _data.writeTypedObject(message, 0);
                    boolean _status = this.mRemote.transact(14, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method sendSms is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.messaging.IRadioMessaging
            public void sendSmsExpectMore(int serial, GsmSmsMessage message) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    _data.writeTypedObject(message, 0);
                    boolean _status = this.mRemote.transact(15, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method sendSmsExpectMore is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.messaging.IRadioMessaging
            public void setCdmaBroadcastActivation(int serial, boolean activate) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    _data.writeBoolean(activate);
                    boolean _status = this.mRemote.transact(16, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method setCdmaBroadcastActivation is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.messaging.IRadioMessaging
            public void setCdmaBroadcastConfig(int serial, CdmaBroadcastSmsConfigInfo[] configInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    _data.writeTypedArray(configInfo, 0);
                    boolean _status = this.mRemote.transact(17, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method setCdmaBroadcastConfig is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.messaging.IRadioMessaging
            public void setGsmBroadcastActivation(int serial, boolean activate) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    _data.writeBoolean(activate);
                    boolean _status = this.mRemote.transact(18, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method setGsmBroadcastActivation is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.messaging.IRadioMessaging
            public void setGsmBroadcastConfig(int serial, GsmBroadcastSmsConfigInfo[] configInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    _data.writeTypedArray(configInfo, 0);
                    boolean _status = this.mRemote.transact(19, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method setGsmBroadcastConfig is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.messaging.IRadioMessaging
            public void setResponseFunctions(IRadioMessagingResponse radioMessagingResponse, IRadioMessagingIndication radioMessagingIndication) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeStrongInterface(radioMessagingResponse);
                    _data.writeStrongInterface(radioMessagingIndication);
                    boolean _status = this.mRemote.transact(20, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method setResponseFunctions is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.messaging.IRadioMessaging
            public void setSmscAddress(int serial, String smsc) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    _data.writeString(smsc);
                    boolean _status = this.mRemote.transact(21, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method setSmscAddress is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.messaging.IRadioMessaging
            public void writeSmsToRuim(int serial, CdmaSmsWriteArgs cdmaSms) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    _data.writeTypedObject(cdmaSms, 0);
                    boolean _status = this.mRemote.transact(22, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method writeSmsToRuim is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.messaging.IRadioMessaging
            public void writeSmsToSim(int serial, SmsWriteArgs smsWriteArgs) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    _data.writeTypedObject(smsWriteArgs, 0);
                    boolean _status = this.mRemote.transact(23, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method writeSmsToSim is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.messaging.IRadioMessaging
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

            @Override // android.hardware.radio.messaging.IRadioMessaging
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
