package android.hardware.radio.voice;

import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes2.dex */
public interface IRadioVoiceIndication extends IInterface {
    public static final String DESCRIPTOR = "android$hardware$radio$voice$IRadioVoiceIndication".replace('$', '.');
    public static final String HASH = "notfrozen";
    public static final int VERSION = 2;

    void callRing(int i, boolean z, CdmaSignalInfoRecord cdmaSignalInfoRecord) throws RemoteException;

    void callStateChanged(int i) throws RemoteException;

    void cdmaCallWaiting(int i, CdmaCallWaiting cdmaCallWaiting) throws RemoteException;

    void cdmaInfoRec(int i, CdmaInformationRecord[] cdmaInformationRecordArr) throws RemoteException;

    void cdmaOtaProvisionStatus(int i, int i2) throws RemoteException;

    void currentEmergencyNumberList(int i, EmergencyNumber[] emergencyNumberArr) throws RemoteException;

    void enterEmergencyCallbackMode(int i) throws RemoteException;

    void exitEmergencyCallbackMode(int i) throws RemoteException;

    String getInterfaceHash() throws RemoteException;

    int getInterfaceVersion() throws RemoteException;

    void indicateRingbackTone(int i, boolean z) throws RemoteException;

    void onSupplementaryServiceIndication(int i, StkCcUnsolSsResult stkCcUnsolSsResult) throws RemoteException;

    void onUssd(int i, int i2, String str) throws RemoteException;

    void resendIncallMute(int i) throws RemoteException;

    void srvccStateNotify(int i, int i2) throws RemoteException;

    void stkCallControlAlphaNotify(int i, String str) throws RemoteException;

    void stkCallSetup(int i, long j) throws RemoteException;

    /* loaded from: classes2.dex */
    public static class Default implements IRadioVoiceIndication {
        @Override // android.hardware.radio.voice.IRadioVoiceIndication
        public void callRing(int type, boolean isGsm, CdmaSignalInfoRecord record) throws RemoteException {
        }

        @Override // android.hardware.radio.voice.IRadioVoiceIndication
        public void callStateChanged(int type) throws RemoteException {
        }

        @Override // android.hardware.radio.voice.IRadioVoiceIndication
        public void cdmaCallWaiting(int type, CdmaCallWaiting callWaitingRecord) throws RemoteException {
        }

        @Override // android.hardware.radio.voice.IRadioVoiceIndication
        public void cdmaInfoRec(int type, CdmaInformationRecord[] records) throws RemoteException {
        }

        @Override // android.hardware.radio.voice.IRadioVoiceIndication
        public void cdmaOtaProvisionStatus(int type, int status) throws RemoteException {
        }

        @Override // android.hardware.radio.voice.IRadioVoiceIndication
        public void currentEmergencyNumberList(int type, EmergencyNumber[] emergencyNumberList) throws RemoteException {
        }

        @Override // android.hardware.radio.voice.IRadioVoiceIndication
        public void enterEmergencyCallbackMode(int type) throws RemoteException {
        }

        @Override // android.hardware.radio.voice.IRadioVoiceIndication
        public void exitEmergencyCallbackMode(int type) throws RemoteException {
        }

        @Override // android.hardware.radio.voice.IRadioVoiceIndication
        public void indicateRingbackTone(int type, boolean start) throws RemoteException {
        }

        @Override // android.hardware.radio.voice.IRadioVoiceIndication
        public void onSupplementaryServiceIndication(int type, StkCcUnsolSsResult ss) throws RemoteException {
        }

        @Override // android.hardware.radio.voice.IRadioVoiceIndication
        public void onUssd(int type, int modeType, String msg) throws RemoteException {
        }

        @Override // android.hardware.radio.voice.IRadioVoiceIndication
        public void resendIncallMute(int type) throws RemoteException {
        }

        @Override // android.hardware.radio.voice.IRadioVoiceIndication
        public void srvccStateNotify(int type, int state) throws RemoteException {
        }

        @Override // android.hardware.radio.voice.IRadioVoiceIndication
        public void stkCallControlAlphaNotify(int type, String alpha) throws RemoteException {
        }

        @Override // android.hardware.radio.voice.IRadioVoiceIndication
        public void stkCallSetup(int type, long timeout) throws RemoteException {
        }

        @Override // android.hardware.radio.voice.IRadioVoiceIndication
        public int getInterfaceVersion() {
            return 0;
        }

