package android.window;

import android.content.ComponentCallbacks;
import android.content.ComponentCallbacksController;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Configuration;
import android.p008os.Bundle;
import android.view.WindowManager;
import android.view.WindowManagerImpl;
import java.lang.ref.Reference;
/* loaded from: classes4.dex */
public class WindowContext extends ContextWrapper implements WindowProvider {
    private final ComponentCallbacksController mCallbacksController;
    private final WindowContextController mController;
    private final Bundle mOptions;
    private final int mType;
    private final WindowManager mWindowManager;

    public WindowContext(Context base, int type, Bundle options) {
        super(base);
        this.mCallbacksController = new ComponentCallbacksController();
        this.mType = type;
        this.mOptions = options;
        this.mWindowManager = WindowManagerImpl.createWindowContextWindowManager(this);
        WindowTokenClient token = (WindowTokenClient) getWindowContextToken();
        this.mController = new WindowContextController(token);
        Reference.reachabilityFence(this);
    }

    public void attachToDisplayArea() {
        this.mController.attachToDisplayArea(this.mType, getDisplayId(), this.mOptions);
    }

    @Override // android.content.ContextWrapper, android.content.Context
    public Object getSystemService(String name) {
        if (Context.WINDOW_SERVICE.equals(name)) {
            return this.mWindowManager;
        }
        return super.getSystemService(name);
    }

    protected void finalize() throws Throwable {
        release();
        super.finalize();
    }

    public void release() {
        this.mController.detachIfNeeded();
        destroy();
    }

    @Override // android.content.Context
    public void destroy() {
        try {
            this.mCallbacksController.clearCallbacks();
            getBaseContext().destroy();
        } finally {
            Reference.reachabilityFence(this);
        }
    }

    @Override // android.content.ContextWrapper, android.content.Context
    public void registerComponentCallbacks(ComponentCallbacks callback) {
        this.mCallbacksController.registerCallbacks(callback);
    }

    @Override // android.content.ContextWrapper, android.content.Context
    public void unregisterComponentCallbacks(ComponentCallbacks callback) {
        this.mCallbacksController.unregisterCallbacks(callback);
    }

    public void dispatchConfigurationChanged(Configuration newConfig) {
        this.mCallbacksController.dispatchConfigurationChanged(newConfig);
    }

    @Override // android.window.WindowProvider
    public int getWindowType() {
        return this.mType;
    }

    @Override // android.window.WindowProvider
    public Bundle getWindowContextOptions() {
        return this.mOptions;
    }
}
