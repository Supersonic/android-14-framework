package android.telephony;

import android.annotation.SystemApi;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Objects;
@SystemApi
/* loaded from: classes3.dex */
public final class CallQuality implements Parcelable {
    public static final int CALL_QUALITY_BAD = 4;
    public static final int CALL_QUALITY_EXCELLENT = 0;
    public static final int CALL_QUALITY_FAIR = 2;
    public static final int CALL_QUALITY_GOOD = 1;
    public static final int CALL_QUALITY_NOT_AVAILABLE = 5;
    public static final int CALL_QUALITY_POOR = 3;
    public static final Parcelable.Creator<CallQuality> CREATOR = new Parcelable.Creator() { // from class: android.telephony.CallQuality.1
        @Override // android.p008os.Parcelable.Creator
        public CallQuality createFromParcel(Parcel in) {
            return new CallQuality(in);
        }

        @Override // android.p008os.Parcelable.Creator
        public CallQuality[] newArray(int size) {
            return new CallQuality[size];
        }
    };
    private int mAverageRelativeJitter;
    private int mAverageRoundTripTime;
    private int mCallDuration;
    private int mCodecType;
    private int mDownlinkCallQualityLevel;
    private long mMaxPlayoutDelayMillis;
    private int mMaxRelativeJitter;
    private long mMinPlayoutDelayMillis;
    private int mNumDroppedRtpPackets;
    private int mNumNoDataFrames;
    private int mNumRtpDuplicatePackets;
    private int mNumRtpPacketsNotReceived;
    private int mNumRtpPacketsReceived;
    private int mNumRtpPacketsTransmitted;
    private int mNumRtpPacketsTransmittedLost;
    private int mNumRtpSidPacketsReceived;
    private int mNumVoiceFrames;
    private boolean mRtpInactivityDetected;
    private boolean mRxSilenceDetected;
    private boolean mTxSilenceDetected;
    private int mUplinkCallQualityLevel;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface CallQualityLevel {
    }

    public CallQuality(Parcel in) {
        this.mDownlinkCallQualityLevel = in.readInt();
        this.mUplinkCallQualityLevel = in.readInt();
        this.mCallDuration = in.readInt();
        this.mNumRtpPacketsTransmitted = in.readInt();
        this.mNumRtpPacketsReceived = in.readInt();
        this.mNumRtpPacketsTransmittedLost = in.readInt();
        this.mNumRtpPacketsNotReceived = in.readInt();
        this.mAverageRelativeJitter = in.readInt();
        this.mMaxRelativeJitter = in.readInt();
        this.mAverageRoundTripTime = in.readInt();
        this.mCodecType = in.readInt();
        this.mRtpInactivityDetected = in.readBoolean();
        this.mRxSilenceDetected = in.readBoolean();
        this.mTxSilenceDetected = in.readBoolean();
        this.mNumVoiceFrames = in.readInt();
        this.mNumNoDataFrames = in.readInt();
        this.mNumDroppedRtpPackets = in.readInt();
        this.mMinPlayoutDelayMillis = in.readLong();
        this.mMaxPlayoutDelayMillis = in.readLong();
        this.mNumRtpSidPacketsReceived = in.readInt();
        this.mNumRtpDuplicatePackets = in.readInt();
    }

    public CallQuality() {
    }

    public CallQuality(int downlinkCallQualityLevel, int uplinkCallQualityLevel, int callDuration, int numRtpPacketsTransmitted, int numRtpPacketsReceived, int numRtpPacketsTransmittedLost, int numRtpPacketsNotReceived, int averageRelativeJitter, int maxRelativeJitter, int averageRoundTripTime, int codecType) {
        this(downlinkCallQualityLevel, uplinkCallQualityLevel, callDuration, numRtpPacketsTransmitted, numRtpPacketsReceived, numRtpPacketsTransmittedLost, numRtpPacketsNotReceived, averageRelativeJitter, maxRelativeJitter, averageRoundTripTime, codecType, false, false, false);
    }

