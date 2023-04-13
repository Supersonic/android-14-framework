package com.android.server.accessibility;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.accessibilityservice.AccessibilityTrace;
import android.accessibilityservice.IAccessibilityServiceClient;
import android.accessibilityservice.TouchInteractionController;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ParceledListSlice;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.UserHandle;
import android.util.Slog;
import android.view.MotionEvent;
import com.android.internal.util.function.TriConsumer;
import com.android.internal.util.function.pooled.PooledLambda;
import com.android.server.accessibility.AbstractAccessibilityServiceConnection;
import com.android.server.inputmethod.InputMethodManagerInternal;
import com.android.server.p014wm.ActivityTaskManagerInternal;
import com.android.server.p014wm.WindowManagerInternal;
import java.lang.ref.WeakReference;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
/* loaded from: classes.dex */
public class AccessibilityServiceConnection extends AbstractAccessibilityServiceConnection {
    public final ActivityTaskManagerInternal mActivityTaskManagerService;
    public final Intent mIntent;
    public final Handler mMainHandler;
    public final WeakReference<AccessibilityUserState> mUserStateWeakReference;

    public AccessibilityServiceConnection(AccessibilityUserState accessibilityUserState, Context context, ComponentName componentName, AccessibilityServiceInfo accessibilityServiceInfo, int i, Handler handler, Object obj, AccessibilitySecurityPolicy accessibilitySecurityPolicy, AbstractAccessibilityServiceConnection.SystemSupport systemSupport, AccessibilityTrace accessibilityTrace, WindowManagerInternal windowManagerInternal, SystemActionPerformer systemActionPerformer, AccessibilityWindowManager accessibilityWindowManager, ActivityTaskManagerInternal activityTaskManagerInternal) {
        super(context, componentName, accessibilityServiceInfo, i, handler, obj, accessibilitySecurityPolicy, systemSupport, accessibilityTrace, windowManagerInternal, systemActionPerformer, accessibilityWindowManager);
        this.mUserStateWeakReference = new WeakReference<>(accessibilityUserState);
        Intent component = new Intent().setComponent(this.mComponentName);
        this.mIntent = component;
        this.mMainHandler = handler;
        component.putExtra("android.intent.extra.client_label", 17039570);
        this.mActivityTaskManagerService = activityTaskManagerInternal;
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            component.putExtra("android.intent.extra.client_intent", this.mSystemSupport.getPendingIntentActivity(this.mContext, 0, new Intent("android.settings.ACCESSIBILITY_SETTINGS"), 67108864));
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public void bindLocked() {
        AccessibilityUserState accessibilityUserState = this.mUserStateWeakReference.get();
        if (accessibilityUserState == null) {
            return;
        }
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            int i = accessibilityUserState.getBindInstantServiceAllowedLocked() ? 38801409 : 34607105;
            if (this.mService == null && this.mContext.bindServiceAsUser(this.mIntent, this, i, new UserHandle(accessibilityUserState.mUserId))) {
                accessibilityUserState.getBindingServicesLocked().add(this.mComponentName);
            }
            Binder.restoreCallingIdentity(clearCallingIdentity);
            this.mActivityTaskManagerService.setAllowAppSwitches(this.mComponentName.flattenToString(), this.mAccessibilityServiceInfo.getResolveInfo().serviceInfo.applicationInfo.uid, accessibilityUserState.mUserId);
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(clearCallingIdentity);
            throw th;
        }
    }

    public void unbindLocked() {
        if (requestImeApis()) {
            this.mSystemSupport.unbindImeLocked(this);
        }
        this.mContext.unbindService(this);
        AccessibilityUserState accessibilityUserState = this.mUserStateWeakReference.get();
        if (accessibilityUserState == null) {
            return;
        }
        accessibilityUserState.removeServiceLocked(this);
        this.mSystemSupport.getMagnificationProcessor().resetAllIfNeeded(this.mId);
        this.mActivityTaskManagerService.setAllowAppSwitches(this.mComponentName.flattenToString(), -1, accessibilityUserState.mUserId);
        resetLocked();
    }

