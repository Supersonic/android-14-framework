package android.p008os;

import android.p008os.ICancellationSignal;
/* renamed from: android.os.CancellationSignal */
/* loaded from: classes3.dex */
public final class CancellationSignal {
    private boolean mCancelInProgress;
    private boolean mIsCanceled;
    private OnCancelListener mOnCancelListener;
    private ICancellationSignal mRemote;

    /* renamed from: android.os.CancellationSignal$OnCancelListener */
    /* loaded from: classes3.dex */
    public interface OnCancelListener {
        void onCancel();
    }

    public boolean isCanceled() {
        boolean z;
        synchronized (this) {
            z = this.mIsCanceled;
        }
        return z;
    }

    public void throwIfCanceled() {
        if (isCanceled()) {
            throw new OperationCanceledException();
        }
    }

    public void cancel() {
        synchronized (this) {
            if (this.mIsCanceled) {
                return;
            }
            this.mIsCanceled = true;
            this.mCancelInProgress = true;
            OnCancelListener listener = this.mOnCancelListener;
            ICancellationSignal remote = this.mRemote;
            if (listener != null) {
                try {
                    listener.onCancel();
                } catch (Throwable th) {
                    synchronized (this) {
                        this.mCancelInProgress = false;
                        notifyAll();
                        throw th;
                    }
                }
            }
            if (remote != null) {
                try {
                    remote.cancel();
                } catch (RemoteException e) {
                }
            }
            synchronized (this) {
                this.mCancelInProgress = false;
                notifyAll();
            }
        }
    }

    public void setOnCancelListener(OnCancelListener listener) {
        synchronized (this) {
            waitForCancelFinishedLocked();
            if (this.mOnCancelListener == listener) {
                return;
            }
            this.mOnCancelListener = listener;
            if (this.mIsCanceled && listener != null) {
                listener.onCancel();
            }
        }
    }

    public void setRemote(ICancellationSignal remote) {
        synchronized (this) {
            waitForCancelFinishedLocked();
            if (this.mRemote == remote) {
                return;
            }
            this.mRemote = remote;
            if (this.mIsCanceled && remote != null) {
                try {
                    remote.cancel();
                } catch (RemoteException e) {
                }
            }
        }
    }

    private void waitForCancelFinishedLocked() {
        while (this.mCancelInProgress) {
            try {
                wait();
            } catch (InterruptedException e) {
            }
        }
    }

    public static ICancellationSignal createTransport() {
        return new Transport();
    }

    public static CancellationSignal fromTransport(ICancellationSignal transport) {
        if (transport instanceof Transport) {
            return ((Transport) transport).mCancellationSignal;
        }
        return null;
    }

    /* renamed from: android.os.CancellationSignal$Transport */
    /* loaded from: classes3.dex */
    private static final class Transport extends ICancellationSignal.Stub {
        final CancellationSignal mCancellationSignal;

        private Transport() {
            this.mCancellationSignal = new CancellationSignal();
        }

        @Override // android.p008os.ICancellationSignal
        public void cancel() throws RemoteException {
            this.mCancellationSignal.cancel();
        }
    }
}
