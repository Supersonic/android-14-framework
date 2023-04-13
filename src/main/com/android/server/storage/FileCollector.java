package com.android.server.storage;

import android.app.usage.ExternalStorageStats;
import android.app.usage.StorageStatsManager;
import android.content.Context;
import android.os.UserHandle;
import android.os.storage.StorageManager;
import android.os.storage.VolumeInfo;
import android.util.ArrayMap;
import java.io.File;
import java.io.IOException;
import java.util.Map;
/* loaded from: classes2.dex */
public class FileCollector {
    public static final Map<String, Integer> EXTENSION_MAP;

    static {
        ArrayMap arrayMap = new ArrayMap();
        EXTENSION_MAP = arrayMap;
        arrayMap.put("aac", 2);
        arrayMap.put("amr", 2);
        arrayMap.put("awb", 2);
        arrayMap.put("snd", 2);
        arrayMap.put("flac", 2);
        arrayMap.put("mp3", 2);
        arrayMap.put("mpga", 2);
        arrayMap.put("mpega", 2);
        arrayMap.put("mp2", 2);
        arrayMap.put("m4a", 2);
        arrayMap.put("aif", 2);
        arrayMap.put("aiff", 2);
        arrayMap.put("aifc", 2);
        arrayMap.put("gsm", 2);
        arrayMap.put("mka", 2);
        arrayMap.put("m3u", 2);
        arrayMap.put("wma", 2);
        arrayMap.put("wax", 2);
        arrayMap.put("ra", 2);
        arrayMap.put("rm", 2);
        arrayMap.put("ram", 2);
        arrayMap.put("pls", 2);
        arrayMap.put("sd2", 2);
        arrayMap.put("wav", 2);
        arrayMap.put("ogg", 2);
        arrayMap.put("oga", 2);
        arrayMap.put("3gpp", 1);
        arrayMap.put("3gp", 1);
        arrayMap.put("3gpp2", 1);
        arrayMap.put("3g2", 1);
        arrayMap.put("avi", 1);
        arrayMap.put("dl", 1);
        arrayMap.put("dif", 1);
        arrayMap.put("dv", 1);
        arrayMap.put("fli", 1);
        arrayMap.put("m4v", 1);
        arrayMap.put("ts", 1);
        arrayMap.put("mpeg", 1);
        arrayMap.put("mpg", 1);
        arrayMap.put("mpe", 1);
        arrayMap.put("mp4", 1);
        arrayMap.put("vob", 1);
        arrayMap.put("qt", 1);
        arrayMap.put("mov", 1);
        arrayMap.put("mxu", 1);
        arrayMap.put("webm", 1);
        arrayMap.put("lsf", 1);
        arrayMap.put("lsx", 1);
        arrayMap.put("mkv", 1);
        arrayMap.put("mng", 1);
        arrayMap.put("asf", 1);
        arrayMap.put("asx", 1);
        arrayMap.put("wm", 1);
        arrayMap.put("wmv", 1);
        arrayMap.put("wmx", 1);
        arrayMap.put("wvx", 1);
        arrayMap.put("movie", 1);
        arrayMap.put("wrf", 1);
        arrayMap.put("bmp", 0);
        arrayMap.put("gif", 0);
        arrayMap.put("jpg", 0);
        arrayMap.put("jpeg", 0);
        arrayMap.put("jpe", 0);
        arrayMap.put("pcx", 0);
        arrayMap.put("png", 0);
        arrayMap.put("svg", 0);
        arrayMap.put("svgz", 0);
        arrayMap.put("tiff", 0);
        arrayMap.put("tif", 0);
        arrayMap.put("wbmp", 0);
        arrayMap.put("webp", 0);
        arrayMap.put("dng", 0);
        arrayMap.put("cr2", 0);
        arrayMap.put("ras", 0);
        arrayMap.put("art", 0);
        arrayMap.put("jng", 0);
        arrayMap.put("nef", 0);
        arrayMap.put("nrw", 0);
        arrayMap.put("orf", 0);
        arrayMap.put("rw2", 0);
        arrayMap.put("pef", 0);
        arrayMap.put("psd", 0);
        arrayMap.put("pnm", 0);
        arrayMap.put("pbm", 0);
        arrayMap.put("pgm", 0);
        arrayMap.put("ppm", 0);
        arrayMap.put("srw", 0);
        arrayMap.put("arw", 0);
        arrayMap.put("rgb", 0);
        arrayMap.put("xbm", 0);
        arrayMap.put("xpm", 0);
        arrayMap.put("xwd", 0);
    }

