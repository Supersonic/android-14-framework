package com.android.server.power.stats;

import android.content.Context;
import android.os.BatteryUsageStats;
import android.os.BatteryUsageStatsQuery;
import android.os.Handler;
import android.util.AtomicFile;
import android.util.Log;
import android.util.LongArray;
import android.util.Slog;
import android.util.Xml;
import com.android.internal.annotations.VisibleForTesting;
import com.android.modules.utils.TypedXmlPullParser;
import com.android.modules.utils.TypedXmlSerializer;
import com.android.server.power.stats.BatteryStatsImpl;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.charset.StandardCharsets;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import java.util.concurrent.locks.ReentrantLock;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes2.dex */
public class BatteryUsageStatsStore {
    public static final List<BatteryUsageStatsQuery> BATTERY_USAGE_STATS_QUERY = List.of(new BatteryUsageStatsQuery.Builder().setMaxStatsAgeMs(0).includePowerModels().includeProcessStateData().build());
    public final BatteryStatsImpl mBatteryStats;
    public final BatteryUsageStatsProvider mBatteryUsageStatsProvider;
    public final AtomicFile mConfigFile;
    public final Context mContext;
    public final ReentrantLock mFileLock;
    public final Handler mHandler;
    public FileLock mJvmLock;
    public final File mLockFile;
    public final long mMaxStorageBytes;
    public final File mStoreDir;
    public boolean mSystemReady;

    public BatteryUsageStatsStore(Context context, BatteryStatsImpl batteryStatsImpl, File file, Handler handler) {
        this(context, batteryStatsImpl, file, handler, 102400L);
    }

    @VisibleForTesting
    public BatteryUsageStatsStore(Context context, BatteryStatsImpl batteryStatsImpl, File file, Handler handler, long j) {
        this.mFileLock = new ReentrantLock();
        this.mContext = context;
        this.mBatteryStats = batteryStatsImpl;
        File file2 = new File(file, "battery-usage-stats");
        this.mStoreDir = file2;
        this.mLockFile = new File(file2, ".lock");
        this.mConfigFile = new AtomicFile(new File(file2, "config"));
        this.mHandler = handler;
        this.mMaxStorageBytes = j;
        batteryStatsImpl.setBatteryResetListener(new BatteryStatsImpl.BatteryResetListener() { // from class: com.android.server.power.stats.BatteryUsageStatsStore$$ExternalSyntheticLambda0
            @Override // com.android.server.power.stats.BatteryStatsImpl.BatteryResetListener
            public final void prepareForBatteryStatsReset(int i) {
                BatteryUsageStatsStore.this.prepareForBatteryStatsReset(i);
            }
        });
        this.mBatteryUsageStatsProvider = new BatteryUsageStatsProvider(context, batteryStatsImpl);
    }

    public void onSystemReady() {
        this.mSystemReady = true;
    }

