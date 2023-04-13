package android.window;

import android.app.ActivityThread;
import android.app.LoadedApk;
import android.app.Service;
import android.content.ComponentCallbacks;
import android.content.ComponentCallbacksController;
import android.content.Context;
import android.content.res.Configuration;
import android.hardware.display.DisplayManager;
import android.p008os.Bundle;
import android.p008os.IBinder;
import android.view.Display;
import android.view.WindowManager;
import android.view.WindowManagerImpl;
/* loaded from: classes4.dex */
public abstract class WindowProviderService extends Service implements WindowProvider {
    private final ComponentCallbacksController mCallbacksController;
    private final WindowContextController mController;
    private boolean mInitialized;
    private final Bundle mOptions;
    private WindowManager mWindowManager;
    private final WindowTokenClient mWindowToken;

    public abstract int getWindowType();

    public static boolean isWindowProviderService(Bundle windowContextOptions) {
        if (windowContextOptions == null) {
            return false;
        }
        return windowContextOptions.getBoolean(WindowProvider.KEY_IS_WINDOW_PROVIDER_SERVICE, false);
    }

    public WindowProviderService() {
        WindowTokenClient windowTokenClient = new WindowTokenClient();
        this.mWindowToken = windowTokenClient;
        this.mController = new WindowContextController(windowTokenClient);
        this.mCallbacksController = new ComponentCallbacksController();
        Bundle bundle = new Bundle();
        this.mOptions = bundle;
        bundle.putBoolean(WindowProvider.KEY_IS_WINDOW_PROVIDER_SERVICE, true);
    }

    public Bundle getWindowContextOptions() {
        return this.mOptions;
    }

    @Override // android.content.ContextWrapper, android.content.Context
    public void registerComponentCallbacks(ComponentCallbacks callback) {
        this.mCallbacksController.registerCallbacks(callback);
    }

    @Override // android.content.ContextWrapper, android.content.Context
    public void unregisterComponentCallbacks(ComponentCallbacks callback) {
        this.mCallbacksController.unregisterCallbacks(callback);
    }

    @Override // android.app.Service, android.content.ComponentCallbacks
    public void onConfigurationChanged(Configuration configuration) {
        this.mCallbacksController.dispatchConfigurationChanged(configuration);
    }

    @Override // android.app.Service, android.content.ComponentCallbacks
    public void onLowMemory() {
        this.mCallbacksController.dispatchLowMemory();
    }

    @Override // android.app.Service, android.content.ComponentCallbacks2
    public void onTrimMemory(int level) {
        this.mCallbacksController.dispatchTrimMemory(level);
    }

    public int getInitialDisplayId() {
        return 0;
    }

    public final void attachToWindowToken(IBinder windowToken) {
        this.mController.attachToWindowToken(windowToken);
    }

    @Override // android.app.Service
    public final Context createServiceBaseContext(ActivityThread mainThread, LoadedApk packageInfo) {
        Context context = super.createServiceBaseContext(mainThread, packageInfo);
        Display display = ((DisplayManager) context.getSystemService(DisplayManager.class)).getDisplay(getInitialDisplayId());
        return context.createTokenContext(this.mWindowToken, display);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.app.Service, android.content.ContextWrapper
    public void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        if (!this.mInitialized) {
            this.mWindowToken.attachContext(this);
            this.mController.attachToDisplayArea(getWindowType(), getDisplayId(), getWindowContextOptions());
            this.mWindowManager = WindowManagerImpl.createWindowContextWindowManager(this);
            this.mInitialized = true;
        }
    }

    @Override // android.content.ContextWrapper, android.content.Context
    public Object getSystemService(String name) {
        if (Context.WINDOW_SERVICE.equals(name)) {
            return this.mWindowManager;
        }
        return super.getSystemService(name);
    }

    @Override // android.app.Service
    public void onDestroy() {
        super.onDestroy();
        this.mController.detachIfNeeded();
        this.mCallbacksController.clearCallbacks();
    }
}
