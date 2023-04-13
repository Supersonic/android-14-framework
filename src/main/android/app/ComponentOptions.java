package android.app;

import android.p008os.Bundle;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
/* loaded from: classes.dex */
public class ComponentOptions {
    public static final String KEY_PENDING_INTENT_BACKGROUND_ACTIVITY_ALLOWED = "android.pendingIntent.backgroundActivityAllowed";
    public static final String KEY_PENDING_INTENT_BACKGROUND_ACTIVITY_ALLOWED_BY_PERMISSION = "android.pendingIntent.backgroundActivityAllowedByPermission";
    public static final int MODE_BACKGROUND_ACTIVITY_START_ALLOWED = 1;
    public static final int MODE_BACKGROUND_ACTIVITY_START_DENIED = 2;
    public static final int MODE_BACKGROUND_ACTIVITY_START_SYSTEM_DEFINED = 0;
    private Boolean mPendingIntentBalAllowed;
    private boolean mPendingIntentBalAllowedByPermission;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface BackgroundActivityStartMode {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ComponentOptions() {
        this.mPendingIntentBalAllowed = null;
        this.mPendingIntentBalAllowedByPermission = false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ComponentOptions(Bundle opts) {
        this.mPendingIntentBalAllowed = null;
        this.mPendingIntentBalAllowedByPermission = false;
        opts.setDefusable(true);
        boolean pendingIntentBalAllowedIsSetExplicitly = opts.containsKey(KEY_PENDING_INTENT_BACKGROUND_ACTIVITY_ALLOWED);
        if (pendingIntentBalAllowedIsSetExplicitly) {
            this.mPendingIntentBalAllowed = Boolean.valueOf(opts.getBoolean(KEY_PENDING_INTENT_BACKGROUND_ACTIVITY_ALLOWED));
        }
        setPendingIntentBackgroundActivityLaunchAllowedByPermission(opts.getBoolean(KEY_PENDING_INTENT_BACKGROUND_ACTIVITY_ALLOWED_BY_PERMISSION, false));
    }

    @Deprecated
    public void setPendingIntentBackgroundActivityLaunchAllowed(boolean allowed) {
        this.mPendingIntentBalAllowed = Boolean.valueOf(allowed);
    }

    @Deprecated
    public boolean isPendingIntentBackgroundActivityLaunchAllowed() {
        Boolean bool = this.mPendingIntentBalAllowed;
        if (bool == null) {
            return true;
        }
        return bool.booleanValue();
    }

    public ComponentOptions setPendingIntentBackgroundActivityStartMode(int state) {
        switch (state) {
            case 0:
                this.mPendingIntentBalAllowed = null;
                break;
            case 1:
                this.mPendingIntentBalAllowed = true;
                break;
            case 2:
                this.mPendingIntentBalAllowed = false;
                break;
            default:
                throw new IllegalArgumentException(state + " is not valid");
        }
        return this;
    }

    public int getPendingIntentBackgroundActivityStartMode() {
        Boolean bool = this.mPendingIntentBalAllowed;
        if (bool == null) {
            return 0;
        }
        if (bool.booleanValue()) {
            return 1;
        }
        return 2;
    }

    public void setPendingIntentBackgroundActivityLaunchAllowedByPermission(boolean allowed) {
        this.mPendingIntentBalAllowedByPermission = allowed;
    }

    public boolean isPendingIntentBackgroundActivityLaunchAllowedByPermission() {
        return this.mPendingIntentBalAllowedByPermission;
    }

    public Bundle toBundle() {
        Bundle b = new Bundle();
        Boolean bool = this.mPendingIntentBalAllowed;
        if (bool != null) {
            b.putBoolean(KEY_PENDING_INTENT_BACKGROUND_ACTIVITY_ALLOWED, bool.booleanValue());
        }
        boolean z = this.mPendingIntentBalAllowedByPermission;
        if (z) {
            b.putBoolean(KEY_PENDING_INTENT_BACKGROUND_ACTIVITY_ALLOWED_BY_PERMISSION, z);
        }
        return b;
    }

    public static ComponentOptions fromBundle(Bundle options) {
        if (options != null) {
            return new ComponentOptions(options);
        }
        return null;
    }
}
