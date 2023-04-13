package android.security.keystore;

import android.annotation.SystemApi;
import android.text.TextUtils;
import java.math.BigInteger;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Date;
import javax.security.auth.x500.X500Principal;
/* loaded from: classes3.dex */
public final class KeyGenParameterSpec implements AlgorithmParameterSpec, UserAuthArgs {
    private final String mAttestKeyAlias;
    private final byte[] mAttestationChallenge;
    private final int[] mAttestationIds;
    private final String[] mBlockModes;
    private final Date mCertificateNotAfter;
    private final Date mCertificateNotBefore;
    private final BigInteger mCertificateSerialNumber;
    private final X500Principal mCertificateSubject;
    private final boolean mCriticalToDeviceEncryption;
    private final boolean mDevicePropertiesAttestationIncluded;
    private final String[] mDigests;
    private final String[] mEncryptionPaddings;
    private final boolean mInvalidatedByBiometricEnrollment;
    private final boolean mIsStrongBoxBacked;
    private final int mKeySize;
    private final Date mKeyValidityForConsumptionEnd;
    private final Date mKeyValidityForOriginationEnd;
    private final Date mKeyValidityStart;
    private final String mKeystoreAlias;
    private final int mMaxUsageCount;
    private final int mNamespace;
    private final int mPurposes;
    private final boolean mRandomizedEncryptionRequired;
    private final String[] mSignaturePaddings;
    private final AlgorithmParameterSpec mSpec;
    private final boolean mUniqueIdIncluded;
    private final boolean mUnlockedDeviceRequired;
    private final boolean mUserAuthenticationRequired;
    private final int mUserAuthenticationType;
    private final boolean mUserAuthenticationValidWhileOnBody;
    private final int mUserAuthenticationValidityDurationSeconds;
    private final boolean mUserConfirmationRequired;
    private final boolean mUserPresenceRequired;
    private static final X500Principal DEFAULT_ATTESTATION_CERT_SUBJECT = new X500Principal("CN=Android Keystore Key");
    private static final X500Principal DEFAULT_SELF_SIGNED_CERT_SUBJECT = new X500Principal("CN=Fake");
    private static final BigInteger DEFAULT_CERT_SERIAL_NUMBER = new BigInteger("1");
    private static final Date DEFAULT_CERT_NOT_BEFORE = new Date(0);
    private static final Date DEFAULT_CERT_NOT_AFTER = new Date(2461449600000L);

