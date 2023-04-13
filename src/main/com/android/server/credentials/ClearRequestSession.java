package com.android.server.credentials;

import android.content.ComponentName;
import android.content.Context;
import android.credentials.ClearCredentialStateRequest;
import android.credentials.CredentialProviderInfo;
import android.credentials.IClearCredentialStateCallback;
import android.credentials.ui.ProviderData;
import android.credentials.ui.UserSelectionDialogResult;
import android.os.CancellationSignal;
import android.os.RemoteException;
import android.service.credentials.CallingAppInfo;
import android.util.Log;
import com.android.server.credentials.ProviderSession;
import com.android.server.credentials.metrics.ApiName;
import com.android.server.credentials.metrics.ApiStatus;
import com.android.server.credentials.metrics.ProviderStatusForMetrics;
import java.util.ArrayList;
/* loaded from: classes.dex */
public final class ClearRequestSession extends RequestSession<ClearCredentialStateRequest, IClearCredentialStateCallback> implements ProviderSession.ProviderInternalCallback<Void> {
    @Override // com.android.server.credentials.RequestSession
    public void launchUiWithProviderData(ArrayList<ProviderData> arrayList) {
    }

    @Override // com.android.server.credentials.ProviderSession.ProviderInternalCallback
    public void onFinalErrorReceived(ComponentName componentName, String str, String str2) {
    }

    @Override // com.android.server.credentials.CredentialManagerUi.CredentialManagerUiCallback
    public void onUiCancellation(boolean z) {
    }

    @Override // com.android.server.credentials.CredentialManagerUi.CredentialManagerUiCallback
    public void onUiSelectorInvocationFailure() {
    }

    @Override // com.android.server.credentials.RequestSession, com.android.server.credentials.CredentialManagerUi.CredentialManagerUiCallback
    public /* bridge */ /* synthetic */ void onUiSelection(UserSelectionDialogResult userSelectionDialogResult) {
        super.onUiSelection(userSelectionDialogResult);
    }

    public ClearRequestSession(Context context, int i, int i2, IClearCredentialStateCallback iClearCredentialStateCallback, ClearCredentialStateRequest clearCredentialStateRequest, CallingAppInfo callingAppInfo, CancellationSignal cancellationSignal, long j) {
        super(context, i, i2, clearCredentialStateRequest, iClearCredentialStateCallback, "android.credentials.ui.TYPE_UNDEFINED", callingAppInfo, cancellationSignal, j);
        setupInitialPhaseMetric(ApiName.CLEAR_CREDENTIAL.getMetricCode(), 0);
    }

    @Override // com.android.server.credentials.RequestSession
    public ProviderSession initiateProviderSession(CredentialProviderInfo credentialProviderInfo, RemoteCredentialService remoteCredentialService) {
        ProviderClearSession createNewSession = ProviderClearSession.createNewSession(this.mContext, this.mUserId, credentialProviderInfo, this, remoteCredentialService);
        if (createNewSession != null) {
            Log.i("GetRequestSession", "In startProviderSession - provider session created and being added");
            this.mProviders.put(createNewSession.getComponentName().flattenToString(), createNewSession);
        }
        return createNewSession;
    }

    @Override // com.android.server.credentials.ProviderSession.ProviderInternalCallback
    public void onProviderStatusChanged(ProviderSession.Status status, ComponentName componentName) {
        Log.i("GetRequestSession", "in onStatusChanged with status: " + status);
        if (ProviderSession.isTerminatingStatus(status)) {
            Log.i("GetRequestSession", "in onStatusChanged terminating status");
            onProviderTerminated(componentName);
        } else if (ProviderSession.isCompletionStatus(status)) {
            Log.i("GetRequestSession", "in onStatusChanged isCompletionStatus status");
            onProviderResponseComplete(componentName);
        }
    }

    @Override // com.android.server.credentials.ProviderSession.ProviderInternalCallback
    public void onFinalResponseReceived(ComponentName componentName, Void r2) {
        setChosenMetric(componentName);
        respondToClientWithResponseAndFinish();
    }

    public void onProviderResponseComplete(ComponentName componentName) {
        if (isAnyProviderPending()) {
            return;
        }
        onFinalResponseReceived(componentName, (Void) null);
    }

    public void onProviderTerminated(ComponentName componentName) {
        if (isAnyProviderPending()) {
            return;
        }
        processResponses();
    }

    public final void respondToClientWithResponseAndFinish() {
        Log.i("GetRequestSession", "respondToClientWithResponseAndFinish");
        collectFinalPhaseMetricStatus(false, ProviderStatusForMetrics.FINAL_SUCCESS);
        if (isSessionCancelled()) {
            logApiCall(this.mChosenProviderFinalPhaseMetric, this.mCandidateBrowsingPhaseMetric, ApiStatus.CLIENT_CANCELED.getMetricCode());
            finishSession(true);
            return;
        }
        try {
            ((IClearCredentialStateCallback) this.mClientCallback).onSuccess();
            logApiCall(this.mChosenProviderFinalPhaseMetric, this.mCandidateBrowsingPhaseMetric, ApiStatus.SUCCESS.getMetricCode());
        } catch (RemoteException unused) {
            collectFinalPhaseMetricStatus(true, ProviderStatusForMetrics.FINAL_FAILURE);
            Log.i("GetRequestSession", "Issue while propagating the response to the client");
            logApiCall(this.mChosenProviderFinalPhaseMetric, this.mCandidateBrowsingPhaseMetric, ApiStatus.FAILURE.getMetricCode());
        }
        finishSession(false);
    }

    public final void respondToClientWithErrorAndFinish(String str, String str2) {
        Log.i("GetRequestSession", "respondToClientWithErrorAndFinish");
        collectFinalPhaseMetricStatus(true, ProviderStatusForMetrics.FINAL_FAILURE);
        if (isSessionCancelled()) {
            logApiCall(this.mChosenProviderFinalPhaseMetric, this.mCandidateBrowsingPhaseMetric, ApiStatus.CLIENT_CANCELED.getMetricCode());
            finishSession(true);
            return;
        }
        try {
            ((IClearCredentialStateCallback) this.mClientCallback).onError(str, str2);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        logApiCall(this.mChosenProviderFinalPhaseMetric, this.mCandidateBrowsingPhaseMetric, ApiStatus.FAILURE.getMetricCode());
        finishSession(false);
    }

    public final void processResponses() {
        for (ProviderSession providerSession : this.mProviders.values()) {
            if (providerSession.isProviderResponseSet().booleanValue()) {
                respondToClientWithResponseAndFinish();
                return;
            }
        }
        respondToClientWithErrorAndFinish("UNKNOWN", "All providers failed");
    }
}
