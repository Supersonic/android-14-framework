package android.media.p007tv;

import android.annotation.NonNull;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.p008os.SharedMemory;
import com.android.internal.util.AnnotationValidations;
/* renamed from: android.media.tv.AdBuffer */
/* loaded from: classes2.dex */
public final class AdBuffer implements Parcelable {
    public static final Parcelable.Creator<AdBuffer> CREATOR = new Parcelable.Creator<AdBuffer>() { // from class: android.media.tv.AdBuffer.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public AdBuffer[] newArray(int size) {
            return new AdBuffer[size];
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public AdBuffer createFromParcel(Parcel in) {
            return new AdBuffer(in);
        }
    };
    private final SharedMemory mBuffer;
    private final int mFlags;
    private final int mId;
    private final int mLength;
    private final String mMimeType;
    private final int mOffset;
    private final long mPresentationTimeUs;

    public AdBuffer(int id, String mimeType, SharedMemory buffer, int offset, int length, long presentationTimeUs, int flags) {
        this.mId = id;
        this.mMimeType = mimeType;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) mimeType);
        this.mBuffer = buffer;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) buffer);
        this.mOffset = offset;
        this.mLength = length;
        this.mPresentationTimeUs = presentationTimeUs;
        this.mFlags = flags;
    }

    public int getId() {
        return this.mId;
    }

    public String getMimeType() {
        return this.mMimeType;
    }

    public SharedMemory getSharedMemory() {
        return this.mBuffer;
    }

    public int getOffset() {
        return this.mOffset;
    }

    public int getLength() {
        return this.mLength;
    }

    public long getPresentationTimeUs() {
        return this.mPresentationTimeUs;
    }

    public int getFlags() {
        return this.mFlags;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mId);
        dest.writeString(this.mMimeType);
        dest.writeTypedObject(this.mBuffer, flags);
        dest.writeInt(this.mOffset);
        dest.writeInt(this.mLength);
        dest.writeLong(this.mPresentationTimeUs);
        dest.writeInt(this.mFlags);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    private AdBuffer(Parcel in) {
        int id = in.readInt();
        String mimeType = in.readString();
        SharedMemory buffer = (SharedMemory) in.readTypedObject(SharedMemory.CREATOR);
        int offset = in.readInt();
        int length = in.readInt();
        long presentationTimeUs = in.readLong();
        int flags = in.readInt();
        this.mId = id;
        this.mMimeType = mimeType;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) mimeType);
        this.mBuffer = buffer;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) buffer);
        this.mOffset = offset;
        this.mLength = length;
        this.mPresentationTimeUs = presentationTimeUs;
        this.mFlags = flags;
    }
}
