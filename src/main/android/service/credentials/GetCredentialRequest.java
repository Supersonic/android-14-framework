package android.service.credentials;

import android.annotation.NonNull;
import android.credentials.CredentialOption;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import com.android.internal.util.AnnotationValidations;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
/* loaded from: classes3.dex */
public final class GetCredentialRequest implements Parcelable {
    public static final Parcelable.Creator<GetCredentialRequest> CREATOR = new Parcelable.Creator<GetCredentialRequest>() { // from class: android.service.credentials.GetCredentialRequest.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public GetCredentialRequest createFromParcel(Parcel in) {
            return new GetCredentialRequest(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public GetCredentialRequest[] newArray(int size) {
            return new GetCredentialRequest[size];
        }
    };
    private final CallingAppInfo mCallingAppInfo;
    private final List<CredentialOption> mCredentialOptions;

    public GetCredentialRequest(CallingAppInfo callingAppInfo, List<CredentialOption> credentialOptions) {
        this.mCallingAppInfo = (CallingAppInfo) Objects.requireNonNull(callingAppInfo, "callingAppInfo must not be null");
        this.mCredentialOptions = (List) Objects.requireNonNull(credentialOptions, "credentialOptions must not be null");
    }

    private GetCredentialRequest(Parcel in) {
        CallingAppInfo callingAppInfo = (CallingAppInfo) in.readTypedObject(CallingAppInfo.CREATOR);
        this.mCallingAppInfo = callingAppInfo;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) callingAppInfo);
        ArrayList arrayList = new ArrayList();
        in.readTypedList(arrayList, CredentialOption.CREATOR);
        this.mCredentialOptions = arrayList;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) arrayList);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedObject(this.mCallingAppInfo, flags);
        dest.writeTypedList(this.mCredentialOptions, flags);
    }

    public CallingAppInfo getCallingAppInfo() {
        return this.mCallingAppInfo;
    }

    public List<CredentialOption> getCredentialOptions() {
        return this.mCredentialOptions;
    }
}
