package android.app.people;

import android.graphics.drawable.Icon;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.text.format.DateFormat;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Objects;
/* loaded from: classes.dex */
public final class ConversationStatus implements Parcelable {
    public static final int ACTIVITY_ANNIVERSARY = 2;
    public static final int ACTIVITY_AUDIO = 4;
    public static final int ACTIVITY_BIRTHDAY = 1;
    public static final int ACTIVITY_GAME = 6;
    public static final int ACTIVITY_LOCATION = 7;
    public static final int ACTIVITY_NEW_STORY = 3;
    public static final int ACTIVITY_OTHER = 0;
    public static final int ACTIVITY_UPCOMING_BIRTHDAY = 8;
    public static final int ACTIVITY_VIDEO = 5;
    public static final int AVAILABILITY_AVAILABLE = 0;
    public static final int AVAILABILITY_BUSY = 1;
    public static final int AVAILABILITY_OFFLINE = 2;
    public static final int AVAILABILITY_UNKNOWN = -1;
    public static final Parcelable.Creator<ConversationStatus> CREATOR = new Parcelable.Creator<ConversationStatus>() { // from class: android.app.people.ConversationStatus.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ConversationStatus createFromParcel(Parcel source) {
            return new ConversationStatus(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ConversationStatus[] newArray(int size) {
            return new ConversationStatus[size];
        }
    };
    private static final String TAG = "ConversationStatus";
    private final int mActivity;
    private int mAvailability;
    private CharSequence mDescription;
    private long mEndTimeMs;
    private Icon mIcon;
    private final String mId;
    private long mStartTimeMs;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface ActivityType {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface Availability {
    }

    private ConversationStatus(Builder b) {
        this.mId = b.mId;
        this.mActivity = b.mActivity;
        this.mAvailability = b.mAvailability;
        this.mDescription = b.mDescription;
        this.mIcon = b.mIcon;
        this.mStartTimeMs = b.mStartTimeMs;
        this.mEndTimeMs = b.mEndTimeMs;
    }

    private ConversationStatus(Parcel p) {
        this.mId = p.readString();
        this.mActivity = p.readInt();
        this.mAvailability = p.readInt();
        this.mDescription = p.readCharSequence();
        this.mIcon = (Icon) p.readParcelable(Icon.class.getClassLoader(), Icon.class);
        this.mStartTimeMs = p.readLong();
        this.mEndTimeMs = p.readLong();
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mId);
        dest.writeInt(this.mActivity);
        dest.writeInt(this.mAvailability);
        dest.writeCharSequence(this.mDescription);
        dest.writeParcelable(this.mIcon, flags);
        dest.writeLong(this.mStartTimeMs);
        dest.writeLong(this.mEndTimeMs);
    }

    public String getId() {
        return this.mId;
    }

    public int getActivity() {
        return this.mActivity;
    }

    public int getAvailability() {
        return this.mAvailability;
    }

    public CharSequence getDescription() {
        return this.mDescription;
    }

    public Icon getIcon() {
        return this.mIcon;
    }

    public long getStartTimeMillis() {
        return this.mStartTimeMs;
    }

    public long getEndTimeMillis() {
        return this.mEndTimeMs;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ConversationStatus that = (ConversationStatus) o;
        if (this.mActivity == that.mActivity && this.mAvailability == that.mAvailability && this.mStartTimeMs == that.mStartTimeMs && this.mEndTimeMs == that.mEndTimeMs && this.mId.equals(that.mId) && Objects.equals(this.mDescription, that.mDescription) && Objects.equals(this.mIcon, that.mIcon)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(this.mId, Integer.valueOf(this.mActivity), Integer.valueOf(this.mAvailability), this.mDescription, this.mIcon, Long.valueOf(this.mStartTimeMs), Long.valueOf(this.mEndTimeMs));
    }

    public String toString() {
        return "ConversationStatus{mId='" + this.mId + DateFormat.QUOTE + ", mActivity=" + this.mActivity + ", mAvailability=" + this.mAvailability + ", mDescription=" + ((Object) this.mDescription) + ", mIcon=" + this.mIcon + ", mStartTimeMs=" + this.mStartTimeMs + ", mEndTimeMs=" + this.mEndTimeMs + '}';
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    /* loaded from: classes.dex */
    public static final class Builder {
        final int mActivity;
        CharSequence mDescription;
        Icon mIcon;
        final String mId;
        int mAvailability = -1;
        long mStartTimeMs = -1;
        long mEndTimeMs = -1;

        public Builder(String id, int activity) {
            this.mId = id;
            this.mActivity = activity;
        }

        public Builder setAvailability(int availability) {
            this.mAvailability = availability;
            return this;
        }

        public Builder setDescription(CharSequence description) {
            this.mDescription = description;
            return this;
        }

        public Builder setIcon(Icon icon) {
            this.mIcon = icon;
            return this;
        }

        public Builder setStartTimeMillis(long startTimeMs) {
            this.mStartTimeMs = startTimeMs;
            return this;
        }

        public Builder setEndTimeMillis(long endTimeMs) {
            this.mEndTimeMs = endTimeMs;
            return this;
        }

        public ConversationStatus build() {
            return new ConversationStatus(this);
        }
    }
}
