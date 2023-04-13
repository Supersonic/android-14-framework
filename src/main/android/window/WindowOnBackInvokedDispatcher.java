package android.window;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.p008os.Handler;
import android.p008os.RemoteException;
import android.p008os.SystemProperties;
import android.util.Log;
import android.view.IWindow;
import android.view.IWindowSession;
import android.window.BackProgressAnimator;
import android.window.IOnBackInvokedCallback;
import android.window.ImeOnBackInvokedDispatcher;
import android.window.WindowOnBackInvokedDispatcher;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.TreeMap;
import java.util.function.BiConsumer;
/* loaded from: classes4.dex */
public class WindowOnBackInvokedDispatcher implements OnBackInvokedDispatcher {
    private static final boolean ALWAYS_ENFORCE_PREDICTIVE_BACK;
    private static final boolean ENABLE_PREDICTIVE_BACK;
    private static final String TAG = "WindowOnBackDispatcher";
    private static final BackProgressAnimator mProgressAnimator;
    private Checker mChecker;
    private ImeOnBackInvokedDispatcher mImeDispatcher;
    private IWindow mWindow;
    private IWindowSession mWindowSession;
    private final HashMap<OnBackInvokedCallback, Integer> mAllCallbacks = new HashMap<>();
    private final TreeMap<Integer, ArrayList<OnBackInvokedCallback>> mOnBackInvokedCallbacks = new TreeMap<>();

    static {
        ENABLE_PREDICTIVE_BACK = SystemProperties.getInt("persist.wm.debug.predictive_back", 1) != 0;
        ALWAYS_ENFORCE_PREDICTIVE_BACK = SystemProperties.getInt("persist.wm.debug.predictive_back_always_enforce", 0) != 0;
        mProgressAnimator = new BackProgressAnimator();
    }

    public WindowOnBackInvokedDispatcher(Context context) {
        this.mChecker = new Checker(context);
    }

    public void attachToWindow(IWindowSession windowSession, IWindow window) {
        this.mWindowSession = windowSession;
        this.mWindow = window;
        if (!this.mAllCallbacks.isEmpty()) {
            setTopOnBackInvokedCallback(getTopCallback());
        }
    }

    public void detachFromWindow() {
        clear();
        this.mWindow = null;
        this.mWindowSession = null;
    }

    @Override // android.window.OnBackInvokedDispatcher
    public void registerOnBackInvokedCallback(int priority, OnBackInvokedCallback callback) {
        if (this.mChecker.checkApplicationCallbackRegistration(priority, callback)) {
            registerOnBackInvokedCallbackUnchecked(callback, priority);
        }
    }

    public void registerOnBackInvokedCallbackUnchecked(OnBackInvokedCallback callback, int priority) {
        ImeOnBackInvokedDispatcher imeOnBackInvokedDispatcher = this.mImeDispatcher;
        if (imeOnBackInvokedDispatcher != null) {
            imeOnBackInvokedDispatcher.registerOnBackInvokedCallback(priority, callback);
            return;
        }
        if (!this.mOnBackInvokedCallbacks.containsKey(Integer.valueOf(priority))) {
            this.mOnBackInvokedCallbacks.put(Integer.valueOf(priority), new ArrayList<>());
        }
        ArrayList<OnBackInvokedCallback> callbacks = this.mOnBackInvokedCallbacks.get(Integer.valueOf(priority));
        if (this.mAllCallbacks.containsKey(callback)) {
            Integer prevPriority = this.mAllCallbacks.get(callback);
            this.mOnBackInvokedCallbacks.get(prevPriority).remove(callback);
        }
        OnBackInvokedCallback previousTopCallback = getTopCallback();
        callbacks.add(callback);
        this.mAllCallbacks.put(callback, Integer.valueOf(priority));
        if (previousTopCallback == null || (previousTopCallback != callback && this.mAllCallbacks.get(previousTopCallback).intValue() <= priority)) {
            setTopOnBackInvokedCallback(callback);
        }
    }

