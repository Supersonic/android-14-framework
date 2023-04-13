package com.android.internal.compat;

import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.p008os.Parcel;
import android.p008os.Parcelable;
/* loaded from: classes4.dex */
public class CompatibilityChangeInfo implements Parcelable {
    public static final Parcelable.Creator<CompatibilityChangeInfo> CREATOR = new Parcelable.Creator<CompatibilityChangeInfo>() { // from class: com.android.internal.compat.CompatibilityChangeInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public CompatibilityChangeInfo createFromParcel(Parcel in) {
            return new CompatibilityChangeInfo(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public CompatibilityChangeInfo[] newArray(int size) {
            return new CompatibilityChangeInfo[size];
        }
    };
    private final long mChangeId;
    private final String mDescription;
    private final boolean mDisabled;
    private final int mEnableSinceTargetSdk;
    private final boolean mLoggingOnly;
    private final String mName;
    private final boolean mOverridable;

    public long getId() {
        return this.mChangeId;
    }

    public String getName() {
        return this.mName;
    }

    public int getEnableSinceTargetSdk() {
        return this.mEnableSinceTargetSdk;
    }

    public boolean getDisabled() {
        return this.mDisabled;
    }

    public boolean getLoggingOnly() {
        return this.mLoggingOnly;
    }

    public String getDescription() {
        return this.mDescription;
    }

    public boolean getOverridable() {
        return this.mOverridable;
    }

    public CompatibilityChangeInfo(Long changeId, String name, int enableAfterTargetSdk, int enableSinceTargetSdk, boolean disabled, boolean loggingOnly, String description, boolean overridable) {
        this.mChangeId = changeId.longValue();
        this.mName = name;
        if (enableAfterTargetSdk > 0) {
            this.mEnableSinceTargetSdk = enableAfterTargetSdk + 1;
        } else if (enableSinceTargetSdk > 0) {
            this.mEnableSinceTargetSdk = enableSinceTargetSdk;
        } else {
            this.mEnableSinceTargetSdk = -1;
        }
        this.mDisabled = disabled;
        this.mLoggingOnly = loggingOnly;
        this.mDescription = description;
        this.mOverridable = overridable;
    }

    public CompatibilityChangeInfo(CompatibilityChangeInfo other) {
        this.mChangeId = other.mChangeId;
        this.mName = other.mName;
        this.mEnableSinceTargetSdk = other.mEnableSinceTargetSdk;
        this.mDisabled = other.mDisabled;
        this.mLoggingOnly = other.mLoggingOnly;
        this.mDescription = other.mDescription;
        this.mOverridable = other.mOverridable;
    }

    private CompatibilityChangeInfo(Parcel in) {
        this.mChangeId = in.readLong();
        this.mName = in.readString();
        this.mEnableSinceTargetSdk = in.readInt();
        this.mDisabled = in.readBoolean();
        this.mLoggingOnly = in.readBoolean();
        this.mDescription = in.readString();
        this.mOverridable = in.readBoolean();
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.mChangeId);
        dest.writeString(this.mName);
        dest.writeInt(this.mEnableSinceTargetSdk);
        dest.writeBoolean(this.mDisabled);
        dest.writeBoolean(this.mLoggingOnly);
        dest.writeString(this.mDescription);
        dest.writeBoolean(this.mOverridable);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("CompatibilityChangeInfo(").append(getId());
        if (getName() != null) {
            sb.append("; name=").append(getName());
        }
        if (getEnableSinceTargetSdk() != -1) {
            sb.append("; enableSinceTargetSdk=").append(getEnableSinceTargetSdk());
        }
        if (getDisabled()) {
            sb.append("; disabled");
        }
        if (getLoggingOnly()) {
            sb.append("; loggingOnly");
        }
        if (getOverridable()) {
            sb.append("; overridable");
        }
        return sb.append(NavigationBarInflaterView.KEY_CODE_END).toString();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || !(o instanceof CompatibilityChangeInfo)) {
            return false;
        }
        CompatibilityChangeInfo that = (CompatibilityChangeInfo) o;
        if (this.mChangeId == that.mChangeId && this.mName.equals(that.mName) && this.mEnableSinceTargetSdk == that.mEnableSinceTargetSdk && this.mDisabled == that.mDisabled && this.mLoggingOnly == that.mLoggingOnly && this.mDescription.equals(that.mDescription) && this.mOverridable == that.mOverridable) {
            return true;
        }
        return false;
    }
}
