package android.hardware.radio.voice;

import android.hardware.radio.voice.IRadioVoiceIndication;
import android.hardware.radio.voice.IRadioVoiceResponse;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes2.dex */
public interface IRadioVoice extends IInterface {
    public static final String DESCRIPTOR = "android$hardware$radio$voice$IRadioVoice".replace('$', '.');
    public static final String HASH = "notfrozen";
    public static final int VERSION = 2;

    void acceptCall(int i) throws RemoteException;

    void cancelPendingUssd(int i) throws RemoteException;

    void conference(int i) throws RemoteException;

    void dial(int i, Dial dial) throws RemoteException;

    void emergencyDial(int i, Dial dial, int i2, String[] strArr, int i3, boolean z, boolean z2) throws RemoteException;

    void exitEmergencyCallbackMode(int i) throws RemoteException;

    void explicitCallTransfer(int i) throws RemoteException;

    void getCallForwardStatus(int i, CallForwardInfo callForwardInfo) throws RemoteException;

    void getCallWaiting(int i, int i2) throws RemoteException;

    void getClip(int i) throws RemoteException;

    void getClir(int i) throws RemoteException;

    void getCurrentCalls(int i) throws RemoteException;

    String getInterfaceHash() throws RemoteException;

    int getInterfaceVersion() throws RemoteException;

    void getLastCallFailCause(int i) throws RemoteException;

    void getMute(int i) throws RemoteException;

    void getPreferredVoicePrivacy(int i) throws RemoteException;

    void getTtyMode(int i) throws RemoteException;

    void handleStkCallSetupRequestFromSim(int i, boolean z) throws RemoteException;

    void hangup(int i, int i2) throws RemoteException;

    void hangupForegroundResumeBackground(int i) throws RemoteException;

    void hangupWaitingOrBackground(int i) throws RemoteException;

    void isVoNrEnabled(int i) throws RemoteException;

    void rejectCall(int i) throws RemoteException;

    void responseAcknowledgement() throws RemoteException;

    void sendBurstDtmf(int i, String str, int i2, int i3) throws RemoteException;

    void sendCdmaFeatureCode(int i, String str) throws RemoteException;

    void sendDtmf(int i, String str) throws RemoteException;

    void sendUssd(int i, String str) throws RemoteException;

    void separateConnection(int i, int i2) throws RemoteException;

    void setCallForward(int i, CallForwardInfo callForwardInfo) throws RemoteException;

    void setCallWaiting(int i, boolean z, int i2) throws RemoteException;

    void setClir(int i, int i2) throws RemoteException;

    void setMute(int i, boolean z) throws RemoteException;

    void setPreferredVoicePrivacy(int i, boolean z) throws RemoteException;

    void setResponseFunctions(IRadioVoiceResponse iRadioVoiceResponse, IRadioVoiceIndication iRadioVoiceIndication) throws RemoteException;

    void setTtyMode(int i, int i2) throws RemoteException;

    void setVoNrEnabled(int i, boolean z) throws RemoteException;

    void startDtmf(int i, String str) throws RemoteException;

    void stopDtmf(int i) throws RemoteException;

    void switchWaitingOrHoldingAndActive(int i) throws RemoteException;

    /* loaded from: classes2.dex */
    public static class Default implements IRadioVoice {
        @Override // android.hardware.radio.voice.IRadioVoice
        public void acceptCall(int serial) throws RemoteException {
        }

        @Override // android.hardware.radio.voice.IRadioVoice
        public void cancelPendingUssd(int serial) throws RemoteException {
        }

        @Override // android.hardware.radio.voice.IRadioVoice
        public void conference(int serial) throws RemoteException {
        }

        @Override // android.hardware.radio.voice.IRadioVoice
        public void dial(int serial, Dial dialInfo) throws RemoteException {
        }

        @Override // android.hardware.radio.voice.IRadioVoice
        public void emergencyDial(int serial, Dial dialInfo, int categories, String[] urns, int routing, boolean hasKnownUserIntentEmergency, boolean isTesting) throws RemoteException {
        }

