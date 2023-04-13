package com.android.internal.compat;

import android.app.compat.PackageOverride;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.util.HashMap;
import java.util.Map;
/* loaded from: classes4.dex */
public final class CompatibilityOverrideConfig implements Parcelable {
    public static final Parcelable.Creator<CompatibilityOverrideConfig> CREATOR = new Parcelable.Creator<CompatibilityOverrideConfig>() { // from class: com.android.internal.compat.CompatibilityOverrideConfig.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public CompatibilityOverrideConfig createFromParcel(Parcel in) {
            return new CompatibilityOverrideConfig(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public CompatibilityOverrideConfig[] newArray(int size) {
            return new CompatibilityOverrideConfig[size];
        }
    };
    public final Map<Long, PackageOverride> overrides;

    public CompatibilityOverrideConfig(Map<Long, PackageOverride> overrides) {
        this.overrides = overrides;
    }

    private CompatibilityOverrideConfig(Parcel in) {
        int keyCount = in.readInt();
        this.overrides = new HashMap();
        for (int i = 0; i < keyCount; i++) {
            long key = in.readLong();
            this.overrides.put(Long.valueOf(key), PackageOverride.createFromParcel(in));
        }
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.overrides.size());
        for (Long key : this.overrides.keySet()) {
            dest.writeLong(key.longValue());
            this.overrides.get(key).writeToParcel(dest);
        }
    }
}
