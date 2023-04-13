package android.hardware.usb;

import android.annotation.SystemApi;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Objects;
@SystemApi
/* loaded from: classes2.dex */
public final class DisplayPortAltModeInfo implements Parcelable {
    public static final Parcelable.Creator<DisplayPortAltModeInfo> CREATOR = new Parcelable.Creator<DisplayPortAltModeInfo>() { // from class: android.hardware.usb.DisplayPortAltModeInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public DisplayPortAltModeInfo createFromParcel(Parcel in) {
            int partnerSinkStatus = in.readInt();
            int cableStatus = in.readInt();
            int numLanes = in.readInt();
            boolean hotPlugDetect = in.readBoolean();
            int linkTrainingStatus = in.readInt();
            return new DisplayPortAltModeInfo(partnerSinkStatus, cableStatus, numLanes, hotPlugDetect, linkTrainingStatus);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public DisplayPortAltModeInfo[] newArray(int size) {
            return new DisplayPortAltModeInfo[size];
        }
    };
    public static final int DISPLAYPORT_ALT_MODE_STATUS_CAPABLE_DISABLED = 2;
    public static final int DISPLAYPORT_ALT_MODE_STATUS_ENABLED = 3;
    public static final int DISPLAYPORT_ALT_MODE_STATUS_NOT_CAPABLE = 1;
    public static final int DISPLAYPORT_ALT_MODE_STATUS_UNKNOWN = 0;
    public static final int LINK_TRAINING_STATUS_FAILURE = 2;
    public static final int LINK_TRAINING_STATUS_SUCCESS = 1;
    public static final int LINK_TRAINING_STATUS_UNKNOWN = 0;
    private final int mCableStatus;
    private final boolean mHotPlugDetect;
    private final int mLinkTrainingStatus;
    private final int mNumLanes;
    private final int mPartnerSinkStatus;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes2.dex */
    public @interface DisplayPortAltModeStatus {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes2.dex */
    public @interface LinkTrainingStatus {
    }

    public DisplayPortAltModeInfo() {
        this.mPartnerSinkStatus = 0;
        this.mCableStatus = 0;
        this.mNumLanes = 0;
        this.mHotPlugDetect = false;
        this.mLinkTrainingStatus = 0;
    }

    public DisplayPortAltModeInfo(int partnerSinkStatus, int cableStatus, int numLanes, boolean hotPlugDetect, int linkTrainingStatus) {
        this.mPartnerSinkStatus = partnerSinkStatus;
        this.mCableStatus = cableStatus;
        this.mNumLanes = numLanes;
        this.mHotPlugDetect = hotPlugDetect;
        this.mLinkTrainingStatus = linkTrainingStatus;
    }

    public int getPartnerSinkStatus() {
        return this.mPartnerSinkStatus;
    }

    public int getCableStatus() {
        return this.mCableStatus;
    }

    public int getNumberOfLanes() {
        return this.mNumLanes;
    }

    public boolean isHotPlugDetectActive() {
        return this.mHotPlugDetect;
    }

    public int getLinkTrainingStatus() {
        return this.mLinkTrainingStatus;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mPartnerSinkStatus);
        dest.writeInt(this.mCableStatus);
        dest.writeInt(this.mNumLanes);
        dest.writeBoolean(this.mHotPlugDetect);
        dest.writeInt(this.mLinkTrainingStatus);
    }

    public String toString() {
        return "DisplayPortAltModeInfo{partnerSink=" + this.mPartnerSinkStatus + " cable=" + this.mCableStatus + " numLanes=" + this.mNumLanes + " hotPlugDetect=" + this.mHotPlugDetect + " linkTrainingStatus=" + this.mLinkTrainingStatus + "}";
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof DisplayPortAltModeInfo) {
            DisplayPortAltModeInfo other = (DisplayPortAltModeInfo) o;
            return this.mPartnerSinkStatus == other.mPartnerSinkStatus && this.mCableStatus == other.mCableStatus && this.mNumLanes == other.mNumLanes && this.mHotPlugDetect == other.mHotPlugDetect && this.mLinkTrainingStatus == other.mLinkTrainingStatus;
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(Integer.valueOf(this.mPartnerSinkStatus), Integer.valueOf(this.mCableStatus), Integer.valueOf(this.mNumLanes), Boolean.valueOf(this.mHotPlugDetect), Integer.valueOf(this.mLinkTrainingStatus));
    }
}