    public KeyGenParameterSpec(String keyStoreAlias, int namespace, int keySize, AlgorithmParameterSpec spec, X500Principal certificateSubject, BigInteger certificateSerialNumber, Date certificateNotBefore, Date certificateNotAfter, Date keyValidityStart, Date keyValidityForOriginationEnd, Date keyValidityForConsumptionEnd, int purposes, String[] digests, String[] encryptionPaddings, String[] signaturePaddings, String[] blockModes, boolean randomizedEncryptionRequired, boolean userAuthenticationRequired, int userAuthenticationValidityDurationSeconds, int userAuthenticationType, boolean userPresenceRequired, byte[] attestationChallenge, boolean devicePropertiesAttestationIncluded, int[] attestationIds, boolean uniqueIdIncluded, boolean userAuthenticationValidWhileOnBody, boolean invalidatedByBiometricEnrollment, boolean isStrongBoxBacked, boolean userConfirmationRequired, boolean unlockedDeviceRequired, boolean criticalToDeviceEncryption, int maxUsageCount, String attestKeyAlias) {
        X500Principal certificateSubject2;
        Date certificateNotBefore2;
        Date certificateNotAfter2;
        BigInteger certificateSerialNumber2;
        if (TextUtils.isEmpty(keyStoreAlias)) {
            throw new IllegalArgumentException("keyStoreAlias must not be empty");
        }
        if (certificateSubject != null) {
            certificateSubject2 = certificateSubject;
        } else if (attestationChallenge == null) {
            certificateSubject2 = DEFAULT_SELF_SIGNED_CERT_SUBJECT;
        } else {
            certificateSubject2 = DEFAULT_ATTESTATION_CERT_SUBJECT;
        }
        if (certificateNotBefore != null) {
            certificateNotBefore2 = certificateNotBefore;
        } else {
            certificateNotBefore2 = DEFAULT_CERT_NOT_BEFORE;
        }
        if (certificateNotAfter != null) {
            certificateNotAfter2 = certificateNotAfter;
        } else {
            certificateNotAfter2 = DEFAULT_CERT_NOT_AFTER;
        }
        if (certificateSerialNumber != null) {
            certificateSerialNumber2 = certificateSerialNumber;
        } else {
            certificateSerialNumber2 = DEFAULT_CERT_SERIAL_NUMBER;
        }
        if (certificateNotAfter2.before(certificateNotBefore2)) {
            throw new IllegalArgumentException("certificateNotAfter < certificateNotBefore");
        }
        this.mKeystoreAlias = keyStoreAlias;
        this.mNamespace = namespace;
        this.mKeySize = keySize;
        this.mSpec = spec;
        this.mCertificateSubject = certificateSubject2;
        this.mCertificateSerialNumber = certificateSerialNumber2;
        this.mCertificateNotBefore = Utils.cloneIfNotNull(certificateNotBefore2);
        this.mCertificateNotAfter = Utils.cloneIfNotNull(certificateNotAfter2);
        this.mKeyValidityStart = Utils.cloneIfNotNull(keyValidityStart);
        this.mKeyValidityForOriginationEnd = Utils.cloneIfNotNull(keyValidityForOriginationEnd);
        this.mKeyValidityForConsumptionEnd = Utils.cloneIfNotNull(keyValidityForConsumptionEnd);
        this.mPurposes = purposes;
        this.mDigests = ArrayUtils.cloneIfNotEmpty(digests);
        this.mEncryptionPaddings = ArrayUtils.cloneIfNotEmpty(ArrayUtils.nullToEmpty(encryptionPaddings));
        this.mSignaturePaddings = ArrayUtils.cloneIfNotEmpty(ArrayUtils.nullToEmpty(signaturePaddings));
        this.mBlockModes = ArrayUtils.cloneIfNotEmpty(ArrayUtils.nullToEmpty(blockModes));
        this.mRandomizedEncryptionRequired = randomizedEncryptionRequired;
        this.mUserAuthenticationRequired = userAuthenticationRequired;
        this.mUserPresenceRequired = userPresenceRequired;
        this.mUserAuthenticationValidityDurationSeconds = userAuthenticationValidityDurationSeconds;
        this.mUserAuthenticationType = userAuthenticationType;
        this.mAttestationChallenge = Utils.cloneIfNotNull(attestationChallenge);
        this.mDevicePropertiesAttestationIncluded = devicePropertiesAttestationIncluded;
        this.mAttestationIds = attestationIds;
        this.mUniqueIdIncluded = uniqueIdIncluded;
        this.mUserAuthenticationValidWhileOnBody = userAuthenticationValidWhileOnBody;
        this.mInvalidatedByBiometricEnrollment = invalidatedByBiometricEnrollment;
        this.mIsStrongBoxBacked = isStrongBoxBacked;
        this.mUserConfirmationRequired = userConfirmationRequired;
        this.mUnlockedDeviceRequired = unlockedDeviceRequired;
        this.mCriticalToDeviceEncryption = criticalToDeviceEncryption;
        this.mMaxUsageCount = maxUsageCount;
        this.mAttestKeyAlias = attestKeyAlias;
    }

    public String getKeystoreAlias() {
        return this.mKeystoreAlias;
    }

    @Deprecated
    public int getUid() {
        try {
            return KeyProperties.namespaceToLegacyUid(this.mNamespace);
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException("getUid called on KeyGenParameterSpec with non legacy keystore namespace.", e);
        }
    }

    @SystemApi
    public int getNamespace() {
        return this.mNamespace;
    }

    public int getKeySize() {
        return this.mKeySize;
    }

    public AlgorithmParameterSpec getAlgorithmParameterSpec() {
        return this.mSpec;
    }

    public X500Principal getCertificateSubject() {
        return this.mCertificateSubject;
    }

    public BigInteger getCertificateSerialNumber() {
        return this.mCertificateSerialNumber;
    }

    public Date getCertificateNotBefore() {
        return Utils.cloneIfNotNull(this.mCertificateNotBefore);
    }

