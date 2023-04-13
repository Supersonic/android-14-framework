package com.android.server.p014wm;

import android.view.SurfaceControl;
import com.android.server.p014wm.SurfaceAnimator;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
/* renamed from: com.android.server.wm.SimpleSurfaceAnimatable */
/* loaded from: classes2.dex */
public class SimpleSurfaceAnimatable implements SurfaceAnimator.Animatable {
    public final Supplier<SurfaceControl.Builder> mAnimationLeashFactory;
    public final SurfaceControl mAnimationLeashParent;
    public final Runnable mCommitTransactionRunnable;
    public final int mHeight;
    public final Consumer<Runnable> mOnAnimationFinished;
    public final BiConsumer<SurfaceControl.Transaction, SurfaceControl> mOnAnimationLeashCreated;
    public final Consumer<SurfaceControl.Transaction> mOnAnimationLeashLost;
    public final SurfaceControl mParentSurfaceControl;
    public final Supplier<SurfaceControl.Transaction> mPendingTransaction;
    public final boolean mShouldDeferAnimationFinish;
    public final SurfaceControl mSurfaceControl;
    public final Supplier<SurfaceControl.Transaction> mSyncTransaction;
    public final int mWidth;

    public SimpleSurfaceAnimatable(Builder builder) {
        this.mWidth = builder.mWidth;
        this.mHeight = builder.mHeight;
        this.mShouldDeferAnimationFinish = builder.mShouldDeferAnimationFinish;
        this.mAnimationLeashParent = builder.mAnimationLeashParent;
        this.mSurfaceControl = builder.mSurfaceControl;
        this.mParentSurfaceControl = builder.mParentSurfaceControl;
        this.mCommitTransactionRunnable = builder.mCommitTransactionRunnable;
        this.mAnimationLeashFactory = builder.mAnimationLeashFactory;
        this.mOnAnimationLeashCreated = builder.mOnAnimationLeashCreated;
        this.mOnAnimationLeashLost = builder.mOnAnimationLeashLost;
        this.mSyncTransaction = builder.mSyncTransactionSupplier;
        this.mPendingTransaction = builder.mPendingTransactionSupplier;
        this.mOnAnimationFinished = builder.mOnAnimationFinished;
    }

    @Override // com.android.server.p014wm.SurfaceAnimator.Animatable
    public SurfaceControl.Transaction getSyncTransaction() {
        return this.mSyncTransaction.get();
    }

    @Override // com.android.server.p014wm.SurfaceAnimator.Animatable
    public void commitPendingTransaction() {
        this.mCommitTransactionRunnable.run();
    }

    @Override // com.android.server.p014wm.SurfaceAnimator.Animatable
    public void onAnimationLeashCreated(SurfaceControl.Transaction transaction, SurfaceControl surfaceControl) {
        BiConsumer<SurfaceControl.Transaction, SurfaceControl> biConsumer = this.mOnAnimationLeashCreated;
        if (biConsumer != null) {
            biConsumer.accept(transaction, surfaceControl);
        }
    }

    @Override // com.android.server.p014wm.SurfaceAnimator.Animatable
    public void onAnimationLeashLost(SurfaceControl.Transaction transaction) {
        Consumer<SurfaceControl.Transaction> consumer = this.mOnAnimationLeashLost;
        if (consumer != null) {
            consumer.accept(transaction);
        }
    }

    @Override // com.android.server.p014wm.SurfaceAnimator.Animatable
    public SurfaceControl.Builder makeAnimationLeash() {
        return this.mAnimationLeashFactory.get();
    }

    @Override // com.android.server.p014wm.SurfaceAnimator.Animatable
    public SurfaceControl getAnimationLeashParent() {
        return this.mAnimationLeashParent;
    }

    @Override // com.android.server.p014wm.SurfaceAnimator.Animatable
    public SurfaceControl getSurfaceControl() {
        return this.mSurfaceControl;
    }

