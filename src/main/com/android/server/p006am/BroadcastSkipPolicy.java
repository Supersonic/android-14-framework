package com.android.server.p006am;

import android.app.ActivityManager;
import android.app.AppGlobals;
import android.app.AppOpsManager;
import android.app.BroadcastOptions;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageManager;
import android.content.pm.PermissionInfo;
import android.content.pm.ResolveInfo;
import android.os.RemoteException;
import android.os.UserHandle;
import android.permission.IPermissionManager;
import android.util.Slog;
import com.android.internal.util.ArrayUtils;
import com.android.server.p011pm.PackageManagerShellCommandDataLoader;
import java.util.Objects;
/* renamed from: com.android.server.am.BroadcastSkipPolicy */
/* loaded from: classes.dex */
public class BroadcastSkipPolicy {
    public final ActivityManagerService mService;

    public BroadcastSkipPolicy(ActivityManagerService activityManagerService) {
        Objects.requireNonNull(activityManagerService);
        this.mService = activityManagerService;
    }

    public boolean shouldSkip(BroadcastRecord broadcastRecord, Object obj) {
        String shouldSkipMessage = shouldSkipMessage(broadcastRecord, obj);
        if (shouldSkipMessage != null) {
            Slog.w("BroadcastQueue", shouldSkipMessage);
            return true;
        }
        return false;
    }

    public String shouldSkipMessage(BroadcastRecord broadcastRecord, Object obj) {
        if (obj instanceof BroadcastFilter) {
            return shouldSkipMessage(broadcastRecord, (BroadcastFilter) obj);
        }
        return shouldSkipMessage(broadcastRecord, (ResolveInfo) obj);
    }

