package com.android.server.appbinding;

import android.app.AppGlobals;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.IPackageManager;
import android.content.pm.ServiceInfo;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.IInterface;
import android.os.UserHandle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Slog;
import android.util.SparseBooleanArray;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.os.BackgroundThread;
import com.android.internal.util.DumpUtils;
import com.android.server.SystemService;
import com.android.server.appbinding.finders.AppServiceFinder;
import com.android.server.appbinding.finders.CarrierMessagingClientServiceFinder;
import com.android.server.p006am.PersistentConnection;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
/* loaded from: classes.dex */
public class AppBindingService extends Binder {
    @GuardedBy({"mLock"})
    public final ArrayList<AppServiceFinder> mApps;
    @GuardedBy({"mLock"})
    public final ArrayList<AppServiceConnection> mConnections;
    @GuardedBy({"mLock"})
    public AppBindingConstants mConstants;
    public final Context mContext;
    public final Handler mHandler;
    public final IPackageManager mIPackageManager;
    public final Injector mInjector;
    public final Object mLock;
    @VisibleForTesting
    final BroadcastReceiver mPackageUserMonitor;
    @GuardedBy({"mLock"})
    public final SparseBooleanArray mRunningUsers;
    public final ContentObserver mSettingsObserver;

    /* loaded from: classes.dex */
    public static class Injector {
        public IPackageManager getIPackageManager() {
            return AppGlobals.getPackageManager();
        }

        public String getGlobalSettingString(ContentResolver contentResolver, String str) {
            return Settings.Global.getString(contentResolver, str);
        }
    }

    /* loaded from: classes.dex */
    public static class Lifecycle extends SystemService {
        public final AppBindingService mService;

        public Lifecycle(Context context) {
            this(context, new Injector());
        }

        public Lifecycle(Context context, Injector injector) {
            super(context);
            this.mService = new AppBindingService(injector, context);
        }

        @Override // com.android.server.SystemService
        public void onStart() {
            publishBinderService("app_binding", this.mService);
        }

        @Override // com.android.server.SystemService
        public void onBootPhase(int i) {
            this.mService.onBootPhase(i);
        }

        @Override // com.android.server.SystemService
        public void onUserStarting(SystemService.TargetUser targetUser) {
            this.mService.onStartUser(targetUser.getUserIdentifier());
        }

        @Override // com.android.server.SystemService
        public void onUserUnlocking(SystemService.TargetUser targetUser) {
            this.mService.onUnlockUser(targetUser.getUserIdentifier());
        }

        @Override // com.android.server.SystemService
        public void onUserStopping(SystemService.TargetUser targetUser) {
            this.mService.onStopUser(targetUser.getUserIdentifier());
        }
    }

