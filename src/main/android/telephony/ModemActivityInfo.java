package android.telephony;

import android.annotation.SystemApi;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.util.Range;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Arrays;
import java.util.Objects;
@SystemApi
/* loaded from: classes3.dex */
public final class ModemActivityInfo implements Parcelable {
    private static final int TX_POWER_LEVELS = 5;
    public static final int TX_POWER_LEVEL_0 = 0;
    public static final int TX_POWER_LEVEL_1 = 1;
    public static final int TX_POWER_LEVEL_2 = 2;
    public static final int TX_POWER_LEVEL_3 = 3;
    public static final int TX_POWER_LEVEL_4 = 4;
    private ActivityStatsTechSpecificInfo[] mActivityStatsTechSpecificInfo;
    private int mIdleTimeMs;
    private int mSizeOfSpecificInfo;
    private int mSleepTimeMs;
    private long mTimestamp;
    private int mTotalRxTimeMs;
    private int[] mTotalTxTimeMs;
    private static final Range<Integer>[] TX_POWER_RANGES = {new Range(Integer.MIN_VALUE, 0), new Range(0, 5), new Range(5, 15), new Range(15, 20), new Range(20, Integer.MAX_VALUE)};
    public static final Parcelable.Creator<ModemActivityInfo> CREATOR = new Parcelable.Creator<ModemActivityInfo>() { // from class: android.telephony.ModemActivityInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ModemActivityInfo createFromParcel(Parcel in) {
            long timestamp = in.readLong();
            int sleepTimeMs = in.readInt();
            int idleTimeMs = in.readInt();
            Parcelable[] tempSpecifiers = (Parcelable[]) in.createTypedArray(ActivityStatsTechSpecificInfo.CREATOR);
            ActivityStatsTechSpecificInfo[] activityStatsTechSpecificInfo = new ActivityStatsTechSpecificInfo[tempSpecifiers.length];
            for (int i = 0; i < tempSpecifiers.length; i++) {
                activityStatsTechSpecificInfo[i] = (ActivityStatsTechSpecificInfo) tempSpecifiers[i];
            }
            return new ModemActivityInfo(timestamp, sleepTimeMs, idleTimeMs, activityStatsTechSpecificInfo);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ModemActivityInfo[] newArray(int size) {
            return new ModemActivityInfo[size];
        }
    };

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface TxPowerLevel {
    }

    public static int getNumTxPowerLevels() {
        return 5;
    }

    public ModemActivityInfo(long timestamp, int sleepTimeMs, int idleTimeMs, int[] txTimeMs, int rxTimeMs) {
        Objects.requireNonNull(txTimeMs);
        if (txTimeMs.length != 5) {
            throw new IllegalArgumentException("txTimeMs must have length == TX_POWER_LEVELS");
        }
        this.mTimestamp = timestamp;
        this.mSleepTimeMs = sleepTimeMs;
        this.mIdleTimeMs = idleTimeMs;
        this.mTotalTxTimeMs = txTimeMs;
        this.mTotalRxTimeMs = rxTimeMs;
        this.mActivityStatsTechSpecificInfo = r0;
        this.mSizeOfSpecificInfo = r0.length;
        ActivityStatsTechSpecificInfo[] activityStatsTechSpecificInfoArr = {new ActivityStatsTechSpecificInfo(0, 0, txTimeMs, rxTimeMs)};
    }

    public ModemActivityInfo(long timestamp, long sleepTimeMs, long idleTimeMs, int[] txTimeMs, long rxTimeMs) {
        this(timestamp, (int) sleepTimeMs, (int) idleTimeMs, txTimeMs, (int) rxTimeMs);
    }

    public ModemActivityInfo(long timestamp, int sleepTimeMs, int idleTimeMs, ActivityStatsTechSpecificInfo[] activityStatsTechSpecificInfo) {
        this.mTimestamp = timestamp;
        this.mSleepTimeMs = sleepTimeMs;
        this.mIdleTimeMs = idleTimeMs;
        this.mActivityStatsTechSpecificInfo = activityStatsTechSpecificInfo;
        this.mSizeOfSpecificInfo = activityStatsTechSpecificInfo.length;
        this.mTotalTxTimeMs = new int[5];
        for (int i = 0; i < getNumTxPowerLevels(); i++) {
            for (int j = 0; j < getSpecificInfoLength(); j++) {
                int[] iArr = this.mTotalTxTimeMs;
                iArr[i] = iArr[i] + ((int) this.mActivityStatsTechSpecificInfo[j].getTransmitTimeMillis(i));
            }
        }
        this.mTotalRxTimeMs = 0;
        for (int i2 = 0; i2 < getSpecificInfoLength(); i2++) {
            this.mTotalRxTimeMs += (int) this.mActivityStatsTechSpecificInfo[i2].getReceiveTimeMillis();
        }
    }

