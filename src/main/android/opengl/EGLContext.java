package android.opengl;
/* loaded from: classes2.dex */
public class EGLContext extends EGLObjectHandle {
    private EGLContext(long handle) {
        super(handle);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof EGLContext) {
            EGLContext that = (EGLContext) o;
            return getNativeHandle() == that.getNativeHandle();
        }
        return false;
    }
}
