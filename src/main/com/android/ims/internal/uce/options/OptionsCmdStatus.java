package com.android.ims.internal.uce.options;

import android.p008os.Parcel;
import android.p008os.Parcelable;
import com.android.ims.internal.uce.common.CapInfo;
import com.android.ims.internal.uce.common.StatusCode;
/* loaded from: classes4.dex */
public class OptionsCmdStatus implements Parcelable {
    public static final Parcelable.Creator<OptionsCmdStatus> CREATOR = new Parcelable.Creator<OptionsCmdStatus>() { // from class: com.android.ims.internal.uce.options.OptionsCmdStatus.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public OptionsCmdStatus createFromParcel(Parcel source) {
            return new OptionsCmdStatus(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public OptionsCmdStatus[] newArray(int size) {
            return new OptionsCmdStatus[size];
        }
    };
    private CapInfo mCapInfo;
    private OptionsCmdId mCmdId;
    private StatusCode mStatus;
    private int mUserData;

    public OptionsCmdId getCmdId() {
        return this.mCmdId;
    }

    public void setCmdId(OptionsCmdId cmdId) {
        this.mCmdId = cmdId;
    }

    public int getUserData() {
        return this.mUserData;
    }

    public void setUserData(int userData) {
        this.mUserData = userData;
    }

    public StatusCode getStatus() {
        return this.mStatus;
    }

    public void setStatus(StatusCode status) {
        this.mStatus = status;
    }

    public OptionsCmdStatus() {
        this.mStatus = new StatusCode();
        this.mCapInfo = new CapInfo();
        this.mCmdId = new OptionsCmdId();
        this.mUserData = 0;
    }

    public CapInfo getCapInfo() {
        return this.mCapInfo;
    }

    public void setCapInfo(CapInfo capInfo) {
        this.mCapInfo = capInfo;
    }

    public static OptionsCmdStatus getOptionsCmdStatusInstance() {
        return new OptionsCmdStatus();
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mUserData);
        dest.writeParcelable(this.mCmdId, flags);
        dest.writeParcelable(this.mStatus, flags);
        dest.writeParcelable(this.mCapInfo, flags);
    }

    private OptionsCmdStatus(Parcel source) {
        readFromParcel(source);
    }

    public void readFromParcel(Parcel source) {
        this.mUserData = source.readInt();
        this.mCmdId = (OptionsCmdId) source.readParcelable(OptionsCmdId.class.getClassLoader(), OptionsCmdId.class);
        this.mStatus = (StatusCode) source.readParcelable(StatusCode.class.getClassLoader(), StatusCode.class);
        this.mCapInfo = (CapInfo) source.readParcelable(CapInfo.class.getClassLoader(), CapInfo.class);
    }
}
