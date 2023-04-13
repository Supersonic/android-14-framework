package com.android.server.usage;

import android.app.usage.ConfigurationStats;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStats;
import android.content.res.Configuration;
import android.util.ArrayMap;
import android.util.Slog;
import android.util.proto.ProtoInputStream;
import android.util.proto.ProtoOutputStream;
import com.android.server.usage.IntervalStats;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ProtocolException;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes2.dex */
public final class UsageStatsProto {
    public static String TAG = "UsageStatsProto";

    public static List<String> readStringPool(ProtoInputStream protoInputStream) throws IOException {
        ArrayList arrayList;
        long start = protoInputStream.start(1146756268034L);
        if (protoInputStream.nextField(1120986464257L)) {
            arrayList = new ArrayList(protoInputStream.readInt(1120986464257L));
        } else {
            arrayList = new ArrayList();
        }
        while (protoInputStream.nextField() != -1) {
            if (protoInputStream.getFieldNumber() == 2) {
                arrayList.add(protoInputStream.readString(2237677961218L));
            }
        }
        protoInputStream.end(start);
        return arrayList;
    }

    public static void loadUsageStats(ProtoInputStream protoInputStream, long j, IntervalStats intervalStats, List<String> list) throws IOException {
        UsageStats usageStats;
        UsageStats orCreateUsageStats;
        long start = protoInputStream.start(j);
        if (protoInputStream.nextField(1120986464258L)) {
            usageStats = intervalStats.getOrCreateUsageStats(list.get(protoInputStream.readInt(1120986464258L) - 1));
        } else if (protoInputStream.nextField(1138166333441L)) {
            usageStats = intervalStats.getOrCreateUsageStats(protoInputStream.readString(1138166333441L));
        } else {
            usageStats = new UsageStats();
        }
        while (protoInputStream.nextField() != -1) {
            switch (protoInputStream.getFieldNumber()) {
                case 1:
                    orCreateUsageStats = intervalStats.getOrCreateUsageStats(protoInputStream.readString(1138166333441L));
                    orCreateUsageStats.mLastTimeUsed = usageStats.mLastTimeUsed;
                    orCreateUsageStats.mTotalTimeInForeground = usageStats.mTotalTimeInForeground;
                    orCreateUsageStats.mLastEvent = usageStats.mLastEvent;
                    orCreateUsageStats.mAppLaunchCount = usageStats.mAppLaunchCount;
                    break;
                case 2:
                    orCreateUsageStats = intervalStats.getOrCreateUsageStats(list.get(protoInputStream.readInt(1120986464258L) - 1));
                    orCreateUsageStats.mLastTimeUsed = usageStats.mLastTimeUsed;
                    orCreateUsageStats.mTotalTimeInForeground = usageStats.mTotalTimeInForeground;
                    orCreateUsageStats.mLastEvent = usageStats.mLastEvent;
                    orCreateUsageStats.mAppLaunchCount = usageStats.mAppLaunchCount;
                    break;
                case 3:
                    usageStats.mLastTimeUsed = intervalStats.beginTime + protoInputStream.readLong(1112396529667L);
                    continue;
                case 4:
                    usageStats.mTotalTimeInForeground = protoInputStream.readLong(1112396529668L);
                    continue;
                case 5:
                    usageStats.mLastEvent = protoInputStream.readInt(1120986464261L);
                    continue;
                case 6:
                    usageStats.mAppLaunchCount = protoInputStream.readInt(1120986464262L);
                    continue;
                case 7:
                    try {
                        long start2 = protoInputStream.start(2246267895815L);
                        loadChooserCounts(protoInputStream, usageStats);
                        protoInputStream.end(start2);
                        continue;
                    } catch (IOException e) {
                        String str = TAG;
                        Slog.e(str, "Unable to read chooser counts for " + usageStats.mPackageName, e);
                    }
                case 8:
                    usageStats.mLastTimeForegroundServiceUsed = intervalStats.beginTime + protoInputStream.readLong(1112396529672L);
                    continue;
                case 9:
                    usageStats.mTotalTimeForegroundServiceUsed = protoInputStream.readLong(1112396529673L);
                    continue;
                case 10:
                    usageStats.mLastTimeVisible = intervalStats.beginTime + protoInputStream.readLong(1112396529674L);
                    continue;
                case 11:
                    usageStats.mTotalTimeVisible = protoInputStream.readLong(1112396529675L);
                    continue;
                case 12:
                    usageStats.mLastTimeComponentUsed = intervalStats.beginTime + protoInputStream.readLong(1112396529676L);
                    continue;
            }
            usageStats = orCreateUsageStats;
        }
        protoInputStream.end(start);
    }

