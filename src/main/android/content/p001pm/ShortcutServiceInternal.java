package android.content.p001pm;

import android.appwidget.AppWidgetProviderInfo;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.LocusId;
import android.content.p001pm.LauncherApps;
import android.content.p001pm.ShortcutManager;
import android.p008os.Bundle;
import android.p008os.ParcelFileDescriptor;
import com.android.internal.infra.AndroidFuture;
import java.util.List;
/* renamed from: android.content.pm.ShortcutServiceInternal */
/* loaded from: classes.dex */
public abstract class ShortcutServiceInternal {

    /* renamed from: android.content.pm.ShortcutServiceInternal$ShortcutChangeListener */
    /* loaded from: classes.dex */
    public interface ShortcutChangeListener {
        void onShortcutChanged(String str, int i);
    }

    public abstract void addListener(ShortcutChangeListener shortcutChangeListener);

    public abstract void addShortcutChangeCallback(LauncherApps.ShortcutChangeCallback shortcutChangeCallback);

    public abstract void cacheShortcuts(int i, String str, String str2, List<String> list, int i2, int i3);

    public abstract Intent[] createShortcutIntents(int i, String str, String str2, String str3, int i2, int i3, int i4);

    public abstract void createShortcutIntentsAsync(int i, String str, String str2, String str3, int i2, int i3, int i4, AndroidFuture<Intent[]> androidFuture);

    public abstract List<ShortcutManager.ShareShortcutInfo> getShareTargets(String str, IntentFilter intentFilter, int i);

    public abstract ParcelFileDescriptor getShortcutIconFd(int i, String str, String str2, String str3, int i2);

    public abstract void getShortcutIconFdAsync(int i, String str, String str2, String str3, int i2, AndroidFuture<ParcelFileDescriptor> androidFuture);

    public abstract int getShortcutIconResId(int i, String str, String str2, String str3, int i2);

    public abstract String getShortcutIconUri(int i, String str, String str2, String str3, int i2);

    public abstract void getShortcutIconUriAsync(int i, String str, String str2, String str3, int i2, AndroidFuture<String> androidFuture);

    public abstract String getShortcutStartingThemeResName(int i, String str, String str2, String str3, int i2);

    public abstract List<ShortcutInfo> getShortcuts(int i, String str, long j, String str2, List<String> list, List<LocusId> list2, ComponentName componentName, int i2, int i3, int i4, int i5);

    public abstract void getShortcutsAsync(int i, String str, long j, String str2, List<String> list, List<LocusId> list2, ComponentName componentName, int i2, int i3, int i4, int i5, AndroidFuture<List<ShortcutInfo>> androidFuture);

    public abstract boolean hasShortcutHostPermission(int i, String str, int i2, int i3);

    public abstract boolean isForegroundDefaultLauncher(String str, int i);

    public abstract boolean isPinnedByCaller(int i, String str, String str2, String str3, int i2);

    public abstract boolean isRequestPinItemSupported(int i, int i2);

    public abstract boolean isSharingShortcut(int i, String str, String str2, String str3, int i2, IntentFilter intentFilter);

    public abstract void pinShortcuts(int i, String str, String str2, List<String> list, int i2);

    public abstract boolean requestPinAppWidget(String str, AppWidgetProviderInfo appWidgetProviderInfo, Bundle bundle, IntentSender intentSender, int i);

    public abstract void setShortcutHostPackage(String str, String str2, int i);

    public abstract void uncacheShortcuts(int i, String str, String str2, List<String> list, int i2, int i3);
}
