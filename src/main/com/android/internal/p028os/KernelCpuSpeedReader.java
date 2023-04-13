package com.android.internal.p028os;

import android.p008os.StrictMode;
import android.system.Os;
import android.system.OsConstants;
import android.text.TextUtils;
import android.util.Slog;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
/* renamed from: com.android.internal.os.KernelCpuSpeedReader */
/* loaded from: classes4.dex */
public class KernelCpuSpeedReader {
    private static final String TAG = "KernelCpuSpeedReader";
    private final long[] mDeltaSpeedTimesMs;
    private final long mJiffyMillis;
    private final long[] mLastSpeedTimesMs;
    private final int mNumSpeedSteps;
    private final String mProcFile;

    public KernelCpuSpeedReader(int cpuNumber, int numSpeedSteps) {
        this.mProcFile = String.format("/sys/devices/system/cpu/cpu%d/cpufreq/stats/time_in_state", Integer.valueOf(cpuNumber));
        this.mNumSpeedSteps = numSpeedSteps;
        this.mLastSpeedTimesMs = new long[numSpeedSteps];
        this.mDeltaSpeedTimesMs = new long[numSpeedSteps];
        long jiffyHz = Os.sysconf(OsConstants._SC_CLK_TCK);
        this.mJiffyMillis = 1000 / jiffyHz;
    }

    public long[] readDelta() {
        String line;
        StrictMode.ThreadPolicy policy = StrictMode.allowThreadDiskReads();
        try {
            try {
                BufferedReader reader = new BufferedReader(new FileReader(this.mProcFile));
                try {
                    TextUtils.SimpleStringSplitter splitter = new TextUtils.SimpleStringSplitter(' ');
                    for (int speedIndex = 0; speedIndex < this.mLastSpeedTimesMs.length && (line = reader.readLine()) != null; speedIndex++) {
                        splitter.setString(line);
                        splitter.next();
                        long time = Long.parseLong(splitter.next()) * this.mJiffyMillis;
                        long[] jArr = this.mLastSpeedTimesMs;
                        long j = jArr[speedIndex];
                        if (time < j) {
                            this.mDeltaSpeedTimesMs[speedIndex] = time;
                        } else {
                            this.mDeltaSpeedTimesMs[speedIndex] = time - j;
                        }
                        jArr[speedIndex] = time;
                    }
                    reader.close();
                } catch (Throwable th) {
                    try {
                        reader.close();
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                    }
                    throw th;
                }
            } catch (IOException e) {
                Slog.m96e(TAG, "Failed to read cpu-freq: " + e.getMessage());
                Arrays.fill(this.mDeltaSpeedTimesMs, 0L);
            }
            StrictMode.setThreadPolicy(policy);
            return this.mDeltaSpeedTimesMs;
        } catch (Throwable th3) {
            StrictMode.setThreadPolicy(policy);
            throw th3;
        }
    }

    public long[] readAbsolute() {
        String line;
        StrictMode.ThreadPolicy policy = StrictMode.allowThreadDiskReads();
        long[] speedTimeMs = new long[this.mNumSpeedSteps];
        try {
            try {
                BufferedReader reader = new BufferedReader(new FileReader(this.mProcFile));
                try {
                    TextUtils.SimpleStringSplitter splitter = new TextUtils.SimpleStringSplitter(' ');
                    for (int speedIndex = 0; speedIndex < this.mNumSpeedSteps && (line = reader.readLine()) != null; speedIndex++) {
                        splitter.setString(line);
                        splitter.next();
                        long time = Long.parseLong(splitter.next()) * this.mJiffyMillis;
                        speedTimeMs[speedIndex] = time;
                    }
                    reader.close();
                } catch (Throwable th) {
                    try {
                        reader.close();
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                    }
                    throw th;
                }
            } catch (IOException e) {
                Slog.m96e(TAG, "Failed to read cpu-freq: " + e.getMessage());
                Arrays.fill(speedTimeMs, 0L);
            }
            return speedTimeMs;
        } finally {
            StrictMode.setThreadPolicy(policy);
        }
    }
}
