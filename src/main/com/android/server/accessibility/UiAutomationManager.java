package com.android.server.accessibility;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.accessibilityservice.AccessibilityTrace;
import android.accessibilityservice.IAccessibilityServiceClient;
import android.content.ComponentName;
import android.content.Context;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteCallback;
import android.os.RemoteException;
import android.util.Slog;
import android.view.accessibility.AccessibilityEvent;
import com.android.internal.util.DumpUtils;
import com.android.server.accessibility.AbstractAccessibilityServiceConnection;
import com.android.server.accessibility.UiAutomationManager;
import com.android.server.p014wm.WindowManagerInternal;
import java.io.FileDescriptor;
import java.io.PrintWriter;
/* loaded from: classes.dex */
public class UiAutomationManager {
    public static final ComponentName COMPONENT_NAME = new ComponentName("com.android.server.accessibility", "UiAutomation");
    public final Object mLock;
    public AbstractAccessibilityServiceConnection.SystemSupport mSystemSupport;
    public AccessibilityTrace mTrace;
    public int mUiAutomationFlags;
    public UiAutomationService mUiAutomationService;
    public AccessibilityServiceInfo mUiAutomationServiceInfo;
    public IBinder mUiAutomationServiceOwner;
    public final IBinder.DeathRecipient mUiAutomationServiceOwnerDeathRecipient = new IBinder.DeathRecipient() { // from class: com.android.server.accessibility.UiAutomationManager.1
        @Override // android.os.IBinder.DeathRecipient
        public void binderDied() {
            UiAutomationManager.this.mUiAutomationServiceOwner.unlinkToDeath(this, 0);
            UiAutomationManager.this.mUiAutomationServiceOwner = null;
            UiAutomationManager.this.destroyUiAutomationService();
            Slog.v("UiAutomationManager", "UiAutomation service owner died");
        }
    };

    public UiAutomationManager(Object obj) {
        this.mLock = obj;
    }

    public void registerUiTestAutomationServiceLocked(IBinder iBinder, IAccessibilityServiceClient iAccessibilityServiceClient, Context context, AccessibilityServiceInfo accessibilityServiceInfo, int i, Handler handler, AccessibilitySecurityPolicy accessibilitySecurityPolicy, AbstractAccessibilityServiceConnection.SystemSupport systemSupport, AccessibilityTrace accessibilityTrace, WindowManagerInternal windowManagerInternal, SystemActionPerformer systemActionPerformer, AccessibilityWindowManager accessibilityWindowManager, int i2) {
        Object obj;
        Object obj2 = this.mLock;
        synchronized (obj2) {
            try {
                try {
                    accessibilityServiceInfo.setComponentName(COMPONENT_NAME);
                    if (this.mUiAutomationService != null) {
                        throw new IllegalStateException("UiAutomationService " + this.mUiAutomationService.mServiceInterface + "already registered!");
                    }
                    try {
                        iBinder.linkToDeath(this.mUiAutomationServiceOwnerDeathRecipient, 0);
                        this.mUiAutomationFlags = i2;
                        this.mSystemSupport = systemSupport;
                        this.mTrace = accessibilityTrace;
                        if (useAccessibility()) {
                            obj = obj2;
                            UiAutomationService uiAutomationService = new UiAutomationService(context, accessibilityServiceInfo, i, handler, this.mLock, accessibilitySecurityPolicy, systemSupport, accessibilityTrace, windowManagerInternal, systemActionPerformer, accessibilityWindowManager);
                            this.mUiAutomationService = uiAutomationService;
                            this.mUiAutomationServiceOwner = iBinder;
                            this.mUiAutomationServiceInfo = accessibilityServiceInfo;
                            uiAutomationService.mServiceInterface = iAccessibilityServiceClient;
                            try {
                                iAccessibilityServiceClient.asBinder().linkToDeath(this.mUiAutomationService, 0);
                                this.mUiAutomationService.onAdded();
                                this.mUiAutomationService.connectServiceUnknownThread();
                            } catch (RemoteException e) {
                                Slog.e("UiAutomationManager", "Failed registering death link: " + e);
                                destroyUiAutomationService();
                            }
                        }
                    } catch (RemoteException e2) {
                        Slog.e("UiAutomationManager", "Couldn't register for the death of a UiTestAutomationService!", e2);
                    }
                } catch (Throwable th) {
                    th = th;
                    obj = obj2;
                    throw th;
                }
            } catch (Throwable th2) {
                th = th2;
                throw th;
            }
        }
    }

