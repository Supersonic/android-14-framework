package android.telephony.ims;

import android.app.PendingIntent$$ExternalSyntheticLambda1;
import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.p008os.Message;
import android.p008os.RemoteException;
import android.telephony.CallQuality;
import android.telephony.ims.ImsCallSession;
import android.telephony.ims.aidl.IImsCallSessionListener;
import android.util.ArraySet;
import android.util.Log;
import com.android.ims.internal.IImsCallSession;
import com.android.ims.internal.IImsVideoCallProvider;
import com.android.internal.telephony.util.TelephonyUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;
/* loaded from: classes3.dex */
public class ImsCallSession {
    private static final String TAG = "ImsCallSession";
    private String mCallId;
    private boolean mClosed;
    private IImsCallSessionListenerProxy mIImsCallSessionListenerProxy;
    private Listener mListener;
    private Executor mListenerExecutor;
    private final IImsCallSession miSession;

    /* loaded from: classes3.dex */
    public static class State {
        public static final int ESTABLISHED = 4;
        public static final int ESTABLISHING = 3;
        public static final int IDLE = 0;
        public static final int INITIATED = 1;
        public static final int INVALID = -1;
        public static final int NEGOTIATING = 2;
        public static final int REESTABLISHING = 6;
        public static final int RENEGOTIATING = 5;
        public static final int TERMINATED = 8;
        public static final int TERMINATING = 7;

        public static String toString(int state) {
            switch (state) {
                case 0:
                    return "IDLE";
                case 1:
                    return "INITIATED";
                case 2:
                    return "NEGOTIATING";
                case 3:
                    return "ESTABLISHING";
                case 4:
                    return "ESTABLISHED";
                case 5:
                    return "RENEGOTIATING";
                case 6:
                    return "REESTABLISHING";
                case 7:
                    return "TERMINATING";
                case 8:
                    return "TERMINATED";
                default:
                    return "UNKNOWN";
            }
        }

        private State() {
        }
    }

    /* loaded from: classes3.dex */
    public static class Listener {
        public void callSessionInitiating(ImsCallSession session, ImsCallProfile profile) {
        }

        public void callSessionInitiatingFailed(ImsCallSession session, ImsReasonInfo reasonInfo) {
        }

        public void callSessionProgressing(ImsCallSession session, ImsStreamMediaProfile profile) {
        }

        public void callSessionStarted(ImsCallSession session, ImsCallProfile profile) {
        }

        public void callSessionStartFailed(ImsCallSession session, ImsReasonInfo reasonInfo) {
        }

        public void callSessionTerminated(ImsCallSession session, ImsReasonInfo reasonInfo) {
        }

        public void callSessionHeld(ImsCallSession session, ImsCallProfile profile) {
        }

        public void callSessionHoldFailed(ImsCallSession session, ImsReasonInfo reasonInfo) {
        }

        public void callSessionHoldReceived(ImsCallSession session, ImsCallProfile profile) {
        }

        public void callSessionResumed(ImsCallSession session, ImsCallProfile profile) {
        }

        public void callSessionResumeFailed(ImsCallSession session, ImsReasonInfo reasonInfo) {
        }

        public void callSessionResumeReceived(ImsCallSession session, ImsCallProfile profile) {
        }

        public void callSessionMergeStarted(ImsCallSession session, ImsCallSession newSession, ImsCallProfile profile) {
        }

        public void callSessionMergeComplete(ImsCallSession session) {
        }

        public void callSessionMergeFailed(ImsCallSession session, ImsReasonInfo reasonInfo) {
        }

        public void callSessionUpdated(ImsCallSession session, ImsCallProfile profile) {
        }

        public void callSessionUpdateFailed(ImsCallSession session, ImsReasonInfo reasonInfo) {
        }

        public void callSessionUpdateReceived(ImsCallSession session, ImsCallProfile profile) {
        }

        public void callSessionConferenceExtended(ImsCallSession session, ImsCallSession newSession, ImsCallProfile profile) {
        }

        public void callSessionConferenceExtendFailed(ImsCallSession session, ImsReasonInfo reasonInfo) {
        }

        public void callSessionConferenceExtendReceived(ImsCallSession session, ImsCallSession newSession, ImsCallProfile profile) {
        }

        public void callSessionInviteParticipantsRequestDelivered(ImsCallSession session) {
        }

        public void callSessionInviteParticipantsRequestFailed(ImsCallSession session, ImsReasonInfo reasonInfo) {
        }

        public void callSessionRemoveParticipantsRequestDelivered(ImsCallSession session) {
        }

        public void callSessionRemoveParticipantsRequestFailed(ImsCallSession session, ImsReasonInfo reasonInfo) {
        }

        public void callSessionConferenceStateUpdated(ImsCallSession session, ImsConferenceState state) {
        }

        public void callSessionUssdMessageReceived(ImsCallSession session, int mode, String ussdMessage) {
        }

        public void callSessionMayHandover(ImsCallSession session, int srcNetworkType, int targetNetworkType) {
        }

        public void callSessionHandover(ImsCallSession session, int srcNetworkType, int targetNetworkType, ImsReasonInfo reasonInfo) {
        }

        public void callSessionHandoverFailed(ImsCallSession session, int srcNetworkType, int targetNetworkType, ImsReasonInfo reasonInfo) {
        }

        public void callSessionTtyModeReceived(ImsCallSession session, int mode) {
        }

        public void callSessionMultipartyStateChanged(ImsCallSession session, boolean isMultiParty) {
        }

        public void callSessionSuppServiceReceived(ImsCallSession session, ImsSuppServiceNotification suppServiceInfo) {
        }

        public void callSessionRttModifyRequestReceived(ImsCallSession session, ImsCallProfile callProfile) {
        }

        public void callSessionRttModifyResponseReceived(int status) {
        }

        public void callSessionRttMessageReceived(String rttMessage) {
        }

        public void callSessionRttAudioIndicatorChanged(ImsStreamMediaProfile profile) {
        }

        public void callSessionTransferred(ImsCallSession session) {
        }

        public void callSessionTransferFailed(ImsCallSession session, ImsReasonInfo reasonInfo) {
        }

        public void callSessionDtmfReceived(char digit) {
        }

        public void callQualityChanged(CallQuality callQuality) {
        }

        public void callSessionRtpHeaderExtensionsReceived(Set<RtpHeaderExtension> extensions) {
        }

        public void callSessionSendAnbrQuery(int mediaType, int direction, int bitsPerSecond) {
        }
    }

