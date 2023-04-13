package android.telephony;

import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Objects;
/* loaded from: classes3.dex */
public abstract class CellInfo implements Parcelable {
    public static final int CONNECTION_NONE = 0;
    public static final int CONNECTION_PRIMARY_SERVING = 1;
    public static final int CONNECTION_SECONDARY_SERVING = 2;
    public static final int CONNECTION_UNKNOWN = Integer.MAX_VALUE;
    public static final Parcelable.Creator<CellInfo> CREATOR = new Parcelable.Creator<CellInfo>() { // from class: android.telephony.CellInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public CellInfo createFromParcel(Parcel in) {
            int type = in.readInt();
            switch (type) {
                case 1:
                    return CellInfoGsm.createFromParcelBody(in);
                case 2:
                    return CellInfoCdma.createFromParcelBody(in);
                case 3:
                    return CellInfoLte.createFromParcelBody(in);
                case 4:
                    return CellInfoWcdma.createFromParcelBody(in);
                case 5:
                    return CellInfoTdscdma.createFromParcelBody(in);
                case 6:
                    return CellInfoNr.createFromParcelBody(in);
                default:
                    throw new RuntimeException("Bad CellInfo Parcel");
            }
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public CellInfo[] newArray(int size) {
            return new CellInfo[size];
        }
    };
    public static final int TIMESTAMP_TYPE_ANTENNA = 1;
    public static final int TIMESTAMP_TYPE_JAVA_RIL = 4;
    public static final int TIMESTAMP_TYPE_MODEM = 2;
    public static final int TIMESTAMP_TYPE_OEM_RIL = 3;
    public static final int TIMESTAMP_TYPE_UNKNOWN = 0;
    public static final int TYPE_CDMA = 2;
    public static final int TYPE_GSM = 1;
    public static final int TYPE_LTE = 3;
    public static final int TYPE_NR = 6;
    public static final int TYPE_TDSCDMA = 5;
    public static final int TYPE_UNKNOWN = 0;
    public static final int TYPE_WCDMA = 4;
    public static final int UNAVAILABLE = Integer.MAX_VALUE;
    public static final long UNAVAILABLE_LONG = Long.MAX_VALUE;
    private int mCellConnectionStatus;
    private boolean mRegistered;
    private long mTimeStamp;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface CellConnectionStatus {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface Type {
    }

    public abstract CellIdentity getCellIdentity();

    public abstract CellSignalStrength getCellSignalStrength();

    @Override // android.p008os.Parcelable
    public abstract void writeToParcel(Parcel parcel, int i);

    /* JADX INFO: Access modifiers changed from: protected */
    public CellInfo(int cellConnectionStatus, boolean registered, long timestamp) {
        this.mCellConnectionStatus = cellConnectionStatus;
        this.mRegistered = registered;
        this.mTimeStamp = timestamp;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public CellInfo() {
        this.mRegistered = false;
        this.mTimeStamp = Long.MAX_VALUE;
        this.mCellConnectionStatus = 0;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public CellInfo(CellInfo ci) {
        this.mRegistered = ci.mRegistered;
        this.mTimeStamp = ci.mTimeStamp;
        this.mCellConnectionStatus = ci.mCellConnectionStatus;
    }

    public boolean isRegistered() {
        return this.mRegistered;
    }

    public void setRegistered(boolean registered) {
        this.mRegistered = registered;
    }

    public long getTimestampMillis() {
        return this.mTimeStamp / 1000000;
    }

    @Deprecated
    public long getTimeStamp() {
        return this.mTimeStamp;
    }

    public void setTimeStamp(long ts) {
        this.mTimeStamp = ts;
    }

    public CellInfo sanitizeLocationInfo() {
        return null;
    }

    public int getCellConnectionStatus() {
        return this.mCellConnectionStatus;
    }

    public void setCellConnectionStatus(int cellConnectionStatus) {
        this.mCellConnectionStatus = cellConnectionStatus;
    }

    public int hashCode() {
        return Objects.hash(Integer.valueOf(this.mCellConnectionStatus), Boolean.valueOf(this.mRegistered), Long.valueOf(this.mTimeStamp));
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof CellInfo) {
            CellInfo cellInfo = (CellInfo) o;
            return this.mCellConnectionStatus == cellInfo.mCellConnectionStatus && this.mRegistered == cellInfo.mRegistered && this.mTimeStamp == cellInfo.mTimeStamp;
        }
        return false;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("mRegistered=").append(this.mRegistered ? "YES" : "NO");
        sb.append(" mTimeStamp=").append(this.mTimeStamp).append("ns");
        sb.append(" mCellConnectionStatus=").append(this.mCellConnectionStatus);
        return sb.toString();
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void writeToParcel(Parcel dest, int flags, int type) {
        dest.writeInt(type);
        dest.writeInt(this.mRegistered ? 1 : 0);
        dest.writeLong(this.mTimeStamp);
        dest.writeInt(this.mCellConnectionStatus);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public CellInfo(Parcel in) {
        this.mRegistered = in.readInt() == 1;
        this.mTimeStamp = in.readLong();
        this.mCellConnectionStatus = in.readInt();
    }
}
