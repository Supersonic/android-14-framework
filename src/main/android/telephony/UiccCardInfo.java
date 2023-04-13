package android.telephony;

import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import com.android.internal.telephony.util.TelephonyUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
/* loaded from: classes3.dex */
public final class UiccCardInfo implements Parcelable {
    public static final Parcelable.Creator<UiccCardInfo> CREATOR = new Parcelable.Creator<UiccCardInfo>() { // from class: android.telephony.UiccCardInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public UiccCardInfo createFromParcel(Parcel in) {
            return new UiccCardInfo(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public UiccCardInfo[] newArray(int size) {
            return new UiccCardInfo[size];
        }
    };
    private final int mCardId;
    private final String mEid;
    private final String mIccId;
    private boolean mIccIdAccessRestricted;
    private final boolean mIsEuicc;
    private final boolean mIsMultipleEnabledProfilesSupported;
    private final boolean mIsRemovable;
    private final int mPhysicalSlotIndex;
    private final List<UiccPortInfo> mPortList;

    private UiccCardInfo(Parcel in) {
        this.mIccIdAccessRestricted = false;
        this.mIsEuicc = in.readBoolean();
        this.mCardId = in.readInt();
        this.mEid = in.readString8();
        this.mIccId = in.readString8();
        this.mPhysicalSlotIndex = in.readInt();
        this.mIsRemovable = in.readBoolean();
        this.mIsMultipleEnabledProfilesSupported = in.readBoolean();
        ArrayList arrayList = new ArrayList();
        this.mPortList = arrayList;
        in.readTypedList(arrayList, UiccPortInfo.CREATOR);
        this.mIccIdAccessRestricted = in.readBoolean();
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeBoolean(this.mIsEuicc);
        dest.writeInt(this.mCardId);
        dest.writeString8(this.mEid);
        dest.writeString8(this.mIccId);
        dest.writeInt(this.mPhysicalSlotIndex);
        dest.writeBoolean(this.mIsRemovable);
        dest.writeBoolean(this.mIsMultipleEnabledProfilesSupported);
        dest.writeTypedList(this.mPortList, flags);
        dest.writeBoolean(this.mIccIdAccessRestricted);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    public UiccCardInfo(boolean isEuicc, int cardId, String eid, int physicalSlotIndex, boolean isRemovable, boolean isMultipleEnabledProfilesSupported, List<UiccPortInfo> portList) {
        this.mIccIdAccessRestricted = false;
        this.mIsEuicc = isEuicc;
        this.mCardId = cardId;
        this.mEid = eid;
        this.mIccId = null;
        this.mPhysicalSlotIndex = physicalSlotIndex;
        this.mIsRemovable = isRemovable;
        this.mIsMultipleEnabledProfilesSupported = isMultipleEnabledProfilesSupported;
        this.mPortList = portList;
    }

    public boolean isEuicc() {
        return this.mIsEuicc;
    }

    public int getCardId() {
        return this.mCardId;
    }

    public String getEid() {
        if (!this.mIsEuicc) {
            return null;
        }
        return this.mEid;
    }

    @Deprecated
    public String getIccId() {
        if (this.mIccIdAccessRestricted) {
            throw new UnsupportedOperationException("getIccId() is not supported by UiccCardInfo. Please Use UiccPortInfo API instead");
        }
        return getPorts().stream().findFirst().get().getIccId();
    }

    @Deprecated
    public int getSlotIndex() {
        return this.mPhysicalSlotIndex;
    }

    public int getPhysicalSlotIndex() {
        return this.mPhysicalSlotIndex;
    }

    public boolean isRemovable() {
        return this.mIsRemovable;
    }

    public boolean isMultipleEnabledProfilesSupported() {
        return this.mIsMultipleEnabledProfilesSupported;
    }

    public Collection<UiccPortInfo> getPorts() {
        return Collections.unmodifiableList(this.mPortList);
    }

    public void setIccIdAccessRestricted(boolean iccIdAccessRestricted) {
        this.mIccIdAccessRestricted = iccIdAccessRestricted;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        UiccCardInfo that = (UiccCardInfo) obj;
        if (this.mIsEuicc == that.mIsEuicc && this.mCardId == that.mCardId && Objects.equals(this.mEid, that.mEid) && Objects.equals(this.mIccId, that.mIccId) && this.mPhysicalSlotIndex == that.mPhysicalSlotIndex && this.mIsRemovable == that.mIsRemovable && this.mIsMultipleEnabledProfilesSupported == that.mIsMultipleEnabledProfilesSupported && Objects.equals(this.mPortList, that.mPortList)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(Boolean.valueOf(this.mIsEuicc), Integer.valueOf(this.mCardId), this.mEid, this.mIccId, Integer.valueOf(this.mPhysicalSlotIndex), Boolean.valueOf(this.mIsRemovable), Boolean.valueOf(this.mIsMultipleEnabledProfilesSupported), this.mPortList);
    }

    public String toString() {
        return "UiccCardInfo (mIsEuicc=" + this.mIsEuicc + ", mCardId=" + this.mCardId + ", mEid=" + com.android.telephony.Rlog.pii(TelephonyUtils.IS_DEBUGGABLE, this.mEid) + ", mPhysicalSlotIndex=" + this.mPhysicalSlotIndex + ", mIsRemovable=" + this.mIsRemovable + ", mIsMultipleEnabledProfilesSupported=" + this.mIsMultipleEnabledProfilesSupported + ", mPortList=" + this.mPortList + ", mIccIdAccessRestricted=" + this.mIccIdAccessRestricted + NavigationBarInflaterView.KEY_CODE_END;
    }
}
