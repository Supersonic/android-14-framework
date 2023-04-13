package com.android.server.media;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.os.Handler;
import android.os.UserHandle;
import android.util.Log;
import android.util.Slog;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
/* loaded from: classes2.dex */
public final class RemoteDisplayProviderWatcher {
    public static final boolean DEBUG = Log.isLoggable("RemoteDisplayProvider", 3);
    public final Callback mCallback;
    public final Context mContext;
    public final Handler mHandler;
    public final PackageManager mPackageManager;
    public boolean mRunning;
    public final int mUserId;
    public final ArrayList<RemoteDisplayProviderProxy> mProviders = new ArrayList<>();
    public final BroadcastReceiver mScanPackagesReceiver = new BroadcastReceiver() { // from class: com.android.server.media.RemoteDisplayProviderWatcher.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if (RemoteDisplayProviderWatcher.DEBUG) {
                Slog.d("RemoteDisplayProvider", "Received package manager broadcast: " + intent);
            }
            RemoteDisplayProviderWatcher.this.scanPackages();
        }
    };
    public final Runnable mScanPackagesRunnable = new Runnable() { // from class: com.android.server.media.RemoteDisplayProviderWatcher.2
        @Override // java.lang.Runnable
        public void run() {
            RemoteDisplayProviderWatcher.this.scanPackages();
        }
    };

    /* loaded from: classes2.dex */
    public interface Callback {
        void addProvider(RemoteDisplayProviderProxy remoteDisplayProviderProxy);

        void removeProvider(RemoteDisplayProviderProxy remoteDisplayProviderProxy);
    }

    public RemoteDisplayProviderWatcher(Context context, Callback callback, Handler handler, int i) {
        this.mContext = context;
        this.mCallback = callback;
        this.mHandler = handler;
        this.mUserId = i;
        this.mPackageManager = context.getPackageManager();
    }

    public void dump(PrintWriter printWriter, String str) {
        printWriter.println(str + "Watcher");
        printWriter.println(str + "  mUserId=" + this.mUserId);
        printWriter.println(str + "  mRunning=" + this.mRunning);
        printWriter.println(str + "  mProviders.size()=" + this.mProviders.size());
    }

    public void start() {
        if (this.mRunning) {
            return;
        }
        this.mRunning = true;
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.PACKAGE_ADDED");
        intentFilter.addAction("android.intent.action.PACKAGE_REMOVED");
        intentFilter.addAction("android.intent.action.PACKAGE_CHANGED");
        intentFilter.addAction("android.intent.action.PACKAGE_REPLACED");
        intentFilter.addAction("android.intent.action.PACKAGE_RESTARTED");
        intentFilter.addDataScheme("package");
        this.mContext.registerReceiverAsUser(this.mScanPackagesReceiver, new UserHandle(this.mUserId), intentFilter, null, this.mHandler);
        this.mHandler.post(this.mScanPackagesRunnable);
    }

    public void stop() {
        if (this.mRunning) {
            this.mRunning = false;
            this.mContext.unregisterReceiver(this.mScanPackagesReceiver);
            this.mHandler.removeCallbacks(this.mScanPackagesRunnable);
            for (int size = this.mProviders.size() - 1; size >= 0; size--) {
                this.mProviders.get(size).stop();
            }
        }
    }

    public final void scanPackages() {
        int i;
        if (this.mRunning) {
            int i2 = 0;
            for (ResolveInfo resolveInfo : this.mPackageManager.queryIntentServicesAsUser(new Intent("com.android.media.remotedisplay.RemoteDisplayProvider"), 0, this.mUserId)) {
                ServiceInfo serviceInfo = resolveInfo.serviceInfo;
                if (serviceInfo != null && verifyServiceTrusted(serviceInfo)) {
                    int findProvider = findProvider(serviceInfo.packageName, serviceInfo.name);
                    if (findProvider < 0) {
                        RemoteDisplayProviderProxy remoteDisplayProviderProxy = new RemoteDisplayProviderProxy(this.mContext, new ComponentName(serviceInfo.packageName, serviceInfo.name), this.mUserId);
                        remoteDisplayProviderProxy.start();
                        i = i2 + 1;
                        this.mProviders.add(i2, remoteDisplayProviderProxy);
                        this.mCallback.addProvider(remoteDisplayProviderProxy);
                    } else if (findProvider >= i2) {
                        RemoteDisplayProviderProxy remoteDisplayProviderProxy2 = this.mProviders.get(findProvider);
                        remoteDisplayProviderProxy2.start();
                        remoteDisplayProviderProxy2.rebindIfDisconnected();
                        i = i2 + 1;
                        Collections.swap(this.mProviders, findProvider, i2);
                    }
                    i2 = i;
                }
            }
            if (i2 < this.mProviders.size()) {
                for (int size = this.mProviders.size() - 1; size >= i2; size--) {
                    RemoteDisplayProviderProxy remoteDisplayProviderProxy3 = this.mProviders.get(size);
                    this.mCallback.removeProvider(remoteDisplayProviderProxy3);
                    this.mProviders.remove(remoteDisplayProviderProxy3);
                    remoteDisplayProviderProxy3.stop();
                }
            }
        }
    }

    public final boolean verifyServiceTrusted(ServiceInfo serviceInfo) {
        String str = serviceInfo.permission;
        if (str == null || !str.equals("android.permission.BIND_REMOTE_DISPLAY")) {
            Slog.w("RemoteDisplayProvider", "Ignoring remote display provider service because it did not require the BIND_REMOTE_DISPLAY permission in its manifest: " + serviceInfo.packageName + "/" + serviceInfo.name);
            return false;
        } else if (this.mPackageManager.checkPermission("android.permission.REMOTE_DISPLAY_PROVIDER", serviceInfo.packageName) != 0) {
            Slog.w("RemoteDisplayProvider", "Ignoring remote display provider service because it does not have the REMOTE_DISPLAY_PROVIDER permission: " + serviceInfo.packageName + "/" + serviceInfo.name);
            return false;
        } else {
            return true;
        }
    }

    public final int findProvider(String str, String str2) {
        int size = this.mProviders.size();
        for (int i = 0; i < size; i++) {
            if (this.mProviders.get(i).hasComponentName(str, str2)) {
                return i;
            }
        }
        return -1;
    }
}