    public final String shouldSkipMessage(BroadcastRecord broadcastRecord, ResolveInfo resolveInfo) {
        String[] strArr;
        int i;
        int i2;
        int permissionToOpCode;
        BroadcastSkipPolicy broadcastSkipPolicy = this;
        BroadcastOptions broadcastOptions = broadcastRecord.options;
        ActivityInfo activityInfo = resolveInfo.activityInfo;
        ComponentName componentName = new ComponentName(activityInfo.applicationInfo.packageName, activityInfo.name);
        if (broadcastOptions != null && (resolveInfo.activityInfo.applicationInfo.targetSdkVersion < broadcastOptions.getMinManifestReceiverApiLevel() || resolveInfo.activityInfo.applicationInfo.targetSdkVersion > broadcastOptions.getMaxManifestReceiverApiLevel())) {
            return "Target SDK mismatch: receiver " + resolveInfo.activityInfo + " targets " + resolveInfo.activityInfo.applicationInfo.targetSdkVersion + " but delivery restricted to [" + broadcastOptions.getMinManifestReceiverApiLevel() + ", " + broadcastOptions.getMaxManifestReceiverApiLevel() + "] broadcasting " + broadcastDescription(broadcastRecord, componentName);
        } else if (broadcastOptions != null && !broadcastOptions.testRequireCompatChange(resolveInfo.activityInfo.applicationInfo.uid)) {
            return "Compat change filtered: broadcasting " + broadcastDescription(broadcastRecord, componentName) + " to uid " + resolveInfo.activityInfo.applicationInfo.uid + " due to compat change " + broadcastRecord.options.getRequireCompatChangeId();
        } else if (!broadcastSkipPolicy.mService.validateAssociationAllowedLocked(broadcastRecord.callerPackage, broadcastRecord.callingUid, componentName.getPackageName(), resolveInfo.activityInfo.applicationInfo.uid)) {
            return "Association not allowed: broadcasting " + broadcastDescription(broadcastRecord, componentName);
        } else if (!broadcastSkipPolicy.mService.mIntentFirewall.checkBroadcast(broadcastRecord.intent, broadcastRecord.callingUid, broadcastRecord.callingPid, broadcastRecord.resolvedType, resolveInfo.activityInfo.applicationInfo.uid)) {
            return "Firewall blocked: broadcasting " + broadcastDescription(broadcastRecord, componentName);
        } else {
            ActivityInfo activityInfo2 = resolveInfo.activityInfo;
            if (ActivityManagerService.checkComponentPermission(activityInfo2.permission, broadcastRecord.callingPid, broadcastRecord.callingUid, activityInfo2.applicationInfo.uid, activityInfo2.exported) != 0) {
                if (!resolveInfo.activityInfo.exported) {
                    return "Permission Denial: broadcasting " + broadcastDescription(broadcastRecord, componentName) + " is not exported from uid " + resolveInfo.activityInfo.applicationInfo.uid;
                }
                return "Permission Denial: broadcasting " + broadcastDescription(broadcastRecord, componentName) + " requires " + resolveInfo.activityInfo.permission;
            }
            String str = resolveInfo.activityInfo.permission;
            int i3 = -1;
            if (str != null && (permissionToOpCode = AppOpsManager.permissionToOpCode(str)) != -1) {
                if (broadcastSkipPolicy.mService.getAppOpsManager().noteOpNoThrow(permissionToOpCode, broadcastRecord.callingUid, broadcastRecord.callerPackage, broadcastRecord.callerFeatureId, "Broadcast delivered to " + resolveInfo.activityInfo.name) != 0) {
                    return "Appop Denial: broadcasting " + broadcastDescription(broadcastRecord, componentName) + " requires appop " + AppOpsManager.permissionToOp(resolveInfo.activityInfo.permission);
                }
            }
            ActivityInfo activityInfo3 = resolveInfo.activityInfo;
            if ((activityInfo3.flags & 1073741824) != 0 && ActivityManager.checkUidPermission("android.permission.INTERACT_ACROSS_USERS", activityInfo3.applicationInfo.uid) != 0) {
                return "Permission Denial: Receiver " + componentName.flattenToShortString() + " requests FLAG_SINGLE_USER, but app does not hold android.permission.INTERACT_ACROSS_USERS";
            } else if (resolveInfo.activityInfo.applicationInfo.isInstantApp() && broadcastRecord.callingUid != resolveInfo.activityInfo.applicationInfo.uid) {
                return "Instant App Denial: receiving " + broadcastRecord.intent + " to " + componentName.flattenToShortString() + " due to sender " + broadcastRecord.callerPackage + " (uid " + broadcastRecord.callingUid + ") Instant Apps do not support manifest receivers";
            } else {
                if (broadcastRecord.callerInstantApp) {
                    ActivityInfo activityInfo4 = resolveInfo.activityInfo;
                    if ((activityInfo4.flags & 1048576) == 0 && broadcastRecord.callingUid != activityInfo4.applicationInfo.uid) {
                        return "Instant App Denial: receiving " + broadcastRecord.intent + " to " + componentName.flattenToShortString() + " requires receiver have visibleToInstantApps set due to sender " + broadcastRecord.callerPackage + " (uid " + broadcastRecord.callingUid + ")";
                    }
                }
                ProcessRecord processRecord = broadcastRecord.curApp;
                if (processRecord != null && processRecord.mErrorState.isCrashing()) {
                    return "Skipping deliver ordered [" + broadcastRecord.queue.toString() + "] " + broadcastRecord + " to " + broadcastRecord.curApp + ": process crashing";
                }
                try {
                    IPackageManager packageManager = AppGlobals.getPackageManager();
                    ActivityInfo activityInfo5 = resolveInfo.activityInfo;
                    if (!packageManager.isPackageAvailable(activityInfo5.packageName, UserHandle.getUserId(activityInfo5.applicationInfo.uid))) {
                        return "Skipping delivery to " + resolveInfo.activityInfo.packageName + " / " + resolveInfo.activityInfo.applicationInfo.uid + " : package no longer available";
                    }
                    ActivityInfo activityInfo6 = resolveInfo.activityInfo;
                    if (!broadcastSkipPolicy.requestStartTargetPermissionsReviewIfNeededLocked(broadcastRecord, activityInfo6.packageName, UserHandle.getUserId(activityInfo6.applicationInfo.uid))) {
                        return "Skipping delivery: permission review required for " + broadcastDescription(broadcastRecord, componentName);
                    }
                    ActivityManagerService activityManagerService = broadcastSkipPolicy.mService;
                    ActivityInfo activityInfo7 = resolveInfo.activityInfo;
                    ApplicationInfo applicationInfo = activityInfo7.applicationInfo;
                    int appStartModeLOSP = activityManagerService.getAppStartModeLOSP(applicationInfo.uid, activityInfo7.packageName, applicationInfo.targetSdkVersion, -1, true, false, false);
                    if (appStartModeLOSP != 0) {
                        if (appStartModeLOSP == 3) {
                            return "Background execution disabled: receiving " + broadcastRecord.intent + " to " + componentName.flattenToShortString();
                        } else if (disallowBackgroundStart(broadcastRecord)) {
                            broadcastSkipPolicy.mService.addBackgroundCheckViolationLocked(broadcastRecord.intent.getAction(), componentName.getPackageName());
                            return "Background execution not allowed: receiving " + broadcastRecord.intent + " to " + componentName.flattenToShortString();
                        }
                    }
                    if (!"android.intent.action.ACTION_SHUTDOWN".equals(broadcastRecord.intent.getAction()) && !broadcastSkipPolicy.mService.mUserController.isUserRunning(UserHandle.getUserId(resolveInfo.activityInfo.applicationInfo.uid), 0)) {
                        return "Skipping delivery to " + resolveInfo.activityInfo.packageName + " / " + resolveInfo.activityInfo.applicationInfo.uid + " : user is not running";
                    }
                    String[] strArr2 = broadcastRecord.excludedPermissions;
                    if (strArr2 != null && strArr2.length > 0) {
                        int i4 = 0;
                        while (true) {
                            String[] strArr3 = broadcastRecord.excludedPermissions;
                            if (i4 >= strArr3.length) {
                                break;
                            }
                            String str2 = strArr3[i4];
                            try {
                                IPackageManager packageManager2 = AppGlobals.getPackageManager();
                                ApplicationInfo applicationInfo2 = resolveInfo.activityInfo.applicationInfo;
                                i2 = packageManager2.checkPermission(str2, applicationInfo2.packageName, UserHandle.getUserId(applicationInfo2.uid));
                            } catch (RemoteException unused) {
                                i2 = i3;
                            }
                            int permissionToOpCode2 = AppOpsManager.permissionToOpCode(str2);
                            if (permissionToOpCode2 != i3) {
                                if (i2 == 0) {
                                    AppOpsManager appOpsManager = broadcastSkipPolicy.mService.getAppOpsManager();
                                    ActivityInfo activityInfo8 = resolveInfo.activityInfo;
                                    if (appOpsManager.checkOpNoThrow(permissionToOpCode2, activityInfo8.applicationInfo.uid, activityInfo8.packageName) == 0) {
                                        return "Skipping delivery to " + resolveInfo.activityInfo.packageName + " due to excluded permission " + str2;
                                    }
                                } else {
                                    continue;
                                }
                            } else if (i2 == 0) {
                                return "Skipping delivery to " + resolveInfo.activityInfo.packageName + " due to excluded permission " + str2;
                            }
                            i4++;
                            i3 = -1;
                            broadcastSkipPolicy = this;
                        }
                    }
                    String[] strArr4 = broadcastRecord.excludedPackages;
                    if (strArr4 != null && strArr4.length > 0 && ArrayUtils.contains(strArr4, componentName.getPackageName())) {
                        return "Skipping delivery of excluded package " + broadcastRecord.intent + " to " + componentName.flattenToShortString() + " excludes package " + componentName.getPackageName() + " due to sender " + broadcastRecord.callerPackage + " (uid " + broadcastRecord.callingUid + ")";
                    }
                    if (resolveInfo.activityInfo.applicationInfo.uid != 1000 && (strArr = broadcastRecord.requiredPermissions) != null && strArr.length > 0) {
                        int i5 = 0;
                        while (true) {
                            String[] strArr5 = broadcastRecord.requiredPermissions;
                            if (i5 >= strArr5.length) {
                                break;
                            }
                            String str3 = strArr5[i5];
                            try {
                                IPackageManager packageManager3 = AppGlobals.getPackageManager();
                                ApplicationInfo applicationInfo3 = resolveInfo.activityInfo.applicationInfo;
                                i = packageManager3.checkPermission(str3, applicationInfo3.packageName, UserHandle.getUserId(applicationInfo3.uid));
                            } catch (RemoteException unused2) {
                                i = -1;
                            }
                            if (i != 0) {
                                return "Permission Denial: receiving " + broadcastRecord.intent + " to " + componentName.flattenToShortString() + " requires " + str3 + " due to sender " + broadcastRecord.callerPackage + " (uid " + broadcastRecord.callingUid + ")";
                            }
                            int permissionToOpCode3 = AppOpsManager.permissionToOpCode(str3);
                            if (permissionToOpCode3 != -1 && permissionToOpCode3 != broadcastRecord.appOp) {
                                if (!noteOpForManifestReceiver(permissionToOpCode3, broadcastRecord, resolveInfo, componentName)) {
                                    return "Skipping delivery to " + resolveInfo.activityInfo.packageName + " due to required appop " + permissionToOpCode3;
                                }
                            }
                            i5++;
                        }
                    }
                    int i6 = broadcastRecord.appOp;
                    if (i6 == -1 || noteOpForManifestReceiver(i6, broadcastRecord, resolveInfo, componentName)) {
                        return null;
                    }
                    return "Skipping delivery to " + resolveInfo.activityInfo.packageName + " due to required appop " + broadcastRecord.appOp;
                } catch (Exception unused3) {
                    return "Exception getting recipient info for " + resolveInfo.activityInfo.packageName;
                }
            }
        }
    }

