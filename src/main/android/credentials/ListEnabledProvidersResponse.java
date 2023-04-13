package android.credentials;

import android.p008os.Parcel;
import android.p008os.Parcelable;
import com.android.internal.util.Preconditions;
import java.util.List;
import java.util.Objects;
/* loaded from: classes.dex */
public final class ListEnabledProvidersResponse implements Parcelable {
    public static final Parcelable.Creator<ListEnabledProvidersResponse> CREATOR = new Parcelable.Creator<ListEnabledProvidersResponse>() { // from class: android.credentials.ListEnabledProvidersResponse.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ListEnabledProvidersResponse createFromParcel(Parcel in) {
            return new ListEnabledProvidersResponse(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ListEnabledProvidersResponse[] newArray(int size) {
            return new ListEnabledProvidersResponse[size];
        }
    };
    private final List<String> mProviders;

    public static ListEnabledProvidersResponse create(List<String> providers) {
        Objects.requireNonNull(providers, "providers must not be null");
        Preconditions.checkCollectionElementsNotNull(providers, "providers");
        return new ListEnabledProvidersResponse(providers);
    }

    private ListEnabledProvidersResponse(List<String> providers) {
        this.mProviders = providers;
    }

    private ListEnabledProvidersResponse(Parcel in) {
        this.mProviders = in.createStringArrayList();
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringList(this.mProviders);
    }

    public List<String> getProviderComponentNames() {
        return this.mProviders;
    }
}