    @Override // com.android.server.p014wm.SurfaceAnimator.Animatable
    public SurfaceControl getParentSurfaceControl() {
        return this.mParentSurfaceControl;
    }

    @Override // com.android.server.p014wm.SurfaceAnimator.Animatable
    public int getSurfaceWidth() {
        return this.mWidth;
    }

    @Override // com.android.server.p014wm.SurfaceAnimator.Animatable
    public int getSurfaceHeight() {
        return this.mHeight;
    }

    @Override // com.android.server.p014wm.SurfaceAnimator.Animatable
    public boolean shouldDeferAnimationFinish(Runnable runnable) {
        Consumer<Runnable> consumer = this.mOnAnimationFinished;
        if (consumer != null) {
            consumer.accept(runnable);
        }
        return this.mShouldDeferAnimationFinish;
    }

    /* renamed from: com.android.server.wm.SimpleSurfaceAnimatable$Builder */
    /* loaded from: classes2.dex */
    public static class Builder {
        public Supplier<SurfaceControl.Builder> mAnimationLeashFactory;
        public Runnable mCommitTransactionRunnable;
        public Supplier<SurfaceControl.Transaction> mPendingTransactionSupplier;
        public Supplier<SurfaceControl.Transaction> mSyncTransactionSupplier;
        public int mWidth = -1;
        public int mHeight = -1;
        public boolean mShouldDeferAnimationFinish = false;
        public SurfaceControl mAnimationLeashParent = null;
        public SurfaceControl mSurfaceControl = null;
        public SurfaceControl mParentSurfaceControl = null;
        public BiConsumer<SurfaceControl.Transaction, SurfaceControl> mOnAnimationLeashCreated = null;
        public Consumer<SurfaceControl.Transaction> mOnAnimationLeashLost = null;
        public Consumer<Runnable> mOnAnimationFinished = null;

        public Builder setCommitTransactionRunnable(Runnable runnable) {
            this.mCommitTransactionRunnable = runnable;
            return this;
        }

        public Builder setSyncTransactionSupplier(Supplier<SurfaceControl.Transaction> supplier) {
            this.mSyncTransactionSupplier = supplier;
            return this;
        }

        public Builder setPendingTransactionSupplier(Supplier<SurfaceControl.Transaction> supplier) {
            this.mPendingTransactionSupplier = supplier;
            return this;
        }

        public Builder setAnimationLeashSupplier(Supplier<SurfaceControl.Builder> supplier) {
            this.mAnimationLeashFactory = supplier;
            return this;
        }

        public Builder setAnimationLeashParent(SurfaceControl surfaceControl) {
            this.mAnimationLeashParent = surfaceControl;
            return this;
        }

        public Builder setSurfaceControl(SurfaceControl surfaceControl) {
            this.mSurfaceControl = surfaceControl;
            return this;
        }

        public Builder setParentSurfaceControl(SurfaceControl surfaceControl) {
            this.mParentSurfaceControl = surfaceControl;
            return this;
        }

        public Builder setWidth(int i) {
            this.mWidth = i;
            return this;
        }

        public Builder setHeight(int i) {
            this.mHeight = i;
            return this;
        }

        public SurfaceAnimator.Animatable build() {
            if (this.mSyncTransactionSupplier == null) {
                throw new IllegalArgumentException("mSyncTransactionSupplier cannot be null");
            }
            if (this.mPendingTransactionSupplier == null) {
                throw new IllegalArgumentException("mPendingTransactionSupplier cannot be null");
            }
            if (this.mAnimationLeashFactory == null) {
                throw new IllegalArgumentException("mAnimationLeashFactory cannot be null");
            }
            if (this.mCommitTransactionRunnable == null) {
                throw new IllegalArgumentException("mCommitTransactionRunnable cannot be null");
            }
            if (this.mSurfaceControl == null) {
                throw new IllegalArgumentException("mSurfaceControl cannot be null");
            }
            return new SimpleSurfaceAnimatable(this);
        }
    }
}
