package com.android.internal.accessibility.dialog;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.accessibilityservice.AccessibilityShortcutInfo;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.provider.Settings;
import android.text.BidiFormatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.internal.C4057R;
import com.android.internal.accessibility.AccessibilityShortcutController;
import com.android.internal.accessibility.util.AccessibilityUtils;
import com.android.internal.accessibility.util.ShortcutUtils;
import com.android.internal.p028os.RoSystemProperties;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.function.Predicate;
/* loaded from: classes4.dex */
public final class AccessibilityTargetHelper {
    private AccessibilityTargetHelper() {
    }

    public static List<AccessibilityTarget> getTargets(Context context, int shortcutType) {
        List<AccessibilityTarget> installedTargets = getInstalledTargets(context, shortcutType);
        AccessibilityManager am = (AccessibilityManager) context.getSystemService(Context.ACCESSIBILITY_SERVICE);
        List<String> assignedTargets = am.getAccessibilityShortcutTargets(shortcutType);
        List<AccessibilityTarget> results = new ArrayList<>();
        for (String assignedTarget : assignedTargets) {
            for (AccessibilityTarget installedTarget : installedTargets) {
                if (!"com.android.server.accessibility.MagnificationController".contentEquals(assignedTarget)) {
                    ComponentName assignedTargetComponentName = ComponentName.unflattenFromString(assignedTarget);
                    ComponentName targetComponentName = ComponentName.unflattenFromString(installedTarget.getId());
                    if (assignedTargetComponentName.equals(targetComponentName)) {
                        results.add(installedTarget);
                    }
                }
                if (assignedTarget.contentEquals(installedTarget.getId())) {
                    results.add(installedTarget);
                }
            }
        }
        return results;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static List<AccessibilityTarget> getInstalledTargets(Context context, int shortcutType) {
        List<AccessibilityTarget> targets = new ArrayList<>();
        targets.addAll(getAccessibilityFilteredTargets(context, shortcutType));
        targets.addAll(getAllowListingFeatureTargets(context, shortcutType));
        return targets;
    }

    private static List<AccessibilityTarget> getAccessibilityFilteredTargets(Context context, int shortcutType) {
        List<AccessibilityTarget> serviceTargets = getAccessibilityServiceTargets(context, shortcutType);
        List<AccessibilityTarget> activityTargets = getAccessibilityActivityTargets(context, shortcutType);
        for (final AccessibilityTarget activityTarget : activityTargets) {
            serviceTargets.removeIf(new Predicate() { // from class: com.android.internal.accessibility.dialog.AccessibilityTargetHelper$$ExternalSyntheticLambda0
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean arePackageNameAndLabelTheSame;
                    arePackageNameAndLabelTheSame = AccessibilityTargetHelper.arePackageNameAndLabelTheSame((AccessibilityTarget) obj, AccessibilityTarget.this);
                    return arePackageNameAndLabelTheSame;
                }
            });
        }
        List<AccessibilityTarget> targets = new ArrayList<>();
        targets.addAll(serviceTargets);
        targets.addAll(activityTargets);
        return targets;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean arePackageNameAndLabelTheSame(AccessibilityTarget serviceTarget, AccessibilityTarget activityTarget) {
        ComponentName serviceComponentName = ComponentName.unflattenFromString(serviceTarget.getId());
        ComponentName activityComponentName = ComponentName.unflattenFromString(activityTarget.getId());
        boolean isSamePackageName = activityComponentName.getPackageName().equals(serviceComponentName.getPackageName());
        boolean isSameLabel = activityTarget.getLabel().equals(serviceTarget.getLabel());
        return isSamePackageName && isSameLabel;
    }

    private static List<AccessibilityTarget> getAccessibilityServiceTargets(Context context, int shortcutType) {
        AccessibilityManager am = (AccessibilityManager) context.getSystemService(Context.ACCESSIBILITY_SERVICE);
        List<AccessibilityServiceInfo> installedServices = am.getInstalledAccessibilityServiceList();
        if (installedServices == null) {
            return Collections.emptyList();
        }
        List<AccessibilityTarget> targets = new ArrayList<>(installedServices.size());
        for (AccessibilityServiceInfo info : installedServices) {
            int targetSdk = info.getResolveInfo().serviceInfo.applicationInfo.targetSdkVersion;
            boolean hasRequestAccessibilityButtonFlag = (info.flags & 256) != 0;
            if (targetSdk > 29 || hasRequestAccessibilityButtonFlag || shortcutType != 0) {
                targets.add(createAccessibilityServiceTarget(context, shortcutType, info));
            }
        }
        return targets;
    }

    private static List<AccessibilityTarget> getAccessibilityActivityTargets(Context context, int shortcutType) {
        AccessibilityManager am = (AccessibilityManager) context.getSystemService(Context.ACCESSIBILITY_SERVICE);
        List<AccessibilityShortcutInfo> installedServices = am.getInstalledAccessibilityShortcutListAsUser(context, ActivityManager.getCurrentUser());
        if (installedServices == null) {
            return Collections.emptyList();
        }
        List<AccessibilityTarget> targets = new ArrayList<>(installedServices.size());
        for (AccessibilityShortcutInfo info : installedServices) {
            targets.add(new AccessibilityActivityTarget(context, shortcutType, info));
        }
        return targets;
    }

    private static List<AccessibilityTarget> getAllowListingFeatureTargets(Context context, int shortcutType) {
        List<AccessibilityTarget> targets = new ArrayList<>();
        InvisibleToggleAllowListingFeatureTarget magnification = new InvisibleToggleAllowListingFeatureTarget(context, shortcutType, ShortcutUtils.isShortcutContained(context, shortcutType, "com.android.server.accessibility.MagnificationController"), "com.android.server.accessibility.MagnificationController", context.getString(C4057R.string.accessibility_magnification_chooser_text), context.getDrawable(C4057R.C4058drawable.ic_accessibility_magnification), Settings.Secure.ACCESSIBILITY_DISPLAY_MAGNIFICATION_NAVBAR_ENABLED);
        targets.add(magnification);
        ToggleAllowListingFeatureTarget daltonizer = new ToggleAllowListingFeatureTarget(context, shortcutType, ShortcutUtils.isShortcutContained(context, shortcutType, AccessibilityShortcutController.DALTONIZER_COMPONENT_NAME.flattenToString()), AccessibilityShortcutController.DALTONIZER_COMPONENT_NAME.flattenToString(), context.getString(C4057R.string.color_correction_feature_name), context.getDrawable(C4057R.C4058drawable.ic_accessibility_color_correction), Settings.Secure.ACCESSIBILITY_DISPLAY_DALTONIZER_ENABLED);
        targets.add(daltonizer);
        ToggleAllowListingFeatureTarget colorInversion = new ToggleAllowListingFeatureTarget(context, shortcutType, ShortcutUtils.isShortcutContained(context, shortcutType, AccessibilityShortcutController.COLOR_INVERSION_COMPONENT_NAME.flattenToString()), AccessibilityShortcutController.COLOR_INVERSION_COMPONENT_NAME.flattenToString(), context.getString(C4057R.string.color_inversion_feature_name), context.getDrawable(C4057R.C4058drawable.ic_accessibility_color_inversion), Settings.Secure.ACCESSIBILITY_DISPLAY_INVERSION_ENABLED);
        targets.add(colorInversion);
        if (RoSystemProperties.SUPPORT_ONE_HANDED_MODE) {
            ToggleAllowListingFeatureTarget oneHandedMode = new ToggleAllowListingFeatureTarget(context, shortcutType, ShortcutUtils.isShortcutContained(context, shortcutType, AccessibilityShortcutController.ONE_HANDED_COMPONENT_NAME.flattenToString()), AccessibilityShortcutController.ONE_HANDED_COMPONENT_NAME.flattenToString(), context.getString(C4057R.string.one_handed_mode_feature_name), context.getDrawable(C4057R.C4058drawable.ic_accessibility_one_handed), Settings.Secure.ONE_HANDED_MODE_ACTIVATED);
            targets.add(oneHandedMode);
        }
        ToggleAllowListingFeatureTarget reduceBrightColors = new ToggleAllowListingFeatureTarget(context, shortcutType, ShortcutUtils.isShortcutContained(context, shortcutType, AccessibilityShortcutController.REDUCE_BRIGHT_COLORS_COMPONENT_NAME.flattenToString()), AccessibilityShortcutController.REDUCE_BRIGHT_COLORS_COMPONENT_NAME.flattenToString(), context.getString(C4057R.string.reduce_bright_colors_feature_name), context.getDrawable(C4057R.C4058drawable.ic_accessibility_reduce_bright_colors), Settings.Secure.REDUCE_BRIGHT_COLORS_ACTIVATED);
        targets.add(reduceBrightColors);
        InvisibleToggleAllowListingFeatureTarget hearingAids = new InvisibleToggleAllowListingFeatureTarget(context, shortcutType, ShortcutUtils.isShortcutContained(context, shortcutType, AccessibilityShortcutController.ACCESSIBILITY_HEARING_AIDS_COMPONENT_NAME.flattenToString()), AccessibilityShortcutController.ACCESSIBILITY_HEARING_AIDS_COMPONENT_NAME.flattenToString(), context.getString(C4057R.string.hearing_aids_feature_name), context.getDrawable(C4057R.C4058drawable.ic_accessibility_hearing_aid), null);
        targets.add(hearingAids);
        return targets;
    }

    private static AccessibilityTarget createAccessibilityServiceTarget(Context context, int shortcutType, AccessibilityServiceInfo info) {
        switch (AccessibilityUtils.getAccessibilityServiceFragmentType(info)) {
            case 0:
                return new VolumeShortcutToggleAccessibilityServiceTarget(context, shortcutType, info);
            case 1:
                return new InvisibleToggleAccessibilityServiceTarget(context, shortcutType, info);
            case 2:
                return new ToggleAccessibilityServiceTarget(context, shortcutType, info);
            default:
                throw new IllegalStateException("Unexpected fragment type");
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static View createEnableDialogContentView(Context context, final AccessibilityServiceTarget target, final View.OnClickListener allowListener, final View.OnClickListener denyListener) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View content = inflater.inflate(C4057R.layout.accessibility_enable_service_warning, (ViewGroup) null);
        ImageView dialogIcon = (ImageView) content.findViewById(C4057R.C4059id.accessibility_permissionDialog_icon);
        dialogIcon.setImageDrawable(target.getIcon());
        TextView dialogTitle = (TextView) content.findViewById(C4057R.C4059id.accessibility_permissionDialog_title);
        dialogTitle.setText(context.getString(C4057R.string.accessibility_enable_service_title, getServiceName(context, target.getLabel())));
        Button allowButton = (Button) content.findViewById(C4057R.C4059id.accessibility_permission_enable_allow_button);
        Button denyButton = (Button) content.findViewById(C4057R.C4059id.accessibility_permission_enable_deny_button);
        allowButton.setOnClickListener(new View.OnClickListener() { // from class: com.android.internal.accessibility.dialog.AccessibilityTargetHelper$$ExternalSyntheticLambda1
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                AccessibilityTargetHelper.lambda$createEnableDialogContentView$1(AccessibilityServiceTarget.this, allowListener, view);
            }
        });
        denyButton.setOnClickListener(new View.OnClickListener() { // from class: com.android.internal.accessibility.dialog.AccessibilityTargetHelper$$ExternalSyntheticLambda2
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                AccessibilityTargetHelper.lambda$createEnableDialogContentView$2(AccessibilityServiceTarget.this, denyListener, view);
            }
        });
        return content;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ void lambda$createEnableDialogContentView$1(AccessibilityServiceTarget target, View.OnClickListener allowListener, View view) {
        target.onCheckedChanged(true);
        allowListener.onClick(view);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ void lambda$createEnableDialogContentView$2(AccessibilityServiceTarget target, View.OnClickListener denyListener, View view) {
        target.onCheckedChanged(false);
        denyListener.onClick(view);
    }

    private static CharSequence getServiceName(Context context, CharSequence label) {
        Locale locale = context.getResources().getConfiguration().getLocales().get(0);
        return BidiFormatter.getInstance(locale).unicodeWrap(label);
    }
}
