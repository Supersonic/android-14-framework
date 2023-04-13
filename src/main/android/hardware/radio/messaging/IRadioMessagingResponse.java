package android.hardware.radio.messaging;

import android.hardware.radio.RadioResponseInfo;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes2.dex */
public interface IRadioMessagingResponse extends IInterface {
    public static final String DESCRIPTOR = "android$hardware$radio$messaging$IRadioMessagingResponse".replace('$', '.');
    public static final String HASH = "notfrozen";
    public static final int VERSION = 2;

    void acknowledgeIncomingGsmSmsWithPduResponse(RadioResponseInfo radioResponseInfo) throws RemoteException;

    void acknowledgeLastIncomingCdmaSmsResponse(RadioResponseInfo radioResponseInfo) throws RemoteException;

    void acknowledgeLastIncomingGsmSmsResponse(RadioResponseInfo radioResponseInfo) throws RemoteException;

    void acknowledgeRequest(int i) throws RemoteException;

    void deleteSmsOnRuimResponse(RadioResponseInfo radioResponseInfo) throws RemoteException;

    void deleteSmsOnSimResponse(RadioResponseInfo radioResponseInfo) throws RemoteException;

    void getCdmaBroadcastConfigResponse(RadioResponseInfo radioResponseInfo, CdmaBroadcastSmsConfigInfo[] cdmaBroadcastSmsConfigInfoArr) throws RemoteException;

    void getGsmBroadcastConfigResponse(RadioResponseInfo radioResponseInfo, GsmBroadcastSmsConfigInfo[] gsmBroadcastSmsConfigInfoArr) throws RemoteException;

    String getInterfaceHash() throws RemoteException;

    int getInterfaceVersion() throws RemoteException;

    void getSmscAddressResponse(RadioResponseInfo radioResponseInfo, String str) throws RemoteException;

    void reportSmsMemoryStatusResponse(RadioResponseInfo radioResponseInfo) throws RemoteException;

    void sendCdmaSmsExpectMoreResponse(RadioResponseInfo radioResponseInfo, SendSmsResult sendSmsResult) throws RemoteException;

    void sendCdmaSmsResponse(RadioResponseInfo radioResponseInfo, SendSmsResult sendSmsResult) throws RemoteException;

    void sendImsSmsResponse(RadioResponseInfo radioResponseInfo, SendSmsResult sendSmsResult) throws RemoteException;

    void sendSmsExpectMoreResponse(RadioResponseInfo radioResponseInfo, SendSmsResult sendSmsResult) throws RemoteException;

    void sendSmsResponse(RadioResponseInfo radioResponseInfo, SendSmsResult sendSmsResult) throws RemoteException;

    void setCdmaBroadcastActivationResponse(RadioResponseInfo radioResponseInfo) throws RemoteException;

    void setCdmaBroadcastConfigResponse(RadioResponseInfo radioResponseInfo) throws RemoteException;

    void setGsmBroadcastActivationResponse(RadioResponseInfo radioResponseInfo) throws RemoteException;

    void setGsmBroadcastConfigResponse(RadioResponseInfo radioResponseInfo) throws RemoteException;

    void setSmscAddressResponse(RadioResponseInfo radioResponseInfo) throws RemoteException;

    void writeSmsToRuimResponse(RadioResponseInfo radioResponseInfo, int i) throws RemoteException;

    void writeSmsToSimResponse(RadioResponseInfo radioResponseInfo, int i) throws RemoteException;

    /* loaded from: classes2.dex */
    public static class Default implements IRadioMessagingResponse {
        @Override // android.hardware.radio.messaging.IRadioMessagingResponse
        public void acknowledgeIncomingGsmSmsWithPduResponse(RadioResponseInfo info) throws RemoteException {
        }

        @Override // android.hardware.radio.messaging.IRadioMessagingResponse
        public void acknowledgeLastIncomingCdmaSmsResponse(RadioResponseInfo info) throws RemoteException {
        }

        @Override // android.hardware.radio.messaging.IRadioMessagingResponse
        public void acknowledgeLastIncomingGsmSmsResponse(RadioResponseInfo info) throws RemoteException {
        }

