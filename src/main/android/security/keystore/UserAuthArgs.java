package android.security.keystore;
/* loaded from: classes3.dex */
public interface UserAuthArgs {
    long getBoundToSpecificSecureUserId();

    int getUserAuthenticationType();

    int getUserAuthenticationValidityDurationSeconds();

    boolean isInvalidatedByBiometricEnrollment();

    boolean isUnlockedDeviceRequired();

    boolean isUserAuthenticationRequired();

    boolean isUserAuthenticationValidWhileOnBody();

    boolean isUserConfirmationRequired();

    boolean isUserPresenceRequired();
}
