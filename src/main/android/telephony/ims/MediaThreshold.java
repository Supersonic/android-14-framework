package android.telephony.ims;

import android.annotation.SystemApi;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;
import java.util.TreeSet;
@SystemApi
/* loaded from: classes3.dex */
public final class MediaThreshold implements Parcelable {
    public static final Parcelable.Creator<MediaThreshold> CREATOR = new Parcelable.Creator<MediaThreshold>() { // from class: android.telephony.ims.MediaThreshold.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public MediaThreshold createFromParcel(Parcel in) {
            return new MediaThreshold(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public MediaThreshold[] newArray(int size) {
            return new MediaThreshold[size];
        }
    };
    private final long[] mRtpInactivityTimeMillis;
    private final int[] mRtpJitter;
    private final int[] mRtpPacketLossRate;

    @SystemApi
    public int[] getThresholdsRtpPacketLossRate() {
        return this.mRtpPacketLossRate;
    }

    public int[] getThresholdsRtpJitterMillis() {
        return this.mRtpJitter;
    }

    public long[] getThresholdsRtpInactivityTimeMillis() {
        return this.mRtpInactivityTimeMillis;
    }

    private MediaThreshold(int[] packetLossRateThresholds, int[] jitterThresholds, long[] inactivityTimeThresholds) {
        this.mRtpPacketLossRate = packetLossRateThresholds;
        this.mRtpJitter = jitterThresholds;
        this.mRtpInactivityTimeMillis = inactivityTimeThresholds;
    }

    private MediaThreshold(Parcel in) {
        this.mRtpPacketLossRate = in.createIntArray();
        this.mRtpJitter = in.createIntArray();
        this.mRtpInactivityTimeMillis = in.createLongArray();
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeIntArray(this.mRtpPacketLossRate);
        dest.writeIntArray(this.mRtpJitter);
        dest.writeLongArray(this.mRtpInactivityTimeMillis);
    }

    public static boolean isValidRtpPacketLossRate(int packetLossRate) {
        return packetLossRate >= 0 && packetLossRate <= 100;
    }

    public static boolean isValidJitterMillis(int jitter) {
        return jitter >= 0 && jitter <= 10000;
    }

    public static boolean isValidRtpInactivityTimeMillis(long inactivityTime) {
        return inactivityTime >= 0 && inactivityTime <= 60000;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MediaThreshold that = (MediaThreshold) o;
        if (Arrays.equals(this.mRtpPacketLossRate, that.mRtpPacketLossRate) && Arrays.equals(this.mRtpJitter, that.mRtpJitter) && Arrays.equals(this.mRtpInactivityTimeMillis, that.mRtpInactivityTimeMillis)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(Integer.valueOf(Arrays.hashCode(this.mRtpPacketLossRate)), Integer.valueOf(Arrays.hashCode(this.mRtpJitter)), Integer.valueOf(Arrays.hashCode(this.mRtpInactivityTimeMillis)));
    }

    public String toString() {
        int[] iArr;
        int[] iArr2;
        long[] jArr;
        StringBuilder sb = new StringBuilder();
        sb.append("MediaThreshold{mRtpPacketLossRate=");
        for (int i : this.mRtpPacketLossRate) {
            sb.append(" ").append(i);
        }
        sb.append(", mRtpJitter=");
        for (int b : this.mRtpJitter) {
            sb.append(" ").append(b);
        }
        sb.append(", mRtpInactivityTimeMillis=");
        for (long i2 : this.mRtpInactivityTimeMillis) {
            sb.append(" ").append(i2);
        }
        sb.append("}");
        return sb.toString();
    }

    /* loaded from: classes3.dex */
    public static final class Builder {
        private int[] mRtpPacketLossRate = null;
        private int[] mRtpJitter = null;
        private long[] mRtpInactivityTimeMillis = null;

        public Builder setThresholdsRtpPacketLossRate(int[] packetLossRateThresholds) {
            if (packetLossRateThresholds.length > 0) {
                TreeSet<Integer> thresholds = new TreeSet<>();
                for (int i : packetLossRateThresholds) {
                    Integer value = Integer.valueOf(i);
                    if (MediaThreshold.isValidRtpPacketLossRate(value.intValue())) {
                        thresholds.add(value);
                    }
                }
                int[] targetArray = new int[thresholds.size()];
                int i2 = 0;
                Iterator<Integer> it = thresholds.iterator();
                while (it.hasNext()) {
                    int element = it.next().intValue();
                    targetArray[i2] = element;
                    i2++;
                }
                this.mRtpPacketLossRate = targetArray;
            } else {
                this.mRtpPacketLossRate = packetLossRateThresholds;
            }
            return this;
        }

        public Builder setThresholdsRtpJitterMillis(int[] jitterThresholds) {
            if (jitterThresholds.length > 0) {
                TreeSet<Integer> thresholds = new TreeSet<>();
                for (int i : jitterThresholds) {
                    Integer value = Integer.valueOf(i);
                    if (MediaThreshold.isValidJitterMillis(value.intValue())) {
                        thresholds.add(value);
                    }
                }
                int[] targetArray = new int[thresholds.size()];
                int i2 = 0;
                Iterator<Integer> it = thresholds.iterator();
                while (it.hasNext()) {
                    int element = it.next().intValue();
                    targetArray[i2] = element;
                    i2++;
                }
                this.mRtpJitter = targetArray;
            } else {
                this.mRtpJitter = jitterThresholds;
            }
            return this;
        }

        public Builder setThresholdsRtpInactivityTimeMillis(long[] inactivityTimeThresholds) {
            if (inactivityTimeThresholds.length > 0) {
                TreeSet<Long> thresholds = new TreeSet<>();
                for (long j : inactivityTimeThresholds) {
                    Long value = Long.valueOf(j);
                    if (MediaThreshold.isValidRtpInactivityTimeMillis(value.longValue())) {
                        thresholds.add(value);
                    }
                }
                long[] targetArray = new long[thresholds.size()];
                int i = 0;
                Iterator<Long> it = thresholds.iterator();
                while (it.hasNext()) {
                    long element = it.next().longValue();
                    targetArray[i] = element;
                    i++;
                }
                this.mRtpInactivityTimeMillis = targetArray;
            } else {
                this.mRtpInactivityTimeMillis = inactivityTimeThresholds;
            }
            return this;
        }

        public MediaThreshold build() {
            int[] iArr = this.mRtpPacketLossRate;
            if (iArr == null) {
                iArr = new int[0];
            }
            this.mRtpPacketLossRate = iArr;
            int[] iArr2 = this.mRtpJitter;
            if (iArr2 == null) {
                iArr2 = new int[0];
            }
            this.mRtpJitter = iArr2;
            long[] jArr = this.mRtpInactivityTimeMillis;
            if (jArr == null) {
                jArr = new long[0];
            }
            this.mRtpInactivityTimeMillis = jArr;
            return new MediaThreshold(this.mRtpPacketLossRate, this.mRtpJitter, this.mRtpInactivityTimeMillis);
        }
    }
}
