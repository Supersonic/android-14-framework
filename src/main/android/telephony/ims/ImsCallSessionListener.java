package android.telephony.ims;

import android.annotation.SystemApi;
import android.p008os.RemoteException;
import android.telephony.CallQuality;
import android.telephony.ServiceState;
import android.telephony.ims.aidl.IImsCallSessionListener;
import android.telephony.ims.stub.ImsCallSessionImplBase;
import android.util.Log;
import com.android.ims.internal.IImsCallSession;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Executor;
@SystemApi
/* loaded from: classes3.dex */
public class ImsCallSessionListener {
    private static final String TAG = "ImsCallSessionListener";
    private Executor mExecutor = null;
    private final IImsCallSessionListener mListener;

    public ImsCallSessionListener(IImsCallSessionListener l) {
        this.mListener = l;
    }

    public void callSessionInitiating(ImsCallProfile profile) {
        try {
            this.mListener.callSessionInitiating(profile);
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    public void callSessionInitiatingFailed(ImsReasonInfo reasonInfo) {
        try {
            this.mListener.callSessionInitiatingFailed(reasonInfo);
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    public void callSessionProgressing(ImsStreamMediaProfile profile) {
        try {
            this.mListener.callSessionProgressing(profile);
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    public void callSessionInitiated(ImsCallProfile profile) {
        try {
            this.mListener.callSessionInitiated(profile);
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    @Deprecated
    public void callSessionInitiatedFailed(ImsReasonInfo reasonInfo) {
        try {
            this.mListener.callSessionInitiatedFailed(reasonInfo);
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    public void callSessionTerminated(ImsReasonInfo reasonInfo) {
        try {
            this.mListener.callSessionTerminated(reasonInfo);
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    public void callSessionHeld(ImsCallProfile profile) {
        try {
            this.mListener.callSessionHeld(profile);
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    public void callSessionHoldFailed(ImsReasonInfo reasonInfo) {
        try {
            this.mListener.callSessionHoldFailed(reasonInfo);
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    public void callSessionHoldReceived(ImsCallProfile profile) {
        try {
            this.mListener.callSessionHoldReceived(profile);
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    public void callSessionResumed(ImsCallProfile profile) {
        try {
            this.mListener.callSessionResumed(profile);
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    public void callSessionResumeFailed(ImsReasonInfo reasonInfo) {
        try {
            this.mListener.callSessionResumeFailed(reasonInfo);
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    public void callSessionResumeReceived(ImsCallProfile profile) {
        try {
            this.mListener.callSessionResumeReceived(profile);
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    public void callSessionMergeStarted(ImsCallSessionImplBase newSession, ImsCallProfile profile) {
        if (newSession != null) {
            try {
                Executor executor = this.mExecutor;
                if (executor != null) {
                    newSession.setDefaultExecutor(executor);
                }
            } catch (RemoteException e) {
                e.rethrowFromSystemServer();
                return;
            }
        }
        this.mListener.callSessionMergeStarted(newSession != null ? newSession.getServiceImpl() : null, profile);
    }

    public void callSessionMergeStarted(IImsCallSession newSession, ImsCallProfile profile) {
        try {
            this.mListener.callSessionMergeStarted(newSession, profile);
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    public void callSessionMergeComplete(ImsCallSessionImplBase newSession) {
        if (newSession != null) {
            try {
                Executor executor = this.mExecutor;
                if (executor != null) {
                    newSession.setDefaultExecutor(executor);
                }
            } catch (RemoteException e) {
                e.rethrowFromSystemServer();
                return;
            }
        }
        this.mListener.callSessionMergeComplete(newSession != null ? newSession.getServiceImpl() : null);
    }

    public void callSessionMergeComplete(IImsCallSession newSession) {
        try {
            this.mListener.callSessionMergeComplete(newSession);
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    public void callSessionMergeFailed(ImsReasonInfo reasonInfo) {
        try {
            this.mListener.callSessionMergeFailed(reasonInfo);
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    public void callSessionUpdated(ImsCallProfile profile) {
        try {
            this.mListener.callSessionUpdated(profile);
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    public void callSessionUpdateFailed(ImsReasonInfo reasonInfo) {
        try {
            this.mListener.callSessionUpdateFailed(reasonInfo);
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    public void callSessionUpdateReceived(ImsCallProfile profile) {
        try {
            this.mListener.callSessionUpdateReceived(profile);
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    public void callSessionConferenceExtended(ImsCallSessionImplBase newSession, ImsCallProfile profile) {
        if (newSession != null) {
            try {
                Executor executor = this.mExecutor;
                if (executor != null) {
                    newSession.setDefaultExecutor(executor);
                }
            } catch (RemoteException e) {
                e.rethrowFromSystemServer();
                return;
            }
        }
        this.mListener.callSessionConferenceExtended(newSession != null ? newSession.getServiceImpl() : null, profile);
    }

    public void callSessionConferenceExtended(IImsCallSession newSession, ImsCallProfile profile) {
        try {
            this.mListener.callSessionConferenceExtended(newSession, profile);
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    public void callSessionConferenceExtendFailed(ImsReasonInfo reasonInfo) {
        try {
            this.mListener.callSessionConferenceExtendFailed(reasonInfo);
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    public void callSessionConferenceExtendReceived(ImsCallSessionImplBase newSession, ImsCallProfile profile) {
        if (newSession != null) {
            try {
                Executor executor = this.mExecutor;
                if (executor != null) {
                    newSession.setDefaultExecutor(executor);
                }
            } catch (RemoteException e) {
                e.rethrowFromSystemServer();
                return;
            }
        }
        this.mListener.callSessionConferenceExtendReceived(newSession != null ? newSession.getServiceImpl() : null, profile);
    }

    public void callSessionConferenceExtendReceived(IImsCallSession newSession, ImsCallProfile profile) {
        try {
            this.mListener.callSessionConferenceExtendReceived(newSession, profile);
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    public void callSessionInviteParticipantsRequestDelivered() {
        try {
            this.mListener.callSessionInviteParticipantsRequestDelivered();
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    public void callSessionInviteParticipantsRequestFailed(ImsReasonInfo reasonInfo) {
        try {
            this.mListener.callSessionInviteParticipantsRequestFailed(reasonInfo);
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    public void callSessionRemoveParticipantsRequestDelivered() {
        try {
            this.mListener.callSessionRemoveParticipantsRequestDelivered();
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    public void callSessionRemoveParticipantsRequestFailed(ImsReasonInfo reasonInfo) {
        try {
            this.mListener.callSessionInviteParticipantsRequestFailed(reasonInfo);
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    public void callSessionConferenceStateUpdated(ImsConferenceState state) {
        try {
            this.mListener.callSessionConferenceStateUpdated(state);
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    public void callSessionUssdMessageReceived(int mode, String ussdMessage) {
        try {
            this.mListener.callSessionUssdMessageReceived(mode, ussdMessage);
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    @Deprecated
    public void callSessionMayHandover(int srcAccessTech, int targetAccessTech) {
        onMayHandover(ServiceState.rilRadioTechnologyToNetworkType(srcAccessTech), ServiceState.rilRadioTechnologyToNetworkType(targetAccessTech));
    }

    public void onMayHandover(int srcNetworkType, int targetNetworkType) {
        try {
            this.mListener.callSessionMayHandover(srcNetworkType, targetNetworkType);
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    @Deprecated
    public void callSessionHandover(int srcAccessTech, int targetAccessTech, ImsReasonInfo reasonInfo) {
        onHandover(ServiceState.rilRadioTechnologyToNetworkType(srcAccessTech), ServiceState.rilRadioTechnologyToNetworkType(targetAccessTech), reasonInfo);
    }

    public void onHandover(int srcNetworkType, int targetNetworkType, ImsReasonInfo reasonInfo) {
        try {
            this.mListener.callSessionHandover(srcNetworkType, targetNetworkType, reasonInfo);
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    @Deprecated
    public void callSessionHandoverFailed(int srcAccessTech, int targetAccessTech, ImsReasonInfo reasonInfo) {
        onHandoverFailed(ServiceState.rilRadioTechnologyToNetworkType(srcAccessTech), ServiceState.rilRadioTechnologyToNetworkType(targetAccessTech), reasonInfo);
    }

    public void onHandoverFailed(int srcNetworkType, int targetNetworkType, ImsReasonInfo reasonInfo) {
        try {
            this.mListener.callSessionHandoverFailed(srcNetworkType, targetNetworkType, reasonInfo);
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    public void callSessionTtyModeReceived(int mode) {
        try {
            this.mListener.callSessionTtyModeReceived(mode);
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    public void callSessionMultipartyStateChanged(boolean isMultiParty) {
        try {
            this.mListener.callSessionMultipartyStateChanged(isMultiParty);
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    public void callSessionSuppServiceReceived(ImsSuppServiceNotification suppSrvNotification) {
        try {
            this.mListener.callSessionSuppServiceReceived(suppSrvNotification);
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    public void callSessionRttModifyRequestReceived(ImsCallProfile callProfile) {
        try {
            this.mListener.callSessionRttModifyRequestReceived(callProfile);
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    public void callSessionRttModifyResponseReceived(int status) {
        try {
            this.mListener.callSessionRttModifyResponseReceived(status);
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    public void callSessionRttMessageReceived(String rttMessage) {
        try {
            this.mListener.callSessionRttMessageReceived(rttMessage);
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    public void callSessionRttAudioIndicatorChanged(ImsStreamMediaProfile profile) {
        try {
            this.mListener.callSessionRttAudioIndicatorChanged(profile);
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    public void callQualityChanged(CallQuality callQuality) {
        try {
            this.mListener.callQualityChanged(callQuality);
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    public void callSessionDtmfReceived(char dtmf) {
        if ((dtmf < '0' || dtmf > '9') && ((dtmf < 'A' || dtmf > 'D') && ((dtmf < 'a' || dtmf > 'd') && dtmf != '*' && dtmf != '#'))) {
            throw new IllegalArgumentException("DTMF digit must be 0-9, *, #, A, B, C, D");
        }
        try {
            this.mListener.callSessionDtmfReceived(Character.toUpperCase(dtmf));
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    public void callSessionRtpHeaderExtensionsReceived(Set<RtpHeaderExtension> extensions) {
        Objects.requireNonNull(extensions, "extensions are required.");
        try {
            this.mListener.callSessionRtpHeaderExtensionsReceived(new ArrayList(extensions));
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    public void callSessionTransferred() {
        try {
            this.mListener.callSessionTransferred();
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    public void callSessionTransferFailed(ImsReasonInfo reasonInfo) {
        try {
            this.mListener.callSessionTransferFailed(reasonInfo);
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    public final void callSessionSendAnbrQuery(int mediaType, int direction, int bitsPerSecond) {
        Log.m112d(TAG, "callSessionSendAnbrQuery in imscallsessonListener");
        try {
            this.mListener.callSessionSendAnbrQuery(mediaType, direction, bitsPerSecond);
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    public final void setDefaultExecutor(Executor executor) {
        if (this.mExecutor == null) {
            this.mExecutor = executor;
        }
    }
}
