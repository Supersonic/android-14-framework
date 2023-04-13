package android.window;

import android.p008os.Bundle;
import android.p008os.IBinder;
import android.util.Log;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
/* loaded from: classes4.dex */
public class WindowContextController {
    private static final boolean DEBUG_ATTACH = false;
    private static final String TAG = "WindowContextController";
    public int mAttachedToDisplayArea = 0;
    private final WindowTokenClient mToken;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes4.dex */
    public @interface AttachStatus {
        public static final int STATUS_ATTACHED = 1;
        public static final int STATUS_DETACHED = 2;
        public static final int STATUS_FAILED = 3;
        public static final int STATUS_INITIALIZED = 0;
    }

    public WindowContextController(WindowTokenClient token) {
        this.mToken = token;
    }

    public void attachToDisplayArea(int type, int displayId, Bundle options) {
        if (this.mAttachedToDisplayArea == 1) {
            throw new IllegalStateException("A Window Context can be only attached to a DisplayArea once.");
        }
        int i = this.mToken.attachToDisplayArea(type, displayId, options) ? 1 : 3;
        this.mAttachedToDisplayArea = i;
        if (i == 3) {
            Log.m104w(TAG, "attachToDisplayArea fail, type:" + type + ", displayId:" + displayId);
        }
    }

    public void attachToWindowToken(IBinder windowToken) {
        if (this.mAttachedToDisplayArea != 1) {
            throw new IllegalStateException("The Window Context should have been attached to a DisplayArea. AttachToDisplayArea:" + this.mAttachedToDisplayArea);
        }
        this.mToken.attachToWindowToken(windowToken);
    }

    public void detachIfNeeded() {
        if (this.mAttachedToDisplayArea == 1) {
            this.mToken.detachFromWindowContainerIfNeeded();
            this.mAttachedToDisplayArea = 2;
        }
    }
}