    public boolean disallowBackgroundStart(BroadcastRecord broadcastRecord) {
        return (broadcastRecord.intent.getFlags() & 8388608) != 0 || (broadcastRecord.intent.getComponent() == null && broadcastRecord.intent.getPackage() == null && (broadcastRecord.intent.getFlags() & 16777216) == 0 && !isSignaturePerm(broadcastRecord.requiredPermissions));
    }

    public final String shouldSkipMessage(BroadcastRecord broadcastRecord, BroadcastFilter broadcastFilter) {
        String str;
        String str2;
        String str3;
        String str4;
        String str5;
        String str6;
        String str7;
        String str8;
        String str9;
        String str10;
        String str11;
        String str12;
        String str13;
        BroadcastSkipPolicy broadcastSkipPolicy = this;
        BroadcastOptions broadcastOptions = broadcastRecord.options;
        if (broadcastOptions != null && !broadcastOptions.testRequireCompatChange(broadcastFilter.owningUid)) {
            return "Compat change filtered: broadcasting " + broadcastRecord.intent.toString() + " to uid " + broadcastFilter.owningUid + " due to compat change " + broadcastRecord.options.getRequireCompatChangeId();
        } else if (!broadcastSkipPolicy.mService.validateAssociationAllowedLocked(broadcastRecord.callerPackage, broadcastRecord.callingUid, broadcastFilter.packageName, broadcastFilter.owningUid)) {
            return "Association not allowed: broadcasting " + broadcastRecord.intent.toString() + " from " + broadcastRecord.callerPackage + " (pid=" + broadcastRecord.callingPid + ", uid=" + broadcastRecord.callingUid + ") to " + broadcastFilter.packageName + " through " + broadcastFilter;
        } else if (!broadcastSkipPolicy.mService.mIntentFirewall.checkBroadcast(broadcastRecord.intent, broadcastRecord.callingUid, broadcastRecord.callingPid, broadcastRecord.resolvedType, broadcastFilter.receiverList.uid)) {
            return "Firewall blocked: broadcasting " + broadcastRecord.intent.toString() + " from " + broadcastRecord.callerPackage + " (pid=" + broadcastRecord.callingPid + ", uid=" + broadcastRecord.callingUid + ") to " + broadcastFilter.packageName + " through " + broadcastFilter;
        } else {
            String str14 = broadcastFilter.requiredPermission;
            String str15 = ") requires ";
            String str16 = ") requires appop ";
            if (str14 != null) {
                if (ActivityManagerService.checkComponentPermission(str14, broadcastRecord.callingPid, broadcastRecord.callingUid, -1, true) != 0) {
                    return "Permission Denial: broadcasting " + broadcastRecord.intent.toString() + " from " + broadcastRecord.callerPackage + " (pid=" + broadcastRecord.callingPid + ", uid=" + broadcastRecord.callingUid + ") requires " + broadcastFilter.requiredPermission + " due to registered receiver " + broadcastFilter;
                }
                int permissionToOpCode = AppOpsManager.permissionToOpCode(broadcastFilter.requiredPermission);
                if (permissionToOpCode != -1 && broadcastSkipPolicy.mService.getAppOpsManager().noteOpNoThrow(permissionToOpCode, broadcastRecord.callingUid, broadcastRecord.callerPackage, broadcastRecord.callerFeatureId, "Broadcast sent to protected receiver") != 0) {
                    return "Appop Denial: broadcasting " + broadcastRecord.intent.toString() + " from " + broadcastRecord.callerPackage + " (pid=" + broadcastRecord.callingPid + ", uid=" + broadcastRecord.callingUid + ") requires appop " + AppOpsManager.permissionToOp(broadcastFilter.requiredPermission) + " due to registered receiver " + broadcastFilter;
                }
            }
            ProcessRecord processRecord = broadcastFilter.receiverList.app;
            if (processRecord == null || processRecord.isKilled() || broadcastFilter.receiverList.app.mErrorState.isCrashing()) {
                return "Skipping deliver [" + broadcastRecord.queue.toString() + "] " + broadcastRecord + " to " + broadcastFilter.receiverList + ": process gone or crashing";
            }
            if (!((broadcastRecord.intent.getFlags() & 2097152) != 0) && broadcastFilter.instantApp && broadcastFilter.receiverList.uid != broadcastRecord.callingUid) {
                return "Instant App Denial: receiving " + broadcastRecord.intent.toString() + " to " + broadcastFilter.receiverList.app + " (pid=" + broadcastFilter.receiverList.pid + ", uid=" + broadcastFilter.receiverList.uid + ") due to sender " + broadcastRecord.callerPackage + " (uid " + broadcastRecord.callingUid + ") not specifying FLAG_RECEIVER_VISIBLE_TO_INSTANT_APPS";
            } else if (!broadcastFilter.visibleToInstantApp && broadcastRecord.callerInstantApp && broadcastFilter.receiverList.uid != broadcastRecord.callingUid) {
                return "Instant App Denial: receiving " + broadcastRecord.intent.toString() + " to " + broadcastFilter.receiverList.app + " (pid=" + broadcastFilter.receiverList.pid + ", uid=" + broadcastFilter.receiverList.uid + ") requires receiver be visible to instant apps due to sender " + broadcastRecord.callerPackage + " (uid " + broadcastRecord.callingUid + ")";
            } else {
                String[] strArr = broadcastRecord.requiredPermissions;
                String str17 = "Broadcast delivered to registered receiver ";
                String str18 = "Permission Denial: receiving ";
                String str19 = "Appop Denial: receiving ";
                if (strArr != null && strArr.length > 0) {
                    str4 = ") due to sender ";
                    int i = 0;
                    while (true) {
                        String[] strArr2 = broadcastRecord.requiredPermissions;
                        String str20 = str16;
                        if (i >= strArr2.length) {
                            str2 = str17;
                            str3 = str19;
                            str5 = str18;
                            str = str20;
                            break;
                        }
                        String str21 = strArr2[i];
                        ReceiverList receiverList = broadcastFilter.receiverList;
                        int i2 = i;
                        String str22 = str17;
                        String str23 = str19;
                        if (ActivityManagerService.checkComponentPermission(str21, receiverList.pid, receiverList.uid, -1, true) != 0) {
                            return str18 + broadcastRecord.intent.toString() + " to " + broadcastFilter.receiverList.app + " (pid=" + broadcastFilter.receiverList.pid + ", uid=" + broadcastFilter.receiverList.uid + str15 + str21 + " due to sender " + broadcastRecord.callerPackage + " (uid " + broadcastRecord.callingUid + ")";
                        }
                        int permissionToOpCode2 = AppOpsManager.permissionToOpCode(str21);
                        if (permissionToOpCode2 == -1 || permissionToOpCode2 == broadcastRecord.appOp) {
                            str9 = str15;
                            str10 = str18;
                            str11 = str20;
                            str12 = str23;
                            str13 = str22;
                        } else {
                            AppOpsManager appOpsManager = broadcastSkipPolicy.mService.getAppOpsManager();
                            int i3 = broadcastFilter.receiverList.uid;
                            String str24 = broadcastFilter.packageName;
                            str9 = str15;
                            String str25 = broadcastFilter.featureId;
                            str10 = str18;
                            StringBuilder sb = new StringBuilder();
                            sb.append(str22);
                            str13 = str22;
                            sb.append(broadcastFilter.receiverId);
                            if (appOpsManager.noteOpNoThrow(permissionToOpCode2, i3, str24, str25, sb.toString()) != 0) {
                                return str23 + broadcastRecord.intent.toString() + " to " + broadcastFilter.receiverList.app + " (pid=" + broadcastFilter.receiverList.pid + ", uid=" + broadcastFilter.receiverList.uid + str20 + AppOpsManager.permissionToOp(str21) + " due to sender " + broadcastRecord.callerPackage + " (uid " + broadcastRecord.callingUid + ")";
                            }
                            str11 = str20;
                            str12 = str23;
                        }
                        str19 = str12;
                        str16 = str11;
                        str15 = str9;
                        str18 = str10;
                        str17 = str13;
                        i = i2 + 1;
                        broadcastSkipPolicy = this;
                    }
                } else {
                    str = ") requires appop ";
                    str2 = "Broadcast delivered to registered receiver ";
                    str3 = "Appop Denial: receiving ";
                    str4 = ") due to sender ";
                    str5 = "Permission Denial: receiving ";
                }
                String[] strArr3 = broadcastRecord.requiredPermissions;
                if (strArr3 == null || strArr3.length == 0) {
                    ReceiverList receiverList2 = broadcastFilter.receiverList;
                    if (ActivityManagerService.checkComponentPermission(null, receiverList2.pid, receiverList2.uid, -1, true) != 0) {
                        return "Permission Denial: security check failed when receiving " + broadcastRecord.intent.toString() + " to " + broadcastFilter.receiverList.app + " (pid=" + broadcastFilter.receiverList.pid + ", uid=" + broadcastFilter.receiverList.uid + str4 + broadcastRecord.callerPackage + " (uid " + broadcastRecord.callingUid + ")";
                    }
                }
                String[] strArr4 = broadcastRecord.excludedPermissions;
                if (strArr4 != null && strArr4.length > 0) {
                    int i4 = 0;
                    while (true) {
                        String[] strArr5 = broadcastRecord.excludedPermissions;
                        if (i4 >= strArr5.length) {
                            break;
                        }
                        String str26 = strArr5[i4];
                        ReceiverList receiverList3 = broadcastFilter.receiverList;
                        int checkComponentPermission = ActivityManagerService.checkComponentPermission(str26, receiverList3.pid, receiverList3.uid, -1, true);
                        int permissionToOpCode3 = AppOpsManager.permissionToOpCode(str26);
                        if (permissionToOpCode3 == -1) {
                            str6 = str;
                            str7 = str2;
                            if (checkComponentPermission == 0) {
                                return str5 + broadcastRecord.intent.toString() + " to " + broadcastFilter.receiverList.app + " (pid=" + broadcastFilter.receiverList.pid + ", uid=" + broadcastFilter.receiverList.uid + ") excludes " + str26 + " due to sender " + broadcastRecord.callerPackage + " (uid " + broadcastRecord.callingUid + ")";
                            }
                        } else if (checkComponentPermission == 0) {
                            String str27 = str2;
                            str6 = str;
                            if (this.mService.getAppOpsManager().checkOpNoThrow(permissionToOpCode3, broadcastFilter.receiverList.uid, broadcastFilter.packageName) == 0) {
                                return str3 + broadcastRecord.intent.toString() + " to " + broadcastFilter.receiverList.app + " (pid=" + broadcastFilter.receiverList.pid + ", uid=" + broadcastFilter.receiverList.uid + ") excludes appop " + AppOpsManager.permissionToOp(str26) + " due to sender " + broadcastRecord.callerPackage + " (uid " + broadcastRecord.callingUid + ")";
                            }
                            str7 = str27;
                        } else {
                            str6 = str;
                            str8 = str5;
                            str7 = str2;
                            i4++;
                            str2 = str7;
                            str5 = str8;
                            str = str6;
                        }
                        str8 = str5;
                        i4++;
                        str2 = str7;
                        str5 = str8;
                        str = str6;
                    }
                }
                String str28 = str;
                String str29 = str2;
                String[] strArr6 = broadcastRecord.excludedPackages;
                if (strArr6 != null && strArr6.length > 0 && ArrayUtils.contains(strArr6, broadcastFilter.packageName)) {
                    return "Skipping delivery of excluded package " + broadcastRecord.intent.toString() + " to " + broadcastFilter.receiverList.app + " (pid=" + broadcastFilter.receiverList.pid + ", uid=" + broadcastFilter.receiverList.uid + ") excludes package " + broadcastFilter.packageName + " due to sender " + broadcastRecord.callerPackage + " (uid " + broadcastRecord.callingUid + ")";
                }
                if (broadcastRecord.appOp != -1) {
                    AppOpsManager appOpsManager2 = this.mService.getAppOpsManager();
                    int i5 = broadcastRecord.appOp;
                    int i6 = broadcastFilter.receiverList.uid;
                    String str30 = broadcastFilter.packageName;
                    String str31 = broadcastFilter.featureId;
                    if (appOpsManager2.noteOpNoThrow(i5, i6, str30, str31, str29 + broadcastFilter.receiverId) != 0) {
                        return str3 + broadcastRecord.intent.toString() + " to " + broadcastFilter.receiverList.app + " (pid=" + broadcastFilter.receiverList.pid + ", uid=" + broadcastFilter.receiverList.uid + str28 + AppOpsManager.opToName(broadcastRecord.appOp) + " due to sender " + broadcastRecord.callerPackage + " (uid " + broadcastRecord.callingUid + ")";
                    }
                }
                boolean z = broadcastFilter.exported;
                if (!z && ActivityManagerService.checkComponentPermission(null, broadcastRecord.callingPid, broadcastRecord.callingUid, broadcastFilter.receiverList.uid, z) != 0) {
                    return "Exported Denial: sending " + broadcastRecord.intent.toString() + ", action: " + broadcastRecord.intent.getAction() + " from " + broadcastRecord.callerPackage + " (uid=" + broadcastRecord.callingUid + ") due to receiver " + broadcastFilter.receiverList.app + " (uid " + broadcastFilter.receiverList.uid + ") not specifying RECEIVER_EXPORTED";
                } else if (requestStartTargetPermissionsReviewIfNeededLocked(broadcastRecord, broadcastFilter.packageName, broadcastFilter.owningUserId)) {
                    return null;
                } else {
                    return "Skipping delivery to " + broadcastFilter.packageName + " due to permissions review";
                }
            }
        }
    }

