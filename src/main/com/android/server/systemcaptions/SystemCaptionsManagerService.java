package com.android.server.systemcaptions;

import android.content.Context;
import com.android.server.infra.AbstractMasterSystemService;
import com.android.server.infra.FrameworkResourcesServiceNameResolver;
/* loaded from: classes2.dex */
public final class SystemCaptionsManagerService extends AbstractMasterSystemService<SystemCaptionsManagerService, SystemCaptionsManagerPerUserService> {
    @Override // com.android.server.SystemService
    public void onStart() {
    }

    public SystemCaptionsManagerService(Context context) {
        super(context, new FrameworkResourcesServiceNameResolver(context, 17039903), null, 68);
    }

    @Override // com.android.server.infra.AbstractMasterSystemService
    public SystemCaptionsManagerPerUserService newServiceLocked(int i, boolean z) {
        SystemCaptionsManagerPerUserService systemCaptionsManagerPerUserService = new SystemCaptionsManagerPerUserService(this, this.mLock, z, i);
        systemCaptionsManagerPerUserService.initializeLocked();
        return systemCaptionsManagerPerUserService;
    }

    @Override // com.android.server.infra.AbstractMasterSystemService
    public void onServiceRemoved(SystemCaptionsManagerPerUserService systemCaptionsManagerPerUserService, int i) {
        synchronized (this.mLock) {
            systemCaptionsManagerPerUserService.destroyLocked();
        }
    }
}
