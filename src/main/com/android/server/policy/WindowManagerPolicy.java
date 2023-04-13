package com.android.server.policy;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Slog;
import android.util.proto.ProtoOutputStream;
import android.view.Display;
import android.view.IDisplayFoldListener;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.view.WindowManagerPolicyConstants;
import com.android.internal.policy.IKeyguardDismissCallback;
import com.android.internal.policy.IShortcutService;
import com.android.server.p014wm.DisplayRotation;
import com.android.server.p014wm.StartingSurfaceController;
import java.io.PrintWriter;
/* loaded from: classes2.dex */
public interface WindowManagerPolicy extends WindowManagerPolicyConstants {

    /* loaded from: classes2.dex */
    public interface DisplayContentInfo {
        Display getDisplay();

        DisplayRotation getDisplayRotation();
    }

    /* loaded from: classes2.dex */
    public interface OnKeyguardExitResult {
        void onKeyguardExitResult(boolean z);
    }

    /* loaded from: classes2.dex */
    public interface ScreenOffListener {
        void onScreenOff();
    }

    /* loaded from: classes2.dex */
    public interface ScreenOnListener {
        void onScreenOn();
    }

    /* loaded from: classes2.dex */
    public interface WindowState {
        default boolean canAddInternalSystemWindow() {
            return false;
        }

        int getBaseType();

        String getOwningPackage();
    }

    void adjustConfigurationLw(Configuration configuration, int i, int i2);

    int applyKeyguardOcclusionChange(boolean z);

    boolean canDismissBootAnimation();

    int checkAddPermission(int i, boolean z, String str, int[] iArr);

    void dismissKeyguardLw(IKeyguardDismissCallback iKeyguardDismissCallback, CharSequence charSequence);

    KeyEvent dispatchUnhandledKey(IBinder iBinder, KeyEvent keyEvent, int i);

    void dump(String str, PrintWriter printWriter, String[] strArr);

    void dumpDebug(ProtoOutputStream protoOutputStream, long j);

    void enableKeyguard(boolean z);

    void enableScreenAfterBoot();

    void exitKeyguardSecurely(OnKeyguardExitResult onKeyguardExitResult);

    void finishedGoingToSleep(int i);

    void finishedWakingUp(int i);

    default int getMaxWindowLayer() {
        return 36;
    }

    void hideBootMessages();

    void init(Context context, WindowManagerFuncs windowManagerFuncs);

    long interceptKeyBeforeDispatching(IBinder iBinder, KeyEvent keyEvent, int i);

    int interceptKeyBeforeQueueing(KeyEvent keyEvent, int i);

    int interceptMotionBeforeQueueingNonInteractive(int i, long j, int i2);

    boolean isGlobalKey(int i);

    boolean isKeyguardDrawnLw();

    boolean isKeyguardHostWindow(WindowManager.LayoutParams layoutParams);

    boolean isKeyguardLocked();

    boolean isKeyguardOccluded();

    boolean isKeyguardSecure(int i);

    boolean isKeyguardShowing();

    boolean isKeyguardShowingAndNotOccluded();

    boolean isKeyguardTrustedLw();

    default boolean isKeyguardUnoccluding() {
        return false;
    }

    boolean isScreenOn();

    boolean isUserSetupComplete();

    void lockNow(Bundle bundle);

    void notifyCameraLensCoverSwitchChanged(long j, boolean z);

    void notifyLidSwitchChanged(long j, boolean z);

    boolean okToAnimate(boolean z);

    default void onDefaultDisplayFocusChangedLw(WindowState windowState) {
    }

    void onKeyguardOccludedChangedLw(boolean z, boolean z2);

    void onPowerGroupWakefulnessChanged(int i, int i2, int i3, int i4);

    void onSystemUiStarted();

    boolean performHapticFeedback(int i, String str, int i2, boolean z, String str2);

    default void registerDisplayFoldListener(IDisplayFoldListener iDisplayFoldListener) {
    }

    void registerShortcutKey(long j, IShortcutService iShortcutService) throws RemoteException;

    void screenTurnedOff(int i);

    void screenTurnedOn(int i);

    void screenTurningOff(int i, ScreenOffListener screenOffListener);

    void screenTurningOn(int i, ScreenOnListener screenOnListener);

    void setAllowLockscreenWhenOn(int i, boolean z);

    void setCurrentUserLw(int i);

    void setDefaultDisplay(DisplayContentInfo displayContentInfo);

    default void setDismissImeOnBackKeyPressed(boolean z) {
    }

    void setNavBarVirtualKeyHapticFeedbackEnabledLw(boolean z);

    default void setOverrideFoldedArea(Rect rect) {
    }

    void setPipVisibilityLw(boolean z);

    void setRecentsVisibilityLw(boolean z);

    void setSafeMode(boolean z);

