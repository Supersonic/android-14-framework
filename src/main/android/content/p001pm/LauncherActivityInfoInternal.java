package android.content.p001pm;

import android.content.ComponentName;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.p008os.UserHandle;
/* renamed from: android.content.pm.LauncherActivityInfoInternal */
/* loaded from: classes.dex */
public class LauncherActivityInfoInternal implements Parcelable {
    public static final Parcelable.Creator<LauncherActivityInfoInternal> CREATOR = new Parcelable.Creator<LauncherActivityInfoInternal>() { // from class: android.content.pm.LauncherActivityInfoInternal.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public LauncherActivityInfoInternal createFromParcel(Parcel source) {
            return new LauncherActivityInfoInternal(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public LauncherActivityInfoInternal[] newArray(int size) {
            return new LauncherActivityInfoInternal[size];
        }
    };
    private ActivityInfo mActivityInfo;
    private ComponentName mComponentName;
    private IncrementalStatesInfo mIncrementalStatesInfo;
    private UserHandle mUser;

    public LauncherActivityInfoInternal(ActivityInfo info, IncrementalStatesInfo incrementalStatesInfo, UserHandle user) {
        this.mActivityInfo = info;
        this.mComponentName = new ComponentName(info.packageName, info.name);
        this.mIncrementalStatesInfo = incrementalStatesInfo;
        this.mUser = user;
    }

    public LauncherActivityInfoInternal(Parcel source) {
        this.mActivityInfo = (ActivityInfo) source.readTypedObject(ActivityInfo.CREATOR);
        this.mComponentName = new ComponentName(this.mActivityInfo.packageName, this.mActivityInfo.name);
        this.mIncrementalStatesInfo = (IncrementalStatesInfo) source.readTypedObject(IncrementalStatesInfo.CREATOR);
        this.mUser = (UserHandle) source.readTypedObject(UserHandle.CREATOR);
    }

    public ComponentName getComponentName() {
        return this.mComponentName;
    }

    public ActivityInfo getActivityInfo() {
        return this.mActivityInfo;
    }

    public UserHandle getUser() {
        return this.mUser;
    }

    public IncrementalStatesInfo getIncrementalStatesInfo() {
        return this.mIncrementalStatesInfo;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedObject(this.mActivityInfo, flags);
        dest.writeTypedObject(this.mIncrementalStatesInfo, flags);
        dest.writeTypedObject(this.mUser, flags);
    }
}
