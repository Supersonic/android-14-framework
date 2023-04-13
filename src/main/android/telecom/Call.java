package android.telecom;

import android.annotation.SystemApi;
import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.net.Uri;
import android.p008os.Bundle;
import android.p008os.Handler;
import android.p008os.ParcelFileDescriptor;
import android.telecom.Call;
import android.telecom.InCallService;
import com.android.internal.telecom.IVideoProvider;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
/* loaded from: classes3.dex */
public final class Call {
    @Deprecated
    public static final String AVAILABLE_PHONE_ACCOUNTS = "selectPhoneAccountAccounts";
    public static final String EVENT_CLEAR_DIAGNOSTIC_MESSAGE = "android.telecom.event.CLEAR_DIAGNOSTIC_MESSAGE";
    public static final String EVENT_DISPLAY_DIAGNOSTIC_MESSAGE = "android.telecom.event.DISPLAY_DIAGNOSTIC_MESSAGE";
    public static final String EVENT_DISPLAY_SOS_MESSAGE = "android.telecom.event.DISPLAY_SOS_MESSAGE";
    public static final String EVENT_HANDOVER_COMPLETE = "android.telecom.event.HANDOVER_COMPLETE";
    public static final String EVENT_HANDOVER_FAILED = "android.telecom.event.HANDOVER_FAILED";
    public static final String EVENT_HANDOVER_SOURCE_DISCONNECTED = "android.telecom.event.HANDOVER_SOURCE_DISCONNECTED";
    public static final String EVENT_REQUEST_HANDOVER = "android.telecom.event.REQUEST_HANDOVER";
    public static final String EXTRA_DIAGNOSTIC_MESSAGE = "android.telecom.extra.DIAGNOSTIC_MESSAGE";
    public static final String EXTRA_DIAGNOSTIC_MESSAGE_ID = "android.telecom.extra.DIAGNOSTIC_MESSAGE_ID";
    public static final String EXTRA_HANDOVER_EXTRAS = "android.telecom.extra.HANDOVER_EXTRAS";
    public static final String EXTRA_HANDOVER_PHONE_ACCOUNT_HANDLE = "android.telecom.extra.HANDOVER_PHONE_ACCOUNT_HANDLE";
    public static final String EXTRA_HANDOVER_VIDEO_STATE = "android.telecom.extra.HANDOVER_VIDEO_STATE";
    public static final String EXTRA_IS_SUPPRESSED_BY_DO_NOT_DISTURB = "android.telecom.extra.IS_SUPPRESSED_BY_DO_NOT_DISTURB";
    public static final String EXTRA_LAST_EMERGENCY_CALLBACK_TIME_MILLIS = "android.telecom.extra.LAST_EMERGENCY_CALLBACK_TIME_MILLIS";
    public static final String EXTRA_SILENT_RINGING_REQUESTED = "android.telecom.extra.SILENT_RINGING_REQUESTED";
    public static final String EXTRA_SUGGESTED_PHONE_ACCOUNTS = "android.telecom.extra.SUGGESTED_PHONE_ACCOUNTS";
    public static final int REJECT_REASON_DECLINED = 1;
    public static final int REJECT_REASON_UNWANTED = 2;
    public static final int STATE_ACTIVE = 4;
    public static final int STATE_AUDIO_PROCESSING = 12;
    public static final int STATE_CONNECTING = 9;
    public static final int STATE_DIALING = 1;
    public static final int STATE_DISCONNECTED = 7;
    public static final int STATE_DISCONNECTING = 10;
    public static final int STATE_HOLDING = 3;
    public static final int STATE_NEW = 0;
    @SystemApi
    @Deprecated
    public static final int STATE_PRE_DIAL_WAIT = 8;
    public static final int STATE_PULLING_CALL = 11;
    public static final int STATE_RINGING = 2;
    public static final int STATE_SELECT_PHONE_ACCOUNT = 8;
    public static final int STATE_SIMULATED_RINGING = 13;
    private String mActiveGenericConferenceChild;
    private final List<CallbackRecord<Callback>> mCallbackRecords;
    private String mCallingPackage;
    private List<String> mCannedTextResponses;
    private final List<Call> mChildren;
    private boolean mChildrenCached;
    private final List<String> mChildrenIds = new ArrayList();
    private final List<Call> mConferenceableCalls;
    private Details mDetails;
    private Bundle mExtras;
    private final InCallAdapter mInCallAdapter;
    private String mParentId;
    private final Phone mPhone;
    private String mRemainingPostDialSequence;
    private RttCall mRttCall;
    private int mState;
    private int mTargetSdkVersion;
    private final String mTelecomCallId;
    private final List<Call> mUnmodifiableChildren;
    private final List<Call> mUnmodifiableConferenceableCalls;
    private VideoCallImpl mVideoCallImpl;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface CallState {
    }

    @SystemApi
    @Deprecated
    /* loaded from: classes3.dex */
    public static abstract class Listener extends Callback {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface RejectReason {
    }

