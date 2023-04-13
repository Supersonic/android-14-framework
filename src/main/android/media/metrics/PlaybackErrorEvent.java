package android.media.metrics;

import android.p008os.Bundle;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Objects;
/* loaded from: classes2.dex */
public final class PlaybackErrorEvent extends Event implements Parcelable {
    public static final Parcelable.Creator<PlaybackErrorEvent> CREATOR = new Parcelable.Creator<PlaybackErrorEvent>() { // from class: android.media.metrics.PlaybackErrorEvent.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public PlaybackErrorEvent[] newArray(int size) {
            return new PlaybackErrorEvent[size];
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public PlaybackErrorEvent createFromParcel(Parcel in) {
            return new PlaybackErrorEvent(in);
        }
    };
    public static final int ERROR_AUDIO_TRACK_INIT_FAILED = 17;
    public static final int ERROR_AUDIO_TRACK_OTHER = 19;
    public static final int ERROR_AUDIO_TRACK_WRITE_FAILED = 18;
    public static final int ERROR_DECODER_INIT_FAILED = 13;
    public static final int ERROR_DECODING_FAILED = 14;
    public static final int ERROR_DECODING_FORMAT_EXCEEDS_CAPABILITIES = 15;
    public static final int ERROR_DECODING_FORMAT_UNSUPPORTED = 35;
    public static final int ERROR_DECODING_OTHER = 16;
    public static final int ERROR_DRM_CONTENT_ERROR = 28;
    public static final int ERROR_DRM_DEVICE_REVOKED = 29;
    public static final int ERROR_DRM_DISALLOWED_OPERATION = 26;
    public static final int ERROR_DRM_LICENSE_ACQUISITION_FAILED = 25;
    public static final int ERROR_DRM_OTHER = 30;
    public static final int ERROR_DRM_PROVISIONING_FAILED = 24;
    public static final int ERROR_DRM_SCHEME_UNSUPPORTED = 23;
    public static final int ERROR_DRM_SYSTEM_ERROR = 27;
    public static final int ERROR_IO_BAD_HTTP_STATUS = 5;
    public static final int ERROR_IO_CONNECTION_CLOSED = 8;
    public static final int ERROR_IO_CONNECTION_TIMEOUT = 7;
    public static final int ERROR_IO_DNS_FAILED = 6;
    public static final int ERROR_IO_FILE_NOT_FOUND = 31;
    public static final int ERROR_IO_NETWORK_CONNECTION_FAILED = 4;
    public static final int ERROR_IO_NETWORK_UNAVAILABLE = 3;
    public static final int ERROR_IO_NO_PERMISSION = 32;
    public static final int ERROR_IO_OTHER = 9;
    public static final int ERROR_OTHER = 1;
    public static final int ERROR_PARSING_CONTAINER_MALFORMED = 11;
    public static final int ERROR_PARSING_CONTAINER_UNSUPPORTED = 34;
    public static final int ERROR_PARSING_MANIFEST_MALFORMED = 10;
    public static final int ERROR_PARSING_MANIFEST_UNSUPPORTED = 33;
    public static final int ERROR_PARSING_OTHER = 12;
    public static final int ERROR_PLAYER_BEHIND_LIVE_WINDOW = 21;
    public static final int ERROR_PLAYER_OTHER = 22;
    public static final int ERROR_PLAYER_REMOTE = 20;
    public static final int ERROR_RUNTIME = 2;
    public static final int ERROR_UNKNOWN = 0;
    private final int mErrorCode;
    private final String mExceptionStack;
    private final int mSubErrorCode;
    private final long mTimeSinceCreatedMillis;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes2.dex */
    public @interface ErrorCode {
    }

    private PlaybackErrorEvent(String exceptionStack, int errorCode, int subErrorCode, long timeSinceCreatedMillis, Bundle extras) {
        this.mExceptionStack = exceptionStack;
        this.mErrorCode = errorCode;
        this.mSubErrorCode = subErrorCode;
        this.mTimeSinceCreatedMillis = timeSinceCreatedMillis;
        this.mMetricsBundle = extras.deepCopy();
    }

