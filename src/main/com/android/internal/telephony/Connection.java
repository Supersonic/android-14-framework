package com.android.internal.telephony;

import android.compat.annotation.UnsupportedAppUsage;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.telecom.Connection;
import android.telephony.emergency.EmergencyNumber;
import android.telephony.ims.RtpHeaderExtension;
import com.android.ims.internal.ConferenceParticipant;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.telephony.Call;
import com.android.internal.telephony.emergency.EmergencyNumberTracker;
import com.android.internal.telephony.util.TelephonyUtils;
import com.android.telephony.Rlog;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
/* loaded from: classes.dex */
public abstract class Connection {
    public static final String ADHOC_CONFERENCE_ADDRESS = "tel:conf-factory";
    public static final int AUDIO_QUALITY_HIGH_DEFINITION = 2;
    public static final int AUDIO_QUALITY_STANDARD = 1;
    @UnsupportedAppUsage
    private static String LOG_TAG = "Connection";
    public static final float THRESHOLD = 0.01f;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    protected String mAddress;
    private boolean mAllowAddCallDuringVideoCall;
    private boolean mAllowHoldingVideoCall;
    private boolean mAnsweringDisconnectsActiveCall;
    protected int mAudioCodec;
    protected float mAudioCodecBandwidthKhz;
    protected float mAudioCodecBitrateKbps;
    private boolean mAudioModeIsVoip;
    private int mAudioQuality;
    private int mCallSubstate;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    protected String mCnapName;
    protected long mConnectTime;
    protected long mConnectTimeReal;
    private int mConnectionCapabilities;
    protected String mConvertedNumber;
    protected long mCreateTime;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    protected String mDialString;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    protected long mDuration;
    private EmergencyNumber mEmergencyNumberInfo;
    private Bundle mExtras;
    private boolean mHasKnownUserIntentEmergency;
    protected long mHoldingStartTime;
    protected boolean mIsAdhocConference;
    private boolean mIsEmergencyCall;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    protected boolean mIsIncoming;
    private boolean mIsNetworkIdentifiedEmergencyCall;
    protected int mNextPostDialChar;
    protected Connection mOrigConnection;
    protected String[] mParticipantsToDial;
    private int mPhoneType;
    protected String mPostDialString;
    private int mPulledDialogId;
    private String mTelecomCallId;
    Object mUserData;
    private Connection.VideoProvider mVideoProvider;
    private int mVideoState;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    protected int mCnapNamePresentation = 1;
    protected int mNumberVerificationStatus = 0;
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    protected int mNumberPresentation = 1;
    private List<PostDialListener> mPostDialListeners = new ArrayList();
    public Set<Listener> mListeners = new CopyOnWriteArraySet();
    protected boolean mNumberConverted = false;
    protected ArrayList<String> mForwardedNumber = null;
    protected int mCause = 0;
    protected PostDialState mPostDialState = PostDialState.NOT_STARTED;
    private int mCallRadioTech = 0;
    public Call.State mPreHandoverState = Call.State.IDLE;
    private boolean mIsPulledCall = false;
    private PhoneFactoryProxy mPhoneFactoryProxy = new PhoneFactoryProxy() { // from class: com.android.internal.telephony.Connection.1
        @Override // com.android.internal.telephony.Connection.PhoneFactoryProxy
        public Phone getPhone(int i) {
            return PhoneFactory.getPhone(i);
        }

        @Override // com.android.internal.telephony.Connection.PhoneFactoryProxy
        public Phone getDefaultPhone() {
            return PhoneFactory.getDefaultPhone();
        }

        @Override // com.android.internal.telephony.Connection.PhoneFactoryProxy
        public Phone[] getPhones() {
            return PhoneFactory.getPhones();
        }
    };

    /* loaded from: classes.dex */
    public static class Capability {
        public static final int IS_EXTERNAL_CONNECTION = 16;
        public static final int IS_PULLABLE = 32;
        public static final int SUPPORTS_DOWNGRADE_TO_VOICE_LOCAL = 1;
        public static final int SUPPORTS_DOWNGRADE_TO_VOICE_REMOTE = 2;
        public static final int SUPPORTS_RTT_REMOTE = 64;
        public static final int SUPPORTS_VT_LOCAL_BIDIRECTIONAL = 4;
        public static final int SUPPORTS_VT_REMOTE_BIDIRECTIONAL = 8;
    }

