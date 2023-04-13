package android.media.metrics;

import android.p008os.Bundle;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Objects;
/* loaded from: classes2.dex */
public final class TrackChangeEvent extends Event implements Parcelable {
    public static final Parcelable.Creator<TrackChangeEvent> CREATOR = new Parcelable.Creator<TrackChangeEvent>() { // from class: android.media.metrics.TrackChangeEvent.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public TrackChangeEvent[] newArray(int size) {
            return new TrackChangeEvent[size];
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public TrackChangeEvent createFromParcel(Parcel in) {
            return new TrackChangeEvent(in);
        }
    };
    public static final int TRACK_CHANGE_REASON_ADAPTIVE = 4;
    public static final int TRACK_CHANGE_REASON_INITIAL = 2;
    public static final int TRACK_CHANGE_REASON_MANUAL = 3;
    public static final int TRACK_CHANGE_REASON_OTHER = 1;
    public static final int TRACK_CHANGE_REASON_UNKNOWN = 0;
    public static final int TRACK_STATE_OFF = 0;
    public static final int TRACK_STATE_ON = 1;
    public static final int TRACK_TYPE_AUDIO = 0;
    public static final int TRACK_TYPE_TEXT = 2;
    public static final int TRACK_TYPE_VIDEO = 1;
    private final int mAudioSampleRate;
    private final int mBitrate;
    private final int mChannelCount;
    private final String mCodecName;
    private final String mContainerMimeType;
    private final int mHeight;
    private final String mLanguage;
    private final String mLanguageRegion;
    private final int mReason;
    private final String mSampleMimeType;
    private final int mState;
    private final long mTimeSinceCreatedMillis;
    private final int mType;
    private final float mVideoFrameRate;
    private final int mWidth;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes2.dex */
    public @interface TrackChangeReason {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes2.dex */
    public @interface TrackState {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes2.dex */
    public @interface TrackType {
    }

    private TrackChangeEvent(int state, int reason, String containerMimeType, String sampleMimeType, String codecName, int bitrate, long timeSinceCreatedMillis, int type, String language, String languageRegion, int channelCount, int sampleRate, int width, int height, float videoFrameRate, Bundle extras) {
        this.mState = state;
        this.mReason = reason;
        this.mContainerMimeType = containerMimeType;
        this.mSampleMimeType = sampleMimeType;
        this.mCodecName = codecName;
        this.mBitrate = bitrate;
        this.mTimeSinceCreatedMillis = timeSinceCreatedMillis;
        this.mType = type;
        this.mLanguage = language;
        this.mLanguageRegion = languageRegion;
        this.mChannelCount = channelCount;
        this.mAudioSampleRate = sampleRate;
        this.mWidth = width;
        this.mHeight = height;
        this.mVideoFrameRate = videoFrameRate;
        this.mMetricsBundle = extras.deepCopy();
    }

    public int getTrackState() {
        return this.mState;
    }

    public int getTrackChangeReason() {
        return this.mReason;
    }

    public String getContainerMimeType() {
        return this.mContainerMimeType;
    }

    public String getSampleMimeType() {
        return this.mSampleMimeType;
    }

    public String getCodecName() {
        return this.mCodecName;
    }

    public int getBitrate() {
        return this.mBitrate;
    }

    @Override // android.media.metrics.Event
    public long getTimeSinceCreatedMillis() {
        return this.mTimeSinceCreatedMillis;
    }

    public int getTrackType() {
        return this.mType;
    }

    public String getLanguage() {
        return this.mLanguage;
    }

    public String getLanguageRegion() {
        return this.mLanguageRegion;
    }

    public int getChannelCount() {
        return this.mChannelCount;
    }

    public int getAudioSampleRate() {
        return this.mAudioSampleRate;
    }

    public int getWidth() {
        return this.mWidth;
    }

    public int getHeight() {
        return this.mHeight;
    }

    public float getVideoFrameRate() {
        return this.mVideoFrameRate;
    }

