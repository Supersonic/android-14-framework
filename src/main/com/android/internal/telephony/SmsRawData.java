package com.android.internal.telephony;

import android.p008os.Parcel;
import android.p008os.Parcelable;
/* loaded from: classes3.dex */
public class SmsRawData implements Parcelable {
    public static final Parcelable.Creator<SmsRawData> CREATOR = new Parcelable.Creator<SmsRawData>() { // from class: com.android.internal.telephony.SmsRawData.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public SmsRawData createFromParcel(Parcel source) {
            int size = source.readInt();
            byte[] data = new byte[size];
            source.readByteArray(data);
            return new SmsRawData(data);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public SmsRawData[] newArray(int size) {
            return new SmsRawData[size];
        }
    };
    byte[] data;

    public SmsRawData(byte[] data) {
        this.data = data;
    }

    public byte[] getBytes() {
        return this.data;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.data.length);
        dest.writeByteArray(this.data);
    }
}
