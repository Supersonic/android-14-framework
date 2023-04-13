package com.android.internal.telecom;

import android.net.Uri;
import android.p008os.Binder;
import android.p008os.Bundle;
import android.p008os.IBinder;
import android.p008os.IInterface;
import android.p008os.Parcel;
import android.p008os.RemoteException;
import android.p008os.ResultReceiver;
import android.telecom.CallEndpoint;
import android.telecom.ConnectionRequest;
import android.telecom.DisconnectCause;
import android.telecom.Logging.Session;
import android.telecom.ParcelableConference;
import android.telecom.ParcelableConnection;
import android.telecom.PhoneAccountHandle;
import android.telecom.StatusHints;
import com.android.internal.telecom.IVideoProvider;
import com.android.internal.telecom.RemoteServiceCallback;
import java.util.List;
/* loaded from: classes2.dex */
public interface IConnectionServiceAdapter extends IInterface {
    void addConferenceCall(String str, ParcelableConference parcelableConference, Session.Info info) throws RemoteException;

    void addExistingConnection(String str, ParcelableConnection parcelableConnection, Session.Info info) throws RemoteException;

    void handleCreateConferenceComplete(String str, ConnectionRequest connectionRequest, ParcelableConference parcelableConference, Session.Info info) throws RemoteException;

    void handleCreateConnectionComplete(String str, ConnectionRequest connectionRequest, ParcelableConnection parcelableConnection, Session.Info info) throws RemoteException;

    void onConnectionEvent(String str, String str2, Bundle bundle, Session.Info info) throws RemoteException;

    void onConnectionServiceFocusReleased(Session.Info info) throws RemoteException;

    void onPhoneAccountChanged(String str, PhoneAccountHandle phoneAccountHandle, Session.Info info) throws RemoteException;

    void onPostDialChar(String str, char c, Session.Info info) throws RemoteException;

    void onPostDialWait(String str, String str2, Session.Info info) throws RemoteException;

    void onRemoteRttRequest(String str, Session.Info info) throws RemoteException;

    void onRttInitiationFailure(String str, int i, Session.Info info) throws RemoteException;

    void onRttInitiationSuccess(String str, Session.Info info) throws RemoteException;

    void onRttSessionRemotelyTerminated(String str, Session.Info info) throws RemoteException;

    void putExtras(String str, Bundle bundle, Session.Info info) throws RemoteException;

    void queryLocation(String str, long j, String str2, ResultReceiver resultReceiver, Session.Info info) throws RemoteException;

    void queryRemoteConnectionServices(RemoteServiceCallback remoteServiceCallback, String str, Session.Info info) throws RemoteException;

    void removeCall(String str, Session.Info info) throws RemoteException;

    void removeExtras(String str, List<String> list, Session.Info info) throws RemoteException;

    void requestCallEndpointChange(String str, CallEndpoint callEndpoint, ResultReceiver resultReceiver, Session.Info info) throws RemoteException;

    void resetConnectionTime(String str, Session.Info info) throws RemoteException;

    void setActive(String str, Session.Info info) throws RemoteException;

    void setAddress(String str, Uri uri, int i, Session.Info info) throws RemoteException;

    void setAudioRoute(String str, int i, String str2, Session.Info info) throws RemoteException;

    void setCallDirection(String str, int i, Session.Info info) throws RemoteException;

    void setCallerDisplayName(String str, String str2, int i, Session.Info info) throws RemoteException;

    void setConferenceMergeFailed(String str, Session.Info info) throws RemoteException;

    void setConferenceState(String str, boolean z, Session.Info info) throws RemoteException;

    void setConferenceableConnections(String str, List<String> list, Session.Info info) throws RemoteException;

    void setConnectionCapabilities(String str, int i, Session.Info info) throws RemoteException;

    void setConnectionProperties(String str, int i, Session.Info info) throws RemoteException;

    void setDialing(String str, Session.Info info) throws RemoteException;

    void setDisconnected(String str, DisconnectCause disconnectCause, Session.Info info) throws RemoteException;

    void setIsConferenced(String str, String str2, Session.Info info) throws RemoteException;

    void setIsVoipAudioMode(String str, boolean z, Session.Info info) throws RemoteException;

    void setOnHold(String str, Session.Info info) throws RemoteException;

    void setPulling(String str, Session.Info info) throws RemoteException;

    void setRingbackRequested(String str, boolean z, Session.Info info) throws RemoteException;

    void setRinging(String str, Session.Info info) throws RemoteException;

    void setStatusHints(String str, StatusHints statusHints, Session.Info info) throws RemoteException;

    void setVideoProvider(String str, IVideoProvider iVideoProvider, Session.Info info) throws RemoteException;

    void setVideoState(String str, int i, Session.Info info) throws RemoteException;

    /* loaded from: classes2.dex */
    public static class Default implements IConnectionServiceAdapter {
        @Override // com.android.internal.telecom.IConnectionServiceAdapter
        public void handleCreateConnectionComplete(String callId, ConnectionRequest request, ParcelableConnection connection, Session.Info sessionInfo) throws RemoteException {
        }

        @Override // com.android.internal.telecom.IConnectionServiceAdapter
        public void handleCreateConferenceComplete(String callId, ConnectionRequest request, ParcelableConference connection, Session.Info sessionInfo) throws RemoteException {
        }

        @Override // com.android.internal.telecom.IConnectionServiceAdapter
        public void setActive(String callId, Session.Info sessionInfo) throws RemoteException {
        }

