package android.app;

import android.annotation.SystemApi;
import android.content.ContentResolver;
import android.content.Context;
import android.media.AudioAttributes;
import android.net.Uri;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.provider.Settings;
import android.service.notification.NotificationListenerService;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.proto.ProtoOutputStream;
import com.android.internal.util.Preconditions;
import com.android.internal.util.XmlUtils;
import com.android.modules.utils.TypedXmlPullParser;
import com.android.modules.utils.TypedXmlSerializer;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Objects;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;
/* loaded from: classes.dex */
public final class NotificationChannel implements Parcelable {
    public static final int ALLOW_BUBBLE_OFF = 0;
    public static final int ALLOW_BUBBLE_ON = 1;
    private static final String ATT_ALLOW_BUBBLE = "allow_bubbles";
    private static final String ATT_BLOCKABLE_SYSTEM = "blockable_system";
    private static final String ATT_CONTENT_TYPE = "content_type";
    private static final String ATT_CONVERSATION_ID = "conv_id";
    private static final String ATT_DELETED = "deleted";
    private static final String ATT_DELETED_TIME_MS = "del_time";
    private static final String ATT_DEMOTE = "dem";
    private static final String ATT_DESC = "desc";
    private static final String ATT_FG_SERVICE_SHOWN = "fgservice";
    private static final String ATT_FLAGS = "flags";
    private static final String ATT_GROUP = "group";
    private static final String ATT_ID = "id";
    private static final String ATT_IMPORTANCE = "importance";
    private static final String ATT_IMP_CONVERSATION = "imp_conv";
    private static final String ATT_LIGHTS = "lights";
    private static final String ATT_LIGHT_COLOR = "light_color";
    private static final String ATT_NAME = "name";
    private static final String ATT_ORIG_IMP = "orig_imp";
    private static final String ATT_PARENT_CHANNEL = "parent";
    private static final String ATT_PRIORITY = "priority";
    private static final String ATT_SHOW_BADGE = "show_badge";
    private static final String ATT_SOUND = "sound";
    private static final String ATT_USAGE = "usage";
    private static final String ATT_USER_LOCKED = "locked";
    private static final String ATT_VIBRATION = "vibration";
    private static final String ATT_VIBRATION_ENABLED = "vibration_enabled";
    private static final String ATT_VISIBILITY = "visibility";
    public static final String CONVERSATION_CHANNEL_ID_FORMAT = "%1$s : %2$s";
    public static final int DEFAULT_ALLOW_BUBBLE = -1;
    public static final String DEFAULT_CHANNEL_ID = "miscellaneous";
    private static final boolean DEFAULT_DELETED = false;
    private static final long DEFAULT_DELETION_TIME_MS = -1;
    private static final int DEFAULT_IMPORTANCE = -1000;
    private static final int DEFAULT_LIGHT_COLOR = 0;
    private static final boolean DEFAULT_SHOW_BADGE = true;
    private static final int DEFAULT_VISIBILITY = -1000;
    private static final String DELIMITER = ",";
    public static final String EDIT_CONVERSATION = "conversation";
    public static final String EDIT_IMPORTANCE = "importance";
    public static final String EDIT_LAUNCHER = "launcher";
    public static final String EDIT_LOCKED_DEVICE = "locked";
    public static final String EDIT_SOUND = "sound";
    public static final String EDIT_VIBRATION = "vibration";
    public static final String EDIT_ZEN = "zen";
    public static final int MAX_TEXT_LENGTH = 1000;
    public static final int MAX_VIBRATION_LENGTH = 1000;
    public static final String PLACEHOLDER_CONVERSATION_ID = ":placeholder_id";
    private static final String TAG_CHANNEL = "channel";
    public static final int USER_LOCKED_ALLOW_BUBBLE = 256;
    public static final int USER_LOCKED_IMPORTANCE = 4;
    public static final int USER_LOCKED_LIGHTS = 8;
    public static final int USER_LOCKED_PRIORITY = 1;
    public static final int USER_LOCKED_SHOW_BADGE = 128;
    @SystemApi
    public static final int USER_LOCKED_SOUND = 32;
    public static final int USER_LOCKED_VIBRATION = 16;
    public static final int USER_LOCKED_VISIBILITY = 2;
    private int mAllowBubbles;
    private AudioAttributes mAudioAttributes;
    private boolean mBlockableSystem;
    private boolean mBypassDnd;
    private String mConversationId;
    private boolean mDeleted;
    private long mDeletedTime;
    private boolean mDemoted;
    private String mDesc;
    private boolean mFgServiceShown;
    private String mGroup;
    private String mId;
    private int mImportance;
    private boolean mImportanceLockedDefaultApp;
    private boolean mImportantConvo;
    private int mLightColor;
    private boolean mLights;
    private int mLockscreenVisibility;
    private String mName;
    private int mOriginalImportance;
    private String mParentId;
    private boolean mShowBadge;
    private Uri mSound;
    private int mUserLockedFields;
    private long[] mVibration;
    private boolean mVibrationEnabled;
    public static final int[] LOCKABLE_FIELDS = {1, 2, 4, 8, 16, 32, 128, 256};
    public static final Parcelable.Creator<NotificationChannel> CREATOR = new Parcelable.Creator<NotificationChannel>() { // from class: android.app.NotificationChannel.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public NotificationChannel createFromParcel(Parcel in) {
            return new NotificationChannel(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public NotificationChannel[] newArray(int size) {
            return new NotificationChannel[size];
        }
    };

    public NotificationChannel(String id, CharSequence name, int importance) {
        this.mImportance = -1000;
        this.mOriginalImportance = -1000;
        this.mLockscreenVisibility = -1000;
        this.mSound = Settings.System.DEFAULT_NOTIFICATION_URI;
        this.mLightColor = 0;
        this.mShowBadge = true;
        this.mDeleted = false;
        this.mAudioAttributes = Notification.AUDIO_ATTRIBUTES_DEFAULT;
        this.mBlockableSystem = false;
        this.mAllowBubbles = -1;
        this.mParentId = null;
        this.mConversationId = null;
        this.mDemoted = false;
        this.mImportantConvo = false;
        this.mDeletedTime = -1L;
        this.mId = getTrimmedString(id);
        this.mName = name != null ? getTrimmedString(name.toString()) : null;
        this.mImportance = importance;
    }

    protected NotificationChannel(Parcel in) {
        boolean z;
        boolean z2;
        boolean z3;
        boolean z4;
        boolean z5;
        this.mImportance = -1000;
        this.mOriginalImportance = -1000;
        this.mLockscreenVisibility = -1000;
        this.mSound = Settings.System.DEFAULT_NOTIFICATION_URI;
        this.mLightColor = 0;
        this.mShowBadge = true;
        this.mDeleted = false;
        this.mAudioAttributes = Notification.AUDIO_ATTRIBUTES_DEFAULT;
        this.mBlockableSystem = false;
        this.mAllowBubbles = -1;
        this.mParentId = null;
        this.mConversationId = null;
        this.mDemoted = false;
        this.mImportantConvo = false;
        this.mDeletedTime = -1L;
        if (in.readByte() != 0) {
            this.mId = getTrimmedString(in.readString());
        } else {
            this.mId = null;
        }
        if (in.readByte() != 0) {
            this.mName = getTrimmedString(in.readString());
        } else {
            this.mName = null;
        }
        if (in.readByte() != 0) {
            this.mDesc = getTrimmedString(in.readString());
        } else {
            this.mDesc = null;
        }
        this.mImportance = in.readInt();
        if (in.readByte() == 0) {
            z = false;
        } else {
            z = true;
        }
        this.mBypassDnd = z;
        this.mLockscreenVisibility = in.readInt();
        if (in.readByte() != 0) {
            Uri createFromParcel = Uri.CREATOR.createFromParcel(in);
            this.mSound = createFromParcel;
            this.mSound = Uri.parse(getTrimmedString(createFromParcel.toString()));
        } else {
            this.mSound = null;
        }
        if (in.readByte() == 0) {
            z2 = false;
        } else {
            z2 = true;
        }
        this.mLights = z2;
        long[] createLongArray = in.createLongArray();
        this.mVibration = createLongArray;
        if (createLongArray != null && createLongArray.length > 1000) {
            this.mVibration = Arrays.copyOf(createLongArray, 1000);
        }
        this.mUserLockedFields = in.readInt();
        if (in.readByte() == 0) {
            z3 = false;
        } else {
            z3 = true;
        }
        this.mFgServiceShown = z3;
        if (in.readByte() == 0) {
            z4 = false;
        } else {
            z4 = true;
        }
        this.mVibrationEnabled = z4;
        if (in.readByte() == 0) {
            z5 = false;
        } else {
            z5 = true;
        }
        this.mShowBadge = z5;
        this.mDeleted = in.readByte() != 0;
        if (in.readByte() != 0) {
            this.mGroup = getTrimmedString(in.readString());
        } else {
            this.mGroup = null;
        }
        this.mAudioAttributes = in.readInt() > 0 ? AudioAttributes.CREATOR.createFromParcel(in) : null;
        this.mLightColor = in.readInt();
        this.mBlockableSystem = in.readBoolean();
        this.mAllowBubbles = in.readInt();
        this.mOriginalImportance = in.readInt();
        this.mParentId = getTrimmedString(in.readString());
        this.mConversationId = getTrimmedString(in.readString());
        this.mDemoted = in.readBoolean();
        this.mImportantConvo = in.readBoolean();
        this.mDeletedTime = in.readLong();
        this.mImportanceLockedDefaultApp = in.readBoolean();
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
            dest.writeString(this.mName);
        } else {
            dest.writeByte((byte) 0);
        }
        if (this.mDesc != null) {
            dest.writeByte((byte) 1);
            dest.writeString(this.mDesc);
        } else {
            dest.writeByte((byte) 0);
        }
        dest.writeInt(this.mImportance);
        dest.writeByte(this.mBypassDnd ? (byte) 1 : (byte) 0);
        dest.writeInt(this.mLockscreenVisibility);
        if (this.mSound != null) {
            dest.writeByte((byte) 1);
            this.mSound.writeToParcel(dest, 0);
        } else {
            dest.writeByte((byte) 0);
        }
        dest.writeByte(this.mLights ? (byte) 1 : (byte) 0);
        dest.writeLongArray(this.mVibration);
        dest.writeInt(this.mUserLockedFields);
        dest.writeByte(this.mFgServiceShown ? (byte) 1 : (byte) 0);
        dest.writeByte(this.mVibrationEnabled ? (byte) 1 : (byte) 0);
        dest.writeByte(this.mShowBadge ? (byte) 1 : (byte) 0);
        dest.writeByte(this.mDeleted ? (byte) 1 : (byte) 0);
        if (this.mGroup != null) {
            dest.writeByte((byte) 1);
            dest.writeString(this.mGroup);
        } else {
            dest.writeByte((byte) 0);
        }
        if (this.mAudioAttributes != null) {
            dest.writeInt(1);
            this.mAudioAttributes.writeToParcel(dest, 0);
        } else {
            dest.writeInt(0);
        }
        dest.writeInt(this.mLightColor);
        dest.writeBoolean(this.mBlockableSystem);
        dest.writeInt(this.mAllowBubbles);
        dest.writeInt(this.mOriginalImportance);
        dest.writeString(this.mParentId);
        dest.writeString(this.mConversationId);
        dest.writeBoolean(this.mDemoted);
        dest.writeBoolean(this.mImportantConvo);
        dest.writeLong(this.mDeletedTime);
        dest.writeBoolean(this.mImportanceLockedDefaultApp);
    }

