package com.android.server;

import android.content.ContentResolver;
import android.database.ContentObserver;
import android.os.Handler;
import android.os.Looper;
import android.os.UserManager;
import android.provider.Settings;
import com.android.server.p006am.ActivityManagerService;
import com.android.server.p011pm.UserManagerInternal;
import com.android.server.utils.Slogf;
import com.android.server.utils.TimingsTraceAndSlog;
/* loaded from: classes.dex */
public final class HsumBootUserInitializer {
    public static final String TAG = "HsumBootUserInitializer";
    public final ActivityManagerService mAms;
    public final ContentResolver mContentResolver;
    public final ContentObserver mDeviceProvisionedObserver = new ContentObserver(new Handler(Looper.getMainLooper())) { // from class: com.android.server.HsumBootUserInitializer.1
        @Override // android.database.ContentObserver
        public void onChange(boolean z) {
            if (HsumBootUserInitializer.this.isDeviceProvisioned()) {
                Slogf.m22i(HsumBootUserInitializer.TAG, "Marking USER_SETUP_COMPLETE for system user");
                Settings.Secure.putInt(HsumBootUserInitializer.this.mContentResolver, "user_setup_complete", 1);
                HsumBootUserInitializer.this.mContentResolver.unregisterContentObserver(HsumBootUserInitializer.this.mDeviceProvisionedObserver);
            }
        }
    };
    public final boolean mShouldAlwaysHaveMainUser;
    public final UserManagerInternal mUmi;

    public static HsumBootUserInitializer createInstance(ActivityManagerService activityManagerService, ContentResolver contentResolver, boolean z) {
        if (UserManager.isHeadlessSystemUserMode()) {
            return new HsumBootUserInitializer((UserManagerInternal) LocalServices.getService(UserManagerInternal.class), activityManagerService, contentResolver, z);
        }
        return null;
    }

    public HsumBootUserInitializer(UserManagerInternal userManagerInternal, ActivityManagerService activityManagerService, ContentResolver contentResolver, boolean z) {
        this.mUmi = userManagerInternal;
        this.mAms = activityManagerService;
        this.mContentResolver = contentResolver;
        this.mShouldAlwaysHaveMainUser = z;
    }

    public void init(TimingsTraceAndSlog timingsTraceAndSlog) {
        Slogf.m22i(TAG, "init())");
        if (this.mShouldAlwaysHaveMainUser) {
            timingsTraceAndSlog.traceBegin("createMainUserIfNeeded");
            createMainUserIfNeeded();
            timingsTraceAndSlog.traceEnd();
        }
    }

    public final void createMainUserIfNeeded() {
        int mainUserId = this.mUmi.getMainUserId();
        if (mainUserId != -10000) {
            Slogf.m28d(TAG, "Found existing MainUser, userId=%d", Integer.valueOf(mainUserId));
            return;
        }
        String str = TAG;
        Slogf.m30d(str, "Creating a new MainUser");
        try {
            Slogf.m20i(str, "Successfully created MainUser, userId=%d", Integer.valueOf(this.mUmi.createUserEvenWhenDisallowed(null, "android.os.usertype.full.SECONDARY", 16386, null, null).id));
        } catch (UserManager.CheckedUserOperationException e) {
            Slogf.wtf(TAG, "Initial bootable MainUser creation failed", (Throwable) e);
        }
    }

    public void systemRunning(TimingsTraceAndSlog timingsTraceAndSlog) {
        observeDeviceProvisioning();
        unlockSystemUser(timingsTraceAndSlog);
        try {
            timingsTraceAndSlog.traceBegin("getBootUser");
            int bootUser = this.mUmi.getBootUser();
            timingsTraceAndSlog.traceEnd();
            timingsTraceAndSlog.traceBegin("switchToBootUser-" + bootUser);
            switchToBootUser(bootUser);
            timingsTraceAndSlog.traceEnd();
        } catch (UserManager.CheckedUserOperationException unused) {
            Slogf.wtf(TAG, "Failed to switch to boot user since there isn't one.");
        }
    }

    public final void observeDeviceProvisioning() {
        if (isDeviceProvisioned()) {
            return;
        }
        this.mContentResolver.registerContentObserver(Settings.Global.getUriFor("device_provisioned"), false, this.mDeviceProvisionedObserver);
    }

    public final boolean isDeviceProvisioned() {
        try {
            return Settings.Global.getInt(this.mContentResolver, "device_provisioned") == 1;
        } catch (Exception e) {
            Slogf.wtf(TAG, "DEVICE_PROVISIONED setting not found.", e);
            return false;
        }
    }

    public final void unlockSystemUser(TimingsTraceAndSlog timingsTraceAndSlog) {
        String str = TAG;
        Slogf.m22i(str, "Unlocking system user");
        timingsTraceAndSlog.traceBegin("unlock-system-user");
        try {
            timingsTraceAndSlog.traceBegin("am.startUser");
            boolean startUserInBackgroundWithListener = this.mAms.startUserInBackgroundWithListener(0, null);
            timingsTraceAndSlog.traceEnd();
            if (!startUserInBackgroundWithListener) {
                Slogf.m14w(str, "could not restart system user in background; trying unlock instead");
                timingsTraceAndSlog.traceBegin("am.unlockUser");
                boolean unlockUser = this.mAms.unlockUser(0, null, null, null);
                timingsTraceAndSlog.traceEnd();
                if (!unlockUser) {
                    Slogf.m14w(str, "could not unlock system user either");
                }
            }
        } finally {
            timingsTraceAndSlog.traceEnd();
        }
    }

    public final void switchToBootUser(int i) {
        String str = TAG;
        Slogf.m20i(str, "Switching to boot user %d", Integer.valueOf(i));
        if (this.mAms.startUserInForegroundWithListener(i, null)) {
            return;
        }
        Slogf.wtf(str, "Failed to start user %d in foreground", Integer.valueOf(i));
    }
}
