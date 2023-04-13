package android.telephony;

import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Arrays;
/* loaded from: classes3.dex */
public final class NetworkScanRequest implements Parcelable {
    public static final Parcelable.Creator<NetworkScanRequest> CREATOR = new Parcelable.Creator<NetworkScanRequest>() { // from class: android.telephony.NetworkScanRequest.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public NetworkScanRequest createFromParcel(Parcel in) {
            return new NetworkScanRequest(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public NetworkScanRequest[] newArray(int size) {
            return new NetworkScanRequest[size];
        }
    };
    public static final int MAX_BANDS = 8;
    public static final int MAX_CHANNELS = 32;
    public static final int MAX_INCREMENTAL_PERIODICITY_SEC = 10;
    public static final int MAX_MCC_MNC_LIST_SIZE = 20;
    public static final int MAX_RADIO_ACCESS_NETWORKS = 8;
    public static final int MAX_SEARCH_MAX_SEC = 3600;
    public static final int MAX_SEARCH_PERIODICITY_SEC = 300;
    public static final int MIN_INCREMENTAL_PERIODICITY_SEC = 1;
    public static final int MIN_SEARCH_MAX_SEC = 60;
    public static final int MIN_SEARCH_PERIODICITY_SEC = 5;
    public static final int SCAN_TYPE_ONE_SHOT = 0;
    public static final int SCAN_TYPE_PERIODIC = 1;
    private boolean mIncrementalResults;
    private int mIncrementalResultsPeriodicity;
    private int mMaxSearchTime;
    private ArrayList<String> mMccMncs;
    private int mScanType;
    private int mSearchPeriodicity;
    private RadioAccessSpecifier[] mSpecifiers;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes3.dex */
    public @interface ScanType {
    }

    public NetworkScanRequest(int scanType, RadioAccessSpecifier[] specifiers, int searchPeriodicity, int maxSearchTime, boolean incrementalResults, int incrementalResultsPeriodicity, ArrayList<String> mccMncs) {
        this.mScanType = scanType;
        if (specifiers != null) {
            this.mSpecifiers = (RadioAccessSpecifier[]) specifiers.clone();
        } else {
            this.mSpecifiers = null;
        }
        this.mSearchPeriodicity = searchPeriodicity;
        this.mMaxSearchTime = maxSearchTime;
        this.mIncrementalResults = incrementalResults;
        this.mIncrementalResultsPeriodicity = incrementalResultsPeriodicity;
        if (mccMncs != null) {
            this.mMccMncs = (ArrayList) mccMncs.clone();
        } else {
            this.mMccMncs = new ArrayList<>();
        }
    }

    public int getScanType() {
        return this.mScanType;
    }

    public int getSearchPeriodicity() {
        return this.mSearchPeriodicity;
    }

    public int getMaxSearchTime() {
        return this.mMaxSearchTime;
    }

    public boolean getIncrementalResults() {
        return this.mIncrementalResults;
    }

    public int getIncrementalResultsPeriodicity() {
        return this.mIncrementalResultsPeriodicity;
    }

    public RadioAccessSpecifier[] getSpecifiers() {
        RadioAccessSpecifier[] radioAccessSpecifierArr = this.mSpecifiers;
        if (radioAccessSpecifierArr == null) {
            return null;
        }
        return (RadioAccessSpecifier[]) radioAccessSpecifierArr.clone();
    }

    public ArrayList<String> getPlmns() {
        return (ArrayList) this.mMccMncs.clone();
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mScanType);
        dest.writeParcelableArray(this.mSpecifiers, flags);
        dest.writeInt(this.mSearchPeriodicity);
        dest.writeInt(this.mMaxSearchTime);
        dest.writeBoolean(this.mIncrementalResults);
        dest.writeInt(this.mIncrementalResultsPeriodicity);
        dest.writeStringList(this.mMccMncs);
    }

    private NetworkScanRequest(Parcel in) {
        this.mScanType = in.readInt();
        Parcelable[] tempSpecifiers = (Parcelable[]) in.readParcelableArray(Object.class.getClassLoader(), RadioAccessSpecifier.class);
        if (tempSpecifiers != null) {
            this.mSpecifiers = new RadioAccessSpecifier[tempSpecifiers.length];
            for (int i = 0; i < tempSpecifiers.length; i++) {
                this.mSpecifiers[i] = (RadioAccessSpecifier) tempSpecifiers[i];
            }
        } else {
            this.mSpecifiers = null;
        }
        this.mSearchPeriodicity = in.readInt();
        this.mMaxSearchTime = in.readInt();
        this.mIncrementalResults = in.readBoolean();
        this.mIncrementalResultsPeriodicity = in.readInt();
        ArrayList<String> arrayList = new ArrayList<>();
        this.mMccMncs = arrayList;
        in.readStringList(arrayList);
    }

    public boolean equals(Object o) {
        ArrayList<String> arrayList;
        try {
            NetworkScanRequest nsr = (NetworkScanRequest) o;
            return o != null && this.mScanType == nsr.mScanType && Arrays.equals(this.mSpecifiers, nsr.mSpecifiers) && this.mSearchPeriodicity == nsr.mSearchPeriodicity && this.mMaxSearchTime == nsr.mMaxSearchTime && this.mIncrementalResults == nsr.mIncrementalResults && this.mIncrementalResultsPeriodicity == nsr.mIncrementalResultsPeriodicity && (arrayList = this.mMccMncs) != null && arrayList.equals(nsr.mMccMncs);
        } catch (ClassCastException e) {
            return false;
        }
    }

    public int hashCode() {
        return (this.mScanType * 31) + (Arrays.hashCode(this.mSpecifiers) * 37) + (this.mSearchPeriodicity * 41) + (this.mMaxSearchTime * 43) + ((!this.mIncrementalResults ? 0 : 1) * 47) + (this.mIncrementalResultsPeriodicity * 53) + (this.mMccMncs.hashCode() * 59);
    }
}
