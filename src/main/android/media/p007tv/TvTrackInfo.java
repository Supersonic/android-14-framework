package android.media.p007tv;

import android.p008os.Bundle;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.text.TextUtils;
import com.android.internal.util.Preconditions;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Objects;
/* renamed from: android.media.tv.TvTrackInfo */
/* loaded from: classes2.dex */
public final class TvTrackInfo implements Parcelable {
    public static final Parcelable.Creator<TvTrackInfo> CREATOR = new Parcelable.Creator<TvTrackInfo>() { // from class: android.media.tv.TvTrackInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public TvTrackInfo createFromParcel(Parcel in) {
            return new TvTrackInfo(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public TvTrackInfo[] newArray(int size) {
            return new TvTrackInfo[size];
        }
    };
    public static final int TYPE_AUDIO = 0;
    public static final int TYPE_SUBTITLE = 2;
    public static final int TYPE_VIDEO = 1;
    private final int mAudioChannelCount;
    private final boolean mAudioDescription;
    private final int mAudioSampleRate;
    private final CharSequence mDescription;
    private final String mEncoding;
    private final boolean mEncrypted;
    private final Bundle mExtra;
    private final boolean mHardOfHearing;
    private final String mId;
    private final String mLanguage;
    private final boolean mSpokenSubtitle;
    private final int mType;
    private final byte mVideoActiveFormatDescription;
    private final float mVideoFrameRate;
    private final int mVideoHeight;
    private final float mVideoPixelAspectRatio;
    private final int mVideoWidth;

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.media.tv.TvTrackInfo$Type */
    /* loaded from: classes2.dex */
    public @interface Type {
    }

    private TvTrackInfo(int type, String id, String language, CharSequence description, String encoding, boolean encrypted, int audioChannelCount, int audioSampleRate, boolean audioDescription, boolean hardOfHearing, boolean spokenSubtitle, int videoWidth, int videoHeight, float videoFrameRate, float videoPixelAspectRatio, byte videoActiveFormatDescription, Bundle extra) {
        this.mType = type;
        this.mId = id;
        this.mLanguage = language;
        this.mDescription = description;
        this.mEncoding = encoding;
        this.mEncrypted = encrypted;
        this.mAudioChannelCount = audioChannelCount;
        this.mAudioSampleRate = audioSampleRate;
        this.mAudioDescription = audioDescription;
        this.mHardOfHearing = hardOfHearing;
        this.mSpokenSubtitle = spokenSubtitle;
        this.mVideoWidth = videoWidth;
        this.mVideoHeight = videoHeight;
        this.mVideoFrameRate = videoFrameRate;
        this.mVideoPixelAspectRatio = videoPixelAspectRatio;
        this.mVideoActiveFormatDescription = videoActiveFormatDescription;
        this.mExtra = extra;
    }

    private TvTrackInfo(Parcel in) {
        this.mType = in.readInt();
        this.mId = in.readString();
        this.mLanguage = in.readString();
        this.mDescription = in.readString();
        this.mEncoding = in.readString();
        this.mEncrypted = in.readInt() != 0;
        this.mAudioChannelCount = in.readInt();
        this.mAudioSampleRate = in.readInt();
        this.mAudioDescription = in.readInt() != 0;
        this.mHardOfHearing = in.readInt() != 0;
        this.mSpokenSubtitle = in.readInt() != 0;
        this.mVideoWidth = in.readInt();
        this.mVideoHeight = in.readInt();
        this.mVideoFrameRate = in.readFloat();
        this.mVideoPixelAspectRatio = in.readFloat();
        this.mVideoActiveFormatDescription = in.readByte();
        this.mExtra = in.readBundle();
    }

    public final int getType() {
        return this.mType;
    }

    public final String getId() {
        return this.mId;
    }

    public final String getLanguage() {
        return this.mLanguage;
    }

    public final CharSequence getDescription() {
        return this.mDescription;
    }

    public String getEncoding() {
        return this.mEncoding;
    }

    public boolean isEncrypted() {
        return this.mEncrypted;
    }

    public final int getAudioChannelCount() {
        if (this.mType != 0) {
            throw new IllegalStateException("Not an audio track");
        }
        return this.mAudioChannelCount;
    }

    public final int getAudioSampleRate() {
        if (this.mType != 0) {
            throw new IllegalStateException("Not an audio track");
        }
        return this.mAudioSampleRate;
    }

    public boolean isAudioDescription() {
        if (this.mType != 0) {
            throw new IllegalStateException("Not an audio track");
        }
        return this.mAudioDescription;
    }

    public boolean isHardOfHearing() {
        int i = this.mType;
        if (i != 0 && i != 2) {
            throw new IllegalStateException("Not an audio or a subtitle track");
        }
        return this.mHardOfHearing;
    }

    public boolean isSpokenSubtitle() {
        if (this.mType != 0) {
            throw new IllegalStateException("Not an audio track");
        }
        return this.mSpokenSubtitle;
    }

    public final int getVideoWidth() {
        if (this.mType != 1) {
            throw new IllegalStateException("Not a video track");
        }
        return this.mVideoWidth;
    }

    public final int getVideoHeight() {
        if (this.mType != 1) {
            throw new IllegalStateException("Not a video track");
        }
        return this.mVideoHeight;
    }

    public final float getVideoFrameRate() {
        if (this.mType != 1) {
            throw new IllegalStateException("Not a video track");
        }
        return this.mVideoFrameRate;
    }

    public final float getVideoPixelAspectRatio() {
        if (this.mType != 1) {
            throw new IllegalStateException("Not a video track");
        }
        return this.mVideoPixelAspectRatio;
    }

    public final byte getVideoActiveFormatDescription() {
        if (this.mType != 1) {
            throw new IllegalStateException("Not a video track");
        }
        return this.mVideoActiveFormatDescription;
    }

    public final Bundle getExtra() {
        return this.mExtra;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        Preconditions.checkNotNull(dest);
        dest.writeInt(this.mType);
        dest.writeString(this.mId);
        dest.writeString(this.mLanguage);
        CharSequence charSequence = this.mDescription;
        dest.writeString(charSequence != null ? charSequence.toString() : null);
        dest.writeString(this.mEncoding);
        dest.writeInt(this.mEncrypted ? 1 : 0);
        dest.writeInt(this.mAudioChannelCount);
        dest.writeInt(this.mAudioSampleRate);
        dest.writeInt(this.mAudioDescription ? 1 : 0);
        dest.writeInt(this.mHardOfHearing ? 1 : 0);
        dest.writeInt(this.mSpokenSubtitle ? 1 : 0);
        dest.writeInt(this.mVideoWidth);
        dest.writeInt(this.mVideoHeight);
        dest.writeFloat(this.mVideoFrameRate);
        dest.writeFloat(this.mVideoPixelAspectRatio);
        dest.writeByte(this.mVideoActiveFormatDescription);
        dest.writeBundle(this.mExtra);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof TvTrackInfo) {
            TvTrackInfo obj = (TvTrackInfo) o;
            if (TextUtils.equals(this.mId, obj.mId) && this.mType == obj.mType && TextUtils.equals(this.mLanguage, obj.mLanguage) && TextUtils.equals(this.mDescription, obj.mDescription) && TextUtils.equals(this.mEncoding, obj.mEncoding) && this.mEncrypted == obj.mEncrypted) {
                switch (this.mType) {
                    case 0:
                        return this.mAudioChannelCount == obj.mAudioChannelCount && this.mAudioSampleRate == obj.mAudioSampleRate && this.mAudioDescription == obj.mAudioDescription && this.mHardOfHearing == obj.mHardOfHearing && this.mSpokenSubtitle == obj.mSpokenSubtitle;
                    case 1:
                        return this.mVideoWidth == obj.mVideoWidth && this.mVideoHeight == obj.mVideoHeight && this.mVideoFrameRate == obj.mVideoFrameRate && this.mVideoPixelAspectRatio == obj.mVideoPixelAspectRatio && this.mVideoActiveFormatDescription == obj.mVideoActiveFormatDescription;
                    case 2:
                        return this.mHardOfHearing == obj.mHardOfHearing;
                    default:
                        return true;
                }
            }
            return false;
        }
        return false;
    }

