package com.google.android.gles_jni;

import javax.microedition.khronos.egl.EGLSurface;
/* loaded from: classes5.dex */
public class EGLSurfaceImpl extends EGLSurface {
    long mEGLSurface;

    public EGLSurfaceImpl() {
        this.mEGLSurface = 0L;
    }

    public EGLSurfaceImpl(long surface) {
        this.mEGLSurface = surface;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        EGLSurfaceImpl that = (EGLSurfaceImpl) o;
        if (this.mEGLSurface == that.mEGLSurface) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        long j = this.mEGLSurface;
        int result = (17 * 31) + ((int) (j ^ (j >>> 32)));
        return result;
    }
}
