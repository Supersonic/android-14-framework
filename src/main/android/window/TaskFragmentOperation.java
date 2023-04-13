package android.window;

import android.content.Intent;
import android.p008os.Bundle;
import android.p008os.IBinder;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Objects;
/* loaded from: classes4.dex */
public final class TaskFragmentOperation implements Parcelable {
    public static final Parcelable.Creator<TaskFragmentOperation> CREATOR = new Parcelable.Creator<TaskFragmentOperation>() { // from class: android.window.TaskFragmentOperation.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public TaskFragmentOperation createFromParcel(Parcel in) {
            return new TaskFragmentOperation(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public TaskFragmentOperation[] newArray(int size) {
            return new TaskFragmentOperation[size];
        }
    };
    public static final int OP_TYPE_CLEAR_ADJACENT_TASK_FRAGMENTS = 5;
    public static final int OP_TYPE_CREATE_TASK_FRAGMENT = 0;
    public static final int OP_TYPE_DELETE_TASK_FRAGMENT = 1;
    public static final int OP_TYPE_REPARENT_ACTIVITY_TO_TASK_FRAGMENT = 3;
    public static final int OP_TYPE_REQUEST_FOCUS_ON_TASK_FRAGMENT = 6;
    public static final int OP_TYPE_SET_ADJACENT_TASK_FRAGMENTS = 4;
    public static final int OP_TYPE_SET_ANIMATION_PARAMS = 8;
    public static final int OP_TYPE_SET_COMPANION_TASK_FRAGMENT = 7;
    public static final int OP_TYPE_SET_RELATIVE_BOUNDS = 9;
    public static final int OP_TYPE_START_ACTIVITY_IN_TASK_FRAGMENT = 2;
    public static final int OP_TYPE_UNKNOWN = -1;
    private final Intent mActivityIntent;
    private final IBinder mActivityToken;
    private final TaskFragmentAnimationParams mAnimationParams;
    private final Bundle mBundle;
    private final int mOpType;
    private final IBinder mSecondaryFragmentToken;
    private final TaskFragmentCreationParams mTaskFragmentCreationParams;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes4.dex */
    public @interface OperationType {
    }

    private TaskFragmentOperation(int opType, TaskFragmentCreationParams taskFragmentCreationParams, IBinder activityToken, Intent activityIntent, Bundle bundle, IBinder secondaryFragmentToken, TaskFragmentAnimationParams animationParams) {
        this.mOpType = opType;
        this.mTaskFragmentCreationParams = taskFragmentCreationParams;
        this.mActivityToken = activityToken;
        this.mActivityIntent = activityIntent;
        this.mBundle = bundle;
        this.mSecondaryFragmentToken = secondaryFragmentToken;
        this.mAnimationParams = animationParams;
    }

    private TaskFragmentOperation(Parcel in) {
        this.mOpType = in.readInt();
        this.mTaskFragmentCreationParams = (TaskFragmentCreationParams) in.readTypedObject(TaskFragmentCreationParams.CREATOR);
        this.mActivityToken = in.readStrongBinder();
        this.mActivityIntent = (Intent) in.readTypedObject(Intent.CREATOR);
        this.mBundle = in.readBundle(getClass().getClassLoader());
        this.mSecondaryFragmentToken = in.readStrongBinder();
        this.mAnimationParams = (TaskFragmentAnimationParams) in.readTypedObject(TaskFragmentAnimationParams.CREATOR);
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mOpType);
        dest.writeTypedObject(this.mTaskFragmentCreationParams, flags);
        dest.writeStrongBinder(this.mActivityToken);
        dest.writeTypedObject(this.mActivityIntent, flags);
        dest.writeBundle(this.mBundle);
        dest.writeStrongBinder(this.mSecondaryFragmentToken);
        dest.writeTypedObject(this.mAnimationParams, flags);
    }

    public int getOpType() {
        return this.mOpType;
    }

