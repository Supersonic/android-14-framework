package com.android.server.statusbar;

import android.annotation.RequiresPermission;
import android.app.ActivityManager;
import android.app.ActivityManagerInternal;
import android.app.ActivityThread;
import android.app.ITransientNotificationCallback;
import android.app.Notification;
import android.app.compat.CompatChanges;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.om.IOverlayManager;
import android.content.p000pm.PackageManagerInternal;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.graphics.drawable.Icon;
import android.hardware.biometrics.IBiometricContextListener;
import android.hardware.biometrics.IBiometricSysuiReceiver;
import android.hardware.biometrics.PromptInfo;
import android.hardware.display.DisplayManager;
import android.hardware.fingerprint.IUdfpsRefreshRateRequestCallback;
import android.media.INearbyMediaDevicesProvider;
import android.media.MediaRoute2Info;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Process;
import android.os.RemoteException;
import android.os.ResultReceiver;
import android.os.ServiceManager;
import android.os.ShellCallback;
import android.os.UserHandle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.IndentingPrintWriter;
import android.util.Pair;
import android.util.Slog;
import android.util.SparseArray;
import android.view.WindowInsets;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.logging.InstanceId;
import com.android.internal.os.TransferPipe;
import com.android.internal.statusbar.IAddTileResultCallback;
import com.android.internal.statusbar.ISessionListener;
import com.android.internal.statusbar.IStatusBar;
import com.android.internal.statusbar.IStatusBarService;
import com.android.internal.statusbar.IUndoMediaTransferCallback;
import com.android.internal.statusbar.LetterboxDetails;
import com.android.internal.statusbar.NotificationVisibility;
import com.android.internal.statusbar.RegisterStatusBarResult;
import com.android.internal.statusbar.StatusBarIcon;
import com.android.internal.util.DumpUtils;
import com.android.internal.util.GcUtils;
import com.android.internal.view.AppearanceRegion;
import com.android.server.LocalServices;
import com.android.server.UiThread;
import com.android.server.inputmethod.InputMethodManagerInternal;
import com.android.server.notification.NotificationDelegate;
import com.android.server.p014wm.ActivityTaskManagerInternal;
import com.android.server.policy.GlobalActionsProvider;
import com.android.server.power.ShutdownCheckPoints;
import com.android.server.power.ShutdownThread;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
/* loaded from: classes2.dex */
public class StatusBarManagerService extends IStatusBarService.Stub implements DisplayManager.DisplayListener {
    public static final long REQUEST_TIME_OUT = TimeUnit.MINUTES.toNanos(5);
    public final ActivityManagerInternal mActivityManagerInternal;
    public final ActivityTaskManagerInternal mActivityTaskManager;
    public volatile IStatusBar mBar;
    @GuardedBy({"mLock"})
    public IBiometricContextListener mBiometricContextListener;
    public final Context mContext;
    @GuardedBy({"mCurrentRequestAddTilePackages"})
    public final ArrayMap<String, Long> mCurrentRequestAddTilePackages;
    public int mCurrentUserId;
    public final DeathRecipient mDeathRecipient;
    public final ArrayList<DisableRecord> mDisableRecords;
    public final SparseArray<UiState> mDisplayUiState;
    public GlobalActionsProvider.GlobalActionsListener mGlobalActionListener;
    public final GlobalActionsProvider mGlobalActionsProvider;
    public final Handler mHandler;
    public final ArrayMap<String, StatusBarIcon> mIcons;
    public final StatusBarManagerInternal mInternalService;
    public final Object mLock;
    public NotificationDelegate mNotificationDelegate;
    public IOverlayManager mOverlayManager;
    public final PackageManagerInternal mPackageManagerInternal;
    public final SessionMonitor mSessionMonitor;
    public final IBinder mSysUiVisToken;
    public final TileRequestTracker mTileRequestTracker;
    public boolean mTracingEnabled;
    @GuardedBy({"mLock"})
    public IUdfpsRefreshRateRequestCallback mUdfpsRefreshRateRequestCallback;

    @Override // android.hardware.display.DisplayManager.DisplayListener
    public void onDisplayAdded(int i) {
    }

    @Override // android.hardware.display.DisplayManager.DisplayListener
    public void onDisplayChanged(int i) {
    }

    public final boolean resolveEnabledComponent(boolean z, int i) {
        if (i == 1) {
            return true;
        }
        if (i == 0) {
            return z;
        }
        return false;
    }

    /* loaded from: classes2.dex */
    public class DeathRecipient implements IBinder.DeathRecipient {
        public DeathRecipient() {
        }

        @Override // android.os.IBinder.DeathRecipient
        public void binderDied() {
            StatusBarManagerService.this.mBar.asBinder().unlinkToDeath(this, 0);
            StatusBarManagerService.this.mBar = null;
            StatusBarManagerService.this.notifyBarAttachChanged();
        }

        public void linkToDeath() {
            try {
                StatusBarManagerService.this.mBar.asBinder().linkToDeath(StatusBarManagerService.this.mDeathRecipient, 0);
            } catch (RemoteException e) {
                Slog.e("StatusBarManagerService", "Unable to register Death Recipient for status bar", e);
            }
        }
    }

    /* loaded from: classes2.dex */
    public class DisableRecord implements IBinder.DeathRecipient {
        public String pkg;
        public IBinder token;
        public int userId;
        public int what1;
        public int what2;

        public DisableRecord(int i, IBinder iBinder) {
            this.userId = i;
            this.token = iBinder;
            try {
                iBinder.linkToDeath(this, 0);
            } catch (RemoteException unused) {
            }
        }

        @Override // android.os.IBinder.DeathRecipient
        public void binderDied() {
            Slog.i("StatusBarManagerService", "binder died for pkg=" + this.pkg);
            StatusBarManagerService.this.disableForUser(0, this.token, this.pkg, this.userId);
            StatusBarManagerService.this.disable2ForUser(0, this.token, this.pkg, this.userId);
            this.token.unlinkToDeath(this, 0);
        }

        public void setFlags(int i, int i2, String str) {
            if (i2 == 1) {
                this.what1 = i;
            } else if (i2 == 2) {
                this.what2 = i;
            } else {
                Slog.w("StatusBarManagerService", "Can't set unsupported disable flag " + i2 + ": 0x" + Integer.toHexString(i));
            }
            this.pkg = str;
        }

        public int getFlags(int i) {
            if (i != 1) {
                if (i == 2) {
                    return this.what2;
                }
                Slog.w("StatusBarManagerService", "Can't get unsupported disable flag " + i);
                return 0;
            }
            return this.what1;
        }

        public boolean isEmpty() {
            return this.what1 == 0 && this.what2 == 0;
        }

        public String toString() {
            return String.format("userId=%d what1=0x%08X what2=0x%08X pkg=%s token=%s", Integer.valueOf(this.userId), Integer.valueOf(this.what1), Integer.valueOf(this.what2), this.pkg, this.token);
        }
    }

