package com.android.internal.telecom;

import android.media.MediaMetrics;
import android.net.Uri;
import android.p008os.Binder;
import android.p008os.Bundle;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.ParcelFileDescriptor;
import android.p008os.RemoteException;
import android.telecom.CallAudioState;
import android.telecom.CallEndpoint;
import android.telecom.Connection;
import android.telecom.ConnectionRequest;
import android.telecom.Logging.Session;
import android.telecom.PhoneAccountHandle;
import android.telephony.ims.ImsCallProfile;
import com.android.internal.telecom.IConnectionServiceAdapter;
import java.util.List;
/* loaded from: classes2.dex */
public interface IConnectionService extends IInterface {
    void abort(String str, Session.Info info) throws RemoteException;

    void addConferenceParticipants(String str, List<Uri> list, Session.Info info) throws RemoteException;

    void addConnectionServiceAdapter(IConnectionServiceAdapter iConnectionServiceAdapter, Session.Info info) throws RemoteException;

    void answer(String str, Session.Info info) throws RemoteException;

    void answerVideo(String str, int i, Session.Info info) throws RemoteException;

    void conference(String str, String str2, Session.Info info) throws RemoteException;

    void connectionServiceFocusGained(Session.Info info) throws RemoteException;

    void connectionServiceFocusLost(Session.Info info) throws RemoteException;

    void consultativeTransfer(String str, String str2, Session.Info info) throws RemoteException;

    void createConference(PhoneAccountHandle phoneAccountHandle, String str, ConnectionRequest connectionRequest, boolean z, boolean z2, Session.Info info) throws RemoteException;

    void createConferenceComplete(String str, Session.Info info) throws RemoteException;

    void createConferenceFailed(PhoneAccountHandle phoneAccountHandle, String str, ConnectionRequest connectionRequest, boolean z, Session.Info info) throws RemoteException;

    void createConnection(PhoneAccountHandle phoneAccountHandle, String str, ConnectionRequest connectionRequest, boolean z, boolean z2, Session.Info info) throws RemoteException;

    void createConnectionComplete(String str, Session.Info info) throws RemoteException;

    void createConnectionFailed(PhoneAccountHandle phoneAccountHandle, String str, ConnectionRequest connectionRequest, boolean z, Session.Info info) throws RemoteException;

    void deflect(String str, Uri uri, Session.Info info) throws RemoteException;

    void disconnect(String str, Session.Info info) throws RemoteException;

    void handoverComplete(String str, Session.Info info) throws RemoteException;

    void handoverFailed(String str, ConnectionRequest connectionRequest, int i, Session.Info info) throws RemoteException;

    void hold(String str, Session.Info info) throws RemoteException;

    void mergeConference(String str, Session.Info info) throws RemoteException;

    void onAvailableCallEndpointsChanged(String str, List<CallEndpoint> list, Session.Info info) throws RemoteException;

    void onCallAudioStateChanged(String str, CallAudioState callAudioState, Session.Info info) throws RemoteException;

    void onCallEndpointChanged(String str, CallEndpoint callEndpoint, Session.Info info) throws RemoteException;

    void onCallFilteringCompleted(String str, Connection.CallFilteringCompletionInfo callFilteringCompletionInfo, Session.Info info) throws RemoteException;

    void onExtrasChanged(String str, Bundle bundle, Session.Info info) throws RemoteException;

    void onMuteStateChanged(String str, boolean z, Session.Info info) throws RemoteException;

    void onPostDialContinue(String str, boolean z, Session.Info info) throws RemoteException;

    void onTrackedByNonUiService(String str, boolean z, Session.Info info) throws RemoteException;

    void onUsingAlternativeUi(String str, boolean z, Session.Info info) throws RemoteException;

    void playDtmfTone(String str, char c, Session.Info info) throws RemoteException;

    void pullExternalCall(String str, Session.Info info) throws RemoteException;

    void reject(String str, Session.Info info) throws RemoteException;

    void rejectWithMessage(String str, String str2, Session.Info info) throws RemoteException;

    void rejectWithReason(String str, int i, Session.Info info) throws RemoteException;

    void removeConnectionServiceAdapter(IConnectionServiceAdapter iConnectionServiceAdapter, Session.Info info) throws RemoteException;

    void respondToRttUpgradeRequest(String str, ParcelFileDescriptor parcelFileDescriptor, ParcelFileDescriptor parcelFileDescriptor2, Session.Info info) throws RemoteException;

    void sendCallEvent(String str, String str2, Bundle bundle, Session.Info info) throws RemoteException;

    void silence(String str, Session.Info info) throws RemoteException;

    void splitFromConference(String str, Session.Info info) throws RemoteException;

    void startRtt(String str, ParcelFileDescriptor parcelFileDescriptor, ParcelFileDescriptor parcelFileDescriptor2, Session.Info info) throws RemoteException;

    void stopDtmfTone(String str, Session.Info info) throws RemoteException;

    void stopRtt(String str, Session.Info info) throws RemoteException;

    void swapConference(String str, Session.Info info) throws RemoteException;

    void transfer(String str, Uri uri, boolean z, Session.Info info) throws RemoteException;

    void unhold(String str, Session.Info info) throws RemoteException;

    /* loaded from: classes2.dex */
    public static class Default implements IConnectionService {
        @Override // com.android.internal.telecom.IConnectionService
        public void addConnectionServiceAdapter(IConnectionServiceAdapter adapter, Session.Info sessionInfo) throws RemoteException {
        }

        @Override // com.android.internal.telecom.IConnectionService
        public void removeConnectionServiceAdapter(IConnectionServiceAdapter adapter, Session.Info sessionInfo) throws RemoteException {
        }

        @Override // com.android.internal.telecom.IConnectionService
        public void createConnection(PhoneAccountHandle connectionManagerPhoneAccount, String callId, ConnectionRequest request, boolean isIncoming, boolean isUnknown, Session.Info sessionInfo) throws RemoteException {
        }