    public void lockFields(int field) {
        this.mUserLockedFields |= field;
    }

    public void unlockFields(int field) {
        this.mUserLockedFields &= ~field;
    }

    public void setFgServiceShown(boolean shown) {
        this.mFgServiceShown = shown;
    }

    public void setDeleted(boolean deleted) {
        this.mDeleted = deleted;
    }

    public void setDeletedTimeMs(long time) {
        this.mDeletedTime = time;
    }

    public void setImportantConversation(boolean importantConvo) {
        this.mImportantConvo = importantConvo;
    }

    public void setBlockable(boolean blockable) {
        this.mBlockableSystem = blockable;
    }

    public void setName(CharSequence name) {
        this.mName = name != null ? getTrimmedString(name.toString()) : null;
    }

    public void setDescription(String description) {
        this.mDesc = getTrimmedString(description);
    }

    private String getTrimmedString(String input) {
        if (input != null && input.length() > 1000) {
            return input.substring(0, 1000);
        }
        return input;
    }

    public void setId(String id) {
        this.mId = id;
    }

    public void setGroup(String groupId) {
        this.mGroup = groupId;
    }

    public void setShowBadge(boolean showBadge) {
        this.mShowBadge = showBadge;
    }

    public void setSound(Uri sound, AudioAttributes audioAttributes) {
        this.mSound = sound;
        this.mAudioAttributes = audioAttributes;
    }