    public static String broadcastDescription(BroadcastRecord broadcastRecord, ComponentName componentName) {
        return broadcastRecord.intent.toString() + " from " + broadcastRecord.callerPackage + " (pid=" + broadcastRecord.callingPid + ", uid=" + broadcastRecord.callingUid + ") to " + componentName.flattenToShortString();
    }

    public final boolean noteOpForManifestReceiver(int i, BroadcastRecord broadcastRecord, ResolveInfo resolveInfo, ComponentName componentName) {
        if (ArrayUtils.isEmpty(resolveInfo.activityInfo.attributionTags)) {
            return noteOpForManifestReceiverInner(i, broadcastRecord, resolveInfo, componentName, null);
        }
        for (String str : resolveInfo.activityInfo.attributionTags) {
            if (!noteOpForManifestReceiverInner(i, broadcastRecord, resolveInfo, componentName, str)) {
                return false;
            }
        }
        return true;
    }

    public final boolean noteOpForManifestReceiverInner(int i, BroadcastRecord broadcastRecord, ResolveInfo resolveInfo, ComponentName componentName, String str) {
        AppOpsManager appOpsManager = this.mService.getAppOpsManager();
        ActivityInfo activityInfo = resolveInfo.activityInfo;
        int i2 = activityInfo.applicationInfo.uid;
        String str2 = activityInfo.packageName;
        if (appOpsManager.noteOpNoThrow(i, i2, str2, str, "Broadcast delivered to " + resolveInfo.activityInfo.name) != 0) {
            Slog.w("BroadcastQueue", "Appop Denial: receiving " + broadcastRecord.intent + " to " + componentName.flattenToShortString() + " requires appop " + AppOpsManager.opToName(i) + " due to sender " + broadcastRecord.callerPackage + " (uid " + broadcastRecord.callingUid + ")");
            return false;
        }
        return true;
    }

