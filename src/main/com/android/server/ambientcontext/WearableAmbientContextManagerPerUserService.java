package com.android.server.ambientcontext;

import android.app.ambientcontext.AmbientContextEventRequest;
import android.app.ambientcontext.IAmbientContextObserver;
import android.content.ComponentName;
import android.os.RemoteCallback;
import android.util.Slog;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.server.ambientcontext.AmbientContextManagerPerUserService;
/* loaded from: classes.dex */
public class WearableAmbientContextManagerPerUserService extends AmbientContextManagerPerUserService {
    public static final String TAG = "WearableAmbientContextManagerPerUserService";
    public ComponentName mComponentName;
    @VisibleForTesting
    RemoteWearableSensingService mRemoteService;
    public final String mServiceName;
    public final AmbientContextManagerPerUserService.ServiceType mServiceType;

    @Override // com.android.server.ambientcontext.AmbientContextManagerPerUserService
    public int getAmbientContextEventArrayExtraKeyConfig() {
        return 17040028;
    }

    @Override // com.android.server.ambientcontext.AmbientContextManagerPerUserService
    public int getAmbientContextPackageNameExtraKeyConfig() {
        return 17040029;
    }

    @Override // com.android.server.ambientcontext.AmbientContextManagerPerUserService
    public int getConsentComponentConfig() {
        return 17039908;
    }

    @Override // com.android.server.ambientcontext.AmbientContextManagerPerUserService
    public String getProtectedBindPermission() {
        return "android.permission.BIND_WEARABLE_SENSING_SERVICE";
    }

    @Override // com.android.server.ambientcontext.AmbientContextManagerPerUserService
    public /* bridge */ /* synthetic */ void onQueryServiceStatus(int[] iArr, String str, RemoteCallback remoteCallback) {
        super.onQueryServiceStatus(iArr, str, remoteCallback);
    }

    @Override // com.android.server.ambientcontext.AmbientContextManagerPerUserService
    public /* bridge */ /* synthetic */ void onRegisterObserver(AmbientContextEventRequest ambientContextEventRequest, String str, IAmbientContextObserver iAmbientContextObserver) {
        super.onRegisterObserver(ambientContextEventRequest, str, iAmbientContextObserver);
    }

    @Override // com.android.server.ambientcontext.AmbientContextManagerPerUserService
    public /* bridge */ /* synthetic */ void onStartConsentActivity(int[] iArr, String str) {
        super.onStartConsentActivity(iArr, str);
    }

    @Override // com.android.server.ambientcontext.AmbientContextManagerPerUserService
    public /* bridge */ /* synthetic */ void onUnregisterObserver(String str) {
        super.onUnregisterObserver(str);
    }

    public WearableAmbientContextManagerPerUserService(AmbientContextManagerService ambientContextManagerService, Object obj, int i, AmbientContextManagerPerUserService.ServiceType serviceType, String str) {
        super(ambientContextManagerService, obj, i);
        this.mServiceType = serviceType;
        this.mServiceName = str;
        this.mComponentName = ComponentName.unflattenFromString(str);
        String str2 = TAG;
        Slog.d(str2, "Created WearableAmbientContextManagerPerUserServiceand service type: " + serviceType.name() + " and service name: " + str);
    }

    @Override // com.android.server.ambientcontext.AmbientContextManagerPerUserService
    @GuardedBy({"mLock"})
    public void ensureRemoteServiceInitiated() {
        if (this.mRemoteService == null) {
            this.mRemoteService = new RemoteWearableSensingService(getContext(), this.mComponentName, getUserId());
        }
    }

    @Override // com.android.server.ambientcontext.AmbientContextManagerPerUserService
    @VisibleForTesting
    public ComponentName getComponentName() {
        return this.mComponentName;
    }

    @Override // com.android.server.ambientcontext.AmbientContextManagerPerUserService
    public void setComponentName(ComponentName componentName) {
        this.mComponentName = componentName;
    }

    @Override // com.android.server.ambientcontext.AmbientContextManagerPerUserService
    public RemoteAmbientDetectionService getRemoteService() {
        return this.mRemoteService;
    }

    @Override // com.android.server.ambientcontext.AmbientContextManagerPerUserService
    public AmbientContextManagerPerUserService.ServiceType getServiceType() {
        return this.mServiceType;
    }

    @Override // com.android.server.ambientcontext.AmbientContextManagerPerUserService
    public void clearRemoteService() {
        this.mRemoteService = null;
    }
}
