package android.window;

import android.content.Context;
import android.util.Pair;
import android.window.WindowOnBackInvokedDispatcher;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
/* loaded from: classes4.dex */
public class ProxyOnBackInvokedDispatcher implements OnBackInvokedDispatcher {
    private final WindowOnBackInvokedDispatcher.Checker mChecker;
    private ImeOnBackInvokedDispatcher mImeDispatcher;
    private final List<Pair<OnBackInvokedCallback, Integer>> mCallbacks = new ArrayList();
    private final Object mLock = new Object();
    private OnBackInvokedDispatcher mActualDispatcher = null;

    public ProxyOnBackInvokedDispatcher(Context context) {
        this.mChecker = new WindowOnBackInvokedDispatcher.Checker(context);
    }

    @Override // android.window.OnBackInvokedDispatcher
    public void registerOnBackInvokedCallback(int priority, OnBackInvokedCallback callback) {
        if (this.mChecker.checkApplicationCallbackRegistration(priority, callback)) {
            registerOnBackInvokedCallbackUnchecked(callback, priority);
        }
    }

    @Override // android.window.OnBackInvokedDispatcher
    public void registerSystemOnBackInvokedCallback(OnBackInvokedCallback callback) {
        registerOnBackInvokedCallbackUnchecked(callback, -1);
    }

    @Override // android.window.OnBackInvokedDispatcher
    public void unregisterOnBackInvokedCallback(final OnBackInvokedCallback callback) {
        synchronized (this.mLock) {
            this.mCallbacks.removeIf(new Predicate() { // from class: android.window.ProxyOnBackInvokedDispatcher$$ExternalSyntheticLambda0
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean equals;
                    equals = ((OnBackInvokedCallback) ((Pair) obj).first).equals(OnBackInvokedCallback.this);
                    return equals;
                }
            });
            OnBackInvokedDispatcher onBackInvokedDispatcher = this.mActualDispatcher;
            if (onBackInvokedDispatcher != null) {
                onBackInvokedDispatcher.unregisterOnBackInvokedCallback(callback);
            }
        }
    }

    private void registerOnBackInvokedCallbackUnchecked(OnBackInvokedCallback callback, int priority) {
        synchronized (this.mLock) {
            this.mCallbacks.add(Pair.create(callback, Integer.valueOf(priority)));
            OnBackInvokedDispatcher onBackInvokedDispatcher = this.mActualDispatcher;
            if (onBackInvokedDispatcher != null) {
                if (priority <= -1) {
                    onBackInvokedDispatcher.registerSystemOnBackInvokedCallback(callback);
                } else {
                    onBackInvokedDispatcher.registerOnBackInvokedCallback(priority, callback);
                }
            }
        }
    }

    private void transferCallbacksToDispatcher() {
        OnBackInvokedDispatcher onBackInvokedDispatcher = this.mActualDispatcher;
        if (onBackInvokedDispatcher == null) {
            return;
        }
        ImeOnBackInvokedDispatcher imeOnBackInvokedDispatcher = this.mImeDispatcher;
        if (imeOnBackInvokedDispatcher != null) {
            onBackInvokedDispatcher.setImeOnBackInvokedDispatcher(imeOnBackInvokedDispatcher);
        }
        for (Pair<OnBackInvokedCallback, Integer> callbackPair : this.mCallbacks) {
            int priority = callbackPair.second.intValue();
            if (priority >= 0) {
                this.mActualDispatcher.registerOnBackInvokedCallback(priority, callbackPair.first);
            } else {
                this.mActualDispatcher.registerSystemOnBackInvokedCallback(callbackPair.first);
            }
        }
        this.mCallbacks.clear();
        this.mImeDispatcher = null;
    }

    private void clearCallbacksOnDispatcher() {
        if (this.mActualDispatcher == null) {
            return;
        }
        for (Pair<OnBackInvokedCallback, Integer> callback : this.mCallbacks) {
            this.mActualDispatcher.unregisterOnBackInvokedCallback(callback.first);
        }
    }

    public void reset() {
        synchronized (this.mLock) {
            this.mCallbacks.clear();
            this.mImeDispatcher = null;
        }
    }

    public void setActualDispatcher(OnBackInvokedDispatcher actualDispatcher) {
        synchronized (this.mLock) {
            if (actualDispatcher == this.mActualDispatcher) {
                return;
            }
            clearCallbacksOnDispatcher();
            this.mActualDispatcher = actualDispatcher;
            transferCallbacksToDispatcher();
        }
    }

    @Override // android.window.OnBackInvokedDispatcher
    public void setImeOnBackInvokedDispatcher(ImeOnBackInvokedDispatcher imeDispatcher) {
        OnBackInvokedDispatcher onBackInvokedDispatcher = this.mActualDispatcher;
        if (onBackInvokedDispatcher != null) {
            onBackInvokedDispatcher.setImeOnBackInvokedDispatcher(imeDispatcher);
        } else {
            this.mImeDispatcher = imeDispatcher;
        }
    }
}
