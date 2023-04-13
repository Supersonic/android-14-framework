package android.hardware.radio.voice;

import android.hardware.radio.RadioResponseInfo;
import android.p008os.Binder;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
/* loaded from: classes2.dex */
public interface IRadioVoiceResponse extends IInterface {
    public static final String DESCRIPTOR = "android$hardware$radio$voice$IRadioVoiceResponse".replace('$', '.');
    public static final String HASH = "notfrozen";
    public static final int VERSION = 2;

    void acceptCallResponse(RadioResponseInfo radioResponseInfo) throws RemoteException;

    void acknowledgeRequest(int i) throws RemoteException;

    void cancelPendingUssdResponse(RadioResponseInfo radioResponseInfo) throws RemoteException;

    void conferenceResponse(RadioResponseInfo radioResponseInfo) throws RemoteException;

    void dialResponse(RadioResponseInfo radioResponseInfo) throws RemoteException;

    void emergencyDialResponse(RadioResponseInfo radioResponseInfo) throws RemoteException;

    void exitEmergencyCallbackModeResponse(RadioResponseInfo radioResponseInfo) throws RemoteException;

    void explicitCallTransferResponse(RadioResponseInfo radioResponseInfo) throws RemoteException;

    void getCallForwardStatusResponse(RadioResponseInfo radioResponseInfo, CallForwardInfo[] callForwardInfoArr) throws RemoteException;

    void getCallWaitingResponse(RadioResponseInfo radioResponseInfo, boolean z, int i) throws RemoteException;

    void getClipResponse(RadioResponseInfo radioResponseInfo, int i) throws RemoteException;

    void getClirResponse(RadioResponseInfo radioResponseInfo, int i, int i2) throws RemoteException;

    void getCurrentCallsResponse(RadioResponseInfo radioResponseInfo, Call[] callArr) throws RemoteException;

    String getInterfaceHash() throws RemoteException;

    int getInterfaceVersion() throws RemoteException;

    void getLastCallFailCauseResponse(RadioResponseInfo radioResponseInfo, LastCallFailCauseInfo lastCallFailCauseInfo) throws RemoteException;

    void getMuteResponse(RadioResponseInfo radioResponseInfo, boolean z) throws RemoteException;

    void getPreferredVoicePrivacyResponse(RadioResponseInfo radioResponseInfo, boolean z) throws RemoteException;

    void getTtyModeResponse(RadioResponseInfo radioResponseInfo, int i) throws RemoteException;

    void handleStkCallSetupRequestFromSimResponse(RadioResponseInfo radioResponseInfo) throws RemoteException;

    void hangupConnectionResponse(RadioResponseInfo radioResponseInfo) throws RemoteException;

    void hangupForegroundResumeBackgroundResponse(RadioResponseInfo radioResponseInfo) throws RemoteException;

    void hangupWaitingOrBackgroundResponse(RadioResponseInfo radioResponseInfo) throws RemoteException;

    void isVoNrEnabledResponse(RadioResponseInfo radioResponseInfo, boolean z) throws RemoteException;

    void rejectCallResponse(RadioResponseInfo radioResponseInfo) throws RemoteException;

    void sendBurstDtmfResponse(RadioResponseInfo radioResponseInfo) throws RemoteException;

    void sendCdmaFeatureCodeResponse(RadioResponseInfo radioResponseInfo) throws RemoteException;

    void sendDtmfResponse(RadioResponseInfo radioResponseInfo) throws RemoteException;

    void sendUssdResponse(RadioResponseInfo radioResponseInfo) throws RemoteException;

    void separateConnectionResponse(RadioResponseInfo radioResponseInfo) throws RemoteException;

    void setCallForwardResponse(RadioResponseInfo radioResponseInfo) throws RemoteException;

    void setCallWaitingResponse(RadioResponseInfo radioResponseInfo) throws RemoteException;

    void setClirResponse(RadioResponseInfo radioResponseInfo) throws RemoteException;

    void setMuteResponse(RadioResponseInfo radioResponseInfo) throws RemoteException;

    void setPreferredVoicePrivacyResponse(RadioResponseInfo radioResponseInfo) throws RemoteException;

    void setTtyModeResponse(RadioResponseInfo radioResponseInfo) throws RemoteException;

