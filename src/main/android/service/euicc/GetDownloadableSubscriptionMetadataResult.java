package android.service.euicc;

import android.annotation.SystemApi;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.telephony.euicc.DownloadableSubscription;
@SystemApi
/* loaded from: classes3.dex */
public final class GetDownloadableSubscriptionMetadataResult implements Parcelable {
    public static final Parcelable.Creator<GetDownloadableSubscriptionMetadataResult> CREATOR = new Parcelable.Creator<GetDownloadableSubscriptionMetadataResult>() { // from class: android.service.euicc.GetDownloadableSubscriptionMetadataResult.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public GetDownloadableSubscriptionMetadataResult createFromParcel(Parcel in) {
            return new GetDownloadableSubscriptionMetadataResult(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public GetDownloadableSubscriptionMetadataResult[] newArray(int size) {
            return new GetDownloadableSubscriptionMetadataResult[size];
        }
    };
    private final DownloadableSubscription mSubscription;
    @Deprecated
    public final int result;

    public int getResult() {
        return this.result;
    }

    public DownloadableSubscription getDownloadableSubscription() {
        return this.mSubscription;
    }

    public GetDownloadableSubscriptionMetadataResult(int result, DownloadableSubscription subscription) {
        this.result = result;
        if (result == 0) {
            this.mSubscription = subscription;
        } else if (subscription != null) {
            throw new IllegalArgumentException("Error result with non-null subscription: " + result);
        } else {
            this.mSubscription = null;
        }
    }

    private GetDownloadableSubscriptionMetadataResult(Parcel in) {
        this.result = in.readInt();
        this.mSubscription = (DownloadableSubscription) in.readTypedObject(DownloadableSubscription.CREATOR);
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.result);
        dest.writeTypedObject(this.mSubscription, flags);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }
}
