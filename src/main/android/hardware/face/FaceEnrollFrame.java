package android.hardware.face;

import android.p008os.Parcel;
import android.p008os.Parcelable;
/* loaded from: classes.dex */
public final class FaceEnrollFrame implements Parcelable {
    public static final Parcelable.Creator<FaceEnrollFrame> CREATOR = new Parcelable.Creator<FaceEnrollFrame>() { // from class: android.hardware.face.FaceEnrollFrame.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public FaceEnrollFrame createFromParcel(Parcel source) {
            return new FaceEnrollFrame(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public FaceEnrollFrame[] newArray(int size) {
            return new FaceEnrollFrame[size];
        }
    };
    private final FaceEnrollCell mCell;
    private final FaceDataFrame mData;
    private final int mStage;

    public FaceEnrollFrame(FaceEnrollCell cell, int stage, FaceDataFrame data) {
        this.mCell = cell;
        this.mStage = stage;
        this.mData = data;
    }

    public FaceEnrollCell getCell() {
        return this.mCell;
    }

    public int getStage() {
        return this.mStage;
    }

    public FaceDataFrame getData() {
        return this.mData;
    }

    private FaceEnrollFrame(Parcel source) {
        this.mCell = (FaceEnrollCell) source.readParcelable(FaceEnrollCell.class.getClassLoader(), FaceEnrollCell.class);
        this.mStage = source.readInt();
        this.mData = (FaceDataFrame) source.readParcelable(FaceDataFrame.class.getClassLoader(), FaceDataFrame.class);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.mCell, flags);
        dest.writeInt(this.mStage);
        dest.writeParcelable(this.mData, flags);
    }
}
