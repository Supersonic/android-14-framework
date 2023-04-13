package com.android.ims.internal.uce.presence;

import android.p008os.Parcel;
import android.p008os.Parcelable;
import com.android.ims.internal.uce.common.CapInfo;
/* loaded from: classes4.dex */
public class PresCapInfo implements Parcelable {
    public static final Parcelable.Creator<PresCapInfo> CREATOR = new Parcelable.Creator<PresCapInfo>() { // from class: com.android.ims.internal.uce.presence.PresCapInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public PresCapInfo createFromParcel(Parcel source) {
            return new PresCapInfo(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public PresCapInfo[] newArray(int size) {
            return new PresCapInfo[size];
        }
    };
    private CapInfo mCapInfo;
    private String mContactUri;

    public CapInfo getCapInfo() {
        return this.mCapInfo;
    }

    public void setCapInfo(CapInfo capInfo) {
        this.mCapInfo = capInfo;
    }

    public String getContactUri() {
        return this.mContactUri;
    }

    public void setContactUri(String contactUri) {
        this.mContactUri = contactUri;
    }

    public PresCapInfo() {
        this.mContactUri = "";
        this.mCapInfo = new CapInfo();
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mContactUri);
        dest.writeParcelable(this.mCapInfo, flags);
    }

    private PresCapInfo(Parcel source) {
        this.mContactUri = "";
        readFromParcel(source);
    }

    public void readFromParcel(Parcel source) {
        this.mContactUri = source.readString();
        this.mCapInfo = (CapInfo) source.readParcelable(CapInfo.class.getClassLoader(), CapInfo.class);
    }
}
