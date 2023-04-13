package android.app.cloudsearch;

import android.annotation.SystemApi;
import android.p008os.Bundle;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
@SystemApi
/* loaded from: classes.dex */
public final class SearchRequest implements Parcelable {
    public static final String CONSTRAINT_IS_PRESUBMIT_SUGGESTION = "android.app.cloudsearch.IS_PRESUBMIT_SUGGESTION";
    public static final String CONSTRAINT_SEARCH_PROVIDER_FILTER = "android.app.cloudsearch.SEARCH_PROVIDER_FILTER";
    public static final Parcelable.Creator<SearchRequest> CREATOR = new Parcelable.Creator<SearchRequest>() { // from class: android.app.cloudsearch.SearchRequest.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public SearchRequest createFromParcel(Parcel p) {
            return new SearchRequest();
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public SearchRequest[] newArray(int size) {
            return new SearchRequest[size];
        }
    };

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface SearchConstraintKey {
    }

    private SearchRequest() {
    }

    public String getQuery() {
        return "";
    }

    public int getResultOffset() {
        return 0;
    }

    public int getResultNumber() {
        return 0;
    }

    public float getMaxLatencyMillis() {
        return 0.0f;
    }

    public Bundle getSearchConstraints() {
        return Bundle.EMPTY;
    }

    public String getCallerPackageName() {
        return "";
    }

    public String getRequestId() {
        return "";
    }

    public void setCallerPackageName(String callerPackageName) {
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    public boolean equals(Object obj) {
        return false;
    }

    public String toString() {
        return "";
    }

    public int hashCode() {
        return 0;
    }

    @SystemApi
    /* loaded from: classes.dex */
    public static final class Builder {
        @SystemApi
        public Builder(String query) {
        }

        public Builder setQuery(String query) {
            return this;
        }

        public Builder setResultOffset(int resultOffset) {
            return this;
        }

        public Builder setResultNumber(int resultNumber) {
            return this;
        }

        public Builder setMaxLatencyMillis(float maxLatencyMillis) {
            return this;
        }

        public Builder setSearchConstraints(Bundle searchConstraints) {
            return this;
        }

        public Builder setCallerPackageName(String callerPackageName) {
            return this;
        }

        public SearchRequest build() {
            return new SearchRequest();
        }
    }
}
