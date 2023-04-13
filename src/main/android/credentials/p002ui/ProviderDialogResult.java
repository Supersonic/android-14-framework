package android.credentials.p002ui;

import android.annotation.NonNull;
import android.p008os.Bundle;
import android.p008os.IBinder;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import com.android.internal.util.AnnotationValidations;
/* renamed from: android.credentials.ui.ProviderDialogResult */
/* loaded from: classes.dex */
public final class ProviderDialogResult extends BaseDialogResult implements Parcelable {
    public static final Parcelable.Creator<ProviderDialogResult> CREATOR = new Parcelable.Creator<ProviderDialogResult>() { // from class: android.credentials.ui.ProviderDialogResult.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ProviderDialogResult createFromParcel(Parcel in) {
            return new ProviderDialogResult(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ProviderDialogResult[] newArray(int size) {
            return new ProviderDialogResult[size];
        }
    };
    private static final String EXTRA_PROVIDER_RESULT = "android.credentials.ui.extra.PROVIDER_RESULT";
    private final String mProviderId;

    public static ProviderDialogResult fromResultData(Bundle resultData) {
        return (ProviderDialogResult) resultData.getParcelable(EXTRA_PROVIDER_RESULT, ProviderDialogResult.class);
    }

    public static void addToBundle(ProviderDialogResult result, Bundle bundle) {
        bundle.putParcelable(EXTRA_PROVIDER_RESULT, result);
    }

    public ProviderDialogResult(IBinder requestToken, String providerId) {
        super(requestToken);
        this.mProviderId = providerId;
    }

    public String getProviderId() {
        return this.mProviderId;
    }

    protected ProviderDialogResult(Parcel in) {
        super(in);
        String providerId = in.readString8();
        this.mProviderId = providerId;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) providerId);
    }

    @Override // android.credentials.p002ui.BaseDialogResult, android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString8(this.mProviderId);
    }

    @Override // android.credentials.p002ui.BaseDialogResult, android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }
}
