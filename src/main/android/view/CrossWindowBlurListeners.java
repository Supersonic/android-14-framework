package android.view;

import android.p008os.Binder;
import android.p008os.Handler;
import android.p008os.Looper;
import android.p008os.RemoteException;
import android.p008os.SystemProperties;
import android.util.ArrayMap;
import android.util.Log;
import android.view.ICrossWindowBlurEnabledListener;
import com.android.internal.util.Preconditions;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
/* loaded from: classes4.dex */
public final class CrossWindowBlurListeners {
    private static final String TAG = "CrossWindowBlurListeners";
    private static volatile CrossWindowBlurListeners sInstance;
    private boolean mCrossWindowBlurEnabled;
    private static final String BLUR_PROPERTY = "ro.surface_flinger.supports_background_blur";
    public static final boolean CROSS_WINDOW_BLUR_SUPPORTED = SystemProperties.getBoolean(BLUR_PROPERTY, false);
    private static final Object sLock = new Object();
    private final BlurEnabledListenerInternal mListenerInternal = new BlurEnabledListenerInternal();
    private final ArrayMap<Consumer<Boolean>, Executor> mListeners = new ArrayMap<>();
    private final Handler mMainHandler = new Handler(Looper.getMainLooper());
    private boolean mInternalListenerAttached = false;

    private CrossWindowBlurListeners() {
    }

    public static CrossWindowBlurListeners getInstance() {
        CrossWindowBlurListeners instance = sInstance;
        if (instance == null) {
            synchronized (sLock) {
                instance = sInstance;
                if (instance == null) {
                    instance = new CrossWindowBlurListeners();
                    sInstance = instance;
                }
            }
        }
        return instance;
    }

    public boolean isCrossWindowBlurEnabled() {
        boolean z;
        synchronized (sLock) {
            attachInternalListenerIfNeededLocked();
            z = this.mCrossWindowBlurEnabled;
        }
        return z;
    }

    public void addListener(Executor executor, Consumer<Boolean> listener) {
        Preconditions.checkNotNull(listener, "listener cannot be null");
        Preconditions.checkNotNull(executor, "executor cannot be null");
        synchronized (sLock) {
            attachInternalListenerIfNeededLocked();
            this.mListeners.put(listener, executor);
            notifyListener(listener, executor, this.mCrossWindowBlurEnabled);
        }
    }

    public void removeListener(Consumer<Boolean> listener) {
        Preconditions.checkNotNull(listener, "listener cannot be null");
        synchronized (sLock) {
            this.mListeners.remove(listener);
            if (this.mInternalListenerAttached && this.mListeners.size() == 0) {
                try {
                    WindowManagerGlobal.getWindowManagerService().unregisterCrossWindowBlurEnabledListener(this.mListenerInternal);
                    this.mInternalListenerAttached = false;
                } catch (RemoteException e) {
                    Log.m112d(TAG, "Could not unregister ICrossWindowBlurEnabledListener");
                }
            }
        }
    }

    private void attachInternalListenerIfNeededLocked() {
        if (!this.mInternalListenerAttached) {
            try {
                this.mCrossWindowBlurEnabled = WindowManagerGlobal.getWindowManagerService().registerCrossWindowBlurEnabledListener(this.mListenerInternal);
                this.mInternalListenerAttached = true;
            } catch (RemoteException e) {
                Log.m112d(TAG, "Could not register ICrossWindowBlurEnabledListener");
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyListener(final Consumer<Boolean> listener, Executor executor, final boolean enabled) {
        executor.execute(new Runnable() { // from class: android.view.CrossWindowBlurListeners$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                listener.accept(Boolean.valueOf(enabled));
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes4.dex */
    public final class BlurEnabledListenerInternal extends ICrossWindowBlurEnabledListener.Stub {
        private BlurEnabledListenerInternal() {
        }

        @Override // android.view.ICrossWindowBlurEnabledListener
        public void onCrossWindowBlurEnabledChanged(boolean enabled) {
            synchronized (CrossWindowBlurListeners.sLock) {
                CrossWindowBlurListeners.this.mCrossWindowBlurEnabled = enabled;
                long token = Binder.clearCallingIdentity();
                for (int i = 0; i < CrossWindowBlurListeners.this.mListeners.size(); i++) {
                    CrossWindowBlurListeners crossWindowBlurListeners = CrossWindowBlurListeners.this;
                    crossWindowBlurListeners.notifyListener((Consumer) crossWindowBlurListeners.mListeners.keyAt(i), (Executor) CrossWindowBlurListeners.this.mListeners.valueAt(i), enabled);
                }
                Binder.restoreCallingIdentity(token);
            }
        }
    }
}