    public ImsCallSession(IImsCallSession iSession) {
        this.mClosed = false;
        this.mCallId = null;
        this.mListenerExecutor = new PendingIntent$$ExternalSyntheticLambda1();
        this.mIImsCallSessionListenerProxy = null;
        this.miSession = iSession;
        IImsCallSessionListenerProxy iImsCallSessionListenerProxy = new IImsCallSessionListenerProxy();
        this.mIImsCallSessionListenerProxy = iImsCallSessionListenerProxy;
        if (iSession != null) {
            try {
                iSession.setListener(iImsCallSessionListenerProxy);
                return;
            } catch (RemoteException e) {
                return;
            }
        }
        this.mClosed = true;
    }

    public ImsCallSession(IImsCallSession iSession, Listener listener, Executor executor) {
        this(iSession);
        setListener(listener, executor);
    }

    public final IImsCallSessionListenerProxy getIImsCallSessionListenerProxy() {
        return this.mIImsCallSessionListenerProxy;
    }

    public void close() {
        synchronized (this) {
            if (this.mClosed) {
                return;
            }
            try {
                this.miSession.close();
                this.mClosed = true;
            } catch (RemoteException e) {
            }
        }
    }

    public String getCallId() {
        if (this.mClosed) {
            return null;
        }
        String str = this.mCallId;
        if (str != null) {
            return str;
        }
        try {
            String callId = this.miSession.getCallId();
            this.mCallId = callId;
            return callId;
        } catch (RemoteException e) {
            return null;
        }
    }

    public void setCallId(String callId) {
        if (callId != null) {
            this.mCallId = callId;
        }
    }

    public ImsCallProfile getCallProfile() {
        if (this.mClosed) {
            return null;
        }
        try {
            return this.miSession.getCallProfile();
        } catch (RemoteException e) {
            return null;
        }
    }

    public ImsCallProfile getLocalCallProfile() {
        if (this.mClosed) {
            return null;
        }
        try {
            return this.miSession.getLocalCallProfile();
        } catch (RemoteException e) {
            return null;
        }
    }

    public ImsCallProfile getRemoteCallProfile() {
        if (this.mClosed) {
            return null;
        }
        try {
            return this.miSession.getRemoteCallProfile();
        } catch (RemoteException e) {
            return null;
        }
    }

    public IImsVideoCallProvider getVideoCallProvider() {
        if (this.mClosed) {
            return null;
        }
        try {
            return this.miSession.getVideoCallProvider();
        } catch (RemoteException e) {
            return null;
        }
    }

    public String getProperty(String name) {
        if (this.mClosed) {
            return null;
        }
        try {
            return this.miSession.getProperty(name);
        } catch (RemoteException e) {
            return null;
        }
    }

    public int getState() {
        if (this.mClosed) {
            return -1;
        }
        try {
            return this.miSession.getState();
        } catch (RemoteException e) {
            return -1;
        }
    }

