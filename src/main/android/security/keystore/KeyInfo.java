package android.security.keystore;

import java.security.spec.KeySpec;
import java.util.Date;
/* loaded from: classes3.dex */
public class KeyInfo implements KeySpec {
    private final String[] mBlockModes;
    private final String[] mDigests;
    private final String[] mEncryptionPaddings;
    private final boolean mInsideSecureHardware;
    private final boolean mInvalidatedByBiometricEnrollment;
    private final int mKeySize;
    private final Date mKeyValidityForConsumptionEnd;
    private final Date mKeyValidityForOriginationEnd;
    private final Date mKeyValidityStart;
    private final String mKeystoreAlias;
    private final int mOrigin;
    private final int mPurposes;
    private final int mRemainingUsageCount;
    private final int mSecurityLevel;
    private final String[] mSignaturePaddings;
    private final boolean mTrustedUserPresenceRequired;
    private final boolean mUserAuthenticationRequired;
    private final boolean mUserAuthenticationRequirementEnforcedBySecureHardware;
    private final int mUserAuthenticationType;
    private final boolean mUserAuthenticationValidWhileOnBody;
    private final int mUserAuthenticationValidityDurationSeconds;
    private final boolean mUserConfirmationRequired;

    public KeyInfo(String keystoreKeyAlias, boolean insideSecureHardware, int origin, int keySize, Date keyValidityStart, Date keyValidityForOriginationEnd, Date keyValidityForConsumptionEnd, int purposes, String[] encryptionPaddings, String[] signaturePaddings, String[] digests, String[] blockModes, boolean userAuthenticationRequired, int userAuthenticationValidityDurationSeconds, int userAuthenticationType, boolean userAuthenticationRequirementEnforcedBySecureHardware, boolean userAuthenticationValidWhileOnBody, boolean trustedUserPresenceRequired, boolean invalidatedByBiometricEnrollment, boolean userConfirmationRequired, int securityLevel, int remainingUsageCount) {
        this.mKeystoreAlias = keystoreKeyAlias;
        this.mInsideSecureHardware = insideSecureHardware;
        this.mOrigin = origin;
        this.mKeySize = keySize;
        this.mKeyValidityStart = Utils.cloneIfNotNull(keyValidityStart);
        this.mKeyValidityForOriginationEnd = Utils.cloneIfNotNull(keyValidityForOriginationEnd);
        this.mKeyValidityForConsumptionEnd = Utils.cloneIfNotNull(keyValidityForConsumptionEnd);
        this.mPurposes = purposes;
        this.mEncryptionPaddings = ArrayUtils.cloneIfNotEmpty(ArrayUtils.nullToEmpty(encryptionPaddings));
        this.mSignaturePaddings = ArrayUtils.cloneIfNotEmpty(ArrayUtils.nullToEmpty(signaturePaddings));
        this.mDigests = ArrayUtils.cloneIfNotEmpty(ArrayUtils.nullToEmpty(digests));
        this.mBlockModes = ArrayUtils.cloneIfNotEmpty(ArrayUtils.nullToEmpty(blockModes));
        this.mUserAuthenticationRequired = userAuthenticationRequired;
        this.mUserAuthenticationValidityDurationSeconds = userAuthenticationValidityDurationSeconds;
        this.mUserAuthenticationType = userAuthenticationType;
        this.mUserAuthenticationRequirementEnforcedBySecureHardware = userAuthenticationRequirementEnforcedBySecureHardware;
        this.mUserAuthenticationValidWhileOnBody = userAuthenticationValidWhileOnBody;
        this.mTrustedUserPresenceRequired = trustedUserPresenceRequired;
        this.mInvalidatedByBiometricEnrollment = invalidatedByBiometricEnrollment;
        this.mUserConfirmationRequired = userConfirmationRequired;
        this.mSecurityLevel = securityLevel;
        this.mRemainingUsageCount = remainingUsageCount;
    }

    public String getKeystoreAlias() {
        return this.mKeystoreAlias;
    }

    @Deprecated
    public boolean isInsideSecureHardware() {
        return this.mInsideSecureHardware;
    }

    public int getOrigin() {
        return this.mOrigin;
    }

    public int getKeySize() {
        return this.mKeySize;
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

    public String[] getBlockModes() {
        return ArrayUtils.cloneIfNotEmpty(this.mBlockModes);
    }

    public String[] getEncryptionPaddings() {
        return ArrayUtils.cloneIfNotEmpty(this.mEncryptionPaddings);
    }

    public String[] getSignaturePaddings() {
        return ArrayUtils.cloneIfNotEmpty(this.mSignaturePaddings);
    }

    public String[] getDigests() {
        return ArrayUtils.cloneIfNotEmpty(this.mDigests);
    }

    public boolean isUserAuthenticationRequired() {
        return this.mUserAuthenticationRequired;
    }

    public boolean isUserConfirmationRequired() {
        return this.mUserConfirmationRequired;
    }

    public int getUserAuthenticationValidityDurationSeconds() {
        return this.mUserAuthenticationValidityDurationSeconds;
    }

    public int getUserAuthenticationType() {
        return this.mUserAuthenticationType;
    }

    public boolean isUserAuthenticationRequirementEnforcedBySecureHardware() {
        return this.mUserAuthenticationRequirementEnforcedBySecureHardware;
    }

    public boolean isUserAuthenticationValidWhileOnBody() {
        return this.mUserAuthenticationValidWhileOnBody;
    }

    public boolean isInvalidatedByBiometricEnrollment() {
        return this.mInvalidatedByBiometricEnrollment;
    }

    public boolean isTrustedUserPresenceRequired() {
        return this.mTrustedUserPresenceRequired;
    }

    public int getSecurityLevel() {
        return this.mSecurityLevel;
    }

    public int getRemainingUsageCount() {
        return this.mRemainingUsageCount;
    }
}
