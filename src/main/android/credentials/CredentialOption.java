package android.credentials;

import android.annotation.NonNull;
import android.p008os.Bundle;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import com.android.internal.util.AnnotationValidations;
import com.android.internal.util.Preconditions;
import java.util.Objects;
/* loaded from: classes.dex */
public final class CredentialOption implements Parcelable {
    public static final Parcelable.Creator<CredentialOption> CREATOR = new Parcelable.Creator<CredentialOption>() { // from class: android.credentials.CredentialOption.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public CredentialOption[] newArray(int size) {
            return new CredentialOption[size];
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public CredentialOption createFromParcel(Parcel in) {
            return new CredentialOption(in);
        }
    };
    public static final String FLATTENED_REQUEST = "android.credentials.GetCredentialOption.FLATTENED_REQUEST_STRING";
    private final Bundle mCandidateQueryData;
    private final Bundle mCredentialRetrievalData;
    private final boolean mIsSystemProviderRequired;
    private final String mType;

    public String getType() {
        return this.mType;
    }

    public Bundle getCredentialRetrievalData() {
        return this.mCredentialRetrievalData;
    }

    public Bundle getCandidateQueryData() {
        return this.mCandidateQueryData;
    }

    public boolean isSystemProviderRequired() {
        return this.mIsSystemProviderRequired;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString8(this.mType);
        dest.writeBundle(this.mCredentialRetrievalData);
        dest.writeBundle(this.mCandidateQueryData);
        dest.writeBoolean(this.mIsSystemProviderRequired);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    public String toString() {
        return "CredentialOption {type=" + this.mType + ", requestData=" + this.mCredentialRetrievalData + ", candidateQueryData=" + this.mCandidateQueryData + ", isSystemProviderRequired=" + this.mIsSystemProviderRequired + "}";
    }

    public CredentialOption(String type, Bundle credentialRetrievalData, Bundle candidateQueryData, boolean isSystemProviderRequired) {
        this.mType = (String) Preconditions.checkStringNotEmpty(type, "type must not be empty");
        this.mCredentialRetrievalData = (Bundle) Objects.requireNonNull(credentialRetrievalData, "requestData must not be null");
        this.mCandidateQueryData = (Bundle) Objects.requireNonNull(candidateQueryData, "candidateQueryData must not be null");
        this.mIsSystemProviderRequired = isSystemProviderRequired;
    }

    private CredentialOption(Parcel in) {
        String type = in.readString8();
        Bundle data = in.readBundle();
        Bundle candidateQueryData = in.readBundle();
        boolean isSystemProviderRequired = in.readBoolean();
        this.mType = type;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) type);
        this.mCredentialRetrievalData = data;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) data);
        this.mCandidateQueryData = candidateQueryData;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) candidateQueryData);
        this.mIsSystemProviderRequired = isSystemProviderRequired;
    }
}