    public Date getCertificateNotAfter() {
        return Utils.cloneIfNotNull(this.mCertificateNotAfter);
    }

    public Date getKeyValidityStart() {
        return Utils.cloneIfNotNull(this.mKeyValidityStart);
    }

    public Date getKeyValidityForConsumptionEnd() {
        return Utils.cloneIfNotNull(this.mKeyValidityForConsumptionEnd);
    }

    public Date getKeyValidityForOriginationEnd() {
        return Utils.cloneIfNotNull(this.mKeyValidityForOriginationEnd);
    }

    public int getPurposes() {
        return this.mPurposes;
    }

    public String[] getDigests() {
        String[] strArr = this.mDigests;
        if (strArr == null) {
            throw new IllegalStateException("Digests not specified");
        }
        return ArrayUtils.cloneIfNotEmpty(strArr);
    }

    public boolean isDigestsSpecified() {
        return this.mDigests != null;
    }

    public String[] getEncryptionPaddings() {
        return ArrayUtils.cloneIfNotEmpty(this.mEncryptionPaddings);
    }

    public String[] getSignaturePaddings() {
        return ArrayUtils.cloneIfNotEmpty(this.mSignaturePaddings);
    }

    public String[] getBlockModes() {
        return ArrayUtils.cloneIfNotEmpty(this.mBlockModes);
    }

    public boolean isRandomizedEncryptionRequired() {
        return this.mRandomizedEncryptionRequired;
    }

    @Override // android.security.keystore.UserAuthArgs
    public boolean isUserAuthenticationRequired() {
        return this.mUserAuthenticationRequired;
    }

    @Override // android.security.keystore.UserAuthArgs
    public boolean isUserConfirmationRequired() {
        return this.mUserConfirmationRequired;
    }

    @Override // android.security.keystore.UserAuthArgs
    public int getUserAuthenticationValidityDurationSeconds() {
        return this.mUserAuthenticationValidityDurationSeconds;
    }

    @Override // android.security.keystore.UserAuthArgs
    public int getUserAuthenticationType() {
        return this.mUserAuthenticationType;
    }

    @Override // android.security.keystore.UserAuthArgs
    public boolean isUserPresenceRequired() {
        return this.mUserPresenceRequired;
    }

    public byte[] getAttestationChallenge() {
        return Utils.cloneIfNotNull(this.mAttestationChallenge);
    }

    public boolean isDevicePropertiesAttestationIncluded() {
        return this.mDevicePropertiesAttestationIncluded;
    }

    @SystemApi
    public int[] getAttestationIds() {
        return (int[]) this.mAttestationIds.clone();
    }

    public boolean isUniqueIdIncluded() {
        return this.mUniqueIdIncluded;
    }

    @Override // android.security.keystore.UserAuthArgs
    public boolean isUserAuthenticationValidWhileOnBody() {
        return this.mUserAuthenticationValidWhileOnBody;
    }

    @Override // android.security.keystore.UserAuthArgs
    public boolean isInvalidatedByBiometricEnrollment() {
        return this.mInvalidatedByBiometricEnrollment;
    }

    public boolean isStrongBoxBacked() {
        return this.mIsStrongBoxBacked;
    }

    @Override // android.security.keystore.UserAuthArgs
    public boolean isUnlockedDeviceRequired() {
        return this.mUnlockedDeviceRequired;
    }

    @Override // android.security.keystore.UserAuthArgs
    public long getBoundToSpecificSecureUserId() {
        return 0L;
    }

    public boolean isCriticalToDeviceEncryption() {
        return this.mCriticalToDeviceEncryption;
    }

    public int getMaxUsageCount() {
        return this.mMaxUsageCount;
    }

    public String getAttestKeyAlias() {
        return this.mAttestKeyAlias;
    }