        @Override // android.hardware.radio.voice.IRadioVoiceIndication
        public String getInterfaceHash() {
            return "";
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements IRadioVoiceIndication {
        static final int TRANSACTION_callRing = 1;
        static final int TRANSACTION_callStateChanged = 2;
        static final int TRANSACTION_cdmaCallWaiting = 3;
        static final int TRANSACTION_cdmaInfoRec = 4;
        static final int TRANSACTION_cdmaOtaProvisionStatus = 5;
        static final int TRANSACTION_currentEmergencyNumberList = 6;
        static final int TRANSACTION_enterEmergencyCallbackMode = 7;
        static final int TRANSACTION_exitEmergencyCallbackMode = 8;
        static final int TRANSACTION_getInterfaceHash = 16777214;
        static final int TRANSACTION_getInterfaceVersion = 16777215;
        static final int TRANSACTION_indicateRingbackTone = 9;
        static final int TRANSACTION_onSupplementaryServiceIndication = 10;
        static final int TRANSACTION_onUssd = 11;
        static final int TRANSACTION_resendIncallMute = 12;
        static final int TRANSACTION_srvccStateNotify = 13;
        static final int TRANSACTION_stkCallControlAlphaNotify = 14;
        static final int TRANSACTION_stkCallSetup = 15;

        public Stub() {
            markVintfStability();
            attachInterface(this, DESCRIPTOR);
        }

        public static IRadioVoiceIndication asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IRadioVoiceIndication)) {
                return (IRadioVoiceIndication) iin;
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
                            CdmaSignalInfoRecord _arg2 = (CdmaSignalInfoRecord) data.readTypedObject(CdmaSignalInfoRecord.CREATOR);
                            data.enforceNoDataAvail();
                            callRing(_arg0, _arg1, _arg2);
                            break;
                        case 2:
                            int _arg02 = data.readInt();
                            data.enforceNoDataAvail();
                            callStateChanged(_arg02);
                            break;
                        case 3:
                            int _arg03 = data.readInt();
                            CdmaCallWaiting _arg12 = (CdmaCallWaiting) data.readTypedObject(CdmaCallWaiting.CREATOR);
                            data.enforceNoDataAvail();
                            cdmaCallWaiting(_arg03, _arg12);
                            break;
                        case 4:
                            int _arg04 = data.readInt();
                            CdmaInformationRecord[] _arg13 = (CdmaInformationRecord[]) data.createTypedArray(CdmaInformationRecord.CREATOR);
                            data.enforceNoDataAvail();
                            cdmaInfoRec(_arg04, _arg13);
                            break;
                        case 5:
                            int _arg05 = data.readInt();
                            int _arg14 = data.readInt();
                            data.enforceNoDataAvail();
                            cdmaOtaProvisionStatus(_arg05, _arg14);
                            break;
                        case 6:
                            int _arg06 = data.readInt();
                            EmergencyNumber[] _arg15 = (EmergencyNumber[]) data.createTypedArray(EmergencyNumber.CREATOR);
                            data.enforceNoDataAvail();
                            currentEmergencyNumberList(_arg06, _arg15);
                            break;
                        case 7:
                            int _arg07 = data.readInt();
                            data.enforceNoDataAvail();
                            enterEmergencyCallbackMode(_arg07);
                            break;
                        case 8:
                            int _arg08 = data.readInt();
                            data.enforceNoDataAvail();
                            exitEmergencyCallbackMode(_arg08);
                            break;
                        case 9:
                            int _arg09 = data.readInt();
                            boolean _arg16 = data.readBoolean();
                            data.enforceNoDataAvail();
                            indicateRingbackTone(_arg09, _arg16);
                            break;
                        case 10:
                            int _arg010 = data.readInt();
                            StkCcUnsolSsResult _arg17 = (StkCcUnsolSsResult) data.readTypedObject(StkCcUnsolSsResult.CREATOR);
                            data.enforceNoDataAvail();
                            onSupplementaryServiceIndication(_arg010, _arg17);
                            break;
                        case 11:
                            int _arg011 = data.readInt();
                            int _arg18 = data.readInt();
                            String _arg22 = data.readString();
                            data.enforceNoDataAvail();
                            onUssd(_arg011, _arg18, _arg22);
                            break;
                        case 12:
                            int _arg012 = data.readInt();
                            data.enforceNoDataAvail();
                            resendIncallMute(_arg012);
                            break;
                        case 13:
                            int _arg013 = data.readInt();
                            int _arg19 = data.readInt();
                            data.enforceNoDataAvail();
                            srvccStateNotify(_arg013, _arg19);
                            break;
                        case 14:
                            int _arg014 = data.readInt();
                            String _arg110 = data.readString();
                            data.enforceNoDataAvail();
                            stkCallControlAlphaNotify(_arg014, _arg110);
                            break;
                        case 15:
                            int _arg015 = data.readInt();
                            long _arg111 = data.readLong();
                            data.enforceNoDataAvail();
                            stkCallSetup(_arg015, _arg111);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes2.dex */
        private static class Proxy implements IRadioVoiceIndication {
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

            @Override // android.hardware.radio.voice.IRadioVoiceIndication
            public void callRing(int type, boolean isGsm, CdmaSignalInfoRecord record) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(type);
                    _data.writeBoolean(isGsm);
                    _data.writeTypedObject(record, 0);
                    boolean _status = this.mRemote.transact(1, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method callRing is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.voice.IRadioVoiceIndication
            public void callStateChanged(int type) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(type);
                    boolean _status = this.mRemote.transact(2, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method callStateChanged is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.voice.IRadioVoiceIndication
            public void cdmaCallWaiting(int type, CdmaCallWaiting callWaitingRecord) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(type);
                    _data.writeTypedObject(callWaitingRecord, 0);
                    boolean _status = this.mRemote.transact(3, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method cdmaCallWaiting is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.voice.IRadioVoiceIndication
            public void cdmaInfoRec(int type, CdmaInformationRecord[] records) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(type);
                    _data.writeTypedArray(records, 0);
                    boolean _status = this.mRemote.transact(4, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method cdmaInfoRec is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.voice.IRadioVoiceIndication
            public void cdmaOtaProvisionStatus(int type, int status) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(type);
                    _data.writeInt(status);
                    boolean _status = this.mRemote.transact(5, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method cdmaOtaProvisionStatus is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.voice.IRadioVoiceIndication
            public void currentEmergencyNumberList(int type, EmergencyNumber[] emergencyNumberList) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(type);
                    _data.writeTypedArray(emergencyNumberList, 0);
                    boolean _status = this.mRemote.transact(6, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method currentEmergencyNumberList is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.voice.IRadioVoiceIndication
            public void enterEmergencyCallbackMode(int type) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(type);
                    boolean _status = this.mRemote.transact(7, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method enterEmergencyCallbackMode is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.voice.IRadioVoiceIndication
            public void exitEmergencyCallbackMode(int type) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(type);
                    boolean _status = this.mRemote.transact(8, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method exitEmergencyCallbackMode is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.voice.IRadioVoiceIndication
            public void indicateRingbackTone(int type, boolean start) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(type);
                    _data.writeBoolean(start);
                    boolean _status = this.mRemote.transact(9, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method indicateRingbackTone is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.voice.IRadioVoiceIndication
            public void onSupplementaryServiceIndication(int type, StkCcUnsolSsResult ss) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(type);
                    _data.writeTypedObject(ss, 0);
                    boolean _status = this.mRemote.transact(10, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method onSupplementaryServiceIndication is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.voice.IRadioVoiceIndication
            public void onUssd(int type, int modeType, String msg) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(type);
                    _data.writeInt(modeType);
                    _data.writeString(msg);
                    boolean _status = this.mRemote.transact(11, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method onUssd is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.voice.IRadioVoiceIndication
            public void resendIncallMute(int type) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(type);
                    boolean _status = this.mRemote.transact(12, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method resendIncallMute is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.voice.IRadioVoiceIndication
            public void srvccStateNotify(int type, int state) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(type);
                    _data.writeInt(state);
                    boolean _status = this.mRemote.transact(13, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method srvccStateNotify is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.voice.IRadioVoiceIndication
            public void stkCallControlAlphaNotify(int type, String alpha) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(type);
                    _data.writeString(alpha);
                    boolean _status = this.mRemote.transact(14, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method stkCallControlAlphaNotify is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.voice.IRadioVoiceIndication
            public void stkCallSetup(int type, long timeout) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(type);
                    _data.writeLong(timeout);
                    boolean _status = this.mRemote.transact(15, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method stkCallSetup is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.voice.IRadioVoiceIndication
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

            @Override // android.hardware.radio.voice.IRadioVoiceIndication
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
