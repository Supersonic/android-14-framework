package android.app.servertransaction;

import android.app.ActivityThread;
import android.app.ClientTransactionHandler;
import android.content.res.CompatibilityInfo;
import android.content.res.Configuration;
import android.p008os.IBinder;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.p008os.Trace;
import java.util.Objects;
/* loaded from: classes.dex */
public class ActivityConfigurationChangeItem extends ActivityTransactionItem {
    public static final Parcelable.Creator<ActivityConfigurationChangeItem> CREATOR = new Parcelable.Creator<ActivityConfigurationChangeItem>() { // from class: android.app.servertransaction.ActivityConfigurationChangeItem.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ActivityConfigurationChangeItem createFromParcel(Parcel in) {
            return new ActivityConfigurationChangeItem(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ActivityConfigurationChangeItem[] newArray(int size) {
            return new ActivityConfigurationChangeItem[size];
        }
    };
    private Configuration mConfiguration;

    @Override // android.app.servertransaction.BaseClientRequest
    public void preExecute(ClientTransactionHandler client, IBinder token) {
        CompatibilityInfo.applyOverrideScaleIfNeeded(this.mConfiguration);
        client.updatePendingActivityConfiguration(token, this.mConfiguration);
    }

    @Override // android.app.servertransaction.ActivityTransactionItem
    public void execute(ClientTransactionHandler client, ActivityThread.ActivityClientRecord r, PendingTransactionActions pendingActions) {
        Trace.traceBegin(64L, "activityConfigChanged");
        client.handleActivityConfigurationChanged(r, this.mConfiguration, -1);
        Trace.traceEnd(64L);
    }

    private ActivityConfigurationChangeItem() {
    }

    public static ActivityConfigurationChangeItem obtain(Configuration config) {
        if (config == null) {
            throw new IllegalArgumentException("Config must not be null.");
        }
        ActivityConfigurationChangeItem instance = (ActivityConfigurationChangeItem) ObjectPool.obtain(ActivityConfigurationChangeItem.class);
        if (instance == null) {
            instance = new ActivityConfigurationChangeItem();
        }
        instance.mConfiguration = config;
        return instance;
    }

    @Override // android.app.servertransaction.ObjectPoolItem
    public void recycle() {
        this.mConfiguration = Configuration.EMPTY;
        ObjectPool.recycle(this);
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedObject(this.mConfiguration, flags);
    }

    private ActivityConfigurationChangeItem(Parcel in) {
        this.mConfiguration = (Configuration) in.readTypedObject(Configuration.CREATOR);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ActivityConfigurationChangeItem other = (ActivityConfigurationChangeItem) o;
        return Objects.equals(this.mConfiguration, other.mConfiguration);
    }

    public int hashCode() {
        return this.mConfiguration.hashCode();
    }

    public String toString() {
        return "ActivityConfigurationChange{config=" + this.mConfiguration + "}";
    }
}