    public static void loadCountAndTime(ProtoInputStream protoInputStream, long j, IntervalStats.EventTracker eventTracker) {
        try {
            long start = protoInputStream.start(j);
            while (true) {
                int nextField = protoInputStream.nextField();
                if (nextField == -1) {
                    protoInputStream.end(start);
                    return;
                } else if (nextField == 1) {
                    eventTracker.count = protoInputStream.readInt(1120986464257L);
                } else if (nextField == 2) {
                    eventTracker.duration = protoInputStream.readLong(1112396529666L);
                }
            }
        } catch (IOException e) {
            String str = TAG;
            Slog.e(str, "Unable to read event tracker " + j, e);
        }
    }

    public static void loadChooserCounts(ProtoInputStream protoInputStream, UsageStats usageStats) throws IOException {
        ArrayMap arrayMap;
        String str;
        if (usageStats.mChooserCounts == null) {
            usageStats.mChooserCounts = new ArrayMap();
        }
        if (protoInputStream.nextField(1138166333441L)) {
            str = protoInputStream.readString(1138166333441L);
            arrayMap = (ArrayMap) usageStats.mChooserCounts.get(str);
            if (arrayMap == null) {
                arrayMap = new ArrayMap();
                usageStats.mChooserCounts.put(str, arrayMap);
            }
        } else {
            arrayMap = new ArrayMap();
            str = null;
        }
        while (true) {
            int nextField = protoInputStream.nextField();
            if (nextField == -1) {
                break;
            } else if (nextField == 1) {
                str = protoInputStream.readString(1138166333441L);
                usageStats.mChooserCounts.put(str, arrayMap);
            } else if (nextField == 3) {
                long start = protoInputStream.start(2246267895811L);
                loadCountsForAction(protoInputStream, arrayMap);
                protoInputStream.end(start);
                break;
            }
        }
        if (str == null) {
            usageStats.mChooserCounts.put("", arrayMap);
        }
    }

    public static void loadCountsForAction(ProtoInputStream protoInputStream, ArrayMap<String, Integer> arrayMap) throws IOException {
        String str = null;
        int i = 0;
        while (true) {
            int nextField = protoInputStream.nextField();
            if (nextField == -1) {
                break;
            } else if (nextField == 1) {
                str = protoInputStream.readString(1138166333441L);
            } else if (nextField == 3) {
                i = protoInputStream.readInt(1120986464259L);
            }
        }
        if (str == null) {
            arrayMap.put("", Integer.valueOf(i));
        } else {
            arrayMap.put(str, Integer.valueOf(i));
        }
    }

    public static void loadConfigStats(ProtoInputStream protoInputStream, long j, IntervalStats intervalStats) throws IOException {
        ConfigurationStats configurationStats;
        long start = protoInputStream.start(j);
        Configuration configuration = new Configuration();
        boolean z = false;
        if (protoInputStream.nextField(1146756268033L)) {
            configuration.readFromProto(protoInputStream, 1146756268033L);
            configurationStats = intervalStats.getOrCreateConfigurationStats(configuration);
        } else {
            configurationStats = new ConfigurationStats();
        }
        while (true) {
            int nextField = protoInputStream.nextField();
            if (nextField == -1) {
                break;
            } else if (nextField == 1) {
                configuration.readFromProto(protoInputStream, 1146756268033L);
                ConfigurationStats orCreateConfigurationStats = intervalStats.getOrCreateConfigurationStats(configuration);
                orCreateConfigurationStats.mLastTimeActive = configurationStats.mLastTimeActive;
                orCreateConfigurationStats.mTotalTimeActive = configurationStats.mTotalTimeActive;
                orCreateConfigurationStats.mActivationCount = configurationStats.mActivationCount;
                configurationStats = orCreateConfigurationStats;
            } else if (nextField == 2) {
                configurationStats.mLastTimeActive = intervalStats.beginTime + protoInputStream.readLong(1112396529666L);
            } else if (nextField == 3) {
                configurationStats.mTotalTimeActive = protoInputStream.readLong(1112396529667L);
            } else if (nextField == 4) {
                configurationStats.mActivationCount = protoInputStream.readInt(1120986464260L);
            } else if (nextField == 5) {
                z = protoInputStream.readBoolean(1133871366149L);
            }
        }
        if (z) {
            intervalStats.activeConfiguration = configurationStats.mConfiguration;
        }
        protoInputStream.end(start);
    }

