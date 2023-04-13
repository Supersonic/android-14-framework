package android.service.notification;

import android.app.NotificationChannel;
import android.content.p001pm.ShortcutInfo;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.util.Objects;
/* loaded from: classes3.dex */
public final class ConversationChannelWrapper implements Parcelable {
    public static final Parcelable.Creator<ConversationChannelWrapper> CREATOR = new Parcelable.Creator<ConversationChannelWrapper>() { // from class: android.service.notification.ConversationChannelWrapper.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ConversationChannelWrapper createFromParcel(Parcel in) {
            return new ConversationChannelWrapper(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ConversationChannelWrapper[] newArray(int size) {
            return new ConversationChannelWrapper[size];
        }
    };
    private CharSequence mGroupLabel;
    private NotificationChannel mNotificationChannel;
    private CharSequence mParentChannelLabel;
    private String mPkg;
    private ShortcutInfo mShortcutInfo;
    private int mUid;

    public ConversationChannelWrapper() {
    }

    protected ConversationChannelWrapper(Parcel in) {
        this.mNotificationChannel = (NotificationChannel) in.readParcelable(NotificationChannel.class.getClassLoader(), NotificationChannel.class);
        this.mGroupLabel = in.readCharSequence();
        this.mParentChannelLabel = in.readCharSequence();
        this.mShortcutInfo = (ShortcutInfo) in.readParcelable(ShortcutInfo.class.getClassLoader(), ShortcutInfo.class);
        this.mPkg = in.readStringNoHelper();
        this.mUid = in.readInt();
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.mNotificationChannel, flags);
        dest.writeCharSequence(this.mGroupLabel);
        dest.writeCharSequence(this.mParentChannelLabel);
        dest.writeParcelable(this.mShortcutInfo, flags);
        dest.writeStringNoHelper(this.mPkg);
        dest.writeInt(this.mUid);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    public NotificationChannel getNotificationChannel() {
        return this.mNotificationChannel;
    }

    public void setNotificationChannel(NotificationChannel notificationChannel) {
        this.mNotificationChannel = notificationChannel;
    }

    public CharSequence getGroupLabel() {
        return this.mGroupLabel;
    }

    public void setGroupLabel(CharSequence groupLabel) {
        this.mGroupLabel = groupLabel;
    }

    public CharSequence getParentChannelLabel() {
        return this.mParentChannelLabel;
    }

    public void setParentChannelLabel(CharSequence parentChannelLabel) {
        this.mParentChannelLabel = parentChannelLabel;
    }

    public ShortcutInfo getShortcutInfo() {
        return this.mShortcutInfo;
    }

    public void setShortcutInfo(ShortcutInfo shortcutInfo) {
        this.mShortcutInfo = shortcutInfo;
    }

    public String getPkg() {
        return this.mPkg;
    }

    public void setPkg(String pkg) {
        this.mPkg = pkg;
    }

    public int getUid() {
        return this.mUid;
    }

    public void setUid(int uid) {
        this.mUid = uid;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ConversationChannelWrapper that = (ConversationChannelWrapper) o;
        if (Objects.equals(getNotificationChannel(), that.getNotificationChannel()) && Objects.equals(getGroupLabel(), that.getGroupLabel()) && Objects.equals(getParentChannelLabel(), that.getParentChannelLabel()) && Objects.equals(getShortcutInfo(), that.getShortcutInfo()) && Objects.equals(getPkg(), that.getPkg()) && getUid() == that.getUid()) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(getNotificationChannel(), getGroupLabel(), getParentChannelLabel(), getShortcutInfo(), getPkg(), Integer.valueOf(getUid()));
    }
}
