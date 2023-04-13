package com.android.server.usage;

import android.app.usage.AppStandbyInfo;
import android.content.Context;
import android.util.IndentingPrintWriter;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Set;
/* loaded from: classes5.dex */
public interface AppStandbyInternal {
    void addActiveDeviceAdmin(String str, int i);

    void addListener(AppIdleStateChangeListener appIdleStateChangeListener);

    void clearCarrierPrivilegedApps();

    void clearLastUsedTimestampsForTest(String str, int i);

    void dumpState(String[] strArr, PrintWriter printWriter);

    void dumpUsers(IndentingPrintWriter indentingPrintWriter, int[] iArr, List<String> list);

    void flushToDisk();

    int getAppId(String str);

    int getAppMinStandbyBucket(String str, int i, int i2, boolean z);

    int getAppStandbyBucket(String str, int i, long j, boolean z);

    int getAppStandbyBucketReason(String str, int i, long j);

    List<AppStandbyInfo> getAppStandbyBuckets(int i);

    String getAppStandbyConstant(String str);

    List<String> getBroadcastResponseExemptedPermissions();

    List<String> getBroadcastResponseExemptedRoles();

    int getBroadcastResponseFgThresholdState();

    long getBroadcastResponseWindowDurationMs();

    long getBroadcastSessionsDurationMs();

    long getBroadcastSessionsWithResponseDurationMs();

    long getEstimatedLaunchTime(String str, int i);

    int[] getIdleUidsForUser(int i);

    long getTimeSinceLastJobRun(String str, int i);

    long getTimeSinceLastUsedByUser(String str, int i);

    void initializeDefaultsForSystemApps(int i);

    boolean isActiveDeviceAdmin(String str, int i);

    boolean isAppIdleEnabled();

    boolean isAppIdleFiltered(String str, int i, int i2, long j);

    boolean isAppIdleFiltered(String str, int i, long j, boolean z);

    boolean isInParole();

    void maybeUnrestrictApp(String str, int i, int i2, int i3, int i4, int i5);

    void onAdminDataAvailable();

    void onBootPhase(int i);

    void onUserRemoved(int i);

    void postCheckIdleStates(int i);

    void postOneTimeCheckIdleStates();

    void postReportContentProviderUsage(String str, String str2, int i);

    void postReportExemptedSyncStart(String str, int i);

    void postReportSyncScheduled(String str, int i, boolean z);

    void removeListener(AppIdleStateChangeListener appIdleStateChangeListener);

    void restoreAppsToRare(Set<String> set, int i);

    void restrictApp(String str, int i, int i2);

    void restrictApp(String str, int i, int i2, int i3);

    void setActiveAdminApps(Set<String> set, int i);

    void setAppIdleAsync(String str, boolean z, int i);

    void setAppStandbyBucket(String str, int i, int i2, int i3, int i4);

    void setAppStandbyBuckets(List<AppStandbyInfo> list, int i, int i2, int i3);

    void setEstimatedLaunchTime(String str, int i, long j);

    void setLastJobRunTime(String str, int i, long j);

    boolean shouldNoteResponseEventForAllBroadcastSessions();

    static AppStandbyInternal newAppStandbyController(ClassLoader loader, Context context) {
        try {
            Class<?> clazz = Class.forName("com.android.server.usage.AppStandbyController", true, loader);
            Constructor<?> ctor = clazz.getConstructor(Context.class);
            return (AppStandbyInternal) ctor.newInstance(context);
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
            throw new RuntimeException("Unable to instantiate AppStandbyController!", e);
        }
    }

    /* loaded from: classes5.dex */
    public static abstract class AppIdleStateChangeListener {
        public abstract void onAppIdleStateChanged(String str, int i, boolean z, int i2, int i3);

        public void onParoleStateChanged(boolean isParoleOn) {
        }

        public void onUserInteractionStarted(String packageName, int userId) {
        }

        public void triggerTemporaryQuotaBump(String packageName, int userId) {
        }
    }
}
