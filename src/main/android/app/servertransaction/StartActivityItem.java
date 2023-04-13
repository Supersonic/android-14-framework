package android.app.servertransaction;

import android.app.ActivityOptions;
import android.app.ActivityThread;
import android.app.ClientTransactionHandler;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.p008os.Trace;
/* loaded from: classes.dex */
public class StartActivityItem extends ActivityLifecycleItem {
    public static final Parcelable.Creator<StartActivityItem> CREATOR = new Parcelable.Creator<StartActivityItem>() { // from class: android.app.servertransaction.StartActivityItem.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public StartActivityItem createFromParcel(Parcel in) {
            return new StartActivityItem(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public StartActivityItem[] newArray(int size) {
            return new StartActivityItem[size];
        }
    };
    private static final String TAG = "StartActivityItem";
    private ActivityOptions mActivityOptions;

    @Override // android.app.servertransaction.ActivityTransactionItem
    public void execute(ClientTransactionHandler client, ActivityThread.ActivityClientRecord r, PendingTransactionActions pendingActions) {
        Trace.traceBegin(64L, "startActivityItem");
        client.handleStartActivity(r, pendingActions, this.mActivityOptions);
        Trace.traceEnd(64L);
    }

    @Override // android.app.servertransaction.ActivityLifecycleItem
    public int getTargetState() {
        return 2;
    }

    private StartActivityItem() {
    }

    public static StartActivityItem obtain(ActivityOptions activityOptions) {
        StartActivityItem instance = (StartActivityItem) ObjectPool.obtain(StartActivityItem.class);
        if (instance == null) {
            instance = new StartActivityItem();
        }
        instance.mActivityOptions = activityOptions;
        return instance;
    }

    @Override // android.app.servertransaction.ActivityLifecycleItem, android.app.servertransaction.ObjectPoolItem
    public void recycle() {
        super.recycle();
        this.mActivityOptions = null;
        ObjectPool.recycle(this);
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        ActivityOptions activityOptions = this.mActivityOptions;
        dest.writeBundle(activityOptions != null ? activityOptions.toBundle() : null);
    }

    private StartActivityItem(Parcel in) {
        this.mActivityOptions = ActivityOptions.fromBundle(in.readBundle());
    }

    public boolean equals(Object o) {
        boolean z;
        boolean z2;
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        StartActivityItem other = (StartActivityItem) o;
        if (this.mActivityOptions == null) {
            z = true;
        } else {
            z = false;
        }
        if (other.mActivityOptions == null) {
            z2 = true;
        } else {
            z2 = false;
        }
        if (z == z2) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        int result = (17 * 31) + (this.mActivityOptions != null ? 1 : 0);
        return result;
    }

    public String toString() {
        return "StartActivityItem{options=" + this.mActivityOptions + "}";
    }
}
