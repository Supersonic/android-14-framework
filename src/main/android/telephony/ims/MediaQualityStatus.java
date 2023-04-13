package android.telephony.ims;

import android.annotation.SystemApi;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.util.Objects;
@SystemApi
/* loaded from: classes3.dex */
public final class MediaQualityStatus implements Parcelable {
    public static final Parcelable.Creator<MediaQualityStatus> CREATOR = new Parcelable.Creator<MediaQualityStatus>() { // from class: android.telephony.ims.MediaQualityStatus.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public MediaQualityStatus createFromParcel(Parcel in) {
            return new MediaQualityStatus(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public MediaQualityStatus[] newArray(int size) {
            return new MediaQualityStatus[size];
        }
    };
    public static final int MEDIA_SESSION_TYPE_AUDIO = 1;
    public static final int MEDIA_SESSION_TYPE_VIDEO = 2;
    private final String mImsCallSessionId;
    private final int mMediaSessionType;
    private final long mRtpInactivityTimeMillis;
    private final int mRtpJitterMillis;
    private final int mRtpPacketLossRate;
    private final int mTransportType;

    /* loaded from: classes3.dex */
    public @interface MediaSessionType {
    }

    public MediaQualityStatus(String imsCallSessionId, int mediaSessionType, int transportType, int rtpPacketLossRate, int rtpJitterMillis, long rptInactivityTimeMillis) {
        this.mImsCallSessionId = imsCallSessionId;
        this.mMediaSessionType = mediaSessionType;
        this.mTransportType = transportType;
        this.mRtpPacketLossRate = rtpPacketLossRate;
        this.mRtpJitterMillis = rtpJitterMillis;
        this.mRtpInactivityTimeMillis = rptInactivityTimeMillis;
    }

    public String getCallSessionId() {
        return this.mImsCallSessionId;
    }

    public int getMediaSessionType() {
        return this.mMediaSessionType;
    }

    public int getTransportType() {
        return this.mTransportType;
    }

    public int getRtpPacketLossRate() {
        return this.mRtpPacketLossRate;
    }

    public int getRtpJitterMillis() {
        return this.mRtpJitterMillis;
    }

    public long getRtpInactivityMillis() {
        return this.mRtpInactivityTimeMillis;
    }

    private MediaQualityStatus(Parcel in) {
        this.mImsCallSessionId = in.readString();
        this.mMediaSessionType = in.readInt();
        this.mTransportType = in.readInt();
        this.mRtpPacketLossRate = in.readInt();
        this.mRtpJitterMillis = in.readInt();
        this.mRtpInactivityTimeMillis = in.readLong();
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mImsCallSessionId);
        dest.writeInt(this.mMediaSessionType);
        dest.writeInt(this.mTransportType);
        dest.writeInt(this.mRtpPacketLossRate);
        dest.writeInt(this.mRtpJitterMillis);
        dest.writeLong(this.mRtpInactivityTimeMillis);
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
        MediaQualityStatus that = (MediaQualityStatus) o;
        String str = this.mImsCallSessionId;
        if (str != null && str.equals(that.mImsCallSessionId) && this.mMediaSessionType == that.mMediaSessionType && this.mTransportType == that.mTransportType && this.mRtpPacketLossRate == that.mRtpPacketLossRate && this.mRtpJitterMillis == that.mRtpJitterMillis && this.mRtpInactivityTimeMillis == that.mRtpInactivityTimeMillis) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(this.mImsCallSessionId, Integer.valueOf(this.mMediaSessionType), Integer.valueOf(this.mTransportType), Integer.valueOf(this.mRtpPacketLossRate), Integer.valueOf(this.mRtpJitterMillis), Long.valueOf(this.mRtpInactivityTimeMillis));
    }

    public String toString() {
        return "MediaThreshold{mImsCallSessionId=" + this.mImsCallSessionId + ", mMediaSessionType=" + this.mMediaSessionType + ", mTransportType=" + this.mTransportType + ", mRtpPacketLossRate=" + this.mRtpPacketLossRate + ", mRtpJitterMillis=" + this.mRtpJitterMillis + ", mRtpInactivityTimeMillis=" + this.mRtpInactivityTimeMillis + "}";
    }
}