        @Override // com.android.internal.telecom.IConnectionServiceAdapter
        public void setRinging(String callId, Session.Info sessionInfo) throws RemoteException {
        }

        @Override // com.android.internal.telecom.IConnectionServiceAdapter
        public void setDialing(String callId, Session.Info sessionInfo) throws RemoteException {
        }

        @Override // com.android.internal.telecom.IConnectionServiceAdapter
        public void setPulling(String callId, Session.Info sessionInfo) throws RemoteException {
        }

        @Override // com.android.internal.telecom.IConnectionServiceAdapter
        public void setDisconnected(String callId, DisconnectCause disconnectCause, Session.Info sessionInfo) throws RemoteException {
        }

        @Override // com.android.internal.telecom.IConnectionServiceAdapter
        public void setOnHold(String callId, Session.Info sessionInfo) throws RemoteException {
        }

        @Override // com.android.internal.telecom.IConnectionServiceAdapter
        public void setRingbackRequested(String callId, boolean ringing, Session.Info sessionInfo) throws RemoteException {
        }

        @Override // com.android.internal.telecom.IConnectionServiceAdapter
        public void setConnectionCapabilities(String callId, int connectionCapabilities, Session.Info sessionInfo) throws RemoteException {
        }

        @Override // com.android.internal.telecom.IConnectionServiceAdapter
        public void setConnectionProperties(String callId, int connectionProperties, Session.Info sessionInfo) throws RemoteException {
        }

        @Override // com.android.internal.telecom.IConnectionServiceAdapter
        public void setIsConferenced(String callId, String conferenceCallId, Session.Info sessionInfo) throws RemoteException {
        }

        @Override // com.android.internal.telecom.IConnectionServiceAdapter
        public void setConferenceMergeFailed(String callId, Session.Info sessionInfo) throws RemoteException {
        }

        @Override // com.android.internal.telecom.IConnectionServiceAdapter
        public void addConferenceCall(String callId, ParcelableConference conference, Session.Info sessionInfo) throws RemoteException {
        }

        @Override // com.android.internal.telecom.IConnectionServiceAdapter
        public void removeCall(String callId, Session.Info sessionInfo) throws RemoteException {
        }

        @Override // com.android.internal.telecom.IConnectionServiceAdapter
        public void onPostDialWait(String callId, String remaining, Session.Info sessionInfo) throws RemoteException {
        }

        @Override // com.android.internal.telecom.IConnectionServiceAdapter
        public void onPostDialChar(String callId, char nextChar, Session.Info sessionInfo) throws RemoteException {
        }

        @Override // com.android.internal.telecom.IConnectionServiceAdapter
        public void queryRemoteConnectionServices(RemoteServiceCallback callback, String callingPackage, Session.Info sessionInfo) throws RemoteException {
        }

        @Override // com.android.internal.telecom.IConnectionServiceAdapter
        public void setVideoProvider(String callId, IVideoProvider videoProvider, Session.Info sessionInfo) throws RemoteException {
        }

        @Override // com.android.internal.telecom.IConnectionServiceAdapter
        public void setVideoState(String callId, int videoState, Session.Info sessionInfo) throws RemoteException {
        }

        @Override // com.android.internal.telecom.IConnectionServiceAdapter
        public void setIsVoipAudioMode(String callId, boolean isVoip, Session.Info sessionInfo) throws RemoteException {
        }

        @Override // com.android.internal.telecom.IConnectionServiceAdapter
        public void setStatusHints(String callId, StatusHints statusHints, Session.Info sessionInfo) throws RemoteException {
        }

        @Override // com.android.internal.telecom.IConnectionServiceAdapter
        public void setAddress(String callId, Uri address, int presentation, Session.Info sessionInfo) throws RemoteException {
        }

        @Override // com.android.internal.telecom.IConnectionServiceAdapter
        public void setCallerDisplayName(String callId, String callerDisplayName, int presentation, Session.Info sessionInfo) throws RemoteException {
        }

        @Override // com.android.internal.telecom.IConnectionServiceAdapter
        public void setConferenceableConnections(String callId, List<String> conferenceableCallIds, Session.Info sessionInfo) throws RemoteException {
        }

        @Override // com.android.internal.telecom.IConnectionServiceAdapter
        public void addExistingConnection(String callId, ParcelableConnection connection, Session.Info sessionInfo) throws RemoteException {
        }

        @Override // com.android.internal.telecom.IConnectionServiceAdapter
        public void putExtras(String callId, Bundle extras, Session.Info sessionInfo) throws RemoteException {
        }

        @Override // com.android.internal.telecom.IConnectionServiceAdapter
        public void removeExtras(String callId, List<String> keys, Session.Info sessionInfo) throws RemoteException {
        }

        @Override // com.android.internal.telecom.IConnectionServiceAdapter
        public void setAudioRoute(String callId, int audioRoute, String bluetoothAddress, Session.Info sessionInfo) throws RemoteException {
        }

        @Override // com.android.internal.telecom.IConnectionServiceAdapter
        public void requestCallEndpointChange(String callId, CallEndpoint endpoint, ResultReceiver callback, Session.Info sessionInfo) throws RemoteException {
        }

        @Override // com.android.internal.telecom.IConnectionServiceAdapter
        public void onConnectionEvent(String callId, String event, Bundle extras, Session.Info sessionInfo) throws RemoteException {
        }

        @Override // com.android.internal.telecom.IConnectionServiceAdapter
        public void onRttInitiationSuccess(String callId, Session.Info sessionInfo) throws RemoteException {
        }