    /* loaded from: classes.dex */
    public interface Listener {
        void onAudioModeIsVoipChanged(int i);

        void onAudioQualityChanged(int i);

        void onCallPullFailed(Connection connection);

        void onCallRadioTechChanged(int i);

        void onCallSubstateChanged(int i);

        void onConferenceMergedFailed();

        void onConferenceParticipantsChanged(List<ConferenceParticipant> list);

        void onConnectionCapabilitiesChanged(int i);

        void onConnectionEvent(String str, Bundle bundle);

        void onDisconnect(int i);

        void onExitedEcmMode();

        void onExtrasChanged(Bundle bundle);

        void onHandoverToWifiFailed();

        void onIsNetworkEmergencyCallChanged(boolean z);

        void onMediaAttributesChanged();

        void onMultipartyStateChanged(boolean z);

        void onOriginalConnectionReplaced(Connection connection);

        void onReceivedDtmfDigit(char c);

        void onReceivedRtpHeaderExtensions(Set<RtpHeaderExtension> set);

        void onRttInitiated();

        void onRttModifyRequestReceived();

        void onRttModifyResponseReceived(int i);

        void onRttTerminated();

        void onVideoProviderChanged(Connection.VideoProvider videoProvider);

        void onVideoStateChanged(int i);
    }

    /* loaded from: classes.dex */
    public static abstract class ListenerBase implements Listener {
        @Override // com.android.internal.telephony.Connection.Listener
        public void onAudioModeIsVoipChanged(int i) {
        }

        @Override // com.android.internal.telephony.Connection.Listener
        public void onAudioQualityChanged(int i) {
        }

        @Override // com.android.internal.telephony.Connection.Listener
        public void onCallPullFailed(Connection connection) {
        }

        @Override // com.android.internal.telephony.Connection.Listener
        public void onCallRadioTechChanged(int i) {
        }

        @Override // com.android.internal.telephony.Connection.Listener
        public void onCallSubstateChanged(int i) {
        }

        @Override // com.android.internal.telephony.Connection.Listener
        public void onConferenceMergedFailed() {
        }

        @Override // com.android.internal.telephony.Connection.Listener
        public void onConferenceParticipantsChanged(List<ConferenceParticipant> list) {
        }

        @Override // com.android.internal.telephony.Connection.Listener
        public void onConnectionCapabilitiesChanged(int i) {
        }

        @Override // com.android.internal.telephony.Connection.Listener
        public void onConnectionEvent(String str, Bundle bundle) {
        }

        @Override // com.android.internal.telephony.Connection.Listener
        public void onDisconnect(int i) {
        }

        @Override // com.android.internal.telephony.Connection.Listener
        public void onExitedEcmMode() {
        }

        @Override // com.android.internal.telephony.Connection.Listener
        public void onExtrasChanged(Bundle bundle) {
        }

        @Override // com.android.internal.telephony.Connection.Listener
        public void onHandoverToWifiFailed() {
        }

        @Override // com.android.internal.telephony.Connection.Listener
        public void onIsNetworkEmergencyCallChanged(boolean z) {
        }

        @Override // com.android.internal.telephony.Connection.Listener
        public void onMediaAttributesChanged() {
        }

        @Override // com.android.internal.telephony.Connection.Listener
        public void onMultipartyStateChanged(boolean z) {
        }

        @Override // com.android.internal.telephony.Connection.Listener
        public void onOriginalConnectionReplaced(Connection connection) {
        }

        @Override // com.android.internal.telephony.Connection.Listener
        public void onReceivedDtmfDigit(char c) {
        }

        @Override // com.android.internal.telephony.Connection.Listener
        public void onReceivedRtpHeaderExtensions(Set<RtpHeaderExtension> set) {
        }

        @Override // com.android.internal.telephony.Connection.Listener
        public void onRttInitiated() {
        }

        @Override // com.android.internal.telephony.Connection.Listener
        public void onRttModifyRequestReceived() {
        }

