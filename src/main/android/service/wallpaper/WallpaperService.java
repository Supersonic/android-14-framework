package android.service.wallpaper;

import android.animation.AnimationHandler;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SystemApi;
import android.app.Service;
import android.app.WallpaperColors;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.BLASTBufferQueue;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.hardware.HardwareBuffer;
import android.hardware.display.DisplayManager;
import android.p008os.Bundle;
import android.p008os.Handler;
import android.p008os.IBinder;
import android.p008os.Looper;
import android.p008os.Message;
import android.p008os.Process;
import android.p008os.RemoteException;
import android.p008os.SystemClock;
import android.p008os.SystemProperties;
import android.p008os.Trace;
import android.service.wallpaper.IWallpaperEngine;
import android.service.wallpaper.IWallpaperService;
import android.service.wallpaper.WallpaperService;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.Log;
import android.util.MergedConfiguration;
import android.util.Slog;
import android.view.Display;
import android.view.DisplayCutout;
import android.view.IWindowSession;
import android.view.InputChannel;
import android.view.InputEvent;
import android.view.InputEventReceiver;
import android.view.InsetsSourceControl;
import android.view.InsetsState;
import android.view.MotionEvent;
import android.view.PixelCopy;
import android.view.Surface;
import android.view.SurfaceControl;
import android.view.SurfaceHolder;
import android.view.WindowInsets;
import android.view.WindowLayout;
import android.view.WindowManager;
import android.view.WindowManagerGlobal;
import android.window.ClientWindowFrames;
import android.window.ScreenCapture;
import com.android.internal.C4057R;
import com.android.internal.p028os.HandlerCaller;
import com.android.internal.view.BaseIWindow;
import com.android.internal.view.BaseSurfaceHolder;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;
/* loaded from: classes3.dex */
public abstract class WallpaperService extends Service {
    static final boolean DEBUG = false;
    private static final long DEFAULT_UPDATE_SCREENSHOT_DURATION = 60000;
    private static final long DIMMING_ANIMATION_DURATION_MS = 300;
    private static final int DO_ATTACH = 10;
    private static final int DO_DETACH = 20;
    private static final int DO_IN_AMBIENT_MODE = 50;
    private static final int DO_SET_DESIRED_SIZE = 30;
    private static final int DO_SET_DISPLAY_PADDING = 40;
    private static final int MIN_BITMAP_SCREENSHOT_WIDTH = 64;
    static final float MIN_PAGE_ALLOWED_MARGIN = 0.05f;
    private static final int MSG_REPORT_SHOWN = 10150;
    private static final int MSG_REQUEST_WALLPAPER_COLORS = 10050;
    private static final int MSG_RESIZE_PREVIEW = 10110;
    private static final int MSG_TOUCH_EVENT = 10040;
    private static final int MSG_UPDATE_DIMMING = 10200;
    private static final int MSG_UPDATE_SURFACE = 10000;
    private static final int MSG_VISIBILITY_CHANGED = 10010;
    private static final int MSG_WALLPAPER_COMMAND = 10025;
    private static final int MSG_WALLPAPER_FLAGS_CHANGED = 10210;
    private static final int MSG_WALLPAPER_OFFSETS = 10020;
    private static final int MSG_WINDOW_MOVED = 10035;
    private static final int MSG_WINDOW_RESIZED = 10030;
    private static final int MSG_ZOOM = 10100;
    private static final int NOTIFY_COLORS_RATE_LIMIT_MS = 1000;
    private static final int PROCESS_LOCAL_COLORS_INTERVAL_MS = 1000;
    public static final String SERVICE_INTERFACE = "android.service.wallpaper.WallpaperService";
    public static final String SERVICE_META_DATA = "android.service.wallpaper";
    static final String TAG = "WallpaperService";
    private final ArrayMap<IBinder, IWallpaperEngineWrapper> mActiveEngines = new ArrayMap<>();
    private static final RectF LOCAL_COLOR_BOUNDS = new RectF(0.0f, 0.0f, 1.0f, 1.0f);
    private static final List<Float> PROHIBITED_STEPS = Arrays.asList(Float.valueOf(0.0f), Float.valueOf(Float.POSITIVE_INFINITY), Float.valueOf(Float.NEGATIVE_INFINITY));
    private static final boolean ENABLE_WALLPAPER_DIMMING = SystemProperties.getBoolean("persist.debug.enable_wallpaper_dimming", true);

    public abstract Engine onCreateEngine();

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes3.dex */
    public static final class WallpaperCommand {
        String action;
        Bundle extras;
        boolean sync;

        /* renamed from: x */
        int f438x;

        /* renamed from: y */
        int f439y;

        /* renamed from: z */
        int f440z;

        WallpaperCommand() {
        }
    }

    /* loaded from: classes3.dex */
    public class Engine {
        SurfaceControl mBbqSurfaceControl;
        BLASTBufferQueue mBlastBufferQueue;
        HandlerCaller mCaller;
        private final Supplier<Long> mClockFunction;
        IWallpaperConnection mConnection;
        boolean mCreated;
        int mCurHeight;
        int mCurWidth;
        int mCurWindowFlags;
        int mCurWindowPrivateFlags;
        private float mDefaultDimAmount;
        boolean mDestroyed;
        final Rect mDispatchedContentInsets;
        DisplayCutout mDispatchedDisplayCutout;
        final Rect mDispatchedStableInsets;
        private Display mDisplay;
        private Context mDisplayContext;
        private final DisplayManager.DisplayListener mDisplayListener;
        private int mDisplayState;
        boolean mDrawingAllowed;
        boolean mFixedSizeAllowed;
        int mFormat;
        private boolean mFrozenRequested;
        private final Handler mHandler;
        int mHeight;
        IWallpaperEngineWrapper mIWallpaperEngine;
        boolean mInitializing;
        WallpaperInputEventReceiver mInputEventReceiver;
        final InsetsState mInsetsState;
        boolean mIsCreating;
        boolean mIsInAmbientMode;
        private long mLastColorInvalidation;
        private long mLastProcessLocalColorsTimestamp;
        Bitmap mLastScreenshot;
        private final Point mLastSurfaceSize;
        int mLastWindowPage;
        final WindowManager.LayoutParams mLayout;
        final ArraySet<RectF> mLocalColorAreas;
        final ArraySet<RectF> mLocalColorsToAdd;
        final Object mLock;
        final MergedConfiguration mMergedConfiguration;
        private final Runnable mNotifyColorsChanged;
        boolean mOffsetMessageEnqueued;
        boolean mOffsetsChanged;
        MotionEvent mPendingMove;
        boolean mPendingSync;
        float mPendingXOffset;
        float mPendingXOffsetStep;
        float mPendingYOffset;
        float mPendingYOffsetStep;
        Rect mPreviewSurfacePosition;
        private float mPreviousWallpaperDimAmount;
        private AtomicBoolean mProcessLocalColorsPending;
        boolean mReportedVisible;
        private boolean mResetWindowPages;
        private Point mScreenshotSize;
        private SurfaceControl mScreenshotSurfaceControl;
        IWindowSession mSession;
        boolean mShouldDim;
        boolean mShouldDimByDefault;
        SurfaceControl mSurfaceControl;
        boolean mSurfaceCreated;
        final BaseSurfaceHolder mSurfaceHolder;
        private final Point mSurfaceSize;
        final Bundle mSyncSeqIdBundle;
        final InsetsSourceControl.Array mTempControls;
        private final Matrix mTmpMatrix;
        private final float[] mTmpValues;
        int mType;
        boolean mVisible;
        private float mWallpaperDimAmount;
        int mWidth;
        final ClientWindowFrames mWinFrames;
        final BaseIWindow mWindow;
        int mWindowFlags;
        EngineWindowPage[] mWindowPages;
        int mWindowPrivateFlags;
        IBinder mWindowToken;
        float mZoom;

        /* JADX INFO: Access modifiers changed from: package-private */
        /* loaded from: classes3.dex */
        public final class WallpaperInputEventReceiver extends InputEventReceiver {
            public WallpaperInputEventReceiver(InputChannel inputChannel, Looper looper) {
                super(inputChannel, looper);
            }

            @Override // android.view.InputEventReceiver
            public void onInputEvent(InputEvent event) {
                boolean handled = false;
                try {
                    if ((event instanceof MotionEvent) && (event.getSource() & 2) != 0) {
                        MotionEvent dup = MotionEvent.obtainNoHistory((MotionEvent) event);
                        Engine.this.dispatchPointer(dup);
                        handled = true;
                    }
                } finally {
                    finishInputEvent(event, false);
                }
            }
        }

        public Engine(WallpaperService this$0) {
            this(new Supplier() { // from class: android.service.wallpaper.WallpaperService$Engine$$ExternalSyntheticLambda1
                @Override // java.util.function.Supplier
                public final Object get() {
                    return Long.valueOf(SystemClock.elapsedRealtime());
                }
            }, Handler.getMain());
        }

