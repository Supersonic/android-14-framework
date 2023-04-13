package android.window;

import android.content.res.Configuration;
import android.p008os.Parcel;
import android.p008os.Parcelable;
/* loaded from: classes4.dex */
public class TaskFragmentParentInfo implements Parcelable {
    public static final Parcelable.Creator<TaskFragmentParentInfo> CREATOR = new Parcelable.Creator<TaskFragmentParentInfo>() { // from class: android.window.TaskFragmentParentInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public TaskFragmentParentInfo createFromParcel(Parcel in) {
            return new TaskFragmentParentInfo(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public TaskFragmentParentInfo[] newArray(int size) {
            return new TaskFragmentParentInfo[size];
        }
    };
    private final Configuration mConfiguration;
    private final int mDisplayId;
    private final boolean mVisible;

    public TaskFragmentParentInfo(Configuration configuration, int displayId, boolean visible) {
        Configuration configuration2 = new Configuration();
        this.mConfiguration = configuration2;
        configuration2.setTo(configuration);
        this.mDisplayId = displayId;
        this.mVisible = visible;
    }

    public TaskFragmentParentInfo(TaskFragmentParentInfo info) {
        Configuration configuration = new Configuration();
        this.mConfiguration = configuration;
        configuration.setTo(info.getConfiguration());
        this.mDisplayId = info.mDisplayId;
        this.mVisible = info.mVisible;
    }

    public Configuration getConfiguration() {
        return this.mConfiguration;
    }

    public int getDisplayId() {
        return this.mDisplayId;
    }

    public boolean isVisible() {
        return this.mVisible;
    }

    public boolean equalsForTaskFragmentOrganizer(TaskFragmentParentInfo that) {
        return that != null && getWindowingMode() == that.getWindowingMode() && this.mDisplayId == that.mDisplayId && this.mVisible == that.mVisible;
    }

    private int getWindowingMode() {
        return this.mConfiguration.windowConfiguration.getWindowingMode();
    }

    public String toString() {
        return TaskFragmentParentInfo.class.getSimpleName() + ":{config=" + this.mConfiguration + ", displayId=" + this.mDisplayId + ", visible=" + this.mVisible + "}";
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof TaskFragmentParentInfo) {
            TaskFragmentParentInfo that = (TaskFragmentParentInfo) obj;
            return this.mConfiguration.equals(that.mConfiguration) && this.mDisplayId == that.mDisplayId && this.mVisible == that.mVisible;
        }
        return false;
    }

    public int hashCode() {
        int result = this.mConfiguration.hashCode();
        return (((result * 31) + this.mDisplayId) * 31) + (this.mVisible ? 1 : 0);
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        this.mConfiguration.writeToParcel(dest, flags);
        dest.writeInt(this.mDisplayId);
        dest.writeBoolean(this.mVisible);
    }

    private TaskFragmentParentInfo(Parcel in) {
        Configuration configuration = new Configuration();
        this.mConfiguration = configuration;
        configuration.readFromParcel(in);
        this.mDisplayId = in.readInt();
        this.mVisible = in.readBoolean();
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }
}
