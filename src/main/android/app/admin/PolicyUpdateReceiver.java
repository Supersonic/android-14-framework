package android.app.admin;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.p008os.Bundle;
import android.util.Log;
import java.util.Objects;
/* loaded from: classes.dex */
public abstract class PolicyUpdateReceiver extends BroadcastReceiver {
    public static final String ACTION_DEVICE_POLICY_CHANGED = "android.app.admin.action.DEVICE_POLICY_CHANGED";
    public static final String ACTION_DEVICE_POLICY_SET_RESULT = "android.app.admin.action.DEVICE_POLICY_SET_RESULT";
    public static final String EXTRA_ACCOUNT_TYPE = "android.app.admin.extra.ACCOUNT_TYPE";
    public static final String EXTRA_INTENT_FILTER = "android.app.admin.extra.INTENT_FILTER";
    public static final String EXTRA_PACKAGE_NAME = "android.app.admin.extra.PACKAGE_NAME";
    public static final String EXTRA_PERMISSION_NAME = "android.app.admin.extra.PERMISSION_NAME";
    public static final String EXTRA_POLICY_BUNDLE_KEY = "android.app.admin.extra.POLICY_BUNDLE_KEY";
    public static final String EXTRA_POLICY_KEY = "android.app.admin.extra.POLICY_KEY";
    public static final String EXTRA_POLICY_TARGET_USER_ID = "android.app.admin.extra.POLICY_TARGET_USER_ID";
    public static final String EXTRA_POLICY_UPDATE_RESULT_KEY = "android.app.admin.extra.POLICY_UPDATE_RESULT_KEY";
    private static String TAG = "PolicyUpdateReceiver";

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    @Override // android.content.BroadcastReceiver
    public final void onReceive(Context context, Intent intent) {
        char c;
        Objects.requireNonNull(intent.getAction());
        String action = intent.getAction();
        switch (action.hashCode()) {
            case -1756222069:
                if (action.equals(ACTION_DEVICE_POLICY_SET_RESULT)) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            case -1497094813:
                if (action.equals(ACTION_DEVICE_POLICY_CHANGED)) {
                    c = 1;
                    break;
                }
                c = 65535;
                break;
            default:
                c = 65535;
                break;
        }
        switch (c) {
            case 0:
                Log.m108i(TAG, "Received ACTION_DEVICE_POLICY_SET_RESULT");
                onPolicySetResult(context, getPolicyKey(intent), getPolicyExtraBundle(intent), getTargetUser(intent), getPolicyChangedReason(intent));
                return;
            case 1:
                Log.m108i(TAG, "Received ACTION_DEVICE_POLICY_CHANGED");
                onPolicyChanged(context, getPolicyKey(intent), getPolicyExtraBundle(intent), getTargetUser(intent), getPolicyChangedReason(intent));
                return;
            default:
                Log.m110e(TAG, "Unknown action received: " + intent.getAction());
                return;
        }
    }

    static String getPolicyKey(Intent intent) {
        if (!intent.hasExtra(EXTRA_POLICY_KEY)) {
            throw new IllegalArgumentException("PolicyKey has to be provided.");
        }
        return intent.getStringExtra(EXTRA_POLICY_KEY);
    }

    static Bundle getPolicyExtraBundle(Intent intent) {
        Bundle bundle = intent.getBundleExtra(EXTRA_POLICY_BUNDLE_KEY);
        return bundle == null ? new Bundle() : bundle;
    }

    static PolicyUpdateResult getPolicyChangedReason(Intent intent) {
        if (intent.hasExtra(EXTRA_POLICY_UPDATE_RESULT_KEY)) {
            int reasonCode = intent.getIntExtra(EXTRA_POLICY_UPDATE_RESULT_KEY, -1);
            return new PolicyUpdateResult(reasonCode);
        }
        throw new IllegalArgumentException("PolicyUpdateResult has to be provided.");
    }

    static TargetUser getTargetUser(Intent intent) {
        if (intent.hasExtra(EXTRA_POLICY_TARGET_USER_ID)) {
            int targetUserId = intent.getIntExtra(EXTRA_POLICY_TARGET_USER_ID, -1);
            return new TargetUser(targetUserId);
        }
        throw new IllegalArgumentException("TargetUser has to be provided.");
    }

    public void onPolicySetResult(Context context, String policyIdentifier, Bundle additionalPolicyParams, TargetUser targetUser, PolicyUpdateResult policyUpdateResult) {
    }

    public void onPolicyChanged(Context context, String policyIdentifier, Bundle additionalPolicyParams, TargetUser targetUser, PolicyUpdateResult policyUpdateResult) {
    }
}
