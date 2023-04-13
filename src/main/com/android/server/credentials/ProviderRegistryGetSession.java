package com.android.server.credentials;

import android.content.Context;
import android.content.Intent;
import android.credentials.CredentialOption;
import android.credentials.CredentialProviderInfo;
import android.credentials.GetCredentialException;
import android.credentials.GetCredentialResponse;
import android.credentials.ui.Entry;
import android.credentials.ui.GetCredentialProviderData;
import android.credentials.ui.ProviderData;
import android.credentials.ui.ProviderPendingIntentResponse;
import android.os.Parcelable;
import android.service.credentials.CallingAppInfo;
import android.service.credentials.CredentialEntry;
import android.service.credentials.GetCredentialRequest;
import android.telecom.Log;
import com.android.internal.annotations.VisibleForTesting;
import com.android.server.credentials.CredentialDescriptionRegistry;
import com.android.server.credentials.ProviderSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
/* loaded from: classes.dex */
public class ProviderRegistryGetSession extends ProviderSession<CredentialOption, Set<CredentialDescriptionRegistry.FilterResult>> {
    @VisibleForTesting
    static final String CREDENTIAL_ENTRY_KEY = "credential_key";
    public final CallingAppInfo mCallingAppInfo;
    public final CredentialDescriptionRegistry mCredentialDescriptionRegistry;
    @VisibleForTesting
    List<CredentialEntry> mCredentialEntries;
    public final String mCredentialProviderPackageName;
    public final String mFlattenedRequestOptionString;
    public final Map<String, CredentialEntry> mUiCredentialEntries;

    @Override // com.android.server.credentials.RemoteCredentialService.ProviderCallbacks
    public void onProviderResponseFailure(int i, Exception exc) {
    }

    @Override // com.android.server.credentials.RemoteCredentialService.ProviderCallbacks
    public void onProviderResponseSuccess(Set<CredentialDescriptionRegistry.FilterResult> set) {
    }

    public static ProviderRegistryGetSession createNewSession(Context context, int i, GetRequestSession getRequestSession, CredentialProviderInfo credentialProviderInfo, CallingAppInfo callingAppInfo, String str, CredentialOption credentialOption) {
        return new ProviderRegistryGetSession(context, i, getRequestSession, credentialProviderInfo, callingAppInfo, str, credentialOption);
    }

    public ProviderRegistryGetSession(Context context, int i, GetRequestSession getRequestSession, CredentialProviderInfo credentialProviderInfo, CallingAppInfo callingAppInfo, String str, CredentialOption credentialOption) {
        super(context, credentialProviderInfo, credentialOption, getRequestSession, i, null);
        this.mUiCredentialEntries = new HashMap();
        this.mCredentialDescriptionRegistry = CredentialDescriptionRegistry.forUser(i);
        this.mCallingAppInfo = callingAppInfo;
        this.mCredentialProviderPackageName = str;
        this.mFlattenedRequestOptionString = credentialOption.getCredentialRetrievalData().getString("android.credentials.GetCredentialOption.FLATTENED_REQUEST_STRING");
    }

    public final List<Entry> prepareUiCredentialEntries(List<CredentialEntry> list) {
        Log.i("ProviderRegistryGetSession", "in prepareUiProviderDataWithCredentials", new Object[0]);
        ArrayList arrayList = new ArrayList();
        for (CredentialEntry credentialEntry : list) {
            String generateUniqueId = ProviderSession.generateUniqueId();
            this.mUiCredentialEntries.put(generateUniqueId, credentialEntry);
            Log.i("ProviderRegistryGetSession", "in prepareUiProviderData creating ui entry with id " + generateUniqueId, new Object[0]);
            arrayList.add(new Entry(CREDENTIAL_ENTRY_KEY, generateUniqueId, credentialEntry.getSlice(), setUpFillInIntent()));
        }
        return arrayList;
    }

    public final Intent setUpFillInIntent() {
        Intent intent = new Intent();
        intent.putExtra("android.service.credentials.extra.GET_CREDENTIAL_REQUEST", (Parcelable) new GetCredentialRequest(this.mCallingAppInfo, List.of((CredentialOption) this.mProviderRequest)));
        return intent;
    }

