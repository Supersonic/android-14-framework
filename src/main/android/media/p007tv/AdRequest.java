package android.media.p007tv;

import android.net.Uri;
import android.p008os.Bundle;
import android.p008os.Parcel;
import android.p008os.ParcelFileDescriptor;
import android.p008os.Parcelable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
/* renamed from: android.media.tv.AdRequest */
/* loaded from: classes2.dex */
public final class AdRequest implements Parcelable {
    public static final Parcelable.Creator<AdRequest> CREATOR = new Parcelable.Creator<AdRequest>() { // from class: android.media.tv.AdRequest.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public AdRequest createFromParcel(Parcel source) {
            return new AdRequest(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public AdRequest[] newArray(int size) {
            return new AdRequest[size];
        }
    };
    public static final int REQUEST_TYPE_START = 1;
    public static final int REQUEST_TYPE_STOP = 2;
    private final long mEchoInterval;
    private final ParcelFileDescriptor mFileDescriptor;
    private final int mId;
    private final String mMediaFileType;
    private final Bundle mMetadata;
    private final int mRequestType;
    private final long mStartTime;
    private final long mStopTime;
    private final Uri mUri;

    @Retention(RetentionPolicy.SOURCE)
    /* renamed from: android.media.tv.AdRequest$RequestType */
    /* loaded from: classes2.dex */
    public @interface RequestType {
    }

    public AdRequest(int id, int requestType, ParcelFileDescriptor fileDescriptor, long startTime, long stopTime, long echoInterval, String mediaFileType, Bundle metadata) {
        this(id, requestType, fileDescriptor, null, startTime, stopTime, echoInterval, mediaFileType, metadata);
    }

    public AdRequest(int id, int requestType, Uri uri, long startTime, long stopTime, long echoInterval, Bundle metadata) {
        this(id, requestType, null, uri, startTime, stopTime, echoInterval, null, metadata);
    }

    private AdRequest(int id, int requestType, ParcelFileDescriptor fileDescriptor, Uri uri, long startTime, long stopTime, long echoInterval, String mediaFileType, Bundle metadata) {
        this.mId = id;
        this.mRequestType = requestType;
        this.mFileDescriptor = fileDescriptor;
        this.mStartTime = startTime;
        this.mStopTime = stopTime;
        this.mEchoInterval = echoInterval;
        this.mMediaFileType = mediaFileType;
        this.mMetadata = metadata;
        this.mUri = uri;
    }

    private AdRequest(Parcel source) {
        this.mId = source.readInt();
        this.mRequestType = source.readInt();
        int readInt = source.readInt();
        if (readInt == 1) {
            this.mFileDescriptor = ParcelFileDescriptor.CREATOR.createFromParcel(source);
            this.mUri = null;
        } else if (readInt == 2) {
            String stringUri = source.readString();
            this.mUri = stringUri == null ? null : Uri.parse(stringUri);
            this.mFileDescriptor = null;
        } else {
            this.mFileDescriptor = null;
            this.mUri = null;
        }
        this.mStartTime = source.readLong();
        this.mStopTime = source.readLong();
        this.mEchoInterval = source.readLong();
        this.mMediaFileType = source.readString();
        this.mMetadata = source.readBundle();
    }

    public int getId() {
        return this.mId;
    }

    public int getRequestType() {
        return this.mRequestType;
    }

    public ParcelFileDescriptor getFileDescriptor() {
        return this.mFileDescriptor;
    }

    public Uri getUri() {
        return this.mUri;
    }

    public long getStartTimeMillis() {
        return this.mStartTime;
    }

    public long getStopTimeMillis() {
        return this.mStopTime;
    }

    public long getEchoIntervalMillis() {
        return this.mEchoInterval;
    }

    public String getMediaFileType() {
        return this.mMediaFileType;
    }

    public Bundle getMetadata() {
        return this.mMetadata;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mId);
        dest.writeInt(this.mRequestType);
        if (this.mFileDescriptor != null) {
            dest.writeInt(1);
            this.mFileDescriptor.writeToParcel(dest, flags);
        } else if (this.mUri != null) {
            dest.writeInt(2);
            String stringUri = this.mUri.toString();
            dest.writeString(stringUri);
        } else {
            dest.writeInt(0);
        }
        dest.writeLong(this.mStartTime);
        dest.writeLong(this.mStopTime);
        dest.writeLong(this.mEchoInterval);
        dest.writeString(this.mMediaFileType);
        dest.writeBundle(this.mMetadata);
    }
}
