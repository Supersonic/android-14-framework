package android.app.cloudsearch;

import android.annotation.SystemApi;
import android.p008os.Bundle;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
@SystemApi
/* loaded from: classes.dex */
public final class SearchResult implements Parcelable {
    public static final Parcelable.Creator<SearchResult> CREATOR = new Parcelable.Creator<SearchResult>() { // from class: android.app.cloudsearch.SearchResult.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public SearchResult createFromParcel(Parcel p) {
            return new SearchResult();
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public SearchResult[] newArray(int size) {
            return new SearchResult[size];
        }
    };
    public static final String EXTRAINFO_ACTION_APP_CARD = "android.app.cloudsearch.ACTION_APP_CARD";
    public static final String EXTRAINFO_ACTION_BUTTON_IMAGE_PREREGISTERING = "android.app.cloudsearch.ACTION_BUTTON_IMAGE";
    public static final String EXTRAINFO_ACTION_BUTTON_TEXT_PREREGISTERING = "android.app.cloudsearch.ACTION_BUTTON_TEXT";
    public static final String EXTRAINFO_ACTION_INSTALL_BUTTON = "android.app.cloudsearch.ACTION_INSTALL_BUTTON";
    public static final String EXTRAINFO_APP_BADGES = "android.app.cloudsearch.APP_BADGES";
    public static final String EXTRAINFO_APP_CONTAINS_ADS_DISCLAIMER = "android.app.cloudsearch.APP_CONTAINS_ADS_DISCLAIMER";
    public static final String EXTRAINFO_APP_CONTAINS_IAP_DISCLAIMER = "android.app.cloudsearch.APP_CONTAINS_IAP_DISCLAIMER";
    public static final String EXTRAINFO_APP_DEVELOPER_NAME = "android.app.cloudsearch.APP_DEVELOPER_NAME";
    public static final String EXTRAINFO_APP_DOMAIN_URL = "android.app.cloudsearch.APP_DOMAIN_URL";
    public static final String EXTRAINFO_APP_IARC = "android.app.cloudsearch.APP_IARC";
    public static final String EXTRAINFO_APP_ICON = "android.app.cloudsearch.APP_ICON";
    public static final String EXTRAINFO_APP_INSTALL_COUNT = "android.app.cloudsearch.APP_INSTALL_COUNT";
    public static final String EXTRAINFO_APP_PACKAGE_NAME = "android.app.cloudsearch.APP_PACKAGE_NAME";
    public static final String EXTRAINFO_APP_REVIEW_COUNT = "android.app.cloudsearch.APP_REVIEW_COUNT";
    public static final String EXTRAINFO_APP_SIZE_BYTES = "android.app.cloudsearch.APP_SIZE_BYTES";
    public static final String EXTRAINFO_APP_STAR_RATING = "android.app.cloudsearch.APP_STAR_RATING";
    public static final String EXTRAINFO_LONG_DESCRIPTION = "android.app.cloudsearch.LONG_DESCRIPTION";
    public static final String EXTRAINFO_SCREENSHOTS = "android.app.cloudsearch.SCREENSHOTS";
    public static final String EXTRAINFO_SHORT_DESCRIPTION = "android.app.cloudsearch.SHORT_DESCRIPTION";
    public static final String EXTRAINFO_WEB_ICON = "android.app.cloudsearch.WEB_ICON";
    public static final String EXTRAINFO_WEB_URL = "android.app.cloudsearch.WEB_URL";

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface SearchResultExtraInfoKey {
    }

    private SearchResult() {
    }

    public String getTitle() {
        return "";
    }

    public String getSnippet() {
        return "";
    }

    public float getScore() {
        return 0.0f;
    }

    public Bundle getExtraInfos() {
        return Bundle.EMPTY;
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

    public int hashCode() {
        return 0;
    }

    @SystemApi
    /* loaded from: classes.dex */
    public static final class Builder {
        @SystemApi
        public Builder(String title, Bundle extraInfos) {
        }

        public Builder setTitle(String title) {
            return this;
        }

        public Builder setSnippet(String snippet) {
            return this;
        }

        public Builder setScore(float score) {
            return this;
        }

        public Builder setExtraInfos(Bundle extraInfos) {
            return this;
        }

        public SearchResult build() {
            return new SearchResult();
        }
    }
}
