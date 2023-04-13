package com.google.android.gles_jni;

import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.opengles.InterfaceC4503GL;
/* loaded from: classes5.dex */
public class EGLContextImpl extends EGLContext {
    long mEGLContext;
    private GLImpl mGLContext = new GLImpl();

    public EGLContextImpl(long ctx) {
        this.mEGLContext = ctx;
    }

    @Override // javax.microedition.khronos.egl.EGLContext
    public InterfaceC4503GL getGL() {
        return this.mGLContext;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        EGLContextImpl that = (EGLContextImpl) o;
        if (this.mEGLContext == that.mEGLContext) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        long j = this.mEGLContext;
        int result = (17 * 31) + ((int) (j ^ (j >>> 32)));
        return result;
    }
}
