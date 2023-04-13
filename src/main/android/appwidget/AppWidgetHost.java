package android.appwidget;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.p001pm.PackageManager;
import android.p008os.Binder;
import android.p008os.Bundle;
import android.p008os.Handler;
import android.p008os.IBinder;
import android.p008os.Looper;
import android.p008os.Message;
import android.p008os.Process;
import android.p008os.RemoteException;
import android.p008os.ServiceManager;
import android.util.DisplayMetrics;
import android.util.SparseArray;
import android.widget.RemoteViews;
import com.android.internal.C4057R;
import com.android.internal.appwidget.IAppWidgetHost;
import com.android.internal.appwidget.IAppWidgetService;
import java.lang.ref.WeakReference;
import java.util.List;
/* loaded from: classes.dex */
public class AppWidgetHost {
    static final int HANDLE_APP_WIDGET_REMOVED = 5;
    static final int HANDLE_PROVIDERS_CHANGED = 3;
    static final int HANDLE_PROVIDER_CHANGED = 2;
    static final int HANDLE_UPDATE = 1;
    static final int HANDLE_VIEW_DATA_CHANGED = 4;
    static IAppWidgetService sService;
    private final Callbacks mCallbacks;
    private String mContextOpPackageName;
    private DisplayMetrics mDisplayMetrics;
    private final Handler mHandler;
    private final int mHostId;
    private RemoteViews.InteractionHandler mInteractionHandler;
    private final SparseArray<AppWidgetHostListener> mListeners;
    static final Object sServiceLock = new Object();
    static boolean sServiceInitialized = false;

    /* loaded from: classes.dex */
    public interface AppWidgetHostListener {
        void onUpdateProviderInfo(AppWidgetProviderInfo appWidgetProviderInfo);

        void onViewDataChanged(int i);

        void updateAppWidget(RemoteViews remoteViews);
    }

    /* loaded from: classes.dex */
    static class Callbacks extends IAppWidgetHost.Stub {
        private final WeakReference<Handler> mWeakHandler;

        public Callbacks(Handler handler) {
            this.mWeakHandler = new WeakReference<>(handler);
        }

        @Override // com.android.internal.appwidget.IAppWidgetHost
        public void updateAppWidget(int appWidgetId, RemoteViews views) {
            if (isLocalBinder() && views != null) {
                views = views.mo583clone();
            }
            Handler handler = this.mWeakHandler.get();
            if (handler == null) {
                return;
            }
            Message msg = handler.obtainMessage(1, appWidgetId, 0, views);
            msg.sendToTarget();
        }

        @Override // com.android.internal.appwidget.IAppWidgetHost
        public void providerChanged(int appWidgetId, AppWidgetProviderInfo info) {
            if (isLocalBinder() && info != null) {
                info = info.m864clone();
            }
            Handler handler = this.mWeakHandler.get();
            if (handler == null) {
                return;
            }
            Message msg = handler.obtainMessage(2, appWidgetId, 0, info);
            msg.sendToTarget();
        }

        @Override // com.android.internal.appwidget.IAppWidgetHost
        public void appWidgetRemoved(int appWidgetId) {
            Handler handler = this.mWeakHandler.get();
            if (handler == null) {
                return;
            }
            handler.obtainMessage(5, appWidgetId, 0).sendToTarget();
        }

        @Override // com.android.internal.appwidget.IAppWidgetHost
        public void providersChanged() {
            Handler handler = this.mWeakHandler.get();
            if (handler == null) {
                return;
            }
            handler.obtainMessage(3).sendToTarget();
        }

        @Override // com.android.internal.appwidget.IAppWidgetHost
        public void viewDataChanged(int appWidgetId, int viewId) {
            Handler handler = this.mWeakHandler.get();
            if (handler == null) {
                return;
            }
            Message msg = handler.obtainMessage(4, appWidgetId, viewId);
            msg.sendToTarget();
        }

        private static boolean isLocalBinder() {
            return Process.myPid() == Binder.getCallingPid();
        }
    }

    /* loaded from: classes.dex */
    class UpdateHandler extends Handler {
        public UpdateHandler(Looper looper) {
            super(looper);
        }

