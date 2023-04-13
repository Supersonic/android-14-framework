package com.android.server.p011pm.dex;

import android.os.SystemClock;
import android.util.Slog;
import android.util.jar.StrictJarFile;
import com.android.internal.art.ArtStatsLog;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
/* renamed from: com.android.server.pm.dex.ArtStatsLogUtils */
/* loaded from: classes2.dex */
public class ArtStatsLogUtils {
    public static final Map<Integer, Integer> COMPILATION_REASON_MAP;
    public static final Map<String, Integer> COMPILE_FILTER_MAP;
    public static final Map<String, Integer> ISA_MAP;
    public static final Map<Integer, Integer> STATUS_MAP;
    public static final String TAG = "ArtStatsLogUtils";

    static {
        HashMap hashMap = new HashMap();
        COMPILATION_REASON_MAP = hashMap;
        hashMap.put(0, 3);
        hashMap.put(1, 17);
        hashMap.put(2, 11);
        hashMap.put(3, 5);
        hashMap.put(4, 12);
        hashMap.put(5, 13);
        hashMap.put(6, 14);
        hashMap.put(7, 15);
        hashMap.put(8, 16);
        hashMap.put(9, 6);
        hashMap.put(10, 7);
        hashMap.put(11, 8);
        hashMap.put(12, 19);
        hashMap.put(14, 9);
        HashMap hashMap2 = new HashMap();
        COMPILE_FILTER_MAP = hashMap2;
        hashMap2.put("error", 1);
        hashMap2.put("unknown", 2);
        hashMap2.put("assume-verified", 3);
        hashMap2.put("extract", 4);
        hashMap2.put("verify", 5);
        hashMap2.put("quicken", 6);
        hashMap2.put("space-profile", 7);
        hashMap2.put("space", 8);
        hashMap2.put("speed-profile", 9);
        hashMap2.put("speed", 10);
        hashMap2.put("everything-profile", 11);
        hashMap2.put("everything", 12);
        hashMap2.put("run-from-apk", 13);
        hashMap2.put("run-from-apk-fallback", 14);
        hashMap2.put("run-from-vdex-fallback", 15);
        HashMap hashMap3 = new HashMap();
        ISA_MAP = hashMap3;
        hashMap3.put("arm", 1);
        hashMap3.put("arm64", 2);
        hashMap3.put("x86", 3);
        hashMap3.put("x86_64", 4);
        hashMap3.put("mips", 5);
        hashMap3.put("mips64", 6);
        STATUS_MAP = Map.of(-1, 0, 0, 1, 1, 2, 2, 3, 3, 4, 4, 5, 5, 1, 6, 7);
    }

    public static void writeStatsLog(ArtStatsLogger artStatsLogger, long j, String str, int i, long j2, String str2, int i2, int i3, int i4, String str3, String str4) {
        int dexMetadataType = getDexMetadataType(str2);
        artStatsLogger.write(j, i, i2, str, 10, i3, dexMetadataType, i4, str3);
        artStatsLogger.write(j, i, i2, str, 11, getDexBytes(str4), dexMetadataType, i4, str3);
        artStatsLogger.write(j, i, i2, str, 12, j2, dexMetadataType, i4, str3);
    }

