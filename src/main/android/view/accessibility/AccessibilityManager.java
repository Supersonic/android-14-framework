package android.view.accessibility;

import android.Manifest;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.accessibilityservice.AccessibilityShortcutInfo;
import android.annotation.SystemApi;
import android.app.RemoteAction;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.p001pm.ActivityInfo;
import android.content.p001pm.PackageManager;
import android.content.p001pm.ResolveInfo;
import android.content.p001pm.ServiceInfo;
import android.content.res.Resources;
import android.p008os.Binder;
import android.p008os.Handler;
import android.p008os.HandlerExecutor;
import android.p008os.IBinder;
import android.p008os.Looper;
import android.p008os.Message;
import android.p008os.RemoteException;
import android.p008os.ServiceManager;
import android.p008os.SystemClock;
import android.util.ArrayMap;
import android.util.Log;
import android.util.SparseArray;
import android.view.IWindow;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.IAccessibilityManager;
import android.view.accessibility.IAccessibilityManagerClient;
import com.android.internal.C4057R;
import com.android.internal.util.IntPair;
import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes4.dex */
public final class AccessibilityManager {
    public static final int ACCESSIBILITY_BUTTON = 0;
    public static final int ACCESSIBILITY_SHORTCUT_KEY = 1;
    public static final String ACTION_CHOOSE_ACCESSIBILITY_BUTTON = "com.android.internal.intent.action.CHOOSE_ACCESSIBILITY_BUTTON";
    public static final int AUTOCLICK_DELAY_DEFAULT = 600;
    public static final int DALTONIZER_CORRECT_DEUTERANOMALY = 12;
    public static final int DALTONIZER_DISABLED = -1;
    public static final int DALTONIZER_SIMULATE_MONOCHROMACY = 0;
    private static final boolean DEBUG = false;
    public static final int FLAG_CONTENT_CONTROLS = 4;
    public static final int FLAG_CONTENT_ICONS = 1;
    public static final int FLAG_CONTENT_TEXT = 2;
    public static final int FLASH_REASON_ALARM = 2;
    public static final int FLASH_REASON_CALL = 1;
    public static final int FLASH_REASON_NOTIFICATION = 3;
    public static final int FLASH_REASON_PREVIEW = 4;
    private static final String LOG_TAG = "AccessibilityManager";
    public static final int STATE_FLAG_ACCESSIBILITY_ENABLED = 1;
    public static final int STATE_FLAG_AUDIO_DESCRIPTION_BY_DEFAULT_ENABLED = 4096;
    public static final int STATE_FLAG_DISPATCH_DOUBLE_TAP = 8;
    public static final int STATE_FLAG_HIGH_TEXT_CONTRAST_ENABLED = 4;
    public static final int STATE_FLAG_REQUEST_MULTI_FINGER_GESTURES = 16;
    public static final int STATE_FLAG_TOUCH_EXPLORATION_ENABLED = 2;
    public static final int STATE_FLAG_TRACE_A11Y_INTERACTION_CLIENT_ENABLED = 1024;
    public static final int STATE_FLAG_TRACE_A11Y_INTERACTION_CONNECTION_CB_ENABLED = 512;
    public static final int STATE_FLAG_TRACE_A11Y_INTERACTION_CONNECTION_ENABLED = 256;
    public static final int STATE_FLAG_TRACE_A11Y_SERVICE_ENABLED = 2048;
    private static AccessibilityManager sInstance;
    static final Object sInstanceSync = new Object();
    AccessibilityPolicy mAccessibilityPolicy;
    private final ArrayMap<AccessibilityStateChangeListener, Handler> mAccessibilityStateChangeListeners;
    int mAccessibilityTracingState;
    private final ArrayMap<AudioDescriptionRequestedChangeListener, Executor> mAudioDescriptionRequestedChangeListeners;
    private final Binder mBinder;
    final Handler.Callback mCallback;
    private final IAccessibilityManagerClient.Stub mClient;
    private int mFocusColor;
    private int mFocusStrokeWidth;
    final Handler mHandler;
    private final ArrayMap<HighTextContrastChangeListener, Handler> mHighTextContrastStateChangeListeners;
    int mInteractiveUiTimeout;
    boolean mIsAudioDescriptionByDefaultRequested;
    boolean mIsEnabled;
    boolean mIsHighTextContrastEnabled;
    boolean mIsTouchExplorationEnabled;
    private final Object mLock;
    int mNonInteractiveUiTimeout;
    private int mPerformingAction;
    int mRelevantEventTypes;
    private boolean mRequestFromAccessibilityTool;
    private SparseArray<List<AccessibilityRequestPreparer>> mRequestPreparerLists;
    private IAccessibilityManager mService;
    private final ArrayMap<AccessibilityServicesStateChangeListener, Executor> mServicesStateChangeListeners;
    private final ArrayMap<TouchExplorationStateChangeListener, Handler> mTouchExplorationStateChangeListeners;
    final int mUserId;

    /* loaded from: classes4.dex */
    public interface AccessibilityPolicy {
        List<AccessibilityServiceInfo> getEnabledAccessibilityServiceList(int i, List<AccessibilityServiceInfo> list);

        List<AccessibilityServiceInfo> getInstalledAccessibilityServiceList(List<AccessibilityServiceInfo> list);

        int getRelevantEventTypes(int i);

        boolean isEnabled(boolean z);

        AccessibilityEvent onAccessibilityEvent(AccessibilityEvent accessibilityEvent, boolean z, int i);
    }

    /* loaded from: classes4.dex */
    public interface AccessibilityServicesStateChangeListener {
        void onAccessibilityServicesStateChanged(AccessibilityManager accessibilityManager);
    }

    /* loaded from: classes4.dex */
    public interface AccessibilityStateChangeListener {
        void onAccessibilityStateChanged(boolean z);
    }

