package android.app.admin;

import android.annotation.SystemApi;
import android.util.AndroidException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
@SystemApi
/* loaded from: classes.dex */
public class ProvisioningException extends AndroidException {
    public static final int ERROR_ADMIN_PACKAGE_INSTALLATION_FAILED = 3;
    public static final int ERROR_PRE_CONDITION_FAILED = 1;
    public static final int ERROR_PROFILE_CREATION_FAILED = 2;
    public static final int ERROR_REMOVE_NON_REQUIRED_APPS_FAILED = 6;
    public static final int ERROR_SETTING_PROFILE_OWNER_FAILED = 4;
    public static final int ERROR_SET_DEVICE_OWNER_FAILED = 7;
    public static final int ERROR_STARTING_PROFILE_FAILED = 5;
    public static final int ERROR_UNKNOWN = 0;
    private final int mProvisioningError;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface ProvisioningError {
    }

    public ProvisioningException(Exception cause, int provisioningError) {
        this(cause, provisioningError, null);
    }

    public ProvisioningException(Exception cause, int provisioningError, String errorMessage) {
        super(errorMessage, cause);
        this.mProvisioningError = provisioningError;
    }

    public int getProvisioningError() {
        return this.mProvisioningError;
    }
}
