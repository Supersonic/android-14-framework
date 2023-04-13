package com.android.internal.compat;

import android.compat.Compatibility;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.util.HashSet;
import java.util.Set;
/* loaded from: classes4.dex */
public final class CompatibilityChangeConfig implements Parcelable {
    public static final Parcelable.Creator<CompatibilityChangeConfig> CREATOR = new Parcelable.Creator<CompatibilityChangeConfig>() { // from class: com.android.internal.compat.CompatibilityChangeConfig.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public CompatibilityChangeConfig createFromParcel(Parcel in) {
            return new CompatibilityChangeConfig(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public CompatibilityChangeConfig[] newArray(int size) {
            return new CompatibilityChangeConfig[size];
        }
    };
    private final Compatibility.ChangeConfig mChangeConfig;

    public CompatibilityChangeConfig(Compatibility.ChangeConfig changeConfig) {
        this.mChangeConfig = changeConfig;
    }

    public Set<Long> enabledChanges() {
        return this.mChangeConfig.getEnabledSet();
    }

    public Set<Long> disabledChanges() {
        return this.mChangeConfig.getDisabledSet();
    }

    public boolean isChangeEnabled(long changeId) {
        if (this.mChangeConfig.isForceEnabled(changeId)) {
            return true;
        }
        if (this.mChangeConfig.isForceDisabled(changeId)) {
            return false;
        }
        throw new IllegalStateException("Change " + changeId + " is not defined.");
    }

    private CompatibilityChangeConfig(Parcel in) {
        long[] enabledArray = in.createLongArray();
        long[] disabledArray = in.createLongArray();
        Set<Long> enabled = toLongSet(enabledArray);
        Set<Long> disabled = toLongSet(disabledArray);
        this.mChangeConfig = new Compatibility.ChangeConfig(enabled, disabled);
    }

    private static Set<Long> toLongSet(long[] values) {
        Set<Long> ret = new HashSet<>();
        for (long value : values) {
            ret.add(Long.valueOf(value));
        }
        return ret;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        long[] enabled = this.mChangeConfig.getEnabledChangesArray();
        long[] disabled = this.mChangeConfig.getDisabledChangesArray();
        dest.writeLongArray(enabled);
        dest.writeLongArray(disabled);
    }
}