    public static void loadEvent(ProtoInputStream protoInputStream, long j, IntervalStats intervalStats, List<String> list) throws IOException {
        long start = protoInputStream.start(j);
        UsageEvents.Event buildEvent = intervalStats.buildEvent(protoInputStream, list);
        protoInputStream.end(start);
        if (buildEvent.mPackage == null) {
            throw new ProtocolException("no package field present");
        }
        intervalStats.events.insert(buildEvent);
    }

    public static void writeStringPool(ProtoOutputStream protoOutputStream, IntervalStats intervalStats) throws IllegalArgumentException {
        long start = protoOutputStream.start(1146756268034L);
        int size = intervalStats.mStringCache.size();
        protoOutputStream.write(1120986464257L, size);
        for (int i = 0; i < size; i++) {
            protoOutputStream.write(2237677961218L, intervalStats.mStringCache.valueAt(i));
        }
        protoOutputStream.end(start);
    }

    public static void writeUsageStats(ProtoOutputStream protoOutputStream, long j, IntervalStats intervalStats, UsageStats usageStats) throws IllegalArgumentException {
        long start = protoOutputStream.start(j);
        int indexOf = intervalStats.mStringCache.indexOf(usageStats.mPackageName);
        if (indexOf >= 0) {
            protoOutputStream.write(1120986464258L, indexOf + 1);
        } else {
            protoOutputStream.write(1138166333441L, usageStats.mPackageName);
        }
        UsageStatsProtoV2.writeOffsetTimestamp(protoOutputStream, 1112396529667L, usageStats.mLastTimeUsed, intervalStats.beginTime);
        protoOutputStream.write(1112396529668L, usageStats.mTotalTimeInForeground);
        protoOutputStream.write(1120986464261L, usageStats.mLastEvent);
        UsageStatsProtoV2.writeOffsetTimestamp(protoOutputStream, 1112396529672L, usageStats.mLastTimeForegroundServiceUsed, intervalStats.beginTime);
        protoOutputStream.write(1112396529673L, usageStats.mTotalTimeForegroundServiceUsed);
        UsageStatsProtoV2.writeOffsetTimestamp(protoOutputStream, 1112396529674L, usageStats.mLastTimeVisible, intervalStats.beginTime);
        protoOutputStream.write(1112396529675L, usageStats.mTotalTimeVisible);
        UsageStatsProtoV2.writeOffsetTimestamp(protoOutputStream, 1112396529676L, usageStats.mLastTimeComponentUsed, intervalStats.beginTime);
        protoOutputStream.write(1120986464262L, usageStats.mAppLaunchCount);
        try {
            writeChooserCounts(protoOutputStream, usageStats);
        } catch (IllegalArgumentException e) {
            String str = TAG;
            Slog.e(str, "Unable to write chooser counts for " + usageStats.mPackageName, e);
        }
        protoOutputStream.end(start);
    }

    public static void writeCountAndTime(ProtoOutputStream protoOutputStream, long j, int i, long j2) throws IllegalArgumentException {
        long start = protoOutputStream.start(j);
        protoOutputStream.write(1120986464257L, i);
        protoOutputStream.write(1112396529666L, j2);
        protoOutputStream.end(start);
    }

    public static void writeChooserCounts(ProtoOutputStream protoOutputStream, UsageStats usageStats) throws IllegalArgumentException {
        ArrayMap arrayMap;
        if (usageStats == null || (arrayMap = usageStats.mChooserCounts) == null || arrayMap.keySet().isEmpty()) {
            return;
        }
        int size = usageStats.mChooserCounts.size();
        for (int i = 0; i < size; i++) {
            String str = (String) usageStats.mChooserCounts.keyAt(i);
            ArrayMap arrayMap2 = (ArrayMap) usageStats.mChooserCounts.valueAt(i);
            if (str != null && arrayMap2 != null && !arrayMap2.isEmpty()) {
                long start = protoOutputStream.start(2246267895815L);
                protoOutputStream.write(1138166333441L, str);
                writeCountsForAction(protoOutputStream, arrayMap2);
                protoOutputStream.end(start);
            }
        }
    }

    public static void writeCountsForAction(ProtoOutputStream protoOutputStream, ArrayMap<String, Integer> arrayMap) throws IllegalArgumentException {
        int size = arrayMap.size();
        for (int i = 0; i < size; i++) {
            String keyAt = arrayMap.keyAt(i);
            int intValue = arrayMap.valueAt(i).intValue();
            if (intValue > 0) {
                long start = protoOutputStream.start(2246267895811L);
                protoOutputStream.write(1138166333441L, keyAt);
                protoOutputStream.write(1120986464259L, intValue);
                protoOutputStream.end(start);
            }
        }
    }