    /* loaded from: classes3.dex */
    public static class Details {
        public static final int CAPABILITY_ADD_PARTICIPANT = 33554432;
        public static final int CAPABILITY_CANNOT_DOWNGRADE_VIDEO_TO_AUDIO = 4194304;
        public static final int CAPABILITY_CAN_PAUSE_VIDEO = 1048576;
        public static final int CAPABILITY_CAN_PULL_CALL = 8388608;
        public static final int CAPABILITY_CAN_SEND_RESPONSE_VIA_CONNECTION = 2097152;
        public static final int CAPABILITY_CAN_UPGRADE_TO_VIDEO = 524288;
        public static final int CAPABILITY_DISCONNECT_FROM_CONFERENCE = 8192;
        public static final int CAPABILITY_HOLD = 1;
        public static final int CAPABILITY_MANAGE_CONFERENCE = 128;
        public static final int CAPABILITY_MERGE_CONFERENCE = 4;
        public static final int CAPABILITY_MUTE = 64;
        public static final int CAPABILITY_REMOTE_PARTY_SUPPORTS_RTT = 268435456;
        public static final int CAPABILITY_RESPOND_VIA_TEXT = 32;
        public static final int CAPABILITY_SEPARATE_FROM_CONFERENCE = 4096;
        public static final int CAPABILITY_SPEED_UP_MT_AUDIO = 262144;
        public static final int CAPABILITY_SUPPORTS_VT_LOCAL_BIDIRECTIONAL = 768;
        public static final int CAPABILITY_SUPPORTS_VT_LOCAL_RX = 256;
        public static final int CAPABILITY_SUPPORTS_VT_LOCAL_TX = 512;
        public static final int CAPABILITY_SUPPORTS_VT_REMOTE_BIDIRECTIONAL = 3072;
        public static final int CAPABILITY_SUPPORTS_VT_REMOTE_RX = 1024;
        public static final int CAPABILITY_SUPPORTS_VT_REMOTE_TX = 2048;
        public static final int CAPABILITY_SUPPORT_DEFLECT = 16777216;
        public static final int CAPABILITY_SUPPORT_HOLD = 2;
        public static final int CAPABILITY_SWAP_CONFERENCE = 8;
        public static final int CAPABILITY_TRANSFER = 67108864;
        public static final int CAPABILITY_TRANSFER_CONSULTATIVE = 134217728;
        public static final int CAPABILITY_UNUSED_1 = 16;
        public static final int DIRECTION_INCOMING = 0;
        public static final int DIRECTION_OUTGOING = 1;
        public static final int DIRECTION_UNKNOWN = -1;
        public static final int PROPERTY_ASSISTED_DIALING = 512;
        public static final int PROPERTY_CONFERENCE = 1;
        public static final int PROPERTY_CROSS_SIM = 16384;
        public static final int PROPERTY_EMERGENCY_CALLBACK_MODE = 4;
        public static final int PROPERTY_ENTERPRISE_CALL = 32;
        public static final int PROPERTY_GENERIC_CONFERENCE = 2;
        public static final int PROPERTY_HAS_CDMA_VOICE_PRIVACY = 128;
        public static final int PROPERTY_HIGH_DEF_AUDIO = 16;
        public static final int PROPERTY_IS_ADHOC_CONFERENCE = 8192;
        public static final int PROPERTY_IS_EXTERNAL_CALL = 64;
        public static final int PROPERTY_NETWORK_IDENTIFIED_EMERGENCY_CALL = 2048;
        public static final int PROPERTY_RTT = 1024;
        public static final int PROPERTY_SELF_MANAGED = 256;
        public static final int PROPERTY_VOIP_AUDIO_MODE = 4096;
        public static final int PROPERTY_WIFI = 8;
        private final PhoneAccountHandle mAccountHandle;
        private final int mCallCapabilities;
        private final int mCallDirection;
        private final int mCallProperties;
        private final String mCallerDisplayName;
        private final int mCallerDisplayNamePresentation;
        private final int mCallerNumberVerificationStatus;
        private final long mConnectTimeMillis;
        private final String mContactDisplayName;
        private final Uri mContactPhotoUri;
        private final long mCreationTimeMillis;
        private final DisconnectCause mDisconnectCause;
        private final Bundle mExtras;
        private final GatewayInfo mGatewayInfo;
        private final Uri mHandle;
        private final int mHandlePresentation;
        private final Bundle mIntentExtras;
        private final int mState;
        private final StatusHints mStatusHints;
        private final int mSupportedAudioRoutes = 31;
        private final String mTelecomCallId;
        private final int mVideoState;

        @Retention(RetentionPolicy.SOURCE)
        /* loaded from: classes3.dex */
        public @interface CallDirection {
        }

        public static boolean can(int capabilities, int capability) {
            return (capabilities & capability) == capability;
        }

        public boolean can(int capability) {
            return can(this.mCallCapabilities, capability);
        }

        public static String capabilitiesToString(int capabilities) {
            StringBuilder builder = new StringBuilder();
            builder.append("[Capabilities:");
            if (can(capabilities, 1)) {
                builder.append(" CAPABILITY_HOLD");
            }
            if (can(capabilities, 2)) {
                builder.append(" CAPABILITY_SUPPORT_HOLD");
            }
            if (can(capabilities, 4)) {
                builder.append(" CAPABILITY_MERGE_CONFERENCE");
            }
            if (can(capabilities, 8)) {
                builder.append(" CAPABILITY_SWAP_CONFERENCE");
            }
            if (can(capabilities, 32)) {
                builder.append(" CAPABILITY_RESPOND_VIA_TEXT");
            }
            if (can(capabilities, 64)) {
                builder.append(" CAPABILITY_MUTE");
            }
            if (can(capabilities, 128)) {
                builder.append(" CAPABILITY_MANAGE_CONFERENCE");
            }
            if (can(capabilities, 256)) {
                builder.append(" CAPABILITY_SUPPORTS_VT_LOCAL_RX");
            }
            if (can(capabilities, 512)) {
                builder.append(" CAPABILITY_SUPPORTS_VT_LOCAL_TX");
            }
            if (can(capabilities, 768)) {
                builder.append(" CAPABILITY_SUPPORTS_VT_LOCAL_BIDIRECTIONAL");
            }
            if (can(capabilities, 1024)) {
                builder.append(" CAPABILITY_SUPPORTS_VT_REMOTE_RX");
            }
            if (can(capabilities, 2048)) {
                builder.append(" CAPABILITY_SUPPORTS_VT_REMOTE_TX");
            }
            if (can(capabilities, 4194304)) {
                builder.append(" CAPABILITY_CANNOT_DOWNGRADE_VIDEO_TO_AUDIO");
            }
            if (can(capabilities, 3072)) {
                builder.append(" CAPABILITY_SUPPORTS_VT_REMOTE_BIDIRECTIONAL");
            }
            if (can(capabilities, 262144)) {
                builder.append(" CAPABILITY_SPEED_UP_MT_AUDIO");
            }
            if (can(capabilities, 524288)) {
                builder.append(" CAPABILITY_CAN_UPGRADE_TO_VIDEO");
            }
            if (can(capabilities, 1048576)) {
                builder.append(" CAPABILITY_CAN_PAUSE_VIDEO");
            }
            if (can(capabilities, 8388608)) {
                builder.append(" CAPABILITY_CAN_PULL_CALL");
            }
            if (can(capabilities, 16777216)) {
                builder.append(" CAPABILITY_SUPPORT_DEFLECT");
            }
            if (can(capabilities, 33554432)) {
                builder.append(" CAPABILITY_ADD_PARTICIPANT");
            }
            if (can(capabilities, 67108864)) {
                builder.append(" CAPABILITY_TRANSFER");
            }
            if (can(capabilities, 134217728)) {
                builder.append(" CAPABILITY_TRANSFER_CONSULTATIVE");
            }
            if (can(capabilities, 268435456)) {
                builder.append(" CAPABILITY_REMOTE_PARTY_SUPPORTS_RTT");
            }
            builder.append(NavigationBarInflaterView.SIZE_MOD_END);
            return builder.toString();
        }

        public static boolean hasProperty(int properties, int property) {
            return (properties & property) == property;
        }

        public boolean hasProperty(int property) {
            return hasProperty(this.mCallProperties, property);
        }

