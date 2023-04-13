package com.android.server.usage;

import android.app.usage.ConfigurationStats;
import android.app.usage.EventList;
import android.app.usage.EventStats;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStats;
import android.content.res.Configuration;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.Slog;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.util.proto.ProtoInputStream;
import com.android.internal.annotations.VisibleForTesting;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
/* loaded from: classes2.dex */
public class IntervalStats {
    public Configuration activeConfiguration;
    public long beginTime;
    public long endTime;
    public long lastTimeSaved;
    public int majorVersion = 1;
    public int minorVersion = 1;
    public final EventTracker interactiveTracker = new EventTracker();
    public final EventTracker nonInteractiveTracker = new EventTracker();
    public final EventTracker keyguardShownTracker = new EventTracker();
    public final EventTracker keyguardHiddenTracker = new EventTracker();
    public final ArrayMap<String, UsageStats> packageStats = new ArrayMap<>();
    public final SparseArray<UsageStats> packageStatsObfuscated = new SparseArray<>();
    public final ArrayMap<Configuration, ConfigurationStats> configurations = new ArrayMap<>();
    public final EventList events = new EventList();
    public final ArraySet<String> mStringCache = new ArraySet<>();

    /* loaded from: classes2.dex */
    public static final class EventTracker {
        public int count;
        public long curStartTime;
        public long duration;
        public long lastEventTime;

        public void commitTime(long j) {
            long j2 = this.curStartTime;
            if (j2 != 0) {
                this.duration += j - j2;
                this.curStartTime = 0L;
            }
        }

        public void update(long j) {
            if (this.curStartTime == 0) {
                this.count++;
            }
            commitTime(j);
            this.curStartTime = j;
            this.lastEventTime = j;
        }

        public void addToEventStats(List<EventStats> list, int i, long j, long j2) {
            if (this.count == 0 && this.duration == 0) {
                return;
            }
            EventStats eventStats = new EventStats();
            eventStats.mEventType = i;
            eventStats.mCount = this.count;
            eventStats.mTotalTime = this.duration;
            eventStats.mLastEventTime = this.lastEventTime;
            eventStats.mBeginTimeStamp = j;
            eventStats.mEndTimeStamp = j2;
            list.add(eventStats);
        }
    }

    public UsageStats getOrCreateUsageStats(String str) {
        UsageStats usageStats = this.packageStats.get(str);
        if (usageStats == null) {
            UsageStats usageStats2 = new UsageStats();
            String cachedStringRef = getCachedStringRef(str);
            usageStats2.mPackageName = cachedStringRef;
            usageStats2.mBeginTimeStamp = this.beginTime;
            usageStats2.mEndTimeStamp = this.endTime;
            this.packageStats.put(cachedStringRef, usageStats2);
            return usageStats2;
        }
        return usageStats;
    }

    public ConfigurationStats getOrCreateConfigurationStats(Configuration configuration) {
        ConfigurationStats configurationStats = this.configurations.get(configuration);
        if (configurationStats == null) {
            ConfigurationStats configurationStats2 = new ConfigurationStats();
            configurationStats2.mBeginTimeStamp = this.beginTime;
            configurationStats2.mEndTimeStamp = this.endTime;
            configurationStats2.mConfiguration = configuration;
            this.configurations.put(configuration, configurationStats2);
            return configurationStats2;
        }
        return configurationStats;
    }

    public UsageEvents.Event buildEvent(String str, String str2) {
        UsageEvents.Event event = new UsageEvents.Event();
        event.mPackage = getCachedStringRef(str);
        if (str2 != null) {
            event.mClass = getCachedStringRef(str2);
        }
        return event;
    }

