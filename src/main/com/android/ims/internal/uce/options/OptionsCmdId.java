package com.android.ims.internal.uce.options;

import android.p008os.Parcel;
import android.p008os.Parcelable;
/* loaded from: classes4.dex */
public class OptionsCmdId implements Parcelable {
    public static final Parcelable.Creator<OptionsCmdId> CREATOR = new Parcelable.Creator<OptionsCmdId>() { // from class: com.android.ims.internal.uce.options.OptionsCmdId.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public OptionsCmdId createFromParcel(Parcel source) {
            return new OptionsCmdId(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public OptionsCmdId[] newArray(int size) {
            return new OptionsCmdId[size];
        }
    };
    public static final int UCE_OPTIONS_CMD_GETCONTACTCAP = 2;
    public static final int UCE_OPTIONS_CMD_GETCONTACTLISTCAP = 3;
    public static final int UCE_OPTIONS_CMD_GETMYCDINFO = 0;
    public static final int UCE_OPTIONS_CMD_GET_VERSION = 5;
    public static final int UCE_OPTIONS_CMD_RESPONSEINCOMINGOPTIONS = 4;
    public static final int UCE_OPTIONS_CMD_SETMYCDINFO = 1;
    public static final int UCE_OPTIONS_CMD_UNKNOWN = 6;
    private int mCmdId;

    public int getCmdId() {
        return this.mCmdId;
    }

    public void setCmdId(int nCmdId) {
        this.mCmdId = nCmdId;
    }

    public OptionsCmdId() {
        this.mCmdId = 6;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mCmdId);
    }

    private OptionsCmdId(Parcel source) {
        this.mCmdId = 6;
        readFromParcel(source);
    }

    public void readFromParcel(Parcel source) {
        this.mCmdId = source.readInt();
    }
}
