package android.view;

import android.graphics.Matrix;
import android.graphics.Region;
import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.p008os.IBinder;
import android.view.IWindow;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.ref.WeakReference;
/* loaded from: classes4.dex */
public final class InputWindowHandle {
    public long dispatchingTimeoutMillis;
    public int displayId;
    public int frameBottom;
    public int frameLeft;
    public int frameRight;
    public int frameTop;
    public InputApplicationHandle inputApplicationHandle;
    public int inputConfig;
    public int layoutParamsFlags;
    public int layoutParamsType;
    public String name;
    public int ownerPid;
    public int ownerUid;
    public String packageName;
    private long ptr;
    public boolean replaceTouchableRegionWithCrop;
    public float scaleFactor;
    public int surfaceInset;
    public IBinder token;
    public int touchOcclusionMode;
    public final Region touchableRegion;
    public WeakReference<SurfaceControl> touchableRegionSurfaceControl;
    public Matrix transform;
    private IWindow window;
    private IBinder windowToken;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes4.dex */
    public @interface InputConfigFlags {
    }

    private native void nativeDispose();

    public InputWindowHandle(InputApplicationHandle inputApplicationHandle, int displayId) {
        this.touchableRegion = new Region();
        this.touchOcclusionMode = 0;
        this.touchableRegionSurfaceControl = new WeakReference<>(null);
        this.inputApplicationHandle = inputApplicationHandle;
        this.displayId = displayId;
    }

    public InputWindowHandle(InputWindowHandle other) {
        Region region = new Region();
        this.touchableRegion = region;
        this.touchOcclusionMode = 0;
        this.touchableRegionSurfaceControl = new WeakReference<>(null);
        this.ptr = 0L;
        this.inputApplicationHandle = new InputApplicationHandle(other.inputApplicationHandle);
        this.token = other.token;
        this.windowToken = other.windowToken;
        this.window = other.window;
        this.name = other.name;
        this.layoutParamsFlags = other.layoutParamsFlags;
        this.layoutParamsType = other.layoutParamsType;
        this.dispatchingTimeoutMillis = other.dispatchingTimeoutMillis;
        this.frameLeft = other.frameLeft;
        this.frameTop = other.frameTop;
        this.frameRight = other.frameRight;
        this.frameBottom = other.frameBottom;
        this.surfaceInset = other.surfaceInset;
        this.scaleFactor = other.scaleFactor;
        region.set(other.touchableRegion);
        this.inputConfig = other.inputConfig;
        this.touchOcclusionMode = other.touchOcclusionMode;
        this.ownerPid = other.ownerPid;
        this.ownerUid = other.ownerUid;
        this.packageName = other.packageName;
        this.displayId = other.displayId;
        this.touchableRegionSurfaceControl = other.touchableRegionSurfaceControl;
        this.replaceTouchableRegionWithCrop = other.replaceTouchableRegionWithCrop;
        if (other.transform != null) {
            Matrix matrix = new Matrix();
            this.transform = matrix;
            matrix.set(other.transform);
        }
    }

    public String toString() {
        String str = this.name;
        if (str == null) {
            str = "";
        }
        return str + ", frame=[" + this.frameLeft + "," + this.frameTop + "," + this.frameRight + "," + this.frameBottom + NavigationBarInflaterView.SIZE_MOD_END + ", touchableRegion=" + this.touchableRegion + ", scaleFactor=" + this.scaleFactor + ", transform=" + this.transform + ", windowToken=" + this.windowToken + ", isClone=" + ((this.inputConfig & 65536) != 0);
    }

    protected void finalize() throws Throwable {
        try {
            nativeDispose();
        } finally {
            super.finalize();
        }
    }

    public void replaceTouchableRegionWithCrop(SurfaceControl bounds) {
        setTouchableRegionCrop(bounds);
        this.replaceTouchableRegionWithCrop = true;
    }

    public void setTouchableRegionCrop(SurfaceControl bounds) {
        this.touchableRegionSurfaceControl = new WeakReference<>(bounds);
    }

    public void setWindowToken(IWindow iwindow) {
        this.windowToken = iwindow.asBinder();
        this.window = iwindow;
    }

    public IBinder getWindowToken() {
        return this.windowToken;
    }

    public IWindow getWindow() {
        IWindow iWindow = this.window;
        if (iWindow != null) {
            return iWindow;
        }
        IWindow asInterface = IWindow.Stub.asInterface(this.windowToken);
        this.window = asInterface;
        return asInterface;
    }

    public void setInputConfig(int inputConfig, boolean value) {
        if (value) {
            this.inputConfig |= inputConfig;
        } else {
            this.inputConfig &= ~inputConfig;
        }
    }
}