    public boolean isAlive() {
        if (this.mClosed) {
            return false;
        }
        int state = getState();
        switch (state) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
                return true;
            default:
                return false;
        }
    }

    public IImsCallSession getSession() {
        return this.miSession;
    }

    public boolean isInCall() {
        if (this.mClosed) {
            return false;
        }
        try {
            return this.miSession.isInCall();
        } catch (RemoteException e) {
            return false;
        }
    }

    public void setListener(Listener listener, Executor executor) {
        this.mListener = listener;
        if (executor != null) {
            this.mListenerExecutor = executor;
        }
    }

    public void setMute(boolean muted) {
        if (this.mClosed) {
            return;
        }
        try {
            this.miSession.setMute(muted);
        } catch (RemoteException e) {
        }
    }

    public void start(String callee, ImsCallProfile profile) {
        if (this.mClosed) {
            return;
        }
        try {
            this.miSession.start(callee, profile);
        } catch (RemoteException e) {
        }
    }

    public void start(String[] participants, ImsCallProfile profile) {
        if (this.mClosed) {
            return;
        }
        try {
            this.miSession.startConference(participants, profile);
        } catch (RemoteException e) {
        }
    }

    public void accept(int callType, ImsStreamMediaProfile profile) {
        if (this.mClosed) {
            return;
        }
        try {
            this.miSession.accept(callType, profile);
        } catch (RemoteException e) {
        }
    }

    public void deflect(String number) {
        if (this.mClosed) {
            return;
        }
        try {
            this.miSession.deflect(number);
        } catch (RemoteException e) {
        }
    }

    public void reject(int reason) {
        if (this.mClosed) {
            return;
        }
        try {
            this.miSession.reject(reason);
        } catch (RemoteException e) {
        }
    }

    public void transfer(String number, boolean isConfirmationRequired) {
        if (this.mClosed) {
            return;
        }
        try {
            this.miSession.transfer(number, isConfirmationRequired);
        } catch (RemoteException e) {
        }
    }

    public void transfer(ImsCallSession transferToSession) {
        if (!this.mClosed && transferToSession != null) {
            try {
                this.miSession.consultativeTransfer(transferToSession.getSession());
            } catch (RemoteException e) {
            }
        }
    }

    public void terminate(int reason) {
        if (this.mClosed) {
            return;
        }
        try {
            this.miSession.terminate(reason);
        } catch (RemoteException e) {
        }
    }

    public void hold(ImsStreamMediaProfile profile) {
        if (this.mClosed) {
            return;
        }
        try {
            this.miSession.hold(profile);
        } catch (RemoteException e) {
        }
    }

    public void resume(ImsStreamMediaProfile profile) {
        if (this.mClosed) {
            return;
        }
        try {
            this.miSession.resume(profile);
        } catch (RemoteException e) {
        }
    }

    public void merge() {
        if (this.mClosed) {
            return;
        }
        try {
            this.miSession.merge();
        } catch (RemoteException e) {
        }
    }

    public void update(int callType, ImsStreamMediaProfile profile) {
        if (this.mClosed) {
            return;
        }
        try {
            this.miSession.update(callType, profile);
        } catch (RemoteException e) {
        }
    }

    public void extendToConference(String[] participants) {
        if (this.mClosed) {
            return;
        }
        try {
            this.miSession.extendToConference(participants);
        } catch (RemoteException e) {
        }
    }

    public void inviteParticipants(String[] participants) {
        if (this.mClosed) {
            return;
        }
        try {
            this.miSession.inviteParticipants(participants);
        } catch (RemoteException e) {
        }
    }

    public void removeParticipants(String[] participants) {
        if (this.mClosed) {
            return;
        }
        try {
            this.miSession.removeParticipants(participants);
        } catch (RemoteException e) {
        }
    }

    public void sendDtmf(char c, Message result) {
        if (this.mClosed) {
            return;
        }
        try {
            this.miSession.sendDtmf(c, result);
        } catch (RemoteException e) {
        }
    }

    public void startDtmf(char c) {
        if (this.mClosed) {
            return;
        }
        try {
            this.miSession.startDtmf(c);
        } catch (RemoteException e) {
        }
    }

    public void stopDtmf() {
        if (this.mClosed) {
            return;
        }
        try {
            this.miSession.stopDtmf();
        } catch (RemoteException e) {
        }
    }

    public void sendUssd(String ussdMessage) {
        if (this.mClosed) {
            return;
        }
        try {
            this.miSession.sendUssd(ussdMessage);
        } catch (RemoteException e) {
        }
    }

    public boolean isMultiparty() {
        if (this.mClosed) {
            return false;
        }
        try {
            return this.miSession.isMultiparty();
        } catch (RemoteException e) {
            return false;
        }
    }

    public void sendRttMessage(String rttMessage) {
        if (this.mClosed) {
            return;
        }
        try {
            this.miSession.sendRttMessage(rttMessage);
        } catch (RemoteException e) {
        }
    }

    public void sendRttModifyRequest(ImsCallProfile to) {
        if (this.mClosed) {
            return;
        }
        try {
            this.miSession.sendRttModifyRequest(to);
        } catch (RemoteException e) {
        }
    }

    public void sendRttModifyResponse(boolean response) {
        if (this.mClosed) {
            return;
        }
        try {
            this.miSession.sendRttModifyResponse(response);
        } catch (RemoteException e) {
        }
    }

    public void sendRtpHeaderExtensions(Set<RtpHeaderExtension> rtpHeaderExtensions) {
        if (this.mClosed) {
            return;
        }
        try {
            this.miSession.sendRtpHeaderExtensions(new ArrayList(rtpHeaderExtensions));
        } catch (RemoteException e) {
        }
    }

    public void callSessionNotifyAnbr(int mediaType, int direction, int bitsPerSecond) {
        if (this.mClosed) {
            return;
        }
        try {
            this.miSession.callSessionNotifyAnbr(mediaType, direction, bitsPerSecond);
        } catch (RemoteException e) {
            Log.m110e(TAG, "callSessionNotifyAnbr" + e);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public class IImsCallSessionListenerProxy extends IImsCallSessionListener.Stub {
        private IImsCallSessionListenerProxy() {
        }

        @Override // android.telephony.ims.aidl.IImsCallSessionListener
        public void callSessionInitiating(final ImsCallProfile profile) {
            TelephonyUtils.runWithCleanCallingIdentity(new Runnable() { // from class: android.telephony.ims.ImsCallSession$IImsCallSessionListenerProxy$$ExternalSyntheticLambda41
                @Override // java.lang.Runnable
                public final void run() {
                    ImsCallSession.IImsCallSessionListenerProxy.this.lambda$callSessionInitiating$0(profile);
                }
            }, ImsCallSession.this.mListenerExecutor);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$callSessionInitiating$0(ImsCallProfile profile) {
            if (ImsCallSession.this.mListener != null) {
                ImsCallSession.this.mListener.callSessionInitiating(ImsCallSession.this, profile);
            }
        }

        @Override // android.telephony.ims.aidl.IImsCallSessionListener
        public void callSessionProgressing(final ImsStreamMediaProfile profile) {
            TelephonyUtils.runWithCleanCallingIdentity(new Runnable() { // from class: android.telephony.ims.ImsCallSession$IImsCallSessionListenerProxy$$ExternalSyntheticLambda7
                @Override // java.lang.Runnable
                public final void run() {
                    ImsCallSession.IImsCallSessionListenerProxy.this.lambda$callSessionProgressing$1(profile);
                }
            }, ImsCallSession.this.mListenerExecutor);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$callSessionProgressing$1(ImsStreamMediaProfile profile) {
            if (ImsCallSession.this.mListener != null) {
                ImsCallSession.this.mListener.callSessionProgressing(ImsCallSession.this, profile);
            }
        }

        @Override // android.telephony.ims.aidl.IImsCallSessionListener
        public void callSessionInitiated(final ImsCallProfile profile) {
            TelephonyUtils.runWithCleanCallingIdentity(new Runnable() { // from class: android.telephony.ims.ImsCallSession$IImsCallSessionListenerProxy$$ExternalSyntheticLambda16
                @Override // java.lang.Runnable
                public final void run() {
                    ImsCallSession.IImsCallSessionListenerProxy.this.lambda$callSessionInitiated$2(profile);
                }
            }, ImsCallSession.this.mListenerExecutor);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$callSessionInitiated$2(ImsCallProfile profile) {
            if (ImsCallSession.this.mListener != null) {
                ImsCallSession.this.mListener.callSessionStarted(ImsCallSession.this, profile);
            }
        }

        @Override // android.telephony.ims.aidl.IImsCallSessionListener
        public void callSessionInitiatingFailed(final ImsReasonInfo reasonInfo) {
            TelephonyUtils.runWithCleanCallingIdentity(new Runnable() { // from class: android.telephony.ims.ImsCallSession$IImsCallSessionListenerProxy$$ExternalSyntheticLambda26
                @Override // java.lang.Runnable
                public final void run() {
                    ImsCallSession.IImsCallSessionListenerProxy.this.lambda$callSessionInitiatingFailed$3(reasonInfo);
                }
            }, ImsCallSession.this.mListenerExecutor);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$callSessionInitiatingFailed$3(ImsReasonInfo reasonInfo) {
            if (ImsCallSession.this.mListener != null) {
                ImsCallSession.this.mListener.callSessionStartFailed(ImsCallSession.this, reasonInfo);
            }
        }

        @Override // android.telephony.ims.aidl.IImsCallSessionListener
        public void callSessionInitiatedFailed(final ImsReasonInfo reasonInfo) {
            TelephonyUtils.runWithCleanCallingIdentity(new Runnable() { // from class: android.telephony.ims.ImsCallSession$IImsCallSessionListenerProxy$$ExternalSyntheticLambda14
                @Override // java.lang.Runnable
                public final void run() {
                    ImsCallSession.IImsCallSessionListenerProxy.this.lambda$callSessionInitiatedFailed$4(reasonInfo);
                }
            }, ImsCallSession.this.mListenerExecutor);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$callSessionInitiatedFailed$4(ImsReasonInfo reasonInfo) {
            if (ImsCallSession.this.mListener != null) {
                ImsCallSession.this.mListener.callSessionStartFailed(ImsCallSession.this, reasonInfo);
            }
        }

        @Override // android.telephony.ims.aidl.IImsCallSessionListener
        public void callSessionTerminated(final ImsReasonInfo reasonInfo) {
            TelephonyUtils.runWithCleanCallingIdentity(new Runnable() { // from class: android.telephony.ims.ImsCallSession$IImsCallSessionListenerProxy$$ExternalSyntheticLambda25
                @Override // java.lang.Runnable
                public final void run() {
                    ImsCallSession.IImsCallSessionListenerProxy.this.lambda$callSessionTerminated$5(reasonInfo);
                }
            }, ImsCallSession.this.mListenerExecutor);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$callSessionTerminated$5(ImsReasonInfo reasonInfo) {
            if (ImsCallSession.this.mListener != null) {
                ImsCallSession.this.mListener.callSessionTerminated(ImsCallSession.this, reasonInfo);
            }
        }

        @Override // android.telephony.ims.aidl.IImsCallSessionListener
        public void callSessionHeld(final ImsCallProfile profile) {
            TelephonyUtils.runWithCleanCallingIdentity(new Runnable() { // from class: android.telephony.ims.ImsCallSession$IImsCallSessionListenerProxy$$ExternalSyntheticLambda31
                @Override // java.lang.Runnable
                public final void run() {
                    ImsCallSession.IImsCallSessionListenerProxy.this.lambda$callSessionHeld$6(profile);
                }
            }, ImsCallSession.this.mListenerExecutor);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$callSessionHeld$6(ImsCallProfile profile) {
            if (ImsCallSession.this.mListener != null) {
                ImsCallSession.this.mListener.callSessionHeld(ImsCallSession.this, profile);
            }
        }

        @Override // android.telephony.ims.aidl.IImsCallSessionListener
        public void callSessionHoldFailed(final ImsReasonInfo reasonInfo) {
            TelephonyUtils.runWithCleanCallingIdentity(new Runnable() { // from class: android.telephony.ims.ImsCallSession$IImsCallSessionListenerProxy$$ExternalSyntheticLambda4
                @Override // java.lang.Runnable
                public final void run() {
                    ImsCallSession.IImsCallSessionListenerProxy.this.lambda$callSessionHoldFailed$7(reasonInfo);
                }
            }, ImsCallSession.this.mListenerExecutor);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$callSessionHoldFailed$7(ImsReasonInfo reasonInfo) {
            if (ImsCallSession.this.mListener != null) {
                ImsCallSession.this.mListener.callSessionHoldFailed(ImsCallSession.this, reasonInfo);
            }
        }

        @Override // android.telephony.ims.aidl.IImsCallSessionListener
        public void callSessionHoldReceived(final ImsCallProfile profile) {
            TelephonyUtils.runWithCleanCallingIdentity(new Runnable() { // from class: android.telephony.ims.ImsCallSession$IImsCallSessionListenerProxy$$ExternalSyntheticLambda34
                @Override // java.lang.Runnable
                public final void run() {
                    ImsCallSession.IImsCallSessionListenerProxy.this.lambda$callSessionHoldReceived$8(profile);
                }
            }, ImsCallSession.this.mListenerExecutor);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$callSessionHoldReceived$8(ImsCallProfile profile) {
            if (ImsCallSession.this.mListener != null) {
                ImsCallSession.this.mListener.callSessionHoldReceived(ImsCallSession.this, profile);
            }
        }

        @Override // android.telephony.ims.aidl.IImsCallSessionListener
        public void callSessionResumed(final ImsCallProfile profile) {
            TelephonyUtils.runWithCleanCallingIdentity(new Runnable() { // from class: android.telephony.ims.ImsCallSession$IImsCallSessionListenerProxy$$ExternalSyntheticLambda17
                @Override // java.lang.Runnable
                public final void run() {
                    ImsCallSession.IImsCallSessionListenerProxy.this.lambda$callSessionResumed$9(profile);
                }
            }, ImsCallSession.this.mListenerExecutor);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$callSessionResumed$9(ImsCallProfile profile) {
            if (ImsCallSession.this.mListener != null) {
                ImsCallSession.this.mListener.callSessionResumed(ImsCallSession.this, profile);
            }
        }

        @Override // android.telephony.ims.aidl.IImsCallSessionListener
        public void callSessionResumeFailed(final ImsReasonInfo reasonInfo) {
            TelephonyUtils.runWithCleanCallingIdentity(new Runnable() { // from class: android.telephony.ims.ImsCallSession$IImsCallSessionListenerProxy$$ExternalSyntheticLambda23
                @Override // java.lang.Runnable
                public final void run() {
                    ImsCallSession.IImsCallSessionListenerProxy.this.lambda$callSessionResumeFailed$10(reasonInfo);
                }
            }, ImsCallSession.this.mListenerExecutor);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$callSessionResumeFailed$10(ImsReasonInfo reasonInfo) {
            if (ImsCallSession.this.mListener != null) {
                ImsCallSession.this.mListener.callSessionResumeFailed(ImsCallSession.this, reasonInfo);
            }
        }

        @Override // android.telephony.ims.aidl.IImsCallSessionListener
        public void callSessionResumeReceived(final ImsCallProfile profile) {
            TelephonyUtils.runWithCleanCallingIdentity(new Runnable() { // from class: android.telephony.ims.ImsCallSession$IImsCallSessionListenerProxy$$ExternalSyntheticLambda22
                @Override // java.lang.Runnable
                public final void run() {
                    ImsCallSession.IImsCallSessionListenerProxy.this.lambda$callSessionResumeReceived$11(profile);
                }
            }, ImsCallSession.this.mListenerExecutor);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$callSessionResumeReceived$11(ImsCallProfile profile) {
            if (ImsCallSession.this.mListener != null) {
                ImsCallSession.this.mListener.callSessionResumeReceived(ImsCallSession.this, profile);
            }
        }

        @Override // android.telephony.ims.aidl.IImsCallSessionListener
        public void callSessionMergeStarted(IImsCallSession newSession, ImsCallProfile profile) {
            Log.m112d(ImsCallSession.TAG, "callSessionMergeStarted");
        }

        @Override // android.telephony.ims.aidl.IImsCallSessionListener
        public void callSessionMergeComplete(final IImsCallSession newSession) {
            TelephonyUtils.runWithCleanCallingIdentity(new Runnable() { // from class: android.telephony.ims.ImsCallSession$IImsCallSessionListenerProxy$$ExternalSyntheticLambda11
                @Override // java.lang.Runnable
                public final void run() {
                    ImsCallSession.IImsCallSessionListenerProxy.this.lambda$callSessionMergeComplete$12(newSession);
                }
            }, ImsCallSession.this.mListenerExecutor);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$callSessionMergeComplete$12(IImsCallSession newSession) {
            if (ImsCallSession.this.mListener != null) {
                if (newSession != null) {
                    ImsCallSession.this.mListener.callSessionMergeComplete(new ImsCallSession(newSession));
                } else {
                    ImsCallSession.this.mListener.callSessionMergeComplete(null);
                }
            }
        }

        @Override // android.telephony.ims.aidl.IImsCallSessionListener
        public void callSessionMergeFailed(final ImsReasonInfo reasonInfo) {
            TelephonyUtils.runWithCleanCallingIdentity(new Runnable() { // from class: android.telephony.ims.ImsCallSession$IImsCallSessionListenerProxy$$ExternalSyntheticLambda20
                @Override // java.lang.Runnable
                public final void run() {
                    ImsCallSession.IImsCallSessionListenerProxy.this.lambda$callSessionMergeFailed$13(reasonInfo);
                }
            }, ImsCallSession.this.mListenerExecutor);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$callSessionMergeFailed$13(ImsReasonInfo reasonInfo) {
            if (ImsCallSession.this.mListener != null) {
                ImsCallSession.this.mListener.callSessionMergeFailed(ImsCallSession.this, reasonInfo);
            }
        }

        @Override // android.telephony.ims.aidl.IImsCallSessionListener
        public void callSessionUpdated(final ImsCallProfile profile) {
            TelephonyUtils.runWithCleanCallingIdentity(new Runnable() { // from class: android.telephony.ims.ImsCallSession$IImsCallSessionListenerProxy$$ExternalSyntheticLambda36
                @Override // java.lang.Runnable
                public final void run() {
                    ImsCallSession.IImsCallSessionListenerProxy.this.lambda$callSessionUpdated$14(profile);
                }
            }, ImsCallSession.this.mListenerExecutor);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$callSessionUpdated$14(ImsCallProfile profile) {
            if (ImsCallSession.this.mListener != null) {
                ImsCallSession.this.mListener.callSessionUpdated(ImsCallSession.this, profile);
            }
        }

        @Override // android.telephony.ims.aidl.IImsCallSessionListener
        public void callSessionUpdateFailed(final ImsReasonInfo reasonInfo) {
            TelephonyUtils.runWithCleanCallingIdentity(new Runnable() { // from class: android.telephony.ims.ImsCallSession$IImsCallSessionListenerProxy$$ExternalSyntheticLambda21
                @Override // java.lang.Runnable
                public final void run() {
                    ImsCallSession.IImsCallSessionListenerProxy.this.lambda$callSessionUpdateFailed$15(reasonInfo);
                }
            }, ImsCallSession.this.mListenerExecutor);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$callSessionUpdateFailed$15(ImsReasonInfo reasonInfo) {
            if (ImsCallSession.this.mListener != null) {
                ImsCallSession.this.mListener.callSessionUpdateFailed(ImsCallSession.this, reasonInfo);
            }
        }

        @Override // android.telephony.ims.aidl.IImsCallSessionListener
        public void callSessionUpdateReceived(final ImsCallProfile profile) {
            TelephonyUtils.runWithCleanCallingIdentity(new Runnable() { // from class: android.telephony.ims.ImsCallSession$IImsCallSessionListenerProxy$$ExternalSyntheticLambda24
                @Override // java.lang.Runnable
                public final void run() {
                    ImsCallSession.IImsCallSessionListenerProxy.this.lambda$callSessionUpdateReceived$16(profile);
                }
            }, ImsCallSession.this.mListenerExecutor);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$callSessionUpdateReceived$16(ImsCallProfile profile) {
            if (ImsCallSession.this.mListener != null) {
                ImsCallSession.this.mListener.callSessionUpdateReceived(ImsCallSession.this, profile);
            }
        }

        @Override // android.telephony.ims.aidl.IImsCallSessionListener
        public void callSessionConferenceExtended(final IImsCallSession newSession, final ImsCallProfile profile) {
            TelephonyUtils.runWithCleanCallingIdentity(new Runnable() { // from class: android.telephony.ims.ImsCallSession$IImsCallSessionListenerProxy$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    ImsCallSession.IImsCallSessionListenerProxy.this.lambda$callSessionConferenceExtended$17(newSession, profile);
                }
            }, ImsCallSession.this.mListenerExecutor);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$callSessionConferenceExtended$17(IImsCallSession newSession, ImsCallProfile profile) {
            if (ImsCallSession.this.mListener != null) {
                ImsCallSession.this.mListener.callSessionConferenceExtended(ImsCallSession.this, new ImsCallSession(newSession), profile);
            }
        }

        @Override // android.telephony.ims.aidl.IImsCallSessionListener
        public void callSessionConferenceExtendFailed(final ImsReasonInfo reasonInfo) {
            TelephonyUtils.runWithCleanCallingIdentity(new Runnable() { // from class: android.telephony.ims.ImsCallSession$IImsCallSessionListenerProxy$$ExternalSyntheticLambda37
                @Override // java.lang.Runnable
                public final void run() {
                    ImsCallSession.IImsCallSessionListenerProxy.this.lambda$callSessionConferenceExtendFailed$18(reasonInfo);
                }
            }, ImsCallSession.this.mListenerExecutor);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$callSessionConferenceExtendFailed$18(ImsReasonInfo reasonInfo) {
            if (ImsCallSession.this.mListener != null) {
                ImsCallSession.this.mListener.callSessionConferenceExtendFailed(ImsCallSession.this, reasonInfo);
            }
        }

        @Override // android.telephony.ims.aidl.IImsCallSessionListener
        public void callSessionConferenceExtendReceived(final IImsCallSession newSession, final ImsCallProfile profile) {
            TelephonyUtils.runWithCleanCallingIdentity(new Runnable() { // from class: android.telephony.ims.ImsCallSession$IImsCallSessionListenerProxy$$ExternalSyntheticLambda28
                @Override // java.lang.Runnable
                public final void run() {
                    ImsCallSession.IImsCallSessionListenerProxy.this.lambda$callSessionConferenceExtendReceived$19(newSession, profile);
                }
            }, ImsCallSession.this.mListenerExecutor);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$callSessionConferenceExtendReceived$19(IImsCallSession newSession, ImsCallProfile profile) {
            if (ImsCallSession.this.mListener != null) {
                ImsCallSession.this.mListener.callSessionConferenceExtendReceived(ImsCallSession.this, new ImsCallSession(newSession), profile);
            }
        }

        @Override // android.telephony.ims.aidl.IImsCallSessionListener
        public void callSessionInviteParticipantsRequestDelivered() {
            TelephonyUtils.runWithCleanCallingIdentity(new Runnable() { // from class: android.telephony.ims.ImsCallSession$IImsCallSessionListenerProxy$$ExternalSyntheticLambda29
                @Override // java.lang.Runnable
                public final void run() {
                    ImsCallSession.IImsCallSessionListenerProxy.this.lambda$callSessionInviteParticipantsRequestDelivered$20();
                }
            }, ImsCallSession.this.mListenerExecutor);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$callSessionInviteParticipantsRequestDelivered$20() {
            if (ImsCallSession.this.mListener != null) {
                ImsCallSession.this.mListener.callSessionInviteParticipantsRequestDelivered(ImsCallSession.this);
            }
        }

        @Override // android.telephony.ims.aidl.IImsCallSessionListener
        public void callSessionInviteParticipantsRequestFailed(final ImsReasonInfo reasonInfo) {
            TelephonyUtils.runWithCleanCallingIdentity(new Runnable() { // from class: android.telephony.ims.ImsCallSession$IImsCallSessionListenerProxy$$ExternalSyntheticLambda3
                @Override // java.lang.Runnable
                public final void run() {
                    ImsCallSession.IImsCallSessionListenerProxy.this.lambda$callSessionInviteParticipantsRequestFailed$21(reasonInfo);
                }
            }, ImsCallSession.this.mListenerExecutor);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$callSessionInviteParticipantsRequestFailed$21(ImsReasonInfo reasonInfo) {
            if (ImsCallSession.this.mListener != null) {
                ImsCallSession.this.mListener.callSessionInviteParticipantsRequestFailed(ImsCallSession.this, reasonInfo);
            }
        }

        @Override // android.telephony.ims.aidl.IImsCallSessionListener
        public void callSessionRemoveParticipantsRequestDelivered() {
            TelephonyUtils.runWithCleanCallingIdentity(new Runnable() { // from class: android.telephony.ims.ImsCallSession$IImsCallSessionListenerProxy$$ExternalSyntheticLambda27
                @Override // java.lang.Runnable
                public final void run() {
                    ImsCallSession.IImsCallSessionListenerProxy.this.lambda$callSessionRemoveParticipantsRequestDelivered$22();
                }
            }, ImsCallSession.this.mListenerExecutor);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$callSessionRemoveParticipantsRequestDelivered$22() {
            if (ImsCallSession.this.mListener != null) {
                ImsCallSession.this.mListener.callSessionRemoveParticipantsRequestDelivered(ImsCallSession.this);
            }
        }

        @Override // android.telephony.ims.aidl.IImsCallSessionListener
        public void callSessionRemoveParticipantsRequestFailed(final ImsReasonInfo reasonInfo) {
            TelephonyUtils.runWithCleanCallingIdentity(new Runnable() { // from class: android.telephony.ims.ImsCallSession$IImsCallSessionListenerProxy$$ExternalSyntheticLambda10
                @Override // java.lang.Runnable
                public final void run() {
                    ImsCallSession.IImsCallSessionListenerProxy.this.lambda$callSessionRemoveParticipantsRequestFailed$23(reasonInfo);
                }
            }, ImsCallSession.this.mListenerExecutor);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$callSessionRemoveParticipantsRequestFailed$23(ImsReasonInfo reasonInfo) {
            if (ImsCallSession.this.mListener != null) {
                ImsCallSession.this.mListener.callSessionRemoveParticipantsRequestFailed(ImsCallSession.this, reasonInfo);
            }
        }

        @Override // android.telephony.ims.aidl.IImsCallSessionListener
        public void callSessionConferenceStateUpdated(final ImsConferenceState state) {
            TelephonyUtils.runWithCleanCallingIdentity(new Runnable() { // from class: android.telephony.ims.ImsCallSession$IImsCallSessionListenerProxy$$ExternalSyntheticLambda18
                @Override // java.lang.Runnable
                public final void run() {
                    ImsCallSession.IImsCallSessionListenerProxy.this.lambda$callSessionConferenceStateUpdated$24(state);
                }
            }, ImsCallSession.this.mListenerExecutor);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$callSessionConferenceStateUpdated$24(ImsConferenceState state) {
            if (ImsCallSession.this.mListener != null) {
                ImsCallSession.this.mListener.callSessionConferenceStateUpdated(ImsCallSession.this, state);
            }
        }

        @Override // android.telephony.ims.aidl.IImsCallSessionListener
        public void callSessionUssdMessageReceived(final int mode, final String ussdMessage) {
            TelephonyUtils.runWithCleanCallingIdentity(new Runnable() { // from class: android.telephony.ims.ImsCallSession$IImsCallSessionListenerProxy$$ExternalSyntheticLambda32
                @Override // java.lang.Runnable
                public final void run() {
                    ImsCallSession.IImsCallSessionListenerProxy.this.lambda$callSessionUssdMessageReceived$25(mode, ussdMessage);
                }
            }, ImsCallSession.this.mListenerExecutor);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$callSessionUssdMessageReceived$25(int mode, String ussdMessage) {
            if (ImsCallSession.this.mListener != null) {
                ImsCallSession.this.mListener.callSessionUssdMessageReceived(ImsCallSession.this, mode, ussdMessage);
            }
        }

        @Override // android.telephony.ims.aidl.IImsCallSessionListener
        public void callSessionMayHandover(final int srcNetworkType, final int targetNetworkType) {
            TelephonyUtils.runWithCleanCallingIdentity(new Runnable() { // from class: android.telephony.ims.ImsCallSession$IImsCallSessionListenerProxy$$ExternalSyntheticLambda33
                @Override // java.lang.Runnable
                public final void run() {
                    ImsCallSession.IImsCallSessionListenerProxy.this.lambda$callSessionMayHandover$26(srcNetworkType, targetNetworkType);
                }
            }, ImsCallSession.this.mListenerExecutor);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$callSessionMayHandover$26(int srcNetworkType, int targetNetworkType) {
            if (ImsCallSession.this.mListener != null) {
                ImsCallSession.this.mListener.callSessionMayHandover(ImsCallSession.this, srcNetworkType, targetNetworkType);
            }
        }

        @Override // android.telephony.ims.aidl.IImsCallSessionListener
        public void callSessionHandover(final int srcNetworkType, final int targetNetworkType, final ImsReasonInfo reasonInfo) {
            TelephonyUtils.runWithCleanCallingIdentity(new Runnable() { // from class: android.telephony.ims.ImsCallSession$IImsCallSessionListenerProxy$$ExternalSyntheticLambda5
                @Override // java.lang.Runnable
                public final void run() {
                    ImsCallSession.IImsCallSessionListenerProxy.this.lambda$callSessionHandover$27(srcNetworkType, targetNetworkType, reasonInfo);
                }
            }, ImsCallSession.this.mListenerExecutor);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$callSessionHandover$27(int srcNetworkType, int targetNetworkType, ImsReasonInfo reasonInfo) {
            if (ImsCallSession.this.mListener != null) {
                ImsCallSession.this.mListener.callSessionHandover(ImsCallSession.this, srcNetworkType, targetNetworkType, reasonInfo);
            }
        }

        @Override // android.telephony.ims.aidl.IImsCallSessionListener
        public void callSessionHandoverFailed(final int srcNetworkType, final int targetNetworkType, final ImsReasonInfo reasonInfo) {
            TelephonyUtils.runWithCleanCallingIdentity(new Runnable() { // from class: android.telephony.ims.ImsCallSession$IImsCallSessionListenerProxy$$ExternalSyntheticLambda15
                @Override // java.lang.Runnable
                public final void run() {
                    ImsCallSession.IImsCallSessionListenerProxy.this.lambda$callSessionHandoverFailed$28(srcNetworkType, targetNetworkType, reasonInfo);
                }
            }, ImsCallSession.this.mListenerExecutor);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$callSessionHandoverFailed$28(int srcNetworkType, int targetNetworkType, ImsReasonInfo reasonInfo) {
            if (ImsCallSession.this.mListener != null) {
                ImsCallSession.this.mListener.callSessionHandoverFailed(ImsCallSession.this, srcNetworkType, targetNetworkType, reasonInfo);
            }
        }

        @Override // android.telephony.ims.aidl.IImsCallSessionListener
        public void callSessionTtyModeReceived(final int mode) {
            TelephonyUtils.runWithCleanCallingIdentity(new Runnable() { // from class: android.telephony.ims.ImsCallSession$IImsCallSessionListenerProxy$$ExternalSyntheticLambda30
                @Override // java.lang.Runnable
                public final void run() {
                    ImsCallSession.IImsCallSessionListenerProxy.this.lambda$callSessionTtyModeReceived$29(mode);
                }
            }, ImsCallSession.this.mListenerExecutor);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$callSessionTtyModeReceived$29(int mode) {
            if (ImsCallSession.this.mListener != null) {
                ImsCallSession.this.mListener.callSessionTtyModeReceived(ImsCallSession.this, mode);
            }
        }

        @Override // android.telephony.ims.aidl.IImsCallSessionListener
        public void callSessionMultipartyStateChanged(final boolean isMultiParty) {
            TelephonyUtils.runWithCleanCallingIdentity(new Runnable() { // from class: android.telephony.ims.ImsCallSession$IImsCallSessionListenerProxy$$ExternalSyntheticLambda40
                @Override // java.lang.Runnable
                public final void run() {
                    ImsCallSession.IImsCallSessionListenerProxy.this.lambda$callSessionMultipartyStateChanged$30(isMultiParty);
                }
            }, ImsCallSession.this.mListenerExecutor);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$callSessionMultipartyStateChanged$30(boolean isMultiParty) {
            if (ImsCallSession.this.mListener != null) {
                ImsCallSession.this.mListener.callSessionMultipartyStateChanged(ImsCallSession.this, isMultiParty);
            }
        }

        @Override // android.telephony.ims.aidl.IImsCallSessionListener
        public void callSessionSuppServiceReceived(final ImsSuppServiceNotification suppServiceInfo) {
            TelephonyUtils.runWithCleanCallingIdentity(new Runnable() { // from class: android.telephony.ims.ImsCallSession$IImsCallSessionListenerProxy$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    ImsCallSession.IImsCallSessionListenerProxy.this.lambda$callSessionSuppServiceReceived$31(suppServiceInfo);
                }
            }, ImsCallSession.this.mListenerExecutor);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$callSessionSuppServiceReceived$31(ImsSuppServiceNotification suppServiceInfo) {
            if (ImsCallSession.this.mListener != null) {
                ImsCallSession.this.mListener.callSessionSuppServiceReceived(ImsCallSession.this, suppServiceInfo);
            }
        }

        @Override // android.telephony.ims.aidl.IImsCallSessionListener
        public void callSessionRttModifyRequestReceived(final ImsCallProfile callProfile) {
            TelephonyUtils.runWithCleanCallingIdentity(new Runnable() { // from class: android.telephony.ims.ImsCallSession$IImsCallSessionListenerProxy$$ExternalSyntheticLambda35
                @Override // java.lang.Runnable
                public final void run() {
                    ImsCallSession.IImsCallSessionListenerProxy.this.lambda$callSessionRttModifyRequestReceived$32(callProfile);
                }
            }, ImsCallSession.this.mListenerExecutor);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$callSessionRttModifyRequestReceived$32(ImsCallProfile callProfile) {
            if (ImsCallSession.this.mListener != null) {
                ImsCallSession.this.mListener.callSessionRttModifyRequestReceived(ImsCallSession.this, callProfile);
            }
        }

        @Override // android.telephony.ims.aidl.IImsCallSessionListener
        public void callSessionRttModifyResponseReceived(final int status) {
            TelephonyUtils.runWithCleanCallingIdentity(new Runnable() { // from class: android.telephony.ims.ImsCallSession$IImsCallSessionListenerProxy$$ExternalSyntheticLambda38
                @Override // java.lang.Runnable
                public final void run() {
                    ImsCallSession.IImsCallSessionListenerProxy.this.lambda$callSessionRttModifyResponseReceived$33(status);
                }
            }, ImsCallSession.this.mListenerExecutor);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$callSessionRttModifyResponseReceived$33(int status) {
            if (ImsCallSession.this.mListener != null) {
                ImsCallSession.this.mListener.callSessionRttModifyResponseReceived(status);
            }
        }

        @Override // android.telephony.ims.aidl.IImsCallSessionListener
        public void callSessionRttMessageReceived(final String rttMessage) {
            TelephonyUtils.runWithCleanCallingIdentity(new Runnable() { // from class: android.telephony.ims.ImsCallSession$IImsCallSessionListenerProxy$$ExternalSyntheticLambda9
                @Override // java.lang.Runnable
                public final void run() {
                    ImsCallSession.IImsCallSessionListenerProxy.this.lambda$callSessionRttMessageReceived$34(rttMessage);
                }
            }, ImsCallSession.this.mListenerExecutor);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$callSessionRttMessageReceived$34(String rttMessage) {
            if (ImsCallSession.this.mListener != null) {
                ImsCallSession.this.mListener.callSessionRttMessageReceived(rttMessage);
            }
        }

        @Override // android.telephony.ims.aidl.IImsCallSessionListener
        public void callSessionRttAudioIndicatorChanged(final ImsStreamMediaProfile profile) {
            TelephonyUtils.runWithCleanCallingIdentity(new Runnable() { // from class: android.telephony.ims.ImsCallSession$IImsCallSessionListenerProxy$$ExternalSyntheticLambda39
                @Override // java.lang.Runnable
                public final void run() {
                    ImsCallSession.IImsCallSessionListenerProxy.this.lambda$callSessionRttAudioIndicatorChanged$35(profile);
                }
            }, ImsCallSession.this.mListenerExecutor);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$callSessionRttAudioIndicatorChanged$35(ImsStreamMediaProfile profile) {
            if (ImsCallSession.this.mListener != null) {
                ImsCallSession.this.mListener.callSessionRttAudioIndicatorChanged(profile);
            }
        }

        @Override // android.telephony.ims.aidl.IImsCallSessionListener
        public void callSessionTransferred() {
            TelephonyUtils.runWithCleanCallingIdentity(new Runnable() { // from class: android.telephony.ims.ImsCallSession$IImsCallSessionListenerProxy$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    ImsCallSession.IImsCallSessionListenerProxy.this.lambda$callSessionTransferred$36();
                }
            }, ImsCallSession.this.mListenerExecutor);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$callSessionTransferred$36() {
            if (ImsCallSession.this.mListener != null) {
                ImsCallSession.this.mListener.callSessionTransferred(ImsCallSession.this);
            }
        }

        @Override // android.telephony.ims.aidl.IImsCallSessionListener
        public void callSessionTransferFailed(final ImsReasonInfo reasonInfo) {
            TelephonyUtils.runWithCleanCallingIdentity(new Runnable() { // from class: android.telephony.ims.ImsCallSession$IImsCallSessionListenerProxy$$ExternalSyntheticLambda6
                @Override // java.lang.Runnable
                public final void run() {
                    ImsCallSession.IImsCallSessionListenerProxy.this.lambda$callSessionTransferFailed$37(reasonInfo);
                }
            }, ImsCallSession.this.mListenerExecutor);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$callSessionTransferFailed$37(ImsReasonInfo reasonInfo) {
            if (ImsCallSession.this.mListener != null) {
                ImsCallSession.this.mListener.callSessionTransferFailed(ImsCallSession.this, reasonInfo);
            }
        }

        @Override // android.telephony.ims.aidl.IImsCallSessionListener
        public void callSessionDtmfReceived(final char dtmf) {
            TelephonyUtils.runWithCleanCallingIdentity(new Runnable() { // from class: android.telephony.ims.ImsCallSession$IImsCallSessionListenerProxy$$ExternalSyntheticLambda12
                @Override // java.lang.Runnable
                public final void run() {
                    ImsCallSession.IImsCallSessionListenerProxy.this.lambda$callSessionDtmfReceived$38(dtmf);
                }
            }, ImsCallSession.this.mListenerExecutor);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$callSessionDtmfReceived$38(char dtmf) {
            if (ImsCallSession.this.mListener != null) {
                ImsCallSession.this.mListener.callSessionDtmfReceived(dtmf);
            }
        }

        @Override // android.telephony.ims.aidl.IImsCallSessionListener
        public void callQualityChanged(final CallQuality callQuality) {
            TelephonyUtils.runWithCleanCallingIdentity(new Runnable() { // from class: android.telephony.ims.ImsCallSession$IImsCallSessionListenerProxy$$ExternalSyntheticLambda8
                @Override // java.lang.Runnable
                public final void run() {
                    ImsCallSession.IImsCallSessionListenerProxy.this.lambda$callQualityChanged$39(callQuality);
                }
            }, ImsCallSession.this.mListenerExecutor);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$callQualityChanged$39(CallQuality callQuality) {
            if (ImsCallSession.this.mListener != null) {
                ImsCallSession.this.mListener.callQualityChanged(callQuality);
            }
        }

        @Override // android.telephony.ims.aidl.IImsCallSessionListener
        public void callSessionRtpHeaderExtensionsReceived(final List<RtpHeaderExtension> extensions) {
            TelephonyUtils.runWithCleanCallingIdentity(new Runnable() { // from class: android.telephony.ims.ImsCallSession$IImsCallSessionListenerProxy$$ExternalSyntheticLambda13
                @Override // java.lang.Runnable
                public final void run() {
                    ImsCallSession.IImsCallSessionListenerProxy.this.lambda$callSessionRtpHeaderExtensionsReceived$40(extensions);
                }
            }, ImsCallSession.this.mListenerExecutor);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$callSessionRtpHeaderExtensionsReceived$40(List extensions) {
            if (ImsCallSession.this.mListener != null) {
                ImsCallSession.this.mListener.callSessionRtpHeaderExtensionsReceived(new ArraySet(extensions));
            }
        }

        @Override // android.telephony.ims.aidl.IImsCallSessionListener
        public void callSessionSendAnbrQuery(final int mediaType, final int direction, final int bitsPerSecond) {
            Log.m112d(ImsCallSession.TAG, "callSessionSendAnbrQuery in ImsCallSession");
            TelephonyUtils.runWithCleanCallingIdentity(new Runnable() { // from class: android.telephony.ims.ImsCallSession$IImsCallSessionListenerProxy$$ExternalSyntheticLambda19
                @Override // java.lang.Runnable
                public final void run() {
                    ImsCallSession.IImsCallSessionListenerProxy.this.lambda$callSessionSendAnbrQuery$41(mediaType, direction, bitsPerSecond);
                }
            }, ImsCallSession.this.mListenerExecutor);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$callSessionSendAnbrQuery$41(int mediaType, int direction, int bitsPerSecond) {
            if (ImsCallSession.this.mListener != null) {
                ImsCallSession.this.mListener.callSessionSendAnbrQuery(mediaType, direction, bitsPerSecond);
            }
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[ImsCallSession objId:");
        sb.append(System.identityHashCode(this));
        sb.append(" callId:");
        String str = this.mCallId;
        if (str == null) {
            str = "[UNINITIALIZED]";
        }
        sb.append(str);
        sb.append(NavigationBarInflaterView.SIZE_MOD_END);
        return sb.toString();
    }
}
