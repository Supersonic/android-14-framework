package android.service.euicc;

import android.annotation.SystemApi;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.telephony.euicc.DownloadableSubscription;
import java.util.Arrays;
import java.util.List;
@SystemApi
/* loaded from: classes3.dex */
public final class GetDefaultDownloadableSubscriptionListResult implements Parcelable {
    public static final Parcelable.Creator<GetDefaultDownloadableSubscriptionListResult> CREATOR = new Parcelable.Creator<GetDefaultDownloadableSubscriptionListResult>() { // from class: android.service.euicc.GetDefaultDownloadableSubscriptionListResult.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public GetDefaultDownloadableSubscriptionListResult createFromParcel(Parcel in) {
            return new GetDefaultDownloadableSubscriptionListResult(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public GetDefaultDownloadableSubscriptionListResult[] newArray(int size) {
            return new GetDefaultDownloadableSubscriptionListResult[size];
        }
    };
    private final DownloadableSubscription[] mSubscriptions;
    @Deprecated
    public final int result;

    public int getResult() {
        return this.result;
    }

    public List<DownloadableSubscription> getDownloadableSubscriptions() {
        DownloadableSubscription[] downloadableSubscriptionArr = this.mSubscriptions;
        if (downloadableSubscriptionArr == null) {
            return null;
        }
        return Arrays.asList(downloadableSubscriptionArr);
    }

    public GetDefaultDownloadableSubscriptionListResult(int result, DownloadableSubscription[] subscriptions) {
        this.result = result;
        if (result == 0) {
            this.mSubscriptions = subscriptions;
        } else if (subscriptions != null) {
            throw new IllegalArgumentException("Error result with non-null subscriptions: " + result);
        } else {
            this.mSubscriptions = null;
        }
    }

    private GetDefaultDownloadableSubscriptionListResult(Parcel in) {
        this.result = in.readInt();
        this.mSubscriptions = (DownloadableSubscription[]) in.createTypedArray(DownloadableSubscription.CREATOR);
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.result);
        dest.writeTypedArray(this.mSubscriptions, flags);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }
}
