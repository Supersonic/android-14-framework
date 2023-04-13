package android.telephony;

import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.util.Objects;
/* loaded from: classes3.dex */
public final class UiccPortInfo implements Parcelable {
    public static final Parcelable.Creator<UiccPortInfo> CREATOR = new Parcelable.Creator<UiccPortInfo>() { // from class: android.telephony.UiccPortInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public UiccPortInfo createFromParcel(Parcel in) {
            return new UiccPortInfo(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public UiccPortInfo[] newArray(int size) {
            return new UiccPortInfo[size];
        }
    };
    public static final String ICCID_REDACTED = "FFFFFFFFFFFFFFFFFFFF";
    private final String mIccId;
    private final boolean mIsActive;
    private final int mLogicalSlotIndex;
    private final int mPortIndex;

    private UiccPortInfo(Parcel in) {
        this.mIccId = in.readString8();
        this.mPortIndex = in.readInt();
        this.mLogicalSlotIndex = in.readInt();
        this.mIsActive = in.readBoolean();
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString8(this.mIccId);
        dest.writeInt(this.mPortIndex);
        dest.writeInt(this.mLogicalSlotIndex);
        dest.writeBoolean(this.mIsActive);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    public UiccPortInfo(String iccId, int portIndex, int logicalSlotIndex, boolean isActive) {
        this.mIccId = iccId;
        this.mPortIndex = portIndex;
        this.mLogicalSlotIndex = logicalSlotIndex;
        this.mIsActive = isActive;
    }

    public String getIccId() {
        return this.mIccId;
    }

    public int getPortIndex() {
        return this.mPortIndex;
    }

    public boolean isActive() {
        return this.mIsActive;
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
        UiccPortInfo that = (UiccPortInfo) obj;
        if (Objects.equals(this.mIccId, that.mIccId) && this.mPortIndex == that.mPortIndex && this.mLogicalSlotIndex == that.mLogicalSlotIndex && this.mIsActive == that.mIsActive) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(this.mIccId, Integer.valueOf(this.mPortIndex), Integer.valueOf(this.mLogicalSlotIndex), Boolean.valueOf(this.mIsActive));
    }

    public String toString() {
        return "UiccPortInfo (isActive=" + this.mIsActive + ", iccId=" + SubscriptionInfo.givePrintableIccid(this.mIccId) + ", portIndex=" + this.mPortIndex + ", mLogicalSlotIndex=" + this.mLogicalSlotIndex + NavigationBarInflaterView.KEY_CODE_END;
    }
}
