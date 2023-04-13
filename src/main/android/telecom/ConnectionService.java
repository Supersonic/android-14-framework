package android.telecom;

import android.annotation.SystemApi;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.location.Location;
import android.media.MediaMetrics;
import android.net.Uri;
import android.p008os.Bundle;
import android.p008os.Handler;
import android.p008os.IBinder;
import android.p008os.Looper;
import android.p008os.Message;
import android.p008os.OutcomeReceiver;
import android.p008os.ParcelFileDescriptor;
import android.p008os.RemoteException;
import android.telecom.Conference;
import android.telecom.Connection;
import android.telecom.Logging.Runnable;
import android.telecom.Logging.Session;
import android.telecom.ParcelableConference;
import android.telephony.ims.ImsCallProfile;
import com.android.internal.p028os.SomeArgs;
import com.android.internal.telecom.IConnectionService;
import com.android.internal.telecom.IConnectionServiceAdapter;
import com.android.internal.telecom.RemoteServiceCallback;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
/* loaded from: classes3.dex */
public abstract class ConnectionService extends Service {
    public static final String EXTRA_IS_HANDOVER = "android.telecom.extra.IS_HANDOVER";
    private static final int MSG_ABORT = 3;
    private static final int MSG_ADD_CONNECTION_SERVICE_ADAPTER = 1;
    private static final int MSG_ADD_PARTICIPANT = 39;
    private static final int MSG_ANSWER = 4;
    private static final int MSG_ANSWER_VIDEO = 17;
    private static final int MSG_CONFERENCE = 12;
    private static final int MSG_CONNECTION_SERVICE_FOCUS_GAINED = 31;
    private static final int MSG_CONNECTION_SERVICE_FOCUS_LOST = 30;
    private static final int MSG_CREATE_CONFERENCE = 35;
    private static final int MSG_CREATE_CONFERENCE_COMPLETE = 36;
    private static final int MSG_CREATE_CONFERENCE_FAILED = 37;
    private static final int MSG_CREATE_CONNECTION = 2;
    private static final int MSG_CREATE_CONNECTION_COMPLETE = 29;
    private static final int MSG_CREATE_CONNECTION_FAILED = 25;
    private static final int MSG_DEFLECT = 34;
    private static final int MSG_DISCONNECT = 6;
    private static final int MSG_EXPLICIT_CALL_TRANSFER = 40;
    private static final int MSG_EXPLICIT_CALL_TRANSFER_CONSULTATIVE = 41;
    private static final int MSG_HANDOVER_COMPLETE = 33;
    private static final int MSG_HANDOVER_FAILED = 32;
    private static final int MSG_HOLD = 7;
    private static final int MSG_MERGE_CONFERENCE = 18;
    private static final int MSG_ON_AVAILABLE_CALL_ENDPOINTS_CHANGED = 46;
    private static final int MSG_ON_CALL_AUDIO_STATE_CHANGED = 9;
    private static final int MSG_ON_CALL_ENDPOINT_CHANGED = 45;
    private static final int MSG_ON_CALL_FILTERING_COMPLETED = 42;
    private static final int MSG_ON_EXTRAS_CHANGED = 24;
    private static final int MSG_ON_MUTE_STATE_CHANGED = 47;
    private static final int MSG_ON_POST_DIAL_CONTINUE = 14;
    private static final int MSG_ON_START_RTT = 26;
    private static final int MSG_ON_STOP_RTT = 27;
    private static final int MSG_ON_TRACKED_BY_NON_UI_SERVICE = 44;
    private static final int MSG_ON_USING_ALTERNATIVE_UI = 43;
    private static final int MSG_PLAY_DTMF_TONE = 10;
    private static final int MSG_PULL_EXTERNAL_CALL = 22;
    private static final int MSG_REJECT = 5;
    private static final int MSG_REJECT_WITH_MESSAGE = 20;
    private static final int MSG_REJECT_WITH_REASON = 38;
    private static final int MSG_REMOVE_CONNECTION_SERVICE_ADAPTER = 16;
    private static final int MSG_RTT_UPGRADE_RESPONSE = 28;
    private static final int MSG_SEND_CALL_EVENT = 23;
    private static final int MSG_SILENCE = 21;
    private static final int MSG_SPLIT_FROM_CONFERENCE = 13;
    private static final int MSG_STOP_DTMF_TONE = 11;
    private static final int MSG_SWAP_CONFERENCE = 19;
    private static final int MSG_UNHOLD = 8;
    private static final boolean PII_DEBUG = Log.isLoggable(3);
    public static final String SERVICE_INTERFACE = "android.telecom.ConnectionService";
    private static final String SESSION_ABORT = "CS.ab";
    private static final String SESSION_ADD_CS_ADAPTER = "CS.aCSA";
    private static final String SESSION_ADD_PARTICIPANT = "CS.aP";
    private static final String SESSION_ANSWER = "CS.an";
    private static final String SESSION_ANSWER_VIDEO = "CS.anV";
    private static final String SESSION_AVAILABLE_CALL_ENDPOINTS_CHANGED = "CS.oACEC";
    private static final String SESSION_CALL_AUDIO_SC = "CS.cASC";
    private static final String SESSION_CALL_ENDPOINT_CHANGED = "CS.oCEC";
    private static final String SESSION_CALL_FILTERING_COMPLETED = "CS.oCFC";
    private static final String SESSION_CONFERENCE = "CS.c";
    private static final String SESSION_CONNECTION_SERVICE_FOCUS_GAINED = "CS.cSFG";
    private static final String SESSION_CONNECTION_SERVICE_FOCUS_LOST = "CS.cSFL";
    private static final String SESSION_CONSULTATIVE_TRANSFER = "CS.cTrans";
    private static final String SESSION_CREATE_CONF = "CS.crConf";
    private static final String SESSION_CREATE_CONF_COMPLETE = "CS.crConfC";
    private static final String SESSION_CREATE_CONF_FAILED = "CS.crConfF";
    private static final String SESSION_CREATE_CONN = "CS.crCo";
    private static final String SESSION_CREATE_CONN_COMPLETE = "CS.crCoC";
    private static final String SESSION_CREATE_CONN_FAILED = "CS.crCoF";
    private static final String SESSION_DEFLECT = "CS.def";
    private static final String SESSION_DISCONNECT = "CS.d";
    private static final String SESSION_EXTRAS_CHANGED = "CS.oEC";
    private static final String SESSION_HANDLER = "H.";
    private static final String SESSION_HANDOVER_COMPLETE = "CS.hC";
    private static final String SESSION_HANDOVER_FAILED = "CS.haF";
    private static final String SESSION_HOLD = "CS.h";
    private static final String SESSION_MERGE_CONFERENCE = "CS.mC";
    private static final String SESSION_MUTE_STATE_CHANGED = "CS.oMSC";
    private static final String SESSION_PLAY_DTMF = "CS.pDT";
    private static final String SESSION_POST_DIAL_CONT = "CS.oPDC";
    private static final String SESSION_PULL_EXTERNAL_CALL = "CS.pEC";
    private static final String SESSION_REJECT = "CS.r";
    private static final String SESSION_REJECT_MESSAGE = "CS.rWM";
    private static final String SESSION_REMOVE_CS_ADAPTER = "CS.rCSA";
    private static final String SESSION_RTT_UPGRADE_RESPONSE = "CS.rTRUR";
    private static final String SESSION_SEND_CALL_EVENT = "CS.sCE";
    private static final String SESSION_SILENCE = "CS.s";
    private static final String SESSION_SPLIT_CONFERENCE = "CS.sFC";
    private static final String SESSION_START_RTT = "CS.+RTT";
    private static final String SESSION_STOP_DTMF = "CS.sDT";
    private static final String SESSION_STOP_RTT = "CS.-RTT";
    private static final String SESSION_SWAP_CONFERENCE = "CS.sC";
    private static final String SESSION_TRACKED_BY_NON_UI_SERVICE = "CS.tBNUS";
    private static final String SESSION_TRANSFER = "CS.trans";
    private static final String SESSION_UNHOLD = "CS.u";
    private static final String SESSION_UPDATE_RTT_PIPES = "CS.uRTT";
    private static final String SESSION_USING_ALTERNATIVE_UI = "CS.uAU";
    private static Connection sNullConnection;
    private Conference sNullConference;
    private final Map<String, Connection> mConnectionById = new ConcurrentHashMap();
    private final Map<Connection, String> mIdByConnection = new ConcurrentHashMap();
    private final Map<String, Conference> mConferenceById = new ConcurrentHashMap();
    private final Map<Conference, String> mIdByConference = new ConcurrentHashMap();
    private final RemoteConnectionManager mRemoteConnectionManager = new RemoteConnectionManager(this);
    private final List<Runnable> mPreInitializationConnectionRequests = new ArrayList();
    private final ConnectionServiceAdapter mAdapter = new ConnectionServiceAdapter();
    private boolean mAreAccountsInitialized = false;
    private Object mIdSyncRoot = new Object();
    private int mId = 0;
    private final IBinder mBinder = new IConnectionService.Stub() { // from class: android.telecom.ConnectionService.1
        @Override // com.android.internal.telecom.IConnectionService
        public void addConnectionServiceAdapter(IConnectionServiceAdapter adapter, Session.Info sessionInfo) {
            Log.startSession(sessionInfo, ConnectionService.SESSION_ADD_CS_ADAPTER);
            try {
                SomeArgs args = SomeArgs.obtain();
                args.arg1 = adapter;
                args.arg2 = Log.createSubsession();
                ConnectionService.this.mHandler.obtainMessage(1, args).sendToTarget();
            } finally {
                Log.endSession();
            }
        }

        @Override // com.android.internal.telecom.IConnectionService
        public void removeConnectionServiceAdapter(IConnectionServiceAdapter adapter, Session.Info sessionInfo) {
            Log.startSession(sessionInfo, ConnectionService.SESSION_REMOVE_CS_ADAPTER);
            try {
                SomeArgs args = SomeArgs.obtain();
                args.arg1 = adapter;
                args.arg2 = Log.createSubsession();
                ConnectionService.this.mHandler.obtainMessage(16, args).sendToTarget();
            } finally {
                Log.endSession();
            }
        }

        @Override // com.android.internal.telecom.IConnectionService
        public void createConnection(PhoneAccountHandle connectionManagerPhoneAccount, String id, ConnectionRequest request, boolean isIncoming, boolean isUnknown, Session.Info sessionInfo) {
            Log.startSession(sessionInfo, ConnectionService.SESSION_CREATE_CONN);
            try {
                SomeArgs args = SomeArgs.obtain();
                args.arg1 = connectionManagerPhoneAccount;
                args.arg2 = id;
                args.arg3 = request;
                args.arg4 = Log.createSubsession();
                int i = 1;
                args.argi1 = isIncoming ? 1 : 0;
                if (!isUnknown) {
                    i = 0;
                }
                args.argi2 = i;
                ConnectionService.this.mHandler.obtainMessage(2, args).sendToTarget();
            } finally {
                Log.endSession();
            }
        }

        @Override // com.android.internal.telecom.IConnectionService
        public void createConnectionComplete(String id, Session.Info sessionInfo) {
            Log.startSession(sessionInfo, ConnectionService.SESSION_CREATE_CONN_COMPLETE);
            try {
                SomeArgs args = SomeArgs.obtain();
                args.arg1 = id;
                args.arg2 = Log.createSubsession();
                ConnectionService.this.mHandler.obtainMessage(29, args).sendToTarget();
            } finally {
                Log.endSession();
            }
        }

        @Override // com.android.internal.telecom.IConnectionService
        public void createConnectionFailed(PhoneAccountHandle connectionManagerPhoneAccount, String callId, ConnectionRequest request, boolean isIncoming, Session.Info sessionInfo) {
            Log.startSession(sessionInfo, ConnectionService.SESSION_CREATE_CONN_FAILED);
            try {
                SomeArgs args = SomeArgs.obtain();
                args.arg1 = callId;
                args.arg2 = request;
                args.arg3 = Log.createSubsession();
                args.arg4 = connectionManagerPhoneAccount;
                args.argi1 = isIncoming ? 1 : 0;
                ConnectionService.this.mHandler.obtainMessage(25, args).sendToTarget();
            } finally {
                Log.endSession();
            }
        }

        @Override // com.android.internal.telecom.IConnectionService
        public void createConference(PhoneAccountHandle connectionManagerPhoneAccount, String id, ConnectionRequest request, boolean isIncoming, boolean isUnknown, Session.Info sessionInfo) {
            Log.startSession(sessionInfo, ConnectionService.SESSION_CREATE_CONF);
            try {
                SomeArgs args = SomeArgs.obtain();
                args.arg1 = connectionManagerPhoneAccount;
                args.arg2 = id;
                args.arg3 = request;
                args.arg4 = Log.createSubsession();
                int i = 1;
                args.argi1 = isIncoming ? 1 : 0;
                if (!isUnknown) {
                    i = 0;
                }
                args.argi2 = i;
                ConnectionService.this.mHandler.obtainMessage(35, args).sendToTarget();
            } finally {
                Log.endSession();
            }
        }

        @Override // com.android.internal.telecom.IConnectionService
        public void createConferenceComplete(String id, Session.Info sessionInfo) {
            Log.startSession(sessionInfo, ConnectionService.SESSION_CREATE_CONF_COMPLETE);
            try {
                SomeArgs args = SomeArgs.obtain();
                args.arg1 = id;
                args.arg2 = Log.createSubsession();
                ConnectionService.this.mHandler.obtainMessage(36, args).sendToTarget();
            } finally {
                Log.endSession();
            }
        }

        @Override // com.android.internal.telecom.IConnectionService
        public void createConferenceFailed(PhoneAccountHandle connectionManagerPhoneAccount, String callId, ConnectionRequest request, boolean isIncoming, Session.Info sessionInfo) {
            Log.startSession(sessionInfo, ConnectionService.SESSION_CREATE_CONF_FAILED);
            try {
                SomeArgs args = SomeArgs.obtain();
                args.arg1 = callId;
                args.arg2 = request;
                args.arg3 = Log.createSubsession();
                args.arg4 = connectionManagerPhoneAccount;
                args.argi1 = isIncoming ? 1 : 0;
                ConnectionService.this.mHandler.obtainMessage(37, args).sendToTarget();
            } finally {
                Log.endSession();
            }
        }

        @Override // com.android.internal.telecom.IConnectionService
        public void handoverFailed(String callId, ConnectionRequest request, int reason, Session.Info sessionInfo) {
            Log.startSession(sessionInfo, ConnectionService.SESSION_HANDOVER_FAILED);
            try {
                SomeArgs args = SomeArgs.obtain();
                args.arg1 = callId;
                args.arg2 = request;
                args.arg3 = Log.createSubsession();
                args.arg4 = Integer.valueOf(reason);
                ConnectionService.this.mHandler.obtainMessage(32, args).sendToTarget();
            } finally {
                Log.endSession();
            }
        }

        @Override // com.android.internal.telecom.IConnectionService
        public void handoverComplete(String callId, Session.Info sessionInfo) {
            Log.startSession(sessionInfo, ConnectionService.SESSION_HANDOVER_COMPLETE);
            try {
                SomeArgs args = SomeArgs.obtain();
                args.arg1 = callId;
                args.arg2 = Log.createSubsession();
                ConnectionService.this.mHandler.obtainMessage(33, args).sendToTarget();
            } finally {
                Log.endSession();
            }
        }

        @Override // com.android.internal.telecom.IConnectionService
        public void abort(String callId, Session.Info sessionInfo) {
            Log.startSession(sessionInfo, ConnectionService.SESSION_ABORT);
            try {
                SomeArgs args = SomeArgs.obtain();
                args.arg1 = callId;
                args.arg2 = Log.createSubsession();
                ConnectionService.this.mHandler.obtainMessage(3, args).sendToTarget();
            } finally {
                Log.endSession();
            }
        }

        @Override // com.android.internal.telecom.IConnectionService
        public void answerVideo(String callId, int videoState, Session.Info sessionInfo) {
            Log.startSession(sessionInfo, ConnectionService.SESSION_ANSWER_VIDEO);
            try {
                SomeArgs args = SomeArgs.obtain();
                args.arg1 = callId;
                args.arg2 = Log.createSubsession();
                args.argi1 = videoState;
                ConnectionService.this.mHandler.obtainMessage(17, args).sendToTarget();
            } finally {
                Log.endSession();
            }
        }

        @Override // com.android.internal.telecom.IConnectionService
        public void answer(String callId, Session.Info sessionInfo) {
            Log.startSession(sessionInfo, ConnectionService.SESSION_ANSWER);
            try {
                SomeArgs args = SomeArgs.obtain();
                args.arg1 = callId;
                args.arg2 = Log.createSubsession();
                ConnectionService.this.mHandler.obtainMessage(4, args).sendToTarget();
            } finally {
                Log.endSession();
            }
        }

        @Override // com.android.internal.telecom.IConnectionService
        public void deflect(String callId, Uri address, Session.Info sessionInfo) {
            Log.startSession(sessionInfo, ConnectionService.SESSION_DEFLECT);
            try {
                SomeArgs args = SomeArgs.obtain();
                args.arg1 = callId;
                args.arg2 = address;
                args.arg3 = Log.createSubsession();
                ConnectionService.this.mHandler.obtainMessage(34, args).sendToTarget();
            } finally {
                Log.endSession();
            }
        }

        @Override // com.android.internal.telecom.IConnectionService
        public void reject(String callId, Session.Info sessionInfo) {
            Log.startSession(sessionInfo, ConnectionService.SESSION_REJECT);
            try {
                SomeArgs args = SomeArgs.obtain();
                args.arg1 = callId;
                args.arg2 = Log.createSubsession();
                ConnectionService.this.mHandler.obtainMessage(5, args).sendToTarget();
            } finally {
                Log.endSession();
            }
        }

        @Override // com.android.internal.telecom.IConnectionService
        public void rejectWithReason(String callId, int rejectReason, Session.Info sessionInfo) {
            Log.startSession(sessionInfo, ConnectionService.SESSION_REJECT);
            try {
                SomeArgs args = SomeArgs.obtain();
                args.arg1 = callId;
                args.argi1 = rejectReason;
                args.arg2 = Log.createSubsession();
                ConnectionService.this.mHandler.obtainMessage(38, args).sendToTarget();
            } finally {
                Log.endSession();
            }
        }

        @Override // com.android.internal.telecom.IConnectionService
        public void rejectWithMessage(String callId, String message, Session.Info sessionInfo) {
            Log.startSession(sessionInfo, ConnectionService.SESSION_REJECT_MESSAGE);
            try {
                SomeArgs args = SomeArgs.obtain();
                args.arg1 = callId;
                args.arg2 = message;
                args.arg3 = Log.createSubsession();
                ConnectionService.this.mHandler.obtainMessage(20, args).sendToTarget();
            } finally {
                Log.endSession();
            }
        }

        @Override // com.android.internal.telecom.IConnectionService
        public void transfer(String callId, Uri number, boolean isConfirmationRequired, Session.Info sessionInfo) {
            Log.startSession(sessionInfo, ConnectionService.SESSION_TRANSFER);
            try {
                SomeArgs args = SomeArgs.obtain();
                args.arg1 = callId;
                args.arg2 = number;
                args.argi1 = isConfirmationRequired ? 1 : 0;
                args.arg3 = Log.createSubsession();
                ConnectionService.this.mHandler.obtainMessage(40, args).sendToTarget();
            } finally {
                Log.endSession();
            }
        }

        @Override // com.android.internal.telecom.IConnectionService
        public void consultativeTransfer(String callId, String otherCallId, Session.Info sessionInfo) {
            Log.startSession(sessionInfo, ConnectionService.SESSION_CONSULTATIVE_TRANSFER);
            try {
                SomeArgs args = SomeArgs.obtain();
                args.arg1 = callId;
                args.arg2 = otherCallId;
                args.arg3 = Log.createSubsession();
                ConnectionService.this.mHandler.obtainMessage(41, args).sendToTarget();
            } finally {
                Log.endSession();
            }
        }

        @Override // com.android.internal.telecom.IConnectionService
        public void silence(String callId, Session.Info sessionInfo) {
            Log.startSession(sessionInfo, ConnectionService.SESSION_SILENCE);
            try {
                SomeArgs args = SomeArgs.obtain();
                args.arg1 = callId;
                args.arg2 = Log.createSubsession();
                ConnectionService.this.mHandler.obtainMessage(21, args).sendToTarget();
            } finally {
                Log.endSession();
            }
        }

        @Override // com.android.internal.telecom.IConnectionService
        public void disconnect(String callId, Session.Info sessionInfo) {
            Log.startSession(sessionInfo, ConnectionService.SESSION_DISCONNECT);
            try {
                SomeArgs args = SomeArgs.obtain();
                args.arg1 = callId;
                args.arg2 = Log.createSubsession();
                ConnectionService.this.mHandler.obtainMessage(6, args).sendToTarget();
            } finally {
                Log.endSession();
            }
        }

        @Override // com.android.internal.telecom.IConnectionService
        public void hold(String callId, Session.Info sessionInfo) {
            Log.startSession(sessionInfo, ConnectionService.SESSION_HOLD);
            try {
                SomeArgs args = SomeArgs.obtain();
                args.arg1 = callId;
                args.arg2 = Log.createSubsession();
                ConnectionService.this.mHandler.obtainMessage(7, args).sendToTarget();
            } finally {
                Log.endSession();
            }
        }

        @Override // com.android.internal.telecom.IConnectionService
        public void unhold(String callId, Session.Info sessionInfo) {
            Log.startSession(sessionInfo, ConnectionService.SESSION_UNHOLD);
            try {
                SomeArgs args = SomeArgs.obtain();
                args.arg1 = callId;
                args.arg2 = Log.createSubsession();
                ConnectionService.this.mHandler.obtainMessage(8, args).sendToTarget();
            } finally {
                Log.endSession();
            }
        }

        @Override // com.android.internal.telecom.IConnectionService
        public void onCallAudioStateChanged(String callId, CallAudioState callAudioState, Session.Info sessionInfo) {
            Log.startSession(sessionInfo, ConnectionService.SESSION_CALL_AUDIO_SC);
            try {
                SomeArgs args = SomeArgs.obtain();
                args.arg1 = callId;
                args.arg2 = callAudioState;
                args.arg3 = Log.createSubsession();
                ConnectionService.this.mHandler.obtainMessage(9, args).sendToTarget();
            } finally {
                Log.endSession();
            }
        }

        @Override // com.android.internal.telecom.IConnectionService
        public void onCallEndpointChanged(String callId, CallEndpoint callEndpoint, Session.Info sessionInfo) {
            Log.startSession(sessionInfo, ConnectionService.SESSION_CALL_ENDPOINT_CHANGED);
            try {
                SomeArgs args = SomeArgs.obtain();
                args.arg1 = callId;
                args.arg2 = callEndpoint;
                args.arg3 = Log.createSubsession();
                ConnectionService.this.mHandler.obtainMessage(45, args).sendToTarget();
            } finally {
                Log.endSession();
            }
        }

        @Override // com.android.internal.telecom.IConnectionService
        public void onAvailableCallEndpointsChanged(String callId, List<CallEndpoint> availableCallEndpoints, Session.Info sessionInfo) {
            Log.startSession(sessionInfo, ConnectionService.SESSION_AVAILABLE_CALL_ENDPOINTS_CHANGED);
            try {
                SomeArgs args = SomeArgs.obtain();
                args.arg1 = callId;
                args.arg2 = availableCallEndpoints;
                args.arg3 = Log.createSubsession();
                ConnectionService.this.mHandler.obtainMessage(46, args).sendToTarget();
            } finally {
                Log.endSession();
            }
        }

        @Override // com.android.internal.telecom.IConnectionService
        public void onMuteStateChanged(String callId, boolean isMuted, Session.Info sessionInfo) {
            Log.startSession(sessionInfo, ConnectionService.SESSION_MUTE_STATE_CHANGED);
            try {
                SomeArgs args = SomeArgs.obtain();
                args.arg1 = callId;
                args.arg2 = Boolean.valueOf(isMuted);
                args.arg3 = Log.createSubsession();
                ConnectionService.this.mHandler.obtainMessage(47, args).sendToTarget();
            } finally {
                Log.endSession();
            }
        }

        @Override // com.android.internal.telecom.IConnectionService
        public void onUsingAlternativeUi(String callId, boolean usingAlternativeUiShowing, Session.Info sessionInfo) {
            Log.startSession(sessionInfo, ConnectionService.SESSION_USING_ALTERNATIVE_UI);
            try {
                SomeArgs args = SomeArgs.obtain();
                args.arg1 = callId;
                args.arg2 = Boolean.valueOf(usingAlternativeUiShowing);
                args.arg3 = Log.createSubsession();
                ConnectionService.this.mHandler.obtainMessage(43, args).sendToTarget();
            } finally {
                Log.endSession();
            }
        }

        @Override // com.android.internal.telecom.IConnectionService
        public void onTrackedByNonUiService(String callId, boolean isTracked, Session.Info sessionInfo) {
            Log.startSession(sessionInfo, ConnectionService.SESSION_TRACKED_BY_NON_UI_SERVICE);
            try {
                SomeArgs args = SomeArgs.obtain();
                args.arg1 = callId;
                args.arg2 = Boolean.valueOf(isTracked);
                args.arg3 = Log.createSubsession();
                ConnectionService.this.mHandler.obtainMessage(44, args).sendToTarget();
            } finally {
                Log.endSession();
            }
        }

        @Override // com.android.internal.telecom.IConnectionService
        public void playDtmfTone(String callId, char digit, Session.Info sessionInfo) {
            Log.startSession(sessionInfo, ConnectionService.SESSION_PLAY_DTMF);
            try {
                SomeArgs args = SomeArgs.obtain();
                args.arg1 = Character.valueOf(digit);
                args.arg2 = callId;
                args.arg3 = Log.createSubsession();
                ConnectionService.this.mHandler.obtainMessage(10, args).sendToTarget();
            } finally {
                Log.endSession();
            }
        }

        @Override // com.android.internal.telecom.IConnectionService
        public void stopDtmfTone(String callId, Session.Info sessionInfo) {
            Log.startSession(sessionInfo, ConnectionService.SESSION_STOP_DTMF);
            try {
                SomeArgs args = SomeArgs.obtain();
                args.arg1 = callId;
                args.arg2 = Log.createSubsession();
                ConnectionService.this.mHandler.obtainMessage(11, args).sendToTarget();
            } finally {
                Log.endSession();
            }
        }

        @Override // com.android.internal.telecom.IConnectionService
        public void conference(String callId1, String callId2, Session.Info sessionInfo) {
            Log.startSession(sessionInfo, ConnectionService.SESSION_CONFERENCE);
            try {
                SomeArgs args = SomeArgs.obtain();
                args.arg1 = callId1;
                args.arg2 = callId2;
                args.arg3 = Log.createSubsession();
                ConnectionService.this.mHandler.obtainMessage(12, args).sendToTarget();
            } finally {
                Log.endSession();
            }
        }

        @Override // com.android.internal.telecom.IConnectionService
        public void splitFromConference(String callId, Session.Info sessionInfo) {
            Log.startSession(sessionInfo, ConnectionService.SESSION_SPLIT_CONFERENCE);
            try {
                SomeArgs args = SomeArgs.obtain();
                args.arg1 = callId;
                args.arg2 = Log.createSubsession();
                ConnectionService.this.mHandler.obtainMessage(13, args).sendToTarget();
            } finally {
                Log.endSession();
            }
        }

        @Override // com.android.internal.telecom.IConnectionService
        public void mergeConference(String callId, Session.Info sessionInfo) {
            Log.startSession(sessionInfo, ConnectionService.SESSION_MERGE_CONFERENCE);
            try {
                SomeArgs args = SomeArgs.obtain();
                args.arg1 = callId;
                args.arg2 = Log.createSubsession();
                ConnectionService.this.mHandler.obtainMessage(18, args).sendToTarget();
            } finally {
                Log.endSession();
            }
        }

        @Override // com.android.internal.telecom.IConnectionService
        public void swapConference(String callId, Session.Info sessionInfo) {
            Log.startSession(sessionInfo, ConnectionService.SESSION_SWAP_CONFERENCE);
            try {
                SomeArgs args = SomeArgs.obtain();
                args.arg1 = callId;
                args.arg2 = Log.createSubsession();
                ConnectionService.this.mHandler.obtainMessage(19, args).sendToTarget();
            } finally {
                Log.endSession();
            }
        }

        @Override // com.android.internal.telecom.IConnectionService
        public void addConferenceParticipants(String callId, List<Uri> participants, Session.Info sessionInfo) {
            Log.startSession(sessionInfo, ConnectionService.SESSION_ADD_PARTICIPANT);
            try {
                SomeArgs args = SomeArgs.obtain();
                args.arg1 = callId;
                args.arg2 = participants;
                args.arg3 = Log.createSubsession();
                ConnectionService.this.mHandler.obtainMessage(39, args).sendToTarget();
            } finally {
                Log.endSession();
            }
        }

        @Override // com.android.internal.telecom.IConnectionService
        public void onPostDialContinue(String callId, boolean proceed, Session.Info sessionInfo) {
            Log.startSession(sessionInfo, ConnectionService.SESSION_POST_DIAL_CONT);
            try {
                SomeArgs args = SomeArgs.obtain();
                args.arg1 = callId;
                args.arg2 = Log.createSubsession();
                args.argi1 = proceed ? 1 : 0;
                ConnectionService.this.mHandler.obtainMessage(14, args).sendToTarget();
            } finally {
                Log.endSession();
            }
        }

        @Override // com.android.internal.telecom.IConnectionService
        public void pullExternalCall(String callId, Session.Info sessionInfo) {
            Log.startSession(sessionInfo, ConnectionService.SESSION_PULL_EXTERNAL_CALL);
            try {
                SomeArgs args = SomeArgs.obtain();
                args.arg1 = callId;
                args.arg2 = Log.createSubsession();
                ConnectionService.this.mHandler.obtainMessage(22, args).sendToTarget();
            } finally {
                Log.endSession();
            }
        }

        @Override // com.android.internal.telecom.IConnectionService
        public void sendCallEvent(String callId, String event, Bundle extras, Session.Info sessionInfo) {
            Log.startSession(sessionInfo, ConnectionService.SESSION_SEND_CALL_EVENT);
            try {
                SomeArgs args = SomeArgs.obtain();
                args.arg1 = callId;
                args.arg2 = event;
                args.arg3 = extras;
                args.arg4 = Log.createSubsession();
                ConnectionService.this.mHandler.obtainMessage(23, args).sendToTarget();
            } finally {
                Log.endSession();
            }
        }

        @Override // com.android.internal.telecom.IConnectionService
        public void onCallFilteringCompleted(String callId, Connection.CallFilteringCompletionInfo completionInfo, Session.Info sessionInfo) {
            Log.startSession(sessionInfo, ConnectionService.SESSION_CALL_FILTERING_COMPLETED);
            try {
                SomeArgs args = SomeArgs.obtain();
                args.arg1 = callId;
                args.arg2 = completionInfo;
                args.arg3 = Log.createSubsession();
                ConnectionService.this.mHandler.obtainMessage(42, args).sendToTarget();
            } finally {
                Log.endSession();
            }
        }

        @Override // com.android.internal.telecom.IConnectionService
        public void onExtrasChanged(String callId, Bundle extras, Session.Info sessionInfo) {
            Log.startSession(sessionInfo, ConnectionService.SESSION_EXTRAS_CHANGED);
            try {
                SomeArgs args = SomeArgs.obtain();
                args.arg1 = callId;
                args.arg2 = extras;
                args.arg3 = Log.createSubsession();
                ConnectionService.this.mHandler.obtainMessage(24, args).sendToTarget();
            } finally {
                Log.endSession();
            }
        }

        @Override // com.android.internal.telecom.IConnectionService
        public void startRtt(String callId, ParcelFileDescriptor fromInCall, ParcelFileDescriptor toInCall, Session.Info sessionInfo) throws RemoteException {
            Log.startSession(sessionInfo, ConnectionService.SESSION_START_RTT);
            try {
                SomeArgs args = SomeArgs.obtain();
                args.arg1 = callId;
                args.arg2 = new Connection.RttTextStream(toInCall, fromInCall);
                args.arg3 = Log.createSubsession();
                ConnectionService.this.mHandler.obtainMessage(26, args).sendToTarget();
            } finally {
                Log.endSession();
            }
        }

        @Override // com.android.internal.telecom.IConnectionService
        public void stopRtt(String callId, Session.Info sessionInfo) throws RemoteException {
            Log.startSession(sessionInfo, ConnectionService.SESSION_STOP_RTT);
            try {
                SomeArgs args = SomeArgs.obtain();
                args.arg1 = callId;
                args.arg2 = Log.createSubsession();
                ConnectionService.this.mHandler.obtainMessage(27, args).sendToTarget();
            } finally {
                Log.endSession();
            }
        }

        @Override // com.android.internal.telecom.IConnectionService
        public void respondToRttUpgradeRequest(String callId, ParcelFileDescriptor fromInCall, ParcelFileDescriptor toInCall, Session.Info sessionInfo) throws RemoteException {
            Log.startSession(sessionInfo, ConnectionService.SESSION_RTT_UPGRADE_RESPONSE);
            try {
                SomeArgs args = SomeArgs.obtain();
                args.arg1 = callId;
                if (toInCall != null && fromInCall != null) {
                    args.arg2 = new Connection.RttTextStream(toInCall, fromInCall);
                    args.arg3 = Log.createSubsession();
                    ConnectionService.this.mHandler.obtainMessage(28, args).sendToTarget();
                }
                args.arg2 = null;
                args.arg3 = Log.createSubsession();
                ConnectionService.this.mHandler.obtainMessage(28, args).sendToTarget();
            } finally {
                Log.endSession();
            }
        }

        @Override // com.android.internal.telecom.IConnectionService
        public void connectionServiceFocusLost(Session.Info sessionInfo) throws RemoteException {
            Log.startSession(sessionInfo, ConnectionService.SESSION_CONNECTION_SERVICE_FOCUS_LOST);
            try {
                ConnectionService.this.mHandler.obtainMessage(30).sendToTarget();
            } finally {
                Log.endSession();
            }
        }

        @Override // com.android.internal.telecom.IConnectionService
        public void connectionServiceFocusGained(Session.Info sessionInfo) throws RemoteException {
            Log.startSession(sessionInfo, ConnectionService.SESSION_CONNECTION_SERVICE_FOCUS_GAINED);
            try {
                ConnectionService.this.mHandler.obtainMessage(31).sendToTarget();
            } finally {
                Log.endSession();
            }
        }
    };
    private final Handler mHandler = new Handler(Looper.getMainLooper()) { // from class: android.telecom.ConnectionService.2
        @Override // android.p008os.Handler
        public void handleMessage(Message msg) {
            SomeArgs args;
            final boolean isIncoming;
            switch (msg.what) {
                case 1:
                    args = (SomeArgs) msg.obj;
                    try {
                        IConnectionServiceAdapter adapter = (IConnectionServiceAdapter) args.arg1;
                        Log.continueSession((Session) args.arg2, "H.CS.aCSA");
                        ConnectionService.this.mAdapter.addAdapter(adapter);
                        ConnectionService.this.onAdapterAttached();
                        return;
                    } finally {
                    }
                case 2:
                    args = (SomeArgs) msg.obj;
                    Log.continueSession((Session) args.arg4, "H.CS.crCo");
                    try {
                        final PhoneAccountHandle connectionManagerPhoneAccount = (PhoneAccountHandle) args.arg1;
                        final String id = (String) args.arg2;
                        final ConnectionRequest request = (ConnectionRequest) args.arg3;
                        final boolean isIncoming2 = args.argi1 == 1;
                        isIncoming = args.argi2 == 1;
                        if (!ConnectionService.this.mAreAccountsInitialized) {
                            Log.m139d(this, "Enqueueing pre-init request %s", id);
                            ConnectionService.this.mPreInitializationConnectionRequests.add(new Runnable("H.CS.crCo.pICR", null) { // from class: android.telecom.ConnectionService.2.1
                                @Override // android.telecom.Logging.Runnable
                                public void loggedRun() {
                                    ConnectionService.this.createConnection(connectionManagerPhoneAccount, id, request, isIncoming2, isIncoming);
                                }
                            }.prepare());
                        } else {
                            ConnectionService.this.createConnection(connectionManagerPhoneAccount, id, request, isIncoming2, isIncoming);
                        }
                        return;
                    } finally {
                    }
                case 3:
                    args = (SomeArgs) msg.obj;
                    Log.continueSession((Session) args.arg2, "H.CS.ab");
                    try {
                        ConnectionService.this.abort((String) args.arg1);
                        return;
                    } finally {
                    }
                case 4:
                    args = (SomeArgs) msg.obj;
                    Log.continueSession((Session) args.arg2, "H.CS.an");
                    try {
                        ConnectionService.this.answer((String) args.arg1);
                        return;
                    } finally {
                    }
                case 5:
                    args = (SomeArgs) msg.obj;
                    Log.continueSession((Session) args.arg2, "H.CS.r");
                    try {
                        ConnectionService.this.reject((String) args.arg1);
                        return;
                    } finally {
                    }
                case 6:
                    args = (SomeArgs) msg.obj;
                    Log.continueSession((Session) args.arg2, "H.CS.d");
                    try {
                        ConnectionService.this.disconnect((String) args.arg1);
                        return;
                    } finally {
                    }
                case 7:
                    args = (SomeArgs) msg.obj;
                    Log.continueSession((Session) args.arg2, "H.CS.r");
                    try {
                        ConnectionService.this.hold((String) args.arg1);
                        return;
                    } finally {
                    }
                case 8:
                    args = (SomeArgs) msg.obj;
                    Log.continueSession((Session) args.arg2, "H.CS.u");
                    try {
                        ConnectionService.this.unhold((String) args.arg1);
                        return;
                    } finally {
                    }
                case 9:
                    args = (SomeArgs) msg.obj;
                    Log.continueSession((Session) args.arg3, "H.CS.cASC");
                    try {
                        String callId = (String) args.arg1;
                        CallAudioState audioState = (CallAudioState) args.arg2;
                        ConnectionService.this.onCallAudioStateChanged(callId, new CallAudioState(audioState));
                        return;
                    } finally {
                    }
                case 10:
                    args = (SomeArgs) msg.obj;
                    try {
                        Log.continueSession((Session) args.arg3, "H.CS.pDT");
                        ConnectionService.this.playDtmfTone((String) args.arg2, ((Character) args.arg1).charValue());
                        return;
                    } finally {
                    }
                case 11:
                    args = (SomeArgs) msg.obj;
                    try {
                        Log.continueSession((Session) args.arg2, "H.CS.sDT");
                        ConnectionService.this.stopDtmfTone((String) args.arg1);
                        return;
                    } finally {
                    }
                case 12:
                    args = (SomeArgs) msg.obj;
                    try {
                        Log.continueSession((Session) args.arg3, "H.CS.c");
                        String callId1 = (String) args.arg1;
                        String callId2 = (String) args.arg2;
                        ConnectionService.this.conference(callId1, callId2);
                        return;
                    } finally {
                    }
                case 13:
                    args = (SomeArgs) msg.obj;
                    try {
                        Log.continueSession((Session) args.arg2, "H.CS.sFC");
                        ConnectionService.this.splitFromConference((String) args.arg1);
                        return;
                    } finally {
                    }
                case 14:
                    args = (SomeArgs) msg.obj;
                    try {
                        Log.continueSession((Session) args.arg2, "H.CS.oPDC");
                        String callId3 = (String) args.arg1;
                        boolean proceed = args.argi1 == 1;
                        ConnectionService.this.onPostDialContinue(callId3, proceed);
                        return;
                    } finally {
                    }
                case 15:
                default:
                    return;
                case 16:
                    args = (SomeArgs) msg.obj;
                    try {
                        Log.continueSession((Session) args.arg2, "H.CS.rCSA");
                        ConnectionService.this.mAdapter.removeAdapter((IConnectionServiceAdapter) args.arg1);
                        return;
                    } finally {
                    }
                case 17:
                    args = (SomeArgs) msg.obj;
                    Log.continueSession((Session) args.arg2, "H.CS.anV");
                    try {
                        String callId4 = (String) args.arg1;
                        int videoState = args.argi1;
                        ConnectionService.this.answerVideo(callId4, videoState);
                        return;
                    } finally {
                    }
                case 18:
                    args = (SomeArgs) msg.obj;
                    try {
                        Log.continueSession((Session) args.arg2, "H.CS.mC");
                        ConnectionService.this.mergeConference((String) args.arg1);
                        return;
                    } finally {
                    }
                case 19:
                    args = (SomeArgs) msg.obj;
                    try {
                        Log.continueSession((Session) args.arg2, "H.CS.sC");
                        ConnectionService.this.swapConference((String) args.arg1);
                        return;
                    } finally {
                    }
                case 20:
                    args = (SomeArgs) msg.obj;
                    Log.continueSession((Session) args.arg3, "H.CS.rWM");
                    try {
                        ConnectionService.this.reject((String) args.arg1, (String) args.arg2);
                        return;
                    } finally {
                    }
                case 21:
                    args = (SomeArgs) msg.obj;
                    Log.continueSession((Session) args.arg2, "H.CS.s");
                    try {
                        ConnectionService.this.silence((String) args.arg1);
                        return;
                    } finally {
                    }
                case 22:
                    args = (SomeArgs) msg.obj;
                    try {
                        Log.continueSession((Session) args.arg2, "H.CS.pEC");
                        ConnectionService.this.pullExternalCall((String) args.arg1);
                        return;
                    } finally {
                    }
                case 23:
                    args = (SomeArgs) msg.obj;
                    try {
                        Log.continueSession((Session) args.arg4, "H.CS.sCE");
                        String callId5 = (String) args.arg1;
                        String event = (String) args.arg2;
                        Bundle extras = (Bundle) args.arg3;
                        ConnectionService.this.sendCallEvent(callId5, event, extras);
                        return;
                    } finally {
                    }
                case 24:
                    args = (SomeArgs) msg.obj;
                    try {
                        Log.continueSession((Session) args.arg3, "H.CS.oEC");
                        String callId6 = (String) args.arg1;
                        Bundle extras2 = (Bundle) args.arg2;
                        ConnectionService.this.handleExtrasChanged(callId6, extras2);
                        return;
                    } finally {
                    }
                case 25:
                    args = (SomeArgs) msg.obj;
                    Log.continueSession((Session) args.arg3, "H.CS.crCoF");
                    try {
                        final String id2 = (String) args.arg1;
                        final ConnectionRequest request2 = (ConnectionRequest) args.arg2;
                        isIncoming = args.argi1 == 1;
                        final PhoneAccountHandle connectionMgrPhoneAccount = (PhoneAccountHandle) args.arg4;
                        if (!ConnectionService.this.mAreAccountsInitialized) {
                            Log.m139d(this, "Enqueueing pre-init request %s", id2);
                            final boolean z = isIncoming;
                            ConnectionService.this.mPreInitializationConnectionRequests.add(new Runnable("H.CS.crCoF.pICR", null) { // from class: android.telecom.ConnectionService.2.3
                                @Override // android.telecom.Logging.Runnable
                                public void loggedRun() {
                                    ConnectionService.this.createConnectionFailed(connectionMgrPhoneAccount, id2, request2, z);
                                }
                            }.prepare());
                        } else {
                            Log.m135i(this, "createConnectionFailed %s", id2);
                            ConnectionService.this.createConnectionFailed(connectionMgrPhoneAccount, id2, request2, isIncoming);
                        }
                        return;
                    } finally {
                    }
                case 26:
                    args = (SomeArgs) msg.obj;
                    try {
                        Log.continueSession((Session) args.arg3, "H.CS.+RTT");
                        String callId7 = (String) args.arg1;
                        Connection.RttTextStream rttTextStream = (Connection.RttTextStream) args.arg2;
                        ConnectionService.this.startRtt(callId7, rttTextStream);
                        return;
                    } finally {
                    }
                case 27:
                    args = (SomeArgs) msg.obj;
                    try {
                        Log.continueSession((Session) args.arg2, "H.CS.-RTT");
                        String callId8 = (String) args.arg1;
                        ConnectionService.this.stopRtt(callId8);
                        return;
                    } finally {
                    }
                case 28:
                    args = (SomeArgs) msg.obj;
                    try {
                        Log.continueSession((Session) args.arg3, "H.CS.rTRUR");
                        String callId9 = (String) args.arg1;
                        Connection.RttTextStream rttTextStream2 = (Connection.RttTextStream) args.arg2;
                        ConnectionService.this.handleRttUpgradeResponse(callId9, rttTextStream2);
                        return;
                    } finally {
                    }
                case 29:
                    args = (SomeArgs) msg.obj;
                    Log.continueSession((Session) args.arg2, "H.CS.crCoC");
                    try {
                        final String id3 = (String) args.arg1;
                        if (!ConnectionService.this.mAreAccountsInitialized) {
                            Log.m139d(this, "Enqueueing pre-init request %s", id3);
                            ConnectionService.this.mPreInitializationConnectionRequests.add(new Runnable("H.CS.crCoC.pICR", null) { // from class: android.telecom.ConnectionService.2.2
                                @Override // android.telecom.Logging.Runnable
                                public void loggedRun() {
                                    ConnectionService.this.notifyCreateConnectionComplete(id3);
                                }
                            }.prepare());
                        } else {
                            ConnectionService.this.notifyCreateConnectionComplete(id3);
                        }
                        return;
                    } finally {
                    }
                case 30:
                    ConnectionService.this.onConnectionServiceFocusLost();
                    return;
                case 31:
                    ConnectionService.this.onConnectionServiceFocusGained();
                    return;
                case 32:
                    args = (SomeArgs) msg.obj;
                    Log.continueSession((Session) args.arg3, "H.CS.haF");
                    try {
                        final String id4 = (String) args.arg1;
                        final ConnectionRequest request3 = (ConnectionRequest) args.arg2;
                        final int reason = ((Integer) args.arg4).intValue();
                        if (!ConnectionService.this.mAreAccountsInitialized) {
                            Log.m139d(this, "Enqueueing pre-init request %s", id4);
                            ConnectionService.this.mPreInitializationConnectionRequests.add(new Runnable("H.CS.haF.pICR", null) { // from class: android.telecom.ConnectionService.2.7
                                @Override // android.telecom.Logging.Runnable
                                public void loggedRun() {
                                    ConnectionService.this.handoverFailed(id4, request3, reason);
                                }
                            }.prepare());
                        } else {
                            Log.m135i(this, "createConnectionFailed %s", id4);
                            ConnectionService.this.handoverFailed(id4, request3, reason);
                        }
                        return;
                    } finally {
                    }
                case 33:
                    args = (SomeArgs) msg.obj;
                    try {
                        Log.continueSession((Session) args.arg2, "H.CS.hC");
                        String callId10 = (String) args.arg1;
                        ConnectionService.this.notifyHandoverComplete(callId10);
                        return;
                    } finally {
                    }
                case 34:
                    args = (SomeArgs) msg.obj;
                    Log.continueSession((Session) args.arg3, "H.CS.def");
                    try {
                        ConnectionService.this.deflect((String) args.arg1, (Uri) args.arg2);
                        return;
                    } finally {
                    }
                case 35:
                    args = (SomeArgs) msg.obj;
                    Log.continueSession((Session) args.arg4, "H.CS.crCo");
                    try {
                        final PhoneAccountHandle connectionManagerPhoneAccount2 = (PhoneAccountHandle) args.arg1;
                        final String id5 = (String) args.arg2;
                        final ConnectionRequest request4 = (ConnectionRequest) args.arg3;
                        final boolean isIncoming3 = args.argi1 == 1;
                        isIncoming = args.argi2 == 1;
                        if (!ConnectionService.this.mAreAccountsInitialized) {
                            Log.m139d(this, "Enqueueing pre-initconference request %s", id5);
                            ConnectionService.this.mPreInitializationConnectionRequests.add(new Runnable("H.CS.crConf.pIConfR", null) { // from class: android.telecom.ConnectionService.2.4
                                @Override // android.telecom.Logging.Runnable
                                public void loggedRun() {
                                    ConnectionService.this.createConference(connectionManagerPhoneAccount2, id5, request4, isIncoming3, isIncoming);
                                }
                            }.prepare());
                        } else {
                            ConnectionService.this.createConference(connectionManagerPhoneAccount2, id5, request4, isIncoming3, isIncoming);
                        }
                        return;
                    } finally {
                    }
                case 36:
                    args = (SomeArgs) msg.obj;
                    Log.continueSession((Session) args.arg2, "H.CS.crCoC");
                    try {
                        final String id6 = (String) args.arg1;
                        if (!ConnectionService.this.mAreAccountsInitialized) {
                            Log.m139d(this, "Enqueueing pre-init conference request %s", id6);
                            ConnectionService.this.mPreInitializationConnectionRequests.add(new Runnable("H.CS.crConfC.pIConfR", null) { // from class: android.telecom.ConnectionService.2.5
                                @Override // android.telecom.Logging.Runnable
                                public void loggedRun() {
                                    ConnectionService.this.notifyCreateConferenceComplete(id6);
                                }
                            }.prepare());
                        } else {
                            ConnectionService.this.notifyCreateConferenceComplete(id6);
                        }
                        return;
                    } finally {
                    }
                case 37:
                    args = (SomeArgs) msg.obj;
                    Log.continueSession((Session) args.arg3, "H.CS.crCoF");
                    try {
                        final String id7 = (String) args.arg1;
                        final ConnectionRequest request5 = (ConnectionRequest) args.arg2;
                        isIncoming = args.argi1 == 1;
                        final PhoneAccountHandle connectionMgrPhoneAccount2 = (PhoneAccountHandle) args.arg4;
                        if (!ConnectionService.this.mAreAccountsInitialized) {
                            Log.m139d(this, "Enqueueing pre-init conference request %s", id7);
                            final boolean z2 = isIncoming;
                            ConnectionService.this.mPreInitializationConnectionRequests.add(new Runnable("H.CS.crConfF.pIConfR", null) { // from class: android.telecom.ConnectionService.2.6
                                @Override // android.telecom.Logging.Runnable
                                public void loggedRun() {
                                    ConnectionService.this.createConferenceFailed(connectionMgrPhoneAccount2, id7, request5, z2);
                                }
                            }.prepare());
                        } else {
                            Log.m135i(this, "createConferenceFailed %s", id7);
                            ConnectionService.this.createConferenceFailed(connectionMgrPhoneAccount2, id7, request5, isIncoming);
                        }
                        return;
                    } finally {
                    }
                case 38:
                    args = (SomeArgs) msg.obj;
                    Log.continueSession((Session) args.arg2, "H.CS.r");
                    try {
                        ConnectionService.this.reject((String) args.arg1, args.argi1);
                        return;
                    } finally {
                    }
                case 39:
                    args = (SomeArgs) msg.obj;
                    try {
                        Log.continueSession((Session) args.arg3, "H.CS.aP");
                        ConnectionService.this.addConferenceParticipants((String) args.arg1, (List) args.arg2);
                        return;
                    } finally {
                    }
                case 40:
                    args = (SomeArgs) msg.obj;
                    Log.continueSession((Session) args.arg3, "H.CS.trans");
                    try {
                        boolean isConfirmationRequired = args.argi1 == 1;
                        ConnectionService.this.transfer((String) args.arg1, (Uri) args.arg2, isConfirmationRequired);
                        return;
                    } finally {
                    }
                case 41:
                    args = (SomeArgs) msg.obj;
                    Log.continueSession((Session) args.arg3, "H.CS.cTrans");
                    try {
                        ConnectionService.this.consultativeTransfer((String) args.arg1, (String) args.arg2);
                        return;
                    } finally {
                    }
                case 42:
                    args = (SomeArgs) msg.obj;
                    try {
                        Log.continueSession((Session) args.arg3, "H.CS.oCFC");
                        String callId11 = (String) args.arg1;
                        Connection.CallFilteringCompletionInfo completionInfo = (Connection.CallFilteringCompletionInfo) args.arg2;
                        ConnectionService.this.onCallFilteringCompleted(callId11, completionInfo);
                        return;
                    } finally {
                    }
                case 43:
                    args = (SomeArgs) msg.obj;
                    Log.continueSession((Session) args.arg3, "H.CS.uAU");
                    try {
                        String callId12 = (String) args.arg1;
                        boolean isUsingAlternativeUi = ((Boolean) args.arg2).booleanValue();
                        ConnectionService.this.onUsingAlternativeUi(callId12, isUsingAlternativeUi);
                        return;
                    } finally {
                    }
                case 44:
                    args = (SomeArgs) msg.obj;
                    Log.continueSession((Session) args.arg3, "H.CS.tBNUS");
                    try {
                        String callId13 = (String) args.arg1;
                        boolean isTracked = ((Boolean) args.arg2).booleanValue();
                        ConnectionService.this.onTrackedByNonUiService(callId13, isTracked);
                        return;
                    } finally {
                    }
                case 45:
                    args = (SomeArgs) msg.obj;
                    Log.continueSession((Session) args.arg3, "H.CS.cASC");
                    try {
                        String callId14 = (String) args.arg1;
                        CallEndpoint callEndpoint = (CallEndpoint) args.arg2;
                        ConnectionService.this.onCallEndpointChanged(callId14, callEndpoint);
                        return;
                    } finally {
                    }
                case 46:
                    args = (SomeArgs) msg.obj;
                    Log.continueSession((Session) args.arg3, "H.CS.cASC");
                    try {
                        String callId15 = (String) args.arg1;
                        List<CallEndpoint> availableCallEndpoints = (List) args.arg2;
                        ConnectionService.this.onAvailableCallEndpointsChanged(callId15, availableCallEndpoints);
                        return;
                    } finally {
                    }
                case 47:
                    args = (SomeArgs) msg.obj;
                    Log.continueSession((Session) args.arg3, "H.CS.cASC");
                    try {
                        String callId16 = (String) args.arg1;
                        boolean isMuted = ((Boolean) args.arg2).booleanValue();
                        ConnectionService.this.onMuteStateChanged(callId16, isMuted);
                        return;
                    } finally {
                    }
            }
        }
    };
    private final Conference.Listener mConferenceListener = new Conference.Listener() { // from class: android.telecom.ConnectionService.3
        @Override // android.telecom.Conference.Listener
        public void onStateChanged(Conference conference, int oldState, int newState) {
            String id = (String) ConnectionService.this.mIdByConference.get(conference);
            switch (newState) {
                case 2:
                    ConnectionService.this.mAdapter.setRinging(id);
                    return;
                case 3:
                    ConnectionService.this.mAdapter.setDialing(id);
                    return;
                case 4:
                    ConnectionService.this.mAdapter.setActive(id);
                    return;
                case 5:
                    ConnectionService.this.mAdapter.setOnHold(id);
                    return;
                default:
                    return;
            }
        }

        @Override // android.telecom.Conference.Listener
        public void onDisconnected(Conference conference, DisconnectCause disconnectCause) {
            String id = (String) ConnectionService.this.mIdByConference.get(conference);
            ConnectionService.this.mAdapter.setDisconnected(id, disconnectCause);
        }

        @Override // android.telecom.Conference.Listener
        public void onConnectionAdded(Conference conference, Connection connection) {
        }

        @Override // android.telecom.Conference.Listener
        public void onConnectionRemoved(Conference conference, Connection connection) {
        }

        @Override // android.telecom.Conference.Listener
        public void onConferenceableConnectionsChanged(Conference conference, List<Connection> conferenceableConnections) {
            ConnectionService.this.mAdapter.setConferenceableConnections((String) ConnectionService.this.mIdByConference.get(conference), ConnectionService.this.createConnectionIdList(conferenceableConnections));
        }

        @Override // android.telecom.Conference.Listener
        public void onDestroyed(Conference conference) {
            ConnectionService.this.removeConference(conference);
        }

        @Override // android.telecom.Conference.Listener
        public void onConnectionCapabilitiesChanged(Conference conference, int connectionCapabilities) {
            String id = (String) ConnectionService.this.mIdByConference.get(conference);
            Log.m139d(this, "call capabilities: conference: %s", Connection.capabilitiesToString(connectionCapabilities));
            ConnectionService.this.mAdapter.setConnectionCapabilities(id, connectionCapabilities);
        }

        @Override // android.telecom.Conference.Listener
        public void onConnectionPropertiesChanged(Conference conference, int connectionProperties) {
            String id = (String) ConnectionService.this.mIdByConference.get(conference);
            Log.m139d(this, "call capabilities: conference: %s", Connection.propertiesToString(connectionProperties));
            ConnectionService.this.mAdapter.setConnectionProperties(id, connectionProperties);
        }

        @Override // android.telecom.Conference.Listener
        public void onVideoStateChanged(Conference c, int videoState) {
            String id = (String) ConnectionService.this.mIdByConference.get(c);
            Log.m139d(this, "onVideoStateChanged set video state %d", Integer.valueOf(videoState));
            ConnectionService.this.mAdapter.setVideoState(id, videoState);
        }

        @Override // android.telecom.Conference.Listener
        public void onVideoProviderChanged(Conference c, Connection.VideoProvider videoProvider) {
            String id = (String) ConnectionService.this.mIdByConference.get(c);
            Log.m139d(this, "onVideoProviderChanged: Connection: %s, VideoProvider: %s", c, videoProvider);
            ConnectionService.this.mAdapter.setVideoProvider(id, videoProvider);
        }

        @Override // android.telecom.Conference.Listener
        public void onStatusHintsChanged(Conference conference, StatusHints statusHints) {
            String id = (String) ConnectionService.this.mIdByConference.get(conference);
            if (id != null) {
                ConnectionService.this.mAdapter.setStatusHints(id, statusHints);
            }
        }

        @Override // android.telecom.Conference.Listener
        public void onExtrasChanged(Conference c, Bundle extras) {
            String id = (String) ConnectionService.this.mIdByConference.get(c);
            if (id != null) {
                ConnectionService.this.mAdapter.putExtras(id, extras);
            }
        }

        @Override // android.telecom.Conference.Listener
        public void onExtrasRemoved(Conference c, List<String> keys) {
            String id = (String) ConnectionService.this.mIdByConference.get(c);
            if (id != null) {
                ConnectionService.this.mAdapter.removeExtras(id, keys);
            }
        }

        @Override // android.telecom.Conference.Listener
        public void onConferenceStateChanged(Conference c, boolean isConference) {
            String id = (String) ConnectionService.this.mIdByConference.get(c);
            if (id != null) {
                ConnectionService.this.mAdapter.setConferenceState(id, isConference);
            }
        }

        @Override // android.telecom.Conference.Listener
        public void onCallDirectionChanged(Conference c, int direction) {
            String id = (String) ConnectionService.this.mIdByConference.get(c);
            if (id != null) {
                ConnectionService.this.mAdapter.setCallDirection(id, direction);
            }
        }

        @Override // android.telecom.Conference.Listener
        public void onAddressChanged(Conference c, Uri newAddress, int presentation) {
            String id = (String) ConnectionService.this.mIdByConference.get(c);
            if (id != null) {
                ConnectionService.this.mAdapter.setAddress(id, newAddress, presentation);
            }
        }

        @Override // android.telecom.Conference.Listener
        public void onCallerDisplayNameChanged(Conference c, String callerDisplayName, int presentation) {
            String id = (String) ConnectionService.this.mIdByConference.get(c);
            if (id != null) {
                ConnectionService.this.mAdapter.setCallerDisplayName(id, callerDisplayName, presentation);
            }
        }

        @Override // android.telecom.Conference.Listener
        public void onConnectionEvent(Conference c, String event, Bundle extras) {
            String id = (String) ConnectionService.this.mIdByConference.get(c);
            if (id != null) {
                ConnectionService.this.mAdapter.onConnectionEvent(id, event, extras);
            }
        }

        @Override // android.telecom.Conference.Listener
        public void onRingbackRequested(Conference c, boolean ringback) {
            String id = (String) ConnectionService.this.mIdByConference.get(c);
            Log.m139d(this, "Adapter conference onRingback %b", Boolean.valueOf(ringback));
            ConnectionService.this.mAdapter.setRingbackRequested(id, ringback);
        }
    };
    private final Connection.Listener mConnectionListener = new Connection.Listener() { // from class: android.telecom.ConnectionService.4
        @Override // android.telecom.Connection.Listener
        public void onStateChanged(Connection c, int state) {
            String id = (String) ConnectionService.this.mIdByConnection.get(c);
            Log.m139d(this, "Adapter set state %s %s", id, Connection.stateToString(state));
            switch (state) {
                case 1:
                case 6:
                default:
                    return;
                case 2:
                    ConnectionService.this.mAdapter.setRinging(id);
                    return;
                case 3:
                    ConnectionService.this.mAdapter.setDialing(id);
                    return;
                case 4:
                    ConnectionService.this.mAdapter.setActive(id);
                    return;
                case 5:
                    ConnectionService.this.mAdapter.setOnHold(id);
                    return;
                case 7:
                    ConnectionService.this.mAdapter.setPulling(id);
                    return;
            }
        }

        @Override // android.telecom.Connection.Listener
        public void onDisconnected(Connection c, DisconnectCause disconnectCause) {
            String id = (String) ConnectionService.this.mIdByConnection.get(c);
            Log.m139d(this, "Adapter set disconnected %s", disconnectCause);
            ConnectionService.this.mAdapter.setDisconnected(id, disconnectCause);
        }

        @Override // android.telecom.Connection.Listener
        public void onVideoStateChanged(Connection c, int videoState) {
            String id = (String) ConnectionService.this.mIdByConnection.get(c);
            Log.m139d(this, "Adapter set video state %d", Integer.valueOf(videoState));
            ConnectionService.this.mAdapter.setVideoState(id, videoState);
        }

        @Override // android.telecom.Connection.Listener
        public void onAddressChanged(Connection c, Uri address, int presentation) {
            String id = (String) ConnectionService.this.mIdByConnection.get(c);
            ConnectionService.this.mAdapter.setAddress(id, address, presentation);
        }

        @Override // android.telecom.Connection.Listener
        public void onCallerDisplayNameChanged(Connection c, String callerDisplayName, int presentation) {
            String id = (String) ConnectionService.this.mIdByConnection.get(c);
            ConnectionService.this.mAdapter.setCallerDisplayName(id, callerDisplayName, presentation);
        }

        @Override // android.telecom.Connection.Listener
        public void onDestroyed(Connection c) {
            ConnectionService.this.removeConnection(c);
        }

        @Override // android.telecom.Connection.Listener
        public void onPostDialWait(Connection c, String remaining) {
            String id = (String) ConnectionService.this.mIdByConnection.get(c);
            Log.m139d(this, "Adapter onPostDialWait %s, %s", c, remaining);
            ConnectionService.this.mAdapter.onPostDialWait(id, remaining);
        }

        @Override // android.telecom.Connection.Listener
        public void onPostDialChar(Connection c, char nextChar) {
            String id = (String) ConnectionService.this.mIdByConnection.get(c);
            Log.m139d(this, "Adapter onPostDialChar %s, %s", c, Character.valueOf(nextChar));
            ConnectionService.this.mAdapter.onPostDialChar(id, nextChar);
        }

        @Override // android.telecom.Connection.Listener
        public void onRingbackRequested(Connection c, boolean ringback) {
            String id = (String) ConnectionService.this.mIdByConnection.get(c);
            Log.m139d(this, "Adapter onRingback %b", Boolean.valueOf(ringback));
            ConnectionService.this.mAdapter.setRingbackRequested(id, ringback);
        }

        @Override // android.telecom.Connection.Listener
        public void onConnectionCapabilitiesChanged(Connection c, int capabilities) {
            String id = (String) ConnectionService.this.mIdByConnection.get(c);
            Log.m139d(this, "capabilities: parcelableconnection: %s", Connection.capabilitiesToString(capabilities));
            ConnectionService.this.mAdapter.setConnectionCapabilities(id, capabilities);
        }

        @Override // android.telecom.Connection.Listener
        public void onConnectionPropertiesChanged(Connection c, int properties) {
            String id = (String) ConnectionService.this.mIdByConnection.get(c);
            Log.m139d(this, "properties: parcelableconnection: %s", Connection.propertiesToString(properties));
            ConnectionService.this.mAdapter.setConnectionProperties(id, properties);
        }

        @Override // android.telecom.Connection.Listener
        public void onVideoProviderChanged(Connection c, Connection.VideoProvider videoProvider) {
            String id = (String) ConnectionService.this.mIdByConnection.get(c);
            Log.m139d(this, "onVideoProviderChanged: Connection: %s, VideoProvider: %s", c, videoProvider);
            ConnectionService.this.mAdapter.setVideoProvider(id, videoProvider);
        }

        @Override // android.telecom.Connection.Listener
        public void onAudioModeIsVoipChanged(Connection c, boolean isVoip) {
            String id = (String) ConnectionService.this.mIdByConnection.get(c);
            ConnectionService.this.mAdapter.setIsVoipAudioMode(id, isVoip);
        }

        @Override // android.telecom.Connection.Listener
        public void onStatusHintsChanged(Connection c, StatusHints statusHints) {
            String id = (String) ConnectionService.this.mIdByConnection.get(c);
            ConnectionService.this.mAdapter.setStatusHints(id, statusHints);
        }

        @Override // android.telecom.Connection.Listener
        public void onConferenceablesChanged(Connection connection, List<Conferenceable> conferenceables) {
            ConnectionService.this.mAdapter.setConferenceableConnections((String) ConnectionService.this.mIdByConnection.get(connection), ConnectionService.this.createIdList(conferenceables));
        }

        @Override // android.telecom.Connection.Listener
        public void onConferenceChanged(Connection connection, Conference conference) {
            String id = (String) ConnectionService.this.mIdByConnection.get(connection);
            if (id != null) {
                String conferenceId = null;
                if (conference != null) {
                    conferenceId = (String) ConnectionService.this.mIdByConference.get(conference);
                }
                ConnectionService.this.mAdapter.setIsConferenced(id, conferenceId);
            }
        }

        @Override // android.telecom.Connection.Listener
        public void onConferenceMergeFailed(Connection connection) {
            String id = (String) ConnectionService.this.mIdByConnection.get(connection);
            if (id != null) {
                ConnectionService.this.mAdapter.onConferenceMergeFailed(id);
            }
        }

        @Override // android.telecom.Connection.Listener
        public void onExtrasChanged(Connection c, Bundle extras) {
            String id = (String) ConnectionService.this.mIdByConnection.get(c);
            if (id != null) {
                ConnectionService.this.mAdapter.putExtras(id, extras);
            }
        }

        @Override // android.telecom.Connection.Listener
        public void onExtrasRemoved(Connection c, List<String> keys) {
            String id = (String) ConnectionService.this.mIdByConnection.get(c);
            if (id != null) {
                ConnectionService.this.mAdapter.removeExtras(id, keys);
            }
        }

        @Override // android.telecom.Connection.Listener
        public void onConnectionEvent(Connection connection, String event, Bundle extras) {
            String id = (String) ConnectionService.this.mIdByConnection.get(connection);
            if (id != null) {
                ConnectionService.this.mAdapter.onConnectionEvent(id, event, extras);
            }
        }

        @Override // android.telecom.Connection.Listener
        public void onAudioRouteChanged(Connection c, int audioRoute, String bluetoothAddress) {
            String id = (String) ConnectionService.this.mIdByConnection.get(c);
            if (id != null) {
                ConnectionService.this.mAdapter.setAudioRoute(id, audioRoute, bluetoothAddress);
            }
        }

        @Override // android.telecom.Connection.Listener
        public void onRttInitiationSuccess(Connection c) {
            String id = (String) ConnectionService.this.mIdByConnection.get(c);
            if (id != null) {
                ConnectionService.this.mAdapter.onRttInitiationSuccess(id);
            }
        }

        @Override // android.telecom.Connection.Listener
        public void onRttInitiationFailure(Connection c, int reason) {
            String id = (String) ConnectionService.this.mIdByConnection.get(c);
            if (id != null) {
                ConnectionService.this.mAdapter.onRttInitiationFailure(id, reason);
            }
        }

        @Override // android.telecom.Connection.Listener
        public void onRttSessionRemotelyTerminated(Connection c) {
            String id = (String) ConnectionService.this.mIdByConnection.get(c);
            if (id != null) {
                ConnectionService.this.mAdapter.onRttSessionRemotelyTerminated(id);
            }
        }

        @Override // android.telecom.Connection.Listener
        public void onRemoteRttRequest(Connection c) {
            String id = (String) ConnectionService.this.mIdByConnection.get(c);
            if (id != null) {
                ConnectionService.this.mAdapter.onRemoteRttRequest(id);
            }
        }

        @Override // android.telecom.Connection.Listener
        public void onPhoneAccountChanged(Connection c, PhoneAccountHandle pHandle) {
            String id = (String) ConnectionService.this.mIdByConnection.get(c);
            if (id != null) {
                ConnectionService.this.mAdapter.onPhoneAccountChanged(id, pHandle);
            }
        }

        @Override // android.telecom.Connection.Listener
        public void onConnectionTimeReset(Connection c) {
            String id = (String) ConnectionService.this.mIdByConnection.get(c);
            if (id != null) {
                ConnectionService.this.mAdapter.resetConnectionTime(id);
            }
        }

        @Override // android.telecom.Connection.Listener
        public void onEndpointChanged(Connection c, CallEndpoint endpoint, Executor executor, OutcomeReceiver<Void, CallEndpointException> callback) {
            String id = (String) ConnectionService.this.mIdByConnection.get(c);
            if (id != null) {
                ConnectionService.this.mAdapter.requestCallEndpointChange(id, endpoint, executor, callback);
            }
        }

        @Override // android.telecom.Connection.Listener
        public void onQueryLocation(Connection c, long timeoutMillis, String provider, Executor executor, OutcomeReceiver<Location, QueryLocationException> callback) {
            String id = (String) ConnectionService.this.mIdByConnection.get(c);
            if (id != null) {
                ConnectionService.this.mAdapter.queryLocation(id, timeoutMillis, provider, executor, callback);
            }
        }
    };

