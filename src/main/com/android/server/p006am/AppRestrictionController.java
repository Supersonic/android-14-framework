package com.android.server.p006am;

import android.app.ActivityManager;
import android.app.ActivityManagerInternal;
import android.app.AppOpsManager;
import android.app.IActivityManager;
import android.app.IUidObserver;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.role.OnRoleHoldersChangedListener;
import android.app.role.RoleManager;
import android.app.usage.AppStandbyInfo;
import android.app.usage.UsageStatsManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.p000pm.PackageManagerInternal;
import android.content.pm.ApplicationInfo;
import android.content.pm.ModuleInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerExecutor;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.PowerExemptionManager;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.UserHandle;
import android.os.UserManager;
import android.provider.DeviceConfig;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.ArraySet;
import android.util.AtomicFile;
import android.util.Pair;
import android.util.Slog;
import android.util.SparseArray;
import android.util.SparseArrayMap;
import android.util.TimeUtils;
import android.util.Xml;
import android.util.proto.ProtoOutputStream;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.notification.SystemNotificationChannels;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.FrameworkStatsLog;
import com.android.internal.util.function.TriConsumer;
import com.android.modules.utils.TypedXmlPullParser;
import com.android.modules.utils.TypedXmlSerializer;
import com.android.server.AppStateTracker;
import com.android.server.LocalServices;
import com.android.server.SystemConfig;
import com.android.server.apphibernation.AppHibernationManagerInternal;
import com.android.server.p006am.AppBatteryTracker;
import com.android.server.p006am.AppRestrictionController;
import com.android.server.p011pm.PackageManagerShellCommandDataLoader;
import com.android.server.p011pm.UserManagerInternal;
import com.android.server.usage.AppStandbyInternal;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.ToIntFunction;
import org.xmlpull.v1.XmlPullParserException;
/* renamed from: com.android.server.am.AppRestrictionController */
/* loaded from: classes.dex */
public final class AppRestrictionController {
    public static final String[] ROLES_IN_INTEREST = {"android.app.role.DIALER", "android.app.role.EMERGENCY"};
    @GuardedBy({"mSettingsLock"})
    public final SparseArrayMap<String, Runnable> mActiveUids;
    public final ActivityManagerService mActivityManagerService;
    public final AppStandbyInternal.AppIdleStateChangeListener mAppIdleStateChangeListener;
    public final ArrayList<BaseAppStateTracker> mAppStateTrackers;
    public final AppStateTracker.BackgroundRestrictedAppListener mBackgroundRestrictionListener;
    public final HandlerExecutor mBgExecutor;
    public final BgHandler mBgHandler;
    public final HandlerThread mBgHandlerThread;
    public ArraySet<String> mBgRestrictionExemptioFromSysConfig;
    public final BroadcastReceiver mBootReceiver;
    public final BroadcastReceiver mBroadcastReceiver;
    @GuardedBy({"mCarrierPrivilegedLock"})
    public List<String> mCarrierPrivilegedApps;
    public final Object mCarrierPrivilegedLock;
    public final ConstantsObserver mConstantsObserver;
    public final Context mContext;
    public int[] mDeviceIdleAllowlist;
    public int[] mDeviceIdleExceptIdleAllowlist;
    public final TrackerInfo mEmptyTrackerInfo;
    public final Injector mInjector;
    public final Object mLock;
    public final NotificationHelper mNotificationHelper;
    public final CopyOnWriteArraySet<ActivityManagerInternal.AppBackgroundRestrictionListener> mRestrictionListeners;
    @GuardedBy({"mSettingsLock"})
    @VisibleForTesting
    final RestrictionSettings mRestrictionSettings;
    public final AtomicBoolean mRestrictionSettingsXmlLoaded;
    public final OnRoleHoldersChangedListener mRoleHolderChangedListener;
    public final Object mSettingsLock;
    public final ArraySet<Integer> mSystemDeviceIdleAllowlist;
    public final ArraySet<Integer> mSystemDeviceIdleExceptIdleAllowlist;
    @GuardedBy({"mLock"})
    public final HashMap<String, Boolean> mSystemModulesCache;
    public final ArrayList<Runnable> mTmpRunnables;
    public final IUidObserver mUidObserver;
    @GuardedBy({"mLock"})
    public final SparseArray<ArrayList<String>> mUidRolesMapping;

    /* renamed from: com.android.server.am.AppRestrictionController$UidBatteryUsageProvider */
    /* loaded from: classes.dex */
    public interface UidBatteryUsageProvider {
        AppBatteryTracker.ImmutableBatteryUsage getUidBatteryUsage(int i);
    }

    public static int standbyBucketToRestrictionLevel(int i) {
        if (i != 5) {
            if (i != 10 && i != 20 && i != 30) {
                int i2 = 40;
                if (i != 40) {
                    if (i != 45) {
                        i2 = 50;
                        if (i != 50) {
                            return 0;
                        }
                    }
                    return i2;
                }
            }
            return 30;
        }
        return 20;
    }

    public final int getOptimizationLevelStatsd(int i) {
        if (i != 10) {
            if (i != 30) {
                return i != 50 ? 0 : 2;
            }
            return 1;
        }
        return 3;
    }

    public final int getRestrictionLevelStatsd(int i) {
        if (i != 10) {
            if (i != 20) {
                if (i != 30) {
                    if (i != 40) {
                        if (i != 50) {
                            return i != 60 ? 0 : 6;
                        }
                        return 5;
                    }
                    return 4;
                }
                return 3;
            }
            return 2;
        }
        return 1;
    }

    public final int getThresholdStatsd(int i) {
        if (i != 1024) {
            return i != 1536 ? 0 : 1;
        }
        return 2;
    }

    public final int getTrackerTypeStatsd(int i) {
        switch (i) {
            case 1:
                return 1;
            case 2:
                return 2;
            case 3:
                return 3;
            case 4:
                return 4;
            case 5:
                return 5;
            case 6:
                return 6;
            case 7:
                return 7;
            default:
                return 0;
        }
    }

    /* renamed from: com.android.server.am.AppRestrictionController$RestrictionSettings */
    /* loaded from: classes.dex */
    public final class RestrictionSettings {
        @GuardedBy({"mSettingsLock"})
        public final SparseArrayMap<String, PkgSettings> mRestrictionLevels = new SparseArrayMap<>();

        public RestrictionSettings() {
        }

        /* renamed from: com.android.server.am.AppRestrictionController$RestrictionSettings$PkgSettings */
        /* loaded from: classes.dex */
        public final class PkgSettings {
            public long[] mLastNotificationShownTime;
            public long mLevelChangeTime;
            public int[] mNotificationId;
            public final String mPackageName;
            public int mReason;
            public final int mUid;
            public int mLastRestrictionLevel = 0;
            public int mCurrentRestrictionLevel = 0;

            public PkgSettings(String str, int i) {
                this.mPackageName = str;
                this.mUid = i;
            }

            @GuardedBy({"mSettingsLock"})
            public int update(int i, int i2, int i3) {
                int i4 = this.mCurrentRestrictionLevel;
                if (i != i4) {
                    this.mLastRestrictionLevel = i4;
                    this.mCurrentRestrictionLevel = i;
                    this.mLevelChangeTime = AppRestrictionController.this.mInjector.currentTimeMillis();
                    this.mReason = (i2 & 65280) | (i3 & 255);
                    AppRestrictionController.this.mBgHandler.obtainMessage(1, this.mUid, i, this.mPackageName).sendToTarget();
                }
                return this.mLastRestrictionLevel;
            }

            @GuardedBy({"mSettingsLock"})
            public String toString() {
                StringBuilder sb = new StringBuilder(128);
                sb.append("RestrictionLevel{");
                sb.append(Integer.toHexString(System.identityHashCode(this)));
                sb.append(':');
                sb.append(this.mPackageName);
                sb.append('/');
                sb.append(UserHandle.formatUid(this.mUid));
                sb.append('}');
                sb.append(' ');
                sb.append(ActivityManager.restrictionLevelToName(this.mCurrentRestrictionLevel));
                sb.append('(');
                sb.append(UsageStatsManager.reasonToString(this.mReason));
                sb.append(')');
                return sb.toString();
            }

            public void dump(PrintWriter printWriter, long j) {
                synchronized (AppRestrictionController.this.mSettingsLock) {
                    printWriter.print(toString());
                    if (this.mLastRestrictionLevel != 0) {
                        printWriter.print('/');
                        printWriter.print(ActivityManager.restrictionLevelToName(this.mLastRestrictionLevel));
                    }
                    printWriter.print(" levelChange=");
                    TimeUtils.formatDuration(this.mLevelChangeTime - j, printWriter);
                    if (this.mLastNotificationShownTime != null) {
                        int i = 0;
                        while (true) {
                            long[] jArr = this.mLastNotificationShownTime;
                            if (i >= jArr.length) {
                                break;
                            }
                            if (jArr[i] > 0) {
                                printWriter.print(" lastNoti(");
                                NotificationHelper unused = AppRestrictionController.this.mNotificationHelper;
                                printWriter.print(NotificationHelper.notificationTypeToString(i));
                                printWriter.print(")=");
                                TimeUtils.formatDuration(this.mLastNotificationShownTime[i] - j, printWriter);
                            }
                            i++;
                        }
                    }
                }
                printWriter.print(" effectiveExemption=");
                printWriter.print(PowerExemptionManager.reasonCodeToString(AppRestrictionController.this.getBackgroundRestrictionExemptionReason(this.mUid)));
            }

            public String getPackageName() {
                return this.mPackageName;
            }

            public int getUid() {
                return this.mUid;
            }

            @GuardedBy({"mSettingsLock"})
            public int getCurrentRestrictionLevel() {
                return this.mCurrentRestrictionLevel;
            }

            @GuardedBy({"mSettingsLock"})
            public int getLastRestrictionLevel() {
                return this.mLastRestrictionLevel;
            }

            @GuardedBy({"mSettingsLock"})
            public int getReason() {
                return this.mReason;
            }

            @GuardedBy({"mSettingsLock"})
            public long getLastNotificationTime(int i) {
                long[] jArr = this.mLastNotificationShownTime;
                if (jArr == null) {
                    return 0L;
                }
                return jArr[i];
            }

            @GuardedBy({"mSettingsLock"})
            public void setLastNotificationTime(int i, long j) {
                setLastNotificationTime(i, j, true);
            }

            @GuardedBy({"mSettingsLock"})
            @VisibleForTesting
            public void setLastNotificationTime(int i, long j, boolean z) {
                if (this.mLastNotificationShownTime == null) {
                    this.mLastNotificationShownTime = new long[2];
                }
                this.mLastNotificationShownTime[i] = j;
                if (z && AppRestrictionController.this.mRestrictionSettingsXmlLoaded.get()) {
                    RestrictionSettings.this.schedulePersistToXml(UserHandle.getUserId(this.mUid));
                }
            }

            @GuardedBy({"mSettingsLock"})
            public int getNotificationId(int i) {
                int[] iArr = this.mNotificationId;
                if (iArr == null) {
                    return 0;
                }
                return iArr[i];
            }

            @GuardedBy({"mSettingsLock"})
            public void setNotificationId(int i, int i2) {
                if (this.mNotificationId == null) {
                    this.mNotificationId = new int[2];
                }
                this.mNotificationId[i] = i2;
            }

            @GuardedBy({"mSettingsLock"})
            @VisibleForTesting
            public void setLevelChangeTime(long j) {
                this.mLevelChangeTime = j;
            }

            @GuardedBy({"mSettingsLock"})
            public Object clone() {
                PkgSettings pkgSettings = new PkgSettings(this.mPackageName, this.mUid);
                pkgSettings.mCurrentRestrictionLevel = this.mCurrentRestrictionLevel;
                pkgSettings.mLastRestrictionLevel = this.mLastRestrictionLevel;
                pkgSettings.mLevelChangeTime = this.mLevelChangeTime;
                pkgSettings.mReason = this.mReason;
                long[] jArr = this.mLastNotificationShownTime;
                if (jArr != null) {
                    pkgSettings.mLastNotificationShownTime = Arrays.copyOf(jArr, jArr.length);
                }
                int[] iArr = this.mNotificationId;
                if (iArr != null) {
                    pkgSettings.mNotificationId = Arrays.copyOf(iArr, iArr.length);
                }
                return pkgSettings;
            }

            @GuardedBy({"mSettingsLock"})
            public boolean equals(Object obj) {
                if (obj == this) {
                    return true;
                }
                if (obj == null || !(obj instanceof PkgSettings)) {
                    return false;
                }
                PkgSettings pkgSettings = (PkgSettings) obj;
                return pkgSettings.mUid == this.mUid && pkgSettings.mCurrentRestrictionLevel == this.mCurrentRestrictionLevel && pkgSettings.mLastRestrictionLevel == this.mLastRestrictionLevel && pkgSettings.mLevelChangeTime == this.mLevelChangeTime && pkgSettings.mReason == this.mReason && TextUtils.equals(pkgSettings.mPackageName, this.mPackageName) && Arrays.equals(pkgSettings.mLastNotificationShownTime, this.mLastNotificationShownTime) && Arrays.equals(pkgSettings.mNotificationId, this.mNotificationId);
            }
        }

        public int update(String str, int i, int i2, int i3, int i4) {
            int update;
            synchronized (AppRestrictionController.this.mSettingsLock) {
                PkgSettings restrictionSettingsLocked = getRestrictionSettingsLocked(i, str);
                if (restrictionSettingsLocked == null) {
                    restrictionSettingsLocked = new PkgSettings(str, i);
                    this.mRestrictionLevels.add(i, str, restrictionSettingsLocked);
                }
                update = restrictionSettingsLocked.update(i2, i3, i4);
            }
            return update;
        }

        public int getReason(String str, int i) {
            int reason;
            synchronized (AppRestrictionController.this.mSettingsLock) {
                PkgSettings pkgSettings = (PkgSettings) this.mRestrictionLevels.get(i, str);
                reason = pkgSettings != null ? pkgSettings.getReason() : 256;
            }
            return reason;
        }

