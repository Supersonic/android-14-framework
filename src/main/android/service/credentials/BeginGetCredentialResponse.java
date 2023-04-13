package android.service.credentials;

import android.app.slice.Slice;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import com.android.internal.util.Preconditions;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
/* loaded from: classes3.dex */
public final class BeginGetCredentialResponse implements Parcelable {
    public static final Parcelable.Creator<BeginGetCredentialResponse> CREATOR = new Parcelable.Creator<BeginGetCredentialResponse>() { // from class: android.service.credentials.BeginGetCredentialResponse.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public BeginGetCredentialResponse createFromParcel(Parcel in) {
            return new BeginGetCredentialResponse(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public BeginGetCredentialResponse[] newArray(int size) {
            return new BeginGetCredentialResponse[size];
        }
    };
    private final List<Action> mActions;
    private final List<Action> mAuthenticationEntries;
    private final List<CredentialEntry> mCredentialEntries;
    private final RemoteEntry mRemoteCredentialEntry;

    public BeginGetCredentialResponse() {
        this(new ArrayList(), new ArrayList(), new ArrayList(), null);
    }

    private BeginGetCredentialResponse(List<CredentialEntry> credentialEntries, List<Action> authenticationEntries, List<Action> actions, RemoteEntry remoteCredentialEntry) {
        this.mCredentialEntries = new ArrayList(credentialEntries);
        this.mAuthenticationEntries = new ArrayList(authenticationEntries);
        this.mActions = new ArrayList(actions);
        this.mRemoteCredentialEntry = remoteCredentialEntry;
    }

    private BeginGetCredentialResponse(Parcel in) {
        ArrayList arrayList = new ArrayList();
        in.readTypedList(arrayList, CredentialEntry.CREATOR);
        this.mCredentialEntries = arrayList;
        ArrayList arrayList2 = new ArrayList();
        in.readTypedList(arrayList2, Action.CREATOR);
        this.mAuthenticationEntries = arrayList2;
        ArrayList arrayList3 = new ArrayList();
        in.readTypedList(arrayList3, Action.CREATOR);
        this.mActions = arrayList3;
        this.mRemoteCredentialEntry = (RemoteEntry) in.readTypedObject(RemoteEntry.CREATOR);
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(this.mCredentialEntries, flags);
        dest.writeTypedList(this.mAuthenticationEntries, flags);
        dest.writeTypedList(this.mActions, flags);
        dest.writeTypedObject(this.mRemoteCredentialEntry, flags);
    }

    public List<CredentialEntry> getCredentialEntries() {
        return this.mCredentialEntries;
    }

    public List<Action> getAuthenticationActions() {
        return this.mAuthenticationEntries;
    }

    public List<Action> getActions() {
        return this.mActions;
    }

    public RemoteEntry getRemoteCredentialEntry() {
        return this.mRemoteCredentialEntry;
    }

    /* loaded from: classes3.dex */
    public static final class Builder {
        private RemoteEntry mRemoteCredentialEntry;
        private List<CredentialEntry> mCredentialEntries = new ArrayList();
        private List<Action> mAuthenticationEntries = new ArrayList();
        private List<Action> mActions = new ArrayList();

        public Builder setRemoteCredentialEntry(RemoteEntry remoteCredentialEntry) {
            this.mRemoteCredentialEntry = remoteCredentialEntry;
            return this;
        }

        public Builder addCredentialEntry(CredentialEntry credentialEntry) {
            this.mCredentialEntries.add((CredentialEntry) Objects.requireNonNull(credentialEntry));
            return this;
        }

        public Builder addAuthenticationAction(Action authenticationAction) {
            this.mAuthenticationEntries.add((Action) Objects.requireNonNull(authenticationAction));
            return this;
        }

        public Builder addAction(Action action) {
            this.mActions.add((Action) Objects.requireNonNull(action, "action must not be null"));
            return this;
        }

        public Builder setActions(List<Action> actions) {
            this.mActions = (List) Preconditions.checkCollectionElementsNotNull(actions, Slice.HINT_ACTIONS);
            return this;
        }

        public Builder setCredentialEntries(List<CredentialEntry> credentialEntries) {
            this.mCredentialEntries = (List) Preconditions.checkCollectionElementsNotNull(credentialEntries, "credentialEntries");
            return this;
        }

        public Builder setAuthenticationActions(List<Action> authenticationActions) {
            this.mAuthenticationEntries = (List) Preconditions.checkCollectionElementsNotNull(authenticationActions, "authenticationActions");
            return this;
        }

        public BeginGetCredentialResponse build() {
            return new BeginGetCredentialResponse(this.mCredentialEntries, this.mAuthenticationEntries, this.mActions, this.mRemoteCredentialEntry);
        }
    }
}
