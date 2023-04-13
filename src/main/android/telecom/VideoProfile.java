package android.telecom;

import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
/* loaded from: classes3.dex */
public class VideoProfile implements Parcelable {
    public static final Parcelable.Creator<VideoProfile> CREATOR = new Parcelable.Creator<VideoProfile>() { // from class: android.telecom.VideoProfile.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public VideoProfile createFromParcel(Parcel source) {
            int state = source.readInt();
            int quality = source.readInt();
            VideoProfile.class.getClassLoader();
            return new VideoProfile(state, quality);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public VideoProfile[] newArray(int size) {
            return new VideoProfile[size];
        }
    };
    public static final int QUALITY_DEFAULT = 4;
    public static final int QUALITY_HIGH = 1;
    public static final int QUALITY_LOW = 3;
    public static final int QUALITY_MEDIUM = 2;
    public static final int QUALITY_UNKNOWN = 0;
    public static final int STATE_AUDIO_ONLY = 0;
    public static final int STATE_BIDIRECTIONAL = 3;
    public static final int STATE_PAUSED = 4;
    public static final int STATE_RX_ENABLED = 2;
    public static final int STATE_TX_ENABLED = 1;
    private final int mQuality;
    private final int mVideoState;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface VideoQuality {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface VideoState {
    }

    public VideoProfile(int videoState) {
        this(videoState, 4);
    }

    public VideoProfile(int videoState, int quality) {
        this.mVideoState = videoState;
        this.mQuality = quality;
    }

    public int getVideoState() {
        return this.mVideoState;
    }

    public int getQuality() {
        return this.mQuality;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mVideoState);
        dest.writeInt(this.mQuality);
    }

    public String toString() {
        return "[VideoProfile videoState = " + videoStateToString(this.mVideoState) + " videoQuality = " + this.mQuality + NavigationBarInflaterView.SIZE_MOD_END;
    }

    public static String videoStateToString(int videoState) {
        StringBuilder sb = new StringBuilder();
        sb.append("Audio");
        if (videoState == 0) {
            sb.append(" Only");
        } else {
            if (isTransmissionEnabled(videoState)) {
                sb.append(" Tx");
            }
            if (isReceptionEnabled(videoState)) {
                sb.append(" Rx");
            }
            if (isPaused(videoState)) {
                sb.append(" Pause");
            }
        }
        return sb.toString();
    }

    public static boolean isAudioOnly(int videoState) {
        return (hasState(videoState, 1) || hasState(videoState, 2)) ? false : true;
    }

    public static boolean isVideo(int videoState) {
        return hasState(videoState, 1) || hasState(videoState, 2) || hasState(videoState, 3);
    }

    public static boolean isTransmissionEnabled(int videoState) {
        return hasState(videoState, 1);
    }

    public static boolean isReceptionEnabled(int videoState) {
        return hasState(videoState, 2);
    }

    public static boolean isBidirectional(int videoState) {
        return hasState(videoState, 3);
    }

    public static boolean isPaused(int videoState) {
        return hasState(videoState, 4);
    }

    private static boolean hasState(int videoState, int state) {
        return (videoState & state) == state;
    }

    /* loaded from: classes3.dex */
    public static final class CameraCapabilities implements Parcelable {
        public static final Parcelable.Creator<CameraCapabilities> CREATOR = new Parcelable.Creator<CameraCapabilities>() { // from class: android.telecom.VideoProfile.CameraCapabilities.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public CameraCapabilities createFromParcel(Parcel source) {
                int width = source.readInt();
                int height = source.readInt();
                boolean supportsZoom = source.readByte() != 0;
                float maxZoom = source.readFloat();
                return new CameraCapabilities(width, height, supportsZoom, maxZoom);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public CameraCapabilities[] newArray(int size) {
                return new CameraCapabilities[size];
            }
        };
        private final int mHeight;
        private final float mMaxZoom;
        private final int mWidth;
        private final boolean mZoomSupported;

        public CameraCapabilities(int width, int height) {
            this(width, height, false, 1.0f);
        }

        public CameraCapabilities(int width, int height, boolean zoomSupported, float maxZoom) {
            this.mWidth = width;
            this.mHeight = height;
            this.mZoomSupported = zoomSupported;
            this.mMaxZoom = maxZoom;
        }

        @Override // android.p008os.Parcelable
        public int describeContents() {
            return 0;
        }

        @Override // android.p008os.Parcelable
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(getWidth());
            dest.writeInt(getHeight());
            dest.writeByte(isZoomSupported() ? (byte) 1 : (byte) 0);
            dest.writeFloat(getMaxZoom());
        }

        public int getWidth() {
            return this.mWidth;
        }

        public int getHeight() {
            return this.mHeight;
        }

        public boolean isZoomSupported() {
            return this.mZoomSupported;
        }

        public float getMaxZoom() {
            return this.mMaxZoom;
        }
    }
}