    public UsageEvents.Event buildEvent(ProtoInputStream protoInputStream, List<String> list) throws IOException {
        UsageEvents.Event event = new UsageEvents.Event();
        while (true) {
            switch (protoInputStream.nextField()) {
                case -1:
                    int i = event.mEventType;
                    if (i != 5) {
                        if (i != 8) {
                            if (i == 12) {
                                if (event.mNotificationChannelId == null) {
                                    event.mNotificationChannelId = "";
                                }
                            } else if (i == 30 && event.mLocusId == null) {
                                event.mLocusId = "";
                            }
                        } else if (event.mShortcutId == null) {
                            event.mShortcutId = "";
                        }
                    } else if (event.mConfiguration == null) {
                        event.mConfiguration = new Configuration();
                    }
                    return event;
                case 1:
                    event.mPackage = getCachedStringRef(protoInputStream.readString(1138166333441L));
                    break;
                case 2:
                    event.mPackage = getCachedStringRef(list.get(protoInputStream.readInt(1120986464258L) - 1));
                    break;
                case 3:
                    event.mClass = getCachedStringRef(protoInputStream.readString(1138166333443L));
                    break;
                case 4:
                    event.mClass = getCachedStringRef(list.get(protoInputStream.readInt(1120986464260L) - 1));
                    break;
                case 5:
                    event.mTimeStamp = this.beginTime + protoInputStream.readLong(1112396529669L);
                    break;
                case 6:
                    event.mFlags = protoInputStream.readInt(1120986464262L);
                    break;
                case 7:
                    event.mEventType = protoInputStream.readInt(1120986464263L);
                    break;
                case 8:
                    Configuration configuration = new Configuration();
                    event.mConfiguration = configuration;
                    configuration.readFromProto(protoInputStream, 1146756268040L);
                    break;
                case 9:
                    event.mShortcutId = protoInputStream.readString(1138166333449L).intern();
                    break;
                case 11:
                    event.mBucketAndReason = protoInputStream.readInt(1120986464267L);
                    break;
                case 12:
                    event.mNotificationChannelId = protoInputStream.readString(1138166333452L);
                    break;
                case 13:
                    event.mNotificationChannelId = getCachedStringRef(list.get(protoInputStream.readInt(1120986464269L) - 1));
                    break;
                case 14:
                    event.mInstanceId = protoInputStream.readInt(1120986464270L);
                    break;
                case 15:
                    event.mTaskRootPackage = getCachedStringRef(list.get(protoInputStream.readInt(1120986464271L) - 1));
                    break;
                case 16:
                    event.mTaskRootClass = getCachedStringRef(list.get(protoInputStream.readInt(1120986464272L) - 1));
                    break;
                case 17:
                    event.mLocusId = getCachedStringRef(list.get(protoInputStream.readInt(1120986464273L) - 1));
                    break;
            }
        }
    }

    @VisibleForTesting
    public void update(String str, String str2, long j, int i, int i2) {
        if (i == 26 || i == 25) {
            int size = this.packageStats.size();
            for (int i3 = 0; i3 < size; i3++) {
                this.packageStats.valueAt(i3).update(null, j, i, i2);
            }
        } else {
            getOrCreateUsageStats(str).update(str2, j, i, i2);
        }
        if (j > this.endTime) {
            this.endTime = j;
        }
    }

    @VisibleForTesting
    public void addEvent(UsageEvents.Event event) {
        event.mPackage = getCachedStringRef(event.mPackage);
        String str = event.mClass;
        if (str != null) {
            event.mClass = getCachedStringRef(str);
        }
        String str2 = event.mTaskRootPackage;
        if (str2 != null) {
            event.mTaskRootPackage = getCachedStringRef(str2);
        }
        String str3 = event.mTaskRootClass;
        if (str3 != null) {
            event.mTaskRootClass = getCachedStringRef(str3);
        }
        if (event.mEventType == 12) {
            event.mNotificationChannelId = getCachedStringRef(event.mNotificationChannelId);
        }
        this.events.insert(event);
        long j = event.mTimeStamp;
        if (j > this.endTime) {
            this.endTime = j;
        }
    }

