package com.android.internal.view;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.HardwareRenderer;
import android.graphics.RecordingCanvas;
import android.graphics.Rect;
import android.graphics.RenderNode;
import android.p008os.CancellationSignal;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ScrollCaptureCallback;
import android.view.ScrollCaptureSession;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import com.android.internal.view.ScrollCaptureViewHelper;
import java.lang.ref.WeakReference;
import java.util.function.Consumer;
/* loaded from: classes2.dex */
public class ScrollCaptureViewSupport<V extends View> implements ScrollCaptureCallback {
    private static final String SETTING_CAPTURE_DELAY = "screenshot.scroll_capture_delay";
    private static final long SETTING_CAPTURE_DELAY_DEFAULT = 60;
    private static final String TAG = "SCViewSupport";
    private boolean mEnded;
    private final long mPostScrollDelayMillis;
    private final ViewRenderer mRenderer = new ViewRenderer();
    private boolean mStarted;
    private final ScrollCaptureViewHelper<V> mViewHelper;
    private final WeakReference<V> mWeakView;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ScrollCaptureViewSupport(V containingView, ScrollCaptureViewHelper<V> viewHelper) {
        this.mWeakView = new WeakReference<>(containingView);
        this.mViewHelper = viewHelper;
        Context context = containingView.getContext();
        ContentResolver contentResolver = context.getContentResolver();
        long j = Settings.Global.getLong(contentResolver, SETTING_CAPTURE_DELAY, SETTING_CAPTURE_DELAY_DEFAULT);
        this.mPostScrollDelayMillis = j;
        Log.m112d(TAG, "screenshot.scroll_capture_delay = " + j);
    }

    private static int getColorMode(View containingView) {
        Context context = containingView.getContext();
        int colorMode = containingView.getViewRootImpl().mWindowAttributes.getColorMode();
        if (!context.getResources().getConfiguration().isScreenWideColorGamut()) {
            return 0;
        }
        return colorMode;
    }

    public static Rect transformFromRequestToContainer(int scrollY, Rect requestBounds, Rect requestRect) {
        Rect requestedContainerBounds = new Rect(requestRect);
        requestedContainerBounds.offset(0, -scrollY);
        requestedContainerBounds.offset(requestBounds.left, requestBounds.top);
        return requestedContainerBounds;
    }

    public static Rect transformFromContainerToRequest(int scrollY, Rect requestBounds, Rect containerRect) {
        Rect requestRect = new Rect(containerRect);
        requestRect.offset(-requestBounds.left, -requestBounds.top);
        requestRect.offset(0, scrollY);
        return requestRect;
    }

    public static int computeScrollAmount(Rect parentVisibleBounds, Rect requested) {
        int height = parentVisibleBounds.height();
        int top = parentVisibleBounds.top;
        int bottom = parentVisibleBounds.bottom;
        if (requested.bottom > bottom && requested.top > top) {
            if (requested.height() > height) {
                int scrollYDelta = 0 + (requested.top - top);
                return scrollYDelta;
            }
            int scrollYDelta2 = 0 + (requested.bottom - bottom);
            return scrollYDelta2;
        } else if (requested.top >= top || requested.bottom >= bottom) {
            return 0;
        } else {
            if (requested.height() > height) {
                int scrollYDelta3 = 0 - (bottom - requested.bottom);
                return scrollYDelta3;
            }
            int scrollYDelta4 = 0 - (top - requested.top);
            return scrollYDelta4;
        }
    }

