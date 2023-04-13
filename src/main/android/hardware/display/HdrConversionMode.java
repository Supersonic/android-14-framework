package android.hardware.display;

import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.view.Display;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
/* loaded from: classes.dex */
public final class HdrConversionMode implements Parcelable {
    public static final Parcelable.Creator<HdrConversionMode> CREATOR = new Parcelable.Creator<HdrConversionMode>() { // from class: android.hardware.display.HdrConversionMode.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public HdrConversionMode createFromParcel(Parcel source) {
            return new HdrConversionMode(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public HdrConversionMode[] newArray(int size) {
            return new HdrConversionMode[size];
        }
    };
    public static final int HDR_CONVERSION_FORCE = 3;
    public static final int HDR_CONVERSION_PASSTHROUGH = 1;
    public static final int HDR_CONVERSION_SYSTEM = 2;
    public static final int HDR_CONVERSION_UNSUPPORTED = 0;
    private final int mConversionMode;
    private int mPreferredHdrOutputType;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface ConversionMode {
    }

    public HdrConversionMode(int conversionMode, int preferredHdrOutputType) {
        if ((conversionMode == 1 || conversionMode == 0) && preferredHdrOutputType != -1) {
            throw new IllegalArgumentException("preferredHdrOutputType must not be set if the conversion mode is " + hdrConversionModeString(conversionMode));
        }
        this.mConversionMode = conversionMode;
        this.mPreferredHdrOutputType = preferredHdrOutputType;
    }

    public HdrConversionMode(int conversionMode) {
        this.mConversionMode = conversionMode;
        this.mPreferredHdrOutputType = -1;
    }

    private HdrConversionMode(Parcel source) {
        this(source.readInt(), source.readInt());
    }

    public int getConversionMode() {
        return this.mConversionMode;
    }

    public int getPreferredHdrOutputType() {
        return this.mPreferredHdrOutputType;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mConversionMode);
        dest.writeInt(this.mPreferredHdrOutputType);
    }

    public boolean equals(Object o) {
        return (o instanceof HdrConversionMode) && equals((HdrConversionMode) o);
    }

    public int hashCode() {
        return 0;
    }

    public String toString() {
        return "HdrConversionMode{ConversionMode=" + hdrConversionModeString(getConversionMode()) + ", PreferredHdrOutputType=" + Display.HdrCapabilities.hdrTypeToString(getPreferredHdrOutputType()) + "}";
    }

    private boolean equals(HdrConversionMode other) {
        return other != null && this.mConversionMode == other.getConversionMode() && this.mPreferredHdrOutputType == other.getPreferredHdrOutputType();
    }

    private static String hdrConversionModeString(int hdrConversionMode) {
        switch (hdrConversionMode) {
            case 1:
                return "HDR_CONVERSION_PASSTHROUGH";
            case 2:
                return "HDR_CONVERSION_SYSTEM";
            case 3:
                return "HDR_CONVERSION_FORCE";
            default:
                return "HDR_CONVERSION_UNSUPPORTED";
        }
    }
}
