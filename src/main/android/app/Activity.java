package android.app;

import android.annotation.SystemApi;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Application;
import android.app.IRequestFinishCallback;
import android.app.Instrumentation;
import android.app.PictureInPictureParams;
import android.app.VoiceInteractor;
import android.app.assist.AssistContent;
import android.app.compat.CompatChanges;
import android.content.ComponentCallbacks;
import android.content.ComponentCallbacks2;
import android.content.ComponentCallbacksController;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.IIntentSender;
import android.content.Intent;
import android.content.IntentSender;
import android.content.LocusId;
import android.content.SharedPreferences;
import android.content.p001pm.ActivityInfo;
import android.content.p001pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.media.session.MediaController;
import android.net.Uri;
import android.p008os.BadParcelableException;
import android.p008os.Bundle;
import android.p008os.CancellationSignal;
import android.p008os.GraphicsEnvironment;
import android.p008os.Handler;
import android.p008os.IBinder;
import android.p008os.Looper;
import android.p008os.OutcomeReceiver;
import android.p008os.Parcelable;
import android.p008os.PersistableBundle;
import android.p008os.Process;
import android.p008os.RemoteException;
import android.p008os.ServiceManager;
import android.p008os.StrictMode;
import android.p008os.SystemClock;
import android.p008os.Trace;
import android.p008os.UserHandle;
import android.text.Selection;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.method.TextKeyListener;
import android.transition.Scene;
import android.transition.TransitionManager;
import android.util.ArrayMap;
import android.util.AttributeSet;
import android.util.Dumpable;
import android.util.EventLog;
import android.util.Log;
import android.util.PrintWriterPrinter;
import android.util.SparseArray;
import android.util.SuperNotCalledException;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.ContextThemeWrapper;
import android.view.DragAndDropPermissions;
import android.view.DragEvent;
import android.view.KeyEvent;
import android.view.KeyboardShortcutGroup;
import android.view.KeyboardShortcutInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.RemoteAnimationDefinition;
import android.view.SearchEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.view.ViewRootImpl;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManagerGlobal;
import android.view.accessibility.AccessibilityEvent;
import android.view.autofill.AutofillClientController;
import android.view.autofill.AutofillId;
import android.view.autofill.AutofillManager;
import android.view.contentcapture.ContentCaptureContext;
import android.view.contentcapture.ContentCaptureManager;
import android.view.translation.TranslationSpec;
import android.view.translation.UiTranslationController;
import android.view.translation.UiTranslationSpec;
import android.webkit.WebView;
import android.widget.Toast;
import android.widget.Toolbar;
import android.window.OnBackInvokedCallback;
import android.window.OnBackInvokedDispatcher;
import android.window.SplashScreen;
import android.window.WindowOnBackInvokedDispatcher;
import com.android.internal.C4057R;
import com.android.internal.app.IVoiceInteractionManagerService;
import com.android.internal.app.IVoiceInteractor;
import com.android.internal.app.ToolbarActionBar;
import com.android.internal.app.WindowDecorActionBar;
import com.android.internal.policy.PhoneWindow;
import com.android.internal.util.dump.DumpableContainerImpl;
import dalvik.system.VMRuntime;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
/* loaded from: classes.dex */
public class Activity extends ContextThemeWrapper implements LayoutInflater.Factory2, Window.Callback, KeyEvent.Callback, View.OnCreateContextMenuListener, ComponentCallbacks2, Window.OnWindowDismissedCallback, ContentCaptureManager.ContentCaptureClient {
    private static final int CONTENT_CAPTURE_PAUSE = 3;
    private static final int CONTENT_CAPTURE_RESUME = 2;
    private static final int CONTENT_CAPTURE_START = 1;
    private static final int CONTENT_CAPTURE_STOP = 4;
    private static final boolean DEBUG_LIFECYCLE = false;
    public static final int DEFAULT_KEYS_DIALER = 1;
    public static final int DEFAULT_KEYS_DISABLE = 0;
    public static final int DEFAULT_KEYS_SEARCH_GLOBAL = 4;
    public static final int DEFAULT_KEYS_SEARCH_LOCAL = 3;
    public static final int DEFAULT_KEYS_SHORTCUT = 2;
    public static final int DONT_FINISH_TASK_WITH_ACTIVITY = 0;
    public static final String DUMP_ARG_AUTOFILL = "--autofill";
    public static final String DUMP_ARG_CONTENT_CAPTURE = "--contentcapture";
    public static final String DUMP_ARG_DUMP_DUMPABLE = "--dump-dumpable";
    public static final String DUMP_ARG_LIST_DUMPABLES = "--list-dumpables";
    public static final String DUMP_ARG_TRANSLATION = "--translation";
    private static final long DUMP_IGNORES_SPECIAL_ARGS = 149254050;
    public static final int FINISH_TASK_WITH_ACTIVITY = 2;
    public static final int FINISH_TASK_WITH_ROOT_ACTIVITY = 1;
    protected static final int[] FOCUSED_STATE_SET = {16842908};
    static final String FRAGMENTS_TAG = "android:fragments";
    public static final int FULLSCREEN_MODE_REQUEST_ENTER = 1;
    public static final int FULLSCREEN_MODE_REQUEST_EXIT = 0;
    private static final String HAS_CURENT_PERMISSIONS_REQUEST_KEY = "android:hasCurrentPermissionsRequest";
    private static final String KEYBOARD_SHORTCUTS_RECEIVER_PKG_NAME = "com.android.systemui";
    private static final int LOG_AM_ON_ACTIVITY_RESULT_CALLED = 30062;
    private static final int LOG_AM_ON_CREATE_CALLED = 30057;
    private static final int LOG_AM_ON_DESTROY_CALLED = 30060;
    private static final int LOG_AM_ON_PAUSE_CALLED = 30021;
    private static final int LOG_AM_ON_RESTART_CALLED = 30058;
    private static final int LOG_AM_ON_RESUME_CALLED = 30022;
    private static final int LOG_AM_ON_START_CALLED = 30059;
    private static final int LOG_AM_ON_STOP_CALLED = 30049;
    private static final int LOG_AM_ON_TOP_RESUMED_GAINED_CALLED = 30064;
    private static final int LOG_AM_ON_TOP_RESUMED_LOST_CALLED = 30065;
    public static final int OVERRIDE_TRANSITION_CLOSE = 1;
    public static final int OVERRIDE_TRANSITION_OPEN = 0;
    private static final String REQUEST_PERMISSIONS_WHO_PREFIX = "@android:requestPermissions:";
    public static final int RESULT_CANCELED = 0;
    public static final int RESULT_FIRST_USER = 1;
    public static final int RESULT_OK = -1;
    private static final String SAVED_DIALOGS_TAG = "android:savedDialogs";
    private static final String SAVED_DIALOG_ARGS_KEY_PREFIX = "android:dialog_args_";
    private static final String SAVED_DIALOG_IDS_KEY = "android:savedDialogIds";
    private static final String SAVED_DIALOG_KEY_PREFIX = "android:dialog_";
    private static final String TAG = "Activity";
    private static final String WINDOW_HIERARCHY_TAG = "android:viewHierarchyState";
    ActivityInfo mActivityInfo;
    private Application mApplication;
    private IBinder mAssistToken;
    private AutofillClientController mAutofillClientController;
    private ComponentCallbacksController mCallbacksController;
    boolean mCalled;
    private boolean mChangeCanvasToTranslucent;
    private ComponentName mComponent;
    int mConfigChangeFlags;
    private ContentCaptureManager mContentCaptureManager;
    private OnBackInvokedCallback mDefaultBackCallback;
    private boolean mDestroyed;
    private DumpableContainerImpl mDumpableContainer;
    String mEmbeddedID;
    private boolean mEnableDefaultActionBarUp;
    boolean mEnterAnimationComplete;
    boolean mFinished;
    private boolean mHasCurrentPermissionsRequest;
    private int mIdent;
    private Instrumentation mInstrumentation;
    Intent mIntent;
    private boolean mIsInMultiWindowMode;
    boolean mIsInPictureInPictureMode;
    NonConfigurationInstances mLastNonConfigurationInstances;
    boolean mLaunchedFromBubble;
    ActivityThread mMainThread;
    private SparseArray<ManagedDialog> mManagedDialogs;
    private MenuInflater mMenuInflater;
    Activity mParent;
    ActivityOptions mPendingOptions;
    String mReferrer;
    private boolean mRestoredFromBundle;
    boolean mResumed;
    private ScreenCaptureCallbackHandler mScreenCaptureCallbackHandler;
    private SearchEvent mSearchEvent;
    private SearchManager mSearchManager;
    private IBinder mShareableActivityToken;
    private boolean mShouldDockBigOverlays;
    private SplashScreen mSplashScreen;
    boolean mStartedActivity;
    boolean mStopped;
    private CharSequence mTitle;
    private IBinder mToken;
    private TranslucentConversionListener mTranslucentCallback;
    private Thread mUiThread;
    private UiTranslationController mUiTranslationController;
    private IVoiceInteractionManagerService mVoiceInteractionManagerService;
    VoiceInteractor mVoiceInteractor;
    private Window mWindow;
    private WindowManager mWindowManager;
    private boolean mDoReportFullyDrawn = true;
    private boolean mCanEnterPictureInPicture = false;
    boolean mChangingConfigurations = false;
    Configuration mCurrentConfig = Configuration.EMPTY;
    private final ArrayList<Application.ActivityLifecycleCallbacks> mActivityLifecycleCallbacks = new ArrayList<>();
    View mDecor = null;
    boolean mWindowAdded = false;
    boolean mVisibleFromServer = false;
    boolean mVisibleFromClient = true;
    ActionBar mActionBar = null;
    private int mTitleColor = 0;
    final Handler mHandler = new Handler();
    final FragmentController mFragments = FragmentController.createController(new HostCallbacks());
    private final ArrayList<ManagedCursor> mManagedCursors = new ArrayList<>();
    int mResultCode = 0;
    Intent mResultData = null;
    private boolean mTitleReady = false;
    private int mActionModeTypeStarting = 0;
    private int mDefaultKeyMode = 0;
    private SpannableStringBuilder mDefaultKeySsb = null;
    private ActivityManager.TaskDescription mTaskDescription = new ActivityManager.TaskDescription();
    private final Object mInstanceTracker = StrictMode.trackActivity(this);
    ActivityTransitionState mActivityTransitionState = new ActivityTransitionState();
    SharedElementCallback mEnterTransitionListener = SharedElementCallback.NULL_CALLBACK;
    SharedElementCallback mExitTransitionListener = SharedElementCallback.NULL_CALLBACK;
    private final Window.WindowControllerCallback mWindowControllerCallback = new Window.WindowControllerCallback() { // from class: android.app.Activity.1
        @Override // android.view.Window.WindowControllerCallback
        public void toggleFreeformWindowingMode() {
            ActivityClient.getInstance().toggleFreeformWindowingMode(Activity.this.mToken);
        }

        @Override // android.view.Window.WindowControllerCallback
        public void enterPictureInPictureModeIfPossible() {
            if (Activity.this.mActivityInfo.supportsPictureInPicture()) {
                Activity.this.enterPictureInPictureMode();
            }
        }

        @Override // android.view.Window.WindowControllerCallback
        public boolean isTaskRoot() {
            return ActivityClient.getInstance().getTaskForActivity(Activity.this.mToken, true) >= 0;
        }

        @Override // android.view.Window.WindowControllerCallback
        public void updateStatusBarColor(int color) {
            Activity.this.mTaskDescription.setStatusBarColor(color);
            Activity activity = Activity.this;
            activity.setTaskDescription(activity.mTaskDescription);
        }

        @Override // android.view.Window.WindowControllerCallback
        public void updateNavigationBarColor(int color) {
            Activity.this.mTaskDescription.setNavigationBarColor(color);
            Activity activity = Activity.this;
            activity.setTaskDescription(activity.mTaskDescription);
        }
    };

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    @interface ContentCaptureNotificationType {
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    @interface DefaultKeyMode {
    }

    /* loaded from: classes.dex */
    public @interface FullscreenModeRequest {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class NonConfigurationInstances {
        Object activity;
        HashMap<String, Object> children;
        FragmentManagerNonConfig fragments;
        ArrayMap<String, LoaderManager> loaders;
        VoiceInteractor voiceInteractor;
    }

    /* loaded from: classes.dex */
    public @interface OverrideTransition {
    }

    /* loaded from: classes.dex */
    public interface ScreenCaptureCallback {
        void onScreenCaptured();
    }

    @SystemApi
    /* loaded from: classes.dex */
    public interface TranslucentConversionListener {
        void onTranslucentConversionComplete(boolean z);
    }

    private static native String getDlWarning();

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class ManagedDialog {
        Bundle mArgs;
        Dialog mDialog;

        private ManagedDialog() {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static final class ManagedCursor {
        private final Cursor mCursor;
        private boolean mReleased = false;
        private boolean mUpdated = false;

        ManagedCursor(Cursor cursor) {
            this.mCursor = cursor;
        }
    }

    public Intent getIntent() {
        return this.mIntent;
    }

    public void setIntent(Intent newIntent) {
        this.mIntent = newIntent;
    }

    public void setLocusContext(LocusId locusId, Bundle bundle) {
        try {
            ActivityManager.getService().setActivityLocusContext(this.mComponent, locusId, this.mToken);
        } catch (RemoteException re) {
            re.rethrowFromSystemServer();
        }
        if (locusId != null) {
            setLocusContextToContentCapture(locusId, bundle);
        }
    }

    public final Application getApplication() {
        return this.mApplication;
    }

    public final boolean isChild() {
        return this.mParent != null;
    }

    public final Activity getParent() {
        return this.mParent;
    }

    public WindowManager getWindowManager() {
        return this.mWindowManager;
    }

    public Window getWindow() {
        return this.mWindow;
    }

    @Deprecated
    public LoaderManager getLoaderManager() {
        return this.mFragments.getLoaderManager();
    }

    public View getCurrentFocus() {
        Window window = this.mWindow;
        if (window != null) {
            return window.getCurrentFocus();
        }
        return null;
    }

    private ContentCaptureManager getContentCaptureManager() {
        if (UserHandle.isApp(Process.myUid())) {
            if (this.mContentCaptureManager == null) {
                this.mContentCaptureManager = (ContentCaptureManager) getSystemService(ContentCaptureManager.class);
            }
            return this.mContentCaptureManager;
        }
        return null;
    }

    private String getContentCaptureTypeAsString(int type) {
        switch (type) {
            case 1:
                return "START";
            case 2:
                return "RESUME";
            case 3:
                return "PAUSE";
            case 4:
                return "STOP";
            default:
                return "UNKNOW-" + type;
        }
    }

    private void notifyContentCaptureManagerIfNeeded(int type) {
        if (Trace.isTagEnabled(64L)) {
            Trace.traceBegin(64L, "notifyContentCapture(" + getContentCaptureTypeAsString(type) + ") for " + this.mComponent.toShortString());
        }
        try {
            ContentCaptureManager cm = getContentCaptureManager();
            if (cm == null) {
                return;
            }
            switch (type) {
                case 1:
                    Window window = getWindow();
                    if (window != null) {
                        cm.updateWindowAttributes(window.getAttributes());
                    }
                    cm.onActivityCreated(this.mToken, this.mShareableActivityToken, getComponentName());
                    break;
                case 2:
                    cm.onActivityResumed();
                    break;
                case 3:
                    cm.onActivityPaused();
                    break;
                case 4:
                    cm.onActivityDestroyed();
                    break;
                default:
                    Log.wtf(TAG, "Invalid @ContentCaptureNotificationType: " + type);
                    break;
            }
        } finally {
            Trace.traceEnd(64L);
        }
    }

    private void setLocusContextToContentCapture(LocusId locusId, Bundle bundle) {
        ContentCaptureManager cm = getContentCaptureManager();
        if (cm == null) {
            return;
        }
        ContentCaptureContext.Builder contentCaptureContextBuilder = new ContentCaptureContext.Builder(locusId);
        if (bundle != null) {
            contentCaptureContextBuilder.setExtras(bundle);
        }
        cm.getMainContentCaptureSession().setContentCaptureContext(contentCaptureContextBuilder.build());
    }

    @Override // android.view.ContextThemeWrapper, android.content.ContextWrapper
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        if (newBase != null) {
            newBase.setAutofillClient(getAutofillClient());
            newBase.setContentCaptureOptions(getContentCaptureOptions());
        }
    }

    @Override // android.content.ContextWrapper, android.content.Context
    public final AutofillManager.AutofillClient getAutofillClient() {
        return getAutofillClientController();
    }

    private AutofillClientController getAutofillClientController() {
        if (this.mAutofillClientController == null) {
            this.mAutofillClientController = new AutofillClientController(this);
        }
        return this.mAutofillClientController;
    }

    @Override // android.content.Context
    public final ContentCaptureManager.ContentCaptureClient getContentCaptureClient() {
        return this;
    }

    public void registerActivityLifecycleCallbacks(Application.ActivityLifecycleCallbacks callback) {
        synchronized (this.mActivityLifecycleCallbacks) {
            this.mActivityLifecycleCallbacks.add(callback);
        }
    }

    public void unregisterActivityLifecycleCallbacks(Application.ActivityLifecycleCallbacks callback) {
        synchronized (this.mActivityLifecycleCallbacks) {
            this.mActivityLifecycleCallbacks.remove(callback);
        }
    }

    @Override // android.content.ContextWrapper, android.content.Context
    public void registerComponentCallbacks(ComponentCallbacks callback) {
        if (CompatChanges.isChangeEnabled(Context.OVERRIDABLE_COMPONENT_CALLBACKS) && this.mCallbacksController == null) {
            this.mCallbacksController = new ComponentCallbacksController();
        }
        ComponentCallbacksController componentCallbacksController = this.mCallbacksController;
        if (componentCallbacksController != null) {
            componentCallbacksController.registerCallbacks(callback);
        } else {
            super.registerComponentCallbacks(callback);
        }
    }

    @Override // android.content.ContextWrapper, android.content.Context
    public void unregisterComponentCallbacks(ComponentCallbacks callback) {
        ComponentCallbacksController componentCallbacksController = this.mCallbacksController;
        if (componentCallbacksController != null) {
            componentCallbacksController.unregisterCallbacks(callback);
        } else {
            super.unregisterComponentCallbacks(callback);
        }
    }

    private void dispatchActivityPreCreated(Bundle savedInstanceState) {
        getApplication().dispatchActivityPreCreated(this, savedInstanceState);
        Object[] callbacks = collectActivityLifecycleCallbacks();
        if (callbacks != null) {
            for (Object obj : callbacks) {
                ((Application.ActivityLifecycleCallbacks) obj).onActivityPreCreated(this, savedInstanceState);
            }
        }
    }

    private void dispatchActivityCreated(Bundle savedInstanceState) {
        getApplication().dispatchActivityCreated(this, savedInstanceState);
        Object[] callbacks = collectActivityLifecycleCallbacks();
        if (callbacks != null) {
            for (Object obj : callbacks) {
                ((Application.ActivityLifecycleCallbacks) obj).onActivityCreated(this, savedInstanceState);
            }
        }
    }

    private void dispatchActivityPostCreated(Bundle savedInstanceState) {
        Object[] callbacks = collectActivityLifecycleCallbacks();
        if (callbacks != null) {
            for (Object obj : callbacks) {
                ((Application.ActivityLifecycleCallbacks) obj).onActivityPostCreated(this, savedInstanceState);
            }
        }
        getApplication().dispatchActivityPostCreated(this, savedInstanceState);
    }

    private void dispatchActivityPreStarted() {
        getApplication().dispatchActivityPreStarted(this);
        Object[] callbacks = collectActivityLifecycleCallbacks();
        if (callbacks != null) {
            for (Object obj : callbacks) {
                ((Application.ActivityLifecycleCallbacks) obj).onActivityPreStarted(this);
            }
        }
    }

    private void dispatchActivityStarted() {
        getApplication().dispatchActivityStarted(this);
        Object[] callbacks = collectActivityLifecycleCallbacks();
        if (callbacks != null) {
            for (Object obj : callbacks) {
                ((Application.ActivityLifecycleCallbacks) obj).onActivityStarted(this);
            }
        }
    }

    private void dispatchActivityPostStarted() {
        Object[] callbacks = collectActivityLifecycleCallbacks();
        if (callbacks != null) {
            for (Object obj : callbacks) {
                ((Application.ActivityLifecycleCallbacks) obj).onActivityPostStarted(this);
            }
        }
        getApplication().dispatchActivityPostStarted(this);
    }

    private void dispatchActivityPreResumed() {
        getApplication().dispatchActivityPreResumed(this);
        Object[] callbacks = collectActivityLifecycleCallbacks();
        if (callbacks != null) {
            for (Object obj : callbacks) {
                ((Application.ActivityLifecycleCallbacks) obj).onActivityPreResumed(this);
            }
        }
    }

    private void dispatchActivityResumed() {
        getApplication().dispatchActivityResumed(this);
        Object[] callbacks = collectActivityLifecycleCallbacks();
        if (callbacks != null) {
            for (Object obj : callbacks) {
                ((Application.ActivityLifecycleCallbacks) obj).onActivityResumed(this);
            }
        }
    }

    private void dispatchActivityPostResumed() {
        Object[] callbacks = collectActivityLifecycleCallbacks();
        if (callbacks != null) {
            for (Object obj : callbacks) {
                ((Application.ActivityLifecycleCallbacks) obj).onActivityPostResumed(this);
            }
        }
        getApplication().dispatchActivityPostResumed(this);
    }

    private void dispatchActivityPrePaused() {
        getApplication().dispatchActivityPrePaused(this);
        Object[] callbacks = collectActivityLifecycleCallbacks();
        if (callbacks != null) {
            for (int i = callbacks.length - 1; i >= 0; i--) {
                ((Application.ActivityLifecycleCallbacks) callbacks[i]).onActivityPrePaused(this);
            }
        }
    }

    private void dispatchActivityPaused() {
        Object[] callbacks = collectActivityLifecycleCallbacks();
        if (callbacks != null) {
            for (int i = callbacks.length - 1; i >= 0; i--) {
                ((Application.ActivityLifecycleCallbacks) callbacks[i]).onActivityPaused(this);
            }
        }
        getApplication().dispatchActivityPaused(this);
    }

    private void dispatchActivityPostPaused() {
        Object[] callbacks = collectActivityLifecycleCallbacks();
        if (callbacks != null) {
            for (int i = callbacks.length - 1; i >= 0; i--) {
                ((Application.ActivityLifecycleCallbacks) callbacks[i]).onActivityPostPaused(this);
            }
        }
        getApplication().dispatchActivityPostPaused(this);
    }

    private void dispatchActivityPreStopped() {
        getApplication().dispatchActivityPreStopped(this);
        Object[] callbacks = collectActivityLifecycleCallbacks();
        if (callbacks != null) {
            for (int i = callbacks.length - 1; i >= 0; i--) {
                ((Application.ActivityLifecycleCallbacks) callbacks[i]).onActivityPreStopped(this);
            }
        }
    }

    private void dispatchActivityStopped() {
        Object[] callbacks = collectActivityLifecycleCallbacks();
        if (callbacks != null) {
            for (int i = callbacks.length - 1; i >= 0; i--) {
                ((Application.ActivityLifecycleCallbacks) callbacks[i]).onActivityStopped(this);
            }
        }
        getApplication().dispatchActivityStopped(this);
    }

    private void dispatchActivityPostStopped() {
        Object[] callbacks = collectActivityLifecycleCallbacks();
        if (callbacks != null) {
            for (int i = callbacks.length - 1; i >= 0; i--) {
                ((Application.ActivityLifecycleCallbacks) callbacks[i]).onActivityPostStopped(this);
            }
        }
        getApplication().dispatchActivityPostStopped(this);
    }

    private void dispatchActivityPreSaveInstanceState(Bundle outState) {
        getApplication().dispatchActivityPreSaveInstanceState(this, outState);
        Object[] callbacks = collectActivityLifecycleCallbacks();
        if (callbacks != null) {
            for (int i = callbacks.length - 1; i >= 0; i--) {
                ((Application.ActivityLifecycleCallbacks) callbacks[i]).onActivityPreSaveInstanceState(this, outState);
            }
        }
    }

    private void dispatchActivitySaveInstanceState(Bundle outState) {
        Object[] callbacks = collectActivityLifecycleCallbacks();
        if (callbacks != null) {
            for (int i = callbacks.length - 1; i >= 0; i--) {
                ((Application.ActivityLifecycleCallbacks) callbacks[i]).onActivitySaveInstanceState(this, outState);
            }
        }
        getApplication().dispatchActivitySaveInstanceState(this, outState);
    }

    private void dispatchActivityPostSaveInstanceState(Bundle outState) {
        Object[] callbacks = collectActivityLifecycleCallbacks();
        if (callbacks != null) {
            for (int i = callbacks.length - 1; i >= 0; i--) {
                ((Application.ActivityLifecycleCallbacks) callbacks[i]).onActivityPostSaveInstanceState(this, outState);
            }
        }
        getApplication().dispatchActivityPostSaveInstanceState(this, outState);
    }

    private void dispatchActivityPreDestroyed() {
        getApplication().dispatchActivityPreDestroyed(this);
        Object[] callbacks = collectActivityLifecycleCallbacks();
        if (callbacks != null) {
            for (int i = callbacks.length - 1; i >= 0; i--) {
                ((Application.ActivityLifecycleCallbacks) callbacks[i]).onActivityPreDestroyed(this);
            }
        }
    }

    private void dispatchActivityDestroyed() {
        Object[] callbacks = collectActivityLifecycleCallbacks();
        if (callbacks != null) {
            for (int i = callbacks.length - 1; i >= 0; i--) {
                ((Application.ActivityLifecycleCallbacks) callbacks[i]).onActivityDestroyed(this);
            }
        }
        getApplication().dispatchActivityDestroyed(this);
    }

    private void dispatchActivityPostDestroyed() {
        Object[] callbacks = collectActivityLifecycleCallbacks();
        if (callbacks != null) {
            for (int i = callbacks.length - 1; i >= 0; i--) {
                ((Application.ActivityLifecycleCallbacks) callbacks[i]).onActivityPostDestroyed(this);
            }
        }
        getApplication().dispatchActivityPostDestroyed(this);
    }

    private void dispatchActivityConfigurationChanged() {
        if (getApplication() != null) {
            getApplication().dispatchActivityConfigurationChanged(this);
        }
        Object[] callbacks = collectActivityLifecycleCallbacks();
        if (callbacks != null) {
            for (Object obj : callbacks) {
                ((Application.ActivityLifecycleCallbacks) obj).onActivityConfigurationChanged(this);
            }
        }
    }

    private Object[] collectActivityLifecycleCallbacks() {
        Object[] callbacks = null;
        synchronized (this.mActivityLifecycleCallbacks) {
            if (this.mActivityLifecycleCallbacks.size() > 0) {
                callbacks = this.mActivityLifecycleCallbacks.toArray();
            }
        }
        return callbacks;
    }

    private void notifyVoiceInteractionManagerServiceActivityEvent(int type) {
        if (this.mVoiceInteractionManagerService == null) {
            IVoiceInteractionManagerService asInterface = IVoiceInteractionManagerService.Stub.asInterface(ServiceManager.getService(Context.VOICE_INTERACTION_MANAGER_SERVICE));
            this.mVoiceInteractionManagerService = asInterface;
            if (asInterface == null) {
                Log.m104w(TAG, "notifyVoiceInteractionManagerServiceActivityEvent: Can not get VoiceInteractionManagerService");
                return;
            }
        }
        try {
            this.mVoiceInteractionManagerService.notifyActivityEventChanged(this.mToken, type);
        } catch (RemoteException e) {
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onCreate(Bundle savedInstanceState) {
        NonConfigurationInstances nonConfigurationInstances = this.mLastNonConfigurationInstances;
        if (nonConfigurationInstances != null) {
            this.mFragments.restoreLoaderNonConfig(nonConfigurationInstances.loaders);
        }
        if (this.mActivityInfo.parentActivityName != null) {
            ActionBar actionBar = this.mActionBar;
            if (actionBar == null) {
                this.mEnableDefaultActionBarUp = true;
            } else {
                actionBar.setDefaultDisplayHomeAsUpEnabled(true);
            }
        }
        if (savedInstanceState != null) {
            getAutofillClientController().onActivityCreated(savedInstanceState);
            Parcelable p = savedInstanceState.getParcelable(FRAGMENTS_TAG);
            FragmentController fragmentController = this.mFragments;
            NonConfigurationInstances nonConfigurationInstances2 = this.mLastNonConfigurationInstances;
            fragmentController.restoreAllState(p, nonConfigurationInstances2 != null ? nonConfigurationInstances2.fragments : null);
        }
        this.mFragments.dispatchCreate();
        dispatchActivityCreated(savedInstanceState);
        VoiceInteractor voiceInteractor = this.mVoiceInteractor;
        if (voiceInteractor != null) {
            voiceInteractor.attachActivity(this);
        }
        this.mRestoredFromBundle = savedInstanceState != null;
        this.mCalled = true;
        boolean aheadOfTimeBack = WindowOnBackInvokedDispatcher.isOnBackInvokedCallbackEnabled(this);
        if (aheadOfTimeBack) {
            this.mDefaultBackCallback = new OnBackInvokedCallback() { // from class: android.app.Activity$$ExternalSyntheticLambda0
                @Override // android.window.OnBackInvokedCallback
                public final void onBackInvoked() {
                    Activity.this.onBackInvoked();
                }
            };
            getOnBackInvokedDispatcher().registerSystemOnBackInvokedCallback(this.mDefaultBackCallback);
        }
    }

    public final SplashScreen getSplashScreen() {
        return getOrCreateSplashScreen();
    }

    private SplashScreen getOrCreateSplashScreen() {
        SplashScreen splashScreen;
        synchronized (this) {
            if (this.mSplashScreen == null) {
                this.mSplashScreen = new SplashScreen.SplashScreenImpl(this);
            }
            splashScreen = this.mSplashScreen;
        }
        return splashScreen;
    }

    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        onCreate(savedInstanceState);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void performRestoreInstanceState(Bundle savedInstanceState) {
        onRestoreInstanceState(savedInstanceState);
        restoreManagedDialogs(savedInstanceState);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void performRestoreInstanceState(Bundle savedInstanceState, PersistableBundle persistentState) {
        onRestoreInstanceState(savedInstanceState, persistentState);
        if (savedInstanceState != null) {
            restoreManagedDialogs(savedInstanceState);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        Bundle windowState;
        if (this.mWindow != null && (windowState = savedInstanceState.getBundle(WINDOW_HIERARCHY_TAG)) != null) {
            this.mWindow.restoreHierarchyState(windowState);
        }
    }

    public void onRestoreInstanceState(Bundle savedInstanceState, PersistableBundle persistentState) {
        if (savedInstanceState != null) {
            onRestoreInstanceState(savedInstanceState);
        }
    }

    private void restoreManagedDialogs(Bundle savedInstanceState) {
        Bundle b = savedInstanceState.getBundle(SAVED_DIALOGS_TAG);
        if (b == null) {
            return;
        }
        int[] ids = b.getIntArray(SAVED_DIALOG_IDS_KEY);
        int numDialogs = ids.length;
        this.mManagedDialogs = new SparseArray<>(numDialogs);
        for (int i : ids) {
            Integer dialogId = Integer.valueOf(i);
            Bundle dialogState = b.getBundle(savedDialogKeyFor(dialogId.intValue()));
            if (dialogState != null) {
                ManagedDialog md = new ManagedDialog();
                md.mArgs = b.getBundle(savedDialogArgsKeyFor(dialogId.intValue()));
                md.mDialog = createDialog(dialogId, dialogState, md.mArgs);
                if (md.mDialog != null) {
                    this.mManagedDialogs.put(dialogId.intValue(), md);
                    onPrepareDialog(dialogId.intValue(), md.mDialog, md.mArgs);
                    md.mDialog.onRestoreInstanceState(dialogState);
                }
            }
        }
    }

    private Dialog createDialog(Integer dialogId, Bundle state, Bundle args) {
        Dialog dialog = onCreateDialog(dialogId.intValue(), args);
        if (dialog == null) {
            return null;
        }
        dialog.dispatchOnCreate(state);
        return dialog;
    }

    private static String savedDialogKeyFor(int key) {
        return SAVED_DIALOG_KEY_PREFIX + key;
    }

    private static String savedDialogArgsKeyFor(int key) {
        return SAVED_DIALOG_ARGS_KEY_PREFIX + key;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onPostCreate(Bundle savedInstanceState) {
        if (!isChild()) {
            this.mTitleReady = true;
            onTitleChanged(getTitle(), getTitleColor());
        }
        this.mCalled = true;
        notifyContentCaptureManagerIfNeeded(1);
        notifyVoiceInteractionManagerServiceActivityEvent(1);
    }

    public void onPostCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        onPostCreate(savedInstanceState);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onStart() {
        this.mCalled = true;
        this.mFragments.doLoaderStart();
        dispatchActivityStarted();
        getAutofillClientController().onActivityStarted();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onRestart() {
        this.mCalled = true;
    }

    @Deprecated
    public void onStateNotSaved() {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onResume() {
        dispatchActivityResumed();
        this.mActivityTransitionState.onResume(this);
        getAutofillClientController().onActivityResumed();
        notifyContentCaptureManagerIfNeeded(2);
        this.mCalled = true;
    }

    protected void onPostResume() {
        Window win = getWindow();
        if (win != null) {
            win.makeActive();
        }
        ActionBar actionBar = this.mActionBar;
        if (actionBar != null) {
            actionBar.setShowHideAnimationEnabled(true);
        }
        notifyVoiceInteractionManagerServiceActivityEvent(2);
        this.mCalled = true;
    }

    public void onTopResumedActivityChanged(boolean isTopResumedActivity) {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void performTopResumedActivityChanged(boolean isTopResumedActivity, String reason) {
        onTopResumedActivityChanged(isTopResumedActivity);
        if (isTopResumedActivity) {
            EventLogTags.writeWmOnTopResumedGainedCalled(this.mIdent, getComponentName().getClassName(), reason);
        } else {
            EventLogTags.writeWmOnTopResumedLostCalled(this.mIdent, getComponentName().getClassName(), reason);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setVoiceInteractor(IVoiceInteractor voiceInteractor) {
        VoiceInteractor.Request[] activeRequests;
        VoiceInteractor voiceInteractor2 = this.mVoiceInteractor;
        if (voiceInteractor2 != null) {
            VoiceInteractor.Request[] requests = voiceInteractor2.getActiveRequests();
            if (requests != null) {
                for (VoiceInteractor.Request activeRequest : this.mVoiceInteractor.getActiveRequests()) {
                    activeRequest.cancel();
                    activeRequest.clear();
                }
            }
        }
        if (voiceInteractor == null) {
            this.mVoiceInteractor = null;
        } else {
            this.mVoiceInteractor = new VoiceInteractor(voiceInteractor, this, this, Looper.myLooper());
        }
    }

    @Override // android.content.ContextWrapper, android.content.Context
    public int getNextAutofillId() {
        return getAutofillClientController().getNextAutofillId();
    }

    public boolean isVoiceInteraction() {
        return this.mVoiceInteractor != null;
    }

    public boolean isVoiceInteractionRoot() {
        return this.mVoiceInteractor != null && ActivityClient.getInstance().isRootVoiceInteraction(this.mToken);
    }

    public VoiceInteractor getVoiceInteractor() {
        return this.mVoiceInteractor;
    }

    public boolean isLocalVoiceInteractionSupported() {
        try {
            return ActivityTaskManager.getService().supportsLocalVoiceInteraction();
        } catch (RemoteException e) {
            return false;
        }
    }

    public void startLocalVoiceInteraction(Bundle privateOptions) {
        ActivityClient.getInstance().startLocalVoiceInteraction(this.mToken, privateOptions);
    }

    public void onLocalVoiceInteractionStarted() {
    }

    public void onLocalVoiceInteractionStopped() {
    }

    public void stopLocalVoiceInteraction() {
        ActivityClient.getInstance().stopLocalVoiceInteraction(this.mToken);
    }

    protected void onNewIntent(Intent intent) {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void performSaveInstanceState(Bundle outState) {
        dispatchActivityPreSaveInstanceState(outState);
        onSaveInstanceState(outState);
        saveManagedDialogs(outState);
        this.mActivityTransitionState.saveState(outState);
        storeHasCurrentPermissionRequest(outState);
        dispatchActivityPostSaveInstanceState(outState);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void performSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        dispatchActivityPreSaveInstanceState(outState);
        onSaveInstanceState(outState, outPersistentState);
        saveManagedDialogs(outState);
        storeHasCurrentPermissionRequest(outState);
        dispatchActivityPostSaveInstanceState(outState);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onSaveInstanceState(Bundle outState) {
        outState.putBundle(WINDOW_HIERARCHY_TAG, this.mWindow.saveHierarchyState());
        Parcelable p = this.mFragments.saveAllState();
        if (p != null) {
            outState.putParcelable(FRAGMENTS_TAG, p);
        }
        getAutofillClientController().onSaveInstanceState(outState);
        dispatchActivitySaveInstanceState(outState);
    }

    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        onSaveInstanceState(outState);
    }

    private void saveManagedDialogs(Bundle outState) {
        int numDialogs;
        SparseArray<ManagedDialog> sparseArray = this.mManagedDialogs;
        if (sparseArray == null || (numDialogs = sparseArray.size()) == 0) {
            return;
        }
        Bundle dialogState = new Bundle();
        int[] ids = new int[this.mManagedDialogs.size()];
        for (int i = 0; i < numDialogs; i++) {
            int key = this.mManagedDialogs.keyAt(i);
            ids[i] = key;
            ManagedDialog md = this.mManagedDialogs.valueAt(i);
            dialogState.putBundle(savedDialogKeyFor(key), md.mDialog.onSaveInstanceState());
            if (md.mArgs != null) {
                dialogState.putBundle(savedDialogArgsKeyFor(key), md.mArgs);
            }
        }
        dialogState.putIntArray(SAVED_DIALOG_IDS_KEY, ids);
        outState.putBundle(SAVED_DIALOGS_TAG, dialogState);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onPause() {
        dispatchActivityPaused();
        getAutofillClientController().onActivityPaused();
        notifyContentCaptureManagerIfNeeded(3);
        notifyVoiceInteractionManagerServiceActivityEvent(3);
        this.mCalled = true;
    }

    protected void onUserLeaveHint() {
    }

    @Deprecated
    public boolean onCreateThumbnail(Bitmap outBitmap, Canvas canvas) {
        return false;
    }

    public CharSequence onCreateDescription() {
        return null;
    }

    public void onProvideAssistData(Bundle data) {
    }

    public void onProvideAssistContent(AssistContent outContent) {
    }

    public void onGetDirectActions(CancellationSignal cancellationSignal, Consumer<List<DirectAction>> callback) {
        callback.accept(Collections.emptyList());
    }

    public void onPerformDirectAction(String actionId, Bundle arguments, CancellationSignal cancellationSignal, Consumer<Bundle> resultListener) {
    }

    public final void requestShowKeyboardShortcuts() {
        ComponentName sysuiComponent = ComponentName.unflattenFromString(getResources().getString(C4057R.string.config_systemUIServiceComponent));
        Intent intent = new Intent(Intent.ACTION_SHOW_KEYBOARD_SHORTCUTS);
        intent.setPackage(sysuiComponent.getPackageName());
        sendBroadcastAsUser(intent, Process.myUserHandle());
    }

    public final void dismissKeyboardShortcutsHelper() {
        ComponentName sysuiComponent = ComponentName.unflattenFromString(getResources().getString(C4057R.string.config_systemUIServiceComponent));
        Intent intent = new Intent(Intent.ACTION_DISMISS_KEYBOARD_SHORTCUTS);
        intent.setPackage(sysuiComponent.getPackageName());
        sendBroadcastAsUser(intent, Process.myUserHandle());
    }

    @Override // android.view.Window.Callback
    public void onProvideKeyboardShortcuts(List<KeyboardShortcutGroup> data, Menu menu, int deviceId) {
        if (menu == null) {
            return;
        }
        KeyboardShortcutGroup group = null;
        int menuSize = menu.size();
        for (int i = 0; i < menuSize; i++) {
            MenuItem item = menu.getItem(i);
            CharSequence title = item.getTitle();
            char alphaShortcut = item.getAlphabeticShortcut();
            int alphaModifiers = item.getAlphabeticModifiers();
            if (title != null && alphaShortcut != 0) {
                if (group == null) {
                    int resource = this.mApplication.getApplicationInfo().labelRes;
                    group = new KeyboardShortcutGroup(resource != 0 ? getString(resource) : null);
                }
                group.addItem(new KeyboardShortcutInfo(title, alphaShortcut, alphaModifiers));
            }
        }
        if (group != null) {
            data.add(group);
        }
    }

    public boolean showAssist(Bundle args) {
        return ActivityClient.getInstance().showAssistFromActivity(this.mToken, args);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onStop() {
        ActionBar actionBar = this.mActionBar;
        if (actionBar != null) {
            actionBar.setShowHideAnimationEnabled(false);
        }
        this.mActivityTransitionState.onStop(this);
        dispatchActivityStopped();
        this.mTranslucentCallback = null;
        this.mCalled = true;
        getAutofillClientController().onActivityStopped(this.mIntent, this.mChangingConfigurations);
        this.mEnterAnimationComplete = false;
        notifyVoiceInteractionManagerServiceActivityEvent(4);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onDestroy() {
        this.mCalled = true;
        getAutofillClientController().onActivityDestroyed();
        SparseArray<ManagedDialog> sparseArray = this.mManagedDialogs;
        if (sparseArray != null) {
            int numDialogs = sparseArray.size();
            for (int i = 0; i < numDialogs; i++) {
                ManagedDialog md = this.mManagedDialogs.valueAt(i);
                if (md.mDialog.isShowing()) {
                    md.mDialog.dismiss();
                }
            }
            this.mManagedDialogs = null;
        }
        synchronized (this.mManagedCursors) {
            int numCursors = this.mManagedCursors.size();
            for (int i2 = 0; i2 < numCursors; i2++) {
                ManagedCursor c = this.mManagedCursors.get(i2);
                if (c != null) {
                    c.mCursor.close();
                }
            }
            this.mManagedCursors.clear();
        }
        SearchManager searchManager = this.mSearchManager;
        if (searchManager != null) {
            searchManager.stopSearch();
        }
        ActionBar actionBar = this.mActionBar;
        if (actionBar != null) {
            actionBar.onDestroy();
        }
        dispatchActivityDestroyed();
        notifyContentCaptureManagerIfNeeded(4);
        UiTranslationController uiTranslationController = this.mUiTranslationController;
        if (uiTranslationController != null) {
            uiTranslationController.onActivityDestroyed();
        }
        if (this.mDefaultBackCallback != null) {
            getOnBackInvokedDispatcher().unregisterOnBackInvokedCallback(this.mDefaultBackCallback);
            this.mDefaultBackCallback = null;
        }
        ComponentCallbacksController componentCallbacksController = this.mCallbacksController;
        if (componentCallbacksController != null) {
            componentCallbacksController.clearCallbacks();
        }
    }

    public void reportFullyDrawn() {
        if (this.mDoReportFullyDrawn) {
            if (Trace.isTagEnabled(64L)) {
                Trace.traceBegin(64L, "reportFullyDrawn() for " + this.mComponent.toShortString());
            }
            this.mDoReportFullyDrawn = false;
            try {
                ActivityClient.getInstance().reportActivityFullyDrawn(this.mToken, this.mRestoredFromBundle);
                VMRuntime.getRuntime().notifyStartupCompleted();
            } finally {
                Trace.traceEnd(64L);
            }
        }
    }

    public void onMultiWindowModeChanged(boolean isInMultiWindowMode, Configuration newConfig) {
        onMultiWindowModeChanged(isInMultiWindowMode);
    }

    @Deprecated
    public void onMultiWindowModeChanged(boolean isInMultiWindowMode) {
    }

    public boolean isInMultiWindowMode() {
        return this.mIsInMultiWindowMode;
    }

    public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode, Configuration newConfig) {
        onPictureInPictureModeChanged(isInPictureInPictureMode);
    }

    public void onPictureInPictureUiStateChanged(PictureInPictureUiState pipState) {
    }

    @Deprecated
    public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode) {
    }

    public boolean isInPictureInPictureMode() {
        return this.mIsInPictureInPictureMode;
    }

    @Deprecated
    public void enterPictureInPictureMode() {
        enterPictureInPictureMode(new PictureInPictureParams.Builder().build());
    }

    public boolean enterPictureInPictureMode(PictureInPictureParams params) {
        if (!deviceSupportsPictureInPictureMode()) {
            return false;
        }
        if (params == null) {
            throw new IllegalArgumentException("Expected non-null picture-in-picture params");
        }
        if (!this.mCanEnterPictureInPicture) {
            throw new IllegalStateException("Activity must be resumed to enter picture-in-picture");
        }
        boolean enterPictureInPictureMode = ActivityClient.getInstance().enterPictureInPictureMode(this.mToken, params);
        this.mIsInPictureInPictureMode = enterPictureInPictureMode;
        return enterPictureInPictureMode;
    }

    public void setPictureInPictureParams(PictureInPictureParams params) {
        if (!deviceSupportsPictureInPictureMode()) {
            return;
        }
        if (params == null) {
            throw new IllegalArgumentException("Expected non-null picture-in-picture params");
        }
        ActivityClient.getInstance().setPictureInPictureParams(this.mToken, params);
    }

    public int getMaxNumPictureInPictureActions() {
        return ActivityTaskManager.getMaxNumPictureInPictureActions(this);
    }

    private boolean deviceSupportsPictureInPictureMode() {
        return getPackageManager().hasSystemFeature(PackageManager.FEATURE_PICTURE_IN_PICTURE);
    }

    public boolean onPictureInPictureRequested() {
        return false;
    }

    public void requestFullscreenMode(int request, OutcomeReceiver<Void, Throwable> approvalCallback) {
        FullscreenRequestHandler.requestFullscreenMode(request, approvalCallback, this.mCurrentConfig, getActivityToken());
    }

    public void setShouldDockBigOverlays(boolean shouldDockBigOverlays) {
        ActivityClient.getInstance().setShouldDockBigOverlays(this.mToken, shouldDockBigOverlays);
        this.mShouldDockBigOverlays = shouldDockBigOverlays;
    }

    public boolean shouldDockBigOverlays() {
        return this.mShouldDockBigOverlays;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void dispatchMovedToDisplay(int displayId, Configuration config) {
        updateDisplay(displayId);
        onMovedToDisplay(displayId, config);
    }

    public void onMovedToDisplay(int displayId, Configuration config) {
    }

    @Override // android.content.ComponentCallbacks
    public void onConfigurationChanged(Configuration newConfig) {
        this.mCalled = true;
        this.mFragments.dispatchConfigurationChanged(newConfig);
        Window window = this.mWindow;
        if (window != null) {
            window.onConfigurationChanged(newConfig);
        }
        ActionBar actionBar = this.mActionBar;
        if (actionBar != null) {
            actionBar.onConfigurationChanged(newConfig);
        }
        dispatchActivityConfigurationChanged();
        ComponentCallbacksController componentCallbacksController = this.mCallbacksController;
        if (componentCallbacksController != null) {
            componentCallbacksController.dispatchConfigurationChanged(newConfig);
        }
    }

    public int getChangingConfigurations() {
        return this.mConfigChangeFlags;
    }

    public Object getLastNonConfigurationInstance() {
        NonConfigurationInstances nonConfigurationInstances = this.mLastNonConfigurationInstances;
        if (nonConfigurationInstances != null) {
            return nonConfigurationInstances.activity;
        }
        return null;
    }

    public Object onRetainNonConfigurationInstance() {
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public HashMap<String, Object> getLastNonConfigurationChildInstances() {
        NonConfigurationInstances nonConfigurationInstances = this.mLastNonConfigurationInstances;
        if (nonConfigurationInstances != null) {
            return nonConfigurationInstances.children;
        }
        return null;
    }

    HashMap<String, Object> onRetainNonConfigurationChildInstances() {
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public NonConfigurationInstances retainNonConfigurationInstances() {
        Object activity = onRetainNonConfigurationInstance();
        HashMap<String, Object> children = onRetainNonConfigurationChildInstances();
        FragmentManagerNonConfig fragments = this.mFragments.retainNestedNonConfig();
        this.mFragments.doLoaderStart();
        this.mFragments.doLoaderStop(true);
        ArrayMap<String, LoaderManager> loaders = this.mFragments.retainLoaderNonConfig();
        if (activity == null && children == null && fragments == null && loaders == null && this.mVoiceInteractor == null) {
            return null;
        }
        NonConfigurationInstances nci = new NonConfigurationInstances();
        nci.activity = activity;
        nci.children = children;
        nci.fragments = fragments;
        nci.loaders = loaders;
        VoiceInteractor voiceInteractor = this.mVoiceInteractor;
        if (voiceInteractor != null) {
            voiceInteractor.retainInstance();
            nci.voiceInteractor = this.mVoiceInteractor;
        }
        return nci;
    }

    @Override // android.content.ComponentCallbacks
    public void onLowMemory() {
        this.mCalled = true;
        this.mFragments.dispatchLowMemory();
        ComponentCallbacksController componentCallbacksController = this.mCallbacksController;
        if (componentCallbacksController != null) {
            componentCallbacksController.dispatchLowMemory();
        }
    }

    @Override // android.content.ComponentCallbacks2
    public void onTrimMemory(int level) {
        this.mCalled = true;
        this.mFragments.dispatchTrimMemory(level);
        ComponentCallbacksController componentCallbacksController = this.mCallbacksController;
        if (componentCallbacksController != null) {
            componentCallbacksController.dispatchTrimMemory(level);
        }
    }

    @Deprecated
    public FragmentManager getFragmentManager() {
        return this.mFragments.getFragmentManager();
    }

    @Deprecated
    public void onAttachFragment(Fragment fragment) {
    }

    @Deprecated
    public final Cursor managedQuery(Uri uri, String[] projection, String selection, String sortOrder) {
        Cursor c = getContentResolver().query(uri, projection, selection, null, sortOrder);
        if (c != null) {
            startManagingCursor(c);
        }
        return c;
    }

    @Deprecated
    public final Cursor managedQuery(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor c = getContentResolver().query(uri, projection, selection, selectionArgs, sortOrder);
        if (c != null) {
            startManagingCursor(c);
        }
        return c;
    }

    @Deprecated
    public void startManagingCursor(Cursor c) {
        synchronized (this.mManagedCursors) {
            this.mManagedCursors.add(new ManagedCursor(c));
        }
    }

    @Deprecated
    public void stopManagingCursor(Cursor c) {
        synchronized (this.mManagedCursors) {
            int N = this.mManagedCursors.size();
            int i = 0;
            while (true) {
                if (i >= N) {
                    break;
                }
                ManagedCursor mc = this.mManagedCursors.get(i);
                if (mc.mCursor != c) {
                    i++;
                } else {
                    this.mManagedCursors.remove(i);
                    break;
                }
            }
        }
    }

    @Deprecated
    public void setPersistent(boolean isPersistent) {
    }

    public <T extends View> T findViewById(int id) {
        return (T) getWindow().findViewById(id);
    }

    public final <T extends View> T requireViewById(int id) {
        T view = (T) findViewById(id);
        if (view == null) {
            throw new IllegalArgumentException("ID does not reference a View inside this Activity");
        }
        return view;
    }

    public ActionBar getActionBar() {
        initWindowDecorActionBar();
        return this.mActionBar;
    }

    public void setActionBar(Toolbar toolbar) {
        ActionBar ab = getActionBar();
        if (ab instanceof WindowDecorActionBar) {
            throw new IllegalStateException("This Activity already has an action bar supplied by the window decor. Do not request Window.FEATURE_ACTION_BAR and set android:windowActionBar to false in your theme to use a Toolbar instead.");
        }
        this.mMenuInflater = null;
        if (ab != null) {
            ab.onDestroy();
        }
        if (toolbar != null) {
            ToolbarActionBar tbab = new ToolbarActionBar(toolbar, getTitle(), this);
            this.mActionBar = tbab;
            this.mWindow.setCallback(tbab.getWrappedWindowCallback());
        } else {
            this.mActionBar = null;
            this.mWindow.setCallback(this);
        }
        invalidateOptionsMenu();
    }

    private void initWindowDecorActionBar() {
        Window window = getWindow();
        window.getDecorView();
        if (isChild() || !window.hasFeature(8) || this.mActionBar != null) {
            return;
        }
        WindowDecorActionBar windowDecorActionBar = new WindowDecorActionBar(this);
        this.mActionBar = windowDecorActionBar;
        windowDecorActionBar.setDefaultDisplayHomeAsUpEnabled(this.mEnableDefaultActionBarUp);
        this.mWindow.setDefaultIcon(this.mActivityInfo.getIconResource());
        this.mWindow.setDefaultLogo(this.mActivityInfo.getLogoResource());
    }

    public void setContentView(int layoutResID) {
        getWindow().setContentView(layoutResID);
        initWindowDecorActionBar();
    }

    public void setContentView(View view) {
        getWindow().setContentView(view);
        initWindowDecorActionBar();
    }

    public void setContentView(View view, ViewGroup.LayoutParams params) {
        getWindow().setContentView(view, params);
        initWindowDecorActionBar();
    }

    public void addContentView(View view, ViewGroup.LayoutParams params) {
        getWindow().addContentView(view, params);
        initWindowDecorActionBar();
    }

    public TransitionManager getContentTransitionManager() {
        return getWindow().getTransitionManager();
    }

    public void setContentTransitionManager(TransitionManager tm) {
        getWindow().setTransitionManager(tm);
    }

    public Scene getContentScene() {
        return getWindow().getContentScene();
    }

    public void setFinishOnTouchOutside(boolean finish) {
        this.mWindow.setCloseOnTouchOutside(finish);
    }

    public final void setDefaultKeyMode(int mode) {
        this.mDefaultKeyMode = mode;
        switch (mode) {
            case 0:
            case 2:
                this.mDefaultKeySsb = null;
                return;
            case 1:
            case 3:
            case 4:
                SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
                this.mDefaultKeySsb = spannableStringBuilder;
                Selection.setSelection(spannableStringBuilder, 0);
                return;
            default:
                throw new IllegalArgumentException();
        }
    }

    @Override // android.view.KeyEvent.Callback
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        boolean handled;
        if (keyCode == 4) {
            if (getApplicationInfo().targetSdkVersion >= 5) {
                event.startTracking();
            } else {
                onBackPressed();
            }
            return true;
        }
        int i = this.mDefaultKeyMode;
        if (i == 0) {
            return false;
        }
        if (i == 2) {
            Window w = getWindow();
            return w.hasFeature(0) && w.performPanelShortcut(0, keyCode, event, 2);
        } else if (keyCode == 61) {
            return false;
        } else {
            boolean clearSpannable = false;
            if (event.getRepeatCount() != 0 || event.isSystem()) {
                clearSpannable = true;
                handled = false;
            } else {
                handled = TextKeyListener.getInstance().onKeyDown(null, this.mDefaultKeySsb, keyCode, event);
                if (handled && this.mDefaultKeySsb.length() > 0) {
                    String str = this.mDefaultKeySsb.toString();
                    clearSpannable = true;
                    switch (this.mDefaultKeyMode) {
                        case 1:
                            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse(WebView.SCHEME_TEL + str));
                            intent.addFlags(268435456);
                            startActivity(intent);
                            break;
                        case 3:
                            startSearch(str, false, null, false);
                            break;
                        case 4:
                            startSearch(str, false, null, true);
                            break;
                    }
                }
            }
            if (clearSpannable) {
                this.mDefaultKeySsb.clear();
                this.mDefaultKeySsb.clearSpans();
                Selection.setSelection(this.mDefaultKeySsb, 0);
            }
            return handled;
        }
    }

    @Override // android.view.KeyEvent.Callback
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        return false;
    }

    @Override // android.view.KeyEvent.Callback
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        int sdkVersion = getApplicationInfo().targetSdkVersion;
        if (sdkVersion >= 5 && keyCode == 4 && event.isTracking() && !event.isCanceled() && this.mDefaultBackCallback == null) {
            onBackPressed();
            return true;
        }
        return false;
    }

    @Override // android.view.KeyEvent.Callback
    public boolean onKeyMultiple(int keyCode, int repeatCount, KeyEvent event) {
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static final class RequestFinishCallback extends IRequestFinishCallback.Stub {
        private final WeakReference<Activity> mActivityRef;

        RequestFinishCallback(WeakReference<Activity> activityRef) {
            this.mActivityRef = activityRef;
        }

        @Override // android.app.IRequestFinishCallback
        public void requestFinish() {
            final Activity activity = this.mActivityRef.get();
            if (activity != null) {
                Handler handler = activity.mHandler;
                Objects.requireNonNull(activity);
                handler.post(new Runnable() { // from class: android.app.Activity$RequestFinishCallback$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        Activity.this.finishAfterTransition();
                    }
                });
            }
        }
    }

    @Deprecated
    public void onBackPressed() {
        ActionBar actionBar = this.mActionBar;
        if (actionBar != null && actionBar.collapseActionView()) {
            return;
        }
        FragmentManager fragmentManager = this.mFragments.getFragmentManager();
        if (!fragmentManager.isStateSaved() && fragmentManager.popBackStackImmediate()) {
            return;
        }
        onBackInvoked();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onBackInvoked() {
        ActivityClient.getInstance().onBackPressed(this.mToken, new RequestFinishCallback(new WeakReference(this)));
        if (isTaskRoot()) {
            getAutofillClientController().onActivityBackPressed(this.mIntent);
        }
    }

    public boolean onKeyShortcut(int keyCode, KeyEvent event) {
        ActionBar actionBar = getActionBar();
        return actionBar != null && actionBar.onKeyShortcut(keyCode, event);
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (this.mWindow.shouldCloseOnTouch(this, event)) {
            finish();
            return true;
        }
        return false;
    }

    public boolean onTrackballEvent(MotionEvent event) {
        return false;
    }

    public boolean onGenericMotionEvent(MotionEvent event) {
        return false;
    }

    public void onUserInteraction() {
    }

    @Override // android.view.Window.Callback
    public void onWindowAttributesChanged(WindowManager.LayoutParams params) {
        View decor;
        if (this.mParent == null && (decor = this.mDecor) != null && decor.getParent() != null) {
            getWindowManager().updateViewLayout(decor, params);
            ContentCaptureManager contentCaptureManager = this.mContentCaptureManager;
            if (contentCaptureManager != null) {
                contentCaptureManager.updateWindowAttributes(params);
            }
        }
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

    public boolean hasWindowFocus() {
        View d;
        Window w = getWindow();
        if (w != null && (d = w.getDecorView()) != null) {
            return d.hasWindowFocus();
        }
        return false;
    }

    @Override // android.view.Window.OnWindowDismissedCallback
    public void onWindowDismissed(boolean finishTask, boolean suppressWindowTransition) {
        finish(finishTask ? 2 : 0);
        if (suppressWindowTransition) {
            overridePendingTransition(0, 0);
        }
    }

    @Override // android.view.Window.Callback
    public boolean dispatchKeyEvent(KeyEvent event) {
        ActionBar actionBar;
        onUserInteraction();
        int keyCode = event.getKeyCode();
        if (keyCode == 82 && (actionBar = this.mActionBar) != null && actionBar.onMenuKeyEvent(event)) {
            return true;
        }
        Window win = getWindow();
        if (win.superDispatchKeyEvent(event)) {
            return true;
        }
        View decor = this.mDecor;
        if (decor == null) {
            decor = win.getDecorView();
        }
        return event.dispatch(this, decor != null ? decor.getKeyDispatcherState() : null, this);
    }

    @Override // android.view.Window.Callback
    public boolean dispatchKeyShortcutEvent(KeyEvent event) {
        onUserInteraction();
        if (getWindow().superDispatchKeyShortcutEvent(event)) {
            return true;
        }
        return onKeyShortcut(event.getKeyCode(), event);
    }

    @Override // android.view.Window.Callback
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == 0) {
            onUserInteraction();
        }
        if (getWindow().superDispatchTouchEvent(ev)) {
            return true;
        }
        return onTouchEvent(ev);
    }

    @Override // android.view.Window.Callback
    public boolean dispatchTrackballEvent(MotionEvent ev) {
        onUserInteraction();
        if (getWindow().superDispatchTrackballEvent(ev)) {
            return true;
        }
        return onTrackballEvent(ev);
    }

    @Override // android.view.Window.Callback
    public boolean dispatchGenericMotionEvent(MotionEvent ev) {
        onUserInteraction();
        if (getWindow().superDispatchGenericMotionEvent(ev)) {
            return true;
        }
        return onGenericMotionEvent(ev);
    }

    @Override // android.view.Window.Callback
    public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent event) {
        event.setClassName(getClass().getName());
        event.setPackageName(getPackageName());
        ViewGroup.LayoutParams params = getWindow().getAttributes();
        boolean isFullScreen = params.width == -1 && params.height == -1;
        event.setFullScreen(isFullScreen);
        CharSequence title = getTitle();
        if (!TextUtils.isEmpty(title)) {
            event.getText().add(title);
        }
        return true;
    }

    @Override // android.view.Window.Callback
    public View onCreatePanelView(int featureId) {
        return null;
    }

    @Override // android.view.Window.Callback
    public boolean onCreatePanelMenu(int featureId, Menu menu) {
        if (featureId == 0) {
            boolean show = onCreateOptionsMenu(menu);
            return show | this.mFragments.dispatchCreateOptionsMenu(menu, getMenuInflater());
        }
        return false;
    }

    @Override // android.view.Window.Callback
    public boolean onPreparePanel(int featureId, View view, Menu menu) {
        if (featureId == 0) {
            boolean goforit = onPrepareOptionsMenu(menu);
            return goforit | this.mFragments.dispatchPrepareOptionsMenu(menu);
        }
        return true;
    }

    @Override // android.view.Window.Callback
    public boolean onMenuOpened(int featureId, Menu menu) {
        if (featureId == 8) {
            initWindowDecorActionBar();
            ActionBar actionBar = this.mActionBar;
            if (actionBar != null) {
                actionBar.dispatchMenuVisibilityChanged(true);
            } else {
                Log.m110e(TAG, "Tried to open action bar menu with no action bar");
            }
        }
        return true;
    }

    @Override // android.view.Window.Callback
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        ActionBar actionBar;
        CharSequence titleCondensed = item.getTitleCondensed();
        switch (featureId) {
            case 0:
                if (titleCondensed != null) {
                    EventLog.writeEvent(50000, 0, titleCondensed.toString());
                }
                if (onOptionsItemSelected(item) || this.mFragments.dispatchOptionsItemSelected(item)) {
                    return true;
                }
                if (item.getItemId() != 16908332 || (actionBar = this.mActionBar) == null || (actionBar.getDisplayOptions() & 4) == 0) {
                    return false;
                }
                Activity activity = this.mParent;
                if (activity == null) {
                    return onNavigateUp();
                }
                return activity.onNavigateUpFromChild(this);
            case 6:
                if (titleCondensed != null) {
                    EventLog.writeEvent(50000, 1, titleCondensed.toString());
                }
                if (onContextItemSelected(item)) {
                    return true;
                }
                return this.mFragments.dispatchContextItemSelected(item);
            default:
                return false;
        }
    }

    @Override // android.view.Window.Callback
    public void onPanelClosed(int featureId, Menu menu) {
        switch (featureId) {
            case 0:
                this.mFragments.dispatchOptionsMenuClosed(menu);
                onOptionsMenuClosed(menu);
                return;
            case 6:
                onContextMenuClosed(menu);
                return;
            case 8:
                initWindowDecorActionBar();
                this.mActionBar.dispatchMenuVisibilityChanged(false);
                return;
            default:
                return;
        }
    }

    public void invalidateOptionsMenu() {
        if (this.mWindow.hasFeature(0)) {
            ActionBar actionBar = this.mActionBar;
            if (actionBar == null || !actionBar.invalidateOptionsMenu()) {
                this.mWindow.invalidatePanelMenu(0);
            }
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        Activity activity = this.mParent;
        if (activity != null) {
            return activity.onCreateOptionsMenu(menu);
        }
        return true;
    }

    public boolean onPrepareOptionsMenu(Menu menu) {
        Activity activity = this.mParent;
        if (activity != null) {
            return activity.onPrepareOptionsMenu(menu);
        }
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        Activity activity = this.mParent;
        if (activity != null) {
            return activity.onOptionsItemSelected(item);
        }
        return false;
    }

    public boolean onNavigateUp() {
        Intent upIntent = getParentActivityIntent();
        if (upIntent != null) {
            if (this.mActivityInfo.taskAffinity == null) {
                finish();
                return true;
            } else if (shouldUpRecreateTask(upIntent)) {
                TaskStackBuilder b = TaskStackBuilder.create(this);
                onCreateNavigateUpTaskStack(b);
                onPrepareNavigateUpTaskStack(b);
                b.startActivities();
                if (this.mResultCode != 0 || this.mResultData != null) {
                    Log.m108i(TAG, "onNavigateUp only finishing topmost activity to return a result");
                    finish();
                    return true;
                }
                finishAffinity();
                return true;
            } else {
                navigateUpTo(upIntent);
                return true;
            }
        }
        return false;
    }

    @Deprecated
    public boolean onNavigateUpFromChild(Activity child) {
        return onNavigateUp();
    }

    public void onCreateNavigateUpTaskStack(TaskStackBuilder builder) {
        builder.addParentStack(this);
    }

    public void onPrepareNavigateUpTaskStack(TaskStackBuilder builder) {
    }

    public void onOptionsMenuClosed(Menu menu) {
        Activity activity = this.mParent;
        if (activity != null) {
            activity.onOptionsMenuClosed(menu);
        }
    }

    public void openOptionsMenu() {
        if (this.mWindow.hasFeature(0)) {
            ActionBar actionBar = this.mActionBar;
            if (actionBar == null || !actionBar.openOptionsMenu()) {
                this.mWindow.openPanel(0, null);
            }
        }
    }

    public void closeOptionsMenu() {
        if (this.mWindow.hasFeature(0)) {
            ActionBar actionBar = this.mActionBar;
            if (actionBar == null || !actionBar.closeOptionsMenu()) {
                this.mWindow.closePanel(0);
            }
        }
    }

    @Override // android.view.View.OnCreateContextMenuListener
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
    }

    public void registerForContextMenu(View view) {
        view.setOnCreateContextMenuListener(this);
    }

    public void unregisterForContextMenu(View view) {
        view.setOnCreateContextMenuListener(null);
    }

    public void openContextMenu(View view) {
        view.showContextMenu();
    }

    public void closeContextMenu() {
        if (this.mWindow.hasFeature(6)) {
            this.mWindow.closePanel(6);
        }
    }

    public boolean onContextItemSelected(MenuItem item) {
        Activity activity = this.mParent;
        if (activity != null) {
            return activity.onContextItemSelected(item);
        }
        return false;
    }

    public void onContextMenuClosed(Menu menu) {
        Activity activity = this.mParent;
        if (activity != null) {
            activity.onContextMenuClosed(menu);
        }
    }

    @Deprecated
    protected Dialog onCreateDialog(int id) {
        return null;
    }

    @Deprecated
    protected Dialog onCreateDialog(int id, Bundle args) {
        return onCreateDialog(id);
    }

    @Deprecated
    protected void onPrepareDialog(int id, Dialog dialog) {
        dialog.setOwnerActivity(this);
    }

    @Deprecated
    protected void onPrepareDialog(int id, Dialog dialog, Bundle args) {
        onPrepareDialog(id, dialog);
    }

    @Deprecated
    public final void showDialog(int id) {
        showDialog(id, null);
    }

    @Deprecated
    public final boolean showDialog(int id, Bundle args) {
        if (this.mManagedDialogs == null) {
            this.mManagedDialogs = new SparseArray<>();
        }
        ManagedDialog md = this.mManagedDialogs.get(id);
        if (md == null) {
            md = new ManagedDialog();
            md.mDialog = createDialog(Integer.valueOf(id), null, args);
            if (md.mDialog == null) {
                return false;
            }
            this.mManagedDialogs.put(id, md);
        }
        md.mArgs = args;
        onPrepareDialog(id, md.mDialog, args);
        md.mDialog.show();
        return true;
    }

    @Deprecated
    public final void dismissDialog(int id) {
        SparseArray<ManagedDialog> sparseArray = this.mManagedDialogs;
        if (sparseArray == null) {
            throw missingDialog(id);
        }
        ManagedDialog md = sparseArray.get(id);
        if (md == null) {
            throw missingDialog(id);
        }
        md.mDialog.dismiss();
    }

    private IllegalArgumentException missingDialog(int id) {
        return new IllegalArgumentException("no dialog with id " + id + " was ever shown via Activity#showDialog");
    }

    @Deprecated
    public final void removeDialog(int id) {
        ManagedDialog md;
        SparseArray<ManagedDialog> sparseArray = this.mManagedDialogs;
        if (sparseArray != null && (md = sparseArray.get(id)) != null) {
            md.mDialog.dismiss();
            this.mManagedDialogs.remove(id);
        }
    }

    @Override // android.view.Window.Callback
    public boolean onSearchRequested(SearchEvent searchEvent) {
        this.mSearchEvent = searchEvent;
        boolean result = onSearchRequested();
        this.mSearchEvent = null;
        return result;
    }

    @Override // android.view.Window.Callback
    public boolean onSearchRequested() {
        int uiMode = getResources().getConfiguration().uiMode & 15;
        if (uiMode == 4 || uiMode == 6) {
            return false;
        }
        startSearch(null, false, null, false);
        return true;
    }

    public final SearchEvent getSearchEvent() {
        return this.mSearchEvent;
    }

    public void startSearch(String initialQuery, boolean selectInitialQuery, Bundle appSearchData, boolean globalSearch) {
        ensureSearchManager();
        this.mSearchManager.startSearch(initialQuery, selectInitialQuery, getComponentName(), appSearchData, globalSearch);
    }

    public void triggerSearch(String query, Bundle appSearchData) {
        ensureSearchManager();
        this.mSearchManager.triggerSearch(query, getComponentName(), appSearchData);
    }

    public void takeKeyEvents(boolean get) {
        getWindow().takeKeyEvents(get);
    }

    public final boolean requestWindowFeature(int featureId) {
        return getWindow().requestFeature(featureId);
    }

    public final void setFeatureDrawableResource(int featureId, int resId) {
        getWindow().setFeatureDrawableResource(featureId, resId);
    }

    public final void setFeatureDrawableUri(int featureId, Uri uri) {
        getWindow().setFeatureDrawableUri(featureId, uri);
    }

    public final void setFeatureDrawable(int featureId, Drawable drawable) {
        getWindow().setFeatureDrawable(featureId, drawable);
    }

    public final void setFeatureDrawableAlpha(int featureId, int alpha) {
        getWindow().setFeatureDrawableAlpha(featureId, alpha);
    }

    public LayoutInflater getLayoutInflater() {
        return getWindow().getLayoutInflater();
    }

    public MenuInflater getMenuInflater() {
        if (this.mMenuInflater == null) {
            initWindowDecorActionBar();
            if (this.mActionBar != null) {
                this.mMenuInflater = new MenuInflater(this.mActionBar.getThemedContext(), this);
            } else {
                this.mMenuInflater = new MenuInflater(this);
            }
        }
        return this.mMenuInflater;
    }

    @Override // android.view.ContextThemeWrapper, android.content.ContextWrapper, android.content.Context
    public void setTheme(int resid) {
        super.setTheme(resid);
        this.mWindow.setTheme(resid);
    }

    @Override // android.view.ContextThemeWrapper
    protected void onApplyThemeResource(Resources.Theme theme, int resid, boolean first) {
        int colorPrimary;
        Activity activity = this.mParent;
        if (activity == null) {
            super.onApplyThemeResource(theme, resid, first);
        } else {
            try {
                theme.setTo(activity.getTheme());
            } catch (Exception e) {
            }
            theme.applyStyle(resid, false);
        }
        TypedArray a = theme.obtainStyledAttributes(C4057R.styleable.ActivityTaskDescription);
        if (this.mTaskDescription.getPrimaryColor() == 0 && (colorPrimary = a.getColor(1, 0)) != 0 && Color.alpha(colorPrimary) == 255) {
            this.mTaskDescription.setPrimaryColor(colorPrimary);
        }
        int colorBackground = a.getColor(0, 0);
        if (colorBackground != 0 && Color.alpha(colorBackground) == 255) {
            this.mTaskDescription.setBackgroundColor(colorBackground);
        }
        int colorBackgroundFloating = a.getColor(4, 0);
        if (colorBackgroundFloating != 0 && Color.alpha(colorBackgroundFloating) == 255) {
            this.mTaskDescription.setBackgroundColorFloating(colorBackgroundFloating);
        }
        int statusBarColor = a.getColor(2, 0);
        if (statusBarColor != 0) {
            this.mTaskDescription.setStatusBarColor(statusBarColor);
        }
        int navigationBarColor = a.getColor(3, 0);
        if (navigationBarColor != 0) {
            this.mTaskDescription.setNavigationBarColor(navigationBarColor);
        }
        int targetSdk = getApplicationInfo().targetSdkVersion;
        boolean targetPreQ = targetSdk < 29;
        if (!targetPreQ) {
            this.mTaskDescription.setEnsureStatusBarContrastWhenTransparent(a.getBoolean(5, false));
            this.mTaskDescription.setEnsureNavigationBarContrastWhenTransparent(a.getBoolean(6, true));
        }
        a.recycle();
        setTaskDescription(this.mTaskDescription);
    }

    public final void requestPermissions(String[] permissions, int requestCode) {
        if (requestCode < 0) {
            throw new IllegalArgumentException("requestCode should be >= 0");
        }
        if (this.mHasCurrentPermissionsRequest) {
            Log.m104w(TAG, "Can request only one set of permissions at a time");
            onRequestPermissionsResult(requestCode, new String[0], new int[0]);
            return;
        }
        if (!getAttributionSource().getRenouncedPermissions().isEmpty()) {
            int permissionCount = permissions.length;
            for (int i = 0; i < permissionCount; i++) {
                if (getAttributionSource().getRenouncedPermissions().contains(permissions[i])) {
                    throw new IllegalArgumentException("Cannot request renounced permission: " + permissions[i]);
                }
            }
        }
        Intent intent = getPackageManager().buildRequestPermissionsIntent(permissions);
        startActivityForResult(REQUEST_PERMISSIONS_WHO_PREFIX, intent, requestCode, null);
        this.mHasCurrentPermissionsRequest = true;
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
    }

    public boolean shouldShowRequestPermissionRationale(String permission) {
        return getPackageManager().shouldShowRequestPermissionRationale(permission);
    }

    public void startActivityForResult(Intent intent, int requestCode) {
        startActivityForResult(intent, requestCode, null);
    }

    public void startActivityForResult(Intent intent, int requestCode, Bundle options) {
        Activity activity = this.mParent;
        if (activity == null) {
            Bundle options2 = transferSpringboardActivityOptions(options);
            Instrumentation.ActivityResult ar = this.mInstrumentation.execStartActivity(this, this.mMainThread.getApplicationThread(), this.mToken, this, intent, requestCode, options2);
            if (ar != null) {
                this.mMainThread.sendActivityResult(this.mToken, this.mEmbeddedID, requestCode, ar.getResultCode(), ar.getResultData());
            }
            if (requestCode >= 0) {
                this.mStartedActivity = true;
            }
            cancelInputsAndStartExitTransition(options2);
        } else if (options != null) {
            activity.startActivityFromChild(this, intent, requestCode, options);
        } else {
            activity.startActivityFromChild(this, intent, requestCode);
        }
    }

    private void cancelInputsAndStartExitTransition(Bundle options) {
        Window window = this.mWindow;
        View decor = window != null ? window.peekDecorView() : null;
        if (decor != null) {
            decor.cancelPendingInputEvents();
        }
        if (options != null) {
            this.mActivityTransitionState.startExitOutTransition(this, options);
        }
    }

    public boolean isActivityTransitionRunning() {
        return this.mActivityTransitionState.isTransitionRunning();
    }

    private Bundle transferSpringboardActivityOptions(Bundle options) {
        Window window;
        ActivityOptions activityOptions;
        if (options == null && (window = this.mWindow) != null && !window.isActive() && (activityOptions = getActivityOptions()) != null && activityOptions.getAnimationType() == 5) {
            return activityOptions.toBundle();
        }
        return options;
    }

    @SystemApi
    public void startActivityForResultAsUser(Intent intent, int requestCode, UserHandle user) {
        startActivityForResultAsUser(intent, requestCode, null, user);
    }

    @SystemApi
    public void startActivityForResultAsUser(Intent intent, int requestCode, Bundle options, UserHandle user) {
        startActivityForResultAsUser(intent, this.mEmbeddedID, requestCode, options, user);
    }

    @SystemApi
    public void startActivityForResultAsUser(Intent intent, String resultWho, int requestCode, Bundle options, UserHandle user) {
        if (this.mParent != null) {
            throw new RuntimeException("Can't be called from a child");
        }
        Bundle options2 = transferSpringboardActivityOptions(options);
        Instrumentation.ActivityResult ar = this.mInstrumentation.execStartActivity(this, this.mMainThread.getApplicationThread(), this.mToken, resultWho, intent, requestCode, options2, user);
        if (ar != null) {
            this.mMainThread.sendActivityResult(this.mToken, this.mEmbeddedID, requestCode, ar.getResultCode(), ar.getResultData());
        }
        if (requestCode >= 0) {
            this.mStartedActivity = true;
        }
        cancelInputsAndStartExitTransition(options2);
    }

    @Override // android.content.ContextWrapper, android.content.Context
    public void startActivityAsUser(Intent intent, UserHandle user) {
        startActivityAsUser(intent, null, user);
    }

    @Override // android.content.ContextWrapper, android.content.Context
    public void startActivityAsUser(Intent intent, Bundle options, UserHandle user) {
        if (this.mParent != null) {
            throw new RuntimeException("Can't be called from a child");
        }
        Bundle options2 = transferSpringboardActivityOptions(options);
        Instrumentation.ActivityResult ar = this.mInstrumentation.execStartActivity(this, this.mMainThread.getApplicationThread(), this.mToken, this.mEmbeddedID, intent, -1, options2, user);
        if (ar != null) {
            this.mMainThread.sendActivityResult(this.mToken, this.mEmbeddedID, -1, ar.getResultCode(), ar.getResultData());
        }
        cancelInputsAndStartExitTransition(options2);
    }

    public void startActivityAsCaller(Intent intent, Bundle options, boolean ignoreTargetSecurity, int userId) {
        startActivityAsCaller(intent, options, ignoreTargetSecurity, userId, -1);
    }

    public void startActivityAsCaller(Intent intent, Bundle options, boolean ignoreTargetSecurity, int userId, int requestCode) {
        if (this.mParent != null) {
            throw new RuntimeException("Can't be called from a child");
        }
        Bundle options2 = transferSpringboardActivityOptions(options);
        Instrumentation.ActivityResult ar = this.mInstrumentation.execStartActivityAsCaller(this, this.mMainThread.getApplicationThread(), this.mToken, this, intent, requestCode, options2, ignoreTargetSecurity, userId);
        if (ar != null) {
            this.mMainThread.sendActivityResult(this.mToken, this.mEmbeddedID, requestCode, ar.getResultCode(), ar.getResultData());
        }
        cancelInputsAndStartExitTransition(options2);
    }

    public void startIntentSenderForResult(IntentSender intent, int requestCode, Intent fillInIntent, int flagsMask, int flagsValues, int extraFlags) throws IntentSender.SendIntentException {
        startIntentSenderForResult(intent, requestCode, fillInIntent, flagsMask, flagsValues, extraFlags, (Bundle) null);
    }

    public void startIntentSenderForResult(IntentSender intent, String who, int requestCode, Intent fillInIntent, int flagsMask, int flagsValues, Bundle options) throws IntentSender.SendIntentException {
        startIntentSenderForResultInner(intent, who, requestCode, fillInIntent, flagsMask, flagsValues, options);
    }

    public void startIntentSenderForResult(IntentSender intent, int requestCode, Intent fillInIntent, int flagsMask, int flagsValues, int extraFlags, Bundle options) throws IntentSender.SendIntentException {
        Activity activity = this.mParent;
        if (activity == null) {
            startIntentSenderForResultInner(intent, this.mEmbeddedID, requestCode, fillInIntent, flagsMask, flagsValues, options);
        } else if (options != null) {
            activity.startIntentSenderFromChild(this, intent, requestCode, fillInIntent, flagsMask, flagsValues, extraFlags, options);
        } else {
            activity.startIntentSenderFromChild(this, intent, requestCode, fillInIntent, flagsMask, flagsValues, extraFlags);
        }
    }

    public void startIntentSenderForResultInner(IntentSender intent, String who, int requestCode, Intent fillInIntent, int flagsMask, int flagsValues, Bundle options) throws IntentSender.SendIntentException {
        Bundle options2;
        int result;
        try {
            options2 = transferSpringboardActivityOptions(options);
            String resolvedType = null;
            if (fillInIntent != null) {
                try {
                    fillInIntent.migrateExtraStreamToClipData(this);
                    fillInIntent.prepareToLeaveProcess(this);
                    resolvedType = fillInIntent.resolveTypeIfNeeded(getContentResolver());
                } catch (RemoteException e) {
                }
            }
            result = ActivityTaskManager.getService().startActivityIntentSender(this.mMainThread.getApplicationThread(), intent != null ? intent.getTarget() : null, intent != null ? intent.getWhitelistToken() : null, fillInIntent, resolvedType, this.mToken, who, requestCode, flagsMask, flagsValues, options2);
        } catch (RemoteException e2) {
        }
        if (result == -96) {
            throw new IntentSender.SendIntentException();
        }
        Instrumentation.checkStartActivityResult(result, null);
        if (options2 != null) {
            cancelInputsAndStartExitTransition(options2);
        }
        if (requestCode >= 0) {
            this.mStartedActivity = true;
        }
    }

    @Override // android.content.ContextWrapper, android.content.Context
    public void startActivity(Intent intent) {
        startActivity(intent, null);
    }

    @Override // android.content.ContextWrapper, android.content.Context
    public void startActivity(Intent intent, Bundle options) {
        getAutofillClientController().onStartActivity(intent, this.mIntent);
        if (options != null) {
            startActivityForResult(intent, -1, options);
        } else {
            startActivityForResult(intent, -1);
        }
    }

    @Override // android.content.ContextWrapper, android.content.Context
    public void startActivities(Intent[] intents) {
        startActivities(intents, null);
    }

    @Override // android.content.ContextWrapper, android.content.Context
    public void startActivities(Intent[] intents, Bundle options) {
        this.mInstrumentation.execStartActivities(this, this.mMainThread.getApplicationThread(), this.mToken, this, intents, options);
    }

    @Override // android.content.ContextWrapper, android.content.Context
    public void startIntentSender(IntentSender intent, Intent fillInIntent, int flagsMask, int flagsValues, int extraFlags) throws IntentSender.SendIntentException {
        startIntentSender(intent, fillInIntent, flagsMask, flagsValues, extraFlags, null);
    }

    @Override // android.content.ContextWrapper, android.content.Context
    public void startIntentSender(IntentSender intent, Intent fillInIntent, int flagsMask, int flagsValues, int extraFlags, Bundle options) throws IntentSender.SendIntentException {
        if (options != null) {
            startIntentSenderForResult(intent, -1, fillInIntent, flagsMask, flagsValues, extraFlags, options);
        } else {
            startIntentSenderForResult(intent, -1, fillInIntent, flagsMask, flagsValues, extraFlags);
        }
    }

    public boolean startActivityIfNeeded(Intent intent, int requestCode) {
        return startActivityIfNeeded(intent, requestCode, null);
    }

    public boolean startActivityIfNeeded(Intent intent, int requestCode, Bundle options) {
        if (this.mParent == null) {
            int result = 1;
            try {
                Uri referrer = onProvideReferrer();
                if (referrer != null) {
                    intent.putExtra(Intent.EXTRA_REFERRER, referrer);
                }
                intent.migrateExtraStreamToClipData(this);
                intent.prepareToLeaveProcess(this);
                result = ActivityTaskManager.getService().startActivity(this.mMainThread.getApplicationThread(), getOpPackageName(), getAttributionTag(), intent, intent.resolveTypeIfNeeded(getContentResolver()), this.mToken, this.mEmbeddedID, requestCode, 1, null, options);
            } catch (RemoteException e) {
            }
            Instrumentation.checkStartActivityResult(result, intent);
            if (requestCode >= 0) {
                this.mStartedActivity = true;
            }
            return result != 1;
        }
        throw new UnsupportedOperationException("startActivityIfNeeded can only be called from a top-level activity");
    }

    public boolean startNextMatchingActivity(Intent intent) {
        return startNextMatchingActivity(intent, null);
    }

    public boolean startNextMatchingActivity(Intent intent, Bundle options) {
        if (this.mParent == null) {
            try {
                intent.migrateExtraStreamToClipData(this);
                intent.prepareToLeaveProcess(this);
                return ActivityTaskManager.getService().startNextMatchingActivity(this.mToken, intent, options);
            } catch (RemoteException e) {
                return false;
            }
        }
        throw new UnsupportedOperationException("startNextMatchingActivity can only be called from a top-level activity");
    }

    @Deprecated
    public void startActivityFromChild(Activity child, Intent intent, int requestCode) {
        startActivityFromChild(child, intent, requestCode, null);
    }

    @Deprecated
    public void startActivityFromChild(Activity child, Intent intent, int requestCode, Bundle options) {
        Bundle options2 = transferSpringboardActivityOptions(options);
        Instrumentation.ActivityResult ar = this.mInstrumentation.execStartActivity(this, this.mMainThread.getApplicationThread(), this.mToken, child, intent, requestCode, options2);
        if (ar != null) {
            this.mMainThread.sendActivityResult(this.mToken, child.mEmbeddedID, requestCode, ar.getResultCode(), ar.getResultData());
        }
        cancelInputsAndStartExitTransition(options2);
    }

    @Deprecated
    public void startActivityFromFragment(Fragment fragment, Intent intent, int requestCode) {
        startActivityFromFragment(fragment, intent, requestCode, null);
    }

    @Deprecated
    public void startActivityFromFragment(Fragment fragment, Intent intent, int requestCode, Bundle options) {
        startActivityForResult(fragment.mWho, intent, requestCode, options);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startActivityAsUserFromFragment(Fragment fragment, Intent intent, int requestCode, Bundle options, UserHandle user) {
        startActivityForResultAsUser(intent, fragment.mWho, requestCode, options, user);
    }

    @Override // android.content.ContextWrapper, android.content.Context
    public void startActivityForResult(String who, Intent intent, int requestCode, Bundle options) {
        Uri referrer = onProvideReferrer();
        if (referrer != null) {
            intent.putExtra(Intent.EXTRA_REFERRER, referrer);
        }
        Bundle options2 = transferSpringboardActivityOptions(options);
        Instrumentation.ActivityResult ar = this.mInstrumentation.execStartActivity(this, this.mMainThread.getApplicationThread(), this.mToken, who, intent, requestCode, options2);
        if (ar != null) {
            this.mMainThread.sendActivityResult(this.mToken, who, requestCode, ar.getResultCode(), ar.getResultData());
        }
        cancelInputsAndStartExitTransition(options2);
    }

    @Override // android.content.ContextWrapper, android.content.Context
    public boolean canStartActivityForResult() {
        return true;
    }

    @Deprecated
    public void startIntentSenderFromChild(Activity child, IntentSender intent, int requestCode, Intent fillInIntent, int flagsMask, int flagsValues, int extraFlags) throws IntentSender.SendIntentException {
        startIntentSenderFromChild(child, intent, requestCode, fillInIntent, flagsMask, flagsValues, extraFlags, null);
    }

    @Deprecated
    public void startIntentSenderFromChild(Activity child, IntentSender intent, int requestCode, Intent fillInIntent, int flagsMask, int flagsValues, int extraFlags, Bundle options) throws IntentSender.SendIntentException {
        startIntentSenderForResultInner(intent, child.mEmbeddedID, requestCode, fillInIntent, flagsMask, flagsValues, options);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void startIntentSenderFromFragment(Fragment fragment, IntentSender intent, int requestCode, Intent fillInIntent, int flagsMask, int flagsValues, Bundle options) throws IntentSender.SendIntentException {
        startIntentSenderForResultInner(intent, fragment.mWho, requestCode, fillInIntent, flagsMask, flagsValues, options);
    }

    public void overrideActivityTransition(int overrideType, int enterAnim, int exitAnim) {
        overrideActivityTransition(overrideType, enterAnim, exitAnim, 0);
    }

    public void overrideActivityTransition(int overrideType, int enterAnim, int exitAnim, int backgroundColor) {
        if (overrideType != 0 && overrideType != 1) {
            throw new IllegalArgumentException("Override type must be either open or close");
        }
        ActivityClient.getInstance().overrideActivityTransition(this.mToken, overrideType == 0, enterAnim, exitAnim, backgroundColor);
    }

    public void clearOverrideActivityTransition(int overrideType) {
        if (overrideType != 0 && overrideType != 1) {
            throw new IllegalArgumentException("Override type must be either open or close");
        }
        ActivityClient.getInstance().clearOverrideActivityTransition(this.mToken, overrideType == 0);
    }

    @Deprecated
    public void overridePendingTransition(int enterAnim, int exitAnim) {
        overridePendingTransition(enterAnim, exitAnim, 0);
    }

    @Deprecated
    public void overridePendingTransition(int enterAnim, int exitAnim, int backgroundColor) {
        ActivityClient.getInstance().overridePendingTransition(this.mToken, getPackageName(), enterAnim, exitAnim, backgroundColor);
    }

    public final void setResult(int resultCode) {
        synchronized (this) {
            this.mResultCode = resultCode;
            this.mResultData = null;
        }
    }

    public final void setForceSendResultForMediaProjection() {
        ActivityClient.getInstance().setForceSendResultForMediaProjection(this.mToken);
    }

    public final void setResult(int resultCode, Intent data) {
        synchronized (this) {
            this.mResultCode = resultCode;
            this.mResultData = data;
        }
    }

    public Uri getReferrer() {
        Intent intent = getIntent();
        if (intent != null) {
            try {
                Uri referrer = (Uri) intent.getParcelableExtra(Intent.EXTRA_REFERRER, Uri.class);
                if (referrer != null) {
                    return referrer;
                }
                String referrerName = intent.getStringExtra(Intent.EXTRA_REFERRER_NAME);
                if (referrerName != null) {
                    return Uri.parse(referrerName);
                }
            } catch (BadParcelableException e) {
                Log.m104w(TAG, "Cannot read referrer from intent; intent extras contain unknown custom Parcelable objects");
            }
        }
        if (this.mReferrer != null) {
            return new Uri.Builder().scheme("android-app").authority(this.mReferrer).build();
        }
        return null;
    }

    public Uri onProvideReferrer() {
        return null;
    }

    public String getCallingPackage() {
        return ActivityClient.getInstance().getCallingPackage(this.mToken);
    }

    public ComponentName getCallingActivity() {
        return ActivityClient.getInstance().getCallingActivity(this.mToken);
    }

    public int getLaunchedFromUid() {
        return ActivityClient.getInstance().getLaunchedFromUid(getActivityToken());
    }

    public String getLaunchedFromPackage() {
        return ActivityClient.getInstance().getLaunchedFromPackage(getActivityToken());
    }

    public void setVisible(boolean visible) {
        if (this.mVisibleFromClient != visible) {
            this.mVisibleFromClient = visible;
            if (this.mVisibleFromServer) {
                if (!visible) {
                    this.mDecor.setVisibility(4);
                } else {
                    makeVisible();
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void makeVisible() {
        if (!this.mWindowAdded) {
            ViewManager wm = getWindowManager();
            wm.addView(this.mDecor, getWindow().getAttributes());
            this.mWindowAdded = true;
        }
        this.mDecor.setVisibility(0);
    }

    public boolean isFinishing() {
        return this.mFinished;
    }

    public boolean isDestroyed() {
        return this.mDestroyed;
    }

    public boolean isChangingConfigurations() {
        return this.mChangingConfigurations;
    }

    public void recreate() {
        if (this.mParent != null) {
            throw new IllegalStateException("Can only be called on top-level activity");
        }
        if (Looper.myLooper() != this.mMainThread.getLooper()) {
            throw new IllegalStateException("Must be called from main thread");
        }
        this.mMainThread.scheduleRelaunchActivity(this.mToken);
    }

    private void finish(int finishTask) {
        int resultCode;
        Intent resultData;
        Activity activity = this.mParent;
        if (activity == null) {
            synchronized (this) {
                resultCode = this.mResultCode;
                resultData = this.mResultData;
            }
            if (resultData != null) {
                resultData.prepareToLeaveProcess(this);
            }
            if (ActivityClient.getInstance().finishActivity(this.mToken, resultCode, resultData, finishTask)) {
                this.mFinished = true;
            }
        } else {
            activity.finishFromChild(this);
        }
        getAutofillClientController().onActivityFinish(this.mIntent);
    }

    public void finish() {
        finish(0);
    }

    public void finishAffinity() {
        if (this.mParent != null) {
            throw new IllegalStateException("Can not be called from an embedded activity");
        }
        if (this.mResultCode != 0 || this.mResultData != null) {
            throw new IllegalStateException("Can not be called to deliver a result");
        }
        if (ActivityClient.getInstance().finishActivityAffinity(this.mToken)) {
            this.mFinished = true;
        }
    }

    @Deprecated
    public void finishFromChild(Activity child) {
        finish();
    }

    public void finishAfterTransition() {
        if (!this.mActivityTransitionState.startExitBackTransition(this)) {
            finish();
        }
    }

    public void finishActivity(int requestCode) {
        Activity activity = this.mParent;
        if (activity == null) {
            ActivityClient.getInstance().finishSubActivity(this.mToken, this.mEmbeddedID, requestCode);
        } else {
            activity.finishActivityFromChild(this, requestCode);
        }
    }

    @Deprecated
    public void finishActivityFromChild(Activity child, int requestCode) {
        ActivityClient.getInstance().finishSubActivity(this.mToken, child.mEmbeddedID, requestCode);
    }

    public void finishAndRemoveTask() {
        finish(1);
    }

    public boolean releaseInstance() {
        return ActivityClient.getInstance().releaseActivityInstance(this.mToken);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    }

    public void onActivityReenter(int resultCode, Intent data) {
    }

    public PendingIntent createPendingResult(int requestCode, Intent data, int flags) {
        String packageName = getPackageName();
        try {
            data.prepareToLeaveProcess(this);
            IActivityManager service = ActivityManager.getService();
            String attributionTag = getAttributionTag();
            Activity activity = this.mParent;
            IIntentSender target = service.getIntentSenderWithFeature(3, packageName, attributionTag, activity == null ? this.mToken : activity.mToken, this.mEmbeddedID, requestCode, new Intent[]{data}, null, flags, null, getUserId());
            if (target != null) {
                return new PendingIntent(target);
            }
            return null;
        } catch (RemoteException e) {
            return null;
        }
    }

    public void setRequestedOrientation(int requestedOrientation) {
        Activity activity = this.mParent;
        if (activity == null) {
            ActivityClient.getInstance().setRequestedOrientation(this.mToken, requestedOrientation);
        } else {
            activity.setRequestedOrientation(requestedOrientation);
        }
    }

    public int getRequestedOrientation() {
        Activity activity = this.mParent;
        if (activity == null) {
            return ActivityClient.getInstance().getRequestedOrientation(this.mToken);
        }
        return activity.getRequestedOrientation();
    }

    public int getTaskId() {
        return ActivityClient.getInstance().getTaskForActivity(this.mToken, false);
    }

    public boolean isTaskRoot() {
        return this.mWindowControllerCallback.isTaskRoot();
    }

    public boolean moveTaskToBack(boolean nonRoot) {
        return ActivityClient.getInstance().moveActivityTaskToBack(this.mToken, nonRoot);
    }

    public String getLocalClassName() {
        String pkg = getPackageName();
        String cls = this.mComponent.getClassName();
        int packageLen = pkg.length();
        if (!cls.startsWith(pkg) || cls.length() <= packageLen || cls.charAt(packageLen) != '.') {
            return cls;
        }
        return cls.substring(packageLen + 1);
    }

    public ComponentName getComponentName() {
        return this.mComponent;
    }

    @Override // android.view.contentcapture.ContentCaptureManager.ContentCaptureClient
    public final ComponentName contentCaptureClientGetComponentName() {
        return getComponentName();
    }

    public SharedPreferences getPreferences(int mode) {
        return getSharedPreferences(getLocalClassName(), mode);
    }

    public boolean isLaunchedFromBubble() {
        return this.mLaunchedFromBubble;
    }

    private void ensureSearchManager() {
        if (this.mSearchManager != null) {
            return;
        }
        try {
            this.mSearchManager = new SearchManager(this, null);
        } catch (ServiceManager.ServiceNotFoundException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override // android.view.ContextThemeWrapper, android.content.ContextWrapper, android.content.Context
    public Object getSystemService(String name) {
        if (getBaseContext() == null) {
            throw new IllegalStateException("System services not available to Activities before onCreate()");
        }
        if (Context.WINDOW_SERVICE.equals(name)) {
            return this.mWindowManager;
        }
        if ("search".equals(name)) {
            ensureSearchManager();
            return this.mSearchManager;
        }
        return super.getSystemService(name);
    }

    public void setTitle(CharSequence title) {
        this.mTitle = title;
        onTitleChanged(title, this.mTitleColor);
        Activity activity = this.mParent;
        if (activity != null) {
            activity.onChildTitleChanged(this, title);
        }
    }

    public void setTitle(int titleId) {
        setTitle(getText(titleId));
    }

    @Deprecated
    public void setTitleColor(int textColor) {
        this.mTitleColor = textColor;
        onTitleChanged(this.mTitle, textColor);
    }

    public final CharSequence getTitle() {
        return this.mTitle;
    }

    public final int getTitleColor() {
        return this.mTitleColor;
    }

    protected void onTitleChanged(CharSequence title, int color) {
        if (this.mTitleReady) {
            Window win = getWindow();
            if (win != null) {
                win.setTitle(title);
                if (color != 0) {
                    win.setTitleColor(color);
                }
            }
            ActionBar actionBar = this.mActionBar;
            if (actionBar != null) {
                actionBar.setWindowTitle(title);
            }
        }
    }

    protected void onChildTitleChanged(Activity childActivity, CharSequence title) {
    }

    public void setTaskDescription(ActivityManager.TaskDescription taskDescription) {
        ActivityManager.TaskDescription taskDescription2 = this.mTaskDescription;
        if (taskDescription2 != taskDescription) {
            taskDescription2.copyFromPreserveHiddenFields(taskDescription);
            if (taskDescription.getIconFilename() == null && taskDescription.getIcon() != null) {
                int size = ActivityManager.getLauncherLargeIconSizeInner(this);
                Bitmap icon = Bitmap.createScaledBitmap(taskDescription.getIcon(), size, size, true);
                this.mTaskDescription.setIcon(Icon.createWithBitmap(icon));
            }
        }
        ActivityClient.getInstance().setTaskDescription(this.mToken, this.mTaskDescription);
    }

    @Deprecated
    public final void setProgressBarVisibility(boolean visible) {
        getWindow().setFeatureInt(2, visible ? -1 : -2);
    }

    @Deprecated
    public final void setProgressBarIndeterminateVisibility(boolean visible) {
        getWindow().setFeatureInt(5, visible ? -1 : -2);
    }

    @Deprecated
    public final void setProgressBarIndeterminate(boolean indeterminate) {
        getWindow().setFeatureInt(2, indeterminate ? -3 : -4);
    }

    @Deprecated
    public final void setProgress(int progress) {
        getWindow().setFeatureInt(2, progress + 0);
    }

    @Deprecated
    public final void setSecondaryProgress(int secondaryProgress) {
        getWindow().setFeatureInt(2, secondaryProgress + 20000);
    }

    public final void setVolumeControlStream(int streamType) {
        getWindow().setVolumeControlStream(streamType);
    }

    public final int getVolumeControlStream() {
        return getWindow().getVolumeControlStream();
    }

    public final void setMediaController(MediaController controller) {
        getWindow().setMediaController(controller);
    }

    public final MediaController getMediaController() {
        return getWindow().getMediaController();
    }

    public final void runOnUiThread(Runnable action) {
        if (Thread.currentThread() != this.mUiThread) {
            this.mHandler.post(action);
        } else {
            action.run();
        }
    }

    @Override // android.view.LayoutInflater.Factory
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        return null;
    }

    @Override // android.view.LayoutInflater.Factory2
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
        if (!"fragment".equals(name)) {
            return onCreateView(name, context, attrs);
        }
        return this.mFragments.onCreateView(parent, name, context, attrs);
    }

    public void dump(String prefix, FileDescriptor fd, PrintWriter writer, String[] args) {
        dumpInner(prefix, fd, writer, args);
    }

    @SystemApi(client = SystemApi.Client.MODULE_LIBRARIES)
    public final boolean addDumpable(Dumpable dumpable) {
        if (this.mDumpableContainer == null) {
            this.mDumpableContainer = new DumpableContainerImpl();
        }
        return this.mDumpableContainer.addDumpable(dumpable);
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    public void dumpInternal(String prefix, FileDescriptor fd, PrintWriter writer, String[] args) {
        char c;
        AutofillClientController autofillClientController = this.mAutofillClientController;
        if (autofillClientController != null) {
            addDumpable(autofillClientController);
        }
        UiTranslationController uiTranslationController = this.mUiTranslationController;
        if (uiTranslationController != null) {
            addDumpable(uiTranslationController);
        }
        ContentCaptureManager contentCaptureManager = this.mContentCaptureManager;
        if (contentCaptureManager != null) {
            contentCaptureManager.addDumpable(this);
        }
        boolean dumpInternalState = true;
        String arg = null;
        if (args != null && args.length > 0) {
            arg = args[0];
            boolean isSpecialCase = true;
            switch (arg.hashCode()) {
                case -645125871:
                    if (arg.equals(DUMP_ARG_TRANSLATION)) {
                        c = 2;
                        break;
                    }
                    c = 65535;
                    break;
                case 100470631:
                    if (arg.equals(DUMP_ARG_DUMP_DUMPABLE)) {
                        c = 4;
                        break;
                    }
                    c = 65535;
                    break;
                case 472614934:
                    if (arg.equals(DUMP_ARG_LIST_DUMPABLES)) {
                        c = 3;
                        break;
                    }
                    c = 65535;
                    break;
                case 1159329357:
                    if (arg.equals(DUMP_ARG_CONTENT_CAPTURE)) {
                        c = 1;
                        break;
                    }
                    c = 65535;
                    break;
                case 1455016274:
                    if (arg.equals(DUMP_ARG_AUTOFILL)) {
                        c = 0;
                        break;
                    }
                    c = 65535;
                    break;
                default:
                    c = 65535;
                    break;
            }
            switch (c) {
                case 0:
                    dumpLegacyDumpable(prefix, writer, arg, AutofillClientController.DUMPABLE_NAME);
                    return;
                case 1:
                    dumpLegacyDumpable(prefix, writer, arg, ContentCaptureManager.DUMPABLE_NAME);
                    return;
                case 2:
                    dumpLegacyDumpable(prefix, writer, arg, UiTranslationController.DUMPABLE_NAME);
                    return;
                case 3:
                    DumpableContainerImpl dumpableContainerImpl = this.mDumpableContainer;
                    if (dumpableContainerImpl == null) {
                        writer.print(prefix);
                        writer.println("No dumpables");
                        return;
                    }
                    dumpableContainerImpl.listDumpables(prefix, writer);
                    return;
                case 4:
                    if (args.length == 1) {
                        writer.print(DUMP_ARG_DUMP_DUMPABLE);
                        writer.println(" requires the dumpable name");
                        break;
                    } else if (this.mDumpableContainer == null) {
                        writer.println("no dumpables");
                        break;
                    } else {
                        String[] prunedArgs = new String[args.length - 2];
                        System.arraycopy(args, 2, prunedArgs, 0, prunedArgs.length);
                        this.mDumpableContainer.dumpOneDumpable(prefix, writer, args[1], prunedArgs);
                        break;
                    }
                default:
                    isSpecialCase = false;
                    break;
            }
            if (isSpecialCase) {
                dumpInternalState = !CompatChanges.isChangeEnabled(DUMP_IGNORES_SPECIAL_ARGS);
            }
        }
        if (dumpInternalState) {
            dump(prefix, fd, writer, args);
        } else {
            Log.m108i(TAG, "Not calling dump() on " + this + " because of special argument " + arg);
        }
    }

    void dumpInner(String prefix, FileDescriptor fd, PrintWriter writer, String[] args) {
        String innerPrefix = prefix + "  ";
        writer.print(prefix);
        writer.print("Local Activity ");
        writer.print(Integer.toHexString(System.identityHashCode(this)));
        writer.println(" State:");
        writer.print(innerPrefix);
        writer.print("mResumed=");
        writer.print(this.mResumed);
        writer.print(" mStopped=");
        writer.print(this.mStopped);
        writer.print(" mFinished=");
        writer.println(this.mFinished);
        writer.print(innerPrefix);
        writer.print("mIsInMultiWindowMode=");
        writer.print(this.mIsInMultiWindowMode);
        writer.print(" mIsInPictureInPictureMode=");
        writer.println(this.mIsInPictureInPictureMode);
        writer.print(innerPrefix);
        writer.print("mChangingConfigurations=");
        writer.println(this.mChangingConfigurations);
        writer.print(innerPrefix);
        writer.print("mCurrentConfig=");
        writer.println(this.mCurrentConfig);
        this.mFragments.dumpLoaders(innerPrefix, fd, writer, args);
        this.mFragments.getFragmentManager().dump(innerPrefix, fd, writer, args);
        VoiceInteractor voiceInteractor = this.mVoiceInteractor;
        if (voiceInteractor != null) {
            voiceInteractor.dump(innerPrefix, fd, writer, args);
        }
        if (getWindow() != null && getWindow().peekDecorView() != null && getWindow().peekDecorView().getViewRootImpl() != null) {
            getWindow().peekDecorView().getViewRootImpl().dump(prefix, writer);
        }
        this.mHandler.getLooper().dump(new PrintWriterPrinter(writer), prefix);
        ResourcesManager.getInstance().dump(prefix, writer);
        DumpableContainerImpl dumpableContainerImpl = this.mDumpableContainer;
        if (dumpableContainerImpl != null) {
            dumpableContainerImpl.dumpAllDumpables(prefix, writer, args);
        }
    }

    private void dumpLegacyDumpable(String prefix, PrintWriter writer, String legacyOption, String dumpableName) {
        writer.printf("%s%s option deprecated. Use %s %s instead\n", prefix, legacyOption, DUMP_ARG_DUMP_DUMPABLE, dumpableName);
    }

    public boolean isImmersive() {
        return ActivityClient.getInstance().isImmersive(this.mToken);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean isTopOfTask() {
        if (this.mToken == null || this.mWindow == null) {
            return false;
        }
        return ActivityClient.getInstance().isTopOfTask(getActivityToken());
    }

    public boolean setTranslucent(boolean translucent) {
        if (translucent) {
            return convertToTranslucent(null, null);
        }
        return convertFromTranslucentInternal();
    }

    @SystemApi
    public void convertFromTranslucent() {
        convertFromTranslucentInternal();
    }

    private boolean convertFromTranslucentInternal() {
        this.mTranslucentCallback = null;
        if (ActivityClient.getInstance().convertFromTranslucent(this.mToken)) {
            WindowManagerGlobal.getInstance().changeCanvasOpacity(this.mToken, true);
            return true;
        }
        return false;
    }

    @SystemApi
    public boolean convertToTranslucent(TranslucentConversionListener callback, ActivityOptions options) {
        TranslucentConversionListener translucentConversionListener;
        this.mTranslucentCallback = callback;
        this.mChangeCanvasToTranslucent = ActivityClient.getInstance().convertToTranslucent(this.mToken, options == null ? null : options.toBundle());
        WindowManagerGlobal.getInstance().changeCanvasOpacity(this.mToken, false);
        if (!this.mChangeCanvasToTranslucent && (translucentConversionListener = this.mTranslucentCallback) != null) {
            translucentConversionListener.onTranslucentConversionComplete(true);
        }
        return this.mChangeCanvasToTranslucent;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onTranslucentConversionComplete(boolean drawComplete) {
        TranslucentConversionListener translucentConversionListener = this.mTranslucentCallback;
        if (translucentConversionListener != null) {
            translucentConversionListener.onTranslucentConversionComplete(drawComplete);
            this.mTranslucentCallback = null;
        }
        if (this.mChangeCanvasToTranslucent) {
            WindowManagerGlobal.getInstance().changeCanvasOpacity(this.mToken, false);
        }
    }

    public void onNewActivityOptions(ActivityOptions options) {
        this.mActivityTransitionState.setEnterActivityOptions(this, options);
        if (!this.mStopped) {
            this.mActivityTransitionState.enterReady(this);
        }
    }

    ActivityOptions getActivityOptions() {
        ActivityOptions options = this.mPendingOptions;
        this.mPendingOptions = null;
        return options;
    }

    @Deprecated
    public boolean requestVisibleBehind(boolean visible) {
        return false;
    }

    @Deprecated
    public void onVisibleBehindCanceled() {
        this.mCalled = true;
    }

    @SystemApi
    @Deprecated
    public boolean isBackgroundVisibleBehind() {
        return false;
    }

    @SystemApi
    @Deprecated
    public void onBackgroundVisibleBehindChanged(boolean visible) {
    }

    public void onEnterAnimationComplete() {
    }

    public void dispatchEnterAnimationComplete() {
        this.mEnterAnimationComplete = true;
        this.mInstrumentation.onEnterAnimationComplete();
        onEnterAnimationComplete();
        if (getWindow() != null && getWindow().getDecorView() != null) {
            View decorView = getWindow().getDecorView();
            decorView.getViewTreeObserver().dispatchOnEnterAnimationComplete();
        }
    }

    public void setImmersive(boolean i) {
        ActivityClient.getInstance().setImmersive(this.mToken, i);
    }

    public void setVrModeEnabled(boolean enabled, ComponentName requestedComponent) throws PackageManager.NameNotFoundException {
        if (ActivityClient.getInstance().setVrMode(this.mToken, enabled, requestedComponent) != 0) {
            throw new PackageManager.NameNotFoundException(requestedComponent.flattenToString());
        }
    }

    public ActionMode startActionMode(ActionMode.Callback callback) {
        return this.mWindow.getDecorView().startActionMode(callback);
    }

    public ActionMode startActionMode(ActionMode.Callback callback, int type) {
        return this.mWindow.getDecorView().startActionMode(callback, type);
    }

    @Override // android.view.Window.Callback
    public ActionMode onWindowStartingActionMode(ActionMode.Callback callback) {
        if (this.mActionModeTypeStarting == 0) {
            initWindowDecorActionBar();
            ActionBar actionBar = this.mActionBar;
            if (actionBar != null) {
                return actionBar.startActionMode(callback);
            }
            return null;
        }
        return null;
    }

    @Override // android.view.Window.Callback
    public ActionMode onWindowStartingActionMode(ActionMode.Callback callback, int type) {
        try {
            this.mActionModeTypeStarting = type;
            return onWindowStartingActionMode(callback);
        } finally {
            this.mActionModeTypeStarting = 0;
        }
    }

    @Override // android.view.Window.Callback
    public void onActionModeStarted(ActionMode mode) {
    }

    @Override // android.view.Window.Callback
    public void onActionModeFinished(ActionMode mode) {
    }

    public boolean shouldUpRecreateTask(Intent targetIntent) {
        try {
            PackageManager pm = getPackageManager();
            ComponentName cn = targetIntent.getComponent();
            if (cn == null) {
                cn = targetIntent.resolveActivity(pm);
            }
            ActivityInfo info = pm.getActivityInfo(cn, 0);
            if (info.taskAffinity == null) {
                return false;
            }
            return ActivityClient.getInstance().shouldUpRecreateTask(this.mToken, info.taskAffinity);
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public boolean navigateUpTo(Intent upIntent) {
        Intent upIntent2;
        int resultCode;
        Intent resultData;
        Activity activity = this.mParent;
        if (activity == null) {
            if (upIntent.getComponent() != null) {
                upIntent2 = upIntent;
            } else {
                ComponentName destInfo = upIntent.resolveActivity(getPackageManager());
                if (destInfo == null) {
                    return false;
                }
                Intent upIntent3 = new Intent(upIntent);
                upIntent3.setComponent(destInfo);
                upIntent2 = upIntent3;
            }
            synchronized (this) {
                resultCode = this.mResultCode;
                resultData = this.mResultData;
            }
            if (resultData != null) {
                resultData.prepareToLeaveProcess(this);
            }
            upIntent2.prepareToLeaveProcess(this);
            String resolvedType = upIntent2.resolveTypeIfNeeded(getContentResolver());
            return ActivityClient.getInstance().navigateUpTo(this.mToken, upIntent2, resolvedType, resultCode, resultData);
        }
        return activity.navigateUpToFromChild(this, upIntent);
    }

    @Deprecated
    public boolean navigateUpToFromChild(Activity child, Intent upIntent) {
        return navigateUpTo(upIntent);
    }

    public Intent getParentActivityIntent() {
        String parentName = this.mActivityInfo.parentActivityName;
        if (TextUtils.isEmpty(parentName)) {
            return null;
        }
        ComponentName target = new ComponentName(this, parentName);
        try {
            ActivityInfo parentInfo = getPackageManager().getActivityInfo(target, 0);
            String parentActivity = parentInfo.parentActivityName;
            if (parentActivity == null) {
                Intent parentIntent = Intent.makeMainActivity(target);
                return parentIntent;
            }
            Intent parentIntent2 = new Intent().setComponent(target);
            return parentIntent2;
        } catch (PackageManager.NameNotFoundException e) {
            Log.m110e(TAG, "getParentActivityIntent: bad parentActivityName '" + parentName + "' in manifest");
            return null;
        }
    }

    public void setEnterSharedElementCallback(SharedElementCallback callback) {
        if (callback == null) {
            callback = SharedElementCallback.NULL_CALLBACK;
        }
        this.mEnterTransitionListener = callback;
    }

    public void setExitSharedElementCallback(SharedElementCallback callback) {
        if (callback == null) {
            callback = SharedElementCallback.NULL_CALLBACK;
        }
        this.mExitTransitionListener = callback;
    }

    public void postponeEnterTransition() {
        this.mActivityTransitionState.postponeEnterTransition();
    }

    public void startPostponedEnterTransition() {
        this.mActivityTransitionState.startPostponedEnterTransition();
    }

    public DragAndDropPermissions requestDragAndDropPermissions(DragEvent event) {
        DragAndDropPermissions dragAndDropPermissions = DragAndDropPermissions.obtain(event);
        if (dragAndDropPermissions != null && dragAndDropPermissions.take(getActivityToken())) {
            return dragAndDropPermissions;
        }
        return null;
    }

    final void setParent(Activity parent) {
        this.mParent = parent;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void attach(Context context, ActivityThread aThread, Instrumentation instr, IBinder token, int ident, Application application, Intent intent, ActivityInfo info, CharSequence title, Activity parent, String id, NonConfigurationInstances lastNonConfigurationInstances, Configuration config, String referrer, IVoiceInteractor voiceInteractor, Window window, ViewRootImpl.ActivityConfigCallback activityConfigCallback, IBinder assistToken, IBinder shareableActivityToken) {
        attachBaseContext(context);
        this.mFragments.attachHost(null);
        this.mActivityInfo = info;
        PhoneWindow phoneWindow = new PhoneWindow(this, window, activityConfigCallback);
        this.mWindow = phoneWindow;
        phoneWindow.setWindowControllerCallback(this.mWindowControllerCallback);
        this.mWindow.setCallback(this);
        this.mWindow.setOnWindowDismissedCallback(this);
        this.mWindow.getLayoutInflater().setPrivateFactory(this);
        if (info.softInputMode != 0) {
            this.mWindow.setSoftInputMode(info.softInputMode);
        }
        if (info.uiOptions != 0) {
            this.mWindow.setUiOptions(info.uiOptions);
        }
        this.mUiThread = Thread.currentThread();
        this.mMainThread = aThread;
        this.mInstrumentation = instr;
        this.mToken = token;
        this.mAssistToken = assistToken;
        this.mShareableActivityToken = shareableActivityToken;
        this.mIdent = ident;
        this.mApplication = application;
        this.mIntent = intent;
        this.mReferrer = referrer;
        this.mComponent = intent.getComponent();
        this.mTitle = title;
        this.mParent = parent;
        this.mEmbeddedID = id;
        this.mLastNonConfigurationInstances = lastNonConfigurationInstances;
        if (voiceInteractor != null) {
            if (lastNonConfigurationInstances != null) {
                this.mVoiceInteractor = lastNonConfigurationInstances.voiceInteractor;
            } else {
                this.mVoiceInteractor = new VoiceInteractor(voiceInteractor, this, this, Looper.myLooper());
            }
        }
        this.mWindow.setWindowManager((WindowManager) context.getSystemService(Context.WINDOW_SERVICE), this.mToken, this.mComponent.flattenToString(), (info.flags & 512) != 0);
        Activity activity = this.mParent;
        if (activity != null) {
            this.mWindow.setContainer(activity.getWindow());
        }
        this.mWindowManager = this.mWindow.getWindowManager();
        this.mCurrentConfig = config;
        this.mWindow.setColorMode(info.colorMode);
        this.mWindow.setPreferMinimalPostProcessing((info.flags & 33554432) != 0);
        getAutofillClientController().onActivityAttached(application);
        setContentCaptureOptions(application.getContentCaptureOptions());
    }

    @Override // android.content.ContextWrapper, android.content.Context
    public final IBinder getActivityToken() {
        Activity activity = this.mParent;
        return activity != null ? activity.getActivityToken() : this.mToken;
    }

    public final IBinder getAssistToken() {
        Activity activity = this.mParent;
        return activity != null ? activity.getAssistToken() : this.mAssistToken;
    }

    public final IBinder getShareableActivityToken() {
        Activity activity = this.mParent;
        return activity != null ? activity.getShareableActivityToken() : this.mShareableActivityToken;
    }

    public final ActivityThread getActivityThread() {
        return this.mMainThread;
    }

    public final ActivityInfo getActivityInfo() {
        return this.mActivityInfo;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void performCreate(Bundle icicle) {
        performCreate(icicle, null);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void performCreate(Bundle icicle, PersistableBundle persistentState) {
        if (Trace.isTagEnabled(32L)) {
            Trace.traceBegin(32L, "performCreate:" + this.mComponent.getClassName());
        }
        dispatchActivityPreCreated(icicle);
        this.mCanEnterPictureInPicture = true;
        int windowingMode = getResources().getConfiguration().windowConfiguration.getWindowingMode();
        this.mIsInMultiWindowMode = WindowConfiguration.inMultiWindowMode(windowingMode);
        this.mIsInPictureInPictureMode = windowingMode == 2;
        this.mShouldDockBigOverlays = getResources().getBoolean(C4057R.bool.config_dockBigOverlayWindows);
        restoreHasCurrentPermissionRequest(icicle);
        long startTime = SystemClock.uptimeMillis();
        if (persistentState != null) {
            onCreate(icicle, persistentState);
        } else {
            onCreate(icicle);
        }
        long duration = SystemClock.uptimeMillis() - startTime;
        EventLogTags.writeWmOnCreateCalled(this.mIdent, getComponentName().getClassName(), "performCreate", duration);
        this.mActivityTransitionState.readState(icicle);
        this.mVisibleFromClient = true ^ this.mWindow.getWindowStyle().getBoolean(10, false);
        this.mFragments.dispatchActivityCreated();
        this.mActivityTransitionState.setEnterActivityOptions(this, getActivityOptions());
        dispatchActivityPostCreated(icicle);
        Trace.traceEnd(32L);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void performNewIntent(Intent intent) {
        Trace.traceBegin(32L, "performNewIntent");
        this.mCanEnterPictureInPicture = true;
        onNewIntent(intent);
        Trace.traceEnd(32L);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void performStart(String reason) {
        String dlwarning;
        if (Trace.isTagEnabled(32L)) {
            Trace.traceBegin(32L, "performStart:" + this.mComponent.getClassName());
        }
        dispatchActivityPreStarted();
        this.mActivityTransitionState.setEnterActivityOptions(this, getActivityOptions());
        this.mFragments.noteStateNotSaved();
        this.mCalled = false;
        this.mFragments.execPendingActions();
        long startTime = SystemClock.uptimeMillis();
        this.mInstrumentation.callActivityOnStart(this);
        long duration = SystemClock.uptimeMillis() - startTime;
        EventLogTags.writeWmOnStartCalled(this.mIdent, getComponentName().getClassName(), reason, duration);
        if (!this.mCalled) {
            throw new SuperNotCalledException("Activity " + this.mComponent.toShortString() + " did not call through to super.onStart()");
        }
        this.mFragments.dispatchStart();
        this.mFragments.reportLoaderStart();
        boolean isAppDebuggable = (this.mApplication.getApplicationInfo().flags & 2) != 0;
        if (isAppDebuggable && (dlwarning = getDlWarning()) != null) {
            String appName = getApplicationInfo().loadLabel(getPackageManager()).toString();
            String warning = "Detected problems with app native libraries\n(please consult log for detail):\n" + dlwarning;
            if (isAppDebuggable) {
                new AlertDialog.Builder(this).setTitle(appName).setMessage(warning).setPositiveButton(17039370, (DialogInterface.OnClickListener) null).setCancelable(false).show();
            } else {
                Toast.makeText(this, appName + "\n" + warning, 1).show();
            }
        }
        GraphicsEnvironment.getInstance().showAngleInUseDialogBox(this);
        this.mActivityTransitionState.enterReady(this);
        dispatchActivityPostStarted();
        Trace.traceEnd(32L);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void performRestart(boolean start) {
        Trace.traceBegin(32L, "performRestart");
        this.mCanEnterPictureInPicture = true;
        this.mFragments.noteStateNotSaved();
        if (this.mToken != null && this.mParent == null) {
            WindowManagerGlobal.getInstance().setStoppedState(this.mToken, false);
        }
        if (this.mStopped) {
            this.mStopped = false;
            synchronized (this.mManagedCursors) {
                int N = this.mManagedCursors.size();
                for (int i = 0; i < N; i++) {
                    ManagedCursor mc = this.mManagedCursors.get(i);
                    if (mc.mReleased || mc.mUpdated) {
                        if (!mc.mCursor.requery() && getApplicationInfo().targetSdkVersion >= 14) {
                            throw new IllegalStateException("trying to requery an already closed cursor  " + mc.mCursor);
                        }
                        mc.mReleased = false;
                        mc.mUpdated = false;
                    }
                }
            }
            this.mCalled = false;
            long startTime = SystemClock.uptimeMillis();
            this.mInstrumentation.callActivityOnRestart(this);
            long duration = SystemClock.uptimeMillis() - startTime;
            EventLogTags.writeWmOnRestartCalled(this.mIdent, getComponentName().getClassName(), "performRestart", duration);
            if (!this.mCalled) {
                throw new SuperNotCalledException("Activity " + this.mComponent.toShortString() + " did not call through to super.onRestart()");
            }
            if (start) {
                performStart("performRestart");
            }
        }
        Trace.traceEnd(32L);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void performResume(boolean followedByPause, String reason) {
        if (Trace.isTagEnabled(32L)) {
            Trace.traceBegin(32L, "performResume:" + this.mComponent.getClassName());
        }
        dispatchActivityPreResumed();
        this.mFragments.execPendingActions();
        this.mLastNonConfigurationInstances = null;
        getAutofillClientController().onActivityPerformResume(followedByPause);
        this.mCalled = false;
        long startTime = SystemClock.uptimeMillis();
        this.mInstrumentation.callActivityOnResume(this);
        long duration = SystemClock.uptimeMillis() - startTime;
        EventLogTags.writeWmOnResumeCalled(this.mIdent, getComponentName().getClassName(), reason, duration);
        if (!this.mCalled) {
            throw new SuperNotCalledException("Activity " + this.mComponent.toShortString() + " did not call through to super.onResume()");
        }
        if (!this.mVisibleFromClient && !this.mFinished) {
            Log.m104w(TAG, "An activity without a UI must call finish() before onResume() completes");
            if (getApplicationInfo().targetSdkVersion > 22) {
                throw new IllegalStateException("Activity " + this.mComponent.toShortString() + " did not call finish() prior to onResume() completing");
            }
        }
        this.mCalled = false;
        this.mFragments.dispatchResume();
        this.mFragments.execPendingActions();
        onPostResume();
        if (!this.mCalled) {
            throw new SuperNotCalledException("Activity " + this.mComponent.toShortString() + " did not call through to super.onPostResume()");
        }
        dispatchActivityPostResumed();
        Trace.traceEnd(32L);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void performPause() {
        if (Trace.isTagEnabled(32L)) {
            Trace.traceBegin(32L, "performPause:" + this.mComponent.getClassName());
        }
        dispatchActivityPrePaused();
        this.mDoReportFullyDrawn = false;
        this.mFragments.dispatchPause();
        this.mCalled = false;
        long startTime = SystemClock.uptimeMillis();
        onPause();
        long duration = SystemClock.uptimeMillis() - startTime;
        EventLogTags.writeWmOnPausedCalled(this.mIdent, getComponentName().getClassName(), "performPause", duration);
        this.mResumed = false;
        if (!this.mCalled && getApplicationInfo().targetSdkVersion >= 9) {
            throw new SuperNotCalledException("Activity " + this.mComponent.toShortString() + " did not call through to super.onPause()");
        }
        dispatchActivityPostPaused();
        Trace.traceEnd(32L);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void performUserLeaving() {
        onUserInteraction();
        onUserLeaveHint();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void performStop(boolean preserveWindow, String reason) {
        if (Trace.isTagEnabled(32L)) {
            Trace.traceBegin(32L, "performStop:" + this.mComponent.getClassName());
        }
        this.mDoReportFullyDrawn = false;
        this.mFragments.doLoaderStop(this.mChangingConfigurations);
        this.mCanEnterPictureInPicture = false;
        if (!this.mStopped) {
            dispatchActivityPreStopped();
            Window window = this.mWindow;
            if (window != null) {
                window.closeAllPanels();
            }
            if (!preserveWindow && this.mToken != null && this.mParent == null) {
                WindowManagerGlobal.getInstance().setStoppedState(this.mToken, true);
            }
            this.mFragments.dispatchStop();
            this.mCalled = false;
            long startTime = SystemClock.uptimeMillis();
            this.mInstrumentation.callActivityOnStop(this);
            long duration = SystemClock.uptimeMillis() - startTime;
            EventLogTags.writeWmOnStopCalled(this.mIdent, getComponentName().getClassName(), reason, duration);
            if (!this.mCalled) {
                throw new SuperNotCalledException("Activity " + this.mComponent.toShortString() + " did not call through to super.onStop()");
            }
            synchronized (this.mManagedCursors) {
                int N = this.mManagedCursors.size();
                for (int i = 0; i < N; i++) {
                    ManagedCursor mc = this.mManagedCursors.get(i);
                    if (!mc.mReleased) {
                        mc.mCursor.deactivate();
                        mc.mReleased = true;
                    }
                }
            }
            this.mStopped = true;
            dispatchActivityPostStopped();
        }
        this.mResumed = false;
        Trace.traceEnd(32L);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void performDestroy() {
        if (Trace.isTagEnabled(32L)) {
            Trace.traceBegin(32L, "performDestroy:" + this.mComponent.getClassName());
        }
        dispatchActivityPreDestroyed();
        this.mDestroyed = true;
        this.mWindow.destroy();
        this.mFragments.dispatchDestroy();
        long startTime = SystemClock.uptimeMillis();
        onDestroy();
        long duration = SystemClock.uptimeMillis() - startTime;
        EventLogTags.writeWmOnDestroyCalled(this.mIdent, getComponentName().getClassName(), "performDestroy", duration);
        this.mFragments.doLoaderDestroy();
        VoiceInteractor voiceInteractor = this.mVoiceInteractor;
        if (voiceInteractor != null) {
            voiceInteractor.detachActivity();
        }
        dispatchActivityPostDestroyed();
        Trace.traceEnd(32L);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void dispatchMultiWindowModeChanged(boolean isInMultiWindowMode, Configuration newConfig) {
        this.mFragments.dispatchMultiWindowModeChanged(isInMultiWindowMode, newConfig);
        Window window = this.mWindow;
        if (window != null) {
            window.onMultiWindowModeChanged();
        }
        this.mIsInMultiWindowMode = isInMultiWindowMode;
        onMultiWindowModeChanged(isInMultiWindowMode, newConfig);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void dispatchPictureInPictureModeChanged(boolean isInPictureInPictureMode, Configuration newConfig) {
        this.mFragments.dispatchPictureInPictureModeChanged(isInPictureInPictureMode, newConfig);
        Window window = this.mWindow;
        if (window != null) {
            window.onPictureInPictureModeChanged(isInPictureInPictureMode);
        }
        this.mIsInPictureInPictureMode = isInPictureInPictureMode;
        onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig);
    }

    public final boolean isResumed() {
        return this.mResumed;
    }

    private void storeHasCurrentPermissionRequest(Bundle bundle) {
        if (bundle != null && this.mHasCurrentPermissionsRequest) {
            bundle.putBoolean(HAS_CURENT_PERMISSIONS_REQUEST_KEY, true);
        }
    }

    private void restoreHasCurrentPermissionRequest(Bundle bundle) {
        if (bundle != null) {
            this.mHasCurrentPermissionsRequest = bundle.getBoolean(HAS_CURENT_PERMISSIONS_REQUEST_KEY, false);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void dispatchActivityResult(String who, int requestCode, int resultCode, Intent data, String reason) {
        this.mFragments.noteStateNotSaved();
        if (who == null) {
            onActivityResult(requestCode, resultCode, data);
        } else if (who.startsWith(REQUEST_PERMISSIONS_WHO_PREFIX)) {
            String who2 = who.substring(REQUEST_PERMISSIONS_WHO_PREFIX.length());
            if (TextUtils.isEmpty(who2)) {
                dispatchRequestPermissionsResult(requestCode, data);
            } else {
                Fragment frag = this.mFragments.findFragmentByWho(who2);
                if (frag != null) {
                    dispatchRequestPermissionsResultToFragment(requestCode, data, frag);
                }
            }
        } else if (who.startsWith("@android:view:")) {
            ArrayList<ViewRootImpl> views = WindowManagerGlobal.getInstance().getRootViews(getActivityToken());
            Iterator<ViewRootImpl> it = views.iterator();
            while (it.hasNext()) {
                ViewRootImpl viewRoot = it.next();
                if (viewRoot.getView() != null && viewRoot.getView().dispatchActivityResult(who, requestCode, resultCode, data)) {
                    return;
                }
            }
        } else if (who.startsWith(AutofillClientController.AUTO_FILL_AUTH_WHO_PREFIX)) {
            getAutofillClientController().onDispatchActivityResult(requestCode, resultCode, data);
        } else {
            Fragment frag2 = this.mFragments.findFragmentByWho(who);
            if (frag2 != null) {
                frag2.onActivityResult(requestCode, resultCode, data);
            }
        }
        EventLogTags.writeWmOnActivityResultCalled(this.mIdent, getComponentName().getClassName(), reason);
    }

    public void startLockTask() {
        ActivityClient.getInstance().startLockTaskModeByToken(this.mToken);
    }

    public void stopLockTask() {
        ActivityClient.getInstance().stopLockTaskModeByToken(this.mToken);
    }

    public void showLockTaskEscapeMessage() {
        ActivityClient.getInstance().showLockTaskEscapeMessage(this.mToken);
    }

    public boolean isOverlayWithDecorCaptionEnabled() {
        return this.mWindow.isOverlayWithDecorCaptionEnabled();
    }

    public void setOverlayWithDecorCaptionEnabled(boolean enabled) {
        this.mWindow.setOverlayWithDecorCaptionEnabled(enabled);
    }

    private void dispatchRequestPermissionsResult(int requestCode, Intent data) {
        this.mHasCurrentPermissionsRequest = false;
        String[] permissions = data != null ? data.getStringArrayExtra(PackageManager.EXTRA_REQUEST_PERMISSIONS_NAMES) : new String[0];
        int[] grantResults = data != null ? data.getIntArrayExtra(PackageManager.EXTRA_REQUEST_PERMISSIONS_RESULTS) : new int[0];
        onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void dispatchRequestPermissionsResultToFragment(int requestCode, Intent data, Fragment fragment) {
        String[] permissions = data != null ? data.getStringArrayExtra(PackageManager.EXTRA_REQUEST_PERMISSIONS_NAMES) : new String[0];
        int[] grantResults = data != null ? data.getIntArrayExtra(PackageManager.EXTRA_REQUEST_PERMISSIONS_RESULTS) : new int[0];
        fragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public final boolean isVisibleForAutofill() {
        return !this.mStopped;
    }

    public void setDisablePreviewScreenshots(boolean disable) {
        setRecentsScreenshotEnabled(!disable);
    }

    public void setRecentsScreenshotEnabled(boolean enabled) {
        ActivityClient.getInstance().setRecentsScreenshotEnabled(this.mToken, enabled);
    }

    public void setShowWhenLocked(boolean showWhenLocked) {
        ActivityClient.getInstance().setShowWhenLocked(this.mToken, showWhenLocked);
    }

    public void setInheritShowWhenLocked(boolean inheritShowWhenLocked) {
        ActivityClient.getInstance().setInheritShowWhenLocked(this.mToken, inheritShowWhenLocked);
    }

    public void setTurnScreenOn(boolean turnScreenOn) {
        ActivityClient.getInstance().setTurnScreenOn(this.mToken, turnScreenOn);
    }

    public void setAllowCrossUidActivitySwitchFromBelow(boolean allowed) {
        ActivityClient.getInstance().setAllowCrossUidActivitySwitchFromBelow(this.mToken, allowed);
    }

    public void registerRemoteAnimations(RemoteAnimationDefinition definition) {
        ActivityClient.getInstance().registerRemoteAnimations(this.mToken, definition);
    }

    public void unregisterRemoteAnimations() {
        ActivityClient.getInstance().unregisterRemoteAnimations(this.mToken);
    }

    public void updateUiTranslationState(int state, TranslationSpec sourceSpec, TranslationSpec targetSpec, List<AutofillId> viewIds, UiTranslationSpec uiTranslationSpec) {
        if (this.mUiTranslationController == null) {
            this.mUiTranslationController = new UiTranslationController(this, getApplicationContext());
        }
        this.mUiTranslationController.updateUiTranslationState(state, sourceSpec, targetSpec, viewIds, uiTranslationSpec);
    }

    public void enableTaskLocaleOverride() {
        ActivityClient.getInstance().enableTaskLocaleOverride(this.mToken);
    }

    /* loaded from: classes.dex */
    class HostCallbacks extends FragmentHostCallback<Activity> {
        public HostCallbacks() {
            super(Activity.this);
        }

        @Override // android.app.FragmentHostCallback
        public void onDump(String prefix, FileDescriptor fd, PrintWriter writer, String[] args) {
            Activity.this.dump(prefix, fd, writer, args);
        }

        @Override // android.app.FragmentHostCallback
        public boolean onShouldSaveFragmentState(Fragment fragment) {
            return !Activity.this.isFinishing();
        }

        @Override // android.app.FragmentHostCallback
        public LayoutInflater onGetLayoutInflater() {
            LayoutInflater result = Activity.this.getLayoutInflater();
            if (onUseFragmentManagerInflaterFactory()) {
                return result.cloneInContext(Activity.this);
            }
            return result;
        }

        @Override // android.app.FragmentHostCallback
        public boolean onUseFragmentManagerInflaterFactory() {
            return Activity.this.getApplicationInfo().targetSdkVersion >= 21;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.app.FragmentHostCallback
        public Activity onGetHost() {
            return Activity.this;
        }

        @Override // android.app.FragmentHostCallback
        public void onInvalidateOptionsMenu() {
            Activity.this.invalidateOptionsMenu();
        }

        @Override // android.app.FragmentHostCallback
        public void onStartActivityFromFragment(Fragment fragment, Intent intent, int requestCode, Bundle options) {
            Activity.this.startActivityFromFragment(fragment, intent, requestCode, options);
        }

        @Override // android.app.FragmentHostCallback
        public void onStartActivityAsUserFromFragment(Fragment fragment, Intent intent, int requestCode, Bundle options, UserHandle user) {
            Activity.this.startActivityAsUserFromFragment(fragment, intent, requestCode, options, user);
        }

        @Override // android.app.FragmentHostCallback
        public void onStartIntentSenderFromFragment(Fragment fragment, IntentSender intent, int requestCode, Intent fillInIntent, int flagsMask, int flagsValues, int extraFlags, Bundle options) throws IntentSender.SendIntentException {
            if (Activity.this.mParent == null) {
                Activity.this.startIntentSenderForResultInner(intent, fragment.mWho, requestCode, fillInIntent, flagsMask, flagsValues, options);
            } else if (options != null) {
                Activity.this.mParent.startIntentSenderFromFragment(fragment, intent, requestCode, fillInIntent, flagsMask, flagsValues, options);
            }
        }

        @Override // android.app.FragmentHostCallback
        public void onRequestPermissionsFromFragment(Fragment fragment, String[] permissions, int requestCode) {
            String who = Activity.REQUEST_PERMISSIONS_WHO_PREFIX + fragment.mWho;
            Intent intent = Activity.this.getPackageManager().buildRequestPermissionsIntent(permissions);
            Activity.this.startActivityForResult(who, intent, requestCode, null);
        }

        @Override // android.app.FragmentHostCallback
        public boolean onHasWindowAnimations() {
            return Activity.this.getWindow() != null;
        }

        @Override // android.app.FragmentHostCallback
        public int onGetWindowAnimations() {
            Window w = Activity.this.getWindow();
            if (w == null) {
                return 0;
            }
            return w.getAttributes().windowAnimations;
        }

        @Override // android.app.FragmentHostCallback
        public void onAttachFragment(Fragment fragment) {
            Activity.this.onAttachFragment(fragment);
        }

        @Override // android.app.FragmentHostCallback, android.app.FragmentContainer
        public <T extends View> T onFindViewById(int id) {
            return (T) Activity.this.findViewById(id);
        }

        @Override // android.app.FragmentHostCallback, android.app.FragmentContainer
        public boolean onHasView() {
            Window w = Activity.this.getWindow();
            return (w == null || w.peekDecorView() == null) ? false : true;
        }
    }

    public OnBackInvokedDispatcher getOnBackInvokedDispatcher() {
        Window window = this.mWindow;
        if (window == null) {
            throw new IllegalStateException("OnBackInvokedDispatcher are not available on non-visual activities");
        }
        return window.getOnBackInvokedDispatcher();
    }

    public void registerScreenCaptureCallback(Executor executor, ScreenCaptureCallback callback) {
        if (this.mScreenCaptureCallbackHandler == null) {
            this.mScreenCaptureCallbackHandler = new ScreenCaptureCallbackHandler(this.mToken);
        }
        this.mScreenCaptureCallbackHandler.registerScreenCaptureCallback(executor, callback);
    }

    public void unregisterScreenCaptureCallback(ScreenCaptureCallback callback) {
        ScreenCaptureCallbackHandler screenCaptureCallbackHandler = this.mScreenCaptureCallbackHandler;
        if (screenCaptureCallbackHandler != null) {
            screenCaptureCallbackHandler.unregisterScreenCaptureCallback(callback);
        }
    }
}