    public boolean canRetrieveInteractiveWindowsLocked() {
        return this.mSecurityPolicy.canRetrieveWindowContentLocked(this) && this.mRetrieveInteractiveWindows;
    }

    public void disableSelf() {
        if (svcConnTracingEnabled()) {
            logTraceSvcConn("disableSelf", "");
        }
        synchronized (this.mLock) {
            AccessibilityUserState accessibilityUserState = this.mUserStateWeakReference.get();
            if (accessibilityUserState == null) {
                return;
            }
            if (accessibilityUserState.getEnabledServicesLocked().remove(this.mComponentName)) {
                long clearCallingIdentity = Binder.clearCallingIdentity();
                this.mSystemSupport.persistComponentNamesToSettingLocked("enabled_accessibility_services", accessibilityUserState.getEnabledServicesLocked(), accessibilityUserState.mUserId);
                Binder.restoreCallingIdentity(clearCallingIdentity);
                this.mSystemSupport.onClientChangeLocked(false);
            }
        }
    }

    @Override // android.content.ServiceConnection
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        synchronized (this.mLock) {
            IBinder iBinder2 = this.mService;
            if (iBinder2 != iBinder) {
                if (iBinder2 != null) {
                    iBinder2.unlinkToDeath(this, 0);
                }
                this.mService = iBinder;
                try {
                    iBinder.linkToDeath(this, 0);
                } catch (RemoteException unused) {
                    Slog.e("AccessibilityServiceConnection", "Failed registering death link");
                    binderDied();
                    return;
                }
            }
            this.mServiceInterface = IAccessibilityServiceClient.Stub.asInterface(iBinder);
            AccessibilityUserState accessibilityUserState = this.mUserStateWeakReference.get();
            if (accessibilityUserState == null) {
                return;
            }
            accessibilityUserState.addServiceLocked(this);
            this.mSystemSupport.onClientChangeLocked(false);
            this.mMainHandler.sendMessage(PooledLambda.obtainMessage(new Consumer() { // from class: com.android.server.accessibility.AccessibilityServiceConnection$$ExternalSyntheticLambda1
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    ((AccessibilityServiceConnection) obj).initializeService();
                }
            }, this));
            if (requestImeApis()) {
                this.mSystemSupport.requestImeLocked(this);
            }
        }
    }

    @Override // com.android.server.accessibility.AbstractAccessibilityServiceConnection
    public AccessibilityServiceInfo getServiceInfo() {
        return this.mAccessibilityServiceInfo;
    }

    /* JADX WARN: Removed duplicated region for block: B:23:0x0053  */
    /* JADX WARN: Removed duplicated region for block: B:36:0x0057 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final void initializeService() {
        IAccessibilityServiceClient iAccessibilityServiceClient;
        synchronized (this.mLock) {
            AccessibilityUserState accessibilityUserState = this.mUserStateWeakReference.get();
            if (accessibilityUserState == null) {
                return;
            }
            Set<ComponentName> bindingServicesLocked = accessibilityUserState.getBindingServicesLocked();
            Set<ComponentName> crashedServicesLocked = accessibilityUserState.getCrashedServicesLocked();
            if (!bindingServicesLocked.contains(this.mComponentName) && !crashedServicesLocked.contains(this.mComponentName)) {
                iAccessibilityServiceClient = null;
                if (iAccessibilityServiceClient == null && !accessibilityUserState.getEnabledServicesLocked().contains(this.mComponentName)) {
                    this.mSystemSupport.onClientChangeLocked(false);
                    return;
                } else if (iAccessibilityServiceClient != null) {
                    binderDied();
                    return;
                } else {
                    try {
                        if (svcClientTracingEnabled()) {
                            logTraceSvcClient("init", this + "," + this.mId + "," + this.mOverlayWindowTokens.get(0));
                        }
                        iAccessibilityServiceClient.init(this, this.mId, this.mOverlayWindowTokens.get(0));
                        return;
                    } catch (RemoteException e) {
                        Slog.w("AccessibilityServiceConnection", "Error while setting connection for service: " + iAccessibilityServiceClient, e);
                        binderDied();
                        return;
                    }
                }
            }
            bindingServicesLocked.remove(this.mComponentName);
            crashedServicesLocked.remove(this.mComponentName);
            this.mAccessibilityServiceInfo.crashed = false;
            iAccessibilityServiceClient = this.mServiceInterface;
            if (iAccessibilityServiceClient == null) {
            }
            if (iAccessibilityServiceClient != null) {
            }
        }
    }

    @Override // android.content.ServiceConnection
    public void onServiceDisconnected(ComponentName componentName) {
        binderDied();
        AccessibilityUserState accessibilityUserState = this.mUserStateWeakReference.get();
        if (accessibilityUserState != null) {
            this.mActivityTaskManagerService.setAllowAppSwitches(this.mComponentName.flattenToString(), -1, accessibilityUserState.mUserId);
        }
    }

    @Override // com.android.server.accessibility.AbstractAccessibilityServiceConnection
    public boolean hasRightsToCurrentUserLocked() {
        int callingUid = Binder.getCallingUid();
        return callingUid == 0 || callingUid == 1000 || callingUid == 2000 || this.mSecurityPolicy.resolveProfileParentLocked(UserHandle.getUserId(callingUid)) == this.mSystemSupport.getCurrentUserIdLocked() || this.mSecurityPolicy.hasPermission("android.permission.INTERACT_ACROSS_USERS") || this.mSecurityPolicy.hasPermission("android.permission.INTERACT_ACROSS_USERS_FULL");
    }

    public boolean setSoftKeyboardShowMode(int i) {
        if (svcConnTracingEnabled()) {
            logTraceSvcConn("setSoftKeyboardShowMode", "showMode=" + i);
        }
        synchronized (this.mLock) {
            if (hasRightsToCurrentUserLocked()) {
                AccessibilityUserState accessibilityUserState = this.mUserStateWeakReference.get();
                if (accessibilityUserState == null) {
                    return false;
                }
                return accessibilityUserState.setSoftKeyboardModeLocked(i, this.mComponentName);
            }
            return false;
        }
    }

    public int getSoftKeyboardShowMode() {
        if (svcConnTracingEnabled()) {
            logTraceSvcConn("getSoftKeyboardShowMode", "");
        }
        AccessibilityUserState accessibilityUserState = this.mUserStateWeakReference.get();
        if (accessibilityUserState != null) {
            return accessibilityUserState.getSoftKeyboardShowModeLocked();
        }
        return 0;
    }

    public boolean switchToInputMethod(String str) {
        if (svcConnTracingEnabled()) {
            logTraceSvcConn("switchToInputMethod", "imeId=" + str);
        }
        synchronized (this.mLock) {
            if (hasRightsToCurrentUserLocked()) {
                int callingUserId = UserHandle.getCallingUserId();
                long clearCallingIdentity = Binder.clearCallingIdentity();
                try {
                    return InputMethodManagerInternal.get().switchToInputMethod(str, callingUserId);
                } finally {
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                }
            }
            return false;
        }
    }

    public int setInputMethodEnabled(String str, boolean z) throws SecurityException {
        int canEnableDisableInputMethod;
        if (svcConnTracingEnabled()) {
            logTraceSvcConn("switchToInputMethod", "imeId=" + str);
        }
        synchronized (this.mLock) {
            if (hasRightsToCurrentUserLocked()) {
                int callingUserId = UserHandle.getCallingUserId();
                InputMethodManagerInternal inputMethodManagerInternal = InputMethodManagerInternal.get();
                long clearCallingIdentity = Binder.clearCallingIdentity();
                try {
                    synchronized (this.mLock) {
                        canEnableDisableInputMethod = this.mSecurityPolicy.canEnableDisableInputMethod(str, this);
                    }
                    if (canEnableDisableInputMethod != 0) {
                        return canEnableDisableInputMethod;
                    }
                    if (inputMethodManagerInternal.setInputMethodEnabled(str, z, callingUserId)) {
                        Binder.restoreCallingIdentity(clearCallingIdentity);
                        return 0;
                    }
                    return 2;
                } finally {
                    Binder.restoreCallingIdentity(clearCallingIdentity);
                }
            }
            return 2;
        }
    }

    public boolean isAccessibilityButtonAvailable() {
        if (svcConnTracingEnabled()) {
            logTraceSvcConn("isAccessibilityButtonAvailable", "");
        }
        synchronized (this.mLock) {
            boolean z = false;
            if (hasRightsToCurrentUserLocked()) {
                AccessibilityUserState accessibilityUserState = this.mUserStateWeakReference.get();
                if (accessibilityUserState != null && isAccessibilityButtonAvailableLocked(accessibilityUserState)) {
                    z = true;
                }
                return z;
            }
            return false;
        }
    }

    @Override // android.os.IBinder.DeathRecipient
    public void binderDied() {
        synchronized (this.mLock) {
            if (isConnectedLocked()) {
                if (requestImeApis()) {
                    this.mSystemSupport.unbindImeLocked(this);
                }
                this.mAccessibilityServiceInfo.crashed = true;
                AccessibilityUserState accessibilityUserState = this.mUserStateWeakReference.get();
                if (accessibilityUserState != null) {
                    accessibilityUserState.serviceDisconnectedLocked(this);
                }
                resetLocked();
                this.mSystemSupport.getMagnificationProcessor().resetAllIfNeeded(this.mId);
                this.mSystemSupport.onClientChangeLocked(false);
            }
        }
    }

    public boolean isAccessibilityButtonAvailableLocked(AccessibilityUserState accessibilityUserState) {
        return this.mRequestAccessibilityButton && this.mSystemSupport.isAccessibilityButtonShown();
    }

    @Override // com.android.server.accessibility.FingerprintGestureDispatcher.FingerprintGestureClient
    public boolean isCapturingFingerprintGestures() {
        return this.mServiceInterface != null && this.mSecurityPolicy.canCaptureFingerprintGestures(this) && this.mCaptureFingerprintGestures;
    }

    @Override // com.android.server.accessibility.FingerprintGestureDispatcher.FingerprintGestureClient
    public void onFingerprintGestureDetectionActiveChanged(boolean z) {
        IAccessibilityServiceClient iAccessibilityServiceClient;
        if (isCapturingFingerprintGestures()) {
            synchronized (this.mLock) {
                iAccessibilityServiceClient = this.mServiceInterface;
            }
            if (iAccessibilityServiceClient != null) {
                try {
                    if (svcClientTracingEnabled()) {
                        logTraceSvcClient("onFingerprintCapturingGesturesChanged", String.valueOf(z));
                    }
                    this.mServiceInterface.onFingerprintCapturingGesturesChanged(z);
                } catch (RemoteException unused) {
                }
            }
        }
    }

    @Override // com.android.server.accessibility.FingerprintGestureDispatcher.FingerprintGestureClient
    public void onFingerprintGesture(int i) {
        IAccessibilityServiceClient iAccessibilityServiceClient;
        if (isCapturingFingerprintGestures()) {
            synchronized (this.mLock) {
                iAccessibilityServiceClient = this.mServiceInterface;
            }
            if (iAccessibilityServiceClient != null) {
                try {
                    if (svcClientTracingEnabled()) {
                        logTraceSvcClient("onFingerprintGesture", String.valueOf(i));
                    }
                    this.mServiceInterface.onFingerprintGesture(i);
                } catch (RemoteException unused) {
                }
            }
        }
    }

    @Override // com.android.server.accessibility.AbstractAccessibilityServiceConnection
    public void dispatchGesture(int i, ParceledListSlice parceledListSlice, int i2) {
        synchronized (this.mLock) {
            if (this.mServiceInterface != null && this.mSecurityPolicy.canPerformGestures(this)) {
                MotionEventInjector motionEventInjectorForDisplayLocked = this.mSystemSupport.getMotionEventInjectorForDisplayLocked(i2);
                if (wmTracingEnabled()) {
                    logTraceWM("isTouchOrFaketouchDevice", "");
                }
                if (motionEventInjectorForDisplayLocked != null && this.mWindowManagerService.isTouchOrFaketouchDevice()) {
                    motionEventInjectorForDisplayLocked.injectEvents(parceledListSlice.getList(), this.mServiceInterface, i, i2);
                } else {
                    try {
                        if (svcClientTracingEnabled()) {
                            logTraceSvcClient("onPerformGestureResult", i + ", false");
                        }
                        this.mServiceInterface.onPerformGestureResult(i, false);
                    } catch (RemoteException e) {
                        Slog.e("AccessibilityServiceConnection", "Error sending motion event injection failure to " + this.mServiceInterface, e);
                    }
                }
            }
        }
    }

    @Override // com.android.server.accessibility.AbstractAccessibilityServiceConnection
    public void setFocusAppearance(int i, int i2) {
        AccessibilityUserState accessibilityUserState = this.mUserStateWeakReference.get();
        if (accessibilityUserState == null) {
            return;
        }
        synchronized (this.mLock) {
            if (hasRightsToCurrentUserLocked()) {
                if (this.mSecurityPolicy.checkAccessibilityAccess(this)) {
                    if (accessibilityUserState.getFocusStrokeWidthLocked() == i && accessibilityUserState.getFocusColorLocked() == i2) {
                        return;
                    }
                    accessibilityUserState.setFocusAppearanceLocked(i, i2);
                    this.mSystemSupport.onClientChangeLocked(false);
                }
            }
        }
    }

    public void notifyMotionEvent(MotionEvent motionEvent) {
        this.mMainHandler.sendMessage(PooledLambda.obtainMessage(new BiConsumer() { // from class: com.android.server.accessibility.AccessibilityServiceConnection$$ExternalSyntheticLambda0
            @Override // java.util.function.BiConsumer
            public final void accept(Object obj, Object obj2) {
                ((AccessibilityServiceConnection) obj).notifyMotionEventInternal((MotionEvent) obj2);
            }
        }, this, motionEvent));
    }

    public void notifyTouchState(int i, int i2) {
        this.mMainHandler.sendMessage(PooledLambda.obtainMessage(new TriConsumer() { // from class: com.android.server.accessibility.AccessibilityServiceConnection$$ExternalSyntheticLambda2
            public final void accept(Object obj, Object obj2, Object obj3) {
                ((AccessibilityServiceConnection) obj).notifyTouchStateInternal(((Integer) obj2).intValue(), ((Integer) obj3).intValue());
            }
        }, this, Integer.valueOf(i), Integer.valueOf(i2)));
    }

    public boolean requestImeApis() {
        return this.mRequestImeApis;
    }

    public final void notifyMotionEventInternal(MotionEvent motionEvent) {
        IAccessibilityServiceClient serviceInterfaceSafely = getServiceInterfaceSafely();
        if (serviceInterfaceSafely != null) {
            try {
                if (this.mTrace.isA11yTracingEnabled()) {
                    logTraceSvcClient(".onMotionEvent ", motionEvent.toString());
                }
                serviceInterfaceSafely.onMotionEvent(motionEvent);
            } catch (RemoteException e) {
                Slog.e("AccessibilityServiceConnection", "Error sending motion event to" + this.mService, e);
            }
        }
    }

    public final void notifyTouchStateInternal(int i, int i2) {
        IAccessibilityServiceClient serviceInterfaceSafely = getServiceInterfaceSafely();
        if (serviceInterfaceSafely != null) {
            try {
                if (this.mTrace.isA11yTracingEnabled()) {
                    logTraceSvcClient(".onTouchStateChanged ", TouchInteractionController.stateToString(i2));
                }
                serviceInterfaceSafely.onTouchStateChanged(i, i2);
            } catch (RemoteException e) {
                Slog.e("AccessibilityServiceConnection", "Error sending motion event to" + this.mService, e);
            }
        }
    }
}
