package com.android.server.credentials;

import android.content.ComponentName;
import android.content.Context;
import android.credentials.CredentialProviderInfo;
import android.credentials.ui.ProviderData;
import android.credentials.ui.UserSelectionDialogResult;
import android.os.Binder;
import android.os.CancellationSignal;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.service.credentials.CallingAppInfo;
import android.util.Log;
import com.android.server.credentials.CredentialManagerUi;
import com.android.server.credentials.metrics.CandidateBrowsingPhaseMetric;
import com.android.server.credentials.metrics.CandidatePhaseMetric;
import com.android.server.credentials.metrics.ChosenProviderFinalPhaseMetric;
import com.android.server.credentials.metrics.EntryEnum;
import com.android.server.credentials.metrics.InitialPhaseMetric;
import com.android.server.credentials.metrics.ProviderStatusForMetrics;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
/* loaded from: classes.dex */
public abstract class RequestSession<T, U> implements CredentialManagerUi.CredentialManagerUiCallback {
    public final int mCallingUid;
    public final CancellationSignal mCancellationSignal;
    public final CallingAppInfo mClientAppInfo;
    public final U mClientCallback;
    public final T mClientRequest;
    public final Context mContext;
    public final CredentialManagerUi mCredentialManagerUi;
    public final String mHybridService;
    public final String mRequestType;
    public final int mUserId;
    public final Map<String, ProviderSession> mProviders = new HashMap();
    public final InitialPhaseMetric mInitialPhaseMetric = new InitialPhaseMetric();
    public final ChosenProviderFinalPhaseMetric mChosenProviderFinalPhaseMetric = new ChosenProviderFinalPhaseMetric();
    public List<CandidateBrowsingPhaseMetric> mCandidateBrowsingPhaseMetric = new ArrayList();
    public int mSequenceCounter = 0;
    public RequestSessionStatus mRequestSessionStatus = RequestSessionStatus.IN_PROGRESS;
    public final Handler mHandler = new Handler(Looper.getMainLooper(), null, true);
    public final IBinder mRequestId = new Binder();

    /* loaded from: classes.dex */
    public enum RequestSessionStatus {
        IN_PROGRESS,
        CANCELLED,
        COMPLETE
    }

    public abstract ProviderSession initiateProviderSession(CredentialProviderInfo credentialProviderInfo, RemoteCredentialService remoteCredentialService);

    public abstract void launchUiWithProviderData(ArrayList<ProviderData> arrayList);

    public RequestSession(Context context, int i, int i2, T t, U u, String str, CallingAppInfo callingAppInfo, CancellationSignal cancellationSignal, long j) {
        this.mContext = context;
        this.mUserId = i;
        this.mCallingUid = i2;
        this.mClientRequest = t;
        this.mClientCallback = u;
        this.mRequestType = str;
        this.mClientAppInfo = callingAppInfo;
        this.mCancellationSignal = cancellationSignal;
        this.mCredentialManagerUi = new CredentialManagerUi(context, i, this);
        this.mHybridService = context.getResources().getString(17039880);
        initialPhaseMetricSetup(j);
    }

    public final void initialPhaseMetricSetup(long j) {
        try {
            this.mInitialPhaseMetric.setCredentialServiceStartedTimeNanoseconds(j);
            this.mInitialPhaseMetric.setSessionId(this.mRequestId.hashCode());
            this.mInitialPhaseMetric.setCallerUid(this.mCallingUid);
        } catch (Exception e) {
            Log.w("RequestSession", "Unexpected error during metric logging: " + e);
        }
    }

    public void setupInitialPhaseMetric(int i, int i2) {
        this.mInitialPhaseMetric.setApiName(i);
        this.mInitialPhaseMetric.setCountRequestClassType(i2);
    }

    @Override // com.android.server.credentials.CredentialManagerUi.CredentialManagerUiCallback
    public void onUiSelection(UserSelectionDialogResult userSelectionDialogResult) {
        if (this.mRequestSessionStatus == RequestSessionStatus.COMPLETE) {
            Log.i("RequestSession", "Request has already been completed. This is strange.");
        } else if (isSessionCancelled()) {
            finishSession(true);
        } else {
            String providerId = userSelectionDialogResult.getProviderId();
            Log.i("RequestSession", "onUiSelection, providerId: " + providerId);
            ProviderSession providerSession = this.mProviders.get(providerId);
            if (providerSession == null) {
                Log.i("RequestSession", "providerSession not found in onUiSelection");
                return;
            }
            Log.i("RequestSession", "Provider session found");
            logBrowsingPhasePerSelect(userSelectionDialogResult, providerSession);
            providerSession.onUiEntrySelected(userSelectionDialogResult.getEntryKey(), userSelectionDialogResult.getEntrySubkey(), userSelectionDialogResult.getPendingIntentProviderResponse());
        }
    }

    public final void logBrowsingPhasePerSelect(UserSelectionDialogResult userSelectionDialogResult, ProviderSession providerSession) {
        try {
            CandidateBrowsingPhaseMetric candidateBrowsingPhaseMetric = new CandidateBrowsingPhaseMetric();
            candidateBrowsingPhaseMetric.setSessionId(this.mInitialPhaseMetric.getSessionId());
            candidateBrowsingPhaseMetric.setEntryEnum(EntryEnum.getMetricCodeFromString(userSelectionDialogResult.getEntryKey()));
            candidateBrowsingPhaseMetric.setProviderUid(providerSession.mCandidatePhasePerProviderMetric.getCandidateUid());
            this.mCandidateBrowsingPhaseMetric.add(candidateBrowsingPhaseMetric);
        } catch (Exception e) {
            Log.w("RequestSession", "Unexpected error during metric logging: " + e);
        }
    }

