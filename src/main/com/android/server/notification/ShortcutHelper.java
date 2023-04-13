package com.android.server.notification;

import android.content.IntentFilter;
import android.content.pm.LauncherApps;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutServiceInternal;
import android.os.Binder;
import android.os.Handler;
import android.os.UserHandle;
import android.os.UserManager;
import android.text.TextUtils;
import android.util.Slog;
import com.android.internal.annotations.VisibleForTesting;
import com.android.server.p011pm.PackageManagerShellCommandDataLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
/* loaded from: classes2.dex */
public class ShortcutHelper {
    public static final IntentFilter SHARING_FILTER;
    public HashMap<String, HashMap<String, String>> mActiveShortcutBubbles = new HashMap<>();
    public final LauncherApps.Callback mLauncherAppsCallback = new LauncherApps.Callback() { // from class: com.android.server.notification.ShortcutHelper.1
        @Override // android.content.pm.LauncherApps.Callback
        public void onPackageAdded(String str, UserHandle userHandle) {
        }

        @Override // android.content.pm.LauncherApps.Callback
        public void onPackageChanged(String str, UserHandle userHandle) {
        }

        @Override // android.content.pm.LauncherApps.Callback
        public void onPackageRemoved(String str, UserHandle userHandle) {
        }

        @Override // android.content.pm.LauncherApps.Callback
        public void onPackagesAvailable(String[] strArr, UserHandle userHandle, boolean z) {
        }

        @Override // android.content.pm.LauncherApps.Callback
        public void onPackagesUnavailable(String[] strArr, UserHandle userHandle, boolean z) {
        }

        @Override // android.content.pm.LauncherApps.Callback
        public void onShortcutsChanged(String str, List<ShortcutInfo> list, UserHandle userHandle) {
            boolean z;
            HashMap hashMap = (HashMap) ShortcutHelper.this.mActiveShortcutBubbles.get(str);
            ArrayList arrayList = new ArrayList();
            if (hashMap != null) {
                for (String str2 : new HashSet(hashMap.keySet())) {
                    int i = 0;
                    while (true) {
                        if (i >= list.size()) {
                            z = false;
                            break;
                        } else if (list.get(i).getId().equals(str2)) {
                            z = true;
                            break;
                        } else {
                            i++;
                        }
                    }
                    if (!z) {
                        arrayList.add((String) hashMap.get(str2));
                        hashMap.remove(str2);
                        if (hashMap.isEmpty()) {
                            ShortcutHelper.this.mActiveShortcutBubbles.remove(str);
                            if (ShortcutHelper.this.mLauncherAppsCallbackRegistered && ShortcutHelper.this.mActiveShortcutBubbles.isEmpty()) {
                                ShortcutHelper.this.mLauncherAppsService.unregisterCallback(ShortcutHelper.this.mLauncherAppsCallback);
                                ShortcutHelper.this.mLauncherAppsCallbackRegistered = false;
                            }
                        }
                    }
                }
            }
            for (int i2 = 0; i2 < arrayList.size(); i2++) {
                String str3 = (String) arrayList.get(i2);
                if (ShortcutHelper.this.mShortcutListener != null) {
                    ShortcutHelper.this.mShortcutListener.onShortcutRemoved(str3);
                }
            }
        }
    };
    public boolean mLauncherAppsCallbackRegistered;
    public LauncherApps mLauncherAppsService;
    public ShortcutListener mShortcutListener;
    public ShortcutServiceInternal mShortcutServiceInternal;
    public UserManager mUserManager;

    /* loaded from: classes2.dex */
    public interface ShortcutListener {
        void onShortcutRemoved(String str);
    }

    static {
        IntentFilter intentFilter = new IntentFilter();
        SHARING_FILTER = intentFilter;
        try {
            intentFilter.addDataType("*/*");
        } catch (IntentFilter.MalformedMimeTypeException e) {
            Slog.e("ShortcutHelper", "Bad mime type", e);
        }
    }

    public ShortcutHelper(LauncherApps launcherApps, ShortcutListener shortcutListener, ShortcutServiceInternal shortcutServiceInternal, UserManager userManager) {
        this.mLauncherAppsService = launcherApps;
        this.mShortcutListener = shortcutListener;
        this.mShortcutServiceInternal = shortcutServiceInternal;
        this.mUserManager = userManager;
    }

