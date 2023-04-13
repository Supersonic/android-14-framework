package android.view.autofill;

import android.content.Context;
import android.provider.DeviceConfig;
import android.text.TextUtils;
import android.util.ArraySet;
import com.android.internal.util.ArrayUtils;
import java.util.Arrays;
import java.util.Set;
import java.util.function.IntFunction;
import java.util.function.Predicate;
/* loaded from: classes4.dex */
public class AutofillFeatureFlags {
    public static final boolean DEFAULT_AUTOFILL_PCC_CLASSIFICATION_ENABLED = false;
    private static final boolean DEFAULT_CREDENTIAL_MANAGER_ENABLED = true;
    private static final boolean DEFAULT_CREDENTIAL_MANAGER_IGNORE_VIEWS = true;
    private static final boolean DEFAULT_CREDENTIAL_MANAGER_SUPPRESS_FILL_DIALOG = false;
    private static final boolean DEFAULT_CREDENTIAL_MANAGER_SUPPRESS_SAVE_DIALOG = false;
    private static final String DEFAULT_FILL_DIALOG_ENABLED_HINTS = "";
    private static final boolean DEFAULT_HAS_FILL_DIALOG_UI_FEATURE = false;
    public static final String DEVICE_CONFIG_AUGMENTED_SERVICE_IDLE_UNBIND_TIMEOUT = "augmented_service_idle_unbind_timeout";
    public static final String DEVICE_CONFIG_AUGMENTED_SERVICE_REQUEST_TIMEOUT = "augmented_service_request_timeout";
    public static final String DEVICE_CONFIG_AUTOFILL_COMPAT_MODE_ALLOWED_PACKAGES = "compat_mode_allowed_packages";
    public static final String DEVICE_CONFIG_AUTOFILL_CREDENTIAL_MANAGER_ENABLED = "autofill_credential_manager_enabled";
    public static final String DEVICE_CONFIG_AUTOFILL_CREDENTIAL_MANAGER_IGNORE_VIEWS = "autofill_credential_manager_ignore_views";
    public static final String DEVICE_CONFIG_AUTOFILL_CREDENTIAL_MANAGER_SUPPRESS_FILL_DIALOG = "autofill_credential_manager_suppress_fill_dialog";
    public static final String DEVICE_CONFIG_AUTOFILL_CREDENTIAL_MANAGER_SUPPRESS_SAVE_DIALOG = "autofill_credential_manager_suppress_save_dialog";
    public static final String DEVICE_CONFIG_AUTOFILL_DIALOG_ENABLED = "autofill_dialog_enabled";
    public static final String DEVICE_CONFIG_AUTOFILL_DIALOG_HINTS = "autofill_dialog_hints";
    public static final String DEVICE_CONFIG_AUTOFILL_PCC_CLASSIFICATION_ENABLED = "pcc_classification_enabled";
    public static final String DEVICE_CONFIG_AUTOFILL_PCC_FEATURE_PROVIDER_HINTS = "pcc_classification_hints";
    public static final String DEVICE_CONFIG_AUTOFILL_SMART_SUGGESTION_SUPPORTED_MODES = "smart_suggestion_supported_modes";
    public static final String DEVICE_CONFIG_AUTOFILL_TOOLTIP_SHOW_UP_DELAY = "autofill_inline_tooltip_first_show_delay";
    public static final String DEVICE_CONFIG_NON_AUTOFILLABLE_IME_ACTION_IDS = "non_autofillable_ime_action_ids";
    public static final String DEVICE_CONFIG_PACKAGE_DENYLIST_FOR_UNIMPORTANT_VIEW = "package_deny_list_for_unimportant_view";
    public static final String DEVICE_CONFIG_PCC_USE_FALLBACK = "pcc_use_fallback";
    public static final String DEVICE_CONFIG_PREFER_PROVIDER_OVER_PCC = "prefer_provider_over_pcc";
    public static final String DEVICE_CONFIG_TRIGGER_FILL_REQUEST_ON_UNIMPORTANT_VIEW = "trigger_fill_request_on_unimportant_view";
    private static final String DIALOG_HINTS_DELIMITER = ":";

