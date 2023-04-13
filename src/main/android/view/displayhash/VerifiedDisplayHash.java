package android.view.displayhash;

import android.annotation.CurrentTimeMillisLong;
import android.annotation.NonNull;
import android.graphics.Rect;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import com.android.internal.util.AnnotationValidations;
import java.lang.annotation.Annotation;
/* loaded from: classes4.dex */
public final class VerifiedDisplayHash implements Parcelable {
    public static final Parcelable.Creator<VerifiedDisplayHash> CREATOR = new Parcelable.Creator<VerifiedDisplayHash>() { // from class: android.view.displayhash.VerifiedDisplayHash.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public VerifiedDisplayHash[] newArray(int size) {
            return new VerifiedDisplayHash[size];
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public VerifiedDisplayHash createFromParcel(Parcel in) {
            return new VerifiedDisplayHash(in);
        }
    };
    private final Rect mBoundsInWindow;
    private final String mHashAlgorithm;
    private final byte[] mImageHash;
    private final long mTimeMillis;

    private String imageHashToString() {
        return byteArrayToString(this.mImageHash);
    }

    private String byteArrayToString(byte[] byteArray) {
        if (byteArray == null) {
            return "null";
        }
        int iMax = byteArray.length - 1;
        if (iMax == -1) {
            return "[]";
        }
        StringBuilder b = new StringBuilder();
        b.append('[');
        int i = 0;
        while (true) {
            String formatted = String.format("%02X", Integer.valueOf(byteArray[i] & 255));
            b.append(formatted);
            if (i == iMax) {
                return b.append(']').toString();
            }
            b.append(", ");
            i++;
        }
    }

    public VerifiedDisplayHash(long timeMillis, Rect boundsInWindow, String hashAlgorithm, byte[] imageHash) {
        this.mTimeMillis = timeMillis;
        AnnotationValidations.validate(CurrentTimeMillisLong.class, (Annotation) null, timeMillis);
        this.mBoundsInWindow = boundsInWindow;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) boundsInWindow);
        this.mHashAlgorithm = hashAlgorithm;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) hashAlgorithm);
        this.mImageHash = imageHash;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) imageHash);
    }

    public long getTimeMillis() {
        return this.mTimeMillis;
    }

    public Rect getBoundsInWindow() {
        return this.mBoundsInWindow;
    }

    public String getHashAlgorithm() {
        return this.mHashAlgorithm;
    }

    public byte[] getImageHash() {
        return this.mImageHash;
    }

    public String toString() {
        return "VerifiedDisplayHash { timeMillis = " + this.mTimeMillis + ", boundsInWindow = " + this.mBoundsInWindow + ", hashAlgorithm = " + this.mHashAlgorithm + ", imageHash = " + imageHashToString() + " }";
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.mTimeMillis);
        dest.writeTypedObject(this.mBoundsInWindow, flags);
        dest.writeString(this.mHashAlgorithm);
        dest.writeByteArray(this.mImageHash);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    VerifiedDisplayHash(Parcel in) {
        long timeMillis = in.readLong();
        Rect boundsInWindow = (Rect) in.readTypedObject(Rect.CREATOR);
        String hashAlgorithm = in.readString();
        byte[] imageHash = in.createByteArray();
        this.mTimeMillis = timeMillis;
        AnnotationValidations.validate(CurrentTimeMillisLong.class, (Annotation) null, timeMillis);
        this.mBoundsInWindow = boundsInWindow;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) boundsInWindow);
        this.mHashAlgorithm = hashAlgorithm;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) hashAlgorithm);
        this.mImageHash = imageHash;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) imageHash);
    }

    @Deprecated
    private void __metadata() {
    }
}
