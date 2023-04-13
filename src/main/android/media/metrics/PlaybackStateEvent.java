package android.media.metrics;

import android.p008os.Bundle;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Objects;
/* loaded from: classes2.dex */
public final class PlaybackStateEvent extends Event implements Parcelable {
    public static final Parcelable.Creator<PlaybackStateEvent> CREATOR = new Parcelable.Creator<PlaybackStateEvent>() { // from class: android.media.metrics.PlaybackStateEvent.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public PlaybackStateEvent[] newArray(int size) {
            return new PlaybackStateEvent[size];
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public PlaybackStateEvent createFromParcel(Parcel in) {
            return new PlaybackStateEvent(in);
        }
    };
    public static final int STATE_ABANDONED = 15;
    public static final int STATE_BUFFERING = 6;
    public static final int STATE_ENDED = 11;
    public static final int STATE_FAILED = 13;
    public static final int STATE_INTERRUPTED_BY_AD = 14;
    public static final int STATE_JOINING_BACKGROUND = 1;
    public static final int STATE_JOINING_FOREGROUND = 2;
    public static final int STATE_NOT_STARTED = 0;
    public static final int STATE_PAUSED = 4;
    public static final int STATE_PAUSED_BUFFERING = 7;
    public static final int STATE_PLAYING = 3;
    public static final int STATE_SEEKING = 5;
    public static final int STATE_STOPPED = 12;
    public static final int STATE_SUPPRESSED = 9;
    public static final int STATE_SUPPRESSED_BUFFERING = 10;
    private final int mState;
    private final long mTimeSinceCreatedMillis;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes2.dex */
    public @interface State {
    }

    public static String stateToString(int value) {
        switch (value) {
            case 0:
                return "STATE_NOT_STARTED";
            case 1:
                return "STATE_JOINING_BACKGROUND";
            case 2:
                return "STATE_JOINING_FOREGROUND";
            case 3:
                return "STATE_PLAYING";
            case 4:
                return "STATE_PAUSED";
            case 5:
                return "STATE_SEEKING";
            case 6:
                return "STATE_BUFFERING";
            case 7:
                return "STATE_PAUSED_BUFFERING";
            case 8:
            default:
                return Integer.toHexString(value);
            case 9:
                return "STATE_SUPPRESSED";
            case 10:
                return "STATE_SUPPRESSED_BUFFERING";
            case 11:
                return "STATE_ENDED";
            case 12:
                return "STATE_STOPPED";
            case 13:
                return "STATE_FAILED";
            case 14:
                return "STATE_INTERRUPTED_BY_AD";
            case 15:
                return "STATE_ABANDONED";
        }
    }

    private PlaybackStateEvent(int state, long timeSinceCreatedMillis, Bundle extras) {
        this.mTimeSinceCreatedMillis = timeSinceCreatedMillis;
        this.mState = state;
        this.mMetricsBundle = extras.deepCopy();
    }

    public int getState() {
        return this.mState;
    }

    @Override // android.media.metrics.Event
    public long getTimeSinceCreatedMillis() {
        return this.mTimeSinceCreatedMillis;
    }

    @Override // android.media.metrics.Event
    public Bundle getMetricsBundle() {
        return this.mMetricsBundle;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PlaybackStateEvent that = (PlaybackStateEvent) o;
        if (this.mState == that.mState && this.mTimeSinceCreatedMillis == that.mTimeSinceCreatedMillis) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(Integer.valueOf(this.mState), Long.valueOf(this.mTimeSinceCreatedMillis));
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mState);
        dest.writeLong(this.mTimeSinceCreatedMillis);
        dest.writeBundle(this.mMetricsBundle);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    private PlaybackStateEvent(Parcel in) {
        int state = in.readInt();
        long timeSinceCreatedMillis = in.readLong();
        Bundle extras = in.readBundle();
        this.mState = state;
        this.mTimeSinceCreatedMillis = timeSinceCreatedMillis;
        this.mMetricsBundle = extras;
    }

    /* loaded from: classes2.dex */
    public static final class Builder {
        private int mState = 0;
        private long mTimeSinceCreatedMillis = -1;
        private Bundle mMetricsBundle = new Bundle();

        public Builder setState(int value) {
            this.mState = value;
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

        public PlaybackStateEvent build() {
            PlaybackStateEvent o = new PlaybackStateEvent(this.mState, this.mTimeSinceCreatedMillis, this.mMetricsBundle);
            return o;
        }
    }
}
