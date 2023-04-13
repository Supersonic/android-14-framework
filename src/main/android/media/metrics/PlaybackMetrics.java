package android.media.metrics;

import android.annotation.NonNull;
import android.p008os.Bundle;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import com.android.internal.util.AnnotationValidations;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
/* loaded from: classes2.dex */
public final class PlaybackMetrics implements Parcelable {
    public static final int CONTENT_TYPE_AD = 2;
    public static final int CONTENT_TYPE_MAIN = 1;
    public static final int CONTENT_TYPE_OTHER = 3;
    public static final int CONTENT_TYPE_UNKNOWN = 0;
    public static final Parcelable.Creator<PlaybackMetrics> CREATOR = new Parcelable.Creator<PlaybackMetrics>() { // from class: android.media.metrics.PlaybackMetrics.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public PlaybackMetrics[] newArray(int size) {
            return new PlaybackMetrics[size];
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public PlaybackMetrics createFromParcel(Parcel in) {
            return new PlaybackMetrics(in);
        }
    };
    public static final int DRM_TYPE_CLEARKEY = 6;
    public static final int DRM_TYPE_NONE = 0;
    public static final int DRM_TYPE_OTHER = 1;
    public static final int DRM_TYPE_PLAY_READY = 2;
    public static final int DRM_TYPE_WIDEVINE_L1 = 3;
    public static final int DRM_TYPE_WIDEVINE_L3 = 4;
    public static final int DRM_TYPE_WV_L3_FALLBACK = 5;
    public static final int PLAYBACK_TYPE_LIVE = 2;
    public static final int PLAYBACK_TYPE_OTHER = 3;
    public static final int PLAYBACK_TYPE_UNKNOWN = 0;
    public static final int PLAYBACK_TYPE_VOD = 1;
    public static final int STREAM_SOURCE_DEVICE = 2;
    public static final int STREAM_SOURCE_MIXED = 3;
    public static final int STREAM_SOURCE_NETWORK = 1;
    public static final int STREAM_SOURCE_UNKNOWN = 0;
    public static final int STREAM_TYPE_DASH = 3;
    public static final int STREAM_TYPE_HLS = 4;
    public static final int STREAM_TYPE_OTHER = 1;
    public static final int STREAM_TYPE_PROGRESSIVE = 2;
    public static final int STREAM_TYPE_SS = 5;
    public static final int STREAM_TYPE_UNKNOWN = 0;
    private final int mAudioUnderrunCount;
    private final int mContentType;
    private final byte[] mDrmSessionId;
    private final int mDrmType;
    private final long[] mExperimentIds;
    private final long mLocalBytesRead;
    private final long mMediaDurationMillis;
    private final Bundle mMetricsBundle;
    private final long mNetworkBytesRead;
    private final long mNetworkTransferDurationMillis;
    private final int mPlaybackType;
    private final String mPlayerName;
    private final String mPlayerVersion;
    private final int mStreamSource;
    private final int mStreamType;
    private final int mVideoFramesDropped;
    private final int mVideoFramesPlayed;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes2.dex */
    public @interface ContentType {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes2.dex */
    public @interface DrmType {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes2.dex */
    public @interface PlaybackType {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes2.dex */
    public @interface StreamSource {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes2.dex */
    public @interface StreamType {
    }

