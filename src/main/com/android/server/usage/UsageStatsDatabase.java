package com.android.server.usage;

import android.app.usage.ConfigurationStats;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStats;
import android.content.res.Configuration;
import android.os.Build;
import android.os.SystemProperties;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.AtomicFile;
import android.util.Slog;
import android.util.SparseArray;
import android.util.TimeSparseArray;
import android.util.TimeUtils;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.IndentingPrintWriter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import libcore.io.IoUtils;
/* loaded from: classes2.dex */
public class UsageStatsDatabase {
    @VisibleForTesting
    public static final int BACKUP_VERSION = 4;
    @VisibleForTesting
    static final int[] MAX_FILES_PER_INTERVAL_TYPE = {100, 50, 12, 10};
    public static final int SELECTION_LOG_RETENTION_LEN = SystemProperties.getInt("ro.usagestats.chooser.retention", 14);
    public final File mBackupsDir;
    public final UnixCalendar mCal;
    public int mCurrentVersion;
    public boolean mFirstUpdate;
    public final File[] mIntervalDirs;
    public final Object mLock;
    public boolean mNewUpdate;
    public final File mPackageMappingsFile;
    public final PackagesTokenData mPackagesTokenData;
    @VisibleForTesting
    final TimeSparseArray<AtomicFile>[] mSortedStatFiles;
    public final File mUpdateBreadcrumb;
    public boolean mUpgradePerformed;
    public final File mVersionFile;

    /* loaded from: classes2.dex */
    public interface CheckinAction {
        boolean checkin(IntervalStats intervalStats);
    }

    /* loaded from: classes2.dex */
    public interface StatCombiner<T> {
        boolean combine(IntervalStats intervalStats, boolean z, List<T> list);
    }

    @VisibleForTesting
    public UsageStatsDatabase(File file, int i) {
        this.mLock = new Object();
        this.mPackagesTokenData = new PackagesTokenData();
        File[] fileArr = {new File(file, "daily"), new File(file, "weekly"), new File(file, "monthly"), new File(file, "yearly")};
        this.mIntervalDirs = fileArr;
        this.mCurrentVersion = i;
        this.mVersionFile = new File(file, "version");
        this.mBackupsDir = new File(file, "backups");
        this.mUpdateBreadcrumb = new File(file, "breadcrumb");
        this.mSortedStatFiles = new TimeSparseArray[fileArr.length];
        this.mPackageMappingsFile = new File(file, "mappings");
        this.mCal = new UnixCalendar(0L);
    }

    public UsageStatsDatabase(File file) {
        this(file, 5);
    }

    public void init(long j) {
        File[] fileArr;
        TimeSparseArray<AtomicFile>[] timeSparseArrayArr;
        synchronized (this.mLock) {
            for (File file : this.mIntervalDirs) {
                file.mkdirs();
                if (!file.exists()) {
                    throw new IllegalStateException("Failed to create directory " + file.getAbsolutePath());
                }
            }
            checkVersionAndBuildLocked();
            indexFilesLocked();
            for (TimeSparseArray<AtomicFile> timeSparseArray : this.mSortedStatFiles) {
                int closestIndexOnOrAfter = timeSparseArray.closestIndexOnOrAfter(j);
                if (closestIndexOnOrAfter >= 0) {
                    int size = timeSparseArray.size();
                    for (int i = closestIndexOnOrAfter; i < size; i++) {
                        ((AtomicFile) timeSparseArray.valueAt(i)).delete();
                    }
                    while (closestIndexOnOrAfter < size) {
                        timeSparseArray.removeAt(closestIndexOnOrAfter);
                        closestIndexOnOrAfter++;
                    }
                }
            }
        }
    }

    public boolean checkinDailyFiles(CheckinAction checkinAction) {
        int i;
        synchronized (this.mLock) {
            TimeSparseArray<AtomicFile> timeSparseArray = this.mSortedStatFiles[0];
            int size = timeSparseArray.size();
            int i2 = -1;
            int i3 = 0;
            while (true) {
                i = size - 1;
                if (i3 >= i) {
                    break;
                }
                if (((AtomicFile) timeSparseArray.valueAt(i3)).getBaseFile().getPath().endsWith("-c")) {
                    i2 = i3;
                }
                i3++;
            }
            int i4 = i2 + 1;
            if (i4 == i) {
                return true;
            }
            for (int i5 = i4; i5 < i; i5++) {
                try {
                    IntervalStats intervalStats = new IntervalStats();
                    readLocked((AtomicFile) timeSparseArray.valueAt(i5), intervalStats);
                    if (!checkinAction.checkin(intervalStats)) {
                        return false;
                    }
                } catch (Exception e) {
                    Slog.e("UsageStatsDatabase", "Failed to check-in", e);
                    return false;
                }
            }
            while (i4 < i) {
                AtomicFile atomicFile = (AtomicFile) timeSparseArray.valueAt(i4);
                File file = new File(atomicFile.getBaseFile().getPath() + "-c");
                if (!atomicFile.getBaseFile().renameTo(file)) {
                    Slog.e("UsageStatsDatabase", "Failed to mark file " + atomicFile.getBaseFile().getPath() + " as checked-in");
                    return true;
                }
                timeSparseArray.setValueAt(i4, new AtomicFile(file));
                i4++;
            }
            return true;
        }
    }

    @VisibleForTesting
    public void forceIndexFiles() {
        synchronized (this.mLock) {
            indexFilesLocked();
        }
    }

