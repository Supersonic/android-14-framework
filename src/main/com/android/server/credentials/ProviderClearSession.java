package com.android.server.credentials;

import android.content.Context;
import android.credentials.ClearCredentialStateException;
import android.credentials.CredentialProviderInfo;
import android.credentials.ui.ProviderData;
import android.credentials.ui.ProviderPendingIntentResponse;
import android.service.credentials.CallingAppInfo;
import android.service.credentials.ClearCredentialStateRequest;
import android.util.Log;
import com.android.server.credentials.ProviderSession;
/* loaded from: classes.dex */
public final class ProviderClearSession extends ProviderSession<ClearCredentialStateRequest, Void> {
    public ClearCredentialStateException mProviderException;

    @Override // com.android.server.credentials.ProviderSession
    public void onUiEntrySelected(String str, String str2, ProviderPendingIntentResponse providerPendingIntentResponse) {
    }

    @Override // com.android.server.credentials.ProviderSession
    /* renamed from: prepareUiData */
    public ProviderData mo2860prepareUiData() {
        return null;
    }

    public static ProviderClearSession createNewSession(Context context, int i, CredentialProviderInfo credentialProviderInfo, ClearRequestSession clearRequestSession, RemoteCredentialService remoteCredentialService) {
        return new ProviderClearSession(context, credentialProviderInfo, clearRequestSession, i, remoteCredentialService, createProviderRequest((android.credentials.ClearCredentialStateRequest) clearRequestSession.mClientRequest, clearRequestSession.mClientAppInfo));
    }

    public static ClearCredentialStateRequest createProviderRequest(android.credentials.ClearCredentialStateRequest clearCredentialStateRequest, CallingAppInfo callingAppInfo) {
        return new ClearCredentialStateRequest(callingAppInfo, clearCredentialStateRequest.getData());
    }

    public ProviderClearSession(Context context, CredentialProviderInfo credentialProviderInfo, ProviderSession.ProviderInternalCallback providerInternalCallback, int i, RemoteCredentialService remoteCredentialService, ClearCredentialStateRequest clearCredentialStateRequest) {
        super(context, credentialProviderInfo, clearCredentialStateRequest, providerInternalCallback, i, remoteCredentialService);
        setStatus(ProviderSession.Status.PENDING);
    }

    @Override // com.android.server.credentials.RemoteCredentialService.ProviderCallbacks
    public void onProviderResponseSuccess(Void r2) {
        Log.i("ProviderClearSession", "in onProviderResponseSuccess");
        this.mProviderResponseSet = Boolean.TRUE;
        updateStatusAndInvokeCallback(ProviderSession.Status.COMPLETE);
    }

    @Override // com.android.server.credentials.RemoteCredentialService.ProviderCallbacks
    public void onProviderResponseFailure(int i, Exception exc) {
        if (exc instanceof ClearCredentialStateException) {
            this.mProviderException = (ClearCredentialStateException) exc;
        }
        captureCandidateFailureInMetrics();
        updateStatusAndInvokeCallback(ProviderSession.toStatus(i));
    }

    @Override // com.android.server.credentials.ProviderSession
    public void invokeSession() {
        if (this.mRemoteCredentialService != null) {
            startCandidateMetrics();
            this.mRemoteCredentialService.onClearCredentialState((ClearCredentialStateRequest) this.mProviderRequest, this);
        }
    }
}
