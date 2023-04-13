package android.hardware.radio.modem;

import android.hardware.radio.RadioResponseInfo;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes2.dex */
public interface IRadioModemResponse extends IInterface {
    public static final String DESCRIPTOR = "android$hardware$radio$modem$IRadioModemResponse".replace('$', '.');
    public static final String HASH = "notfrozen";
    public static final int VERSION = 2;

    void acknowledgeRequest(int i) throws RemoteException;

    void enableModemResponse(RadioResponseInfo radioResponseInfo) throws RemoteException;

    void getBasebandVersionResponse(RadioResponseInfo radioResponseInfo, String str) throws RemoteException;

    @Deprecated
    void getDeviceIdentityResponse(RadioResponseInfo radioResponseInfo, String str, String str2, String str3, String str4) throws RemoteException;

    void getHardwareConfigResponse(RadioResponseInfo radioResponseInfo, HardwareConfig[] hardwareConfigArr) throws RemoteException;

    void getImeiResponse(RadioResponseInfo radioResponseInfo, ImeiInfo imeiInfo) throws RemoteException;

    String getInterfaceHash() throws RemoteException;

    int getInterfaceVersion() throws RemoteException;

    void getModemActivityInfoResponse(RadioResponseInfo radioResponseInfo, ActivityStatsInfo activityStatsInfo) throws RemoteException;

    void getModemStackStatusResponse(RadioResponseInfo radioResponseInfo, boolean z) throws RemoteException;

    void getRadioCapabilityResponse(RadioResponseInfo radioResponseInfo, RadioCapability radioCapability) throws RemoteException;

    @Deprecated
    void nvReadItemResponse(RadioResponseInfo radioResponseInfo, String str) throws RemoteException;

    void nvResetConfigResponse(RadioResponseInfo radioResponseInfo) throws RemoteException;

    @Deprecated
    void nvWriteCdmaPrlResponse(RadioResponseInfo radioResponseInfo) throws RemoteException;

    @Deprecated
    void nvWriteItemResponse(RadioResponseInfo radioResponseInfo) throws RemoteException;

    void requestShutdownResponse(RadioResponseInfo radioResponseInfo) throws RemoteException;

    void sendDeviceStateResponse(RadioResponseInfo radioResponseInfo) throws RemoteException;

    void setRadioCapabilityResponse(RadioResponseInfo radioResponseInfo, RadioCapability radioCapability) throws RemoteException;

    void setRadioPowerResponse(RadioResponseInfo radioResponseInfo) throws RemoteException;

    /* loaded from: classes2.dex */
    public static class Default implements IRadioModemResponse {
        @Override // android.hardware.radio.modem.IRadioModemResponse
        public void acknowledgeRequest(int serial) throws RemoteException {
        }

        @Override // android.hardware.radio.modem.IRadioModemResponse
        public void enableModemResponse(RadioResponseInfo info) throws RemoteException {
        }

        @Override // android.hardware.radio.modem.IRadioModemResponse
        public void getBasebandVersionResponse(RadioResponseInfo info, String version) throws RemoteException {
        }

        @Override // android.hardware.radio.modem.IRadioModemResponse
        public void getDeviceIdentityResponse(RadioResponseInfo info, String imei, String imeisv, String esn, String meid) throws RemoteException {
        }

        @Override // android.hardware.radio.modem.IRadioModemResponse
        public void getHardwareConfigResponse(RadioResponseInfo info, HardwareConfig[] config) throws RemoteException {
        }

        @Override // android.hardware.radio.modem.IRadioModemResponse
        public void getModemActivityInfoResponse(RadioResponseInfo info, ActivityStatsInfo activityInfo) throws RemoteException {
        }

        @Override // android.hardware.radio.modem.IRadioModemResponse
        public void getModemStackStatusResponse(RadioResponseInfo info, boolean isEnabled) throws RemoteException {
        }

        @Override // android.hardware.radio.modem.IRadioModemResponse
        public void getRadioCapabilityResponse(RadioResponseInfo info, RadioCapability rc) throws RemoteException {
        }

        @Override // android.hardware.radio.modem.IRadioModemResponse
        public void nvReadItemResponse(RadioResponseInfo info, String result) throws RemoteException {
        }

        @Override // android.hardware.radio.modem.IRadioModemResponse
        public void nvResetConfigResponse(RadioResponseInfo info) throws RemoteException {
        }

        @Override // android.hardware.radio.modem.IRadioModemResponse
        public void nvWriteCdmaPrlResponse(RadioResponseInfo info) throws RemoteException {
        }

        @Override // android.hardware.radio.modem.IRadioModemResponse
        public void nvWriteItemResponse(RadioResponseInfo info) throws RemoteException {
        }

        @Override // android.hardware.radio.modem.IRadioModemResponse
        public void requestShutdownResponse(RadioResponseInfo info) throws RemoteException {
        }

        @Override // android.hardware.radio.modem.IRadioModemResponse
        public void sendDeviceStateResponse(RadioResponseInfo info) throws RemoteException {
        }

