package com.android.server.credentials;

import android.content.ComponentName;
import android.content.Context;
import android.credentials.CredentialProviderInfo;
import android.credentials.GetCredentialRequest;
import android.credentials.GetCredentialResponse;
import android.credentials.IGetCredentialCallback;
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
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
/* loaded from: classes.dex */
public class GetRequestSession extends RequestSession<GetCredentialRequest, IGetCredentialCallback> implements ProviderSession.ProviderInternalCallback<GetCredentialResponse> {
    @Override // com.android.server.credentials.RequestSession, com.android.server.credentials.CredentialManagerUi.CredentialManagerUiCallback
    public /* bridge */ /* synthetic */ void onUiSelection(UserSelectionDialogResult userSelectionDialogResult) {
        super.onUiSelection(userSelectionDialogResult);
    }

    public GetRequestSession(Context context, int i, int i2, IGetCredentialCallback iGetCredentialCallback, GetCredentialRequest getCredentialRequest, CallingAppInfo callingAppInfo, CancellationSignal cancellationSignal, long j) {
        super(context, i, i2, getCredentialRequest, iGetCredentialCallback, "android.credentials.ui.TYPE_GET", callingAppInfo, cancellationSignal, j);
        setupInitialPhaseMetric(ApiName.GET_CREDENTIAL.getMetricCode(), ((Set) getCredentialRequest.getCredentialOptions().stream().map(new C0725x1051f629()).collect(Collectors.toSet())).size());
    }

    @Override // com.android.server.credentials.RequestSession
    public ProviderSession initiateProviderSession(CredentialProviderInfo credentialProviderInfo, RemoteCredentialService remoteCredentialService) {
        ProviderGetSession createNewSession = ProviderGetSession.createNewSession(this.mContext, this.mUserId, credentialProviderInfo, this, remoteCredentialService);
        if (createNewSession != null) {
            Log.i("GetRequestSession", "In startProviderSession - provider session created and being added");
            this.mProviders.put(createNewSession.getComponentName().flattenToString(), createNewSession);
        }
        return createNewSession;
    }

    @Override // com.android.server.credentials.RequestSession
    public void launchUiWithProviderData(ArrayList<ProviderData> arrayList) {
        this.mChosenProviderFinalPhaseMetric.setUiCallStartTimeNanoseconds(System.nanoTime());
        try {
            ((IGetCredentialCallback) this.mClientCallback).onPendingIntent(this.mCredentialManagerUi.createPendingIntent(RequestInfo.newGetRequestInfo(this.mRequestId, (GetCredentialRequest) this.mClientRequest, this.mClientAppInfo.getPackageName()), arrayList));
        } catch (RemoteException unused) {
            this.mChosenProviderFinalPhaseMetric.setUiReturned(false);
            respondToClientWithErrorAndFinish("android.credentials.GetCredentialException.TYPE_UNKNOWN", "Unable to instantiate selector");
        }
    }

    @Override // com.android.server.credentials.ProviderSession.ProviderInternalCallback
    public void onFinalResponseReceived(ComponentName componentName, GetCredentialResponse getCredentialResponse) {
        this.mChosenProviderFinalPhaseMetric.setUiReturned(true);
        this.mChosenProviderFinalPhaseMetric.setUiCallEndTimeNanoseconds(System.nanoTime());
        Log.i("GetRequestSession", "onFinalCredentialReceived from: " + componentName.flattenToString());
        setChosenMetric(componentName);
        if (getCredentialResponse != null) {
            this.mChosenProviderFinalPhaseMetric.setChosenProviderStatus(ProviderStatusForMetrics.FINAL_SUCCESS.getMetricCode());
            respondToClientWithResponseAndFinish(getCredentialResponse);
            return;
        }
        this.mChosenProviderFinalPhaseMetric.setChosenProviderStatus(ProviderStatusForMetrics.FINAL_FAILURE.getMetricCode());
        respondToClientWithErrorAndFinish("android.credentials.GetCredentialException.TYPE_NO_CREDENTIAL", "Invalid response from provider");
    }

    @Override // com.android.server.credentials.ProviderSession.ProviderInternalCallback
    public void onFinalErrorReceived(ComponentName componentName, String str, String str2) {
        respondToClientWithErrorAndFinish(str, str2);
    }

    public final void respondToClientWithResponseAndFinish(GetCredentialResponse getCredentialResponse) {
        collectFinalPhaseMetricStatus(false, ProviderStatusForMetrics.FINAL_SUCCESS);
        if (this.mRequestSessionStatus == RequestSession.RequestSessionStatus.COMPLETE) {
            Log.i("GetRequestSession", "Request has already been completed. This is strange.");
        } else if (isSessionCancelled()) {
            logApiCall(this.mChosenProviderFinalPhaseMetric, this.mCandidateBrowsingPhaseMetric, ApiStatus.CLIENT_CANCELED.getMetricCode());
            finishSession(true);
        } else {
            try {
                ((IGetCredentialCallback) this.mClientCallback).onResponse(getCredentialResponse);
                logApiCall(this.mChosenProviderFinalPhaseMetric, this.mCandidateBrowsingPhaseMetric, ApiStatus.SUCCESS.getMetricCode());
            } catch (RemoteException e) {
                collectFinalPhaseMetricStatus(true, ProviderStatusForMetrics.FINAL_FAILURE);
                Log.i("GetRequestSession", "Issue while responding to client with a response : " + e.getMessage());
                logApiCall(this.mChosenProviderFinalPhaseMetric, this.mCandidateBrowsingPhaseMetric, ApiStatus.FAILURE.getMetricCode());
            }
            finishSession(false);
        }
    }

