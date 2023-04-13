package com.android.commands.monkey;

import android.app.ActivityManager;
import android.app.IActivityManager;
import android.app.IApplicationThread;
import android.app.ProfilerInfo;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.IPackageManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.view.IWindowManager;
import java.util.HashMap;
/* loaded from: classes.dex */
public class MonkeyActivityEvent extends MonkeyEvent {
    long mAlarmTime;
    private ComponentName mApp;
    private HashMap<ComponentName, String> mMainApps;

    public MonkeyActivityEvent(ComponentName app) {
        super(4);
        this.mAlarmTime = 0L;
        this.mMainApps = new HashMap<>();
        this.mApp = app;
    }

    public MonkeyActivityEvent(ComponentName app, long arg) {
        super(4);
        this.mAlarmTime = 0L;
        this.mMainApps = new HashMap<>();
        this.mApp = app;
        this.mAlarmTime = arg;
    }

    public MonkeyActivityEvent(ComponentName app, HashMap<ComponentName, String> MainApps) {
        super(4);
        this.mAlarmTime = 0L;
        this.mMainApps = new HashMap<>();
        this.mApp = app;
        this.mMainApps = MainApps;
    }

    private Intent getEvent() {
        Intent intent = new Intent("android.intent.action.MAIN");
        if (this.mMainApps.containsKey(this.mApp)) {
            intent.addCategory(this.mMainApps.get(this.mApp));
        } else {
            intent.addCategory("android.intent.category.LAUNCHER");
        }
        intent.setComponent(this.mApp);
        intent.addFlags(270532608);
        return intent;
    }

    @Override // com.android.commands.monkey.MonkeyEvent
    public int injectEvent(IWindowManager iwm, IActivityManager iam, int verbose) {
        int i;
        Intent intent = getEvent();
        if (verbose > 0) {
            Logger.out.println(":Switch: " + intent.toUri(0));
        }
        if (this.mAlarmTime != 0) {
            Bundle args = new Bundle();
            args.putLong("alarmTime", this.mAlarmTime);
            intent.putExtras(args);
        }
        try {
            try {
                i = 0;
                try {
                    iam.startActivityAsUserWithFeature((IApplicationThread) null, getPackageName(), (String) null, intent, (String) null, (IBinder) null, (String) null, 0, 0, (ProfilerInfo) null, (Bundle) null, ActivityManager.getCurrentUser());
                    return 1;
                } catch (SecurityException e) {
                    if (verbose > 0) {
                        Logger.out.println("** Permissions error starting activity " + intent.toUri(i));
                        return -2;
                    }
                    return -2;
                }
            } catch (RemoteException e2) {
                Logger.err.println("** Failed talking with activity manager!");
                return -1;
            }
        } catch (SecurityException e3) {
            i = 0;
        }
    }

    private static String getPackageName() {
        String[] packages;
        try {
            IPackageManager pm = IPackageManager.Stub.asInterface(ServiceManager.getService("package"));
            if (pm == null || (packages = pm.getPackagesForUid(Binder.getCallingUid())) == null) {
                return null;
            }
            return packages[0];
        } catch (RemoteException e) {
            Logger.err.println("** Failed talking with package manager!");
            return null;
        }
    }
}
