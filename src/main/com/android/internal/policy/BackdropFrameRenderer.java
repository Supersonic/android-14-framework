package com.android.internal.policy;

import android.graphics.Insets;
import android.graphics.RecordingCanvas;
import android.graphics.Rect;
import android.graphics.RenderNode;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.p008os.Looper;
import android.view.Choreographer;
import android.view.ThreadedRenderer;
/* loaded from: classes4.dex */
public class BackdropFrameRenderer extends Thread implements Choreographer.FrameCallback {
    private Drawable mCaptionBackgroundDrawable;
    private Choreographer mChoreographer;
    private DecorView mDecorView;
    private RenderNode mFrameAndBackdropNode;
    private boolean mFullscreen;
    private int mLastCaptionHeight;
    private int mLastContentHeight;
    private int mLastContentWidth;
    private int mLastXOffset;
    private int mLastYOffset;
    private ColorDrawable mNavigationBarColor;
    private final Rect mNewTargetRect;
    private boolean mOldFullscreen;
    private final Rect mOldSystemBarInsets;
    private final Rect mOldTargetRect;
    private ThreadedRenderer mRenderer;
    private boolean mReportNextDraw;
    private Drawable mResizingBackgroundDrawable;
    private ColorDrawable mStatusBarColor;
    private RenderNode mSystemBarBackgroundNode;
    private final Rect mSystemBarInsets;
    private final Rect mTargetRect;
    private final Rect mTmpRect;
    private Drawable mUserCaptionBackgroundDrawable;

