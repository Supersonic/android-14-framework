package com.android.server.credentials;

import android.content.Intent;
import android.credentials.CreateCredentialException;
import android.credentials.CreateCredentialResponse;
import android.credentials.GetCredentialException;
import android.credentials.GetCredentialResponse;
import android.credentials.ui.ProviderPendingIntentResponse;
import android.service.credentials.BeginGetCredentialResponse;
/* loaded from: classes.dex */
public class PendingIntentResultHandler {
    public static boolean isValidResponse(ProviderPendingIntentResponse providerPendingIntentResponse) {
        return providerPendingIntentResponse.getResultCode() == -1;
    }

    public static boolean isCancelledResponse(ProviderPendingIntentResponse providerPendingIntentResponse) {
        return providerPendingIntentResponse.getResultCode() == 0;
    }

    public static BeginGetCredentialResponse extractResponseContent(Intent intent) {
        if (intent == null) {
            return null;
        }
        return (BeginGetCredentialResponse) intent.getParcelableExtra("android.service.credentials.extra.BEGIN_GET_CREDENTIAL_RESPONSE", BeginGetCredentialResponse.class);
    }

    public static CreateCredentialResponse extractCreateCredentialResponse(Intent intent) {
        if (intent == null) {
            return null;
        }
        return (CreateCredentialResponse) intent.getParcelableExtra("android.service.credentials.extra.CREATE_CREDENTIAL_RESPONSE", CreateCredentialResponse.class);
    }

    public static GetCredentialResponse extractGetCredentialResponse(Intent intent) {
        if (intent == null) {
            return null;
        }
        return (GetCredentialResponse) intent.getParcelableExtra("android.service.credentials.extra.GET_CREDENTIAL_RESPONSE", GetCredentialResponse.class);
    }

    public static CreateCredentialException extractCreateCredentialException(Intent intent) {
        if (intent == null) {
            return null;
        }
        return (CreateCredentialException) intent.getParcelableExtra("android.service.credentials.extra.CREATE_CREDENTIAL_EXCEPTION", CreateCredentialException.class);
    }

    public static GetCredentialException extractGetCredentialException(Intent intent) {
        if (intent == null) {
            return null;
        }
        return (GetCredentialException) intent.getParcelableExtra("android.service.credentials.extra.GET_CREDENTIAL_EXCEPTION", GetCredentialException.class);
    }
}
