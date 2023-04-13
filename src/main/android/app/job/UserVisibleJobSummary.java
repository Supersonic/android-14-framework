package android.app.job;

import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.util.Objects;
/* loaded from: classes.dex */
public class UserVisibleJobSummary implements Parcelable {
    public static final Parcelable.Creator<UserVisibleJobSummary> CREATOR = new Parcelable.Creator<UserVisibleJobSummary>() { // from class: android.app.job.UserVisibleJobSummary.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public UserVisibleJobSummary createFromParcel(Parcel in) {
            return new UserVisibleJobSummary(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public UserVisibleJobSummary[] newArray(int size) {
            return new UserVisibleJobSummary[size];
        }
    };
    private final String mCallingPackageName;
    private final int mCallingUid;
    private final int mJobId;
    private final String mNamespace;
    private final String mSourcePackageName;
    private final int mSourceUserId;

    public UserVisibleJobSummary(int callingUid, String callingPackageName, int sourceUserId, String sourcePackageName, String namespace, int jobId) {
        this.mCallingUid = callingUid;
        this.mCallingPackageName = callingPackageName;
        this.mSourceUserId = sourceUserId;
        this.mSourcePackageName = sourcePackageName;
        this.mNamespace = namespace;
        this.mJobId = jobId;
    }

    protected UserVisibleJobSummary(Parcel in) {
        this.mCallingUid = in.readInt();
        this.mCallingPackageName = in.readString();
        this.mSourceUserId = in.readInt();
        this.mSourcePackageName = in.readString();
        this.mNamespace = in.readString();
        this.mJobId = in.readInt();
    }

    public String getCallingPackageName() {
        return this.mCallingPackageName;
    }

    public int getCallingUid() {
        return this.mCallingUid;
    }

    public int getJobId() {
        return this.mJobId;
    }

    public String getNamespace() {
        return this.mNamespace;
    }

    public int getSourceUserId() {
        return this.mSourceUserId;
    }

    public String getSourcePackageName() {
        return this.mSourcePackageName;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof UserVisibleJobSummary) {
            UserVisibleJobSummary that = (UserVisibleJobSummary) o;
            return this.mCallingUid == that.mCallingUid && this.mCallingPackageName.equals(that.mCallingPackageName) && this.mSourceUserId == that.mSourceUserId && this.mSourcePackageName.equals(that.mSourcePackageName) && Objects.equals(this.mNamespace, that.mNamespace) && this.mJobId == that.mJobId;
        }
        return false;
    }

    public int hashCode() {
        int result = (((((((0 * 31) + this.mCallingUid) * 31) + this.mCallingPackageName.hashCode()) * 31) + this.mSourceUserId) * 31) + this.mSourcePackageName.hashCode();
        String str = this.mNamespace;
        if (str != null) {
            result = (result * 31) + str.hashCode();
        }
        return (result * 31) + this.mJobId;
    }

    public String toString() {
        return "UserVisibleJobSummary{callingUid=" + this.mCallingUid + ", callingPackageName='" + this.mCallingPackageName + "', sourceUserId=" + this.mSourceUserId + ", sourcePackageName='" + this.mSourcePackageName + "', namespace=" + this.mNamespace + ", jobId=" + this.mJobId + "}";
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mCallingUid);
        dest.writeString(this.mCallingPackageName);
        dest.writeInt(this.mSourceUserId);
        dest.writeString(this.mSourcePackageName);
        dest.writeString(this.mNamespace);
        dest.writeInt(this.mJobId);
    }
}
