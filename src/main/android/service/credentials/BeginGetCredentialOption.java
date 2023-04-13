package android.service.credentials;

import android.annotation.NonNull;
import android.p008os.Bundle;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import com.android.internal.util.AnnotationValidations;
import com.android.internal.util.Preconditions;
/* loaded from: classes3.dex */
public class BeginGetCredentialOption implements Parcelable {
    private static final String BUNDLE_ID_KEY = "android.service.credentials.BeginGetCredentialOption.BUNDLE_ID_KEY";
    public static final Parcelable.Creator<BeginGetCredentialOption> CREATOR = new Parcelable.Creator<BeginGetCredentialOption>() { // from class: android.service.credentials.BeginGetCredentialOption.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public BeginGetCredentialOption[] newArray(int size) {
            return new BeginGetCredentialOption[size];
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public BeginGetCredentialOption createFromParcel(Parcel in) {
            return new BeginGetCredentialOption(in);
        }
    };
    private final Bundle mCandidateQueryData;
    private final String mId;
    private final String mType;

    public String getId() {
        return this.mId;
    }

    public String getType() {
        return this.mType;
    }

    public Bundle getCandidateQueryData() {
        return this.mCandidateQueryData;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString8(this.mType);
        dest.writeBundle(this.mCandidateQueryData);
        dest.writeString8(this.mId);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    public String toString() {
        return "GetCredentialOption {type=" + this.mType + ", candidateQueryData=" + this.mCandidateQueryData + ", id=" + this.mId + "}";
    }

    public BeginGetCredentialOption(String id, String type, Bundle candidateQueryData) {
        this.mId = (String) Preconditions.checkStringNotEmpty(id, "id must not be empty");
        this.mType = (String) Preconditions.checkStringNotEmpty(type, "type must not be empty");
        Bundle bundle = new Bundle();
        bundle.putAll(candidateQueryData);
        this.mCandidateQueryData = bundle;
        addIdToBundle();
    }

    private void addIdToBundle() {
        this.mCandidateQueryData.putString(BUNDLE_ID_KEY, this.mId);
    }

    private BeginGetCredentialOption(Parcel in) {
        String type = in.readString8();
        Bundle candidateQueryData = in.readBundle();
        String id = in.readString8();
        this.mType = type;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) type);
        this.mCandidateQueryData = candidateQueryData;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) candidateQueryData);
        this.mId = id;
    }
}