        public static String propertiesToString(int properties) {
            StringBuilder builder = new StringBuilder();
            builder.append("[Properties:");
            if (hasProperty(properties, 1)) {
                builder.append(" PROPERTY_CONFERENCE");
            }
            if (hasProperty(properties, 2)) {
                builder.append(" PROPERTY_GENERIC_CONFERENCE");
            }
            if (hasProperty(properties, 8)) {
                builder.append(" PROPERTY_WIFI");
            }
            if (hasProperty(properties, 16)) {
                builder.append(" PROPERTY_HIGH_DEF_AUDIO");
            }
            if (hasProperty(properties, 4)) {
                builder.append(" PROPERTY_EMERGENCY_CALLBACK_MODE");
            }
            if (hasProperty(properties, 64)) {
                builder.append(" PROPERTY_IS_EXTERNAL_CALL");
            }
            if (hasProperty(properties, 128)) {
                builder.append(" PROPERTY_HAS_CDMA_VOICE_PRIVACY");
            }
            if (hasProperty(properties, 512)) {
                builder.append(" PROPERTY_ASSISTED_DIALING_USED");
            }
            if (hasProperty(properties, 2048)) {
                builder.append(" PROPERTY_NETWORK_IDENTIFIED_EMERGENCY_CALL");
            }
            if (hasProperty(properties, 1024)) {
                builder.append(" PROPERTY_RTT");
            }
            if (hasProperty(properties, 4096)) {
                builder.append(" PROPERTY_VOIP_AUDIO_MODE");
            }
            if (hasProperty(properties, 8192)) {
                builder.append(" PROPERTY_IS_ADHOC_CONFERENCE");
            }
            if (hasProperty(properties, 16384)) {
                builder.append(" PROPERTY_CROSS_SIM");
            }
            builder.append(NavigationBarInflaterView.SIZE_MOD_END);
            return builder.toString();
        }

        public final int getState() {
            return this.mState;
        }

        public String getTelecomCallId() {
            return this.mTelecomCallId;
        }

        public Uri getHandle() {
            return this.mHandle;
        }

        public int getHandlePresentation() {
            return this.mHandlePresentation;
        }

        public Uri getContactPhotoUri() {
            return this.mContactPhotoUri;
        }

        public String getCallerDisplayName() {
            return this.mCallerDisplayName;
        }

        public int getCallerDisplayNamePresentation() {
            return this.mCallerDisplayNamePresentation;
        }

        public PhoneAccountHandle getAccountHandle() {
            return this.mAccountHandle;
        }

        public int getCallCapabilities() {
            return this.mCallCapabilities;
        }

        public int getCallProperties() {
            return this.mCallProperties;
        }

        public int getSupportedAudioRoutes() {
            return 31;
        }

        public DisconnectCause getDisconnectCause() {
            return this.mDisconnectCause;
        }

        public final long getConnectTimeMillis() {
            return this.mConnectTimeMillis;
        }

        public GatewayInfo getGatewayInfo() {
            return this.mGatewayInfo;
        }

        public int getVideoState() {
            return this.mVideoState;
        }

        public StatusHints getStatusHints() {
            return this.mStatusHints;
        }

        public Bundle getExtras() {
            return this.mExtras;
        }

        public Bundle getIntentExtras() {
            return this.mIntentExtras;
        }

        public long getCreationTimeMillis() {
            return this.mCreationTimeMillis;
        }

        public String getContactDisplayName() {
            return this.mContactDisplayName;
        }

        public int getCallDirection() {
            return this.mCallDirection;
        }

        public int getCallerNumberVerificationStatus() {
            return this.mCallerNumberVerificationStatus;
        }

        public boolean equals(Object o) {
            if (o instanceof Details) {
                Details d = (Details) o;
                return Objects.equals(Integer.valueOf(this.mState), Integer.valueOf(d.mState)) && Objects.equals(this.mHandle, d.mHandle) && Objects.equals(Integer.valueOf(this.mHandlePresentation), Integer.valueOf(d.mHandlePresentation)) && Objects.equals(this.mCallerDisplayName, d.mCallerDisplayName) && Objects.equals(Integer.valueOf(this.mCallerDisplayNamePresentation), Integer.valueOf(d.mCallerDisplayNamePresentation)) && Objects.equals(this.mAccountHandle, d.mAccountHandle) && Objects.equals(Integer.valueOf(this.mCallCapabilities), Integer.valueOf(d.mCallCapabilities)) && Objects.equals(Integer.valueOf(this.mCallProperties), Integer.valueOf(d.mCallProperties)) && Objects.equals(this.mDisconnectCause, d.mDisconnectCause) && Objects.equals(Long.valueOf(this.mConnectTimeMillis), Long.valueOf(d.mConnectTimeMillis)) && Objects.equals(this.mGatewayInfo, d.mGatewayInfo) && Objects.equals(Integer.valueOf(this.mVideoState), Integer.valueOf(d.mVideoState)) && Objects.equals(this.mStatusHints, d.mStatusHints) && Call.areBundlesEqual(this.mExtras, d.mExtras) && Call.areBundlesEqual(this.mIntentExtras, d.mIntentExtras) && Objects.equals(Long.valueOf(this.mCreationTimeMillis), Long.valueOf(d.mCreationTimeMillis)) && Objects.equals(this.mContactDisplayName, d.mContactDisplayName) && Objects.equals(Integer.valueOf(this.mCallDirection), Integer.valueOf(d.mCallDirection)) && Objects.equals(Integer.valueOf(this.mCallerNumberVerificationStatus), Integer.valueOf(d.mCallerNumberVerificationStatus)) && Objects.equals(this.mContactPhotoUri, d.mContactPhotoUri);
            }
            return false;
        }

        public int hashCode() {
            return Objects.hash(Integer.valueOf(this.mState), this.mHandle, Integer.valueOf(this.mHandlePresentation), this.mCallerDisplayName, Integer.valueOf(this.mCallerDisplayNamePresentation), this.mAccountHandle, Integer.valueOf(this.mCallCapabilities), Integer.valueOf(this.mCallProperties), this.mDisconnectCause, Long.valueOf(this.mConnectTimeMillis), this.mGatewayInfo, Integer.valueOf(this.mVideoState), this.mStatusHints, this.mExtras, this.mIntentExtras, Long.valueOf(this.mCreationTimeMillis), this.mContactDisplayName, Integer.valueOf(this.mCallDirection), Integer.valueOf(this.mCallerNumberVerificationStatus), this.mContactPhotoUri);
        }

