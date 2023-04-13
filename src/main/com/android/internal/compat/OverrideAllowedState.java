package com.android.internal.compat;

import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
/* loaded from: classes4.dex */
public final class OverrideAllowedState implements Parcelable {
    public static final int ALLOWED = 0;
    public static final Parcelable.Creator<OverrideAllowedState> CREATOR = new Parcelable.Creator<OverrideAllowedState>() { // from class: com.android.internal.compat.OverrideAllowedState.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public OverrideAllowedState createFromParcel(Parcel parcel) {
            OverrideAllowedState info = new OverrideAllowedState(parcel);
            return info;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public OverrideAllowedState[] newArray(int size) {
            return new OverrideAllowedState[size];
        }
    };
    public static final int DEFERRED_VERIFICATION = 4;
    public static final int DISABLED_NON_TARGET_SDK = 2;
    public static final int DISABLED_NOT_DEBUGGABLE = 1;
    public static final int DISABLED_TARGET_SDK_TOO_HIGH = 3;
    public static final int LOGGING_ONLY_CHANGE = 5;
    public static final int PLATFORM_TOO_OLD = 6;
    public final int appTargetSdk;
    public final int changeIdTargetSdk;
    public final int state;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes4.dex */
    public @interface State {
    }

    private OverrideAllowedState(Parcel parcel) {
        this.state = parcel.readInt();
        this.appTargetSdk = parcel.readInt();
        this.changeIdTargetSdk = parcel.readInt();
    }

    public OverrideAllowedState(int state, int appTargetSdk, int changeIdTargetSdk) {
        this.state = state;
        this.appTargetSdk = appTargetSdk;
        this.changeIdTargetSdk = changeIdTargetSdk;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(this.state);
        out.writeInt(this.appTargetSdk);
        out.writeInt(this.changeIdTargetSdk);
    }

    public void enforce(long changeId, String packageName) throws SecurityException {
        switch (this.state) {
            case 0:
            case 4:
                return;
            case 1:
                throw new SecurityException("Cannot override a change on a non-debuggable app and user build.");
            case 2:
                throw new SecurityException("Cannot override a default enabled/disabled change on a user build.");
            case 3:
                throw new SecurityException(String.format("Cannot override %1$d for %2$s because the app's targetSdk (%3$d) is above the change's targetSdk threshold (%4$d)", Long.valueOf(changeId), packageName, Integer.valueOf(this.appTargetSdk), Integer.valueOf(this.changeIdTargetSdk)));
            case 5:
                throw new SecurityException(String.format("Cannot override %1$d because it is marked as a logging-only change.", Long.valueOf(changeId)));
            case 6:
                throw new SecurityException(String.format("Cannot override %1$d for %2$s because the change's targetSdk threshold (%3$d) is above the platform sdk.", Long.valueOf(changeId), packageName, Integer.valueOf(this.changeIdTargetSdk)));
            default:
                return;
        }
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || !(obj instanceof OverrideAllowedState)) {
            return false;
        }
        OverrideAllowedState otherState = (OverrideAllowedState) obj;
        if (this.state == otherState.state && this.appTargetSdk == otherState.appTargetSdk && this.changeIdTargetSdk == otherState.changeIdTargetSdk) {
            return true;
        }
        return false;
    }

    private String stateName() {
        switch (this.state) {
            case 0:
                return "ALLOWED";
            case 1:
                return "DISABLED_NOT_DEBUGGABLE";
            case 2:
                return "DISABLED_NON_TARGET_SDK";
            case 3:
                return "DISABLED_TARGET_SDK_TOO_HIGH";
            case 4:
                return "DEFERRED_VERIFICATION";
            case 5:
                return "LOGGING_ONLY_CHANGE";
            case 6:
                return "PLATFORM_TOO_OLD";
            default:
                return "UNKNOWN";
        }
    }

    public String toString() {
        return "OverrideAllowedState(state=" + stateName() + "; appTargetSdk=" + this.appTargetSdk + "; changeIdTargetSdk=" + this.changeIdTargetSdk + NavigationBarInflaterView.KEY_CODE_END;
    }
}
