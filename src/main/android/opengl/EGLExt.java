package android.opengl;

import android.hardware.SyncFence;
import android.p008os.ParcelFileDescriptor;
import android.util.Log;
/* loaded from: classes2.dex */
public class EGLExt {
    public static final int EGL_CONTEXT_FLAGS_KHR = 12540;
    public static final int EGL_CONTEXT_MAJOR_VERSION_KHR = 12440;
    public static final int EGL_CONTEXT_MINOR_VERSION_KHR = 12539;
    public static final int EGL_NO_NATIVE_FENCE_FD_ANDROID = -1;
    public static final int EGL_OPENGL_ES3_BIT_KHR = 64;
    public static final int EGL_RECORDABLE_ANDROID = 12610;
    public static final int EGL_SYNC_NATIVE_FENCE_ANDROID = 12612;
    public static final int EGL_SYNC_NATIVE_FENCE_FD_ANDROID = 12613;
    public static final int EGL_SYNC_NATIVE_FENCE_SIGNALED_ANDROID = 12614;

    private static native void _nativeClassInit();

    private static native int eglDupNativeFenceFDANDROIDImpl(EGLDisplay eGLDisplay, EGLSync eGLSync);

    public static native boolean eglPresentationTimeANDROID(EGLDisplay eGLDisplay, EGLSurface eGLSurface, long j);

    static {
        _nativeClassInit();
    }

    public static SyncFence eglDupNativeFenceFDANDROID(EGLDisplay display, EGLSync sync) {
        int fd = eglDupNativeFenceFDANDROIDImpl(display, sync);
        Log.m112d("EGL", "eglDupNativeFence returned " + fd);
        if (fd >= 0) {
            return SyncFence.create(ParcelFileDescriptor.adoptFd(fd));
        }
        return SyncFence.createEmpty();
    }
}