    public void unregisterUiTestAutomationServiceLocked(IAccessibilityServiceClient iAccessibilityServiceClient) {
        UiAutomationService uiAutomationService;
        synchronized (this.mLock) {
            if (useAccessibility() && ((uiAutomationService = this.mUiAutomationService) == null || iAccessibilityServiceClient == null || uiAutomationService.mServiceInterface == null || iAccessibilityServiceClient.asBinder() != this.mUiAutomationService.mServiceInterface.asBinder())) {
                throw new IllegalStateException("UiAutomationService " + iAccessibilityServiceClient + " not registered!");
            }
            destroyUiAutomationService();
        }
    }

    public void sendAccessibilityEventLocked(AccessibilityEvent accessibilityEvent) {
        UiAutomationService uiAutomationService = this.mUiAutomationService;
        if (uiAutomationService != null) {
            uiAutomationService.notifyAccessibilityEvent(accessibilityEvent);
        }
    }

    public boolean isUiAutomationRunningLocked() {
        return (this.mUiAutomationService == null && useAccessibility()) ? false : true;
    }

    public boolean suppressingAccessibilityServicesLocked() {
        return !(this.mUiAutomationService == null && useAccessibility()) && (this.mUiAutomationFlags & 1) == 0;
    }

    public boolean useAccessibility() {
        return (this.mUiAutomationFlags & 2) == 0;
    }

    public boolean isTouchExplorationEnabledLocked() {
        UiAutomationService uiAutomationService = this.mUiAutomationService;
        return uiAutomationService != null && uiAutomationService.mRequestTouchExplorationMode;
    }

    public boolean canRetrieveInteractiveWindowsLocked() {
        UiAutomationService uiAutomationService = this.mUiAutomationService;
        return uiAutomationService != null && uiAutomationService.mRetrieveInteractiveWindows;
    }

    public int getRelevantEventTypes() {
        UiAutomationService uiAutomationService;
        synchronized (this.mLock) {
            uiAutomationService = this.mUiAutomationService;
        }
        if (uiAutomationService == null) {
            return 0;
        }
        return uiAutomationService.getRelevantEventTypes();
    }

    public AccessibilityServiceInfo getServiceInfo() {
        UiAutomationService uiAutomationService;
        synchronized (this.mLock) {
            uiAutomationService = this.mUiAutomationService;
        }
        if (uiAutomationService == null) {
            return null;
        }
        return uiAutomationService.getServiceInfo();
    }

    public void dumpUiAutomationService(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        UiAutomationService uiAutomationService;
        synchronized (this.mLock) {
            uiAutomationService = this.mUiAutomationService;
        }
        if (uiAutomationService != null) {
            uiAutomationService.dump(fileDescriptor, printWriter, strArr);
        }
    }

    public final void destroyUiAutomationService() {
        synchronized (this.mLock) {
            UiAutomationService uiAutomationService = this.mUiAutomationService;
            if (uiAutomationService != null) {
                uiAutomationService.mServiceInterface.asBinder().unlinkToDeath(this.mUiAutomationService, 0);
                this.mUiAutomationService.onRemoved();
                this.mUiAutomationService.resetLocked();
                this.mUiAutomationService = null;
                IBinder iBinder = this.mUiAutomationServiceOwner;
                if (iBinder != null) {
                    iBinder.unlinkToDeath(this.mUiAutomationServiceOwnerDeathRecipient, 0);
                    this.mUiAutomationServiceOwner = null;
                }
            }
            this.mUiAutomationFlags = 0;
            this.mSystemSupport.onClientChangeLocked(false);
        }
    }

    /* loaded from: classes.dex */
    public class UiAutomationService extends AbstractAccessibilityServiceConnection {
        public final Handler mMainHandler;

        public void disableSelf() {
        }

        public int getSoftKeyboardShowMode() {
            return 0;
        }

        @Override // com.android.server.accessibility.AbstractAccessibilityServiceConnection
        public boolean hasRightsToCurrentUserLocked() {
            return true;
        }

