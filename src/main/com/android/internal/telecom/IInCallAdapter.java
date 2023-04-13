package com.android.internal.telecom;

import android.media.MediaMetrics;
import android.net.Uri;
import android.p008os.Binder;
import android.p008os.Bundle;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import android.p008os.ResultReceiver;
import android.telecom.CallEndpoint;
import android.telecom.PhoneAccountHandle;
import android.telephony.ims.ImsCallProfile;
import java.util.List;
/* loaded from: classes2.dex */
public interface IInCallAdapter extends IInterface {
    void addConferenceParticipants(String str, List<Uri> list) throws RemoteException;

    void answerCall(String str, int i) throws RemoteException;

    void conference(String str, String str2) throws RemoteException;

    void consultativeTransfer(String str, String str2) throws RemoteException;

    void deflectCall(String str, Uri uri) throws RemoteException;

    void disconnectCall(String str) throws RemoteException;

    void enterBackgroundAudioProcessing(String str) throws RemoteException;

    void exitBackgroundAudioProcessing(String str, boolean z) throws RemoteException;

    void handoverTo(String str, PhoneAccountHandle phoneAccountHandle, int i, Bundle bundle) throws RemoteException;

    void holdCall(String str) throws RemoteException;

    void mergeConference(String str) throws RemoteException;

    void mute(boolean z) throws RemoteException;

    void phoneAccountSelected(String str, PhoneAccountHandle phoneAccountHandle, boolean z) throws RemoteException;

    void playDtmfTone(String str, char c) throws RemoteException;

    void postDialContinue(String str, boolean z) throws RemoteException;

    void pullExternalCall(String str) throws RemoteException;

    void putExtras(String str, Bundle bundle) throws RemoteException;

    void rejectCall(String str, boolean z, String str2) throws RemoteException;

    void rejectCallWithReason(String str, int i) throws RemoteException;

    void removeExtras(String str, List<String> list) throws RemoteException;

    void requestCallEndpointChange(CallEndpoint callEndpoint, ResultReceiver resultReceiver) throws RemoteException;

    void respondToRttRequest(String str, int i, boolean z) throws RemoteException;

    void sendCallEvent(String str, String str2, int i, Bundle bundle) throws RemoteException;

    void sendRttRequest(String str) throws RemoteException;

    void setAudioRoute(int i, String str) throws RemoteException;

    void setRttMode(String str, int i) throws RemoteException;

    void splitFromConference(String str) throws RemoteException;

    void stopDtmfTone(String str) throws RemoteException;

    void stopRtt(String str) throws RemoteException;

    void swapConference(String str) throws RemoteException;

    void transferCall(String str, Uri uri, boolean z) throws RemoteException;

    void turnOffProximitySensor(boolean z) throws RemoteException;

    void turnOnProximitySensor() throws RemoteException;

    void unholdCall(String str) throws RemoteException;

    /* loaded from: classes2.dex */
    public static class Default implements IInCallAdapter {
        @Override // com.android.internal.telecom.IInCallAdapter
        public void answerCall(String callId, int videoState) throws RemoteException {
        }

        @Override // com.android.internal.telecom.IInCallAdapter
        public void deflectCall(String callId, Uri address) throws RemoteException {
        }

        @Override // com.android.internal.telecom.IInCallAdapter
        public void rejectCall(String callId, boolean rejectWithMessage, String textMessage) throws RemoteException {
        }

        @Override // com.android.internal.telecom.IInCallAdapter
        public void rejectCallWithReason(String callId, int rejectReason) throws RemoteException {
        }

        @Override // com.android.internal.telecom.IInCallAdapter
        public void transferCall(String callId, Uri targetNumber, boolean isConfirmationRequired) throws RemoteException {
        }

        @Override // com.android.internal.telecom.IInCallAdapter
        public void consultativeTransfer(String callId, String otherCallId) throws RemoteException {
        }

        @Override // com.android.internal.telecom.IInCallAdapter
        public void disconnectCall(String callId) throws RemoteException {
        }

        @Override // com.android.internal.telecom.IInCallAdapter
        public void holdCall(String callId) throws RemoteException {
        }

        @Override // com.android.internal.telecom.IInCallAdapter
        public void unholdCall(String callId) throws RemoteException {
        }

