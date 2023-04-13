package com.android.commands.monkey;

import android.content.pm.IPackageManager;
import android.content.pm.PackageInfo;
import android.content.pm.PermissionInfo;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.permission.IPermissionManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
/* loaded from: classes.dex */
public class MonkeyPermissionUtil {
    private static final String[] MODERN_PERMISSION_GROUPS = {"android.permission-group.CALENDAR", "android.permission-group.CAMERA", "android.permission-group.CONTACTS", "android.permission-group.LOCATION", "android.permission-group.SENSORS", "android.permission-group.SMS", "android.permission-group.PHONE", "android.permission-group.MICROPHONE", "android.permission-group.STORAGE"};
    private static final String PERMISSION_GROUP_PREFIX = "android.permission-group.";
    private static final String PERMISSION_PREFIX = "android.permission.";
    private Map<String, List<PermissionInfo>> mPermissionMap;
    private boolean mTargetSystemPackages;
    private List<String> mTargetedPackages;
    private IPackageManager mPm = IPackageManager.Stub.asInterface(ServiceManager.getService("package"));
    private final IPermissionManager mPermManager = IPermissionManager.Stub.asInterface(ServiceManager.getService("permissionmgr"));

    private static boolean isModernPermissionGroup(String name) {
        String[] strArr;
        for (String modernGroup : MODERN_PERMISSION_GROUPS) {
            if (modernGroup.equals(name)) {
                return true;
            }
        }
        return false;
    }

    public void setTargetSystemPackages(boolean targetSystemPackages) {
        this.mTargetSystemPackages = targetSystemPackages;
    }

    private boolean shouldTargetPackage(PackageInfo info) {
        if (MonkeyUtils.getPackageFilter().checkEnteringPackage(info.packageName)) {
            return true;
        }
        return (!this.mTargetSystemPackages || MonkeyUtils.getPackageFilter().isPackageInvalid(info.packageName) || (info.applicationInfo.flags & 1) == 0) ? false : true;
    }

    private boolean shouldTargetPermission(String pkg, PermissionInfo pi) throws RemoteException {
        int flags = this.mPermManager.getPermissionFlags(pkg, pi.name, UserHandle.myUserId());
        return pi.group != null && pi.protectionLevel == 1 && (flags & 20) == 0 && isModernPermissionGroup(pi.group);
    }

    public boolean populatePermissionsMapping() {
        String[] strArr;
        this.mPermissionMap = new HashMap();
        try {
            List<?> pkgInfos = this.mPm.getInstalledPackages(4096L, UserHandle.myUserId()).getList();
            for (Object o : pkgInfos) {
                PackageInfo info = (PackageInfo) o;
                if (shouldTargetPackage(info)) {
                    List<PermissionInfo> permissions = new ArrayList<>();
                    if (info.applicationInfo.targetSdkVersion > 22 && info.requestedPermissions != null) {
                        for (String perm : info.requestedPermissions) {
                            PermissionInfo pi = this.mPermManager.getPermissionInfo(perm, "shell", 0);
                            if (pi != null && shouldTargetPermission(info.packageName, pi)) {
                                permissions.add(pi);
                            }
                        }
                        if (!permissions.isEmpty()) {
                            this.mPermissionMap.put(info.packageName, permissions);
                        }
                    }
                }
            }
            if (!this.mPermissionMap.isEmpty()) {
                this.mTargetedPackages = new ArrayList(this.mPermissionMap.keySet());
                return true;
            }
            return true;
        } catch (RemoteException e) {
            Logger.err.println("** Failed talking with package manager!");
            return false;
        }
    }

    public void dump() {
        Logger.out.println("// Targeted packages and permissions:");
        for (Map.Entry<String, List<PermissionInfo>> e : this.mPermissionMap.entrySet()) {
            Logger.out.println(String.format("//  + Using %s", e.getKey()));
            for (PermissionInfo pi : e.getValue()) {
                String name = pi.name;
                if (name != null && name.startsWith(PERMISSION_PREFIX)) {
                    name = name.substring(PERMISSION_PREFIX.length());
                }
                String group = pi.group;
                if (group != null && group.startsWith(PERMISSION_GROUP_PREFIX)) {
                    group = group.substring(PERMISSION_GROUP_PREFIX.length());
                }
                Logger.out.println(String.format("//    Permission: %s [%s]", name, group));
            }
        }
    }

    public MonkeyPermissionEvent generateRandomPermissionEvent(Random random) {
        List<String> list = this.mTargetedPackages;
        String pkg = list.get(random.nextInt(list.size()));
        List<PermissionInfo> infos = this.mPermissionMap.get(pkg);
        return new MonkeyPermissionEvent(pkg, infos.get(random.nextInt(infos.size())));
    }
}
