package com.android.server.storage;

import android.content.pm.PackageStats;
import android.os.Environment;
import android.util.ArrayMap;
import android.util.Log;
import com.android.server.storage.FileCollector;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: classes2.dex */
public class DiskStatsFileLogger {
    public long mDownloadsSize;
    public List<PackageStats> mPackageStats;
    public FileCollector.MeasurementResult mResult;
    public long mSystemSize;

    public DiskStatsFileLogger(FileCollector.MeasurementResult measurementResult, FileCollector.MeasurementResult measurementResult2, List<PackageStats> list, long j) {
        this.mResult = measurementResult;
        this.mDownloadsSize = measurementResult2.totalAccountedSize();
        this.mSystemSize = j;
        this.mPackageStats = list;
    }

    public void dumpToFile(File file) throws FileNotFoundException {
        PrintWriter printWriter = new PrintWriter(file);
        JSONObject jsonRepresentation = getJsonRepresentation();
        if (jsonRepresentation != null) {
            printWriter.println(jsonRepresentation);
        }
        printWriter.close();
    }

    public final JSONObject getJsonRepresentation() {
        JSONObject jSONObject = new JSONObject();
        try {
            jSONObject.put("queryTime", System.currentTimeMillis());
            jSONObject.put("photosSize", this.mResult.imagesSize);
            jSONObject.put("videosSize", this.mResult.videosSize);
            jSONObject.put("audioSize", this.mResult.audioSize);
            jSONObject.put("downloadsSize", this.mDownloadsSize);
            jSONObject.put("systemSize", this.mSystemSize);
            jSONObject.put("otherSize", this.mResult.miscSize);
            addAppsToJson(jSONObject);
            return jSONObject;
        } catch (JSONException e) {
            Log.e("DiskStatsLogger", e.toString());
            return null;
        }
    }

    public final void addAppsToJson(JSONObject jSONObject) throws JSONException {
        Object jSONArray = new JSONArray();
        JSONArray jSONArray2 = new JSONArray();
        Object jSONArray3 = new JSONArray();
        Object jSONArray4 = new JSONArray();
        boolean isExternalStorageEmulated = Environment.isExternalStorageEmulated();
        Iterator<Map.Entry<String, PackageStats>> it = filterOnlyPrimaryUser().entrySet().iterator();
        long j = 0;
        long j2 = 0;
        long j3 = 0;
        while (it.hasNext()) {
            PackageStats value = it.next().getValue();
            long j4 = value.codeSize;
            JSONArray jSONArray5 = jSONArray3;
            JSONArray jSONArray6 = jSONArray4;
            long j5 = value.dataSize;
            JSONArray jSONArray7 = jSONArray;
            long j6 = value.cacheSize;
            boolean z = isExternalStorageEmulated;
            Iterator<Map.Entry<String, PackageStats>> it2 = it;
            if (isExternalStorageEmulated) {
                j4 += value.externalCodeSize;
                j5 += value.externalDataSize;
                j6 += value.externalCacheSize;
            }
            j += j4;
            j3 += j5;
            j2 += j6;
            jSONArray7.put(value.packageName);
            jSONArray2.put(j4);
            jSONArray5.put(j5);
            jSONArray6.put(j6);
            jSONArray4 = jSONArray6;
            jSONArray3 = jSONArray5;
            jSONArray = jSONArray7;
            isExternalStorageEmulated = z;
            it = it2;
        }
        jSONObject.put("packageNames", jSONArray);
        jSONObject.put("appSizes", jSONArray2);
        jSONObject.put("cacheSizes", jSONArray4);
        jSONObject.put("appDataSizes", jSONArray3);
        jSONObject.put("appSize", j);
        jSONObject.put("cacheSize", j2);
        jSONObject.put("appDataSize", j3);
    }

    public final ArrayMap<String, PackageStats> filterOnlyPrimaryUser() {
        ArrayMap<String, PackageStats> arrayMap = new ArrayMap<>();
        for (PackageStats packageStats : this.mPackageStats) {
            if (packageStats.userHandle == 0) {
                PackageStats packageStats2 = arrayMap.get(packageStats.packageName);
                if (packageStats2 != null) {
                    packageStats2.cacheSize += packageStats.cacheSize;
                    packageStats2.codeSize += packageStats.codeSize;
                    packageStats2.dataSize += packageStats.dataSize;
                    packageStats2.externalCacheSize += packageStats.externalCacheSize;
                    packageStats2.externalCodeSize += packageStats.externalCodeSize;
                    packageStats2.externalDataSize += packageStats.externalDataSize;
                } else {
                    arrayMap.put(packageStats.packageName, new PackageStats(packageStats));
                }
            }
        }
        return arrayMap;
    }
}
