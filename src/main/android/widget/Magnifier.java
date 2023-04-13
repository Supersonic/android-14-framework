package android.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.BLASTBufferQueue;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.HardwareRenderer;
import android.graphics.Insets;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RecordingCanvas;
import android.graphics.Rect;
import android.graphics.RenderNode;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.p008os.Handler;
import android.p008os.HandlerThread;
import android.p008os.Message;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.Display;
import android.view.PixelCopy;
import android.view.Surface;
import android.view.SurfaceControl;
import android.view.SurfaceHolder;
import android.view.SurfaceSession;
import android.view.SurfaceView;
import android.view.ThreadedRenderer;
import android.view.View;
import android.view.ViewRootImpl;
import android.widget.Magnifier;
import com.android.internal.C4057R;
import com.android.internal.util.Preconditions;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Objects;
/* loaded from: classes4.dex */
public final class Magnifier {
    private static final float FISHEYE_RAMP_WIDTH = 12.0f;
    private static final int NONEXISTENT_PREVIOUS_CONFIG_VALUE = -1;
    public static final int SOURCE_BOUND_MAX_IN_SURFACE = 0;
    public static final int SOURCE_BOUND_MAX_VISIBLE = 1;
    private static final String TAG = "Magnifier";
    private static final HandlerThread sPixelCopyHandlerThread;
    private int mBottomContentBound;
    private Callback mCallback;
    private final Point mClampedCenterZoomCoords;
    private final boolean mClippingEnabled;
    private SurfaceInfo mContentCopySurface;
    private Drawable mCursorDrawable;
    private final int mDefaultHorizontalSourceToMagnifierOffset;
    private final int mDefaultVerticalSourceToMagnifierOffset;
    private boolean mDirtyState;
    private boolean mDrawCursorEnabled;
    private boolean mIsFishEyeStyle;
    private int mLeftContentBound;
    private int mLeftCutWidth;
    private final Object mLock;
    private final Drawable mOverlay;
    private SurfaceInfo mParentSurface;
    private final Rect mPixelCopyRequestRect;
    private final PointF mPrevShowSourceCoords;
    private final PointF mPrevShowWindowCoords;
    private final Point mPrevStartCoordsInSurface;
    private final int mRamp;
    private int mRightContentBound;
    private int mRightCutWidth;
    private int mSourceHeight;
    private int mSourceWidth;
    private int mTopContentBound;
    private final View mView;
    private final int[] mViewCoordinatesInSurface;
    private InternalPopupWindow mWindow;
    private final Point mWindowCoords;
    private final float mWindowCornerRadius;
    private final float mWindowElevation;
    private int mWindowHeight;
    private final int mWindowWidth;
    private float mZoom;

    /* loaded from: classes4.dex */
    public interface Callback {
        void onOperationComplete();
    }

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes4.dex */
    public @interface SourceBound {
    }

    static {
        HandlerThread handlerThread = new HandlerThread("magnifier pixel copy result handler");
        sPixelCopyHandlerThread = handlerThread;
        handlerThread.start();
    }

