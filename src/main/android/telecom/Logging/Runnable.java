package android.telecom.Logging;

import android.telecom.Log;
/* loaded from: classes3.dex */
public abstract class Runnable {
    private final Object mLock;
    private final java.lang.Runnable mRunnable = new java.lang.Runnable() { // from class: android.telecom.Logging.Runnable.1
        @Override // java.lang.Runnable
        public void run() {
            synchronized (Runnable.this.mLock) {
                Log.continueSession(Runnable.this.mSubsession, Runnable.this.mSubsessionName);
                Runnable.this.loggedRun();
                if (Runnable.this.mSubsession != null) {
                    Log.endSession();
                    Runnable.this.mSubsession = null;
                }
            }
        }
    };
    private Session mSubsession;
    private final String mSubsessionName;

    public abstract void loggedRun();

    public Runnable(String subsessionName, Object lock) {
        if (lock == null) {
            this.mLock = new Object();
        } else {
            this.mLock = lock;
        }
        this.mSubsessionName = subsessionName;
    }

    public final java.lang.Runnable getRunnableToCancel() {
        return this.mRunnable;
    }

    public java.lang.Runnable prepare() {
        cancel();
        this.mSubsession = Log.createSubsession();
        return this.mRunnable;
    }

    public void cancel() {
        synchronized (this.mLock) {
            Log.cancelSubsession(this.mSubsession);
            this.mSubsession = null;
        }
    }
}
