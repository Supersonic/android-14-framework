package android.telecom;

import android.net.Uri;
import android.p008os.Bundle;
import android.p008os.Handler;
import android.p008os.Message;
import android.p008os.RemoteException;
import android.p008os.ResultReceiver;
import android.telecom.Logging.Session;
import com.android.internal.p028os.SomeArgs;
import com.android.internal.telecom.IConnectionServiceAdapter;
import com.android.internal.telecom.IVideoProvider;
import com.android.internal.telecom.RemoteServiceCallback;
import java.util.List;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes3.dex */
public final class ConnectionServiceAdapterServant {
    private static final int MSG_ADD_CONFERENCE_CALL = 10;
    private static final int MSG_ADD_EXISTING_CONNECTION = 21;
    private static final int MSG_CONNECTION_SERVICE_FOCUS_RELEASED = 35;
    private static final int MSG_HANDLE_CREATE_CONFERENCE_COMPLETE = 37;
    private static final int MSG_HANDLE_CREATE_CONNECTION_COMPLETE = 1;
    private static final int MSG_ON_CONNECTION_EVENT = 26;
    private static final int MSG_ON_POST_DIAL_CHAR = 22;
    private static final int MSG_ON_POST_DIAL_WAIT = 12;
    private static final int MSG_ON_RTT_INITIATION_FAILURE = 31;
    private static final int MSG_ON_RTT_INITIATION_SUCCESS = 30;
    private static final int MSG_ON_RTT_REMOTELY_TERMINATED = 32;
    private static final int MSG_ON_RTT_UPGRADE_REQUEST = 33;
    private static final int MSG_PUT_EXTRAS = 24;
    private static final int MSG_QUERY_LOCATION = 39;
    private static final int MSG_QUERY_REMOTE_CALL_SERVICES = 13;
    private static final int MSG_REMOVE_CALL = 11;
    private static final int MSG_REMOVE_EXTRAS = 25;
    private static final int MSG_SET_ACTIVE = 2;
    private static final int MSG_SET_ADDRESS = 18;
    private static final int MSG_SET_AUDIO_ROUTE = 29;
    private static final int MSG_SET_CALLER_DISPLAY_NAME = 19;
    private static final int MSG_SET_CALL_DIRECTION = 38;
    private static final int MSG_SET_CONFERENCEABLE_CONNECTIONS = 20;
    private static final int MSG_SET_CONFERENCE_MERGE_FAILED = 23;
    private static final int MSG_SET_CONFERENCE_STATE = 36;
    private static final int MSG_SET_CONNECTION_CAPABILITIES = 8;
    private static final int MSG_SET_CONNECTION_PROPERTIES = 27;
    private static final int MSG_SET_DIALING = 4;
    private static final int MSG_SET_DISCONNECTED = 5;
    private static final int MSG_SET_IS_CONFERENCED = 9;
    private static final int MSG_SET_IS_VOIP_AUDIO_MODE = 16;
    private static final int MSG_SET_ON_HOLD = 6;
    private static final int MSG_SET_PHONE_ACCOUNT_CHANGED = 34;
    private static final int MSG_SET_PULLING = 28;
    private static final int MSG_SET_RINGBACK_REQUESTED = 7;
    private static final int MSG_SET_RINGING = 3;
    private static final int MSG_SET_STATUS_HINTS = 17;
    private static final int MSG_SET_VIDEO_CALL_PROVIDER = 15;
    private static final int MSG_SET_VIDEO_STATE = 14;
    private final IConnectionServiceAdapter mDelegate;
    private final Handler mHandler = new Handler() { // from class: android.telecom.ConnectionServiceAdapterServant.1
        @Override // android.p008os.Handler
        public void handleMessage(Message msg) {
            try {
                internalHandleMessage(msg);
            } catch (RemoteException e) {
            }
        }

        private void internalHandleMessage(Message msg) throws RemoteException {
            SomeArgs args;
            switch (msg.what) {
                case 1:
                    args = (SomeArgs) msg.obj;
                    try {
                        ConnectionServiceAdapterServant.this.mDelegate.handleCreateConnectionComplete((String) args.arg1, (ConnectionRequest) args.arg2, (ParcelableConnection) args.arg3, null);
                        return;
                    } finally {
                    }
                case 2:
                    ConnectionServiceAdapterServant.this.mDelegate.setActive((String) msg.obj, null);
                    return;
                case 3:
                    ConnectionServiceAdapterServant.this.mDelegate.setRinging((String) msg.obj, null);
                    return;
                case 4:
                    ConnectionServiceAdapterServant.this.mDelegate.setDialing((String) msg.obj, null);
                    return;
                case 5:
                    args = (SomeArgs) msg.obj;
                    try {
                        ConnectionServiceAdapterServant.this.mDelegate.setDisconnected((String) args.arg1, (DisconnectCause) args.arg2, null);
                        return;
                    } finally {
                    }
                case 6:
                    ConnectionServiceAdapterServant.this.mDelegate.setOnHold((String) msg.obj, null);
                    return;
                case 7:
                    ConnectionServiceAdapterServant.this.mDelegate.setRingbackRequested((String) msg.obj, msg.arg1 == 1, null);
                    return;
                case 8:
                    ConnectionServiceAdapterServant.this.mDelegate.setConnectionCapabilities((String) msg.obj, msg.arg1, null);
                    return;
                case 9:
                    args = (SomeArgs) msg.obj;
                    try {
                        ConnectionServiceAdapterServant.this.mDelegate.setIsConferenced((String) args.arg1, (String) args.arg2, null);
                        return;
                    } finally {
                    }
                case 10:
                    args = (SomeArgs) msg.obj;
                    try {
                        ConnectionServiceAdapterServant.this.mDelegate.addConferenceCall((String) args.arg1, (ParcelableConference) args.arg2, null);
                        return;
                    } finally {
                    }
                case 11:
                    ConnectionServiceAdapterServant.this.mDelegate.removeCall((String) msg.obj, null);
                    return;
                case 12:
                    args = (SomeArgs) msg.obj;
                    try {
                        ConnectionServiceAdapterServant.this.mDelegate.onPostDialWait((String) args.arg1, (String) args.arg2, null);
                        return;
                    } finally {
                    }
                case 13:
                    args = (SomeArgs) msg.obj;
                    try {
                        ConnectionServiceAdapterServant.this.mDelegate.queryRemoteConnectionServices((RemoteServiceCallback) args.arg1, (String) args.arg2, null);
                        return;
                    } finally {
                    }
                case 14:
                    ConnectionServiceAdapterServant.this.mDelegate.setVideoState((String) msg.obj, msg.arg1, null);
                    return;
                case 15:
                    args = (SomeArgs) msg.obj;
                    try {
                        ConnectionServiceAdapterServant.this.mDelegate.setVideoProvider((String) args.arg1, (IVideoProvider) args.arg2, null);
                        return;
                    } finally {
                    }
                case 16:
                    ConnectionServiceAdapterServant.this.mDelegate.setIsVoipAudioMode((String) msg.obj, msg.arg1 == 1, null);
                    return;
                case 17:
                    args = (SomeArgs) msg.obj;
                    try {
                        ConnectionServiceAdapterServant.this.mDelegate.setStatusHints((String) args.arg1, (StatusHints) args.arg2, null);
                        return;
                    } finally {
                    }
                case 18:
                    args = (SomeArgs) msg.obj;
                    try {
                        ConnectionServiceAdapterServant.this.mDelegate.setAddress((String) args.arg1, (Uri) args.arg2, args.argi1, null);
                        return;
                    } finally {
                    }
                case 19:
                    args = (SomeArgs) msg.obj;
                    try {
                        ConnectionServiceAdapterServant.this.mDelegate.setCallerDisplayName((String) args.arg1, (String) args.arg2, args.argi1, null);
                        return;
                    } finally {
                    }
                case 20:
                    args = (SomeArgs) msg.obj;
                    try {
                        ConnectionServiceAdapterServant.this.mDelegate.setConferenceableConnections((String) args.arg1, (List) args.arg2, null);
                        return;
                    } finally {
                    }
                case 21:
                    args = (SomeArgs) msg.obj;
                    try {
                        ConnectionServiceAdapterServant.this.mDelegate.addExistingConnection((String) args.arg1, (ParcelableConnection) args.arg2, null);
                        return;
                    } finally {
                    }
                case 22:
                    args = (SomeArgs) msg.obj;
                    try {
                        ConnectionServiceAdapterServant.this.mDelegate.onPostDialChar((String) args.arg1, (char) args.argi1, null);
                        return;
                    } finally {
                    }
                case 23:
                    args = (SomeArgs) msg.obj;
                    try {
                        ConnectionServiceAdapterServant.this.mDelegate.setConferenceMergeFailed((String) args.arg1, null);
                        return;
                    } finally {
                    }
                case 24:
                    args = (SomeArgs) msg.obj;
                    try {
                        ConnectionServiceAdapterServant.this.mDelegate.putExtras((String) args.arg1, (Bundle) args.arg2, null);
                        return;
                    } finally {
                    }
                case 25:
                    args = (SomeArgs) msg.obj;
                    try {
                        ConnectionServiceAdapterServant.this.mDelegate.removeExtras((String) args.arg1, (List) args.arg2, null);
                        return;
                    } finally {
                    }
                case 26:
                    args = (SomeArgs) msg.obj;
                    try {
                        ConnectionServiceAdapterServant.this.mDelegate.onConnectionEvent((String) args.arg1, (String) args.arg2, (Bundle) args.arg3, null);
                        return;
                    } finally {
                    }
                case 27:
                    ConnectionServiceAdapterServant.this.mDelegate.setConnectionProperties((String) msg.obj, msg.arg1, null);
                    return;
                case 28:
                    ConnectionServiceAdapterServant.this.mDelegate.setPulling((String) msg.obj, null);
                    return;
                case 29:
                    args = (SomeArgs) msg.obj;
                    try {
                        ConnectionServiceAdapterServant.this.mDelegate.setAudioRoute((String) args.arg1, args.argi1, (String) args.arg2, (Session.Info) args.arg3);
                        return;
                    } finally {
                    }
                case 30:
                    ConnectionServiceAdapterServant.this.mDelegate.onRttInitiationSuccess((String) msg.obj, null);
                    return;
                case 31:
                    ConnectionServiceAdapterServant.this.mDelegate.onRttInitiationFailure((String) msg.obj, msg.arg1, null);
                    return;
                case 32:
                    ConnectionServiceAdapterServant.this.mDelegate.onRttSessionRemotelyTerminated((String) msg.obj, null);
                    return;
                case 33:
                    ConnectionServiceAdapterServant.this.mDelegate.onRemoteRttRequest((String) msg.obj, null);
                    return;
                case 34:
                    args = (SomeArgs) msg.obj;
                    try {
                        ConnectionServiceAdapterServant.this.mDelegate.onPhoneAccountChanged((String) args.arg1, (PhoneAccountHandle) args.arg2, null);
                        return;
                    } finally {
                    }
                case 35:
                    ConnectionServiceAdapterServant.this.mDelegate.onConnectionServiceFocusReleased(null);
                    return;
                case 36:
                    args = (SomeArgs) msg.obj;
                    try {
                        ConnectionServiceAdapterServant.this.mDelegate.setConferenceState((String) args.arg1, ((Boolean) args.arg2).booleanValue(), (Session.Info) args.arg3);
                        return;
                    } finally {
                    }
                case 37:
                    args = (SomeArgs) msg.obj;
                    try {
                        ConnectionServiceAdapterServant.this.mDelegate.handleCreateConferenceComplete((String) args.arg1, (ConnectionRequest) args.arg2, (ParcelableConference) args.arg3, null);
                        return;
                    } finally {
                    }
                case 38:
                    args = (SomeArgs) msg.obj;
                    try {
                        ConnectionServiceAdapterServant.this.mDelegate.setCallDirection((String) args.arg1, args.argi1, (Session.Info) args.arg2);
                        return;
                    } finally {
                    }
                case 39:
                    args = (SomeArgs) msg.obj;
                    try {
                        ConnectionServiceAdapterServant.this.mDelegate.queryLocation((String) args.arg1, ((Long) args.arg2).longValue(), (String) args.arg3, (ResultReceiver) args.arg4, (Session.Info) args.arg5);
                        return;
                    } finally {
                    }
                default:
                    return;
            }
        }
    };
    private final IConnectionServiceAdapter mStub = new IConnectionServiceAdapter.Stub() { // from class: android.telecom.ConnectionServiceAdapterServant.2
        @Override // com.android.internal.telecom.IConnectionServiceAdapter
        public void handleCreateConnectionComplete(String id, ConnectionRequest request, ParcelableConnection connection, Session.Info sessionInfo) {
            SomeArgs args = SomeArgs.obtain();
            args.arg1 = id;
            args.arg2 = request;
            args.arg3 = connection;
            ConnectionServiceAdapterServant.this.mHandler.obtainMessage(1, args).sendToTarget();
        }

        @Override // com.android.internal.telecom.IConnectionServiceAdapter
        public void handleCreateConferenceComplete(String id, ConnectionRequest request, ParcelableConference conference, Session.Info sessionInfo) {
            SomeArgs args = SomeArgs.obtain();
            args.arg1 = id;
            args.arg2 = request;
            args.arg3 = conference;
            ConnectionServiceAdapterServant.this.mHandler.obtainMessage(37, args).sendToTarget();
        }

        @Override // com.android.internal.telecom.IConnectionServiceAdapter
        public void setActive(String connectionId, Session.Info sessionInfo) {
            ConnectionServiceAdapterServant.this.mHandler.obtainMessage(2, connectionId).sendToTarget();
        }

        @Override // com.android.internal.telecom.IConnectionServiceAdapter
        public void setRinging(String connectionId, Session.Info sessionInfo) {
            ConnectionServiceAdapterServant.this.mHandler.obtainMessage(3, connectionId).sendToTarget();
        }

        @Override // com.android.internal.telecom.IConnectionServiceAdapter
        public void setDialing(String connectionId, Session.Info sessionInfo) {
            ConnectionServiceAdapterServant.this.mHandler.obtainMessage(4, connectionId).sendToTarget();
        }

        @Override // com.android.internal.telecom.IConnectionServiceAdapter
        public void setPulling(String connectionId, Session.Info sessionInfo) {
            ConnectionServiceAdapterServant.this.mHandler.obtainMessage(28, connectionId).sendToTarget();
        }

        @Override // com.android.internal.telecom.IConnectionServiceAdapter
        public void setDisconnected(String connectionId, DisconnectCause disconnectCause, Session.Info sessionInfo) {
            SomeArgs args = SomeArgs.obtain();
            args.arg1 = connectionId;
            args.arg2 = disconnectCause;
            ConnectionServiceAdapterServant.this.mHandler.obtainMessage(5, args).sendToTarget();
        }

        @Override // com.android.internal.telecom.IConnectionServiceAdapter
        public void setOnHold(String connectionId, Session.Info sessionInfo) {
            ConnectionServiceAdapterServant.this.mHandler.obtainMessage(6, connectionId).sendToTarget();
        }

        @Override // com.android.internal.telecom.IConnectionServiceAdapter
        public void setRingbackRequested(String connectionId, boolean ringback, Session.Info sessionInfo) {
            ConnectionServiceAdapterServant.this.mHandler.obtainMessage(7, ringback ? 1 : 0, 0, connectionId).sendToTarget();
        }

        @Override // com.android.internal.telecom.IConnectionServiceAdapter
        public void setConnectionCapabilities(String connectionId, int connectionCapabilities, Session.Info sessionInfo) {
            ConnectionServiceAdapterServant.this.mHandler.obtainMessage(8, connectionCapabilities, 0, connectionId).sendToTarget();
        }

        @Override // com.android.internal.telecom.IConnectionServiceAdapter
        public void setConnectionProperties(String connectionId, int connectionProperties, Session.Info sessionInfo) {
            ConnectionServiceAdapterServant.this.mHandler.obtainMessage(27, connectionProperties, 0, connectionId).sendToTarget();
        }

        @Override // com.android.internal.telecom.IConnectionServiceAdapter
        public void setConferenceMergeFailed(String callId, Session.Info sessionInfo) {
            SomeArgs args = SomeArgs.obtain();
            args.arg1 = callId;
            ConnectionServiceAdapterServant.this.mHandler.obtainMessage(23, args).sendToTarget();
        }

        @Override // com.android.internal.telecom.IConnectionServiceAdapter
        public void setIsConferenced(String callId, String conferenceCallId, Session.Info sessionInfo) {
            SomeArgs args = SomeArgs.obtain();
            args.arg1 = callId;
            args.arg2 = conferenceCallId;
            ConnectionServiceAdapterServant.this.mHandler.obtainMessage(9, args).sendToTarget();
        }

        @Override // com.android.internal.telecom.IConnectionServiceAdapter
        public void addConferenceCall(String callId, ParcelableConference parcelableConference, Session.Info sessionInfo) {
            SomeArgs args = SomeArgs.obtain();
            args.arg1 = callId;
            args.arg2 = parcelableConference;
            ConnectionServiceAdapterServant.this.mHandler.obtainMessage(10, args).sendToTarget();
        }

        @Override // com.android.internal.telecom.IConnectionServiceAdapter
        public void removeCall(String connectionId, Session.Info sessionInfo) {
            ConnectionServiceAdapterServant.this.mHandler.obtainMessage(11, connectionId).sendToTarget();
        }

        @Override // com.android.internal.telecom.IConnectionServiceAdapter
        public void onPostDialWait(String connectionId, String remainingDigits, Session.Info sessionInfo) {
            SomeArgs args = SomeArgs.obtain();
            args.arg1 = connectionId;
            args.arg2 = remainingDigits;
            ConnectionServiceAdapterServant.this.mHandler.obtainMessage(12, args).sendToTarget();
        }

        @Override // com.android.internal.telecom.IConnectionServiceAdapter
        public void onPostDialChar(String connectionId, char nextChar, Session.Info sessionInfo) {
            SomeArgs args = SomeArgs.obtain();
            args.arg1 = connectionId;
            args.argi1 = nextChar;
            ConnectionServiceAdapterServant.this.mHandler.obtainMessage(22, args).sendToTarget();
        }

        @Override // com.android.internal.telecom.IConnectionServiceAdapter
        public void queryRemoteConnectionServices(RemoteServiceCallback callback, String callingPackage, Session.Info sessionInfo) {
            SomeArgs args = SomeArgs.obtain();
            args.arg1 = callback;
            args.arg2 = callingPackage;
            ConnectionServiceAdapterServant.this.mHandler.obtainMessage(13, args).sendToTarget();
        }

        @Override // com.android.internal.telecom.IConnectionServiceAdapter
        public void setVideoState(String connectionId, int videoState, Session.Info sessionInfo) {
            ConnectionServiceAdapterServant.this.mHandler.obtainMessage(14, videoState, 0, connectionId).sendToTarget();
        }

        @Override // com.android.internal.telecom.IConnectionServiceAdapter
        public void setVideoProvider(String connectionId, IVideoProvider videoProvider, Session.Info sessionInfo) {
            SomeArgs args = SomeArgs.obtain();
            args.arg1 = connectionId;
            args.arg2 = videoProvider;
            ConnectionServiceAdapterServant.this.mHandler.obtainMessage(15, args).sendToTarget();
        }

        @Override // com.android.internal.telecom.IConnectionServiceAdapter
        public final void setIsVoipAudioMode(String connectionId, boolean isVoip, Session.Info sessionInfo) {
            ConnectionServiceAdapterServant.this.mHandler.obtainMessage(16, isVoip ? 1 : 0, 0, connectionId).sendToTarget();
        }

        @Override // com.android.internal.telecom.IConnectionServiceAdapter
        public final void setStatusHints(String connectionId, StatusHints statusHints, Session.Info sessionInfo) {
            SomeArgs args = SomeArgs.obtain();
            args.arg1 = connectionId;
            args.arg2 = statusHints;
            ConnectionServiceAdapterServant.this.mHandler.obtainMessage(17, args).sendToTarget();
        }

        @Override // com.android.internal.telecom.IConnectionServiceAdapter
        public final void setAddress(String connectionId, Uri address, int presentation, Session.Info sessionInfo) {
            SomeArgs args = SomeArgs.obtain();
            args.arg1 = connectionId;
            args.arg2 = address;
            args.argi1 = presentation;
            ConnectionServiceAdapterServant.this.mHandler.obtainMessage(18, args).sendToTarget();
        }

        @Override // com.android.internal.telecom.IConnectionServiceAdapter
        public final void setCallerDisplayName(String connectionId, String callerDisplayName, int presentation, Session.Info sessionInfo) {
            SomeArgs args = SomeArgs.obtain();
            args.arg1 = connectionId;
            args.arg2 = callerDisplayName;
            args.argi1 = presentation;
            ConnectionServiceAdapterServant.this.mHandler.obtainMessage(19, args).sendToTarget();
        }

        @Override // com.android.internal.telecom.IConnectionServiceAdapter
        public final void setConferenceableConnections(String connectionId, List<String> conferenceableConnectionIds, Session.Info sessionInfo) {
            SomeArgs args = SomeArgs.obtain();
            args.arg1 = connectionId;
            args.arg2 = conferenceableConnectionIds;
            ConnectionServiceAdapterServant.this.mHandler.obtainMessage(20, args).sendToTarget();
        }

        @Override // com.android.internal.telecom.IConnectionServiceAdapter
        public final void addExistingConnection(String connectionId, ParcelableConnection connection, Session.Info sessionInfo) {
            SomeArgs args = SomeArgs.obtain();
            args.arg1 = connectionId;
            args.arg2 = connection;
            ConnectionServiceAdapterServant.this.mHandler.obtainMessage(21, args).sendToTarget();
        }

        @Override // com.android.internal.telecom.IConnectionServiceAdapter
        public final void putExtras(String connectionId, Bundle extras, Session.Info sessionInfo) {
            SomeArgs args = SomeArgs.obtain();
            args.arg1 = connectionId;
            args.arg2 = extras;
            ConnectionServiceAdapterServant.this.mHandler.obtainMessage(24, args).sendToTarget();
        }

        @Override // com.android.internal.telecom.IConnectionServiceAdapter
        public final void removeExtras(String connectionId, List<String> keys, Session.Info sessionInfo) {
            SomeArgs args = SomeArgs.obtain();
            args.arg1 = connectionId;
            args.arg2 = keys;
            ConnectionServiceAdapterServant.this.mHandler.obtainMessage(25, args).sendToTarget();
        }

        @Override // com.android.internal.telecom.IConnectionServiceAdapter
        public final void setAudioRoute(String connectionId, int audioRoute, String bluetoothAddress, Session.Info sessionInfo) {
            SomeArgs args = SomeArgs.obtain();
            args.arg1 = connectionId;
            args.argi1 = audioRoute;
            args.arg2 = bluetoothAddress;
            args.arg3 = sessionInfo;
            ConnectionServiceAdapterServant.this.mHandler.obtainMessage(29, args).sendToTarget();
        }

        @Override // com.android.internal.telecom.IConnectionServiceAdapter
        public final void onConnectionEvent(String connectionId, String event, Bundle extras, Session.Info sessionInfo) {
            SomeArgs args = SomeArgs.obtain();
            args.arg1 = connectionId;
            args.arg2 = event;
            args.arg3 = extras;
            ConnectionServiceAdapterServant.this.mHandler.obtainMessage(26, args).sendToTarget();
        }

        @Override // com.android.internal.telecom.IConnectionServiceAdapter
        public void onRttInitiationSuccess(String connectionId, Session.Info sessionInfo) throws RemoteException {
            ConnectionServiceAdapterServant.this.mHandler.obtainMessage(30, connectionId).sendToTarget();
        }

        @Override // com.android.internal.telecom.IConnectionServiceAdapter
        public void onRttInitiationFailure(String connectionId, int reason, Session.Info sessionInfo) throws RemoteException {
            ConnectionServiceAdapterServant.this.mHandler.obtainMessage(31, reason, 0, connectionId).sendToTarget();
        }

        @Override // com.android.internal.telecom.IConnectionServiceAdapter
        public void onRttSessionRemotelyTerminated(String connectionId, Session.Info sessionInfo) throws RemoteException {
            ConnectionServiceAdapterServant.this.mHandler.obtainMessage(32, connectionId).sendToTarget();
        }

        @Override // com.android.internal.telecom.IConnectionServiceAdapter
        public void onRemoteRttRequest(String connectionId, Session.Info sessionInfo) throws RemoteException {
            ConnectionServiceAdapterServant.this.mHandler.obtainMessage(33, connectionId).sendToTarget();
        }

        @Override // com.android.internal.telecom.IConnectionServiceAdapter
        public void onPhoneAccountChanged(String callId, PhoneAccountHandle pHandle, Session.Info sessionInfo) {
            SomeArgs args = SomeArgs.obtain();
            args.arg1 = callId;
            args.arg2 = pHandle;
            ConnectionServiceAdapterServant.this.mHandler.obtainMessage(34, args).sendToTarget();
        }

        @Override // com.android.internal.telecom.IConnectionServiceAdapter
        public void onConnectionServiceFocusReleased(Session.Info sessionInfo) {
            ConnectionServiceAdapterServant.this.mHandler.obtainMessage(35).sendToTarget();
        }

        @Override // com.android.internal.telecom.IConnectionServiceAdapter
        public void resetConnectionTime(String callId, Session.Info sessionInfo) {
        }

        @Override // com.android.internal.telecom.IConnectionServiceAdapter
        public void setConferenceState(String callId, boolean isConference, Session.Info sessionInfo) {
            SomeArgs args = SomeArgs.obtain();
            args.arg1 = callId;
            args.arg2 = Boolean.valueOf(isConference);
            args.arg3 = sessionInfo;
            ConnectionServiceAdapterServant.this.mHandler.obtainMessage(36, args).sendToTarget();
        }

        @Override // com.android.internal.telecom.IConnectionServiceAdapter
        public void setCallDirection(String callId, int direction, Session.Info sessionInfo) {
            SomeArgs args = SomeArgs.obtain();
            args.arg1 = callId;
            args.argi1 = direction;
            args.arg2 = sessionInfo;
            ConnectionServiceAdapterServant.this.mHandler.obtainMessage(38, args).sendToTarget();
        }

        @Override // com.android.internal.telecom.IConnectionServiceAdapter
        public void requestCallEndpointChange(String callId, CallEndpoint endpoint, ResultReceiver callback, Session.Info sessionInfo) {
        }

        @Override // com.android.internal.telecom.IConnectionServiceAdapter
        public void queryLocation(String callId, long timeoutMillis, String provider, ResultReceiver callback, Session.Info sessionInfo) {
            SomeArgs args = SomeArgs.obtain();
            args.arg1 = callId;
            args.arg2 = Long.valueOf(timeoutMillis);
            args.arg3 = provider;
            args.arg4 = callback;
            args.arg5 = sessionInfo;
            ConnectionServiceAdapterServant.this.mHandler.obtainMessage(39, args).sendToTarget();
        }
    };

    public ConnectionServiceAdapterServant(IConnectionServiceAdapter delegate) {
        this.mDelegate = delegate;
    }

    public IConnectionServiceAdapter getStub() {
        return this.mStub;
    }
}
