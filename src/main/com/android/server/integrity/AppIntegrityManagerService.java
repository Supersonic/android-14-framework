package com.android.server.integrity;

import android.content.Context;
import com.android.server.SystemService;
/* loaded from: classes.dex */
public class AppIntegrityManagerService extends SystemService {
    public Context mContext;
    public AppIntegrityManagerServiceImpl mService;

    public AppIntegrityManagerService(Context context) {
        super(context);
        this.mContext = context;
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v1, types: [com.android.server.integrity.AppIntegrityManagerServiceImpl, android.os.IBinder] */
    @Override // com.android.server.SystemService
    public void onStart() {
        ?? create = AppIntegrityManagerServiceImpl.create(this.mContext);
        this.mService = create;
        publishBinderService("app_integrity", create);
    }
}
