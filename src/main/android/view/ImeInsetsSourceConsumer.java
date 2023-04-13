package android.view;

import android.p008os.IBinder;
import android.p008os.Process;
import android.p008os.Trace;
import android.util.proto.ProtoOutputStream;
import android.view.SurfaceControl;
import android.view.WindowInsets;
import android.view.inputmethod.ImeTracker;
import android.view.inputmethod.InputMethodManager;
import com.android.internal.inputmethod.ImeTracing;
import java.util.function.Supplier;
/* loaded from: classes4.dex */
public final class ImeInsetsSourceConsumer extends InsetsSourceConsumer {
    private boolean mHasPendingRequest;
    private boolean mIsRequestedVisibleAwaitingControl;

    public ImeInsetsSourceConsumer(int id, InsetsState state, Supplier<SurfaceControl.Transaction> transactionSupplier, InsetsController controller) {
        super(id, WindowInsets.Type.ime(), state, transactionSupplier, controller);
    }

    @Override // android.view.InsetsSourceConsumer
    public boolean onAnimationStateChanged(boolean running) {
        if (!running) {
            ImeTracing.getInstance().triggerClientDump("ImeInsetsSourceConsumer#onAnimationFinished", this.mController.getHost().getInputMethodManager(), null);
        }
        boolean insetsChanged = super.onAnimationStateChanged(running);
        if (!isShowRequested()) {
            this.mIsRequestedVisibleAwaitingControl = false;
            if (!running && !this.mHasPendingRequest) {
                notifyHidden(null);
                removeSurface();
            }
        }
        this.mHasPendingRequest = false;
        return insetsChanged;
    }

    @Override // android.view.InsetsSourceConsumer
    public void onWindowFocusGained(boolean hasViewFocus) {
        super.onWindowFocusGained(hasViewFocus);
        getImm().registerImeConsumer(this);
        if ((this.mController.getRequestedVisibleTypes() & getType()) != 0 && getControl() == null) {
            this.mIsRequestedVisibleAwaitingControl = true;
        }
    }

    @Override // android.view.InsetsSourceConsumer
    public void onWindowFocusLost() {
        super.onWindowFocusLost();
        getImm().unregisterImeConsumer(this);
        this.mIsRequestedVisibleAwaitingControl = false;
    }

    @Override // android.view.InsetsSourceConsumer
    public boolean applyLocalVisibilityOverride() {
        ImeTracing.getInstance().triggerClientDump("ImeInsetsSourceConsumer#applyLocalVisibilityOverride", this.mController.getHost().getInputMethodManager(), null);
        return super.applyLocalVisibilityOverride();
    }

    @Override // android.view.InsetsSourceConsumer
    public int requestShow(boolean fromIme, ImeTracker.Token statsToken) {
        if (fromIme) {
            ImeTracing.getInstance().triggerClientDump("ImeInsetsSourceConsumer#requestShow", this.mController.getHost().getInputMethodManager(), null);
        }
        onShowRequested();
        ImeTracker.forLogging().onProgress(statsToken, 36);
        if (getControl() == null) {
            this.mIsRequestedVisibleAwaitingControl = true;
        }
        if (!fromIme) {
            if (!this.mState.isSourceOrDefaultVisible(getId(), getType()) || getControl() == null) {
                return getImm().requestImeShow(this.mController.getHost().getWindowToken(), statsToken) ? 1 : 2;
            }
            return 0;
        }
        return 0;
    }

    @Override // android.view.InsetsSourceConsumer
    void requestHide(boolean fromIme, ImeTracker.Token statsToken) {
        if (!fromIme) {
            notifyHidden(statsToken);
        }
        if (this.mAnimationState == 1) {
            this.mHasPendingRequest = true;
        }
    }

    private void notifyHidden(ImeTracker.Token statsToken) {
        if (statsToken == null || getControl() != null) {
            statsToken = ImeTracker.forLogging().onRequestHide(null, Process.myUid(), 2, 28);
        }
        ImeTracker.forLogging().onProgress(statsToken, 38);
        getImm().notifyImeHidden(this.mController.getHost().getWindowToken(), statsToken);
        this.mIsRequestedVisibleAwaitingControl = false;
        Trace.asyncTraceEnd(8L, "IC.hideRequestFromApi", 0);
    }

    @Override // android.view.InsetsSourceConsumer
    public void removeSurface() {
        IBinder window = this.mController.getHost().getWindowToken();
        if (window != null) {
            getImm().removeImeSurface(window);
        }
    }

    @Override // android.view.InsetsSourceConsumer
    public boolean setControl(InsetsSourceControl control, int[] showTypes, int[] hideTypes) {
        ImeTracing.getInstance().triggerClientDump("ImeInsetsSourceConsumer#setControl", this.mController.getHost().getInputMethodManager(), null);
        if (super.setControl(control, showTypes, hideTypes)) {
            if (control == null && !this.mIsRequestedVisibleAwaitingControl) {
                this.mController.setRequestedVisibleTypes(0, getType());
                removeSurface();
            }
            if (control != null) {
                this.mIsRequestedVisibleAwaitingControl = false;
                return true;
            }
            return true;
        }
        return false;
    }

    @Override // android.view.InsetsSourceConsumer
    protected boolean isRequestedVisibleAwaitingControl() {
        return super.isRequestedVisibleAwaitingControl() || this.mIsRequestedVisibleAwaitingControl;
    }

    @Override // android.view.InsetsSourceConsumer
    public void onPerceptible(boolean perceptible) {
        super.onPerceptible(perceptible);
        IBinder window = this.mController.getHost().getWindowToken();
        if (window != null) {
            getImm().reportPerceptible(window, perceptible);
        }
    }

    @Override // android.view.InsetsSourceConsumer
    public void dumpDebug(ProtoOutputStream proto, long fieldId) {
        long token = proto.start(fieldId);
        super.dumpDebug(proto, 1146756268033L);
        proto.write(1133871366147L, this.mIsRequestedVisibleAwaitingControl);
        proto.write(1133871366150L, this.mHasPendingRequest);
        proto.end(token);
    }

    public void onShowRequested() {
        if (this.mAnimationState == 2) {
            this.mHasPendingRequest = true;
        }
    }

    private InputMethodManager getImm() {
        return this.mController.getHost().getInputMethodManager();
    }
}
