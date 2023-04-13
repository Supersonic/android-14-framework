package android.telephony;

import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.IntPredicate;
/* loaded from: classes3.dex */
public final class ActivityStatsTechSpecificInfo implements Parcelable {
    public static final Parcelable.Creator<ActivityStatsTechSpecificInfo> CREATOR = new Parcelable.Creator<ActivityStatsTechSpecificInfo>() { // from class: android.telephony.ActivityStatsTechSpecificInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ActivityStatsTechSpecificInfo createFromParcel(Parcel in) {
            int rat = in.readInt();
            int frequencyRange = in.readInt();
            int[] txTimeMs = new int[5];
            in.readIntArray(txTimeMs);
            int rxTimeMs = in.readInt();
            return new ActivityStatsTechSpecificInfo(rat, frequencyRange, txTimeMs, rxTimeMs);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ActivityStatsTechSpecificInfo[] newArray(int size) {
            return new ActivityStatsTechSpecificInfo[size];
        }
    };
    private static final int TX_POWER_LEVELS = 5;
    private int mFrequencyRange;
    private int mRat;
    private int mRxTimeMs;
    private int[] mTxTimeMs;

    public ActivityStatsTechSpecificInfo(int rat, int frequencyRange, int[] txTimeMs, int rxTimeMs) {
        Objects.requireNonNull(txTimeMs);
        if (txTimeMs.length != 5) {
            throw new IllegalArgumentException("txTimeMs must have length == TX_POWER_LEVELS");
        }
        this.mRat = rat;
        this.mFrequencyRange = frequencyRange;
        this.mTxTimeMs = txTimeMs;
        this.mRxTimeMs = rxTimeMs;
    }

    public int getRat() {
        return this.mRat;
    }

    public int getFrequencyRange() {
        return this.mFrequencyRange;
    }

    public long getTransmitTimeMillis(int powerLevel) {
        return this.mTxTimeMs[powerLevel];
    }

    public int[] getTransmitTimeMillis() {
        return this.mTxTimeMs;
    }

    public long getReceiveTimeMillis() {
        return this.mRxTimeMs;
    }

    public void setRat(int rat) {
        this.mRat = rat;
    }

    public void setFrequencyRange(int frequencyRange) {
        this.mFrequencyRange = frequencyRange;
    }

    public void setReceiveTimeMillis(int receiveTimeMillis) {
        this.mRxTimeMs = receiveTimeMillis;
    }

    public void setReceiveTimeMillis(long receiveTimeMillis) {
        this.mRxTimeMs = (int) receiveTimeMillis;
    }

    public void setTransmitTimeMillis(int[] txTimeMs) {
        this.mTxTimeMs = Arrays.copyOf(txTimeMs, 5);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ boolean lambda$isTxPowerValid$0(int i) {
        return i >= 0;
    }

    public boolean isTxPowerValid() {
        return Arrays.stream(this.mTxTimeMs).allMatch(new IntPredicate() { // from class: android.telephony.ActivityStatsTechSpecificInfo$$ExternalSyntheticLambda0
            @Override // java.util.function.IntPredicate
            public final boolean test(int i) {
                return ActivityStatsTechSpecificInfo.lambda$isTxPowerValid$0(i);
            }
        });
    }

    public boolean isRxPowerValid() {
        return getReceiveTimeMillis() >= 0;
    }

    public boolean isTxPowerEmpty() {
        int[] iArr = this.mTxTimeMs;
        return iArr == null || iArr.length == 0 || Arrays.stream(iArr).allMatch(new IntPredicate() { // from class: android.telephony.ActivityStatsTechSpecificInfo$$ExternalSyntheticLambda1
            @Override // java.util.function.IntPredicate
            public final boolean test(int i) {
                return ActivityStatsTechSpecificInfo.lambda$isTxPowerEmpty$1(i);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ boolean lambda$isTxPowerEmpty$1(int i) {
        return i == 0;
    }

    public boolean isRxPowerEmpty() {
        return getReceiveTimeMillis() == 0;
    }

    public int hashCode() {
        int result = Objects.hash(Integer.valueOf(this.mRat), Integer.valueOf(this.mFrequencyRange), Integer.valueOf(this.mRxTimeMs));
        return (result * 31) + Arrays.hashCode(this.mTxTimeMs);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof ActivityStatsTechSpecificInfo) {
            ActivityStatsTechSpecificInfo that = (ActivityStatsTechSpecificInfo) o;
            return this.mRat == that.mRat && this.mFrequencyRange == that.mFrequencyRange && Arrays.equals(this.mTxTimeMs, that.mTxTimeMs) && this.mRxTimeMs == that.mRxTimeMs;
        }
        return false;
    }

    private static String ratToString(int type) {
        switch (type) {
            case 0:
                return "UNKNOWN";
            case 1:
                return "GERAN";
            case 2:
                return "UTRAN";
            case 3:
                return "EUTRAN";
            case 4:
                return "CDMA2000";
            case 5:
                return "IWLAN";
            case 6:
                return "NGRAN";
            default:
                return Integer.toString(type);
        }
    }

    public String toString() {
        return "{mRat=" + ratToString(this.mRat) + ",mFrequencyRange=" + ServiceState.frequencyRangeToString(this.mFrequencyRange) + ",mTxTimeMs[]=" + Arrays.toString(this.mTxTimeMs) + ",mRxTimeMs=" + this.mRxTimeMs + "}";
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mRat);
        dest.writeInt(this.mFrequencyRange);
        dest.writeIntArray(this.mTxTimeMs);
        dest.writeInt(this.mRxTimeMs);
    }
}
