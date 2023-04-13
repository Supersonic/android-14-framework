package android.app.admin;

import java.util.Objects;
/* loaded from: classes.dex */
public final class DevicePolicyIdentifiers {
    public static final String ACCOUNT_MANAGEMENT_DISABLED_POLICY = "accountManagementDisabled";
    public static final String APPLICATION_HIDDEN_POLICY = "applicationHidden";
    public static final String APPLICATION_RESTRICTIONS_POLICY = "applicationRestrictions";
    public static final String AUTO_TIMEZONE_POLICY = "autoTimezone";
    public static final String AUTO_TIME_POLICY = "autoTime";
    public static final String BACKUP_SERVICE_POLICY = "backupService";
    public static final String CAMERA_DISABLED_POLICY = "cameraDisabled";
    public static final String CROSS_PROFILE_INTENT_FILTER_POLICY = "crossProfileIntentFilter";
    public static final String CROSS_PROFILE_WIDGET_PROVIDER_POLICY = "crossProfileWidgetProvider";
    public static final String KEYGUARD_DISABLED_FEATURES_POLICY = "keyguardDisabledFeatures";
    public static final String LOCK_TASK_POLICY = "lockTask";
    public static final String PACKAGES_SUSPENDED_POLICY = "packagesSuspended";
    public static final String PACKAGE_UNINSTALL_BLOCKED_POLICY = "packageUninstallBlocked";
    public static final String PERMISSION_GRANT_POLICY = "permissionGrant";
    public static final String PERMITTED_INPUT_METHODS_POLICY = "permittedInputMethods";
    public static final String PERSISTENT_PREFERRED_ACTIVITY_POLICY = "persistentPreferredActivity";
    public static final String PERSONAL_APPS_SUSPENDED_POLICY = "personalAppsSuspended";
    public static final String RESET_PASSWORD_TOKEN_POLICY = "resetPasswordToken";
    public static final String SCREEN_CAPTURE_DISABLED_POLICY = "screenCaptureDisabled";
    public static final String STATUS_BAR_DISABLED_POLICY = "statusBarDisabled";
    public static final String TRUST_AGENT_CONFIGURATION_POLICY = "trustAgentConfiguration";
    public static final String USER_CONTROL_DISABLED_PACKAGES_POLICY = "userControlDisabledPackages";
    public static final String USER_RESTRICTION_PREFIX = "userRestriction_";

    private DevicePolicyIdentifiers() {
    }

    public static String getIdentifierForUserRestriction(String restriction) {
        Objects.requireNonNull(restriction);
        return USER_RESTRICTION_PREFIX + restriction;
    }
}
