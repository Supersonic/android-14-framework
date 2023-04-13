package android.location;

import android.content.Context;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.p008os.Process;
import java.util.Locale;
import java.util.Objects;
/* loaded from: classes2.dex */
public class GeocoderParams implements Parcelable {
    public static final Parcelable.Creator<GeocoderParams> CREATOR = new Parcelable.Creator<GeocoderParams>() { // from class: android.location.GeocoderParams.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public GeocoderParams createFromParcel(Parcel in) {
            int uid = in.readInt();
            String packageName = in.readString8();
            String attributionTag = in.readString8();
            String language = in.readString8();
            String country = in.readString8();
            String variant = in.readString8();
            return new GeocoderParams(uid, packageName, attributionTag, new Locale(language, country, variant));
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public GeocoderParams[] newArray(int size) {
            return new GeocoderParams[size];
        }
    };
    private final String mAttributionTag;
    private final Locale mLocale;
    private final String mPackageName;
    private final int mUid;

    public GeocoderParams(Context context) {
        this(context, Locale.getDefault());
    }

    public GeocoderParams(Context context, Locale locale) {
        this(Process.myUid(), context.getPackageName(), context.getAttributionTag(), locale);
    }

    private GeocoderParams(int uid, String packageName, String attributionTag, Locale locale) {
        this.mUid = uid;
        this.mPackageName = (String) Objects.requireNonNull(packageName);
        this.mAttributionTag = attributionTag;
        this.mLocale = (Locale) Objects.requireNonNull(locale);
    }

    public int getClientUid() {
        return this.mUid;
    }

    public String getClientPackage() {
        return this.mPackageName;
    }

    public String getClientAttributionTag() {
        return this.mAttributionTag;
    }

    public Locale getLocale() {
        return this.mLocale;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeInt(this.mUid);
        parcel.writeString8(this.mPackageName);
        parcel.writeString8(this.mAttributionTag);
        parcel.writeString8(this.mLocale.getLanguage());
        parcel.writeString8(this.mLocale.getCountry());
        parcel.writeString8(this.mLocale.getVariant());
    }
}