    public static MeasurementResult getMeasurementResult(File file) {
        return collectFiles(StorageManager.maybeTranslateEmulatedPathToInternal(file), new MeasurementResult());
    }

    public static MeasurementResult getMeasurementResult(Context context) {
        MeasurementResult measurementResult = new MeasurementResult();
        try {
            ExternalStorageStats queryExternalStatsForUser = ((StorageStatsManager) context.getSystemService("storagestats")).queryExternalStatsForUser(StorageManager.UUID_PRIVATE_INTERNAL, UserHandle.of(context.getUserId()));
            measurementResult.imagesSize = queryExternalStatsForUser.getImageBytes();
            measurementResult.videosSize = queryExternalStatsForUser.getVideoBytes();
            measurementResult.audioSize = queryExternalStatsForUser.getAudioBytes();
            measurementResult.miscSize = ((queryExternalStatsForUser.getTotalBytes() - measurementResult.imagesSize) - measurementResult.videosSize) - measurementResult.audioSize;
            return measurementResult;
        } catch (IOException unused) {
            throw new IllegalStateException("Could not query storage");
        }
    }

    public static long getSystemSize(Context context) {
        File path;
        VolumeInfo primaryStorageCurrentVolume = context.getPackageManager().getPrimaryStorageCurrentVolume();
        StorageManager storageManager = (StorageManager) context.getSystemService("storage");
        VolumeInfo findEmulatedForPrivate = storageManager.findEmulatedForPrivate(primaryStorageCurrentVolume);
        if (findEmulatedForPrivate == null || (path = findEmulatedForPrivate.getPath()) == null) {
            return 0L;
        }
        long primaryStorageSize = storageManager.getPrimaryStorageSize() - path.getTotalSpace();
        if (primaryStorageSize <= 0) {
            return 0L;
        }
        return primaryStorageSize;
    }

    public static MeasurementResult collectFiles(File file, MeasurementResult measurementResult) {
        File[] listFiles = file.listFiles();
        if (listFiles == null) {
            return measurementResult;
        }
        for (File file2 : listFiles) {
            if (file2.isDirectory()) {
                try {
                    collectFiles(file2, measurementResult);
                } catch (StackOverflowError unused) {
                    return measurementResult;
                }
            } else {
                handleFile(measurementResult, file2);
            }
        }
        return measurementResult;
    }

    public static void handleFile(MeasurementResult measurementResult, File file) {
        long length = file.length();
        int intValue = EXTENSION_MAP.getOrDefault(getExtensionForFile(file), -1).intValue();
        if (intValue == 0) {
            measurementResult.imagesSize += length;
        } else if (intValue == 1) {
            measurementResult.videosSize += length;
        } else if (intValue == 2) {
            measurementResult.audioSize += length;
        } else {
            measurementResult.miscSize += length;
        }
    }

    public static String getExtensionForFile(File file) {
        String name = file.getName();
        int lastIndexOf = name.lastIndexOf(46);
        return lastIndexOf == -1 ? "" : name.substring(lastIndexOf + 1).toLowerCase();
    }

    /* loaded from: classes2.dex */
    public static class MeasurementResult {
        public long audioSize;
        public long imagesSize;
        public long miscSize;
        public long videosSize;

        public long totalAccountedSize() {
            return this.imagesSize + this.videosSize + this.miscSize + this.audioSize;
        }
    }
}
