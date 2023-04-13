package android.view.displayhash;

import android.annotation.NonNull;
import android.annotation.SystemApi;
import android.graphics.Rect;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import com.android.internal.util.AnnotationValidations;
/* loaded from: classes4.dex */
public final class DisplayHash implements Parcelable {
    public static final Parcelable.Creator<DisplayHash> CREATOR = new Parcelable.Creator<DisplayHash>() { // from class: android.view.displayhash.DisplayHash.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public DisplayHash[] newArray(int size) {
            return new DisplayHash[size];
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public DisplayHash createFromParcel(Parcel in) {
            return new DisplayHash(in);
        }
    };
    private final Rect mBoundsInWindow;
    private final String mHashAlgorithm;
    private final byte[] mHmac;
    private final byte[] mImageHash;
    private final long mTimeMillis;

    @SystemApi
    public DisplayHash(long timeMillis, Rect boundsInWindow, String hashAlgorithm, byte[] imageHash, byte[] hmac) {
        this.mTimeMillis = timeMillis;
        this.mBoundsInWindow = boundsInWindow;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) boundsInWindow);
        this.mHashAlgorithm = hashAlgorithm;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) hashAlgorithm);
        this.mImageHash = imageHash;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) imageHash);
        this.mHmac = hmac;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) hmac);
    }

    @SystemApi
    public long getTimeMillis() {
        return this.mTimeMillis;
    }

    @SystemApi
    public Rect getBoundsInWindow() {
        return this.mBoundsInWindow;
    }

    @SystemApi
    public String getHashAlgorithm() {
        return this.mHashAlgorithm;
    }

    @SystemApi
    public byte[] getImageHash() {
        return this.mImageHash;
    }

    @SystemApi
    public byte[] getHmac() {
        return this.mHmac;
    }

    public String toString() {
        return "DisplayHash { timeMillis = " + this.mTimeMillis + ", boundsInWindow = " + this.mBoundsInWindow + ", hashAlgorithm = " + this.mHashAlgorithm + ", imageHash = " + byteArrayToString(this.mImageHash) + ", hmac = " + byteArrayToString(this.mHmac) + " }";
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

    @Override // android.p008os.Parcelable
    @SystemApi
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.mTimeMillis);
        dest.writeTypedObject(this.mBoundsInWindow, flags);
        dest.writeString(this.mHashAlgorithm);
        dest.writeByteArray(this.mImageHash);
        dest.writeByteArray(this.mHmac);
    }

    @Override // android.p008os.Parcelable
    @SystemApi
    public int describeContents() {
        return 0;
    }

    private DisplayHash(Parcel in) {
        this.mTimeMillis = in.readLong();
        Rect boundsInWindow = (Rect) in.readTypedObject(Rect.CREATOR);
        String hashAlgorithm = in.readString();
        byte[] imageHash = in.createByteArray();
        byte[] hmac = in.createByteArray();
        this.mBoundsInWindow = boundsInWindow;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) boundsInWindow);
        this.mHashAlgorithm = hashAlgorithm;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) hashAlgorithm);
        this.mImageHash = imageHash;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) imageHash);
        this.mHmac = hmac;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) hmac);
    }
}