    public void enableLights(boolean lights) {
        this.mLights = lights;
    }

    public void setLightColor(int argb) {
        this.mLightColor = argb;
    }

    public void enableVibration(boolean vibration) {
        this.mVibrationEnabled = vibration;
    }

    public void setVibrationPattern(long[] vibrationPattern) {
        this.mVibrationEnabled = vibrationPattern != null && vibrationPattern.length > 0;
        this.mVibration = vibrationPattern;
    }

    public void setImportance(int importance) {
        this.mImportance = importance;
    }

    public void setBypassDnd(boolean bypassDnd) {
        this.mBypassDnd = bypassDnd;
    }

    public void setLockscreenVisibility(int lockscreenVisibility) {
        this.mLockscreenVisibility = lockscreenVisibility;
    }

    public void setAllowBubbles(boolean allowBubbles) {
        this.mAllowBubbles = allowBubbles ? 1 : 0;
    }

    public void setAllowBubbles(int allowed) {
        this.mAllowBubbles = allowed;
    }

    public void setConversationId(String parentChannelId, String conversationId) {
        this.mParentId = parentChannelId;
        this.mConversationId = conversationId;
    }

    public String getId() {
        return this.mId;
    }

    public CharSequence getName() {
        return this.mName;
    }

    public String getDescription() {
        return this.mDesc;
    }