    public BackdropFrameRenderer(DecorView decorView, ThreadedRenderer renderer, Rect initialBounds, Drawable resizingBackgroundDrawable, Drawable captionBackgroundDrawable, Drawable userCaptionBackgroundDrawable, int statusBarColor, int navigationBarColor, boolean fullscreen, Insets systemBarInsets) {
        Rect rect = new Rect();
        this.mTargetRect = rect;
        this.mOldTargetRect = new Rect();
        this.mNewTargetRect = new Rect();
        Rect rect2 = new Rect();
        this.mOldSystemBarInsets = rect2;
        Rect rect3 = new Rect();
        this.mSystemBarInsets = rect3;
        this.mTmpRect = new Rect();
        setName("ResizeFrame");
        this.mRenderer = renderer;
        onResourcesLoaded(decorView, resizingBackgroundDrawable, captionBackgroundDrawable, userCaptionBackgroundDrawable, statusBarColor, navigationBarColor);
        RenderNode create = RenderNode.create("FrameAndBackdropNode", null);
        this.mFrameAndBackdropNode = create;
        this.mRenderer.addRenderNode(create, true);
        rect.set(initialBounds);
        this.mFullscreen = fullscreen;
        this.mOldFullscreen = fullscreen;
        rect3.set(systemBarInsets.toRect());
        rect2.set(systemBarInsets.toRect());
        start();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onResourcesLoaded(DecorView decorView, Drawable resizingBackgroundDrawable, Drawable captionBackgroundDrawableDrawable, Drawable userCaptionBackgroundDrawable, int statusBarColor, int navigationBarColor) {
        Drawable drawable;
        Drawable drawable2;
        Drawable drawable3;
        synchronized (this) {
            this.mDecorView = decorView;
            if (resizingBackgroundDrawable != null && resizingBackgroundDrawable.getConstantState() != null) {
                drawable = resizingBackgroundDrawable.getConstantState().newDrawable();
            } else {
                drawable = null;
            }
            this.mResizingBackgroundDrawable = drawable;
            if (captionBackgroundDrawableDrawable != null && captionBackgroundDrawableDrawable.getConstantState() != null) {
                drawable2 = captionBackgroundDrawableDrawable.getConstantState().newDrawable();
            } else {
                drawable2 = null;
            }
            this.mCaptionBackgroundDrawable = drawable2;
            if (userCaptionBackgroundDrawable != null && userCaptionBackgroundDrawable.getConstantState() != null) {
                drawable3 = userCaptionBackgroundDrawable.getConstantState().newDrawable();
            } else {
                drawable3 = null;
            }
            this.mUserCaptionBackgroundDrawable = drawable3;
            if (this.mCaptionBackgroundDrawable == null) {
                this.mCaptionBackgroundDrawable = this.mResizingBackgroundDrawable;
            }
            if (statusBarColor != 0) {
                this.mStatusBarColor = new ColorDrawable(statusBarColor);
                addSystemBarNodeIfNeeded();
            } else {
                this.mStatusBarColor = null;
            }
            if (navigationBarColor != 0) {
                this.mNavigationBarColor = new ColorDrawable(navigationBarColor);
                addSystemBarNodeIfNeeded();
            } else {
                this.mNavigationBarColor = null;
            }
        }
    }

    private void addSystemBarNodeIfNeeded() {
        if (this.mSystemBarBackgroundNode != null) {
            return;
        }
        RenderNode create = RenderNode.create("SystemBarBackgroundNode", null);
        this.mSystemBarBackgroundNode = create;
        this.mRenderer.addRenderNode(create, false);
    }

    public void setTargetRect(Rect newTargetBounds, boolean fullscreen, Rect systemBarInsets) {
        synchronized (this) {
            this.mFullscreen = fullscreen;
            this.mTargetRect.set(newTargetBounds);
            this.mSystemBarInsets.set(systemBarInsets);
            pingRenderLocked(false);
        }
    }

    public void onConfigurationChange() {
        synchronized (this) {
            if (this.mRenderer != null) {
                this.mOldTargetRect.set(0, 0, 0, 0);
                pingRenderLocked(false);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void releaseRenderer() {
        synchronized (this) {
            ThreadedRenderer threadedRenderer = this.mRenderer;
            if (threadedRenderer != null) {
                threadedRenderer.setContentDrawBounds(0, 0, 0, 0);
                this.mRenderer.removeRenderNode(this.mFrameAndBackdropNode);
                RenderNode renderNode = this.mSystemBarBackgroundNode;
                if (renderNode != null) {
                    this.mRenderer.removeRenderNode(renderNode);
                }
                this.mRenderer = null;
                pingRenderLocked(false);
            }
        }
    }

    @Override // java.lang.Thread, java.lang.Runnable
    public void run() {
        try {
            Looper.prepare();
            synchronized (this) {
                if (this.mRenderer == null) {
                    return;
                }
                this.mChoreographer = Choreographer.getInstance();
                Looper.loop();
                synchronized (this) {
                    this.mChoreographer = null;
                    Choreographer.releaseInstance();
                }
            }
        } finally {
            releaseRenderer();
        }
    }

    @Override // android.view.Choreographer.FrameCallback
    public void doFrame(long frameTimeNanos) {
        synchronized (this) {
            if (this.mRenderer == null) {
                reportDrawIfNeeded();
                Looper.myLooper().quit();
                return;
            }
            doFrameUncheckedLocked();
        }
    }

    private void doFrameUncheckedLocked() {
        this.mNewTargetRect.set(this.mTargetRect);
        if (!this.mNewTargetRect.equals(this.mOldTargetRect) || this.mOldFullscreen != this.mFullscreen || !this.mSystemBarInsets.equals(this.mOldSystemBarInsets) || this.mReportNextDraw) {
            this.mOldFullscreen = this.mFullscreen;
            this.mOldTargetRect.set(this.mNewTargetRect);
            this.mOldSystemBarInsets.set(this.mSystemBarInsets);
            redrawLocked(this.mNewTargetRect, this.mFullscreen);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean onContentDrawn(int xOffset, int yOffset, int xSize, int ySize) {
        boolean z;
        synchronized (this) {
            z = true;
            boolean firstCall = this.mLastContentWidth == 0;
            this.mLastContentWidth = xSize;
            int i = this.mLastCaptionHeight;
            int i2 = ySize - i;
            this.mLastContentHeight = i2;
            this.mLastXOffset = xOffset;
            this.mLastYOffset = yOffset;
            this.mRenderer.setContentDrawBounds(xOffset, yOffset, xOffset + xSize, i + yOffset + i2);
            if (!firstCall || (this.mLastCaptionHeight == 0 && this.mDecorView.isShowingCaption())) {
                z = false;
            }
        }
        return z;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onRequestDraw(boolean reportNextDraw) {
        synchronized (this) {
            this.mReportNextDraw = reportNextDraw;
            this.mOldTargetRect.set(0, 0, 0, 0);
            pingRenderLocked(true);
        }
    }

    private void redrawLocked(Rect newBounds, boolean fullscreen) {
        int captionHeight = this.mDecorView.getCaptionHeight();
        if (captionHeight != 0) {
            this.mLastCaptionHeight = captionHeight;
        }
        if ((this.mLastCaptionHeight == 0 && this.mDecorView.isShowingCaption()) || this.mLastContentWidth == 0 || this.mLastContentHeight == 0) {
            return;
        }
        int left = this.mLastXOffset + newBounds.left;
        int top = this.mLastYOffset + newBounds.top;
        int width = newBounds.width();
        int height = newBounds.height();
        this.mFrameAndBackdropNode.setLeftTopRightBottom(left, top, left + width, top + height);
        RecordingCanvas canvas = this.mFrameAndBackdropNode.beginRecording(width, height);
        Drawable drawable = this.mUserCaptionBackgroundDrawable;
        if (drawable == null) {
            drawable = this.mCaptionBackgroundDrawable;
        }
        Drawable drawable2 = drawable;
        if (drawable2 != null) {
            drawable2.setBounds(0, 0, left + width, this.mLastCaptionHeight + top);
            drawable2.draw(canvas);
        }
        Drawable drawable3 = this.mResizingBackgroundDrawable;
        if (drawable3 != null) {
            drawable3.setBounds(0, this.mLastCaptionHeight, left + width, top + height);
            this.mResizingBackgroundDrawable.draw(canvas);
        }
        this.mFrameAndBackdropNode.endRecording();
        drawColorViews(left, top, width, height, fullscreen);
        this.mRenderer.drawRenderNode(this.mFrameAndBackdropNode);
        reportDrawIfNeeded();
    }

    private void drawColorViews(int left, int top, int width, int height, boolean fullscreen) {
        RenderNode renderNode = this.mSystemBarBackgroundNode;
        if (renderNode == null) {
            return;
        }
        RecordingCanvas canvas = renderNode.beginRecording(width, height);
        this.mSystemBarBackgroundNode.setLeftTopRightBottom(left, top, left + width, top + height);
        int topInset = this.mSystemBarInsets.top;
        ColorDrawable colorDrawable = this.mStatusBarColor;
        if (colorDrawable != null) {
            colorDrawable.setBounds(0, 0, left + width, topInset);
            this.mStatusBarColor.draw(canvas);
        }
        if (this.mNavigationBarColor != null && fullscreen) {
            DecorView.getNavigationBarRect(width, height, this.mSystemBarInsets, this.mTmpRect, 1.0f);
            this.mNavigationBarColor.setBounds(this.mTmpRect);
            this.mNavigationBarColor.draw(canvas);
        }
        this.mSystemBarBackgroundNode.endRecording();
        this.mRenderer.drawRenderNode(this.mSystemBarBackgroundNode);
    }

    private void reportDrawIfNeeded() {
        if (this.mReportNextDraw) {
            if (this.mDecorView.isAttachedToWindow()) {
                this.mDecorView.getViewRootImpl().reportDrawFinish();
            }
            this.mReportNextDraw = false;
        }
    }

    private void pingRenderLocked(boolean drawImmediate) {
        Choreographer choreographer = this.mChoreographer;
        if (choreographer != null && !drawImmediate) {
            choreographer.postFrameCallback(this);
        } else {
            doFrameUncheckedLocked();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setUserCaptionBackgroundDrawable(Drawable userCaptionBackgroundDrawable) {
        synchronized (this) {
            this.mUserCaptionBackgroundDrawable = userCaptionBackgroundDrawable;
        }
    }
}
