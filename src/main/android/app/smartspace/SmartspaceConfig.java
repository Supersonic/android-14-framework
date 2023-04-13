package android.app.smartspace;

import android.annotation.SystemApi;
import android.content.Context;
import android.p008os.Bundle;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.util.Objects;
@SystemApi
/* loaded from: classes.dex */
public final class SmartspaceConfig implements Parcelable {
    public static final Parcelable.Creator<SmartspaceConfig> CREATOR = new Parcelable.Creator<SmartspaceConfig>() { // from class: android.app.smartspace.SmartspaceConfig.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public SmartspaceConfig createFromParcel(Parcel parcel) {
            return new SmartspaceConfig(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public SmartspaceConfig[] newArray(int size) {
            return new SmartspaceConfig[size];
        }
    };
    private final Bundle mExtras;
    private String mPackageName;
    private final int mSmartspaceTargetCount;
    private final String mUiSurface;

    private SmartspaceConfig(String uiSurface, int numPredictedTargets, String packageName, Bundle extras) {
        this.mUiSurface = uiSurface;
        this.mSmartspaceTargetCount = numPredictedTargets;
        this.mPackageName = packageName;
        this.mExtras = extras;
    }

    private SmartspaceConfig(Parcel parcel) {
        this.mUiSurface = parcel.readString();
        this.mSmartspaceTargetCount = parcel.readInt();
        this.mPackageName = parcel.readString();
        this.mExtras = parcel.readBundle();
    }

    public String getPackageName() {
        return this.mPackageName;
    }

    public int getSmartspaceTargetCount() {
        return this.mSmartspaceTargetCount;
    }

    public String getUiSurface() {
        return this.mUiSurface;
    }

    public Bundle getExtras() {
        return this.mExtras;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mUiSurface);
        dest.writeInt(this.mSmartspaceTargetCount);
        dest.writeString(this.mPackageName);
        dest.writeBundle(this.mExtras);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SmartspaceConfig that = (SmartspaceConfig) o;
        if (this.mSmartspaceTargetCount == that.mSmartspaceTargetCount && Objects.equals(this.mUiSurface, that.mUiSurface) && Objects.equals(this.mPackageName, that.mPackageName) && Objects.equals(this.mExtras, that.mExtras)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(Integer.valueOf(this.mSmartspaceTargetCount), this.mUiSurface, this.mPackageName, this.mExtras);
    }

    @SystemApi
    /* loaded from: classes.dex */
    public static final class Builder {
        private final String mPackageName;
        private final String mUiSurface;
        private int mSmartspaceTargetCount = 5;
        private Bundle mExtras = Bundle.EMPTY;

        @SystemApi
        public Builder(Context context, String uiSurface) {
            this.mPackageName = context.getPackageName();
            this.mUiSurface = uiSurface;
        }

        public Builder setSmartspaceTargetCount(int smartspaceTargetCount) {
            this.mSmartspaceTargetCount = smartspaceTargetCount;
            return this;
        }

        public Builder setExtras(Bundle extras) {
            this.mExtras = extras;
            return this;
        }

        public SmartspaceConfig build() {
            return new SmartspaceConfig(this.mUiSurface, this.mSmartspaceTargetCount, this.mPackageName, this.mExtras);
        }
    }
}
