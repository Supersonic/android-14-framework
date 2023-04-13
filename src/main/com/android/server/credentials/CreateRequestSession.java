package com.android.server.credentials;

import android.content.ComponentName;
import android.content.Context;
import android.credentials.CreateCredentialRequest;
import android.credentials.CreateCredentialResponse;
import android.credentials.CredentialProviderInfo;
import android.credentials.ICreateCredentialCallback;
import android.credentials.ui.ProviderData;
import android.credentials.ui.RequestInfo;
import android.credentials.ui.UserSelectionDialogResult;
import android.os.CancellationSignal;
import android.os.RemoteException;
import android.service.credentials.CallingAppInfo;
import android.util.Log;
import com.android.server.credentials.ProviderSession;
import com.android.server.credentials.RequestSession;
import com.android.server.credentials.metrics.ApiName;
import com.android.server.credentials.metrics.ApiStatus;
import com.android.server.credentials.metrics.ProviderStatusForMetrics;
import java.util.ArrayList;
/* loaded from: classes.dex */
public final class CreateRequestSession extends RequestSession<CreateCredentialRequest, ICreateCredentialCallback> implements ProviderSession.ProviderInternalCallback<CreateCredentialResponse> {
    @Override // com.android.server.credentials.RequestSession, com.android.server.credentials.CredentialManagerUi.CredentialManagerUiCallback
    public /* bridge */ /* synthetic */ void onUiSelection(UserSelectionDialogResult userSelectionDialogResult) {
        super.onUiSelection(userSelectionDialogResult);
    }

    public CreateRequestSession(Context context, int i, int i2, CreateCredentialRequest createCredentialRequest, ICreateCredentialCallback iCreateCredentialCallback, CallingAppInfo callingAppInfo, CancellationSignal cancellationSignal, long j) {
        super(context, i, i2, createCredentialRequest, iCreateCredentialCallback, "android.credentials.ui.TYPE_CREATE", callingAppInfo, cancellationSignal, j);
        setupInitialPhaseMetric(ApiName.CREATE_CREDENTIAL.getMetricCode(), 1);
    }

    @Override // com.android.server.credentials.RequestSession
    public ProviderSession initiateProviderSession(CredentialProviderInfo credentialProviderInfo, RemoteCredentialService remoteCredentialService) {
        ProviderCreateSession createNewSession = ProviderCreateSession.createNewSession(this.mContext, this.mUserId, credentialProviderInfo, this, remoteCredentialService);
        if (createNewSession != null) {
            Log.i("CreateRequestSession", "In startProviderSession - provider session created and being added");
            this.mProviders.put(createNewSession.getComponentName().flattenToString(), createNewSession);
        }
        return createNewSession;
    }

    @Override // com.android.server.credentials.RequestSession
    public void launchUiWithProviderData(ArrayList<ProviderData> arrayList) {
        this.mChosenProviderFinalPhaseMetric.setUiCallStartTimeNanoseconds(System.nanoTime());
        try {
            ((ICreateCredentialCallback) this.mClientCallback).onPendingIntent(this.mCredentialManagerUi.createPendingIntent(RequestInfo.newCreateRequestInfo(this.mRequestId, (CreateCredentialRequest) this.mClientRequest, this.mClientAppInfo.getPackageName()), arrayList));
        } catch (RemoteException unused) {
            this.mChosenProviderFinalPhaseMetric.setUiReturned(false);
            respondToClientWithErrorAndFinish("android.credentials.CreateCredentialException.TYPE_UNKNOWN", "Unable to invoke selector");
        }
    }

    @Override // com.android.server.credentials.ProviderSession.ProviderInternalCallback
    public void onFinalResponseReceived(ComponentName componentName, CreateCredentialResponse createCredentialResponse) {
        this.mChosenProviderFinalPhaseMetric.setUiReturned(true);
        this.mChosenProviderFinalPhaseMetric.setUiCallEndTimeNanoseconds(System.nanoTime());
        Log.i("CreateRequestSession", "onFinalCredentialReceived from: " + componentName.flattenToString());
        setChosenMetric(componentName);
        if (createCredentialResponse != null) {
            this.mChosenProviderFinalPhaseMetric.setChosenProviderStatus(ProviderStatusForMetrics.FINAL_SUCCESS.getMetricCode());
            respondToClientWithResponseAndFinish(createCredentialResponse);
            return;
        }
        this.mChosenProviderFinalPhaseMetric.setChosenProviderStatus(ProviderStatusForMetrics.FINAL_FAILURE.getMetricCode());
        respondToClientWithErrorAndFinish("android.credentials.CreateCredentialException.TYPE_NO_CREATE_OPTIONS", "Invalid response");
    }

