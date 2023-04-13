package android.hardware.radio.satellite;

import android.hardware.radio.RadioResponseInfo;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes2.dex */
public interface IRadioSatelliteResponse extends IInterface {
    public static final String DESCRIPTOR = "android$hardware$radio$satellite$IRadioSatelliteResponse".replace('$', '.');
    public static final String HASH = "notfrozen";
    public static final int VERSION = 1;

    void acknowledgeRequest(int i) throws RemoteException;

    void addAllowedSatelliteContactsResponse(RadioResponseInfo radioResponseInfo) throws RemoteException;

    void getCapabilitiesResponse(RadioResponseInfo radioResponseInfo, SatelliteCapabilities satelliteCapabilities) throws RemoteException;

    String getInterfaceHash() throws RemoteException;

    int getInterfaceVersion() throws RemoteException;

    void getMaxCharactersPerTextMessageResponse(RadioResponseInfo radioResponseInfo, int i) throws RemoteException;

    void getPendingMessagesResponse(RadioResponseInfo radioResponseInfo, String[] strArr) throws RemoteException;

    void getPowerStateResponse(RadioResponseInfo radioResponseInfo, boolean z) throws RemoteException;

    void getSatelliteModeResponse(RadioResponseInfo radioResponseInfo, int i, int i2) throws RemoteException;

    void getTimeForNextSatelliteVisibilityResponse(RadioResponseInfo radioResponseInfo, int i) throws RemoteException;

    void provisionServiceResponse(RadioResponseInfo radioResponseInfo, boolean z) throws RemoteException;

    void removeAllowedSatelliteContactsResponse(RadioResponseInfo radioResponseInfo) throws RemoteException;

    void sendMessagesResponse(RadioResponseInfo radioResponseInfo) throws RemoteException;

    void setIndicationFilterResponse(RadioResponseInfo radioResponseInfo) throws RemoteException;

    void setPowerResponse(RadioResponseInfo radioResponseInfo) throws RemoteException;

    void startSendingSatellitePointingInfoResponse(RadioResponseInfo radioResponseInfo) throws RemoteException;

    void stopSendingSatellitePointingInfoResponse(RadioResponseInfo radioResponseInfo) throws RemoteException;

    /* loaded from: classes2.dex */
    public static class Default implements IRadioSatelliteResponse {
        @Override // android.hardware.radio.satellite.IRadioSatelliteResponse
        public void acknowledgeRequest(int serial) throws RemoteException {
        }

        @Override // android.hardware.radio.satellite.IRadioSatelliteResponse
        public void addAllowedSatelliteContactsResponse(RadioResponseInfo info) throws RemoteException {
        }

        @Override // android.hardware.radio.satellite.IRadioSatelliteResponse
        public void getCapabilitiesResponse(RadioResponseInfo info, SatelliteCapabilities capabilities) throws RemoteException {
        }

        @Override // android.hardware.radio.satellite.IRadioSatelliteResponse
        public void getMaxCharactersPerTextMessageResponse(RadioResponseInfo info, int charLimit) throws RemoteException {
        }

        @Override // android.hardware.radio.satellite.IRadioSatelliteResponse
        public void getPendingMessagesResponse(RadioResponseInfo info, String[] messages) throws RemoteException {
        }

        @Override // android.hardware.radio.satellite.IRadioSatelliteResponse
        public void getPowerStateResponse(RadioResponseInfo info, boolean on) throws RemoteException {
        }

        @Override // android.hardware.radio.satellite.IRadioSatelliteResponse
        public void getSatelliteModeResponse(RadioResponseInfo info, int mode, int technology) throws RemoteException {
        }

        @Override // android.hardware.radio.satellite.IRadioSatelliteResponse
        public void getTimeForNextSatelliteVisibilityResponse(RadioResponseInfo info, int timeInSeconds) throws RemoteException {
        }

        @Override // android.hardware.radio.satellite.IRadioSatelliteResponse
        public void provisionServiceResponse(RadioResponseInfo info, boolean provisioned) throws RemoteException {
        }

        @Override // android.hardware.radio.satellite.IRadioSatelliteResponse
        public void removeAllowedSatelliteContactsResponse(RadioResponseInfo info) throws RemoteException {
        }

        @Override // android.hardware.radio.satellite.IRadioSatelliteResponse
        public void sendMessagesResponse(RadioResponseInfo info) throws RemoteException {
        }

