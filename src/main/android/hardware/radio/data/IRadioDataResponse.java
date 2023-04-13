package android.hardware.radio.data;

import android.hardware.radio.RadioResponseInfo;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes2.dex */
public interface IRadioDataResponse extends IInterface {
    public static final String DESCRIPTOR = "android$hardware$radio$data$IRadioDataResponse".replace('$', '.');
    public static final String HASH = "notfrozen";
    public static final int VERSION = 2;

    void acknowledgeRequest(int i) throws RemoteException;

    void allocatePduSessionIdResponse(RadioResponseInfo radioResponseInfo, int i) throws RemoteException;

    void cancelHandoverResponse(RadioResponseInfo radioResponseInfo) throws RemoteException;

    void deactivateDataCallResponse(RadioResponseInfo radioResponseInfo) throws RemoteException;

    void getDataCallListResponse(RadioResponseInfo radioResponseInfo, SetupDataCallResult[] setupDataCallResultArr) throws RemoteException;

    String getInterfaceHash() throws RemoteException;

    int getInterfaceVersion() throws RemoteException;

    void getSlicingConfigResponse(RadioResponseInfo radioResponseInfo, SlicingConfig slicingConfig) throws RemoteException;

    void releasePduSessionIdResponse(RadioResponseInfo radioResponseInfo) throws RemoteException;

    void setDataAllowedResponse(RadioResponseInfo radioResponseInfo) throws RemoteException;

    void setDataProfileResponse(RadioResponseInfo radioResponseInfo) throws RemoteException;

    void setDataThrottlingResponse(RadioResponseInfo radioResponseInfo) throws RemoteException;

    void setInitialAttachApnResponse(RadioResponseInfo radioResponseInfo) throws RemoteException;

    void setupDataCallResponse(RadioResponseInfo radioResponseInfo, SetupDataCallResult setupDataCallResult) throws RemoteException;

    void startHandoverResponse(RadioResponseInfo radioResponseInfo) throws RemoteException;

    void startKeepaliveResponse(RadioResponseInfo radioResponseInfo, KeepaliveStatus keepaliveStatus) throws RemoteException;

    void stopKeepaliveResponse(RadioResponseInfo radioResponseInfo) throws RemoteException;

    /* loaded from: classes2.dex */
    public static class Default implements IRadioDataResponse {
        @Override // android.hardware.radio.data.IRadioDataResponse
        public void acknowledgeRequest(int serial) throws RemoteException {
        }

        @Override // android.hardware.radio.data.IRadioDataResponse
        public void allocatePduSessionIdResponse(RadioResponseInfo info, int id) throws RemoteException {
        }

        @Override // android.hardware.radio.data.IRadioDataResponse
        public void cancelHandoverResponse(RadioResponseInfo info) throws RemoteException {
        }

        @Override // android.hardware.radio.data.IRadioDataResponse
        public void deactivateDataCallResponse(RadioResponseInfo info) throws RemoteException {
        }

        @Override // android.hardware.radio.data.IRadioDataResponse
        public void getDataCallListResponse(RadioResponseInfo info, SetupDataCallResult[] dcResponse) throws RemoteException {
        }

        @Override // android.hardware.radio.data.IRadioDataResponse
        public void getSlicingConfigResponse(RadioResponseInfo info, SlicingConfig slicingConfig) throws RemoteException {
        }

        @Override // android.hardware.radio.data.IRadioDataResponse
        public void releasePduSessionIdResponse(RadioResponseInfo info) throws RemoteException {
        }

        @Override // android.hardware.radio.data.IRadioDataResponse
        public void setDataAllowedResponse(RadioResponseInfo info) throws RemoteException {
        }

        @Override // android.hardware.radio.data.IRadioDataResponse
        public void setDataProfileResponse(RadioResponseInfo info) throws RemoteException {
        }

        @Override // android.hardware.radio.data.IRadioDataResponse
        public void setDataThrottlingResponse(RadioResponseInfo info) throws RemoteException {
        }

        @Override // android.hardware.radio.data.IRadioDataResponse
        public void setInitialAttachApnResponse(RadioResponseInfo info) throws RemoteException {
        }