    public PlaybackMetrics(long mediaDurationMillis, int streamSource, int streamType, int playbackType, int drmType, int contentType, String playerName, String playerVersion, long[] experimentIds, int videoFramesPlayed, int videoFramesDropped, int audioUnderrunCount, long networkBytesRead, long localBytesRead, long networkTransferDurationMillis, byte[] drmSessionId, Bundle extras) {
        this.mMediaDurationMillis = mediaDurationMillis;
        this.mStreamSource = streamSource;
        this.mStreamType = streamType;
        this.mPlaybackType = playbackType;
        this.mDrmType = drmType;
        this.mContentType = contentType;
        this.mPlayerName = playerName;
        this.mPlayerVersion = playerVersion;
        this.mExperimentIds = experimentIds;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) experimentIds);
        this.mVideoFramesPlayed = videoFramesPlayed;
        this.mVideoFramesDropped = videoFramesDropped;
        this.mAudioUnderrunCount = audioUnderrunCount;
        this.mNetworkBytesRead = networkBytesRead;
        this.mLocalBytesRead = localBytesRead;
        this.mNetworkTransferDurationMillis = networkTransferDurationMillis;
        this.mDrmSessionId = drmSessionId;
        this.mMetricsBundle = extras.deepCopy();
    }

    public long getMediaDurationMillis() {
        return this.mMediaDurationMillis;
    }

    public int getStreamSource() {
        return this.mStreamSource;
    }

    public int getStreamType() {
        return this.mStreamType;
    }

    public int getPlaybackType() {
        return this.mPlaybackType;
    }

    public int getDrmType() {
        return this.mDrmType;
    }

    public int getContentType() {
        return this.mContentType;
    }

    public String getPlayerName() {
        return this.mPlayerName;
    }

    public String getPlayerVersion() {
        return this.mPlayerVersion;
    }

    public long[] getExperimentIds() {
        long[] jArr = this.mExperimentIds;
        return Arrays.copyOf(jArr, jArr.length);
    }

    public int getVideoFramesPlayed() {
        return this.mVideoFramesPlayed;
    }

    public int getVideoFramesDropped() {
        return this.mVideoFramesDropped;
    }

    public int getAudioUnderrunCount() {
        return this.mAudioUnderrunCount;
    }

    public long getNetworkBytesRead() {
        return this.mNetworkBytesRead;
    }

    public long getLocalBytesRead() {
        return this.mLocalBytesRead;
    }

    public long getNetworkTransferDurationMillis() {
        return this.mNetworkTransferDurationMillis;
    }

    public byte[] getDrmSessionId() {
        return this.mDrmSessionId;
    }

    public Bundle getMetricsBundle() {
        return this.mMetricsBundle;
    }

