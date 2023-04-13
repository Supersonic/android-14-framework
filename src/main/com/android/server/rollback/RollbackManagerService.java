package com.android.server.rollback;

import android.content.Context;
import com.android.server.LocalServices;
import com.android.server.SystemService;
/* loaded from: classes2.dex */
public final class RollbackManagerService extends SystemService {
    public RollbackManagerServiceImpl mService;

    public RollbackManagerService(Context context) {
        super(context);
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v0, types: [com.android.server.rollback.RollbackManagerServiceImpl, android.os.IBinder] */
    @Override // com.android.server.SystemService
    public void onStart() {
        ?? rollbackManagerServiceImpl = new RollbackManagerServiceImpl(getContext());
        this.mService = rollbackManagerServiceImpl;
        publishBinderService("rollback", rollbackManagerServiceImpl);
        LocalServices.addService(RollbackManagerInternal.class, this.mService);
    }

    @Override // com.android.server.SystemService
    public void onUserUnlocking(SystemService.TargetUser targetUser) {
        this.mService.onUnlockUser(targetUser.getUserIdentifier());
    }

    @Override // com.android.server.SystemService
    public void onBootPhase(int i) {
        if (i == 1000) {
            this.mService.onBootCompleted();
        }
    }
}