    @Override // android.app.Service
    public final IBinder onBind(Intent intent) {
        onBindClient(intent);
        return this.mBinder;
    }

    @Override // android.app.Service
    public boolean onUnbind(Intent intent) {
        endAllConnections();
        return super.onUnbind(intent);
    }

    public void onBindClient(Intent intent) {
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void createConference(PhoneAccountHandle callManagerAccount, String callId, ConnectionRequest request, boolean isIncoming, boolean isUnknown) {
        Conference conference = isIncoming ? onCreateIncomingConference(callManagerAccount, request) : onCreateOutgoingConference(callManagerAccount, request);
        Log.m139d(this, "createConference, conference: %s", conference);
        if (conference == null) {
            Log.m135i(this, "createConference, implementation returned null conference.", new Object[0]);
            conference = Conference.createFailedConference(new DisconnectCause(1, "IMPL_RETURNED_NULL_CONFERENCE"), request.getAccountHandle());
        }
        Bundle extras = request.getExtras();
        Bundle newExtras = new Bundle();
        newExtras.putString(Connection.EXTRA_ORIGINAL_CONNECTION_ID, callId);
        if (extras != null && extras.containsKey(Connection.EXTRA_REMOTE_CONNECTION_ORIGINATING_PACKAGE_NAME)) {
            newExtras.putString(Connection.EXTRA_REMOTE_CONNECTION_ORIGINATING_PACKAGE_NAME, extras.getString(Connection.EXTRA_REMOTE_CONNECTION_ORIGINATING_PACKAGE_NAME));
            newExtras.putParcelable(Connection.EXTRA_REMOTE_PHONE_ACCOUNT_HANDLE, request.getAccountHandle());
        }
        conference.putExtras(newExtras);
        this.mConferenceById.put(callId, conference);
        this.mIdByConference.put(conference, callId);
        conference.addListener(this.mConferenceListener);
        ParcelableConference parcelableConference = new ParcelableConference.Builder(request.getAccountHandle(), conference.getState()).setConnectionCapabilities(conference.getConnectionCapabilities()).setConnectionProperties(conference.getConnectionProperties()).setVideoAttributes(conference.getVideoProvider() == null ? null : conference.getVideoProvider().getInterface(), conference.getVideoState()).setConnectTimeMillis(conference.getConnectTimeMillis(), conference.getConnectionStartElapsedRealtimeMillis()).setStatusHints(conference.getStatusHints()).setExtras(conference.getExtras()).setAddress(conference.getAddress(), conference.getAddressPresentation()).setCallerDisplayName(conference.getCallerDisplayName(), conference.getCallerDisplayNamePresentation()).setDisconnectCause(conference.getDisconnectCause()).setRingbackRequested(conference.isRingbackRequested()).build();
        if (conference.getState() != 6) {
            conference.setTelecomCallId(callId);
            this.mAdapter.setVideoProvider(callId, conference.getVideoProvider());
            this.mAdapter.setVideoState(callId, conference.getVideoState());
            onConferenceAdded(conference);
        }
        Log.m139d(this, "createConference, calling handleCreateConferenceSuccessful %s", callId);
        this.mAdapter.handleCreateConferenceComplete(callId, request, parcelableConference);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void createConnection(PhoneAccountHandle callManagerAccount, String callId, ConnectionRequest request, boolean isIncoming, boolean isUnknown) {
        Connection onCreateIncomingConnection;
        Connection connection;
        PhoneAccountHandle fromPhoneAccountHandle;
        boolean isLegacyHandover = request.getExtras() != null && request.getExtras().getBoolean("android.telecom.extra.IS_HANDOVER", false);
        boolean isHandover = request.getExtras() != null && request.getExtras().getBoolean(TelecomManager.EXTRA_IS_HANDOVER_CONNECTION, false);
        boolean addSelfManaged = request.getExtras() != null && request.getExtras().getBoolean(PhoneAccount.EXTRA_ADD_SELF_MANAGED_CALLS_TO_INCALLSERVICE, true);
        Log.m135i(this, "createConnection, callManagerAccount: %s, callId: %s, request: %s, isIncoming: %b, isUnknown: %b, isLegacyHandover: %b, isHandover: %b,  addSelfManaged: %b", callManagerAccount, callId, request, Boolean.valueOf(isIncoming), Boolean.valueOf(isUnknown), Boolean.valueOf(isLegacyHandover), Boolean.valueOf(isHandover), Boolean.valueOf(addSelfManaged));
        if (isHandover) {
            if (request.getExtras() != null) {
                fromPhoneAccountHandle = (PhoneAccountHandle) request.getExtras().getParcelable(TelecomManager.EXTRA_HANDOVER_FROM_PHONE_ACCOUNT, PhoneAccountHandle.class);
            } else {
                fromPhoneAccountHandle = null;
            }
            if (!isIncoming) {
                connection = onCreateOutgoingHandoverConnection(fromPhoneAccountHandle, request);
            } else {
                connection = onCreateIncomingHandoverConnection(fromPhoneAccountHandle, request);
            }
        } else {
            if (isUnknown) {
                onCreateIncomingConnection = onCreateUnknownConnection(callManagerAccount, request);
            } else {
                onCreateIncomingConnection = isIncoming ? onCreateIncomingConnection(callManagerAccount, request) : onCreateOutgoingConnection(callManagerAccount, request);
            }
            connection = onCreateIncomingConnection;
        }
        Log.m139d(this, "createConnection, connection: %s", connection);
        if (connection == null) {
            Log.m135i(this, "createConnection, implementation returned null connection.", new Object[0]);
            connection = Connection.createFailedConnection(new DisconnectCause(1, "IMPL_RETURNED_NULL_CONNECTION"));
        } else {
            try {
                Bundle extras = request.getExtras();
                if (extras != null && extras.containsKey(Connection.EXTRA_REMOTE_CONNECTION_ORIGINATING_PACKAGE_NAME)) {
                    Bundle newExtras = new Bundle();
                    newExtras.putString(Connection.EXTRA_REMOTE_CONNECTION_ORIGINATING_PACKAGE_NAME, extras.getString(Connection.EXTRA_REMOTE_CONNECTION_ORIGINATING_PACKAGE_NAME));
                    newExtras.putParcelable(Connection.EXTRA_REMOTE_PHONE_ACCOUNT_HANDLE, request.getAccountHandle());
                    connection.putExtras(newExtras);
                }
            } catch (UnsupportedOperationException e) {
            }
        }
        boolean isSelfManaged = (connection.getConnectionProperties() & 128) == 128;
        if (isSelfManaged) {
            connection.setAudioModeIsVoip(true);
        }
        connection.setTelecomCallId(callId);
        PhoneAccountHandle phoneAccountHandle = connection.getPhoneAccountHandle() == null ? request.getAccountHandle() : connection.getPhoneAccountHandle();
        if (connection.getState() != 6) {
            addConnection(phoneAccountHandle, callId, connection);
        }
        Uri address = connection.getAddress();
        String number = address == null ? "null" : address.getSchemeSpecificPart();
        Log.m133v(this, "createConnection, number: %s, state: %s, capabilities: %s, properties: %s", Connection.toLogSafePhoneNumber(number), Connection.stateToString(connection.getState()), Connection.capabilitiesToString(connection.getConnectionCapabilities()), Connection.propertiesToString(connection.getConnectionProperties()));
        Log.m139d(this, "createConnection, calling handleCreateConnectionSuccessful %s", callId);
        this.mAdapter.handleCreateConnectionComplete(callId, request, new ParcelableConnection(phoneAccountHandle, connection.getState(), connection.getConnectionCapabilities(), connection.getConnectionProperties(), connection.getSupportedAudioRoutes(), connection.getAddress(), connection.getAddressPresentation(), connection.getCallerDisplayName(), connection.getCallerDisplayNamePresentation(), connection.getVideoProvider() != null ? connection.getVideoProvider().getInterface() : null, connection.getVideoState(), connection.isRingbackRequested(), connection.getAudioModeIsVoip(), connection.getConnectTimeMillis(), connection.getConnectionStartElapsedRealtimeMillis(), connection.getStatusHints(), connection.getDisconnectCause(), createIdList(connection.getConferenceables()), connection.getExtras(), connection.getCallerNumberVerificationStatus()));
        if (isIncoming && request.shouldShowIncomingCallUi() && isSelfManaged) {
            connection.onShowIncomingCallUi();
        }
        if (isUnknown) {
            triggerConferenceRecalculate();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void createConnectionFailed(PhoneAccountHandle callManagerAccount, String callId, ConnectionRequest request, boolean isIncoming) {
        Log.m135i(this, "createConnectionFailed %s", callId);
        if (isIncoming) {
            onCreateIncomingConnectionFailed(callManagerAccount, request);
        } else {
            onCreateOutgoingConnectionFailed(callManagerAccount, request);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void createConferenceFailed(PhoneAccountHandle callManagerAccount, String callId, ConnectionRequest request, boolean isIncoming) {
        Log.m135i(this, "createConferenceFailed %s", callId);
        if (isIncoming) {
            onCreateIncomingConferenceFailed(callManagerAccount, request);
        } else {
            onCreateOutgoingConferenceFailed(callManagerAccount, request);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handoverFailed(String callId, ConnectionRequest request, int reason) {
        Log.m135i(this, "handoverFailed %s", callId);
        onHandoverFailed(request, reason);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyCreateConnectionComplete(String callId) {
        Log.m135i(this, "notifyCreateConnectionComplete %s", callId);
        if (callId == null) {
            Log.m131w(this, "notifyCreateConnectionComplete: callId is null.", new Object[0]);
        } else {
            onCreateConnectionComplete(findConnectionForAction(callId, "notifyCreateConnectionComplete"));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyCreateConferenceComplete(String callId) {
        Log.m135i(this, "notifyCreateConferenceComplete %s", callId);
        if (callId == null) {
            Log.m131w(this, "notifyCreateConferenceComplete: callId is null.", new Object[0]);
        } else {
            onCreateConferenceComplete(findConferenceForAction(callId, "notifyCreateConferenceComplete"));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void abort(String callId) {
        Log.m135i(this, "abort %s", callId);
        findConnectionForAction(callId, "abort").onAbort();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void answerVideo(String callId, int videoState) {
        Log.m135i(this, "answerVideo %s", callId);
        if (this.mConnectionById.containsKey(callId)) {
            findConnectionForAction(callId, "answer").onAnswer(videoState);
        } else {
            findConferenceForAction(callId, "answer").onAnswer(videoState);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void answer(String callId) {
        Log.m135i(this, "answer %s", callId);
        if (this.mConnectionById.containsKey(callId)) {
            findConnectionForAction(callId, "answer").onAnswer();
        } else {
            findConferenceForAction(callId, "answer").onAnswer();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void deflect(String callId, Uri address) {
        Log.m135i(this, "deflect %s", callId);
        findConnectionForAction(callId, "deflect").onDeflect(address);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void reject(String callId) {
        Log.m135i(this, "reject %s", callId);
        if (this.mConnectionById.containsKey(callId)) {
            findConnectionForAction(callId, "reject").onReject();
        } else {
            findConferenceForAction(callId, "reject").onReject();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void reject(String callId, String rejectWithMessage) {
        Log.m135i(this, "reject %s with message", callId);
        findConnectionForAction(callId, "reject").onReject(rejectWithMessage);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void reject(String callId, int rejectReason) {
        Log.m135i(this, "reject %s with reason %d", callId, Integer.valueOf(rejectReason));
        findConnectionForAction(callId, "reject").onReject(rejectReason);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void transfer(String callId, Uri number, boolean isConfirmationRequired) {
        Log.m135i(this, "transfer %s", callId);
        findConnectionForAction(callId, "transfer").onTransfer(number, isConfirmationRequired);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void consultativeTransfer(String callId, String otherCallId) {
        Log.m135i(this, "consultativeTransfer %s", callId);
        Connection connection1 = findConnectionForAction(callId, "consultativeTransfer");
        Connection connection2 = findConnectionForAction(otherCallId, " consultativeTransfer");
        connection1.onTransfer(connection2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void silence(String callId) {
        Log.m135i(this, "silence %s", callId);
        findConnectionForAction(callId, "silence").onSilence();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void disconnect(String callId) {
        Log.m135i(this, "disconnect %s", callId);
        if (this.mConnectionById.containsKey(callId)) {
            findConnectionForAction(callId, MediaMetrics.Value.DISCONNECT).onDisconnect();
        } else {
            findConferenceForAction(callId, MediaMetrics.Value.DISCONNECT).onDisconnect();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void hold(String callId) {
        Log.m135i(this, "hold %s", callId);
        if (this.mConnectionById.containsKey(callId)) {
            findConnectionForAction(callId, "hold").onHold();
        } else {
            findConferenceForAction(callId, "hold").onHold();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void unhold(String callId) {
        Log.m135i(this, "unhold %s", callId);
        if (this.mConnectionById.containsKey(callId)) {
            findConnectionForAction(callId, "unhold").onUnhold();
        } else {
            findConferenceForAction(callId, "unhold").onUnhold();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onCallAudioStateChanged(String callId, CallAudioState callAudioState) {
        Log.m135i(this, "onAudioStateChanged %s %s", callId, callAudioState);
        if (this.mConnectionById.containsKey(callId)) {
            findConnectionForAction(callId, "onCallAudioStateChanged").setCallAudioState(callAudioState);
        } else {
            findConferenceForAction(callId, "onCallAudioStateChanged").setCallAudioState(callAudioState);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onCallEndpointChanged(String callId, CallEndpoint callEndpoint) {
        Log.m135i(this, "onCallEndpointChanged %s %s", callId, callEndpoint);
        if (this.mConnectionById.containsKey(callId)) {
            findConnectionForAction(callId, "onCallEndpointChanged").setCallEndpoint(callEndpoint);
        } else {
            findConferenceForAction(callId, "onCallEndpointChanged").setCallEndpoint(callEndpoint);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onAvailableCallEndpointsChanged(String callId, List<CallEndpoint> availableCallEndpoints) {
        Log.m135i(this, "onAvailableCallEndpointsChanged %s", callId);
        if (this.mConnectionById.containsKey(callId)) {
            findConnectionForAction(callId, "onAvailableCallEndpointsChanged").setAvailableCallEndpoints(availableCallEndpoints);
        } else {
            findConferenceForAction(callId, "onAvailableCallEndpointsChanged").setAvailableCallEndpoints(availableCallEndpoints);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onMuteStateChanged(String callId, boolean isMuted) {
        Log.m135i(this, "onMuteStateChanged %s %s", callId, Boolean.valueOf(isMuted));
        if (this.mConnectionById.containsKey(callId)) {
            findConnectionForAction(callId, "onMuteStateChanged").setMuteState(isMuted);
        } else {
            findConferenceForAction(callId, "onMuteStateChanged").setMuteState(isMuted);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onUsingAlternativeUi(String callId, boolean isUsingAlternativeUi) {
        Log.m135i(this, "onUsingAlternativeUi %s %s", callId, Boolean.valueOf(isUsingAlternativeUi));
        if (this.mConnectionById.containsKey(callId)) {
            findConnectionForAction(callId, "onUsingAlternativeUi").onUsingAlternativeUi(isUsingAlternativeUi);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onTrackedByNonUiService(String callId, boolean isTracked) {
        Log.m135i(this, "onTrackedByNonUiService %s %s", callId, Boolean.valueOf(isTracked));
        if (this.mConnectionById.containsKey(callId)) {
            findConnectionForAction(callId, "onTrackedByNonUiService").onTrackedByNonUiService(isTracked);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void playDtmfTone(String callId, char digit) {
        Log.m135i(this, "playDtmfTone %s %s", callId, Log.pii(Character.valueOf(digit)));
        if (this.mConnectionById.containsKey(callId)) {
            findConnectionForAction(callId, "playDtmfTone").onPlayDtmfTone(digit);
        } else {
            findConferenceForAction(callId, "playDtmfTone").onPlayDtmfTone(digit);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void stopDtmfTone(String callId) {
        Log.m135i(this, "stopDtmfTone %s", callId);
        if (this.mConnectionById.containsKey(callId)) {
            findConnectionForAction(callId, "stopDtmfTone").onStopDtmfTone();
        } else {
            findConferenceForAction(callId, "stopDtmfTone").onStopDtmfTone();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void conference(String callId1, String callId2) {
        Log.m135i(this, "conference %s, %s", callId1, callId2);
        Connection connection2 = findConnectionForAction(callId2, ImsCallProfile.EXTRA_CONFERENCE_DEPRECATED);
        Conference conference2 = getNullConference();
        if (connection2 == getNullConnection() && (conference2 = findConferenceForAction(callId2, ImsCallProfile.EXTRA_CONFERENCE_DEPRECATED)) == getNullConference()) {
            Log.m131w(this, "Connection2 or Conference2 missing in conference request %s.", callId2);
            return;
        }
        Connection connection1 = findConnectionForAction(callId1, ImsCallProfile.EXTRA_CONFERENCE_DEPRECATED);
        if (connection1 == getNullConnection()) {
            Conference conference1 = findConferenceForAction(callId1, "addConnection");
            if (conference1 == getNullConference()) {
                Log.m131w(this, "Connection1 or Conference1 missing in conference request %s.", callId1);
            } else if (connection2 != getNullConnection()) {
                conference1.onMerge(connection2);
            } else {
                Log.wtf(this, "There can only be one conference and an attempt was made to merge two conferences.", new Object[0]);
            }
        } else if (conference2 != getNullConference()) {
            conference2.onMerge(connection1);
        } else {
            onConference(connection1, connection2);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void splitFromConference(String callId) {
        Log.m135i(this, "splitFromConference(%s)", callId);
        Connection connection = findConnectionForAction(callId, "splitFromConference");
        if (connection == getNullConnection()) {
            Log.m131w(this, "Connection missing in conference request %s.", callId);
            return;
        }
        Conference conference = connection.getConference();
        if (conference != null) {
            conference.onSeparate(connection);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void mergeConference(String callId) {
        Log.m135i(this, "mergeConference(%s)", callId);
        Conference conference = findConferenceForAction(callId, "mergeConference");
        if (conference != null) {
            conference.onMerge();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void swapConference(String callId) {
        Log.m135i(this, "swapConference(%s)", callId);
        Conference conference = findConferenceForAction(callId, "swapConference");
        if (conference != null) {
            conference.onSwap();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void addConferenceParticipants(String callId, List<Uri> participants) {
        Log.m135i(this, "addConferenceParticipants(%s)", callId);
        if (this.mConnectionById.containsKey(callId)) {
            findConnectionForAction(callId, "addConferenceParticipants").onAddConferenceParticipants(participants);
        } else {
            findConferenceForAction(callId, "addConferenceParticipants").onAddConferenceParticipants(participants);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void pullExternalCall(String callId) {
        Log.m135i(this, "pullExternalCall(%s)", callId);
        Connection connection = findConnectionForAction(callId, "pullExternalCall");
        if (connection != null) {
            connection.onPullExternalCall();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendCallEvent(String callId, String event, Bundle extras) {
        Log.m135i(this, "sendCallEvent(%s, %s)", callId, event);
        Connection connection = findConnectionForAction(callId, "sendCallEvent");
        if (connection != null) {
            connection.onCallEvent(event, extras);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onCallFilteringCompleted(String callId, Connection.CallFilteringCompletionInfo callFilteringCompletionInfo) {
        Log.m135i(this, "onCallFilteringCompleted(%s, %s)", callId, callFilteringCompletionInfo);
        Connection connection = findConnectionForAction(callId, "onCallFilteringCompleted");
        if (connection != null) {
            connection.onCallFilteringCompleted(callFilteringCompletionInfo);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyHandoverComplete(String callId) {
        Log.m135i(this, "notifyHandoverComplete(%s)", callId);
        Connection connection = findConnectionForAction(callId, "notifyHandoverComplete");
        if (connection != null) {
            connection.onHandoverComplete();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleExtrasChanged(String callId, Bundle extras) {
        Log.m135i(this, "handleExtrasChanged(%s, %s)", callId, extras);
        if (this.mConnectionById.containsKey(callId)) {
            findConnectionForAction(callId, "handleExtrasChanged").handleExtrasChanged(extras);
        } else if (this.mConferenceById.containsKey(callId)) {
            findConferenceForAction(callId, "handleExtrasChanged").handleExtrasChanged(extras);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startRtt(String callId, Connection.RttTextStream rttTextStream) {
        Log.m135i(this, "startRtt(%s)", callId);
        if (this.mConnectionById.containsKey(callId)) {
            findConnectionForAction(callId, "startRtt").onStartRtt(rttTextStream);
        } else if (this.mConferenceById.containsKey(callId)) {
            Log.m131w(this, "startRtt called on a conference.", new Object[0]);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void stopRtt(String callId) {
        Log.m135i(this, "stopRtt(%s)", callId);
        if (this.mConnectionById.containsKey(callId)) {
            findConnectionForAction(callId, "stopRtt").onStopRtt();
        } else if (this.mConferenceById.containsKey(callId)) {
            Log.m131w(this, "stopRtt called on a conference.", new Object[0]);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleRttUpgradeResponse(String callId, Connection.RttTextStream rttTextStream) {
        Object[] objArr = new Object[2];
        objArr[0] = callId;
        objArr[1] = Boolean.valueOf(rttTextStream == null);
        Log.m135i(this, "handleRttUpgradeResponse(%s, %s)", objArr);
        if (this.mConnectionById.containsKey(callId)) {
            findConnectionForAction(callId, "handleRttUpgradeResponse").handleRttUpgradeResponse(rttTextStream);
        } else if (this.mConferenceById.containsKey(callId)) {
            Log.m131w(this, "handleRttUpgradeResponse called on a conference.", new Object[0]);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onPostDialContinue(String callId, boolean proceed) {
        Log.m135i(this, "onPostDialContinue(%s)", callId);
        findConnectionForAction(callId, "stopDtmfTone").onPostDialContinue(proceed);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onAdapterAttached() {
        if (this.mAreAccountsInitialized) {
            return;
        }
        String callingPackage = getOpPackageName();
        this.mAdapter.queryRemoteConnectionServices(new RemoteServiceCallback.Stub() { // from class: android.telecom.ConnectionService.5
            @Override // com.android.internal.telecom.RemoteServiceCallback
            public void onResult(final List<ComponentName> componentNames, final List<IBinder> services) {
                ConnectionService.this.mHandler.post(new Runnable("oAA.qRCS.oR", null) { // from class: android.telecom.ConnectionService.5.1
                    @Override // android.telecom.Logging.Runnable
                    public void loggedRun() {
                        for (int i = 0; i < componentNames.size() && i < services.size(); i++) {
                            ConnectionService.this.mRemoteConnectionManager.addConnectionService((ComponentName) componentNames.get(i), IConnectionService.Stub.asInterface((IBinder) services.get(i)));
                        }
                        ConnectionService.this.onAccountsInitialized();
                        Log.m139d(this, "remote connection services found: " + services, new Object[0]);
                    }
                }.prepare());
            }

            @Override // com.android.internal.telecom.RemoteServiceCallback
            public void onError() {
                ConnectionService.this.mHandler.post(new Runnable("oAA.qRCS.oE", null) { // from class: android.telecom.ConnectionService.5.2
                    @Override // android.telecom.Logging.Runnable
                    public void loggedRun() {
                        ConnectionService.this.mAreAccountsInitialized = true;
                    }
                }.prepare());
            }
        }, callingPackage);
    }

    public final RemoteConnection createRemoteIncomingConnection(PhoneAccountHandle connectionManagerPhoneAccount, ConnectionRequest request) {
        return this.mRemoteConnectionManager.createRemoteConnection(connectionManagerPhoneAccount, request, true);
    }

    public final RemoteConnection createRemoteOutgoingConnection(PhoneAccountHandle connectionManagerPhoneAccount, ConnectionRequest request) {
        return this.mRemoteConnectionManager.createRemoteConnection(connectionManagerPhoneAccount, request, false);
    }

    public final RemoteConference createRemoteIncomingConference(PhoneAccountHandle connectionManagerPhoneAccount, ConnectionRequest request) {
        return this.mRemoteConnectionManager.createRemoteConference(connectionManagerPhoneAccount, request, true);
    }

    public final RemoteConference createRemoteOutgoingConference(PhoneAccountHandle connectionManagerPhoneAccount, ConnectionRequest request) {
        return this.mRemoteConnectionManager.createRemoteConference(connectionManagerPhoneAccount, request, false);
    }

    public final void conferenceRemoteConnections(RemoteConnection remoteConnection1, RemoteConnection remoteConnection2) {
        this.mRemoteConnectionManager.conferenceRemoteConnections(remoteConnection1, remoteConnection2);
    }

    public final void addConference(Conference conference) {
        Log.m139d(this, "addConference: conference=%s", conference);
        String id = addConferenceInternal(conference);
        if (id != null) {
            List<String> connectionIds = new ArrayList<>(2);
            for (Connection connection : conference.getConnections()) {
                if (this.mIdByConnection.containsKey(connection)) {
                    connectionIds.add(this.mIdByConnection.get(connection));
                }
            }
            conference.setTelecomCallId(id);
            ParcelableConference parcelableConference = new ParcelableConference.Builder(conference.getPhoneAccountHandle(), conference.getState()).setConnectionCapabilities(conference.getConnectionCapabilities()).setConnectionProperties(conference.getConnectionProperties()).setConnectionIds(connectionIds).setVideoAttributes(conference.getVideoProvider() == null ? null : conference.getVideoProvider().getInterface(), conference.getVideoState()).setConnectTimeMillis(conference.getConnectTimeMillis(), conference.getConnectionStartElapsedRealtimeMillis()).setStatusHints(conference.getStatusHints()).setExtras(conference.getExtras()).setAddress(conference.getAddress(), conference.getAddressPresentation()).setCallerDisplayName(conference.getCallerDisplayName(), conference.getCallerDisplayNamePresentation()).setDisconnectCause(conference.getDisconnectCause()).setRingbackRequested(conference.isRingbackRequested()).setCallDirection(conference.getCallDirection()).build();
            this.mAdapter.addConferenceCall(id, parcelableConference);
            this.mAdapter.setVideoProvider(id, conference.getVideoProvider());
            this.mAdapter.setVideoState(id, conference.getVideoState());
            if (!conference.isMultiparty()) {
                this.mAdapter.setConferenceState(id, conference.isMultiparty());
            }
            for (Connection connection2 : conference.getConnections()) {
                String connectionId = this.mIdByConnection.get(connection2);
                if (connectionId != null) {
                    this.mAdapter.setIsConferenced(connectionId, id);
                }
            }
            onConferenceAdded(conference);
        }
    }

    public final void addExistingConnection(PhoneAccountHandle phoneAccountHandle, Connection connection) {
        addExistingConnection(phoneAccountHandle, connection, null);
    }

    public final void connectionServiceFocusReleased() {
        this.mAdapter.onConnectionServiceFocusReleased();
    }

    @SystemApi
    public final void addExistingConnection(PhoneAccountHandle phoneAccountHandle, Connection connection, Conference conference) {
        String conferenceId;
        String id = addExistingConnectionInternal(phoneAccountHandle, connection);
        if (id != null) {
            List<String> emptyList = new ArrayList<>(0);
            if (conference == null) {
                conferenceId = null;
            } else {
                String conferenceId2 = this.mIdByConference.get(conference);
                conferenceId = conferenceId2;
            }
            ParcelableConnection parcelableConnection = new ParcelableConnection(phoneAccountHandle, connection.getState(), connection.getConnectionCapabilities(), connection.getConnectionProperties(), connection.getSupportedAudioRoutes(), connection.getAddress(), connection.getAddressPresentation(), connection.getCallerDisplayName(), connection.getCallerDisplayNamePresentation(), connection.getVideoProvider() == null ? null : connection.getVideoProvider().getInterface(), connection.getVideoState(), connection.isRingbackRequested(), connection.getAudioModeIsVoip(), connection.getConnectTimeMillis(), connection.getConnectionStartElapsedRealtimeMillis(), connection.getStatusHints(), connection.getDisconnectCause(), emptyList, connection.getExtras(), conferenceId, connection.getCallDirection(), 0);
            this.mAdapter.addExistingConnection(id, parcelableConnection);
        }
    }

    public final Collection<Connection> getAllConnections() {
        return this.mConnectionById.values();
    }

    public final Collection<Conference> getAllConferences() {
        return this.mConferenceById.values();
    }

    public Connection onCreateIncomingConnection(PhoneAccountHandle connectionManagerPhoneAccount, ConnectionRequest request) {
        return null;
    }

    public Conference onCreateIncomingConference(PhoneAccountHandle connectionManagerPhoneAccount, ConnectionRequest request) {
        return null;
    }

    public void onCreateConnectionComplete(Connection connection) {
    }

    public void onCreateConferenceComplete(Conference conference) {
    }

    public void onCreateIncomingConnectionFailed(PhoneAccountHandle connectionManagerPhoneAccount, ConnectionRequest request) {
    }

    public void onCreateOutgoingConnectionFailed(PhoneAccountHandle connectionManagerPhoneAccount, ConnectionRequest request) {
    }

    public void onCreateIncomingConferenceFailed(PhoneAccountHandle connectionManagerPhoneAccount, ConnectionRequest request) {
    }

    public void onCreateOutgoingConferenceFailed(PhoneAccountHandle connectionManagerPhoneAccount, ConnectionRequest request) {
    }

    public void triggerConferenceRecalculate() {
    }

    public Connection onCreateOutgoingConnection(PhoneAccountHandle connectionManagerPhoneAccount, ConnectionRequest request) {
        return null;
    }

    public Conference onCreateOutgoingConference(PhoneAccountHandle connectionManagerPhoneAccount, ConnectionRequest request) {
        return null;
    }

    public Connection onCreateOutgoingHandoverConnection(PhoneAccountHandle fromPhoneAccountHandle, ConnectionRequest request) {
        return null;
    }

    public Connection onCreateIncomingHandoverConnection(PhoneAccountHandle fromPhoneAccountHandle, ConnectionRequest request) {
        return null;
    }

    public void onHandoverFailed(ConnectionRequest request, int error) {
    }

    @SystemApi(client = SystemApi.Client.MODULE_LIBRARIES)
    public Connection onCreateUnknownConnection(PhoneAccountHandle connectionManagerPhoneAccount, ConnectionRequest request) {
        return null;
    }

    public void onConference(Connection connection1, Connection connection2) {
    }

    public void onConnectionAdded(Connection connection) {
    }

    public void onConnectionRemoved(Connection connection) {
    }

    public void onConferenceAdded(Conference conference) {
    }

    public void onConferenceRemoved(Conference conference) {
    }

    public void onRemoteConferenceAdded(RemoteConference conference) {
    }

    public void onRemoteExistingConnectionAdded(RemoteConnection connection) {
    }

    public void onConnectionServiceFocusLost() {
    }

    public void onConnectionServiceFocusGained() {
    }

    public boolean containsConference(Conference conference) {
        return this.mIdByConference.containsKey(conference);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void addRemoteConference(RemoteConference remoteConference) {
        onRemoteConferenceAdded(remoteConference);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void addRemoteExistingConnection(RemoteConnection remoteConnection) {
        onRemoteExistingConnectionAdded(remoteConnection);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onAccountsInitialized() {
        this.mAreAccountsInitialized = true;
        for (Runnable r : this.mPreInitializationConnectionRequests) {
            r.run();
        }
        this.mPreInitializationConnectionRequests.clear();
    }

    private String addExistingConnectionInternal(PhoneAccountHandle handle, Connection connection) {
        String id;
        if (connection.getExtras() != null && connection.getExtras().containsKey(Connection.EXTRA_ORIGINAL_CONNECTION_ID)) {
            id = connection.getExtras().getString(Connection.EXTRA_ORIGINAL_CONNECTION_ID);
            Log.m139d(this, "addExistingConnectionInternal - conn %s reusing original id %s", connection.getTelecomCallId(), id);
        } else if (handle == null) {
            id = UUID.randomUUID().toString();
        } else {
            id = handle.getComponentName().getClassName() + "@" + getNextCallId();
        }
        addConnection(handle, id, connection);
        return id;
    }

    private void addConnection(PhoneAccountHandle handle, String callId, Connection connection) {
        connection.setTelecomCallId(callId);
        this.mConnectionById.put(callId, connection);
        this.mIdByConnection.put(connection, callId);
        connection.addConnectionListener(this.mConnectionListener);
        connection.setConnectionService(this);
        connection.setPhoneAccountHandle(handle);
        onConnectionAdded(connection);
    }

    protected void removeConnection(Connection connection) {
        connection.unsetConnectionService(this);
        connection.removeConnectionListener(this.mConnectionListener);
        String id = this.mIdByConnection.get(connection);
        if (id != null) {
            this.mConnectionById.remove(id);
            this.mIdByConnection.remove(connection);
            this.mAdapter.removeCall(id);
            onConnectionRemoved(connection);
        }
    }

    private String addConferenceInternal(Conference conference) {
        String originalId = null;
        if (conference.getExtras() != null && conference.getExtras().containsKey(Connection.EXTRA_ORIGINAL_CONNECTION_ID)) {
            originalId = conference.getExtras().getString(Connection.EXTRA_ORIGINAL_CONNECTION_ID);
            Log.m139d(this, "addConferenceInternal: conf %s reusing original id %s", conference.getTelecomCallId(), originalId);
        }
        if (this.mIdByConference.containsKey(conference)) {
            Log.m131w(this, "Re-adding an existing conference: %s.", conference);
            return null;
        } else if (conference != null) {
            String id = originalId == null ? UUID.randomUUID().toString() : originalId;
            this.mConferenceById.put(id, conference);
            this.mIdByConference.put(conference, id);
            conference.addListener(this.mConferenceListener);
            return id;
        } else {
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void removeConference(Conference conference) {
        if (this.mIdByConference.containsKey(conference)) {
            conference.removeListener(this.mConferenceListener);
            String id = this.mIdByConference.get(conference);
            this.mConferenceById.remove(id);
            this.mIdByConference.remove(conference);
            this.mAdapter.removeCall(id);
            onConferenceRemoved(conference);
        }
    }

    private Connection findConnectionForAction(String callId, String action) {
        if (callId != null && this.mConnectionById.containsKey(callId)) {
            return this.mConnectionById.get(callId);
        }
        Log.m131w(this, "%s - Cannot find Connection %s", action, callId);
        return getNullConnection();
    }

    static synchronized Connection getNullConnection() {
        Connection connection;
        synchronized (ConnectionService.class) {
            if (sNullConnection == null) {
                sNullConnection = new Connection() { // from class: android.telecom.ConnectionService.6
                };
            }
            connection = sNullConnection;
        }
        return connection;
    }

    private Conference findConferenceForAction(String conferenceId, String action) {
        if (this.mConferenceById.containsKey(conferenceId)) {
            return this.mConferenceById.get(conferenceId);
        }
        Log.m131w(this, "%s - Cannot find conference %s", action, conferenceId);
        return getNullConference();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public List<String> createConnectionIdList(List<Connection> connections) {
        List<String> ids = new ArrayList<>();
        for (Connection c : connections) {
            if (this.mIdByConnection.containsKey(c)) {
                ids.add(this.mIdByConnection.get(c));
            }
        }
        Collections.sort(ids);
        return ids;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public List<String> createIdList(List<Conferenceable> conferenceables) {
        List<String> ids = new ArrayList<>();
        for (Conferenceable c : conferenceables) {
            if (c instanceof Connection) {
                Connection connection = (Connection) c;
                if (this.mIdByConnection.containsKey(connection)) {
                    ids.add(this.mIdByConnection.get(connection));
                }
            } else if (c instanceof Conference) {
                Conference conference = (Conference) c;
                if (this.mIdByConference.containsKey(conference)) {
                    ids.add(this.mIdByConference.get(conference));
                }
            }
        }
        Collections.sort(ids);
        return ids;
    }

    private Conference getNullConference() {
        if (this.sNullConference == null) {
            this.sNullConference = new Conference(null) { // from class: android.telecom.ConnectionService.7
            };
        }
        return this.sNullConference;
    }

    private void endAllConnections() {
        for (Connection connection : this.mIdByConnection.keySet()) {
            if (connection.getConference() == null) {
                connection.onDisconnect();
            }
        }
        for (Conference conference : this.mIdByConference.keySet()) {
            conference.onDisconnect();
        }
    }

    private int getNextCallId() {
        int i;
        synchronized (this.mIdSyncRoot) {
            i = this.mId + 1;
            this.mId = i;
        }
        return i;
    }

    public Handler getHandler() {
        return this.mHandler;
    }

    public void setReadyForTest() {
        this.mAreAccountsInitialized = true;
    }
}