    public void updateChooserCounts(String str, String str2, String str3) {
        ArrayMap arrayMap;
        UsageStats orCreateUsageStats = getOrCreateUsageStats(str);
        if (orCreateUsageStats.mChooserCounts == null) {
            orCreateUsageStats.mChooserCounts = new ArrayMap();
        }
        int indexOfKey = orCreateUsageStats.mChooserCounts.indexOfKey(str3);
        if (indexOfKey < 0) {
            arrayMap = new ArrayMap();
            orCreateUsageStats.mChooserCounts.put(str3, arrayMap);
        } else {
            arrayMap = (ArrayMap) orCreateUsageStats.mChooserCounts.valueAt(indexOfKey);
        }
        arrayMap.put(str2, Integer.valueOf(((Integer) arrayMap.getOrDefault(str2, 0)).intValue() + 1));
    }

    public void updateConfigurationStats(Configuration configuration, long j) {
        Configuration configuration2 = this.activeConfiguration;
        if (configuration2 != null) {
            ConfigurationStats configurationStats = this.configurations.get(configuration2);
            configurationStats.mTotalTimeActive += j - configurationStats.mLastTimeActive;
            configurationStats.mLastTimeActive = j - 1;
        }
        if (configuration != null) {
            ConfigurationStats orCreateConfigurationStats = getOrCreateConfigurationStats(configuration);
            orCreateConfigurationStats.mLastTimeActive = j;
            orCreateConfigurationStats.mActivationCount++;
            this.activeConfiguration = orCreateConfigurationStats.mConfiguration;
        }
        if (j > this.endTime) {
            this.endTime = j;
        }
    }

    public void incrementAppLaunchCount(String str) {
        getOrCreateUsageStats(str).mAppLaunchCount++;
    }

    public void commitTime(long j) {
        this.interactiveTracker.commitTime(j);
        this.nonInteractiveTracker.commitTime(j);
        this.keyguardShownTracker.commitTime(j);
        this.keyguardHiddenTracker.commitTime(j);
    }

    public void updateScreenInteractive(long j) {
        this.interactiveTracker.update(j);
        this.nonInteractiveTracker.commitTime(j);
    }

    public void updateScreenNonInteractive(long j) {
        this.nonInteractiveTracker.update(j);
        this.interactiveTracker.commitTime(j);
    }

    public void updateKeyguardShown(long j) {
        this.keyguardShownTracker.update(j);
        this.keyguardHiddenTracker.commitTime(j);
    }

    public void updateKeyguardHidden(long j) {
        this.keyguardHiddenTracker.update(j);
        this.keyguardShownTracker.commitTime(j);
    }

    public void addEventStatsTo(List<EventStats> list) {
        this.interactiveTracker.addToEventStats(list, 15, this.beginTime, this.endTime);
        this.nonInteractiveTracker.addToEventStats(list, 16, this.beginTime, this.endTime);
        this.keyguardShownTracker.addToEventStats(list, 17, this.beginTime, this.endTime);
        this.keyguardHiddenTracker.addToEventStats(list, 18, this.beginTime, this.endTime);
    }

    public final String getCachedStringRef(String str) {
        int indexOf = this.mStringCache.indexOf(str);
        if (indexOf < 0) {
            this.mStringCache.add(str);
            return str;
        }
        return this.mStringCache.valueAt(indexOf);
    }

    public void upgradeIfNeeded() {
        if (this.majorVersion >= 1) {
            return;
        }
        this.majorVersion = 1;
    }