    @Override // android.window.OnBackInvokedDispatcher
    public void unregisterOnBackInvokedCallback(OnBackInvokedCallback callback) {
        ImeOnBackInvokedDispatcher imeOnBackInvokedDispatcher = this.mImeDispatcher;
        if (imeOnBackInvokedDispatcher != null) {
            imeOnBackInvokedDispatcher.unregisterOnBackInvokedCallback(callback);
        } else if (!this.mAllCallbacks.containsKey(callback)) {
        } else {
            OnBackInvokedCallback previousTopCallback = getTopCallback();
            Integer priority = this.mAllCallbacks.get(callback);
            ArrayList<OnBackInvokedCallback> callbacks = this.mOnBackInvokedCallbacks.get(priority);
            callbacks.remove(callback);
            if (callbacks.isEmpty()) {
                this.mOnBackInvokedCallbacks.remove(priority);
            }
            this.mAllCallbacks.remove(callback);
            if (previousTopCallback == callback) {
                setTopOnBackInvokedCallback(getTopCallback());
            }
        }
    }

    @Override // android.window.OnBackInvokedDispatcher
    public void registerSystemOnBackInvokedCallback(OnBackInvokedCallback callback) {
        registerOnBackInvokedCallbackUnchecked(callback, -1);
    }

    public void clear() {
        ImeOnBackInvokedDispatcher imeOnBackInvokedDispatcher = this.mImeDispatcher;
        if (imeOnBackInvokedDispatcher != null) {
            imeOnBackInvokedDispatcher.clear();
            this.mImeDispatcher = null;
        }
        if (!this.mAllCallbacks.isEmpty()) {
            setTopOnBackInvokedCallback(null);
        }
        this.mAllCallbacks.clear();
        this.mOnBackInvokedCallbacks.clear();
    }

    private void setTopOnBackInvokedCallback(OnBackInvokedCallback callback) {
        IOnBackInvokedCallback iCallback;
        if (this.mWindowSession == null || this.mWindow == null) {
            return;
        }
        OnBackInvokedCallbackInfo callbackInfo = null;
        if (callback != null) {
            try {
                int priority = this.mAllCallbacks.get(callback).intValue();
                if (callback instanceof ImeOnBackInvokedDispatcher.ImeOnBackInvokedCallback) {
                    iCallback = ((ImeOnBackInvokedDispatcher.ImeOnBackInvokedCallback) callback).getIOnBackInvokedCallback();
                } else {
                    iCallback = new OnBackInvokedCallbackWrapper(callback);
                }
                callbackInfo = new OnBackInvokedCallbackInfo(iCallback, priority);
            } catch (RemoteException e) {
                Log.m110e(TAG, "Failed to set OnBackInvokedCallback to WM. Error: " + e);
                return;
            }
        }
        this.mWindowSession.setOnBackInvokedCallbackInfo(this.mWindow, callbackInfo);
    }

    public OnBackInvokedCallback getTopCallback() {
        if (this.mAllCallbacks.isEmpty()) {
            return null;
        }
        for (Integer priority : this.mOnBackInvokedCallbacks.descendingKeySet()) {
            ArrayList<OnBackInvokedCallback> callbacks = this.mOnBackInvokedCallbacks.get(priority);
            if (!callbacks.isEmpty()) {
                return callbacks.get(callbacks.size() - 1);
            }
        }
        return null;
    }

    public void updateContext(Context context) {
        this.mChecker = new Checker(context);
    }

    public boolean isOnBackInvokedCallbackEnabled() {
        return Checker.isOnBackInvokedCallbackEnabled(this.mChecker.getContext());
    }

