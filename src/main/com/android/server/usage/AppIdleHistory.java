package com.android.server.usage;

import android.app.usage.AppStandbyInfo;
import android.app.usage.UsageStatsManager;
import android.os.SystemClock;
import android.util.ArrayMap;
import android.util.AtomicFile;
import android.util.IndentingPrintWriter;
import android.util.Slog;
import android.util.SparseArray;
import android.util.SparseLongArray;
import android.util.TimeUtils;
import android.util.Xml;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.FrameworkStatsLog;
import com.android.internal.util.jobs.CollectionUtils;
import com.android.internal.util.jobs.FastXmlSerializer;
import com.android.internal.util.jobs.XmlUtils;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import libcore.io.IoUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes2.dex */
public class AppIdleHistory {
    @VisibleForTesting
    static final String APP_IDLE_FILENAME = "app_idle_stats.xml";
    public long mElapsedDuration;
    public long mElapsedSnapshot;
    public SparseArray<ArrayMap<String, AppUsageHistory>> mIdleHistory = new SparseArray<>();
    public boolean mScreenOn;
    public long mScreenOnDuration;
    public long mScreenOnSnapshot;
    public final File mStorageDir;

    /* loaded from: classes2.dex */
    public static class AppUsageHistory {
        public SparseLongArray bucketExpiryTimesMs;
        public int bucketingReason;
        public int currentBucket;
        public int lastInformedBucket;
        public long lastJobRunTime;
        public int lastPredictedBucket = -1;
        public long lastPredictedTime;
        public long lastRestrictAttemptElapsedTime;
        public int lastRestrictReason;
        public long lastUsedByUserElapsedTime;
        public long lastUsedElapsedTime;
        public long lastUsedScreenTime;
        public long nextEstimatedLaunchTime;
    }

    public AppIdleHistory(File file, long j) {
        this.mElapsedSnapshot = j;
        this.mScreenOnSnapshot = j;
        this.mStorageDir = file;
        readScreenOnTime();
    }

    public void updateDisplay(boolean z, long j) {
        if (z == this.mScreenOn) {
            return;
        }
        this.mScreenOn = z;
        if (z) {
            this.mScreenOnSnapshot = j;
            return;
        }
        this.mScreenOnDuration += j - this.mScreenOnSnapshot;
        this.mElapsedDuration += j - this.mElapsedSnapshot;
        this.mElapsedSnapshot = j;
    }

    public long getScreenOnTime(long j) {
        long j2 = this.mScreenOnDuration;
        return this.mScreenOn ? j2 + (j - this.mScreenOnSnapshot) : j2;
    }

    @VisibleForTesting
    public File getScreenOnTimeFile() {
        return new File(this.mStorageDir, "screen_on_time");
    }

    public final void readScreenOnTime() {
        File screenOnTimeFile = getScreenOnTimeFile();
        if (screenOnTimeFile.exists()) {
            try {
                BufferedReader bufferedReader = new BufferedReader(new FileReader(screenOnTimeFile));
                this.mScreenOnDuration = Long.parseLong(bufferedReader.readLine());
                this.mElapsedDuration = Long.parseLong(bufferedReader.readLine());
                bufferedReader.close();
                return;
            } catch (IOException | NumberFormatException unused) {
                return;
            }
        }
        writeScreenOnTime();
    }

    public final void writeScreenOnTime() {
        FileOutputStream fileOutputStream;
        AtomicFile atomicFile = new AtomicFile(getScreenOnTimeFile());
        try {
            fileOutputStream = atomicFile.startWrite();
        } catch (IOException unused) {
            fileOutputStream = null;
        }
        try {
            fileOutputStream.write((Long.toString(this.mScreenOnDuration) + "\n" + Long.toString(this.mElapsedDuration) + "\n").getBytes());
            atomicFile.finishWrite(fileOutputStream);
        } catch (IOException unused2) {
            atomicFile.failWrite(fileOutputStream);
        }
    }

    public void writeAppIdleDurations() {
        long elapsedRealtime = SystemClock.elapsedRealtime();
        this.mElapsedDuration += elapsedRealtime - this.mElapsedSnapshot;
        this.mElapsedSnapshot = elapsedRealtime;
        writeScreenOnTime();
    }

