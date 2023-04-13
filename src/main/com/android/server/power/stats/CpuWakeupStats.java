package com.android.server.power.stats;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerExecutor;
import android.os.UserHandle;
import android.provider.DeviceConfig;
import android.util.IndentingPrintWriter;
import android.util.IntArray;
import android.util.LongSparseArray;
import android.util.Slog;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.util.SparseLongArray;
import android.util.TimeSparseArray;
import android.util.TimeUtils;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.FrameworkStatsLog;
import com.android.internal.util.IntPair;
import com.android.internal.util.jobs.XmlUtils;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/* loaded from: classes2.dex */
public class CpuWakeupStats {
    @VisibleForTesting
    static final long WAKEUP_REASON_HALF_WINDOW_MS = 500;
    public static final long WAKEUP_WRITE_DELAY_MS = TimeUnit.MINUTES.toMillis(2);
    public final Handler mHandler;
    public final IrqDeviceMap mIrqDeviceMap;
    @VisibleForTesting
    final Config mConfig = new Config();
    public final WakingActivityHistory mRecentWakingActivity = new WakingActivityHistory();
    @VisibleForTesting
    final TimeSparseArray<Wakeup> mWakeupEvents = new TimeSparseArray<>();
    @VisibleForTesting
    final TimeSparseArray<SparseArray<SparseBooleanArray>> mWakeupAttribution = new TimeSparseArray<>();

    public static int subsystemToStatsReason(int i) {
        int i2 = 1;
        if (i != 1) {
            i2 = 2;
            if (i != 2) {
                return 0;
            }
        }
        return i2;
    }

    public static String subsystemToString(int i) {
        return i != -1 ? i != 1 ? i != 2 ? "N/A" : "Wifi" : "Alarm" : "Unknown";
    }

    public CpuWakeupStats(Context context, int i, Handler handler) {
        this.mIrqDeviceMap = IrqDeviceMap.getInstance(context, i);
        this.mHandler = handler;
    }

    public synchronized void systemServicesReady() {
        this.mConfig.register(new HandlerExecutor(this.mHandler));
    }

    /* renamed from: logWakeupToStatsLog */
    public final synchronized void lambda$noteWakeupTimeAndReason$0(Wakeup wakeup) {
        int[] iArr;
        if (ArrayUtils.isEmpty(wakeup.mDevices)) {
            FrameworkStatsLog.write((int) FrameworkStatsLog.KERNEL_WAKEUP_ATTRIBUTED, 0, 0, (int[]) null, wakeup.mElapsedMillis);
            return;
        }
        SparseArray sparseArray = (SparseArray) this.mWakeupAttribution.get(wakeup.mElapsedMillis);
        if (sparseArray == null) {
            Slog.wtf("CpuWakeupStats", "Unexpected null attribution found for " + wakeup);
            return;
        }
        for (int i = 0; i < sparseArray.size(); i++) {
            int keyAt = sparseArray.keyAt(i);
            SparseBooleanArray sparseBooleanArray = (SparseBooleanArray) sparseArray.valueAt(i);
            if (sparseBooleanArray != null && sparseBooleanArray.size() != 0) {
                IntArray intArray = new IntArray(sparseBooleanArray.size());
                for (int i2 = 0; i2 < sparseBooleanArray.size(); i2++) {
                    if (sparseBooleanArray.valueAt(i2)) {
                        intArray.add(sparseBooleanArray.keyAt(i2));
                    }
                }
                iArr = intArray.toArray();
                FrameworkStatsLog.write((int) FrameworkStatsLog.KERNEL_WAKEUP_ATTRIBUTED, 1, subsystemToStatsReason(keyAt), iArr, wakeup.mElapsedMillis);
            }
            iArr = new int[0];
            FrameworkStatsLog.write((int) FrameworkStatsLog.KERNEL_WAKEUP_ATTRIBUTED, 1, subsystemToStatsReason(keyAt), iArr, wakeup.mElapsedMillis);
        }
    }

