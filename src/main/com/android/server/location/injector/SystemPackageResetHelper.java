package com.android.server.location.injector;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import com.android.internal.util.Preconditions;
import com.android.server.FgThread;
import com.android.server.location.injector.SystemPackageResetHelper;
/* loaded from: classes.dex */
public class SystemPackageResetHelper extends PackageResetHelper {
    public final Context mContext;
    public BroadcastReceiver mReceiver;

    public SystemPackageResetHelper(Context context) {
        this.mContext = context;
    }

    @Override // com.android.server.location.injector.PackageResetHelper
    public void onRegister() {
        Preconditions.checkState(this.mReceiver == null);
        this.mReceiver = new Receiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.PACKAGE_CHANGED");
        intentFilter.addAction("android.intent.action.PACKAGE_REMOVED");
        intentFilter.addAction("android.intent.action.PACKAGE_RESTARTED");
        intentFilter.addAction("android.intent.action.QUERY_PACKAGE_RESTART");
        intentFilter.addDataScheme("package");
        this.mContext.registerReceiver(this.mReceiver, intentFilter);
    }

    @Override // com.android.server.location.injector.PackageResetHelper
    public void onUnregister() {
        Preconditions.checkState(this.mReceiver != null);
        this.mContext.unregisterReceiver(this.mReceiver);
        this.mReceiver = null;
    }

    /* loaded from: classes.dex */
    public class Receiver extends BroadcastReceiver {
        public Receiver() {
        }

        /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
        /* JADX WARN: Removed duplicated region for block: B:56:0x00b4 A[ORIG_RETURN, RETURN] */
        /* JADX WARN: Removed duplicated region for block: B:57:0x006b A[EXC_TOP_SPLITTER, SYNTHETIC] */
        @Override // android.content.BroadcastReceiver
        /*
            Code decompiled incorrectly, please refer to instructions dump.
        */
        public void onReceive(Context context, Intent intent) {
            Uri data;
            final String schemeSpecificPart;
            char c;
            String action = intent.getAction();
            if (action == null || (data = intent.getData()) == null || (schemeSpecificPart = data.getSchemeSpecificPart()) == null) {
                return;
            }
            boolean z = true;
            switch (action.hashCode()) {
                case -1072806502:
                    if (action.equals("android.intent.action.QUERY_PACKAGE_RESTART")) {
                        c = 0;
                        break;
                    }
                    c = 65535;
                    break;
                case -757780528:
                    if (action.equals("android.intent.action.PACKAGE_RESTARTED")) {
                        c = 1;
                        break;
                    }
                    c = 65535;
                    break;
                case 172491798:
                    if (action.equals("android.intent.action.PACKAGE_CHANGED")) {
                        c = 2;
                        break;
                    }
                    c = 65535;
                    break;
                case 525384130:
                    if (action.equals("android.intent.action.PACKAGE_REMOVED")) {
                        c = 3;
                        break;
                    }
                    c = 65535;
                    break;
                default:
                    c = 65535;
                    break;
            }
            switch (c) {
                case 0:
                    String[] stringArrayExtra = intent.getStringArrayExtra("android.intent.extra.PACKAGES");
                    if (stringArrayExtra != null) {
                        for (String str : stringArrayExtra) {
                            if (SystemPackageResetHelper.this.queryResetableForPackage(str)) {
                                setResultCode(-1);
                                return;
                            }
                        }
                        return;
                    }
                    return;
                case 1:
                case 3:
                    FgThread.getExecutor().execute(new Runnable() { // from class: com.android.server.location.injector.SystemPackageResetHelper$Receiver$$ExternalSyntheticLambda1
                        @Override // java.lang.Runnable
                        public final void run() {
                            SystemPackageResetHelper.Receiver.this.lambda$onReceive$1(schemeSpecificPart);
                        }
                    });
                    return;
                case 2:
                    String[] stringArrayExtra2 = intent.getStringArrayExtra("android.intent.extra.changed_component_name_list");
                    if (stringArrayExtra2 != null) {
                        for (String str2 : stringArrayExtra2) {
                            if (schemeSpecificPart.equals(str2)) {
                                if (z) {
                                    return;
                                }
                                try {
                                    if (context.getPackageManager().getApplicationInfo(schemeSpecificPart, PackageManager.ApplicationInfoFlags.of(0L)).enabled) {
                                        return;
                                    }
                                    FgThread.getExecutor().execute(new Runnable() { // from class: com.android.server.location.injector.SystemPackageResetHelper$Receiver$$ExternalSyntheticLambda0
                                        @Override // java.lang.Runnable
                                        public final void run() {
                                            SystemPackageResetHelper.Receiver.this.lambda$onReceive$0(schemeSpecificPart);
                                        }
                                    });
                                    return;
                                } catch (PackageManager.NameNotFoundException unused) {
                                    return;
                                }
                            }
                        }
                    }
                    z = false;
                    if (z) {
                    }
                default:
                    return;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onReceive$0(String str) {
            SystemPackageResetHelper.this.notifyPackageReset(str);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onReceive$1(String str) {
            SystemPackageResetHelper.this.notifyPackageReset(str);
        }
    }
}
