package android.content.rollback;

import android.annotation.SystemApi;
import android.content.p001pm.VersionedPackage;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
@SystemApi
/* loaded from: classes.dex */
public final class PackageRollbackInfo implements Parcelable {
    public static final Parcelable.Creator<PackageRollbackInfo> CREATOR = new Parcelable.Creator<PackageRollbackInfo>() { // from class: android.content.rollback.PackageRollbackInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public PackageRollbackInfo createFromParcel(Parcel in) {
            return new PackageRollbackInfo(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public PackageRollbackInfo[] newArray(int size) {
            return new PackageRollbackInfo[size];
        }
    };
    private final boolean mIsApex;
    private final boolean mIsApkInApex;
    private final List<Integer> mPendingBackups;
    private final ArrayList<RestoreInfo> mPendingRestores;
    private final int mRollbackDataPolicy;
    private final List<Integer> mSnapshottedUsers;
    private final VersionedPackage mVersionRolledBackFrom;
    private final VersionedPackage mVersionRolledBackTo;

    /* loaded from: classes.dex */
    public static class RestoreInfo {
        public final int appId;
        public final String seInfo;
        public final int userId;

        public RestoreInfo(int userId, int appId, String seInfo) {
            this.userId = userId;
            this.appId = appId;
            this.seInfo = seInfo;
        }
    }

    public String getPackageName() {
        return this.mVersionRolledBackFrom.getPackageName();
    }

    public VersionedPackage getVersionRolledBackFrom() {
        return this.mVersionRolledBackFrom;
    }

    public VersionedPackage getVersionRolledBackTo() {
        return this.mVersionRolledBackTo;
    }

    public void addPendingBackup(int userId) {
        this.mPendingBackups.add(Integer.valueOf(userId));
    }

    public List<Integer> getPendingBackups() {
        return this.mPendingBackups;
    }

    public ArrayList<RestoreInfo> getPendingRestores() {
        return this.mPendingRestores;
    }

    public RestoreInfo getRestoreInfo(int userId) {
        Iterator<RestoreInfo> it = this.mPendingRestores.iterator();
        while (it.hasNext()) {
            RestoreInfo ri = it.next();
            if (ri.userId == userId) {
                return ri;
            }
        }
        return null;
    }

    public void removeRestoreInfo(RestoreInfo ri) {
        this.mPendingRestores.remove(ri);
    }

    public boolean isApex() {
        return this.mIsApex;
    }

    public int getRollbackDataPolicy() {
        return this.mRollbackDataPolicy;
    }

    public boolean isApkInApex() {
        return this.mIsApkInApex;
    }

    public List<Integer> getSnapshottedUsers() {
        return this.mSnapshottedUsers;
    }

    public void removePendingBackup(int userId) {
        this.mPendingBackups.remove(Integer.valueOf(userId));
    }

    public void removePendingRestoreInfo(int userId) {
        removeRestoreInfo(getRestoreInfo(userId));
    }

    public PackageRollbackInfo(VersionedPackage packageRolledBackFrom, VersionedPackage packageRolledBackTo, List<Integer> pendingBackups, ArrayList<RestoreInfo> pendingRestores, boolean isApex, boolean isApkInApex, List<Integer> snapshottedUsers) {
        this(packageRolledBackFrom, packageRolledBackTo, pendingBackups, pendingRestores, isApex, isApkInApex, snapshottedUsers, 0);
    }

    public PackageRollbackInfo(VersionedPackage packageRolledBackFrom, VersionedPackage packageRolledBackTo, List<Integer> pendingBackups, ArrayList<RestoreInfo> pendingRestores, boolean isApex, boolean isApkInApex, List<Integer> snapshottedUsers, int rollbackDataPolicy) {
        this.mVersionRolledBackFrom = packageRolledBackFrom;
        this.mVersionRolledBackTo = packageRolledBackTo;
        this.mPendingBackups = pendingBackups;
        this.mPendingRestores = pendingRestores;
        this.mIsApex = isApex;
        this.mRollbackDataPolicy = rollbackDataPolicy;
        this.mIsApkInApex = isApkInApex;
        this.mSnapshottedUsers = snapshottedUsers;
    }

    private PackageRollbackInfo(Parcel in) {
        this.mVersionRolledBackFrom = VersionedPackage.CREATOR.createFromParcel(in);
        this.mVersionRolledBackTo = VersionedPackage.CREATOR.createFromParcel(in);
        this.mIsApex = in.readBoolean();
        this.mIsApkInApex = in.readBoolean();
        this.mPendingRestores = null;
        this.mPendingBackups = null;
        this.mSnapshottedUsers = null;
        this.mRollbackDataPolicy = 0;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        this.mVersionRolledBackFrom.writeToParcel(out, flags);
        this.mVersionRolledBackTo.writeToParcel(out, flags);
        out.writeBoolean(this.mIsApex);
        out.writeBoolean(this.mIsApkInApex);
    }
}