    public String getExceptionStack() {
        return this.mExceptionStack;
    }

    public int getErrorCode() {
        return this.mErrorCode;
    }

    public int getSubErrorCode() {
        return this.mSubErrorCode;
    }

    @Override // android.media.metrics.Event
    public long getTimeSinceCreatedMillis() {
        return this.mTimeSinceCreatedMillis;
    }

    @Override // android.media.metrics.Event
    public Bundle getMetricsBundle() {
        return this.mMetricsBundle;
    }

    public String toString() {
        return "PlaybackErrorEvent { exceptionStack = " + this.mExceptionStack + ", errorCode = " + this.mErrorCode + ", subErrorCode = " + this.mSubErrorCode + ", timeSinceCreatedMillis = " + this.mTimeSinceCreatedMillis + " }";
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PlaybackErrorEvent that = (PlaybackErrorEvent) o;
        if (Objects.equals(this.mExceptionStack, that.mExceptionStack) && this.mErrorCode == that.mErrorCode && this.mSubErrorCode == that.mSubErrorCode && this.mTimeSinceCreatedMillis == that.mTimeSinceCreatedMillis) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(this.mExceptionStack, Integer.valueOf(this.mErrorCode), Integer.valueOf(this.mSubErrorCode), Long.valueOf(this.mTimeSinceCreatedMillis));
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        byte flg = this.mExceptionStack != null ? (byte) (0 | 1) : (byte) 0;
        dest.writeByte(flg);
        String str = this.mExceptionStack;
        if (str != null) {
            dest.writeString(str);
        }
        dest.writeInt(this.mErrorCode);
        dest.writeInt(this.mSubErrorCode);
        dest.writeLong(this.mTimeSinceCreatedMillis);
        dest.writeBundle(this.mMetricsBundle);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    private PlaybackErrorEvent(Parcel in) {
        byte flg = in.readByte();
        String exceptionStack = (flg & 1) == 0 ? null : in.readString();
        int errorCode = in.readInt();
        int subErrorCode = in.readInt();
        long timeSinceCreatedMillis = in.readLong();
        Bundle extras = in.readBundle();
        this.mExceptionStack = exceptionStack;
        this.mErrorCode = errorCode;
        this.mSubErrorCode = subErrorCode;
        this.mTimeSinceCreatedMillis = timeSinceCreatedMillis;
        this.mMetricsBundle = extras;
    }

    /* loaded from: classes2.dex */
    public static final class Builder {
        private Exception mException;
        private int mSubErrorCode;
        private int mErrorCode = 0;
        private long mTimeSinceCreatedMillis = -1;
        private Bundle mMetricsBundle = new Bundle();

        public Builder setException(Exception value) {
            this.mException = value;
            return this;
        }

        public Builder setErrorCode(int value) {
            this.mErrorCode = value;
            return this;
        }

        public Builder setSubErrorCode(int value) {
            this.mSubErrorCode = value;
            return this;
        }

        public Builder setTimeSinceCreatedMillis(long value) {
            this.mTimeSinceCreatedMillis = value;
            return this;
        }

        public Builder setMetricsBundle(Bundle metricsBundle) {
            this.mMetricsBundle = metricsBundle;
            return this;
        }

        public PlaybackErrorEvent build() {
            String stack;
            if (this.mException.getStackTrace() != null && this.mException.getStackTrace().length > 0) {
                stack = this.mException.getStackTrace()[0].toString();
            } else {
                stack = null;
            }
            PlaybackErrorEvent o = new PlaybackErrorEvent(stack, this.mErrorCode, this.mSubErrorCode, this.mTimeSinceCreatedMillis, this.mMetricsBundle);
            return o;
        }
    }
}
