package com.android.ims;

import android.p008os.Parcel;
import android.p008os.Parcelable;
/* loaded from: classes4.dex */
public class RcsTypeIdPair implements Parcelable {
    public static final Parcelable.Creator<RcsTypeIdPair> CREATOR = new Parcelable.Creator<RcsTypeIdPair>() { // from class: com.android.ims.RcsTypeIdPair.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public RcsTypeIdPair createFromParcel(Parcel in) {
            return new RcsTypeIdPair(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public RcsTypeIdPair[] newArray(int size) {
            return new RcsTypeIdPair[size];
        }
    };
    private int mId;
    private int mType;

    public RcsTypeIdPair(int type, int id) {
        this.mType = type;
        this.mId = id;
    }

    public int getType() {
        return this.mType;
    }

    public void setType(int type) {
        this.mType = type;
    }

    public int getId() {
        return this.mId;
    }

    public void setId(int id) {
        this.mId = id;
    }

    public RcsTypeIdPair(Parcel in) {
        this.mType = in.readInt();
        this.mId = in.readInt();
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mType);
        dest.writeInt(this.mId);
    }
}
