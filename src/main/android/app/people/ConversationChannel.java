package android.app.people;

import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.content.p001pm.ShortcutInfo;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public final class ConversationChannel implements Parcelable {
    public static final Parcelable.Creator<ConversationChannel> CREATOR = new Parcelable.Creator<ConversationChannel>() { // from class: android.app.people.ConversationChannel.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ConversationChannel createFromParcel(Parcel in) {
            return new ConversationChannel(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ConversationChannel[] newArray(int size) {
            return new ConversationChannel[size];
        }
    };
    private boolean mHasActiveNotifications;
    private boolean mHasBirthdayToday;
    private long mLastEventTimestamp;
    private NotificationChannel mNotificationChannel;
    private NotificationChannelGroup mNotificationChannelGroup;
    private ShortcutInfo mShortcutInfo;
    private List<ConversationStatus> mStatuses;
    private int mUid;

    public ConversationChannel(ShortcutInfo shortcutInfo, int uid, NotificationChannel parentNotificationChannel, NotificationChannelGroup parentNotificationChannelGroup, long lastEventTimestamp, boolean hasActiveNotifications) {
        this.mShortcutInfo = shortcutInfo;
        this.mUid = uid;
        this.mNotificationChannel = parentNotificationChannel;
        this.mNotificationChannelGroup = parentNotificationChannelGroup;
        this.mLastEventTimestamp = lastEventTimestamp;
        this.mHasActiveNotifications = hasActiveNotifications;
    }

    public ConversationChannel(ShortcutInfo shortcutInfo, int uid, NotificationChannel parentNotificationChannel, NotificationChannelGroup parentNotificationChannelGroup, long lastEventTimestamp, boolean hasActiveNotifications, boolean hasBirthdayToday, List<ConversationStatus> statuses) {
        this.mShortcutInfo = shortcutInfo;
        this.mUid = uid;
        this.mNotificationChannel = parentNotificationChannel;
        this.mNotificationChannelGroup = parentNotificationChannelGroup;
        this.mLastEventTimestamp = lastEventTimestamp;
        this.mHasActiveNotifications = hasActiveNotifications;
        this.mHasBirthdayToday = hasBirthdayToday;
        this.mStatuses = statuses;
    }

    public ConversationChannel(Parcel in) {
        this.mShortcutInfo = (ShortcutInfo) in.readParcelable(ShortcutInfo.class.getClassLoader(), ShortcutInfo.class);
        this.mUid = in.readInt();
        this.mNotificationChannel = (NotificationChannel) in.readParcelable(NotificationChannel.class.getClassLoader(), NotificationChannel.class);
        this.mNotificationChannelGroup = (NotificationChannelGroup) in.readParcelable(NotificationChannelGroup.class.getClassLoader(), NotificationChannelGroup.class);
        this.mLastEventTimestamp = in.readLong();
        this.mHasActiveNotifications = in.readBoolean();
        this.mHasBirthdayToday = in.readBoolean();
        ArrayList arrayList = new ArrayList();
        this.mStatuses = arrayList;
        in.readParcelableList(arrayList, ConversationStatus.class.getClassLoader(), ConversationStatus.class);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.mShortcutInfo, flags);
        dest.writeInt(this.mUid);
        dest.writeParcelable(this.mNotificationChannel, flags);
        dest.writeParcelable(this.mNotificationChannelGroup, flags);
        dest.writeLong(this.mLastEventTimestamp);
        dest.writeBoolean(this.mHasActiveNotifications);
        dest.writeBoolean(this.mHasBirthdayToday);
        dest.writeParcelableList(this.mStatuses, flags);
    }

    public ShortcutInfo getShortcutInfo() {
        return this.mShortcutInfo;
    }

    public int getUid() {
        return this.mUid;
    }

    public NotificationChannel getNotificationChannel() {
        return this.mNotificationChannel;
    }

    public NotificationChannelGroup getNotificationChannelGroup() {
        return this.mNotificationChannelGroup;
    }

    public long getLastEventTimestamp() {
        return this.mLastEventTimestamp;
    }

    public boolean hasActiveNotifications() {
        return this.mHasActiveNotifications;
    }

    public boolean hasBirthdayToday() {
        return this.mHasBirthdayToday;
    }

    public List<ConversationStatus> getStatuses() {
        return this.mStatuses;
    }

    public String toString() {
        return "ConversationChannel{mShortcutInfo=" + this.mShortcutInfo + ", mUid=" + this.mUid + ", mNotificationChannel=" + this.mNotificationChannel + ", mNotificationChannelGroup=" + this.mNotificationChannelGroup + ", mLastEventTimestamp=" + this.mLastEventTimestamp + ", mHasActiveNotifications=" + this.mHasActiveNotifications + ", mHasBirthdayToday=" + this.mHasBirthdayToday + ", mStatuses=" + this.mStatuses + '}';
    }
}
