package com.android.ims;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.os.Parcel;
import android.telephony.CallQuality;
import android.telephony.ServiceState;
import android.telephony.ims.ImsCallProfile;
import android.telephony.ims.ImsCallSession;
import android.telephony.ims.ImsConferenceState;
import android.telephony.ims.ImsReasonInfo;
import android.telephony.ims.ImsStreamMediaProfile;
import android.telephony.ims.ImsSuppServiceNotification;
import android.telephony.ims.RtpHeaderExtension;
import android.text.TextUtils;
import android.util.Log;
import com.android.ims.internal.ConferenceParticipant;
import com.android.ims.internal.ICall;
import com.android.ims.internal.ImsStreamMediaSession;
import com.android.ims.rcs.uce.presence.pidfparser.pidf.Status;
import com.android.telephony.Rlog;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;
/* loaded from: classes.dex */
public class ImsCall implements ICall {
    private static final boolean CONF_DBG = true;
    private static final boolean FORCE_DEBUG = false;
    private static final int UPDATE_EXTEND_TO_CONFERENCE = 5;
    private static final int UPDATE_HOLD = 1;
    private static final int UPDATE_HOLD_MERGE = 2;
    private static final int UPDATE_MERGE = 4;
    private static final int UPDATE_NONE = 0;
    private static final int UPDATE_RESUME = 3;
    private static final int UPDATE_UNSPECIFIED = 6;
    public static final int USSD_MODE_NOTIFY = 0;
    public static final int USSD_MODE_REQUEST = 1;
    private List<ConferenceParticipant> mConferenceParticipants;
    private Context mContext;
    private ImsCallSessionListenerProxy mImsCallSessionListenerProxy;
    public final int uniqueId;
    private static final String TAG = "ImsCall";
    private static final boolean DBG = Log.isLoggable(TAG, 3);
    private static final boolean VDBG = Log.isLoggable(TAG, 2);
    private static final AtomicInteger sUniqueIdGenerator = new AtomicInteger();
    private Object mLockObj = new Object();
    private boolean mInCall = false;
    private boolean mHold = false;
    private boolean mMute = false;
    private int mUpdateRequest = 0;
    private Listener mListener = null;
    private ImsCall mMergePeer = null;
    private ImsCall mMergeHost = null;
    private boolean mMergeRequestedByConference = false;
    private ImsCallSession mSession = null;
    private ImsCallProfile mCallProfile = null;
    private ImsCallProfile mProposedCallProfile = null;
    private ImsReasonInfo mLastReasonInfo = null;
    private ImsStreamMediaSession mMediaSession = null;
    private ImsCallSession mTransientConferenceSession = null;
    private boolean mSessionEndDuringMerge = false;
    private ImsReasonInfo mSessionEndDuringMergeReasonInfo = null;
    private boolean mIsMerged = false;
    private boolean mCallSessionMergePending = false;
    private boolean mTerminationRequestPending = false;
    private boolean mIsConferenceHost = false;
    private boolean mWasVideoCall = false;
    private int mOverrideReason = 0;
    private boolean mAnswerWithRtt = false;

    /* loaded from: classes.dex */
    public static class Listener {
        public void onCallInitiating(ImsCall call) {
            onCallStateChanged(call);
        }

        public void onCallProgressing(ImsCall call) {
            onCallStateChanged(call);
        }

        public void onCallStarted(ImsCall call) {
            onCallStateChanged(call);
        }

        public void onCallStartFailed(ImsCall call, ImsReasonInfo reasonInfo) {
            onCallError(call, reasonInfo);
        }

        public void onCallTerminated(ImsCall call, ImsReasonInfo reasonInfo) {
            onCallStateChanged(call);
        }

        public void onCallHeld(ImsCall call) {
            onCallStateChanged(call);
        }

        public void onCallHoldFailed(ImsCall call, ImsReasonInfo reasonInfo) {
            onCallError(call, reasonInfo);
        }

        public void onCallHoldReceived(ImsCall call) {
            onCallStateChanged(call);
        }

        public void onCallResumed(ImsCall call) {
            onCallStateChanged(call);
        }

        public void onCallResumeFailed(ImsCall call, ImsReasonInfo reasonInfo) {
            onCallError(call, reasonInfo);
        }

        public void onCallResumeReceived(ImsCall call) {
            onCallStateChanged(call);
        }

        public void onCallMerged(ImsCall call, ImsCall peerCall, boolean swapCalls) {
            onCallStateChanged(call);
        }

        public void onCallMergeFailed(ImsCall call, ImsReasonInfo reasonInfo) {
            onCallError(call, reasonInfo);
        }

        public void onCallUpdated(ImsCall call) {
            onCallStateChanged(call);
        }

        public void onCallUpdateFailed(ImsCall call, ImsReasonInfo reasonInfo) {
            onCallError(call, reasonInfo);
        }

        public void onCallUpdateReceived(ImsCall call) {
        }

        public void onCallConferenceExtended(ImsCall call, ImsCall newCall) {
            onCallStateChanged(call);
        }

        public void onCallConferenceExtendFailed(ImsCall call, ImsReasonInfo reasonInfo) {
            onCallError(call, reasonInfo);
        }

        public void onCallConferenceExtendReceived(ImsCall call, ImsCall newCall) {
            onCallStateChanged(call);
        }

        public void onCallInviteParticipantsRequestDelivered(ImsCall call) {
        }

        public void onCallInviteParticipantsRequestFailed(ImsCall call, ImsReasonInfo reasonInfo) {
        }

        public void onCallRemoveParticipantsRequestDelivered(ImsCall call) {
        }

        public void onCallRemoveParticipantsRequestFailed(ImsCall call, ImsReasonInfo reasonInfo) {
        }

        public void onCallConferenceStateUpdated(ImsCall call, ImsConferenceState state) {
        }

        public void onConferenceParticipantsStateChanged(ImsCall call, List<ConferenceParticipant> participants) {
        }

        public void onCallUssdMessageReceived(ImsCall call, int mode, String ussdMessage) {
        }

        public void onCallError(ImsCall call, ImsReasonInfo reasonInfo) {
        }

        public void onCallStateChanged(ImsCall call) {
        }

        public void onCallStateChanged(ImsCall call, int state) {
        }

        public void onCallSuppServiceReceived(ImsCall call, ImsSuppServiceNotification suppServiceInfo) {
        }

        public void onCallSessionTtyModeReceived(ImsCall call, int mode) {
        }

        public void onCallHandover(ImsCall imsCall, int srcAccessTech, int targetAccessTech, ImsReasonInfo reasonInfo) {
        }

        public void onRttModifyRequestReceived(ImsCall imsCall) {
        }

        public void onRttModifyResponseReceived(ImsCall imsCall, int status) {
        }

        public void onRttMessageReceived(ImsCall imsCall, String message) {
        }

        public void onCallHandoverFailed(ImsCall imsCall, int srcAccessTech, int targetAccessTech, ImsReasonInfo reasonInfo) {
        }

        public void onMultipartyStateChanged(ImsCall imsCall, boolean isMultiParty) {
        }

        public void onRttAudioIndicatorChanged(ImsCall imsCall, ImsStreamMediaProfile profile) {
        }

        public void onCallSessionTransferred(ImsCall imsCall) {
        }

        public void onCallSessionTransferFailed(ImsCall imsCall, ImsReasonInfo reasonInfo) {
        }

        public void onCallSessionDtmfReceived(ImsCall imsCall, char digit) {
        }

        public void onCallQualityChanged(ImsCall imsCall, CallQuality callQuality) {
        }

        public void onCallSessionRtpHeaderExtensionsReceived(ImsCall imsCall, Set<RtpHeaderExtension> rtpHeaderExtensionData) {
        }

        public void onCallSessionSendAnbrQuery(ImsCall imsCall, int mediaType, int direction, int bitsPerSecond) {
        }
    }

    public ImsCall(Context context, ImsCallProfile profile) {
        this.mContext = context;
        setCallProfile(profile);
        this.uniqueId = sUniqueIdGenerator.getAndIncrement();
    }

    @Override // com.android.ims.internal.ICall
    public void close() {
        synchronized (this.mLockObj) {
            ImsCallSession imsCallSession = this.mSession;
            if (imsCallSession != null) {
                imsCallSession.close();
                this.mSession = null;
            } else {
                logi("close :: Cannot close Null call session!");
            }
            this.mCallProfile = null;
            this.mProposedCallProfile = null;
            this.mLastReasonInfo = null;
            this.mMediaSession = null;
        }
    }

    @Override // com.android.ims.internal.ICall
    public boolean checkIfRemoteUserIsSame(String userId) {
        if (userId == null) {
            return false;
        }
        return userId.equals(this.mCallProfile.getCallExtra("remote_uri", ""));
    }

    @Override // com.android.ims.internal.ICall
    public boolean equalsTo(ICall call) {
        if (call == null || !(call instanceof ImsCall)) {
            return false;
        }
        return equals(call);
    }

    public static boolean isSessionAlive(ImsCallSession session) {
        if (session == null || !session.isAlive()) {
            return false;
        }
        return CONF_DBG;
    }

    public ImsCallProfile getCallProfile() {
        ImsCallProfile imsCallProfile;
        synchronized (this.mLockObj) {
            imsCallProfile = this.mCallProfile;
        }
        return imsCallProfile;
    }

    public void setCallProfile(ImsCallProfile profile) {
        synchronized (this.mLockObj) {
            this.mCallProfile = profile;
            trackVideoStateHistory(profile);
        }
    }

    public ImsCallProfile getLocalCallProfile() throws ImsException {
        ImsCallProfile localCallProfile;
        synchronized (this.mLockObj) {
            ImsCallSession imsCallSession = this.mSession;
            if (imsCallSession == null) {
                throw new ImsException("No call session", 148);
            }
            localCallProfile = imsCallSession.getLocalCallProfile();
        }
        return localCallProfile;
    }

    public ImsCallProfile getRemoteCallProfile() throws ImsException {
        ImsCallProfile remoteCallProfile;
        synchronized (this.mLockObj) {
            ImsCallSession imsCallSession = this.mSession;
            if (imsCallSession == null) {
                throw new ImsException("No call session", 148);
            }
            remoteCallProfile = imsCallSession.getRemoteCallProfile();
        }
        return remoteCallProfile;
    }

    public ImsCallProfile getProposedCallProfile() {
        synchronized (this.mLockObj) {
            if (isInCall()) {
                return this.mProposedCallProfile;
            }
            return null;
        }
    }

