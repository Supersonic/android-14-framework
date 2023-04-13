package android.telephony;

import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.telephony.cdma.CdmaCellLocation;
import android.text.TextUtils;
import java.util.Objects;
/* loaded from: classes3.dex */
public final class CellIdentityCdma extends CellIdentity {
    private static final int BASESTATION_ID_MAX = 65535;
    private static final boolean DBG = false;
    private static final int LATITUDE_MAX = 1296000;
    private static final int LATITUDE_MIN = -1296000;
    private static final int LONGITUDE_MAX = 2592000;
    private static final int LONGITUDE_MIN = -2592000;
    private static final int NETWORK_ID_MAX = 65535;
    private static final int SYSTEM_ID_MAX = 32767;
    private final int mBasestationId;
    private final int mLatitude;
    private final int mLongitude;
    private final int mNetworkId;
    private final int mSystemId;
    private static final String TAG = CellIdentityCdma.class.getSimpleName();
    public static final Parcelable.Creator<CellIdentityCdma> CREATOR = new Parcelable.Creator<CellIdentityCdma>() { // from class: android.telephony.CellIdentityCdma.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public CellIdentityCdma createFromParcel(Parcel in) {
            in.readInt();
            return CellIdentityCdma.createFromParcelBody(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public CellIdentityCdma[] newArray(int size) {
            return new CellIdentityCdma[size];
        }
    };

    public CellIdentityCdma() {
        super(TAG, 2, null, null, null, null);
        this.mNetworkId = Integer.MAX_VALUE;
        this.mSystemId = Integer.MAX_VALUE;
        this.mBasestationId = Integer.MAX_VALUE;
        this.mLongitude = Integer.MAX_VALUE;
        this.mLatitude = Integer.MAX_VALUE;
        this.mGlobalCellId = null;
    }

    public CellIdentityCdma(int nid, int sid, int bid, int lon, int lat, String alphal, String alphas) {
        super(TAG, 2, null, null, alphal, alphas);
        this.mNetworkId = inRangeOrUnavailable(nid, 0, 65535);
        this.mSystemId = inRangeOrUnavailable(sid, 0, 32767);
        this.mBasestationId = inRangeOrUnavailable(bid, 0, 65535);
        int lat2 = inRangeOrUnavailable(lat, (int) LATITUDE_MIN, (int) LATITUDE_MAX);
        int lon2 = inRangeOrUnavailable(lon, (int) LONGITUDE_MIN, (int) LONGITUDE_MAX);
        if (!isNullIsland(lat2, lon2)) {
            this.mLongitude = lon2;
            this.mLatitude = lat2;
        } else {
            this.mLatitude = Integer.MAX_VALUE;
            this.mLongitude = Integer.MAX_VALUE;
        }
        updateGlobalCellId();
    }

    private CellIdentityCdma(CellIdentityCdma cid) {
        this(cid.mNetworkId, cid.mSystemId, cid.mBasestationId, cid.mLongitude, cid.mLatitude, cid.mAlphaLong, cid.mAlphaShort);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public CellIdentityCdma copy() {
        return new CellIdentityCdma(this);
    }

    @Override // android.telephony.CellIdentity
    public CellIdentityCdma sanitizeLocationInfo() {
        return new CellIdentityCdma(Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, this.mAlphaLong, this.mAlphaShort);
    }

    @Override // android.telephony.CellIdentity
    protected void updateGlobalCellId() {
        int i;
        this.mGlobalCellId = null;
        if (this.mNetworkId == Integer.MAX_VALUE || (i = this.mSystemId) == Integer.MAX_VALUE || this.mBasestationId == Integer.MAX_VALUE) {
            return;
        }
        this.mGlobalCellId = TextUtils.formatSimple("%04x%04x%04x", Integer.valueOf(i), Integer.valueOf(this.mNetworkId), Integer.valueOf(this.mBasestationId));
    }

    private boolean isNullIsland(int lat, int lon) {
        return Math.abs(lat) <= 1 && Math.abs(lon) <= 1;
    }

    public int getNetworkId() {
        return this.mNetworkId;
    }

    public int getSystemId() {
        return this.mSystemId;
    }

    public int getBasestationId() {
        return this.mBasestationId;
    }

    public int getLongitude() {
        return this.mLongitude;
    }

    public int getLatitude() {
        return this.mLatitude;
    }

    @Override // android.telephony.CellIdentity
    public int hashCode() {
        return Objects.hash(Integer.valueOf(this.mNetworkId), Integer.valueOf(this.mSystemId), Integer.valueOf(this.mBasestationId), Integer.valueOf(this.mLatitude), Integer.valueOf(this.mLongitude), Integer.valueOf(super.hashCode()));
    }

    @Override // android.telephony.CellIdentity
    public CdmaCellLocation asCellLocation() {
        CdmaCellLocation cl = new CdmaCellLocation();
        int i = this.mBasestationId;
        int bsid = i != Integer.MAX_VALUE ? i : -1;
        int i2 = this.mSystemId;
        int sid = i2 != Integer.MAX_VALUE ? i2 : -1;
        int i3 = this.mNetworkId;
        int nid = i3 != Integer.MAX_VALUE ? i3 : -1;
        cl.setCellLocationData(bsid, this.mLatitude, this.mLongitude, sid, nid);
        return cl;
    }

    @Override // android.telephony.CellIdentity
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other instanceof CellIdentityCdma) {
            CellIdentityCdma o = (CellIdentityCdma) other;
            return this.mNetworkId == o.mNetworkId && this.mSystemId == o.mSystemId && this.mBasestationId == o.mBasestationId && this.mLatitude == o.mLatitude && this.mLongitude == o.mLongitude && super.equals(other);
        }
        return false;
    }

    public String toString() {
        return TAG + ":{ mNetworkId=" + this.mNetworkId + " mSystemId=" + this.mSystemId + " mBasestationId=" + this.mBasestationId + " mLongitude=" + this.mLongitude + " mLatitude=" + this.mLatitude + " mAlphaLong=" + this.mAlphaLong + " mAlphaShort=" + this.mAlphaShort + "}";
    }

    @Override // android.telephony.CellIdentity, android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, 2);
        dest.writeInt(this.mNetworkId);
        dest.writeInt(this.mSystemId);
        dest.writeInt(this.mBasestationId);
        dest.writeInt(this.mLongitude);
        dest.writeInt(this.mLatitude);
    }

    private CellIdentityCdma(Parcel in) {
        super(TAG, 2, in);
        this.mNetworkId = in.readInt();
        this.mSystemId = in.readInt();
        this.mBasestationId = in.readInt();
        this.mLongitude = in.readInt();
        this.mLatitude = in.readInt();
        updateGlobalCellId();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static CellIdentityCdma createFromParcelBody(Parcel in) {
        return new CellIdentityCdma(in);
    }
}
