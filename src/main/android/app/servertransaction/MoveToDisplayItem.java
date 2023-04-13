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
public class MoveToDisplayItem extends ActivityTransactionItem {
    public static final Parcelable.Creator<MoveToDisplayItem> CREATOR = new Parcelable.Creator<MoveToDisplayItem>() { // from class: android.app.servertransaction.MoveToDisplayItem.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public MoveToDisplayItem createFromParcel(Parcel in) {
            return new MoveToDisplayItem(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public MoveToDisplayItem[] newArray(int size) {
            return new MoveToDisplayItem[size];
        }
    };
    private Configuration mConfiguration;
    private int mTargetDisplayId;

    @Override // android.app.servertransaction.BaseClientRequest
    public void preExecute(ClientTransactionHandler client, IBinder token) {
        CompatibilityInfo.applyOverrideScaleIfNeeded(this.mConfiguration);
        client.updatePendingActivityConfiguration(token, this.mConfiguration);
    }

    @Override // android.app.servertransaction.ActivityTransactionItem
    public void execute(ClientTransactionHandler client, ActivityThread.ActivityClientRecord r, PendingTransactionActions pendingActions) {
        Trace.traceBegin(64L, "activityMovedToDisplay");
        client.handleActivityConfigurationChanged(r, this.mConfiguration, this.mTargetDisplayId);
        Trace.traceEnd(64L);
    }

    private MoveToDisplayItem() {
    }

    public static MoveToDisplayItem obtain(int targetDisplayId, Configuration configuration) {
        if (configuration == null) {
            throw new IllegalArgumentException("Configuration must not be null");
        }
        MoveToDisplayItem instance = (MoveToDisplayItem) ObjectPool.obtain(MoveToDisplayItem.class);
        if (instance == null) {
            instance = new MoveToDisplayItem();
        }
        instance.mTargetDisplayId = targetDisplayId;
        instance.mConfiguration = configuration;
        return instance;
    }

    @Override // android.app.servertransaction.ObjectPoolItem
    public void recycle() {
        this.mTargetDisplayId = 0;
        this.mConfiguration = Configuration.EMPTY;
        ObjectPool.recycle(this);
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mTargetDisplayId);
        dest.writeTypedObject(this.mConfiguration, flags);
    }

    private MoveToDisplayItem(Parcel in) {
        this.mTargetDisplayId = in.readInt();
        this.mConfiguration = (Configuration) in.readTypedObject(Configuration.CREATOR);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MoveToDisplayItem other = (MoveToDisplayItem) o;
        if (this.mTargetDisplayId == other.mTargetDisplayId && Objects.equals(this.mConfiguration, other.mConfiguration)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        int result = (17 * 31) + this.mTargetDisplayId;
        return (result * 31) + this.mConfiguration.hashCode();
    }

    public String toString() {
        return "MoveToDisplayItem{targetDisplayId=" + this.mTargetDisplayId + ",configuration=" + this.mConfiguration + "}";
    }
}
