package android.telephony;

import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.text.format.DateFormat;
import android.util.SparseArray;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
/* loaded from: classes3.dex */
public final class ClientRequestStats implements Parcelable {
    public static final Parcelable.Creator<ClientRequestStats> CREATOR = new Parcelable.Creator<ClientRequestStats>() { // from class: android.telephony.ClientRequestStats.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ClientRequestStats createFromParcel(Parcel in) {
            return new ClientRequestStats(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ClientRequestStats[] newArray(int size) {
            return new ClientRequestStats[size];
        }
    };
    private static final int REQUEST_HISTOGRAM_BUCKET_COUNT = 5;
    private String mCallingPackage;
    private long mCompletedRequestsCount;
    private long mCompletedRequestsWakelockTime;
    private long mPendingRequestsCount;
    private long mPendingRequestsWakelockTime;
    private SparseArray<TelephonyHistogram> mRequestHistograms;

    public ClientRequestStats(Parcel in) {
        this.mCompletedRequestsWakelockTime = 0L;
        this.mCompletedRequestsCount = 0L;
        this.mPendingRequestsWakelockTime = 0L;
        this.mPendingRequestsCount = 0L;
        this.mRequestHistograms = new SparseArray<>();
        readFromParcel(in);
    }

    public ClientRequestStats() {
        this.mCompletedRequestsWakelockTime = 0L;
        this.mCompletedRequestsCount = 0L;
        this.mPendingRequestsWakelockTime = 0L;
        this.mPendingRequestsCount = 0L;
        this.mRequestHistograms = new SparseArray<>();
    }

    public ClientRequestStats(ClientRequestStats clientRequestStats) {
        this.mCompletedRequestsWakelockTime = 0L;
        this.mCompletedRequestsCount = 0L;
        this.mPendingRequestsWakelockTime = 0L;
        this.mPendingRequestsCount = 0L;
        this.mRequestHistograms = new SparseArray<>();
        this.mCallingPackage = clientRequestStats.getCallingPackage();
        this.mCompletedRequestsCount = clientRequestStats.getCompletedRequestsCount();
        this.mCompletedRequestsWakelockTime = clientRequestStats.getCompletedRequestsWakelockTime();
        this.mPendingRequestsCount = clientRequestStats.getPendingRequestsCount();
        this.mPendingRequestsWakelockTime = clientRequestStats.getPendingRequestsWakelockTime();
        List<TelephonyHistogram> list = clientRequestStats.getRequestHistograms();
        for (TelephonyHistogram entry : list) {
            this.mRequestHistograms.put(entry.getId(), entry);
        }
    }

    public String getCallingPackage() {
        return this.mCallingPackage;
    }

    public void setCallingPackage(String mCallingPackage) {
        this.mCallingPackage = mCallingPackage;
    }

    public long getCompletedRequestsWakelockTime() {
        return this.mCompletedRequestsWakelockTime;
    }

    public void addCompletedWakelockTime(long completedRequestsWakelockTime) {
        this.mCompletedRequestsWakelockTime += completedRequestsWakelockTime;
    }

    public long getPendingRequestsWakelockTime() {
        return this.mPendingRequestsWakelockTime;
    }

    public void setPendingRequestsWakelockTime(long pendingRequestsWakelockTime) {
        this.mPendingRequestsWakelockTime = pendingRequestsWakelockTime;
    }

    public long getCompletedRequestsCount() {
        return this.mCompletedRequestsCount;
    }

    public void incrementCompletedRequestsCount() {
        this.mCompletedRequestsCount++;
    }

    public long getPendingRequestsCount() {
        return this.mPendingRequestsCount;
    }

    public void setPendingRequestsCount(long pendingRequestsCount) {
        this.mPendingRequestsCount = pendingRequestsCount;
    }

    public List<TelephonyHistogram> getRequestHistograms() {
        List<TelephonyHistogram> list;
        synchronized (this.mRequestHistograms) {
            list = new ArrayList<>(this.mRequestHistograms.size());
            for (int i = 0; i < this.mRequestHistograms.size(); i++) {
                TelephonyHistogram entry = new TelephonyHistogram(this.mRequestHistograms.valueAt(i));
                list.add(entry);
            }
        }
        return list;
    }

    public void updateRequestHistograms(int requestId, int time) {
        synchronized (this.mRequestHistograms) {
            TelephonyHistogram entry = this.mRequestHistograms.get(requestId);
            if (entry == null) {
                entry = new TelephonyHistogram(1, requestId, 5);
                this.mRequestHistograms.put(requestId, entry);
            }
            entry.addTimeTaken(time);
        }
    }

    public String toString() {
        return "ClientRequestStats{mCallingPackage='" + this.mCallingPackage + DateFormat.QUOTE + ", mCompletedRequestsWakelockTime=" + this.mCompletedRequestsWakelockTime + ", mCompletedRequestsCount=" + this.mCompletedRequestsCount + ", mPendingRequestsWakelockTime=" + this.mPendingRequestsWakelockTime + ", mPendingRequestsCount=" + this.mPendingRequestsCount + '}';
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    public void readFromParcel(Parcel in) {
        this.mCallingPackage = in.readString();
        this.mCompletedRequestsWakelockTime = in.readLong();
        this.mCompletedRequestsCount = in.readLong();
        this.mPendingRequestsWakelockTime = in.readLong();
        this.mPendingRequestsCount = in.readLong();
        ArrayList<TelephonyHistogram> requestHistograms = new ArrayList<>();
        in.readTypedList(requestHistograms, TelephonyHistogram.CREATOR);
        Iterator<TelephonyHistogram> it = requestHistograms.iterator();
        while (it.hasNext()) {
            TelephonyHistogram h = it.next();
            this.mRequestHistograms.put(h.getId(), h);
        }
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mCallingPackage);
        dest.writeLong(this.mCompletedRequestsWakelockTime);
        dest.writeLong(this.mCompletedRequestsCount);
        dest.writeLong(this.mPendingRequestsWakelockTime);
        dest.writeLong(this.mPendingRequestsCount);
        dest.writeTypedList(getRequestHistograms());
    }
}
