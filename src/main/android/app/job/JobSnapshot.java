package android.app.job;

import android.p008os.Parcel;
import android.p008os.Parcelable;
/* loaded from: classes.dex */
public class JobSnapshot implements Parcelable {
    public static final Parcelable.Creator<JobSnapshot> CREATOR = new Parcelable.Creator<JobSnapshot>() { // from class: android.app.job.JobSnapshot.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public JobSnapshot createFromParcel(Parcel in) {
            return new JobSnapshot(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public JobSnapshot[] newArray(int size) {
            return new JobSnapshot[size];
        }
    };
    private final boolean mIsRunnable;
    private final JobInfo mJob;
    private final int mSatisfiedConstraints;

    public JobSnapshot(JobInfo info, int satisfiedMask, boolean runnable) {
        this.mJob = info;
        this.mSatisfiedConstraints = satisfiedMask;
        this.mIsRunnable = runnable;
    }

    public JobSnapshot(Parcel in) {
        this.mJob = JobInfo.CREATOR.createFromParcel(in);
        this.mSatisfiedConstraints = in.readInt();
        this.mIsRunnable = in.readBoolean();
    }

    private boolean satisfied(int flag) {
        return (this.mSatisfiedConstraints & flag) != 0;
    }

    public JobInfo getJobInfo() {
        return this.mJob;
    }

    public boolean isRunnable() {
        return this.mIsRunnable;
    }

    public boolean isChargingSatisfied() {
        return !this.mJob.isRequireCharging() || satisfied(1);
    }

    public boolean isBatteryNotLowSatisfied() {
        return !this.mJob.isRequireBatteryNotLow() || satisfied(2);
    }

    public boolean isRequireDeviceIdleSatisfied() {
        return !this.mJob.isRequireDeviceIdle() || satisfied(4);
    }

    public boolean isRequireStorageNotLowSatisfied() {
        return !this.mJob.isRequireStorageNotLow() || satisfied(8);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        this.mJob.writeToParcel(out, flags);
        out.writeInt(this.mSatisfiedConstraints);
        out.writeBoolean(this.mIsRunnable);
    }
}
