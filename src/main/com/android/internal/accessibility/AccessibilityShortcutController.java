package com.android.internal.accessibility;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.ActivityManager;
import android.app.ActivityThread;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.p001pm.PackageManager;
import android.database.ContentObserver;
import android.media.AudioAttributes;
import android.media.AudioSystem;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.p008os.Handler;
import android.p008os.Vibrator;
import android.provider.Settings;
import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Slog;
import android.view.Window;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityManager;
import android.widget.Toast;
import com.android.internal.C4057R;
import com.android.internal.accessibility.AccessibilityShortcutController;
import com.android.internal.accessibility.dialog.AccessibilityTarget;
import com.android.internal.accessibility.dialog.AccessibilityTargetHelper;
import com.android.internal.p028os.RoSystemProperties;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.function.pooled.PooledLambda;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Consumer;
/* loaded from: classes4.dex */
public class AccessibilityShortcutController {
    public static final String MAGNIFICATION_CONTROLLER_NAME = "com.android.server.accessibility.MagnificationController";
    private static final String TAG = "AccessibilityShortcutController";
    private static Map<ComponentName, FrameworkFeatureInfo> sFrameworkShortcutFeaturesMap;
    private AlertDialog mAlertDialog;
    private final Context mContext;
    private boolean mEnabledOnLockScreen;
    public FrameworkObjectProvider mFrameworkObjectProvider = new FrameworkObjectProvider();
    private final Handler mHandler;
    private boolean mIsShortcutEnabled;
    private int mUserId;
    private final UserSetupCompleteObserver mUserSetupCompleteObserver;
    public static final ComponentName COLOR_INVERSION_COMPONENT_NAME = new ComponentName("com.android.server.accessibility", "ColorInversion");
    public static final ComponentName DALTONIZER_COMPONENT_NAME = new ComponentName("com.android.server.accessibility", "Daltonizer");
    public static final ComponentName MAGNIFICATION_COMPONENT_NAME = new ComponentName("com.android.server.accessibility", "Magnification");
    public static final ComponentName ONE_HANDED_COMPONENT_NAME = new ComponentName("com.android.server.accessibility", "OneHandedMode");
    public static final ComponentName REDUCE_BRIGHT_COLORS_COMPONENT_NAME = new ComponentName("com.android.server.accessibility", "ReduceBrightColors");
    public static final ComponentName ACCESSIBILITY_BUTTON_COMPONENT_NAME = new ComponentName("com.android.server.accessibility", "AccessibilityButton");
    public static final ComponentName ACCESSIBILITY_HEARING_AIDS_COMPONENT_NAME = new ComponentName("com.android.server.accessibility", "HearingAids");
    public static final ComponentName COLOR_INVERSION_TILE_COMPONENT_NAME = new ComponentName("com.android.server.accessibility", "ColorInversionTile");
    public static final ComponentName DALTONIZER_TILE_COMPONENT_NAME = new ComponentName("com.android.server.accessibility", "ColorCorrectionTile");
    public static final ComponentName ONE_HANDED_TILE_COMPONENT_NAME = new ComponentName("com.android.server.accessibility", "OneHandedModeTile");
    public static final ComponentName REDUCE_BRIGHT_COLORS_TILE_SERVICE_COMPONENT_NAME = new ComponentName("com.android.server.accessibility", "ReduceBrightColorsTile");
    private static final AudioAttributes VIBRATION_ATTRIBUTES = new AudioAttributes.Builder().setContentType(4).setUsage(11).build();

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes4.dex */
    private @interface DialogStatus {
        public static final int NOT_SHOWN = 0;
        public static final int SHOWN = 1;
    }