    public StatusBarManagerService(Context context) {
        Handler handler = new Handler();
        this.mHandler = handler;
        this.mIcons = new ArrayMap<>();
        this.mDisableRecords = new ArrayList<>();
        this.mSysUiVisToken = new Binder();
        this.mLock = new Object();
        this.mDeathRecipient = new DeathRecipient();
        SparseArray<UiState> sparseArray = new SparseArray<>();
        this.mDisplayUiState = sparseArray;
        this.mCurrentRequestAddTilePackages = new ArrayMap<>();
        StatusBarManagerInternal statusBarManagerInternal = new StatusBarManagerInternal() { // from class: com.android.server.statusbar.StatusBarManagerService.1
            @Override // com.android.server.statusbar.StatusBarManagerInternal
            public void setNotificationDelegate(NotificationDelegate notificationDelegate) {
                StatusBarManagerService.this.mNotificationDelegate = notificationDelegate;
            }

            @Override // com.android.server.statusbar.StatusBarManagerInternal
            public void showScreenPinningRequest(int i) {
                if (StatusBarManagerService.this.mBar != null) {
                    try {
                        StatusBarManagerService.this.mBar.showScreenPinningRequest(i);
                    } catch (RemoteException unused) {
                    }
                }
            }

            @Override // com.android.server.statusbar.StatusBarManagerInternal
            public void showAssistDisclosure() {
                if (StatusBarManagerService.this.mBar != null) {
                    try {
                        StatusBarManagerService.this.mBar.showAssistDisclosure();
                    } catch (RemoteException unused) {
                    }
                }
            }

            @Override // com.android.server.statusbar.StatusBarManagerInternal
            public void startAssist(Bundle bundle) {
                if (StatusBarManagerService.this.mBar != null) {
                    try {
                        StatusBarManagerService.this.mBar.startAssist(bundle);
                    } catch (RemoteException unused) {
                    }
                }
            }

            @Override // com.android.server.statusbar.StatusBarManagerInternal
            public void onCameraLaunchGestureDetected(int i) {
                if (StatusBarManagerService.this.mBar != null) {
                    try {
                        StatusBarManagerService.this.mBar.onCameraLaunchGestureDetected(i);
                    } catch (RemoteException unused) {
                    }
                }
            }

            @Override // com.android.server.statusbar.StatusBarManagerInternal
            public void onEmergencyActionLaunchGestureDetected() {
                if (StatusBarManagerService.this.mBar != null) {
                    try {
                        StatusBarManagerService.this.mBar.onEmergencyActionLaunchGestureDetected();
                    } catch (RemoteException unused) {
                    }
                }
            }

            @Override // com.android.server.statusbar.StatusBarManagerInternal
            public void setDisableFlags(int i, int i2, String str) {
                StatusBarManagerService.this.setDisableFlags(i, i2, str);
            }

            @Override // com.android.server.statusbar.StatusBarManagerInternal
            public void appTransitionFinished(int i) {
                StatusBarManagerService.this.enforceStatusBarService();
                if (StatusBarManagerService.this.mBar != null) {
                    try {
                        StatusBarManagerService.this.mBar.appTransitionFinished(i);
                    } catch (RemoteException unused) {
                    }
                }
            }

            @Override // com.android.server.statusbar.StatusBarManagerInternal
            public void toggleTaskbar() {
                if (StatusBarManagerService.this.mBar != null) {
                    try {
                        StatusBarManagerService.this.mBar.toggleTaskbar();
                    } catch (RemoteException unused) {
                    }
                }
            }

            @Override // com.android.server.statusbar.StatusBarManagerInternal
            public void toggleRecentApps() {
                if (StatusBarManagerService.this.mBar != null) {
                    try {
                        StatusBarManagerService.this.mBar.toggleRecentApps();
                    } catch (RemoteException unused) {
                    }
                }
            }

            @Override // com.android.server.statusbar.StatusBarManagerInternal
            public void setCurrentUser(int i) {
                StatusBarManagerService.this.mCurrentUserId = i;
            }

            @Override // com.android.server.statusbar.StatusBarManagerInternal
            public void preloadRecentApps() {
                if (StatusBarManagerService.this.mBar != null) {
                    try {
                        StatusBarManagerService.this.mBar.preloadRecentApps();
                    } catch (RemoteException unused) {
                    }
                }
            }

            @Override // com.android.server.statusbar.StatusBarManagerInternal
            public void cancelPreloadRecentApps() {
                if (StatusBarManagerService.this.mBar != null) {
                    try {
                        StatusBarManagerService.this.mBar.cancelPreloadRecentApps();
                    } catch (RemoteException unused) {
                    }
                }
            }

            @Override // com.android.server.statusbar.StatusBarManagerInternal
            public void showRecentApps(boolean z) {
                if (StatusBarManagerService.this.mBar != null) {
                    try {
                        StatusBarManagerService.this.mBar.showRecentApps(z);
                    } catch (RemoteException unused) {
                    }
                }
            }

            @Override // com.android.server.statusbar.StatusBarManagerInternal
            public void hideRecentApps(boolean z, boolean z2) {
                if (StatusBarManagerService.this.mBar != null) {
                    try {
                        StatusBarManagerService.this.mBar.hideRecentApps(z, z2);
                    } catch (RemoteException unused) {
                    }
                }
            }

            @Override // com.android.server.statusbar.StatusBarManagerInternal
            public void collapsePanels() {
                if (StatusBarManagerService.this.mBar != null) {
                    try {
                        StatusBarManagerService.this.mBar.animateCollapsePanels();
                    } catch (RemoteException unused) {
                    }
                }
            }

            @Override // com.android.server.statusbar.StatusBarManagerInternal
            public void dismissKeyboardShortcutsMenu() {
                if (StatusBarManagerService.this.mBar != null) {
                    try {
                        StatusBarManagerService.this.mBar.dismissKeyboardShortcutsMenu();
                    } catch (RemoteException unused) {
                    }
                }
            }

            @Override // com.android.server.statusbar.StatusBarManagerInternal
            public void toggleKeyboardShortcutsMenu(int i) {
                if (StatusBarManagerService.this.mBar != null) {
                    try {
                        StatusBarManagerService.this.mBar.toggleKeyboardShortcutsMenu(i);
                    } catch (RemoteException unused) {
                    }
                }
            }

            @Override // com.android.server.statusbar.StatusBarManagerInternal
            public void setImeWindowStatus(int i, IBinder iBinder, int i2, int i3, boolean z) {
                StatusBarManagerService.this.setImeWindowStatus(i, iBinder, i2, i3, z);
            }

            @Override // com.android.server.statusbar.StatusBarManagerInternal
            public void setIcon(String str, String str2, int i, int i2, String str3) {
                StatusBarManagerService.this.setIcon(str, str2, i, i2, str3);
            }

            @Override // com.android.server.statusbar.StatusBarManagerInternal
            public void setIconVisibility(String str, boolean z) {
                StatusBarManagerService.this.setIconVisibility(str, z);
            }

            @Override // com.android.server.statusbar.StatusBarManagerInternal
            public void showChargingAnimation(int i) {
                if (StatusBarManagerService.this.mBar != null) {
                    try {
                        StatusBarManagerService.this.mBar.showWirelessChargingAnimation(i);
                    } catch (RemoteException unused) {
                    }
                }
            }

            @Override // com.android.server.statusbar.StatusBarManagerInternal
            public void showPictureInPictureMenu() {
                if (StatusBarManagerService.this.mBar != null) {
                    try {
                        StatusBarManagerService.this.mBar.showPictureInPictureMenu();
                    } catch (RemoteException unused) {
                    }
                }
            }

            @Override // com.android.server.statusbar.StatusBarManagerInternal
            public void setWindowState(int i, int i2, int i3) {
                if (StatusBarManagerService.this.mBar != null) {
                    try {
                        StatusBarManagerService.this.mBar.setWindowState(i, i2, i3);
                    } catch (RemoteException unused) {
                    }
                }
            }

            @Override // com.android.server.statusbar.StatusBarManagerInternal
            public void appTransitionPending(int i) {
                if (StatusBarManagerService.this.mBar != null) {
                    try {
                        StatusBarManagerService.this.mBar.appTransitionPending(i);
                    } catch (RemoteException unused) {
                    }
                }
            }

            @Override // com.android.server.statusbar.StatusBarManagerInternal
            public void appTransitionCancelled(int i) {
                if (StatusBarManagerService.this.mBar != null) {
                    try {
                        StatusBarManagerService.this.mBar.appTransitionCancelled(i);
                    } catch (RemoteException unused) {
                    }
                }
            }

            @Override // com.android.server.statusbar.StatusBarManagerInternal
            public void appTransitionStarting(int i, long j, long j2) {
                if (StatusBarManagerService.this.mBar != null) {
                    try {
                        StatusBarManagerService.this.mBar.appTransitionStarting(i, j, j2);
                    } catch (RemoteException unused) {
                    }
                }
            }

            @Override // com.android.server.statusbar.StatusBarManagerInternal
            public void setTopAppHidesStatusBar(boolean z) {
                if (StatusBarManagerService.this.mBar != null) {
                    try {
                        StatusBarManagerService.this.mBar.setTopAppHidesStatusBar(z);
                    } catch (RemoteException unused) {
                    }
                }
            }

            @Override // com.android.server.statusbar.StatusBarManagerInternal
            public boolean showShutdownUi(boolean z, String str) {
                if (StatusBarManagerService.this.mContext.getResources().getBoolean(17891793) && StatusBarManagerService.this.mBar != null) {
                    try {
                        StatusBarManagerService.this.mBar.showShutdownUi(z, str);
                        return true;
                    } catch (RemoteException unused) {
                    }
                }
                return false;
            }

            @Override // com.android.server.statusbar.StatusBarManagerInternal
            public void onProposedRotationChanged(int i, boolean z) {
                if (StatusBarManagerService.this.mBar != null) {
                    try {
                        StatusBarManagerService.this.mBar.onProposedRotationChanged(i, z);
                    } catch (RemoteException unused) {
                    }
                }
            }

            @Override // com.android.server.statusbar.StatusBarManagerInternal
            public void onDisplayReady(int i) {
                if (StatusBarManagerService.this.mBar != null) {
                    try {
                        StatusBarManagerService.this.mBar.onDisplayReady(i);
                    } catch (RemoteException unused) {
                    }
                }
            }

            @Override // com.android.server.statusbar.StatusBarManagerInternal
            public void onRecentsAnimationStateChanged(boolean z) {
                if (StatusBarManagerService.this.mBar != null) {
                    try {
                        StatusBarManagerService.this.mBar.onRecentsAnimationStateChanged(z);
                    } catch (RemoteException unused) {
                    }
                }
            }

            @Override // com.android.server.statusbar.StatusBarManagerInternal
            public void onSystemBarAttributesChanged(int i, int i2, AppearanceRegion[] appearanceRegionArr, boolean z, int i3, int i4, String str, LetterboxDetails[] letterboxDetailsArr) {
                StatusBarManagerService.this.getUiState(i).setBarAttributes(i2, appearanceRegionArr, z, i3, i4, str, letterboxDetailsArr);
                if (StatusBarManagerService.this.mBar != null) {
                    try {
                        StatusBarManagerService.this.mBar.onSystemBarAttributesChanged(i, i2, appearanceRegionArr, z, i3, i4, str, letterboxDetailsArr);
                    } catch (RemoteException unused) {
                    }
                }
            }

            @Override // com.android.server.statusbar.StatusBarManagerInternal
            public void showTransient(int i, int i2, boolean z) {
                StatusBarManagerService.this.getUiState(i).showTransient(i2);
                if (StatusBarManagerService.this.mBar != null) {
                    try {
                        StatusBarManagerService.this.mBar.showTransient(i, i2, z);
                    } catch (RemoteException unused) {
                    }
                }
            }

            @Override // com.android.server.statusbar.StatusBarManagerInternal
            public void abortTransient(int i, int i2) {
                StatusBarManagerService.this.getUiState(i).clearTransient(i2);
                if (StatusBarManagerService.this.mBar != null) {
                    try {
                        StatusBarManagerService.this.mBar.abortTransient(i, i2);
                    } catch (RemoteException unused) {
                    }
                }
            }

            @Override // com.android.server.statusbar.StatusBarManagerInternal
            public void showToast(int i, String str, IBinder iBinder, CharSequence charSequence, IBinder iBinder2, int i2, ITransientNotificationCallback iTransientNotificationCallback, int i3) {
                if (StatusBarManagerService.this.mBar != null) {
                    try {
                        StatusBarManagerService.this.mBar.showToast(i, str, iBinder, charSequence, iBinder2, i2, iTransientNotificationCallback, i3);
                    } catch (RemoteException unused) {
                    }
                }
            }

            @Override // com.android.server.statusbar.StatusBarManagerInternal
            public void hideToast(String str, IBinder iBinder) {
                if (StatusBarManagerService.this.mBar != null) {
                    try {
                        StatusBarManagerService.this.mBar.hideToast(str, iBinder);
                    } catch (RemoteException unused) {
                    }
                }
            }

            @Override // com.android.server.statusbar.StatusBarManagerInternal
            public boolean requestWindowMagnificationConnection(boolean z) {
                if (StatusBarManagerService.this.mBar != null) {
                    try {
                        StatusBarManagerService.this.mBar.requestWindowMagnificationConnection(z);
                        return true;
                    } catch (RemoteException unused) {
                        return false;
                    }
                }
                return false;
            }

            @Override // com.android.server.statusbar.StatusBarManagerInternal
            public void setNavigationBarLumaSamplingEnabled(int i, boolean z) {
                if (StatusBarManagerService.this.mBar != null) {
                    try {
                        StatusBarManagerService.this.mBar.setNavigationBarLumaSamplingEnabled(i, z);
                    } catch (RemoteException unused) {
                    }
                }
            }

            @Override // com.android.server.statusbar.StatusBarManagerInternal
            public void setUdfpsRefreshRateCallback(IUdfpsRefreshRateRequestCallback iUdfpsRefreshRateRequestCallback) {
                synchronized (StatusBarManagerService.this.mLock) {
                    StatusBarManagerService.this.mUdfpsRefreshRateRequestCallback = iUdfpsRefreshRateRequestCallback;
                }
                if (StatusBarManagerService.this.mBar != null) {
                    try {
                        StatusBarManagerService.this.mBar.setUdfpsRefreshRateCallback(iUdfpsRefreshRateRequestCallback);
                    } catch (RemoteException unused) {
                    }
                }
            }

            @Override // com.android.server.statusbar.StatusBarManagerInternal
            public void showRearDisplayDialog(int i) {
                if (StatusBarManagerService.this.mBar != null) {
                    try {
                        StatusBarManagerService.this.mBar.showRearDisplayDialog(i);
                    } catch (RemoteException unused) {
                    }
                }
            }

            @Override // com.android.server.statusbar.StatusBarManagerInternal
            public void goToFullscreenFromSplit() {
                if (StatusBarManagerService.this.mBar != null) {
                    try {
                        StatusBarManagerService.this.mBar.goToFullscreenFromSplit();
                    } catch (RemoteException unused) {
                    }
                }
            }

            @Override // com.android.server.statusbar.StatusBarManagerInternal
            public void enterStageSplitFromRunningApp(boolean z) {
                if (StatusBarManagerService.this.mBar != null) {
                    try {
                        StatusBarManagerService.this.mBar.enterStageSplitFromRunningApp(z);
                    } catch (RemoteException unused) {
                    }
                }
            }

            @Override // com.android.server.statusbar.StatusBarManagerInternal
            public void showMediaOutputSwitcher(String str) {
                if (StatusBarManagerService.this.mBar != null) {
                    try {
                        StatusBarManagerService.this.mBar.showMediaOutputSwitcher(str);
                    } catch (RemoteException unused) {
                    }
                }
            }
        };
        this.mInternalService = statusBarManagerInternal;
        this.mGlobalActionsProvider = new GlobalActionsProvider() { // from class: com.android.server.statusbar.StatusBarManagerService.2
            @Override // com.android.server.policy.GlobalActionsProvider
            public boolean isGlobalActionsDisabled() {
                return (((UiState) StatusBarManagerService.this.mDisplayUiState.get(0)).getDisabled2() & 8) != 0;
            }

            @Override // com.android.server.policy.GlobalActionsProvider
            public void setGlobalActionsListener(GlobalActionsProvider.GlobalActionsListener globalActionsListener) {
                StatusBarManagerService.this.mGlobalActionListener = globalActionsListener;
                StatusBarManagerService.this.mGlobalActionListener.onGlobalActionsAvailableChanged(StatusBarManagerService.this.mBar != null);
            }

            @Override // com.android.server.policy.GlobalActionsProvider
            public void showGlobalActions() {
                if (StatusBarManagerService.this.mBar != null) {
                    try {
                        StatusBarManagerService.this.mBar.showGlobalActionsMenu();
                    } catch (RemoteException unused) {
                    }
                }
            }
        };
        this.mContext = context;
        LocalServices.addService(StatusBarManagerInternal.class, statusBarManagerInternal);
        sparseArray.put(0, new UiState());
        ((DisplayManager) context.getSystemService("display")).registerDisplayListener(this, handler);
        this.mActivityTaskManager = (ActivityTaskManagerInternal) LocalServices.getService(ActivityTaskManagerInternal.class);
        this.mPackageManagerInternal = (PackageManagerInternal) LocalServices.getService(PackageManagerInternal.class);
        this.mActivityManagerInternal = (ActivityManagerInternal) LocalServices.getService(ActivityManagerInternal.class);
        this.mTileRequestTracker = new TileRequestTracker(context);
        this.mSessionMonitor = new SessionMonitor(context);
    }

