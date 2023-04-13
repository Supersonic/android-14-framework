package android.content.integrity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
/* loaded from: classes.dex */
public final class AppInstallMetadata {
    private final Map<String, String> mAllowedInstallersAndCertificates;
    private final List<String> mAppCertificateLineage;
    private final List<String> mAppCertificates;
    private final List<String> mInstallerCertificates;
    private final String mInstallerName;
    private final boolean mIsPreInstalled;
    private final boolean mIsStampPresent;
    private final boolean mIsStampTrusted;
    private final boolean mIsStampVerified;
    private final String mPackageName;
    private final String mStampCertificateHash;
    private final long mVersionCode;

    private AppInstallMetadata(Builder builder) {
        this.mPackageName = builder.mPackageName;
        this.mAppCertificates = builder.mAppCertificates;
        this.mAppCertificateLineage = builder.mAppCertificateLineage;
        this.mInstallerName = builder.mInstallerName;
        this.mInstallerCertificates = builder.mInstallerCertificates;
        this.mVersionCode = builder.mVersionCode;
        this.mIsPreInstalled = builder.mIsPreInstalled;
        this.mIsStampPresent = builder.mIsStampPresent;
        this.mIsStampVerified = builder.mIsStampVerified;
        this.mIsStampTrusted = builder.mIsStampTrusted;
        this.mStampCertificateHash = builder.mStampCertificateHash;
        this.mAllowedInstallersAndCertificates = builder.mAllowedInstallersAndCertificates;
    }

    public String getPackageName() {
        return this.mPackageName;
    }

    public List<String> getAppCertificates() {
        return this.mAppCertificates;
    }

    public List<String> getAppCertificateLineage() {
        return this.mAppCertificateLineage;
    }

    public String getInstallerName() {
        return this.mInstallerName;
    }

    public List<String> getInstallerCertificates() {
        return this.mInstallerCertificates;
    }

    public long getVersionCode() {
        return this.mVersionCode;
    }

    public boolean isPreInstalled() {
        return this.mIsPreInstalled;
    }

    public boolean isStampPresent() {
        return this.mIsStampPresent;
    }

    public boolean isStampVerified() {
        return this.mIsStampVerified;
    }

    public boolean isStampTrusted() {
        return this.mIsStampTrusted;
    }

    public String getStampCertificateHash() {
        return this.mStampCertificateHash;
    }

    public Map<String, String> getAllowedInstallersAndCertificates() {
        return this.mAllowedInstallersAndCertificates;
    }

    public String toString() {
        Object[] objArr = new Object[11];
        objArr[0] = this.mPackageName;
        objArr[1] = this.mAppCertificates;
        objArr[2] = this.mAppCertificateLineage;
        String str = this.mInstallerName;
        if (str == null) {
            str = "null";
        }
        objArr[3] = str;
        List<String> list = this.mInstallerCertificates;
        if (list == null) {
            list = "null";
        }
        objArr[4] = list;
        objArr[5] = Long.valueOf(this.mVersionCode);
        objArr[6] = Boolean.valueOf(this.mIsPreInstalled);
        objArr[7] = Boolean.valueOf(this.mIsStampPresent);
        objArr[8] = Boolean.valueOf(this.mIsStampVerified);
        objArr[9] = Boolean.valueOf(this.mIsStampTrusted);
        String str2 = this.mStampCertificateHash;
        objArr[10] = str2 != null ? str2 : "null";
        return String.format("AppInstallMetadata { PackageName = %s, AppCerts = %s, AppCertsLineage = %s, InstallerName = %s, InstallerCerts = %s, VersionCode = %d, PreInstalled = %b, StampPresent = %b, StampVerified = %b, StampTrusted = %b, StampCert = %s }", objArr);
    }

    /* loaded from: classes.dex */
    public static final class Builder {
        private Map<String, String> mAllowedInstallersAndCertificates = new HashMap();
        private List<String> mAppCertificateLineage;
        private List<String> mAppCertificates;
        private List<String> mInstallerCertificates;
        private String mInstallerName;
        private boolean mIsPreInstalled;
        private boolean mIsStampPresent;
        private boolean mIsStampTrusted;
        private boolean mIsStampVerified;
        private String mPackageName;
        private String mStampCertificateHash;
        private long mVersionCode;

        public Builder setAllowedInstallersAndCert(Map<String, String> allowedInstallersAndCertificates) {
            this.mAllowedInstallersAndCertificates = allowedInstallersAndCertificates;
            return this;
        }

        public Builder setPackageName(String packageName) {
            this.mPackageName = (String) Objects.requireNonNull(packageName);
            return this;
        }

        public Builder setAppCertificates(List<String> appCertificates) {
            this.mAppCertificates = (List) Objects.requireNonNull(appCertificates);
            return this;
        }

        public Builder setAppCertificateLineage(List<String> appCertificateLineage) {
            this.mAppCertificateLineage = (List) Objects.requireNonNull(appCertificateLineage);
            return this;
        }

        public Builder setInstallerName(String installerName) {
            this.mInstallerName = (String) Objects.requireNonNull(installerName);
            return this;
        }

        public Builder setInstallerCertificates(List<String> installerCertificates) {
            this.mInstallerCertificates = (List) Objects.requireNonNull(installerCertificates);
            return this;
        }

        public Builder setVersionCode(long versionCode) {
            this.mVersionCode = versionCode;
            return this;
        }

        public Builder setIsPreInstalled(boolean isPreInstalled) {
            this.mIsPreInstalled = isPreInstalled;
            return this;
        }

        public Builder setIsStampPresent(boolean isStampPresent) {
            this.mIsStampPresent = isStampPresent;
            return this;
        }

        public Builder setIsStampVerified(boolean isStampVerified) {
            this.mIsStampVerified = isStampVerified;
            return this;
        }

        public Builder setIsStampTrusted(boolean isStampTrusted) {
            this.mIsStampTrusted = isStampTrusted;
            return this;
        }

        public Builder setStampCertificateHash(String stampCertificateHash) {
            this.mStampCertificateHash = (String) Objects.requireNonNull(stampCertificateHash);
            return this;
        }

        public AppInstallMetadata build() {
            Objects.requireNonNull(this.mPackageName);
            Objects.requireNonNull(this.mAppCertificates);
            Objects.requireNonNull(this.mAppCertificateLineage);
            return new AppInstallMetadata(this);
        }
    }
}