    public int getImportance() {
        return this.mImportance;
    }

    public boolean canBypassDnd() {
        return this.mBypassDnd;
    }

    public boolean isConversation() {
        return !TextUtils.isEmpty(getConversationId());
    }

    public boolean isImportantConversation() {
        return this.mImportantConvo;
    }

    public Uri getSound() {
        return this.mSound;
    }

    public AudioAttributes getAudioAttributes() {
        return this.mAudioAttributes;
    }

    public boolean shouldShowLights() {
        return this.mLights;
    }

    public int getLightColor() {
        return this.mLightColor;
    }

    public boolean shouldVibrate() {
        return this.mVibrationEnabled;
    }

    public long[] getVibrationPattern() {
        return this.mVibration;
    }

    public int getLockscreenVisibility() {
        return this.mLockscreenVisibility;
    }

    public boolean canShowBadge() {
        return this.mShowBadge;
    }

    public String getGroup() {
        return this.mGroup;
    }

    public boolean canBubble() {
        return this.mAllowBubbles == 1;
    }

    public int getAllowBubbles() {
        return this.mAllowBubbles;
    }

    public String getParentChannelId() {
        return this.mParentId;
    }

    public String getConversationId() {
        return this.mConversationId;
    }

    @SystemApi
    public boolean isDeleted() {
        return this.mDeleted;
    }

    public long getDeletedTimeMs() {
        return this.mDeletedTime;
    }

    @SystemApi
    public int getUserLockedFields() {
        return this.mUserLockedFields;
    }

    public boolean isFgServiceShown() {
        return this.mFgServiceShown;
    }

    public boolean isBlockable() {
        return this.mBlockableSystem;
    }

    public void setImportanceLockedByCriticalDeviceFunction(boolean locked) {
        this.mImportanceLockedDefaultApp = locked;
    }

    public boolean isImportanceLockedByCriticalDeviceFunction() {
        return this.mImportanceLockedDefaultApp;
    }

    public int getOriginalImportance() {
        return this.mOriginalImportance;
    }

    public void setOriginalImportance(int importance) {
        this.mOriginalImportance = importance;
    }

    public void setDemoted(boolean demoted) {
        this.mDemoted = demoted;
    }

    public boolean isDemoted() {
        return this.mDemoted;
    }

    public boolean hasUserSetImportance() {
        return (this.mUserLockedFields & 4) != 0;
    }

