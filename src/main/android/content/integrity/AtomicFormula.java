package android.content.integrity;

import android.p008os.Parcel;
import android.p008os.Parcelable;
import com.android.internal.util.Preconditions;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
/* loaded from: classes.dex */
public abstract class AtomicFormula extends IntegrityFormula {
    public static final int APP_CERTIFICATE = 1;
    public static final int APP_CERTIFICATE_LINEAGE = 8;

    /* renamed from: EQ */
    public static final int f44EQ = 0;

    /* renamed from: GT */
    public static final int f45GT = 1;
    public static final int GTE = 2;
    public static final int INSTALLER_CERTIFICATE = 3;
    public static final int INSTALLER_NAME = 2;
    public static final int PACKAGE_NAME = 0;
    public static final int PRE_INSTALLED = 5;
    public static final int STAMP_CERTIFICATE_HASH = 7;
    public static final int STAMP_TRUSTED = 6;
    public static final int VERSION_CODE = 4;
    private final int mKey;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface Key {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface Operator {
    }

    public AtomicFormula(int key) {
        Preconditions.checkArgument(isValidKey(key), "Unknown key: %d", Integer.valueOf(key));
        this.mKey = key;
    }

    /* loaded from: classes.dex */
    public static final class LongAtomicFormula extends AtomicFormula implements Parcelable {
        public static final Parcelable.Creator<LongAtomicFormula> CREATOR = new Parcelable.Creator<LongAtomicFormula>() { // from class: android.content.integrity.AtomicFormula.LongAtomicFormula.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public LongAtomicFormula createFromParcel(Parcel in) {
                return new LongAtomicFormula(in);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public LongAtomicFormula[] newArray(int size) {
                return new LongAtomicFormula[size];
            }
        };
        private final Integer mOperator;
        private final Long mValue;

        public LongAtomicFormula(int key) {
            super(key);
            Preconditions.checkArgument(key == 4, "Key %s cannot be used with LongAtomicFormula", keyToString(key));
            this.mValue = null;
            this.mOperator = null;
        }

        public LongAtomicFormula(int key, int operator, long value) {
            super(key);
            Preconditions.checkArgument(key == 4, "Key %s cannot be used with LongAtomicFormula", keyToString(key));
            Preconditions.checkArgument(isValidOperator(operator), "Unknown operator: %d", Integer.valueOf(operator));
            this.mOperator = Integer.valueOf(operator);
            this.mValue = Long.valueOf(value);
        }

        LongAtomicFormula(Parcel in) {
            super(in.readInt());
            this.mValue = Long.valueOf(in.readLong());
            this.mOperator = Integer.valueOf(in.readInt());
        }

        @Override // android.content.integrity.IntegrityFormula
        public int getTag() {
            return 2;
        }

        @Override // android.content.integrity.IntegrityFormula
        public boolean matches(AppInstallMetadata appInstallMetadata) {
            if (this.mValue == null || this.mOperator == null) {
                return false;
            }
            long metadataValue = getLongMetadataValue(appInstallMetadata, getKey());
            switch (this.mOperator.intValue()) {
                case 0:
                    return metadataValue == this.mValue.longValue();
                case 1:
                    return metadataValue > this.mValue.longValue();
                case 2:
                    return metadataValue >= this.mValue.longValue();
                default:
                    throw new IllegalArgumentException(String.format("Unexpected operator %d", this.mOperator));
            }
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
            return false;
        }

        public String toString() {
            if (this.mValue == null || this.mOperator == null) {
                return String.format("(%s)", keyToString(getKey()));
            }
            return String.format("(%s %s %s)", keyToString(getKey()), operatorToString(this.mOperator.intValue()), this.mValue);
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            LongAtomicFormula that = (LongAtomicFormula) o;
            if (getKey() == that.getKey() && Objects.equals(this.mValue, that.mValue) && Objects.equals(this.mOperator, that.mOperator)) {
                return true;
            }
            return false;
        }

        public int hashCode() {
            return Objects.hash(Integer.valueOf(getKey()), this.mOperator, this.mValue);
        }

        @Override // android.p008os.Parcelable
        public int describeContents() {
            return 0;
        }

        @Override // android.p008os.Parcelable
        public void writeToParcel(Parcel dest, int flags) {
            if (this.mValue == null || this.mOperator == null) {
                throw new IllegalStateException("Cannot write an empty LongAtomicFormula.");
            }
            dest.writeInt(getKey());
            dest.writeLong(this.mValue.longValue());
            dest.writeInt(this.mOperator.intValue());
        }

        public Long getValue() {
            return this.mValue;
        }

        public Integer getOperator() {
            return this.mOperator;
        }

        private static boolean isValidOperator(int operator) {
            return operator == 0 || operator == 1 || operator == 2;
        }

        private static long getLongMetadataValue(AppInstallMetadata appInstallMetadata, int key) {
            switch (key) {
                case 4:
                    return appInstallMetadata.getVersionCode();
                default:
                    throw new IllegalStateException("Unexpected key in IntAtomicFormula" + key);
            }
        }
    }

    /* loaded from: classes.dex */
    public static final class StringAtomicFormula extends AtomicFormula implements Parcelable {
        public static final Parcelable.Creator<StringAtomicFormula> CREATOR = new Parcelable.Creator<StringAtomicFormula>() { // from class: android.content.integrity.AtomicFormula.StringAtomicFormula.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public StringAtomicFormula createFromParcel(Parcel in) {
                return new StringAtomicFormula(in);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public StringAtomicFormula[] newArray(int size) {
                return new StringAtomicFormula[size];
            }
        };
        private final Boolean mIsHashedValue;
        private final String mValue;

        public StringAtomicFormula(int key) {
            super(key);
            boolean z = true;
            if (key != 0 && key != 1 && key != 3 && key != 2 && key != 7 && key != 8) {
                z = false;
            }
            Preconditions.checkArgument(z, "Key %s cannot be used with StringAtomicFormula", keyToString(key));
            this.mValue = null;
            this.mIsHashedValue = null;
        }

        public StringAtomicFormula(int key, String value, boolean isHashed) {
            super(key);
            boolean z = true;
            if (key != 0 && key != 1 && key != 3 && key != 2 && key != 7 && key != 8) {
                z = false;
            }
            Preconditions.checkArgument(z, "Key %s cannot be used with StringAtomicFormula", keyToString(key));
            this.mValue = value;
            this.mIsHashedValue = Boolean.valueOf(isHashed);
        }

        public StringAtomicFormula(int key, String value) {
            super(key);
            boolean z = false;
            Preconditions.checkArgument(key == 0 || key == 1 || key == 3 || key == 2 || key == 7 || key == 8, "Key %s cannot be used with StringAtomicFormula", keyToString(key));
            String hashValue = hashValue(key, value);
            this.mValue = hashValue;
            this.mIsHashedValue = Boolean.valueOf((key == 1 || key == 3 || key == 7 || key == 8 || !hashValue.equals(value)) ? true : z);
        }

        StringAtomicFormula(Parcel in) {
            super(in.readInt());
            this.mValue = in.readStringNoHelper();
            this.mIsHashedValue = Boolean.valueOf(in.readByte() != 0);
        }

        @Override // android.content.integrity.IntegrityFormula
        public int getTag() {
            return 1;
        }

        @Override // android.content.integrity.IntegrityFormula
        public boolean matches(AppInstallMetadata appInstallMetadata) {
            if (this.mValue == null || this.mIsHashedValue == null) {
                return false;
            }
            return getMetadataValue(appInstallMetadata, getKey()).contains(this.mValue);
        }

        @Override // android.content.integrity.IntegrityFormula
        public boolean isAppCertificateFormula() {
            return getKey() == 1;
        }

        @Override // android.content.integrity.IntegrityFormula
        public boolean isAppCertificateLineageFormula() {
            return getKey() == 8;
        }

        @Override // android.content.integrity.IntegrityFormula
        public boolean isInstallerFormula() {
            return getKey() == 2 || getKey() == 3;
        }

        public String toString() {
            if (this.mValue == null || this.mIsHashedValue == null) {
                return String.format("(%s)", keyToString(getKey()));
            }
            return String.format("(%s %s %s)", keyToString(getKey()), operatorToString(0), this.mValue);
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            StringAtomicFormula that = (StringAtomicFormula) o;
            if (getKey() == that.getKey() && Objects.equals(this.mValue, that.mValue)) {
                return true;
            }
            return false;
        }

        public int hashCode() {
            return Objects.hash(Integer.valueOf(getKey()), this.mValue);
        }

        @Override // android.p008os.Parcelable
        public int describeContents() {
            return 0;
        }

        @Override // android.p008os.Parcelable
        public void writeToParcel(Parcel dest, int flags) {
            if (this.mValue == null || this.mIsHashedValue == null) {
                throw new IllegalStateException("Cannot write an empty StringAtomicFormula.");
            }
            dest.writeInt(getKey());
            dest.writeStringNoHelper(this.mValue);
            dest.writeByte(this.mIsHashedValue.booleanValue() ? (byte) 1 : (byte) 0);
        }

        public String getValue() {
            return this.mValue;
        }

        public Boolean getIsHashedValue() {
            return this.mIsHashedValue;
        }

        private static List<String> getMetadataValue(AppInstallMetadata appInstallMetadata, int key) {
            switch (key) {
                case 0:
                    return Collections.singletonList(appInstallMetadata.getPackageName());
                case 1:
                    return appInstallMetadata.getAppCertificates();
                case 2:
                    return Collections.singletonList(appInstallMetadata.getInstallerName());
                case 3:
                    return appInstallMetadata.getInstallerCertificates();
                case 4:
                case 5:
                case 6:
                default:
                    throw new IllegalStateException("Unexpected key in StringAtomicFormula: " + key);
                case 7:
                    return Collections.singletonList(appInstallMetadata.getStampCertificateHash());
                case 8:
                    return appInstallMetadata.getAppCertificateLineage();
            }
        }

        private static String hashValue(int key, String value) {
            if (value.length() > 32 && (key == 0 || key == 2)) {
                return hash(value);
            }
            return value;
        }

        private static String hash(String value) {
            try {
                MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
                byte[] hashBytes = messageDigest.digest(value.getBytes(StandardCharsets.UTF_8));
                return IntegrityUtils.getHexDigest(hashBytes);
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException("SHA-256 algorithm not found", e);
            }
        }
    }

    /* loaded from: classes.dex */
    public static final class BooleanAtomicFormula extends AtomicFormula implements Parcelable {
        public static final Parcelable.Creator<BooleanAtomicFormula> CREATOR = new Parcelable.Creator<BooleanAtomicFormula>() { // from class: android.content.integrity.AtomicFormula.BooleanAtomicFormula.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public BooleanAtomicFormula createFromParcel(Parcel in) {
                return new BooleanAtomicFormula(in);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public BooleanAtomicFormula[] newArray(int size) {
                return new BooleanAtomicFormula[size];
            }
        };
        private final Boolean mValue;

        public BooleanAtomicFormula(int key) {
            super(key);
            Preconditions.checkArgument(key == 5 || key == 6, String.format("Key %s cannot be used with BooleanAtomicFormula", keyToString(key)));
            this.mValue = null;
        }

        public BooleanAtomicFormula(int key, boolean value) {
            super(key);
            Preconditions.checkArgument(key == 5 || key == 6, String.format("Key %s cannot be used with BooleanAtomicFormula", keyToString(key)));
            this.mValue = Boolean.valueOf(value);
        }

        BooleanAtomicFormula(Parcel in) {
            super(in.readInt());
            this.mValue = Boolean.valueOf(in.readByte() != 0);
        }

        @Override // android.content.integrity.IntegrityFormula
        public int getTag() {
            return 3;
        }

        @Override // android.content.integrity.IntegrityFormula
        public boolean matches(AppInstallMetadata appInstallMetadata) {
            return this.mValue != null && getBooleanMetadataValue(appInstallMetadata, getKey()) == this.mValue.booleanValue();
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
            return false;
        }

        public String toString() {
            if (this.mValue == null) {
                return String.format("(%s)", keyToString(getKey()));
            }
            return String.format("(%s %s %s)", keyToString(getKey()), operatorToString(0), this.mValue);
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            BooleanAtomicFormula that = (BooleanAtomicFormula) o;
            if (getKey() == that.getKey() && Objects.equals(this.mValue, that.mValue)) {
                return true;
            }
            return false;
        }

        public int hashCode() {
            return Objects.hash(Integer.valueOf(getKey()), this.mValue);
        }

        @Override // android.p008os.Parcelable
        public int describeContents() {
            return 0;
        }

        @Override // android.p008os.Parcelable
        public void writeToParcel(Parcel dest, int flags) {
            if (this.mValue == null) {
                throw new IllegalStateException("Cannot write an empty BooleanAtomicFormula.");
            }
            dest.writeInt(getKey());
            dest.writeByte(this.mValue.booleanValue() ? (byte) 1 : (byte) 0);
        }

        public Boolean getValue() {
            return this.mValue;
        }

        private static boolean getBooleanMetadataValue(AppInstallMetadata appInstallMetadata, int key) {
            switch (key) {
                case 5:
                    return appInstallMetadata.isPreInstalled();
                case 6:
                    return appInstallMetadata.isStampTrusted();
                default:
                    throw new IllegalStateException("Unexpected key in BooleanAtomicFormula: " + key);
            }
        }
    }

    public int getKey() {
        return this.mKey;
    }

    static String keyToString(int key) {
        switch (key) {
            case 0:
                return "PACKAGE_NAME";
            case 1:
                return "APP_CERTIFICATE";
            case 2:
                return "INSTALLER_NAME";
            case 3:
                return "INSTALLER_CERTIFICATE";
            case 4:
                return "VERSION_CODE";
            case 5:
                return "PRE_INSTALLED";
            case 6:
                return "STAMP_TRUSTED";
            case 7:
                return "STAMP_CERTIFICATE_HASH";
            case 8:
                return "APP_CERTIFICATE_LINEAGE";
            default:
                throw new IllegalArgumentException("Unknown key " + key);
        }
    }

    static String operatorToString(int op) {
        switch (op) {
            case 0:
                return "EQ";
            case 1:
                return "GT";
            case 2:
                return "GTE";
            default:
                throw new IllegalArgumentException("Unknown operator " + op);
        }
    }

    private static boolean isValidKey(int key) {
        return key == 0 || key == 1 || key == 4 || key == 2 || key == 3 || key == 5 || key == 6 || key == 7 || key == 8;
    }
}