    /* loaded from: classes3.dex */
    public static final class Builder {
        private String mAttestKeyAlias;
        private byte[] mAttestationChallenge;
        private int[] mAttestationIds;
        private String[] mBlockModes;
        private Date mCertificateNotAfter;
        private Date mCertificateNotBefore;
        private BigInteger mCertificateSerialNumber;
        private X500Principal mCertificateSubject;
        private boolean mCriticalToDeviceEncryption;
        private boolean mDevicePropertiesAttestationIncluded;
        private String[] mDigests;
        private String[] mEncryptionPaddings;
        private boolean mInvalidatedByBiometricEnrollment;
        private boolean mIsStrongBoxBacked;
        private int mKeySize;
        private Date mKeyValidityForConsumptionEnd;
        private Date mKeyValidityForOriginationEnd;
        private Date mKeyValidityStart;
        private final String mKeystoreAlias;
        private int mMaxUsageCount;
        private int mNamespace;
        private int mPurposes;
        private boolean mRandomizedEncryptionRequired;
        private String[] mSignaturePaddings;
        private AlgorithmParameterSpec mSpec;
        private boolean mUniqueIdIncluded;
        private boolean mUnlockedDeviceRequired;
        private boolean mUserAuthenticationRequired;
        private int mUserAuthenticationType;
        private boolean mUserAuthenticationValidWhileOnBody;
        private int mUserAuthenticationValidityDurationSeconds;
        private boolean mUserConfirmationRequired;
        private boolean mUserPresenceRequired;

        public Builder(String keystoreAlias, int purposes) {
            this.mNamespace = -1;
            this.mKeySize = -1;
            this.mRandomizedEncryptionRequired = true;
            this.mUserAuthenticationValidityDurationSeconds = 0;
            this.mUserAuthenticationType = 2;
            this.mUserPresenceRequired = false;
            this.mAttestationChallenge = null;
            this.mDevicePropertiesAttestationIncluded = false;
            this.mAttestationIds = new int[0];
            this.mUniqueIdIncluded = false;
            this.mInvalidatedByBiometricEnrollment = true;
            this.mIsStrongBoxBacked = false;
            this.mUnlockedDeviceRequired = false;
            this.mCriticalToDeviceEncryption = false;
            this.mMaxUsageCount = -1;
            this.mAttestKeyAlias = null;
            if (keystoreAlias == null) {
                throw new NullPointerException("keystoreAlias == null");
            }
            if (keystoreAlias.isEmpty()) {
                throw new IllegalArgumentException("keystoreAlias must not be empty");
            }
            this.mKeystoreAlias = keystoreAlias;
            this.mPurposes = purposes;
        }

        public Builder(KeyGenParameterSpec sourceSpec) {
            this(sourceSpec.getKeystoreAlias(), sourceSpec.getPurposes());
            this.mNamespace = sourceSpec.getNamespace();
            this.mKeySize = sourceSpec.getKeySize();
            this.mSpec = sourceSpec.getAlgorithmParameterSpec();
            this.mCertificateSubject = sourceSpec.getCertificateSubject();
            this.mCertificateSerialNumber = sourceSpec.getCertificateSerialNumber();
            this.mCertificateNotBefore = sourceSpec.getCertificateNotBefore();
            this.mCertificateNotAfter = sourceSpec.getCertificateNotAfter();
            this.mKeyValidityStart = sourceSpec.getKeyValidityStart();
            this.mKeyValidityForOriginationEnd = sourceSpec.getKeyValidityForOriginationEnd();
            this.mKeyValidityForConsumptionEnd = sourceSpec.getKeyValidityForConsumptionEnd();
            this.mPurposes = sourceSpec.getPurposes();
            if (sourceSpec.isDigestsSpecified()) {
                this.mDigests = sourceSpec.getDigests();
            }
            this.mEncryptionPaddings = sourceSpec.getEncryptionPaddings();
            this.mSignaturePaddings = sourceSpec.getSignaturePaddings();
            this.mBlockModes = sourceSpec.getBlockModes();
            this.mRandomizedEncryptionRequired = sourceSpec.isRandomizedEncryptionRequired();
            this.mUserAuthenticationRequired = sourceSpec.isUserAuthenticationRequired();
            this.mUserAuthenticationValidityDurationSeconds = sourceSpec.getUserAuthenticationValidityDurationSeconds();
            this.mUserAuthenticationType = sourceSpec.getUserAuthenticationType();
            this.mUserPresenceRequired = sourceSpec.isUserPresenceRequired();
            this.mAttestationChallenge = sourceSpec.getAttestationChallenge();
            this.mDevicePropertiesAttestationIncluded = sourceSpec.isDevicePropertiesAttestationIncluded();
            this.mAttestationIds = sourceSpec.getAttestationIds();
            this.mUniqueIdIncluded = sourceSpec.isUniqueIdIncluded();
            this.mUserAuthenticationValidWhileOnBody = sourceSpec.isUserAuthenticationValidWhileOnBody();
            this.mInvalidatedByBiometricEnrollment = sourceSpec.isInvalidatedByBiometricEnrollment();
            this.mIsStrongBoxBacked = sourceSpec.isStrongBoxBacked();
            this.mUserConfirmationRequired = sourceSpec.isUserConfirmationRequired();
            this.mUnlockedDeviceRequired = sourceSpec.isUnlockedDeviceRequired();
            this.mCriticalToDeviceEncryption = sourceSpec.isCriticalToDeviceEncryption();
            this.mMaxUsageCount = sourceSpec.getMaxUsageCount();
            this.mAttestKeyAlias = sourceSpec.getAttestKeyAlias();
        }