    private AutofillFeatureFlags() {
    }

    public static boolean isFillDialogEnabled() {
        return DeviceConfig.getBoolean(Context.AUTOFILL_MANAGER_SERVICE, DEVICE_CONFIG_AUTOFILL_DIALOG_ENABLED, false);
    }

    public static String[] getFillDialogEnabledHints() {
        String dialogHints = DeviceConfig.getString(Context.AUTOFILL_MANAGER_SERVICE, DEVICE_CONFIG_AUTOFILL_DIALOG_HINTS, "");
        if (TextUtils.isEmpty(dialogHints)) {
            return new String[0];
        }
        return (String[]) ArrayUtils.filter(dialogHints.split(":"), new IntFunction() { // from class: android.view.autofill.AutofillFeatureFlags$$ExternalSyntheticLambda0
            @Override // java.util.function.IntFunction
            public final Object apply(int i) {
                return AutofillFeatureFlags.lambda$getFillDialogEnabledHints$0(i);
            }
        }, new Predicate() { // from class: android.view.autofill.AutofillFeatureFlags$$ExternalSyntheticLambda1
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return AutofillFeatureFlags.lambda$getFillDialogEnabledHints$1((String) obj);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ String[] lambda$getFillDialogEnabledHints$0(int x$0) {
        return new String[x$0];
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ boolean lambda$getFillDialogEnabledHints$1(String str) {
        return !TextUtils.isEmpty(str);
    }

    public static boolean isCredentialManagerEnabled() {
        return DeviceConfig.getBoolean(Context.AUTOFILL_MANAGER_SERVICE, DEVICE_CONFIG_AUTOFILL_CREDENTIAL_MANAGER_ENABLED, true);
    }

    public static boolean shouldIgnoreCredentialViews() {
        return isCredentialManagerEnabled() && DeviceConfig.getBoolean(Context.AUTOFILL_MANAGER_SERVICE, DEVICE_CONFIG_AUTOFILL_CREDENTIAL_MANAGER_IGNORE_VIEWS, true);
    }

    public static boolean isFillDialogDisabledForCredentialManager() {
        return isCredentialManagerEnabled() && DeviceConfig.getBoolean(Context.AUTOFILL_MANAGER_SERVICE, DEVICE_CONFIG_AUTOFILL_CREDENTIAL_MANAGER_SUPPRESS_FILL_DIALOG, false);
    }

    public static boolean isTriggerFillRequestOnUnimportantViewEnabled() {
        return DeviceConfig.getBoolean(Context.AUTOFILL_MANAGER_SERVICE, DEVICE_CONFIG_TRIGGER_FILL_REQUEST_ON_UNIMPORTANT_VIEW, false);
    }

    public static Set<String> getNonAutofillableImeActionIdSetFromFlag() {
        String mNonAutofillableImeActions = DeviceConfig.getString(Context.AUTOFILL_MANAGER_SERVICE, DEVICE_CONFIG_NON_AUTOFILLABLE_IME_ACTION_IDS, "");
        return new ArraySet(Arrays.asList(mNonAutofillableImeActions.split(",")));
    }

    public static String getDenylistStringFromFlag() {
        return DeviceConfig.getString(Context.AUTOFILL_MANAGER_SERVICE, DEVICE_CONFIG_PACKAGE_DENYLIST_FOR_UNIMPORTANT_VIEW, "");
    }

    public static boolean isAutofillPccClassificationEnabled() {
        return DeviceConfig.getBoolean(Context.AUTOFILL_MANAGER_SERVICE, DEVICE_CONFIG_AUTOFILL_PCC_CLASSIFICATION_ENABLED, false);
    }
}