    void setVoNrEnabledResponse(RadioResponseInfo radioResponseInfo) throws RemoteException;

    void startDtmfResponse(RadioResponseInfo radioResponseInfo) throws RemoteException;

    void stopDtmfResponse(RadioResponseInfo radioResponseInfo) throws RemoteException;

    void switchWaitingOrHoldingAndActiveResponse(RadioResponseInfo radioResponseInfo) throws RemoteException;

    /* loaded from: classes2.dex */
    public static class Default implements IRadioVoiceResponse {
        @Override // android.hardware.radio.voice.IRadioVoiceResponse
        public void acceptCallResponse(RadioResponseInfo info) throws RemoteException {
        }

        @Override // android.hardware.radio.voice.IRadioVoiceResponse
        public void acknowledgeRequest(int serial) throws RemoteException {
        }

        @Override // android.hardware.radio.voice.IRadioVoiceResponse
        public void cancelPendingUssdResponse(RadioResponseInfo info) throws RemoteException {
        }

        @Override // android.hardware.radio.voice.IRadioVoiceResponse
        public void conferenceResponse(RadioResponseInfo info) throws RemoteException {
        }

        @Override // android.hardware.radio.voice.IRadioVoiceResponse
        public void dialResponse(RadioResponseInfo info) throws RemoteException {
        }

        @Override // android.hardware.radio.voice.IRadioVoiceResponse
        public void emergencyDialResponse(RadioResponseInfo info) throws RemoteException {
        }

        @Override // android.hardware.radio.voice.IRadioVoiceResponse
        public void exitEmergencyCallbackModeResponse(RadioResponseInfo info) throws RemoteException {
        }

        @Override // android.hardware.radio.voice.IRadioVoiceResponse
        public void explicitCallTransferResponse(RadioResponseInfo info) throws RemoteException {
        }

        @Override // android.hardware.radio.voice.IRadioVoiceResponse
        public void getCallForwardStatusResponse(RadioResponseInfo info, CallForwardInfo[] callForwardInfos) throws RemoteException {
        }

        @Override // android.hardware.radio.voice.IRadioVoiceResponse
        public void getCallWaitingResponse(RadioResponseInfo info, boolean enable, int serviceClass) throws RemoteException {
        }

        @Override // android.hardware.radio.voice.IRadioVoiceResponse
        public void getClipResponse(RadioResponseInfo info, int status) throws RemoteException {
        }

        @Override // android.hardware.radio.voice.IRadioVoiceResponse
        public void getClirResponse(RadioResponseInfo info, int n, int m) throws RemoteException {
        }

        @Override // android.hardware.radio.voice.IRadioVoiceResponse
        public void getCurrentCallsResponse(RadioResponseInfo info, Call[] calls) throws RemoteException {
        }

        @Override // android.hardware.radio.voice.IRadioVoiceResponse
        public void getLastCallFailCauseResponse(RadioResponseInfo info, LastCallFailCauseInfo failCauseinfo) throws RemoteException {
        }

        @Override // android.hardware.radio.voice.IRadioVoiceResponse
        public void getMuteResponse(RadioResponseInfo info, boolean enable) throws RemoteException {
        }

        @Override // android.hardware.radio.voice.IRadioVoiceResponse
        public void getPreferredVoicePrivacyResponse(RadioResponseInfo info, boolean enable) throws RemoteException {
        }

        @Override // android.hardware.radio.voice.IRadioVoiceResponse
        public void getTtyModeResponse(RadioResponseInfo info, int mode) throws RemoteException {
        }

        @Override // android.hardware.radio.voice.IRadioVoiceResponse
        public void handleStkCallSetupRequestFromSimResponse(RadioResponseInfo info) throws RemoteException {
        }

        @Override // android.hardware.radio.voice.IRadioVoiceResponse
        public void hangupConnectionResponse(RadioResponseInfo info) throws RemoteException {
        }

        @Override // android.hardware.radio.voice.IRadioVoiceResponse
        public void hangupForegroundResumeBackgroundResponse(RadioResponseInfo info) throws RemoteException {
        }

        @Override // android.hardware.radio.voice.IRadioVoiceResponse
        public void hangupWaitingOrBackgroundResponse(RadioResponseInfo info) throws RemoteException {
        }

        @Override // android.hardware.radio.voice.IRadioVoiceResponse
        public void isVoNrEnabledResponse(RadioResponseInfo info, boolean enable) throws RemoteException {
        }