        @Override // com.android.internal.telecom.IConnectionServiceAdapter
        public void onRttInitiationFailure(String callId, int reason, Session.Info sessionInfo) throws RemoteException {
        }

        @Override // com.android.internal.telecom.IConnectionServiceAdapter
        public void onRttSessionRemotelyTerminated(String callId, Session.Info sessionInfo) throws RemoteException {
        }

        @Override // com.android.internal.telecom.IConnectionServiceAdapter
        public void onRemoteRttRequest(String callId, Session.Info sessionInfo) throws RemoteException {
        }

        @Override // com.android.internal.telecom.IConnectionServiceAdapter
        public void onPhoneAccountChanged(String callId, PhoneAccountHandle pHandle, Session.Info sessionInfo) throws RemoteException {
        }

        @Override // com.android.internal.telecom.IConnectionServiceAdapter
        public void onConnectionServiceFocusReleased(Session.Info sessionInfo) throws RemoteException {
        }

        @Override // com.android.internal.telecom.IConnectionServiceAdapter
        public void resetConnectionTime(String callIdi, Session.Info sessionInfo) throws RemoteException {
        }

        @Override // com.android.internal.telecom.IConnectionServiceAdapter
        public void setConferenceState(String callId, boolean isConference, Session.Info sessionInfo) throws RemoteException {
        }

        @Override // com.android.internal.telecom.IConnectionServiceAdapter
        public void setCallDirection(String callId, int direction, Session.Info sessionInfo) throws RemoteException {
        }

        @Override // com.android.internal.telecom.IConnectionServiceAdapter
        public void queryLocation(String callId, long timeoutMillis, String provider, ResultReceiver callback, Session.Info sessionInfo) throws RemoteException {
        }

        @Override // android.p008os.IInterface
        public IBinder asBinder() {
            return null;
        }
    }