    void setSwitchingUser(boolean z);

    void setTopFocusedDisplay(int i);

    void showBootMessage(CharSequence charSequence, boolean z);

    void showGlobalActions();

    void startedGoingToSleep(int i);

    void startedWakingUp(int i);

    void systemBooted();

    void systemReady();

    default void unregisterDisplayFoldListener(IDisplayFoldListener iDisplayFoldListener) {
    }

    void userActivity(int i, int i2);

    /* loaded from: classes2.dex */
    public interface WindowManagerFuncs {
        void enableScreenIfNeeded();

        int getCameraLensCoverState();

        int getLidState();

        boolean isAppTransitionStateIdle();

        void lockDeviceNow();

        void moveDisplayToTopIfAllowed(int i);

        void notifyKeyguardTrustedChanged();

        void onKeyguardShowingAndNotOccludedChanged();

        void onPowerKeyDown(boolean z);

        void onUserSwitched();

        void reboot(boolean z);

        void rebootSafeMode(boolean z);

        void screenTurningOff(int i, ScreenOffListener screenOffListener);

        void shutdown(boolean z);

        void switchKeyboardLayout(int i, int i2);

        void triggerAnimationFailsafe();

        void updateRotation(boolean z, boolean z2);

        static String lidStateToString(int i) {
            return i != -1 ? i != 0 ? i != 1 ? Integer.toString(i) : "LID_OPEN" : "LID_CLOSED" : "LID_ABSENT";
        }

        static String cameraLensStateToString(int i) {
            return i != -1 ? i != 0 ? i != 1 ? Integer.toString(i) : "CAMERA_LENS_COVERED" : "CAMERA_LENS_UNCOVERED" : "CAMERA_LENS_COVER_ABSENT";
        }
    }

    default int getWindowLayerLw(WindowState windowState) {
        return getWindowLayerFromTypeLw(windowState.getBaseType(), windowState.canAddInternalSystemWindow());
    }

    default int getWindowLayerFromTypeLw(int i) {
        if (WindowManager.LayoutParams.isSystemAlertWindowType(i)) {
            throw new IllegalArgumentException("Use getWindowLayerFromTypeLw() or getWindowLayerLw() for alert window types");
        }
        return getWindowLayerFromTypeLw(i, false);
    }

    default int getWindowLayerFromTypeLw(int i, boolean z) {
        return getWindowLayerFromTypeLw(i, z, false);
    }

    default int getWindowLayerFromTypeLw(int i, boolean z, boolean z2) {
        if (z2 && z) {
            return getMaxWindowLayer();
        }
        if (i < 1 || i > 99) {
            switch (i) {
                case 2000:
                    return 15;
                case 2001:
                    return 4;
                case 2002:
                case 2030:
                case 2034:
                case 2035:
                case 2037:
                    return 3;
                case 2003:
                    return z ? 12 : 9;
                case 2004:
                case 2014:
                case 2023:
                case 2025:
                case 2028:
                case 2029:
                default:
                    Slog.e(StartingSurfaceController.TAG, "Unknown window type: " + i);
                    return 3;
                case 2005:
                    return 7;
                case 2006:
                    return z ? 23 : 10;
                case 2007:
                    return 8;
                case 2008:
                    return 6;
                case 2009:
                    return 19;
                case 2010:
                    return z ? 27 : 9;
                case 2011:
                    return 13;
                case 2012:
                    return 14;
                case 2013:
                    return 1;
                case 2015:
                    return 33;
                case 2016:
                    return 30;
                case 2017:
                    return 18;
                case 2018:
                    return 35;
                case 2019:
                    return 24;
                case 2020:
                    return 22;
                case 2021:
                    return 34;
                case 2022:
                    return 5;
                case 2024:
                    return 25;
                case 2026:
                    return 29;
                case 2027:
                    return 28;
                case 2031:
                    return 21;
                case 2032:
                    return 31;
                case 2033:
                    return 20;
                case 2036:
                    return 26;
                case 2038:
                    return 11;
                case 2039:
                    return 32;
                case 2040:
                    return 17;
                case 2041:
                    return 16;
            }
        }
        return 2;
    }

    default int getSubWindowLayerFromTypeLw(int i) {
        switch (i) {
            case 1000:
            case 1003:
                return 1;
            case 1001:
                return -2;
            case 1002:
                return 2;
            case 1004:
                return -1;
            case 1005:
                return 3;
            default:
                Slog.e(StartingSurfaceController.TAG, "Unknown sub-window type: " + i);
                return 0;
        }
    }

    static String userRotationModeToString(int i) {
        return i != 0 ? i != 1 ? Integer.toString(i) : "USER_ROTATION_LOCKED" : "USER_ROTATION_FREE";
    }

    default Rect getFoldedArea() {
        return new Rect();
    }
}
