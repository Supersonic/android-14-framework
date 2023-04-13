package android.app.servertransaction;

import android.app.ActivityThread;
import android.app.ClientTransactionHandler;
import android.app.ResultInfo;
import android.content.res.CompatibilityInfo;
import android.p008os.IBinder;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.p008os.Trace;
import android.util.MergedConfiguration;
import com.android.internal.content.ReferrerIntent;
import java.util.List;
import java.util.Objects;
/* loaded from: classes.dex */
public class ActivityRelaunchItem extends ActivityTransactionItem {
    public static final Parcelable.Creator<ActivityRelaunchItem> CREATOR = new Parcelable.Creator<ActivityRelaunchItem>() { // from class: android.app.servertransaction.ActivityRelaunchItem.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ActivityRelaunchItem createFromParcel(Parcel in) {
            return new ActivityRelaunchItem(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ActivityRelaunchItem[] newArray(int size) {
            return new ActivityRelaunchItem[size];
        }
    };
    private static final String TAG = "ActivityRelaunchItem";
    private ActivityThread.ActivityClientRecord mActivityClientRecord;
    private MergedConfiguration mConfig;
    private int mConfigChanges;
    private List<ReferrerIntent> mPendingNewIntents;
    private List<ResultInfo> mPendingResults;
    private boolean mPreserveWindow;

    @Override // android.app.servertransaction.BaseClientRequest
    public void preExecute(ClientTransactionHandler client, IBinder token) {
        if (!client.isExecutingLocalTransaction()) {
            CompatibilityInfo.applyOverrideScaleIfNeeded(this.mConfig);
        }
        this.mActivityClientRecord = client.prepareRelaunchActivity(token, this.mPendingResults, this.mPendingNewIntents, this.mConfigChanges, this.mConfig, this.mPreserveWindow);
    }

    @Override // android.app.servertransaction.ActivityTransactionItem
    public void execute(ClientTransactionHandler client, ActivityThread.ActivityClientRecord r, PendingTransactionActions pendingActions) {
        if (this.mActivityClientRecord == null) {
            return;
        }
        Trace.traceBegin(64L, "activityRestart");
        client.handleRelaunchActivity(this.mActivityClientRecord, pendingActions);
        Trace.traceEnd(64L);
    }

    @Override // android.app.servertransaction.BaseClientRequest
    public void postExecute(ClientTransactionHandler client, IBinder token, PendingTransactionActions pendingActions) {
        ActivityThread.ActivityClientRecord r = getActivityClientRecord(client, token);
        client.reportRelaunch(r);
    }

    private ActivityRelaunchItem() {
    }

    public static ActivityRelaunchItem obtain(List<ResultInfo> pendingResults, List<ReferrerIntent> pendingNewIntents, int configChanges, MergedConfiguration config, boolean preserveWindow) {
        ActivityRelaunchItem instance = (ActivityRelaunchItem) ObjectPool.obtain(ActivityRelaunchItem.class);
        if (instance == null) {
            instance = new ActivityRelaunchItem();
        }
        instance.mPendingResults = pendingResults;
        instance.mPendingNewIntents = pendingNewIntents;
        instance.mConfigChanges = configChanges;
        instance.mConfig = config;
        instance.mPreserveWindow = preserveWindow;
        return instance;
    }

    @Override // android.app.servertransaction.ObjectPoolItem
    public void recycle() {
        this.mPendingResults = null;
        this.mPendingNewIntents = null;
        this.mConfigChanges = 0;
        this.mConfig = null;
        this.mPreserveWindow = false;
        this.mActivityClientRecord = null;
        ObjectPool.recycle(this);
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(this.mPendingResults, flags);
        dest.writeTypedList(this.mPendingNewIntents, flags);
        dest.writeInt(this.mConfigChanges);
        dest.writeTypedObject(this.mConfig, flags);
        dest.writeBoolean(this.mPreserveWindow);
    }

    private ActivityRelaunchItem(Parcel in) {
        this.mPendingResults = in.createTypedArrayList(ResultInfo.CREATOR);
        this.mPendingNewIntents = in.createTypedArrayList(ReferrerIntent.CREATOR);
        this.mConfigChanges = in.readInt();
        this.mConfig = (MergedConfiguration) in.readTypedObject(MergedConfiguration.CREATOR);
        this.mPreserveWindow = in.readBoolean();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ActivityRelaunchItem other = (ActivityRelaunchItem) o;
        if (Objects.equals(this.mPendingResults, other.mPendingResults) && Objects.equals(this.mPendingNewIntents, other.mPendingNewIntents) && this.mConfigChanges == other.mConfigChanges && Objects.equals(this.mConfig, other.mConfig) && this.mPreserveWindow == other.mPreserveWindow) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        int result = (17 * 31) + Objects.hashCode(this.mPendingResults);
        return (((((((result * 31) + Objects.hashCode(this.mPendingNewIntents)) * 31) + this.mConfigChanges) * 31) + Objects.hashCode(this.mConfig)) * 31) + (this.mPreserveWindow ? 1 : 0);
    }

    public String toString() {
        return "ActivityRelaunchItem{pendingResults=" + this.mPendingResults + ",pendingNewIntents=" + this.mPendingNewIntents + ",configChanges=" + this.mConfigChanges + ",config=" + this.mConfig + ",preserveWindow" + this.mPreserveWindow + "}";
    }
}
