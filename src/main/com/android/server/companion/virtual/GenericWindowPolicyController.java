package com.android.server.companion.virtual;

import android.app.WindowConfiguration;
import android.app.compat.CompatChanges;
import android.companion.virtual.VirtualDeviceManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Handler;
import android.os.Looper;
import android.os.UserHandle;
import android.p005os.IInstalld;
import android.util.ArraySet;
import android.util.Slog;
import android.window.DisplayWindowPolicyController;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.app.BlockedAppStreamingActivity;
import com.android.server.p011pm.PackageManagerShellCommandDataLoader;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
/* loaded from: classes.dex */
public class GenericWindowPolicyController extends DisplayWindowPolicyController {
    public static final ComponentName BLOCKED_APP_STREAMING_COMPONENT = new ComponentName(PackageManagerShellCommandDataLoader.PACKAGE, BlockedAppStreamingActivity.class.getName());
    public final ActivityBlockedCallback mActivityBlockedCallback;
    public final VirtualDeviceManager.ActivityListener mActivityListener;
    public final ArraySet<ComponentName> mAllowedActivities;
    public final ArraySet<ComponentName> mAllowedCrossTaskNavigations;
    public final ArraySet<UserHandle> mAllowedUsers;
    public final ArraySet<ComponentName> mBlockedActivities;
    public final ArraySet<ComponentName> mBlockedCrossTaskNavigations;
    public final int mDefaultActivityPolicy;
    public final Set<String> mDisplayCategories;
    public final IntentListenerCallback mIntentListenerCallback;
    public final PipBlockedCallback mPipBlockedCallback;
    public final SecureWindowCallback mSecureWindowCallback;
    public final boolean mShowTasksInHostDeviceRecents;
    public final Object mGenericWindowPolicyControllerLock = new Object();
    public int mDisplayId = -1;
    @GuardedBy({"mGenericWindowPolicyControllerLock"})
    public final ArraySet<Integer> mRunningUids = new ArraySet<>();
    public final Handler mHandler = new Handler(Looper.getMainLooper());
    @GuardedBy({"mGenericWindowPolicyControllerLock"})
    public final ArraySet<RunningAppsChangedListener> mRunningAppsChangedListeners = new ArraySet<>();

    /* loaded from: classes.dex */
    public interface ActivityBlockedCallback {
        void onActivityBlocked(int i, ActivityInfo activityInfo);
    }

    /* loaded from: classes.dex */
    public interface IntentListenerCallback {
        boolean shouldInterceptIntent(Intent intent);
    }

    /* loaded from: classes.dex */
    public interface PipBlockedCallback {
        void onEnteringPipBlocked(int i);
    }

    /* loaded from: classes.dex */
    public interface RunningAppsChangedListener {
        void onRunningAppsChanged(ArraySet<Integer> arraySet);
    }

    /* loaded from: classes.dex */
    public interface SecureWindowCallback {
        void onSecureWindowShown(int i, int i2);
    }

    public GenericWindowPolicyController(int i, int i2, ArraySet<UserHandle> arraySet, Set<ComponentName> set, Set<ComponentName> set2, Set<ComponentName> set3, Set<ComponentName> set4, int i3, VirtualDeviceManager.ActivityListener activityListener, PipBlockedCallback pipBlockedCallback, ActivityBlockedCallback activityBlockedCallback, SecureWindowCallback secureWindowCallback, IntentListenerCallback intentListenerCallback, Set<String> set5, boolean z) {
        this.mAllowedUsers = arraySet;
        this.mAllowedCrossTaskNavigations = new ArraySet<>(set);
        this.mBlockedCrossTaskNavigations = new ArraySet<>(set2);
        this.mAllowedActivities = new ArraySet<>(set3);
        this.mBlockedActivities = new ArraySet<>(set4);
        this.mDefaultActivityPolicy = i3;
        this.mActivityBlockedCallback = activityBlockedCallback;
        setInterestedWindowFlags(i, i2);
        this.mActivityListener = activityListener;
        this.mPipBlockedCallback = pipBlockedCallback;
        this.mSecureWindowCallback = secureWindowCallback;
        this.mIntentListenerCallback = intentListenerCallback;
        this.mDisplayCategories = set5;
        this.mShowTasksInHostDeviceRecents = z;
    }

    public void setDisplayId(int i) {
        this.mDisplayId = i;
    }