    @Override // com.android.server.credentials.ProviderSession.ProviderInternalCallback
    public void onFinalErrorReceived(ComponentName componentName, String str, String str2) {
        respondToClientWithErrorAndFinish(str, str2);
    }

    @Override // com.android.server.credentials.CredentialManagerUi.CredentialManagerUiCallback
    public void onUiCancellation(boolean z) {
        if (z) {
            respondToClientWithErrorAndFinish("android.credentials.CreateCredentialException.TYPE_USER_CANCELED", "User cancelled the selector");
        } else {
            respondToClientWithErrorAndFinish("android.credentials.CreateCredentialException.TYPE_INTERRUPTED", "The UI was interrupted - please try again.");
        }
    }

    @Override // com.android.server.credentials.CredentialManagerUi.CredentialManagerUiCallback
    public void onUiSelectorInvocationFailure() {
        respondToClientWithErrorAndFinish("android.credentials.CreateCredentialException.TYPE_NO_CREATE_OPTIONS", "No create options available.");
    }

    public final void respondToClientWithResponseAndFinish(CreateCredentialResponse createCredentialResponse) {
        Log.i("CreateRequestSession", "respondToClientWithResponseAndFinish");
        collectFinalPhaseMetricStatus(false, ProviderStatusForMetrics.FINAL_SUCCESS);
        if (this.mRequestSessionStatus == RequestSession.RequestSessionStatus.COMPLETE) {
            Log.i("CreateRequestSession", "Request has already been completed. This is strange.");
        } else if (isSessionCancelled()) {
            logApiCall(this.mChosenProviderFinalPhaseMetric, this.mCandidateBrowsingPhaseMetric, ApiStatus.CLIENT_CANCELED.getMetricCode());
            finishSession(true);
        } else {
            try {
                ((ICreateCredentialCallback) this.mClientCallback).onResponse(createCredentialResponse);
                logApiCall(this.mChosenProviderFinalPhaseMetric, this.mCandidateBrowsingPhaseMetric, ApiStatus.SUCCESS.getMetricCode());
            } catch (RemoteException e) {
                collectFinalPhaseMetricStatus(true, ProviderStatusForMetrics.FINAL_FAILURE);
                Log.i("CreateRequestSession", "Issue while responding to client: " + e.getMessage());
                logApiCall(this.mChosenProviderFinalPhaseMetric, this.mCandidateBrowsingPhaseMetric, ApiStatus.FAILURE.getMetricCode());
            }
            finishSession(false);
        }
    }

    public final void respondToClientWithErrorAndFinish(String str, String str2) {
        Log.i("CreateRequestSession", "respondToClientWithErrorAndFinish");
        collectFinalPhaseMetricStatus(true, ProviderStatusForMetrics.FINAL_FAILURE);
        if (this.mRequestSessionStatus == RequestSession.RequestSessionStatus.COMPLETE) {
            Log.i("CreateRequestSession", "Request has already been completed. This is strange.");
        } else if (isSessionCancelled()) {
            logApiCall(this.mChosenProviderFinalPhaseMetric, this.mCandidateBrowsingPhaseMetric, ApiStatus.CLIENT_CANCELED.getMetricCode());
            finishSession(true);
        } else {
            try {
                ((ICreateCredentialCallback) this.mClientCallback).onError(str, str2);
            } catch (RemoteException e) {
                Log.i("CreateRequestSession", "Issue while responding to client: " + e.getMessage());
            }
            logFailureOrUserCancel(str);
            finishSession(false);
        }
    }

    public final void logFailureOrUserCancel(String str) {
        collectFinalPhaseMetricStatus(true, ProviderStatusForMetrics.FINAL_FAILURE);
        if ("android.credentials.CreateCredentialException.TYPE_USER_CANCELED".equals(str)) {
            this.mChosenProviderFinalPhaseMetric.setHasException(false);
            logApiCall(this.mChosenProviderFinalPhaseMetric, this.mCandidateBrowsingPhaseMetric, ApiStatus.USER_CANCELED.getMetricCode());
            return;
        }
        logApiCall(this.mChosenProviderFinalPhaseMetric, this.mCandidateBrowsingPhaseMetric, ApiStatus.FAILURE.getMetricCode());
    }

    @Override // com.android.server.credentials.ProviderSession.ProviderInternalCallback
    public void onProviderStatusChanged(ProviderSession.Status status, ComponentName componentName) {
        Log.i("CreateRequestSession", "in onProviderStatusChanged with status: " + status);
        if (isAnyProviderPending()) {
            return;
        }
        if (isUiInvocationNeeded()) {
            Log.i("CreateRequestSession", "in onProviderStatusChanged - isUiInvocationNeeded");
            getProviderDataAndInitiateUi();
            return;
        }
        respondToClientWithErrorAndFinish("android.credentials.CreateCredentialException.TYPE_NO_CREATE_OPTIONS", "No create options available.");
    }
}
