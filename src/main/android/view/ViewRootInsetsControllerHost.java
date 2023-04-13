package android.view;

import android.content.Context;
import android.content.res.CompatibilityInfo;
import android.graphics.HardwareRenderer;
import android.p008os.Handler;
import android.p008os.IBinder;
import android.p008os.RemoteException;
import android.util.Log;
import android.view.InsetsController;
import android.view.SurfaceControl;
import android.view.SyncRtSurfaceTransactionApplier;
import android.view.ViewTreeObserver;
import android.view.WindowInsetsAnimation;
import android.view.inputmethod.InputMethodManager;
import java.util.List;
/* loaded from: classes4.dex */
public class ViewRootInsetsControllerHost implements InsetsController.Host {
    private final String TAG = "VRInsetsControllerHost";
    private SyncRtSurfaceTransactionApplier mApplier;
    private final ViewRootImpl mViewRoot;

    public ViewRootInsetsControllerHost(ViewRootImpl viewRoot) {
        this.mViewRoot = viewRoot;
    }

    @Override // android.view.InsetsController.Host
    public Handler getHandler() {
        return this.mViewRoot.mHandler;
    }

    @Override // android.view.InsetsController.Host
    public void notifyInsetsChanged() {
        this.mViewRoot.notifyInsetsChanged();
    }