    @Override // android.media.metrics.Event
    public Bundle getMetricsBundle() {
        return this.mMetricsBundle;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        int flg = this.mContainerMimeType != null ? 0 | 4 : 0;
        if (this.mSampleMimeType != null) {
            flg |= 8;
        }
        if (this.mCodecName != null) {
            flg |= 16;
        }
        if (this.mLanguage != null) {
            flg |= 256;
        }
        if (this.mLanguageRegion != null) {
            flg |= 512;
        }
        dest.writeInt(flg);
        dest.writeInt(this.mState);
        dest.writeInt(this.mReason);
        String str = this.mContainerMimeType;
        if (str != null) {
            dest.writeString(str);
        }
        String str2 = this.mSampleMimeType;
        if (str2 != null) {
            dest.writeString(str2);
        }
        String str3 = this.mCodecName;
        if (str3 != null) {
            dest.writeString(str3);
        }
        dest.writeInt(this.mBitrate);
        dest.writeLong(this.mTimeSinceCreatedMillis);
        dest.writeInt(this.mType);
        String str4 = this.mLanguage;
        if (str4 != null) {
            dest.writeString(str4);
        }
        String str5 = this.mLanguageRegion;
        if (str5 != null) {
            dest.writeString(str5);
        }
        dest.writeInt(this.mChannelCount);
        dest.writeInt(this.mAudioSampleRate);
        dest.writeInt(this.mWidth);
        dest.writeInt(this.mHeight);
        dest.writeFloat(this.mVideoFrameRate);
        dest.writeBundle(this.mMetricsBundle);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    private TrackChangeEvent(Parcel in) {
        int flg = in.readInt();
        int state = in.readInt();
        int reason = in.readInt();
        String containerMimeType = (flg & 4) == 0 ? null : in.readString();
        String sampleMimeType = (flg & 8) == 0 ? null : in.readString();
        String codecName = (flg & 16) == 0 ? null : in.readString();
        int bitrate = in.readInt();
        long timeSinceCreatedMillis = in.readLong();
        int type = in.readInt();
        String language = (flg & 256) == 0 ? null : in.readString();
        String languageRegion = (flg & 512) != 0 ? in.readString() : null;
        int channelCount = in.readInt();
        int sampleRate = in.readInt();
        int width = in.readInt();
        int height = in.readInt();
        float videoFrameRate = in.readFloat();
        Bundle extras = in.readBundle();
        this.mState = state;
        this.mReason = reason;
        this.mContainerMimeType = containerMimeType;
        this.mSampleMimeType = sampleMimeType;
        this.mCodecName = codecName;
        this.mBitrate = bitrate;
        this.mTimeSinceCreatedMillis = timeSinceCreatedMillis;
        this.mType = type;
        this.mLanguage = language;
        this.mLanguageRegion = languageRegion;
        this.mChannelCount = channelCount;
        this.mAudioSampleRate = sampleRate;
        this.mWidth = width;
        this.mHeight = height;
        this.mVideoFrameRate = videoFrameRate;
        this.mMetricsBundle = extras;
    }

    public String toString() {
        return "TrackChangeEvent { state = " + this.mState + ", reason = " + this.mReason + ", containerMimeType = " + this.mContainerMimeType + ", sampleMimeType = " + this.mSampleMimeType + ", codecName = " + this.mCodecName + ", bitrate = " + this.mBitrate + ", timeSinceCreatedMillis = " + this.mTimeSinceCreatedMillis + ", type = " + this.mType + ", language = " + this.mLanguage + ", languageRegion = " + this.mLanguageRegion + ", channelCount = " + this.mChannelCount + ", sampleRate = " + this.mAudioSampleRate + ", width = " + this.mWidth + ", height = " + this.mHeight + ", videoFrameRate = " + this.mVideoFrameRate + " }";
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TrackChangeEvent that = (TrackChangeEvent) o;
        if (this.mState == that.mState && this.mReason == that.mReason && Objects.equals(this.mContainerMimeType, that.mContainerMimeType) && Objects.equals(this.mSampleMimeType, that.mSampleMimeType) && Objects.equals(this.mCodecName, that.mCodecName) && this.mBitrate == that.mBitrate && this.mTimeSinceCreatedMillis == that.mTimeSinceCreatedMillis && this.mType == that.mType && Objects.equals(this.mLanguage, that.mLanguage) && Objects.equals(this.mLanguageRegion, that.mLanguageRegion) && this.mChannelCount == that.mChannelCount && this.mAudioSampleRate == that.mAudioSampleRate && this.mWidth == that.mWidth && this.mHeight == that.mHeight && this.mVideoFrameRate == that.mVideoFrameRate) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(Integer.valueOf(this.mState), Integer.valueOf(this.mReason), this.mContainerMimeType, this.mSampleMimeType, this.mCodecName, Integer.valueOf(this.mBitrate), Long.valueOf(this.mTimeSinceCreatedMillis), Integer.valueOf(this.mType), this.mLanguage, this.mLanguageRegion, Integer.valueOf(this.mChannelCount), Integer.valueOf(this.mAudioSampleRate), Integer.valueOf(this.mWidth), Integer.valueOf(this.mHeight), Float.valueOf(this.mVideoFrameRate));
    }

    /* loaded from: classes2.dex */
    public static final class Builder {
        private String mCodecName;
        private String mContainerMimeType;
        private String mLanguage;
        private String mLanguageRegion;
        private String mSampleMimeType;
        private final int mType;
        private int mState = 0;
        private int mReason = 0;
        private int mBitrate = -1;
        private long mTimeSinceCreatedMillis = -1;
        private int mChannelCount = -1;
        private int mAudioSampleRate = -1;
        private int mWidth = -1;
        private int mHeight = -1;
        private float mVideoFrameRate = -1.0f;
        private Bundle mMetricsBundle = new Bundle();
        private long mBuilderFieldsSet = 0;

        public Builder(int type) {
            if (type != 0 && type != 1 && type != 2) {
                throw new IllegalArgumentException("track type must be one of TRACK_TYPE_AUDIO, TRACK_TYPE_VIDEO, TRACK_TYPE_TEXT.");
            }
            this.mType = type;
        }

        public Builder setTrackState(int value) {
            checkNotUsed();
            this.mBuilderFieldsSet |= 1;
            this.mState = value;
            return this;
        }

        public Builder setTrackChangeReason(int value) {
            checkNotUsed();
            this.mBuilderFieldsSet |= 2;
            this.mReason = value;
            return this;
        }

        public Builder setContainerMimeType(String value) {
            checkNotUsed();
            this.mBuilderFieldsSet |= 4;
            this.mContainerMimeType = value;
            return this;
        }

        public Builder setSampleMimeType(String value) {
            checkNotUsed();
            this.mBuilderFieldsSet |= 8;
            this.mSampleMimeType = value;
            return this;
        }

        public Builder setCodecName(String value) {
            checkNotUsed();
            this.mBuilderFieldsSet |= 16;
            this.mCodecName = value;
            return this;
        }

        public Builder setBitrate(int value) {
            checkNotUsed();
            this.mBuilderFieldsSet |= 32;
            this.mBitrate = value;
            return this;
        }

        public Builder setTimeSinceCreatedMillis(long value) {
            checkNotUsed();
            this.mBuilderFieldsSet |= 64;
            this.mTimeSinceCreatedMillis = value;
            return this;
        }

        public Builder setLanguage(String value) {
            checkNotUsed();
            this.mBuilderFieldsSet |= 256;
            this.mLanguage = value;
            return this;
        }

        public Builder setLanguageRegion(String value) {
            checkNotUsed();
            this.mBuilderFieldsSet |= 512;
            this.mLanguageRegion = value;
            return this;
        }

        public Builder setChannelCount(int value) {
            checkNotUsed();
            this.mBuilderFieldsSet |= 1024;
            this.mChannelCount = value;
            return this;
        }

        public Builder setAudioSampleRate(int value) {
            checkNotUsed();
            this.mBuilderFieldsSet |= 2048;
            this.mAudioSampleRate = value;
            return this;
        }

        public Builder setWidth(int value) {
            checkNotUsed();
            this.mBuilderFieldsSet |= 4096;
            this.mWidth = value;
            return this;
        }

        public Builder setHeight(int value) {
            checkNotUsed();
            this.mBuilderFieldsSet |= 8192;
            this.mHeight = value;
            return this;
        }

        public Builder setVideoFrameRate(float value) {
            checkNotUsed();
            this.mVideoFrameRate = value;
            return this;
        }

        public Builder setMetricsBundle(Bundle metricsBundle) {
            this.mMetricsBundle = metricsBundle;
            return this;
        }

        public TrackChangeEvent build() {
            checkNotUsed();
            this.mBuilderFieldsSet |= 16384;
            TrackChangeEvent o = new TrackChangeEvent(this.mState, this.mReason, this.mContainerMimeType, this.mSampleMimeType, this.mCodecName, this.mBitrate, this.mTimeSinceCreatedMillis, this.mType, this.mLanguage, this.mLanguageRegion, this.mChannelCount, this.mAudioSampleRate, this.mWidth, this.mHeight, this.mVideoFrameRate, this.mMetricsBundle);
            return o;
        }

        private void checkNotUsed() {
            if ((this.mBuilderFieldsSet & 16384) != 0) {
                throw new IllegalStateException("This Builder should not be reused. Use a new Builder instance instead");
            }
        }
    }
}
