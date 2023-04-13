package com.android.server.p012tv;

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
import android.text.TextUtils;
import android.util.Log;
import android.util.Slog;
import com.android.internal.annotations.VisibleForTesting;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
/* renamed from: com.android.server.tv.TvRemoteProviderWatcher */
/* loaded from: classes2.dex */
public final class TvRemoteProviderWatcher {
    public static final boolean DEBUG = Log.isLoggable("TvRemoteProviderWatcher", 2);
    public final Context mContext;
    public final Handler mHandler;
    public final Object mLock;
    public final PackageManager mPackageManager;
    public final ArrayList<TvRemoteProviderProxy> mProviderProxies;
    public boolean mRunning;
    public final BroadcastReceiver mScanPackagesReceiver;
    public final Runnable mScanPackagesRunnable;
    public final Set<String> mUnbundledServicePackages;
    public final int mUserId;

    public TvRemoteProviderWatcher(Context context, Object obj, Handler handler) {
        this.mProviderProxies = new ArrayList<>();
        this.mUnbundledServicePackages = new HashSet();
        this.mScanPackagesReceiver = new BroadcastReceiver() { // from class: com.android.server.tv.TvRemoteProviderWatcher.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                if (TvRemoteProviderWatcher.DEBUG) {
                    Slog.d("TvRemoteProviderWatcher", "Received package manager broadcast: " + intent);
                }
                TvRemoteProviderWatcher.this.mHandler.post(TvRemoteProviderWatcher.this.mScanPackagesRunnable);
            }
        };
        this.mScanPackagesRunnable = new Runnable() { // from class: com.android.server.tv.TvRemoteProviderWatcher.2
            @Override // java.lang.Runnable
            public void run() {
                TvRemoteProviderWatcher.this.scanPackages();
            }
        };
        this.mContext = context;
        this.mHandler = handler;
        this.mUserId = UserHandle.myUserId();
        this.mPackageManager = context.getPackageManager();
        this.mLock = obj;
        TextUtils.SimpleStringSplitter simpleStringSplitter = new TextUtils.SimpleStringSplitter(',');
        simpleStringSplitter.setString(context.getString(17040015));
        simpleStringSplitter.forEach(new Consumer() { // from class: com.android.server.tv.TvRemoteProviderWatcher$$ExternalSyntheticLambda0
            @Override // java.util.function.Consumer
            public final void accept(Object obj2) {
                TvRemoteProviderWatcher.this.lambda$new$0((String) obj2);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(String str) {
        String trim = str.trim();
        if (trim.isEmpty()) {
            return;
        }
        this.mUnbundledServicePackages.add(trim);
    }

    public TvRemoteProviderWatcher(Context context, Object obj) {
        this(context, obj, new Handler(true));
    }

    public void start() {
        if (DEBUG) {
            Slog.d("TvRemoteProviderWatcher", "start()");
        }
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

    public final void scanPackages() {
        int i;
        if (this.mRunning) {
            if (DEBUG) {
                Log.d("TvRemoteProviderWatcher", "scanPackages()");
            }
            int i2 = 0;
            for (ResolveInfo resolveInfo : this.mPackageManager.queryIntentServicesAsUser(new Intent("com.android.media.tv.remoteprovider.TvRemoteProvider"), 0, this.mUserId)) {
                ServiceInfo serviceInfo = resolveInfo.serviceInfo;
                if (serviceInfo != null && verifyServiceTrusted(serviceInfo)) {
                    int findProvider = findProvider(serviceInfo.packageName, serviceInfo.name);
                    if (findProvider < 0) {
                        TvRemoteProviderProxy tvRemoteProviderProxy = new TvRemoteProviderProxy(this.mContext, this.mLock, new ComponentName(serviceInfo.packageName, serviceInfo.name), this.mUserId, serviceInfo.applicationInfo.uid);
                        tvRemoteProviderProxy.start();
                        i = i2 + 1;
                        this.mProviderProxies.add(i2, tvRemoteProviderProxy);
                    } else if (findProvider >= i2) {
                        TvRemoteProviderProxy tvRemoteProviderProxy2 = this.mProviderProxies.get(findProvider);
                        tvRemoteProviderProxy2.start();
                        tvRemoteProviderProxy2.rebindIfDisconnected();
                        i = i2 + 1;
                        Collections.swap(this.mProviderProxies, findProvider, i2);
                    }
                    i2 = i;
                }
            }
            if (DEBUG) {
                Log.d("TvRemoteProviderWatcher", "scanPackages() targetIndex " + i2);
            }
            if (i2 < this.mProviderProxies.size()) {
                for (int size = this.mProviderProxies.size() - 1; size >= i2; size--) {
                    TvRemoteProviderProxy tvRemoteProviderProxy3 = this.mProviderProxies.get(size);
                    this.mProviderProxies.remove(tvRemoteProviderProxy3);
                    tvRemoteProviderProxy3.stop();
                }
            }
        }
    }

    @VisibleForTesting
    public boolean verifyServiceTrusted(ServiceInfo serviceInfo) {
        String str = serviceInfo.permission;
        if (str == null || !str.equals("android.permission.BIND_TV_REMOTE_SERVICE")) {
            Slog.w("TvRemoteProviderWatcher", "Ignoring atv remote provider service because it did not require the BIND_TV_REMOTE_SERVICE permission in its manifest: " + serviceInfo.packageName + "/" + serviceInfo.name);
            return false;
        } else if (!this.mUnbundledServicePackages.contains(serviceInfo.packageName)) {
            Slog.w("TvRemoteProviderWatcher", "Ignoring atv remote provider service because the package has not been set and/or whitelisted: " + serviceInfo.packageName + "/" + serviceInfo.name);
            return false;
        } else if (hasNecessaryPermissions(serviceInfo.packageName)) {
            return true;
        } else {
            Slog.w("TvRemoteProviderWatcher", "Ignoring atv remote provider service because its package does not have TV_VIRTUAL_REMOTE_CONTROLLER permission: " + serviceInfo.packageName);
            return false;
        }
    }

    public final boolean hasNecessaryPermissions(String str) {
        return this.mPackageManager.checkPermission("android.permission.TV_VIRTUAL_REMOTE_CONTROLLER", str) == 0;
    }

    public final int findProvider(String str, String str2) {
        int size = this.mProviderProxies.size();
        for (int i = 0; i < size; i++) {
            if (this.mProviderProxies.get(i).hasComponentName(str, str2)) {
                return i;
            }
        }
        return -1;
    }
}