        @Override // com.android.internal.telecom.IInCallAdapter
        public void mute(boolean shouldMute) throws RemoteException {
        }

        @Override // com.android.internal.telecom.IInCallAdapter
        public void setAudioRoute(int route, String bluetoothAddress) throws RemoteException {
        }

        @Override // com.android.internal.telecom.IInCallAdapter
        public void requestCallEndpointChange(CallEndpoint endpoint, ResultReceiver callback) throws RemoteException {
        }

        @Override // com.android.internal.telecom.IInCallAdapter
        public void enterBackgroundAudioProcessing(String callId) throws RemoteException {
        }

        @Override // com.android.internal.telecom.IInCallAdapter
        public void exitBackgroundAudioProcessing(String callId, boolean shouldRing) throws RemoteException {
        }

        @Override // com.android.internal.telecom.IInCallAdapter
        public void playDtmfTone(String callId, char digit) throws RemoteException {
        }

        @Override // com.android.internal.telecom.IInCallAdapter
        public void stopDtmfTone(String callId) throws RemoteException {
        }

        @Override // com.android.internal.telecom.IInCallAdapter
        public void postDialContinue(String callId, boolean proceed) throws RemoteException {
        }

        @Override // com.android.internal.telecom.IInCallAdapter
        public void phoneAccountSelected(String callId, PhoneAccountHandle accountHandle, boolean setDefault) throws RemoteException {
        }

        @Override // com.android.internal.telecom.IInCallAdapter
        public void conference(String callId, String otherCallId) throws RemoteException {
        }

        @Override // com.android.internal.telecom.IInCallAdapter
        public void splitFromConference(String callId) throws RemoteException {
        }

        @Override // com.android.internal.telecom.IInCallAdapter
        public void mergeConference(String callId) throws RemoteException {
        }

        @Override // com.android.internal.telecom.IInCallAdapter
        public void swapConference(String callId) throws RemoteException {
        }

        @Override // com.android.internal.telecom.IInCallAdapter
        public void addConferenceParticipants(String callId, List<Uri> participants) throws RemoteException {
        }

        @Override // com.android.internal.telecom.IInCallAdapter
        public void turnOnProximitySensor() throws RemoteException {
        }

        @Override // com.android.internal.telecom.IInCallAdapter
        public void turnOffProximitySensor(boolean screenOnImmediately) throws RemoteException {
        }

        @Override // com.android.internal.telecom.IInCallAdapter
        public void pullExternalCall(String callId) throws RemoteException {
        }

        @Override // com.android.internal.telecom.IInCallAdapter
        public void sendCallEvent(String callId, String event, int targetSdkVer, Bundle extras) throws RemoteException {
        }

        @Override // com.android.internal.telecom.IInCallAdapter
        public void putExtras(String callId, Bundle extras) throws RemoteException {
        }

        @Override // com.android.internal.telecom.IInCallAdapter
        public void removeExtras(String callId, List<String> keys) throws RemoteException {
        }

        @Override // com.android.internal.telecom.IInCallAdapter
        public void sendRttRequest(String callId) throws RemoteException {
        }

        @Override // com.android.internal.telecom.IInCallAdapter
        public void respondToRttRequest(String callId, int id, boolean accept) throws RemoteException {
        }

        @Override // com.android.internal.telecom.IInCallAdapter
        public void stopRtt(String callId) throws RemoteException {
        }

        @Override // com.android.internal.telecom.IInCallAdapter
        public void setRttMode(String callId, int mode) throws RemoteException {
        }