    public TaskFragmentCreationParams getTaskFragmentCreationParams() {
        return this.mTaskFragmentCreationParams;
    }

    public IBinder getActivityToken() {
        return this.mActivityToken;
    }

    public Intent getActivityIntent() {
        return this.mActivityIntent;
    }

    public Bundle getBundle() {
        return this.mBundle;
    }

    public IBinder getSecondaryFragmentToken() {
        return this.mSecondaryFragmentToken;
    }

    public TaskFragmentAnimationParams getAnimationParams() {
        return this.mAnimationParams;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("TaskFragmentOperation{ opType=").append(this.mOpType);
        if (this.mTaskFragmentCreationParams != null) {
            sb.append(", taskFragmentCreationParams=").append(this.mTaskFragmentCreationParams);
        }
        if (this.mActivityToken != null) {
            sb.append(", activityToken=").append(this.mActivityToken);
        }
        if (this.mActivityIntent != null) {
            sb.append(", activityIntent=").append(this.mActivityIntent);
        }
        if (this.mBundle != null) {
            sb.append(", bundle=").append(this.mBundle);
        }
        if (this.mSecondaryFragmentToken != null) {
            sb.append(", secondaryFragmentToken=").append(this.mSecondaryFragmentToken);
        }
        if (this.mAnimationParams != null) {
            sb.append(", animationParams=").append(this.mAnimationParams);
        }
        sb.append('}');
        return sb.toString();
    }

    public int hashCode() {
        return Objects.hash(Integer.valueOf(this.mOpType), this.mTaskFragmentCreationParams, this.mActivityToken, this.mActivityIntent, this.mBundle, this.mSecondaryFragmentToken, this.mAnimationParams);
    }

    public boolean equals(Object obj) {
        if (obj instanceof TaskFragmentOperation) {
            TaskFragmentOperation other = (TaskFragmentOperation) obj;
            return this.mOpType == other.mOpType && Objects.equals(this.mTaskFragmentCreationParams, other.mTaskFragmentCreationParams) && Objects.equals(this.mActivityToken, other.mActivityToken) && Objects.equals(this.mActivityIntent, other.mActivityIntent) && Objects.equals(this.mBundle, other.mBundle) && Objects.equals(this.mSecondaryFragmentToken, other.mSecondaryFragmentToken) && Objects.equals(this.mAnimationParams, other.mAnimationParams);
        }
        return false;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    /* loaded from: classes4.dex */
    public static final class Builder {
        private Intent mActivityIntent;
        private IBinder mActivityToken;
        private TaskFragmentAnimationParams mAnimationParams;
        private Bundle mBundle;
        private final int mOpType;
        private IBinder mSecondaryFragmentToken;
        private TaskFragmentCreationParams mTaskFragmentCreationParams;

        public Builder(int opType) {
            this.mOpType = opType;
        }

        public Builder setTaskFragmentCreationParams(TaskFragmentCreationParams taskFragmentCreationParams) {
            this.mTaskFragmentCreationParams = taskFragmentCreationParams;
            return this;
        }

        public Builder setActivityToken(IBinder activityToken) {
            this.mActivityToken = activityToken;
            return this;
        }

        public Builder setActivityIntent(Intent activityIntent) {
            this.mActivityIntent = activityIntent;
            return this;
        }

        public Builder setBundle(Bundle bundle) {
            this.mBundle = bundle;
            return this;
        }

        public Builder setSecondaryFragmentToken(IBinder secondaryFragmentToken) {
            this.mSecondaryFragmentToken = secondaryFragmentToken;
            return this;
        }

        public Builder setAnimationParams(TaskFragmentAnimationParams animationParams) {
            this.mAnimationParams = animationParams;
            return this;
        }

        public TaskFragmentOperation build() {
            return new TaskFragmentOperation(this.mOpType, this.mTaskFragmentCreationParams, this.mActivityToken, this.mActivityIntent, this.mBundle, this.mSecondaryFragmentToken, this.mAnimationParams);
        }
    }
}
