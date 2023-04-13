package com.android.internal.telephony;

import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes3.dex */
public class CellNetworkScanResult implements Parcelable {
    public static final Parcelable.Creator<CellNetworkScanResult> CREATOR = new Parcelable.Creator<CellNetworkScanResult>() { // from class: com.android.internal.telephony.CellNetworkScanResult.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public CellNetworkScanResult createFromParcel(Parcel in) {
            return new CellNetworkScanResult(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public CellNetworkScanResult[] newArray(int size) {
            return new CellNetworkScanResult[size];
        }
    };
    public static final int STATUS_RADIO_GENERIC_FAILURE = 3;
    public static final int STATUS_RADIO_NOT_AVAILABLE = 2;
    public static final int STATUS_SUCCESS = 1;
    public static final int STATUS_UNKNOWN_ERROR = 4;
    private final List<OperatorInfo> mOperators;
    private final int mStatus;

    public CellNetworkScanResult(int status, List<OperatorInfo> operators) {
        this.mStatus = status;
        this.mOperators = operators;
    }

    private CellNetworkScanResult(Parcel in) {
        this.mStatus = in.readInt();
        int len = in.readInt();
        if (len > 0) {
            this.mOperators = new ArrayList();
            for (int i = 0; i < len; i++) {
                this.mOperators.add(OperatorInfo.CREATOR.createFromParcel(in));
            }
            return;
        }
        this.mOperators = null;
    }

    public int getStatus() {
        return this.mStatus;
    }

    public List<OperatorInfo> getOperators() {
        return this.mOperators;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(this.mStatus);
        List<OperatorInfo> list = this.mOperators;
        if (list != null && list.size() > 0) {
            out.writeInt(this.mOperators.size());
            for (OperatorInfo network : this.mOperators) {
                network.writeToParcel(out, flags);
            }
            return;
        }
        out.writeInt(0);
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("CellNetworkScanResult: {");
        sb.append(" status:").append(this.mStatus);
        List<OperatorInfo> list = this.mOperators;
        if (list != null) {
            for (OperatorInfo network : list) {
                sb.append(" network:").append(network);
            }
        }
        sb.append("}");
        return sb.toString();
    }
}