    public final void indexFilesLocked() {
        FilenameFilter filenameFilter = new FilenameFilter() { // from class: com.android.server.usage.UsageStatsDatabase.1
            @Override // java.io.FilenameFilter
            public boolean accept(File file, String str) {
                return !str.endsWith(".bak");
            }
        };
        int i = 0;
        while (true) {
            TimeSparseArray<AtomicFile>[] timeSparseArrayArr = this.mSortedStatFiles;
            if (i >= timeSparseArrayArr.length) {
                return;
            }
            TimeSparseArray<AtomicFile> timeSparseArray = timeSparseArrayArr[i];
            if (timeSparseArray == null) {
                timeSparseArrayArr[i] = new TimeSparseArray<>();
            } else {
                timeSparseArray.clear();
            }
            File[] listFiles = this.mIntervalDirs[i].listFiles(filenameFilter);
            if (listFiles != null) {
                for (File file : listFiles) {
                    AtomicFile atomicFile = new AtomicFile(file);
                    try {
                        this.mSortedStatFiles[i].put(parseBeginTime(atomicFile), atomicFile);
                    } catch (IOException e) {
                        Slog.e("UsageStatsDatabase", "failed to index file: " + file, e);
                    }
                }
                int size = this.mSortedStatFiles[i].size() - MAX_FILES_PER_INTERVAL_TYPE[i];
                if (size > 0) {
                    for (int i2 = 0; i2 < size; i2++) {
                        ((AtomicFile) this.mSortedStatFiles[i].valueAt(0)).delete();
                        this.mSortedStatFiles[i].removeAt(0);
                    }
                    Slog.d("UsageStatsDatabase", "Deleted " + size + " stat files for interval " + i);
                }
            }
            i++;
        }
    }

    public boolean isNewUpdate() {
        return this.mNewUpdate;
    }

    public boolean wasUpgradePerformed() {
        return this.mUpgradePerformed;
    }