        @Override // com.android.internal.telecom.IConnectionService
        public void createConnectionComplete(String callId, Session.Info sessionInfo) throws RemoteException {
        }

        @Override // com.android.internal.telecom.IConnectionService
        public void createConnectionFailed(PhoneAccountHandle connectionManagerPhoneAccount, String callId, ConnectionRequest request, boolean isIncoming, Session.Info sessionInfo) throws RemoteException {
        }

        @Override // com.android.internal.telecom.IConnectionService
        public void createConference(PhoneAccountHandle connectionManagerPhoneAccount, String callId, ConnectionRequest request, boolean isIncoming, boolean isUnknown, Session.Info sessionInfo) throws RemoteException {
        }

        @Override // com.android.internal.telecom.IConnectionService
        public void createConferenceComplete(String callId, Session.Info sessionInfo) throws RemoteException {
        }

        @Override // com.android.internal.telecom.IConnectionService
        public void createConferenceFailed(PhoneAccountHandle connectionManagerPhoneAccount, String callId, ConnectionRequest request, boolean isIncoming, Session.Info sessionInfo) throws RemoteException {
        }

        @Override // com.android.internal.telecom.IConnectionService
        public void abort(String callId, Session.Info sessionInfo) throws RemoteException {
        }

        @Override // com.android.internal.telecom.IConnectionService
        public void answerVideo(String callId, int videoState, Session.Info sessionInfo) throws RemoteException {
        }

        @Override // com.android.internal.telecom.IConnectionService
        public void answer(String callId, Session.Info sessionInfo) throws RemoteException {
        }

        @Override // com.android.internal.telecom.IConnectionService
        public void deflect(String callId, Uri address, Session.Info sessionInfo) throws RemoteException {
        }

        @Override // com.android.internal.telecom.IConnectionService
        public void reject(String callId, Session.Info sessionInfo) throws RemoteException {
        }

        @Override // com.android.internal.telecom.IConnectionService
        public void rejectWithReason(String callId, int rejectReason, Session.Info sessionInfo) throws RemoteException {
        }

        @Override // com.android.internal.telecom.IConnectionService
        public void rejectWithMessage(String callId, String message, Session.Info sessionInfo) throws RemoteException {
        }

        @Override // com.android.internal.telecom.IConnectionService
        public void transfer(String callId, Uri number, boolean isConfirmationRequired, Session.Info sessionInfo) throws RemoteException {
        }

        @Override // com.android.internal.telecom.IConnectionService
        public void consultativeTransfer(String callId, String otherCallId, Session.Info sessionInfo) throws RemoteException {
        }

        @Override // com.android.internal.telecom.IConnectionService
        public void disconnect(String callId, Session.Info sessionInfo) throws RemoteException {
        }

        @Override // com.android.internal.telecom.IConnectionService
        public void silence(String callId, Session.Info sessionInfo) throws RemoteException {
        }

        @Override // com.android.internal.telecom.IConnectionService
        public void hold(String callId, Session.Info sessionInfo) throws RemoteException {
        }

        @Override // com.android.internal.telecom.IConnectionService
        public void unhold(String callId, Session.Info sessionInfo) throws RemoteException {
        }

        @Override // com.android.internal.telecom.IConnectionService
        public void onCallAudioStateChanged(String activeCallId, CallAudioState callAudioState, Session.Info sessionInfo) throws RemoteException {
        }

        @Override // com.android.internal.telecom.IConnectionService
        public void onCallEndpointChanged(String activeCallId, CallEndpoint callEndpoint, Session.Info sessionInfo) throws RemoteException {
        }

        @Override // com.android.internal.telecom.IConnectionService
        public void onAvailableCallEndpointsChanged(String activeCallId, List<CallEndpoint> availableCallEndpoints, Session.Info sessionInfo) throws RemoteException {
        }

        @Override // com.android.internal.telecom.IConnectionService
        public void onMuteStateChanged(String activeCallId, boolean isMuted, Session.Info sessionInfo) throws RemoteException {
        }

        @Override // com.android.internal.telecom.IConnectionService
        public void playDtmfTone(String callId, char digit, Session.Info sessionInfo) throws RemoteException {
        }

        @Override // com.android.internal.telecom.IConnectionService
        public void stopDtmfTone(String callId, Session.Info sessionInfo) throws RemoteException {
        }

        @Override // com.android.internal.telecom.IConnectionService
        public void conference(String conferenceCallId, String callId, Session.Info sessionInfo) throws RemoteException {
        }

        @Override // com.android.internal.telecom.IConnectionService
        public void splitFromConference(String callId, Session.Info sessionInfo) throws RemoteException {
        }

        @Override // com.android.internal.telecom.IConnectionService
        public void mergeConference(String conferenceCallId, Session.Info sessionInfo) throws RemoteException {
        }

        @Override // com.android.internal.telecom.IConnectionService
        public void swapConference(String conferenceCallId, Session.Info sessionInfo) throws RemoteException {
        }

        @Override // com.android.internal.telecom.IConnectionService
        public void addConferenceParticipants(String CallId, List<Uri> participants, Session.Info sessionInfo) throws RemoteException {
        }

        @Override // com.android.internal.telecom.IConnectionService
        public void onPostDialContinue(String callId, boolean proceed, Session.Info sessionInfo) throws RemoteException {
        }

        @Override // com.android.internal.telecom.IConnectionService
        public void pullExternalCall(String callId, Session.Info sessionInfo) throws RemoteException {
        }

        @Override // com.android.internal.telecom.IConnectionService
        public void sendCallEvent(String callId, String event, Bundle extras, Session.Info sessionInfo) throws RemoteException {
        }

        @Override // com.android.internal.telecom.IConnectionService
        public void onCallFilteringCompleted(String callId, Connection.CallFilteringCompletionInfo completionInfo, Session.Info sessionInfo) throws RemoteException {
        }

        @Override // com.android.internal.telecom.IConnectionService
        public void onExtrasChanged(String callId, Bundle extras, Session.Info sessionInfo) throws RemoteException {
        }