    public CallQuality(int downlinkCallQualityLevel, int uplinkCallQualityLevel, int callDuration, int numRtpPacketsTransmitted, int numRtpPacketsReceived, int numRtpPacketsTransmittedLost, int numRtpPacketsNotReceived, int averageRelativeJitter, int maxRelativeJitter, int averageRoundTripTime, int codecType, boolean rtpInactivityDetected, boolean rxSilenceDetected, boolean txSilenceDetected) {
        this.mDownlinkCallQualityLevel = downlinkCallQualityLevel;
        this.mUplinkCallQualityLevel = uplinkCallQualityLevel;
        this.mCallDuration = callDuration;
        this.mNumRtpPacketsTransmitted = numRtpPacketsTransmitted;
        this.mNumRtpPacketsReceived = numRtpPacketsReceived;
        this.mNumRtpPacketsTransmittedLost = numRtpPacketsTransmittedLost;
        this.mNumRtpPacketsNotReceived = numRtpPacketsNotReceived;
        this.mAverageRelativeJitter = averageRelativeJitter;
        this.mMaxRelativeJitter = maxRelativeJitter;
        this.mAverageRoundTripTime = averageRoundTripTime;
        this.mCodecType = codecType;
        this.mRtpInactivityDetected = rtpInactivityDetected;
        this.mRxSilenceDetected = rxSilenceDetected;
        this.mTxSilenceDetected = txSilenceDetected;
    }

    public int getDownlinkCallQualityLevel() {
        return this.mDownlinkCallQualityLevel;
    }

    public int getUplinkCallQualityLevel() {
        return this.mUplinkCallQualityLevel;
    }

    public int getCallDuration() {
        return this.mCallDuration;
    }

    public int getNumRtpPacketsTransmitted() {
        return this.mNumRtpPacketsTransmitted;
    }

    public int getNumRtpPacketsReceived() {
        return this.mNumRtpPacketsReceived;
    }

    public int getNumRtpPacketsTransmittedLost() {
        return this.mNumRtpPacketsTransmittedLost;
    }

    public int getNumRtpPacketsNotReceived() {
        return this.mNumRtpPacketsNotReceived;
    }

    public int getAverageRelativeJitter() {
        return this.mAverageRelativeJitter;
    }

    public int getMaxRelativeJitter() {
        return this.mMaxRelativeJitter;
    }

    public int getAverageRoundTripTime() {
        return this.mAverageRoundTripTime;
    }

    public boolean isRtpInactivityDetected() {
        return this.mRtpInactivityDetected;
    }

    public boolean isIncomingSilenceDetectedAtCallSetup() {
        return this.mRxSilenceDetected;
    }

    public boolean isOutgoingSilenceDetectedAtCallSetup() {
        return this.mTxSilenceDetected;
    }

    public int getNumVoiceFrames() {
        return this.mNumVoiceFrames;
    }

    public int getNumNoDataFrames() {
        return this.mNumNoDataFrames;
    }

    public int getNumDroppedRtpPackets() {
        return this.mNumDroppedRtpPackets;
    }

    public long getMinPlayoutDelayMillis() {
        return this.mMinPlayoutDelayMillis;
    }

    public long getMaxPlayoutDelayMillis() {
        return this.mMaxPlayoutDelayMillis;
    }

    public int getNumRtpSidPacketsReceived() {
        return this.mNumRtpSidPacketsReceived;
    }

    public int getNumRtpDuplicatePackets() {
        return this.mNumRtpDuplicatePackets;
    }

    public int getCodecType() {
        return this.mCodecType;
    }

