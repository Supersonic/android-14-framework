package android.service.autofill.augmented;

import android.annotation.SystemApi;
import android.graphics.Rect;
import android.p008os.Handler;
import android.p008os.Looper;
import android.p008os.RemoteException;
import android.service.autofill.augmented.AugmentedAutofillService;
import android.service.autofill.augmented.PresentationParams;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.autofill.IAutofillWindowPresenter;
import com.android.internal.util.function.pooled.PooledLambda;
import dalvik.system.CloseGuard;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
@SystemApi
/* loaded from: classes3.dex */
public final class FillWindow implements AutoCloseable {
    private static final String TAG = FillWindow.class.getSimpleName();
    private Rect mBounds;
    private boolean mDestroyed;
    private View mFillView;
    private AugmentedAutofillService.AutofillProxy mProxy;
    private boolean mShowing;
    private boolean mUpdateCalled;
    private WindowManager mWm;
    private final Object mLock = new Object();
    private final CloseGuard mCloseGuard = CloseGuard.get();
    private final Handler mUiThreadHandler = new Handler(Looper.getMainLooper());

    public boolean update(PresentationParams.Area area, View rootView, long flags) {
        if (AugmentedAutofillService.sDebug) {
            Log.m112d(TAG, "Updating " + area + " + with " + rootView);
        }
        Objects.requireNonNull(area);
        Objects.requireNonNull(area.proxy);
        Objects.requireNonNull(rootView);
        PresentationParams smartSuggestion = area.proxy.getSmartSuggestionParams();
        if (smartSuggestion == null) {
            Log.m104w(TAG, "No SmartSuggestionParams");
            return false;
        }
        Rect rect = area.getBounds();
        if (rect == null) {
            Log.wtf(TAG, "No Rect on SmartSuggestionParams");
            return false;
        }
        synchronized (this.mLock) {
            checkNotDestroyedLocked();
            this.mProxy = area.proxy;
            this.mWm = (WindowManager) rootView.getContext().getSystemService(WindowManager.class);
            this.mFillView = rootView;
            rootView.setOnTouchListener(new View.OnTouchListener() { // from class: android.service.autofill.augmented.FillWindow$$ExternalSyntheticLambda0
                @Override // android.view.View.OnTouchListener
                public final boolean onTouch(View view, MotionEvent motionEvent) {
                    boolean lambda$update$0;
                    lambda$update$0 = FillWindow.this.lambda$update$0(view, motionEvent);
                    return lambda$update$0;
                }
            });
            this.mShowing = false;
            this.mBounds = new Rect(area.getBounds());
            if (AugmentedAutofillService.sDebug) {
                Log.m112d(TAG, "Created FillWindow: params= " + smartSuggestion + " view=" + rootView);
            }
            this.mUpdateCalled = true;
            this.mDestroyed = false;
            this.mProxy.setFillWindow(this);
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$update$0(View view, MotionEvent motionEvent) {
        if (motionEvent.getAction() == 4) {
            if (AugmentedAutofillService.sVerbose) {
                Log.m106v(TAG, "Outside touch detected, hiding the window");
            }
            hide();
            return false;
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void show() {
        if (AugmentedAutofillService.sDebug) {
            Log.m112d(TAG, "show()");
        }
        synchronized (this.mLock) {
            checkNotDestroyedLocked();
            if (this.mWm == null || this.mFillView == null) {
                throw new IllegalStateException("update() not called yet, or already destroyed()");
            }
            AugmentedAutofillService.AutofillProxy autofillProxy = this.mProxy;
            if (autofillProxy != null) {
                try {
                    autofillProxy.requestShowFillUi(this.mBounds.right - this.mBounds.left, this.mBounds.bottom - this.mBounds.top, null, new FillWindowPresenter(this));
                } catch (RemoteException e) {
                    Log.m103w(TAG, "Error requesting to show fill window", e);
                }
                this.mProxy.logEvent(2);
            }
        }
    }

    private void hide() {
        if (AugmentedAutofillService.sDebug) {
            Log.m112d(TAG, "hide()");
        }
        synchronized (this.mLock) {
            checkNotDestroyedLocked();
            if (this.mWm == null || this.mFillView == null) {
                throw new IllegalStateException("update() not called yet, or already destroyed()");
            }
            AugmentedAutofillService.AutofillProxy autofillProxy = this.mProxy;
            if (autofillProxy != null && this.mShowing) {
                try {
                    autofillProxy.requestHideFillUi();
                } catch (RemoteException e) {
                    Log.m103w(TAG, "Error requesting to hide fill window", e);
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleShow(WindowManager.LayoutParams p) {
        if (AugmentedAutofillService.sDebug) {
            Log.m112d(TAG, "handleShow()");
        }
        synchronized (this.mLock) {
            if (this.mWm != null && this.mFillView != null) {
                try {
                    p.flags |= 262144;
                    if (!this.mShowing) {
                        this.mWm.addView(this.mFillView, p);
                        this.mShowing = true;
                    } else {
                        this.mWm.updateViewLayout(this.mFillView, p);
                    }
                } catch (WindowManager.BadTokenException e) {
                    if (AugmentedAutofillService.sDebug) {
                        Log.m112d(TAG, "Filed with token " + p.token + " gone.");
                    }
                } catch (IllegalStateException e2) {
                    if (AugmentedAutofillService.sDebug) {
                        Log.m112d(TAG, "Exception showing window.");
                    }
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleHide() {
        View view;
        if (AugmentedAutofillService.sDebug) {
            Log.m112d(TAG, "handleHide()");
        }
        synchronized (this.mLock) {
            WindowManager windowManager = this.mWm;
            if (windowManager != null && (view = this.mFillView) != null && this.mShowing) {
                try {
                    windowManager.removeView(view);
                    this.mShowing = false;
                } catch (IllegalStateException e) {
                    if (AugmentedAutofillService.sDebug) {
                        Log.m112d(TAG, "Exception hiding window.");
                    }
                }
            }
        }
    }

    public void destroy() {
        if (AugmentedAutofillService.sDebug) {
            Log.m112d(TAG, "destroy(): mDestroyed=" + this.mDestroyed + " mShowing=" + this.mShowing + " mFillView=" + this.mFillView);
        }
        synchronized (this.mLock) {
            if (this.mDestroyed) {
                return;
            }
            if (this.mUpdateCalled) {
                this.mFillView.setOnClickListener(null);
                hide();
                this.mProxy.logEvent(3);
            }
            this.mDestroyed = true;
            this.mCloseGuard.close();
        }
    }

    protected void finalize() throws Throwable {
        try {
            this.mCloseGuard.warnIfOpen();
            destroy();
        } finally {
            super.finalize();
        }
    }

    private void checkNotDestroyedLocked() {
        if (this.mDestroyed) {
            throw new IllegalStateException("already destroyed()");
        }
    }

    public void dump(String prefix, PrintWriter pw) {
        synchronized (this) {
            pw.print(prefix);
            pw.print("destroyed: ");
            pw.println(this.mDestroyed);
            pw.print(prefix);
            pw.print("updateCalled: ");
            pw.println(this.mUpdateCalled);
            if (this.mFillView != null) {
                pw.print(prefix);
                pw.print("fill window: ");
                pw.println(this.mShowing ? "shown" : "hidden");
                pw.print(prefix);
                pw.print("fill view: ");
                pw.println(this.mFillView);
                pw.print(prefix);
                pw.print("mBounds: ");
                pw.println(this.mBounds);
                pw.print(prefix);
                pw.print("mWm: ");
                pw.println(this.mWm);
            }
        }
    }

    @Override // java.lang.AutoCloseable
    public void close() {
        destroy();
    }

    /* loaded from: classes3.dex */
    private static final class FillWindowPresenter extends IAutofillWindowPresenter.Stub {
        private final WeakReference<FillWindow> mFillWindowReference;

        FillWindowPresenter(FillWindow fillWindow) {
            this.mFillWindowReference = new WeakReference<>(fillWindow);
        }

        @Override // android.view.autofill.IAutofillWindowPresenter
        public void show(WindowManager.LayoutParams p, Rect transitionEpicenter, boolean fitsSystemWindows, int layoutDirection) {
            if (AugmentedAutofillService.sDebug) {
                Log.m112d(FillWindow.TAG, "FillWindowPresenter.show()");
            }
            FillWindow fillWindow = this.mFillWindowReference.get();
            if (fillWindow != null) {
                fillWindow.mUiThreadHandler.sendMessage(PooledLambda.obtainMessage(new BiConsumer() { // from class: android.service.autofill.augmented.FillWindow$FillWindowPresenter$$ExternalSyntheticLambda0
                    @Override // java.util.function.BiConsumer
                    public final void accept(Object obj, Object obj2) {
                        ((FillWindow) obj).handleShow((WindowManager.LayoutParams) obj2);
                    }
                }, fillWindow, p));
            }
        }

        @Override // android.view.autofill.IAutofillWindowPresenter
        public void hide(Rect transitionEpicenter) {
            if (AugmentedAutofillService.sDebug) {
                Log.m112d(FillWindow.TAG, "FillWindowPresenter.hide()");
            }
            FillWindow fillWindow = this.mFillWindowReference.get();
            if (fillWindow != null) {
                fillWindow.mUiThreadHandler.sendMessage(PooledLambda.obtainMessage(new Consumer() { // from class: android.service.autofill.augmented.FillWindow$FillWindowPresenter$$ExternalSyntheticLambda1
                    @Override // java.util.function.Consumer
                    public final void accept(Object obj) {
                        ((FillWindow) obj).handleHide();
                    }
                }, fillWindow));
            }
        }
    }
}
