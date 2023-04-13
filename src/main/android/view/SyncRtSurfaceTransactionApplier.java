package android.view;

import android.graphics.HardwareRenderer;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.view.SurfaceControl;
import android.view.View;
import java.util.function.Consumer;
/* loaded from: classes4.dex */
public class SyncRtSurfaceTransactionApplier {
    public static final int FLAG_ALL = -1;
    public static final int FLAG_ALPHA = 1;
    public static final int FLAG_BACKGROUND_BLUR_RADIUS = 32;
    public static final int FLAG_CORNER_RADIUS = 16;
    public static final int FLAG_LAYER = 8;
    public static final int FLAG_MATRIX = 2;
    public static final int FLAG_TRANSACTION = 128;
    public static final int FLAG_VISIBILITY = 64;
    public static final int FLAG_WINDOW_CROP = 4;
    private SurfaceControl mTargetSc;
    private final ViewRootImpl mTargetViewRootImpl;
    private final float[] mTmpFloat9 = new float[9];

    public SyncRtSurfaceTransactionApplier(View targetView) {
        this.mTargetViewRootImpl = targetView != null ? targetView.getViewRootImpl() : null;
    }

    public void scheduleApply(SurfaceParams... params) {
        ViewRootImpl viewRootImpl = this.mTargetViewRootImpl;
        if (viewRootImpl == null) {
            return;
        }
        this.mTargetSc = viewRootImpl.getSurfaceControl();
        final SurfaceControl.Transaction t = new SurfaceControl.Transaction();
        applyParams(t, params);
        this.mTargetViewRootImpl.registerRtFrameCallback(new HardwareRenderer.FrameDrawingCallback() { // from class: android.view.SyncRtSurfaceTransactionApplier$$ExternalSyntheticLambda0
            @Override // android.graphics.HardwareRenderer.FrameDrawingCallback
            public final void onFrameDraw(long j) {
                SyncRtSurfaceTransactionApplier.this.lambda$scheduleApply$0(t, j);
            }
        });
        this.mTargetViewRootImpl.getView().invalidate();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$scheduleApply$0(SurfaceControl.Transaction t, long frame) {
        SurfaceControl surfaceControl = this.mTargetSc;
        if (surfaceControl != null && surfaceControl.isValid()) {
            applyTransaction(t, frame);
        }
        t.close();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void applyParams(SurfaceControl.Transaction t, SurfaceParams... params) {
        for (int i = params.length - 1; i >= 0; i--) {
            SurfaceParams surfaceParams = params[i];
            SurfaceControl surfaceControl = surfaceParams.surface;
            applyParams(t, surfaceParams, this.mTmpFloat9);
        }
    }

    void applyTransaction(SurfaceControl.Transaction t, long frame) {
        ViewRootImpl viewRootImpl = this.mTargetViewRootImpl;
        if (viewRootImpl != null) {
            viewRootImpl.lambda$applyTransactionOnDraw$11(t, frame);
        } else {
            t.apply();
        }
    }

    public static void applyParams(SurfaceControl.Transaction t, SurfaceParams params, float[] tmpFloat9) {
        if ((params.flags & 128) != 0) {
            t.merge(params.mergeTransaction);
        }
        if ((params.flags & 2) != 0) {
            t.setMatrix(params.surface, params.matrix, tmpFloat9);
        }
        if ((params.flags & 4) != 0) {
            t.setWindowCrop(params.surface, params.windowCrop);
        }
        if ((params.flags & 1) != 0) {
            t.setAlpha(params.surface, params.alpha);
        }
        if ((params.flags & 8) != 0) {
            t.setLayer(params.surface, params.layer);
        }
        if ((params.flags & 16) != 0) {
            t.setCornerRadius(params.surface, params.cornerRadius);
        }
        if ((params.flags & 32) != 0) {
            t.setBackgroundBlurRadius(params.surface, params.backgroundBlurRadius);
        }
        if ((params.flags & 64) != 0) {
            if (params.visible) {
                t.show(params.surface);
            } else {
                t.hide(params.surface);
            }
        }
    }

    public static void create(final View targetView, final Consumer<SyncRtSurfaceTransactionApplier> callback) {
        if (targetView == null) {
            callback.accept(null);
        } else if (targetView.getViewRootImpl() != null) {
            callback.accept(new SyncRtSurfaceTransactionApplier(targetView));
        } else {
            targetView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() { // from class: android.view.SyncRtSurfaceTransactionApplier.1
                @Override // android.view.View.OnAttachStateChangeListener
                public void onViewAttachedToWindow(View v) {
                    View.this.removeOnAttachStateChangeListener(this);
                    callback.accept(new SyncRtSurfaceTransactionApplier(View.this));
                }

                @Override // android.view.View.OnAttachStateChangeListener
                public void onViewDetachedFromWindow(View v) {
                }
            });
        }
    }

    /* loaded from: classes4.dex */
    public static class SurfaceParams {
        public final float alpha;
        public final int backgroundBlurRadius;
        public final float cornerRadius;
        private final int flags;
        public final int layer;
        public final Matrix matrix;
        public final SurfaceControl.Transaction mergeTransaction;
        public final SurfaceControl surface;
        public final boolean visible;
        public final Rect windowCrop;

        /* loaded from: classes4.dex */
        public static class Builder {
            float alpha;
            int backgroundBlurRadius;
            float cornerRadius;
            int flags;
            int layer;
            Matrix matrix;
            SurfaceControl.Transaction mergeTransaction;
            final SurfaceControl surface;
            boolean visible;
            Rect windowCrop;

            public Builder(SurfaceControl surface) {
                this.surface = surface;
            }

            public Builder withAlpha(float alpha) {
                this.alpha = alpha;
                this.flags |= 1;
                return this;
            }

            public Builder withMatrix(Matrix matrix) {
                this.matrix = new Matrix(matrix);
                this.flags |= 2;
                return this;
            }

            public Builder withWindowCrop(Rect windowCrop) {
                this.windowCrop = new Rect(windowCrop);
                this.flags |= 4;
                return this;
            }

            public Builder withLayer(int layer) {
                this.layer = layer;
                this.flags |= 8;
                return this;
            }

            public Builder withCornerRadius(float radius) {
                this.cornerRadius = radius;
                this.flags |= 16;
                return this;
            }

            public Builder withBackgroundBlur(int radius) {
                this.backgroundBlurRadius = radius;
                this.flags |= 32;
                return this;
            }

            public Builder withVisibility(boolean visible) {
                this.visible = visible;
                this.flags |= 64;
                return this;
            }

            public Builder withMergeTransaction(SurfaceControl.Transaction mergeTransaction) {
                this.mergeTransaction = mergeTransaction;
                this.flags |= 128;
                return this;
            }

            public SurfaceParams build() {
                return new SurfaceParams(this.surface, this.flags, this.alpha, this.matrix, this.windowCrop, this.layer, this.cornerRadius, this.backgroundBlurRadius, this.visible, this.mergeTransaction);
            }
        }

        private SurfaceParams(SurfaceControl surface, int params, float alpha, Matrix matrix, Rect windowCrop, int layer, float cornerRadius, int backgroundBlurRadius, boolean visible, SurfaceControl.Transaction mergeTransaction) {
            this.flags = params;
            this.surface = surface;
            this.alpha = alpha;
            this.matrix = matrix;
            this.windowCrop = windowCrop;
            this.layer = layer;
            this.cornerRadius = cornerRadius;
            this.backgroundBlurRadius = backgroundBlurRadius;
            this.visible = visible;
            this.mergeTransaction = mergeTransaction;
        }
    }
}
