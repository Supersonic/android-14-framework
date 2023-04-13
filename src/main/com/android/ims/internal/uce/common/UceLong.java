package com.android.ims.internal.uce.common;

import android.p008os.Parcel;
import android.p008os.Parcelable;
/* loaded from: classes4.dex */
public class UceLong implements Parcelable {
    public static final Parcelable.Creator<UceLong> CREATOR = new Parcelable.Creator<UceLong>() { // from class: com.android.ims.internal.uce.common.UceLong.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public UceLong createFromParcel(Parcel source) {
            return new UceLong(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public UceLong[] newArray(int size) {
            return new UceLong[size];
        }
    };
    private int mClientId;
    private long mUceLong;

    public UceLong() {
        this.mClientId = 1001;
    }

    public long getUceLong() {
        return this.mUceLong;
    }

    public void setUceLong(long uceLong) {
        this.mUceLong = uceLong;
    }

    public int getClientId() {
        return this.mClientId;
    }

    public void setClientId(int nClientId) {
        this.mClientId = nClientId;
    }

    public static UceLong getUceLongInstance() {
        return new UceLong();
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        writeToParcel(dest);
    }

    private void writeToParcel(Parcel out) {
        out.writeLong(this.mUceLong);
        out.writeInt(this.mClientId);
    }

    private UceLong(Parcel source) {
        this.mClientId = 1001;
        readFromParcel(source);
    }

    public void readFromParcel(Parcel source) {
        this.mUceLong = source.readLong();
        this.mClientId = source.readInt();
    }
}