    public void finishSession(boolean z) {
        Log.i("RequestSession", "finishing session");
        if (z) {
            this.mProviders.values().forEach(new Consumer() { // from class: com.android.server.credentials.RequestSession$$ExternalSyntheticLambda0
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    ((ProviderSession) obj).cancelProviderRemoteSession();
                }
            });
        }
        this.mRequestSessionStatus = RequestSessionStatus.COMPLETE;
        this.mProviders.clear();
    }

    public boolean isAnyProviderPending() {
        for (ProviderSession providerSession : this.mProviders.values()) {
            if (ProviderSession.isStatusWaitingForRemoteResponse(providerSession.getStatus())) {
                return true;
            }
        }
        return false;
    }

    public void logApiCall(ChosenProviderFinalPhaseMetric chosenProviderFinalPhaseMetric, List<CandidateBrowsingPhaseMetric> list, int i) {
        int i2 = this.mSequenceCounter + 1;
        this.mSequenceCounter = i2;
        MetricUtilities.logApiCalled(chosenProviderFinalPhaseMetric, list, i, i2);
    }

    public boolean isSessionCancelled() {
        return this.mCancellationSignal.isCanceled();
    }

    public boolean isUiInvocationNeeded() {
        for (ProviderSession providerSession : this.mProviders.values()) {
            if (ProviderSession.isUiInvokingStatus(providerSession.getStatus())) {
                return true;
            }
            if (ProviderSession.isStatusWaitingForRemoteResponse(providerSession.getStatus())) {
                break;
            }
        }
        return false;
    }

    public void getProviderDataAndInitiateUi() {
        Log.i("RequestSession", "In getProviderDataAndInitiateUi");
        Log.i("RequestSession", "In getProviderDataAndInitiateUi providers size: " + this.mProviders.size());
        if (isSessionCancelled()) {
            Map<String, ProviderSession> map = this.mProviders;
            int i = this.mSequenceCounter + 1;
            this.mSequenceCounter = i;
            MetricUtilities.logApiCalled(map, i);
            finishSession(true);
            return;
        }
        ArrayList<ProviderData> arrayList = new ArrayList<>();
        for (ProviderSession providerSession : this.mProviders.values()) {
            Log.i("RequestSession", "preparing data for : " + providerSession.getComponentName());
            ProviderData mo2860prepareUiData = providerSession.mo2860prepareUiData();
            if (mo2860prepareUiData != null) {
                Log.i("RequestSession", "Provider data is not null");
                arrayList.add(mo2860prepareUiData);
            }
        }
        if (arrayList.isEmpty()) {
            return;
        }
        Log.i("RequestSession", "provider list not empty about to initiate ui");
        Map<String, ProviderSession> map2 = this.mProviders;
        int i2 = this.mSequenceCounter + 1;
        this.mSequenceCounter = i2;
        MetricUtilities.logApiCalled(map2, i2);
        launchUiWithProviderData(arrayList);
    }

    public void collectFinalPhaseMetricStatus(boolean z, ProviderStatusForMetrics providerStatusForMetrics) {
        this.mChosenProviderFinalPhaseMetric.setHasException(z);
        this.mChosenProviderFinalPhaseMetric.setChosenProviderStatus(providerStatusForMetrics.getMetricCode());
    }

    public void setChosenMetric(ComponentName componentName) {
        try {
            CandidatePhaseMetric candidatePhaseMetric = this.mProviders.get(componentName.flattenToString()).mCandidatePhasePerProviderMetric;
            this.mChosenProviderFinalPhaseMetric.setSessionId(candidatePhaseMetric.getSessionId());
            this.mChosenProviderFinalPhaseMetric.setChosenUid(candidatePhaseMetric.getCandidateUid());
            this.mChosenProviderFinalPhaseMetric.setQueryPhaseLatencyMicroseconds(candidatePhaseMetric.getQueryLatencyMicroseconds());
            this.mChosenProviderFinalPhaseMetric.setServiceBeganTimeNanoseconds(candidatePhaseMetric.getServiceBeganTimeNanoseconds());
            this.mChosenProviderFinalPhaseMetric.setQueryStartTimeNanoseconds(candidatePhaseMetric.getStartQueryTimeNanoseconds());
            this.mChosenProviderFinalPhaseMetric.setQueryEndTimeNanoseconds(candidatePhaseMetric.getQueryFinishTimeNanoseconds());
            this.mChosenProviderFinalPhaseMetric.setNumEntriesTotal(candidatePhaseMetric.getNumEntriesTotal());
            this.mChosenProviderFinalPhaseMetric.setCredentialEntryCount(candidatePhaseMetric.getCredentialEntryCount());
            this.mChosenProviderFinalPhaseMetric.setCredentialEntryTypeCount(candidatePhaseMetric.getCredentialEntryTypeCount());
            this.mChosenProviderFinalPhaseMetric.setActionEntryCount(candidatePhaseMetric.getActionEntryCount());
            this.mChosenProviderFinalPhaseMetric.setRemoteEntryCount(candidatePhaseMetric.getRemoteEntryCount());
            this.mChosenProviderFinalPhaseMetric.setAuthenticationEntryCount(candidatePhaseMetric.getAuthenticationEntryCount());
            this.mChosenProviderFinalPhaseMetric.setAvailableEntries(candidatePhaseMetric.getAvailableEntries());
            this.mChosenProviderFinalPhaseMetric.setFinalFinishTimeNanoseconds(System.nanoTime());
        } catch (Exception e) {
            Log.w("RequestSession", "Unexpected error during metric logging: " + e);
        }
    }
}
