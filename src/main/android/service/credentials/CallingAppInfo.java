package android.service.credentials;

import android.content.p001pm.SigningInfo;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import com.android.internal.util.Preconditions;
import java.util.Objects;
/* loaded from: classes3.dex */
public final class CallingAppInfo implements Parcelable {
    public static final Parcelable.Creator<CallingAppInfo> CREATOR = new Parcelable.Creator<CallingAppInfo>() { // from class: android.service.credentials.CallingAppInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public CallingAppInfo createFromParcel(Parcel in) {
            return new CallingAppInfo(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public CallingAppInfo[] newArray(int size) {
            return new CallingAppInfo[size];
        }
    };
    private final String mOrigin;
    private final String mPackageName;
    private final SigningInfo mSigningInfo;

    public CallingAppInfo(String packageName, SigningInfo signingInfo) {
        this(packageName, signingInfo, null);
    }

    public CallingAppInfo(String packageName, SigningInfo signingInfo, String origin) {
        this.mPackageName = (String) Preconditions.checkStringNotEmpty(packageName, "package namemust not be null or empty");
        this.mSigningInfo = (SigningInfo) Objects.requireNonNull(signingInfo);
        this.mOrigin = origin;
    }

    private CallingAppInfo(Parcel in) {
        this.mPackageName = in.readString8();
        this.mSigningInfo = (SigningInfo) in.readTypedObject(SigningInfo.CREATOR);
        this.mOrigin = in.readString8();
    }

    public String getPackageName() {
        return this.mPackageName;
    }

    public SigningInfo getSigningInfo() {
        return this.mSigningInfo;
    }

    public String getOrigin() {
        return this.mOrigin;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString8(this.mPackageName);
        dest.writeTypedObject(this.mSigningInfo, flags);
        dest.writeString8(this.mOrigin);
    }

    public String toString() {
        StringBuilder builder = new StringBuilder("CallingAppInfo {packageName= " + this.mPackageName);
        if (this.mSigningInfo != null) {
            builder.append(", mSigningInfo : No. of signatures: " + this.mSigningInfo.getApkContentsSigners().length);
        } else {
            builder.append(", mSigningInfo: null");
        }
        builder.append(",mOrigin: " + this.mOrigin);
        builder.append(" }");
        return builder.toString();
    }
}
