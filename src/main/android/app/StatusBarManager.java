package android.app;

import android.annotation.SystemApi;
import android.app.StatusBarManager;
import android.app.compat.CompatChanges;
import android.content.ComponentName;
import android.content.Context;
import android.graphics.drawable.Icon;
import android.media.INearbyMediaDevicesProvider;
import android.media.INearbyMediaDevicesUpdateCallback;
import android.media.MediaRoute2Info;
import android.media.NearbyDevice;
import android.media.NearbyMediaDevicesProvider;
import android.p008os.Binder;
import android.p008os.Bundle;
import android.p008os.IBinder;
import android.p008os.RemoteException;
import android.p008os.ServiceManager;
import android.p008os.UserHandle;
import android.util.Pair;
import android.util.Slog;
import com.android.internal.statusbar.AppClipsServiceConnector;
import com.android.internal.statusbar.IAddTileResultCallback;
import com.android.internal.statusbar.IStatusBarService;
import com.android.internal.statusbar.IUndoMediaTransferCallback;
import com.android.internal.statusbar.NotificationVisibility;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
/* loaded from: classes.dex */
public class StatusBarManager {
    public static final Set<Integer> ALL_SESSIONS = Set.of(1, 2);
    public static final int CAMERA_LAUNCH_SOURCE_LIFT_TRIGGER = 2;
    public static final int CAMERA_LAUNCH_SOURCE_POWER_DOUBLE_TAP = 1;
    public static final int CAMERA_LAUNCH_SOURCE_QUICK_AFFORDANCE = 3;
    public static final int CAMERA_LAUNCH_SOURCE_WIGGLE = 0;
    public static final int DEFAULT_SETUP_DISABLE2_FLAGS = 0;
    public static final int DEFAULT_SETUP_DISABLE_FLAGS = 61145088;
    private static final int DEFAULT_SIM_LOCKED_DISABLED_FLAGS = 65536;
    public static final int DISABLE2_GLOBAL_ACTIONS = 8;
    public static final int DISABLE2_MASK = 31;
    public static final int DISABLE2_NONE = 0;
    public static final int DISABLE2_NOTIFICATION_SHADE = 4;
    public static final int DISABLE2_QUICK_SETTINGS = 1;
    public static final int DISABLE2_ROTATE_SUGGESTIONS = 16;
    public static final int DISABLE2_SYSTEM_ICONS = 2;
    public static final int DISABLE_BACK = 4194304;
    public static final int DISABLE_CLOCK = 8388608;
    public static final int DISABLE_EXPAND = 65536;
    public static final int DISABLE_HOME = 2097152;
    public static final int DISABLE_MASK = 134152192;
    @Deprecated
    public static final int DISABLE_NAVIGATION = 18874368;
    public static final int DISABLE_NONE = 0;
    public static final int DISABLE_NOTIFICATION_ALERTS = 262144;
    public static final int DISABLE_NOTIFICATION_ICONS = 131072;
    @Deprecated
    public static final int DISABLE_NOTIFICATION_TICKER = 524288;
    public static final int DISABLE_ONGOING_CALL_CHIP = 67108864;
    public static final int DISABLE_RECENT = 16777216;
    public static final int DISABLE_SEARCH = 33554432;
    public static final int DISABLE_SYSTEM_INFO = 1048576;
    private static final long MEDIA_CONTROL_SESSION_ACTIONS = 203800354;
    @SystemApi
    public static final int MEDIA_TRANSFER_RECEIVER_STATE_CLOSE_TO_SENDER = 0;
    @SystemApi
    public static final int MEDIA_TRANSFER_RECEIVER_STATE_FAR_FROM_SENDER = 1;
    @SystemApi
    public static final int MEDIA_TRANSFER_RECEIVER_STATE_TRANSFER_TO_RECEIVER_FAILED = 3;
    @SystemApi
    public static final int MEDIA_TRANSFER_RECEIVER_STATE_TRANSFER_TO_RECEIVER_SUCCEEDED = 2;
    @SystemApi
    public static final int MEDIA_TRANSFER_SENDER_STATE_ALMOST_CLOSE_TO_END_CAST = 1;
    @SystemApi
    public static final int MEDIA_TRANSFER_SENDER_STATE_ALMOST_CLOSE_TO_START_CAST = 0;
    @SystemApi
    public static final int MEDIA_TRANSFER_SENDER_STATE_FAR_FROM_RECEIVER = 8;
    @SystemApi
    public static final int MEDIA_TRANSFER_SENDER_STATE_TRANSFER_TO_RECEIVER_FAILED = 6;
    @SystemApi
    public static final int MEDIA_TRANSFER_SENDER_STATE_TRANSFER_TO_RECEIVER_SUCCEEDED = 4;
    @SystemApi
    public static final int MEDIA_TRANSFER_SENDER_STATE_TRANSFER_TO_RECEIVER_TRIGGERED = 2;
    @SystemApi
    public static final int MEDIA_TRANSFER_SENDER_STATE_TRANSFER_TO_THIS_DEVICE_FAILED = 7;
    @SystemApi
    public static final int MEDIA_TRANSFER_SENDER_STATE_TRANSFER_TO_THIS_DEVICE_SUCCEEDED = 5;
    @SystemApi
    public static final int MEDIA_TRANSFER_SENDER_STATE_TRANSFER_TO_THIS_DEVICE_TRIGGERED = 3;
    public static final int NAVIGATION_HINT_BACK_ALT = 1;
    public static final int NAVIGATION_HINT_IME_SHOWN = 2;
    public static final int NAVIGATION_HINT_IME_SWITCHER_SHOWN = 4;
    @SystemApi
    public static final int NAV_BAR_MODE_DEFAULT = 0;
    @SystemApi
    public static final int NAV_BAR_MODE_KIDS = 1;
    public static final int SESSION_BIOMETRIC_PROMPT = 2;
    public static final int SESSION_KEYGUARD = 1;
    public static final int TILE_ADD_REQUEST_ERROR_APP_NOT_IN_FOREGROUND = 1004;
    public static final int TILE_ADD_REQUEST_ERROR_BAD_COMPONENT = 1002;
    public static final int TILE_ADD_REQUEST_ERROR_MISMATCHED_PACKAGE = 1000;
    public static final int TILE_ADD_REQUEST_ERROR_NOT_CURRENT_USER = 1003;
    public static final int TILE_ADD_REQUEST_ERROR_NO_STATUS_BAR_SERVICE = 1005;
    public static final int TILE_ADD_REQUEST_ERROR_REQUEST_IN_PROGRESS = 1001;
    private static final int TILE_ADD_REQUEST_FIRST_ERROR_CODE = 1000;
    public static final int TILE_ADD_REQUEST_RESULT_DIALOG_DISMISSED = 3;
    public static final int TILE_ADD_REQUEST_RESULT_TILE_ADDED = 2;
    public static final int TILE_ADD_REQUEST_RESULT_TILE_ALREADY_ADDED = 1;
    public static final int TILE_ADD_REQUEST_RESULT_TILE_NOT_ADDED = 0;
    public static final int WINDOW_NAVIGATION_BAR = 2;
    public static final int WINDOW_STATE_HIDDEN = 2;
    public static final int WINDOW_STATE_HIDING = 1;
    public static final int WINDOW_STATE_SHOWING = 0;
    public static final int WINDOW_STATUS_BAR = 1;
    private Context mContext;
    private IStatusBarService mService;
    private final Map<NearbyMediaDevicesProvider, NearbyMediaDevicesProviderWrapper> nearbyMediaDevicesProviderMap = new HashMap();
    private IBinder mToken = new Binder();

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface Disable2Flags {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface DisableFlags {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface MediaTransferReceiverState {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface MediaTransferSenderState {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface NavBarMode {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface RequestResult {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface SessionFlags {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface WindowType {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface WindowVisibleState {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public StatusBarManager(Context context) {
        this.mContext = context;
    }

    private synchronized IStatusBarService getService() {
        if (this.mService == null) {
            IStatusBarService asInterface = IStatusBarService.Stub.asInterface(ServiceManager.getService(Context.STATUS_BAR_SERVICE));
            this.mService = asInterface;
            if (asInterface == null) {
                Slog.m90w("StatusBarManager", "warning: no STATUS_BAR_SERVICE");
            }
        }
        return this.mService;
    }

    public void disable(int what) {
        try {
            int userId = Binder.getCallingUserHandle().getIdentifier();
            IStatusBarService svc = getService();
            if (svc != null) {
                svc.disableForUser(what, this.mToken, this.mContext.getPackageName(), userId);
            }
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    public void disable2(int what) {
        try {
            int userId = Binder.getCallingUserHandle().getIdentifier();
            IStatusBarService svc = getService();
            if (svc != null) {
                svc.disable2ForUser(what, this.mToken, this.mContext.getPackageName(), userId);
            }
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    public void clickNotification(String key, int rank, int count, boolean visible) {
        clickNotificationInternal(key, rank, count, visible);
    }

    private void clickNotificationInternal(String key, int rank, int count, boolean visible) {
        try {
            IStatusBarService svc = getService();
            if (svc != null) {
                svc.onNotificationClick(key, NotificationVisibility.obtain(key, rank, count, visible));
            }
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    public void sendNotificationFeedback(String key, Bundle feedback) {
        try {
            IStatusBarService svc = getService();
            if (svc != null) {
                svc.onNotificationFeedbackReceived(key, feedback);
            }
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    public void expandNotificationsPanel() {
        try {
            IStatusBarService svc = getService();
            if (svc != null) {
                svc.expandNotificationsPanel();
            }
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    public void collapsePanels() {
        try {
            IStatusBarService svc = getService();
            if (svc != null) {
                svc.collapsePanels();
            }
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    public void togglePanel() {
        try {
            IStatusBarService svc = getService();
            if (svc != null) {
                svc.togglePanel();
            }
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    public void handleSystemKey(int key) {
        try {
            IStatusBarService svc = getService();
            if (svc != null) {
                svc.handleSystemKey(key);
            }
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    public void expandSettingsPanel() {
        expandSettingsPanel(null);
    }

    public void expandSettingsPanel(String subPanel) {
        try {
            IStatusBarService svc = getService();
            if (svc != null) {
                svc.expandSettingsPanel(subPanel);
            }
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    public void setIcon(String slot, int iconId, int iconLevel, String contentDescription) {
        try {
            IStatusBarService svc = getService();
            if (svc != null) {
                svc.setIcon(slot, this.mContext.getPackageName(), iconId, iconLevel, contentDescription);
            }
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    public void removeIcon(String slot) {
        try {
            IStatusBarService svc = getService();
            if (svc != null) {
                svc.removeIcon(slot);
            }
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    public void setIconVisibility(String slot, boolean visible) {
        try {
            IStatusBarService svc = getService();
            if (svc != null) {
                svc.setIconVisibility(slot, visible);
            }
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    @SystemApi
    public void setDisabledForSetup(boolean disabled) {
        try {
            int userId = Binder.getCallingUserHandle().getIdentifier();
            IStatusBarService svc = getService();
            if (svc != null) {
                svc.disableForUser(disabled ? DEFAULT_SETUP_DISABLE_FLAGS : 0, this.mToken, this.mContext.getPackageName(), userId);
                if (disabled) {
                }
                svc.disable2ForUser(0, this.mToken, this.mContext.getPackageName(), userId);
            }
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    @SystemApi(client = SystemApi.Client.MODULE_LIBRARIES)
    public void setExpansionDisabledForSimNetworkLock(boolean disabled) {
        try {
            int userId = Binder.getCallingUserHandle().getIdentifier();
            IStatusBarService svc = getService();
            if (svc != null) {
                svc.disableForUser(disabled ? 65536 : 0, this.mToken, this.mContext.getPackageName(), userId);
            }
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    @SystemApi
    public DisableInfo getDisableInfo() {
        try {
            int userId = Binder.getCallingUserHandle().getIdentifier();
            IStatusBarService svc = getService();
            int[] flags = {0, 0};
            if (svc != null) {
                flags = svc.getDisableFlags(this.mToken, userId);
            }
            return new DisableInfo(flags[0], flags[1]);
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    public void requestTileServiceListeningState(ComponentName componentName) {
        Objects.requireNonNull(componentName);
        try {
            getService().requestTileServiceListeningState(componentName, this.mContext.getUserId());
        } catch (RemoteException ex) {
            throw ex.rethrowFromSystemServer();
        }
    }

    public void requestAddTileService(ComponentName tileServiceComponentName, CharSequence tileLabel, Icon icon, Executor resultExecutor, final Consumer<Integer> resultCallback) {
        Objects.requireNonNull(tileServiceComponentName);
        Objects.requireNonNull(tileLabel);
        Objects.requireNonNull(icon);
        Objects.requireNonNull(resultExecutor);
        Objects.requireNonNull(resultCallback);
        if (!tileServiceComponentName.getPackageName().equals(this.mContext.getPackageName())) {
            resultExecutor.execute(new Runnable() { // from class: android.app.StatusBarManager$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    resultCallback.accept(1000);
                }
            });
            return;
        }
        int userId = this.mContext.getUserId();
        RequestResultCallback callbackProxy = new RequestResultCallback(resultExecutor, resultCallback);
        IStatusBarService svc = getService();
        try {
            svc.requestAddTile(tileServiceComponentName, tileLabel, icon, userId, callbackProxy);
        } catch (RemoteException ex) {
            ex.rethrowFromSystemServer();
        }
    }

    public void cancelRequestAddTile(String packageName) {
        Objects.requireNonNull(packageName);
        IStatusBarService svc = getService();
        try {
            svc.cancelRequestAddTile(packageName);
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    @SystemApi
    public void setNavBarMode(int navBarMode) {
        if (navBarMode != 0 && navBarMode != 1) {
            throw new IllegalArgumentException("Supplied navBarMode not supported: " + navBarMode);
        }
        try {
            IStatusBarService svc = getService();
            if (svc != null) {
                svc.setNavBarMode(navBarMode);
            }
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @SystemApi
    public int getNavBarMode() {
        try {
            IStatusBarService svc = getService();
            if (svc == null) {
                return 0;
            }
            int navBarMode = svc.getNavBarMode();
            return navBarMode;
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @SystemApi
    public void updateMediaTapToTransferSenderDisplay(int displayState, MediaRoute2Info routeInfo, Executor undoExecutor, Runnable undoCallback) {
        Objects.requireNonNull(routeInfo);
        if (displayState != 4 && displayState != 5 && undoCallback != null) {
            throw new IllegalArgumentException("The undoCallback should only be provided when the state is a transfer succeeded state");
        }
        if (undoCallback != null && undoExecutor == null) {
            throw new IllegalArgumentException("You must pass an executor when you pass an undo callback");
        }
        IStatusBarService svc = getService();
        UndoCallback callbackProxy = null;
        if (undoExecutor != null) {
            try {
                callbackProxy = new UndoCallback(undoExecutor, undoCallback);
            } catch (RemoteException e) {
                e.rethrowFromSystemServer();
                return;
            }
        }
        svc.updateMediaTapToTransferSenderDisplay(displayState, routeInfo, callbackProxy);
    }

    @SystemApi
    public void updateMediaTapToTransferReceiverDisplay(int displayState, MediaRoute2Info routeInfo, Icon appIcon, CharSequence appName) {
        Objects.requireNonNull(routeInfo);
        IStatusBarService svc = getService();
        try {
            svc.updateMediaTapToTransferReceiverDisplay(displayState, routeInfo, appIcon, appName);
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    @SystemApi
    public void registerNearbyMediaDevicesProvider(NearbyMediaDevicesProvider provider) {
        Objects.requireNonNull(provider);
        if (this.nearbyMediaDevicesProviderMap.containsKey(provider)) {
            return;
        }
        try {
            IStatusBarService svc = getService();
            NearbyMediaDevicesProviderWrapper providerWrapper = new NearbyMediaDevicesProviderWrapper(provider);
            this.nearbyMediaDevicesProviderMap.put(provider, providerWrapper);
            svc.registerNearbyMediaDevicesProvider(providerWrapper);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    @SystemApi
    public void unregisterNearbyMediaDevicesProvider(NearbyMediaDevicesProvider provider) {
        Objects.requireNonNull(provider);
        if (!this.nearbyMediaDevicesProviderMap.containsKey(provider)) {
            return;
        }
        try {
            IStatusBarService svc = getService();
            NearbyMediaDevicesProviderWrapper providerWrapper = this.nearbyMediaDevicesProviderMap.get(provider);
            this.nearbyMediaDevicesProviderMap.remove(provider);
            svc.unregisterNearbyMediaDevicesProvider(providerWrapper);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public static boolean useMediaSessionActionsForApp(String packageName, UserHandle user) {
        return CompatChanges.isChangeEnabled(MEDIA_CONTROL_SESSION_ACTIONS, packageName, user);
    }

    public boolean canLaunchCaptureContentActivityForNote(Activity activity) {
        Objects.requireNonNull(activity);
        IBinder activityToken = activity.getActivityToken();
        int taskId = ActivityClient.getInstance().getTaskForActivity(activityToken, false);
        return new AppClipsServiceConnector(this.mContext).canLaunchCaptureContentActivityForNote(taskId);
    }

    public static String windowStateToString(int state) {
        return state == 1 ? "WINDOW_STATE_HIDING" : state == 2 ? "WINDOW_STATE_HIDDEN" : state == 0 ? "WINDOW_STATE_SHOWING" : "WINDOW_STATE_UNKNOWN";
    }

    @SystemApi
    /* loaded from: classes.dex */
    public static final class DisableInfo {
        private boolean mClock;
        private boolean mNavigateHome;
        private boolean mNotificationIcons;
        private boolean mNotificationPeeking;
        private boolean mRecents;
        private boolean mRotationSuggestion;
        private boolean mSearch;
        private boolean mStatusBarExpansion;
        private boolean mSystemIcons;

        public DisableInfo(int flags1, int flags2) {
            this.mStatusBarExpansion = (65536 & flags1) != 0;
            this.mNavigateHome = (2097152 & flags1) != 0;
            this.mNotificationPeeking = (262144 & flags1) != 0;
            this.mRecents = (16777216 & flags1) != 0;
            this.mSearch = (33554432 & flags1) != 0;
            this.mSystemIcons = (1048576 & flags1) != 0;
            this.mClock = (8388608 & flags1) != 0;
            this.mNotificationIcons = (131072 & flags1) != 0;
            this.mRotationSuggestion = (flags2 & 16) != 0;
        }

        public DisableInfo() {
        }

        @SystemApi
        public boolean isStatusBarExpansionDisabled() {
            return this.mStatusBarExpansion;
        }

        public void setStatusBarExpansionDisabled(boolean disabled) {
            this.mStatusBarExpansion = disabled;
        }

        @SystemApi
        public boolean isNavigateToHomeDisabled() {
            return this.mNavigateHome;
        }

        public void setNagivationHomeDisabled(boolean disabled) {
            this.mNavigateHome = disabled;
        }

        @SystemApi
        public boolean isNotificationPeekingDisabled() {
            return this.mNotificationPeeking;
        }

        public void setNotificationPeekingDisabled(boolean disabled) {
            this.mNotificationPeeking = disabled;
        }

        @SystemApi
        public boolean isRecentsDisabled() {
            return this.mRecents;
        }

        public void setRecentsDisabled(boolean disabled) {
            this.mRecents = disabled;
        }

        @SystemApi
        public boolean isSearchDisabled() {
            return this.mSearch;
        }

        public void setSearchDisabled(boolean disabled) {
            this.mSearch = disabled;
        }

        public boolean areSystemIconsDisabled() {
            return this.mSystemIcons;
        }

        public void setSystemIconsDisabled(boolean disabled) {
            this.mSystemIcons = disabled;
        }

        public boolean isClockDisabled() {
            return this.mClock;
        }

        public void setClockDisabled(boolean disabled) {
            this.mClock = disabled;
        }

        public boolean areNotificationIconsDisabled() {
            return this.mNotificationIcons;
        }

        public void setNotificationIconsDisabled(boolean disabled) {
            this.mNotificationIcons = disabled;
        }

        public boolean isRotationSuggestionDisabled() {
            return this.mRotationSuggestion;
        }

        @SystemApi
        public boolean areAllComponentsEnabled() {
            return (this.mStatusBarExpansion || this.mNavigateHome || this.mNotificationPeeking || this.mRecents || this.mSearch || this.mSystemIcons || this.mClock || this.mNotificationIcons || this.mRotationSuggestion) ? false : true;
        }

        public void setEnableAll() {
            this.mStatusBarExpansion = false;
            this.mNavigateHome = false;
            this.mNotificationPeeking = false;
            this.mRecents = false;
            this.mSearch = false;
            this.mSystemIcons = false;
            this.mClock = false;
            this.mNotificationIcons = false;
            this.mRotationSuggestion = false;
        }

        public boolean areAllComponentsDisabled() {
            return this.mStatusBarExpansion && this.mNavigateHome && this.mNotificationPeeking && this.mRecents && this.mSearch && this.mSystemIcons && this.mClock && this.mNotificationIcons && this.mRotationSuggestion;
        }

        public void setDisableAll() {
            this.mStatusBarExpansion = true;
            this.mNavigateHome = true;
            this.mNotificationPeeking = true;
            this.mRecents = true;
            this.mSearch = true;
            this.mSystemIcons = true;
            this.mClock = true;
            this.mNotificationIcons = true;
            this.mRotationSuggestion = true;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("DisableInfo: ");
            sb.append(" mStatusBarExpansion=").append(this.mStatusBarExpansion ? "disabled" : "enabled");
            sb.append(" mNavigateHome=").append(this.mNavigateHome ? "disabled" : "enabled");
            sb.append(" mNotificationPeeking=").append(this.mNotificationPeeking ? "disabled" : "enabled");
            sb.append(" mRecents=").append(this.mRecents ? "disabled" : "enabled");
            sb.append(" mSearch=").append(this.mSearch ? "disabled" : "enabled");
            sb.append(" mSystemIcons=").append(this.mSystemIcons ? "disabled" : "enabled");
            sb.append(" mClock=").append(this.mClock ? "disabled" : "enabled");
            sb.append(" mNotificationIcons=").append(this.mNotificationIcons ? "disabled" : "enabled");
            sb.append(" mRotationSuggestion=").append(this.mRotationSuggestion ? "disabled" : "enabled");
            return sb.toString();
        }

        public Pair<Integer, Integer> toFlags() {
            int disable1 = this.mStatusBarExpansion ? 0 | 65536 : 0;
            if (this.mNavigateHome) {
                disable1 |= 2097152;
            }
            if (this.mNotificationPeeking) {
                disable1 |= 262144;
            }
            if (this.mRecents) {
                disable1 |= 16777216;
            }
            if (this.mSearch) {
                disable1 |= 33554432;
            }
            if (this.mSystemIcons) {
                disable1 |= 1048576;
            }
            if (this.mClock) {
                disable1 |= 8388608;
            }
            if (this.mNotificationIcons) {
                disable1 |= 131072;
            }
            int disable2 = this.mRotationSuggestion ? 0 | 16 : 0;
            return new Pair<>(Integer.valueOf(disable1), Integer.valueOf(disable2));
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class RequestResultCallback extends IAddTileResultCallback.Stub {
        private final Consumer<Integer> mCallback;
        private final Executor mExecutor;

        RequestResultCallback(Executor executor, Consumer<Integer> callback) {
            this.mExecutor = executor;
            this.mCallback = callback;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onTileRequest$0(int userResponse) {
            this.mCallback.accept(Integer.valueOf(userResponse));
        }

        @Override // com.android.internal.statusbar.IAddTileResultCallback
        public void onTileRequest(final int userResponse) {
            this.mExecutor.execute(new Runnable() { // from class: android.app.StatusBarManager$RequestResultCallback$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    StatusBarManager.RequestResultCallback.this.lambda$onTileRequest$0(userResponse);
                }
            });
        }
    }

    /* loaded from: classes.dex */
    static final class UndoCallback extends IUndoMediaTransferCallback.Stub {
        private final Runnable mCallback;
        private final Executor mExecutor;

        UndoCallback(Executor executor, Runnable callback) {
            this.mExecutor = executor;
            this.mCallback = callback;
        }

        @Override // com.android.internal.statusbar.IUndoMediaTransferCallback
        public void onUndoTriggered() {
            long callingIdentity = Binder.clearCallingIdentity();
            try {
                this.mExecutor.execute(this.mCallback);
            } finally {
                restoreCallingIdentity(callingIdentity);
            }
        }
    }

    /* loaded from: classes.dex */
    static final class NearbyMediaDevicesProviderWrapper extends INearbyMediaDevicesProvider.Stub {
        private final NearbyMediaDevicesProvider mProvider;
        private final Map<INearbyMediaDevicesUpdateCallback, Consumer<List<NearbyDevice>>> mRegisteredCallbacks = new HashMap();

        NearbyMediaDevicesProviderWrapper(NearbyMediaDevicesProvider provider) {
            this.mProvider = provider;
        }

        @Override // android.media.INearbyMediaDevicesProvider
        public void registerNearbyDevicesCallback(final INearbyMediaDevicesUpdateCallback callback) {
            Consumer<List<NearbyDevice>> callbackAsConsumer = new Consumer() { // from class: android.app.StatusBarManager$NearbyMediaDevicesProviderWrapper$$ExternalSyntheticLambda0
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    StatusBarManager.NearbyMediaDevicesProviderWrapper.lambda$registerNearbyDevicesCallback$0(INearbyMediaDevicesUpdateCallback.this, (List) obj);
                }
            };
            this.mRegisteredCallbacks.put(callback, callbackAsConsumer);
            this.mProvider.registerNearbyDevicesCallback(callbackAsConsumer);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public static /* synthetic */ void lambda$registerNearbyDevicesCallback$0(INearbyMediaDevicesUpdateCallback callback, List nearbyDevices) {
            try {
                callback.onDevicesUpdated(nearbyDevices);
            } catch (RemoteException ex) {
                throw ex.rethrowFromSystemServer();
            }
        }

        @Override // android.media.INearbyMediaDevicesProvider
        public void unregisterNearbyDevicesCallback(INearbyMediaDevicesUpdateCallback callback) {
            if (!this.mRegisteredCallbacks.containsKey(callback)) {
                return;
            }
            this.mProvider.unregisterNearbyDevicesCallback(this.mRegisteredCallbacks.get(callback));
            this.mRegisteredCallbacks.remove(callback);
        }
    }
}