    public List<ConferenceParticipant> getConferenceParticipants() {
        synchronized (this.mLockObj) {
            logi("getConferenceParticipants :: mConferenceParticipants" + this.mConferenceParticipants);
            List<ConferenceParticipant> list = this.mConferenceParticipants;
            if (list == null) {
                return null;
            }
            if (list.isEmpty()) {
                return new ArrayList(0);
            }
            return new ArrayList(this.mConferenceParticipants);
        }
    }

    public int getState() {
        synchronized (this.mLockObj) {
            ImsCallSession imsCallSession = this.mSession;
            if (imsCallSession == null) {
                return 0;
            }
            return imsCallSession.getState();
        }
    }

    public ImsCallSession getCallSession() {
        ImsCallSession imsCallSession;
        synchronized (this.mLockObj) {
            imsCallSession = this.mSession;
        }
        return imsCallSession;
    }

    public ImsStreamMediaSession getMediaSession() {
        ImsStreamMediaSession imsStreamMediaSession;
        synchronized (this.mLockObj) {
            imsStreamMediaSession = this.mMediaSession;
        }
        return imsStreamMediaSession;
    }

    public void callSessionNotifyAnbr(int mediaType, int direction, int bitsPerSecond) {
        synchronized (this.mLockObj) {
            ImsCallSession imsCallSession = this.mSession;
            if (imsCallSession != null) {
                imsCallSession.callSessionNotifyAnbr(mediaType, direction, bitsPerSecond);
            } else {
                logi("callSessionNotifyAnbr : session - null");
            }
        }
    }

    public String getCallExtra(String name) throws ImsException {
        String property;
        synchronized (this.mLockObj) {
            ImsCallSession imsCallSession = this.mSession;
            if (imsCallSession == null) {
                throw new ImsException("No call session", 148);
            }
            property = imsCallSession.getProperty(name);
        }
        return property;
    }

    public ImsReasonInfo getLastReasonInfo() {
        ImsReasonInfo imsReasonInfo;
        synchronized (this.mLockObj) {
            imsReasonInfo = this.mLastReasonInfo;
        }
        return imsReasonInfo;
    }

    public boolean hasPendingUpdate() {
        boolean z;
        synchronized (this.mLockObj) {
            z = this.mUpdateRequest != 0 ? CONF_DBG : false;
        }
        return z;
    }

    public boolean isPendingHold() {
        boolean z;
        synchronized (this.mLockObj) {
            int i = this.mUpdateRequest;
            z = CONF_DBG;
            if (i != 1) {
                z = false;
            }
        }
        return z;
    }

    public boolean isInCall() {
        boolean z;
        synchronized (this.mLockObj) {
            z = this.mInCall;
        }
        return z;
    }

    public boolean isMuted() {
        boolean z;
        synchronized (this.mLockObj) {
            z = this.mMute;
        }
        return z;
    }

    public boolean isOnHold() {
        boolean z;
        synchronized (this.mLockObj) {
            z = this.mHold;
        }
        return z;
    }

    public boolean isMultiparty() {
        synchronized (this.mLockObj) {
            ImsCallSession imsCallSession = this.mSession;
            if (imsCallSession == null) {
                return false;
            }
            return imsCallSession.isMultiparty();
        }
    }

    public boolean isConferenceHost() {
        boolean z;
        synchronized (this.mLockObj) {
            z = (isMultiparty() && this.mIsConferenceHost) ? CONF_DBG : false;
        }
        return z;
    }

    public void setIsMerged(boolean isMerged) {
        this.mIsMerged = isMerged;
    }

    public boolean isMerged() {
        return this.mIsMerged;
    }

    public void setListener(Listener listener) {
        setListener(listener, false);
    }

    public void setListener(Listener listener, boolean callbackImmediately) {
        synchronized (this.mLockObj) {
            this.mListener = listener;
            if (listener != null && callbackImmediately) {
                boolean inCall = this.mInCall;
                boolean onHold = this.mHold;
                int state = getState();
                ImsReasonInfo lastReasonInfo = this.mLastReasonInfo;
                try {
                    if (lastReasonInfo != null) {
                        listener.onCallError(this, lastReasonInfo);
                    } else if (inCall) {
                        if (onHold) {
                            listener.onCallHeld(this);
                        } else {
                            listener.onCallStarted(this);
                        }
                    } else {
                        switch (state) {
                            case 3:
                                listener.onCallProgressing(this);
                                break;
                            case 8:
                                listener.onCallTerminated(this, lastReasonInfo);
                                break;
                        }
                    }
                } catch (Throwable t) {
                    loge("setListener() :: ", t);
                }
            }
        }
    }

    public void setMute(boolean muted) throws ImsException {
        synchronized (this.mLockObj) {
            if (this.mMute != muted) {
                logi("setMute :: turning mute " + (muted ? "on" : "off"));
                this.mMute = muted;
                this.mSession.setMute(muted);
            }
        }
    }

    public void attachSession(ImsCallSession session) throws ImsException {
        logi("attachSession :: session=" + session);
        synchronized (this.mLockObj) {
            this.mSession = session;
            session.setListener(createCallSessionListener(), this.mContext.getMainExecutor());
        }
    }

    public void start(ImsCallSession session, String callee) throws ImsException {
        logi("start(1) :: session=" + session);
        synchronized (this.mLockObj) {
            this.mSession = session;
            session.setListener(createCallSessionListener(), this.mContext.getMainExecutor());
            session.start(callee, this.mCallProfile);
        }
    }

    public void start(ImsCallSession session, String[] participants) throws ImsException {
        logi("start(n) :: session=" + session);
        synchronized (this.mLockObj) {
            this.mSession = session;
            this.mIsConferenceHost = CONF_DBG;
            session.setListener(createCallSessionListener(), this.mContext.getMainExecutor());
            session.start(participants, this.mCallProfile);
        }
    }

    public void accept(int callType) throws ImsException {
        accept(callType, new ImsStreamMediaProfile());
    }

    public void accept(int callType, ImsStreamMediaProfile profile) throws ImsException {
        logi("accept :: callType=" + callType + ", profile=" + profile);
        if (this.mAnswerWithRtt) {
            profile.mRttMode = 1;
            logi("accept :: changing media profile RTT mode to full");
        }
        synchronized (this.mLockObj) {
            ImsCallSession imsCallSession = this.mSession;
            if (imsCallSession == null) {
                throw new ImsException("No call to answer", 148);
            }
            imsCallSession.accept(callType, profile);
            if (this.mInCall && this.mProposedCallProfile != null) {
                if (DBG) {
                    logi("accept :: call profile will be updated");
                }
                ImsCallProfile imsCallProfile = this.mProposedCallProfile;
                this.mCallProfile = imsCallProfile;
                trackVideoStateHistory(imsCallProfile);
                this.mProposedCallProfile = null;
            }
            if (this.mInCall && this.mUpdateRequest == 6) {
                this.mUpdateRequest = 0;
            }
        }
    }

    public void deflect(String number) throws ImsException {
        logi("deflect :: session=" + this.mSession + ", number=" + Rlog.pii(TAG, number));
        synchronized (this.mLockObj) {
            ImsCallSession imsCallSession = this.mSession;
            if (imsCallSession == null) {
                throw new ImsException("No call to deflect", 148);
            }
            imsCallSession.deflect(number);
        }
    }

    public void reject(int reason) throws ImsException {
        logi("reject :: reason=" + reason);
        synchronized (this.mLockObj) {
            ImsCallSession imsCallSession = this.mSession;
            if (imsCallSession != null) {
                imsCallSession.reject(reason);
            }
            if (this.mInCall && this.mProposedCallProfile != null) {
                if (DBG) {
                    logi("reject :: call profile is not updated; destroy it...");
                }
                this.mProposedCallProfile = null;
            }
            if (this.mInCall && this.mUpdateRequest == 6) {
                this.mUpdateRequest = 0;
            }
        }
    }

    public void transfer(String number, boolean isConfirmationRequired) throws ImsException {
        logi("transfer :: session=" + this.mSession + ", number=" + Rlog.pii(TAG, number) + ", isConfirmationRequired=" + isConfirmationRequired);
        synchronized (this.mLockObj) {
            ImsCallSession imsCallSession = this.mSession;
            if (imsCallSession == null) {
                throw new ImsException("No call to transfer", 148);
            }
            imsCallSession.transfer(number, isConfirmationRequired);
        }
    }

    public void consultativeTransfer(ImsCall transferToImsCall) throws ImsException {
        logi("consultativeTransfer :: session=" + this.mSession + ", other call=" + transferToImsCall);
        synchronized (this.mLockObj) {
            ImsCallSession imsCallSession = this.mSession;
            if (imsCallSession == null) {
                throw new ImsException("No call to transfer", 148);
            }
            imsCallSession.transfer(transferToImsCall.getSession());
        }
    }

    public void terminate(int reason, int overrideReason) {
        logi("terminate :: reason=" + reason + " ; overrideReason=" + overrideReason);
        this.mOverrideReason = overrideReason;
        terminate(reason);
    }

    public void terminate(int reason) {
        logi("terminate :: reason=" + reason);
        synchronized (this.mLockObj) {
            this.mHold = false;
            this.mInCall = false;
            this.mTerminationRequestPending = CONF_DBG;
            ImsCallSession imsCallSession = this.mSession;
            if (imsCallSession != null) {
                imsCallSession.terminate(reason);
            }
        }
    }

    public void hold() throws ImsException {
        logi("hold :: ");
        if (isOnHold()) {
            if (DBG) {
                logi("hold :: call is already on hold");
                return;
            }
            return;
        }
        synchronized (this.mLockObj) {
            if (this.mUpdateRequest != 0) {
                loge("hold :: update is in progress; request=" + updateRequestToString(this.mUpdateRequest));
                throw new ImsException("Call update is in progress", 102);
            }
            ImsCallSession imsCallSession = this.mSession;
            if (imsCallSession == null) {
                throw new ImsException("No call session", 148);
            }
            this.mHold = CONF_DBG;
            imsCallSession.hold(createHoldMediaProfile());
            this.mUpdateRequest = 1;
        }
    }