        public boolean isAccessibilityButtonAvailable() {
            return false;
        }

        @Override // com.android.server.accessibility.FingerprintGestureDispatcher.FingerprintGestureClient
        public boolean isCapturingFingerprintGestures() {
            return false;
        }

        @Override // com.android.server.accessibility.FingerprintGestureDispatcher.FingerprintGestureClient
        public void onFingerprintGesture(int i) {
        }

        @Override // com.android.server.accessibility.FingerprintGestureDispatcher.FingerprintGestureClient
        public void onFingerprintGestureDetectionActiveChanged(boolean z) {
        }

        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName componentName) {
        }

        public int setInputMethodEnabled(String str, boolean z) {
            return 2;
        }

        public boolean setSoftKeyboardShowMode(int i) {
            return false;
        }

        @Override // com.android.server.accessibility.AbstractAccessibilityServiceConnection
        public boolean supportsFlagForNotImportantViews(AccessibilityServiceInfo accessibilityServiceInfo) {
            return true;
        }

        public boolean switchToInputMethod(String str) {
            return false;
        }

        @Override // com.android.server.accessibility.AbstractAccessibilityServiceConnection
        public void takeScreenshot(int i, RemoteCallback remoteCallback) {
        }

        public UiAutomationService(Context context, AccessibilityServiceInfo accessibilityServiceInfo, int i, Handler handler, Object obj, AccessibilitySecurityPolicy accessibilitySecurityPolicy, AbstractAccessibilityServiceConnection.SystemSupport systemSupport, AccessibilityTrace accessibilityTrace, WindowManagerInternal windowManagerInternal, SystemActionPerformer systemActionPerformer, AccessibilityWindowManager accessibilityWindowManager) {
            super(context, UiAutomationManager.COMPONENT_NAME, accessibilityServiceInfo, i, handler, obj, accessibilitySecurityPolicy, systemSupport, accessibilityTrace, windowManagerInternal, systemActionPerformer, accessibilityWindowManager);
            this.mMainHandler = handler;
            setDisplayTypes(3);
        }

        public void connectServiceUnknownThread() {
            this.mMainHandler.post(new Runnable() { // from class: com.android.server.accessibility.UiAutomationManager$UiAutomationService$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    UiAutomationManager.UiAutomationService.this.lambda$connectServiceUnknownThread$0();
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$connectServiceUnknownThread$0() {
            IAccessibilityServiceClient iAccessibilityServiceClient;
            try {
                synchronized (this.mLock) {
                    iAccessibilityServiceClient = this.mServiceInterface;
                    if (iAccessibilityServiceClient == null) {
                        this.mService = null;
                    } else {
                        IBinder asBinder = iAccessibilityServiceClient.asBinder();
                        this.mService = asBinder;
                        asBinder.linkToDeath(this, 0);
                    }
                }
                if (iAccessibilityServiceClient != null) {
                    if (this.mTrace.isA11yTracingEnabledForTypes(2L)) {
                        AccessibilityTrace accessibilityTrace = this.mTrace;
                        accessibilityTrace.logTrace("UiAutomationService.connectServiceUnknownThread", 2L, "serviceConnection=" + this + ";connectionId=" + this.mId + "windowToken=" + this.mOverlayWindowTokens.get(0));
                    }
                    iAccessibilityServiceClient.init(this, this.mId, this.mOverlayWindowTokens.get(0));
                }
            } catch (RemoteException e) {
                Slog.w("UiAutomationManager", "Error initialized connection", e);
                UiAutomationManager.this.destroyUiAutomationService();
            }
        }

        @Override // android.os.IBinder.DeathRecipient
        public void binderDied() {
            UiAutomationManager.this.destroyUiAutomationService();
        }

        @Override // com.android.server.accessibility.AbstractAccessibilityServiceConnection
        public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
            if (DumpUtils.checkDumpPermission(this.mContext, "UiAutomationManager", printWriter)) {
                synchronized (this.mLock) {
                    printWriter.append((CharSequence) ("Ui Automation[eventTypes=" + AccessibilityEvent.eventTypeToString(this.mEventTypes)));
                    printWriter.append((CharSequence) (", notificationTimeout=" + this.mNotificationTimeout));
                    printWriter.append("]");
                }
            }
        }
    }
}
