package com.android.server.devicepolicy;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Handler;
import android.os.RemoteException;
import android.os.UserHandle;
import android.security.Credentials;
import android.security.KeyChain;
import android.util.PluralsMessageFormatter;
import com.android.internal.notification.SystemNotificationChannels;
import com.android.server.devicepolicy.DevicePolicyManagerService;
import com.android.server.utils.Slogf;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.List;
/* loaded from: classes.dex */
public class CertificateMonitor {
    public final Handler mHandler;
    public final DevicePolicyManagerService.Injector mInjector;
    public final BroadcastReceiver mRootCaReceiver;
    public final DevicePolicyManagerService mService;

    public CertificateMonitor(DevicePolicyManagerService devicePolicyManagerService, DevicePolicyManagerService.Injector injector, Handler handler) {
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() { // from class: com.android.server.devicepolicy.CertificateMonitor.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context, Intent intent) {
                CertificateMonitor.this.updateInstalledCertificates(UserHandle.of(intent.getIntExtra("android.intent.extra.user_handle", getSendingUserId())));
            }
        };
        this.mRootCaReceiver = broadcastReceiver;
        this.mService = devicePolicyManagerService;
        this.mInjector = injector;
        this.mHandler = handler;
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.USER_STARTED");
        intentFilter.addAction("android.intent.action.USER_UNLOCKED");
        intentFilter.addAction("android.security.action.TRUST_STORE_CHANGED");
        intentFilter.setPriority(1000);
        injector.mContext.registerReceiverAsUser(broadcastReceiver, UserHandle.ALL, intentFilter, null, handler);
    }

    public String installCaCert(UserHandle userHandle, byte[] bArr) {
        try {
            byte[] convertToPem = Credentials.convertToPem(new Certificate[]{parseCert(bArr)});
            try {
                KeyChain.KeyChainConnection keyChainBindAsUser = this.mInjector.keyChainBindAsUser(userHandle);
                String installCaCertificate = keyChainBindAsUser.getService().installCaCertificate(convertToPem);
                keyChainBindAsUser.close();
                return installCaCertificate;
            } catch (RemoteException e) {
                Slogf.m25e("DevicePolicyManager", "installCaCertsToKeyChain(): ", e);
                return null;
            } catch (InterruptedException e2) {
                Slogf.m13w("DevicePolicyManager", "installCaCertsToKeyChain(): ", e2);
                Thread.currentThread().interrupt();
                return null;
            }
        } catch (IOException | CertificateException e3) {
            Slogf.m25e("DevicePolicyManager", "Problem converting cert", e3);
            return null;
        }
    }

    public void uninstallCaCerts(UserHandle userHandle, String[] strArr) {
        try {
            KeyChain.KeyChainConnection keyChainBindAsUser = this.mInjector.keyChainBindAsUser(userHandle);
            for (String str : strArr) {
                try {
                    keyChainBindAsUser.getService().deleteCaCertificate(str);
                } catch (Throwable th) {
                    if (keyChainBindAsUser != null) {
                        try {
                            keyChainBindAsUser.close();
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                    }
                    throw th;
                }
            }
            if (keyChainBindAsUser != null) {
                keyChainBindAsUser.close();
            }
        } catch (RemoteException e) {
            Slogf.m25e("DevicePolicyManager", "from CaCertUninstaller: ", e);
        } catch (InterruptedException e2) {
            Slogf.m13w("DevicePolicyManager", "CaCertUninstaller: ", e2);
            Thread.currentThread().interrupt();
        }
    }

    public final List<String> getInstalledCaCertificates(UserHandle userHandle) throws RemoteException, RuntimeException {
        try {
            KeyChain.KeyChainConnection keyChainBindAsUser = this.mInjector.keyChainBindAsUser(userHandle);
            List<String> list = keyChainBindAsUser.getService().getUserCaAliases().getList();
            keyChainBindAsUser.close();
            return list;
        } catch (AssertionError e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e2) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e2);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onCertificateApprovalsChanged$0(int i) {
        updateInstalledCertificates(UserHandle.of(i));
    }

    public void onCertificateApprovalsChanged(final int i) {
        this.mHandler.post(new Runnable() { // from class: com.android.server.devicepolicy.CertificateMonitor$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                CertificateMonitor.this.lambda$onCertificateApprovalsChanged$0(i);
            }
        });
    }

    public final void updateInstalledCertificates(UserHandle userHandle) {
        int identifier = userHandle.getIdentifier();
        if (this.mInjector.getUserManager().isUserUnlocked(identifier)) {
            try {
                List<String> installedCaCertificates = getInstalledCaCertificates(userHandle);
                this.mService.onInstalledCertificatesChanged(userHandle, installedCaCertificates);
                int size = installedCaCertificates.size() - this.mService.getAcceptedCaCertificates(userHandle).size();
                if (size != 0) {
                    this.mInjector.getNotificationManager().notifyAsUser("DevicePolicyManager", 33, buildNotification(userHandle, size), userHandle);
                    return;
                }
                this.mInjector.getNotificationManager().cancelAsUser("DevicePolicyManager", 33, userHandle);
            } catch (RemoteException | RuntimeException e) {
                Slogf.m23e("DevicePolicyManager", e, "Could not retrieve certificates from KeyChain service for user %d", Integer.valueOf(identifier));
            }
        }
    }

    public final Notification buildNotification(UserHandle userHandle, int i) {
        String string;
        String str;
        try {
            Context createContextAsUser = this.mInjector.createContextAsUser(userHandle);
            Resources resources = this.mInjector.getResources();
            int identifier = userHandle.getIdentifier();
            int i2 = 17303626;
            if (this.mService.lambda$isProfileOwner$66(userHandle.getIdentifier()) != null) {
                str = resources.getString(17041571, this.mService.getProfileOwnerName(userHandle.getIdentifier()));
                identifier = this.mService.getProfileParentId(userHandle.getIdentifier());
            } else {
                if (this.mService.getDeviceOwnerUserId() == userHandle.getIdentifier()) {
                    string = resources.getString(17041571, this.mService.getDeviceOwnerName());
                } else {
                    string = resources.getString(17041570);
                    i2 = 17301642;
                }
                str = string;
            }
            int i3 = i2;
            Intent intent = new Intent("com.android.settings.MONITORING_CERT_INFO");
            intent.setFlags(268468224);
            intent.putExtra("android.settings.extra.number_of_certificates", i);
            intent.putExtra("android.intent.extra.USER_ID", userHandle.getIdentifier());
            ActivityInfo resolveActivityInfo = intent.resolveActivityInfo(this.mInjector.getPackageManager(), 1048576);
            if (resolveActivityInfo != null) {
                intent.setComponent(resolveActivityInfo.getComponentName());
            }
            PendingIntent pendingIntentGetActivityAsUser = this.mInjector.pendingIntentGetActivityAsUser(createContextAsUser, 0, intent, 201326592, null, UserHandle.of(identifier));
            HashMap hashMap = new HashMap();
            hashMap.put("count", Integer.valueOf(i));
            return new Notification.Builder(createContextAsUser, SystemNotificationChannels.SECURITY).setSmallIcon(i3).setContentTitle(PluralsMessageFormatter.format(resources, hashMap, 17041572)).setContentText(str).setContentIntent(pendingIntentGetActivityAsUser).setShowWhen(false).setColor(17170460).build();
        } catch (PackageManager.NameNotFoundException e) {
            Slogf.m23e("DevicePolicyManager", e, "Create context as %s failed", userHandle);
            return null;
        }
    }

    public static X509Certificate parseCert(byte[] bArr) throws CertificateException {
        return (X509Certificate) CertificateFactory.getInstance("X.509").generateCertificate(new ByteArrayInputStream(bArr));
    }
}