    public static Map<ComponentName, FrameworkFeatureInfo> getFrameworkShortcutFeaturesMap() {
        if (sFrameworkShortcutFeaturesMap == null) {
            Map<ComponentName, FrameworkFeatureInfo> featuresMap = new ArrayMap<>(4);
            featuresMap.put(COLOR_INVERSION_COMPONENT_NAME, new ToggleableFrameworkFeatureInfo(Settings.Secure.ACCESSIBILITY_DISPLAY_INVERSION_ENABLED, "1", AudioSystem.LEGACY_REMOTE_SUBMIX_ADDRESS, C4057R.string.color_inversion_feature_name));
            featuresMap.put(DALTONIZER_COMPONENT_NAME, new ToggleableFrameworkFeatureInfo(Settings.Secure.ACCESSIBILITY_DISPLAY_DALTONIZER_ENABLED, "1", AudioSystem.LEGACY_REMOTE_SUBMIX_ADDRESS, C4057R.string.color_correction_feature_name));
            if (RoSystemProperties.SUPPORT_ONE_HANDED_MODE) {
                featuresMap.put(ONE_HANDED_COMPONENT_NAME, new ToggleableFrameworkFeatureInfo(Settings.Secure.ONE_HANDED_MODE_ACTIVATED, "1", AudioSystem.LEGACY_REMOTE_SUBMIX_ADDRESS, C4057R.string.one_handed_mode_feature_name));
            }
            featuresMap.put(REDUCE_BRIGHT_COLORS_COMPONENT_NAME, new ToggleableFrameworkFeatureInfo(Settings.Secure.REDUCE_BRIGHT_COLORS_ACTIVATED, "1", AudioSystem.LEGACY_REMOTE_SUBMIX_ADDRESS, C4057R.string.reduce_bright_colors_feature_name));
            featuresMap.put(ACCESSIBILITY_HEARING_AIDS_COMPONENT_NAME, new LaunchableFrameworkFeatureInfo(C4057R.string.hearing_aids_feature_name));
            sFrameworkShortcutFeaturesMap = Collections.unmodifiableMap(featuresMap);
        }
        return sFrameworkShortcutFeaturesMap;
    }

    public AccessibilityShortcutController(Context context, Handler handler, int initialUserId) {
        this.mContext = context;
        this.mHandler = handler;
        this.mUserId = initialUserId;
        this.mUserSetupCompleteObserver = new UserSetupCompleteObserver(handler, initialUserId);
        ContentObserver co = new ContentObserver(handler) { // from class: com.android.internal.accessibility.AccessibilityShortcutController.1
            {
                AccessibilityShortcutController.this = this;
            }

            @Override // android.database.ContentObserver
            public void onChange(boolean selfChange, Collection<Uri> uris, int flags, int userId) {
                if (userId == AccessibilityShortcutController.this.mUserId) {
                    AccessibilityShortcutController.this.onSettingsChanged();
                }
            }
        };
        context.getContentResolver().registerContentObserver(Settings.Secure.getUriFor(Settings.Secure.ACCESSIBILITY_SHORTCUT_TARGET_SERVICE), false, co, -1);
        context.getContentResolver().registerContentObserver(Settings.Secure.getUriFor(Settings.Secure.ACCESSIBILITY_SHORTCUT_ON_LOCK_SCREEN), false, co, -1);
        context.getContentResolver().registerContentObserver(Settings.Secure.getUriFor(Settings.Secure.ACCESSIBILITY_SHORTCUT_DIALOG_SHOWN), false, co, -1);
        setCurrentUser(this.mUserId);
    }

    public void setCurrentUser(int currentUserId) {
        this.mUserId = currentUserId;
        onSettingsChanged();
        this.mUserSetupCompleteObserver.onUserSwitched(currentUserId);
    }

    public boolean isAccessibilityShortcutAvailable(boolean phoneLocked) {
        return this.mIsShortcutEnabled && (!phoneLocked || this.mEnabledOnLockScreen);
    }

    public void onSettingsChanged() {
        boolean hasShortcutTarget = hasShortcutTarget();
        ContentResolver cr = this.mContext.getContentResolver();
        int dialogAlreadyShown = Settings.Secure.getIntForUser(cr, Settings.Secure.ACCESSIBILITY_SHORTCUT_DIALOG_SHOWN, 0, this.mUserId);
        this.mEnabledOnLockScreen = Settings.Secure.getIntForUser(cr, Settings.Secure.ACCESSIBILITY_SHORTCUT_ON_LOCK_SCREEN, dialogAlreadyShown, this.mUserId) == 1;
        this.mIsShortcutEnabled = hasShortcutTarget;
    }

