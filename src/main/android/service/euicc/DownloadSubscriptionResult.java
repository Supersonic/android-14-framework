package android.service.euicc;

import android.annotation.SystemApi;
import android.p008os.Parcel;
import android.p008os.Parcelable;
@SystemApi
/* loaded from: classes3.dex */
public final class DownloadSubscriptionResult implements Parcelable {
    public static final Parcelable.Creator<DownloadSubscriptionResult> CREATOR = new Parcelable.Creator<DownloadSubscriptionResult>() { // from class: android.service.euicc.DownloadSubscriptionResult.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public DownloadSubscriptionResult createFromParcel(Parcel in) {
            return new DownloadSubscriptionResult(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public DownloadSubscriptionResult[] newArray(int size) {
            return new DownloadSubscriptionResult[size];
        }
    };
    private final int mCardId;
    private final int mResolvableErrors;
    private final int mResult;

    public DownloadSubscriptionResult(int result, int resolvableErrors, int cardId) {
        this.mResult = result;
        this.mResolvableErrors = resolvableErrors;
        this.mCardId = cardId;
    }

    public int getResult() {
        return this.mResult;
    }

    public int getResolvableErrors() {
        return this.mResolvableErrors;
    }

    public int getCardId() {
        return this.mCardId;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mResult);
        dest.writeInt(this.mResolvableErrors);
        dest.writeInt(this.mCardId);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    private DownloadSubscriptionResult(Parcel in) {
        this.mResult = in.readInt();
        this.mResolvableErrors = in.readInt();
        this.mCardId = in.readInt();
    }
}