        public int getRestrictionLevel(int i) {
            synchronized (AppRestrictionController.this.mSettingsLock) {
                int indexOfKey = this.mRestrictionLevels.indexOfKey(i);
                if (indexOfKey < 0) {
                    return 0;
                }
                int numElementsForKeyAt = this.mRestrictionLevels.numElementsForKeyAt(indexOfKey);
                if (numElementsForKeyAt == 0) {
                    return 0;
                }
                int i2 = 0;
                for (int i3 = 0; i3 < numElementsForKeyAt; i3++) {
                    PkgSettings pkgSettings = (PkgSettings) this.mRestrictionLevels.valueAt(indexOfKey, i3);
                    if (pkgSettings != null) {
                        int currentRestrictionLevel = pkgSettings.getCurrentRestrictionLevel();
                        if (i2 != 0) {
                            currentRestrictionLevel = Math.min(i2, currentRestrictionLevel);
                        }
                        i2 = currentRestrictionLevel;
                    }
                }
                return i2;
            }
        }

        public int getRestrictionLevel(int i, String str) {
            int restrictionLevel;
            synchronized (AppRestrictionController.this.mSettingsLock) {
                PkgSettings restrictionSettingsLocked = getRestrictionSettingsLocked(i, str);
                restrictionLevel = restrictionSettingsLocked == null ? getRestrictionLevel(i) : restrictionSettingsLocked.getCurrentRestrictionLevel();
            }
            return restrictionLevel;
        }

        public int getRestrictionLevel(String str, int i) {
            return getRestrictionLevel(AppRestrictionController.this.mInjector.getPackageManagerInternal().getPackageUid(str, 819200L, i), str);
        }

        public final int getLastRestrictionLevel(int i, String str) {
            int lastRestrictionLevel;
            synchronized (AppRestrictionController.this.mSettingsLock) {
                PkgSettings pkgSettings = (PkgSettings) this.mRestrictionLevels.get(i, str);
                lastRestrictionLevel = pkgSettings == null ? 0 : pkgSettings.getLastRestrictionLevel();
            }
            return lastRestrictionLevel;
        }

        @GuardedBy({"mSettingsLock"})
        public void forEachPackageInUidLocked(int i, TriConsumer<String, Integer, Integer> triConsumer) {
            int indexOfKey = this.mRestrictionLevels.indexOfKey(i);
            if (indexOfKey < 0) {
                return;
            }
            int numElementsForKeyAt = this.mRestrictionLevels.numElementsForKeyAt(indexOfKey);
            for (int i2 = 0; i2 < numElementsForKeyAt; i2++) {
                PkgSettings pkgSettings = (PkgSettings) this.mRestrictionLevels.valueAt(indexOfKey, i2);
                triConsumer.accept((String) this.mRestrictionLevels.keyAt(indexOfKey, i2), Integer.valueOf(pkgSettings.getCurrentRestrictionLevel()), Integer.valueOf(pkgSettings.getReason()));
            }
        }

        @GuardedBy({"mSettingsLock"})
        public void forEachUidLocked(Consumer<Integer> consumer) {
            for (int numMaps = this.mRestrictionLevels.numMaps() - 1; numMaps >= 0; numMaps--) {
                consumer.accept(Integer.valueOf(this.mRestrictionLevels.keyAt(numMaps)));
            }
        }

        @GuardedBy({"mSettingsLock"})
        public PkgSettings getRestrictionSettingsLocked(int i, String str) {
            return (PkgSettings) this.mRestrictionLevels.get(i, str);
        }

        public void removeUser(int i) {
            synchronized (AppRestrictionController.this.mSettingsLock) {
                for (int numMaps = this.mRestrictionLevels.numMaps() - 1; numMaps >= 0; numMaps--) {
                    if (UserHandle.getUserId(this.mRestrictionLevels.keyAt(numMaps)) == i) {
                        this.mRestrictionLevels.deleteAt(numMaps);
                    }
                }
            }
        }

        public void removePackage(String str, int i) {
            removePackage(str, i, true);
        }

        public void removePackage(String str, int i, boolean z) {
            synchronized (AppRestrictionController.this.mSettingsLock) {
                int indexOfKey = this.mRestrictionLevels.indexOfKey(i);
                this.mRestrictionLevels.delete(i, str);
                if (indexOfKey >= 0 && this.mRestrictionLevels.numElementsForKeyAt(indexOfKey) == 0) {
                    this.mRestrictionLevels.deleteAt(indexOfKey);
                }
            }
            if (z && AppRestrictionController.this.mRestrictionSettingsXmlLoaded.get()) {
                schedulePersistToXml(UserHandle.getUserId(i));
            }
        }

        public void removeUid(int i) {
            removeUid(i, true);
        }

        public void removeUid(int i, boolean z) {
            synchronized (AppRestrictionController.this.mSettingsLock) {
                this.mRestrictionLevels.delete(i);
            }
            if (z && AppRestrictionController.this.mRestrictionSettingsXmlLoaded.get()) {
                schedulePersistToXml(UserHandle.getUserId(i));
            }
        }

        @VisibleForTesting
        public void reset() {
            synchronized (AppRestrictionController.this.mSettingsLock) {
                for (int numMaps = this.mRestrictionLevels.numMaps() - 1; numMaps >= 0; numMaps--) {
                    this.mRestrictionLevels.deleteAt(numMaps);
                }
            }
        }

        @VisibleForTesting
        public void resetToDefault() {
            synchronized (AppRestrictionController.this.mSettingsLock) {
                this.mRestrictionLevels.forEach(new Consumer() { // from class: com.android.server.am.AppRestrictionController$RestrictionSettings$$ExternalSyntheticLambda0
                    @Override // java.util.function.Consumer
                    public final void accept(Object obj) {
                        AppRestrictionController.RestrictionSettings.lambda$resetToDefault$0((AppRestrictionController.RestrictionSettings.PkgSettings) obj);
                    }
                });
            }
        }

        public static /* synthetic */ void lambda$resetToDefault$0(PkgSettings pkgSettings) {
            pkgSettings.mCurrentRestrictionLevel = 0;
            pkgSettings.mLastRestrictionLevel = 0;
            pkgSettings.mLevelChangeTime = 0L;
            pkgSettings.mReason = 256;
            if (pkgSettings.mLastNotificationShownTime != null) {
                for (int i = 0; i < pkgSettings.mLastNotificationShownTime.length; i++) {
                    pkgSettings.mLastNotificationShownTime[i] = 0;
                }
            }
        }

        public void dump(PrintWriter printWriter, String str) {
            final ArrayList arrayList = new ArrayList();
            synchronized (AppRestrictionController.this.mSettingsLock) {
                this.mRestrictionLevels.forEach(new Consumer() { // from class: com.android.server.am.AppRestrictionController$RestrictionSettings$$ExternalSyntheticLambda1
                    @Override // java.util.function.Consumer
                    public final void accept(Object obj) {
                        arrayList.add((AppRestrictionController.RestrictionSettings.PkgSettings) obj);
                    }
                });
            }
            Collections.sort(arrayList, Comparator.comparingInt(new ToIntFunction() { // from class: com.android.server.am.AppRestrictionController$RestrictionSettings$$ExternalSyntheticLambda2
                @Override // java.util.function.ToIntFunction
                public final int applyAsInt(Object obj) {
                    return ((AppRestrictionController.RestrictionSettings.PkgSettings) obj).getUid();
                }
            }));
            long currentTimeMillis = AppRestrictionController.this.mInjector.currentTimeMillis();
            int size = arrayList.size();
            for (int i = 0; i < size; i++) {
                printWriter.print(str);
                printWriter.print('#');
                printWriter.print(i);
                printWriter.print(' ');
                ((PkgSettings) arrayList.get(i)).dump(printWriter, currentTimeMillis);
                printWriter.println();
            }
        }

        @VisibleForTesting
        public void schedulePersistToXml(int i) {
            AppRestrictionController.this.mBgHandler.obtainMessage(11, i, 0).sendToTarget();
        }

        @VisibleForTesting
        public void scheduleLoadFromXml() {
            AppRestrictionController.this.mBgHandler.sendEmptyMessage(10);
        }

        @VisibleForTesting
        public File getXmlFileNameForUser(int i) {
            return new File(new File(AppRestrictionController.this.mInjector.getDataSystemDeDirectory(i), "apprestriction"), "settings.xml");
        }

        @VisibleForTesting
        public void loadFromXml(boolean z) {
            for (int i : AppRestrictionController.this.mInjector.getUserManagerInternal().getUserIds()) {
                loadFromXml(i, z);
            }
            AppRestrictionController.this.mRestrictionSettingsXmlLoaded.set(true);
        }

        public void loadFromXml(int i, boolean z) {
            File xmlFileNameForUser = getXmlFileNameForUser(i);
            if (!xmlFileNameForUser.exists()) {
                return;
            }
            long[] jArr = new long[2];
            try {
                FileInputStream fileInputStream = new FileInputStream(xmlFileNameForUser);
                TypedXmlPullParser resolvePullParser = Xml.resolvePullParser(fileInputStream);
                long elapsedRealtime = SystemClock.elapsedRealtime();
                while (true) {
                    int next = resolvePullParser.next();
                    if (next == 1) {
                        fileInputStream.close();
                        return;
                    } else if (next == 2) {
                        String name = resolvePullParser.getName();
                        if (!"settings".equals(name)) {
                            Slog.w("ActivityManager", "Unexpected tag name: " + name);
                        } else {
                            loadOneFromXml(resolvePullParser, elapsedRealtime, jArr, z);
                        }
                    }
                }
            } catch (IOException | XmlPullParserException unused) {
            }
        }

        public final void loadOneFromXml(TypedXmlPullParser typedXmlPullParser, long j, long[] jArr, boolean z) {
            char c;
            for (int i = 0; i < jArr.length; i++) {
                jArr[i] = 0;
            }
            String str = null;
            int i2 = 256;
            long j2 = 0;
            int i3 = 0;
            int i4 = 0;
            for (int i5 = 0; i5 < typedXmlPullParser.getAttributeCount(); i5++) {
                try {
                    String attributeName = typedXmlPullParser.getAttributeName(i5);
                    String attributeValue = typedXmlPullParser.getAttributeValue(i5);
                    switch (attributeName.hashCode()) {
                        case -934964668:
                            if (attributeName.equals("reason")) {
                                c = 4;
                                break;
                            }
                            c = 65535;
                            break;
                        case -807062458:
                            if (attributeName.equals("package")) {
                                c = 1;
                                break;
                            }
                            c = 65535;
                            break;
                        case 115792:
                            if (attributeName.equals("uid")) {
                                c = 0;
                                break;
                            }
                            c = 65535;
                            break;
                        case 69785859:
                            if (attributeName.equals("levelts")) {
                                c = 3;
                                break;
                            }
                            c = 65535;
                            break;
                        case 569868612:
                            if (attributeName.equals("curlevel")) {
                                c = 2;
                                break;
                            }
                            c = 65535;
                            break;
                        default:
                            c = 65535;
                            break;
                    }
                    if (c == 0) {
                        i3 = Integer.parseInt(attributeValue);
                    } else if (c == 1) {
                        str = attributeValue;
                    } else if (c == 2) {
                        i4 = Integer.parseInt(attributeValue);
                    } else if (c == 3) {
                        j2 = Long.parseLong(attributeValue);
                    } else if (c == 4) {
                        i2 = Integer.parseInt(attributeValue);
                    } else {
                        jArr[NotificationHelper.notificationTimeAttrToType(attributeName)] = Long.parseLong(attributeValue);
                    }
                } catch (IllegalArgumentException unused) {
                }
            }
            if (i3 != 0) {
                synchronized (AppRestrictionController.this.mSettingsLock) {
                    PkgSettings restrictionSettingsLocked = getRestrictionSettingsLocked(i3, str);
                    if (restrictionSettingsLocked == null) {
                        return;
                    }
                    for (int i6 = 0; i6 < jArr.length; i6++) {
                        if (restrictionSettingsLocked.getLastNotificationTime(i6) == 0) {
                            long j3 = jArr[i6];
                            if (j3 != 0) {
                                restrictionSettingsLocked.setLastNotificationTime(i6, j3, false);
                            }
                        }
                    }
                    if (restrictionSettingsLocked.mCurrentRestrictionLevel >= i4) {
                        return;
                    }
                    long j4 = j2;
                    int appStandbyBucket = AppRestrictionController.this.mInjector.getAppStandbyInternal().getAppStandbyBucket(str, UserHandle.getUserId(i3), j, false);
                    if (z) {
                        AppRestrictionController appRestrictionController = AppRestrictionController.this;
                        appRestrictionController.applyRestrictionLevel(str, i3, i4, appRestrictionController.mEmptyTrackerInfo, appStandbyBucket, true, 65280 & i2, i2 & 255);
                    } else {
                        restrictionSettingsLocked.update(i4, 65280 & i2, i2 & 255);
                    }
                    synchronized (AppRestrictionController.this.mSettingsLock) {
                        restrictionSettingsLocked.setLevelChangeTime(j4);
                    }
                }
            }
        }

        @VisibleForTesting
        public void persistToXml(int i) {
            FileOutputStream fileOutputStream;
            File xmlFileNameForUser = getXmlFileNameForUser(i);
            File parentFile = xmlFileNameForUser.getParentFile();
            if (!parentFile.isDirectory() && !parentFile.mkdirs()) {
                Slog.w("ActivityManager", "Failed to create folder for " + i);
                return;
            }
            AtomicFile atomicFile = new AtomicFile(xmlFileNameForUser);
            try {
                fileOutputStream = atomicFile.startWrite();
                try {
                    fileOutputStream.write(toXmlByteArray(i));
                    atomicFile.finishWrite(fileOutputStream);
                } catch (Exception e) {
                    e = e;
                    Slog.e("ActivityManager", "Failed to write file " + xmlFileNameForUser, e);
                    if (fileOutputStream != null) {
                        atomicFile.failWrite(fileOutputStream);
                    }
                }
            } catch (Exception e2) {
                e = e2;
                fileOutputStream = null;
            }
        }