        @Override // android.p008os.Handler
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    AppWidgetHost.this.updateAppWidgetView(msg.arg1, (RemoteViews) msg.obj);
                    return;
                case 2:
                    AppWidgetHost.this.onProviderChanged(msg.arg1, (AppWidgetProviderInfo) msg.obj);
                    return;
                case 3:
                    AppWidgetHost.this.onProvidersChanged();
                    return;
                case 4:
                    AppWidgetHost.this.viewDataChanged(msg.arg1, msg.arg2);
                    return;
                case 5:
                    AppWidgetHost.this.dispatchOnAppWidgetRemoved(msg.arg1);
                    return;
                default:
                    return;
            }
        }
    }

    public AppWidgetHost(Context context, int hostId) {
        this(context, hostId, null, context.getMainLooper());
    }

    private AppWidgetHostListener getListener(int appWidgetId) {
        AppWidgetHostListener tempListener;
        synchronized (this.mListeners) {
            tempListener = this.mListeners.get(appWidgetId);
        }
        return tempListener;
    }

    public AppWidgetHost(Context context, int hostId, RemoteViews.InteractionHandler handler, Looper looper) {
        this.mListeners = new SparseArray<>();
        this.mContextOpPackageName = context.getOpPackageName();
        this.mHostId = hostId;
        this.mInteractionHandler = handler;
        UpdateHandler updateHandler = new UpdateHandler(looper);
        this.mHandler = updateHandler;
        this.mCallbacks = new Callbacks(updateHandler);
        this.mDisplayMetrics = context.getResources().getDisplayMetrics();
        bindService(context);
    }

    private static void bindService(Context context) {
        synchronized (sServiceLock) {
            if (sServiceInitialized) {
                return;
            }
            sServiceInitialized = true;
            PackageManager packageManager = context.getPackageManager();
            if (packageManager.hasSystemFeature(PackageManager.FEATURE_APP_WIDGETS) || context.getResources().getBoolean(C4057R.bool.config_enableAppWidgetService)) {
                IBinder b = ServiceManager.getService(Context.APPWIDGET_SERVICE);
                sService = IAppWidgetService.Stub.asInterface(b);
            }
        }
    }

    public void startListening() {
        int[] idsToUpdate;
        if (sService == null) {
            return;
        }
        synchronized (this.mListeners) {
            int n = this.mListeners.size();
            idsToUpdate = new int[n];
            for (int i = 0; i < n; i++) {
                idsToUpdate[i] = this.mListeners.keyAt(i);
            }
        }
        try {
            List<PendingHostUpdate> updates = sService.startListening(this.mCallbacks, this.mContextOpPackageName, this.mHostId, idsToUpdate).getList();
            int N = updates.size();
            for (int i2 = 0; i2 < N; i2++) {
                PendingHostUpdate update = updates.get(i2);
                switch (update.type) {
                    case 0:
                        updateAppWidgetView(update.appWidgetId, update.views);
                        break;
                    case 1:
                        onProviderChanged(update.appWidgetId, update.widgetInfo);
                        break;
                    case 2:
                        viewDataChanged(update.appWidgetId, update.viewId);
                        break;
                    case 3:
                        dispatchOnAppWidgetRemoved(update.appWidgetId);
                        break;
                }
            }
        } catch (RemoteException e) {
            throw new RuntimeException("system server dead?", e);
        }
    }

    public void stopListening() {
        IAppWidgetService iAppWidgetService = sService;
        if (iAppWidgetService == null) {
            return;
        }
        try {
            iAppWidgetService.stopListening(this.mContextOpPackageName, this.mHostId);
        } catch (RemoteException e) {
            throw new RuntimeException("system server dead?", e);
        }
    }

    public int allocateAppWidgetId() {
        IAppWidgetService iAppWidgetService = sService;
        if (iAppWidgetService == null) {
            return -1;
        }
        try {
            return iAppWidgetService.allocateAppWidgetId(this.mContextOpPackageName, this.mHostId);
        } catch (RemoteException e) {
            throw new RuntimeException("system server dead?", e);
        }
    }

    public final void startAppWidgetConfigureActivityForResult(Activity activity, int appWidgetId, int intentFlags, int requestCode, Bundle options) {
        IAppWidgetService iAppWidgetService = sService;
        if (iAppWidgetService == null) {
            return;
        }
        try {
            IntentSender intentSender = iAppWidgetService.createAppWidgetConfigIntentSender(this.mContextOpPackageName, appWidgetId, intentFlags);
            if (intentSender != null) {
                activity.startIntentSenderForResult(intentSender, requestCode, (Intent) null, 0, 0, 0, options);
                return;
            }
            throw new ActivityNotFoundException();
        } catch (IntentSender.SendIntentException e) {
            throw new ActivityNotFoundException();
        } catch (RemoteException e2) {
            throw new RuntimeException("system server dead?", e2);
        }
    }

    public void setAppWidgetHidden() {
        IAppWidgetService iAppWidgetService = sService;
        if (iAppWidgetService == null) {
            return;
        }
        try {
            iAppWidgetService.setAppWidgetHidden(this.mContextOpPackageName, this.mHostId);
        } catch (RemoteException e) {
            throw new RuntimeException("System server dead?", e);
        }
    }

    public void setInteractionHandler(RemoteViews.InteractionHandler interactionHandler) {
        this.mInteractionHandler = interactionHandler;
    }

    public int[] getAppWidgetIds() {
        IAppWidgetService iAppWidgetService = sService;
        if (iAppWidgetService == null) {
            return new int[0];
        }
        try {
            return iAppWidgetService.getAppWidgetIdsForHost(this.mContextOpPackageName, this.mHostId);
        } catch (RemoteException e) {
            throw new RuntimeException("system server dead?", e);
        }
    }

    public void deleteAppWidgetId(int appWidgetId) {
        if (sService == null) {
            return;
        }
        removeListener(appWidgetId);
        try {
            sService.deleteAppWidgetId(this.mContextOpPackageName, appWidgetId);
        } catch (RemoteException e) {
            throw new RuntimeException("system server dead?", e);
        }
    }

    public void deleteHost() {
        IAppWidgetService iAppWidgetService = sService;
        if (iAppWidgetService == null) {
            return;
        }
        try {
            iAppWidgetService.deleteHost(this.mContextOpPackageName, this.mHostId);
        } catch (RemoteException e) {
            throw new RuntimeException("system server dead?", e);
        }
    }

    public static void deleteAllHosts() {
        IAppWidgetService iAppWidgetService = sService;
        if (iAppWidgetService == null) {
            return;
        }
        try {
            iAppWidgetService.deleteAllHosts();
        } catch (RemoteException e) {
            throw new RuntimeException("system server dead?", e);
        }
    }

    public final AppWidgetHostView createView(Context context, int appWidgetId, AppWidgetProviderInfo appWidget) {
        if (sService == null) {
            return null;
        }
        AppWidgetHostView view = onCreateView(context, appWidgetId, appWidget);
        view.setInteractionHandler(this.mInteractionHandler);
        view.setAppWidget(appWidgetId, appWidget);
        setListener(appWidgetId, view);
        return view;
    }

    protected AppWidgetHostView onCreateView(Context context, int appWidgetId, AppWidgetProviderInfo appWidget) {
        return new AppWidgetHostView(context, this.mInteractionHandler);
    }

    protected void onProviderChanged(int appWidgetId, AppWidgetProviderInfo appWidget) {
        AppWidgetHostListener v = getListener(appWidgetId);
        appWidget.updateDimensions(this.mDisplayMetrics);
        if (v != null) {
            v.onUpdateProviderInfo(appWidget);
        }
    }

    void dispatchOnAppWidgetRemoved(int appWidgetId) {
        removeListener(appWidgetId);
        onAppWidgetRemoved(appWidgetId);
    }

    public void onAppWidgetRemoved(int appWidgetId) {
    }

    protected void onProvidersChanged() {
    }

    public void setListener(int appWidgetId, AppWidgetHostListener listener) {
        synchronized (this.mListeners) {
            this.mListeners.put(appWidgetId, listener);
        }
        try {
            RemoteViews views = sService.getAppWidgetViews(this.mContextOpPackageName, appWidgetId);
            listener.updateAppWidget(views);
        } catch (RemoteException e) {
            throw new RuntimeException("system server dead?", e);
        }
    }

    public void removeListener(int appWidgetId) {
        synchronized (this.mListeners) {
            this.mListeners.remove(appWidgetId);
        }
    }

    void updateAppWidgetView(int appWidgetId, RemoteViews views) {
        AppWidgetHostListener v = getListener(appWidgetId);
        if (v != null) {
            v.updateAppWidget(views);
        }
    }

    void viewDataChanged(int appWidgetId, int viewId) {
        AppWidgetHostListener v = getListener(appWidgetId);
        if (v != null) {
            v.onViewDataChanged(viewId);
        }
    }

    protected void clearViews() {
        synchronized (this.mListeners) {
            this.mListeners.clear();
        }
    }
}
