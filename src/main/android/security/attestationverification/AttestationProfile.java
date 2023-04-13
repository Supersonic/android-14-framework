package android.security.attestationverification;

import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.security.attestationverification.AttestationVerificationManager;
import android.service.timezone.TimeZoneProviderService;
import android.util.Log;
import com.android.internal.util.AnnotationValidations;
import java.lang.annotation.Annotation;
import java.util.Objects;
/* loaded from: classes3.dex */
public final class AttestationProfile implements Parcelable {
    public static final Parcelable.Creator<AttestationProfile> CREATOR = new Parcelable.Creator<AttestationProfile>() { // from class: android.security.attestationverification.AttestationProfile.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public AttestationProfile[] newArray(int size) {
            return new AttestationProfile[size];
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public AttestationProfile createFromParcel(Parcel in) {
            return new AttestationProfile(in);
        }
    };
    private static final String TAG = "AVF";
    private final int mAttestationProfileId;
    private final String mPackageName;
    private final String mProfileName;

    private AttestationProfile(int attestationProfileId, String packageName, String profileName) {
        this.mAttestationProfileId = attestationProfileId;
        this.mPackageName = packageName;
        this.mProfileName = profileName;
    }

    public AttestationProfile(int attestationProfileId) {
        this(attestationProfileId, null, null);
        if (attestationProfileId == 1) {
            throw new IllegalArgumentException("App-defined profiles must be specified with the constructor AttestationProfile#constructor(String, String)");
        }
    }

    public AttestationProfile(String packageName, String profileName) {
        this(1, packageName, profileName);
        if (packageName == null || profileName == null) {
            throw new IllegalArgumentException("Both packageName and profileName must be non-null");
        }
    }

    public String toString() {
        String humanReadableProfileId;
        int i = this.mAttestationProfileId;
        if (i == 1) {
            return "AttestationProfile(package=" + this.mPackageName + ", name=" + this.mProfileName + NavigationBarInflaterView.KEY_CODE_END;
        }
        switch (i) {
            case 0:
                humanReadableProfileId = "PROFILE_UNKNOWN";
                break;
            default:
                Log.m110e(TAG, "ERROR: Missing case in AttestationProfile#toString");
                humanReadableProfileId = TimeZoneProviderService.TEST_COMMAND_RESULT_ERROR_KEY;
                break;
        }
        return "AttestationProfile(" + humanReadableProfileId + "/" + this.mAttestationProfileId + NavigationBarInflaterView.KEY_CODE_END;
    }

    public int getAttestationProfileId() {
        return this.mAttestationProfileId;
    }

    public String getPackageName() {
        return this.mPackageName;
    }

    public String getProfileName() {
        return this.mProfileName;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        AttestationProfile that = (AttestationProfile) o;
        if (this.mAttestationProfileId == that.mAttestationProfileId && Objects.equals(this.mPackageName, that.mPackageName) && Objects.equals(this.mProfileName, that.mProfileName)) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        int _hash = (1 * 31) + this.mAttestationProfileId;
        return (((_hash * 31) + Objects.hashCode(this.mPackageName)) * 31) + Objects.hashCode(this.mProfileName);
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        byte flg = this.mPackageName != null ? (byte) (0 | 2) : (byte) 0;
        if (this.mProfileName != null) {
            flg = (byte) (flg | 4);
        }
        dest.writeByte(flg);
        dest.writeInt(this.mAttestationProfileId);
        String str = this.mPackageName;
        if (str != null) {
            dest.writeString(str);
        }
        String str2 = this.mProfileName;
        if (str2 != null) {
            dest.writeString(str2);
        }
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    AttestationProfile(Parcel in) {
        byte flg = in.readByte();
        int attestationProfileId = in.readInt();
        String packageName = (flg & 2) == 0 ? null : in.readString();
        String profileName = (flg & 4) == 0 ? null : in.readString();
        this.mAttestationProfileId = attestationProfileId;
        AnnotationValidations.validate((Class<? extends Annotation>) AttestationVerificationManager.AttestationProfileId.class, (Annotation) null, attestationProfileId);
        this.mPackageName = packageName;
        this.mProfileName = profileName;
    }

    @Deprecated
    private void __metadata() {
    }
}
