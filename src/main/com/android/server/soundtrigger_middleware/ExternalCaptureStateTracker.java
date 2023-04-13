package com.android.server.soundtrigger_middleware;

import android.util.Log;
import com.android.server.soundtrigger_middleware.ICaptureStateNotifier;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Semaphore;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes2.dex */
public class ExternalCaptureStateTracker implements ICaptureStateNotifier {
    public static final String TAG = "CaptureStateTracker";
    public final List<ICaptureStateNotifier.Listener> mListeners = new LinkedList();
    public boolean mCaptureActive = true;
    public final Semaphore mNeedToConnect = new Semaphore(1);

    private native void connect();

    public ExternalCaptureStateTracker() {
        new Thread(new Runnable() { // from class: com.android.server.soundtrigger_middleware.ExternalCaptureStateTracker$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                ExternalCaptureStateTracker.this.run();
            }
        }).start();
    }

    @Override // com.android.server.soundtrigger_middleware.ICaptureStateNotifier
    public boolean registerListener(ICaptureStateNotifier.Listener listener) {
        boolean z;
        synchronized (this.mListeners) {
            this.mListeners.add(listener);
            z = this.mCaptureActive;
        }
        return z;
    }

    @Override // com.android.server.soundtrigger_middleware.ICaptureStateNotifier
    public void unregisterListener(ICaptureStateNotifier.Listener listener) {
        synchronized (this.mListeners) {
            this.mListeners.remove(listener);
        }
    }

    public final void run() {
        while (true) {
            this.mNeedToConnect.acquireUninterruptibly();
            connect();
        }
    }

    public final void setCaptureState(boolean z) {
        try {
            synchronized (this.mListeners) {
                this.mCaptureActive = z;
                for (ICaptureStateNotifier.Listener listener : this.mListeners) {
                    listener.onCaptureStateChange(z);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception caught while setting capture state", e);
        }
    }

    public final void binderDied() {
        Log.w(TAG, "Audio policy service died");
        this.mNeedToConnect.release();
    }
}
