package com.android.server.credentials;

import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ServiceInfo;
import android.credentials.CredentialProviderInfo;
import android.credentials.ui.DisabledProviderData;
import android.credentials.ui.IntentFactory;
import android.credentials.ui.ProviderData;
import android.credentials.ui.RequestInfo;
import android.credentials.ui.UserSelectionDialogResult;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.ResultReceiver;
import android.service.credentials.CredentialProviderInfoFactory;
import android.util.Log;
import android.util.Slog;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
/* loaded from: classes.dex */
public class CredentialManagerUi {
    public final CredentialManagerUiCallback mCallbacks;
    public final Context mContext;
    public final ResultReceiver mResultReceiver = new ResultReceiver(new Handler(Looper.getMainLooper())) { // from class: com.android.server.credentials.CredentialManagerUi.1
        @Override // android.os.ResultReceiver
        public void onReceiveResult(int i, Bundle bundle) {
            CredentialManagerUi.this.handleUiResult(i, bundle);
        }
    };
    public final int mUserId;

    /* loaded from: classes.dex */
    public interface CredentialManagerUiCallback {
        void onUiCancellation(boolean z);

        void onUiSelection(UserSelectionDialogResult userSelectionDialogResult);

        void onUiSelectorInvocationFailure();
    }

    public final void handleUiResult(int i, Bundle bundle) {
        if (i == 0) {
            this.mCallbacks.onUiCancellation(true);
        } else if (i == 1) {
            this.mCallbacks.onUiCancellation(false);
        } else if (i != 2) {
            if (i == 3) {
                this.mCallbacks.onUiSelectorInvocationFailure();
                return;
            }
            Slog.i("CredentialManagerUi", "Unknown error code returned from the UI");
            this.mCallbacks.onUiSelectorInvocationFailure();
        } else {
            UserSelectionDialogResult fromResultData = UserSelectionDialogResult.fromResultData(bundle);
            if (fromResultData != null) {
                this.mCallbacks.onUiSelection(fromResultData);
            } else {
                Slog.i("CredentialManagerUi", "No selection found in UI result");
            }
        }
    }

    public CredentialManagerUi(Context context, int i, CredentialManagerUiCallback credentialManagerUiCallback) {
        Log.i("CredentialManagerUi", "In CredentialManagerUi constructor");
        this.mContext = context;
        this.mUserId = i;
        this.mCallbacks = credentialManagerUiCallback;
    }

    public PendingIntent createPendingIntent(RequestInfo requestInfo, ArrayList<ProviderData> arrayList) {
        Log.i("CredentialManagerUi", "In createPendingIntent");
        ArrayList arrayList2 = new ArrayList();
        Set set = (Set) arrayList.stream().map(new Function() { // from class: com.android.server.credentials.CredentialManagerUi$$ExternalSyntheticLambda0
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return ((ProviderData) obj).getProviderFlattenedComponentName();
            }
        }).collect(Collectors.toUnmodifiableSet());
        for (String str : (Set) CredentialProviderInfoFactory.getCredentialProviderServices(this.mContext, this.mUserId, 2, new HashSet()).stream().map(new Function() { // from class: com.android.server.credentials.CredentialManagerUi$$ExternalSyntheticLambda1
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return ((CredentialProviderInfo) obj).getServiceInfo();
            }
        }).map(new Function() { // from class: com.android.server.credentials.CredentialManagerUi$$ExternalSyntheticLambda2
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return ((ServiceInfo) obj).getComponentName();
            }
        }).map(new Function() { // from class: com.android.server.credentials.CredentialManagerUi$$ExternalSyntheticLambda3
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return ((ComponentName) obj).flattenToString();
            }
        }).collect(Collectors.toUnmodifiableSet())) {
            if (!set.contains(str)) {
                arrayList2.add(new DisabledProviderData(str));
            }
        }
        return PendingIntent.getActivity(this.mContext, 0, IntentFactory.createCredentialSelectorIntent(requestInfo, arrayList, arrayList2, this.mResultReceiver).setAction(UUID.randomUUID().toString()), 67108864);
    }
}
