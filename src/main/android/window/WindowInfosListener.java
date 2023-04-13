package android.window;

import android.graphics.Matrix;
import android.util.Pair;
import android.util.Size;
import android.view.InputWindowHandle;
import libcore.util.NativeAllocationRegistry;
/* loaded from: classes4.dex */
public abstract class WindowInfosListener {
    private final long mNativeListener;

    private static native long nativeCreate(WindowInfosListener windowInfosListener);

    private static native long nativeGetFinalizer();

    private static native Pair<InputWindowHandle[], DisplayInfo[]> nativeRegister(long j);

    private static native void nativeUnregister(long j);

    public abstract void onWindowInfosChanged(InputWindowHandle[] inputWindowHandleArr, DisplayInfo[] displayInfoArr);

    public WindowInfosListener() {
        NativeAllocationRegistry registry = NativeAllocationRegistry.createMalloced(WindowInfosListener.class.getClassLoader(), nativeGetFinalizer());
        long nativeCreate = nativeCreate(this);
        this.mNativeListener = nativeCreate;
        registry.registerNativeAllocation(this, nativeCreate);
    }

    public Pair<InputWindowHandle[], DisplayInfo[]> register() {
        return nativeRegister(this.mNativeListener);
    }

    public void unregister() {
        nativeUnregister(this.mNativeListener);
    }

    /* loaded from: classes4.dex */
    public static final class DisplayInfo {
        public final int mDisplayId;
        public final Size mLogicalSize;
        public final Matrix mTransform;

        private DisplayInfo(int displayId, int logicalWidth, int logicalHeight, Matrix transform) {
            this.mDisplayId = displayId;
            this.mLogicalSize = new Size(logicalWidth, logicalHeight);
            this.mTransform = transform;
        }

        public String toString() {
            return "displayId=" + this.mDisplayId + ", mLogicalSize=" + this.mLogicalSize + ", mTransform=" + this.mTransform;
        }
    }
}