        @SystemApi
        @Deprecated
        public Builder setUid(int uid) {
            this.mNamespace = KeyProperties.legacyUidToNamespace(uid);
            return this;
        }

        @SystemApi
        public Builder setNamespace(int namespace) {
            this.mNamespace = namespace;
            return this;
        }

        public Builder setKeySize(int keySize) {
            if (keySize < 0) {
                throw new IllegalArgumentException("keySize < 0");
            }
            this.mKeySize = keySize;
            return this;
        }

        public Builder setAlgorithmParameterSpec(AlgorithmParameterSpec spec) {
            if (spec == null) {
                throw new NullPointerException("spec == null");
            }
            this.mSpec = spec;
            return this;
        }

        public Builder setCertificateSubject(X500Principal subject) {
            if (subject == null) {
                throw new NullPointerException("subject == null");
            }
            this.mCertificateSubject = subject;
            return this;
        }

        public Builder setCertificateSerialNumber(BigInteger serialNumber) {
            if (serialNumber == null) {
                throw new NullPointerException("serialNumber == null");
            }
            this.mCertificateSerialNumber = serialNumber;
            return this;
        }

        public Builder setCertificateNotBefore(Date date) {
            if (date == null) {
                throw new NullPointerException("date == null");
            }
            this.mCertificateNotBefore = Utils.cloneIfNotNull(date);
            return this;
        }

        public Builder setCertificateNotAfter(Date date) {
            if (date == null) {
                throw new NullPointerException("date == null");
            }
            this.mCertificateNotAfter = Utils.cloneIfNotNull(date);
            return this;
        }

        public Builder setKeyValidityStart(Date startDate) {
            this.mKeyValidityStart = Utils.cloneIfNotNull(startDate);
            return this;
        }

        public Builder setKeyValidityEnd(Date endDate) {
            setKeyValidityForOriginationEnd(endDate);
            setKeyValidityForConsumptionEnd(endDate);
            return this;
        }

        public Builder setKeyValidityForOriginationEnd(Date endDate) {
            this.mKeyValidityForOriginationEnd = Utils.cloneIfNotNull(endDate);
            return this;
        }

        public Builder setKeyValidityForConsumptionEnd(Date endDate) {
            this.mKeyValidityForConsumptionEnd = Utils.cloneIfNotNull(endDate);
            return this;
        }

        public Builder setDigests(String... digests) {
            this.mDigests = ArrayUtils.cloneIfNotEmpty(digests);
            return this;
        }

        public Builder setEncryptionPaddings(String... paddings) {
            this.mEncryptionPaddings = ArrayUtils.cloneIfNotEmpty(paddings);
            return this;
        }

        public Builder setSignaturePaddings(String... paddings) {
            this.mSignaturePaddings = ArrayUtils.cloneIfNotEmpty(paddings);
            return this;
        }

        public Builder setBlockModes(String... blockModes) {
            this.mBlockModes = ArrayUtils.cloneIfNotEmpty(blockModes);
            return this;
        }

        public Builder setRandomizedEncryptionRequired(boolean required) {
            this.mRandomizedEncryptionRequired = required;
            return this;
        }

        public Builder setUserAuthenticationRequired(boolean required) {
            this.mUserAuthenticationRequired = required;
            return this;
        }

