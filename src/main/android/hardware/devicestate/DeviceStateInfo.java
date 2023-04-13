package android.hardware.devicestate;

import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Arrays;
import java.util.Objects;
/* loaded from: classes.dex */
public final class DeviceStateInfo implements Parcelable {
    public static final int CHANGED_BASE_STATE = 2;
    public static final int CHANGED_CURRENT_STATE = 4;
    public static final int CHANGED_SUPPORTED_STATES = 1;
    public static final Parcelable.Creator<DeviceStateInfo> CREATOR = new Parcelable.Creator<DeviceStateInfo>() { // from class: android.hardware.devicestate.DeviceStateInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public DeviceStateInfo createFromParcel(Parcel source) {
            int numberOfSupportedStates = source.readInt();
            int[] supportedStates = new int[numberOfSupportedStates];
            for (int i = 0; i < numberOfSupportedStates; i++) {
                supportedStates[i] = source.readInt();
            }
            int baseState = source.readInt();
            int currentState = source.readInt();
            return new DeviceStateInfo(supportedStates, baseState, currentState);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public DeviceStateInfo[] newArray(int size) {
            return new DeviceStateInfo[size];
        }
    };
    public final int baseState;
    public final int currentState;
    public final int[] supportedStates;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface ChangeFlags {
    }

    public DeviceStateInfo(int[] supportedStates, int baseState, int state) {
        this.supportedStates = supportedStates;
        this.baseState = baseState;
        this.currentState = state;
    }

    /* JADX WARN: Illegal instructions before constructor call */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public DeviceStateInfo(DeviceStateInfo info) {
        this(Arrays.copyOf(r0, r0.length), info.baseState, info.currentState);
        int[] iArr = info.supportedStates;
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        DeviceStateInfo that = (DeviceStateInfo) other;
        if (this.baseState == that.baseState && this.currentState == that.currentState && Arrays.equals(this.supportedStates, that.supportedStates)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        int result = Objects.hash(Integer.valueOf(this.baseState), Integer.valueOf(this.currentState));
        return (result * 31) + Arrays.hashCode(this.supportedStates);
    }

    public int diff(DeviceStateInfo other) {
        int diff = 0;
        if (!Arrays.equals(this.supportedStates, other.supportedStates)) {
            diff = 0 | 1;
        }
        if (this.baseState != other.baseState) {
            diff |= 2;
        }
        if (this.currentState != other.currentState) {
            return diff | 4;
        }
        return diff;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.supportedStates.length);
        int i = 0;
        while (true) {
            int[] iArr = this.supportedStates;
            if (i < iArr.length) {
                dest.writeInt(iArr[i]);
                i++;
            } else {
                int i2 = this.baseState;
                dest.writeInt(i2);
                dest.writeInt(this.currentState);
                return;
            }
        }
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }
}