        public final byte[] toXmlByteArray(int i) {
            try {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                TypedXmlSerializer resolveSerializer = Xml.resolveSerializer(byteArrayOutputStream);
                resolveSerializer.startDocument((String) null, Boolean.TRUE);
                synchronized (AppRestrictionController.this.mSettingsLock) {
                    for (int numMaps = this.mRestrictionLevels.numMaps() - 1; numMaps >= 0; numMaps--) {
                        for (int numElementsForKeyAt = this.mRestrictionLevels.numElementsForKeyAt(numMaps) - 1; numElementsForKeyAt >= 0; numElementsForKeyAt--) {
                            PkgSettings pkgSettings = (PkgSettings) this.mRestrictionLevels.valueAt(numMaps, numElementsForKeyAt);
                            int uid = pkgSettings.getUid();
                            if (UserHandle.getUserId(uid) == i) {
                                resolveSerializer.startTag((String) null, "settings");
                                resolveSerializer.attributeInt((String) null, "uid", uid);
                                resolveSerializer.attribute((String) null, "package", pkgSettings.getPackageName());
                                resolveSerializer.attributeInt((String) null, "curlevel", pkgSettings.mCurrentRestrictionLevel);
                                resolveSerializer.attributeLong((String) null, "levelts", pkgSettings.mLevelChangeTime);
                                resolveSerializer.attributeInt((String) null, "reason", pkgSettings.mReason);
                                for (int i2 = 0; i2 < 2; i2++) {
                                    resolveSerializer.attributeLong((String) null, NotificationHelper.notificationTypeToTimeAttr(i2), pkgSettings.getLastNotificationTime(i2));
                                }
                                resolveSerializer.endTag((String) null, "settings");
                            }
                        }
                    }
                }
                resolveSerializer.endDocument();
                resolveSerializer.flush();
                byte[] byteArray = byteArrayOutputStream.toByteArray();
                byteArrayOutputStream.close();
                return byteArray;
            } catch (IOException unused) {
                return null;
            }
        }

        @VisibleForTesting
        public void removeXml() {
            for (int i : AppRestrictionController.this.mInjector.getUserManagerInternal().getUserIds()) {
                getXmlFileNameForUser(i).delete();
            }
        }

        public Object clone() {
            RestrictionSettings restrictionSettings = new RestrictionSettings();
            synchronized (AppRestrictionController.this.mSettingsLock) {
                for (int numMaps = this.mRestrictionLevels.numMaps() - 1; numMaps >= 0; numMaps--) {
                    for (int numElementsForKeyAt = this.mRestrictionLevels.numElementsForKeyAt(numMaps) - 1; numElementsForKeyAt >= 0; numElementsForKeyAt--) {
                        restrictionSettings.mRestrictionLevels.add(this.mRestrictionLevels.keyAt(numMaps), (String) this.mRestrictionLevels.keyAt(numMaps, numElementsForKeyAt), (PkgSettings) ((PkgSettings) this.mRestrictionLevels.valueAt(numMaps, numElementsForKeyAt)).clone());
                    }
                }
            }
            return restrictionSettings;
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (obj == null || !(obj instanceof RestrictionSettings)) {
                return false;
            }
            SparseArrayMap<String, PkgSettings> sparseArrayMap = ((RestrictionSettings) obj).mRestrictionLevels;
            synchronized (AppRestrictionController.this.mSettingsLock) {
                if (sparseArrayMap.numMaps() != this.mRestrictionLevels.numMaps()) {
                    return false;
                }
                for (int numMaps = this.mRestrictionLevels.numMaps() - 1; numMaps >= 0; numMaps--) {
                    int keyAt = this.mRestrictionLevels.keyAt(numMaps);
                    if (sparseArrayMap.numElementsForKey(keyAt) != this.mRestrictionLevels.numElementsForKeyAt(numMaps)) {
                        return false;
                    }
                    for (int numElementsForKeyAt = this.mRestrictionLevels.numElementsForKeyAt(numMaps) - 1; numElementsForKeyAt >= 0; numElementsForKeyAt--) {
                        PkgSettings pkgSettings = (PkgSettings) this.mRestrictionLevels.valueAt(numMaps, numElementsForKeyAt);
                        if (!pkgSettings.equals(sparseArrayMap.get(keyAt, pkgSettings.getPackageName()))) {
                            return false;
                        }
                    }
                }
                return true;
            }
        }
    }

    /* renamed from: com.android.server.am.AppRestrictionController$ConstantsObserver */
    /* loaded from: classes.dex */
    public final class ConstantsObserver extends ContentObserver implements DeviceConfig.OnPropertiesChangedListener {
        public volatile long mBgAbusiveNotificationMinIntervalMs;
        public volatile boolean mBgAutoRestrictAbusiveApps;
        public volatile boolean mBgAutoRestrictedBucket;
        public volatile long mBgLongFgsNotificationMinIntervalMs;
        public volatile boolean mBgPromptAbusiveAppsToBgRestricted;
        public volatile boolean mBgPromptFgsOnLongRunning;
        public volatile boolean mBgPromptFgsWithNotiOnLongRunning;
        public volatile boolean mBgPromptFgsWithNotiToBgRestricted;
        public volatile Set<String> mBgRestrictionExemptedPackages;
        public final boolean mDefaultBgPromptAbusiveAppToBgRestricted;
        public final boolean mDefaultBgPromptFgsWithNotiToBgRestricted;
        public volatile boolean mRestrictedBucketEnabled;

        public ConstantsObserver(Handler handler, Context context) {
            super(handler);
            this.mBgRestrictionExemptedPackages = Collections.emptySet();
            this.mDefaultBgPromptFgsWithNotiToBgRestricted = context.getResources().getBoolean(17891393);
            this.mDefaultBgPromptAbusiveAppToBgRestricted = context.getResources().getBoolean(17891392);
        }

        public void onPropertiesChanged(DeviceConfig.Properties properties) {
            String str;
            Iterator it = properties.getKeyset().iterator();
            while (it.hasNext() && (str = (String) it.next()) != null && str.startsWith("bg_")) {
                char c = 65535;
                switch (str.hashCode()) {
                    case -1918659497:
                        if (str.equals("bg_prompt_abusive_apps_to_bg_restricted")) {
                            c = 0;
                            break;
                        }
                        break;
                    case -1199889595:
                        if (str.equals("bg_auto_restrict_abusive_apps")) {
                            c = 1;
                            break;
                        }
                        break;
                    case -582264882:
                        if (str.equals("bg_prompt_fgs_on_long_running")) {
                            c = 2;
                            break;
                        }
                        break;
                    case -395763044:
                        if (str.equals("bg_auto_restricted_bucket_on_bg_restricted")) {
                            c = 3;
                            break;
                        }
                        break;
                    case -157665503:
                        if (str.equals("bg_restriction_exempted_packages")) {
                            c = 4;
                            break;
                        }
                        break;
                    case 854605367:
                        if (str.equals("bg_abusive_notification_minimal_interval")) {
                            c = 5;
                            break;
                        }
                        break;
                    case 892275457:
                        if (str.equals("bg_long_fgs_notification_minimal_interval")) {
                            c = 6;
                            break;
                        }
                        break;
                    case 1771474142:
                        if (str.equals("bg_prompt_fgs_with_noti_on_long_running")) {
                            c = 7;
                            break;
                        }
                        break;
                    case 1965398671:
                        if (str.equals("bg_prompt_fgs_with_noti_to_bg_restricted")) {
                            c = '\b';
                            break;
                        }
                        break;
                }
                switch (c) {
                    case 0:
                        updateBgPromptAbusiveAppToBgRestricted();
                        break;
                    case 1:
                        updateBgAutoRestrictAbusiveApps();
                        break;
                    case 2:
                        updateBgPromptFgsOnLongRunning();
                        break;
                    case 3:
                        updateBgAutoRestrictedBucketChanged();
                        break;
                    case 4:
                        updateBgRestrictionExemptedPackages();
                        break;
                    case 5:
                        updateBgAbusiveNotificationMinimalInterval();
                        break;
                    case 6:
                        updateBgLongFgsNotificationMinimalInterval();
                        break;
                    case 7:
                        updateBgPromptFgsWithNotiOnLongRunning();
                        break;
                    case '\b':
                        updateBgPromptFgsWithNotiToBgRestricted();
                        break;
                }
                AppRestrictionController.this.onPropertiesChanged(str);
            }
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean z) {
            updateSettings();
        }

        public void start() {
            AppRestrictionController.this.mContext.getContentResolver().registerContentObserver(Settings.Global.getUriFor("enable_restricted_bucket"), false, this);
            updateSettings();
            updateDeviceConfig();
        }

        public void updateSettings() {
            this.mRestrictedBucketEnabled = isRestrictedBucketEnabled();
        }

        public final boolean isRestrictedBucketEnabled() {
            return Settings.Global.getInt(AppRestrictionController.this.mContext.getContentResolver(), "enable_restricted_bucket", 1) == 1;
        }

        public void updateDeviceConfig() {
            updateBgAutoRestrictedBucketChanged();
            updateBgAutoRestrictAbusiveApps();
            updateBgAbusiveNotificationMinimalInterval();
            updateBgLongFgsNotificationMinimalInterval();
            updateBgPromptFgsWithNotiToBgRestricted();
            updateBgPromptFgsWithNotiOnLongRunning();
            updateBgPromptFgsOnLongRunning();
            updateBgPromptAbusiveAppToBgRestricted();
            updateBgRestrictionExemptedPackages();
        }

        public final void updateBgAutoRestrictedBucketChanged() {
            boolean z = this.mBgAutoRestrictedBucket;
            this.mBgAutoRestrictedBucket = DeviceConfig.getBoolean("activity_manager", "bg_auto_restricted_bucket_on_bg_restricted", false);
            if (z != this.mBgAutoRestrictedBucket) {
                AppRestrictionController.this.dispatchAutoRestrictedBucketFeatureFlagChanged(this.mBgAutoRestrictedBucket);
            }
        }

        public final void updateBgAutoRestrictAbusiveApps() {
            this.mBgAutoRestrictAbusiveApps = DeviceConfig.getBoolean("activity_manager", "bg_auto_restrict_abusive_apps", true);
        }

        public final void updateBgAbusiveNotificationMinimalInterval() {
            this.mBgAbusiveNotificationMinIntervalMs = DeviceConfig.getLong("activity_manager", "bg_abusive_notification_minimal_interval", 2592000000L);
        }

        public final void updateBgLongFgsNotificationMinimalInterval() {
            this.mBgLongFgsNotificationMinIntervalMs = DeviceConfig.getLong("activity_manager", "bg_long_fgs_notification_minimal_interval", 2592000000L);
        }

        public final void updateBgPromptFgsWithNotiToBgRestricted() {
            this.mBgPromptFgsWithNotiToBgRestricted = DeviceConfig.getBoolean("activity_manager", "bg_prompt_fgs_with_noti_to_bg_restricted", this.mDefaultBgPromptFgsWithNotiToBgRestricted);
        }

        public final void updateBgPromptFgsWithNotiOnLongRunning() {
            this.mBgPromptFgsWithNotiOnLongRunning = DeviceConfig.getBoolean("activity_manager", "bg_prompt_fgs_with_noti_on_long_running", false);
        }

        public final void updateBgPromptFgsOnLongRunning() {
            this.mBgPromptFgsOnLongRunning = DeviceConfig.getBoolean("activity_manager", "bg_prompt_fgs_on_long_running", true);
        }

        public final void updateBgPromptAbusiveAppToBgRestricted() {
            this.mBgPromptAbusiveAppsToBgRestricted = DeviceConfig.getBoolean("activity_manager", "bg_prompt_abusive_apps_to_bg_restricted", this.mDefaultBgPromptAbusiveAppToBgRestricted);
        }

        public final void updateBgRestrictionExemptedPackages() {
            String string = DeviceConfig.getString("activity_manager", "bg_restriction_exempted_packages", (String) null);
            if (string == null) {
                this.mBgRestrictionExemptedPackages = Collections.emptySet();
                return;
            }
            String[] split = string.split(",");
            ArraySet arraySet = new ArraySet();
            for (String str : split) {
                arraySet.add(str);
            }
            this.mBgRestrictionExemptedPackages = Collections.unmodifiableSet(arraySet);
        }

        public void dump(PrintWriter printWriter, String str) {
            printWriter.print(str);
            printWriter.println("BACKGROUND RESTRICTION POLICY SETTINGS:");
            String str2 = "  " + str;
            printWriter.print(str2);
            printWriter.print("bg_auto_restricted_bucket_on_bg_restricted");
            printWriter.print('=');
            printWriter.println(this.mBgAutoRestrictedBucket);
            printWriter.print(str2);
            printWriter.print("bg_auto_restrict_abusive_apps");
            printWriter.print('=');
            printWriter.println(this.mBgAutoRestrictAbusiveApps);
            printWriter.print(str2);
            printWriter.print("bg_abusive_notification_minimal_interval");
            printWriter.print('=');
            printWriter.println(this.mBgAbusiveNotificationMinIntervalMs);
            printWriter.print(str2);
            printWriter.print("bg_long_fgs_notification_minimal_interval");
            printWriter.print('=');
            printWriter.println(this.mBgLongFgsNotificationMinIntervalMs);
            printWriter.print(str2);
            printWriter.print("bg_prompt_fgs_on_long_running");
            printWriter.print('=');
            printWriter.println(this.mBgPromptFgsOnLongRunning);
            printWriter.print(str2);
            printWriter.print("bg_prompt_fgs_with_noti_on_long_running");
            printWriter.print('=');
            printWriter.println(this.mBgPromptFgsWithNotiOnLongRunning);
            printWriter.print(str2);
            printWriter.print("bg_prompt_fgs_with_noti_to_bg_restricted");
            printWriter.print('=');
            printWriter.println(this.mBgPromptFgsWithNotiToBgRestricted);
            printWriter.print(str2);
            printWriter.print("bg_prompt_abusive_apps_to_bg_restricted");
            printWriter.print('=');
            printWriter.println(this.mBgPromptAbusiveAppsToBgRestricted);
            printWriter.print(str2);
            printWriter.print("bg_restriction_exempted_packages");
            printWriter.print('=');
            printWriter.println(this.mBgRestrictionExemptedPackages.toString());
        }
    }

    /* renamed from: com.android.server.am.AppRestrictionController$TrackerInfo */
    /* loaded from: classes.dex */
    public class TrackerInfo {
        public final byte[] mInfo;
        public final int mType;

        public TrackerInfo() {
            this.mType = 0;
            this.mInfo = null;
        }

        public TrackerInfo(int i, byte[] bArr) {
            this.mType = i;
            this.mInfo = bArr;
        }
    }

    public void addAppBackgroundRestrictionListener(ActivityManagerInternal.AppBackgroundRestrictionListener appBackgroundRestrictionListener) {
        this.mRestrictionListeners.add(appBackgroundRestrictionListener);
    }