        @Override // android.hardware.radio.voice.IRadioVoiceResponse
        public void rejectCallResponse(RadioResponseInfo info) throws RemoteException {
        }

        @Override // android.hardware.radio.voice.IRadioVoiceResponse
        public void sendBurstDtmfResponse(RadioResponseInfo info) throws RemoteException {
        }

        @Override // android.hardware.radio.voice.IRadioVoiceResponse
        public void sendCdmaFeatureCodeResponse(RadioResponseInfo info) throws RemoteException {
        }

        @Override // android.hardware.radio.voice.IRadioVoiceResponse
        public void sendDtmfResponse(RadioResponseInfo info) throws RemoteException {
        }

        @Override // android.hardware.radio.voice.IRadioVoiceResponse
        public void sendUssdResponse(RadioResponseInfo info) throws RemoteException {
        }

        @Override // android.hardware.radio.voice.IRadioVoiceResponse
        public void separateConnectionResponse(RadioResponseInfo info) throws RemoteException {
        }

        @Override // android.hardware.radio.voice.IRadioVoiceResponse
        public void setCallForwardResponse(RadioResponseInfo info) throws RemoteException {
        }

        @Override // android.hardware.radio.voice.IRadioVoiceResponse
        public void setCallWaitingResponse(RadioResponseInfo info) throws RemoteException {
        }

        @Override // android.hardware.radio.voice.IRadioVoiceResponse
        public void setClirResponse(RadioResponseInfo info) throws RemoteException {
        }

        @Override // android.hardware.radio.voice.IRadioVoiceResponse
        public void setMuteResponse(RadioResponseInfo info) throws RemoteException {
        }

        @Override // android.hardware.radio.voice.IRadioVoiceResponse
        public void setPreferredVoicePrivacyResponse(RadioResponseInfo info) throws RemoteException {
        }

        @Override // android.hardware.radio.voice.IRadioVoiceResponse
        public void setTtyModeResponse(RadioResponseInfo info) throws RemoteException {
        }

        @Override // android.hardware.radio.voice.IRadioVoiceResponse
        public void setVoNrEnabledResponse(RadioResponseInfo info) throws RemoteException {
        }

        @Override // android.hardware.radio.voice.IRadioVoiceResponse
        public void startDtmfResponse(RadioResponseInfo info) throws RemoteException {
        }

        @Override // android.hardware.radio.voice.IRadioVoiceResponse
        public void stopDtmfResponse(RadioResponseInfo info) throws RemoteException {
        }

        @Override // android.hardware.radio.voice.IRadioVoiceResponse
        public void switchWaitingOrHoldingAndActiveResponse(RadioResponseInfo info) throws RemoteException {
        }

        @Override // android.hardware.radio.voice.IRadioVoiceResponse
        public int getInterfaceVersion() {
            return 0;
        }

