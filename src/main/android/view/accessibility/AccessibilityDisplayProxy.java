package android.view.accessibility;

import android.accessibilityservice.AccessibilityGestureEvent;
import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.accessibilityservice.IAccessibilityServiceClient;
import android.accessibilityservice.IAccessibilityServiceConnection;
import android.accessibilityservice.MagnificationConfig;
import android.annotation.SystemApi;
import android.content.Context;
import android.graphics.Region;
import android.p008os.IBinder;
import android.p008os.RemoteException;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.inputmethod.EditorInfo;
import com.android.internal.inputmethod.IAccessibilityInputMethodSessionCallback;
import com.android.internal.inputmethod.RemoteAccessibilityInputConnection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;
@SystemApi
/* loaded from: classes4.dex */
public abstract class AccessibilityDisplayProxy {
    private static final int INVALID_CONNECTION_ID = -1;
    private static final String LOG_TAG = "AccessibilityDisplayProxy";
    private int mConnectionId = -1;
    private int mDisplayId;
    private Executor mExecutor;
    private List<AccessibilityServiceInfo> mInstalledAndEnabledServices;
    IAccessibilityServiceClient mServiceClient;

    public AccessibilityDisplayProxy(int displayId, Executor executor, List<AccessibilityServiceInfo> installedAndEnabledServices) {
        this.mDisplayId = displayId;
        this.mExecutor = executor;
        this.mServiceClient = new IAccessibilityServiceClientImpl(null, this.mExecutor);
        this.mInstalledAndEnabledServices = installedAndEnabledServices;
    }

    public int getDisplayId() {
        return this.mDisplayId;
    }

    public void onAccessibilityEvent(AccessibilityEvent event) {
    }

    public void onProxyConnected() {
    }

    public void interrupt() {
    }

    public AccessibilityNodeInfo findFocus(int focusType) {
        return AccessibilityInteractionClient.getInstance().findFocus(this.mConnectionId, -2, AccessibilityNodeInfo.ROOT_NODE_ID, focusType);
    }

    public List<AccessibilityWindowInfo> getWindows() {
        return AccessibilityInteractionClient.getInstance().getWindowsOnDisplay(this.mConnectionId, this.mDisplayId);
    }