    public static void writeConfigStats(ProtoOutputStream protoOutputStream, long j, IntervalStats intervalStats, ConfigurationStats configurationStats, boolean z) throws IllegalArgumentException {
        long start = protoOutputStream.start(j);
        configurationStats.mConfiguration.dumpDebug(protoOutputStream, 1146756268033L);
        UsageStatsProtoV2.writeOffsetTimestamp(protoOutputStream, 1112396529666L, configurationStats.mLastTimeActive, intervalStats.beginTime);
        protoOutputStream.write(1112396529667L, configurationStats.mTotalTimeActive);
        protoOutputStream.write(1120986464260L, configurationStats.mActivationCount);
        protoOutputStream.write(1133871366149L, z);
        protoOutputStream.end(start);
    }

    public static void writeEvent(ProtoOutputStream protoOutputStream, long j, IntervalStats intervalStats, UsageEvents.Event event) throws IllegalArgumentException {
        int indexOf;
        String str;
        int indexOf2;
        int indexOf3;
        long start = protoOutputStream.start(j);
        int indexOf4 = intervalStats.mStringCache.indexOf(event.mPackage);
        if (indexOf4 >= 0) {
            protoOutputStream.write(1120986464258L, indexOf4 + 1);
        } else {
            protoOutputStream.write(1138166333441L, event.mPackage);
        }
        String str2 = event.mClass;
        if (str2 != null) {
            int indexOf5 = intervalStats.mStringCache.indexOf(str2);
            if (indexOf5 >= 0) {
                protoOutputStream.write(1120986464260L, indexOf5 + 1);
            } else {
                protoOutputStream.write(1138166333443L, event.mClass);
            }
        }
        UsageStatsProtoV2.writeOffsetTimestamp(protoOutputStream, 1112396529669L, event.mTimeStamp, intervalStats.beginTime);
        protoOutputStream.write(1120986464262L, event.mFlags);
        protoOutputStream.write(1120986464263L, event.mEventType);
        protoOutputStream.write(1120986464270L, event.mInstanceId);
        String str3 = event.mTaskRootPackage;
        if (str3 != null && (indexOf3 = intervalStats.mStringCache.indexOf(str3)) >= 0) {
            protoOutputStream.write(1120986464271L, indexOf3 + 1);
        }
        String str4 = event.mTaskRootClass;
        if (str4 != null && (indexOf2 = intervalStats.mStringCache.indexOf(str4)) >= 0) {
            protoOutputStream.write(1120986464272L, indexOf2 + 1);
        }
        int i = event.mEventType;
        if (i == 5) {
            Configuration configuration = event.mConfiguration;
            if (configuration != null) {
                configuration.dumpDebug(protoOutputStream, 1146756268040L);
            }
        } else if (i == 8) {
            String str5 = event.mShortcutId;
            if (str5 != null) {
                protoOutputStream.write(1138166333449L, str5);
            }
        } else if (i == 30) {
            String str6 = event.mLocusId;
            if (str6 != null && (indexOf = intervalStats.mStringCache.indexOf(str6)) >= 0) {
                protoOutputStream.write(1120986464273L, indexOf + 1);
            }
        } else if (i == 11) {
            int i2 = event.mBucketAndReason;
            if (i2 != 0) {
                protoOutputStream.write(1120986464267L, i2);
            }
        } else if (i == 12 && (str = event.mNotificationChannelId) != null) {
            int indexOf6 = intervalStats.mStringCache.indexOf(str);
            if (indexOf6 >= 0) {
                protoOutputStream.write(1120986464269L, indexOf6 + 1);
            } else {
                protoOutputStream.write(1138166333452L, event.mNotificationChannelId);
            }
        }
        protoOutputStream.end(start);
    }