    public static View findScrollingReferenceView(ViewGroup parent, int expectedScrollDistance) {
        View selected = null;
        Rect parentLocalVisible = new Rect();
        parent.getLocalVisibleRect(parentLocalVisible);
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);
            if (selected == null) {
                selected = child;
            } else if (expectedScrollDistance < 0) {
                if (child.getTop() < selected.getTop()) {
                    selected = child;
                }
            } else if (child.getBottom() > selected.getBottom()) {
                selected = child;
            }
        }
        return selected;
    }

    @Override // android.view.ScrollCaptureCallback
    public final void onScrollCaptureSearch(CancellationSignal signal, Consumer<Rect> onReady) {
        if (signal.isCanceled()) {
            return;
        }
        V view = this.mWeakView.get();
        this.mStarted = false;
        this.mEnded = false;
        if (view != null && view.isVisibleToUser() && this.mViewHelper.onAcceptSession(view)) {
            onReady.accept(this.mViewHelper.onComputeScrollBounds(view));
        } else {
            onReady.accept(null);
        }
    }

    @Override // android.view.ScrollCaptureCallback
    public final void onScrollCaptureStart(ScrollCaptureSession session, CancellationSignal signal, Runnable onReady) {
        if (signal.isCanceled()) {
            return;
        }
        V view = this.mWeakView.get();
        this.mEnded = false;
        this.mStarted = true;
        if (view != null && view.isVisibleToUser()) {
            this.mRenderer.setSurface(session.getSurface());
            this.mViewHelper.onPrepareForStart(view, session.getScrollBounds());
        }
        onReady.run();
    }

    @Override // android.view.ScrollCaptureCallback
    public final void onScrollCaptureImageRequest(ScrollCaptureSession session, final CancellationSignal signal, Rect requestRect, final Consumer<Rect> onComplete) {
        if (signal.isCanceled()) {
            Log.m104w(TAG, "onScrollCaptureImageRequest: cancelled!");
            return;
        }
        final V view = this.mWeakView.get();
        if (view == null || !view.isVisibleToUser()) {
            onComplete.accept(new Rect());
        } else {
            this.mViewHelper.onScrollRequested(view, session.getScrollBounds(), requestRect, signal, new Consumer() { // from class: com.android.internal.view.ScrollCaptureViewSupport$$ExternalSyntheticLambda0
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    ScrollCaptureViewSupport.this.lambda$onScrollCaptureImageRequest$0(view, signal, onComplete, (ScrollCaptureViewHelper.ScrollResult) obj);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: onScrollResult */
    public void lambda$onScrollCaptureImageRequest$0(final ScrollCaptureViewHelper.ScrollResult scrollResult, final V view, CancellationSignal signal, final Consumer<Rect> onComplete) {
        if (signal.isCanceled()) {
            Log.m104w(TAG, "onScrollCaptureImageRequest: cancelled! skipping render.");
        } else if (scrollResult.availableArea.isEmpty()) {
            onComplete.accept(scrollResult.availableArea);
        } else {
            final Rect viewCaptureArea = new Rect(scrollResult.availableArea);
            viewCaptureArea.offset(0, -scrollResult.scrollDelta);
            view.postOnAnimationDelayed(new Runnable() { // from class: com.android.internal.view.ScrollCaptureViewSupport$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    ScrollCaptureViewSupport.this.lambda$onScrollResult$1(scrollResult, view, viewCaptureArea, onComplete);
                }
            }, this.mPostScrollDelayMillis);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* renamed from: doCapture */
    public void lambda$onScrollResult$1(ScrollCaptureViewHelper.ScrollResult scrollResult, V view, Rect viewCaptureArea, Consumer<Rect> onComplete) {
        int result = this.mRenderer.renderView(view, viewCaptureArea);
        if (result == 0 || result == 1) {
            onComplete.accept(new Rect(scrollResult.availableArea));
            return;
        }
        Log.m110e(TAG, "syncAndDraw(): SyncAndDrawResult = " + result);
        onComplete.accept(new Rect());
    }

    @Override // android.view.ScrollCaptureCallback
    public final void onScrollCaptureEnd(Runnable onReady) {
        V view = this.mWeakView.get();
        if (this.mStarted && !this.mEnded) {
            if (view != null) {
                this.mViewHelper.onPrepareForEnd(view);
                view.invalidate();
            }
            this.mEnded = true;
            this.mRenderer.destroy();
        }
        onReady.run();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes2.dex */
    public static final class ViewRenderer {
        private static final float AMBIENT_SHADOW_ALPHA = 0.039f;
        private static final float LIGHT_RADIUS_DP = 800.0f;
        private static final float LIGHT_Z_DP = 400.0f;
        private static final float SPOT_SHADOW_ALPHA = 0.039f;
        private static final String TAG = "ViewRenderer";
        private final RenderNode mCaptureRenderNode;
        private final HardwareRenderer mRenderer;
        private Surface mSurface;
        private final Rect mTempRect = new Rect();
        private final int[] mTempLocation = new int[2];
        private long mLastRenderedSourceDrawingId = -1;

        ViewRenderer() {
            HardwareRenderer hardwareRenderer = new HardwareRenderer();
            this.mRenderer = hardwareRenderer;
            hardwareRenderer.setName("ScrollCapture");
            RenderNode renderNode = new RenderNode("ScrollCaptureRoot");
            this.mCaptureRenderNode = renderNode;
            hardwareRenderer.setContentRoot(renderNode);
            hardwareRenderer.setOpaque(false);
        }

        public void setSurface(Surface surface) {
            this.mSurface = surface;
            this.mRenderer.setSurface(surface);
        }

        private boolean updateForView(View source) {
            if (this.mLastRenderedSourceDrawingId == source.getUniqueDrawingId()) {
                return false;
            }
            this.mLastRenderedSourceDrawingId = source.getUniqueDrawingId();
            return true;
        }

        private void setupLighting(View mSource) {
            this.mLastRenderedSourceDrawingId = mSource.getUniqueDrawingId();
            DisplayMetrics metrics = mSource.getResources().getDisplayMetrics();
            mSource.getLocationOnScreen(this.mTempLocation);
            float lightX = (metrics.widthPixels / 2.0f) - this.mTempLocation[0];
            float lightY = metrics.heightPixels - this.mTempLocation[1];
            int lightZ = (int) (metrics.density * 400.0f);
            int lightRadius = (int) (metrics.density * LIGHT_RADIUS_DP);
            this.mRenderer.setLightSourceGeometry(lightX, lightY, lightZ, lightRadius);
            this.mRenderer.setLightSourceAlpha(0.039f, 0.039f);
        }

        private void updateRootNode(View source, Rect localSourceRect) {
            View rootView = source.getRootView();
            transformToRoot(source, localSourceRect, this.mTempRect);
            this.mCaptureRenderNode.setPosition(0, 0, this.mTempRect.width(), this.mTempRect.height());
            RecordingCanvas canvas = this.mCaptureRenderNode.beginRecording();
            canvas.enableZ();
            canvas.translate(-this.mTempRect.left, -this.mTempRect.top);
            RenderNode rootViewRenderNode = rootView.updateDisplayListIfDirty();
            if (rootViewRenderNode.hasDisplayList()) {
                canvas.drawRenderNode(rootViewRenderNode);
            }
            this.mCaptureRenderNode.endRecording();
        }

        public int renderView(View view, Rect sourceRect) {
            HardwareRenderer.FrameRenderRequest request = this.mRenderer.createRenderRequest();
            request.setVsyncTime(System.nanoTime());
            if (updateForView(view)) {
                setupLighting(view);
            }
            view.invalidate();
            updateRootNode(view, sourceRect);
            return request.syncAndDraw();
        }

        public void trimMemory() {
            this.mRenderer.clearContent();
        }

        public void destroy() {
            this.mSurface = null;
            this.mRenderer.destroy();
        }

        private void transformToRoot(View local, Rect localRect, Rect outRect) {
            local.getLocationInWindow(this.mTempLocation);
            outRect.set(localRect);
            int[] iArr = this.mTempLocation;
            outRect.offset(iArr[0], iArr[1]);
        }

        public void setColorMode(int colorMode) {
            this.mRenderer.setColorMode(colorMode);
        }
    }

    public String toString() {
        return "ScrollCaptureViewSupport{view=" + this.mWeakView.get() + ", helper=" + this.mViewHelper + '}';
    }
}
