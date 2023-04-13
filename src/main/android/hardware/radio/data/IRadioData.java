package android.hardware.radio.data;

import android.hardware.radio.data.IRadioDataIndication;
import android.hardware.radio.data.IRadioDataResponse;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes2.dex */
public interface IRadioData extends IInterface {
    public static final String DESCRIPTOR = "android$hardware$radio$data$IRadioData".replace('$', '.');
    public static final String HASH = "notfrozen";
    public static final int VERSION = 2;

    void allocatePduSessionId(int i) throws RemoteException;

    void cancelHandover(int i, int i2) throws RemoteException;

    void deactivateDataCall(int i, int i2, int i3) throws RemoteException;

    void getDataCallList(int i) throws RemoteException;

    String getInterfaceHash() throws RemoteException;

    int getInterfaceVersion() throws RemoteException;

    void getSlicingConfig(int i) throws RemoteException;

    void releasePduSessionId(int i, int i2) throws RemoteException;

    void responseAcknowledgement() throws RemoteException;

    void setDataAllowed(int i, boolean z) throws RemoteException;

    void setDataProfile(int i, DataProfileInfo[] dataProfileInfoArr) throws RemoteException;

    void setDataThrottling(int i, byte b, long j) throws RemoteException;

    void setInitialAttachApn(int i, DataProfileInfo dataProfileInfo) throws RemoteException;

    void setResponseFunctions(IRadioDataResponse iRadioDataResponse, IRadioDataIndication iRadioDataIndication) throws RemoteException;

    void setupDataCall(int i, int i2, DataProfileInfo dataProfileInfo, boolean z, int i3, LinkAddress[] linkAddressArr, String[] strArr, int i4, SliceInfo sliceInfo, boolean z2) throws RemoteException;

    void startHandover(int i, int i2) throws RemoteException;

    void startKeepalive(int i, KeepaliveRequest keepaliveRequest) throws RemoteException;

    void stopKeepalive(int i, int i2) throws RemoteException;

    /* loaded from: classes2.dex */
    public static class Default implements IRadioData {
        @Override // android.hardware.radio.data.IRadioData
        public void allocatePduSessionId(int serial) throws RemoteException {
        }

        @Override // android.hardware.radio.data.IRadioData
        public void cancelHandover(int serial, int callId) throws RemoteException {
        }

        @Override // android.hardware.radio.data.IRadioData
        public void deactivateDataCall(int serial, int cid, int reason) throws RemoteException {
        }

        @Override // android.hardware.radio.data.IRadioData
        public void getDataCallList(int serial) throws RemoteException {
        }

        @Override // android.hardware.radio.data.IRadioData
        public void getSlicingConfig(int serial) throws RemoteException {
        }

        @Override // android.hardware.radio.data.IRadioData
        public void releasePduSessionId(int serial, int id) throws RemoteException {
        }

        @Override // android.hardware.radio.data.IRadioData
        public void responseAcknowledgement() throws RemoteException {
        }

        @Override // android.hardware.radio.data.IRadioData
        public void setDataAllowed(int serial, boolean allow) throws RemoteException {
        }

        @Override // android.hardware.radio.data.IRadioData
        public void setDataProfile(int serial, DataProfileInfo[] profiles) throws RemoteException {
        }

        @Override // android.hardware.radio.data.IRadioData
        public void setDataThrottling(int serial, byte dataThrottlingAction, long completionDurationMillis) throws RemoteException {
        }

        @Override // android.hardware.radio.data.IRadioData
        public void setInitialAttachApn(int serial, DataProfileInfo dataProfileInfo) throws RemoteException {
        }

        @Override // android.hardware.radio.data.IRadioData
        public void setResponseFunctions(IRadioDataResponse radioDataResponse, IRadioDataIndication radioDataIndication) throws RemoteException {
        }

        @Override // android.hardware.radio.data.IRadioData
        public void setupDataCall(int serial, int accessNetwork, DataProfileInfo dataProfileInfo, boolean roamingAllowed, int reason, LinkAddress[] addresses, String[] dnses, int pduSessionId, SliceInfo sliceInfo, boolean matchAllRuleAllowed) throws RemoteException {
        }

        @Override // android.hardware.radio.data.IRadioData
        public void startHandover(int serial, int callId) throws RemoteException {
        }

        @Override // android.hardware.radio.data.IRadioData
        public void startKeepalive(int serial, KeepaliveRequest keepalive) throws RemoteException {
        }

        @Override // android.hardware.radio.data.IRadioData
        public void stopKeepalive(int serial, int sessionHandle) throws RemoteException {
        }

        @Override // android.hardware.radio.data.IRadioData
        public int getInterfaceVersion() {
            return 0;
        }