    /* loaded from: classes4.dex */
    public interface AudioDescriptionRequestedChangeListener {
        void onAudioDescriptionRequestedChanged(boolean z);
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes4.dex */
    public @interface ContentFlag {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes4.dex */
    public @interface FlashNotificationReason {
    }

    /* loaded from: classes4.dex */
    public interface HighTextContrastChangeListener {
        void onHighTextContrastStateChanged(boolean z);
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes4.dex */
    public @interface ShortcutType {
    }

    /* loaded from: classes4.dex */
    public interface TouchExplorationStateChangeListener {
        void onTouchExplorationStateChanged(boolean z);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: android.view.accessibility.AccessibilityManager$1 */
    /* loaded from: classes4.dex */
    public class BinderC35891 extends IAccessibilityManagerClient.Stub {
        BinderC35891() {
        }

        @Override // android.view.accessibility.IAccessibilityManagerClient
        public void setState(int state) {
            AccessibilityManager.this.mHandler.obtainMessage(1, state, 0).sendToTarget();
        }

        @Override // android.view.accessibility.IAccessibilityManagerClient
        public void notifyServicesStateChanged(long updatedUiTimeout) {
            AccessibilityManager.this.updateUiTimeout(updatedUiTimeout);
            synchronized (AccessibilityManager.this.mLock) {
                if (AccessibilityManager.this.mServicesStateChangeListeners.isEmpty()) {
                    return;
                }
                ArrayMap<AccessibilityServicesStateChangeListener, Executor> listeners = new ArrayMap<>(AccessibilityManager.this.mServicesStateChangeListeners);
                int numListeners = listeners.size();
                for (int i = 0; i < numListeners; i++) {
                    final AccessibilityServicesStateChangeListener listener = (AccessibilityServicesStateChangeListener) AccessibilityManager.this.mServicesStateChangeListeners.keyAt(i);
                    ((Executor) AccessibilityManager.this.mServicesStateChangeListeners.valueAt(i)).execute(new Runnable() { // from class: android.view.accessibility.AccessibilityManager$1$$ExternalSyntheticLambda0
                        @Override // java.lang.Runnable
                        public final void run() {
                            AccessibilityManager.BinderC35891.this.lambda$notifyServicesStateChanged$0(listener);
                        }
                    });
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$notifyServicesStateChanged$0(AccessibilityServicesStateChangeListener listener) {
            listener.onAccessibilityServicesStateChanged(AccessibilityManager.this);
        }

        @Override // android.view.accessibility.IAccessibilityManagerClient
        public void setRelevantEventTypes(int eventTypes) {
            AccessibilityManager.this.mRelevantEventTypes = eventTypes;
        }

        @Override // android.view.accessibility.IAccessibilityManagerClient
        public void setFocusAppearance(int strokeWidth, int color) {
            synchronized (AccessibilityManager.this.mLock) {
                AccessibilityManager.this.updateFocusAppearanceLocked(strokeWidth, color);
            }
        }
    }

    public static AccessibilityManager getInstance(Context context) {
        int userId;
        synchronized (sInstanceSync) {
            if (sInstance == null) {
                if (Binder.getCallingUid() != 1000 && context.checkCallingOrSelfPermission(Manifest.C0000permission.INTERACT_ACROSS_USERS) != 0 && context.checkCallingOrSelfPermission(Manifest.C0000permission.INTERACT_ACROSS_USERS_FULL) != 0) {
                    userId = context.getUserId();
                    sInstance = new AccessibilityManager(context, null, userId);
                }
                userId = -2;
                sInstance = new AccessibilityManager(context, null, userId);
            }
        }
        return sInstance;
    }

    public AccessibilityManager(Context context, IAccessibilityManager service, int userId) {
        Object obj = new Object();
        this.mLock = obj;
        this.mRelevantEventTypes = -1;
        this.mAccessibilityTracingState = 0;
        this.mPerformingAction = 0;
        this.mAccessibilityStateChangeListeners = new ArrayMap<>();
        this.mTouchExplorationStateChangeListeners = new ArrayMap<>();
        this.mHighTextContrastStateChangeListeners = new ArrayMap<>();
        this.mServicesStateChangeListeners = new ArrayMap<>();
        this.mAudioDescriptionRequestedChangeListeners = new ArrayMap<>();
        this.mBinder = new Binder();
        this.mClient = new BinderC35891();
        MyCallback myCallback = new MyCallback();
        this.mCallback = myCallback;
        this.mHandler = new Handler(context.getMainLooper(), myCallback);
        this.mUserId = userId;
        synchronized (obj) {
            initialFocusAppearanceLocked(context.getResources());
            tryConnectToServiceLocked(service);
        }
    }

    public AccessibilityManager(Context context, Handler handler, IAccessibilityManager service, int userId, boolean serviceConnect) {
        Object obj = new Object();
        this.mLock = obj;
        this.mRelevantEventTypes = -1;
        this.mAccessibilityTracingState = 0;
        this.mPerformingAction = 0;
        this.mAccessibilityStateChangeListeners = new ArrayMap<>();
        this.mTouchExplorationStateChangeListeners = new ArrayMap<>();
        this.mHighTextContrastStateChangeListeners = new ArrayMap<>();
        this.mServicesStateChangeListeners = new ArrayMap<>();
        this.mAudioDescriptionRequestedChangeListeners = new ArrayMap<>();
        this.mBinder = new Binder();
        this.mClient = new BinderC35891();
        this.mCallback = new MyCallback();
        this.mHandler = handler;
        this.mUserId = userId;
        synchronized (obj) {
            initialFocusAppearanceLocked(context.getResources());
            if (serviceConnect) {
                tryConnectToServiceLocked(service);
            }
        }
    }

    public IAccessibilityManagerClient getClient() {
        return this.mClient;
    }

    public boolean removeClient() {
        synchronized (this.mLock) {
            IAccessibilityManager service = getServiceLocked();
            if (service == null) {
                return false;
            }
            try {
                return service.removeClient(this.mClient, this.mUserId);
            } catch (RemoteException re) {
                Log.m109e(LOG_TAG, "AccessibilityManagerService is dead", re);
                return false;
            }
        }
    }

    public Handler.Callback getCallback() {
        return this.mCallback;
    }

    public boolean isEnabled() {
        boolean z;
        AccessibilityPolicy accessibilityPolicy;
        synchronized (this.mLock) {
            z = this.mIsEnabled || hasAnyDirectConnection() || ((accessibilityPolicy = this.mAccessibilityPolicy) != null && accessibilityPolicy.isEnabled(this.mIsEnabled));
        }
        return z;
    }

    public boolean hasAnyDirectConnection() {
        return AccessibilityInteractionClient.hasAnyDirectConnection();
    }

    public boolean isTouchExplorationEnabled() {
        synchronized (this.mLock) {
            IAccessibilityManager service = getServiceLocked();
            if (service == null) {
                return false;
            }
            return this.mIsTouchExplorationEnabled;
        }
    }

    public boolean isHighTextContrastEnabled() {
        synchronized (this.mLock) {
            IAccessibilityManager service = getServiceLocked();
            if (service == null) {
                return false;
            }
            return this.mIsHighTextContrastEnabled;
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:46:0x0099, code lost:
        if (r8 == r2) goto L40;
     */
    /* JADX WARN: Code restructure failed: missing block: B:48:0x009c, code lost:
        return;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public void sendAccessibilityEvent(AccessibilityEvent event) {
        AccessibilityEvent dispatchedEvent;
        synchronized (this.mLock) {
            IAccessibilityManager service = getServiceLocked();
            if (service == null) {
                return;
            }
            event.setEventTime(SystemClock.uptimeMillis());
            if (event.getAction() == 0) {
                event.setAction(this.mPerformingAction);
            }
            AccessibilityPolicy accessibilityPolicy = this.mAccessibilityPolicy;
            if (accessibilityPolicy != null) {
                dispatchedEvent = accessibilityPolicy.onAccessibilityEvent(event, this.mIsEnabled, this.mRelevantEventTypes);
                if (dispatchedEvent == null) {
                    return;
                }
            } else {
                dispatchedEvent = event;
            }
            if (!isEnabled()) {
                Looper myLooper = Looper.myLooper();
                if (myLooper == Looper.getMainLooper()) {
                    throw new IllegalStateException("Accessibility off. Did you forget to check that?");
                }
                Log.m110e(LOG_TAG, "AccessibilityEvent sent with accessibility disabled");
            } else if ((dispatchedEvent.getEventType() & this.mRelevantEventTypes) == 0) {
            } else {
                int userId = this.mUserId;
                try {
                    try {
                        long identityToken = Binder.clearCallingIdentity();
                        try {
                            service.sendAccessibilityEvent(dispatchedEvent, userId);
                        } finally {
                            Binder.restoreCallingIdentity(identityToken);
                        }
                    } catch (RemoteException re) {
                        Log.m109e(LOG_TAG, "Error during sending " + dispatchedEvent + " ", re);
                    }
                } finally {
                    if (event != dispatchedEvent) {
                        event.recycle();
                    }
                    dispatchedEvent.recycle();
                }
            }
        }
    }

    public void interrupt() {
        synchronized (this.mLock) {
            IAccessibilityManager service = getServiceLocked();
            if (service == null) {
                return;
            }
            if (!isEnabled()) {
                Looper myLooper = Looper.myLooper();
                if (myLooper == Looper.getMainLooper()) {
                    throw new IllegalStateException("Accessibility off. Did you forget to check that?");
                }
                Log.m110e(LOG_TAG, "Interrupt called with accessibility disabled");
                return;
            }
            int userId = this.mUserId;
            try {
                service.interrupt(userId);
            } catch (RemoteException re) {
                Log.m109e(LOG_TAG, "Error while requesting interrupt from all services. ", re);
            }
        }
    }

    @Deprecated
    public List<ServiceInfo> getAccessibilityServiceList() {
        List<AccessibilityServiceInfo> infos = getInstalledAccessibilityServiceList();
        List<ServiceInfo> services = new ArrayList<>();
        int infoCount = infos.size();
        for (int i = 0; i < infoCount; i++) {
            AccessibilityServiceInfo info = infos.get(i);
            services.add(info.getResolveInfo().serviceInfo);
        }
        return Collections.unmodifiableList(services);
    }

    public List<AccessibilityServiceInfo> getInstalledAccessibilityServiceList() {
        synchronized (this.mLock) {
            IAccessibilityManager service = getServiceLocked();
            if (service == null) {
                return Collections.emptyList();
            }
            int userId = this.mUserId;
            List<AccessibilityServiceInfo> services = null;
            try {
                services = service.getInstalledAccessibilityServiceList(userId);
            } catch (RemoteException re) {
                Log.m109e(LOG_TAG, "Error while obtaining the installed AccessibilityServices. ", re);
            }
            AccessibilityPolicy accessibilityPolicy = this.mAccessibilityPolicy;
            if (accessibilityPolicy != null) {
                services = accessibilityPolicy.getInstalledAccessibilityServiceList(services);
            }
            if (services != null) {
                return Collections.unmodifiableList(services);
            }
            return Collections.emptyList();
        }
    }

    public List<AccessibilityServiceInfo> getEnabledAccessibilityServiceList(int feedbackTypeFlags) {
        synchronized (this.mLock) {
            IAccessibilityManager service = getServiceLocked();
            if (service == null) {
                return Collections.emptyList();
            }
            int userId = this.mUserId;
            List<AccessibilityServiceInfo> services = null;
            try {
                services = service.getEnabledAccessibilityServiceList(feedbackTypeFlags, userId);
            } catch (RemoteException re) {
                Log.m109e(LOG_TAG, "Error while obtaining the enabled AccessibilityServices. ", re);
            }
            AccessibilityPolicy accessibilityPolicy = this.mAccessibilityPolicy;
            if (accessibilityPolicy != null) {
                services = accessibilityPolicy.getEnabledAccessibilityServiceList(feedbackTypeFlags, services);
            }
            if (services != null) {
                return Collections.unmodifiableList(services);
            }
            return Collections.emptyList();
        }
    }

    public boolean addAccessibilityStateChangeListener(AccessibilityStateChangeListener listener) {
        addAccessibilityStateChangeListener(listener, null);
        return true;
    }

    public void addAccessibilityStateChangeListener(AccessibilityStateChangeListener listener, Handler handler) {
        synchronized (this.mLock) {
            this.mAccessibilityStateChangeListeners.put(listener, handler == null ? this.mHandler : handler);
        }
    }

    public boolean removeAccessibilityStateChangeListener(AccessibilityStateChangeListener listener) {
        boolean z;
        synchronized (this.mLock) {
            int index = this.mAccessibilityStateChangeListeners.indexOfKey(listener);
            this.mAccessibilityStateChangeListeners.remove(listener);
            z = index >= 0;
        }
        return z;
    }

    public boolean addTouchExplorationStateChangeListener(TouchExplorationStateChangeListener listener) {
        addTouchExplorationStateChangeListener(listener, null);
        return true;
    }

    public void addTouchExplorationStateChangeListener(TouchExplorationStateChangeListener listener, Handler handler) {
        synchronized (this.mLock) {
            this.mTouchExplorationStateChangeListeners.put(listener, handler == null ? this.mHandler : handler);
        }
    }

    public boolean removeTouchExplorationStateChangeListener(TouchExplorationStateChangeListener listener) {
        boolean z;
        synchronized (this.mLock) {
            int index = this.mTouchExplorationStateChangeListeners.indexOfKey(listener);
            this.mTouchExplorationStateChangeListeners.remove(listener);
            z = index >= 0;
        }
        return z;
    }

    public void addAccessibilityServicesStateChangeListener(Executor executor, AccessibilityServicesStateChangeListener listener) {
        synchronized (this.mLock) {
            this.mServicesStateChangeListeners.put(listener, executor);
        }
    }

    public void addAccessibilityServicesStateChangeListener(AccessibilityServicesStateChangeListener listener) {
        addAccessibilityServicesStateChangeListener(new HandlerExecutor(this.mHandler), listener);
    }

    public boolean removeAccessibilityServicesStateChangeListener(AccessibilityServicesStateChangeListener listener) {
        boolean z;
        synchronized (this.mLock) {
            z = this.mServicesStateChangeListeners.remove(listener) != null;
        }
        return z;
    }

    public boolean isRequestFromAccessibilityTool() {
        return this.mRequestFromAccessibilityTool;
    }

    public void setRequestFromAccessibilityTool(boolean requestFromAccessibilityTool) {
        this.mRequestFromAccessibilityTool = requestFromAccessibilityTool;
    }

    public void addAccessibilityRequestPreparer(AccessibilityRequestPreparer preparer) {
        if (this.mRequestPreparerLists == null) {
            this.mRequestPreparerLists = new SparseArray<>(1);
        }
        int id = preparer.getAccessibilityViewId();
        List<AccessibilityRequestPreparer> requestPreparerList = this.mRequestPreparerLists.get(id);
        if (requestPreparerList == null) {
            requestPreparerList = new ArrayList(1);
            this.mRequestPreparerLists.put(id, requestPreparerList);
        }
        requestPreparerList.add(preparer);
    }

    public void removeAccessibilityRequestPreparer(AccessibilityRequestPreparer preparer) {
        int viewId;
        List<AccessibilityRequestPreparer> requestPreparerList;
        if (this.mRequestPreparerLists != null && (requestPreparerList = this.mRequestPreparerLists.get((viewId = preparer.getAccessibilityViewId()))) != null) {
            requestPreparerList.remove(preparer);
            if (requestPreparerList.isEmpty()) {
                this.mRequestPreparerLists.remove(viewId);
            }
        }
    }

    public int getRecommendedTimeoutMillis(int originalTimeout, int uiContentFlags) {
        boolean hasIconsOrText = false;
        boolean hasControls = (uiContentFlags & 4) != 0;
        if ((uiContentFlags & 1) != 0 || (uiContentFlags & 2) != 0) {
            hasIconsOrText = true;
        }
        int recommendedTimeout = originalTimeout;
        if (hasControls) {
            recommendedTimeout = Math.max(recommendedTimeout, this.mInteractiveUiTimeout);
        }
        if (hasIconsOrText) {
            return Math.max(recommendedTimeout, this.mNonInteractiveUiTimeout);
        }
        return recommendedTimeout;
    }

    public int getAccessibilityFocusStrokeWidth() {
        int i;
        synchronized (this.mLock) {
            i = this.mFocusStrokeWidth;
        }
        return i;
    }

    public int getAccessibilityFocusColor() {
        int i;
        synchronized (this.mLock) {
            i = this.mFocusColor;
        }
        return i;
    }

    public boolean isA11yInteractionConnectionTraceEnabled() {
        boolean z;
        synchronized (this.mLock) {
            z = (this.mAccessibilityTracingState & 256) != 0;
        }
        return z;
    }

    public boolean isA11yInteractionConnectionCBTraceEnabled() {
        boolean z;
        synchronized (this.mLock) {
            z = (this.mAccessibilityTracingState & 512) != 0;
        }
        return z;
    }

    public boolean isA11yInteractionClientTraceEnabled() {
        boolean z;
        synchronized (this.mLock) {
            z = (this.mAccessibilityTracingState & 1024) != 0;
        }
        return z;
    }

    public boolean isA11yServiceTraceEnabled() {
        boolean z;
        synchronized (this.mLock) {
            z = (this.mAccessibilityTracingState & 2048) != 0;
        }
        return z;
    }

    public List<AccessibilityRequestPreparer> getRequestPreparersForAccessibilityId(int id) {
        SparseArray<List<AccessibilityRequestPreparer>> sparseArray = this.mRequestPreparerLists;
        if (sparseArray == null) {
            return null;
        }
        return sparseArray.get(id);
    }

    public void notifyPerformingAction(int actionId) {
        this.mPerformingAction = actionId;
    }

    public int getPerformingAction() {
        return this.mPerformingAction;
    }

    public void addHighTextContrastStateChangeListener(HighTextContrastChangeListener listener, Handler handler) {
        synchronized (this.mLock) {
            this.mHighTextContrastStateChangeListeners.put(listener, handler == null ? this.mHandler : handler);
        }
    }

    public void removeHighTextContrastStateChangeListener(HighTextContrastChangeListener listener) {
        synchronized (this.mLock) {
            this.mHighTextContrastStateChangeListeners.remove(listener);
        }
    }

    public void addAudioDescriptionRequestedChangeListener(Executor executor, AudioDescriptionRequestedChangeListener listener) {
        synchronized (this.mLock) {
            this.mAudioDescriptionRequestedChangeListeners.put(listener, executor);
        }
    }

    public boolean removeAudioDescriptionRequestedChangeListener(AudioDescriptionRequestedChangeListener listener) {
        boolean z;
        synchronized (this.mLock) {
            z = this.mAudioDescriptionRequestedChangeListeners.remove(listener) != null;
        }
        return z;
    }

    public void setAccessibilityPolicy(AccessibilityPolicy policy) {
        synchronized (this.mLock) {
            this.mAccessibilityPolicy = policy;
        }
    }

    public boolean isAccessibilityVolumeStreamActive() {
        List<AccessibilityServiceInfo> serviceInfos = getEnabledAccessibilityServiceList(-1);
        for (int i = 0; i < serviceInfos.size(); i++) {
            if ((serviceInfos.get(i).flags & 128) != 0) {
                return true;
            }
        }
        return false;
    }

    public boolean sendFingerprintGesture(int keyCode) {
        synchronized (this.mLock) {
            IAccessibilityManager service = getServiceLocked();
            if (service == null) {
                return false;
            }
            try {
                return service.sendFingerprintGesture(keyCode);
            } catch (RemoteException e) {
                return false;
            }
        }
    }

    @SystemApi
    public int getAccessibilityWindowId(IBinder windowToken) {
        if (windowToken == null) {
            return -1;
        }
        synchronized (this.mLock) {
            IAccessibilityManager service = getServiceLocked();
            if (service == null) {
                return -1;
            }
            try {
                return service.getAccessibilityWindowId(windowToken);
            } catch (RemoteException e) {
                return -1;
            }
        }
    }

    public void associateEmbeddedHierarchy(IBinder host, IBinder embedded) {
        synchronized (this.mLock) {
            IAccessibilityManager service = getServiceLocked();
            if (service == null) {
                return;
            }
            try {
                service.associateEmbeddedHierarchy(host, embedded);
            } catch (RemoteException e) {
            }
        }
    }

    public void disassociateEmbeddedHierarchy(IBinder token) {
        if (token == null) {
            return;
        }
        synchronized (this.mLock) {
            IAccessibilityManager service = getServiceLocked();
            if (service == null) {
                return;
            }
            try {
                service.disassociateEmbeddedHierarchy(token);
            } catch (RemoteException e) {
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setStateLocked(int stateFlags) {
        boolean enabled = (stateFlags & 1) != 0;
        boolean touchExplorationEnabled = (stateFlags & 2) != 0;
        boolean highTextContrastEnabled = (stateFlags & 4) != 0;
        boolean audioDescriptionEnabled = (stateFlags & 4096) != 0;
        boolean wasEnabled = isEnabled();
        boolean wasTouchExplorationEnabled = this.mIsTouchExplorationEnabled;
        boolean wasHighTextContrastEnabled = this.mIsHighTextContrastEnabled;
        boolean wasAudioDescriptionByDefaultRequested = this.mIsAudioDescriptionByDefaultRequested;
        this.mIsEnabled = enabled;
        this.mIsTouchExplorationEnabled = touchExplorationEnabled;
        this.mIsHighTextContrastEnabled = highTextContrastEnabled;
        this.mIsAudioDescriptionByDefaultRequested = audioDescriptionEnabled;
        if (wasEnabled != isEnabled()) {
            notifyAccessibilityStateChanged();
        }
        if (wasTouchExplorationEnabled != touchExplorationEnabled) {
            notifyTouchExplorationStateChanged();
        }
        if (wasHighTextContrastEnabled != highTextContrastEnabled) {
            notifyHighTextContrastStateChanged();
        }
        if (wasAudioDescriptionByDefaultRequested != audioDescriptionEnabled) {
            notifyAudioDescriptionbyDefaultStateChanged();
        }
        updateAccessibilityTracingState(stateFlags);
    }

    public AccessibilityServiceInfo getInstalledServiceInfoWithComponentName(ComponentName componentName) {
        List<AccessibilityServiceInfo> installedServiceInfos = getInstalledAccessibilityServiceList();
        if (installedServiceInfos == null || componentName == null) {
            return null;
        }
        for (int i = 0; i < installedServiceInfos.size(); i++) {
            if (componentName.equals(installedServiceInfos.get(i).getComponentName())) {
                return installedServiceInfos.get(i);
            }
        }
        return null;
    }

    public int addAccessibilityInteractionConnection(IWindow windowToken, IBinder leashToken, String packageName, IAccessibilityInteractionConnection connection) {
        synchronized (this.mLock) {
            IAccessibilityManager service = getServiceLocked();
            if (service == null) {
                return -1;
            }
            int userId = this.mUserId;
            try {
                return service.addAccessibilityInteractionConnection(windowToken, leashToken, connection, packageName, userId);
            } catch (RemoteException re) {
                Log.m109e(LOG_TAG, "Error while adding an accessibility interaction connection. ", re);
                return -1;
            }
        }
    }

    public void removeAccessibilityInteractionConnection(IWindow windowToken) {
        synchronized (this.mLock) {
            IAccessibilityManager service = getServiceLocked();
            if (service == null) {
                return;
            }
            try {
                service.removeAccessibilityInteractionConnection(windowToken);
            } catch (RemoteException re) {
                Log.m109e(LOG_TAG, "Error while removing an accessibility interaction connection. ", re);
            }
        }
    }

    @SystemApi
    public void performAccessibilityShortcut() {
        performAccessibilityShortcut(null);
    }

    public void performAccessibilityShortcut(String targetName) {
        synchronized (this.mLock) {
            IAccessibilityManager service = getServiceLocked();
            if (service == null) {
                return;
            }
            try {
                service.performAccessibilityShortcut(targetName);
            } catch (RemoteException re) {
                Log.m109e(LOG_TAG, "Error performing accessibility shortcut. ", re);
            }
        }
    }

    @SystemApi
    public void registerSystemAction(RemoteAction action, int actionId) {
        synchronized (this.mLock) {
            IAccessibilityManager service = getServiceLocked();
            if (service == null) {
                return;
            }
            try {
                service.registerSystemAction(action, actionId);
            } catch (RemoteException re) {
                Log.m109e(LOG_TAG, "Error registering system action " + ((Object) action.getTitle()) + " ", re);
            }
        }
    }

    @SystemApi
    public void unregisterSystemAction(int actionId) {
        synchronized (this.mLock) {
            IAccessibilityManager service = getServiceLocked();
            if (service == null) {
                return;
            }
            try {
                service.unregisterSystemAction(actionId);
            } catch (RemoteException re) {
                Log.m109e(LOG_TAG, "Error unregistering system action with actionId " + actionId + " ", re);
            }
        }
    }

    public void notifyAccessibilityButtonClicked(int displayId) {
        notifyAccessibilityButtonClicked(displayId, null);
    }

    public void notifyAccessibilityButtonClicked(int displayId, String targetName) {
        synchronized (this.mLock) {
            IAccessibilityManager service = getServiceLocked();
            if (service == null) {
                return;
            }
            try {
                service.notifyAccessibilityButtonClicked(displayId, targetName);
            } catch (RemoteException re) {
                Log.m109e(LOG_TAG, "Error while dispatching accessibility button click", re);
            }
        }
    }

    public void notifyAccessibilityButtonVisibilityChanged(boolean shown) {
        synchronized (this.mLock) {
            IAccessibilityManager service = getServiceLocked();
            if (service == null) {
                return;
            }
            try {
                service.notifyAccessibilityButtonVisibilityChanged(shown);
            } catch (RemoteException re) {
                Log.m109e(LOG_TAG, "Error while dispatching accessibility button visibility change", re);
            }
        }
    }

    public void setPictureInPictureActionReplacingConnection(IAccessibilityInteractionConnection connection) {
        synchronized (this.mLock) {
            IAccessibilityManager service = getServiceLocked();
            if (service == null) {
                return;
            }
            try {
                service.setPictureInPictureActionReplacingConnection(connection);
            } catch (RemoteException re) {
                Log.m109e(LOG_TAG, "Error setting picture in picture action replacement", re);
            }
        }
    }

    public List<String> getAccessibilityShortcutTargets(int shortcutType) {
        IAccessibilityManager service;
        synchronized (this.mLock) {
            service = getServiceLocked();
        }
        if (service != null) {
            try {
                return service.getAccessibilityShortcutTargets(shortcutType);
            } catch (RemoteException re) {
                re.rethrowFromSystemServer();
            }
        }
        return Collections.emptyList();
    }

    public List<AccessibilityShortcutInfo> getInstalledAccessibilityShortcutListAsUser(Context context, int userId) {
        List<AccessibilityShortcutInfo> shortcutInfos = new ArrayList<>();
        Intent actionMain = new Intent(Intent.ACTION_MAIN);
        actionMain.addCategory(Intent.CATEGORY_ACCESSIBILITY_SHORTCUT_TARGET);
        PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> installedShortcutList = packageManager.queryIntentActivitiesAsUser(actionMain, 819329, userId);
        for (int i = 0; i < installedShortcutList.size(); i++) {
            AccessibilityShortcutInfo shortcutInfo = getShortcutInfo(context, installedShortcutList.get(i));
            if (shortcutInfo != null) {
                shortcutInfos.add(shortcutInfo);
            }
        }
        return shortcutInfos;
    }

    private AccessibilityShortcutInfo getShortcutInfo(Context context, ResolveInfo resolveInfo) {
        ActivityInfo activityInfo = resolveInfo.activityInfo;
        if (activityInfo == null || activityInfo.metaData == null || activityInfo.metaData.getInt(AccessibilityShortcutInfo.META_DATA) == 0) {
            return null;
        }
        try {
            return new AccessibilityShortcutInfo(context, activityInfo);
        } catch (IOException | XmlPullParserException exp) {
            Log.m109e(LOG_TAG, "Error while initializing AccessibilityShortcutInfo", exp);
            return null;
        }
    }

    public void setWindowMagnificationConnection(IWindowMagnificationConnection connection) {
        synchronized (this.mLock) {
            IAccessibilityManager service = getServiceLocked();
            if (service == null) {
                return;
            }
            try {
                service.setWindowMagnificationConnection(connection);
            } catch (RemoteException re) {
                Log.m109e(LOG_TAG, "Error setting window magnfication connection", re);
            }
        }
    }

    public boolean isAudioDescriptionRequested() {
        synchronized (this.mLock) {
            IAccessibilityManager service = getServiceLocked();
            if (service == null) {
                return false;
            }
            return this.mIsAudioDescriptionByDefaultRequested;
        }
    }

    public void setSystemAudioCaptioningEnabled(boolean isEnabled, int userId) {
        synchronized (this.mLock) {
            IAccessibilityManager service = getServiceLocked();
            if (service == null) {
                return;
            }
            try {
                service.setSystemAudioCaptioningEnabled(isEnabled, userId);
            } catch (RemoteException re) {
                throw re.rethrowFromSystemServer();
            }
        }
    }

    public boolean isSystemAudioCaptioningUiEnabled(int userId) {
        synchronized (this.mLock) {
            IAccessibilityManager service = getServiceLocked();
            if (service == null) {
                return false;
            }
            try {
                return service.isSystemAudioCaptioningUiEnabled(userId);
            } catch (RemoteException re) {
                throw re.rethrowFromSystemServer();
            }
        }
    }

    public void setSystemAudioCaptioningUiEnabled(boolean isEnabled, int userId) {
        synchronized (this.mLock) {
            IAccessibilityManager service = getServiceLocked();
            if (service == null) {
                return;
            }
            try {
                service.setSystemAudioCaptioningUiEnabled(isEnabled, userId);
            } catch (RemoteException re) {
                throw re.rethrowFromSystemServer();
            }
        }
    }

    public void setAccessibilityWindowAttributes(int displayId, int windowId, AccessibilityWindowAttributes attributes) {
        synchronized (this.mLock) {
            IAccessibilityManager service = getServiceLocked();
            if (service == null) {
                return;
            }
            try {
                service.setAccessibilityWindowAttributes(displayId, windowId, this.mUserId, attributes);
            } catch (RemoteException re) {
                re.rethrowFromSystemServer();
            }
        }
    }

    @SystemApi
    public boolean registerDisplayProxy(AccessibilityDisplayProxy proxy) {
        synchronized (this.mLock) {
            IAccessibilityManager service = getServiceLocked();
            if (service == null) {
                return false;
            }
            try {
                return service.registerProxyForDisplay(proxy.mServiceClient, proxy.getDisplayId());
            } catch (RemoteException re) {
                throw re.rethrowFromSystemServer();
            }
        }
    }

    @SystemApi
    public boolean unregisterDisplayProxy(AccessibilityDisplayProxy proxy) {
        synchronized (this.mLock) {
            IAccessibilityManager service = getServiceLocked();
            if (service == null) {
                return false;
            }
            try {
                return service.unregisterProxyForDisplay(proxy.getDisplayId());
            } catch (RemoteException re) {
                throw re.rethrowFromSystemServer();
            }
        }
    }

    public boolean startFlashNotificationSequence(Context context, int reason) {
        synchronized (this.mLock) {
            IAccessibilityManager service = getServiceLocked();
            if (service == null) {
                return false;
            }
            try {
                return service.startFlashNotificationSequence(context.getOpPackageName(), reason, this.mBinder);
            } catch (RemoteException re) {
                Log.m109e(LOG_TAG, "Error while start flash notification sequence", re);
                return false;
            }
        }
    }

    public boolean stopFlashNotificationSequence(Context context) {
        synchronized (this.mLock) {
            IAccessibilityManager service = getServiceLocked();
            if (service == null) {
                return false;
            }
            try {
                return service.stopFlashNotificationSequence(context.getOpPackageName());
            } catch (RemoteException re) {
                Log.m109e(LOG_TAG, "Error while stop flash notification sequence", re);
                return false;
            }
        }
    }

    public boolean startFlashNotificationEvent(Context context, int reason, String reasonPkg) {
        synchronized (this.mLock) {
            IAccessibilityManager service = getServiceLocked();
            if (service == null) {
                return false;
            }
            try {
                return service.startFlashNotificationEvent(context.getOpPackageName(), reason, reasonPkg);
            } catch (RemoteException re) {
                Log.m109e(LOG_TAG, "Error while start flash notification event", re);
                return false;
            }
        }
    }

    private IAccessibilityManager getServiceLocked() {
        if (this.mService == null) {
            tryConnectToServiceLocked(null);
        }
        return this.mService;
    }

    private void tryConnectToServiceLocked(IAccessibilityManager service) {
        if (service == null) {
            IBinder iBinder = ServiceManager.getService(Context.ACCESSIBILITY_SERVICE);
            if (iBinder == null) {
                return;
            }
            service = IAccessibilityManager.Stub.asInterface(iBinder);
        }
        try {
            long userStateAndRelevantEvents = service.addClient(this.mClient, this.mUserId);
            setStateLocked(IntPair.first(userStateAndRelevantEvents));
            this.mRelevantEventTypes = IntPair.second(userStateAndRelevantEvents);
            updateUiTimeout(service.getRecommendedTimeoutMillis());
            updateFocusAppearanceLocked(service.getFocusStrokeWidth(), service.getFocusColor());
            this.mService = service;
        } catch (RemoteException re) {
            Log.m109e(LOG_TAG, "AccessibilityManagerService is dead", re);
        }
    }

    public void notifyAccessibilityStateChanged() {
        synchronized (this.mLock) {
            if (this.mAccessibilityStateChangeListeners.isEmpty()) {
                return;
            }
            final boolean isEnabled = isEnabled();
            ArrayMap<AccessibilityStateChangeListener, Handler> listeners = new ArrayMap<>(this.mAccessibilityStateChangeListeners);
            int numListeners = listeners.size();
            for (int i = 0; i < numListeners; i++) {
                final AccessibilityStateChangeListener listener = listeners.keyAt(i);
                listeners.valueAt(i).post(new Runnable() { // from class: android.view.accessibility.AccessibilityManager$$ExternalSyntheticLambda1
                    @Override // java.lang.Runnable
                    public final void run() {
                        AccessibilityManager.AccessibilityStateChangeListener.this.onAccessibilityStateChanged(isEnabled);
                    }
                });
            }
        }
    }

    private void notifyTouchExplorationStateChanged() {
        synchronized (this.mLock) {
            if (this.mTouchExplorationStateChangeListeners.isEmpty()) {
                return;
            }
            final boolean isTouchExplorationEnabled = this.mIsTouchExplorationEnabled;
            ArrayMap<TouchExplorationStateChangeListener, Handler> listeners = new ArrayMap<>(this.mTouchExplorationStateChangeListeners);
            int numListeners = listeners.size();
            for (int i = 0; i < numListeners; i++) {
                final TouchExplorationStateChangeListener listener = listeners.keyAt(i);
                listeners.valueAt(i).post(new Runnable() { // from class: android.view.accessibility.AccessibilityManager$$ExternalSyntheticLambda3
                    @Override // java.lang.Runnable
                    public final void run() {
                        AccessibilityManager.TouchExplorationStateChangeListener.this.onTouchExplorationStateChanged(isTouchExplorationEnabled);
                    }
                });
            }
        }
    }

    private void notifyHighTextContrastStateChanged() {
        synchronized (this.mLock) {
            if (this.mHighTextContrastStateChangeListeners.isEmpty()) {
                return;
            }
            final boolean isHighTextContrastEnabled = this.mIsHighTextContrastEnabled;
            ArrayMap<HighTextContrastChangeListener, Handler> listeners = new ArrayMap<>(this.mHighTextContrastStateChangeListeners);
            int numListeners = listeners.size();
            for (int i = 0; i < numListeners; i++) {
                final HighTextContrastChangeListener listener = listeners.keyAt(i);
                listeners.valueAt(i).post(new Runnable() { // from class: android.view.accessibility.AccessibilityManager$$ExternalSyntheticLambda2
                    @Override // java.lang.Runnable
                    public final void run() {
                        AccessibilityManager.HighTextContrastChangeListener.this.onHighTextContrastStateChanged(isHighTextContrastEnabled);
                    }
                });
            }
        }
    }

    private void notifyAudioDescriptionbyDefaultStateChanged() {
        synchronized (this.mLock) {
            if (this.mAudioDescriptionRequestedChangeListeners.isEmpty()) {
                return;
            }
            final boolean isAudioDescriptionByDefaultRequested = this.mIsAudioDescriptionByDefaultRequested;
            ArrayMap<AudioDescriptionRequestedChangeListener, Executor> listeners = new ArrayMap<>(this.mAudioDescriptionRequestedChangeListeners);
            int numListeners = listeners.size();
            for (int i = 0; i < numListeners; i++) {
                final AudioDescriptionRequestedChangeListener listener = listeners.keyAt(i);
                listeners.valueAt(i).execute(new Runnable() { // from class: android.view.accessibility.AccessibilityManager$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        AccessibilityManager.AudioDescriptionRequestedChangeListener.this.onAudioDescriptionRequestedChanged(isAudioDescriptionByDefaultRequested);
                    }
                });
            }
        }
    }

    private void updateAccessibilityTracingState(int stateFlag) {
        synchronized (this.mLock) {
            this.mAccessibilityTracingState = stateFlag;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateUiTimeout(long uiTimeout) {
        this.mInteractiveUiTimeout = IntPair.first(uiTimeout);
        this.mNonInteractiveUiTimeout = IntPair.second(uiTimeout);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateFocusAppearanceLocked(int strokeWidth, int color) {
        if (this.mFocusStrokeWidth == strokeWidth && this.mFocusColor == color) {
            return;
        }
        this.mFocusStrokeWidth = strokeWidth;
        this.mFocusColor = color;
    }

    private void initialFocusAppearanceLocked(Resources resource) {
        try {
            this.mFocusStrokeWidth = resource.getDimensionPixelSize(C4057R.dimen.accessibility_focus_highlight_stroke_width);
            this.mFocusColor = resource.getColor(C4057R.color.accessibility_focus_highlight_color);
        } catch (Resources.NotFoundException re) {
            this.mFocusStrokeWidth = (int) (resource.getDisplayMetrics().density * 4.0f);
            this.mFocusColor = -1086737152;
            Log.m109e(LOG_TAG, "Error while initialing the focus appearance data then setting to default value by hardcoded", re);
        }
    }

    public static boolean isAccessibilityButtonSupported() {
        Resources res = Resources.getSystem();
        return res.getBoolean(C4057R.bool.config_showNavigationBar);
    }

    /* loaded from: classes4.dex */
    private final class MyCallback implements Handler.Callback {
        public static final int MSG_SET_STATE = 1;

        private MyCallback() {
        }

        @Override // android.p008os.Handler.Callback
        public boolean handleMessage(Message message) {
            switch (message.what) {
                case 1:
                    int state = message.arg1;
                    synchronized (AccessibilityManager.this.mLock) {
                        AccessibilityManager.this.setStateLocked(state);
                    }
                    return true;
                default:
                    return true;
            }
        }
    }

    public IAccessibilityManager.WindowTransformationSpec getWindowTransformationSpec(int windowId) {
        synchronized (this.mLock) {
            IAccessibilityManager service = getServiceLocked();
            if (service == null) {
                return null;
            }
            try {
                return service.getWindowTransformationSpec(windowId);
            } catch (RemoteException re) {
                throw re.rethrowFromSystemServer();
            }
        }
    }
}