        public Engine(Supplier<Long> clockFunction, Handler handler) {
            this.mLocalColorAreas = new ArraySet<>(4);
            this.mLocalColorsToAdd = new ArraySet<>(4);
            this.mWindowPages = new EngineWindowPage[0];
            this.mLastWindowPage = -1;
            this.mInitializing = true;
            this.mFrozenRequested = false;
            this.mZoom = 0.0f;
            this.mWindowFlags = 16;
            this.mWindowPrivateFlags = 33554436;
            this.mCurWindowFlags = 16;
            this.mCurWindowPrivateFlags = 33554436;
            this.mWinFrames = new ClientWindowFrames();
            this.mDispatchedContentInsets = new Rect();
            this.mDispatchedStableInsets = new Rect();
            this.mDispatchedDisplayCutout = DisplayCutout.NO_CUTOUT;
            this.mInsetsState = new InsetsState();
            this.mTempControls = new InsetsSourceControl.Array();
            this.mMergedConfiguration = new MergedConfiguration();
            this.mSyncSeqIdBundle = new Bundle();
            this.mSurfaceSize = new Point();
            this.mLastSurfaceSize = new Point();
            this.mTmpMatrix = new Matrix();
            this.mTmpValues = new float[9];
            this.mLayout = new WindowManager.LayoutParams();
            this.mLock = new Object();
            this.mNotifyColorsChanged = new Runnable() { // from class: android.service.wallpaper.WallpaperService$Engine$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    WallpaperService.Engine.this.notifyColorsChanged();
                }
            };
            this.mProcessLocalColorsPending = new AtomicBoolean(false);
            this.mWallpaperDimAmount = WallpaperService.MIN_PAGE_ALLOWED_MARGIN;
            this.mPreviousWallpaperDimAmount = WallpaperService.MIN_PAGE_ALLOWED_MARGIN;
            this.mDefaultDimAmount = WallpaperService.MIN_PAGE_ALLOWED_MARGIN;
            this.mSurfaceControl = new SurfaceControl();
            this.mScreenshotSize = new Point();
            this.mSurfaceHolder = new BaseSurfaceHolder() { // from class: android.service.wallpaper.WallpaperService.Engine.1
                {
                    this.mRequestedFormat = 2;
                }

                @Override // com.android.internal.view.BaseSurfaceHolder
                public boolean onAllowLockCanvas() {
                    return Engine.this.mDrawingAllowed;
                }

                @Override // com.android.internal.view.BaseSurfaceHolder
                public void onRelayoutContainer() {
                    Message msg = Engine.this.mCaller.obtainMessage(10000);
                    Engine.this.mCaller.sendMessage(msg);
                }

                @Override // com.android.internal.view.BaseSurfaceHolder
                public void onUpdateSurface() {
                    Message msg = Engine.this.mCaller.obtainMessage(10000);
                    Engine.this.mCaller.sendMessage(msg);
                }

                @Override // android.view.SurfaceHolder
                public boolean isCreating() {
                    return Engine.this.mIsCreating;
                }

                @Override // com.android.internal.view.BaseSurfaceHolder, android.view.SurfaceHolder
                public void setFixedSize(int width, int height) {
                    if (!Engine.this.mFixedSizeAllowed && !Engine.this.mIWallpaperEngine.mIsPreview) {
                        throw new UnsupportedOperationException("Wallpapers currently only support sizing from layout");
                    }
                    super.setFixedSize(width, height);
                }

                @Override // android.view.SurfaceHolder
                public void setKeepScreenOn(boolean screenOn) {
                    throw new UnsupportedOperationException("Wallpapers do not support keep screen on");
                }

                private void prepareToDraw() {
                    if (Engine.this.mDisplayState == 3 || Engine.this.mDisplayState == 4) {
                        try {
                            Engine.this.mSession.pokeDrawLock(Engine.this.mWindow);
                        } catch (RemoteException e) {
                        }
                    }
                }

                @Override // com.android.internal.view.BaseSurfaceHolder, android.view.SurfaceHolder
                public Canvas lockCanvas() {
                    prepareToDraw();
                    return super.lockCanvas();
                }

                @Override // com.android.internal.view.BaseSurfaceHolder, android.view.SurfaceHolder
                public Canvas lockCanvas(Rect dirty) {
                    prepareToDraw();
                    return super.lockCanvas(dirty);
                }

                @Override // com.android.internal.view.BaseSurfaceHolder, android.view.SurfaceHolder
                public Canvas lockHardwareCanvas() {
                    prepareToDraw();
                    return super.lockHardwareCanvas();
                }
            };
            this.mWindow = new BaseIWindow() { // from class: android.service.wallpaper.WallpaperService.Engine.2
                @Override // com.android.internal.view.BaseIWindow, android.view.IWindow
                public void resized(ClientWindowFrames frames, boolean reportDraw, MergedConfiguration mergedConfiguration, InsetsState insetsState, boolean forceLayout, boolean alwaysConsumeSystemBars, int displayId, int syncSeqId, boolean dragResizing) {
                    Message msg = Engine.this.mCaller.obtainMessageIO(10030, reportDraw ? 1 : 0, mergedConfiguration);
                    Engine.this.mCaller.sendMessage(msg);
                }

                @Override // com.android.internal.view.BaseIWindow, android.view.IWindow
                public void moved(int newX, int newY) {
                    Message msg = Engine.this.mCaller.obtainMessageII(10035, newX, newY);
                    Engine.this.mCaller.sendMessage(msg);
                }

                @Override // com.android.internal.view.BaseIWindow, android.view.IWindow
                public void dispatchAppVisibility(boolean visible) {
                    if (!Engine.this.mIWallpaperEngine.mIsPreview) {
                        Message msg = Engine.this.mCaller.obtainMessageI(10010, visible ? 1 : 0);
                        Engine.this.mCaller.sendMessage(msg);
                    }
                }

                @Override // com.android.internal.view.BaseIWindow, android.view.IWindow
                public void dispatchWallpaperOffsets(float x, float y, float xStep, float yStep, float zoom, boolean sync) {
                    synchronized (Engine.this.mLock) {
                        Engine.this.mPendingXOffset = x;
                        Engine.this.mPendingYOffset = y;
                        Engine.this.mPendingXOffsetStep = xStep;
                        Engine.this.mPendingYOffsetStep = yStep;
                        if (sync) {
                            Engine.this.mPendingSync = true;
                        }
                        if (!Engine.this.mOffsetMessageEnqueued) {
                            Engine.this.mOffsetMessageEnqueued = true;
                            Message msg = Engine.this.mCaller.obtainMessage(10020);
                            Engine.this.mCaller.sendMessage(msg);
                        }
                        Message msg2 = Engine.this.mCaller.obtainMessageI(10100, Float.floatToIntBits(zoom));
                        Engine.this.mCaller.sendMessage(msg2);
                    }
                }

                @Override // com.android.internal.view.BaseIWindow, android.view.IWindow
                public void dispatchWallpaperCommand(String action, int x, int y, int z, Bundle extras, boolean sync) {
                    synchronized (Engine.this.mLock) {
                        WallpaperCommand cmd = new WallpaperCommand();
                        cmd.action = action;
                        cmd.f438x = x;
                        cmd.f439y = y;
                        cmd.f440z = z;
                        cmd.extras = extras;
                        cmd.sync = sync;
                        Message msg = Engine.this.mCaller.obtainMessage(10025);
                        msg.obj = cmd;
                        Engine.this.mCaller.sendMessage(msg);
                    }
                }
            };
            this.mDisplayListener = new DisplayManager.DisplayListener() { // from class: android.service.wallpaper.WallpaperService.Engine.4
                @Override // android.hardware.display.DisplayManager.DisplayListener
                public void onDisplayChanged(int displayId) {
                    if (Engine.this.mDisplay.getDisplayId() == displayId) {
                        boolean forceReport = Engine.this.mDisplay.getState() != 4;
                        Engine.this.reportVisibility(forceReport);
                    }
                }

                @Override // android.hardware.display.DisplayManager.DisplayListener
                public void onDisplayRemoved(int displayId) {
                }

                @Override // android.hardware.display.DisplayManager.DisplayListener
                public void onDisplayAdded(int displayId) {
                }
            };
            this.mClockFunction = clockFunction;
            this.mHandler = handler;
        }

        public SurfaceHolder getSurfaceHolder() {
            return this.mSurfaceHolder;
        }

        public int getWallpaperFlags() {
            return this.mIWallpaperEngine.mWhich;
        }

        public int getDesiredMinimumWidth() {
            return this.mIWallpaperEngine.mReqWidth;
        }

        public int getDesiredMinimumHeight() {
            return this.mIWallpaperEngine.mReqHeight;
        }

        public boolean isVisible() {
            return this.mReportedVisible;
        }

        public boolean supportsLocalColorExtraction() {
            return false;
        }

        public boolean isPreview() {
            return this.mIWallpaperEngine.mIsPreview;
        }

        @SystemApi
        public boolean isInAmbientMode() {
            return this.mIsInAmbientMode;
        }

        public boolean shouldZoomOutWallpaper() {
            return false;
        }

        public boolean shouldWaitForEngineShown() {
            return false;
        }

        public void reportEngineShown(boolean waitForEngineShown) {
            if (this.mIWallpaperEngine.mShownReported) {
                return;
            }
            Trace.beginSection("WPMS.reportEngineShown-" + waitForEngineShown);
            Log.m112d(WallpaperService.TAG, "reportEngineShown: shouldWait=" + waitForEngineShown);
            if (!waitForEngineShown) {
                Message message = this.mCaller.obtainMessage(10150);
                this.mCaller.removeMessages(10150);
                this.mCaller.sendMessage(message);
            } else if (!this.mCaller.hasMessages(10150)) {
                Message message2 = this.mCaller.obtainMessage(10150);
                this.mCaller.sendMessageDelayed(message2, TimeUnit.SECONDS.toMillis(5L));
            }
            Trace.endSection();
        }

        public void setTouchEventsEnabled(boolean enabled) {
            int i;
            if (enabled) {
                i = this.mWindowFlags & (-17);
            } else {
                i = this.mWindowFlags | 16;
            }
            this.mWindowFlags = i;
            if (this.mCreated) {
                updateSurface(false, false, false);
            }
        }

        public void setOffsetNotificationsEnabled(boolean enabled) {
            int i;
            if (enabled) {
                i = this.mWindowPrivateFlags | 4;
            } else {
                i = this.mWindowPrivateFlags & (-5);
            }
            this.mWindowPrivateFlags = i;
            if (this.mCreated) {
                updateSurface(false, false, false);
            }
        }

        public void setShowForAllUsers(boolean show) {
            int i;
            if (show) {
                i = this.mWindowPrivateFlags | 16;
            } else {
                i = this.mWindowPrivateFlags & (-17);
            }
            this.mWindowPrivateFlags = i;
            if (this.mCreated) {
                updateSurface(false, false, false);
            }
        }

        public void setFixedSizeAllowed(boolean allowed) {
            this.mFixedSizeAllowed = allowed;
        }

        public float getZoom() {
            return this.mZoom;
        }

        public void onCreate(SurfaceHolder surfaceHolder) {
        }

        public void onDestroy() {
        }

        public void onVisibilityChanged(boolean visible) {
        }

        public void onApplyWindowInsets(WindowInsets insets) {
        }

        public void onTouchEvent(MotionEvent event) {
        }

        public void onOffsetsChanged(float xOffset, float yOffset, float xOffsetStep, float yOffsetStep, int xPixelOffset, int yPixelOffset) {
        }

        public Bundle onCommand(String action, int x, int y, int z, Bundle extras, boolean resultRequested) {
            return null;
        }

        @SystemApi
        public void onAmbientModeChanged(boolean inAmbientMode, long animationDuration) {
        }

