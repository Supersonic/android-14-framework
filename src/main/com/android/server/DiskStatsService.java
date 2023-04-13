package com.android.server;

import android.content.Context;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.StatFs;
import android.os.SystemClock;
import android.os.storage.StorageManager;
import android.p005os.IStoraged;
import android.util.Log;
import android.util.Slog;
import android.util.proto.ProtoOutputStream;
import com.android.internal.util.DumpUtils;
import com.android.server.storage.DiskStatsLoggingService;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import libcore.io.IoUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: classes.dex */
public class DiskStatsService extends Binder {
    public final Context mContext;

    public DiskStatsService(Context context) {
        this.mContext = context;
        DiskStatsLoggingService.schedule(context);
    }

    /* JADX WARN: Removed duplicated region for block: B:27:0x0057  */
    /* JADX WARN: Removed duplicated region for block: B:30:0x0065  */
    /* JADX WARN: Removed duplicated region for block: B:38:0x0091  */
    /* JADX WARN: Removed duplicated region for block: B:43:0x00b3  */
    /* JADX WARN: Removed duplicated region for block: B:44:0x00b7  */
    /* JADX WARN: Removed duplicated region for block: B:47:0x00ef  */
    /* JADX WARN: Removed duplicated region for block: B:51:0x00ff  */
    /* JADX WARN: Removed duplicated region for block: B:54:0x0108  */
    /* JADX WARN: Removed duplicated region for block: B:55:0x010c  */
    /* JADX WARN: Removed duplicated region for block: B:57:0x0111  */
    /* JADX WARN: Removed duplicated region for block: B:73:? A[RETURN, SYNTHETIC] */
    @Override // android.os.Binder
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        FileOutputStream fileOutputStream;
        boolean hasOption;
        ProtoOutputStream protoOutputStream;
        if (DumpUtils.checkDumpAndUsageStatsPermission(this.mContext, "DiskStatsService", printWriter)) {
            byte[] bArr = new byte[512];
            for (int i = 0; i < 512; i++) {
                bArr[i] = (byte) i;
            }
            File file = new File(Environment.getDataDirectory(), "system/perftest.tmp");
            long uptimeMillis = SystemClock.uptimeMillis();
            FileOutputStream fileOutputStream2 = null;
            PrintWriter printWriter2 = null;
            try {
                fileOutputStream = new FileOutputStream(file);
            } catch (IOException e) {
                e = e;
                fileOutputStream = null;
            } catch (Throwable th) {
                th = th;
            }
            try {
                fileOutputStream.write(bArr);
                try {
                    fileOutputStream.close();
                } catch (IOException unused) {
                }
                e = null;
            } catch (IOException e2) {
                e = e2;
                if (fileOutputStream != null) {
                    try {
                        fileOutputStream.close();
                    } catch (IOException unused2) {
                    }
                }
                long uptimeMillis2 = SystemClock.uptimeMillis();
                if (file.exists()) {
                }
                hasOption = hasOption(strArr, "--proto");
                if (hasOption) {
                }
                if (!hasOption) {
                }
                PrintWriter printWriter3 = printWriter2;
                ProtoOutputStream protoOutputStream2 = protoOutputStream;
                reportFreeSpace(Environment.getDataDirectory(), "Data", printWriter3, protoOutputStream2, 0);
                reportFreeSpace(Environment.getDownloadCacheDirectory(), "Cache", printWriter3, protoOutputStream2, 1);
                reportFreeSpace(new File("/system"), "System", printWriter3, protoOutputStream2, 2);
                reportFreeSpace(Environment.getMetadataDirectory(), "Metadata", printWriter3, protoOutputStream2, 3);
                boolean isFileEncrypted = StorageManager.isFileEncrypted();
                if (hasOption) {
                }
                if (!hasOption) {
                }
                if (hasOption) {
                }
            } catch (Throwable th2) {
                th = th2;
                fileOutputStream2 = fileOutputStream;
                if (fileOutputStream2 != null) {
                    try {
                        fileOutputStream2.close();
                    } catch (IOException unused3) {
                    }
                }
                throw th;
            }
            long uptimeMillis22 = SystemClock.uptimeMillis();
            if (file.exists()) {
                file.delete();
            }
            hasOption = hasOption(strArr, "--proto");
            if (hasOption) {
                if (e != null) {
                    printWriter.print("Test-Error: ");
                    printWriter.println(e.toString());
                } else {
                    printWriter.print("Latency: ");
                    printWriter.print(uptimeMillis22 - uptimeMillis);
                    printWriter.println("ms [512B Data Write]");
                }
                protoOutputStream = null;
                printWriter2 = printWriter;
            } else {
                ProtoOutputStream protoOutputStream3 = new ProtoOutputStream(fileDescriptor);
                protoOutputStream3.write(1133871366145L, e != null);
                if (e != null) {
                    protoOutputStream3.write(1138166333442L, e.toString());
                } else {
                    protoOutputStream3.write(1120986464259L, uptimeMillis22 - uptimeMillis);
                }
                protoOutputStream = protoOutputStream3;
            }
            if (!hasOption) {
                reportDiskWriteSpeedProto(protoOutputStream);
            } else {
                reportDiskWriteSpeed(printWriter2);
            }
            PrintWriter printWriter32 = printWriter2;
            ProtoOutputStream protoOutputStream22 = protoOutputStream;
            reportFreeSpace(Environment.getDataDirectory(), "Data", printWriter32, protoOutputStream22, 0);
            reportFreeSpace(Environment.getDownloadCacheDirectory(), "Cache", printWriter32, protoOutputStream22, 1);
            reportFreeSpace(new File("/system"), "System", printWriter32, protoOutputStream22, 2);
            reportFreeSpace(Environment.getMetadataDirectory(), "Metadata", printWriter32, protoOutputStream22, 3);
            boolean isFileEncrypted2 = StorageManager.isFileEncrypted();
            if (hasOption) {
                if (isFileEncrypted2) {
                    printWriter2.println("File-based Encryption: true");
                }
            } else if (isFileEncrypted2) {
                protoOutputStream.write(1159641169925L, 3);
            } else {
                protoOutputStream.write(1159641169925L, 1);
            }
            if (!hasOption) {
                reportCachedValuesProto(protoOutputStream);
            } else {
                reportCachedValues(printWriter2);
            }
            if (hasOption) {
                return;
            }
            protoOutputStream.flush();
        }
    }

    public final void reportFreeSpace(File file, String str, PrintWriter printWriter, ProtoOutputStream protoOutputStream, int i) {
        try {
            StatFs statFs = new StatFs(file.getPath());
            long blockSize = statFs.getBlockSize();
            long availableBlocks = statFs.getAvailableBlocks();
            long blockCount = statFs.getBlockCount();
            if (blockSize <= 0 || blockCount <= 0) {
                throw new IllegalArgumentException("Invalid stat: bsize=" + blockSize + " avail=" + availableBlocks + " total=" + blockCount);
            } else if (protoOutputStream != null) {
                long start = protoOutputStream.start(2246267895812L);
                protoOutputStream.write(1159641169921L, i);
                protoOutputStream.write(1112396529666L, (availableBlocks * blockSize) / 1024);
                protoOutputStream.write(1112396529667L, (blockCount * blockSize) / 1024);
                protoOutputStream.end(start);
            } else {
                printWriter.print(str);
                printWriter.print("-Free: ");
                printWriter.print((availableBlocks * blockSize) / 1024);
                printWriter.print("K / ");
                printWriter.print((blockSize * blockCount) / 1024);
                printWriter.print("K total = ");
                printWriter.print((availableBlocks * 100) / blockCount);
                printWriter.println("% free");
            }
        } catch (IllegalArgumentException e) {
            if (protoOutputStream != null) {
                return;
            }
            printWriter.print(str);
            printWriter.print("-Error: ");
            printWriter.println(e.toString());
        }
    }

    public final boolean hasOption(String[] strArr, String str) {
        for (String str2 : strArr) {
            if (str.equals(str2)) {
                return true;
            }
        }
        return false;
    }

    public final void reportCachedValues(PrintWriter printWriter) {
        try {
            JSONObject jSONObject = new JSONObject(IoUtils.readFileAsString("/data/system/diskstats_cache.json"));
            printWriter.print("App Size: ");
            printWriter.println(jSONObject.getLong("appSize"));
            printWriter.print("App Data Size: ");
            printWriter.println(jSONObject.getLong("appDataSize"));
            printWriter.print("App Cache Size: ");
            printWriter.println(jSONObject.getLong("cacheSize"));
            printWriter.print("Photos Size: ");
            printWriter.println(jSONObject.getLong("photosSize"));
            printWriter.print("Videos Size: ");
            printWriter.println(jSONObject.getLong("videosSize"));
            printWriter.print("Audio Size: ");
            printWriter.println(jSONObject.getLong("audioSize"));
            printWriter.print("Downloads Size: ");
            printWriter.println(jSONObject.getLong("downloadsSize"));
            printWriter.print("System Size: ");
            printWriter.println(jSONObject.getLong("systemSize"));
            printWriter.print("Other Size: ");
            printWriter.println(jSONObject.getLong("otherSize"));
            printWriter.print("Package Names: ");
            printWriter.println(jSONObject.getJSONArray("packageNames"));
            printWriter.print("App Sizes: ");
            printWriter.println(jSONObject.getJSONArray("appSizes"));
            printWriter.print("App Data Sizes: ");
            printWriter.println(jSONObject.getJSONArray("appDataSizes"));
            printWriter.print("Cache Sizes: ");
            printWriter.println(jSONObject.getJSONArray("cacheSizes"));
        } catch (IOException | JSONException e) {
            Log.w("DiskStatsService", "exception reading diskstats cache file", e);
        }
    }

    public final void reportCachedValuesProto(ProtoOutputStream protoOutputStream) {
        try {
            JSONObject jSONObject = new JSONObject(IoUtils.readFileAsString("/data/system/diskstats_cache.json"));
            long start = protoOutputStream.start(1146756268038L);
            protoOutputStream.write(1112396529665L, jSONObject.getLong("appSize"));
            protoOutputStream.write(1112396529674L, jSONObject.getLong("appDataSize"));
            protoOutputStream.write(1112396529666L, jSONObject.getLong("cacheSize"));
            protoOutputStream.write(1112396529667L, jSONObject.getLong("photosSize"));
            protoOutputStream.write(1112396529668L, jSONObject.getLong("videosSize"));
            protoOutputStream.write(1112396529669L, jSONObject.getLong("audioSize"));
            protoOutputStream.write(1112396529670L, jSONObject.getLong("downloadsSize"));
            protoOutputStream.write(1112396529671L, jSONObject.getLong("systemSize"));
            protoOutputStream.write(1112396529672L, jSONObject.getLong("otherSize"));
            JSONArray jSONArray = jSONObject.getJSONArray("packageNames");
            JSONArray jSONArray2 = jSONObject.getJSONArray("appSizes");
            JSONArray jSONArray3 = jSONObject.getJSONArray("appDataSizes");
            JSONArray jSONArray4 = jSONObject.getJSONArray("cacheSizes");
            int length = jSONArray.length();
            if (length == jSONArray2.length() && length == jSONArray3.length() && length == jSONArray4.length()) {
                for (int i = 0; i < length; i++) {
                    long start2 = protoOutputStream.start(2246267895817L);
                    protoOutputStream.write(1138166333441L, jSONArray.getString(i));
                    protoOutputStream.write(1112396529666L, jSONArray2.getLong(i));
                    protoOutputStream.write(1112396529668L, jSONArray3.getLong(i));
                    protoOutputStream.write(1112396529667L, jSONArray4.getLong(i));
                    protoOutputStream.end(start2);
                }
            } else {
                Slog.wtf("DiskStatsService", "Sizes of packageNamesArray, appSizesArray, appDataSizesArray  and cacheSizesArray are not the same");
            }
            protoOutputStream.end(start);
        } catch (IOException | JSONException e) {
            Log.w("DiskStatsService", "exception reading diskstats cache file", e);
        }
    }

    public final int getRecentPerf() throws RemoteException, IllegalStateException {
        IBinder service = ServiceManager.getService("storaged");
        if (service == null) {
            throw new IllegalStateException("storaged not found");
        }
        return IStoraged.Stub.asInterface(service).getRecentPerf();
    }

    public final void reportDiskWriteSpeed(PrintWriter printWriter) {
        try {
            long recentPerf = getRecentPerf();
            if (recentPerf != 0) {
                printWriter.print("Recent Disk Write Speed (kB/s) = ");
                printWriter.println(recentPerf);
            } else {
                printWriter.println("Recent Disk Write Speed data unavailable");
                Log.w("DiskStatsService", "Recent Disk Write Speed data unavailable!");
            }
        } catch (RemoteException | IllegalStateException e) {
            printWriter.println(e.toString());
            Log.e("DiskStatsService", e.toString());
        }
    }

    public final void reportDiskWriteSpeedProto(ProtoOutputStream protoOutputStream) {
        try {
            long recentPerf = getRecentPerf();
            if (recentPerf != 0) {
                protoOutputStream.write(1120986464263L, recentPerf);
            } else {
                Log.w("DiskStatsService", "Recent Disk Write Speed data unavailable!");
            }
        } catch (RemoteException | IllegalStateException e) {
            Log.e("DiskStatsService", e.toString());
        }
    }
}