    public int hashCode() {
        int result = Objects.hash(this.mId, Integer.valueOf(this.mType), this.mLanguage, this.mDescription);
        int i = this.mType;
        if (i == 0) {
            return Objects.hash(Integer.valueOf(result), Integer.valueOf(this.mAudioChannelCount), Integer.valueOf(this.mAudioSampleRate));
        }
        if (i == 1) {
            return Objects.hash(Integer.valueOf(result), Integer.valueOf(this.mVideoWidth), Integer.valueOf(this.mVideoHeight), Float.valueOf(this.mVideoFrameRate), Float.valueOf(this.mVideoPixelAspectRatio));
        }
        return result;
    }

    /* renamed from: android.media.tv.TvTrackInfo$Builder */
    /* loaded from: classes2.dex */
    public static final class Builder {
        private int mAudioChannelCount;
        private boolean mAudioDescription;
        private int mAudioSampleRate;
        private CharSequence mDescription;
        private String mEncoding;
        private boolean mEncrypted;
        private Bundle mExtra;
        private boolean mHardOfHearing;
        private final String mId;
        private String mLanguage;
        private boolean mSpokenSubtitle;
        private final int mType;
        private byte mVideoActiveFormatDescription;
        private float mVideoFrameRate;
        private int mVideoHeight;
        private float mVideoPixelAspectRatio = 1.0f;
        private int mVideoWidth;