    public boolean hasUserSetSound() {
        return (this.mUserLockedFields & 32) != 0;
    }

    public void populateFromXmlForRestore(XmlPullParser parser, Context context) {
        populateFromXml(XmlUtils.makeTyped(parser), true, context);
    }

    @SystemApi
    public void populateFromXml(XmlPullParser parser) {
        populateFromXml(XmlUtils.makeTyped(parser), false, null);
    }

    private void populateFromXml(TypedXmlPullParser parser, boolean forRestore, Context context) {
        Preconditions.checkArgument((forRestore && context == null) ? false : true, "forRestore is true but got null context");
        setDescription(parser.getAttributeValue(null, ATT_DESC));
        setBypassDnd(safeInt(parser, "priority", 0) != 0);
        setLockscreenVisibility(safeInt(parser, "visibility", -1000));
        Uri sound = safeUri(parser, "sound");
        setSound(forRestore ? restoreSoundUri(context, sound) : sound, safeAudioAttributes(parser));
        enableLights(safeBool(parser, "lights", false));
        setLightColor(safeInt(parser, ATT_LIGHT_COLOR, 0));
        setVibrationPattern(safeLongArray(parser, "vibration", null));
        enableVibration(safeBool(parser, ATT_VIBRATION_ENABLED, false));
        setShowBadge(safeBool(parser, ATT_SHOW_BADGE, false));
        setDeleted(safeBool(parser, "deleted", false));
        setDeletedTimeMs(XmlUtils.readLongAttribute(parser, ATT_DELETED_TIME_MS, -1L));
        setGroup(parser.getAttributeValue(null, ATT_GROUP));
        lockFields(safeInt(parser, "locked", 0));
        setFgServiceShown(safeBool(parser, ATT_FG_SERVICE_SHOWN, false));
        setBlockable(safeBool(parser, ATT_BLOCKABLE_SYSTEM, false));
        setAllowBubbles(safeInt(parser, ATT_ALLOW_BUBBLE, -1));
        setOriginalImportance(safeInt(parser, ATT_ORIG_IMP, -1000));
        setConversationId(parser.getAttributeValue(null, "parent"), parser.getAttributeValue(null, ATT_CONVERSATION_ID));
        setDemoted(safeBool(parser, ATT_DEMOTE, false));
        setImportantConversation(safeBool(parser, ATT_IMP_CONVERSATION, false));
    }

    private Uri restoreSoundUri(Context context, Uri uri) {
        if (uri == null || Uri.EMPTY.equals(uri)) {
            return null;
        }
        ContentResolver contentResolver = context.getContentResolver();
        Uri canonicalizedUri = contentResolver.canonicalize(uri);
        if (canonicalizedUri == null) {
            return Settings.System.DEFAULT_NOTIFICATION_URI;
        }
        return contentResolver.uncanonicalize(canonicalizedUri);
    }

    @SystemApi
    public void writeXml(XmlSerializer out) throws IOException {
        writeXml(XmlUtils.makeTyped(out), false, null);
    }

    public void writeXmlForBackup(XmlSerializer out, Context context) throws IOException {
        writeXml(XmlUtils.makeTyped(out), true, context);
    }

    private Uri getSoundForBackup(Context context) {
        Uri sound = getSound();
        if (sound == null || Uri.EMPTY.equals(sound)) {
            return null;
        }
        Uri canonicalSound = context.getContentResolver().canonicalize(sound);
        if (canonicalSound == null) {
            return Settings.System.DEFAULT_NOTIFICATION_URI;
        }
        return canonicalSound;
    }

