package com.android.internal.power;

import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.p008os.Parcel;
import android.text.TextUtils;
import android.util.DebugUtils;
import android.util.Slog;
import android.view.Display;
import com.android.internal.p028os.LongMultiStateCounter;
import java.io.PrintWriter;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Arrays;
/* loaded from: classes4.dex */
public class EnergyConsumerStats {
    private static final int INVALID_STATE = -1;
    public static final int NUMBER_STANDARD_POWER_BUCKETS = 10;
    public static final int POWER_BUCKET_BLUETOOTH = 5;
    public static final int POWER_BUCKET_CAMERA = 8;
    public static final int POWER_BUCKET_CPU = 3;
    public static final int POWER_BUCKET_GNSS = 6;
    public static final int POWER_BUCKET_MOBILE_RADIO = 7;
    public static final int POWER_BUCKET_PHONE = 9;
    public static final int POWER_BUCKET_SCREEN_DOZE = 1;
    public static final int POWER_BUCKET_SCREEN_ON = 0;
    public static final int POWER_BUCKET_SCREEN_OTHER = 2;
    public static final int POWER_BUCKET_UNKNOWN = -1;
    public static final int POWER_BUCKET_WIFI = 4;
    private static final String TAG = "MeasuredEnergyStats";
    private final long[] mAccumulatedChargeMicroCoulomb;
    private LongMultiStateCounter[] mAccumulatedMultiStateChargeMicroCoulomb;
    private final Config mConfig;
    private int mState = -1;
    private long mStateChangeTimestampMs;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes4.dex */
    public @interface StandardPowerBucket {
    }

    /* loaded from: classes4.dex */
    public static class Config {
        private final String[] mCustomBucketNames;
        private final String[] mStateNames;
        private final boolean[] mSupportedMultiStateBuckets;
        private final boolean[] mSupportedStandardBuckets;

        public Config(boolean[] supportedStandardBuckets, String[] customBucketNames, int[] supportedMultiStateBuckets, String[] stateNames) {
            this.mSupportedStandardBuckets = supportedStandardBuckets;
            String[] strArr = customBucketNames != null ? customBucketNames : new String[0];
            this.mCustomBucketNames = strArr;
            this.mSupportedMultiStateBuckets = new boolean[supportedStandardBuckets.length + strArr.length];
            for (int bucket : supportedMultiStateBuckets) {
                if (this.mSupportedStandardBuckets[bucket]) {
                    this.mSupportedMultiStateBuckets[bucket] = true;
                }
            }
            this.mStateNames = stateNames != null ? stateNames : new String[]{""};
        }

        public boolean isCompatible(Config other) {
            return Arrays.equals(this.mSupportedStandardBuckets, other.mSupportedStandardBuckets) && Arrays.equals(this.mCustomBucketNames, other.mCustomBucketNames) && Arrays.equals(this.mSupportedMultiStateBuckets, other.mSupportedMultiStateBuckets) && Arrays.equals(this.mStateNames, other.mStateNames);
        }

        public static void writeToParcel(Config config, Parcel out) {
            boolean[] zArr;
            if (config == null) {
                out.writeBoolean(false);
                return;
            }
            out.writeBoolean(true);
            out.writeInt(config.mSupportedStandardBuckets.length);
            out.writeBooleanArray(config.mSupportedStandardBuckets);
            out.writeStringArray(config.mCustomBucketNames);
            int multiStateBucketCount = 0;
            for (boolean supported : config.mSupportedMultiStateBuckets) {
                if (supported) {
                    multiStateBucketCount++;
                }
            }
            int[] supportedMultiStateBuckets = new int[multiStateBucketCount];
            int index = 0;
            int bucket = 0;
            while (true) {
                boolean[] zArr2 = config.mSupportedMultiStateBuckets;
                if (bucket < zArr2.length) {
                    if (zArr2[bucket]) {
                        supportedMultiStateBuckets[index] = bucket;
                        index++;
                    }
                    bucket++;
                } else {
                    out.writeInt(multiStateBucketCount);
                    out.writeIntArray(supportedMultiStateBuckets);
                    out.writeStringArray(config.mStateNames);
                    return;
                }
            }
        }

