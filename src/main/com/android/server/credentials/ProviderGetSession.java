package com.android.server.credentials;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.credentials.CredentialOption;
import android.credentials.CredentialProviderInfo;
import android.credentials.GetCredentialException;
import android.credentials.GetCredentialRequest;
import android.credentials.GetCredentialResponse;
import android.credentials.ui.AuthenticationEntry;
import android.credentials.ui.Entry;
import android.credentials.ui.GetCredentialProviderData;
import android.credentials.ui.ProviderPendingIntentResponse;
import android.os.Parcelable;
import android.service.credentials.Action;
import android.service.credentials.BeginGetCredentialOption;
import android.service.credentials.BeginGetCredentialRequest;
import android.service.credentials.BeginGetCredentialResponse;
import android.service.credentials.CallingAppInfo;
import android.service.credentials.CredentialEntry;
import android.service.credentials.RemoteEntry;
import android.util.Log;
import android.util.Pair;
import com.android.server.credentials.ProviderGetSession;
import com.android.server.credentials.ProviderSession;
import com.android.server.credentials.metrics.EntryEnum;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
/* loaded from: classes.dex */
public final class ProviderGetSession extends ProviderSession<BeginGetCredentialRequest, BeginGetCredentialResponse> {
    public final Map<String, CredentialOption> mBeginGetOptionToCredentialOptionMap;
    public final CallingAppInfo mCallingAppInfo;
    public final GetCredentialRequest mCompleteRequest;
    public GetCredentialException mProviderException;
    public final ProviderResponseDataHandler mProviderResponseDataHandler;

    public static ProviderGetSession createNewSession(Context context, int i, CredentialProviderInfo credentialProviderInfo, GetRequestSession getRequestSession, RemoteCredentialService remoteCredentialService) {
        GetCredentialRequest filterOptions = filterOptions(credentialProviderInfo.getCapabilities(), (GetCredentialRequest) getRequestSession.mClientRequest);
        if (filterOptions != null) {
            HashMap hashMap = new HashMap();
            return new ProviderGetSession(context, credentialProviderInfo, getRequestSession, i, remoteCredentialService, constructQueryPhaseRequest(filterOptions, getRequestSession.mClientAppInfo, ((GetCredentialRequest) getRequestSession.mClientRequest).alwaysSendAppInfoToProvider(), hashMap), filterOptions, getRequestSession.mClientAppInfo, hashMap, getRequestSession.mHybridService);
        }
        Log.i("ProviderGetSession", "Unable to create provider session");
        return null;
    }

