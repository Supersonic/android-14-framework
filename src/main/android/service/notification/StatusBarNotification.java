package android.service.notification;

import android.app.Notification;
import android.app.Person;
import android.content.Context;
import android.content.p001pm.ApplicationInfo;
import android.content.p001pm.PackageManager;
import android.metrics.LogMaker;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.p008os.UserHandle;
import android.text.TextUtils;
import android.util.NtpTrustedTime;
import com.android.internal.content.NativeLibraryHelper;
import com.android.internal.logging.InstanceId;
import com.android.internal.logging.nano.MetricsProto;
import java.util.ArrayList;
/* loaded from: classes3.dex */
public class StatusBarNotification implements Parcelable {
    public static final Parcelable.Creator<StatusBarNotification> CREATOR = new Parcelable.Creator<StatusBarNotification>() { // from class: android.service.notification.StatusBarNotification.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public StatusBarNotification createFromParcel(Parcel parcel) {
            return new StatusBarNotification(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public StatusBarNotification[] newArray(int size) {
            return new StatusBarNotification[size];
        }
    };
    static final int MAX_LOG_TAG_LENGTH = 36;
    private String groupKey;

    /* renamed from: id */
    private final int f425id;
    private final int initialPid;
    private final String key;
    private Context mContext;
    private InstanceId mInstanceId;
    private final Notification notification;
    private final String opPkg;
    private String overrideGroupKey;
    private final String pkg;
    private final long postTime;
    private final String tag;
    private final int uid;
    private final UserHandle user;

    public StatusBarNotification(String pkg, String opPkg, int id, String tag, int uid, int initialPid, Notification notification, UserHandle user, String overrideGroupKey, long postTime) {
        if (pkg == null) {
            throw new NullPointerException();
        }
        if (notification == null) {
            throw new NullPointerException();
        }
        this.pkg = pkg;
        this.opPkg = opPkg;
        this.f425id = id;
        this.tag = tag;
        this.uid = uid;
        this.initialPid = initialPid;
        this.notification = notification;
        this.user = user;
        this.postTime = postTime;
        this.overrideGroupKey = overrideGroupKey;
        this.key = key();
        this.groupKey = groupKey();
    }

    @Deprecated
    public StatusBarNotification(String pkg, String opPkg, int id, String tag, int uid, int initialPid, int score, Notification notification, UserHandle user, long postTime) {
        if (pkg == null) {
            throw new NullPointerException();
        }
        if (notification == null) {
            throw new NullPointerException();
        }
        this.pkg = pkg;
        this.opPkg = opPkg;
        this.f425id = id;
        this.tag = tag;
        this.uid = uid;
        this.initialPid = initialPid;
        this.notification = notification;
        this.user = user;
        this.postTime = postTime;
        this.key = key();
        this.groupKey = groupKey();
    }

    public StatusBarNotification(Parcel in) {
        this.pkg = in.readString();
        this.opPkg = in.readString();
        this.f425id = in.readInt();
        if (in.readInt() != 0) {
            this.tag = in.readString();
        } else {
            this.tag = null;
        }
        this.uid = in.readInt();
        this.initialPid = in.readInt();
        this.notification = new Notification(in);
        this.user = UserHandle.readFromParcel(in);
        this.postTime = in.readLong();
        if (in.readInt() != 0) {
            this.overrideGroupKey = in.readString();
        }
        if (in.readInt() != 0) {
            this.mInstanceId = InstanceId.CREATOR.createFromParcel(in);
        }
        this.key = key();
        this.groupKey = groupKey();
    }

    public static int getUidFromKey(String key) {
        String[] parts = key.split("\\|");
        if (parts.length >= 5) {
            try {
                int uid = Integer.parseInt(parts[4]);
                return uid;
            } catch (NumberFormatException e) {
                return -1;
            }
        }
        return -1;
    }

    public static String getPkgFromKey(String key) {
        String[] parts = key.split("\\|");
        if (parts.length >= 2) {
            return parts[1];
        }
        return null;
    }

    private String key() {
        String sbnKey = this.user.getIdentifier() + NtpTrustedTime.NTP_SETTING_SERVER_NAME_DELIMITER + this.pkg + NtpTrustedTime.NTP_SETTING_SERVER_NAME_DELIMITER + this.f425id + NtpTrustedTime.NTP_SETTING_SERVER_NAME_DELIMITER + this.tag + NtpTrustedTime.NTP_SETTING_SERVER_NAME_DELIMITER + this.uid;
        if (this.overrideGroupKey != null && getNotification().isGroupSummary()) {
            return sbnKey + NtpTrustedTime.NTP_SETTING_SERVER_NAME_DELIMITER + this.overrideGroupKey;
        }
        return sbnKey;
    }

    private String groupKey() {
        String str;
        if (this.overrideGroupKey != null) {
            return this.user.getIdentifier() + NtpTrustedTime.NTP_SETTING_SERVER_NAME_DELIMITER + this.pkg + "|g:" + this.overrideGroupKey;
        }
        String group = getNotification().getGroup();
        String sortKey = getNotification().getSortKey();
        if (group == null && sortKey == null) {
            return this.key;
        }
        StringBuilder append = new StringBuilder().append(this.user.getIdentifier()).append(NtpTrustedTime.NTP_SETTING_SERVER_NAME_DELIMITER).append(this.pkg).append(NtpTrustedTime.NTP_SETTING_SERVER_NAME_DELIMITER);
        if (group == null) {
            str = "c:" + this.notification.getChannelId();
        } else {
            str = "g:" + group;
        }
        return append.append(str).toString();
    }

    public boolean isGroup() {
        if (this.overrideGroupKey != null || isAppGroup()) {
            return true;
        }
        return false;
    }

    public boolean isAppGroup() {
        if (getNotification().getGroup() != null || getNotification().getSortKey() != null) {
            return true;
        }
        return false;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(this.pkg);
        out.writeString(this.opPkg);
        out.writeInt(this.f425id);
        if (this.tag != null) {
            out.writeInt(1);
            out.writeString(this.tag);
        } else {
            out.writeInt(0);
        }
        out.writeInt(this.uid);
        out.writeInt(this.initialPid);
        this.notification.writeToParcel(out, flags);
        this.user.writeToParcel(out, flags);
        out.writeLong(this.postTime);
        if (this.overrideGroupKey != null) {
            out.writeInt(1);
            out.writeString(this.overrideGroupKey);
        } else {
            out.writeInt(0);
        }
        if (this.mInstanceId != null) {
            out.writeInt(1);
            this.mInstanceId.writeToParcel(out, flags);
            return;
        }
        out.writeInt(0);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    public StatusBarNotification cloneLight() {
        Notification no = new Notification();
        this.notification.cloneInto(no, false);
        return cloneShallow(no);
    }

    /* renamed from: clone */
    public StatusBarNotification m3711clone() {
        return cloneShallow(this.notification.m552clone());
    }

    StatusBarNotification cloneShallow(Notification notification) {
        StatusBarNotification result = new StatusBarNotification(this.pkg, this.opPkg, this.f425id, this.tag, this.uid, this.initialPid, notification, this.user, this.overrideGroupKey, this.postTime);
        result.setInstanceId(this.mInstanceId);
        return result;
    }

    public String toString() {
        return TextUtils.formatSimple("StatusBarNotification(pkg=%s user=%s id=%d tag=%s key=%s: %s)", this.pkg, this.user, Integer.valueOf(this.f425id), this.tag, this.key, this.notification);
    }

    public boolean isOngoing() {
        return (this.notification.flags & 2) != 0;
    }

    public boolean isNonDismissable() {
        return (this.notification.flags & 8192) != 0;
    }

    public boolean isClearable() {
        return (this.notification.flags & 2) == 0 && (this.notification.flags & 32) == 0;
    }

    @Deprecated
    public int getUserId() {
        return this.user.getIdentifier();
    }

    public int getNormalizedUserId() {
        int userId = getUserId();
        if (userId == -1) {
            return 0;
        }
        return userId;
    }

    public String getPackageName() {
        return this.pkg;
    }

    public int getId() {
        return this.f425id;
    }

    public String getTag() {
        return this.tag;
    }

    public int getUid() {
        return this.uid;
    }

    public String getOpPkg() {
        return this.opPkg;
    }

    public int getInitialPid() {
        return this.initialPid;
    }

    public Notification getNotification() {
        return this.notification;
    }

    public UserHandle getUser() {
        return this.user;
    }

    public long getPostTime() {
        return this.postTime;
    }

    public String getKey() {
        return this.key;
    }

    public String getGroupKey() {
        return this.groupKey;
    }

    public String getGroup() {
        String str = this.overrideGroupKey;
        if (str != null) {
            return str;
        }
        return getNotification().getGroup();
    }

    public void setOverrideGroupKey(String overrideGroupKey) {
        this.overrideGroupKey = overrideGroupKey;
        this.groupKey = groupKey();
    }

    public String getOverrideGroupKey() {
        return this.overrideGroupKey;
    }

    public void clearPackageContext() {
        this.mContext = null;
    }

    public InstanceId getInstanceId() {
        return this.mInstanceId;
    }

    public void setInstanceId(InstanceId instanceId) {
        this.mInstanceId = instanceId;
    }

    public Context getPackageContext(Context context) {
        if (this.mContext == null) {
            try {
                ApplicationInfo ai = context.getPackageManager().getApplicationInfoAsUser(this.pkg, 8192, getNormalizedUserId());
                this.mContext = context.createApplicationContext(ai, 4);
            } catch (PackageManager.NameNotFoundException e) {
                this.mContext = null;
            }
        }
        if (this.mContext == null) {
            this.mContext = context;
        }
        return this.mContext;
    }

    public LogMaker getLogMaker() {
        LogMaker logMaker = new LogMaker(0).setPackageName(getPackageName()).addTaggedData(MetricsProto.MetricsEvent.NOTIFICATION_ID, Integer.valueOf(getId())).addTaggedData(MetricsProto.MetricsEvent.NOTIFICATION_TAG, getTag()).addTaggedData(MetricsProto.MetricsEvent.FIELD_NOTIFICATION_CHANNEL_ID, getChannelIdLogTag()).addTaggedData(MetricsProto.MetricsEvent.FIELD_NOTIFICATION_GROUP_ID, getGroupLogTag()).addTaggedData(MetricsProto.MetricsEvent.FIELD_NOTIFICATION_GROUP_SUMMARY, Integer.valueOf(getNotification().isGroupSummary() ? 1 : 0)).addTaggedData(MetricsProto.MetricsEvent.FIELD_NOTIFICATION_CATEGORY, getNotification().category);
        if (getNotification().extras != null) {
            String template = getNotification().extras.getString(Notification.EXTRA_TEMPLATE);
            if (template != null && !template.isEmpty()) {
                logMaker.addTaggedData(1745, Integer.valueOf(template.hashCode()));
            }
            ArrayList<Person> people = getNotification().extras.getParcelableArrayList(Notification.EXTRA_PEOPLE_LIST, Person.class);
            if (people != null && !people.isEmpty()) {
                logMaker.addTaggedData(1744, Integer.valueOf(people.size()));
            }
        }
        return logMaker;
    }

    public String getShortcutId() {
        return getNotification().getShortcutId();
    }

    public String getGroupLogTag() {
        return shortenTag(getGroup());
    }

    public String getChannelIdLogTag() {
        if (this.notification.getChannelId() == null) {
            return null;
        }
        return shortenTag(this.notification.getChannelId());
    }

    private String shortenTag(String logTag) {
        if (logTag == null || logTag.length() <= 36) {
            return logTag;
        }
        String hash = Integer.toHexString(logTag.hashCode());
        return logTag.substring(0, (36 - hash.length()) - 1) + NativeLibraryHelper.CLEAR_ABI_OVERRIDE + hash;
    }
}
