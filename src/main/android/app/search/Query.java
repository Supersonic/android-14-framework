package android.app.search;

import android.annotation.SystemApi;
import android.p008os.Bundle;
import android.p008os.Parcel;
import android.p008os.Parcelable;
@SystemApi
/* loaded from: classes.dex */
public final class Query implements Parcelable {
    public static final Parcelable.Creator<Query> CREATOR = new Parcelable.Creator<Query>() { // from class: android.app.search.Query.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public Query createFromParcel(Parcel parcel) {
            return new Query(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public Query[] newArray(int size) {
            return new Query[size];
        }
    };
    public static final String EXTRA_IME_HEIGHT = "android.app.search.extra.IME_HEIGHT";
    private final Bundle mExtras;
    private final String mInput;
    private final long mTimestampMillis;

    public Query(String input, long timestampMillis, Bundle extras) {
        this.mInput = input;
        this.mTimestampMillis = timestampMillis;
        this.mExtras = extras != null ? extras : new Bundle();
    }

    public Query(String input, long timestampMillis) {
        this(input, timestampMillis, new Bundle());
    }

    private Query(Parcel parcel) {
        this.mInput = parcel.readString();
        this.mTimestampMillis = parcel.readLong();
        this.mExtras = parcel.readBundle();
    }

    public String getInput() {
        return this.mInput;
    }

    @Deprecated
    public long getTimestamp() {
        return this.mTimestampMillis;
    }

    public long getTimestampMillis() {
        return this.mTimestampMillis;
    }

    public Bundle getExtras() {
        Bundle bundle = this.mExtras;
        if (bundle == null) {
            return new Bundle();
        }
        return bundle;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mInput);
        dest.writeLong(this.mTimestampMillis);
        dest.writeBundle(this.mExtras);
    }
}