    public static BeginGetCredentialRequest constructQueryPhaseRequest(GetCredentialRequest getCredentialRequest, CallingAppInfo callingAppInfo, boolean z, final Map<String, CredentialOption> map) {
        final BeginGetCredentialRequest.Builder builder = new BeginGetCredentialRequest.Builder();
        getCredentialRequest.getCredentialOptions().forEach(new Consumer() { // from class: com.android.server.credentials.ProviderGetSession$$ExternalSyntheticLambda0
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                ProviderGetSession.lambda$constructQueryPhaseRequest$0(builder, map, (CredentialOption) obj);
            }
        });
        if (z) {
            builder.setCallingAppInfo(callingAppInfo);
        }
        return builder.build();
    }

    public static /* synthetic */ void lambda$constructQueryPhaseRequest$0(BeginGetCredentialRequest.Builder builder, Map map, CredentialOption credentialOption) {
        String generateUniqueId = ProviderSession.generateUniqueId();
        builder.addBeginGetCredentialOption(new BeginGetCredentialOption(generateUniqueId, credentialOption.getType(), credentialOption.getCandidateQueryData()));
        map.put(generateUniqueId, credentialOption);
    }

    public static GetCredentialRequest filterOptions(List<String> list, GetCredentialRequest getCredentialRequest) {
        ArrayList arrayList = new ArrayList();
        for (CredentialOption credentialOption : getCredentialRequest.getCredentialOptions()) {
            if (list.contains(credentialOption.getType())) {
                Log.i("ProviderGetSession", "In createProviderRequest - capability found : " + credentialOption.getType());
                arrayList.add(credentialOption);
            } else {
                Log.i("ProviderGetSession", "In createProviderRequest - capability not found : " + credentialOption.getType());
            }
        }
        if (!arrayList.isEmpty()) {
            return new GetCredentialRequest.Builder(getCredentialRequest.getData()).setCredentialOptions(arrayList).build();
        }
        Log.i("ProviderGetSession", "In createProviderRequest - returning null");
        return null;
    }

    public ProviderGetSession(Context context, CredentialProviderInfo credentialProviderInfo, ProviderSession.ProviderInternalCallback<GetCredentialResponse> providerInternalCallback, int i, RemoteCredentialService remoteCredentialService, BeginGetCredentialRequest beginGetCredentialRequest, GetCredentialRequest getCredentialRequest, CallingAppInfo callingAppInfo, Map<String, CredentialOption> map, String str) {
        super(context, credentialProviderInfo, beginGetCredentialRequest, providerInternalCallback, i, remoteCredentialService);
        this.mCompleteRequest = getCredentialRequest;
        this.mCallingAppInfo = callingAppInfo;
        setStatus(ProviderSession.Status.PENDING);
        this.mBeginGetOptionToCredentialOptionMap = new HashMap(map);
        this.mProviderResponseDataHandler = new ProviderResponseDataHandler(ComponentName.unflattenFromString(str));
    }

    @Override // com.android.server.credentials.RemoteCredentialService.ProviderCallbacks
    public void onProviderResponseSuccess(BeginGetCredentialResponse beginGetCredentialResponse) {
        onSetInitialRemoteResponse(beginGetCredentialResponse);
    }

    @Override // com.android.server.credentials.RemoteCredentialService.ProviderCallbacks
    public void onProviderResponseFailure(int i, Exception exc) {
        if (exc instanceof GetCredentialException) {
            this.mProviderException = (GetCredentialException) exc;
        }
        captureCandidateFailureInMetrics();
        updateStatusAndInvokeCallback(ProviderSession.toStatus(i));
    }

    @Override // com.android.server.credentials.ProviderSession
    public void onUiEntrySelected(String str, String str2, ProviderPendingIntentResponse providerPendingIntentResponse) {
        Log.i("ProviderGetSession", "onUiEntrySelected with entryKey: " + str2);
        str.hashCode();
        char c = 65535;
        switch (str.hashCode()) {
            case 1110515801:
                if (str.equals("remote_entry_key")) {
                    c = 0;
                    break;
                }
                break;
            case 1182857469:
                if (str.equals("authentication_action_key")) {
                    c = 1;
                    break;
                }
                break;
            case 1208398455:
                if (str.equals("credential_key")) {
                    c = 2;
                    break;
                }
                break;
            case 1852195030:
                if (str.equals("action_key")) {
                    c = 3;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                if (this.mProviderResponseDataHandler.getRemoteEntry(str2) != null) {
                    onRemoteEntrySelected(providerPendingIntentResponse);
                    return;
                }
                Log.i("ProviderGetSession", "Unexpected remote entry key");
                invokeCallbackOnInternalInvalidState();
                return;
            case 1:
                if (this.mProviderResponseDataHandler.getAuthenticationAction(str2) == null) {
                    Log.i("ProviderGetSession", "Unexpected authenticationEntry key");
                    invokeCallbackOnInternalInvalidState();
                    return;
                } else if (onAuthenticationEntrySelected(providerPendingIntentResponse)) {
                    Log.i("ProviderGetSession", "Additional content received - removing authentication entry");
                    this.mProviderResponseDataHandler.removeAuthenticationAction(str2);
                    if (this.mProviderResponseDataHandler.isEmptyResponse()) {
                        return;
                    }
                    updateStatusAndInvokeCallback(ProviderSession.Status.CREDENTIALS_RECEIVED);
                    return;
                } else {
                    Log.i("ProviderGetSession", "Additional content not received");
                    this.mProviderResponseDataHandler.updateAuthEntryWithNoCredentialsReceived(str2);
                    updateStatusAndInvokeCallback(ProviderSession.Status.NO_CREDENTIALS_FROM_AUTH_ENTRY);
                    return;
                }
            case 2:
                if (this.mProviderResponseDataHandler.getCredentialEntry(str2) == null) {
                    Log.i("ProviderGetSession", "Unexpected credential entry key");
                    invokeCallbackOnInternalInvalidState();
                    return;
                }
                onCredentialEntrySelected(providerPendingIntentResponse);
                return;
            case 3:
                if (this.mProviderResponseDataHandler.getActionEntry(str2) == null) {
                    Log.i("ProviderGetSession", "Unexpected action entry key");
                    invokeCallbackOnInternalInvalidState();
                    return;
                }
                onActionEntrySelected(providerPendingIntentResponse);
                return;
            default:
                Log.i("ProviderGetSession", "Unsupported entry type selected");
                invokeCallbackOnInternalInvalidState();
                return;
        }
    }

    @Override // com.android.server.credentials.ProviderSession
    public void invokeSession() {
        if (this.mRemoteCredentialService != null) {
            startCandidateMetrics();
            this.mRemoteCredentialService.onBeginGetCredential((BeginGetCredentialRequest) this.mProviderRequest, this);
        }
    }

    @Override // com.android.server.credentials.ProviderSession
    /* renamed from: prepareUiData */
    public GetCredentialProviderData mo2860prepareUiData() throws IllegalArgumentException {
        Log.i("ProviderGetSession", "In prepareUiData");
        if (!ProviderSession.isUiInvokingStatus(getStatus())) {
            Log.i("ProviderGetSession", "In prepareUiData - provider does not want to show UI: " + this.mComponentName.flattenToString());
            return null;
        } else if (this.mProviderResponse != 0 && !this.mProviderResponseDataHandler.isEmptyResponse()) {
            return this.mProviderResponseDataHandler.toGetCredentialProviderData();
        } else {
            Log.i("ProviderGetSession", "In prepareUiData response null");
            return null;
        }
    }

    public final Intent setUpFillInIntent(String str) {
        if (this.mBeginGetOptionToCredentialOptionMap.get(str) == null) {
            Log.i("ProviderGetSession", "Id from Credential Entry does not resolve to a valid option");
            return new Intent();
        }
        return new Intent().putExtra("android.service.credentials.extra.GET_CREDENTIAL_REQUEST", (Parcelable) new android.service.credentials.GetCredentialRequest(this.mCallingAppInfo, List.of(this.mBeginGetOptionToCredentialOptionMap.get(str))));
    }

    public final Intent setUpFillInIntentWithQueryRequest() {
        Intent intent = new Intent();
        intent.putExtra("android.service.credentials.extra.BEGIN_GET_CREDENTIAL_REQUEST", (Parcelable) this.mProviderRequest);
        return intent;
    }

    public final void onRemoteEntrySelected(ProviderPendingIntentResponse providerPendingIntentResponse) {
        onCredentialEntrySelected(providerPendingIntentResponse);
    }

    public final void onCredentialEntrySelected(ProviderPendingIntentResponse providerPendingIntentResponse) {
        if (providerPendingIntentResponse == null) {
            invokeCallbackOnInternalInvalidState();
            return;
        }
        GetCredentialException maybeGetPendingIntentException = maybeGetPendingIntentException(providerPendingIntentResponse);
        if (maybeGetPendingIntentException != null) {
            invokeCallbackWithError(maybeGetPendingIntentException.getType(), maybeGetPendingIntentException.getMessage());
            return;
        }
        GetCredentialResponse extractGetCredentialResponse = PendingIntentResultHandler.extractGetCredentialResponse(providerPendingIntentResponse.getResultData());
        if (extractGetCredentialResponse != null) {
            this.mCallbacks.onFinalResponseReceived(this.mComponentName, extractGetCredentialResponse);
            return;
        }
        Log.i("ProviderGetSession", "Pending intent response contains no credential, or error");
        invokeCallbackOnInternalInvalidState();
    }

    public final GetCredentialException maybeGetPendingIntentException(ProviderPendingIntentResponse providerPendingIntentResponse) {
        if (providerPendingIntentResponse == null) {
            Log.i("ProviderGetSession", "pendingIntentResponse is null");
            return null;
        } else if (PendingIntentResultHandler.isValidResponse(providerPendingIntentResponse)) {
            GetCredentialException extractGetCredentialException = PendingIntentResultHandler.extractGetCredentialException(providerPendingIntentResponse.getResultData());
            if (extractGetCredentialException != null) {
                Log.i("ProviderGetSession", "Pending intent contains provider exception");
                return extractGetCredentialException;
            }
            return null;
        } else if (PendingIntentResultHandler.isCancelledResponse(providerPendingIntentResponse)) {
            return new GetCredentialException("android.credentials.GetCredentialException.TYPE_USER_CANCELED");
        } else {
            return new GetCredentialException("android.credentials.GetCredentialException.TYPE_NO_CREDENTIAL");
        }
    }

    public final boolean onAuthenticationEntrySelected(ProviderPendingIntentResponse providerPendingIntentResponse) {
        Log.i("ProviderGetSession", "onAuthenticationEntrySelected");
        if (providerPendingIntentResponse == null) {
            Log.i("ProviderGetSession", "providerPendingIntentResponse is null");
            return false;
        }
        GetCredentialException maybeGetPendingIntentException = maybeGetPendingIntentException(providerPendingIntentResponse);
        if (maybeGetPendingIntentException != null) {
            invokeCallbackWithError(maybeGetPendingIntentException.getType(), maybeGetPendingIntentException.getMessage());
            return true;
        }
        BeginGetCredentialResponse extractResponseContent = PendingIntentResultHandler.extractResponseContent(providerPendingIntentResponse.getResultData());
        if (extractResponseContent == null || this.mProviderResponseDataHandler.isEmptyResponse(extractResponseContent)) {
            return false;
        }
        addToInitialRemoteResponse(extractResponseContent, false);
        return true;
    }

    public final void addToInitialRemoteResponse(BeginGetCredentialResponse beginGetCredentialResponse, boolean z) {
        if (beginGetCredentialResponse == null) {
            return;
        }
        this.mProviderResponseDataHandler.addResponseContent(beginGetCredentialResponse.getCredentialEntries(), beginGetCredentialResponse.getActions(), beginGetCredentialResponse.getAuthenticationActions(), beginGetCredentialResponse.getRemoteCredentialEntry(), z);
    }

    public final void onActionEntrySelected(ProviderPendingIntentResponse providerPendingIntentResponse) {
        Log.i("ProviderGetSession", "onActionEntrySelected");
        onCredentialEntrySelected(providerPendingIntentResponse);
    }

    /* JADX WARN: Multi-variable type inference failed */
    public final void onSetInitialRemoteResponse(BeginGetCredentialResponse beginGetCredentialResponse) {
        this.mProviderResponse = beginGetCredentialResponse;
        addToInitialRemoteResponse(beginGetCredentialResponse, true);
        if (this.mProviderResponseDataHandler.isEmptyResponse(beginGetCredentialResponse)) {
            updateStatusAndInvokeCallback(ProviderSession.Status.EMPTY_RESPONSE);
            return;
        }
        gatherCandidateEntryMetrics(beginGetCredentialResponse);
        updateStatusAndInvokeCallback(ProviderSession.Status.CREDENTIALS_RECEIVED);
    }

    public final void gatherCandidateEntryMetrics(BeginGetCredentialResponse beginGetCredentialResponse) {
        int i;
        try {
            int size = beginGetCredentialResponse.getCredentialEntries().size();
            int size2 = beginGetCredentialResponse.getActions().size();
            int size3 = beginGetCredentialResponse.getAuthenticationActions().size();
            if (beginGetCredentialResponse.getRemoteCredentialEntry() != null) {
                this.mCandidatePhasePerProviderMetric.addEntry(EntryEnum.REMOTE_ENTRY);
                i = 1;
            } else {
                i = 0;
            }
            beginGetCredentialResponse.getCredentialEntries().forEach(new Consumer() { // from class: com.android.server.credentials.ProviderGetSession$$ExternalSyntheticLambda2
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    ProviderGetSession.this.lambda$gatherCandidateEntryMetrics$1((CredentialEntry) obj);
                }
            });
            beginGetCredentialResponse.getActions().forEach(new Consumer() { // from class: com.android.server.credentials.ProviderGetSession$$ExternalSyntheticLambda3
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    ProviderGetSession.this.lambda$gatherCandidateEntryMetrics$2((Action) obj);
                }
            });
            beginGetCredentialResponse.getAuthenticationActions().forEach(new Consumer() { // from class: com.android.server.credentials.ProviderGetSession$$ExternalSyntheticLambda4
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    ProviderGetSession.this.lambda$gatherCandidateEntryMetrics$3((Action) obj);
                }
            });
            this.mCandidatePhasePerProviderMetric.setNumEntriesTotal(size + size3 + size2 + i);
            this.mCandidatePhasePerProviderMetric.setCredentialEntryCount(size);
            this.mCandidatePhasePerProviderMetric.setCredentialEntryTypeCount(((Set) beginGetCredentialResponse.getCredentialEntries().stream().map(new Function() { // from class: com.android.server.credentials.ProviderGetSession$$ExternalSyntheticLambda5
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    return ((CredentialEntry) obj).getType();
                }
            }).collect(Collectors.toSet())).size());
            this.mCandidatePhasePerProviderMetric.setActionEntryCount(size2);
            this.mCandidatePhasePerProviderMetric.setAuthenticationEntryCount(size3);
            this.mCandidatePhasePerProviderMetric.setRemoteEntryCount(i);
        } catch (Exception e) {
            Log.w("ProviderGetSession", "Unexpected error during metric logging: " + e);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$gatherCandidateEntryMetrics$1(CredentialEntry credentialEntry) {
        this.mCandidatePhasePerProviderMetric.addEntry(EntryEnum.CREDENTIAL_ENTRY);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$gatherCandidateEntryMetrics$2(Action action) {
        this.mCandidatePhasePerProviderMetric.addEntry(EntryEnum.ACTION_ENTRY);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$gatherCandidateEntryMetrics$3(Action action) {
        this.mCandidatePhasePerProviderMetric.addEntry(EntryEnum.AUTHENTICATION_ENTRY);
    }

    public final void invokeCallbackOnInternalInvalidState() {
        this.mCallbacks.onFinalErrorReceived(this.mComponentName, "android.credentials.GetCredentialException.TYPE_NO_CREDENTIAL", null);
    }

    public void updateAuthEntriesStatusFromAnotherSession() {
        this.mProviderResponseDataHandler.updateAuthEntryWithNoCredentialsReceived(null);
    }

    public boolean containsEmptyAuthEntriesOnly() {
        return this.mProviderResponseDataHandler.mUiCredentialEntries.isEmpty() && this.mProviderResponseDataHandler.mUiRemoteEntry == null && this.mProviderResponseDataHandler.mUiAuthenticationEntries.values().stream().allMatch(new Predicate() { // from class: com.android.server.credentials.ProviderGetSession$$ExternalSyntheticLambda1
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$containsEmptyAuthEntriesOnly$4;
                lambda$containsEmptyAuthEntriesOnly$4 = ProviderGetSession.lambda$containsEmptyAuthEntriesOnly$4((Pair) obj);
                return lambda$containsEmptyAuthEntriesOnly$4;
            }
        });
    }

    public static /* synthetic */ boolean lambda$containsEmptyAuthEntriesOnly$4(Pair pair) {
        return ((AuthenticationEntry) pair.second).getStatus() == 1 || ((AuthenticationEntry) pair.second).getStatus() == 2;
    }

    /* loaded from: classes.dex */
    public class ProviderResponseDataHandler {
        public final ComponentName mExpectedRemoteEntryProviderService;
        public final Map<String, Pair<CredentialEntry, Entry>> mUiCredentialEntries = new HashMap();
        public final Map<String, Pair<Action, Entry>> mUiActionsEntries = new HashMap();
        public final Map<String, Pair<Action, AuthenticationEntry>> mUiAuthenticationEntries = new HashMap();
        public Pair<String, Pair<RemoteEntry, Entry>> mUiRemoteEntry = null;

        public ProviderResponseDataHandler(ComponentName componentName) {
            this.mExpectedRemoteEntryProviderService = componentName;
        }

        public void addResponseContent(List<CredentialEntry> list, List<Action> list2, List<Action> list3, RemoteEntry remoteEntry, boolean z) {
            list.forEach(new Consumer() { // from class: com.android.server.credentials.ProviderGetSession$ProviderResponseDataHandler$$ExternalSyntheticLambda1
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    ProviderGetSession.ProviderResponseDataHandler.this.addCredentialEntry((CredentialEntry) obj);
                }
            });
            list2.forEach(new Consumer() { // from class: com.android.server.credentials.ProviderGetSession$ProviderResponseDataHandler$$ExternalSyntheticLambda2
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    ProviderGetSession.ProviderResponseDataHandler.this.addAction((Action) obj);
                }
            });
            list3.forEach(new Consumer() { // from class: com.android.server.credentials.ProviderGetSession$ProviderResponseDataHandler$$ExternalSyntheticLambda3
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    ProviderGetSession.ProviderResponseDataHandler.this.lambda$addResponseContent$0((Action) obj);
                }
            });
            if (remoteEntry == null && z) {
                return;
            }
            setRemoteEntry(remoteEntry);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$addResponseContent$0(Action action) {
            addAuthenticationAction(action, 0);
        }

        public void addCredentialEntry(CredentialEntry credentialEntry) {
            String generateUniqueId = ProviderSession.generateUniqueId();
            this.mUiCredentialEntries.put(generateUniqueId, new Pair<>(credentialEntry, new Entry("credential_key", generateUniqueId, credentialEntry.getSlice(), ProviderGetSession.this.setUpFillInIntent(credentialEntry.getBeginGetCredentialOptionId()))));
        }

        public void addAction(Action action) {
            String generateUniqueId = ProviderSession.generateUniqueId();
            this.mUiActionsEntries.put(generateUniqueId, new Pair<>(action, new Entry("action_key", generateUniqueId, action.getSlice(), ProviderGetSession.this.setUpFillInIntentWithQueryRequest())));
        }

        public void addAuthenticationAction(Action action, int i) {
            Log.i("ProviderGetSession", "In addAuthenticationAction");
            String generateUniqueId = ProviderSession.generateUniqueId();
            Log.i("ProviderGetSession", "In addAuthenticationAction, id : " + generateUniqueId);
            this.mUiAuthenticationEntries.put(generateUniqueId, new Pair<>(action, new AuthenticationEntry("authentication_action_key", generateUniqueId, action.getSlice(), i, ProviderGetSession.this.setUpFillInIntentWithQueryRequest())));
        }

        public void removeAuthenticationAction(String str) {
            this.mUiAuthenticationEntries.remove(str);
        }

        public void setRemoteEntry(RemoteEntry remoteEntry) {
            if (!ProviderGetSession.this.enforceRemoteEntryRestrictions(this.mExpectedRemoteEntryProviderService)) {
                Log.i("ProviderGetSession", "Remote entry being dropped as it does not meet the restriction checks.");
            } else if (remoteEntry == null) {
                this.mUiRemoteEntry = null;
            } else {
                String generateUniqueId = ProviderSession.generateUniqueId();
                this.mUiRemoteEntry = new Pair<>(generateUniqueId, new Pair(remoteEntry, new Entry("remote_entry_key", generateUniqueId, remoteEntry.getSlice(), ProviderGetSession.this.setUpFillInIntentForRemoteEntry())));
            }
        }

        public GetCredentialProviderData toGetCredentialProviderData() {
            return new GetCredentialProviderData.Builder(ProviderGetSession.this.mComponentName.flattenToString()).setActionChips(prepareActionEntries()).setCredentialEntries(prepareCredentialEntries()).setAuthenticationEntries(prepareAuthenticationEntries()).setRemoteEntry(prepareRemoteEntry()).build();
        }

        public final List<Entry> prepareActionEntries() {
            ArrayList arrayList = new ArrayList();
            for (String str : this.mUiActionsEntries.keySet()) {
                arrayList.add((Entry) this.mUiActionsEntries.get(str).second);
            }
            return arrayList;
        }

        public final List<AuthenticationEntry> prepareAuthenticationEntries() {
            ArrayList arrayList = new ArrayList();
            for (String str : this.mUiAuthenticationEntries.keySet()) {
                arrayList.add((AuthenticationEntry) this.mUiAuthenticationEntries.get(str).second);
            }
            return arrayList;
        }

        public final List<Entry> prepareCredentialEntries() {
            ArrayList arrayList = new ArrayList();
            for (String str : this.mUiCredentialEntries.keySet()) {
                arrayList.add((Entry) this.mUiCredentialEntries.get(str).second);
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
            return this.mUiCredentialEntries.isEmpty() && this.mUiActionsEntries.isEmpty() && this.mUiAuthenticationEntries.isEmpty() && this.mUiRemoteEntry == null;
        }

        public final boolean isEmptyResponse(BeginGetCredentialResponse beginGetCredentialResponse) {
            return beginGetCredentialResponse.getCredentialEntries().isEmpty() && beginGetCredentialResponse.getActions().isEmpty() && beginGetCredentialResponse.getAuthenticationActions().isEmpty() && beginGetCredentialResponse.getRemoteCredentialEntry() == null;
        }

        public Action getAuthenticationAction(String str) {
            if (this.mUiAuthenticationEntries.get(str) == null) {
                return null;
            }
            return (Action) this.mUiAuthenticationEntries.get(str).first;
        }

        public Action getActionEntry(String str) {
            if (this.mUiActionsEntries.get(str) == null) {
                return null;
            }
            return (Action) this.mUiActionsEntries.get(str).first;
        }

        public RemoteEntry getRemoteEntry(String str) {
            Object obj;
            if (!((String) this.mUiRemoteEntry.first).equals(str) || (obj = this.mUiRemoteEntry.second) == null) {
                return null;
            }
            return (RemoteEntry) ((Pair) obj).first;
        }

        public CredentialEntry getCredentialEntry(String str) {
            if (this.mUiCredentialEntries.get(str) == null) {
                return null;
            }
            return (CredentialEntry) this.mUiCredentialEntries.get(str).first;
        }

        public void updateAuthEntryWithNoCredentialsReceived(String str) {
            if (str == null) {
                updatePreviousMostRecentAuthEntry();
                return;
            }
            updatePreviousMostRecentAuthEntry();
            updateMostRecentAuthEntry(str);
        }

        public final void updateMostRecentAuthEntry(String str) {
            this.mUiAuthenticationEntries.put(str, new Pair<>((Action) this.mUiAuthenticationEntries.get(str).first, copyAuthEntryAndChangeStatus((AuthenticationEntry) this.mUiAuthenticationEntries.get(str).second, 2)));
        }

        public final void updatePreviousMostRecentAuthEntry() {
            Optional<Map.Entry<String, Pair<Action, AuthenticationEntry>>> findFirst = this.mUiAuthenticationEntries.entrySet().stream().filter(new Predicate() { // from class: com.android.server.credentials.ProviderGetSession$ProviderResponseDataHandler$$ExternalSyntheticLambda0
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean lambda$updatePreviousMostRecentAuthEntry$1;
                    lambda$updatePreviousMostRecentAuthEntry$1 = ProviderGetSession.ProviderResponseDataHandler.lambda$updatePreviousMostRecentAuthEntry$1((Map.Entry) obj);
                    return lambda$updatePreviousMostRecentAuthEntry$1;
                }
            }).findFirst();
            if (findFirst.isEmpty()) {
                Log.i("ProviderGetSession", "In updatePreviousMostRecentAuthEntry - previous entry not found");
                return;
            }
            String key = findFirst.get().getKey();
            this.mUiAuthenticationEntries.remove(key);
            this.mUiAuthenticationEntries.put(key, new Pair<>((Action) findFirst.get().getValue().first, copyAuthEntryAndChangeStatus((AuthenticationEntry) findFirst.get().getValue().second, 1)));
        }

        public static /* synthetic */ boolean lambda$updatePreviousMostRecentAuthEntry$1(Map.Entry entry) {
            return ((AuthenticationEntry) ((Pair) entry.getValue()).second).getStatus() == 2;
        }

        public final AuthenticationEntry copyAuthEntryAndChangeStatus(AuthenticationEntry authenticationEntry, Integer num) {
            return new AuthenticationEntry("authentication_action_key", authenticationEntry.getSubkey(), authenticationEntry.getSlice(), num.intValue(), authenticationEntry.getFrameworkExtrasIntent());
        }
    }

    public final Intent setUpFillInIntentForRemoteEntry() {
        return new Intent().putExtra("android.service.credentials.extra.GET_CREDENTIAL_REQUEST", (Parcelable) new android.service.credentials.GetCredentialRequest(this.mCallingAppInfo, this.mCompleteRequest.getCredentialOptions()));
    }
}