        @Override // android.hardware.radio.voice.IRadioVoice
        public void exitEmergencyCallbackMode(int serial) throws RemoteException {
        }

        @Override // android.hardware.radio.voice.IRadioVoice
        public void explicitCallTransfer(int serial) throws RemoteException {
        }

        @Override // android.hardware.radio.voice.IRadioVoice
        public void getCallForwardStatus(int serial, CallForwardInfo callInfo) throws RemoteException {
        }

        @Override // android.hardware.radio.voice.IRadioVoice
        public void getCallWaiting(int serial, int serviceClass) throws RemoteException {
        }

        @Override // android.hardware.radio.voice.IRadioVoice
        public void getClip(int serial) throws RemoteException {
        }

        @Override // android.hardware.radio.voice.IRadioVoice
        public void getClir(int serial) throws RemoteException {
        }

        @Override // android.hardware.radio.voice.IRadioVoice
        public void getCurrentCalls(int serial) throws RemoteException {
        }

        @Override // android.hardware.radio.voice.IRadioVoice
        public void getLastCallFailCause(int serial) throws RemoteException {
        }

        @Override // android.hardware.radio.voice.IRadioVoice
        public void getMute(int serial) throws RemoteException {
        }

        @Override // android.hardware.radio.voice.IRadioVoice
        public void getPreferredVoicePrivacy(int serial) throws RemoteException {
        }

        @Override // android.hardware.radio.voice.IRadioVoice
        public void getTtyMode(int serial) throws RemoteException {
        }

        @Override // android.hardware.radio.voice.IRadioVoice
        public void handleStkCallSetupRequestFromSim(int serial, boolean accept) throws RemoteException {
        }

        @Override // android.hardware.radio.voice.IRadioVoice
        public void hangup(int serial, int gsmIndex) throws RemoteException {
        }

        @Override // android.hardware.radio.voice.IRadioVoice
        public void hangupForegroundResumeBackground(int serial) throws RemoteException {
        }

        @Override // android.hardware.radio.voice.IRadioVoice
        public void hangupWaitingOrBackground(int serial) throws RemoteException {
        }

        @Override // android.hardware.radio.voice.IRadioVoice
        public void isVoNrEnabled(int serial) throws RemoteException {
        }

        @Override // android.hardware.radio.voice.IRadioVoice
        public void rejectCall(int serial) throws RemoteException {
        }

        @Override // android.hardware.radio.voice.IRadioVoice
        public void responseAcknowledgement() throws RemoteException {
        }

        @Override // android.hardware.radio.voice.IRadioVoice
        public void sendBurstDtmf(int serial, String dtmf, int on, int off) throws RemoteException {
        }

        @Override // android.hardware.radio.voice.IRadioVoice
        public void sendCdmaFeatureCode(int serial, String featureCode) throws RemoteException {
        }

        @Override // android.hardware.radio.voice.IRadioVoice
        public void sendDtmf(int serial, String s) throws RemoteException {
        }

        @Override // android.hardware.radio.voice.IRadioVoice
        public void sendUssd(int serial, String ussd) throws RemoteException {
        }

        @Override // android.hardware.radio.voice.IRadioVoice
        public void separateConnection(int serial, int gsmIndex) throws RemoteException {
        }

        @Override // android.hardware.radio.voice.IRadioVoice
        public void setCallForward(int serial, CallForwardInfo callInfo) throws RemoteException {
        }

        @Override // android.hardware.radio.voice.IRadioVoice
        public void setCallWaiting(int serial, boolean enable, int serviceClass) throws RemoteException {
        }

        @Override // android.hardware.radio.voice.IRadioVoice
        public void setClir(int serial, int status) throws RemoteException {
        }

        @Override // android.hardware.radio.voice.IRadioVoice
        public void setMute(int serial, boolean enable) throws RemoteException {
        }