    public void resume() throws ImsException {
        logi("resume :: ");
        if (!isOnHold()) {
            if (DBG) {
                logi("resume :: call is not being held");
                return;
            }
            return;
        }
        synchronized (this.mLockObj) {
            if (this.mUpdateRequest != 0) {
                loge("resume :: update is in progress; request=" + updateRequestToString(this.mUpdateRequest));
                throw new ImsException("Call update is in progress", 102);
            }
            ImsCallSession imsCallSession = this.mSession;
            if (imsCallSession == null) {
                loge("resume :: ");
                throw new ImsException("No call session", 148);
            } else {
                this.mUpdateRequest = 3;
                imsCallSession.resume(createResumeMediaProfile());
            }
        }
    }

    private boolean isUpdatePending(ImsCall imsCall) {
        if (imsCall != null && imsCall.mUpdateRequest != 0) {
            loge("merge :: update is in progress; request=" + updateRequestToString(this.mUpdateRequest));
            return CONF_DBG;
        }
        return false;
    }

    private void merge() throws ImsException {
        logi("merge :: ");
        synchronized (this.mLockObj) {
            if (isUpdatePending(this)) {
                setCallSessionMergePending(false);
                ImsCall imsCall = this.mMergePeer;
                if (imsCall != null) {
                    imsCall.setCallSessionMergePending(false);
                }
                ImsCall imsCall2 = this.mMergeHost;
                if (imsCall2 != null) {
                    imsCall2.setCallSessionMergePending(false);
                }
                throw new ImsException("Call update is in progress", 102);
            }
            if (!isUpdatePending(this.mMergePeer) && !isUpdatePending(this.mMergeHost)) {
                if (this.mSession == null) {
                    loge("merge :: no call session");
                    throw new ImsException("No call session", 148);
                }
                if (!this.mHold && !this.mContext.getResources().getBoolean(17891913)) {
                    this.mSession.hold(createHoldMediaProfile());
                    this.mHold = CONF_DBG;
                    this.mUpdateRequest = 2;
                }
                ImsCall imsCall3 = this.mMergePeer;
                if (imsCall3 != null && !imsCall3.isMultiparty() && !isMultiparty()) {
                    this.mUpdateRequest = 4;
                    this.mMergePeer.mUpdateRequest = 4;
                } else {
                    ImsCall imsCall4 = this.mMergeHost;
                    if (imsCall4 != null && !imsCall4.isMultiparty() && !isMultiparty()) {
                        this.mUpdateRequest = 4;
                        this.mMergeHost.mUpdateRequest = 4;
                    }
                }
                this.mSession.merge();
            }
            setCallSessionMergePending(false);
            ImsCall imsCall5 = this.mMergePeer;
            if (imsCall5 != null) {
                imsCall5.setCallSessionMergePending(false);
            }
            ImsCall imsCall6 = this.mMergeHost;
            if (imsCall6 != null) {
                imsCall6.setCallSessionMergePending(false);
            }
            throw new ImsException("Peer or host call update is in progress", 102);
        }
    }

    public void merge(ImsCall bgCall) throws ImsException {
        logi("merge(1) :: bgImsCall=" + bgCall);
        if (bgCall == null) {
            throw new ImsException("No background call", (int) ImsManager.INCOMING_CALL_RESULT_CODE);
        }
        synchronized (this.mLockObj) {
            setCallSessionMergePending(CONF_DBG);
            bgCall.setCallSessionMergePending(CONF_DBG);
            if ((!isMultiparty() && !bgCall.isMultiparty()) || isMultiparty()) {
                setMergePeer(bgCall);
            } else {
                setMergeHost(bgCall);
            }
        }
        if (isMultiparty()) {
            this.mMergeRequestedByConference = CONF_DBG;
        } else {
            logi("merge : mMergeRequestedByConference not set");
        }
        merge();
    }

    public void update(int callType, ImsStreamMediaProfile mediaProfile) throws ImsException {
        logi("update :: callType=" + callType + ", mediaProfile=" + mediaProfile);
        if (isOnHold()) {
            if (DBG) {
                logi("update :: call is on hold");
            }
            throw new ImsException("Not in a call to update call", 102);
        }
        synchronized (this.mLockObj) {
            if (this.mUpdateRequest != 0) {
                if (DBG) {
                    logi("update :: update is in progress; request=" + updateRequestToString(this.mUpdateRequest));
                }
                throw new ImsException("Call update is in progress", 102);
            }
            ImsCallSession imsCallSession = this.mSession;
            if (imsCallSession == null) {
                loge("update :: ");
                throw new ImsException("No call session", 148);
            } else {
                imsCallSession.update(callType, mediaProfile);
                this.mUpdateRequest = 6;
            }
        }
    }

    public void extendToConference(String[] participants) throws ImsException {
        logi("extendToConference ::");
        if (isOnHold()) {
            if (DBG) {
                logi("extendToConference :: call is on hold");
            }
            throw new ImsException("Not in a call to extend a call to conference", 102);
        }
        synchronized (this.mLockObj) {
            if (this.mUpdateRequest != 0) {
                logi("extendToConference :: update is in progress; request=" + updateRequestToString(this.mUpdateRequest));
                throw new ImsException("Call update is in progress", 102);
            }
            ImsCallSession imsCallSession = this.mSession;
            if (imsCallSession == null) {
                loge("extendToConference :: ");
                throw new ImsException("No call session", 148);
            } else {
                imsCallSession.extendToConference(participants);
                this.mUpdateRequest = 5;
            }
        }
    }

    public void inviteParticipants(String[] participants) throws ImsException {
        logi("inviteParticipants ::");
        synchronized (this.mLockObj) {
            ImsCallSession imsCallSession = this.mSession;
            if (imsCallSession == null) {
                loge("inviteParticipants :: ");
                throw new ImsException("No call session", 148);
            }
            imsCallSession.inviteParticipants(participants);
        }
    }

    public void removeParticipants(String[] participants) throws ImsException {
        logi("removeParticipants :: session=" + this.mSession);
        synchronized (this.mLockObj) {
            ImsCallSession imsCallSession = this.mSession;
            if (imsCallSession == null) {
                loge("removeParticipants :: ");
                throw new ImsException("No call session", 148);
            }
            imsCallSession.removeParticipants(participants);
        }
    }

    public void sendDtmf(char c, Message result) {
        logi("sendDtmf :: ");
        synchronized (this.mLockObj) {
            ImsCallSession imsCallSession = this.mSession;
            if (imsCallSession != null) {
                imsCallSession.sendDtmf(c, result);
            }
        }
    }

    public void startDtmf(char c) {
        logi("startDtmf :: ");
        synchronized (this.mLockObj) {
            ImsCallSession imsCallSession = this.mSession;
            if (imsCallSession != null) {
                imsCallSession.startDtmf(c);
            }
        }
    }

    public void stopDtmf() {
        logi("stopDtmf :: ");
        synchronized (this.mLockObj) {
            ImsCallSession imsCallSession = this.mSession;
            if (imsCallSession != null) {
                imsCallSession.stopDtmf();
            }
        }
    }

    public void sendUssd(String ussdMessage) throws ImsException {
        logi("sendUssd :: ussdMessage=" + ussdMessage);
        synchronized (this.mLockObj) {
            ImsCallSession imsCallSession = this.mSession;
            if (imsCallSession == null) {
                loge("sendUssd :: ");
                throw new ImsException("No call session", 148);
            }
            imsCallSession.sendUssd(ussdMessage);
        }
    }

    public void sendRttMessage(String rttMessage) {
        synchronized (this.mLockObj) {
            if (this.mSession == null) {
                loge("sendRttMessage::no session, ignoring");
                return;
            }
            ImsCallProfile imsCallProfile = this.mCallProfile;
            if (imsCallProfile != null && imsCallProfile.mMediaProfile != null) {
                if (!this.mCallProfile.mMediaProfile.isRttCall()) {
                    logi("sendRttMessage::Not an rtt call, ignoring");
                    return;
                } else {
                    this.mSession.sendRttMessage(rttMessage);
                    return;
                }
            }
            loge("sendRttMessage:: no valid call profile, ignoring");
        }
    }

    public void sendRttModifyRequest(boolean rttOn) {
        logi("sendRttModifyRequest");
        synchronized (this.mLockObj) {
            if (this.mSession == null) {
                loge("sendRttModifyRequest::no session, ignoring");
                return;
            }
            ImsCallProfile imsCallProfile = this.mCallProfile;
            if (imsCallProfile != null && imsCallProfile.mMediaProfile != null) {
                if (rttOn && this.mCallProfile.mMediaProfile.isRttCall()) {
                    logi("sendRttModifyRequest::Already RTT call, ignoring request to turn on.");
                    return;
                } else if (!rttOn && !this.mCallProfile.mMediaProfile.isRttCall()) {
                    logi("sendRttModifyRequest::Not RTT call, ignoring request to turn off.");
                    return;
                } else {
                    Parcel p = Parcel.obtain();
                    this.mCallProfile.writeToParcel(p, 0);
                    p.setDataPosition(0);
                    ImsCallProfile requestedProfile = new ImsCallProfile(p);
                    requestedProfile.mMediaProfile.setRttMode(rttOn ? 1 : 0);
                    this.mSession.sendRttModifyRequest(requestedProfile);
                    return;
                }
            }
            loge("sendRttModifyRequest:: no valid call profile, ignoring");
        }
    }

    public void sendRttModifyResponse(boolean status) {
        logi("sendRttModifyResponse");
        synchronized (this.mLockObj) {
            if (this.mSession == null) {
                loge("sendRttModifyResponse::no session");
                return;
            }
            ImsCallProfile imsCallProfile = this.mCallProfile;
            if (imsCallProfile != null && imsCallProfile.mMediaProfile != null) {
                if (this.mCallProfile.mMediaProfile.isRttCall()) {
                    logi("sendRttModifyResponse::Already RTT call, ignoring.");
                    return;
                } else {
                    this.mSession.sendRttModifyResponse(status);
                    return;
                }
            }
            loge("sendRttModifyResponse:: no valid call profile, ignoring");
        }
    }

    public void sendRtpHeaderExtensions(Set<RtpHeaderExtension> rtpHeaderExtensions) {
        logi("sendRtpHeaderExtensions; extensionsSent=" + rtpHeaderExtensions.size());
        synchronized (this.mLockObj) {
            if (this.mSession == null) {
                loge("sendRtpHeaderExtensions::no session");
            }
            this.mSession.sendRtpHeaderExtensions(rtpHeaderExtensions);
        }
    }