        @Override // android.hardware.radio.voice.IRadioVoiceResponse
        public String getInterfaceHash() {
            return "";
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements IRadioVoiceResponse {
        static final int TRANSACTION_acceptCallResponse = 1;
        static final int TRANSACTION_acknowledgeRequest = 2;
        static final int TRANSACTION_cancelPendingUssdResponse = 3;
        static final int TRANSACTION_conferenceResponse = 4;
        static final int TRANSACTION_dialResponse = 5;
        static final int TRANSACTION_emergencyDialResponse = 6;
        static final int TRANSACTION_exitEmergencyCallbackModeResponse = 7;
        static final int TRANSACTION_explicitCallTransferResponse = 8;
        static final int TRANSACTION_getCallForwardStatusResponse = 9;
        static final int TRANSACTION_getCallWaitingResponse = 10;
        static final int TRANSACTION_getClipResponse = 11;
        static final int TRANSACTION_getClirResponse = 12;
        static final int TRANSACTION_getCurrentCallsResponse = 13;
        static final int TRANSACTION_getInterfaceHash = 16777214;
        static final int TRANSACTION_getInterfaceVersion = 16777215;
        static final int TRANSACTION_getLastCallFailCauseResponse = 14;
        static final int TRANSACTION_getMuteResponse = 15;
        static final int TRANSACTION_getPreferredVoicePrivacyResponse = 16;
        static final int TRANSACTION_getTtyModeResponse = 17;
        static final int TRANSACTION_handleStkCallSetupRequestFromSimResponse = 18;
        static final int TRANSACTION_hangupConnectionResponse = 19;
        static final int TRANSACTION_hangupForegroundResumeBackgroundResponse = 20;
        static final int TRANSACTION_hangupWaitingOrBackgroundResponse = 21;
        static final int TRANSACTION_isVoNrEnabledResponse = 22;
        static final int TRANSACTION_rejectCallResponse = 23;
        static final int TRANSACTION_sendBurstDtmfResponse = 24;
        static final int TRANSACTION_sendCdmaFeatureCodeResponse = 25;
        static final int TRANSACTION_sendDtmfResponse = 26;
        static final int TRANSACTION_sendUssdResponse = 27;
        static final int TRANSACTION_separateConnectionResponse = 28;
        static final int TRANSACTION_setCallForwardResponse = 29;
        static final int TRANSACTION_setCallWaitingResponse = 30;
        static final int TRANSACTION_setClirResponse = 31;
        static final int TRANSACTION_setMuteResponse = 32;
        static final int TRANSACTION_setPreferredVoicePrivacyResponse = 33;
        static final int TRANSACTION_setTtyModeResponse = 34;
        static final int TRANSACTION_setVoNrEnabledResponse = 35;
        static final int TRANSACTION_startDtmfResponse = 36;
        static final int TRANSACTION_stopDtmfResponse = 37;
        static final int TRANSACTION_switchWaitingOrHoldingAndActiveResponse = 38;

        public Stub() {
            markVintfStability();
            attachInterface(this, DESCRIPTOR);
        }

        public static IRadioVoiceResponse asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IRadioVoiceResponse)) {
                return (IRadioVoiceResponse) iin;
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
                            acceptCallResponse(_arg0);
                            break;
                        case 2:
                            int _arg02 = data.readInt();
                            data.enforceNoDataAvail();
                            acknowledgeRequest(_arg02);
                            break;
                        case 3:
                            RadioResponseInfo _arg03 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            data.enforceNoDataAvail();
                            cancelPendingUssdResponse(_arg03);
                            break;
                        case 4:
                            RadioResponseInfo _arg04 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            data.enforceNoDataAvail();
                            conferenceResponse(_arg04);
                            break;
                        case 5:
                            RadioResponseInfo _arg05 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            data.enforceNoDataAvail();
                            dialResponse(_arg05);
                            break;
                        case 6:
                            RadioResponseInfo _arg06 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            data.enforceNoDataAvail();
                            emergencyDialResponse(_arg06);
                            break;
                        case 7:
                            RadioResponseInfo _arg07 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            data.enforceNoDataAvail();
                            exitEmergencyCallbackModeResponse(_arg07);
                            break;
                        case 8:
                            RadioResponseInfo _arg08 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            data.enforceNoDataAvail();
                            explicitCallTransferResponse(_arg08);
                            break;
                        case 9:
                            RadioResponseInfo _arg09 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            CallForwardInfo[] _arg1 = (CallForwardInfo[]) data.createTypedArray(CallForwardInfo.CREATOR);
                            data.enforceNoDataAvail();
                            getCallForwardStatusResponse(_arg09, _arg1);
                            break;
                        case 10:
                            RadioResponseInfo _arg010 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            boolean _arg12 = data.readBoolean();
                            int _arg2 = data.readInt();
                            data.enforceNoDataAvail();
                            getCallWaitingResponse(_arg010, _arg12, _arg2);
                            break;
                        case 11:
                            RadioResponseInfo _arg011 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            int _arg13 = data.readInt();
                            data.enforceNoDataAvail();
                            getClipResponse(_arg011, _arg13);
                            break;
                        case 12:
                            RadioResponseInfo _arg012 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            int _arg14 = data.readInt();
                            int _arg22 = data.readInt();
                            data.enforceNoDataAvail();
                            getClirResponse(_arg012, _arg14, _arg22);
                            break;
                        case 13:
                            RadioResponseInfo _arg013 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            Call[] _arg15 = (Call[]) data.createTypedArray(Call.CREATOR);
                            data.enforceNoDataAvail();
                            getCurrentCallsResponse(_arg013, _arg15);
                            break;
                        case 14:
                            RadioResponseInfo _arg014 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            LastCallFailCauseInfo _arg16 = (LastCallFailCauseInfo) data.readTypedObject(LastCallFailCauseInfo.CREATOR);
                            data.enforceNoDataAvail();
                            getLastCallFailCauseResponse(_arg014, _arg16);
                            break;
                        case 15:
                            RadioResponseInfo _arg015 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            boolean _arg17 = data.readBoolean();
                            data.enforceNoDataAvail();
                            getMuteResponse(_arg015, _arg17);
                            break;
                        case 16:
                            RadioResponseInfo _arg016 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            boolean _arg18 = data.readBoolean();
                            data.enforceNoDataAvail();
                            getPreferredVoicePrivacyResponse(_arg016, _arg18);
                            break;
                        case 17:
                            RadioResponseInfo _arg017 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            int _arg19 = data.readInt();
                            data.enforceNoDataAvail();
                            getTtyModeResponse(_arg017, _arg19);
                            break;
                        case 18:
                            RadioResponseInfo _arg018 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            data.enforceNoDataAvail();
                            handleStkCallSetupRequestFromSimResponse(_arg018);
                            break;
                        case 19:
                            RadioResponseInfo _arg019 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            data.enforceNoDataAvail();
                            hangupConnectionResponse(_arg019);
                            break;
                        case 20:
                            RadioResponseInfo _arg020 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            data.enforceNoDataAvail();
                            hangupForegroundResumeBackgroundResponse(_arg020);
                            break;
                        case 21:
                            RadioResponseInfo _arg021 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            data.enforceNoDataAvail();
                            hangupWaitingOrBackgroundResponse(_arg021);
                            break;
                        case 22:
                            RadioResponseInfo _arg022 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            boolean _arg110 = data.readBoolean();
                            data.enforceNoDataAvail();
                            isVoNrEnabledResponse(_arg022, _arg110);
                            break;
                        case 23:
                            RadioResponseInfo _arg023 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            data.enforceNoDataAvail();
                            rejectCallResponse(_arg023);
                            break;
                        case 24:
                            RadioResponseInfo _arg024 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            data.enforceNoDataAvail();
                            sendBurstDtmfResponse(_arg024);
                            break;
                        case 25:
                            RadioResponseInfo _arg025 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            data.enforceNoDataAvail();
                            sendCdmaFeatureCodeResponse(_arg025);
                            break;
                        case 26:
                            RadioResponseInfo _arg026 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            data.enforceNoDataAvail();
                            sendDtmfResponse(_arg026);
                            break;
                        case 27:
                            RadioResponseInfo _arg027 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            data.enforceNoDataAvail();
                            sendUssdResponse(_arg027);
                            break;
                        case 28:
                            RadioResponseInfo _arg028 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            data.enforceNoDataAvail();
                            separateConnectionResponse(_arg028);
                            break;
                        case 29:
                            RadioResponseInfo _arg029 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            data.enforceNoDataAvail();
                            setCallForwardResponse(_arg029);
                            break;
                        case 30:
                            RadioResponseInfo _arg030 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            data.enforceNoDataAvail();
                            setCallWaitingResponse(_arg030);
                            break;
                        case 31:
                            RadioResponseInfo _arg031 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            data.enforceNoDataAvail();
                            setClirResponse(_arg031);
                            break;
                        case 32:
                            RadioResponseInfo _arg032 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            data.enforceNoDataAvail();
                            setMuteResponse(_arg032);
                            break;
                        case 33:
                            RadioResponseInfo _arg033 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            data.enforceNoDataAvail();
                            setPreferredVoicePrivacyResponse(_arg033);
                            break;
                        case 34:
                            RadioResponseInfo _arg034 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            data.enforceNoDataAvail();
                            setTtyModeResponse(_arg034);
                            break;
                        case 35:
                            RadioResponseInfo _arg035 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            data.enforceNoDataAvail();
                            setVoNrEnabledResponse(_arg035);
                            break;
                        case 36:
                            RadioResponseInfo _arg036 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            data.enforceNoDataAvail();
                            startDtmfResponse(_arg036);
                            break;
                        case 37:
                            RadioResponseInfo _arg037 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            data.enforceNoDataAvail();
                            stopDtmfResponse(_arg037);
                            break;
                        case 38:
                            RadioResponseInfo _arg038 = (RadioResponseInfo) data.readTypedObject(RadioResponseInfo.CREATOR);
                            data.enforceNoDataAvail();
                            switchWaitingOrHoldingAndActiveResponse(_arg038);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes2.dex */
        private static class Proxy implements IRadioVoiceResponse {
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

            @Override // android.hardware.radio.voice.IRadioVoiceResponse
            public void acceptCallResponse(RadioResponseInfo info) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    boolean _status = this.mRemote.transact(1, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method acceptCallResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.voice.IRadioVoiceResponse
            public void acknowledgeRequest(int serial) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeInt(serial);
                    boolean _status = this.mRemote.transact(2, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method acknowledgeRequest is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.voice.IRadioVoiceResponse
            public void cancelPendingUssdResponse(RadioResponseInfo info) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    boolean _status = this.mRemote.transact(3, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method cancelPendingUssdResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.voice.IRadioVoiceResponse
            public void conferenceResponse(RadioResponseInfo info) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    boolean _status = this.mRemote.transact(4, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method conferenceResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.voice.IRadioVoiceResponse
            public void dialResponse(RadioResponseInfo info) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    boolean _status = this.mRemote.transact(5, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method dialResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.voice.IRadioVoiceResponse
            public void emergencyDialResponse(RadioResponseInfo info) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    boolean _status = this.mRemote.transact(6, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method emergencyDialResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.voice.IRadioVoiceResponse
            public void exitEmergencyCallbackModeResponse(RadioResponseInfo info) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    boolean _status = this.mRemote.transact(7, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method exitEmergencyCallbackModeResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.voice.IRadioVoiceResponse
            public void explicitCallTransferResponse(RadioResponseInfo info) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    boolean _status = this.mRemote.transact(8, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method explicitCallTransferResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.voice.IRadioVoiceResponse
            public void getCallForwardStatusResponse(RadioResponseInfo info, CallForwardInfo[] callForwardInfos) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    _data.writeTypedArray(callForwardInfos, 0);
                    boolean _status = this.mRemote.transact(9, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method getCallForwardStatusResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.voice.IRadioVoiceResponse
            public void getCallWaitingResponse(RadioResponseInfo info, boolean enable, int serviceClass) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    _data.writeBoolean(enable);
                    _data.writeInt(serviceClass);
                    boolean _status = this.mRemote.transact(10, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method getCallWaitingResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.voice.IRadioVoiceResponse
            public void getClipResponse(RadioResponseInfo info, int status) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    _data.writeInt(status);
                    boolean _status = this.mRemote.transact(11, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method getClipResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.voice.IRadioVoiceResponse
            public void getClirResponse(RadioResponseInfo info, int n, int m) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    _data.writeInt(n);
                    _data.writeInt(m);
                    boolean _status = this.mRemote.transact(12, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method getClirResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.voice.IRadioVoiceResponse
            public void getCurrentCallsResponse(RadioResponseInfo info, Call[] calls) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    _data.writeTypedArray(calls, 0);
                    boolean _status = this.mRemote.transact(13, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method getCurrentCallsResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.voice.IRadioVoiceResponse
            public void getLastCallFailCauseResponse(RadioResponseInfo info, LastCallFailCauseInfo failCauseinfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    _data.writeTypedObject(failCauseinfo, 0);
                    boolean _status = this.mRemote.transact(14, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method getLastCallFailCauseResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.voice.IRadioVoiceResponse
            public void getMuteResponse(RadioResponseInfo info, boolean enable) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    _data.writeBoolean(enable);
                    boolean _status = this.mRemote.transact(15, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method getMuteResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.voice.IRadioVoiceResponse
            public void getPreferredVoicePrivacyResponse(RadioResponseInfo info, boolean enable) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    _data.writeBoolean(enable);
                    boolean _status = this.mRemote.transact(16, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method getPreferredVoicePrivacyResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.voice.IRadioVoiceResponse
            public void getTtyModeResponse(RadioResponseInfo info, int mode) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    _data.writeInt(mode);
                    boolean _status = this.mRemote.transact(17, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method getTtyModeResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.voice.IRadioVoiceResponse
            public void handleStkCallSetupRequestFromSimResponse(RadioResponseInfo info) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    boolean _status = this.mRemote.transact(18, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method handleStkCallSetupRequestFromSimResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.voice.IRadioVoiceResponse
            public void hangupConnectionResponse(RadioResponseInfo info) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    boolean _status = this.mRemote.transact(19, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method hangupConnectionResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.voice.IRadioVoiceResponse
            public void hangupForegroundResumeBackgroundResponse(RadioResponseInfo info) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    boolean _status = this.mRemote.transact(20, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method hangupForegroundResumeBackgroundResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.voice.IRadioVoiceResponse
            public void hangupWaitingOrBackgroundResponse(RadioResponseInfo info) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    boolean _status = this.mRemote.transact(21, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method hangupWaitingOrBackgroundResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.voice.IRadioVoiceResponse
            public void isVoNrEnabledResponse(RadioResponseInfo info, boolean enable) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    _data.writeBoolean(enable);
                    boolean _status = this.mRemote.transact(22, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method isVoNrEnabledResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.voice.IRadioVoiceResponse
            public void rejectCallResponse(RadioResponseInfo info) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    boolean _status = this.mRemote.transact(23, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method rejectCallResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.voice.IRadioVoiceResponse
            public void sendBurstDtmfResponse(RadioResponseInfo info) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    boolean _status = this.mRemote.transact(24, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method sendBurstDtmfResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.voice.IRadioVoiceResponse
            public void sendCdmaFeatureCodeResponse(RadioResponseInfo info) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    boolean _status = this.mRemote.transact(25, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method sendCdmaFeatureCodeResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.voice.IRadioVoiceResponse
            public void sendDtmfResponse(RadioResponseInfo info) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    boolean _status = this.mRemote.transact(26, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method sendDtmfResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.voice.IRadioVoiceResponse
            public void sendUssdResponse(RadioResponseInfo info) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    boolean _status = this.mRemote.transact(27, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method sendUssdResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.voice.IRadioVoiceResponse
            public void separateConnectionResponse(RadioResponseInfo info) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    boolean _status = this.mRemote.transact(28, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method separateConnectionResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.voice.IRadioVoiceResponse
            public void setCallForwardResponse(RadioResponseInfo info) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    boolean _status = this.mRemote.transact(29, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method setCallForwardResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.voice.IRadioVoiceResponse
            public void setCallWaitingResponse(RadioResponseInfo info) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    boolean _status = this.mRemote.transact(30, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method setCallWaitingResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.voice.IRadioVoiceResponse
            public void setClirResponse(RadioResponseInfo info) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    boolean _status = this.mRemote.transact(31, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method setClirResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.voice.IRadioVoiceResponse
            public void setMuteResponse(RadioResponseInfo info) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    boolean _status = this.mRemote.transact(32, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method setMuteResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.voice.IRadioVoiceResponse
            public void setPreferredVoicePrivacyResponse(RadioResponseInfo info) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    boolean _status = this.mRemote.transact(33, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method setPreferredVoicePrivacyResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.voice.IRadioVoiceResponse
            public void setTtyModeResponse(RadioResponseInfo info) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    boolean _status = this.mRemote.transact(34, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method setTtyModeResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.voice.IRadioVoiceResponse
            public void setVoNrEnabledResponse(RadioResponseInfo info) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    boolean _status = this.mRemote.transact(35, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method setVoNrEnabledResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.voice.IRadioVoiceResponse
            public void startDtmfResponse(RadioResponseInfo info) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    boolean _status = this.mRemote.transact(36, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method startDtmfResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.voice.IRadioVoiceResponse
            public void stopDtmfResponse(RadioResponseInfo info) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    boolean _status = this.mRemote.transact(37, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method stopDtmfResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.voice.IRadioVoiceResponse
            public void switchWaitingOrHoldingAndActiveResponse(RadioResponseInfo info) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeTypedObject(info, 0);
                    boolean _status = this.mRemote.transact(38, _data, null, 1);
                    if (!_status) {
                        throw new RemoteException("Method switchWaitingOrHoldingAndActiveResponse is unimplemented.");
                    }
                } finally {
                    _data.recycle();
                }
            }

            @Override // android.hardware.radio.voice.IRadioVoiceResponse
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

            @Override // android.hardware.radio.voice.IRadioVoiceResponse
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
