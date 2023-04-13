package android.content.integrity;

import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.util.Map;
/* loaded from: classes.dex */
public class InstallerAllowedByManifestFormula extends IntegrityFormula implements Parcelable {
    public static final Parcelable.Creator<InstallerAllowedByManifestFormula> CREATOR = new Parcelable.Creator<InstallerAllowedByManifestFormula>() { // from class: android.content.integrity.InstallerAllowedByManifestFormula.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public InstallerAllowedByManifestFormula createFromParcel(Parcel in) {
            return new InstallerAllowedByManifestFormula(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public InstallerAllowedByManifestFormula[] newArray(int size) {
            return new InstallerAllowedByManifestFormula[size];
        }
    };
    public static final String INSTALLER_CERTIFICATE_NOT_EVALUATED = "";

    public InstallerAllowedByManifestFormula() {
    }

    private InstallerAllowedByManifestFormula(Parcel in) {
    }

    @Override // android.content.integrity.IntegrityFormula
    public int getTag() {
        return 4;
    }

    @Override // android.content.integrity.IntegrityFormula
    public boolean matches(AppInstallMetadata appInstallMetadata) {
        Map<String, String> allowedInstallersAndCertificates = appInstallMetadata.getAllowedInstallersAndCertificates();
        return allowedInstallersAndCertificates.isEmpty() || installerInAllowedInstallersFromManifest(appInstallMetadata, allowedInstallersAndCertificates);
    }

    @Override // android.content.integrity.IntegrityFormula
    public boolean isAppCertificateFormula() {
        return false;
    }

    @Override // android.content.integrity.IntegrityFormula
    public boolean isAppCertificateLineageFormula() {
        return false;
    }

    @Override // android.content.integrity.IntegrityFormula
    public boolean isInstallerFormula() {
        return true;
    }

    private static boolean installerInAllowedInstallersFromManifest(AppInstallMetadata appInstallMetadata, Map<String, String> allowedInstallersAndCertificates) {
        String installerPackage = appInstallMetadata.getInstallerName();
        if (!allowedInstallersAndCertificates.containsKey(installerPackage)) {
            return false;
        }
        if (!allowedInstallersAndCertificates.get(installerPackage).equals("")) {
            return appInstallMetadata.getInstallerCertificates().contains(allowedInstallersAndCertificates.get(appInstallMetadata.getInstallerName()));
        }
        return true;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
    }
}
