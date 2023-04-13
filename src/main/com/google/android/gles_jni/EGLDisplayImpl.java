package com.google.android.gles_jni;

import javax.microedition.khronos.egl.EGLDisplay;
/* loaded from: classes5.dex */
public class EGLDisplayImpl extends EGLDisplay {
    long mEGLDisplay;

    public EGLDisplayImpl(long dpy) {
        this.mEGLDisplay = dpy;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        EGLDisplayImpl that = (EGLDisplayImpl) o;
        if (this.mEGLDisplay == that.mEGLDisplay) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        long j = this.mEGLDisplay;
        int result = (17 * 31) + ((int) (j ^ (j >>> 32)));
        return result;
    }
}