    @Override // com.android.server.credentials.ProviderSession
    /* renamed from: prepareUiData */
    public ProviderData mo2860prepareUiData() {
        Log.i("ProviderRegistryGetSession", "In prepareUiData", new Object[0]);
        if (!ProviderSession.isUiInvokingStatus(getStatus())) {
            Log.i("ProviderRegistryGetSession", "In prepareUiData - provider does not want to show UI: " + this.mComponentName.flattenToString(), new Object[0]);
            return null;
        } else if (this.mProviderResponse == 0) {
            Log.i("ProviderRegistryGetSession", "In prepareUiData response null", new Object[0]);
            throw new IllegalStateException("Response must be in completion mode");
        } else {
            return new GetCredentialProviderData.Builder(this.mComponentName.flattenToString()).setActionChips((List) null).setCredentialEntries(prepareUiCredentialEntries((List) ((Set) this.mProviderResponse).stream().flatMap(new Function() { // from class: com.android.server.credentials.ProviderRegistryGetSession$$ExternalSyntheticLambda1
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    Stream lambda$prepareUiData$0;
                    lambda$prepareUiData$0 = ProviderRegistryGetSession.lambda$prepareUiData$0((CredentialDescriptionRegistry.FilterResult) obj);
                    return lambda$prepareUiData$0;
                }
            }).collect(Collectors.toList()))).build();
        }
    }

    public static /* synthetic */ Stream lambda$prepareUiData$0(CredentialDescriptionRegistry.FilterResult filterResult) {
        return filterResult.mCredentialEntries.stream();
    }

    @Override // com.android.server.credentials.ProviderSession
    public void onUiEntrySelected(String str, String str2, ProviderPendingIntentResponse providerPendingIntentResponse) {
        str.hashCode();
        if (str.equals(CREDENTIAL_ENTRY_KEY)) {
            CredentialEntry credentialEntry = this.mUiCredentialEntries.get(str2);
            if (credentialEntry == null) {
                Log.i("ProviderRegistryGetSession", "Unexpected credential entry key", new Object[0]);
                return;
            } else {
                onCredentialEntrySelected(credentialEntry, providerPendingIntentResponse);
                return;
            }
        }
        Log.i("ProviderRegistryGetSession", "Unsupported entry type selected", new Object[0]);
    }

    public final void onCredentialEntrySelected(CredentialEntry credentialEntry, ProviderPendingIntentResponse providerPendingIntentResponse) {
        if (providerPendingIntentResponse != null) {
            GetCredentialException maybeGetPendingIntentException = maybeGetPendingIntentException(providerPendingIntentResponse);
            if (maybeGetPendingIntentException != null) {
                invokeCallbackWithError(maybeGetPendingIntentException.getType(), maybeGetPendingIntentException.getMessage());
                return;
            }
            GetCredentialResponse extractGetCredentialResponse = PendingIntentResultHandler.extractGetCredentialResponse(providerPendingIntentResponse.getResultData());
            if (extractGetCredentialResponse != null) {
                ProviderSession.ProviderInternalCallback providerInternalCallback = this.mCallbacks;
                if (providerInternalCallback != null) {
                    ((GetRequestSession) providerInternalCallback).onFinalResponseReceived(this.mComponentName, extractGetCredentialResponse);
                    return;
                }
                return;
            }
            Log.i("ProviderRegistryGetSession", "Pending intent response contains no credential, or error", new Object[0]);
        }
        Log.i("ProviderRegistryGetSession", "CredentialEntry does not have a credential or a pending intent result", new Object[0]);
    }

    /* JADX WARN: Type inference failed for: r0v1, types: [R, java.util.Set] */
    @Override // com.android.server.credentials.ProviderSession
    public void invokeSession() {
        ?? filteredResultForProvider = this.mCredentialDescriptionRegistry.getFilteredResultForProvider(this.mCredentialProviderPackageName, this.mFlattenedRequestOptionString);
        this.mProviderResponse = filteredResultForProvider;
        this.mCredentialEntries = (List) ((Set) filteredResultForProvider).stream().flatMap(new Function() { // from class: com.android.server.credentials.ProviderRegistryGetSession$$ExternalSyntheticLambda0
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                Stream lambda$invokeSession$1;
                lambda$invokeSession$1 = ProviderRegistryGetSession.lambda$invokeSession$1((CredentialDescriptionRegistry.FilterResult) obj);
                return lambda$invokeSession$1;
            }
        }).collect(Collectors.toList());
        updateStatusAndInvokeCallback(ProviderSession.Status.CREDENTIALS_RECEIVED);
    }

    public static /* synthetic */ Stream lambda$invokeSession$1(CredentialDescriptionRegistry.FilterResult filterResult) {
        return filterResult.mCredentialEntries.stream();
    }

    public GetCredentialException maybeGetPendingIntentException(ProviderPendingIntentResponse providerPendingIntentResponse) {
        if (providerPendingIntentResponse == null) {
            android.util.Log.i("ProviderRegistryGetSession", "pendingIntentResponse is null");
            return null;
        } else if (PendingIntentResultHandler.isValidResponse(providerPendingIntentResponse)) {
            GetCredentialException extractGetCredentialException = PendingIntentResultHandler.extractGetCredentialException(providerPendingIntentResponse.getResultData());
            if (extractGetCredentialException != null) {
                android.util.Log.i("ProviderRegistryGetSession", "Pending intent contains provider exception");
                return extractGetCredentialException;
            }
            return null;
        } else if (PendingIntentResultHandler.isCancelledResponse(providerPendingIntentResponse)) {
            return new GetCredentialException("android.credentials.GetCredentialException.TYPE_USER_CANCELED");
        } else {
            return new GetCredentialException("android.credentials.GetCredentialException.TYPE_NO_CREDENTIAL");
        }
    }
}