    public synchronized void noteWakeupTimeAndReason(long j, long j2, String str) {
        final Wakeup parseWakeup = Wakeup.parseWakeup(str, j, j2);
        if (parseWakeup == null) {
            return;
        }
        this.mWakeupEvents.put(j, parseWakeup);
        attemptAttributionFor(parseWakeup);
        this.mRecentWakingActivity.clearAllBefore(j - WAKEUP_REASON_HALF_WINDOW_MS);
        long j3 = j - this.mConfig.WAKEUP_STATS_RETENTION_MS;
        for (int closestIndexOnOrBefore = this.mWakeupEvents.closestIndexOnOrBefore(j3); closestIndexOnOrBefore >= 0; closestIndexOnOrBefore--) {
            this.mWakeupEvents.removeAt(closestIndexOnOrBefore);
        }
        for (int closestIndexOnOrBefore2 = this.mWakeupAttribution.closestIndexOnOrBefore(j3); closestIndexOnOrBefore2 >= 0; closestIndexOnOrBefore2--) {
            this.mWakeupAttribution.removeAt(closestIndexOnOrBefore2);
        }
        this.mHandler.postDelayed(new Runnable() { // from class: com.android.server.power.stats.CpuWakeupStats$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                CpuWakeupStats.this.lambda$noteWakeupTimeAndReason$0(parseWakeup);
            }
        }, WAKEUP_WRITE_DELAY_MS);
    }

    public synchronized void noteWakingActivity(int i, long j, int... iArr) {
        if (!attemptAttributionWith(i, j, iArr)) {
            this.mRecentWakingActivity.recordActivity(i, j, iArr);
        }
    }

    public final synchronized void attemptAttributionFor(Wakeup wakeup) {
        SparseBooleanArray responsibleSubsystemsForWakeup = getResponsibleSubsystemsForWakeup(wakeup);
        if (responsibleSubsystemsForWakeup == null) {
            return;
        }
        SparseArray sparseArray = (SparseArray) this.mWakeupAttribution.get(wakeup.mElapsedMillis);
        if (sparseArray == null) {
            sparseArray = new SparseArray();
            this.mWakeupAttribution.put(wakeup.mElapsedMillis, sparseArray);
        }
        for (int i = 0; i < responsibleSubsystemsForWakeup.size(); i++) {
            int keyAt = responsibleSubsystemsForWakeup.keyAt(i);
            long j = wakeup.mElapsedMillis;
            sparseArray.put(keyAt, this.mRecentWakingActivity.removeBetween(keyAt, j - WAKEUP_REASON_HALF_WINDOW_MS, j + WAKEUP_REASON_HALF_WINDOW_MS));
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:11:0x002a, code lost:
        r7 = (android.util.SparseArray) r5.mWakeupAttribution.get(r1.mElapsedMillis);
     */
    /* JADX WARN: Code restructure failed: missing block: B:12:0x0034, code lost:
        if (r7 != null) goto L17;
     */
    /* JADX WARN: Code restructure failed: missing block: B:13:0x0036, code lost:
        r7 = new android.util.SparseArray();
        r5.mWakeupAttribution.put(r1.mElapsedMillis, r7);
     */
    /* JADX WARN: Code restructure failed: missing block: B:14:0x0042, code lost:
        r0 = (android.util.SparseBooleanArray) r7.get(r6);
     */
    /* JADX WARN: Code restructure failed: missing block: B:15:0x0048, code lost:
        if (r0 != null) goto L20;
     */
    /* JADX WARN: Code restructure failed: missing block: B:16:0x004a, code lost:
        r0 = new android.util.SparseBooleanArray(r9.length);
        r7.put(r6, r0);
     */
    /* JADX WARN: Code restructure failed: missing block: B:17:0x0053, code lost:
        r6 = r9.length;
     */
    /* JADX WARN: Code restructure failed: missing block: B:19:0x0055, code lost:
        if (r8 >= r6) goto L26;
     */
    /* JADX WARN: Code restructure failed: missing block: B:20:0x0057, code lost:
        r0.put(r9[r8], true);
     */
    /* JADX WARN: Code restructure failed: missing block: B:21:0x005c, code lost:
        r8 = r8 + 1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:23:0x0060, code lost:
        return true;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final synchronized boolean attemptAttributionWith(int i, long j, int... iArr) {
        int closestIndexOnOrAfter = this.mWakeupEvents.closestIndexOnOrAfter(j - WAKEUP_REASON_HALF_WINDOW_MS);
        int closestIndexOnOrBefore = this.mWakeupEvents.closestIndexOnOrBefore(j + WAKEUP_REASON_HALF_WINDOW_MS);
        while (true) {
            int i2 = 0;
            if (closestIndexOnOrAfter > closestIndexOnOrBefore) {
                return false;
            }
            Wakeup wakeup = (Wakeup) this.mWakeupEvents.valueAt(closestIndexOnOrAfter);
            SparseBooleanArray responsibleSubsystemsForWakeup = getResponsibleSubsystemsForWakeup(wakeup);
            if (responsibleSubsystemsForWakeup != null && responsibleSubsystemsForWakeup.get(i)) {
                break;
            }
            closestIndexOnOrAfter++;
        }
    }

    public synchronized void dump(IndentingPrintWriter indentingPrintWriter, long j) {
        int i;
        indentingPrintWriter.println("CPU wakeup stats:");
        indentingPrintWriter.increaseIndent();
        this.mConfig.dump(indentingPrintWriter);
        indentingPrintWriter.println();
        this.mIrqDeviceMap.dump(indentingPrintWriter);
        indentingPrintWriter.println();
        this.mRecentWakingActivity.dump(indentingPrintWriter, j);
        indentingPrintWriter.println();
        SparseLongArray sparseLongArray = new SparseLongArray();
        indentingPrintWriter.println("Wakeup events:");
        indentingPrintWriter.increaseIndent();
        int size = this.mWakeupEvents.size() - 1;
        while (true) {
            if (size < 0) {
                break;
            }
            TimeUtils.formatDuration(this.mWakeupEvents.keyAt(size), j, indentingPrintWriter);
            indentingPrintWriter.println(XmlUtils.STRING_ARRAY_SEPARATOR);
            indentingPrintWriter.increaseIndent();
            Wakeup wakeup = (Wakeup) this.mWakeupEvents.valueAt(size);
            indentingPrintWriter.println(wakeup);
            indentingPrintWriter.print("Attribution: ");
            SparseArray sparseArray = (SparseArray) this.mWakeupAttribution.get(wakeup.mElapsedMillis);
            if (sparseArray == null) {
                indentingPrintWriter.println("N/A");
            } else {
                for (int i2 = 0; i2 < sparseArray.size(); i2++) {
                    if (i2 > 0) {
                        indentingPrintWriter.print(", ");
                    }
                    long j2 = sparseLongArray.get(sparseArray.keyAt(i2), IntPair.of(0, 0));
                    int first = IntPair.first(j2);
                    int second = IntPair.second(j2) + 1;
                    indentingPrintWriter.print("subsystem: " + subsystemToString(sparseArray.keyAt(i2)));
                    indentingPrintWriter.print(", uids: [");
                    SparseBooleanArray sparseBooleanArray = (SparseBooleanArray) sparseArray.valueAt(i2);
                    if (sparseBooleanArray != null) {
                        for (int i3 = 0; i3 < sparseBooleanArray.size(); i3++) {
                            if (i3 > 0) {
                                indentingPrintWriter.print(", ");
                            }
                            UserHandle.formatUid(indentingPrintWriter, sparseBooleanArray.keyAt(i3));
                        }
                        first++;
                    }
                    indentingPrintWriter.print("]");
                    sparseLongArray.put(sparseArray.keyAt(i2), IntPair.of(first, second));
                }
                indentingPrintWriter.println();
            }
            indentingPrintWriter.decreaseIndent();
            size--;
        }
        indentingPrintWriter.decreaseIndent();
        indentingPrintWriter.println("Attribution stats:");
        indentingPrintWriter.increaseIndent();
        for (i = 0; i < sparseLongArray.size(); i++) {
            indentingPrintWriter.print("Subsystem " + subsystemToString(sparseLongArray.keyAt(i)));
            indentingPrintWriter.print(": ");
            long valueAt = sparseLongArray.valueAt(i);
            indentingPrintWriter.println(IntPair.first(valueAt) + "/" + IntPair.second(valueAt));
        }
        indentingPrintWriter.println("Total: " + this.mWakeupEvents.size());
        indentingPrintWriter.decreaseIndent();
        indentingPrintWriter.decreaseIndent();
        indentingPrintWriter.println();
    }

    /* loaded from: classes2.dex */
    public static final class WakingActivityHistory {
        public static final long WAKING_ACTIVITY_RETENTION_MS = TimeUnit.MINUTES.toMillis(10);
        public SparseArray<TimeSparseArray<SparseBooleanArray>> mWakingActivity;

        public WakingActivityHistory() {
            this.mWakingActivity = new SparseArray<>();
        }

        public void recordActivity(int i, long j, int... iArr) {
            if (iArr == null) {
                return;
            }
            TimeSparseArray<SparseBooleanArray> timeSparseArray = this.mWakingActivity.get(i);
            if (timeSparseArray == null) {
                timeSparseArray = new TimeSparseArray<>();
                this.mWakingActivity.put(i, timeSparseArray);
            }
            SparseBooleanArray sparseBooleanArray = (SparseBooleanArray) timeSparseArray.get(j);
            if (sparseBooleanArray == null) {
                sparseBooleanArray = new SparseBooleanArray(iArr.length);
                timeSparseArray.put(j, sparseBooleanArray);
            }
            for (int i2 : iArr) {
                sparseBooleanArray.put(i2, true);
            }
            int closestIndexOnOrBefore = timeSparseArray.closestIndexOnOrBefore(j - WAKING_ACTIVITY_RETENTION_MS);
            for (int i3 = closestIndexOnOrBefore; i3 >= 0; i3--) {
                timeSparseArray.removeAt(closestIndexOnOrBefore);
            }
        }

        public void clearAllBefore(long j) {
            for (int size = this.mWakingActivity.size() - 1; size >= 0; size--) {
                TimeSparseArray<SparseBooleanArray> valueAt = this.mWakingActivity.valueAt(size);
                for (int closestIndexOnOrBefore = valueAt.closestIndexOnOrBefore(j); closestIndexOnOrBefore >= 0; closestIndexOnOrBefore--) {
                    valueAt.removeAt(closestIndexOnOrBefore);
                }
            }
        }

        public SparseBooleanArray removeBetween(int i, long j, long j2) {
            SparseBooleanArray sparseBooleanArray = new SparseBooleanArray();
            TimeSparseArray<SparseBooleanArray> timeSparseArray = this.mWakingActivity.get(i);
            if (timeSparseArray != null) {
                int closestIndexOnOrAfter = timeSparseArray.closestIndexOnOrAfter(j);
                int closestIndexOnOrBefore = timeSparseArray.closestIndexOnOrBefore(j2);
                for (int i2 = closestIndexOnOrBefore; i2 >= closestIndexOnOrAfter; i2--) {
                    SparseBooleanArray sparseBooleanArray2 = (SparseBooleanArray) timeSparseArray.valueAt(i2);
                    for (int i3 = 0; i3 < sparseBooleanArray2.size(); i3++) {
                        if (sparseBooleanArray2.valueAt(i3)) {
                            sparseBooleanArray.put(sparseBooleanArray2.keyAt(i3), true);
                        }
                    }
                }
                while (closestIndexOnOrBefore >= closestIndexOnOrAfter) {
                    timeSparseArray.removeAt(closestIndexOnOrBefore);
                    closestIndexOnOrBefore--;
                }
            }
            if (sparseBooleanArray.size() > 0) {
                return sparseBooleanArray;
            }
            return null;
        }

        public void dump(IndentingPrintWriter indentingPrintWriter, long j) {
            indentingPrintWriter.println("Recent waking activity:");
            indentingPrintWriter.increaseIndent();
            for (int i = 0; i < this.mWakingActivity.size(); i++) {
                indentingPrintWriter.println("Subsystem " + CpuWakeupStats.subsystemToString(this.mWakingActivity.keyAt(i)) + XmlUtils.STRING_ARRAY_SEPARATOR);
                LongSparseArray valueAt = this.mWakingActivity.valueAt(i);
                if (valueAt != null) {
                    indentingPrintWriter.increaseIndent();
                    for (int size = valueAt.size() - 1; size >= 0; size--) {
                        TimeUtils.formatDuration(valueAt.keyAt(size), j, indentingPrintWriter);
                        SparseBooleanArray sparseBooleanArray = (SparseBooleanArray) valueAt.valueAt(size);
                        if (sparseBooleanArray == null) {
                            indentingPrintWriter.println();
                        } else {
                            indentingPrintWriter.print(": ");
                            for (int i2 = 0; i2 < sparseBooleanArray.size(); i2++) {
                                if (sparseBooleanArray.valueAt(i2)) {
                                    UserHandle.formatUid(indentingPrintWriter, sparseBooleanArray.keyAt(i2));
                                    indentingPrintWriter.print(", ");
                                }
                            }
                            indentingPrintWriter.println();
                        }
                    }
                    indentingPrintWriter.decreaseIndent();
                }
            }
            indentingPrintWriter.decreaseIndent();
        }
    }

    public final SparseBooleanArray getResponsibleSubsystemsForWakeup(Wakeup wakeup) {
        boolean z;
        if (ArrayUtils.isEmpty(wakeup.mDevices)) {
            return null;
        }
        SparseBooleanArray sparseBooleanArray = new SparseBooleanArray();
        for (Wakeup.IrqDevice irqDevice : wakeup.mDevices) {
            List<String> subsystemsForDevice = this.mIrqDeviceMap.getSubsystemsForDevice(irqDevice.mDevice);
            if (subsystemsForDevice != null) {
                z = false;
                for (int i = 0; i < subsystemsForDevice.size(); i++) {
                    int stringToKnownSubsystem = stringToKnownSubsystem(subsystemsForDevice.get(i));
                    if (stringToKnownSubsystem != -1) {
                        sparseBooleanArray.put(stringToKnownSubsystem, true);
                        z = true;
                    }
                }
            } else {
                z = false;
            }
            if (!z) {
                sparseBooleanArray.put(-1, true);
            }
        }
        return sparseBooleanArray;
    }

    public static int stringToKnownSubsystem(String str) {
        str.hashCode();
        if (str.equals("Wifi")) {
            return 2;
        }
        return !str.equals("Alarm") ? -1 : 1;
    }

    /* loaded from: classes2.dex */
    public static final class Wakeup {
        public static final Pattern sIrqPattern = Pattern.compile("^(\\d+)\\s+(\\S+)");
        public IrqDevice[] mDevices;
        public long mElapsedMillis;
        public long mUptimeMillis;

        public Wakeup(IrqDevice[] irqDeviceArr, long j, long j2) {
            this.mElapsedMillis = j;
            this.mUptimeMillis = j2;
            this.mDevices = irqDeviceArr;
        }

        public static Wakeup parseWakeup(String str, long j, long j2) {
            String[] split = str.split(XmlUtils.STRING_ARRAY_SEPARATOR);
            if (!ArrayUtils.isEmpty(split)) {
                if (!split[0].startsWith("Abort")) {
                    IrqDevice[] irqDeviceArr = new IrqDevice[split.length];
                    int i = 0;
                    for (String str2 : split) {
                        Matcher matcher = sIrqPattern.matcher(str2.trim());
                        if (matcher.find()) {
                            try {
                                irqDeviceArr[i] = new IrqDevice(Integer.parseInt(matcher.group(1)), matcher.group(2));
                                i++;
                            } catch (NumberFormatException e) {
                                Slog.e("CpuWakeupStats.Wakeup", "Exception while parsing device names from part: " + str2, e);
                            }
                        }
                    }
                    if (i == 0) {
                        return null;
                    }
                    return new Wakeup((IrqDevice[]) Arrays.copyOf(irqDeviceArr, i), j, j2);
                }
            }
            return null;
        }

        public String toString() {
            return "Wakeup{mElapsedMillis=" + this.mElapsedMillis + ", mUptimeMillis=" + TimeUtils.formatDuration(this.mUptimeMillis) + ", mDevices=" + Arrays.toString(this.mDevices) + '}';
        }

        /* loaded from: classes2.dex */
        public static final class IrqDevice {
            public String mDevice;
            public int mLine;

            public IrqDevice(int i, String str) {
                this.mLine = i;
                this.mDevice = str;
            }

            public String toString() {
                return "IrqDevice{mLine=" + this.mLine + ", mDevice='" + this.mDevice + "'}";
            }
        }
    }

    /* loaded from: classes2.dex */
    public static final class Config implements DeviceConfig.OnPropertiesChangedListener {
        public volatile long WAKEUP_STATS_RETENTION_MS = DEFAULT_WAKEUP_STATS_RETENTION_MS;
        public static final String[] PROPERTY_NAMES = {"wakeup_stats_retention_ms"};
        public static final long DEFAULT_WAKEUP_STATS_RETENTION_MS = TimeUnit.DAYS.toMillis(3);

        public void register(Executor executor) {
            DeviceConfig.addOnPropertiesChangedListener("battery_stats", executor, this);
            onPropertiesChanged(DeviceConfig.getProperties("battery_stats", PROPERTY_NAMES));
        }

        public void onPropertiesChanged(DeviceConfig.Properties properties) {
            for (String str : properties.getKeyset()) {
                if (str != null && str.equals("wakeup_stats_retention_ms")) {
                    this.WAKEUP_STATS_RETENTION_MS = properties.getLong("wakeup_stats_retention_ms", DEFAULT_WAKEUP_STATS_RETENTION_MS);
                }
            }
        }

        public void dump(IndentingPrintWriter indentingPrintWriter) {
            indentingPrintWriter.println("Config:");
            indentingPrintWriter.increaseIndent();
            indentingPrintWriter.print("wakeup_stats_retention_ms", Long.valueOf(this.WAKEUP_STATS_RETENTION_MS));
            indentingPrintWriter.println();
            indentingPrintWriter.decreaseIndent();
        }
    }
}
