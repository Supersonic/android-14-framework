package android.credentials.p002ui;

import android.annotation.NonNull;
import android.p008os.Bundle;
import android.p008os.IBinder;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import com.android.internal.util.AnnotationValidations;
/* renamed from: android.credentials.ui.UserSelectionDialogResult */
/* loaded from: classes.dex */
public final class UserSelectionDialogResult extends BaseDialogResult implements Parcelable {
    public static final Parcelable.Creator<UserSelectionDialogResult> CREATOR = new Parcelable.Creator<UserSelectionDialogResult>() { // from class: android.credentials.ui.UserSelectionDialogResult.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public UserSelectionDialogResult createFromParcel(Parcel in) {
            return new UserSelectionDialogResult(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public UserSelectionDialogResult[] newArray(int size) {
            return new UserSelectionDialogResult[size];
        }
    };
    private static final String EXTRA_USER_SELECTION_RESULT = "android.credentials.ui.extra.USER_SELECTION_RESULT";
    private final String mEntryKey;
    private final String mEntrySubkey;
    private final String mProviderId;
    private ProviderPendingIntentResponse mProviderPendingIntentResponse;

    public static UserSelectionDialogResult fromResultData(Bundle resultData) {
        return (UserSelectionDialogResult) resultData.getParcelable(EXTRA_USER_SELECTION_RESULT, UserSelectionDialogResult.class);
    }

    public static void addToBundle(UserSelectionDialogResult result, Bundle bundle) {
        bundle.putParcelable(EXTRA_USER_SELECTION_RESULT, result);
    }

    public UserSelectionDialogResult(IBinder requestToken, String providerId, String entryKey, String entrySubkey) {
        super(requestToken);
        this.mProviderId = providerId;
        this.mEntryKey = entryKey;
        this.mEntrySubkey = entrySubkey;
    }

    public UserSelectionDialogResult(IBinder requestToken, String providerId, String entryKey, String entrySubkey, ProviderPendingIntentResponse providerPendingIntentResponse) {
        super(requestToken);
        this.mProviderId = providerId;
        this.mEntryKey = entryKey;
        this.mEntrySubkey = entrySubkey;
        this.mProviderPendingIntentResponse = providerPendingIntentResponse;
    }

    public String getProviderId() {
        return this.mProviderId;
    }

    public String getEntryKey() {
        return this.mEntryKey;
    }

    public String getEntrySubkey() {
        return this.mEntrySubkey;
    }

    public ProviderPendingIntentResponse getPendingIntentProviderResponse() {
        return this.mProviderPendingIntentResponse;
    }

    protected UserSelectionDialogResult(Parcel in) {
        super(in);
        String providerId = in.readString8();
        String entryKey = in.readString8();
        String entrySubkey = in.readString8();
        this.mProviderId = providerId;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) providerId);
        this.mEntryKey = entryKey;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) entryKey);
        this.mEntrySubkey = entrySubkey;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) entrySubkey);
        this.mProviderPendingIntentResponse = (ProviderPendingIntentResponse) in.readTypedObject(ProviderPendingIntentResponse.CREATOR);
    }

    @Override // android.credentials.p002ui.BaseDialogResult, android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString8(this.mProviderId);
        dest.writeString8(this.mEntryKey);
        dest.writeString8(this.mEntrySubkey);
        dest.writeTypedObject(this.mProviderPendingIntentResponse, flags);
    }

    @Override // android.credentials.p002ui.BaseDialogResult, android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }
}