    public final boolean deobfuscateUsageStats(PackagesTokenData packagesTokenData) {
        PackagesTokenData packagesTokenData2 = packagesTokenData;
        ArraySet arraySet = new ArraySet();
        int size = this.packageStatsObfuscated.size();
        int i = 0;
        boolean z = false;
        while (i < size) {
            int keyAt = this.packageStatsObfuscated.keyAt(i);
            UsageStats valueAt = this.packageStatsObfuscated.valueAt(i);
            String packageString = packagesTokenData2.getPackageString(keyAt);
            valueAt.mPackageName = packageString;
            if (packageString == null) {
                arraySet.add(Integer.valueOf(keyAt));
                z = true;
            } else {
                int size2 = valueAt.mChooserCountsObfuscated.size();
                int i2 = 0;
                while (i2 < size2) {
                    ArrayMap arrayMap = new ArrayMap();
                    String string = packagesTokenData2.getString(keyAt, valueAt.mChooserCountsObfuscated.keyAt(i2));
                    if (string != null) {
                        SparseIntArray sparseIntArray = (SparseIntArray) valueAt.mChooserCountsObfuscated.valueAt(i2);
                        int size3 = sparseIntArray.size();
                        int i3 = 0;
                        while (i3 < size3) {
                            String string2 = packagesTokenData2.getString(keyAt, sparseIntArray.keyAt(i3));
                            if (string2 != null) {
                                arrayMap.put(string2, Integer.valueOf(sparseIntArray.valueAt(i3)));
                            }
                            i3++;
                            packagesTokenData2 = packagesTokenData;
                        }
                        valueAt.mChooserCounts.put(string, arrayMap);
                    }
                    i2++;
                    packagesTokenData2 = packagesTokenData;
                }
                this.packageStats.put(valueAt.mPackageName, valueAt);
            }
            i++;
            packagesTokenData2 = packagesTokenData;
        }
        if (z) {
            Slog.d("IntervalStats", "Unable to parse usage stats packages: " + Arrays.toString(arraySet.toArray()));
        }
        return z;
    }

    public final boolean deobfuscateEvents(PackagesTokenData packagesTokenData) {
        ArraySet arraySet = new ArraySet();
        boolean z = false;
        for (int size = this.events.size() - 1; size >= 0; size--) {
            UsageEvents.Event event = this.events.get(size);
            int i = event.mPackageToken;
            String packageString = packagesTokenData.getPackageString(i);
            event.mPackage = packageString;
            if (packageString == null) {
                arraySet.add(Integer.valueOf(i));
                this.events.remove(size);
            } else {
                int i2 = event.mClassToken;
                if (i2 != -1) {
                    event.mClass = packagesTokenData.getString(i, i2);
                }
                int i3 = event.mTaskRootPackageToken;
                if (i3 != -1) {
                    event.mTaskRootPackage = packagesTokenData.getString(i, i3);
                }
                int i4 = event.mTaskRootClassToken;
                if (i4 != -1) {
                    event.mTaskRootClass = packagesTokenData.getString(i, i4);
                }
                int i5 = event.mEventType;
                if (i5 != 5) {
                    if (i5 == 8) {
                        String string = packagesTokenData.getString(i, event.mShortcutIdToken);
                        event.mShortcutId = string;
                        if (string == null) {
                            Slog.v("IntervalStats", "Unable to parse shortcut " + event.mShortcutIdToken + " for package " + i);
                            this.events.remove(size);
                        }
                    } else if (i5 == 12) {
                        String string2 = packagesTokenData.getString(i, event.mNotificationChannelIdToken);
                        event.mNotificationChannelId = string2;
                        if (string2 == null) {
                            Slog.v("IntervalStats", "Unable to parse notification channel " + event.mNotificationChannelIdToken + " for package " + i);
                            this.events.remove(size);
                        }
                    } else if (i5 == 30) {
                        String string3 = packagesTokenData.getString(i, event.mLocusIdToken);
                        event.mLocusId = string3;
                        if (string3 == null) {
                            Slog.v("IntervalStats", "Unable to parse locus " + event.mLocusIdToken + " for package " + i);
                            this.events.remove(size);
                        }
                    }
                } else if (event.mConfiguration == null) {
                    event.mConfiguration = new Configuration();
                }
            }
            z = true;
        }
        if (z) {
            Slog.d("IntervalStats", "Unable to parse event packages: " + Arrays.toString(arraySet.toArray()));
        }
        return z;
    }

    public boolean deobfuscateData(PackagesTokenData packagesTokenData) {
        return deobfuscateUsageStats(packagesTokenData) || deobfuscateEvents(packagesTokenData);
    }

