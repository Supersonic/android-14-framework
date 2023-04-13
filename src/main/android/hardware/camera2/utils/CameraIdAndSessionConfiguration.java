package android.hardware.camera2.utils;

import android.hardware.camera2.params.SessionConfiguration;
import android.p008os.Parcel;
import android.p008os.Parcelable;
/* loaded from: classes.dex */
public class CameraIdAndSessionConfiguration implements Parcelable {
    public static final Parcelable.Creator<CameraIdAndSessionConfiguration> CREATOR = new Parcelable.Creator<CameraIdAndSessionConfiguration>() { // from class: android.hardware.camera2.utils.CameraIdAndSessionConfiguration.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public CameraIdAndSessionConfiguration createFromParcel(Parcel in) {
            return new CameraIdAndSessionConfiguration(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public CameraIdAndSessionConfiguration[] newArray(int size) {
            return new CameraIdAndSessionConfiguration[size];
        }
    };
    private String mCameraId;
    private SessionConfiguration mSessionConfiguration;

    public CameraIdAndSessionConfiguration(String cameraId, SessionConfiguration sessionConfiguration) {
        this.mCameraId = cameraId;
        this.mSessionConfiguration = sessionConfiguration;
    }

    private CameraIdAndSessionConfiguration(Parcel in) {
        readFromParcel(in);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mCameraId);
        this.mSessionConfiguration.writeToParcel(dest, flags);
    }

    public void readFromParcel(Parcel in) {
        this.mCameraId = in.readString();
        this.mSessionConfiguration = SessionConfiguration.CREATOR.createFromParcel(in);
    }

    public String getCameraId() {
        return this.mCameraId;
    }

    public SessionConfiguration getSessionConfiguration() {
        return this.mSessionConfiguration;
    }
}
