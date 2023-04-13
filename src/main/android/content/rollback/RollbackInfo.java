package android.content.rollback;

import android.annotation.SystemApi;
import android.content.p001pm.VersionedPackage;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.util.List;
@SystemApi
/* loaded from: classes.dex */
public final class RollbackInfo implements Parcelable {
    public static final Parcelable.Creator<RollbackInfo> CREATOR = new Parcelable.Creator<RollbackInfo>() { // from class: android.content.rollback.RollbackInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public RollbackInfo createFromParcel(Parcel in) {
            return new RollbackInfo(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public RollbackInfo[] newArray(int size) {
            return new RollbackInfo[size];
        }
    };
    private final List<VersionedPackage> mCausePackages;
    private int mCommittedSessionId;
    private final boolean mIsStaged;
    private final List<PackageRollbackInfo> mPackages;
    private final int mRollbackId;

    public RollbackInfo(int rollbackId, List<PackageRollbackInfo> packages, boolean isStaged, List<VersionedPackage> causePackages, int committedSessionId) {
        this.mRollbackId = rollbackId;
        this.mPackages = packages;
        this.mIsStaged = isStaged;
        this.mCausePackages = causePackages;
        this.mCommittedSessionId = committedSessionId;
    }

    private RollbackInfo(Parcel in) {
        this.mRollbackId = in.readInt();
        this.mPackages = in.createTypedArrayList(PackageRollbackInfo.CREATOR);
        this.mIsStaged = in.readBoolean();
        this.mCausePackages = in.createTypedArrayList(VersionedPackage.CREATOR);
        this.mCommittedSessionId = in.readInt();
    }

    public int getRollbackId() {
        return this.mRollbackId;
    }

    public List<PackageRollbackInfo> getPackages() {
        return this.mPackages;
    }

    public boolean isStaged() {
        return this.mIsStaged;
    }

    public int getCommittedSessionId() {
        return this.mCommittedSessionId;
    }

    public void setCommittedSessionId(int sessionId) {
        this.mCommittedSessionId = sessionId;
    }

    public List<VersionedPackage> getCausePackages() {
        return this.mCausePackages;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(this.mRollbackId);
        out.writeTypedList(this.mPackages);
        out.writeBoolean(this.mIsStaged);
        out.writeTypedList(this.mCausePackages);
        out.writeInt(this.mCommittedSessionId);
    }
}
