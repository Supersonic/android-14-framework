package com.android.ims.internal.uce.presence;

import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.util.Arrays;
/* loaded from: classes4.dex */
public class PresResInstanceInfo implements Parcelable {
    public static final Parcelable.Creator<PresResInstanceInfo> CREATOR = new Parcelable.Creator<PresResInstanceInfo>() { // from class: com.android.ims.internal.uce.presence.PresResInstanceInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public PresResInstanceInfo createFromParcel(Parcel source) {
            return new PresResInstanceInfo(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public PresResInstanceInfo[] newArray(int size) {
            return new PresResInstanceInfo[size];
        }
    };
    public static final int UCE_PRES_RES_INSTANCE_STATE_ACTIVE = 0;
    public static final int UCE_PRES_RES_INSTANCE_STATE_PENDING = 1;
    public static final int UCE_PRES_RES_INSTANCE_STATE_TERMINATED = 2;
    public static final int UCE_PRES_RES_INSTANCE_STATE_UNKNOWN = 3;
    public static final int UCE_PRES_RES_INSTANCE_UNKNOWN = 4;
    private String mId;
    private String mPresentityUri;
    private String mReason;
    private int mResInstanceState;
    private PresTupleInfo[] mTupleInfoArray;

    public int getResInstanceState() {
        return this.mResInstanceState;
    }

    public void setResInstanceState(int nResInstanceState) {
        this.mResInstanceState = nResInstanceState;
    }

    public String getResId() {
        return this.mId;
    }

    public void setResId(String resourceId) {
        this.mId = resourceId;
    }

    public String getReason() {
        return this.mReason;
    }

    public void setReason(String reason) {
        this.mReason = reason;
    }

    public String getPresentityUri() {
        return this.mPresentityUri;
    }

    public void setPresentityUri(String presentityUri) {
        this.mPresentityUri = presentityUri;
    }

    public PresTupleInfo[] getTupleInfo() {
        return this.mTupleInfoArray;
    }

    public void setTupleInfo(PresTupleInfo[] tupleInfo) {
        this.mTupleInfoArray = new PresTupleInfo[tupleInfo.length];
        this.mTupleInfoArray = tupleInfo;
    }

    public PresResInstanceInfo() {
        this.mId = "";
        this.mReason = "";
        this.mPresentityUri = "";
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mId);
        dest.writeString(this.mReason);
        dest.writeInt(this.mResInstanceState);
        dest.writeString(this.mPresentityUri);
        dest.writeParcelableArray(this.mTupleInfoArray, flags);
    }

    private PresResInstanceInfo(Parcel source) {
        this.mId = "";
        this.mReason = "";
        this.mPresentityUri = "";
        readFromParcel(source);
    }

    public void readFromParcel(Parcel source) {
        this.mId = source.readString();
        this.mReason = source.readString();
        this.mResInstanceState = source.readInt();
        this.mPresentityUri = source.readString();
        Parcelable[] tempParcelableArray = (Parcelable[]) source.readParcelableArray(PresTupleInfo.class.getClassLoader(), PresTupleInfo.class);
        this.mTupleInfoArray = new PresTupleInfo[0];
        if (tempParcelableArray != null) {
            this.mTupleInfoArray = (PresTupleInfo[]) Arrays.copyOf(tempParcelableArray, tempParcelableArray.length, PresTupleInfo[].class);
        }
    }
}