        @Override // com.android.internal.telephony.Connection.Listener
        public void onRttModifyResponseReceived(int i) {
        }

        @Override // com.android.internal.telephony.Connection.Listener
        public void onRttTerminated() {
        }

        @Override // com.android.internal.telephony.Connection.Listener
        public void onVideoProviderChanged(Connection.VideoProvider videoProvider) {
        }

        @Override // com.android.internal.telephony.Connection.Listener
        public void onVideoStateChanged(int i) {
        }
    }

    @VisibleForTesting
    /* loaded from: classes.dex */
    public interface PhoneFactoryProxy {
        Phone getDefaultPhone();

        Phone getPhone(int i);

        Phone[] getPhones();
    }

    /* loaded from: classes.dex */
    public interface PostDialListener {
        void onPostDialChar(char c);

        void onPostDialWait();
    }

    /* loaded from: classes.dex */
    public enum PostDialState {
        NOT_STARTED,
        STARTED,
        WAIT,
        WILD,
        COMPLETE,
        CANCELLED,
        PAUSE
    }

    public static int addCapability(int i, int i2) {
        return i | i2;
    }

    public static int removeCapability(int i, int i2) {
        return i & (~i2);
    }

    public abstract void cancelPostDial();

    public abstract void consultativeTransfer(Connection connection) throws CallStateException;

    public abstract void deflect(String str) throws CallStateException;

    @UnsupportedAppUsage
    public abstract Call getCall();

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public abstract long getDisconnectTime();

    public abstract long getHoldDurationMillis();

    public abstract int getNumberPresentation();

    public String getOrigDialString() {
        return null;
    }

    public abstract int getPreciseDisconnectCause();

    public abstract UUSInfo getUUSInfo();

    public abstract String getVendorDisconnectCause();

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public abstract void hangup() throws CallStateException;

    public boolean isConferenceHost() {
        return false;
    }

    public boolean isMemberOfPeerConference() {
        return false;
    }

    public abstract boolean isMultiparty();

    public boolean onDisconnect(int i) {
        return false;
    }

    public void onDisconnectConferenceParticipant(Uri uri) {
    }

    public abstract void proceedAfterWaitChar();

    public abstract void proceedAfterWildChar(String str);

    public void pullExternalCall() {
    }

    public abstract void separate() throws CallStateException;

    public abstract void transfer(String str, boolean z) throws CallStateException;

    /* JADX INFO: Access modifiers changed from: protected */
    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public Connection(int i) {
        this.mPhoneType = i;
    }

    @VisibleForTesting
    public void setPhoneFactoryProxy(PhoneFactoryProxy phoneFactoryProxy) {
        this.mPhoneFactoryProxy = phoneFactoryProxy;
    }

    public String getTelecomCallId() {
        return this.mTelecomCallId;
    }

    public void setTelecomCallId(String str) {
        this.mTelecomCallId = str;
    }

    @UnsupportedAppUsage
    public String getAddress() {
        return this.mAddress;
    }

    public String[] getParticipantsToDial() {
        return this.mParticipantsToDial;
    }

    public boolean isAdhocConference() {
        return this.mIsAdhocConference;
    }

    public ArrayList<String> getForwardedNumber() {
        return this.mForwardedNumber;
    }

    public String getCnapName() {
        return this.mCnapName;
    }

    @VisibleForTesting
    public String getConvertedNumber() {
        return this.mConvertedNumber;
    }

    public int getCnapNamePresentation() {
        return this.mCnapNamePresentation;
    }

    @UnsupportedAppUsage
    public long getCreateTime() {
        return this.mCreateTime;
    }

    @UnsupportedAppUsage
    public long getConnectTime() {
        return this.mConnectTime;
    }

    public void setConnectTime(long j) {
        this.mConnectTime = j;
    }

    public void setConnectTimeReal(long j) {
        this.mConnectTimeReal = j;
    }

