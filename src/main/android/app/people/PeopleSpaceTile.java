package android.app.people;

import android.app.Person;
import android.content.Intent;
import android.content.p001pm.LauncherApps;
import android.content.p001pm.ShortcutInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.p008os.UserHandle;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public class PeopleSpaceTile implements Parcelable {
    public static final int BLOCK_CONVERSATIONS = 2;
    public static final Parcelable.Creator<PeopleSpaceTile> CREATOR = new Parcelable.Creator<PeopleSpaceTile>() { // from class: android.app.people.PeopleSpaceTile.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public PeopleSpaceTile createFromParcel(Parcel source) {
            return new PeopleSpaceTile(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public PeopleSpaceTile[] newArray(int size) {
            return new PeopleSpaceTile[size];
        }
    };
    public static final int SHOW_CONTACTS = 16;
    public static final int SHOW_CONVERSATIONS = 1;
    public static final int SHOW_IMPORTANT_CONVERSATIONS = 4;
    public static final int SHOW_STARRED_CONTACTS = 8;
    private String mBirthdayText;
    private boolean mCanBypassDnd;
    private float mContactAffinity;
    private Uri mContactUri;
    private String mId;
    private Intent mIntent;
    private boolean mIsImportantConversation;
    private boolean mIsPackageSuspended;
    private boolean mIsUserQuieted;
    private long mLastInteractionTimestamp;
    private int mMessagesCount;
    private String mNotificationCategory;
    private CharSequence mNotificationContent;
    private Uri mNotificationDataUri;
    private String mNotificationKey;
    private int mNotificationPolicyState;
    private CharSequence mNotificationSender;
    private long mNotificationTimestamp;
    private String mPackageName;
    private List<ConversationStatus> mStatuses;
    private UserHandle mUserHandle;
    private Icon mUserIcon;
    private CharSequence mUserName;

    private PeopleSpaceTile(Builder b) {
        this.mId = b.mId;
        this.mUserName = b.mUserName;
        this.mUserIcon = b.mUserIcon;
        this.mContactUri = b.mContactUri;
        this.mUserHandle = b.mUserHandle;
        this.mPackageName = b.mPackageName;
        this.mBirthdayText = b.mBirthdayText;
        this.mLastInteractionTimestamp = b.mLastInteractionTimestamp;
        this.mIsImportantConversation = b.mIsImportantConversation;
        this.mNotificationKey = b.mNotificationKey;
        this.mNotificationContent = b.mNotificationContent;
        this.mNotificationSender = b.mNotificationSender;
        this.mNotificationCategory = b.mNotificationCategory;
        this.mNotificationDataUri = b.mNotificationDataUri;
        this.mMessagesCount = b.mMessagesCount;
        this.mIntent = b.mIntent;
        this.mNotificationTimestamp = b.mNotificationTimestamp;
        this.mStatuses = b.mStatuses;
        this.mCanBypassDnd = b.mCanBypassDnd;
        this.mIsPackageSuspended = b.mIsPackageSuspended;
        this.mIsUserQuieted = b.mIsUserQuieted;
        this.mNotificationPolicyState = b.mNotificationPolicyState;
        this.mContactAffinity = b.mContactAffinity;
    }

    public String getId() {
        return this.mId;
    }

    public CharSequence getUserName() {
        return this.mUserName;
    }

    public Icon getUserIcon() {
        return this.mUserIcon;
    }

    public Uri getContactUri() {
        return this.mContactUri;
    }

    public UserHandle getUserHandle() {
        return this.mUserHandle;
    }

    public String getPackageName() {
        return this.mPackageName;
    }

    public String getBirthdayText() {
        return this.mBirthdayText;
    }

    public long getLastInteractionTimestamp() {
        return this.mLastInteractionTimestamp;
    }

    public boolean isImportantConversation() {
        return this.mIsImportantConversation;
    }

    public String getNotificationKey() {
        return this.mNotificationKey;
    }

    public CharSequence getNotificationContent() {
        return this.mNotificationContent;
    }

    public CharSequence getNotificationSender() {
        return this.mNotificationSender;
    }

    public String getNotificationCategory() {
        return this.mNotificationCategory;
    }

    public Uri getNotificationDataUri() {
        return this.mNotificationDataUri;
    }

    public int getMessagesCount() {
        return this.mMessagesCount;
    }

    public Intent getIntent() {
        return this.mIntent;
    }

    public long getNotificationTimestamp() {
        return this.mNotificationTimestamp;
    }

    public List<ConversationStatus> getStatuses() {
        return this.mStatuses;
    }

    public boolean canBypassDnd() {
        return this.mCanBypassDnd;
    }

    public boolean isPackageSuspended() {
        return this.mIsPackageSuspended;
    }

    public boolean isUserQuieted() {
        return this.mIsUserQuieted;
    }

    public int getNotificationPolicyState() {
        return this.mNotificationPolicyState;
    }

    public float getContactAffinity() {
        return this.mContactAffinity;
    }

    public Builder toBuilder() {
        Builder builder = new Builder(this.mId, this.mUserName, this.mUserIcon, this.mIntent);
        builder.setContactUri(this.mContactUri);
        builder.setUserHandle(this.mUserHandle);
        builder.setPackageName(this.mPackageName);
        builder.setBirthdayText(this.mBirthdayText);
        builder.setLastInteractionTimestamp(this.mLastInteractionTimestamp);
        builder.setIsImportantConversation(this.mIsImportantConversation);
        builder.setNotificationKey(this.mNotificationKey);
        builder.setNotificationContent(this.mNotificationContent);
        builder.setNotificationSender(this.mNotificationSender);
        builder.setNotificationCategory(this.mNotificationCategory);
        builder.setNotificationDataUri(this.mNotificationDataUri);
        builder.setMessagesCount(this.mMessagesCount);
        builder.setIntent(this.mIntent);
        builder.setNotificationTimestamp(this.mNotificationTimestamp);
        builder.setStatuses(this.mStatuses);
        builder.setCanBypassDnd(this.mCanBypassDnd);
        builder.setIsPackageSuspended(this.mIsPackageSuspended);
        builder.setIsUserQuieted(this.mIsUserQuieted);
        builder.setNotificationPolicyState(this.mNotificationPolicyState);
        builder.setContactAffinity(this.mContactAffinity);
        return builder;
    }

    /* loaded from: classes.dex */
    public static class Builder {
        private String mBirthdayText;
        private boolean mCanBypassDnd;
        private float mContactAffinity;
        private Uri mContactUri;
        private String mId;
        private Intent mIntent;
        private boolean mIsImportantConversation;
        private boolean mIsPackageSuspended;
        private boolean mIsUserQuieted;
        private long mLastInteractionTimestamp;
        private int mMessagesCount;
        private String mNotificationCategory;
        private CharSequence mNotificationContent;
        private Uri mNotificationDataUri;
        private String mNotificationKey;
        private int mNotificationPolicyState;
        private CharSequence mNotificationSender;
        private long mNotificationTimestamp;
        private String mPackageName;
        private List<ConversationStatus> mStatuses;
        private UserHandle mUserHandle;
        private Icon mUserIcon;
        private CharSequence mUserName;

        public Builder(String id, CharSequence userName, Icon userIcon, Intent intent) {
            this.mId = id;
            this.mUserName = userName;
            this.mUserIcon = userIcon;
            this.mIntent = intent;
            this.mPackageName = intent == null ? null : intent.getPackage();
            this.mNotificationPolicyState = 1;
        }

        public Builder(ShortcutInfo info, LauncherApps launcherApps) {
            this.mId = info.getId();
            this.mUserName = info.getLabel();
            this.mUserIcon = PeopleSpaceTile.convertDrawableToIcon(launcherApps.getShortcutIconDrawable(info, 0));
            this.mUserHandle = info.getUserHandle();
            this.mPackageName = info.getPackage();
            this.mContactUri = getContactUri(info);
            this.mNotificationPolicyState = 1;
        }

        public Builder(ConversationChannel channel, LauncherApps launcherApps) {
            ShortcutInfo info = channel.getShortcutInfo();
            this.mId = info.getId();
            this.mUserName = info.getLabel();
            boolean z = false;
            this.mUserIcon = PeopleSpaceTile.convertDrawableToIcon(launcherApps.getShortcutIconDrawable(info, 0));
            this.mUserHandle = info.getUserHandle();
            this.mPackageName = info.getPackage();
            this.mContactUri = getContactUri(info);
            this.mStatuses = channel.getStatuses();
            this.mLastInteractionTimestamp = channel.getLastEventTimestamp();
            this.mIsImportantConversation = channel.getNotificationChannel() != null && channel.getNotificationChannel().isImportantConversation();
            if (channel.getNotificationChannel() != null && channel.getNotificationChannel().canBypassDnd()) {
                z = true;
            }
            this.mCanBypassDnd = z;
            this.mNotificationPolicyState = 1;
        }

        public Uri getContactUri(ShortcutInfo info) {
            if (info.getPersons() == null || info.getPersons().length != 1) {
                return null;
            }
            Person person = info.getPersons()[0];
            if (person.getUri() == null) {
                return null;
            }
            return Uri.parse(person.getUri());
        }

        public Builder setId(String id) {
            this.mId = id;
            return this;
        }

        public Builder setUserName(CharSequence userName) {
            this.mUserName = userName;
            return this;
        }

        public Builder setUserIcon(Icon userIcon) {
            this.mUserIcon = userIcon;
            return this;
        }

        public Builder setContactUri(Uri uri) {
            this.mContactUri = uri;
            return this;
        }

        public Builder setUserHandle(UserHandle userHandle) {
            this.mUserHandle = userHandle;
            return this;
        }

        public Builder setPackageName(String packageName) {
            this.mPackageName = packageName;
            return this;
        }

        public Builder setBirthdayText(String birthdayText) {
            this.mBirthdayText = birthdayText;
            return this;
        }

        public Builder setLastInteractionTimestamp(long lastInteractionTimestamp) {
            this.mLastInteractionTimestamp = lastInteractionTimestamp;
            return this;
        }

        public Builder setIsImportantConversation(boolean isImportantConversation) {
            this.mIsImportantConversation = isImportantConversation;
            return this;
        }

        public Builder setNotificationKey(String notificationKey) {
            this.mNotificationKey = notificationKey;
            return this;
        }

        public Builder setNotificationContent(CharSequence notificationContent) {
            this.mNotificationContent = notificationContent;
            return this;
        }

        public Builder setNotificationSender(CharSequence notificationSender) {
            this.mNotificationSender = notificationSender;
            return this;
        }

        public Builder setNotificationCategory(String notificationCategory) {
            this.mNotificationCategory = notificationCategory;
            return this;
        }

        public Builder setNotificationDataUri(Uri notificationDataUri) {
            this.mNotificationDataUri = notificationDataUri;
            return this;
        }

        public Builder setMessagesCount(int messagesCount) {
            this.mMessagesCount = messagesCount;
            return this;
        }

        public Builder setIntent(Intent intent) {
            this.mIntent = intent;
            return this;
        }

        public Builder setNotificationTimestamp(long notificationTimestamp) {
            this.mNotificationTimestamp = notificationTimestamp;
            return this;
        }

        public Builder setStatuses(List<ConversationStatus> statuses) {
            this.mStatuses = statuses;
            return this;
        }

        public Builder setCanBypassDnd(boolean canBypassDnd) {
            this.mCanBypassDnd = canBypassDnd;
            return this;
        }

        public Builder setIsPackageSuspended(boolean isPackageSuspended) {
            this.mIsPackageSuspended = isPackageSuspended;
            return this;
        }

        public Builder setIsUserQuieted(boolean isUserQuieted) {
            this.mIsUserQuieted = isUserQuieted;
            return this;
        }

        public Builder setNotificationPolicyState(int notificationPolicyState) {
            this.mNotificationPolicyState = notificationPolicyState;
            return this;
        }

        public Builder setContactAffinity(float contactAffinity) {
            this.mContactAffinity = contactAffinity;
            return this;
        }

        public PeopleSpaceTile build() {
            return new PeopleSpaceTile(this);
        }
    }

    public PeopleSpaceTile(Parcel in) {
        this.mId = in.readString();
        this.mUserName = in.readCharSequence();
        this.mUserIcon = (Icon) in.readParcelable(Icon.class.getClassLoader(), Icon.class);
        this.mContactUri = (Uri) in.readParcelable(Uri.class.getClassLoader(), Uri.class);
        this.mUserHandle = (UserHandle) in.readParcelable(UserHandle.class.getClassLoader(), UserHandle.class);
        this.mPackageName = in.readString();
        this.mBirthdayText = in.readString();
        this.mLastInteractionTimestamp = in.readLong();
        this.mIsImportantConversation = in.readBoolean();
        this.mNotificationKey = in.readString();
        this.mNotificationContent = in.readCharSequence();
        this.mNotificationSender = in.readCharSequence();
        this.mNotificationCategory = in.readString();
        this.mNotificationDataUri = (Uri) in.readParcelable(Uri.class.getClassLoader(), Uri.class);
        this.mMessagesCount = in.readInt();
        this.mIntent = (Intent) in.readParcelable(Intent.class.getClassLoader(), Intent.class);
        this.mNotificationTimestamp = in.readLong();
        ArrayList arrayList = new ArrayList();
        this.mStatuses = arrayList;
        in.readParcelableList(arrayList, ConversationStatus.class.getClassLoader(), ConversationStatus.class);
        this.mCanBypassDnd = in.readBoolean();
        this.mIsPackageSuspended = in.readBoolean();
        this.mIsUserQuieted = in.readBoolean();
        this.mNotificationPolicyState = in.readInt();
        this.mContactAffinity = in.readFloat();
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mId);
        dest.writeCharSequence(this.mUserName);
        dest.writeParcelable(this.mUserIcon, flags);
        dest.writeParcelable(this.mContactUri, flags);
        dest.writeParcelable(this.mUserHandle, flags);
        dest.writeString(this.mPackageName);
        dest.writeString(this.mBirthdayText);
        dest.writeLong(this.mLastInteractionTimestamp);
        dest.writeBoolean(this.mIsImportantConversation);
        dest.writeString(this.mNotificationKey);
        dest.writeCharSequence(this.mNotificationContent);
        dest.writeCharSequence(this.mNotificationSender);
        dest.writeString(this.mNotificationCategory);
        dest.writeParcelable(this.mNotificationDataUri, flags);
        dest.writeInt(this.mMessagesCount);
        dest.writeParcelable(this.mIntent, flags);
        dest.writeLong(this.mNotificationTimestamp);
        dest.writeParcelableList(this.mStatuses, flags);
        dest.writeBoolean(this.mCanBypassDnd);
        dest.writeBoolean(this.mIsPackageSuspended);
        dest.writeBoolean(this.mIsUserQuieted);
        dest.writeInt(this.mNotificationPolicyState);
        dest.writeFloat(this.mContactAffinity);
    }

    public static Icon convertDrawableToIcon(Drawable drawable) {
        Bitmap bitmap;
        if (drawable == null) {
            return null;
        }
        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null) {
                return Icon.createWithBitmap(bitmapDrawable.getBitmap());
            }
        }
        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return Icon.createWithBitmap(bitmap);
    }
}
