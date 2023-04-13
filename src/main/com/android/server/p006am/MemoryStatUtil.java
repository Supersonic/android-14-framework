package com.android.server.p006am;

import android.os.FileUtils;
import android.os.SystemProperties;
import android.system.Os;
import android.system.OsConstants;
import android.util.Slog;
import com.android.internal.annotations.VisibleForTesting;
import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/* renamed from: com.android.server.am.MemoryStatUtil */
/* loaded from: classes.dex */
public final class MemoryStatUtil {
    public static final int PAGE_SIZE = (int) Os.sysconf(OsConstants._SC_PAGESIZE);
    public static final boolean DEVICE_HAS_PER_APP_MEMCG = SystemProperties.getBoolean("ro.config.per_app_memcg", false);
    public static final Pattern PGFAULT = Pattern.compile("total_pgfault (\\d+)");
    public static final Pattern PGMAJFAULT = Pattern.compile("total_pgmajfault (\\d+)");
    public static final Pattern RSS_IN_BYTES = Pattern.compile("total_rss (\\d+)");
    public static final Pattern CACHE_IN_BYTES = Pattern.compile("total_cache (\\d+)");
    public static final Pattern SWAP_IN_BYTES = Pattern.compile("total_swap (\\d+)");

    /* renamed from: com.android.server.am.MemoryStatUtil$MemoryStat */
    /* loaded from: classes.dex */
    public static final class MemoryStat {
        public long cacheInBytes;
        public long pgfault;
        public long pgmajfault;
        public long rssInBytes;
        public long swapInBytes;
    }

    public static MemoryStat readMemoryStatFromFilesystem(int i, int i2) {
        return hasMemcg() ? readMemoryStatFromMemcg(i, i2) : readMemoryStatFromProcfs(i2);
    }

    public static MemoryStat readMemoryStatFromMemcg(int i, int i2) {
        return parseMemoryStatFromMemcg(readFileContents(String.format(Locale.US, "/dev/memcg/apps/uid_%d/pid_%d/memory.stat", Integer.valueOf(i), Integer.valueOf(i2))));
    }

    public static MemoryStat readMemoryStatFromProcfs(int i) {
        return parseMemoryStatFromProcfs(readFileContents(String.format(Locale.US, "/proc/%d/stat", Integer.valueOf(i))));
    }

    public static String readFileContents(String str) {
        File file = new File(str);
        if (file.exists()) {
            try {
                return FileUtils.readTextFile(file, 0, null);
            } catch (IOException e) {
                Slog.e("ActivityManager", "Failed to read file:", e);
                return null;
            }
        }
        return null;
    }

    @VisibleForTesting
    public static MemoryStat parseMemoryStatFromMemcg(String str) {
        if (str == null || str.isEmpty()) {
            return null;
        }
        MemoryStat memoryStat = new MemoryStat();
        memoryStat.pgfault = tryParseLong(PGFAULT, str);
        memoryStat.pgmajfault = tryParseLong(PGMAJFAULT, str);
        memoryStat.rssInBytes = tryParseLong(RSS_IN_BYTES, str);
        memoryStat.cacheInBytes = tryParseLong(CACHE_IN_BYTES, str);
        memoryStat.swapInBytes = tryParseLong(SWAP_IN_BYTES, str);
        return memoryStat;
    }

    @VisibleForTesting
    public static MemoryStat parseMemoryStatFromProcfs(String str) {
        if (str != null && !str.isEmpty()) {
            String[] split = str.split(" ");
            if (split.length < 24) {
                return null;
            }
            try {
                MemoryStat memoryStat = new MemoryStat();
                memoryStat.pgfault = Long.parseLong(split[9]);
                memoryStat.pgmajfault = Long.parseLong(split[11]);
                memoryStat.rssInBytes = Long.parseLong(split[23]) * PAGE_SIZE;
                return memoryStat;
            } catch (NumberFormatException e) {
                Slog.e("ActivityManager", "Failed to parse value", e);
            }
        }
        return null;
    }

    public static boolean hasMemcg() {
        return DEVICE_HAS_PER_APP_MEMCG;
    }

    public static long tryParseLong(Pattern pattern, String str) {
        Matcher matcher = pattern.matcher(str);
        try {
            if (matcher.find()) {
                return Long.parseLong(matcher.group(1));
            }
            return 0L;
        } catch (NumberFormatException e) {
            Slog.e("ActivityManager", "Failed to parse value", e);
            return 0L;
        }
    }
}