    @VisibleForTesting
    public void setLauncherApps(LauncherApps launcherApps) {
        this.mLauncherAppsService = launcherApps;
    }

    @VisibleForTesting
    public void setShortcutServiceInternal(ShortcutServiceInternal shortcutServiceInternal) {
        this.mShortcutServiceInternal = shortcutServiceInternal;
    }

    @VisibleForTesting
    public void setUserManager(UserManager userManager) {
        this.mUserManager = userManager;
    }

    public static boolean isConversationShortcut(ShortcutInfo shortcutInfo, ShortcutServiceInternal shortcutServiceInternal, int i) {
        return shortcutInfo != null && shortcutInfo.isLongLived() && shortcutInfo.isEnabled();
    }

    public ShortcutInfo getValidShortcutInfo(String str, String str2, UserHandle userHandle) {
        if (this.mLauncherAppsService != null && this.mUserManager.isUserUnlocked(userHandle)) {
            long clearCallingIdentity = Binder.clearCallingIdentity();
            if (str != null && str2 != null && userHandle != null) {
                try {
                    LauncherApps.ShortcutQuery shortcutQuery = new LauncherApps.ShortcutQuery();
                    shortcutQuery.setPackage(str2);
                    shortcutQuery.setShortcutIds(Arrays.asList(str));
                    shortcutQuery.setQueryFlags(3089);
                    List<ShortcutInfo> shortcuts = this.mLauncherAppsService.getShortcuts(shortcutQuery, userHandle);
                    ShortcutInfo shortcutInfo = (shortcuts == null || shortcuts.size() <= 0) ? null : shortcuts.get(0);
                    if (isConversationShortcut(shortcutInfo, this.mShortcutServiceInternal, userHandle.getIdentifier())) {
                        return shortcutInfo;
                    }
                    return null;
                } finally {
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                }
            }
        }
        return null;
    }

    public void cacheShortcut(ShortcutInfo shortcutInfo, UserHandle userHandle) {
        if (!shortcutInfo.isLongLived() || shortcutInfo.isCached()) {
            return;
        }
        this.mShortcutServiceInternal.cacheShortcuts(userHandle.getIdentifier(), PackageManagerShellCommandDataLoader.PACKAGE, shortcutInfo.getPackage(), Collections.singletonList(shortcutInfo.getId()), shortcutInfo.getUserId(), 16384);
    }

    public void maybeListenForShortcutChangesForBubbles(NotificationRecord notificationRecord, boolean z, Handler handler) {
        String shortcutId = notificationRecord.getNotification().getBubbleMetadata() != null ? notificationRecord.getNotification().getBubbleMetadata().getShortcutId() : null;
        if (!z && !TextUtils.isEmpty(shortcutId) && notificationRecord.getShortcutInfo() != null && notificationRecord.getShortcutInfo().getId().equals(shortcutId)) {
            HashMap<String, String> hashMap = this.mActiveShortcutBubbles.get(notificationRecord.getSbn().getPackageName());
            if (hashMap == null) {
                hashMap = new HashMap<>();
            }
            hashMap.put(shortcutId, notificationRecord.getKey());
            this.mActiveShortcutBubbles.put(notificationRecord.getSbn().getPackageName(), hashMap);
            if (this.mLauncherAppsCallbackRegistered) {
                return;
            }
            this.mLauncherAppsService.registerCallback(this.mLauncherAppsCallback, handler);
            this.mLauncherAppsCallbackRegistered = true;
            return;
        }
        HashMap<String, String> hashMap2 = this.mActiveShortcutBubbles.get(notificationRecord.getSbn().getPackageName());
        if (hashMap2 != null) {
            if (!TextUtils.isEmpty(shortcutId)) {
                hashMap2.remove(shortcutId);
            } else {
                for (String str : new HashSet(hashMap2.keySet())) {
                    if (notificationRecord.getKey().equals(hashMap2.get(str))) {
                        hashMap2.remove(str);
                    }
                }
            }
            if (hashMap2.isEmpty()) {
                this.mActiveShortcutBubbles.remove(notificationRecord.getSbn().getPackageName());
            }
        }
        if (this.mLauncherAppsCallbackRegistered && this.mActiveShortcutBubbles.isEmpty()) {
            this.mLauncherAppsService.unregisterCallback(this.mLauncherAppsCallback);
            this.mLauncherAppsCallbackRegistered = false;
        }
    }
}