    /* loaded from: classes2.dex */
    public static abstract class Stub extends Binder implements IConnectionServiceAdapter {
        public static final String DESCRIPTOR = "com.android.internal.telecom.IConnectionServiceAdapter";
        static final int TRANSACTION_addConferenceCall = 14;
        static final int TRANSACTION_addExistingConnection = 26;
        static final int TRANSACTION_handleCreateConferenceComplete = 2;
        static final int TRANSACTION_handleCreateConnectionComplete = 1;
        static final int TRANSACTION_onConnectionEvent = 31;
        static final int TRANSACTION_onConnectionServiceFocusReleased = 37;
        static final int TRANSACTION_onPhoneAccountChanged = 36;
        static final int TRANSACTION_onPostDialChar = 17;
        static final int TRANSACTION_onPostDialWait = 16;
        static final int TRANSACTION_onRemoteRttRequest = 35;
        static final int TRANSACTION_onRttInitiationFailure = 33;
        static final int TRANSACTION_onRttInitiationSuccess = 32;
        static final int TRANSACTION_onRttSessionRemotelyTerminated = 34;
        static final int TRANSACTION_putExtras = 27;
        static final int TRANSACTION_queryLocation = 41;
        static final int TRANSACTION_queryRemoteConnectionServices = 18;
        static final int TRANSACTION_removeCall = 15;
        static final int TRANSACTION_removeExtras = 28;
        static final int TRANSACTION_requestCallEndpointChange = 30;
        static final int TRANSACTION_resetConnectionTime = 38;
        static final int TRANSACTION_setActive = 3;
        static final int TRANSACTION_setAddress = 23;
        static final int TRANSACTION_setAudioRoute = 29;
        static final int TRANSACTION_setCallDirection = 40;
        static final int TRANSACTION_setCallerDisplayName = 24;
        static final int TRANSACTION_setConferenceMergeFailed = 13;
        static final int TRANSACTION_setConferenceState = 39;
        static final int TRANSACTION_setConferenceableConnections = 25;
        static final int TRANSACTION_setConnectionCapabilities = 10;
        static final int TRANSACTION_setConnectionProperties = 11;
        static final int TRANSACTION_setDialing = 5;
        static final int TRANSACTION_setDisconnected = 7;
        static final int TRANSACTION_setIsConferenced = 12;
        static final int TRANSACTION_setIsVoipAudioMode = 21;
        static final int TRANSACTION_setOnHold = 8;
        static final int TRANSACTION_setPulling = 6;
        static final int TRANSACTION_setRingbackRequested = 9;
        static final int TRANSACTION_setRinging = 4;
        static final int TRANSACTION_setStatusHints = 22;
        static final int TRANSACTION_setVideoProvider = 19;
        static final int TRANSACTION_setVideoState = 20;

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IConnectionServiceAdapter asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin != null && (iin instanceof IConnectionServiceAdapter)) {
                return (IConnectionServiceAdapter) iin;
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
                    return "handleCreateConnectionComplete";
                case 2:
                    return "handleCreateConferenceComplete";
                case 3:
                    return "setActive";
                case 4:
                    return "setRinging";
                case 5:
                    return "setDialing";
                case 6:
                    return "setPulling";
                case 7:
                    return "setDisconnected";
                case 8:
                    return "setOnHold";
                case 9:
                    return "setRingbackRequested";
                case 10:
                    return "setConnectionCapabilities";
                case 11:
                    return "setConnectionProperties";
                case 12:
                    return "setIsConferenced";
                case 13:
                    return "setConferenceMergeFailed";
                case 14:
                    return "addConferenceCall";
                case 15:
                    return "removeCall";
                case 16:
                    return "onPostDialWait";
                case 17:
                    return "onPostDialChar";
                case 18:
                    return "queryRemoteConnectionServices";
                case 19:
                    return "setVideoProvider";
                case 20:
                    return "setVideoState";
                case 21:
                    return "setIsVoipAudioMode";
                case 22:
                    return "setStatusHints";
                case 23:
                    return "setAddress";
                case 24:
                    return "setCallerDisplayName";
                case 25:
                    return "setConferenceableConnections";
                case 26:
                    return "addExistingConnection";
                case 27:
                    return "putExtras";
                case 28:
                    return "removeExtras";
                case 29:
                    return "setAudioRoute";
                case 30:
                    return "requestCallEndpointChange";
                case 31:
                    return "onConnectionEvent";
                case 32:
                    return "onRttInitiationSuccess";
                case 33:
                    return "onRttInitiationFailure";
                case 34:
                    return "onRttSessionRemotelyTerminated";
                case 35:
                    return "onRemoteRttRequest";
                case 36:
                    return "onPhoneAccountChanged";
                case 37:
                    return "onConnectionServiceFocusReleased";
                case 38:
                    return "resetConnectionTime";
                case 39:
                    return "setConferenceState";
                case 40:
                    return "setCallDirection";
                case 41:
                    return "queryLocation";
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
                            ConnectionRequest _arg1 = (ConnectionRequest) data.readTypedObject(ConnectionRequest.CREATOR);
                            ParcelableConnection _arg2 = (ParcelableConnection) data.readTypedObject(ParcelableConnection.CREATOR);
                            Session.Info _arg3 = (Session.Info) data.readTypedObject(Session.Info.CREATOR);
                            data.enforceNoDataAvail();
                            handleCreateConnectionComplete(_arg0, _arg1, _arg2, _arg3);
                            break;
                        case 2:
                            String _arg02 = data.readString();
                            ConnectionRequest _arg12 = (ConnectionRequest) data.readTypedObject(ConnectionRequest.CREATOR);
                            ParcelableConference _arg22 = (ParcelableConference) data.readTypedObject(ParcelableConference.CREATOR);
                            Session.Info _arg32 = (Session.Info) data.readTypedObject(Session.Info.CREATOR);
                            data.enforceNoDataAvail();
                            handleCreateConferenceComplete(_arg02, _arg12, _arg22, _arg32);
                            break;
                        case 3:
                            String _arg03 = data.readString();
                            Session.Info _arg13 = (Session.Info) data.readTypedObject(Session.Info.CREATOR);
                            data.enforceNoDataAvail();
                            setActive(_arg03, _arg13);
                            break;
                        case 4:
                            String _arg04 = data.readString();
                            Session.Info _arg14 = (Session.Info) data.readTypedObject(Session.Info.CREATOR);
                            data.enforceNoDataAvail();
                            setRinging(_arg04, _arg14);
                            break;
                        case 5:
                            String _arg05 = data.readString();
                            Session.Info _arg15 = (Session.Info) data.readTypedObject(Session.Info.CREATOR);
                            data.enforceNoDataAvail();
                            setDialing(_arg05, _arg15);
                            break;
                        case 6:
                            String _arg06 = data.readString();
                            Session.Info _arg16 = (Session.Info) data.readTypedObject(Session.Info.CREATOR);
                            data.enforceNoDataAvail();
                            setPulling(_arg06, _arg16);
                            break;
                        case 7:
                            String _arg07 = data.readString();
                            DisconnectCause _arg17 = (DisconnectCause) data.readTypedObject(DisconnectCause.CREATOR);
                            Session.Info _arg23 = (Session.Info) data.readTypedObject(Session.Info.CREATOR);
                            data.enforceNoDataAvail();
                            setDisconnected(_arg07, _arg17, _arg23);
                            break;
                        case 8:
                            String _arg08 = data.readString();
                            Session.Info _arg18 = (Session.Info) data.readTypedObject(Session.Info.CREATOR);
                            data.enforceNoDataAvail();
                            setOnHold(_arg08, _arg18);
                            break;
                        case 9:
                            String _arg09 = data.readString();
                            boolean _arg19 = data.readBoolean();
                            Session.Info _arg24 = (Session.Info) data.readTypedObject(Session.Info.CREATOR);
                            data.enforceNoDataAvail();
                            setRingbackRequested(_arg09, _arg19, _arg24);
                            break;
                        case 10:
                            String _arg010 = data.readString();
                            int _arg110 = data.readInt();
                            Session.Info _arg25 = (Session.Info) data.readTypedObject(Session.Info.CREATOR);
                            data.enforceNoDataAvail();
                            setConnectionCapabilities(_arg010, _arg110, _arg25);
                            break;
                        case 11:
                            String _arg011 = data.readString();
                            int _arg111 = data.readInt();
                            Session.Info _arg26 = (Session.Info) data.readTypedObject(Session.Info.CREATOR);
                            data.enforceNoDataAvail();
                            setConnectionProperties(_arg011, _arg111, _arg26);
                            break;
                        case 12:
                            String _arg012 = data.readString();
                            String _arg112 = data.readString();
                            Session.Info _arg27 = (Session.Info) data.readTypedObject(Session.Info.CREATOR);
                            data.enforceNoDataAvail();
                            setIsConferenced(_arg012, _arg112, _arg27);
                            break;
                        case 13:
                            String _arg013 = data.readString();
                            Session.Info _arg113 = (Session.Info) data.readTypedObject(Session.Info.CREATOR);
                            data.enforceNoDataAvail();
                            setConferenceMergeFailed(_arg013, _arg113);
                            break;
                        case 14:
                            String _arg014 = data.readString();
                            ParcelableConference _arg114 = (ParcelableConference) data.readTypedObject(ParcelableConference.CREATOR);
                            Session.Info _arg28 = (Session.Info) data.readTypedObject(Session.Info.CREATOR);
                            data.enforceNoDataAvail();
                            addConferenceCall(_arg014, _arg114, _arg28);
                            break;
                        case 15:
                            String _arg015 = data.readString();
                            Session.Info _arg115 = (Session.Info) data.readTypedObject(Session.Info.CREATOR);
                            data.enforceNoDataAvail();
                            removeCall(_arg015, _arg115);
                            break;
                        case 16:
                            String _arg016 = data.readString();
                            String _arg116 = data.readString();
                            Session.Info _arg29 = (Session.Info) data.readTypedObject(Session.Info.CREATOR);
                            data.enforceNoDataAvail();
                            onPostDialWait(_arg016, _arg116, _arg29);
                            break;
                        case 17:
                            String _arg017 = data.readString();
                            char _arg117 = (char) data.readInt();
                            Session.Info _arg210 = (Session.Info) data.readTypedObject(Session.Info.CREATOR);
                            data.enforceNoDataAvail();
                            onPostDialChar(_arg017, _arg117, _arg210);
                            break;
                        case 18:
                            RemoteServiceCallback _arg018 = RemoteServiceCallback.Stub.asInterface(data.readStrongBinder());
                            String _arg118 = data.readString();
                            Session.Info _arg211 = (Session.Info) data.readTypedObject(Session.Info.CREATOR);
                            data.enforceNoDataAvail();
                            queryRemoteConnectionServices(_arg018, _arg118, _arg211);
                            break;
                        case 19:
                            String _arg019 = data.readString();
                            IVideoProvider _arg119 = IVideoProvider.Stub.asInterface(data.readStrongBinder());
                            Session.Info _arg212 = (Session.Info) data.readTypedObject(Session.Info.CREATOR);
                            data.enforceNoDataAvail();
                            setVideoProvider(_arg019, _arg119, _arg212);
                            break;
                        case 20:
                            String _arg020 = data.readString();
                            int _arg120 = data.readInt();
                            Session.Info _arg213 = (Session.Info) data.readTypedObject(Session.Info.CREATOR);
                            data.enforceNoDataAvail();
                            setVideoState(_arg020, _arg120, _arg213);
                            break;
                        case 21:
                            String _arg021 = data.readString();
                            boolean _arg121 = data.readBoolean();
                            Session.Info _arg214 = (Session.Info) data.readTypedObject(Session.Info.CREATOR);
                            data.enforceNoDataAvail();
                            setIsVoipAudioMode(_arg021, _arg121, _arg214);
                            break;
                        case 22:
                            String _arg022 = data.readString();
                            StatusHints _arg122 = (StatusHints) data.readTypedObject(StatusHints.CREATOR);
                            Session.Info _arg215 = (Session.Info) data.readTypedObject(Session.Info.CREATOR);
                            data.enforceNoDataAvail();
                            setStatusHints(_arg022, _arg122, _arg215);
                            break;
                        case 23:
                            String _arg023 = data.readString();
                            Uri _arg123 = (Uri) data.readTypedObject(Uri.CREATOR);
                            int _arg216 = data.readInt();
                            Session.Info _arg33 = (Session.Info) data.readTypedObject(Session.Info.CREATOR);
                            data.enforceNoDataAvail();
                            setAddress(_arg023, _arg123, _arg216, _arg33);
                            break;
                        case 24:
                            String _arg024 = data.readString();
                            String _arg124 = data.readString();
                            int _arg217 = data.readInt();
                            Session.Info _arg34 = (Session.Info) data.readTypedObject(Session.Info.CREATOR);
                            data.enforceNoDataAvail();
                            setCallerDisplayName(_arg024, _arg124, _arg217, _arg34);
                            break;
                        case 25:
                            String _arg025 = data.readString();
                            List<String> _arg125 = data.createStringArrayList();
                            Session.Info _arg218 = (Session.Info) data.readTypedObject(Session.Info.CREATOR);
                            data.enforceNoDataAvail();
                            setConferenceableConnections(_arg025, _arg125, _arg218);
                            break;
                        case 26:
                            String _arg026 = data.readString();
                            ParcelableConnection _arg126 = (ParcelableConnection) data.readTypedObject(ParcelableConnection.CREATOR);
                            Session.Info _arg219 = (Session.Info) data.readTypedObject(Session.Info.CREATOR);
                            data.enforceNoDataAvail();
                            addExistingConnection(_arg026, _arg126, _arg219);
                            break;
                        case 27:
                            String _arg027 = data.readString();
                            Bundle _arg127 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            Session.Info _arg220 = (Session.Info) data.readTypedObject(Session.Info.CREATOR);
                            data.enforceNoDataAvail();
                            putExtras(_arg027, _arg127, _arg220);
                            break;
                        case 28:
                            String _arg028 = data.readString();
                            List<String> _arg128 = data.createStringArrayList();
                            Session.Info _arg221 = (Session.Info) data.readTypedObject(Session.Info.CREATOR);
                            data.enforceNoDataAvail();
                            removeExtras(_arg028, _arg128, _arg221);
                            break;
                        case 29:
                            String _arg029 = data.readString();
                            int _arg129 = data.readInt();
                            String _arg222 = data.readString();
                            Session.Info _arg35 = (Session.Info) data.readTypedObject(Session.Info.CREATOR);
                            data.enforceNoDataAvail();
                            setAudioRoute(_arg029, _arg129, _arg222, _arg35);
                            break;
                        case 30:
                            String _arg030 = data.readString();
                            CallEndpoint _arg130 = (CallEndpoint) data.readTypedObject(CallEndpoint.CREATOR);
                            ResultReceiver _arg223 = (ResultReceiver) data.readTypedObject(ResultReceiver.CREATOR);
                            Session.Info _arg36 = (Session.Info) data.readTypedObject(Session.Info.CREATOR);
                            data.enforceNoDataAvail();
                            requestCallEndpointChange(_arg030, _arg130, _arg223, _arg36);
                            break;
                        case 31:
                            String _arg031 = data.readString();
                            String _arg131 = data.readString();
                            Bundle _arg224 = (Bundle) data.readTypedObject(Bundle.CREATOR);
                            Session.Info _arg37 = (Session.Info) data.readTypedObject(Session.Info.CREATOR);
                            data.enforceNoDataAvail();
                            onConnectionEvent(_arg031, _arg131, _arg224, _arg37);
                            break;
                        case 32:
                            String _arg032 = data.readString();
                            Session.Info _arg132 = (Session.Info) data.readTypedObject(Session.Info.CREATOR);
                            data.enforceNoDataAvail();
                            onRttInitiationSuccess(_arg032, _arg132);
                            break;
                        case 33:
                            String _arg033 = data.readString();
                            int _arg133 = data.readInt();
                            Session.Info _arg225 = (Session.Info) data.readTypedObject(Session.Info.CREATOR);
                            data.enforceNoDataAvail();
                            onRttInitiationFailure(_arg033, _arg133, _arg225);
                            break;
                        case 34:
                            String _arg034 = data.readString();
                            Session.Info _arg134 = (Session.Info) data.readTypedObject(Session.Info.CREATOR);
                            data.enforceNoDataAvail();
                            onRttSessionRemotelyTerminated(_arg034, _arg134);
                            break;
                        case 35:
                            String _arg035 = data.readString();
                            Session.Info _arg135 = (Session.Info) data.readTypedObject(Session.Info.CREATOR);
                            data.enforceNoDataAvail();
                            onRemoteRttRequest(_arg035, _arg135);
                            break;
                        case 36:
                            String _arg036 = data.readString();
                            PhoneAccountHandle _arg136 = (PhoneAccountHandle) data.readTypedObject(PhoneAccountHandle.CREATOR);
                            Session.Info _arg226 = (Session.Info) data.readTypedObject(Session.Info.CREATOR);
                            data.enforceNoDataAvail();
                            onPhoneAccountChanged(_arg036, _arg136, _arg226);
                            break;
                        case 37:
                            Session.Info _arg037 = (Session.Info) data.readTypedObject(Session.Info.CREATOR);
                            data.enforceNoDataAvail();
                            onConnectionServiceFocusReleased(_arg037);
                            break;
                        case 38:
                            String _arg038 = data.readString();
                            Session.Info _arg137 = (Session.Info) data.readTypedObject(Session.Info.CREATOR);
                            data.enforceNoDataAvail();
                            resetConnectionTime(_arg038, _arg137);
                            break;
                        case 39:
                            String _arg039 = data.readString();
                            boolean _arg138 = data.readBoolean();
                            Session.Info _arg227 = (Session.Info) data.readTypedObject(Session.Info.CREATOR);
                            data.enforceNoDataAvail();
                            setConferenceState(_arg039, _arg138, _arg227);
                            break;
                        case 40:
                            String _arg040 = data.readString();
                            int _arg139 = data.readInt();
                            Session.Info _arg228 = (Session.Info) data.readTypedObject(Session.Info.CREATOR);
                            data.enforceNoDataAvail();
                            setCallDirection(_arg040, _arg139, _arg228);
                            break;
                        case 41:
                            String _arg041 = data.readString();
                            long _arg140 = data.readLong();
                            String _arg229 = data.readString();
                            ResultReceiver _arg38 = (ResultReceiver) data.readTypedObject(ResultReceiver.CREATOR);
                            Session.Info _arg4 = (Session.Info) data.readTypedObject(Session.Info.CREATOR);
                            data.enforceNoDataAvail();
                            queryLocation(_arg041, _arg140, _arg229, _arg38, _arg4);
                            break;
                        default:
                            return super.onTransact(code, data, reply, flags);
                    }
                    return true;
            }
        }

        /* loaded from: classes2.dex */
        private static class Proxy implements IConnectionServiceAdapter {
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

            @Override // com.android.internal.telecom.IConnectionServiceAdapter
            public void handleCreateConnectionComplete(String callId, ConnectionRequest request, ParcelableConnection connection, Session.Info sessionInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callId);
                    _data.writeTypedObject(request, 0);
                    _data.writeTypedObject(connection, 0);
                    _data.writeTypedObject(sessionInfo, 0);
                    this.mRemote.transact(1, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telecom.IConnectionServiceAdapter
            public void handleCreateConferenceComplete(String callId, ConnectionRequest request, ParcelableConference connection, Session.Info sessionInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callId);
                    _data.writeTypedObject(request, 0);
                    _data.writeTypedObject(connection, 0);
                    _data.writeTypedObject(sessionInfo, 0);
                    this.mRemote.transact(2, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telecom.IConnectionServiceAdapter
            public void setActive(String callId, Session.Info sessionInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callId);
                    _data.writeTypedObject(sessionInfo, 0);
                    this.mRemote.transact(3, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telecom.IConnectionServiceAdapter
            public void setRinging(String callId, Session.Info sessionInfo) throws RemoteException {
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

            @Override // com.android.internal.telecom.IConnectionServiceAdapter
            public void setDialing(String callId, Session.Info sessionInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callId);
                    _data.writeTypedObject(sessionInfo, 0);
                    this.mRemote.transact(5, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telecom.IConnectionServiceAdapter
            public void setPulling(String callId, Session.Info sessionInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callId);
                    _data.writeTypedObject(sessionInfo, 0);
                    this.mRemote.transact(6, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telecom.IConnectionServiceAdapter
            public void setDisconnected(String callId, DisconnectCause disconnectCause, Session.Info sessionInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callId);
                    _data.writeTypedObject(disconnectCause, 0);
                    _data.writeTypedObject(sessionInfo, 0);
                    this.mRemote.transact(7, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telecom.IConnectionServiceAdapter
            public void setOnHold(String callId, Session.Info sessionInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callId);
                    _data.writeTypedObject(sessionInfo, 0);
                    this.mRemote.transact(8, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telecom.IConnectionServiceAdapter
            public void setRingbackRequested(String callId, boolean ringing, Session.Info sessionInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callId);
                    _data.writeBoolean(ringing);
                    _data.writeTypedObject(sessionInfo, 0);
                    this.mRemote.transact(9, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telecom.IConnectionServiceAdapter
            public void setConnectionCapabilities(String callId, int connectionCapabilities, Session.Info sessionInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callId);
                    _data.writeInt(connectionCapabilities);
                    _data.writeTypedObject(sessionInfo, 0);
                    this.mRemote.transact(10, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telecom.IConnectionServiceAdapter
            public void setConnectionProperties(String callId, int connectionProperties, Session.Info sessionInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callId);
                    _data.writeInt(connectionProperties);
                    _data.writeTypedObject(sessionInfo, 0);
                    this.mRemote.transact(11, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telecom.IConnectionServiceAdapter
            public void setIsConferenced(String callId, String conferenceCallId, Session.Info sessionInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callId);
                    _data.writeString(conferenceCallId);
                    _data.writeTypedObject(sessionInfo, 0);
                    this.mRemote.transact(12, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telecom.IConnectionServiceAdapter
            public void setConferenceMergeFailed(String callId, Session.Info sessionInfo) throws RemoteException {
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

            @Override // com.android.internal.telecom.IConnectionServiceAdapter
            public void addConferenceCall(String callId, ParcelableConference conference, Session.Info sessionInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callId);
                    _data.writeTypedObject(conference, 0);
                    _data.writeTypedObject(sessionInfo, 0);
                    this.mRemote.transact(14, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telecom.IConnectionServiceAdapter
            public void removeCall(String callId, Session.Info sessionInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callId);
                    _data.writeTypedObject(sessionInfo, 0);
                    this.mRemote.transact(15, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telecom.IConnectionServiceAdapter
            public void onPostDialWait(String callId, String remaining, Session.Info sessionInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callId);
                    _data.writeString(remaining);
                    _data.writeTypedObject(sessionInfo, 0);
                    this.mRemote.transact(16, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telecom.IConnectionServiceAdapter
            public void onPostDialChar(String callId, char nextChar, Session.Info sessionInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callId);
                    _data.writeInt(nextChar);
                    _data.writeTypedObject(sessionInfo, 0);
                    this.mRemote.transact(17, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telecom.IConnectionServiceAdapter
            public void queryRemoteConnectionServices(RemoteServiceCallback callback, String callingPackage, Session.Info sessionInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeStrongInterface(callback);
                    _data.writeString(callingPackage);
                    _data.writeTypedObject(sessionInfo, 0);
                    this.mRemote.transact(18, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telecom.IConnectionServiceAdapter
            public void setVideoProvider(String callId, IVideoProvider videoProvider, Session.Info sessionInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callId);
                    _data.writeStrongInterface(videoProvider);
                    _data.writeTypedObject(sessionInfo, 0);
                    this.mRemote.transact(19, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telecom.IConnectionServiceAdapter
            public void setVideoState(String callId, int videoState, Session.Info sessionInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callId);
                    _data.writeInt(videoState);
                    _data.writeTypedObject(sessionInfo, 0);
                    this.mRemote.transact(20, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telecom.IConnectionServiceAdapter
            public void setIsVoipAudioMode(String callId, boolean isVoip, Session.Info sessionInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callId);
                    _data.writeBoolean(isVoip);
                    _data.writeTypedObject(sessionInfo, 0);
                    this.mRemote.transact(21, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telecom.IConnectionServiceAdapter
            public void setStatusHints(String callId, StatusHints statusHints, Session.Info sessionInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callId);
                    _data.writeTypedObject(statusHints, 0);
                    _data.writeTypedObject(sessionInfo, 0);
                    this.mRemote.transact(22, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telecom.IConnectionServiceAdapter
            public void setAddress(String callId, Uri address, int presentation, Session.Info sessionInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callId);
                    _data.writeTypedObject(address, 0);
                    _data.writeInt(presentation);
                    _data.writeTypedObject(sessionInfo, 0);
                    this.mRemote.transact(23, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telecom.IConnectionServiceAdapter
            public void setCallerDisplayName(String callId, String callerDisplayName, int presentation, Session.Info sessionInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callId);
                    _data.writeString(callerDisplayName);
                    _data.writeInt(presentation);
                    _data.writeTypedObject(sessionInfo, 0);
                    this.mRemote.transact(24, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telecom.IConnectionServiceAdapter
            public void setConferenceableConnections(String callId, List<String> conferenceableCallIds, Session.Info sessionInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callId);
                    _data.writeStringList(conferenceableCallIds);
                    _data.writeTypedObject(sessionInfo, 0);
                    this.mRemote.transact(25, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telecom.IConnectionServiceAdapter
            public void addExistingConnection(String callId, ParcelableConnection connection, Session.Info sessionInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callId);
                    _data.writeTypedObject(connection, 0);
                    _data.writeTypedObject(sessionInfo, 0);
                    this.mRemote.transact(26, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telecom.IConnectionServiceAdapter
            public void putExtras(String callId, Bundle extras, Session.Info sessionInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callId);
                    _data.writeTypedObject(extras, 0);
                    _data.writeTypedObject(sessionInfo, 0);
                    this.mRemote.transact(27, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telecom.IConnectionServiceAdapter
            public void removeExtras(String callId, List<String> keys, Session.Info sessionInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callId);
                    _data.writeStringList(keys);
                    _data.writeTypedObject(sessionInfo, 0);
                    this.mRemote.transact(28, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telecom.IConnectionServiceAdapter
            public void setAudioRoute(String callId, int audioRoute, String bluetoothAddress, Session.Info sessionInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callId);
                    _data.writeInt(audioRoute);
                    _data.writeString(bluetoothAddress);
                    _data.writeTypedObject(sessionInfo, 0);
                    this.mRemote.transact(29, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telecom.IConnectionServiceAdapter
            public void requestCallEndpointChange(String callId, CallEndpoint endpoint, ResultReceiver callback, Session.Info sessionInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callId);
                    _data.writeTypedObject(endpoint, 0);
                    _data.writeTypedObject(callback, 0);
                    _data.writeTypedObject(sessionInfo, 0);
                    this.mRemote.transact(30, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telecom.IConnectionServiceAdapter
            public void onConnectionEvent(String callId, String event, Bundle extras, Session.Info sessionInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callId);
                    _data.writeString(event);
                    _data.writeTypedObject(extras, 0);
                    _data.writeTypedObject(sessionInfo, 0);
                    this.mRemote.transact(31, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telecom.IConnectionServiceAdapter
            public void onRttInitiationSuccess(String callId, Session.Info sessionInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callId);
                    _data.writeTypedObject(sessionInfo, 0);
                    this.mRemote.transact(32, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telecom.IConnectionServiceAdapter
            public void onRttInitiationFailure(String callId, int reason, Session.Info sessionInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callId);
                    _data.writeInt(reason);
                    _data.writeTypedObject(sessionInfo, 0);
                    this.mRemote.transact(33, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telecom.IConnectionServiceAdapter
            public void onRttSessionRemotelyTerminated(String callId, Session.Info sessionInfo) throws RemoteException {
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

            @Override // com.android.internal.telecom.IConnectionServiceAdapter
            public void onRemoteRttRequest(String callId, Session.Info sessionInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callId);
                    _data.writeTypedObject(sessionInfo, 0);
                    this.mRemote.transact(35, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telecom.IConnectionServiceAdapter
            public void onPhoneAccountChanged(String callId, PhoneAccountHandle pHandle, Session.Info sessionInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callId);
                    _data.writeTypedObject(pHandle, 0);
                    _data.writeTypedObject(sessionInfo, 0);
                    this.mRemote.transact(36, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telecom.IConnectionServiceAdapter
            public void onConnectionServiceFocusReleased(Session.Info sessionInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeTypedObject(sessionInfo, 0);
                    this.mRemote.transact(37, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telecom.IConnectionServiceAdapter
            public void resetConnectionTime(String callIdi, Session.Info sessionInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callIdi);
                    _data.writeTypedObject(sessionInfo, 0);
                    this.mRemote.transact(38, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telecom.IConnectionServiceAdapter
            public void setConferenceState(String callId, boolean isConference, Session.Info sessionInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callId);
                    _data.writeBoolean(isConference);
                    _data.writeTypedObject(sessionInfo, 0);
                    this.mRemote.transact(39, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telecom.IConnectionServiceAdapter
            public void setCallDirection(String callId, int direction, Session.Info sessionInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callId);
                    _data.writeInt(direction);
                    _data.writeTypedObject(sessionInfo, 0);
                    this.mRemote.transact(40, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }

            @Override // com.android.internal.telecom.IConnectionServiceAdapter
            public void queryLocation(String callId, long timeoutMillis, String provider, ResultReceiver callback, Session.Info sessionInfo) throws RemoteException {
                Parcel _data = Parcel.obtain(asBinder());
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(callId);
                    _data.writeLong(timeoutMillis);
                    _data.writeString(provider);
                    _data.writeTypedObject(callback, 0);
                    _data.writeTypedObject(sessionInfo, 0);
                    this.mRemote.transact(41, _data, null, 1);
                } finally {
                    _data.recycle();
                }
            }
        }

        @Override // android.p008os.Binder
        public int getMaxTransactionId() {
            return 40;
        }
    }
}
