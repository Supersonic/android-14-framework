package android.credentials;

import android.annotation.NonNull;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.service.credentials.CredentialEntry;
import com.android.internal.util.AnnotationValidations;
import com.android.internal.util.Preconditions;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
/* loaded from: classes.dex */
public final class CredentialDescription implements Parcelable {
    public static final Parcelable.Creator<CredentialDescription> CREATOR = new Parcelable.Creator<CredentialDescription>() { // from class: android.credentials.CredentialDescription.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public CredentialDescription createFromParcel(Parcel in) {
            return new CredentialDescription(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public CredentialDescription[] newArray(int size) {
            return new CredentialDescription[size];
        }
    };
    private static final int MAX_ALLOWED_ENTRIES_PER_DESCRIPTION = 16;
    private final List<CredentialEntry> mCredentialEntries;
    private final String mFlattenedRequestString;
    private final String mType;

    public CredentialDescription(String type, String flattenedRequestString, List<CredentialEntry> credentialEntries) {
        this.mType = (String) Preconditions.checkStringNotEmpty(type, "type must not be empty");
        this.mFlattenedRequestString = (String) Preconditions.checkStringNotEmpty(flattenedRequestString);
        this.mCredentialEntries = (List) Objects.requireNonNull(credentialEntries);
        Preconditions.checkArgument(credentialEntries.size() <= 16, "The number of Credential Entries exceed 16.");
        Preconditions.checkArgument(compareEntryTypes(type, credentialEntries) == 0, "Credential Entry type(s) do not match the request type.");
    }

    private CredentialDescription(Parcel in) {
        String type = in.readString8();
        String flattenedRequestString = in.readString();
        ArrayList arrayList = new ArrayList();
        in.readTypedList(arrayList, CredentialEntry.CREATOR);
        this.mType = type;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) type);
        this.mFlattenedRequestString = flattenedRequestString;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) flattenedRequestString);
        this.mCredentialEntries = arrayList;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) arrayList);
    }

    private static int compareEntryTypes(final String type, List<CredentialEntry> credentialEntries) {
        return credentialEntries.stream().filter(new Predicate() { // from class: android.credentials.CredentialDescription$$ExternalSyntheticLambda0
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return CredentialDescription.lambda$compareEntryTypes$0(type, (CredentialEntry) obj);
            }
        }).toList().size();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ boolean lambda$compareEntryTypes$0(String type, CredentialEntry credentialEntry) {
        return !credentialEntry.getType().equals(type);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString8(this.mType);
        dest.writeString(this.mFlattenedRequestString);
        dest.writeTypedList(this.mCredentialEntries, flags);
    }

    public String getType() {
        return this.mType;
    }

    public String getFlattenedRequestString() {
        return this.mFlattenedRequestString;
    }

    public List<CredentialEntry> getCredentialEntries() {
        return this.mCredentialEntries;
    }
}