        public static Config createFromParcel(Parcel in) {
            if (!in.readBoolean()) {
                return null;
            }
            int supportedStandardBucketCount = in.readInt();
            boolean[] supportedStandardBuckets = new boolean[supportedStandardBucketCount];
            in.readBooleanArray(supportedStandardBuckets);
            String[] customBucketNames = in.readStringArray();
            int supportedMultiStateBucketCount = in.readInt();
            int[] supportedMultiStateBuckets = new int[supportedMultiStateBucketCount];
            in.readIntArray(supportedMultiStateBuckets);
            String[] stateNames = in.readStringArray();
            return new Config(supportedStandardBuckets, customBucketNames, supportedMultiStateBuckets, stateNames);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public int getNumberOfBuckets() {
            return this.mSupportedStandardBuckets.length + this.mCustomBucketNames.length;
        }

        public boolean isSupportedBucket(int index) {
            return this.mSupportedStandardBuckets[index];
        }

        public String[] getCustomBucketNames() {
            return this.mCustomBucketNames;
        }

        public boolean isSupportedMultiStateBucket(int index) {
            return this.mSupportedMultiStateBuckets[index];
        }

        public String[] getStateNames() {
            return this.mStateNames;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public String getBucketName(int index) {
            if (EnergyConsumerStats.isValidStandardBucket(index)) {
                return DebugUtils.valueToString(EnergyConsumerStats.class, "POWER_BUCKET_", index);
            }
            int customBucket = EnergyConsumerStats.indexToCustomBucket(index);
            StringBuilder name = new StringBuilder().append("CUSTOM_").append(customBucket);
            if (!TextUtils.isEmpty(this.mCustomBucketNames[customBucket])) {
                name.append('(').append(this.mCustomBucketNames[customBucket]).append(')');
            }
            return name.toString();
        }
    }

    public EnergyConsumerStats(Config config) {
        this.mConfig = config;
        int numTotalBuckets = config.getNumberOfBuckets();
        this.mAccumulatedChargeMicroCoulomb = new long[numTotalBuckets];
        for (int stdBucket = 0; stdBucket < 10; stdBucket++) {
            if (!this.mConfig.mSupportedStandardBuckets[stdBucket]) {
                this.mAccumulatedChargeMicroCoulomb[stdBucket] = -1;
            }
        }
    }

    public static EnergyConsumerStats createFromParcel(Config config, Parcel in) {
        if (!in.readBoolean()) {
            return null;
        }
        return new EnergyConsumerStats(config, in);
    }

    public EnergyConsumerStats(Config config, Parcel in) {
        this.mConfig = config;
        int size = in.readInt();
        long[] jArr = new long[size];
        this.mAccumulatedChargeMicroCoulomb = jArr;
        in.readLongArray(jArr);
        if (in.readBoolean()) {
            this.mAccumulatedMultiStateChargeMicroCoulomb = new LongMultiStateCounter[size];
            for (int i = 0; i < size; i++) {
                if (in.readBoolean()) {
                    this.mAccumulatedMultiStateChargeMicroCoulomb[i] = LongMultiStateCounter.CREATOR.createFromParcel(in);
                }
            }
            return;
        }
        this.mAccumulatedMultiStateChargeMicroCoulomb = null;
    }

    public void writeToParcel(Parcel out) {
        LongMultiStateCounter[] longMultiStateCounterArr;
        out.writeInt(this.mAccumulatedChargeMicroCoulomb.length);
        out.writeLongArray(this.mAccumulatedChargeMicroCoulomb);
        if (this.mAccumulatedMultiStateChargeMicroCoulomb != null) {
            out.writeBoolean(true);
            for (LongMultiStateCounter counter : this.mAccumulatedMultiStateChargeMicroCoulomb) {
                if (counter != null) {
                    out.writeBoolean(true);
                    counter.writeToParcel(out, 0);
                } else {
                    out.writeBoolean(false);
                }
            }
            return;
        }
        out.writeBoolean(false);
    }

    private void readSummaryFromParcel(Parcel in) {
        int numWrittenEntries = in.readInt();
        for (int entry = 0; entry < numWrittenEntries; entry++) {
            int index = in.readInt();
            long chargeUC = in.readLong();
            LongMultiStateCounter multiStateCounter = null;
            if (in.readBoolean()) {
                LongMultiStateCounter multiStateCounter2 = LongMultiStateCounter.CREATOR.createFromParcel(in);
                multiStateCounter = multiStateCounter2;
                if (this.mConfig == null || multiStateCounter.getStateCount() != this.mConfig.getStateNames().length) {
                    multiStateCounter = null;
                }
            }
            if (index < this.mAccumulatedChargeMicroCoulomb.length) {
                setValueIfSupported(index, chargeUC);
                if (multiStateCounter != null) {
                    if (this.mAccumulatedMultiStateChargeMicroCoulomb == null) {
                        this.mAccumulatedMultiStateChargeMicroCoulomb = new LongMultiStateCounter[this.mAccumulatedChargeMicroCoulomb.length];
                    }
                    this.mAccumulatedMultiStateChargeMicroCoulomb[index] = multiStateCounter;
                }
            }
        }
    }

    private void writeSummaryToParcel(Parcel out) {
        int posOfNumWrittenEntries = out.dataPosition();
        out.writeInt(0);
        int numWrittenEntries = 0;
        int index = 0;
        while (true) {
            long[] jArr = this.mAccumulatedChargeMicroCoulomb;
            if (index < jArr.length) {
                long charge = jArr[index];
                if (charge > 0) {
                    out.writeInt(index);
                    out.writeLong(charge);
                    LongMultiStateCounter[] longMultiStateCounterArr = this.mAccumulatedMultiStateChargeMicroCoulomb;
                    if (longMultiStateCounterArr != null && longMultiStateCounterArr[index] != null) {
                        out.writeBoolean(true);
                        this.mAccumulatedMultiStateChargeMicroCoulomb[index].writeToParcel(out, 0);
                    } else {
                        out.writeBoolean(false);
                    }
                    numWrittenEntries++;
                }
                index++;
            } else {
                int currPos = out.dataPosition();
                out.setDataPosition(posOfNumWrittenEntries);
                out.writeInt(numWrittenEntries);
                out.setDataPosition(currPos);
                return;
            }
        }
    }

    public void updateStandardBucket(int bucket, long chargeDeltaUC) {
        updateStandardBucket(bucket, chargeDeltaUC, 0L);
    }

    public void updateStandardBucket(int bucket, long chargeDeltaUC, long timestampMs) {
        checkValidStandardBucket(bucket);
        updateEntry(bucket, chargeDeltaUC, timestampMs);
    }

    public void updateCustomBucket(int customBucket, long chargeDeltaUC) {
        updateCustomBucket(customBucket, chargeDeltaUC, 0L);
    }

    public void updateCustomBucket(int customBucket, long chargeDeltaUC, long timestampMs) {
        if (!isValidCustomBucket(customBucket)) {
            Slog.m96e(TAG, "Attempted to update invalid custom bucket " + customBucket);
            return;
        }
        int index = customBucketToIndex(customBucket);
        updateEntry(index, chargeDeltaUC, timestampMs);
    }

    private void updateEntry(int index, long chargeDeltaUC, long timestampMs) {
        long[] jArr = this.mAccumulatedChargeMicroCoulomb;
        long j = jArr[index];
        if (j >= 0) {
            jArr[index] = j + chargeDeltaUC;
            if (this.mState != -1 && this.mConfig.isSupportedMultiStateBucket(index)) {
                if (this.mAccumulatedMultiStateChargeMicroCoulomb == null) {
                    this.mAccumulatedMultiStateChargeMicroCoulomb = new LongMultiStateCounter[this.mAccumulatedChargeMicroCoulomb.length];
                }
                LongMultiStateCounter counter = this.mAccumulatedMultiStateChargeMicroCoulomb[index];
                if (counter == null) {
                    counter = new LongMultiStateCounter(this.mConfig.mStateNames.length);
                    this.mAccumulatedMultiStateChargeMicroCoulomb[index] = counter;
                    counter.setState(this.mState, this.mStateChangeTimestampMs);
                    counter.updateValue(0L, this.mStateChangeTimestampMs);
                }
                counter.updateValue(this.mAccumulatedChargeMicroCoulomb[index], timestampMs);
                return;
            }
            return;
        }
        Slog.wtf(TAG, "Attempting to add " + chargeDeltaUC + " to unavailable bucket " + this.mConfig.getBucketName(index) + " whose value was " + this.mAccumulatedChargeMicroCoulomb[index]);
    }

    public void setState(int state, long timestampMs) {
        this.mState = state;
        this.mStateChangeTimestampMs = timestampMs;
        if (this.mAccumulatedMultiStateChargeMicroCoulomb == null) {
            this.mAccumulatedMultiStateChargeMicroCoulomb = new LongMultiStateCounter[this.mAccumulatedChargeMicroCoulomb.length];
        }
        int i = 0;
        while (true) {
            LongMultiStateCounter[] longMultiStateCounterArr = this.mAccumulatedMultiStateChargeMicroCoulomb;
            if (i < longMultiStateCounterArr.length) {
                LongMultiStateCounter counter = longMultiStateCounterArr[i];
                if (counter == null && this.mConfig.isSupportedMultiStateBucket(i)) {
                    counter = new LongMultiStateCounter(this.mConfig.mStateNames.length);
                    counter.updateValue(0L, timestampMs);
                    this.mAccumulatedMultiStateChargeMicroCoulomb[i] = counter;
                }
                if (counter != null) {
                    counter.setState(state, timestampMs);
                }
                i++;
            } else {
                return;
            }
        }
    }

    public long getAccumulatedStandardBucketCharge(int bucket) {
        checkValidStandardBucket(bucket);
        return this.mAccumulatedChargeMicroCoulomb[bucket];
    }

    public long getAccumulatedStandardBucketCharge(int bucket, int state) {
        LongMultiStateCounter counter;
        if (!this.mConfig.isSupportedMultiStateBucket(bucket)) {
            return -1L;
        }
        LongMultiStateCounter[] longMultiStateCounterArr = this.mAccumulatedMultiStateChargeMicroCoulomb;
        if (longMultiStateCounterArr == null || (counter = longMultiStateCounterArr[bucket]) == null) {
            return 0L;
        }
        return counter.getCount(state);
    }

    public long getAccumulatedCustomBucketCharge(int customBucket) {
        if (!isValidCustomBucket(customBucket)) {
            return -1L;
        }
        return this.mAccumulatedChargeMicroCoulomb[customBucketToIndex(customBucket)];
    }

    public long[] getAccumulatedCustomBucketCharges() {
        long[] charges = new long[getNumberCustomPowerBuckets()];
        for (int bucket = 0; bucket < charges.length; bucket++) {
            charges[bucket] = this.mAccumulatedChargeMicroCoulomb[customBucketToIndex(bucket)];
        }
        return charges;
    }

    public static int getDisplayPowerBucket(int screenState) {
        if (Display.isOnState(screenState)) {
            return 0;
        }
        if (Display.isDozeState(screenState)) {
            return 1;
        }
        return 2;
    }

    public static EnergyConsumerStats createAndReadSummaryFromParcel(Config config, Parcel in) {
        int arraySize = in.readInt();
        if (arraySize == 0) {
            return null;
        }
        if (config == null) {
            EnergyConsumerStats mes = new EnergyConsumerStats(new Config(new boolean[arraySize], null, new int[0], new String[]{""}));
            mes.readSummaryFromParcel(in);
            return null;
        } else if (arraySize != config.getNumberOfBuckets()) {
            Slog.wtf(TAG, "Size of MeasuredEnergyStats parcel (" + arraySize + ") does not match config (" + config.getNumberOfBuckets() + ").");
            EnergyConsumerStats mes2 = new EnergyConsumerStats(config);
            mes2.readSummaryFromParcel(in);
            return null;
        } else {
            EnergyConsumerStats stats = new EnergyConsumerStats(config);
            stats.readSummaryFromParcel(in);
            if (!stats.containsInterestingData()) {
                return null;
            }
            return stats;
        }
    }

    private boolean containsInterestingData() {
        int index = 0;
        while (true) {
            long[] jArr = this.mAccumulatedChargeMicroCoulomb;
            if (index < jArr.length) {
                if (jArr[index] > 0) {
                    return true;
                }
                index++;
            } else {
                return false;
            }
        }
    }

    public static void writeSummaryToParcel(EnergyConsumerStats stats, Parcel dest) {
        if (stats == null) {
            dest.writeInt(0);
            return;
        }
        dest.writeInt(stats.mConfig.getNumberOfBuckets());
        stats.writeSummaryToParcel(dest);
    }

    private void reset() {
        LongMultiStateCounter longMultiStateCounter;
        int numIndices = this.mConfig.getNumberOfBuckets();
        for (int index = 0; index < numIndices; index++) {
            setValueIfSupported(index, 0L);
            LongMultiStateCounter[] longMultiStateCounterArr = this.mAccumulatedMultiStateChargeMicroCoulomb;
            if (longMultiStateCounterArr != null && (longMultiStateCounter = longMultiStateCounterArr[index]) != null) {
                longMultiStateCounter.reset();
            }
        }
    }

    public static void resetIfNotNull(EnergyConsumerStats stats) {
        if (stats != null) {
            stats.reset();
        }
    }

    private void setValueIfSupported(int index, long value) {
        long[] jArr = this.mAccumulatedChargeMicroCoulomb;
        if (jArr[index] != -1) {
            jArr[index] = value;
        }
    }

    public boolean isStandardBucketSupported(int bucket) {
        checkValidStandardBucket(bucket);
        return isIndexSupported(bucket);
    }

    private boolean isIndexSupported(int index) {
        return this.mAccumulatedChargeMicroCoulomb[index] != -1;
    }

    public void dump(PrintWriter pw) {
        LongMultiStateCounter counter;
        pw.print("   ");
        for (int index = 0; index < this.mAccumulatedChargeMicroCoulomb.length; index++) {
            pw.print(this.mConfig.getBucketName(index));
            pw.print(" : ");
            pw.print(this.mAccumulatedChargeMicroCoulomb[index]);
            if (!isIndexSupported(index)) {
                pw.print(" (unsupported)");
            }
            LongMultiStateCounter[] longMultiStateCounterArr = this.mAccumulatedMultiStateChargeMicroCoulomb;
            if (longMultiStateCounterArr != null && (counter = longMultiStateCounterArr[index]) != null) {
                pw.print(" [");
                for (int i = 0; i < this.mConfig.mStateNames.length; i++) {
                    if (i != 0) {
                        pw.print(" ");
                    }
                    pw.print(this.mConfig.mStateNames[i]);
                    pw.print(": ");
                    pw.print(counter.getCount(i));
                }
                pw.print(NavigationBarInflaterView.SIZE_MOD_END);
            }
            if (index != this.mAccumulatedChargeMicroCoulomb.length - 1) {
                pw.print(", ");
            }
        }
        pw.println();
    }

    public int getNumberCustomPowerBuckets() {
        return this.mAccumulatedChargeMicroCoulomb.length - 10;
    }

    private static int customBucketToIndex(int customBucket) {
        return customBucket + 10;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static int indexToCustomBucket(int index) {
        return index - 10;
    }

    private static void checkValidStandardBucket(int bucket) {
        if (!isValidStandardBucket(bucket)) {
            throw new IllegalArgumentException("Illegal StandardPowerBucket " + bucket);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean isValidStandardBucket(int bucket) {
        return bucket >= 0 && bucket < 10;
    }

    public boolean isValidCustomBucket(int customBucket) {
        return customBucket >= 0 && customBucketToIndex(customBucket) < this.mAccumulatedChargeMicroCoulomb.length;
    }
}
