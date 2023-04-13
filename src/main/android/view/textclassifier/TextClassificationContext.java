package android.view.textclassifier;

import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.util.Locale;
import java.util.Objects;
/* loaded from: classes4.dex */
public final class TextClassificationContext implements Parcelable {
    public static final Parcelable.Creator<TextClassificationContext> CREATOR = new Parcelable.Creator<TextClassificationContext>() { // from class: android.view.textclassifier.TextClassificationContext.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public TextClassificationContext createFromParcel(Parcel parcel) {
            return new TextClassificationContext(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public TextClassificationContext[] newArray(int size) {
            return new TextClassificationContext[size];
        }
    };
    private String mPackageName;
    private SystemTextClassifierMetadata mSystemTcMetadata;
    private final String mWidgetType;
    private final String mWidgetVersion;

    private TextClassificationContext(String packageName, String widgetType, String widgetVersion) {
        this.mPackageName = (String) Objects.requireNonNull(packageName);
        this.mWidgetType = (String) Objects.requireNonNull(widgetType);
        this.mWidgetVersion = widgetVersion;
    }

    public String getPackageName() {
        return this.mPackageName;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setSystemTextClassifierMetadata(SystemTextClassifierMetadata systemTcMetadata) {
        this.mSystemTcMetadata = systemTcMetadata;
    }

    public SystemTextClassifierMetadata getSystemTextClassifierMetadata() {
        return this.mSystemTcMetadata;
    }

    public String getWidgetType() {
        return this.mWidgetType;
    }

    public String getWidgetVersion() {
        return this.mWidgetVersion;
    }

    public String toString() {
        return String.format(Locale.US, "TextClassificationContext{packageName=%s, widgetType=%s, widgetVersion=%s, systemTcMetadata=%s}", this.mPackageName, this.mWidgetType, this.mWidgetVersion, this.mSystemTcMetadata);
    }

    /* loaded from: classes4.dex */
    public static final class Builder {
        private final String mPackageName;
        private final String mWidgetType;
        private String mWidgetVersion;

        public Builder(String packageName, String widgetType) {
            this.mPackageName = (String) Objects.requireNonNull(packageName);
            this.mWidgetType = (String) Objects.requireNonNull(widgetType);
        }

        public Builder setWidgetVersion(String widgetVersion) {
            this.mWidgetVersion = widgetVersion;
            return this;
        }

        public TextClassificationContext build() {
            return new TextClassificationContext(this.mPackageName, this.mWidgetType, this.mWidgetVersion);
        }
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(this.mPackageName);
        parcel.writeString(this.mWidgetType);
        parcel.writeString(this.mWidgetVersion);
        parcel.writeParcelable(this.mSystemTcMetadata, flags);
    }

    private TextClassificationContext(Parcel in) {
        this.mPackageName = in.readString();
        this.mWidgetType = in.readString();
        this.mWidgetVersion = in.readString();
        this.mSystemTcMetadata = (SystemTextClassifierMetadata) in.readParcelable(null, SystemTextClassifierMetadata.class);
    }
}
