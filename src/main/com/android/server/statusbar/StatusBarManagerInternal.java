package com.android.server.statusbar;

import android.app.ITransientNotificationCallback;
import android.hardware.fingerprint.IUdfpsRefreshRateRequestCallback;
import android.os.Bundle;
import android.os.IBinder;
import com.android.internal.statusbar.LetterboxDetails;
import com.android.internal.view.AppearanceRegion;
import com.android.server.notification.NotificationDelegate;
/* loaded from: classes2.dex */
public interface StatusBarManagerInternal {
    void abortTransient(int i, int i2);

    void appTransitionCancelled(int i);

    void appTransitionFinished(int i);

    void appTransitionPending(int i);

    void appTransitionStarting(int i, long j, long j2);

    void cancelPreloadRecentApps();

    void collapsePanels();

    void dismissKeyboardShortcutsMenu();

    void enterStageSplitFromRunningApp(boolean z);

    void goToFullscreenFromSplit();

    void hideRecentApps(boolean z, boolean z2);

    void hideToast(String str, IBinder iBinder);

    void onCameraLaunchGestureDetected(int i);

    void onDisplayReady(int i);

    void onEmergencyActionLaunchGestureDetected();

    void onProposedRotationChanged(int i, boolean z);

    void onRecentsAnimationStateChanged(boolean z);

    void onSystemBarAttributesChanged(int i, int i2, AppearanceRegion[] appearanceRegionArr, boolean z, int i3, int i4, String str, LetterboxDetails[] letterboxDetailsArr);

    void preloadRecentApps();

    boolean requestWindowMagnificationConnection(boolean z);

    void setCurrentUser(int i);

    void setDisableFlags(int i, int i2, String str);

    void setIcon(String str, String str2, int i, int i2, String str3);

    void setIconVisibility(String str, boolean z);

    void setImeWindowStatus(int i, IBinder iBinder, int i2, int i3, boolean z);

    void setNavigationBarLumaSamplingEnabled(int i, boolean z);

    void setNotificationDelegate(NotificationDelegate notificationDelegate);

    void setTopAppHidesStatusBar(boolean z);

    void setUdfpsRefreshRateCallback(IUdfpsRefreshRateRequestCallback iUdfpsRefreshRateRequestCallback);

    void setWindowState(int i, int i2, int i3);

    void showAssistDisclosure();

    void showChargingAnimation(int i);

    void showMediaOutputSwitcher(String str);

    void showPictureInPictureMenu();

    void showRearDisplayDialog(int i);

    void showRecentApps(boolean z);

    void showScreenPinningRequest(int i);

    boolean showShutdownUi(boolean z, String str);

    void showToast(int i, String str, IBinder iBinder, CharSequence charSequence, IBinder iBinder2, int i2, ITransientNotificationCallback iTransientNotificationCallback, int i3);

    void showTransient(int i, int i2, boolean z);

    void startAssist(Bundle bundle);

    void toggleKeyboardShortcutsMenu(int i);

    void toggleRecentApps();

    void toggleTaskbar();
}