    public final void checkVersionAndBuildLocked() {
        String buildFingerprint = getBuildFingerprint();
        this.mFirstUpdate = true;
        this.mNewUpdate = true;
        int i = 0;
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(this.mVersionFile));
            int parseInt = Integer.parseInt(bufferedReader.readLine());
            String readLine = bufferedReader.readLine();
            if (readLine != null) {
                this.mFirstUpdate = false;
            }
            if (buildFingerprint.equals(readLine)) {
                this.mNewUpdate = false;
            }
            bufferedReader.close();
            i = parseInt;
        } catch (IOException | NumberFormatException unused) {
        }
        if (i != this.mCurrentVersion) {
            Slog.i("UsageStatsDatabase", "Upgrading from version " + i + " to " + this.mCurrentVersion);
            if (!this.mUpdateBreadcrumb.exists()) {
                try {
                    doUpgradeLocked(i);
                } catch (Exception e) {
                    Slog.e("UsageStatsDatabase", "Failed to upgrade from version " + i + " to " + this.mCurrentVersion, e);
                    this.mCurrentVersion = i;
                    return;
                }
            } else {
                Slog.i("UsageStatsDatabase", "Version upgrade breadcrumb found on disk! Continuing version upgrade");
            }
        }
        if (this.mUpdateBreadcrumb.exists()) {
            try {
                BufferedReader bufferedReader2 = new BufferedReader(new FileReader(this.mUpdateBreadcrumb));
                long parseLong = Long.parseLong(bufferedReader2.readLine());
                int parseInt2 = Integer.parseInt(bufferedReader2.readLine());
                bufferedReader2.close();
                if (this.mCurrentVersion >= 4) {
                    continueUpgradeLocked(parseInt2, parseLong);
                } else {
                    Slog.wtf("UsageStatsDatabase", "Attempting to upgrade to an unsupported version: " + this.mCurrentVersion);
                }
            } catch (IOException | NumberFormatException e2) {
                Slog.e("UsageStatsDatabase", "Failed read version upgrade breadcrumb");
                throw new RuntimeException(e2);
            }
        }
        if (i != this.mCurrentVersion || this.mNewUpdate) {
            try {
                BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(this.mVersionFile));
                bufferedWriter.write(Integer.toString(this.mCurrentVersion));
                bufferedWriter.write("\n");
                bufferedWriter.write(buildFingerprint);
                bufferedWriter.write("\n");
                bufferedWriter.flush();
                bufferedWriter.close();
            } catch (IOException e3) {
                Slog.e("UsageStatsDatabase", "Failed to write new version");
                throw new RuntimeException(e3);
            }
        }
        if (this.mUpdateBreadcrumb.exists()) {
            this.mUpdateBreadcrumb.delete();
            this.mUpgradePerformed = true;
        }
        if (this.mBackupsDir.exists()) {
            this.mUpgradePerformed = true;
            deleteDirectory(this.mBackupsDir);
        }
    }

    public final String getBuildFingerprint() {
        return Build.VERSION.RELEASE + ";" + Build.VERSION.CODENAME + ";" + Build.VERSION.INCREMENTAL;
    }

    public final void doUpgradeLocked(int i) {
        BufferedWriter bufferedWriter;
        if (i < 2) {
            Slog.i("UsageStatsDatabase", "Deleting all usage stats files");
            int i2 = 0;
            while (true) {
                File[] fileArr = this.mIntervalDirs;
                if (i2 >= fileArr.length) {
                    return;
                }
                File[] listFiles = fileArr[i2].listFiles();
                if (listFiles != null) {
                    for (File file : listFiles) {
                        file.delete();
                    }
                }
                i2++;
            }
        } else {
            long currentTimeMillis = System.currentTimeMillis();
            File file2 = new File(this.mBackupsDir, Long.toString(currentTimeMillis));
            file2.mkdirs();
            if (!file2.exists()) {
                throw new IllegalStateException("Failed to create backup directory " + file2.getAbsolutePath());
            }
            try {
                Files.copy(this.mVersionFile.toPath(), new File(file2, this.mVersionFile.getName()).toPath(), StandardCopyOption.REPLACE_EXISTING);
                for (int i3 = 0; i3 < this.mIntervalDirs.length; i3++) {
                    File file3 = new File(file2, this.mIntervalDirs[i3].getName());
                    file3.mkdir();
                    if (!file3.exists()) {
                        throw new IllegalStateException("Failed to create interval backup directory " + file3.getAbsolutePath());
                    }
                    File[] listFiles2 = this.mIntervalDirs[i3].listFiles();
                    if (listFiles2 != null) {
                        for (int i4 = 0; i4 < listFiles2.length; i4++) {
                            try {
                                Files.move(listFiles2[i4].toPath(), new File(file3, listFiles2[i4].getName()).toPath(), StandardCopyOption.REPLACE_EXISTING);
                            } catch (IOException e) {
                                Slog.e("UsageStatsDatabase", "Failed to back up file : " + listFiles2[i4].toString());
                                throw new RuntimeException(e);
                            }
                        }
                        continue;
                    }
                }
                BufferedWriter bufferedWriter2 = null;
                try {
                    try {
                        bufferedWriter = new BufferedWriter(new FileWriter(this.mUpdateBreadcrumb));
                    } catch (IOException e2) {
                        e = e2;
                    }
                } catch (Throwable th) {
                    th = th;
                }
                try {
                    bufferedWriter.write(Long.toString(currentTimeMillis));
                    bufferedWriter.write("\n");
                    bufferedWriter.write(Integer.toString(i));
                    bufferedWriter.write("\n");
                    bufferedWriter.flush();
                    IoUtils.closeQuietly(bufferedWriter);
                } catch (IOException e3) {
                    e = e3;
                    bufferedWriter2 = bufferedWriter;
                    Slog.e("UsageStatsDatabase", "Failed to write new version upgrade breadcrumb");
                    throw new RuntimeException(e);
                } catch (Throwable th2) {
                    th = th2;
                    bufferedWriter2 = bufferedWriter;
                    IoUtils.closeQuietly(bufferedWriter2);
                    throw th;
                }
            } catch (IOException e4) {
                Slog.e("UsageStatsDatabase", "Failed to back up version file : " + this.mVersionFile.toString());
                throw new RuntimeException(e4);
            }
        }
    }

    public final void continueUpgradeLocked(int i, long j) {
        if (i <= 3) {
            Slog.w("UsageStatsDatabase", "Reading UsageStats as XML; current database version: " + this.mCurrentVersion);
        }
        File file = new File(this.mBackupsDir, Long.toString(j));
        if (i >= 5) {
            readMappingsLocked();
        }
        for (int i2 = 0; i2 < this.mIntervalDirs.length; i2++) {
            File[] listFiles = new File(file, this.mIntervalDirs[i2].getName()).listFiles();
            if (listFiles != null) {
                for (int i3 = 0; i3 < listFiles.length; i3++) {
                    try {
                        IntervalStats intervalStats = new IntervalStats();
                        readLocked(new AtomicFile(listFiles[i3]), intervalStats, i, this.mPackagesTokenData);
                        if (this.mCurrentVersion >= 5) {
                            intervalStats.obfuscateData(this.mPackagesTokenData);
                        }
                        writeLocked(new AtomicFile(new File(this.mIntervalDirs[i2], Long.toString(intervalStats.beginTime))), intervalStats, this.mCurrentVersion, this.mPackagesTokenData);
                    } catch (Exception unused) {
                        Slog.e("UsageStatsDatabase", "Failed to upgrade backup file : " + listFiles[i3].toString());
                    }
                }
            }
        }
        if (this.mCurrentVersion >= 5) {
            try {
                writeMappingsLocked();
            } catch (IOException unused2) {
                Slog.e("UsageStatsDatabase", "Failed to write the tokens mappings file.");
            }
        }
    }

    public int onPackageRemoved(String str, long j) {
        int removePackage;
        synchronized (this.mLock) {
            removePackage = this.mPackagesTokenData.removePackage(str, j);
            try {
                writeMappingsLocked();
            } catch (Exception unused) {
                Slog.w("UsageStatsDatabase", "Unable to update package mappings on disk after removing token " + removePackage);
            }
        }
        return removePackage;
    }

    public boolean pruneUninstalledPackagesData() {
        synchronized (this.mLock) {
            int i = 0;
            while (true) {
                File[] fileArr = this.mIntervalDirs;
                if (i < fileArr.length) {
                    File[] listFiles = fileArr[i].listFiles();
                    if (listFiles != null) {
                        for (int i2 = 0; i2 < listFiles.length; i2++) {
                            try {
                                IntervalStats intervalStats = new IntervalStats();
                                AtomicFile atomicFile = new AtomicFile(listFiles[i2]);
                                if (readLocked(atomicFile, intervalStats, this.mCurrentVersion, this.mPackagesTokenData)) {
                                    writeLocked(atomicFile, intervalStats, this.mCurrentVersion, this.mPackagesTokenData);
                                }
                            } catch (Exception unused) {
                                Slog.e("UsageStatsDatabase", "Failed to prune data from: " + listFiles[i2].toString());
                                return false;
                            }
                        }
                        continue;
                    }
                    i++;
                } else {
                    try {
                        writeMappingsLocked();
                    } catch (IOException unused2) {
                        Slog.e("UsageStatsDatabase", "Failed to write package mappings after pruning data.");
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public void prunePackagesDataOnUpgrade(HashMap<String, Long> hashMap) {
        if (ArrayUtils.isEmpty(hashMap)) {
            return;
        }
        synchronized (this.mLock) {
            int i = 0;
            while (true) {
                File[] fileArr = this.mIntervalDirs;
                if (i < fileArr.length) {
                    File[] listFiles = fileArr[i].listFiles();
                    if (listFiles != null) {
                        for (int i2 = 0; i2 < listFiles.length; i2++) {
                            try {
                                IntervalStats intervalStats = new IntervalStats();
                                AtomicFile atomicFile = new AtomicFile(listFiles[i2]);
                                readLocked(atomicFile, intervalStats, this.mCurrentVersion, this.mPackagesTokenData);
                                if (pruneStats(hashMap, intervalStats)) {
                                    writeLocked(atomicFile, intervalStats, this.mCurrentVersion, this.mPackagesTokenData);
                                }
                            } catch (Exception unused) {
                                Slog.e("UsageStatsDatabase", "Failed to prune data from: " + listFiles[i2].toString());
                            }
                        }
                    }
                    i++;
                }
            }
        }
    }

    public final boolean pruneStats(HashMap<String, Long> hashMap, IntervalStats intervalStats) {
        boolean z = false;
        for (int size = intervalStats.packageStats.size() - 1; size >= 0; size--) {
            UsageStats valueAt = intervalStats.packageStats.valueAt(size);
            Long l = hashMap.get(valueAt.mPackageName);
            if (l == null || l.longValue() > valueAt.mEndTimeStamp) {
                intervalStats.packageStats.removeAt(size);
                z = true;
            }
        }
        if (z) {
            intervalStats.packageStatsObfuscated.clear();
        }
        for (int size2 = intervalStats.events.size() - 1; size2 >= 0; size2--) {
            UsageEvents.Event event = intervalStats.events.get(size2);
            Long l2 = hashMap.get(event.mPackage);
            if (l2 == null || l2.longValue() > event.mTimeStamp) {
                intervalStats.events.remove(size2);
                z = true;
            }
        }
        return z;
    }

    public void onTimeChanged(long j) {
        TimeSparseArray<AtomicFile>[] timeSparseArrayArr;
        synchronized (this.mLock) {
            StringBuilder sb = new StringBuilder();
            sb.append("Time changed by ");
            TimeUtils.formatDuration(j, sb);
            sb.append(".");
            int i = 0;
            int i2 = 0;
            for (TimeSparseArray<AtomicFile> timeSparseArray : this.mSortedStatFiles) {
                int size = timeSparseArray.size();
                for (int i3 = 0; i3 < size; i3++) {
                    AtomicFile atomicFile = (AtomicFile) timeSparseArray.valueAt(i3);
                    long keyAt = timeSparseArray.keyAt(i3) + j;
                    if (keyAt < 0) {
                        i++;
                        atomicFile.delete();
                    } else {
                        try {
                            atomicFile.openRead().close();
                        } catch (IOException unused) {
                        }
                        String l = Long.toString(keyAt);
                        if (atomicFile.getBaseFile().getName().endsWith("-c")) {
                            l = l + "-c";
                        }
                        i2++;
                        atomicFile.getBaseFile().renameTo(new File(atomicFile.getBaseFile().getParentFile(), l));
                    }
                }
                timeSparseArray.clear();
            }
            sb.append(" files deleted: ");
            sb.append(i);
            sb.append(" files moved: ");
            sb.append(i2);
            Slog.i("UsageStatsDatabase", sb.toString());
            indexFilesLocked();
        }
    }

    public IntervalStats getLatestUsageStats(int i) {
        synchronized (this.mLock) {
            if (i >= 0) {
                if (i < this.mIntervalDirs.length) {
                    int size = this.mSortedStatFiles[i].size();
                    if (size == 0) {
                        return null;
                    }
                    try {
                        IntervalStats intervalStats = new IntervalStats();
                        readLocked((AtomicFile) this.mSortedStatFiles[i].valueAt(size - 1), intervalStats);
                        return intervalStats;
                    } catch (Exception e) {
                        Slog.e("UsageStatsDatabase", "Failed to read usage stats file", e);
                        return null;
                    }
                }
            }
            throw new IllegalArgumentException("Bad interval type " + i);
        }
    }

    public void filterStats(IntervalStats intervalStats) {
        if (this.mPackagesTokenData.removedPackagesMap.isEmpty()) {
            return;
        }
        ArrayMap<String, Long> arrayMap = this.mPackagesTokenData.removedPackagesMap;
        int size = arrayMap.size();
        for (int i = 0; i < size; i++) {
            String keyAt = arrayMap.keyAt(i);
            UsageStats usageStats = intervalStats.packageStats.get(keyAt);
            if (usageStats != null && usageStats.mEndTimeStamp < arrayMap.valueAt(i).longValue()) {
                intervalStats.packageStats.remove(keyAt);
            }
        }
        for (int size2 = intervalStats.events.size() - 1; size2 >= 0; size2--) {
            UsageEvents.Event event = intervalStats.events.get(size2);
            Long l = arrayMap.get(event.mPackage);
            if (l != null && l.longValue() > event.mTimeStamp) {
                intervalStats.events.remove(size2);
            }
        }
    }

    public <T> List<T> queryUsageStats(int i, long j, long j2, StatCombiner<T> statCombiner) {
        if (i < 0 || i >= this.mIntervalDirs.length) {
            throw new IllegalArgumentException("Bad interval type " + i);
        } else if (j2 <= j) {
            return null;
        } else {
            synchronized (this.mLock) {
                TimeSparseArray<AtomicFile> timeSparseArray = this.mSortedStatFiles[i];
                int closestIndexOnOrBefore = timeSparseArray.closestIndexOnOrBefore(j2);
                if (closestIndexOnOrBefore < 0) {
                    return null;
                }
                if (timeSparseArray.keyAt(closestIndexOnOrBefore) != j2 || closestIndexOnOrBefore - 1 >= 0) {
                    int closestIndexOnOrBefore2 = timeSparseArray.closestIndexOnOrBefore(j);
                    if (closestIndexOnOrBefore2 < 0) {
                        closestIndexOnOrBefore2 = 0;
                    }
                    ArrayList arrayList = new ArrayList();
                    while (closestIndexOnOrBefore2 <= closestIndexOnOrBefore) {
                        AtomicFile atomicFile = (AtomicFile) timeSparseArray.valueAt(closestIndexOnOrBefore2);
                        IntervalStats intervalStats = new IntervalStats();
                        try {
                            readLocked(atomicFile, intervalStats);
                            if (j < intervalStats.endTime && !statCombiner.combine(intervalStats, false, arrayList)) {
                                break;
                            }
                        } catch (Exception e) {
                            Slog.e("UsageStatsDatabase", "Failed to read usage stats file", e);
                        }
                        closestIndexOnOrBefore2++;
                    }
                    return arrayList;
                }
                return null;
            }
        }
    }

    public int findBestFitBucket(long j, long j2) {
        int i;
        synchronized (this.mLock) {
            i = -1;
            long j3 = Long.MAX_VALUE;
            for (int length = this.mSortedStatFiles.length - 1; length >= 0; length--) {
                int closestIndexOnOrBefore = this.mSortedStatFiles[length].closestIndexOnOrBefore(j);
                int size = this.mSortedStatFiles[length].size();
                if (closestIndexOnOrBefore >= 0 && closestIndexOnOrBefore < size) {
                    long abs = Math.abs(this.mSortedStatFiles[length].keyAt(closestIndexOnOrBefore) - j);
                    if (abs < j3) {
                        i = length;
                        j3 = abs;
                    }
                }
            }
        }
        return i;
    }

    public void prune(long j) {
        synchronized (this.mLock) {
            this.mCal.setTimeInMillis(j);
            this.mCal.addYears(-3);
            pruneFilesOlderThan(this.mIntervalDirs[3], this.mCal.getTimeInMillis());
            this.mCal.setTimeInMillis(j);
            this.mCal.addMonths(-6);
            pruneFilesOlderThan(this.mIntervalDirs[2], this.mCal.getTimeInMillis());
            this.mCal.setTimeInMillis(j);
            this.mCal.addWeeks(-4);
            pruneFilesOlderThan(this.mIntervalDirs[1], this.mCal.getTimeInMillis());
            this.mCal.setTimeInMillis(j);
            this.mCal.addDays(-10);
            int i = 0;
            pruneFilesOlderThan(this.mIntervalDirs[0], this.mCal.getTimeInMillis());
            this.mCal.setTimeInMillis(j);
            this.mCal.addDays(-SELECTION_LOG_RETENTION_LEN);
            while (true) {
                File[] fileArr = this.mIntervalDirs;
                if (i < fileArr.length) {
                    pruneChooserCountsOlderThan(fileArr[i], this.mCal.getTimeInMillis());
                    i++;
                } else {
                    indexFilesLocked();
                }
            }
        }
    }

    public static void pruneFilesOlderThan(File file, long j) {
        long j2;
        File[] listFiles = file.listFiles();
        if (listFiles != null) {
            for (File file2 : listFiles) {
                try {
                    j2 = parseBeginTime(file2);
                } catch (IOException unused) {
                    j2 = 0;
                }
                if (j2 < j) {
                    new AtomicFile(file2).delete();
                }
            }
        }
    }

    public final void pruneChooserCountsOlderThan(File file, long j) {
        long j2;
        File[] listFiles = file.listFiles();
        if (listFiles != null) {
            for (File file2 : listFiles) {
                try {
                    j2 = parseBeginTime(file2);
                } catch (IOException unused) {
                    j2 = 0;
                }
                if (j2 < j) {
                    try {
                        AtomicFile atomicFile = new AtomicFile(file2);
                        IntervalStats intervalStats = new IntervalStats();
                        readLocked(atomicFile, intervalStats);
                        int size = intervalStats.packageStats.size();
                        for (int i = 0; i < size; i++) {
                            ArrayMap arrayMap = intervalStats.packageStats.valueAt(i).mChooserCounts;
                            if (arrayMap != null) {
                                arrayMap.clear();
                            }
                        }
                        writeLocked(atomicFile, intervalStats);
                    } catch (Exception e) {
                        Slog.e("UsageStatsDatabase", "Failed to delete chooser counts from usage stats file", e);
                    }
                }
            }
        }
    }

    public static long parseBeginTime(AtomicFile atomicFile) throws IOException {
        return parseBeginTime(atomicFile.getBaseFile());
    }

    public static long parseBeginTime(File file) throws IOException {
        String name = file.getName();
        for (int i = 0; i < name.length(); i++) {
            char charAt = name.charAt(i);
            if (charAt < '0' || charAt > '9') {
                name = name.substring(0, i);
                break;
            }
        }
        try {
            return Long.parseLong(name);
        } catch (NumberFormatException e) {
            throw new IOException(e);
        }
    }

    public final void writeLocked(AtomicFile atomicFile, IntervalStats intervalStats) throws IOException, RuntimeException {
        int i = this.mCurrentVersion;
        if (i <= 3) {
            Slog.wtf("UsageStatsDatabase", "Attempting to write UsageStats as XML with version " + this.mCurrentVersion);
            return;
        }
        writeLocked(atomicFile, intervalStats, i, this.mPackagesTokenData);
    }

    public static void writeLocked(AtomicFile atomicFile, IntervalStats intervalStats, int i, PackagesTokenData packagesTokenData) throws IOException, RuntimeException {
        FileOutputStream startWrite = atomicFile.startWrite();
        try {
            writeLocked(startWrite, intervalStats, i, packagesTokenData);
            atomicFile.finishWrite(startWrite);
            atomicFile.failWrite(null);
        } catch (Exception unused) {
            atomicFile.failWrite(startWrite);
        } catch (Throwable th) {
            atomicFile.failWrite(startWrite);
            throw th;
        }
    }

    public static void writeLocked(OutputStream outputStream, IntervalStats intervalStats, int i, PackagesTokenData packagesTokenData) throws Exception {
        if (i == 1 || i == 2 || i == 3) {
            Slog.wtf("UsageStatsDatabase", "Attempting to write UsageStats as XML with version " + i);
        } else if (i == 4) {
            try {
                UsageStatsProto.write(outputStream, intervalStats);
            } catch (Exception e) {
                Slog.e("UsageStatsDatabase", "Unable to write interval stats to proto.", e);
                throw e;
            }
        } else if (i == 5) {
            intervalStats.obfuscateData(packagesTokenData);
            try {
                UsageStatsProtoV2.write(outputStream, intervalStats);
            } catch (Exception e2) {
                Slog.e("UsageStatsDatabase", "Unable to write interval stats to proto.", e2);
                throw e2;
            }
        } else {
            throw new RuntimeException("Unhandled UsageStatsDatabase version: " + Integer.toString(i) + " on write.");
        }
    }

    public final void readLocked(AtomicFile atomicFile, IntervalStats intervalStats) throws IOException, RuntimeException {
        if (this.mCurrentVersion <= 3) {
            Slog.wtf("UsageStatsDatabase", "Reading UsageStats as XML; current database version: " + this.mCurrentVersion);
        }
        readLocked(atomicFile, intervalStats, this.mCurrentVersion, this.mPackagesTokenData);
    }

    public static boolean readLocked(AtomicFile atomicFile, IntervalStats intervalStats, int i, PackagesTokenData packagesTokenData) throws IOException, RuntimeException {
        try {
            FileInputStream openRead = atomicFile.openRead();
            intervalStats.beginTime = parseBeginTime(atomicFile);
            boolean readLocked = readLocked(openRead, intervalStats, i, packagesTokenData);
            intervalStats.lastTimeSaved = atomicFile.getLastModifiedTime();
            try {
                openRead.close();
            } catch (IOException unused) {
            }
            return readLocked;
        } catch (FileNotFoundException e) {
            Slog.e("UsageStatsDatabase", "UsageStatsDatabase", e);
            throw e;
        }
    }

    public static boolean readLocked(InputStream inputStream, IntervalStats intervalStats, int i, PackagesTokenData packagesTokenData) throws RuntimeException {
        if (i == 1 || i == 2 || i == 3) {
            Slog.w("UsageStatsDatabase", "Reading UsageStats as XML; database version: " + i);
            try {
                UsageStatsXml.read(inputStream, intervalStats);
            } catch (Exception e) {
                Slog.e("UsageStatsDatabase", "Unable to read interval stats from XML", e);
            }
        } else if (i != 4) {
            if (i == 5) {
                try {
                    UsageStatsProtoV2.read(inputStream, intervalStats);
                } catch (Exception e2) {
                    Slog.e("UsageStatsDatabase", "Unable to read interval stats from proto.", e2);
                }
                return intervalStats.deobfuscateData(packagesTokenData);
            }
            throw new RuntimeException("Unhandled UsageStatsDatabase version: " + Integer.toString(i) + " on read.");
        } else {
            try {
                UsageStatsProto.read(inputStream, intervalStats);
            } catch (Exception e3) {
                Slog.e("UsageStatsDatabase", "Unable to read interval stats from proto.", e3);
            }
        }
        return false;
    }

    public void readMappingsLocked() {
        if (this.mPackageMappingsFile.exists()) {
            try {
                FileInputStream openRead = new AtomicFile(this.mPackageMappingsFile).openRead();
                UsageStatsProtoV2.readObfuscatedData(openRead, this.mPackagesTokenData);
                if (openRead != null) {
                    openRead.close();
                }
                SparseArray<ArrayList<String>> sparseArray = this.mPackagesTokenData.tokensToPackagesMap;
                int size = sparseArray.size();
                for (int i = 0; i < size; i++) {
                    int keyAt = sparseArray.keyAt(i);
                    ArrayList<String> valueAt = sparseArray.valueAt(i);
                    ArrayMap<String, Integer> arrayMap = new ArrayMap<>();
                    int size2 = valueAt.size();
                    arrayMap.put(valueAt.get(0), Integer.valueOf(keyAt));
                    for (int i2 = 1; i2 < size2; i2++) {
                        arrayMap.put(valueAt.get(i2), Integer.valueOf(i2));
                    }
                    this.mPackagesTokenData.packagesToTokensMap.put(valueAt.get(0), arrayMap);
                }
            } catch (Exception e) {
                Slog.e("UsageStatsDatabase", "Failed to read the obfuscated packages mapping file.", e);
            }
        }
    }

    public void writeMappingsLocked() throws IOException {
        AtomicFile atomicFile = new AtomicFile(this.mPackageMappingsFile);
        FileOutputStream startWrite = atomicFile.startWrite();
        try {
            try {
                UsageStatsProtoV2.writeObfuscatedData(startWrite, this.mPackagesTokenData);
                atomicFile.finishWrite(startWrite);
                atomicFile.failWrite(null);
            } catch (Exception e) {
                Slog.e("UsageStatsDatabase", "Unable to write obfuscated data to proto.", e);
                atomicFile.failWrite(startWrite);
            }
        } catch (Throwable th) {
            atomicFile.failWrite(startWrite);
            throw th;
        }
    }

    public void obfuscateCurrentStats(IntervalStats[] intervalStatsArr) {
        if (this.mCurrentVersion < 5) {
            return;
        }
        for (IntervalStats intervalStats : intervalStatsArr) {
            intervalStats.obfuscateData(this.mPackagesTokenData);
        }
    }

    public void putUsageStats(int i, IntervalStats intervalStats) throws IOException {
        if (intervalStats == null) {
            return;
        }
        synchronized (this.mLock) {
            if (i >= 0) {
                if (i < this.mIntervalDirs.length) {
                    AtomicFile atomicFile = (AtomicFile) this.mSortedStatFiles[i].get(intervalStats.beginTime);
                    if (atomicFile == null) {
                        atomicFile = new AtomicFile(new File(this.mIntervalDirs[i], Long.toString(intervalStats.beginTime)));
                        this.mSortedStatFiles[i].put(intervalStats.beginTime, atomicFile);
                    }
                    writeLocked(atomicFile, intervalStats);
                    intervalStats.lastTimeSaved = atomicFile.getLastModifiedTime();
                }
            }
            throw new IllegalArgumentException("Bad interval type " + i);
        }
    }

    public byte[] getBackupPayload(String str) {
        return getBackupPayload(str, 4);
    }

    @VisibleForTesting
    public byte[] getBackupPayload(String str, int i) {
        byte[] byteArray;
        if (i >= 1 && i <= 3) {
            Slog.wtf("UsageStatsDatabase", "Attempting to backup UsageStats as XML with version " + i);
            return null;
        } else if (i < 1 || i > 4) {
            Slog.wtf("UsageStatsDatabase", "Attempting to backup UsageStats with an unknown version: " + i);
            return null;
        } else {
            synchronized (this.mLock) {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                if ("usage_stats".equals(str)) {
                    prune(System.currentTimeMillis());
                    DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
                    try {
                        dataOutputStream.writeInt(i);
                        dataOutputStream.writeInt(this.mSortedStatFiles[0].size());
                        for (int i2 = 0; i2 < this.mSortedStatFiles[0].size(); i2++) {
                            writeIntervalStatsToStream(dataOutputStream, (AtomicFile) this.mSortedStatFiles[0].valueAt(i2), i);
                        }
                        dataOutputStream.writeInt(this.mSortedStatFiles[1].size());
                        for (int i3 = 0; i3 < this.mSortedStatFiles[1].size(); i3++) {
                            writeIntervalStatsToStream(dataOutputStream, (AtomicFile) this.mSortedStatFiles[1].valueAt(i3), i);
                        }
                        dataOutputStream.writeInt(this.mSortedStatFiles[2].size());
                        for (int i4 = 0; i4 < this.mSortedStatFiles[2].size(); i4++) {
                            writeIntervalStatsToStream(dataOutputStream, (AtomicFile) this.mSortedStatFiles[2].valueAt(i4), i);
                        }
                        dataOutputStream.writeInt(this.mSortedStatFiles[3].size());
                        for (int i5 = 0; i5 < this.mSortedStatFiles[3].size(); i5++) {
                            writeIntervalStatsToStream(dataOutputStream, (AtomicFile) this.mSortedStatFiles[3].valueAt(i5), i);
                        }
                    } catch (IOException e) {
                        Slog.d("UsageStatsDatabase", "Failed to write data to output stream", e);
                        byteArrayOutputStream.reset();
                    }
                }
                byteArray = byteArrayOutputStream.toByteArray();
            }
            return byteArray;
        }
    }

    public final void calculatePackagesUsedWithinTimeframe(IntervalStats intervalStats, Set<String> set, long j) {
        for (UsageStats usageStats : intervalStats.packageStats.values()) {
            if (usageStats.getLastTimePackageUsed() > j) {
                set.add(usageStats.mPackageName);
            }
        }
    }

    @VisibleForTesting
    public Set<String> applyRestoredPayload(String str, byte[] bArr) {
        DataInputStream dataInputStream;
        int readInt;
        synchronized (this.mLock) {
            if ("usage_stats".equals(str)) {
                int i = 0;
                IntervalStats latestUsageStats = getLatestUsageStats(0);
                IntervalStats latestUsageStats2 = getLatestUsageStats(1);
                IntervalStats latestUsageStats3 = getLatestUsageStats(2);
                IntervalStats latestUsageStats4 = getLatestUsageStats(3);
                ArraySet arraySet = new ArraySet();
                try {
                    dataInputStream = new DataInputStream(new ByteArrayInputStream(bArr));
                    readInt = dataInputStream.readInt();
                } catch (IOException e) {
                    Slog.d("UsageStatsDatabase", "Failed to read data from input stream", e);
                }
                if (readInt >= 1 && readInt <= 4) {
                    int i2 = 0;
                    while (true) {
                        File[] fileArr = this.mIntervalDirs;
                        if (i2 >= fileArr.length) {
                            break;
                        }
                        deleteDirectoryContents(fileArr[i2]);
                        i2++;
                    }
                    IntervalStats intervalStats = latestUsageStats4;
                    long currentTimeMillis = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(90L);
                    int readInt2 = dataInputStream.readInt();
                    for (int i3 = 0; i3 < readInt2; i3++) {
                        IntervalStats deserializeIntervalStats = deserializeIntervalStats(getIntervalStatsBytes(dataInputStream), readInt);
                        calculatePackagesUsedWithinTimeframe(deserializeIntervalStats, arraySet, currentTimeMillis);
                        arraySet.addAll(deserializeIntervalStats.packageStats.keySet());
                        putUsageStats(0, mergeStats(deserializeIntervalStats, latestUsageStats));
                    }
                    int readInt3 = dataInputStream.readInt();
                    for (int i4 = 0; i4 < readInt3; i4++) {
                        IntervalStats deserializeIntervalStats2 = deserializeIntervalStats(getIntervalStatsBytes(dataInputStream), readInt);
                        calculatePackagesUsedWithinTimeframe(deserializeIntervalStats2, arraySet, currentTimeMillis);
                        putUsageStats(1, mergeStats(deserializeIntervalStats2, latestUsageStats2));
                    }
                    int readInt4 = dataInputStream.readInt();
                    for (int i5 = 0; i5 < readInt4; i5++) {
                        IntervalStats deserializeIntervalStats3 = deserializeIntervalStats(getIntervalStatsBytes(dataInputStream), readInt);
                        calculatePackagesUsedWithinTimeframe(deserializeIntervalStats3, arraySet, currentTimeMillis);
                        putUsageStats(2, mergeStats(deserializeIntervalStats3, latestUsageStats3));
                    }
                    int readInt5 = dataInputStream.readInt();
                    while (i < readInt5) {
                        IntervalStats deserializeIntervalStats4 = deserializeIntervalStats(getIntervalStatsBytes(dataInputStream), readInt);
                        calculatePackagesUsedWithinTimeframe(deserializeIntervalStats4, arraySet, currentTimeMillis);
                        IntervalStats intervalStats2 = intervalStats;
                        putUsageStats(3, mergeStats(deserializeIntervalStats4, intervalStats2));
                        i++;
                        intervalStats = intervalStats2;
                    }
                    indexFilesLocked();
                    return arraySet;
                }
                indexFilesLocked();
                return arraySet;
            }
            return Collections.EMPTY_SET;
        }
    }

    public final IntervalStats mergeStats(IntervalStats intervalStats, IntervalStats intervalStats2) {
        if (intervalStats2 == null) {
            return intervalStats;
        }
        if (intervalStats == null) {
            return null;
        }
        intervalStats.activeConfiguration = intervalStats2.activeConfiguration;
        intervalStats.configurations.putAll((ArrayMap<? extends Configuration, ? extends ConfigurationStats>) intervalStats2.configurations);
        intervalStats.events.clear();
        intervalStats.events.merge(intervalStats2.events);
        return intervalStats;
    }

    public final void writeIntervalStatsToStream(DataOutputStream dataOutputStream, AtomicFile atomicFile, int i) throws IOException {
        IntervalStats intervalStats = new IntervalStats();
        try {
            readLocked(atomicFile, intervalStats);
            sanitizeIntervalStatsForBackup(intervalStats);
            byte[] serializeIntervalStats = serializeIntervalStats(intervalStats, i);
            dataOutputStream.writeInt(serializeIntervalStats.length);
            dataOutputStream.write(serializeIntervalStats);
        } catch (IOException e) {
            Slog.e("UsageStatsDatabase", "Failed to read usage stats file", e);
            dataOutputStream.writeInt(0);
        }
    }

    public static byte[] getIntervalStatsBytes(DataInputStream dataInputStream) throws IOException {
        int readInt = dataInputStream.readInt();
        byte[] bArr = new byte[readInt];
        dataInputStream.read(bArr, 0, readInt);
        return bArr;
    }

    public static void sanitizeIntervalStatsForBackup(IntervalStats intervalStats) {
        if (intervalStats == null) {
            return;
        }
        intervalStats.activeConfiguration = null;
        intervalStats.configurations.clear();
        intervalStats.events.clear();
    }

    public final byte[] serializeIntervalStats(IntervalStats intervalStats, int i) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
        try {
            dataOutputStream.writeLong(intervalStats.beginTime);
            writeLocked(dataOutputStream, intervalStats, i, this.mPackagesTokenData);
        } catch (Exception e) {
            Slog.d("UsageStatsDatabase", "Serializing IntervalStats Failed", e);
            byteArrayOutputStream.reset();
        }
        return byteArrayOutputStream.toByteArray();
    }

    public final IntervalStats deserializeIntervalStats(byte[] bArr, int i) {
        DataInputStream dataInputStream = new DataInputStream(new ByteArrayInputStream(bArr));
        IntervalStats intervalStats = new IntervalStats();
        try {
            intervalStats.beginTime = dataInputStream.readLong();
            readLocked(dataInputStream, intervalStats, i, this.mPackagesTokenData);
            return intervalStats;
        } catch (Exception e) {
            Slog.d("UsageStatsDatabase", "DeSerializing IntervalStats Failed", e);
            return null;
        }
    }

    public static void deleteDirectoryContents(File file) {
        for (File file2 : file.listFiles()) {
            deleteDirectory(file2);
        }
    }

    public static void deleteDirectory(File file) {
        File[] listFiles = file.listFiles();
        if (listFiles != null) {
            for (File file2 : listFiles) {
                if (!file2.isDirectory()) {
                    file2.delete();
                } else {
                    deleteDirectory(file2);
                }
            }
        }
        file.delete();
    }

    public void dump(IndentingPrintWriter indentingPrintWriter, boolean z) {
        synchronized (this.mLock) {
            indentingPrintWriter.println();
            indentingPrintWriter.println("UsageStatsDatabase:");
            indentingPrintWriter.increaseIndent();
            dumpMappings(indentingPrintWriter);
            indentingPrintWriter.decreaseIndent();
            indentingPrintWriter.println("Database Summary:");
            indentingPrintWriter.increaseIndent();
            int i = 0;
            while (true) {
                TimeSparseArray<AtomicFile>[] timeSparseArrayArr = this.mSortedStatFiles;
                if (i < timeSparseArrayArr.length) {
                    TimeSparseArray<AtomicFile> timeSparseArray = timeSparseArrayArr[i];
                    int size = timeSparseArray.size();
                    indentingPrintWriter.print(UserUsageStatsService.intervalToString(i));
                    indentingPrintWriter.print(" stats files: ");
                    indentingPrintWriter.print(size);
                    indentingPrintWriter.println(", sorted list of files:");
                    indentingPrintWriter.increaseIndent();
                    for (int i2 = 0; i2 < size; i2++) {
                        long keyAt = timeSparseArray.keyAt(i2);
                        if (z) {
                            indentingPrintWriter.print(UserUsageStatsService.formatDateTime(keyAt, false));
                        } else {
                            indentingPrintWriter.printPair(Long.toString(keyAt), UserUsageStatsService.formatDateTime(keyAt, true));
                        }
                        indentingPrintWriter.println();
                    }
                    indentingPrintWriter.decreaseIndent();
                    i++;
                } else {
                    indentingPrintWriter.decreaseIndent();
                }
            }
        }
    }

    public void dumpMappings(IndentingPrintWriter indentingPrintWriter) {
        synchronized (this.mLock) {
            indentingPrintWriter.println("Obfuscated Packages Mappings:");
            indentingPrintWriter.increaseIndent();
            indentingPrintWriter.println("Counter: " + this.mPackagesTokenData.counter);
            indentingPrintWriter.println("Tokens Map Size: " + this.mPackagesTokenData.tokensToPackagesMap.size());
            if (!this.mPackagesTokenData.removedPackageTokens.isEmpty()) {
                indentingPrintWriter.println("Removed Package Tokens: " + Arrays.toString(this.mPackagesTokenData.removedPackageTokens.toArray()));
            }
            for (int i = 0; i < this.mPackagesTokenData.tokensToPackagesMap.size(); i++) {
                int keyAt = this.mPackagesTokenData.tokensToPackagesMap.keyAt(i);
                String join = String.join(", ", this.mPackagesTokenData.tokensToPackagesMap.valueAt(i));
                indentingPrintWriter.println("Token " + keyAt + ": [" + join + "]");
            }
            indentingPrintWriter.println();
            indentingPrintWriter.decreaseIndent();
        }
    }

    public IntervalStats readIntervalStatsForFile(int i, long j) {
        IntervalStats intervalStats;
        synchronized (this.mLock) {
            intervalStats = new IntervalStats();
            try {
                readLocked((AtomicFile) this.mSortedStatFiles[i].get(j, (Object) null), intervalStats);
            } catch (Exception unused) {
                return null;
            }
        }
        return intervalStats;
    }
}