    public void setAnswerWithRtt() {
        this.mAnswerWithRtt = CONF_DBG;
    }

    private void clear(ImsReasonInfo lastReasonInfo) {
        this.mInCall = false;
        this.mHold = false;
        this.mUpdateRequest = 0;
        this.mLastReasonInfo = lastReasonInfo;
    }

    private ImsCallSession.Listener createCallSessionListener() {
        ImsCallSessionListenerProxy imsCallSessionListenerProxy = new ImsCallSessionListenerProxy();
        this.mImsCallSessionListenerProxy = imsCallSessionListenerProxy;
        return imsCallSessionListenerProxy;
    }

    public ImsCallSessionListenerProxy getImsCallSessionListenerProxy() {
        return this.mImsCallSessionListenerProxy;
    }

    public Listener getListener() {
        return this.mListener;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public ImsCall createNewCall(ImsCallSession session, ImsCallProfile profile) {
        ImsCall call = new ImsCall(this.mContext, profile);
        try {
            call.attachSession(session);
            return call;
        } catch (ImsException e) {
            call.close();
            return null;
        }
    }

    private ImsStreamMediaProfile createHoldMediaProfile() {
        ImsStreamMediaProfile mediaProfile = new ImsStreamMediaProfile();
        ImsCallProfile imsCallProfile = this.mCallProfile;
        if (imsCallProfile == null) {
            return mediaProfile;
        }
        mediaProfile.mAudioQuality = imsCallProfile.mMediaProfile.mAudioQuality;
        mediaProfile.mVideoQuality = this.mCallProfile.mMediaProfile.mVideoQuality;
        mediaProfile.mAudioDirection = 2;
        if (mediaProfile.mVideoQuality != 0) {
            mediaProfile.mVideoDirection = 2;
        }
        return mediaProfile;
    }

    private ImsStreamMediaProfile createResumeMediaProfile() {
        ImsStreamMediaProfile mediaProfile = new ImsStreamMediaProfile();
        ImsCallProfile imsCallProfile = this.mCallProfile;
        if (imsCallProfile == null) {
            return mediaProfile;
        }
        mediaProfile.mAudioQuality = imsCallProfile.mMediaProfile.mAudioQuality;
        mediaProfile.mVideoQuality = this.mCallProfile.mMediaProfile.mVideoQuality;
        mediaProfile.mAudioDirection = 3;
        if (mediaProfile.mVideoQuality != 0) {
            mediaProfile.mVideoDirection = 3;
        }
        return mediaProfile;
    }

    private void enforceConversationMode() {
        if (this.mInCall) {
            this.mHold = false;
            this.mUpdateRequest = 0;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void mergeInternal() {
        logi("mergeInternal :: ");
        this.mSession.merge();
        this.mUpdateRequest = 4;
    }

    private void notifyConferenceSessionTerminated(ImsReasonInfo reasonInfo) {
        Listener listener = this.mListener;
        clear(reasonInfo);
        if (listener != null) {
            try {
                listener.onCallTerminated(this, reasonInfo);
            } catch (Throwable t) {
                loge("notifyConferenceSessionTerminated :: ", t);
            }
        }
    }

    private void notifyConferenceStateUpdated(ImsConferenceState state) {
        Listener listener;
        if (state == null || state.mParticipants == null) {
            return;
        }
        List<ConferenceParticipant> parseConferenceState = parseConferenceState(state);
        this.mConferenceParticipants = parseConferenceState;
        if (parseConferenceState != null && (listener = this.mListener) != null) {
            try {
                listener.onConferenceParticipantsStateChanged(this, parseConferenceState);
            } catch (Throwable t) {
                loge("notifyConferenceStateUpdated :: ", t);
            }
        }
    }

    public static List<ConferenceParticipant> parseConferenceState(ImsConferenceState state) {
        String endpoint;
        Set<Map.Entry<String, Bundle>> participants = state.mParticipants.entrySet();
        if (participants == null) {
            return Collections.emptyList();
        }
        List<ConferenceParticipant> conferenceParticipants = new ArrayList<>(participants.size());
        for (Map.Entry<String, Bundle> entry : participants) {
            String key = entry.getKey();
            Bundle confInfo = entry.getValue();
            String status = confInfo.getString(Status.ELEMENT_NAME);
            String user = confInfo.getString("user");
            String displayName = confInfo.getString("display-text");
            String endpoint2 = confInfo.getString("endpoint");
            Log.i(TAG, "notifyConferenceStateUpdated :: key=" + Rlog.pii(TAG, key) + ", status=" + status + ", user=" + Rlog.pii(TAG, user) + ", displayName= " + Rlog.pii(TAG, displayName) + ", endpoint=" + Rlog.pii(TAG, endpoint2));
            Uri handle = Uri.parse(user);
            if (endpoint2 != null) {
                endpoint = endpoint2;
            } else {
                endpoint = "";
            }
            Uri endpointUri = Uri.parse(endpoint);
            int connectionState = ImsConferenceState.getConnectionStateForStatus(status);
            if (connectionState != 6) {
                ConferenceParticipant conferenceParticipant = new ConferenceParticipant(handle, displayName, endpointUri, connectionState, -1);
                conferenceParticipants.add(conferenceParticipant);
            }
        }
        return conferenceParticipants;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void processCallTerminated(ImsReasonInfo reasonInfo) {
        logi("processCallTerminated :: reason=" + reasonInfo + " userInitiated = " + this.mTerminationRequestPending);
        synchronized (this) {
            if (isCallSessionMergePending() && !this.mTerminationRequestPending) {
                logi("processCallTerminated :: burying termination during ongoing merge.");
                this.mSessionEndDuringMerge = CONF_DBG;
                this.mSessionEndDuringMergeReasonInfo = reasonInfo;
            } else if (isMultiparty()) {
                notifyConferenceSessionTerminated(reasonInfo);
            } else {
                Listener listener = this.mListener;
                clear(reasonInfo);
                if (listener != null) {
                    try {
                        listener.onCallTerminated(this, reasonInfo);
                    } catch (Throwable t) {
                        loge("processCallTerminated :: ", t);
                    }
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isTransientConferenceSession(ImsCallSession session) {
        if (session != null && session != this.mSession && session == this.mTransientConferenceSession) {
            return CONF_DBG;
        }
        return false;
    }

    private void setTransientSessionAsPrimary(ImsCallSession transientSession) {
        synchronized (this) {
            this.mSession.setListener((ImsCallSession.Listener) null, (Executor) null);
            this.mSession = transientSession;
            transientSession.setListener(createCallSessionListener(), this.mContext.getMainExecutor());
        }
    }

    private void markCallAsMerged(boolean playDisconnectTone) {
        int reasonCode;
        String reasonInfo;
        if (!isSessionAlive(this.mSession)) {
            logi("markCallAsMerged");
            setIsMerged(playDisconnectTone);
            this.mSessionEndDuringMerge = CONF_DBG;
            if (playDisconnectTone) {
                reasonCode = 510;
                reasonInfo = "Call ended by network";
            } else {
                reasonCode = 108;
                reasonInfo = "Call ended during conference merge process.";
            }
            this.mSessionEndDuringMergeReasonInfo = new ImsReasonInfo(reasonCode, 0, reasonInfo);
        }
    }

    public boolean isMergeRequestedByConf() {
        boolean z;
        synchronized (this.mLockObj) {
            z = this.mMergeRequestedByConference;
        }
        return z;
    }

    public void resetIsMergeRequestedByConf(boolean value) {
        synchronized (this.mLockObj) {
            this.mMergeRequestedByConference = value;
        }
    }

    public ImsCallSession getSession() {
        ImsCallSession imsCallSession;
        synchronized (this.mLockObj) {
            imsCallSession = this.mSession;
        }
        return imsCallSession;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void processMergeComplete() {
        ImsCall finalHostCall;
        ImsCall finalPeerCall;
        ImsCall finalHostCall2;
        logi("processMergeComplete :: ");
        if (!isMergeHost()) {
            loge("processMergeComplete :: We are not the merge host!");
            return;
        }
        boolean swapRequired = false;
        synchronized (this) {
            if (isMultiparty()) {
                setIsMerged(false);
                if (!this.mMergeRequestedByConference) {
                    this.mHold = false;
                    swapRequired = CONF_DBG;
                }
                this.mMergePeer.markCallAsMerged(false);
                finalHostCall2 = this;
                finalPeerCall = this.mMergePeer;
            } else {
                ImsCallSession transientConferenceSession = this.mTransientConferenceSession;
                if (transientConferenceSession == null) {
                    loge("processMergeComplete :: No transient session!");
                    return;
                } else if (this.mMergePeer == null) {
                    loge("processMergeComplete :: No merge peer!");
                    return;
                } else {
                    this.mTransientConferenceSession = null;
                    transientConferenceSession.setListener((ImsCallSession.Listener) null, (Executor) null);
                    if (isSessionAlive(this.mSession) && !isSessionAlive(this.mMergePeer.getCallSession())) {
                        this.mMergePeer.mHold = false;
                        this.mHold = CONF_DBG;
                        List<ConferenceParticipant> list = this.mConferenceParticipants;
                        if (list != null && !list.isEmpty()) {
                            this.mMergePeer.mConferenceParticipants = this.mConferenceParticipants;
                        }
                        finalHostCall = this.mMergePeer;
                        swapRequired = CONF_DBG;
                        setIsMerged(false);
                        this.mMergePeer.setIsMerged(false);
                        logi("processMergeComplete :: transient will transfer to merge peer");
                        finalPeerCall = this;
                    } else if (!isSessionAlive(this.mSession) && isSessionAlive(this.mMergePeer.getCallSession())) {
                        finalHostCall = this;
                        ImsCall finalPeerCall2 = this.mMergePeer;
                        swapRequired = false;
                        setIsMerged(false);
                        this.mMergePeer.setIsMerged(false);
                        logi("processMergeComplete :: transient will stay with the merge host");
                        finalPeerCall = finalPeerCall2;
                    } else {
                        finalHostCall = this;
                        ImsCall finalPeerCall3 = this.mMergePeer;
                        finalPeerCall3.markCallAsMerged(false);
                        swapRequired = false;
                        setIsMerged(false);
                        this.mMergePeer.setIsMerged(CONF_DBG);
                        logi("processMergeComplete :: transient will stay with us (I'm the host).");
                        finalPeerCall = finalPeerCall3;
                    }
                    logi("processMergeComplete :: call=" + finalHostCall + " is the final host");
                    finalHostCall.setTransientSessionAsPrimary(transientConferenceSession);
                    finalHostCall2 = finalHostCall;
                }
            }
            Listener listener = finalHostCall2.mListener;
            updateCallProfile(finalPeerCall);
            updateCallProfile(finalHostCall2);
            clearMergeInfo();
            finalPeerCall.notifySessionTerminatedDuringMerge();
            finalHostCall2.clearSessionTerminationFlags();
            finalHostCall2.mIsConferenceHost = CONF_DBG;
            if (listener != null) {
                try {
                    listener.onCallMerged(finalHostCall2, finalPeerCall, swapRequired);
                } catch (Throwable t) {
                    loge("processMergeComplete :: ", t);
                }
                List<ConferenceParticipant> list2 = this.mConferenceParticipants;
                if (list2 != null && !list2.isEmpty()) {
                    try {
                        listener.onConferenceParticipantsStateChanged(finalHostCall2, this.mConferenceParticipants);
                    } catch (Throwable t2) {
                        loge("processMergeComplete :: ", t2);
                    }
                }
            }
        }
    }

    private static void updateCallProfile(ImsCall call) {
        if (call != null) {
            call.updateCallProfile();
        }
    }

    private void updateCallProfile() {
        synchronized (this.mLockObj) {
            ImsCallSession imsCallSession = this.mSession;
            if (imsCallSession != null) {
                setCallProfile(imsCallSession.getCallProfile());
            }
        }
    }

    private void notifySessionTerminatedDuringMerge() {
        Listener listener;
        boolean notifyFailure = false;
        ImsReasonInfo notifyFailureReasonInfo = null;
        synchronized (this) {
            listener = this.mListener;
            if (this.mSessionEndDuringMerge) {
                logi("notifySessionTerminatedDuringMerge ::reporting terminate during merge");
                notifyFailure = CONF_DBG;
                notifyFailureReasonInfo = this.mSessionEndDuringMergeReasonInfo;
            }
            clearSessionTerminationFlags();
        }
        if (listener != null && notifyFailure) {
            try {
                processCallTerminated(notifyFailureReasonInfo);
            } catch (Throwable t) {
                loge("notifySessionTerminatedDuringMerge :: ", t);
            }
        }
    }

    private void clearSessionTerminationFlags() {
        this.mSessionEndDuringMerge = false;
        this.mSessionEndDuringMergeReasonInfo = null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void processMergeFailed(ImsReasonInfo reasonInfo) {
        logi("processMergeFailed :: reason=" + reasonInfo);
        synchronized (this) {
            if (!isMergeHost()) {
                loge("processMergeFailed :: We are not the merge host!");
                return;
            }
            ImsCallSession imsCallSession = this.mTransientConferenceSession;
            if (imsCallSession != null) {
                imsCallSession.setListener((ImsCallSession.Listener) null, (Executor) null);
                this.mTransientConferenceSession = null;
            }
            Listener listener = this.mListener;
            markCallAsMerged(CONF_DBG);
            setCallSessionMergePending(false);
            notifySessionTerminatedDuringMerge();
            ImsCall imsCall = this.mMergePeer;
            if (imsCall != null) {
                imsCall.markCallAsMerged(CONF_DBG);
                this.mMergePeer.setCallSessionMergePending(false);
                this.mMergePeer.notifySessionTerminatedDuringMerge();
            } else {
                loge("processMergeFailed :: No merge peer!");
            }
            clearMergeInfo();
            if (listener != null) {
                try {
                    listener.onCallMergeFailed(this, reasonInfo);
                } catch (Throwable t) {
                    loge("processMergeFailed :: ", t);
                }
            }
        }
    }

    /* loaded from: classes.dex */
    public class ImsCallSessionListenerProxy extends ImsCallSession.Listener {
        public ImsCallSessionListenerProxy() {
        }

        public void callSessionInitiating(ImsCallSession session, ImsCallProfile profile) {
            Listener listener;
            ImsCall.this.logi("callSessionInitiating :: session=" + session + " profile=" + profile);
            if (ImsCall.this.isTransientConferenceSession(session)) {
                ImsCall.this.logi("callSessionInitiating :: not supported for transient conference session=" + session);
                return;
            }
            synchronized (ImsCall.this) {
                listener = ImsCall.this.mListener;
                ImsCall.this.setCallProfile(profile);
            }
            if (listener != null) {
                try {
                    listener.onCallInitiating(ImsCall.this);
                } catch (Throwable t) {
                    ImsCall.this.loge("callSessionInitiating :: ", t);
                }
            }
        }

        public void callSessionProgressing(ImsCallSession session, ImsStreamMediaProfile profile) {
            Listener listener;
            ImsCall.this.logi("callSessionProgressing :: session=" + session + " profile=" + profile);
            if (ImsCall.this.isTransientConferenceSession(session)) {
                ImsCall.this.logi("callSessionProgressing :: not supported for transient conference session=" + session);
                return;
            }
            ImsCallProfile updatedProfile = session.getCallProfile();
            synchronized (ImsCall.this) {
                listener = ImsCall.this.mListener;
                ImsCall.this.setCallProfile(updatedProfile);
                ImsCall.this.mCallProfile.mMediaProfile.copyFrom(profile);
            }
            if (listener != null) {
                try {
                    listener.onCallProgressing(ImsCall.this);
                } catch (Throwable t) {
                    ImsCall.this.loge("callSessionProgressing :: ", t);
                }
            }
        }

        public void callSessionStarted(ImsCallSession session, ImsCallProfile profile) {
            Listener listener;
            ImsCall.this.logi("callSessionStarted :: session=" + session + " profile=" + profile);
            if (!ImsCall.this.isTransientConferenceSession(session)) {
                ImsCall.this.setCallSessionMergePending(false);
                if (ImsCall.this.isTransientConferenceSession(session)) {
                    return;
                }
                synchronized (ImsCall.this) {
                    listener = ImsCall.this.mListener;
                    ImsCall.this.setCallProfile(profile);
                }
                if (listener != null) {
                    try {
                        listener.onCallStarted(ImsCall.this);
                        return;
                    } catch (Throwable t) {
                        ImsCall.this.loge("callSessionStarted :: ", t);
                        return;
                    }
                }
                return;
            }
            ImsCall.this.logi("callSessionStarted :: on transient session=" + session);
        }

        public void callSessionStartFailed(ImsCallSession session, ImsReasonInfo reasonInfo) {
            Listener listener;
            ImsCall.this.loge("callSessionStartFailed :: session=" + session + " reasonInfo=" + reasonInfo);
            if (ImsCall.this.isTransientConferenceSession(session)) {
                ImsCall.this.logi("callSessionStartFailed :: not supported for transient conference session=" + session);
                return;
            }
            if (ImsCall.this.mIsConferenceHost) {
                ImsCall.this.mIsConferenceHost = false;
            }
            synchronized (ImsCall.this) {
                listener = ImsCall.this.mListener;
                ImsCall.this.mLastReasonInfo = reasonInfo;
            }
            if (listener != null) {
                try {
                    listener.onCallStartFailed(ImsCall.this, reasonInfo);
                } catch (Throwable t) {
                    ImsCall.this.loge("callSessionStarted :: ", t);
                }
            }
        }

        public void callSessionTerminated(ImsCallSession session, ImsReasonInfo reasonInfo) {
            ImsCall.this.logi("callSessionTerminated :: session=" + session + " reasonInfo=" + reasonInfo);
            if (ImsCall.this.isTransientConferenceSession(session)) {
                ImsCall.this.logi("callSessionTerminated :: on transient session=" + session);
                ImsCall.this.processMergeFailed(reasonInfo);
                return;
            }
            if (ImsCall.this.mOverrideReason != 0) {
                ImsCall.this.logi("callSessionTerminated :: overrideReasonInfo=" + ImsCall.this.mOverrideReason);
                reasonInfo = new ImsReasonInfo(ImsCall.this.mOverrideReason, reasonInfo.getExtraCode(), reasonInfo.getExtraMessage());
            }
            ImsCall.this.processCallTerminated(reasonInfo);
            ImsCall.this.setCallSessionMergePending(false);
        }

        public void callSessionHeld(ImsCallSession session, ImsCallProfile profile) {
            ImsCall.this.logi("callSessionHeld :: session=" + session + "profile=" + profile);
            synchronized (ImsCall.this) {
                ImsCall.this.setCallSessionMergePending(false);
                ImsCall.this.setCallProfile(profile);
                if (ImsCall.this.mUpdateRequest == 2) {
                    ImsCall.this.mergeInternal();
                    return;
                }
                ImsCall.this.mHold = ImsCall.CONF_DBG;
                ImsCall.this.mUpdateRequest = 0;
                Listener listener = ImsCall.this.mListener;
                if (listener != null) {
                    try {
                        listener.onCallHeld(ImsCall.this);
                    } catch (Throwable t) {
                        ImsCall.this.loge("callSessionHeld :: ", t);
                    }
                }
            }
        }

        public void callSessionHoldFailed(ImsCallSession session, ImsReasonInfo reasonInfo) {
            Listener listener;
            ImsCall.this.loge("callSessionHoldFailed :: session" + session + "reasonInfo=" + reasonInfo);
            if (ImsCall.this.isTransientConferenceSession(session)) {
                ImsCall.this.logi("callSessionHoldFailed :: not supported for transient conference session=" + session);
                return;
            }
            ImsCall.this.logi("callSessionHoldFailed :: session=" + session + ", reasonInfo=" + reasonInfo);
            synchronized (ImsCall.this.mLockObj) {
                ImsCall.this.mHold = false;
            }
            synchronized (ImsCall.this) {
                if (ImsCall.this.mUpdateRequest == 2) {
                }
                ImsCall.this.mUpdateRequest = 0;
                listener = ImsCall.this.mListener;
            }
            if (listener != null) {
                try {
                    listener.onCallHoldFailed(ImsCall.this, reasonInfo);
                } catch (Throwable t) {
                    ImsCall.this.loge("callSessionHoldFailed :: ", t);
                }
            }
        }

        public void callSessionHoldReceived(ImsCallSession session, ImsCallProfile profile) {
            Listener listener;
            ImsCall.this.logi("callSessionHoldReceived :: session=" + session + "profile=" + profile);
            if (ImsCall.this.isTransientConferenceSession(session)) {
                ImsCall.this.logi("callSessionHoldReceived :: not supported for transient conference session=" + session);
                return;
            }
            synchronized (ImsCall.this) {
                listener = ImsCall.this.mListener;
                ImsCall.this.setCallProfile(profile);
            }
            if (listener != null) {
                try {
                    listener.onCallHoldReceived(ImsCall.this);
                } catch (Throwable t) {
                    ImsCall.this.loge("callSessionHoldReceived :: ", t);
                }
            }
        }

        public void callSessionResumed(ImsCallSession session, ImsCallProfile profile) {
            Listener listener;
            ImsCall.this.logi("callSessionResumed :: session=" + session + "profile=" + profile);
            if (ImsCall.this.isTransientConferenceSession(session)) {
                ImsCall.this.logi("callSessionResumed :: not supported for transient conference session=" + session);
                return;
            }
            ImsCall.this.setCallSessionMergePending(false);
            synchronized (ImsCall.this) {
                listener = ImsCall.this.mListener;
                ImsCall.this.setCallProfile(profile);
                ImsCall.this.mUpdateRequest = 0;
                ImsCall.this.mHold = false;
            }
            if (listener != null) {
                try {
                    listener.onCallResumed(ImsCall.this);
                } catch (Throwable t) {
                    ImsCall.this.loge("callSessionResumed :: ", t);
                }
            }
        }

        public void callSessionResumeFailed(ImsCallSession session, ImsReasonInfo reasonInfo) {
            Listener listener;
            ImsCall.this.loge("callSessionResumeFailed :: session=" + session + "reasonInfo=" + reasonInfo);
            if (ImsCall.this.isTransientConferenceSession(session)) {
                ImsCall.this.logi("callSessionResumeFailed :: not supported for transient conference session=" + session);
                return;
            }
            synchronized (ImsCall.this.mLockObj) {
                ImsCall.this.mHold = ImsCall.CONF_DBG;
            }
            synchronized (ImsCall.this) {
                listener = ImsCall.this.mListener;
                ImsCall.this.mUpdateRequest = 0;
            }
            if (listener != null) {
                try {
                    listener.onCallResumeFailed(ImsCall.this, reasonInfo);
                } catch (Throwable t) {
                    ImsCall.this.loge("callSessionResumeFailed :: ", t);
                }
            }
        }

        public void callSessionResumeReceived(ImsCallSession session, ImsCallProfile profile) {
            Listener listener;
            ImsCall.this.logi("callSessionResumeReceived :: session=" + session + "profile=" + profile);
            if (ImsCall.this.isTransientConferenceSession(session)) {
                ImsCall.this.logi("callSessionResumeReceived :: not supported for transient conference session=" + session);
                return;
            }
            synchronized (ImsCall.this) {
                listener = ImsCall.this.mListener;
                ImsCall.this.setCallProfile(profile);
            }
            if (listener != null) {
                try {
                    listener.onCallResumeReceived(ImsCall.this);
                } catch (Throwable t) {
                    ImsCall.this.loge("callSessionResumeReceived :: ", t);
                }
            }
        }

        public void callSessionMergeStarted(ImsCallSession session, ImsCallSession newSession, ImsCallProfile profile) {
            ImsCall.this.logi("callSessionMergeStarted :: session=" + session + " newSession=" + newSession + ", profile=" + profile);
        }

        public void callSessionMergeComplete(ImsCallSession newSession) {
            ImsCall.this.logi("callSessionMergeComplete :: newSession =" + newSession);
            if (!ImsCall.this.isMergeHost()) {
                ImsCall.this.mMergeHost.processMergeComplete();
                return;
            }
            if (newSession != null) {
                ImsCall.this.mTransientConferenceSession = newSession;
            }
            ImsCall.this.processMergeComplete();
        }

        public void callSessionMergeFailed(ImsCallSession session, ImsReasonInfo reasonInfo) {
            ImsCall.this.loge("callSessionMergeFailed :: session=" + session + "reasonInfo=" + reasonInfo);
            synchronized (ImsCall.this) {
                if (ImsCall.this.isMergeHost()) {
                    ImsCall.this.processMergeFailed(reasonInfo);
                } else if (ImsCall.this.mMergeHost != null) {
                    ImsCall.this.mMergeHost.processMergeFailed(reasonInfo);
                } else {
                    ImsCall.this.loge("callSessionMergeFailed :: No merge host for this conference!");
                }
            }
        }

        public void callSessionUpdated(ImsCallSession session, ImsCallProfile profile) {
            Listener listener;
            ImsCall.this.logi("callSessionUpdated :: session=" + session + " profile=" + profile);
            if (ImsCall.this.isTransientConferenceSession(session)) {
                ImsCall.this.logi("callSessionUpdated :: not supported for transient conference session=" + session);
                return;
            }
            synchronized (ImsCall.this) {
                listener = ImsCall.this.mListener;
                ImsCall.this.setCallProfile(profile);
            }
            if (listener != null) {
                try {
                    listener.onCallUpdated(ImsCall.this);
                } catch (Throwable t) {
                    ImsCall.this.loge("callSessionUpdated :: ", t);
                }
            }
        }

        public void callSessionUpdateFailed(ImsCallSession session, ImsReasonInfo reasonInfo) {
            Listener listener;
            ImsCall.this.loge("callSessionUpdateFailed :: session=" + session + " reasonInfo=" + reasonInfo);
            if (ImsCall.this.isTransientConferenceSession(session)) {
                ImsCall.this.logi("callSessionUpdateFailed :: not supported for transient conference session=" + session);
                return;
            }
            synchronized (ImsCall.this) {
                listener = ImsCall.this.mListener;
                ImsCall.this.mUpdateRequest = 0;
            }
            if (listener != null) {
                try {
                    listener.onCallUpdateFailed(ImsCall.this, reasonInfo);
                } catch (Throwable t) {
                    ImsCall.this.loge("callSessionUpdateFailed :: ", t);
                }
            }
        }

        public void callSessionUpdateReceived(ImsCallSession session, ImsCallProfile profile) {
            Listener listener;
            ImsCall.this.logi("callSessionUpdateReceived :: session=" + session + " profile=" + profile);
            if (ImsCall.this.isTransientConferenceSession(session)) {
                ImsCall.this.logi("callSessionUpdateReceived :: not supported for transient conference session=" + session);
                return;
            }
            synchronized (ImsCall.this) {
                listener = ImsCall.this.mListener;
                ImsCall.this.mProposedCallProfile = profile;
                ImsCall.this.mUpdateRequest = 6;
            }
            if (listener != null) {
                try {
                    listener.onCallUpdateReceived(ImsCall.this);
                } catch (Throwable t) {
                    ImsCall.this.loge("callSessionUpdateReceived :: ", t);
                }
            }
        }

        public void callSessionConferenceExtended(ImsCallSession session, ImsCallSession newSession, ImsCallProfile profile) {
            Listener listener;
            ImsCall.this.logi("callSessionConferenceExtended :: session=" + session + " newSession=" + newSession + ", profile=" + profile);
            if (ImsCall.this.isTransientConferenceSession(session)) {
                ImsCall.this.logi("callSessionConferenceExtended :: not supported for transient conference session=" + session);
                return;
            }
            ImsCall newCall = ImsCall.this.createNewCall(newSession, profile);
            if (newCall == null) {
                callSessionConferenceExtendFailed(session, new ImsReasonInfo());
                return;
            }
            synchronized (ImsCall.this) {
                listener = ImsCall.this.mListener;
                ImsCall.this.mUpdateRequest = 0;
            }
            if (listener != null) {
                try {
                    listener.onCallConferenceExtended(ImsCall.this, newCall);
                } catch (Throwable t) {
                    ImsCall.this.loge("callSessionConferenceExtended :: ", t);
                }
            }
        }

        public void callSessionConferenceExtendFailed(ImsCallSession session, ImsReasonInfo reasonInfo) {
            Listener listener;
            ImsCall.this.loge("callSessionConferenceExtendFailed :: reasonInfo=" + reasonInfo);
            if (ImsCall.this.isTransientConferenceSession(session)) {
                ImsCall.this.logi("callSessionConferenceExtendFailed :: not supported for transient conference session=" + session);
                return;
            }
            synchronized (ImsCall.this) {
                listener = ImsCall.this.mListener;
                ImsCall.this.mUpdateRequest = 0;
            }
            if (listener != null) {
                try {
                    listener.onCallConferenceExtendFailed(ImsCall.this, reasonInfo);
                } catch (Throwable t) {
                    ImsCall.this.loge("callSessionConferenceExtendFailed :: ", t);
                }
            }
        }

        public void callSessionConferenceExtendReceived(ImsCallSession session, ImsCallSession newSession, ImsCallProfile profile) {
            Listener listener;
            ImsCall.this.logi("callSessionConferenceExtendReceived :: newSession=" + newSession + ", profile=" + profile);
            if (ImsCall.this.isTransientConferenceSession(session)) {
                ImsCall.this.logi("callSessionConferenceExtendReceived :: not supported for transient conference session" + session);
                return;
            }
            ImsCall newCall = ImsCall.this.createNewCall(newSession, profile);
            if (newCall == null) {
                return;
            }
            synchronized (ImsCall.this) {
                listener = ImsCall.this.mListener;
            }
            if (listener != null) {
                try {
                    listener.onCallConferenceExtendReceived(ImsCall.this, newCall);
                } catch (Throwable t) {
                    ImsCall.this.loge("callSessionConferenceExtendReceived :: ", t);
                }
            }
        }

        public void callSessionInviteParticipantsRequestDelivered(ImsCallSession session) {
            Listener listener;
            ImsCall.this.logi("callSessionInviteParticipantsRequestDelivered ::");
            if (ImsCall.this.isTransientConferenceSession(session)) {
                ImsCall.this.logi("callSessionInviteParticipantsRequestDelivered :: not supported for conference session=" + session);
                return;
            }
            synchronized (ImsCall.this) {
                listener = ImsCall.this.mListener;
            }
            ImsCall.this.mIsConferenceHost = ImsCall.CONF_DBG;
            if (listener != null) {
                try {
                    listener.onCallInviteParticipantsRequestDelivered(ImsCall.this);
                } catch (Throwable t) {
                    ImsCall.this.loge("callSessionInviteParticipantsRequestDelivered :: ", t);
                }
            }
        }

        public void callSessionInviteParticipantsRequestFailed(ImsCallSession session, ImsReasonInfo reasonInfo) {
            Listener listener;
            ImsCall.this.loge("callSessionInviteParticipantsRequestFailed :: reasonInfo=" + reasonInfo);
            if (ImsCall.this.isTransientConferenceSession(session)) {
                ImsCall.this.logi("callSessionInviteParticipantsRequestFailed :: not supported for conference session=" + session);
                return;
            }
            synchronized (ImsCall.this) {
                listener = ImsCall.this.mListener;
            }
            if (listener != null) {
                try {
                    listener.onCallInviteParticipantsRequestFailed(ImsCall.this, reasonInfo);
                } catch (Throwable t) {
                    ImsCall.this.loge("callSessionInviteParticipantsRequestFailed :: ", t);
                }
            }
        }

        public void callSessionRemoveParticipantsRequestDelivered(ImsCallSession session) {
            Listener listener;
            ImsCall.this.logi("callSessionRemoveParticipantsRequestDelivered ::");
            if (ImsCall.this.isTransientConferenceSession(session)) {
                ImsCall.this.logi("callSessionRemoveParticipantsRequestDelivered :: not supported for conference session=" + session);
                return;
            }
            synchronized (ImsCall.this) {
                listener = ImsCall.this.mListener;
            }
            if (listener != null) {
                try {
                    listener.onCallRemoveParticipantsRequestDelivered(ImsCall.this);
                } catch (Throwable t) {
                    ImsCall.this.loge("callSessionRemoveParticipantsRequestDelivered :: ", t);
                }
            }
        }

        public void callSessionRemoveParticipantsRequestFailed(ImsCallSession session, ImsReasonInfo reasonInfo) {
            Listener listener;
            ImsCall.this.loge("callSessionRemoveParticipantsRequestFailed :: reasonInfo=" + reasonInfo);
            if (ImsCall.this.isTransientConferenceSession(session)) {
                ImsCall.this.logi("callSessionRemoveParticipantsRequestFailed :: not supported for conference session=" + session);
                return;
            }
            synchronized (ImsCall.this) {
                listener = ImsCall.this.mListener;
            }
            if (listener != null) {
                try {
                    listener.onCallRemoveParticipantsRequestFailed(ImsCall.this, reasonInfo);
                } catch (Throwable t) {
                    ImsCall.this.loge("callSessionRemoveParticipantsRequestFailed :: ", t);
                }
            }
        }

        public void callSessionConferenceStateUpdated(ImsCallSession session, ImsConferenceState state) {
            ImsCall.this.logi("callSessionConferenceStateUpdated :: state=" + state);
            ImsCall.this.conferenceStateUpdated(state);
        }

        public void callSessionUssdMessageReceived(ImsCallSession session, int mode, String ussdMessage) {
            Listener listener;
            ImsCall.this.logi("callSessionUssdMessageReceived :: mode=" + mode + ", ussdMessage=" + ussdMessage);
            if (ImsCall.this.isTransientConferenceSession(session)) {
                ImsCall.this.logi("callSessionUssdMessageReceived :: not supported for transient conference session=" + session);
                return;
            }
            synchronized (ImsCall.this) {
                listener = ImsCall.this.mListener;
            }
            if (listener != null) {
                try {
                    listener.onCallUssdMessageReceived(ImsCall.this, mode, ussdMessage);
                } catch (Throwable t) {
                    ImsCall.this.loge("callSessionUssdMessageReceived :: ", t);
                }
            }
        }

        public void callSessionTtyModeReceived(ImsCallSession session, int mode) {
            Listener listener;
            ImsCall.this.logi("callSessionTtyModeReceived :: mode=" + mode);
            synchronized (ImsCall.this) {
                listener = ImsCall.this.mListener;
            }
            if (listener != null) {
                try {
                    listener.onCallSessionTtyModeReceived(ImsCall.this, mode);
                } catch (Throwable t) {
                    ImsCall.this.loge("callSessionTtyModeReceived :: ", t);
                }
            }
        }

        public void callSessionMultipartyStateChanged(ImsCallSession session, boolean isMultiParty) {
            Listener listener;
            if (ImsCall.VDBG) {
                ImsCall.this.logi("callSessionMultipartyStateChanged isMultiParty: " + (isMultiParty ? "Y" : "N"));
            }
            synchronized (ImsCall.this) {
                listener = ImsCall.this.mListener;
            }
            if (listener != null) {
                try {
                    listener.onMultipartyStateChanged(ImsCall.this, isMultiParty);
                } catch (Throwable t) {
                    ImsCall.this.loge("callSessionMultipartyStateChanged :: ", t);
                }
            }
        }

        public void callSessionHandover(ImsCallSession session, int srcNetworkType, int targetNetworkType, ImsReasonInfo reasonInfo) {
            Listener listener;
            ImsCall.this.logi("callSessionHandover :: session=" + session + ", srcAccessTech=" + srcNetworkType + ", targetAccessTech=" + targetNetworkType + ", reasonInfo=" + reasonInfo);
            synchronized (ImsCall.this) {
                listener = ImsCall.this.mListener;
            }
            if (listener != null) {
                try {
                    listener.onCallHandover(ImsCall.this, ServiceState.networkTypeToRilRadioTechnology(srcNetworkType), ServiceState.networkTypeToRilRadioTechnology(targetNetworkType), reasonInfo);
                } catch (Throwable t) {
                    ImsCall.this.loge("callSessionHandover :: ", t);
                }
            }
        }

        public void callSessionHandoverFailed(ImsCallSession session, int srcNetworkType, int targetNetworkType, ImsReasonInfo reasonInfo) {
            Listener listener;
            ImsCall.this.loge("callSessionHandoverFailed :: session=" + session + ", srcAccessTech=" + srcNetworkType + ", targetAccessTech=" + targetNetworkType + ", reasonInfo=" + reasonInfo);
            synchronized (ImsCall.this) {
                listener = ImsCall.this.mListener;
            }
            if (listener != null) {
                try {
                    listener.onCallHandoverFailed(ImsCall.this, ServiceState.networkTypeToRilRadioTechnology(srcNetworkType), ServiceState.networkTypeToRilRadioTechnology(targetNetworkType), reasonInfo);
                } catch (Throwable t) {
                    ImsCall.this.loge("callSessionHandoverFailed :: ", t);
                }
            }
        }

        public void callSessionSuppServiceReceived(ImsCallSession session, ImsSuppServiceNotification suppServiceInfo) {
            Listener listener;
            if (ImsCall.this.isTransientConferenceSession(session)) {
                ImsCall.this.logi("callSessionSuppServiceReceived :: not supported for transient conference session=" + session);
                return;
            }
            ImsCall.this.logi("callSessionSuppServiceReceived :: session=" + session + ", suppServiceInfo" + suppServiceInfo);
            synchronized (ImsCall.this) {
                listener = ImsCall.this.mListener;
            }
            if (listener != null) {
                try {
                    listener.onCallSuppServiceReceived(ImsCall.this, suppServiceInfo);
                } catch (Throwable t) {
                    ImsCall.this.loge("callSessionSuppServiceReceived :: ", t);
                }
            }
        }

        public void callSessionRttModifyRequestReceived(ImsCallSession session, ImsCallProfile callProfile) {
            Listener listener;
            ImsCall.this.logi("callSessionRttModifyRequestReceived");
            synchronized (ImsCall.this) {
                listener = ImsCall.this.mListener;
            }
            if (!callProfile.mMediaProfile.isRttCall()) {
                ImsCall.this.logi("callSessionRttModifyRequestReceived:: ignoring request, requested profile is not RTT.");
            } else if (listener != null) {
                try {
                    listener.onRttModifyRequestReceived(ImsCall.this);
                } catch (Throwable t) {
                    ImsCall.this.loge("callSessionRttModifyRequestReceived:: ", t);
                }
            }
        }

        public void callSessionRttModifyResponseReceived(int status) {
            Listener listener;
            ImsCall.this.logi("callSessionRttModifyResponseReceived: " + status);
            synchronized (ImsCall.this) {
                listener = ImsCall.this.mListener;
            }
            if (listener != null) {
                try {
                    listener.onRttModifyResponseReceived(ImsCall.this, status);
                } catch (Throwable t) {
                    ImsCall.this.loge("callSessionRttModifyResponseReceived:: ", t);
                }
            }
        }

        public void callSessionRttMessageReceived(String rttMessage) {
            Listener listener;
            synchronized (ImsCall.this) {
                listener = ImsCall.this.mListener;
            }
            if (listener != null) {
                try {
                    listener.onRttMessageReceived(ImsCall.this, rttMessage);
                } catch (Throwable t) {
                    ImsCall.this.loge("callSessionRttMessageReceived:: ", t);
                }
            }
        }

        public void callSessionRttAudioIndicatorChanged(ImsStreamMediaProfile profile) {
            Listener listener;
            synchronized (ImsCall.this) {
                listener = ImsCall.this.mListener;
            }
            if (listener != null) {
                try {
                    listener.onRttAudioIndicatorChanged(ImsCall.this, profile);
                } catch (Throwable t) {
                    ImsCall.this.loge("callSessionRttAudioIndicatorChanged:: ", t);
                }
            }
        }

        public void callSessionTransferred(ImsCallSession session) {
            Listener listener;
            synchronized (ImsCall.this) {
                listener = ImsCall.this.mListener;
            }
            if (listener != null) {
                try {
                    listener.onCallSessionTransferred(ImsCall.this);
                } catch (Throwable t) {
                    ImsCall.this.loge("callSessionTransferred:: ", t);
                }
            }
        }

        public void callSessionTransferFailed(ImsCallSession session, ImsReasonInfo reasonInfo) {
            Listener listener;
            synchronized (ImsCall.this) {
                listener = ImsCall.this.mListener;
            }
            if (listener != null) {
                try {
                    listener.onCallSessionTransferFailed(ImsCall.this, reasonInfo);
                } catch (Throwable t) {
                    ImsCall.this.loge("callSessionTransferFailed:: ", t);
                }
            }
        }

        public void callSessionDtmfReceived(char digit) {
            Listener listener;
            synchronized (ImsCall.this) {
                listener = ImsCall.this.mListener;
            }
            if (listener != null) {
                try {
                    listener.onCallSessionDtmfReceived(ImsCall.this, digit);
                } catch (Throwable t) {
                    ImsCall.this.loge("callSessionDtmfReceived:: ", t);
                }
            }
        }

        public void callQualityChanged(CallQuality callQuality) {
            Listener listener;
            synchronized (ImsCall.this) {
                listener = ImsCall.this.mListener;
            }
            if (listener != null) {
                try {
                    listener.onCallQualityChanged(ImsCall.this, callQuality);
                } catch (Throwable t) {
                    ImsCall.this.loge("callQualityChanged:: ", t);
                }
            }
        }

        public void callSessionRtpHeaderExtensionsReceived(Set<RtpHeaderExtension> extensions) {
            Listener listener;
            synchronized (ImsCall.this) {
                listener = ImsCall.this.mListener;
            }
            if (listener != null) {
                try {
                    listener.onCallSessionRtpHeaderExtensionsReceived(ImsCall.this, extensions);
                } catch (Throwable t) {
                    ImsCall.this.loge("callSessionRtpHeaderExtensionsReceived:: ", t);
                }
            }
        }

        public void callSessionSendAnbrQuery(int mediaType, int direction, int bitsPerSecond) {
            Listener listener;
            ImsCall.this.logi("callSessionSendAnbrQuery in ImsCall");
            synchronized (ImsCall.this) {
                listener = ImsCall.this.mListener;
            }
            if (listener != null) {
                try {
                    listener.onCallSessionSendAnbrQuery(ImsCall.this, mediaType, direction, bitsPerSecond);
                } catch (Throwable t) {
                    ImsCall.this.loge("callSessionSendAnbrQuery:: ", t);
                }
            }
        }
    }

    public void conferenceStateUpdated(ImsConferenceState state) {
        Listener listener;
        synchronized (this) {
            notifyConferenceStateUpdated(state);
            listener = this.mListener;
        }
        if (listener != null) {
            try {
                listener.onCallConferenceStateUpdated(this, state);
            } catch (Throwable t) {
                loge("callSessionConferenceStateUpdated :: ", t);
            }
        }
    }

    private String updateRequestToString(int updateRequest) {
        switch (updateRequest) {
            case 0:
                return "NONE";
            case 1:
                return "HOLD";
            case 2:
                return "HOLD_MERGE";
            case 3:
                return "RESUME";
            case 4:
                return "MERGE";
            case 5:
                return "EXTEND_TO_CONFERENCE";
            case 6:
                return "UNSPECIFIED";
            default:
                return "UNKNOWN";
        }
    }

    private void clearMergeInfo() {
        logi("clearMergeInfo :: clearing all merge info");
        ImsCall imsCall = this.mMergeHost;
        if (imsCall != null) {
            imsCall.mMergePeer = null;
            imsCall.mUpdateRequest = 0;
            imsCall.mCallSessionMergePending = false;
        }
        ImsCall imsCall2 = this.mMergePeer;
        if (imsCall2 != null) {
            imsCall2.mMergeHost = null;
            imsCall2.mUpdateRequest = 0;
            imsCall2.mCallSessionMergePending = false;
        }
        this.mMergeHost = null;
        this.mMergePeer = null;
        this.mUpdateRequest = 0;
        this.mCallSessionMergePending = false;
    }

    private void setMergePeer(ImsCall mergePeer) {
        this.mMergePeer = mergePeer;
        this.mMergeHost = null;
        mergePeer.mMergeHost = this;
        mergePeer.mMergePeer = null;
    }

    public void setMergeHost(ImsCall mergeHost) {
        this.mMergeHost = mergeHost;
        this.mMergePeer = null;
        mergeHost.mMergeHost = null;
        mergeHost.mMergePeer = this;
    }

    private boolean isMerging() {
        if (this.mMergePeer == null && this.mMergeHost == null) {
            return false;
        }
        return CONF_DBG;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isMergeHost() {
        if (this.mMergePeer == null || this.mMergeHost != null) {
            return false;
        }
        return CONF_DBG;
    }

    private boolean isMergePeer() {
        if (this.mMergePeer != null || this.mMergeHost == null) {
            return false;
        }
        return CONF_DBG;
    }

    public boolean isCallSessionMergePending() {
        return this.mCallSessionMergePending;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setCallSessionMergePending(boolean callSessionMergePending) {
        this.mCallSessionMergePending = callSessionMergePending;
    }

    private boolean shouldProcessConferenceResult() {
        boolean areMergeTriggersDone = false;
        synchronized (this) {
            boolean z = false;
            if (!isMergeHost() && !isMergePeer()) {
                loge("shouldProcessConferenceResult :: no merge in progress");
                return false;
            }
            if (isMergeHost()) {
                logi("shouldProcessConferenceResult :: We are a merge host");
                logi("shouldProcessConferenceResult :: Here is the merge peer=" + this.mMergePeer);
                if (!isCallSessionMergePending() && !this.mMergePeer.isCallSessionMergePending()) {
                    z = true;
                }
                areMergeTriggersDone = z;
                if (!isMultiparty()) {
                    areMergeTriggersDone &= isSessionAlive(this.mTransientConferenceSession);
                }
            } else if (isMergePeer()) {
                logi("shouldProcessConferenceResult :: We are a merge peer");
                logi("shouldProcessConferenceResult :: Here is the merge host=" + this.mMergeHost);
                if (!isCallSessionMergePending() && !this.mMergeHost.isCallSessionMergePending()) {
                    z = true;
                }
                boolean areMergeTriggersDone2 = z;
                if (!this.mMergeHost.isMultiparty()) {
                    areMergeTriggersDone = areMergeTriggersDone2 & isSessionAlive(this.mMergeHost.mTransientConferenceSession);
                } else {
                    areMergeTriggersDone = isCallSessionMergePending() ^ CONF_DBG;
                }
            } else {
                loge("shouldProcessConferenceResult : merge in progress but call is neither host nor peer.");
            }
            logi("shouldProcessConferenceResult :: returning:" + (areMergeTriggersDone ? ImsManager.TRUE : ImsManager.FALSE));
            return areMergeTriggersDone;
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[ImsCall objId:");
        sb.append(System.identityHashCode(this));
        sb.append(" onHold:");
        sb.append(isOnHold() ? "Y" : "N");
        sb.append(" mute:");
        sb.append(isMuted() ? "Y" : "N");
        ImsCallProfile imsCallProfile = this.mCallProfile;
        if (imsCallProfile != null) {
            sb.append(" mCallProfile:" + imsCallProfile);
            sb.append(" networkType:");
            sb.append(getNetworkType());
        }
        sb.append(" updateRequest:");
        sb.append(updateRequestToString(this.mUpdateRequest));
        sb.append(" merging:");
        sb.append(isMerging() ? "Y" : "N");
        if (isMerging()) {
            if (isMergePeer()) {
                sb.append("P");
            } else {
                sb.append("H");
            }
        }
        sb.append(" merge action pending:");
        sb.append(isCallSessionMergePending() ? "Y" : "N");
        sb.append(" merged:");
        sb.append(isMerged() ? "Y" : "N");
        sb.append(" multiParty:");
        sb.append(isMultiparty() ? "Y" : "N");
        sb.append(" confHost:");
        sb.append(isConferenceHost() ? "Y" : "N");
        sb.append(" buried term:");
        sb.append(this.mSessionEndDuringMerge ? "Y" : "N");
        sb.append(" isVideo: ");
        sb.append(isVideoCall() ? "Y" : "N");
        sb.append(" wasVideo: ");
        sb.append(this.mWasVideoCall ? "Y" : "N");
        sb.append(" isWifi: ");
        sb.append(isWifiCall() ? "Y" : "N");
        sb.append(" session:");
        sb.append(this.mSession);
        sb.append(" transientSession:");
        sb.append(this.mTransientConferenceSession);
        sb.append("]");
        return sb.toString();
    }

    private void throwImsException(Throwable t, int code) throws ImsException {
        if (t instanceof ImsException) {
            throw ((ImsException) t);
        }
        throw new ImsException(String.valueOf(code), t, code);
    }

    private String appendImsCallInfoToString(String s) {
        return s + " ImsCall=" + this;
    }

    private void trackVideoStateHistory(ImsCallProfile profile) {
        this.mWasVideoCall = (this.mWasVideoCall || (profile != null && profile.isVideoCall())) ? CONF_DBG : false;
    }

    public boolean wasVideoCall() {
        return this.mWasVideoCall;
    }

    public boolean isVideoCall() {
        boolean z;
        synchronized (this.mLockObj) {
            ImsCallProfile imsCallProfile = this.mCallProfile;
            z = (imsCallProfile == null || !imsCallProfile.isVideoCall()) ? false : CONF_DBG;
        }
        return z;
    }

    public boolean isWifiCall() {
        synchronized (this.mLockObj) {
            if (this.mCallProfile == null) {
                return false;
            }
            return getNetworkType() == 18 ? CONF_DBG : false;
        }
    }

    public int getNetworkType() {
        synchronized (this.mLockObj) {
            ImsCallProfile imsCallProfile = this.mCallProfile;
            if (imsCallProfile == null) {
                return 0;
            }
            int networkType = imsCallProfile.getCallExtraInt("android.telephony.ims.extra.CALL_NETWORK_TYPE", 0);
            if (networkType == 0) {
                String oldRatType = this.mCallProfile.getCallExtra("CallRadioTech");
                if (TextUtils.isEmpty(oldRatType)) {
                    oldRatType = this.mCallProfile.getCallExtra("callRadioTech");
                }
                try {
                    int oldRatTypeConverted = Integer.parseInt(oldRatType);
                    networkType = ServiceState.rilRadioTechnologyToNetworkType(oldRatTypeConverted);
                } catch (NumberFormatException e) {
                    networkType = 0;
                }
            }
            return networkType;
        }
    }

    public boolean isCrossSimCall() {
        synchronized (this.mLockObj) {
            ImsCallProfile imsCallProfile = this.mCallProfile;
            if (imsCallProfile == null) {
                return false;
            }
            return imsCallProfile.getCallExtraBoolean("android.telephony.ims.extra.IS_CROSS_SIM_CALL", false);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void logi(String s) {
        Log.i(TAG, appendImsCallInfoToString(s));
    }

    private void logd(String s) {
        Log.d(TAG, appendImsCallInfoToString(s));
    }

    private void logv(String s) {
        Log.v(TAG, appendImsCallInfoToString(s));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void loge(String s) {
        Log.e(TAG, appendImsCallInfoToString(s));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void loge(String s, Throwable t) {
        Log.e(TAG, appendImsCallInfoToString(s), t);
    }
}