    private void writeXml(TypedXmlSerializer out, boolean forBackup, Context context) throws IOException {
        Preconditions.checkArgument((forBackup && context == null) ? false : true, "forBackup is true but got null context");
        out.startTag(null, "channel");
        out.attribute(null, "id", getId());
        if (getName() != null) {
            out.attribute(null, "name", getName().toString());
        }
        if (getDescription() != null) {
            out.attribute(null, ATT_DESC, getDescription());
        }
        if (getImportance() != -1000) {
            out.attributeInt(null, "importance", getImportance());
        }
        if (canBypassDnd()) {
            out.attributeInt(null, "priority", 2);
        }
        if (getLockscreenVisibility() != -1000) {
            out.attributeInt(null, "visibility", getLockscreenVisibility());
        }
        Uri sound = forBackup ? getSoundForBackup(context) : getSound();
        if (sound != null) {
            out.attribute(null, "sound", sound.toString());
        }
        if (getAudioAttributes() != null) {
            out.attributeInt(null, ATT_USAGE, getAudioAttributes().getUsage());
            out.attributeInt(null, ATT_CONTENT_TYPE, getAudioAttributes().getContentType());
            out.attributeInt(null, "flags", getAudioAttributes().getFlags());
        }
        if (shouldShowLights()) {
            out.attributeBoolean(null, "lights", shouldShowLights());
        }
        if (getLightColor() != 0) {
            out.attributeInt(null, ATT_LIGHT_COLOR, getLightColor());
        }
        if (shouldVibrate()) {
            out.attributeBoolean(null, ATT_VIBRATION_ENABLED, shouldVibrate());
        }
        if (getVibrationPattern() != null) {
            out.attribute(null, "vibration", longArrayToString(getVibrationPattern()));
        }
        if (getUserLockedFields() != 0) {
            out.attributeInt(null, "locked", getUserLockedFields());
        }
        if (isFgServiceShown()) {
            out.attributeBoolean(null, ATT_FG_SERVICE_SHOWN, isFgServiceShown());
        }
        if (canShowBadge()) {
            out.attributeBoolean(null, ATT_SHOW_BADGE, canShowBadge());
        }
        if (isDeleted()) {
            out.attributeBoolean(null, "deleted", isDeleted());
        }
        if (getDeletedTimeMs() >= 0) {
            out.attributeLong(null, ATT_DELETED_TIME_MS, getDeletedTimeMs());
        }
        if (getGroup() != null) {
            out.attribute(null, ATT_GROUP, getGroup());
        }
        if (isBlockable()) {
            out.attributeBoolean(null, ATT_BLOCKABLE_SYSTEM, isBlockable());
        }
        if (getAllowBubbles() != -1) {
            out.attributeInt(null, ATT_ALLOW_BUBBLE, getAllowBubbles());
        }
        if (getOriginalImportance() != -1000) {
            out.attributeInt(null, ATT_ORIG_IMP, getOriginalImportance());
        }
        if (getParentChannelId() != null) {
            out.attribute(null, "parent", getParentChannelId());
        }
        if (getConversationId() != null) {
            out.attribute(null, ATT_CONVERSATION_ID, getConversationId());
        }
        if (isDemoted()) {
            out.attributeBoolean(null, ATT_DEMOTE, isDemoted());
        }
        if (isImportantConversation()) {
            out.attributeBoolean(null, ATT_IMP_CONVERSATION, isImportantConversation());
        }
        out.endTag(null, "channel");
    }

    @SystemApi
    public JSONObject toJson() throws JSONException {
        JSONObject record = new JSONObject();
        record.put("id", getId());
        record.put("name", getName());
        record.put(ATT_DESC, getDescription());
        if (getImportance() != -1000) {
            record.put("importance", NotificationListenerService.Ranking.importanceToString(getImportance()));
        }
        if (canBypassDnd()) {
            record.put("priority", 2);
        }
        if (getLockscreenVisibility() != -1000) {
            record.put("visibility", Notification.visibilityToString(getLockscreenVisibility()));
        }
        if (getSound() != null) {
            record.put("sound", getSound().toString());
        }
        if (getAudioAttributes() != null) {
            record.put(ATT_USAGE, Integer.toString(getAudioAttributes().getUsage()));
            record.put(ATT_CONTENT_TYPE, Integer.toString(getAudioAttributes().getContentType()));
            record.put("flags", Integer.toString(getAudioAttributes().getFlags()));
        }
        record.put("lights", Boolean.toString(shouldShowLights()));
        record.put(ATT_LIGHT_COLOR, Integer.toString(getLightColor()));
        record.put(ATT_VIBRATION_ENABLED, Boolean.toString(shouldVibrate()));
        record.put("locked", Integer.toString(getUserLockedFields()));
        record.put(ATT_FG_SERVICE_SHOWN, Boolean.toString(isFgServiceShown()));
        record.put("vibration", longArrayToString(getVibrationPattern()));
        record.put(ATT_SHOW_BADGE, Boolean.toString(canShowBadge()));
        record.put("deleted", Boolean.toString(isDeleted()));
        record.put(ATT_DELETED_TIME_MS, Long.toString(getDeletedTimeMs()));
        record.put(ATT_GROUP, getGroup());
        record.put(ATT_BLOCKABLE_SYSTEM, isBlockable());
        record.put(ATT_ALLOW_BUBBLE, getAllowBubbles());
        return record;
    }

