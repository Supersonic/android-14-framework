package android.media.projection;

import android.annotation.IntRange;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import com.android.internal.util.AnnotationValidations;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
/* loaded from: classes2.dex */
public final class MediaProjectionConfig implements Parcelable {
    public static final int CAPTURE_REGION_FIXED_DISPLAY = 1;
    public static final int CAPTURE_REGION_USER_CHOICE = 0;
    public static final Parcelable.Creator<MediaProjectionConfig> CREATOR = new Parcelable.Creator<MediaProjectionConfig>() { // from class: android.media.projection.MediaProjectionConfig.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public MediaProjectionConfig[] newArray(int size) {
            return new MediaProjectionConfig[size];
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public MediaProjectionConfig createFromParcel(Parcel in) {
            return new MediaProjectionConfig(in);
        }
    };
    private int mDisplayToCapture;
    private int mRegionToCapture;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes2.dex */
    public @interface CaptureRegion {
    }

    private MediaProjectionConfig() {
        this.mRegionToCapture = 0;
    }

    private MediaProjectionConfig(int captureRegion) {
        this.mRegionToCapture = 0;
        this.mRegionToCapture = captureRegion;
    }

    public static MediaProjectionConfig createConfigForDefaultDisplay() {
        MediaProjectionConfig config = new MediaProjectionConfig(1);
        config.mDisplayToCapture = 0;
        return config;
    }

    public static MediaProjectionConfig createConfigForUserChoice() {
        return new MediaProjectionConfig(0);
    }

    private static String captureRegionToString(int value) {
        switch (value) {
            case 0:
                return "CAPTURE_REGION_USERS_CHOICE";
            case 1:
                return "CAPTURE_REGION_GIVEN_DISPLAY";
            default:
                return Integer.toHexString(value);
        }
    }

    public String toString() {
        return "MediaProjectionConfig { displayToCapture = " + this.mDisplayToCapture + ", regionToCapture = " + captureRegionToString(this.mRegionToCapture) + " }";
    }

    public int getDisplayToCapture() {
        return this.mDisplayToCapture;
    }

    public int getRegionToCapture() {
        return this.mRegionToCapture;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MediaProjectionConfig that = (MediaProjectionConfig) o;
        if (this.mDisplayToCapture == that.mDisplayToCapture && this.mRegionToCapture == that.mRegionToCapture) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        int _hash = (1 * 31) + this.mDisplayToCapture;
        return (_hash * 31) + this.mRegionToCapture;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mDisplayToCapture);
        dest.writeInt(this.mRegionToCapture);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    MediaProjectionConfig(Parcel in) {
        this.mRegionToCapture = 0;
        int displayToCapture = in.readInt();
        int regionToCapture = in.readInt();
        this.mDisplayToCapture = displayToCapture;
        AnnotationValidations.validate((Class<IntRange>) IntRange.class, (IntRange) null, displayToCapture, "from", 0L, "to", 0L);
        this.mRegionToCapture = regionToCapture;
        AnnotationValidations.validate((Class<? extends Annotation>) CaptureRegion.class, (Annotation) null, regionToCapture);
    }

    @Deprecated
    private void __metadata() {
    }
}
