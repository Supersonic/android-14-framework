package android.view;

import android.Manifest;
import android.animation.AnimationHandler;
import android.animation.LayoutTransition;
import android.app.ActivityManager;
import android.app.ActivityThread;
import android.app.ICompatCameraControlCallback;
import android.app.PendingIntent$$ExternalSyntheticLambda1;
import android.app.ResourcesManager;
import android.app.WindowConfiguration;
import android.app.compat.CompatChanges;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.content.p001pm.ActivityInfo;
import android.content.p001pm.PackageManager;
import android.content.res.CompatibilityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.BLASTBufferQueue;
import android.graphics.Canvas;
import android.graphics.FrameInfo;
import android.graphics.HardwareRenderer;
import android.graphics.HardwareRendererObserver;
import android.graphics.Insets;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.RecordingCanvas;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.RenderNode;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.hardware.display.DisplayManager;
import android.hardware.gnss.GnssSignalType;
import android.hardware.input.InputManager;
import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.media.AudioManager;
import android.media.AudioSystem;
import android.media.TtmlUtils;
import android.media.audio.Enums;
import android.p008os.Binder;
import android.p008os.Bundle;
import android.p008os.Debug;
import android.p008os.Handler;
import android.p008os.IBinder;
import android.p008os.Looper;
import android.p008os.Message;
import android.p008os.ParcelFileDescriptor;
import android.p008os.Process;
import android.p008os.RemoteException;
import android.p008os.SystemClock;
import android.p008os.SystemProperties;
import android.p008os.Trace;
import android.p008os.UserHandle;
import android.sysprop.DisplayProperties;
import android.text.TextUtils;
import android.util.AndroidRuntimeException;
import android.util.DisplayMetrics;
import android.util.EventLog;
import android.util.IndentingPrintWriter;
import android.util.Log;
import android.util.LongArray;
import android.util.MergedConfiguration;
import android.util.NtpTrustedTime;
import android.util.Slog;
import android.util.SparseArray;
import android.util.TypedValue;
import android.util.proto.ProtoOutputStream;
import android.view.ActionMode;
import android.view.AttachedSurfaceControl;
import android.view.Choreographer;
import android.view.IWindow;
import android.view.InputDevice;
import android.view.InputQueue;
import android.view.InsetsSourceControl;
import android.view.KeyCharacterMap;
import android.view.ScrollCaptureResponse;
import android.view.Surface;
import android.view.SurfaceControl;
import android.view.SurfaceHolder;
import android.view.ThreadedRenderer;
import android.view.View;
import android.view.ViewRootImpl;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityInteractionClient;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityNodeIdManager;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityNodeProvider;
import android.view.accessibility.AccessibilityWindowAttributes;
import android.view.accessibility.IAccessibilityEmbeddedConnection;
import android.view.accessibility.IAccessibilityInteractionConnection;
import android.view.accessibility.IAccessibilityInteractionConnectionCallback;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.autofill.AutofillId;
import android.view.autofill.AutofillManager;
import android.view.contentcapture.ContentCaptureManager;
import android.view.contentcapture.ContentCaptureSession;
import android.view.contentcapture.MainContentCaptureSession;
import android.view.inputmethod.ImeTracker;
import android.view.inputmethod.InputMethodManager;
import android.widget.Scroller;
import android.window.ClientWindowFrames;
import android.window.CompatOnBackInvokedCallback;
import android.window.OnBackInvokedCallback;
import android.window.OnBackInvokedDispatcher;
import android.window.ScreenCapture;
import android.window.SurfaceSyncGroup;
import android.window.WindowOnBackInvokedDispatcher;
import com.android.internal.C4057R;
import com.android.internal.graphics.drawable.BackgroundBlurDrawable;
import com.android.internal.inputmethod.ImeTracing;
import com.android.internal.inputmethod.InputMethodDebug;
import com.android.internal.p028os.IResultReceiver;
import com.android.internal.p028os.SomeArgs;
import com.android.internal.policy.DecorView;
import com.android.internal.policy.PhoneFallbackEventHandler;
import com.android.internal.util.Preconditions;
import com.android.internal.view.BaseSurfaceHolder;
import com.android.internal.view.RootViewSurfaceTaker;
import com.android.internal.view.SurfaceCallbackHelper;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.ref.WeakReference;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.OptionalInt;
import java.util.Queue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.Function;
/* loaded from: classes4.dex */
public final class ViewRootImpl implements ViewParent, View.AttachInfo.Callbacks, ThreadedRenderer.DrawCallbacks, AttachedSurfaceControl {
    private static final int CONTENT_CAPTURE_ENABLED_FALSE = 2;
    private static final int CONTENT_CAPTURE_ENABLED_NOT_CHECKED = 0;
    private static final int CONTENT_CAPTURE_ENABLED_TRUE = 1;
    private static final boolean DBG = false;
    private static final boolean DEBUG_BLAST = false;
    private static final boolean DEBUG_CONFIGURATION = false;
    private static final boolean DEBUG_CONTENT_CAPTURE = false;
    private static final boolean DEBUG_DIALOG = false;
    private static final boolean DEBUG_DRAW = false;
    private static final boolean DEBUG_FPS = false;
    private static final boolean DEBUG_IMF = false;
    private static final boolean DEBUG_INPUT_RESIZE = false;
    private static final boolean DEBUG_INPUT_STAGES = false;
    private static final boolean DEBUG_KEEP_SCREEN_ON = false;
    private static final boolean DEBUG_LAYOUT = false;
    private static final boolean DEBUG_ORIENTATION = false;
    private static final boolean DEBUG_SCROLL_CAPTURE = false;
    private static final boolean DEBUG_TRACKBALL = false;
    private static final boolean ENABLE_INPUT_LATENCY_TRACKING = true;
    private static final int KEEP_CLEAR_AREA_REPORT_RATE_MILLIS = 100;
    private static final boolean LOCAL_LOGV = false;
    private static final int LOGTAG_INPUT_FOCUS = 62001;
    private static final int MAX_QUEUED_INPUT_EVENT_POOL_SIZE = 10;
    static final int MAX_TRACKBALL_DELAY = 250;
    private static final int MSG_CHECK_FOCUS = 13;
    private static final int MSG_CLEAR_ACCESSIBILITY_FOCUS_HOST = 21;
    private static final int MSG_CLOSE_SYSTEM_DIALOGS = 14;
    private static final int MSG_DIE = 3;
    private static final int MSG_DISPATCH_APP_VISIBILITY = 8;
    private static final int MSG_DISPATCH_DRAG_EVENT = 15;
    private static final int MSG_DISPATCH_DRAG_LOCATION_EVENT = 16;
    private static final int MSG_DISPATCH_GET_NEW_SURFACE = 9;
    private static final int MSG_DISPATCH_INPUT_EVENT = 7;
    private static final int MSG_DISPATCH_KEY_FROM_AUTOFILL = 12;
    private static final int MSG_DISPATCH_KEY_FROM_IME = 11;
    private static final int MSG_DISPATCH_SYSTEM_UI_VISIBILITY = 17;
    private static final int MSG_DISPATCH_WINDOW_SHOWN = 25;
    private static final int MSG_HIDE_INSETS = 32;
    private static final int MSG_INSETS_CONTROL_CHANGED = 29;
    private static final int MSG_INVALIDATE = 1;
    private static final int MSG_INVALIDATE_RECT = 2;
    private static final int MSG_INVALIDATE_WORLD = 22;
    private static final int MSG_KEEP_CLEAR_RECTS_CHANGED = 35;
    private static final int MSG_POINTER_CAPTURE_CHANGED = 28;
    private static final int MSG_PROCESS_INPUT_EVENTS = 19;
    private static final int MSG_REPORT_KEEP_CLEAR_RECTS = 36;
    private static final int MSG_REQUEST_KEYBOARD_SHORTCUTS = 26;
    private static final int MSG_REQUEST_SCROLL_CAPTURE = 33;
    private static final int MSG_RESIZED = 4;
    private static final int MSG_RESIZED_REPORT = 5;
    private static final int MSG_SHOW_INSETS = 31;
    private static final int MSG_SYNTHESIZE_INPUT_EVENT = 24;
    private static final int MSG_SYSTEM_GESTURE_EXCLUSION_CHANGED = 30;
    private static final int MSG_UPDATE_CONFIGURATION = 18;
    private static final int MSG_UPDATE_POINTER_ICON = 27;
    private static final int MSG_WINDOW_FOCUS_CHANGED = 6;
    private static final int MSG_WINDOW_MOVED = 23;
    private static final int MSG_WINDOW_TOUCH_MODE_CHANGED = 34;
    private static final boolean MT_RENDERER_AVAILABLE = true;
    private static final String PROPERTY_PROFILE_RENDERING = "viewroot.profile_rendering";
    private static final int SCROLL_CAPTURE_REQUEST_TIMEOUT_MILLIS = 2500;
    private static final String TAG = "ViewRootImpl";
    private static final int UNSET_SYNC_ID = -1;
    private static final boolean USE_ASYNC_PERFORM_HAPTIC_FEEDBACK = true;
    private static final int WMS_SYNC_MERGED = 3;
    private static final int WMS_SYNC_NONE = 0;
    private static final int WMS_SYNC_PENDING = 1;
    private static final int WMS_SYNC_RETURNED = 2;
    private static boolean sAlwaysAssignFocus;
    private IAccessibilityEmbeddedConnection mAccessibilityEmbeddedConnection;
    View mAccessibilityFocusedHost;
    AccessibilityNodeInfo mAccessibilityFocusedVirtualView;
    final AccessibilityInteractionConnectionManager mAccessibilityInteractionConnectionManager;
    AccessibilityInteractionController mAccessibilityInteractionController;
    final AccessibilityManager mAccessibilityManager;
    private AccessibilityWindowAttributes mAccessibilityWindowAttributes;
    private SurfaceSyncGroup mActiveSurfaceSyncGroup;
    private ActivityConfigCallback mActivityConfigCallback;
    boolean mAdded;
    boolean mAddedTouchMode;
    private boolean mAppVisibilityChanged;
    boolean mAppVisible;
    boolean mApplyInsetsRequested;
    final View.AttachInfo mAttachInfo;
    AudioManager mAudioManager;
    final String mBasePackageName;
    private BLASTBufferQueue mBlastBufferQueue;
    private final BackgroundBlurDrawable.Aggregator mBlurRegionAggregator;
    private SurfaceControl mBoundsLayer;
    private int mCanvasOffsetX;
    private int mCanvasOffsetY;
    private boolean mCheckIfCanDraw;
    private final Rect mChildBoundingInsets;
    private boolean mChildBoundingInsetsChanged;
    final Choreographer mChoreographer;
    int mClientWindowLayoutFlags;
    private CompatOnBackInvokedCallback mCompatOnBackInvokedCallback;
    final SystemUiVisibilityInfo mCompatibleVisibilityInfo;
    final ConsumeBatchedInputImmediatelyRunnable mConsumeBatchedInputImmediatelyRunnable;
    boolean mConsumeBatchedInputImmediatelyScheduled;
    boolean mConsumeBatchedInputScheduled;
    final ConsumeBatchedInputRunnable mConsumedBatchedInputRunnable;
    int mContentCaptureEnabled;
    public final Context mContext;
    int mCurScrollY;
    View mCurrentDragView;
    private PointerIcon mCustomPointerIcon;
    private final int mDensity;
    private float mDesiredHdrSdrRatio;
    private Rect mDirty;
    int mDispatchedSystemBarAppearance;
    int mDispatchedSystemUiVisibility;
    Display mDisplay;
    boolean mDisplayDecorationCached;
    private final DisplayManager.DisplayListener mDisplayListener;
    final DisplayManager mDisplayManager;
    ClipDescription mDragDescription;
    final PointF mDragPoint;
    private boolean mDragResizing;
    boolean mDrawingAllowed;
    private boolean mDrewOnceForSync;
    private final Executor mExecutor;
    FallbackEventHandler mFallbackEventHandler;
    private boolean mFastScrollSoundEffectsEnabled;
    boolean mFirst;
    InputStage mFirstInputStage;
    InputStage mFirstPostImeInputStage;
    private boolean mForceDecorViewVisibility;
    private boolean mForceDisableBLAST;
    private boolean mForceNextConfigUpdate;
    boolean mForceNextWindowRelayout;
    private int mFpsNumFrames;
    private long mFpsPrevTime;
    private long mFpsStartTime;
    boolean mFullRedrawNeeded;
    private final ViewRootRectTracker mGestureExclusionTracker;
    final ViewRootHandler mHandler;
    boolean mHandlingLayoutInLayoutRequest;
    private final HandwritingInitiator mHandwritingInitiator;
    HardwareRendererObserver mHardwareRendererObserver;
    int mHardwareXOffset;
    int mHardwareYOffset;
    private boolean mHasPendingKeepClearAreaChange;
    boolean mHasPendingTransactions;
    private Consumer<Display> mHdrSdrRatioChangedListener;
    int mHeight;
    final HighContrastTextManager mHighContrastTextManager;
    private final ImeFocusController mImeFocusController;
    private boolean mInLayout;
    private final InputEventCompatProcessor mInputCompatProcessor;
    private final InputEventAssigner mInputEventAssigner;
    protected final InputEventConsistencyVerifier mInputEventConsistencyVerifier;
    private WindowInputEventReceiver mInputEventReceiver;
    final InputManager mInputManager;
    InputQueue mInputQueue;
    InputQueue.Callback mInputQueueCallback;
    private final InsetsController mInsetsController;
    private float mInvCompatScale;
    final InvalidateOnAnimationRunnable mInvalidateOnAnimationRunnable;
    private boolean mInvalidateRootRequested;
    boolean mIsAmbientMode;
    public boolean mIsAnimating;
    boolean mIsCreating;
    boolean mIsDrawing;
    boolean mIsInTraversal;
    private boolean mIsSurfaceOpaque;
    private Rect mKeepClearAccessibilityFocusRect;
    private final ViewRootRectTracker mKeepClearRectsTracker;
    private int mLastClickToolType;
    private final Configuration mLastConfigurationFromResources;
    final ViewTreeObserver.InternalInsetsInfo mLastGivenInsets;
    boolean mLastInCompatMode;
    private final Rect mLastLayoutFrame;
    String mLastPerformDrawSkippedReason;
    String mLastPerformTraversalsSkipDrawReason;
    String mLastReportNextDrawReason;
    private final MergedConfiguration mLastReportedMergedConfiguration;
    WeakReference<View> mLastScrolledFocus;
    private final Point mLastSurfaceSize;
    int mLastSyncSeqId;
    int mLastSystemUiVisibility;
    final PointF mLastTouchPoint;
    int mLastTouchSource;
    private int mLastTransformHint;
    private WindowInsets mLastWindowInsets;
    boolean mLayoutRequested;
    ArrayList<View> mLayoutRequesters;
    final IBinder mLeashToken;
    volatile Object mLocalDragState;
    final WindowLeaked mLocation;
    private int mMeasuredHeight;
    private int mMeasuredWidth;
    private boolean mNeedsRendererSetup;
    boolean mNewSurfaceNeeded;
    private final int mNoncompatDensity;
    private int mNumPausedForSync;
    private final WindowOnBackInvokedDispatcher mOnBackInvokedDispatcher;
    int mOrigWindowType;
    Rect mOverrideInsetsFrame;
    boolean mPausedForTransition;
    boolean mPendingAlwaysConsumeSystemBars;
    final Rect mPendingBackDropFrame;
    private boolean mPendingDragResizing;
    int mPendingInputEventCount;
    QueuedInputEvent mPendingInputEventHead;
    String mPendingInputEventQueueLengthCounterName;
    QueuedInputEvent mPendingInputEventTail;
    private final MergedConfiguration mPendingMergedConfiguration;
    private ArrayList<LayoutTransition> mPendingTransitions;
    boolean mPerformContentCapture;
    boolean mPointerCapture;
    private Integer mPointerIconType;
    Region mPreviousTouchableRegion;
    private int mPreviousTransformHint;
    final Region mPreviousTransparentRegion;
    boolean mProcessInputEventsScheduled;
    private boolean mProfile;
    private boolean mProfileRendering;
    private QueuedInputEvent mQueuedInputEventPool;
    private int mQueuedInputEventPoolSize;
    private Bundle mRelayoutBundle;
    private boolean mRelayoutRequested;
    private int mRelayoutSeq;
    private boolean mRemoved;
    private float mRenderHdrSdrRatio;
    private Choreographer.FrameCallback mRenderProfiler;
    private boolean mRenderProfilingEnabled;
    boolean mReportNextDraw;
    private HashSet<ScrollCaptureCallback> mRootScrollCaptureCallbacks;
    private long mScrollCaptureRequestTimeout;
    boolean mScrollMayChange;
    int mScrollY;
    Scroller mScroller;
    SendWindowContentChangedAccessibilityEvent mSendWindowContentChangedAccessibilityEvent;
    private final Executor mSimpleExecutor;
    int mSoftInputMode;
    View mStartedDragViewForA11y;
    boolean mStopped;
    public final Surface mSurface;
    private final ArrayList<SurfaceChangedCallback> mSurfaceChangedCallbacks;
    private final SurfaceControl mSurfaceControl;
    BaseSurfaceHolder mSurfaceHolder;
    SurfaceHolder.Callback2 mSurfaceHolderCallback;
    private int mSurfaceSequenceId;
    private final SurfaceSession mSurfaceSession;
    private final Point mSurfaceSize;
    private boolean mSyncBuffer;
    int mSyncSeqId;
    InputStage mSyntheticInputStage;
    private String mTag;
    final int mTargetSdkVersion;
    private final InsetsSourceControl.Array mTempControls;
    HashSet<View> mTempHashSet;
    private final InsetsState mTempInsets;
    private final Rect mTempRect;
    private final WindowConfiguration mTempWinConfig;
    final Thread mThread;
    private final ClientWindowFrames mTmpFrames;
    final int[] mTmpLocation;
    final TypedValue mTmpValue;
    Region mTouchableRegion;
    private final SurfaceControl.Transaction mTransaction;
    private ArrayList<AttachedSurfaceControl.OnBufferTransformHintChangedListener> mTransformHintListeners;
    CompatibilityInfo.Translator mTranslator;
    final Region mTransparentRegion;
    int mTraversalBarrier;
    final TraversalRunnable mTraversalRunnable;
    public boolean mTraversalScheduled;
    private int mTypesHiddenByFlags;
    boolean mUnbufferedInputDispatch;
    int mUnbufferedInputSource;
    private final UnhandledKeyManager mUnhandledKeyManager;
    private final ViewRootRectTracker mUnrestrictedKeepClearRectsTracker;
    boolean mUpcomingInTouchMode;
    boolean mUpcomingWindowFocus;
    private boolean mUpdateHdrSdrRatioInfo;
    private boolean mUpdateSurfaceNeeded;
    private boolean mUseBLASTAdapter;
    private boolean mUseMTRenderer;
    View mView;
    private final boolean mViewBoundsSandboxingEnabled;
    final ViewConfiguration mViewConfiguration;
    protected final ViewFrameInfo mViewFrameInfo;
    private int mViewLayoutDirectionInitial;
    private boolean mViewMeasureDeferred;
    int mViewVisibility;
    private final Rect mVisRect;
    int mWidth;
    boolean mWillDrawSoon;
    final Rect mWinFrame;
    private final Rect mWinFrameInScreen;
    final BinderC3573W mWindow;
    public final WindowManager.LayoutParams mWindowAttributes;
    boolean mWindowAttributesChanged;
    final ArrayList<WindowCallbacks> mWindowCallbacks;
    CountDownLatch mWindowDrawCountDown;
    boolean mWindowFocusChanged;
    private final WindowLayout mWindowLayout;
    final IWindowSession mWindowSession;
    private SurfaceSyncGroup mWmsRequestSyncGroup;
    int mWmsRequestSyncGroupState;
    public static final boolean CAPTION_ON_SHELL = SystemProperties.getBoolean("persist.wm.debug.caption_on_shell", true);
    public static final boolean LOCAL_LAYOUT = SystemProperties.getBoolean("persist.debug.local_layout", true);
    static final ThreadLocal<HandlerActionQueue> sRunQueues = new ThreadLocal<>();
    static final ArrayList<Runnable> sFirstDrawHandlers = new ArrayList<>();
    static boolean sFirstDrawComplete = false;
    private static final ArrayList<ConfigChangedCallback> sConfigCallbacks = new ArrayList<>();
    private static boolean sCompatibilityDone = false;
    static final Interpolator mResizeInterpolator = new AccelerateDecelerateInterpolator();
    private static final Object sSyncProgressLock = new Object();
    private static int sNumSyncsInProgress = 0;
    private static volatile boolean sAnrReported = false;
    static BLASTBufferQueue.TransactionHangCallback sTransactionHangCallback = new BLASTBufferQueue.TransactionHangCallback() { // from class: android.view.ViewRootImpl.1
        @Override // android.graphics.BLASTBufferQueue.TransactionHangCallback
        public void onTransactionHang(String reason) {
            if (ViewRootImpl.sAnrReported) {
                return;
            }
            ViewRootImpl.sAnrReported = true;
            try {
                ActivityManager.getService().appNotResponding(reason);
            } catch (RemoteException e) {
            }
        }
    };

    /* loaded from: classes4.dex */
    public interface ActivityConfigCallback {
        void onConfigurationChanged(Configuration configuration, int i);

        void requestCompatCameraControl(boolean z, boolean z2, ICompatCameraControlCallback iCompatCameraControlCallback);
    }

    /* loaded from: classes4.dex */
    public interface ConfigChangedCallback {
        void onConfigurationChanged(Configuration configuration);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public FrameInfo getUpdatedFrameInfo() {
        FrameInfo frameInfo = this.mChoreographer.mFrameInfo;
        this.mViewFrameInfo.populateFrameInfo(frameInfo);
        this.mViewFrameInfo.reset();
        this.mInputEventAssigner.notifyFrameProcessed();
        return frameInfo;
    }

    public ImeFocusController getImeFocusController() {
        return this.mImeFocusController;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes4.dex */
    public static final class SystemUiVisibilityInfo {
        int globalVisibility;
        int localChanges;
        int localValue;

        SystemUiVisibilityInfo() {
        }
    }

    public HandwritingInitiator getHandwritingInitiator() {
        return this.mHandwritingInitiator;
    }

    public ViewRootImpl(Context context, Display display) {
        this(context, display, WindowManagerGlobal.getWindowSession(), new WindowLayout());
    }

    public ViewRootImpl(Context context, Display display, IWindowSession session, WindowLayout windowLayout) {
        this.mTransformHintListeners = new ArrayList<>();
        this.mPreviousTransformHint = 0;
        this.mWindowCallbacks = new ArrayList<>();
        this.mTmpLocation = new int[2];
        this.mTmpValue = new TypedValue();
        this.mWindowAttributes = new WindowManager.LayoutParams();
        this.mAppVisible = true;
        this.mForceDecorViewVisibility = false;
        this.mOrigWindowType = -1;
        this.mStopped = false;
        this.mIsAmbientMode = false;
        this.mPausedForTransition = false;
        this.mLastInCompatMode = false;
        this.mViewFrameInfo = new ViewFrameInfo();
        this.mInputEventAssigner = new InputEventAssigner();
        this.mDisplayDecorationCached = false;
        this.mSurfaceSize = new Point();
        this.mLastSurfaceSize = new Point();
        this.mVisRect = new Rect();
        this.mTempRect = new Rect();
        this.mContentCaptureEnabled = 0;
        this.mSyncBuffer = false;
        this.mCheckIfCanDraw = false;
        this.mDrewOnceForSync = false;
        this.mSyncSeqId = 0;
        this.mLastSyncSeqId = 0;
        this.mUnbufferedInputSource = 0;
        this.mPendingInputEventQueueLengthCounterName = "pq";
        this.mUnhandledKeyManager = new UnhandledKeyManager();
        this.mWindowAttributesChanged = false;
        this.mSurface = new Surface();
        this.mSurfaceControl = new SurfaceControl();
        this.mUpdateHdrSdrRatioInfo = false;
        this.mDesiredHdrSdrRatio = 1.0f;
        this.mRenderHdrSdrRatio = 1.0f;
        this.mHdrSdrRatioChangedListener = null;
        this.mSurfaceSession = new SurfaceSession();
        this.mTransaction = new SurfaceControl.Transaction();
        this.mTmpFrames = new ClientWindowFrames();
        this.mPendingBackDropFrame = new Rect();
        this.mWinFrameInScreen = new Rect();
        this.mTempInsets = new InsetsState();
        this.mTempControls = new InsetsSourceControl.Array();
        this.mTempWinConfig = new WindowConfiguration();
        this.mInvCompatScale = 1.0f;
        this.mLastGivenInsets = new ViewTreeObserver.InternalInsetsInfo();
        this.mTypesHiddenByFlags = 0;
        this.mLastConfigurationFromResources = new Configuration();
        this.mLastReportedMergedConfiguration = new MergedConfiguration();
        this.mPendingMergedConfiguration = new MergedConfiguration();
        this.mDragPoint = new PointF();
        this.mLastTouchPoint = new PointF();
        this.mFpsStartTime = -1L;
        this.mFpsPrevTime = -1L;
        this.mPointerIconType = null;
        this.mCustomPointerIcon = null;
        this.mAccessibilityInteractionConnectionManager = new AccessibilityInteractionConnectionManager();
        this.mInLayout = false;
        this.mLayoutRequesters = new ArrayList<>();
        this.mHandlingLayoutInLayoutRequest = false;
        this.mInputEventConsistencyVerifier = InputEventConsistencyVerifier.isInstrumentationEnabled() ? new InputEventConsistencyVerifier(this, 0) : null;
        this.mBlurRegionAggregator = new BackgroundBlurDrawable.Aggregator(this);
        this.mGestureExclusionTracker = new ViewRootRectTracker(new Function() { // from class: android.view.ViewRootImpl$$ExternalSyntheticLambda4
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                List systemGestureExclusionRects;
                systemGestureExclusionRects = ((View) obj).getSystemGestureExclusionRects();
                return systemGestureExclusionRects;
            }
        });
        this.mKeepClearRectsTracker = new ViewRootRectTracker(new Function() { // from class: android.view.ViewRootImpl$$ExternalSyntheticLambda5
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                List collectPreferKeepClearRects;
                collectPreferKeepClearRects = ((View) obj).collectPreferKeepClearRects();
                return collectPreferKeepClearRects;
            }
        });
        this.mUnrestrictedKeepClearRectsTracker = new ViewRootRectTracker(new Function() { // from class: android.view.ViewRootImpl$$ExternalSyntheticLambda6
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                List collectUnrestrictedPreferKeepClearRects;
                collectUnrestrictedPreferKeepClearRects = ((View) obj).collectUnrestrictedPreferKeepClearRects();
                return collectUnrestrictedPreferKeepClearRects;
            }
        });
        this.mNumPausedForSync = 0;
        this.mScrollCaptureRequestTimeout = 2500L;
        this.mSurfaceSequenceId = 0;
        this.mLastTransformHint = Integer.MIN_VALUE;
        this.mRelayoutBundle = new Bundle();
        this.mChildBoundingInsets = new Rect();
        this.mChildBoundingInsetsChanged = false;
        this.mTag = TAG;
        this.mProfile = false;
        this.mDisplayListener = new DisplayManager.DisplayListener() { // from class: android.view.ViewRootImpl.3
            @Override // android.hardware.display.DisplayManager.DisplayListener
            public void onDisplayChanged(int displayId) {
                int oldDisplayState;
                int newDisplayState;
                if (ViewRootImpl.this.mView != null && ViewRootImpl.this.mDisplay.getDisplayId() == displayId && (oldDisplayState = ViewRootImpl.this.mAttachInfo.mDisplayState) != (newDisplayState = ViewRootImpl.this.mDisplay.getState())) {
                    ViewRootImpl.this.mAttachInfo.mDisplayState = newDisplayState;
                    ViewRootImpl.this.pokeDrawLockIfNeeded();
                    if (oldDisplayState != 0) {
                        int oldScreenState = toViewScreenState(oldDisplayState);
                        int newScreenState = toViewScreenState(newDisplayState);
                        if (oldScreenState != newScreenState) {
                            ViewRootImpl.this.mView.dispatchScreenStateChanged(newScreenState);
                        }
                        if (oldDisplayState == 1) {
                            ViewRootImpl.this.mFullRedrawNeeded = true;
                            ViewRootImpl.this.scheduleTraversals();
                        }
                    }
                }
            }

            @Override // android.hardware.display.DisplayManager.DisplayListener
            public void onDisplayRemoved(int displayId) {
            }

            @Override // android.hardware.display.DisplayManager.DisplayListener
            public void onDisplayAdded(int displayId) {
            }

            private int toViewScreenState(int displayState) {
                if (displayState != 1) {
                    return 1;
                }
                return 0;
            }
        };
        this.mSurfaceChangedCallbacks = new ArrayList<>();
        ViewRootHandler viewRootHandler = new ViewRootHandler();
        this.mHandler = viewRootHandler;
        this.mExecutor = new Executor() { // from class: android.view.ViewRootImpl$$ExternalSyntheticLambda7
            @Override // java.util.concurrent.Executor
            public final void execute(Runnable runnable) {
                ViewRootImpl.this.lambda$new$9(runnable);
            }
        };
        this.mTraversalRunnable = new TraversalRunnable();
        this.mConsumedBatchedInputRunnable = new ConsumeBatchedInputRunnable();
        this.mConsumeBatchedInputImmediatelyRunnable = new ConsumeBatchedInputImmediatelyRunnable();
        this.mInvalidateOnAnimationRunnable = new InvalidateOnAnimationRunnable();
        this.mSimpleExecutor = new PendingIntent$$ExternalSyntheticLambda1();
        this.mContext = context;
        this.mWindowSession = session;
        this.mWindowLayout = windowLayout;
        this.mDisplay = display;
        this.mBasePackageName = context.getBasePackageName();
        this.mThread = Thread.currentThread();
        WindowLeaked windowLeaked = new WindowLeaked(null);
        this.mLocation = windowLeaked;
        windowLeaked.fillInStackTrace();
        this.mWidth = -1;
        this.mHeight = -1;
        this.mDirty = new Rect();
        this.mWinFrame = new Rect();
        this.mLastLayoutFrame = new Rect();
        BinderC3573W binderC3573W = new BinderC3573W(this);
        this.mWindow = binderC3573W;
        this.mLeashToken = new Binder();
        this.mTargetSdkVersion = context.getApplicationInfo().targetSdkVersion;
        this.mViewVisibility = 8;
        this.mTransparentRegion = new Region();
        this.mPreviousTransparentRegion = new Region();
        this.mFirst = true;
        this.mPerformContentCapture = true;
        this.mAdded = false;
        this.mAttachInfo = new View.AttachInfo(session, binderC3573W, display, this, viewRootHandler, this, context);
        this.mCompatibleVisibilityInfo = new SystemUiVisibilityInfo();
        this.mAccessibilityManager = AccessibilityManager.getInstance(context);
        this.mHighContrastTextManager = new HighContrastTextManager();
        ViewConfiguration viewConfiguration = ViewConfiguration.get(context);
        this.mViewConfiguration = viewConfiguration;
        this.mDensity = context.getResources().getDisplayMetrics().densityDpi;
        this.mNoncompatDensity = context.getResources().getDisplayMetrics().noncompatDensityDpi;
        this.mFallbackEventHandler = new PhoneFallbackEventHandler(context);
        this.mChoreographer = Choreographer.getInstance();
        this.mDisplayManager = (DisplayManager) context.getSystemService(Context.DISPLAY_SERVICE);
        this.mInputManager = (InputManager) context.getSystemService(InputManager.class);
        this.mInsetsController = new InsetsController(new ViewRootInsetsControllerHost(this));
        this.mHandwritingInitiator = new HandwritingInitiator(viewConfiguration, (InputMethodManager) context.getSystemService(InputMethodManager.class));
        this.mViewBoundsSandboxingEnabled = getViewBoundsSandboxingEnabled();
        String processorOverrideName = context.getResources().getString(C4057R.string.config_inputEventCompatProcessorOverrideClassName);
        if (processorOverrideName.isEmpty()) {
            this.mInputCompatProcessor = new InputEventCompatProcessor(context);
        } else {
            InputEventCompatProcessor compatProcessor = null;
            try {
                compatProcessor = (InputEventCompatProcessor) Class.forName(processorOverrideName).getConstructor(Context.class).newInstance(context);
            } catch (Exception e) {
                Log.m109e(TAG, "Unable to create the InputEventCompatProcessor. ", e);
            } finally {
                this.mInputCompatProcessor = compatProcessor;
            }
        }
        if (!sCompatibilityDone) {
            sAlwaysAssignFocus = this.mTargetSdkVersion < 28;
            sCompatibilityDone = true;
        }
        loadSystemProperties();
        this.mImeFocusController = new ImeFocusController(this);
        AudioManager audioManager = (AudioManager) this.mContext.getSystemService(AudioManager.class);
        this.mFastScrollSoundEffectsEnabled = audioManager.areNavigationRepeatSoundEffectsEnabled();
        this.mScrollCaptureRequestTimeout = 2500L;
        this.mOnBackInvokedDispatcher = new WindowOnBackInvokedDispatcher(context);
    }

    public static void addFirstDrawHandler(Runnable callback) {
        ArrayList<Runnable> arrayList = sFirstDrawHandlers;
        synchronized (arrayList) {
            if (!sFirstDrawComplete) {
                arrayList.add(callback);
            }
        }
    }

    public static void addConfigCallback(ConfigChangedCallback callback) {
        ArrayList<ConfigChangedCallback> arrayList = sConfigCallbacks;
        synchronized (arrayList) {
            arrayList.add(callback);
        }
    }

    public static void removeConfigCallback(ConfigChangedCallback callback) {
        ArrayList<ConfigChangedCallback> arrayList = sConfigCallbacks;
        synchronized (arrayList) {
            arrayList.remove(callback);
        }
    }

    public void setActivityConfigCallback(ActivityConfigCallback callback) {
        this.mActivityConfigCallback = callback;
    }

    public void setOnContentApplyWindowInsetsListener(Window.OnContentApplyWindowInsetsListener listener) {
        this.mAttachInfo.mContentOnApplyWindowInsetsListener = listener;
        if (!this.mFirst) {
            requestFitSystemWindows();
        }
    }

    public void addWindowCallbacks(WindowCallbacks callback) {
        this.mWindowCallbacks.add(callback);
    }

    public void removeWindowCallbacks(WindowCallbacks callback) {
        this.mWindowCallbacks.remove(callback);
    }

    public void reportDrawFinish() {
        CountDownLatch countDownLatch = this.mWindowDrawCountDown;
        if (countDownLatch != null) {
            countDownLatch.countDown();
        }
    }

    public void profile() {
        this.mProfile = true;
    }

    private boolean isInTouchMode() {
        IWindowManager windowManager = WindowManagerGlobal.getWindowManagerService();
        if (windowManager != null) {
            try {
                return windowManager.isInTouchMode(getDisplayId());
            } catch (RemoteException e) {
                return false;
            }
        }
        return false;
    }

    public void notifyChildRebuilt() {
        if (this.mView instanceof RootViewSurfaceTaker) {
            SurfaceHolder.Callback2 callback2 = this.mSurfaceHolderCallback;
            if (callback2 != null) {
                this.mSurfaceHolder.removeCallback(callback2);
            }
            SurfaceHolder.Callback2 willYouTakeTheSurface = ((RootViewSurfaceTaker) this.mView).willYouTakeTheSurface();
            this.mSurfaceHolderCallback = willYouTakeTheSurface;
            if (willYouTakeTheSurface != null) {
                TakenSurfaceHolder takenSurfaceHolder = new TakenSurfaceHolder();
                this.mSurfaceHolder = takenSurfaceHolder;
                takenSurfaceHolder.setFormat(0);
                this.mSurfaceHolder.addCallback(this.mSurfaceHolderCallback);
            } else {
                this.mSurfaceHolder = null;
            }
            InputQueue.Callback willYouTakeTheInputQueue = ((RootViewSurfaceTaker) this.mView).willYouTakeTheInputQueue();
            this.mInputQueueCallback = willYouTakeTheInputQueue;
            if (willYouTakeTheInputQueue != null) {
                willYouTakeTheInputQueue.onInputQueueCreated(this.mInputQueue);
            }
        }
        updateLastConfigurationFromResources(getConfiguration());
        reportNextDraw("rebuilt");
        if (this.mStopped) {
            setWindowStopped(false);
        }
    }

    private Configuration getConfiguration() {
        return this.mContext.getResources().getConfiguration();
    }

    private WindowConfiguration getCompatWindowConfiguration() {
        WindowConfiguration winConfig = getConfiguration().windowConfiguration;
        if (this.mInvCompatScale == 1.0f) {
            return winConfig;
        }
        this.mTempWinConfig.setTo(winConfig);
        this.mTempWinConfig.scale(this.mInvCompatScale);
        return this.mTempWinConfig;
    }

    public void setView(View view, WindowManager.LayoutParams attrs, View panelParentView) {
        setView(view, attrs, panelParentView, UserHandle.myUserId());
    }

    public void setView(View view, WindowManager.LayoutParams attrs, View panelParentView, int userId) {
        boolean restore;
        InputChannel inputChannel;
        Rect attachedFrame;
        PendingInsetsController pendingInsetsController;
        synchronized (this) {
            try {
                try {
                    if (this.mView == null) {
                        this.mView = view;
                        this.mViewLayoutDirectionInitial = view.getRawLayoutDirection();
                        this.mFallbackEventHandler.setView(view);
                        try {
                            this.mWindowAttributes.copyFrom(attrs);
                            if (this.mWindowAttributes.packageName == null) {
                                this.mWindowAttributes.packageName = this.mBasePackageName;
                            }
                            this.mWindowAttributes.privateFlags |= 33554432;
                            WindowManager.LayoutParams attrs2 = this.mWindowAttributes;
                            setTag();
                            this.mClientWindowLayoutFlags = attrs2.flags;
                            setAccessibilityFocus(null, null);
                            if (view instanceof RootViewSurfaceTaker) {
                                SurfaceHolder.Callback2 willYouTakeTheSurface = ((RootViewSurfaceTaker) view).willYouTakeTheSurface();
                                this.mSurfaceHolderCallback = willYouTakeTheSurface;
                                if (willYouTakeTheSurface != null) {
                                    TakenSurfaceHolder takenSurfaceHolder = new TakenSurfaceHolder();
                                    this.mSurfaceHolder = takenSurfaceHolder;
                                    takenSurfaceHolder.setFormat(0);
                                    this.mSurfaceHolder.addCallback(this.mSurfaceHolderCallback);
                                }
                            }
                            if (!attrs2.hasManualSurfaceInsets) {
                                attrs2.setSurfaceInsets(view, false, true);
                            }
                            CompatibilityInfo compatibilityInfo = this.mDisplay.getDisplayAdjustments().getCompatibilityInfo();
                            this.mTranslator = compatibilityInfo.getTranslator();
                            if (this.mSurfaceHolder == null) {
                                enableHardwareAcceleration(attrs2);
                                boolean useMTRenderer = this.mAttachInfo.mThreadedRenderer != null;
                                if (this.mUseMTRenderer != useMTRenderer) {
                                    endDragResizing();
                                    this.mUseMTRenderer = useMTRenderer;
                                }
                            }
                            CompatibilityInfo.Translator translator = this.mTranslator;
                            if (translator == null) {
                                restore = false;
                            } else {
                                this.mSurface.setCompatibilityTranslator(translator);
                                attrs2.backup();
                                this.mTranslator.translateWindowLayout(attrs2);
                                restore = true;
                            }
                            boolean restore2 = compatibilityInfo.supportsScreen();
                            if (!restore2) {
                                attrs2.privateFlags |= 128;
                                this.mLastInCompatMode = true;
                            }
                            this.mSoftInputMode = attrs2.softInputMode;
                            this.mWindowAttributesChanged = true;
                            this.mAttachInfo.mRootView = view;
                            this.mAttachInfo.mScalingRequired = this.mTranslator != null;
                            View.AttachInfo attachInfo = this.mAttachInfo;
                            CompatibilityInfo.Translator translator2 = this.mTranslator;
                            attachInfo.mApplicationScale = translator2 == null ? 1.0f : translator2.applicationScale;
                            if (panelParentView != null) {
                                this.mAttachInfo.mPanelParentWindowToken = panelParentView.getApplicationWindowToken();
                            }
                            this.mAdded = true;
                            requestLayout();
                            if ((this.mWindowAttributes.inputFeatures & 1) != 0) {
                                inputChannel = null;
                            } else {
                                inputChannel = new InputChannel();
                            }
                            this.mForceDecorViewVisibility = (this.mWindowAttributes.privateFlags & 16384) != 0;
                            View view2 = this.mView;
                            if ((view2 instanceof RootViewSurfaceTaker) && (pendingInsetsController = ((RootViewSurfaceTaker) view2).providePendingInsetsController()) != null) {
                                pendingInsetsController.replayAndAttach(this.mInsetsController);
                            }
                            try {
                                this.mOrigWindowType = this.mWindowAttributes.type;
                                this.mAttachInfo.mRecomputeGlobalAttributes = true;
                                collectViewAttributes();
                                adjustLayoutParamsForCompatibility(this.mWindowAttributes);
                                controlInsetsForCompatibility(this.mWindowAttributes);
                                Rect attachedFrame2 = new Rect();
                                float[] compatScale = {1.0f};
                                int res = this.mWindowSession.addToDisplayAsUser(this.mWindow, this.mWindowAttributes, getHostVisibility(), this.mDisplay.getDisplayId(), userId, this.mInsetsController.getRequestedVisibleTypes(), inputChannel, this.mTempInsets, this.mTempControls, attachedFrame2, compatScale);
                                if (attachedFrame2.isValid()) {
                                    attachedFrame = attachedFrame2;
                                } else {
                                    attachedFrame = null;
                                }
                                CompatibilityInfo.Translator translator3 = this.mTranslator;
                                if (translator3 != null) {
                                    translator3.translateInsetsStateInScreenToAppWindow(this.mTempInsets);
                                    this.mTranslator.translateSourceControlsInScreenToAppWindow(this.mTempControls.get());
                                    this.mTranslator.translateRectInScreenToAppWindow(attachedFrame);
                                }
                                this.mTmpFrames.attachedFrame = attachedFrame;
                                this.mTmpFrames.compatScale = compatScale[0];
                                this.mInvCompatScale = 1.0f / compatScale[0];
                                if (restore) {
                                    attrs2.restore();
                                }
                                this.mAttachInfo.mAlwaysConsumeSystemBars = (res & 4) != 0;
                                this.mPendingAlwaysConsumeSystemBars = this.mAttachInfo.mAlwaysConsumeSystemBars;
                                this.mInsetsController.onStateChanged(this.mTempInsets);
                                this.mInsetsController.onControlsChanged(this.mTempControls.get());
                                InsetsState state = this.mInsetsController.getState();
                                Rect displayCutoutSafe = this.mTempRect;
                                state.getDisplayCutoutSafe(displayCutoutSafe);
                                WindowConfiguration winConfig = getCompatWindowConfiguration();
                                this.mWindowLayout.computeFrames(this.mWindowAttributes, state, displayCutoutSafe, winConfig.getBounds(), winConfig.getWindowingMode(), -1, -1, this.mInsetsController.getRequestedVisibleTypes(), 1.0f, this.mTmpFrames);
                                setFrame(this.mTmpFrames.frame, true);
                                registerBackCallbackOnWindow();
                                if (res < 0) {
                                    this.mAttachInfo.mRootView = null;
                                    this.mAdded = false;
                                    this.mFallbackEventHandler.setView(null);
                                    unscheduleTraversals();
                                    setAccessibilityFocus(null, null);
                                    switch (res) {
                                        case -11:
                                            throw new WindowManager.BadTokenException("Unable to add Window " + this.mWindow + " -- requested userId is not valid");
                                        case -10:
                                            throw new WindowManager.InvalidDisplayException("Unable to add window " + this.mWindow + " -- the specified window type " + this.mWindowAttributes.type + " is not valid");
                                        case -9:
                                            throw new WindowManager.InvalidDisplayException("Unable to add window " + this.mWindow + " -- the specified display can not be found");
                                        case -8:
                                            throw new WindowManager.BadTokenException("Unable to add window " + this.mWindow + " -- permission denied for window type " + this.mWindowAttributes.type);
                                        case -7:
                                            throw new WindowManager.BadTokenException("Unable to add window " + this.mWindow + " -- another window of type " + this.mWindowAttributes.type + " already exists");
                                        case -6:
                                            return;
                                        case -5:
                                            throw new WindowManager.BadTokenException("Unable to add window -- window " + this.mWindow + " has already been added");
                                        case -4:
                                            throw new WindowManager.BadTokenException("Unable to add window -- app for token " + attrs2.token + " is exiting");
                                        case -3:
                                            throw new WindowManager.BadTokenException("Unable to add window -- token " + attrs2.token + " is not for an application");
                                        case -2:
                                        case -1:
                                            throw new WindowManager.BadTokenException("Unable to add window -- token " + attrs2.token + " is not valid; is your activity running?");
                                        default:
                                            throw new RuntimeException("Unable to add window -- unknown error code " + res);
                                    }
                                }
                                registerListeners();
                                this.mAttachInfo.mDisplayState = this.mDisplay.getState();
                                if ((res & 8) != 0) {
                                    this.mUseBLASTAdapter = true;
                                }
                                if (view instanceof RootViewSurfaceTaker) {
                                    this.mInputQueueCallback = ((RootViewSurfaceTaker) view).willYouTakeTheInputQueue();
                                }
                                if (inputChannel != null) {
                                    if (this.mInputQueueCallback != null) {
                                        InputQueue inputQueue = new InputQueue();
                                        this.mInputQueue = inputQueue;
                                        this.mInputQueueCallback.onInputQueueCreated(inputQueue);
                                    }
                                    this.mInputEventReceiver = new WindowInputEventReceiver(inputChannel, Looper.myLooper());
                                    if (this.mAttachInfo.mThreadedRenderer != null) {
                                        InputMetricsListener listener = new InputMetricsListener();
                                        this.mHardwareRendererObserver = new HardwareRendererObserver(listener, listener.data, this.mHandler, true);
                                        this.mAttachInfo.mThreadedRenderer.addObserver(this.mHardwareRendererObserver);
                                    }
                                    this.mUnbufferedInputSource = this.mView.mUnbufferedInputSource;
                                }
                                view.assignParent(this);
                                this.mAddedTouchMode = (res & 1) != 0;
                                this.mAppVisible = (res & 2) != 0;
                                if (this.mAccessibilityManager.isEnabled()) {
                                    this.mAccessibilityInteractionConnectionManager.ensureConnection();
                                    setAccessibilityWindowAttributesIfNeeded();
                                }
                                if (view.getImportantForAccessibility() == 0) {
                                    view.setImportantForAccessibility(1);
                                }
                                CharSequence counterSuffix = attrs2.getTitle();
                                SyntheticInputStage syntheticInputStage = new SyntheticInputStage();
                                this.mSyntheticInputStage = syntheticInputStage;
                                InputStage viewPostImeStage = new ViewPostImeInputStage(syntheticInputStage);
                                InputStage nativePostImeStage = new NativePostImeInputStage(viewPostImeStage, "aq:native-post-ime:" + ((Object) counterSuffix));
                                InputStage earlyPostImeStage = new EarlyPostImeInputStage(nativePostImeStage);
                                InputStage imeStage = new ImeInputStage(earlyPostImeStage, "aq:ime:" + ((Object) counterSuffix));
                                InputStage viewPreImeStage = new ViewPreImeInputStage(imeStage);
                                InputStage nativePreImeStage = new NativePreImeInputStage(viewPreImeStage, "aq:native-pre-ime:" + ((Object) counterSuffix));
                                this.mFirstInputStage = nativePreImeStage;
                                this.mFirstPostImeInputStage = earlyPostImeStage;
                                this.mPendingInputEventQueueLengthCounterName = "aq:pending:" + ((Object) counterSuffix);
                                if (!this.mRemoved || !this.mAppVisible) {
                                    AnimationHandler.requestAnimatorsEnabled(this.mAppVisible, this);
                                }
                            } catch (RemoteException | RuntimeException e) {
                                this.mAdded = false;
                                this.mView = null;
                                this.mAttachInfo.mRootView = null;
                                this.mFallbackEventHandler.setView(null);
                                unscheduleTraversals();
                                setAccessibilityFocus(null, null);
                                throw new RuntimeException("Adding window failed", e);
                            }
                        } catch (Throwable th) {
                            th = th;
                            throw th;
                        }
                    }
                } catch (Throwable th2) {
                    th = th2;
                }
            } catch (Throwable th3) {
                th = th3;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setAccessibilityWindowAttributesIfNeeded() {
        boolean registered = this.mAttachInfo.mAccessibilityWindowId != -1;
        if (registered) {
            AccessibilityWindowAttributes attributes = new AccessibilityWindowAttributes(this.mWindowAttributes, this.mContext.getResources().getConfiguration().getLocales());
            if (!attributes.equals(this.mAccessibilityWindowAttributes)) {
                this.mAccessibilityWindowAttributes = attributes;
                this.mAccessibilityManager.setAccessibilityWindowAttributes(getDisplayId(), this.mAttachInfo.mAccessibilityWindowId, attributes);
            }
        }
    }

    private void registerListeners() {
        this.mAccessibilityManager.addAccessibilityStateChangeListener(this.mAccessibilityInteractionConnectionManager, this.mHandler);
        this.mAccessibilityManager.addHighTextContrastStateChangeListener(this.mHighContrastTextManager, this.mHandler);
        this.mDisplayManager.registerDisplayListener(this.mDisplayListener, this.mHandler);
    }

    private void unregisterListeners() {
        this.mAccessibilityManager.removeAccessibilityStateChangeListener(this.mAccessibilityInteractionConnectionManager);
        this.mAccessibilityManager.removeHighTextContrastStateChangeListener(this.mHighContrastTextManager);
        this.mDisplayManager.unregisterDisplayListener(this.mDisplayListener);
    }

    private void setTag() {
        String[] split = this.mWindowAttributes.getTitle().toString().split("\\.");
        if (split.length > 0) {
            this.mTag = "VRI[" + split[split.length - 1] + NavigationBarInflaterView.SIZE_MOD_END;
        }
    }

    public int getWindowFlags() {
        return this.mWindowAttributes.flags;
    }

    public int getDisplayId() {
        return this.mDisplay.getDisplayId();
    }

    public CharSequence getTitle() {
        return this.mWindowAttributes.getTitle();
    }

    public int getWidth() {
        return this.mWidth;
    }

    public int getHeight() {
        return this.mHeight;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void destroyHardwareResources() {
        ThreadedRenderer renderer = this.mAttachInfo.mThreadedRenderer;
        if (renderer != null) {
            if (Looper.myLooper() != this.mAttachInfo.mHandler.getLooper()) {
                this.mAttachInfo.mHandler.postAtFrontOfQueue(new Runnable() { // from class: android.view.ViewRootImpl$$ExternalSyntheticLambda16
                    @Override // java.lang.Runnable
                    public final void run() {
                        ViewRootImpl.this.destroyHardwareResources();
                    }
                });
                return;
            }
            renderer.destroyHardwareResources(this.mView);
            renderer.destroy();
        }
    }

    public void detachFunctor(long functor) {
    }

    public static void invokeFunctor(long functor, boolean waitForCompletion) {
    }

    public void registerAnimatingRenderNode(RenderNode animator) {
        if (this.mAttachInfo.mThreadedRenderer != null) {
            this.mAttachInfo.mThreadedRenderer.registerAnimatingRenderNode(animator);
            return;
        }
        if (this.mAttachInfo.mPendingAnimatingRenderNodes == null) {
            this.mAttachInfo.mPendingAnimatingRenderNodes = new ArrayList();
        }
        this.mAttachInfo.mPendingAnimatingRenderNodes.add(animator);
    }

    public void registerVectorDrawableAnimator(NativeVectorDrawableAnimator animator) {
        if (this.mAttachInfo.mThreadedRenderer != null) {
            this.mAttachInfo.mThreadedRenderer.registerVectorDrawableAnimator(animator);
        }
    }

    public void registerRtFrameCallback(final HardwareRenderer.FrameDrawingCallback callback) {
        if (this.mAttachInfo.mThreadedRenderer != null) {
            this.mAttachInfo.mThreadedRenderer.registerRtFrameCallback(new HardwareRenderer.FrameDrawingCallback() { // from class: android.view.ViewRootImpl.2
                @Override // android.graphics.HardwareRenderer.FrameDrawingCallback
                public void onFrameDraw(long frame) {
                }

                @Override // android.graphics.HardwareRenderer.FrameDrawingCallback
                public HardwareRenderer.FrameCommitCallback onFrameDraw(int syncResult, long frame) {
                    try {
                        return callback.onFrameDraw(syncResult, frame);
                    } catch (Exception e) {
                        Log.m109e(ViewRootImpl.TAG, "Exception while executing onFrameDraw", e);
                        return null;
                    }
                }
            });
        }
    }

    private void enableHardwareAcceleration(WindowManager.LayoutParams attrs) {
        boolean translucent = false;
        this.mAttachInfo.mHardwareAccelerated = false;
        this.mAttachInfo.mHardwareAccelerationRequested = false;
        if (this.mTranslator != null) {
            return;
        }
        boolean hardwareAccelerated = (attrs.flags & 16777216) != 0;
        if (hardwareAccelerated) {
            boolean forceHwAccelerated = (attrs.privateFlags & 2) != 0;
            if (ThreadedRenderer.sRendererEnabled || forceHwAccelerated) {
                if (this.mAttachInfo.mThreadedRenderer != null) {
                    this.mAttachInfo.mThreadedRenderer.destroy();
                }
                Rect insets = attrs.surfaceInsets;
                boolean hasSurfaceInsets = (insets.left == 0 && insets.right == 0 && insets.top == 0 && insets.bottom == 0) ? false : true;
                if (attrs.format != -1 || hasSurfaceInsets) {
                    translucent = true;
                }
                ThreadedRenderer renderer = ThreadedRenderer.create(this.mContext, translucent, attrs.getTitle().toString());
                this.mAttachInfo.mThreadedRenderer = renderer;
                renderer.setSurfaceControl(this.mSurfaceControl, this.mBlastBufferQueue);
                updateColorModeIfNeeded(attrs.getColorMode());
                updateRenderHdrSdrRatio();
                updateForceDarkMode();
                this.mAttachInfo.mHardwareAccelerated = true;
                this.mAttachInfo.mHardwareAccelerationRequested = true;
                HardwareRendererObserver hardwareRendererObserver = this.mHardwareRendererObserver;
                if (hardwareRendererObserver != null) {
                    renderer.addObserver(hardwareRendererObserver);
                }
            }
        }
    }

    private int getNightMode() {
        return getConfiguration().uiMode & 48;
    }

    private void updateForceDarkMode() {
        if (this.mAttachInfo.mThreadedRenderer == null) {
            return;
        }
        boolean z = true;
        boolean useAutoDark = getNightMode() == 32;
        if (useAutoDark) {
            boolean forceDarkAllowedDefault = SystemProperties.getBoolean(ThreadedRenderer.DEBUG_FORCE_DARK, false);
            TypedArray a = this.mContext.obtainStyledAttributes(C4057R.styleable.Theme);
            if (!a.getBoolean(279, true) || !a.getBoolean(278, forceDarkAllowedDefault)) {
                z = false;
            }
            useAutoDark = z;
            a.recycle();
        }
        if (this.mAttachInfo.mThreadedRenderer.setForceDark(useAutoDark)) {
            invalidateWorld(this.mView);
        }
    }

    public View getView() {
        return this.mView;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final WindowLeaked getLocation() {
        return this.mLocation;
    }

    public void setLayoutParams(WindowManager.LayoutParams attrs, boolean newView) {
        int oldInsetLeft;
        synchronized (this) {
            int oldInsetLeft2 = this.mWindowAttributes.surfaceInsets.left;
            int oldInsetTop = this.mWindowAttributes.surfaceInsets.top;
            int oldInsetRight = this.mWindowAttributes.surfaceInsets.right;
            int oldInsetBottom = this.mWindowAttributes.surfaceInsets.bottom;
            int oldSoftInputMode = this.mWindowAttributes.softInputMode;
            boolean oldHasManualSurfaceInsets = this.mWindowAttributes.hasManualSurfaceInsets;
            this.mClientWindowLayoutFlags = attrs.flags;
            int compatibleWindowFlag = this.mWindowAttributes.privateFlags & 128;
            int systemUiVisibility = this.mWindowAttributes.systemUiVisibility;
            int subtreeSystemUiVisibility = this.mWindowAttributes.subtreeSystemUiVisibility;
            int appearance = this.mWindowAttributes.insetsFlags.appearance;
            int behavior = this.mWindowAttributes.insetsFlags.behavior;
            int appearanceAndBehaviorPrivateFlags = this.mWindowAttributes.privateFlags & Enums.AUDIO_FORMAT_DTS_HD;
            int changes = this.mWindowAttributes.copyFrom(attrs);
            if ((524288 & changes) != 0) {
                this.mAttachInfo.mRecomputeGlobalAttributes = true;
            }
            if ((changes & 1) != 0) {
                this.mAttachInfo.mNeedsUpdateLightCenter = true;
            }
            if (this.mWindowAttributes.packageName == null) {
                this.mWindowAttributes.packageName = this.mBasePackageName;
            }
            this.mWindowAttributes.systemUiVisibility = systemUiVisibility;
            this.mWindowAttributes.subtreeSystemUiVisibility = subtreeSystemUiVisibility;
            this.mWindowAttributes.insetsFlags.appearance = appearance;
            this.mWindowAttributes.insetsFlags.behavior = behavior;
            this.mWindowAttributes.privateFlags |= compatibleWindowFlag | appearanceAndBehaviorPrivateFlags | 33554432;
            if (this.mWindowAttributes.preservePreviousSurfaceInsets) {
                this.mWindowAttributes.surfaceInsets.set(oldInsetLeft2, oldInsetTop, oldInsetRight, oldInsetBottom);
                this.mWindowAttributes.hasManualSurfaceInsets = oldHasManualSurfaceInsets;
            } else if (this.mWindowAttributes.surfaceInsets.left != oldInsetLeft2 || this.mWindowAttributes.surfaceInsets.top != oldInsetTop || this.mWindowAttributes.surfaceInsets.right != oldInsetRight || this.mWindowAttributes.surfaceInsets.bottom != oldInsetBottom) {
                this.mNeedsRendererSetup = true;
            }
            applyKeepScreenOnFlag(this.mWindowAttributes);
            if (newView) {
                this.mSoftInputMode = attrs.softInputMode;
                requestLayout();
            }
            if ((attrs.softInputMode & 240) == 0) {
                WindowManager.LayoutParams layoutParams = this.mWindowAttributes;
                oldInsetLeft = oldSoftInputMode;
                layoutParams.softInputMode = (oldInsetLeft & 240) | (layoutParams.softInputMode & (-241));
            } else {
                oldInsetLeft = oldSoftInputMode;
            }
            if (this.mWindowAttributes.softInputMode != oldInsetLeft) {
                requestFitSystemWindows();
            }
            this.mWindowAttributesChanged = true;
            scheduleTraversals();
            setAccessibilityWindowAttributesIfNeeded();
        }
    }

    void handleAppVisibility(boolean visible) {
        if (this.mAppVisible != visible) {
            boolean previousVisible = getHostVisibility() == 0;
            this.mAppVisible = visible;
            boolean currentVisible = getHostVisibility() == 0;
            if (previousVisible != currentVisible) {
                this.mAppVisibilityChanged = true;
                scheduleTraversals();
            }
            if (!this.mRemoved || !this.mAppVisible) {
                AnimationHandler.requestAnimatorsEnabled(this.mAppVisible, this);
            }
        }
    }

    void handleGetNewSurface() {
        this.mNewSurfaceNeeded = true;
        this.mFullRedrawNeeded = true;
        scheduleTraversals();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleResized(int msg, SomeArgs args) {
        boolean compatScaleChanged;
        boolean z;
        if (!this.mAdded) {
            return;
        }
        ClientWindowFrames frames = (ClientWindowFrames) args.arg1;
        MergedConfiguration mergedConfiguration = (MergedConfiguration) args.arg2;
        CompatibilityInfo.applyOverrideScaleIfNeeded(mergedConfiguration);
        boolean forceNextWindowRelayout = args.argi1 != 0;
        int displayId = args.argi3;
        boolean dragResizing = args.argi5 != 0;
        Rect frame = frames.frame;
        Rect displayFrame = frames.displayFrame;
        Rect attachedFrame = frames.attachedFrame;
        CompatibilityInfo.Translator translator = this.mTranslator;
        if (translator != null) {
            translator.translateRectInScreenToAppWindow(frame);
            this.mTranslator.translateRectInScreenToAppWindow(displayFrame);
            this.mTranslator.translateRectInScreenToAppWindow(attachedFrame);
        }
        float compatScale = frames.compatScale;
        boolean frameChanged = !this.mWinFrame.equals(frame);
        boolean configChanged = !this.mLastReportedMergedConfiguration.equals(mergedConfiguration);
        boolean attachedFrameChanged = LOCAL_LAYOUT && !Objects.equals(this.mTmpFrames.attachedFrame, attachedFrame);
        boolean displayChanged = this.mDisplay.getDisplayId() != displayId;
        boolean compatScaleChanged2 = this.mTmpFrames.compatScale != compatScale;
        if (msg == 4 && !frameChanged && !configChanged && !attachedFrameChanged && !displayChanged && !forceNextWindowRelayout && !compatScaleChanged2) {
            return;
        }
        this.mPendingDragResizing = dragResizing;
        this.mTmpFrames.compatScale = compatScale;
        this.mInvCompatScale = 1.0f / compatScale;
        if (configChanged) {
            compatScaleChanged = false;
            performConfigurationChange(mergedConfiguration, false, displayChanged ? displayId : -1);
        } else {
            compatScaleChanged = false;
            if (displayChanged) {
                onMovedToDisplay(displayId, this.mLastConfigurationFromResources);
            }
        }
        setFrame(frame, compatScaleChanged);
        this.mTmpFrames.displayFrame.set(displayFrame);
        if (this.mTmpFrames.attachedFrame != null && attachedFrame != null) {
            this.mTmpFrames.attachedFrame.set(attachedFrame);
        }
        if (this.mDragResizing && this.mUseMTRenderer) {
            boolean fullscreen = frame.equals(this.mPendingBackDropFrame);
            z = true;
            int i = this.mWindowCallbacks.size() - 1;
            while (i >= 0) {
                this.mWindowCallbacks.get(i).onWindowSizeIsChanging(this.mPendingBackDropFrame, fullscreen, this.mAttachInfo.mVisibleInsets, this.mAttachInfo.mStableInsets);
                i--;
                mergedConfiguration = mergedConfiguration;
                displayChanged = displayChanged;
                attachedFrameChanged = attachedFrameChanged;
                displayId = displayId;
            }
        } else {
            z = true;
        }
        this.mForceNextWindowRelayout |= forceNextWindowRelayout;
        this.mPendingAlwaysConsumeSystemBars = args.argi2 != 0 ? z : false;
        int i2 = args.argi4;
        int i3 = this.mSyncSeqId;
        if (i2 > i3) {
            i3 = args.argi4;
        }
        this.mSyncSeqId = i3;
        if (msg == 5) {
            reportNextDraw("resized");
        }
        View view = this.mView;
        if (view != null && (frameChanged || configChanged)) {
            forceLayout(view);
        }
        requestLayout();
    }

    public void onMovedToDisplay(int displayId, Configuration config) {
        if (this.mDisplay.getDisplayId() == displayId) {
            return;
        }
        updateInternalDisplay(displayId, this.mView.getResources());
        this.mImeFocusController.onMovedToDisplay();
        this.mAttachInfo.mDisplayState = this.mDisplay.getState();
        this.mView.dispatchMovedToDisplay(this.mDisplay, config);
    }

    private void updateInternalDisplay(int displayId, Resources resources) {
        Display display;
        Display display2;
        Display preferredDisplay = ResourcesManager.getInstance().getAdjustedDisplay(displayId, resources);
        Consumer<Display> consumer = this.mHdrSdrRatioChangedListener;
        if (consumer != null && (display2 = this.mDisplay) != null) {
            display2.unregisterHdrSdrRatioChangedListener(consumer);
        }
        if (preferredDisplay == null) {
            Slog.m90w(TAG, "Cannot get desired display with Id: " + displayId);
            this.mDisplay = ResourcesManager.getInstance().getAdjustedDisplay(0, resources);
        } else {
            this.mDisplay = preferredDisplay;
        }
        Consumer<Display> consumer2 = this.mHdrSdrRatioChangedListener;
        if (consumer2 != null && (display = this.mDisplay) != null) {
            display.registerHdrSdrRatioChangedListener(this.mExecutor, consumer2);
        }
        this.mContext.updateDisplay(this.mDisplay.getDisplayId());
    }

    void pokeDrawLockIfNeeded() {
        if (Display.isDozeState(this.mAttachInfo.mDisplayState) && this.mWindowAttributes.type == 1 && this.mAdded && this.mTraversalScheduled && this.mAttachInfo.mHasWindowFocus) {
            try {
                this.mWindowSession.pokeDrawLock(this.mWindow);
            } catch (RemoteException e) {
            }
        }
    }

    @Override // android.view.ViewParent
    public void requestFitSystemWindows() {
        checkThread();
        this.mApplyInsetsRequested = true;
        scheduleTraversals();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void notifyInsetsChanged() {
        this.mApplyInsetsRequested = true;
        requestLayout();
        if (View.sForceLayoutWhenInsetsChanged && this.mView != null && (this.mWindowAttributes.softInputMode & 240) == 16) {
            forceLayout(this.mView);
        }
        if (!this.mIsInTraversal) {
            scheduleTraversals();
        }
    }

    @Override // android.view.ViewParent
    public void requestLayout() {
        if (!this.mHandlingLayoutInLayoutRequest) {
            checkThread();
            this.mLayoutRequested = true;
            scheduleTraversals();
        }
    }

    @Override // android.view.ViewParent
    public boolean isLayoutRequested() {
        return this.mLayoutRequested;
    }

    @Override // android.view.ViewParent
    public void onDescendantInvalidated(View child, View descendant) {
        if ((descendant.mPrivateFlags & 64) != 0) {
            this.mIsAnimating = true;
        }
        invalidate();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void invalidate() {
        this.mDirty.set(0, 0, this.mWidth, this.mHeight);
        if (!this.mWillDrawSoon) {
            scheduleTraversals();
        }
    }

    void invalidateWorld(View view) {
        view.invalidate();
        if (view instanceof ViewGroup) {
            ViewGroup parent = (ViewGroup) view;
            for (int i = 0; i < parent.getChildCount(); i++) {
                invalidateWorld(parent.getChildAt(i));
            }
        }
    }

    @Override // android.view.ViewParent
    public void invalidateChild(View child, Rect dirty) {
        invalidateChildInParent(null, dirty);
    }

    @Override // android.view.ViewParent
    public ViewParent invalidateChildInParent(int[] location, Rect dirty) {
        checkThread();
        if (dirty == null) {
            invalidate();
            return null;
        } else if (dirty.isEmpty() && !this.mIsAnimating) {
            return null;
        } else {
            if (this.mCurScrollY != 0 || this.mTranslator != null) {
                this.mTempRect.set(dirty);
                dirty = this.mTempRect;
                int i = this.mCurScrollY;
                if (i != 0) {
                    dirty.offset(0, -i);
                }
                CompatibilityInfo.Translator translator = this.mTranslator;
                if (translator != null) {
                    translator.translateRectInAppWindowToScreen(dirty);
                }
                if (this.mAttachInfo.mScalingRequired) {
                    dirty.inset(-1, -1);
                }
            }
            invalidateRectOnScreen(dirty);
            return null;
        }
    }

    private void invalidateRectOnScreen(Rect dirty) {
        Rect localDirty = this.mDirty;
        localDirty.union(dirty.left, dirty.top, dirty.right, dirty.bottom);
        float appScale = this.mAttachInfo.mApplicationScale;
        boolean intersected = localDirty.intersect(0, 0, (int) ((this.mWidth * appScale) + 0.5f), (int) ((this.mHeight * appScale) + 0.5f));
        if (!intersected) {
            localDirty.setEmpty();
        }
        if (this.mWillDrawSoon) {
            return;
        }
        if (intersected || this.mIsAnimating) {
            scheduleTraversals();
        }
    }

    public void setIsAmbientMode(boolean ambient) {
        this.mIsAmbientMode = ambient;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setWindowStopped(boolean stopped) {
        checkThread();
        if (this.mStopped != stopped) {
            this.mStopped = stopped;
            ThreadedRenderer renderer = this.mAttachInfo.mThreadedRenderer;
            if (renderer != null) {
                renderer.setStopped(this.mStopped);
            }
            if (!this.mStopped) {
                this.mAppVisibilityChanged = true;
                scheduleTraversals();
                return;
            }
            if (renderer != null) {
                renderer.destroyHardwareResources(this.mView);
            }
            if (this.mSurface.isValid()) {
                if (this.mSurfaceHolder != null) {
                    notifyHolderSurfaceDestroyed();
                }
                notifySurfaceDestroyed();
            }
            destroySurface();
        }
    }

    /* loaded from: classes4.dex */
    public interface SurfaceChangedCallback {
        void surfaceCreated(SurfaceControl.Transaction transaction);

        void surfaceDestroyed();

        void surfaceReplaced(SurfaceControl.Transaction transaction);

        default void vriDrawStarted(boolean isWmSync) {
        }
    }

    public void addSurfaceChangedCallback(SurfaceChangedCallback c) {
        this.mSurfaceChangedCallbacks.add(c);
    }

    public void removeSurfaceChangedCallback(SurfaceChangedCallback c) {
        this.mSurfaceChangedCallbacks.remove(c);
    }

    private void notifySurfaceCreated(SurfaceControl.Transaction t) {
        for (int i = 0; i < this.mSurfaceChangedCallbacks.size(); i++) {
            this.mSurfaceChangedCallbacks.get(i).surfaceCreated(t);
        }
    }

    private void notifySurfaceReplaced(SurfaceControl.Transaction t) {
        for (int i = 0; i < this.mSurfaceChangedCallbacks.size(); i++) {
            this.mSurfaceChangedCallbacks.get(i).surfaceReplaced(t);
        }
    }

    private void notifySurfaceDestroyed() {
        for (int i = 0; i < this.mSurfaceChangedCallbacks.size(); i++) {
            this.mSurfaceChangedCallbacks.get(i).surfaceDestroyed();
        }
    }

    private void notifyDrawStarted(boolean isWmSync) {
        for (int i = 0; i < this.mSurfaceChangedCallbacks.size(); i++) {
            this.mSurfaceChangedCallbacks.get(i).vriDrawStarted(isWmSync);
        }
    }

    public SurfaceControl getBoundsLayer() {
        if (this.mBoundsLayer == null) {
            this.mBoundsLayer = new SurfaceControl.Builder(this.mSurfaceSession).setContainerLayer().setName("Bounds for - " + getTitle().toString()).setParent(getSurfaceControl()).setCallsite("ViewRootImpl.getBoundsLayer").build();
            setBoundsLayerCrop(this.mTransaction);
            this.mTransaction.show(this.mBoundsLayer).apply();
        }
        return this.mBoundsLayer;
    }

    void updateBlastSurfaceIfNeeded() {
        if (!this.mSurfaceControl.isValid()) {
            return;
        }
        BLASTBufferQueue bLASTBufferQueue = this.mBlastBufferQueue;
        if (bLASTBufferQueue != null && bLASTBufferQueue.isSameSurfaceControl(this.mSurfaceControl)) {
            this.mBlastBufferQueue.update(this.mSurfaceControl, this.mSurfaceSize.f76x, this.mSurfaceSize.f77y, this.mWindowAttributes.format);
            return;
        }
        BLASTBufferQueue bLASTBufferQueue2 = this.mBlastBufferQueue;
        if (bLASTBufferQueue2 != null) {
            bLASTBufferQueue2.destroy();
        }
        BLASTBufferQueue bLASTBufferQueue3 = new BLASTBufferQueue(this.mTag, this.mSurfaceControl, this.mSurfaceSize.f76x, this.mSurfaceSize.f77y, this.mWindowAttributes.format);
        this.mBlastBufferQueue = bLASTBufferQueue3;
        bLASTBufferQueue3.setTransactionHangCallback(sTransactionHangCallback);
        Surface blastSurface = this.mBlastBufferQueue.createSurface();
        this.mSurface.transferFrom(blastSurface);
    }

    private void setBoundsLayerCrop(SurfaceControl.Transaction t) {
        this.mTempRect.set(0, 0, this.mSurfaceSize.f76x, this.mSurfaceSize.f77y);
        this.mTempRect.inset(this.mWindowAttributes.surfaceInsets.left, this.mWindowAttributes.surfaceInsets.top, this.mWindowAttributes.surfaceInsets.right, this.mWindowAttributes.surfaceInsets.bottom);
        this.mTempRect.inset(this.mChildBoundingInsets.left, this.mChildBoundingInsets.top, this.mChildBoundingInsets.right, this.mChildBoundingInsets.bottom);
        t.setWindowCrop(this.mBoundsLayer, this.mTempRect);
    }

    private boolean updateBoundsLayer(SurfaceControl.Transaction t) {
        if (this.mBoundsLayer != null) {
            setBoundsLayerCrop(t);
            return true;
        }
        return false;
    }

    private void prepareSurfaces() {
        SurfaceControl.Transaction t = this.mTransaction;
        SurfaceControl sc = getSurfaceControl();
        if (sc.isValid() && updateBoundsLayer(t)) {
            applyTransactionOnDraw(t);
        }
    }

    private void destroySurface() {
        SurfaceControl surfaceControl = this.mBoundsLayer;
        if (surfaceControl != null) {
            surfaceControl.release();
            this.mBoundsLayer = null;
        }
        this.mSurface.release();
        this.mSurfaceControl.release();
        BLASTBufferQueue bLASTBufferQueue = this.mBlastBufferQueue;
        if (bLASTBufferQueue != null) {
            bLASTBufferQueue.destroy();
            this.mBlastBufferQueue = null;
        }
        if (this.mAttachInfo.mThreadedRenderer != null) {
            this.mAttachInfo.mThreadedRenderer.setSurfaceControl(null, null);
        }
    }

    public void setPausedForTransition(boolean paused) {
        this.mPausedForTransition = paused;
    }

    @Override // android.view.ViewParent
    public ViewParent getParent() {
        return null;
    }

    @Override // android.view.ViewParent
    public boolean getChildVisibleRect(View child, Rect r, Point offset) {
        if (child != this.mView) {
            throw new RuntimeException("child is not mine, honest!");
        }
        return r.intersect(0, 0, this.mWidth, this.mHeight);
    }

    @Override // android.view.ViewParent
    public void bringChildToFront(View child) {
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getHostVisibility() {
        View view = this.mView;
        if (view == null || !(this.mAppVisible || this.mForceDecorViewVisibility)) {
            return 8;
        }
        return view.getVisibility();
    }

    public void requestTransitionStart(LayoutTransition transition) {
        ArrayList<LayoutTransition> arrayList = this.mPendingTransitions;
        if (arrayList == null || !arrayList.contains(transition)) {
            if (this.mPendingTransitions == null) {
                this.mPendingTransitions = new ArrayList<>();
            }
            this.mPendingTransitions.add(transition);
        }
    }

    void notifyRendererOfFramePending() {
        if (this.mAttachInfo.mThreadedRenderer != null) {
            this.mAttachInfo.mThreadedRenderer.notifyFramePending();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void notifyRendererOfExpensiveFrame() {
        if (this.mAttachInfo.mThreadedRenderer != null) {
            this.mAttachInfo.mThreadedRenderer.notifyExpensiveFrame();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void scheduleTraversals() {
        if (!this.mTraversalScheduled) {
            this.mTraversalScheduled = true;
            this.mTraversalBarrier = this.mHandler.getLooper().getQueue().postSyncBarrier();
            this.mChoreographer.postCallback(3, this.mTraversalRunnable, null);
            notifyRendererOfFramePending();
            pokeDrawLockIfNeeded();
        }
    }

    void unscheduleTraversals() {
        if (this.mTraversalScheduled) {
            this.mTraversalScheduled = false;
            this.mHandler.getLooper().getQueue().removeSyncBarrier(this.mTraversalBarrier);
            this.mChoreographer.removeCallbacks(3, this.mTraversalRunnable, null);
        }
    }

    void doTraversal() {
        if (this.mTraversalScheduled) {
            this.mTraversalScheduled = false;
            this.mHandler.getLooper().getQueue().removeSyncBarrier(this.mTraversalBarrier);
            if (this.mProfile) {
                Debug.startMethodTracing("ViewAncestor");
            }
            performTraversals();
            if (this.mProfile) {
                Debug.stopMethodTracing();
                this.mProfile = false;
            }
        }
    }

    private void applyKeepScreenOnFlag(WindowManager.LayoutParams params) {
        if (this.mAttachInfo.mKeepScreenOn) {
            params.flags |= 128;
        } else {
            params.flags = (params.flags & PackageManager.INSTALL_FAILED_PRE_APPROVAL_NOT_AVAILABLE) | (this.mClientWindowLayoutFlags & 128);
        }
    }

    private boolean collectViewAttributes() {
        if (this.mAttachInfo.mRecomputeGlobalAttributes) {
            this.mAttachInfo.mRecomputeGlobalAttributes = false;
            boolean oldScreenOn = this.mAttachInfo.mKeepScreenOn;
            this.mAttachInfo.mKeepScreenOn = false;
            this.mAttachInfo.mSystemUiVisibility = 0;
            this.mAttachInfo.mHasSystemUiListeners = false;
            this.mView.dispatchCollectViewAttributes(this.mAttachInfo, 0);
            this.mAttachInfo.mSystemUiVisibility &= ~this.mAttachInfo.mDisabledSystemUiVisibility;
            WindowManager.LayoutParams params = this.mWindowAttributes;
            this.mAttachInfo.mSystemUiVisibility |= getImpliedSystemUiVisibility(params);
            SystemUiVisibilityInfo systemUiVisibilityInfo = this.mCompatibleVisibilityInfo;
            systemUiVisibilityInfo.globalVisibility = (systemUiVisibilityInfo.globalVisibility & (-2)) | (this.mAttachInfo.mSystemUiVisibility & 1);
            dispatchDispatchSystemUiVisibilityChanged();
            if (this.mAttachInfo.mKeepScreenOn != oldScreenOn || this.mAttachInfo.mSystemUiVisibility != params.subtreeSystemUiVisibility || this.mAttachInfo.mHasSystemUiListeners != params.hasSystemUiListeners) {
                applyKeepScreenOnFlag(params);
                params.subtreeSystemUiVisibility = this.mAttachInfo.mSystemUiVisibility;
                params.hasSystemUiListeners = this.mAttachInfo.mHasSystemUiListeners;
                this.mView.dispatchWindowSystemUiVisiblityChanged(this.mAttachInfo.mSystemUiVisibility);
                return true;
            }
        }
        return false;
    }

    private int getImpliedSystemUiVisibility(WindowManager.LayoutParams params) {
        int vis = 0;
        if ((params.flags & 67108864) != 0) {
            vis = 0 | 1280;
        }
        if ((params.flags & 134217728) != 0) {
            return vis | 768;
        }
        return vis;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void updateCompatSysUiVisibility(int visibleTypes, int requestedVisibleTypes, int controllableTypes) {
        int visibleTypes2 = (requestedVisibleTypes & controllableTypes) | ((~controllableTypes) & visibleTypes);
        updateCompatSystemUiVisibilityInfo(4, WindowInsets.Type.statusBars(), visibleTypes2, controllableTypes);
        updateCompatSystemUiVisibilityInfo(2, WindowInsets.Type.navigationBars(), visibleTypes2, controllableTypes);
        dispatchDispatchSystemUiVisibilityChanged();
    }

    private void updateCompatSystemUiVisibilityInfo(int systemUiFlag, int insetsType, int visibleTypes, int controllableTypes) {
        SystemUiVisibilityInfo info = this.mCompatibleVisibilityInfo;
        boolean willBeVisible = (visibleTypes & insetsType) != 0;
        boolean hasControl = (controllableTypes & insetsType) != 0;
        boolean wasInvisible = (this.mAttachInfo.mSystemUiVisibility & systemUiFlag) != 0;
        if (willBeVisible) {
            info.globalVisibility &= ~systemUiFlag;
            if (hasControl && wasInvisible) {
                info.localChanges |= systemUiFlag;
                return;
            }
            return;
        }
        info.globalVisibility |= systemUiFlag;
        info.localChanges &= ~systemUiFlag;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void clearLowProfileModeIfNeeded(int showTypes, boolean fromIme) {
        SystemUiVisibilityInfo info = this.mCompatibleVisibilityInfo;
        if ((WindowInsets.Type.systemBars() & showTypes) != 0 && !fromIme && (info.globalVisibility & 1) != 0) {
            info.globalVisibility &= -2;
            info.localChanges |= 1;
            dispatchDispatchSystemUiVisibilityChanged();
        }
    }

    private void dispatchDispatchSystemUiVisibilityChanged() {
        if (this.mDispatchedSystemUiVisibility != this.mCompatibleVisibilityInfo.globalVisibility) {
            this.mHandler.removeMessages(17);
            ViewRootHandler viewRootHandler = this.mHandler;
            viewRootHandler.sendMessage(viewRootHandler.obtainMessage(17));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleDispatchSystemUiVisibilityChanged() {
        if (this.mView == null) {
            return;
        }
        SystemUiVisibilityInfo info = this.mCompatibleVisibilityInfo;
        if (info.localChanges != 0) {
            this.mView.updateLocalSystemUiVisibility(info.localValue, info.localChanges);
            info.localChanges = 0;
        }
        int visibility = info.globalVisibility & 7;
        if (this.mDispatchedSystemUiVisibility != visibility) {
            this.mDispatchedSystemUiVisibility = visibility;
            this.mView.dispatchSystemUiVisibilityChanged(visibility);
        }
    }

    public static void adjustLayoutParamsForCompatibility(WindowManager.LayoutParams inOutParams) {
        int sysUiVis = inOutParams.systemUiVisibility | inOutParams.subtreeSystemUiVisibility;
        int flags = inOutParams.flags;
        int type = inOutParams.type;
        int adjust = inOutParams.softInputMode & 240;
        if ((inOutParams.privateFlags & 67108864) == 0) {
            inOutParams.insetsFlags.appearance = 0;
            if ((sysUiVis & 1) != 0) {
                inOutParams.insetsFlags.appearance |= 4;
            }
            if ((sysUiVis & 8192) != 0) {
                inOutParams.insetsFlags.appearance |= 8;
            }
            if ((sysUiVis & 16) != 0) {
                inOutParams.insetsFlags.appearance |= 16;
            }
        }
        if ((inOutParams.privateFlags & 134217728) == 0) {
            if ((sysUiVis & 4096) != 0 || (flags & 1024) != 0) {
                inOutParams.insetsFlags.behavior = 2;
            } else {
                inOutParams.insetsFlags.behavior = 1;
            }
        }
        inOutParams.privateFlags &= -1073741825;
        if ((inOutParams.privateFlags & 268435456) != 0) {
            return;
        }
        int types = inOutParams.getFitInsetsTypes();
        boolean ignoreVis = inOutParams.isFitInsetsIgnoringVisibility();
        if ((sysUiVis & 1024) != 0 || (flags & 256) != 0 || (67108864 & flags) != 0) {
            types &= ~WindowInsets.Type.statusBars();
        }
        if ((sysUiVis & 512) != 0 || (flags & 134217728) != 0) {
            types &= ~WindowInsets.Type.systemBars();
        }
        if (type == 2005 || type == 2003) {
            ignoreVis = true;
        } else if ((WindowInsets.Type.systemBars() & types) == WindowInsets.Type.systemBars()) {
            if (adjust == 16) {
                types |= WindowInsets.Type.ime();
            } else {
                inOutParams.privateFlags |= 1073741824;
            }
        }
        inOutParams.setFitInsetsTypes(types);
        inOutParams.setFitInsetsIgnoringVisibility(ignoreVis);
        inOutParams.privateFlags &= -268435457;
    }

    private void controlInsetsForCompatibility(WindowManager.LayoutParams params) {
        int sysUiVis = params.systemUiVisibility | params.subtreeSystemUiVisibility;
        int flags = params.flags;
        boolean matchParent = params.width == -1 && params.height == -1;
        boolean nonAttachedAppWindow = params.type >= 1 && params.type <= 99;
        boolean statusWasHiddenByFlags = (this.mTypesHiddenByFlags & WindowInsets.Type.statusBars()) != 0;
        boolean statusIsHiddenByFlags = (sysUiVis & 4) != 0 || ((flags & 1024) != 0 && matchParent && nonAttachedAppWindow);
        boolean navWasHiddenByFlags = (this.mTypesHiddenByFlags & WindowInsets.Type.navigationBars()) != 0;
        boolean navIsHiddenByFlags = (sysUiVis & 2) != 0;
        int typesToHide = 0;
        int typesToShow = 0;
        if (statusIsHiddenByFlags && !statusWasHiddenByFlags) {
            typesToHide = 0 | WindowInsets.Type.statusBars();
        } else if (!statusIsHiddenByFlags && statusWasHiddenByFlags) {
            typesToShow = 0 | WindowInsets.Type.statusBars();
        }
        if (navIsHiddenByFlags && !navWasHiddenByFlags) {
            typesToHide |= WindowInsets.Type.navigationBars();
        } else if (!navIsHiddenByFlags && navWasHiddenByFlags) {
            typesToShow |= WindowInsets.Type.navigationBars();
        }
        if (typesToHide != 0) {
            getInsetsController().hide(typesToHide);
        }
        if (typesToShow != 0) {
            getInsetsController().show(typesToShow);
        }
        int i = this.mTypesHiddenByFlags | typesToHide;
        this.mTypesHiddenByFlags = i;
        this.mTypesHiddenByFlags = i & (~typesToShow);
    }

    private boolean measureHierarchy(View host, WindowManager.LayoutParams lp, Resources res, int desiredWindowWidth, int desiredWindowHeight, boolean forRootSizeOnly) {
        boolean goodMeasure = false;
        if (lp.width == -2) {
            DisplayMetrics packageMetrics = res.getDisplayMetrics();
            res.getValue(C4057R.dimen.config_prefDialogWidth, this.mTmpValue, true);
            int baseSize = 0;
            if (this.mTmpValue.type == 5) {
                baseSize = (int) this.mTmpValue.getDimension(packageMetrics);
            }
            if (baseSize != 0 && desiredWindowWidth > baseSize) {
                int childWidthMeasureSpec = getRootMeasureSpec(baseSize, lp.width, lp.privateFlags);
                int childHeightMeasureSpec = getRootMeasureSpec(desiredWindowHeight, lp.height, lp.privateFlags);
                performMeasure(childWidthMeasureSpec, childHeightMeasureSpec);
                if ((host.getMeasuredWidthAndState() & 16777216) == 0) {
                    goodMeasure = true;
                } else {
                    int baseSize2 = (baseSize + desiredWindowWidth) / 2;
                    int baseSize3 = lp.width;
                    performMeasure(getRootMeasureSpec(baseSize2, baseSize3, lp.privateFlags), childHeightMeasureSpec);
                    if ((host.getMeasuredWidthAndState() & 16777216) == 0) {
                        goodMeasure = true;
                    }
                }
            }
        }
        if (goodMeasure) {
            return false;
        }
        int childWidthMeasureSpec2 = getRootMeasureSpec(desiredWindowWidth, lp.width, lp.privateFlags);
        int childHeightMeasureSpec2 = getRootMeasureSpec(desiredWindowHeight, lp.height, lp.privateFlags);
        if (!forRootSizeOnly || !setMeasuredRootSizeFromSpec(childWidthMeasureSpec2, childHeightMeasureSpec2)) {
            performMeasure(childWidthMeasureSpec2, childHeightMeasureSpec2);
        } else {
            this.mViewMeasureDeferred = true;
        }
        return (this.mWidth == host.getMeasuredWidth() && this.mHeight == host.getMeasuredHeight()) ? false : true;
    }

    private boolean setMeasuredRootSizeFromSpec(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = View.MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = View.MeasureSpec.getMode(heightMeasureSpec);
        if (widthMode != 1073741824 || heightMode != 1073741824) {
            return false;
        }
        this.mMeasuredWidth = View.MeasureSpec.getSize(widthMeasureSpec);
        this.mMeasuredHeight = View.MeasureSpec.getSize(heightMeasureSpec);
        return true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void transformMatrixToGlobal(Matrix m) {
        m.preTranslate(this.mAttachInfo.mWindowLeft, this.mAttachInfo.mWindowTop);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void transformMatrixToLocal(Matrix m) {
        m.postTranslate(-this.mAttachInfo.mWindowLeft, -this.mAttachInfo.mWindowTop);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public WindowInsets getWindowInsets(boolean forceConstruct) {
        if (this.mLastWindowInsets == null || forceConstruct) {
            Configuration config = getConfiguration();
            this.mLastWindowInsets = this.mInsetsController.calculateInsets(config.isScreenRound(), this.mAttachInfo.mAlwaysConsumeSystemBars, this.mWindowAttributes.type, config.windowConfiguration.getWindowingMode(), this.mWindowAttributes.softInputMode, this.mWindowAttributes.flags, this.mWindowAttributes.systemUiVisibility | this.mWindowAttributes.subtreeSystemUiVisibility);
            this.mAttachInfo.mContentInsets.set(this.mLastWindowInsets.getSystemWindowInsets().toRect());
            this.mAttachInfo.mStableInsets.set(this.mLastWindowInsets.getStableInsets().toRect());
            this.mAttachInfo.mVisibleInsets.set(this.mInsetsController.calculateVisibleInsets(this.mWindowAttributes.type, config.windowConfiguration.getWindowingMode(), this.mWindowAttributes.softInputMode, this.mWindowAttributes.flags).toRect());
        }
        return this.mLastWindowInsets;
    }

    public void dispatchApplyInsets(View host) {
        Trace.traceBegin(8L, "dispatchApplyInsets");
        this.mApplyInsetsRequested = false;
        WindowInsets insets = getWindowInsets(true);
        if (!shouldDispatchCutout()) {
            insets = insets.consumeDisplayCutout();
        }
        host.dispatchApplyWindowInsets(insets);
        this.mAttachInfo.delayNotifyContentCaptureInsetsEvent(insets.getInsets(WindowInsets.Type.all()));
        Trace.traceEnd(8L);
    }

    private boolean updateCaptionInsets() {
        if (CAPTION_ON_SHELL) {
            return false;
        }
        View view = this.mView;
        if (view instanceof DecorView) {
            int captionInsetsHeight = ((DecorView) view).getCaptionInsetsHeight();
            Rect captionFrame = new Rect();
            if (captionInsetsHeight != 0) {
                captionFrame.set(this.mWinFrame.left, this.mWinFrame.top, this.mWinFrame.right, this.mWinFrame.top + captionInsetsHeight);
            }
            if (this.mAttachInfo.mCaptionInsets.equals(captionFrame)) {
                return false;
            }
            this.mAttachInfo.mCaptionInsets.set(captionFrame);
            return true;
        }
        return false;
    }

    private boolean shouldDispatchCutout() {
        return this.mWindowAttributes.layoutInDisplayCutoutMode == 3 || this.mWindowAttributes.layoutInDisplayCutoutMode == 1;
    }

    public InsetsController getInsetsController() {
        return this.mInsetsController;
    }

    private static boolean shouldUseDisplaySize(WindowManager.LayoutParams lp) {
        return lp.type == 2041 || lp.type == 2011 || lp.type == 2020;
    }

    private static boolean shouldOptimizeMeasure(WindowManager.LayoutParams lp) {
        return (lp.privateFlags & 512) != 0;
    }

    private Rect getWindowBoundsInsetSystemBars() {
        Rect bounds = new Rect(this.mContext.getResources().getConfiguration().windowConfiguration.getBounds());
        bounds.inset(this.mInsetsController.getState().calculateInsets(bounds, WindowInsets.Type.systemBars(), false));
        return bounds;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int dipToPx(int dip) {
        DisplayMetrics displayMetrics = this.mContext.getResources().getDisplayMetrics();
        return (int) ((displayMetrics.density * dip) + 0.5f);
    }

    /* JADX WARN: Can't wrap try/catch for region: R(125:(4:24|(1:26)(1:787)|(1:28)(1:786)|(168:30|31|(3:33|(1:35)(1:784)|36)(1:785)|37|(6:39|(1:41)(2:773|(1:778)(1:777))|42|(1:44)|45|(1:47))(2:779|(1:783))|(3:49|(2:(1:52)(1:54)|53)|(1:58))|59|(1:61)|62|(1:64)|65|(1:772)(1:71)|72|(3:74|(1:770)(2:80|(1:82)(1:769))|83)(1:771)|84|(1:86)|87|(1:89)|90|(2:752|(6:754|(3:756|(2:758|759)(1:761)|760)|762|(1:764)|765|(1:767))(1:768))(1:94)|95|(2:97|(1:99)(1:750))(1:751)|(1:101)|(1:749)(124:104|(1:748)(2:108|(2:740|(2:742|(1:744))(1:747))(1:114))|115|116|(1:739)(1:120)|121|(1:738)(1:125)|126|(1:128)(1:737)|129|(1:131)(1:736)|(4:133|(1:137)|138|(1:140))(1:735)|141|(1:734)(2:146|(1:148)(42:733|251|(1:253)|254|(3:258|259|260)|(1:468)|(1:467)(1:274)|275|(1:466)(1:279)|280|(2:282|(4:284|(1:286)|287|(2:289|(1:291)))(1:464))(1:465)|292|(1:294)(1:(1:460)(2:(1:462)|463))|(1:296)|(1:298)|299|(3:301|(3:453|(1:455)(1:457)|456)(1:305)|306)(1:458)|307|(1:452)(1:311)|312|(7:314|(4:316|(1:318)|319|(1:321))(1:440)|(1:323)(1:439)|(1:325)|(1:327)(1:(1:438))|328|329)(2:441|(3:445|446|447))|330|(1:430)(4:332|(2:423|(1:427))|337|(1:341))|(15:422|(1:347)|348|(1:420)(1:351)|352|(1:354)|(1:419)(1:357)|358|(1:418)(1:363)|(1:417)(3:365|(1:367)(1:416)|368)|(4:370|(1:394)(3:374|(2:375|(1:377)(1:378))|379)|380|(1:382))(2:395|(2:(1:398)(1:400)|399)(4:401|(4:405|(2:408|406)|409|410)|411|(1:415)))|383|(1:385)|386|(2:388|(2:390|391)(1:392))(1:393))|345|(0)|348|(0)|420|352|(0)|(0)|419|358|(1:361)|418|(0)(0)|(0)(0)|383|(0)|386|(0)(0)))|149|(3:151|(1:153)(1:731)|154)(1:732)|155|(1:157)|158|159|160|(5:718|719|720|721|722)(1:163)|164|165|(1:167)(1:712)|168|169|170|171|172|173|174|175|176|(3:694|695|(1:697))|178|(1:180)(1:693)|181|182|(4:668|669|(1:673)|(1:687)(4:682|683|684|685))(1:184)|185|186|(4:190|(1:192)(1:506)|193|194)|507|(6:509|510|511|512|513|514)(1:662)|515|516|(1:518)(1:648)|519|(1:647)(1:523)|524|(1:646)(1:528)|529|530|(1:645)(1:533)|534|(1:536)|(2:538|539)|540|(1:542)|(2:641|642)|(3:549|550|(5:584|585|586|(3:589|590|(1:592))|588))(1:(8:609|(1:611)|612|(1:614)|615|(1:617)|618|(1:620))(1:(3:630|631|632)))|552|553|(1:579)(1:(6:556|(1:577)(1:560)|561|(1:563)(1:575)|564|565)(1:578))|566|(1:(1:569)(1:570))|571|(1:573)|574|202|(1:499)|206|(6:208|(1:210)|211|(2:213|(3:215|(1:217)|218))|(2:486|(3:488|(4:490|(1:492)|493|494)(1:496)|495)(1:497))(1:223)|(4:225|226|227|228))(1:498)|233|(1:244)|245|(6:475|(1:477)(1:485)|478|(1:480)(1:484)|(1:482)|483)(1:249)|250|251|(0)|254|(4:256|258|259|260)|(0)|468|(2:270|272)|467|275|(1:277)|466|280|(0)(0)|292|(0)(0)|(0)|(0)|299|(0)(0)|307|(1:309)|452|312|(0)(0)|330|(0)(0)|(0)|345|(0)|348|(0)|420|352|(0)|(0)|419|358|(0)|418|(0)(0)|(0)(0)|383|(0)|386|(0)(0))|746|116|(1:118)|739|121|(1:123)|738|126|(0)(0)|129|(0)(0)|(0)(0)|141|(0)|734|149|(0)(0)|155|(0)|158|159|160|(0)|718|719|720|721|722|164|165|(0)(0)|168|169|170|171|172|173|174|175|176|(0)|178|(0)(0)|181|182|(0)(0)|185|186|(5:188|190|(0)(0)|193|194)|507|(0)(0)|515|516|(0)(0)|519|(1:521)|647|524|(1:526)|646|529|530|(0)|643|645|534|(0)|(0)|540|(0)|(1:544)|641|642|(0)(0)|552|553|(0)(0)|566|(0)|571|(0)|574|202|(1:204)|499|206|(0)(0)|233|(2:235|244)|245|(1:247)|469|475|(0)(0)|478|(0)(0)|(0)|483|250|251|(0)|254|(0)|(0)|468|(0)|467|275|(0)|466|280|(0)(0)|292|(0)(0)|(0)|(0)|299|(0)(0)|307|(0)|452|312|(0)(0)|330|(0)(0)|(0)|345|(0)|348|(0)|420|352|(0)|(0)|419|358|(0)|418|(0)(0)|(0)(0)|383|(0)|386|(0)(0)))|159|160|(0)|718|719|720|721|722|164|165|(0)(0)|168|169|170|171|172|173|174|175|176|(0)|178|(0)(0)|181|182|(0)(0)|185|186|(0)|507|(0)(0)|515|516|(0)(0)|519|(0)|647|524|(0)|646|529|530|(0)|643|645|534|(0)|(0)|540|(0)|(0)|641|642|(0)(0)|552|553|(0)(0)|566|(0)|571|(0)|574|202|(0)|499|206|(0)(0)|233|(0)|245|(0)|469|475|(0)(0)|478|(0)(0)|(0)|483|250|251|(0)|254|(0)|(0)|468|(0)|467|275|(0)|466|280|(0)(0)|292|(0)(0)|(0)|(0)|299|(0)(0)|307|(0)|452|312|(0)(0)|330|(0)(0)|(0)|345|(0)|348|(0)|420|352|(0)|(0)|419|358|(0)|418|(0)(0)|(0)(0)|383|(0)|386|(0)(0)) */
    /* JADX WARN: Can't wrap try/catch for region: R(176:13|(1:789)(1:21)|22|(4:24|(1:26)(1:787)|(1:28)(1:786)|(168:30|31|(3:33|(1:35)(1:784)|36)(1:785)|37|(6:39|(1:41)(2:773|(1:778)(1:777))|42|(1:44)|45|(1:47))(2:779|(1:783))|(3:49|(2:(1:52)(1:54)|53)|(1:58))|59|(1:61)|62|(1:64)|65|(1:772)(1:71)|72|(3:74|(1:770)(2:80|(1:82)(1:769))|83)(1:771)|84|(1:86)|87|(1:89)|90|(2:752|(6:754|(3:756|(2:758|759)(1:761)|760)|762|(1:764)|765|(1:767))(1:768))(1:94)|95|(2:97|(1:99)(1:750))(1:751)|(1:101)|(1:749)(124:104|(1:748)(2:108|(2:740|(2:742|(1:744))(1:747))(1:114))|115|116|(1:739)(1:120)|121|(1:738)(1:125)|126|(1:128)(1:737)|129|(1:131)(1:736)|(4:133|(1:137)|138|(1:140))(1:735)|141|(1:734)(2:146|(1:148)(42:733|251|(1:253)|254|(3:258|259|260)|(1:468)|(1:467)(1:274)|275|(1:466)(1:279)|280|(2:282|(4:284|(1:286)|287|(2:289|(1:291)))(1:464))(1:465)|292|(1:294)(1:(1:460)(2:(1:462)|463))|(1:296)|(1:298)|299|(3:301|(3:453|(1:455)(1:457)|456)(1:305)|306)(1:458)|307|(1:452)(1:311)|312|(7:314|(4:316|(1:318)|319|(1:321))(1:440)|(1:323)(1:439)|(1:325)|(1:327)(1:(1:438))|328|329)(2:441|(3:445|446|447))|330|(1:430)(4:332|(2:423|(1:427))|337|(1:341))|(15:422|(1:347)|348|(1:420)(1:351)|352|(1:354)|(1:419)(1:357)|358|(1:418)(1:363)|(1:417)(3:365|(1:367)(1:416)|368)|(4:370|(1:394)(3:374|(2:375|(1:377)(1:378))|379)|380|(1:382))(2:395|(2:(1:398)(1:400)|399)(4:401|(4:405|(2:408|406)|409|410)|411|(1:415)))|383|(1:385)|386|(2:388|(2:390|391)(1:392))(1:393))|345|(0)|348|(0)|420|352|(0)|(0)|419|358|(1:361)|418|(0)(0)|(0)(0)|383|(0)|386|(0)(0)))|149|(3:151|(1:153)(1:731)|154)(1:732)|155|(1:157)|158|159|160|(5:718|719|720|721|722)(1:163)|164|165|(1:167)(1:712)|168|169|170|171|172|173|174|175|176|(3:694|695|(1:697))|178|(1:180)(1:693)|181|182|(4:668|669|(1:673)|(1:687)(4:682|683|684|685))(1:184)|185|186|(4:190|(1:192)(1:506)|193|194)|507|(6:509|510|511|512|513|514)(1:662)|515|516|(1:518)(1:648)|519|(1:647)(1:523)|524|(1:646)(1:528)|529|530|(1:645)(1:533)|534|(1:536)|(2:538|539)|540|(1:542)|(2:641|642)|(3:549|550|(5:584|585|586|(3:589|590|(1:592))|588))(1:(8:609|(1:611)|612|(1:614)|615|(1:617)|618|(1:620))(1:(3:630|631|632)))|552|553|(1:579)(1:(6:556|(1:577)(1:560)|561|(1:563)(1:575)|564|565)(1:578))|566|(1:(1:569)(1:570))|571|(1:573)|574|202|(1:499)|206|(6:208|(1:210)|211|(2:213|(3:215|(1:217)|218))|(2:486|(3:488|(4:490|(1:492)|493|494)(1:496)|495)(1:497))(1:223)|(4:225|226|227|228))(1:498)|233|(1:244)|245|(6:475|(1:477)(1:485)|478|(1:480)(1:484)|(1:482)|483)(1:249)|250|251|(0)|254|(4:256|258|259|260)|(0)|468|(2:270|272)|467|275|(1:277)|466|280|(0)(0)|292|(0)(0)|(0)|(0)|299|(0)(0)|307|(1:309)|452|312|(0)(0)|330|(0)(0)|(0)|345|(0)|348|(0)|420|352|(0)|(0)|419|358|(0)|418|(0)(0)|(0)(0)|383|(0)|386|(0)(0))|746|116|(1:118)|739|121|(1:123)|738|126|(0)(0)|129|(0)(0)|(0)(0)|141|(0)|734|149|(0)(0)|155|(0)|158|159|160|(0)|718|719|720|721|722|164|165|(0)(0)|168|169|170|171|172|173|174|175|176|(0)|178|(0)(0)|181|182|(0)(0)|185|186|(5:188|190|(0)(0)|193|194)|507|(0)(0)|515|516|(0)(0)|519|(1:521)|647|524|(1:526)|646|529|530|(0)|643|645|534|(0)|(0)|540|(0)|(1:544)|641|642|(0)(0)|552|553|(0)(0)|566|(0)|571|(0)|574|202|(1:204)|499|206|(0)(0)|233|(2:235|244)|245|(1:247)|469|475|(0)(0)|478|(0)(0)|(0)|483|250|251|(0)|254|(0)|(0)|468|(0)|467|275|(0)|466|280|(0)(0)|292|(0)(0)|(0)|(0)|299|(0)(0)|307|(0)|452|312|(0)(0)|330|(0)(0)|(0)|345|(0)|348|(0)|420|352|(0)|(0)|419|358|(0)|418|(0)(0)|(0)(0)|383|(0)|386|(0)(0)))|788|31|(0)(0)|37|(0)(0)|(0)|59|(0)|62|(0)|65|(2:67|69)|772|72|(0)(0)|84|(0)|87|(0)|90|(1:92)|752|(0)(0)|95|(0)(0)|(0)|(0)|749|746|116|(0)|739|121|(0)|738|126|(0)(0)|129|(0)(0)|(0)(0)|141|(0)|734|149|(0)(0)|155|(0)|158|159|160|(0)|718|719|720|721|722|164|165|(0)(0)|168|169|170|171|172|173|174|175|176|(0)|178|(0)(0)|181|182|(0)(0)|185|186|(0)|507|(0)(0)|515|516|(0)(0)|519|(0)|647|524|(0)|646|529|530|(0)|643|645|534|(0)|(0)|540|(0)|(0)|641|642|(0)(0)|552|553|(0)(0)|566|(0)|571|(0)|574|202|(0)|499|206|(0)(0)|233|(0)|245|(0)|469|475|(0)(0)|478|(0)(0)|(0)|483|250|251|(0)|254|(0)|(0)|468|(0)|467|275|(0)|466|280|(0)(0)|292|(0)(0)|(0)|(0)|299|(0)(0)|307|(0)|452|312|(0)(0)|330|(0)(0)|(0)|345|(0)|348|(0)|420|352|(0)|(0)|419|358|(0)|418|(0)(0)|(0)(0)|383|(0)|386|(0)(0)) */
    /* JADX WARN: Code restructure failed: missing block: B:162:0x02e6, code lost:
        if (r24.height() != r54.mHeight) goto L115;
     */
    /* JADX WARN: Code restructure failed: missing block: B:427:0x0721, code lost:
        r0 = th;
     */
    /* JADX WARN: Code restructure failed: missing block: B:430:0x072d, code lost:
        r6 = r29;
        r3 = r35;
        r14 = r36;
        r9 = r41;
        r25 = 8;
     */
    /* JADX WARN: Code restructure failed: missing block: B:431:0x0739, code lost:
        r0 = th;
     */
    /* JADX WARN: Code restructure failed: missing block: B:434:0x0747, code lost:
        r6 = r29;
        r3 = r35;
        r14 = r36;
        r9 = r41;
        r25 = 8;
     */
    /* JADX WARN: Code restructure failed: missing block: B:435:0x0755, code lost:
        r0 = th;
     */
    /* JADX WARN: Code restructure failed: missing block: B:438:0x0763, code lost:
        r6 = r29;
        r3 = false;
        r9 = false;
        r25 = 8;
     */
    /* JADX WARN: Code restructure failed: missing block: B:439:0x0771, code lost:
        r0 = th;
     */
    /* JADX WARN: Code restructure failed: missing block: B:442:0x0781, code lost:
        r6 = r9;
        r3 = false;
        r9 = false;
        r25 = 8;
     */
    /* JADX WARN: Code restructure failed: missing block: B:443:0x0791, code lost:
        r0 = th;
     */
    /* JADX WARN: Code restructure failed: missing block: B:446:0x07a3, code lost:
        r6 = r9;
        r3 = false;
        r9 = false;
        r25 = 8;
     */
    /* JADX WARN: Code restructure failed: missing block: B:447:0x07b5, code lost:
        r0 = th;
     */
    /* JADX WARN: Code restructure failed: missing block: B:450:0x07c9, code lost:
        r6 = r9;
        r3 = false;
        r9 = false;
        r25 = 8;
     */
    /* JADX WARN: Code restructure failed: missing block: B:451:0x07dd, code lost:
        r0 = th;
     */
    /* JADX WARN: Code restructure failed: missing block: B:454:0x07f2, code lost:
        r44 = r4;
        r3 = false;
        r6 = r36;
        r9 = false;
        r14 = r42;
        r15 = r43;
        r25 = 8;
     */
    /* JADX WARN: Code restructure failed: missing block: B:456:0x0808, code lost:
        r44 = r4;
        r3 = false;
        r6 = r36;
        r9 = false;
        r25 = 8;
     */
    /* JADX WARN: Code restructure failed: missing block: B:461:0x0838, code lost:
        android.p008os.Trace.traceEnd(8);
     */
    /* JADX WARN: Code restructure failed: missing block: B:464:0x083d, code lost:
        r44 = r4;
        r25 = 8;
        r3 = false;
        r6 = r36;
        r9 = false;
     */
    /* JADX WARN: Code restructure failed: missing block: B:467:0x0857, code lost:
        android.p008os.Trace.traceEnd(r25);
     */
    /* JADX WARN: Code restructure failed: missing block: B:684:0x0c52, code lost:
        if (r54.mFirst == false) goto L345;
     */
    /* JADX WARN: Removed duplicated region for block: B:107:0x0204  */
    /* JADX WARN: Removed duplicated region for block: B:110:0x0214  */
    /* JADX WARN: Removed duplicated region for block: B:113:0x021c  */
    /* JADX WARN: Removed duplicated region for block: B:122:0x023a  */
    /* JADX WARN: Removed duplicated region for block: B:133:0x0270  */
    /* JADX WARN: Removed duplicated region for block: B:136:0x0276  */
    /* JADX WARN: Removed duplicated region for block: B:140:0x029e  */
    /* JADX WARN: Removed duplicated region for block: B:142:0x02a4  */
    /* JADX WARN: Removed duplicated region for block: B:171:0x02fc  */
    /* JADX WARN: Removed duplicated region for block: B:177:0x030e  */
    /* JADX WARN: Removed duplicated region for block: B:184:0x0328  */
    /* JADX WARN: Removed duplicated region for block: B:185:0x032b  */
    /* JADX WARN: Removed duplicated region for block: B:188:0x033b  */
    /* JADX WARN: Removed duplicated region for block: B:189:0x0345  */
    /* JADX WARN: Removed duplicated region for block: B:191:0x034b  */
    /* JADX WARN: Removed duplicated region for block: B:199:0x037a  */
    /* JADX WARN: Removed duplicated region for block: B:202:0x0382 A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:212:0x03ad  */
    /* JADX WARN: Removed duplicated region for block: B:217:0x03f2  */
    /* JADX WARN: Removed duplicated region for block: B:220:0x03ff  */
    /* JADX WARN: Removed duplicated region for block: B:224:0x0417 A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:233:0x0437  */
    /* JADX WARN: Removed duplicated region for block: B:234:0x0439  */
    /* JADX WARN: Removed duplicated region for block: B:252:0x0474  */
    /* JADX WARN: Removed duplicated region for block: B:253:0x0476  */
    /* JADX WARN: Removed duplicated region for block: B:274:0x04c7  */
    /* JADX WARN: Removed duplicated region for block: B:277:0x04cd A[Catch: all -> 0x04ed, RemoteException -> 0x04f8, TRY_ENTER, TryCatch #30 {RemoteException -> 0x04f8, all -> 0x04ed, blocks: (B:267:0x04a0, B:277:0x04cd, B:279:0x04d7, B:283:0x04e5), top: B:792:0x04a0 }] */
    /* JADX WARN: Removed duplicated region for block: B:281:0x04e2  */
    /* JADX WARN: Removed duplicated region for block: B:282:0x04e4  */
    /* JADX WARN: Removed duplicated region for block: B:291:0x0517  */
    /* JADX WARN: Removed duplicated region for block: B:304:0x0561  */
    /* JADX WARN: Removed duplicated region for block: B:307:0x056b  */
    /* JADX WARN: Removed duplicated region for block: B:308:0x056d  */
    /* JADX WARN: Removed duplicated region for block: B:311:0x0578 A[Catch: all -> 0x0529, RemoteException -> 0x0536, TRY_ENTER, TryCatch #32 {RemoteException -> 0x0536, all -> 0x0529, blocks: (B:294:0x0521, B:311:0x0578, B:317:0x0587, B:331:0x05ad, B:333:0x05b5, B:339:0x05c9, B:341:0x05d1, B:347:0x05e4, B:349:0x05f1, B:364:0x062f, B:406:0x06c0, B:370:0x0645, B:372:0x0649, B:373:0x064c, B:375:0x0657, B:376:0x065d, B:378:0x0661, B:379:0x0664, B:381:0x066a, B:388:0x067c, B:390:0x0682, B:392:0x068a, B:393:0x068d, B:396:0x0698), top: B:789:0x0521, inners: #4 }] */
    /* JADX WARN: Removed duplicated region for block: B:317:0x0587 A[Catch: all -> 0x0529, RemoteException -> 0x0536, TRY_LEAVE, TryCatch #32 {RemoteException -> 0x0536, all -> 0x0529, blocks: (B:294:0x0521, B:311:0x0578, B:317:0x0587, B:331:0x05ad, B:333:0x05b5, B:339:0x05c9, B:341:0x05d1, B:347:0x05e4, B:349:0x05f1, B:364:0x062f, B:406:0x06c0, B:370:0x0645, B:372:0x0649, B:373:0x064c, B:375:0x0657, B:376:0x065d, B:378:0x0661, B:379:0x0664, B:381:0x066a, B:388:0x067c, B:390:0x0682, B:392:0x068a, B:393:0x068d, B:396:0x0698), top: B:789:0x0521, inners: #4 }] */
    /* JADX WARN: Removed duplicated region for block: B:324:0x059c A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:331:0x05ad A[Catch: all -> 0x0529, RemoteException -> 0x0536, TRY_ENTER, TryCatch #32 {RemoteException -> 0x0536, all -> 0x0529, blocks: (B:294:0x0521, B:311:0x0578, B:317:0x0587, B:331:0x05ad, B:333:0x05b5, B:339:0x05c9, B:341:0x05d1, B:347:0x05e4, B:349:0x05f1, B:364:0x062f, B:406:0x06c0, B:370:0x0645, B:372:0x0649, B:373:0x064c, B:375:0x0657, B:376:0x065d, B:378:0x0661, B:379:0x0664, B:381:0x066a, B:388:0x067c, B:390:0x0682, B:392:0x068a, B:393:0x068d, B:396:0x0698), top: B:789:0x0521, inners: #4 }] */
    /* JADX WARN: Removed duplicated region for block: B:333:0x05b5 A[Catch: all -> 0x0529, RemoteException -> 0x0536, TRY_LEAVE, TryCatch #32 {RemoteException -> 0x0536, all -> 0x0529, blocks: (B:294:0x0521, B:311:0x0578, B:317:0x0587, B:331:0x05ad, B:333:0x05b5, B:339:0x05c9, B:341:0x05d1, B:347:0x05e4, B:349:0x05f1, B:364:0x062f, B:406:0x06c0, B:370:0x0645, B:372:0x0649, B:373:0x064c, B:375:0x0657, B:376:0x065d, B:378:0x0661, B:379:0x0664, B:381:0x066a, B:388:0x067c, B:390:0x0682, B:392:0x068a, B:393:0x068d, B:396:0x0698), top: B:789:0x0521, inners: #4 }] */
    /* JADX WARN: Removed duplicated region for block: B:337:0x05c4  */
    /* JADX WARN: Removed duplicated region for block: B:339:0x05c9 A[Catch: all -> 0x0529, RemoteException -> 0x0536, TRY_ENTER, TryCatch #32 {RemoteException -> 0x0536, all -> 0x0529, blocks: (B:294:0x0521, B:311:0x0578, B:317:0x0587, B:331:0x05ad, B:333:0x05b5, B:339:0x05c9, B:341:0x05d1, B:347:0x05e4, B:349:0x05f1, B:364:0x062f, B:406:0x06c0, B:370:0x0645, B:372:0x0649, B:373:0x064c, B:375:0x0657, B:376:0x065d, B:378:0x0661, B:379:0x0664, B:381:0x066a, B:388:0x067c, B:390:0x0682, B:392:0x068a, B:393:0x068d, B:396:0x0698), top: B:789:0x0521, inners: #4 }] */
    /* JADX WARN: Removed duplicated region for block: B:346:0x05e3  */
    /* JADX WARN: Removed duplicated region for block: B:369:0x0643  */
    /* JADX WARN: Removed duplicated region for block: B:38:0x008c  */
    /* JADX WARN: Removed duplicated region for block: B:403:0x06b0  */
    /* JADX WARN: Removed duplicated region for block: B:417:0x06f4  */
    /* JADX WARN: Removed duplicated region for block: B:420:0x06fc  */
    /* JADX WARN: Removed duplicated region for block: B:425:0x0718  */
    /* JADX WARN: Removed duplicated region for block: B:43:0x00a7  */
    /* JADX WARN: Removed duplicated region for block: B:461:0x0838  */
    /* JADX WARN: Removed duplicated region for block: B:467:0x0857  */
    /* JADX WARN: Removed duplicated region for block: B:46:0x00b1  */
    /* JADX WARN: Removed duplicated region for block: B:471:0x0872  */
    /* JADX WARN: Removed duplicated region for block: B:476:0x088a  */
    /* JADX WARN: Removed duplicated region for block: B:510:0x0939  */
    /* JADX WARN: Removed duplicated region for block: B:513:0x0941  */
    /* JADX WARN: Removed duplicated region for block: B:525:0x0971  */
    /* JADX WARN: Removed duplicated region for block: B:537:0x09b7  */
    /* JADX WARN: Removed duplicated region for block: B:538:0x09cb  */
    /* JADX WARN: Removed duplicated region for block: B:541:0x09d3  */
    /* JADX WARN: Removed duplicated region for block: B:542:0x09e7  */
    /* JADX WARN: Removed duplicated region for block: B:544:0x09eb  */
    /* JADX WARN: Removed duplicated region for block: B:549:0x09f9  */
    /* JADX WARN: Removed duplicated region for block: B:552:0x0a12  */
    /* JADX WARN: Removed duplicated region for block: B:558:0x0a27 A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:565:0x0a39  */
    /* JADX WARN: Removed duplicated region for block: B:573:0x0a47  */
    /* JADX WARN: Removed duplicated region for block: B:580:0x0a54  */
    /* JADX WARN: Removed duplicated region for block: B:591:0x0ace  */
    /* JADX WARN: Removed duplicated region for block: B:594:0x0ad9  */
    /* JADX WARN: Removed duplicated region for block: B:595:0x0ae1  */
    /* JADX WARN: Removed duplicated region for block: B:601:0x0af3  */
    /* JADX WARN: Removed duplicated region for block: B:603:0x0afa  */
    /* JADX WARN: Removed duplicated region for block: B:606:0x0b0d  */
    /* JADX WARN: Removed duplicated region for block: B:618:0x0b70  */
    /* JADX WARN: Removed duplicated region for block: B:621:0x0b87  */
    /* JADX WARN: Removed duplicated region for block: B:627:0x0b91  */
    /* JADX WARN: Removed duplicated region for block: B:63:0x0132  */
    /* JADX WARN: Removed duplicated region for block: B:652:0x0bf2  */
    /* JADX WARN: Removed duplicated region for block: B:664:0x0c0f  */
    /* JADX WARN: Removed duplicated region for block: B:681:0x0c4c  */
    /* JADX WARN: Removed duplicated region for block: B:686:0x0c56  */
    /* JADX WARN: Removed duplicated region for block: B:689:0x0c5b  */
    /* JADX WARN: Removed duplicated region for block: B:692:0x0c6d A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:697:0x0c7f  */
    /* JADX WARN: Removed duplicated region for block: B:699:0x0c86 A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:69:0x0149  */
    /* JADX WARN: Removed duplicated region for block: B:705:0x0c98 A[ADDED_TO_REGION] */
    /* JADX WARN: Removed duplicated region for block: B:712:0x0ca4  */
    /* JADX WARN: Removed duplicated region for block: B:717:0x0cbd  */
    /* JADX WARN: Removed duplicated region for block: B:719:0x0cc1  */
    /* JADX WARN: Removed duplicated region for block: B:732:0x0cfb  */
    /* JADX WARN: Removed duplicated region for block: B:754:0x0d70  */
    /* JADX WARN: Removed duplicated region for block: B:757:0x0d7a  */
    /* JADX WARN: Removed duplicated region for block: B:783:0x0449 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:790:0x0480 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:815:? A[RETURN, SYNTHETIC] */
    /* JADX WARN: Removed duplicated region for block: B:81:0x0173  */
    /* JADX WARN: Removed duplicated region for block: B:84:0x0185  */
    /* JADX WARN: Removed duplicated region for block: B:95:0x01a3  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    private void performTraversals() {
        boolean z;
        boolean supportsScreen;
        boolean z2;
        WindowManager.LayoutParams params;
        int desiredWindowHeight;
        int desiredWindowWidth;
        boolean layoutRequested;
        Rect frame;
        boolean z3;
        int viewVisibility;
        WindowManager.LayoutParams lp;
        int desiredWindowHeight2;
        int desiredWindowHeight3;
        boolean windowSizeMayChange;
        int resizeMode;
        WindowManager.LayoutParams lp2;
        WindowManager.LayoutParams lp3;
        int desiredWindowHeight4;
        WindowManager.LayoutParams lp4;
        int desiredWindowHeight5;
        boolean windowShouldResize;
        boolean computesInternalInsets;
        boolean isViewVisible;
        boolean surfaceSizeChanged;
        boolean surfaceCreated;
        boolean surfaceReplaced;
        boolean insetsPending;
        int relayoutResult;
        WindowManager.LayoutParams params2;
        boolean updatedConfiguration;
        boolean layoutRequested2;
        boolean updatedConfiguration2;
        Rect frame2;
        int viewVisibility2;
        boolean layoutRequested3;
        int width;
        boolean didLayout;
        boolean triggerGlobalLayoutListener;
        boolean didUseTransaction;
        Rect contentInsets;
        boolean needsSetInsets;
        boolean changedVisibility;
        boolean cancelAndRedraw;
        SurfaceSyncGroup surfaceSyncGroup;
        int i;
        Region region;
        BaseSurfaceHolder baseSurfaceHolder;
        boolean dispatchApplyInsets;
        long j;
        boolean hadSurface;
        int relayoutResult2;
        boolean dispatchApplyInsets2;
        int relayoutResult3;
        ThreadedRenderer threadedRenderer;
        boolean measureAgain;
        boolean cancelDraw;
        String cancelReason;
        boolean insetsPending2;
        int i2;
        int relayoutResult4;
        boolean cancelDraw2;
        boolean alwaysConsumeSystemBarsChanged;
        int desiredWindowHeight6;
        boolean windowSizeMayChange2;
        this.mLastPerformTraversalsSkipDrawReason = null;
        View host = this.mView;
        if (host == null || !this.mAdded) {
            this.mLastPerformTraversalsSkipDrawReason = host == null ? "no_host" : "not_added";
        } else if (this.mNumPausedForSync > 0) {
            if (Trace.isTagEnabled(8L)) {
                Trace.instant(8L, TextUtils.formatSimple("performTraversals#mNumPausedForSync=%d", Integer.valueOf(this.mNumPausedForSync)));
            }
            this.mLastPerformTraversalsSkipDrawReason = "paused_for_sync";
        } else {
            this.mIsInTraversal = true;
            this.mWillDrawSoon = true;
            boolean cancelDraw3 = false;
            String cancelReason2 = null;
            boolean isSyncRequest = false;
            boolean windowSizeMayChange3 = false;
            WindowManager.LayoutParams lp5 = this.mWindowAttributes;
            int viewVisibility3 = getHostVisibility();
            boolean z4 = this.mFirst;
            boolean viewVisibilityChanged = !z4 && (this.mViewVisibility != viewVisibility3 || this.mNewSurfaceNeeded || this.mAppVisibilityChanged);
            this.mAppVisibilityChanged = false;
            try {
                if (!z4) {
                    if ((this.mViewVisibility == 0) != (viewVisibility3 == 0)) {
                        z = true;
                        boolean viewUserVisibilityChanged = z;
                        boolean shouldOptimizeMeasure = shouldOptimizeMeasure(lp5);
                        CompatibilityInfo compatibilityInfo = this.mDisplay.getDisplayAdjustments().getCompatibilityInfo();
                        supportsScreen = compatibilityInfo.supportsScreen();
                        z2 = this.mLastInCompatMode;
                        if (supportsScreen != z2) {
                            this.mFullRedrawNeeded = true;
                            this.mLayoutRequested = true;
                            if (z2) {
                                lp5.privateFlags &= PackageManager.INSTALL_FAILED_PRE_APPROVAL_NOT_AVAILABLE;
                                this.mLastInCompatMode = false;
                            } else {
                                lp5.privateFlags |= 128;
                                this.mLastInCompatMode = true;
                            }
                            params = lp5;
                        } else {
                            params = null;
                        }
                        Rect frame3 = this.mWinFrame;
                        if (this.mFirst) {
                            desiredWindowHeight = frame3.width();
                            desiredWindowWidth = frame3.height();
                            if (desiredWindowHeight != this.mWidth || desiredWindowWidth != this.mHeight) {
                                this.mFullRedrawNeeded = true;
                                this.mLayoutRequested = true;
                                windowSizeMayChange3 = true;
                            }
                        } else {
                            this.mFullRedrawNeeded = true;
                            this.mLayoutRequested = true;
                            Configuration config = getConfiguration();
                            if (shouldUseDisplaySize(lp5)) {
                                Point size = new Point();
                                this.mDisplay.getRealSize(size);
                                desiredWindowHeight = size.f76x;
                                desiredWindowWidth = size.f77y;
                            } else if (lp5.width == -2 || lp5.height == -2) {
                                Rect bounds = getWindowBoundsInsetSystemBars();
                                int desiredWindowWidth2 = bounds.width();
                                desiredWindowWidth = bounds.height();
                                desiredWindowHeight = desiredWindowWidth2;
                            } else {
                                desiredWindowHeight = frame3.width();
                                desiredWindowWidth = frame3.height();
                            }
                            this.mAttachInfo.mUse32BitDrawingCache = true;
                            this.mAttachInfo.mWindowVisibility = viewVisibility3;
                            this.mAttachInfo.mRecomputeGlobalAttributes = false;
                            this.mLastConfigurationFromResources.setTo(config);
                            this.mLastSystemUiVisibility = this.mAttachInfo.mSystemUiVisibility;
                            if (this.mViewLayoutDirectionInitial == 2) {
                                host.setLayoutDirection(config.getLayoutDirection());
                            }
                            host.dispatchAttachedToWindow(this.mAttachInfo, 0);
                            this.mAttachInfo.mTreeObserver.dispatchOnWindowAttachedChange(true);
                            dispatchApplyInsets(host);
                            if (!this.mOnBackInvokedDispatcher.isOnBackInvokedCallbackEnabled()) {
                                registerCompatOnBackInvokedCallback();
                            }
                        }
                        if (viewVisibilityChanged) {
                            this.mAttachInfo.mWindowVisibility = viewVisibility3;
                            host.dispatchWindowVisibilityChanged(viewVisibility3);
                            this.mAttachInfo.mTreeObserver.dispatchOnWindowVisibilityChange(viewVisibility3);
                            if (viewUserVisibilityChanged) {
                                host.dispatchVisibilityAggregated(viewVisibility3 == 0);
                            }
                            if (viewVisibility3 != 0 || this.mNewSurfaceNeeded) {
                                endDragResizing();
                                destroyHardwareResources();
                            }
                        }
                        if (this.mAttachInfo.mWindowVisibility != 0) {
                            host.clearAccessibilityFocus();
                        }
                        getRunQueue().executeActions(this.mAttachInfo.mHandler);
                        if (this.mFirst) {
                            this.mAttachInfo.mInTouchMode = !this.mAddedTouchMode;
                            ensureTouchModeLocally(this.mAddedTouchMode);
                        }
                        layoutRequested = !this.mLayoutRequested && (!this.mStopped || this.mReportNextDraw);
                        if (layoutRequested) {
                            frame = frame3;
                            z3 = false;
                            viewVisibility = viewVisibility3;
                            lp = lp5;
                            desiredWindowHeight2 = desiredWindowWidth;
                            desiredWindowHeight3 = desiredWindowHeight;
                            windowSizeMayChange = windowSizeMayChange3;
                        } else {
                            if (this.mFirst || !(lp5.width == -2 || lp5.height == -2)) {
                                desiredWindowHeight6 = desiredWindowWidth;
                                desiredWindowHeight3 = desiredWindowHeight;
                                windowSizeMayChange2 = windowSizeMayChange3;
                            } else if (shouldUseDisplaySize(lp5)) {
                                Point size2 = new Point();
                                this.mDisplay.getRealSize(size2);
                                int desiredWindowWidth3 = size2.f76x;
                                desiredWindowHeight6 = size2.f77y;
                                desiredWindowHeight3 = desiredWindowWidth3;
                                windowSizeMayChange2 = true;
                            } else {
                                Rect bounds2 = getWindowBoundsInsetSystemBars();
                                int desiredWindowWidth4 = bounds2.width();
                                desiredWindowHeight6 = bounds2.height();
                                desiredWindowHeight3 = desiredWindowWidth4;
                                windowSizeMayChange2 = true;
                            }
                            frame = frame3;
                            z3 = false;
                            viewVisibility = viewVisibility3;
                            lp = lp5;
                            windowSizeMayChange = measureHierarchy(host, lp5, this.mView.getContext().getResources(), desiredWindowHeight3, desiredWindowHeight6, shouldOptimizeMeasure) | windowSizeMayChange2;
                            desiredWindowHeight2 = desiredWindowHeight6;
                        }
                        if (collectViewAttributes()) {
                            params = lp;
                        }
                        if (this.mAttachInfo.mForceReportNewAttributes) {
                            this.mAttachInfo.mForceReportNewAttributes = z3;
                            params = lp;
                        }
                        if (!this.mFirst || this.mAttachInfo.mViewVisibilityChanged) {
                            this.mAttachInfo.mViewVisibilityChanged = z3;
                            resizeMode = this.mSoftInputMode & 240;
                            if (resizeMode != 0) {
                                int N = this.mAttachInfo.mScrollContainers.size();
                                for (int i3 = 0; i3 < N; i3++) {
                                    if (this.mAttachInfo.mScrollContainers.get(i3).isShown()) {
                                        resizeMode = 16;
                                    }
                                }
                                if (resizeMode == 0) {
                                    resizeMode = 32;
                                }
                                lp2 = lp;
                                if ((lp2.softInputMode & 240) != resizeMode) {
                                    lp2.softInputMode = (lp2.softInputMode & (-241)) | resizeMode;
                                    params = lp2;
                                }
                            } else {
                                lp2 = lp;
                            }
                        } else {
                            lp2 = lp;
                        }
                        if (this.mApplyInsetsRequested) {
                            lp3 = lp2;
                            desiredWindowHeight4 = desiredWindowHeight2;
                        } else {
                            dispatchApplyInsets(host);
                            if (this.mLayoutRequested) {
                                lp3 = lp2;
                                desiredWindowHeight4 = desiredWindowHeight2;
                                windowSizeMayChange |= measureHierarchy(host, lp2, this.mView.getContext().getResources(), desiredWindowHeight3, desiredWindowHeight2, shouldOptimizeMeasure);
                            } else {
                                lp3 = lp2;
                                desiredWindowHeight4 = desiredWindowHeight2;
                            }
                        }
                        if (layoutRequested) {
                            this.mLayoutRequested = z3;
                        }
                        if (layoutRequested || !windowSizeMayChange) {
                            lp4 = lp3;
                            desiredWindowHeight5 = desiredWindowHeight4;
                        } else {
                            if (this.mWidth == host.getMeasuredWidth() && this.mHeight == host.getMeasuredHeight()) {
                                lp4 = lp3;
                                if (lp4.width == -2 && frame.width() < desiredWindowHeight3 && frame.width() != this.mWidth) {
                                    desiredWindowHeight5 = desiredWindowHeight4;
                                } else if (lp4.height == -2) {
                                    desiredWindowHeight5 = desiredWindowHeight4;
                                    if (frame.height() < desiredWindowHeight5) {
                                    }
                                } else {
                                    desiredWindowHeight5 = desiredWindowHeight4;
                                }
                            } else {
                                lp4 = lp3;
                                desiredWindowHeight5 = desiredWindowHeight4;
                            }
                            windowShouldResize = true;
                            boolean windowShouldResize2 = windowShouldResize | ((this.mDragResizing || !this.mPendingDragResizing) ? z3 : true);
                            computesInternalInsets = (!this.mAttachInfo.mTreeObserver.hasComputeInternalInsetsListeners() || this.mAttachInfo.mHasNonEmptyGivenInternalInsets) ? true : z3;
                            int surfaceGenerationId = this.mSurface.getGenerationId();
                            int desiredWindowHeight7 = viewVisibility;
                            isViewVisible = desiredWindowHeight7 == 0;
                            surfaceSizeChanged = false;
                            surfaceCreated = false;
                            boolean surfaceDestroyed = false;
                            surfaceReplaced = false;
                            boolean insetsPending3 = false;
                            insetsPending = this.mWindowAttributesChanged;
                            if (insetsPending) {
                                relayoutResult = 0;
                                this.mWindowAttributesChanged = false;
                                WindowManager.LayoutParams params3 = lp4;
                                params2 = params3;
                            } else {
                                relayoutResult = 0;
                                params2 = params;
                            }
                            if (params2 != null) {
                                updatedConfiguration = false;
                                if ((host.mPrivateFlags & 512) != 0 && !PixelFormat.formatHasAlpha(params2.format)) {
                                    params2.format = -3;
                                }
                                adjustLayoutParamsForCompatibility(params2);
                                controlInsetsForCompatibility(params2);
                                layoutRequested2 = layoutRequested;
                                if (this.mDispatchedSystemBarAppearance != params2.insetsFlags.appearance) {
                                    int i4 = params2.insetsFlags.appearance;
                                    this.mDispatchedSystemBarAppearance = i4;
                                    this.mView.onSystemBarAppearanceChanged(i4);
                                }
                            } else {
                                updatedConfiguration = false;
                                layoutRequested2 = layoutRequested;
                            }
                            updatedConfiguration2 = this.mFirst;
                            if (!updatedConfiguration2 || windowShouldResize2 || viewVisibilityChanged || params2 != null) {
                                frame2 = frame;
                            } else if (this.mForceNextWindowRelayout) {
                                frame2 = frame;
                            } else {
                                frame2 = frame;
                                maybeHandleWindowMove(frame2);
                                width = relayoutResult;
                                layoutRequested3 = layoutRequested2;
                                viewVisibility2 = desiredWindowHeight7;
                                if (this.mViewMeasureDeferred) {
                                    performMeasure(View.MeasureSpec.makeMeasureSpec(frame2.width(), 1073741824), View.MeasureSpec.makeMeasureSpec(frame2.height(), 1073741824));
                                }
                                if (!this.mRelayoutRequested && this.mCheckIfCanDraw) {
                                    try {
                                        cancelDraw3 = this.mWindowSession.cancelDraw(this.mWindow);
                                        cancelReason2 = "wm_sync";
                                    } catch (RemoteException e) {
                                    }
                                }
                                if (!surfaceSizeChanged || surfaceReplaced || surfaceCreated || insetsPending || this.mChildBoundingInsetsChanged) {
                                    prepareSurfaces();
                                    this.mChildBoundingInsetsChanged = false;
                                }
                                didLayout = !layoutRequested3 && (!this.mStopped || this.mReportNextDraw);
                                triggerGlobalLayoutListener = !didLayout || this.mAttachInfo.mRecomputeGlobalAttributes;
                                if (didLayout) {
                                    performLayout(lp4, this.mWidth, this.mHeight);
                                    if ((host.mPrivateFlags & 512) != 0) {
                                        host.getLocationInWindow(this.mTmpLocation);
                                        Region region2 = this.mTransparentRegion;
                                        int[] iArr = this.mTmpLocation;
                                        int i5 = iArr[0];
                                        region2.set(i5, iArr[1], (host.mRight + i5) - host.mLeft, (this.mTmpLocation[1] + host.mBottom) - host.mTop);
                                        host.gatherTransparentRegion(this.mTransparentRegion);
                                        CompatibilityInfo.Translator translator = this.mTranslator;
                                        if (translator != null) {
                                            translator.translateRegionInWindowToScreen(this.mTransparentRegion);
                                        }
                                        if (!this.mTransparentRegion.equals(this.mPreviousTransparentRegion)) {
                                            this.mPreviousTransparentRegion.set(this.mTransparentRegion);
                                            this.mFullRedrawNeeded = true;
                                            SurfaceControl sc = getSurfaceControl();
                                            if (sc.isValid()) {
                                                this.mTransaction.setTransparentRegionHint(sc, this.mTransparentRegion).apply();
                                            }
                                        }
                                    }
                                }
                                if (surfaceCreated) {
                                    notifySurfaceCreated(this.mTransaction);
                                    didUseTransaction = true;
                                } else if (surfaceReplaced) {
                                    notifySurfaceReplaced(this.mTransaction);
                                    didUseTransaction = true;
                                } else {
                                    if (surfaceDestroyed) {
                                        notifySurfaceDestroyed();
                                    }
                                    didUseTransaction = false;
                                }
                                if (didUseTransaction) {
                                    applyTransactionOnDraw(this.mTransaction);
                                }
                                if (triggerGlobalLayoutListener) {
                                    this.mAttachInfo.mRecomputeGlobalAttributes = false;
                                    this.mAttachInfo.mTreeObserver.dispatchOnGlobalLayout();
                                }
                                Rect visibleInsets = null;
                                Region touchableRegion = null;
                                int touchableInsetMode = 3;
                                boolean computedInternalInsets = false;
                                if (computesInternalInsets) {
                                    ViewTreeObserver.InternalInsetsInfo insets = this.mAttachInfo.mGivenInternalInsets;
                                    insets.reset();
                                    this.mAttachInfo.mTreeObserver.dispatchOnComputeInternalInsets(insets);
                                    this.mAttachInfo.mHasNonEmptyGivenInternalInsets = !insets.isEmpty();
                                    if (insetsPending3 || !this.mLastGivenInsets.equals(insets)) {
                                        this.mLastGivenInsets.set(insets);
                                        CompatibilityInfo.Translator translator2 = this.mTranslator;
                                        if (translator2 != null) {
                                            Rect contentInsets2 = translator2.getTranslatedContentInsets(insets.contentInsets);
                                            CompatibilityInfo.Translator translator3 = this.mTranslator;
                                            Rect contentInsets3 = insets.visibleInsets;
                                            Rect visibleInsets2 = translator3.getTranslatedVisibleInsets(contentInsets3);
                                            touchableRegion = this.mTranslator.getTranslatedTouchableArea(insets.touchableRegion);
                                            contentInsets = contentInsets2;
                                            visibleInsets = visibleInsets2;
                                        } else {
                                            contentInsets = insets.contentInsets;
                                            visibleInsets = insets.visibleInsets;
                                            touchableRegion = insets.touchableRegion;
                                        }
                                        computedInternalInsets = true;
                                    } else {
                                        contentInsets = null;
                                        visibleInsets = null;
                                    }
                                    touchableInsetMode = insets.mTouchableInsets;
                                } else {
                                    contentInsets = null;
                                }
                                needsSetInsets = computedInternalInsets;
                                if ((Objects.equals(this.mPreviousTouchableRegion, this.mTouchableRegion) && this.mTouchableRegion != null) || needsSetInsets) {
                                    if (this.mTouchableRegion != null) {
                                        if (this.mPreviousTouchableRegion == null) {
                                            this.mPreviousTouchableRegion = new Region();
                                        }
                                        this.mPreviousTouchableRegion.set(this.mTouchableRegion);
                                        if (touchableInsetMode != 3) {
                                            Log.m110e(this.mTag, "Setting touchableInsetMode to non TOUCHABLE_INSETS_REGION from OnComputeInternalInsets, while also using setTouchableRegion causes setTouchableRegion to be ignored");
                                        }
                                    } else {
                                        this.mPreviousTouchableRegion = null;
                                    }
                                    if (contentInsets == null) {
                                        i = 0;
                                        contentInsets = new Rect(0, 0, 0, 0);
                                    } else {
                                        i = 0;
                                    }
                                    if (visibleInsets == null) {
                                        visibleInsets = new Rect(i, i, i, i);
                                    }
                                    if (touchableRegion == null) {
                                        touchableRegion = this.mTouchableRegion;
                                    } else if (touchableRegion != null && (region = this.mTouchableRegion) != null) {
                                        touchableRegion.m176op(touchableRegion, region, Region.EnumC0813Op.UNION);
                                    }
                                    try {
                                        this.mWindowSession.setInsets(this.mWindow, touchableInsetMode, contentInsets, visibleInsets, touchableRegion);
                                    } catch (RemoteException e2) {
                                        throw e2.rethrowFromSystemServer();
                                    }
                                } else if (this.mTouchableRegion == null && this.mPreviousTouchableRegion != null) {
                                    this.mPreviousTouchableRegion = null;
                                    try {
                                        this.mWindowSession.clearTouchableRegion(this.mWindow);
                                    } catch (RemoteException e3) {
                                        throw e3.rethrowFromSystemServer();
                                    }
                                }
                                if (this.mFirst) {
                                    if (!sAlwaysAssignFocus && isInTouchMode()) {
                                        View focused = this.mView.findFocus();
                                        if ((focused instanceof ViewGroup) && ((ViewGroup) focused).getDescendantFocusability() == 262144) {
                                            focused.restoreDefaultFocus();
                                        }
                                    }
                                    View view = this.mView;
                                    if (view != null && !view.hasFocus()) {
                                        this.mView.restoreDefaultFocus();
                                    }
                                }
                                if (isViewVisible) {
                                    changedVisibility = true;
                                    if (changedVisibility) {
                                        maybeFireAccessibilityWindowStateChangedEvent();
                                    }
                                    this.mFirst = false;
                                    this.mWillDrawSoon = false;
                                    this.mNewSurfaceNeeded = false;
                                    this.mViewVisibility = viewVisibility2;
                                    boolean hasWindowFocus = !this.mAttachInfo.mHasWindowFocus && isViewVisible;
                                    this.mImeFocusController.onTraversal(hasWindowFocus, this.mWindowAttributes);
                                    if ((width & 1) != 0) {
                                        reportNextDraw("first_relayout");
                                    }
                                    this.mCheckIfCanDraw = !isSyncRequest || cancelDraw3;
                                    boolean cancelDueToPreDrawListener = this.mAttachInfo.mTreeObserver.dispatchOnPreDraw();
                                    cancelAndRedraw = !cancelDueToPreDrawListener || (cancelDraw3 && this.mDrewOnceForSync);
                                    if (!cancelAndRedraw) {
                                        if (this.mActiveSurfaceSyncGroup != null) {
                                            this.mSyncBuffer = true;
                                        }
                                        createSyncIfNeeded();
                                        notifyDrawStarted(isInWMSRequestedSync());
                                        this.mDrewOnceForSync = true;
                                    }
                                    if (isViewVisible) {
                                        this.mLastPerformTraversalsSkipDrawReason = "view_not_visible";
                                        ArrayList<LayoutTransition> arrayList = this.mPendingTransitions;
                                        if (arrayList != null && arrayList.size() > 0) {
                                            int i6 = 0;
                                            while (true) {
                                                Rect visibleInsets3 = visibleInsets;
                                                if (i6 >= this.mPendingTransitions.size()) {
                                                    break;
                                                }
                                                this.mPendingTransitions.get(i6).endChangingAnimations();
                                                i6++;
                                                visibleInsets = visibleInsets3;
                                            }
                                            this.mPendingTransitions.clear();
                                        }
                                        SurfaceSyncGroup surfaceSyncGroup2 = this.mActiveSurfaceSyncGroup;
                                        if (surfaceSyncGroup2 != null) {
                                            surfaceSyncGroup2.markSyncReady();
                                        }
                                    } else if (cancelAndRedraw) {
                                        this.mLastPerformTraversalsSkipDrawReason = cancelDueToPreDrawListener ? "predraw_" + this.mAttachInfo.mTreeObserver.getLastDispatchOnPreDrawCanceledReason() : "cancel_" + cancelReason2;
                                        scheduleTraversals();
                                    } else {
                                        ArrayList<LayoutTransition> arrayList2 = this.mPendingTransitions;
                                        if (arrayList2 != null && arrayList2.size() > 0) {
                                            for (int i7 = 0; i7 < this.mPendingTransitions.size(); i7++) {
                                                this.mPendingTransitions.get(i7).startChangingAnimations();
                                            }
                                            this.mPendingTransitions.clear();
                                        }
                                        if (!performDraw() && (surfaceSyncGroup = this.mActiveSurfaceSyncGroup) != null) {
                                            surfaceSyncGroup.markSyncReady();
                                        }
                                    }
                                    if (this.mAttachInfo.mContentCaptureEvents != null) {
                                        notifyContentCaptureEvents();
                                    }
                                    this.mIsInTraversal = false;
                                    this.mRelayoutRequested = false;
                                    if (cancelAndRedraw) {
                                        this.mReportNextDraw = false;
                                        this.mLastReportNextDrawReason = null;
                                        this.mActiveSurfaceSyncGroup = null;
                                        this.mSyncBuffer = false;
                                        if (isInWMSRequestedSync()) {
                                            this.mWmsRequestSyncGroup.markSyncReady();
                                            this.mWmsRequestSyncGroup = null;
                                            this.mWmsRequestSyncGroupState = 0;
                                            return;
                                        }
                                        return;
                                    }
                                    return;
                                }
                                changedVisibility = false;
                                if (changedVisibility) {
                                }
                                this.mFirst = false;
                                this.mWillDrawSoon = false;
                                this.mNewSurfaceNeeded = false;
                                this.mViewVisibility = viewVisibility2;
                                if (this.mAttachInfo.mHasWindowFocus) {
                                }
                                this.mImeFocusController.onTraversal(hasWindowFocus, this.mWindowAttributes);
                                if ((width & 1) != 0) {
                                }
                                this.mCheckIfCanDraw = !isSyncRequest || cancelDraw3;
                                boolean cancelDueToPreDrawListener2 = this.mAttachInfo.mTreeObserver.dispatchOnPreDraw();
                                if (cancelDueToPreDrawListener2) {
                                }
                                if (!cancelAndRedraw) {
                                }
                                if (isViewVisible) {
                                }
                                if (this.mAttachInfo.mContentCaptureEvents != null) {
                                }
                                this.mIsInTraversal = false;
                                this.mRelayoutRequested = false;
                                if (cancelAndRedraw) {
                                }
                            }
                            if (Trace.isTagEnabled(8L)) {
                                Object[] objArr = new Object[5];
                                objArr[0] = Boolean.valueOf(this.mFirst);
                                objArr[1] = Boolean.valueOf(windowShouldResize2);
                                objArr[2] = Boolean.valueOf(viewVisibilityChanged);
                                objArr[3] = Boolean.valueOf(params2 != null);
                                objArr[4] = Boolean.valueOf(this.mForceNextWindowRelayout);
                                viewVisibility2 = desiredWindowHeight7;
                                Trace.traceBegin(8L, TextUtils.formatSimple("relayoutWindow#first=%b/resize=%b/vis=%b/params=%b/force=%b", objArr));
                            } else {
                                viewVisibility2 = desiredWindowHeight7;
                            }
                            this.mForceNextWindowRelayout = false;
                            baseSurfaceHolder = this.mSurfaceHolder;
                            if (baseSurfaceHolder != null) {
                                baseSurfaceHolder.mSurfaceLock.lock();
                                this.mDrawingAllowed = true;
                            }
                            boolean hwInitialized = false;
                            boolean hadSurface2 = this.mSurface.isValid();
                            dispatchApplyInsets = false;
                            if (!this.mFirst || viewVisibilityChanged) {
                                cancelDraw = false;
                                cancelReason = null;
                                this.mViewFrameInfo.flags |= 1;
                            } else {
                                cancelDraw = false;
                                cancelReason = null;
                            }
                            int relayoutResult5 = relayoutWindow(params2, viewVisibility2, computesInternalInsets);
                            cancelDraw3 = (relayoutResult5 & 16) == 16;
                            cancelReason2 = "relayout";
                            boolean insetsPending4 = computesInternalInsets;
                            insetsPending2 = this.mPendingDragResizing;
                            i2 = this.mSyncSeqId;
                            if (i2 > this.mLastSyncSeqId) {
                                try {
                                    this.mLastSyncSeqId = i2;
                                    reportNextDraw("relayout");
                                    this.mSyncBuffer = true;
                                    isSyncRequest = true;
                                    if (!cancelDraw3) {
                                        this.mDrewOnceForSync = false;
                                    }
                                } catch (RemoteException e4) {
                                    relayoutResult2 = relayoutResult5;
                                    hadSurface = false;
                                    dispatchApplyInsets2 = false;
                                    j = 8;
                                    if (Trace.isTagEnabled(j)) {
                                    }
                                    hwInitialized = hadSurface;
                                    dispatchApplyInsets = dispatchApplyInsets2;
                                    this.mAttachInfo.mWindowLeft = frame2.left;
                                    this.mAttachInfo.mWindowTop = frame2.top;
                                    if (this.mWidth == frame2.width()) {
                                    }
                                    this.mWidth = frame2.width();
                                    this.mHeight = frame2.height();
                                    if (this.mSurfaceHolder == null) {
                                    }
                                    threadedRenderer = this.mAttachInfo.mThreadedRenderer;
                                    if (threadedRenderer != null) {
                                    }
                                    if (this.mStopped) {
                                    }
                                    int childWidthMeasureSpec = getRootMeasureSpec(this.mWidth, lp4.width, lp4.privateFlags);
                                    int childHeightMeasureSpec = getRootMeasureSpec(this.mHeight, lp4.height, lp4.privateFlags);
                                    performMeasure(childWidthMeasureSpec, childHeightMeasureSpec);
                                    int width2 = host.getMeasuredWidth();
                                    int height = host.getMeasuredHeight();
                                    measureAgain = false;
                                    if (lp4.horizontalWeight <= 0.0f) {
                                    }
                                    if (lp4.verticalWeight <= 0.0f) {
                                    }
                                    if (measureAgain) {
                                    }
                                    layoutRequested3 = true;
                                    width = relayoutResult3;
                                    insetsPending3 = insetsPending4;
                                    if (this.mViewMeasureDeferred) {
                                    }
                                    if (!this.mRelayoutRequested) {
                                    }
                                    if (!surfaceSizeChanged) {
                                    }
                                    prepareSurfaces();
                                    this.mChildBoundingInsetsChanged = false;
                                    didLayout = !layoutRequested3 && (!this.mStopped || this.mReportNextDraw);
                                    triggerGlobalLayoutListener = !didLayout || this.mAttachInfo.mRecomputeGlobalAttributes;
                                    if (didLayout) {
                                    }
                                    if (surfaceCreated) {
                                    }
                                    if (didUseTransaction) {
                                    }
                                    if (triggerGlobalLayoutListener) {
                                    }
                                    Rect visibleInsets4 = null;
                                    Region touchableRegion2 = null;
                                    int touchableInsetMode2 = 3;
                                    boolean computedInternalInsets2 = false;
                                    if (computesInternalInsets) {
                                    }
                                    needsSetInsets = computedInternalInsets2;
                                    if ((Objects.equals(this.mPreviousTouchableRegion, this.mTouchableRegion) && this.mTouchableRegion != null) | needsSetInsets) {
                                    }
                                    if (this.mFirst) {
                                    }
                                    if (isViewVisible) {
                                    }
                                    changedVisibility = false;
                                    if (changedVisibility) {
                                    }
                                    this.mFirst = false;
                                    this.mWillDrawSoon = false;
                                    this.mNewSurfaceNeeded = false;
                                    this.mViewVisibility = viewVisibility2;
                                    if (this.mAttachInfo.mHasWindowFocus) {
                                    }
                                    this.mImeFocusController.onTraversal(hasWindowFocus, this.mWindowAttributes);
                                    if ((width & 1) != 0) {
                                    }
                                    this.mCheckIfCanDraw = !isSyncRequest || cancelDraw3;
                                    boolean cancelDueToPreDrawListener22 = this.mAttachInfo.mTreeObserver.dispatchOnPreDraw();
                                    if (cancelDueToPreDrawListener22) {
                                    }
                                    if (!cancelAndRedraw) {
                                    }
                                    if (isViewVisible) {
                                    }
                                    if (this.mAttachInfo.mContentCaptureEvents != null) {
                                    }
                                    this.mIsInTraversal = false;
                                    this.mRelayoutRequested = false;
                                    if (cancelAndRedraw) {
                                    }
                                } catch (Throwable th) {
                                    th = th;
                                    if (Trace.isTagEnabled(8L)) {
                                    }
                                    throw th;
                                }
                            }
                            boolean surfaceControlChanged = (relayoutResult5 & 2) == 2;
                            if (this.mSurfaceControl.isValid()) {
                                try {
                                    updateOpacity(this.mWindowAttributes, insetsPending2, surfaceControlChanged);
                                    if (surfaceControlChanged && this.mDisplayDecorationCached) {
                                        updateDisplayDecoration();
                                    }
                                    if (surfaceControlChanged && this.mWindowAttributes.type == 2000) {
                                        relayoutResult4 = relayoutResult5;
                                        try {
                                            this.mTransaction.setDefaultFrameRateCompatibility(this.mSurfaceControl, 101).apply();
                                        } catch (RemoteException e5) {
                                            relayoutResult2 = relayoutResult4;
                                            hadSurface = false;
                                            dispatchApplyInsets2 = false;
                                            j = 8;
                                            if (Trace.isTagEnabled(j)) {
                                            }
                                            hwInitialized = hadSurface;
                                            dispatchApplyInsets = dispatchApplyInsets2;
                                            this.mAttachInfo.mWindowLeft = frame2.left;
                                            this.mAttachInfo.mWindowTop = frame2.top;
                                            if (this.mWidth == frame2.width()) {
                                            }
                                            this.mWidth = frame2.width();
                                            this.mHeight = frame2.height();
                                            if (this.mSurfaceHolder == null) {
                                            }
                                            threadedRenderer = this.mAttachInfo.mThreadedRenderer;
                                            if (threadedRenderer != null) {
                                            }
                                            if (this.mStopped) {
                                            }
                                            int childWidthMeasureSpec2 = getRootMeasureSpec(this.mWidth, lp4.width, lp4.privateFlags);
                                            int childHeightMeasureSpec2 = getRootMeasureSpec(this.mHeight, lp4.height, lp4.privateFlags);
                                            performMeasure(childWidthMeasureSpec2, childHeightMeasureSpec2);
                                            int width22 = host.getMeasuredWidth();
                                            int height2 = host.getMeasuredHeight();
                                            measureAgain = false;
                                            if (lp4.horizontalWeight <= 0.0f) {
                                            }
                                            if (lp4.verticalWeight <= 0.0f) {
                                            }
                                            if (measureAgain) {
                                            }
                                            layoutRequested3 = true;
                                            width = relayoutResult3;
                                            insetsPending3 = insetsPending4;
                                            if (this.mViewMeasureDeferred) {
                                            }
                                            if (!this.mRelayoutRequested) {
                                            }
                                            if (!surfaceSizeChanged) {
                                            }
                                            prepareSurfaces();
                                            this.mChildBoundingInsetsChanged = false;
                                            didLayout = !layoutRequested3 && (!this.mStopped || this.mReportNextDraw);
                                            triggerGlobalLayoutListener = !didLayout || this.mAttachInfo.mRecomputeGlobalAttributes;
                                            if (didLayout) {
                                            }
                                            if (surfaceCreated) {
                                            }
                                            if (didUseTransaction) {
                                            }
                                            if (triggerGlobalLayoutListener) {
                                            }
                                            Rect visibleInsets42 = null;
                                            Region touchableRegion22 = null;
                                            int touchableInsetMode22 = 3;
                                            boolean computedInternalInsets22 = false;
                                            if (computesInternalInsets) {
                                            }
                                            needsSetInsets = computedInternalInsets22;
                                            if ((Objects.equals(this.mPreviousTouchableRegion, this.mTouchableRegion) && this.mTouchableRegion != null) | needsSetInsets) {
                                            }
                                            if (this.mFirst) {
                                            }
                                            if (isViewVisible) {
                                            }
                                            changedVisibility = false;
                                            if (changedVisibility) {
                                            }
                                            this.mFirst = false;
                                            this.mWillDrawSoon = false;
                                            this.mNewSurfaceNeeded = false;
                                            this.mViewVisibility = viewVisibility2;
                                            if (this.mAttachInfo.mHasWindowFocus) {
                                            }
                                            this.mImeFocusController.onTraversal(hasWindowFocus, this.mWindowAttributes);
                                            if ((width & 1) != 0) {
                                            }
                                            this.mCheckIfCanDraw = !isSyncRequest || cancelDraw3;
                                            boolean cancelDueToPreDrawListener222 = this.mAttachInfo.mTreeObserver.dispatchOnPreDraw();
                                            if (cancelDueToPreDrawListener222) {
                                            }
                                            if (!cancelAndRedraw) {
                                            }
                                            if (isViewVisible) {
                                            }
                                            if (this.mAttachInfo.mContentCaptureEvents != null) {
                                            }
                                            this.mIsInTraversal = false;
                                            this.mRelayoutRequested = false;
                                            if (cancelAndRedraw) {
                                            }
                                        } catch (Throwable th2) {
                                            th = th2;
                                            if (Trace.isTagEnabled(8L)) {
                                            }
                                            throw th;
                                        }
                                    } else {
                                        relayoutResult4 = relayoutResult5;
                                    }
                                } catch (RemoteException e6) {
                                    relayoutResult2 = relayoutResult5;
                                    hadSurface = false;
                                    dispatchApplyInsets2 = false;
                                    j = 8;
                                    if (Trace.isTagEnabled(j)) {
                                    }
                                    hwInitialized = hadSurface;
                                    dispatchApplyInsets = dispatchApplyInsets2;
                                    this.mAttachInfo.mWindowLeft = frame2.left;
                                    this.mAttachInfo.mWindowTop = frame2.top;
                                    if (this.mWidth == frame2.width()) {
                                    }
                                    this.mWidth = frame2.width();
                                    this.mHeight = frame2.height();
                                    if (this.mSurfaceHolder == null) {
                                    }
                                    threadedRenderer = this.mAttachInfo.mThreadedRenderer;
                                    if (threadedRenderer != null) {
                                    }
                                    if (this.mStopped) {
                                    }
                                    int childWidthMeasureSpec22 = getRootMeasureSpec(this.mWidth, lp4.width, lp4.privateFlags);
                                    int childHeightMeasureSpec22 = getRootMeasureSpec(this.mHeight, lp4.height, lp4.privateFlags);
                                    performMeasure(childWidthMeasureSpec22, childHeightMeasureSpec22);
                                    int width222 = host.getMeasuredWidth();
                                    int height22 = host.getMeasuredHeight();
                                    measureAgain = false;
                                    if (lp4.horizontalWeight <= 0.0f) {
                                    }
                                    if (lp4.verticalWeight <= 0.0f) {
                                    }
                                    if (measureAgain) {
                                    }
                                    layoutRequested3 = true;
                                    width = relayoutResult3;
                                    insetsPending3 = insetsPending4;
                                    if (this.mViewMeasureDeferred) {
                                    }
                                    if (!this.mRelayoutRequested) {
                                    }
                                    if (!surfaceSizeChanged) {
                                    }
                                    prepareSurfaces();
                                    this.mChildBoundingInsetsChanged = false;
                                    didLayout = !layoutRequested3 && (!this.mStopped || this.mReportNextDraw);
                                    triggerGlobalLayoutListener = !didLayout || this.mAttachInfo.mRecomputeGlobalAttributes;
                                    if (didLayout) {
                                    }
                                    if (surfaceCreated) {
                                    }
                                    if (didUseTransaction) {
                                    }
                                    if (triggerGlobalLayoutListener) {
                                    }
                                    Rect visibleInsets422 = null;
                                    Region touchableRegion222 = null;
                                    int touchableInsetMode222 = 3;
                                    boolean computedInternalInsets222 = false;
                                    if (computesInternalInsets) {
                                    }
                                    needsSetInsets = computedInternalInsets222;
                                    if ((Objects.equals(this.mPreviousTouchableRegion, this.mTouchableRegion) && this.mTouchableRegion != null) | needsSetInsets) {
                                    }
                                    if (this.mFirst) {
                                    }
                                    if (isViewVisible) {
                                    }
                                    changedVisibility = false;
                                    if (changedVisibility) {
                                    }
                                    this.mFirst = false;
                                    this.mWillDrawSoon = false;
                                    this.mNewSurfaceNeeded = false;
                                    this.mViewVisibility = viewVisibility2;
                                    if (this.mAttachInfo.mHasWindowFocus) {
                                    }
                                    this.mImeFocusController.onTraversal(hasWindowFocus, this.mWindowAttributes);
                                    if ((width & 1) != 0) {
                                    }
                                    this.mCheckIfCanDraw = !isSyncRequest || cancelDraw3;
                                    boolean cancelDueToPreDrawListener2222 = this.mAttachInfo.mTreeObserver.dispatchOnPreDraw();
                                    if (cancelDueToPreDrawListener2222) {
                                    }
                                    if (!cancelAndRedraw) {
                                    }
                                    if (isViewVisible) {
                                    }
                                    if (this.mAttachInfo.mContentCaptureEvents != null) {
                                    }
                                    this.mIsInTraversal = false;
                                    this.mRelayoutRequested = false;
                                    if (cancelAndRedraw) {
                                    }
                                } catch (Throwable th3) {
                                    th = th3;
                                    if (Trace.isTagEnabled(8L)) {
                                    }
                                    throw th;
                                }
                            } else {
                                relayoutResult4 = relayoutResult5;
                            }
                            if (this.mRelayoutRequested && !this.mPendingMergedConfiguration.equals(this.mLastReportedMergedConfiguration)) {
                                performConfigurationChange(new MergedConfiguration(this.mPendingMergedConfiguration), this.mFirst, -1);
                                updatedConfiguration = true;
                            }
                            boolean updateSurfaceNeeded = this.mUpdateSurfaceNeeded;
                            this.mUpdateSurfaceNeeded = false;
                            surfaceSizeChanged = false;
                            if (this.mLastSurfaceSize.equals(this.mSurfaceSize)) {
                                cancelDraw2 = cancelDraw3;
                            } else {
                                surfaceSizeChanged = true;
                                try {
                                    cancelDraw2 = cancelDraw3;
                                    try {
                                        this.mLastSurfaceSize.set(this.mSurfaceSize.f76x, this.mSurfaceSize.f77y);
                                    } catch (RemoteException e7) {
                                        relayoutResult2 = relayoutResult4;
                                        hadSurface = false;
                                        cancelDraw3 = cancelDraw2;
                                        dispatchApplyInsets2 = dispatchApplyInsets;
                                        j = 8;
                                        if (Trace.isTagEnabled(j)) {
                                        }
                                        hwInitialized = hadSurface;
                                        dispatchApplyInsets = dispatchApplyInsets2;
                                        this.mAttachInfo.mWindowLeft = frame2.left;
                                        this.mAttachInfo.mWindowTop = frame2.top;
                                        if (this.mWidth == frame2.width()) {
                                        }
                                        this.mWidth = frame2.width();
                                        this.mHeight = frame2.height();
                                        if (this.mSurfaceHolder == null) {
                                        }
                                        threadedRenderer = this.mAttachInfo.mThreadedRenderer;
                                        if (threadedRenderer != null) {
                                        }
                                        if (this.mStopped) {
                                        }
                                        int childWidthMeasureSpec222 = getRootMeasureSpec(this.mWidth, lp4.width, lp4.privateFlags);
                                        int childHeightMeasureSpec222 = getRootMeasureSpec(this.mHeight, lp4.height, lp4.privateFlags);
                                        performMeasure(childWidthMeasureSpec222, childHeightMeasureSpec222);
                                        int width2222 = host.getMeasuredWidth();
                                        int height222 = host.getMeasuredHeight();
                                        measureAgain = false;
                                        if (lp4.horizontalWeight <= 0.0f) {
                                        }
                                        if (lp4.verticalWeight <= 0.0f) {
                                        }
                                        if (measureAgain) {
                                        }
                                        layoutRequested3 = true;
                                        width = relayoutResult3;
                                        insetsPending3 = insetsPending4;
                                        if (this.mViewMeasureDeferred) {
                                        }
                                        if (!this.mRelayoutRequested) {
                                        }
                                        if (!surfaceSizeChanged) {
                                        }
                                        prepareSurfaces();
                                        this.mChildBoundingInsetsChanged = false;
                                        didLayout = !layoutRequested3 && (!this.mStopped || this.mReportNextDraw);
                                        triggerGlobalLayoutListener = !didLayout || this.mAttachInfo.mRecomputeGlobalAttributes;
                                        if (didLayout) {
                                        }
                                        if (surfaceCreated) {
                                        }
                                        if (didUseTransaction) {
                                        }
                                        if (triggerGlobalLayoutListener) {
                                        }
                                        Rect visibleInsets4222 = null;
                                        Region touchableRegion2222 = null;
                                        int touchableInsetMode2222 = 3;
                                        boolean computedInternalInsets2222 = false;
                                        if (computesInternalInsets) {
                                        }
                                        needsSetInsets = computedInternalInsets2222;
                                        if ((Objects.equals(this.mPreviousTouchableRegion, this.mTouchableRegion) && this.mTouchableRegion != null) | needsSetInsets) {
                                        }
                                        if (this.mFirst) {
                                        }
                                        if (isViewVisible) {
                                        }
                                        changedVisibility = false;
                                        if (changedVisibility) {
                                        }
                                        this.mFirst = false;
                                        this.mWillDrawSoon = false;
                                        this.mNewSurfaceNeeded = false;
                                        this.mViewVisibility = viewVisibility2;
                                        if (this.mAttachInfo.mHasWindowFocus) {
                                        }
                                        this.mImeFocusController.onTraversal(hasWindowFocus, this.mWindowAttributes);
                                        if ((width & 1) != 0) {
                                        }
                                        this.mCheckIfCanDraw = !isSyncRequest || cancelDraw3;
                                        boolean cancelDueToPreDrawListener22222 = this.mAttachInfo.mTreeObserver.dispatchOnPreDraw();
                                        if (cancelDueToPreDrawListener22222) {
                                        }
                                        if (!cancelAndRedraw) {
                                        }
                                        if (isViewVisible) {
                                        }
                                        if (this.mAttachInfo.mContentCaptureEvents != null) {
                                        }
                                        this.mIsInTraversal = false;
                                        this.mRelayoutRequested = false;
                                        if (cancelAndRedraw) {
                                        }
                                    } catch (Throwable th4) {
                                        th = th4;
                                        if (Trace.isTagEnabled(8L)) {
                                        }
                                        throw th;
                                    }
                                } catch (RemoteException e8) {
                                    relayoutResult2 = relayoutResult4;
                                    hadSurface = false;
                                    dispatchApplyInsets2 = false;
                                    j = 8;
                                    if (Trace.isTagEnabled(j)) {
                                    }
                                    hwInitialized = hadSurface;
                                    dispatchApplyInsets = dispatchApplyInsets2;
                                    this.mAttachInfo.mWindowLeft = frame2.left;
                                    this.mAttachInfo.mWindowTop = frame2.top;
                                    if (this.mWidth == frame2.width()) {
                                    }
                                    this.mWidth = frame2.width();
                                    this.mHeight = frame2.height();
                                    if (this.mSurfaceHolder == null) {
                                    }
                                    threadedRenderer = this.mAttachInfo.mThreadedRenderer;
                                    if (threadedRenderer != null) {
                                    }
                                    if (this.mStopped) {
                                    }
                                    int childWidthMeasureSpec2222 = getRootMeasureSpec(this.mWidth, lp4.width, lp4.privateFlags);
                                    int childHeightMeasureSpec2222 = getRootMeasureSpec(this.mHeight, lp4.height, lp4.privateFlags);
                                    performMeasure(childWidthMeasureSpec2222, childHeightMeasureSpec2222);
                                    int width22222 = host.getMeasuredWidth();
                                    int height2222 = host.getMeasuredHeight();
                                    measureAgain = false;
                                    if (lp4.horizontalWeight <= 0.0f) {
                                    }
                                    if (lp4.verticalWeight <= 0.0f) {
                                    }
                                    if (measureAgain) {
                                    }
                                    layoutRequested3 = true;
                                    width = relayoutResult3;
                                    insetsPending3 = insetsPending4;
                                    if (this.mViewMeasureDeferred) {
                                    }
                                    if (!this.mRelayoutRequested) {
                                    }
                                    if (!surfaceSizeChanged) {
                                    }
                                    prepareSurfaces();
                                    this.mChildBoundingInsetsChanged = false;
                                    didLayout = !layoutRequested3 && (!this.mStopped || this.mReportNextDraw);
                                    triggerGlobalLayoutListener = !didLayout || this.mAttachInfo.mRecomputeGlobalAttributes;
                                    if (didLayout) {
                                    }
                                    if (surfaceCreated) {
                                    }
                                    if (didUseTransaction) {
                                    }
                                    if (triggerGlobalLayoutListener) {
                                    }
                                    Rect visibleInsets42222 = null;
                                    Region touchableRegion22222 = null;
                                    int touchableInsetMode22222 = 3;
                                    boolean computedInternalInsets22222 = false;
                                    if (computesInternalInsets) {
                                    }
                                    needsSetInsets = computedInternalInsets22222;
                                    if ((Objects.equals(this.mPreviousTouchableRegion, this.mTouchableRegion) && this.mTouchableRegion != null) | needsSetInsets) {
                                    }
                                    if (this.mFirst) {
                                    }
                                    if (isViewVisible) {
                                    }
                                    changedVisibility = false;
                                    if (changedVisibility) {
                                    }
                                    this.mFirst = false;
                                    this.mWillDrawSoon = false;
                                    this.mNewSurfaceNeeded = false;
                                    this.mViewVisibility = viewVisibility2;
                                    if (this.mAttachInfo.mHasWindowFocus) {
                                    }
                                    this.mImeFocusController.onTraversal(hasWindowFocus, this.mWindowAttributes);
                                    if ((width & 1) != 0) {
                                    }
                                    this.mCheckIfCanDraw = !isSyncRequest || cancelDraw3;
                                    boolean cancelDueToPreDrawListener222222 = this.mAttachInfo.mTreeObserver.dispatchOnPreDraw();
                                    if (cancelDueToPreDrawListener222222) {
                                    }
                                    if (!cancelAndRedraw) {
                                    }
                                    if (isViewVisible) {
                                    }
                                    if (this.mAttachInfo.mContentCaptureEvents != null) {
                                    }
                                    this.mIsInTraversal = false;
                                    this.mRelayoutRequested = false;
                                    if (cancelAndRedraw) {
                                    }
                                } catch (Throwable th5) {
                                    th = th5;
                                    if (Trace.isTagEnabled(8L)) {
                                    }
                                    throw th;
                                }
                            }
                            alwaysConsumeSystemBarsChanged = this.mPendingAlwaysConsumeSystemBars != this.mAttachInfo.mAlwaysConsumeSystemBars;
                            updateColorModeIfNeeded(lp4.getColorMode());
                            surfaceCreated = hadSurface2 && this.mSurface.isValid();
                            surfaceDestroyed = (hadSurface2 || this.mSurface.isValid()) ? false : true;
                            surfaceReplaced = (surfaceGenerationId == this.mSurface.getGenerationId() || surfaceControlChanged) && this.mSurface.isValid();
                            if (surfaceReplaced) {
                                this.mSurfaceSequenceId++;
                            }
                            if (alwaysConsumeSystemBarsChanged) {
                                this.mAttachInfo.mAlwaysConsumeSystemBars = this.mPendingAlwaysConsumeSystemBars;
                                dispatchApplyInsets = true;
                            }
                            if (updateCaptionInsets()) {
                                dispatchApplyInsets = true;
                            }
                            if (!dispatchApplyInsets || this.mLastSystemUiVisibility != this.mAttachInfo.mSystemUiVisibility || this.mApplyInsetsRequested) {
                                this.mLastSystemUiVisibility = this.mAttachInfo.mSystemUiVisibility;
                                dispatchApplyInsets(host);
                                dispatchApplyInsets = true;
                            }
                            if (surfaceCreated) {
                                this.mFullRedrawNeeded = true;
                                this.mPreviousTransparentRegion.setEmpty();
                                if (this.mAttachInfo.mThreadedRenderer != null) {
                                    try {
                                        boolean hwInitialized2 = this.mAttachInfo.mThreadedRenderer.initialize(this.mSurface);
                                        if (hwInitialized2) {
                                            try {
                                                if ((host.mPrivateFlags & 512) == 0) {
                                                    this.mAttachInfo.mThreadedRenderer.allocateBuffers();
                                                }
                                            } catch (RemoteException e9) {
                                                hadSurface = hwInitialized2;
                                                relayoutResult2 = relayoutResult4;
                                                cancelDraw3 = cancelDraw2;
                                                dispatchApplyInsets2 = dispatchApplyInsets;
                                                j = 8;
                                                if (Trace.isTagEnabled(j)) {
                                                }
                                                hwInitialized = hadSurface;
                                                dispatchApplyInsets = dispatchApplyInsets2;
                                                this.mAttachInfo.mWindowLeft = frame2.left;
                                                this.mAttachInfo.mWindowTop = frame2.top;
                                                if (this.mWidth == frame2.width()) {
                                                }
                                                this.mWidth = frame2.width();
                                                this.mHeight = frame2.height();
                                                if (this.mSurfaceHolder == null) {
                                                }
                                                threadedRenderer = this.mAttachInfo.mThreadedRenderer;
                                                if (threadedRenderer != null) {
                                                }
                                                if (this.mStopped) {
                                                }
                                                int childWidthMeasureSpec22222 = getRootMeasureSpec(this.mWidth, lp4.width, lp4.privateFlags);
                                                int childHeightMeasureSpec22222 = getRootMeasureSpec(this.mHeight, lp4.height, lp4.privateFlags);
                                                performMeasure(childWidthMeasureSpec22222, childHeightMeasureSpec22222);
                                                int width222222 = host.getMeasuredWidth();
                                                int height22222 = host.getMeasuredHeight();
                                                measureAgain = false;
                                                if (lp4.horizontalWeight <= 0.0f) {
                                                }
                                                if (lp4.verticalWeight <= 0.0f) {
                                                }
                                                if (measureAgain) {
                                                }
                                                layoutRequested3 = true;
                                                width = relayoutResult3;
                                                insetsPending3 = insetsPending4;
                                                if (this.mViewMeasureDeferred) {
                                                }
                                                if (!this.mRelayoutRequested) {
                                                }
                                                if (!surfaceSizeChanged) {
                                                }
                                                prepareSurfaces();
                                                this.mChildBoundingInsetsChanged = false;
                                                didLayout = !layoutRequested3 && (!this.mStopped || this.mReportNextDraw);
                                                triggerGlobalLayoutListener = !didLayout || this.mAttachInfo.mRecomputeGlobalAttributes;
                                                if (didLayout) {
                                                }
                                                if (surfaceCreated) {
                                                }
                                                if (didUseTransaction) {
                                                }
                                                if (triggerGlobalLayoutListener) {
                                                }
                                                Rect visibleInsets422222 = null;
                                                Region touchableRegion222222 = null;
                                                int touchableInsetMode222222 = 3;
                                                boolean computedInternalInsets222222 = false;
                                                if (computesInternalInsets) {
                                                }
                                                needsSetInsets = computedInternalInsets222222;
                                                if ((Objects.equals(this.mPreviousTouchableRegion, this.mTouchableRegion) && this.mTouchableRegion != null) | needsSetInsets) {
                                                }
                                                if (this.mFirst) {
                                                }
                                                if (isViewVisible) {
                                                }
                                                changedVisibility = false;
                                                if (changedVisibility) {
                                                }
                                                this.mFirst = false;
                                                this.mWillDrawSoon = false;
                                                this.mNewSurfaceNeeded = false;
                                                this.mViewVisibility = viewVisibility2;
                                                if (this.mAttachInfo.mHasWindowFocus) {
                                                }
                                                this.mImeFocusController.onTraversal(hasWindowFocus, this.mWindowAttributes);
                                                if ((width & 1) != 0) {
                                                }
                                                this.mCheckIfCanDraw = !isSyncRequest || cancelDraw3;
                                                boolean cancelDueToPreDrawListener2222222 = this.mAttachInfo.mTreeObserver.dispatchOnPreDraw();
                                                if (cancelDueToPreDrawListener2222222) {
                                                }
                                                if (!cancelAndRedraw) {
                                                }
                                                if (isViewVisible) {
                                                }
                                                if (this.mAttachInfo.mContentCaptureEvents != null) {
                                                }
                                                this.mIsInTraversal = false;
                                                this.mRelayoutRequested = false;
                                                if (cancelAndRedraw) {
                                                }
                                            } catch (Surface.OutOfResourcesException e10) {
                                                e = e10;
                                                handleOutOfResourcesException(e);
                                                this.mLastPerformTraversalsSkipDrawReason = "oom_initialize_renderer";
                                                if (Trace.isTagEnabled(8L)) {
                                                    Trace.traceEnd(8L);
                                                    return;
                                                }
                                                return;
                                            } catch (Throwable th6) {
                                                th = th6;
                                                if (Trace.isTagEnabled(8L)) {
                                                }
                                                throw th;
                                            }
                                        }
                                        hwInitialized = hwInitialized2;
                                    } catch (Surface.OutOfResourcesException e11) {
                                        e = e11;
                                    }
                                }
                            } else if (surfaceDestroyed) {
                                WeakReference<View> weakReference = this.mLastScrolledFocus;
                                if (weakReference != null) {
                                    weakReference.clear();
                                }
                                this.mCurScrollY = 0;
                                this.mScrollY = 0;
                                View view2 = this.mView;
                                if (view2 instanceof RootViewSurfaceTaker) {
                                    ((RootViewSurfaceTaker) view2).onRootViewScrollYChanged(0);
                                }
                                Scroller scroller = this.mScroller;
                                if (scroller != null) {
                                    scroller.abortAnimation();
                                }
                                if (isHardwareEnabled()) {
                                    this.mAttachInfo.mThreadedRenderer.destroy();
                                }
                            } else if ((surfaceReplaced || surfaceSizeChanged || updateSurfaceNeeded) && this.mSurfaceHolder == null && this.mAttachInfo.mThreadedRenderer != null && this.mSurface.isValid()) {
                                this.mFullRedrawNeeded = true;
                                try {
                                    this.mAttachInfo.mThreadedRenderer.updateSurface(this.mSurface);
                                } catch (Surface.OutOfResourcesException e12) {
                                    handleOutOfResourcesException(e12);
                                    this.mLastPerformTraversalsSkipDrawReason = "oom_update_surface";
                                    if (Trace.isTagEnabled(8L)) {
                                        Trace.traceEnd(8L);
                                        return;
                                    }
                                    return;
                                }
                            }
                            if (this.mDragResizing != insetsPending2) {
                                if (insetsPending2) {
                                    boolean backdropSizeMatchesFrame = this.mWinFrame.width() == this.mPendingBackDropFrame.width() && this.mWinFrame.height() == this.mPendingBackDropFrame.height();
                                    Rect rect = this.mPendingBackDropFrame;
                                    boolean backdropSizeMatchesFrame2 = !backdropSizeMatchesFrame;
                                    startDragResizing(rect, backdropSizeMatchesFrame2, this.mAttachInfo.mContentInsets, this.mAttachInfo.mStableInsets);
                                } else {
                                    endDragResizing();
                                }
                            }
                            if (!this.mUseMTRenderer) {
                                if (insetsPending2) {
                                    this.mCanvasOffsetX = this.mWinFrame.left;
                                    this.mCanvasOffsetY = this.mWinFrame.top;
                                } else {
                                    this.mCanvasOffsetY = 0;
                                    this.mCanvasOffsetX = 0;
                                }
                            }
                            if (Trace.isTagEnabled(8L)) {
                                Trace.traceEnd(8L);
                            }
                            relayoutResult2 = relayoutResult4;
                            cancelDraw3 = cancelDraw2;
                            this.mAttachInfo.mWindowLeft = frame2.left;
                            this.mAttachInfo.mWindowTop = frame2.top;
                            if (this.mWidth == frame2.width() || this.mHeight != frame2.height()) {
                                this.mWidth = frame2.width();
                                this.mHeight = frame2.height();
                            }
                            if (this.mSurfaceHolder == null) {
                                if (this.mSurface.isValid()) {
                                    this.mSurfaceHolder.mSurface = this.mSurface;
                                }
                                this.mSurfaceHolder.setSurfaceFrameSize(this.mWidth, this.mHeight);
                                if (surfaceCreated) {
                                    this.mSurfaceHolder.ungetCallbacks();
                                    this.mIsCreating = true;
                                    SurfaceHolder.Callback[] callbacks = this.mSurfaceHolder.getCallbacks();
                                    if (callbacks != null) {
                                        for (SurfaceHolder.Callback c : callbacks) {
                                            c.surfaceCreated(this.mSurfaceHolder);
                                        }
                                    }
                                }
                                if (!surfaceCreated && !surfaceReplaced && !surfaceSizeChanged && !insetsPending) {
                                    relayoutResult3 = relayoutResult2;
                                } else if (this.mSurface.isValid()) {
                                    SurfaceHolder.Callback[] callbacks2 = this.mSurfaceHolder.getCallbacks();
                                    if (callbacks2 != null) {
                                        int length = callbacks2.length;
                                        int i8 = 0;
                                        while (i8 < length) {
                                            SurfaceHolder.Callback c2 = callbacks2[i8];
                                            BaseSurfaceHolder baseSurfaceHolder2 = this.mSurfaceHolder;
                                            SurfaceHolder.Callback[] callbacks3 = callbacks2;
                                            int i9 = lp4.format;
                                            int i10 = length;
                                            int i11 = this.mWidth;
                                            int relayoutResult6 = relayoutResult2;
                                            int relayoutResult7 = this.mHeight;
                                            c2.surfaceChanged(baseSurfaceHolder2, i9, i11, relayoutResult7);
                                            i8++;
                                            callbacks2 = callbacks3;
                                            length = i10;
                                            relayoutResult2 = relayoutResult6;
                                        }
                                        relayoutResult3 = relayoutResult2;
                                    } else {
                                        relayoutResult3 = relayoutResult2;
                                    }
                                    this.mIsCreating = false;
                                } else {
                                    relayoutResult3 = relayoutResult2;
                                }
                                if (surfaceDestroyed) {
                                    notifyHolderSurfaceDestroyed();
                                    this.mSurfaceHolder.mSurfaceLock.lock();
                                    try {
                                        this.mSurfaceHolder.mSurface = new Surface();
                                    } finally {
                                        this.mSurfaceHolder.mSurfaceLock.unlock();
                                    }
                                }
                            } else {
                                relayoutResult3 = relayoutResult2;
                            }
                            threadedRenderer = this.mAttachInfo.mThreadedRenderer;
                            if (threadedRenderer != null && threadedRenderer.isEnabled() && (hwInitialized || this.mWidth != threadedRenderer.getWidth() || this.mHeight != threadedRenderer.getHeight() || this.mNeedsRendererSetup)) {
                                threadedRenderer.setup(this.mWidth, this.mHeight, this.mAttachInfo, this.mWindowAttributes.surfaceInsets);
                                this.mNeedsRendererSetup = false;
                            }
                            if ((this.mStopped || this.mReportNextDraw) && (this.mWidth != host.getMeasuredWidth() || this.mHeight != host.getMeasuredHeight() || dispatchApplyInsets || updatedConfiguration)) {
                                int childWidthMeasureSpec222222 = getRootMeasureSpec(this.mWidth, lp4.width, lp4.privateFlags);
                                int childHeightMeasureSpec222222 = getRootMeasureSpec(this.mHeight, lp4.height, lp4.privateFlags);
                                performMeasure(childWidthMeasureSpec222222, childHeightMeasureSpec222222);
                                int width2222222 = host.getMeasuredWidth();
                                int height222222 = host.getMeasuredHeight();
                                measureAgain = false;
                                if (lp4.horizontalWeight <= 0.0f) {
                                    measureAgain = true;
                                    childWidthMeasureSpec222222 = View.MeasureSpec.makeMeasureSpec(width2222222 + ((int) ((this.mWidth - width2222222) * lp4.horizontalWeight)), 1073741824);
                                }
                                if (lp4.verticalWeight <= 0.0f) {
                                    measureAgain = true;
                                    childHeightMeasureSpec222222 = View.MeasureSpec.makeMeasureSpec(height222222 + ((int) ((this.mHeight - height222222) * lp4.verticalWeight)), 1073741824);
                                }
                                if (measureAgain) {
                                    performMeasure(childWidthMeasureSpec222222, childHeightMeasureSpec222222);
                                }
                                layoutRequested3 = true;
                            } else {
                                layoutRequested3 = layoutRequested2;
                            }
                            width = relayoutResult3;
                            insetsPending3 = insetsPending4;
                            if (this.mViewMeasureDeferred) {
                            }
                            if (!this.mRelayoutRequested) {
                                cancelDraw3 = this.mWindowSession.cancelDraw(this.mWindow);
                                cancelReason2 = "wm_sync";
                            }
                            if (!surfaceSizeChanged) {
                            }
                            prepareSurfaces();
                            this.mChildBoundingInsetsChanged = false;
                            didLayout = !layoutRequested3 && (!this.mStopped || this.mReportNextDraw);
                            triggerGlobalLayoutListener = !didLayout || this.mAttachInfo.mRecomputeGlobalAttributes;
                            if (didLayout) {
                            }
                            if (surfaceCreated) {
                            }
                            if (didUseTransaction) {
                            }
                            if (triggerGlobalLayoutListener) {
                            }
                            Rect visibleInsets4222222 = null;
                            Region touchableRegion2222222 = null;
                            int touchableInsetMode2222222 = 3;
                            boolean computedInternalInsets2222222 = false;
                            if (computesInternalInsets) {
                            }
                            needsSetInsets = computedInternalInsets2222222;
                            if ((Objects.equals(this.mPreviousTouchableRegion, this.mTouchableRegion) && this.mTouchableRegion != null) | needsSetInsets) {
                            }
                            if (this.mFirst) {
                            }
                            if (isViewVisible) {
                            }
                            changedVisibility = false;
                            if (changedVisibility) {
                            }
                            this.mFirst = false;
                            this.mWillDrawSoon = false;
                            this.mNewSurfaceNeeded = false;
                            this.mViewVisibility = viewVisibility2;
                            if (this.mAttachInfo.mHasWindowFocus) {
                            }
                            this.mImeFocusController.onTraversal(hasWindowFocus, this.mWindowAttributes);
                            if ((width & 1) != 0) {
                            }
                            this.mCheckIfCanDraw = !isSyncRequest || cancelDraw3;
                            boolean cancelDueToPreDrawListener22222222 = this.mAttachInfo.mTreeObserver.dispatchOnPreDraw();
                            if (cancelDueToPreDrawListener22222222) {
                            }
                            if (!cancelAndRedraw) {
                            }
                            if (isViewVisible) {
                            }
                            if (this.mAttachInfo.mContentCaptureEvents != null) {
                            }
                            this.mIsInTraversal = false;
                            this.mRelayoutRequested = false;
                            if (cancelAndRedraw) {
                            }
                        }
                        windowShouldResize = z3;
                        boolean windowShouldResize22 = windowShouldResize | ((this.mDragResizing || !this.mPendingDragResizing) ? z3 : true);
                        computesInternalInsets = (!this.mAttachInfo.mTreeObserver.hasComputeInternalInsetsListeners() || this.mAttachInfo.mHasNonEmptyGivenInternalInsets) ? true : z3;
                        int surfaceGenerationId2 = this.mSurface.getGenerationId();
                        int desiredWindowHeight72 = viewVisibility;
                        if (desiredWindowHeight72 == 0) {
                        }
                        surfaceSizeChanged = false;
                        surfaceCreated = false;
                        boolean surfaceDestroyed2 = false;
                        surfaceReplaced = false;
                        boolean insetsPending32 = false;
                        insetsPending = this.mWindowAttributesChanged;
                        if (insetsPending) {
                        }
                        if (params2 != null) {
                        }
                        updatedConfiguration2 = this.mFirst;
                        if (updatedConfiguration2) {
                        }
                        frame2 = frame;
                        if (Trace.isTagEnabled(8L)) {
                        }
                        this.mForceNextWindowRelayout = false;
                        baseSurfaceHolder = this.mSurfaceHolder;
                        if (baseSurfaceHolder != null) {
                        }
                        boolean hwInitialized3 = false;
                        boolean hadSurface22 = this.mSurface.isValid();
                        dispatchApplyInsets = false;
                        if (this.mFirst) {
                        }
                        cancelDraw = false;
                        cancelReason = null;
                        this.mViewFrameInfo.flags |= 1;
                        int relayoutResult52 = relayoutWindow(params2, viewVisibility2, computesInternalInsets);
                        if ((relayoutResult52 & 16) == 16) {
                        }
                        cancelReason2 = "relayout";
                        boolean insetsPending42 = computesInternalInsets;
                        insetsPending2 = this.mPendingDragResizing;
                        i2 = this.mSyncSeqId;
                        if (i2 > this.mLastSyncSeqId) {
                        }
                        boolean surfaceControlChanged2 = (relayoutResult52 & 2) == 2;
                        if (this.mSurfaceControl.isValid()) {
                        }
                        if (this.mRelayoutRequested) {
                            performConfigurationChange(new MergedConfiguration(this.mPendingMergedConfiguration), this.mFirst, -1);
                            updatedConfiguration = true;
                        }
                        boolean updateSurfaceNeeded2 = this.mUpdateSurfaceNeeded;
                        this.mUpdateSurfaceNeeded = false;
                        surfaceSizeChanged = false;
                        if (this.mLastSurfaceSize.equals(this.mSurfaceSize)) {
                        }
                        alwaysConsumeSystemBarsChanged = this.mPendingAlwaysConsumeSystemBars != this.mAttachInfo.mAlwaysConsumeSystemBars;
                        updateColorModeIfNeeded(lp4.getColorMode());
                        surfaceCreated = hadSurface22 && this.mSurface.isValid();
                        surfaceDestroyed2 = (hadSurface22 || this.mSurface.isValid()) ? false : true;
                        surfaceReplaced = (surfaceGenerationId2 == this.mSurface.getGenerationId() || surfaceControlChanged2) && this.mSurface.isValid();
                        if (surfaceReplaced) {
                        }
                        if (alwaysConsumeSystemBarsChanged) {
                        }
                        if (updateCaptionInsets()) {
                        }
                        if (!dispatchApplyInsets) {
                        }
                        this.mLastSystemUiVisibility = this.mAttachInfo.mSystemUiVisibility;
                        dispatchApplyInsets(host);
                        dispatchApplyInsets = true;
                        if (surfaceCreated) {
                        }
                        if (this.mDragResizing != insetsPending2) {
                        }
                        if (!this.mUseMTRenderer) {
                        }
                        if (Trace.isTagEnabled(8L)) {
                        }
                        relayoutResult2 = relayoutResult4;
                        cancelDraw3 = cancelDraw2;
                        this.mAttachInfo.mWindowLeft = frame2.left;
                        this.mAttachInfo.mWindowTop = frame2.top;
                        if (this.mWidth == frame2.width()) {
                        }
                        this.mWidth = frame2.width();
                        this.mHeight = frame2.height();
                        if (this.mSurfaceHolder == null) {
                        }
                        threadedRenderer = this.mAttachInfo.mThreadedRenderer;
                        if (threadedRenderer != null) {
                            threadedRenderer.setup(this.mWidth, this.mHeight, this.mAttachInfo, this.mWindowAttributes.surfaceInsets);
                            this.mNeedsRendererSetup = false;
                        }
                        if (this.mStopped) {
                        }
                        int childWidthMeasureSpec2222222 = getRootMeasureSpec(this.mWidth, lp4.width, lp4.privateFlags);
                        int childHeightMeasureSpec2222222 = getRootMeasureSpec(this.mHeight, lp4.height, lp4.privateFlags);
                        performMeasure(childWidthMeasureSpec2222222, childHeightMeasureSpec2222222);
                        int width22222222 = host.getMeasuredWidth();
                        int height2222222 = host.getMeasuredHeight();
                        measureAgain = false;
                        if (lp4.horizontalWeight <= 0.0f) {
                        }
                        if (lp4.verticalWeight <= 0.0f) {
                        }
                        if (measureAgain) {
                        }
                        layoutRequested3 = true;
                        width = relayoutResult3;
                        insetsPending32 = insetsPending42;
                        if (this.mViewMeasureDeferred) {
                        }
                        if (!this.mRelayoutRequested) {
                        }
                        if (!surfaceSizeChanged) {
                        }
                        prepareSurfaces();
                        this.mChildBoundingInsetsChanged = false;
                        didLayout = !layoutRequested3 && (!this.mStopped || this.mReportNextDraw);
                        triggerGlobalLayoutListener = !didLayout || this.mAttachInfo.mRecomputeGlobalAttributes;
                        if (didLayout) {
                        }
                        if (surfaceCreated) {
                        }
                        if (didUseTransaction) {
                        }
                        if (triggerGlobalLayoutListener) {
                        }
                        Rect visibleInsets42222222 = null;
                        Region touchableRegion22222222 = null;
                        int touchableInsetMode22222222 = 3;
                        boolean computedInternalInsets22222222 = false;
                        if (computesInternalInsets) {
                        }
                        needsSetInsets = computedInternalInsets22222222;
                        if ((Objects.equals(this.mPreviousTouchableRegion, this.mTouchableRegion) && this.mTouchableRegion != null) | needsSetInsets) {
                        }
                        if (this.mFirst) {
                        }
                        if (isViewVisible) {
                        }
                        changedVisibility = false;
                        if (changedVisibility) {
                        }
                        this.mFirst = false;
                        this.mWillDrawSoon = false;
                        this.mNewSurfaceNeeded = false;
                        this.mViewVisibility = viewVisibility2;
                        if (this.mAttachInfo.mHasWindowFocus) {
                        }
                        this.mImeFocusController.onTraversal(hasWindowFocus, this.mWindowAttributes);
                        if ((width & 1) != 0) {
                        }
                        this.mCheckIfCanDraw = !isSyncRequest || cancelDraw3;
                        boolean cancelDueToPreDrawListener222222222 = this.mAttachInfo.mTreeObserver.dispatchOnPreDraw();
                        if (cancelDueToPreDrawListener222222222) {
                        }
                        if (!cancelAndRedraw) {
                        }
                        if (isViewVisible) {
                        }
                        if (this.mAttachInfo.mContentCaptureEvents != null) {
                        }
                        this.mIsInTraversal = false;
                        this.mRelayoutRequested = false;
                        if (cancelAndRedraw) {
                        }
                    }
                }
                if (this.mFirst) {
                }
                cancelDraw = false;
                cancelReason = null;
                this.mViewFrameInfo.flags |= 1;
                int relayoutResult522 = relayoutWindow(params2, viewVisibility2, computesInternalInsets);
                if ((relayoutResult522 & 16) == 16) {
                }
                cancelReason2 = "relayout";
                boolean insetsPending422 = computesInternalInsets;
                insetsPending2 = this.mPendingDragResizing;
                i2 = this.mSyncSeqId;
                if (i2 > this.mLastSyncSeqId) {
                }
                boolean surfaceControlChanged22 = (relayoutResult522 & 2) == 2;
                if (this.mSurfaceControl.isValid()) {
                }
                if (this.mRelayoutRequested) {
                }
                boolean updateSurfaceNeeded22 = this.mUpdateSurfaceNeeded;
                this.mUpdateSurfaceNeeded = false;
                surfaceSizeChanged = false;
                if (this.mLastSurfaceSize.equals(this.mSurfaceSize)) {
                }
                alwaysConsumeSystemBarsChanged = this.mPendingAlwaysConsumeSystemBars != this.mAttachInfo.mAlwaysConsumeSystemBars;
                updateColorModeIfNeeded(lp4.getColorMode());
                surfaceCreated = hadSurface22 && this.mSurface.isValid();
                surfaceDestroyed2 = (hadSurface22 || this.mSurface.isValid()) ? false : true;
                surfaceReplaced = (surfaceGenerationId2 == this.mSurface.getGenerationId() || surfaceControlChanged22) && this.mSurface.isValid();
                if (surfaceReplaced) {
                }
                if (alwaysConsumeSystemBarsChanged) {
                }
                if (updateCaptionInsets()) {
                }
                if (!dispatchApplyInsets) {
                }
                this.mLastSystemUiVisibility = this.mAttachInfo.mSystemUiVisibility;
                dispatchApplyInsets(host);
                dispatchApplyInsets = true;
                if (surfaceCreated) {
                }
                if (this.mDragResizing != insetsPending2) {
                }
                if (!this.mUseMTRenderer) {
                }
                if (Trace.isTagEnabled(8L)) {
                }
                relayoutResult2 = relayoutResult4;
                cancelDraw3 = cancelDraw2;
                this.mAttachInfo.mWindowLeft = frame2.left;
                this.mAttachInfo.mWindowTop = frame2.top;
                if (this.mWidth == frame2.width()) {
                }
                this.mWidth = frame2.width();
                this.mHeight = frame2.height();
                if (this.mSurfaceHolder == null) {
                }
                threadedRenderer = this.mAttachInfo.mThreadedRenderer;
                if (threadedRenderer != null) {
                }
                if (this.mStopped) {
                }
                int childWidthMeasureSpec22222222 = getRootMeasureSpec(this.mWidth, lp4.width, lp4.privateFlags);
                int childHeightMeasureSpec22222222 = getRootMeasureSpec(this.mHeight, lp4.height, lp4.privateFlags);
                performMeasure(childWidthMeasureSpec22222222, childHeightMeasureSpec22222222);
                int width222222222 = host.getMeasuredWidth();
                int height22222222 = host.getMeasuredHeight();
                measureAgain = false;
                if (lp4.horizontalWeight <= 0.0f) {
                }
                if (lp4.verticalWeight <= 0.0f) {
                }
                if (measureAgain) {
                }
                layoutRequested3 = true;
                width = relayoutResult3;
                insetsPending32 = insetsPending422;
                if (this.mViewMeasureDeferred) {
                }
                if (!this.mRelayoutRequested) {
                }
                if (!surfaceSizeChanged) {
                }
                prepareSurfaces();
                this.mChildBoundingInsetsChanged = false;
                didLayout = !layoutRequested3 && (!this.mStopped || this.mReportNextDraw);
                triggerGlobalLayoutListener = !didLayout || this.mAttachInfo.mRecomputeGlobalAttributes;
                if (didLayout) {
                }
                if (surfaceCreated) {
                }
                if (didUseTransaction) {
                }
                if (triggerGlobalLayoutListener) {
                }
                Rect visibleInsets422222222 = null;
                Region touchableRegion222222222 = null;
                int touchableInsetMode222222222 = 3;
                boolean computedInternalInsets222222222 = false;
                if (computesInternalInsets) {
                }
                needsSetInsets = computedInternalInsets222222222;
                if ((Objects.equals(this.mPreviousTouchableRegion, this.mTouchableRegion) && this.mTouchableRegion != null) | needsSetInsets) {
                }
                if (this.mFirst) {
                }
                if (isViewVisible) {
                }
                changedVisibility = false;
                if (changedVisibility) {
                }
                this.mFirst = false;
                this.mWillDrawSoon = false;
                this.mNewSurfaceNeeded = false;
                this.mViewVisibility = viewVisibility2;
                if (this.mAttachInfo.mHasWindowFocus) {
                }
                this.mImeFocusController.onTraversal(hasWindowFocus, this.mWindowAttributes);
                if ((width & 1) != 0) {
                }
                this.mCheckIfCanDraw = !isSyncRequest || cancelDraw3;
                boolean cancelDueToPreDrawListener2222222222 = this.mAttachInfo.mTreeObserver.dispatchOnPreDraw();
                if (cancelDueToPreDrawListener2222222222) {
                }
                if (!cancelAndRedraw) {
                }
                if (isViewVisible) {
                }
                if (this.mAttachInfo.mContentCaptureEvents != null) {
                }
                this.mIsInTraversal = false;
                this.mRelayoutRequested = false;
                if (cancelAndRedraw) {
                }
            } catch (Throwable th7) {
                th = th7;
            }
            z = false;
            boolean viewUserVisibilityChanged2 = z;
            boolean shouldOptimizeMeasure2 = shouldOptimizeMeasure(lp5);
            CompatibilityInfo compatibilityInfo2 = this.mDisplay.getDisplayAdjustments().getCompatibilityInfo();
            supportsScreen = compatibilityInfo2.supportsScreen();
            z2 = this.mLastInCompatMode;
            if (supportsScreen != z2) {
            }
            Rect frame32 = this.mWinFrame;
            if (this.mFirst) {
            }
            if (viewVisibilityChanged) {
            }
            if (this.mAttachInfo.mWindowVisibility != 0) {
            }
            getRunQueue().executeActions(this.mAttachInfo.mHandler);
            if (this.mFirst) {
            }
            layoutRequested = !this.mLayoutRequested && (!this.mStopped || this.mReportNextDraw);
            if (layoutRequested) {
            }
            if (collectViewAttributes()) {
            }
            if (this.mAttachInfo.mForceReportNewAttributes) {
            }
            if (this.mFirst) {
            }
            this.mAttachInfo.mViewVisibilityChanged = z3;
            resizeMode = this.mSoftInputMode & 240;
            if (resizeMode != 0) {
            }
            if (this.mApplyInsetsRequested) {
            }
            if (layoutRequested) {
            }
            if (layoutRequested) {
            }
            lp4 = lp3;
            desiredWindowHeight5 = desiredWindowHeight4;
            windowShouldResize = z3;
            boolean windowShouldResize222 = windowShouldResize | ((this.mDragResizing || !this.mPendingDragResizing) ? z3 : true);
            computesInternalInsets = (!this.mAttachInfo.mTreeObserver.hasComputeInternalInsetsListeners() || this.mAttachInfo.mHasNonEmptyGivenInternalInsets) ? true : z3;
            int surfaceGenerationId22 = this.mSurface.getGenerationId();
            int desiredWindowHeight722 = viewVisibility;
            if (desiredWindowHeight722 == 0) {
            }
            surfaceSizeChanged = false;
            surfaceCreated = false;
            boolean surfaceDestroyed22 = false;
            surfaceReplaced = false;
            boolean insetsPending322 = false;
            insetsPending = this.mWindowAttributesChanged;
            if (insetsPending) {
            }
            if (params2 != null) {
            }
            updatedConfiguration2 = this.mFirst;
            if (updatedConfiguration2) {
            }
            frame2 = frame;
            if (Trace.isTagEnabled(8L)) {
            }
            this.mForceNextWindowRelayout = false;
            baseSurfaceHolder = this.mSurfaceHolder;
            if (baseSurfaceHolder != null) {
            }
            boolean hwInitialized32 = false;
            boolean hadSurface222 = this.mSurface.isValid();
            dispatchApplyInsets = false;
        }
    }

    private void createSyncIfNeeded() {
        if (isInWMSRequestedSync() || !this.mReportNextDraw) {
            return;
        }
        final int seqId = this.mSyncSeqId;
        this.mWmsRequestSyncGroupState = 1;
        this.mWmsRequestSyncGroup = new SurfaceSyncGroup("wmsSync-" + this.mTag, new Consumer() { // from class: android.view.ViewRootImpl$$ExternalSyntheticLambda13
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                ViewRootImpl.this.lambda$createSyncIfNeeded$3(seqId, (SurfaceControl.Transaction) obj);
            }
        });
        Trace.traceBegin(8L, "create WMS Sync group=" + this.mWmsRequestSyncGroup.getName());
        this.mWmsRequestSyncGroup.add(this, (Runnable) null);
        Trace.traceEnd(8L);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createSyncIfNeeded$3(int seqId, SurfaceControl.Transaction t) {
        this.mWmsRequestSyncGroupState = 3;
        reportDrawFinished(t, seqId);
    }

    private void notifyContentCaptureEvents() {
        Trace.traceBegin(8L, "notifyContentCaptureEvents");
        try {
            MainContentCaptureSession mainSession = this.mAttachInfo.mContentCaptureManager.getMainContentCaptureSession();
            for (int i = 0; i < this.mAttachInfo.mContentCaptureEvents.size(); i++) {
                int sessionId = this.mAttachInfo.mContentCaptureEvents.keyAt(i);
                mainSession.notifyViewTreeEvent(sessionId, true);
                ArrayList<Object> events = this.mAttachInfo.mContentCaptureEvents.valueAt(i);
                for (int j = 0; j < events.size(); j++) {
                    Object event = events.get(j);
                    if (event instanceof AutofillId) {
                        mainSession.notifyViewDisappeared(sessionId, (AutofillId) event);
                    } else if (event instanceof View) {
                        View view = (View) event;
                        ContentCaptureSession session = view.getContentCaptureSession();
                        if (session == null) {
                            Log.m104w(this.mTag, "no content capture session on view: " + view);
                        } else {
                            int actualId = session.getId();
                            if (actualId != sessionId) {
                                Log.m104w(this.mTag, "content capture session mismatch for view (" + view + "): was " + sessionId + " before, it's " + actualId + " now");
                            } else {
                                ViewStructure structure = session.newViewStructure(view);
                                view.onProvideContentCaptureStructure(structure, 0);
                                session.notifyViewAppeared(structure);
                            }
                        }
                    } else if (event instanceof Insets) {
                        mainSession.notifyViewInsetsChanged(sessionId, (Insets) event);
                    } else {
                        Log.m104w(this.mTag, "invalid content capture event: " + event);
                    }
                }
                mainSession.notifyViewTreeEvent(sessionId, false);
            }
            this.mAttachInfo.mContentCaptureEvents = null;
        } finally {
            Trace.traceEnd(8L);
        }
    }

    private void notifyHolderSurfaceDestroyed() {
        this.mSurfaceHolder.ungetCallbacks();
        SurfaceHolder.Callback[] callbacks = this.mSurfaceHolder.getCallbacks();
        if (callbacks != null) {
            for (SurfaceHolder.Callback c : callbacks) {
                c.surfaceDestroyed(this.mSurfaceHolder);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void maybeHandleWindowMove(Rect frame) {
        boolean windowMoved = (this.mAttachInfo.mWindowLeft == frame.left && this.mAttachInfo.mWindowTop == frame.top) ? false : true;
        if (windowMoved) {
            this.mAttachInfo.mWindowLeft = frame.left;
            this.mAttachInfo.mWindowTop = frame.top;
        }
        if (windowMoved || this.mAttachInfo.mNeedsUpdateLightCenter) {
            if (this.mAttachInfo.mThreadedRenderer != null) {
                this.mAttachInfo.mThreadedRenderer.setLightCenter(this.mAttachInfo);
            }
            this.mAttachInfo.mNeedsUpdateLightCenter = false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleWindowFocusChanged() {
        synchronized (this) {
            if (this.mWindowFocusChanged) {
                this.mWindowFocusChanged = false;
                boolean hasWindowFocus = this.mUpcomingWindowFocus;
                if (hasWindowFocus) {
                    this.mInsetsController.onWindowFocusGained(getFocusedViewOrNull() != null);
                } else {
                    this.mInsetsController.onWindowFocusLost();
                }
                if (this.mAdded) {
                    dispatchFocusEvent(hasWindowFocus, false);
                    this.mImeFocusController.onPostWindowFocus(getFocusedViewOrNull(), hasWindowFocus, this.mWindowAttributes);
                    if (hasWindowFocus) {
                        this.mWindowAttributes.softInputMode &= -257;
                        ((WindowManager.LayoutParams) this.mView.getLayoutParams()).softInputMode &= -257;
                        maybeFireAccessibilityWindowStateChangedEvent();
                        fireAccessibilityFocusEventIfHasFocusedNode();
                    } else if (this.mPointerCapture) {
                        handlePointerCaptureChanged(false);
                    }
                }
                this.mFirstInputStage.onWindowFocusChanged(hasWindowFocus);
                if (hasWindowFocus) {
                    handleContentCaptureFlush();
                }
            }
        }
    }

    public void dispatchCompatFakeFocus() {
        boolean aboutToHaveFocus;
        synchronized (this) {
            aboutToHaveFocus = this.mWindowFocusChanged && this.mUpcomingWindowFocus;
        }
        boolean alreadyHaveFocus = this.mAttachInfo.mHasWindowFocus;
        if (aboutToHaveFocus || alreadyHaveFocus) {
            return;
        }
        EventLog.writeEvent((int) LOGTAG_INPUT_FOCUS, "Giving fake focus to " + this.mBasePackageName, "reason=unity bug workaround");
        dispatchFocusEvent(true, true);
        EventLog.writeEvent((int) LOGTAG_INPUT_FOCUS, "Removing fake focus from " + this.mBasePackageName, "reason=timeout callback");
        dispatchFocusEvent(false, true);
    }

    private void dispatchFocusEvent(boolean hasWindowFocus, boolean fakeFocus) {
        profileRendering(hasWindowFocus);
        if (hasWindowFocus && this.mAttachInfo.mThreadedRenderer != null && this.mSurface.isValid()) {
            this.mFullRedrawNeeded = true;
            try {
                Rect surfaceInsets = this.mWindowAttributes.surfaceInsets;
                this.mAttachInfo.mThreadedRenderer.initializeIfNeeded(this.mWidth, this.mHeight, this.mAttachInfo, this.mSurface, surfaceInsets);
            } catch (Surface.OutOfResourcesException e) {
                Log.m109e(this.mTag, "OutOfResourcesException locking surface", e);
                try {
                    if (!this.mWindowSession.outOfMemory(this.mWindow)) {
                        Slog.m90w(this.mTag, "No processes killed for memory; killing self");
                        Process.killProcess(Process.myPid());
                    }
                } catch (RemoteException e2) {
                }
                ViewRootHandler viewRootHandler = this.mHandler;
                viewRootHandler.sendMessageDelayed(viewRootHandler.obtainMessage(6), 500L);
                return;
            }
        }
        this.mAttachInfo.mHasWindowFocus = hasWindowFocus;
        if (!fakeFocus) {
            this.mImeFocusController.onPreWindowFocus(hasWindowFocus, this.mWindowAttributes);
        }
        if (this.mView != null) {
            this.mAttachInfo.mKeyDispatchState.reset();
            this.mView.dispatchWindowFocusChanged(hasWindowFocus);
            this.mAttachInfo.mTreeObserver.dispatchOnWindowFocusChange(hasWindowFocus);
            if (this.mAttachInfo.mTooltipHost != null) {
                this.mAttachInfo.mTooltipHost.hideTooltip();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleWindowTouchModeChanged() {
        boolean inTouchMode;
        synchronized (this) {
            inTouchMode = this.mUpcomingInTouchMode;
        }
        ensureTouchModeLocally(inTouchMode);
    }

    private void maybeFireAccessibilityWindowStateChangedEvent() {
        View view;
        WindowManager.LayoutParams layoutParams = this.mWindowAttributes;
        boolean isToast = layoutParams != null && layoutParams.type == 2005;
        if (!isToast && (view = this.mView) != null) {
            view.sendAccessibilityEvent(32);
        }
    }

    private void fireAccessibilityFocusEventIfHasFocusedNode() {
        View focusedView;
        if (!AccessibilityManager.getInstance(this.mContext).isEnabled() || (focusedView = this.mView.findFocus()) == null) {
            return;
        }
        AccessibilityNodeProvider provider = focusedView.getAccessibilityNodeProvider();
        if (provider == null) {
            focusedView.sendAccessibilityEvent(8);
            return;
        }
        AccessibilityNodeInfo focusedNode = findFocusedVirtualNode(provider);
        if (focusedNode != null) {
            int virtualId = AccessibilityNodeInfo.getVirtualDescendantId(focusedNode.getSourceNodeId());
            AccessibilityEvent event = AccessibilityEvent.obtain(8);
            event.setSource(focusedView, virtualId);
            event.setPackageName(focusedNode.getPackageName());
            event.setChecked(focusedNode.isChecked());
            event.setContentDescription(focusedNode.getContentDescription());
            event.setPassword(focusedNode.isPassword());
            event.getText().add(focusedNode.getText());
            event.setEnabled(focusedNode.isEnabled());
            focusedView.getParent().requestSendAccessibilityEvent(focusedView, event);
            focusedNode.recycle();
        }
    }

    private AccessibilityNodeInfo findFocusedVirtualNode(AccessibilityNodeProvider provider) {
        AccessibilityNodeInfo focusedNode = provider.findFocus(1);
        if (focusedNode != null) {
            return focusedNode;
        }
        if (this.mContext.isAutofillCompatibilityEnabled()) {
            AccessibilityNodeInfo current = provider.createAccessibilityNodeInfo(-1);
            if (current.isFocused()) {
                return current;
            }
            Queue<AccessibilityNodeInfo> fringe = new ArrayDeque<>();
            fringe.offer(current);
            while (!fringe.isEmpty()) {
                AccessibilityNodeInfo current2 = fringe.poll();
                LongArray childNodeIds = current2.getChildNodeIds();
                if (childNodeIds != null && childNodeIds.size() > 0) {
                    int childCount = childNodeIds.size();
                    for (int i = 0; i < childCount; i++) {
                        int virtualId = AccessibilityNodeInfo.getVirtualDescendantId(childNodeIds.get(i));
                        AccessibilityNodeInfo child = provider.createAccessibilityNodeInfo(virtualId);
                        if (child != null) {
                            if (child.isFocused()) {
                                return child;
                            }
                            fringe.offer(child);
                        }
                    }
                    current2.recycle();
                }
            }
            return null;
        }
        return null;
    }

    private void handleOutOfResourcesException(Surface.OutOfResourcesException e) {
        Log.m109e(this.mTag, "OutOfResourcesException initializing HW surface", e);
        try {
            if (!this.mWindowSession.outOfMemory(this.mWindow) && Process.myUid() != 1000) {
                Slog.m90w(this.mTag, "No processes killed for memory; killing self");
                Process.killProcess(Process.myPid());
            }
        } catch (RemoteException e2) {
        }
        this.mLayoutRequested = true;
    }

    private void performMeasure(int childWidthMeasureSpec, int childHeightMeasureSpec) {
        if (this.mView == null) {
            return;
        }
        Trace.traceBegin(8L, "measure");
        try {
            this.mView.measure(childWidthMeasureSpec, childHeightMeasureSpec);
            Trace.traceEnd(8L);
            this.mMeasuredWidth = this.mView.getMeasuredWidth();
            this.mMeasuredHeight = this.mView.getMeasuredHeight();
            this.mViewMeasureDeferred = false;
        } catch (Throwable th) {
            Trace.traceEnd(8L);
            throw th;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isInLayout() {
        return this.mInLayout;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean requestLayoutDuringLayout(View view) {
        if (view.mParent == null || view.mAttachInfo == null) {
            return true;
        }
        if (!this.mLayoutRequesters.contains(view)) {
            this.mLayoutRequesters.add(view);
        }
        return !this.mHandlingLayoutInLayoutRequest;
    }

    private void performLayout(WindowManager.LayoutParams lp, int desiredWindowWidth, int desiredWindowHeight) {
        ArrayList<View> validLayoutRequesters;
        this.mScrollMayChange = true;
        this.mInLayout = true;
        View host = this.mView;
        if (host == null) {
            return;
        }
        Trace.traceBegin(8L, TtmlUtils.TAG_LAYOUT);
        try {
            host.layout(0, 0, host.getMeasuredWidth(), host.getMeasuredHeight());
            this.mInLayout = false;
            int numViewsRequestingLayout = this.mLayoutRequesters.size();
            if (numViewsRequestingLayout > 0 && (validLayoutRequesters = getValidLayoutRequesters(this.mLayoutRequesters, false)) != null) {
                this.mHandlingLayoutInLayoutRequest = true;
                int numValidRequests = validLayoutRequesters.size();
                for (int i = 0; i < numValidRequests; i++) {
                    View view = validLayoutRequesters.get(i);
                    Log.m104w("View", "requestLayout() improperly called by " + view + " during layout: running second layout pass");
                    view.requestLayout();
                }
                measureHierarchy(host, lp, this.mView.getContext().getResources(), desiredWindowWidth, desiredWindowHeight, false);
                this.mInLayout = true;
                host.layout(0, 0, host.getMeasuredWidth(), host.getMeasuredHeight());
                this.mHandlingLayoutInLayoutRequest = false;
                final ArrayList<View> validLayoutRequesters2 = getValidLayoutRequesters(this.mLayoutRequesters, true);
                if (validLayoutRequesters2 != null) {
                    getRunQueue().post(new Runnable() { // from class: android.view.ViewRootImpl.4
                        @Override // java.lang.Runnable
                        public void run() {
                            int numValidRequests2 = validLayoutRequesters2.size();
                            for (int i2 = 0; i2 < numValidRequests2; i2++) {
                                View view2 = (View) validLayoutRequesters2.get(i2);
                                Log.m104w("View", "requestLayout() improperly called by " + view2 + " during second layout pass: posting in next frame");
                                view2.requestLayout();
                            }
                        }
                    });
                }
            }
            Trace.traceEnd(8L);
            this.mInLayout = false;
        } catch (Throwable th) {
            Trace.traceEnd(8L);
            throw th;
        }
    }

    private ArrayList<View> getValidLayoutRequesters(ArrayList<View> layoutRequesters, boolean secondLayoutRequests) {
        int numViewsRequestingLayout = layoutRequesters.size();
        ArrayList<View> validLayoutRequesters = null;
        for (int i = 0; i < numViewsRequestingLayout; i++) {
            View view = layoutRequesters.get(i);
            if (view != null && view.mAttachInfo != null && view.mParent != null && (secondLayoutRequests || (view.mPrivateFlags & 4096) == 4096)) {
                boolean gone = false;
                View parent = view;
                while (true) {
                    if (parent == null) {
                        break;
                    } else if ((parent.mViewFlags & 12) == 8) {
                        gone = true;
                        break;
                    } else if (parent.mParent instanceof View) {
                        parent = (View) parent.mParent;
                    } else {
                        parent = null;
                    }
                }
                if (!gone) {
                    if (validLayoutRequesters == null) {
                        validLayoutRequesters = new ArrayList<>();
                    }
                    validLayoutRequesters.add(view);
                }
            }
        }
        if (!secondLayoutRequests) {
            for (int i2 = 0; i2 < numViewsRequestingLayout; i2++) {
                View view2 = layoutRequesters.get(i2);
                while (view2 != null && (view2.mPrivateFlags & 4096) != 0) {
                    view2.mPrivateFlags &= -4097;
                    if (view2.mParent instanceof View) {
                        view2 = (View) view2.mParent;
                    } else {
                        view2 = null;
                    }
                }
            }
        }
        layoutRequesters.clear();
        return validLayoutRequesters;
    }

    @Override // android.view.ViewParent
    public void requestTransparentRegion(View child) {
        checkThread();
        View view = this.mView;
        if (view != child) {
            return;
        }
        if ((view.mPrivateFlags & 512) == 0) {
            this.mView.mPrivateFlags |= 512;
            this.mWindowAttributesChanged = true;
        }
        requestLayout();
    }

    private static int getRootMeasureSpec(int windowSize, int measurement, int privateFlags) {
        int rootDimension = (privateFlags & 8192) != 0 ? -1 : measurement;
        switch (rootDimension) {
            case -2:
                int measureSpec = View.MeasureSpec.makeMeasureSpec(windowSize, Integer.MIN_VALUE);
                return measureSpec;
            case -1:
                int measureSpec2 = View.MeasureSpec.makeMeasureSpec(windowSize, 1073741824);
                return measureSpec2;
            default:
                int measureSpec3 = View.MeasureSpec.makeMeasureSpec(rootDimension, 1073741824);
                return measureSpec3;
        }
    }

    @Override // android.view.ThreadedRenderer.DrawCallbacks
    public void onPreDraw(RecordingCanvas canvas) {
        if (this.mCurScrollY != 0 && this.mHardwareYOffset != 0 && this.mAttachInfo.mThreadedRenderer.isOpaque()) {
            canvas.drawColor(-16777216);
        }
        canvas.translate(-this.mHardwareXOffset, -this.mHardwareYOffset);
    }

    @Override // android.view.ThreadedRenderer.DrawCallbacks
    public void onPostDraw(RecordingCanvas canvas) {
        drawAccessibilityFocusedDrawableIfNeeded(canvas);
        if (this.mUseMTRenderer) {
            for (int i = this.mWindowCallbacks.size() - 1; i >= 0; i--) {
                this.mWindowCallbacks.get(i).onPostDraw(canvas);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void outputDisplayList(View view) {
        view.mRenderNode.output();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void profileRendering(boolean enabled) {
        if (this.mProfileRendering) {
            this.mRenderProfilingEnabled = enabled;
            Choreographer.FrameCallback frameCallback = this.mRenderProfiler;
            if (frameCallback != null) {
                this.mChoreographer.removeFrameCallback(frameCallback);
            }
            if (this.mRenderProfilingEnabled) {
                if (this.mRenderProfiler == null) {
                    this.mRenderProfiler = new Choreographer.FrameCallback() { // from class: android.view.ViewRootImpl.5
                        @Override // android.view.Choreographer.FrameCallback
                        public void doFrame(long frameTimeNanos) {
                            ViewRootImpl.this.mDirty.set(0, 0, ViewRootImpl.this.mWidth, ViewRootImpl.this.mHeight);
                            ViewRootImpl.this.scheduleTraversals();
                            if (ViewRootImpl.this.mRenderProfilingEnabled) {
                                ViewRootImpl.this.mChoreographer.postFrameCallback(ViewRootImpl.this.mRenderProfiler);
                            }
                        }
                    };
                }
                this.mChoreographer.postFrameCallback(this.mRenderProfiler);
                return;
            }
            this.mRenderProfiler = null;
        }
    }

    private void trackFPS() {
        long nowTime = System.currentTimeMillis();
        if (this.mFpsStartTime < 0) {
            this.mFpsPrevTime = nowTime;
            this.mFpsStartTime = nowTime;
            this.mFpsNumFrames = 0;
            return;
        }
        this.mFpsNumFrames++;
        String thisHash = Integer.toHexString(System.identityHashCode(this));
        long frameTime = nowTime - this.mFpsPrevTime;
        long totalTime = nowTime - this.mFpsStartTime;
        Log.m106v(this.mTag, "0x" + thisHash + "\tFrame time:\t" + frameTime);
        this.mFpsPrevTime = nowTime;
        if (totalTime > 1000) {
            float fps = (this.mFpsNumFrames * 1000.0f) / ((float) totalTime);
            Log.m106v(this.mTag, "0x" + thisHash + "\tFPS:\t" + fps);
            this.mFpsStartTime = nowTime;
            this.mFpsNumFrames = 0;
        }
    }

    private void reportDrawFinished(SurfaceControl.Transaction t, int seqId) {
        try {
            try {
                this.mWindowSession.finishDrawing(this.mWindow, t, seqId);
                if (t == null) {
                    return;
                }
            } catch (RemoteException e) {
                Log.m109e(this.mTag, "Unable to report draw finished", e);
                if (t != null) {
                    t.apply();
                }
                if (t == null) {
                    return;
                }
            }
            t.clear();
        } catch (Throwable th) {
            if (t != null) {
                t.clear();
            }
            throw th;
        }
    }

    public boolean isHardwareEnabled() {
        return this.mAttachInfo.mThreadedRenderer != null && this.mAttachInfo.mThreadedRenderer.isEnabled();
    }

    public boolean isInWMSRequestedSync() {
        return this.mWmsRequestSyncGroup != null;
    }

    private void addFrameCommitCallbackIfNeeded() {
        if (!isHardwareEnabled()) {
            return;
        }
        final ArrayList<Runnable> commitCallbacks = this.mAttachInfo.mTreeObserver.captureFrameCommitCallbacks();
        boolean needFrameCommitCallback = commitCallbacks != null && commitCallbacks.size() > 0;
        if (!needFrameCommitCallback) {
            return;
        }
        this.mAttachInfo.mThreadedRenderer.setFrameCommitCallback(new HardwareRenderer.FrameCommitCallback() { // from class: android.view.ViewRootImpl$$ExternalSyntheticLambda11
            @Override // android.graphics.HardwareRenderer.FrameCommitCallback
            public final void onFrameCommit(boolean z) {
                ViewRootImpl.this.lambda$addFrameCommitCallbackIfNeeded$5(commitCallbacks, z);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$addFrameCommitCallbackIfNeeded$5(final ArrayList commitCallbacks, boolean didProduceBuffer) {
        this.mHandler.postAtFrontOfQueue(new Runnable() { // from class: android.view.ViewRootImpl$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                ViewRootImpl.lambda$addFrameCommitCallbackIfNeeded$4(commitCallbacks);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ void lambda$addFrameCommitCallbackIfNeeded$4(ArrayList commitCallbacks) {
        for (int i = 0; i < commitCallbacks.size(); i++) {
            ((Runnable) commitCallbacks.get(i)).run();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: android.view.ViewRootImpl$6 */
    /* loaded from: classes4.dex */
    public class C35686 implements HardwareRenderer.FrameDrawingCallback {
        C35686() {
        }

        @Override // android.graphics.HardwareRenderer.FrameDrawingCallback
        public HardwareRenderer.FrameCommitCallback onFrameDraw(int syncResult, final long frame) {
            if ((syncResult & 6) != 0) {
                ViewRootImpl.this.mBlastBufferQueue.applyPendingTransactions(frame);
                return null;
            }
            return new HardwareRenderer.FrameCommitCallback() { // from class: android.view.ViewRootImpl$6$$ExternalSyntheticLambda0
                @Override // android.graphics.HardwareRenderer.FrameCommitCallback
                public final void onFrameCommit(boolean z) {
                    ViewRootImpl.C35686.this.lambda$onFrameDraw$0(frame, z);
                }
            };
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onFrameDraw$0(long frame, boolean didProduceBuffer) {
            if (!didProduceBuffer) {
                ViewRootImpl.this.mBlastBufferQueue.applyPendingTransactions(frame);
            }
        }

        @Override // android.graphics.HardwareRenderer.FrameDrawingCallback
        public void onFrameDraw(long frame) {
        }
    }

    private void registerCallbackForPendingTransactions() {
        registerRtFrameCallback(new C35686());
    }

    /* JADX WARN: Removed duplicated region for block: B:45:0x0081  */
    /* JADX WARN: Removed duplicated region for block: B:51:0x00a7  */
    /* JADX WARN: Removed duplicated region for block: B:77:0x010c  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    private boolean performDraw() {
        boolean z;
        SurfaceSyncGroup surfaceSyncGroup;
        this.mLastPerformDrawSkippedReason = null;
        if (this.mAttachInfo.mDisplayState == 1 && !this.mReportNextDraw) {
            this.mLastPerformDrawSkippedReason = "screen_off";
            return false;
        } else if (this.mView == null) {
            this.mLastPerformDrawSkippedReason = "no_root_view";
            return false;
        } else {
            boolean fullRedrawNeeded = this.mFullRedrawNeeded || this.mActiveSurfaceSyncGroup != null;
            this.mFullRedrawNeeded = false;
            this.mIsDrawing = true;
            Trace.traceBegin(8L, "draw");
            addFrameCommitCallbackIfNeeded();
            boolean usingAsyncReport = isHardwareEnabled() && this.mActiveSurfaceSyncGroup != null;
            if (usingAsyncReport) {
                registerCallbacksForSync(this.mSyncBuffer, this.mActiveSurfaceSyncGroup);
            } else if (this.mHasPendingTransactions) {
                registerCallbackForPendingTransactions();
            }
            this.mHasPendingTransactions = false;
            if (usingAsyncReport) {
                try {
                    if (this.mSyncBuffer) {
                        z = true;
                        boolean canUseAsync = draw(fullRedrawNeeded, z);
                        if (usingAsyncReport && !canUseAsync) {
                            this.mAttachInfo.mThreadedRenderer.setFrameCallback(null);
                            usingAsyncReport = false;
                        }
                        this.mIsDrawing = false;
                        Trace.traceEnd(8L);
                        if (this.mAttachInfo.mPendingAnimatingRenderNodes != null) {
                            int count = this.mAttachInfo.mPendingAnimatingRenderNodes.size();
                            for (int i = 0; i < count; i++) {
                                this.mAttachInfo.mPendingAnimatingRenderNodes.get(i).endAllAnimators();
                            }
                            this.mAttachInfo.mPendingAnimatingRenderNodes.clear();
                        }
                        if (this.mReportNextDraw) {
                            CountDownLatch countDownLatch = this.mWindowDrawCountDown;
                            if (countDownLatch != null) {
                                try {
                                    countDownLatch.await();
                                } catch (InterruptedException e) {
                                    Log.m110e(this.mTag, "Window redraw count down interrupted!");
                                }
                                this.mWindowDrawCountDown = null;
                            }
                            if (this.mAttachInfo.mThreadedRenderer != null) {
                                this.mAttachInfo.mThreadedRenderer.setStopped(this.mStopped);
                            }
                            if (this.mSurfaceHolder != null && this.mSurface.isValid()) {
                                final SurfaceSyncGroup surfaceSyncGroup2 = this.mActiveSurfaceSyncGroup;
                                SurfaceCallbackHelper sch = new SurfaceCallbackHelper(new Runnable() { // from class: android.view.ViewRootImpl$$ExternalSyntheticLambda15
                                    @Override // java.lang.Runnable
                                    public final void run() {
                                        ViewRootImpl.this.lambda$performDraw$7(surfaceSyncGroup2);
                                    }
                                });
                                this.mActiveSurfaceSyncGroup = null;
                                SurfaceHolder.Callback[] callbacks = this.mSurfaceHolder.getCallbacks();
                                sch.dispatchSurfaceRedrawNeededAsync(this.mSurfaceHolder, callbacks);
                            } else if (!usingAsyncReport && this.mAttachInfo.mThreadedRenderer != null) {
                                this.mAttachInfo.mThreadedRenderer.fence();
                            }
                        }
                        surfaceSyncGroup = this.mActiveSurfaceSyncGroup;
                        if (surfaceSyncGroup != null && !usingAsyncReport) {
                            surfaceSyncGroup.markSyncReady();
                        }
                        if (this.mPerformContentCapture) {
                            performContentCaptureInitialReport();
                        }
                        return true;
                    }
                } catch (Throwable th) {
                    this.mIsDrawing = false;
                    Trace.traceEnd(8L);
                    throw th;
                }
            }
            z = false;
            boolean canUseAsync2 = draw(fullRedrawNeeded, z);
            if (usingAsyncReport) {
                this.mAttachInfo.mThreadedRenderer.setFrameCallback(null);
                usingAsyncReport = false;
            }
            this.mIsDrawing = false;
            Trace.traceEnd(8L);
            if (this.mAttachInfo.mPendingAnimatingRenderNodes != null) {
            }
            if (this.mReportNextDraw) {
            }
            surfaceSyncGroup = this.mActiveSurfaceSyncGroup;
            if (surfaceSyncGroup != null) {
                surfaceSyncGroup.markSyncReady();
            }
            if (this.mPerformContentCapture) {
            }
            return true;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$performDraw$7(final SurfaceSyncGroup surfaceSyncGroup) {
        this.mHandler.post(new Runnable() { // from class: android.view.ViewRootImpl$$ExternalSyntheticLambda12
            @Override // java.lang.Runnable
            public final void run() {
                SurfaceSyncGroup.this.markSyncReady();
            }
        });
    }

    private boolean isContentCaptureEnabled() {
        switch (this.mContentCaptureEnabled) {
            case 0:
                boolean reallyEnabled = isContentCaptureReallyEnabled();
                this.mContentCaptureEnabled = reallyEnabled ? 1 : 2;
                return reallyEnabled;
            case 1:
                return true;
            case 2:
                return false;
            default:
                Log.m104w(TAG, "isContentCaptureEnabled(): invalid state " + this.mContentCaptureEnabled);
                return false;
        }
    }

    private boolean isContentCaptureReallyEnabled() {
        ContentCaptureManager ccm;
        return (this.mContext.getContentCaptureOptions() == null || (ccm = this.mAttachInfo.getContentCaptureManager(this.mContext)) == null || !ccm.isContentCaptureEnabled()) ? false : true;
    }

    private void performContentCaptureInitialReport() {
        this.mPerformContentCapture = false;
        View rootView = this.mView;
        if (Trace.isTagEnabled(8L)) {
            Trace.traceBegin(8L, "dispatchContentCapture() for " + getClass().getSimpleName());
        }
        try {
            if (!isContentCaptureEnabled()) {
                return;
            }
            if (this.mAttachInfo.mContentCaptureManager != null) {
                MainContentCaptureSession session = this.mAttachInfo.mContentCaptureManager.getMainContentCaptureSession();
                session.notifyWindowBoundsChanged(session.getId(), getConfiguration().windowConfiguration.getBounds());
            }
            rootView.dispatchInitialProvideContentCaptureStructure();
        } finally {
            Trace.traceEnd(8L);
        }
    }

    private void handleContentCaptureFlush() {
        if (Trace.isTagEnabled(8L)) {
            Trace.traceBegin(8L, "flushContentCapture for " + getClass().getSimpleName());
        }
        try {
            if (isContentCaptureEnabled()) {
                ContentCaptureManager ccm = this.mAttachInfo.mContentCaptureManager;
                if (ccm == null) {
                    Log.m104w(TAG, "No ContentCapture on AttachInfo");
                } else {
                    ccm.flush(2);
                }
            }
        } finally {
            Trace.traceEnd(8L);
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:117:0x0235  */
    /* JADX WARN: Removed duplicated region for block: B:72:0x0126  */
    /* JADX WARN: Removed duplicated region for block: B:98:0x01bb  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    private boolean draw(boolean fullRedrawNeeded, boolean forceDraw) {
        int curScrollY;
        boolean fullRedrawNeeded2;
        int xOffset;
        boolean accessibilityFocusDirty;
        Drawable drawable;
        int yOffset;
        int xOffset2;
        Scroller scroller;
        Surface surface = this.mSurface;
        if (surface.isValid()) {
            if (!sFirstDrawComplete) {
                ArrayList<Runnable> arrayList = sFirstDrawHandlers;
                synchronized (arrayList) {
                    sFirstDrawComplete = true;
                    int count = arrayList.size();
                    for (int i = 0; i < count; i++) {
                        this.mHandler.post(sFirstDrawHandlers.get(i));
                    }
                }
            }
            scrollToRectOrFocus(null, false);
            if (this.mAttachInfo.mViewScrollChanged) {
                this.mAttachInfo.mViewScrollChanged = false;
                this.mAttachInfo.mTreeObserver.dispatchOnScrollChanged();
            }
            Scroller scroller2 = this.mScroller;
            boolean animating = scroller2 != null && scroller2.computeScrollOffset();
            if (animating) {
                curScrollY = this.mScroller.getCurrY();
            } else {
                int curScrollY2 = this.mScrollY;
                curScrollY = curScrollY2;
            }
            if (this.mCurScrollY == curScrollY) {
                fullRedrawNeeded2 = fullRedrawNeeded;
            } else {
                this.mCurScrollY = curScrollY;
                View view = this.mView;
                if (view instanceof RootViewSurfaceTaker) {
                    ((RootViewSurfaceTaker) view).onRootViewScrollYChanged(curScrollY);
                }
                fullRedrawNeeded2 = true;
            }
            float appScale = this.mAttachInfo.mApplicationScale;
            boolean scalingRequired = this.mAttachInfo.mScalingRequired;
            Rect dirty = this.mDirty;
            if (this.mSurfaceHolder != null) {
                dirty.setEmpty();
                if (animating && (scroller = this.mScroller) != null) {
                    scroller.abortAnimation();
                }
                return false;
            }
            if (fullRedrawNeeded2) {
                dirty.set(0, 0, (int) ((this.mWidth * appScale) + 0.5f), (int) ((this.mHeight * appScale) + 0.5f));
            }
            this.mAttachInfo.mTreeObserver.dispatchOnDraw();
            int xOffset3 = -this.mCanvasOffsetX;
            int yOffset2 = (-this.mCanvasOffsetY) + curScrollY;
            WindowManager.LayoutParams params = this.mWindowAttributes;
            Rect surfaceInsets = params != null ? params.surfaceInsets : null;
            if (surfaceInsets == null) {
                xOffset = xOffset3;
            } else {
                int xOffset4 = xOffset3 - surfaceInsets.left;
                yOffset2 -= surfaceInsets.top;
                dirty.offset(surfaceInsets.left, surfaceInsets.top);
                xOffset = xOffset4;
            }
            Drawable drawable2 = this.mAttachInfo.mAccessibilityFocusDrawable;
            if (drawable2 != null) {
                Rect bounds = this.mAttachInfo.mTmpInvalRect;
                boolean hasFocus = getAccessibilityFocusedRect(bounds);
                if (!hasFocus) {
                    bounds.setEmpty();
                }
                if (!bounds.equals(drawable2.getBounds())) {
                    accessibilityFocusDirty = true;
                    this.mAttachInfo.mDrawingTime = this.mChoreographer.getFrameTimeNanos() / 1000000;
                    boolean useAsyncReport = false;
                    if (dirty.isEmpty() || this.mIsAnimating || accessibilityFocusDirty) {
                        if (isHardwareEnabled()) {
                            int yOffset3 = yOffset2;
                            int xOffset5 = xOffset;
                            if (this.mAttachInfo.mThreadedRenderer != null && !this.mAttachInfo.mThreadedRenderer.isEnabled() && this.mAttachInfo.mThreadedRenderer.isRequested() && this.mSurface.isValid()) {
                                try {
                                    this.mAttachInfo.mThreadedRenderer.initializeIfNeeded(this.mWidth, this.mHeight, this.mAttachInfo, this.mSurface, surfaceInsets);
                                    this.mFullRedrawNeeded = true;
                                    scheduleTraversals();
                                    return false;
                                } catch (Surface.OutOfResourcesException e) {
                                    handleOutOfResourcesException(e);
                                    return false;
                                }
                            } else if (!drawSoftware(surface, this.mAttachInfo, xOffset5, yOffset3, scalingRequired, dirty, surfaceInsets)) {
                                return false;
                            }
                        } else {
                            boolean invalidateRoot = accessibilityFocusDirty || this.mInvalidateRootRequested;
                            this.mInvalidateRootRequested = false;
                            this.mIsAnimating = false;
                            if (this.mHardwareYOffset != yOffset2 || this.mHardwareXOffset != xOffset) {
                                this.mHardwareYOffset = yOffset2;
                                this.mHardwareXOffset = xOffset;
                                invalidateRoot = true;
                            }
                            if (invalidateRoot) {
                                this.mAttachInfo.mThreadedRenderer.invalidateRoot();
                            }
                            dirty.setEmpty();
                            boolean updated = updateContentDrawBounds();
                            boolean invalidateRoot2 = this.mReportNextDraw;
                            if (!invalidateRoot2) {
                                drawable = drawable2;
                            } else {
                                drawable = drawable2;
                                this.mAttachInfo.mThreadedRenderer.setStopped(false);
                            }
                            if (updated) {
                                requestDrawWindow();
                            }
                            useAsyncReport = true;
                            if (this.mUpdateHdrSdrRatioInfo) {
                                this.mUpdateHdrSdrRatioInfo = false;
                                yOffset = yOffset2;
                                xOffset2 = xOffset;
                                applyTransactionOnDraw(this.mTransaction.setExtendedRangeBrightness(getSurfaceControl(), this.mRenderHdrSdrRatio, this.mDesiredHdrSdrRatio));
                                this.mAttachInfo.mThreadedRenderer.setTargetHdrSdrRatio(this.mRenderHdrSdrRatio);
                            } else {
                                yOffset = yOffset2;
                                xOffset2 = xOffset;
                            }
                            if (forceDraw) {
                                this.mAttachInfo.mThreadedRenderer.forceDrawNextFrame();
                            }
                            this.mAttachInfo.mThreadedRenderer.draw(this.mView, this.mAttachInfo, this);
                        }
                    }
                    if (animating) {
                        this.mFullRedrawNeeded = true;
                        scheduleTraversals();
                    }
                    return useAsyncReport;
                }
            }
            accessibilityFocusDirty = false;
            this.mAttachInfo.mDrawingTime = this.mChoreographer.getFrameTimeNanos() / 1000000;
            boolean useAsyncReport2 = false;
            if (dirty.isEmpty()) {
            }
            if (isHardwareEnabled()) {
            }
            if (animating) {
            }
            return useAsyncReport2;
        }
        return false;
    }

    private boolean drawSoftware(Surface surface, View.AttachInfo attachInfo, int xoff, int yoff, boolean scalingRequired, Rect dirty, Rect surfaceInsets) {
        try {
            Canvas canvas = this.mSurface.lockCanvas(dirty);
            canvas.setDensity(this.mDensity);
            try {
                if (!canvas.isOpaque() || yoff != 0 || xoff != 0) {
                    canvas.drawColor(0, PorterDuff.Mode.CLEAR);
                }
                dirty.setEmpty();
                this.mIsAnimating = false;
                this.mView.mPrivateFlags |= 32;
                canvas.translate(-xoff, -yoff);
                CompatibilityInfo.Translator translator = this.mTranslator;
                if (translator != null) {
                    translator.translateCanvas(canvas);
                }
                canvas.setScreenDensity(scalingRequired ? this.mNoncompatDensity : 0);
                this.mView.draw(canvas);
                drawAccessibilityFocusedDrawableIfNeeded(canvas);
                surface.unlockCanvasAndPost(canvas);
                return true;
            } catch (IllegalArgumentException e) {
                Log.m109e(this.mTag, "Could not unlock surface", e);
                this.mLayoutRequested = true;
                return false;
            }
        } catch (Surface.OutOfResourcesException e2) {
            handleOutOfResourcesException(e2);
            return false;
        } catch (IllegalArgumentException e3) {
            Log.m109e(this.mTag, "Could not lock surface", e3);
            this.mLayoutRequested = true;
            return false;
        }
    }

    private void drawAccessibilityFocusedDrawableIfNeeded(Canvas canvas) {
        Rect bounds = this.mAttachInfo.mTmpInvalRect;
        if (getAccessibilityFocusedRect(bounds)) {
            Drawable drawable = getAccessibilityFocusedDrawable();
            if (drawable != null) {
                drawable.setBounds(bounds);
                drawable.draw(canvas);
            }
        } else if (this.mAttachInfo.mAccessibilityFocusDrawable != null) {
            this.mAttachInfo.mAccessibilityFocusDrawable.setBounds(0, 0, 0, 0);
        }
    }

    private boolean getAccessibilityFocusedRect(Rect bounds) {
        View host;
        AccessibilityManager manager = AccessibilityManager.getInstance(this.mView.mContext);
        if (!manager.isEnabled() || !manager.isTouchExplorationEnabled() || (host = this.mAccessibilityFocusedHost) == null || host.mAttachInfo == null) {
            return false;
        }
        AccessibilityNodeProvider provider = host.getAccessibilityNodeProvider();
        if (provider == null) {
            host.getBoundsOnScreen(bounds, true);
        } else {
            AccessibilityNodeInfo accessibilityNodeInfo = this.mAccessibilityFocusedVirtualView;
            if (accessibilityNodeInfo == null) {
                return false;
            }
            accessibilityNodeInfo.getBoundsInScreen(bounds);
        }
        View.AttachInfo attachInfo = this.mAttachInfo;
        bounds.offset(0, attachInfo.mViewRootImpl.mScrollY);
        bounds.offset(-attachInfo.mWindowLeft, -attachInfo.mWindowTop);
        if (!bounds.intersect(0, 0, attachInfo.mViewRootImpl.mWidth, attachInfo.mViewRootImpl.mHeight)) {
            bounds.setEmpty();
        }
        return !bounds.isEmpty();
    }

    private Drawable getAccessibilityFocusedDrawable() {
        if (this.mAttachInfo.mAccessibilityFocusDrawable == null) {
            TypedValue value = new TypedValue();
            boolean resolved = this.mView.mContext.getTheme().resolveAttribute(C4057R.attr.accessibilityFocusedDrawable, value, true);
            if (resolved) {
                this.mAttachInfo.mAccessibilityFocusDrawable = this.mView.mContext.getDrawable(value.resourceId);
            }
        }
        if (this.mAttachInfo.mAccessibilityFocusDrawable instanceof GradientDrawable) {
            GradientDrawable drawable = (GradientDrawable) this.mAttachInfo.mAccessibilityFocusDrawable;
            drawable.setStroke(this.mAccessibilityManager.getAccessibilityFocusStrokeWidth(), this.mAccessibilityManager.getAccessibilityFocusColor());
        }
        return this.mAttachInfo.mAccessibilityFocusDrawable;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void updateSystemGestureExclusionRectsForView(View view) {
        this.mGestureExclusionTracker.updateRectsForView(view);
        this.mHandler.sendEmptyMessage(30);
    }

    void systemGestureExclusionChanged() {
        List<Rect> rectsForWindowManager = this.mGestureExclusionTracker.computeChangedRects();
        if (rectsForWindowManager != null && this.mView != null) {
            try {
                this.mWindowSession.reportSystemGestureExclusionChanged(this.mWindow, rectsForWindowManager);
                this.mAttachInfo.mTreeObserver.dispatchOnSystemGestureExclusionRectsChanged(rectsForWindowManager);
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
    }

    public void setRootSystemGestureExclusionRects(List<Rect> rects) {
        this.mGestureExclusionTracker.setRootRects(rects);
        this.mHandler.sendEmptyMessage(30);
    }

    public List<Rect> getRootSystemGestureExclusionRects() {
        return this.mGestureExclusionTracker.getRootRects();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void updateKeepClearRectsForView(View view) {
        this.mKeepClearRectsTracker.updateRectsForView(view);
        this.mUnrestrictedKeepClearRectsTracker.updateRectsForView(view);
        this.mHandler.sendEmptyMessage(35);
    }

    private void updateKeepClearForAccessibilityFocusRect() {
        if (this.mViewConfiguration.isPreferKeepClearForFocusEnabled()) {
            if (this.mKeepClearAccessibilityFocusRect == null) {
                this.mKeepClearAccessibilityFocusRect = new Rect();
            }
            boolean hasAccessibilityFocus = getAccessibilityFocusedRect(this.mKeepClearAccessibilityFocusRect);
            if (!hasAccessibilityFocus) {
                this.mKeepClearAccessibilityFocusRect.setEmpty();
            }
            this.mHandler.obtainMessage(35, 1, 0).sendToTarget();
        }
    }

    void keepClearRectsChanged(boolean accessibilityFocusRectChanged) {
        boolean restrictedKeepClearRectsChanged = this.mKeepClearRectsTracker.computeChanges();
        boolean unrestrictedKeepClearRectsChanged = this.mUnrestrictedKeepClearRectsTracker.computeChanges();
        if ((restrictedKeepClearRectsChanged || unrestrictedKeepClearRectsChanged || accessibilityFocusRectChanged) && this.mView != null) {
            this.mHasPendingKeepClearAreaChange = true;
            if (!this.mHandler.hasMessages(36)) {
                this.mHandler.sendEmptyMessageDelayed(36, 100L);
                reportKeepClearAreasChanged();
            }
        }
    }

    void reportKeepClearAreasChanged() {
        if (!this.mHasPendingKeepClearAreaChange || this.mView == null) {
            return;
        }
        this.mHasPendingKeepClearAreaChange = false;
        List<Rect> restrictedKeepClearRects = this.mKeepClearRectsTracker.getLastComputedRects();
        List<Rect> unrestrictedKeepClearRects = this.mUnrestrictedKeepClearRectsTracker.getLastComputedRects();
        Rect rect = this.mKeepClearAccessibilityFocusRect;
        if (rect != null && !rect.isEmpty()) {
            restrictedKeepClearRects = new ArrayList(restrictedKeepClearRects);
            restrictedKeepClearRects.add(this.mKeepClearAccessibilityFocusRect);
        }
        try {
            this.mWindowSession.reportKeepClearAreasChanged(this.mWindow, restrictedKeepClearRects, unrestrictedKeepClearRects);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void requestInvalidateRootRenderNode() {
        this.mInvalidateRootRequested = true;
    }

    boolean scrollToRectOrFocus(Rect rectangle, boolean immediate) {
        Rect ci = this.mAttachInfo.mContentInsets;
        Rect vi = this.mAttachInfo.mVisibleInsets;
        int scrollY = 0;
        boolean handled = false;
        if (vi.left > ci.left || vi.top > ci.top || vi.right > ci.right || vi.bottom > ci.bottom) {
            scrollY = this.mScrollY;
            View focus = this.mView.findFocus();
            if (focus == null) {
                return false;
            }
            WeakReference<View> weakReference = this.mLastScrolledFocus;
            View lastScrolledFocus = weakReference != null ? weakReference.get() : null;
            if (focus != lastScrolledFocus) {
                rectangle = null;
            }
            if (focus != lastScrolledFocus || this.mScrollMayChange || rectangle != null) {
                this.mLastScrolledFocus = new WeakReference<>(focus);
                this.mScrollMayChange = false;
                if (focus.getGlobalVisibleRect(this.mVisRect, null)) {
                    if (rectangle == null) {
                        focus.getFocusedRect(this.mTempRect);
                        View view = this.mView;
                        if (view instanceof ViewGroup) {
                            ((ViewGroup) view).offsetDescendantRectToMyCoords(focus, this.mTempRect);
                        }
                    } else {
                        this.mTempRect.set(rectangle);
                    }
                    if (this.mTempRect.intersect(this.mVisRect)) {
                        if (this.mTempRect.height() <= (this.mView.getHeight() - vi.top) - vi.bottom) {
                            if (this.mTempRect.top < vi.top) {
                                scrollY = this.mTempRect.top - vi.top;
                            } else if (this.mTempRect.bottom > this.mView.getHeight() - vi.bottom) {
                                scrollY = this.mTempRect.bottom - (this.mView.getHeight() - vi.bottom);
                            } else {
                                scrollY = 0;
                            }
                        }
                        handled = true;
                    }
                }
            }
        }
        if (scrollY != this.mScrollY) {
            if (!immediate) {
                if (this.mScroller == null) {
                    this.mScroller = new Scroller(this.mView.getContext());
                }
                Scroller scroller = this.mScroller;
                int i = this.mScrollY;
                scroller.startScroll(0, i, 0, scrollY - i);
            } else {
                Scroller scroller2 = this.mScroller;
                if (scroller2 != null) {
                    scroller2.abortAnimation();
                }
            }
            this.mScrollY = scrollY;
        }
        return handled;
    }

    public View getAccessibilityFocusedHost() {
        return this.mAccessibilityFocusedHost;
    }

    public AccessibilityNodeInfo getAccessibilityFocusedVirtualView() {
        return this.mAccessibilityFocusedVirtualView;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setAccessibilityFocus(View view, AccessibilityNodeInfo node) {
        if (this.mAccessibilityFocusedVirtualView != null) {
            AccessibilityNodeInfo focusNode = this.mAccessibilityFocusedVirtualView;
            View focusHost = this.mAccessibilityFocusedHost;
            this.mAccessibilityFocusedHost = null;
            this.mAccessibilityFocusedVirtualView = null;
            focusHost.clearAccessibilityFocusNoCallbacks(64);
            AccessibilityNodeProvider provider = focusHost.getAccessibilityNodeProvider();
            if (provider != null) {
                focusNode.getBoundsInParent(this.mTempRect);
                focusHost.invalidate(this.mTempRect);
                int virtualNodeId = AccessibilityNodeInfo.getVirtualDescendantId(focusNode.getSourceNodeId());
                provider.performAction(virtualNodeId, 128, null);
            }
            focusNode.recycle();
        }
        View view2 = this.mAccessibilityFocusedHost;
        if (view2 != null && view2 != view) {
            view2.clearAccessibilityFocusNoCallbacks(64);
        }
        this.mAccessibilityFocusedHost = view;
        this.mAccessibilityFocusedVirtualView = node;
        updateKeepClearForAccessibilityFocusRect();
        if (this.mAttachInfo.mThreadedRenderer != null) {
            this.mAttachInfo.mThreadedRenderer.invalidateRoot();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean hasPointerCapture() {
        return this.mPointerCapture;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void requestPointerCapture(boolean enabled) {
        IBinder inputToken = getInputToken();
        if (inputToken == null) {
            Log.m110e(this.mTag, "No input channel to request Pointer Capture.");
        } else {
            this.mInputManager.requestPointerCapture(inputToken, enabled);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handlePointerCaptureChanged(boolean hasCapture) {
        if (this.mPointerCapture == hasCapture) {
            return;
        }
        this.mPointerCapture = hasCapture;
        View view = this.mView;
        if (view != null) {
            view.dispatchPointerCaptureChanged(hasCapture);
        }
    }

    private void updateRenderHdrSdrRatio() {
        this.mRenderHdrSdrRatio = this.mDisplay.getHdrSdrRatio();
        this.mUpdateHdrSdrRatioInfo = true;
    }

    private void updateColorModeIfNeeded(int colorMode) {
        if (this.mAttachInfo.mThreadedRenderer == null) {
            return;
        }
        if ((colorMode == 2 || colorMode == 3) && !this.mDisplay.isHdrSdrRatioAvailable()) {
            colorMode = 1;
        }
        if (colorMode != 4 && !getConfiguration().isScreenWideColorGamut()) {
            colorMode = 0;
        }
        float desiredRatio = this.mAttachInfo.mThreadedRenderer.setColorMode(colorMode);
        if (desiredRatio != this.mDesiredHdrSdrRatio) {
            this.mDesiredHdrSdrRatio = desiredRatio;
            updateRenderHdrSdrRatio();
            if (this.mDesiredHdrSdrRatio < 1.01f) {
                this.mDisplay.unregisterHdrSdrRatioChangedListener(this.mHdrSdrRatioChangedListener);
                this.mHdrSdrRatioChangedListener = null;
                return;
            }
            Consumer<Display> consumer = new Consumer() { // from class: android.view.ViewRootImpl$$ExternalSyntheticLambda18
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    ViewRootImpl.this.lambda$updateColorModeIfNeeded$8((Display) obj);
                }
            };
            this.mHdrSdrRatioChangedListener = consumer;
            this.mDisplay.registerHdrSdrRatioChangedListener(this.mExecutor, consumer);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updateColorModeIfNeeded$8(Display display) {
        setTargetHdrSdrRatio(display.getHdrSdrRatio());
    }

    public void setTargetHdrSdrRatio(float ratio) {
        if (this.mRenderHdrSdrRatio != ratio) {
            this.mRenderHdrSdrRatio = ratio;
            this.mUpdateHdrSdrRatioInfo = true;
            invalidate();
        }
    }

    @Override // android.view.ViewParent
    public void requestChildFocus(View child, View focused) {
        checkThread();
        scheduleTraversals();
    }

    @Override // android.view.ViewParent
    public void clearChildFocus(View child) {
        checkThread();
        scheduleTraversals();
    }

    @Override // android.view.ViewParent
    public ViewParent getParentForAccessibility() {
        return null;
    }

    @Override // android.view.ViewParent
    public void focusableViewAvailable(View v) {
        checkThread();
        View view = this.mView;
        if (view != null) {
            if (!view.hasFocus()) {
                if (sAlwaysAssignFocus || !this.mAttachInfo.mInTouchMode) {
                    v.requestFocus();
                    return;
                }
                return;
            }
            View focused = this.mView.findFocus();
            if (focused instanceof ViewGroup) {
                ViewGroup group = (ViewGroup) focused;
                if (group.getDescendantFocusability() == 262144 && isViewDescendantOf(v, focused)) {
                    v.requestFocus();
                }
            }
        }
    }

    @Override // android.view.ViewParent
    public void recomputeViewAttributes(View child) {
        checkThread();
        if (this.mView == child) {
            this.mAttachInfo.mRecomputeGlobalAttributes = true;
            if (!this.mWillDrawSoon) {
                scheduleTraversals();
            }
        }
    }

    void dispatchDetachedFromWindow() {
        InputQueue inputQueue;
        this.mInsetsController.onWindowFocusLost();
        this.mFirstInputStage.onDetachedFromWindow();
        View view = this.mView;
        if (view != null && view.mAttachInfo != null) {
            this.mAttachInfo.mTreeObserver.dispatchOnWindowAttachedChange(false);
            this.mView.dispatchDetachedFromWindow();
        }
        this.mAccessibilityInteractionConnectionManager.ensureNoConnection();
        this.mAccessibilityInteractionConnectionManager.ensureNoDirectConnection();
        removeSendWindowContentChangedCallback();
        destroyHardwareRenderer();
        setAccessibilityFocus(null, null);
        this.mInsetsController.cancelExistingAnimations();
        this.mView.assignParent(null);
        this.mView = null;
        this.mAttachInfo.mRootView = null;
        destroySurface();
        InputQueue.Callback callback = this.mInputQueueCallback;
        if (callback != null && (inputQueue = this.mInputQueue) != null) {
            callback.onInputQueueDestroyed(inputQueue);
            this.mInputQueue.dispose();
            this.mInputQueueCallback = null;
            this.mInputQueue = null;
        }
        try {
            this.mWindowSession.remove(this.mWindow);
        } catch (RemoteException e) {
        }
        WindowInputEventReceiver windowInputEventReceiver = this.mInputEventReceiver;
        if (windowInputEventReceiver != null) {
            windowInputEventReceiver.dispose();
            this.mInputEventReceiver = null;
        }
        unregisterListeners();
        unscheduleTraversals();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void performConfigurationChange(MergedConfiguration mergedConfiguration, boolean force, int newDisplayId) {
        if (mergedConfiguration == null) {
            throw new IllegalArgumentException("No merged config provided.");
        }
        int lastRotation = this.mLastReportedMergedConfiguration.getMergedConfiguration().windowConfiguration.getRotation();
        int newRotation = mergedConfiguration.getMergedConfiguration().windowConfiguration.getRotation();
        if (lastRotation != newRotation) {
            this.mUpdateSurfaceNeeded = true;
            if (!this.mIsInTraversal) {
                this.mForceNextWindowRelayout = true;
            }
        }
        Configuration globalConfig = mergedConfiguration.getGlobalConfiguration();
        Configuration overrideConfig = mergedConfiguration.getOverrideConfiguration();
        CompatibilityInfo ci = this.mDisplay.getDisplayAdjustments().getCompatibilityInfo();
        if (!ci.equals(CompatibilityInfo.DEFAULT_COMPATIBILITY_INFO)) {
            globalConfig = new Configuration(globalConfig);
            ci.applyToConfiguration(this.mNoncompatDensity, globalConfig);
        }
        ArrayList<ConfigChangedCallback> arrayList = sConfigCallbacks;
        synchronized (arrayList) {
            for (int i = arrayList.size() - 1; i >= 0; i--) {
                sConfigCallbacks.get(i).onConfigurationChanged(globalConfig);
            }
        }
        this.mLastReportedMergedConfiguration.setConfiguration(globalConfig, overrideConfig);
        this.mForceNextConfigUpdate = force;
        ActivityConfigCallback activityConfigCallback = this.mActivityConfigCallback;
        if (activityConfigCallback != null) {
            activityConfigCallback.onConfigurationChanged(overrideConfig, newDisplayId);
        } else {
            updateConfiguration(newDisplayId);
        }
        this.mForceNextConfigUpdate = false;
    }

    public void updateConfiguration(int newDisplayId) {
        View view = this.mView;
        if (view == null) {
            return;
        }
        Resources localResources = view.getResources();
        Configuration config = localResources.getConfiguration();
        if (newDisplayId != -1) {
            onMovedToDisplay(newDisplayId, config);
        }
        if (this.mForceNextConfigUpdate || this.mLastConfigurationFromResources.diff(config) != 0) {
            updateInternalDisplay(this.mDisplay.getDisplayId(), localResources);
            updateLastConfigurationFromResources(config);
            this.mView.dispatchConfigurationChanged(config);
            this.mForceNextWindowRelayout = true;
            requestLayout();
        }
        updateForceDarkMode();
    }

    private void updateLastConfigurationFromResources(Configuration resConfig) {
        View view;
        int lastLayoutDirection = this.mLastConfigurationFromResources.getLayoutDirection();
        int currentLayoutDirection = resConfig.getLayoutDirection();
        this.mLastConfigurationFromResources.setTo(resConfig);
        if (lastLayoutDirection != currentLayoutDirection && (view = this.mView) != null && this.mViewLayoutDirectionInitial == 2) {
            view.setLayoutDirection(currentLayoutDirection);
        }
    }

    public static boolean isViewDescendantOf(View child, View parent) {
        if (child == parent) {
            return true;
        }
        ViewParent theParent = child.getParent();
        return (theParent instanceof ViewGroup) && isViewDescendantOf((View) theParent, parent);
    }

    private static void forceLayout(View view) {
        view.forceLayout();
        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            int count = group.getChildCount();
            for (int i = 0; i < count; i++) {
                forceLayout(group.getChildAt(i));
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes4.dex */
    public final class ViewRootHandler extends Handler {
        ViewRootHandler() {
        }

        @Override // android.p008os.Handler
        public String getMessageName(Message message) {
            switch (message.what) {
                case 1:
                    return "MSG_INVALIDATE";
                case 2:
                    return "MSG_INVALIDATE_RECT";
                case 3:
                    return "MSG_DIE";
                case 4:
                    return "MSG_RESIZED";
                case 5:
                    return "MSG_RESIZED_REPORT";
                case 6:
                    return "MSG_WINDOW_FOCUS_CHANGED";
                case 7:
                    return "MSG_DISPATCH_INPUT_EVENT";
                case 8:
                    return "MSG_DISPATCH_APP_VISIBILITY";
                case 9:
                    return "MSG_DISPATCH_GET_NEW_SURFACE";
                case 10:
                case 20:
                case 22:
                case 26:
                case 33:
                default:
                    return super.getMessageName(message);
                case 11:
                    return "MSG_DISPATCH_KEY_FROM_IME";
                case 12:
                    return "MSG_DISPATCH_KEY_FROM_AUTOFILL";
                case 13:
                    return "MSG_CHECK_FOCUS";
                case 14:
                    return "MSG_CLOSE_SYSTEM_DIALOGS";
                case 15:
                    return "MSG_DISPATCH_DRAG_EVENT";
                case 16:
                    return "MSG_DISPATCH_DRAG_LOCATION_EVENT";
                case 17:
                    return "MSG_DISPATCH_SYSTEM_UI_VISIBILITY";
                case 18:
                    return "MSG_UPDATE_CONFIGURATION";
                case 19:
                    return "MSG_PROCESS_INPUT_EVENTS";
                case 21:
                    return "MSG_CLEAR_ACCESSIBILITY_FOCUS_HOST";
                case 23:
                    return "MSG_WINDOW_MOVED";
                case 24:
                    return "MSG_SYNTHESIZE_INPUT_EVENT";
                case 25:
                    return "MSG_DISPATCH_WINDOW_SHOWN";
                case 27:
                    return "MSG_UPDATE_POINTER_ICON";
                case 28:
                    return "MSG_POINTER_CAPTURE_CHANGED";
                case 29:
                    return "MSG_INSETS_CONTROL_CHANGED";
                case 30:
                    return "MSG_SYSTEM_GESTURE_EXCLUSION_CHANGED";
                case 31:
                    return "MSG_SHOW_INSETS";
                case 32:
                    return "MSG_HIDE_INSETS";
                case 34:
                    return "MSG_WINDOW_TOUCH_MODE_CHANGED";
                case 35:
                    return "MSG_KEEP_CLEAR_RECTS_CHANGED";
            }
        }

        @Override // android.p008os.Handler
        public boolean sendMessageAtTime(Message msg, long uptimeMillis) {
            if (msg.what == 26 && msg.obj == null) {
                throw new NullPointerException("Attempted to call MSG_REQUEST_KEYBOARD_SHORTCUTS with null receiver:");
            }
            return super.sendMessageAtTime(msg, uptimeMillis);
        }

        @Override // android.p008os.Handler
        public void handleMessage(Message msg) {
            if (Trace.isTagEnabled(8L)) {
                Trace.traceBegin(8L, getMessageName(msg));
            }
            try {
                handleMessageImpl(msg);
            } finally {
                Trace.traceEnd(8L);
            }
        }

        private void handleMessageImpl(Message msg) {
            switch (msg.what) {
                case 1:
                    ((View) msg.obj).invalidate();
                    return;
                case 2:
                    View.AttachInfo.InvalidateInfo info = (View.AttachInfo.InvalidateInfo) msg.obj;
                    info.target.invalidate(info.left, info.top, info.right, info.bottom);
                    info.recycle();
                    return;
                case 3:
                    ViewRootImpl.this.doDie();
                    return;
                case 4:
                case 5:
                    SomeArgs args = (SomeArgs) msg.obj;
                    ViewRootImpl.this.mInsetsController.onStateChanged((InsetsState) args.arg3);
                    ViewRootImpl.this.handleResized(msg.what, args);
                    args.recycle();
                    return;
                case 6:
                    ViewRootImpl.this.handleWindowFocusChanged();
                    return;
                case 7:
                    SomeArgs args2 = (SomeArgs) msg.obj;
                    InputEventReceiver receiver = (InputEventReceiver) args2.arg2;
                    ViewRootImpl.this.enqueueInputEvent((InputEvent) args2.arg1, receiver, 0, true);
                    args2.recycle();
                    return;
                case 8:
                    ViewRootImpl.this.handleAppVisibility(msg.arg1 != 0);
                    return;
                case 9:
                    ViewRootImpl.this.handleGetNewSurface();
                    return;
                case 10:
                case 20:
                default:
                    return;
                case 11:
                    KeyEvent event = (KeyEvent) msg.obj;
                    if ((event.getFlags() & 8) != 0) {
                        event = KeyEvent.changeFlags(event, event.getFlags() & (-9));
                    }
                    ViewRootImpl.this.enqueueInputEvent(event, null, 1, true);
                    return;
                case 12:
                    ViewRootImpl.this.enqueueInputEvent((KeyEvent) msg.obj, null, 0, true);
                    return;
                case 13:
                    ViewRootImpl.this.getImeFocusController().onScheduledCheckFocus();
                    return;
                case 14:
                    if (ViewRootImpl.this.mView != null) {
                        ViewRootImpl.this.mView.onCloseSystemDialogs((String) msg.obj);
                        return;
                    }
                    return;
                case 15:
                case 16:
                    DragEvent event2 = (DragEvent) msg.obj;
                    event2.mLocalState = ViewRootImpl.this.mLocalDragState;
                    ViewRootImpl.this.handleDragEvent(event2);
                    return;
                case 17:
                    ViewRootImpl.this.handleDispatchSystemUiVisibilityChanged();
                    return;
                case 18:
                    Configuration config = (Configuration) msg.obj;
                    if (config.isOtherSeqNewer(ViewRootImpl.this.mLastReportedMergedConfiguration.getMergedConfiguration())) {
                        config = ViewRootImpl.this.mLastReportedMergedConfiguration.getGlobalConfiguration();
                    }
                    ViewRootImpl.this.mPendingMergedConfiguration.setConfiguration(config, ViewRootImpl.this.mLastReportedMergedConfiguration.getOverrideConfiguration());
                    ViewRootImpl.this.performConfigurationChange(new MergedConfiguration(ViewRootImpl.this.mPendingMergedConfiguration), false, -1);
                    return;
                case 19:
                    ViewRootImpl.this.mProcessInputEventsScheduled = false;
                    ViewRootImpl.this.doProcessInputEvents();
                    return;
                case 21:
                    ViewRootImpl.this.setAccessibilityFocus(null, null);
                    return;
                case 22:
                    if (ViewRootImpl.this.mView != null) {
                        ViewRootImpl viewRootImpl = ViewRootImpl.this;
                        viewRootImpl.invalidateWorld(viewRootImpl.mView);
                        return;
                    }
                    return;
                case 23:
                    if (ViewRootImpl.this.mAdded) {
                        int w = ViewRootImpl.this.mWinFrame.width();
                        int h = ViewRootImpl.this.mWinFrame.height();
                        int l = msg.arg1;
                        int t = msg.arg2;
                        ViewRootImpl.this.mTmpFrames.frame.left = l;
                        ViewRootImpl.this.mTmpFrames.frame.right = l + w;
                        ViewRootImpl.this.mTmpFrames.frame.top = t;
                        ViewRootImpl.this.mTmpFrames.frame.bottom = t + h;
                        ViewRootImpl viewRootImpl2 = ViewRootImpl.this;
                        viewRootImpl2.setFrame(viewRootImpl2.mTmpFrames.frame, false);
                        ViewRootImpl viewRootImpl3 = ViewRootImpl.this;
                        viewRootImpl3.maybeHandleWindowMove(viewRootImpl3.mWinFrame);
                        return;
                    }
                    return;
                case 24:
                    ViewRootImpl.this.enqueueInputEvent((InputEvent) msg.obj, null, 32, true);
                    return;
                case 25:
                    ViewRootImpl.this.handleDispatchWindowShown();
                    return;
                case 26:
                    IResultReceiver receiver2 = (IResultReceiver) msg.obj;
                    int deviceId = msg.arg1;
                    ViewRootImpl.this.handleRequestKeyboardShortcuts(receiver2, deviceId);
                    return;
                case 27:
                    ViewRootImpl.this.resetPointerIcon((MotionEvent) msg.obj);
                    return;
                case 28:
                    boolean hasCapture = msg.arg1 != 0;
                    ViewRootImpl.this.handlePointerCaptureChanged(hasCapture);
                    return;
                case 29:
                    SomeArgs args3 = (SomeArgs) msg.obj;
                    ViewRootImpl.this.mInsetsController.onStateChanged((InsetsState) args3.arg1);
                    InsetsSourceControl[] controls = (InsetsSourceControl[]) args3.arg2;
                    if (ViewRootImpl.this.mAdded) {
                        ViewRootImpl.this.mInsetsController.onControlsChanged(controls);
                    } else if (controls != null) {
                        for (InsetsSourceControl control : controls) {
                            if (control != null) {
                                control.release(new InsetsAnimationThreadControlRunner$$ExternalSyntheticLambda0());
                            }
                        }
                    }
                    args3.recycle();
                    return;
                case 30:
                    ViewRootImpl.this.systemGestureExclusionChanged();
                    return;
                case 31:
                    ImeTracker.Token statsToken = (ImeTracker.Token) msg.obj;
                    ImeTracker.forLogging().onProgress(statsToken, 30);
                    if (ViewRootImpl.this.mView == null) {
                        Object[] objArr = new Object[2];
                        objArr[0] = Integer.valueOf(msg.arg1);
                        objArr[1] = Boolean.valueOf(msg.arg2 == 1);
                        Log.m110e(ViewRootImpl.TAG, String.format("Calling showInsets(%d,%b) on window that no longer has views.", objArr));
                    }
                    ViewRootImpl.this.clearLowProfileModeIfNeeded(msg.arg1, msg.arg2 == 1);
                    ViewRootImpl.this.mInsetsController.show(msg.arg1, msg.arg2 == 1, statsToken);
                    return;
                case 32:
                    ImeTracker.Token statsToken2 = (ImeTracker.Token) msg.obj;
                    ImeTracker.forLogging().onProgress(statsToken2, 31);
                    ViewRootImpl.this.mInsetsController.hide(msg.arg1, msg.arg2 == 1, statsToken2);
                    return;
                case 33:
                    ViewRootImpl.this.handleScrollCaptureRequest((IScrollCaptureResponseListener) msg.obj);
                    return;
                case 34:
                    ViewRootImpl.this.handleWindowTouchModeChanged();
                    return;
                case 35:
                    ViewRootImpl.this.keepClearRectsChanged(msg.arg1 == 1);
                    return;
                case 36:
                    ViewRootImpl.this.reportKeepClearAreasChanged();
                    return;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$9(Runnable r) {
        this.mHandler.post(r);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean ensureTouchMode(boolean inTouchMode) {
        if (this.mAttachInfo.mInTouchMode == inTouchMode) {
            return false;
        }
        try {
            IWindowManager windowManager = WindowManagerGlobal.getWindowManagerService();
            windowManager.setInTouchMode(inTouchMode, getDisplayId());
            return ensureTouchModeLocally(inTouchMode);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean ensureTouchModeLocally(boolean inTouchMode) {
        if (this.mAttachInfo.mInTouchMode == inTouchMode) {
            return false;
        }
        this.mAttachInfo.mInTouchMode = inTouchMode;
        this.mAttachInfo.mTreeObserver.dispatchOnTouchModeChanged(inTouchMode);
        return inTouchMode ? enterTouchMode() : leaveTouchMode();
    }

    private boolean enterTouchMode() {
        View focused;
        View view = this.mView;
        if (view == null || !view.hasFocus() || (focused = this.mView.findFocus()) == null || focused.isFocusableInTouchMode()) {
            return false;
        }
        ViewGroup ancestorToTakeFocus = findAncestorToTakeFocusInTouchMode(focused);
        if (ancestorToTakeFocus != null) {
            return ancestorToTakeFocus.requestFocus();
        }
        focused.clearFocusInternal(null, true, false);
        return true;
    }

    private static ViewGroup findAncestorToTakeFocusInTouchMode(View focused) {
        ViewParent parent = focused.getParent();
        while (parent instanceof ViewGroup) {
            ViewGroup vgParent = (ViewGroup) parent;
            if (vgParent.getDescendantFocusability() == 262144 && vgParent.isFocusableInTouchMode()) {
                return vgParent;
            }
            if (vgParent.isRootNamespace()) {
                return null;
            }
            parent = vgParent.getParent();
        }
        return null;
    }

    private boolean leaveTouchMode() {
        View view = this.mView;
        if (view != null) {
            if (view.hasFocus()) {
                View focusedView = this.mView.findFocus();
                if (!(focusedView instanceof ViewGroup) || ((ViewGroup) focusedView).getDescendantFocusability() != 262144) {
                    return false;
                }
            }
            return this.mView.restoreDefaultFocus();
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes4.dex */
    public abstract class InputStage {
        protected static final int FINISH_HANDLED = 1;
        protected static final int FINISH_NOT_HANDLED = 2;
        protected static final int FORWARD = 0;
        private final InputStage mNext;
        private String mTracePrefix;

        public InputStage(InputStage next) {
            this.mNext = next;
        }

        public final void deliver(QueuedInputEvent q) {
            if ((q.mFlags & 4) != 0) {
                forward(q);
            } else if (shouldDropInputEvent(q)) {
                finish(q, false);
            } else {
                traceEvent(q, 8L);
                try {
                    int result = onProcess(q);
                    Trace.traceEnd(8L);
                    apply(q, result);
                } catch (Throwable th) {
                    Trace.traceEnd(8L);
                    throw th;
                }
            }
        }

        protected void finish(QueuedInputEvent q, boolean handled) {
            q.mFlags |= 4;
            if (handled) {
                q.mFlags |= 8;
            }
            forward(q);
        }

        protected void forward(QueuedInputEvent q) {
            onDeliverToNext(q);
        }

        protected void apply(QueuedInputEvent q, int result) {
            if (result == 0) {
                forward(q);
            } else if (result == 1) {
                finish(q, true);
            } else if (result == 2) {
                finish(q, false);
            } else {
                throw new IllegalArgumentException("Invalid result: " + result);
            }
        }

        protected int onProcess(QueuedInputEvent q) {
            return 0;
        }

        protected void onDeliverToNext(QueuedInputEvent q) {
            InputStage inputStage = this.mNext;
            if (inputStage != null) {
                inputStage.deliver(q);
            } else {
                ViewRootImpl.this.finishInputEvent(q);
            }
        }

        protected void onWindowFocusChanged(boolean hasWindowFocus) {
            InputStage inputStage = this.mNext;
            if (inputStage != null) {
                inputStage.onWindowFocusChanged(hasWindowFocus);
            }
        }

        protected void onDetachedFromWindow() {
            InputStage inputStage = this.mNext;
            if (inputStage != null) {
                inputStage.onDetachedFromWindow();
            }
        }

        protected boolean shouldDropInputEvent(QueuedInputEvent q) {
            String reason;
            if (ViewRootImpl.this.mView == null || !ViewRootImpl.this.mAdded) {
                Slog.m90w(ViewRootImpl.this.mTag, "Dropping event due to root view being removed: " + q.mEvent);
                return true;
            }
            if (!ViewRootImpl.this.mAttachInfo.mHasWindowFocus && !q.mEvent.isFromSource(2) && !ViewRootImpl.this.isAutofillUiShowing()) {
                reason = "no window focus";
            } else if (ViewRootImpl.this.mStopped) {
                reason = "window is stopped";
            } else if (ViewRootImpl.this.mIsAmbientMode && !q.mEvent.isFromSource(1)) {
                reason = "non-button event in ambient mode";
            } else if (!ViewRootImpl.this.mPausedForTransition || isBack(q.mEvent)) {
                return false;
            } else {
                reason = "paused for transition";
            }
            if (ViewRootImpl.isTerminalInputEvent(q.mEvent)) {
                q.mEvent.cancel();
                Slog.m90w(ViewRootImpl.this.mTag, "Cancelling event (" + reason + "):" + q.mEvent);
                return false;
            }
            Slog.m90w(ViewRootImpl.this.mTag, "Dropping event (" + reason + "):" + q.mEvent);
            return true;
        }

        void dump(String prefix, PrintWriter writer) {
            InputStage inputStage = this.mNext;
            if (inputStage != null) {
                inputStage.dump(prefix, writer);
            }
        }

        boolean isBack(InputEvent event) {
            return (event instanceof KeyEvent) && ((KeyEvent) event).getKeyCode() == 4;
        }

        private void traceEvent(QueuedInputEvent q, long traceTag) {
            if (!Trace.isTagEnabled(traceTag)) {
                return;
            }
            if (this.mTracePrefix == null) {
                this.mTracePrefix = getClass().getSimpleName();
            }
            Trace.traceBegin(traceTag, this.mTracePrefix + " id=0x" + Integer.toHexString(q.mEvent.getId()));
        }
    }

    /* loaded from: classes4.dex */
    abstract class AsyncInputStage extends InputStage {
        protected static final int DEFER = 3;
        private QueuedInputEvent mQueueHead;
        private int mQueueLength;
        private QueuedInputEvent mQueueTail;
        private final String mTraceCounter;

        public AsyncInputStage(InputStage next, String traceCounter) {
            super(next);
            this.mTraceCounter = traceCounter;
        }

        protected void defer(QueuedInputEvent q) {
            q.mFlags |= 2;
            enqueue(q);
        }

        @Override // android.view.ViewRootImpl.InputStage
        protected void forward(QueuedInputEvent q) {
            q.mFlags &= -3;
            QueuedInputEvent curr = this.mQueueHead;
            if (curr == null) {
                super.forward(q);
                return;
            }
            int deviceId = q.mEvent.getDeviceId();
            QueuedInputEvent prev = null;
            boolean blocked = false;
            while (curr != null && curr != q) {
                if (!blocked && deviceId == curr.mEvent.getDeviceId()) {
                    blocked = true;
                }
                prev = curr;
                curr = curr.mNext;
            }
            if (blocked) {
                if (curr == null) {
                    enqueue(q);
                    return;
                }
                return;
            }
            if (curr != null) {
                curr = curr.mNext;
                dequeue(q, prev);
            }
            super.forward(q);
            while (curr != null) {
                if (deviceId == curr.mEvent.getDeviceId()) {
                    if ((curr.mFlags & 2) == 0) {
                        QueuedInputEvent next = curr.mNext;
                        dequeue(curr, prev);
                        super.forward(curr);
                        curr = next;
                    } else {
                        return;
                    }
                } else {
                    prev = curr;
                    curr = curr.mNext;
                }
            }
        }

        @Override // android.view.ViewRootImpl.InputStage
        protected void apply(QueuedInputEvent q, int result) {
            if (result == 3) {
                defer(q);
            } else {
                super.apply(q, result);
            }
        }

        private void enqueue(QueuedInputEvent q) {
            QueuedInputEvent queuedInputEvent = this.mQueueTail;
            if (queuedInputEvent == null) {
                this.mQueueHead = q;
                this.mQueueTail = q;
            } else {
                queuedInputEvent.mNext = q;
                this.mQueueTail = q;
            }
            int i = this.mQueueLength + 1;
            this.mQueueLength = i;
            Trace.traceCounter(4L, this.mTraceCounter, i);
        }

        private void dequeue(QueuedInputEvent q, QueuedInputEvent prev) {
            if (prev == null) {
                this.mQueueHead = q.mNext;
            } else {
                prev.mNext = q.mNext;
            }
            if (this.mQueueTail == q) {
                this.mQueueTail = prev;
            }
            q.mNext = null;
            int i = this.mQueueLength - 1;
            this.mQueueLength = i;
            Trace.traceCounter(4L, this.mTraceCounter, i);
        }

        @Override // android.view.ViewRootImpl.InputStage
        void dump(String prefix, PrintWriter writer) {
            writer.print(prefix);
            writer.print(getClass().getName());
            writer.print(": mQueueLength=");
            writer.println(this.mQueueLength);
            super.dump(prefix, writer);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes4.dex */
    public final class NativePreImeInputStage extends AsyncInputStage implements InputQueue.FinishedInputEventCallback {
        public NativePreImeInputStage(InputStage next, String traceCounter) {
            super(next, traceCounter);
        }

        @Override // android.view.ViewRootImpl.InputStage
        protected int onProcess(QueuedInputEvent q) {
            if (q.mEvent instanceof KeyEvent) {
                KeyEvent event = (KeyEvent) q.mEvent;
                if (isBack(event) && ViewRootImpl.this.mContext != null && ViewRootImpl.this.mOnBackInvokedDispatcher.isOnBackInvokedCallbackEnabled()) {
                    OnBackInvokedCallback topCallback = ViewRootImpl.this.getOnBackInvokedDispatcher().getTopCallback();
                    if (event.getAction() == 1) {
                        if (topCallback != null) {
                            topCallback.onBackInvoked();
                            return 1;
                        }
                    } else {
                        return 2;
                    }
                }
            }
            if (ViewRootImpl.this.mInputQueue != null && (q.mEvent instanceof KeyEvent)) {
                ViewRootImpl.this.mInputQueue.sendInputEvent(q.mEvent, q, true, this);
                return 3;
            }
            return 0;
        }

        @Override // android.view.InputQueue.FinishedInputEventCallback
        public void onFinishedInputEvent(Object token, boolean handled) {
            QueuedInputEvent q = (QueuedInputEvent) token;
            if (handled) {
                finish(q, true);
            } else {
                forward(q);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes4.dex */
    public final class ViewPreImeInputStage extends InputStage {
        public ViewPreImeInputStage(InputStage next) {
            super(next);
        }

        @Override // android.view.ViewRootImpl.InputStage
        protected int onProcess(QueuedInputEvent q) {
            if (q.mEvent instanceof KeyEvent) {
                return processKeyEvent(q);
            }
            return 0;
        }

        private int processKeyEvent(QueuedInputEvent q) {
            KeyEvent event = (KeyEvent) q.mEvent;
            if (ViewRootImpl.this.mView.dispatchKeyEventPreIme(event)) {
                return 1;
            }
            return 0;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes4.dex */
    public final class ImeInputStage extends AsyncInputStage implements InputMethodManager.FinishedInputEventCallback {
        public ImeInputStage(InputStage next, String traceCounter) {
            super(next, traceCounter);
        }

        @Override // android.view.ViewRootImpl.InputStage
        protected int onProcess(QueuedInputEvent q) {
            int result = ViewRootImpl.this.mImeFocusController.onProcessImeInputStage(q, q.mEvent, ViewRootImpl.this.mWindowAttributes, this);
            switch (result) {
                case -1:
                    return 3;
                case 0:
                    return 0;
                case 1:
                    return 1;
                default:
                    throw new IllegalStateException("Unexpected result=" + result);
            }
        }

        @Override // android.view.inputmethod.InputMethodManager.FinishedInputEventCallback
        public void onFinishedInputEvent(Object token, boolean handled) {
            QueuedInputEvent q = (QueuedInputEvent) token;
            if (handled) {
                finish(q, true);
            } else {
                forward(q);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes4.dex */
    public final class EarlyPostImeInputStage extends InputStage {
        public EarlyPostImeInputStage(InputStage next) {
            super(next);
        }

        @Override // android.view.ViewRootImpl.InputStage
        protected int onProcess(QueuedInputEvent q) {
            if (q.mEvent instanceof KeyEvent) {
                return processKeyEvent(q);
            }
            if (q.mEvent instanceof MotionEvent) {
                return processMotionEvent(q);
            }
            return 0;
        }

        private int processKeyEvent(QueuedInputEvent q) {
            KeyEvent event = (KeyEvent) q.mEvent;
            if (ViewRootImpl.this.mAttachInfo.mTooltipHost != null) {
                ViewRootImpl.this.mAttachInfo.mTooltipHost.handleTooltipKey(event);
            }
            if (ViewRootImpl.this.checkForLeavingTouchModeAndConsume(event)) {
                return 1;
            }
            ViewRootImpl.this.mFallbackEventHandler.preDispatchKeyEvent(event);
            if (event.getAction() == 0) {
                ViewRootImpl.this.mLastClickToolType = 0;
            }
            return 0;
        }

        private int processMotionEvent(QueuedInputEvent q) {
            MotionEvent event = (MotionEvent) q.mEvent;
            if (event.isFromSource(2)) {
                return processPointerEvent(q);
            }
            int action = event.getActionMasked();
            if ((action == 0 || action == 8) && event.isFromSource(8)) {
                ViewRootImpl.this.ensureTouchMode(false);
            }
            return 0;
        }

        private int processPointerEvent(QueuedInputEvent q) {
            AutofillManager afm;
            MotionEvent event = (MotionEvent) q.mEvent;
            if (ViewRootImpl.this.mTranslator != null) {
                ViewRootImpl.this.mTranslator.translateEventInScreenToAppWindow(event);
            }
            int action = event.getAction();
            if (action == 0 || action == 8) {
                ViewRootImpl.this.ensureTouchMode(true);
            }
            if (action == 0 && (afm = ViewRootImpl.this.getAutofillManager()) != null) {
                afm.requestHideFillUi();
            }
            if (action == 0 && ViewRootImpl.this.mAttachInfo.mTooltipHost != null) {
                ViewRootImpl.this.mAttachInfo.mTooltipHost.hideTooltip();
            }
            if (ViewRootImpl.this.mCurScrollY != 0) {
                event.offsetLocation(0.0f, ViewRootImpl.this.mCurScrollY);
            }
            if (event.isTouchEvent()) {
                ViewRootImpl.this.mLastTouchPoint.f78x = event.getRawX();
                ViewRootImpl.this.mLastTouchPoint.f79y = event.getRawY();
                ViewRootImpl.this.mLastTouchSource = event.getSource();
                if (event.getActionMasked() == 1) {
                    ViewRootImpl.this.mLastClickToolType = event.getToolType(event.getActionIndex());
                    return 0;
                }
                return 0;
            }
            return 0;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes4.dex */
    public final class NativePostImeInputStage extends AsyncInputStage implements InputQueue.FinishedInputEventCallback {
        public NativePostImeInputStage(InputStage next, String traceCounter) {
            super(next, traceCounter);
        }

        @Override // android.view.ViewRootImpl.InputStage
        protected int onProcess(QueuedInputEvent q) {
            if (ViewRootImpl.this.mInputQueue != null) {
                ViewRootImpl.this.mInputQueue.sendInputEvent(q.mEvent, q, false, this);
                return 3;
            }
            return 0;
        }

        @Override // android.view.InputQueue.FinishedInputEventCallback
        public void onFinishedInputEvent(Object token, boolean handled) {
            QueuedInputEvent q = (QueuedInputEvent) token;
            if (handled) {
                finish(q, true);
            } else {
                forward(q);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes4.dex */
    public final class ViewPostImeInputStage extends InputStage {
        public ViewPostImeInputStage(InputStage next) {
            super(next);
        }

        @Override // android.view.ViewRootImpl.InputStage
        protected int onProcess(QueuedInputEvent q) {
            if (q.mEvent instanceof KeyEvent) {
                return processKeyEvent(q);
            }
            int source = q.mEvent.getSource();
            if ((source & 2) != 0) {
                return processPointerEvent(q);
            }
            if ((source & 4) != 0) {
                return processTrackballEvent(q);
            }
            return processGenericMotionEvent(q);
        }

        @Override // android.view.ViewRootImpl.InputStage
        protected void onDeliverToNext(QueuedInputEvent q) {
            if (ViewRootImpl.this.mUnbufferedInputDispatch && (q.mEvent instanceof MotionEvent) && ((MotionEvent) q.mEvent).isTouchEvent() && ViewRootImpl.isTerminalInputEvent(q.mEvent)) {
                ViewRootImpl.this.mUnbufferedInputDispatch = false;
                ViewRootImpl.this.scheduleConsumeBatchedInput();
            }
            super.onDeliverToNext(q);
        }

        private boolean performFocusNavigation(KeyEvent event) {
            int direction = 0;
            switch (event.getKeyCode()) {
                case 19:
                    if (event.hasNoModifiers()) {
                        direction = 33;
                        break;
                    }
                    break;
                case 20:
                    if (event.hasNoModifiers()) {
                        direction = 130;
                        break;
                    }
                    break;
                case 21:
                    if (event.hasNoModifiers()) {
                        direction = 17;
                        break;
                    }
                    break;
                case 22:
                    if (event.hasNoModifiers()) {
                        direction = 66;
                        break;
                    }
                    break;
                case 61:
                    if (event.hasNoModifiers()) {
                        direction = 2;
                        break;
                    } else if (event.hasModifiers(1)) {
                        direction = 1;
                        break;
                    }
                    break;
            }
            if (direction != 0) {
                View focused = ViewRootImpl.this.mView.findFocus();
                if (focused != null) {
                    View v = focused.focusSearch(direction);
                    if (v != null && v != focused) {
                        focused.getFocusedRect(ViewRootImpl.this.mTempRect);
                        if (ViewRootImpl.this.mView instanceof ViewGroup) {
                            ((ViewGroup) ViewRootImpl.this.mView).offsetDescendantRectToMyCoords(focused, ViewRootImpl.this.mTempRect);
                            ((ViewGroup) ViewRootImpl.this.mView).offsetRectIntoDescendantCoords(v, ViewRootImpl.this.mTempRect);
                        }
                        if (v.requestFocus(direction, ViewRootImpl.this.mTempRect)) {
                            boolean isFastScrolling = event.getRepeatCount() > 0;
                            ViewRootImpl.this.playSoundEffect(SoundEffectConstants.getConstantForFocusDirection(direction, isFastScrolling));
                            return true;
                        }
                    }
                    if (ViewRootImpl.this.mView.dispatchUnhandledMove(focused, direction)) {
                        return true;
                    }
                } else if (ViewRootImpl.this.mView.restoreDefaultFocus()) {
                    return true;
                }
            }
            return false;
        }

        private boolean performKeyboardGroupNavigation(int direction) {
            View cluster;
            View focused = ViewRootImpl.this.mView.findFocus();
            if (focused == null && ViewRootImpl.this.mView.restoreDefaultFocus()) {
                return true;
            }
            if (focused == null) {
                cluster = ViewRootImpl.this.keyboardNavigationClusterSearch(null, direction);
            } else {
                cluster = focused.keyboardNavigationClusterSearch(null, direction);
            }
            int realDirection = direction;
            realDirection = (direction == 2 || direction == 1) ? 130 : 130;
            if (cluster != null && cluster.isRootNamespace()) {
                if (!cluster.restoreFocusNotInCluster()) {
                    cluster = ViewRootImpl.this.keyboardNavigationClusterSearch(null, direction);
                } else {
                    ViewRootImpl.this.playSoundEffect(SoundEffectConstants.getContantForFocusDirection(direction));
                    return true;
                }
            }
            if (cluster != null && cluster.restoreFocusInCluster(realDirection)) {
                ViewRootImpl.this.playSoundEffect(SoundEffectConstants.getContantForFocusDirection(direction));
                return true;
            }
            return false;
        }

        private int processKeyEvent(QueuedInputEvent q) {
            KeyEvent event = (KeyEvent) q.mEvent;
            if (ViewRootImpl.this.mUnhandledKeyManager.preViewDispatch(event) || ViewRootImpl.this.mView.dispatchKeyEvent(event)) {
                return 1;
            }
            if (shouldDropInputEvent(q)) {
                return 2;
            }
            if (ViewRootImpl.this.mUnhandledKeyManager.dispatch(ViewRootImpl.this.mView, event)) {
                return 1;
            }
            int groupNavigationDirection = 0;
            if (event.getAction() == 0 && event.getKeyCode() == 61) {
                if (KeyEvent.metaStateHasModifiers(event.getMetaState(), 65536)) {
                    groupNavigationDirection = 2;
                } else if (KeyEvent.metaStateHasModifiers(event.getMetaState(), 65537)) {
                    groupNavigationDirection = 1;
                }
            }
            if (event.getAction() == 0 && !KeyEvent.metaStateHasNoModifiers(event.getMetaState()) && event.getRepeatCount() == 0 && !KeyEvent.isModifierKey(event.getKeyCode()) && groupNavigationDirection == 0) {
                if (ViewRootImpl.this.mView.dispatchKeyShortcutEvent(event)) {
                    return 1;
                }
                if (shouldDropInputEvent(q)) {
                    return 2;
                }
            }
            if (ViewRootImpl.this.mFallbackEventHandler.dispatchKeyEvent(event)) {
                return 1;
            }
            if (shouldDropInputEvent(q)) {
                return 2;
            }
            if (event.getAction() == 0) {
                return groupNavigationDirection != 0 ? performKeyboardGroupNavigation(groupNavigationDirection) ? 1 : 0 : performFocusNavigation(event) ? 1 : 0;
            }
            return 0;
        }

        private int processPointerEvent(QueuedInputEvent q) {
            MotionEvent event = (MotionEvent) q.mEvent;
            boolean handled = ViewRootImpl.this.mHandwritingInitiator.onTouchEvent(event);
            ViewRootImpl.this.mAttachInfo.mUnbufferedDispatchRequested = false;
            ViewRootImpl.this.mAttachInfo.mHandlingPointerEvent = true;
            boolean handled2 = handled || ViewRootImpl.this.mView.dispatchPointerEvent(event);
            maybeUpdatePointerIcon(event);
            ViewRootImpl.this.maybeUpdateTooltip(event);
            ViewRootImpl.this.mAttachInfo.mHandlingPointerEvent = false;
            if (ViewRootImpl.this.mAttachInfo.mUnbufferedDispatchRequested && !ViewRootImpl.this.mUnbufferedInputDispatch) {
                ViewRootImpl.this.mUnbufferedInputDispatch = true;
                if (ViewRootImpl.this.mConsumeBatchedInputScheduled) {
                    ViewRootImpl.this.scheduleConsumeBatchedInputImmediately();
                }
            }
            return handled2 ? 1 : 0;
        }

        private void maybeUpdatePointerIcon(MotionEvent event) {
            boolean z = true;
            if (event.getPointerCount() != 1) {
                return;
            }
            boolean needsStylusPointerIcon = (event.isStylusPointer() && ViewRootImpl.this.mInputManager.isStylusPointerIconEnabled()) ? false : false;
            if (needsStylusPointerIcon || event.isFromSource(8194)) {
                if (event.getActionMasked() == 9 || event.getActionMasked() == 10) {
                    ViewRootImpl.this.mPointerIconType = null;
                }
                if (event.getActionMasked() != 10 && !ViewRootImpl.this.updatePointerIcon(event) && event.getActionMasked() == 7) {
                    ViewRootImpl.this.mPointerIconType = null;
                }
            }
        }

        private int processTrackballEvent(QueuedInputEvent q) {
            MotionEvent event = (MotionEvent) q.mEvent;
            return ((!event.isFromSource(InputDevice.SOURCE_MOUSE_RELATIVE) || (ViewRootImpl.this.hasPointerCapture() && !ViewRootImpl.this.mView.dispatchCapturedPointerEvent(event))) && !ViewRootImpl.this.mView.dispatchTrackballEvent(event)) ? 0 : 1;
        }

        private int processGenericMotionEvent(QueuedInputEvent q) {
            MotionEvent event = (MotionEvent) q.mEvent;
            return ((event.isFromSource(InputDevice.SOURCE_TOUCHPAD) && ViewRootImpl.this.hasPointerCapture() && ViewRootImpl.this.mView.dispatchCapturedPointerEvent(event)) || ViewRootImpl.this.mView.dispatchGenericMotionEvent(event)) ? 1 : 0;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void resetPointerIcon(MotionEvent event) {
        this.mPointerIconType = null;
        updatePointerIcon(event);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean updatePointerIcon(MotionEvent event) {
        View view;
        int pointerType;
        float x = event.getX(0);
        float y = event.getY(0);
        if (this.mView == null) {
            Slog.m98d(this.mTag, "updatePointerIcon called after view was removed");
            return false;
        } else if (x < 0.0f || x >= view.getWidth() || y < 0.0f || y >= this.mView.getHeight()) {
            Slog.m98d(this.mTag, "updatePointerIcon called with position out of bounds");
            return false;
        } else {
            PointerIcon pointerIcon = null;
            if (event.isStylusPointer() && this.mInputManager.isStylusPointerIconEnabled()) {
                pointerIcon = this.mHandwritingInitiator.onResolvePointerIcon(this.mContext, event);
            }
            if (pointerIcon == null) {
                pointerIcon = this.mView.onResolvePointerIcon(event, 0);
            }
            if (pointerIcon == null) {
                pointerType = 1;
            } else {
                pointerType = pointerIcon.getType();
            }
            Integer num = this.mPointerIconType;
            if (num == null || num.intValue() != pointerType) {
                Integer valueOf = Integer.valueOf(pointerType);
                this.mPointerIconType = valueOf;
                this.mCustomPointerIcon = null;
                if (valueOf.intValue() != -1) {
                    this.mInputManager.setPointerIconType(pointerType);
                    return true;
                }
            }
            if (this.mPointerIconType.intValue() == -1 && !pointerIcon.equals(this.mCustomPointerIcon)) {
                this.mCustomPointerIcon = pointerIcon;
                this.mInputManager.setCustomPointerIcon(pointerIcon);
            }
            return true;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void maybeUpdateTooltip(MotionEvent event) {
        if (event.getPointerCount() != 1) {
            return;
        }
        int action = event.getActionMasked();
        if (action != 9 && action != 7 && action != 10) {
            return;
        }
        AccessibilityManager manager = AccessibilityManager.getInstance(this.mContext);
        if (manager.isEnabled() && manager.isTouchExplorationEnabled()) {
            return;
        }
        View view = this.mView;
        if (view == null) {
            Slog.m98d(this.mTag, "maybeUpdateTooltip called after view was removed");
        } else {
            view.dispatchTooltipHoverEvent(event);
        }
    }

    private View getFocusedViewOrNull() {
        View view = this.mView;
        if (view != null) {
            return view.findFocus();
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes4.dex */
    public final class SyntheticInputStage extends InputStage {
        private final SyntheticJoystickHandler mJoystick;
        private final SyntheticKeyboardHandler mKeyboard;
        private final SyntheticTouchNavigationHandler mTouchNavigation;
        private final SyntheticTrackballHandler mTrackball;

        public SyntheticInputStage() {
            super(null);
            this.mTrackball = new SyntheticTrackballHandler();
            this.mJoystick = new SyntheticJoystickHandler();
            this.mTouchNavigation = new SyntheticTouchNavigationHandler();
            this.mKeyboard = new SyntheticKeyboardHandler();
        }

        @Override // android.view.ViewRootImpl.InputStage
        protected int onProcess(QueuedInputEvent q) {
            q.mFlags |= 16;
            if (q.mEvent instanceof MotionEvent) {
                MotionEvent event = (MotionEvent) q.mEvent;
                int source = event.getSource();
                if ((source & 4) != 0) {
                    this.mTrackball.process(event);
                    return 1;
                } else if ((source & 16) != 0) {
                    this.mJoystick.process(event);
                    return 1;
                } else if ((source & 2097152) == 2097152) {
                    this.mTouchNavigation.process(event);
                    return 1;
                } else {
                    return 0;
                }
            } else if ((q.mFlags & 32) != 0) {
                this.mKeyboard.process((KeyEvent) q.mEvent);
                return 1;
            } else {
                return 0;
            }
        }

        @Override // android.view.ViewRootImpl.InputStage
        protected void onDeliverToNext(QueuedInputEvent q) {
            if ((q.mFlags & 16) == 0 && (q.mEvent instanceof MotionEvent)) {
                MotionEvent event = (MotionEvent) q.mEvent;
                int source = event.getSource();
                if ((source & 4) != 0) {
                    this.mTrackball.cancel();
                } else if ((source & 16) != 0) {
                    this.mJoystick.cancel();
                } else if ((source & 2097152) == 2097152) {
                    this.mTouchNavigation.cancel(event);
                }
            }
            super.onDeliverToNext(q);
        }

        @Override // android.view.ViewRootImpl.InputStage
        protected void onWindowFocusChanged(boolean hasWindowFocus) {
            if (!hasWindowFocus) {
                this.mJoystick.cancel();
            }
        }

        @Override // android.view.ViewRootImpl.InputStage
        protected void onDetachedFromWindow() {
            this.mJoystick.cancel();
        }
    }

    /* loaded from: classes4.dex */
    final class SyntheticTrackballHandler {
        private long mLastTime;

        /* renamed from: mX */
        private final TrackballAxis f500mX = new TrackballAxis();

        /* renamed from: mY */
        private final TrackballAxis f501mY = new TrackballAxis();

        SyntheticTrackballHandler() {
        }

        public void process(MotionEvent event) {
            long curTime;
            int keycode;
            float accel;
            long curTime2;
            long curTime3 = SystemClock.uptimeMillis();
            if (this.mLastTime + 250 < curTime3) {
                this.f500mX.reset(0);
                this.f501mY.reset(0);
                this.mLastTime = curTime3;
            }
            int action = event.getAction();
            int metaState = event.getMetaState();
            switch (action) {
                case 0:
                    curTime = curTime3;
                    this.f500mX.reset(2);
                    this.f501mY.reset(2);
                    ViewRootImpl.this.enqueueInputEvent(new KeyEvent(curTime, curTime, 0, 23, 0, metaState, -1, 0, 1024, 257));
                    break;
                case 1:
                    this.f500mX.reset(2);
                    this.f501mY.reset(2);
                    curTime = curTime3;
                    ViewRootImpl.this.enqueueInputEvent(new KeyEvent(curTime3, curTime3, 1, 23, 0, metaState, -1, 0, 1024, 257));
                    break;
                default:
                    curTime = curTime3;
                    break;
            }
            float xOff = this.f500mX.collect(event.getX(), event.getEventTime(), GnssSignalType.CODE_TYPE_X);
            float yOff = this.f501mY.collect(event.getY(), event.getEventTime(), GnssSignalType.CODE_TYPE_Y);
            int movement = 0;
            if (xOff > yOff) {
                movement = this.f500mX.generate();
                if (movement == 0) {
                    keycode = 0;
                    accel = 1.0f;
                } else {
                    int keycode2 = movement > 0 ? 22 : 21;
                    float accel2 = this.f500mX.acceleration;
                    this.f501mY.reset(2);
                    keycode = keycode2;
                    accel = accel2;
                }
            } else if (yOff <= 0.0f) {
                keycode = 0;
                accel = 1.0f;
            } else {
                movement = this.f501mY.generate();
                if (movement == 0) {
                    keycode = 0;
                    accel = 1.0f;
                } else {
                    int keycode3 = movement > 0 ? 20 : 19;
                    float accel3 = this.f501mY.acceleration;
                    this.f500mX.reset(2);
                    keycode = keycode3;
                    accel = accel3;
                }
            }
            if (keycode != 0) {
                if (movement < 0) {
                    movement = -movement;
                }
                int accelMovement = (int) (movement * accel);
                if (accelMovement > movement) {
                    int movement2 = movement - 1;
                    int repeatCount = accelMovement - movement2;
                    ViewRootImpl.this.enqueueInputEvent(new KeyEvent(curTime, curTime, 2, keycode, repeatCount, metaState, -1, 0, 1024, 257));
                    curTime2 = curTime;
                    movement = movement2;
                } else {
                    curTime2 = curTime;
                }
                while (movement > 0) {
                    long curTime4 = SystemClock.uptimeMillis();
                    int i = keycode;
                    ViewRootImpl.this.enqueueInputEvent(new KeyEvent(curTime4, curTime4, 0, i, 0, metaState, -1, 0, 1024, 257));
                    ViewRootImpl.this.enqueueInputEvent(new KeyEvent(curTime4, curTime4, 1, i, 0, metaState, -1, 0, 1024, 257));
                    movement--;
                    curTime2 = curTime4;
                    yOff = yOff;
                    xOff = xOff;
                }
                this.mLastTime = curTime2;
            }
        }

        public void cancel() {
            this.mLastTime = -2147483648L;
            if (ViewRootImpl.this.mView != null && ViewRootImpl.this.mAdded) {
                ViewRootImpl.this.ensureTouchMode(false);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes4.dex */
    public static final class TrackballAxis {
        static final float ACCEL_MOVE_SCALING_FACTOR = 0.025f;
        static final long FAST_MOVE_TIME = 150;
        static final float FIRST_MOVEMENT_THRESHOLD = 0.5f;
        static final float MAX_ACCELERATION = 20.0f;
        static final float SECOND_CUMULATIVE_MOVEMENT_THRESHOLD = 2.0f;
        static final float SUBSEQUENT_INCREMENTAL_MOVEMENT_THRESHOLD = 1.0f;
        int dir;
        int nonAccelMovement;
        float position;
        int step;
        float acceleration = 1.0f;
        long lastMoveTime = 0;

        TrackballAxis() {
        }

        void reset(int _step) {
            this.position = 0.0f;
            this.acceleration = 1.0f;
            this.lastMoveTime = 0L;
            this.step = _step;
            this.dir = 0;
        }

        float collect(float off, long time, String axis) {
            long normTime;
            if (off > 0.0f) {
                normTime = 150.0f * off;
                if (this.dir < 0) {
                    this.position = 0.0f;
                    this.step = 0;
                    this.acceleration = 1.0f;
                    this.lastMoveTime = 0L;
                }
                this.dir = 1;
            } else if (off < 0.0f) {
                normTime = (-off) * 150.0f;
                if (this.dir > 0) {
                    this.position = 0.0f;
                    this.step = 0;
                    this.acceleration = 1.0f;
                    this.lastMoveTime = 0L;
                }
                this.dir = -1;
            } else {
                normTime = 0;
            }
            if (normTime > 0) {
                long delta = time - this.lastMoveTime;
                this.lastMoveTime = time;
                float acc = this.acceleration;
                if (delta < normTime) {
                    float scale = ((float) (normTime - delta)) * ACCEL_MOVE_SCALING_FACTOR;
                    if (scale > 1.0f) {
                        acc *= scale;
                    }
                    float f = MAX_ACCELERATION;
                    if (acc < MAX_ACCELERATION) {
                        f = acc;
                    }
                    this.acceleration = f;
                } else {
                    float scale2 = ((float) (delta - normTime)) * ACCEL_MOVE_SCALING_FACTOR;
                    if (scale2 > 1.0f) {
                        acc /= scale2;
                    }
                    this.acceleration = acc > 1.0f ? acc : 1.0f;
                }
            }
            float f2 = this.position + off;
            this.position = f2;
            return Math.abs(f2);
        }

        int generate() {
            int movement = 0;
            this.nonAccelMovement = 0;
            while (true) {
                float f = this.position;
                int dir = f >= 0.0f ? 1 : -1;
                switch (this.step) {
                    case 0:
                        if (Math.abs(f) < 0.5f) {
                            return movement;
                        }
                        movement += dir;
                        this.nonAccelMovement += dir;
                        this.step = 1;
                        break;
                    case 1:
                        if (Math.abs(f) < SECOND_CUMULATIVE_MOVEMENT_THRESHOLD) {
                            return movement;
                        }
                        movement += dir;
                        this.nonAccelMovement += dir;
                        this.position -= dir * SECOND_CUMULATIVE_MOVEMENT_THRESHOLD;
                        this.step = 2;
                        break;
                    default:
                        if (Math.abs(f) < 1.0f) {
                            return movement;
                        }
                        movement += dir;
                        this.position -= dir * 1.0f;
                        float acc = this.acceleration * 1.1f;
                        this.acceleration = acc < MAX_ACCELERATION ? acc : this.acceleration;
                        break;
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes4.dex */
    public final class SyntheticJoystickHandler extends Handler {
        private static final int MSG_ENQUEUE_X_AXIS_KEY_REPEAT = 1;
        private static final int MSG_ENQUEUE_Y_AXIS_KEY_REPEAT = 2;
        private final SparseArray<KeyEvent> mDeviceKeyEvents;
        private final JoystickAxesState mJoystickAxesState;

        public SyntheticJoystickHandler() {
            super(true);
            this.mJoystickAxesState = new JoystickAxesState();
            this.mDeviceKeyEvents = new SparseArray<>();
        }

        @Override // android.p008os.Handler
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                case 2:
                    if (ViewRootImpl.this.mAttachInfo.mHasWindowFocus) {
                        KeyEvent oldEvent = (KeyEvent) msg.obj;
                        KeyEvent e = KeyEvent.changeTimeRepeat(oldEvent, SystemClock.uptimeMillis(), oldEvent.getRepeatCount() + 1);
                        ViewRootImpl.this.enqueueInputEvent(e);
                        Message m = obtainMessage(msg.what, e);
                        m.setAsynchronous(true);
                        sendMessageDelayed(m, ViewConfiguration.getKeyRepeatDelay());
                        return;
                    }
                    return;
                default:
                    return;
            }
        }

        public void process(MotionEvent event) {
            switch (event.getActionMasked()) {
                case 2:
                    update(event);
                    return;
                case 3:
                    cancel();
                    return;
                default:
                    Log.m104w(ViewRootImpl.this.mTag, "Unexpected action: " + event.getActionMasked());
                    return;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void cancel() {
            removeMessages(1);
            removeMessages(2);
            for (int i = 0; i < this.mDeviceKeyEvents.size(); i++) {
                KeyEvent keyEvent = this.mDeviceKeyEvents.valueAt(i);
                if (keyEvent != null) {
                    ViewRootImpl.this.enqueueInputEvent(KeyEvent.changeTimeRepeat(keyEvent, SystemClock.uptimeMillis(), 0));
                }
            }
            this.mDeviceKeyEvents.clear();
            this.mJoystickAxesState.resetState();
        }

        private void update(MotionEvent event) {
            int historySize = event.getHistorySize();
            for (int h = 0; h < historySize; h++) {
                long time = event.getHistoricalEventTime(h);
                this.mJoystickAxesState.updateStateForAxis(event, time, 0, event.getHistoricalAxisValue(0, 0, h));
                this.mJoystickAxesState.updateStateForAxis(event, time, 1, event.getHistoricalAxisValue(1, 0, h));
                this.mJoystickAxesState.updateStateForAxis(event, time, 15, event.getHistoricalAxisValue(15, 0, h));
                this.mJoystickAxesState.updateStateForAxis(event, time, 16, event.getHistoricalAxisValue(16, 0, h));
            }
            long time2 = event.getEventTime();
            this.mJoystickAxesState.updateStateForAxis(event, time2, 0, event.getAxisValue(0));
            this.mJoystickAxesState.updateStateForAxis(event, time2, 1, event.getAxisValue(1));
            this.mJoystickAxesState.updateStateForAxis(event, time2, 15, event.getAxisValue(15));
            this.mJoystickAxesState.updateStateForAxis(event, time2, 16, event.getAxisValue(16));
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* loaded from: classes4.dex */
        public final class JoystickAxesState {
            private static final int STATE_DOWN_OR_RIGHT = 1;
            private static final int STATE_NEUTRAL = 0;
            private static final int STATE_UP_OR_LEFT = -1;
            final int[] mAxisStatesHat = {0, 0};
            final int[] mAxisStatesStick = {0, 0};

            JoystickAxesState() {
            }

            void resetState() {
                int[] iArr = this.mAxisStatesHat;
                iArr[0] = 0;
                iArr[1] = 0;
                int[] iArr2 = this.mAxisStatesStick;
                iArr2[0] = 0;
                iArr2[1] = 0;
            }

            void updateStateForAxis(MotionEvent event, long time, int axis, float value) {
                int axisStateIndex;
                int repeatMessage;
                int currentState;
                int keyCode;
                if (isXAxis(axis)) {
                    axisStateIndex = 0;
                    repeatMessage = 1;
                } else if (!isYAxis(axis)) {
                    Log.m110e(ViewRootImpl.this.mTag, "Unexpected axis " + axis + " in updateStateForAxis!");
                    return;
                } else {
                    axisStateIndex = 1;
                    repeatMessage = 2;
                }
                int newState = joystickAxisValueToState(value);
                if (axis == 0 || axis == 1) {
                    currentState = this.mAxisStatesStick[axisStateIndex];
                } else {
                    currentState = this.mAxisStatesHat[axisStateIndex];
                }
                if (currentState == newState) {
                    return;
                }
                int metaState = event.getMetaState();
                int deviceId = event.getDeviceId();
                int source = event.getSource();
                if (currentState == 1 || currentState == -1) {
                    int keyCode2 = joystickAxisAndStateToKeycode(axis, currentState);
                    if (keyCode2 != 0) {
                        ViewRootImpl.this.enqueueInputEvent(new KeyEvent(time, time, 1, keyCode2, 0, metaState, deviceId, 0, 1024, source));
                        deviceId = deviceId;
                        SyntheticJoystickHandler.this.mDeviceKeyEvents.put(deviceId, null);
                    }
                    SyntheticJoystickHandler.this.removeMessages(repeatMessage);
                }
                if ((newState == 1 || newState == -1) && (keyCode = joystickAxisAndStateToKeycode(axis, newState)) != 0) {
                    int deviceId2 = deviceId;
                    KeyEvent keyEvent = new KeyEvent(time, time, 0, keyCode, 0, metaState, deviceId2, 0, 1024, source);
                    ViewRootImpl.this.enqueueInputEvent(keyEvent);
                    Message m = SyntheticJoystickHandler.this.obtainMessage(repeatMessage, keyEvent);
                    m.setAsynchronous(true);
                    SyntheticJoystickHandler.this.sendMessageDelayed(m, ViewConfiguration.getKeyRepeatTimeout());
                    SyntheticJoystickHandler.this.mDeviceKeyEvents.put(deviceId2, new KeyEvent(time, time, 1, keyCode, 0, metaState, deviceId2, 0, 1056, source));
                }
                if (axis == 0 || axis == 1) {
                    this.mAxisStatesStick[axisStateIndex] = newState;
                } else {
                    this.mAxisStatesHat[axisStateIndex] = newState;
                }
            }

            private boolean isXAxis(int axis) {
                return axis == 0 || axis == 15;
            }

            private boolean isYAxis(int axis) {
                return axis == 1 || axis == 16;
            }

            private int joystickAxisAndStateToKeycode(int axis, int state) {
                if (isXAxis(axis) && state == -1) {
                    return 21;
                }
                if (isXAxis(axis) && state == 1) {
                    return 22;
                }
                if (isYAxis(axis) && state == -1) {
                    return 19;
                }
                if (isYAxis(axis) && state == 1) {
                    return 20;
                }
                Log.m110e(ViewRootImpl.this.mTag, "Unknown axis " + axis + " or direction " + state);
                return 0;
            }

            private int joystickAxisValueToState(float value) {
                if (value >= 0.5f) {
                    return 1;
                }
                if (value <= -0.5f) {
                    return -1;
                }
                return 0;
            }
        }
    }

    /* loaded from: classes4.dex */
    final class SyntheticTouchNavigationHandler extends Handler {
        private static final float DEFAULT_HEIGHT_MILLIMETERS = 48.0f;
        private static final float DEFAULT_WIDTH_MILLIMETERS = 48.0f;
        private static final float FLING_TICK_DECAY = 0.8f;
        private static final boolean LOCAL_DEBUG = false;
        private static final String LOCAL_TAG = "SyntheticTouchNavigationHandler";
        private static final float MAX_FLING_VELOCITY_TICKS_PER_SECOND = 20.0f;
        private static final float MIN_FLING_VELOCITY_TICKS_PER_SECOND = 6.0f;
        private static final int TICK_DISTANCE_MILLIMETERS = 12;
        private float mAccumulatedX;
        private float mAccumulatedY;
        private int mActivePointerId;
        private float mConfigMaxFlingVelocity;
        private float mConfigMinFlingVelocity;
        private float mConfigTickDistance;
        private boolean mConsumedMovement;
        private int mCurrentDeviceId;
        private boolean mCurrentDeviceSupported;
        private int mCurrentSource;
        private final Runnable mFlingRunnable;
        private float mFlingVelocity;
        private boolean mFlinging;
        private float mLastX;
        private float mLastY;
        private int mPendingKeyCode;
        private long mPendingKeyDownTime;
        private int mPendingKeyMetaState;
        private int mPendingKeyRepeatCount;
        private float mStartX;
        private float mStartY;
        private VelocityTracker mVelocityTracker;

        public SyntheticTouchNavigationHandler() {
            super(true);
            this.mCurrentDeviceId = -1;
            this.mActivePointerId = -1;
            this.mPendingKeyCode = 0;
            this.mFlingRunnable = new Runnable() { // from class: android.view.ViewRootImpl.SyntheticTouchNavigationHandler.1
                @Override // java.lang.Runnable
                public void run() {
                    long time = SystemClock.uptimeMillis();
                    SyntheticTouchNavigationHandler syntheticTouchNavigationHandler = SyntheticTouchNavigationHandler.this;
                    syntheticTouchNavigationHandler.sendKeyDownOrRepeat(time, syntheticTouchNavigationHandler.mPendingKeyCode, SyntheticTouchNavigationHandler.this.mPendingKeyMetaState);
                    SyntheticTouchNavigationHandler.this.mFlingVelocity *= 0.8f;
                    if (!SyntheticTouchNavigationHandler.this.postFling(time)) {
                        SyntheticTouchNavigationHandler.this.mFlinging = false;
                        SyntheticTouchNavigationHandler.this.finishKeys(time);
                    }
                }
            };
        }

        public void process(MotionEvent event) {
            long time = event.getEventTime();
            int deviceId = event.getDeviceId();
            int source = event.getSource();
            if (this.mCurrentDeviceId != deviceId || this.mCurrentSource != source) {
                finishKeys(time);
                finishTracking(time);
                this.mCurrentDeviceId = deviceId;
                this.mCurrentSource = source;
                this.mCurrentDeviceSupported = false;
                InputDevice device = event.getDevice();
                if (device != null) {
                    InputDevice.MotionRange xRange = device.getMotionRange(0);
                    InputDevice.MotionRange yRange = device.getMotionRange(1);
                    if (xRange != null && yRange != null) {
                        this.mCurrentDeviceSupported = true;
                        float xRes = xRange.getResolution();
                        if (xRes <= 0.0f) {
                            xRes = xRange.getRange() / 48.0f;
                        }
                        float yRes = yRange.getResolution();
                        if (yRes <= 0.0f) {
                            yRes = yRange.getRange() / 48.0f;
                        }
                        float nominalRes = (xRes + yRes) * 0.5f;
                        float f = 12.0f * nominalRes;
                        this.mConfigTickDistance = f;
                        this.mConfigMinFlingVelocity = f * MIN_FLING_VELOCITY_TICKS_PER_SECOND;
                        this.mConfigMaxFlingVelocity = f * MAX_FLING_VELOCITY_TICKS_PER_SECOND;
                    }
                }
            }
            if (!this.mCurrentDeviceSupported) {
                return;
            }
            int action = event.getActionMasked();
            switch (action) {
                case 0:
                    boolean caughtFling = this.mFlinging;
                    finishKeys(time);
                    finishTracking(time);
                    this.mActivePointerId = event.getPointerId(0);
                    VelocityTracker obtain = VelocityTracker.obtain();
                    this.mVelocityTracker = obtain;
                    obtain.addMovement(event);
                    this.mStartX = event.getX();
                    float y = event.getY();
                    this.mStartY = y;
                    this.mLastX = this.mStartX;
                    this.mLastY = y;
                    this.mAccumulatedX = 0.0f;
                    this.mAccumulatedY = 0.0f;
                    this.mConsumedMovement = caughtFling;
                    return;
                case 1:
                case 2:
                    int i = this.mActivePointerId;
                    if (i >= 0) {
                        int index = event.findPointerIndex(i);
                        if (index < 0) {
                            finishKeys(time);
                            finishTracking(time);
                            return;
                        }
                        this.mVelocityTracker.addMovement(event);
                        float x = event.getX(index);
                        float y2 = event.getY(index);
                        this.mAccumulatedX += x - this.mLastX;
                        this.mAccumulatedY += y2 - this.mLastY;
                        this.mLastX = x;
                        this.mLastY = y2;
                        int metaState = event.getMetaState();
                        consumeAccumulatedMovement(time, metaState);
                        if (action == 1) {
                            if (this.mConsumedMovement && this.mPendingKeyCode != 0) {
                                this.mVelocityTracker.computeCurrentVelocity(1000, this.mConfigMaxFlingVelocity);
                                float vx = this.mVelocityTracker.getXVelocity(this.mActivePointerId);
                                float vy = this.mVelocityTracker.getYVelocity(this.mActivePointerId);
                                if (!startFling(time, vx, vy)) {
                                    finishKeys(time);
                                }
                            }
                            finishTracking(time);
                            return;
                        }
                        return;
                    }
                    return;
                case 3:
                    finishKeys(time);
                    finishTracking(time);
                    return;
                default:
                    return;
            }
        }

        public void cancel(MotionEvent event) {
            if (this.mCurrentDeviceId == event.getDeviceId() && this.mCurrentSource == event.getSource()) {
                long time = event.getEventTime();
                finishKeys(time);
                finishTracking(time);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void finishKeys(long time) {
            cancelFling();
            sendKeyUp(time);
        }

        private void finishTracking(long time) {
            if (this.mActivePointerId >= 0) {
                this.mActivePointerId = -1;
                this.mVelocityTracker.recycle();
                this.mVelocityTracker = null;
            }
        }

        private void consumeAccumulatedMovement(long time, int metaState) {
            float absX = Math.abs(this.mAccumulatedX);
            float absY = Math.abs(this.mAccumulatedY);
            if (absX >= absY) {
                if (absX >= this.mConfigTickDistance) {
                    this.mAccumulatedX = consumeAccumulatedMovement(time, metaState, this.mAccumulatedX, 21, 22);
                    this.mAccumulatedY = 0.0f;
                    this.mConsumedMovement = true;
                }
            } else if (absY >= this.mConfigTickDistance) {
                this.mAccumulatedY = consumeAccumulatedMovement(time, metaState, this.mAccumulatedY, 19, 20);
                this.mAccumulatedX = 0.0f;
                this.mConsumedMovement = true;
            }
        }

        private float consumeAccumulatedMovement(long time, int metaState, float accumulator, int negativeKeyCode, int positiveKeyCode) {
            while (accumulator <= (-this.mConfigTickDistance)) {
                sendKeyDownOrRepeat(time, negativeKeyCode, metaState);
                accumulator += this.mConfigTickDistance;
            }
            while (accumulator >= this.mConfigTickDistance) {
                sendKeyDownOrRepeat(time, positiveKeyCode, metaState);
                accumulator -= this.mConfigTickDistance;
            }
            return accumulator;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void sendKeyDownOrRepeat(long time, int keyCode, int metaState) {
            if (this.mPendingKeyCode == keyCode) {
                this.mPendingKeyRepeatCount++;
            } else {
                sendKeyUp(time);
                this.mPendingKeyDownTime = time;
                this.mPendingKeyCode = keyCode;
                this.mPendingKeyRepeatCount = 0;
            }
            this.mPendingKeyMetaState = metaState;
            ViewRootImpl.this.enqueueInputEvent(new KeyEvent(this.mPendingKeyDownTime, time, 0, this.mPendingKeyCode, this.mPendingKeyRepeatCount, this.mPendingKeyMetaState, this.mCurrentDeviceId, 1024, this.mCurrentSource));
        }

        private void sendKeyUp(long time) {
            if (this.mPendingKeyCode != 0) {
                ViewRootImpl.this.enqueueInputEvent(new KeyEvent(this.mPendingKeyDownTime, time, 1, this.mPendingKeyCode, 0, this.mPendingKeyMetaState, this.mCurrentDeviceId, 0, 1024, this.mCurrentSource));
                this.mPendingKeyCode = 0;
            }
        }

        private boolean startFling(long time, float vx, float vy) {
            switch (this.mPendingKeyCode) {
                case 19:
                    if ((-vy) >= this.mConfigMinFlingVelocity && Math.abs(vx) < this.mConfigMinFlingVelocity) {
                        this.mFlingVelocity = -vy;
                        break;
                    } else {
                        return false;
                    }
                case 20:
                    if (vy >= this.mConfigMinFlingVelocity && Math.abs(vx) < this.mConfigMinFlingVelocity) {
                        this.mFlingVelocity = vy;
                        break;
                    } else {
                        return false;
                    }
                case 21:
                    if ((-vx) >= this.mConfigMinFlingVelocity && Math.abs(vy) < this.mConfigMinFlingVelocity) {
                        this.mFlingVelocity = -vx;
                        break;
                    } else {
                        return false;
                    }
                    break;
                case 22:
                    if (vx >= this.mConfigMinFlingVelocity && Math.abs(vy) < this.mConfigMinFlingVelocity) {
                        this.mFlingVelocity = vx;
                        break;
                    } else {
                        return false;
                    }
            }
            boolean postFling = postFling(time);
            this.mFlinging = postFling;
            return postFling;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public boolean postFling(long time) {
            float f = this.mFlingVelocity;
            if (f >= this.mConfigMinFlingVelocity) {
                long delay = (this.mConfigTickDistance / f) * 1000.0f;
                postAtTime(this.mFlingRunnable, time + delay);
                return true;
            }
            return false;
        }

        private void cancelFling() {
            if (this.mFlinging) {
                removeCallbacks(this.mFlingRunnable);
                this.mFlinging = false;
            }
        }
    }

    /* loaded from: classes4.dex */
    final class SyntheticKeyboardHandler {
        SyntheticKeyboardHandler() {
        }

        public void process(KeyEvent event) {
            if ((event.getFlags() & 1024) != 0) {
                return;
            }
            KeyCharacterMap kcm = event.getKeyCharacterMap();
            int keyCode = event.getKeyCode();
            int metaState = event.getMetaState();
            KeyCharacterMap.FallbackAction fallbackAction = kcm.getFallbackAction(keyCode, metaState);
            if (fallbackAction != null) {
                int flags = event.getFlags() | 1024;
                KeyEvent fallbackEvent = KeyEvent.obtain(event.getDownTime(), event.getEventTime(), event.getAction(), fallbackAction.keyCode, event.getRepeatCount(), fallbackAction.metaState, event.getDeviceId(), event.getScanCode(), flags, event.getSource(), null);
                fallbackAction.recycle();
                ViewRootImpl.this.enqueueInputEvent(fallbackEvent);
            }
        }
    }

    private static boolean isNavigationKey(KeyEvent keyEvent) {
        switch (keyEvent.getKeyCode()) {
            case 19:
            case 20:
            case 21:
            case 22:
            case 23:
            case 61:
            case 62:
            case 66:
            case 92:
            case 93:
            case 122:
            case 123:
                return true;
            default:
                return false;
        }
    }

    private static boolean isTypingKey(KeyEvent keyEvent) {
        return keyEvent.getUnicodeChar() > 0;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean checkForLeavingTouchModeAndConsume(KeyEvent event) {
        if (this.mAttachInfo.mInTouchMode) {
            int action = event.getAction();
            if ((action == 0 || action == 2) && (event.getFlags() & 4) == 0) {
                if (event.hasNoModifiers() && isNavigationKey(event)) {
                    return ensureTouchMode(false);
                }
                if (isTypingKey(event)) {
                    ensureTouchMode(false);
                    return false;
                }
                return false;
            }
            return false;
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setLocalDragState(Object obj) {
        this.mLocalDragState = obj;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleDragEvent(DragEvent event) {
        if (this.mView != null && this.mAdded) {
            int what = event.mAction;
            if (what == 1) {
                this.mCurrentDragView = null;
                this.mDragDescription = event.mClipDescription;
                View view = this.mStartedDragViewForA11y;
                if (view != null) {
                    view.sendWindowContentChangedAccessibilityEvent(128);
                }
            } else {
                if (what == 4) {
                    this.mDragDescription = null;
                }
                event.mClipDescription = this.mDragDescription;
            }
            if (what == 6) {
                if (View.sCascadedDragDrop) {
                    this.mView.dispatchDragEnterExitInPreN(event);
                }
                setDragFocus(null, event);
            } else {
                if (what == 2 || what == 3) {
                    this.mDragPoint.set(event.f479mX, event.f480mY);
                    CompatibilityInfo.Translator translator = this.mTranslator;
                    if (translator != null) {
                        translator.translatePointInScreenToAppWindow(this.mDragPoint);
                    }
                    int i = this.mCurScrollY;
                    if (i != 0) {
                        this.mDragPoint.offset(0.0f, i);
                    }
                    event.f479mX = this.mDragPoint.f78x;
                    event.f480mY = this.mDragPoint.f79y;
                }
                View prevDragView = this.mCurrentDragView;
                if (what == 3 && event.mClipData != null) {
                    event.mClipData.prepareToEnterProcess(this.mView.getContext().getAttributionSource());
                }
                boolean result = this.mView.dispatchDragEvent(event);
                if (what == 2 && !event.mEventHandlerWasCalled) {
                    setDragFocus(null, event);
                }
                if (prevDragView != this.mCurrentDragView) {
                    if (prevDragView != null) {
                        try {
                            this.mWindowSession.dragRecipientExited(this.mWindow);
                        } catch (RemoteException e) {
                            Slog.m96e(this.mTag, "Unable to note drag target change");
                        }
                    }
                    if (this.mCurrentDragView != null) {
                        this.mWindowSession.dragRecipientEntered(this.mWindow);
                    }
                }
                if (what == 3) {
                    try {
                        Log.m108i(this.mTag, "Reporting drop result: " + result);
                        this.mWindowSession.reportDropResult(this.mWindow, result);
                    } catch (RemoteException e2) {
                        Log.m110e(this.mTag, "Unable to report drop result");
                    }
                }
                if (what == 4) {
                    if (this.mStartedDragViewForA11y != null) {
                        if (!event.getResult()) {
                            this.mStartedDragViewForA11y.sendWindowContentChangedAccessibilityEvent(512);
                        }
                        this.mStartedDragViewForA11y.setAccessibilityDragStarted(false);
                    }
                    this.mStartedDragViewForA11y = null;
                    this.mCurrentDragView = null;
                    setLocalDragState(null);
                    this.mAttachInfo.mDragToken = null;
                    if (this.mAttachInfo.mDragSurface != null) {
                        this.mAttachInfo.mDragSurface.release();
                        this.mAttachInfo.mDragSurface = null;
                    }
                }
            }
        }
        event.recycle();
    }

    public void onWindowTitleChanged() {
        this.mAttachInfo.mForceReportNewAttributes = true;
    }

    public void handleDispatchWindowShown() {
        this.mAttachInfo.mTreeObserver.dispatchOnWindowShown();
    }

    public void handleRequestKeyboardShortcuts(IResultReceiver receiver, int deviceId) {
        Bundle data = new Bundle();
        ArrayList<KeyboardShortcutGroup> list = new ArrayList<>();
        View view = this.mView;
        if (view != null) {
            view.requestKeyboardShortcuts(list, deviceId);
        }
        data.putParcelableArrayList(WindowManager.PARCEL_KEY_SHORTCUTS_ARRAY, list);
        try {
            receiver.send(0, data);
        } catch (RemoteException e) {
        }
    }

    public void getLastTouchPoint(Point outLocation) {
        outLocation.f76x = (int) this.mLastTouchPoint.f78x;
        outLocation.f77y = (int) this.mLastTouchPoint.f79y;
    }

    public int getLastTouchSource() {
        return this.mLastTouchSource;
    }

    public int getLastClickToolType() {
        return this.mLastClickToolType;
    }

    public void setDragFocus(View newDragTarget, DragEvent event) {
        if (this.mCurrentDragView != newDragTarget && !View.sCascadedDragDrop) {
            float tx = event.f479mX;
            float ty = event.f480mY;
            int action = event.mAction;
            ClipData td = event.mClipData;
            event.f479mX = 0.0f;
            event.f480mY = 0.0f;
            event.mClipData = null;
            if (this.mCurrentDragView != null) {
                event.mAction = 6;
                this.mCurrentDragView.callDragEventHandler(event);
            }
            if (newDragTarget != null) {
                event.mAction = 5;
                newDragTarget.callDragEventHandler(event);
            }
            event.mAction = action;
            event.f479mX = tx;
            event.f480mY = ty;
            event.mClipData = td;
        }
        this.mCurrentDragView = newDragTarget;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setDragStartedViewForAccessibility(View view) {
        if (this.mStartedDragViewForA11y == null) {
            this.mStartedDragViewForA11y = view;
        }
    }

    private AudioManager getAudioManager() {
        View view = this.mView;
        if (view == null) {
            throw new IllegalStateException("getAudioManager called when there is no mView");
        }
        if (this.mAudioManager == null) {
            this.mAudioManager = (AudioManager) view.getContext().getSystemService("audio");
        }
        return this.mAudioManager;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public AutofillManager getAutofillManager() {
        View view = this.mView;
        if (view instanceof ViewGroup) {
            ViewGroup decorView = (ViewGroup) view;
            if (decorView.getChildCount() > 0) {
                return (AutofillManager) decorView.getChildAt(0).getContext().getSystemService(AutofillManager.class);
            }
            return null;
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isAutofillUiShowing() {
        AutofillManager afm = getAutofillManager();
        if (afm == null) {
            return false;
        }
        return afm.isAutofillUiShowing();
    }

    public AccessibilityInteractionController getAccessibilityInteractionController() {
        if (this.mView == null) {
            throw new IllegalStateException("getAccessibilityInteractionController called when there is no mView");
        }
        if (this.mAccessibilityInteractionController == null) {
            this.mAccessibilityInteractionController = new AccessibilityInteractionController(this);
        }
        return this.mAccessibilityInteractionController;
    }

    /* JADX WARN: Removed duplicated region for block: B:105:0x02f0  */
    /* JADX WARN: Removed duplicated region for block: B:112:0x030e  */
    /* JADX WARN: Removed duplicated region for block: B:53:0x0136  */
    /* JADX WARN: Removed duplicated region for block: B:54:0x015b  */
    /* JADX WARN: Removed duplicated region for block: B:67:0x0235  */
    /* JADX WARN: Removed duplicated region for block: B:68:0x0237  */
    /* JADX WARN: Removed duplicated region for block: B:71:0x0247  */
    /* JADX WARN: Removed duplicated region for block: B:72:0x0249  */
    /* JADX WARN: Removed duplicated region for block: B:75:0x0251  */
    /* JADX WARN: Removed duplicated region for block: B:84:0x0271  */
    /* JADX WARN: Removed duplicated region for block: B:92:0x02a0  */
    /* JADX WARN: Removed duplicated region for block: B:95:0x02c1  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    private int relayoutWindow(WindowManager.LayoutParams params, int viewVisibility, boolean insetsPending) throws RemoteException {
        int measuredHeight;
        boolean relayoutAsync;
        boolean restore;
        int requestedHeight;
        int requestedWidth;
        boolean z;
        int i;
        WindowConfiguration winConfigFromAm = getConfiguration().windowConfiguration;
        WindowConfiguration winConfigFromWm = this.mLastReportedMergedConfiguration.getGlobalConfiguration().windowConfiguration;
        WindowConfiguration winConfig = getCompatWindowConfiguration();
        int measuredWidth = this.mMeasuredWidth;
        int measuredHeight2 = this.mMeasuredHeight;
        if (!LOCAL_LAYOUT || (this.mViewFrameInfo.flags & 1) != 0 || this.mWindowAttributes.type == 3 || this.mSyncSeqId > this.mLastSyncSeqId) {
            measuredHeight = measuredHeight2;
        } else if (winConfigFromAm.diff(winConfigFromWm, false) == 0) {
            InsetsState state = this.mInsetsController.getState();
            Rect displayCutoutSafe = this.mTempRect;
            state.getDisplayCutoutSafe(displayCutoutSafe);
            measuredHeight = measuredHeight2;
            this.mWindowLayout.computeFrames(this.mWindowAttributes.forRotation(winConfig.getRotation()), state, displayCutoutSafe, winConfig.getBounds(), winConfig.getWindowingMode(), measuredWidth, measuredHeight2, this.mInsetsController.getRequestedVisibleTypes(), 1.0f, this.mTmpFrames);
            this.mWinFrameInScreen.set(this.mTmpFrames.frame);
            CompatibilityInfo.Translator translator = this.mTranslator;
            if (translator != null) {
                translator.translateRectInAppWindowToScreen(this.mWinFrameInScreen);
            }
            Rect oldFrame = this.mLastLayoutFrame;
            Rect newFrame = this.mTmpFrames.frame;
            boolean positionChanged = (newFrame.top == oldFrame.top && newFrame.left == oldFrame.left) ? false : true;
            boolean sizeChanged = (newFrame.width() == oldFrame.width() && newFrame.height() == oldFrame.height()) ? false : true;
            boolean relayoutAsync2 = (positionChanged && sizeChanged) ? false : true;
            relayoutAsync = relayoutAsync2;
            float appScale = this.mAttachInfo.mApplicationScale;
            if (params == null && this.mTranslator != null) {
                params.backup();
                this.mTranslator.translateWindowLayout(params);
                restore = true;
            } else {
                restore = false;
            }
            if (params != null && this.mOrigWindowType != params.type && this.mTargetSdkVersion < 14) {
                Slog.m90w(this.mTag, "Window type can not be changed after the window is added; ignoring change of " + this.mView);
                params.type = this.mOrigWindowType;
            }
            int requestedWidth2 = (int) ((measuredWidth * appScale) + 0.5f);
            int requestedHeight2 = (int) ((measuredHeight * appScale) + 0.5f);
            int relayoutResult = 0;
            int i2 = this.mRelayoutSeq + 1;
            this.mRelayoutSeq = i2;
            if (!relayoutAsync) {
                requestedHeight = requestedHeight2;
                this.mWindowSession.relayoutAsync(this.mWindow, params, requestedWidth2, requestedHeight2, viewVisibility, insetsPending ? 1 : 0, i2, this.mLastSyncSeqId);
                requestedWidth = requestedWidth2;
                z = true;
            } else {
                requestedHeight = requestedHeight2;
                requestedWidth = requestedWidth2;
                relayoutResult = this.mWindowSession.relayout(this.mWindow, params, requestedWidth2, requestedHeight, viewVisibility, insetsPending ? 1 : 0, i2, this.mLastSyncSeqId, this.mTmpFrames, this.mPendingMergedConfiguration, this.mSurfaceControl, this.mTempInsets, this.mTempControls, this.mRelayoutBundle);
                z = true;
                this.mRelayoutRequested = true;
                int maybeSyncSeqId = this.mRelayoutBundle.getInt("seqid");
                if (maybeSyncSeqId > 0) {
                    this.mSyncSeqId = maybeSyncSeqId;
                }
                this.mWinFrameInScreen.set(this.mTmpFrames.frame);
                CompatibilityInfo.Translator translator2 = this.mTranslator;
                if (translator2 != null) {
                    translator2.translateRectInScreenToAppWindow(this.mTmpFrames.frame);
                    this.mTranslator.translateRectInScreenToAppWindow(this.mTmpFrames.displayFrame);
                    this.mTranslator.translateRectInScreenToAppWindow(this.mTmpFrames.attachedFrame);
                    this.mTranslator.translateInsetsStateInScreenToAppWindow(this.mTempInsets);
                    this.mTranslator.translateSourceControlsInScreenToAppWindow(this.mTempControls.get());
                }
                this.mInvCompatScale = 1.0f / this.mTmpFrames.compatScale;
                CompatibilityInfo.applyOverrideScaleIfNeeded(this.mPendingMergedConfiguration);
                this.mInsetsController.onStateChanged(this.mTempInsets);
                this.mInsetsController.onControlsChanged(this.mTempControls.get());
                this.mPendingAlwaysConsumeSystemBars = (relayoutResult & 8) != 0;
            }
            int transformHint = SurfaceControl.rotationToBufferTransform((this.mDisplay.getInstallOrientation() + this.mDisplay.getRotation()) % 4);
            WindowLayout.computeSurfaceSize(this.mWindowAttributes, winConfig.getMaxBounds(), requestedWidth, requestedHeight, this.mWinFrameInScreen, this.mPendingDragResizing, this.mSurfaceSize);
            boolean transformHintChanged = transformHint == this.mLastTransformHint ? z : false;
            boolean sizeChanged2 = this.mLastSurfaceSize.equals(this.mSurfaceSize) ^ z;
            boolean surfaceControlChanged = (relayoutResult & 2) != 2 ? z : false;
            if (this.mAttachInfo.mThreadedRenderer != null) {
                i = 0;
            } else if (!transformHintChanged && !sizeChanged2 && !surfaceControlChanged) {
                i = 0;
            } else if (this.mAttachInfo.mThreadedRenderer.pause()) {
                i = 0;
                this.mDirty.set(0, 0, this.mWidth, this.mHeight);
            } else {
                i = 0;
            }
            if (this.mSurfaceControl.isValid() && !HardwareRenderer.isDrawingEnabled()) {
                this.mTransaction.setWindowCrop(this.mSurfaceControl, this.mSurfaceSize.f76x, this.mSurfaceSize.f77y).apply();
            }
            this.mLastTransformHint = transformHint;
            this.mSurfaceControl.setTransformHint(transformHint);
            if (this.mAttachInfo.mContentCaptureManager != null) {
                MainContentCaptureSession mainSession = this.mAttachInfo.mContentCaptureManager.getMainContentCaptureSession();
                mainSession.notifyWindowBoundsChanged(mainSession.getId(), getConfiguration().windowConfiguration.getBounds());
            }
            if (!this.mSurfaceControl.isValid()) {
                if (!useBLAST()) {
                    this.mSurface.copyFrom(this.mSurfaceControl);
                } else {
                    updateBlastSurfaceIfNeeded();
                }
                if (this.mAttachInfo.mThreadedRenderer != null) {
                    this.mAttachInfo.mThreadedRenderer.setSurfaceControl(this.mSurfaceControl, this.mBlastBufferQueue);
                }
                updateRenderHdrSdrRatio();
                if (this.mPreviousTransformHint != transformHint) {
                    this.mPreviousTransformHint = transformHint;
                    dispatchTransformHintChanged(transformHint);
                }
            } else {
                if (this.mAttachInfo.mThreadedRenderer != null && this.mAttachInfo.mThreadedRenderer.pause()) {
                    this.mDirty.set(i, i, this.mWidth, this.mHeight);
                }
                destroySurface();
            }
            if (restore) {
                params.restore();
            }
            setFrame(this.mTmpFrames.frame, z);
            return relayoutResult;
        } else {
            measuredHeight = measuredHeight2;
        }
        relayoutAsync = false;
        float appScale2 = this.mAttachInfo.mApplicationScale;
        if (params == null) {
        }
        restore = false;
        if (params != null) {
            Slog.m90w(this.mTag, "Window type can not be changed after the window is added; ignoring change of " + this.mView);
            params.type = this.mOrigWindowType;
        }
        int requestedWidth22 = (int) ((measuredWidth * appScale2) + 0.5f);
        int requestedHeight22 = (int) ((measuredHeight * appScale2) + 0.5f);
        int relayoutResult2 = 0;
        int i22 = this.mRelayoutSeq + 1;
        this.mRelayoutSeq = i22;
        if (!relayoutAsync) {
        }
        int transformHint2 = SurfaceControl.rotationToBufferTransform((this.mDisplay.getInstallOrientation() + this.mDisplay.getRotation()) % 4);
        WindowLayout.computeSurfaceSize(this.mWindowAttributes, winConfig.getMaxBounds(), requestedWidth, requestedHeight, this.mWinFrameInScreen, this.mPendingDragResizing, this.mSurfaceSize);
        boolean transformHintChanged2 = transformHint2 == this.mLastTransformHint ? z : false;
        boolean sizeChanged22 = this.mLastSurfaceSize.equals(this.mSurfaceSize) ^ z;
        boolean surfaceControlChanged2 = (relayoutResult2 & 2) != 2 ? z : false;
        if (this.mAttachInfo.mThreadedRenderer != null) {
        }
        if (this.mSurfaceControl.isValid()) {
            this.mTransaction.setWindowCrop(this.mSurfaceControl, this.mSurfaceSize.f76x, this.mSurfaceSize.f77y).apply();
        }
        this.mLastTransformHint = transformHint2;
        this.mSurfaceControl.setTransformHint(transformHint2);
        if (this.mAttachInfo.mContentCaptureManager != null) {
        }
        if (!this.mSurfaceControl.isValid()) {
        }
        if (restore) {
        }
        setFrame(this.mTmpFrames.frame, z);
        return relayoutResult2;
    }

    private void updateOpacity(WindowManager.LayoutParams params, boolean dragResizing, boolean forceUpdate) {
        boolean opaque = false;
        if (!PixelFormat.formatHasAlpha(params.format) && params.surfaceInsets.left == 0 && params.surfaceInsets.top == 0 && params.surfaceInsets.right == 0 && params.surfaceInsets.bottom == 0 && !dragResizing) {
            opaque = true;
        }
        if (!forceUpdate && this.mIsSurfaceOpaque == opaque) {
            return;
        }
        ThreadedRenderer renderer = this.mAttachInfo.mThreadedRenderer;
        if (renderer != null && renderer.rendererOwnsSurfaceControlOpacity()) {
            opaque = renderer.setSurfaceControlOpaque(opaque);
        } else {
            this.mTransaction.setOpaque(this.mSurfaceControl, opaque).apply();
        }
        this.mIsSurfaceOpaque = opaque;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setFrame(Rect frame, boolean withinRelayout) {
        Rect rect;
        this.mWinFrame.set(frame);
        if (withinRelayout) {
            this.mLastLayoutFrame.set(frame);
        }
        WindowConfiguration winConfig = getCompatWindowConfiguration();
        Rect rect2 = this.mPendingBackDropFrame;
        if (this.mPendingDragResizing && !winConfig.useWindowFrameForBackdrop()) {
            rect = winConfig.getMaxBounds();
        } else {
            rect = frame;
        }
        rect2.set(rect);
        this.mPendingBackDropFrame.offsetTo(0, 0);
        InsetsController insetsController = this.mInsetsController;
        Rect rect3 = this.mOverrideInsetsFrame;
        if (rect3 == null) {
            rect3 = frame;
        }
        insetsController.onFrameChanged(rect3);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setOverrideInsetsFrame(Rect frame) {
        Rect rect = new Rect(frame);
        this.mOverrideInsetsFrame = rect;
        this.mInsetsController.onFrameChanged(rect);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void getDisplayFrame(Rect outFrame) {
        outFrame.set(this.mTmpFrames.displayFrame);
        applyViewBoundsSandboxingIfNeeded(outFrame);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void getWindowVisibleDisplayFrame(Rect outFrame) {
        outFrame.set(this.mTmpFrames.displayFrame);
        Rect insets = this.mAttachInfo.mVisibleInsets;
        outFrame.left += insets.left;
        outFrame.top += insets.top;
        outFrame.right -= insets.right;
        outFrame.bottom -= insets.bottom;
        applyViewBoundsSandboxingIfNeeded(outFrame);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void applyViewBoundsSandboxingIfNeeded(Rect inOutRect) {
        if (this.mViewBoundsSandboxingEnabled) {
            Rect bounds = getConfiguration().windowConfiguration.getBounds();
            inOutRect.offset(-bounds.left, -bounds.top);
        }
    }

    public void applyViewLocationSandboxingIfNeeded(int[] outLocation) {
        if (this.mViewBoundsSandboxingEnabled) {
            Rect bounds = getConfiguration().windowConfiguration.getBounds();
            outLocation[0] = outLocation[0] - bounds.left;
            outLocation[1] = outLocation[1] - bounds.top;
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:15:0x0035 A[RETURN] */
    /* JADX WARN: Removed duplicated region for block: B:18:0x0038 A[RETURN] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    private boolean getViewBoundsSandboxingEnabled() {
        List<PackageManager.Property> properties;
        boolean isOptedOut;
        if (ActivityThread.isSystem() || !CompatChanges.isChangeEnabled(ActivityInfo.OVERRIDE_SANDBOX_VIEW_BOUNDS_APIS)) {
            return false;
        }
        try {
            properties = this.mContext.getPackageManager().queryApplicationProperty(WindowManager.PROPERTY_COMPAT_ALLOW_SANDBOXING_VIEW_BOUNDS_APIS);
        } catch (RuntimeException e) {
        }
        if (!properties.isEmpty()) {
            if (!properties.get(0).getBoolean()) {
                isOptedOut = true;
                return isOptedOut;
            }
        }
        isOptedOut = false;
        if (isOptedOut) {
        }
    }

    @Override // android.view.View.AttachInfo.Callbacks
    public void playSoundEffect(int effectId) {
        if ((this.mDisplay.getFlags() & 1024) != 0) {
            return;
        }
        checkThread();
        try {
            AudioManager audioManager = getAudioManager();
            if (this.mFastScrollSoundEffectsEnabled && SoundEffectConstants.isNavigationRepeat(effectId)) {
                audioManager.playSoundEffect(SoundEffectConstants.nextNavigationRepeatSoundEffectId());
                return;
            }
            switch (effectId) {
                case 0:
                    audioManager.playSoundEffect(0);
                    return;
                case 1:
                case 5:
                    audioManager.playSoundEffect(3);
                    return;
                case 2:
                case 6:
                    audioManager.playSoundEffect(1);
                    return;
                case 3:
                case 7:
                    audioManager.playSoundEffect(4);
                    return;
                case 4:
                case 8:
                    audioManager.playSoundEffect(2);
                    return;
                default:
                    throw new IllegalArgumentException("unknown effect id " + effectId + " not defined in " + SoundEffectConstants.class.getCanonicalName());
            }
        } catch (IllegalStateException e) {
            Log.m110e(this.mTag, "FATAL EXCEPTION when attempting to play sound effect: " + e);
            e.printStackTrace();
        }
    }

    @Override // android.view.View.AttachInfo.Callbacks
    public boolean performHapticFeedback(int effectId, boolean always) {
        if ((this.mDisplay.getFlags() & 1024) != 0) {
            return false;
        }
        try {
            this.mWindowSession.performHapticFeedbackAsync(effectId, always);
            return true;
        } catch (RemoteException e) {
            return false;
        }
    }

    @Override // android.view.ViewParent
    public View focusSearch(View focused, int direction) {
        checkThread();
        if (!(this.mView instanceof ViewGroup)) {
            return null;
        }
        return FocusFinder.getInstance().findNextFocus((ViewGroup) this.mView, focused, direction);
    }

    @Override // android.view.ViewParent
    public View keyboardNavigationClusterSearch(View currentCluster, int direction) {
        checkThread();
        return FocusFinder.getInstance().findNextKeyboardNavigationCluster(this.mView, currentCluster, direction);
    }

    public void debug() {
        this.mView.debug();
    }

    public void dumpDebug(ProtoOutputStream proto, long fieldId) {
        long token = proto.start(fieldId);
        proto.write(1138166333441L, Objects.toString(this.mView));
        proto.write(1120986464258L, this.mDisplay.getDisplayId());
        proto.write(1133871366147L, this.mAppVisible);
        proto.write(1120986464261L, this.mHeight);
        proto.write(1120986464260L, this.mWidth);
        proto.write(1133871366150L, this.mIsAnimating);
        this.mVisRect.dumpDebug(proto, 1146756268039L);
        proto.write(1133871366152L, this.mIsDrawing);
        proto.write(1133871366153L, this.mAdded);
        this.mWinFrame.dumpDebug(proto, 1146756268042L);
        proto.write(1138166333452L, Objects.toString(this.mLastWindowInsets));
        proto.write(1138166333453L, InputMethodDebug.softInputModeToString(this.mSoftInputMode));
        proto.write(1120986464270L, this.mScrollY);
        proto.write(1120986464271L, this.mCurScrollY);
        proto.write(1133871366160L, this.mRemoved);
        this.mWindowAttributes.dumpDebug(proto, 1146756268049L);
        proto.end(token);
        this.mInsetsController.dumpDebug(proto, 1146756268036L);
        this.mImeFocusController.dumpDebug(proto, 1146756268039L);
    }

    public void dump(String prefix, PrintWriter writer) {
        String innerPrefix = prefix + "  ";
        writer.println(prefix + "ViewRoot:");
        writer.println(innerPrefix + "mAdded=" + this.mAdded);
        writer.println(innerPrefix + "mRemoved=" + this.mRemoved);
        writer.println(innerPrefix + "mStopped=" + this.mStopped);
        writer.println(innerPrefix + "mPausedForTransition=" + this.mPausedForTransition);
        writer.println(innerPrefix + "mConsumeBatchedInputScheduled=" + this.mConsumeBatchedInputScheduled);
        writer.println(innerPrefix + "mConsumeBatchedInputImmediatelyScheduled=" + this.mConsumeBatchedInputImmediatelyScheduled);
        writer.println(innerPrefix + "mPendingInputEventCount=" + this.mPendingInputEventCount);
        writer.println(innerPrefix + "mProcessInputEventsScheduled=" + this.mProcessInputEventsScheduled);
        writer.println(innerPrefix + "mTraversalScheduled=" + this.mTraversalScheduled);
        if (this.mTraversalScheduled) {
            writer.println(innerPrefix + " (barrier=" + this.mTraversalBarrier + NavigationBarInflaterView.KEY_CODE_END);
        }
        writer.println(innerPrefix + "mReportNextDraw=" + this.mReportNextDraw);
        if (this.mReportNextDraw) {
            writer.println(innerPrefix + " (reason=" + this.mLastReportNextDrawReason + NavigationBarInflaterView.KEY_CODE_END);
        }
        if (this.mLastPerformTraversalsSkipDrawReason != null) {
            writer.println(innerPrefix + "mLastPerformTraversalsFailedReason=" + this.mLastPerformTraversalsSkipDrawReason);
        }
        if (this.mLastPerformDrawSkippedReason != null) {
            writer.println(innerPrefix + "mLastPerformDrawFailedReason=" + this.mLastPerformDrawSkippedReason);
        }
        if (this.mWmsRequestSyncGroupState != 0) {
            writer.println(innerPrefix + "mWmsRequestSyncGroupState=" + this.mWmsRequestSyncGroupState);
        }
        writer.println(innerPrefix + "mLastReportedMergedConfiguration=" + this.mLastReportedMergedConfiguration);
        writer.println(innerPrefix + "mLastConfigurationFromResources=" + this.mLastConfigurationFromResources);
        writer.println(innerPrefix + "mIsAmbientMode=" + this.mIsAmbientMode);
        writer.println(innerPrefix + "mUnbufferedInputSource=" + Integer.toHexString(this.mUnbufferedInputSource));
        if (this.mAttachInfo != null) {
            writer.print(innerPrefix + "mAttachInfo= ");
            this.mAttachInfo.dump(innerPrefix, writer);
        } else {
            writer.println(innerPrefix + "mAttachInfo=<null>");
        }
        this.mFirstInputStage.dump(innerPrefix, writer);
        WindowInputEventReceiver windowInputEventReceiver = this.mInputEventReceiver;
        if (windowInputEventReceiver != null) {
            windowInputEventReceiver.dump(innerPrefix, writer);
        }
        this.mChoreographer.dump(prefix, writer);
        this.mInsetsController.dump(prefix, writer);
        this.mOnBackInvokedDispatcher.dump(prefix, writer);
        writer.println(prefix + "View Hierarchy:");
        dumpViewHierarchy(innerPrefix, writer, this.mView);
    }

    private void dumpViewHierarchy(String prefix, PrintWriter writer, View view) {
        ViewGroup grp;
        int N;
        writer.print(prefix);
        if (view == null) {
            writer.println("null");
            return;
        }
        writer.println(view.toString());
        if (!(view instanceof ViewGroup) || (N = (grp = (ViewGroup) view).getChildCount()) <= 0) {
            return;
        }
        String prefix2 = prefix + "  ";
        for (int i = 0; i < N; i++) {
            dumpViewHierarchy(prefix2, writer, grp.getChildAt(i));
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes4.dex */
    public static final class GfxInfo {
        public long renderNodeMemoryAllocated;
        public long renderNodeMemoryUsage;
        public int viewCount;

        /* JADX INFO: Access modifiers changed from: package-private */
        public void add(GfxInfo other) {
            this.viewCount += other.viewCount;
            this.renderNodeMemoryUsage += other.renderNodeMemoryUsage;
            this.renderNodeMemoryAllocated += other.renderNodeMemoryAllocated;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public GfxInfo getGfxInfo() {
        GfxInfo info = new GfxInfo();
        View view = this.mView;
        if (view != null) {
            appendGfxInfo(view, info);
        }
        return info;
    }

    private static void computeRenderNodeUsage(RenderNode node, GfxInfo info) {
        if (node == null) {
            return;
        }
        info.renderNodeMemoryUsage += node.computeApproximateMemoryUsage();
        info.renderNodeMemoryAllocated += node.computeApproximateMemoryAllocated();
    }

    private static void appendGfxInfo(View view, GfxInfo info) {
        info.viewCount++;
        computeRenderNodeUsage(view.mRenderNode, info);
        computeRenderNodeUsage(view.mBackgroundRenderNode, info);
        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            int count = group.getChildCount();
            for (int i = 0; i < count; i++) {
                appendGfxInfo(group.getChildAt(i), info);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean die(boolean immediate) {
        if (immediate && !this.mIsInTraversal) {
            doDie();
            return false;
        }
        if (!this.mIsDrawing) {
            destroyHardwareRenderer();
        } else {
            Log.m110e(this.mTag, "Attempting to destroy the window while drawing!\n  window=" + this + ", title=" + ((Object) this.mWindowAttributes.getTitle()));
        }
        this.mHandler.sendEmptyMessage(3);
        return true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void doDie() {
        checkThread();
        synchronized (this) {
            if (this.mRemoved) {
                return;
            }
            this.mRemoved = true;
            this.mOnBackInvokedDispatcher.detachFromWindow();
            if (this.mAdded) {
                dispatchDetachedFromWindow();
            }
            if (this.mAdded && !this.mFirst) {
                destroyHardwareRenderer();
                View view = this.mView;
                if (view != null) {
                    int viewVisibility = view.getVisibility();
                    boolean viewVisibilityChanged = this.mViewVisibility != viewVisibility;
                    if (this.mWindowAttributesChanged || viewVisibilityChanged) {
                        try {
                            if ((1 & relayoutWindow(this.mWindowAttributes, viewVisibility, false)) != 0) {
                                this.mWindowSession.finishDrawing(this.mWindow, null, Integer.MAX_VALUE);
                            }
                        } catch (RemoteException e) {
                        }
                    }
                    destroySurface();
                }
            }
            this.mInsetsController.onControlsChanged(null);
            this.mAdded = false;
            AnimationHandler.removeRequestor(this);
            SurfaceSyncGroup surfaceSyncGroup = this.mActiveSurfaceSyncGroup;
            if (surfaceSyncGroup != null) {
                surfaceSyncGroup.markSyncReady();
                this.mActiveSurfaceSyncGroup = null;
            }
            WindowManagerGlobal.getInstance().doRemoveView(this);
        }
    }

    public void requestUpdateConfiguration(Configuration config) {
        Message msg = this.mHandler.obtainMessage(18, config);
        this.mHandler.sendMessage(msg);
    }

    public void loadSystemProperties() {
        this.mHandler.post(new Runnable() { // from class: android.view.ViewRootImpl.7
            @Override // java.lang.Runnable
            public void run() {
                ViewRootImpl.this.mProfileRendering = SystemProperties.getBoolean(ViewRootImpl.PROPERTY_PROFILE_RENDERING, false);
                ViewRootImpl viewRootImpl = ViewRootImpl.this;
                viewRootImpl.profileRendering(viewRootImpl.mAttachInfo.mHasWindowFocus);
                if (ViewRootImpl.this.mAttachInfo.mThreadedRenderer != null && ViewRootImpl.this.mAttachInfo.mThreadedRenderer.loadSystemProperties()) {
                    ViewRootImpl.this.invalidate();
                }
                boolean layout = DisplayProperties.debug_layout().orElse(false).booleanValue();
                if (layout != ViewRootImpl.this.mAttachInfo.mDebugLayout) {
                    ViewRootImpl.this.mAttachInfo.mDebugLayout = layout;
                    if (!ViewRootImpl.this.mHandler.hasMessages(22)) {
                        ViewRootImpl.this.mHandler.sendEmptyMessageDelayed(22, 200L);
                    }
                }
            }
        });
    }

    private void destroyHardwareRenderer() {
        ThreadedRenderer hardwareRenderer = this.mAttachInfo.mThreadedRenderer;
        Consumer<Display> consumer = this.mHdrSdrRatioChangedListener;
        if (consumer != null) {
            this.mDisplay.unregisterHdrSdrRatioChangedListener(consumer);
        }
        if (hardwareRenderer != null) {
            HardwareRendererObserver hardwareRendererObserver = this.mHardwareRendererObserver;
            if (hardwareRendererObserver != null) {
                hardwareRenderer.removeObserver(hardwareRendererObserver);
            }
            View view = this.mView;
            if (view != null) {
                hardwareRenderer.destroyHardwareResources(view);
            }
            hardwareRenderer.destroy();
            hardwareRenderer.setRequested(false);
            this.mAttachInfo.mThreadedRenderer = null;
            this.mAttachInfo.mHardwareAccelerated = false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void dispatchResized(ClientWindowFrames frames, boolean reportDraw, MergedConfiguration mergedConfiguration, InsetsState insetsState, boolean forceLayout, boolean alwaysConsumeSystemBars, int displayId, int syncSeqId, boolean dragResizing) {
        InsetsState insetsState2;
        Message msg = this.mHandler.obtainMessage(reportDraw ? 5 : 4);
        SomeArgs args = SomeArgs.obtain();
        boolean sameProcessCall = Binder.getCallingPid() == Process.myPid();
        if (!sameProcessCall) {
            insetsState2 = insetsState;
        } else {
            insetsState2 = new InsetsState(insetsState, true);
        }
        CompatibilityInfo.Translator translator = this.mTranslator;
        if (translator != null) {
            translator.translateInsetsStateInScreenToAppWindow(insetsState2);
        }
        if (insetsState2.isSourceOrDefaultVisible(InsetsSource.ID_IME, WindowInsets.Type.ime())) {
            ImeTracing.getInstance().triggerClientDump("ViewRootImpl#dispatchResized", getInsetsController().getHost().getInputMethodManager(), null);
        }
        args.arg1 = sameProcessCall ? new ClientWindowFrames(frames) : frames;
        args.arg2 = (!sameProcessCall || mergedConfiguration == null) ? mergedConfiguration : new MergedConfiguration(mergedConfiguration);
        args.arg3 = insetsState2;
        args.argi1 = forceLayout ? 1 : 0;
        args.argi2 = alwaysConsumeSystemBars ? 1 : 0;
        args.argi3 = displayId;
        args.argi4 = syncSeqId;
        args.argi5 = dragResizing ? 1 : 0;
        msg.obj = args;
        this.mHandler.sendMessage(msg);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void dispatchInsetsControlChanged(InsetsState insetsState, InsetsSourceControl[] activeControls) {
        if (Binder.getCallingPid() == Process.myPid()) {
            insetsState = new InsetsState(insetsState, true);
            if (activeControls != null) {
                for (int i = activeControls.length - 1; i >= 0; i--) {
                    activeControls[i] = new InsetsSourceControl(activeControls[i]);
                }
            }
        }
        CompatibilityInfo.Translator translator = this.mTranslator;
        if (translator != null) {
            translator.translateInsetsStateInScreenToAppWindow(insetsState);
            this.mTranslator.translateSourceControlsInScreenToAppWindow(activeControls);
        }
        if (insetsState != null && insetsState.isSourceOrDefaultVisible(InsetsSource.ID_IME, WindowInsets.Type.ime())) {
            ImeTracing.getInstance().triggerClientDump("ViewRootImpl#dispatchInsetsControlChanged", getInsetsController().getHost().getInputMethodManager(), null);
        }
        SomeArgs args = SomeArgs.obtain();
        args.arg1 = insetsState;
        args.arg2 = activeControls;
        this.mHandler.obtainMessage(29, args).sendToTarget();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showInsets(int types, boolean fromIme, ImeTracker.Token statsToken) {
        this.mHandler.obtainMessage(31, types, fromIme ? 1 : 0, statsToken).sendToTarget();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void hideInsets(int types, boolean fromIme, ImeTracker.Token statsToken) {
        this.mHandler.obtainMessage(32, types, fromIme ? 1 : 0, statsToken).sendToTarget();
    }

    public void dispatchMoved(int newX, int newY) {
        if (this.mTranslator != null) {
            PointF point = new PointF(newX, newY);
            this.mTranslator.translatePointInScreenToAppWindow(point);
            newX = (int) (point.f78x + 0.5d);
            newY = (int) (point.f79y + 0.5d);
        }
        Message msg = this.mHandler.obtainMessage(23, newX, newY);
        this.mHandler.sendMessage(msg);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public static final class QueuedInputEvent {
        public static final int FLAG_DEFERRED = 2;
        public static final int FLAG_DELIVER_POST_IME = 1;
        public static final int FLAG_FINISHED = 4;
        public static final int FLAG_FINISHED_HANDLED = 8;
        public static final int FLAG_MODIFIED_FOR_COMPATIBILITY = 64;
        public static final int FLAG_RESYNTHESIZED = 16;
        public static final int FLAG_UNHANDLED = 32;
        public InputEvent mEvent;
        public int mFlags;
        public QueuedInputEvent mNext;
        public InputEventReceiver mReceiver;

        private QueuedInputEvent() {
        }

        public boolean shouldSkipIme() {
            if ((this.mFlags & 1) != 0) {
                return true;
            }
            InputEvent inputEvent = this.mEvent;
            return (inputEvent instanceof MotionEvent) && (inputEvent.isFromSource(2) || this.mEvent.isFromSource(4194304));
        }

        public boolean shouldSendToSynthesizer() {
            if ((this.mFlags & 32) != 0) {
                return true;
            }
            return false;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder("QueuedInputEvent{flags=");
            boolean hasPrevious = flagToString("DELIVER_POST_IME", 1, false, sb);
            if (!flagToString("UNHANDLED", 32, flagToString("RESYNTHESIZED", 16, flagToString("FINISHED_HANDLED", 8, flagToString("FINISHED", 4, flagToString("DEFERRED", 2, hasPrevious, sb), sb), sb), sb), sb)) {
                sb.append(AudioSystem.LEGACY_REMOTE_SUBMIX_ADDRESS);
            }
            sb.append(", hasNextQueuedEvent=" + (this.mEvent != null ? "true" : "false"));
            sb.append(", hasInputEventReceiver=" + (this.mReceiver == null ? "false" : "true"));
            sb.append(", mEvent=" + this.mEvent + "}");
            return sb.toString();
        }

        private boolean flagToString(String name, int flag, boolean hasPrevious, StringBuilder sb) {
            if ((this.mFlags & flag) != 0) {
                if (hasPrevious) {
                    sb.append(NtpTrustedTime.NTP_SETTING_SERVER_NAME_DELIMITER);
                }
                sb.append(name);
                return true;
            }
            return hasPrevious;
        }
    }

    private QueuedInputEvent obtainQueuedInputEvent(InputEvent event, InputEventReceiver receiver, int flags) {
        QueuedInputEvent q = this.mQueuedInputEventPool;
        if (q != null) {
            this.mQueuedInputEventPoolSize--;
            this.mQueuedInputEventPool = q.mNext;
            q.mNext = null;
        } else {
            q = new QueuedInputEvent();
        }
        q.mEvent = event;
        q.mReceiver = receiver;
        q.mFlags = flags;
        return q;
    }

    private void recycleQueuedInputEvent(QueuedInputEvent q) {
        q.mEvent = null;
        q.mReceiver = null;
        int i = this.mQueuedInputEventPoolSize;
        if (i < 10) {
            this.mQueuedInputEventPoolSize = i + 1;
            q.mNext = this.mQueuedInputEventPool;
            this.mQueuedInputEventPool = q;
        }
    }

    void enqueueInputEvent(InputEvent event) {
        enqueueInputEvent(event, null, 0, false);
    }

    void enqueueInputEvent(InputEvent event, InputEventReceiver receiver, int flags, boolean processImmediately) {
        QueuedInputEvent q = obtainQueuedInputEvent(event, receiver, flags);
        if (event instanceof MotionEvent) {
            MotionEvent me = (MotionEvent) event;
            if (me.getAction() == 3) {
                EventLog.writeEvent((int) EventLogTags.VIEW_ENQUEUE_INPUT_EVENT, "Motion - Cancel", getTitle().toString());
            }
        } else if (event instanceof KeyEvent) {
            KeyEvent ke = (KeyEvent) event;
            if (ke.isCanceled()) {
                EventLog.writeEvent((int) EventLogTags.VIEW_ENQUEUE_INPUT_EVENT, "Key - Cancel", getTitle().toString());
            }
        }
        QueuedInputEvent last = this.mPendingInputEventTail;
        if (last == null) {
            this.mPendingInputEventHead = q;
            this.mPendingInputEventTail = q;
        } else {
            last.mNext = q;
            this.mPendingInputEventTail = q;
        }
        int i = this.mPendingInputEventCount + 1;
        this.mPendingInputEventCount = i;
        Trace.traceCounter(4L, this.mPendingInputEventQueueLengthCounterName, i);
        if (processImmediately) {
            doProcessInputEvents();
        } else {
            scheduleProcessInputEvents();
        }
    }

    private void scheduleProcessInputEvents() {
        if (!this.mProcessInputEventsScheduled) {
            this.mProcessInputEventsScheduled = true;
            Message msg = this.mHandler.obtainMessage(19);
            msg.setAsynchronous(true);
            this.mHandler.sendMessage(msg);
        }
    }

    void doProcessInputEvents() {
        while (this.mPendingInputEventHead != null) {
            QueuedInputEvent q = this.mPendingInputEventHead;
            QueuedInputEvent queuedInputEvent = q.mNext;
            this.mPendingInputEventHead = queuedInputEvent;
            if (queuedInputEvent == null) {
                this.mPendingInputEventTail = null;
            }
            q.mNext = null;
            int i = this.mPendingInputEventCount - 1;
            this.mPendingInputEventCount = i;
            Trace.traceCounter(4L, this.mPendingInputEventQueueLengthCounterName, i);
            this.mViewFrameInfo.setInputEvent(this.mInputEventAssigner.processEvent(q.mEvent));
            deliverInputEvent(q);
        }
        if (this.mProcessInputEventsScheduled) {
            this.mProcessInputEventsScheduled = false;
            this.mHandler.removeMessages(19);
        }
    }

    private void deliverInputEvent(QueuedInputEvent q) {
        InputStage stage;
        Trace.asyncTraceBegin(8L, "deliverInputEvent", q.mEvent.getId());
        if (Trace.isTagEnabled(8L)) {
            Trace.traceBegin(8L, "deliverInputEvent src=0x" + Integer.toHexString(q.mEvent.getSource()) + " eventTimeNano=" + q.mEvent.getEventTimeNano() + " id=0x" + Integer.toHexString(q.mEvent.getId()));
        }
        try {
            if (this.mInputEventConsistencyVerifier != null) {
                Trace.traceBegin(8L, "verifyEventConsistency");
                this.mInputEventConsistencyVerifier.onInputEvent(q.mEvent, 0);
                Trace.traceEnd(8L);
            }
            if (q.shouldSendToSynthesizer()) {
                stage = this.mSyntheticInputStage;
            } else {
                stage = q.shouldSkipIme() ? this.mFirstPostImeInputStage : this.mFirstInputStage;
            }
            if (q.mEvent instanceof KeyEvent) {
                Trace.traceBegin(8L, "preDispatchToUnhandledKeyManager");
                this.mUnhandledKeyManager.preDispatch((KeyEvent) q.mEvent);
                Trace.traceEnd(8L);
            }
            if (stage != null) {
                handleWindowFocusChanged();
                stage.deliver(q);
            } else {
                finishInputEvent(q);
            }
        } finally {
            Trace.traceEnd(8L);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void finishInputEvent(QueuedInputEvent q) {
        Trace.asyncTraceEnd(8L, "deliverInputEvent", q.mEvent.getId());
        if (q.mReceiver != null) {
            boolean handled = (q.mFlags & 8) != 0;
            boolean modified = (q.mFlags & 64) != 0;
            if (modified) {
                Trace.traceBegin(8L, "processInputEventBeforeFinish");
                try {
                    InputEvent processedEvent = this.mInputCompatProcessor.processInputEventBeforeFinish(q.mEvent);
                    if (processedEvent != null) {
                        q.mReceiver.finishInputEvent(processedEvent, handled);
                    }
                } finally {
                    Trace.traceEnd(8L);
                }
            } else {
                q.mReceiver.finishInputEvent(q.mEvent, handled);
            }
        } else {
            q.mEvent.recycleIfNeededAfterDispatch();
        }
        recycleQueuedInputEvent(q);
    }

    static boolean isTerminalInputEvent(InputEvent event) {
        if (event instanceof KeyEvent) {
            KeyEvent keyEvent = (KeyEvent) event;
            return keyEvent.getAction() == 1;
        }
        MotionEvent motionEvent = (MotionEvent) event;
        int action = motionEvent.getAction();
        return action == 1 || action == 3 || action == 10;
    }

    void scheduleConsumeBatchedInput() {
        if (!this.mConsumeBatchedInputScheduled && !this.mConsumeBatchedInputImmediatelyScheduled) {
            this.mConsumeBatchedInputScheduled = true;
            this.mChoreographer.postCallback(0, this.mConsumedBatchedInputRunnable, null);
            if (this.mAttachInfo.mThreadedRenderer != null) {
                this.mAttachInfo.mThreadedRenderer.notifyCallbackPending();
            }
        }
    }

    void unscheduleConsumeBatchedInput() {
        if (this.mConsumeBatchedInputScheduled) {
            this.mConsumeBatchedInputScheduled = false;
            this.mChoreographer.removeCallbacks(0, this.mConsumedBatchedInputRunnable, null);
        }
    }

    void scheduleConsumeBatchedInputImmediately() {
        if (!this.mConsumeBatchedInputImmediatelyScheduled) {
            unscheduleConsumeBatchedInput();
            this.mConsumeBatchedInputImmediatelyScheduled = true;
            this.mHandler.post(this.mConsumeBatchedInputImmediatelyRunnable);
        }
    }

    boolean doConsumeBatchedInput(long frameTimeNanos) {
        boolean consumedBatches;
        WindowInputEventReceiver windowInputEventReceiver = this.mInputEventReceiver;
        if (windowInputEventReceiver != null) {
            consumedBatches = windowInputEventReceiver.consumeBatchedInputEvents(frameTimeNanos);
        } else {
            consumedBatches = false;
        }
        doProcessInputEvents();
        return consumedBatches;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes4.dex */
    public final class TraversalRunnable implements Runnable {
        TraversalRunnable() {
        }

        @Override // java.lang.Runnable
        public void run() {
            ViewRootImpl.this.doTraversal();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes4.dex */
    public final class WindowInputEventReceiver extends InputEventReceiver {
        public WindowInputEventReceiver(InputChannel inputChannel, Looper looper) {
            super(inputChannel, looper);
        }

        @Override // android.view.InputEventReceiver
        public void onInputEvent(InputEvent event) {
            Trace.traceBegin(8L, "processInputEventForCompatibility");
            try {
                List<InputEvent> processedEvents = ViewRootImpl.this.mInputCompatProcessor.processInputEventForCompatibility(event);
                Trace.traceEnd(8L);
                if (processedEvents == null) {
                    ViewRootImpl.this.enqueueInputEvent(event, this, 0, true);
                } else if (processedEvents.isEmpty()) {
                    finishInputEvent(event, true);
                } else {
                    for (int i = 0; i < processedEvents.size(); i++) {
                        ViewRootImpl.this.enqueueInputEvent(processedEvents.get(i), this, 64, true);
                    }
                }
            } catch (Throwable th) {
                Trace.traceEnd(8L);
                throw th;
            }
        }

        @Override // android.view.InputEventReceiver
        public void onBatchedInputEventPending(int source) {
            boolean unbuffered = ViewRootImpl.this.mUnbufferedInputDispatch || (ViewRootImpl.this.mUnbufferedInputSource & source) != 0;
            if (unbuffered) {
                if (ViewRootImpl.this.mConsumeBatchedInputScheduled) {
                    ViewRootImpl.this.unscheduleConsumeBatchedInput();
                }
                consumeBatchedInputEvents(-1L);
                return;
            }
            ViewRootImpl.this.scheduleConsumeBatchedInput();
        }

        @Override // android.view.InputEventReceiver
        public void onFocusEvent(boolean hasFocus) {
            ViewRootImpl.this.windowFocusChanged(hasFocus);
        }

        @Override // android.view.InputEventReceiver
        public void onTouchModeChanged(boolean inTouchMode) {
            ViewRootImpl.this.touchModeChanged(inTouchMode);
        }

        @Override // android.view.InputEventReceiver
        public void onPointerCaptureEvent(boolean pointerCaptureEnabled) {
            ViewRootImpl.this.dispatchPointerCaptureChanged(pointerCaptureEnabled);
        }

        @Override // android.view.InputEventReceiver
        public void onDragEvent(boolean isExiting, float x, float y) {
            DragEvent event = DragEvent.obtain(isExiting ? 6 : 2, x, y, 0.0f, 0.0f, null, null, null, null, null, false);
            ViewRootImpl.this.dispatchDragEvent(event);
        }

        @Override // android.view.InputEventReceiver
        public void dispose() {
            ViewRootImpl.this.unscheduleConsumeBatchedInput();
            super.dispose();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes4.dex */
    public final class InputMetricsListener implements HardwareRendererObserver.OnFrameMetricsAvailableListener {
        public long[] data = new long[23];

        InputMetricsListener() {
        }

        @Override // android.graphics.HardwareRendererObserver.OnFrameMetricsAvailableListener
        public void onFrameMetricsAvailable(int dropCountSinceLastInvocation) {
            long[] jArr = this.data;
            int inputEventId = (int) jArr[4];
            if (inputEventId == 0) {
                return;
            }
            long presentTime = jArr[21];
            if (presentTime <= 0) {
                return;
            }
            long gpuCompletedTime = jArr[19];
            if (ViewRootImpl.this.mInputEventReceiver == null) {
                return;
            }
            if (gpuCompletedTime >= presentTime) {
                double discrepancyMs = (gpuCompletedTime - presentTime) * 1.0E-6d;
                long vsyncId = this.data[1];
                Log.m104w(ViewRootImpl.TAG, "Not reporting timeline because gpuCompletedTime is " + discrepancyMs + "ms ahead of presentTime. FRAME_TIMELINE_VSYNC_ID=" + vsyncId + ", INPUT_EVENT_ID=" + inputEventId);
                return;
            }
            ViewRootImpl.this.mInputEventReceiver.reportTimeline(inputEventId, gpuCompletedTime, presentTime);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes4.dex */
    public final class ConsumeBatchedInputRunnable implements Runnable {
        ConsumeBatchedInputRunnable() {
        }

        @Override // java.lang.Runnable
        public void run() {
            ViewRootImpl.this.mConsumeBatchedInputScheduled = false;
            ViewRootImpl viewRootImpl = ViewRootImpl.this;
            if (viewRootImpl.doConsumeBatchedInput(viewRootImpl.mChoreographer.getFrameTimeNanos())) {
                ViewRootImpl.this.scheduleConsumeBatchedInput();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes4.dex */
    public final class ConsumeBatchedInputImmediatelyRunnable implements Runnable {
        ConsumeBatchedInputImmediatelyRunnable() {
        }

        @Override // java.lang.Runnable
        public void run() {
            ViewRootImpl.this.mConsumeBatchedInputImmediatelyScheduled = false;
            ViewRootImpl.this.doConsumeBatchedInput(-1L);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes4.dex */
    public final class InvalidateOnAnimationRunnable implements Runnable {
        private boolean mPosted;
        private View.AttachInfo.InvalidateInfo[] mTempViewRects;
        private View[] mTempViews;
        private final ArrayList<View> mViews = new ArrayList<>();
        private final ArrayList<View.AttachInfo.InvalidateInfo> mViewRects = new ArrayList<>();

        InvalidateOnAnimationRunnable() {
        }

        public void addView(View view) {
            synchronized (this) {
                this.mViews.add(view);
                postIfNeededLocked();
            }
            if (ViewRootImpl.this.mAttachInfo.mThreadedRenderer != null) {
                ViewRootImpl.this.mAttachInfo.mThreadedRenderer.notifyCallbackPending();
            }
        }

        public void addViewRect(View.AttachInfo.InvalidateInfo info) {
            synchronized (this) {
                this.mViewRects.add(info);
                postIfNeededLocked();
            }
            if (ViewRootImpl.this.mAttachInfo.mThreadedRenderer != null) {
                ViewRootImpl.this.mAttachInfo.mThreadedRenderer.notifyCallbackPending();
            }
        }

        public void removeView(View view) {
            synchronized (this) {
                this.mViews.remove(view);
                int i = this.mViewRects.size();
                while (true) {
                    int i2 = i - 1;
                    if (i <= 0) {
                        break;
                    }
                    View.AttachInfo.InvalidateInfo info = this.mViewRects.get(i2);
                    if (info.target == view) {
                        this.mViewRects.remove(i2);
                        info.recycle();
                    }
                    i = i2;
                }
                if (this.mPosted && this.mViews.isEmpty() && this.mViewRects.isEmpty()) {
                    ViewRootImpl.this.mChoreographer.removeCallbacks(1, this, null);
                    this.mPosted = false;
                }
            }
        }

        @Override // java.lang.Runnable
        public void run() {
            int viewCount;
            int viewRectCount;
            synchronized (this) {
                this.mPosted = false;
                viewCount = this.mViews.size();
                if (viewCount != 0) {
                    ArrayList<View> arrayList = this.mViews;
                    View[] viewArr = this.mTempViews;
                    if (viewArr == null) {
                        viewArr = new View[viewCount];
                    }
                    this.mTempViews = (View[]) arrayList.toArray(viewArr);
                    this.mViews.clear();
                }
                viewRectCount = this.mViewRects.size();
                if (viewRectCount != 0) {
                    ArrayList<View.AttachInfo.InvalidateInfo> arrayList2 = this.mViewRects;
                    View.AttachInfo.InvalidateInfo[] invalidateInfoArr = this.mTempViewRects;
                    if (invalidateInfoArr == null) {
                        invalidateInfoArr = new View.AttachInfo.InvalidateInfo[viewRectCount];
                    }
                    this.mTempViewRects = (View.AttachInfo.InvalidateInfo[]) arrayList2.toArray(invalidateInfoArr);
                    this.mViewRects.clear();
                }
            }
            for (int i = 0; i < viewCount; i++) {
                this.mTempViews[i].invalidate();
                this.mTempViews[i] = null;
            }
            for (int i2 = 0; i2 < viewRectCount; i2++) {
                View.AttachInfo.InvalidateInfo info = this.mTempViewRects[i2];
                info.target.invalidate(info.left, info.top, info.right, info.bottom);
                info.recycle();
            }
        }

        private void postIfNeededLocked() {
            if (!this.mPosted) {
                ViewRootImpl.this.mChoreographer.postCallback(1, this, null);
                this.mPosted = true;
            }
        }
    }

    public void dispatchInvalidateDelayed(View view, long delayMilliseconds) {
        Message msg = this.mHandler.obtainMessage(1, view);
        this.mHandler.sendMessageDelayed(msg, delayMilliseconds);
    }

    public void dispatchInvalidateRectDelayed(View.AttachInfo.InvalidateInfo info, long delayMilliseconds) {
        Message msg = this.mHandler.obtainMessage(2, info);
        this.mHandler.sendMessageDelayed(msg, delayMilliseconds);
    }

    public void dispatchInvalidateOnAnimation(View view) {
        this.mInvalidateOnAnimationRunnable.addView(view);
    }

    public void dispatchInvalidateRectOnAnimation(View.AttachInfo.InvalidateInfo info) {
        this.mInvalidateOnAnimationRunnable.addViewRect(info);
    }

    public void cancelInvalidate(View view) {
        this.mHandler.removeMessages(1, view);
        this.mHandler.removeMessages(2, view);
        this.mInvalidateOnAnimationRunnable.removeView(view);
    }

    public void dispatchInputEvent(InputEvent event) {
        dispatchInputEvent(event, null);
    }

    public void dispatchInputEvent(InputEvent event, InputEventReceiver receiver) {
        SomeArgs args = SomeArgs.obtain();
        args.arg1 = event;
        args.arg2 = receiver;
        Message msg = this.mHandler.obtainMessage(7, args);
        msg.setAsynchronous(true);
        this.mHandler.sendMessage(msg);
    }

    public void synthesizeInputEvent(InputEvent event) {
        Message msg = this.mHandler.obtainMessage(24, event);
        msg.setAsynchronous(true);
        this.mHandler.sendMessage(msg);
    }

    public void dispatchKeyFromIme(KeyEvent event) {
        Message msg = this.mHandler.obtainMessage(11, event);
        msg.setAsynchronous(true);
        this.mHandler.sendMessage(msg);
    }

    public void dispatchKeyFromAutofill(KeyEvent event) {
        Message msg = this.mHandler.obtainMessage(12, event);
        msg.setAsynchronous(true);
        this.mHandler.sendMessage(msg);
    }

    public void dispatchUnhandledInputEvent(InputEvent event) {
        if (event instanceof MotionEvent) {
            event = MotionEvent.obtain((MotionEvent) event);
        }
        synthesizeInputEvent(event);
    }

    public void dispatchAppVisibility(boolean visible) {
        Message msg = this.mHandler.obtainMessage(8);
        msg.arg1 = visible ? 1 : 0;
        this.mHandler.sendMessage(msg);
    }

    public void dispatchGetNewSurface() {
        Message msg = this.mHandler.obtainMessage(9);
        this.mHandler.sendMessage(msg);
    }

    public void windowFocusChanged(boolean hasFocus) {
        synchronized (this) {
            this.mWindowFocusChanged = true;
            this.mUpcomingWindowFocus = hasFocus;
        }
        Message msg = Message.obtain();
        msg.what = 6;
        this.mHandler.sendMessage(msg);
    }

    public void touchModeChanged(boolean inTouchMode) {
        synchronized (this) {
            this.mUpcomingInTouchMode = inTouchMode;
        }
        Message msg = Message.obtain();
        msg.what = 34;
        this.mHandler.sendMessage(msg);
    }

    public void dispatchWindowShown() {
        this.mHandler.sendEmptyMessage(25);
    }

    public void dispatchCloseSystemDialogs(String reason) {
        Message msg = Message.obtain();
        msg.what = 14;
        msg.obj = reason;
        this.mHandler.sendMessage(msg);
    }

    public void dispatchDragEvent(DragEvent event) {
        int what;
        if (event.getAction() == 2) {
            what = 16;
            this.mHandler.removeMessages(16);
        } else {
            what = 15;
        }
        Message msg = this.mHandler.obtainMessage(what, event);
        this.mHandler.sendMessage(msg);
    }

    public void updatePointerIcon(float x, float y) {
        this.mHandler.removeMessages(27);
        long now = SystemClock.uptimeMillis();
        MotionEvent event = MotionEvent.obtain(0L, now, 7, x, y, 0);
        Message msg = this.mHandler.obtainMessage(27, event);
        this.mHandler.sendMessage(msg);
    }

    public void dispatchCheckFocus() {
        if (!this.mHandler.hasMessages(13)) {
            this.mHandler.sendEmptyMessage(13);
        }
    }

    public void dispatchRequestKeyboardShortcuts(IResultReceiver receiver, int deviceId) {
        this.mHandler.obtainMessage(26, deviceId, 0, receiver).sendToTarget();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void dispatchPointerCaptureChanged(boolean on) {
        this.mHandler.removeMessages(28);
        Message msg = this.mHandler.obtainMessage(28);
        msg.arg1 = on ? 1 : 0;
        this.mHandler.sendMessage(msg);
    }

    private void postSendWindowContentChangedCallback(View source, int changeType) {
        if (this.mSendWindowContentChangedAccessibilityEvent == null) {
            this.mSendWindowContentChangedAccessibilityEvent = new SendWindowContentChangedAccessibilityEvent();
        }
        this.mSendWindowContentChangedAccessibilityEvent.runOrPost(source, changeType);
    }

    private void removeSendWindowContentChangedCallback() {
        SendWindowContentChangedAccessibilityEvent sendWindowContentChangedAccessibilityEvent = this.mSendWindowContentChangedAccessibilityEvent;
        if (sendWindowContentChangedAccessibilityEvent != null) {
            this.mHandler.removeCallbacks(sendWindowContentChangedAccessibilityEvent);
        }
    }

    public int getDirectAccessibilityConnectionId() {
        return this.mAccessibilityInteractionConnectionManager.ensureDirectConnection();
    }

    @Override // android.view.ViewParent
    public boolean showContextMenuForChild(View originalView) {
        return false;
    }

    @Override // android.view.ViewParent
    public boolean showContextMenuForChild(View originalView, float x, float y) {
        return false;
    }

    @Override // android.view.ViewParent
    public ActionMode startActionModeForChild(View originalView, ActionMode.Callback callback) {
        return null;
    }

    @Override // android.view.ViewParent
    public ActionMode startActionModeForChild(View originalView, ActionMode.Callback callback, int type) {
        return null;
    }

    @Override // android.view.ViewParent
    public void createContextMenu(ContextMenu menu) {
    }

    @Override // android.view.ViewParent
    public void childDrawableStateChanged(View child) {
    }

    @Override // android.view.ViewParent
    public boolean requestSendAccessibilityEvent(View child, AccessibilityEvent event) {
        AccessibilityNodeProvider provider;
        SendWindowContentChangedAccessibilityEvent sendWindowContentChangedAccessibilityEvent;
        if (this.mView == null || this.mStopped || this.mPausedForTransition) {
            return false;
        }
        if (event.getEventType() != 2048 && (sendWindowContentChangedAccessibilityEvent = this.mSendWindowContentChangedAccessibilityEvent) != null && sendWindowContentChangedAccessibilityEvent.mSource != null) {
            this.mSendWindowContentChangedAccessibilityEvent.removeCallbacksAndRun();
        }
        int eventType = event.getEventType();
        View source = getSourceForAccessibilityEvent(event);
        switch (eventType) {
            case 2048:
                handleWindowContentChangedEvent(event);
                break;
            case 32768:
                if (source != null && (provider = source.getAccessibilityNodeProvider()) != null) {
                    int virtualNodeId = AccessibilityNodeInfo.getVirtualDescendantId(event.getSourceNodeId());
                    AccessibilityNodeInfo node = provider.createAccessibilityNodeInfo(virtualNodeId);
                    setAccessibilityFocus(source, node);
                    break;
                }
                break;
            case 65536:
                if (source != null && source.getAccessibilityNodeProvider() != null) {
                    setAccessibilityFocus(null, null);
                    break;
                }
                break;
        }
        this.mAccessibilityManager.sendAccessibilityEvent(event);
        return true;
    }

    private View getSourceForAccessibilityEvent(AccessibilityEvent event) {
        long sourceNodeId = event.getSourceNodeId();
        int accessibilityViewId = AccessibilityNodeInfo.getAccessibilityViewId(sourceNodeId);
        return AccessibilityNodeIdManager.getInstance().findView(accessibilityViewId);
    }

    private void handleWindowContentChangedEvent(AccessibilityEvent event) {
        View focusedHost = this.mAccessibilityFocusedHost;
        if (focusedHost == null || this.mAccessibilityFocusedVirtualView == null) {
            return;
        }
        AccessibilityNodeProvider provider = focusedHost.getAccessibilityNodeProvider();
        if (provider == null) {
            this.mAccessibilityFocusedHost = null;
            this.mAccessibilityFocusedVirtualView = null;
            focusedHost.clearAccessibilityFocusNoCallbacks(0);
            return;
        }
        int changes = event.getContentChangeTypes();
        if ((changes & 1) == 0 && changes != 0) {
            return;
        }
        long eventSourceNodeId = event.getSourceNodeId();
        int changedViewId = AccessibilityNodeInfo.getAccessibilityViewId(eventSourceNodeId);
        boolean hostInSubtree = false;
        View root = this.mAccessibilityFocusedHost;
        while (root != null && !hostInSubtree) {
            if (changedViewId == root.getAccessibilityViewId()) {
                hostInSubtree = true;
            } else {
                ViewParent parent = root.getParent();
                if (parent instanceof View) {
                    root = (View) parent;
                } else {
                    root = null;
                }
            }
        }
        if (!hostInSubtree) {
            return;
        }
        long focusedSourceNodeId = this.mAccessibilityFocusedVirtualView.getSourceNodeId();
        int focusedChildId = AccessibilityNodeInfo.getVirtualDescendantId(focusedSourceNodeId);
        Rect oldBounds = this.mTempRect;
        this.mAccessibilityFocusedVirtualView.getBoundsInScreen(oldBounds);
        AccessibilityNodeInfo createAccessibilityNodeInfo = provider.createAccessibilityNodeInfo(focusedChildId);
        this.mAccessibilityFocusedVirtualView = createAccessibilityNodeInfo;
        if (createAccessibilityNodeInfo == null) {
            this.mAccessibilityFocusedHost = null;
            focusedHost.clearAccessibilityFocusNoCallbacks(0);
            provider.performAction(focusedChildId, AccessibilityNodeInfo.AccessibilityAction.ACTION_CLEAR_ACCESSIBILITY_FOCUS.getId(), null);
            invalidateRectOnScreen(oldBounds);
            return;
        }
        Rect newBounds = createAccessibilityNodeInfo.getBoundsInScreen();
        if (!oldBounds.equals(newBounds)) {
            oldBounds.union(newBounds);
            invalidateRectOnScreen(oldBounds);
        }
    }

    @Override // android.view.ViewParent
    public void notifySubtreeAccessibilityStateChanged(View child, View source, int changeType) {
        postSendWindowContentChangedCallback((View) Preconditions.checkNotNull(source), changeType);
    }

    @Override // android.view.ViewParent
    public boolean canResolveLayoutDirection() {
        return true;
    }

    @Override // android.view.ViewParent
    public boolean isLayoutDirectionResolved() {
        return true;
    }

    @Override // android.view.ViewParent
    public int getLayoutDirection() {
        return 0;
    }

    @Override // android.view.ViewParent
    public boolean canResolveTextDirection() {
        return true;
    }

    @Override // android.view.ViewParent
    public boolean isTextDirectionResolved() {
        return true;
    }

    @Override // android.view.ViewParent
    public int getTextDirection() {
        return 1;
    }

    @Override // android.view.ViewParent
    public boolean canResolveTextAlignment() {
        return true;
    }

    @Override // android.view.ViewParent
    public boolean isTextAlignmentResolved() {
        return true;
    }

    @Override // android.view.ViewParent
    public int getTextAlignment() {
        return 1;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public View getCommonPredecessor(View first, View second) {
        if (this.mTempHashSet == null) {
            this.mTempHashSet = new HashSet<>();
        }
        HashSet<View> seen = this.mTempHashSet;
        seen.clear();
        View firstCurrent = first;
        while (firstCurrent != null) {
            seen.add(firstCurrent);
            ViewParent firstCurrentParent = firstCurrent.mParent;
            if (firstCurrentParent instanceof View) {
                firstCurrent = (View) firstCurrentParent;
            } else {
                firstCurrent = null;
            }
        }
        View secondCurrent = second;
        while (secondCurrent != null) {
            if (seen.contains(secondCurrent)) {
                seen.clear();
                return secondCurrent;
            }
            ViewParent secondCurrentParent = secondCurrent.mParent;
            if (secondCurrentParent instanceof View) {
                secondCurrent = (View) secondCurrentParent;
            } else {
                secondCurrent = null;
            }
        }
        seen.clear();
        return null;
    }

    void checkThread() {
        Thread current = Thread.currentThread();
        if (this.mThread != current) {
            throw new CalledFromWrongThreadException("Only the original thread that created a view hierarchy can touch its views. Expected: " + this.mThread.getName() + " Calling: " + current.getName());
        }
    }

    @Override // android.view.ViewParent
    public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
    }

    @Override // android.view.ViewParent
    public boolean requestChildRectangleOnScreen(View child, Rect rectangle, boolean immediate) {
        if (rectangle == null) {
            return scrollToRectOrFocus(null, immediate);
        }
        rectangle.offset(child.getLeft() - child.getScrollX(), child.getTop() - child.getScrollY());
        boolean scrolled = scrollToRectOrFocus(rectangle, immediate);
        this.mTempRect.set(rectangle);
        this.mTempRect.offset(0, -this.mCurScrollY);
        this.mTempRect.offset(this.mAttachInfo.mWindowLeft, this.mAttachInfo.mWindowTop);
        try {
            this.mWindowSession.onRectangleOnScreenRequested(this.mWindow, this.mTempRect);
        } catch (RemoteException e) {
        }
        return scrolled;
    }

    @Override // android.view.ViewParent
    public void childHasTransientStateChanged(View child, boolean hasTransientState) {
    }

    @Override // android.view.ViewParent
    public boolean onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        return false;
    }

    @Override // android.view.ViewParent
    public void onStopNestedScroll(View target) {
    }

    @Override // android.view.ViewParent
    public void onNestedScrollAccepted(View child, View target, int nestedScrollAxes) {
    }

    @Override // android.view.ViewParent
    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
    }

    @Override // android.view.ViewParent
    public void onNestedPreScroll(View target, int dx, int dy, int[] consumed) {
    }

    @Override // android.view.ViewParent
    public boolean onNestedFling(View target, float velocityX, float velocityY, boolean consumed) {
        return false;
    }

    @Override // android.view.ViewParent
    public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
        return false;
    }

    @Override // android.view.ViewParent
    public boolean onNestedPrePerformAccessibilityAction(View target, int action, Bundle args) {
        return false;
    }

    public void addScrollCaptureCallback(ScrollCaptureCallback callback) {
        if (this.mRootScrollCaptureCallbacks == null) {
            this.mRootScrollCaptureCallbacks = new HashSet<>();
        }
        this.mRootScrollCaptureCallbacks.add(callback);
    }

    public void removeScrollCaptureCallback(ScrollCaptureCallback callback) {
        HashSet<ScrollCaptureCallback> hashSet = this.mRootScrollCaptureCallbacks;
        if (hashSet != null) {
            hashSet.remove(callback);
            if (this.mRootScrollCaptureCallbacks.isEmpty()) {
                this.mRootScrollCaptureCallbacks = null;
            }
        }
    }

    public void dispatchScrollCaptureRequest(IScrollCaptureResponseListener listener) {
        this.mHandler.obtainMessage(33, listener).sendToTarget();
    }

    private void collectRootScrollCaptureTargets(ScrollCaptureSearchResults results) {
        HashSet<ScrollCaptureCallback> hashSet = this.mRootScrollCaptureCallbacks;
        if (hashSet == null) {
            return;
        }
        Iterator<ScrollCaptureCallback> it = hashSet.iterator();
        while (it.hasNext()) {
            ScrollCaptureCallback cb = it.next();
            Point offset = new Point(this.mView.getLeft(), this.mView.getTop());
            Rect rect = new Rect(0, 0, this.mView.getWidth(), this.mView.getHeight());
            results.addTarget(new ScrollCaptureTarget(this.mView, rect, offset, cb));
        }
    }

    public void setScrollCaptureRequestTimeout(int timeMillis) {
        this.mScrollCaptureRequestTimeout = timeMillis;
    }

    public long getScrollCaptureRequestTimeout() {
        return this.mScrollCaptureRequestTimeout;
    }

    public void handleScrollCaptureRequest(final IScrollCaptureResponseListener listener) {
        final ScrollCaptureSearchResults results = new ScrollCaptureSearchResults(this.mContext.getMainExecutor());
        collectRootScrollCaptureTargets(results);
        View rootView = getView();
        if (rootView != null) {
            Point point = new Point();
            Rect rect = new Rect(0, 0, rootView.getWidth(), rootView.getHeight());
            getChildVisibleRect(rootView, rect, point);
            Objects.requireNonNull(results);
            rootView.dispatchScrollCaptureSearch(rect, point, new Consumer() { // from class: android.view.ViewRootImpl$$ExternalSyntheticLambda8
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    ScrollCaptureSearchResults.this.addTarget((ScrollCaptureTarget) obj);
                }
            });
        }
        Runnable onComplete = new Runnable() { // from class: android.view.ViewRootImpl$$ExternalSyntheticLambda9
            @Override // java.lang.Runnable
            public final void run() {
                ViewRootImpl.this.lambda$handleScrollCaptureRequest$10(listener, results);
            }
        };
        results.setOnCompleteListener(onComplete);
        if (!results.isComplete()) {
            ViewRootHandler viewRootHandler = this.mHandler;
            Objects.requireNonNull(results);
            viewRootHandler.postDelayed(new Runnable() { // from class: android.view.ViewRootImpl$$ExternalSyntheticLambda10
                @Override // java.lang.Runnable
                public final void run() {
                    ScrollCaptureSearchResults.this.finish();
                }
            }, getScrollCaptureRequestTimeout());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: dispatchScrollCaptureSearchResponse */
    public void lambda$handleScrollCaptureRequest$10(IScrollCaptureResponseListener listener, ScrollCaptureSearchResults results) {
        ScrollCaptureTarget selectedTarget = results.getTopResult();
        ScrollCaptureResponse.Builder response = new ScrollCaptureResponse.Builder();
        response.setWindowTitle(getTitle().toString());
        response.setPackageName(this.mContext.getPackageName());
        StringWriter writer = new StringWriter();
        IndentingPrintWriter pw = new IndentingPrintWriter(writer);
        results.dump(pw);
        pw.flush();
        response.addMessage(writer.toString());
        if (selectedTarget == null) {
            response.setDescription("No scrollable targets found in window");
            try {
                listener.onScrollCaptureResponse(response.build());
                return;
            } catch (RemoteException e) {
                Log.m109e(TAG, "Failed to send scroll capture search result", e);
                return;
            }
        }
        response.setDescription("Connected");
        Rect boundsInWindow = new Rect();
        View containingView = selectedTarget.getContainingView();
        containingView.getLocationInWindow(this.mAttachInfo.mTmpLocation);
        boundsInWindow.set(selectedTarget.getScrollBounds());
        boundsInWindow.offset(this.mAttachInfo.mTmpLocation[0], this.mAttachInfo.mTmpLocation[1]);
        response.setBoundsInWindow(boundsInWindow);
        Rect boundsOnScreen = new Rect();
        this.mView.getLocationOnScreen(this.mAttachInfo.mTmpLocation);
        boundsOnScreen.set(0, 0, this.mView.getWidth(), this.mView.getHeight());
        boundsOnScreen.offset(this.mAttachInfo.mTmpLocation[0], this.mAttachInfo.mTmpLocation[1]);
        response.setWindowBounds(boundsOnScreen);
        ScrollCaptureConnection connection = new ScrollCaptureConnection(this.mView.getContext().getMainExecutor(), selectedTarget);
        response.setConnection(connection);
        try {
            listener.onScrollCaptureResponse(response.build());
        } catch (RemoteException e2) {
            connection.close();
        }
    }

    private void reportNextDraw(String reason) {
        this.mReportNextDraw = true;
        this.mLastReportNextDrawReason = reason;
    }

    public void setReportNextDraw(boolean syncBuffer, String reason) {
        this.mSyncBuffer = syncBuffer;
        reportNextDraw(reason);
        invalidate();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void changeCanvasOpacity(boolean opaque) {
        Log.m112d(this.mTag, "changeCanvasOpacity: opaque=" + opaque);
        boolean opaque2 = opaque & ((this.mView.mPrivateFlags & 512) == 0);
        if (this.mAttachInfo.mThreadedRenderer != null) {
            this.mAttachInfo.mThreadedRenderer.setOpaque(opaque2);
        }
    }

    public boolean dispatchUnhandledKeyEvent(KeyEvent event) {
        return this.mUnhandledKeyManager.dispatch(this.mView, event);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes4.dex */
    public class TakenSurfaceHolder extends BaseSurfaceHolder {
        TakenSurfaceHolder() {
        }

        @Override // com.android.internal.view.BaseSurfaceHolder
        public boolean onAllowLockCanvas() {
            return ViewRootImpl.this.mDrawingAllowed;
        }

        @Override // com.android.internal.view.BaseSurfaceHolder
        public void onRelayoutContainer() {
        }

        @Override // com.android.internal.view.BaseSurfaceHolder, android.view.SurfaceHolder
        public void setFormat(int format) {
            ((RootViewSurfaceTaker) ViewRootImpl.this.mView).setSurfaceFormat(format);
        }

        @Override // com.android.internal.view.BaseSurfaceHolder, android.view.SurfaceHolder
        public void setType(int type) {
            ((RootViewSurfaceTaker) ViewRootImpl.this.mView).setSurfaceType(type);
        }

        @Override // com.android.internal.view.BaseSurfaceHolder
        public void onUpdateSurface() {
            throw new IllegalStateException("Shouldn't be here");
        }

        @Override // android.view.SurfaceHolder
        public boolean isCreating() {
            return ViewRootImpl.this.mIsCreating;
        }

        @Override // com.android.internal.view.BaseSurfaceHolder, android.view.SurfaceHolder
        public void setFixedSize(int width, int height) {
            throw new UnsupportedOperationException("Currently only support sizing from layout");
        }

        @Override // android.view.SurfaceHolder
        public void setKeepScreenOn(boolean screenOn) {
            ((RootViewSurfaceTaker) ViewRootImpl.this.mView).setSurfaceKeepScreenOn(screenOn);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: android.view.ViewRootImpl$W */
    /* loaded from: classes4.dex */
    public static class BinderC3573W extends IWindow.Stub {
        private final WeakReference<ViewRootImpl> mViewAncestor;
        private final IWindowSession mWindowSession;

        BinderC3573W(ViewRootImpl viewAncestor) {
            this.mViewAncestor = new WeakReference<>(viewAncestor);
            this.mWindowSession = viewAncestor.mWindowSession;
        }

        @Override // android.view.IWindow
        public void resized(ClientWindowFrames frames, boolean reportDraw, MergedConfiguration mergedConfiguration, InsetsState insetsState, boolean forceLayout, boolean alwaysConsumeSystemBars, int displayId, int syncSeqId, boolean dragResizing) {
            ViewRootImpl viewAncestor = this.mViewAncestor.get();
            if (viewAncestor != null) {
                viewAncestor.dispatchResized(frames, reportDraw, mergedConfiguration, insetsState, forceLayout, alwaysConsumeSystemBars, displayId, syncSeqId, dragResizing);
            }
        }

        @Override // android.view.IWindow
        public void insetsControlChanged(InsetsState insetsState, InsetsSourceControl[] activeControls) {
            ViewRootImpl viewAncestor = this.mViewAncestor.get();
            if (viewAncestor != null) {
                viewAncestor.dispatchInsetsControlChanged(insetsState, activeControls);
            }
        }

        @Override // android.view.IWindow
        public void showInsets(int types, boolean fromIme, ImeTracker.Token statsToken) {
            ViewRootImpl viewAncestor = this.mViewAncestor.get();
            if (fromIme) {
                ImeTracing.getInstance().triggerClientDump("ViewRootImpl.W#showInsets", viewAncestor.getInsetsController().getHost().getInputMethodManager(), null);
            }
            if (viewAncestor != null) {
                ImeTracker.forLogging().onProgress(statsToken, 28);
                viewAncestor.showInsets(types, fromIme, statsToken);
                return;
            }
            ImeTracker.forLogging().onFailed(statsToken, 28);
        }

        @Override // android.view.IWindow
        public void hideInsets(int types, boolean fromIme, ImeTracker.Token statsToken) {
            ViewRootImpl viewAncestor = this.mViewAncestor.get();
            if (fromIme) {
                ImeTracing.getInstance().triggerClientDump("ViewRootImpl.W#hideInsets", viewAncestor.getInsetsController().getHost().getInputMethodManager(), null);
            }
            if (viewAncestor != null) {
                ImeTracker.forLogging().onProgress(statsToken, 29);
                viewAncestor.hideInsets(types, fromIme, statsToken);
                return;
            }
            ImeTracker.forLogging().onFailed(statsToken, 29);
        }

        @Override // android.view.IWindow
        public void moved(int newX, int newY) {
            ViewRootImpl viewAncestor = this.mViewAncestor.get();
            if (viewAncestor != null) {
                viewAncestor.dispatchMoved(newX, newY);
            }
        }

        @Override // android.view.IWindow
        public void dispatchAppVisibility(boolean visible) {
            ViewRootImpl viewAncestor = this.mViewAncestor.get();
            if (viewAncestor != null) {
                viewAncestor.dispatchAppVisibility(visible);
            }
        }

        @Override // android.view.IWindow
        public void dispatchGetNewSurface() {
            ViewRootImpl viewAncestor = this.mViewAncestor.get();
            if (viewAncestor != null) {
                viewAncestor.dispatchGetNewSurface();
            }
        }

        private static int checkCallingPermission(String permission) {
            try {
                return ActivityManager.getService().checkPermission(permission, Binder.getCallingPid(), Binder.getCallingUid());
            } catch (RemoteException e) {
                return -1;
            }
        }

        @Override // android.view.IWindow
        public void executeCommand(String command, String parameters, ParcelFileDescriptor out) {
            View view;
            ViewRootImpl viewAncestor = this.mViewAncestor.get();
            if (viewAncestor != null && (view = viewAncestor.mView) != null) {
                if (checkCallingPermission(Manifest.C0000permission.DUMP) != 0) {
                    throw new SecurityException("Insufficient permissions to invoke executeCommand() from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid());
                }
                OutputStream clientStream = null;
                try {
                    try {
                        try {
                            clientStream = new ParcelFileDescriptor.AutoCloseOutputStream(out);
                            ViewDebug.dispatchCommand(view, command, parameters, clientStream);
                            clientStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                            if (clientStream != null) {
                                clientStream.close();
                            }
                        }
                    } catch (Throwable th) {
                        if (clientStream != null) {
                            try {
                                clientStream.close();
                            } catch (IOException e2) {
                                e2.printStackTrace();
                            }
                        }
                        throw th;
                    }
                } catch (IOException e3) {
                    e3.printStackTrace();
                }
            }
        }

        @Override // android.view.IWindow
        public void closeSystemDialogs(String reason) {
            ViewRootImpl viewAncestor = this.mViewAncestor.get();
            if (viewAncestor != null) {
                viewAncestor.dispatchCloseSystemDialogs(reason);
            }
        }

        @Override // android.view.IWindow
        public void dispatchWallpaperOffsets(float x, float y, float xStep, float yStep, float zoom, boolean sync) {
            if (sync) {
                try {
                    this.mWindowSession.wallpaperOffsetsComplete(asBinder());
                } catch (RemoteException e) {
                }
            }
        }

        @Override // android.view.IWindow
        public void dispatchWallpaperCommand(String action, int x, int y, int z, Bundle extras, boolean sync) {
            if (sync) {
                try {
                    this.mWindowSession.wallpaperCommandComplete(asBinder(), null);
                } catch (RemoteException e) {
                }
            }
        }

        @Override // android.view.IWindow
        public void dispatchDragEvent(DragEvent event) {
            ViewRootImpl viewAncestor = this.mViewAncestor.get();
            if (viewAncestor != null) {
                viewAncestor.dispatchDragEvent(event);
            }
        }

        @Override // android.view.IWindow
        public void updatePointerIcon(float x, float y) {
            ViewRootImpl viewAncestor = this.mViewAncestor.get();
            if (viewAncestor != null) {
                viewAncestor.updatePointerIcon(x, y);
            }
        }

        @Override // android.view.IWindow
        public void dispatchWindowShown() {
            ViewRootImpl viewAncestor = this.mViewAncestor.get();
            if (viewAncestor != null) {
                viewAncestor.dispatchWindowShown();
            }
        }

        @Override // android.view.IWindow
        public void requestAppKeyboardShortcuts(IResultReceiver receiver, int deviceId) {
            ViewRootImpl viewAncestor = this.mViewAncestor.get();
            if (viewAncestor != null) {
                viewAncestor.dispatchRequestKeyboardShortcuts(receiver, deviceId);
            }
        }

        @Override // android.view.IWindow
        public void requestScrollCapture(IScrollCaptureResponseListener listener) {
            ViewRootImpl viewAncestor = this.mViewAncestor.get();
            if (viewAncestor != null) {
                viewAncestor.dispatchScrollCaptureRequest(listener);
            }
        }
    }

    /* loaded from: classes4.dex */
    public static final class CalledFromWrongThreadException extends AndroidRuntimeException {
        public CalledFromWrongThreadException(String msg) {
            super(msg);
        }
    }

    static HandlerActionQueue getRunQueue() {
        ThreadLocal<HandlerActionQueue> threadLocal = sRunQueues;
        HandlerActionQueue rq = threadLocal.get();
        if (rq != null) {
            return rq;
        }
        HandlerActionQueue rq2 = new HandlerActionQueue();
        threadLocal.set(rq2);
        return rq2;
    }

    private void startDragResizing(Rect initialBounds, boolean fullscreen, Rect systemInsets, Rect stableInsets) {
        if (!this.mDragResizing) {
            this.mDragResizing = true;
            if (this.mUseMTRenderer) {
                for (int i = this.mWindowCallbacks.size() - 1; i >= 0; i--) {
                    this.mWindowCallbacks.get(i).onWindowDragResizeStart(initialBounds, fullscreen, systemInsets, stableInsets);
                }
            }
            this.mFullRedrawNeeded = true;
        }
    }

    private void endDragResizing() {
        if (this.mDragResizing) {
            this.mDragResizing = false;
            if (this.mUseMTRenderer) {
                for (int i = this.mWindowCallbacks.size() - 1; i >= 0; i--) {
                    this.mWindowCallbacks.get(i).onWindowDragResizeEnd();
                }
            }
            this.mFullRedrawNeeded = true;
        }
    }

    private boolean updateContentDrawBounds() {
        boolean updated = false;
        boolean z = true;
        if (this.mUseMTRenderer) {
            for (int i = this.mWindowCallbacks.size() - 1; i >= 0; i--) {
                updated |= this.mWindowCallbacks.get(i).onContentDrawn(this.mWindowAttributes.surfaceInsets.left, this.mWindowAttributes.surfaceInsets.top, this.mWidth, this.mHeight);
            }
        }
        return updated | ((this.mDragResizing && this.mReportNextDraw) ? false : false);
    }

    private void requestDrawWindow() {
        if (!this.mUseMTRenderer) {
            return;
        }
        if (this.mReportNextDraw) {
            this.mWindowDrawCountDown = new CountDownLatch(this.mWindowCallbacks.size());
        }
        for (int i = this.mWindowCallbacks.size() - 1; i >= 0; i--) {
            this.mWindowCallbacks.get(i).onRequestDraw(this.mReportNextDraw);
        }
    }

    public SurfaceControl getSurfaceControl() {
        return this.mSurfaceControl;
    }

    public IBinder getInputToken() {
        WindowInputEventReceiver windowInputEventReceiver = this.mInputEventReceiver;
        if (windowInputEventReceiver == null) {
            return null;
        }
        return windowInputEventReceiver.getToken();
    }

    public IBinder getWindowToken() {
        return this.mAttachInfo.mWindowToken;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes4.dex */
    public final class AccessibilityInteractionConnectionManager implements AccessibilityManager.AccessibilityStateChangeListener {
        private int mDirectConnectionId = -1;

        AccessibilityInteractionConnectionManager() {
        }

        @Override // android.view.accessibility.AccessibilityManager.AccessibilityStateChangeListener
        public void onAccessibilityStateChanged(boolean enabled) {
            if (enabled) {
                ensureConnection();
                ViewRootImpl.this.setAccessibilityWindowAttributesIfNeeded();
                if (ViewRootImpl.this.mAttachInfo.mHasWindowFocus && ViewRootImpl.this.mView != null) {
                    ViewRootImpl.this.mView.sendAccessibilityEvent(32);
                    View focusedView = ViewRootImpl.this.mView.findFocus();
                    if (focusedView != null && focusedView != ViewRootImpl.this.mView) {
                        focusedView.sendAccessibilityEvent(8);
                    }
                }
                if (ViewRootImpl.this.mAttachInfo.mLeashedParentToken != null) {
                    ViewRootImpl.this.mAccessibilityManager.associateEmbeddedHierarchy(ViewRootImpl.this.mAttachInfo.mLeashedParentToken, ViewRootImpl.this.mLeashToken);
                    return;
                }
                return;
            }
            ensureNoConnection();
            ViewRootImpl.this.mHandler.obtainMessage(21).sendToTarget();
        }

        public void ensureConnection() {
            boolean registered = ViewRootImpl.this.mAttachInfo.mAccessibilityWindowId != -1;
            if (!registered) {
                ViewRootImpl.this.mAttachInfo.mAccessibilityWindowId = ViewRootImpl.this.mAccessibilityManager.addAccessibilityInteractionConnection(ViewRootImpl.this.mWindow, ViewRootImpl.this.mLeashToken, ViewRootImpl.this.mContext.getPackageName(), new AccessibilityInteractionConnection(ViewRootImpl.this));
            }
        }

        public void ensureNoConnection() {
            boolean registered = ViewRootImpl.this.mAttachInfo.mAccessibilityWindowId != -1;
            if (registered) {
                ViewRootImpl.this.mAttachInfo.mAccessibilityWindowId = -1;
                ViewRootImpl.this.mAccessibilityWindowAttributes = null;
                ViewRootImpl.this.mAccessibilityManager.removeAccessibilityInteractionConnection(ViewRootImpl.this.mWindow);
            }
        }

        public int ensureDirectConnection() {
            if (this.mDirectConnectionId == -1) {
                this.mDirectConnectionId = AccessibilityInteractionClient.addDirectConnection(new AccessibilityInteractionConnection(ViewRootImpl.this), ViewRootImpl.this.mAccessibilityManager);
                ViewRootImpl.this.mAccessibilityManager.notifyAccessibilityStateChanged();
            }
            return this.mDirectConnectionId;
        }

        public void ensureNoDirectConnection() {
            int i = this.mDirectConnectionId;
            if (i != -1) {
                AccessibilityInteractionClient.removeConnection(i);
                this.mDirectConnectionId = -1;
                ViewRootImpl.this.mAccessibilityManager.notifyAccessibilityStateChanged();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes4.dex */
    public final class HighContrastTextManager implements AccessibilityManager.HighTextContrastChangeListener {
        HighContrastTextManager() {
            ThreadedRenderer.setHighContrastText(ViewRootImpl.this.mAccessibilityManager.isHighTextContrastEnabled());
        }

        @Override // android.view.accessibility.AccessibilityManager.HighTextContrastChangeListener
        public void onHighTextContrastStateChanged(boolean enabled) {
            ThreadedRenderer.setHighContrastText(enabled);
            ViewRootImpl.this.destroyHardwareResources();
            ViewRootImpl.this.invalidate();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes4.dex */
    public static final class AccessibilityInteractionConnection extends IAccessibilityInteractionConnection.Stub {
        private final WeakReference<ViewRootImpl> mViewRootImpl;

        AccessibilityInteractionConnection(ViewRootImpl viewRootImpl) {
            this.mViewRootImpl = new WeakReference<>(viewRootImpl);
        }

        @Override // android.view.accessibility.IAccessibilityInteractionConnection
        public void findAccessibilityNodeInfoByAccessibilityId(long accessibilityNodeId, Region interactiveRegion, int interactionId, IAccessibilityInteractionConnectionCallback callback, int flags, int interrogatingPid, long interrogatingTid, MagnificationSpec spec, float[] matrix, Bundle args) {
            ViewRootImpl viewRootImpl = this.mViewRootImpl.get();
            if (viewRootImpl != null && viewRootImpl.mView != null) {
                viewRootImpl.getAccessibilityInteractionController().findAccessibilityNodeInfoByAccessibilityIdClientThread(accessibilityNodeId, interactiveRegion, interactionId, callback, flags, interrogatingPid, interrogatingTid, spec, matrix, args);
                return;
            }
            try {
                callback.setFindAccessibilityNodeInfosResult(null, interactionId);
            } catch (RemoteException e) {
            }
        }

        @Override // android.view.accessibility.IAccessibilityInteractionConnection
        public void performAccessibilityAction(long accessibilityNodeId, int action, Bundle arguments, int interactionId, IAccessibilityInteractionConnectionCallback callback, int flags, int interrogatingPid, long interrogatingTid) {
            ViewRootImpl viewRootImpl = this.mViewRootImpl.get();
            if (viewRootImpl != null && viewRootImpl.mView != null) {
                viewRootImpl.getAccessibilityInteractionController().performAccessibilityActionClientThread(accessibilityNodeId, action, arguments, interactionId, callback, flags, interrogatingPid, interrogatingTid);
                return;
            }
            try {
                callback.setPerformAccessibilityActionResult(false, interactionId);
            } catch (RemoteException e) {
            }
        }

        @Override // android.view.accessibility.IAccessibilityInteractionConnection
        public void findAccessibilityNodeInfosByViewId(long accessibilityNodeId, String viewId, Region interactiveRegion, int interactionId, IAccessibilityInteractionConnectionCallback callback, int flags, int interrogatingPid, long interrogatingTid, MagnificationSpec spec, float[] matrix) {
            ViewRootImpl viewRootImpl = this.mViewRootImpl.get();
            if (viewRootImpl != null && viewRootImpl.mView != null) {
                viewRootImpl.getAccessibilityInteractionController().findAccessibilityNodeInfosByViewIdClientThread(accessibilityNodeId, viewId, interactiveRegion, interactionId, callback, flags, interrogatingPid, interrogatingTid, spec, matrix);
                return;
            }
            try {
                callback.setFindAccessibilityNodeInfoResult(null, interactionId);
            } catch (RemoteException e) {
            }
        }

        @Override // android.view.accessibility.IAccessibilityInteractionConnection
        public void findAccessibilityNodeInfosByText(long accessibilityNodeId, String text, Region interactiveRegion, int interactionId, IAccessibilityInteractionConnectionCallback callback, int flags, int interrogatingPid, long interrogatingTid, MagnificationSpec spec, float[] matrix) {
            ViewRootImpl viewRootImpl = this.mViewRootImpl.get();
            if (viewRootImpl != null && viewRootImpl.mView != null) {
                viewRootImpl.getAccessibilityInteractionController().findAccessibilityNodeInfosByTextClientThread(accessibilityNodeId, text, interactiveRegion, interactionId, callback, flags, interrogatingPid, interrogatingTid, spec, matrix);
                return;
            }
            try {
                callback.setFindAccessibilityNodeInfosResult(null, interactionId);
            } catch (RemoteException e) {
            }
        }

        @Override // android.view.accessibility.IAccessibilityInteractionConnection
        public void findFocus(long accessibilityNodeId, int focusType, Region interactiveRegion, int interactionId, IAccessibilityInteractionConnectionCallback callback, int flags, int interrogatingPid, long interrogatingTid, MagnificationSpec spec, float[] matrix) {
            ViewRootImpl viewRootImpl = this.mViewRootImpl.get();
            if (viewRootImpl != null && viewRootImpl.mView != null) {
                viewRootImpl.getAccessibilityInteractionController().findFocusClientThread(accessibilityNodeId, focusType, interactiveRegion, interactionId, callback, flags, interrogatingPid, interrogatingTid, spec, matrix);
                return;
            }
            try {
                callback.setFindAccessibilityNodeInfoResult(null, interactionId);
            } catch (RemoteException e) {
            }
        }

        @Override // android.view.accessibility.IAccessibilityInteractionConnection
        public void focusSearch(long accessibilityNodeId, int direction, Region interactiveRegion, int interactionId, IAccessibilityInteractionConnectionCallback callback, int flags, int interrogatingPid, long interrogatingTid, MagnificationSpec spec, float[] matrix) {
            ViewRootImpl viewRootImpl = this.mViewRootImpl.get();
            if (viewRootImpl != null && viewRootImpl.mView != null) {
                viewRootImpl.getAccessibilityInteractionController().focusSearchClientThread(accessibilityNodeId, direction, interactiveRegion, interactionId, callback, flags, interrogatingPid, interrogatingTid, spec, matrix);
                return;
            }
            try {
                callback.setFindAccessibilityNodeInfoResult(null, interactionId);
            } catch (RemoteException e) {
            }
        }

        @Override // android.view.accessibility.IAccessibilityInteractionConnection
        public void clearAccessibilityFocus() {
            ViewRootImpl viewRootImpl = this.mViewRootImpl.get();
            if (viewRootImpl != null && viewRootImpl.mView != null) {
                viewRootImpl.getAccessibilityInteractionController().clearAccessibilityFocusClientThread();
            }
        }

        @Override // android.view.accessibility.IAccessibilityInteractionConnection
        public void notifyOutsideTouch() {
            ViewRootImpl viewRootImpl = this.mViewRootImpl.get();
            if (viewRootImpl != null && viewRootImpl.mView != null) {
                viewRootImpl.getAccessibilityInteractionController().notifyOutsideTouchClientThread();
            }
        }

        @Override // android.view.accessibility.IAccessibilityInteractionConnection
        public void takeScreenshotOfWindow(int interactionId, ScreenCapture.ScreenCaptureListener listener, IAccessibilityInteractionConnectionCallback callback) {
            ViewRootImpl viewRootImpl = this.mViewRootImpl.get();
            if (viewRootImpl != null && viewRootImpl.mView != null) {
                viewRootImpl.getAccessibilityInteractionController().takeScreenshotOfWindowClientThread(interactionId, listener, callback);
                return;
            }
            try {
                callback.sendTakeScreenshotOfWindowError(1, interactionId);
            } catch (RemoteException e) {
            }
        }

        @Override // android.view.accessibility.IAccessibilityInteractionConnection
        public void attachAccessibilityOverlayToWindow(SurfaceControl sc) {
            ViewRootImpl viewRootImpl = this.mViewRootImpl.get();
            if (viewRootImpl != null) {
                viewRootImpl.getAccessibilityInteractionController().attachAccessibilityOverlayToWindowClientThread(sc);
            }
        }
    }

    public IAccessibilityEmbeddedConnection getAccessibilityEmbeddedConnection() {
        if (this.mAccessibilityEmbeddedConnection == null) {
            this.mAccessibilityEmbeddedConnection = new AccessibilityEmbeddedConnection(this);
        }
        return this.mAccessibilityEmbeddedConnection;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public class SendWindowContentChangedAccessibilityEvent implements Runnable {
        public OptionalInt mAction;
        private int mChangeTypes;
        public long mLastEventTimeMillis;
        public StackTraceElement[] mOrigin;
        public View mSource;

        private SendWindowContentChangedAccessibilityEvent() {
            this.mChangeTypes = 0;
            this.mAction = OptionalInt.empty();
        }

        @Override // java.lang.Runnable
        public void run() {
            View source = this.mSource;
            this.mSource = null;
            if (source == null) {
                Log.m110e(ViewRootImpl.TAG, "Accessibility content change has no source");
                return;
            }
            if (AccessibilityManager.getInstance(ViewRootImpl.this.mContext).isEnabled()) {
                this.mLastEventTimeMillis = SystemClock.uptimeMillis();
                AccessibilityEvent event = AccessibilityEvent.obtain();
                event.setEventType(2048);
                event.setContentChangeTypes(this.mChangeTypes);
                if (this.mAction.isPresent()) {
                    event.setAction(this.mAction.getAsInt());
                }
                source.sendAccessibilityEventUnchecked(event);
            } else {
                this.mLastEventTimeMillis = 0L;
            }
            source.resetSubtreeAccessibilityStateChanged();
            this.mChangeTypes = 0;
            this.mAction = OptionalInt.empty();
        }

        public void runOrPost(View source, int changeType) {
            if (ViewRootImpl.this.mHandler.getLooper() != Looper.myLooper()) {
                CalledFromWrongThreadException e = new CalledFromWrongThreadException("Only the original thread that created a view hierarchy can touch its views.");
                Log.m109e(ViewRootImpl.TAG, "Accessibility content change on non-UI thread. Future Android versions will throw an exception.", e);
                ViewRootImpl.this.mHandler.removeCallbacks(this);
                if (this.mSource != null) {
                    run();
                }
            }
            View view = this.mSource;
            if (view != null) {
                View predecessor = ViewRootImpl.this.getCommonPredecessor(view, source);
                if (predecessor != null) {
                    predecessor = predecessor.getSelfOrParentImportantForA11y();
                }
                this.mSource = predecessor != null ? predecessor : source;
                this.mChangeTypes |= changeType;
                int performingAction = ViewRootImpl.this.mAccessibilityManager.getPerformingAction();
                if (performingAction != 0) {
                    if (this.mAction.isEmpty()) {
                        this.mAction = OptionalInt.of(performingAction);
                        return;
                    } else if (this.mAction.getAsInt() != performingAction) {
                        this.mAction = OptionalInt.of(0);
                        return;
                    } else {
                        return;
                    }
                }
                return;
            }
            this.mSource = source;
            this.mChangeTypes = changeType;
            if (ViewRootImpl.this.mAccessibilityManager.getPerformingAction() != 0) {
                this.mAction = OptionalInt.of(ViewRootImpl.this.mAccessibilityManager.getPerformingAction());
            }
            long timeSinceLastMillis = SystemClock.uptimeMillis() - this.mLastEventTimeMillis;
            long minEventIntevalMillis = ViewConfiguration.getSendRecurringAccessibilityEventsInterval();
            if (timeSinceLastMillis >= minEventIntevalMillis) {
                removeCallbacksAndRun();
            } else {
                ViewRootImpl.this.mHandler.postDelayed(this, minEventIntevalMillis - timeSinceLastMillis);
            }
        }

        public void removeCallbacksAndRun() {
            ViewRootImpl.this.mHandler.removeCallbacks(this);
            run();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public static class UnhandledKeyManager {
        private final SparseArray<WeakReference<View>> mCapturedKeys;
        private WeakReference<View> mCurrentReceiver;
        private boolean mDispatched;

        private UnhandledKeyManager() {
            this.mDispatched = true;
            this.mCapturedKeys = new SparseArray<>();
            this.mCurrentReceiver = null;
        }

        boolean dispatch(View root, KeyEvent event) {
            if (this.mDispatched) {
                return false;
            }
            try {
                Trace.traceBegin(8L, "UnhandledKeyEvent dispatch");
                this.mDispatched = true;
                View consumer = root.dispatchUnhandledKeyEvent(event);
                if (event.getAction() == 0) {
                    int keycode = event.getKeyCode();
                    if (consumer != null && !KeyEvent.isModifierKey(keycode)) {
                        this.mCapturedKeys.put(keycode, new WeakReference<>(consumer));
                    }
                }
                return consumer != null;
            } finally {
                Trace.traceEnd(8L);
            }
        }

        void preDispatch(KeyEvent event) {
            int idx;
            this.mCurrentReceiver = null;
            if (event.getAction() == 1 && (idx = this.mCapturedKeys.indexOfKey(event.getKeyCode())) >= 0) {
                this.mCurrentReceiver = this.mCapturedKeys.valueAt(idx);
                this.mCapturedKeys.removeAt(idx);
            }
        }

        boolean preViewDispatch(KeyEvent event) {
            this.mDispatched = false;
            if (this.mCurrentReceiver == null) {
                this.mCurrentReceiver = this.mCapturedKeys.get(event.getKeyCode());
            }
            WeakReference<View> weakReference = this.mCurrentReceiver;
            if (weakReference == null) {
                return false;
            }
            View target = weakReference.get();
            if (event.getAction() == 1) {
                this.mCurrentReceiver = null;
            }
            if (target != null && target.isAttachedToWindow()) {
                target.onUnhandledKeyEvent(event);
            }
            return true;
        }
    }

    public void setDisplayDecoration(boolean displayDecoration) {
        if (displayDecoration == this.mDisplayDecorationCached) {
            return;
        }
        this.mDisplayDecorationCached = displayDecoration;
        if (this.mSurfaceControl.isValid()) {
            updateDisplayDecoration();
        }
    }

    private void updateDisplayDecoration() {
        this.mTransaction.setDisplayDecoration(this.mSurfaceControl, this.mDisplayDecorationCached).apply();
    }

    public void dispatchBlurRegions(float[][] regionCopy, long frameNumber) {
        BLASTBufferQueue bLASTBufferQueue;
        SurfaceControl surfaceControl = getSurfaceControl();
        if (!surfaceControl.isValid()) {
            return;
        }
        SurfaceControl.Transaction transaction = new SurfaceControl.Transaction();
        transaction.setBlurRegions(surfaceControl, regionCopy);
        if (useBLAST() && (bLASTBufferQueue = this.mBlastBufferQueue) != null) {
            bLASTBufferQueue.mergeWithNextTransaction(transaction, frameNumber);
        }
    }

    public BackgroundBlurDrawable createBackgroundBlurDrawable() {
        return this.mBlurRegionAggregator.createBackgroundBlurDrawable(this.mContext);
    }

    @Override // android.view.ViewParent
    public void onDescendantUnbufferedRequested() {
        this.mUnbufferedInputSource = this.mView.mUnbufferedInputSource;
    }

    void forceDisableBLAST() {
        this.mForceDisableBLAST = true;
    }

    boolean useBLAST() {
        return this.mUseBLASTAdapter && !this.mForceDisableBLAST;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getSurfaceSequenceId() {
        return this.mSurfaceSequenceId;
    }

    /* renamed from: mergeWithNextTransaction */
    public void lambda$applyTransactionOnDraw$11(SurfaceControl.Transaction t, long frameNumber) {
        BLASTBufferQueue bLASTBufferQueue = this.mBlastBufferQueue;
        if (bLASTBufferQueue != null) {
            bLASTBufferQueue.mergeWithNextTransaction(t, frameNumber);
        } else {
            t.apply();
        }
    }

    @Override // android.view.AttachedSurfaceControl
    public SurfaceControl.Transaction buildReparentTransaction(SurfaceControl child) {
        if (this.mSurfaceControl.isValid()) {
            return new SurfaceControl.Transaction().reparent(child, getBoundsLayer());
        }
        return null;
    }

    @Override // android.view.AttachedSurfaceControl
    public boolean applyTransactionOnDraw(final SurfaceControl.Transaction t) {
        if (this.mRemoved || !isHardwareEnabled()) {
            t.apply();
        } else {
            this.mHasPendingTransactions = true;
            registerRtFrameCallback(new HardwareRenderer.FrameDrawingCallback() { // from class: android.view.ViewRootImpl$$ExternalSyntheticLambda1
                @Override // android.graphics.HardwareRenderer.FrameDrawingCallback
                public final void onFrameDraw(long j) {
                    ViewRootImpl.this.lambda$applyTransactionOnDraw$11(t, j);
                }
            });
        }
        return true;
    }

    @Override // android.view.AttachedSurfaceControl
    public int getBufferTransformHint() {
        return this.mSurfaceControl.getTransformHint();
    }

    @Override // android.view.AttachedSurfaceControl
    public void addOnBufferTransformHintChangedListener(AttachedSurfaceControl.OnBufferTransformHintChangedListener listener) {
        Objects.requireNonNull(listener);
        if (this.mTransformHintListeners.contains(listener)) {
            throw new IllegalArgumentException("attempt to call addOnBufferTransformHintChangedListener() with a previously registered listener");
        }
        this.mTransformHintListeners.add(listener);
    }

    @Override // android.view.AttachedSurfaceControl
    public void removeOnBufferTransformHintChangedListener(AttachedSurfaceControl.OnBufferTransformHintChangedListener listener) {
        Objects.requireNonNull(listener);
        this.mTransformHintListeners.remove(listener);
    }

    private void dispatchTransformHintChanged(int hint) {
        if (this.mTransformHintListeners.isEmpty()) {
            return;
        }
        ArrayList<AttachedSurfaceControl.OnBufferTransformHintChangedListener> listeners = (ArrayList) this.mTransformHintListeners.clone();
        for (int i = 0; i < listeners.size(); i++) {
            AttachedSurfaceControl.OnBufferTransformHintChangedListener listener = listeners.get(i);
            listener.onBufferTransformHintChanged(hint);
        }
    }

    public void requestCompatCameraControl(boolean showControl, boolean transformationApplied, ICompatCameraControlCallback callback) {
        this.mActivityConfigCallback.requestCompatCameraControl(showControl, transformationApplied, callback);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean wasRelayoutRequested() {
        return this.mRelayoutRequested;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void forceWmRelayout() {
        this.mForceNextWindowRelayout = true;
        scheduleTraversals();
    }

    public WindowOnBackInvokedDispatcher getOnBackInvokedDispatcher() {
        return this.mOnBackInvokedDispatcher;
    }

    @Override // android.view.ViewParent
    public OnBackInvokedDispatcher findOnBackInvokedDispatcherForChild(View child, View requester) {
        return getOnBackInvokedDispatcher();
    }

    private void registerBackCallbackOnWindow() {
        this.mOnBackInvokedDispatcher.attachToWindow(this.mWindowSession, this.mWindow);
    }

    private void sendBackKeyEvent(int action) {
        long when = SystemClock.uptimeMillis();
        KeyEvent ev = new KeyEvent(when, when, action, 4, 0, 0, -1, 0, 72, 257);
        enqueueInputEvent(ev);
    }

    private void registerCompatOnBackInvokedCallback() {
        this.mCompatOnBackInvokedCallback = new CompatOnBackInvokedCallback() { // from class: android.view.ViewRootImpl$$ExternalSyntheticLambda17
            @Override // android.window.CompatOnBackInvokedCallback, android.window.OnBackInvokedCallback
            public final void onBackInvoked() {
                ViewRootImpl.this.lambda$registerCompatOnBackInvokedCallback$12();
            }
        };
        if (this.mOnBackInvokedDispatcher.hasImeOnBackInvokedDispatcher()) {
            Log.m112d(TAG, "Skip registering CompatOnBackInvokedCallback on IME dispatcher");
        } else {
            this.mOnBackInvokedDispatcher.registerOnBackInvokedCallback(0, this.mCompatOnBackInvokedCallback);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$registerCompatOnBackInvokedCallback$12() {
        sendBackKeyEvent(0);
        sendBackKeyEvent(1);
    }

    @Override // android.view.AttachedSurfaceControl
    public void setTouchableRegion(Region r) {
        if (r != null) {
            this.mTouchableRegion = new Region(r);
        } else {
            this.mTouchableRegion = null;
        }
        this.mLastGivenInsets.reset();
        requestLayout();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public IWindowSession getWindowSession() {
        return this.mWindowSession;
    }

    private void registerCallbacksForSync(boolean syncBuffer, SurfaceSyncGroup surfaceSyncGroup) {
        if (!isHardwareEnabled()) {
            return;
        }
        this.mAttachInfo.mThreadedRenderer.registerRtFrameCallback(new C35708(surfaceSyncGroup, syncBuffer));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: android.view.ViewRootImpl$8 */
    /* loaded from: classes4.dex */
    public class C35708 implements HardwareRenderer.FrameDrawingCallback {
        final /* synthetic */ SurfaceSyncGroup val$surfaceSyncGroup;
        final /* synthetic */ boolean val$syncBuffer;

        C35708(SurfaceSyncGroup surfaceSyncGroup, boolean z) {
            this.val$surfaceSyncGroup = surfaceSyncGroup;
            this.val$syncBuffer = z;
        }

        @Override // android.graphics.HardwareRenderer.FrameDrawingCallback
        public void onFrameDraw(long frame) {
        }

        @Override // android.graphics.HardwareRenderer.FrameDrawingCallback
        public HardwareRenderer.FrameCommitCallback onFrameDraw(int syncResult, final long frame) {
            if ((syncResult & 6) != 0) {
                this.val$surfaceSyncGroup.addTransaction(ViewRootImpl.this.mBlastBufferQueue.gatherPendingTransactions(frame));
                this.val$surfaceSyncGroup.markSyncReady();
                return null;
            }
            if (this.val$syncBuffer) {
                ViewRootImpl.this.mBlastBufferQueue.syncNextTransaction(new Consumer<SurfaceControl.Transaction>() { // from class: android.view.ViewRootImpl.8.1
                    @Override // java.util.function.Consumer
                    public void accept(SurfaceControl.Transaction transaction) {
                        C35708.this.val$surfaceSyncGroup.addTransaction(transaction);
                        C35708.this.val$surfaceSyncGroup.markSyncReady();
                    }
                });
            }
            final SurfaceSyncGroup surfaceSyncGroup = this.val$surfaceSyncGroup;
            final boolean z = this.val$syncBuffer;
            return new HardwareRenderer.FrameCommitCallback() { // from class: android.view.ViewRootImpl$8$$ExternalSyntheticLambda0
                @Override // android.graphics.HardwareRenderer.FrameCommitCallback
                public final void onFrameCommit(boolean z2) {
                    ViewRootImpl.C35708.this.lambda$onFrameDraw$0(frame, surfaceSyncGroup, z, z2);
                }
            };
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onFrameDraw$0(long frame, SurfaceSyncGroup surfaceSyncGroup, boolean syncBuffer, boolean didProduceBuffer) {
            if (!didProduceBuffer) {
                ViewRootImpl.this.mBlastBufferQueue.syncNextTransaction(null);
                surfaceSyncGroup.addTransaction(ViewRootImpl.this.mBlastBufferQueue.gatherPendingTransactions(frame));
                surfaceSyncGroup.markSyncReady();
            } else if (!syncBuffer) {
                surfaceSyncGroup.markSyncReady();
            }
        }
    }

    @Override // android.view.AttachedSurfaceControl
    public SurfaceSyncGroup getOrCreateSurfaceSyncGroup() {
        boolean newSyncGroup = false;
        if (this.mActiveSurfaceSyncGroup == null) {
            SurfaceSyncGroup surfaceSyncGroup = new SurfaceSyncGroup(this.mTag);
            this.mActiveSurfaceSyncGroup = surfaceSyncGroup;
            surfaceSyncGroup.setAddedToSyncListener(new Runnable() { // from class: android.view.ViewRootImpl$$ExternalSyntheticLambda3
                @Override // java.lang.Runnable
                public final void run() {
                    ViewRootImpl.this.lambda$getOrCreateSurfaceSyncGroup$14();
                }
            });
            updateSyncInProgressCount(this.mActiveSurfaceSyncGroup);
            newSyncGroup = true;
        }
        Trace.instant(8L, "getOrCreateSurfaceSyncGroup isNew=" + newSyncGroup + " " + this.mTag);
        this.mNumPausedForSync++;
        return this.mActiveSurfaceSyncGroup;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$getOrCreateSurfaceSyncGroup$14() {
        Runnable runnable = new Runnable() { // from class: android.view.ViewRootImpl$$ExternalSyntheticLambda14
            @Override // java.lang.Runnable
            public final void run() {
                ViewRootImpl.this.lambda$getOrCreateSurfaceSyncGroup$13();
            }
        };
        if (Thread.currentThread() == this.mThread) {
            runnable.run();
        } else {
            this.mHandler.post(runnable);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$getOrCreateSurfaceSyncGroup$13() {
        int i = this.mNumPausedForSync - 1;
        this.mNumPausedForSync = i;
        if (!this.mIsInTraversal && i == 0) {
            scheduleTraversals();
        }
    }

    private void updateSyncInProgressCount(SurfaceSyncGroup syncGroup) {
        if (this.mAttachInfo.mThreadedRenderer == null) {
            return;
        }
        synchronized (sSyncProgressLock) {
            int i = sNumSyncsInProgress;
            sNumSyncsInProgress = i + 1;
            if (i == 0) {
                HardwareRenderer.setRtAnimationsEnabled(false);
            }
        }
        syncGroup.addSyncCompleteCallback(this.mSimpleExecutor, new Runnable() { // from class: android.view.ViewRootImpl$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                ViewRootImpl.lambda$updateSyncInProgressCount$15();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ void lambda$updateSyncInProgressCount$15() {
        synchronized (sSyncProgressLock) {
            int i = sNumSyncsInProgress - 1;
            sNumSyncsInProgress = i;
            if (i == 0) {
                HardwareRenderer.setRtAnimationsEnabled(true);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void addToSync(SurfaceSyncGroup syncable) {
        SurfaceSyncGroup surfaceSyncGroup = this.mActiveSurfaceSyncGroup;
        if (surfaceSyncGroup == null) {
            return;
        }
        surfaceSyncGroup.add(syncable, (Runnable) null);
    }

    @Override // android.view.AttachedSurfaceControl
    public void setChildBoundingInsets(Rect insets) {
        if (insets.left < 0 || insets.top < 0 || insets.right < 0 || insets.bottom < 0) {
            throw new IllegalArgumentException("Negative insets passed to setChildBoundingInsets.");
        }
        this.mChildBoundingInsets.set(insets);
        this.mChildBoundingInsetsChanged = true;
        scheduleTraversals();
    }
}
