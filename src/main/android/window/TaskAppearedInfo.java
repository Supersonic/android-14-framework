package android.window;

import android.app.ActivityManager;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.view.SurfaceControl;
/* loaded from: classes4.dex */
public final class TaskAppearedInfo implements Parcelable {
    public static final Parcelable.Creator<TaskAppearedInfo> CREATOR = new Parcelable.Creator<TaskAppearedInfo>() { // from class: android.window.TaskAppearedInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public TaskAppearedInfo createFromParcel(Parcel source) {
            ActivityManager.RunningTaskInfo taskInfo = (ActivityManager.RunningTaskInfo) source.readTypedObject(ActivityManager.RunningTaskInfo.CREATOR);
            SurfaceControl leash = (SurfaceControl) source.readTypedObject(SurfaceControl.CREATOR);
            return new TaskAppearedInfo(taskInfo, leash);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public TaskAppearedInfo[] newArray(int size) {
            return new TaskAppearedInfo[size];
        }
    };
    private final SurfaceControl mLeash;
    private final ActivityManager.RunningTaskInfo mTaskInfo;

    public TaskAppearedInfo(ActivityManager.RunningTaskInfo taskInfo, SurfaceControl leash) {
        this.mTaskInfo = taskInfo;
        this.mLeash = leash;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedObject(this.mTaskInfo, flags);
        dest.writeTypedObject(this.mLeash, flags);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    public ActivityManager.RunningTaskInfo getTaskInfo() {
        return this.mTaskInfo;
    }

    public SurfaceControl getLeash() {
        return this.mLeash;
    }
}