    public void registerRunningAppsChangedListener(RunningAppsChangedListener runningAppsChangedListener) {
        synchronized (this.mGenericWindowPolicyControllerLock) {
            this.mRunningAppsChangedListeners.add(runningAppsChangedListener);
        }
    }

    public void unregisterRunningAppsChangedListener(RunningAppsChangedListener runningAppsChangedListener) {
        synchronized (this.mGenericWindowPolicyControllerLock) {
            this.mRunningAppsChangedListeners.remove(runningAppsChangedListener);
        }
    }

    public boolean canContainActivities(List<ActivityInfo> list, @WindowConfiguration.WindowingMode int i) {
        if (isWindowingModeSupported(i)) {
            int size = list.size();
            for (int i2 = 0; i2 < size; i2++) {
                ActivityInfo activityInfo = list.get(i2);
                if (!canContainActivity(activityInfo, 0, 0)) {
                    this.mActivityBlockedCallback.onActivityBlocked(this.mDisplayId, activityInfo);
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public boolean canActivityBeLaunched(ActivityInfo activityInfo, Intent intent, @WindowConfiguration.WindowingMode int i, int i2, boolean z) {
        if (isWindowingModeSupported(i)) {
            ComponentName componentName = activityInfo.getComponentName();
            if (BLOCKED_APP_STREAMING_COMPONENT.equals(componentName)) {
                return true;
            }
            if (!canContainActivity(activityInfo, 0, 0)) {
                this.mActivityBlockedCallback.onActivityBlocked(this.mDisplayId, activityInfo);
                return false;
            } else if (i2 == 0) {
                return true;
            } else {
                if (z && !this.mBlockedCrossTaskNavigations.isEmpty() && this.mBlockedCrossTaskNavigations.contains(componentName)) {
                    Slog.d("GenericWindowPolicyController", "Virtual device blocking cross task navigation of " + componentName);
                    this.mActivityBlockedCallback.onActivityBlocked(this.mDisplayId, activityInfo);
                    return false;
                } else if (z && !this.mAllowedCrossTaskNavigations.isEmpty() && !this.mAllowedCrossTaskNavigations.contains(componentName)) {
                    Slog.d("GenericWindowPolicyController", "Virtual device not allowing cross task navigation of " + componentName);
                    this.mActivityBlockedCallback.onActivityBlocked(this.mDisplayId, activityInfo);
                    return false;
                } else {
                    IntentListenerCallback intentListenerCallback = this.mIntentListenerCallback;
                    if (intentListenerCallback == null || intent == null || !intentListenerCallback.shouldInterceptIntent(intent)) {
                        return true;
                    }
                    Slog.d("GenericWindowPolicyController", "Virtual device has intercepted intent");
                    return false;
                }
            }
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$keepActivityOnWindowFlagsChanged$0(ActivityInfo activityInfo) {
        this.mSecureWindowCallback.onSecureWindowShown(this.mDisplayId, activityInfo.applicationInfo.uid);
    }

    public boolean keepActivityOnWindowFlagsChanged(final ActivityInfo activityInfo, int i, int i2) {
        if ((i & IInstalld.FLAG_FORCE) != 0) {
            this.mHandler.post(new Runnable() { // from class: com.android.server.companion.virtual.GenericWindowPolicyController$$ExternalSyntheticLambda3
                @Override // java.lang.Runnable
                public final void run() {
                    GenericWindowPolicyController.this.lambda$keepActivityOnWindowFlagsChanged$0(activityInfo);
                }
            });
        }
        if (canContainActivity(activityInfo, i, i2)) {
            return true;
        }
        this.mActivityBlockedCallback.onActivityBlocked(this.mDisplayId, activityInfo);
        return false;
    }

    public void onTopActivityChanged(final ComponentName componentName, int i, final int i2) {
        if (this.mActivityListener == null || componentName == null) {
            return;
        }
        this.mHandler.post(new Runnable() { // from class: com.android.server.companion.virtual.GenericWindowPolicyController$$ExternalSyntheticLambda4
            @Override // java.lang.Runnable
            public final void run() {
                GenericWindowPolicyController.this.lambda$onTopActivityChanged$1(componentName, i2);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onTopActivityChanged$1(ComponentName componentName, int i) {
        this.mActivityListener.onTopActivityChanged(this.mDisplayId, componentName, i);
    }

    public void onRunningAppsChanged(final ArraySet<Integer> arraySet) {
        synchronized (this.mGenericWindowPolicyControllerLock) {
            this.mRunningUids.clear();
            this.mRunningUids.addAll((ArraySet<? extends Integer>) arraySet);
            if (this.mActivityListener != null && this.mRunningUids.isEmpty()) {
                this.mHandler.post(new Runnable() { // from class: com.android.server.companion.virtual.GenericWindowPolicyController$$ExternalSyntheticLambda1
                    @Override // java.lang.Runnable
                    public final void run() {
                        GenericWindowPolicyController.this.lambda$onRunningAppsChanged$2();
                    }
                });
            }
            if (!this.mRunningAppsChangedListeners.isEmpty()) {
                final ArraySet arraySet2 = new ArraySet((ArraySet) this.mRunningAppsChangedListeners);
                this.mHandler.post(new Runnable() { // from class: com.android.server.companion.virtual.GenericWindowPolicyController$$ExternalSyntheticLambda2
                    @Override // java.lang.Runnable
                    public final void run() {
                        GenericWindowPolicyController.lambda$onRunningAppsChanged$3(arraySet2, arraySet);
                    }
                });
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onRunningAppsChanged$2() {
        this.mActivityListener.onDisplayEmpty(this.mDisplayId);
    }

    public static /* synthetic */ void lambda$onRunningAppsChanged$3(ArraySet arraySet, ArraySet arraySet2) {
        Iterator it = arraySet.iterator();
        while (it.hasNext()) {
            ((RunningAppsChangedListener) it.next()).onRunningAppsChanged(arraySet2);
        }
    }

    public boolean canShowTasksInHostDeviceRecents() {
        return this.mShowTasksInHostDeviceRecents;
    }

    public boolean isEnteringPipAllowed(final int i) {
        if (super.isEnteringPipAllowed(i)) {
            return true;
        }
        this.mHandler.post(new Runnable() { // from class: com.android.server.companion.virtual.GenericWindowPolicyController$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                GenericWindowPolicyController.this.lambda$isEnteringPipAllowed$4(i);
            }
        });
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$isEnteringPipAllowed$4(int i) {
        this.mPipBlockedCallback.onEnteringPipBlocked(i);
    }

    public boolean containsUid(int i) {
        boolean contains;
        synchronized (this.mGenericWindowPolicyControllerLock) {
            contains = this.mRunningUids.contains(Integer.valueOf(i));
        }
        return contains;
    }

    public final boolean activityMatchesDisplayCategory(ActivityInfo activityInfo) {
        if (this.mDisplayCategories.isEmpty()) {
            return activityInfo.requiredDisplayCategory == null;
        }
        String str = activityInfo.requiredDisplayCategory;
        return str != null && this.mDisplayCategories.contains(str);
    }

    public final boolean canContainActivity(ActivityInfo activityInfo, int i, int i2) {
        if ((activityInfo.flags & 65536) == 0) {
            return false;
        }
        ComponentName componentName = activityInfo.getComponentName();
        if (BLOCKED_APP_STREAMING_COMPONENT.equals(componentName)) {
            return true;
        }
        if (!activityMatchesDisplayCategory(activityInfo)) {
            Slog.d("GenericWindowPolicyController", String.format("The activity's required display category: %s is not found on virtual display with the following categories: %s", activityInfo.requiredDisplayCategory, this.mDisplayCategories.toString()));
            return false;
        }
        UserHandle userHandleForUid = UserHandle.getUserHandleForUid(activityInfo.applicationInfo.uid);
        if (!this.mAllowedUsers.contains(userHandleForUid)) {
            Slog.d("GenericWindowPolicyController", "Virtual device activity not allowed from user " + userHandleForUid);
            return false;
        } else if (this.mDefaultActivityPolicy == 0 && this.mBlockedActivities.contains(componentName)) {
            Slog.d("GenericWindowPolicyController", "Virtual device blocking launch of " + componentName);
            return false;
        } else if (this.mDefaultActivityPolicy != 1 || this.mAllowedActivities.contains(componentName)) {
            return CompatChanges.isChangeEnabled(201712607L, activityInfo.packageName, userHandleForUid) || ((i & IInstalld.FLAG_FORCE) == 0 && (524288 & i2) == 0);
        } else {
            Slog.d("GenericWindowPolicyController", componentName + " is not in the allowed list.");
            return false;
        }
    }

    @VisibleForTesting
    public int getRunningAppsChangedListenersSizeForTesting() {
        int size;
        synchronized (this.mGenericWindowPolicyControllerLock) {
            size = this.mRunningAppsChangedListeners.size();
        }
        return size;
    }
}
