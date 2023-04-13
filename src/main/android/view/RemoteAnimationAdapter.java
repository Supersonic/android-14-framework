package android.view;

import android.app.IApplicationThread;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.view.IRemoteAnimationRunner;
/* loaded from: classes4.dex */
public class RemoteAnimationAdapter implements Parcelable {
    public static final Parcelable.Creator<RemoteAnimationAdapter> CREATOR = new Parcelable.Creator<RemoteAnimationAdapter>() { // from class: android.view.RemoteAnimationAdapter.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public RemoteAnimationAdapter createFromParcel(Parcel in) {
            return new RemoteAnimationAdapter(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public RemoteAnimationAdapter[] newArray(int size) {
            return new RemoteAnimationAdapter[size];
        }
    };
    private IApplicationThread mCallingApplication;
    private int mCallingPid;
    private int mCallingUid;
    private final boolean mChangeNeedsSnapshot;
    private final long mDuration;
    private final IRemoteAnimationRunner mRunner;
    private final long mStatusBarTransitionDelay;

    public RemoteAnimationAdapter(IRemoteAnimationRunner runner, long duration, long statusBarTransitionDelay, boolean changeNeedsSnapshot) {
        this.mRunner = runner;
        this.mDuration = duration;
        this.mChangeNeedsSnapshot = changeNeedsSnapshot;
        this.mStatusBarTransitionDelay = statusBarTransitionDelay;
    }

    public RemoteAnimationAdapter(IRemoteAnimationRunner runner, long duration, long statusBarTransitionDelay) {
        this(runner, duration, statusBarTransitionDelay, false);
    }

    public RemoteAnimationAdapter(IRemoteAnimationRunner runner, long duration, long statusBarTransitionDelay, IApplicationThread callingApplication) {
        this(runner, duration, statusBarTransitionDelay, false);
        this.mCallingApplication = callingApplication;
    }

    public RemoteAnimationAdapter(Parcel in) {
        this.mRunner = IRemoteAnimationRunner.Stub.asInterface(in.readStrongBinder());
        this.mDuration = in.readLong();
        this.mStatusBarTransitionDelay = in.readLong();
        this.mChangeNeedsSnapshot = in.readBoolean();
        this.mCallingApplication = IApplicationThread.Stub.asInterface(in.readStrongBinder());
    }

    public IRemoteAnimationRunner getRunner() {
        return this.mRunner;
    }

    public long getDuration() {
        return this.mDuration;
    }

    public long getStatusBarTransitionDelay() {
        return this.mStatusBarTransitionDelay;
    }

    public boolean getChangeNeedsSnapshot() {
        return this.mChangeNeedsSnapshot;
    }

    public void setCallingPidUid(int pid, int uid) {
        this.mCallingPid = pid;
        this.mCallingUid = uid;
    }

    public int getCallingPid() {
        return this.mCallingPid;
    }

    public int getCallingUid() {
        return this.mCallingUid;
    }

    public IApplicationThread getCallingApplication() {
        return this.mCallingApplication;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStrongInterface(this.mRunner);
        dest.writeLong(this.mDuration);
        dest.writeLong(this.mStatusBarTransitionDelay);
        dest.writeBoolean(this.mChangeNeedsSnapshot);
        dest.writeStrongInterface(this.mCallingApplication);
    }
}