        @Override // android.hardware.radio.messaging.IRadioMessagingResponse
        public void acknowledgeRequest(int serial) throws RemoteException {
        }

        @Override // android.hardware.radio.messaging.IRadioMessagingResponse
        public void deleteSmsOnRuimResponse(RadioResponseInfo info) throws RemoteException {
        }

        @Override // android.hardware.radio.messaging.IRadioMessagingResponse
        public void deleteSmsOnSimResponse(RadioResponseInfo info) throws RemoteException {
        }

        @Override // android.hardware.radio.messaging.IRadioMessagingResponse
        public void getCdmaBroadcastConfigResponse(RadioResponseInfo info, CdmaBroadcastSmsConfigInfo[] configs) throws RemoteException {
        }

        @Override // android.hardware.radio.messaging.IRadioMessagingResponse
        public void getGsmBroadcastConfigResponse(RadioResponseInfo info, GsmBroadcastSmsConfigInfo[] configs) throws RemoteException {
        }

        @Override // android.hardware.radio.messaging.IRadioMessagingResponse
        public void getSmscAddressResponse(RadioResponseInfo info, String smsc) throws RemoteException {
        }

        @Override // android.hardware.radio.messaging.IRadioMessagingResponse
        public void reportSmsMemoryStatusResponse(RadioResponseInfo info) throws RemoteException {
        }

        @Override // android.hardware.radio.messaging.IRadioMessagingResponse
        public void sendCdmaSmsExpectMoreResponse(RadioResponseInfo info, SendSmsResult sms) throws RemoteException {
        }

        @Override // android.hardware.radio.messaging.IRadioMessagingResponse
        public void sendCdmaSmsResponse(RadioResponseInfo info, SendSmsResult sms) throws RemoteException {
        }

        @Override // android.hardware.radio.messaging.IRadioMessagingResponse
        public void sendImsSmsResponse(RadioResponseInfo info, SendSmsResult sms) throws RemoteException {
        }

        @Override // android.hardware.radio.messaging.IRadioMessagingResponse
        public void sendSmsExpectMoreResponse(RadioResponseInfo info, SendSmsResult sms) throws RemoteException {
        }

        @Override // android.hardware.radio.messaging.IRadioMessagingResponse
        public void sendSmsResponse(RadioResponseInfo info, SendSmsResult sms) throws RemoteException {
        }

        @Override // android.hardware.radio.messaging.IRadioMessagingResponse
        public void setCdmaBroadcastActivationResponse(RadioResponseInfo info) throws RemoteException {
        }

        @Override // android.hardware.radio.messaging.IRadioMessagingResponse
        public void setCdmaBroadcastConfigResponse(RadioResponseInfo info) throws RemoteException {
        }

        @Override // android.hardware.radio.messaging.IRadioMessagingResponse
        public void setGsmBroadcastActivationResponse(RadioResponseInfo info) throws RemoteException {
        }

        @Override // android.hardware.radio.messaging.IRadioMessagingResponse
        public void setGsmBroadcastConfigResponse(RadioResponseInfo info) throws RemoteException {
        }

        @Override // android.hardware.radio.messaging.IRadioMessagingResponse
        public void setSmscAddressResponse(RadioResponseInfo info) throws RemoteException {
        }

        @Override // android.hardware.radio.messaging.IRadioMessagingResponse
        public void writeSmsToRuimResponse(RadioResponseInfo info, int index) throws RemoteException {
        }

        @Override // android.hardware.radio.messaging.IRadioMessagingResponse
        public void writeSmsToSimResponse(RadioResponseInfo info, int index) throws RemoteException {
        }

        @Override // android.hardware.radio.messaging.IRadioMessagingResponse
        public int getInterfaceVersion() {
            return 0;
        }

