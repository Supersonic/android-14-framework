package com.android.internal.inputmethod;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
/* loaded from: classes4.dex */
public final class CancellationGroup {
    private final Object mLock = new Object();
    private ArrayList<CompletableFuture<?>> mFutureList = null;
    private boolean mCanceled = false;

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean tryRegisterFutureOrCancelImmediately(CompletableFuture<?> future) {
        synchronized (this.mLock) {
            if (this.mCanceled) {
                future.cancel(false);
                return false;
            }
            if (this.mFutureList == null) {
                this.mFutureList = new ArrayList<>(1);
            }
            this.mFutureList.add(future);
            return true;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void unregisterFuture(CompletableFuture<?> future) {
        synchronized (this.mLock) {
            ArrayList<CompletableFuture<?>> arrayList = this.mFutureList;
            if (arrayList != null) {
                arrayList.remove(future);
            }
        }
    }

    public void cancelAll() {
        synchronized (this.mLock) {
            if (!this.mCanceled) {
                this.mCanceled = true;
                ArrayList<CompletableFuture<?>> arrayList = this.mFutureList;
                if (arrayList != null) {
                    arrayList.forEach(new Consumer() { // from class: com.android.internal.inputmethod.CancellationGroup$$ExternalSyntheticLambda0
                        @Override // java.util.function.Consumer
                        public final void accept(Object obj) {
                            ((CompletableFuture) obj).cancel(false);
                        }
                    });
                    this.mFutureList.clear();
                    this.mFutureList = null;
                }
            }
        }
    }

    public boolean isCanceled() {
        boolean z;
        synchronized (this.mLock) {
            z = this.mCanceled;
        }
        return z;
    }
}
