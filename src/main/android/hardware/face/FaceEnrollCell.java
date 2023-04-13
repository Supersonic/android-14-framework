package android.hardware.face;

import android.p008os.Parcel;
import android.p008os.Parcelable;
/* loaded from: classes.dex */
public final class FaceEnrollCell implements Parcelable {
    public static final Parcelable.Creator<FaceEnrollCell> CREATOR = new Parcelable.Creator<FaceEnrollCell>() { // from class: android.hardware.face.FaceEnrollCell.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public FaceEnrollCell createFromParcel(Parcel source) {
            return new FaceEnrollCell(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public FaceEnrollCell[] newArray(int size) {
            return new FaceEnrollCell[size];
        }
    };

    /* renamed from: mX */
    private final int f129mX;

    /* renamed from: mY */
    private final int f130mY;

    /* renamed from: mZ */
    private final int f131mZ;

    public FaceEnrollCell(int x, int y, int z) {
        this.f129mX = x;
        this.f130mY = y;
        this.f131mZ = z;
    }

    public int getX() {
        return this.f129mX;
    }

    public int getY() {
        return this.f130mY;
    }

    public int getZ() {
        return this.f131mZ;
    }

    private FaceEnrollCell(Parcel source) {
        this.f129mX = source.readInt();
        this.f130mY = source.readInt();
        this.f131mZ = source.readInt();
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.f129mX);
        dest.writeInt(this.f130mY);
        dest.writeInt(this.f131mZ);
    }
}
