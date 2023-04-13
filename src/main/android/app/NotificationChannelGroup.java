package android.app;

import android.annotation.SystemApi;
import android.content.p001pm.ParceledListSlice;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.proto.ProtoOutputStream;
import com.android.modules.utils.TypedXmlPullParser;
import com.android.modules.utils.TypedXmlSerializer;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.json.JSONException;
import org.json.JSONObject;
/* loaded from: classes.dex */
public final class NotificationChannelGroup implements Parcelable {
    private static final String ATT_BLOCKED = "blocked";
    private static final String ATT_DESC = "desc";
    private static final String ATT_ID = "id";
    private static final String ATT_NAME = "name";
    private static final String ATT_USER_LOCKED = "locked";
    public static final Parcelable.Creator<NotificationChannelGroup> CREATOR = new Parcelable.Creator<NotificationChannelGroup>() { // from class: android.app.NotificationChannelGroup.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public NotificationChannelGroup createFromParcel(Parcel in) {
            return new NotificationChannelGroup(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public NotificationChannelGroup[] newArray(int size) {
            return new NotificationChannelGroup[size];
        }
    };
    public static final int MAX_TEXT_LENGTH = 1000;
    private static final String TAG_GROUP = "channelGroup";
    public static final int USER_LOCKED_BLOCKED_STATE = 1;
    private boolean mBlocked;
    private List<NotificationChannel> mChannels;
    private String mDescription;
    private final String mId;
    private CharSequence mName;
    private int mUserLockedFields;

    public NotificationChannelGroup(String id, CharSequence name) {
        this.mChannels = new ArrayList();
        this.mId = getTrimmedString(id);
        this.mName = name != null ? getTrimmedString(name.toString()) : null;
    }

    protected NotificationChannelGroup(Parcel in) {
        this.mChannels = new ArrayList();
        if (in.readByte() != 0) {
            this.mId = getTrimmedString(in.readString());
        } else {
            this.mId = null;
        }
        if (in.readByte() != 0) {
            this.mName = getTrimmedString(in.readString());
        } else {
            this.mName = "";
        }
        if (in.readByte() != 0) {
            this.mDescription = getTrimmedString(in.readString());
        } else {
            this.mDescription = null;
        }
        if (in.readByte() != 0) {
            this.mChannels = ((ParceledListSlice) in.readParcelable(NotificationChannelGroup.class.getClassLoader(), ParceledListSlice.class)).getList();
        } else {
            this.mChannels = new ArrayList();
        }
        this.mBlocked = in.readBoolean();
        this.mUserLockedFields = in.readInt();
    }

    private String getTrimmedString(String input) {
        if (input != null && input.length() > 1000) {
            return input.substring(0, 1000);
        }
        return input;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        if (this.mId != null) {
            dest.writeByte((byte) 1);
            dest.writeString(this.mId);
        } else {
            dest.writeByte((byte) 0);
        }
        if (this.mName != null) {
            dest.writeByte((byte) 1);
            dest.writeString(this.mName.toString());
        } else {
            dest.writeByte((byte) 0);
        }
        if (this.mDescription != null) {
            dest.writeByte((byte) 1);
            dest.writeString(this.mDescription);
        } else {
            dest.writeByte((byte) 0);
        }
        if (this.mChannels != null) {
            dest.writeByte((byte) 1);
            dest.writeParcelable(new ParceledListSlice(this.mChannels), flags);
        } else {
            dest.writeByte((byte) 0);
        }
        dest.writeBoolean(this.mBlocked);
        dest.writeInt(this.mUserLockedFields);
    }

    public String getId() {
        return this.mId;
    }

    public CharSequence getName() {
        return this.mName;
    }

    public String getDescription() {
        return this.mDescription;
    }

    public List<NotificationChannel> getChannels() {
        return this.mChannels;
    }

    public boolean isBlocked() {
        return this.mBlocked;
    }

    public void setDescription(String description) {
        this.mDescription = getTrimmedString(description);
    }

    public void setBlocked(boolean blocked) {
        this.mBlocked = blocked;
    }

    public void addChannel(NotificationChannel channel) {
        this.mChannels.add(channel);
    }

    public void setChannels(List<NotificationChannel> channels) {
        this.mChannels = channels;
    }

    public void lockFields(int field) {
        this.mUserLockedFields |= field;
    }

    public void unlockFields(int field) {
        this.mUserLockedFields &= ~field;
    }

    public int getUserLockedFields() {
        return this.mUserLockedFields;
    }

    public void populateFromXml(TypedXmlPullParser parser) {
        setDescription(parser.getAttributeValue(null, ATT_DESC));
        setBlocked(parser.getAttributeBoolean(null, "blocked", false));
    }

    public void writeXml(TypedXmlSerializer out) throws IOException {
        out.startTag(null, TAG_GROUP);
        out.attribute(null, "id", getId());
        if (getName() != null) {
            out.attribute(null, "name", getName().toString());
        }
        if (getDescription() != null) {
            out.attribute(null, ATT_DESC, getDescription().toString());
        }
        out.attributeBoolean(null, "blocked", isBlocked());
        out.attributeInt(null, "locked", this.mUserLockedFields);
        out.endTag(null, TAG_GROUP);
    }

    @SystemApi
    public JSONObject toJson() throws JSONException {
        JSONObject record = new JSONObject();
        record.put("id", getId());
        record.put("name", getName());
        record.put(ATT_DESC, getDescription());
        record.put("blocked", isBlocked());
        record.put("locked", this.mUserLockedFields);
        return record;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        NotificationChannelGroup that = (NotificationChannelGroup) o;
        if (isBlocked() == that.isBlocked() && this.mUserLockedFields == that.mUserLockedFields && Objects.equals(getId(), that.getId()) && Objects.equals(getName(), that.getName()) && Objects.equals(getDescription(), that.getDescription()) && Objects.equals(getChannels(), that.getChannels())) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(getId(), getName(), getDescription(), Boolean.valueOf(isBlocked()), getChannels(), Integer.valueOf(this.mUserLockedFields));
    }

    /* renamed from: clone */
    public NotificationChannelGroup m589clone() {
        NotificationChannelGroup cloned = new NotificationChannelGroup(getId(), getName());
        cloned.setDescription(getDescription());
        cloned.setBlocked(isBlocked());
        cloned.setChannels(getChannels());
        cloned.lockFields(this.mUserLockedFields);
        return cloned;
    }

    public String toString() {
        return "NotificationChannelGroup{mId='" + this.mId + DateFormat.QUOTE + ", mName=" + ((Object) this.mName) + ", mDescription=" + (!TextUtils.isEmpty(this.mDescription) ? "hasDescription " : "") + ", mBlocked=" + this.mBlocked + ", mChannels=" + this.mChannels + ", mUserLockedFields=" + this.mUserLockedFields + '}';
    }

    public void dumpDebug(ProtoOutputStream proto, long fieldId) {
        long token = proto.start(fieldId);
        proto.write(1138166333441L, this.mId);
        proto.write(1138166333442L, this.mName.toString());
        proto.write(1138166333443L, this.mDescription);
        proto.write(1133871366148L, this.mBlocked);
        for (NotificationChannel channel : this.mChannels) {
            channel.dumpDebug(proto, 2246267895813L);
        }
        proto.end(token);
    }
}
