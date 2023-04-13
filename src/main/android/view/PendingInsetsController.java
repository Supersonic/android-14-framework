package android.view;

import android.p008os.CancellationSignal;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.view.animation.Interpolator;
import java.util.ArrayList;
/* loaded from: classes4.dex */
public class PendingInsetsController implements WindowInsetsController {
    private static final int KEEP_BEHAVIOR = -1;
    private boolean mAnimationsDisabled;
    private int mAppearance;
    private int mAppearanceMask;
    private WindowInsetsAnimationControlListener mLoggingListener;
    private InsetsController mReplayedInsetsController;
    private final ArrayList<PendingRequest> mRequests = new ArrayList<>();
    private int mBehavior = -1;
    private final InsetsState mDummyState = new InsetsState();
    private ArrayList<WindowInsetsController.OnControllableInsetsChangedListener> mControllableInsetsChangedListeners = new ArrayList<>();
    private int mCaptionInsetsHeight = 0;
    private int mRequestedVisibleTypes = WindowInsets.Type.defaultVisible();

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public interface PendingRequest {
        void replay(InsetsController insetsController);
    }

    @Override // android.view.WindowInsetsController
    public void show(int types) {
        InsetsController insetsController = this.mReplayedInsetsController;
        if (insetsController != null) {
            insetsController.show(types);
            return;
        }
        this.mRequests.add(new ShowRequest(types));
        this.mRequestedVisibleTypes |= types;
    }

    @Override // android.view.WindowInsetsController
    public void hide(int types) {
        InsetsController insetsController = this.mReplayedInsetsController;
        if (insetsController != null) {
            insetsController.hide(types);
            return;
        }
        this.mRequests.add(new HideRequest(types));
        this.mRequestedVisibleTypes &= ~types;
    }

    @Override // android.view.WindowInsetsController
    public void setSystemBarsAppearance(int appearance, int mask) {
        InsetsController insetsController = this.mReplayedInsetsController;
        if (insetsController != null) {
            insetsController.setSystemBarsAppearance(appearance, mask);
            return;
        }
        this.mAppearance = (this.mAppearance & (~mask)) | (appearance & mask);
        this.mAppearanceMask |= mask;
    }

    @Override // android.view.WindowInsetsController
    public int getSystemBarsAppearance() {
        InsetsController insetsController = this.mReplayedInsetsController;
        if (insetsController != null) {
            return insetsController.getSystemBarsAppearance();
        }
        return this.mAppearance;
    }

    @Override // android.view.WindowInsetsController
    public void setCaptionInsetsHeight(int height) {
        this.mCaptionInsetsHeight = height;
    }

    @Override // android.view.WindowInsetsController
    public void setSystemBarsBehavior(int behavior) {
        InsetsController insetsController = this.mReplayedInsetsController;
        if (insetsController != null) {
            insetsController.setSystemBarsBehavior(behavior);
        } else {
            this.mBehavior = behavior;
        }
    }

    @Override // android.view.WindowInsetsController
    public int getSystemBarsBehavior() {
        InsetsController insetsController = this.mReplayedInsetsController;
        if (insetsController != null) {
            return insetsController.getSystemBarsBehavior();
        }
        int i = this.mBehavior;
        if (i == -1) {
            return 1;
        }
        return i;
    }

    @Override // android.view.WindowInsetsController
    public void setAnimationsDisabled(boolean disable) {
        InsetsController insetsController = this.mReplayedInsetsController;
        if (insetsController != null) {
            insetsController.setAnimationsDisabled(disable);
        } else {
            this.mAnimationsDisabled = disable;
        }
    }

    @Override // android.view.WindowInsetsController
    public InsetsState getState() {
        return this.mDummyState;
    }

    @Override // android.view.WindowInsetsController
    public int getRequestedVisibleTypes() {
        InsetsController insetsController = this.mReplayedInsetsController;
        if (insetsController != null) {
            return insetsController.getRequestedVisibleTypes();
        }
        return this.mRequestedVisibleTypes;
    }

