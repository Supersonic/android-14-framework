package com.android.server.credentials;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.credentials.CreateCredentialException;
import android.credentials.CreateCredentialResponse;
import android.credentials.CredentialProviderInfo;
import android.credentials.ui.CreateCredentialProviderData;
import android.credentials.ui.Entry;
import android.credentials.ui.ProviderPendingIntentResponse;
import android.os.Bundle;
import android.os.Parcelable;
import android.service.credentials.BeginCreateCredentialRequest;
import android.service.credentials.BeginCreateCredentialResponse;
import android.service.credentials.CallingAppInfo;
import android.service.credentials.CreateCredentialRequest;
import android.service.credentials.CreateEntry;
import android.service.credentials.RemoteEntry;
import android.util.Log;
import android.util.Pair;
import com.android.server.credentials.ProviderCreateSession;
import com.android.server.credentials.ProviderSession;
import com.android.server.credentials.metrics.EntryEnum;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
/* loaded from: classes.dex */
public final class ProviderCreateSession extends ProviderSession<BeginCreateCredentialRequest, BeginCreateCredentialResponse> {
    public final CreateCredentialRequest mCompleteRequest;
    public CreateCredentialException mProviderException;
    public final ProviderResponseDataHandler mProviderResponseDataHandler;

    public static ProviderCreateSession createNewSession(Context context, int i, CredentialProviderInfo credentialProviderInfo, CreateRequestSession createRequestSession, RemoteCredentialService remoteCredentialService) {
        CreateCredentialRequest createProviderRequest = createProviderRequest(credentialProviderInfo.getCapabilities(), (android.credentials.CreateCredentialRequest) createRequestSession.mClientRequest, createRequestSession.mClientAppInfo);
        if (createProviderRequest != null) {
            return new ProviderCreateSession(context, credentialProviderInfo, createRequestSession, i, remoteCredentialService, constructQueryPhaseRequest(((android.credentials.CreateCredentialRequest) createRequestSession.mClientRequest).getType(), ((android.credentials.CreateCredentialRequest) createRequestSession.mClientRequest).getCandidateQueryData(), createRequestSession.mClientAppInfo, ((android.credentials.CreateCredentialRequest) createRequestSession.mClientRequest).alwaysSendAppInfoToProvider()), createProviderRequest, createRequestSession.mHybridService);
        }
        Log.i("ProviderCreateSession", "Unable to create provider session");
        return null;
    }

    public static BeginCreateCredentialRequest constructQueryPhaseRequest(String str, Bundle bundle, CallingAppInfo callingAppInfo, boolean z) {
        if (z) {
            return new BeginCreateCredentialRequest(str, bundle, callingAppInfo);
        }
        return new BeginCreateCredentialRequest(str, bundle);
    }

    public static CreateCredentialRequest createProviderRequest(List<String> list, android.credentials.CreateCredentialRequest createCredentialRequest, CallingAppInfo callingAppInfo) {
        String type = createCredentialRequest.getType();
        if (list.contains(type)) {
            return new CreateCredentialRequest(callingAppInfo, type, createCredentialRequest.getCredentialData());
        }
        Log.i("ProviderCreateSession", "Unable to create provider request - capabilities do not match");
        return null;
    }

    public ProviderCreateSession(Context context, CredentialProviderInfo credentialProviderInfo, ProviderSession.ProviderInternalCallback<CreateCredentialResponse> providerInternalCallback, int i, RemoteCredentialService remoteCredentialService, BeginCreateCredentialRequest beginCreateCredentialRequest, CreateCredentialRequest createCredentialRequest, String str) {
        super(context, credentialProviderInfo, beginCreateCredentialRequest, providerInternalCallback, i, remoteCredentialService);
        this.mCompleteRequest = createCredentialRequest;
        setStatus(ProviderSession.Status.PENDING);
        this.mProviderResponseDataHandler = new ProviderResponseDataHandler(ComponentName.unflattenFromString(str));
    }

