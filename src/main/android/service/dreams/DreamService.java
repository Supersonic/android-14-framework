package android.service.dreams;

import android.app.Activity;
import android.app.ActivityTaskManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.p001pm.PackageManager;
import android.content.p001pm.ServiceInfo;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.graphics.drawable.Drawable;
import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.media.audio.Enums;
import android.p008os.Binder;
import android.p008os.Handler;
import android.p008os.IBinder;
import android.p008os.IRemoteCallback;
import android.p008os.Looper;
import android.p008os.RemoteException;
import android.p008os.ServiceManager;
import android.service.dreams.DreamService;
import android.service.dreams.IDreamManager;
import android.service.dreams.IDreamOverlay;
import android.service.dreams.IDreamOverlayCallback;
import android.service.dreams.IDreamOverlayClientCallback;
import android.service.dreams.IDreamService;
import android.util.AttributeSet;
import android.util.Log;
import android.util.MathUtils;
import android.util.Slog;
import android.util.Xml;
import android.view.ActionMode;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SearchEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import com.android.internal.C4057R;
import com.android.internal.util.DumpUtils;
import com.android.internal.util.ObservableServiceConnection;
import com.android.internal.util.PersistentServiceConnection;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes3.dex */
public class DreamService extends Service implements Window.Callback {
    private static final boolean DEBUG;
    public static final boolean DEFAULT_SHOW_COMPLICATIONS = false;
    public static final String DREAM_META_DATA = "android.service.dream";
    private static final String DREAM_META_DATA_ROOT_TAG = "dream";
    public static final String DREAM_SERVICE = "dreams";
    public static final String EXTRA_DREAM_OVERLAY_COMPONENT = "android.service.dream.DreamService.dream_overlay_component";
    public static final String SERVICE_INTERFACE = "android.service.dreams.DreamService";
    private static final String TAG;
    private Activity mActivity;
    private boolean mCanDoze;
    private Runnable mDispatchAfterOnAttachedToWindow;
    private boolean mDozing;
    private ComponentName mDreamComponent;
    private DreamServiceWrapper mDreamServiceWrapper;
    private IBinder mDreamToken;
    private boolean mFinished;
    private boolean mFullscreen;
    private boolean mInteractive;
    private OverlayConnection mOverlayConnection;
    private boolean mOverlayFinishing;
    private boolean mShouldShowComplications;
    private boolean mStarted;
    private boolean mWaking;
    private Window mWindow;
    private boolean mWindowless;
    private final String mTag = TAG + NavigationBarInflaterView.SIZE_MOD_START + getClass().getSimpleName() + NavigationBarInflaterView.SIZE_MOD_END;
    private final Handler mHandler = new Handler(Looper.getMainLooper());
    private boolean mScreenBright = true;
    private int mDozeScreenState = 0;
    private int mDozeScreenBrightness = -1;
    private boolean mDebug = false;
    private final IDreamOverlayCallback mOverlayCallback = new BinderC25341();
    private final IDreamManager mDreamManager = IDreamManager.Stub.asInterface(ServiceManager.getService(DREAM_SERVICE));