        public Builder(int type, String id) {
            if (type != 0 && type != 1 && type != 2) {
                throw new IllegalArgumentException("Unknown type: " + type);
            }
            Preconditions.checkNotNull(id);
            this.mType = type;
            this.mId = id;
        }

        public Builder setLanguage(String language) {
            Preconditions.checkNotNull(language);
            this.mLanguage = language;
            return this;
        }

        public Builder setDescription(CharSequence description) {
            Preconditions.checkNotNull(description);
            this.mDescription = description;
            return this;
        }

        public Builder setEncoding(String encoding) {
            this.mEncoding = encoding;
            return this;
        }

        public Builder setEncrypted(boolean encrypted) {
            this.mEncrypted = encrypted;
            return this;
        }

        public Builder setAudioChannelCount(int audioChannelCount) {
            if (this.mType != 0) {
                throw new IllegalStateException("Not an audio track");
            }
            this.mAudioChannelCount = audioChannelCount;
            return this;
        }

        public Builder setAudioSampleRate(int audioSampleRate) {
            if (this.mType != 0) {
                throw new IllegalStateException("Not an audio track");
            }
            this.mAudioSampleRate = audioSampleRate;
            return this;
        }

        public Builder setAudioDescription(boolean audioDescription) {
            if (this.mType != 0) {
                throw new IllegalStateException("Not an audio track");
            }
            this.mAudioDescription = audioDescription;
            return this;
        }

        public Builder setHardOfHearing(boolean hardOfHearing) {
            int i = this.mType;
            if (i != 0 && i != 2) {
                throw new IllegalStateException("Not an audio track or a subtitle track");
            }
            this.mHardOfHearing = hardOfHearing;
            return this;
        }

        public Builder setSpokenSubtitle(boolean spokenSubtitle) {
            if (this.mType != 0) {
                throw new IllegalStateException("Not an audio track");
            }
            this.mSpokenSubtitle = spokenSubtitle;
            return this;
        }

        public Builder setVideoWidth(int videoWidth) {
            if (this.mType != 1) {
                throw new IllegalStateException("Not a video track");
            }
            this.mVideoWidth = videoWidth;
            return this;
        }

        public Builder setVideoHeight(int videoHeight) {
            if (this.mType != 1) {
                throw new IllegalStateException("Not a video track");
            }
            this.mVideoHeight = videoHeight;
            return this;
        }

        public Builder setVideoFrameRate(float videoFrameRate) {
            if (this.mType != 1) {
                throw new IllegalStateException("Not a video track");
            }
            this.mVideoFrameRate = videoFrameRate;
            return this;
        }

        public Builder setVideoPixelAspectRatio(float videoPixelAspectRatio) {
            if (this.mType != 1) {
                throw new IllegalStateException("Not a video track");
            }
            this.mVideoPixelAspectRatio = videoPixelAspectRatio;
            return this;
        }

        public Builder setVideoActiveFormatDescription(byte videoActiveFormatDescription) {
            if (this.mType != 1) {
                throw new IllegalStateException("Not a video track");
            }
            this.mVideoActiveFormatDescription = videoActiveFormatDescription;
            return this;
        }

        public Builder setExtra(Bundle extra) {
            Preconditions.checkNotNull(extra);
            this.mExtra = new Bundle(extra);
            return this;
        }

        public TvTrackInfo build() {
            return new TvTrackInfo(this.mType, this.mId, this.mLanguage, this.mDescription, this.mEncoding, this.mEncrypted, this.mAudioChannelCount, this.mAudioSampleRate, this.mAudioDescription, this.mHardOfHearing, this.mSpokenSubtitle, this.mVideoWidth, this.mVideoHeight, this.mVideoFrameRate, this.mVideoPixelAspectRatio, this.mVideoActiveFormatDescription, this.mExtra);
        }
    }
}