    public ModemActivityInfo(long timestamp, long sleepTimeMs, long idleTimeMs, ActivityStatsTechSpecificInfo[] activityStatsTechSpecificInfo) {
        this(timestamp, (int) sleepTimeMs, (int) idleTimeMs, activityStatsTechSpecificInfo);
    }

    public String toString() {
        return "ModemActivityInfo{ mTimestamp=" + this.mTimestamp + " mSleepTimeMs=" + this.mSleepTimeMs + " mIdleTimeMs=" + this.mIdleTimeMs + " mActivityStatsTechSpecificInfo=" + Arrays.toString(this.mActivityStatsTechSpecificInfo) + "}";
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.mTimestamp);
        dest.writeInt(this.mSleepTimeMs);
        dest.writeInt(this.mIdleTimeMs);
        dest.writeTypedArray(this.mActivityStatsTechSpecificInfo, flags);
    }

    public long getTimestampMillis() {
        return this.mTimestamp;
    }

    public void setTimestamp(long timestamp) {
        this.mTimestamp = timestamp;
    }

    public long getTransmitDurationMillisAtPowerLevel(int powerLevel) {
        long txTimeMsAtPowerLevel = 0;
        for (int i = 0; i < getSpecificInfoLength(); i++) {
            txTimeMsAtPowerLevel += this.mActivityStatsTechSpecificInfo[i].getTransmitTimeMillis(powerLevel);
        }
        return txTimeMsAtPowerLevel;
    }

    public long getTransmitDurationMillisAtPowerLevel(int powerLevel, int rat) {
        for (int i = 0; i < getSpecificInfoLength(); i++) {
            if (this.mActivityStatsTechSpecificInfo[i].getRat() == rat) {
                return this.mActivityStatsTechSpecificInfo[i].getTransmitTimeMillis(powerLevel);
            }
        }
        return 0L;
    }

    public long getTransmitDurationMillisAtPowerLevel(int powerLevel, int rat, int freq) {
        for (int i = 0; i < getSpecificInfoLength(); i++) {
            if (this.mActivityStatsTechSpecificInfo[i].getRat() == rat && this.mActivityStatsTechSpecificInfo[i].getFrequencyRange() == freq) {
                return this.mActivityStatsTechSpecificInfo[i].getTransmitTimeMillis(powerLevel);
            }
        }
        return 0L;
    }

    public Range<Integer> getTransmitPowerRange(int powerLevel) {
        return TX_POWER_RANGES[powerLevel];
    }

    public int getSpecificInfoRat(int index) {
        return this.mActivityStatsTechSpecificInfo[index].getRat();
    }

    public int getSpecificInfoFrequencyRange(int index) {
        return this.mActivityStatsTechSpecificInfo[index].getFrequencyRange();
    }

    public void setTransmitTimeMillis(int[] txTimeMs) {
        this.mTotalTxTimeMs = Arrays.copyOf(txTimeMs, 5);
    }

    public void setTransmitTimeMillis(int rat, int[] txTimeMs) {
        for (int i = 0; i < getSpecificInfoLength(); i++) {
            if (this.mActivityStatsTechSpecificInfo[i].getRat() == rat) {
                this.mActivityStatsTechSpecificInfo[i].setTransmitTimeMillis(txTimeMs);
            }
        }
    }

    public void setTransmitTimeMillis(int rat, int freq, int[] txTimeMs) {
        for (int i = 0; i < getSpecificInfoLength(); i++) {
            if (this.mActivityStatsTechSpecificInfo[i].getRat() == rat && this.mActivityStatsTechSpecificInfo[i].getFrequencyRange() == freq) {
                this.mActivityStatsTechSpecificInfo[i].setTransmitTimeMillis(txTimeMs);
            }
        }
    }

    public int[] getTransmitTimeMillis() {
        return this.mTotalTxTimeMs;
    }

    public int[] getTransmitTimeMillis(int rat) {
        int i = 0;
        while (true) {
            ActivityStatsTechSpecificInfo[] activityStatsTechSpecificInfoArr = this.mActivityStatsTechSpecificInfo;
            if (i < activityStatsTechSpecificInfoArr.length) {
                if (activityStatsTechSpecificInfoArr[i].getRat() != rat) {
                    i++;
                } else {
                    return this.mActivityStatsTechSpecificInfo[i].getTransmitTimeMillis();
                }
            } else {
                return new int[5];
            }
        }
    }

    public int[] getTransmitTimeMillis(int rat, int freq) {
        int i = 0;
        while (true) {
            ActivityStatsTechSpecificInfo[] activityStatsTechSpecificInfoArr = this.mActivityStatsTechSpecificInfo;
            if (i < activityStatsTechSpecificInfoArr.length) {
                if (activityStatsTechSpecificInfoArr[i].getRat() != rat || this.mActivityStatsTechSpecificInfo[i].getFrequencyRange() != freq) {
                    i++;
                } else {
                    return this.mActivityStatsTechSpecificInfo[i].getTransmitTimeMillis();
                }
            } else {
                return new int[5];
            }
        }
    }

    public long getSleepTimeMillis() {
        return this.mSleepTimeMs;
    }

    public void setSleepTimeMillis(int sleepTimeMillis) {
        this.mSleepTimeMs = sleepTimeMillis;
    }

    public void setSleepTimeMillis(long sleepTimeMillis) {
        this.mSleepTimeMs = (int) sleepTimeMillis;
    }

    public ModemActivityInfo getDelta(ModemActivityInfo other) {
        ActivityStatsTechSpecificInfo[] mDeltaSpecificInfo = new ActivityStatsTechSpecificInfo[other.getSpecificInfoLength()];
        for (int i = 0; i < other.getSpecificInfoLength(); i++) {
            boolean matched = false;
            for (int j = 0; j < getSpecificInfoLength(); j++) {
                int rat = this.mActivityStatsTechSpecificInfo[j].getRat();
                if (rat == other.mActivityStatsTechSpecificInfo[i].getRat() && !matched) {
                    if (this.mActivityStatsTechSpecificInfo[j].getRat() == 6) {
                        if (other.mActivityStatsTechSpecificInfo[i].getFrequencyRange() == this.mActivityStatsTechSpecificInfo[j].getFrequencyRange()) {
                            int freq = this.mActivityStatsTechSpecificInfo[j].getFrequencyRange();
                            int[] txTimeMs = new int[5];
                            for (int lvl = 0; lvl < 5; lvl++) {
                                txTimeMs[lvl] = (int) (other.getTransmitDurationMillisAtPowerLevel(lvl, rat, freq) - getTransmitDurationMillisAtPowerLevel(lvl, rat, freq));
                            }
                            matched = true;
                            mDeltaSpecificInfo[i] = new ActivityStatsTechSpecificInfo(rat, freq, txTimeMs, (int) (other.getReceiveTimeMillis(rat, freq) - getReceiveTimeMillis(rat, freq)));
                        }
                    } else {
                        int[] txTimeMs2 = new int[5];
                        for (int lvl2 = 0; lvl2 < 5; lvl2++) {
                            txTimeMs2[lvl2] = (int) (other.getTransmitDurationMillisAtPowerLevel(lvl2, rat) - getTransmitDurationMillisAtPowerLevel(lvl2, rat));
                        }
                        matched = true;
                        mDeltaSpecificInfo[i] = new ActivityStatsTechSpecificInfo(rat, 0, txTimeMs2, (int) (other.getReceiveTimeMillis(rat) - getReceiveTimeMillis(rat)));
                    }
                }
            }
            if (!matched) {
                mDeltaSpecificInfo[i] = other.mActivityStatsTechSpecificInfo[i];
            }
        }
        return new ModemActivityInfo(other.getTimestampMillis(), other.getSleepTimeMillis() - getSleepTimeMillis(), other.getIdleTimeMillis() - getIdleTimeMillis(), mDeltaSpecificInfo);
    }

    public long getIdleTimeMillis() {
        return this.mIdleTimeMs;
    }

    public void setIdleTimeMillis(int idleTimeMillis) {
        this.mIdleTimeMs = idleTimeMillis;
    }

    public void setIdleTimeMillis(long idleTimeMillis) {
        this.mIdleTimeMs = (int) idleTimeMillis;
    }

    public long getReceiveTimeMillis() {
        return this.mTotalRxTimeMs;
    }

    public long getReceiveTimeMillis(int rat) {
        int i = 0;
        while (true) {
            ActivityStatsTechSpecificInfo[] activityStatsTechSpecificInfoArr = this.mActivityStatsTechSpecificInfo;
            if (i < activityStatsTechSpecificInfoArr.length) {
                if (activityStatsTechSpecificInfoArr[i].getRat() != rat) {
                    i++;
                } else {
                    return this.mActivityStatsTechSpecificInfo[i].getReceiveTimeMillis();
                }
            } else {
                return 0L;
            }
        }
    }

    public long getReceiveTimeMillis(int rat, int freq) {
        int i = 0;
        while (true) {
            ActivityStatsTechSpecificInfo[] activityStatsTechSpecificInfoArr = this.mActivityStatsTechSpecificInfo;
            if (i < activityStatsTechSpecificInfoArr.length) {
                if (activityStatsTechSpecificInfoArr[i].getRat() != rat || this.mActivityStatsTechSpecificInfo[i].getFrequencyRange() != freq) {
                    i++;
                } else {
                    return this.mActivityStatsTechSpecificInfo[i].getReceiveTimeMillis();
                }
            } else {
                return 0L;
            }
        }
    }

    public void setReceiveTimeMillis(int rxTimeMillis) {
        this.mTotalRxTimeMs = rxTimeMillis;
    }

    public void setReceiveTimeMillis(long receiveTimeMillis) {
        this.mTotalRxTimeMs = (int) receiveTimeMillis;
    }

    public void setReceiveTimeMillis(int rat, long receiveTimeMillis) {
        int i = 0;
        while (true) {
            ActivityStatsTechSpecificInfo[] activityStatsTechSpecificInfoArr = this.mActivityStatsTechSpecificInfo;
            if (i < activityStatsTechSpecificInfoArr.length) {
                if (activityStatsTechSpecificInfoArr[i].getRat() == rat) {
                    this.mActivityStatsTechSpecificInfo[i].setReceiveTimeMillis(receiveTimeMillis);
                }
                i++;
            } else {
                return;
            }
        }
    }

    public void setReceiveTimeMillis(int rat, int freq, long receiveTimeMillis) {
        int i = 0;
        while (true) {
            ActivityStatsTechSpecificInfo[] activityStatsTechSpecificInfoArr = this.mActivityStatsTechSpecificInfo;
            if (i < activityStatsTechSpecificInfoArr.length) {
                if (activityStatsTechSpecificInfoArr[i].getRat() == rat && this.mActivityStatsTechSpecificInfo[i].getFrequencyRange() == freq) {
                    this.mActivityStatsTechSpecificInfo[i].setReceiveTimeMillis(receiveTimeMillis);
                }
                i++;
            } else {
                return;
            }
        }
    }

    public int getSpecificInfoLength() {
        return this.mSizeOfSpecificInfo;
    }

    public boolean isValid() {
        if (this.mActivityStatsTechSpecificInfo == null) {
            return false;
        }
        boolean isTxPowerValid = true;
        boolean isRxPowerValid = true;
        for (int i = 0; i < getSpecificInfoLength(); i++) {
            if (!this.mActivityStatsTechSpecificInfo[i].isTxPowerValid()) {
                isTxPowerValid = false;
            }
            if (!this.mActivityStatsTechSpecificInfo[i].isRxPowerValid()) {
                isRxPowerValid = false;
            }
        }
        return isTxPowerValid && isRxPowerValid && getIdleTimeMillis() >= 0 && getSleepTimeMillis() >= 0 && !isEmpty();
    }

    public boolean isEmpty() {
        boolean isTxPowerEmpty = true;
        boolean isRxPowerEmpty = true;
        for (int i = 0; i < getSpecificInfoLength(); i++) {
            if (!this.mActivityStatsTechSpecificInfo[i].isTxPowerEmpty()) {
                isTxPowerEmpty = false;
            }
            if (!this.mActivityStatsTechSpecificInfo[i].isRxPowerEmpty()) {
                isRxPowerEmpty = false;
            }
        }
        return isTxPowerEmpty && getIdleTimeMillis() == 0 && getSleepTimeMillis() == 0 && isRxPowerEmpty;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ModemActivityInfo that = (ModemActivityInfo) o;
        if (this.mTimestamp == that.mTimestamp && this.mSleepTimeMs == that.mSleepTimeMs && this.mIdleTimeMs == that.mIdleTimeMs && this.mSizeOfSpecificInfo == that.mSizeOfSpecificInfo && Arrays.equals(this.mActivityStatsTechSpecificInfo, that.mActivityStatsTechSpecificInfo)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        int result = Objects.hash(Long.valueOf(this.mTimestamp), Integer.valueOf(this.mSleepTimeMs), Integer.valueOf(this.mIdleTimeMs), Integer.valueOf(this.mTotalRxTimeMs));
        return (result * 31) + Arrays.hashCode(this.mTotalTxTimeMs);
    }
}