        @Override // com.android.internal.telecom.IConnectionService
        public void startRtt(String callId, ParcelFileDescriptor fromInCall, ParcelFileDescriptor toInCall, Session.Info sessionInfo) throws RemoteException {
        }

        @Override // com.android.internal.telecom.IConnectionService
        public void stopRtt(String callId, Session.Info sessionInfo) throws RemoteException {
        }

        @Override // com.android.internal.telecom.IConnectionService
        public void respondToRttUpgradeRequest(String callId, ParcelFileDescriptor fromInCall, ParcelFileDescriptor toInCall, Session.Info sessionInfo) throws RemoteException {
        }

        @Override // com.android.internal.telecom.IConnectionService
        public void connectionServiceFocusLost(Session.Info sessionInfo) throws RemoteException {
        }

        @Override // com.android.internal.telecom.IConnectionService
        public void connectionServiceFocusGained(Session.Info sessionInfo) throws RemoteException {
        }

        @Override // com.android.internal.telecom.IConnectionService
        public void handoverFailed(String callId, ConnectionRequest request, int error, Session.Info sessionInfo) throws RemoteException {
        }

        @Override // com.android.internal.telecom.IConnectionService
        public void handoverComplete(String callId, Session.Info sessionInfo) throws RemoteException {
        }

        @Override // com.android.internal.telecom.IConnectionService
        public void onUsingAlternativeUi(String callId, boolean isUsingAlternativeUi, Session.Info sessionInfo) throws RemoteException {
        }