        @Override // android.hardware.radio.modem.IRadioModemResponse
        public void setRadioCapabilityResponse(RadioResponseInfo info, RadioCapability rc) throws RemoteException {
        }

        @Override // android.hardware.radio.modem.IRadioModemResponse
        public void setRadioPowerResponse(RadioResponseInfo info) throws RemoteException {
        }

        @Override // android.hardware.radio.modem.IRadioModemResponse
        public void getImeiResponse(RadioResponseInfo responseInfo, ImeiInfo imeiInfo) throws RemoteException {
        }

        @Override // android.hardware.radio.modem.IRadioModemResponse
        public int getInterfaceVersion() {
            return 0;
        }

        @Override // android.hardware.radio.modem.IRadioModemResponse
        public String getInterfaceHash() {
            return "";
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements IRadioModemResponse {
        static final int TRANSACTION_acknowledgeRequest = 1;
        static final int TRANSACTION_enableModemResponse = 2;
        static final int TRANSACTION_getBasebandVersionResponse = 3;
        static final int TRANSACTION_getDeviceIdentityResponse = 4;
        static final int TRANSACTION_getHardwareConfigResponse = 5;
        static final int TRANSACTION_getImeiResponse = 17;
        static final int TRANSACTION_getInterfaceHash = 16777214;
        static final int TRANSACTION_getInterfaceVersion = 16777215;
        static final int TRANSACTION_getModemActivityInfoResponse = 6;
        static final int TRANSACTION_getModemStackStatusResponse = 7;
        static final int TRANSACTION_getRadioCapabilityResponse = 8;
        static final int TRANSACTION_nvReadItemResponse = 9;
        static final int TRANSACTION_nvResetConfigResponse = 10;
        static final int TRANSACTION_nvWriteCdmaPrlResponse = 11;
        static final int TRANSACTION_nvWriteItemResponse = 12;
        static final int TRANSACTION_requestShutdownResponse = 13;
        static final int TRANSACTION_sendDeviceStateResponse = 14;
        static final int TRANSACTION_setRadioCapabilityResponse = 15;
        static final int TRANSACTION_setRadioPowerResponse = 16;

        public Stub() {
            markVintfStability();
            attachInterface(this, DESCRIPTOR);
        }

        public static IRadioModemResponse asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IRadioModemResponse)) {
                return (IRadioModemResponse) iin;
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
                            acknowledgeRequest(_arg0);
                            break;
                        case 2:
                            RadioResponseInfo _arg02 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            data.enforceNoDataAvail();
                            enableModemResponse(_arg02);
                            break;
                        case 3:
                            RadioResponseInfo _arg03 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            String _arg1 = data.readString();
                            data.enforceNoDataAvail();
                            getBasebandVersionResponse(_arg03, _arg1);
                            break;
                        case 4:
                            RadioResponseInfo _arg04 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            String _arg12 = data.readString();
                            String _arg2 = data.readString();
                            String _arg3 = data.readString();
                            String _arg4 = data.readString();
                            data.enforceNoDataAvail();
                            getDeviceIdentityResponse(_arg04, _arg12, _arg2, _arg3, _arg4);
                            break;
                        case 5:
                            RadioResponseInfo _arg05 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            HardwareConfig[] _arg13 = (HardwareConfig[]) data.createTypedArray(HardwareConfig.CREATOR);
                            data.enforceNoDataAvail();
                            getHardwareConfigResponse(_arg05, _arg13);
                            break;
                        case 6:
                            RadioResponseInfo _arg06 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            ActivityStatsInfo _arg14 = (ActivityStatsInfo) data.readTypedObject(ActivityStatsInfo.CREATOR);
                            data.enforceNoDataAvail();
                            getModemActivityInfoResponse(_arg06, _arg14);
                            break;
                        case 7:
                            RadioResponseInfo _arg07 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            boolean _arg15 = data.readBoolean();
                            data.enforceNoDataAvail();
                            getModemStackStatusResponse(_arg07, _arg15);
                            break;
                        case 8:
                            RadioResponseInfo _arg08 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            RadioCapability _arg16 = (RadioCapability) data.readTypedObject(RadioCapability.CREATOR);
                            data.enforceNoDataAvail();
                            getRadioCapabilityResponse(_arg08, _arg16);
                            break;
                        case 9:
                            RadioResponseInfo _arg09 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            String _arg17 = data.readString();
                            data.enforceNoDataAvail();
                            nvReadItemResponse(_arg09, _arg17);
                            break;
                        case 10:
                            RadioResponseInfo _arg010 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            data.enforceNoDataAvail();
                            nvResetConfigResponse(_arg010);
                            break;
                        case 11:
                            RadioResponseInfo _arg011 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            data.enforceNoDataAvail();
                            nvWriteCdmaPrlResponse(_arg011);
                            break;
                        case 12:
                            RadioResponseInfo _arg012 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            data.enforceNoDataAvail();
                            nvWriteItemResponse(_arg012);
                            break;
                        case 13:
                            RadioResponseInfo _arg013 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            data.enforceNoDataAvail();
                            requestShutdownResponse(_arg013);
                            break;
                        case 14:
                            RadioResponseInfo _arg014 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            data.enforceNoDataAvail();
                            sendDeviceStateResponse(_arg014);
                            break;
                        case 15:
                            RadioResponseInfo _arg015 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            RadioCapability _arg18 = (RadioCapability) data.readTypedObject(RadioCapability.CREATOR);
                            data.enforceNoDataAvail();
                            setRadioCapabilityResponse(_arg015, _arg18);
                            break;
                        case 16:
                            RadioResponseInfo _arg016 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            data.enforceNoDataAvail();
                            setRadioPowerResponse(_arg016);
                            break;
                        case 17:
                            RadioResponseInfo _arg017 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            ImeiInfo _arg19 = (ImeiInfo) data.readTypedObject(ImeiInfo.CREATOR);
                            data.enforceNoDataAvail();
                            getImeiResponse(_arg017, _arg19);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes2.dex */
        private static class Proxy implements IRadioModemResponse {
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

            @Override // android.hardware.radio.modem.IRadioModemResponse
            public void acknowledgeRequest(int serial) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    boolean _status = this.mRemote.transact(1, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method acknowledgeRequest is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.modem.IRadioModemResponse
            public void enableModemResponse(RadioResponseInfo info) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    boolean _status = this.mRemote.transact(2, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method enableModemResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.modem.IRadioModemResponse
            public void getBasebandVersionResponse(RadioResponseInfo info, String version) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    _data.writeString(version);
                    boolean _status = this.mRemote.transact(3, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method getBasebandVersionResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.modem.IRadioModemResponse
            public void getDeviceIdentityResponse(RadioResponseInfo info, String imei, String imeisv, String esn, String meid) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    _data.writeString(imei);
                    _data.writeString(imeisv);
                    _data.writeString(esn);
                    _data.writeString(meid);
                    boolean _status = this.mRemote.transact(4, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method getDeviceIdentityResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.modem.IRadioModemResponse
            public void getHardwareConfigResponse(RadioResponseInfo info, HardwareConfig[] config) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    _data.writeTypedArray(config, 0);
                    boolean _status = this.mRemote.transact(5, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method getHardwareConfigResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.modem.IRadioModemResponse
            public void getModemActivityInfoResponse(RadioResponseInfo info, ActivityStatsInfo activityInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    _data.writeTypedObject(activityInfo, 0);
                    boolean _status = this.mRemote.transact(6, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method getModemActivityInfoResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.modem.IRadioModemResponse
            public void getModemStackStatusResponse(RadioResponseInfo info, boolean isEnabled) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    _data.writeBoolean(isEnabled);
                    boolean _status = this.mRemote.transact(7, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method getModemStackStatusResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.modem.IRadioModemResponse
            public void getRadioCapabilityResponse(RadioResponseInfo info, RadioCapability rc) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    _data.writeTypedObject(rc, 0);
                    boolean _status = this.mRemote.transact(8, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method getRadioCapabilityResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.modem.IRadioModemResponse
            public void nvReadItemResponse(RadioResponseInfo info, String result) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    _data.writeString(result);
                    boolean _status = this.mRemote.transact(9, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method nvReadItemResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.modem.IRadioModemResponse
            public void nvResetConfigResponse(RadioResponseInfo info) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    boolean _status = this.mRemote.transact(10, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method nvResetConfigResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.modem.IRadioModemResponse
            public void nvWriteCdmaPrlResponse(RadioResponseInfo info) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    boolean _status = this.mRemote.transact(11, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method nvWriteCdmaPrlResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.modem.IRadioModemResponse
            public void nvWriteItemResponse(RadioResponseInfo info) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    boolean _status = this.mRemote.transact(12, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method nvWriteItemResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.modem.IRadioModemResponse
            public void requestShutdownResponse(RadioResponseInfo info) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    boolean _status = this.mRemote.transact(13, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method requestShutdownResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.modem.IRadioModemResponse
            public void sendDeviceStateResponse(RadioResponseInfo info) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    boolean _status = this.mRemote.transact(14, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method sendDeviceStateResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.modem.IRadioModemResponse
            public void setRadioCapabilityResponse(RadioResponseInfo info, RadioCapability rc) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    _data.writeTypedObject(rc, 0);
                    boolean _status = this.mRemote.transact(15, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method setRadioCapabilityResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.modem.IRadioModemResponse
            public void setRadioPowerResponse(RadioResponseInfo info) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    boolean _status = this.mRemote.transact(16, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method setRadioPowerResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.modem.IRadioModemResponse
            public void getImeiResponse(RadioResponseInfo responseInfo, ImeiInfo imeiInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(responseInfo, 0);
                    _data.writeTypedObject(imeiInfo, 0);
                    boolean _status = this.mRemote.transact(17, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method getImeiResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.modem.IRadioModemResponse
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

            @Override // android.hardware.radio.modem.IRadioModemResponse
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
