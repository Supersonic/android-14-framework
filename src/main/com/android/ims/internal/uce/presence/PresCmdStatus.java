package com.android.ims.internal.uce.presence;

import android.p008os.Parcel;
import android.p008os.Parcelable;
import com.android.ims.internal.uce.common.StatusCode;
/* loaded from: classes4.dex */
public class PresCmdStatus implements Parcelable {
    public static final Parcelable.Creator<PresCmdStatus> CREATOR = new Parcelable.Creator<PresCmdStatus>() { // from class: com.android.ims.internal.uce.presence.PresCmdStatus.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public PresCmdStatus createFromParcel(Parcel source) {
            return new PresCmdStatus(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public PresCmdStatus[] newArray(int size) {
            return new PresCmdStatus[size];
        }
    };
    private PresCmdId mCmdId;
    private int mRequestId;
    private StatusCode mStatus;
    private int mUserData;

    public PresCmdId getCmdId() {
        return this.mCmdId;
    }

    public void setCmdId(PresCmdId cmdId) {
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

    public int getRequestId() {
        return this.mRequestId;
    }

    public void setRequestId(int requestId) {
        this.mRequestId = requestId;
    }

    public PresCmdStatus() {
        this.mCmdId = new PresCmdId();
        this.mStatus = new StatusCode();
        this.mStatus = new StatusCode();
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mUserData);
        dest.writeInt(this.mRequestId);
        dest.writeParcelable(this.mCmdId, flags);
        dest.writeParcelable(this.mStatus, flags);
    }

    private PresCmdStatus(Parcel source) {
        this.mCmdId = new PresCmdId();
        this.mStatus = new StatusCode();
        readFromParcel(source);
    }

    public void readFromParcel(Parcel source) {
        this.mUserData = source.readInt();
        this.mRequestId = source.readInt();
        this.mCmdId = (PresCmdId) source.readParcelable(PresCmdId.class.getClassLoader(), PresCmdId.class);
        this.mStatus = (StatusCode) source.readParcelable(StatusCode.class.getClassLoader(), StatusCode.class);
    }
}