    public String toString() {
        return "PlaybackMetrics { mediaDurationMillis = " + this.mMediaDurationMillis + ", streamSource = " + this.mStreamSource + ", streamType = " + this.mStreamType + ", playbackType = " + this.mPlaybackType + ", drmType = " + this.mDrmType + ", contentType = " + this.mContentType + ", playerName = " + this.mPlayerName + ", playerVersion = " + this.mPlayerVersion + ", experimentIds = " + Arrays.toString(this.mExperimentIds) + ", videoFramesPlayed = " + this.mVideoFramesPlayed + ", videoFramesDropped = " + this.mVideoFramesDropped + ", audioUnderrunCount = " + this.mAudioUnderrunCount + ", networkBytesRead = " + this.mNetworkBytesRead + ", localBytesRead = " + this.mLocalBytesRead + ", networkTransferDurationMillis = " + this.mNetworkTransferDurationMillis + "drmSessionId = " + Arrays.toString(this.mDrmSessionId) + " }";
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PlaybackMetrics that = (PlaybackMetrics) o;
        if (this.mMediaDurationMillis == that.mMediaDurationMillis && this.mStreamSource == that.mStreamSource && this.mStreamType == that.mStreamType && this.mPlaybackType == that.mPlaybackType && this.mDrmType == that.mDrmType && this.mContentType == that.mContentType && Objects.equals(this.mPlayerName, that.mPlayerName) && Objects.equals(this.mPlayerVersion, that.mPlayerVersion) && Arrays.equals(this.mExperimentIds, that.mExperimentIds) && this.mVideoFramesPlayed == that.mVideoFramesPlayed && this.mVideoFramesDropped == that.mVideoFramesDropped && this.mAudioUnderrunCount == that.mAudioUnderrunCount && this.mNetworkBytesRead == that.mNetworkBytesRead && this.mLocalBytesRead == that.mLocalBytesRead && this.mNetworkTransferDurationMillis == that.mNetworkTransferDurationMillis && Arrays.equals(this.mDrmSessionId, that.mDrmSessionId)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(Long.valueOf(this.mMediaDurationMillis), Integer.valueOf(this.mStreamSource), Integer.valueOf(this.mStreamType), Integer.valueOf(this.mPlaybackType), Integer.valueOf(this.mDrmType), Integer.valueOf(this.mContentType), this.mPlayerName, this.mPlayerVersion, Integer.valueOf(Arrays.hashCode(this.mExperimentIds)), Integer.valueOf(this.mVideoFramesPlayed), Integer.valueOf(this.mVideoFramesDropped), Integer.valueOf(this.mAudioUnderrunCount), Long.valueOf(this.mNetworkBytesRead), Long.valueOf(this.mLocalBytesRead), Long.valueOf(this.mNetworkTransferDurationMillis), Integer.valueOf(Arrays.hashCode(this.mDrmSessionId)));
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        long flg = this.mPlayerName != null ? 0 | 128 : 0L;
        if (this.mPlayerVersion != null) {
            flg |= 256;
        }
        dest.writeLong(flg);
        dest.writeLong(this.mMediaDurationMillis);
        dest.writeInt(this.mStreamSource);
        dest.writeInt(this.mStreamType);
        dest.writeInt(this.mPlaybackType);
        dest.writeInt(this.mDrmType);
        dest.writeInt(this.mContentType);
        String str = this.mPlayerName;
        if (str != null) {
            dest.writeString(str);
        }
        String str2 = this.mPlayerVersion;
        if (str2 != null) {
            dest.writeString(str2);
        }
        dest.writeLongArray(this.mExperimentIds);
        dest.writeInt(this.mVideoFramesPlayed);
        dest.writeInt(this.mVideoFramesDropped);
        dest.writeInt(this.mAudioUnderrunCount);
        dest.writeLong(this.mNetworkBytesRead);
        dest.writeLong(this.mLocalBytesRead);
        dest.writeLong(this.mNetworkTransferDurationMillis);
        dest.writeInt(this.mDrmSessionId.length);
        dest.writeByteArray(this.mDrmSessionId);
        dest.writeBundle(this.mMetricsBundle);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    PlaybackMetrics(Parcel in) {
        long flg = in.readLong();
        long mediaDurationMillis = in.readLong();
        int streamSource = in.readInt();
        int streamType = in.readInt();
        int playbackType = in.readInt();
        int drmType = in.readInt();
        int contentType = in.readInt();
        String playerName = (128 & flg) == 0 ? null : in.readString();
        String playerVersion = (256 & flg) == 0 ? null : in.readString();
        long[] experimentIds = in.createLongArray();
        int videoFramesPlayed = in.readInt();
        int videoFramesDropped = in.readInt();
        int audioUnderrunCount = in.readInt();
        long networkBytesRead = in.readLong();
        long localBytesRead = in.readLong();
        long networkTransferDurationMillis = in.readLong();
        int drmSessionIdLen = in.readInt();
        byte[] drmSessionId = new byte[drmSessionIdLen];
        in.readByteArray(drmSessionId);
        Bundle extras = in.readBundle();
        this.mMediaDurationMillis = mediaDurationMillis;
        this.mStreamSource = streamSource;
        this.mStreamType = streamType;
        this.mPlaybackType = playbackType;
        this.mDrmType = drmType;
        this.mContentType = contentType;
        this.mPlayerName = playerName;
        this.mPlayerVersion = playerVersion;
        this.mExperimentIds = experimentIds;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) experimentIds);
        this.mVideoFramesPlayed = videoFramesPlayed;
        this.mVideoFramesDropped = videoFramesDropped;
        this.mAudioUnderrunCount = audioUnderrunCount;
        this.mNetworkBytesRead = networkBytesRead;
        this.mLocalBytesRead = localBytesRead;
        this.mNetworkTransferDurationMillis = networkTransferDurationMillis;
        this.mDrmSessionId = drmSessionId;
        this.mMetricsBundle = extras;
    }