    public String toString() {
        return "CallQuality: {downlinkCallQualityLevel=" + this.mDownlinkCallQualityLevel + " uplinkCallQualityLevel=" + this.mUplinkCallQualityLevel + " callDuration=" + this.mCallDuration + " numRtpPacketsTransmitted=" + this.mNumRtpPacketsTransmitted + " numRtpPacketsReceived=" + this.mNumRtpPacketsReceived + " numRtpPacketsTransmittedLost=" + this.mNumRtpPacketsTransmittedLost + " numRtpPacketsNotReceived=" + this.mNumRtpPacketsNotReceived + " averageRelativeJitter=" + this.mAverageRelativeJitter + " maxRelativeJitter=" + this.mMaxRelativeJitter + " averageRoundTripTime=" + this.mAverageRoundTripTime + " codecType=" + this.mCodecType + " rtpInactivityDetected=" + this.mRtpInactivityDetected + " txSilenceDetected=" + this.mTxSilenceDetected + " rxSilenceDetected=" + this.mRxSilenceDetected + " numVoiceFrames=" + this.mNumVoiceFrames + " numNoDataFrames=" + this.mNumNoDataFrames + " numDroppedRtpPackets=" + this.mNumDroppedRtpPackets + " minPlayoutDelayMillis=" + this.mMinPlayoutDelayMillis + " maxPlayoutDelayMillis=" + this.mMaxPlayoutDelayMillis + " numRtpSidPacketsReceived=" + this.mNumRtpSidPacketsReceived + " numRtpDuplicatePackets=" + this.mNumRtpDuplicatePackets + "}";
    }

    public int hashCode() {
        return Objects.hash(Integer.valueOf(this.mDownlinkCallQualityLevel), Integer.valueOf(this.mUplinkCallQualityLevel), Integer.valueOf(this.mCallDuration), Integer.valueOf(this.mNumRtpPacketsTransmitted), Integer.valueOf(this.mNumRtpPacketsReceived), Integer.valueOf(this.mNumRtpPacketsTransmittedLost), Integer.valueOf(this.mNumRtpPacketsNotReceived), Integer.valueOf(this.mAverageRelativeJitter), Integer.valueOf(this.mMaxRelativeJitter), Integer.valueOf(this.mAverageRoundTripTime), Integer.valueOf(this.mCodecType), Boolean.valueOf(this.mRtpInactivityDetected), Boolean.valueOf(this.mRxSilenceDetected), Boolean.valueOf(this.mTxSilenceDetected), Integer.valueOf(this.mNumVoiceFrames), Integer.valueOf(this.mNumNoDataFrames), Integer.valueOf(this.mNumDroppedRtpPackets), Long.valueOf(this.mMinPlayoutDelayMillis), Long.valueOf(this.mMaxPlayoutDelayMillis), Integer.valueOf(this.mNumRtpSidPacketsReceived), Integer.valueOf(this.mNumRtpDuplicatePackets));
    }