        @Override // android.hardware.radio.data.IRadioData
        public String getInterfaceHash() {
            return "";
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements IRadioData {
        static final int TRANSACTION_allocatePduSessionId = 1;
        static final int TRANSACTION_cancelHandover = 2;
        static final int TRANSACTION_deactivateDataCall = 3;
        static final int TRANSACTION_getDataCallList = 4;
        static final int TRANSACTION_getInterfaceHash = 16777214;
        static final int TRANSACTION_getInterfaceVersion = 16777215;
        static final int TRANSACTION_getSlicingConfig = 5;
        static final int TRANSACTION_releasePduSessionId = 6;
        static final int TRANSACTION_responseAcknowledgement = 7;
        static final int TRANSACTION_setDataAllowed = 8;
        static final int TRANSACTION_setDataProfile = 9;
        static final int TRANSACTION_setDataThrottling = 10;
        static final int TRANSACTION_setInitialAttachApn = 11;
        static final int TRANSACTION_setResponseFunctions = 12;
        static final int TRANSACTION_setupDataCall = 13;
        static final int TRANSACTION_startHandover = 14;
        static final int TRANSACTION_startKeepalive = 15;
        static final int TRANSACTION_stopKeepalive = 16;

        public Stub() {
            markVintfStability();
            attachInterface(this, DESCRIPTOR);
        }

        public static IRadioData asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IRadioData)) {
                return (IRadioData) iin;
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
                            allocatePduSessionId(_arg0);
                            return true;
                        case 2:
                            int _arg02 = data.readInt();
                            int _arg1 = data.readInt();
                            data.enforceNoDataAvail();
                            cancelHandover(_arg02, _arg1);
                            return true;
                        case 3:
                            int _arg03 = data.readInt();
                            int _arg12 = data.readInt();
                            int _arg2 = data.readInt();
                            data.enforceNoDataAvail();
                            deactivateDataCall(_arg03, _arg12, _arg2);
                            return true;
                        case 4:
                            int _arg04 = data.readInt();
                            data.enforceNoDataAvail();
                            getDataCallList(_arg04);
                            return true;
                        case 5:
                            int _arg05 = data.readInt();
                            data.enforceNoDataAvail();
                            getSlicingConfig(_arg05);
                            return true;
                        case 6:
                            int _arg06 = data.readInt();
                            int _arg13 = data.readInt();
                            data.enforceNoDataAvail();
                            releasePduSessionId(_arg06, _arg13);
                            return true;
                        case 7:
                            responseAcknowledgement();
                            return true;
                        case 8:
                            int _arg07 = data.readInt();
                            boolean _arg14 = data.readBoolean();
                            data.enforceNoDataAvail();
                            setDataAllowed(_arg07, _arg14);
                            return true;
                        case 9:
                            int _arg08 = data.readInt();
                            DataProfileInfo[] _arg15 = (DataProfileInfo[]) data.createTypedArray(DataProfileInfo.CREATOR);
                            data.enforceNoDataAvail();
                            setDataProfile(_arg08, _arg15);
                            return true;
                        case 10:
                            int _arg09 = data.readInt();
                            byte _arg16 = data.readByte();
                            long _arg22 = data.readLong();
                            data.enforceNoDataAvail();
                            setDataThrottling(_arg09, _arg16, _arg22);
                            return true;
                        case 11:
                            int _arg010 = data.readInt();
                            DataProfileInfo _arg17 = (DataProfileInfo) data.readTypedObject(DataProfileInfo.CREATOR);
                            data.enforceNoDataAvail();
                            setInitialAttachApn(_arg010, _arg17);
                            return true;
                        case 12:
                            IRadioDataResponse _arg011 = IRadioDataResponse.Stub.asInterface(data.readStrongBinder());
                            IRadioDataIndication _arg18 = IRadioDataIndication.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            setResponseFunctions(_arg011, _arg18);
                            return true;
                        case 13:
                            int _arg012 = data.readInt();
                            int _arg19 = data.readInt();
                            DataProfileInfo _arg23 = (DataProfileInfo) data.readTypedObject(DataProfileInfo.CREATOR);
                            boolean _arg3 = data.readBoolean();
                            int _arg4 = data.readInt();
                            LinkAddress[] _arg5 = (LinkAddress[]) data.createTypedArray(LinkAddress.CREATOR);
                            String[] _arg6 = data.createStringArray();
                            int _arg7 = data.readInt();
                            SliceInfo _arg8 = (SliceInfo) data.readTypedObject(SliceInfo.CREATOR);
                            boolean _arg9 = data.readBoolean();
                            data.enforceNoDataAvail();
                            setupDataCall(_arg012, _arg19, _arg23, _arg3, _arg4, _arg5, _arg6, _arg7, _arg8, _arg9);
                            return true;
                        case 14:
                            int _arg013 = data.readInt();
                            int _arg110 = data.readInt();
                            data.enforceNoDataAvail();
                            startHandover(_arg013, _arg110);
                            return true;
                        case 15:
                            int _arg014 = data.readInt();
                            KeepaliveRequest _arg111 = (KeepaliveRequest) data.readTypedObject(KeepaliveRequest.CREATOR);
                            data.enforceNoDataAvail();
                            startKeepalive(_arg014, _arg111);
                            return true;
                        case 16:
                            int _arg015 = data.readInt();
                            int _arg112 = data.readInt();
                            data.enforceNoDataAvail();
                            stopKeepalive(_arg015, _arg112);
                            return true;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
            }
        }