    public final void respondToClientWithErrorAndFinish(String str, String str2) {
        collectFinalPhaseMetricStatus(true, ProviderStatusForMetrics.FINAL_FAILURE);
        if (this.mRequestSessionStatus == RequestSession.RequestSessionStatus.COMPLETE) {
            Log.i("GetRequestSession", "Request has already been completed. This is strange.");
        } else if (isSessionCancelled()) {
            logApiCall(this.mChosenProviderFinalPhaseMetric, this.mCandidateBrowsingPhaseMetric, ApiStatus.CLIENT_CANCELED.getMetricCode());
            finishSession(true);
        } else {
            try {
                ((IGetCredentialCallback) this.mClientCallback).onError(str, str2);
            } catch (RemoteException e) {
                Log.i("GetRequestSession", "Issue while responding to client with error : " + e.getMessage());
            }
            logFailureOrUserCancel(str);
            finishSession(false);
        }
    }

    public final void logFailureOrUserCancel(String str) {
        collectFinalPhaseMetricStatus(true, ProviderStatusForMetrics.FINAL_FAILURE);
        if ("android.credentials.GetCredentialException.TYPE_USER_CANCELED".equals(str)) {
            this.mChosenProviderFinalPhaseMetric.setHasException(false);
            logApiCall(this.mChosenProviderFinalPhaseMetric, this.mCandidateBrowsingPhaseMetric, ApiStatus.USER_CANCELED.getMetricCode());
            return;
        }
        logApiCall(this.mChosenProviderFinalPhaseMetric, this.mCandidateBrowsingPhaseMetric, ApiStatus.FAILURE.getMetricCode());
    }

    @Override // com.android.server.credentials.CredentialManagerUi.CredentialManagerUiCallback
    public void onUiCancellation(boolean z) {
        if (z) {
            respondToClientWithErrorAndFinish("android.credentials.GetCredentialException.TYPE_USER_CANCELED", "User cancelled the selector");
        } else {
            respondToClientWithErrorAndFinish("android.credentials.GetCredentialException.TYPE_INTERRUPTED", "The UI was interrupted - please try again.");
        }
    }

    @Override // com.android.server.credentials.CredentialManagerUi.CredentialManagerUiCallback
    public void onUiSelectorInvocationFailure() {
        respondToClientWithErrorAndFinish("android.credentials.GetCredentialException.TYPE_NO_CREDENTIAL", "No credentials available.");
    }

    @Override // com.android.server.credentials.ProviderSession.ProviderInternalCallback
    public void onProviderStatusChanged(ProviderSession.Status status, ComponentName componentName) {
        Log.i("GetRequestSession", "in onStatusChanged with status: " + status);
        if (status == ProviderSession.Status.NO_CREDENTIALS_FROM_AUTH_ENTRY) {
            handleEmptyAuthenticationSelection(componentName);
        } else if (isAnyProviderPending()) {
        } else {
            if (isUiInvocationNeeded()) {
                Log.i("GetRequestSession", "in onProviderStatusChanged - isUiInvocationNeeded");
                getProviderDataAndInitiateUi();
                return;
            }
            respondToClientWithErrorAndFinish("android.credentials.GetCredentialException.TYPE_NO_CREDENTIAL", "No credentials available");
        }
    }

    public final void handleEmptyAuthenticationSelection(final ComponentName componentName) {
        this.mProviders.keySet().forEach(new Consumer() { // from class: com.android.server.credentials.GetRequestSession$$ExternalSyntheticLambda0
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                GetRequestSession.this.lambda$handleEmptyAuthenticationSelection$0(componentName, (String) obj);
            }
        });
        getProviderDataAndInitiateUi();
        if (providerDataContainsEmptyAuthEntriesOnly()) {
            respondToClientWithErrorAndFinish("android.credentials.GetCredentialException.TYPE_NO_CREDENTIAL", "No credentials available");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$handleEmptyAuthenticationSelection$0(ComponentName componentName, String str) {
        ProviderGetSession providerGetSession = (ProviderGetSession) this.mProviders.get(str);
        if (providerGetSession.mComponentName.equals(componentName)) {
            return;
        }
        providerGetSession.updateAuthEntriesStatusFromAnotherSession();
    }

    public final boolean providerDataContainsEmptyAuthEntriesOnly() {
        for (String str : this.mProviders.keySet()) {
            if (!((ProviderGetSession) this.mProviders.get(str)).containsEmptyAuthEntriesOnly()) {
                return false;
            }
        }
        return true;
    }
}
