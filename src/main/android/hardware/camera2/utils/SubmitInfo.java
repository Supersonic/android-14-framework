package android.hardware.camera2.utils;

import android.p008os.Parcel;
import android.p008os.Parcelable;
/* loaded from: classes.dex */
public class SubmitInfo implements Parcelable {
    public static final Parcelable.Creator<SubmitInfo> CREATOR = new Parcelable.Creator<SubmitInfo>() { // from class: android.hardware.camera2.utils.SubmitInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public SubmitInfo createFromParcel(Parcel in) {
            return new SubmitInfo(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public SubmitInfo[] newArray(int size) {
            return new SubmitInfo[size];
        }
    };
    private long mLastFrameNumber;
    private int mRequestId;

    public SubmitInfo() {
        this.mRequestId = -1;
        this.mLastFrameNumber = -1L;
    }

    public SubmitInfo(int requestId, long lastFrameNumber) {
        this.mRequestId = requestId;
        this.mLastFrameNumber = lastFrameNumber;
    }

    private SubmitInfo(Parcel in) {
        readFromParcel(in);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mRequestId);
        dest.writeLong(this.mLastFrameNumber);
    }

    public void readFromParcel(Parcel in) {
        this.mRequestId = in.readInt();
        this.mLastFrameNumber = in.readLong();
    }

    public int getRequestId() {
        return this.mRequestId;
    }

    public long getLastFrameNumber() {
        return this.mLastFrameNumber;
    }
}
