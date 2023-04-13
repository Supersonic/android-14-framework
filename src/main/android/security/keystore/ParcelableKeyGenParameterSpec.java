package android.security.keystore;

import android.p008os.Parcel;
import android.p008os.Parcelable;
import java.math.BigInteger;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.RSAKeyGenParameterSpec;
import java.util.Date;
import javax.security.auth.x500.X500Principal;
/* loaded from: classes3.dex */
public final class ParcelableKeyGenParameterSpec implements Parcelable {
    private static final int ALGORITHM_PARAMETER_SPEC_EC = 3;
    private static final int ALGORITHM_PARAMETER_SPEC_NONE = 1;
    private static final int ALGORITHM_PARAMETER_SPEC_RSA = 2;
    public static final Parcelable.Creator<ParcelableKeyGenParameterSpec> CREATOR = new Parcelable.Creator<ParcelableKeyGenParameterSpec>() { // from class: android.security.keystore.ParcelableKeyGenParameterSpec.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ParcelableKeyGenParameterSpec createFromParcel(Parcel in) {
            return new ParcelableKeyGenParameterSpec(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public ParcelableKeyGenParameterSpec[] newArray(int size) {
            return new ParcelableKeyGenParameterSpec[size];
        }
    };
    private final KeyGenParameterSpec mSpec;

    public ParcelableKeyGenParameterSpec(KeyGenParameterSpec spec) {
        this.mSpec = spec;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    private static void writeOptionalDate(Parcel out, Date date) {
        if (date != null) {
            out.writeBoolean(true);
            out.writeLong(date.getTime());
            return;
        }
        out.writeBoolean(false);
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(this.mSpec.getKeystoreAlias());
        out.writeInt(this.mSpec.getPurposes());
        out.writeInt(this.mSpec.getNamespace());
        out.writeInt(this.mSpec.getKeySize());
        AlgorithmParameterSpec algoSpec = this.mSpec.getAlgorithmParameterSpec();
        if (algoSpec == null) {
            out.writeInt(1);
        } else if (algoSpec instanceof RSAKeyGenParameterSpec) {
            RSAKeyGenParameterSpec rsaSpec = (RSAKeyGenParameterSpec) algoSpec;
            out.writeInt(2);
            out.writeInt(rsaSpec.getKeysize());
            out.writeByteArray(rsaSpec.getPublicExponent().toByteArray());
        } else if (algoSpec instanceof ECGenParameterSpec) {
            ECGenParameterSpec ecSpec = (ECGenParameterSpec) algoSpec;
            out.writeInt(3);
            out.writeString(ecSpec.getName());
        } else {
            throw new IllegalArgumentException(String.format("Unknown algorithm parameter spec: %s", algoSpec.getClass()));
        }
        out.writeByteArray(this.mSpec.getCertificateSubject().getEncoded());
        out.writeByteArray(this.mSpec.getCertificateSerialNumber().toByteArray());
        out.writeLong(this.mSpec.getCertificateNotBefore().getTime());
        out.writeLong(this.mSpec.getCertificateNotAfter().getTime());
        writeOptionalDate(out, this.mSpec.getKeyValidityStart());
        writeOptionalDate(out, this.mSpec.getKeyValidityForOriginationEnd());
        writeOptionalDate(out, this.mSpec.getKeyValidityForConsumptionEnd());
        if (this.mSpec.isDigestsSpecified()) {
            out.writeStringArray(this.mSpec.getDigests());
        } else {
            out.writeStringArray(null);
        }
        out.writeStringArray(this.mSpec.getEncryptionPaddings());
        out.writeStringArray(this.mSpec.getSignaturePaddings());
        out.writeStringArray(this.mSpec.getBlockModes());
        out.writeBoolean(this.mSpec.isRandomizedEncryptionRequired());
        out.writeBoolean(this.mSpec.isUserAuthenticationRequired());
        out.writeInt(this.mSpec.getUserAuthenticationValidityDurationSeconds());
        out.writeInt(this.mSpec.getUserAuthenticationType());
        out.writeBoolean(this.mSpec.isUserPresenceRequired());
        out.writeByteArray(this.mSpec.getAttestationChallenge());
        out.writeBoolean(this.mSpec.isDevicePropertiesAttestationIncluded());
        out.writeIntArray(this.mSpec.getAttestationIds());
        out.writeBoolean(this.mSpec.isUniqueIdIncluded());
        out.writeBoolean(this.mSpec.isUserAuthenticationValidWhileOnBody());
        out.writeBoolean(this.mSpec.isInvalidatedByBiometricEnrollment());
        out.writeBoolean(this.mSpec.isStrongBoxBacked());
        out.writeBoolean(this.mSpec.isUserConfirmationRequired());
        out.writeBoolean(this.mSpec.isUnlockedDeviceRequired());
        out.writeBoolean(this.mSpec.isCriticalToDeviceEncryption());
        out.writeInt(this.mSpec.getMaxUsageCount());
        out.writeString(this.mSpec.getAttestKeyAlias());
    }

    private static Date readDateOrNull(Parcel in) {
        boolean hasDate = in.readBoolean();
        if (hasDate) {
            return new Date(in.readLong());
        }
        return null;
    }

    private ParcelableKeyGenParameterSpec(Parcel in) {
        AlgorithmParameterSpec algorithmSpec;
        String keystoreAlias = in.readString();
        int purposes = in.readInt();
        int namespace = in.readInt();
        int keySize = in.readInt();
        int keySpecType = in.readInt();
        if (keySpecType == 1) {
            algorithmSpec = null;
        } else if (keySpecType == 2) {
            int rsaKeySize = in.readInt();
            BigInteger publicExponent = new BigInteger(in.createByteArray());
            AlgorithmParameterSpec algorithmSpec2 = new RSAKeyGenParameterSpec(rsaKeySize, publicExponent);
            algorithmSpec = algorithmSpec2;
        } else if (keySpecType == 3) {
            String stdName = in.readString();
            AlgorithmParameterSpec algorithmSpec3 = new ECGenParameterSpec(stdName);
            algorithmSpec = algorithmSpec3;
        } else {
            throw new IllegalArgumentException(String.format("Unknown algorithm parameter spec: %d", Integer.valueOf(keySpecType)));
        }
        X500Principal certificateSubject = new X500Principal(in.createByteArray());
        BigInteger certificateSerialNumber = new BigInteger(in.createByteArray());
        Date certificateNotBefore = new Date(in.readLong());
        Date certificateNotAfter = new Date(in.readLong());
        Date keyValidityStartDate = readDateOrNull(in);
        Date keyValidityForOriginationEnd = readDateOrNull(in);
        Date keyValidityForConsumptionEnd = readDateOrNull(in);
        String[] digests = in.createStringArray();
        String[] encryptionPaddings = in.createStringArray();
        String[] signaturePaddings = in.createStringArray();
        String[] blockModes = in.createStringArray();
        boolean randomizedEncryptionRequired = in.readBoolean();
        boolean userAuthenticationRequired = in.readBoolean();
        int userAuthenticationValidityDurationSeconds = in.readInt();
        int userAuthenticationTypes = in.readInt();
        boolean userPresenceRequired = in.readBoolean();
        byte[] attestationChallenge = in.createByteArray();
        boolean devicePropertiesAttestationIncluded = in.readBoolean();
        int[] attestationIds = in.createIntArray();
        boolean uniqueIdIncluded = in.readBoolean();
        boolean userAuthenticationValidWhileOnBody = in.readBoolean();
        boolean invalidatedByBiometricEnrollment = in.readBoolean();
        boolean isStrongBoxBacked = in.readBoolean();
        boolean userConfirmationRequired = in.readBoolean();
        boolean unlockedDeviceRequired = in.readBoolean();
        boolean criticalToDeviceEncryption = in.readBoolean();
        int maxUsageCount = in.readInt();
        String attestKeyAlias = in.readString();
        this.mSpec = new KeyGenParameterSpec(keystoreAlias, namespace, keySize, algorithmSpec, certificateSubject, certificateSerialNumber, certificateNotBefore, certificateNotAfter, keyValidityStartDate, keyValidityForOriginationEnd, keyValidityForConsumptionEnd, purposes, digests, encryptionPaddings, signaturePaddings, blockModes, randomizedEncryptionRequired, userAuthenticationRequired, userAuthenticationValidityDurationSeconds, userAuthenticationTypes, userPresenceRequired, attestationChallenge, devicePropertiesAttestationIncluded, attestationIds, uniqueIdIncluded, userAuthenticationValidWhileOnBody, invalidatedByBiometricEnrollment, isStrongBoxBacked, userConfirmationRequired, unlockedDeviceRequired, criticalToDeviceEncryption, maxUsageCount, attestKeyAlias);
    }

    public KeyGenParameterSpec getSpec() {
        return this.mSpec;
    }
}
