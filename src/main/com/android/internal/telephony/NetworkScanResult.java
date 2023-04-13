package com.android.internal.telephony;

import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.telephony.CellInfo;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
/* loaded from: classes3.dex */
public final class NetworkScanResult implements Parcelable {
    public static final Parcelable.Creator<NetworkScanResult> CREATOR = new Parcelable.Creator<NetworkScanResult>() { // from class: com.android.internal.telephony.NetworkScanResult.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public NetworkScanResult createFromParcel(Parcel in) {
            return new NetworkScanResult(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public NetworkScanResult[] newArray(int size) {
            return new NetworkScanResult[size];
        }
    };
    public static final int SCAN_STATUS_COMPLETE = 2;
    public static final int SCAN_STATUS_PARTIAL = 1;
    public List<CellInfo> networkInfos;
    public int scanError;
    public int scanStatus;

    public NetworkScanResult(int scanStatus, int scanError, List<CellInfo> networkInfos) {
        this.scanStatus = scanStatus;
        this.scanError = scanError;
        this.networkInfos = networkInfos;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.scanStatus);
        dest.writeInt(this.scanError);
        dest.writeParcelableList(this.networkInfos, flags);
    }

    private NetworkScanResult(Parcel in) {
        this.scanStatus = in.readInt();
        this.scanError = in.readInt();
        ArrayList arrayList = new ArrayList();
        in.readParcelableList(arrayList, Object.class.getClassLoader(), CellInfo.class);
        this.networkInfos = arrayList;
    }

    public boolean equals(Object o) {
        try {
            NetworkScanResult nsr = (NetworkScanResult) o;
            return o != null && this.scanStatus == nsr.scanStatus && this.scanError == nsr.scanError && this.networkInfos.equals(nsr.networkInfos);
        } catch (ClassCastException e) {
            return false;
        }
    }

    public String toString() {
        return "{" + ("scanStatus=" + this.scanStatus) + (", scanError=" + this.scanError) + (", networkInfos=" + this.networkInfos) + "}";
    }

    public int hashCode() {
        return (this.scanStatus * 31) + (this.scanError * 23) + (Objects.hashCode(this.networkInfos) * 37);
    }
}
