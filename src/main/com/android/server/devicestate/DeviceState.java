package com.android.server.devicestate;

import com.android.internal.util.Preconditions;
import java.util.Objects;
/* loaded from: classes.dex */
public final class DeviceState {
    public final int mFlags;
    public final int mIdentifier;
    public final String mName;

    public DeviceState(int i, String str, int i2) {
        Preconditions.checkArgumentInRange(i, 0, 255, "identifier");
        this.mIdentifier = i;
        this.mName = str;
        this.mFlags = i2;
    }

    public int getIdentifier() {
        return this.mIdentifier;
    }

    public String getName() {
        return this.mName;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("DeviceState{identifier=");
        sb.append(this.mIdentifier);
        sb.append(", name='");
        sb.append(this.mName);
        sb.append('\'');
        sb.append(", app_accessible=");
        sb.append(!hasFlag(2));
        sb.append("}");
        return sb.toString();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || DeviceState.class != obj.getClass()) {
            return false;
        }
        DeviceState deviceState = (DeviceState) obj;
        return this.mIdentifier == deviceState.mIdentifier && Objects.equals(this.mName, deviceState.mName) && this.mFlags == deviceState.mFlags;
    }

    public int hashCode() {
        return Objects.hash(Integer.valueOf(this.mIdentifier), this.mName, Integer.valueOf(this.mFlags));
    }

    public boolean hasFlag(int i) {
        return (this.mFlags & i) == i;
    }
}