    @Override // android.view.InsetsController.Host
    public void addOnPreDrawRunnable(final Runnable r) {
        if (this.mViewRoot.mView == null) {
            return;
        }
        this.mViewRoot.mView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() { // from class: android.view.ViewRootInsetsControllerHost.1
            @Override // android.view.ViewTreeObserver.OnPreDrawListener
            public boolean onPreDraw() {
                ViewRootInsetsControllerHost.this.mViewRoot.mView.getViewTreeObserver().removeOnPreDrawListener(this);
                r.run();
                return true;
            }
        });
        this.mViewRoot.mView.invalidate();
    }

    @Override // android.view.InsetsController.Host
    public void dispatchWindowInsetsAnimationPrepare(WindowInsetsAnimation animation) {
        if (this.mViewRoot.mView == null) {
            return;
        }
        this.mViewRoot.mView.dispatchWindowInsetsAnimationPrepare(animation);
    }

    @Override // android.view.InsetsController.Host
    public WindowInsetsAnimation.Bounds dispatchWindowInsetsAnimationStart(WindowInsetsAnimation animation, WindowInsetsAnimation.Bounds bounds) {
        if (this.mViewRoot.mView == null) {
            return null;
        }
        return this.mViewRoot.mView.dispatchWindowInsetsAnimationStart(animation, bounds);
    }

    @Override // android.view.InsetsController.Host
    public WindowInsets dispatchWindowInsetsAnimationProgress(WindowInsets insets, List<WindowInsetsAnimation> runningAnimations) {
        if (this.mViewRoot.mView == null) {
            return null;
        }
        return this.mViewRoot.mView.dispatchWindowInsetsAnimationProgress(insets, runningAnimations);
    }

    @Override // android.view.InsetsController.Host
    public void dispatchWindowInsetsAnimationEnd(WindowInsetsAnimation animation) {
        if (this.mViewRoot.mView == null) {
            return;
        }
        this.mViewRoot.mView.dispatchWindowInsetsAnimationEnd(animation);
    }

    @Override // android.view.InsetsController.Host
    public void applySurfaceParams(SyncRtSurfaceTransactionApplier.SurfaceParams... params) {
        if (this.mViewRoot.mView == null) {
            throw new IllegalStateException("View of the ViewRootImpl is not initiated.");
        }
        if (this.mApplier == null) {
            this.mApplier = new SyncRtSurfaceTransactionApplier(this.mViewRoot.mView);
        }
        if (this.mViewRoot.mView.isHardwareAccelerated() && isVisibleToUser()) {
            this.mApplier.scheduleApply(params);
            return;
        }
        SurfaceControl.Transaction t = new SurfaceControl.Transaction();
        this.mApplier.applyParams(t, params);
        t.apply();
    }

    @Override // android.view.InsetsController.Host
    public void postInsetsAnimationCallback(Runnable r) {
        this.mViewRoot.mChoreographer.postCallback(2, r, null);
    }

    @Override // android.view.InsetsController.Host
    public void updateCompatSysUiVisibility(int visibleTypes, int requestedVisibleTypes, int controllableTypes) {
        this.mViewRoot.updateCompatSysUiVisibility(visibleTypes, requestedVisibleTypes, controllableTypes);
    }

    @Override // android.view.InsetsController.Host
    public void updateRequestedVisibleTypes(int types) {
        try {
            if (this.mViewRoot.mAdded) {
                this.mViewRoot.mWindowSession.updateRequestedVisibleTypes(this.mViewRoot.mWindow, types);
            }
        } catch (RemoteException e) {
            Log.m109e("VRInsetsControllerHost", "Failed to call insetsModified", e);
        }
    }

    @Override // android.view.InsetsController.Host
    public boolean hasAnimationCallbacks() {
        if (this.mViewRoot.mView == null) {
            return false;
        }
        return this.mViewRoot.mView.hasWindowInsetsAnimationCallback();
    }

    @Override // android.view.InsetsController.Host
    public void setSystemBarsAppearance(int appearance, int mask) {
        this.mViewRoot.mWindowAttributes.privateFlags |= 67108864;
        InsetsFlags insetsFlags = this.mViewRoot.mWindowAttributes.insetsFlags;
        int newAppearance = (insetsFlags.appearance & (~mask)) | (appearance & mask);
        if (insetsFlags.appearance != newAppearance) {
            insetsFlags.appearance = newAppearance;
            this.mViewRoot.mWindowAttributesChanged = true;
            this.mViewRoot.scheduleTraversals();
        }
    }

    @Override // android.view.InsetsController.Host
    public int getSystemBarsAppearance() {
        return this.mViewRoot.mWindowAttributes.insetsFlags.appearance;
    }

    @Override // android.view.InsetsController.Host
    public boolean isSystemBarsAppearanceControlled() {
        return (this.mViewRoot.mWindowAttributes.privateFlags & 67108864) != 0;
    }

    @Override // android.view.InsetsController.Host
    public void setSystemBarsBehavior(int behavior) {
        this.mViewRoot.mWindowAttributes.privateFlags |= 134217728;
        if (this.mViewRoot.mWindowAttributes.insetsFlags.behavior != behavior) {
            this.mViewRoot.mWindowAttributes.insetsFlags.behavior = behavior;
            this.mViewRoot.mWindowAttributesChanged = true;
            this.mViewRoot.scheduleTraversals();
        }
    }

    @Override // android.view.InsetsController.Host
    public int getSystemBarsBehavior() {
        return this.mViewRoot.mWindowAttributes.insetsFlags.behavior;
    }

    @Override // android.view.InsetsController.Host
    public boolean isSystemBarsBehaviorControlled() {
        return (this.mViewRoot.mWindowAttributes.privateFlags & 134217728) != 0;
    }

    @Override // android.view.InsetsController.Host
    public void releaseSurfaceControlFromRt(final SurfaceControl surfaceControl) {
        if (this.mViewRoot.mView != null && this.mViewRoot.mView.isHardwareAccelerated()) {
            this.mViewRoot.registerRtFrameCallback(new HardwareRenderer.FrameDrawingCallback() { // from class: android.view.ViewRootInsetsControllerHost$$ExternalSyntheticLambda0
                @Override // android.graphics.HardwareRenderer.FrameDrawingCallback
                public final void onFrameDraw(long j) {
                    SurfaceControl.this.release();
                }
            });
            this.mViewRoot.mView.invalidate();
            return;
        }
        surfaceControl.release();
    }

    @Override // android.view.InsetsController.Host
    public InputMethodManager getInputMethodManager() {
        return (InputMethodManager) this.mViewRoot.mContext.getSystemService(InputMethodManager.class);
    }

    @Override // android.view.InsetsController.Host
    public String getRootViewTitle() {
        ViewRootImpl viewRootImpl = this.mViewRoot;
        if (viewRootImpl == null) {
            return null;
        }
        return viewRootImpl.getTitle().toString();
    }

    @Override // android.view.InsetsController.Host
    public Context getRootViewContext() {
        ViewRootImpl viewRootImpl = this.mViewRoot;
        if (viewRootImpl != null) {
            return viewRootImpl.mContext;
        }
        return null;
    }

    @Override // android.view.InsetsController.Host
    public int dipToPx(int dips) {
        ViewRootImpl viewRootImpl = this.mViewRoot;
        if (viewRootImpl != null) {
            return viewRootImpl.dipToPx(dips);
        }
        return 0;
    }

    @Override // android.view.InsetsController.Host
    public IBinder getWindowToken() {
        View view;
        ViewRootImpl viewRootImpl = this.mViewRoot;
        if (viewRootImpl == null || (view = viewRootImpl.getView()) == null) {
            return null;
        }
        return view.getWindowToken();
    }

    @Override // android.view.InsetsController.Host
    public CompatibilityInfo.Translator getTranslator() {
        ViewRootImpl viewRootImpl = this.mViewRoot;
        if (viewRootImpl != null) {
            return viewRootImpl.mTranslator;
        }
        return null;
    }

    private boolean isVisibleToUser() {
        return this.mViewRoot.getHostVisibility() == 0;
    }
}