        public Details(int state, String telecomCallId, Uri handle, int handlePresentation, String callerDisplayName, int callerDisplayNamePresentation, PhoneAccountHandle accountHandle, int capabilities, int properties, DisconnectCause disconnectCause, long connectTimeMillis, GatewayInfo gatewayInfo, int videoState, StatusHints statusHints, Bundle extras, Bundle intentExtras, long creationTimeMillis, String contactDisplayName, int callDirection, int callerNumberVerificationStatus, Uri contactPhotoUri) {
            this.mState = state;
            this.mTelecomCallId = telecomCallId;
            this.mHandle = handle;
            this.mHandlePresentation = handlePresentation;
            this.mCallerDisplayName = callerDisplayName;
            this.mCallerDisplayNamePresentation = callerDisplayNamePresentation;
            this.mAccountHandle = accountHandle;
            this.mCallCapabilities = capabilities;
            this.mCallProperties = properties;
            this.mDisconnectCause = disconnectCause;
            this.mConnectTimeMillis = connectTimeMillis;
            this.mGatewayInfo = gatewayInfo;
            this.mVideoState = videoState;
            this.mStatusHints = statusHints;
            this.mExtras = extras;
            this.mIntentExtras = intentExtras;
            this.mCreationTimeMillis = creationTimeMillis;
            this.mContactDisplayName = contactDisplayName;
            this.mCallDirection = callDirection;
            this.mCallerNumberVerificationStatus = callerNumberVerificationStatus;
            this.mContactPhotoUri = contactPhotoUri;
        }

        public static Details createFromParcelableCall(ParcelableCall parcelableCall) {
            return new Details(parcelableCall.getState(), parcelableCall.getId(), parcelableCall.getHandle(), parcelableCall.getHandlePresentation(), parcelableCall.getCallerDisplayName(), parcelableCall.getCallerDisplayNamePresentation(), parcelableCall.getAccountHandle(), parcelableCall.getCapabilities(), parcelableCall.getProperties(), parcelableCall.getDisconnectCause(), parcelableCall.getConnectTimeMillis(), parcelableCall.getGatewayInfo(), parcelableCall.getVideoState(), parcelableCall.getStatusHints(), parcelableCall.getExtras(), parcelableCall.getIntentExtras(), parcelableCall.getCreationTimeMillis(), parcelableCall.getContactDisplayName(), parcelableCall.getCallDirection(), parcelableCall.getCallerNumberVerificationStatus(), parcelableCall.getContactPhotoUri());
        }

        public String toString() {
            return "[id: " + this.mTelecomCallId + ", state: " + Call.stateToString(this.mState) + ", pa: " + this.mAccountHandle + ", hdl: " + Log.piiHandle(this.mHandle) + ", hdlPres: " + this.mHandlePresentation + ", videoState: " + VideoProfile.videoStateToString(this.mVideoState) + ", caps: " + capabilitiesToString(this.mCallCapabilities) + ", props: " + propertiesToString(this.mCallProperties) + NavigationBarInflaterView.SIZE_MOD_END;
        }
    }

    /* loaded from: classes3.dex */
    public static abstract class Callback {
        public static final int HANDOVER_FAILURE_DEST_APP_REJECTED = 1;
        public static final int HANDOVER_FAILURE_NOT_SUPPORTED = 2;
        public static final int HANDOVER_FAILURE_ONGOING_EMERGENCY_CALL = 4;
        public static final int HANDOVER_FAILURE_UNKNOWN = 5;
        public static final int HANDOVER_FAILURE_USER_REJECTED = 3;

        @Retention(RetentionPolicy.SOURCE)
        /* loaded from: classes3.dex */
        public @interface HandoverFailureErrors {
        }

        public void onStateChanged(Call call, int state) {
        }

        public void onParentChanged(Call call, Call parent) {
        }

        public void onChildrenChanged(Call call, List<Call> children) {
        }

        public void onDetailsChanged(Call call, Details details) {
        }

        public void onCannedTextResponsesLoaded(Call call, List<String> cannedTextResponses) {
        }

        public void onPostDialWait(Call call, String remainingPostDialSequence) {
        }

        public void onVideoCallChanged(Call call, InCallService.VideoCall videoCall) {
        }

        public void onCallDestroyed(Call call) {
        }

        public void onConferenceableCallsChanged(Call call, List<Call> conferenceableCalls) {
        }

        public void onConnectionEvent(Call call, String event, Bundle extras) {
        }

        public void onRttModeChanged(Call call, int mode) {
        }

        public void onRttStatusChanged(Call call, boolean enabled, RttCall rttCall) {
        }

        public void onRttRequest(Call call, int id) {
        }

        public void onRttInitiationFailure(Call call, int reason) {
        }

        public void onHandoverComplete(Call call) {
        }

        public void onHandoverFailed(Call call, int failureReason) {
        }
    }

    /* loaded from: classes3.dex */
    public static final class RttCall {
        private static final int READ_BUFFER_SIZE = 1000;
        public static final int RTT_MODE_FULL = 1;
        public static final int RTT_MODE_HCO = 2;
        public static final int RTT_MODE_INVALID = 0;
        public static final int RTT_MODE_VCO = 3;
        private final InCallAdapter mInCallAdapter;
        private char[] mReadBuffer = new char[1000];
        private InputStreamReader mReceiveStream;
        private int mRttMode;
        private final String mTelecomCallId;
        private OutputStreamWriter mTransmitStream;

        @Retention(RetentionPolicy.SOURCE)
        /* loaded from: classes3.dex */
        public @interface RttAudioMode {
        }

        public RttCall(String telecomCallId, InputStreamReader receiveStream, OutputStreamWriter transmitStream, int mode, InCallAdapter inCallAdapter) {
            this.mTelecomCallId = telecomCallId;
            this.mReceiveStream = receiveStream;
            this.mTransmitStream = transmitStream;
            this.mRttMode = mode;
            this.mInCallAdapter = inCallAdapter;
        }

        public int getRttAudioMode() {
            return this.mRttMode;
        }

        public void setRttMode(int mode) {
            this.mInCallAdapter.setRttMode(this.mTelecomCallId, mode);
        }

        public void write(String input) throws IOException {
            this.mTransmitStream.write(input);
            this.mTransmitStream.flush();
        }

        public String read() {
            try {
                int numRead = this.mReceiveStream.read(this.mReadBuffer, 0, 1000);
                if (numRead < 0) {
                    return null;
                }
                return new String(this.mReadBuffer, 0, numRead);
            } catch (IOException e) {
                Log.m131w(this, "Exception encountered when reading from InputStreamReader: %s", e);
                return null;
            }
        }

        public String readImmediately() throws IOException {
            int numRead;
            if (!this.mReceiveStream.ready() || (numRead = this.mReceiveStream.read(this.mReadBuffer, 0, 1000)) < 0) {
                return null;
            }
            return new String(this.mReadBuffer, 0, numRead);
        }

        public void close() {
            try {
                this.mReceiveStream.close();
            } catch (IOException e) {
            }
            try {
                this.mTransmitStream.close();
            } catch (IOException e2) {
            }
        }
    }

    public String getRemainingPostDialSequence() {
        return this.mRemainingPostDialSequence;
    }

    public void answer(int videoState) {
        this.mInCallAdapter.answerCall(this.mTelecomCallId, videoState);
    }

    public void deflect(Uri address) {
        this.mInCallAdapter.deflectCall(this.mTelecomCallId, address);
    }

