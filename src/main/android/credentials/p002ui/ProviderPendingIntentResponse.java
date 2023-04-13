package android.credentials.p002ui;

import android.content.Intent;
import android.p008os.Parcel;
import android.p008os.Parcelable;
/* renamed from: android.credentials.ui.ProviderPendingIntentResponse */
/* loaded from: classes.dex */
public final class ProviderPendingIntentResponse implements Parcelable {
    public static final Parcelable.Creator<ProviderPendingIntentResponse> CREATOR = new Parcelable.Creator<ProviderPendingIntentResponse>() { // from class: android.credentials.ui.ProviderPendingIntentResponse.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ProviderPendingIntentResponse createFromParcel(Parcel in) {
            return new ProviderPendingIntentResponse(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ProviderPendingIntentResponse[] newArray(int size) {
            return new ProviderPendingIntentResponse[size];
        }
    };
    private final int mResultCode;
    private final Intent mResultData;

    public ProviderPendingIntentResponse(int resultCode, Intent resultData) {
        this.mResultCode = resultCode;
        this.mResultData = resultData;
    }

    protected ProviderPendingIntentResponse(Parcel in) {
        this.mResultCode = in.readInt();
        this.mResultData = (Intent) in.readTypedObject(Intent.CREATOR);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mResultCode);
        dest.writeTypedObject(this.mResultData, flags);
    }

    public int getResultCode() {
        return this.mResultCode;
    }

    public Intent getResultData() {
        return this.mResultData;
    }
}
