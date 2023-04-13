package android.security.keystore;

import java.security.KeyStore;
import java.util.Date;
/* loaded from: classes3.dex */
public final class KeyProtection implements KeyStore.ProtectionParameter, UserAuthArgs {
    private final String[] mBlockModes;
    private final long mBoundToSecureUserId;
    private final boolean mCriticalToDeviceEncryption;
    private final String[] mDigests;
    private final String[] mEncryptionPaddings;
    private final boolean mInvalidatedByBiometricEnrollment;
    private final boolean mIsStrongBoxBacked;
    private final Date mKeyValidityForConsumptionEnd;
    private final Date mKeyValidityForOriginationEnd;
    private final Date mKeyValidityStart;
    private final int mMaxUsageCount;
    private final int mPurposes;
    private final boolean mRandomizedEncryptionRequired;
    private final boolean mRollbackResistant;
    private final String[] mSignaturePaddings;
    private final boolean mUnlockedDeviceRequired;
    private final boolean mUserAuthenticationRequired;
    private final int mUserAuthenticationType;
    private final boolean mUserAuthenticationValidWhileOnBody;
    private final int mUserAuthenticationValidityDurationSeconds;
    private final boolean mUserConfirmationRequired;
    private final boolean mUserPresenceRequred;

    private KeyProtection(Date keyValidityStart, Date keyValidityForOriginationEnd, Date keyValidityForConsumptionEnd, int purposes, String[] encryptionPaddings, String[] signaturePaddings, String[] digests, String[] blockModes, boolean randomizedEncryptionRequired, boolean userAuthenticationRequired, int userAuthenticationType, int userAuthenticationValidityDurationSeconds, boolean userPresenceRequred, boolean userAuthenticationValidWhileOnBody, boolean invalidatedByBiometricEnrollment, long boundToSecureUserId, boolean criticalToDeviceEncryption, boolean userConfirmationRequired, boolean unlockedDeviceRequired, boolean isStrongBoxBacked, int maxUsageCount, boolean rollbackResistant) {
        this.mKeyValidityStart = Utils.cloneIfNotNull(keyValidityStart);
        this.mKeyValidityForOriginationEnd = Utils.cloneIfNotNull(keyValidityForOriginationEnd);
        this.mKeyValidityForConsumptionEnd = Utils.cloneIfNotNull(keyValidityForConsumptionEnd);
        this.mPurposes = purposes;
        this.mEncryptionPaddings = ArrayUtils.cloneIfNotEmpty(ArrayUtils.nullToEmpty(encryptionPaddings));
        this.mSignaturePaddings = ArrayUtils.cloneIfNotEmpty(ArrayUtils.nullToEmpty(signaturePaddings));
        this.mDigests = ArrayUtils.cloneIfNotEmpty(digests);
        this.mBlockModes = ArrayUtils.cloneIfNotEmpty(ArrayUtils.nullToEmpty(blockModes));
        this.mRandomizedEncryptionRequired = randomizedEncryptionRequired;
        this.mUserAuthenticationRequired = userAuthenticationRequired;
        this.mUserAuthenticationType = userAuthenticationType;
        this.mUserAuthenticationValidityDurationSeconds = userAuthenticationValidityDurationSeconds;
        this.mUserPresenceRequred = userPresenceRequred;
        this.mUserAuthenticationValidWhileOnBody = userAuthenticationValidWhileOnBody;
        this.mInvalidatedByBiometricEnrollment = invalidatedByBiometricEnrollment;
        this.mBoundToSecureUserId = boundToSecureUserId;
        this.mCriticalToDeviceEncryption = criticalToDeviceEncryption;
        this.mUserConfirmationRequired = userConfirmationRequired;
        this.mUnlockedDeviceRequired = unlockedDeviceRequired;
        this.mIsStrongBoxBacked = isStrongBoxBacked;
        this.mMaxUsageCount = maxUsageCount;
        this.mRollbackResistant = rollbackResistant;
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

    public String[] getEncryptionPaddings() {
        return ArrayUtils.cloneIfNotEmpty(this.mEncryptionPaddings);
    }

    public String[] getSignaturePaddings() {
        return ArrayUtils.cloneIfNotEmpty(this.mSignaturePaddings);
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
    public int getUserAuthenticationType() {
        return this.mUserAuthenticationType;
    }

    @Override // android.security.keystore.UserAuthArgs
    public int getUserAuthenticationValidityDurationSeconds() {
        return this.mUserAuthenticationValidityDurationSeconds;
    }

    @Override // android.security.keystore.UserAuthArgs
    public boolean isUserPresenceRequired() {
        return this.mUserPresenceRequred;
    }

    @Override // android.security.keystore.UserAuthArgs
    public boolean isUserAuthenticationValidWhileOnBody() {
        return this.mUserAuthenticationValidWhileOnBody;
    }

    @Override // android.security.keystore.UserAuthArgs
    public boolean isInvalidatedByBiometricEnrollment() {
        return this.mInvalidatedByBiometricEnrollment;
    }

    @Override // android.security.keystore.UserAuthArgs
    public long getBoundToSpecificSecureUserId() {
        return this.mBoundToSecureUserId;
    }

    public boolean isCriticalToDeviceEncryption() {
        return this.mCriticalToDeviceEncryption;
    }

    @Override // android.security.keystore.UserAuthArgs
    public boolean isUnlockedDeviceRequired() {
        return this.mUnlockedDeviceRequired;
    }

    public boolean isStrongBoxBacked() {
        return this.mIsStrongBoxBacked;
    }

    public int getMaxUsageCount() {
        return this.mMaxUsageCount;
    }

    public boolean isRollbackResistant() {
        return this.mRollbackResistant;
    }

    /* loaded from: classes3.dex */
    public static final class Builder {
        private String[] mBlockModes;
        private String[] mDigests;
        private String[] mEncryptionPaddings;
        private Date mKeyValidityForConsumptionEnd;
        private Date mKeyValidityForOriginationEnd;
        private Date mKeyValidityStart;
        private int mPurposes;
        private String[] mSignaturePaddings;
        private boolean mUserAuthenticationRequired;
        private boolean mUserAuthenticationValidWhileOnBody;
        private boolean mUserConfirmationRequired;
        private boolean mRandomizedEncryptionRequired = true;
        private int mUserAuthenticationValidityDurationSeconds = 0;
        private int mUserAuthenticationType = 2;
        private boolean mUserPresenceRequired = false;
        private boolean mInvalidatedByBiometricEnrollment = true;
        private boolean mUnlockedDeviceRequired = false;
        private long mBoundToSecureUserId = 0;
        private boolean mCriticalToDeviceEncryption = false;
        private boolean mIsStrongBoxBacked = false;
        private int mMaxUsageCount = -1;
        private String mAttestKeyAlias = null;
        private boolean mRollbackResistant = false;

        public Builder(int purposes) {
            this.mPurposes = purposes;
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

        public Builder setEncryptionPaddings(String... paddings) {
            this.mEncryptionPaddings = ArrayUtils.cloneIfNotEmpty(paddings);
            return this;
        }

        public Builder setSignaturePaddings(String... paddings) {
            this.mSignaturePaddings = ArrayUtils.cloneIfNotEmpty(paddings);
            return this;
        }

        public Builder setDigests(String... digests) {
            this.mDigests = ArrayUtils.cloneIfNotEmpty(digests);
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

        public Builder setUserAuthenticationValidWhileOnBody(boolean remainsValid) {
            this.mUserAuthenticationValidWhileOnBody = remainsValid;
            return this;
        }

        public Builder setInvalidatedByBiometricEnrollment(boolean invalidateKey) {
            this.mInvalidatedByBiometricEnrollment = invalidateKey;
            return this;
        }

        public Builder setBoundToSpecificSecureUserId(long secureUserId) {
            this.mBoundToSecureUserId = secureUserId;
            return this;
        }

        public Builder setCriticalToDeviceEncryption(boolean critical) {
            this.mCriticalToDeviceEncryption = critical;
            return this;
        }

        public Builder setUnlockedDeviceRequired(boolean unlockedDeviceRequired) {
            this.mUnlockedDeviceRequired = unlockedDeviceRequired;
            return this;
        }

        public Builder setIsStrongBoxBacked(boolean isStrongBoxBacked) {
            this.mIsStrongBoxBacked = isStrongBoxBacked;
            return this;
        }

        public Builder setMaxUsageCount(int maxUsageCount) {
            if (maxUsageCount == -1 || maxUsageCount > 0) {
                this.mMaxUsageCount = maxUsageCount;
                return this;
            }
            throw new IllegalArgumentException("maxUsageCount is not valid");
        }

        public Builder setRollbackResistant(boolean rollbackResistant) {
            this.mRollbackResistant = rollbackResistant;
            return this;
        }

        public KeyProtection build() {
            return new KeyProtection(this.mKeyValidityStart, this.mKeyValidityForOriginationEnd, this.mKeyValidityForConsumptionEnd, this.mPurposes, this.mEncryptionPaddings, this.mSignaturePaddings, this.mDigests, this.mBlockModes, this.mRandomizedEncryptionRequired, this.mUserAuthenticationRequired, this.mUserAuthenticationType, this.mUserAuthenticationValidityDurationSeconds, this.mUserPresenceRequired, this.mUserAuthenticationValidWhileOnBody, this.mInvalidatedByBiometricEnrollment, this.mBoundToSecureUserId, this.mCriticalToDeviceEncryption, this.mUserConfirmationRequired, this.mUnlockedDeviceRequired, this.mIsStrongBoxBacked, this.mMaxUsageCount, this.mRollbackResistant);
        }
    }
}
