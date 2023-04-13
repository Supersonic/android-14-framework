package com.android.ims.internal.uce.common;

import android.p008os.Parcel;
import android.p008os.Parcelable;
/* loaded from: classes4.dex */
public class StatusCode implements Parcelable {
    public static final Parcelable.Creator<StatusCode> CREATOR = new Parcelable.Creator<StatusCode>() { // from class: com.android.ims.internal.uce.common.StatusCode.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public StatusCode createFromParcel(Parcel source) {
            return new StatusCode(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public StatusCode[] newArray(int size) {
            return new StatusCode[size];
        }
    };
    public static final int UCE_FAILURE = 1;
    public static final int UCE_FETCH_ERROR = 6;
    public static final int UCE_INSUFFICIENT_MEMORY = 8;
    public static final int UCE_INVALID_FEATURE_TAG = 15;
    public static final int UCE_INVALID_LISTENER_HANDLE = 4;
    public static final int UCE_INVALID_PARAM = 5;
    public static final int UCE_INVALID_SERVICE_HANDLE = 3;
    public static final int UCE_LOST_NET = 9;
    public static final int UCE_NOT_FOUND = 11;
    public static final int UCE_NOT_SUPPORTED = 10;
    public static final int UCE_NO_CHANGE_IN_CAP = 13;
    public static final int UCE_REQUEST_TIMEOUT = 7;
    public static final int UCE_SERVICE_AVAILABLE = 16;
    public static final int UCE_SERVICE_UNAVAILABLE = 12;
    public static final int UCE_SERVICE_UNKNOWN = 14;
    public static final int UCE_SUCCESS = 0;
    public static final int UCE_SUCCESS_ASYC_UPDATE = 2;
    private int mStatusCode;

    public StatusCode() {
        this.mStatusCode = 0;
    }

    public int getStatusCode() {
        return this.mStatusCode;
    }

    public void setStatusCode(int nStatusCode) {
        this.mStatusCode = nStatusCode;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mStatusCode);
    }

    private StatusCode(Parcel source) {
        this.mStatusCode = 0;
        readFromParcel(source);
    }

    public void readFromParcel(Parcel source) {
        this.mStatusCode = source.readInt();
    }
}
