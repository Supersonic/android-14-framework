package android.media.p007tv;

import android.net.Uri;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
/* renamed from: android.media.tv.TvRecordingInfo */
/* loaded from: classes2.dex */
public final class TvRecordingInfo implements Parcelable {
    public static final Parcelable.Creator<TvRecordingInfo> CREATOR = new Parcelable.Creator<TvRecordingInfo>() { // from class: android.media.tv.TvRecordingInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public TvRecordingInfo createFromParcel(Parcel in) {
            return new TvRecordingInfo(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public TvRecordingInfo[] newArray(int size) {
            return new TvRecordingInfo[size];
        }
    };
    public static final int FRIDAY = 32;
    public static final int MONDAY = 2;
    public static final int RECORDING_ALL = 3;
    public static final int RECORDING_IN_PROGRESS = 2;
    public static final int RECORDING_SCHEDULED = 1;
    public static final int SATURDAY = 64;
    public static final int SUNDAY = 1;
    public static final int THURSDAY = 16;
    public static final int TUESDAY = 4;
    public static final int WEDNESDAY = 8;
    private Uri mChannelUri;
    private List<TvContentRating> mContentRatings;
    private String mDescription;
    private long mEndPaddingMillis;
    private String mName;
    private Uri mProgramUri;
    private long mRecordingDurationMillis;
    private String mRecordingId;
    private long mRecordingStartTimeMillis;
    private Uri mRecordingUri;
    private int mRepeatDays;
    private long mScheduledDurationMillis;
    private long mScheduledStartTimeMillis;
    private long mStartPaddingMillis;

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.media.tv.TvRecordingInfo$DaysOfWeek */
    /* loaded from: classes2.dex */
    public @interface DaysOfWeek {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.media.tv.TvRecordingInfo$TvRecordingListType */
    /* loaded from: classes2.dex */
    public @interface TvRecordingListType {
    }

    public TvRecordingInfo(String recordingId, long startPadding, long endPadding, int repeatDays, String name, String description, long scheduledStartTime, long scheduledDuration, Uri channelUri, Uri programUri, List<TvContentRating> contentRatings, Uri recordingUri, long recordingStartTime, long recordingDuration) {
        this.mRecordingId = recordingId;
        this.mStartPaddingMillis = startPadding;
        this.mEndPaddingMillis = endPadding;
        this.mRepeatDays = repeatDays;
        this.mName = name;
        this.mDescription = description;
        this.mScheduledStartTimeMillis = scheduledStartTime;
        this.mScheduledDurationMillis = scheduledDuration;
        this.mChannelUri = channelUri;
        this.mProgramUri = programUri;
        this.mContentRatings = contentRatings;
        this.mRecordingUri = recordingUri;
        this.mRecordingStartTimeMillis = recordingStartTime;
        this.mRecordingDurationMillis = recordingDuration;
    }

    public String getRecordingId() {
        return this.mRecordingId;
    }

    public long getStartPaddingMillis() {
        return this.mStartPaddingMillis;
    }

    public long getEndPaddingMillis() {
        return this.mEndPaddingMillis;
    }

    public int getRepeatDays() {
        return this.mRepeatDays;
    }

    public String getName() {
        return this.mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public String getDescription() {
        return this.mDescription;
    }

    public void setDescription(String description) {
        this.mDescription = description;
    }

    public long getScheduledStartTimeMillis() {
        return this.mScheduledStartTimeMillis;
    }

    public long getScheduledDurationMillis() {
        return this.mScheduledDurationMillis;
    }

    public Uri getChannelUri() {
        return this.mChannelUri;
    }

    public Uri getProgramUri() {
        return this.mProgramUri;
    }

    public List<TvContentRating> getContentRatings() {
        return this.mContentRatings;
    }

    public Uri getRecordingUri() {
        return this.mRecordingUri;
    }

    public long getRecordingStartTimeMillis() {
        return this.mRecordingStartTimeMillis;
    }

    public long getRecordingDurationMillis() {
        return this.mRecordingDurationMillis;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mRecordingId);
        dest.writeLong(this.mStartPaddingMillis);
        dest.writeLong(this.mEndPaddingMillis);
        dest.writeInt(this.mRepeatDays);
        dest.writeString(this.mName);
        dest.writeString(this.mDescription);
        dest.writeLong(this.mScheduledStartTimeMillis);
        dest.writeLong(this.mScheduledDurationMillis);
        Uri uri = this.mChannelUri;
        dest.writeString(uri == null ? null : uri.toString());
        Uri uri2 = this.mProgramUri;
        dest.writeString(uri2 == null ? null : uri2.toString());
        final List<String> flattenedContentRatings = new ArrayList<>();
        this.mContentRatings.forEach(new Consumer() { // from class: android.media.tv.TvRecordingInfo$$ExternalSyntheticLambda1
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                flattenedContentRatings.add(((TvContentRating) obj).flattenToString());
            }
        });
        dest.writeList(this.mContentRatings);
        dest.writeString(this.mRecordingUri != null ? this.mProgramUri.toString() : null);
        dest.writeLong(this.mRecordingDurationMillis);
        dest.writeLong(this.mRecordingStartTimeMillis);
    }

    private TvRecordingInfo(Parcel in) {
        this.mRecordingId = in.readString();
        this.mStartPaddingMillis = in.readLong();
        this.mEndPaddingMillis = in.readLong();
        this.mRepeatDays = in.readInt();
        this.mName = in.readString();
        this.mDescription = in.readString();
        this.mScheduledStartTimeMillis = in.readLong();
        this.mScheduledDurationMillis = in.readLong();
        this.mChannelUri = Uri.parse(in.readString());
        this.mProgramUri = Uri.parse(in.readString());
        this.mContentRatings = new ArrayList();
        List<String> flattenedContentRatings = new ArrayList<>();
        in.readStringList(flattenedContentRatings);
        flattenedContentRatings.forEach(new Consumer() { // from class: android.media.tv.TvRecordingInfo$$ExternalSyntheticLambda0
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                TvRecordingInfo.this.lambda$new$1((String) obj);
            }
        });
        this.mRecordingUri = Uri.parse(in.readString());
        this.mRecordingDurationMillis = in.readLong();
        this.mRecordingStartTimeMillis = in.readLong();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$1(String rating) {
        this.mContentRatings.add(TvContentRating.unflattenFromString(rating));
    }
}
