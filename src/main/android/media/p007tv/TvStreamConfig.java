package android.media.p007tv;

import android.annotation.SystemApi;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.util.Log;
@SystemApi
/* renamed from: android.media.tv.TvStreamConfig */
/* loaded from: classes2.dex */
public class TvStreamConfig implements Parcelable {
    public static final int STREAM_TYPE_BUFFER_PRODUCER = 2;
    public static final int STREAM_TYPE_INDEPENDENT_VIDEO_SOURCE = 1;
    private int mGeneration;
    private int mMaxHeight;
    private int mMaxWidth;
    private int mStreamId;
    private int mType;
    static final String TAG = TvStreamConfig.class.getSimpleName();
    public static final Parcelable.Creator<TvStreamConfig> CREATOR = new Parcelable.Creator<TvStreamConfig>() { // from class: android.media.tv.TvStreamConfig.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public TvStreamConfig createFromParcel(Parcel source) {
            try {
                return new Builder().streamId(source.readInt()).type(source.readInt()).maxWidth(source.readInt()).maxHeight(source.readInt()).generation(source.readInt()).build();
            } catch (Exception e) {
                Log.m109e(TvStreamConfig.TAG, "Exception creating TvStreamConfig from parcel", e);
                return null;
            }
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public TvStreamConfig[] newArray(int size) {
            return new TvStreamConfig[size];
        }
    };

    private TvStreamConfig() {
    }

    public int getStreamId() {
        return this.mStreamId;
    }

    public int getType() {
        return this.mType;
    }

    public int getMaxWidth() {
        return this.mMaxWidth;
    }

    public int getMaxHeight() {
        return this.mMaxHeight;
    }

    public int getGeneration() {
        return this.mGeneration;
    }

    public String toString() {
        return "TvStreamConfig {mStreamId=" + this.mStreamId + ";mType=" + this.mType + ";mGeneration=" + this.mGeneration + "}";
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mStreamId);
        dest.writeInt(this.mType);
        dest.writeInt(this.mMaxWidth);
        dest.writeInt(this.mMaxHeight);
        dest.writeInt(this.mGeneration);
    }

    /* renamed from: android.media.tv.TvStreamConfig$Builder */
    /* loaded from: classes2.dex */
    public static final class Builder {
        private Integer mGeneration;
        private Integer mMaxHeight;
        private Integer mMaxWidth;
        private Integer mStreamId;
        private Integer mType;

        public Builder streamId(int streamId) {
            this.mStreamId = Integer.valueOf(streamId);
            return this;
        }

        public Builder type(int type) {
            this.mType = Integer.valueOf(type);
            return this;
        }

        public Builder maxWidth(int maxWidth) {
            this.mMaxWidth = Integer.valueOf(maxWidth);
            return this;
        }

        public Builder maxHeight(int maxHeight) {
            this.mMaxHeight = Integer.valueOf(maxHeight);
            return this;
        }

        public Builder generation(int generation) {
            this.mGeneration = Integer.valueOf(generation);
            return this;
        }

        public TvStreamConfig build() {
            if (this.mStreamId == null || this.mType == null || this.mMaxWidth == null || this.mMaxHeight == null || this.mGeneration == null) {
                throw new UnsupportedOperationException();
            }
            TvStreamConfig config = new TvStreamConfig();
            config.mStreamId = this.mStreamId.intValue();
            config.mType = this.mType.intValue();
            config.mMaxWidth = this.mMaxWidth.intValue();
            config.mMaxHeight = this.mMaxHeight.intValue();
            config.mGeneration = this.mGeneration.intValue();
            return config;
        }
    }

    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof TvStreamConfig)) {
            return false;
        }
        TvStreamConfig config = (TvStreamConfig) obj;
        return config.mGeneration == this.mGeneration && config.mStreamId == this.mStreamId && config.mType == this.mType && config.mMaxWidth == this.mMaxWidth && config.mMaxHeight == this.mMaxHeight;
    }
}