    public final void prepareForBatteryStatsReset(int i) {
        if (i == 1 || !this.mSystemReady) {
            return;
        }
        final List<BatteryUsageStats> batteryUsageStats = this.mBatteryUsageStatsProvider.getBatteryUsageStats(BATTERY_USAGE_STATS_QUERY);
        if (batteryUsageStats.isEmpty()) {
            Slog.wtf("BatteryUsageStatsStore", "No battery usage stats generated");
        } else {
            this.mHandler.post(new Runnable() { // from class: com.android.server.power.stats.BatteryUsageStatsStore$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    BatteryUsageStatsStore.this.lambda$prepareForBatteryStatsReset$0(batteryUsageStats);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$prepareForBatteryStatsReset$0(List list) {
        storeBatteryUsageStats((BatteryUsageStats) list.get(0));
    }

    public final void storeBatteryUsageStats(BatteryUsageStats batteryUsageStats) {
        lockSnapshotDirectory();
        try {
            if (!this.mStoreDir.exists() && !this.mStoreDir.mkdirs()) {
                Slog.e("BatteryUsageStatsStore", "Could not create a directory for battery usage stats snapshots");
                return;
            }
            try {
                writeXmlFileLocked(batteryUsageStats, makeSnapshotFilename(batteryUsageStats.getStatsEndTimestamp()));
            } catch (Exception e) {
                Slog.e("BatteryUsageStatsStore", "Cannot save battery usage stats", e);
            }
            removeOldSnapshotsLocked();
        } finally {
            unlockSnapshotDirectory();
        }
    }

    public long[] listBatteryUsageStatsTimestamps() {
        LongArray longArray = new LongArray(100);
        lockSnapshotDirectory();
        try {
            for (File file : this.mStoreDir.listFiles()) {
                String name = file.getName();
                if (name.endsWith(".bus")) {
                    try {
                        longArray.add(Long.parseLong(name.substring(0, name.length() - 4)));
                    } catch (NumberFormatException unused) {
                        Slog.wtf("BatteryUsageStatsStore", "Invalid format of BatteryUsageStats snapshot file name: " + name);
                    }
                }
            }
            unlockSnapshotDirectory();
            return longArray.toArray();
        } catch (Throwable th) {
            unlockSnapshotDirectory();
            throw th;
        }
    }

    public BatteryUsageStats loadBatteryUsageStats(long j) {
        lockSnapshotDirectory();
        try {
            try {
                return readXmlFileLocked(makeSnapshotFilename(j));
            } catch (Exception e) {
                Slog.e("BatteryUsageStatsStore", "Cannot read battery usage stats", e);
                unlockSnapshotDirectory();
                return null;
            }
        } finally {
            unlockSnapshotDirectory();
        }
    }

    public void setLastBatteryUsageStatsBeforeResetAtomPullTimestamp(long j) {
        FileInputStream openRead;
        Properties properties = new Properties();
        lockSnapshotDirectory();
        try {
            try {
                openRead = this.mConfigFile.openRead();
            } catch (IOException e) {
                Slog.e("BatteryUsageStatsStore", "Cannot load config file " + this.mConfigFile, e);
            }
            try {
                properties.load(openRead);
                if (openRead != null) {
                    openRead.close();
                }
                properties.put("BATTERY_USAGE_STATS_BEFORE_RESET_TIMESTAMP", String.valueOf(j));
                FileOutputStream fileOutputStream = null;
                try {
                    fileOutputStream = this.mConfigFile.startWrite();
                    properties.store(fileOutputStream, "Statsd atom pull timestamps");
                    this.mConfigFile.finishWrite(fileOutputStream);
                } catch (IOException e2) {
                    this.mConfigFile.failWrite(fileOutputStream);
                    Slog.e("BatteryUsageStatsStore", "Cannot save config file " + this.mConfigFile, e2);
                }
            } catch (Throwable th) {
                if (openRead != null) {
                    try {
                        openRead.close();
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                    }
                }
                throw th;
            }
        } finally {
            unlockSnapshotDirectory();
        }
    }

    public long getLastBatteryUsageStatsBeforeResetAtomPullTimestamp() {
        FileInputStream openRead;
        Properties properties = new Properties();
        lockSnapshotDirectory();
        try {
            try {
                openRead = this.mConfigFile.openRead();
            } catch (IOException e) {
                Slog.e("BatteryUsageStatsStore", "Cannot load config file " + this.mConfigFile, e);
            }
            try {
                properties.load(openRead);
                if (openRead != null) {
                    openRead.close();
                }
                unlockSnapshotDirectory();
                return Long.parseLong(properties.getProperty("BATTERY_USAGE_STATS_BEFORE_RESET_TIMESTAMP", "0"));
            } catch (Throwable th) {
                if (openRead != null) {
                    try {
                        openRead.close();
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                    }
                }
                throw th;
            }
        } catch (Throwable th3) {
            unlockSnapshotDirectory();
            throw th3;
        }
    }

    public final void lockSnapshotDirectory() {
        this.mFileLock.lock();
        try {
            this.mLockFile.getParentFile().mkdirs();
            this.mLockFile.createNewFile();
            this.mJvmLock = FileChannel.open(this.mLockFile.toPath(), StandardOpenOption.WRITE).lock();
        } catch (IOException e) {
            Log.e("BatteryUsageStatsStore", "Cannot lock snapshot directory", e);
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r3v3, types: [java.util.concurrent.locks.ReentrantLock] */
    public final void unlockSnapshotDirectory() {
        try {
            try {
                this.mJvmLock.close();
            } catch (IOException e) {
                Log.e("BatteryUsageStatsStore", "Cannot unlock snapshot directory", e);
            }
        } finally {
            this.mFileLock.unlock();
        }
    }

    public final File makeSnapshotFilename(long j) {
        File file = this.mStoreDir;
        return new File(file, String.format(Locale.ENGLISH, "%019d", Long.valueOf(j)) + ".bus");
    }

    public final void writeXmlFileLocked(BatteryUsageStats batteryUsageStats, File file) throws IOException {
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        try {
            TypedXmlSerializer newBinarySerializer = Xml.newBinarySerializer();
            newBinarySerializer.setOutput(fileOutputStream, StandardCharsets.UTF_8.name());
            newBinarySerializer.startDocument((String) null, Boolean.TRUE);
            batteryUsageStats.writeXml(newBinarySerializer);
            newBinarySerializer.endDocument();
            fileOutputStream.close();
        } catch (Throwable th) {
            try {
                fileOutputStream.close();
            } catch (Throwable th2) {
                th.addSuppressed(th2);
            }
            throw th;
        }
    }

    public final BatteryUsageStats readXmlFileLocked(File file) throws IOException, XmlPullParserException {
        FileInputStream fileInputStream = new FileInputStream(file);
        try {
            TypedXmlPullParser newBinaryPullParser = Xml.newBinaryPullParser();
            newBinaryPullParser.setInput(fileInputStream, StandardCharsets.UTF_8.name());
            BatteryUsageStats createFromXml = BatteryUsageStats.createFromXml(newBinaryPullParser);
            fileInputStream.close();
            return createFromXml;
        } catch (Throwable th) {
            try {
                fileInputStream.close();
            } catch (Throwable th2) {
                th.addSuppressed(th2);
            }
            throw th;
        }
    }

    public final void removeOldSnapshotsLocked() {
        File[] listFiles;
        Map.Entry firstEntry;
        TreeMap treeMap = new TreeMap();
        long j = 0;
        for (File file : this.mStoreDir.listFiles()) {
            long length = file.length();
            j += length;
            if (file.getName().endsWith(".bus")) {
                treeMap.put(file, Long.valueOf(length));
            }
        }
        while (j > this.mMaxStorageBytes && (firstEntry = treeMap.firstEntry()) != null) {
            File file2 = (File) firstEntry.getKey();
            if (!file2.delete()) {
                Slog.e("BatteryUsageStatsStore", "Cannot delete battery usage stats " + file2);
            }
            j -= ((Long) firstEntry.getValue()).longValue();
            treeMap.remove(file2);
        }
    }

    public void removeAllSnapshots() {
        File[] listFiles;
        lockSnapshotDirectory();
        try {
            for (File file : this.mStoreDir.listFiles()) {
                if (file.getName().endsWith(".bus") && !file.delete()) {
                    Slog.e("BatteryUsageStatsStore", "Cannot delete battery usage stats " + file);
                }
            }
        } finally {
            unlockSnapshotDirectory();
        }
    }
}