    public void setInstalledAndEnabledServices(List<AccessibilityServiceInfo> installedAndEnabledServices) {
        this.mInstalledAndEnabledServices = installedAndEnabledServices;
        sendServiceInfos();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendServiceInfos() {
        AccessibilityInteractionClient.getInstance();
        IAccessibilityServiceConnection connection = AccessibilityInteractionClient.getConnection(this.mConnectionId);
        List<AccessibilityServiceInfo> list = this.mInstalledAndEnabledServices;
        if (list != null && list.size() > 0 && connection != null) {
            try {
                connection.setInstalledAndEnabledServices(this.mInstalledAndEnabledServices);
                AccessibilityInteractionClient.getInstance().clearCache(this.mConnectionId);
            } catch (RemoteException re) {
                Log.m103w(LOG_TAG, "Error while setting AccessibilityServiceInfos", re);
                re.rethrowFromSystemServer();
            }
        }
        this.mInstalledAndEnabledServices = null;
    }

    public final List<AccessibilityServiceInfo> getInstalledAndEnabledServices() {
        AccessibilityInteractionClient.getInstance();
        IAccessibilityServiceConnection connection = AccessibilityInteractionClient.getConnection(this.mConnectionId);
        if (connection != null) {
            try {
                return connection.getInstalledAndEnabledServices();
            } catch (RemoteException re) {
                Log.m103w(LOG_TAG, "Error while getting AccessibilityServiceInfo", re);
                re.rethrowFromSystemServer();
            }
        }
        return Collections.emptyList();
    }

    public void setAccessibilityFocusAppearance(int strokeWidth, int color) {
        AccessibilityInteractionClient.getInstance();
        IAccessibilityServiceConnection connection = AccessibilityInteractionClient.getConnection(this.mConnectionId);
        if (connection != null) {
            try {
                connection.setFocusAppearance(strokeWidth, color);
            } catch (RemoteException re) {
                Log.m103w(LOG_TAG, "Error while setting the strokeWidth and color of the accessibility focus rectangle", re);
                re.rethrowFromSystemServer();
            }
        }
    }

    /* loaded from: classes4.dex */
    private class IAccessibilityServiceClientImpl extends AccessibilityService.IAccessibilityServiceClientWrapper {
        IAccessibilityServiceClientImpl(Context context, Executor executor) {
            super(context, executor, new AccessibilityService.Callbacks() { // from class: android.view.accessibility.AccessibilityDisplayProxy.IAccessibilityServiceClientImpl.1
                @Override // android.accessibilityservice.AccessibilityService.Callbacks
                public void onAccessibilityEvent(AccessibilityEvent event) {
                    AccessibilityDisplayProxy.this.onAccessibilityEvent(event);
                }

                @Override // android.accessibilityservice.AccessibilityService.Callbacks
                public void onInterrupt() {
                    AccessibilityDisplayProxy.this.interrupt();
                }

                @Override // android.accessibilityservice.AccessibilityService.Callbacks
                public void onServiceConnected() {
                    AccessibilityDisplayProxy.this.sendServiceInfos();
                    AccessibilityDisplayProxy.this.onProxyConnected();
                }

                @Override // android.accessibilityservice.AccessibilityService.Callbacks
                public void init(int connectionId, IBinder windowToken) {
                    AccessibilityDisplayProxy.this.mConnectionId = connectionId;
                }

                @Override // android.accessibilityservice.AccessibilityService.Callbacks
                public boolean onGesture(AccessibilityGestureEvent gestureInfo) {
                    return false;
                }

                @Override // android.accessibilityservice.AccessibilityService.Callbacks
                public boolean onKeyEvent(KeyEvent event) {
                    return false;
                }

                @Override // android.accessibilityservice.AccessibilityService.Callbacks
                public void onMagnificationChanged(int displayId, Region region, MagnificationConfig config) {
                }

                @Override // android.accessibilityservice.AccessibilityService.Callbacks
                public void onMotionEvent(MotionEvent event) {
                }

                @Override // android.accessibilityservice.AccessibilityService.Callbacks
                public void onTouchStateChanged(int displayId, int state) {
                }

                @Override // android.accessibilityservice.AccessibilityService.Callbacks
                public void onSoftKeyboardShowModeChanged(int showMode) {
                }

                @Override // android.accessibilityservice.AccessibilityService.Callbacks
                public void onPerformGestureResult(int sequence, boolean completedSuccessfully) {
                }

                @Override // android.accessibilityservice.AccessibilityService.Callbacks
                public void onFingerprintCapturingGesturesChanged(boolean active) {
                }

                @Override // android.accessibilityservice.AccessibilityService.Callbacks
                public void onFingerprintGesture(int gesture) {
                }

                @Override // android.accessibilityservice.AccessibilityService.Callbacks
                public void onAccessibilityButtonClicked(int displayId) {
                }

                @Override // android.accessibilityservice.AccessibilityService.Callbacks
                public void onAccessibilityButtonAvailabilityChanged(boolean available) {
                }

                @Override // android.accessibilityservice.AccessibilityService.Callbacks
                public void onSystemActionsChanged() {
                }

                @Override // android.accessibilityservice.AccessibilityService.Callbacks
                public void createImeSession(IAccessibilityInputMethodSessionCallback callback) {
                }

                @Override // android.accessibilityservice.AccessibilityService.Callbacks
                public void startInput(RemoteAccessibilityInputConnection inputConnection, EditorInfo editorInfo, boolean restarting) {
                }
            });
        }
    }
}