    public void reject(boolean rejectWithMessage, String textMessage) {
        this.mInCallAdapter.rejectCall(this.mTelecomCallId, rejectWithMessage, textMessage);
    }

    public void reject(int rejectReason) {
        this.mInCallAdapter.rejectCall(this.mTelecomCallId, rejectReason);
    }

    public void transfer(Uri targetNumber, boolean isConfirmationRequired) {
        this.mInCallAdapter.transferCall(this.mTelecomCallId, targetNumber, isConfirmationRequired);
    }

    public void transfer(Call toCall) {
        this.mInCallAdapter.transferCall(this.mTelecomCallId, toCall.mTelecomCallId);
    }

    public void disconnect() {
        this.mInCallAdapter.disconnectCall(this.mTelecomCallId);
    }

    public void hold() {
        this.mInCallAdapter.holdCall(this.mTelecomCallId);
    }

    public void unhold() {
        this.mInCallAdapter.unholdCall(this.mTelecomCallId);
    }

    @SystemApi
    public void enterBackgroundAudioProcessing() {
        int i = this.mState;
        if (i != 4 && i != 2) {
            throw new IllegalStateException("Call must be active or ringing");
        }
        this.mInCallAdapter.enterBackgroundAudioProcessing(this.mTelecomCallId);
    }

    @SystemApi
    public void exitBackgroundAudioProcessing(boolean shouldRing) {
        if (this.mState != 12) {
            throw new IllegalStateException("Call must in the audio processing state");
        }
        this.mInCallAdapter.exitBackgroundAudioProcessing(this.mTelecomCallId, shouldRing);
    }

    public void playDtmfTone(char digit) {
        this.mInCallAdapter.playDtmfTone(this.mTelecomCallId, digit);
    }

    public void stopDtmfTone() {
        this.mInCallAdapter.stopDtmfTone(this.mTelecomCallId);
    }

    public void postDialContinue(boolean proceed) {
        this.mInCallAdapter.postDialContinue(this.mTelecomCallId, proceed);
    }

    public void phoneAccountSelected(PhoneAccountHandle accountHandle, boolean setDefault) {
        this.mInCallAdapter.phoneAccountSelected(this.mTelecomCallId, accountHandle, setDefault);
    }

    public void conference(Call callToConferenceWith) {
        if (callToConferenceWith != null) {
            this.mInCallAdapter.conference(this.mTelecomCallId, callToConferenceWith.mTelecomCallId);
        }
    }

    public void splitFromConference() {
        this.mInCallAdapter.splitFromConference(this.mTelecomCallId);
    }

    public void mergeConference() {
        this.mInCallAdapter.mergeConference(this.mTelecomCallId);
    }

    public void swapConference() {
        this.mInCallAdapter.swapConference(this.mTelecomCallId);
    }

    public void addConferenceParticipants(List<Uri> participants) {
        this.mInCallAdapter.addConferenceParticipants(this.mTelecomCallId, participants);
    }

    public void pullExternalCall() {
        if (!this.mDetails.hasProperty(64)) {
            return;
        }
        this.mInCallAdapter.pullExternalCall(this.mTelecomCallId);
    }

    public void sendCallEvent(String event, Bundle extras) {
        this.mInCallAdapter.sendCallEvent(this.mTelecomCallId, event, this.mTargetSdkVersion, extras);
    }

    public void sendRttRequest() {
        this.mInCallAdapter.sendRttRequest(this.mTelecomCallId);
    }

    public void respondToRttRequest(int id, boolean accept) {
        this.mInCallAdapter.respondToRttRequest(this.mTelecomCallId, id, accept);
    }

    public void handoverTo(PhoneAccountHandle toHandle, int videoState, Bundle extras) {
        this.mInCallAdapter.handoverTo(this.mTelecomCallId, toHandle, videoState, extras);
    }

    public void stopRtt() {
        this.mInCallAdapter.stopRtt(this.mTelecomCallId);
    }

    public final void putExtras(Bundle extras) {
        if (extras == null) {
            return;
        }
        if (this.mExtras == null) {
            this.mExtras = new Bundle();
        }
        this.mExtras.putAll(extras);
        this.mInCallAdapter.putExtras(this.mTelecomCallId, extras);
    }

    public final void putExtra(String key, boolean value) {
        if (this.mExtras == null) {
            this.mExtras = new Bundle();
        }
        this.mExtras.putBoolean(key, value);
        this.mInCallAdapter.putExtra(this.mTelecomCallId, key, value);
    }

    public final void putExtra(String key, int value) {
        if (this.mExtras == null) {
            this.mExtras = new Bundle();
        }
        this.mExtras.putInt(key, value);
        this.mInCallAdapter.putExtra(this.mTelecomCallId, key, value);
    }

    public final void putExtra(String key, String value) {
        if (this.mExtras == null) {
            this.mExtras = new Bundle();
        }
        this.mExtras.putString(key, value);
        this.mInCallAdapter.putExtra(this.mTelecomCallId, key, value);
    }

    public final void removeExtras(List<String> keys) {
        if (this.mExtras != null) {
            for (String key : keys) {
                this.mExtras.remove(key);
            }
            if (this.mExtras.size() == 0) {
                this.mExtras = null;
            }
        }
        this.mInCallAdapter.removeExtras(this.mTelecomCallId, keys);
    }

    public final void removeExtras(String... keys) {
        removeExtras(Arrays.asList(keys));
    }

    public Call getParent() {
        String str = this.mParentId;
        if (str != null) {
            return this.mPhone.internalGetCallByTelecomId(str);
        }
        return null;
    }

    public List<Call> getChildren() {
        if (!this.mChildrenCached) {
            this.mChildrenCached = true;
            this.mChildren.clear();
            for (String id : this.mChildrenIds) {
                Call call = this.mPhone.internalGetCallByTelecomId(id);
                if (call == null) {
                    this.mChildrenCached = false;
                } else {
                    this.mChildren.add(call);
                }
            }
        }
        return this.mUnmodifiableChildren;
    }

    public List<Call> getConferenceableCalls() {
        return this.mUnmodifiableConferenceableCalls;
    }

    @Deprecated
    public int getState() {
        return this.mState;
    }

    public Call getGenericConferenceActiveChildCall() {
        String str = this.mActiveGenericConferenceChild;
        if (str != null) {
            return this.mPhone.internalGetCallByTelecomId(str);
        }
        return null;
    }

    public List<String> getCannedTextResponses() {
        return this.mCannedTextResponses;
    }

    public InCallService.VideoCall getVideoCall() {
        return this.mVideoCallImpl;
    }

    public Details getDetails() {
        return this.mDetails;
    }

    public RttCall getRttCall() {
        return this.mRttCall;
    }

    public boolean isRttActive() {
        return this.mRttCall != null && this.mDetails.hasProperty(1024);
    }

