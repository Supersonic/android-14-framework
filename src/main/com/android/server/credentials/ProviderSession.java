package com.android.server.credentials;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.credentials.CredentialProviderInfo;
import android.credentials.ui.ProviderData;
import android.credentials.ui.ProviderPendingIntentResponse;
import android.os.ICancellationSignal;
import android.os.RemoteException;
import android.util.Log;
import com.android.server.credentials.RemoteCredentialService;
import com.android.server.credentials.metrics.CandidatePhaseMetric;
import com.android.server.credentials.metrics.InitialPhaseMetric;
import com.android.server.credentials.metrics.ProviderStatusForMetrics;
import java.util.UUID;
/* loaded from: classes.dex */
public abstract class ProviderSession<T, R> implements RemoteCredentialService.ProviderCallbacks<R> {
    public final ProviderInternalCallback mCallbacks;
    public final ComponentName mComponentName;
    public final Context mContext;
    public ICancellationSignal mProviderCancellationSignal;
    public final CredentialProviderInfo mProviderInfo;
    public final T mProviderRequest;
    public R mProviderResponse;
    public int mProviderSessionUid;
    public final RemoteCredentialService mRemoteCredentialService;
    public final int mUserId;
    public Status mStatus = Status.NOT_STARTED;
    public Boolean mProviderResponseSet = Boolean.FALSE;
    public final CandidatePhaseMetric mCandidatePhasePerProviderMetric = new CandidatePhaseMetric();

    /* loaded from: classes.dex */
    public interface ProviderInternalCallback<V> {
        void onFinalErrorReceived(ComponentName componentName, String str, String str2);

        void onFinalResponseReceived(ComponentName componentName, V v);

        void onProviderStatusChanged(Status status, ComponentName componentName);
    }

    /* loaded from: classes.dex */
    public enum Status {
        NOT_STARTED,
        PENDING,
        REQUIRES_AUTHENTICATION,
        CREDENTIALS_RECEIVED,
        SERVICE_DEAD,
        CREDENTIAL_RECEIVED_FROM_INTENT,
        PENDING_INTENT_INVOKED,
        CREDENTIAL_RECEIVED_FROM_SELECTION,
        SAVE_ENTRIES_RECEIVED,
        CANCELED,
        NO_CREDENTIALS,
        EMPTY_RESPONSE,
        NO_CREDENTIALS_FROM_AUTH_ENTRY,
        COMPLETE
    }

    public abstract void invokeSession();

    public abstract void onUiEntrySelected(String str, String str2, ProviderPendingIntentResponse providerPendingIntentResponse);

    /* renamed from: prepareUiData */
    public abstract ProviderData mo2860prepareUiData();

    public static boolean isUiInvokingStatus(Status status) {
        return status == Status.CREDENTIALS_RECEIVED || status == Status.SAVE_ENTRIES_RECEIVED || status == Status.NO_CREDENTIALS_FROM_AUTH_ENTRY;
    }

    public static boolean isStatusWaitingForRemoteResponse(Status status) {
        return status == Status.PENDING;
    }

    public static boolean isTerminatingStatus(Status status) {
        return status == Status.CANCELED || status == Status.SERVICE_DEAD;
    }

    public static boolean isCompletionStatus(Status status) {
        return status == Status.CREDENTIAL_RECEIVED_FROM_INTENT || status == Status.CREDENTIAL_RECEIVED_FROM_SELECTION || status == Status.COMPLETE;
    }

    public ProviderSession(Context context, CredentialProviderInfo credentialProviderInfo, T t, ProviderInternalCallback providerInternalCallback, int i, RemoteCredentialService remoteCredentialService) {
        this.mContext = context;
        this.mProviderInfo = credentialProviderInfo;
        this.mProviderRequest = t;
        this.mCallbacks = providerInternalCallback;
        this.mUserId = i;
        ComponentName componentName = credentialProviderInfo.getServiceInfo().getComponentName();
        this.mComponentName = componentName;
        this.mRemoteCredentialService = remoteCredentialService;
        this.mProviderSessionUid = MetricUtilities.getPackageUid(context, componentName);
    }

    public static Status toStatus(int i) {
        return Status.CANCELED;
    }

