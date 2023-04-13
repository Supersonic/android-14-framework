package com.android.server.p014wm;

import android.graphics.ColorSpace;
import android.graphics.GraphicBuffer;
import android.graphics.Point;
import android.hardware.HardwareBuffer;
import android.util.proto.ProtoOutputStream;
import android.view.SurfaceControl;
import android.view.animation.Animation;
import com.android.internal.protolog.ProtoLogGroup;
import com.android.internal.protolog.ProtoLogImpl;
import com.android.server.p014wm.SurfaceAnimator;
/* renamed from: com.android.server.wm.WindowContainerThumbnail */
/* loaded from: classes2.dex */
public class WindowContainerThumbnail implements SurfaceAnimator.Animatable {
    public final int mHeight;
    public final SurfaceAnimator mSurfaceAnimator;
    public SurfaceControl mSurfaceControl;
    public final int mWidth;
    public final WindowContainer mWindowContainer;

    public final void onAnimationFinished(int i, AnimationAdapter animationAdapter) {
    }

    public WindowContainerThumbnail(SurfaceControl.Transaction transaction, WindowContainer windowContainer, HardwareBuffer hardwareBuffer) {
        this(transaction, windowContainer, hardwareBuffer, null);
    }

    public WindowContainerThumbnail(SurfaceControl.Transaction transaction, WindowContainer windowContainer, HardwareBuffer hardwareBuffer, SurfaceAnimator surfaceAnimator) {
        this.mWindowContainer = windowContainer;
        if (surfaceAnimator != null) {
            this.mSurfaceAnimator = surfaceAnimator;
        } else {
            this.mSurfaceAnimator = new SurfaceAnimator(this, new SurfaceAnimator.OnAnimationFinishedCallback() { // from class: com.android.server.wm.WindowContainerThumbnail$$ExternalSyntheticLambda0
                @Override // com.android.server.p014wm.SurfaceAnimator.OnAnimationFinishedCallback
                public final void onAnimationFinished(int i, AnimationAdapter animationAdapter) {
                    WindowContainerThumbnail.this.onAnimationFinished(i, animationAdapter);
                }
            }, windowContainer.mWmService);
        }
        this.mWidth = hardwareBuffer.getWidth();
        this.mHeight = hardwareBuffer.getHeight();
        SurfaceControl.Builder makeChildSurface = windowContainer.makeChildSurface(windowContainer.getTopChild());
        SurfaceControl build = makeChildSurface.setName("thumbnail anim: " + windowContainer.toString()).setBLASTLayer().setFormat(-3).setMetadata(2, windowContainer.getWindowingMode()).setMetadata(1, WindowManagerService.MY_UID).setCallsite("WindowContainerThumbnail").build();
        this.mSurfaceControl = build;
        if (ProtoLogCache.WM_SHOW_TRANSACTIONS_enabled) {
            ProtoLogImpl.i(ProtoLogGroup.WM_SHOW_TRANSACTIONS, 531242746, 0, (String) null, new Object[]{String.valueOf(build)});
        }
        transaction.setBuffer(this.mSurfaceControl, GraphicBuffer.createFromHardwareBuffer(hardwareBuffer));
        transaction.setColorSpace(this.mSurfaceControl, ColorSpace.get(ColorSpace.Named.SRGB));
        transaction.show(this.mSurfaceControl);
        transaction.setLayer(this.mSurfaceControl, Integer.MAX_VALUE);
    }

    public void startAnimation(SurfaceControl.Transaction transaction, Animation animation) {
        startAnimation(transaction, animation, null);
    }

    public void startAnimation(SurfaceControl.Transaction transaction, Animation animation, Point point) {
        animation.restrictDuration(10000L);
        animation.scaleCurrentDuration(this.mWindowContainer.mWmService.getTransitionAnimationScaleLocked());
        this.mSurfaceAnimator.startAnimation(transaction, new LocalAnimationAdapter(new WindowAnimationSpec(animation, point, this.mWindowContainer.getDisplayContent().mAppTransition.canSkipFirstFrame(), this.mWindowContainer.getDisplayContent().getWindowCornerRadius()), this.mWindowContainer.mWmService.mSurfaceAnimationRunner), false, 8);
    }

    public void setShowing(SurfaceControl.Transaction transaction, boolean z) {
        if (z) {
            transaction.show(this.mSurfaceControl);
        } else {
            transaction.hide(this.mSurfaceControl);
        }
    }

    public void destroy() {
        this.mSurfaceAnimator.cancelAnimation();
        getPendingTransaction().remove(this.mSurfaceControl);
        this.mSurfaceControl = null;
    }

    public void dumpDebug(ProtoOutputStream protoOutputStream, long j) {
        long start = protoOutputStream.start(j);
        protoOutputStream.write(1120986464257L, this.mWidth);
        protoOutputStream.write(1120986464258L, this.mHeight);
        if (this.mSurfaceAnimator.isAnimating()) {
            this.mSurfaceAnimator.dumpDebug(protoOutputStream, 1146756268035L);
        }
        protoOutputStream.end(start);
    }

    @Override // com.android.server.p014wm.SurfaceAnimator.Animatable
    public SurfaceControl.Transaction getSyncTransaction() {
        return this.mWindowContainer.getSyncTransaction();
    }

    public SurfaceControl.Transaction getPendingTransaction() {
        return this.mWindowContainer.getPendingTransaction();
    }

    @Override // com.android.server.p014wm.SurfaceAnimator.Animatable
    public void commitPendingTransaction() {
        this.mWindowContainer.commitPendingTransaction();
    }

    @Override // com.android.server.p014wm.SurfaceAnimator.Animatable
    public void onAnimationLeashCreated(SurfaceControl.Transaction transaction, SurfaceControl surfaceControl) {
        transaction.setLayer(surfaceControl, Integer.MAX_VALUE);
    }

    @Override // com.android.server.p014wm.SurfaceAnimator.Animatable
    public void onAnimationLeashLost(SurfaceControl.Transaction transaction) {
        transaction.hide(this.mSurfaceControl);
    }

    @Override // com.android.server.p014wm.SurfaceAnimator.Animatable
    public SurfaceControl.Builder makeAnimationLeash() {
        WindowContainer windowContainer = this.mWindowContainer;
        return windowContainer.makeChildSurface(windowContainer.getTopChild());
    }

    @Override // com.android.server.p014wm.SurfaceAnimator.Animatable
    public SurfaceControl getSurfaceControl() {
        return this.mSurfaceControl;
    }

    @Override // com.android.server.p014wm.SurfaceAnimator.Animatable
    public SurfaceControl getAnimationLeashParent() {
        return this.mWindowContainer.getAnimationLeashParent();
    }

    @Override // com.android.server.p014wm.SurfaceAnimator.Animatable
    public SurfaceControl getParentSurfaceControl() {
        return this.mWindowContainer.getParentSurfaceControl();
    }

    @Override // com.android.server.p014wm.SurfaceAnimator.Animatable
    public int getSurfaceWidth() {
        return this.mWidth;
    }

    @Override // com.android.server.p014wm.SurfaceAnimator.Animatable
    public int getSurfaceHeight() {
        return this.mHeight;
    }
}
