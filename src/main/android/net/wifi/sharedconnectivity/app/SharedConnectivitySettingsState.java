package android.net.wifi.sharedconnectivity.app;

import android.annotation.SystemApi;
import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.p008os.Bundle;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.util.Objects;
@SystemApi
/* loaded from: classes2.dex */
public final class SharedConnectivitySettingsState implements Parcelable {
    public static final Parcelable.Creator<SharedConnectivitySettingsState> CREATOR = new Parcelable.Creator<SharedConnectivitySettingsState>() { // from class: android.net.wifi.sharedconnectivity.app.SharedConnectivitySettingsState.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public SharedConnectivitySettingsState createFromParcel(Parcel in) {
            return SharedConnectivitySettingsState.readFromParcel(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public SharedConnectivitySettingsState[] newArray(int size) {
            return new SharedConnectivitySettingsState[size];
        }
    };
    private final Bundle mExtras;
    private final boolean mInstantTetherEnabled;

    /* loaded from: classes2.dex */
    public static final class Builder {
        private Bundle mExtras;
        private boolean mInstantTetherEnabled;

        public Builder setInstantTetherEnabled(boolean instantTetherEnabled) {
            this.mInstantTetherEnabled = instantTetherEnabled;
            return this;
        }

        public Builder setExtras(Bundle extras) {
            this.mExtras = extras;
            return this;
        }

        public SharedConnectivitySettingsState build() {
            return new SharedConnectivitySettingsState(this.mInstantTetherEnabled, this.mExtras);
        }
    }

    private SharedConnectivitySettingsState(boolean instantTetherEnabled, Bundle extras) {
        this.mInstantTetherEnabled = instantTetherEnabled;
        this.mExtras = extras;
    }

    public boolean isInstantTetherEnabled() {
        return this.mInstantTetherEnabled;
    }

    public Bundle getExtras() {
        return this.mExtras;
    }

    public boolean equals(Object obj) {
        if (obj instanceof SharedConnectivitySettingsState) {
            SharedConnectivitySettingsState other = (SharedConnectivitySettingsState) obj;
            return this.mInstantTetherEnabled == other.isInstantTetherEnabled();
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(Boolean.valueOf(this.mInstantTetherEnabled));
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeBoolean(this.mInstantTetherEnabled);
        dest.writeBundle(this.mExtras);
    }

    public static SharedConnectivitySettingsState readFromParcel(Parcel in) {
        return new SharedConnectivitySettingsState(in.readBoolean(), in.readBundle());
    }

    public String toString() {
        return "SharedConnectivitySettingsState[instantTetherEnabled=" + this.mInstantTetherEnabled + "extras=" + this.mExtras.toString() + NavigationBarInflaterView.SIZE_MOD_END;
    }
}
