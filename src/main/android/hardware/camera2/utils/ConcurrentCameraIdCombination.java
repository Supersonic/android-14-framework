package android.hardware.camera2.utils;

import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.util.HashSet;
import java.util.Set;
/* loaded from: classes.dex */
public class ConcurrentCameraIdCombination implements Parcelable {
    public static final Parcelable.Creator<ConcurrentCameraIdCombination> CREATOR = new Parcelable.Creator<ConcurrentCameraIdCombination>() { // from class: android.hardware.camera2.utils.ConcurrentCameraIdCombination.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ConcurrentCameraIdCombination createFromParcel(Parcel in) {
            return new ConcurrentCameraIdCombination(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ConcurrentCameraIdCombination[] newArray(int size) {
            return new ConcurrentCameraIdCombination[size];
        }
    };
    private Set<String> mConcurrentCameraIds;

    private ConcurrentCameraIdCombination(Parcel in) {
        this.mConcurrentCameraIds = new HashSet();
        readFromParcel(in);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mConcurrentCameraIds.size());
        for (String cameraId : this.mConcurrentCameraIds) {
            dest.writeString(cameraId);
        }
    }

    public void readFromParcel(Parcel in) {
        this.mConcurrentCameraIds.clear();
        int cameraCombinationSize = in.readInt();
        if (cameraCombinationSize < 0) {
            throw new RuntimeException("cameraCombinationSize " + cameraCombinationSize + " should not be negative");
        }
        for (int i = 0; i < cameraCombinationSize; i++) {
            String cameraId = in.readString();
            if (cameraId == null) {
                throw new RuntimeException("Failed to read camera id from Parcel");
            }
            this.mConcurrentCameraIds.add(cameraId);
        }
    }

    public Set<String> getConcurrentCameraIdCombination() {
        return this.mConcurrentCameraIds;
    }
}
