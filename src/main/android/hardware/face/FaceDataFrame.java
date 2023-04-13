package android.hardware.face;

import android.p008os.Parcel;
import android.p008os.Parcelable;
/* loaded from: classes.dex */
public final class FaceDataFrame implements Parcelable {
    public static final Parcelable.Creator<FaceDataFrame> CREATOR = new Parcelable.Creator<FaceDataFrame>() { // from class: android.hardware.face.FaceDataFrame.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public FaceDataFrame createFromParcel(Parcel source) {
            return new FaceDataFrame(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public FaceDataFrame[] newArray(int size) {
            return new FaceDataFrame[size];
        }
    };
    private final int mAcquiredInfo;
    private final float mDistance;
    private final boolean mIsCancellable;
    private final float mPan;
    private final float mTilt;
    private final int mVendorCode;

    public FaceDataFrame(int acquiredInfo, int vendorCode, float pan, float tilt, float distance, boolean isCancellable) {
        this.mAcquiredInfo = acquiredInfo;
        this.mVendorCode = vendorCode;
        this.mPan = pan;
        this.mTilt = tilt;
        this.mDistance = distance;
        this.mIsCancellable = isCancellable;
    }

    public FaceDataFrame(int acquiredInfo, int vendorCode) {
        this.mAcquiredInfo = acquiredInfo;
        this.mVendorCode = vendorCode;
        this.mPan = 0.0f;
        this.mTilt = 0.0f;
        this.mDistance = 0.0f;
        this.mIsCancellable = false;
    }

    public int getAcquiredInfo() {
        return this.mAcquiredInfo;
    }

    public int getVendorCode() {
        return this.mVendorCode;
    }

    public float getPan() {
        return this.mPan;
    }

    public float getTilt() {
        return this.mTilt;
    }

    public float getDistance() {
        return this.mDistance;
    }

    public boolean isCancellable() {
        return this.mIsCancellable;
    }

    private FaceDataFrame(Parcel source) {
        this.mAcquiredInfo = source.readInt();
        this.mVendorCode = source.readInt();
        this.mPan = source.readFloat();
        this.mTilt = source.readFloat();
        this.mDistance = source.readFloat();
        this.mIsCancellable = source.readBoolean();
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mAcquiredInfo);
        dest.writeInt(this.mVendorCode);
        dest.writeFloat(this.mPan);
        dest.writeFloat(this.mTilt);
        dest.writeFloat(this.mDistance);
        dest.writeBoolean(this.mIsCancellable);
    }
}