    @Override // com.android.server.credentials.RemoteCredentialService.ProviderCallbacks
    public void onProviderResponseSuccess(BeginCreateCredentialResponse beginCreateCredentialResponse) {
        Log.i("ProviderCreateSession", "in onProviderResponseSuccess");
        onSetInitialRemoteResponse(beginCreateCredentialResponse);
    }

    @Override // com.android.server.credentials.RemoteCredentialService.ProviderCallbacks
    public void onProviderResponseFailure(int i, Exception exc) {
        if (exc instanceof CreateCredentialException) {
            this.mProviderException = (CreateCredentialException) exc;
        }
        captureCandidateFailureInMetrics();
        updateStatusAndInvokeCallback(ProviderSession.toStatus(i));
    }

    /* JADX WARN: Multi-variable type inference failed */
    public final void onSetInitialRemoteResponse(BeginCreateCredentialResponse beginCreateCredentialResponse) {
        Log.i("ProviderCreateSession", "onSetInitialRemoteResponse with save entries");
        this.mProviderResponse = beginCreateCredentialResponse;
        this.mProviderResponseDataHandler.addResponseContent(beginCreateCredentialResponse.getCreateEntries(), beginCreateCredentialResponse.getRemoteCreateEntry());
        if (this.mProviderResponseDataHandler.isEmptyResponse(beginCreateCredentialResponse)) {
            gatherCandidateEntryMetrics(beginCreateCredentialResponse);
            updateStatusAndInvokeCallback(ProviderSession.Status.EMPTY_RESPONSE);
            return;
        }
        gatherCandidateEntryMetrics(beginCreateCredentialResponse);
        updateStatusAndInvokeCallback(ProviderSession.Status.SAVE_ENTRIES_RECEIVED);
    }

