package android.opengl;
/* loaded from: classes2.dex */
public class EGLConfig extends EGLObjectHandle {
    private EGLConfig(long handle) {
        super(handle);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof EGLConfig) {
            EGLConfig that = (EGLConfig) o;
            return getNativeHandle() == that.getNativeHandle();
        }
        return false;
    }
}
