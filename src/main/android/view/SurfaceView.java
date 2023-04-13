package android.view;

import android.content.Context;
import android.content.p001pm.PackageManager;
import android.content.res.CompatibilityInfo;
import android.graphics.BLASTBufferQueue;
import android.graphics.BlendMode;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.RenderNode;
import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.p008os.Handler;
import android.p008os.IBinder;
import android.p008os.Looper;
import android.p008os.SystemClock;
import android.util.ArraySet;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceControl;
import android.view.SurfaceControlViewHost;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewRootImpl;
import android.view.ViewTreeObserver;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.IAccessibilityEmbeddedConnection;
import android.window.SurfaceSyncGroup;
import com.android.internal.view.SurfaceCallbackHelper;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
/* loaded from: classes4.dex */
public class SurfaceView extends View implements ViewRootImpl.SurfaceChangedCallback {
    private static final boolean DEBUG = false;
    private static final boolean DEBUG_POSITION = false;
    public static final int SURFACE_LIFECYCLE_DEFAULT = 0;
    public static final int SURFACE_LIFECYCLE_FOLLOWS_ATTACHMENT = 2;
    public static final int SURFACE_LIFECYCLE_FOLLOWS_VISIBILITY = 1;
    private static final String TAG = "SurfaceView";
    float mAlpha;
    private boolean mAttachedToWindow;
    int mBackgroundColor;
    SurfaceControl mBackgroundControl;
    private BLASTBufferQueue mBlastBufferQueue;
    private SurfaceControl mBlastSurfaceControl;
    final ArrayList<SurfaceHolder.Callback> mCallbacks;
    boolean mClipSurfaceToBounds;
    float mCornerRadius;
    private boolean mDisableBackgroundLayer;
    boolean mDrawFinished;
    private final ViewTreeObserver.OnPreDrawListener mDrawListener;
    boolean mDrawingStopped;
    int mFormat;
    private final SurfaceControl.Transaction mFrameCallbackTransaction;
    private boolean mGlobalListenersAdded;
    boolean mHaveFrame;
    boolean mIsCreating;
    long mLastLockTime;
    int mLastSurfaceHeight;
    int mLastSurfaceWidth;
    boolean mLastWindowVisibility;
    final int[] mLocation;
    private int mParentSurfaceSequenceId;
    private SurfaceViewPositionUpdateListener mPositionListener;
    private final Rect mRTLastReportedPosition;
    private final Point mRTLastReportedSurfaceSize;
    private RemoteAccessibilityController mRemoteAccessibilityController;
    int mRequestedFormat;
    int mRequestedHeight;
    int mRequestedSubLayer;
    private int mRequestedSurfaceLifecycleStrategy;
    boolean mRequestedVisible;
    int mRequestedWidth;
    Paint mRoundedViewportPaint;
    private final SurfaceControl.Transaction mRtTransaction;
    final Rect mScreenRect;
    private final ViewTreeObserver.OnScrollChangedListener mScrollChangedListener;
    int mSubLayer;
    final Surface mSurface;
    SurfaceControl mSurfaceControl;
    final Object mSurfaceControlLock;
    boolean mSurfaceCreated;
    private int mSurfaceFlags;
    final Rect mSurfaceFrame;
    int mSurfaceHeight;
    private final SurfaceHolder mSurfaceHolder;
    private int mSurfaceLifecycleStrategy;
    final ReentrantLock mSurfaceLock;
    SurfaceControlViewHost.SurfacePackage mSurfacePackage;
    private final SurfaceSession mSurfaceSession;
    int mSurfaceWidth;
    private final ArraySet<SurfaceSyncGroup> mSyncGroups;
    private final Matrix mTmpMatrix;
    final Rect mTmpRect;
    int mTransformHint;
    boolean mViewVisibility;
    boolean mVisible;
    int mWindowSpaceLeft;
    int mWindowSpaceTop;
    boolean mWindowStopped;
    boolean mWindowVisibility;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes4.dex */
    public @interface SurfaceLifecycleStrategy {
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$new$0() {
        this.mHaveFrame = getWidth() > 0 && getHeight() > 0;
        updateSurface();
        return true;
    }

    public SurfaceView(Context context) {
        this(context, null);
    }

    public SurfaceView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public SurfaceView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        this(context, attrs, defStyleAttr, defStyleRes, false);
    }

