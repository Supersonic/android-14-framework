package android.app.search;

import android.annotation.SystemApi;
import android.p008os.Bundle;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.util.Objects;
@SystemApi
/* loaded from: classes.dex */
public final class SearchContext implements Parcelable {
    public static final Parcelable.Creator<SearchContext> CREATOR = new Parcelable.Creator<SearchContext>() { // from class: android.app.search.SearchContext.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public SearchContext createFromParcel(Parcel parcel) {
            return new SearchContext(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public SearchContext[] newArray(int size) {
            return new SearchContext[size];
        }
    };
    private final Bundle mExtras;
    private String mPackageName;
    private final int mResultTypes;
    private final int mTimeoutMillis;

    public SearchContext(int resultTypes, int timeoutMillis) {
        this(resultTypes, timeoutMillis, new Bundle());
    }

    public SearchContext(int resultTypes, int timeoutMillis, Bundle extras) {
        this.mResultTypes = resultTypes;
        this.mTimeoutMillis = timeoutMillis;
        this.mExtras = (Bundle) Objects.requireNonNull(extras);
    }

    private SearchContext(Parcel parcel) {
        this.mResultTypes = parcel.readInt();
        this.mTimeoutMillis = parcel.readInt();
        this.mPackageName = parcel.readString();
        this.mExtras = parcel.readBundle();
    }

    public String getPackageName() {
        return this.mPackageName;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setPackageName(String packageName) {
        this.mPackageName = packageName;
    }

    public int getTimeoutMillis() {
        return this.mTimeoutMillis;
    }

    public Bundle getExtras() {
        return this.mExtras;
    }

    public int getResultTypes() {
        return this.mResultTypes;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mResultTypes);
        dest.writeInt(this.mTimeoutMillis);
        dest.writeString(this.mPackageName);
        dest.writeBundle(this.mExtras);
    }
}