    public void publishGlobalActionsProvider() {
        if (LocalServices.getService(GlobalActionsProvider.class) == null) {
            LocalServices.addService(GlobalActionsProvider.class, this.mGlobalActionsProvider);
        }
    }

    public final IOverlayManager getOverlayManager() {
        if (this.mOverlayManager == null) {
            IOverlayManager asInterface = IOverlayManager.Stub.asInterface(ServiceManager.getService("overlay"));
            this.mOverlayManager = asInterface;
            if (asInterface == null) {
                Slog.w("StatusBarManager", "warning: no OVERLAY_SERVICE");
            }
        }
        return this.mOverlayManager;
    }

    @Override // android.hardware.display.DisplayManager.DisplayListener
    public void onDisplayRemoved(int i) {
        synchronized (this.mLock) {
            this.mDisplayUiState.remove(i);
        }
    }

    public final boolean isDisable2FlagSet(int i) {
        return (this.mDisplayUiState.get(0).getDisabled2() & i) == i;
    }

    public void expandNotificationsPanel() {
        enforceExpandStatusBar();
        if (isDisable2FlagSet(4) || this.mBar == null) {
            return;
        }
        try {
            this.mBar.animateExpandNotificationsPanel();
        } catch (RemoteException unused) {
        }
    }

    public void collapsePanels() {
        if (checkCanCollapseStatusBar("collapsePanels") && this.mBar != null) {
            try {
                this.mBar.animateCollapsePanels();
            } catch (RemoteException unused) {
            }
        }
    }