        @Override // android.hardware.radio.data.IRadioDataResponse
        public void setupDataCallResponse(RadioResponseInfo info, SetupDataCallResult dcResponse) throws RemoteException {
        }

        @Override // android.hardware.radio.data.IRadioDataResponse
        public void startHandoverResponse(RadioResponseInfo info) throws RemoteException {
        }

        @Override // android.hardware.radio.data.IRadioDataResponse
        public void startKeepaliveResponse(RadioResponseInfo info, KeepaliveStatus status) throws RemoteException {
        }

        @Override // android.hardware.radio.data.IRadioDataResponse
        public void stopKeepaliveResponse(RadioResponseInfo info) throws RemoteException {
        }

        @Override // android.hardware.radio.data.IRadioDataResponse
        public int getInterfaceVersion() {
            return 0;
        }

        @Override // android.hardware.radio.data.IRadioDataResponse
        public String getInterfaceHash() {
            return "";
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements IRadioDataResponse {
        static final int TRANSACTION_acknowledgeRequest = 1;
        static final int TRANSACTION_allocatePduSessionIdResponse = 2;
        static final int TRANSACTION_cancelHandoverResponse = 3;
        static final int TRANSACTION_deactivateDataCallResponse = 4;
        static final int TRANSACTION_getDataCallListResponse = 5;
        static final int TRANSACTION_getInterfaceHash = 16777214;
        static final int TRANSACTION_getInterfaceVersion = 16777215;
        static final int TRANSACTION_getSlicingConfigResponse = 6;
        static final int TRANSACTION_releasePduSessionIdResponse = 7;
        static final int TRANSACTION_setDataAllowedResponse = 8;
        static final int TRANSACTION_setDataProfileResponse = 9;
        static final int TRANSACTION_setDataThrottlingResponse = 10;
        static final int TRANSACTION_setInitialAttachApnResponse = 11;
        static final int TRANSACTION_setupDataCallResponse = 12;
        static final int TRANSACTION_startHandoverResponse = 13;
        static final int TRANSACTION_startKeepaliveResponse = 14;
        static final int TRANSACTION_stopKeepaliveResponse = 15;

        public Stub() {
            markVintfStability();
            attachInterface(this, DESCRIPTOR);
        }

        public static IRadioDataResponse asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IRadioDataResponse)) {
                return (IRadioDataResponse) iin;
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
                            int _arg1 = data.readInt();
                            data.enforceNoDataAvail();
                            allocatePduSessionIdResponse(_arg02, _arg1);
                            break;
                        case 3:
                            RadioResponseInfo _arg03 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            data.enforceNoDataAvail();
                            cancelHandoverResponse(_arg03);
                            break;
                        case 4:
                            RadioResponseInfo _arg04 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            data.enforceNoDataAvail();
                            deactivateDataCallResponse(_arg04);
                            break;
                        case 5:
                            RadioResponseInfo _arg05 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            SetupDataCallResult[] _arg12 = (SetupDataCallResult[]) data.createTypedArray(SetupDataCallResult.CREATOR);
                            data.enforceNoDataAvail();
                            getDataCallListResponse(_arg05, _arg12);
                            break;
                        case 6:
                            RadioResponseInfo _arg06 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            SlicingConfig _arg13 = (SlicingConfig) data.readTypedObject(SlicingConfig.CREATOR);
                            data.enforceNoDataAvail();
                            getSlicingConfigResponse(_arg06, _arg13);
                            break;
                        case 7:
                            RadioResponseInfo _arg07 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            data.enforceNoDataAvail();
                            releasePduSessionIdResponse(_arg07);
                            break;
                        case 8:
                            RadioResponseInfo _arg08 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            data.enforceNoDataAvail();
                            setDataAllowedResponse(_arg08);
                            break;
                        case 9:
                            RadioResponseInfo _arg09 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            data.enforceNoDataAvail();
                            setDataProfileResponse(_arg09);
                            break;
                        case 10:
                            RadioResponseInfo _arg010 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            data.enforceNoDataAvail();
                            setDataThrottlingResponse(_arg010);
                            break;
                        case 11:
                            RadioResponseInfo _arg011 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            data.enforceNoDataAvail();
                            setInitialAttachApnResponse(_arg011);
                            break;
                        case 12:
                            RadioResponseInfo _arg012 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            SetupDataCallResult _arg14 = (SetupDataCallResult) data.readTypedObject(SetupDataCallResult.CREATOR);
                            data.enforceNoDataAvail();
                            setupDataCallResponse(_arg012, _arg14);
                            break;
                        case 13:
                            RadioResponseInfo _arg013 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            data.enforceNoDataAvail();
                            startHandoverResponse(_arg013);
                            break;
                        case 14:
                            RadioResponseInfo _arg014 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            KeepaliveStatus _arg15 = (KeepaliveStatus) data.readTypedObject(KeepaliveStatus.CREATOR);
                            data.enforceNoDataAvail();
                            startKeepaliveResponse(_arg014, _arg15);
                            break;
                        case 15:
                            RadioResponseInfo _arg015 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            data.enforceNoDataAvail();
                            stopKeepaliveResponse(_arg015);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes2.dex */
        private static class Proxy implements IRadioDataResponse {
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

            @Override // android.hardware.radio.data.IRadioDataResponse
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

            @Override // android.hardware.radio.data.IRadioDataResponse
            public void allocatePduSessionIdResponse(RadioResponseInfo info, int id) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    _data.writeInt(id);
                    boolean _status = this.mRemote.transact(2, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method allocatePduSessionIdResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.data.IRadioDataResponse
            public void cancelHandoverResponse(RadioResponseInfo info) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    boolean _status = this.mRemote.transact(3, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method cancelHandoverResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.data.IRadioDataResponse
            public void deactivateDataCallResponse(RadioResponseInfo info) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    boolean _status = this.mRemote.transact(4, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method deactivateDataCallResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.data.IRadioDataResponse
            public void getDataCallListResponse(RadioResponseInfo info, SetupDataCallResult[] dcResponse) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    _data.writeTypedArray(dcResponse, 0);
                    boolean _status = this.mRemote.transact(5, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method getDataCallListResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.data.IRadioDataResponse
            public void getSlicingConfigResponse(RadioResponseInfo info, SlicingConfig slicingConfig) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    _data.writeTypedObject(slicingConfig, 0);
                    boolean _status = this.mRemote.transact(6, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method getSlicingConfigResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.data.IRadioDataResponse
            public void releasePduSessionIdResponse(RadioResponseInfo info) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    boolean _status = this.mRemote.transact(7, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method releasePduSessionIdResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.data.IRadioDataResponse
            public void setDataAllowedResponse(RadioResponseInfo info) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    boolean _status = this.mRemote.transact(8, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method setDataAllowedResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.data.IRadioDataResponse
            public void setDataProfileResponse(RadioResponseInfo info) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    boolean _status = this.mRemote.transact(9, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method setDataProfileResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.data.IRadioDataResponse
            public void setDataThrottlingResponse(RadioResponseInfo info) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    boolean _status = this.mRemote.transact(10, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method setDataThrottlingResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.data.IRadioDataResponse
            public void setInitialAttachApnResponse(RadioResponseInfo info) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    boolean _status = this.mRemote.transact(11, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method setInitialAttachApnResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.data.IRadioDataResponse
            public void setupDataCallResponse(RadioResponseInfo info, SetupDataCallResult dcResponse) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    _data.writeTypedObject(dcResponse, 0);
                    boolean _status = this.mRemote.transact(12, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method setupDataCallResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.data.IRadioDataResponse
            public void startHandoverResponse(RadioResponseInfo info) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    boolean _status = this.mRemote.transact(13, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method startHandoverResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.data.IRadioDataResponse
            public void startKeepaliveResponse(RadioResponseInfo info, KeepaliveStatus status) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    _data.writeTypedObject(status, 0);
                    boolean _status = this.mRemote.transact(14, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method startKeepaliveResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.data.IRadioDataResponse
            public void stopKeepaliveResponse(RadioResponseInfo info) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    boolean _status = this.mRemote.transact(15, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method stopKeepaliveResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.data.IRadioDataResponse
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

            @Override // android.hardware.radio.data.IRadioDataResponse
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
