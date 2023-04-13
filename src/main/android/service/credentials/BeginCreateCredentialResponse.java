package android.service.credentials;

import android.annotation.NonNull;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import com.android.internal.util.AnnotationValidations;
import com.android.internal.util.Preconditions;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
/* loaded from: classes3.dex */
public final class BeginCreateCredentialResponse implements Parcelable {
    public static final Parcelable.Creator<BeginCreateCredentialResponse> CREATOR = new Parcelable.Creator<BeginCreateCredentialResponse>() { // from class: android.service.credentials.BeginCreateCredentialResponse.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public BeginCreateCredentialResponse createFromParcel(Parcel in) {
            return new BeginCreateCredentialResponse(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public BeginCreateCredentialResponse[] newArray(int size) {
            return new BeginCreateCredentialResponse[size];
        }
    };
    private final List<CreateEntry> mCreateEntries;
    private final RemoteEntry mRemoteCreateEntry;

    public BeginCreateCredentialResponse() {
        this(new ArrayList(), (RemoteEntry) null);
    }

    private BeginCreateCredentialResponse(Parcel in) {
        ArrayList arrayList = new ArrayList();
        in.readTypedList(arrayList, CreateEntry.CREATOR);
        this.mCreateEntries = arrayList;
        this.mRemoteCreateEntry = (RemoteEntry) in.readTypedObject(RemoteEntry.CREATOR);
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(this.mCreateEntries);
        dest.writeTypedObject(this.mRemoteCreateEntry, flags);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    BeginCreateCredentialResponse(List<CreateEntry> createEntries, RemoteEntry remoteCreateEntry) {
        this.mCreateEntries = createEntries;
        AnnotationValidations.validate((Class<NonNull>) NonNull.class, (NonNull) null, (Object) createEntries);
        this.mRemoteCreateEntry = remoteCreateEntry;
    }

    public List<CreateEntry> getCreateEntries() {
        return this.mCreateEntries;
    }

    public RemoteEntry getRemoteCreateEntry() {
        return this.mRemoteCreateEntry;
    }

    /* loaded from: classes3.dex */
    public static final class Builder {
        private List<CreateEntry> mCreateEntries = new ArrayList();
        private RemoteEntry mRemoteCreateEntry;

        public Builder setCreateEntries(List<CreateEntry> createEntries) {
            Preconditions.checkCollectionNotEmpty(createEntries, "createEntries");
            this.mCreateEntries = (List) Preconditions.checkCollectionElementsNotNull(createEntries, "createEntries");
            return this;
        }

        public Builder addCreateEntry(CreateEntry createEntry) {
            this.mCreateEntries.add((CreateEntry) Objects.requireNonNull(createEntry));
            return this;
        }

        public Builder setRemoteCreateEntry(RemoteEntry remoteCreateEntry) {
            this.mRemoteCreateEntry = remoteCreateEntry;
            return this;
        }

        public BeginCreateCredentialResponse build() {
            return new BeginCreateCredentialResponse(this.mCreateEntries, this.mRemoteCreateEntry);
        }
    }
}