    public void dump(String prefix, final PrintWriter writer) {
        final String innerPrefix = prefix + "    ";
        writer.println(prefix + "WindowOnBackDispatcher:");
        if (this.mAllCallbacks.isEmpty()) {
            writer.println(prefix + "<None>");
            return;
        }
        writer.println(innerPrefix + "Top Callback: " + getTopCallback());
        writer.println(innerPrefix + "Callbacks: ");
        this.mAllCallbacks.forEach(new BiConsumer() { // from class: android.window.WindowOnBackInvokedDispatcher$$ExternalSyntheticLambda0
            @Override // java.util.function.BiConsumer
            public final void accept(Object obj, Object obj2) {
                writer.println(innerPrefix + "  Callback: " + ((OnBackInvokedCallback) obj) + " Priority=" + ((Integer) obj2));
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes4.dex */
    public static class OnBackInvokedCallbackWrapper extends IOnBackInvokedCallback.Stub {
        final CallbackRef mCallbackRef;

        /* JADX INFO: Access modifiers changed from: package-private */
        /* loaded from: classes4.dex */
        public static class CallbackRef {
            final OnBackInvokedCallback mStrongRef;
            final WeakReference<OnBackInvokedCallback> mWeakRef;

            CallbackRef(OnBackInvokedCallback callback, boolean useWeakRef) {
                if (useWeakRef) {
                    this.mWeakRef = new WeakReference<>(callback);
                    this.mStrongRef = null;
                    return;
                }
                this.mStrongRef = callback;
                this.mWeakRef = null;
            }

            OnBackInvokedCallback get() {
                OnBackInvokedCallback onBackInvokedCallback = this.mStrongRef;
                if (onBackInvokedCallback != null) {
                    return onBackInvokedCallback;
                }
                return this.mWeakRef.get();
            }
        }

        OnBackInvokedCallbackWrapper(OnBackInvokedCallback callback) {
            this.mCallbackRef = new CallbackRef(callback, true);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public OnBackInvokedCallbackWrapper(OnBackInvokedCallback callback, boolean useWeakRef) {
            this.mCallbackRef = new CallbackRef(callback, useWeakRef);
        }

        @Override // android.window.IOnBackInvokedCallback
        public void onBackStarted(final BackMotionEvent backEvent) {
            Handler.getMain().post(new Runnable() { // from class: android.window.WindowOnBackInvokedDispatcher$OnBackInvokedCallbackWrapper$$ExternalSyntheticLambda4
                @Override // java.lang.Runnable
                public final void run() {
                    WindowOnBackInvokedDispatcher.OnBackInvokedCallbackWrapper.this.lambda$onBackStarted$1(backEvent);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onBackStarted$1(BackMotionEvent backEvent) {
            final OnBackAnimationCallback callback = getBackAnimationCallback();
            if (callback != null) {
                WindowOnBackInvokedDispatcher.mProgressAnimator.onBackStarted(backEvent, new BackProgressAnimator.ProgressCallback() { // from class: android.window.WindowOnBackInvokedDispatcher$OnBackInvokedCallbackWrapper$$ExternalSyntheticLambda5
                    @Override // android.window.BackProgressAnimator.ProgressCallback
                    public final void onProgressUpdate(BackEvent backEvent2) {
                        OnBackAnimationCallback.this.onBackProgressed(backEvent2);
                    }
                });
                callback.onBackStarted(new BackEvent(backEvent.getTouchX(), backEvent.getTouchY(), backEvent.getProgress(), backEvent.getSwipeEdge()));
            }
        }

        @Override // android.window.IOnBackInvokedCallback
        public void onBackProgressed(final BackMotionEvent backEvent) {
            Handler.getMain().post(new Runnable() { // from class: android.window.WindowOnBackInvokedDispatcher$OnBackInvokedCallbackWrapper$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    WindowOnBackInvokedDispatcher.OnBackInvokedCallbackWrapper.this.lambda$onBackProgressed$2(backEvent);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onBackProgressed$2(BackMotionEvent backEvent) {
            OnBackAnimationCallback callback = getBackAnimationCallback();
            if (callback != null) {
                WindowOnBackInvokedDispatcher.mProgressAnimator.onBackProgressed(backEvent);
            }
        }

        @Override // android.window.IOnBackInvokedCallback
        public void onBackCancelled() {
            Handler.getMain().post(new Runnable() { // from class: android.window.WindowOnBackInvokedDispatcher$OnBackInvokedCallbackWrapper$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    WindowOnBackInvokedDispatcher.OnBackInvokedCallbackWrapper.this.lambda$onBackCancelled$4();
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onBackCancelled$4() {
            WindowOnBackInvokedDispatcher.mProgressAnimator.onBackCancelled(new Runnable() { // from class: android.window.WindowOnBackInvokedDispatcher$OnBackInvokedCallbackWrapper$$ExternalSyntheticLambda3
                @Override // java.lang.Runnable
                public final void run() {
                    WindowOnBackInvokedDispatcher.OnBackInvokedCallbackWrapper.this.lambda$onBackCancelled$3();
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onBackCancelled$3() {
            OnBackAnimationCallback callback = getBackAnimationCallback();
            if (callback != null) {
                callback.onBackCancelled();
            }
        }

        @Override // android.window.IOnBackInvokedCallback
        public void onBackInvoked() throws RemoteException {
            Handler.getMain().post(new Runnable() { // from class: android.window.WindowOnBackInvokedDispatcher$OnBackInvokedCallbackWrapper$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    WindowOnBackInvokedDispatcher.OnBackInvokedCallbackWrapper.this.lambda$onBackInvoked$5();
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onBackInvoked$5() {
            WindowOnBackInvokedDispatcher.mProgressAnimator.reset();
            OnBackInvokedCallback callback = this.mCallbackRef.get();
            if (callback == null) {
                Log.m112d(WindowOnBackInvokedDispatcher.TAG, "Trying to call onBackInvoked() on a null callback reference.");
            } else {
                callback.onBackInvoked();
            }
        }

        private OnBackAnimationCallback getBackAnimationCallback() {
            OnBackInvokedCallback callback = this.mCallbackRef.get();
            if (callback instanceof OnBackAnimationCallback) {
                return (OnBackAnimationCallback) callback;
            }
            return null;
        }
    }

    public static boolean isOnBackInvokedCallbackEnabled(Context context) {
        return Checker.isOnBackInvokedCallbackEnabled(context);
    }

    @Override // android.window.OnBackInvokedDispatcher
    public void setImeOnBackInvokedDispatcher(ImeOnBackInvokedDispatcher imeDispatcher) {
        this.mImeDispatcher = imeDispatcher;
    }

    public boolean hasImeOnBackInvokedDispatcher() {
        return this.mImeDispatcher != null;
    }

    /* loaded from: classes4.dex */
    public static class Checker {
        private WeakReference<Context> mContext;

        public Checker(Context context) {
            this.mContext = new WeakReference<>(context);
        }

        public boolean checkApplicationCallbackRegistration(int priority, OnBackInvokedCallback callback) {
            if (!isOnBackInvokedCallbackEnabled(getContext()) && !(callback instanceof CompatOnBackInvokedCallback)) {
                Log.m104w(WindowOnBackInvokedDispatcher.TAG, "OnBackInvokedCallback is not enabled for the application.\nSet 'android:enableOnBackInvokedCallback=\"true\"' in the application manifest.");
                return false;
            } else if (priority < 0) {
                throw new IllegalArgumentException("Application registered OnBackInvokedCallback cannot have negative priority. Priority: " + priority);
            } else {
                Objects.requireNonNull(callback);
                return true;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public Context getContext() {
            return this.mContext.get();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public static boolean isOnBackInvokedCallbackEnabled(Context context) {
            boolean featureFlagEnabled = WindowOnBackInvokedDispatcher.ENABLE_PREDICTIVE_BACK;
            if (!featureFlagEnabled) {
                return false;
            }
            if (WindowOnBackInvokedDispatcher.ALWAYS_ENFORCE_PREDICTIVE_BACK) {
                return true;
            }
            if (context == null) {
                Log.m104w(WindowOnBackInvokedDispatcher.TAG, "OnBackInvokedCallback is not enabled because context is null.");
                return false;
            }
            while ((context instanceof ContextWrapper) && !(context instanceof Activity)) {
                context = ((ContextWrapper) context).getBaseContext();
            }
            if (context instanceof Activity) {
                Activity activity = (Activity) context;
                if (activity.getActivityInfo().hasOnBackInvokedCallbackEnabled()) {
                    boolean requestsPredictiveBack = activity.getActivityInfo().isOnBackInvokedCallbackEnabled();
                    return requestsPredictiveBack;
                }
                boolean requestsPredictiveBack2 = context.getApplicationInfo().isOnBackInvokedCallbackEnabled();
                return requestsPredictiveBack2;
            }
            boolean requestsPredictiveBack3 = context.getApplicationInfo().isOnBackInvokedCallbackEnabled();
            return requestsPredictiveBack3;
        }
    }
}