    public final void obfuscateUsageStatsData(PackagesTokenData packagesTokenData) {
        int packageTokenOrAdd;
        int size = this.packageStats.size();
        for (int i = 0; i < size; i++) {
            String keyAt = this.packageStats.keyAt(i);
            UsageStats valueAt = this.packageStats.valueAt(i);
            if (valueAt != null && (packageTokenOrAdd = packagesTokenData.getPackageTokenOrAdd(keyAt, valueAt.mEndTimeStamp)) != -1) {
                valueAt.mPackageToken = packageTokenOrAdd;
                int size2 = valueAt.mChooserCounts.size();
                for (int i2 = 0; i2 < size2; i2++) {
                    String str = (String) valueAt.mChooserCounts.keyAt(i2);
                    ArrayMap arrayMap = (ArrayMap) valueAt.mChooserCounts.valueAt(i2);
                    if (arrayMap != null) {
                        SparseIntArray sparseIntArray = new SparseIntArray();
                        int size3 = arrayMap.size();
                        for (int i3 = 0; i3 < size3; i3++) {
                            sparseIntArray.put(packagesTokenData.getTokenOrAdd(packageTokenOrAdd, keyAt, (String) arrayMap.keyAt(i3)), ((Integer) arrayMap.valueAt(i3)).intValue());
                        }
                        valueAt.mChooserCountsObfuscated.put(packagesTokenData.getTokenOrAdd(packageTokenOrAdd, keyAt, str), sparseIntArray);
                    }
                }
                this.packageStatsObfuscated.put(packageTokenOrAdd, valueAt);
            }
        }
    }

    public final void obfuscateEventsData(PackagesTokenData packagesTokenData) {
        for (int size = this.events.size() - 1; size >= 0; size--) {
            UsageEvents.Event event = this.events.get(size);
            if (event != null) {
                int packageTokenOrAdd = packagesTokenData.getPackageTokenOrAdd(event.mPackage, event.mTimeStamp);
                if (packageTokenOrAdd == -1) {
                    this.events.remove(size);
                } else {
                    event.mPackageToken = packageTokenOrAdd;
                    if (!TextUtils.isEmpty(event.mClass)) {
                        event.mClassToken = packagesTokenData.getTokenOrAdd(packageTokenOrAdd, event.mPackage, event.mClass);
                    }
                    if (!TextUtils.isEmpty(event.mTaskRootPackage)) {
                        event.mTaskRootPackageToken = packagesTokenData.getTokenOrAdd(packageTokenOrAdd, event.mPackage, event.mTaskRootPackage);
                    }
                    if (!TextUtils.isEmpty(event.mTaskRootClass)) {
                        event.mTaskRootClassToken = packagesTokenData.getTokenOrAdd(packageTokenOrAdd, event.mPackage, event.mTaskRootClass);
                    }
                    int i = event.mEventType;
                    if (i != 8) {
                        if (i == 12) {
                            if (!TextUtils.isEmpty(event.mNotificationChannelId)) {
                                event.mNotificationChannelIdToken = packagesTokenData.getTokenOrAdd(packageTokenOrAdd, event.mPackage, event.mNotificationChannelId);
                            }
                        } else if (i == 30 && !TextUtils.isEmpty(event.mLocusId)) {
                            event.mLocusIdToken = packagesTokenData.getTokenOrAdd(packageTokenOrAdd, event.mPackage, event.mLocusId);
                        }
                    } else if (!TextUtils.isEmpty(event.mShortcutId)) {
                        event.mShortcutIdToken = packagesTokenData.getTokenOrAdd(packageTokenOrAdd, event.mPackage, event.mShortcutId);
                    }
                }
            }
        }
    }

    public void obfuscateData(PackagesTokenData packagesTokenData) {
        obfuscateUsageStatsData(packagesTokenData);
        obfuscateEventsData(packagesTokenData);
    }
}