    public void togglePanel() {
        if (!checkCanCollapseStatusBar("togglePanel") || isDisable2FlagSet(4) || this.mBar == null) {
            return;
        }
        try {
            this.mBar.togglePanel();
        } catch (RemoteException unused) {
        }
    }

    public void expandSettingsPanel(String str) {
        enforceExpandStatusBar();
        if (this.mBar != null) {
            try {
                this.mBar.animateExpandSettingsPanel(str);
            } catch (RemoteException unused) {
            }
        }
    }

    public void addTile(ComponentName componentName) {
        enforceStatusBarOrShell();
        if (this.mBar != null) {
            try {
                this.mBar.addQsTile(componentName);
            } catch (RemoteException unused) {
            }
        }
    }

    public void remTile(ComponentName componentName) {
        enforceStatusBarOrShell();
        if (this.mBar != null) {
            try {
                this.mBar.remQsTile(componentName);
            } catch (RemoteException unused) {
            }
        }
    }

    public void clickTile(ComponentName componentName) {
        enforceStatusBarOrShell();
        if (this.mBar != null) {
            try {
                this.mBar.clickQsTile(componentName);
            } catch (RemoteException unused) {
            }
        }
    }

    public void handleSystemKey(int i) throws RemoteException {
        if (checkCanCollapseStatusBar("handleSystemKey") && this.mBar != null) {
            try {
                this.mBar.handleSystemKey(i);
            } catch (RemoteException unused) {
            }
        }
    }

    public void showPinningEnterExitToast(boolean z) throws RemoteException {
        if (this.mBar != null) {
            try {
                this.mBar.showPinningEnterExitToast(z);
            } catch (RemoteException unused) {
            }
        }
    }

    public void showPinningEscapeToast() throws RemoteException {
        if (this.mBar != null) {
            try {
                this.mBar.showPinningEscapeToast();
            } catch (RemoteException unused) {
            }
        }
    }

    public void showAuthenticationDialog(PromptInfo promptInfo, IBiometricSysuiReceiver iBiometricSysuiReceiver, int[] iArr, boolean z, boolean z2, int i, long j, String str, long j2, int i2) {
        enforceBiometricDialog();
        if (this.mBar != null) {
            try {
                this.mBar.showAuthenticationDialog(promptInfo, iBiometricSysuiReceiver, iArr, z, z2, i, j, str, j2, i2);
            } catch (RemoteException unused) {
            }
        }
    }

    public void onBiometricAuthenticated(int i) {
        enforceBiometricDialog();
        if (this.mBar != null) {
            try {
                this.mBar.onBiometricAuthenticated(i);
            } catch (RemoteException unused) {
            }
        }
    }

    public void onBiometricHelp(int i, String str) {
        enforceBiometricDialog();
        if (this.mBar != null) {
            try {
                this.mBar.onBiometricHelp(i, str);
            } catch (RemoteException unused) {
            }
        }
    }

    public void onBiometricError(int i, int i2, int i3) {
        enforceBiometricDialog();
        if (this.mBar != null) {
            try {
                this.mBar.onBiometricError(i, i2, i3);
            } catch (RemoteException unused) {
            }
        }
    }

    public void hideAuthenticationDialog(long j) {
        enforceBiometricDialog();
        if (this.mBar != null) {
            try {
                this.mBar.hideAuthenticationDialog(j);
            } catch (RemoteException unused) {
            }
        }
    }

    public void setBiometicContextListener(IBiometricContextListener iBiometricContextListener) {
        enforceStatusBarService();
        synchronized (this.mLock) {
            this.mBiometricContextListener = iBiometricContextListener;
        }
        if (this.mBar != null) {
            try {
                this.mBar.setBiometicContextListener(iBiometricContextListener);
            } catch (RemoteException unused) {
            }
        }
    }

    public void setUdfpsRefreshRateCallback(IUdfpsRefreshRateRequestCallback iUdfpsRefreshRateRequestCallback) {
        enforceStatusBarService();
        if (this.mBar != null) {
            try {
                this.mBar.setUdfpsRefreshRateCallback(iUdfpsRefreshRateRequestCallback);
            } catch (RemoteException unused) {
            }
        }
    }

    public void startTracing() {
        if (this.mBar != null) {
            try {
                this.mBar.startTracing();
                this.mTracingEnabled = true;
            } catch (RemoteException unused) {
            }
        }
    }

    public void stopTracing() {
        if (this.mBar != null) {
            try {
                this.mTracingEnabled = false;
                this.mBar.stopTracing();
            } catch (RemoteException unused) {
            }
        }
    }

    public boolean isTracing() {
        return this.mTracingEnabled;
    }

    public void disable(int i, IBinder iBinder, String str) {
        disableForUser(i, iBinder, str, this.mCurrentUserId);
    }

    public void disableForUser(int i, IBinder iBinder, String str, int i2) {
        enforceStatusBar();
        synchronized (this.mLock) {
            disableLocked(0, i2, i, iBinder, str, 1);
        }
    }

    public void disable2(int i, IBinder iBinder, String str) {
        disable2ForUser(i, iBinder, str, this.mCurrentUserId);
    }

    public void disable2ForUser(int i, IBinder iBinder, String str, int i2) {
        enforceStatusBar();
        synchronized (this.mLock) {
            disableLocked(0, i2, i, iBinder, str, 2);
        }
    }