    static {
        String simpleName = DreamService.class.getSimpleName();
        TAG = simpleName;
        DEBUG = Log.isLoggable(simpleName, 3);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public static class OverlayConnection extends PersistentServiceConnection<IDreamOverlay> {
        private final ObservableServiceConnection.Callback<IDreamOverlay> mCallback;
        private IDreamOverlayClient mClient;
        private final IDreamOverlayClientCallback mClientCallback;
        private final ArrayList<Consumer<IDreamOverlayClient>> mConsumers;

        OverlayConnection(Context context, Executor executor, Handler handler, ObservableServiceConnection.ServiceTransformer<IDreamOverlay> transformer, Intent serviceIntent, int flags, int minConnectionDurationMs, int maxReconnectAttempts, int baseReconnectDelayMs) {
            super(context, executor, handler, transformer, serviceIntent, flags, minConnectionDurationMs, maxReconnectAttempts, baseReconnectDelayMs);
            this.mConsumers = new ArrayList<>();
            this.mClientCallback = new IDreamOverlayClientCallback.Stub() { // from class: android.service.dreams.DreamService.OverlayConnection.1
                @Override // android.service.dreams.IDreamOverlayClientCallback
                public void onDreamOverlayClient(IDreamOverlayClient client) {
                    OverlayConnection.this.mClient = client;
                    Iterator it = OverlayConnection.this.mConsumers.iterator();
                    while (it.hasNext()) {
                        Consumer<IDreamOverlayClient> consumer = (Consumer) it.next();
                        consumer.accept(OverlayConnection.this.mClient);
                    }
                }
            };
            this.mCallback = new ObservableServiceConnection.Callback<IDreamOverlay>() { // from class: android.service.dreams.DreamService.OverlayConnection.2
                @Override // com.android.internal.util.ObservableServiceConnection.Callback
                public void onConnected(ObservableServiceConnection<IDreamOverlay> connection, IDreamOverlay service) {
                    try {
                        service.getClient(OverlayConnection.this.mClientCallback);
                    } catch (RemoteException e) {
                        Log.m109e(DreamService.TAG, "could not get DreamOverlayClient", e);
                    }
                }

                @Override // com.android.internal.util.ObservableServiceConnection.Callback
                public void onDisconnected(ObservableServiceConnection<IDreamOverlay> connection, int reason) {
                    OverlayConnection.this.mClient = null;
                }
            };
        }

        @Override // com.android.internal.util.PersistentServiceConnection, com.android.internal.util.ObservableServiceConnection
        public boolean bind() {
            addCallback(this.mCallback);
            return super.bind();
        }

        @Override // com.android.internal.util.PersistentServiceConnection, com.android.internal.util.ObservableServiceConnection
        public void unbind() {
            removeCallback(this.mCallback);
            super.unbind();
        }

        public void addConsumer(final Consumer<IDreamOverlayClient> consumer) {
            execute(new Runnable() { // from class: android.service.dreams.DreamService$OverlayConnection$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    DreamService.OverlayConnection.this.lambda$addConsumer$0(consumer);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$addConsumer$0(Consumer consumer) {
            this.mConsumers.add(consumer);
            IDreamOverlayClient iDreamOverlayClient = this.mClient;
            if (iDreamOverlayClient != null) {
                consumer.accept(iDreamOverlayClient);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$removeConsumer$1(Consumer consumer) {
            this.mConsumers.remove(consumer);
        }

        public void removeConsumer(final Consumer<IDreamOverlayClient> consumer) {
            execute(new Runnable() { // from class: android.service.dreams.DreamService$OverlayConnection$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    DreamService.OverlayConnection.this.lambda$removeConsumer$1(consumer);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$clearConsumers$2() {
            this.mConsumers.clear();
        }

        public void clearConsumers() {
            execute(new Runnable() { // from class: android.service.dreams.DreamService$OverlayConnection$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    DreamService.OverlayConnection.this.lambda$clearConsumers$2();
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: android.service.dreams.DreamService$1 */
    /* loaded from: classes3.dex */
    public class BinderC25341 extends IDreamOverlayCallback.Stub {
        BinderC25341() {
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onExitRequested$0() {
            DreamService.this.finish();
        }

        @Override // android.service.dreams.IDreamOverlayCallback
        public void onExitRequested() {
            DreamService.this.mHandler.post(new Runnable() { // from class: android.service.dreams.DreamService$1$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    DreamService.BinderC25341.this.lambda$onExitRequested$0();
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onWakeUpComplete$1() {
            DreamService.this.finish();
        }

        @Override // android.service.dreams.IDreamOverlayCallback
        public void onWakeUpComplete() {
            DreamService.this.mHandler.post(new Runnable() { // from class: android.service.dreams.DreamService$1$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    DreamService.BinderC25341.this.lambda$onWakeUpComplete$1();
                }
            });
        }
    }

    public void setDebug(boolean dbg) {
        this.mDebug = dbg;
    }

    @Override // android.view.Window.Callback
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (!this.mInteractive) {
            if (this.mDebug) {
                Slog.m92v(this.mTag, "Waking up on keyEvent");
            }
            wakeUp();
            return true;
        } else if (event.getKeyCode() == 4) {
            if (this.mDebug) {
                Slog.m92v(this.mTag, "Waking up on back key");
            }
            wakeUp();
            return true;
        } else {
            return this.mWindow.superDispatchKeyEvent(event);
        }
    }

    @Override // android.view.Window.Callback
    public boolean dispatchKeyShortcutEvent(KeyEvent event) {
        if (!this.mInteractive) {
            if (this.mDebug) {
                Slog.m92v(this.mTag, "Waking up on keyShortcutEvent");
            }
            wakeUp();
            return true;
        }
        return this.mWindow.superDispatchKeyShortcutEvent(event);
    }

    @Override // android.view.Window.Callback
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (!this.mInteractive && event.getActionMasked() == 1) {
            if (this.mDebug) {
                Slog.m92v(this.mTag, "Waking up on touchEvent");
            }
            wakeUp();
            return true;
        }
        return this.mWindow.superDispatchTouchEvent(event);
    }

    @Override // android.view.Window.Callback
    public boolean dispatchTrackballEvent(MotionEvent event) {
        if (!this.mInteractive) {
            if (this.mDebug) {
                Slog.m92v(this.mTag, "Waking up on trackballEvent");
            }
            wakeUp();
            return true;
        }
        return this.mWindow.superDispatchTrackballEvent(event);
    }

    @Override // android.view.Window.Callback
    public boolean dispatchGenericMotionEvent(MotionEvent event) {
        if (!this.mInteractive) {
            if (this.mDebug) {
                Slog.m92v(this.mTag, "Waking up on genericMotionEvent");
            }
            wakeUp();
            return true;
        }
        return this.mWindow.superDispatchGenericMotionEvent(event);
    }

    @Override // android.view.Window.Callback
    public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent event) {
        return false;
    }

    @Override // android.view.Window.Callback
    public View onCreatePanelView(int featureId) {
        return null;
    }

    @Override // android.view.Window.Callback
    public boolean onCreatePanelMenu(int featureId, Menu menu) {
        return false;
    }

    @Override // android.view.Window.Callback
    public boolean onPreparePanel(int featureId, View view, Menu menu) {
        return false;
    }

    @Override // android.view.Window.Callback
    public boolean onMenuOpened(int featureId, Menu menu) {
        return false;
    }

    @Override // android.view.Window.Callback
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        return false;
    }

    @Override // android.view.Window.Callback
    public void onWindowAttributesChanged(WindowManager.LayoutParams attrs) {
    }

    @Override // android.view.Window.Callback
    public void onContentChanged() {
    }

    @Override // android.view.Window.Callback
    public void onWindowFocusChanged(boolean hasFocus) {
    }

    @Override // android.view.Window.Callback
    public void onAttachedToWindow() {
    }

    @Override // android.view.Window.Callback
    public void onDetachedFromWindow() {
    }

    @Override // android.view.Window.Callback
    public void onPanelClosed(int featureId, Menu menu) {
    }

    @Override // android.view.Window.Callback
    public boolean onSearchRequested(SearchEvent event) {
        return onSearchRequested();
    }

    @Override // android.view.Window.Callback
    public boolean onSearchRequested() {
        return false;
    }

    @Override // android.view.Window.Callback
    public ActionMode onWindowStartingActionMode(ActionMode.Callback callback) {
        return null;
    }

    @Override // android.view.Window.Callback
    public ActionMode onWindowStartingActionMode(ActionMode.Callback callback, int type) {
        return null;
    }

    @Override // android.view.Window.Callback
    public void onActionModeStarted(ActionMode mode) {
    }

    @Override // android.view.Window.Callback
    public void onActionModeFinished(ActionMode mode) {
    }

    public WindowManager getWindowManager() {
        Window window = this.mWindow;
        if (window != null) {
            return window.getWindowManager();
        }
        return null;
    }

    public Window getWindow() {
        return this.mWindow;
    }

    public void setContentView(int layoutResID) {
        getWindow().setContentView(layoutResID);
    }

    public void setContentView(View view) {
        getWindow().setContentView(view);
    }

    public void setContentView(View view, ViewGroup.LayoutParams params) {
        getWindow().setContentView(view, params);
    }

    public void addContentView(View view, ViewGroup.LayoutParams params) {
        getWindow().addContentView(view, params);
    }

    public <T extends View> T findViewById(int id) {
        return (T) getWindow().findViewById(id);
    }

    public final <T extends View> T requireViewById(int id) {
        T view = (T) findViewById(id);
        if (view == null) {
            throw new IllegalArgumentException("ID does not reference a View inside this DreamService");
        }
        return view;
    }

    public void setInteractive(boolean interactive) {
        this.mInteractive = interactive;
    }

    public boolean isInteractive() {
        return this.mInteractive;
    }

    public void setFullscreen(boolean fullscreen) {
        if (this.mFullscreen != fullscreen) {
            this.mFullscreen = fullscreen;
            applyWindowFlags(fullscreen ? 1024 : 0, 1024);
        }
    }

    public boolean isFullscreen() {
        return this.mFullscreen;
    }

    public void setScreenBright(boolean screenBright) {
        if (this.mScreenBright != screenBright) {
            this.mScreenBright = screenBright;
            applyWindowFlags(screenBright ? 128 : 0, 128);
        }
    }

    public boolean isScreenBright() {
        return getWindowFlagValue(128, this.mScreenBright);
    }

    public void setWindowless(boolean windowless) {
        this.mWindowless = windowless;
    }

    public boolean isWindowless() {
        return this.mWindowless;
    }

    public boolean canDoze() {
        return this.mCanDoze;
    }

    public void startDozing() {
        if (this.mCanDoze && !this.mDozing) {
            this.mDozing = true;
            updateDoze();
        }
    }

    private void updateDoze() {
        IBinder iBinder = this.mDreamToken;
        if (iBinder == null) {
            Slog.m90w(this.mTag, "Updating doze without a dream token.");
        } else if (this.mDozing) {
            try {
                this.mDreamManager.startDozing(iBinder, this.mDozeScreenState, this.mDozeScreenBrightness);
            } catch (RemoteException e) {
            }
        }
    }

    public void stopDozing() {
        if (this.mDozing) {
            this.mDozing = false;
            try {
                this.mDreamManager.stopDozing(this.mDreamToken);
            } catch (RemoteException e) {
            }
        }
    }

    public boolean isDozing() {
        return this.mDozing;
    }

    public int getDozeScreenState() {
        return this.mDozeScreenState;
    }

    public void setDozeScreenState(int state) {
        if (this.mDozeScreenState != state) {
            this.mDozeScreenState = state;
            updateDoze();
        }
    }

    public int getDozeScreenBrightness() {
        return this.mDozeScreenBrightness;
    }

    public void setDozeScreenBrightness(int brightness) {
        if (brightness != -1) {
            brightness = clampAbsoluteBrightness(brightness);
        }
        if (this.mDozeScreenBrightness != brightness) {
            this.mDozeScreenBrightness = brightness;
            updateDoze();
        }
    }

    @Override // android.app.Service
    public void onCreate() {
        if (this.mDebug) {
            Slog.m92v(this.mTag, "onCreate()");
        }
        ComponentName componentName = new ComponentName(this, getClass());
        this.mDreamComponent = componentName;
        this.mShouldShowComplications = fetchShouldShowComplications(this, fetchServiceInfo(this, componentName));
        super.onCreate();
    }

    public void onDreamingStarted() {
        if (this.mDebug) {
            Slog.m92v(this.mTag, "onDreamingStarted()");
        }
    }

    public void onDreamingStopped() {
        if (this.mDebug) {
            Slog.m92v(this.mTag, "onDreamingStopped()");
        }
    }

    public void onWakeUp() {
        OverlayConnection overlayConnection = this.mOverlayConnection;
        if (overlayConnection != null) {
            overlayConnection.addConsumer(new Consumer() { // from class: android.service.dreams.DreamService$$ExternalSyntheticLambda3
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    DreamService.this.lambda$onWakeUp$0((IDreamOverlayClient) obj);
                }
            });
        } else {
            finish();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onWakeUp$0(IDreamOverlayClient overlay) {
        try {
            overlay.wakeUp();
        } catch (RemoteException e) {
            Slog.m95e(TAG, "Error waking the overlay service", e);
            finish();
        }
    }

    @Override // android.app.Service
    public final IBinder onBind(Intent intent) {
        if (this.mDebug) {
            Slog.m92v(this.mTag, "onBind() intent = " + intent);
        }
        this.mDreamServiceWrapper = new DreamServiceWrapper();
        ComponentName overlayComponent = (ComponentName) intent.getParcelableExtra(EXTRA_DREAM_OVERLAY_COMPONENT, ComponentName.class);
        if (!this.mWindowless && overlayComponent != null) {
            Resources resources = getResources();
            Intent overlayIntent = new Intent().setComponent(overlayComponent);
            OverlayConnection overlayConnection = new OverlayConnection(this, getMainExecutor(), this.mHandler, new ObservableServiceConnection.ServiceTransformer() { // from class: android.service.dreams.DreamService$$ExternalSyntheticLambda0
                @Override // com.android.internal.util.ObservableServiceConnection.ServiceTransformer
                public final Object convert(IBinder iBinder) {
                    return IDreamOverlay.Stub.asInterface(iBinder);
                }
            }, overlayIntent, Enums.AUDIO_FORMAT_AAC_MAIN, resources.getInteger(C4057R.integer.config_minDreamOverlayDurationMs), resources.getInteger(C4057R.integer.config_dreamOverlayMaxReconnectAttempts), resources.getInteger(C4057R.integer.config_dreamOverlayReconnectTimeoutMs));
            this.mOverlayConnection = overlayConnection;
            overlayConnection.bind();
        }
        return this.mDreamServiceWrapper;
    }

    @Override // android.app.Service
    public boolean onUnbind(Intent intent) {
        OverlayConnection overlayConnection = this.mOverlayConnection;
        if (overlayConnection != null) {
            overlayConnection.unbind();
            this.mOverlayConnection = null;
        }
        return super.onUnbind(intent);
    }

    public final void finish() {
        OverlayConnection overlayConnection = this.mOverlayConnection;
        if (overlayConnection != null && !this.mOverlayFinishing) {
            this.mOverlayFinishing = true;
            overlayConnection.addConsumer(new Consumer() { // from class: android.service.dreams.DreamService$$ExternalSyntheticLambda4
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    DreamService.this.lambda$finish$1((IDreamOverlayClient) obj);
                }
            });
            this.mOverlayConnection.clearConsumers();
            return;
        }
        if (this.mDebug) {
            Slog.m92v(this.mTag, "finish(): mFinished=" + this.mFinished);
        }
        Activity activity = this.mActivity;
        if (activity != null) {
            if (!activity.isFinishing()) {
                activity.finishAndRemoveTask();
            }
        } else if (this.mFinished) {
        } else {
            this.mFinished = true;
            IBinder iBinder = this.mDreamToken;
            if (iBinder == null) {
                if (this.mDebug) {
                    Slog.m92v(this.mTag, "finish() called when not attached.");
                }
                stopSelf();
                return;
            }
            try {
                this.mDreamManager.finishSelf(iBinder, true);
            } catch (RemoteException e) {
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$finish$1(IDreamOverlayClient overlay) {
        try {
            overlay.endDream();
            this.mOverlayConnection.unbind();
            this.mOverlayConnection = null;
            finish();
        } catch (RemoteException e) {
            Log.m110e(this.mTag, "could not inform overlay of dream end:" + e);
        }
    }

    public final void wakeUp() {
        wakeUp(false);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void wakeUp(boolean fromSystem) {
        if (this.mDebug) {
            Slog.m92v(this.mTag, "wakeUp(): fromSystem=" + fromSystem + ", mWaking=" + this.mWaking + ", mFinished=" + this.mFinished);
        }
        if (!this.mWaking && !this.mFinished) {
            this.mWaking = true;
            Activity activity = this.mActivity;
            if (activity != null) {
                activity.convertToTranslucent(null, null);
            }
            onWakeUp();
            if (!fromSystem && !this.mFinished) {
                if (this.mActivity == null) {
                    Slog.m90w(this.mTag, "WakeUp was called before the dream was attached.");
                    return;
                }
                try {
                    this.mDreamManager.finishSelf(this.mDreamToken, false);
                } catch (RemoteException e) {
                }
            }
        }
    }

    @Override // android.app.Service
    public void onDestroy() {
        if (this.mDebug) {
            Slog.m92v(this.mTag, "onDestroy()");
        }
        detach();
        super.onDestroy();
    }

    public static DreamMetadata getDreamMetadata(Context context, ServiceInfo serviceInfo) {
        if (serviceInfo == null) {
            return null;
        }
        PackageManager pm = context.getPackageManager();
        TypedArray rawMetadata = readMetadata(pm, serviceInfo);
        if (rawMetadata != null) {
            try {
                DreamMetadata dreamMetadata = new DreamMetadata(convertToComponentName(rawMetadata.getString(0), serviceInfo), rawMetadata.getDrawable(1), rawMetadata.getBoolean(2, false));
                if (rawMetadata != null) {
                    rawMetadata.close();
                }
                return dreamMetadata;
            } catch (Throwable th) {
                if (rawMetadata != null) {
                    try {
                        rawMetadata.close();
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                    }
                }
                throw th;
            }
        }
        if (rawMetadata != null) {
            rawMetadata.close();
        }
        return null;
    }

    private static TypedArray readMetadata(PackageManager pm, ServiceInfo serviceInfo) {
        if (serviceInfo == null || serviceInfo.metaData == null) {
            return null;
        }
        try {
            XmlResourceParser parser = serviceInfo.loadXmlMetaData(pm, DREAM_META_DATA);
            if (parser == null) {
                if (DEBUG) {
                    Log.m104w(TAG, "No android.service.dream metadata");
                }
                if (parser != null) {
                    parser.close();
                }
                return null;
            }
            AttributeSet attrs = Xml.asAttributeSet(parser);
            while (true) {
                int type = parser.next();
                if (type == 1 || type == 2) {
                    break;
                }
            }
            if (parser.getName().equals("dream")) {
                TypedArray obtainAttributes = pm.getResourcesForApplication(serviceInfo.applicationInfo).obtainAttributes(attrs, C4057R.styleable.Dream);
                if (parser != null) {
                    parser.close();
                }
                return obtainAttributes;
            }
            if (DEBUG) {
                Log.m104w(TAG, "Metadata does not start with dream tag");
            }
            if (parser != null) {
                parser.close();
            }
            return null;
        } catch (PackageManager.NameNotFoundException | IOException | XmlPullParserException e) {
            if (DEBUG) {
                Log.m109e(TAG, "Error parsing: " + serviceInfo.packageName, e);
            }
            return null;
        }
    }

    private static ComponentName convertToComponentName(String flattenedString, ServiceInfo serviceInfo) {
        if (flattenedString == null) {
            return null;
        }
        if (!flattenedString.contains("/")) {
            return new ComponentName(serviceInfo.packageName, flattenedString);
        }
        ComponentName cn = ComponentName.unflattenFromString(flattenedString);
        if (cn == null) {
            return null;
        }
        if (!cn.getPackageName().equals(serviceInfo.packageName)) {
            Log.m104w(TAG, "Inconsistent package name in component: " + cn.getPackageName() + ", should be: " + serviceInfo.packageName);
            return null;
        }
        return cn;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void detach() {
        if (this.mStarted) {
            if (this.mDebug) {
                Slog.m92v(this.mTag, "detach(): Calling onDreamingStopped()");
            }
            this.mStarted = false;
            onDreamingStopped();
        }
        Activity activity = this.mActivity;
        if (activity != null && !activity.isFinishing()) {
            this.mActivity.finishAndRemoveTask();
        } else {
            finish();
        }
        this.mDreamToken = null;
        this.mCanDoze = false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void attach(IBinder dreamToken, boolean canDoze, boolean isPreviewMode, final IRemoteCallback started) {
        if (this.mDreamToken != null) {
            Slog.m96e(this.mTag, "attach() called when dream with token=" + this.mDreamToken + " already attached");
        } else if (this.mFinished || this.mWaking) {
            Slog.m90w(this.mTag, "attach() called after dream already finished");
            try {
                this.mDreamManager.finishSelf(dreamToken, true);
            } catch (RemoteException e) {
            }
        } else {
            this.mDreamToken = dreamToken;
            this.mCanDoze = canDoze;
            if (this.mWindowless && !canDoze) {
                throw new IllegalStateException("Only doze dreams can be windowless");
            }
            Runnable runnable = new Runnable() { // from class: android.service.dreams.DreamService$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    DreamService.this.lambda$attach$2(started);
                }
            };
            this.mDispatchAfterOnAttachedToWindow = runnable;
            if (!this.mWindowless) {
                Intent i = new Intent(this, DreamActivity.class);
                i.setPackage(getApplicationContext().getPackageName());
                i.setFlags(268697600);
                i.putExtra("binder", new DreamActivityCallbacks(this.mDreamToken));
                ServiceInfo serviceInfo = fetchServiceInfo(this, new ComponentName(this, getClass()));
                i.putExtra("title", fetchDreamLabel(this, serviceInfo, isPreviewMode));
                try {
                    if (!ActivityTaskManager.getService().startDreamActivity(i)) {
                        detach();
                        return;
                    }
                    return;
                } catch (RemoteException e2) {
                    Log.m104w(this.mTag, "Could not connect to activity task manager to start dream activity");
                    e2.rethrowFromSystemServer();
                    return;
                }
            }
            runnable.run();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$attach$2(IRemoteCallback started) {
        if (this.mWindow != null || this.mWindowless) {
            this.mStarted = true;
            try {
                onDreamingStarted();
                try {
                    started.sendResult(null);
                } catch (RemoteException e) {
                    throw e.rethrowFromSystemServer();
                }
            } catch (Throwable th) {
                try {
                    started.sendResult(null);
                    throw th;
                } catch (RemoteException e2) {
                    throw e2.rethrowFromSystemServer();
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onWindowCreated(Window w) {
        this.mWindow = w;
        w.setCallback(this);
        this.mWindow.requestFeature(1);
        WindowManager.LayoutParams lp = this.mWindow.getAttributes();
        lp.flags |= (this.mFullscreen ? 1024 : 0) | 21561601 | (this.mScreenBright ? 128 : 0);
        lp.layoutInDisplayCutoutMode = 3;
        this.mWindow.setAttributes(lp);
        this.mWindow.clearFlags(Integer.MIN_VALUE);
        this.mWindow.getDecorView().getWindowInsetsController().hide(WindowInsets.Type.systemBars());
        this.mWindow.setDecorFitsSystemWindows(false);
        this.mWindow.getDecorView().addOnAttachStateChangeListener(new View$OnAttachStateChangeListenerC25352());
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: android.service.dreams.DreamService$2 */
    /* loaded from: classes3.dex */
    public class View$OnAttachStateChangeListenerC25352 implements View.OnAttachStateChangeListener {
        private Consumer<IDreamOverlayClient> mDreamStartOverlayConsumer;

        View$OnAttachStateChangeListenerC25352() {
        }

        @Override // android.view.View.OnAttachStateChangeListener
        public void onViewAttachedToWindow(View v) {
            DreamService.this.mDispatchAfterOnAttachedToWindow.run();
            if (DreamService.this.mOverlayConnection != null) {
                this.mDreamStartOverlayConsumer = new Consumer() { // from class: android.service.dreams.DreamService$2$$ExternalSyntheticLambda0
                    @Override // java.util.function.Consumer
                    public final void accept(Object obj) {
                        DreamService.View$OnAttachStateChangeListenerC25352.this.lambda$onViewAttachedToWindow$0((IDreamOverlayClient) obj);
                    }
                };
                DreamService.this.mOverlayConnection.addConsumer(this.mDreamStartOverlayConsumer);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onViewAttachedToWindow$0(IDreamOverlayClient overlay) {
            try {
                overlay.startDream(DreamService.this.mWindow.getAttributes(), DreamService.this.mOverlayCallback, DreamService.this.mDreamComponent.flattenToString(), DreamService.this.mShouldShowComplications);
            } catch (RemoteException e) {
                Log.m110e(DreamService.this.mTag, "could not send window attributes:" + e);
            }
        }

        @Override // android.view.View.OnAttachStateChangeListener
        public void onViewDetachedFromWindow(View v) {
            if (DreamService.this.mActivity == null || !DreamService.this.mActivity.isChangingConfigurations()) {
                DreamService.this.mWindow = null;
                DreamService.this.mActivity = null;
                DreamService.this.finish();
            }
            if (DreamService.this.mOverlayConnection != null && this.mDreamStartOverlayConsumer != null) {
                DreamService.this.mOverlayConnection.removeConsumer(this.mDreamStartOverlayConsumer);
            }
        }
    }

    private boolean getWindowFlagValue(int flag, boolean defaultValue) {
        Window window = this.mWindow;
        return window == null ? defaultValue : (window.getAttributes().flags & flag) != 0;
    }

    private void applyWindowFlags(int flags, int mask) {
        Window window = this.mWindow;
        if (window != null) {
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.flags = applyFlags(lp.flags, flags, mask);
            this.mWindow.setAttributes(lp);
            this.mWindow.getWindowManager().updateViewLayout(this.mWindow.getDecorView(), lp);
        }
    }

    private int applyFlags(int oldFlags, int flags, int mask) {
        return ((~mask) & oldFlags) | (flags & mask);
    }

    private static boolean fetchShouldShowComplications(Context context, ServiceInfo serviceInfo) {
        DreamMetadata metadata = getDreamMetadata(context, serviceInfo);
        if (metadata != null) {
            return metadata.showComplications;
        }
        return false;
    }

    private static CharSequence fetchDreamLabel(Context context, ServiceInfo serviceInfo, boolean isPreviewMode) {
        if (serviceInfo == null) {
            return null;
        }
        PackageManager pm = context.getPackageManager();
        CharSequence dreamLabel = serviceInfo.loadLabel(pm);
        if (!isPreviewMode || dreamLabel == null) {
            return dreamLabel;
        }
        return context.getResources().getString(C4057R.string.dream_preview_title, dreamLabel);
    }

    private static ServiceInfo fetchServiceInfo(Context context, ComponentName componentName) {
        PackageManager pm = context.getPackageManager();
        try {
            return pm.getServiceInfo(componentName, PackageManager.ComponentInfoFlags.m190of(128L));
        } catch (PackageManager.NameNotFoundException e) {
            if (DEBUG) {
                Log.m104w(TAG, "cannot find component " + componentName.flattenToShortString());
                return null;
            }
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$dump$3(FileDescriptor fd, String[] args, PrintWriter pw1, String prefix) {
        dumpOnHandler(fd, pw1, args);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.app.Service
    public void dump(final FileDescriptor fd, PrintWriter pw, final String[] args) {
        DumpUtils.dumpAsync(this.mHandler, new DumpUtils.Dump() { // from class: android.service.dreams.DreamService$$ExternalSyntheticLambda2
            @Override // com.android.internal.util.DumpUtils.Dump
            public final void dump(PrintWriter printWriter, String str) {
                DreamService.this.lambda$dump$3(fd, args, printWriter, str);
            }
        }, pw, "", 1000L);
    }

    protected void dumpOnHandler(FileDescriptor fd, PrintWriter pw, String[] args) {
        pw.print(this.mTag + ": ");
        if (this.mFinished) {
            pw.println("stopped");
        } else {
            pw.println("running (dreamToken=" + this.mDreamToken + NavigationBarInflaterView.KEY_CODE_END);
        }
        pw.println("  window: " + this.mWindow);
        pw.print("  flags:");
        if (isInteractive()) {
            pw.print(" interactive");
        }
        if (isFullscreen()) {
            pw.print(" fullscreen");
        }
        if (isScreenBright()) {
            pw.print(" bright");
        }
        if (isWindowless()) {
            pw.print(" windowless");
        }
        if (isDozing()) {
            pw.print(" dozing");
        } else if (canDoze()) {
            pw.print(" candoze");
        }
        pw.println();
        if (canDoze()) {
            pw.println("  doze screen state: " + Display.stateToString(this.mDozeScreenState));
            pw.println("  doze screen brightness: " + this.mDozeScreenBrightness);
        }
    }

    private static int clampAbsoluteBrightness(int value) {
        return MathUtils.constrain(value, 0, 255);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes3.dex */
    public final class DreamServiceWrapper extends IDreamService.Stub {
        DreamServiceWrapper() {
        }

        @Override // android.service.dreams.IDreamService
        public void attach(final IBinder dreamToken, final boolean canDoze, final boolean isPreviewMode, final IRemoteCallback started) {
            DreamService.this.mHandler.post(new Runnable() { // from class: android.service.dreams.DreamService$DreamServiceWrapper$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    DreamService.DreamServiceWrapper.this.lambda$attach$0(dreamToken, canDoze, isPreviewMode, started);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$attach$0(IBinder dreamToken, boolean canDoze, boolean isPreviewMode, IRemoteCallback started) {
            DreamService.this.attach(dreamToken, canDoze, isPreviewMode, started);
        }

        @Override // android.service.dreams.IDreamService
        public void detach() {
            Handler handler = DreamService.this.mHandler;
            final DreamService dreamService = DreamService.this;
            handler.post(new Runnable() { // from class: android.service.dreams.DreamService$DreamServiceWrapper$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    DreamService.this.detach();
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$wakeUp$1() {
            DreamService.this.wakeUp(true);
        }

        @Override // android.service.dreams.IDreamService
        public void wakeUp() {
            DreamService.this.mHandler.post(new Runnable() { // from class: android.service.dreams.DreamService$DreamServiceWrapper$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    DreamService.DreamServiceWrapper.this.lambda$wakeUp$1();
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes3.dex */
    public final class DreamActivityCallbacks extends Binder {
        private final IBinder mActivityDreamToken;

        DreamActivityCallbacks(IBinder token) {
            this.mActivityDreamToken = token;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public void onActivityCreated(DreamActivity activity) {
            if (this.mActivityDreamToken != DreamService.this.mDreamToken || DreamService.this.mFinished) {
                Slog.m98d(DreamService.TAG, "DreamActivity was created after the dream was finished or a new dream started, finishing DreamActivity");
                if (!activity.isFinishing()) {
                    activity.finishAndRemoveTask();
                }
            } else if (DreamService.this.mActivity != null) {
                Slog.m90w(DreamService.TAG, "A DreamActivity has already been started, finishing latest DreamActivity");
                if (!activity.isFinishing()) {
                    activity.finishAndRemoveTask();
                }
            } else {
                DreamService.this.mActivity = activity;
                DreamService.this.onWindowCreated(activity.getWindow());
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public void onActivityDestroyed() {
            DreamService.this.mActivity = null;
            DreamService.this.mWindow = null;
            DreamService.this.detach();
        }
    }

    /* loaded from: classes3.dex */
    public static final class DreamMetadata {
        public final Drawable previewImage;
        public final ComponentName settingsActivity;
        public final boolean showComplications;

        DreamMetadata(ComponentName settingsActivity, Drawable previewImage, boolean showComplications) {
            this.settingsActivity = settingsActivity;
            this.previewImage = previewImage;
            this.showComplications = showComplications;
        }
    }
}