    public AppBindingService(Injector injector, Context context) {
        this.mLock = new Object();
        this.mRunningUsers = new SparseBooleanArray(2);
        ArrayList<AppServiceFinder> arrayList = new ArrayList<>();
        this.mApps = arrayList;
        this.mConnections = new ArrayList<>();
        this.mSettingsObserver = new ContentObserver(null) { // from class: com.android.server.appbinding.AppBindingService.1
            @Override // android.database.ContentObserver
            public void onChange(boolean z) {
                AppBindingService.this.refreshConstants();
            }
        };
        this.mPackageUserMonitor = new BroadcastReceiver() { // from class: com.android.server.appbinding.AppBindingService.2
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                int intExtra = intent.getIntExtra("android.intent.extra.user_handle", -10000);
                if (intExtra == -10000) {
                    Slog.w("AppBindingService", "Intent broadcast does not contain user handle: " + intent);
                    return;
                }
                String action = intent.getAction();
                if ("android.intent.action.USER_REMOVED".equals(action)) {
                    AppBindingService.this.onUserRemoved(intExtra);
                    return;
                }
                Uri data = intent.getData();
                String schemeSpecificPart = data != null ? data.getSchemeSpecificPart() : null;
                if (schemeSpecificPart == null) {
                    Slog.w("AppBindingService", "Intent broadcast does not contain package name: " + intent);
                    return;
                }
                boolean booleanExtra = intent.getBooleanExtra("android.intent.extra.REPLACING", false);
                action.hashCode();
                if (action.equals("android.intent.action.PACKAGE_CHANGED")) {
                    AppBindingService.this.handlePackageAddedReplacing(schemeSpecificPart, intExtra);
                } else if (action.equals("android.intent.action.PACKAGE_ADDED") && booleanExtra) {
                    AppBindingService.this.handlePackageAddedReplacing(schemeSpecificPart, intExtra);
                }
            }
        };
        this.mInjector = injector;
        this.mContext = context;
        this.mIPackageManager = injector.getIPackageManager();
        Handler handler = BackgroundThread.getHandler();
        this.mHandler = handler;
        arrayList.add(new CarrierMessagingClientServiceFinder(context, new BiConsumer() { // from class: com.android.server.appbinding.AppBindingService$$ExternalSyntheticLambda4
            @Override // java.util.function.BiConsumer
            public final void accept(Object obj, Object obj2) {
                AppBindingService.this.onAppChanged((AppServiceFinder) obj, ((Integer) obj2).intValue());
            }
        }, handler));
        this.mConstants = AppBindingConstants.initializeFromString("");
    }

    public final void forAllAppsLocked(Consumer<AppServiceFinder> consumer) {
        for (int i = 0; i < this.mApps.size(); i++) {
            consumer.accept(this.mApps.get(i));
        }
    }

    public final void onBootPhase(int i) {
        if (i == 550) {
            onPhaseActivityManagerReady();
        } else if (i != 600) {
        } else {
            onPhaseThirdPartyAppsCanStart();
        }
    }

    public final void onPhaseActivityManagerReady() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.PACKAGE_ADDED");
        intentFilter.addAction("android.intent.action.PACKAGE_CHANGED");
        intentFilter.addDataScheme("package");
        this.mContext.registerReceiverAsUser(this.mPackageUserMonitor, UserHandle.ALL, intentFilter, null, this.mHandler);
        IntentFilter intentFilter2 = new IntentFilter();
        intentFilter2.addAction("android.intent.action.USER_REMOVED");
        this.mContext.registerReceiverAsUser(this.mPackageUserMonitor, UserHandle.ALL, intentFilter2, null, this.mHandler);
        this.mContext.getContentResolver().registerContentObserver(Settings.Global.getUriFor("app_binding_constants"), false, this.mSettingsObserver);
        refreshConstants();
    }

    public final void refreshConstants() {
        String globalSettingString = this.mInjector.getGlobalSettingString(this.mContext.getContentResolver(), "app_binding_constants");
        synchronized (this.mLock) {
            if (TextUtils.equals(this.mConstants.sourceSettings, globalSettingString)) {
                return;
            }
            Slog.i("AppBindingService", "Updating constants with: " + globalSettingString);
            this.mConstants = AppBindingConstants.initializeFromString(globalSettingString);
            rebindAllLocked("settings update");
        }
    }

    public final void onPhaseThirdPartyAppsCanStart() {
        synchronized (this.mLock) {
            forAllAppsLocked(new Consumer() { // from class: com.android.server.appbinding.AppBindingService$$ExternalSyntheticLambda1
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    ((AppServiceFinder) obj).startMonitoring();
                }
            });
        }
    }

    public final void onStartUser(int i) {
        synchronized (this.mLock) {
            this.mRunningUsers.append(i, true);
            bindServicesLocked(i, null, "user start");
        }
    }

    public final void onUnlockUser(int i) {
        synchronized (this.mLock) {
            bindServicesLocked(i, null, "user unlock");
        }
    }

    public final void onStopUser(int i) {
        synchronized (this.mLock) {
            unbindServicesLocked(i, null, "user stop");
            this.mRunningUsers.delete(i);
        }
    }

    public final void onUserRemoved(final int i) {
        synchronized (this.mLock) {
            forAllAppsLocked(new Consumer() { // from class: com.android.server.appbinding.AppBindingService$$ExternalSyntheticLambda2
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    ((AppServiceFinder) obj).onUserRemoved(i);
                }
            });
            this.mRunningUsers.delete(i);
        }
    }

    public final void onAppChanged(AppServiceFinder appServiceFinder, int i) {
        synchronized (this.mLock) {
            String str = appServiceFinder.getAppDescription() + " changed";
            unbindServicesLocked(i, appServiceFinder, str);
            bindServicesLocked(i, appServiceFinder, str);
        }
    }

    public final AppServiceFinder findFinderLocked(int i, String str) {
        for (int i2 = 0; i2 < this.mApps.size(); i2++) {
            AppServiceFinder appServiceFinder = this.mApps.get(i2);
            if (str.equals(appServiceFinder.getTargetPackage(i))) {
                return appServiceFinder;
            }
        }
        return null;
    }

    public final AppServiceConnection findConnectionLock(int i, AppServiceFinder appServiceFinder) {
        for (int i2 = 0; i2 < this.mConnections.size(); i2++) {
            AppServiceConnection appServiceConnection = this.mConnections.get(i2);
            if (appServiceConnection.getUserId() == i && appServiceConnection.getFinder() == appServiceFinder) {
                return appServiceConnection;
            }
        }
        return null;
    }

    public final void handlePackageAddedReplacing(String str, int i) {
        synchronized (this.mLock) {
            AppServiceFinder findFinderLocked = findFinderLocked(i, str);
            if (findFinderLocked != null) {
                unbindServicesLocked(i, findFinderLocked, "package update");
                bindServicesLocked(i, findFinderLocked, "package update");
            }
        }
    }

    public final void rebindAllLocked(String str) {
        for (int i = 0; i < this.mRunningUsers.size(); i++) {
            if (this.mRunningUsers.valueAt(i)) {
                int keyAt = this.mRunningUsers.keyAt(i);
                unbindServicesLocked(keyAt, null, str);
                bindServicesLocked(keyAt, null, str);
            }
        }
    }

    public final void bindServicesLocked(int i, AppServiceFinder appServiceFinder, String str) {
        for (int i2 = 0; i2 < this.mApps.size(); i2++) {
            AppServiceFinder appServiceFinder2 = this.mApps.get(i2);
            if (appServiceFinder == null || appServiceFinder == appServiceFinder2) {
                if (findConnectionLock(i, appServiceFinder2) != null) {
                    unbindServicesLocked(i, appServiceFinder, str);
                }
                ServiceInfo findService = appServiceFinder2.findService(i, this.mIPackageManager, this.mConstants);
                if (findService != null) {
                    AppServiceConnection appServiceConnection = new AppServiceConnection(this.mContext, i, this.mConstants, this.mHandler, appServiceFinder2, findService.getComponentName());
                    this.mConnections.add(appServiceConnection);
                    appServiceConnection.bind();
                }
            }
        }
    }

    public final void unbindServicesLocked(int i, AppServiceFinder appServiceFinder, String str) {
        for (int size = this.mConnections.size() - 1; size >= 0; size--) {
            AppServiceConnection appServiceConnection = this.mConnections.get(size);
            if (appServiceConnection.getUserId() == i && (appServiceFinder == null || appServiceConnection.getFinder() == appServiceFinder)) {
                this.mConnections.remove(size);
                appServiceConnection.unbind();
            }
        }
    }

    /* loaded from: classes.dex */
    public static class AppServiceConnection extends PersistentConnection<IInterface> {
        public final AppBindingConstants mConstants;
        public final AppServiceFinder mFinder;

        public AppServiceConnection(Context context, int i, AppBindingConstants appBindingConstants, Handler handler, AppServiceFinder appServiceFinder, ComponentName componentName) {
            super("AppBindingService", context, handler, i, componentName, appBindingConstants.SERVICE_RECONNECT_BACKOFF_SEC, appBindingConstants.SERVICE_RECONNECT_BACKOFF_INCREASE, appBindingConstants.SERVICE_RECONNECT_MAX_BACKOFF_SEC, appBindingConstants.SERVICE_STABLE_CONNECTION_THRESHOLD_SEC);
            this.mFinder = appServiceFinder;
            this.mConstants = appBindingConstants;
        }

        @Override // com.android.server.p006am.PersistentConnection
        public int getBindFlags() {
            return this.mFinder.getBindFlags(this.mConstants);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // com.android.server.p006am.PersistentConnection
        public IInterface asInterface(IBinder iBinder) {
            return this.mFinder.asInterface(iBinder);
        }

        public AppServiceFinder getFinder() {
            return this.mFinder;
        }
    }

    @Override // android.os.Binder
    public void dump(FileDescriptor fileDescriptor, final PrintWriter printWriter, String[] strArr) {
        if (DumpUtils.checkDumpPermission(this.mContext, "AppBindingService", printWriter)) {
            if (strArr.length > 0 && "-s".equals(strArr[0])) {
                dumpSimple(printWriter);
                return;
            }
            synchronized (this.mLock) {
                this.mConstants.dump("  ", printWriter);
                printWriter.println();
                printWriter.print("  Running users:");
                for (int i = 0; i < this.mRunningUsers.size(); i++) {
                    if (this.mRunningUsers.valueAt(i)) {
                        printWriter.print(" ");
                        printWriter.print(this.mRunningUsers.keyAt(i));
                    }
                }
                printWriter.println();
                printWriter.println("  Connections:");
                for (int i2 = 0; i2 < this.mConnections.size(); i2++) {
                    AppServiceConnection appServiceConnection = this.mConnections.get(i2);
                    printWriter.print("    App type: ");
                    printWriter.print(appServiceConnection.getFinder().getAppDescription());
                    printWriter.println();
                    appServiceConnection.dump("      ", printWriter);
                }
                if (this.mConnections.size() == 0) {
                    printWriter.println("    None:");
                }
                printWriter.println();
                printWriter.println("  Finders:");
                forAllAppsLocked(new Consumer() { // from class: com.android.server.appbinding.AppBindingService$$ExternalSyntheticLambda0
                    @Override // java.util.function.Consumer
                    public final void accept(Object obj) {
                        ((AppServiceFinder) obj).dump("    ", printWriter);
                    }
                });
            }
        }
    }

    public final void dumpSimple(final PrintWriter printWriter) {
        synchronized (this.mLock) {
            for (int i = 0; i < this.mConnections.size(); i++) {
                AppServiceConnection appServiceConnection = this.mConnections.get(i);
                printWriter.print("conn,");
                printWriter.print(appServiceConnection.getFinder().getAppDescription());
                printWriter.print(",");
                printWriter.print(appServiceConnection.getUserId());
                printWriter.print(",");
                printWriter.print(appServiceConnection.getComponentName().getPackageName());
                printWriter.print(",");
                printWriter.print(appServiceConnection.getComponentName().getClassName());
                printWriter.print(",");
                printWriter.print(appServiceConnection.isBound() ? "bound" : "not-bound");
                printWriter.print(",");
                printWriter.print(appServiceConnection.isConnected() ? "connected" : "not-connected");
                printWriter.print(",#con=");
                printWriter.print(appServiceConnection.getNumConnected());
                printWriter.print(",#dis=");
                printWriter.print(appServiceConnection.getNumDisconnected());
                printWriter.print(",#died=");
                printWriter.print(appServiceConnection.getNumBindingDied());
                printWriter.print(",backoff=");
                printWriter.print(appServiceConnection.getNextBackoffMs());
                printWriter.println();
            }
            forAllAppsLocked(new Consumer() { // from class: com.android.server.appbinding.AppBindingService$$ExternalSyntheticLambda3
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    ((AppServiceFinder) obj).dumpSimple(printWriter);
                }
            });
        }
    }
}