        @Override // com.android.internal.telecom.IInCallAdapter
        public void handoverTo(String callId, PhoneAccountHandle destAcct, int videoState, Bundle extras) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements IInCallAdapter {
        public static final String DESCRIPTOR = "com.android.internal.telecom.IInCallAdapter";
        static final int TRANSACTION_addConferenceParticipants = 23;
        static final int TRANSACTION_answerCall = 1;
        static final int TRANSACTION_conference = 19;
        static final int TRANSACTION_consultativeTransfer = 6;
        static final int TRANSACTION_deflectCall = 2;
        static final int TRANSACTION_disconnectCall = 7;
        static final int TRANSACTION_enterBackgroundAudioProcessing = 13;
        static final int TRANSACTION_exitBackgroundAudioProcessing = 14;
        static final int TRANSACTION_handoverTo = 34;
        static final int TRANSACTION_holdCall = 8;
        static final int TRANSACTION_mergeConference = 21;
        static final int TRANSACTION_mute = 10;
        static final int TRANSACTION_phoneAccountSelected = 18;
        static final int TRANSACTION_playDtmfTone = 15;
        static final int TRANSACTION_postDialContinue = 17;
        static final int TRANSACTION_pullExternalCall = 26;
        static final int TRANSACTION_putExtras = 28;
        static final int TRANSACTION_rejectCall = 3;
        static final int TRANSACTION_rejectCallWithReason = 4;
        static final int TRANSACTION_removeExtras = 29;
        static final int TRANSACTION_requestCallEndpointChange = 12;
        static final int TRANSACTION_respondToRttRequest = 31;
        static final int TRANSACTION_sendCallEvent = 27;
        static final int TRANSACTION_sendRttRequest = 30;
        static final int TRANSACTION_setAudioRoute = 11;
        static final int TRANSACTION_setRttMode = 33;
        static final int TRANSACTION_splitFromConference = 20;
        static final int TRANSACTION_stopDtmfTone = 16;
        static final int TRANSACTION_stopRtt = 32;
        static final int TRANSACTION_swapConference = 22;
        static final int TRANSACTION_transferCall = 5;
        static final int TRANSACTION_turnOffProximitySensor = 25;
        static final int TRANSACTION_turnOnProximitySensor = 24;
        static final int TRANSACTION_unholdCall = 9;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IInCallAdapter asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IInCallAdapter)) {
                return (IInCallAdapter) iin;
            }
            return new Proxy(obj);
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return this;
        }

        public static String getDefaultTransactionName(int transactionCode) {
            switch (transactionCode) {
                case 1:
                    return "answerCall";
                case 2:
                    return "deflectCall";
                case 3:
                    return "rejectCall";
                case 4:
                    return "rejectCallWithReason";
                case 5:
                    return "transferCall";
                case 6:
                    return "consultativeTransfer";
                case 7:
                    return "disconnectCall";
                case 8:
                    return "holdCall";
                case 9:
                    return "unholdCall";
                case 10:
                    return MediaMetrics.Value.MUTE;
                case 11:
                    return "setAudioRoute";
                case 12:
                    return "requestCallEndpointChange";
                case 13:
                    return "enterBackgroundAudioProcessing";
                case 14:
                    return "exitBackgroundAudioProcessing";
                case 15:
                    return "playDtmfTone";
                case 16:
                    return "stopDtmfTone";
                case 17:
                    return "postDialContinue";
                case 18:
                    return "phoneAccountSelected";
                case 19:
                    return ImsCallProfile.EXTRA_CONFERENCE_DEPRECATED;
                case 20:
                    return "splitFromConference";
                case 21:
                    return "mergeConference";
                case 22:
                    return "swapConference";
                case 23:
                    return "addConferenceParticipants";
                case 24:
                    return "turnOnProximitySensor";
                case 25:
                    return "turnOffProximitySensor";
                case 26:
                    return "pullExternalCall";
                case 27:
                    return "sendCallEvent";
                case 28:
                    return "putExtras";
                case 29:
                    return "removeExtras";
                case 30:
                    return "sendRttRequest";
                case 31:
                    return "respondToRttRequest";
                case 32:
                    return "stopRtt";
                case 33:
                    return "setRttMode";
                case 34:
                    return "handoverTo";
                default:
                    return null;
            }
        }

        @Override // android.p008os.Binder
        public String getTransactionName(int transactionCode) {
            return getDefaultTransactionName(transactionCode);
        }

        @Override // android.p008os.Binder
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            if (code >= 1 && code <= 16777215) {
                data.enforceInterface(DESCRIPTOR);
            }
            switch (code) {
                case IBinder.INTERFACE_TRANSACTION /* 1598968902 */:
                    reply.writeString(DESCRIPTOR);
                    return true;
                default:
                    switch (code) {
                        case 1:
                            String _arg0 = data.readString();
                            int _arg1 = data.readInt();
                            data.enforceNoDataAvail();
                            answerCall(_arg0, _arg1);
                            break;
                        case 2:
                            String _arg02 = data.readString();
                            Uri _arg12 = (Uri) data.readTypedObject(Uri.CREATOR);
                            data.enforceNoDataAvail();
                            deflectCall(_arg02, _arg12);
                            break;
                        case 3:
                            String _arg03 = data.readString();
                            boolean _arg13 = data.readBoolean();
                            String _arg2 = data.readString();
                            data.enforceNoDataAvail();
                            rejectCall(_arg03, _arg13, _arg2);
                            break;
                        case 4:
                            String _arg04 = data.readString();
                            int _arg14 = data.readInt();
                            data.enforceNoDataAvail();
                            rejectCallWithReason(_arg04, _arg14);
                            break;
                        case 5:
                            String _arg05 = data.readString();
                            Uri _arg15 = (Uri) data.readTypedObject(Uri.CREATOR);
                            boolean _arg22 = data.readBoolean();
                            data.enforceNoDataAvail();
                            transferCall(_arg05, _arg15, _arg22);
                            break;
                        case 6:
                            String _arg06 = data.readString();
                            String _arg16 = data.readString();
                            data.enforceNoDataAvail();
                            consultativeTransfer(_arg06, _arg16);
                            break;
                        case 7:
                            String _arg07 = data.readString();
                            data.enforceNoDataAvail();
                            disconnectCall(_arg07);
                            break;
                        case 8:
                            String _arg08 = data.readString();
                            data.enforceNoDataAvail();
                            holdCall(_arg08);
                            break;
                        case 9:
                            String _arg09 = data.readString();
                            data.enforceNoDataAvail();
                            unholdCall(_arg09);
                            break;
                        case 10:
                            boolean _arg010 = data.readBoolean();
                            data.enforceNoDataAvail();
                            mute(_arg010);
                            break;
                        case 11:
                            int _arg011 = data.readInt();
                            String _arg17 = data.readString();
                            data.enforceNoDataAvail();
                            setAudioRoute(_arg011, _arg17);
                            break;
                        case 12:
                            CallEndpoint _arg012 = (CallEndpoint) data.readTypedObject(CallEndpoint.CREATOR);
                            ResultReceiver _arg18 = (ResultReceiver) data.readTypedObject(ResultReceiver.CREATOR);
                            data.enforceNoDataAvail();
                            requestCallEndpointChange(_arg012, _arg18);
                            break;
                        case 13:
                            String _arg013 = data.readString();
                            data.enforceNoDataAvail();
                            enterBackgroundAudioProcessing(_arg013);
                            break;
                        case 14:
                            String _arg014 = data.readString();
                            boolean _arg19 = data.readBoolean();
                            data.enforceNoDataAvail();
                            exitBackgroundAudioProcessing(_arg014, _arg19);
                            break;
                        case 15:
                            String _arg015 = data.readString();
                            char _arg110 = (char) data.readInt();
                            data.enforceNoDataAvail();
                            playDtmfTone(_arg015, _arg110);
                            break;
                        case 16:
                            String _arg016 = data.readString();
                            data.enforceNoDataAvail();
                            stopDtmfTone(_arg016);
                            break;
                        case 17:
                            String _arg017 = data.readString();
                            boolean _arg111 = data.readBoolean();
                            data.enforceNoDataAvail();
                            postDialContinue(_arg017, _arg111);
                            break;
                        case 18:
                            String _arg018 = data.readString();
                            PhoneAccountHandle _arg112 = (PhoneAccountHandle) data.readTypedObject(PhoneAccountHandle.CREATOR);
                            boolean _arg23 = data.readBoolean();
                            data.enforceNoDataAvail();
                            phoneAccountSelected(_arg018, _arg112, _arg23);
                            break;
                        case 19:
                            String _arg019 = data.readString();
                            String _arg113 = data.readString();
                            data.enforceNoDataAvail();
                            conference(_arg019, _arg113);
                            break;
                        case 20:
                            String _arg020 = data.readString();
                            data.enforceNoDataAvail();
                            splitFromConference(_arg020);
                            break;
                        case 21:
                            String _arg021 = data.readString();
                            data.enforceNoDataAvail();
                            mergeConference(_arg021);
                            break;
                        case 22:
                            String _arg022 = data.readString();
                            data.enforceNoDataAvail();
                            swapConference(_arg022);
                            break;
                        case 23:
                            String _arg023 = data.readString();
                            List<Uri> _arg114 = data.createTypedArrayList(Uri.CREATOR);
                            data.enforceNoDataAvail();
                            addConferenceParticipants(_arg023, _arg114);
                            break;
                        case 24:
                            turnOnProximitySensor();
                            break;
                        case 25:
                            boolean _arg024 = data.readBoolean();
                            data.enforceNoDataAvail();
                            turnOffProximitySensor(_arg024);
                            break;
                        case 26:
                            String _arg025 = data.readString();
                            data.enforceNoDataAvail();
                            pullExternalCall(_arg025);
                            break;
                        case 27:
                            String _arg026 = data.readString();
                            String _arg115 = data.readString();
                            int _arg24 = data.readInt();
                            Bundle _arg3 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            data.enforceNoDataAvail();
                            sendCallEvent(_arg026, _arg115, _arg24, _arg3);
                            break;
                        case 28:
                            String _arg027 = data.readString();
                            Bundle _arg116 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            data.enforceNoDataAvail();
                            putExtras(_arg027, _arg116);
                            break;
                        case 29:
                            String _arg028 = data.readString();
                            List<String> _arg117 = data.createStringArrayList();
                            data.enforceNoDataAvail();
                            removeExtras(_arg028, _arg117);
                            break;
                        case 30:
                            String _arg029 = data.readString();
                            data.enforceNoDataAvail();
                            sendRttRequest(_arg029);
                            break;
                        case 31:
                            String _arg030 = data.readString();
                            int _arg118 = data.readInt();
                            boolean _arg25 = data.readBoolean();
                            data.enforceNoDataAvail();
                            respondToRttRequest(_arg030, _arg118, _arg25);
                            break;
                        case 32:
                            String _arg031 = data.readString();
                            data.enforceNoDataAvail();
                            stopRtt(_arg031);
                            break;
                        case 33:
                            String _arg032 = data.readString();
                            int _arg119 = data.readInt();
                            data.enforceNoDataAvail();
                            setRttMode(_arg032, _arg119);
                            break;
                        case 34:
                            String _arg033 = data.readString();
                            PhoneAccountHandle _arg120 = (PhoneAccountHandle) data.readTypedObject(PhoneAccountHandle.CREATOR);
                            int _arg26 = data.readInt();
                            Bundle _arg32 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            data.enforceNoDataAvail();
                            handoverTo(_arg033, _arg120, _arg26, _arg32);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes2.dex */
        private static class Proxy implements IInCallAdapter {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            @Override // android.p008os.IInterface
            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return Stub.DESCRIPTOR;
            }

            @Override // com.android.internal.telecom.IInCallAdapter
            public void answerCall(String callId, int videoState) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callId);
                    _data.writeInt(videoState);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telecom.IInCallAdapter
            public void deflectCall(String callId, Uri address) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callId);
                    _data.writeTypedObject(address, 0);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telecom.IInCallAdapter
            public void rejectCall(String callId, boolean rejectWithMessage, String textMessage) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callId);
                    _data.writeBoolean(rejectWithMessage);
                    _data.writeString(textMessage);
                    this.mRemote.transact(3, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telecom.IInCallAdapter
            public void rejectCallWithReason(String callId, int rejectReason) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callId);
                    _data.writeInt(rejectReason);
                    this.mRemote.transact(4, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telecom.IInCallAdapter
            public void transferCall(String callId, Uri targetNumber, boolean isConfirmationRequired) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callId);
                    _data.writeTypedObject(targetNumber, 0);
                    _data.writeBoolean(isConfirmationRequired);
                    this.mRemote.transact(5, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telecom.IInCallAdapter
            public void consultativeTransfer(String callId, String otherCallId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callId);
                    _data.writeString(otherCallId);
                    this.mRemote.transact(6, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telecom.IInCallAdapter
            public void disconnectCall(String callId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callId);
                    this.mRemote.transact(7, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telecom.IInCallAdapter
            public void holdCall(String callId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callId);
                    this.mRemote.transact(8, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telecom.IInCallAdapter
            public void unholdCall(String callId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callId);
                    this.mRemote.transact(9, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telecom.IInCallAdapter
            public void mute(boolean shouldMute) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeBoolean(shouldMute);
                    this.mRemote.transact(10, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telecom.IInCallAdapter
            public void setAudioRoute(int route, String bluetoothAddress) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeInt(route);
                    _data.writeString(bluetoothAddress);
                    this.mRemote.transact(11, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telecom.IInCallAdapter
            public void requestCallEndpointChange(CallEndpoint endpoint, ResultReceiver callback) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(endpoint, 0);
                    _data.writeTypedObject(callback, 0);
                    this.mRemote.transact(12, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telecom.IInCallAdapter
            public void enterBackgroundAudioProcessing(String callId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callId);
                    this.mRemote.transact(13, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telecom.IInCallAdapter
            public void exitBackgroundAudioProcessing(String callId, boolean shouldRing) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callId);
                    _data.writeBoolean(shouldRing);
                    this.mRemote.transact(14, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telecom.IInCallAdapter
            public void playDtmfTone(String callId, char digit) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callId);
                    _data.writeInt(digit);
                    this.mRemote.transact(15, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telecom.IInCallAdapter
            public void stopDtmfTone(String callId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callId);
                    this.mRemote.transact(16, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telecom.IInCallAdapter
            public void postDialContinue(String callId, boolean proceed) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callId);
                    _data.writeBoolean(proceed);
                    this.mRemote.transact(17, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telecom.IInCallAdapter
            public void phoneAccountSelected(String callId, PhoneAccountHandle accountHandle, boolean setDefault) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callId);
                    _data.writeTypedObject(accountHandle, 0);
                    _data.writeBoolean(setDefault);
                    this.mRemote.transact(18, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telecom.IInCallAdapter
            public void conference(String callId, String otherCallId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callId);
                    _data.writeString(otherCallId);
                    this.mRemote.transact(19, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telecom.IInCallAdapter
            public void splitFromConference(String callId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callId);
                    this.mRemote.transact(20, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telecom.IInCallAdapter
            public void mergeConference(String callId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callId);
                    this.mRemote.transact(21, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telecom.IInCallAdapter
            public void swapConference(String callId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callId);
                    this.mRemote.transact(22, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telecom.IInCallAdapter
            public void addConferenceParticipants(String callId, List<Uri> participants) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callId);
                    _data.writeTypedList(participants, 0);
                    this.mRemote.transact(23, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telecom.IInCallAdapter
            public void turnOnProximitySensor() throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(24, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telecom.IInCallAdapter
            public void turnOffProximitySensor(boolean screenOnImmediately) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeBoolean(screenOnImmediately);
                    this.mRemote.transact(25, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telecom.IInCallAdapter
            public void pullExternalCall(String callId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callId);
                    this.mRemote.transact(26, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telecom.IInCallAdapter
            public void sendCallEvent(String callId, String event, int targetSdkVer, Bundle extras) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callId);
                    _data.writeString(event);
                    _data.writeInt(targetSdkVer);
                    _data.writeTypedObject(extras, 0);
                    this.mRemote.transact(27, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telecom.IInCallAdapter
            public void putExtras(String callId, Bundle extras) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callId);
                    _data.writeTypedObject(extras, 0);
                    this.mRemote.transact(28, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telecom.IInCallAdapter
            public void removeExtras(String callId, List<String> keys) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callId);
                    _data.writeStringList(keys);
                    this.mRemote.transact(29, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telecom.IInCallAdapter
            public void sendRttRequest(String callId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callId);
                    this.mRemote.transact(30, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telecom.IInCallAdapter
            public void respondToRttRequest(String callId, int id, boolean accept) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callId);
                    _data.writeInt(id);
                    _data.writeBoolean(accept);
                    this.mRemote.transact(31, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telecom.IInCallAdapter
            public void stopRtt(String callId) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callId);
                    this.mRemote.transact(32, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telecom.IInCallAdapter
            public void setRttMode(String callId, int mode) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callId);
                    _data.writeInt(mode);
                    this.mRemote.transact(33, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telecom.IInCallAdapter
            public void handoverTo(String callId, PhoneAccountHandle destAcct, int videoState, Bundle extras) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callId);
                    _data.writeTypedObject(destAcct, 0);
                    _data.writeInt(videoState);
                    _data.writeTypedObject(extras, 0);
                    this.mRemote.transact(34, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 33;
        }
    }
}
