package android.net;

import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Objects;
import java.util.StringJoiner;
/* loaded from: classes2.dex */
public final class VpnProfileState implements Parcelable {
    public static final Parcelable.Creator<VpnProfileState> CREATOR = new Parcelable.Creator<VpnProfileState>() { // from class: android.net.VpnProfileState.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public VpnProfileState createFromParcel(Parcel in) {
            return new VpnProfileState(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public VpnProfileState[] newArray(int size) {
            return new VpnProfileState[size];
        }
    };
    public static final int STATE_CONNECTED = 2;
    public static final int STATE_CONNECTING = 1;
    public static final int STATE_DISCONNECTED = 0;
    public static final int STATE_FAILED = 3;
    private final boolean mAlwaysOn;
    private final boolean mLockdown;
    private final String mSessionKey;
    private final int mState;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes2.dex */
    public @interface State {
    }

    public VpnProfileState(int state, String sessionKey, boolean alwaysOn, boolean lockdown) {
        this.mState = state;
        this.mSessionKey = sessionKey;
        this.mAlwaysOn = alwaysOn;
        this.mLockdown = lockdown;
    }

    public int getState() {
        return this.mState;
    }

    public String getSessionId() {
        return this.mSessionKey;
    }

    public boolean isAlwaysOn() {
        return this.mAlwaysOn;
    }

    public boolean isLockdownEnabled() {
        return this.mLockdown;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(this.mState);
        out.writeString(this.mSessionKey);
        out.writeBoolean(this.mAlwaysOn);
        out.writeBoolean(this.mLockdown);
    }

    private VpnProfileState(Parcel in) {
        this.mState = in.readInt();
        this.mSessionKey = in.readString();
        this.mAlwaysOn = in.readBoolean();
        this.mLockdown = in.readBoolean();
    }

    private String convertStateToString(int state) {
        switch (state) {
            case 0:
                return "DISCONNECTED";
            case 1:
                return "CONNECTING";
            case 2:
                return "CONNECTED";
            case 3:
                return "FAILED";
            default:
                return "UNKNOWN";
        }
    }

    public String toString() {
        StringJoiner resultJoiner = new StringJoiner(", ", "{", "}");
        resultJoiner.add("State: " + convertStateToString(getState()));
        resultJoiner.add("SessionId: " + getSessionId());
        resultJoiner.add("Always-on: " + isAlwaysOn());
        resultJoiner.add("Lockdown: " + isLockdownEnabled());
        return resultJoiner.toString();
    }

    public boolean equals(Object obj) {
        if (obj instanceof VpnProfileState) {
            VpnProfileState that = (VpnProfileState) obj;
            return getState() == that.getState() && Objects.equals(getSessionId(), that.getSessionId()) && isAlwaysOn() == that.isAlwaysOn() && isLockdownEnabled() == that.isLockdownEnabled();
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(Integer.valueOf(getState()), getSessionId(), Boolean.valueOf(isAlwaysOn()), Boolean.valueOf(isLockdownEnabled()));
    }
}
