package com.android.server.p011pm;

import android.app.ActivityManager;
import android.app.ActivityManagerInternal;
import android.app.BroadcastOptions;
import android.app.IActivityManager;
import android.app.IApplicationThread;
import android.content.ComponentName;
import android.content.Context;
import android.content.IIntentReceiver;
import android.content.Intent;
import android.content.pm.PackageInstaller;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.UserHandle;
import android.provider.DeviceConfig;
import android.util.IntArray;
import android.util.Pair;
import android.util.SparseArray;
import com.android.internal.util.ArrayUtils;
import java.util.ArrayList;
import java.util.function.BiFunction;
import java.util.function.Supplier;
/* renamed from: com.android.server.pm.BroadcastHelper */
/* loaded from: classes2.dex */
public final class BroadcastHelper {
    public static final String[] INSTANT_APP_BROADCAST_PERMISSION = {"android.permission.ACCESS_INSTANT_APPS"};
    public final ActivityManagerInternal mAmInternal;
    public final Context mContext;
    public final UserManagerInternal mUmInternal;

    public BroadcastHelper(PackageManagerServiceInjector packageManagerServiceInjector) {
        this.mUmInternal = packageManagerServiceInjector.getUserManagerInternal();
        this.mAmInternal = packageManagerServiceInjector.getActivityManagerInternal();
        this.mContext = packageManagerServiceInjector.getContext();
    }

    public void sendPackageBroadcast(String str, String str2, Bundle bundle, int i, String str3, IIntentReceiver iIntentReceiver, int[] iArr, int[] iArr2, SparseArray<int[]> sparseArray, BiFunction<Integer, Bundle, Bundle> biFunction, Bundle bundle2) {
        try {
            IActivityManager service = ActivityManager.getService();
            if (service == null) {
                return;
            }
            int[] runningUserIds = iArr == null ? service.getRunningUserIds() : iArr;
            if (ArrayUtils.isEmpty(iArr2)) {
                doSendBroadcast(str, str2, bundle, i, str3, iIntentReceiver, runningUserIds, false, sparseArray, biFunction, bundle2);
            } else {
                doSendBroadcast(str, str2, bundle, i, str3, iIntentReceiver, iArr2, true, null, null, bundle2);
            }
        } catch (RemoteException unused) {
        }
    }

    public void doSendBroadcast(String str, String str2, Bundle bundle, int i, String str3, IIntentReceiver iIntentReceiver, int[] iArr, boolean z, SparseArray<int[]> sparseArray, BiFunction<Integer, Bundle, Bundle> biFunction, Bundle bundle2) {
        String str4;
        Uri uri;
        for (int i2 : iArr) {
            int[] iArr2 = null;
            if (str2 != null) {
                uri = Uri.fromParts("package", str2, null);
                str4 = str;
            } else {
                str4 = str;
                uri = null;
            }
            Intent intent = new Intent(str4, uri);
            String[] strArr = z ? INSTANT_APP_BROADCAST_PERMISSION : null;
            if (bundle != null) {
                intent.putExtras(bundle);
            }
            if (str3 != null) {
                intent.setPackage(str3);
            }
            int intExtra = intent.getIntExtra("android.intent.extra.UID", -1);
            if (intExtra >= 0 && UserHandle.getUserId(intExtra) != i2) {
                intent.putExtra("android.intent.extra.UID", UserHandle.getUid(i2, UserHandle.getAppId(intExtra)));
            }
            if (sparseArray != null && PackageManagerShellCommandDataLoader.PACKAGE.equals(str3)) {
                intent.putExtra("android.intent.extra.VISIBILITY_ALLOW_LIST", sparseArray.get(i2));
            }
            intent.putExtra("android.intent.extra.user_handle", i2);
            intent.addFlags(i | 67108864);
            boolean z2 = this.mAmInternal.isModernQueueEnabled() ? false : iIntentReceiver != null;
            ActivityManagerInternal activityManagerInternal = this.mAmInternal;
            if (sparseArray != null) {
                iArr2 = sparseArray.get(i2);
            }
            activityManagerInternal.broadcastIntent(intent, iIntentReceiver, strArr, z2, i2, iArr2, biFunction, bundle2);
        }
    }

