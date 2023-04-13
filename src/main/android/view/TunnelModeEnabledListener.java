package android.view;

import java.util.concurrent.Executor;
/* loaded from: classes4.dex */
public abstract class TunnelModeEnabledListener {
    private final Executor mExecutor;
    private long mNativeListener = nativeCreate(this);

    private static native long nativeCreate(TunnelModeEnabledListener tunnelModeEnabledListener);

    private static native void nativeDestroy(long j);

    private static native void nativeRegister(long j);

    private static native void nativeUnregister(long j);

    public abstract void onTunnelModeEnabledChanged(boolean z);

    public TunnelModeEnabledListener(Executor executor) {
        this.mExecutor = executor;
    }

    public void destroy() {
        if (this.mNativeListener == 0) {
            return;
        }
        unregister(this);
        nativeDestroy(this.mNativeListener);
        this.mNativeListener = 0L;
    }

    protected void finalize() throws Throwable {
        try {
            destroy();
        } finally {
            super.finalize();
        }
    }

    public static void register(TunnelModeEnabledListener listener) {
        long j = listener.mNativeListener;
        if (j == 0) {
            return;
        }
        nativeRegister(j);
    }

    public static void unregister(TunnelModeEnabledListener listener) {
        long j = listener.mNativeListener;
        if (j == 0) {
            return;
        }
        nativeUnregister(j);
    }

    public static void dispatchOnTunnelModeEnabledChanged(final TunnelModeEnabledListener listener, final boolean tunnelModeEnabled) {
        listener.mExecutor.execute(new Runnable() { // from class: android.view.TunnelModeEnabledListener$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                TunnelModeEnabledListener.this.onTunnelModeEnabledChanged(tunnelModeEnabled);
            }
        });
    }
}
