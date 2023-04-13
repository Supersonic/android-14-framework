package com.android.server.stats.pull;

import android.os.FileUtils;
import android.util.Slog;
import android.util.SparseArray;
import com.android.internal.annotations.VisibleForTesting;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/* loaded from: classes2.dex */
public final class IonMemoryUtil {
    public static final Pattern ION_HEAP_SIZE_IN_BYTES = Pattern.compile("\n\\s*total\\s*(\\d+)\\s*\n");
    public static final Pattern PROCESS_ION_HEAP_SIZE_IN_BYTES = Pattern.compile("\n\\s+\\S+\\s+(\\d+)\\s+(\\d+)");

    public static long readSystemIonHeapSizeFromDebugfs() {
        return parseIonHeapSizeFromDebugfs(readFile("/sys/kernel/debug/ion/heaps/system"));
    }

    @VisibleForTesting
    public static long parseIonHeapSizeFromDebugfs(String str) {
        if (str.isEmpty()) {
            return 0L;
        }
        Matcher matcher = ION_HEAP_SIZE_IN_BYTES.matcher(str);
        try {
            if (matcher.find()) {
                return Long.parseLong(matcher.group(1));
            }
            return 0L;
        } catch (NumberFormatException e) {
            Slog.e("IonMemoryUtil", "Failed to parse value", e);
            return 0L;
        }
    }

    public static List<IonAllocations> readProcessSystemIonHeapSizesFromDebugfs() {
        return parseProcessIonHeapSizesFromDebugfs(readFile("/sys/kernel/debug/ion/heaps/system"));
    }

    @VisibleForTesting
    public static List<IonAllocations> parseProcessIonHeapSizesFromDebugfs(String str) {
        if (str.isEmpty()) {
            return Collections.emptyList();
        }
        Matcher matcher = PROCESS_ION_HEAP_SIZE_IN_BYTES.matcher(str);
        SparseArray sparseArray = new SparseArray();
        while (matcher.find()) {
            try {
                int parseInt = Integer.parseInt(matcher.group(1));
                long parseLong = Long.parseLong(matcher.group(2));
                IonAllocations ionAllocations = (IonAllocations) sparseArray.get(parseInt);
                if (ionAllocations == null) {
                    ionAllocations = new IonAllocations();
                    sparseArray.put(parseInt, ionAllocations);
                }
                ionAllocations.pid = parseInt;
                ionAllocations.totalSizeInBytes += parseLong;
                ionAllocations.count++;
                ionAllocations.maxSizeInBytes = Math.max(ionAllocations.maxSizeInBytes, parseLong);
            } catch (NumberFormatException e) {
                Slog.e("IonMemoryUtil", "Failed to parse value", e);
            }
        }
        ArrayList arrayList = new ArrayList(sparseArray.size());
        for (int i = 0; i < sparseArray.size(); i++) {
            arrayList.add((IonAllocations) sparseArray.valueAt(i));
        }
        return arrayList;
    }

    public static String readFile(String str) {
        try {
            return FileUtils.readTextFile(new File(str), 0, null);
        } catch (IOException e) {
            Slog.e("IonMemoryUtil", "Failed to read file", e);
            return "";
        }
    }

    /* loaded from: classes2.dex */
    public static final class IonAllocations {
        public int count;
        public long maxSizeInBytes;
        public int pid;
        public long totalSizeInBytes;

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || IonAllocations.class != obj.getClass()) {
                return false;
            }
            IonAllocations ionAllocations = (IonAllocations) obj;
            return this.pid == ionAllocations.pid && this.totalSizeInBytes == ionAllocations.totalSizeInBytes && this.count == ionAllocations.count && this.maxSizeInBytes == ionAllocations.maxSizeInBytes;
        }

        public int hashCode() {
            return Objects.hash(Integer.valueOf(this.pid), Long.valueOf(this.totalSizeInBytes), Integer.valueOf(this.count), Long.valueOf(this.maxSizeInBytes));
        }

        public String toString() {
            return "IonAllocations{pid=" + this.pid + ", totalSizeInBytes=" + this.totalSizeInBytes + ", count=" + this.count + ", maxSizeInBytes=" + this.maxSizeInBytes + '}';
        }
    }
}
