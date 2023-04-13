package com.android.internal.view;

import android.view.SurfaceHolder;
/* loaded from: classes2.dex */
public class SurfaceCallbackHelper {
    int mFinishDrawingCollected = 0;
    int mFinishDrawingExpected = 0;
    private Runnable mFinishDrawingRunnable = new Runnable() { // from class: com.android.internal.view.SurfaceCallbackHelper.1
        @Override // java.lang.Runnable
        public void run() {
            synchronized (SurfaceCallbackHelper.this) {
                SurfaceCallbackHelper.this.mFinishDrawingCollected++;
                if (SurfaceCallbackHelper.this.mFinishDrawingCollected < SurfaceCallbackHelper.this.mFinishDrawingExpected) {
                    return;
                }
                SurfaceCallbackHelper.this.mRunnable.run();
            }
        }
    };
    Runnable mRunnable;

    public SurfaceCallbackHelper(Runnable callbacksCollected) {
        this.mRunnable = callbacksCollected;
    }

    public void dispatchSurfaceRedrawNeededAsync(SurfaceHolder holder, SurfaceHolder.Callback[] callbacks) {
        int i;
        if (callbacks == null || callbacks.length == 0) {
            this.mRunnable.run();
            return;
        }
        synchronized (this) {
            this.mFinishDrawingExpected = callbacks.length;
            this.mFinishDrawingCollected = 0;
        }
        for (SurfaceHolder.Callback c : callbacks) {
            if (c instanceof SurfaceHolder.Callback2) {
                ((SurfaceHolder.Callback2) c).surfaceRedrawNeededAsync(holder, this.mFinishDrawingRunnable);
            } else {
                this.mFinishDrawingRunnable.run();
            }
        }
    }
}