        /* loaded from: classes2.dex */
        private static class Proxy implements IRadioData {
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

            @Override // android.hardware.radio.data.IRadioData
            public void allocatePduSessionId(int serial) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    boolean _status = this.mRemote.transact(1, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method allocatePduSessionId is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.data.IRadioData
            public void cancelHandover(int serial, int callId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    _data.writeInt(callId);
                    boolean _status = this.mRemote.transact(2, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method cancelHandover is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.data.IRadioData
            public void deactivateDataCall(int serial, int cid, int reason) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    _data.writeInt(cid);
                    _data.writeInt(reason);
                    boolean _status = this.mRemote.transact(3, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method deactivateDataCall is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.data.IRadioData
            public void getDataCallList(int serial) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    boolean _status = this.mRemote.transact(4, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method getDataCallList is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.data.IRadioData
            public void getSlicingConfig(int serial) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    boolean _status = this.mRemote.transact(5, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method getSlicingConfig is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.data.IRadioData
            public void releasePduSessionId(int serial, int id) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    _data.writeInt(id);
                    boolean _status = this.mRemote.transact(6, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method releasePduSessionId is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.data.IRadioData
            public void responseAcknowledgement() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    boolean _status = this.mRemote.transact(7, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method responseAcknowledgement is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.data.IRadioData
            public void setDataAllowed(int serial, boolean allow) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    _data.writeBoolean(allow);
                    boolean _status = this.mRemote.transact(8, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method setDataAllowed is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.data.IRadioData
            public void setDataProfile(int serial, DataProfileInfo[] profiles) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    _data.writeTypedArray(profiles, 0);
                    boolean _status = this.mRemote.transact(9, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method setDataProfile is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.data.IRadioData
            public void setDataThrottling(int serial, byte dataThrottlingAction, long completionDurationMillis) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    _data.writeByte(dataThrottlingAction);
                    _data.writeLong(completionDurationMillis);
                    boolean _status = this.mRemote.transact(10, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method setDataThrottling is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.data.IRadioData
            public void setInitialAttachApn(int serial, DataProfileInfo dataProfileInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    _data.writeTypedObject(dataProfileInfo, 0);
                    boolean _status = this.mRemote.transact(11, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method setInitialAttachApn is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.data.IRadioData
            public void setResponseFunctions(IRadioDataResponse radioDataResponse, IRadioDataIndication radioDataIndication) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeStrongInterface(radioDataResponse);
                    _data.writeStrongInterface(radioDataIndication);
                    boolean _status = this.mRemote.transact(12, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method setResponseFunctions is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.data.IRadioData
            public void setupDataCall(int serial, int accessNetwork, DataProfileInfo dataProfileInfo, boolean roamingAllowed, int reason, LinkAddress[] addresses, String[] dnses, int pduSessionId, SliceInfo sliceInfo, boolean matchAllRuleAllowed) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    _data.writeInt(accessNetwork);
                    _data.writeTypedObject(dataProfileInfo, 0);
                    _data.writeBoolean(roamingAllowed);
                    _data.writeInt(reason);
                    _data.writeTypedArray(addresses, 0);
                    _data.writeStringArray(dnses);
                    _data.writeInt(pduSessionId);
                    _data.writeTypedObject(sliceInfo, 0);
                    _data.writeBoolean(matchAllRuleAllowed);
                    boolean _status = this.mRemote.transact(13, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method setupDataCall is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.data.IRadioData
            public void startHandover(int serial, int callId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    _data.writeInt(callId);
                    boolean _status = this.mRemote.transact(14, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method startHandover is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.data.IRadioData
            public void startKeepalive(int serial, KeepaliveRequest keepalive) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    _data.writeTypedObject(keepalive, 0);
                    boolean _status = this.mRemote.transact(15, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method startKeepalive is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.data.IRadioData
            public void stopKeepalive(int serial, int sessionHandle) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    _data.writeInt(sessionHandle);
                    boolean _status = this.mRemote.transact(16, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method stopKeepalive is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.data.IRadioData
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

            @Override // android.hardware.radio.data.IRadioData
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