    public void sendResourcesChangedBroadcast(final Supplier<Computer> supplier, boolean z, boolean z2, String[] strArr, int[] iArr) {
        if (ArrayUtils.isEmpty(strArr) || ArrayUtils.isEmpty(iArr)) {
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putStringArray("android.intent.extra.changed_package_list", strArr);
        bundle.putIntArray("android.intent.extra.changed_uid_list", iArr);
        if (z2) {
            bundle.putBoolean("android.intent.extra.REPLACING", z2);
        }
        sendPackageBroadcast(z ? "android.intent.action.EXTERNAL_APPLICATIONS_AVAILABLE" : "android.intent.action.EXTERNAL_APPLICATIONS_UNAVAILABLE", null, bundle, 0, null, null, null, null, null, new BiFunction() { // from class: com.android.server.pm.BroadcastHelper$$ExternalSyntheticLambda0
            @Override // java.util.function.BiFunction
            public final Object apply(Object obj, Object obj2) {
                Bundle lambda$sendResourcesChangedBroadcast$0;
                lambda$sendResourcesChangedBroadcast$0 = BroadcastHelper.lambda$sendResourcesChangedBroadcast$0(supplier, (Integer) obj, (Bundle) obj2);
                return lambda$sendResourcesChangedBroadcast$0;
            }
        }, null);
    }

    public static /* synthetic */ Bundle lambda$sendResourcesChangedBroadcast$0(Supplier supplier, Integer num, Bundle bundle) {
        return filterExtrasChangedPackageList((Computer) supplier.get(), num.intValue(), bundle);
    }

    public void sendBootCompletedBroadcastToSystemApp(String str, boolean z, int i) {
        if (this.mUmInternal.isUserRunning(i)) {
            IActivityManager service = ActivityManager.getService();
            try {
                Intent intent = new Intent("android.intent.action.LOCKED_BOOT_COMPLETED").setPackage(str);
                if (z) {
                    intent.addFlags(32);
                }
                String[] strArr = {"android.permission.RECEIVE_BOOT_COMPLETED"};
                BroadcastOptions temporaryAppAllowlistBroadcastOptions = getTemporaryAppAllowlistBroadcastOptions(202);
                service.broadcastIntentWithFeature((IApplicationThread) null, (String) null, intent, (String) null, (IIntentReceiver) null, 0, (String) null, (Bundle) null, strArr, (String[]) null, (String[]) null, -1, temporaryAppAllowlistBroadcastOptions.toBundle(), false, false, i);
                if (this.mUmInternal.isUserUnlockingOrUnlocked(i)) {
                    Intent intent2 = new Intent("android.intent.action.BOOT_COMPLETED").setPackage(str);
                    if (z) {
                        intent2.addFlags(32);
                    }
                    service.broadcastIntentWithFeature((IApplicationThread) null, (String) null, intent2, (String) null, (IIntentReceiver) null, 0, (String) null, (Bundle) null, strArr, (String[]) null, (String[]) null, -1, temporaryAppAllowlistBroadcastOptions.toBundle(), false, false, i);
                }
            } catch (RemoteException e) {
                throw e.rethrowFromSystemServer();
            }
        }
    }

    public BroadcastOptions getTemporaryAppAllowlistBroadcastOptions(int i) {
        ActivityManagerInternal activityManagerInternal = this.mAmInternal;
        long bootTimeTempAllowListDuration = activityManagerInternal != null ? activityManagerInternal.getBootTimeTempAllowListDuration() : 10000L;
        BroadcastOptions makeBasic = BroadcastOptions.makeBasic();
        makeBasic.setTemporaryAppAllowlist(bootTimeTempAllowListDuration, 0, i, "");
        return makeBasic;
    }

    public void sendPackageChangedBroadcast(String str, boolean z, ArrayList<String> arrayList, int i, String str2, int[] iArr, int[] iArr2, SparseArray<int[]> sparseArray) {
        Bundle bundle = new Bundle(4);
        bundle.putString("android.intent.extra.changed_component_name", arrayList.get(0));
        String[] strArr = new String[arrayList.size()];
        arrayList.toArray(strArr);
        bundle.putStringArray("android.intent.extra.changed_component_name_list", strArr);
        bundle.putBoolean("android.intent.extra.DONT_KILL_APP", z);
        bundle.putInt("android.intent.extra.UID", i);
        if (str2 != null) {
            bundle.putString("android.intent.extra.REASON", str2);
        }
        sendPackageBroadcast("android.intent.action.PACKAGE_CHANGED", str, bundle, !arrayList.contains(str) ? 1073741824 : 0, null, null, iArr, iArr2, sparseArray, null, null);
    }

    public static void sendDeviceCustomizationReadyBroadcast() {
        Intent intent = new Intent("android.intent.action.DEVICE_CUSTOMIZATION_READY");
        intent.setFlags(16777216);
        try {
            ActivityManager.getService().broadcastIntentWithFeature((IApplicationThread) null, (String) null, intent, (String) null, (IIntentReceiver) null, 0, (String) null, (Bundle) null, new String[]{"android.permission.RECEIVE_DEVICE_CUSTOMIZATION_READY"}, (String[]) null, (String[]) null, -1, (Bundle) null, false, false, -1);
        } catch (RemoteException e) {
            throw e.rethrowFromSystemServer();
        }
    }

    public void sendSessionCommitBroadcast(PackageInstaller.SessionInfo sessionInfo, int i, int i2, ComponentName componentName, String str) {
        if (componentName != null) {
            this.mContext.sendBroadcastAsUser(new Intent("android.content.pm.action.SESSION_COMMITTED").putExtra("android.content.pm.extra.SESSION", sessionInfo).putExtra("android.intent.extra.USER", UserHandle.of(i)).setPackage(componentName.getPackageName()), UserHandle.of(i2));
        }
        if (str != null) {
            this.mContext.sendBroadcastAsUser(new Intent("android.content.pm.action.SESSION_COMMITTED").putExtra("android.content.pm.extra.SESSION", sessionInfo).putExtra("android.intent.extra.USER", UserHandle.of(i)).setPackage(str), UserHandle.of(i2));
        }
    }

    public void sendPreferredActivityChangedBroadcast(int i) {
        IActivityManager service = ActivityManager.getService();
        if (service == null) {
            return;
        }
        Intent intent = new Intent("android.intent.action.ACTION_PREFERRED_ACTIVITY_CHANGED");
        intent.putExtra("android.intent.extra.user_handle", i);
        intent.addFlags(67108864);
        try {
            service.broadcastIntentWithFeature((IApplicationThread) null, (String) null, intent, (String) null, (IIntentReceiver) null, 0, (String) null, (Bundle) null, (String[]) null, (String[]) null, (String[]) null, -1, (Bundle) null, false, false, i);
        } catch (RemoteException unused) {
        }
    }

    public void sendPackageAddedForNewUsers(String str, int i, int[] iArr, int[] iArr2, int i2, SparseArray<int[]> sparseArray) {
        Bundle bundle = new Bundle(1);
        bundle.putInt("android.intent.extra.UID", UserHandle.getUid(ArrayUtils.isEmpty(iArr) ? iArr2[0] : iArr[0], i));
        bundle.putInt("android.content.pm.extra.DATA_LOADER_TYPE", i2);
        sendPackageBroadcast("android.intent.action.PACKAGE_ADDED", str, bundle, 0, null, null, iArr, iArr2, sparseArray, null, null);
        if (isPrivacySafetyLabelChangeNotificationsEnabled()) {
            sendPackageBroadcast("android.intent.action.PACKAGE_ADDED", str, bundle, 0, this.mContext.getPackageManager().getPermissionControllerPackageName(), null, iArr, iArr2, sparseArray, null, null);
        }
    }

    public void sendFirstLaunchBroadcast(String str, String str2, int[] iArr, int[] iArr2) {
        sendPackageBroadcast("android.intent.action.PACKAGE_FIRST_LAUNCH", str, null, 0, str2, null, iArr, iArr2, null, null, null);
    }

    public static Bundle filterExtrasChangedPackageList(Computer computer, int i, Bundle bundle) {
        if (UserHandle.isCore(i)) {
            return bundle;
        }
        String[] stringArray = bundle.getStringArray("android.intent.extra.changed_package_list");
        if (ArrayUtils.isEmpty(stringArray)) {
            return bundle;
        }
        Pair<String[], int[]> filterPackages = filterPackages(computer, stringArray, bundle.getIntArray("android.intent.extra.changed_uid_list"), i, bundle.getInt("android.intent.extra.user_handle", UserHandle.getUserId(i)));
        if (ArrayUtils.isEmpty((String[]) filterPackages.first)) {
            return null;
        }
        Bundle bundle2 = new Bundle(bundle);
        bundle2.putStringArray("android.intent.extra.changed_package_list", (String[]) filterPackages.first);
        bundle2.putIntArray("android.intent.extra.changed_uid_list", (int[]) filterPackages.second);
        return bundle2;
    }

    public static boolean isPrivacySafetyLabelChangeNotificationsEnabled() {
        return DeviceConfig.getBoolean("privacy", "safety_label_change_notifications_enabled", false);
    }

    public static Pair<String[], int[]> filterPackages(Computer computer, String[] strArr, int[] iArr, int i, int i2) {
        int length = strArr.length;
        int length2 = !ArrayUtils.isEmpty(iArr) ? iArr.length : 0;
        ArrayList arrayList = new ArrayList(length);
        int[] iArr2 = null;
        IntArray intArray = length2 > 0 ? new IntArray(length2) : null;
        for (int i3 = 0; i3 < length; i3++) {
            String str = strArr[i3];
            if (!computer.shouldFilterApplication(computer.getPackageStateInternal(str), i, i2)) {
                arrayList.add(str);
                if (intArray != null && i3 < length2) {
                    intArray.add(iArr[i3]);
                }
            }
        }
        String[] strArr2 = arrayList.size() > 0 ? (String[]) arrayList.toArray(new String[arrayList.size()]) : null;
        if (intArray != null && intArray.size() > 0) {
            iArr2 = intArray.toArray();
        }
        return new Pair<>(strArr2, iArr2);
    }
}