    /* JADX WARN: Removed duplicated region for block: B:16:0x004a  */
    /* JADX WARN: Removed duplicated region for block: B:22:0x0061  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public AppUsageHistory reportUsage(AppUsageHistory appUsageHistory, String str, int i, int i2, int i3, long j, long j2) {
        int i4;
        int i5 = i3 | FrameworkStatsLog.APP_STANDBY_BUCKET_CHANGED__MAIN_REASON__MAIN_USAGE;
        boolean isUserUsage = AppStandbyController.isUserUsage(i5);
        if (appUsageHistory.currentBucket == 45 && !isUserUsage) {
            int i6 = appUsageHistory.bucketingReason;
            if ((65280 & i6) != 512) {
                i5 = i6;
                i2 = 45;
                if (j != 0) {
                    long j3 = this.mElapsedDuration + (j - this.mElapsedSnapshot);
                    appUsageHistory.lastUsedElapsedTime = j3;
                    if (isUserUsage) {
                        appUsageHistory.lastUsedByUserElapsedTime = j3;
                    }
                    appUsageHistory.lastUsedScreenTime = getScreenOnTime(j);
                }
                i4 = appUsageHistory.currentBucket;
                if (i4 >= i2) {
                    if (i4 > i2) {
                        appUsageHistory.currentBucket = i2;
                        logAppStandbyBucketChanged(str, i, i2, i5);
                    }
                    appUsageHistory.bucketingReason = i5;
                }
                return appUsageHistory;
            }
        }
        if (j2 > j) {
            long elapsedTime = getElapsedTime(j2);
            if (appUsageHistory.bucketExpiryTimesMs == null) {
                appUsageHistory.bucketExpiryTimesMs = new SparseLongArray();
            }
            appUsageHistory.bucketExpiryTimesMs.put(i2, Math.max(elapsedTime, appUsageHistory.bucketExpiryTimesMs.get(i2)));
            removeElapsedExpiryTimes(appUsageHistory, getElapsedTime(j));
        }
        if (j != 0) {
        }
        i4 = appUsageHistory.currentBucket;
        if (i4 >= i2) {
        }
        return appUsageHistory;
    }

    public final void removeElapsedExpiryTimes(AppUsageHistory appUsageHistory, long j) {
        SparseLongArray sparseLongArray = appUsageHistory.bucketExpiryTimesMs;
        if (sparseLongArray == null) {
            return;
        }
        for (int size = sparseLongArray.size() - 1; size >= 0; size--) {
            if (appUsageHistory.bucketExpiryTimesMs.valueAt(size) < j) {
                appUsageHistory.bucketExpiryTimesMs.removeAt(size);
            }
        }
    }

    public AppUsageHistory reportUsage(String str, int i, int i2, int i3, long j, long j2) {
        return reportUsage(getPackageHistory(getUserHistory(i), str, j, true), str, i, i2, i3, j, j2);
    }

    public final ArrayMap<String, AppUsageHistory> getUserHistory(int i) {
        ArrayMap<String, AppUsageHistory> arrayMap = this.mIdleHistory.get(i);
        if (arrayMap == null) {
            ArrayMap<String, AppUsageHistory> arrayMap2 = new ArrayMap<>();
            this.mIdleHistory.put(i, arrayMap2);
            readAppIdleTimes(i, arrayMap2);
            return arrayMap2;
        }
        return arrayMap;
    }

    public final AppUsageHistory getPackageHistory(ArrayMap<String, AppUsageHistory> arrayMap, String str, long j, boolean z) {
        AppUsageHistory appUsageHistory = arrayMap.get(str);
        if (appUsageHistory == null && z) {
            AppUsageHistory appUsageHistory2 = new AppUsageHistory();
            appUsageHistory2.lastUsedByUserElapsedTime = -2147483648L;
            appUsageHistory2.lastUsedElapsedTime = -2147483648L;
            appUsageHistory2.lastUsedScreenTime = -2147483648L;
            appUsageHistory2.lastPredictedTime = -2147483648L;
            appUsageHistory2.currentBucket = 50;
            appUsageHistory2.bucketingReason = 256;
            appUsageHistory2.lastInformedBucket = -1;
            appUsageHistory2.lastJobRunTime = Long.MIN_VALUE;
            arrayMap.put(str, appUsageHistory2);
            return appUsageHistory2;
        }
        return appUsageHistory;
    }

    public void onUserRemoved(int i) {
        this.mIdleHistory.remove(i);
    }

    public boolean isIdle(String str, int i, long j) {
        return getPackageHistory(getUserHistory(i), str, j, true).currentBucket >= 40;
    }

    public AppUsageHistory getAppUsageHistory(String str, int i, long j) {
        return getPackageHistory(getUserHistory(i), str, j, true);
    }

    public void setAppStandbyBucket(String str, int i, long j, int i2, int i3) {
        setAppStandbyBucket(str, i, j, i2, i3, false);
    }

    public void setAppStandbyBucket(String str, int i, long j, int i2, int i3, boolean z) {
        SparseLongArray sparseLongArray;
        AppUsageHistory packageHistory = getPackageHistory(getUserHistory(i), str, j, true);
        boolean z2 = packageHistory.currentBucket != i2;
        packageHistory.currentBucket = i2;
        packageHistory.bucketingReason = i3;
        long elapsedTime = getElapsedTime(j);
        if ((65280 & i3) == 1280) {
            packageHistory.lastPredictedTime = elapsedTime;
            packageHistory.lastPredictedBucket = i2;
        }
        if (z && (sparseLongArray = packageHistory.bucketExpiryTimesMs) != null) {
            sparseLongArray.clear();
        }
        if (z2) {
            logAppStandbyBucketChanged(str, i, i2, i3);
        }
    }

    public void updateLastPrediction(AppUsageHistory appUsageHistory, long j, int i) {
        appUsageHistory.lastPredictedTime = j;
        appUsageHistory.lastPredictedBucket = i;
    }

    public void setEstimatedLaunchTime(String str, int i, long j, long j2) {
        getPackageHistory(getUserHistory(i), str, j, true).nextEstimatedLaunchTime = j2;
    }

    public void setLastJobRunTime(String str, int i, long j) {
        getPackageHistory(getUserHistory(i), str, j, true).lastJobRunTime = getElapsedTime(j);
    }

    public void noteRestrictionAttempt(String str, int i, long j, int i2) {
        AppUsageHistory packageHistory = getPackageHistory(getUserHistory(i), str, j, true);
        packageHistory.lastRestrictAttemptElapsedTime = getElapsedTime(j);
        packageHistory.lastRestrictReason = i2;
    }

    public long getEstimatedLaunchTime(String str, int i, long j) {
        AppUsageHistory packageHistory = getPackageHistory(getUserHistory(i), str, j, false);
        if (packageHistory == null || packageHistory.nextEstimatedLaunchTime < System.currentTimeMillis()) {
            return Long.MAX_VALUE;
        }
        return packageHistory.nextEstimatedLaunchTime;
    }

    public long getTimeSinceLastJobRun(String str, int i, long j) {
        AppUsageHistory packageHistory = getPackageHistory(getUserHistory(i), str, j, false);
        if (packageHistory == null || packageHistory.lastJobRunTime == Long.MIN_VALUE) {
            return Long.MAX_VALUE;
        }
        return getElapsedTime(j) - packageHistory.lastJobRunTime;
    }

    public long getTimeSinceLastUsedByUser(String str, int i, long j) {
        AppUsageHistory packageHistory = getPackageHistory(getUserHistory(i), str, j, false);
        if (packageHistory != null) {
            long j2 = packageHistory.lastUsedByUserElapsedTime;
            if (j2 == Long.MIN_VALUE || j2 <= 0) {
                return Long.MAX_VALUE;
            }
            return getElapsedTime(j) - packageHistory.lastUsedByUserElapsedTime;
        }
        return Long.MAX_VALUE;
    }

    public int getAppStandbyBucket(String str, int i, long j) {
        AppUsageHistory packageHistory = getPackageHistory(getUserHistory(i), str, j, false);
        if (packageHistory == null) {
            return 50;
        }
        return packageHistory.currentBucket;
    }

    public ArrayList<AppStandbyInfo> getAppStandbyBuckets(int i, boolean z) {
        ArrayMap<String, AppUsageHistory> userHistory = getUserHistory(i);
        int size = userHistory.size();
        ArrayList<AppStandbyInfo> arrayList = new ArrayList<>(size);
        for (int i2 = 0; i2 < size; i2++) {
            arrayList.add(new AppStandbyInfo(userHistory.keyAt(i2), z ? userHistory.valueAt(i2).currentBucket : 10));
        }
        return arrayList;
    }

    public int getAppStandbyReason(String str, int i, long j) {
        AppUsageHistory packageHistory = getPackageHistory(getUserHistory(i), str, j, false);
        if (packageHistory != null) {
            return packageHistory.bucketingReason;
        }
        return 0;
    }

    public long getElapsedTime(long j) {
        return (j - this.mElapsedSnapshot) + this.mElapsedDuration;
    }

    public int setIdle(String str, int i, boolean z, long j) {
        int i2;
        int i3;
        if (z) {
            i2 = 40;
            i3 = 1024;
        } else {
            i2 = 10;
            i3 = 771;
        }
        setAppStandbyBucket(str, i, j, i2, i3, false);
        return i2;
    }

    public void clearUsage(String str, int i) {
        getUserHistory(i).remove(str);
    }

    public boolean shouldInformListeners(String str, int i, long j, int i2) {
        AppUsageHistory packageHistory = getPackageHistory(getUserHistory(i), str, j, true);
        if (packageHistory.lastInformedBucket != i2) {
            packageHistory.lastInformedBucket = i2;
            return true;
        }
        return false;
    }

    public int getThresholdIndex(String str, int i, long j, long[] jArr, long[] jArr2) {
        AppUsageHistory packageHistory = getPackageHistory(getUserHistory(i), str, j, false);
        if (packageHistory == null || packageHistory.lastUsedElapsedTime < 0 || packageHistory.lastUsedScreenTime < 0) {
            return -1;
        }
        long screenOnTime = getScreenOnTime(j) - packageHistory.lastUsedScreenTime;
        long elapsedTime = getElapsedTime(j) - packageHistory.lastUsedElapsedTime;
        for (int length = jArr.length - 1; length >= 0; length--) {
            if (screenOnTime >= jArr[length] && elapsedTime >= jArr2[length]) {
                return length;
            }
        }
        return 0;
    }

    public final void logAppStandbyBucketChanged(String str, int i, int i2, int i3) {
        FrameworkStatsLog.write(258, str, i, i2, i3 & 65280, i3 & 255);
    }

    @VisibleForTesting
    public long getBucketExpiryTimeMs(String str, int i, int i2, long j) {
        SparseLongArray sparseLongArray;
        AppUsageHistory packageHistory = getPackageHistory(getUserHistory(i), str, j, false);
        if (packageHistory == null || (sparseLongArray = packageHistory.bucketExpiryTimesMs) == null) {
            return 0L;
        }
        return sparseLongArray.get(i2, 0L);
    }

    @VisibleForTesting
    public File getUserFile(int i) {
        return new File(new File(new File(this.mStorageDir, "users"), Integer.toString(i)), APP_IDLE_FILENAME);
    }

    public void clearLastUsedTimestamps(String str, int i) {
        AppUsageHistory packageHistory = getPackageHistory(getUserHistory(i), str, SystemClock.elapsedRealtime(), false);
        if (packageHistory != null) {
            packageHistory.lastUsedByUserElapsedTime = -2147483648L;
            packageHistory.lastUsedElapsedTime = -2147483648L;
            packageHistory.lastUsedScreenTime = -2147483648L;
        }
    }

    public boolean userFileExists(int i) {
        return getUserFile(i).exists();
    }

    public final void readAppIdleTimes(int i, ArrayMap<String, AppUsageHistory> arrayMap) {
        FileInputStream fileInputStream;
        FileInputStream fileInputStream2;
        int next;
        int i2;
        int i3;
        String str = null;
        try {
            FileInputStream openRead = new AtomicFile(getUserFile(i)).openRead();
            try {
                XmlPullParser newPullParser = Xml.newPullParser();
                newPullParser.setInput(openRead, StandardCharsets.UTF_8.name());
                do {
                    next = newPullParser.next();
                    i2 = 1;
                    i3 = 2;
                    if (next == 2) {
                        break;
                    }
                } while (next != 1);
                if (next != 2) {
                    try {
                        Slog.e("AppIdleHistory", "Unable to read app idle file for user " + i);
                        IoUtils.closeQuietly(openRead);
                    } catch (IOException | XmlPullParserException e) {
                        e = e;
                        fileInputStream = openRead;
                        try {
                            Slog.e("AppIdleHistory", "Unable to read app idle file for user " + i, e);
                            IoUtils.closeQuietly(fileInputStream);
                        } catch (Throwable th) {
                            th = th;
                            IoUtils.closeQuietly(fileInputStream);
                            throw th;
                        }
                    } catch (Throwable th2) {
                        th = th2;
                        fileInputStream = openRead;
                        IoUtils.closeQuietly(fileInputStream);
                        throw th;
                    }
                } else if (!newPullParser.getName().equals("packages")) {
                    IoUtils.closeQuietly(openRead);
                } else {
                    int intValue = getIntValue(newPullParser, "version", 0);
                    while (true) {
                        int next2 = newPullParser.next();
                        if (next2 == i2) {
                            IoUtils.closeQuietly(openRead);
                            return;
                        } else if (next2 == i3) {
                            if (newPullParser.getName().equals("package")) {
                                String attributeValue = newPullParser.getAttributeValue(str, "name");
                                AppUsageHistory appUsageHistory = new AppUsageHistory();
                                long parseLong = Long.parseLong(newPullParser.getAttributeValue(str, "elapsedIdleTime"));
                                appUsageHistory.lastUsedElapsedTime = parseLong;
                                appUsageHistory.lastUsedByUserElapsedTime = getLongValue(newPullParser, "lastUsedByUserElapsedTime", parseLong);
                                appUsageHistory.lastUsedScreenTime = Long.parseLong(newPullParser.getAttributeValue(str, "screenIdleTime"));
                                appUsageHistory.lastPredictedTime = getLongValue(newPullParser, "lastPredictedTime", 0L);
                                String attributeValue2 = newPullParser.getAttributeValue(str, "appLimitBucket");
                                appUsageHistory.currentBucket = attributeValue2 == null ? 10 : Integer.parseInt(attributeValue2);
                                String attributeValue3 = newPullParser.getAttributeValue(str, "bucketReason");
                                fileInputStream2 = openRead;
                                try {
                                    try {
                                        appUsageHistory.lastJobRunTime = getLongValue(newPullParser, "lastJobRunTime", Long.MIN_VALUE);
                                        appUsageHistory.bucketingReason = 256;
                                        if (attributeValue3 != null) {
                                            try {
                                                appUsageHistory.bucketingReason = Integer.parseInt(attributeValue3, 16);
                                            } catch (NumberFormatException e2) {
                                                Slog.wtf("AppIdleHistory", "Unable to read bucketing reason", e2);
                                            }
                                        }
                                        appUsageHistory.lastRestrictAttemptElapsedTime = getLongValue(newPullParser, "lastRestrictionAttemptElapsedTime", 0L);
                                        String attributeValue4 = newPullParser.getAttributeValue(null, "lastRestrictionAttemptReason");
                                        if (attributeValue4 != null) {
                                            try {
                                                appUsageHistory.lastRestrictReason = Integer.parseInt(attributeValue4, 16);
                                            } catch (NumberFormatException e3) {
                                                Slog.wtf("AppIdleHistory", "Unable to read last restrict reason", e3);
                                            }
                                        }
                                        appUsageHistory.nextEstimatedLaunchTime = getLongValue(newPullParser, "nextEstimatedAppLaunchTime", 0L);
                                        appUsageHistory.lastInformedBucket = -1;
                                        arrayMap.put(attributeValue, appUsageHistory);
                                        if (intValue >= 1) {
                                            int depth = newPullParser.getDepth();
                                            while (XmlUtils.nextElementWithin(newPullParser, depth)) {
                                                if ("expiryTimes".equals(newPullParser.getName())) {
                                                    readBucketExpiryTimes(newPullParser, appUsageHistory);
                                                }
                                            }
                                        } else {
                                            long longValue = getLongValue(newPullParser, "activeTimeoutTime", 0L);
                                            long longValue2 = getLongValue(newPullParser, "workingSetTimeoutTime", 0L);
                                            if (longValue != 0 || longValue2 != 0) {
                                                insertBucketExpiryTime(appUsageHistory, 10, longValue);
                                                insertBucketExpiryTime(appUsageHistory, 20, longValue2);
                                            }
                                        }
                                    } catch (Throwable th3) {
                                        th = th3;
                                        fileInputStream = fileInputStream2;
                                        IoUtils.closeQuietly(fileInputStream);
                                        throw th;
                                    }
                                } catch (IOException | XmlPullParserException e4) {
                                    e = e4;
                                    fileInputStream = fileInputStream2;
                                    Slog.e("AppIdleHistory", "Unable to read app idle file for user " + i, e);
                                    IoUtils.closeQuietly(fileInputStream);
                                }
                            } else {
                                fileInputStream2 = openRead;
                            }
                            openRead = fileInputStream2;
                            str = null;
                            i2 = 1;
                            i3 = 2;
                        }
                    }
                }
            } catch (IOException | XmlPullParserException e5) {
                e = e5;
                fileInputStream2 = openRead;
            } catch (Throwable th4) {
                th = th4;
                fileInputStream2 = openRead;
            }
        } catch (IOException | XmlPullParserException e6) {
            e = e6;
            fileInputStream = null;
        } catch (Throwable th5) {
            th = th5;
            fileInputStream = null;
        }
    }

    public final void readBucketExpiryTimes(XmlPullParser xmlPullParser, AppUsageHistory appUsageHistory) throws IOException, XmlPullParserException {
        int depth = xmlPullParser.getDepth();
        while (XmlUtils.nextElementWithin(xmlPullParser, depth)) {
            if ("item".equals(xmlPullParser.getName())) {
                int intValue = getIntValue(xmlPullParser, "bucket", -1);
                if (intValue == -1) {
                    Slog.e("AppIdleHistory", "Error reading the buckets expiry times");
                } else {
                    insertBucketExpiryTime(appUsageHistory, intValue, getLongValue(xmlPullParser, "expiry", 0L));
                }
            }
        }
    }

    public final void insertBucketExpiryTime(AppUsageHistory appUsageHistory, int i, long j) {
        if (j == 0) {
            return;
        }
        if (appUsageHistory.bucketExpiryTimesMs == null) {
            appUsageHistory.bucketExpiryTimesMs = new SparseLongArray();
        }
        appUsageHistory.bucketExpiryTimesMs.put(i, j);
    }

    public final long getLongValue(XmlPullParser xmlPullParser, String str, long j) {
        String attributeValue = xmlPullParser.getAttributeValue(null, str);
        return attributeValue == null ? j : Long.parseLong(attributeValue);
    }

    public final int getIntValue(XmlPullParser xmlPullParser, String str, int i) {
        String attributeValue = xmlPullParser.getAttributeValue(null, str);
        return attributeValue == null ? i : Integer.parseInt(attributeValue);
    }

    public void writeAppIdleTimes(long j) {
        int size = this.mIdleHistory.size();
        for (int i = 0; i < size; i++) {
            writeAppIdleTimes(this.mIdleHistory.keyAt(i), j);
        }
    }

    public void writeAppIdleTimes(int i, long j) {
        FileOutputStream fileOutputStream;
        FileOutputStream fileOutputStream2;
        ArrayMap<String, AppUsageHistory> arrayMap;
        FastXmlSerializer fastXmlSerializer;
        long j2;
        int i2;
        AtomicFile atomicFile = new AtomicFile(getUserFile(i));
        FileOutputStream fileOutputStream3 = null;
        String str = null;
        try {
            FileOutputStream startWrite = atomicFile.startWrite();
            try {
                BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(startWrite);
                FastXmlSerializer fastXmlSerializer2 = new FastXmlSerializer();
                fastXmlSerializer2.setOutput(bufferedOutputStream, StandardCharsets.UTF_8.name());
                fastXmlSerializer2.startDocument(null, Boolean.TRUE);
                fastXmlSerializer2.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
                fastXmlSerializer2.startTag(null, "packages");
                fastXmlSerializer2.attribute(null, "version", String.valueOf(1));
                long elapsedTime = getElapsedTime(j);
                ArrayMap<String, AppUsageHistory> userHistory = getUserHistory(i);
                int size = userHistory.size();
                int i3 = 0;
                while (i3 < size) {
                    try {
                        String keyAt = userHistory.keyAt(i3);
                        if (keyAt == null) {
                            try {
                                Slog.w("AppIdleHistory", "Skipping App Idle write for unexpected null package");
                                fileOutputStream2 = startWrite;
                                arrayMap = userHistory;
                                fastXmlSerializer = fastXmlSerializer2;
                                j2 = elapsedTime;
                            } catch (Exception e) {
                                e = e;
                                fileOutputStream3 = startWrite;
                                atomicFile.failWrite(fileOutputStream3);
                                Slog.e("AppIdleHistory", "Error writing app idle file for user " + i, e);
                            }
                        } else {
                            AppUsageHistory valueAt = userHistory.valueAt(i3);
                            fastXmlSerializer2.startTag(str, "package");
                            arrayMap = userHistory;
                            fastXmlSerializer2.attribute(str, "name", keyAt);
                            fileOutputStream2 = startWrite;
                            try {
                                fastXmlSerializer2.attribute(null, "elapsedIdleTime", Long.toString(valueAt.lastUsedElapsedTime));
                                fastXmlSerializer2.attribute(null, "lastUsedByUserElapsedTime", Long.toString(valueAt.lastUsedByUserElapsedTime));
                                fastXmlSerializer = fastXmlSerializer2;
                                fastXmlSerializer.attribute(null, "screenIdleTime", Long.toString(valueAt.lastUsedScreenTime));
                                j2 = elapsedTime;
                                fastXmlSerializer.attribute(null, "lastPredictedTime", Long.toString(valueAt.lastPredictedTime));
                                fastXmlSerializer.attribute(null, "appLimitBucket", Integer.toString(valueAt.currentBucket));
                                fastXmlSerializer.attribute(null, "bucketReason", Integer.toHexString(valueAt.bucketingReason));
                                long j3 = valueAt.lastJobRunTime;
                                if (j3 != Long.MIN_VALUE) {
                                    fastXmlSerializer.attribute(null, "lastJobRunTime", Long.toString(j3));
                                }
                                long j4 = valueAt.lastRestrictAttemptElapsedTime;
                                if (j4 > 0) {
                                    fastXmlSerializer.attribute(null, "lastRestrictionAttemptElapsedTime", Long.toString(j4));
                                }
                                fastXmlSerializer.attribute(null, "lastRestrictionAttemptReason", Integer.toHexString(valueAt.lastRestrictReason));
                                long j5 = valueAt.nextEstimatedLaunchTime;
                                if (j5 > 0) {
                                    fastXmlSerializer.attribute(null, "nextEstimatedAppLaunchTime", Long.toString(j5));
                                }
                                if (valueAt.bucketExpiryTimesMs != null) {
                                    fastXmlSerializer.startTag(null, "expiryTimes");
                                    int size2 = valueAt.bucketExpiryTimesMs.size();
                                    int i4 = 0;
                                    while (i4 < size2) {
                                        long valueAt2 = valueAt.bucketExpiryTimesMs.valueAt(i4);
                                        if (valueAt2 < j2) {
                                            i2 = size2;
                                        } else {
                                            int keyAt2 = valueAt.bucketExpiryTimesMs.keyAt(i4);
                                            fastXmlSerializer.startTag(null, "item");
                                            i2 = size2;
                                            fastXmlSerializer.attribute(null, "bucket", String.valueOf(keyAt2));
                                            fastXmlSerializer.attribute(null, "expiry", String.valueOf(valueAt2));
                                            fastXmlSerializer.endTag(null, "item");
                                        }
                                        i4++;
                                        size2 = i2;
                                    }
                                    fastXmlSerializer.endTag(null, "expiryTimes");
                                }
                                fastXmlSerializer.endTag(null, "package");
                            } catch (Exception e2) {
                                e = e2;
                                fileOutputStream3 = fileOutputStream2;
                                atomicFile.failWrite(fileOutputStream3);
                                Slog.e("AppIdleHistory", "Error writing app idle file for user " + i, e);
                            }
                        }
                        i3++;
                        userHistory = arrayMap;
                        fastXmlSerializer2 = fastXmlSerializer;
                        startWrite = fileOutputStream2;
                        elapsedTime = j2;
                        str = null;
                    } catch (Exception e3) {
                        e = e3;
                        fileOutputStream2 = startWrite;
                    }
                }
                FileOutputStream fileOutputStream4 = startWrite;
                FastXmlSerializer fastXmlSerializer3 = fastXmlSerializer2;
                try {
                    fastXmlSerializer3.endTag(str, "packages");
                    fastXmlSerializer3.endDocument();
                    fileOutputStream = fileOutputStream4;
                } catch (Exception e4) {
                    e = e4;
                    fileOutputStream = fileOutputStream4;
                }
            } catch (Exception e5) {
                e = e5;
                fileOutputStream = startWrite;
            }
        } catch (Exception e6) {
            e = e6;
        }
        try {
            atomicFile.finishWrite(fileOutputStream);
        } catch (Exception e7) {
            e = e7;
            fileOutputStream3 = fileOutputStream;
            atomicFile.failWrite(fileOutputStream3);
            Slog.e("AppIdleHistory", "Error writing app idle file for user " + i, e);
        }
    }

    public void dumpUsers(IndentingPrintWriter indentingPrintWriter, int[] iArr, List<String> list) {
        for (int i : iArr) {
            indentingPrintWriter.println();
            dumpUser(indentingPrintWriter, i, list);
        }
    }

    public final void dumpUser(IndentingPrintWriter indentingPrintWriter, int i, List<String> list) {
        ArrayMap<String, AppUsageHistory> arrayMap;
        int i2;
        int i3;
        int i4;
        int i5 = i;
        indentingPrintWriter.print("User ");
        indentingPrintWriter.print(i);
        indentingPrintWriter.println(" App Standby States:");
        indentingPrintWriter.increaseIndent();
        ArrayMap<String, AppUsageHistory> arrayMap2 = this.mIdleHistory.get(i5);
        long currentTimeMillis = System.currentTimeMillis();
        long elapsedRealtime = SystemClock.elapsedRealtime();
        long elapsedTime = getElapsedTime(elapsedRealtime);
        getScreenOnTime(elapsedRealtime);
        if (arrayMap2 == null) {
            return;
        }
        int size = arrayMap2.size();
        int i6 = 0;
        while (i6 < size) {
            String keyAt = arrayMap2.keyAt(i6);
            AppUsageHistory valueAt = arrayMap2.valueAt(i6);
            if (CollectionUtils.isEmpty(list) || list.contains(keyAt)) {
                indentingPrintWriter.print("package=" + keyAt);
                indentingPrintWriter.print(" u=" + i5);
                indentingPrintWriter.print(" bucket=" + valueAt.currentBucket + " reason=" + UsageStatsManager.reasonToString(valueAt.bucketingReason));
                indentingPrintWriter.print(" used=");
                arrayMap = arrayMap2;
                i2 = size;
                i3 = i6;
                printLastActionElapsedTime(indentingPrintWriter, elapsedTime, valueAt.lastUsedElapsedTime);
                indentingPrintWriter.print(" usedByUser=");
                printLastActionElapsedTime(indentingPrintWriter, elapsedTime, valueAt.lastUsedByUserElapsedTime);
                indentingPrintWriter.print(" usedScr=");
                printLastActionElapsedTime(indentingPrintWriter, elapsedTime, valueAt.lastUsedScreenTime);
                indentingPrintWriter.print(" lastPred=");
                printLastActionElapsedTime(indentingPrintWriter, elapsedTime, valueAt.lastPredictedTime);
                dumpBucketExpiryTimes(indentingPrintWriter, valueAt, elapsedTime);
                indentingPrintWriter.print(" lastJob=");
                TimeUtils.formatDuration(elapsedTime - valueAt.lastJobRunTime, indentingPrintWriter);
                indentingPrintWriter.print(" lastInformedBucket=" + valueAt.lastInformedBucket);
                if (valueAt.lastRestrictAttemptElapsedTime > 0) {
                    indentingPrintWriter.print(" lastRestrictAttempt=");
                    TimeUtils.formatDuration(elapsedTime - valueAt.lastRestrictAttemptElapsedTime, indentingPrintWriter);
                    indentingPrintWriter.print(" lastRestrictReason=" + UsageStatsManager.reasonToString(valueAt.lastRestrictReason));
                }
                if (valueAt.nextEstimatedLaunchTime > 0) {
                    indentingPrintWriter.print(" nextEstimatedLaunchTime=");
                    TimeUtils.formatDuration(valueAt.nextEstimatedLaunchTime - currentTimeMillis, indentingPrintWriter);
                }
                StringBuilder sb = new StringBuilder();
                sb.append(" idle=");
                i4 = i;
                sb.append(isIdle(keyAt, i4, elapsedRealtime) ? "y" : "n");
                indentingPrintWriter.print(sb.toString());
                indentingPrintWriter.println();
            } else {
                i2 = size;
                i3 = i6;
                i4 = i5;
                arrayMap = arrayMap2;
            }
            i6 = i3 + 1;
            i5 = i4;
            arrayMap2 = arrayMap;
            size = i2;
        }
        indentingPrintWriter.println();
        indentingPrintWriter.print("totalElapsedTime=");
        TimeUtils.formatDuration(getElapsedTime(elapsedRealtime), indentingPrintWriter);
        indentingPrintWriter.println();
        indentingPrintWriter.print("totalScreenOnTime=");
        TimeUtils.formatDuration(getScreenOnTime(elapsedRealtime), indentingPrintWriter);
        indentingPrintWriter.println();
        indentingPrintWriter.decreaseIndent();
    }

    public final void printLastActionElapsedTime(IndentingPrintWriter indentingPrintWriter, long j, long j2) {
        if (j2 < 0) {
            indentingPrintWriter.print("<uninitialized>");
        } else {
            TimeUtils.formatDuration(j - j2, indentingPrintWriter);
        }
    }

    public final void dumpBucketExpiryTimes(IndentingPrintWriter indentingPrintWriter, AppUsageHistory appUsageHistory, long j) {
        indentingPrintWriter.print(" expiryTimes=");
        SparseLongArray sparseLongArray = appUsageHistory.bucketExpiryTimesMs;
        if (sparseLongArray == null || sparseLongArray.size() == 0) {
            indentingPrintWriter.print("<none>");
            return;
        }
        indentingPrintWriter.print("(");
        int size = appUsageHistory.bucketExpiryTimesMs.size();
        for (int i = 0; i < size; i++) {
            int keyAt = appUsageHistory.bucketExpiryTimesMs.keyAt(i);
            long valueAt = appUsageHistory.bucketExpiryTimesMs.valueAt(i);
            if (i != 0) {
                indentingPrintWriter.print(",");
            }
            indentingPrintWriter.print(keyAt + XmlUtils.STRING_ARRAY_SEPARATOR);
            TimeUtils.formatDuration(j - valueAt, indentingPrintWriter);
        }
        indentingPrintWriter.print(")");
    }
}
