package com.android.server.broadcastradio;

import android.app.ActivityManager;
import android.os.Binder;
/* loaded from: classes.dex */
public final class RadioServiceUserController {
    public static boolean isCurrentOrSystemUser() {
        int identifier = Binder.getCallingUserHandle().getIdentifier();
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            if (identifier == ActivityManager.getCurrentUser() || identifier == 0) {
                Binder.restoreCallingIdentity(clearCallingIdentity);
                return true;
            }
            return false;
        } catch (RuntimeException unused) {
            return false;
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }
}
