package android.hardware.display;

import android.content.Context;
import android.view.Display;
import android.view.Surface;
/* loaded from: classes.dex */
public final class VirtualDisplay {
    private final Display mDisplay;
    private final DisplayManagerGlobal mGlobal;
    private Surface mSurface;
    private IVirtualDisplayCallback mToken;
    private final Context mWindowContext;

    /* JADX INFO: Access modifiers changed from: package-private */
    public VirtualDisplay(DisplayManagerGlobal global, Display display, IVirtualDisplayCallback token, Surface surface, Context windowContext) {
        this.mGlobal = global;
        this.mDisplay = display;
        this.mToken = token;
        this.mSurface = surface;
        this.mWindowContext = windowContext;
    }

    public Display getDisplay() {
        return this.mDisplay;
    }

    public Surface getSurface() {
        return this.mSurface;
    }

    public IVirtualDisplayCallback getToken() {
        return this.mToken;
    }

    public void setSurface(Surface surface) {
        if (this.mSurface != surface) {
            this.mGlobal.setVirtualDisplaySurface(this.mToken, surface);
            this.mSurface = surface;
        }
    }

    public void resize(int width, int height, int densityDpi) {
        this.mGlobal.resizeVirtualDisplay(this.mToken, width, height, densityDpi);
    }

    public void release() {
        IVirtualDisplayCallback iVirtualDisplayCallback = this.mToken;
        if (iVirtualDisplayCallback != null) {
            this.mGlobal.releaseVirtualDisplay(iVirtualDisplayCallback);
            this.mToken = null;
        }
    }

    public void setDisplayState(boolean isOn) {
        IVirtualDisplayCallback iVirtualDisplayCallback = this.mToken;
        if (iVirtualDisplayCallback != null) {
            this.mGlobal.setVirtualDisplayState(iVirtualDisplayCallback, isOn);
        }
    }

    public String toString() {
        return "VirtualDisplay{display=" + this.mDisplay + ", token=" + this.mToken + ", surface=" + this.mSurface + "}";
    }

    /* loaded from: classes.dex */
    public static abstract class Callback {
        public void onPaused() {
        }

        public void onResumed() {
        }

        public void onStopped() {
        }
    }
}
