package android.content.integrity;

import android.annotation.SystemApi;
import android.content.integrity.AtomicFormula;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Arrays;
@SystemApi
/* loaded from: classes.dex */
public abstract class IntegrityFormula {
    public static final int BOOLEAN_ATOMIC_FORMULA_TAG = 3;
    public static final int COMPOUND_FORMULA_TAG = 0;
    public static final int INSTALLER_ALLOWED_BY_MANIFEST_FORMULA_TAG = 4;
    public static final int LONG_ATOMIC_FORMULA_TAG = 2;
    public static final int STRING_ATOMIC_FORMULA_TAG = 1;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    @interface Tag {
    }

    public abstract int getTag();

    public abstract boolean isAppCertificateFormula();

    public abstract boolean isAppCertificateLineageFormula();

    public abstract boolean isInstallerFormula();

    public abstract boolean matches(AppInstallMetadata appInstallMetadata);

    /* loaded from: classes.dex */
    public static final class Application {
        public static IntegrityFormula packageNameEquals(String packageName) {
            return new AtomicFormula.StringAtomicFormula(0, packageName);
        }

        public static IntegrityFormula certificatesContain(String appCertificate) {
            return new AtomicFormula.StringAtomicFormula(1, appCertificate);
        }

        public static IntegrityFormula certificateLineageContains(String appCertificate) {
            return new AtomicFormula.StringAtomicFormula(8, appCertificate);
        }

        public static IntegrityFormula versionCodeEquals(long versionCode) {
            return new AtomicFormula.LongAtomicFormula(4, 0, versionCode);
        }

        public static IntegrityFormula versionCodeGreaterThan(long versionCode) {
            return new AtomicFormula.LongAtomicFormula(4, 1, versionCode);
        }

        public static IntegrityFormula versionCodeGreaterThanOrEqualTo(long versionCode) {
            return new AtomicFormula.LongAtomicFormula(4, 2, versionCode);
        }

        public static IntegrityFormula isPreInstalled() {
            return new AtomicFormula.BooleanAtomicFormula(5, true);
        }

        private Application() {
        }
    }

    /* loaded from: classes.dex */
    public static final class Installer {
        public static IntegrityFormula packageNameEquals(String installerName) {
            return new AtomicFormula.StringAtomicFormula(2, installerName);
        }

        public static IntegrityFormula notAllowedByManifest() {
            return IntegrityFormula.not(new InstallerAllowedByManifestFormula());
        }

        public static IntegrityFormula certificatesContain(String installerCertificate) {
            return new AtomicFormula.StringAtomicFormula(3, installerCertificate);
        }

        private Installer() {
        }
    }

    /* loaded from: classes.dex */
    public static final class SourceStamp {
        public static IntegrityFormula stampCertificateHashEquals(String stampCertificateHash) {
            return new AtomicFormula.StringAtomicFormula(7, stampCertificateHash);
        }

        public static IntegrityFormula notTrusted() {
            return new AtomicFormula.BooleanAtomicFormula(6, false);
        }

        private SourceStamp() {
        }
    }

    public static void writeToParcel(IntegrityFormula formula, Parcel dest, int flags) {
        dest.writeInt(formula.getTag());
        ((Parcelable) formula).writeToParcel(dest, flags);
    }

    public static IntegrityFormula readFromParcel(Parcel in) {
        int tag = in.readInt();
        switch (tag) {
            case 0:
                return CompoundFormula.CREATOR.createFromParcel(in);
            case 1:
                return AtomicFormula.StringAtomicFormula.CREATOR.createFromParcel(in);
            case 2:
                return AtomicFormula.LongAtomicFormula.CREATOR.createFromParcel(in);
            case 3:
                return AtomicFormula.BooleanAtomicFormula.CREATOR.createFromParcel(in);
            case 4:
                return InstallerAllowedByManifestFormula.CREATOR.createFromParcel(in);
            default:
                throw new IllegalArgumentException("Unknown formula tag " + tag);
        }
    }

    public static IntegrityFormula any(IntegrityFormula... formulae) {
        return new CompoundFormula(1, Arrays.asList(formulae));
    }

    public static IntegrityFormula all(IntegrityFormula... formulae) {
        return new CompoundFormula(0, Arrays.asList(formulae));
    }

    public static IntegrityFormula not(IntegrityFormula formula) {
        return new CompoundFormula(2, Arrays.asList(formula));
    }
}
