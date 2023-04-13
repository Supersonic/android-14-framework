package android.hardware.radio.config;

import android.hardware.radio.RadioResponseInfo;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes2.dex */
public interface IRadioConfigResponse extends IInterface {
    public static final String DESCRIPTOR = "android$hardware$radio$config$IRadioConfigResponse".replace('$', '.');
    public static final String HASH = "notfrozen";
    public static final int VERSION = 2;

    void getHalDeviceCapabilitiesResponse(RadioResponseInfo radioResponseInfo, boolean z) throws RemoteException;

    String getInterfaceHash() throws RemoteException;

    int getInterfaceVersion() throws RemoteException;

    void getNumOfLiveModemsResponse(RadioResponseInfo radioResponseInfo, byte b) throws RemoteException;

    void getPhoneCapabilityResponse(RadioResponseInfo radioResponseInfo, PhoneCapability phoneCapability) throws RemoteException;

    void getSimSlotsStatusResponse(RadioResponseInfo radioResponseInfo, SimSlotStatus[] simSlotStatusArr) throws RemoteException;

    void setNumOfLiveModemsResponse(RadioResponseInfo radioResponseInfo) throws RemoteException;

    void setPreferredDataModemResponse(RadioResponseInfo radioResponseInfo) throws RemoteException;

    void setSimSlotsMappingResponse(RadioResponseInfo radioResponseInfo) throws RemoteException;

    /* loaded from: classes2.dex */
    public static class Default implements IRadioConfigResponse {
        @Override // android.hardware.radio.config.IRadioConfigResponse
        public void getHalDeviceCapabilitiesResponse(RadioResponseInfo info, boolean modemReducedFeatureSet1) throws RemoteException {
        }

        @Override // android.hardware.radio.config.IRadioConfigResponse
        public void getNumOfLiveModemsResponse(RadioResponseInfo info, byte numOfLiveModems) throws RemoteException {
        }

        @Override // android.hardware.radio.config.IRadioConfigResponse
        public void getPhoneCapabilityResponse(RadioResponseInfo info, PhoneCapability phoneCapability) throws RemoteException {
        }

        @Override // android.hardware.radio.config.IRadioConfigResponse
        public void getSimSlotsStatusResponse(RadioResponseInfo info, SimSlotStatus[] slotStatus) throws RemoteException {
        }

        @Override // android.hardware.radio.config.IRadioConfigResponse
        public void setNumOfLiveModemsResponse(RadioResponseInfo info) throws RemoteException {
        }

        @Override // android.hardware.radio.config.IRadioConfigResponse
        public void setPreferredDataModemResponse(RadioResponseInfo info) throws RemoteException {
        }

        @Override // android.hardware.radio.config.IRadioConfigResponse
        public void setSimSlotsMappingResponse(RadioResponseInfo info) throws RemoteException {
        }

        @Override // android.hardware.radio.config.IRadioConfigResponse
        public int getInterfaceVersion() {
            return 0;
        }

        @Override // android.hardware.radio.config.IRadioConfigResponse
        public String getInterfaceHash() {
            return "";
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements IRadioConfigResponse {
        static final int TRANSACTION_getHalDeviceCapabilitiesResponse = 1;
        static final int TRANSACTION_getInterfaceHash = 16777214;
        static final int TRANSACTION_getInterfaceVersion = 16777215;
        static final int TRANSACTION_getNumOfLiveModemsResponse = 2;
        static final int TRANSACTION_getPhoneCapabilityResponse = 3;
        static final int TRANSACTION_getSimSlotsStatusResponse = 4;
        static final int TRANSACTION_setNumOfLiveModemsResponse = 5;
        static final int TRANSACTION_setPreferredDataModemResponse = 6;
        static final int TRANSACTION_setSimSlotsMappingResponse = 7;

        public Stub() {
            markVintfStability();
            attachInterface(this, DESCRIPTOR);
        }

        public static IRadioConfigResponse asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IRadioConfigResponse)) {
                return (IRadioConfigResponse) iin;
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
                            boolean _arg1 = data.readBoolean();
                            data.enforceNoDataAvail();
                            getHalDeviceCapabilitiesResponse(_arg0, _arg1);
                            break;
                        case 2:
                            RadioResponseInfo _arg02 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            byte _arg12 = data.readByte();
                            data.enforceNoDataAvail();
                            getNumOfLiveModemsResponse(_arg02, _arg12);
                            break;
                        case 3:
                            RadioResponseInfo _arg03 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            PhoneCapability _arg13 = (PhoneCapability) data.readTypedObject(PhoneCapability.CREATOR);
                            data.enforceNoDataAvail();
                            getPhoneCapabilityResponse(_arg03, _arg13);
                            break;
                        case 4:
                            RadioResponseInfo _arg04 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            SimSlotStatus[] _arg14 = (SimSlotStatus[]) data.createTypedArray(SimSlotStatus.CREATOR);
                            data.enforceNoDataAvail();
                            getSimSlotsStatusResponse(_arg04, _arg14);
                            break;
                        case 5:
                            RadioResponseInfo _arg05 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            data.enforceNoDataAvail();
                            setNumOfLiveModemsResponse(_arg05);
                            break;
                        case 6:
                            RadioResponseInfo _arg06 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            data.enforceNoDataAvail();
                            setPreferredDataModemResponse(_arg06);
                            break;
                        case 7:
                            RadioResponseInfo _arg07 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            data.enforceNoDataAvail();
                            setSimSlotsMappingResponse(_arg07);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes2.dex */
        private static class Proxy implements IRadioConfigResponse {
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

            @Override // android.hardware.radio.config.IRadioConfigResponse
            public void getHalDeviceCapabilitiesResponse(RadioResponseInfo info, boolean modemReducedFeatureSet1) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    _data.writeBoolean(modemReducedFeatureSet1);
                    boolean _status = this.mRemote.transact(1, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method getHalDeviceCapabilitiesResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.config.IRadioConfigResponse
            public void getNumOfLiveModemsResponse(RadioResponseInfo info, byte numOfLiveModems) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    _data.writeByte(numOfLiveModems);
                    boolean _status = this.mRemote.transact(2, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method getNumOfLiveModemsResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.config.IRadioConfigResponse
            public void getPhoneCapabilityResponse(RadioResponseInfo info, PhoneCapability phoneCapability) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    _data.writeTypedObject(phoneCapability, 0);
                    boolean _status = this.mRemote.transact(3, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method getPhoneCapabilityResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.config.IRadioConfigResponse
            public void getSimSlotsStatusResponse(RadioResponseInfo info, SimSlotStatus[] slotStatus) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    _data.writeTypedArray(slotStatus, 0);
                    boolean _status = this.mRemote.transact(4, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method getSimSlotsStatusResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.config.IRadioConfigResponse
            public void setNumOfLiveModemsResponse(RadioResponseInfo info) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    boolean _status = this.mRemote.transact(5, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method setNumOfLiveModemsResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.config.IRadioConfigResponse
            public void setPreferredDataModemResponse(RadioResponseInfo info) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    boolean _status = this.mRemote.transact(6, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method setPreferredDataModemResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.config.IRadioConfigResponse
            public void setSimSlotsMappingResponse(RadioResponseInfo info) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    boolean _status = this.mRemote.transact(7, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method setSimSlotsMappingResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.config.IRadioConfigResponse
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

            @Override // android.hardware.radio.config.IRadioConfigResponse
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
