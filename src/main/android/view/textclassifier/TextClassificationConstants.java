package android.view.textclassifier;

import android.provider.DeviceConfig;
import com.android.internal.util.IndentingPrintWriter;
/* loaded from: classes4.dex */
public final class TextClassificationConstants {
    private static final String DEFAULT_TEXT_CLASSIFIER_SERVICE_PACKAGE_OVERRIDE = null;
    static final String GENERATE_LINKS_MAX_TEXT_LENGTH = "generate_links_max_text_length";
    private static final int GENERATE_LINKS_MAX_TEXT_LENGTH_DEFAULT = 100000;
    static final String LOCAL_TEXT_CLASSIFIER_ENABLED = "local_textclassifier_enabled";
    private static final boolean LOCAL_TEXT_CLASSIFIER_ENABLED_DEFAULT = true;
    private static final String MODEL_DARK_LAUNCH_ENABLED = "model_dark_launch_enabled";
    private static final boolean MODEL_DARK_LAUNCH_ENABLED_DEFAULT = false;
    private static final String SMART_LINKIFY_ENABLED = "smart_linkify_enabled";
    private static final boolean SMART_LINKIFY_ENABLED_DEFAULT = true;
    private static final String SMART_SELECTION_ENABLED = "smart_selection_enabled";
    private static final boolean SMART_SELECTION_ENABLED_DEFAULT = true;
    private static final String SMART_SELECTION_TRIM_DELTA = "smart_selection_trim_delta";
    private static final int SMART_SELECTION_TRIM_DELTA_DEFAULT = 120;
    private static final String SMART_SELECT_ANIMATION_ENABLED = "smart_select_animation_enabled";
    private static final boolean SMART_SELECT_ANIMATION_ENABLED_DEFAULT = true;
    private static final String SMART_TEXT_SHARE_ENABLED = "smart_text_share_enabled";
    private static final boolean SMART_TEXT_SHARE_ENABLED_DEFAULT = true;
    static final String SYSTEM_TEXT_CLASSIFIER_API_TIMEOUT_IN_SECOND = "system_textclassifier_api_timeout_in_second";
    private static final long SYSTEM_TEXT_CLASSIFIER_API_TIMEOUT_IN_SECOND_DEFAULT = 60;
    static final String SYSTEM_TEXT_CLASSIFIER_ENABLED = "system_textclassifier_enabled";
    private static final boolean SYSTEM_TEXT_CLASSIFIER_ENABLED_DEFAULT = true;
    static final String TEXT_CLASSIFIER_SERVICE_PACKAGE_OVERRIDE = "textclassifier_service_package_override";

    public String getTextClassifierServicePackageOverride() {
        return DeviceConfig.getString("textclassifier", TEXT_CLASSIFIER_SERVICE_PACKAGE_OVERRIDE, DEFAULT_TEXT_CLASSIFIER_SERVICE_PACKAGE_OVERRIDE);
    }

    public boolean isLocalTextClassifierEnabled() {
        return DeviceConfig.getBoolean("textclassifier", LOCAL_TEXT_CLASSIFIER_ENABLED, true);
    }

    public boolean isSystemTextClassifierEnabled() {
        return DeviceConfig.getBoolean("textclassifier", SYSTEM_TEXT_CLASSIFIER_ENABLED, true);
    }

    public boolean isModelDarkLaunchEnabled() {
        return DeviceConfig.getBoolean("textclassifier", MODEL_DARK_LAUNCH_ENABLED, false);
    }

    public boolean isSmartSelectionEnabled() {
        return DeviceConfig.getBoolean("textclassifier", SMART_SELECTION_ENABLED, true);
    }

    public boolean isSmartTextShareEnabled() {
        return DeviceConfig.getBoolean("textclassifier", SMART_TEXT_SHARE_ENABLED, true);
    }

    public boolean isSmartLinkifyEnabled() {
        return DeviceConfig.getBoolean("textclassifier", SMART_LINKIFY_ENABLED, true);
    }

    public boolean isSmartSelectionAnimationEnabled() {
        return DeviceConfig.getBoolean("textclassifier", SMART_SELECT_ANIMATION_ENABLED, true);
    }

    public int getGenerateLinksMaxTextLength() {
        return DeviceConfig.getInt("textclassifier", GENERATE_LINKS_MAX_TEXT_LENGTH, 100000);
    }

    public long getSystemTextClassifierApiTimeoutInSecond() {
        return DeviceConfig.getLong("textclassifier", SYSTEM_TEXT_CLASSIFIER_API_TIMEOUT_IN_SECOND, (long) SYSTEM_TEXT_CLASSIFIER_API_TIMEOUT_IN_SECOND_DEFAULT);
    }

    public int getSmartSelectionTrimDelta() {
        return DeviceConfig.getInt("textclassifier", SMART_SELECTION_TRIM_DELTA, 120);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void dump(IndentingPrintWriter pw) {
        pw.println("TextClassificationConstants:");
        pw.increaseIndent();
        pw.print(GENERATE_LINKS_MAX_TEXT_LENGTH, Integer.valueOf(getGenerateLinksMaxTextLength())).println();
        pw.print(LOCAL_TEXT_CLASSIFIER_ENABLED, Boolean.valueOf(isLocalTextClassifierEnabled())).println();
        pw.print(MODEL_DARK_LAUNCH_ENABLED, Boolean.valueOf(isModelDarkLaunchEnabled())).println();
        pw.print(SMART_LINKIFY_ENABLED, Boolean.valueOf(isSmartLinkifyEnabled())).println();
        pw.print(SMART_SELECT_ANIMATION_ENABLED, Boolean.valueOf(isSmartSelectionAnimationEnabled())).println();
        pw.print(SMART_SELECTION_ENABLED, Boolean.valueOf(isSmartSelectionEnabled())).println();
        pw.print(SMART_TEXT_SHARE_ENABLED, Boolean.valueOf(isSmartTextShareEnabled())).println();
        pw.print(SYSTEM_TEXT_CLASSIFIER_ENABLED, Boolean.valueOf(isSystemTextClassifierEnabled())).println();
        pw.print(TEXT_CLASSIFIER_SERVICE_PACKAGE_OVERRIDE, getTextClassifierServicePackageOverride()).println();
        pw.print(SYSTEM_TEXT_CLASSIFIER_API_TIMEOUT_IN_SECOND, Long.valueOf(getSystemTextClassifierApiTimeoutInSecond())).println();
        pw.print(SMART_SELECTION_TRIM_DELTA, Integer.valueOf(getSmartSelectionTrimDelta())).println();
        pw.decreaseIndent();
    }
}
