package android.hardware;

import android.hardware.Camera;
import android.p008os.Parcel;
import android.p008os.Parcelable;
/* loaded from: classes.dex */
public class CameraInfo implements Parcelable {
    public static final Parcelable.Creator<CameraInfo> CREATOR = new Parcelable.Creator<CameraInfo>() { // from class: android.hardware.CameraInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public CameraInfo createFromParcel(Parcel in) {
            CameraInfo info = new CameraInfo();
            info.readFromParcel(in);
            return info;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public CameraInfo[] newArray(int size) {
            return new CameraInfo[size];
        }
    };
    public Camera.CameraInfo info = new Camera.CameraInfo();

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(this.info.facing);
        out.writeInt(this.info.orientation);
    }

    public void readFromParcel(Parcel in) {
        this.info.facing = in.readInt();
        this.info.orientation = in.readInt();
    }
}
