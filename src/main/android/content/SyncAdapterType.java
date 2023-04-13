package android.content;

import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.text.TextUtils;
/* loaded from: classes.dex */
public class SyncAdapterType implements Parcelable {
    public static final Parcelable.Creator<SyncAdapterType> CREATOR = new Parcelable.Creator<SyncAdapterType>() { // from class: android.content.SyncAdapterType.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public SyncAdapterType createFromParcel(Parcel source) {
            return new SyncAdapterType(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public SyncAdapterType[] newArray(int size) {
            return new SyncAdapterType[size];
        }
    };
    public final String accountType;
    private final boolean allowParallelSyncs;
    public final String authority;
    private final boolean isAlwaysSyncable;
    public final boolean isKey;
    private final String packageName;
    private final String settingsActivity;
    private final boolean supportsUploading;
    private final boolean userVisible;

    public SyncAdapterType(String authority, String accountType, boolean userVisible, boolean supportsUploading) {
        if (TextUtils.isEmpty(authority)) {
            throw new IllegalArgumentException("the authority must not be empty: " + authority);
        }
        if (TextUtils.isEmpty(accountType)) {
            throw new IllegalArgumentException("the accountType must not be empty: " + accountType);
        }
        this.authority = authority;
        this.accountType = accountType;
        this.userVisible = userVisible;
        this.supportsUploading = supportsUploading;
        this.isAlwaysSyncable = false;
        this.allowParallelSyncs = false;
        this.settingsActivity = null;
        this.isKey = false;
        this.packageName = null;
    }

    public SyncAdapterType(String authority, String accountType, boolean userVisible, boolean supportsUploading, boolean isAlwaysSyncable, boolean allowParallelSyncs, String settingsActivity, String packageName) {
        if (TextUtils.isEmpty(authority)) {
            throw new IllegalArgumentException("the authority must not be empty: " + authority);
        }
        if (TextUtils.isEmpty(accountType)) {
            throw new IllegalArgumentException("the accountType must not be empty: " + accountType);
        }
        this.authority = authority;
        this.accountType = accountType;
        this.userVisible = userVisible;
        this.supportsUploading = supportsUploading;
        this.isAlwaysSyncable = isAlwaysSyncable;
        this.allowParallelSyncs = allowParallelSyncs;
        this.settingsActivity = settingsActivity;
        this.isKey = false;
        this.packageName = packageName;
    }

    private SyncAdapterType(String authority, String accountType) {
        if (TextUtils.isEmpty(authority)) {
            throw new IllegalArgumentException("the authority must not be empty: " + authority);
        }
        if (TextUtils.isEmpty(accountType)) {
            throw new IllegalArgumentException("the accountType must not be empty: " + accountType);
        }
        this.authority = authority;
        this.accountType = accountType;
        this.userVisible = true;
        this.supportsUploading = true;
        this.isAlwaysSyncable = false;
        this.allowParallelSyncs = false;
        this.settingsActivity = null;
        this.isKey = true;
        this.packageName = null;
    }

    public boolean supportsUploading() {
        if (this.isKey) {
            throw new IllegalStateException("this method is not allowed to be called when this is a key");
        }
        return this.supportsUploading;
    }

    public boolean isUserVisible() {
        if (this.isKey) {
            throw new IllegalStateException("this method is not allowed to be called when this is a key");
        }
        return this.userVisible;
    }

    public boolean allowParallelSyncs() {
        if (this.isKey) {
            throw new IllegalStateException("this method is not allowed to be called when this is a key");
        }
        return this.allowParallelSyncs;
    }

    public boolean isAlwaysSyncable() {
        if (this.isKey) {
            throw new IllegalStateException("this method is not allowed to be called when this is a key");
        }
        return this.isAlwaysSyncable;
    }

    public String getSettingsActivity() {
        if (this.isKey) {
            throw new IllegalStateException("this method is not allowed to be called when this is a key");
        }
        return this.settingsActivity;
    }

    public String getPackageName() {
        return this.packageName;
    }

    public static SyncAdapterType newKey(String authority, String accountType) {
        return new SyncAdapterType(authority, accountType);
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o instanceof SyncAdapterType) {
            SyncAdapterType other = (SyncAdapterType) o;
            return this.authority.equals(other.authority) && this.accountType.equals(other.accountType);
        }
        return false;
    }

    public int hashCode() {
        int result = (17 * 31) + this.authority.hashCode();
        return (result * 31) + this.accountType.hashCode();
    }

    public String toString() {
        if (this.isKey) {
            return "SyncAdapterType Key {name=" + this.authority + ", type=" + this.accountType + "}";
        }
        return "SyncAdapterType {name=" + this.authority + ", type=" + this.accountType + ", userVisible=" + this.userVisible + ", supportsUploading=" + this.supportsUploading + ", isAlwaysSyncable=" + this.isAlwaysSyncable + ", allowParallelSyncs=" + this.allowParallelSyncs + ", settingsActivity=" + this.settingsActivity + ", packageName=" + this.packageName + "}";
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        if (this.isKey) {
            throw new IllegalStateException("keys aren't parcelable");
        }
        dest.writeString(this.authority);
        dest.writeString(this.accountType);
        dest.writeInt(this.userVisible ? 1 : 0);
        dest.writeInt(this.supportsUploading ? 1 : 0);
        dest.writeInt(this.isAlwaysSyncable ? 1 : 0);
        dest.writeInt(this.allowParallelSyncs ? 1 : 0);
        dest.writeString(this.settingsActivity);
        dest.writeString(this.packageName);
    }

    public SyncAdapterType(Parcel source) {
        this(source.readString(), source.readString(), source.readInt() != 0, source.readInt() != 0, source.readInt() != 0, source.readInt() != 0, source.readString(), source.readString());
    }
}
