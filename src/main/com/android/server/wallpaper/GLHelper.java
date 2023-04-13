package com.android.server.wallpaper;

import android.opengl.EGL14;
import android.opengl.EGLConfig;
import android.opengl.EGLContext;
import android.opengl.EGLDisplay;
import android.opengl.EGLSurface;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.os.SystemProperties;
import android.util.Log;
/* loaded from: classes2.dex */
public class GLHelper {
    public static final String TAG = "GLHelper";
    public static final int sMaxTextureSize;

    static {
        int i = SystemProperties.getInt("sys.max_texture_size", 0);
        if (i <= 0) {
            i = retrieveTextureSizeFromGL();
        }
        sMaxTextureSize = i;
    }

    public static int retrieveTextureSizeFromGL() {
        try {
            EGLDisplay eglGetDisplay = EGL14.eglGetDisplay(0);
            if (eglGetDisplay == null || eglGetDisplay == EGL14.EGL_NO_DISPLAY) {
                throw new RuntimeException("eglGetDisplay failed: " + GLUtils.getEGLErrorString(EGL14.eglGetError()));
            }
            if (!EGL14.eglInitialize(eglGetDisplay, null, 0, null, 1)) {
                throw new RuntimeException("eglInitialize failed: " + GLUtils.getEGLErrorString(EGL14.eglGetError()));
            }
            int[] iArr = new int[1];
            EGLConfig[] eGLConfigArr = new EGLConfig[1];
            if (!EGL14.eglChooseConfig(eglGetDisplay, new int[]{12352, 4, 12324, 8, 12323, 8, 12322, 8, 12321, 0, 12325, 0, 12326, 0, 12327, 12344, 12344}, 0, eGLConfigArr, 0, 1, iArr, 0)) {
                throw new RuntimeException("eglChooseConfig failed: " + GLUtils.getEGLErrorString(EGL14.eglGetError()));
            }
            EGLConfig eGLConfig = iArr[0] > 0 ? eGLConfigArr[0] : null;
            if (eGLConfig == null) {
                throw new RuntimeException("eglConfig not initialized!");
            }
            EGLContext eglCreateContext = EGL14.eglCreateContext(eglGetDisplay, eGLConfig, EGL14.EGL_NO_CONTEXT, new int[]{12440, 2, 12344}, 0);
            if (eglCreateContext == null || eglCreateContext == EGL14.EGL_NO_CONTEXT) {
                throw new RuntimeException("eglCreateContext failed: " + GLUtils.getEGLErrorString(EGL14.eglGetError()));
            }
            EGLSurface eglCreatePbufferSurface = EGL14.eglCreatePbufferSurface(eglGetDisplay, eGLConfig, new int[]{12375, 1, 12374, 1, 12344}, 0);
            EGL14.eglMakeCurrent(eglGetDisplay, eglCreatePbufferSurface, eglCreatePbufferSurface, eglCreateContext);
            int[] iArr2 = new int[1];
            GLES20.glGetIntegerv(3379, iArr2, 0);
            EGLSurface eGLSurface = EGL14.EGL_NO_SURFACE;
            EGL14.eglMakeCurrent(eglGetDisplay, eGLSurface, eGLSurface, EGL14.EGL_NO_CONTEXT);
            EGL14.eglDestroySurface(eglGetDisplay, eglCreatePbufferSurface);
            EGL14.eglDestroyContext(eglGetDisplay, eglCreateContext);
            EGL14.eglTerminate(eglGetDisplay);
            return iArr2[0];
        } catch (RuntimeException e) {
            Log.w(TAG, "Retrieve from GL failed", e);
            return Integer.MAX_VALUE;
        }
    }

    public static int getMaxTextureSize() {
        return sMaxTextureSize;
    }
}
