package android.p008os.storage;

import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.p008os.UserHandle;
import android.text.TextUtils;
import com.android.internal.util.Preconditions;
/* renamed from: android.os.storage.CrateInfo */
/* loaded from: classes3.dex */
public final class CrateInfo implements Parcelable {
    public static final Parcelable.Creator<CrateInfo> CREATOR = new Parcelable.Creator<CrateInfo>() { // from class: android.os.storage.CrateInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public CrateInfo createFromParcel(Parcel in) {
            CrateInfo crateInfo = new CrateInfo();
            crateInfo.readFromParcel(in);
            return crateInfo;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public CrateInfo[] newArray(int size) {
            return new CrateInfo[size];
        }
    };
    private static final String TAG = "CrateInfo";
    private long mExpiration;
    private String mId;
    private CharSequence mLabel;
    private String mPackageName;
    private int mUid;

    private CrateInfo() {
        this.mExpiration = 0L;
    }

    public CrateInfo(CharSequence label, long expiration) {
        Preconditions.checkStringNotEmpty(label, "Label should not be either null or empty string");
        Preconditions.checkArgumentNonnegative(expiration, "Expiration should be non negative number");
        this.mLabel = label;
        this.mExpiration = expiration;
    }

    public CrateInfo(CharSequence label) {
        this(label, 0L);
    }

    public CharSequence getLabel() {
        if (TextUtils.isEmpty(this.mLabel)) {
            return this.mId;
        }
        return this.mLabel;
    }

    public long getExpirationMillis() {
        return this.mExpiration;
    }

    public void setExpiration(long expiration) {
        Preconditions.checkArgumentNonnegative(expiration);
        this.mExpiration = expiration;
    }

    public int hashCode() {
        return super.hashCode();
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj instanceof CrateInfo) {
            CrateInfo crateInfo = (CrateInfo) obj;
            if (!TextUtils.isEmpty(this.mId) && TextUtils.equals(this.mId, crateInfo.mId)) {
                return true;
            }
        }
        return super.equals(obj);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        if (dest == null) {
            return;
        }
        dest.writeCharSequence(this.mLabel);
        dest.writeLong(this.mExpiration);
        dest.writeInt(this.mUid);
        dest.writeString(this.mPackageName);
        dest.writeString(this.mId);
    }

    public void readFromParcel(Parcel in) {
        if (in == null) {
            return;
        }
        this.mLabel = in.readCharSequence();
        this.mExpiration = in.readLong();
        this.mUid = in.readInt();
        this.mPackageName = in.readString();
        this.mId = in.readString();
    }

    public static CrateInfo copyFrom(int uid, String packageName, String id) {
        if (!UserHandle.isApp(uid) || TextUtils.isEmpty(packageName) || TextUtils.isEmpty(id)) {
            return null;
        }
        CrateInfo crateInfo = new CrateInfo(id, 0L);
        crateInfo.mUid = uid;
        crateInfo.mPackageName = packageName;
        crateInfo.mId = id;
        return crateInfo;
    }
}
