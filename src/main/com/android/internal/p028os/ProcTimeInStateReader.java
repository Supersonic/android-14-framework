package com.android.internal.p028os;

import android.p008os.Process;
import android.util.IntArray;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
/* renamed from: com.android.internal.os.ProcTimeInStateReader */
/* loaded from: classes4.dex */
public class ProcTimeInStateReader {
    private static final String TAG = "ProcTimeInStateReader";
    private long[] mFrequenciesKhz;
    private int[] mTimeInStateTimeFormat;
    private static final int[] TIME_IN_STATE_LINE_FREQUENCY_FORMAT = {8224, 10};
    private static final int[] TIME_IN_STATE_LINE_TIME_FORMAT = {32, 8202};
    private static final int[] TIME_IN_STATE_HEADER_LINE_FORMAT = {10};

    public ProcTimeInStateReader(Path initialTimeInStateFile) throws IOException {
        initializeTimeInStateFormat(initialTimeInStateFile);
    }

    public long[] getUsageTimesMillis(Path timeInStatePath) {
        long[] readLongs = new long[this.mFrequenciesKhz.length];
        boolean readSuccess = Process.readProcFile(timeInStatePath.toString(), this.mTimeInStateTimeFormat, null, readLongs, null);
        if (!readSuccess) {
            return null;
        }
        for (int i = 0; i < readLongs.length; i++) {
            readLongs[i] = readLongs[i] * 10;
        }
        return readLongs;
    }

    public long[] getFrequenciesKhz() {
        return this.mFrequenciesKhz;
    }

    /* JADX WARN: Incorrect condition in loop: B:4:0x0014 */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    private void initializeTimeInStateFormat(Path timeInStatePath) throws IOException {
        byte[] timeInStateBytes = Files.readAllBytes(timeInStatePath);
        IntArray timeInStateFrequencyFormat = new IntArray();
        IntArray timeInStateTimeFormat = new IntArray();
        int i = 0;
        int numFrequencies = 0;
        while (i < numFrequencies) {
            if (!Character.isDigit(timeInStateBytes[i])) {
                int[] iArr = TIME_IN_STATE_HEADER_LINE_FORMAT;
                timeInStateFrequencyFormat.addAll(iArr);
                timeInStateTimeFormat.addAll(iArr);
            } else {
                timeInStateFrequencyFormat.addAll(TIME_IN_STATE_LINE_FREQUENCY_FORMAT);
                timeInStateTimeFormat.addAll(TIME_IN_STATE_LINE_TIME_FORMAT);
                numFrequencies++;
            }
            while (i < timeInStateBytes.length && timeInStateBytes[i] != 10) {
                i++;
            }
            i++;
        }
        if (numFrequencies == 0) {
            throw new IOException("Empty time_in_state file");
        }
        long[] readLongs = new long[numFrequencies];
        boolean readSuccess = Process.parseProcLine(timeInStateBytes, 0, timeInStateBytes.length, timeInStateFrequencyFormat.toArray(), null, readLongs, null);
        if (!readSuccess) {
            throw new IOException("Failed to parse time_in_state file");
        }
        this.mTimeInStateTimeFormat = timeInStateTimeFormat.toArray();
        this.mFrequenciesKhz = readLongs;
    }
}