    public static String generateUniqueId() {
        return UUID.randomUUID().toString();
    }

    public void cancelProviderRemoteSession() {
        try {
            ICancellationSignal iCancellationSignal = this.mProviderCancellationSignal;
            if (iCancellationSignal != null) {
                iCancellationSignal.cancel();
            }
            setStatus(Status.CANCELED);
        } catch (RemoteException e) {
            Log.i("ProviderSession", "Issue while cancelling provider session: " + e.getMessage());
        }
    }

    public void setStatus(Status status) {
        this.mStatus = status;
    }

    public Status getStatus() {
        return this.mStatus;
    }

    public ComponentName getComponentName() {
        return this.mComponentName;
    }

    public void captureCandidateFailureInMetrics() {
        this.mCandidatePhasePerProviderMetric.setHasException(true);
    }

    public void updateStatusAndInvokeCallback(Status status) {
        setStatus(status);
        updateCandidateMetric(status);
        this.mCallbacks.onProviderStatusChanged(status, this.mComponentName);
    }

    public final void updateCandidateMetric(Status status) {
        try {
            this.mCandidatePhasePerProviderMetric.setCandidateUid(this.mProviderSessionUid);
            this.mCandidatePhasePerProviderMetric.setQueryFinishTimeNanoseconds(System.nanoTime());
            if (isTerminatingStatus(status)) {
                this.mCandidatePhasePerProviderMetric.setQueryReturned(false);
                this.mCandidatePhasePerProviderMetric.setProviderQueryStatus(ProviderStatusForMetrics.QUERY_FAILURE.getMetricCode());
            } else if (isCompletionStatus(status)) {
                this.mCandidatePhasePerProviderMetric.setQueryReturned(true);
                this.mCandidatePhasePerProviderMetric.setProviderQueryStatus(ProviderStatusForMetrics.QUERY_SUCCESS.getMetricCode());
            }
        } catch (Exception e) {
            Log.w("ProviderSession", "Unexpected error during metric logging: " + e);
        }
    }

    public void startCandidateMetrics() {
        try {
            InitialPhaseMetric initialPhaseMetric = ((RequestSession) this.mCallbacks).mInitialPhaseMetric;
            this.mCandidatePhasePerProviderMetric.setSessionId(initialPhaseMetric.getSessionId());
            this.mCandidatePhasePerProviderMetric.setServiceBeganTimeNanoseconds(initialPhaseMetric.getCredentialServiceStartedTimeNanoseconds());
            this.mCandidatePhasePerProviderMetric.setStartQueryTimeNanoseconds(System.nanoTime());
        } catch (Exception e) {
            Log.w("ProviderSession", "Unexpected error during metric logging: " + e);
        }
    }

    public Boolean isProviderResponseSet() {
        return Boolean.valueOf(this.mProviderResponse != null || this.mProviderResponseSet.booleanValue());
    }

    public void invokeCallbackWithError(String str, String str2) {
        this.mCallbacks.onFinalErrorReceived(this.mComponentName, str, str2);
    }

    public boolean enforceRemoteEntryRestrictions(ComponentName componentName) {
        if (!this.mComponentName.equals(componentName)) {
            Log.i("ProviderSession", "Remote entry being dropped as it is not from the service configured by the OEM.");
            return false;
        }
        try {
            ApplicationInfo applicationInfo = this.mContext.getPackageManager().getApplicationInfo(this.mComponentName.getPackageName(), PackageManager.ApplicationInfoFlags.of(1048576L));
            if (applicationInfo != null) {
                if (this.mContext.checkPermission("android.permission.PROVIDE_REMOTE_CREDENTIALS", -1, applicationInfo.uid) == 0) {
                    return true;
                }
            }
            Log.i("ProviderSession", "In enforceRemoteEntryRestrictions - remote entry checks fail");
            return false;
        } catch (PackageManager.NameNotFoundException e) {
            Log.i("ProviderSession", "Error getting info for " + this.mComponentName.flattenToString() + ": " + e.getMessage());
            return false;
        } catch (SecurityException e2) {
            Log.i("ProviderSession", "Error getting info for " + this.mComponentName.flattenToString() + ": " + e2.getMessage());
            return false;
        }
    }
}
