package android.telephony.mbms;

import android.p008os.RemoteException;
import android.telephony.MbmsGroupCallSession;
import android.telephony.mbms.vendor.IMbmsGroupCallService;
import android.util.Log;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;
/* loaded from: classes3.dex */
public class GroupCall implements AutoCloseable {
    private static final String LOG_TAG = "MbmsGroupCall";
    public static final int REASON_BY_USER_REQUEST = 1;
    public static final int REASON_FREQUENCY_CONFLICT = 3;
    public static final int REASON_LEFT_MBMS_BROADCAST_AREA = 6;
    public static final int REASON_NONE = 0;
    public static final int REASON_NOT_CONNECTED_TO_HOMECARRIER_LTE = 5;
    public static final int REASON_OUT_OF_MEMORY = 4;
    public static final int STATE_STALLED = 3;
    public static final int STATE_STARTED = 2;
    public static final int STATE_STOPPED = 1;
    private final InternalGroupCallCallback mCallback;
    private final MbmsGroupCallSession mParentSession;
    private IMbmsGroupCallService mService;
    private final int mSubscriptionId;
    private final long mTmgi;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface GroupCallState {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface GroupCallStateChangeReason {
    }

    public GroupCall(int subscriptionId, IMbmsGroupCallService service, MbmsGroupCallSession session, long tmgi, InternalGroupCallCallback callback) {
        this.mSubscriptionId = subscriptionId;
        this.mParentSession = session;
        this.mService = service;
        this.mTmgi = tmgi;
        this.mCallback = callback;
    }

    public long getTmgi() {
        return this.mTmgi;
    }

    public void updateGroupCall(List<Integer> saiList, List<Integer> frequencyList) {
        IMbmsGroupCallService iMbmsGroupCallService = this.mService;
        try {
            if (iMbmsGroupCallService == null) {
                throw new IllegalStateException("No group call service attached");
            }
            try {
                iMbmsGroupCallService.updateGroupCall(this.mSubscriptionId, this.mTmgi, saiList, frequencyList);
            } catch (RemoteException e) {
                Log.m104w(LOG_TAG, "Remote process died");
                this.mService = null;
                sendErrorToApp(3, null);
            }
        } finally {
            this.mParentSession.onGroupCallStopped(this);
        }
    }

    @Override // java.lang.AutoCloseable
    public void close() {
        IMbmsGroupCallService iMbmsGroupCallService = this.mService;
        try {
            if (iMbmsGroupCallService == null) {
                throw new IllegalStateException("No group call service attached");
            }
            try {
                iMbmsGroupCallService.stopGroupCall(this.mSubscriptionId, this.mTmgi);
            } catch (RemoteException e) {
                Log.m104w(LOG_TAG, "Remote process died");
                this.mService = null;
                sendErrorToApp(3, null);
            }
        } finally {
            this.mParentSession.onGroupCallStopped(this);
        }
    }

    public InternalGroupCallCallback getCallback() {
        return this.mCallback;
    }

    private void sendErrorToApp(int errorCode, String message) {
        this.mCallback.onError(errorCode, message);
    }
}
