package android.opengl;
/* loaded from: classes2.dex */
public abstract class EGLObjectHandle {
    private final long mHandle;

    @Deprecated
    protected EGLObjectHandle(int handle) {
        this.mHandle = handle;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public EGLObjectHandle(long handle) {
        this.mHandle = handle;
    }

    @Deprecated
    public int getHandle() {
        long j = this.mHandle;
        if ((4294967295L & j) != j) {
            throw new UnsupportedOperationException();
        }
        return (int) j;
    }

    public long getNativeHandle() {
        return this.mHandle;
    }

    public int hashCode() {
        long j = this.mHandle;
        int result = (17 * 31) + ((int) (j ^ (j >>> 32)));
        return result;
    }
}
