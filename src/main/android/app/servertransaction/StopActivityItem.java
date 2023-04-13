package android.app.servertransaction;

import android.app.ActivityThread;
import android.app.ClientTransactionHandler;
import android.p008os.IBinder;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.p008os.Trace;
/* loaded from: classes.dex */
public class StopActivityItem extends ActivityLifecycleItem {
    public static final Parcelable.Creator<StopActivityItem> CREATOR = new Parcelable.Creator<StopActivityItem>() { // from class: android.app.servertransaction.StopActivityItem.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public StopActivityItem createFromParcel(Parcel in) {
            return new StopActivityItem(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public StopActivityItem[] newArray(int size) {
            return new StopActivityItem[size];
        }
    };
    private static final String TAG = "StopActivityItem";
    private int mConfigChanges;

    @Override // android.app.servertransaction.ActivityTransactionItem
    public void execute(ClientTransactionHandler client, ActivityThread.ActivityClientRecord r, PendingTransactionActions pendingActions) {
        Trace.traceBegin(64L, "activityStop");
        client.handleStopActivity(r, this.mConfigChanges, pendingActions, true, "STOP_ACTIVITY_ITEM");
        Trace.traceEnd(64L);
    }

    @Override // android.app.servertransaction.BaseClientRequest
    public void postExecute(ClientTransactionHandler client, IBinder token, PendingTransactionActions pendingActions) {
        client.reportStop(pendingActions);
    }

    @Override // android.app.servertransaction.ActivityLifecycleItem
    public int getTargetState() {
        return 5;
    }

    private StopActivityItem() {
    }

    public static StopActivityItem obtain(int configChanges) {
        StopActivityItem instance = (StopActivityItem) ObjectPool.obtain(StopActivityItem.class);
        if (instance == null) {
            instance = new StopActivityItem();
        }
        instance.mConfigChanges = configChanges;
        return instance;
    }

    @Override // android.app.servertransaction.ActivityLifecycleItem, android.app.servertransaction.ObjectPoolItem
    public void recycle() {
        super.recycle();
        this.mConfigChanges = 0;
        ObjectPool.recycle(this);
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mConfigChanges);
    }

    private StopActivityItem(Parcel in) {
        this.mConfigChanges = in.readInt();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        StopActivityItem other = (StopActivityItem) o;
        if (this.mConfigChanges == other.mConfigChanges) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        int result = (17 * 31) + this.mConfigChanges;
        return result;
    }

    public String toString() {
        return "StopActivityItem{configChanges=" + this.mConfigChanges + "}";
    }
}
