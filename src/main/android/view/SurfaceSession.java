package android.view;
/* loaded from: classes4.dex */
public final class SurfaceSession {
    private long mNativeClient = nativeCreate();

    private static native long nativeCreate();

    private static native void nativeDestroy(long j);

    protected void finalize() throws Throwable {
        try {
            kill();
        } finally {
            super.finalize();
        }
    }

    public void kill() {
        long j = this.mNativeClient;
        if (j != 0) {
            nativeDestroy(j);
            this.mNativeClient = 0L;
        }
    }
}
