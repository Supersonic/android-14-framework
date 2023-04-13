package android.view;

import android.graphics.Rect;
import android.util.proto.ProtoOutputStream;
import android.view.SurfaceControl;
import android.view.WindowInsets;
import android.view.inputmethod.ImeTracker;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Objects;
import java.util.function.Supplier;
/* loaded from: classes4.dex */
public class InsetsSourceConsumer {
    protected static final int ANIMATION_STATE_HIDE = 2;
    protected static final int ANIMATION_STATE_NONE = 0;
    protected static final int ANIMATION_STATE_SHOW = 1;
    private static final String TAG = "InsetsSourceConsumer";
    protected int mAnimationState = 0;
    protected final InsetsController mController;
    private boolean mHasViewFocusWhenWindowFocusGain;
    private boolean mHasWindowFocus;
    private int mId;
    private Rect mPendingFrame;
    private Rect mPendingVisibleFrame;
    private InsetsSourceControl mSourceControl;
    protected final InsetsState mState;
    private final Supplier<SurfaceControl.Transaction> mTransactionSupplier;
    private final int mType;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes4.dex */
    @interface ShowResult {
        public static final int IME_SHOW_DELAYED = 1;
        public static final int IME_SHOW_FAILED = 2;
        public static final int SHOW_IMMEDIATELY = 0;
    }

    public InsetsSourceConsumer(int id, int type, InsetsState state, Supplier<SurfaceControl.Transaction> transactionSupplier, InsetsController controller) {
        this.mId = id;
        this.mType = type;
        this.mState = state;
        this.mTransactionSupplier = transactionSupplier;
        this.mController = controller;
    }

    public boolean setControl(InsetsSourceControl control, int[] showTypes, int[] hideTypes) {
        if (Objects.equals(this.mSourceControl, control)) {
            InsetsSourceControl insetsSourceControl = this.mSourceControl;
            if (insetsSourceControl != null && insetsSourceControl != control) {
                insetsSourceControl.release(new InsetsAnimationThreadControlRunner$$ExternalSyntheticLambda0());
                this.mSourceControl = control;
            }
            return false;
        }
        InsetsSourceControl lastControl = this.mSourceControl;
        this.mSourceControl = control;
        if (control == null) {
            this.mController.notifyControlRevoked(this);
            InsetsSource localSource = this.mState.peekSource(this.mId);
            InsetsSource serverSource = this.mController.getLastDispatchedState().peekSource(this.mId);
            if (localSource != null && serverSource != null && localSource.isVisible() != serverSource.isVisible()) {
                localSource.setVisible(serverSource.isVisible());
                this.mController.notifyVisibilityChanged();
            }
        } else {
            boolean requestedVisible = isRequestedVisibleAwaitingControl();
            SurfaceControl oldLeash = lastControl != null ? lastControl.getLeash() : null;
            SurfaceControl newLeash = control.getLeash();
            if (newLeash != null && ((oldLeash == null || !newLeash.isSameSurface(oldLeash)) && requestedVisible != control.isInitiallyVisible())) {
                if (requestedVisible) {
                    showTypes[0] = showTypes[0] | this.mType;
                } else {
                    hideTypes[0] = hideTypes[0] | this.mType;
                }
            } else {
                if (applyLocalVisibilityOverride()) {
                    this.mController.notifyVisibilityChanged();
                }
                if (this.mController.getAnimationType(this.mType) == -1) {
                    applyRequestedVisibilityToControl();
                }
                if (!requestedVisible && lastControl == null) {
                    removeSurface();
                }
            }
        }
        if (lastControl != null) {
            lastControl.release(new InsetsAnimationThreadControlRunner$$ExternalSyntheticLambda0());
            return true;
        }
        return true;
    }

