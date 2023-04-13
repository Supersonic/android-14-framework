package com.android.server.devicepolicy;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.os.UserHandle;
import android.util.Pair;
import com.android.internal.notification.SystemNotificationChannels;
import com.android.server.devicepolicy.DevicePolicyManagerService;
import com.android.server.utils.Slogf;
import java.io.FileNotFoundException;
import java.security.SecureRandom;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
/* loaded from: classes.dex */
public class RemoteBugreportManager {
    public final Context mContext;
    public final Handler mHandler;
    public final DevicePolicyManagerService.Injector mInjector;
    public final DevicePolicyManagerService mService;
    public final SecureRandom mRng = new SecureRandom();
    public final AtomicLong mRemoteBugreportNonce = new AtomicLong();
    public final AtomicBoolean mRemoteBugreportServiceIsActive = new AtomicBoolean();
    public final AtomicBoolean mRemoteBugreportSharingAccepted = new AtomicBoolean();
    public final Runnable mRemoteBugreportTimeoutRunnable = new Runnable() { // from class: com.android.server.devicepolicy.RemoteBugreportManager$$ExternalSyntheticLambda0
        @Override // java.lang.Runnable
        public final void run() {
            RemoteBugreportManager.this.lambda$new$0();
        }
    };
    public final BroadcastReceiver mRemoteBugreportFinishedReceiver = new BroadcastReceiver() { // from class: com.android.server.devicepolicy.RemoteBugreportManager.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if ("android.intent.action.REMOTE_BUGREPORT_DISPATCH".equals(intent.getAction()) && RemoteBugreportManager.this.mRemoteBugreportServiceIsActive.get()) {
                RemoteBugreportManager.this.onBugreportFinished(intent);
            }
        }
    };
    public final BroadcastReceiver mRemoteBugreportConsentReceiver = new BroadcastReceiver() { // from class: com.android.server.devicepolicy.RemoteBugreportManager.2
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            RemoteBugreportManager.this.mInjector.getNotificationManager().cancel("DevicePolicyManager", 678432343);
            if ("com.android.server.action.REMOTE_BUGREPORT_SHARING_ACCEPTED".equals(action)) {
                RemoteBugreportManager.this.onBugreportSharingAccepted();
            } else if ("com.android.server.action.REMOTE_BUGREPORT_SHARING_DECLINED".equals(action)) {
                RemoteBugreportManager.this.onBugreportSharingDeclined();
            }
            RemoteBugreportManager.this.mContext.unregisterReceiver(RemoteBugreportManager.this.mRemoteBugreportConsentReceiver);
        }
    };

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0() {
        if (this.mRemoteBugreportServiceIsActive.get()) {
            onBugreportFailed();
        }
    }

    public RemoteBugreportManager(DevicePolicyManagerService devicePolicyManagerService, DevicePolicyManagerService.Injector injector) {
        this.mService = devicePolicyManagerService;
        this.mInjector = injector;
        this.mContext = devicePolicyManagerService.mContext;
        this.mHandler = devicePolicyManagerService.mHandler;
    }

    public final Notification buildNotification(int i) {
        Intent intent = new Intent("android.settings.SHOW_REMOTE_BUGREPORT_DIALOG");
        intent.addFlags(268468224);
        intent.putExtra("android.app.extra.bugreport_notification_type", i);
        ActivityInfo resolveActivityInfo = intent.resolveActivityInfo(this.mContext.getPackageManager(), 1048576);
        if (resolveActivityInfo != null) {
            intent.setComponent(resolveActivityInfo.getComponentName());
        } else {
            Slogf.wtf("DevicePolicyManager", "Failed to resolve intent for remote bugreport dialog");
        }
        Notification.Builder extend = new Notification.Builder(this.mContext, SystemNotificationChannels.DEVICE_ADMIN).setSmallIcon(17303596).setOngoing(true).setLocalOnly(true).setContentIntent(PendingIntent.getActivityAsUser(this.mContext, i, intent, 67108864, null, UserHandle.CURRENT)).setColor(this.mContext.getColor(17170460)).extend(new Notification.TvExtender());
        if (i == 2) {
            extend.setContentTitle(this.mContext.getString(17041525)).setProgress(0, 0, true);
        } else if (i == 1) {
            extend.setContentTitle(this.mContext.getString(17041638)).setProgress(0, 0, true);
        } else if (i == 3) {
            PendingIntent broadcast = PendingIntent.getBroadcast(this.mContext, 678432343, new Intent("com.android.server.action.REMOTE_BUGREPORT_SHARING_ACCEPTED"), 335544320);
            extend.addAction(new Notification.Action.Builder((Icon) null, this.mContext.getString(17040104), PendingIntent.getBroadcast(this.mContext, 678432343, new Intent("com.android.server.action.REMOTE_BUGREPORT_SHARING_DECLINED"), 335544320)).build()).addAction(new Notification.Action.Builder((Icon) null, this.mContext.getString(17041518), broadcast).build()).setContentTitle(this.mContext.getString(17041520)).setContentText(this.mContext.getString(17041519)).setStyle(new Notification.BigTextStyle().bigText(this.mContext.getString(17041519)));
        }
        return extend.build();
    }

    public boolean requestBugreport() {
        long nextLong;
        if (this.mRemoteBugreportServiceIsActive.get() || this.mService.getDeviceOwnerRemoteBugreportUriAndHash() != null) {
            Slogf.m30d("DevicePolicyManager", "Remote bugreport wasn't started because there's already one running");
            return false;
        }
        long binderClearCallingIdentity = this.mInjector.binderClearCallingIdentity();
        do {
            try {
                nextLong = this.mRng.nextLong();
            } catch (RemoteException e) {
                Slogf.m25e("DevicePolicyManager", "Failed to make remote calls to start bugreportremote service", e);
                return false;
            } finally {
                this.mInjector.binderRestoreCallingIdentity(binderClearCallingIdentity);
            }
        } while (nextLong == 0);
        this.mInjector.getIActivityManager().requestRemoteBugReport(nextLong);
        this.mRemoteBugreportNonce.set(nextLong);
        this.mRemoteBugreportServiceIsActive.set(true);
        this.mRemoteBugreportSharingAccepted.set(false);
        registerRemoteBugreportReceivers();
        this.mInjector.getNotificationManager().notifyAsUser("DevicePolicyManager", 678432343, buildNotification(1), UserHandle.ALL);
        this.mHandler.postDelayed(this.mRemoteBugreportTimeoutRunnable, 600000L);
        return true;
    }

    public final void registerRemoteBugreportReceivers() {
        try {
            this.mContext.registerReceiver(this.mRemoteBugreportFinishedReceiver, new IntentFilter("android.intent.action.REMOTE_BUGREPORT_DISPATCH", "application/vnd.android.bugreport"), 2);
        } catch (IntentFilter.MalformedMimeTypeException e) {
            Slogf.m10w("DevicePolicyManager", e, "Failed to set type %s", "application/vnd.android.bugreport");
        }
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.android.server.action.REMOTE_BUGREPORT_SHARING_DECLINED");
        intentFilter.addAction("com.android.server.action.REMOTE_BUGREPORT_SHARING_ACCEPTED");
        this.mContext.registerReceiver(this.mRemoteBugreportConsentReceiver, intentFilter);
    }

    public final void onBugreportFinished(Intent intent) {
        long longExtra = intent.getLongExtra("android.intent.extra.REMOTE_BUGREPORT_NONCE", 0L);
        if (longExtra == 0 || this.mRemoteBugreportNonce.get() != longExtra) {
            Slogf.m14w("DevicePolicyManager", "Invalid nonce provided, ignoring " + longExtra);
            return;
        }
        this.mHandler.removeCallbacks(this.mRemoteBugreportTimeoutRunnable);
        this.mRemoteBugreportServiceIsActive.set(false);
        Uri data = intent.getData();
        String uri = data != null ? data.toString() : null;
        String stringExtra = intent.getStringExtra("android.intent.extra.REMOTE_BUGREPORT_HASH");
        if (this.mRemoteBugreportSharingAccepted.get()) {
            shareBugreportWithDeviceOwnerIfExists(uri, stringExtra);
            this.mInjector.getNotificationManager().cancel("DevicePolicyManager", 678432343);
        } else {
            this.mService.setDeviceOwnerRemoteBugreportUriAndHash(uri, stringExtra);
            this.mInjector.getNotificationManager().notifyAsUser("DevicePolicyManager", 678432343, buildNotification(3), UserHandle.ALL);
        }
        this.mContext.unregisterReceiver(this.mRemoteBugreportFinishedReceiver);
    }

    public final void onBugreportFailed() {
        this.mRemoteBugreportServiceIsActive.set(false);
        this.mInjector.systemPropertiesSet("ctl.stop", "bugreportd");
        this.mRemoteBugreportSharingAccepted.set(false);
        this.mService.setDeviceOwnerRemoteBugreportUriAndHash(null, null);
        this.mInjector.getNotificationManager().cancel("DevicePolicyManager", 678432343);
        Bundle bundle = new Bundle();
        bundle.putInt("android.app.extra.BUGREPORT_FAILURE_REASON", 0);
        this.mService.sendDeviceOwnerCommand("android.app.action.BUGREPORT_FAILED", bundle);
        this.mContext.unregisterReceiver(this.mRemoteBugreportConsentReceiver);
        this.mContext.unregisterReceiver(this.mRemoteBugreportFinishedReceiver);
    }

    public final void onBugreportSharingAccepted() {
        this.mRemoteBugreportSharingAccepted.set(true);
        Pair<String, String> deviceOwnerRemoteBugreportUriAndHash = this.mService.getDeviceOwnerRemoteBugreportUriAndHash();
        if (deviceOwnerRemoteBugreportUriAndHash != null) {
            shareBugreportWithDeviceOwnerIfExists((String) deviceOwnerRemoteBugreportUriAndHash.first, (String) deviceOwnerRemoteBugreportUriAndHash.second);
        } else if (this.mRemoteBugreportServiceIsActive.get()) {
            this.mInjector.getNotificationManager().notifyAsUser("DevicePolicyManager", 678432343, buildNotification(2), UserHandle.ALL);
        }
    }

    public final void onBugreportSharingDeclined() {
        if (this.mRemoteBugreportServiceIsActive.get()) {
            this.mInjector.systemPropertiesSet("ctl.stop", "bugreportd");
            this.mRemoteBugreportServiceIsActive.set(false);
            this.mHandler.removeCallbacks(this.mRemoteBugreportTimeoutRunnable);
            this.mContext.unregisterReceiver(this.mRemoteBugreportFinishedReceiver);
        }
        this.mRemoteBugreportSharingAccepted.set(false);
        this.mService.setDeviceOwnerRemoteBugreportUriAndHash(null, null);
        this.mService.sendDeviceOwnerCommand("android.app.action.BUGREPORT_SHARING_DECLINED", null);
    }

    public final void shareBugreportWithDeviceOwnerIfExists(String str, String str2) {
        try {
            try {
            } catch (FileNotFoundException unused) {
                Bundle bundle = new Bundle();
                bundle.putInt("android.app.extra.BUGREPORT_FAILURE_REASON", 1);
                this.mService.sendDeviceOwnerCommand("android.app.action.BUGREPORT_FAILED", bundle);
            }
            if (str == null) {
                throw new FileNotFoundException();
            }
            this.mService.sendBugreportToDeviceOwner(Uri.parse(str), str2);
        } finally {
            this.mRemoteBugreportSharingAccepted.set(false);
            this.mService.setDeviceOwnerRemoteBugreportUriAndHash(null, null);
        }
    }

    public void checkForPendingBugreportAfterBoot() {
        if (this.mService.getDeviceOwnerRemoteBugreportUriAndHash() == null) {
            return;
        }
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.android.server.action.REMOTE_BUGREPORT_SHARING_DECLINED");
        intentFilter.addAction("com.android.server.action.REMOTE_BUGREPORT_SHARING_ACCEPTED");
        this.mContext.registerReceiver(this.mRemoteBugreportConsentReceiver, intentFilter);
        this.mInjector.getNotificationManager().notifyAsUser("DevicePolicyManager", 678432343, buildNotification(3), UserHandle.ALL);
    }
}
