package android.app.servertransaction;

import android.app.ActivityClient;
import android.app.ActivityThread;
import android.app.ClientTransactionHandler;
import android.p008os.IBinder;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.p008os.Trace;
/* loaded from: classes.dex */
public class ResumeActivityItem extends ActivityLifecycleItem {
    public static final Parcelable.Creator<ResumeActivityItem> CREATOR = new Parcelable.Creator<ResumeActivityItem>() { // from class: android.app.servertransaction.ResumeActivityItem.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ResumeActivityItem createFromParcel(Parcel in) {
            return new ResumeActivityItem(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ResumeActivityItem[] newArray(int size) {
            return new ResumeActivityItem[size];
        }
    };
    private static final String TAG = "ResumeActivityItem";
    private boolean mIsForward;
    private int mProcState;
    private boolean mShouldSendCompatFakeFocus;
    private boolean mUpdateProcState;

    @Override // android.app.servertransaction.BaseClientRequest
    public void preExecute(ClientTransactionHandler client, IBinder token) {
        if (this.mUpdateProcState) {
            client.updateProcessState(this.mProcState, false);
        }
    }

    @Override // android.app.servertransaction.ActivityTransactionItem
    public void execute(ClientTransactionHandler client, ActivityThread.ActivityClientRecord r, PendingTransactionActions pendingActions) {
        Trace.traceBegin(64L, "activityResume");
        client.handleResumeActivity(r, true, this.mIsForward, this.mShouldSendCompatFakeFocus, "RESUME_ACTIVITY");
        Trace.traceEnd(64L);
    }

    @Override // android.app.servertransaction.BaseClientRequest
    public void postExecute(ClientTransactionHandler client, IBinder token, PendingTransactionActions pendingActions) {
        ActivityClient.getInstance().activityResumed(token, client.isHandleSplashScreenExit(token));
    }

    @Override // android.app.servertransaction.ActivityLifecycleItem
    public int getTargetState() {
        return 3;
    }

    private ResumeActivityItem() {
    }

    public static ResumeActivityItem obtain(int procState, boolean isForward, boolean shouldSendCompatFakeFocus) {
        ResumeActivityItem instance = (ResumeActivityItem) ObjectPool.obtain(ResumeActivityItem.class);
        if (instance == null) {
            instance = new ResumeActivityItem();
        }
        instance.mProcState = procState;
        instance.mUpdateProcState = true;
        instance.mIsForward = isForward;
        instance.mShouldSendCompatFakeFocus = shouldSendCompatFakeFocus;
        return instance;
    }

    public static ResumeActivityItem obtain(boolean isForward, boolean shouldSendCompatFakeFocus) {
        ResumeActivityItem instance = (ResumeActivityItem) ObjectPool.obtain(ResumeActivityItem.class);
        if (instance == null) {
            instance = new ResumeActivityItem();
        }
        instance.mProcState = -1;
        instance.mUpdateProcState = false;
        instance.mIsForward = isForward;
        instance.mShouldSendCompatFakeFocus = shouldSendCompatFakeFocus;
        return instance;
    }

    @Override // android.app.servertransaction.ActivityLifecycleItem, android.app.servertransaction.ObjectPoolItem
    public void recycle() {
        super.recycle();
        this.mProcState = -1;
        this.mUpdateProcState = false;
        this.mIsForward = false;
        this.mShouldSendCompatFakeFocus = false;
        ObjectPool.recycle(this);
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mProcState);
        dest.writeBoolean(this.mUpdateProcState);
        dest.writeBoolean(this.mIsForward);
        dest.writeBoolean(this.mShouldSendCompatFakeFocus);
    }

    private ResumeActivityItem(Parcel in) {
        this.mProcState = in.readInt();
        this.mUpdateProcState = in.readBoolean();
        this.mIsForward = in.readBoolean();
        this.mShouldSendCompatFakeFocus = in.readBoolean();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ResumeActivityItem other = (ResumeActivityItem) o;
        if (this.mProcState == other.mProcState && this.mUpdateProcState == other.mUpdateProcState && this.mIsForward == other.mIsForward && this.mShouldSendCompatFakeFocus == other.mShouldSendCompatFakeFocus) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        int result = (17 * 31) + this.mProcState;
        return (((((result * 31) + (this.mUpdateProcState ? 1 : 0)) * 31) + (this.mIsForward ? 1 : 0)) * 31) + (this.mShouldSendCompatFakeFocus ? 1 : 0);
    }

    public String toString() {
        return "ResumeActivityItem{procState=" + this.mProcState + ",updateProcState=" + this.mUpdateProcState + ",isForward=" + this.mIsForward + ",shouldSendCompatFakeFocus=" + this.mShouldSendCompatFakeFocus + "}";
    }
}