        @Override // android.hardware.radio.messaging.IRadioMessagingResponse
        public String getInterfaceHash() {
            return "";
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements IRadioMessagingResponse {
        static final int TRANSACTION_acknowledgeIncomingGsmSmsWithPduResponse = 1;
        static final int TRANSACTION_acknowledgeLastIncomingCdmaSmsResponse = 2;
        static final int TRANSACTION_acknowledgeLastIncomingGsmSmsResponse = 3;
        static final int TRANSACTION_acknowledgeRequest = 4;
        static final int TRANSACTION_deleteSmsOnRuimResponse = 5;
        static final int TRANSACTION_deleteSmsOnSimResponse = 6;
        static final int TRANSACTION_getCdmaBroadcastConfigResponse = 7;
        static final int TRANSACTION_getGsmBroadcastConfigResponse = 8;
        static final int TRANSACTION_getInterfaceHash = 16777214;
        static final int TRANSACTION_getInterfaceVersion = 16777215;
        static final int TRANSACTION_getSmscAddressResponse = 9;
        static final int TRANSACTION_reportSmsMemoryStatusResponse = 10;
        static final int TRANSACTION_sendCdmaSmsExpectMoreResponse = 11;
        static final int TRANSACTION_sendCdmaSmsResponse = 12;
        static final int TRANSACTION_sendImsSmsResponse = 13;
        static final int TRANSACTION_sendSmsExpectMoreResponse = 14;
        static final int TRANSACTION_sendSmsResponse = 15;
        static final int TRANSACTION_setCdmaBroadcastActivationResponse = 16;
        static final int TRANSACTION_setCdmaBroadcastConfigResponse = 17;
        static final int TRANSACTION_setGsmBroadcastActivationResponse = 18;
        static final int TRANSACTION_setGsmBroadcastConfigResponse = 19;
        static final int TRANSACTION_setSmscAddressResponse = 20;
        static final int TRANSACTION_writeSmsToRuimResponse = 21;
        static final int TRANSACTION_writeSmsToSimResponse = 22;

        public Stub() {
            markVintfStability();
            attachInterface(this, DESCRIPTOR);
        }

        public static IRadioMessagingResponse asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IRadioMessagingResponse)) {
                return (IRadioMessagingResponse) iin;
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
                            RadioResponseInfo _arg0 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            data.enforceNoDataAvail();
                            acknowledgeIncomingGsmSmsWithPduResponse(_arg0);
                            break;
                        case 2:
                            RadioResponseInfo _arg02 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            data.enforceNoDataAvail();
                            acknowledgeLastIncomingCdmaSmsResponse(_arg02);
                            break;
                        case 3:
                            RadioResponseInfo _arg03 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            data.enforceNoDataAvail();
                            acknowledgeLastIncomingGsmSmsResponse(_arg03);
                            break;
                        case 4:
                            int _arg04 = data.readInt();
                            data.enforceNoDataAvail();
                            acknowledgeRequest(_arg04);
                            break;
                        case 5:
                            RadioResponseInfo _arg05 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            data.enforceNoDataAvail();
                            deleteSmsOnRuimResponse(_arg05);
                            break;
                        case 6:
                            RadioResponseInfo _arg06 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            data.enforceNoDataAvail();
                            deleteSmsOnSimResponse(_arg06);
                            break;
                        case 7:
                            RadioResponseInfo _arg07 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            CdmaBroadcastSmsConfigInfo[] _arg1 = (CdmaBroadcastSmsConfigInfo[]) data.createTypedArray(CdmaBroadcastSmsConfigInfo.CREATOR);
                            data.enforceNoDataAvail();
                            getCdmaBroadcastConfigResponse(_arg07, _arg1);
                            break;
                        case 8:
                            RadioResponseInfo _arg08 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            GsmBroadcastSmsConfigInfo[] _arg12 = (GsmBroadcastSmsConfigInfo[]) data.createTypedArray(GsmBroadcastSmsConfigInfo.CREATOR);
                            data.enforceNoDataAvail();
                            getGsmBroadcastConfigResponse(_arg08, _arg12);
                            break;
                        case 9:
                            RadioResponseInfo _arg09 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            String _arg13 = data.readString();
                            data.enforceNoDataAvail();
                            getSmscAddressResponse(_arg09, _arg13);
                            break;
                        case 10:
                            RadioResponseInfo _arg010 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            data.enforceNoDataAvail();
                            reportSmsMemoryStatusResponse(_arg010);
                            break;
                        case 11:
                            RadioResponseInfo _arg011 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            SendSmsResult _arg14 = (SendSmsResult) data.readTypedObject(SendSmsResult.CREATOR);
                            data.enforceNoDataAvail();
                            sendCdmaSmsExpectMoreResponse(_arg011, _arg14);
                            break;
                        case 12:
                            RadioResponseInfo _arg012 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            SendSmsResult _arg15 = (SendSmsResult) data.readTypedObject(SendSmsResult.CREATOR);
                            data.enforceNoDataAvail();
                            sendCdmaSmsResponse(_arg012, _arg15);
                            break;
                        case 13:
                            RadioResponseInfo _arg013 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            SendSmsResult _arg16 = (SendSmsResult) data.readTypedObject(SendSmsResult.CREATOR);
                            data.enforceNoDataAvail();
                            sendImsSmsResponse(_arg013, _arg16);
                            break;
                        case 14:
                            RadioResponseInfo _arg014 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            SendSmsResult _arg17 = (SendSmsResult) data.readTypedObject(SendSmsResult.CREATOR);
                            data.enforceNoDataAvail();
                            sendSmsExpectMoreResponse(_arg014, _arg17);
                            break;
                        case 15:
                            RadioResponseInfo _arg015 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            SendSmsResult _arg18 = (SendSmsResult) data.readTypedObject(SendSmsResult.CREATOR);
                            data.enforceNoDataAvail();
                            sendSmsResponse(_arg015, _arg18);
                            break;
                        case 16:
                            RadioResponseInfo _arg016 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            data.enforceNoDataAvail();
                            setCdmaBroadcastActivationResponse(_arg016);
                            break;
                        case 17:
                            RadioResponseInfo _arg017 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            data.enforceNoDataAvail();
                            setCdmaBroadcastConfigResponse(_arg017);
                            break;
                        case 18:
                            RadioResponseInfo _arg018 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            data.enforceNoDataAvail();
                            setGsmBroadcastActivationResponse(_arg018);
                            break;
                        case 19:
                            RadioResponseInfo _arg019 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            data.enforceNoDataAvail();
                            setGsmBroadcastConfigResponse(_arg019);
                            break;
                        case 20:
                            RadioResponseInfo _arg020 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            data.enforceNoDataAvail();
                            setSmscAddressResponse(_arg020);
                            break;
                        case 21:
                            RadioResponseInfo _arg021 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            int _arg19 = data.readInt();
                            data.enforceNoDataAvail();
                            writeSmsToRuimResponse(_arg021, _arg19);
                            break;
                        case 22:
                            RadioResponseInfo _arg022 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            int _arg110 = data.readInt();
                            data.enforceNoDataAvail();
                            writeSmsToSimResponse(_arg022, _arg110);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes2.dex */
        private static class Proxy implements IRadioMessagingResponse {
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

            @Override // android.hardware.radio.messaging.IRadioMessagingResponse
            public void acknowledgeIncomingGsmSmsWithPduResponse(RadioResponseInfo info) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    boolean _status = this.mRemote.transact(1, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method acknowledgeIncomingGsmSmsWithPduResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.messaging.IRadioMessagingResponse
            public void acknowledgeLastIncomingCdmaSmsResponse(RadioResponseInfo info) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    boolean _status = this.mRemote.transact(2, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method acknowledgeLastIncomingCdmaSmsResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.messaging.IRadioMessagingResponse
            public void acknowledgeLastIncomingGsmSmsResponse(RadioResponseInfo info) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    boolean _status = this.mRemote.transact(3, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method acknowledgeLastIncomingGsmSmsResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.messaging.IRadioMessagingResponse
            public void acknowledgeRequest(int serial) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    boolean _status = this.mRemote.transact(4, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method acknowledgeRequest is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.messaging.IRadioMessagingResponse
            public void deleteSmsOnRuimResponse(RadioResponseInfo info) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    boolean _status = this.mRemote.transact(5, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method deleteSmsOnRuimResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.messaging.IRadioMessagingResponse
            public void deleteSmsOnSimResponse(RadioResponseInfo info) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    boolean _status = this.mRemote.transact(6, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method deleteSmsOnSimResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.messaging.IRadioMessagingResponse
            public void getCdmaBroadcastConfigResponse(RadioResponseInfo info, CdmaBroadcastSmsConfigInfo[] configs) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    _data.writeTypedArray(configs, 0);
                    boolean _status = this.mRemote.transact(7, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method getCdmaBroadcastConfigResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.messaging.IRadioMessagingResponse
            public void getGsmBroadcastConfigResponse(RadioResponseInfo info, GsmBroadcastSmsConfigInfo[] configs) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    _data.writeTypedArray(configs, 0);
                    boolean _status = this.mRemote.transact(8, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method getGsmBroadcastConfigResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.messaging.IRadioMessagingResponse
            public void getSmscAddressResponse(RadioResponseInfo info, String smsc) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    _data.writeString(smsc);
                    boolean _status = this.mRemote.transact(9, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method getSmscAddressResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.messaging.IRadioMessagingResponse
            public void reportSmsMemoryStatusResponse(RadioResponseInfo info) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    boolean _status = this.mRemote.transact(10, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method reportSmsMemoryStatusResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.messaging.IRadioMessagingResponse
            public void sendCdmaSmsExpectMoreResponse(RadioResponseInfo info, SendSmsResult sms) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    _data.writeTypedObject(sms, 0);
                    boolean _status = this.mRemote.transact(11, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method sendCdmaSmsExpectMoreResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.messaging.IRadioMessagingResponse
            public void sendCdmaSmsResponse(RadioResponseInfo info, SendSmsResult sms) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    _data.writeTypedObject(sms, 0);
                    boolean _status = this.mRemote.transact(12, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method sendCdmaSmsResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.messaging.IRadioMessagingResponse
            public void sendImsSmsResponse(RadioResponseInfo info, SendSmsResult sms) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    _data.writeTypedObject(sms, 0);
                    boolean _status = this.mRemote.transact(13, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method sendImsSmsResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.messaging.IRadioMessagingResponse
            public void sendSmsExpectMoreResponse(RadioResponseInfo info, SendSmsResult sms) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    _data.writeTypedObject(sms, 0);
                    boolean _status = this.mRemote.transact(14, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method sendSmsExpectMoreResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.messaging.IRadioMessagingResponse
            public void sendSmsResponse(RadioResponseInfo info, SendSmsResult sms) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    _data.writeTypedObject(sms, 0);
                    boolean _status = this.mRemote.transact(15, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method sendSmsResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.messaging.IRadioMessagingResponse
            public void setCdmaBroadcastActivationResponse(RadioResponseInfo info) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    boolean _status = this.mRemote.transact(16, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method setCdmaBroadcastActivationResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.messaging.IRadioMessagingResponse
            public void setCdmaBroadcastConfigResponse(RadioResponseInfo info) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    boolean _status = this.mRemote.transact(17, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method setCdmaBroadcastConfigResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.messaging.IRadioMessagingResponse
            public void setGsmBroadcastActivationResponse(RadioResponseInfo info) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    boolean _status = this.mRemote.transact(18, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method setGsmBroadcastActivationResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.messaging.IRadioMessagingResponse
            public void setGsmBroadcastConfigResponse(RadioResponseInfo info) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    boolean _status = this.mRemote.transact(19, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method setGsmBroadcastConfigResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.messaging.IRadioMessagingResponse
            public void setSmscAddressResponse(RadioResponseInfo info) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    boolean _status = this.mRemote.transact(20, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method setSmscAddressResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.messaging.IRadioMessagingResponse
            public void writeSmsToRuimResponse(RadioResponseInfo info, int index) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    _data.writeInt(index);
                    boolean _status = this.mRemote.transact(21, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method writeSmsToRuimResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.messaging.IRadioMessagingResponse
            public void writeSmsToSimResponse(RadioResponseInfo info, int index) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    _data.writeInt(index);
                    boolean _status = this.mRemote.transact(22, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method writeSmsToSimResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.messaging.IRadioMessagingResponse
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

            @Override // android.hardware.radio.messaging.IRadioMessagingResponse
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
