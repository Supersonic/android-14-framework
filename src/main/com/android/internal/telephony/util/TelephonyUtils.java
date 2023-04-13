package com.android.internal.telephony.util;

import android.Manifest;
import android.app.ActivityManager;
import android.app.PendingIntent$$ExternalSyntheticLambda1;
import android.content.Context;
import android.content.p001pm.ComponentInfo;
import android.content.p001pm.ResolveInfo;
import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.p008os.Binder;
import android.p008os.Build;
import android.p008os.Bundle;
import android.p008os.PersistableBundle;
import android.p008os.RemoteException;
import android.p008os.SystemProperties;
import android.p008os.UserHandle;
import android.p008os.UserManager;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyFrameworkInitializer;
import android.util.Log;
import com.android.internal.telephony.ITelephony;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
/* loaded from: classes3.dex */
public final class TelephonyUtils {
    public static final Executor DIRECT_EXECUTOR;
    public static boolean IS_DEBUGGABLE = false;
    public static boolean IS_USER = "user".equals(Build.TYPE);
    private static final String LOG_TAG = "TelephonyUtils";

    static {
        IS_DEBUGGABLE = SystemProperties.getInt("ro.debuggable", 0) == 1;
        DIRECT_EXECUTOR = new PendingIntent$$ExternalSyntheticLambda1();
    }

    public static boolean checkDumpPermission(Context context, String tag, PrintWriter pw) {
        if (context.checkCallingOrSelfPermission(Manifest.C0000permission.DUMP) != 0) {
            pw.println("Permission Denial: can't dump " + tag + " from from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid() + " due to missing android.permission.DUMP permission");
            return false;
        }
        return true;
    }

    public static String emptyIfNull(String str) {
        return str == null ? "" : str;
    }

    public static <T> List<T> emptyIfNull(List<T> cur) {
        return cur == null ? Collections.emptyList() : cur;
    }

    public static ComponentInfo getComponentInfo(ResolveInfo resolveInfo) {
        if (resolveInfo.activityInfo != null) {
            return resolveInfo.activityInfo;
        }
        if (resolveInfo.serviceInfo != null) {
            return resolveInfo.serviceInfo;
        }
        if (resolveInfo.providerInfo != null) {
            return resolveInfo.providerInfo;
        }
        throw new IllegalStateException("Missing ComponentInfo!");
    }

    public static void runWithCleanCallingIdentity(Runnable action) {
        long callingIdentity = Binder.clearCallingIdentity();
        try {
            action.run();
        } finally {
            Binder.restoreCallingIdentity(callingIdentity);
        }
    }

    public static void runWithCleanCallingIdentity(final Runnable action, Executor executor) {
        if (action != null) {
            if (executor != null) {
                executor.execute(new Runnable() { // from class: com.android.internal.telephony.util.TelephonyUtils$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        TelephonyUtils.runWithCleanCallingIdentity(action);
                    }
                });
            } else {
                runWithCleanCallingIdentity(action);
            }
        }
    }

    public static <T> T runWithCleanCallingIdentity(Supplier<T> action) {
        long callingIdentity = Binder.clearCallingIdentity();
        try {
            return action.get();
        } finally {
            Binder.restoreCallingIdentity(callingIdentity);
        }
    }

    public static Bundle filterValues(Bundle bundle) {
        Bundle ret = new Bundle(bundle);
        for (String key : bundle.keySet()) {
            Object value = bundle.get(key);
            if (!(value instanceof Integer) && !(value instanceof Long) && !(value instanceof Double) && !(value instanceof String) && !(value instanceof int[]) && !(value instanceof long[]) && !(value instanceof double[]) && !(value instanceof String[]) && !(value instanceof PersistableBundle) && value != null && !(value instanceof Boolean) && !(value instanceof boolean[])) {
                if (value instanceof Bundle) {
                    ret.putBundle(key, filterValues((Bundle) value));
                } else if (!value.getClass().getName().startsWith("android.")) {
                    ret.remove(key);
                }
            }
        }
        return ret;
    }

    public static void waitUntilReady(CountDownLatch latch, long timeoutMs) {
        try {
            latch.await(timeoutMs, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
        }
    }

    public static String dataStateToString(int state) {
        switch (state) {
            case -1:
                return "UNKNOWN";
            case 0:
                return "DISCONNECTED";
            case 1:
                return "CONNECTING";
            case 2:
                return "CONNECTED";
            case 3:
                return "SUSPENDED";
            case 4:
                return "DISCONNECTING";
            case 5:
                return "HANDOVERINPROGRESS";
            default:
                return "UNKNOWN(" + state + NavigationBarInflaterView.KEY_CODE_END;
        }
    }

    public static String mobileDataPolicyToString(int mobileDataPolicy) {
        switch (mobileDataPolicy) {
            case 1:
                return "DATA_ON_NON_DEFAULT_DURING_VOICE_CALL";
            case 2:
                return "MMS_ALWAYS_ALLOWED";
            case 3:
                return "AUTO_DATA_SWITCH";
            default:
                return "UNKNOWN(" + mobileDataPolicy + NavigationBarInflaterView.KEY_CODE_END;
        }
    }

    public static UserHandle getSubscriptionUserHandle(Context context, int subId) {
        SubscriptionManager subManager = (SubscriptionManager) context.getSystemService(SubscriptionManager.class);
        if (subManager == null || !SubscriptionManager.isValidSubscriptionId(subId)) {
            return null;
        }
        UserHandle userHandle = subManager.getSubscriptionUserHandle(subId);
        return userHandle;
    }

    public static void showErrorIfSubscriptionAssociatedWithManagedProfile(Context context, int subId) {
        ITelephony iTelephony;
        long token = Binder.clearCallingIdentity();
        try {
            SubscriptionManager subscriptionManager = (SubscriptionManager) context.getSystemService(SubscriptionManager.class);
            UserHandle associatedUserHandle = subscriptionManager.getSubscriptionUserHandle(subId);
            UserManager um = (UserManager) context.getSystemService(UserManager.class);
            if (associatedUserHandle != null && um.isManagedProfile(associatedUserHandle.getIdentifier()) && (iTelephony = ITelephony.Stub.asInterface(TelephonyFrameworkInitializer.getTelephonyServiceManager().getTelephonyServiceRegisterer().get())) != null) {
                try {
                    iTelephony.showSwitchToManagedProfileDialog();
                } catch (RemoteException e) {
                    Log.m110e(LOG_TAG, "Failed to launch switch to managed profile dialog.");
                }
            }
        } finally {
            Binder.restoreCallingIdentity(token);
        }
    }

    public static boolean isUidForeground(Context context, int uid) {
        boolean result;
        long token = Binder.clearCallingIdentity();
        try {
            ActivityManager am = (ActivityManager) context.getSystemService(ActivityManager.class);
            if (am != null) {
                if (am.getUidImportance(uid) == 100) {
                    result = true;
                    return result;
                }
            }
            result = false;
            return result;
        } finally {
            Binder.restoreCallingIdentity(token);
        }
    }
}
