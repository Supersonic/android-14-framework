package android.app.servertransaction;

import android.app.ActivityThread;
import android.app.ClientTransactionHandler;
import android.app.ResultInfo;
import android.app.compat.CompatChanges;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.p008os.Trace;
import java.util.List;
import java.util.Objects;
/* loaded from: classes.dex */
public class ActivityResultItem extends ActivityTransactionItem {
    public static final long CALL_ACTIVITY_RESULT_BEFORE_RESUME = 78294732;
    public static final Parcelable.Creator<ActivityResultItem> CREATOR = new Parcelable.Creator<ActivityResultItem>() { // from class: android.app.servertransaction.ActivityResultItem.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ActivityResultItem createFromParcel(Parcel in) {
            return new ActivityResultItem(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ActivityResultItem[] newArray(int size) {
            return new ActivityResultItem[size];
        }
    };
    private List<ResultInfo> mResultInfoList;

    @Override // android.app.servertransaction.ClientTransactionItem
    public int getPostExecutionState() {
        return CompatChanges.isChangeEnabled(CALL_ACTIVITY_RESULT_BEFORE_RESUME) ? 3 : -1;
    }

    @Override // android.app.servertransaction.ActivityTransactionItem
    public void execute(ClientTransactionHandler client, ActivityThread.ActivityClientRecord r, PendingTransactionActions pendingActions) {
        Trace.traceBegin(64L, "activityDeliverResult");
        client.handleSendResult(r, this.mResultInfoList, "ACTIVITY_RESULT");
        Trace.traceEnd(64L);
    }

    private ActivityResultItem() {
    }

    public static ActivityResultItem obtain(List<ResultInfo> resultInfoList) {
        ActivityResultItem instance = (ActivityResultItem) ObjectPool.obtain(ActivityResultItem.class);
        if (instance == null) {
            instance = new ActivityResultItem();
        }
        instance.mResultInfoList = resultInfoList;
        return instance;
    }

    @Override // android.app.servertransaction.ObjectPoolItem
    public void recycle() {
        this.mResultInfoList = null;
        ObjectPool.recycle(this);
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(this.mResultInfoList, flags);
    }

    private ActivityResultItem(Parcel in) {
        this.mResultInfoList = in.createTypedArrayList(ResultInfo.CREATOR);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ActivityResultItem other = (ActivityResultItem) o;
        return Objects.equals(this.mResultInfoList, other.mResultInfoList);
    }

    public int hashCode() {
        return this.mResultInfoList.hashCode();
    }

    public String toString() {
        return "ActivityResultItem{resultInfoList=" + this.mResultInfoList + "}";
    }
}
