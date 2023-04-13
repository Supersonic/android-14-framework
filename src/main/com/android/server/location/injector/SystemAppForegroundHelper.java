package com.android.server.location.injector;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Binder;
import com.android.internal.util.Preconditions;
import com.android.server.FgThread;
import java.util.Objects;
/* loaded from: classes.dex */
public class SystemAppForegroundHelper extends AppForegroundHelper {
    public ActivityManager mActivityManager;
    public final Context mContext;

    public SystemAppForegroundHelper(Context context) {
        this.mContext = context;
    }

    public void onSystemReady() {
        if (this.mActivityManager != null) {
            return;
        }
        ActivityManager activityManager = (ActivityManager) this.mContext.getSystemService(ActivityManager.class);
        Objects.requireNonNull(activityManager);
        this.mActivityManager = activityManager;
        activityManager.addOnUidImportanceListener(new ActivityManager.OnUidImportanceListener() { // from class: com.android.server.location.injector.SystemAppForegroundHelper$$ExternalSyntheticLambda0
            public final void onUidImportance(int i, int i2) {
                SystemAppForegroundHelper.this.onAppForegroundChanged(i, i2);
            }
        }, 125);
    }

    public final void onAppForegroundChanged(final int i, int i2) {
        final boolean isForeground = AppForegroundHelper.isForeground(i2);
        FgThread.getHandler().post(new Runnable() { // from class: com.android.server.location.injector.SystemAppForegroundHelper$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                SystemAppForegroundHelper.this.lambda$onAppForegroundChanged$0(i, isForeground);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onAppForegroundChanged$0(int i, boolean z) {
        notifyAppForeground(i, z);
    }

    @Override // com.android.server.location.injector.AppForegroundHelper
    public boolean isAppForeground(int i) {
        Preconditions.checkState(this.mActivityManager != null);
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            return AppForegroundHelper.isForeground(this.mActivityManager.getUidImportance(i));
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }
}