        public Builder setUserConfirmationRequired(boolean required) {
            this.mUserConfirmationRequired = required;
            return this;
        }

        @Deprecated
        public Builder setUserAuthenticationValidityDurationSeconds(int seconds) {
            if (seconds < -1) {
                throw new IllegalArgumentException("seconds must be -1 or larger");
            }
            if (seconds == -1) {
                return setUserAuthenticationParameters(0, 2);
            }
            return setUserAuthenticationParameters(seconds, 3);
        }

        public Builder setUserAuthenticationParameters(int timeout, int type) {
            if (timeout < 0) {
                throw new IllegalArgumentException("timeout must be 0 or larger");
            }
            this.mUserAuthenticationValidityDurationSeconds = timeout;
            this.mUserAuthenticationType = type;
            return this;
        }

        public Builder setUserPresenceRequired(boolean required) {
            this.mUserPresenceRequired = required;
            return this;
        }

        public Builder setAttestationChallenge(byte[] attestationChallenge) {
            this.mAttestationChallenge = attestationChallenge;
            return this;
        }

        public Builder setDevicePropertiesAttestationIncluded(boolean devicePropertiesAttestationIncluded) {
            this.mDevicePropertiesAttestationIncluded = devicePropertiesAttestationIncluded;
            return this;
        }

        @SystemApi
        public Builder setAttestationIds(int[] attestationIds) {
            this.mAttestationIds = attestationIds;
            return this;
        }

        public Builder setUniqueIdIncluded(boolean uniqueIdIncluded) {
            this.mUniqueIdIncluded = uniqueIdIncluded;
            return this;
        }

        public Builder setUserAuthenticationValidWhileOnBody(boolean remainsValid) {
            this.mUserAuthenticationValidWhileOnBody = remainsValid;
            return this;
        }

        public Builder setInvalidatedByBiometricEnrollment(boolean invalidateKey) {
            this.mInvalidatedByBiometricEnrollment = invalidateKey;
            return this;
        }

        public Builder setIsStrongBoxBacked(boolean isStrongBoxBacked) {
            this.mIsStrongBoxBacked = isStrongBoxBacked;
            return this;
        }

        public Builder setUnlockedDeviceRequired(boolean unlockedDeviceRequired) {
            this.mUnlockedDeviceRequired = unlockedDeviceRequired;
            return this;
        }

        public Builder setCriticalToDeviceEncryption(boolean critical) {
            this.mCriticalToDeviceEncryption = critical;
            return this;
        }

        public Builder setMaxUsageCount(int maxUsageCount) {
            if (maxUsageCount == -1 || maxUsageCount > 0) {
                this.mMaxUsageCount = maxUsageCount;
                return this;
            }
            throw new IllegalArgumentException("maxUsageCount is not valid");
        }

        public Builder setAttestKeyAlias(String attestKeyAlias) {
            this.mAttestKeyAlias = attestKeyAlias;
            return this;
        }

        public KeyGenParameterSpec build() {
            return new KeyGenParameterSpec(this.mKeystoreAlias, this.mNamespace, this.mKeySize, this.mSpec, this.mCertificateSubject, this.mCertificateSerialNumber, this.mCertificateNotBefore, this.mCertificateNotAfter, this.mKeyValidityStart, this.mKeyValidityForOriginationEnd, this.mKeyValidityForConsumptionEnd, this.mPurposes, this.mDigests, this.mEncryptionPaddings, this.mSignaturePaddings, this.mBlockModes, this.mRandomizedEncryptionRequired, this.mUserAuthenticationRequired, this.mUserAuthenticationValidityDurationSeconds, this.mUserAuthenticationType, this.mUserPresenceRequired, this.mAttestationChallenge, this.mDevicePropertiesAttestationIncluded, this.mAttestationIds, this.mUniqueIdIncluded, this.mUserAuthenticationValidWhileOnBody, this.mInvalidatedByBiometricEnrollment, this.mIsStrongBoxBacked, this.mUserConfirmationRequired, this.mUnlockedDeviceRequired, this.mCriticalToDeviceEncryption, this.mMaxUsageCount, this.mAttestKeyAlias);
        }
    }
}
