package android.hardware.lights;

import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
/* loaded from: classes2.dex */
public final class Light implements Parcelable {
    public static final Parcelable.Creator<Light> CREATOR = new Parcelable.Creator<Light>() { // from class: android.hardware.lights.Light.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public Light createFromParcel(Parcel in) {
            return new Light(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public Light[] newArray(int size) {
            return new Light[size];
        }
    };
    public static final int LIGHT_CAPABILITY_BRIGHTNESS = 1;
    public static final int LIGHT_CAPABILITY_COLOR_RGB = 2;
    @Deprecated
    public static final int LIGHT_CAPABILITY_RGB = 0;
    public static final int LIGHT_TYPE_CAMERA = 9;
    public static final int LIGHT_TYPE_INPUT = 10001;
    public static final int LIGHT_TYPE_KEYBOARD_BACKLIGHT = 10003;
    public static final int LIGHT_TYPE_MICROPHONE = 8;
    public static final int LIGHT_TYPE_PLAYER_ID = 10002;
    private final int mCapabilities;
    private final int mId;
    private final String mName;
    private final int mOrdinal;
    private final int mType;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes2.dex */
    public @interface LightCapability {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes2.dex */
    public @interface LightType {
    }

    public Light(int id, int ordinal, int type) {
        this(id, "Light", ordinal, type, 0);
    }

    public Light(int id, String name, int ordinal, int type, int capabilities) {
        this.mId = id;
        this.mName = name;
        this.mOrdinal = ordinal;
        this.mType = type;
        this.mCapabilities = capabilities;
    }

    private Light(Parcel in) {
        this.mId = in.readInt();
        this.mName = in.readString();
        this.mOrdinal = in.readInt();
        this.mType = in.readInt();
        this.mCapabilities = in.readInt();
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mId);
        dest.writeString(this.mName);
        dest.writeInt(this.mOrdinal);
        dest.writeInt(this.mType);
        dest.writeInt(this.mCapabilities);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    public boolean equals(Object obj) {
        if (obj instanceof Light) {
            Light light = (Light) obj;
            return this.mId == light.mId && this.mOrdinal == light.mOrdinal && this.mType == light.mType && this.mCapabilities == light.mCapabilities;
        }
        return false;
    }

    public int hashCode() {
        return this.mId;
    }

    public String toString() {
        return "[Name=" + this.mName + " Id=" + this.mId + " Type=" + this.mType + " Capabilities=" + this.mCapabilities + " Ordinal=" + this.mOrdinal + NavigationBarInflaterView.SIZE_MOD_END;
    }

    public int getId() {
        return this.mId;
    }

    public String getName() {
        return this.mName;
    }

    public int getOrdinal() {
        return this.mOrdinal;
    }

    public int getType() {
        return this.mType;
    }

    public int getCapabilities() {
        return this.mCapabilities;
    }

    public boolean hasBrightnessControl() {
        return (this.mCapabilities & 1) == 1;
    }

    public boolean hasRgbControl() {
        return (this.mCapabilities & 2) == 2;
    }
}