    public long getConnectTimeReal() {
        return this.mConnectTimeReal;
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public long getDurationMillis() {
        if (this.mConnectTimeReal == 0) {
            return 0L;
        }
        long j = this.mDuration;
        return j == 0 ? SystemClock.elapsedRealtime() - this.mConnectTimeReal : j;
    }

    public long getHoldingStartTime() {
        return this.mHoldingStartTime;
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public int getDisconnectCause() {
        return this.mCause;
    }

    @UnsupportedAppUsage
    public boolean isIncoming() {
        return this.mIsIncoming;
    }

    public void setIsIncoming(boolean z) {
        this.mIsIncoming = z;
    }

    public boolean isEmergencyCall() {
        return this.mIsEmergencyCall;
    }

    public EmergencyNumber getEmergencyNumberInfo() {
        return this.mEmergencyNumberInfo;
    }

    public boolean hasKnownUserIntentEmergency() {
        return this.mHasKnownUserIntentEmergency;
    }

    public void setEmergencyCallInfo(CallTracker callTracker) {
        EmergencyNumber emergencyNumber;
        if (callTracker != null) {
            Phone phone = callTracker.getPhone();
            if (phone != null) {
                EmergencyNumberTracker emergencyNumberTracker = phone.getEmergencyNumberTracker();
                if (emergencyNumberTracker != null) {
                    EmergencyNumber emergencyNumber2 = emergencyNumberTracker.getEmergencyNumber(this.mAddress);
                    Phone[] phones = this.mPhoneFactoryProxy.getPhones();
                    boolean z = true;
                    if (emergencyNumber2 != null) {
                        this.mIsEmergencyCall = true;
                        this.mEmergencyNumberInfo = emergencyNumber2;
                        return;
                    } else if (phones.length > 1) {
                        int length = phones.length;
                        int i = 0;
                        while (true) {
                            if (i >= length) {
                                z = false;
                                break;
                            }
                            Phone phone2 = phones[i];
                            if (phone2.getPhoneId() != phone.getPhoneId() && (emergencyNumber = phone2.getEmergencyNumberTracker().getEmergencyNumber(this.mAddress)) != null) {
                                this.mIsEmergencyCall = true;
                                this.mEmergencyNumberInfo = emergencyNumber;
                                break;
                            }
                            i++;
                        }
                        if (z) {
                            return;
                        }
                        Rlog.e("Connection", "setEmergencyCallInfo: emergency number is null");
                        return;
                    } else {
                        Rlog.e("Connection", "setEmergencyCallInfo: emergency number is null");
                        return;
                    }
                }
                Rlog.e("Connection", "setEmergencyCallInfo: emergency number tracker is null");
                return;
            }
            Rlog.e("Connection", "setEmergencyCallInfo: phone is null");
            return;
        }
        Rlog.e("Connection", "setEmergencyCallInfo: call tracker is null");
    }

    public void setNonDetectableEmergencyCallInfo(int i) {
        if (this.mIsEmergencyCall) {
            return;
        }
        this.mIsEmergencyCall = true;
        this.mEmergencyNumberInfo = new EmergencyNumber(this.mAddress, PhoneConfigurationManager.SSSS, PhoneConfigurationManager.SSSS, i, new ArrayList(), 1, 0);
    }

    public void setHasKnownUserIntentEmergency(boolean z) {
        this.mHasKnownUserIntentEmergency = z;
    }

    @UnsupportedAppUsage
    public Call.State getState() {
        Call call = getCall();
        if (call == null) {
            return Call.State.IDLE;
        }
        return call.getState();
    }

    public Call.State getStateBeforeHandover() {
        return this.mPreHandoverState;
    }

    public List<ConferenceParticipant> getConferenceParticipants() {
        Call call = getCall();
        if (call == null) {
            return null;
        }
        return call.getConferenceParticipants();
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public boolean isAlive() {
        return getState().isAlive();
    }

    public boolean isRinging() {
        return getState().isRinging();
    }

    @UnsupportedAppUsage
    public Object getUserData() {
        return this.mUserData;
    }

    public void setUserData(Object obj) {
        this.mUserData = obj;
    }

    public void clearUserData() {
        this.mUserData = null;
    }

    public void addPostDialListener(PostDialListener postDialListener) {
        if (this.mPostDialListeners.contains(postDialListener)) {
            return;
        }
        this.mPostDialListeners.add(postDialListener);
    }

    public final void removePostDialListener(PostDialListener postDialListener) {
        this.mPostDialListeners.remove(postDialListener);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final void clearPostDialListeners() {
        List<PostDialListener> list = this.mPostDialListeners;
        if (list != null) {
            list.clear();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final void notifyPostDialListeners() {
        if (getPostDialState() == PostDialState.WAIT) {
            Iterator it = new ArrayList(this.mPostDialListeners).iterator();
            while (it.hasNext()) {
                ((PostDialListener) it.next()).onPostDialWait();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final void notifyPostDialListenersNextChar(char c) {
        Iterator it = new ArrayList(this.mPostDialListeners).iterator();
        while (it.hasNext()) {
            ((PostDialListener) it.next()).onPostDialChar(c);
        }
    }

    public PostDialState getPostDialState() {
        return this.mPostDialState;
    }

    public String getRemainingPostDialString() {
        String str;
        PostDialState postDialState = this.mPostDialState;
        if (postDialState == PostDialState.CANCELLED || postDialState == PostDialState.COMPLETE || (str = this.mPostDialString) == null) {
            return PhoneConfigurationManager.SSSS;
        }
        int length = str.length();
        int i = this.mNextPostDialChar;
        return length <= i ? PhoneConfigurationManager.SSSS : this.mPostDialString.substring(i);
    }

    public Connection getOrigConnection() {
        return this.mOrigConnection;
    }

    public void migrateFrom(Connection connection) {
        if (connection == null) {
            return;
        }
        this.mListeners = connection.mListeners;
        this.mDialString = connection.getOrigDialString();
        this.mCreateTime = connection.getCreateTime();
        this.mConnectTime = connection.getConnectTime();
        this.mConnectTimeReal = connection.getConnectTimeReal();
        this.mHoldingStartTime = connection.getHoldingStartTime();
        this.mOrigConnection = connection.getOrigConnection();
        this.mPostDialString = connection.mPostDialString;
        this.mNextPostDialChar = connection.mNextPostDialChar;
        this.mPostDialState = connection.mPostDialState;
        this.mIsEmergencyCall = connection.isEmergencyCall();
        this.mEmergencyNumberInfo = connection.getEmergencyNumberInfo();
        this.mHasKnownUserIntentEmergency = connection.hasKnownUserIntentEmergency();
    }

    public void addListener(Listener listener) {
        this.mListeners.add(listener);
    }

    public final void removeListener(Listener listener) {
        this.mListeners.remove(listener);
    }

    public int getVideoState() {
        return this.mVideoState;
    }

    public int getConnectionCapabilities() {
        return this.mConnectionCapabilities;
    }

    public boolean hasCapabilities(int i) {
        return (this.mConnectionCapabilities & i) == i;
    }

    public boolean isWifi() {
        return getCallRadioTech() == 18;
    }

    public int getCallRadioTech() {
        return this.mCallRadioTech;
    }

    public boolean getAudioModeIsVoip() {
        return this.mAudioModeIsVoip;
    }

    public Connection.VideoProvider getVideoProvider() {
        return this.mVideoProvider;
    }

    public int getAudioQuality() {
        return this.mAudioQuality;
    }

    public int getCallSubstate() {
        return this.mCallSubstate;
    }

    @UnsupportedAppUsage(maxTargetSdk = 30, trackingBug = 170729553)
    public void setVideoState(int i) {
        this.mVideoState = i;
        for (Listener listener : this.mListeners) {
            listener.onVideoStateChanged(this.mVideoState);
        }
    }

    public void setConnectionCapabilities(int i) {
        if (this.mConnectionCapabilities != i) {
            this.mConnectionCapabilities = i;
            for (Listener listener : this.mListeners) {
                listener.onConnectionCapabilitiesChanged(this.mConnectionCapabilities);
            }
        }
    }

    public void setCallRadioTech(int i) {
        if (this.mCallRadioTech == i) {
            return;
        }
        this.mCallRadioTech = i;
        for (Listener listener : this.mListeners) {
            listener.onCallRadioTechChanged(i);
        }
    }

    public void setAudioModeIsVoip(boolean z) {
        this.mAudioModeIsVoip = z;
    }

    public void setAudioQuality(int i) {
        this.mAudioQuality = i;
        for (Listener listener : this.mListeners) {
            listener.onAudioQualityChanged(this.mAudioQuality);
        }
    }

    public void notifyMediaAttributesChanged() {
        for (Listener listener : this.mListeners) {
            listener.onMediaAttributesChanged();
        }
    }

    public void setConnectionExtras(Bundle bundle) {
        if (bundle != null) {
            Bundle bundle2 = new Bundle(bundle);
            this.mExtras = bundle2;
            int size = bundle2.size();
            Bundle filterValues = TelephonyUtils.filterValues(this.mExtras);
            this.mExtras = filterValues;
            int size2 = filterValues.size();
            if (size2 != size) {
                Rlog.i("Connection", "setConnectionExtras: filtering " + (size - size2) + " invalid extras.");
            }
        } else {
            this.mExtras = null;
        }
        for (Listener listener : this.mListeners) {
            listener.onExtrasChanged(this.mExtras);
        }
    }

    public Bundle getConnectionExtras() {
        if (this.mExtras == null) {
            return null;
        }
        return new Bundle(this.mExtras);
    }

    public boolean isActiveCallDisconnectedOnAnswer() {
        return this.mAnsweringDisconnectsActiveCall;
    }

    public void setActiveCallDisconnectedOnAnswer(boolean z) {
        this.mAnsweringDisconnectsActiveCall = z;
    }

    public boolean shouldAllowAddCallDuringVideoCall() {
        return this.mAllowAddCallDuringVideoCall;
    }

    public void setAllowAddCallDuringVideoCall(boolean z) {
        this.mAllowAddCallDuringVideoCall = z;
    }

    public boolean shouldAllowHoldingVideoCall() {
        return this.mAllowHoldingVideoCall;
    }

    public void setAllowHoldingVideoCall(boolean z) {
        this.mAllowHoldingVideoCall = z;
    }

    public void setIsPulledCall(boolean z) {
        this.mIsPulledCall = z;
    }

    public boolean isPulledCall() {
        return this.mIsPulledCall;
    }

    public void setPulledDialogId(int i) {
        this.mPulledDialogId = i;
    }

    public int getPulledDialogId() {
        return this.mPulledDialogId;
    }

    public void setCallSubstate(int i) {
        this.mCallSubstate = i;
        for (Listener listener : this.mListeners) {
            listener.onCallSubstateChanged(this.mCallSubstate);
        }
    }

    public void setVideoProvider(Connection.VideoProvider videoProvider) {
        this.mVideoProvider = videoProvider;
        for (Listener listener : this.mListeners) {
            listener.onVideoProviderChanged(this.mVideoProvider);
        }
    }

    public void restoreDialedNumberAfterConversion(String str) {
        this.mNumberConverted = true;
        this.mConvertedNumber = this.mAddress;
        this.mAddress = str;
        this.mDialString = str;
    }

    public void setAddress(String str, int i) {
        Rlog.i("Connection", "setAddress = " + str);
        this.mAddress = str;
        this.mNumberPresentation = i;
    }

    public void setDialString(String str) {
        this.mDialString = str;
    }

    public void updateConferenceParticipants(List<ConferenceParticipant> list) {
        for (Listener listener : this.mListeners) {
            listener.onConferenceParticipantsChanged(list);
        }
    }

    public void updateMultipartyState(boolean z) {
        for (Listener listener : this.mListeners) {
            listener.onMultipartyStateChanged(z);
        }
    }

    public void onConferenceMergeFailed() {
        for (Listener listener : this.mListeners) {
            listener.onConferenceMergedFailed();
        }
    }

    public void onExitedEcmMode() {
        for (Listener listener : this.mListeners) {
            listener.onExitedEcmMode();
        }
    }

    public void onCallPullFailed(Connection connection) {
        for (Listener listener : this.mListeners) {
            listener.onCallPullFailed(connection);
        }
    }

    public void onOriginalConnectionReplaced(Connection connection) {
        for (Listener listener : this.mListeners) {
            listener.onOriginalConnectionReplaced(connection);
        }
    }

    public void onHandoverToWifiFailed() {
        for (Listener listener : this.mListeners) {
            listener.onHandoverToWifiFailed();
        }
    }

    public void onConnectionEvent(String str, Bundle bundle) {
        for (Listener listener : this.mListeners) {
            listener.onConnectionEvent(str, bundle);
        }
    }

    public void onRttModifyRequestReceived() {
        for (Listener listener : this.mListeners) {
            listener.onRttModifyRequestReceived();
        }
    }

    public void onRttModifyResponseReceived(int i) {
        for (Listener listener : this.mListeners) {
            listener.onRttModifyResponseReceived(i);
        }
    }

    public void onRttInitiated() {
        for (Listener listener : this.mListeners) {
            listener.onRttInitiated();
        }
    }

    public void onRttTerminated() {
        for (Listener listener : this.mListeners) {
            listener.onRttTerminated();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void notifyDisconnect(int i) {
        Rlog.i("Connection", "notifyDisconnect: callId=" + getTelecomCallId() + ", reason=" + i);
        for (Listener listener : this.mListeners) {
            listener.onDisconnect(i);
        }
    }

    public int getPhoneType() {
        return this.mPhoneType;
    }

    public void resetConnectionTime() {
        int i = this.mPhoneType;
        if (i == 6 || i == 2) {
            this.mConnectTime = System.currentTimeMillis();
            this.mConnectTimeReal = SystemClock.elapsedRealtime();
            this.mDuration = 0L;
        }
    }

    public void setIsNetworkIdentifiedEmergencyCall(boolean z) {
        this.mIsNetworkIdentifiedEmergencyCall = z;
        for (Listener listener : this.mListeners) {
            listener.onIsNetworkEmergencyCallChanged(z);
        }
    }

    public boolean isNetworkIdentifiedEmergencyCall() {
        return this.mIsNetworkIdentifiedEmergencyCall;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(128);
        sb.append(" callId: " + getTelecomCallId());
        sb.append(" objId: " + System.identityHashCode(this));
        StringBuilder sb2 = new StringBuilder();
        sb2.append(" isExternal: ");
        sb2.append((this.mConnectionCapabilities & 16) == 16 ? "Y" : "N");
        sb.append(sb2.toString());
        if (Rlog.isLoggable(LOG_TAG, 3)) {
            sb.append("addr: " + getAddress());
            sb.append(" pres.: " + getNumberPresentation());
            sb.append(" dial: " + getOrigDialString());
            sb.append(" postdial: " + getRemainingPostDialString());
            sb.append(" cnap name: " + getCnapName());
            sb.append("(" + getCnapNamePresentation() + ")");
        }
        sb.append(" incoming: " + isIncoming());
        sb.append(" state: " + getState());
        sb.append(" post dial state: " + getPostDialState());
        return sb.toString();
    }

    public int getAudioCodec() {
        return this.mAudioCodec;
    }

    public float getAudioCodecBitrateKbps() {
        return this.mAudioCodecBitrateKbps;
    }

    public float getAudioCodecBandwidthKhz() {
        return this.mAudioCodecBandwidthKhz;
    }

    public int getNumberVerificationStatus() {
        return this.mNumberVerificationStatus;
    }

    public void setNumberVerificationStatus(int i) {
        this.mNumberVerificationStatus = i;
    }

    public void receivedDtmfDigit(char c) {
        for (Listener listener : this.mListeners) {
            listener.onReceivedDtmfDigit(c);
        }
    }

    public void onAudioModeIsVoipChanged(int i) {
        Rlog.i("Connection", "onAudioModeIsVoipChanged: conn imsAudioHandler " + i);
        boolean z = i == 0;
        if (z == this.mAudioModeIsVoip) {
            return;
        }
        this.mAudioModeIsVoip = z;
        Rlog.i("Connection", "onAudioModeIsVoipChanged: isVoip: " + z + "mAudioModeIsVoip:" + this.mAudioModeIsVoip);
        for (Listener listener : this.mListeners) {
            listener.onAudioModeIsVoipChanged(i);
        }
    }

    public void receivedRtpHeaderExtensions(Set<RtpHeaderExtension> set) {
        for (Listener listener : this.mListeners) {
            listener.onReceivedRtpHeaderExtensions(set);
        }
    }
}
