package android.window;

import android.app.ActivityThread;
import android.app.IWindowToken;
import android.app.ResourcesManager;
import android.content.Context;
import android.content.res.CompatibilityInfo;
import android.content.res.Configuration;
import android.inputmethodservice.AbstractInputMethodService;
import android.p008os.Build;
import android.p008os.Bundle;
import android.p008os.Debug;
import android.p008os.Handler;
import android.p008os.IBinder;
import android.p008os.RemoteException;
import android.util.Log;
import android.view.IWindowManager;
import android.view.WindowManagerGlobal;
import com.android.internal.util.function.TriConsumer;
import com.android.internal.util.function.pooled.PooledLambda;
import java.lang.ref.WeakReference;
import java.util.function.Consumer;
/* loaded from: classes4.dex */
public class WindowTokenClient extends IWindowToken.Stub {
    private static final String TAG = WindowTokenClient.class.getSimpleName();
    private boolean mAttachToWindowContainer;
    private boolean mShouldDumpConfigForIme;
    private IWindowManager mWms;
    private WeakReference<Context> mContextRef = null;
    private final ResourcesManager mResourcesManager = ResourcesManager.getInstance();
    private final Configuration mConfiguration = new Configuration();
    private final Handler mHandler = ActivityThread.currentActivityThread().getHandler();

    public void attachContext(Context context) {
        if (this.mContextRef != null) {
            throw new IllegalStateException("Context is already attached.");
        }
        this.mContextRef = new WeakReference<>(context);
        this.mShouldDumpConfigForIme = Build.IS_DEBUGGABLE && (context instanceof AbstractInputMethodService);
    }

    public boolean attachToDisplayArea(int type, int displayId, Bundle options) {
        try {
            Configuration configuration = getWindowManagerService().attachWindowContextToDisplayArea(this, type, displayId, options);
            if (configuration == null) {
                return false;
            }
            onConfigurationChanged(configuration, displayId, false);
            this.mAttachToWindowContainer = true;
            return true;
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public boolean attachToDisplayContent(int displayId) {
        IWindowManager wms = getWindowManagerService();
        if (wms == null) {
            return false;
        }
        try {
            Configuration configuration = wms.attachToDisplayContent(this, displayId);
            if (configuration == null) {
                return false;
            }
            onConfigurationChanged(configuration, displayId, false);
            this.mAttachToWindowContainer = true;
            return true;
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void attachToWindowToken(IBinder windowToken) {
        try {
            getWindowManagerService().attachWindowContextToWindowToken(this, windowToken);
            this.mAttachToWindowContainer = true;
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void detachFromWindowContainerIfNeeded() {
        if (!this.mAttachToWindowContainer) {
            return;
        }
        try {
            getWindowManagerService().detachWindowContextFromWindowContainer(this);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    private IWindowManager getWindowManagerService() {
        if (this.mWms == null) {
            this.mWms = WindowManagerGlobal.getWindowManagerService();
        }
        return this.mWms;
    }

    @Override // android.app.IWindowToken
    public void onConfigurationChanged(Configuration newConfig, int newDisplayId) {
        this.mHandler.post(PooledLambda.obtainRunnable(new TriConsumer() { // from class: android.window.WindowTokenClient$$ExternalSyntheticLambda1
            @Override // com.android.internal.util.function.TriConsumer
            public final void accept(Object obj, Object obj2, Object obj3) {
                WindowTokenClient.this.onConfigurationChanged((Configuration) obj, ((Integer) obj2).intValue(), ((Boolean) obj3).booleanValue());
            }
        }, newConfig, Integer.valueOf(newDisplayId), true).recycleOnUse());
    }

    public void onConfigurationChanged(Configuration newConfig, int newDisplayId, boolean shouldReportConfigChange) {
        boolean displayChanged;
        boolean shouldUpdateResources;
        int diff;
        Configuration currentConfig;
        Context context = this.mContextRef.get();
        if (context == null) {
            return;
        }
        CompatibilityInfo.applyOverrideScaleIfNeeded(newConfig);
        synchronized (this.mConfiguration) {
            displayChanged = ConfigurationHelper.isDifferentDisplay(context.getDisplayId(), newDisplayId);
            shouldUpdateResources = ConfigurationHelper.shouldUpdateResources(this, this.mConfiguration, newConfig, newConfig, displayChanged, null);
            diff = this.mConfiguration.diffPublicOnly(newConfig);
            currentConfig = this.mShouldDumpConfigForIme ? new Configuration(this.mConfiguration) : null;
            if (shouldUpdateResources) {
                this.mConfiguration.setTo(newConfig);
            }
        }
        if (!shouldUpdateResources && this.mShouldDumpConfigForIme) {
            Log.m112d(TAG, "Configuration not dispatch to IME because configuration is up to date. Current config=" + context.getResources().getConfiguration() + ", reported config=" + currentConfig + ", updated config=" + newConfig);
        }
        if (shouldUpdateResources) {
            this.mResourcesManager.updateResourcesForActivity(this, newConfig, newDisplayId);
            if (shouldReportConfigChange && (context instanceof WindowContext)) {
                WindowContext windowContext = (WindowContext) context;
                windowContext.dispatchConfigurationChanged(newConfig);
            }
            if (shouldReportConfigChange && diff != 0 && (context instanceof WindowProviderService)) {
                WindowProviderService windowProviderService = (WindowProviderService) context;
                windowProviderService.onConfigurationChanged(newConfig);
            }
            ConfigurationHelper.freeTextLayoutCachesIfNeeded(diff);
            if (this.mShouldDumpConfigForIme) {
                if (!shouldReportConfigChange) {
                    Log.m112d(TAG, "Only apply configuration update to Resources because shouldReportConfigChange is false.\n" + Debug.getCallers(5));
                } else if (diff == 0) {
                    Log.m112d(TAG, "Configuration not dispatch to IME because configuration has no  public difference with updated config.  Current config=" + context.getResources().getConfiguration() + ", reported config=" + currentConfig + ", updated config=" + newConfig);
                }
            }
        }
        if (displayChanged) {
            context.updateDisplay(newDisplayId);
        }
    }

    @Override // android.app.IWindowToken
    public void onWindowTokenRemoved() {
        this.mHandler.post(PooledLambda.obtainRunnable(new Consumer() { // from class: android.window.WindowTokenClient$$ExternalSyntheticLambda0
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                ((WindowTokenClient) obj).onWindowTokenRemovedInner();
            }
        }, this).recycleOnUse());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onWindowTokenRemovedInner() {
        Context context = this.mContextRef.get();
        if (context != null) {
            context.destroy();
            this.mContextRef.clear();
        }
    }
}