    public final void disableLocked(int i, int i2, int i3, IBinder iBinder, String str, int i4) {
        manageDisableListLocked(i2, i3, iBinder, str, i4);
        final int gatherDisableActionsLocked = gatherDisableActionsLocked(this.mCurrentUserId, 1);
        int gatherDisableActionsLocked2 = gatherDisableActionsLocked(this.mCurrentUserId, 2);
        UiState uiState = getUiState(i);
        if (uiState.disableEquals(gatherDisableActionsLocked, gatherDisableActionsLocked2)) {
            return;
        }
        uiState.setDisabled(gatherDisableActionsLocked, gatherDisableActionsLocked2);
        this.mHandler.post(new Runnable() { // from class: com.android.server.statusbar.StatusBarManagerService$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                StatusBarManagerService.this.lambda$disableLocked$0(gatherDisableActionsLocked);
            }
        });
        if (this.mBar != null) {
            try {
                this.mBar.disable(i, gatherDisableActionsLocked, gatherDisableActionsLocked2);
            } catch (RemoteException unused) {
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$disableLocked$0(int i) {
        this.mNotificationDelegate.onSetDisabled(i);
    }

    public int[] getDisableFlags(IBinder iBinder, int i) {
        int i2;
        int i3;
        enforceStatusBar();
        synchronized (this.mLock) {
            DisableRecord disableRecord = (DisableRecord) findMatchingRecordLocked(iBinder, i).second;
            if (disableRecord != null) {
                i2 = disableRecord.what1;
                i3 = disableRecord.what2;
            } else {
                i2 = 0;
                i3 = 0;
            }
        }
        return new int[]{i2, i3};
    }

    public void runGcForTest() {
        if (!Build.IS_DEBUGGABLE) {
            throw new SecurityException("runGcForTest requires a debuggable build");
        }
        GcUtils.runGcAndFinalizersSync();
        if (this.mBar != null) {
            try {
                this.mBar.runGcForTest();
            } catch (RemoteException unused) {
            }
        }
    }

    public void setIcon(String str, String str2, int i, int i2, String str3) {
        enforceStatusBar();
        synchronized (this.mIcons) {
            StatusBarIcon statusBarIcon = new StatusBarIcon(str2, UserHandle.SYSTEM, i, i2, 0, str3);
            this.mIcons.put(str, statusBarIcon);
            if (this.mBar != null) {
                try {
                    this.mBar.setIcon(str, statusBarIcon);
                } catch (RemoteException unused) {
                }
            }
        }
    }

    public void setIconVisibility(String str, boolean z) {
        enforceStatusBar();
        synchronized (this.mIcons) {
            StatusBarIcon statusBarIcon = this.mIcons.get(str);
            if (statusBarIcon == null) {
                return;
            }
            if (statusBarIcon.visible != z) {
                statusBarIcon.visible = z;
                if (this.mBar != null) {
                    try {
                        this.mBar.setIcon(str, statusBarIcon);
                    } catch (RemoteException unused) {
                    }
                }
            }
        }
    }

    public void removeIcon(String str) {
        enforceStatusBar();
        synchronized (this.mIcons) {
            this.mIcons.remove(str);
            if (this.mBar != null) {
                try {
                    this.mBar.removeIcon(str);
                } catch (RemoteException unused) {
                }
            }
        }
    }

    public void setImeWindowStatus(final int i, final IBinder iBinder, final int i2, final int i3, final boolean z) {
        enforceStatusBar();
        synchronized (this.mLock) {
            getUiState(i).setImeWindowState(i2, i3, z, iBinder);
            this.mHandler.post(new Runnable() { // from class: com.android.server.statusbar.StatusBarManagerService$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    StatusBarManagerService.this.lambda$setImeWindowStatus$1(i, iBinder, i2, i3, z);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setImeWindowStatus$1(int i, IBinder iBinder, int i2, int i3, boolean z) {
        if (this.mBar == null) {
            return;
        }
        try {
            this.mBar.setImeWindowStatus(i, iBinder, i2, i3, z);
        } catch (RemoteException unused) {
        }
    }

    public final void setDisableFlags(int i, int i2, String str) {
        enforceStatusBarService();
        int i3 = (-134152193) & i2;
        if (i3 != 0) {
            Slog.e("StatusBarManagerService", "Unknown disable flags: 0x" + Integer.toHexString(i3), new RuntimeException());
        }
        synchronized (this.mLock) {
            disableLocked(i, this.mCurrentUserId, i2, this.mSysUiVisToken, str, 1);
        }
    }

    public final UiState getUiState(int i) {
        UiState uiState = this.mDisplayUiState.get(i);
        if (uiState == null) {
            UiState uiState2 = new UiState();
            this.mDisplayUiState.put(i, uiState2);
            return uiState2;
        }
        return uiState;
    }

    /* loaded from: classes2.dex */
    public static class UiState {
        public int mAppearance;
        public AppearanceRegion[] mAppearanceRegions;
        public int mBehavior;
        public int mDisabled1;
        public int mDisabled2;
        public int mImeBackDisposition;
        public IBinder mImeToken;
        public int mImeWindowVis;
        public LetterboxDetails[] mLetterboxDetails;
        public boolean mNavbarColorManagedByIme;
        public String mPackageName;
        public int mRequestedVisibleTypes;
        public boolean mShowImeSwitcher;
        public int mTransientBarTypes;

        public UiState() {
            this.mAppearance = 0;
            this.mAppearanceRegions = new AppearanceRegion[0];
            this.mNavbarColorManagedByIme = false;
            this.mRequestedVisibleTypes = WindowInsets.Type.defaultVisible();
            this.mPackageName = "none";
            this.mDisabled1 = 0;
            this.mDisabled2 = 0;
            this.mImeWindowVis = 0;
            this.mImeBackDisposition = 0;
            this.mShowImeSwitcher = false;
            this.mImeToken = null;
            this.mLetterboxDetails = new LetterboxDetails[0];
        }

        public final void setBarAttributes(int i, AppearanceRegion[] appearanceRegionArr, boolean z, int i2, int i3, String str, LetterboxDetails[] letterboxDetailsArr) {
            this.mAppearance = i;
            this.mAppearanceRegions = appearanceRegionArr;
            this.mNavbarColorManagedByIme = z;
            this.mBehavior = i2;
            this.mRequestedVisibleTypes = i3;
            this.mPackageName = str;
            this.mLetterboxDetails = letterboxDetailsArr;
        }

        public final void showTransient(int i) {
            this.mTransientBarTypes = i | this.mTransientBarTypes;
        }

        public final void clearTransient(int i) {
            this.mTransientBarTypes = (~i) & this.mTransientBarTypes;
        }

        public final int getDisabled1() {
            return this.mDisabled1;
        }

        public final int getDisabled2() {
            return this.mDisabled2;
        }

        public final void setDisabled(int i, int i2) {
            this.mDisabled1 = i;
            this.mDisabled2 = i2;
        }

        public final boolean disableEquals(int i, int i2) {
            return this.mDisabled1 == i && this.mDisabled2 == i2;
        }

        public final void setImeWindowState(int i, int i2, boolean z, IBinder iBinder) {
            this.mImeWindowVis = i;
            this.mImeBackDisposition = i2;
            this.mShowImeSwitcher = z;
            this.mImeToken = iBinder;
        }
    }

    public final void enforceStatusBarOrShell() {
        if (Binder.getCallingUid() == 2000) {
            return;
        }
        enforceStatusBar();
    }

    public final void enforceStatusBar() {
        this.mContext.enforceCallingOrSelfPermission("android.permission.STATUS_BAR", "StatusBarManagerService");
    }

    public final void enforceExpandStatusBar() {
        this.mContext.enforceCallingOrSelfPermission("android.permission.EXPAND_STATUS_BAR", "StatusBarManagerService");
    }

    public final void enforceStatusBarService() {
        this.mContext.enforceCallingOrSelfPermission("android.permission.STATUS_BAR_SERVICE", "StatusBarManagerService");
    }

    public final void enforceBiometricDialog() {
        this.mContext.enforceCallingOrSelfPermission("android.permission.MANAGE_BIOMETRIC_DIALOG", "StatusBarManagerService");
    }

    public final void enforceMediaContentControl() {
        this.mContext.enforceCallingOrSelfPermission("android.permission.MEDIA_CONTENT_CONTROL", "StatusBarManagerService");
    }

    @RequiresPermission("android.permission.CONTROL_DEVICE_STATE")
    public final void enforceControlDeviceStatePermission() {
        this.mContext.enforceCallingOrSelfPermission("android.permission.CONTROL_DEVICE_STATE", "StatusBarManagerService");
    }

    public final boolean doesCallerHoldInteractAcrossUserPermission() {
        return this.mContext.checkCallingPermission("android.permission.INTERACT_ACROSS_USERS_FULL") == 0 || this.mContext.checkCallingPermission("android.permission.INTERACT_ACROSS_USERS") == 0;
    }

    public final boolean checkCanCollapseStatusBar(String str) {
        int callingUid = Binder.getCallingUid();
        int callingPid = Binder.getCallingPid();
        if (CompatChanges.isChangeEnabled(173031413L, callingUid)) {
            enforceStatusBar();
            return true;
        } else if (this.mContext.checkPermission("android.permission.STATUS_BAR", callingPid, callingUid) != 0) {
            enforceExpandStatusBar();
            if (this.mActivityTaskManager.canCloseSystemDialogs(callingPid, callingUid)) {
                return true;
            }
            Slog.e("StatusBarManagerService", "Permission Denial: Method " + str + "() requires permission android.permission.STATUS_BAR, ignoring call.");
            return false;
        } else {
            return true;
        }
    }

    public RegisterStatusBarResult registerStatusBar(IStatusBar iStatusBar) {
        ArrayMap arrayMap;
        RegisterStatusBarResult registerStatusBarResult;
        enforceStatusBarService();
        Slog.i("StatusBarManagerService", "registerStatusBar bar=" + iStatusBar);
        this.mBar = iStatusBar;
        this.mDeathRecipient.linkToDeath();
        notifyBarAttachChanged();
        synchronized (this.mIcons) {
            arrayMap = new ArrayMap(this.mIcons);
        }
        synchronized (this.mLock) {
            UiState uiState = this.mDisplayUiState.get(0);
            registerStatusBarResult = new RegisterStatusBarResult(arrayMap, gatherDisableActionsLocked(this.mCurrentUserId, 1), uiState.mAppearance, uiState.mAppearanceRegions, uiState.mImeWindowVis, uiState.mImeBackDisposition, uiState.mShowImeSwitcher, gatherDisableActionsLocked(this.mCurrentUserId, 2), uiState.mImeToken, uiState.mNavbarColorManagedByIme, uiState.mBehavior, uiState.mRequestedVisibleTypes, uiState.mPackageName, uiState.mTransientBarTypes, uiState.mLetterboxDetails);
        }
        return registerStatusBarResult;
    }

    public final void notifyBarAttachChanged() {
        UiThread.getHandler().post(new Runnable() { // from class: com.android.server.statusbar.StatusBarManagerService$$ExternalSyntheticLambda5
            @Override // java.lang.Runnable
            public final void run() {
                StatusBarManagerService.this.lambda$notifyBarAttachChanged$2();
            }
        });
        this.mHandler.post(new Runnable() { // from class: com.android.server.statusbar.StatusBarManagerService$$ExternalSyntheticLambda6
            @Override // java.lang.Runnable
            public final void run() {
                StatusBarManagerService.this.lambda$notifyBarAttachChanged$3();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$notifyBarAttachChanged$2() {
        GlobalActionsProvider.GlobalActionsListener globalActionsListener = this.mGlobalActionListener;
        if (globalActionsListener == null) {
            return;
        }
        globalActionsListener.onGlobalActionsAvailableChanged(this.mBar != null);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$notifyBarAttachChanged$3() {
        synchronized (this.mLock) {
            setUdfpsRefreshRateCallback(this.mUdfpsRefreshRateRequestCallback);
            setBiometicContextListener(this.mBiometricContextListener);
        }
    }

    @VisibleForTesting
    public void registerOverlayManager(IOverlayManager iOverlayManager) {
        this.mOverlayManager = iOverlayManager;
    }

    public void onPanelRevealed(boolean z, int i) {
        enforceStatusBarService();
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            this.mNotificationDelegate.onPanelRevealed(z, i);
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void clearNotificationEffects() throws RemoteException {
        enforceStatusBarService();
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            this.mNotificationDelegate.clearEffects();
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void onPanelHidden() throws RemoteException {
        enforceStatusBarService();
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            this.mNotificationDelegate.onPanelHidden();
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void shutdown() {
        enforceStatusBarService();
        ShutdownCheckPoints.recordCheckPoint(Binder.getCallingPid(), "userrequested");
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            this.mNotificationDelegate.prepareForPossibleShutdown();
            this.mHandler.post(new Runnable() { // from class: com.android.server.statusbar.StatusBarManagerService$$ExternalSyntheticLambda4
                @Override // java.lang.Runnable
                public final void run() {
                    StatusBarManagerService.lambda$shutdown$4(r1);
                }
            });
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public static /* synthetic */ void lambda$shutdown$4(String str) {
        ShutdownThread.shutdown(getUiContext(), str, false);
    }

    public void reboot(final boolean z) {
        enforceStatusBarService();
        final String str = z ? "safemode" : "userrequested";
        ShutdownCheckPoints.recordCheckPoint(Binder.getCallingPid(), str);
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            this.mNotificationDelegate.prepareForPossibleShutdown();
            this.mHandler.post(new Runnable() { // from class: com.android.server.statusbar.StatusBarManagerService$$ExternalSyntheticLambda3
                @Override // java.lang.Runnable
                public final void run() {
                    StatusBarManagerService.lambda$reboot$5(z, str);
                }
            });
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public static /* synthetic */ void lambda$reboot$5(boolean z, String str) {
        if (z) {
            ShutdownThread.rebootSafeMode(getUiContext(), true);
        } else {
            ShutdownThread.reboot(getUiContext(), str, false);
        }
    }

    public void restart() {
        enforceStatusBarService();
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            this.mHandler.post(new Runnable() { // from class: com.android.server.statusbar.StatusBarManagerService$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    StatusBarManagerService.this.lambda$restart$6();
                }
            });
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$restart$6() {
        this.mActivityManagerInternal.restart();
    }

    public void onGlobalActionsShown() {
        enforceStatusBarService();
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            GlobalActionsProvider.GlobalActionsListener globalActionsListener = this.mGlobalActionListener;
            if (globalActionsListener == null) {
                return;
            }
            globalActionsListener.onGlobalActionsShown();
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void onGlobalActionsHidden() {
        enforceStatusBarService();
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            GlobalActionsProvider.GlobalActionsListener globalActionsListener = this.mGlobalActionListener;
            if (globalActionsListener == null) {
                return;
            }
            globalActionsListener.onGlobalActionsDismissed();
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void onNotificationClick(String str, NotificationVisibility notificationVisibility) {
        enforceStatusBarService();
        int callingUid = Binder.getCallingUid();
        int callingPid = Binder.getCallingPid();
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            this.mNotificationDelegate.onNotificationClick(callingUid, callingPid, str, notificationVisibility);
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void onNotificationActionClick(String str, int i, Notification.Action action, NotificationVisibility notificationVisibility, boolean z) {
        enforceStatusBarService();
        int callingUid = Binder.getCallingUid();
        int callingPid = Binder.getCallingPid();
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            this.mNotificationDelegate.onNotificationActionClick(callingUid, callingPid, str, i, action, notificationVisibility, z);
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void onNotificationError(String str, String str2, int i, int i2, int i3, String str3, int i4) {
        enforceStatusBarService();
        int callingUid = Binder.getCallingUid();
        int callingPid = Binder.getCallingPid();
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            this.mNotificationDelegate.onNotificationError(callingUid, callingPid, str, str2, i, i2, i3, str3, i4);
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void onNotificationClear(String str, int i, String str2, int i2, int i3, NotificationVisibility notificationVisibility) {
        enforceStatusBarService();
        int callingUid = Binder.getCallingUid();
        int callingPid = Binder.getCallingPid();
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            this.mNotificationDelegate.onNotificationClear(callingUid, callingPid, str, i, str2, i2, i3, notificationVisibility);
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void onNotificationVisibilityChanged(NotificationVisibility[] notificationVisibilityArr, NotificationVisibility[] notificationVisibilityArr2) throws RemoteException {
        enforceStatusBarService();
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            this.mNotificationDelegate.onNotificationVisibilityChanged(notificationVisibilityArr, notificationVisibilityArr2);
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void onNotificationExpansionChanged(String str, boolean z, boolean z2, int i) throws RemoteException {
        enforceStatusBarService();
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            this.mNotificationDelegate.onNotificationExpansionChanged(str, z, z2, i);
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void onNotificationDirectReplied(String str) throws RemoteException {
        enforceStatusBarService();
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            this.mNotificationDelegate.onNotificationDirectReplied(str);
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void onNotificationSmartSuggestionsAdded(String str, int i, int i2, boolean z, boolean z2) {
        enforceStatusBarService();
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            this.mNotificationDelegate.onNotificationSmartSuggestionsAdded(str, i, i2, z, z2);
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void onNotificationSmartReplySent(String str, int i, CharSequence charSequence, int i2, boolean z) throws RemoteException {
        enforceStatusBarService();
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            this.mNotificationDelegate.onNotificationSmartReplySent(str, i, charSequence, i2, z);
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void onNotificationSettingsViewed(String str) throws RemoteException {
        enforceStatusBarService();
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            this.mNotificationDelegate.onNotificationSettingsViewed(str);
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void onClearAllNotifications(int i) {
        enforceStatusBarService();
        int callingUid = Binder.getCallingUid();
        int callingPid = Binder.getCallingPid();
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            this.mNotificationDelegate.onClearAll(callingUid, callingPid, i);
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void onNotificationBubbleChanged(String str, boolean z, int i) {
        enforceStatusBarService();
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            this.mNotificationDelegate.onNotificationBubbleChanged(str, z, i);
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void onBubbleMetadataFlagChanged(String str, int i) {
        enforceStatusBarService();
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            this.mNotificationDelegate.onBubbleMetadataFlagChanged(str, i);
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void hideCurrentInputMethodForBubbles() {
        enforceStatusBarService();
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            InputMethodManagerInternal.get().hideCurrentInputMethod(20);
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void grantInlineReplyUriPermission(String str, Uri uri, UserHandle userHandle, String str2) {
        enforceStatusBarService();
        int callingUid = Binder.getCallingUid();
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            this.mNotificationDelegate.grantInlineReplyUriPermission(str, uri, userHandle, str2, callingUid);
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void clearInlineReplyUriPermissions(String str) {
        enforceStatusBarService();
        int callingUid = Binder.getCallingUid();
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            this.mNotificationDelegate.clearInlineReplyUriPermissions(str, callingUid);
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void onNotificationFeedbackReceived(String str, Bundle bundle) {
        enforceStatusBarService();
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            this.mNotificationDelegate.onNotificationFeedbackReceived(str, bundle);
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    public void onShellCommand(FileDescriptor fileDescriptor, FileDescriptor fileDescriptor2, FileDescriptor fileDescriptor3, String[] strArr, ShellCallback shellCallback, ResultReceiver resultReceiver) {
        new StatusBarShellCommand(this, this.mContext).exec(this, fileDescriptor, fileDescriptor2, fileDescriptor3, strArr, shellCallback, resultReceiver);
    }

    public void showInattentiveSleepWarning() {
        enforceStatusBarService();
        if (this.mBar != null) {
            try {
                this.mBar.showInattentiveSleepWarning();
            } catch (RemoteException unused) {
            }
        }
    }

    public void dismissInattentiveSleepWarning(boolean z) {
        enforceStatusBarService();
        if (this.mBar != null) {
            try {
                this.mBar.dismissInattentiveSleepWarning(z);
            } catch (RemoteException unused) {
            }
        }
    }

    public void suppressAmbientDisplay(boolean z) {
        enforceStatusBarService();
        if (this.mBar != null) {
            try {
                this.mBar.suppressAmbientDisplay(z);
            } catch (RemoteException unused) {
            }
        }
    }

    public final void checkCallingUidPackage(String str, int i, int i2) {
        if (UserHandle.getAppId(i) == UserHandle.getAppId(this.mPackageManagerInternal.getPackageUid(str, 0L, i2))) {
            return;
        }
        throw new SecurityException("Package " + str + " does not belong to the calling uid " + i);
    }

    public final ResolveInfo isComponentValidTileService(ComponentName componentName, int i) {
        ServiceInfo serviceInfo;
        Intent intent = new Intent("android.service.quicksettings.action.QS_TILE");
        intent.setComponent(componentName);
        ResolveInfo resolveService = this.mPackageManagerInternal.resolveService(intent, intent.resolveTypeIfNeeded(this.mContext.getContentResolver()), 0L, i, Process.myUid());
        int componentEnabledSetting = this.mPackageManagerInternal.getComponentEnabledSetting(componentName, Process.myUid(), i);
        if (resolveService == null || (serviceInfo = resolveService.serviceInfo) == null || !resolveEnabledComponent(serviceInfo.enabled, componentEnabledSetting) || !"android.permission.BIND_QUICK_SETTINGS_TILE".equals(resolveService.serviceInfo.permission)) {
            return null;
        }
        return resolveService;
    }

    public void requestTileServiceListeningState(ComponentName componentName, int i) {
        int callingUid = Binder.getCallingUid();
        String packageName = componentName.getPackageName();
        if (CompatChanges.isChangeEnabled(172251878L, callingUid)) {
            int handleIncomingUser = this.mActivityManagerInternal.handleIncomingUser(Binder.getCallingPid(), callingUid, i, false, 0, "requestTileServiceListeningState", packageName);
            checkCallingUidPackage(packageName, callingUid, handleIncomingUser);
            if (handleIncomingUser != this.mActivityManagerInternal.getCurrentUserId()) {
                if (CompatChanges.isChangeEnabled(242194868L, callingUid)) {
                    return;
                }
                throw new IllegalArgumentException("User " + handleIncomingUser + " is not the current user.");
            }
        }
        if (this.mBar != null) {
            try {
                this.mBar.requestTileServiceListeningState(componentName);
            } catch (RemoteException e) {
                Slog.e("StatusBarManagerService", "requestTileServiceListeningState", e);
            }
        }
    }

    public void requestAddTile(final ComponentName componentName, CharSequence charSequence, Icon icon, final int i, final IAddTileResultCallback iAddTileResultCallback) {
        int callingUid = Binder.getCallingUid();
        final String packageName = componentName.getPackageName();
        this.mActivityManagerInternal.handleIncomingUser(Binder.getCallingPid(), callingUid, i, false, 0, "requestAddTile", packageName);
        checkCallingUidPackage(packageName, callingUid, i);
        if (i != this.mActivityManagerInternal.getCurrentUserId()) {
            try {
                iAddTileResultCallback.onTileRequest(1003);
                return;
            } catch (RemoteException e) {
                Slog.e("StatusBarManagerService", "requestAddTile", e);
                return;
            }
        }
        ResolveInfo isComponentValidTileService = isComponentValidTileService(componentName, i);
        if (isComponentValidTileService == null || !isComponentValidTileService.serviceInfo.exported) {
            try {
                iAddTileResultCallback.onTileRequest(1002);
            } catch (RemoteException e2) {
                Slog.e("StatusBarManagerService", "requestAddTile", e2);
            }
        } else if (ActivityManager.RunningAppProcessInfo.procStateToImportance(this.mActivityManagerInternal.getUidProcessState(callingUid)) != 100) {
            try {
                iAddTileResultCallback.onTileRequest(1004);
            } catch (RemoteException e3) {
                Slog.e("StatusBarManagerService", "requestAddTile", e3);
            }
        } else {
            synchronized (this.mCurrentRequestAddTilePackages) {
                Long l = this.mCurrentRequestAddTilePackages.get(packageName);
                long nanoTime = System.nanoTime();
                if (l != null && nanoTime - l.longValue() < REQUEST_TIME_OUT) {
                    try {
                        iAddTileResultCallback.onTileRequest(1001);
                    } catch (RemoteException e4) {
                        Slog.e("StatusBarManagerService", "requestAddTile", e4);
                    }
                    return;
                }
                if (l != null) {
                    cancelRequestAddTileInternal(packageName);
                }
                this.mCurrentRequestAddTilePackages.put(packageName, Long.valueOf(nanoTime));
                if (this.mTileRequestTracker.shouldBeDenied(i, componentName)) {
                    if (clearTileAddRequest(packageName)) {
                        try {
                            iAddTileResultCallback.onTileRequest(0);
                            return;
                        } catch (RemoteException e5) {
                            Slog.e("StatusBarManagerService", "requestAddTile - callback", e5);
                            return;
                        }
                    }
                    return;
                }
                IAddTileResultCallback.Stub stub = new IAddTileResultCallback.Stub() { // from class: com.android.server.statusbar.StatusBarManagerService.3
                    public void onTileRequest(int i2) {
                        if (i2 == 3) {
                            i2 = 0;
                        } else if (i2 == 0) {
                            StatusBarManagerService.this.mTileRequestTracker.addDenial(i, componentName);
                        } else if (i2 == 2) {
                            StatusBarManagerService.this.mTileRequestTracker.resetRequests(i, componentName);
                        }
                        if (StatusBarManagerService.this.clearTileAddRequest(packageName)) {
                            try {
                                iAddTileResultCallback.onTileRequest(i2);
                            } catch (RemoteException e6) {
                                Slog.e("StatusBarManagerService", "requestAddTile - callback", e6);
                            }
                        }
                    }
                };
                CharSequence loadLabel = isComponentValidTileService.serviceInfo.applicationInfo.loadLabel(this.mContext.getPackageManager());
                if (this.mBar != null) {
                    try {
                        this.mBar.requestAddTile(componentName, loadLabel, charSequence, icon, stub);
                        return;
                    } catch (RemoteException e6) {
                        Slog.e("StatusBarManagerService", "requestAddTile", e6);
                    }
                }
                clearTileAddRequest(packageName);
                try {
                    iAddTileResultCallback.onTileRequest(1005);
                } catch (RemoteException e7) {
                    Slog.e("StatusBarManagerService", "requestAddTile", e7);
                }
            }
        }
    }

    public void cancelRequestAddTile(String str) {
        enforceStatusBar();
        cancelRequestAddTileInternal(str);
    }

    public final void cancelRequestAddTileInternal(String str) {
        clearTileAddRequest(str);
        if (this.mBar != null) {
            try {
                this.mBar.cancelRequestAddTile(str);
            } catch (RemoteException e) {
                Slog.e("StatusBarManagerService", "requestAddTile", e);
            }
        }
    }

    public final boolean clearTileAddRequest(String str) {
        boolean z;
        synchronized (this.mCurrentRequestAddTilePackages) {
            z = this.mCurrentRequestAddTilePackages.remove(str) != null;
        }
        return z;
    }

    public void onSessionStarted(int i, InstanceId instanceId) {
        this.mSessionMonitor.onSessionStarted(i, instanceId);
    }

    public void onSessionEnded(int i, InstanceId instanceId) {
        this.mSessionMonitor.onSessionEnded(i, instanceId);
    }

    public void registerSessionListener(int i, ISessionListener iSessionListener) {
        this.mSessionMonitor.registerSessionListener(i, iSessionListener);
    }

    public void unregisterSessionListener(int i, ISessionListener iSessionListener) {
        this.mSessionMonitor.unregisterSessionListener(i, iSessionListener);
    }

    public String[] getStatusBarIcons() {
        return this.mContext.getResources().getStringArray(17236143);
    }

    public void setNavBarMode(int i) {
        enforceStatusBar();
        if (i != 0 && i != 1) {
            throw new IllegalArgumentException("Supplied navBarMode not supported: " + i);
        }
        int i2 = this.mCurrentUserId;
        int userId = UserHandle.getUserId(Binder.getCallingUid());
        if (this.mCurrentUserId != userId && !doesCallerHoldInteractAcrossUserPermission()) {
            throw new SecurityException("Calling user id: " + userId + ", cannot call on behalf of current user id: " + this.mCurrentUserId + ".");
        }
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            try {
                Settings.Secure.putIntForUser(this.mContext.getContentResolver(), "nav_bar_kids_mode", i, i2);
                Settings.Secure.putIntForUser(this.mContext.getContentResolver(), "nav_bar_force_visible", i, i2);
                IOverlayManager overlayManager = getOverlayManager();
                if (overlayManager != null && i == 1 && isPackageSupported("com.android.internal.systemui.navbar.threebutton")) {
                    overlayManager.setEnabledExclusiveInCategory("com.android.internal.systemui.navbar.threebutton", i2);
                }
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public int getNavBarMode() {
        enforceStatusBar();
        int i = this.mCurrentUserId;
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            int intForUser = Settings.Secure.getIntForUser(this.mContext.getContentResolver(), "nav_bar_kids_mode", i);
            Binder.restoreCallingIdentity(clearCallingIdentity);
            return intForUser;
        } catch (Settings.SettingNotFoundException unused) {
            Binder.restoreCallingIdentity(clearCallingIdentity);
            return 0;
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(clearCallingIdentity);
            throw th;
        }
    }

    public final boolean isPackageSupported(String str) {
        if (str == null) {
            return false;
        }
        try {
            return this.mContext.getPackageManager().getPackageInfo(str, PackageManager.PackageInfoFlags.of(0L)) != null;
        } catch (PackageManager.NameNotFoundException unused) {
            return false;
        }
    }

    public void updateMediaTapToTransferSenderDisplay(int i, MediaRoute2Info mediaRoute2Info, IUndoMediaTransferCallback iUndoMediaTransferCallback) {
        enforceMediaContentControl();
        if (this.mBar != null) {
            try {
                this.mBar.updateMediaTapToTransferSenderDisplay(i, mediaRoute2Info, iUndoMediaTransferCallback);
            } catch (RemoteException e) {
                Slog.e("StatusBarManagerService", "updateMediaTapToTransferSenderDisplay", e);
            }
        }
    }

    public void updateMediaTapToTransferReceiverDisplay(int i, MediaRoute2Info mediaRoute2Info, Icon icon, CharSequence charSequence) {
        enforceMediaContentControl();
        if (this.mBar != null) {
            try {
                this.mBar.updateMediaTapToTransferReceiverDisplay(i, mediaRoute2Info, icon, charSequence);
            } catch (RemoteException e) {
                Slog.e("StatusBarManagerService", "updateMediaTapToTransferReceiverDisplay", e);
            }
        }
    }

    public void registerNearbyMediaDevicesProvider(INearbyMediaDevicesProvider iNearbyMediaDevicesProvider) {
        enforceMediaContentControl();
        if (this.mBar != null) {
            try {
                this.mBar.registerNearbyMediaDevicesProvider(iNearbyMediaDevicesProvider);
            } catch (RemoteException e) {
                Slog.e("StatusBarManagerService", "registerNearbyMediaDevicesProvider", e);
            }
        }
    }

    public void unregisterNearbyMediaDevicesProvider(INearbyMediaDevicesProvider iNearbyMediaDevicesProvider) {
        enforceMediaContentControl();
        if (this.mBar != null) {
            try {
                this.mBar.unregisterNearbyMediaDevicesProvider(iNearbyMediaDevicesProvider);
            } catch (RemoteException e) {
                Slog.e("StatusBarManagerService", "unregisterNearbyMediaDevicesProvider", e);
            }
        }
    }

    @RequiresPermission("android.permission.CONTROL_DEVICE_STATE")
    public void showRearDisplayDialog(int i) {
        enforceControlDeviceStatePermission();
        if (this.mBar != null) {
            try {
                this.mBar.showRearDisplayDialog(i);
            } catch (RemoteException e) {
                Slog.e("StatusBarManagerService", "showRearDisplayDialog", e);
            }
        }
    }

    public void passThroughShellCommand(String[] strArr, FileDescriptor fileDescriptor) {
        enforceStatusBarOrShell();
        if (this.mBar == null) {
            return;
        }
        try {
            TransferPipe transferPipe = new TransferPipe();
            transferPipe.setBufferPrefix("  ");
            this.mBar.passThroughShellCommand(strArr, transferPipe.getWriteFd());
            transferPipe.go(fileDescriptor);
            transferPipe.close();
        } catch (Throwable th) {
            Slog.e("StatusBarManagerService", "Error sending command to IStatusBar", th);
        }
    }

    public void manageDisableListLocked(int i, int i2, IBinder iBinder, String str, int i3) {
        Pair<Integer, DisableRecord> findMatchingRecordLocked = findMatchingRecordLocked(iBinder, i);
        int intValue = ((Integer) findMatchingRecordLocked.first).intValue();
        DisableRecord disableRecord = (DisableRecord) findMatchingRecordLocked.second;
        if (!iBinder.isBinderAlive()) {
            if (disableRecord != null) {
                this.mDisableRecords.remove(intValue);
                disableRecord.token.unlinkToDeath(disableRecord, 0);
            }
        } else if (disableRecord != null) {
            disableRecord.setFlags(i2, i3, str);
            if (disableRecord.isEmpty()) {
                this.mDisableRecords.remove(intValue);
                disableRecord.token.unlinkToDeath(disableRecord, 0);
            }
        } else {
            DisableRecord disableRecord2 = new DisableRecord(i, iBinder);
            disableRecord2.setFlags(i2, i3, str);
            this.mDisableRecords.add(disableRecord2);
        }
    }

    @GuardedBy({"mLock"})
    public final Pair<Integer, DisableRecord> findMatchingRecordLocked(IBinder iBinder, int i) {
        DisableRecord disableRecord;
        int size = this.mDisableRecords.size();
        int i2 = 0;
        while (true) {
            if (i2 >= size) {
                disableRecord = null;
                break;
            }
            disableRecord = this.mDisableRecords.get(i2);
            if (disableRecord.token == iBinder && disableRecord.userId == i) {
                break;
            }
            i2++;
        }
        return new Pair<>(Integer.valueOf(i2), disableRecord);
    }

    public int gatherDisableActionsLocked(int i, int i2) {
        int size = this.mDisableRecords.size();
        int i3 = 0;
        for (int i4 = 0; i4 < size; i4++) {
            DisableRecord disableRecord = this.mDisableRecords.get(i4);
            if (disableRecord.userId == i) {
                i3 |= disableRecord.getFlags(i2);
            }
        }
        return i3;
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        ArrayList arrayList;
        if (DumpUtils.checkDumpPermission(this.mContext, "StatusBarManagerService", printWriter)) {
            boolean z = false;
            for (String str : strArr) {
                if ("--proto".equals(str)) {
                    z = true;
                }
            }
            if (z) {
                if (this.mBar == null) {
                    return;
                }
                try {
                    TransferPipe transferPipe = new TransferPipe();
                    this.mBar.dumpProto(strArr, transferPipe.getWriteFd());
                    transferPipe.go(fileDescriptor);
                    transferPipe.close();
                    return;
                } catch (Throwable th) {
                    Slog.e("StatusBarManagerService", "Error sending command to IStatusBar", th);
                    return;
                }
            }
            synchronized (this.mLock) {
                for (int i = 0; i < this.mDisplayUiState.size(); i++) {
                    int keyAt = this.mDisplayUiState.keyAt(i);
                    UiState uiState = this.mDisplayUiState.get(keyAt);
                    printWriter.println("  displayId=" + keyAt);
                    printWriter.println("    mDisabled1=0x" + Integer.toHexString(uiState.getDisabled1()));
                    printWriter.println("    mDisabled2=0x" + Integer.toHexString(uiState.getDisabled2()));
                }
                int size = this.mDisableRecords.size();
                printWriter.println("  mDisableRecords.size=" + size);
                for (int i2 = 0; i2 < size; i2++) {
                    printWriter.println("    [" + i2 + "] " + this.mDisableRecords.get(i2));
                }
                printWriter.println("  mCurrentUserId=" + this.mCurrentUserId);
                printWriter.println("  mIcons=");
                for (String str2 : this.mIcons.keySet()) {
                    printWriter.println("    ");
                    printWriter.print(str2);
                    printWriter.print(" -> ");
                    StatusBarIcon statusBarIcon = this.mIcons.get(str2);
                    printWriter.print(statusBarIcon);
                    if (!TextUtils.isEmpty(statusBarIcon.contentDescription)) {
                        printWriter.print(" \"");
                        printWriter.print(statusBarIcon.contentDescription);
                        printWriter.print("\"");
                    }
                    printWriter.println();
                }
                synchronized (this.mCurrentRequestAddTilePackages) {
                    arrayList = new ArrayList(this.mCurrentRequestAddTilePackages.keySet());
                }
                printWriter.println("  mCurrentRequestAddTilePackages=[");
                int size2 = arrayList.size();
                for (int i3 = 0; i3 < size2; i3++) {
                    printWriter.println("    " + ((String) arrayList.get(i3)) + ",");
                }
                printWriter.println("  ]");
                this.mTileRequestTracker.dump(fileDescriptor, new IndentingPrintWriter(printWriter, "  ").increaseIndent(), strArr);
            }
        }
    }

    public static final Context getUiContext() {
        return ActivityThread.currentActivityThread().getSystemUiContext();
    }
}
