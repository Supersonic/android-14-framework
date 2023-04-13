package android.hardware.input;

import android.annotation.SystemApi;
import android.hardware.input.VirtualInputDeviceConfig;
import android.icu.util.ULocale;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.util.Objects;
@SystemApi
/* loaded from: classes2.dex */
public final class VirtualKeyboardConfig extends VirtualInputDeviceConfig implements Parcelable {
    public static final Parcelable.Creator<VirtualKeyboardConfig> CREATOR = new Parcelable.Creator<VirtualKeyboardConfig>() { // from class: android.hardware.input.VirtualKeyboardConfig.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public VirtualKeyboardConfig createFromParcel(Parcel in) {
            return new VirtualKeyboardConfig(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public VirtualKeyboardConfig[] newArray(int size) {
            return new VirtualKeyboardConfig[size];
        }
    };
    public static final String DEFAULT_LANGUAGE_TAG = "en-Latn-US";
    public static final String DEFAULT_LAYOUT_TYPE = "qwerty";
    private final String mLanguageTag;
    private final String mLayoutType;

    private VirtualKeyboardConfig(Builder builder) {
        super(builder);
        this.mLanguageTag = builder.mLanguageTag;
        this.mLayoutType = builder.mLayoutType;
    }

    private VirtualKeyboardConfig(Parcel in) {
        super(in);
        this.mLanguageTag = in.readString8();
        this.mLayoutType = in.readString8();
    }

    public String getLanguageTag() {
        return this.mLanguageTag;
    }

    public String getLayoutType() {
        return this.mLayoutType;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.hardware.input.VirtualInputDeviceConfig, android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString8(this.mLanguageTag);
        dest.writeString8(this.mLayoutType);
    }

    /* loaded from: classes2.dex */
    public static final class Builder extends VirtualInputDeviceConfig.Builder<Builder> {
        private String mLanguageTag = VirtualKeyboardConfig.DEFAULT_LANGUAGE_TAG;
        private String mLayoutType = "qwerty";

        public Builder setLanguageTag(String languageTag) {
            Objects.requireNonNull(languageTag, "languageTag cannot be null");
            ULocale locale = ULocale.forLanguageTag(languageTag);
            if (locale.getLanguage().isEmpty() || locale.getCountry().isEmpty()) {
                throw new IllegalArgumentException("The language tag is not valid.");
            }
            this.mLanguageTag = ULocale.createCanonical(locale).toLanguageTag();
            return this;
        }

        public Builder setLayoutType(String layoutType) {
            Objects.requireNonNull(layoutType, "layoutType cannot be null");
            this.mLayoutType = layoutType;
            return this;
        }

        public VirtualKeyboardConfig build() {
            return new VirtualKeyboardConfig(this);
        }
    }
}