        public void onDesiredSizeChanged(int desiredWidth, int desiredHeight) {
        }

        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        }

        public void onSurfaceRedrawNeeded(SurfaceHolder holder) {
        }

        public void onSurfaceCreated(SurfaceHolder holder) {
        }

        public void onSurfaceDestroyed(SurfaceHolder holder) {
        }

        public void onWallpaperFlagsChanged(int which) {
        }

        public void onZoomChanged(float zoom) {
        }

        public void notifyColorsChanged() {
            long now = this.mClockFunction.get().longValue();
            if (now - this.mLastColorInvalidation < 1000) {
                Log.m104w(WallpaperService.TAG, "This call has been deferred. You should only call notifyColorsChanged() once every 1.0 seconds.");
                if (!this.mHandler.hasCallbacks(this.mNotifyColorsChanged)) {
                    this.mHandler.postDelayed(this.mNotifyColorsChanged, 1000L);
                    return;
                }
                return;
            }
            this.mLastColorInvalidation = now;
            this.mHandler.removeCallbacks(this.mNotifyColorsChanged);
            try {
                WallpaperColors newColors = onComputeColors();
                IWallpaperConnection iWallpaperConnection = this.mConnection;
                if (iWallpaperConnection != null) {
                    iWallpaperConnection.onWallpaperColorsChanged(newColors, this.mDisplay.getDisplayId());
                } else {
                    Log.m104w(WallpaperService.TAG, "Can't notify system because wallpaper connection was not established.");
                }
                this.mResetWindowPages = true;
                processLocalColors(this.mPendingXOffset, this.mPendingXOffsetStep);
            } catch (RemoteException e) {
                Log.m103w(WallpaperService.TAG, "Can't notify system because wallpaper connection was lost.", e);
            }
        }

        public WallpaperColors onComputeColors() {
            return null;
        }

        public void notifyLocalColorsChanged(List<RectF> regions, List<WallpaperColors> colors) throws RuntimeException {
            for (int i = 0; i < regions.size() && i < colors.size(); i++) {
                WallpaperColors color = colors.get(i);
                RectF area = regions.get(i);
                if (color != null && area != null) {
                    try {
                        this.mConnection.onLocalWallpaperColorsChanged(area, color, this.mDisplayContext.getDisplayId());
                    } catch (RemoteException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            WallpaperColors primaryColors = this.mIWallpaperEngine.mWallpaperManager.getWallpaperColors(1);
            setPrimaryWallpaperColors(primaryColors);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setPrimaryWallpaperColors(WallpaperColors colors) {
            if (colors == null) {
                return;
            }
            int colorHints = colors.getColorHints();
            boolean z = (colorHints & 1) == 0 && (colorHints & 2) == 0;
            this.mShouldDimByDefault = z;
            if (z != this.mShouldDim && this.mWallpaperDimAmount == 0.0f) {
                this.mShouldDim = z;
                updateSurfaceDimming();
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void updateWallpaperDimming(float dimAmount) {
            if (dimAmount == this.mWallpaperDimAmount) {
                return;
            }
            this.mWallpaperDimAmount = Math.max(this.mDefaultDimAmount, dimAmount);
            this.mShouldDim = dimAmount != 0.0f || this.mShouldDimByDefault;
            updateSurfaceDimming();
        }

        private void updateSurfaceDimming() {
            if (!WallpaperService.ENABLE_WALLPAPER_DIMMING || this.mBbqSurfaceControl == null) {
                return;
            }
            final SurfaceControl.Transaction surfaceControlTransaction = new SurfaceControl.Transaction();
            if ((!isPreview() && this.mShouldDim) || this.mPreviousWallpaperDimAmount != this.mWallpaperDimAmount) {
                Log.m106v(WallpaperService.TAG, "Setting wallpaper dimming: " + this.mWallpaperDimAmount);
                ValueAnimator animator = ValueAnimator.ofFloat(this.mPreviousWallpaperDimAmount, this.mWallpaperDimAmount);
                animator.setDuration(WallpaperService.DIMMING_ANIMATION_DURATION_MS);
                animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: android.service.wallpaper.WallpaperService$Engine$$ExternalSyntheticLambda3
                    @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                        WallpaperService.Engine.this.lambda$updateSurfaceDimming$0(surfaceControlTransaction, valueAnimator);
                    }
                });
                animator.addListener(new AnimatorListenerAdapter() { // from class: android.service.wallpaper.WallpaperService.Engine.3
                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animation) {
                        Engine.this.updateSurface(false, false, true);
                    }
                });
                animator.start();
            } else {
                Log.m106v(WallpaperService.TAG, "Setting wallpaper dimming: 0");
                surfaceControlTransaction.setAlpha(this.mBbqSurfaceControl, 1.0f).apply();
                updateSurface(false, false, true);
            }
            this.mPreviousWallpaperDimAmount = this.mWallpaperDimAmount;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$updateSurfaceDimming$0(SurfaceControl.Transaction surfaceControlTransaction, ValueAnimator va) {
            float dimValue = ((Float) va.getAnimatedValue()).floatValue();
            SurfaceControl surfaceControl = this.mBbqSurfaceControl;
            if (surfaceControl != null) {
                surfaceControlTransaction.setAlpha(surfaceControl, 1.0f - dimValue).apply();
            }
        }

        public void setCreated(boolean created) {
            this.mCreated = created;
        }

        protected void dump(String prefix, FileDescriptor fd, PrintWriter out, String[] args) {
            out.print(prefix);
            out.print("mInitializing=");
            out.print(this.mInitializing);
            out.print(" mDestroyed=");
            out.println(this.mDestroyed);
            out.print(prefix);
            out.print("mVisible=");
            out.print(this.mVisible);
            out.print(" mReportedVisible=");
            out.println(this.mReportedVisible);
            out.print(prefix);
            out.print("mDisplay=");
            out.println(this.mDisplay);
            out.print(prefix);
            out.print("mCreated=");
            out.print(this.mCreated);
            out.print(" mSurfaceCreated=");
            out.print(this.mSurfaceCreated);
            out.print(" mIsCreating=");
            out.print(this.mIsCreating);
            out.print(" mDrawingAllowed=");
            out.println(this.mDrawingAllowed);
            out.print(prefix);
            out.print("mWidth=");
            out.print(this.mWidth);
            out.print(" mCurWidth=");
            out.print(this.mCurWidth);
            out.print(" mHeight=");
            out.print(this.mHeight);
            out.print(" mCurHeight=");
            out.println(this.mCurHeight);
            out.print(prefix);
            out.print("mType=");
            out.print(this.mType);
            out.print(" mWindowFlags=");
            out.print(this.mWindowFlags);
            out.print(" mCurWindowFlags=");
            out.println(this.mCurWindowFlags);
            out.print(prefix);
            out.print("mWindowPrivateFlags=");
            out.print(this.mWindowPrivateFlags);
            out.print(" mCurWindowPrivateFlags=");
            out.println(this.mCurWindowPrivateFlags);
            out.print(prefix);
            out.println("mWinFrames=");
            out.println(this.mWinFrames);
            out.print(prefix);
            out.print("mConfiguration=");
            out.println(this.mMergedConfiguration.getMergedConfiguration());
            out.print(prefix);
            out.print("mLayout=");
            out.println(this.mLayout);
            out.print(prefix);
            out.print("mZoom=");
            out.println(this.mZoom);
            out.print(prefix);
            out.print("mPreviewSurfacePosition=");
            out.println(this.mPreviewSurfacePosition);
            synchronized (this.mLock) {
                out.print(prefix);
                out.print("mPendingXOffset=");
                out.print(this.mPendingXOffset);
                out.print(" mPendingXOffset=");
                out.println(this.mPendingXOffset);
                out.print(prefix);
                out.print("mPendingXOffsetStep=");
                out.print(this.mPendingXOffsetStep);
                out.print(" mPendingXOffsetStep=");
                out.println(this.mPendingXOffsetStep);
                out.print(prefix);
                out.print("mOffsetMessageEnqueued=");
                out.print(this.mOffsetMessageEnqueued);
                out.print(" mPendingSync=");
                out.println(this.mPendingSync);
                if (this.mPendingMove != null) {
                    out.print(prefix);
                    out.print("mPendingMove=");
                    out.println(this.mPendingMove);
                }
            }
        }

        public void setZoom(float zoom) {
            boolean updated = false;
            synchronized (this.mLock) {
                if (this.mIsInAmbientMode) {
                    this.mZoom = 0.0f;
                }
                if (Float.compare(zoom, this.mZoom) != 0) {
                    this.mZoom = zoom;
                    updated = true;
                }
            }
            if (updated && !this.mDestroyed) {
                onZoomChanged(this.mZoom);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void dispatchPointer(MotionEvent event) {
            if (event.isTouchEvent()) {
                synchronized (this.mLock) {
                    if (event.getAction() == 2) {
                        this.mPendingMove = event;
                    } else {
                        this.mPendingMove = null;
                    }
                }
                Message msg = this.mCaller.obtainMessageO(10040, event);
                this.mCaller.sendMessage(msg);
                return;
            }
            event.recycle();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void updateConfiguration(MergedConfiguration mergedConfiguration) {
            this.mMergedConfiguration.setTo(mergedConfiguration);
        }

        /* JADX WARN: Removed duplicated region for block: B:239:0x0619 A[Catch: RemoteException -> 0x063a, TryCatch #14 {RemoteException -> 0x063a, blocks: (B:225:0x05c0, B:227:0x05c7, B:228:0x05dc, B:237:0x0611, B:239:0x0619, B:240:0x062e, B:241:0x0639), top: B:289:0x047a }] */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
        */
        void updateSurface(boolean forceRelayout, boolean forceReport, boolean redrawNeeded) {
            int myHeight;
            boolean fixedSize;
            boolean sizeChanged;
            boolean creating;
            boolean formatChanged;
            WindowInsets windowInsets;
            int h;
            int h2;
            boolean sizeChanged2;
            boolean redrawNeeded2;
            if (this.mDestroyed) {
                Log.m104w(WallpaperService.TAG, "Ignoring updateSurface due to destroyed");
                return;
            }
            boolean fixedSize2 = false;
            int myWidth = this.mSurfaceHolder.getRequestedWidth();
            if (myWidth <= 0) {
                myWidth = -1;
            } else {
                fixedSize2 = true;
            }
            int myHeight2 = this.mSurfaceHolder.getRequestedHeight();
            if (myHeight2 <= 0) {
                myHeight = -1;
                fixedSize = fixedSize2;
            } else {
                myHeight = myHeight2;
                fixedSize = true;
            }
            boolean fixedSize3 = this.mCreated;
            boolean creating2 = !fixedSize3;
            boolean surfaceCreating = !this.mSurfaceCreated;
            boolean formatChanged2 = this.mFormat != this.mSurfaceHolder.getRequestedFormat();
            boolean sizeChanged3 = (this.mWidth == myWidth && this.mHeight == myHeight) ? false : true;
            boolean insetsChanged = !this.mCreated;
            boolean typeChanged = this.mType != this.mSurfaceHolder.getRequestedType();
            boolean flagsChanged = (this.mCurWindowFlags == this.mWindowFlags && this.mCurWindowPrivateFlags == this.mWindowPrivateFlags) ? false : true;
            if (!forceRelayout && !creating2 && !surfaceCreating && !formatChanged2 && !sizeChanged3 && !typeChanged && !flagsChanged && !redrawNeeded && this.mIWallpaperEngine.mShownReported) {
                return;
            }
            try {
                this.mWidth = myWidth;
                this.mHeight = myHeight;
                this.mFormat = this.mSurfaceHolder.getRequestedFormat();
                this.mType = this.mSurfaceHolder.getRequestedType();
                this.mLayout.f504x = 0;
                this.mLayout.f505y = 0;
                this.mLayout.format = this.mFormat;
                int i = this.mWindowFlags;
                this.mCurWindowFlags = i;
                this.mLayout.flags = i | 512 | 65536 | 256 | 8;
                Configuration config = this.mMergedConfiguration.getMergedConfiguration();
                Rect maxBounds = config.windowConfiguration.getMaxBounds();
                if (myWidth == -1 && myHeight == -1) {
                    try {
                        this.mLayout.width = myWidth;
                        this.mLayout.height = myHeight;
                        this.mLayout.flags &= -16385;
                    } catch (RemoteException e) {
                        return;
                    }
                } else {
                    float layoutScale = Math.max(maxBounds.width() / myWidth, maxBounds.height() / myHeight);
                    this.mLayout.width = (int) ((myWidth * layoutScale) + 0.5f);
                    this.mLayout.height = (int) ((myHeight * layoutScale) + 0.5f);
                    this.mLayout.flags |= 16384;
                }
                int i2 = this.mWindowPrivateFlags;
                this.mCurWindowPrivateFlags = i2;
                this.mLayout.privateFlags = i2;
                this.mLayout.memoryType = this.mType;
                this.mLayout.token = this.mWindowToken;
                if (this.mCreated) {
                    creating = creating2;
                    formatChanged = formatChanged2;
                    sizeChanged = sizeChanged3;
                } else {
                    try {
                        this.mLayout.type = this.mIWallpaperEngine.mWindowType;
                        this.mLayout.gravity = 8388659;
                        this.mLayout.setFitInsetsTypes(0);
                        this.mLayout.setTitle(WallpaperService.this.getClass().getName());
                        this.mLayout.windowAnimations = C4057R.C4062style.Animation_Wallpaper;
                        InputChannel inputChannel = new InputChannel();
                        sizeChanged = sizeChanged3;
                        try {
                            try {
                                try {
                                    creating = creating2;
                                    formatChanged = formatChanged2;
                                    try {
                                        if (this.mSession.addToDisplay(this.mWindow, this.mLayout, 0, this.mDisplay.getDisplayId(), WindowInsets.Type.defaultVisible(), inputChannel, this.mInsetsState, this.mTempControls, new Rect(), new float[1]) < 0) {
                                            Log.m104w(WallpaperService.TAG, "Failed to add window while updating wallpaper surface.");
                                            return;
                                        }
                                        this.mSession.setShouldZoomOutWallpaper(this.mWindow, shouldZoomOutWallpaper());
                                        this.mCreated = true;
                                        this.mInputEventReceiver = new WallpaperInputEventReceiver(inputChannel, Looper.myLooper());
                                    } catch (RemoteException e2) {
                                        return;
                                    }
                                } catch (RemoteException e3) {
                                    return;
                                }
                            } catch (RemoteException e4) {
                                return;
                            }
                        } catch (RemoteException e5) {
                            return;
                        }
                    } catch (RemoteException e6) {
                        return;
                    }
                }
                try {
                    this.mSurfaceHolder.mSurfaceLock.lock();
                    this.mDrawingAllowed = true;
                    if (fixedSize) {
                        this.mLayout.surfaceInsets.set(0, 0, 0, 0);
                    } else {
                        this.mLayout.surfaceInsets.set(this.mIWallpaperEngine.mDisplayPadding);
                    }
                    try {
                        try {
                            int relayoutResult = this.mSession.relayout(this.mWindow, this.mLayout, this.mWidth, this.mHeight, 0, 0, 0, 0, this.mWinFrames, this.mMergedConfiguration, this.mSurfaceControl, this.mInsetsState, this.mTempControls, this.mSyncSeqIdBundle);
                            int transformHint = SurfaceControl.rotationToBufferTransform((this.mDisplay.getInstallOrientation() + this.mDisplay.getRotation()) % 4);
                            this.mSurfaceControl.setTransformHint(transformHint);
                            WindowLayout.computeSurfaceSize(this.mLayout, maxBounds, this.mWidth, this.mHeight, this.mWinFrames.frame, false, this.mSurfaceSize);
                            if (this.mSurfaceControl.isValid()) {
                                try {
                                    if (this.mBbqSurfaceControl == null) {
                                        this.mBbqSurfaceControl = new SurfaceControl.Builder().setName("Wallpaper BBQ wrapper").setHidden(false).setBLASTLayer().setParent(this.mSurfaceControl).setCallsite("Wallpaper#relayout").build();
                                        SurfaceControl.Transaction transaction = new SurfaceControl.Transaction();
                                        transaction.setDefaultFrameRateCompatibility(this.mBbqSurfaceControl, 102).apply();
                                    }
                                    this.mBbqSurfaceControl.setTransformHint(transformHint);
                                    Surface blastSurface = getOrCreateBLASTSurface(this.mSurfaceSize.f76x, this.mSurfaceSize.f77y, this.mFormat);
                                    if (blastSurface != null) {
                                        this.mSurfaceHolder.mSurface.transferFrom(blastSurface);
                                    }
                                } catch (RemoteException e7) {
                                    return;
                                }
                            }
                            if (!this.mLastSurfaceSize.equals(this.mSurfaceSize)) {
                                this.mLastSurfaceSize.set(this.mSurfaceSize.f76x, this.mSurfaceSize.f77y);
                            }
                            int w = this.mWinFrames.frame.width();
                            int h3 = this.mWinFrames.frame.height();
                            DisplayCutout rawCutout = this.mInsetsState.getDisplayCutout();
                            Rect visibleFrame = new Rect(this.mWinFrames.frame);
                            visibleFrame.intersect(this.mInsetsState.getDisplayFrame());
                            WindowInsets windowInsets2 = this.mInsetsState.calculateInsets(visibleFrame, null, config.isScreenRound(), false, this.mLayout.softInputMode, this.mLayout.flags, 0, this.mLayout.type, config.windowConfiguration.getWindowingMode(), null);
                            if (fixedSize) {
                                int w2 = myWidth;
                                int h4 = myHeight;
                                windowInsets = windowInsets2;
                                h = h4;
                                h2 = w2;
                            } else {
                                Rect padding = this.mIWallpaperEngine.mDisplayPadding;
                                int w3 = w + padding.left + padding.right;
                                int h5 = h3 + padding.top + padding.bottom;
                                int w4 = padding.bottom;
                                windowInsets = windowInsets2.insetUnchecked(-padding.left, -padding.top, -padding.right, -w4);
                                h = h5;
                                h2 = w3;
                            }
                            int w5 = this.mCurWidth;
                            if (w5 != h2) {
                                sizeChanged2 = true;
                                try {
                                    this.mCurWidth = h2;
                                } catch (RemoteException e8) {
                                    return;
                                }
                            } else {
                                sizeChanged2 = sizeChanged;
                            }
                            try {
                                if (this.mCurHeight != h) {
                                    sizeChanged2 = true;
                                    this.mCurHeight = h;
                                }
                                Rect contentInsets = windowInsets.getSystemWindowInsets().toRect();
                                Rect stableInsets = windowInsets.getStableInsets().toRect();
                                DisplayCutout displayCutout = windowInsets.getDisplayCutout() != null ? windowInsets.getDisplayCutout() : rawCutout;
                                boolean insetsChanged2 = insetsChanged | (!this.mDispatchedContentInsets.equals(contentInsets));
                                try {
                                    insetsChanged2 |= !this.mDispatchedStableInsets.equals(stableInsets);
                                    try {
                                        boolean insetsChanged3 = insetsChanged2 | (!this.mDispatchedDisplayCutout.equals(displayCutout));
                                        try {
                                            this.mSurfaceHolder.setSurfaceFrameSize(h2, h);
                                            this.mSurfaceHolder.mSurfaceLock.unlock();
                                            if (!this.mSurfaceHolder.mSurface.isValid()) {
                                                try {
                                                    reportSurfaceDestroyed();
                                                    return;
                                                } catch (RemoteException e9) {
                                                    return;
                                                }
                                            }
                                            boolean didSurface = false;
                                            try {
                                                try {
                                                    this.mSurfaceHolder.ungetCallbacks();
                                                    if (surfaceCreating) {
                                                        try {
                                                            this.mIsCreating = true;
                                                            didSurface = true;
                                                            Trace.beginSection("WPMS.Engine.onSurfaceCreated");
                                                            onSurfaceCreated(this.mSurfaceHolder);
                                                            Trace.endSection();
                                                            SurfaceHolder.Callback[] callbacks = this.mSurfaceHolder.getCallbacks();
                                                            if (callbacks != null) {
                                                                int length = callbacks.length;
                                                                int myHeight3 = 0;
                                                                while (myHeight3 < length) {
                                                                    try {
                                                                        SurfaceHolder.Callback c = callbacks[myHeight3];
                                                                        SurfaceHolder.Callback[] callbacks2 = callbacks;
                                                                        int i3 = length;
                                                                        c.surfaceCreated(this.mSurfaceHolder);
                                                                        myHeight3++;
                                                                        callbacks = callbacks2;
                                                                        length = i3;
                                                                    } catch (Throwable th) {
                                                                        th = th;
                                                                        redrawNeeded2 = redrawNeeded;
                                                                        this.mIsCreating = false;
                                                                        this.mSurfaceCreated = true;
                                                                        if (redrawNeeded2) {
                                                                        }
                                                                        reposition();
                                                                        reportEngineShown(shouldWaitForEngineShown());
                                                                        throw th;
                                                                    }
                                                                }
                                                            }
                                                        } catch (Throwable th2) {
                                                            th = th2;
                                                            redrawNeeded2 = redrawNeeded;
                                                        }
                                                    }
                                                    redrawNeeded2 = redrawNeeded | (creating || (relayoutResult & 1) != 0);
                                                    if (forceReport || creating || surfaceCreating || formatChanged || sizeChanged2) {
                                                        didSurface = true;
                                                        try {
                                                            Trace.beginSection("WPMS.Engine.onSurfaceChanged");
                                                            BaseSurfaceHolder baseSurfaceHolder = this.mSurfaceHolder;
                                                            int i4 = this.mFormat;
                                                            try {
                                                                int relayoutResult2 = this.mCurWidth;
                                                                try {
                                                                    onSurfaceChanged(baseSurfaceHolder, i4, relayoutResult2, this.mCurHeight);
                                                                    Trace.endSection();
                                                                    SurfaceHolder.Callback[] callbacks3 = this.mSurfaceHolder.getCallbacks();
                                                                    if (callbacks3 != null) {
                                                                        int length2 = callbacks3.length;
                                                                        int i5 = 0;
                                                                        while (i5 < length2) {
                                                                            SurfaceHolder.Callback c2 = callbacks3[i5];
                                                                            SurfaceHolder.Callback[] callbacks4 = callbacks3;
                                                                            BaseSurfaceHolder baseSurfaceHolder2 = this.mSurfaceHolder;
                                                                            int i6 = length2;
                                                                            int i7 = this.mFormat;
                                                                            int w6 = h2;
                                                                            try {
                                                                                int w7 = this.mCurWidth;
                                                                                DisplayCutout rawCutout2 = rawCutout;
                                                                                try {
                                                                                    c2.surfaceChanged(baseSurfaceHolder2, i7, w7, this.mCurHeight);
                                                                                    i5++;
                                                                                    callbacks3 = callbacks4;
                                                                                    length2 = i6;
                                                                                    h2 = w6;
                                                                                    rawCutout = rawCutout2;
                                                                                } catch (Throwable th3) {
                                                                                    th = th3;
                                                                                    this.mIsCreating = false;
                                                                                    this.mSurfaceCreated = true;
                                                                                    if (redrawNeeded2) {
                                                                                        resetWindowPages();
                                                                                        this.mSession.finishDrawing(this.mWindow, null, Integer.MAX_VALUE);
                                                                                        processLocalColors(this.mPendingXOffset, this.mPendingXOffsetStep);
                                                                                    }
                                                                                    reposition();
                                                                                    reportEngineShown(shouldWaitForEngineShown());
                                                                                    throw th;
                                                                                }
                                                                            } catch (Throwable th4) {
                                                                                th = th4;
                                                                                this.mIsCreating = false;
                                                                                this.mSurfaceCreated = true;
                                                                                if (redrawNeeded2) {
                                                                                }
                                                                                reposition();
                                                                                reportEngineShown(shouldWaitForEngineShown());
                                                                                throw th;
                                                                            }
                                                                        }
                                                                    }
                                                                } catch (Throwable th5) {
                                                                    th = th5;
                                                                }
                                                            } catch (Throwable th6) {
                                                                th = th6;
                                                            }
                                                        } catch (Throwable th7) {
                                                            th = th7;
                                                        }
                                                    }
                                                    if (insetsChanged3) {
                                                        this.mDispatchedContentInsets.set(contentInsets);
                                                        this.mDispatchedStableInsets.set(stableInsets);
                                                        this.mDispatchedDisplayCutout = displayCutout;
                                                        Trace.beginSection("WPMS.Engine.onApplyWindowInsets");
                                                        onApplyWindowInsets(windowInsets);
                                                        Trace.endSection();
                                                    }
                                                    if (redrawNeeded2) {
                                                        Trace.beginSection("WPMS.Engine.onSurfaceRedrawNeeded");
                                                        onSurfaceRedrawNeeded(this.mSurfaceHolder);
                                                        Trace.endSection();
                                                        SurfaceHolder.Callback[] callbacks5 = this.mSurfaceHolder.getCallbacks();
                                                        if (callbacks5 != null) {
                                                            for (SurfaceHolder.Callback c3 : callbacks5) {
                                                                if (c3 instanceof SurfaceHolder.Callback2) {
                                                                    ((SurfaceHolder.Callback2) c3).surfaceRedrawNeeded(this.mSurfaceHolder);
                                                                }
                                                            }
                                                        }
                                                    }
                                                    if (didSurface && !this.mReportedVisible) {
                                                        if (this.mIsCreating) {
                                                            Trace.beginSection("WPMS.Engine.onVisibilityChanged-true");
                                                            onVisibilityChanged(true);
                                                            Trace.endSection();
                                                        }
                                                        Trace.beginSection("WPMS.Engine.onVisibilityChanged-false");
                                                        onVisibilityChanged(false);
                                                        Trace.endSection();
                                                    }
                                                    this.mIsCreating = false;
                                                    this.mSurfaceCreated = true;
                                                    if (redrawNeeded2) {
                                                        resetWindowPages();
                                                        this.mSession.finishDrawing(this.mWindow, null, Integer.MAX_VALUE);
                                                        processLocalColors(this.mPendingXOffset, this.mPendingXOffsetStep);
                                                    }
                                                    reposition();
                                                    reportEngineShown(shouldWaitForEngineShown());
                                                } catch (RemoteException e10) {
                                                }
                                            } catch (Throwable th8) {
                                                th = th8;
                                                redrawNeeded2 = redrawNeeded;
                                            }
                                        } catch (RemoteException e11) {
                                        }
                                    } catch (RemoteException e12) {
                                    }
                                } catch (RemoteException e13) {
                                }
                            } catch (RemoteException e14) {
                            }
                        } catch (RemoteException e15) {
                        }
                    } catch (RemoteException e16) {
                    }
                } catch (RemoteException e17) {
                }
            } catch (RemoteException e18) {
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void resizePreview(Rect position) {
            if (position != null) {
                this.mSurfaceHolder.setFixedSize(position.width(), position.height());
            }
        }

        private void reposition() {
            Rect rect = this.mPreviewSurfacePosition;
            if (rect == null) {
                return;
            }
            this.mTmpMatrix.setTranslate(rect.left, this.mPreviewSurfacePosition.top);
            this.mTmpMatrix.postScale(this.mPreviewSurfacePosition.width() / this.mCurWidth, this.mPreviewSurfacePosition.height() / this.mCurHeight);
            this.mTmpMatrix.getValues(this.mTmpValues);
            SurfaceControl.Transaction t = new SurfaceControl.Transaction();
            t.setPosition(this.mSurfaceControl, this.mPreviewSurfacePosition.left, this.mPreviewSurfacePosition.top);
            SurfaceControl surfaceControl = this.mSurfaceControl;
            float[] fArr = this.mTmpValues;
            t.setMatrix(surfaceControl, fArr[0], fArr[3], fArr[1], fArr[4]);
            t.apply();
        }

        void attach(IWallpaperEngineWrapper wrapper) {
            if (this.mDestroyed) {
                return;
            }
            this.mIWallpaperEngine = wrapper;
            this.mCaller = wrapper.mCaller;
            this.mConnection = wrapper.mConnection;
            this.mWindowToken = wrapper.mWindowToken;
            this.mSurfaceHolder.setSizeFromLayout();
            this.mInitializing = true;
            IWindowSession windowSession = WindowManagerGlobal.getWindowSession();
            this.mSession = windowSession;
            this.mWindow.setSession(windowSession);
            this.mLayout.packageName = WallpaperService.this.getPackageName();
            this.mIWallpaperEngine.mDisplayManager.registerDisplayListener(this.mDisplayListener, this.mCaller.getHandler());
            Display display = this.mIWallpaperEngine.mDisplay;
            this.mDisplay = display;
            Context createWindowContext = WallpaperService.this.createDisplayContext(display).createWindowContext(2013, null);
            this.mDisplayContext = createWindowContext;
            float f = createWindowContext.getResources().getFloat(C4057R.dimen.config_wallpaperDimAmount);
            this.mDefaultDimAmount = f;
            this.mWallpaperDimAmount = f;
            this.mPreviousWallpaperDimAmount = f;
            this.mDisplayState = this.mDisplay.getState();
            Trace.beginSection("WPMS.Engine.onCreate");
            onCreate(this.mSurfaceHolder);
            Trace.endSection();
            this.mInitializing = false;
            this.mReportedVisible = false;
            Trace.beginSection("WPMS.Engine.updateSurface");
            updateSurface(false, false, false);
            Trace.endSection();
        }

        public Context getDisplayContext() {
            return this.mDisplayContext;
        }

        public void doAmbientModeChanged(boolean inAmbientMode, long animationDuration) {
            if (!this.mDestroyed) {
                this.mIsInAmbientMode = inAmbientMode;
                if (this.mCreated) {
                    onAmbientModeChanged(inAmbientMode, animationDuration);
                }
            }
        }

        void doDesiredSizeChanged(int desiredWidth, int desiredHeight) {
            if (!this.mDestroyed) {
                this.mIWallpaperEngine.mReqWidth = desiredWidth;
                this.mIWallpaperEngine.mReqHeight = desiredHeight;
                onDesiredSizeChanged(desiredWidth, desiredHeight);
                doOffsetsChanged(true);
            }
        }

        void doDisplayPaddingChanged(Rect padding) {
            if (!this.mDestroyed && !this.mIWallpaperEngine.mDisplayPadding.equals(padding)) {
                this.mIWallpaperEngine.mDisplayPadding.set(padding);
                updateSurface(true, false, false);
            }
        }

        void doVisibilityChanged(boolean visible) {
            if (!this.mDestroyed) {
                this.mVisible = visible;
                reportVisibility(false);
                if (this.mReportedVisible) {
                    processLocalColors(this.mPendingXOffset, this.mPendingXOffsetStep);
                    return;
                }
                return;
            }
            AnimationHandler.requestAnimatorsEnabled(visible, this);
        }

        /* JADX WARN: Code restructure failed: missing block: B:16:0x001f, code lost:
            if (r0 != 1) goto L16;
         */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
        */
        void reportVisibility(boolean forceReport) {
            boolean z;
            if ((this.mScreenshotSurfaceControl == null || !this.mVisible) && !this.mDestroyed) {
                Display display = this.mDisplay;
                int state = display == null ? 0 : display.getState();
                this.mDisplayState = state;
                if (this.mVisible) {
                    z = true;
                }
                z = false;
                boolean visible = z;
                if (this.mReportedVisible != visible || forceReport) {
                    this.mReportedVisible = visible;
                    if (visible) {
                        doOffsetsChanged(false);
                        updateSurface(false, false, false);
                    }
                    onVisibilityChanged(visible);
                    if (this.mReportedVisible && this.mFrozenRequested) {
                        freeze();
                    }
                    AnimationHandler.requestAnimatorsEnabled(visible, this);
                }
            }
        }

        void doOffsetsChanged(boolean always) {
            float xOffset;
            float yOffset;
            float xOffsetStep;
            float yOffsetStep;
            boolean sync;
            int i;
            int xPixels;
            if (this.mDestroyed) {
                return;
            }
            if (!always && !this.mOffsetsChanged) {
                return;
            }
            synchronized (this.mLock) {
                xOffset = this.mPendingXOffset;
                yOffset = this.mPendingYOffset;
                xOffsetStep = this.mPendingXOffsetStep;
                yOffsetStep = this.mPendingYOffsetStep;
                sync = this.mPendingSync;
                i = 0;
                this.mPendingSync = false;
                this.mOffsetMessageEnqueued = false;
            }
            if (this.mSurfaceCreated) {
                if (this.mReportedVisible) {
                    int availw = this.mIWallpaperEngine.mReqWidth - this.mCurWidth;
                    if (availw <= 0) {
                        xPixels = 0;
                    } else {
                        xPixels = -((int) ((availw * xOffset) + 0.5f));
                    }
                    int availh = this.mIWallpaperEngine.mReqHeight - this.mCurHeight;
                    if (availh > 0) {
                        i = -((int) ((availh * yOffset) + 0.5f));
                    }
                    int yPixels = i;
                    onOffsetsChanged(xOffset, yOffset, xOffsetStep, yOffsetStep, xPixels, yPixels);
                } else {
                    this.mOffsetsChanged = true;
                }
            }
            if (sync) {
                try {
                    this.mSession.wallpaperOffsetsComplete(this.mWindow.asBinder());
                } catch (RemoteException e) {
                }
            }
            processLocalColors(xOffset, xOffsetStep);
        }

        private void processLocalColors(final float xOffset, final float xOffsetStep) {
            if (this.mProcessLocalColorsPending.compareAndSet(false, true)) {
                final long now = this.mClockFunction.get().longValue();
                long timeSinceLastColorProcess = now - this.mLastProcessLocalColorsTimestamp;
                final long timeToWait = Math.max(0L, 1000 - timeSinceLastColorProcess);
                this.mHandler.postDelayed(new Runnable() { // from class: android.service.wallpaper.WallpaperService$Engine$$ExternalSyntheticLambda5
                    @Override // java.lang.Runnable
                    public final void run() {
                        WallpaperService.Engine.this.lambda$processLocalColors$1(now, timeToWait, xOffset, xOffsetStep);
                    }
                }, timeToWait);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$processLocalColors$1(long now, long timeToWait, float xOffset, float xOffsetStep) {
            this.mLastProcessLocalColorsTimestamp = now + timeToWait;
            this.mProcessLocalColorsPending.set(false);
            processLocalColorsInternal(xOffset, xOffsetStep);
        }

        private void processLocalColorsInternal(float xOffset, float xOffsetStep) {
            int xPages;
            float xOffsetStep2;
            float xOffset2;
            int xCurrentPage;
            if (supportsLocalColorExtraction() || xOffset % xOffsetStep > WallpaperService.MIN_PAGE_ALLOWED_MARGIN || !this.mSurfaceHolder.getSurface().isValid()) {
                return;
            }
            if (!validStep(xOffsetStep)) {
                xOffset2 = 0.0f;
                xOffsetStep2 = 1.0f;
                xCurrentPage = 0;
                xPages = 1;
            } else {
                xPages = Math.round(1.0f / xOffsetStep) + 1;
                xOffsetStep2 = 1.0f / xPages;
                float shrink = (xPages - 1) / xPages;
                xOffset2 = xOffset * shrink;
                xCurrentPage = Math.round(xOffset2 / xOffsetStep2);
            }
            float finalXOffsetStep = xOffsetStep2;
            Trace.beginSection("WallpaperService#processLocalColors");
            resetWindowPages();
            int xPage = xCurrentPage;
            EngineWindowPage[] engineWindowPageArr = this.mWindowPages;
            if (engineWindowPageArr.length == 0 || engineWindowPageArr.length != xPages) {
                EngineWindowPage[] engineWindowPageArr2 = new EngineWindowPage[xPages];
                this.mWindowPages = engineWindowPageArr2;
                initWindowPages(engineWindowPageArr2, finalXOffsetStep);
            }
            if (this.mLocalColorsToAdd.size() != 0) {
                Iterator<RectF> it = this.mLocalColorsToAdd.iterator();
                while (it.hasNext()) {
                    RectF colorArea = it.next();
                    if (WallpaperService.this.isValid(colorArea)) {
                        this.mLocalColorAreas.add(colorArea);
                        int colorPage = getRectFPage(colorArea, finalXOffsetStep);
                        EngineWindowPage currentPage = this.mWindowPages[colorPage];
                        currentPage.setLastUpdateTime(0L);
                        currentPage.removeColor(colorArea);
                    }
                }
                this.mLocalColorsToAdd.clear();
            }
            EngineWindowPage[] engineWindowPageArr3 = this.mWindowPages;
            if (xPage >= engineWindowPageArr3.length) {
                xPage = engineWindowPageArr3.length - 1;
            }
            EngineWindowPage current = engineWindowPageArr3[xPage];
            updatePage(current, xPage, xPages, finalXOffsetStep);
            Trace.endSection();
        }

        private void initWindowPages(EngineWindowPage[] windowPages, float step) {
            for (int i = 0; i < windowPages.length; i++) {
                windowPages[i] = new EngineWindowPage();
            }
            this.mLocalColorAreas.addAll((ArraySet<? extends RectF>) this.mLocalColorsToAdd);
            this.mLocalColorsToAdd.clear();
            Iterator<RectF> it = this.mLocalColorAreas.iterator();
            while (it.hasNext()) {
                RectF area = it.next();
                if (!WallpaperService.this.isValid(area)) {
                    this.mLocalColorAreas.remove(area);
                } else {
                    int pageNum = getRectFPage(area, step);
                    windowPages[pageNum].addArea(area);
                }
            }
        }

        void updatePage(final EngineWindowPage currentPage, final int pageIndx, final int numPages, final float xOffsetStep) {
            int i;
            int height;
            int width;
            final long current = SystemClock.elapsedRealtime() - 60000;
            long lapsed = current - currentPage.getLastUpdateTime();
            if (lapsed < 60000) {
                return;
            }
            Surface surface = this.mSurfaceHolder.getSurface();
            if (surface.isValid()) {
                boolean widthIsLarger = this.mSurfaceSize.f76x > this.mSurfaceSize.f77y;
                if (widthIsLarger) {
                    i = this.mSurfaceSize.f76x;
                } else {
                    i = this.mSurfaceSize.f77y;
                }
                int smaller = i;
                float ratio = 64.0f / smaller;
                int width2 = (int) (this.mSurfaceSize.f76x * ratio);
                int height2 = (int) (this.mSurfaceSize.f77y * ratio);
                if (width2 <= 0) {
                    height = height2;
                    width = width2;
                } else if (height2 > 0) {
                    final Bitmap screenShot = Bitmap.createBitmap(width2, height2, Bitmap.Config.ARGB_8888);
                    Trace.beginSection("WallpaperService#pixelCopy");
                    PixelCopy.request(surface, screenShot, new PixelCopy.OnPixelCopyFinishedListener() { // from class: android.service.wallpaper.WallpaperService$Engine$$ExternalSyntheticLambda0
                        @Override // android.view.PixelCopy.OnPixelCopyFinishedListener
                        public final void onPixelCopyFinished(int i2) {
                            WallpaperService.Engine.this.lambda$updatePage$2(currentPage, pageIndx, numPages, xOffsetStep, screenShot, current, i2);
                        }
                    }, this.mHandler);
                    return;
                } else {
                    height = height2;
                    width = width2;
                }
                Log.m110e(WallpaperService.TAG, "wrong width and height values of bitmap " + width + " " + height);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$updatePage$2(EngineWindowPage currentPage, int pageIndx, int numPages, float xOffsetStep, Bitmap finalScreenShot, long current, int res) {
            Trace.endSection();
            if (res != 0) {
                Bitmap lastBitmap = currentPage.getBitmap();
                currentPage.setBitmap(this.mLastScreenshot);
                Bitmap lastScreenshot = this.mLastScreenshot;
                if (lastScreenshot != null && !lastScreenshot.isRecycled() && !Objects.equals(lastBitmap, lastScreenshot)) {
                    updatePageColors(currentPage, pageIndx, numPages, xOffsetStep);
                    return;
                }
                return;
            }
            this.mLastScreenshot = finalScreenShot;
            currentPage.setBitmap(finalScreenShot);
            currentPage.setLastUpdateTime(current);
            updatePageColors(currentPage, pageIndx, numPages, xOffsetStep);
        }

        private void updatePageColors(EngineWindowPage page, int pageIndx, int numPages, float xOffsetStep) {
            EngineWindowPage engineWindowPage = page;
            if (page.getBitmap() == null) {
                return;
            }
            Trace.beginSection("WallpaperService#updatePageColors");
            Iterator<RectF> it = page.getAreas().iterator();
            while (it.hasNext()) {
                RectF area = it.next();
                if (area != null) {
                    RectF subArea = generateSubRect(area, pageIndx, numPages);
                    Bitmap b = page.getBitmap();
                    int x = Math.round(b.getWidth() * subArea.left);
                    int y = Math.round(b.getHeight() * subArea.top);
                    int width = Math.round(b.getWidth() * subArea.width());
                    int height = Math.round(b.getHeight() * subArea.height());
                    try {
                        Bitmap target = Bitmap.createBitmap(page.getBitmap(), x, y, width, height);
                        WallpaperColors color = WallpaperColors.fromBitmap(target, this.mWallpaperDimAmount);
                        target.recycle();
                        Iterator<RectF> it2 = it;
                        WallpaperColors currentColor = engineWindowPage.getColors(area);
                        if (currentColor == null || !color.equals(currentColor)) {
                            engineWindowPage.addWallpaperColors(area, color);
                            try {
                                this.mConnection.onLocalWallpaperColorsChanged(area, color, this.mDisplayContext.getDisplayId());
                            } catch (RemoteException e) {
                                Log.m109e(WallpaperService.TAG, "Error calling Connection.onLocalWallpaperColorsChanged", e);
                            }
                        }
                        engineWindowPage = page;
                        it = it2;
                    } catch (Exception e2) {
                        Log.m109e(WallpaperService.TAG, "Error creating page local color bitmap", e2);
                        engineWindowPage = page;
                        it = it;
                    }
                }
            }
            Trace.endSection();
        }

        private RectF generateSubRect(RectF in, int pageInx, int numPages) {
            float minLeft = pageInx / numPages;
            float maxRight = (pageInx + 1) / numPages;
            float left = in.left;
            float right = in.right;
            if (left < minLeft) {
                left = minLeft;
            }
            if (right > maxRight) {
                right = maxRight;
            }
            float left2 = (numPages * left) % 1.0f;
            float right2 = (numPages * right) % 1.0f;
            if (right2 == 0.0f) {
                right2 = 1.0f;
            }
            return new RectF(left2, in.top, right2, in.bottom);
        }

        private void resetWindowPages() {
            if (supportsLocalColorExtraction() || !this.mResetWindowPages) {
                return;
            }
            this.mResetWindowPages = false;
            this.mLastWindowPage = -1;
            int i = 0;
            while (true) {
                EngineWindowPage[] engineWindowPageArr = this.mWindowPages;
                if (i < engineWindowPageArr.length) {
                    engineWindowPageArr[i].setLastUpdateTime(0L);
                    i++;
                } else {
                    return;
                }
            }
        }

        private int getRectFPage(RectF area, float step) {
            if (WallpaperService.this.isValid(area) && validStep(step)) {
                int pages = Math.round(1.0f / step);
                int page = Math.round(area.centerX() * pages);
                if (page == pages) {
                    return pages - 1;
                }
                EngineWindowPage[] engineWindowPageArr = this.mWindowPages;
                return page == engineWindowPageArr.length ? engineWindowPageArr.length - 1 : page;
            }
            return 0;
        }

        public void addLocalColorsAreas(final List<RectF> regions) {
            if (supportsLocalColorExtraction()) {
                return;
            }
            this.mHandler.post(new Runnable() { // from class: android.service.wallpaper.WallpaperService$Engine$$ExternalSyntheticLambda6
                @Override // java.lang.Runnable
                public final void run() {
                    WallpaperService.Engine.this.lambda$addLocalColorsAreas$3(regions);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$addLocalColorsAreas$3(List regions) {
            this.mLocalColorsToAdd.addAll(regions);
            processLocalColors(this.mPendingXOffset, this.mPendingYOffset);
        }

        public void removeLocalColorsAreas(final List<RectF> regions) {
            if (supportsLocalColorExtraction()) {
                return;
            }
            this.mHandler.post(new Runnable() { // from class: android.service.wallpaper.WallpaperService$Engine$$ExternalSyntheticLambda4
                @Override // java.lang.Runnable
                public final void run() {
                    WallpaperService.Engine.this.lambda$removeLocalColorsAreas$4(regions);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$removeLocalColorsAreas$4(List regions) {
            float step = this.mPendingXOffsetStep;
            this.mLocalColorsToAdd.removeAll(regions);
            this.mLocalColorAreas.removeAll(regions);
            if (!validStep(step)) {
                return;
            }
            for (int i = 0; i < this.mWindowPages.length; i++) {
                for (int j = 0; j < regions.size(); j++) {
                    this.mWindowPages[i].removeArea((RectF) regions.get(j));
                }
            }
        }

        private Rect fixRect(Bitmap b, Rect r) {
            int i;
            int width;
            if (r.left >= r.right || r.left >= b.getWidth() || r.left > 0) {
                i = 0;
            } else {
                i = r.left;
            }
            r.left = i;
            if (r.left >= r.right || r.right > b.getWidth()) {
                width = b.getWidth();
            } else {
                width = r.right;
            }
            r.right = width;
            return r;
        }

        private boolean validStep(float step) {
            return !WallpaperService.PROHIBITED_STEPS.contains(Float.valueOf(step)) && ((double) step) > 0.0d && ((double) step) <= 1.0d;
        }

        void doCommand(WallpaperCommand cmd) {
            Bundle result;
            if (!this.mDestroyed) {
                if (WallpaperManager.COMMAND_FREEZE.equals(cmd.action) || WallpaperManager.COMMAND_UNFREEZE.equals(cmd.action)) {
                    updateFrozenState(!WallpaperManager.COMMAND_UNFREEZE.equals(cmd.action));
                }
                result = onCommand(cmd.action, cmd.f438x, cmd.f439y, cmd.f440z, cmd.extras, cmd.sync);
            } else {
                result = null;
            }
            if (cmd.sync) {
                try {
                    this.mSession.wallpaperCommandComplete(this.mWindow.asBinder(), result);
                } catch (RemoteException e) {
                }
            }
        }

        private void updateFrozenState(boolean frozenRequested) {
            if (this.mIWallpaperEngine.mWallpaperManager.getWallpaperInfo() == null && frozenRequested) {
                return;
            }
            this.mFrozenRequested = frozenRequested;
            boolean isFrozen = this.mScreenshotSurfaceControl != null;
            if (frozenRequested == isFrozen) {
                return;
            }
            if (frozenRequested) {
                freeze();
            } else {
                unfreeze();
            }
        }

        private void freeze() {
            if (!this.mReportedVisible || this.mDestroyed || !showScreenshotOfWallpaper()) {
                return;
            }
            doVisibilityChanged(false);
            this.mVisible = true;
        }

        private void unfreeze() {
            cleanUpScreenshotSurfaceControl();
            if (this.mVisible) {
                doVisibilityChanged(true);
            }
        }

        private void cleanUpScreenshotSurfaceControl() {
            if (this.mScreenshotSurfaceControl != null) {
                new SurfaceControl.Transaction().remove(this.mScreenshotSurfaceControl).show(this.mBbqSurfaceControl).apply();
                this.mScreenshotSurfaceControl = null;
            }
        }

        void scaleAndCropScreenshot() {
            if (this.mScreenshotSurfaceControl == null) {
                return;
            }
            if (this.mScreenshotSize.f76x <= 0 || this.mScreenshotSize.f77y <= 0) {
                Log.m104w(WallpaperService.TAG, "Unexpected screenshot size: " + this.mScreenshotSize);
                return;
            }
            float scaleFactor = Math.max(1.0f, Math.max(this.mSurfaceSize.f76x / this.mScreenshotSize.f76x, this.mSurfaceSize.f77y / this.mScreenshotSize.f77y));
            int diffX = ((int) (this.mScreenshotSize.f76x * scaleFactor)) - this.mSurfaceSize.f76x;
            int diffY = ((int) (this.mScreenshotSize.f77y * scaleFactor)) - this.mSurfaceSize.f77y;
            new SurfaceControl.Transaction().setMatrix(this.mScreenshotSurfaceControl, scaleFactor, 0.0f, 0.0f, scaleFactor).setWindowCrop(this.mScreenshotSurfaceControl, new Rect(diffX / 2, diffY / 2, (diffX / 2) + this.mScreenshotSize.f76x, (diffY / 2) + this.mScreenshotSize.f77y)).setPosition(this.mScreenshotSurfaceControl, (-diffX) / 2, (-diffY) / 2).apply();
        }

        private boolean showScreenshotOfWallpaper() {
            SurfaceControl surfaceControl;
            if (this.mDestroyed || (surfaceControl = this.mSurfaceControl) == null || !surfaceControl.isValid()) {
                return false;
            }
            Rect bounds = new Rect(0, 0, this.mSurfaceSize.f76x, this.mSurfaceSize.f77y);
            if (bounds.isEmpty()) {
                Log.m104w(WallpaperService.TAG, "Failed to screenshot wallpaper: surface bounds are empty");
                return false;
            }
            if (this.mScreenshotSurfaceControl != null) {
                Log.m110e(WallpaperService.TAG, "Screenshot is unexpectedly not null");
                cleanUpScreenshotSurfaceControl();
            }
            ScreenCapture.ScreenshotHardwareBuffer screenshotBuffer = ScreenCapture.captureLayers(new ScreenCapture.LayerCaptureArgs.Builder(this.mSurfaceControl).setUid(Process.myUid()).setChildrenOnly(false).setSourceCrop(bounds).build());
            if (screenshotBuffer == null) {
                Log.m104w(WallpaperService.TAG, "Failed to screenshot wallpaper: screenshotBuffer is null");
                return false;
            }
            HardwareBuffer hardwareBuffer = screenshotBuffer.getHardwareBuffer();
            SurfaceControl.Transaction t = new SurfaceControl.Transaction();
            this.mScreenshotSurfaceControl = new SurfaceControl.Builder().setName("Wallpaper snapshot for engine " + this).setFormat(hardwareBuffer.getFormat()).setParent(this.mSurfaceControl).setSecure(screenshotBuffer.containsSecureLayers()).setCallsite("WallpaperService.Engine.showScreenshotOfWallpaper").setBLASTLayer().build();
            this.mScreenshotSize.set(this.mSurfaceSize.f76x, this.mSurfaceSize.f77y);
            t.setBuffer(this.mScreenshotSurfaceControl, hardwareBuffer);
            t.setColorSpace(this.mScreenshotSurfaceControl, screenshotBuffer.getColorSpace());
            t.setLayer(this.mScreenshotSurfaceControl, Integer.MAX_VALUE);
            t.show(this.mScreenshotSurfaceControl);
            t.hide(this.mBbqSurfaceControl);
            t.apply();
            return true;
        }

        void reportSurfaceDestroyed() {
            if (this.mSurfaceCreated) {
                this.mSurfaceCreated = false;
                this.mSurfaceHolder.ungetCallbacks();
                SurfaceHolder.Callback[] callbacks = this.mSurfaceHolder.getCallbacks();
                if (callbacks != null) {
                    for (SurfaceHolder.Callback c : callbacks) {
                        c.surfaceDestroyed(this.mSurfaceHolder);
                    }
                }
                onSurfaceDestroyed(this.mSurfaceHolder);
            }
        }

        void detach() {
            if (this.mDestroyed) {
                return;
            }
            AnimationHandler.removeRequestor(this);
            this.mDestroyed = true;
            if (this.mIWallpaperEngine.mDisplayManager != null) {
                this.mIWallpaperEngine.mDisplayManager.unregisterDisplayListener(this.mDisplayListener);
            }
            if (this.mVisible) {
                this.mVisible = false;
                onVisibilityChanged(false);
            }
            reportSurfaceDestroyed();
            onDestroy();
            if (this.mCreated) {
                try {
                    WallpaperInputEventReceiver wallpaperInputEventReceiver = this.mInputEventReceiver;
                    if (wallpaperInputEventReceiver != null) {
                        wallpaperInputEventReceiver.dispose();
                        this.mInputEventReceiver = null;
                    }
                    this.mSession.remove(this.mWindow);
                } catch (RemoteException e) {
                }
                this.mSurfaceHolder.mSurface.release();
                BLASTBufferQueue bLASTBufferQueue = this.mBlastBufferQueue;
                if (bLASTBufferQueue != null) {
                    bLASTBufferQueue.destroy();
                    this.mBlastBufferQueue = null;
                }
                if (this.mBbqSurfaceControl != null) {
                    new SurfaceControl.Transaction().remove(this.mBbqSurfaceControl).apply();
                    this.mBbqSurfaceControl = null;
                }
                this.mCreated = false;
            }
            SurfaceControl surfaceControl = this.mSurfaceControl;
            if (surfaceControl != null) {
                surfaceControl.release();
                this.mSurfaceControl = null;
            }
        }

        private Surface getOrCreateBLASTSurface(int width, int height, int format) {
            BLASTBufferQueue bLASTBufferQueue = this.mBlastBufferQueue;
            if (bLASTBufferQueue == null) {
                BLASTBufferQueue bLASTBufferQueue2 = new BLASTBufferQueue("Wallpaper", this.mBbqSurfaceControl, width, height, format);
                this.mBlastBufferQueue = bLASTBufferQueue2;
                Surface ret = bLASTBufferQueue2.createSurface();
                return ret;
            }
            bLASTBufferQueue.update(this.mBbqSurfaceControl, width, height, format);
            return null;
        }
    }

    public Looper onProvideEngineLooper() {
        return super.getMainLooper();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean isValid(RectF area) {
        return area != null && area.bottom > area.top && area.left < area.right && LOCAL_COLOR_BOUNDS.contains(area);
    }

    private boolean inRectFRange(float number) {
        return number >= 0.0f && number <= 1.0f;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes3.dex */
    public class IWallpaperEngineWrapper extends IWallpaperEngine.Stub implements HandlerCaller.Callback {
        private final HandlerCaller mCaller;
        final IWallpaperConnection mConnection;
        final Display mDisplay;
        final int mDisplayId;
        final DisplayManager mDisplayManager;
        final Rect mDisplayPadding;
        Engine mEngine;
        final boolean mIsPreview;
        int mReqHeight;
        int mReqWidth;
        boolean mShownReported;
        final WallpaperManager mWallpaperManager;
        int mWhich;
        final IBinder mWindowToken;
        final int mWindowType;

        IWallpaperEngineWrapper(WallpaperService service, IWallpaperConnection conn, IBinder windowToken, int windowType, boolean isPreview, int reqWidth, int reqHeight, Rect padding, int displayId, int which) {
            Rect rect = new Rect();
            this.mDisplayPadding = rect;
            this.mWallpaperManager = (WallpaperManager) WallpaperService.this.getSystemService(WallpaperManager.class);
            HandlerCaller handlerCaller = new HandlerCaller(service, service.onProvideEngineLooper(), this, true);
            this.mCaller = handlerCaller;
            this.mConnection = conn;
            this.mWindowToken = windowToken;
            this.mWindowType = windowType;
            this.mIsPreview = isPreview;
            this.mReqWidth = reqWidth;
            this.mReqHeight = reqHeight;
            rect.set(padding);
            this.mDisplayId = displayId;
            this.mWhich = which;
            DisplayManager displayManager = (DisplayManager) WallpaperService.this.getSystemService(DisplayManager.class);
            this.mDisplayManager = displayManager;
            Display display = displayManager.getDisplay(displayId);
            this.mDisplay = display;
            if (display == null) {
                throw new IllegalArgumentException("Cannot find display with id" + displayId);
            }
            Message msg = handlerCaller.obtainMessage(10);
            handlerCaller.sendMessage(msg);
        }

        @Override // android.service.wallpaper.IWallpaperEngine
        public void setDesiredSize(int width, int height) {
            Message msg = this.mCaller.obtainMessageII(30, width, height);
            this.mCaller.sendMessage(msg);
        }

        @Override // android.service.wallpaper.IWallpaperEngine
        public void setDisplayPadding(Rect padding) {
            Message msg = this.mCaller.obtainMessageO(40, padding);
            this.mCaller.sendMessage(msg);
        }

        @Override // android.service.wallpaper.IWallpaperEngine
        public void setVisibility(boolean visible) {
            Message msg = this.mCaller.obtainMessageI(10010, visible ? 1 : 0);
            this.mCaller.sendMessage(msg);
        }

        @Override // android.service.wallpaper.IWallpaperEngine
        public void setWallpaperFlags(int which) {
            if (which == this.mWhich) {
                return;
            }
            this.mWhich = which;
            Message msg = this.mCaller.obtainMessageI(WallpaperService.MSG_WALLPAPER_FLAGS_CHANGED, which);
            this.mCaller.sendMessage(msg);
        }

        @Override // android.service.wallpaper.IWallpaperEngine
        public void setInAmbientMode(boolean inAmbientDisplay, long animationDuration) throws RemoteException {
            Message msg = this.mCaller.obtainMessageIO(50, inAmbientDisplay ? 1 : 0, Long.valueOf(animationDuration));
            this.mCaller.sendMessage(msg);
        }

        @Override // android.service.wallpaper.IWallpaperEngine
        public void dispatchPointer(MotionEvent event) {
            Engine engine = this.mEngine;
            if (engine != null) {
                engine.dispatchPointer(event);
            } else {
                event.recycle();
            }
        }

        @Override // android.service.wallpaper.IWallpaperEngine
        public void dispatchWallpaperCommand(String action, int x, int y, int z, Bundle extras) {
            Engine engine = this.mEngine;
            if (engine != null) {
                engine.mWindow.dispatchWallpaperCommand(action, x, y, z, extras, false);
            }
        }

        @Override // android.service.wallpaper.IWallpaperEngine
        public void setZoomOut(float scale) {
            Message msg = this.mCaller.obtainMessageI(10100, Float.floatToIntBits(scale));
            this.mCaller.sendMessage(msg);
        }

        public void reportShown() {
            if (!this.mShownReported) {
                this.mShownReported = true;
                Trace.beginSection("WPMS.mConnection.engineShown");
                try {
                    this.mConnection.engineShown(this);
                    Log.m112d(WallpaperService.TAG, "Wallpaper has updated the surface:" + this.mWallpaperManager.getWallpaperInfo());
                } catch (RemoteException e) {
                    Log.m103w(WallpaperService.TAG, "Wallpaper host disappeared", e);
                }
                Trace.endSection();
            }
        }

        @Override // android.service.wallpaper.IWallpaperEngine
        public void requestWallpaperColors() {
            Message msg = this.mCaller.obtainMessage(10050);
            this.mCaller.sendMessage(msg);
        }

        @Override // android.service.wallpaper.IWallpaperEngine
        public void addLocalColorsAreas(List<RectF> regions) {
            this.mEngine.addLocalColorsAreas(regions);
        }

        @Override // android.service.wallpaper.IWallpaperEngine
        public void removeLocalColorsAreas(List<RectF> regions) {
            this.mEngine.removeLocalColorsAreas(regions);
        }

        @Override // android.service.wallpaper.IWallpaperEngine
        public void applyDimming(float dimAmount) throws RemoteException {
            Message msg = this.mCaller.obtainMessageI(WallpaperService.MSG_UPDATE_DIMMING, Float.floatToIntBits(dimAmount));
            this.mCaller.sendMessage(msg);
        }

        @Override // android.service.wallpaper.IWallpaperEngine
        public void destroy() {
            Message msg = this.mCaller.obtainMessage(20);
            this.mCaller.getHandler().removeCallbacksAndMessages(null);
            this.mCaller.sendMessage(msg);
        }

        @Override // android.service.wallpaper.IWallpaperEngine
        public void resizePreview(Rect position) {
            Message msg = this.mCaller.obtainMessageO(WallpaperService.MSG_RESIZE_PREVIEW, position);
            this.mCaller.sendMessage(msg);
        }

        @Override // android.service.wallpaper.IWallpaperEngine
        public SurfaceControl mirrorSurfaceControl() {
            Engine engine = this.mEngine;
            if (engine == null) {
                return null;
            }
            return SurfaceControl.mirrorSurface(engine.mSurfaceControl);
        }

        private void doAttachEngine() {
            Trace.beginSection("WPMS.onCreateEngine");
            Engine engine = WallpaperService.this.onCreateEngine();
            Trace.endSection();
            this.mEngine = engine;
            Trace.beginSection("WPMS.mConnection.attachEngine-" + this.mDisplayId);
            try {
                this.mConnection.attachEngine(this, this.mDisplayId);
                Trace.endSection();
                Trace.beginSection("WPMS.engine.attach");
                engine.attach(this);
            } catch (RemoteException e) {
                engine.detach();
                Log.m103w(WallpaperService.TAG, "Wallpaper host disappeared", e);
            } catch (IllegalStateException e2) {
                Log.m103w(WallpaperService.TAG, "Connector instance already destroyed, can't attach engine to non existing connector", e2);
            } finally {
                Trace.endSection();
            }
        }

        private void doDetachEngine() {
            if (!this.mEngine.mDestroyed) {
                this.mEngine.detach();
                for (IWallpaperEngineWrapper engineWrapper : WallpaperService.this.mActiveEngines.values()) {
                    Engine engine = engineWrapper.mEngine;
                    if (engine != null && engine.mVisible) {
                        engineWrapper.mEngine.doVisibilityChanged(false);
                        engineWrapper.mEngine.doVisibilityChanged(true);
                    }
                }
            }
        }

        @Override // com.android.internal.p028os.HandlerCaller.Callback
        public void executeMessage(Message message) {
            switch (message.what) {
                case 10:
                    Trace.beginSection("WPMS.DO_ATTACH");
                    doAttachEngine();
                    Trace.endSection();
                    return;
                case 20:
                    Trace.beginSection("WPMS.DO_DETACH");
                    doDetachEngine();
                    Trace.endSection();
                    return;
                case 30:
                    this.mEngine.doDesiredSizeChanged(message.arg1, message.arg2);
                    return;
                case 40:
                    this.mEngine.doDisplayPaddingChanged((Rect) message.obj);
                    return;
                case 50:
                    this.mEngine.doAmbientModeChanged(message.arg1 != 0, ((Long) message.obj).longValue());
                    return;
                case 10000:
                    this.mEngine.updateSurface(true, false, false);
                    return;
                case 10010:
                    this.mEngine.doVisibilityChanged(message.arg1 != 0);
                    return;
                case 10020:
                    this.mEngine.doOffsetsChanged(true);
                    return;
                case 10025:
                    WallpaperCommand cmd = (WallpaperCommand) message.obj;
                    this.mEngine.doCommand(cmd);
                    return;
                case 10030:
                    boolean reportDraw = message.arg1 != 0;
                    this.mEngine.updateConfiguration((MergedConfiguration) message.obj);
                    this.mEngine.updateSurface(true, false, reportDraw);
                    this.mEngine.doOffsetsChanged(true);
                    this.mEngine.scaleAndCropScreenshot();
                    return;
                case 10035:
                    return;
                case 10040:
                    boolean skip = false;
                    MotionEvent ev = (MotionEvent) message.obj;
                    if (ev.getAction() == 2) {
                        synchronized (this.mEngine.mLock) {
                            if (this.mEngine.mPendingMove == ev) {
                                this.mEngine.mPendingMove = null;
                            } else {
                                skip = true;
                            }
                        }
                    }
                    if (!skip) {
                        this.mEngine.onTouchEvent(ev);
                    }
                    ev.recycle();
                    return;
                case 10050:
                    if (this.mConnection != null) {
                        try {
                            WallpaperColors colors = this.mEngine.onComputeColors();
                            this.mEngine.setPrimaryWallpaperColors(colors);
                            this.mConnection.onWallpaperColorsChanged(colors, this.mDisplayId);
                            return;
                        } catch (RemoteException e) {
                            return;
                        }
                    }
                    return;
                case 10100:
                    this.mEngine.setZoom(Float.intBitsToFloat(message.arg1));
                    return;
                case WallpaperService.MSG_RESIZE_PREVIEW /* 10110 */:
                    this.mEngine.resizePreview((Rect) message.obj);
                    return;
                case 10150:
                    Trace.beginSection("WPMS.MSG_REPORT_SHOWN");
                    reportShown();
                    Trace.endSection();
                    return;
                case WallpaperService.MSG_UPDATE_DIMMING /* 10200 */:
                    this.mEngine.updateWallpaperDimming(Float.intBitsToFloat(message.arg1));
                    return;
                case WallpaperService.MSG_WALLPAPER_FLAGS_CHANGED /* 10210 */:
                    this.mEngine.onWallpaperFlagsChanged(message.arg1);
                    return;
                default:
                    Log.m104w(WallpaperService.TAG, "Unknown message type " + message.what);
                    return;
            }
        }
    }

    /* loaded from: classes3.dex */
    class IWallpaperServiceWrapper extends IWallpaperService.Stub {
        private final WallpaperService mTarget;

        public IWallpaperServiceWrapper(WallpaperService context) {
            this.mTarget = context;
        }

        @Override // android.service.wallpaper.IWallpaperService
        public void attach(IWallpaperConnection conn, IBinder windowToken, int windowType, boolean isPreview, int reqWidth, int reqHeight, Rect padding, int displayId, int which) {
            Trace.beginSection("WPMS.ServiceWrapper.attach");
            IWallpaperEngineWrapper engineWrapper = new IWallpaperEngineWrapper(this.mTarget, conn, windowToken, windowType, isPreview, reqWidth, reqHeight, padding, displayId, which);
            WallpaperService.this.mActiveEngines.put(windowToken, engineWrapper);
            Trace.endSection();
        }

        @Override // android.service.wallpaper.IWallpaperService
        public void detach(IBinder windowToken) {
            IWallpaperEngineWrapper engineWrapper = (IWallpaperEngineWrapper) WallpaperService.this.mActiveEngines.remove(windowToken);
            if (engineWrapper == null) {
                Log.m104w(WallpaperService.TAG, "Engine for window token " + windowToken + " already detached");
            } else {
                engineWrapper.destroy();
            }
        }
    }

    @Override // android.app.Service
    public void onCreate() {
        Trace.beginSection("WPMS.onCreate");
        super.onCreate();
        Trace.endSection();
    }

    @Override // android.app.Service
    public void onDestroy() {
        Trace.beginSection("WPMS.onDestroy");
        super.onDestroy();
        for (IWallpaperEngineWrapper engineWrapper : this.mActiveEngines.values()) {
            engineWrapper.destroy();
        }
        this.mActiveEngines.clear();
        Trace.endSection();
    }

    @Override // android.app.Service
    public final IBinder onBind(Intent intent) {
        return new IWallpaperServiceWrapper(this);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.app.Service
    public void dump(FileDescriptor fd, PrintWriter out, String[] args) {
        out.print("State of wallpaper ");
        out.print(this);
        out.println(":");
        for (IWallpaperEngineWrapper engineWrapper : this.mActiveEngines.values()) {
            Engine engine = engineWrapper.mEngine;
            if (engine == null) {
                Slog.m90w(TAG, "Engine for wrapper " + engineWrapper + " not attached");
            } else {
                out.print("  Engine ");
                out.print(engine);
                out.println(":");
                engine.dump("    ", fd, out, args);
            }
        }
    }
}