    public void performAccessibilityShortcut() {
        Slog.m98d(TAG, "Accessibility shortcut activated");
        ContentResolver cr = this.mContext.getContentResolver();
        int userId = ActivityManager.getCurrentUser();
        Vibrator vibrator = (Vibrator) this.mContext.getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null && vibrator.hasVibrator()) {
            long[] vibePattern = ArrayUtils.convertToLongArray(this.mContext.getResources().getIntArray(C4057R.array.config_longPressVibePattern));
            vibrator.vibrate(vibePattern, -1, VIBRATION_ATTRIBUTES);
        }
        if (shouldShowDialog()) {
            AlertDialog createShortcutWarningDialog = createShortcutWarningDialog(userId);
            this.mAlertDialog = createShortcutWarningDialog;
            if (createShortcutWarningDialog == null) {
                return;
            }
            if (!performTtsPrompt(createShortcutWarningDialog)) {
                playNotificationTone();
            }
            Window w = this.mAlertDialog.getWindow();
            WindowManager.LayoutParams attr = w.getAttributes();
            attr.type = 2009;
            w.setAttributes(attr);
            this.mAlertDialog.show();
            Settings.Secure.putIntForUser(cr, Settings.Secure.ACCESSIBILITY_SHORTCUT_DIALOG_SHOWN, 1, userId);
            return;
        }
        playNotificationTone();
        AlertDialog alertDialog = this.mAlertDialog;
        if (alertDialog != null) {
            alertDialog.dismiss();
            this.mAlertDialog = null;
        }
        showToast();
        this.mFrameworkObjectProvider.getAccessibilityManagerInstance(this.mContext).performAccessibilityShortcut();
    }

    private boolean shouldShowDialog() {
        if (hasFeatureLeanback()) {
            return false;
        }
        ContentResolver cr = this.mContext.getContentResolver();
        int userId = ActivityManager.getCurrentUser();
        int dialogAlreadyShown = Settings.Secure.getIntForUser(cr, Settings.Secure.ACCESSIBILITY_SHORTCUT_DIALOG_SHOWN, 0, userId);
        return dialogAlreadyShown == 0;
    }

    private void showToast() {
        int i;
        AccessibilityServiceInfo serviceInfo = getInfoForTargetService();
        if (serviceInfo == null) {
            return;
        }
        String serviceName = getShortcutFeatureDescription(false);
        if (serviceName == null) {
            return;
        }
        boolean requestA11yButton = (serviceInfo.flags & 256) != 0;
        boolean isServiceEnabled = isServiceEnabled(serviceInfo);
        if (serviceInfo.getResolveInfo().serviceInfo.applicationInfo.targetSdkVersion > 29 && requestA11yButton && isServiceEnabled) {
            return;
        }
        Context context = this.mContext;
        if (isServiceEnabled) {
            i = C4057R.string.accessibility_shortcut_disabling_service;
        } else {
            i = C4057R.string.accessibility_shortcut_enabling_service;
        }
        String toastMessageFormatString = context.getString(i);
        String toastMessage = String.format(toastMessageFormatString, serviceName);
        Toast warningToast = this.mFrameworkObjectProvider.makeToastFromText(this.mContext, toastMessage, 1);
        warningToast.show();
    }

    private AlertDialog createShortcutWarningDialog(final int userId) {
        List<AccessibilityTarget> targets = AccessibilityTargetHelper.getTargets(this.mContext, 1);
        if (targets.size() == 0) {
            return null;
        }
        FrameworkObjectProvider frameworkObjectProvider = this.mFrameworkObjectProvider;
        AlertDialog alertDialog = frameworkObjectProvider.getAlertDialogBuilder(frameworkObjectProvider.getSystemUiContext()).setTitle(getShortcutWarningTitle(targets)).setMessage(getShortcutWarningMessage(targets)).setCancelable(false).setNegativeButton(C4057R.string.accessibility_shortcut_on, (DialogInterface.OnClickListener) null).setPositiveButton(C4057R.string.accessibility_shortcut_off, new DialogInterface.OnClickListener() { // from class: com.android.internal.accessibility.AccessibilityShortcutController$$ExternalSyntheticLambda0
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                AccessibilityShortcutController.this.lambda$createShortcutWarningDialog$0(userId, dialogInterface, i);
            }
        }).setOnCancelListener(new DialogInterface.OnCancelListener() { // from class: com.android.internal.accessibility.AccessibilityShortcutController$$ExternalSyntheticLambda1
            @Override // android.content.DialogInterface.OnCancelListener
            public final void onCancel(DialogInterface dialogInterface) {
                AccessibilityShortcutController.this.lambda$createShortcutWarningDialog$1(userId, dialogInterface);
            }
        }).create();
        return alertDialog;
    }

    public /* synthetic */ void lambda$createShortcutWarningDialog$0(int userId, DialogInterface d, int which) {
        Settings.Secure.putStringForUser(this.mContext.getContentResolver(), Settings.Secure.ACCESSIBILITY_SHORTCUT_TARGET_SERVICE, "", userId);
        Settings.Secure.putIntForUser(this.mContext.getContentResolver(), Settings.Secure.ACCESSIBILITY_SHORTCUT_DIALOG_SHOWN, 0, userId);
    }

    public /* synthetic */ void lambda$createShortcutWarningDialog$1(int userId, DialogInterface d) {
        Settings.Secure.putIntForUser(this.mContext.getContentResolver(), Settings.Secure.ACCESSIBILITY_SHORTCUT_DIALOG_SHOWN, 0, userId);
    }

    private String getShortcutWarningTitle(List<AccessibilityTarget> targets) {
        if (targets.size() == 1) {
            return this.mContext.getString(C4057R.string.accessibility_shortcut_single_service_warning_title, targets.get(0).getLabel());
        }
        return this.mContext.getString(C4057R.string.accessibility_shortcut_multiple_service_warning_title);
    }

    private String getShortcutWarningMessage(List<AccessibilityTarget> targets) {
        if (targets.size() == 1) {
            return this.mContext.getString(C4057R.string.accessibility_shortcut_single_service_warning, targets.get(0).getLabel());
        }
        StringBuilder sb = new StringBuilder();
        for (AccessibilityTarget target : targets) {
            sb.append(this.mContext.getString(C4057R.string.accessibility_shortcut_multiple_service_list, target.getLabel()));
        }
        return this.mContext.getString(C4057R.string.accessibility_shortcut_multiple_service_warning, sb.toString());
    }

    private AccessibilityServiceInfo getInfoForTargetService() {
        ComponentName targetComponentName = getShortcutTargetComponentName();
        if (targetComponentName == null) {
            return null;
        }
        AccessibilityManager accessibilityManager = this.mFrameworkObjectProvider.getAccessibilityManagerInstance(this.mContext);
        return accessibilityManager.getInstalledServiceInfoWithComponentName(targetComponentName);
    }

    private String getShortcutFeatureDescription(boolean includeSummary) {
        ComponentName targetComponentName = getShortcutTargetComponentName();
        if (targetComponentName == null) {
            return null;
        }
        FrameworkFeatureInfo frameworkFeatureInfo = getFrameworkShortcutFeaturesMap().get(targetComponentName);
        if (frameworkFeatureInfo != null) {
            return frameworkFeatureInfo.getLabel(this.mContext);
        }
        AccessibilityServiceInfo serviceInfo = this.mFrameworkObjectProvider.getAccessibilityManagerInstance(this.mContext).getInstalledServiceInfoWithComponentName(targetComponentName);
        if (serviceInfo == null) {
            return null;
        }
        PackageManager pm = this.mContext.getPackageManager();
        String label = serviceInfo.getResolveInfo().loadLabel(pm).toString();
        CharSequence summary = serviceInfo.loadSummary(pm);
        if (!includeSummary || TextUtils.isEmpty(summary)) {
            return label;
        }
        return String.format("%s\n%s", label, summary);
    }

    private boolean isServiceEnabled(AccessibilityServiceInfo serviceInfo) {
        AccessibilityManager accessibilityManager = this.mFrameworkObjectProvider.getAccessibilityManagerInstance(this.mContext);
        return accessibilityManager.getEnabledAccessibilityServiceList(-1).contains(serviceInfo);
    }

    public boolean hasFeatureLeanback() {
        return this.mContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_LEANBACK);
    }

    public void playNotificationTone() {
        int audioAttributesUsage;
        if (hasFeatureLeanback()) {
            audioAttributesUsage = 11;
        } else {
            audioAttributesUsage = 10;
        }
        Uri ringtoneUri = Uri.parse("file://" + this.mContext.getString(C4057R.string.config_defaultAccessibilityNotificationSound));
        Ringtone tone = this.mFrameworkObjectProvider.getRingtone(this.mContext, ringtoneUri);
        if (tone == null) {
            tone = this.mFrameworkObjectProvider.getRingtone(this.mContext, Settings.System.DEFAULT_NOTIFICATION_URI);
        }
        if (tone != null) {
            tone.setAudioAttributes(new AudioAttributes.Builder().setUsage(audioAttributesUsage).build());
            tone.play();
        }
    }

    private boolean performTtsPrompt(AlertDialog alertDialog) {
        String serviceName = getShortcutFeatureDescription(false);
        AccessibilityServiceInfo serviceInfo = getInfoForTargetService();
        if (TextUtils.isEmpty(serviceName) || serviceInfo == null || (serviceInfo.flags & 1024) == 0) {
            return false;
        }
        final TtsPrompt tts = new TtsPrompt(serviceName);
        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: com.android.internal.accessibility.AccessibilityShortcutController$$ExternalSyntheticLambda2
            @Override // android.content.DialogInterface.OnDismissListener
            public final void onDismiss(DialogInterface dialogInterface) {
                AccessibilityShortcutController.TtsPrompt.this.dismiss();
            }
        });
        return true;
    }

    private boolean hasShortcutTarget() {
        String shortcutTargets = Settings.Secure.getStringForUser(this.mContext.getContentResolver(), Settings.Secure.ACCESSIBILITY_SHORTCUT_TARGET_SERVICE, this.mUserId);
        if (shortcutTargets == null) {
            shortcutTargets = this.mContext.getString(C4057R.string.config_defaultAccessibilityService);
        }
        return !TextUtils.isEmpty(shortcutTargets);
    }

    private ComponentName getShortcutTargetComponentName() {
        List<String> shortcutTargets = this.mFrameworkObjectProvider.getAccessibilityManagerInstance(this.mContext).getAccessibilityShortcutTargets(1);
        if (shortcutTargets.size() != 1) {
            return null;
        }
        return ComponentName.unflattenFromString(shortcutTargets.get(0));
    }

    /* loaded from: classes4.dex */
    public class TtsPrompt implements TextToSpeech.OnInitListener {
        private static final int RETRY_MILLIS = 1000;
        private boolean mDismiss;
        private final CharSequence mText;
        private TextToSpeech mTts;
        private int mRetryCount = 3;
        private boolean mLanguageReady = false;

        TtsPrompt(String serviceName) {
            AccessibilityShortcutController.this = r4;
            this.mText = r4.mContext.getString(C4057R.string.accessibility_shortcut_spoken_feedback, serviceName);
            this.mTts = r4.mFrameworkObjectProvider.getTextToSpeech(r4.mContext, this);
        }

        public void dismiss() {
            this.mDismiss = true;
            AccessibilityShortcutController.this.mHandler.sendMessage(PooledLambda.obtainMessage(new Consumer() { // from class: com.android.internal.accessibility.AccessibilityShortcutController$TtsPrompt$$ExternalSyntheticLambda0
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    ((TextToSpeech) obj).shutdown();
                }
            }, this.mTts));
        }

        @Override // android.speech.tts.TextToSpeech.OnInitListener
        public void onInit(int status) {
            if (status != 0) {
                Slog.m98d(AccessibilityShortcutController.TAG, "Tts init fail, status=" + Integer.toString(status));
                AccessibilityShortcutController.this.playNotificationTone();
                return;
            }
            AccessibilityShortcutController.this.mHandler.sendMessage(PooledLambda.obtainMessage(new C4066xe6764ed3(), this));
        }

        public void play() {
            if (this.mDismiss) {
                return;
            }
            int status = this.mTts.speak(this.mText, 0, null, null);
            if (status != 0) {
                Slog.m98d(AccessibilityShortcutController.TAG, "Tts play fail");
                AccessibilityShortcutController.this.playNotificationTone();
            }
        }

        public void waitForTtsReady() {
            if (this.mDismiss) {
                return;
            }
            boolean voiceDataInstalled = false;
            if (!this.mLanguageReady) {
                int status = this.mTts.setLanguage(Locale.getDefault());
                this.mLanguageReady = (status == -1 || status == -2) ? false : true;
            }
            if (this.mLanguageReady) {
                Voice voice = this.mTts.getVoice();
                if (voice != null && voice.getFeatures() != null && !voice.getFeatures().contains(TextToSpeech.Engine.KEY_FEATURE_NOT_INSTALLED)) {
                    voiceDataInstalled = true;
                }
                if (voiceDataInstalled) {
                    AccessibilityShortcutController.this.mHandler.sendMessage(PooledLambda.obtainMessage(new Consumer() { // from class: com.android.internal.accessibility.AccessibilityShortcutController$TtsPrompt$$ExternalSyntheticLambda2
                        @Override // java.util.function.Consumer
                        public final void accept(Object obj) {
                            ((AccessibilityShortcutController.TtsPrompt) obj).play();
                        }
                    }, this));
                    return;
                }
            }
            int i = this.mRetryCount;
            if (i == 0) {
                Slog.m98d(AccessibilityShortcutController.TAG, "Tts not ready to speak.");
                AccessibilityShortcutController.this.playNotificationTone();
                return;
            }
            this.mRetryCount = i - 1;
            AccessibilityShortcutController.this.mHandler.sendMessageDelayed(PooledLambda.obtainMessage(new C4066xe6764ed3(), this), 1000L);
        }
    }

    /* loaded from: classes4.dex */
    public class UserSetupCompleteObserver extends ContentObserver {
        private boolean mIsRegistered;
        private int mUserId;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        UserSetupCompleteObserver(Handler handler, int userId) {
            super(handler);
            AccessibilityShortcutController.this = r1;
            this.mIsRegistered = false;
            this.mUserId = userId;
            if (!isUserSetupComplete()) {
                registerObserver();
            }
        }

        private boolean isUserSetupComplete() {
            return Settings.Secure.getIntForUser(AccessibilityShortcutController.this.mContext.getContentResolver(), Settings.Secure.USER_SETUP_COMPLETE, 0, this.mUserId) == 1;
        }

        private void registerObserver() {
            if (this.mIsRegistered) {
                return;
            }
            AccessibilityShortcutController.this.mContext.getContentResolver().registerContentObserver(Settings.Secure.getUriFor(Settings.Secure.USER_SETUP_COMPLETE), false, this, this.mUserId);
            this.mIsRegistered = true;
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean selfChange) {
            if (isUserSetupComplete()) {
                unregisterObserver();
                setEmptyShortcutTargetIfNeeded();
            }
        }

        private void unregisterObserver() {
            if (!this.mIsRegistered) {
                return;
            }
            AccessibilityShortcutController.this.mContext.getContentResolver().unregisterContentObserver(this);
            this.mIsRegistered = false;
        }

        private void setEmptyShortcutTargetIfNeeded() {
            if (AccessibilityShortcutController.this.hasFeatureLeanback()) {
                return;
            }
            ContentResolver contentResolver = AccessibilityShortcutController.this.mContext.getContentResolver();
            String shortcutTargets = Settings.Secure.getStringForUser(contentResolver, Settings.Secure.ACCESSIBILITY_SHORTCUT_TARGET_SERVICE, this.mUserId);
            if (shortcutTargets != null) {
                return;
            }
            String defaultShortcutTarget = AccessibilityShortcutController.this.mContext.getString(C4057R.string.config_defaultAccessibilityService);
            List<AccessibilityServiceInfo> enabledServices = AccessibilityShortcutController.this.mFrameworkObjectProvider.getAccessibilityManagerInstance(AccessibilityShortcutController.this.mContext).getEnabledAccessibilityServiceList(-1);
            for (int i = enabledServices.size() - 1; i >= 0; i--) {
                if (TextUtils.equals(defaultShortcutTarget, enabledServices.get(i).getId())) {
                    return;
                }
            }
            Settings.Secure.putStringForUser(contentResolver, Settings.Secure.ACCESSIBILITY_SHORTCUT_TARGET_SERVICE, "", this.mUserId);
        }

        void onUserSwitched(int userId) {
            if (this.mUserId == userId) {
                return;
            }
            unregisterObserver();
            this.mUserId = userId;
            if (!isUserSetupComplete()) {
                registerObserver();
            }
        }
    }

    /* loaded from: classes4.dex */
    public static abstract class FrameworkFeatureInfo {
        private final int mLabelStringResourceId;
        private final String mSettingKey;
        private final String mSettingOffValue;
        private final String mSettingOnValue;

        FrameworkFeatureInfo(String settingKey, String settingOnValue, String settingOffValue, int labelStringResourceId) {
            this.mSettingKey = settingKey;
            this.mSettingOnValue = settingOnValue;
            this.mSettingOffValue = settingOffValue;
            this.mLabelStringResourceId = labelStringResourceId;
        }

        public String getSettingKey() {
            return this.mSettingKey;
        }

        public String getSettingOnValue() {
            return this.mSettingOnValue;
        }

        public String getSettingOffValue() {
            return this.mSettingOffValue;
        }

        public String getLabel(Context context) {
            return context.getString(this.mLabelStringResourceId);
        }
    }

    /* loaded from: classes4.dex */
    public static class ToggleableFrameworkFeatureInfo extends FrameworkFeatureInfo {
        ToggleableFrameworkFeatureInfo(String settingKey, String settingOnValue, String settingOffValue, int labelStringResourceId) {
            super(settingKey, settingOnValue, settingOffValue, labelStringResourceId);
        }
    }

    /* loaded from: classes4.dex */
    public static class LaunchableFrameworkFeatureInfo extends FrameworkFeatureInfo {
        LaunchableFrameworkFeatureInfo(int labelStringResourceId) {
            super(null, null, null, labelStringResourceId);
        }
    }

    /* loaded from: classes4.dex */
    public static class FrameworkObjectProvider {
        public AccessibilityManager getAccessibilityManagerInstance(Context context) {
            return AccessibilityManager.getInstance(context);
        }

        public AlertDialog.Builder getAlertDialogBuilder(Context context) {
            boolean inNightMode = (context.getResources().getConfiguration().uiMode & 48) == 32;
            int themeId = inNightMode ? 16974545 : 16974546;
            return new AlertDialog.Builder(context, themeId);
        }

        public Toast makeToastFromText(Context context, CharSequence charSequence, int duration) {
            return Toast.makeText(context, charSequence, duration);
        }

        public Context getSystemUiContext() {
            return ActivityThread.currentActivityThread().getSystemUiContext();
        }

        public TextToSpeech getTextToSpeech(Context ctx, TextToSpeech.OnInitListener listener) {
            return new TextToSpeech(ctx, listener);
        }

        public Ringtone getRingtone(Context ctx, Uri uri) {
            return RingtoneManager.getRingtone(ctx, uri);
        }
    }
}