    public static int getApkType(final String str, String str2, String[] strArr) {
        if (str.equals(str2)) {
            return 1;
        }
        return Arrays.stream(strArr).anyMatch(new Predicate() { // from class: com.android.server.pm.dex.ArtStatsLogUtils$$ExternalSyntheticLambda0
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean equals;
                equals = ((String) obj).equals(str);
                return equals;
            }
        }) ? 2 : 0;
    }

    public static long getDexBytes(String str) {
        StrictJarFile strictJarFile;
        StrictJarFile strictJarFile2 = null;
        try {
            try {
                strictJarFile = new StrictJarFile(str, false, false);
            } catch (Throwable th) {
                th = th;
            }
        } catch (IOException unused) {
        }
        try {
            Iterator it = strictJarFile.iterator();
            Matcher matcher = Pattern.compile("classes(\\d)*[.]dex").matcher("");
            long j = 0;
            while (it.hasNext()) {
                ZipEntry zipEntry = (ZipEntry) it.next();
                matcher.reset(zipEntry.getName());
                if (matcher.matches()) {
                    j += zipEntry.getSize();
                }
            }
            try {
                strictJarFile.close();
            } catch (IOException unused2) {
            }
            return j;
        } catch (IOException unused3) {
            strictJarFile2 = strictJarFile;
            String str2 = TAG;
            Slog.e(str2, "Error when parsing APK " + str);
            if (strictJarFile2 != null) {
                try {
                    strictJarFile2.close();
                    return -1L;
                } catch (IOException unused4) {
                    return -1L;
                }
            }
            return -1L;
        } catch (Throwable th2) {
            th = th2;
            strictJarFile2 = strictJarFile;
            if (strictJarFile2 != null) {
                try {
                    strictJarFile2.close();
                } catch (IOException unused5) {
                }
            }
            throw th;
        }
    }

    public static int getDexMetadataType(String str) {
        StrictJarFile strictJarFile;
        if (str == null) {
            return 4;
        }
        StrictJarFile strictJarFile2 = null;
        try {
            try {
                strictJarFile = new StrictJarFile(str, false, false);
            } catch (Throwable th) {
                th = th;
            }
        } catch (IOException unused) {
        }
        try {
            boolean findFileName = findFileName(strictJarFile, "primary.prof");
            boolean findFileName2 = findFileName(strictJarFile, "primary.vdex");
            if (findFileName && findFileName2) {
                try {
                    strictJarFile.close();
                    return 3;
                } catch (IOException unused2) {
                    return 3;
                }
            } else if (findFileName) {
                try {
                    strictJarFile.close();
                    return 1;
                } catch (IOException unused3) {
                    return 1;
                }
            } else if (!findFileName2) {
                try {
                    strictJarFile.close();
                } catch (IOException unused4) {
                }
                return 0;
            } else {
                try {
                    strictJarFile.close();
                    return 2;
                } catch (IOException unused5) {
                    return 2;
                }
            }
        } catch (IOException unused6) {
            strictJarFile2 = strictJarFile;
            String str2 = TAG;
            Slog.e(str2, "Error when parsing dex metadata " + str);
            if (strictJarFile2 != null) {
                try {
                    strictJarFile2.close();
                    return 5;
                } catch (IOException unused7) {
                    return 5;
                }
            }
            return 5;
        } catch (Throwable th2) {
            th = th2;
            strictJarFile2 = strictJarFile;
            if (strictJarFile2 != null) {
                try {
                    strictJarFile2.close();
                } catch (IOException unused8) {
                }
            }
            throw th;
        }
    }

    public static boolean findFileName(StrictJarFile strictJarFile, String str) throws IOException {
        Iterator it = strictJarFile.iterator();
        while (it.hasNext()) {
            if (((ZipEntry) it.next()).getName().equals(str)) {
                return true;
            }
        }
        return false;
    }

    /* renamed from: com.android.server.pm.dex.ArtStatsLogUtils$ArtStatsLogger */
    /* loaded from: classes2.dex */
    public static class ArtStatsLogger {
        public void write(long j, int i, int i2, String str, int i3, long j2, int i4, int i5, String str2) {
            ArtStatsLog.write(332, j, i, ((Integer) ArtStatsLogUtils.COMPILE_FILTER_MAP.getOrDefault(str, 2)).intValue(), ((Integer) ArtStatsLogUtils.COMPILATION_REASON_MAP.getOrDefault(Integer.valueOf(i2), 2)).intValue(), SystemClock.uptimeMillis(), 1, i3, j2, i4, i5, ((Integer) ArtStatsLogUtils.ISA_MAP.getOrDefault(str2, 0)).intValue(), 0, 0);
        }
    }

    /* renamed from: com.android.server.pm.dex.ArtStatsLogUtils$BackgroundDexoptJobStatsLogger */
    /* loaded from: classes2.dex */
    public static class BackgroundDexoptJobStatsLogger {
        public void write(int i, int i2, long j) {
            ArtStatsLog.write(467, ((Integer) ArtStatsLogUtils.STATUS_MAP.getOrDefault(Integer.valueOf(i), 0)).intValue(), i2, j, 0L);
        }
    }
}