    private static AudioAttributes safeAudioAttributes(TypedXmlPullParser parser) {
        int usage = safeInt(parser, ATT_USAGE, 5);
        int contentType = safeInt(parser, ATT_CONTENT_TYPE, 4);
        int flags = safeInt(parser, "flags", 0);
        return new AudioAttributes.Builder().setUsage(usage).setContentType(contentType).setFlags(flags).build();
    }

    private static Uri safeUri(TypedXmlPullParser parser, String att) {
        String val = parser.getAttributeValue(null, att);
        if (val == null) {
            return null;
        }
        return Uri.parse(val);
    }

    private static int safeInt(TypedXmlPullParser parser, String att, int defValue) {
        return parser.getAttributeInt(null, att, defValue);
    }

    private static boolean safeBool(TypedXmlPullParser parser, String att, boolean defValue) {
        return parser.getAttributeBoolean(null, att, defValue);
    }

    private static long[] safeLongArray(TypedXmlPullParser parser, String att, long[] defValue) {
        String attributeValue = parser.getAttributeValue(null, att);
        if (TextUtils.isEmpty(attributeValue)) {
            return defValue;
        }
        String[] values = attributeValue.split(",");
        long[] longValues = new long[values.length];
        for (int i = 0; i < values.length; i++) {
            try {
                longValues[i] = Long.parseLong(values[i]);
            } catch (NumberFormatException e) {
                longValues[i] = 0;
            }
        }
        return longValues;
    }

    private static String longArrayToString(long[] values) {
        StringBuilder sb = new StringBuilder();
        if (values != null && values.length > 0) {
            for (int i = 0; i < values.length - 1; i++) {
                sb.append(values[i]).append(",");
            }
            int i2 = values.length;
            sb.append(values[i2 - 1]);
        }
        return sb.toString();
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
        NotificationChannel that = (NotificationChannel) o;
        if (getImportance() == that.getImportance() && this.mBypassDnd == that.mBypassDnd && getLockscreenVisibility() == that.getLockscreenVisibility() && this.mLights == that.mLights && getLightColor() == that.getLightColor() && getUserLockedFields() == that.getUserLockedFields() && isFgServiceShown() == that.isFgServiceShown() && this.mVibrationEnabled == that.mVibrationEnabled && this.mShowBadge == that.mShowBadge && isDeleted() == that.isDeleted() && getDeletedTimeMs() == that.getDeletedTimeMs() && isBlockable() == that.isBlockable() && this.mAllowBubbles == that.mAllowBubbles && Objects.equals(getId(), that.getId()) && Objects.equals(getName(), that.getName()) && Objects.equals(this.mDesc, that.mDesc) && Objects.equals(getSound(), that.getSound()) && Arrays.equals(this.mVibration, that.mVibration) && Objects.equals(getGroup(), that.getGroup()) && Objects.equals(getAudioAttributes(), that.getAudioAttributes()) && this.mImportanceLockedDefaultApp == that.mImportanceLockedDefaultApp && this.mOriginalImportance == that.mOriginalImportance && Objects.equals(getParentChannelId(), that.getParentChannelId()) && Objects.equals(getConversationId(), that.getConversationId()) && isDemoted() == that.isDemoted() && isImportantConversation() == that.isImportantConversation()) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        int result = Objects.hash(getId(), getName(), this.mDesc, Integer.valueOf(getImportance()), Boolean.valueOf(this.mBypassDnd), Integer.valueOf(getLockscreenVisibility()), getSound(), Boolean.valueOf(this.mLights), Integer.valueOf(getLightColor()), Integer.valueOf(getUserLockedFields()), Boolean.valueOf(isFgServiceShown()), Boolean.valueOf(this.mVibrationEnabled), Boolean.valueOf(this.mShowBadge), Boolean.valueOf(isDeleted()), Long.valueOf(getDeletedTimeMs()), getGroup(), getAudioAttributes(), Boolean.valueOf(isBlockable()), Integer.valueOf(this.mAllowBubbles), Boolean.valueOf(this.mImportanceLockedDefaultApp), Integer.valueOf(this.mOriginalImportance), this.mParentId, this.mConversationId, Boolean.valueOf(this.mDemoted), Boolean.valueOf(this.mImportantConvo));
        return (result * 31) + Arrays.hashCode(this.mVibration);
    }