    public InsetsSourceControl getControl() {
        return this.mSourceControl;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean isRequestedVisibleAwaitingControl() {
        return (this.mController.getRequestedVisibleTypes() & this.mType) != 0;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getId() {
        return this.mId;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setId(int id) {
        this.mId = id;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getType() {
        return this.mType;
    }

    public boolean onAnimationStateChanged(boolean running) {
        boolean cancelledForNewAnimation;
        boolean insetsChanged = false;
        if (!running && this.mPendingFrame != null) {
            InsetsSource source = this.mState.peekSource(this.mId);
            if (source != null) {
                source.setFrame(this.mPendingFrame);
                source.setVisibleFrame(this.mPendingVisibleFrame);
                insetsChanged = true;
            }
            this.mPendingFrame = null;
            this.mPendingVisibleFrame = null;
        }
        boolean showRequested = isShowRequested();
        int i = 2;
        if (!running && showRequested) {
            cancelledForNewAnimation = this.mAnimationState == 2;
        } else {
            cancelledForNewAnimation = this.mAnimationState == 1;
        }
        if (running) {
            if (showRequested) {
                i = 1;
            }
        } else {
            i = 0;
        }
        this.mAnimationState = i;
        if (!cancelledForNewAnimation) {
            return insetsChanged | applyLocalVisibilityOverride();
        }
        return insetsChanged;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean isShowRequested() {
        return (this.mController.getRequestedVisibleTypes() & getType()) != 0;
    }

    public void onWindowFocusGained(boolean hasViewFocus) {
        this.mHasWindowFocus = true;
        this.mHasViewFocusWhenWindowFocusGain = hasViewFocus;
    }

    public void onWindowFocusLost() {
        this.mHasWindowFocus = false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean hasViewFocusWhenWindowFocusGain() {
        return this.mHasViewFocusWhenWindowFocusGain;
    }

    public boolean applyLocalVisibilityOverride() {
        InsetsSource source = this.mState.peekSource(this.mId);
        if (source == null) {
            return false;
        }
        boolean requestedVisible = (this.mController.getRequestedVisibleTypes() & this.mType) != 0;
        if (this.mSourceControl == null || source.isVisible() == requestedVisible) {
            return false;
        }
        source.setVisible(requestedVisible);
        return true;
    }

    public int requestShow(boolean fromController, ImeTracker.Token statsToken) {
        return 0;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void requestHide(boolean fromController, ImeTracker.Token statsToken) {
    }

    public void onPerceptible(boolean perceptible) {
    }

    public void removeSurface() {
    }

    public void updateSource(InsetsSource newSource, int animationType) {
        InsetsSource source = this.mState.peekSource(this.mId);
        Rect rect = null;
        if (source == null || animationType == -1 || source.getFrame().equals(newSource.getFrame())) {
            this.mPendingFrame = null;
            this.mPendingVisibleFrame = null;
            this.mState.addSource(newSource);
            return;
        }
        InsetsSource newSource2 = new InsetsSource(newSource);
        this.mPendingFrame = new Rect(newSource2.getFrame());
        if (newSource2.getVisibleFrame() != null) {
            rect = new Rect(newSource2.getVisibleFrame());
        }
        this.mPendingVisibleFrame = rect;
        newSource2.setFrame(source.getFrame());
        newSource2.setVisibleFrame(source.getVisibleFrame());
        this.mState.addSource(newSource2);
    }

    private void applyRequestedVisibilityToControl() {
        InsetsSourceControl insetsSourceControl = this.mSourceControl;
        if (insetsSourceControl == null || insetsSourceControl.getLeash() == null) {
            return;
        }
        boolean requestedVisible = (this.mController.getRequestedVisibleTypes() & this.mType) != 0;
        SurfaceControl.Transaction t = this.mTransactionSupplier.get();
        try {
            if (requestedVisible) {
                t.show(this.mSourceControl.getLeash());
            } else {
                t.hide(this.mSourceControl.getLeash());
            }
            t.setAlpha(this.mSourceControl.getLeash(), requestedVisible ? 1.0f : 0.0f);
            t.apply();
            if (t != null) {
                t.close();
            }
            onPerceptible(requestedVisible);
        } catch (Throwable th) {
            if (t != null) {
                try {
                    t.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
            }
            throw th;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void dumpDebug(ProtoOutputStream proto, long fieldId) {
        long token = proto.start(fieldId);
        proto.write(1138166333441L, WindowInsets.Type.toString(this.mType));
        proto.write(1133871366146L, this.mHasWindowFocus);
        proto.write(1133871366147L, isShowRequested());
        InsetsSourceControl insetsSourceControl = this.mSourceControl;
        if (insetsSourceControl != null) {
            insetsSourceControl.dumpDebug(proto, 1146756268036L);
        }
        Rect rect = this.mPendingFrame;
        if (rect != null) {
            rect.dumpDebug(proto, 1146756268037L);
        }
        Rect rect2 = this.mPendingVisibleFrame;
        if (rect2 != null) {
            rect2.dumpDebug(proto, 1146756268038L);
        }
        proto.write(1120986464263L, this.mAnimationState);
        proto.end(token);
    }
}