        @Override // android.hardware.radio.voice.IRadioVoice
        public void setPreferredVoicePrivacy(int serial, boolean enable) throws RemoteException {
        }

        @Override // android.hardware.radio.voice.IRadioVoice
        public void setResponseFunctions(IRadioVoiceResponse radioVoiceResponse, IRadioVoiceIndication radioVoiceIndication) throws RemoteException {
        }

        @Override // android.hardware.radio.voice.IRadioVoice
        public void setTtyMode(int serial, int mode) throws RemoteException {
        }

        @Override // android.hardware.radio.voice.IRadioVoice
        public void setVoNrEnabled(int serial, boolean enable) throws RemoteException {
        }

        @Override // android.hardware.radio.voice.IRadioVoice
        public void startDtmf(int serial, String s) throws RemoteException {
        }

        @Override // android.hardware.radio.voice.IRadioVoice
        public void stopDtmf(int serial) throws RemoteException {
        }

        @Override // android.hardware.radio.voice.IRadioVoice
        public void switchWaitingOrHoldingAndActive(int serial) throws RemoteException {
        }

        @Override // android.hardware.radio.voice.IRadioVoice
        public int getInterfaceVersion() {
            return 0;
        }

        @Override // android.hardware.radio.voice.IRadioVoice
        public String getInterfaceHash() {
            return "";
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements IRadioVoice {
        static final int TRANSACTION_acceptCall = 1;
        static final int TRANSACTION_cancelPendingUssd = 2;
        static final int TRANSACTION_conference = 3;
        static final int TRANSACTION_dial = 4;
        static final int TRANSACTION_emergencyDial = 5;
        static final int TRANSACTION_exitEmergencyCallbackMode = 6;
        static final int TRANSACTION_explicitCallTransfer = 7;
        static final int TRANSACTION_getCallForwardStatus = 8;
        static final int TRANSACTION_getCallWaiting = 9;
        static final int TRANSACTION_getClip = 10;
        static final int TRANSACTION_getClir = 11;
        static final int TRANSACTION_getCurrentCalls = 12;
        static final int TRANSACTION_getInterfaceHash = 16777214;
        static final int TRANSACTION_getInterfaceVersion = 16777215;
        static final int TRANSACTION_getLastCallFailCause = 13;
        static final int TRANSACTION_getMute = 14;
        static final int TRANSACTION_getPreferredVoicePrivacy = 15;
        static final int TRANSACTION_getTtyMode = 16;
        static final int TRANSACTION_handleStkCallSetupRequestFromSim = 17;
        static final int TRANSACTION_hangup = 18;
        static final int TRANSACTION_hangupForegroundResumeBackground = 19;
        static final int TRANSACTION_hangupWaitingOrBackground = 20;
        static final int TRANSACTION_isVoNrEnabled = 21;
        static final int TRANSACTION_rejectCall = 22;
        static final int TRANSACTION_responseAcknowledgement = 23;
        static final int TRANSACTION_sendBurstDtmf = 24;
        static final int TRANSACTION_sendCdmaFeatureCode = 25;
        static final int TRANSACTION_sendDtmf = 26;
        static final int TRANSACTION_sendUssd = 27;
        static final int TRANSACTION_separateConnection = 28;
        static final int TRANSACTION_setCallForward = 29;
        static final int TRANSACTION_setCallWaiting = 30;
        static final int TRANSACTION_setClir = 31;
        static final int TRANSACTION_setMute = 32;
        static final int TRANSACTION_setPreferredVoicePrivacy = 33;
        static final int TRANSACTION_setResponseFunctions = 34;
        static final int TRANSACTION_setTtyMode = 35;
        static final int TRANSACTION_setVoNrEnabled = 36;
        static final int TRANSACTION_startDtmf = 37;
        static final int TRANSACTION_stopDtmf = 38;
        static final int TRANSACTION_switchWaitingOrHoldingAndActive = 39;

        public Stub() {
            markVintfStability();
            attachInterface(this, DESCRIPTOR);
        }

        public static IRadioVoice asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IRadioVoice)) {
                return (IRadioVoice) iin;
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
                            acceptCall(_arg0);
                            break;
                        case 2:
                            int _arg02 = data.readInt();
                            data.enforceNoDataAvail();
                            cancelPendingUssd(_arg02);
                            break;
                        case 3:
                            int _arg03 = data.readInt();
                            data.enforceNoDataAvail();
                            conference(_arg03);
                            break;
                        case 4:
                            int _arg04 = data.readInt();
                            Dial _arg1 = (Dial) data.readTypedObject(Dial.CREATOR);
                            data.enforceNoDataAvail();
                            dial(_arg04, _arg1);
                            break;
                        case 5:
                            int _arg05 = data.readInt();
                            Dial _arg12 = (Dial) data.readTypedObject(Dial.CREATOR);
                            int _arg2 = data.readInt();
                            String[] _arg3 = data.createStringArray();
                            int _arg4 = data.readInt();
                            boolean _arg5 = data.readBoolean();
                            boolean _arg6 = data.readBoolean();
                            data.enforceNoDataAvail();
                            emergencyDial(_arg05, _arg12, _arg2, _arg3, _arg4, _arg5, _arg6);
                            break;
                        case 6:
                            int _arg06 = data.readInt();
                            data.enforceNoDataAvail();
                            exitEmergencyCallbackMode(_arg06);
                            break;
                        case 7:
                            int _arg07 = data.readInt();
                            data.enforceNoDataAvail();
                            explicitCallTransfer(_arg07);
                            break;
                        case 8:
                            int _arg08 = data.readInt();
                            CallForwardInfo _arg13 = (CallForwardInfo) data.readTypedObject(CallForwardInfo.CREATOR);
                            data.enforceNoDataAvail();
                            getCallForwardStatus(_arg08, _arg13);
                            break;
                        case 9:
                            int _arg09 = data.readInt();
                            int _arg14 = data.readInt();
                            data.enforceNoDataAvail();
                            getCallWaiting(_arg09, _arg14);
                            break;
                        case 10:
                            int _arg010 = data.readInt();
                            data.enforceNoDataAvail();
                            getClip(_arg010);
                            break;
                        case 11:
                            int _arg011 = data.readInt();
                            data.enforceNoDataAvail();
                            getClir(_arg011);
                            break;
                        case 12:
                            int _arg012 = data.readInt();
                            data.enforceNoDataAvail();
                            getCurrentCalls(_arg012);
                            break;
                        case 13:
                            int _arg013 = data.readInt();
                            data.enforceNoDataAvail();
                            getLastCallFailCause(_arg013);
                            break;
                        case 14:
                            int _arg014 = data.readInt();
                            data.enforceNoDataAvail();
                            getMute(_arg014);
                            break;
                        case 15:
                            int _arg015 = data.readInt();
                            data.enforceNoDataAvail();
                            getPreferredVoicePrivacy(_arg015);
                            break;
                        case 16:
                            int _arg016 = data.readInt();
                            data.enforceNoDataAvail();
                            getTtyMode(_arg016);
                            break;
                        case 17:
                            int _arg017 = data.readInt();
                            boolean _arg15 = data.readBoolean();
                            data.enforceNoDataAvail();
                            handleStkCallSetupRequestFromSim(_arg017, _arg15);
                            break;
                        case 18:
                            int _arg018 = data.readInt();
                            int _arg16 = data.readInt();
                            data.enforceNoDataAvail();
                            hangup(_arg018, _arg16);
                            break;
                        case 19:
                            int _arg019 = data.readInt();
                            data.enforceNoDataAvail();
                            hangupForegroundResumeBackground(_arg019);
                            break;
                        case 20:
                            int _arg020 = data.readInt();
                            data.enforceNoDataAvail();
                            hangupWaitingOrBackground(_arg020);
                            break;
                        case 21:
                            int _arg021 = data.readInt();
                            data.enforceNoDataAvail();
                            isVoNrEnabled(_arg021);
                            break;
                        case 22:
                            int _arg022 = data.readInt();
                            data.enforceNoDataAvail();
                            rejectCall(_arg022);
                            break;
                        case 23:
                            responseAcknowledgement();
                            break;
                        case 24:
                            int _arg023 = data.readInt();
                            String _arg17 = data.readString();
                            int _arg22 = data.readInt();
                            int _arg32 = data.readInt();
                            data.enforceNoDataAvail();
                            sendBurstDtmf(_arg023, _arg17, _arg22, _arg32);
                            break;
                        case 25:
                            int _arg024 = data.readInt();
                            String _arg18 = data.readString();
                            data.enforceNoDataAvail();
                            sendCdmaFeatureCode(_arg024, _arg18);
                            break;
                        case 26:
                            int _arg025 = data.readInt();
                            String _arg19 = data.readString();
                            data.enforceNoDataAvail();
                            sendDtmf(_arg025, _arg19);
                            break;
                        case 27:
                            int _arg026 = data.readInt();
                            String _arg110 = data.readString();
                            data.enforceNoDataAvail();
                            sendUssd(_arg026, _arg110);
                            break;
                        case 28:
                            int _arg027 = data.readInt();
                            int _arg111 = data.readInt();
                            data.enforceNoDataAvail();
                            separateConnection(_arg027, _arg111);
                            break;
                        case 29:
                            int _arg028 = data.readInt();
                            CallForwardInfo _arg112 = (CallForwardInfo) data.readTypedObject(CallForwardInfo.CREATOR);
                            data.enforceNoDataAvail();
                            setCallForward(_arg028, _arg112);
                            break;
                        case 30:
                            int _arg029 = data.readInt();
                            boolean _arg113 = data.readBoolean();
                            int _arg23 = data.readInt();
                            data.enforceNoDataAvail();
                            setCallWaiting(_arg029, _arg113, _arg23);
                            break;
                        case 31:
                            int _arg030 = data.readInt();
                            int _arg114 = data.readInt();
                            data.enforceNoDataAvail();
                            setClir(_arg030, _arg114);
                            break;
                        case 32:
                            int _arg031 = data.readInt();
                            boolean _arg115 = data.readBoolean();
                            data.enforceNoDataAvail();
                            setMute(_arg031, _arg115);
                            break;
                        case 33:
                            int _arg032 = data.readInt();
                            boolean _arg116 = data.readBoolean();
                            data.enforceNoDataAvail();
                            setPreferredVoicePrivacy(_arg032, _arg116);
                            break;
                        case 34:
                            IRadioVoiceResponse _arg033 = IRadioVoiceResponse.Stub.asInterface(data.readStrongBinder());
                            IRadioVoiceIndication _arg117 = IRadioVoiceIndication.Stub.asInterface(data.readStrongBinder());
                            data.enforceNoDataAvail();
                            setResponseFunctions(_arg033, _arg117);
                            break;
                        case 35:
                            int _arg034 = data.readInt();
                            int _arg118 = data.readInt();
                            data.enforceNoDataAvail();
                            setTtyMode(_arg034, _arg118);
                            break;
                        case 36:
                            int _arg035 = data.readInt();
                            boolean _arg119 = data.readBoolean();
                            data.enforceNoDataAvail();
                            setVoNrEnabled(_arg035, _arg119);
                            break;
                        case 37:
                            int _arg036 = data.readInt();
                            String _arg120 = data.readString();
                            data.enforceNoDataAvail();
                            startDtmf(_arg036, _arg120);
                            break;
                        case 38:
                            int _arg037 = data.readInt();
                            data.enforceNoDataAvail();
                            stopDtmf(_arg037);
                            break;
                        case 39:
                            int _arg038 = data.readInt();
                            data.enforceNoDataAvail();
                            switchWaitingOrHoldingAndActive(_arg038);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes2.dex */
        private static class Proxy implements IRadioVoice {
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

            @Override // android.hardware.radio.voice.IRadioVoice
            public void acceptCall(int serial) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    boolean _status = this.mRemote.transact(1, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method acceptCall is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.voice.IRadioVoice
            public void cancelPendingUssd(int serial) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    boolean _status = this.mRemote.transact(2, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method cancelPendingUssd is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.voice.IRadioVoice
            public void conference(int serial) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    boolean _status = this.mRemote.transact(3, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method conference is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.voice.IRadioVoice
            public void dial(int serial, Dial dialInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    _data.writeTypedObject(dialInfo, 0);
                    boolean _status = this.mRemote.transact(4, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method dial is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.voice.IRadioVoice
            public void emergencyDial(int serial, Dial dialInfo, int categories, String[] urns, int routing, boolean hasKnownUserIntentEmergency, boolean isTesting) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    _data.writeTypedObject(dialInfo, 0);
                    _data.writeInt(categories);
                    _data.writeStringArray(urns);
                    _data.writeInt(routing);
                    _data.writeBoolean(hasKnownUserIntentEmergency);
                    _data.writeBoolean(isTesting);
                    boolean _status = this.mRemote.transact(5, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method emergencyDial is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.voice.IRadioVoice
            public void exitEmergencyCallbackMode(int serial) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    boolean _status = this.mRemote.transact(6, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method exitEmergencyCallbackMode is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.voice.IRadioVoice
            public void explicitCallTransfer(int serial) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    boolean _status = this.mRemote.transact(7, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method explicitCallTransfer is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.voice.IRadioVoice
            public void getCallForwardStatus(int serial, CallForwardInfo callInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    _data.writeTypedObject(callInfo, 0);
                    boolean _status = this.mRemote.transact(8, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method getCallForwardStatus is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.voice.IRadioVoice
            public void getCallWaiting(int serial, int serviceClass) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    _data.writeInt(serviceClass);
                    boolean _status = this.mRemote.transact(9, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method getCallWaiting is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.voice.IRadioVoice
            public void getClip(int serial) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    boolean _status = this.mRemote.transact(10, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method getClip is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.voice.IRadioVoice
            public void getClir(int serial) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    boolean _status = this.mRemote.transact(11, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method getClir is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.voice.IRadioVoice
            public void getCurrentCalls(int serial) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    boolean _status = this.mRemote.transact(12, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method getCurrentCalls is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.voice.IRadioVoice
            public void getLastCallFailCause(int serial) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    boolean _status = this.mRemote.transact(13, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method getLastCallFailCause is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.voice.IRadioVoice
            public void getMute(int serial) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    boolean _status = this.mRemote.transact(14, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method getMute is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.voice.IRadioVoice
            public void getPreferredVoicePrivacy(int serial) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    boolean _status = this.mRemote.transact(15, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method getPreferredVoicePrivacy is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.voice.IRadioVoice
            public void getTtyMode(int serial) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    boolean _status = this.mRemote.transact(16, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method getTtyMode is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.voice.IRadioVoice
            public void handleStkCallSetupRequestFromSim(int serial, boolean accept) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    _data.writeBoolean(accept);
                    boolean _status = this.mRemote.transact(17, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method handleStkCallSetupRequestFromSim is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.voice.IRadioVoice
            public void hangup(int serial, int gsmIndex) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    _data.writeInt(gsmIndex);
                    boolean _status = this.mRemote.transact(18, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method hangup is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.voice.IRadioVoice
            public void hangupForegroundResumeBackground(int serial) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    boolean _status = this.mRemote.transact(19, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method hangupForegroundResumeBackground is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.voice.IRadioVoice
            public void hangupWaitingOrBackground(int serial) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    boolean _status = this.mRemote.transact(20, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method hangupWaitingOrBackground is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.voice.IRadioVoice
            public void isVoNrEnabled(int serial) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    boolean _status = this.mRemote.transact(21, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method isVoNrEnabled is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.voice.IRadioVoice
            public void rejectCall(int serial) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    boolean _status = this.mRemote.transact(22, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method rejectCall is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.voice.IRadioVoice
            public void responseAcknowledgement() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    boolean _status = this.mRemote.transact(23, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method responseAcknowledgement is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.voice.IRadioVoice
            public void sendBurstDtmf(int serial, String dtmf, int on, int off) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    _data.writeString(dtmf);
                    _data.writeInt(on);
                    _data.writeInt(off);
                    boolean _status = this.mRemote.transact(24, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method sendBurstDtmf is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.voice.IRadioVoice
            public void sendCdmaFeatureCode(int serial, String featureCode) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    _data.writeString(featureCode);
                    boolean _status = this.mRemote.transact(25, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method sendCdmaFeatureCode is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.voice.IRadioVoice
            public void sendDtmf(int serial, String s) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    _data.writeString(s);
                    boolean _status = this.mRemote.transact(26, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method sendDtmf is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.voice.IRadioVoice
            public void sendUssd(int serial, String ussd) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    _data.writeString(ussd);
                    boolean _status = this.mRemote.transact(27, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method sendUssd is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.voice.IRadioVoice
            public void separateConnection(int serial, int gsmIndex) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    _data.writeInt(gsmIndex);
                    boolean _status = this.mRemote.transact(28, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method separateConnection is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.voice.IRadioVoice
            public void setCallForward(int serial, CallForwardInfo callInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    _data.writeTypedObject(callInfo, 0);
                    boolean _status = this.mRemote.transact(29, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method setCallForward is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.voice.IRadioVoice
            public void setCallWaiting(int serial, boolean enable, int serviceClass) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    _data.writeBoolean(enable);
                    _data.writeInt(serviceClass);
                    boolean _status = this.mRemote.transact(30, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method setCallWaiting is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.voice.IRadioVoice
            public void setClir(int serial, int status) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    _data.writeInt(status);
                    boolean _status = this.mRemote.transact(31, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method setClir is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.voice.IRadioVoice
            public void setMute(int serial, boolean enable) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    _data.writeBoolean(enable);
                    boolean _status = this.mRemote.transact(32, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method setMute is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.voice.IRadioVoice
            public void setPreferredVoicePrivacy(int serial, boolean enable) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    _data.writeBoolean(enable);
                    boolean _status = this.mRemote.transact(33, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method setPreferredVoicePrivacy is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.voice.IRadioVoice
            public void setResponseFunctions(IRadioVoiceResponse radioVoiceResponse, IRadioVoiceIndication radioVoiceIndication) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeStrongInterface(radioVoiceResponse);
                    _data.writeStrongInterface(radioVoiceIndication);
                    boolean _status = this.mRemote.transact(34, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method setResponseFunctions is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.voice.IRadioVoice
            public void setTtyMode(int serial, int mode) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    _data.writeInt(mode);
                    boolean _status = this.mRemote.transact(35, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method setTtyMode is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.voice.IRadioVoice
            public void setVoNrEnabled(int serial, boolean enable) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    _data.writeBoolean(enable);
                    boolean _status = this.mRemote.transact(36, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method setVoNrEnabled is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.voice.IRadioVoice
            public void startDtmf(int serial, String s) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    _data.writeString(s);
                    boolean _status = this.mRemote.transact(37, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method startDtmf is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.voice.IRadioVoice
            public void stopDtmf(int serial) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    boolean _status = this.mRemote.transact(38, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method stopDtmf is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.voice.IRadioVoice
            public void switchWaitingOrHoldingAndActive(int serial) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    boolean _status = this.mRemote.transact(39, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method switchWaitingOrHoldingAndActive is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.voice.IRadioVoice
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

            @Override // android.hardware.radio.voice.IRadioVoice
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