    public AppRestrictionController(Context context, ActivityManagerService activityManagerService) {
        this(new Injector(context), activityManagerService);
    }

    public AppRestrictionController(Injector injector, ActivityManagerService activityManagerService) {
        this.mAppStateTrackers = new ArrayList<>();
        this.mRestrictionSettings = new RestrictionSettings();
        this.mRestrictionListeners = new CopyOnWriteArraySet<>();
        this.mActiveUids = new SparseArrayMap<>();
        this.mTmpRunnables = new ArrayList<>();
        this.mDeviceIdleAllowlist = new int[0];
        this.mDeviceIdleExceptIdleAllowlist = new int[0];
        this.mSystemDeviceIdleAllowlist = new ArraySet<>();
        this.mSystemDeviceIdleExceptIdleAllowlist = new ArraySet<>();
        this.mLock = new Object();
        this.mSettingsLock = new Object();
        this.mRoleHolderChangedListener = new OnRoleHoldersChangedListener() { // from class: com.android.server.am.AppRestrictionController$$ExternalSyntheticLambda1
            public final void onRoleHoldersChanged(String str, UserHandle userHandle) {
                AppRestrictionController.this.onRoleHoldersChanged(str, userHandle);
            }
        };
        this.mUidRolesMapping = new SparseArray<>();
        this.mSystemModulesCache = new HashMap<>();
        this.mCarrierPrivilegedLock = new Object();
        this.mRestrictionSettingsXmlLoaded = new AtomicBoolean();
        this.mEmptyTrackerInfo = new TrackerInfo();
        this.mBroadcastReceiver = new BroadcastReceiver() { // from class: com.android.server.am.AppRestrictionController.1
            /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context, Intent intent) {
                char c;
                int intExtra;
                int intExtra2;
                String schemeSpecificPart;
                intent.getAction();
                String action = intent.getAction();
                action.hashCode();
                switch (action.hashCode()) {
                    case -2061058799:
                        if (action.equals("android.intent.action.USER_REMOVED")) {
                            c = 0;
                            break;
                        }
                        c = 65535;
                        break;
                    case -1749672628:
                        if (action.equals("android.intent.action.UID_REMOVED")) {
                            c = 1;
                            break;
                        }
                        c = 65535;
                        break;
                    case -755112654:
                        if (action.equals("android.intent.action.USER_STARTED")) {
                            c = 2;
                            break;
                        }
                        c = 65535;
                        break;
                    case -742246786:
                        if (action.equals("android.intent.action.USER_STOPPED")) {
                            c = 3;
                            break;
                        }
                        c = 65535;
                        break;
                    case 172491798:
                        if (action.equals("android.intent.action.PACKAGE_CHANGED")) {
                            c = 4;
                            break;
                        }
                        c = 65535;
                        break;
                    case 1121780209:
                        if (action.equals("android.intent.action.USER_ADDED")) {
                            c = 5;
                            break;
                        }
                        c = 65535;
                        break;
                    case 1544582882:
                        if (action.equals("android.intent.action.PACKAGE_ADDED")) {
                            c = 6;
                            break;
                        }
                        c = 65535;
                        break;
                    case 1580442797:
                        if (action.equals("android.intent.action.PACKAGE_FULLY_REMOVED")) {
                            c = 7;
                            break;
                        }
                        c = 65535;
                        break;
                    default:
                        c = 65535;
                        break;
                }
                switch (c) {
                    case 0:
                        int intExtra3 = intent.getIntExtra("android.intent.extra.user_handle", -1);
                        if (intExtra3 >= 0) {
                            AppRestrictionController.this.onUserRemoved(intExtra3);
                            return;
                        }
                        return;
                    case 1:
                        if (intent.getBooleanExtra("android.intent.extra.REPLACING", false) || (intExtra = intent.getIntExtra("android.intent.extra.UID", -1)) < 0) {
                            return;
                        }
                        AppRestrictionController.this.onUidRemoved(intExtra);
                        return;
                    case 2:
                        int intExtra4 = intent.getIntExtra("android.intent.extra.user_handle", -1);
                        if (intExtra4 >= 0) {
                            AppRestrictionController.this.onUserStarted(intExtra4);
                            return;
                        }
                        return;
                    case 3:
                        int intExtra5 = intent.getIntExtra("android.intent.extra.user_handle", -1);
                        if (intExtra5 >= 0) {
                            AppRestrictionController.this.onUserStopped(intExtra5);
                            return;
                        }
                        return;
                    case 4:
                        break;
                    case 5:
                        int intExtra6 = intent.getIntExtra("android.intent.extra.user_handle", -1);
                        if (intExtra6 >= 0) {
                            AppRestrictionController.this.onUserAdded(intExtra6);
                            return;
                        }
                        return;
                    case 6:
                        if (!intent.getBooleanExtra("android.intent.extra.REPLACING", false) && (intExtra2 = intent.getIntExtra("android.intent.extra.UID", -1)) >= 0) {
                            AppRestrictionController.this.onUidAdded(intExtra2);
                            break;
                        }
                        break;
                    case 7:
                        int intExtra7 = intent.getIntExtra("android.intent.extra.UID", -1);
                        Uri data = intent.getData();
                        if (intExtra7 < 0 || data == null || (schemeSpecificPart = data.getSchemeSpecificPart()) == null) {
                            return;
                        }
                        AppRestrictionController.this.onPackageRemoved(schemeSpecificPart, intExtra7);
                        return;
                    default:
                        return;
                }
                String schemeSpecificPart2 = intent.getData().getSchemeSpecificPart();
                String[] stringArrayExtra = intent.getStringArrayExtra("android.intent.extra.changed_component_name_list");
                if (stringArrayExtra == null || (stringArrayExtra.length == 1 && schemeSpecificPart2.equals(stringArrayExtra[0]))) {
                    AppRestrictionController.this.clearCarrierPrivilegedApps();
                }
            }
        };
        this.mBootReceiver = new BroadcastReceiver() { // from class: com.android.server.am.AppRestrictionController.2
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context, Intent intent) {
                intent.getAction();
                String action = intent.getAction();
                action.hashCode();
                if (action.equals("android.intent.action.LOCKED_BOOT_COMPLETED")) {
                    AppRestrictionController.this.onLockedBootCompleted();
                }
            }
        };
        this.mBackgroundRestrictionListener = new AppStateTracker.BackgroundRestrictedAppListener() { // from class: com.android.server.am.AppRestrictionController.3
            public void updateBackgroundRestrictedForUidPackage(int i, String str, boolean z) {
                AppRestrictionController.this.mBgHandler.obtainMessage(0, i, z ? 1 : 0, str).sendToTarget();
            }
        };
        this.mAppIdleStateChangeListener = new AppStandbyInternal.AppIdleStateChangeListener() { // from class: com.android.server.am.AppRestrictionController.4
            public void onAppIdleStateChanged(String str, int i, boolean z, int i2, int i3) {
                AppRestrictionController.this.mBgHandler.obtainMessage(2, i, i2, str).sendToTarget();
            }

            public void onUserInteractionStarted(String str, int i) {
                AppRestrictionController.this.mBgHandler.obtainMessage(3, i, 0, str).sendToTarget();
            }
        };
        this.mUidObserver = new IUidObserver.Stub() { // from class: com.android.server.am.AppRestrictionController.5
            public void onUidCachedChanged(int i, boolean z) {
            }

            public void onUidProcAdjChanged(int i) {
            }

            public void onUidStateChanged(int i, int i2, long j, int i3) {
                AppRestrictionController.this.mBgHandler.obtainMessage(8, i, i2).sendToTarget();
            }

            public void onUidIdle(int i, boolean z) {
                AppRestrictionController.this.mBgHandler.obtainMessage(5, i, z ? 1 : 0).sendToTarget();
            }

            public void onUidGone(int i, boolean z) {
                AppRestrictionController.this.mBgHandler.obtainMessage(7, i, z ? 1 : 0).sendToTarget();
            }

            public void onUidActive(int i) {
                AppRestrictionController.this.mBgHandler.obtainMessage(6, i, 0).sendToTarget();
            }
        };
        this.mInjector = injector;
        Context context = injector.getContext();
        this.mContext = context;
        this.mActivityManagerService = activityManagerService;
        HandlerThread handlerThread = new HandlerThread("bgres-controller", 10);
        this.mBgHandlerThread = handlerThread;
        handlerThread.start();
        BgHandler bgHandler = new BgHandler(handlerThread.getLooper(), injector);
        this.mBgHandler = bgHandler;
        this.mBgExecutor = new HandlerExecutor(bgHandler);
        this.mConstantsObserver = new ConstantsObserver(bgHandler, context);
        this.mNotificationHelper = new NotificationHelper(this);
        injector.initAppStateTrackers(this);
    }

    public void onSystemReady() {
        DeviceConfig.addOnPropertiesChangedListener("activity_manager", this.mBgExecutor, this.mConstantsObserver);
        this.mConstantsObserver.start();
        initBgRestrictionExemptioFromSysConfig();
        initRestrictionStates();
        initSystemModuleNames();
        initRolesInInterest();
        registerForUidObservers();
        registerForSystemBroadcasts();
        this.mNotificationHelper.onSystemReady();
        this.mInjector.getAppStateTracker().addBackgroundRestrictedAppListener(this.mBackgroundRestrictionListener);
        this.mInjector.getAppStandbyInternal().addListener(this.mAppIdleStateChangeListener);
        this.mInjector.getRoleManager().addOnRoleHoldersChangedListenerAsUser(this.mBgExecutor, this.mRoleHolderChangedListener, UserHandle.ALL);
        this.mInjector.scheduleInitTrackers(this.mBgHandler, new Runnable() { // from class: com.android.server.am.AppRestrictionController$$ExternalSyntheticLambda3
            @Override // java.lang.Runnable
            public final void run() {
                AppRestrictionController.this.lambda$onSystemReady$0();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onSystemReady$0() {
        int size = this.mAppStateTrackers.size();
        for (int i = 0; i < size; i++) {
            this.mAppStateTrackers.get(i).onSystemReady();
        }
    }

    @VisibleForTesting
    public void resetRestrictionSettings() {
        synchronized (this.mSettingsLock) {
            this.mRestrictionSettings.reset();
        }
        initRestrictionStates();
    }

    @VisibleForTesting
    public void tearDown() {
        DeviceConfig.removeOnPropertiesChangedListener(this.mConstantsObserver);
        unregisterForUidObservers();
        unregisterForSystemBroadcasts();
        this.mRestrictionSettings.removeXml();
    }

    public final void initBgRestrictionExemptioFromSysConfig() {
        SystemConfig systemConfig = SystemConfig.getInstance();
        this.mBgRestrictionExemptioFromSysConfig = systemConfig.getBgRestrictionExemption();
        loadAppIdsFromPackageList(systemConfig.getAllowInPowerSaveExceptIdle(), this.mSystemDeviceIdleExceptIdleAllowlist);
        loadAppIdsFromPackageList(systemConfig.getAllowInPowerSave(), this.mSystemDeviceIdleAllowlist);
    }

    public final void loadAppIdsFromPackageList(ArraySet<String> arraySet, ArraySet<Integer> arraySet2) {
        PackageManager packageManager = this.mInjector.getPackageManager();
        for (int size = arraySet.size() - 1; size >= 0; size--) {
            try {
                ApplicationInfo applicationInfo = packageManager.getApplicationInfo(arraySet.valueAt(size), 1048576);
                if (applicationInfo != null) {
                    arraySet2.add(Integer.valueOf(UserHandle.getAppId(applicationInfo.uid)));
                }
            } catch (PackageManager.NameNotFoundException unused) {
            }
        }
    }

    public final boolean isExemptedFromSysConfig(String str) {
        ArraySet<String> arraySet = this.mBgRestrictionExemptioFromSysConfig;
        return arraySet != null && arraySet.contains(str);
    }

    public final void initRestrictionStates() {
        int[] userIds = this.mInjector.getUserManagerInternal().getUserIds();
        for (int i : userIds) {
            refreshAppRestrictionLevelForUser(i, 1024, 2);
        }
        if (this.mInjector.isTest()) {
            return;
        }
        this.mRestrictionSettings.scheduleLoadFromXml();
        for (int i2 : userIds) {
            this.mRestrictionSettings.schedulePersistToXml(i2);
        }
    }

    public final void initSystemModuleNames() {
        List<ModuleInfo> installedModules = this.mInjector.getPackageManager().getInstalledModules(0);
        if (installedModules == null) {
            return;
        }
        synchronized (this.mLock) {
            for (ModuleInfo moduleInfo : installedModules) {
                this.mSystemModulesCache.put(moduleInfo.getPackageName(), Boolean.TRUE);
            }
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:19:0x003d, code lost:
        if (r0.applicationInfo.sourceDir.startsWith(android.os.Environment.getApexDirectory().getAbsolutePath()) != false) goto L32;
     */
    /* JADX WARN: Removed duplicated region for block: B:36:0x0045 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:41:0x0027 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final boolean isSystemModule(String str) {
        boolean z;
        synchronized (this.mLock) {
            Boolean bool = this.mSystemModulesCache.get(str);
            if (bool != null) {
                return bool.booleanValue();
            }
            PackageManager packageManager = this.mInjector.getPackageManager();
            boolean z2 = true;
            if (packageManager.getModuleInfo(str, 0) != null) {
                z = true;
                if (!z) {
                    try {
                        PackageInfo packageInfo = packageManager.getPackageInfo(str, 0);
                        if (packageInfo != null) {
                        }
                        z2 = false;
                        z = z2;
                    } catch (PackageManager.NameNotFoundException unused) {
                    }
                }
                synchronized (this.mLock) {
                    this.mSystemModulesCache.put(str, Boolean.valueOf(z));
                }
                return z;
            }
            z = false;
            if (!z) {
            }
            synchronized (this.mLock) {
            }
        }
    }

    public final void registerForUidObservers() {
        try {
            this.mInjector.getIActivityManager().registerUidObserver(this.mUidObserver, 15, 4, PackageManagerShellCommandDataLoader.PACKAGE);
        } catch (RemoteException unused) {
        }
    }

    public final void unregisterForUidObservers() {
        try {
            this.mInjector.getIActivityManager().unregisterUidObserver(this.mUidObserver);
        } catch (RemoteException unused) {
        }
    }

    public final void refreshAppRestrictionLevelForUser(int i, int i2, int i3) {
        List<AppStandbyInfo> appStandbyBuckets = this.mInjector.getAppStandbyInternal().getAppStandbyBuckets(i);
        if (ArrayUtils.isEmpty(appStandbyBuckets)) {
            return;
        }
        PackageManagerInternal packageManagerInternal = this.mInjector.getPackageManagerInternal();
        for (AppStandbyInfo appStandbyInfo : appStandbyBuckets) {
            int packageUid = packageManagerInternal.getPackageUid(appStandbyInfo.mPackageName, 819200L, i);
            if (packageUid < 0) {
                Slog.e("ActivityManager", "Unable to find " + appStandbyInfo.mPackageName + "/u" + i);
            } else {
                Pair<Integer, TrackerInfo> calcAppRestrictionLevel = calcAppRestrictionLevel(i, packageUid, appStandbyInfo.mPackageName, appStandbyInfo.mStandbyBucket, false, false);
                applyRestrictionLevel(appStandbyInfo.mPackageName, packageUid, ((Integer) calcAppRestrictionLevel.first).intValue(), (TrackerInfo) calcAppRestrictionLevel.second, appStandbyInfo.mStandbyBucket, true, i2, i3);
            }
        }
    }

    public void refreshAppRestrictionLevelForUid(int i, int i2, int i3, boolean z) {
        String[] packagesForUid = this.mInjector.getPackageManager().getPackagesForUid(i);
        if (ArrayUtils.isEmpty(packagesForUid)) {
            return;
        }
        AppStandbyInternal appStandbyInternal = this.mInjector.getAppStandbyInternal();
        int userId = UserHandle.getUserId(i);
        long elapsedRealtime = SystemClock.elapsedRealtime();
        int i4 = 0;
        for (int length = packagesForUid.length; i4 < length; length = length) {
            String str = packagesForUid[i4];
            int appStandbyBucket = appStandbyInternal.getAppStandbyBucket(str, userId, elapsedRealtime, false);
            Pair<Integer, TrackerInfo> calcAppRestrictionLevel = calcAppRestrictionLevel(userId, i, str, appStandbyBucket, z, true);
            applyRestrictionLevel(str, i, ((Integer) calcAppRestrictionLevel.first).intValue(), (TrackerInfo) calcAppRestrictionLevel.second, appStandbyBucket, true, i2, i3);
            i4++;
        }
    }

    public final Pair<Integer, TrackerInfo> calcAppRestrictionLevel(int i, int i2, String str, int i3, boolean z, boolean z2) {
        if (this.mInjector.getAppHibernationInternal().isHibernatingForUser(str, i)) {
            return new Pair<>(60, this.mEmptyTrackerInfo);
        }
        int i4 = 20;
        TrackerInfo trackerInfo = null;
        if (i3 != 5) {
            if (i3 == 50) {
                i4 = 50;
            } else if (this.mInjector.getAppStateTracker().isAppBackgroundRestricted(i2, str)) {
                return new Pair<>(50, this.mEmptyTrackerInfo);
            } else {
                int i5 = (this.mConstantsObserver.mRestrictedBucketEnabled && i3 == 45) ? 40 : 30;
                if (z2) {
                    Pair<Integer, TrackerInfo> calcAppRestrictionLevelFromTackers = calcAppRestrictionLevelFromTackers(i2, str, 100);
                    int intValue = ((Integer) calcAppRestrictionLevelFromTackers.first).intValue();
                    if (intValue == 20) {
                        return new Pair<>(20, (TrackerInfo) calcAppRestrictionLevelFromTackers.second);
                    }
                    if (intValue > i5) {
                        trackerInfo = (TrackerInfo) calcAppRestrictionLevelFromTackers.second;
                        i4 = intValue;
                    } else {
                        i4 = i5;
                    }
                    if (i4 == 50) {
                        if (z) {
                            this.mBgHandler.obtainMessage(4, i2, 0, str).sendToTarget();
                        }
                        Pair<Integer, TrackerInfo> calcAppRestrictionLevelFromTackers2 = calcAppRestrictionLevelFromTackers(i2, str, 50);
                        i4 = ((Integer) calcAppRestrictionLevelFromTackers2.first).intValue();
                        trackerInfo = (TrackerInfo) calcAppRestrictionLevelFromTackers2.second;
                    }
                } else {
                    i4 = i5;
                }
            }
        }
        return new Pair<>(Integer.valueOf(i4), trackerInfo);
    }

    public final Pair<Integer, TrackerInfo> calcAppRestrictionLevelFromTackers(int i, String str, int i2) {
        TrackerInfo trackerInfo;
        boolean z = this.mConstantsObserver.mRestrictedBucketEnabled;
        int i3 = 0;
        BaseAppStateTracker baseAppStateTracker = null;
        int i4 = 0;
        for (int size = this.mAppStateTrackers.size() - 1; size >= 0; size--) {
            int proposedRestrictionLevel = this.mAppStateTrackers.get(size).getPolicy().getProposedRestrictionLevel(str, i, i2);
            if (!z && proposedRestrictionLevel == 40) {
                proposedRestrictionLevel = 30;
            }
            i3 = Math.max(i3, proposedRestrictionLevel);
            if (i3 != i4) {
                baseAppStateTracker = this.mAppStateTrackers.get(size);
                i4 = i3;
            }
        }
        if (baseAppStateTracker == null) {
            trackerInfo = this.mEmptyTrackerInfo;
        } else {
            trackerInfo = new TrackerInfo(baseAppStateTracker.getType(), baseAppStateTracker.getTrackerInfoForStatsd(i));
        }
        return new Pair<>(Integer.valueOf(i3), trackerInfo);
    }

    public int getRestrictionLevel(int i) {
        return this.mRestrictionSettings.getRestrictionLevel(i);
    }

    public int getRestrictionLevel(int i, String str) {
        return this.mRestrictionSettings.getRestrictionLevel(i, str);
    }

    public int getRestrictionLevel(String str, int i) {
        return this.mRestrictionSettings.getRestrictionLevel(str, i);
    }

    public boolean isAutoRestrictAbusiveAppEnabled() {
        return this.mConstantsObserver.mBgAutoRestrictAbusiveApps;
    }

    public long getForegroundServiceTotalDurationsSince(String str, int i, long j, long j2, int i2) {
        return this.mInjector.getAppFGSTracker().getTotalDurationsSince(str, i, j, j2, AppFGSTracker.foregroundServiceTypeToIndex(i2));
    }

    public long getForegroundServiceTotalDurationsSince(int i, long j, long j2, int i2) {
        return this.mInjector.getAppFGSTracker().getTotalDurationsSince(i, j, j2, AppFGSTracker.foregroundServiceTypeToIndex(i2));
    }

    public long getMediaSessionTotalDurationsSince(String str, int i, long j, long j2) {
        return this.mInjector.getAppMediaSessionTracker().getTotalDurationsSince(str, i, j, j2);
    }

    public long getMediaSessionTotalDurationsSince(int i, long j, long j2) {
        return this.mInjector.getAppMediaSessionTracker().getTotalDurationsSince(i, j, j2);
    }

    public long getCompositeMediaPlaybackDurations(String str, int i, long j, long j2) {
        long max = Math.max(0L, j - j2);
        return Math.max(getMediaSessionTotalDurationsSince(str, i, max, j), getForegroundServiceTotalDurationsSince(str, i, max, j, 2));
    }

    public long getCompositeMediaPlaybackDurations(int i, long j, long j2) {
        long max = Math.max(0L, j - j2);
        return Math.max(getMediaSessionTotalDurationsSince(i, max, j), getForegroundServiceTotalDurationsSince(i, max, j, 2));
    }

    public boolean hasForegroundServices(String str, int i) {
        return this.mInjector.getAppFGSTracker().hasForegroundServices(str, i);
    }

    public boolean hasForegroundServiceNotifications(String str, int i) {
        return this.mInjector.getAppFGSTracker().hasForegroundServiceNotifications(str, i);
    }

    public AppBatteryTracker.ImmutableBatteryUsage getUidBatteryExemptedUsageSince(int i, long j, long j2, int i2) {
        return this.mInjector.getAppBatteryExemptionTracker().getUidBatteryExemptedUsageSince(i, j, j2, i2);
    }

    public AppBatteryTracker.ImmutableBatteryUsage getUidBatteryUsage(int i) {
        return this.mInjector.getUidBatteryUsageProvider().getUidBatteryUsage(i);
    }

    public void dump(PrintWriter printWriter, String str) {
        printWriter.print(str);
        printWriter.println("APP BACKGROUND RESTRICTIONS");
        String str2 = "  " + str;
        printWriter.print(str2);
        printWriter.println("BACKGROUND RESTRICTION LEVEL SETTINGS");
        this.mRestrictionSettings.dump(printWriter, "  " + str2);
        this.mConstantsObserver.dump(printWriter, "  " + str2);
        int size = this.mAppStateTrackers.size();
        for (int i = 0; i < size; i++) {
            printWriter.println();
            this.mAppStateTrackers.get(i).dump(printWriter, str2);
        }
    }

    public void dumpAsProto(ProtoOutputStream protoOutputStream, int i) {
        int size = this.mAppStateTrackers.size();
        for (int i2 = 0; i2 < size; i2++) {
            this.mAppStateTrackers.get(i2).dumpAsProto(protoOutputStream, i);
        }
    }

    public final int getExemptionReasonStatsd(int i, int i2) {
        if (i2 != 20) {
            return 1;
        }
        return PowerExemptionManager.getExemptionReasonForStatsd(getBackgroundRestrictionExemptionReason(i));
    }

    public final int getTargetSdkStatsd(String str) {
        ApplicationInfo applicationInfo;
        PackageManager packageManager = this.mInjector.getPackageManager();
        if (packageManager == null) {
            return 0;
        }
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(str, 0);
            if (packageInfo != null && (applicationInfo = packageInfo.applicationInfo) != null) {
                int i = applicationInfo.targetSdkVersion;
                if (i < 31) {
                    return 1;
                }
                if (i < 33) {
                    return 2;
                }
                if (i == 33) {
                    return 3;
                }
            }
        } catch (PackageManager.NameNotFoundException unused) {
        }
        return 0;
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r16v0 */
    /* JADX WARN: Type inference failed for: r16v1 */
    /* JADX WARN: Type inference failed for: r16v2 */
    public void applyRestrictionLevel(final String str, final int i, final int i2, TrackerInfo trackerInfo, int i3, boolean z, int i4, int i5) {
        int i6;
        int i7;
        Object obj;
        int i8;
        boolean z2;
        int appStandbyBucketReason;
        final AppStandbyInternal appStandbyInternal = this.mInjector.getAppStandbyInternal();
        TrackerInfo trackerInfo2 = trackerInfo == null ? this.mEmptyTrackerInfo : trackerInfo;
        synchronized (this.mSettingsLock) {
            final int restrictionLevel = getRestrictionLevel(i, str);
            if (restrictionLevel == i2) {
                return;
            }
            if (standbyBucketToRestrictionLevel(i3) != i2 || (appStandbyBucketReason = appStandbyInternal.getAppStandbyBucketReason(str, UserHandle.getUserId(i), SystemClock.elapsedRealtime())) == 0) {
                i6 = i4;
                i7 = i5;
            } else {
                i7 = appStandbyBucketReason & 255;
                i6 = appStandbyBucketReason & 65280;
            }
            ?? reason = this.mRestrictionSettings.getReason(str, i);
            final int i9 = i7;
            final int i10 = i6;
            this.mRestrictionSettings.update(str, i, i2, i6, i9);
            if (!z || i3 == 5) {
                return;
            }
            if (i2 < 40 || restrictionLevel >= 40) {
                if (restrictionLevel < 40 || i2 >= 40) {
                    return;
                }
                synchronized (this.mSettingsLock) {
                    if (this.mActiveUids.indexOfKey(i, str) >= 0) {
                        this.mActiveUids.add(i, str, (Object) null);
                    }
                }
                appStandbyInternal.maybeUnrestrictApp(str, UserHandle.getUserId(i), reason & 65280, reason & 255, i10, i9);
                logAppBackgroundRestrictionInfo(str, i, restrictionLevel, i2, trackerInfo2, i10);
            } else if (!this.mConstantsObserver.mRestrictedBucketEnabled || i3 == 45) {
            } else {
                if (!this.mConstantsObserver.mBgAutoRestrictedBucket && i2 != 40) {
                    return;
                }
                Object obj2 = this.mSettingsLock;
                synchronized (obj2) {
                    try {
                        try {
                            if (this.mActiveUids.indexOfKey(i, str) >= 0) {
                                obj = obj2;
                                i8 = restrictionLevel;
                                final TrackerInfo trackerInfo3 = trackerInfo2;
                                this.mActiveUids.add(i, str, new Runnable() { // from class: com.android.server.am.AppRestrictionController$$ExternalSyntheticLambda0
                                    @Override // java.lang.Runnable
                                    public final void run() {
                                        AppRestrictionController.this.lambda$applyRestrictionLevel$1(appStandbyInternal, str, i, i10, i9, restrictionLevel, i2, trackerInfo3);
                                    }
                                });
                                z2 = false;
                            } else {
                                obj = obj2;
                                i8 = restrictionLevel;
                                z2 = true;
                            }
                            if (z2) {
                                appStandbyInternal.restrictApp(str, UserHandle.getUserId(i), i10, i9);
                                logAppBackgroundRestrictionInfo(str, i, i8, i2, trackerInfo2, i10);
                            }
                        } catch (Throwable th) {
                            th = th;
                            reason = obj2;
                            throw th;
                        }
                    } catch (Throwable th2) {
                        th = th2;
                    }
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$applyRestrictionLevel$1(AppStandbyInternal appStandbyInternal, String str, int i, int i2, int i3, int i4, int i5, TrackerInfo trackerInfo) {
        appStandbyInternal.restrictApp(str, UserHandle.getUserId(i), i2, i3);
        logAppBackgroundRestrictionInfo(str, i, i4, i5, trackerInfo, i2);
    }

    public final void logAppBackgroundRestrictionInfo(String str, int i, int i2, int i3, TrackerInfo trackerInfo, int i4) {
        int restrictionLevelStatsd = getRestrictionLevelStatsd(i3);
        int thresholdStatsd = getThresholdStatsd(i4);
        int trackerTypeStatsd = getTrackerTypeStatsd(trackerInfo.mType);
        int i5 = trackerInfo.mType;
        FrameworkStatsLog.write((int) FrameworkStatsLog.APP_BACKGROUND_RESTRICTIONS_INFO, i, restrictionLevelStatsd, thresholdStatsd, trackerTypeStatsd, i5 == 3 ? trackerInfo.mInfo : null, i5 == 1 ? trackerInfo.mInfo : null, i5 == 6 ? trackerInfo.mInfo : null, i5 == 7 ? trackerInfo.mInfo : null, getExemptionReasonStatsd(i, i3), getOptimizationLevelStatsd(i3), getTargetSdkStatsd(str), ActivityManager.isLowRamDeviceStatic(), getRestrictionLevelStatsd(i2));
    }

    public final void handleBackgroundRestrictionChanged(int i, String str, boolean z) {
        int i2;
        int size = this.mAppStateTrackers.size();
        for (int i3 = 0; i3 < size; i3++) {
            this.mAppStateTrackers.get(i3).onBackgroundRestrictionChanged(i, str, z);
        }
        int appStandbyBucket = this.mInjector.getAppStandbyInternal().getAppStandbyBucket(str, UserHandle.getUserId(i), SystemClock.elapsedRealtime(), false);
        if (z) {
            applyRestrictionLevel(str, i, 50, this.mEmptyTrackerInfo, appStandbyBucket, true, 1024, 2);
            this.mBgHandler.obtainMessage(9, i, 0, str).sendToTarget();
            return;
        }
        int lastRestrictionLevel = this.mRestrictionSettings.getLastRestrictionLevel(i, str);
        int i4 = 5;
        if (appStandbyBucket != 5) {
            i4 = 40;
            if (lastRestrictionLevel == 40) {
                i2 = 45;
                Pair<Integer, TrackerInfo> calcAppRestrictionLevel = calcAppRestrictionLevel(UserHandle.getUserId(i), i, str, i2, false, true);
                applyRestrictionLevel(str, i, ((Integer) calcAppRestrictionLevel.first).intValue(), (TrackerInfo) calcAppRestrictionLevel.second, appStandbyBucket, true, FrameworkStatsLog.APP_STANDBY_BUCKET_CHANGED__MAIN_REASON__MAIN_USAGE, 3);
            }
        }
        i2 = i4;
        Pair<Integer, TrackerInfo> calcAppRestrictionLevel2 = calcAppRestrictionLevel(UserHandle.getUserId(i), i, str, i2, false, true);
        applyRestrictionLevel(str, i, ((Integer) calcAppRestrictionLevel2.first).intValue(), (TrackerInfo) calcAppRestrictionLevel2.second, appStandbyBucket, true, FrameworkStatsLog.APP_STANDBY_BUCKET_CHANGED__MAIN_REASON__MAIN_USAGE, 3);
    }

    public final void dispatchAppRestrictionLevelChanges(final int i, final String str, final int i2) {
        this.mRestrictionListeners.forEach(new Consumer() { // from class: com.android.server.am.AppRestrictionController$$ExternalSyntheticLambda2
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                ((ActivityManagerInternal.AppBackgroundRestrictionListener) obj).onRestrictionLevelChanged(i, str, i2);
            }
        });
    }

    public final void dispatchAutoRestrictedBucketFeatureFlagChanged(final boolean z) {
        final AppStandbyInternal appStandbyInternal = this.mInjector.getAppStandbyInternal();
        final ArrayList arrayList = new ArrayList();
        synchronized (this.mSettingsLock) {
            this.mRestrictionSettings.forEachUidLocked(new Consumer() { // from class: com.android.server.am.AppRestrictionController$$ExternalSyntheticLambda6
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    AppRestrictionController.this.lambda$dispatchAutoRestrictedBucketFeatureFlagChanged$6(arrayList, z, appStandbyInternal, (Integer) obj);
                }
            });
        }
        for (int i = 0; i < arrayList.size(); i++) {
            ((Runnable) arrayList.get(i)).run();
        }
        this.mRestrictionListeners.forEach(new Consumer() { // from class: com.android.server.am.AppRestrictionController$$ExternalSyntheticLambda7
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                ((ActivityManagerInternal.AppBackgroundRestrictionListener) obj).onAutoRestrictedBucketFeatureFlagChanged(z);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$dispatchAutoRestrictedBucketFeatureFlagChanged$6(final ArrayList arrayList, final boolean z, final AppStandbyInternal appStandbyInternal, final Integer num) {
        this.mRestrictionSettings.forEachPackageInUidLocked(num.intValue(), new TriConsumer() { // from class: com.android.server.am.AppRestrictionController$$ExternalSyntheticLambda8
            public final void accept(Object obj, Object obj2, Object obj3) {
                AppRestrictionController.lambda$dispatchAutoRestrictedBucketFeatureFlagChanged$5(arrayList, z, appStandbyInternal, num, (String) obj, (Integer) obj2, (Integer) obj3);
            }
        });
    }

    public static /* synthetic */ void lambda$dispatchAutoRestrictedBucketFeatureFlagChanged$5(ArrayList arrayList, boolean z, final AppStandbyInternal appStandbyInternal, final Integer num, final String str, Integer num2, final Integer num3) {
        Runnable runnable;
        if (num2.intValue() == 50) {
            if (z) {
                runnable = new Runnable() { // from class: com.android.server.am.AppRestrictionController$$ExternalSyntheticLambda9
                    @Override // java.lang.Runnable
                    public final void run() {
                        AppRestrictionController.lambda$dispatchAutoRestrictedBucketFeatureFlagChanged$3(appStandbyInternal, str, num, num3);
                    }
                };
            } else {
                runnable = new Runnable() { // from class: com.android.server.am.AppRestrictionController$$ExternalSyntheticLambda10
                    @Override // java.lang.Runnable
                    public final void run() {
                        AppRestrictionController.lambda$dispatchAutoRestrictedBucketFeatureFlagChanged$4(appStandbyInternal, str, num, num3);
                    }
                };
            }
            arrayList.add(runnable);
        }
    }

    public static /* synthetic */ void lambda$dispatchAutoRestrictedBucketFeatureFlagChanged$3(AppStandbyInternal appStandbyInternal, String str, Integer num, Integer num2) {
        appStandbyInternal.restrictApp(str, UserHandle.getUserId(num.intValue()), num2.intValue() & 65280, num2.intValue() & 255);
    }

    public static /* synthetic */ void lambda$dispatchAutoRestrictedBucketFeatureFlagChanged$4(AppStandbyInternal appStandbyInternal, String str, Integer num, Integer num2) {
        appStandbyInternal.maybeUnrestrictApp(str, UserHandle.getUserId(num.intValue()), num2.intValue() & 65280, num2.intValue() & 255, (int) FrameworkStatsLog.APP_STANDBY_BUCKET_CHANGED__MAIN_REASON__MAIN_USAGE, 6);
    }

    public final void handleAppStandbyBucketChanged(int i, String str, int i2) {
        int packageUid = this.mInjector.getPackageManagerInternal().getPackageUid(str, 819200L, i2);
        Pair<Integer, TrackerInfo> calcAppRestrictionLevel = calcAppRestrictionLevel(i2, packageUid, str, i, false, false);
        applyRestrictionLevel(str, packageUid, ((Integer) calcAppRestrictionLevel.first).intValue(), (TrackerInfo) calcAppRestrictionLevel.second, i, false, 256, 0);
    }

    public void handleRequestBgRestricted(String str, int i) {
        this.mNotificationHelper.postRequestBgRestrictedIfNecessary(str, i);
    }

    public void handleCancelRequestBgRestricted(String str, int i) {
        this.mNotificationHelper.cancelRequestBgRestrictedIfNecessary(str, i);
    }

    public void handleUidProcStateChanged(int i, int i2) {
        int size = this.mAppStateTrackers.size();
        for (int i3 = 0; i3 < size; i3++) {
            this.mAppStateTrackers.get(i3).onUidProcStateChanged(i, i2);
        }
    }

    public void handleUidGone(int i) {
        int size = this.mAppStateTrackers.size();
        for (int i2 = 0; i2 < size; i2++) {
            this.mAppStateTrackers.get(i2).onUidGone(i);
        }
    }

    /* renamed from: com.android.server.am.AppRestrictionController$NotificationHelper */
    /* loaded from: classes.dex */
    public static class NotificationHelper {
        public final AppRestrictionController mBgController;
        public final Context mContext;
        public final Injector mInjector;
        public final Object mLock;
        public final NotificationManager mNotificationManager;
        public final Object mSettingsLock;
        public static final String[] NOTIFICATION_TYPE_STRINGS = {"Abusive current drain", "Long-running FGS"};
        public static final String[] NOTIFICATION_TIME_ATTRS = {"last_batt_noti_ts", "last_long_fgs_noti_ts"};
        public final BroadcastReceiver mActionButtonReceiver = new BroadcastReceiver() { // from class: com.android.server.am.AppRestrictionController.NotificationHelper.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context, Intent intent) {
                intent.getAction();
                String action = intent.getAction();
                action.hashCode();
                if (action.equals("com.android.server.am.ACTION_FGS_MANAGER_TRAMPOLINE")) {
                    NotificationHelper.this.cancelRequestBgRestrictedIfNecessary(intent.getStringExtra("android.intent.extra.PACKAGE_NAME"), intent.getIntExtra("android.intent.extra.UID", 0));
                    Intent intent2 = new Intent("android.intent.action.SHOW_FOREGROUND_SERVICE_MANAGER");
                    intent2.addFlags(16777216);
                    NotificationHelper.this.mContext.sendBroadcastAsUser(intent2, UserHandle.SYSTEM);
                }
            }
        };
        @GuardedBy({"mSettingsLock"})
        public int mNotificationIDStepper = 203105545;

        public static int notificationTimeAttrToType(String str) {
            str.hashCode();
            if (str.equals("last_long_fgs_noti_ts")) {
                return 1;
            }
            if (str.equals("last_batt_noti_ts")) {
                return 0;
            }
            throw new IllegalArgumentException();
        }

        public static String notificationTypeToTimeAttr(int i) {
            return NOTIFICATION_TIME_ATTRS[i];
        }

        public static String notificationTypeToString(int i) {
            return NOTIFICATION_TYPE_STRINGS[i];
        }

        public NotificationHelper(AppRestrictionController appRestrictionController) {
            this.mBgController = appRestrictionController;
            Injector injector = appRestrictionController.mInjector;
            this.mInjector = injector;
            this.mNotificationManager = injector.getNotificationManager();
            this.mLock = appRestrictionController.mLock;
            this.mSettingsLock = appRestrictionController.mSettingsLock;
            this.mContext = injector.getContext();
        }

        public void onSystemReady() {
            this.mContext.registerReceiverForAllUsers(this.mActionButtonReceiver, new IntentFilter("com.android.server.am.ACTION_FGS_MANAGER_TRAMPOLINE"), "android.permission.MANAGE_ACTIVITY_TASKS", this.mBgController.mBgHandler, 4);
        }

        public void postRequestBgRestrictedIfNecessary(String str, int i) {
            if (this.mBgController.mConstantsObserver.mBgPromptAbusiveAppsToBgRestricted) {
                Intent intent = new Intent("android.settings.VIEW_ADVANCED_POWER_USAGE_DETAIL");
                intent.setData(Uri.fromParts("package", str, null));
                intent.addFlags(335544320);
                PendingIntent activityAsUser = PendingIntent.getActivityAsUser(this.mContext, 0, intent, 201326592, null, UserHandle.of(UserHandle.getUserId(i)));
                boolean hasForegroundServices = this.mBgController.hasForegroundServices(str, i);
                boolean hasForegroundServiceNotifications = this.mBgController.hasForegroundServiceNotifications(str, i);
                if (!this.mBgController.mConstantsObserver.mBgPromptFgsWithNotiToBgRestricted && hasForegroundServices && hasForegroundServiceNotifications) {
                    return;
                }
                postNotificationIfNecessary(0, 17040909, 17040891, activityAsUser, str, i, null);
            }
        }

        public void postLongRunningFgsIfNecessary(String str, int i) {
            FrameworkStatsLog.write((int) FrameworkStatsLog.APP_BACKGROUND_RESTRICTIONS_INFO, i, this.mBgController.getRestrictionLevel(i), 0, 3, this.mInjector.getAppFGSTracker().getTrackerInfoForStatsd(i), (byte[]) null, (byte[]) null, (byte[]) null, PowerExemptionManager.getExemptionReasonForStatsd(this.mBgController.getBackgroundRestrictionExemptionReason(i)), 0, 0, ActivityManager.isLowRamDeviceStatic(), this.mBgController.getRestrictionLevel(i));
            if (this.mBgController.mConstantsObserver.mBgPromptFgsOnLongRunning) {
                if (!this.mBgController.mConstantsObserver.mBgPromptFgsWithNotiOnLongRunning && this.mBgController.hasForegroundServiceNotifications(str, i)) {
                    return;
                }
                Intent intent = new Intent("android.intent.action.SHOW_FOREGROUND_SERVICE_MANAGER");
                intent.addFlags(16777216);
                postNotificationIfNecessary(1, 17040910, 17040892, PendingIntent.getBroadcastAsUser(this.mContext, 0, intent, 201326592, UserHandle.SYSTEM), str, i, null);
            }
        }

        public long getNotificationMinInterval(int i) {
            if (i != 0) {
                if (i != 1) {
                    return 0L;
                }
                return this.mBgController.mConstantsObserver.mBgLongFgsNotificationMinIntervalMs;
            }
            return this.mBgController.mConstantsObserver.mBgAbusiveNotificationMinIntervalMs;
        }

        public int getNotificationIdIfNecessary(int i, String str, int i2) {
            synchronized (this.mSettingsLock) {
                RestrictionSettings.PkgSettings restrictionSettingsLocked = this.mBgController.mRestrictionSettings.getRestrictionSettingsLocked(i2, str);
                if (restrictionSettingsLocked == null) {
                    return 0;
                }
                long currentTimeMillis = this.mInjector.currentTimeMillis();
                long lastNotificationTime = restrictionSettingsLocked.getLastNotificationTime(i);
                if (lastNotificationTime == 0 || lastNotificationTime + getNotificationMinInterval(i) <= currentTimeMillis) {
                    restrictionSettingsLocked.setLastNotificationTime(i, currentTimeMillis);
                    int notificationId = restrictionSettingsLocked.getNotificationId(i);
                    if (notificationId <= 0) {
                        notificationId = this.mNotificationIDStepper;
                        this.mNotificationIDStepper = notificationId + 1;
                        restrictionSettingsLocked.setNotificationId(i, notificationId);
                    }
                    return notificationId;
                }
                return 0;
            }
        }

        public void postNotificationIfNecessary(int i, int i2, int i3, PendingIntent pendingIntent, String str, int i4, Notification.Action[] actionArr) {
            int notificationIdIfNecessary = getNotificationIdIfNecessary(i, str, i4);
            if (notificationIdIfNecessary <= 0) {
                return;
            }
            PackageManagerInternal packageManagerInternal = this.mInjector.getPackageManagerInternal();
            PackageManager packageManager = this.mInjector.getPackageManager();
            ApplicationInfo applicationInfo = packageManagerInternal.getApplicationInfo(str, 819200L, 1000, UserHandle.getUserId(i4));
            String string = this.mContext.getString(i2);
            Context context = this.mContext;
            Object[] objArr = new Object[1];
            objArr[0] = applicationInfo != null ? applicationInfo.loadLabel(packageManager) : str;
            postNotification(notificationIdIfNecessary, str, i4, string, context.getString(i3, objArr), applicationInfo != null ? Icon.createWithResource(str, applicationInfo.icon) : null, pendingIntent, actionArr);
        }

        public void postNotification(int i, String str, int i2, String str2, String str3, Icon icon, PendingIntent pendingIntent, Notification.Action[] actionArr) {
            UserHandle of = UserHandle.of(UserHandle.getUserId(i2));
            postSummaryNotification(of);
            Notification.Builder contentIntent = new Notification.Builder(this.mContext, SystemNotificationChannels.ABUSIVE_BACKGROUND_APPS).setAutoCancel(true).setGroup("com.android.app.abusive_bg_apps").setWhen(this.mInjector.currentTimeMillis()).setSmallIcon(17301642).setColor(this.mContext.getColor(17170460)).setContentTitle(str2).setContentText(str3).setContentIntent(pendingIntent);
            if (icon != null) {
                contentIntent.setLargeIcon(icon);
            }
            if (actionArr != null) {
                for (Notification.Action action : actionArr) {
                    contentIntent.addAction(action);
                }
            }
            Notification build = contentIntent.build();
            build.extras.putString("android.intent.extra.PACKAGE_NAME", str);
            this.mNotificationManager.notifyAsUser(null, i, build, of);
        }

        public final void postSummaryNotification(UserHandle userHandle) {
            this.mNotificationManager.notifyAsUser(null, 203105544, new Notification.Builder(this.mContext, SystemNotificationChannels.ABUSIVE_BACKGROUND_APPS).setGroup("com.android.app.abusive_bg_apps").setGroupSummary(true).setStyle(new Notification.BigTextStyle()).setSmallIcon(17301642).setColor(this.mContext.getColor(17170460)).build(), userHandle);
        }

        public void cancelRequestBgRestrictedIfNecessary(String str, int i) {
            int notificationId;
            synchronized (this.mSettingsLock) {
                RestrictionSettings.PkgSettings restrictionSettingsLocked = this.mBgController.mRestrictionSettings.getRestrictionSettingsLocked(i, str);
                if (restrictionSettingsLocked != null && (notificationId = restrictionSettingsLocked.getNotificationId(0)) > 0) {
                    this.mNotificationManager.cancel(notificationId);
                }
            }
        }

        public void cancelLongRunningFGSNotificationIfNecessary(String str, int i) {
            int notificationId;
            synchronized (this.mSettingsLock) {
                RestrictionSettings.PkgSettings restrictionSettingsLocked = this.mBgController.mRestrictionSettings.getRestrictionSettingsLocked(i, str);
                if (restrictionSettingsLocked != null && (notificationId = restrictionSettingsLocked.getNotificationId(1)) > 0) {
                    this.mNotificationManager.cancel(notificationId);
                }
            }
        }
    }

    public void handleUidInactive(int i, boolean z) {
        ArrayList<Runnable> arrayList = this.mTmpRunnables;
        synchronized (this.mSettingsLock) {
            int indexOfKey = this.mActiveUids.indexOfKey(i);
            if (indexOfKey < 0) {
                return;
            }
            int numElementsForKeyAt = this.mActiveUids.numElementsForKeyAt(indexOfKey);
            for (int i2 = 0; i2 < numElementsForKeyAt; i2++) {
                Runnable runnable = (Runnable) this.mActiveUids.valueAt(indexOfKey, i2);
                if (runnable != null) {
                    arrayList.add(runnable);
                }
            }
            this.mActiveUids.deleteAt(indexOfKey);
            int size = arrayList.size();
            for (int i3 = 0; i3 < size; i3++) {
                arrayList.get(i3).run();
            }
            arrayList.clear();
        }
    }

    public void handleUidActive(final int i) {
        synchronized (this.mSettingsLock) {
            final AppStandbyInternal appStandbyInternal = this.mInjector.getAppStandbyInternal();
            final int userId = UserHandle.getUserId(i);
            this.mRestrictionSettings.forEachPackageInUidLocked(i, new TriConsumer() { // from class: com.android.server.am.AppRestrictionController$$ExternalSyntheticLambda4
                public final void accept(Object obj, Object obj2, Object obj3) {
                    AppRestrictionController.this.lambda$handleUidActive$9(i, appStandbyInternal, userId, (String) obj, (Integer) obj2, (Integer) obj3);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$handleUidActive$9(int i, final AppStandbyInternal appStandbyInternal, final int i2, final String str, Integer num, final Integer num2) {
        if (this.mConstantsObserver.mBgAutoRestrictedBucket && num.intValue() == 50) {
            this.mActiveUids.add(i, str, new Runnable() { // from class: com.android.server.am.AppRestrictionController$$ExternalSyntheticLambda5
                @Override // java.lang.Runnable
                public final void run() {
                    AppRestrictionController.lambda$handleUidActive$8(appStandbyInternal, str, i2, num2);
                }
            });
        } else {
            this.mActiveUids.add(i, str, (Object) null);
        }
    }

    public static /* synthetic */ void lambda$handleUidActive$8(AppStandbyInternal appStandbyInternal, String str, int i, Integer num) {
        appStandbyInternal.restrictApp(str, i, num.intValue() & 65280, num.intValue() & 255);
    }

    public boolean isOnDeviceIdleAllowlist(int i) {
        int appId = UserHandle.getAppId(i);
        return Arrays.binarySearch(this.mDeviceIdleAllowlist, appId) >= 0 || Arrays.binarySearch(this.mDeviceIdleExceptIdleAllowlist, appId) >= 0;
    }

    public boolean isOnSystemDeviceIdleAllowlist(int i) {
        int appId = UserHandle.getAppId(i);
        return this.mSystemDeviceIdleAllowlist.contains(Integer.valueOf(appId)) || this.mSystemDeviceIdleExceptIdleAllowlist.contains(Integer.valueOf(appId));
    }

    public void setDeviceIdleAllowlist(int[] iArr, int[] iArr2) {
        this.mDeviceIdleAllowlist = iArr;
        this.mDeviceIdleExceptIdleAllowlist = iArr2;
    }

    public int getBackgroundRestrictionExemptionReason(int i) {
        int potentialSystemExemptionReason = getPotentialSystemExemptionReason(i);
        if (potentialSystemExemptionReason != -1) {
            return potentialSystemExemptionReason;
        }
        String[] packagesForUid = this.mInjector.getPackageManager().getPackagesForUid(i);
        if (packagesForUid != null) {
            for (String str : packagesForUid) {
                int potentialSystemExemptionReason2 = getPotentialSystemExemptionReason(i, str);
                if (potentialSystemExemptionReason2 != -1) {
                    return potentialSystemExemptionReason2;
                }
            }
            for (String str2 : packagesForUid) {
                int potentialUserAllowedExemptionReason = getPotentialUserAllowedExemptionReason(i, str2);
                if (potentialUserAllowedExemptionReason != -1) {
                    return potentialUserAllowedExemptionReason;
                }
            }
        }
        return -1;
    }

    public int getPotentialSystemExemptionReason(int i) {
        if (UserHandle.isCore(i)) {
            return 51;
        }
        if (isOnSystemDeviceIdleAllowlist(i)) {
            return 300;
        }
        if (UserManager.isDeviceInDemoMode(this.mContext)) {
            return 63;
        }
        if (this.mInjector.getUserManagerInternal().hasUserRestriction("no_control_apps", UserHandle.getUserId(i))) {
            return 323;
        }
        ActivityManagerInternal activityManagerInternal = this.mInjector.getActivityManagerInternal();
        if (activityManagerInternal.isDeviceOwner(i)) {
            return 55;
        }
        if (activityManagerInternal.isProfileOwner(i)) {
            return 56;
        }
        int uidProcessState = activityManagerInternal.getUidProcessState(i);
        if (uidProcessState <= 0) {
            return 10;
        }
        return uidProcessState <= 1 ? 11 : -1;
    }

    public int getPotentialSystemExemptionReason(int i, String str) {
        PackageManagerInternal packageManagerInternal = this.mInjector.getPackageManagerInternal();
        AppStandbyInternal appStandbyInternal = this.mInjector.getAppStandbyInternal();
        AppOpsManager appOpsManager = this.mInjector.getAppOpsManager();
        int userId = UserHandle.getUserId(i);
        if (isSystemModule(str)) {
            return 320;
        }
        if (isCarrierApp(str)) {
            return 321;
        }
        if (isExemptedFromSysConfig(str) || this.mConstantsObserver.mBgRestrictionExemptedPackages.contains(str)) {
            return 300;
        }
        if (packageManagerInternal.isPackageStateProtected(str, userId)) {
            return 322;
        }
        if (appStandbyInternal.isActiveDeviceAdmin(str, userId)) {
            return FrameworkStatsLog.f56x60da79b1;
        }
        if (this.mActivityManagerService.mConstants.mFlagSystemExemptPowerRestrictionsEnabled && appOpsManager.checkOpNoThrow(128, i, str) == 0) {
            return FrameworkStatsLog.TIF_TUNE_CHANGED;
        }
        return -1;
    }

    public int getPotentialUserAllowedExemptionReason(int i, String str) {
        AppOpsManager appOpsManager = this.mInjector.getAppOpsManager();
        if (appOpsManager.checkOpNoThrow(47, i, str) == 0) {
            return 68;
        }
        if (appOpsManager.checkOpNoThrow(94, i, str) == 0) {
            return 69;
        }
        if (isRoleHeldByUid("android.app.role.DIALER", i)) {
            return FrameworkStatsLog.f106xb8c45718;
        }
        if (isRoleHeldByUid("android.app.role.EMERGENCY", i)) {
            return FrameworkStatsLog.f107x9a09c896;
        }
        if (isOnDeviceIdleAllowlist(i)) {
            return 65;
        }
        return this.mInjector.getActivityManagerInternal().isAssociatedCompanionApp(UserHandle.getUserId(i), i) ? 57 : -1;
    }

    public final boolean isCarrierApp(String str) {
        synchronized (this.mCarrierPrivilegedLock) {
            if (this.mCarrierPrivilegedApps == null) {
                fetchCarrierPrivilegedAppsCPL();
            }
            List<String> list = this.mCarrierPrivilegedApps;
            if (list != null) {
                return list.contains(str);
            }
            return false;
        }
    }

    public final void clearCarrierPrivilegedApps() {
        synchronized (this.mCarrierPrivilegedLock) {
            this.mCarrierPrivilegedApps = null;
        }
    }

    @GuardedBy({"mCarrierPrivilegedLock"})
    public final void fetchCarrierPrivilegedAppsCPL() {
        this.mCarrierPrivilegedApps = this.mInjector.getTelephonyManager().getCarrierPrivilegedPackagesForAllActiveSubscriptions();
    }

    public final boolean isRoleHeldByUid(String str, int i) {
        boolean z;
        synchronized (this.mLock) {
            ArrayList<String> arrayList = this.mUidRolesMapping.get(i);
            z = arrayList != null && arrayList.indexOf(str) >= 0;
        }
        return z;
    }

    public final void initRolesInInterest() {
        String[] strArr;
        int[] userIds = this.mInjector.getUserManagerInternal().getUserIds();
        for (String str : ROLES_IN_INTEREST) {
            if (this.mInjector.getRoleManager().isRoleAvailable(str)) {
                for (int i : userIds) {
                    onRoleHoldersChanged(str, UserHandle.of(i));
                }
            }
        }
    }

    public final void onRoleHoldersChanged(String str, UserHandle userHandle) {
        List<String> roleHoldersAsUser = this.mInjector.getRoleManager().getRoleHoldersAsUser(str, userHandle);
        ArraySet arraySet = new ArraySet();
        int identifier = userHandle.getIdentifier();
        if (roleHoldersAsUser != null) {
            PackageManagerInternal packageManagerInternal = this.mInjector.getPackageManagerInternal();
            for (String str2 : roleHoldersAsUser) {
                arraySet.add(Integer.valueOf(packageManagerInternal.getPackageUid(str2, 819200L, identifier)));
            }
        }
        synchronized (this.mLock) {
            for (int size = this.mUidRolesMapping.size() - 1; size >= 0; size--) {
                int keyAt = this.mUidRolesMapping.keyAt(size);
                if (UserHandle.getUserId(keyAt) == identifier) {
                    ArrayList<String> valueAt = this.mUidRolesMapping.valueAt(size);
                    int indexOf = valueAt.indexOf(str);
                    boolean contains = arraySet.contains(Integer.valueOf(keyAt));
                    if (indexOf >= 0) {
                        if (!contains) {
                            valueAt.remove(indexOf);
                            if (valueAt.isEmpty()) {
                                this.mUidRolesMapping.removeAt(size);
                            }
                        }
                    } else if (contains) {
                        valueAt.add(str);
                        arraySet.remove(Integer.valueOf(keyAt));
                    }
                }
            }
            for (int size2 = arraySet.size() - 1; size2 >= 0; size2--) {
                ArrayList<String> arrayList = new ArrayList<>();
                arrayList.add(str);
                this.mUidRolesMapping.put(((Integer) arraySet.valueAt(size2)).intValue(), arrayList);
            }
        }
    }

    public Handler getBackgroundHandler() {
        return this.mBgHandler;
    }

    @VisibleForTesting
    public HandlerThread getBackgroundHandlerThread() {
        return this.mBgHandlerThread;
    }

    public Object getLock() {
        return this.mLock;
    }

    @VisibleForTesting
    public void addAppStateTracker(BaseAppStateTracker baseAppStateTracker) {
        this.mAppStateTrackers.add(baseAppStateTracker);
    }

    public <T extends BaseAppStateTracker> T getAppStateTracker(Class<T> cls) {
        Iterator<BaseAppStateTracker> it = this.mAppStateTrackers.iterator();
        while (it.hasNext()) {
            T t = (T) it.next();
            if (cls.isAssignableFrom(t.getClass())) {
                return t;
            }
        }
        return null;
    }

    public void postLongRunningFgsIfNecessary(String str, int i) {
        this.mNotificationHelper.postLongRunningFgsIfNecessary(str, i);
    }

    public void cancelLongRunningFGSNotificationIfNecessary(String str, int i) {
        this.mNotificationHelper.cancelLongRunningFGSNotificationIfNecessary(str, i);
    }

    public String getPackageName(int i) {
        return this.mInjector.getPackageName(i);
    }

    /* renamed from: com.android.server.am.AppRestrictionController$BgHandler */
    /* loaded from: classes.dex */
    public static class BgHandler extends Handler {
        public final Injector mInjector;

        public BgHandler(Looper looper, Injector injector) {
            super(looper);
            this.mInjector = injector;
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            AppRestrictionController appRestrictionController = this.mInjector.getAppRestrictionController();
            switch (message.what) {
                case 0:
                    appRestrictionController.handleBackgroundRestrictionChanged(message.arg1, (String) message.obj, message.arg2 == 1);
                    return;
                case 1:
                    appRestrictionController.dispatchAppRestrictionLevelChanges(message.arg1, (String) message.obj, message.arg2);
                    return;
                case 2:
                    appRestrictionController.handleAppStandbyBucketChanged(message.arg2, (String) message.obj, message.arg1);
                    return;
                case 3:
                    appRestrictionController.onUserInteractionStarted((String) message.obj, message.arg1);
                    return;
                case 4:
                    appRestrictionController.handleRequestBgRestricted((String) message.obj, message.arg1);
                    return;
                case 5:
                    appRestrictionController.handleUidInactive(message.arg1, message.arg2 == 1);
                    return;
                case 6:
                    appRestrictionController.handleUidActive(message.arg1);
                    return;
                case 7:
                    appRestrictionController.handleUidInactive(message.arg1, message.arg2 == 1);
                    appRestrictionController.handleUidGone(message.arg1);
                    return;
                case 8:
                    appRestrictionController.handleUidProcStateChanged(message.arg1, message.arg2);
                    return;
                case 9:
                    appRestrictionController.handleCancelRequestBgRestricted((String) message.obj, message.arg1);
                    return;
                case 10:
                    appRestrictionController.mRestrictionSettings.loadFromXml(true);
                    return;
                case 11:
                    appRestrictionController.mRestrictionSettings.persistToXml(message.arg1);
                    return;
                default:
                    return;
            }
        }
    }

    /* renamed from: com.android.server.am.AppRestrictionController$Injector */
    /* loaded from: classes.dex */
    public static class Injector {
        public ActivityManagerInternal mActivityManagerInternal;
        public AppBatteryExemptionTracker mAppBatteryExemptionTracker;
        public AppBatteryTracker mAppBatteryTracker;
        public AppFGSTracker mAppFGSTracker;
        public AppHibernationManagerInternal mAppHibernationInternal;
        public AppMediaSessionTracker mAppMediaSessionTracker;
        public AppOpsManager mAppOpsManager;
        public AppPermissionTracker mAppPermissionTracker;
        public AppRestrictionController mAppRestrictionController;
        public AppStandbyInternal mAppStandbyInternal;
        public AppStateTracker mAppStateTracker;
        public final Context mContext;
        public NotificationManager mNotificationManager;
        public PackageManagerInternal mPackageManagerInternal;
        public RoleManager mRoleManager;
        public TelephonyManager mTelephonyManager;
        public UserManagerInternal mUserManagerInternal;

        public boolean isTest() {
            return false;
        }

        public Injector(Context context) {
            this.mContext = context;
        }

        public Context getContext() {
            return this.mContext;
        }

        public void initAppStateTrackers(AppRestrictionController appRestrictionController) {
            this.mAppRestrictionController = appRestrictionController;
            this.mAppBatteryTracker = new AppBatteryTracker(this.mContext, appRestrictionController);
            this.mAppBatteryExemptionTracker = new AppBatteryExemptionTracker(this.mContext, appRestrictionController);
            this.mAppFGSTracker = new AppFGSTracker(this.mContext, appRestrictionController);
            this.mAppMediaSessionTracker = new AppMediaSessionTracker(this.mContext, appRestrictionController);
            this.mAppPermissionTracker = new AppPermissionTracker(this.mContext, appRestrictionController);
            appRestrictionController.mAppStateTrackers.add(this.mAppBatteryTracker);
            appRestrictionController.mAppStateTrackers.add(this.mAppBatteryExemptionTracker);
            appRestrictionController.mAppStateTrackers.add(this.mAppFGSTracker);
            appRestrictionController.mAppStateTrackers.add(this.mAppMediaSessionTracker);
            appRestrictionController.mAppStateTrackers.add(this.mAppPermissionTracker);
            appRestrictionController.mAppStateTrackers.add(new AppBroadcastEventsTracker(this.mContext, appRestrictionController));
            appRestrictionController.mAppStateTrackers.add(new AppBindServiceEventsTracker(this.mContext, appRestrictionController));
        }

        public ActivityManagerInternal getActivityManagerInternal() {
            if (this.mActivityManagerInternal == null) {
                this.mActivityManagerInternal = (ActivityManagerInternal) LocalServices.getService(ActivityManagerInternal.class);
            }
            return this.mActivityManagerInternal;
        }

        public AppRestrictionController getAppRestrictionController() {
            return this.mAppRestrictionController;
        }

        public AppOpsManager getAppOpsManager() {
            if (this.mAppOpsManager == null) {
                this.mAppOpsManager = (AppOpsManager) getContext().getSystemService(AppOpsManager.class);
            }
            return this.mAppOpsManager;
        }

        public AppStandbyInternal getAppStandbyInternal() {
            if (this.mAppStandbyInternal == null) {
                this.mAppStandbyInternal = (AppStandbyInternal) LocalServices.getService(AppStandbyInternal.class);
            }
            return this.mAppStandbyInternal;
        }

        public AppHibernationManagerInternal getAppHibernationInternal() {
            if (this.mAppHibernationInternal == null) {
                this.mAppHibernationInternal = (AppHibernationManagerInternal) LocalServices.getService(AppHibernationManagerInternal.class);
            }
            return this.mAppHibernationInternal;
        }

        public AppStateTracker getAppStateTracker() {
            if (this.mAppStateTracker == null) {
                this.mAppStateTracker = (AppStateTracker) LocalServices.getService(AppStateTracker.class);
            }
            return this.mAppStateTracker;
        }

        public IActivityManager getIActivityManager() {
            return ActivityManager.getService();
        }

        public UserManagerInternal getUserManagerInternal() {
            if (this.mUserManagerInternal == null) {
                this.mUserManagerInternal = (UserManagerInternal) LocalServices.getService(UserManagerInternal.class);
            }
            return this.mUserManagerInternal;
        }

        public PackageManagerInternal getPackageManagerInternal() {
            if (this.mPackageManagerInternal == null) {
                this.mPackageManagerInternal = (PackageManagerInternal) LocalServices.getService(PackageManagerInternal.class);
            }
            return this.mPackageManagerInternal;
        }

        public PackageManager getPackageManager() {
            return getContext().getPackageManager();
        }

        public NotificationManager getNotificationManager() {
            if (this.mNotificationManager == null) {
                this.mNotificationManager = (NotificationManager) getContext().getSystemService(NotificationManager.class);
            }
            return this.mNotificationManager;
        }

        public RoleManager getRoleManager() {
            if (this.mRoleManager == null) {
                this.mRoleManager = (RoleManager) getContext().getSystemService(RoleManager.class);
            }
            return this.mRoleManager;
        }

        public TelephonyManager getTelephonyManager() {
            if (this.mTelephonyManager == null) {
                this.mTelephonyManager = (TelephonyManager) getContext().getSystemService(TelephonyManager.class);
            }
            return this.mTelephonyManager;
        }

        public AppFGSTracker getAppFGSTracker() {
            return this.mAppFGSTracker;
        }

        public AppMediaSessionTracker getAppMediaSessionTracker() {
            return this.mAppMediaSessionTracker;
        }

        public ActivityManagerService getActivityManagerService() {
            return this.mAppRestrictionController.mActivityManagerService;
        }

        public UidBatteryUsageProvider getUidBatteryUsageProvider() {
            return this.mAppBatteryTracker;
        }

        public AppBatteryExemptionTracker getAppBatteryExemptionTracker() {
            return this.mAppBatteryExemptionTracker;
        }

        public String getPackageName(int i) {
            ApplicationInfo applicationInfo;
            ActivityManagerService activityManagerService = getActivityManagerService();
            synchronized (activityManagerService.mPidsSelfLocked) {
                ProcessRecord processRecord = activityManagerService.mPidsSelfLocked.get(i);
                if (processRecord == null || (applicationInfo = processRecord.info) == null) {
                    return null;
                }
                return applicationInfo.packageName;
            }
        }

        public void scheduleInitTrackers(Handler handler, Runnable runnable) {
            handler.post(runnable);
        }

        public File getDataSystemDeDirectory(int i) {
            return Environment.getDataSystemDeDirectory(i);
        }

        public long currentTimeMillis() {
            return System.currentTimeMillis();
        }
    }

    public final void registerForSystemBroadcasts() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.PACKAGE_ADDED");
        intentFilter.addAction("android.intent.action.PACKAGE_CHANGED");
        intentFilter.addAction("android.intent.action.PACKAGE_FULLY_REMOVED");
        intentFilter.addDataScheme("package");
        this.mContext.registerReceiverForAllUsers(this.mBroadcastReceiver, intentFilter, null, this.mBgHandler);
        IntentFilter intentFilter2 = new IntentFilter();
        intentFilter2.addAction("android.intent.action.USER_ADDED");
        intentFilter2.addAction("android.intent.action.USER_REMOVED");
        intentFilter2.addAction("android.intent.action.UID_REMOVED");
        this.mContext.registerReceiverForAllUsers(this.mBroadcastReceiver, intentFilter2, null, this.mBgHandler);
        IntentFilter intentFilter3 = new IntentFilter();
        intentFilter3.addAction("android.intent.action.LOCKED_BOOT_COMPLETED");
        this.mContext.registerReceiverAsUser(this.mBootReceiver, UserHandle.SYSTEM, intentFilter3, null, this.mBgHandler);
    }

    public final void unregisterForSystemBroadcasts() {
        this.mContext.unregisterReceiver(this.mBroadcastReceiver);
        this.mContext.unregisterReceiver(this.mBootReceiver);
    }

    public void forEachTracker(Consumer<BaseAppStateTracker> consumer) {
        int size = this.mAppStateTrackers.size();
        for (int i = 0; i < size; i++) {
            consumer.accept(this.mAppStateTrackers.get(i));
        }
    }

    public final void onUserAdded(int i) {
        int size = this.mAppStateTrackers.size();
        for (int i2 = 0; i2 < size; i2++) {
            this.mAppStateTrackers.get(i2).onUserAdded(i);
        }
    }

    public final void onUserStarted(int i) {
        refreshAppRestrictionLevelForUser(i, 1024, 2);
        int size = this.mAppStateTrackers.size();
        for (int i2 = 0; i2 < size; i2++) {
            this.mAppStateTrackers.get(i2).onUserStarted(i);
        }
    }

    public final void onUserStopped(int i) {
        int size = this.mAppStateTrackers.size();
        for (int i2 = 0; i2 < size; i2++) {
            this.mAppStateTrackers.get(i2).onUserStopped(i);
        }
    }

    public final void onUserRemoved(int i) {
        int size = this.mAppStateTrackers.size();
        for (int i2 = 0; i2 < size; i2++) {
            this.mAppStateTrackers.get(i2).onUserRemoved(i);
        }
        this.mRestrictionSettings.removeUser(i);
    }

    public final void onUidAdded(int i) {
        refreshAppRestrictionLevelForUid(i, FrameworkStatsLog.APP_STANDBY_BUCKET_CHANGED__MAIN_REASON__MAIN_FORCED_BY_SYSTEM, 0, false);
        int size = this.mAppStateTrackers.size();
        for (int i2 = 0; i2 < size; i2++) {
            this.mAppStateTrackers.get(i2).onUidAdded(i);
        }
    }

    public final void onPackageRemoved(String str, int i) {
        this.mRestrictionSettings.removePackage(str, i);
    }

    public final void onUidRemoved(int i) {
        int size = this.mAppStateTrackers.size();
        for (int i2 = 0; i2 < size; i2++) {
            this.mAppStateTrackers.get(i2).onUidRemoved(i);
        }
        this.mRestrictionSettings.removeUid(i);
    }

    public final void onLockedBootCompleted() {
        int size = this.mAppStateTrackers.size();
        for (int i = 0; i < size; i++) {
            this.mAppStateTrackers.get(i).onLockedBootCompleted();
        }
    }

    public boolean isBgAutoRestrictedBucketFeatureFlagEnabled() {
        return this.mConstantsObserver.mBgAutoRestrictedBucket;
    }

    public final void onPropertiesChanged(String str) {
        int size = this.mAppStateTrackers.size();
        for (int i = 0; i < size; i++) {
            this.mAppStateTrackers.get(i).onPropertiesChanged(str);
        }
    }

    public final void onUserInteractionStarted(String str, int i) {
        int packageUid = this.mInjector.getPackageManagerInternal().getPackageUid(str, 819200L, i);
        int size = this.mAppStateTrackers.size();
        for (int i2 = 0; i2 < size; i2++) {
            this.mAppStateTrackers.get(i2).onUserInteractionStarted(str, packageUid);
        }
    }
}
