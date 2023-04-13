package android.app.servertransaction;

import android.app.ActivityThread;
import android.app.ClientTransactionHandler;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.p008os.Trace;
import com.android.internal.content.ReferrerIntent;
import java.util.List;
import java.util.Objects;
/* loaded from: classes.dex */
public class NewIntentItem extends ActivityTransactionItem {
    public static final Parcelable.Creator<NewIntentItem> CREATOR = new Parcelable.Creator<NewIntentItem>() { // from class: android.app.servertransaction.NewIntentItem.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public NewIntentItem createFromParcel(Parcel in) {
            return new NewIntentItem(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public NewIntentItem[] newArray(int size) {
            return new NewIntentItem[size];
        }
    };
    private List<ReferrerIntent> mIntents;
    private boolean mResume;

    @Override // android.app.servertransaction.ClientTransactionItem
    public int getPostExecutionState() {
        return this.mResume ? 3 : -1;
    }

    @Override // android.app.servertransaction.ActivityTransactionItem
    public void execute(ClientTransactionHandler client, ActivityThread.ActivityClientRecord r, PendingTransactionActions pendingActions) {
        Trace.traceBegin(64L, "activityNewIntent");
        client.handleNewIntent(r, this.mIntents);
        Trace.traceEnd(64L);
    }

    private NewIntentItem() {
    }

    public static NewIntentItem obtain(List<ReferrerIntent> intents, boolean resume) {
        NewIntentItem instance = (NewIntentItem) ObjectPool.obtain(NewIntentItem.class);
        if (instance == null) {
            instance = new NewIntentItem();
        }
        instance.mIntents = intents;
        instance.mResume = resume;
        return instance;
    }

    @Override // android.app.servertransaction.ObjectPoolItem
    public void recycle() {
        this.mIntents = null;
        this.mResume = false;
        ObjectPool.recycle(this);
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeBoolean(this.mResume);
        dest.writeTypedList(this.mIntents, flags);
    }

    private NewIntentItem(Parcel in) {
        this.mResume = in.readBoolean();
        this.mIntents = in.createTypedArrayList(ReferrerIntent.CREATOR);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        NewIntentItem other = (NewIntentItem) o;
        if (this.mResume == other.mResume && Objects.equals(this.mIntents, other.mIntents)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        int result = (17 * 31) + (this.mResume ? 1 : 0);
        return (result * 31) + this.mIntents.hashCode();
    }

    public String toString() {
        return "NewIntentItem{intents=" + this.mIntents + ",resume=" + this.mResume + "}";
    }
}
