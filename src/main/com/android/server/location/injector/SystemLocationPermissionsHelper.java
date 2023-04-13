package com.android.server.location.injector;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.util.identity.CallerIdentity;
import android.os.Binder;
import com.android.server.FgThread;
/* loaded from: classes.dex */
public class SystemLocationPermissionsHelper extends LocationPermissionsHelper {
    public final Context mContext;
    public boolean mInited;

    public SystemLocationPermissionsHelper(Context context, AppOpsHelper appOpsHelper) {
        super(appOpsHelper);
        this.mContext = context;
    }

    public void onSystemReady() {
        if (this.mInited) {
            return;
        }
        this.mContext.getPackageManager().addOnPermissionsChangeListener(new PackageManager.OnPermissionsChangedListener() { // from class: com.android.server.location.injector.SystemLocationPermissionsHelper$$ExternalSyntheticLambda0
            public final void onPermissionsChanged(int i) {
                SystemLocationPermissionsHelper.this.lambda$onSystemReady$1(i);
            }
        });
        this.mInited = true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onSystemReady$0(int i) {
        notifyLocationPermissionsChanged(i);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onSystemReady$1(final int i) {
        FgThread.getHandler().post(new Runnable() { // from class: com.android.server.location.injector.SystemLocationPermissionsHelper$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                SystemLocationPermissionsHelper.this.lambda$onSystemReady$0(i);
            }
        });
    }

    @Override // com.android.server.location.injector.LocationPermissionsHelper
    public boolean hasPermission(String str, CallerIdentity callerIdentity) {
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            return this.mContext.checkPermission(str, callerIdentity.getPid(), callerIdentity.getUid()) == 0;
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }
}
