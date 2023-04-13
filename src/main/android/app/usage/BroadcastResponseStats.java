package android.app.usage;

import android.annotation.SystemApi;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.util.Objects;
@SystemApi
/* loaded from: classes.dex */
public final class BroadcastResponseStats implements Parcelable {
    public static final Parcelable.Creator<BroadcastResponseStats> CREATOR = new Parcelable.Creator<BroadcastResponseStats>() { // from class: android.app.usage.BroadcastResponseStats.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public BroadcastResponseStats createFromParcel(Parcel source) {
            return new BroadcastResponseStats(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public BroadcastResponseStats[] newArray(int size) {
            return new BroadcastResponseStats[size];
        }
    };
    private int mBroadcastsDispatchedCount;
    private final long mId;
    private int mNotificationsCancelledCount;
    private int mNotificationsPostedCount;
    private int mNotificationsUpdatedCount;
    private final String mPackageName;

    public BroadcastResponseStats(String packageName, long id) {
        this.mPackageName = packageName;
        this.mId = id;
    }

    private BroadcastResponseStats(Parcel in) {
        this.mPackageName = in.readString8();
        this.mId = in.readLong();
        this.mBroadcastsDispatchedCount = in.readInt();
        this.mNotificationsPostedCount = in.readInt();
        this.mNotificationsUpdatedCount = in.readInt();
        this.mNotificationsCancelledCount = in.readInt();
    }

    public String getPackageName() {
        return this.mPackageName;
    }

    public long getId() {
        return this.mId;
    }

    public int getBroadcastsDispatchedCount() {
        return this.mBroadcastsDispatchedCount;
    }

    public int getNotificationsPostedCount() {
        return this.mNotificationsPostedCount;
    }

    public int getNotificationsUpdatedCount() {
        return this.mNotificationsUpdatedCount;
    }

    public int getNotificationsCancelledCount() {
        return this.mNotificationsCancelledCount;
    }

    public void incrementBroadcastsDispatchedCount(int count) {
        this.mBroadcastsDispatchedCount += count;
    }

    public void incrementNotificationsPostedCount(int count) {
        this.mNotificationsPostedCount += count;
    }

    public void incrementNotificationsUpdatedCount(int count) {
        this.mNotificationsUpdatedCount += count;
    }

    public void incrementNotificationsCancelledCount(int count) {
        this.mNotificationsCancelledCount += count;
    }

    public void addCounts(BroadcastResponseStats stats) {
        incrementBroadcastsDispatchedCount(stats.getBroadcastsDispatchedCount());
        incrementNotificationsPostedCount(stats.getNotificationsPostedCount());
        incrementNotificationsUpdatedCount(stats.getNotificationsUpdatedCount());
        incrementNotificationsCancelledCount(stats.getNotificationsCancelledCount());
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || !(obj instanceof BroadcastResponseStats)) {
            return false;
        }
        BroadcastResponseStats other = (BroadcastResponseStats) obj;
        if (this.mBroadcastsDispatchedCount == other.mBroadcastsDispatchedCount && this.mNotificationsPostedCount == other.mNotificationsPostedCount && this.mNotificationsUpdatedCount == other.mNotificationsUpdatedCount && this.mNotificationsCancelledCount == other.mNotificationsCancelledCount && this.mId == other.mId && this.mPackageName.equals(other.mPackageName)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(this.mPackageName, Long.valueOf(this.mId), Integer.valueOf(this.mBroadcastsDispatchedCount), Integer.valueOf(this.mNotificationsPostedCount), Integer.valueOf(this.mNotificationsUpdatedCount), Integer.valueOf(this.mNotificationsCancelledCount));
    }

    public String toString() {
        return "stats {package=" + this.mPackageName + ",id=" + this.mId + ",broadcastsSent=" + this.mBroadcastsDispatchedCount + ",notificationsPosted=" + this.mNotificationsPostedCount + ",notificationsUpdated=" + this.mNotificationsUpdatedCount + ",notificationsCancelled=" + this.mNotificationsCancelledCount + "}";
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString8(this.mPackageName);
        dest.writeLong(this.mId);
        dest.writeInt(this.mBroadcastsDispatchedCount);
        dest.writeInt(this.mNotificationsPostedCount);
        dest.writeInt(this.mNotificationsUpdatedCount);
        dest.writeInt(this.mNotificationsCancelledCount);
    }
}
