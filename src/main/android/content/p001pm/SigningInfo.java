package android.content.p001pm;

import android.p008os.Parcel;
import android.p008os.Parcelable;
/* renamed from: android.content.pm.SigningInfo */
/* loaded from: classes.dex */
public final class SigningInfo implements Parcelable {
    public static final Parcelable.Creator<SigningInfo> CREATOR = new Parcelable.Creator<SigningInfo>() { // from class: android.content.pm.SigningInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public SigningInfo createFromParcel(Parcel source) {
            return new SigningInfo(source);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public SigningInfo[] newArray(int size) {
            return new SigningInfo[size];
        }
    };
    private final SigningDetails mSigningDetails;

    public SigningInfo() {
        this.mSigningDetails = SigningDetails.UNKNOWN;
    }

    public SigningInfo(SigningDetails signingDetails) {
        this.mSigningDetails = new SigningDetails(signingDetails);
    }

    public SigningInfo(SigningInfo orig) {
        this.mSigningDetails = new SigningDetails(orig.mSigningDetails);
    }

    private SigningInfo(Parcel source) {
        this.mSigningDetails = SigningDetails.CREATOR.createFromParcel(source);
    }

    public boolean hasMultipleSigners() {
        return this.mSigningDetails.getSignatures() != null && this.mSigningDetails.getSignatures().length > 1;
    }

    public boolean hasPastSigningCertificates() {
        return this.mSigningDetails.getPastSigningCertificates() != null && this.mSigningDetails.getPastSigningCertificates().length > 0;
    }

    public Signature[] getSigningCertificateHistory() {
        if (hasMultipleSigners()) {
            return null;
        }
        if (!hasPastSigningCertificates()) {
            return this.mSigningDetails.getSignatures();
        }
        return this.mSigningDetails.getPastSigningCertificates();
    }

    public Signature[] getApkContentsSigners() {
        return this.mSigningDetails.getSignatures();
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int parcelableFlags) {
        this.mSigningDetails.writeToParcel(dest, parcelableFlags);
    }
}
