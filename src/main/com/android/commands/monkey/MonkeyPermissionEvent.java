package com.android.commands.monkey;

import android.app.ActivityManager;
import android.app.AppGlobals;
import android.app.IActivityManager;
import android.content.pm.IPackageManager;
import android.content.pm.PermissionInfo;
import android.os.RemoteException;
import android.permission.IPermissionManager;
import android.view.IWindowManager;
/* loaded from: classes.dex */
public class MonkeyPermissionEvent extends MonkeyEvent {
    private PermissionInfo mPermissionInfo;
    private String mPkg;

    public MonkeyPermissionEvent(String pkg, PermissionInfo permissionInfo) {
        super(7);
        this.mPkg = pkg;
        this.mPermissionInfo = permissionInfo;
    }

    @Override // com.android.commands.monkey.MonkeyEvent
    public int injectEvent(IWindowManager iwm, IActivityManager iam, int verbose) {
        IPackageManager packageManager = AppGlobals.getPackageManager();
        IPermissionManager permissionManager = AppGlobals.getPermissionManager();
        int currentUser = ActivityManager.getCurrentUser();
        try {
            int perm = packageManager.checkPermission(this.mPermissionInfo.name, this.mPkg, currentUser);
            boolean grant = perm == -1;
            Logger logger = Logger.out;
            Object[] objArr = new Object[3];
            objArr[0] = grant ? "grant" : "revoke";
            objArr[1] = this.mPermissionInfo.name;
            objArr[2] = this.mPkg;
            logger.println(String.format(":Permission %s %s to package %s", objArr));
            if (grant) {
                permissionManager.grantRuntimePermission(this.mPkg, this.mPermissionInfo.name, currentUser);
            } else {
                permissionManager.revokeRuntimePermission(this.mPkg, this.mPermissionInfo.name, currentUser, (String) null);
            }
            return 1;
        } catch (RemoteException e) {
            return -1;
        }
    }
}