    public void dump(PrintWriter pw, String prefix, boolean redacted) {
        String redactedName = this.mName;
        if (redacted) {
            redactedName = (String) TextUtils.trimToLengthWithEllipsis(redactedName, 3);
        }
        String output = "NotificationChannel{mId='" + this.mId + DateFormat.QUOTE + ", mName=" + redactedName + getFieldsString() + '}';
        pw.println(prefix + output);
    }

    public String toString() {
        return "NotificationChannel{mId='" + this.mId + DateFormat.QUOTE + ", mName=" + this.mName + getFieldsString() + '}';
    }

    private String getFieldsString() {
        return ", mDescription=" + (!TextUtils.isEmpty(this.mDesc) ? "hasDescription " : "") + ", mImportance=" + this.mImportance + ", mBypassDnd=" + this.mBypassDnd + ", mLockscreenVisibility=" + this.mLockscreenVisibility + ", mSound=" + this.mSound + ", mLights=" + this.mLights + ", mLightColor=" + this.mLightColor + ", mVibration=" + Arrays.toString(this.mVibration) + ", mUserLockedFields=" + Integer.toHexString(this.mUserLockedFields) + ", mFgServiceShown=" + this.mFgServiceShown + ", mVibrationEnabled=" + this.mVibrationEnabled + ", mShowBadge=" + this.mShowBadge + ", mDeleted=" + this.mDeleted + ", mDeletedTimeMs=" + this.mDeletedTime + ", mGroup='" + this.mGroup + DateFormat.QUOTE + ", mAudioAttributes=" + this.mAudioAttributes + ", mBlockableSystem=" + this.mBlockableSystem + ", mAllowBubbles=" + this.mAllowBubbles + ", mImportanceLockedDefaultApp=" + this.mImportanceLockedDefaultApp + ", mOriginalImp=" + this.mOriginalImportance + ", mParent=" + this.mParentId + ", mConversationId=" + this.mConversationId + ", mDemoted=" + this.mDemoted + ", mImportantConvo=" + this.mImportantConvo;
    }

    public void dumpDebug(ProtoOutputStream proto, long fieldId) {
        long token = proto.start(fieldId);
        proto.write(1138166333441L, this.mId);
        proto.write(1138166333442L, this.mName);
        proto.write(1138166333443L, this.mDesc);
        proto.write(1120986464260L, this.mImportance);
        proto.write(1133871366149L, this.mBypassDnd);
        proto.write(1120986464262L, this.mLockscreenVisibility);
        Uri uri = this.mSound;
        if (uri != null) {
            proto.write(1138166333447L, uri.toString());
        }
        proto.write(1133871366152L, this.mLights);
        proto.write(1120986464265L, this.mLightColor);
        long[] jArr = this.mVibration;
        if (jArr != null) {
            for (long v : jArr) {
                proto.write(NotificationChannelProto.VIBRATION, v);
            }
        }
        proto.write(1120986464267L, this.mUserLockedFields);
        proto.write(1133871366162L, this.mFgServiceShown);
        proto.write(1133871366156L, this.mVibrationEnabled);
        proto.write(1133871366157L, this.mShowBadge);
        proto.write(1133871366158L, this.mDeleted);
        proto.write(1138166333455L, this.mGroup);
        AudioAttributes audioAttributes = this.mAudioAttributes;
        if (audioAttributes != null) {
            audioAttributes.dumpDebug(proto, 1146756268048L);
        }
        proto.write(1133871366161L, this.mBlockableSystem);
        proto.write(1133871366163L, this.mAllowBubbles);
        proto.end(token);
    }
}