    public boolean equals(Object o) {
        if (o != null && (o instanceof CallQuality) && hashCode() == o.hashCode()) {
            if (this == o) {
                return true;
            }
            CallQuality s = (CallQuality) o;
            if (this.mDownlinkCallQualityLevel != s.mDownlinkCallQualityLevel || this.mUplinkCallQualityLevel != s.mUplinkCallQualityLevel || this.mCallDuration != s.mCallDuration || this.mNumRtpPacketsTransmitted != s.mNumRtpPacketsTransmitted || this.mNumRtpPacketsReceived != s.mNumRtpPacketsReceived || this.mNumRtpPacketsTransmittedLost != s.mNumRtpPacketsTransmittedLost || this.mNumRtpPacketsNotReceived != s.mNumRtpPacketsNotReceived || this.mAverageRelativeJitter != s.mAverageRelativeJitter || this.mMaxRelativeJitter != s.mMaxRelativeJitter || this.mAverageRoundTripTime != s.mAverageRoundTripTime || this.mCodecType != s.mCodecType || this.mRtpInactivityDetected != s.mRtpInactivityDetected || this.mRxSilenceDetected != s.mRxSilenceDetected || this.mTxSilenceDetected != s.mTxSilenceDetected || this.mNumVoiceFrames != s.mNumVoiceFrames || this.mNumNoDataFrames != s.mNumNoDataFrames || this.mNumDroppedRtpPackets != s.mNumDroppedRtpPackets || this.mMinPlayoutDelayMillis != s.mMinPlayoutDelayMillis || this.mMaxPlayoutDelayMillis != s.mMaxPlayoutDelayMillis || this.mNumRtpSidPacketsReceived != s.mNumRtpSidPacketsReceived || this.mNumRtpDuplicatePackets != s.mNumRtpDuplicatePackets) {
                return false;
            }
            return true;
        }
        return false;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mDownlinkCallQualityLevel);
        dest.writeInt(this.mUplinkCallQualityLevel);
        dest.writeInt(this.mCallDuration);
        dest.writeInt(this.mNumRtpPacketsTransmitted);
        dest.writeInt(this.mNumRtpPacketsReceived);
        dest.writeInt(this.mNumRtpPacketsTransmittedLost);
        dest.writeInt(this.mNumRtpPacketsNotReceived);
        dest.writeInt(this.mAverageRelativeJitter);
        dest.writeInt(this.mMaxRelativeJitter);
        dest.writeInt(this.mAverageRoundTripTime);
        dest.writeInt(this.mCodecType);
        dest.writeBoolean(this.mRtpInactivityDetected);
        dest.writeBoolean(this.mRxSilenceDetected);
        dest.writeBoolean(this.mTxSilenceDetected);
        dest.writeInt(this.mNumVoiceFrames);
        dest.writeInt(this.mNumNoDataFrames);
        dest.writeInt(this.mNumDroppedRtpPackets);
        dest.writeLong(this.mMinPlayoutDelayMillis);
        dest.writeLong(this.mMaxPlayoutDelayMillis);
        dest.writeInt(this.mNumRtpSidPacketsReceived);
        dest.writeInt(this.mNumRtpDuplicatePackets);
    }

    /* loaded from: classes3.dex */
    public static final class Builder {
        private int mAverageRelativeJitter;
        private int mAverageRoundTripTime;
        private int mCallDuration;
        private int mCodecType;
        private int mDownlinkCallQualityLevel;
        private long mMaxPlayoutDelayMillis;
        private int mMaxRelativeJitter;
        private long mMinPlayoutDelayMillis;
        private int mNumDroppedRtpPackets;
        private int mNumNoDataFrames;
        private int mNumRtpDuplicatePackets;
        private int mNumRtpPacketsNotReceived;
        private int mNumRtpPacketsReceived;
        private int mNumRtpPacketsTransmitted;
        private int mNumRtpPacketsTransmittedLost;
        private int mNumRtpSidPacketsReceived;
        private int mNumVoiceFrames;
        private boolean mRtpInactivityDetected;
        private boolean mRxSilenceDetected;
        private boolean mTxSilenceDetected;
        private int mUplinkCallQualityLevel;

        public Builder setDownlinkCallQualityLevel(int downlinkCallQualityLevel) {
            this.mDownlinkCallQualityLevel = downlinkCallQualityLevel;
            return this;
        }

        public Builder setUplinkCallQualityLevel(int uplinkCallQualityLevel) {
            this.mUplinkCallQualityLevel = uplinkCallQualityLevel;
            return this;
        }

        public Builder setCallDurationMillis(int callDurationMillis) {
            this.mCallDuration = callDurationMillis;
            return this;
        }

        public Builder setNumRtpPacketsTransmitted(int numRtpPacketsTransmitted) {
            this.mNumRtpPacketsTransmitted = numRtpPacketsTransmitted;
            return this;
        }

        public Builder setNumRtpPacketsReceived(int numRtpPacketsReceived) {
            this.mNumRtpPacketsReceived = numRtpPacketsReceived;
            return this;
        }

        public Builder setNumRtpPacketsTransmittedLost(int numRtpPacketsTransmittedLost) {
            this.mNumRtpPacketsTransmittedLost = numRtpPacketsTransmittedLost;
            return this;
        }

        public Builder setNumRtpPacketsNotReceived(int numRtpPacketsNotReceived) {
            this.mNumRtpPacketsNotReceived = numRtpPacketsNotReceived;
            return this;
        }

        public Builder setAverageRelativeJitter(int averageRelativeJitter) {
            this.mAverageRelativeJitter = averageRelativeJitter;
            return this;
        }

        public Builder setMaxRelativeJitter(int maxRelativeJitter) {
            this.mMaxRelativeJitter = maxRelativeJitter;
            return this;
        }

        public Builder setAverageRoundTripTimeMillis(int averageRoundTripTimeMillis) {
            this.mAverageRoundTripTime = averageRoundTripTimeMillis;
            return this;
        }

        public Builder setCodecType(int codecType) {
            this.mCodecType = codecType;
            return this;
        }

        public Builder setRtpInactivityDetected(boolean rtpInactivityDetected) {
            this.mRtpInactivityDetected = rtpInactivityDetected;
            return this;
        }

        public Builder setIncomingSilenceDetectedAtCallSetup(boolean rxSilenceDetected) {
            this.mRxSilenceDetected = rxSilenceDetected;
            return this;
        }

        public Builder setOutgoingSilenceDetectedAtCallSetup(boolean txSilenceDetected) {
            this.mTxSilenceDetected = txSilenceDetected;
            return this;
        }

        public Builder setNumVoiceFrames(int numVoiceFrames) {
            this.mNumVoiceFrames = numVoiceFrames;
            return this;
        }

        public Builder setNumNoDataFrames(int numNoDataFrames) {
            this.mNumNoDataFrames = numNoDataFrames;
            return this;
        }

        public Builder setNumDroppedRtpPackets(int numDroppedRtpPackets) {
            this.mNumDroppedRtpPackets = numDroppedRtpPackets;
            return this;
        }

        public Builder setMinPlayoutDelayMillis(long minPlayoutDelayMillis) {
            this.mMinPlayoutDelayMillis = minPlayoutDelayMillis;
            return this;
        }

        public Builder setMaxPlayoutDelayMillis(long maxPlayoutDelayMillis) {
            this.mMaxPlayoutDelayMillis = maxPlayoutDelayMillis;
            return this;
        }

        public Builder setNumRtpSidPacketsReceived(int numRtpSidPacketsReceived) {
            this.mNumRtpSidPacketsReceived = numRtpSidPacketsReceived;
            return this;
        }

        public Builder setNumRtpDuplicatePackets(int numRtpDuplicatePackets) {
            this.mNumRtpDuplicatePackets = numRtpDuplicatePackets;
            return this;
        }

        public CallQuality build() {
            CallQuality callQuality = new CallQuality();
            callQuality.mDownlinkCallQualityLevel = this.mDownlinkCallQualityLevel;
            callQuality.mUplinkCallQualityLevel = this.mUplinkCallQualityLevel;
            callQuality.mCallDuration = this.mCallDuration;
            callQuality.mNumRtpPacketsTransmitted = this.mNumRtpPacketsTransmitted;
            callQuality.mNumRtpPacketsReceived = this.mNumRtpPacketsReceived;
            callQuality.mNumRtpPacketsTransmittedLost = this.mNumRtpPacketsTransmittedLost;
            callQuality.mNumRtpPacketsNotReceived = this.mNumRtpPacketsNotReceived;
            callQuality.mAverageRelativeJitter = this.mAverageRelativeJitter;
            callQuality.mMaxRelativeJitter = this.mMaxRelativeJitter;
            callQuality.mAverageRoundTripTime = this.mAverageRoundTripTime;
            callQuality.mCodecType = this.mCodecType;
            callQuality.mRtpInactivityDetected = this.mRtpInactivityDetected;
            callQuality.mTxSilenceDetected = this.mTxSilenceDetected;
            callQuality.mRxSilenceDetected = this.mRxSilenceDetected;
            callQuality.mNumVoiceFrames = this.mNumVoiceFrames;
            callQuality.mNumNoDataFrames = this.mNumNoDataFrames;
            callQuality.mNumDroppedRtpPackets = this.mNumDroppedRtpPackets;
            callQuality.mMinPlayoutDelayMillis = this.mMinPlayoutDelayMillis;
            callQuality.mMaxPlayoutDelayMillis = this.mMaxPlayoutDelayMillis;
            callQuality.mNumRtpSidPacketsReceived = this.mNumRtpSidPacketsReceived;
            callQuality.mNumRtpDuplicatePackets = this.mNumRtpDuplicatePackets;
            return callQuality;
        }
    }
}
