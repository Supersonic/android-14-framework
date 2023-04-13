package android.telecom;

import android.annotation.SystemApi;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.util.Objects;
@SystemApi
/* loaded from: classes3.dex */
public final class BluetoothCallQualityReport implements Parcelable {
    public static final Parcelable.Creator<BluetoothCallQualityReport> CREATOR = new Parcelable.Creator<BluetoothCallQualityReport>() { // from class: android.telecom.BluetoothCallQualityReport.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public BluetoothCallQualityReport createFromParcel(Parcel in) {
            return new BluetoothCallQualityReport(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public BluetoothCallQualityReport[] newArray(int size) {
            return new BluetoothCallQualityReport[size];
        }
    };
    public static final String EVENT_BLUETOOTH_CALL_QUALITY_REPORT = "android.telecom.event.BLUETOOTH_CALL_QUALITY_REPORT";
    public static final String EXTRA_BLUETOOTH_CALL_QUALITY_REPORT = "android.telecom.extra.BLUETOOTH_CALL_QUALITY_REPORT";
    private final boolean mChoppyVoice;
    private final int mNegativeAcknowledgementCount;
    private final int mPacketsNotReceivedCount;
    private final int mRetransmittedPacketsCount;
    private final int mRssiDbm;
    private final long mSentTimestampMillis;
    private final int mSnrDb;

    public long getSentTimestampMillis() {
        return this.mSentTimestampMillis;
    }

    public boolean isChoppyVoice() {
        return this.mChoppyVoice;
    }

    public int getRssiDbm() {
        return this.mRssiDbm;
    }

    public int getSnrDb() {
        return this.mSnrDb;
    }

    public int getRetransmittedPacketsCount() {
        return this.mRetransmittedPacketsCount;
    }

    public int getPacketsNotReceivedCount() {
        return this.mPacketsNotReceivedCount;
    }

    public int getNegativeAcknowledgementCount() {
        return this.mNegativeAcknowledgementCount;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        out.writeLong(this.mSentTimestampMillis);
        out.writeBoolean(this.mChoppyVoice);
        out.writeInt(this.mRssiDbm);
        out.writeInt(this.mSnrDb);
        out.writeInt(this.mRetransmittedPacketsCount);
        out.writeInt(this.mPacketsNotReceivedCount);
        out.writeInt(this.mNegativeAcknowledgementCount);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BluetoothCallQualityReport that = (BluetoothCallQualityReport) o;
        if (this.mSentTimestampMillis == that.mSentTimestampMillis && this.mChoppyVoice == that.mChoppyVoice && this.mRssiDbm == that.mRssiDbm && this.mSnrDb == that.mSnrDb && this.mRetransmittedPacketsCount == that.mRetransmittedPacketsCount && this.mPacketsNotReceivedCount == that.mPacketsNotReceivedCount && this.mNegativeAcknowledgementCount == that.mNegativeAcknowledgementCount) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(Long.valueOf(this.mSentTimestampMillis), Boolean.valueOf(this.mChoppyVoice), Integer.valueOf(this.mRssiDbm), Integer.valueOf(this.mSnrDb), Integer.valueOf(this.mRetransmittedPacketsCount), Integer.valueOf(this.mPacketsNotReceivedCount), Integer.valueOf(this.mNegativeAcknowledgementCount));
    }

    /* loaded from: classes3.dex */
    public static final class Builder {
        private boolean mChoppyVoice;
        private int mNegativeAcknowledgementCount;
        private int mPacketsNotReceivedCount;
        private int mRetransmittedPacketsCount;
        private int mRssiDbm;
        private long mSentTimestampMillis;
        private int mSnrDb;

        public Builder setSentTimestampMillis(long sentTimestampMillis) {
            this.mSentTimestampMillis = sentTimestampMillis;
            return this;
        }

        public Builder setChoppyVoice(boolean choppyVoice) {
            this.mChoppyVoice = choppyVoice;
            return this;
        }

        public Builder setRssiDbm(int rssiDbm) {
            this.mRssiDbm = rssiDbm;
            return this;
        }

        public Builder setSnrDb(int snrDb) {
            this.mSnrDb = snrDb;
            return this;
        }

        public Builder setRetransmittedPacketsCount(int retransmittedPacketsCount) {
            this.mRetransmittedPacketsCount = retransmittedPacketsCount;
            return this;
        }

        public Builder setPacketsNotReceivedCount(int packetsNotReceivedCount) {
            this.mPacketsNotReceivedCount = packetsNotReceivedCount;
            return this;
        }

        public Builder setNegativeAcknowledgementCount(int negativeAcknowledgementCount) {
            this.mNegativeAcknowledgementCount = negativeAcknowledgementCount;
            return this;
        }

        public BluetoothCallQualityReport build() {
            return new BluetoothCallQualityReport(this);
        }
    }

    private BluetoothCallQualityReport(Parcel in) {
        this.mSentTimestampMillis = in.readLong();
        this.mChoppyVoice = in.readBoolean();
        this.mRssiDbm = in.readInt();
        this.mSnrDb = in.readInt();
        this.mRetransmittedPacketsCount = in.readInt();
        this.mPacketsNotReceivedCount = in.readInt();
        this.mNegativeAcknowledgementCount = in.readInt();
    }

    private BluetoothCallQualityReport(Builder builder) {
        this.mSentTimestampMillis = builder.mSentTimestampMillis;
        this.mChoppyVoice = builder.mChoppyVoice;
        this.mRssiDbm = builder.mRssiDbm;
        this.mSnrDb = builder.mSnrDb;
        this.mRetransmittedPacketsCount = builder.mRetransmittedPacketsCount;
        this.mPacketsNotReceivedCount = builder.mPacketsNotReceivedCount;
        this.mNegativeAcknowledgementCount = builder.mNegativeAcknowledgementCount;
    }
}
