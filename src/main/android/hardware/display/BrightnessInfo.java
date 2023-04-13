package android.hardware.display;

import android.hardware.Camera;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.p008os.PowerManager;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
/* loaded from: classes.dex */
public final class BrightnessInfo implements Parcelable {
    public static final int BRIGHTNESS_MAX_REASON_NONE = 0;
    public static final int BRIGHTNESS_MAX_REASON_THERMAL = 1;
    public static final Parcelable.Creator<BrightnessInfo> CREATOR = new Parcelable.Creator<BrightnessInfo>() { // from class: android.hardware.display.BrightnessInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public BrightnessInfo createFromParcel(Parcel source) {
            return new BrightnessInfo(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public BrightnessInfo[] newArray(int size) {
            return new BrightnessInfo[size];
        }
    };
    public static final int HIGH_BRIGHTNESS_MODE_HDR = 2;
    public static final int HIGH_BRIGHTNESS_MODE_OFF = 0;
    public static final int HIGH_BRIGHTNESS_MODE_SUNLIGHT = 1;
    public final float adjustedBrightness;
    public final float brightness;
    public final int brightnessMaxReason;
    public final float brightnessMaximum;
    public final float brightnessMinimum;
    public final int highBrightnessMode;
    public final float highBrightnessTransitionPoint;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface BrightnessMaxReason {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface HighBrightnessMode {
    }

    public BrightnessInfo(float brightness, float brightnessMinimum, float brightnessMaximum, int highBrightnessMode, float highBrightnessTransitionPoint, int brightnessMaxReason) {
        this(brightness, brightness, brightnessMinimum, brightnessMaximum, highBrightnessMode, highBrightnessTransitionPoint, brightnessMaxReason);
    }

    public BrightnessInfo(float brightness, float adjustedBrightness, float brightnessMinimum, float brightnessMaximum, int highBrightnessMode, float highBrightnessTransitionPoint, int brightnessMaxReason) {
        this.brightness = brightness;
        this.adjustedBrightness = adjustedBrightness;
        this.brightnessMinimum = brightnessMinimum;
        this.brightnessMaximum = brightnessMaximum;
        this.highBrightnessMode = highBrightnessMode;
        this.highBrightnessTransitionPoint = highBrightnessTransitionPoint;
        this.brightnessMaxReason = brightnessMaxReason;
    }

    public static String hbmToString(int highBrightnessMode) {
        switch (highBrightnessMode) {
            case 0:
                return "off";
            case 1:
                return "sunlight";
            case 2:
                return Camera.Parameters.SCENE_MODE_HDR;
            default:
                return "invalid";
        }
    }

    public static String briMaxReasonToString(int reason) {
        switch (reason) {
            case 0:
                return "none";
            case 1:
                return PowerManager.SHUTDOWN_THERMAL_STATE;
            default:
                return "invalid";
        }
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloat(this.brightness);
        dest.writeFloat(this.adjustedBrightness);
        dest.writeFloat(this.brightnessMinimum);
        dest.writeFloat(this.brightnessMaximum);
        dest.writeInt(this.highBrightnessMode);
        dest.writeFloat(this.highBrightnessTransitionPoint);
        dest.writeInt(this.brightnessMaxReason);
    }

    private BrightnessInfo(Parcel source) {
        this.brightness = source.readFloat();
        this.adjustedBrightness = source.readFloat();
        this.brightnessMinimum = source.readFloat();
        this.brightnessMaximum = source.readFloat();
        this.highBrightnessMode = source.readInt();
        this.highBrightnessTransitionPoint = source.readFloat();
        this.brightnessMaxReason = source.readInt();
    }
}