    public static boolean isSignaturePerm(String[] strArr) {
        if (strArr == null) {
            return false;
        }
        IPermissionManager permissionManager = AppGlobals.getPermissionManager();
        for (int length = strArr.length - 1; length >= 0; length--) {
            try {
                PermissionInfo permissionInfo = permissionManager.getPermissionInfo(strArr[length], PackageManagerShellCommandDataLoader.PACKAGE, 0);
                if (permissionInfo == null || (permissionInfo.protectionLevel & 31) != 2) {
                    return false;
                }
            } catch (RemoteException unused) {
                return false;
            }
        }
        return true;
    }

    public final boolean requestStartTargetPermissionsReviewIfNeededLocked(BroadcastRecord broadcastRecord, String str, final int i) {
        boolean z = true;
        if (this.mService.getPackageManagerInternal().isPermissionsReviewRequired(str, i)) {
            ProcessRecord processRecord = broadcastRecord.callerApp;
            if (processRecord != null && processRecord.mState.getSetSchedGroup() == 0) {
                z = false;
            }
            if (z && broadcastRecord.intent.getComponent() != null) {
                ActivityManagerService activityManagerService = this.mService;
                PendingIntentController pendingIntentController = activityManagerService.mPendingIntentController;
                String str2 = broadcastRecord.callerPackage;
                String str3 = broadcastRecord.callerFeatureId;
                int i2 = broadcastRecord.callingUid;
                int i3 = broadcastRecord.userId;
                Intent intent = broadcastRecord.intent;
                PendingIntentRecord intentSender = pendingIntentController.getIntentSender(1, str2, str3, i2, i3, null, null, 0, new Intent[]{intent}, new String[]{intent.resolveType(activityManagerService.mContext.getContentResolver())}, 1409286144, null);
                final Intent intent2 = new Intent("android.intent.action.REVIEW_PERMISSIONS");
                intent2.addFlags(411041792);
                intent2.putExtra("android.intent.extra.PACKAGE_NAME", str);
                intent2.putExtra("android.intent.extra.INTENT", new IntentSender(intentSender));
                this.mService.mHandler.post(new Runnable() { // from class: com.android.server.am.BroadcastSkipPolicy.1
                    @Override // java.lang.Runnable
                    public void run() {
                        BroadcastSkipPolicy.this.mService.mContext.startActivityAsUser(intent2, new UserHandle(i));
                    }
                });
            } else {
                Slog.w("BroadcastQueue", "u" + i + " Receiving a broadcast in package" + str + " requires a permissions review");
            }
            return false;
        }
        return true;
    }
}