    /* loaded from: classes2.dex */
    public static final class Builder {
        private String mPlayerName;
        private String mPlayerVersion;
        private long mMediaDurationMillis = -1;
        private int mStreamSource = 0;
        private int mStreamType = 0;
        private int mPlaybackType = 0;
        private int mDrmType = 0;
        private int mContentType = 0;
        private List<Long> mExperimentIds = new ArrayList();
        private int mVideoFramesPlayed = -1;
        private int mVideoFramesDropped = -1;
        private int mAudioUnderrunCount = -1;
        private long mNetworkBytesRead = -1;
        private long mLocalBytesRead = -1;
        private long mNetworkTransferDurationMillis = -1;
        private byte[] mDrmSessionId = new byte[0];
        private Bundle mMetricsBundle = new Bundle();

        public Builder setMediaDurationMillis(long value) {
            this.mMediaDurationMillis = value;
            return this;
        }

        public Builder setStreamSource(int value) {
            this.mStreamSource = value;
            return this;
        }

        public Builder setStreamType(int value) {
            this.mStreamType = value;
            return this;
        }

        public Builder setPlaybackType(int value) {
            this.mPlaybackType = value;
            return this;
        }

        public Builder setDrmType(int value) {
            this.mDrmType = value;
            return this;
        }

        public Builder setContentType(int value) {
            this.mContentType = value;
            return this;
        }

        public Builder setPlayerName(String value) {
            this.mPlayerName = value;
            return this;
        }

        public Builder setPlayerVersion(String value) {
            this.mPlayerVersion = value;
            return this;
        }

        public Builder addExperimentId(long value) {
            this.mExperimentIds.add(Long.valueOf(value));
            return this;
        }

        public Builder setVideoFramesPlayed(int value) {
            this.mVideoFramesPlayed = value;
            return this;
        }

        public Builder setVideoFramesDropped(int value) {
            this.mVideoFramesDropped = value;
            return this;
        }

        public Builder setAudioUnderrunCount(int value) {
            this.mAudioUnderrunCount = value;
            return this;
        }

        public Builder setNetworkBytesRead(long value) {
            this.mNetworkBytesRead = value;
            return this;
        }

        public Builder setLocalBytesRead(long value) {
            this.mLocalBytesRead = value;
            return this;
        }

        public Builder setNetworkTransferDurationMillis(long value) {
            this.mNetworkTransferDurationMillis = value;
            return this;
        }

        public Builder setDrmSessionId(byte[] drmSessionId) {
            this.mDrmSessionId = drmSessionId;
            return this;
        }

        public Builder setMetricsBundle(Bundle metricsBundle) {
            this.mMetricsBundle = metricsBundle;
            return this;
        }

        public PlaybackMetrics build() {
            PlaybackMetrics o = new PlaybackMetrics(this.mMediaDurationMillis, this.mStreamSource, this.mStreamType, this.mPlaybackType, this.mDrmType, this.mContentType, this.mPlayerName, this.mPlayerVersion, idsToLongArray(), this.mVideoFramesPlayed, this.mVideoFramesDropped, this.mAudioUnderrunCount, this.mNetworkBytesRead, this.mLocalBytesRead, this.mNetworkTransferDurationMillis, this.mDrmSessionId, this.mMetricsBundle);
            return o;
        }

        private long[] idsToLongArray() {
            long[] ids = new long[this.mExperimentIds.size()];
            for (int i = 0; i < this.mExperimentIds.size(); i++) {
                ids[i] = this.mExperimentIds.get(i).longValue();
            }
            return ids;
        }
    }
}