    public static void read(InputStream inputStream, IntervalStats intervalStats) throws IOException {
        ProtoInputStream protoInputStream = new ProtoInputStream(inputStream);
        intervalStats.packageStats.clear();
        intervalStats.configurations.clear();
        List<String> list = null;
        intervalStats.activeConfiguration = null;
        intervalStats.events.clear();
        while (true) {
            int nextField = protoInputStream.nextField();
            if (nextField == -1) {
                intervalStats.upgradeIfNeeded();
                return;
            } else if (nextField == 1) {
                intervalStats.endTime = intervalStats.beginTime + protoInputStream.readLong(1112396529665L);
            } else if (nextField == 2) {
                try {
                    list = readStringPool(protoInputStream);
                    intervalStats.mStringCache.addAll(list);
                } catch (IOException e) {
                    Slog.e(TAG, "Unable to read string pool from proto.", e);
                }
            } else if (nextField == 3) {
                intervalStats.majorVersion = protoInputStream.readInt(1120986464259L);
            } else if (nextField == 4) {
                intervalStats.minorVersion = protoInputStream.readInt(1120986464260L);
            } else {
                switch (nextField) {
                    case 10:
                        loadCountAndTime(protoInputStream, 1146756268042L, intervalStats.interactiveTracker);
                        continue;
                    case 11:
                        loadCountAndTime(protoInputStream, 1146756268043L, intervalStats.nonInteractiveTracker);
                        continue;
                    case 12:
                        loadCountAndTime(protoInputStream, 1146756268044L, intervalStats.keyguardShownTracker);
                        continue;
                    case 13:
                        loadCountAndTime(protoInputStream, 1146756268045L, intervalStats.keyguardHiddenTracker);
                        continue;
                    default:
                        switch (nextField) {
                            case 20:
                                try {
                                    loadUsageStats(protoInputStream, 2246267895828L, intervalStats, list);
                                    continue;
                                } catch (IOException e2) {
                                    Slog.e(TAG, "Unable to read some usage stats from proto.", e2);
                                    break;
                                }
                            case 21:
                                try {
                                    loadConfigStats(protoInputStream, 2246267895829L, intervalStats);
                                    continue;
                                } catch (IOException e3) {
                                    Slog.e(TAG, "Unable to read some configuration stats from proto.", e3);
                                    break;
                                }
                            case 22:
                                try {
                                    loadEvent(protoInputStream, 2246267895830L, intervalStats, list);
                                    continue;
                                } catch (IOException e4) {
                                    Slog.e(TAG, "Unable to read some events from proto.", e4);
                                    break;
                                }
                            default:
                                continue;
                        }
                }
            }
        }
    }

    public static void write(OutputStream outputStream, IntervalStats intervalStats) throws IOException, IllegalArgumentException {
        ProtoOutputStream protoOutputStream = new ProtoOutputStream(outputStream);
        protoOutputStream.write(1112396529665L, UsageStatsProtoV2.getOffsetTimestamp(intervalStats.endTime, intervalStats.beginTime));
        protoOutputStream.write(1120986464259L, intervalStats.majorVersion);
        protoOutputStream.write(1120986464260L, intervalStats.minorVersion);
        try {
            writeStringPool(protoOutputStream, intervalStats);
        } catch (IllegalArgumentException e) {
            Slog.e(TAG, "Unable to write string pool to proto.", e);
        }
        try {
            IntervalStats.EventTracker eventTracker = intervalStats.interactiveTracker;
            writeCountAndTime(protoOutputStream, 1146756268042L, eventTracker.count, eventTracker.duration);
            IntervalStats.EventTracker eventTracker2 = intervalStats.nonInteractiveTracker;
            writeCountAndTime(protoOutputStream, 1146756268043L, eventTracker2.count, eventTracker2.duration);
            IntervalStats.EventTracker eventTracker3 = intervalStats.keyguardShownTracker;
            writeCountAndTime(protoOutputStream, 1146756268044L, eventTracker3.count, eventTracker3.duration);
            IntervalStats.EventTracker eventTracker4 = intervalStats.keyguardHiddenTracker;
            writeCountAndTime(protoOutputStream, 1146756268045L, eventTracker4.count, eventTracker4.duration);
        } catch (IllegalArgumentException e2) {
            Slog.e(TAG, "Unable to write some interval stats trackers to proto.", e2);
        }
        int size = intervalStats.packageStats.size();
        for (int i = 0; i < size; i++) {
            try {
                writeUsageStats(protoOutputStream, 2246267895828L, intervalStats, intervalStats.packageStats.valueAt(i));
            } catch (IllegalArgumentException e3) {
                Slog.e(TAG, "Unable to write some usage stats to proto.", e3);
            }
        }
        int size2 = intervalStats.configurations.size();
        for (int i2 = 0; i2 < size2; i2++) {
            try {
                writeConfigStats(protoOutputStream, 2246267895829L, intervalStats, intervalStats.configurations.valueAt(i2), intervalStats.activeConfiguration.equals(intervalStats.configurations.keyAt(i2)));
            } catch (IllegalArgumentException e4) {
                Slog.e(TAG, "Unable to write some configuration stats to proto.", e4);
            }
        }
        int size3 = intervalStats.events.size();
        for (int i3 = 0; i3 < size3; i3++) {
            try {
                writeEvent(protoOutputStream, 2246267895830L, intervalStats, intervalStats.events.get(i3));
            } catch (IllegalArgumentException e5) {
                Slog.e(TAG, "Unable to write some events to proto.", e5);
            }
        }
        protoOutputStream.flush();
    }
}
