package android.webkit;

import android.annotation.SystemApi;
import android.app.ActivityThread;
import android.app.Application;
import android.app.ResourcesManager;
import android.content.Context;
import android.content.p001pm.ApplicationInfo;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.RecordingCanvas;
import android.p008os.RemoteException;
import android.p008os.SystemProperties;
import android.p008os.Trace;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewRootImpl;
import android.webkit.WebViewFactory;
import com.android.internal.util.ArrayUtils;
@SystemApi
/* loaded from: classes4.dex */
public final class WebViewDelegate {

    /* loaded from: classes4.dex */
    public interface OnTraceEnabledChangeListener {
        void onTraceEnabledChange(boolean z);
    }

    public void setOnTraceEnabledChangeListener(final OnTraceEnabledChangeListener listener) {
        SystemProperties.addChangeCallback(new Runnable() { // from class: android.webkit.WebViewDelegate.1
            @Override // java.lang.Runnable
            public void run() {
                listener.onTraceEnabledChange(WebViewDelegate.this.isTraceTagEnabled());
            }
        });
    }

    public boolean isTraceTagEnabled() {
        return Trace.isTagEnabled(16L);
    }

    @Deprecated
    public boolean canInvokeDrawGlFunctor(View containerView) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    public void invokeDrawGlFunctor(View containerView, long nativeDrawGLFunctor, boolean waitForCompletion) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    public void callDrawGlFunction(Canvas canvas, long nativeDrawGLFunctor) {
        throw new UnsupportedOperationException();
    }

    @Deprecated
    public void callDrawGlFunction(Canvas canvas, long nativeDrawGLFunctor, Runnable releasedRunnable) {
        throw new UnsupportedOperationException();
    }

    public void drawWebViewFunctor(Canvas canvas, int functor) {
        if (!(canvas instanceof RecordingCanvas)) {
            throw new IllegalArgumentException(canvas.getClass().getName() + " is not a RecordingCanvas canvas");
        }
        ((RecordingCanvas) canvas).drawWebViewFunctor(functor);
    }

    @Deprecated
    public void detachDrawGlFunctor(View containerView, long nativeDrawGLFunctor) {
        ViewRootImpl viewRootImpl = containerView.getViewRootImpl();
        if (nativeDrawGLFunctor != 0 && viewRootImpl != null) {
            viewRootImpl.detachFunctor(nativeDrawGLFunctor);
        }
    }

    public int getPackageId(Resources resources, String packageName) {
        SparseArray<String> packageIdentifiers = resources.getAssets().getAssignedPackageIdentifiers();
        for (int i = 0; i < packageIdentifiers.size(); i++) {
            String name = packageIdentifiers.valueAt(i);
            if (packageName.equals(name)) {
                return packageIdentifiers.keyAt(i);
            }
        }
        throw new RuntimeException("Package not found: " + packageName);
    }

    public Application getApplication() {
        return ActivityThread.currentApplication();
    }

    public String getErrorString(Context context, int errorCode) {
        return LegacyErrorStrings.getString(errorCode, context);
    }

    public void addWebViewAssetPath(Context context) {
        String[] newAssetPaths = WebViewFactory.getLoadedPackageInfo().applicationInfo.getAllApkPaths();
        ApplicationInfo appInfo = context.getApplicationInfo();
        String[] newLibAssets = appInfo.sharedLibraryFiles;
        for (String newAssetPath : newAssetPaths) {
            newLibAssets = (String[]) ArrayUtils.appendElement(String.class, newLibAssets, newAssetPath);
        }
        if (newLibAssets != appInfo.sharedLibraryFiles) {
            appInfo.sharedLibraryFiles = newLibAssets;
            ResourcesManager.getInstance().appendLibAssetsForMainAssetPath(appInfo.getBaseResourcePath(), newAssetPaths);
        }
    }

    public boolean isMultiProcessEnabled() {
        try {
            return WebViewFactory.getUpdateService().isMultiProcessEnabled();
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public String getDataDirectorySuffix() {
        return WebViewFactory.getDataDirectorySuffix();
    }

    public WebViewFactory.StartupTimestamps getStartupTimestamps() {
        return WebViewFactory.getStartupTimestamps();
    }
}
