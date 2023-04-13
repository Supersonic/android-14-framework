package android.opengl;
/* loaded from: classes2.dex */
public class EGLImage extends EGLObjectHandle {
    private EGLImage(long handle) {
        super(handle);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof EGLImage) {
            EGLImage that = (EGLImage) o;
            return getNativeHandle() == that.getNativeHandle();
        }
        return false;
    }
}