    @Deprecated
    public Magnifier(View view) {
        this(createBuilderWithOldMagnifierDefaults(view));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Builder createBuilderWithOldMagnifierDefaults(View view) {
        Builder params = new Builder(view);
        Context context = view.getContext();
        TypedArray a = context.obtainStyledAttributes(null, C4057R.styleable.Magnifier, C4057R.attr.magnifierStyle, 0);
        params.mWidth = a.getDimensionPixelSize(5, 0);
        params.mHeight = a.getDimensionPixelSize(2, 0);
        params.mElevation = a.getDimension(1, 0.0f);
        params.mCornerRadius = getDeviceDefaultDialogCornerRadius(context);
        params.mZoom = a.getFloat(6, 0.0f);
        params.mHorizontalDefaultSourceToMagnifierOffset = a.getDimensionPixelSize(3, 0);
        params.mVerticalDefaultSourceToMagnifierOffset = a.getDimensionPixelSize(4, 0);
        params.mOverlay = new ColorDrawable(a.getColor(0, 0));
        a.recycle();
        params.mClippingEnabled = true;
        params.mLeftContentBound = 1;
        params.mTopContentBound = 0;
        params.mRightContentBound = 1;
        params.mBottomContentBound = 0;
        return params;
    }

    private static float getDeviceDefaultDialogCornerRadius(Context context) {
        Context deviceDefaultContext = new ContextThemeWrapper(context, 16974120);
        TypedArray ta = deviceDefaultContext.obtainStyledAttributes(new int[]{16844145});
        float dialogCornerRadius = ta.getDimension(0, 0.0f);
        ta.recycle();
        return dialogCornerRadius;
    }

    private Magnifier(Builder params) {
        this.mWindowCoords = new Point();
        this.mClampedCenterZoomCoords = new Point();
        this.mPrevStartCoordsInSurface = new Point(-1, -1);
        this.mPrevShowSourceCoords = new PointF(-1.0f, -1.0f);
        this.mPrevShowWindowCoords = new PointF(-1.0f, -1.0f);
        this.mPixelCopyRequestRect = new Rect();
        this.mLock = new Object();
        this.mLeftCutWidth = 0;
        this.mRightCutWidth = 0;
        View view = params.mView;
        this.mView = view;
        int i = params.mWidth;
        this.mWindowWidth = i;
        this.mWindowHeight = params.mHeight;
        this.mZoom = params.mZoom;
        this.mIsFishEyeStyle = params.mIsFishEyeStyle;
        if (params.mSourceWidth > 0 && params.mSourceHeight > 0) {
            this.mSourceWidth = params.mSourceWidth;
            this.mSourceHeight = params.mSourceHeight;
        } else {
            this.mSourceWidth = Math.round(i / this.mZoom);
            this.mSourceHeight = Math.round(this.mWindowHeight / this.mZoom);
        }
        this.mWindowElevation = params.mElevation;
        this.mWindowCornerRadius = params.mCornerRadius;
        this.mOverlay = params.mOverlay;
        this.mDefaultHorizontalSourceToMagnifierOffset = params.mHorizontalDefaultSourceToMagnifierOffset;
        this.mDefaultVerticalSourceToMagnifierOffset = params.mVerticalDefaultSourceToMagnifierOffset;
        this.mClippingEnabled = params.mClippingEnabled;
        this.mLeftContentBound = params.mLeftContentBound;
        this.mTopContentBound = params.mTopContentBound;
        this.mRightContentBound = params.mRightContentBound;
        this.mBottomContentBound = params.mBottomContentBound;
        this.mViewCoordinatesInSurface = new int[2];
        this.mRamp = (int) TypedValue.applyDimension(1, FISHEYE_RAMP_WIDTH, view.getContext().getResources().getDisplayMetrics());
    }

    public void show(float sourceCenterX, float sourceCenterY) {
        show(sourceCenterX, sourceCenterY, this.mDefaultHorizontalSourceToMagnifierOffset + sourceCenterX, this.mDefaultVerticalSourceToMagnifierOffset + sourceCenterY);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setDrawCursor(boolean enabled, Drawable cursorDrawable) {
        this.mDrawCursorEnabled = enabled;
        this.mCursorDrawable = cursorDrawable;
    }

    public void show(float sourceCenterX, float sourceCenterY, float magnifierCenterX, float magnifierCenterY) {
        float magnifierCenterX2;
        float magnifierCenterY2;
        int startX;
        float magnifierCenterX3;
        float magnifierCenterY3;
        ColorDrawable colorDrawable;
        float magnifierCenterX4;
        obtainSurfaces();
        obtainContentCoordinates(sourceCenterX, sourceCenterY);
        int startX2 = this.mClampedCenterZoomCoords.f76x - (this.mSourceWidth / 2);
        int startY = this.mClampedCenterZoomCoords.f77y - (this.mSourceHeight / 2);
        if (this.mIsFishEyeStyle) {
            float magnifierCenterX5 = this.mClampedCenterZoomCoords.f76x - this.mViewCoordinatesInSurface[0];
            magnifierCenterY2 = this.mClampedCenterZoomCoords.f77y - this.mViewCoordinatesInSurface[1];
            int i = this.mSourceWidth;
            int i2 = this.mRamp;
            float f = this.mZoom;
            float rampPre = (i - ((i - (i2 * 2)) / f)) / 2.0f;
            float x0 = sourceCenterX - (i / 2.0f);
            float rampX0 = i2 + x0;
            float leftEdge = 0.0f;
            if (0.0f > rampX0) {
                leftEdge = sourceCenterX - ((sourceCenterX - 0.0f) / f);
            } else if (0.0f > x0) {
                leftEdge = (x0 + rampPre) - (((rampX0 - 0.0f) * rampPre) / i2);
            }
            int leftBound = Math.min((int) leftEdge, this.mView.getWidth());
            float x1 = sourceCenterX + (this.mSourceWidth / 2.0f);
            float rampX1 = x1 - this.mRamp;
            float rightEdge = this.mView.getWidth();
            if (rightEdge < rampX1) {
                rightEdge = sourceCenterX + ((rightEdge - sourceCenterX) / this.mZoom);
                magnifierCenterX4 = magnifierCenterX5;
            } else if (rightEdge < x1) {
                magnifierCenterX4 = magnifierCenterX5;
                rightEdge = (x1 - rampPre) + (((rightEdge - rampX1) * rampPre) / this.mRamp);
            } else {
                magnifierCenterX4 = magnifierCenterX5;
            }
            int rightBound = Math.max(leftBound, (int) rightEdge);
            int leftBound2 = Math.max(this.mViewCoordinatesInSurface[0] + leftBound, 0);
            int rightBound2 = Math.min(this.mViewCoordinatesInSurface[0] + rightBound, this.mContentCopySurface.mWidth);
            this.mLeftCutWidth = Math.max(0, leftBound2 - startX2);
            this.mRightCutWidth = Math.max(0, (this.mSourceWidth + startX2) - rightBound2);
            magnifierCenterX2 = magnifierCenterX4;
            startX = Math.max(startX2, leftBound2);
        } else {
            magnifierCenterX2 = magnifierCenterX;
            magnifierCenterY2 = magnifierCenterY;
            startX = startX2;
        }
        obtainWindowCoordinates(magnifierCenterX2, magnifierCenterY2);
        if (sourceCenterX != this.mPrevShowSourceCoords.f78x || sourceCenterY != this.mPrevShowSourceCoords.f79y || this.mDirtyState) {
            if (this.mWindow != null) {
                magnifierCenterX3 = magnifierCenterX2;
                magnifierCenterY3 = magnifierCenterY2;
            } else {
                synchronized (this.mLock) {
                    try {
                        try {
                            Context context = this.mView.getContext();
                            Display display = this.mView.getDisplay();
                            SurfaceControl surfaceControl = this.mParentSurface.mSurfaceControl;
                            int i3 = this.mWindowWidth;
                            int i4 = this.mWindowHeight;
                            float f2 = this.mZoom;
                            int i5 = this.mRamp;
                            float f3 = this.mWindowElevation;
                            float f4 = this.mWindowCornerRadius;
                            Drawable drawable = this.mOverlay;
                            if (drawable != null) {
                                magnifierCenterY3 = magnifierCenterY2;
                                colorDrawable = drawable;
                            } else {
                                magnifierCenterY3 = magnifierCenterY2;
                                try {
                                    colorDrawable = new ColorDrawable(0);
                                } catch (Throwable th) {
                                    th = th;
                                    throw th;
                                }
                            }
                            magnifierCenterX3 = magnifierCenterX2;
                            this.mWindow = new InternalPopupWindow(context, display, surfaceControl, i3, i4, f2, i5, f3, f4, colorDrawable, Handler.getMain(), this.mLock, this.mCallback, this.mIsFishEyeStyle);
                        } catch (Throwable th2) {
                            th = th2;
                        }
                    } catch (Throwable th3) {
                        th = th3;
                    }
                }
            }
            performPixelCopy(startX, startY, true);
        } else if (magnifierCenterX2 == this.mPrevShowWindowCoords.f78x && magnifierCenterY2 == this.mPrevShowWindowCoords.f79y) {
            magnifierCenterX3 = magnifierCenterX2;
            magnifierCenterY3 = magnifierCenterY2;
        } else {
            final Point windowCoords = getCurrentClampedWindowCoordinates();
            final InternalPopupWindow currentWindowInstance = this.mWindow;
            sPixelCopyHandlerThread.getThreadHandler().post(new Runnable() { // from class: android.widget.Magnifier$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    Magnifier.this.lambda$show$0(currentWindowInstance, windowCoords);
                }
            });
            magnifierCenterX3 = magnifierCenterX2;
            magnifierCenterY3 = magnifierCenterY2;
        }
        this.mPrevShowSourceCoords.f78x = sourceCenterX;
        this.mPrevShowSourceCoords.f79y = sourceCenterY;
        this.mPrevShowWindowCoords.f78x = magnifierCenterX3;
        this.mPrevShowWindowCoords.f79y = magnifierCenterY3;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$show$0(InternalPopupWindow currentWindowInstance, Point windowCoords) {
        synchronized (this.mLock) {
            InternalPopupWindow internalPopupWindow = this.mWindow;
            if (internalPopupWindow != currentWindowInstance) {
                return;
            }
            internalPopupWindow.setContentPositionForNextDraw(windowCoords.f76x, windowCoords.f77y);
        }
    }

    public void dismiss() {
        if (this.mWindow != null) {
            synchronized (this.mLock) {
                this.mWindow.destroy();
                this.mWindow = null;
            }
            this.mPrevShowSourceCoords.f78x = -1.0f;
            this.mPrevShowSourceCoords.f79y = -1.0f;
            this.mPrevShowWindowCoords.f78x = -1.0f;
            this.mPrevShowWindowCoords.f79y = -1.0f;
            this.mPrevStartCoordsInSurface.f76x = -1;
            this.mPrevStartCoordsInSurface.f77y = -1;
        }
    }

    public void update() {
        if (this.mWindow != null) {
            obtainSurfaces();
            if (!this.mDirtyState) {
                performPixelCopy(this.mPrevStartCoordsInSurface.f76x, this.mPrevStartCoordsInSurface.f77y, false);
            } else {
                show(this.mPrevShowSourceCoords.f78x, this.mPrevShowSourceCoords.f79y, this.mPrevShowWindowCoords.f78x, this.mPrevShowWindowCoords.f79y);
            }
        }
    }

    public int getWidth() {
        return this.mWindowWidth;
    }

    public int getHeight() {
        return this.mWindowHeight;
    }

    public int getSourceWidth() {
        return this.mSourceWidth;
    }

    public int getSourceHeight() {
        return this.mSourceHeight;
    }

    public void setZoom(float zoom) {
        Preconditions.checkArgumentPositive(zoom, "Zoom should be positive");
        this.mZoom = zoom;
        this.mSourceWidth = this.mIsFishEyeStyle ? this.mWindowWidth : Math.round(this.mWindowWidth / zoom);
        this.mSourceHeight = Math.round(this.mWindowHeight / this.mZoom);
        this.mDirtyState = true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void updateSourceFactors(int sourceHeight, float zoom) {
        this.mZoom = zoom;
        this.mSourceHeight = sourceHeight;
        int i = (int) (sourceHeight * zoom);
        this.mWindowHeight = i;
        InternalPopupWindow internalPopupWindow = this.mWindow;
        if (internalPopupWindow != null) {
            internalPopupWindow.updateContentFactors(i, zoom);
        }
    }

    public float getZoom() {
        return this.mZoom;
    }

    public float getElevation() {
        return this.mWindowElevation;
    }

    public float getCornerRadius() {
        return this.mWindowCornerRadius;
    }

    public int getDefaultHorizontalSourceToMagnifierOffset() {
        return this.mDefaultHorizontalSourceToMagnifierOffset;
    }

    public int getDefaultVerticalSourceToMagnifierOffset() {
        return this.mDefaultVerticalSourceToMagnifierOffset;
    }

    public Drawable getOverlay() {
        return this.mOverlay;
    }

    public boolean isClippingEnabled() {
        return this.mClippingEnabled;
    }

    public Point getPosition() {
        if (this.mWindow == null) {
            return null;
        }
        Point position = getCurrentClampedWindowCoordinates();
        position.offset(-this.mParentSurface.mInsets.left, -this.mParentSurface.mInsets.top);
        return new Point(position);
    }

    public Point getSourcePosition() {
        if (this.mWindow == null) {
            return null;
        }
        Point position = new Point(this.mPixelCopyRequestRect.left, this.mPixelCopyRequestRect.top);
        position.offset(-this.mContentCopySurface.mInsets.left, -this.mContentCopySurface.mInsets.top);
        return new Point(position);
    }

    private void obtainSurfaces() {
        ViewRootImpl viewRootImpl;
        Surface mainWindowSurface;
        SurfaceInfo validMainWindowSurface = SurfaceInfo.NULL;
        if (this.mView.getViewRootImpl() != null && (mainWindowSurface = (viewRootImpl = this.mView.getViewRootImpl()).mSurface) != null && mainWindowSurface.isValid()) {
            Rect surfaceInsets = viewRootImpl.mWindowAttributes.surfaceInsets;
            int surfaceWidth = viewRootImpl.getWidth() + surfaceInsets.left + surfaceInsets.right;
            int surfaceHeight = viewRootImpl.getHeight() + surfaceInsets.top + surfaceInsets.bottom;
            validMainWindowSurface = new SurfaceInfo(viewRootImpl.getSurfaceControl(), mainWindowSurface, surfaceWidth, surfaceHeight, surfaceInsets, true);
        }
        SurfaceInfo validSurfaceViewSurface = SurfaceInfo.NULL;
        View view = this.mView;
        if (view instanceof SurfaceView) {
            SurfaceControl sc = ((SurfaceView) view).getSurfaceControl();
            SurfaceHolder surfaceHolder = ((SurfaceView) this.mView).getHolder();
            Surface surfaceViewSurface = surfaceHolder.getSurface();
            if (sc != null && sc.isValid()) {
                Rect surfaceFrame = surfaceHolder.getSurfaceFrame();
                validSurfaceViewSurface = new SurfaceInfo(sc, surfaceViewSurface, surfaceFrame.right, surfaceFrame.bottom, new Rect(), false);
            }
        }
        this.mParentSurface = validMainWindowSurface != SurfaceInfo.NULL ? validMainWindowSurface : validSurfaceViewSurface;
        this.mContentCopySurface = this.mView instanceof SurfaceView ? validSurfaceViewSurface : validMainWindowSurface;
    }

    private void obtainContentCoordinates(float xPosInView, float yPosInView) {
        int zoomCenterX;
        int zoomCenterY;
        int max;
        int[] iArr = this.mViewCoordinatesInSurface;
        int prevViewXInSurface = iArr[0];
        int prevViewYInSurface = iArr[1];
        this.mView.getLocationInSurface(iArr);
        int[] iArr2 = this.mViewCoordinatesInSurface;
        int zoomCenterY2 = iArr2[0];
        if (zoomCenterY2 != prevViewXInSurface || iArr2[1] != prevViewYInSurface) {
            this.mDirtyState = true;
        }
        if (this.mView instanceof SurfaceView) {
            zoomCenterX = Math.round(xPosInView);
            zoomCenterY = Math.round(yPosInView);
        } else {
            zoomCenterX = Math.round(xPosInView + zoomCenterY2);
            zoomCenterY = Math.round(yPosInView + this.mViewCoordinatesInSurface[1]);
        }
        Rect[] bounds = new Rect[2];
        Rect surfaceBounds = new Rect(0, 0, this.mContentCopySurface.mWidth, this.mContentCopySurface.mHeight);
        bounds[0] = surfaceBounds;
        Rect viewVisibleRegion = new Rect();
        this.mView.getGlobalVisibleRect(viewVisibleRegion);
        if (this.mView.getViewRootImpl() != null) {
            Rect surfaceInsets = this.mView.getViewRootImpl().mWindowAttributes.surfaceInsets;
            viewVisibleRegion.offset(surfaceInsets.left, surfaceInsets.top);
        }
        if (this.mView instanceof SurfaceView) {
            int[] iArr3 = this.mViewCoordinatesInSurface;
            viewVisibleRegion.offset(-iArr3[0], -iArr3[1]);
        }
        bounds[1] = viewVisibleRegion;
        int resolvedLeft = Integer.MIN_VALUE;
        for (int i = this.mLeftContentBound; i >= 0; i--) {
            resolvedLeft = Math.max(resolvedLeft, bounds[i].left);
        }
        int resolvedTop = Integer.MIN_VALUE;
        for (int i2 = this.mTopContentBound; i2 >= 0; i2--) {
            resolvedTop = Math.max(resolvedTop, bounds[i2].top);
        }
        int resolvedRight = Integer.MAX_VALUE;
        for (int i3 = this.mRightContentBound; i3 >= 0; i3--) {
            resolvedRight = Math.min(resolvedRight, bounds[i3].right);
        }
        int resolvedBottom = Integer.MAX_VALUE;
        for (int i4 = this.mBottomContentBound; i4 >= 0; i4--) {
            resolvedBottom = Math.min(resolvedBottom, bounds[i4].bottom);
        }
        int resolvedLeft2 = Math.min(resolvedLeft, this.mContentCopySurface.mWidth - this.mSourceWidth);
        int resolvedTop2 = Math.min(resolvedTop, this.mContentCopySurface.mHeight - this.mSourceHeight);
        if (resolvedLeft2 < 0 || resolvedTop2 < 0) {
            Log.m110e(TAG, "Magnifier's content is copied from a surface smaller thanthe content requested size. The magnifier will be dismissed.");
        }
        int resolvedRight2 = Math.max(resolvedRight, this.mSourceWidth + resolvedLeft2);
        int resolvedBottom2 = Math.max(resolvedBottom, this.mSourceHeight + resolvedTop2);
        Point point = this.mClampedCenterZoomCoords;
        if (this.mIsFishEyeStyle) {
            max = Math.max(resolvedLeft2, Math.min(zoomCenterX, resolvedRight2));
        } else {
            int i5 = this.mSourceWidth;
            max = Math.max((i5 / 2) + resolvedLeft2, Math.min(zoomCenterX, resolvedRight2 - (i5 / 2)));
        }
        point.f76x = max;
        Point point2 = this.mClampedCenterZoomCoords;
        int i6 = this.mSourceHeight;
        point2.f77y = Math.max((i6 / 2) + resolvedTop2, Math.min(zoomCenterY, resolvedBottom2 - (i6 / 2)));
    }

    private void obtainWindowCoordinates(float xWindowPos, float yWindowPos) {
        int windowCenterX;
        int windowCenterY;
        if (this.mView instanceof SurfaceView) {
            windowCenterX = Math.round(xWindowPos);
            windowCenterY = Math.round(yWindowPos);
        } else {
            windowCenterX = Math.round(this.mViewCoordinatesInSurface[0] + xWindowPos);
            windowCenterY = Math.round(this.mViewCoordinatesInSurface[1] + yWindowPos);
        }
        this.mWindowCoords.f76x = windowCenterX - (this.mWindowWidth / 2);
        this.mWindowCoords.f77y = windowCenterY - (this.mWindowHeight / 2);
        if (this.mParentSurface != this.mContentCopySurface) {
            this.mWindowCoords.f76x += this.mViewCoordinatesInSurface[0];
            this.mWindowCoords.f77y += this.mViewCoordinatesInSurface[1];
        }
    }

    private void maybeDrawCursor(Canvas canvas) {
        if (this.mDrawCursorEnabled) {
            Drawable drawable = this.mCursorDrawable;
            if (drawable != null) {
                int i = this.mSourceWidth;
                drawable.setBounds(i / 2, 0, (i / 2) + drawable.getIntrinsicWidth(), this.mSourceHeight);
                this.mCursorDrawable.draw(canvas);
                return;
            }
            Paint paint = new Paint();
            paint.setColor(-16777216);
            int i2 = this.mSourceWidth;
            canvas.drawRect(new Rect((i2 / 2) - 1, 0, (i2 / 2) + 1, this.mSourceHeight), paint);
        }
    }

    private void performPixelCopy(int startXInSurface, int startYInSurface, final boolean updateWindowPosition) {
        if (this.mContentCopySurface.mSurface == null || !this.mContentCopySurface.mSurface.isValid()) {
            onPixelCopyFailed();
            return;
        }
        final Point windowCoords = getCurrentClampedWindowCoordinates();
        this.mPixelCopyRequestRect.set(startXInSurface, startYInSurface, ((this.mSourceWidth + startXInSurface) - this.mLeftCutWidth) - this.mRightCutWidth, this.mSourceHeight + startYInSurface);
        this.mPrevStartCoordsInSurface.f76x = startXInSurface;
        this.mPrevStartCoordsInSurface.f77y = startYInSurface;
        this.mDirtyState = false;
        final InternalPopupWindow currentWindowInstance = this.mWindow;
        if (this.mPixelCopyRequestRect.width() == 0) {
            this.mWindow.updateContent(Bitmap.createBitmap(this.mSourceWidth, this.mSourceHeight, Bitmap.Config.ALPHA_8));
            return;
        }
        final Bitmap bitmap = Bitmap.createBitmap((this.mSourceWidth - this.mLeftCutWidth) - this.mRightCutWidth, this.mSourceHeight, Bitmap.Config.ARGB_8888);
        PixelCopy.request(this.mContentCopySurface.mSurface, this.mPixelCopyRequestRect, bitmap, new PixelCopy.OnPixelCopyFinishedListener() { // from class: android.widget.Magnifier$$ExternalSyntheticLambda1
            @Override // android.view.PixelCopy.OnPixelCopyFinishedListener
            public final void onPixelCopyFinished(int i) {
                Magnifier.this.lambda$performPixelCopy$1(currentWindowInstance, updateWindowPosition, windowCoords, bitmap, i);
            }
        }, sPixelCopyHandlerThread.getThreadHandler());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$performPixelCopy$1(InternalPopupWindow currentWindowInstance, boolean updateWindowPosition, Point windowCoords, Bitmap bitmap, int result) {
        if (result != 0) {
            onPixelCopyFailed();
            return;
        }
        synchronized (this.mLock) {
            InternalPopupWindow internalPopupWindow = this.mWindow;
            if (internalPopupWindow != currentWindowInstance) {
                return;
            }
            if (updateWindowPosition) {
                internalPopupWindow.setContentPositionForNextDraw(windowCoords.f76x, windowCoords.f77y);
            }
            int width = bitmap.getWidth();
            int i = this.mSourceWidth;
            if (width < i) {
                Bitmap newBitmap = Bitmap.createBitmap(i, bitmap.getHeight(), bitmap.getConfig());
                Canvas can = new Canvas(newBitmap);
                Rect dstRect = new Rect(this.mLeftCutWidth, 0, this.mSourceWidth - this.mRightCutWidth, bitmap.getHeight());
                can.drawBitmap(bitmap, (Rect) null, dstRect, (Paint) null);
                maybeDrawCursor(can);
                this.mWindow.updateContent(newBitmap);
            } else {
                maybeDrawCursor(new Canvas(bitmap));
                this.mWindow.updateContent(bitmap);
            }
        }
    }

    private void onPixelCopyFailed() {
        Log.m110e(TAG, "Magnifier failed to copy content from the view Surface. It will be dismissed.");
        Handler.getMain().postAtFrontOfQueue(new Runnable() { // from class: android.widget.Magnifier$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                Magnifier.this.lambda$onPixelCopyFailed$2();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onPixelCopyFailed$2() {
        dismiss();
        Callback callback = this.mCallback;
        if (callback != null) {
            callback.onOperationComplete();
        }
    }

    private Point getCurrentClampedWindowCoordinates() {
        Rect windowBounds;
        if (!this.mClippingEnabled) {
            return new Point(this.mWindowCoords);
        }
        if (this.mParentSurface.mIsMainWindowSurface) {
            Insets systemInsets = this.mView.getRootWindowInsets().getSystemWindowInsets();
            windowBounds = new Rect(systemInsets.left + this.mParentSurface.mInsets.left, systemInsets.top + this.mParentSurface.mInsets.top, (this.mParentSurface.mWidth - systemInsets.right) - this.mParentSurface.mInsets.right, (this.mParentSurface.mHeight - systemInsets.bottom) - this.mParentSurface.mInsets.bottom);
        } else {
            windowBounds = new Rect(0, 0, this.mParentSurface.mWidth, this.mParentSurface.mHeight);
        }
        int windowCoordsX = Math.max(windowBounds.left, Math.min(windowBounds.right - this.mWindowWidth, this.mWindowCoords.f76x));
        int windowCoordsY = Math.max(windowBounds.top, Math.min(windowBounds.bottom - this.mWindowHeight, this.mWindowCoords.f77y));
        return new Point(windowCoordsX, windowCoordsY);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public static class SurfaceInfo {
        public static final SurfaceInfo NULL = new SurfaceInfo(null, null, 0, 0, null, false);
        private int mHeight;
        private Rect mInsets;
        private boolean mIsMainWindowSurface;
        private Surface mSurface;
        private SurfaceControl mSurfaceControl;
        private int mWidth;

        SurfaceInfo(SurfaceControl surfaceControl, Surface surface, int width, int height, Rect insets, boolean isMainWindowSurface) {
            this.mSurfaceControl = surfaceControl;
            this.mSurface = surface;
            this.mWidth = width;
            this.mHeight = height;
            this.mInsets = insets;
            this.mIsMainWindowSurface = isMainWindowSurface;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public static class InternalPopupWindow {
        private static final int SURFACE_Z = 5;
        private final BLASTBufferQueue mBBQ;
        private final SurfaceControl mBbqSurfaceControl;
        private Bitmap mBitmap;
        private final RenderNode mBitmapRenderNode;
        private Callback mCallback;
        private int mContentHeight;
        private final int mContentWidth;
        private Bitmap mCurrentContent;
        private final Display mDisplay;
        private boolean mFrameDrawScheduled;
        private final Handler mHandler;
        private boolean mIsFishEyeStyle;
        private final Object mLock;
        private final Runnable mMagnifierUpdater;
        private int mMeshHeight;
        private float[] mMeshLeft;
        private float[] mMeshRight;
        private int mMeshWidth;
        private final int mOffsetX;
        private final int mOffsetY;
        private final Drawable mOverlay;
        private final RenderNode mOverlayRenderNode;
        private boolean mPendingWindowPositionUpdate;
        private final int mRamp;
        private final ThreadedRenderer.SimpleRenderer mRenderer;
        private final Surface mSurface;
        private final SurfaceControl mSurfaceControl;
        private final SurfaceSession mSurfaceSession;
        private int mWindowPositionX;
        private int mWindowPositionY;
        private float mZoom;
        private final SurfaceControl.Transaction mTransaction = new SurfaceControl.Transaction();
        private boolean mFirstDraw = true;

        InternalPopupWindow(Context context, Display display, SurfaceControl parentSurfaceControl, int width, int height, float zoom, int ramp, float elevation, float cornerRadius, Drawable overlay, Handler handler, Object lock, Callback callback, boolean isFishEyeStyle) {
            this.mDisplay = display;
            this.mOverlay = overlay;
            this.mLock = lock;
            this.mCallback = callback;
            this.mContentWidth = width;
            this.mContentHeight = height;
            this.mZoom = zoom;
            this.mRamp = ramp;
            int i = (int) (elevation * 1.05f);
            this.mOffsetX = i;
            int i2 = (int) (1.05f * elevation);
            this.mOffsetY = i2;
            int surfaceWidth = (i * 2) + width;
            int surfaceHeight = height + (i2 * 2);
            SurfaceSession surfaceSession = new SurfaceSession();
            this.mSurfaceSession = surfaceSession;
            SurfaceControl build = new SurfaceControl.Builder(surfaceSession).setName("magnifier surface").setFlags(4).setContainerLayer().setParent(parentSurfaceControl).setCallsite("InternalPopupWindow").build();
            this.mSurfaceControl = build;
            SurfaceControl build2 = new SurfaceControl.Builder(surfaceSession).setName("magnifier surface bbq wrapper").setHidden(false).setBLASTLayer().setParent(build).setCallsite("InternalPopupWindow").build();
            this.mBbqSurfaceControl = build2;
            BLASTBufferQueue bLASTBufferQueue = new BLASTBufferQueue("magnifier surface", build2, surfaceWidth, surfaceHeight, -3);
            this.mBBQ = bLASTBufferQueue;
            Surface createSurface = bLASTBufferQueue.createSurface();
            this.mSurface = createSurface;
            ThreadedRenderer.SimpleRenderer simpleRenderer = new ThreadedRenderer.SimpleRenderer(context, "magnifier renderer", createSurface);
            this.mRenderer = simpleRenderer;
            RenderNode createRenderNodeForBitmap = createRenderNodeForBitmap("magnifier content", elevation, cornerRadius);
            this.mBitmapRenderNode = createRenderNodeForBitmap;
            RenderNode createRenderNodeForOverlay = createRenderNodeForOverlay("magnifier overlay", cornerRadius);
            this.mOverlayRenderNode = createRenderNodeForOverlay;
            setupOverlay();
            RecordingCanvas canvas = simpleRenderer.getRootNode().beginRecording(width, height);
            try {
                canvas.enableZ();
                canvas.drawRenderNode(createRenderNodeForBitmap);
                canvas.disableZ();
                canvas.drawRenderNode(createRenderNodeForOverlay);
                canvas.disableZ();
                simpleRenderer.getRootNode().endRecording();
                if (this.mCallback != null) {
                    this.mCurrentContent = Bitmap.createBitmap(width, this.mContentHeight, Bitmap.Config.ARGB_8888);
                    updateCurrentContentForTesting();
                }
                this.mHandler = handler;
                this.mMagnifierUpdater = new Runnable() { // from class: android.widget.Magnifier$InternalPopupWindow$$ExternalSyntheticLambda1
                    @Override // java.lang.Runnable
                    public final void run() {
                        Magnifier.InternalPopupWindow.this.doDraw();
                    }
                };
                this.mFrameDrawScheduled = false;
                this.mIsFishEyeStyle = isFishEyeStyle;
                if (isFishEyeStyle) {
                    createMeshMatrixForFishEyeEffect();
                }
            } catch (Throwable th) {
                this.mRenderer.getRootNode().endRecording();
                throw th;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void updateContentFactors(int contentHeight, float zoom) {
            int i = this.mContentHeight;
            if (i == contentHeight && this.mZoom == zoom) {
                return;
            }
            if (i < contentHeight) {
                this.mBBQ.update(this.mBbqSurfaceControl, this.mContentWidth, contentHeight, -3);
                this.mRenderer.setSurface(this.mSurface);
                Outline outline = new Outline();
                outline.setRoundRect(0, 0, this.mContentWidth, contentHeight, 0.0f);
                outline.setAlpha(1.0f);
                RenderNode renderNode = this.mBitmapRenderNode;
                int i2 = this.mOffsetX;
                int i3 = this.mOffsetY;
                renderNode.setLeftTopRightBottom(i2, i3, this.mContentWidth + i2, i3 + contentHeight);
                this.mBitmapRenderNode.setOutline(outline);
                RenderNode renderNode2 = this.mOverlayRenderNode;
                int i4 = this.mOffsetX;
                int i5 = this.mOffsetY;
                renderNode2.setLeftTopRightBottom(i4, i5, this.mContentWidth + i4, i5 + contentHeight);
                this.mOverlayRenderNode.setOutline(outline);
                RecordingCanvas canvas = this.mRenderer.getRootNode().beginRecording(this.mContentWidth, contentHeight);
                try {
                    canvas.enableZ();
                    canvas.drawRenderNode(this.mBitmapRenderNode);
                    canvas.disableZ();
                    canvas.drawRenderNode(this.mOverlayRenderNode);
                    canvas.disableZ();
                } finally {
                    this.mRenderer.getRootNode().endRecording();
                }
            }
            this.mContentHeight = contentHeight;
            this.mZoom = zoom;
            fillMeshMatrix();
        }

        private void createMeshMatrixForFishEyeEffect() {
            this.mMeshWidth = 1;
            this.mMeshHeight = 6;
            this.mMeshLeft = new float[(1 + 1) * 2 * (6 + 1)];
            this.mMeshRight = new float[(1 + 1) * 2 * (6 + 1)];
            fillMeshMatrix();
        }

        private void fillMeshMatrix() {
            InternalPopupWindow internalPopupWindow = this;
            internalPopupWindow.mMeshWidth = 1;
            internalPopupWindow.mMeshHeight = 6;
            float w = internalPopupWindow.mContentWidth;
            float h = internalPopupWindow.mContentHeight;
            float h0 = h / internalPopupWindow.mZoom;
            float dh = h - h0;
            int i = 0;
            while (true) {
                int i2 = internalPopupWindow.mMeshWidth;
                int i3 = internalPopupWindow.mMeshHeight;
                if (i < (i2 + 1) * 2 * (i3 + 1)) {
                    int colIndex = (i % ((i2 + 1) * 2)) / 2;
                    float[] fArr = internalPopupWindow.mMeshLeft;
                    int i4 = internalPopupWindow.mRamp;
                    fArr[i] = (colIndex * i4) / i2;
                    float[] fArr2 = internalPopupWindow.mMeshRight;
                    fArr2[i] = (w - i4) + ((i4 * colIndex) / i2);
                    int rowIndex = (i / 2) / (i2 + 1);
                    float hl = ((colIndex * dh) / i2) + h0;
                    float yl = (h - hl) / 2.0f;
                    fArr[i + 1] = ((rowIndex * hl) / i3) + yl;
                    float hr = h - ((colIndex * dh) / i2);
                    float yr = (h - hr) / 2.0f;
                    fArr2[i + 1] = ((rowIndex * hr) / i3) + yr;
                    i += 2;
                    internalPopupWindow = this;
                } else {
                    return;
                }
            }
        }

        private RenderNode createRenderNodeForBitmap(String name, float elevation, float cornerRadius) {
            RenderNode bitmapRenderNode = RenderNode.create(name, null);
            int i = this.mOffsetX;
            int i2 = this.mOffsetY;
            bitmapRenderNode.setLeftTopRightBottom(i, i2, this.mContentWidth + i, this.mContentHeight + i2);
            bitmapRenderNode.setElevation(elevation);
            Outline outline = new Outline();
            outline.setRoundRect(0, 0, this.mContentWidth, this.mContentHeight, cornerRadius);
            outline.setAlpha(1.0f);
            bitmapRenderNode.setOutline(outline);
            bitmapRenderNode.setClipToOutline(true);
            RecordingCanvas canvas = bitmapRenderNode.beginRecording(this.mContentWidth, this.mContentHeight);
            try {
                canvas.drawColor(Color.GREEN);
                return bitmapRenderNode;
            } finally {
                bitmapRenderNode.endRecording();
            }
        }

        private RenderNode createRenderNodeForOverlay(String name, float cornerRadius) {
            RenderNode overlayRenderNode = RenderNode.create(name, null);
            int i = this.mOffsetX;
            int i2 = this.mOffsetY;
            overlayRenderNode.setLeftTopRightBottom(i, i2, this.mContentWidth + i, this.mContentHeight + i2);
            Outline outline = new Outline();
            outline.setRoundRect(0, 0, this.mContentWidth, this.mContentHeight, cornerRadius);
            outline.setAlpha(1.0f);
            overlayRenderNode.setOutline(outline);
            overlayRenderNode.setClipToOutline(true);
            return overlayRenderNode;
        }

        private void setupOverlay() {
            drawOverlay();
            this.mOverlay.setCallback(new Drawable.Callback() { // from class: android.widget.Magnifier.InternalPopupWindow.1
                @Override // android.graphics.drawable.Drawable.Callback
                public void invalidateDrawable(Drawable who) {
                    InternalPopupWindow.this.drawOverlay();
                    if (InternalPopupWindow.this.mCallback != null) {
                        InternalPopupWindow.this.updateCurrentContentForTesting();
                    }
                }

                @Override // android.graphics.drawable.Drawable.Callback
                public void scheduleDrawable(Drawable who, Runnable what, long when) {
                    Handler.getMain().postAtTime(what, who, when);
                }

                @Override // android.graphics.drawable.Drawable.Callback
                public void unscheduleDrawable(Drawable who, Runnable what) {
                    Handler.getMain().removeCallbacks(what, who);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void drawOverlay() {
            RecordingCanvas canvas = this.mOverlayRenderNode.beginRecording(this.mContentWidth, this.mContentHeight);
            try {
                this.mOverlay.setBounds(0, 0, this.mContentWidth, this.mContentHeight);
                this.mOverlay.draw(canvas);
            } finally {
                this.mOverlayRenderNode.endRecording();
            }
        }

        public void setContentPositionForNextDraw(int contentX, int contentY) {
            this.mWindowPositionX = contentX - this.mOffsetX;
            this.mWindowPositionY = contentY - this.mOffsetY;
            this.mPendingWindowPositionUpdate = true;
            requestUpdate();
        }

        public void updateContent(Bitmap bitmap) {
            Bitmap bitmap2 = this.mBitmap;
            if (bitmap2 != null) {
                bitmap2.recycle();
            }
            this.mBitmap = bitmap;
            requestUpdate();
        }

        private void requestUpdate() {
            if (this.mFrameDrawScheduled) {
                return;
            }
            Message request = Message.obtain(this.mHandler, this.mMagnifierUpdater);
            request.setAsynchronous(true);
            request.sendToTarget();
            this.mFrameDrawScheduled = true;
        }

        public void destroy() {
            this.mRenderer.destroy();
            this.mSurface.destroy();
            this.mBBQ.destroy();
            new SurfaceControl.Transaction().remove(this.mSurfaceControl).remove(this.mBbqSurfaceControl).apply();
            this.mSurfaceSession.kill();
            this.mHandler.removeCallbacks(this.mMagnifierUpdater);
            Bitmap bitmap = this.mBitmap;
            if (bitmap != null) {
                bitmap.recycle();
            }
            this.mOverlay.setCallback(null);
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* JADX WARN: Removed duplicated region for block: B:32:0x00ef  */
        /* JADX WARN: Removed duplicated region for block: B:46:? A[RETURN, SYNTHETIC] */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
        */
        public void doDraw() {
            int i;
            boolean z;
            HardwareRenderer.FrameDrawingCallback callback;
            synchronized (this.mLock) {
                if (!this.mSurface.isValid()) {
                    return;
                }
                RecordingCanvas canvas = this.mBitmapRenderNode.beginRecording(this.mContentWidth, this.mContentHeight);
                try {
                    int w = this.mBitmap.getWidth();
                    int h = this.mBitmap.getHeight();
                    Paint paint = new Paint();
                    paint.setFilterBitmap(true);
                    if (this.mIsFishEyeStyle) {
                        try {
                            int margin = (int) ((this.mContentWidth - ((i - (this.mRamp * 2)) / this.mZoom)) / 2.0f);
                            Rect srcRect = new Rect(margin, 0, w - margin, h);
                            int i2 = this.mRamp;
                            Rect dstRect = new Rect(i2, 0, this.mContentWidth - i2, this.mContentHeight);
                            canvas.drawBitmap(this.mBitmap, srcRect, dstRect, paint);
                            canvas.drawBitmapMesh(Bitmap.createBitmap(this.mBitmap, 0, 0, margin, h), this.mMeshWidth, this.mMeshHeight, this.mMeshLeft, 0, null, 0, paint);
                            canvas.drawBitmapMesh(Bitmap.createBitmap(this.mBitmap, w - margin, 0, margin, h), this.mMeshWidth, this.mMeshHeight, this.mMeshRight, 0, null, 0, paint);
                        } catch (Throwable th) {
                            th = th;
                            this.mBitmapRenderNode.endRecording();
                            throw th;
                        }
                    } else {
                        Rect srcRect2 = new Rect(0, 0, w, h);
                        Rect dstRect2 = new Rect(0, 0, this.mContentWidth, this.mContentHeight);
                        canvas.drawBitmap(this.mBitmap, srcRect2, dstRect2, paint);
                    }
                    this.mBitmapRenderNode.endRecording();
                    final boolean updateWindowPosition = this.mPendingWindowPositionUpdate;
                    if (!updateWindowPosition && !this.mFirstDraw) {
                        callback = null;
                        z = false;
                        this.mFrameDrawScheduled = z;
                        this.mRenderer.draw(callback);
                        if (this.mCallback == null) {
                            updateCurrentContentForTesting();
                            this.mCallback.onOperationComplete();
                            return;
                        }
                        return;
                    }
                    final boolean firstDraw = this.mFirstDraw;
                    this.mFirstDraw = false;
                    this.mPendingWindowPositionUpdate = false;
                    final int pendingX = this.mWindowPositionX;
                    final int pendingY = this.mWindowPositionY;
                    z = false;
                    HardwareRenderer.FrameDrawingCallback callback2 = new HardwareRenderer.FrameDrawingCallback() { // from class: android.widget.Magnifier$InternalPopupWindow$$ExternalSyntheticLambda0
                        @Override // android.graphics.HardwareRenderer.FrameDrawingCallback
                        public final void onFrameDraw(long j) {
                            Magnifier.InternalPopupWindow.this.lambda$doDraw$0(updateWindowPosition, pendingX, pendingY, firstDraw, j);
                        }
                    };
                    if (!this.mIsFishEyeStyle) {
                        this.mRenderer.setLightCenter(this.mDisplay, pendingX, pendingY);
                    }
                    callback = callback2;
                    this.mFrameDrawScheduled = z;
                    this.mRenderer.draw(callback);
                    if (this.mCallback == null) {
                    }
                } catch (Throwable th2) {
                    th = th2;
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$doDraw$0(boolean updateWindowPosition, int pendingX, int pendingY, boolean firstDraw, long frame) {
            if (!this.mSurface.isValid()) {
                return;
            }
            if (updateWindowPosition) {
                this.mTransaction.setPosition(this.mSurfaceControl, pendingX, pendingY);
            }
            if (firstDraw) {
                this.mTransaction.setLayer(this.mSurfaceControl, 5).show(this.mSurfaceControl);
            }
            this.mBBQ.mergeWithNextTransaction(this.mTransaction, frame);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void updateCurrentContentForTesting() {
            Canvas canvas = new Canvas(this.mCurrentContent);
            Rect bounds = new Rect(0, 0, this.mContentWidth, this.mContentHeight);
            Bitmap bitmap = this.mBitmap;
            if (bitmap != null && !bitmap.isRecycled()) {
                Rect originalBounds = new Rect(0, 0, this.mBitmap.getWidth(), this.mBitmap.getHeight());
                canvas.drawBitmap(this.mBitmap, originalBounds, bounds, (Paint) null);
            }
            this.mOverlay.setBounds(bounds);
            this.mOverlay.draw(canvas);
        }
    }

    /* loaded from: classes4.dex */
    public static final class Builder {
        private int mBottomContentBound;
        private boolean mClippingEnabled;
        private float mCornerRadius;
        private float mElevation;
        private int mHeight;
        private int mHorizontalDefaultSourceToMagnifierOffset;
        private boolean mIsFishEyeStyle;
        private int mLeftContentBound;
        private Drawable mOverlay;
        private int mRightContentBound;
        private int mSourceHeight;
        private int mSourceWidth;
        private int mTopContentBound;
        private int mVerticalDefaultSourceToMagnifierOffset;
        private View mView;
        private int mWidth;
        private float mZoom;

        public Builder(View view) {
            this.mView = (View) Objects.requireNonNull(view);
            applyDefaults();
        }

        private void applyDefaults() {
            Resources resources = this.mView.getContext().getResources();
            this.mWidth = resources.getDimensionPixelSize(C4057R.dimen.default_magnifier_width);
            this.mHeight = resources.getDimensionPixelSize(C4057R.dimen.default_magnifier_height);
            this.mElevation = resources.getDimension(C4057R.dimen.default_magnifier_elevation);
            this.mCornerRadius = resources.getDimension(C4057R.dimen.default_magnifier_corner_radius);
            this.mZoom = resources.getFloat(C4057R.dimen.default_magnifier_zoom);
            this.mHorizontalDefaultSourceToMagnifierOffset = resources.getDimensionPixelSize(C4057R.dimen.default_magnifier_horizontal_offset);
            this.mVerticalDefaultSourceToMagnifierOffset = resources.getDimensionPixelSize(C4057R.dimen.default_magnifier_vertical_offset);
            this.mOverlay = new ColorDrawable(resources.getColor(C4057R.color.default_magnifier_color_overlay, null));
            this.mClippingEnabled = true;
            this.mLeftContentBound = 1;
            this.mTopContentBound = 1;
            this.mRightContentBound = 1;
            this.mBottomContentBound = 1;
            this.mIsFishEyeStyle = false;
        }

        public Builder setSize(int width, int height) {
            Preconditions.checkArgumentPositive(width, "Width should be positive");
            Preconditions.checkArgumentPositive(height, "Height should be positive");
            this.mWidth = width;
            this.mHeight = height;
            return this;
        }

        public Builder setInitialZoom(float zoom) {
            Preconditions.checkArgumentPositive(zoom, "Zoom should be positive");
            this.mZoom = zoom;
            return this;
        }

        public Builder setElevation(float elevation) {
            Preconditions.checkArgumentNonNegative(elevation, "Elevation should be non-negative");
            this.mElevation = elevation;
            return this;
        }

        public Builder setCornerRadius(float cornerRadius) {
            Preconditions.checkArgumentNonNegative(cornerRadius, "Corner radius should be non-negative");
            this.mCornerRadius = cornerRadius;
            return this;
        }

        public Builder setOverlay(Drawable overlay) {
            this.mOverlay = overlay;
            return this;
        }

        public Builder setDefaultSourceToMagnifierOffset(int horizontalOffset, int verticalOffset) {
            this.mHorizontalDefaultSourceToMagnifierOffset = horizontalOffset;
            this.mVerticalDefaultSourceToMagnifierOffset = verticalOffset;
            return this;
        }

        public Builder setClippingEnabled(boolean clip) {
            this.mClippingEnabled = clip;
            return this;
        }

        public Builder setSourceBounds(int left, int top, int right, int bottom) {
            this.mLeftContentBound = left;
            this.mTopContentBound = top;
            this.mRightContentBound = right;
            this.mBottomContentBound = bottom;
            return this;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public Builder setSourceSize(int width, int height) {
            this.mSourceWidth = width;
            this.mSourceHeight = height;
            return this;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public Builder setFishEyeStyle() {
            this.mIsFishEyeStyle = true;
            return this;
        }

        public Magnifier build() {
            return new Magnifier(this);
        }
    }

    public void setOnOperationCompleteCallback(Callback callback) {
        this.mCallback = callback;
        InternalPopupWindow internalPopupWindow = this.mWindow;
        if (internalPopupWindow != null) {
            internalPopupWindow.mCallback = callback;
        }
    }

    public Bitmap getContent() {
        Bitmap bitmap;
        InternalPopupWindow internalPopupWindow = this.mWindow;
        if (internalPopupWindow == null) {
            return null;
        }
        synchronized (internalPopupWindow.mLock) {
            bitmap = this.mWindow.mCurrentContent;
        }
        return bitmap;
    }

    public Bitmap getOriginalContent() {
        Bitmap createBitmap;
        InternalPopupWindow internalPopupWindow = this.mWindow;
        if (internalPopupWindow == null) {
            return null;
        }
        synchronized (internalPopupWindow.mLock) {
            createBitmap = Bitmap.createBitmap(this.mWindow.mBitmap);
        }
        return createBitmap;
    }

    public static PointF getMagnifierDefaultSize() {
        Resources resources = Resources.getSystem();
        float density = resources.getDisplayMetrics().density;
        PointF size = new PointF();
        size.f78x = resources.getDimension(C4057R.dimen.default_magnifier_width) / density;
        size.f79y = resources.getDimension(C4057R.dimen.default_magnifier_height) / density;
        return size;
    }
}
