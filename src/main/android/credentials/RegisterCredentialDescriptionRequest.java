package android.credentials;

import android.annotation.NonNull;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import com.android.internal.util.AnnotationValidations;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
/* loaded from: classes.dex */
public final class RegisterCredentialDescriptionRequest implements Parcelable {
    public static final Parcelable.Creator<RegisterCredentialDescriptionRequest> CREATOR = new Parcelable.Creator<RegisterCredentialDescriptionRequest>() { // from class: android.credentials.RegisterCredentialDescriptionRequest.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public RegisterCredentialDescriptionRequest createFromParcel(Parcel in) {
            return new RegisterCredentialDescriptionRequest(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public RegisterCredentialDescriptionRequest[] newArray(int size) {
            return new RegisterCredentialDescriptionRequest[size];
        }
    };
    private final List<CredentialDescription> mCredentialDescriptions;

    public RegisterCredentialDescriptionRequest(CredentialDescription credentialDescription) {
        this.mCredentialDescriptions = Arrays.asList((CredentialDescription) Objects.requireNonNull(credentialDescription));
    }

    public RegisterCredentialDescriptionRequest(Set<CredentialDescription> credentialDescriptions) {
        this.mCredentialDescriptions = new ArrayList((Collection) Objects.requireNonNull(credentialDescriptions));
    }

    private RegisterCredentialDescriptionRequest(Parcel in) {
        ArrayList arrayList = new ArrayList();
        in.readTypedList(arrayList, CredentialDescription.CREATOR);
        ArrayList arrayList2 = new ArrayList();
        this.mCredentialDescriptions = arrayList2;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) arrayList);
        arrayList2.addAll(arrayList);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(this.mCredentialDescriptions, flags);
    }

    public Set<CredentialDescription> getCredentialDescriptions() {
        return new HashSet(this.mCredentialDescriptions);
    }
}