        @Override // android.hardware.radio.satellite.IRadioSatelliteResponse
        public void setIndicationFilterResponse(RadioResponseInfo info) throws RemoteException {
        }

        @Override // android.hardware.radio.satellite.IRadioSatelliteResponse
        public void setPowerResponse(RadioResponseInfo info) throws RemoteException {
        }

        @Override // android.hardware.radio.satellite.IRadioSatelliteResponse
        public void startSendingSatellitePointingInfoResponse(RadioResponseInfo info) throws RemoteException {
        }

        @Override // android.hardware.radio.satellite.IRadioSatelliteResponse
        public void stopSendingSatellitePointingInfoResponse(RadioResponseInfo info) throws RemoteException {
        }

        @Override // android.hardware.radio.satellite.IRadioSatelliteResponse
        public int getInterfaceVersion() {
            return 0;
        }

        @Override // android.hardware.radio.satellite.IRadioSatelliteResponse
        public String getInterfaceHash() {
            return "";
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements IRadioSatelliteResponse {
        static final int TRANSACTION_acknowledgeRequest = 1;
        static final int TRANSACTION_addAllowedSatelliteContactsResponse = 2;
        static final int TRANSACTION_getCapabilitiesResponse = 3;
        static final int TRANSACTION_getInterfaceHash = 16777214;
        static final int TRANSACTION_getInterfaceVersion = 16777215;
        static final int TRANSACTION_getMaxCharactersPerTextMessageResponse = 4;
        static final int TRANSACTION_getPendingMessagesResponse = 5;
        static final int TRANSACTION_getPowerStateResponse = 6;
        static final int TRANSACTION_getSatelliteModeResponse = 7;
        static final int TRANSACTION_getTimeForNextSatelliteVisibilityResponse = 8;
        static final int TRANSACTION_provisionServiceResponse = 9;
        static final int TRANSACTION_removeAllowedSatelliteContactsResponse = 10;
        static final int TRANSACTION_sendMessagesResponse = 11;
        static final int TRANSACTION_setIndicationFilterResponse = 12;
        static final int TRANSACTION_setPowerResponse = 13;
        static final int TRANSACTION_startSendingSatellitePointingInfoResponse = 14;
        static final int TRANSACTION_stopSendingSatellitePointingInfoResponse = 15;

        public Stub() {
            markVintfStability();
            attachInterface(this, DESCRIPTOR);
        }

        public static IRadioSatelliteResponse asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IRadioSatelliteResponse)) {
                return (IRadioSatelliteResponse) iin;
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
                            addAllowedSatelliteContactsResponse(_arg02);
                            break;
                        case 3:
                            RadioResponseInfo _arg03 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            SatelliteCapabilities _arg1 = (SatelliteCapabilities) data.readTypedObject(SatelliteCapabilities.CREATOR);
                            data.enforceNoDataAvail();
                            getCapabilitiesResponse(_arg03, _arg1);
                            break;
                        case 4:
                            RadioResponseInfo _arg04 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            int _arg12 = data.readInt();
                            data.enforceNoDataAvail();
                            getMaxCharactersPerTextMessageResponse(_arg04, _arg12);
                            break;
                        case 5:
                            RadioResponseInfo _arg05 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            String[] _arg13 = data.createStringArray();
                            data.enforceNoDataAvail();
                            getPendingMessagesResponse(_arg05, _arg13);
                            break;
                        case 6:
                            RadioResponseInfo _arg06 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            boolean _arg14 = data.readBoolean();
                            data.enforceNoDataAvail();
                            getPowerStateResponse(_arg06, _arg14);
                            break;
                        case 7:
                            RadioResponseInfo _arg07 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            int _arg15 = data.readInt();
                            int _arg2 = data.readInt();
                            data.enforceNoDataAvail();
                            getSatelliteModeResponse(_arg07, _arg15, _arg2);
                            break;
                        case 8:
                            RadioResponseInfo _arg08 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            int _arg16 = data.readInt();
                            data.enforceNoDataAvail();
                            getTimeForNextSatelliteVisibilityResponse(_arg08, _arg16);
                            break;
                        case 9:
                            RadioResponseInfo _arg09 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            boolean _arg17 = data.readBoolean();
                            data.enforceNoDataAvail();
                            provisionServiceResponse(_arg09, _arg17);
                            break;
                        case 10:
                            RadioResponseInfo _arg010 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            data.enforceNoDataAvail();
                            removeAllowedSatelliteContactsResponse(_arg010);
                            break;
                        case 11:
                            RadioResponseInfo _arg011 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            data.enforceNoDataAvail();
                            sendMessagesResponse(_arg011);
                            break;
                        case 12:
                            RadioResponseInfo _arg012 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            data.enforceNoDataAvail();
                            setIndicationFilterResponse(_arg012);
                            break;
                        case 13:
                            RadioResponseInfo _arg013 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            data.enforceNoDataAvail();
                            setPowerResponse(_arg013);
                            break;
                        case 14:
                            RadioResponseInfo _arg014 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            data.enforceNoDataAvail();
                            startSendingSatellitePointingInfoResponse(_arg014);
                            break;
                        case 15:
                            RadioResponseInfo _arg015 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            data.enforceNoDataAvail();
                            stopSendingSatellitePointingInfoResponse(_arg015);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes2.dex */
        private static class Proxy implements IRadioSatelliteResponse {
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

            @Override // android.hardware.radio.satellite.IRadioSatelliteResponse
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

            @Override // android.hardware.radio.satellite.IRadioSatelliteResponse
            public void addAllowedSatelliteContactsResponse(RadioResponseInfo info) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    boolean _status = this.mRemote.transact(2, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method addAllowedSatelliteContactsResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.satellite.IRadioSatelliteResponse
            public void getCapabilitiesResponse(RadioResponseInfo info, SatelliteCapabilities capabilities) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    _data.writeTypedObject(capabilities, 0);
                    boolean _status = this.mRemote.transact(3, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method getCapabilitiesResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.satellite.IRadioSatelliteResponse
            public void getMaxCharactersPerTextMessageResponse(RadioResponseInfo info, int charLimit) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    _data.writeInt(charLimit);
                    boolean _status = this.mRemote.transact(4, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method getMaxCharactersPerTextMessageResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.satellite.IRadioSatelliteResponse
            public void getPendingMessagesResponse(RadioResponseInfo info, String[] messages) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    _data.writeStringArray(messages);
                    boolean _status = this.mRemote.transact(5, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method getPendingMessagesResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.satellite.IRadioSatelliteResponse
            public void getPowerStateResponse(RadioResponseInfo info, boolean on) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    _data.writeBoolean(on);
                    boolean _status = this.mRemote.transact(6, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method getPowerStateResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.satellite.IRadioSatelliteResponse
            public void getSatelliteModeResponse(RadioResponseInfo info, int mode, int technology) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    _data.writeInt(mode);
                    _data.writeInt(technology);
                    boolean _status = this.mRemote.transact(7, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method getSatelliteModeResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.satellite.IRadioSatelliteResponse
            public void getTimeForNextSatelliteVisibilityResponse(RadioResponseInfo info, int timeInSeconds) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    _data.writeInt(timeInSeconds);
                    boolean _status = this.mRemote.transact(8, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method getTimeForNextSatelliteVisibilityResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.satellite.IRadioSatelliteResponse
            public void provisionServiceResponse(RadioResponseInfo info, boolean provisioned) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    _data.writeBoolean(provisioned);
                    boolean _status = this.mRemote.transact(9, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method provisionServiceResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.satellite.IRadioSatelliteResponse
            public void removeAllowedSatelliteContactsResponse(RadioResponseInfo info) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    boolean _status = this.mRemote.transact(10, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method removeAllowedSatelliteContactsResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.satellite.IRadioSatelliteResponse
            public void sendMessagesResponse(RadioResponseInfo info) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    boolean _status = this.mRemote.transact(11, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method sendMessagesResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.satellite.IRadioSatelliteResponse
            public void setIndicationFilterResponse(RadioResponseInfo info) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    boolean _status = this.mRemote.transact(12, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method setIndicationFilterResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.satellite.IRadioSatelliteResponse
            public void setPowerResponse(RadioResponseInfo info) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    boolean _status = this.mRemote.transact(13, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method setPowerResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.satellite.IRadioSatelliteResponse
            public void startSendingSatellitePointingInfoResponse(RadioResponseInfo info) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    boolean _status = this.mRemote.transact(14, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method startSendingSatellitePointingInfoResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.satellite.IRadioSatelliteResponse
            public void stopSendingSatellitePointingInfoResponse(RadioResponseInfo info) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    boolean _status = this.mRemote.transact(15, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method stopSendingSatellitePointingInfoResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.satellite.IRadioSatelliteResponse
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

            @Override // android.hardware.radio.satellite.IRadioSatelliteResponse
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