    @Override // android.view.WindowInsetsController
    public void addOnControllableInsetsChangedListener(WindowInsetsController.OnControllableInsetsChangedListener listener) {
        InsetsController insetsController = this.mReplayedInsetsController;
        if (insetsController != null) {
            insetsController.addOnControllableInsetsChangedListener(listener);
            return;
        }
        this.mControllableInsetsChangedListeners.add(listener);
        listener.onControllableInsetsChanged(this, 0);
    }

    @Override // android.view.WindowInsetsController
    public void removeOnControllableInsetsChangedListener(WindowInsetsController.OnControllableInsetsChangedListener listener) {
        InsetsController insetsController = this.mReplayedInsetsController;
        if (insetsController != null) {
            insetsController.removeOnControllableInsetsChangedListener(listener);
        } else {
            this.mControllableInsetsChangedListeners.remove(listener);
        }
    }

    public void replayAndAttach(InsetsController controller) {
        int i = this.mBehavior;
        if (i != -1) {
            controller.setSystemBarsBehavior(i);
        }
        int i2 = this.mAppearanceMask;
        if (i2 != 0) {
            controller.setSystemBarsAppearance(this.mAppearance, i2);
        }
        int i3 = this.mCaptionInsetsHeight;
        if (i3 != 0) {
            controller.setCaptionInsetsHeight(i3);
        }
        if (this.mAnimationsDisabled) {
            controller.setAnimationsDisabled(true);
        }
        int size = this.mRequests.size();
        for (int i4 = 0; i4 < size; i4++) {
            this.mRequests.get(i4).replay(controller);
        }
        int size2 = this.mControllableInsetsChangedListeners.size();
        for (int i5 = 0; i5 < size2; i5++) {
            controller.addOnControllableInsetsChangedListener(this.mControllableInsetsChangedListeners.get(i5));
        }
        WindowInsetsAnimationControlListener windowInsetsAnimationControlListener = this.mLoggingListener;
        if (windowInsetsAnimationControlListener != null) {
            controller.setSystemDrivenInsetsAnimationLoggingListener(windowInsetsAnimationControlListener);
        }
        this.mRequests.clear();
        this.mControllableInsetsChangedListeners.clear();
        this.mBehavior = -1;
        this.mAppearance = 0;
        this.mAppearanceMask = 0;
        this.mAnimationsDisabled = false;
        this.mLoggingListener = null;
        this.mRequestedVisibleTypes = WindowInsets.Type.defaultVisible();
        this.mReplayedInsetsController = controller;
    }

    public void detach() {
        this.mReplayedInsetsController = null;
    }

    @Override // android.view.WindowInsetsController
    public void setSystemDrivenInsetsAnimationLoggingListener(WindowInsetsAnimationControlListener listener) {
        InsetsController insetsController = this.mReplayedInsetsController;
        if (insetsController != null) {
            insetsController.setSystemDrivenInsetsAnimationLoggingListener(listener);
        } else {
            this.mLoggingListener = listener;
        }
    }

    @Override // android.view.WindowInsetsController
    public void controlWindowInsetsAnimation(int types, long durationMillis, Interpolator interpolator, CancellationSignal cancellationSignal, WindowInsetsAnimationControlListener listener) {
        InsetsController insetsController = this.mReplayedInsetsController;
        if (insetsController != null) {
            insetsController.controlWindowInsetsAnimation(types, durationMillis, interpolator, cancellationSignal, listener);
        } else {
            listener.onCancelled(null);
        }
    }

    /* loaded from: classes4.dex */
    private static class ShowRequest implements PendingRequest {
        private final int mTypes;

        public ShowRequest(int types) {
            this.mTypes = types;
        }

        @Override // android.view.PendingInsetsController.PendingRequest
        public void replay(InsetsController controller) {
            controller.show(this.mTypes);
        }
    }

    /* loaded from: classes4.dex */
    private static class HideRequest implements PendingRequest {
        private final int mTypes;

        public HideRequest(int types) {
            this.mTypes = types;
        }

        @Override // android.view.PendingInsetsController.PendingRequest
        public void replay(InsetsController controller) {
            controller.hide(this.mTypes);
        }
    }
}