    public void registerCallback(Callback callback) {
        registerCallback(callback, new Handler());
    }

    public void registerCallback(Callback callback, Handler handler) {
        unregisterCallback(callback);
        if (callback != null && handler != null && this.mState != 7) {
            this.mCallbackRecords.add(new CallbackRecord<>(callback, handler));
        }
    }

    public void unregisterCallback(Callback callback) {
        if (callback != null && this.mState != 7) {
            for (CallbackRecord<Callback> record : this.mCallbackRecords) {
                if (record.getCallback() == callback) {
                    this.mCallbackRecords.remove(record);
                    return;
                }
            }
        }
    }

    public String toString() {
        return "Call [id: " + this.mTelecomCallId + ", state: " + stateToString(this.mState) + ", details: " + this.mDetails + NavigationBarInflaterView.SIZE_MOD_END;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static String stateToString(int state) {
        switch (state) {
            case 0:
                return "NEW";
            case 1:
                return "DIALING";
            case 2:
                return "RINGING";
            case 3:
                return "HOLDING";
            case 4:
                return "ACTIVE";
            case 5:
            case 6:
            case 11:
            default:
                Log.m131w(Call.class, "Unknown state %d", Integer.valueOf(state));
                return "UNKNOWN";
            case 7:
                return "DISCONNECTED";
            case 8:
                return "SELECT_PHONE_ACCOUNT";
            case 9:
                return "CONNECTING";
            case 10:
                return "DISCONNECTING";
            case 12:
                return "AUDIO_PROCESSING";
            case 13:
                return "SIMULATED_RINGING";
        }
    }

    @SystemApi
    @Deprecated
    public void addListener(Listener listener) {
        registerCallback(listener);
    }

    @SystemApi
    @Deprecated
    public void removeListener(Listener listener) {
        unregisterCallback(listener);
    }

    Call(Phone phone, String telecomCallId, InCallAdapter inCallAdapter, String callingPackage, int targetSdkVersion) {
        ArrayList arrayList = new ArrayList();
        this.mChildren = arrayList;
        this.mUnmodifiableChildren = Collections.unmodifiableList(arrayList);
        this.mCallbackRecords = new CopyOnWriteArrayList();
        ArrayList arrayList2 = new ArrayList();
        this.mConferenceableCalls = arrayList2;
        this.mUnmodifiableConferenceableCalls = Collections.unmodifiableList(arrayList2);
        this.mParentId = null;
        this.mActiveGenericConferenceChild = null;
        this.mCannedTextResponses = null;
        this.mPhone = phone;
        this.mTelecomCallId = telecomCallId;
        this.mInCallAdapter = inCallAdapter;
        this.mState = 0;
        this.mCallingPackage = callingPackage;
        this.mTargetSdkVersion = targetSdkVersion;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Call(Phone phone, String telecomCallId, InCallAdapter inCallAdapter, int state, String callingPackage, int targetSdkVersion) {
        ArrayList arrayList = new ArrayList();
        this.mChildren = arrayList;
        this.mUnmodifiableChildren = Collections.unmodifiableList(arrayList);
        this.mCallbackRecords = new CopyOnWriteArrayList();
        ArrayList arrayList2 = new ArrayList();
        this.mConferenceableCalls = arrayList2;
        this.mUnmodifiableConferenceableCalls = Collections.unmodifiableList(arrayList2);
        this.mParentId = null;
        this.mActiveGenericConferenceChild = null;
        this.mCannedTextResponses = null;
        this.mPhone = phone;
        this.mTelecomCallId = telecomCallId;
        this.mInCallAdapter = inCallAdapter;
        this.mState = state;
        this.mCallingPackage = callingPackage;
        this.mTargetSdkVersion = targetSdkVersion;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final String internalGetCallId() {
        return this.mTelecomCallId;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Removed duplicated region for block: B:102:0x0204  */
    /* JADX WARN: Removed duplicated region for block: B:108:0x0211  */
    /* JADX WARN: Removed duplicated region for block: B:111:0x021f  */
    /* JADX WARN: Removed duplicated region for block: B:116:? A[RETURN, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:89:0x01d4  */
    /* JADX WARN: Removed duplicated region for block: B:91:0x01db  */
    /* JADX WARN: Removed duplicated region for block: B:93:0x01e2  */
    /* JADX WARN: Removed duplicated region for block: B:95:0x01e9  */
    /* JADX WARN: Removed duplicated region for block: B:97:0x01f0  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final void internalUpdate(ParcelableCall parcelableCall, Map<String, Call> callIdMap) {
        boolean isRttChanged;
        boolean rttModeChanged;
        boolean isRttChanged2;
        boolean rttModeChanged2;
        Map<String, Call> map = callIdMap;
        Details details = Details.createFromParcelableCall(parcelableCall);
        boolean detailsChanged = !Objects.equals(this.mDetails, details);
        if (detailsChanged) {
            this.mDetails = details;
        }
        boolean cannedTextResponsesChanged = false;
        if (this.mCannedTextResponses == null && parcelableCall.getCannedSmsResponses() != null && !parcelableCall.getCannedSmsResponses().isEmpty()) {
            this.mCannedTextResponses = Collections.unmodifiableList(parcelableCall.getCannedSmsResponses());
            cannedTextResponsesChanged = true;
        }
        VideoCallImpl videoCallImpl = this.mVideoCallImpl;
        IVideoProvider previousVideoProvider = videoCallImpl == null ? null : videoCallImpl.getVideoProvider();
        IVideoProvider newVideoProvider = parcelableCall.getVideoProvider();
        boolean videoCallChanged = parcelableCall.isVideoCallProviderChanged() && !Objects.equals(previousVideoProvider, newVideoProvider);
        if (videoCallChanged) {
            VideoCallImpl videoCallImpl2 = this.mVideoCallImpl;
            if (videoCallImpl2 != null) {
                videoCallImpl2.destroy();
            }
            this.mVideoCallImpl = parcelableCall.isVideoCallProviderChanged() ? parcelableCall.getVideoCallImpl(this.mCallingPackage, this.mTargetSdkVersion) : null;
        }
        VideoCallImpl videoCallImpl3 = this.mVideoCallImpl;
        if (videoCallImpl3 != null) {
            videoCallImpl3.setVideoState(getDetails().getVideoState());
        }
        int state = parcelableCall.getState();
        if (this.mTargetSdkVersion < 30 && state == 13) {
            state = 2;
        }
        boolean stateChanged = this.mState != state;
        if (stateChanged) {
            this.mState = state;
        }
        String parentId = parcelableCall.getParentCallId();
        boolean parentChanged = !Objects.equals(this.mParentId, parentId);
        if (parentChanged) {
            this.mParentId = parentId;
        }
        List<String> childCallIds = parcelableCall.getChildCallIds();
        boolean childrenChanged = !Objects.equals(childCallIds, this.mChildrenIds);
        if (childrenChanged) {
            this.mChildrenIds.clear();
            this.mChildrenIds.addAll(parcelableCall.getChildCallIds());
            this.mChildrenCached = false;
        }
        String activeChildCallId = parcelableCall.getActiveChildCallId();
        boolean activeChildChanged = !Objects.equals(activeChildCallId, this.mActiveGenericConferenceChild);
        if (activeChildChanged) {
            this.mActiveGenericConferenceChild = activeChildCallId;
        }
        List<String> conferenceableCallIds = parcelableCall.getConferenceableCallIds();
        List<Call> conferenceableCalls = new ArrayList<>(conferenceableCallIds.size());
        Iterator<String> it = conferenceableCallIds.iterator();
        while (it.hasNext()) {
            Iterator<String> it2 = it;
            String otherId = it.next();
            if (map.containsKey(otherId)) {
                conferenceableCalls.add(map.get(otherId));
            }
            map = callIdMap;
            it = it2;
        }
        if (!Objects.equals(this.mConferenceableCalls, conferenceableCalls)) {
            this.mConferenceableCalls.clear();
            this.mConferenceableCalls.addAll(conferenceableCalls);
            fireConferenceableCallsChanged();
        }
        if (parcelableCall.getIsRttCallChanged()) {
            isRttChanged = false;
            if (!this.mDetails.hasProperty(1024)) {
                rttModeChanged = false;
            } else {
                ParcelableRttCall parcelableRttCall = parcelableCall.getParcelableRttCall();
                InputStreamReader receiveStream = new InputStreamReader(new ParcelFileDescriptor.AutoCloseInputStream(parcelableRttCall.getReceiveStream()), StandardCharsets.UTF_8);
                OutputStreamWriter transmitStream = new OutputStreamWriter(new ParcelFileDescriptor.AutoCloseOutputStream(parcelableRttCall.getTransmitStream()), StandardCharsets.UTF_8);
                RttCall newRttCall = new RttCall(this.mTelecomCallId, receiveStream, transmitStream, parcelableRttCall.getRttMode(), this.mInCallAdapter);
                RttCall rttCall = this.mRttCall;
                if (rttCall == null) {
                    isRttChanged = true;
                    rttModeChanged2 = false;
                } else if (rttCall.getRttAudioMode() == newRttCall.getRttAudioMode()) {
                    rttModeChanged2 = false;
                } else {
                    rttModeChanged2 = true;
                }
                this.mRttCall = newRttCall;
                isRttChanged2 = isRttChanged;
                if (stateChanged) {
                    fireStateChanged(this.mState);
                }
                if (detailsChanged) {
                    fireDetailsChanged(this.mDetails);
                }
                if (cannedTextResponsesChanged) {
                    fireCannedTextResponsesLoaded(this.mCannedTextResponses);
                }
                if (videoCallChanged) {
                    fireVideoCallChanged(this.mVideoCallImpl);
                }
                if (parentChanged) {
                    fireParentChanged(getParent());
                }
                if (!childrenChanged || activeChildChanged) {
                    fireChildrenChanged(getChildren());
                }
                if (isRttChanged2) {
                    RttCall rttCall2 = this.mRttCall;
                    fireOnIsRttChanged(rttCall2 != null, rttCall2);
                }
                if (rttModeChanged2) {
                    fireOnRttModeChanged(this.mRttCall.getRttAudioMode());
                }
                if (this.mState != 7) {
                    fireCallDestroyed();
                    return;
                }
                return;
            }
        } else {
            isRttChanged = false;
            rttModeChanged = false;
        }
        if (this.mRttCall != null && parcelableCall.getParcelableRttCall() == null && parcelableCall.getIsRttCallChanged()) {
            isRttChanged2 = true;
            this.mRttCall.close();
            this.mRttCall = null;
            rttModeChanged2 = rttModeChanged;
        } else {
            isRttChanged2 = isRttChanged;
            rttModeChanged2 = rttModeChanged;
        }
        if (stateChanged) {
        }
        if (detailsChanged) {
        }
        if (cannedTextResponsesChanged) {
        }
        if (videoCallChanged) {
        }
        if (parentChanged) {
        }
        if (!childrenChanged) {
        }
        fireChildrenChanged(getChildren());
        if (isRttChanged2) {
        }
        if (rttModeChanged2) {
        }
        if (this.mState != 7) {
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void internalSetPostDialWait(String remaining) {
        this.mRemainingPostDialSequence = remaining;
        firePostDialWait(remaining);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void internalSetDisconnected() {
        if (this.mState != 7) {
            this.mState = 7;
            Details details = this.mDetails;
            if (details != null) {
                Details details2 = new Details(7, details.getTelecomCallId(), this.mDetails.getHandle(), this.mDetails.getHandlePresentation(), this.mDetails.getCallerDisplayName(), this.mDetails.getCallerDisplayNamePresentation(), this.mDetails.getAccountHandle(), this.mDetails.getCallCapabilities(), this.mDetails.getCallProperties(), this.mDetails.getDisconnectCause(), this.mDetails.getConnectTimeMillis(), this.mDetails.getGatewayInfo(), this.mDetails.getVideoState(), this.mDetails.getStatusHints(), this.mDetails.getExtras(), this.mDetails.getIntentExtras(), this.mDetails.getCreationTimeMillis(), this.mDetails.getContactDisplayName(), this.mDetails.getCallDirection(), this.mDetails.getCallerNumberVerificationStatus(), this.mDetails.getContactPhotoUri());
                this.mDetails = details2;
                fireDetailsChanged(details2);
            }
            fireStateChanged(this.mState);
            fireCallDestroyed();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void internalOnConnectionEvent(String event, Bundle extras) {
        fireOnConnectionEvent(event, extras);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void internalOnRttUpgradeRequest(final int requestId) {
        for (CallbackRecord<Callback> record : this.mCallbackRecords) {
            final Callback callback = record.getCallback();
            record.getHandler().post(new Runnable() { // from class: android.telecom.Call$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    Call.Callback.this.onRttRequest(call, requestId);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void internalOnRttInitiationFailure(final int reason) {
        for (CallbackRecord<Callback> record : this.mCallbackRecords) {
            final Callback callback = record.getCallback();
            record.getHandler().post(new Runnable() { // from class: android.telecom.Call$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    Call.Callback.this.onRttInitiationFailure(call, reason);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void internalOnHandoverFailed(final int error) {
        for (CallbackRecord<Callback> record : this.mCallbackRecords) {
            final Callback callback = record.getCallback();
            record.getHandler().post(new Runnable() { // from class: android.telecom.Call$$ExternalSyntheticLambda5
                @Override // java.lang.Runnable
                public final void run() {
                    Call.Callback.this.onHandoverFailed(call, error);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void internalOnHandoverComplete() {
        for (CallbackRecord<Callback> record : this.mCallbackRecords) {
            final Callback callback = record.getCallback();
            record.getHandler().post(new Runnable() { // from class: android.telecom.Call$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    Call.Callback.this.onHandoverComplete(call);
                }
            });
        }
    }

    private void fireStateChanged(final int newState) {
        for (CallbackRecord<Callback> record : this.mCallbackRecords) {
            final Callback callback = record.getCallback();
            record.getHandler().post(new Runnable() { // from class: android.telecom.Call.1
                @Override // java.lang.Runnable
                public void run() {
                    callback.onStateChanged(call, newState);
                }
            });
        }
    }

    private void fireParentChanged(final Call newParent) {
        for (CallbackRecord<Callback> record : this.mCallbackRecords) {
            final Callback callback = record.getCallback();
            record.getHandler().post(new Runnable() { // from class: android.telecom.Call.2
                @Override // java.lang.Runnable
                public void run() {
                    callback.onParentChanged(call, newParent);
                }
            });
        }
    }

    private void fireChildrenChanged(final List<Call> children) {
        for (CallbackRecord<Callback> record : this.mCallbackRecords) {
            final Callback callback = record.getCallback();
            record.getHandler().post(new Runnable() { // from class: android.telecom.Call.3
                @Override // java.lang.Runnable
                public void run() {
                    callback.onChildrenChanged(call, children);
                }
            });
        }
    }

    private void fireDetailsChanged(final Details details) {
        for (CallbackRecord<Callback> record : this.mCallbackRecords) {
            final Callback callback = record.getCallback();
            record.getHandler().post(new Runnable() { // from class: android.telecom.Call.4
                @Override // java.lang.Runnable
                public void run() {
                    callback.onDetailsChanged(call, details);
                }
            });
        }
    }

    private void fireCannedTextResponsesLoaded(final List<String> cannedTextResponses) {
        for (CallbackRecord<Callback> record : this.mCallbackRecords) {
            final Callback callback = record.getCallback();
            record.getHandler().post(new Runnable() { // from class: android.telecom.Call.5
                @Override // java.lang.Runnable
                public void run() {
                    callback.onCannedTextResponsesLoaded(call, cannedTextResponses);
                }
            });
        }
    }

    private void fireVideoCallChanged(final InCallService.VideoCall videoCall) {
        for (CallbackRecord<Callback> record : this.mCallbackRecords) {
            final Callback callback = record.getCallback();
            record.getHandler().post(new Runnable() { // from class: android.telecom.Call.6
                @Override // java.lang.Runnable
                public void run() {
                    callback.onVideoCallChanged(call, videoCall);
                }
            });
        }
    }

    private void firePostDialWait(final String remainingPostDialSequence) {
        for (CallbackRecord<Callback> record : this.mCallbackRecords) {
            final Callback callback = record.getCallback();
            record.getHandler().post(new Runnable() { // from class: android.telecom.Call.7
                @Override // java.lang.Runnable
                public void run() {
                    callback.onPostDialWait(call, remainingPostDialSequence);
                }
            });
        }
    }

    private void fireCallDestroyed() {
        if (this.mCallbackRecords.isEmpty()) {
            this.mPhone.internalRemoveCall(this);
        }
        for (final CallbackRecord<Callback> record : this.mCallbackRecords) {
            final Callback callback = record.getCallback();
            record.getHandler().post(new Runnable() { // from class: android.telecom.Call.8
                @Override // java.lang.Runnable
                public void run() {
                    boolean isFinalRemoval = false;
                    RuntimeException toThrow = null;
                    try {
                        callback.onCallDestroyed(call);
                    } catch (RuntimeException e) {
                        toThrow = e;
                    }
                    synchronized (Call.this) {
                        Call.this.mCallbackRecords.remove(record);
                        if (Call.this.mCallbackRecords.isEmpty()) {
                            isFinalRemoval = true;
                        }
                    }
                    if (isFinalRemoval) {
                        Call.this.mPhone.internalRemoveCall(call);
                    }
                    if (toThrow != null) {
                        throw toThrow;
                    }
                }
            });
        }
    }

    private void fireConferenceableCallsChanged() {
        for (CallbackRecord<Callback> record : this.mCallbackRecords) {
            final Callback callback = record.getCallback();
            record.getHandler().post(new Runnable() { // from class: android.telecom.Call.9
                @Override // java.lang.Runnable
                public void run() {
                    callback.onConferenceableCallsChanged(call, Call.this.mUnmodifiableConferenceableCalls);
                }
            });
        }
    }

    private void fireOnConnectionEvent(final String event, final Bundle extras) {
        for (CallbackRecord<Callback> record : this.mCallbackRecords) {
            final Callback callback = record.getCallback();
            record.getHandler().post(new Runnable() { // from class: android.telecom.Call.10
                @Override // java.lang.Runnable
                public void run() {
                    callback.onConnectionEvent(call, event, extras);
                }
            });
        }
    }

    private void fireOnIsRttChanged(final boolean enabled, final RttCall rttCall) {
        for (CallbackRecord<Callback> record : this.mCallbackRecords) {
            final Callback callback = record.getCallback();
            record.getHandler().post(new Runnable() { // from class: android.telecom.Call$$ExternalSyntheticLambda4
                @Override // java.lang.Runnable
                public final void run() {
                    Call.Callback.this.onRttStatusChanged(call, enabled, rttCall);
                }
            });
        }
    }

    private void fireOnRttModeChanged(final int mode) {
        for (CallbackRecord<Callback> record : this.mCallbackRecords) {
            final Callback callback = record.getCallback();
            record.getHandler().post(new Runnable() { // from class: android.telecom.Call$$ExternalSyntheticLambda3
                @Override // java.lang.Runnable
                public final void run() {
                    Call.Callback.this.onRttModeChanged(call, mode);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean areBundlesEqual(Bundle bundle, Bundle newBundle) {
        if (bundle == null || newBundle == null) {
            return bundle == newBundle;
        } else if (bundle.size() != newBundle.size()) {
            return false;
        } else {
            for (String key : bundle.keySet()) {
                if (key != null) {
                    Object value = bundle.get(key);
                    Object newValue = newBundle.get(key);
                    if (!newBundle.containsKey(key)) {
                        return false;
                    }
                    if ((value instanceof Bundle) && (newValue instanceof Bundle) && !areBundlesEqual((Bundle) value, (Bundle) newValue)) {
                        return false;
                    }
                    if ((value instanceof byte[]) && (newValue instanceof byte[])) {
                        if (!Arrays.equals((byte[]) value, (byte[]) newValue)) {
                            return false;
                        }
                    } else if (!Objects.equals(value, newValue)) {
                        return false;
                    }
                }
            }
            return true;
        }
    }
}
