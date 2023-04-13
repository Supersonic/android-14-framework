package android.telephony;

import android.annotation.SystemApi;
import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.util.Objects;
@SystemApi
/* loaded from: classes3.dex */
public final class UiccSlotMapping implements Parcelable {
    public static final Parcelable.Creator<UiccSlotMapping> CREATOR = new Parcelable.Creator<UiccSlotMapping>() { // from class: android.telephony.UiccSlotMapping.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public UiccSlotMapping createFromParcel(Parcel in) {
            return new UiccSlotMapping(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public UiccSlotMapping[] newArray(int size) {
            return new UiccSlotMapping[size];
        }
    };
    private final int mLogicalSlotIndex;
    private final int mPhysicalSlotIndex;
    private final int mPortIndex;

    private UiccSlotMapping(Parcel in) {
        this.mPortIndex = in.readInt();
        this.mPhysicalSlotIndex = in.readInt();
        this.mLogicalSlotIndex = in.readInt();
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mPortIndex);
        dest.writeInt(this.mPhysicalSlotIndex);
        dest.writeInt(this.mLogicalSlotIndex);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    public UiccSlotMapping(int portIndex, int physicalSlotIndex, int logicalSlotIndex) {
        this.mPortIndex = portIndex;
        this.mPhysicalSlotIndex = physicalSlotIndex;
        this.mLogicalSlotIndex = logicalSlotIndex;
    }

    public int getPortIndex() {
        return this.mPortIndex;
    }

    public int getPhysicalSlotIndex() {
        return this.mPhysicalSlotIndex;
    }

    public int getLogicalSlotIndex() {
        return this.mLogicalSlotIndex;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        UiccSlotMapping that = (UiccSlotMapping) obj;
        if (this.mPortIndex == that.mPortIndex && this.mPhysicalSlotIndex == that.mPhysicalSlotIndex && this.mLogicalSlotIndex == that.mLogicalSlotIndex) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(Integer.valueOf(this.mPortIndex), Integer.valueOf(this.mPhysicalSlotIndex), Integer.valueOf(this.mLogicalSlotIndex));
    }

    public String toString() {
        return "UiccSlotMapping (mPortIndex=" + this.mPortIndex + ", mPhysicalSlotIndex=" + this.mPhysicalSlotIndex + ", mLogicalSlotIndex=" + this.mLogicalSlotIndex + NavigationBarInflaterView.KEY_CODE_END;
    }
}