    public final void gatherCandidateEntryMetrics(BeginCreateCredentialResponse beginCreateCredentialResponse) {
        int i;
        try {
            List createEntries = beginCreateCredentialResponse.getCreateEntries();
            int i2 = 0;
            if (beginCreateCredentialResponse.getRemoteCreateEntry() != null) {
                this.mCandidatePhasePerProviderMetric.addEntry(EntryEnum.REMOTE_ENTRY);
                i = 1;
            } else {
                i = 0;
            }
            if (createEntries != null) {
                i2 = createEntries.size();
            }
            if (i2 > 0) {
                createEntries.forEach(new Consumer() { // from class: com.android.server.credentials.ProviderCreateSession$$ExternalSyntheticLambda0
                    @Override // java.util.function.Consumer
                    public final void accept(Object obj) {
                        ProviderCreateSession.this.lambda$gatherCandidateEntryMetrics$0((CreateEntry) obj);
                    }
                });
            }
            this.mCandidatePhasePerProviderMetric.setNumEntriesTotal(i2 + i);
            this.mCandidatePhasePerProviderMetric.setRemoteEntryCount(i);
            this.mCandidatePhasePerProviderMetric.setCredentialEntryCount(i2);
            this.mCandidatePhasePerProviderMetric.setCredentialEntryTypeCount(1);
        } catch (Exception e) {
            Log.w("ProviderCreateSession", "Unexpected error during metric logging: " + e);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$gatherCandidateEntryMetrics$0(CreateEntry createEntry) {
        this.mCandidatePhasePerProviderMetric.addEntry(EntryEnum.CREDENTIAL_ENTRY);
    }

    @Override // com.android.server.credentials.ProviderSession
    /* renamed from: prepareUiData */
    public CreateCredentialProviderData mo2860prepareUiData() throws IllegalArgumentException {
        Log.i("ProviderCreateSession", "In prepareUiData");
        if (!ProviderSession.isUiInvokingStatus(getStatus())) {
            Log.i("ProviderCreateSession", "In prepareUiData not in uiInvokingStatus");
            return null;
        } else if (this.mProviderResponse == 0 || this.mProviderResponseDataHandler.isEmptyResponse()) {
            return null;
        } else {
            Log.i("ProviderCreateSession", "In prepareUiData save entries not null");
            return this.mProviderResponseDataHandler.toCreateCredentialProviderData();
        }
    }

    @Override // com.android.server.credentials.ProviderSession
    public void onUiEntrySelected(String str, String str2, ProviderPendingIntentResponse providerPendingIntentResponse) {
        str.hashCode();
        if (str.equals("save_entry_key")) {
            if (this.mProviderResponseDataHandler.getCreateEntry(str2) == null) {
                Log.i("ProviderCreateSession", "Unexpected save entry key");
                invokeCallbackOnInternalInvalidState();
                return;
            }
            onCreateEntrySelected(providerPendingIntentResponse);
        } else if (str.equals("remote_entry_key")) {
            if (this.mProviderResponseDataHandler.getRemoteEntry(str2) == null) {
                Log.i("ProviderCreateSession", "Unexpected remote entry key");
                invokeCallbackOnInternalInvalidState();
                return;
            }
            onRemoteEntrySelected(providerPendingIntentResponse);
        } else {
            Log.i("ProviderCreateSession", "Unsupported entry type selected");
            invokeCallbackOnInternalInvalidState();
        }
    }

    @Override // com.android.server.credentials.ProviderSession
    public void invokeSession() {
        if (this.mRemoteCredentialService != null) {
            startCandidateMetrics();
            this.mRemoteCredentialService.onCreateCredential((BeginCreateCredentialRequest) this.mProviderRequest, this);
        }
    }

    public final Intent setUpFillInIntent() {
        Intent intent = new Intent();
        intent.putExtra("android.service.credentials.extra.CREATE_CREDENTIAL_REQUEST", (Parcelable) this.mCompleteRequest);
        return intent;
    }

    public final void onCreateEntrySelected(ProviderPendingIntentResponse providerPendingIntentResponse) {
        CreateCredentialException maybeGetPendingIntentException = maybeGetPendingIntentException(providerPendingIntentResponse);
        if (maybeGetPendingIntentException != null) {
            invokeCallbackWithError(maybeGetPendingIntentException.getType(), maybeGetPendingIntentException.getMessage());
            return;
        }
        CreateCredentialResponse extractCreateCredentialResponse = PendingIntentResultHandler.extractCreateCredentialResponse(providerPendingIntentResponse.getResultData());
        if (extractCreateCredentialResponse != null) {
            this.mCallbacks.onFinalResponseReceived(this.mComponentName, extractCreateCredentialResponse);
            return;
        }
        Log.i("ProviderCreateSession", "onSaveEntrySelected - no response or error found in pending intent response");
        invokeCallbackOnInternalInvalidState();
    }

    public final void onRemoteEntrySelected(ProviderPendingIntentResponse providerPendingIntentResponse) {
        onCreateEntrySelected(providerPendingIntentResponse);
    }

    public final CreateCredentialException maybeGetPendingIntentException(ProviderPendingIntentResponse providerPendingIntentResponse) {
        if (providerPendingIntentResponse == null) {
            Log.i("ProviderCreateSession", "pendingIntentResponse is null");
            return new CreateCredentialException("android.credentials.CreateCredentialException.TYPE_NO_CREATE_OPTIONS");
        } else if (PendingIntentResultHandler.isValidResponse(providerPendingIntentResponse)) {
            CreateCredentialException extractCreateCredentialException = PendingIntentResultHandler.extractCreateCredentialException(providerPendingIntentResponse.getResultData());
            if (extractCreateCredentialException != null) {
                Log.i("ProviderCreateSession", "Pending intent contains provider exception");
                return extractCreateCredentialException;
            }
            return null;
        } else if (PendingIntentResultHandler.isCancelledResponse(providerPendingIntentResponse)) {
            return new CreateCredentialException("android.credentials.CreateCredentialException.TYPE_USER_CANCELED");
        } else {
            return new CreateCredentialException("android.credentials.CreateCredentialException.TYPE_NO_CREATE_OPTIONS");
        }
    }

    public final void invokeCallbackOnInternalInvalidState() {
        this.mCallbacks.onFinalErrorReceived(this.mComponentName, "android.credentials.CreateCredentialException.TYPE_UNKNOWN", null);
    }

    /* loaded from: classes.dex */
    public class ProviderResponseDataHandler {
        public final ComponentName mExpectedRemoteEntryProviderService;
        public final Map<String, Pair<CreateEntry, Entry>> mUiCreateEntries = new HashMap();
        public Pair<String, Pair<RemoteEntry, Entry>> mUiRemoteEntry = null;

        public ProviderResponseDataHandler(ComponentName componentName) {
            this.mExpectedRemoteEntryProviderService = componentName;
        }

        public void addResponseContent(List<CreateEntry> list, RemoteEntry remoteEntry) {
            list.forEach(new Consumer() { // from class: com.android.server.credentials.ProviderCreateSession$ProviderResponseDataHandler$$ExternalSyntheticLambda0
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    ProviderCreateSession.ProviderResponseDataHandler.this.addCreateEntry((CreateEntry) obj);
                }
            });
            if (remoteEntry != null) {
                setRemoteEntry(remoteEntry);
            }
        }

        public void addCreateEntry(CreateEntry createEntry) {
            String generateUniqueId = ProviderSession.generateUniqueId();
            this.mUiCreateEntries.put(generateUniqueId, new Pair<>(createEntry, new Entry("save_entry_key", generateUniqueId, createEntry.getSlice(), ProviderCreateSession.this.setUpFillInIntent())));
        }

        public void setRemoteEntry(RemoteEntry remoteEntry) {
            if (!ProviderCreateSession.this.enforceRemoteEntryRestrictions(this.mExpectedRemoteEntryProviderService)) {
                Log.i("ProviderCreateSession", "Remote entry being dropped as it does not meet the restrictionchecks.");
            } else if (remoteEntry == null) {
                this.mUiRemoteEntry = null;
            } else {
                String generateUniqueId = ProviderSession.generateUniqueId();
                this.mUiRemoteEntry = new Pair<>(generateUniqueId, new Pair(remoteEntry, new Entry("remote_entry_key", generateUniqueId, remoteEntry.getSlice(), ProviderCreateSession.this.setUpFillInIntent())));
            }
        }

        public CreateCredentialProviderData toCreateCredentialProviderData() {
            return new CreateCredentialProviderData.Builder(ProviderCreateSession.this.mComponentName.flattenToString()).setSaveEntries(prepareUiCreateEntries()).setRemoteEntry(prepareRemoteEntry()).build();
        }

        public final List<Entry> prepareUiCreateEntries() {
            ArrayList arrayList = new ArrayList();
            for (String str : this.mUiCreateEntries.keySet()) {
                arrayList.add((Entry) this.mUiCreateEntries.get(str).second);
            }
            return arrayList;
        }

        public final Entry prepareRemoteEntry() {
            Object obj;
            Pair<String, Pair<RemoteEntry, Entry>> pair = this.mUiRemoteEntry;
            if (pair == null || pair.first == null || (obj = pair.second) == null) {
                return null;
            }
            return (Entry) ((Pair) obj).second;
        }

        public final boolean isEmptyResponse() {
            return this.mUiCreateEntries.isEmpty() && this.mUiRemoteEntry == null;
        }

        public RemoteEntry getRemoteEntry(String str) {
            Object obj;
            Object obj2;
            Pair<String, Pair<RemoteEntry, Entry>> pair = this.mUiRemoteEntry;
            if (pair == null || (obj = pair.first) == null || !((String) obj).equals(str) || (obj2 = this.mUiRemoteEntry.second) == null) {
                return null;
            }
            return (RemoteEntry) ((Pair) obj2).first;
        }

        public CreateEntry getCreateEntry(String str) {
            if (this.mUiCreateEntries.get(str) == null) {
                return null;
            }
            return (CreateEntry) this.mUiCreateEntries.get(str).first;
        }

        public boolean isEmptyResponse(BeginCreateCredentialResponse beginCreateCredentialResponse) {
            return (beginCreateCredentialResponse.getCreateEntries() == null || beginCreateCredentialResponse.getCreateEntries().isEmpty()) && beginCreateCredentialResponse.getRemoteCreateEntry() == null;
        }
    }
}