    public SurfaceView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes, boolean disableBackgroundLayer) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.mCallbacks = new ArrayList<>();
        this.mLocation = new int[2];
        this.mSurfaceLock = new ReentrantLock();
        this.mSurface = new Surface();
        this.mDrawingStopped = true;
        this.mDrawFinished = false;
        this.mScreenRect = new Rect();
        this.mSurfaceSession = new SurfaceSession();
        this.mDisableBackgroundLayer = false;
        this.mRequestedSurfaceLifecycleStrategy = 0;
        this.mSurfaceLifecycleStrategy = 0;
        this.mSurfaceControlLock = new Object();
        this.mTmpRect = new Rect();
        this.mSubLayer = -2;
        this.mRequestedSubLayer = -2;
        this.mIsCreating = false;
        this.mScrollChangedListener = new ViewTreeObserver.OnScrollChangedListener() { // from class: android.view.SurfaceView$$ExternalSyntheticLambda1
            @Override // android.view.ViewTreeObserver.OnScrollChangedListener
            public final void onScrollChanged() {
                SurfaceView.this.updateSurface();
            }
        };
        this.mDrawListener = new ViewTreeObserver.OnPreDrawListener() { // from class: android.view.SurfaceView$$ExternalSyntheticLambda2
            @Override // android.view.ViewTreeObserver.OnPreDrawListener
            public final boolean onPreDraw() {
                boolean lambda$new$0;
                lambda$new$0 = SurfaceView.this.lambda$new$0();
                return lambda$new$0;
            }
        };
        this.mRequestedVisible = false;
        this.mWindowVisibility = false;
        this.mLastWindowVisibility = false;
        this.mViewVisibility = false;
        this.mWindowStopped = false;
        this.mRequestedWidth = -1;
        this.mRequestedHeight = -1;
        this.mRequestedFormat = 4;
        this.mAlpha = 1.0f;
        this.mBackgroundColor = -16777216;
        this.mHaveFrame = false;
        this.mSurfaceCreated = false;
        this.mLastLockTime = 0L;
        this.mVisible = false;
        this.mWindowSpaceLeft = -1;
        this.mWindowSpaceTop = -1;
        this.mSurfaceWidth = -1;
        this.mSurfaceHeight = -1;
        this.mFormat = -1;
        this.mSurfaceFrame = new Rect();
        this.mLastSurfaceWidth = -1;
        this.mLastSurfaceHeight = -1;
        this.mTransformHint = 0;
        this.mSurfaceFlags = 4;
        this.mSyncGroups = new ArraySet<>();
        this.mRtTransaction = new SurfaceControl.Transaction();
        this.mFrameCallbackTransaction = new SurfaceControl.Transaction();
        this.mRemoteAccessibilityController = new RemoteAccessibilityController(this);
        this.mTmpMatrix = new Matrix();
        this.mRTLastReportedPosition = new Rect();
        this.mRTLastReportedSurfaceSize = new Point();
        this.mPositionListener = null;
        this.mSurfaceHolder = new SurfaceHolderC35271();
        setWillNotDraw(true);
        this.mDisableBackgroundLayer = disableBackgroundLayer;
    }

    public SurfaceHolder getHolder() {
        return this.mSurfaceHolder;
    }

    private void updateRequestedVisibility() {
        this.mRequestedVisible = this.mViewVisibility && this.mWindowVisibility && !this.mWindowStopped;
    }

    private void setWindowStopped(boolean stopped) {
        this.mWindowStopped = stopped;
        updateRequestedVisibility();
        updateSurface();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        getViewRootImpl().addSurfaceChangedCallback(this);
        this.mWindowStopped = false;
        this.mViewVisibility = getVisibility() == 0;
        updateRequestedVisibility();
        this.mAttachedToWindow = true;
        this.mParent.requestTransparentRegion(this);
        if (!this.mGlobalListenersAdded) {
            ViewTreeObserver observer = getViewTreeObserver();
            observer.addOnScrollChangedListener(this.mScrollChangedListener);
            observer.addOnPreDrawListener(this.mDrawListener);
            this.mGlobalListenersAdded = true;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        this.mWindowVisibility = visibility == 0;
        updateRequestedVisibility();
        updateSurface();
    }

    @Override // android.view.View
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        boolean newRequestedVisible = true;
        boolean z = visibility == 0;
        this.mViewVisibility = z;
        if (!this.mWindowVisibility || !z || this.mWindowStopped) {
            newRequestedVisible = false;
        }
        if (newRequestedVisible != this.mRequestedVisible) {
            requestLayout();
        }
        this.mRequestedVisible = newRequestedVisible;
        updateSurface();
    }

    public void setUseAlpha() {
    }

    @Override // android.view.View
    public void setAlpha(float alpha) {
        super.setAlpha(alpha);
    }

    @Override // android.view.View
    protected boolean onSetAlpha(int alpha) {
        if (Math.round(this.mAlpha * 255.0f) != alpha) {
            updateSurface();
            return true;
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void performDrawFinished() {
        this.mDrawFinished = true;
        if (this.mAttachedToWindow) {
            this.mParent.requestTransparentRegion(this);
            invalidate();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onDetachedFromWindow() {
        ViewRootImpl viewRoot = getViewRootImpl();
        if (viewRoot != null) {
            viewRoot.removeSurfaceChangedCallback(this);
        }
        this.mAttachedToWindow = false;
        if (this.mGlobalListenersAdded) {
            ViewTreeObserver observer = getViewTreeObserver();
            observer.removeOnScrollChangedListener(this.mScrollChangedListener);
            observer.removeOnPreDrawListener(this.mDrawListener);
            this.mGlobalListenersAdded = false;
        }
        this.mRequestedVisible = false;
        updateSurface();
        releaseSurfaces(true);
        this.mHaveFrame = false;
        super.onDetachedFromWindow();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width;
        int height;
        int i = this.mRequestedWidth;
        if (i >= 0) {
            width = resolveSizeAndState(i, widthMeasureSpec, 0);
        } else {
            width = getDefaultSize(0, widthMeasureSpec);
        }
        int i2 = this.mRequestedHeight;
        if (i2 >= 0) {
            height = resolveSizeAndState(i2, heightMeasureSpec, 0);
        } else {
            height = getDefaultSize(0, heightMeasureSpec);
        }
        setMeasuredDimension(width, height);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public boolean setFrame(int left, int top, int right, int bottom) {
        boolean result = super.setFrame(left, top, right, bottom);
        updateSurface();
        return result;
    }

    @Override // android.view.View
    public boolean gatherTransparentRegion(Region region) {
        if (isAboveParent() || !this.mDrawFinished) {
            return super.gatherTransparentRegion(region);
        }
        boolean opaque = true;
        if ((this.mPrivateFlags & 128) == 0) {
            opaque = super.gatherTransparentRegion(region);
        } else if (region != null) {
            int w = getWidth();
            int h = getHeight();
            if (w > 0 && h > 0) {
                getLocationInWindow(this.mLocation);
                int[] iArr = this.mLocation;
                int l = iArr[0];
                int t = iArr[1];
                region.m180op(l, t, l + w, t + h, Region.EnumC0813Op.UNION);
            }
        }
        if (PixelFormat.formatHasAlpha(this.mRequestedFormat)) {
            return false;
        }
        return opaque;
    }

    @Override // android.view.View
    public void draw(Canvas canvas) {
        if (this.mDrawFinished && !isAboveParent() && (this.mPrivateFlags & 128) == 0) {
            clearSurfaceViewPort(canvas);
        }
        super.draw(canvas);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void dispatchDraw(Canvas canvas) {
        if (this.mDrawFinished && !isAboveParent() && (this.mPrivateFlags & 128) == 128) {
            clearSurfaceViewPort(canvas);
        }
        super.dispatchDraw(canvas);
    }

    public void setEnableSurfaceClipping(boolean enabled) {
        this.mClipSurfaceToBounds = enabled;
        invalidate();
    }

    @Override // android.view.View
    public void setClipBounds(Rect clipBounds) {
        super.setClipBounds(clipBounds);
        if (!this.mClipSurfaceToBounds || this.mSurfaceControl == null) {
            return;
        }
        if (this.mCornerRadius > 0.0f && !isAboveParent()) {
            invalidate();
        }
        if (this.mClipBounds != null) {
            this.mTmpRect.set(this.mClipBounds);
        } else {
            this.mTmpRect.set(0, 0, this.mSurfaceWidth, this.mSurfaceHeight);
        }
        SurfaceControl.Transaction transaction = new SurfaceControl.Transaction();
        transaction.setWindowCrop(this.mSurfaceControl, this.mTmpRect);
        applyTransactionOnVriDraw(transaction);
        invalidate();
    }

    @Override // android.view.View
    public boolean hasOverlappingRendering() {
        return false;
    }

    private void clearSurfaceViewPort(Canvas canvas) {
        float alpha = getAlpha();
        if (this.mCornerRadius > 0.0f) {
            canvas.getClipBounds(this.mTmpRect);
            if (this.mClipSurfaceToBounds && this.mClipBounds != null) {
                this.mTmpRect.intersect(this.mClipBounds);
            }
            float f = this.mCornerRadius;
            canvas.punchHole(this.mTmpRect.left, this.mTmpRect.top, this.mTmpRect.right, this.mTmpRect.bottom, f, f, alpha);
            return;
        }
        canvas.punchHole(0.0f, 0.0f, getWidth(), getHeight(), 0.0f, 0.0f, alpha);
    }

    public void setCornerRadius(float cornerRadius) {
        this.mCornerRadius = cornerRadius;
        if (cornerRadius > 0.0f && this.mRoundedViewportPaint == null) {
            Paint paint = new Paint(1);
            this.mRoundedViewportPaint = paint;
            paint.setBlendMode(BlendMode.CLEAR);
            this.mRoundedViewportPaint.setColor(0);
        }
        invalidate();
    }

    public float getCornerRadius() {
        return this.mCornerRadius;
    }

    public void setZOrderMediaOverlay(boolean isMediaOverlay) {
        this.mRequestedSubLayer = isMediaOverlay ? -1 : -2;
    }

    public void setZOrderOnTop(boolean onTop) {
        boolean allowDynamicChange = getContext().getApplicationInfo().targetSdkVersion > 29;
        setZOrderedOnTop(onTop, allowDynamicChange);
    }

    public boolean isZOrderedOnTop() {
        return this.mRequestedSubLayer > 0;
    }

    public boolean setZOrderedOnTop(boolean onTop, boolean allowDynamicChange) {
        int subLayer;
        if (onTop) {
            subLayer = 1;
        } else {
            subLayer = -2;
        }
        if (this.mRequestedSubLayer == subLayer) {
            return false;
        }
        this.mRequestedSubLayer = subLayer;
        if (allowDynamicChange) {
            if (this.mSurfaceControl == null) {
                return true;
            }
            ViewRootImpl viewRoot = getViewRootImpl();
            if (viewRoot == null) {
                return true;
            }
            updateSurface();
            invalidate();
            return true;
        }
        return false;
    }

    public void setSecure(boolean isSecure) {
        if (isSecure) {
            this.mSurfaceFlags |= 128;
        } else {
            this.mSurfaceFlags &= PackageManager.INSTALL_FAILED_PRE_APPROVAL_NOT_AVAILABLE;
        }
    }

    public void setSurfaceLifecycle(int lifecycleStrategy) {
        this.mRequestedSurfaceLifecycleStrategy = lifecycleStrategy;
        updateSurface();
    }

    private void updateOpaqueFlag() {
        if (!PixelFormat.formatHasAlpha(this.mRequestedFormat)) {
            this.mSurfaceFlags |= 1024;
        } else {
            this.mSurfaceFlags &= -1025;
        }
    }

    private void updateBackgroundVisibility(SurfaceControl.Transaction t) {
        SurfaceControl surfaceControl = this.mBackgroundControl;
        if (surfaceControl == null) {
            return;
        }
        if (this.mSubLayer < 0 && (this.mSurfaceFlags & 1024) != 0 && !this.mDisableBackgroundLayer) {
            t.show(surfaceControl);
        } else {
            t.hide(surfaceControl);
        }
    }

    private SurfaceControl.Transaction updateBackgroundColor(SurfaceControl.Transaction t) {
        float[] colorComponents = {Color.red(this.mBackgroundColor) / 255.0f, Color.green(this.mBackgroundColor) / 255.0f, Color.blue(this.mBackgroundColor) / 255.0f};
        t.setColor(this.mBackgroundControl, colorComponents);
        return t;
    }

    private void releaseSurfaces(boolean releaseSurfacePackage) {
        SurfaceControlViewHost.SurfacePackage surfacePackage;
        this.mAlpha = 1.0f;
        this.mSurface.destroy();
        synchronized (this.mSurfaceControlLock) {
            BLASTBufferQueue bLASTBufferQueue = this.mBlastBufferQueue;
            if (bLASTBufferQueue != null) {
                bLASTBufferQueue.destroy();
                this.mBlastBufferQueue = null;
            }
            SurfaceControl.Transaction transaction = new SurfaceControl.Transaction();
            SurfaceControl surfaceControl = this.mSurfaceControl;
            if (surfaceControl != null) {
                transaction.remove(surfaceControl);
                this.mSurfaceControl = null;
            }
            SurfaceControl surfaceControl2 = this.mBackgroundControl;
            if (surfaceControl2 != null) {
                transaction.remove(surfaceControl2);
                this.mBackgroundControl = null;
            }
            SurfaceControl surfaceControl3 = this.mBlastSurfaceControl;
            if (surfaceControl3 != null) {
                transaction.remove(surfaceControl3);
                this.mBlastSurfaceControl = null;
            }
            if (releaseSurfacePackage && (surfacePackage = this.mSurfacePackage) != null) {
                surfacePackage.release();
                this.mSurfacePackage = null;
            }
            applyTransactionOnVriDraw(transaction);
        }
    }

    private void replacePositionUpdateListener(int surfaceWidth, int surfaceHeight) {
        if (this.mPositionListener != null) {
            this.mRenderNode.removePositionUpdateListener(this.mPositionListener);
        }
        this.mPositionListener = new SurfaceViewPositionUpdateListener(surfaceWidth, surfaceHeight);
        this.mRenderNode.addPositionUpdateListener(this.mPositionListener);
    }

    private boolean performSurfaceTransaction(ViewRootImpl viewRoot, CompatibilityInfo.Translator translator, boolean creating, boolean sizeChanged, boolean hintChanged, boolean relativeZChanged, SurfaceControl.Transaction surfaceUpdateTransaction) {
        this.mSurfaceLock.lock();
        try {
            boolean z = true;
            this.mDrawingStopped = !surfaceShouldExist();
            if (creating) {
                updateRelativeZ(surfaceUpdateTransaction);
                SurfaceControlViewHost.SurfacePackage surfacePackage = this.mSurfacePackage;
                if (surfacePackage != null) {
                    reparentSurfacePackage(surfaceUpdateTransaction, surfacePackage);
                }
            }
            this.mParentSurfaceSequenceId = viewRoot.getSurfaceSequenceId();
            if (this.mViewVisibility) {
                surfaceUpdateTransaction.show(this.mSurfaceControl);
            } else {
                surfaceUpdateTransaction.hide(this.mSurfaceControl);
            }
            updateBackgroundVisibility(surfaceUpdateTransaction);
            updateBackgroundColor(surfaceUpdateTransaction);
            if (isAboveParent()) {
                float alpha = getAlpha();
                surfaceUpdateTransaction.setAlpha(this.mSurfaceControl, alpha);
            }
            if (relativeZChanged) {
                if (!isAboveParent()) {
                    surfaceUpdateTransaction.setAlpha(this.mSurfaceControl, 1.0f);
                }
                updateRelativeZ(surfaceUpdateTransaction);
            }
            surfaceUpdateTransaction.setCornerRadius(this.mSurfaceControl, this.mCornerRadius);
            if ((sizeChanged || hintChanged) && !creating) {
                setBufferSize(surfaceUpdateTransaction);
            }
            if (sizeChanged || creating || !isHardwareAccelerated()) {
                if (!this.mClipSurfaceToBounds || this.mClipBounds == null) {
                    surfaceUpdateTransaction.setWindowCrop(this.mSurfaceControl, this.mSurfaceWidth, this.mSurfaceHeight);
                } else {
                    surfaceUpdateTransaction.setWindowCrop(this.mSurfaceControl, this.mClipBounds);
                }
                surfaceUpdateTransaction.setDesintationFrame(this.mBlastSurfaceControl, this.mSurfaceWidth, this.mSurfaceHeight);
                if (isHardwareAccelerated()) {
                    replacePositionUpdateListener(this.mSurfaceWidth, this.mSurfaceHeight);
                } else {
                    onSetSurfacePositionAndScale(surfaceUpdateTransaction, this.mSurfaceControl, this.mScreenRect.left, this.mScreenRect.top, this.mScreenRect.width() / this.mSurfaceWidth, this.mScreenRect.height() / this.mSurfaceHeight);
                }
            }
            applyTransactionOnVriDraw(surfaceUpdateTransaction);
            updateEmbeddedAccessibilityMatrix(false);
            this.mSurfaceFrame.left = 0;
            this.mSurfaceFrame.top = 0;
            if (translator == null) {
                this.mSurfaceFrame.right = this.mSurfaceWidth;
                this.mSurfaceFrame.bottom = this.mSurfaceHeight;
            } else {
                float appInvertedScale = translator.applicationInvertedScale;
                this.mSurfaceFrame.right = (int) ((this.mSurfaceWidth * appInvertedScale) + 0.5f);
                this.mSurfaceFrame.bottom = (int) ((this.mSurfaceHeight * appInvertedScale) + 0.5f);
            }
            int surfaceWidth = this.mSurfaceFrame.right;
            int surfaceHeight = this.mSurfaceFrame.bottom;
            if (this.mLastSurfaceWidth == surfaceWidth && this.mLastSurfaceHeight == surfaceHeight) {
                z = false;
            }
            boolean realSizeChanged = z;
            this.mLastSurfaceWidth = surfaceWidth;
            this.mLastSurfaceHeight = surfaceHeight;
            return realSizeChanged;
        } finally {
            this.mSurfaceLock.unlock();
        }
    }

    private boolean requiresSurfaceControlCreation(boolean formatChanged, boolean visibleChanged) {
        return this.mSurfaceLifecycleStrategy == 2 ? (this.mSurfaceControl == null || formatChanged) && this.mAttachedToWindow : (this.mSurfaceControl == null || formatChanged || visibleChanged) && this.mRequestedVisible;
    }

    private boolean surfaceShouldExist() {
        boolean respectVisibility = this.mSurfaceLifecycleStrategy != 2;
        if (this.mVisible) {
            return true;
        }
        return !respectVisibility && this.mAttachedToWindow;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* JADX WARN: Code restructure failed: missing block: B:150:0x022e, code lost:
        if (r35.mAttachedToWindow != false) goto L126;
     */
    /* JADX WARN: Code restructure failed: missing block: B:155:0x0238, code lost:
        if (r6 != false) goto L142;
     */
    /* JADX WARN: Code restructure failed: missing block: B:191:0x02b5, code lost:
        r2 = getSurfaceCallbacks();
     */
    /* JADX WARN: Removed duplicated region for block: B:126:0x01d1 A[Catch: Exception -> 0x0154, TRY_LEAVE, TryCatch #7 {Exception -> 0x0154, blocks: (B:91:0x014e, B:98:0x0175, B:107:0x01a9, B:109:0x01ad, B:118:0x01bd, B:120:0x01c3, B:126:0x01d1), top: B:248:0x014e }] */
    /* JADX WARN: Removed duplicated region for block: B:128:0x01ea  */
    /* JADX WARN: Removed duplicated region for block: B:134:0x0212  */
    /* JADX WARN: Removed duplicated region for block: B:135:0x0214  */
    /* JADX WARN: Removed duplicated region for block: B:138:0x0218  */
    /* JADX WARN: Removed duplicated region for block: B:139:0x021a  */
    /* JADX WARN: Removed duplicated region for block: B:147:0x0228  */
    /* JADX WARN: Removed duplicated region for block: B:199:0x02dc  */
    /* JADX WARN: Removed duplicated region for block: B:221:0x0316 A[Catch: Exception -> 0x0320, TryCatch #0 {Exception -> 0x0320, blocks: (B:219:0x030f, B:221:0x0316, B:223:0x031a, B:225:0x031f, B:209:0x02f5, B:211:0x02fb, B:213:0x02ff), top: B:237:0x020d }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public void updateSurface() {
        ViewRootImpl viewRoot;
        boolean z;
        boolean shouldSyncBuffer;
        SyncBufferTransactionCallback syncBufferTransactionCallback;
        if (this.mHaveFrame && (viewRoot = getViewRootImpl()) != null) {
            if (viewRoot.mSurface != null && viewRoot.mSurface.isValid()) {
                CompatibilityInfo.Translator translator = viewRoot.mTranslator;
                if (translator != null) {
                    this.mSurface.setCompatibilityTranslator(translator);
                }
                int myWidth = this.mRequestedWidth;
                if (myWidth <= 0) {
                    myWidth = getWidth();
                }
                int myWidth2 = myWidth;
                int myHeight = this.mRequestedHeight;
                if (myHeight <= 0) {
                    myHeight = getHeight();
                }
                int myHeight2 = myHeight;
                float alpha = getAlpha();
                boolean formatChanged = this.mFormat != this.mRequestedFormat;
                boolean visibleChanged = this.mVisible != this.mRequestedVisible;
                boolean alphaChanged = this.mAlpha != alpha;
                boolean creating = requiresSurfaceControlCreation(formatChanged, visibleChanged);
                boolean sizeChanged = (this.mSurfaceWidth == myWidth2 && this.mSurfaceHeight == myHeight2) ? false : true;
                boolean windowVisibleChanged = this.mWindowVisibility != this.mLastWindowVisibility;
                getLocationInWindow(this.mLocation);
                int i = this.mWindowSpaceLeft;
                int[] iArr = this.mLocation;
                boolean positionChanged = (i == iArr[0] && this.mWindowSpaceTop == iArr[1]) ? false : true;
                boolean layoutSizeChanged = (getWidth() == this.mScreenRect.width() && getHeight() == this.mScreenRect.height()) ? false : true;
                boolean hintChanged = viewRoot.getBufferTransformHint() != this.mTransformHint && this.mRequestedVisible;
                boolean relativeZChanged = this.mSubLayer != this.mRequestedSubLayer;
                boolean surfaceLifecycleStrategyChanged = this.mSurfaceLifecycleStrategy != this.mRequestedSurfaceLifecycleStrategy;
                if (!creating && !formatChanged && !sizeChanged && !visibleChanged && !alphaChanged && !windowVisibleChanged && !positionChanged && !layoutSizeChanged && !hintChanged && !relativeZChanged && this.mAttachedToWindow && !surfaceLifecycleStrategyChanged) {
                    return;
                }
                try {
                    this.mVisible = this.mRequestedVisible;
                    int[] iArr2 = this.mLocation;
                    this.mWindowSpaceLeft = iArr2[0];
                    this.mWindowSpaceTop = iArr2[1];
                    this.mSurfaceWidth = myWidth2;
                    this.mSurfaceHeight = myHeight2;
                    this.mFormat = this.mRequestedFormat;
                    this.mAlpha = alpha;
                    this.mLastWindowVisibility = this.mWindowVisibility;
                    this.mTransformHint = viewRoot.getBufferTransformHint();
                    this.mSubLayer = this.mRequestedSubLayer;
                    int previousSurfaceLifecycleStrategy = this.mSurfaceLifecycleStrategy;
                    this.mSurfaceLifecycleStrategy = this.mRequestedSurfaceLifecycleStrategy;
                    this.mScreenRect.left = this.mWindowSpaceLeft;
                    this.mScreenRect.top = this.mWindowSpaceTop;
                    this.mScreenRect.right = this.mWindowSpaceLeft + getWidth();
                    this.mScreenRect.bottom = this.mWindowSpaceTop + getHeight();
                    if (translator != null) {
                        try {
                            translator.translateRectInAppWindowToScreen(this.mScreenRect);
                        } catch (Exception e) {
                            ex = e;
                            Log.m109e(TAG, "Exception configuring surface", ex);
                            return;
                        }
                    }
                    Rect surfaceInsets = viewRoot.mWindowAttributes.surfaceInsets;
                    this.mScreenRect.offset(surfaceInsets.left, surfaceInsets.top);
                    SurfaceControl.Transaction surfaceUpdateTransaction = new SurfaceControl.Transaction();
                    if (creating) {
                        updateOpaqueFlag();
                        String name = "SurfaceView[" + viewRoot.getTitle().toString() + NavigationBarInflaterView.SIZE_MOD_END;
                        createBlastSurfaceControls(viewRoot, name, surfaceUpdateTransaction);
                    } else if (this.mSurfaceControl == null) {
                        return;
                    }
                    try {
                        try {
                            try {
                                try {
                                    if (!sizeChanged && !creating && !hintChanged && ((!this.mVisible || this.mDrawFinished) && !alphaChanged && !relativeZChanged)) {
                                        z = false;
                                        boolean redrawNeeded = z;
                                        shouldSyncBuffer = !redrawNeeded && viewRoot.wasRelayoutRequested() && viewRoot.isInWMSRequestedSync();
                                        if (shouldSyncBuffer) {
                                            syncBufferTransactionCallback = null;
                                        } else {
                                            final SyncBufferTransactionCallback syncBufferTransactionCallback2 = new SyncBufferTransactionCallback();
                                            BLASTBufferQueue bLASTBufferQueue = this.mBlastBufferQueue;
                                            Objects.requireNonNull(syncBufferTransactionCallback2);
                                            bLASTBufferQueue.syncNextTransaction(false, new Consumer() { // from class: android.view.SurfaceView$$ExternalSyntheticLambda4
                                                @Override // java.util.function.Consumer
                                                public final void accept(Object obj) {
                                                    SurfaceView.SyncBufferTransactionCallback.this.onTransactionReady((SurfaceControl.Transaction) obj);
                                                }
                                            });
                                            syncBufferTransactionCallback = syncBufferTransactionCallback2;
                                        }
                                        boolean realSizeChanged = performSurfaceTransaction(viewRoot, translator, creating, sizeChanged, hintChanged, relativeZChanged, surfaceUpdateTransaction);
                                        SurfaceHolder.Callback[] callbacks = null;
                                        boolean respectVisibility = this.mSurfaceLifecycleStrategy == 2;
                                        boolean previouslyDidNotRespectVisibility = previousSurfaceLifecycleStrategy != 2;
                                        boolean lifecycleNewlyRespectsVisibility = !respectVisibility && previouslyDidNotRespectVisibility;
                                        if (this.mSurfaceCreated) {
                                            if (!creating) {
                                                if (!respectVisibility) {
                                                    try {
                                                    } catch (Throwable th) {
                                                        th = th;
                                                        this.mIsCreating = false;
                                                        if (this.mSurfaceControl != null && !this.mSurfaceCreated) {
                                                            releaseSurfaces(false);
                                                        }
                                                        throw th;
                                                    }
                                                }
                                                if (respectVisibility) {
                                                    if (!this.mVisible) {
                                                        if (!visibleChanged) {
                                                        }
                                                    }
                                                }
                                            }
                                            this.mSurfaceCreated = false;
                                            notifySurfaceDestroyed();
                                        }
                                        copySurface(creating, sizeChanged);
                                        if (!surfaceShouldExist() && this.mSurface.isValid()) {
                                            if (!this.mSurfaceCreated) {
                                                if (creating || (respectVisibility && visibleChanged)) {
                                                    try {
                                                        this.mSurfaceCreated = true;
                                                        this.mIsCreating = true;
                                                        callbacks = getSurfaceCallbacks();
                                                        int length = callbacks.length;
                                                        int i2 = 0;
                                                        while (i2 < length) {
                                                            SurfaceHolder.Callback c = callbacks[i2];
                                                            c.surfaceCreated(this.mSurfaceHolder);
                                                            i2++;
                                                            length = length;
                                                            callbacks = callbacks;
                                                        }
                                                    } catch (Throwable th2) {
                                                        th = th2;
                                                        this.mIsCreating = false;
                                                        if (this.mSurfaceControl != null) {
                                                        }
                                                        throw th;
                                                    }
                                                }
                                            }
                                            if (!creating && !formatChanged && !sizeChanged && !hintChanged && ((!respectVisibility || !visibleChanged) && !realSizeChanged)) {
                                                if (redrawNeeded) {
                                                    if (callbacks == null) {
                                                        callbacks = getSurfaceCallbacks();
                                                    }
                                                    if (shouldSyncBuffer) {
                                                        handleSyncBufferCallback(callbacks, syncBufferTransactionCallback);
                                                    } else {
                                                        handleSyncNoBuffer(callbacks);
                                                    }
                                                }
                                            }
                                            int length2 = callbacks.length;
                                            int i3 = 0;
                                            while (i3 < length2) {
                                                SurfaceHolder.Callback c2 = callbacks[i3];
                                                int i4 = length2;
                                                boolean realSizeChanged2 = realSizeChanged;
                                                try {
                                                    c2.surfaceChanged(this.mSurfaceHolder, this.mFormat, myWidth2, myHeight2);
                                                    i3++;
                                                    length2 = i4;
                                                    realSizeChanged = realSizeChanged2;
                                                    callbacks = callbacks;
                                                } catch (Throwable th3) {
                                                    th = th3;
                                                    this.mIsCreating = false;
                                                    if (this.mSurfaceControl != null) {
                                                        releaseSurfaces(false);
                                                    }
                                                    throw th;
                                                }
                                            }
                                            if (redrawNeeded) {
                                            }
                                        }
                                        this.mIsCreating = false;
                                        if (this.mSurfaceControl != null && !this.mSurfaceCreated) {
                                            releaseSurfaces(false);
                                        }
                                        return;
                                    }
                                    copySurface(creating, sizeChanged);
                                    if (!surfaceShouldExist()) {
                                    }
                                    this.mIsCreating = false;
                                    if (this.mSurfaceControl != null) {
                                        releaseSurfaces(false);
                                    }
                                    return;
                                } catch (Throwable th4) {
                                    th = th4;
                                }
                                boolean respectVisibility2 = this.mSurfaceLifecycleStrategy == 2;
                                boolean previouslyDidNotRespectVisibility2 = previousSurfaceLifecycleStrategy != 2;
                                boolean lifecycleNewlyRespectsVisibility2 = !respectVisibility2 && previouslyDidNotRespectVisibility2;
                                if (this.mSurfaceCreated) {
                                }
                            } catch (Exception e2) {
                                ex = e2;
                                Log.m109e(TAG, "Exception configuring surface", ex);
                                return;
                            }
                        } catch (Throwable th5) {
                            th = th5;
                        }
                        boolean realSizeChanged3 = performSurfaceTransaction(viewRoot, translator, creating, sizeChanged, hintChanged, relativeZChanged, surfaceUpdateTransaction);
                        SurfaceHolder.Callback[] callbacks2 = null;
                    } catch (Exception e3) {
                        ex = e3;
                    }
                    z = true;
                    boolean redrawNeeded2 = z;
                    shouldSyncBuffer = !redrawNeeded2 && viewRoot.wasRelayoutRequested() && viewRoot.isInWMSRequestedSync();
                    if (shouldSyncBuffer) {
                    }
                } catch (Exception e4) {
                    ex = e4;
                }
            }
            notifySurfaceDestroyed();
            releaseSurfaces(false);
        }
    }

    public String getName() {
        ViewRootImpl viewRoot = getViewRootImpl();
        String viewRootName = viewRoot == null ? "detached" : viewRoot.getTitle().toString();
        return "SurfaceView[" + viewRootName + NavigationBarInflaterView.SIZE_MOD_END;
    }

    private void handleSyncBufferCallback(SurfaceHolder.Callback[] callbacks, final SyncBufferTransactionCallback syncBufferTransactionCallback) {
        final SurfaceSyncGroup surfaceSyncGroup = new SurfaceSyncGroup(getName());
        getViewRootImpl().addToSync(surfaceSyncGroup);
        redrawNeededAsync(callbacks, new Runnable() { // from class: android.view.SurfaceView$$ExternalSyntheticLambda3
            @Override // java.lang.Runnable
            public final void run() {
                SurfaceView.this.lambda$handleSyncBufferCallback$1(syncBufferTransactionCallback, surfaceSyncGroup);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$handleSyncBufferCallback$1(SyncBufferTransactionCallback syncBufferTransactionCallback, SurfaceSyncGroup surfaceSyncGroup) {
        SurfaceControl.Transaction t = null;
        BLASTBufferQueue bLASTBufferQueue = this.mBlastBufferQueue;
        if (bLASTBufferQueue != null) {
            bLASTBufferQueue.stopContinuousSyncTransaction();
            t = syncBufferTransactionCallback.waitForTransaction();
        }
        surfaceSyncGroup.addTransaction(t);
        surfaceSyncGroup.markSyncReady();
        onDrawFinished();
    }

    private void handleSyncNoBuffer(SurfaceHolder.Callback[] callbacks) {
        final SurfaceSyncGroup surfaceSyncGroup = new SurfaceSyncGroup(getName());
        synchronized (this.mSyncGroups) {
            this.mSyncGroups.add(surfaceSyncGroup);
        }
        redrawNeededAsync(callbacks, new Runnable() { // from class: android.view.SurfaceView$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                SurfaceView.this.lambda$handleSyncNoBuffer$2(surfaceSyncGroup);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$handleSyncNoBuffer$2(SurfaceSyncGroup surfaceSyncGroup) {
        synchronized (this.mSyncGroups) {
            this.mSyncGroups.remove(surfaceSyncGroup);
        }
        surfaceSyncGroup.markSyncReady();
        onDrawFinished();
    }

    private void redrawNeededAsync(SurfaceHolder.Callback[] callbacks, Runnable callbacksCollected) {
        SurfaceCallbackHelper sch = new SurfaceCallbackHelper(callbacksCollected);
        sch.dispatchSurfaceRedrawNeededAsync(this.mSurfaceHolder, callbacks);
    }

    @Override // android.view.ViewRootImpl.SurfaceChangedCallback
    public void vriDrawStarted(boolean isWmSync) {
        ViewRootImpl viewRoot = getViewRootImpl();
        synchronized (this.mSyncGroups) {
            if (isWmSync && viewRoot != null) {
                Iterator<SurfaceSyncGroup> it = this.mSyncGroups.iterator();
                while (it.hasNext()) {
                    SurfaceSyncGroup syncGroup = it.next();
                    viewRoot.addToSync(syncGroup);
                }
            }
            this.mSyncGroups.clear();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public static class SyncBufferTransactionCallback {
        private final CountDownLatch mCountDownLatch;
        private SurfaceControl.Transaction mTransaction;

        private SyncBufferTransactionCallback() {
            this.mCountDownLatch = new CountDownLatch(1);
        }

        SurfaceControl.Transaction waitForTransaction() {
            try {
                this.mCountDownLatch.await();
            } catch (InterruptedException e) {
            }
            return this.mTransaction;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public void onTransactionReady(SurfaceControl.Transaction t) {
            this.mTransaction = t;
            this.mCountDownLatch.countDown();
        }
    }

    private void copySurface(boolean surfaceControlCreated, boolean bufferSizeChanged) {
        BLASTBufferQueue bLASTBufferQueue;
        if (surfaceControlCreated) {
            this.mSurface.copyFrom(this.mBlastBufferQueue);
        }
        if (bufferSizeChanged && getContext().getApplicationInfo().targetSdkVersion < 26 && (bLASTBufferQueue = this.mBlastBufferQueue) != null) {
            this.mSurface.transferFrom(bLASTBufferQueue.createSurfaceWithHandle());
        }
    }

    private void setBufferSize(SurfaceControl.Transaction transaction) {
        this.mBlastSurfaceControl.setTransformHint(this.mTransformHint);
        BLASTBufferQueue bLASTBufferQueue = this.mBlastBufferQueue;
        if (bLASTBufferQueue != null) {
            bLASTBufferQueue.update(this.mBlastSurfaceControl, this.mSurfaceWidth, this.mSurfaceHeight, this.mFormat);
        }
    }

    private void createBlastSurfaceControls(ViewRootImpl viewRoot, String name, SurfaceControl.Transaction surfaceUpdateTransaction) {
        if (this.mSurfaceControl == null) {
            this.mSurfaceControl = new SurfaceControl.Builder(this.mSurfaceSession).setName(name).setLocalOwnerView(this).setParent(viewRoot.getBoundsLayer()).setCallsite("SurfaceView.updateSurface").setContainerLayer().build();
        }
        SurfaceControl surfaceControl = this.mBlastSurfaceControl;
        if (surfaceControl == null) {
            this.mBlastSurfaceControl = new SurfaceControl.Builder(this.mSurfaceSession).setName(name + "(BLAST)").setLocalOwnerView(this).setParent(this.mSurfaceControl).setFlags(this.mSurfaceFlags).setHidden(false).setBLASTLayer().setCallsite("SurfaceView.updateSurface").build();
        } else {
            surfaceUpdateTransaction.setOpaque(surfaceControl, (this.mSurfaceFlags & 1024) != 0).setSecure(this.mBlastSurfaceControl, (this.mSurfaceFlags & 128) != 0).show(this.mBlastSurfaceControl);
        }
        if (this.mBackgroundControl == null) {
            this.mBackgroundControl = new SurfaceControl.Builder(this.mSurfaceSession).setName("Background for " + name).setLocalOwnerView(this).setOpaque(true).setColorLayer().setParent(this.mSurfaceControl).setCallsite("SurfaceView.updateSurface").build();
        }
        BLASTBufferQueue bLASTBufferQueue = this.mBlastBufferQueue;
        if (bLASTBufferQueue != null) {
            bLASTBufferQueue.destroy();
        }
        int bufferTransformHint = viewRoot.getBufferTransformHint();
        this.mTransformHint = bufferTransformHint;
        this.mBlastSurfaceControl.setTransformHint(bufferTransformHint);
        BLASTBufferQueue bLASTBufferQueue2 = new BLASTBufferQueue(name, false);
        this.mBlastBufferQueue = bLASTBufferQueue2;
        bLASTBufferQueue2.update(this.mBlastSurfaceControl, this.mSurfaceWidth, this.mSurfaceHeight, this.mFormat);
        this.mBlastBufferQueue.setTransactionHangCallback(ViewRootImpl.sTransactionHangCallback);
    }

    private void onDrawFinished() {
        runOnUiThread(new Runnable() { // from class: android.view.SurfaceView$$ExternalSyntheticLambda5
            @Override // java.lang.Runnable
            public final void run() {
                SurfaceView.this.performDrawFinished();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void onSetSurfacePositionAndScale(SurfaceControl.Transaction transaction, SurfaceControl surface, int positionLeft, int positionTop, float postScaleX, float postScaleY) {
        transaction.setPosition(surface, positionLeft, positionTop);
        transaction.setMatrix(surface, postScaleX, 0.0f, 0.0f, postScaleY);
    }

    public void requestUpdateSurfacePositionAndScale() {
        if (this.mSurfaceControl == null) {
            return;
        }
        SurfaceControl.Transaction transaction = new SurfaceControl.Transaction();
        onSetSurfacePositionAndScale(transaction, this.mSurfaceControl, this.mScreenRect.left, this.mScreenRect.top, this.mScreenRect.width() / this.mSurfaceWidth, this.mScreenRect.height() / this.mSurfaceHeight);
        applyTransactionOnVriDraw(transaction);
        invalidate();
    }

    public Rect getSurfaceRenderPosition() {
        return this.mRTLastReportedPosition;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void applyOrMergeTransaction(SurfaceControl.Transaction t, long frameNumber) {
        ViewRootImpl viewRoot = getViewRootImpl();
        if (viewRoot != null) {
            viewRoot.lambda$applyTransactionOnDraw$11(t, frameNumber);
        } else {
            t.apply();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public class SurfaceViewPositionUpdateListener implements RenderNode.PositionUpdateListener {
        private final int mRtSurfaceHeight;
        private final int mRtSurfaceWidth;
        private boolean mRtFirst = true;
        private final SurfaceControl.Transaction mPositionChangedTransaction = new SurfaceControl.Transaction();

        SurfaceViewPositionUpdateListener(int surfaceWidth, int surfaceHeight) {
            this.mRtSurfaceWidth = surfaceWidth;
            this.mRtSurfaceHeight = surfaceHeight;
        }

        @Override // android.graphics.RenderNode.PositionUpdateListener
        public void positionChanged(long frameNumber, int left, int top, int right, int bottom) {
            if (this.mRtFirst || SurfaceView.this.mRTLastReportedPosition.left != left || SurfaceView.this.mRTLastReportedPosition.top != top || SurfaceView.this.mRTLastReportedPosition.right != right || SurfaceView.this.mRTLastReportedPosition.bottom != bottom || SurfaceView.this.mRTLastReportedSurfaceSize.f76x != this.mRtSurfaceWidth || SurfaceView.this.mRTLastReportedSurfaceSize.f77y != this.mRtSurfaceHeight) {
                this.mRtFirst = false;
                try {
                    synchronized (SurfaceView.this.mSurfaceControlLock) {
                        try {
                            if (SurfaceView.this.mSurfaceControl == null) {
                                return;
                            }
                            SurfaceView.this.mRTLastReportedPosition.set(left, top, right, bottom);
                            SurfaceView.this.mRTLastReportedSurfaceSize.set(this.mRtSurfaceWidth, this.mRtSurfaceHeight);
                            SurfaceView surfaceView = SurfaceView.this;
                            surfaceView.onSetSurfacePositionAndScale(this.mPositionChangedTransaction, surfaceView.mSurfaceControl, SurfaceView.this.mRTLastReportedPosition.left, SurfaceView.this.mRTLastReportedPosition.top, SurfaceView.this.mRTLastReportedPosition.width() / this.mRtSurfaceWidth, SurfaceView.this.mRTLastReportedPosition.height() / this.mRtSurfaceHeight);
                            if (SurfaceView.this.mViewVisibility) {
                                this.mPositionChangedTransaction.show(SurfaceView.this.mSurfaceControl);
                            }
                            SurfaceView.this.applyOrMergeTransaction(this.mPositionChangedTransaction, frameNumber);
                        } catch (Exception e) {
                            ex = e;
                            Log.m109e(SurfaceView.TAG, "Exception from repositionChild", ex);
                        }
                    }
                } catch (Exception e2) {
                    ex = e2;
                }
            }
        }

        @Override // android.graphics.RenderNode.PositionUpdateListener
        public void applyStretch(long frameNumber, float width, float height, float vecX, float vecY, float maxStretchX, float maxStretchY, float childRelativeLeft, float childRelativeTop, float childRelativeRight, float childRelativeBottom) {
            SurfaceView.this.mRtTransaction.setStretchEffect(SurfaceView.this.mSurfaceControl, width, height, vecX, vecY, maxStretchX, maxStretchY, childRelativeLeft, childRelativeTop, childRelativeRight, childRelativeBottom);
            SurfaceView surfaceView = SurfaceView.this;
            surfaceView.applyOrMergeTransaction(surfaceView.mRtTransaction, frameNumber);
        }

        @Override // android.graphics.RenderNode.PositionUpdateListener
        public void positionLost(long frameNumber) {
            SurfaceView.this.mRTLastReportedPosition.setEmpty();
            SurfaceView.this.mRTLastReportedSurfaceSize.set(-1, -1);
            synchronized (SurfaceView.this.mSurfaceControlLock) {
                if (SurfaceView.this.mSurfaceControl == null) {
                    return;
                }
                SurfaceView.this.mRtTransaction.hide(SurfaceView.this.mSurfaceControl);
                SurfaceView surfaceView = SurfaceView.this;
                surfaceView.applyOrMergeTransaction(surfaceView.mRtTransaction, frameNumber);
            }
        }
    }

    private SurfaceHolder.Callback[] getSurfaceCallbacks() {
        SurfaceHolder.Callback[] callbacks;
        synchronized (this.mCallbacks) {
            callbacks = new SurfaceHolder.Callback[this.mCallbacks.size()];
            this.mCallbacks.toArray(callbacks);
        }
        return callbacks;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void runOnUiThread(Runnable runnable) {
        Handler handler = getHandler();
        if (handler != null && handler.getLooper() != Looper.myLooper()) {
            handler.post(runnable);
        } else {
            runnable.run();
        }
    }

    public boolean isFixedSize() {
        return (this.mRequestedWidth == -1 && this.mRequestedHeight == -1) ? false : true;
    }

    private boolean isAboveParent() {
        return this.mSubLayer >= 0;
    }

    public void setResizeBackgroundColor(int bgColor) {
        SurfaceControl.Transaction transaction = new SurfaceControl.Transaction();
        setResizeBackgroundColor(transaction, bgColor);
        applyTransactionOnVriDraw(transaction);
        invalidate();
    }

    public void setResizeBackgroundColor(SurfaceControl.Transaction t, int bgColor) {
        if (this.mBackgroundControl == null) {
            return;
        }
        this.mBackgroundColor = bgColor;
        updateBackgroundColor(t);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: android.view.SurfaceView$1 */
    /* loaded from: classes4.dex */
    public class SurfaceHolderC35271 implements SurfaceHolder {
        private static final String LOG_TAG = "SurfaceHolder";

        SurfaceHolderC35271() {
        }

        @Override // android.view.SurfaceHolder
        public boolean isCreating() {
            return SurfaceView.this.mIsCreating;
        }

        @Override // android.view.SurfaceHolder
        public void addCallback(SurfaceHolder.Callback callback) {
            synchronized (SurfaceView.this.mCallbacks) {
                if (!SurfaceView.this.mCallbacks.contains(callback)) {
                    SurfaceView.this.mCallbacks.add(callback);
                }
            }
        }

        @Override // android.view.SurfaceHolder
        public void removeCallback(SurfaceHolder.Callback callback) {
            synchronized (SurfaceView.this.mCallbacks) {
                SurfaceView.this.mCallbacks.remove(callback);
            }
        }

        @Override // android.view.SurfaceHolder
        public void setFixedSize(int width, int height) {
            if (SurfaceView.this.mRequestedWidth != width || SurfaceView.this.mRequestedHeight != height) {
                SurfaceView.this.mRequestedWidth = width;
                SurfaceView.this.mRequestedHeight = height;
                SurfaceView.this.requestLayout();
            }
        }

        @Override // android.view.SurfaceHolder
        public void setSizeFromLayout() {
            if (SurfaceView.this.mRequestedWidth != -1 || SurfaceView.this.mRequestedHeight != -1) {
                SurfaceView surfaceView = SurfaceView.this;
                surfaceView.mRequestedHeight = -1;
                surfaceView.mRequestedWidth = -1;
                SurfaceView.this.requestLayout();
            }
        }

        @Override // android.view.SurfaceHolder
        public void setFormat(int format) {
            if (format == -1) {
                format = 4;
            }
            SurfaceView.this.mRequestedFormat = format;
            if (SurfaceView.this.mSurfaceControl != null) {
                SurfaceView.this.updateSurface();
            }
        }

        @Override // android.view.SurfaceHolder
        @Deprecated
        public void setType(int type) {
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$setKeepScreenOn$0(boolean screenOn) {
            SurfaceView.this.setKeepScreenOn(screenOn);
        }

        @Override // android.view.SurfaceHolder
        public void setKeepScreenOn(final boolean screenOn) {
            SurfaceView.this.runOnUiThread(new Runnable() { // from class: android.view.SurfaceView$1$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    SurfaceView.SurfaceHolderC35271.this.lambda$setKeepScreenOn$0(screenOn);
                }
            });
        }

        @Override // android.view.SurfaceHolder
        public Canvas lockCanvas() {
            return internalLockCanvas(null, false);
        }

        @Override // android.view.SurfaceHolder
        public Canvas lockCanvas(Rect inOutDirty) {
            return internalLockCanvas(inOutDirty, false);
        }

        @Override // android.view.SurfaceHolder
        public Canvas lockHardwareCanvas() {
            return internalLockCanvas(null, true);
        }

        private Canvas internalLockCanvas(Rect dirty, boolean hardware) {
            SurfaceView.this.mSurfaceLock.lock();
            Canvas c = null;
            if (!SurfaceView.this.mDrawingStopped && SurfaceView.this.mSurfaceControl != null) {
                try {
                    if (hardware) {
                        c = SurfaceView.this.mSurface.lockHardwareCanvas();
                    } else {
                        c = SurfaceView.this.mSurface.lockCanvas(dirty);
                    }
                } catch (Exception e) {
                    Log.m109e(LOG_TAG, "Exception locking surface", e);
                }
            }
            if (c != null) {
                SurfaceView.this.mLastLockTime = SystemClock.uptimeMillis();
                return c;
            }
            long now = SystemClock.uptimeMillis();
            long nextTime = SurfaceView.this.mLastLockTime + 100;
            if (nextTime > now) {
                try {
                    Thread.sleep(nextTime - now);
                } catch (InterruptedException e2) {
                }
                now = SystemClock.uptimeMillis();
            }
            SurfaceView.this.mLastLockTime = now;
            SurfaceView.this.mSurfaceLock.unlock();
            return null;
        }

        @Override // android.view.SurfaceHolder
        public void unlockCanvasAndPost(Canvas canvas) {
            try {
                SurfaceView.this.mSurface.unlockCanvasAndPost(canvas);
            } finally {
                SurfaceView.this.mSurfaceLock.unlock();
            }
        }

        @Override // android.view.SurfaceHolder
        public Surface getSurface() {
            return SurfaceView.this.mSurface;
        }

        @Override // android.view.SurfaceHolder
        public Rect getSurfaceFrame() {
            return SurfaceView.this.mSurfaceFrame;
        }
    }

    public SurfaceControl getSurfaceControl() {
        return this.mSurfaceControl;
    }

    public IBinder getHostToken() {
        ViewRootImpl viewRoot = getViewRootImpl();
        if (viewRoot == null) {
            return null;
        }
        return viewRoot.getInputToken();
    }

    @Override // android.view.ViewRootImpl.SurfaceChangedCallback
    public void surfaceCreated(SurfaceControl.Transaction t) {
        setWindowStopped(false);
    }

    @Override // android.view.ViewRootImpl.SurfaceChangedCallback
    public void surfaceDestroyed() {
        setWindowStopped(true);
        this.mRemoteAccessibilityController.disassosciateHierarchy();
    }

    @Override // android.view.ViewRootImpl.SurfaceChangedCallback
    public void surfaceReplaced(SurfaceControl.Transaction t) {
        if (this.mSurfaceControl != null && this.mBackgroundControl != null) {
            updateRelativeZ(t);
        }
    }

    private void updateRelativeZ(SurfaceControl.Transaction t) {
        ViewRootImpl viewRoot = getViewRootImpl();
        if (viewRoot == null) {
            return;
        }
        SurfaceControl viewRootControl = viewRoot.getSurfaceControl();
        t.setRelativeLayer(this.mBackgroundControl, viewRootControl, Integer.MIN_VALUE);
        t.setRelativeLayer(this.mSurfaceControl, viewRootControl, this.mSubLayer);
    }

    public void setChildSurfacePackage(SurfaceControlViewHost.SurfacePackage p) {
        SurfaceControlViewHost.SurfacePackage surfacePackage = this.mSurfacePackage;
        SurfaceControl lastSc = surfacePackage != null ? surfacePackage.getSurfaceControl() : null;
        SurfaceControl.Transaction transaction = new SurfaceControl.Transaction();
        if (this.mSurfaceControl != null) {
            if (lastSc != null) {
                transaction.reparent(lastSc, null);
                this.mSurfacePackage.release();
            }
            reparentSurfacePackage(transaction, p);
            applyTransactionOnVriDraw(transaction);
        }
        this.mSurfacePackage = p;
        invalidate();
    }

    private void reparentSurfacePackage(SurfaceControl.Transaction t, SurfaceControlViewHost.SurfacePackage p) {
        SurfaceControl sc = p.getSurfaceControl();
        if (sc == null || !sc.isValid()) {
            return;
        }
        initEmbeddedHierarchyForAccessibility(p);
        t.reparent(sc, this.mBlastSurfaceControl).show(sc);
    }

    @Override // android.view.View
    public void onInitializeAccessibilityNodeInfoInternal(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfoInternal(info);
        if (!this.mRemoteAccessibilityController.connected()) {
            return;
        }
        info.addChild(this.mRemoteAccessibilityController.getLeashToken());
    }

    @Override // android.view.View
    public int getImportantForAccessibility() {
        int mode = super.getImportantForAccessibility();
        RemoteAccessibilityController remoteAccessibilityController = this.mRemoteAccessibilityController;
        if ((remoteAccessibilityController != null && !remoteAccessibilityController.connected()) || mode != 0) {
            return mode;
        }
        return 1;
    }

    private void initEmbeddedHierarchyForAccessibility(SurfaceControlViewHost.SurfacePackage p) {
        IAccessibilityEmbeddedConnection connection = p.getAccessibilityEmbeddedConnection();
        if (this.mRemoteAccessibilityController.alreadyAssociated(connection)) {
            return;
        }
        this.mRemoteAccessibilityController.assosciateHierarchy(connection, getViewRootImpl().mLeashToken, getAccessibilityViewId());
        updateEmbeddedAccessibilityMatrix(true);
    }

    private void notifySurfaceDestroyed() {
        if (this.mSurface.isValid()) {
            SurfaceHolder.Callback[] callbacks = getSurfaceCallbacks();
            for (SurfaceHolder.Callback c : callbacks) {
                c.surfaceDestroyed(this.mSurfaceHolder);
            }
            if (this.mSurface.isValid()) {
                this.mSurface.forceScopedDisconnect();
            }
        }
    }

    void updateEmbeddedAccessibilityMatrix(boolean force) {
        if (!this.mRemoteAccessibilityController.connected()) {
            return;
        }
        getBoundsOnScreen(this.mTmpRect);
        this.mTmpRect.offset(-this.mAttachInfo.mWindowLeft, -this.mAttachInfo.mWindowTop);
        this.mTmpMatrix.reset();
        this.mTmpMatrix.setTranslate(this.mTmpRect.left, this.mTmpRect.top);
        this.mTmpMatrix.postScale(this.mScreenRect.width() / this.mSurfaceWidth, this.mScreenRect.height() / this.mSurfaceHeight);
        this.mRemoteAccessibilityController.setWindowMatrix(this.mTmpMatrix, force);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        ViewRootImpl viewRoot = getViewRootImpl();
        if (this.mSurfacePackage == null || viewRoot == null) {
            return;
        }
        try {
            viewRoot.mWindowSession.grantEmbeddedWindowFocus(viewRoot.mWindow, this.mSurfacePackage.getInputToken(), gainFocus);
        } catch (Exception e) {
            Log.m109e(TAG, System.identityHashCode(this) + "Exception requesting focus on embedded window", e);
        }
    }

    private void applyTransactionOnVriDraw(SurfaceControl.Transaction t) {
        ViewRootImpl viewRoot = getViewRootImpl();
        if (viewRoot != null) {
            viewRoot.applyTransactionOnDraw(t);
        } else {
            t.apply();
        }
    }

    public void syncNextFrame(Consumer<SurfaceControl.Transaction> t) {
        this.mBlastBufferQueue.syncNextTransaction(t);
    }

    public void applyTransactionToFrame(SurfaceControl.Transaction transaction) {
        synchronized (this.mSurfaceControlLock) {
            BLASTBufferQueue bLASTBufferQueue = this.mBlastBufferQueue;
            if (bLASTBufferQueue == null) {
                throw new IllegalStateException("Surface does not exist!");
            }
            long frameNumber = bLASTBufferQueue.getLastAcquiredFrameNum() + 1;
            this.mBlastBufferQueue.mergeWithNextTransaction(transaction, frameNumber);
        }
    }
}
