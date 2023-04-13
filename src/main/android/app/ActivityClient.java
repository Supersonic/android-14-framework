package android.app;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.p008os.Bundle;
import android.p008os.IBinder;
import android.p008os.IRemoteCallback;
import android.p008os.PersistableBundle;
import android.p008os.RemoteException;
import android.util.Singleton;
import android.view.RemoteAnimationDefinition;
import android.window.SizeConfigurationBuckets;
import com.android.internal.C4057R;
import com.android.internal.policy.IKeyguardDismissCallback;
/* loaded from: classes.dex */
public class ActivityClient {
    private static final Singleton<ActivityClient> sInstance = new Singleton<ActivityClient>() { // from class: android.app.ActivityClient.1
        /* JADX INFO: Access modifiers changed from: protected */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.util.Singleton
        public ActivityClient create() {
            return new ActivityClient();
        }
    };
    private static final ActivityClientControllerSingleton INTERFACE_SINGLETON = new ActivityClientControllerSingleton();

    private ActivityClient() {
    }

    public void activityIdle(IBinder token, Configuration config, boolean stopProfiling) {
        try {
            getActivityClientController().activityIdle(token, config, stopProfiling);
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    public void activityResumed(IBinder token, boolean handleSplashScreenExit) {
        try {
            getActivityClientController().activityResumed(token, handleSplashScreenExit);
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    public void activityRefreshed(IBinder token) {
        try {
            getActivityClientController().activityRefreshed(token);
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    public void activityTopResumedStateLost() {
        try {
            getActivityClientController().activityTopResumedStateLost();
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    public void activityPaused(IBinder token) {
        try {
            getActivityClientController().activityPaused(token);
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    public void activityStopped(IBinder token, Bundle state, PersistableBundle persistentState, CharSequence description) {
        try {
            getActivityClientController().activityStopped(token, state, persistentState, description);
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    public void activityDestroyed(IBinder token) {
        try {
            getActivityClientController().activityDestroyed(token);
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    public void activityLocalRelaunch(IBinder token) {
        try {
            getActivityClientController().activityLocalRelaunch(token);
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    public void activityRelaunched(IBinder token) {
        try {
            getActivityClientController().activityRelaunched(token);
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void reportSizeConfigurations(IBinder token, SizeConfigurationBuckets sizeConfigurations) {
        try {
            getActivityClientController().reportSizeConfigurations(token, sizeConfigurations);
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    public boolean moveActivityTaskToBack(IBinder token, boolean nonRoot) {
        try {
            return getActivityClientController().moveActivityTaskToBack(token, nonRoot);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean shouldUpRecreateTask(IBinder token, String destAffinity) {
        try {
            return getActivityClientController().shouldUpRecreateTask(token, destAffinity);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean navigateUpTo(IBinder token, Intent destIntent, String resolvedType, int resultCode, Intent resultData) {
        try {
            return getActivityClientController().navigateUpTo(token, destIntent, resolvedType, resultCode, resultData);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean releaseActivityInstance(IBinder token) {
        try {
            return getActivityClientController().releaseActivityInstance(token);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public boolean finishActivity(IBinder token, int resultCode, Intent resultData, int finishTask) {
        try {
            return getActivityClientController().finishActivity(token, resultCode, resultData, finishTask);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean finishActivityAffinity(IBinder token) {
        try {
            return getActivityClientController().finishActivityAffinity(token);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void finishSubActivity(IBinder token, String resultWho, int requestCode) {
        try {
            getActivityClientController().finishSubActivity(token, resultWho, requestCode);
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setForceSendResultForMediaProjection(IBinder token) {
        try {
            getActivityClientController().setForceSendResultForMediaProjection(token);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public boolean isTopOfTask(IBinder token) {
        try {
            return getActivityClientController().isTopOfTask(token);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean willActivityBeVisible(IBinder token) {
        try {
            return getActivityClientController().willActivityBeVisible(token);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public int getDisplayId(IBinder token) {
        try {
            return getActivityClientController().getDisplayId(token);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public int getTaskForActivity(IBinder token, boolean onlyRoot) {
        try {
            return getActivityClientController().getTaskForActivity(token, onlyRoot);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public Configuration getTaskConfiguration(IBinder activityToken) {
        try {
            return getActivityClientController().getTaskConfiguration(activityToken);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public IBinder getActivityTokenBelow(IBinder activityToken) {
        try {
            return getActivityClientController().getActivityTokenBelow(activityToken);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ComponentName getCallingActivity(IBinder token) {
        try {
            return getActivityClientController().getCallingActivity(token);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public String getCallingPackage(IBinder token) {
        try {
            return getActivityClientController().getCallingPackage(token);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public int getLaunchedFromUid(IBinder token) {
        try {
            return getActivityClientController().getLaunchedFromUid(token);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public String getLaunchedFromPackage(IBinder token) {
        try {
            return getActivityClientController().getLaunchedFromPackage(token);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void setRequestedOrientation(IBinder token, int requestedOrientation) {
        try {
            getActivityClientController().setRequestedOrientation(token, requestedOrientation);
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getRequestedOrientation(IBinder token) {
        try {
            return getActivityClientController().getRequestedOrientation(token);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean convertFromTranslucent(IBinder token) {
        try {
            return getActivityClientController().convertFromTranslucent(token);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean convertToTranslucent(IBinder token, Bundle options) {
        try {
            return getActivityClientController().convertToTranslucent(token, options);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void reportActivityFullyDrawn(IBinder token, boolean restoredFromBundle) {
        try {
            getActivityClientController().reportActivityFullyDrawn(token, restoredFromBundle);
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isImmersive(IBinder token) {
        try {
            return getActivityClientController().isImmersive(token);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setImmersive(IBinder token, boolean immersive) {
        try {
            getActivityClientController().setImmersive(token, immersive);
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean enterPictureInPictureMode(IBinder token, PictureInPictureParams params) {
        try {
            return getActivityClientController().enterPictureInPictureMode(token, params);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setPictureInPictureParams(IBinder token, PictureInPictureParams params) {
        try {
            getActivityClientController().setPictureInPictureParams(token, params);
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setShouldDockBigOverlays(IBinder token, boolean shouldDockBigOverlays) {
        try {
            getActivityClientController().setShouldDockBigOverlays(token, shouldDockBigOverlays);
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void toggleFreeformWindowingMode(IBinder token) {
        try {
            getActivityClientController().toggleFreeformWindowingMode(token);
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void requestMultiwindowFullscreen(IBinder token, int request, IRemoteCallback callback) {
        try {
            getActivityClientController().requestMultiwindowFullscreen(token, request, callback);
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void startLockTaskModeByToken(IBinder token) {
        try {
            getActivityClientController().startLockTaskModeByToken(token);
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void stopLockTaskModeByToken(IBinder token) {
        try {
            getActivityClientController().stopLockTaskModeByToken(token);
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void showLockTaskEscapeMessage(IBinder token) {
        try {
            getActivityClientController().showLockTaskEscapeMessage(token);
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setTaskDescription(IBinder token, ActivityManager.TaskDescription td) {
        try {
            getActivityClientController().setTaskDescription(token, td);
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean showAssistFromActivity(IBinder token, Bundle args) {
        try {
            return getActivityClientController().showAssistFromActivity(token, args);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isRootVoiceInteraction(IBinder token) {
        try {
            return getActivityClientController().isRootVoiceInteraction(token);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void startLocalVoiceInteraction(IBinder callingActivity, Bundle options) {
        try {
            getActivityClientController().startLocalVoiceInteraction(callingActivity, options);
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void stopLocalVoiceInteraction(IBinder callingActivity) {
        try {
            getActivityClientController().stopLocalVoiceInteraction(callingActivity);
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setShowWhenLocked(IBinder token, boolean showWhenLocked) {
        try {
            getActivityClientController().setShowWhenLocked(token, showWhenLocked);
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setInheritShowWhenLocked(IBinder token, boolean inheritShowWhenLocked) {
        try {
            getActivityClientController().setInheritShowWhenLocked(token, inheritShowWhenLocked);
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setTurnScreenOn(IBinder token, boolean turnScreenOn) {
        try {
            getActivityClientController().setTurnScreenOn(token, turnScreenOn);
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setAllowCrossUidActivitySwitchFromBelow(IBinder token, boolean allowed) {
        try {
            getActivityClientController().setAllowCrossUidActivitySwitchFromBelow(token, allowed);
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int setVrMode(IBinder token, boolean enabled, ComponentName packageName) {
        try {
            return getActivityClientController().setVrMode(token, enabled, packageName);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void overrideActivityTransition(IBinder token, boolean open, int enterAnim, int exitAnim, int backgroundColor) {
        try {
            getActivityClientController().overrideActivityTransition(token, open, enterAnim, exitAnim, backgroundColor);
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void clearOverrideActivityTransition(IBinder token, boolean open) {
        try {
            getActivityClientController().clearOverrideActivityTransition(token, open);
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void overridePendingTransition(IBinder token, String packageName, int enterAnim, int exitAnim, int backgroundColor) {
        try {
            getActivityClientController().overridePendingTransition(token, packageName, enterAnim, exitAnim, backgroundColor);
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setRecentsScreenshotEnabled(IBinder token, boolean enabled) {
        try {
            getActivityClientController().setRecentsScreenshotEnabled(token, enabled);
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    public void invalidateHomeTaskSnapshot(IBinder homeToken) {
        try {
            getActivityClientController().invalidateHomeTaskSnapshot(homeToken);
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void dismissKeyguard(IBinder token, IKeyguardDismissCallback callback, CharSequence message) {
        try {
            getActivityClientController().dismissKeyguard(token, callback, message);
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void registerRemoteAnimations(IBinder token, RemoteAnimationDefinition definition) {
        try {
            getActivityClientController().registerRemoteAnimations(token, definition);
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void unregisterRemoteAnimations(IBinder token) {
        try {
            getActivityClientController().unregisterRemoteAnimations(token);
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onBackPressed(IBinder token, IRequestFinishCallback callback) {
        try {
            getActivityClientController().onBackPressed(token, callback);
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void reportSplashScreenAttached(IBinder token) {
        try {
            getActivityClientController().splashScreenAttached(token);
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void enableTaskLocaleOverride(IBinder token) {
        try {
            getActivityClientController().enableTaskLocaleOverride(token);
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    public boolean isRequestedToLaunchInTaskFragment(IBinder activityToken, IBinder taskFragmentToken) {
        try {
            return getActivityClientController().isRequestedToLaunchInTaskFragment(activityToken, taskFragmentToken);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void requestCompatCameraControl(Resources res, IBinder token, boolean showControl, boolean transformationApplied, ICompatCameraControlCallback callback) {
        if (!res.getBoolean(C4057R.bool.config_isCameraCompatControlForStretchedIssuesEnabled)) {
            return;
        }
        try {
            getActivityClientController().requestCompatCameraControl(token, showControl, transformationApplied, callback);
        } catch (RemoteException e) {
            e.rethrowFromSystemServer();
        }
    }

    public static ActivityClient getInstance() {
        return sInstance.get();
    }

    public static IActivityClientController setActivityClientController(IActivityClientController activityClientController) {
        INTERFACE_SINGLETON.mKnownInstance = activityClientController;
        return activityClientController;
    }

    private static IActivityClientController getActivityClientController() {
        ActivityClientControllerSingleton activityClientControllerSingleton = INTERFACE_SINGLETON;
        IActivityClientController controller = activityClientControllerSingleton.mKnownInstance;
        return controller != null ? controller : activityClientControllerSingleton.get();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class ActivityClientControllerSingleton extends Singleton<IActivityClientController> {
        IActivityClientController mKnownInstance;

        private ActivityClientControllerSingleton() {
        }

        /* JADX INFO: Access modifiers changed from: protected */
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.util.Singleton
        public IActivityClientController create() {
            try {
                return ActivityTaskManager.getService().getActivityClientController();
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
    }
}