        @Override // com.android.internal.telecom.IConnectionService
        public void onTrackedByNonUiService(String callId, boolean isTracked, Session.Info sessionInfo) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements IConnectionService {
        public static final String DESCRIPTOR = "com.android.internal.telecom.IConnectionService";
        static final int TRANSACTION_abort = 9;
        static final int TRANSACTION_addConferenceParticipants = 32;
        static final int TRANSACTION_addConnectionServiceAdapter = 1;
        static final int TRANSACTION_answer = 11;
        static final int TRANSACTION_answerVideo = 10;
        static final int TRANSACTION_conference = 28;
        static final int TRANSACTION_connectionServiceFocusGained = 42;
        static final int TRANSACTION_connectionServiceFocusLost = 41;
        static final int TRANSACTION_consultativeTransfer = 17;
        static final int TRANSACTION_createConference = 6;
        static final int TRANSACTION_createConferenceComplete = 7;
        static final int TRANSACTION_createConferenceFailed = 8;
        static final int TRANSACTION_createConnection = 3;
        static final int TRANSACTION_createConnectionComplete = 4;
        static final int TRANSACTION_createConnectionFailed = 5;
        static final int TRANSACTION_deflect = 12;
        static final int TRANSACTION_disconnect = 18;
        static final int TRANSACTION_handoverComplete = 44;
        static final int TRANSACTION_handoverFailed = 43;
        static final int TRANSACTION_hold = 20;
        static final int TRANSACTION_mergeConference = 30;
        static final int TRANSACTION_onAvailableCallEndpointsChanged = 24;
        static final int TRANSACTION_onCallAudioStateChanged = 22;
        static final int TRANSACTION_onCallEndpointChanged = 23;
        static final int TRANSACTION_onCallFilteringCompleted = 36;
        static final int TRANSACTION_onExtrasChanged = 37;
        static final int TRANSACTION_onMuteStateChanged = 25;
        static final int TRANSACTION_onPostDialContinue = 33;
        static final int TRANSACTION_onTrackedByNonUiService = 46;
        static final int TRANSACTION_onUsingAlternativeUi = 45;
        static final int TRANSACTION_playDtmfTone = 26;
        static final int TRANSACTION_pullExternalCall = 34;
        static final int TRANSACTION_reject = 13;
        static final int TRANSACTION_rejectWithMessage = 15;
        static final int TRANSACTION_rejectWithReason = 14;
        static final int TRANSACTION_removeConnectionServiceAdapter = 2;
        static final int TRANSACTION_respondToRttUpgradeRequest = 40;
        static final int TRANSACTION_sendCallEvent = 35;
        static final int TRANSACTION_silence = 19;
        static final int TRANSACTION_splitFromConference = 29;
        static final int TRANSACTION_startRtt = 38;
        static final int TRANSACTION_stopDtmfTone = 27;
        static final int TRANSACTION_stopRtt = 39;
        static final int TRANSACTION_swapConference = 31;
        static final int TRANSACTION_transfer = 16;
        static final int TRANSACTION_unhold = 21;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IConnectionService asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IConnectionService)) {
                return (IConnectionService) iin;
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
                    return "addConnectionServiceAdapter";
                case 2:
                    return "removeConnectionServiceAdapter";
                case 3:
                    return "createConnection";
                case 4:
                    return "createConnectionComplete";
                case 5:
                    return "createConnectionFailed";
                case 6:
                    return "createConference";
                case 7:
                    return "createConferenceComplete";
                case 8:
                    return "createConferenceFailed";
                case 9:
                    return "abort";
                case 10:
                    return "answerVideo";
                case 11:
                    return "answer";
                case 12:
                    return "deflect";
                case 13:
                    return "reject";
                case 14:
                    return "rejectWithReason";
                case 15:
                    return "rejectWithMessage";
                case 16:
                    return "transfer";
                case 17:
                    return "consultativeTransfer";
                case 18:
                    return MediaMetrics.Value.DISCONNECT;
                case 19:
                    return "silence";
                case 20:
                    return "hold";
                case 21:
                    return "unhold";
                case 22:
                    return "onCallAudioStateChanged";
                case 23:
                    return "onCallEndpointChanged";
                case 24:
                    return "onAvailableCallEndpointsChanged";
                case 25:
                    return "onMuteStateChanged";
                case 26:
                    return "playDtmfTone";
                case 27:
                    return "stopDtmfTone";
                case 28:
                    return ImsCallProfile.EXTRA_CONFERENCE_DEPRECATED;
                case 29:
                    return "splitFromConference";
                case 30:
                    return "mergeConference";
                case 31:
                    return "swapConference";
                case 32:
                    return "addConferenceParticipants";
                case 33:
                    return "onPostDialContinue";
                case 34:
                    return "pullExternalCall";
                case 35:
                    return "sendCallEvent";
                case 36:
                    return "onCallFilteringCompleted";
                case 37:
                    return "onExtrasChanged";
                case 38:
                    return "startRtt";
                case 39:
                    return "stopRtt";
                case 40:
                    return "respondToRttUpgradeRequest";
                case 41:
                    return "connectionServiceFocusLost";
                case 42:
                    return "connectionServiceFocusGained";
                case 43:
                    return "handoverFailed";
                case 44:
                    return "handoverComplete";
                case 45:
                    return "onUsingAlternativeUi";
                case 46:
                    return "onTrackedByNonUiService";
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
                            IConnectionServiceAdapter _arg0 = IConnectionServiceAdapter.Stub.asInterface(data.readStrongBinder());
                            Session.Info _arg1 = (Session.Info) data.readTypedObject(Session.Info.CREATOR);
                            data.enforceNoDataAvail();
                            addConnectionServiceAdapter(_arg0, _arg1);
                            break;
                        case 2:
                            IConnectionServiceAdapter _arg02 = IConnectionServiceAdapter.Stub.asInterface(data.readStrongBinder());
                            Session.Info _arg12 = (Session.Info) data.readTypedObject(Session.Info.CREATOR);
                            data.enforceNoDataAvail();
                            removeConnectionServiceAdapter(_arg02, _arg12);
                            break;
                        case 3:
                            PhoneAccountHandle _arg03 = (PhoneAccountHandle) data.readTypedObject(PhoneAccountHandle.CREATOR);
                            String _arg13 = data.readString();
                            ConnectionRequest _arg2 = (ConnectionRequest) data.readTypedObject(ConnectionRequest.CREATOR);
                            boolean _arg3 = data.readBoolean();
                            boolean _arg4 = data.readBoolean();
                            Session.Info _arg5 = (Session.Info) data.readTypedObject(Session.Info.CREATOR);
                            data.enforceNoDataAvail();
                            createConnection(_arg03, _arg13, _arg2, _arg3, _arg4, _arg5);
                            break;
                        case 4:
                            String _arg04 = data.readString();
                            Session.Info _arg14 = (Session.Info) data.readTypedObject(Session.Info.CREATOR);
                            data.enforceNoDataAvail();
                            createConnectionComplete(_arg04, _arg14);
                            break;
                        case 5:
                            PhoneAccountHandle _arg05 = (PhoneAccountHandle) data.readTypedObject(PhoneAccountHandle.CREATOR);
                            String _arg15 = data.readString();
                            ConnectionRequest _arg22 = (ConnectionRequest) data.readTypedObject(ConnectionRequest.CREATOR);
                            boolean _arg32 = data.readBoolean();
                            Session.Info _arg42 = (Session.Info) data.readTypedObject(Session.Info.CREATOR);
                            data.enforceNoDataAvail();
                            createConnectionFailed(_arg05, _arg15, _arg22, _arg32, _arg42);
                            break;
                        case 6:
                            PhoneAccountHandle _arg06 = (PhoneAccountHandle) data.readTypedObject(PhoneAccountHandle.CREATOR);
                            String _arg16 = data.readString();
                            ConnectionRequest _arg23 = (ConnectionRequest) data.readTypedObject(ConnectionRequest.CREATOR);
                            boolean _arg33 = data.readBoolean();
                            boolean _arg43 = data.readBoolean();
                            Session.Info _arg52 = (Session.Info) data.readTypedObject(Session.Info.CREATOR);
                            data.enforceNoDataAvail();
                            createConference(_arg06, _arg16, _arg23, _arg33, _arg43, _arg52);
                            break;
                        case 7:
                            String _arg07 = data.readString();
                            Session.Info _arg17 = (Session.Info) data.readTypedObject(Session.Info.CREATOR);
                            data.enforceNoDataAvail();
                            createConferenceComplete(_arg07, _arg17);
                            break;
                        case 8:
                            PhoneAccountHandle _arg08 = (PhoneAccountHandle) data.readTypedObject(PhoneAccountHandle.CREATOR);
                            String _arg18 = data.readString();
                            ConnectionRequest _arg24 = (ConnectionRequest) data.readTypedObject(ConnectionRequest.CREATOR);
                            boolean _arg34 = data.readBoolean();
                            Session.Info _arg44 = (Session.Info) data.readTypedObject(Session.Info.CREATOR);
                            data.enforceNoDataAvail();
                            createConferenceFailed(_arg08, _arg18, _arg24, _arg34, _arg44);
                            break;
                        case 9:
                            String _arg09 = data.readString();
                            Session.Info _arg19 = (Session.Info) data.readTypedObject(Session.Info.CREATOR);
                            data.enforceNoDataAvail();
                            abort(_arg09, _arg19);
                            break;
                        case 10:
                            String _arg010 = data.readString();
                            int _arg110 = data.readInt();
                            Session.Info _arg25 = (Session.Info) data.readTypedObject(Session.Info.CREATOR);
                            data.enforceNoDataAvail();
                            answerVideo(_arg010, _arg110, _arg25);
                            break;
                        case 11:
                            String _arg011 = data.readString();
                            Session.Info _arg111 = (Session.Info) data.readTypedObject(Session.Info.CREATOR);
                            data.enforceNoDataAvail();
                            answer(_arg011, _arg111);
                            break;
                        case 12:
                            String _arg012 = data.readString();
                            Uri _arg112 = (Uri) data.readTypedObject(Uri.CREATOR);
                            Session.Info _arg26 = (Session.Info) data.readTypedObject(Session.Info.CREATOR);
                            data.enforceNoDataAvail();
                            deflect(_arg012, _arg112, _arg26);
                            break;
                        case 13:
                            String _arg013 = data.readString();
                            Session.Info _arg113 = (Session.Info) data.readTypedObject(Session.Info.CREATOR);
                            data.enforceNoDataAvail();
                            reject(_arg013, _arg113);
                            break;
                        case 14:
                            String _arg014 = data.readString();
                            int _arg114 = data.readInt();
                            Session.Info _arg27 = (Session.Info) data.readTypedObject(Session.Info.CREATOR);
                            data.enforceNoDataAvail();
                            rejectWithReason(_arg014, _arg114, _arg27);
                            break;
                        case 15:
                            String _arg015 = data.readString();
                            String _arg115 = data.readString();
                            Session.Info _arg28 = (Session.Info) data.readTypedObject(Session.Info.CREATOR);
                            data.enforceNoDataAvail();
                            rejectWithMessage(_arg015, _arg115, _arg28);
                            break;
                        case 16:
                            String _arg016 = data.readString();
                            Uri _arg116 = (Uri) data.readTypedObject(Uri.CREATOR);
                            boolean _arg29 = data.readBoolean();
                            Session.Info _arg35 = (Session.Info) data.readTypedObject(Session.Info.CREATOR);
                            data.enforceNoDataAvail();
                            transfer(_arg016, _arg116, _arg29, _arg35);
                            break;
                        case 17:
                            String _arg017 = data.readString();
                            String _arg117 = data.readString();
                            Session.Info _arg210 = (Session.Info) data.readTypedObject(Session.Info.CREATOR);
                            data.enforceNoDataAvail();
                            consultativeTransfer(_arg017, _arg117, _arg210);
                            break;
                        case 18:
                            String _arg018 = data.readString();
                            Session.Info _arg118 = (Session.Info) data.readTypedObject(Session.Info.CREATOR);
                            data.enforceNoDataAvail();
                            disconnect(_arg018, _arg118);
                            break;
                        case 19:
                            String _arg019 = data.readString();
                            Session.Info _arg119 = (Session.Info) data.readTypedObject(Session.Info.CREATOR);
                            data.enforceNoDataAvail();
                            silence(_arg019, _arg119);
                            break;
                        case 20:
                            String _arg020 = data.readString();
                            Session.Info _arg120 = (Session.Info) data.readTypedObject(Session.Info.CREATOR);
                            data.enforceNoDataAvail();
                            hold(_arg020, _arg120);
                            break;
                        case 21:
                            String _arg021 = data.readString();
                            Session.Info _arg121 = (Session.Info) data.readTypedObject(Session.Info.CREATOR);
                            data.enforceNoDataAvail();
                            unhold(_arg021, _arg121);
                            break;
                        case 22:
                            String _arg022 = data.readString();
                            CallAudioState _arg122 = (CallAudioState) data.readTypedObject(CallAudioState.CREATOR);
                            Session.Info _arg211 = (Session.Info) data.readTypedObject(Session.Info.CREATOR);
                            data.enforceNoDataAvail();
                            onCallAudioStateChanged(_arg022, _arg122, _arg211);
                            break;
                        case 23:
                            String _arg023 = data.readString();
                            CallEndpoint _arg123 = (CallEndpoint) data.readTypedObject(CallEndpoint.CREATOR);
                            Session.Info _arg212 = (Session.Info) data.readTypedObject(Session.Info.CREATOR);
                            data.enforceNoDataAvail();
                            onCallEndpointChanged(_arg023, _arg123, _arg212);
                            break;
                        case 24:
                            String _arg024 = data.readString();
                            List<CallEndpoint> _arg124 = data.createTypedArrayList(CallEndpoint.CREATOR);
                            Session.Info _arg213 = (Session.Info) data.readTypedObject(Session.Info.CREATOR);
                            data.enforceNoDataAvail();
                            onAvailableCallEndpointsChanged(_arg024, _arg124, _arg213);
                            break;
                        case 25:
                            String _arg025 = data.readString();
                            boolean _arg125 = data.readBoolean();
                            Session.Info _arg214 = (Session.Info) data.readTypedObject(Session.Info.CREATOR);
                            data.enforceNoDataAvail();
                            onMuteStateChanged(_arg025, _arg125, _arg214);
                            break;
                        case 26:
                            String _arg026 = data.readString();
                            char _arg126 = (char) data.readInt();
                            Session.Info _arg215 = (Session.Info) data.readTypedObject(Session.Info.CREATOR);
                            data.enforceNoDataAvail();
                            playDtmfTone(_arg026, _arg126, _arg215);
                            break;
                        case 27:
                            String _arg027 = data.readString();
                            Session.Info _arg127 = (Session.Info) data.readTypedObject(Session.Info.CREATOR);
                            data.enforceNoDataAvail();
                            stopDtmfTone(_arg027, _arg127);
                            break;
                        case 28:
                            String _arg028 = data.readString();
                            String _arg128 = data.readString();
                            Session.Info _arg216 = (Session.Info) data.readTypedObject(Session.Info.CREATOR);
                            data.enforceNoDataAvail();
                            conference(_arg028, _arg128, _arg216);
                            break;
                        case 29:
                            String _arg029 = data.readString();
                            Session.Info _arg129 = (Session.Info) data.readTypedObject(Session.Info.CREATOR);
                            data.enforceNoDataAvail();
                            splitFromConference(_arg029, _arg129);
                            break;
                        case 30:
                            String _arg030 = data.readString();
                            Session.Info _arg130 = (Session.Info) data.readTypedObject(Session.Info.CREATOR);
                            data.enforceNoDataAvail();
                            mergeConference(_arg030, _arg130);
                            break;
                        case 31:
                            String _arg031 = data.readString();
                            Session.Info _arg131 = (Session.Info) data.readTypedObject(Session.Info.CREATOR);
                            data.enforceNoDataAvail();
                            swapConference(_arg031, _arg131);
                            break;
                        case 32:
                            String _arg032 = data.readString();
                            List<Uri> _arg132 = data.createTypedArrayList(Uri.CREATOR);
                            Session.Info _arg217 = (Session.Info) data.readTypedObject(Session.Info.CREATOR);
                            data.enforceNoDataAvail();
                            addConferenceParticipants(_arg032, _arg132, _arg217);
                            break;
                        case 33:
                            String _arg033 = data.readString();
                            boolean _arg133 = data.readBoolean();
                            Session.Info _arg218 = (Session.Info) data.readTypedObject(Session.Info.CREATOR);
                            data.enforceNoDataAvail();
                            onPostDialContinue(_arg033, _arg133, _arg218);
                            break;
                        case 34:
                            String _arg034 = data.readString();
                            Session.Info _arg134 = (Session.Info) data.readTypedObject(Session.Info.CREATOR);
                            data.enforceNoDataAvail();
                            pullExternalCall(_arg034, _arg134);
                            break;
                        case 35:
                            String _arg035 = data.readString();
                            String _arg135 = data.readString();
                            Bundle _arg219 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            Session.Info _arg36 = (Session.Info) data.readTypedObject(Session.Info.CREATOR);
                            data.enforceNoDataAvail();
                            sendCallEvent(_arg035, _arg135, _arg219, _arg36);
                            break;
                        case 36:
                            String _arg036 = data.readString();
                            Connection.CallFilteringCompletionInfo _arg136 = (Connection.CallFilteringCompletionInfo) data.readTypedObject(Connection.CallFilteringCompletionInfo.CREATOR);
                            Session.Info _arg220 = (Session.Info) data.readTypedObject(Session.Info.CREATOR);
                            data.enforceNoDataAvail();
                            onCallFilteringCompleted(_arg036, _arg136, _arg220);
                            break;
                        case 37:
                            String _arg037 = data.readString();
                            Bundle _arg137 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            Session.Info _arg221 = (Session.Info) data.readTypedObject(Session.Info.CREATOR);
                            data.enforceNoDataAvail();
                            onExtrasChanged(_arg037, _arg137, _arg221);
                            break;
                        case 38:
                            String _arg038 = data.readString();
                            ParcelFileDescriptor _arg138 = (ParcelFileDescriptor) data.readTypedObject(ParcelFileDescriptor.CREATOR);
                            ParcelFileDescriptor _arg222 = (ParcelFileDescriptor) data.readTypedObject(ParcelFileDescriptor.CREATOR);
                            Session.Info _arg37 = (Session.Info) data.readTypedObject(Session.Info.CREATOR);
                            data.enforceNoDataAvail();
                            startRtt(_arg038, _arg138, _arg222, _arg37);
                            break;
                        case 39:
                            String _arg039 = data.readString();
                            Session.Info _arg139 = (Session.Info) data.readTypedObject(Session.Info.CREATOR);
                            data.enforceNoDataAvail();
                            stopRtt(_arg039, _arg139);
                            break;
                        case 40:
                            String _arg040 = data.readString();
                            ParcelFileDescriptor _arg140 = (ParcelFileDescriptor) data.readTypedObject(ParcelFileDescriptor.CREATOR);
                            ParcelFileDescriptor _arg223 = (ParcelFileDescriptor) data.readTypedObject(ParcelFileDescriptor.CREATOR);
                            Session.Info _arg38 = (Session.Info) data.readTypedObject(Session.Info.CREATOR);
                            data.enforceNoDataAvail();
                            respondToRttUpgradeRequest(_arg040, _arg140, _arg223, _arg38);
                            break;
                        case 41:
                            Session.Info _arg041 = (Session.Info) data.readTypedObject(Session.Info.CREATOR);
                            data.enforceNoDataAvail();
                            connectionServiceFocusLost(_arg041);
                            break;
                        case 42:
                            Session.Info _arg042 = (Session.Info) data.readTypedObject(Session.Info.CREATOR);
                            data.enforceNoDataAvail();
                            connectionServiceFocusGained(_arg042);
                            break;
                        case 43:
                            String _arg043 = data.readString();
                            ConnectionRequest _arg141 = (ConnectionRequest) data.readTypedObject(ConnectionRequest.CREATOR);
                            int _arg224 = data.readInt();
                            Session.Info _arg39 = (Session.Info) data.readTypedObject(Session.Info.CREATOR);
                            data.enforceNoDataAvail();
                            handoverFailed(_arg043, _arg141, _arg224, _arg39);
                            break;
                        case 44:
                            String _arg044 = data.readString();
                            Session.Info _arg142 = (Session.Info) data.readTypedObject(Session.Info.CREATOR);
                            data.enforceNoDataAvail();
                            handoverComplete(_arg044, _arg142);
                            break;
                        case 45:
                            String _arg045 = data.readString();
                            boolean _arg143 = data.readBoolean();
                            Session.Info _arg225 = (Session.Info) data.readTypedObject(Session.Info.CREATOR);
                            data.enforceNoDataAvail();
                            onUsingAlternativeUi(_arg045, _arg143, _arg225);
                            break;
                        case 46:
                            String _arg046 = data.readString();
                            boolean _arg144 = data.readBoolean();
                            Session.Info _arg226 = (Session.Info) data.readTypedObject(Session.Info.CREATOR);
                            data.enforceNoDataAvail();
                            onTrackedByNonUiService(_arg046, _arg144, _arg226);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: classes2.dex */
        public static class Proxy implements IConnectionService {
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

            @Override // com.android.internal.telecom.IConnectionService
            public void addConnectionServiceAdapter(IConnectionServiceAdapter adapter, Session.Info sessionInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(adapter);
                    _data.writeTypedObject(sessionInfo, 0);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telecom.IConnectionService
            public void removeConnectionServiceAdapter(IConnectionServiceAdapter adapter, Session.Info sessionInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(adapter);
                    _data.writeTypedObject(sessionInfo, 0);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telecom.IConnectionService
            public void createConnection(PhoneAccountHandle connectionManagerPhoneAccount, String callId, ConnectionRequest request, boolean isIncoming, boolean isUnknown, Session.Info sessionInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(connectionManagerPhoneAccount, 0);
                    _data.writeString(callId);
                    _data.writeTypedObject(request, 0);
                    _data.writeBoolean(isIncoming);
                    _data.writeBoolean(isUnknown);
                    _data.writeTypedObject(sessionInfo, 0);
                    this.mRemote.transact(3, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telecom.IConnectionService
            public void createConnectionComplete(String callId, Session.Info sessionInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callId);
                    _data.writeTypedObject(sessionInfo, 0);
                    this.mRemote.transact(4, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telecom.IConnectionService
            public void createConnectionFailed(PhoneAccountHandle connectionManagerPhoneAccount, String callId, ConnectionRequest request, boolean isIncoming, Session.Info sessionInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(connectionManagerPhoneAccount, 0);
                    _data.writeString(callId);
                    _data.writeTypedObject(request, 0);
                    _data.writeBoolean(isIncoming);
                    _data.writeTypedObject(sessionInfo, 0);
                    this.mRemote.transact(5, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telecom.IConnectionService
            public void createConference(PhoneAccountHandle connectionManagerPhoneAccount, String callId, ConnectionRequest request, boolean isIncoming, boolean isUnknown, Session.Info sessionInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(connectionManagerPhoneAccount, 0);
                    _data.writeString(callId);
                    _data.writeTypedObject(request, 0);
                    _data.writeBoolean(isIncoming);
                    _data.writeBoolean(isUnknown);
                    _data.writeTypedObject(sessionInfo, 0);
                    this.mRemote.transact(6, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telecom.IConnectionService
            public void createConferenceComplete(String callId, Session.Info sessionInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callId);
                    _data.writeTypedObject(sessionInfo, 0);
                    this.mRemote.transact(7, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telecom.IConnectionService
            public void createConferenceFailed(PhoneAccountHandle connectionManagerPhoneAccount, String callId, ConnectionRequest request, boolean isIncoming, Session.Info sessionInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(connectionManagerPhoneAccount, 0);
                    _data.writeString(callId);
                    _data.writeTypedObject(request, 0);
                    _data.writeBoolean(isIncoming);
                    _data.writeTypedObject(sessionInfo, 0);
                    this.mRemote.transact(8, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telecom.IConnectionService
            public void abort(String callId, Session.Info sessionInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callId);
                    _data.writeTypedObject(sessionInfo, 0);
                    this.mRemote.transact(9, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telecom.IConnectionService
            public void answerVideo(String callId, int videoState, Session.Info sessionInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callId);
                    _data.writeInt(videoState);
                    _data.writeTypedObject(sessionInfo, 0);
                    this.mRemote.transact(10, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telecom.IConnectionService
            public void answer(String callId, Session.Info sessionInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callId);
                    _data.writeTypedObject(sessionInfo, 0);
                    this.mRemote.transact(11, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telecom.IConnectionService
            public void deflect(String callId, Uri address, Session.Info sessionInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callId);
                    _data.writeTypedObject(address, 0);
                    _data.writeTypedObject(sessionInfo, 0);
                    this.mRemote.transact(12, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telecom.IConnectionService
            public void reject(String callId, Session.Info sessionInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callId);
                    _data.writeTypedObject(sessionInfo, 0);
                    this.mRemote.transact(13, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telecom.IConnectionService
            public void rejectWithReason(String callId, int rejectReason, Session.Info sessionInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callId);
                    _data.writeInt(rejectReason);
                    _data.writeTypedObject(sessionInfo, 0);
                    this.mRemote.transact(14, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telecom.IConnectionService
            public void rejectWithMessage(String callId, String message, Session.Info sessionInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callId);
                    _data.writeString(message);
                    _data.writeTypedObject(sessionInfo, 0);
                    this.mRemote.transact(15, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telecom.IConnectionService
            public void transfer(String callId, Uri number, boolean isConfirmationRequired, Session.Info sessionInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callId);
                    _data.writeTypedObject(number, 0);
                    _data.writeBoolean(isConfirmationRequired);
                    _data.writeTypedObject(sessionInfo, 0);
                    this.mRemote.transact(16, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telecom.IConnectionService
            public void consultativeTransfer(String callId, String otherCallId, Session.Info sessionInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callId);
                    _data.writeString(otherCallId);
                    _data.writeTypedObject(sessionInfo, 0);
                    this.mRemote.transact(17, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telecom.IConnectionService
            public void disconnect(String callId, Session.Info sessionInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callId);
                    _data.writeTypedObject(sessionInfo, 0);
                    this.mRemote.transact(18, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telecom.IConnectionService
            public void silence(String callId, Session.Info sessionInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callId);
                    _data.writeTypedObject(sessionInfo, 0);
                    this.mRemote.transact(19, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telecom.IConnectionService
            public void hold(String callId, Session.Info sessionInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callId);
                    _data.writeTypedObject(sessionInfo, 0);
                    this.mRemote.transact(20, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telecom.IConnectionService
            public void unhold(String callId, Session.Info sessionInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callId);
                    _data.writeTypedObject(sessionInfo, 0);
                    this.mRemote.transact(21, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telecom.IConnectionService
            public void onCallAudioStateChanged(String activeCallId, CallAudioState callAudioState, Session.Info sessionInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(activeCallId);
                    _data.writeTypedObject(callAudioState, 0);
                    _data.writeTypedObject(sessionInfo, 0);
                    this.mRemote.transact(22, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telecom.IConnectionService
            public void onCallEndpointChanged(String activeCallId, CallEndpoint callEndpoint, Session.Info sessionInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(activeCallId);
                    _data.writeTypedObject(callEndpoint, 0);
                    _data.writeTypedObject(sessionInfo, 0);
                    this.mRemote.transact(23, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telecom.IConnectionService
            public void onAvailableCallEndpointsChanged(String activeCallId, List<CallEndpoint> availableCallEndpoints, Session.Info sessionInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(activeCallId);
                    _data.writeTypedList(availableCallEndpoints, 0);
                    _data.writeTypedObject(sessionInfo, 0);
                    this.mRemote.transact(24, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telecom.IConnectionService
            public void onMuteStateChanged(String activeCallId, boolean isMuted, Session.Info sessionInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(activeCallId);
                    _data.writeBoolean(isMuted);
                    _data.writeTypedObject(sessionInfo, 0);
                    this.mRemote.transact(25, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telecom.IConnectionService
            public void playDtmfTone(String callId, char digit, Session.Info sessionInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callId);
                    _data.writeInt(digit);
                    _data.writeTypedObject(sessionInfo, 0);
                    this.mRemote.transact(26, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telecom.IConnectionService
            public void stopDtmfTone(String callId, Session.Info sessionInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callId);
                    _data.writeTypedObject(sessionInfo, 0);
                    this.mRemote.transact(27, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telecom.IConnectionService
            public void conference(String conferenceCallId, String callId, Session.Info sessionInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(conferenceCallId);
                    _data.writeString(callId);
                    _data.writeTypedObject(sessionInfo, 0);
                    this.mRemote.transact(28, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telecom.IConnectionService
            public void splitFromConference(String callId, Session.Info sessionInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callId);
                    _data.writeTypedObject(sessionInfo, 0);
                    this.mRemote.transact(29, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telecom.IConnectionService
            public void mergeConference(String conferenceCallId, Session.Info sessionInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(conferenceCallId);
                    _data.writeTypedObject(sessionInfo, 0);
                    this.mRemote.transact(30, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telecom.IConnectionService
            public void swapConference(String conferenceCallId, Session.Info sessionInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(conferenceCallId);
                    _data.writeTypedObject(sessionInfo, 0);
                    this.mRemote.transact(31, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telecom.IConnectionService
            public void addConferenceParticipants(String CallId, List<Uri> participants, Session.Info sessionInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(CallId);
                    _data.writeTypedList(participants, 0);
                    _data.writeTypedObject(sessionInfo, 0);
                    this.mRemote.transact(32, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telecom.IConnectionService
            public void onPostDialContinue(String callId, boolean proceed, Session.Info sessionInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callId);
                    _data.writeBoolean(proceed);
                    _data.writeTypedObject(sessionInfo, 0);
                    this.mRemote.transact(33, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telecom.IConnectionService
            public void pullExternalCall(String callId, Session.Info sessionInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callId);
                    _data.writeTypedObject(sessionInfo, 0);
                    this.mRemote.transact(34, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telecom.IConnectionService
            public void sendCallEvent(String callId, String event, Bundle extras, Session.Info sessionInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callId);
                    _data.writeString(event);
                    _data.writeTypedObject(extras, 0);
                    _data.writeTypedObject(sessionInfo, 0);
                    this.mRemote.transact(35, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telecom.IConnectionService
            public void onCallFilteringCompleted(String callId, Connection.CallFilteringCompletionInfo completionInfo, Session.Info sessionInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callId);
                    _data.writeTypedObject(completionInfo, 0);
                    _data.writeTypedObject(sessionInfo, 0);
                    this.mRemote.transact(36, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telecom.IConnectionService
            public void onExtrasChanged(String callId, Bundle extras, Session.Info sessionInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callId);
                    _data.writeTypedObject(extras, 0);
                    _data.writeTypedObject(sessionInfo, 0);
                    this.mRemote.transact(37, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telecom.IConnectionService
            public void startRtt(String callId, ParcelFileDescriptor fromInCall, ParcelFileDescriptor toInCall, Session.Info sessionInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callId);
                    _data.writeTypedObject(fromInCall, 0);
                    _data.writeTypedObject(toInCall, 0);
                    _data.writeTypedObject(sessionInfo, 0);
                    this.mRemote.transact(38, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telecom.IConnectionService
            public void stopRtt(String callId, Session.Info sessionInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callId);
                    _data.writeTypedObject(sessionInfo, 0);
                    this.mRemote.transact(39, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telecom.IConnectionService
            public void respondToRttUpgradeRequest(String callId, ParcelFileDescriptor fromInCall, ParcelFileDescriptor toInCall, Session.Info sessionInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callId);
                    _data.writeTypedObject(fromInCall, 0);
                    _data.writeTypedObject(toInCall, 0);
                    _data.writeTypedObject(sessionInfo, 0);
                    this.mRemote.transact(40, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telecom.IConnectionService
            public void connectionServiceFocusLost(Session.Info sessionInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(sessionInfo, 0);
                    this.mRemote.transact(41, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telecom.IConnectionService
            public void connectionServiceFocusGained(Session.Info sessionInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(sessionInfo, 0);
                    this.mRemote.transact(42, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telecom.IConnectionService
            public void handoverFailed(String callId, ConnectionRequest request, int error, Session.Info sessionInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callId);
                    _data.writeTypedObject(request, 0);
                    _data.writeInt(error);
                    _data.writeTypedObject(sessionInfo, 0);
                    this.mRemote.transact(43, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telecom.IConnectionService
            public void handoverComplete(String callId, Session.Info sessionInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callId);
                    _data.writeTypedObject(sessionInfo, 0);
                    this.mRemote.transact(44, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telecom.IConnectionService
            public void onUsingAlternativeUi(String callId, boolean isUsingAlternativeUi, Session.Info sessionInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callId);
                    _data.writeBoolean(isUsingAlternativeUi);
                    _data.writeTypedObject(sessionInfo, 0);
                    this.mRemote.transact(45, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telecom.IConnectionService
            public void onTrackedByNonUiService(String callId, boolean isTracked, Session.Info sessionInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callId);
                    _data.writeBoolean(isTracked);
                    _data.writeTypedObject(sessionInfo, 0);
                    this.mRemote.transact(46, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 45;
        }
    }
}
