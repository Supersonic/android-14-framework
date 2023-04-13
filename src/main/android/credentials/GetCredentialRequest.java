package android.credentials;

import android.annotation.NonNull;
import android.p008os.Bundle;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import com.android.internal.util.AnnotationValidations;
import com.android.internal.util.Preconditions;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
/* loaded from: classes.dex */
public final class GetCredentialRequest implements Parcelable {
    public static final Parcelable.Creator<GetCredentialRequest> CREATOR = new Parcelable.Creator<GetCredentialRequest>() { // from class: android.credentials.GetCredentialRequest.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public GetCredentialRequest[] newArray(int size) {
            return new GetCredentialRequest[size];
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public GetCredentialRequest createFromParcel(Parcel in) {
            return new GetCredentialRequest(in);
        }
    };
    private final boolean mAlwaysSendAppInfoToProvider;
    private final List<CredentialOption> mCredentialOptions;
    private final Bundle mData;
    private String mOrigin;

    public List<CredentialOption> getCredentialOptions() {
        return this.mCredentialOptions;
    }

    public Bundle getData() {
        return this.mData;
    }

    public String getOrigin() {
        return this.mOrigin;
    }

    public boolean alwaysSendAppInfoToProvider() {
        return this.mAlwaysSendAppInfoToProvider;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(this.mCredentialOptions, flags);
        dest.writeBundle(this.mData);
        dest.writeBoolean(this.mAlwaysSendAppInfoToProvider);
        dest.writeString8(this.mOrigin);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    public String toString() {
        return "GetCredentialRequest {credentialOption=" + this.mCredentialOptions + ", data=" + this.mData + ", alwaysSendAppInfoToProvider=" + this.mAlwaysSendAppInfoToProvider + ", origin=" + this.mOrigin + "}";
    }

    private GetCredentialRequest(List<CredentialOption> credentialOptions, Bundle data, boolean alwaysSendAppInfoToProvider, String origin) {
        Preconditions.checkCollectionNotEmpty(credentialOptions, "credentialOptions");
        Preconditions.checkCollectionElementsNotNull(credentialOptions, "credentialOptions");
        this.mCredentialOptions = credentialOptions;
        this.mData = (Bundle) Objects.requireNonNull(data, "data must not be null");
        this.mAlwaysSendAppInfoToProvider = alwaysSendAppInfoToProvider;
        this.mOrigin = origin;
    }

    private GetCredentialRequest(Parcel in) {
        ArrayList arrayList = new ArrayList();
        in.readTypedList(arrayList, CredentialOption.CREATOR);
        this.mCredentialOptions = arrayList;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) arrayList);
        Bundle data = in.readBundle();
        this.mData = data;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) data);
        this.mAlwaysSendAppInfoToProvider = in.readBoolean();
        this.mOrigin = in.readString8();
    }

    /* loaded from: classes.dex */
    public static final class Builder {
        private final Bundle mData;
        private String mOrigin;
        private List<CredentialOption> mCredentialOptions = new ArrayList();
        private boolean mAlwaysSendAppInfoToProvider = true;

        public Builder(Bundle data) {
            this.mData = (Bundle) Objects.requireNonNull(data, "data must not be null");
        }

        public Builder addCredentialOption(CredentialOption credentialOption) {
            this.mCredentialOptions.add((CredentialOption) Objects.requireNonNull(credentialOption, "credentialOption must not be null"));
            return this;
        }

        public Builder setAlwaysSendAppInfoToProvider(boolean value) {
            this.mAlwaysSendAppInfoToProvider = value;
            return this;
        }

        public Builder setCredentialOptions(List<CredentialOption> credentialOptions) {
            Preconditions.checkCollectionElementsNotNull(credentialOptions, "credentialOptions");
            this.mCredentialOptions = new ArrayList(credentialOptions);
            return this;
        }

        public Builder setOrigin(String origin) {
            this.mOrigin = origin;
            return this;
        }

        public GetCredentialRequest build() {
            Preconditions.checkCollectionNotEmpty(this.mCredentialOptions, "credentialOptions");
            Preconditions.checkCollectionElementsNotNull(this.mCredentialOptions, "credentialOptions");
            return new GetCredentialRequest(this.mCredentialOptions, this.mData, this.mAlwaysSendAppInfoToProvider, this.mOrigin);
        }
    }
}
