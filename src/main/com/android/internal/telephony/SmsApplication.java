package com.android.internal.telephony;

import android.Manifest;
import android.app.AppOpsManager;
import android.app.role.OnRoleHoldersChangedListener;
import android.app.role.RoleManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.p001pm.ActivityInfo;
import android.content.p001pm.ApplicationInfo;
import android.content.p001pm.PackageInfo;
import android.content.p001pm.PackageManager;
import android.content.p001pm.ResolveInfo;
import android.content.p001pm.ServiceInfo;
import android.net.Uri;
import android.p008os.AsyncTask;
import android.p008os.Binder;
import android.p008os.Process;
import android.p008os.UserHandle;
import android.p008os.UserManager;
import android.provider.Telephony;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.util.SparseArray;
import com.google.android.mms.ContentType;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
/* loaded from: classes3.dex */
public final class SmsApplication {
    public static final String ACTION_DEFAULT_SMS_PACKAGE_CHANGED_INTERNAL = "android.provider.action.DEFAULT_SMS_PACKAGE_CHANGED_INTERNAL";
    private static final boolean DEBUG = false;
    private static final boolean DEBUG_MULTIUSER = false;
    static final String LOG_TAG = "SmsApplication";
    public static final String MMS_SERVICE_PACKAGE_NAME = "com.android.mms.service";
    public static final String PERMISSION_MONITOR_DEFAULT_SMS_PACKAGE = "android.permission.MONITOR_DEFAULT_SMS_PACKAGE";
    public static final String PHONE_PACKAGE_NAME = "com.android.phone";
    private static final String SCHEME_MMS = "mms";
    private static final String SCHEME_MMSTO = "mmsto";
    private static final String SCHEME_SMS = "sms";
    private static final String SCHEME_SMSTO = "smsto";
    public static final String TELEPHONY_PROVIDER_PACKAGE_NAME = "com.android.providers.telephony";
    private static final String[] DEFAULT_APP_EXCLUSIVE_APPOPS = {AppOpsManager.OPSTR_READ_SMS, AppOpsManager.OPSTR_WRITE_SMS, AppOpsManager.OPSTR_RECEIVE_SMS, AppOpsManager.OPSTR_RECEIVE_WAP_PUSH, AppOpsManager.OPSTR_SEND_SMS, AppOpsManager.OPSTR_READ_CELL_BROADCASTS};
    private static SmsPackageMonitor sSmsPackageMonitor = null;
    private static SmsRoleListener sSmsRoleListener = null;

    /* loaded from: classes3.dex */
    public static class SmsApplicationData {
        private String mApplicationName;
        private String mMmsReceiverClass;
        public String mPackageName;
        private String mProviderChangedReceiverClass;
        private String mRespondViaMessageClass;
        private String mSendToClass;
        private String mSimFullReceiverClass;
        private String mSmsAppChangedReceiverClass;
        private String mSmsReceiverClass;
        private int mUid;

        public boolean isComplete() {
            return (this.mSmsReceiverClass == null || this.mMmsReceiverClass == null || this.mRespondViaMessageClass == null || this.mSendToClass == null) ? false : true;
        }

        public SmsApplicationData(String packageName, int uid) {
            this.mPackageName = packageName;
            this.mUid = uid;
        }

        public String getApplicationName(Context context) {
            if (this.mApplicationName == null) {
                PackageManager pm = context.getPackageManager();
                try {
                    ApplicationInfo appInfo = pm.getApplicationInfoAsUser(this.mPackageName, 0, UserHandle.getUserHandleForUid(this.mUid));
                    if (appInfo != null) {
                        CharSequence label = pm.getApplicationLabel(appInfo);
                        this.mApplicationName = label != null ? label.toString() : null;
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    return null;
                }
            }
            return this.mApplicationName;
        }

        public String toString() {
            return " mPackageName: " + this.mPackageName + " mSmsReceiverClass: " + this.mSmsReceiverClass + " mMmsReceiverClass: " + this.mMmsReceiverClass + " mRespondViaMessageClass: " + this.mRespondViaMessageClass + " mSendToClass: " + this.mSendToClass + " mSmsAppChangedClass: " + this.mSmsAppChangedReceiverClass + " mProviderChangedReceiverClass: " + this.mProviderChangedReceiverClass + " mSimFullReceiverClass: " + this.mSimFullReceiverClass + " mUid: " + this.mUid;
        }
    }

    private static int getIncomingUserId() {
        int contextUserId = UserHandle.myUserId();
        int callingUid = Binder.getCallingUid();
        if (UserHandle.getAppId(callingUid) < 10000) {
            return contextUserId;
        }
        return UserHandle.getUserHandleForUid(callingUid).getIdentifier();
    }

    private static UserHandle getIncomingUserHandle() {
        return UserHandle.m145of(getIncomingUserId());
    }

    public static Collection<SmsApplicationData> getApplicationCollection(Context context) {
        return getApplicationCollectionAsUser(context, getIncomingUserId());
    }

    public static Collection<SmsApplicationData> getApplicationCollectionAsUser(Context context, int userId) {
        long token = Binder.clearCallingIdentity();
        try {
            return getApplicationCollectionInternal(context, userId);
        } finally {
            Binder.restoreCallingIdentity(token);
        }
    }

    private static Collection<SmsApplicationData> getApplicationCollectionInternal(Context context, int userId) {
        String packageName;
        SmsApplicationData smsApplicationData;
        PackageManager packageManager;
        SmsApplicationData smsApplicationData2;
        SmsApplicationData smsApplicationData3;
        SmsApplicationData smsApplicationData4;
        SmsApplicationData smsApplicationData5;
        PackageManager packageManager2 = context.getPackageManager();
        UserHandle userHandle = UserHandle.m145of(userId);
        List<ResolveInfo> smsReceivers = packageManager2.queryBroadcastReceiversAsUser(new Intent(Telephony.Sms.Intents.SMS_DELIVER_ACTION), 786432, userHandle);
        HashMap<String, SmsApplicationData> receivers = new HashMap<>();
        for (ResolveInfo resolveInfo : smsReceivers) {
            ActivityInfo activityInfo = resolveInfo.activityInfo;
            if (activityInfo != null && Manifest.C0000permission.BROADCAST_SMS.equals(activityInfo.permission)) {
                String packageName2 = activityInfo.packageName;
                if (!receivers.containsKey(packageName2)) {
                    SmsApplicationData smsApplicationData6 = new SmsApplicationData(packageName2, activityInfo.applicationInfo.uid);
                    smsApplicationData6.mSmsReceiverClass = activityInfo.name;
                    receivers.put(packageName2, smsApplicationData6);
                }
            }
        }
        Intent intent = new Intent(Telephony.Sms.Intents.WAP_PUSH_DELIVER_ACTION);
        intent.setDataAndType(null, ContentType.MMS_MESSAGE);
        List<ResolveInfo> mmsReceivers = packageManager2.queryBroadcastReceiversAsUser(intent, 786432, userHandle);
        for (ResolveInfo resolveInfo2 : mmsReceivers) {
            ActivityInfo activityInfo2 = resolveInfo2.activityInfo;
            if (activityInfo2 != null && Manifest.C0000permission.BROADCAST_WAP_PUSH.equals(activityInfo2.permission) && (smsApplicationData5 = receivers.get(activityInfo2.packageName)) != null) {
                smsApplicationData5.mMmsReceiverClass = activityInfo2.name;
            }
        }
        List<ResolveInfo> respondServices = packageManager2.queryIntentServicesAsUser(new Intent(TelephonyManager.ACTION_RESPOND_VIA_MESSAGE, Uri.fromParts(SCHEME_SMSTO, "", null)), 786432, UserHandle.m145of(userId));
        for (ResolveInfo resolveInfo3 : respondServices) {
            ServiceInfo serviceInfo = resolveInfo3.serviceInfo;
            if (serviceInfo != null && Manifest.C0000permission.SEND_RESPOND_VIA_MESSAGE.equals(serviceInfo.permission)) {
                SmsApplicationData smsApplicationData7 = receivers.get(serviceInfo.packageName);
                if (smsApplicationData7 != null) {
                    smsApplicationData7.mRespondViaMessageClass = serviceInfo.name;
                }
            }
        }
        List<ResolveInfo> sendToActivities = packageManager2.queryIntentActivitiesAsUser(new Intent(Intent.ACTION_SENDTO, Uri.fromParts(SCHEME_SMSTO, "", null)), 786432, userHandle);
        for (ResolveInfo resolveInfo4 : sendToActivities) {
            ActivityInfo activityInfo3 = resolveInfo4.activityInfo;
            if (activityInfo3 != null && (smsApplicationData4 = receivers.get(activityInfo3.packageName)) != null) {
                smsApplicationData4.mSendToClass = activityInfo3.name;
            }
        }
        List<ResolveInfo> smsAppChangedReceivers = packageManager2.queryBroadcastReceiversAsUser(new Intent(Telephony.Sms.Intents.ACTION_DEFAULT_SMS_PACKAGE_CHANGED), 786432, userHandle);
        for (ResolveInfo resolveInfo5 : smsAppChangedReceivers) {
            ActivityInfo activityInfo4 = resolveInfo5.activityInfo;
            if (activityInfo4 != null && (smsApplicationData3 = receivers.get(activityInfo4.packageName)) != null) {
                smsApplicationData3.mSmsAppChangedReceiverClass = activityInfo4.name;
            }
        }
        List<ResolveInfo> providerChangedReceivers = packageManager2.queryBroadcastReceiversAsUser(new Intent(Telephony.Sms.Intents.ACTION_EXTERNAL_PROVIDER_CHANGE), 786432, userHandle);
        for (ResolveInfo resolveInfo6 : providerChangedReceivers) {
            ActivityInfo activityInfo5 = resolveInfo6.activityInfo;
            if (activityInfo5 != null && (smsApplicationData2 = receivers.get(activityInfo5.packageName)) != null) {
                smsApplicationData2.mProviderChangedReceiverClass = activityInfo5.name;
            }
        }
        List<ResolveInfo> simFullReceivers = packageManager2.queryBroadcastReceiversAsUser(new Intent(Telephony.Sms.Intents.SIM_FULL_ACTION), 786432, userHandle);
        for (ResolveInfo resolveInfo7 : simFullReceivers) {
            ActivityInfo activityInfo6 = resolveInfo7.activityInfo;
            if (activityInfo6 != null) {
                SmsApplicationData smsApplicationData8 = receivers.get(activityInfo6.packageName);
                if (smsApplicationData8 == null) {
                    packageManager = packageManager2;
                } else {
                    packageManager = packageManager2;
                    smsApplicationData8.mSimFullReceiverClass = activityInfo6.name;
                }
                packageManager2 = packageManager;
            }
        }
        for (ResolveInfo resolveInfo8 : smsReceivers) {
            ActivityInfo activityInfo7 = resolveInfo8.activityInfo;
            if (activityInfo7 != null && (smsApplicationData = receivers.get((packageName = activityInfo7.packageName))) != null && !smsApplicationData.isComplete()) {
                receivers.remove(packageName);
            }
        }
        return receivers.values();
    }

    public static SmsApplicationData getApplicationForPackage(Collection<SmsApplicationData> applications, String packageName) {
        if (packageName == null) {
            return null;
        }
        for (SmsApplicationData application : applications) {
            if (application.mPackageName.contentEquals(packageName)) {
                return application;
            }
        }
        return null;
    }

    private static SmsApplicationData getApplication(Context context, boolean updateIfNeeded, int userId) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService("phone");
        RoleManager roleManager = (RoleManager) context.getSystemService(Context.ROLE_SERVICE);
        if (!tm.isSmsCapable() && (roleManager == null || !roleManager.isRoleAvailable("android.app.role.SMS"))) {
            return null;
        }
        Collection<SmsApplicationData> applications = getApplicationCollectionInternal(context, userId);
        String defaultApplication = getDefaultSmsPackage(context, userId);
        SmsApplicationData applicationData = null;
        if (defaultApplication != null) {
            applicationData = getApplicationForPackage(applications, defaultApplication);
        }
        if (applicationData != null) {
            if (updateIfNeeded || applicationData.mUid == Process.myUid()) {
                boolean appOpsFixed = tryFixExclusiveSmsAppops(context, applicationData, updateIfNeeded);
                if (!appOpsFixed) {
                    applicationData = null;
                }
            }
            if (applicationData != null && updateIfNeeded) {
                defaultSmsAppChanged(context);
            }
        }
        return applicationData;
    }

    private static String getDefaultSmsPackage(Context context, int userId) {
        return ((RoleManager) context.getSystemService(RoleManager.class)).getSmsRoleHolder(userId);
    }

    private static void defaultSmsAppChanged(Context context) {
        String[] strArr;
        PackageManager packageManager = context.getPackageManager();
        AppOpsManager appOps = (AppOpsManager) context.getSystemService(AppOpsManager.class);
        String bluetoothPackageName = context.getResources().getString(17039427);
        assignExclusiveSmsPermissionsToSystemApp(context, packageManager, appOps, "com.android.phone", true);
        assignExclusiveSmsPermissionsToSystemApp(context, packageManager, appOps, bluetoothPackageName, false);
        assignExclusiveSmsPermissionsToSystemApp(context, packageManager, appOps, MMS_SERVICE_PACKAGE_NAME, true);
        assignExclusiveSmsPermissionsToSystemApp(context, packageManager, appOps, TELEPHONY_PROVIDER_PACKAGE_NAME, true);
        assignExclusiveSmsPermissionsToSystemApp(context, packageManager, appOps, CellBroadcastUtils.getDefaultCellBroadcastReceiverPackageName(context), false);
        for (String opStr : DEFAULT_APP_EXCLUSIVE_APPOPS) {
            appOps.setUidMode(opStr, 1001, 0);
        }
    }

    private static boolean tryFixExclusiveSmsAppops(Context context, SmsApplicationData applicationData, boolean updateIfNeeded) {
        String[] strArr;
        AppOpsManager appOps = (AppOpsManager) context.getSystemService(AppOpsManager.class);
        for (String opStr : DEFAULT_APP_EXCLUSIVE_APPOPS) {
            int mode = appOps.unsafeCheckOp(opStr, applicationData.mUid, applicationData.mPackageName);
            if (mode != 0) {
                Log.m110e(LOG_TAG, applicationData.mPackageName + " lost " + opStr + ": " + (updateIfNeeded ? " (fixing)" : " (no permission to fix)"));
                if (!updateIfNeeded) {
                    return false;
                }
                appOps.setUidMode(opStr, applicationData.mUid, 0);
            }
        }
        return true;
    }

    public static void setDefaultApplication(String packageName, Context context) {
        setDefaultApplicationAsUser(packageName, context, getIncomingUserId());
    }

    public static void setDefaultApplicationAsUser(String packageName, Context context, int userId) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService("phone");
        RoleManager roleManager = (RoleManager) context.getSystemService(Context.ROLE_SERVICE);
        if (!tm.isSmsCapable() && (roleManager == null || !roleManager.isRoleAvailable("android.app.role.SMS"))) {
            return;
        }
        long token = Binder.clearCallingIdentity();
        try {
            setDefaultApplicationInternal(packageName, context, userId);
        } finally {
            Binder.restoreCallingIdentity(token);
        }
    }

    private static void setDefaultApplicationInternal(String packageName, Context context, int userId) {
        UserHandle userHandle = UserHandle.m145of(userId);
        String oldPackageName = getDefaultSmsPackage(context, userId);
        if (packageName != null && oldPackageName != null && packageName.equals(oldPackageName)) {
            return;
        }
        PackageManager packageManager = context.createContextAsUser(userHandle, 0).getPackageManager();
        Collection<SmsApplicationData> applications = getApplicationCollectionInternal(context, userId);
        if (oldPackageName != null) {
            getApplicationForPackage(applications, oldPackageName);
        }
        SmsApplicationData applicationData = getApplicationForPackage(applications, packageName);
        if (applicationData != null) {
            AppOpsManager appOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            if (oldPackageName != null) {
                try {
                    int uid = packageManager.getPackageInfo(oldPackageName, 0).applicationInfo.uid;
                    setExclusiveAppops(oldPackageName, appOps, uid, 3);
                } catch (PackageManager.NameNotFoundException e) {
                    Log.m104w(LOG_TAG, "Old SMS package not found: " + oldPackageName);
                }
            }
            final CompletableFuture<Void> future = new CompletableFuture<>();
            Consumer<Boolean> callback = new Consumer() { // from class: com.android.internal.telephony.SmsApplication$$ExternalSyntheticLambda1
                @Override // java.util.function.Consumer
                public final void accept(Object obj) {
                    SmsApplication.lambda$setDefaultApplicationInternal$0(future, (Boolean) obj);
                }
            };
            ((RoleManager) context.getSystemService(RoleManager.class)).addRoleHolderAsUser("android.app.role.SMS", applicationData.mPackageName, 0, UserHandle.m145of(userId), AsyncTask.THREAD_POOL_EXECUTOR, callback);
            try {
                future.get(5L, TimeUnit.SECONDS);
                defaultSmsAppChanged(context);
            } catch (InterruptedException | ExecutionException | TimeoutException e2) {
                Log.m109e(LOG_TAG, "Exception while adding sms role holder " + applicationData, e2);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ void lambda$setDefaultApplicationInternal$0(CompletableFuture future, Boolean successful) {
        if (successful.booleanValue()) {
            future.complete(null);
        } else {
            future.completeExceptionally(new RuntimeException());
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void broadcastSmsAppChange(Context context, UserHandle userHandle, String oldPackage, String newPackage) {
        Collection<SmsApplicationData> apps = getApplicationCollection(context);
        broadcastSmsAppChange(context, userHandle, getApplicationForPackage(apps, oldPackage), getApplicationForPackage(apps, newPackage));
    }

    private static void broadcastSmsAppChange(Context context, UserHandle userHandle, SmsApplicationData oldAppData, SmsApplicationData applicationData) {
        if (oldAppData != null && oldAppData.mSmsAppChangedReceiverClass != null) {
            Intent oldAppIntent = new Intent(Telephony.Sms.Intents.ACTION_DEFAULT_SMS_PACKAGE_CHANGED);
            ComponentName component = new ComponentName(oldAppData.mPackageName, oldAppData.mSmsAppChangedReceiverClass);
            oldAppIntent.setComponent(component);
            oldAppIntent.putExtra(Telephony.Sms.Intents.EXTRA_IS_DEFAULT_SMS_APP, false);
            context.sendBroadcastAsUser(oldAppIntent, userHandle);
        }
        if (applicationData != null && applicationData.mSmsAppChangedReceiverClass != null) {
            Intent intent = new Intent(Telephony.Sms.Intents.ACTION_DEFAULT_SMS_PACKAGE_CHANGED);
            ComponentName component2 = new ComponentName(applicationData.mPackageName, applicationData.mSmsAppChangedReceiverClass);
            intent.setComponent(component2);
            intent.putExtra(Telephony.Sms.Intents.EXTRA_IS_DEFAULT_SMS_APP, true);
            context.sendBroadcastAsUser(intent, userHandle);
        }
        context.sendBroadcastAsUser(new Intent(ACTION_DEFAULT_SMS_PACKAGE_CHANGED_INTERNAL), userHandle, "android.permission.MONITOR_DEFAULT_SMS_PACKAGE");
    }

    private static void assignExclusiveSmsPermissionsToSystemApp(Context context, PackageManager packageManager, AppOpsManager appOps, String packageName, boolean sigatureMatch) {
        if (packageName == null) {
            return;
        }
        if (sigatureMatch) {
            int result = packageManager.checkSignatures(context.getPackageName(), packageName);
            if (result != 0) {
                Log.m110e(LOG_TAG, packageName + " does not have system signature");
                return;
            }
        }
        try {
            PackageInfo info = packageManager.getPackageInfo(packageName, 0);
            int mode = appOps.unsafeCheckOp(AppOpsManager.OPSTR_WRITE_SMS, info.applicationInfo.uid, packageName);
            if (mode != 0) {
                Log.m104w(LOG_TAG, packageName + " does not have OP_WRITE_SMS:  (fixing)");
                setExclusiveAppops(packageName, appOps, info.applicationInfo.uid, 0);
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.m110e(LOG_TAG, "Package not found: " + packageName);
        }
    }

    private static void setExclusiveAppops(String pkg, AppOpsManager appOpsManager, int uid, int mode) {
        String[] strArr;
        for (String opStr : DEFAULT_APP_EXCLUSIVE_APPOPS) {
            appOpsManager.setUidMode(opStr, uid, mode);
        }
    }

    /* loaded from: classes3.dex */
    private static final class SmsPackageMonitor extends PackageChangeReceiver {
        final Context mContext;

        public SmsPackageMonitor(Context context) {
            this.mContext = context;
        }

        @Override // com.android.internal.telephony.PackageChangeReceiver
        public void onPackageDisappeared() {
            onPackageChanged();
        }

        @Override // com.android.internal.telephony.PackageChangeReceiver
        public void onPackageAppeared() {
            onPackageChanged();
        }

        @Override // com.android.internal.telephony.PackageChangeReceiver
        public void onPackageModified(String packageName) {
            onPackageChanged();
        }

        private void onPackageChanged() {
            int userId;
            try {
                userId = getSendingUser().getIdentifier();
            } catch (NullPointerException e) {
                userId = UserHandle.SYSTEM.getIdentifier();
            }
            Context userContext = this.mContext;
            if (userId != UserHandle.SYSTEM.getIdentifier()) {
                try {
                    Context context = this.mContext;
                    userContext = context.createPackageContextAsUser(context.getPackageName(), 0, UserHandle.m145of(userId));
                } catch (PackageManager.NameNotFoundException e2) {
                }
            }
            PackageManager packageManager = userContext.getPackageManager();
            ComponentName componentName = SmsApplication.getDefaultSendToApplication(userContext, true);
            if (componentName != null) {
                SmsApplication.configurePreferredActivity(packageManager, componentName);
            }
        }
    }

    /* loaded from: classes3.dex */
    private static final class SmsRoleListener implements OnRoleHoldersChangedListener {
        private final Context mContext;
        private final RoleManager mRoleManager;
        private final SparseArray<String> mSmsPackageNames = new SparseArray<>();

        public SmsRoleListener(Context context) {
            this.mContext = context;
            this.mRoleManager = (RoleManager) context.getSystemService(RoleManager.class);
            List<UserHandle> users = ((UserManager) context.getSystemService(UserManager.class)).getUserHandles(true);
            int usersSize = users.size();
            for (int i = 0; i < usersSize; i++) {
                UserHandle user = users.get(i);
                this.mSmsPackageNames.put(user.getIdentifier(), getSmsPackageName(user));
            }
            this.mRoleManager.addOnRoleHoldersChangedListenerAsUser(context.getMainExecutor(), this, UserHandle.ALL);
        }

        public void onRoleHoldersChanged(String roleName, UserHandle user) {
            if (!Objects.equals(roleName, "android.app.role.SMS")) {
                return;
            }
            int userId = user.getIdentifier();
            String newSmsPackageName = getSmsPackageName(user);
            SmsApplication.broadcastSmsAppChange(this.mContext, user, this.mSmsPackageNames.get(userId), newSmsPackageName);
            this.mSmsPackageNames.put(userId, newSmsPackageName);
        }

        private String getSmsPackageName(UserHandle user) {
            List<String> roleHolders = this.mRoleManager.getRoleHoldersAsUser("android.app.role.SMS", user);
            if (roleHolders.isEmpty()) {
                return null;
            }
            return roleHolders.get(0);
        }
    }

    public static void initSmsPackageMonitor(Context context) {
        SmsPackageMonitor smsPackageMonitor = new SmsPackageMonitor(context);
        sSmsPackageMonitor = smsPackageMonitor;
        smsPackageMonitor.register(context, context.getMainLooper(), UserHandle.ALL);
        sSmsRoleListener = new SmsRoleListener(context);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void configurePreferredActivity(PackageManager packageManager, ComponentName componentName) {
        replacePreferredActivity(packageManager, componentName, "sms");
        replacePreferredActivity(packageManager, componentName, SCHEME_SMSTO);
        replacePreferredActivity(packageManager, componentName, "mms");
        replacePreferredActivity(packageManager, componentName, SCHEME_MMSTO);
    }

    private static void replacePreferredActivity(PackageManager packageManager, ComponentName componentName, String scheme) {
        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(scheme, "", null));
        List<ResolveInfo> resolveInfoList = packageManager.queryIntentActivities(intent, 65600);
        List<ComponentName> components = (List) resolveInfoList.stream().map(new Function() { // from class: com.android.internal.telephony.SmsApplication$$ExternalSyntheticLambda0
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return SmsApplication.lambda$replacePreferredActivity$1((ResolveInfo) obj);
            }
        }).collect(Collectors.toList());
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_SENDTO);
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        intentFilter.addDataScheme(scheme);
        packageManager.replacePreferredActivity(intentFilter, 2129920, components, componentName);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ ComponentName lambda$replacePreferredActivity$1(ResolveInfo info) {
        return new ComponentName(info.activityInfo.packageName, info.activityInfo.name);
    }

    public static SmsApplicationData getSmsApplicationData(String packageName, Context context) {
        Collection<SmsApplicationData> applications = getApplicationCollection(context);
        return getApplicationForPackage(applications, packageName);
    }

    public static ComponentName getDefaultSmsApplication(Context context, boolean updateIfNeeded) {
        return getDefaultSmsApplicationAsUser(context, updateIfNeeded, getIncomingUserHandle());
    }

    public static ComponentName getDefaultSmsApplicationAsUser(Context context, boolean updateIfNeeded, UserHandle userHandle) {
        if (userHandle == null) {
            userHandle = getIncomingUserHandle();
        }
        long token = Binder.clearCallingIdentity();
        ComponentName component = null;
        try {
            SmsApplicationData smsApplicationData = getApplication(context, updateIfNeeded, userHandle.getIdentifier());
            if (smsApplicationData != null) {
                component = new ComponentName(smsApplicationData.mPackageName, smsApplicationData.mSmsReceiverClass);
            }
            return component;
        } finally {
            Binder.restoreCallingIdentity(token);
        }
    }

    public static ComponentName getDefaultMmsApplication(Context context, boolean updateIfNeeded) {
        return getDefaultMmsApplicationAsUser(context, updateIfNeeded, getIncomingUserHandle());
    }

    public static ComponentName getDefaultMmsApplicationAsUser(Context context, boolean updateIfNeeded, UserHandle userHandle) {
        if (userHandle == null) {
            userHandle = getIncomingUserHandle();
        }
        long token = Binder.clearCallingIdentity();
        ComponentName component = null;
        try {
            SmsApplicationData smsApplicationData = getApplication(context, updateIfNeeded, userHandle.getIdentifier());
            if (smsApplicationData != null) {
                component = new ComponentName(smsApplicationData.mPackageName, smsApplicationData.mMmsReceiverClass);
            }
            return component;
        } finally {
            Binder.restoreCallingIdentity(token);
        }
    }

    public static ComponentName getDefaultRespondViaMessageApplication(Context context, boolean updateIfNeeded) {
        return getDefaultRespondViaMessageApplicationAsUser(context, updateIfNeeded, getIncomingUserHandle());
    }

    public static ComponentName getDefaultRespondViaMessageApplicationAsUser(Context context, boolean updateIfNeeded, UserHandle userHandle) {
        if (userHandle == null) {
            userHandle = getIncomingUserHandle();
        }
        long token = Binder.clearCallingIdentity();
        ComponentName component = null;
        try {
            SmsApplicationData smsApplicationData = getApplication(context, updateIfNeeded, userHandle.getIdentifier());
            if (smsApplicationData != null) {
                component = new ComponentName(smsApplicationData.mPackageName, smsApplicationData.mRespondViaMessageClass);
            }
            return component;
        } finally {
            Binder.restoreCallingIdentity(token);
        }
    }

    public static ComponentName getDefaultSendToApplication(Context context, boolean updateIfNeeded) {
        int userId = getIncomingUserId();
        long token = Binder.clearCallingIdentity();
        ComponentName component = null;
        try {
            SmsApplicationData smsApplicationData = getApplication(context, updateIfNeeded, userId);
            if (smsApplicationData != null) {
                component = new ComponentName(smsApplicationData.mPackageName, smsApplicationData.mSendToClass);
            }
            return component;
        } finally {
            Binder.restoreCallingIdentity(token);
        }
    }

    public static ComponentName getDefaultExternalTelephonyProviderChangedApplication(Context context, boolean updateIfNeeded) {
        return getDefaultExternalTelephonyProviderChangedApplicationAsUser(context, updateIfNeeded, getIncomingUserHandle());
    }

    public static ComponentName getDefaultExternalTelephonyProviderChangedApplicationAsUser(Context context, boolean updateIfNeeded, UserHandle userHandle) {
        if (userHandle == null) {
            userHandle = getIncomingUserHandle();
        }
        long token = Binder.clearCallingIdentity();
        ComponentName component = null;
        try {
            SmsApplicationData smsApplicationData = getApplication(context, updateIfNeeded, userHandle.getIdentifier());
            if (smsApplicationData != null && smsApplicationData.mProviderChangedReceiverClass != null) {
                component = new ComponentName(smsApplicationData.mPackageName, smsApplicationData.mProviderChangedReceiverClass);
            }
            return component;
        } finally {
            Binder.restoreCallingIdentity(token);
        }
    }

    public static ComponentName getDefaultSimFullApplication(Context context, boolean updateIfNeeded) {
        return getDefaultSimFullApplicationAsUser(context, updateIfNeeded, getIncomingUserHandle());
    }

    public static ComponentName getDefaultSimFullApplicationAsUser(Context context, boolean updateIfNeeded, UserHandle userHandle) {
        if (userHandle == null) {
            userHandle = getIncomingUserHandle();
        }
        long token = Binder.clearCallingIdentity();
        ComponentName component = null;
        try {
            SmsApplicationData smsApplicationData = getApplication(context, updateIfNeeded, userHandle.getIdentifier());
            if (smsApplicationData != null && smsApplicationData.mSimFullReceiverClass != null) {
                component = new ComponentName(smsApplicationData.mPackageName, smsApplicationData.mSimFullReceiverClass);
            }
            return component;
        } finally {
            Binder.restoreCallingIdentity(token);
        }
    }

    public static boolean shouldWriteMessageForPackage(String packageName, Context context) {
        return !shouldWriteMessageForPackageAsUser(packageName, context, getIncomingUserHandle());
    }

    public static boolean shouldWriteMessageForPackageAsUser(String packageName, Context context, UserHandle userHandle) {
        return !isDefaultSmsApplicationAsUser(context, packageName, userHandle);
    }

    public static boolean isDefaultSmsApplication(Context context, String packageName) {
        return isDefaultSmsApplicationAsUser(context, packageName, getIncomingUserHandle());
    }

    public static boolean isDefaultSmsApplicationAsUser(Context context, String packageName, UserHandle userHandle) {
        String defaultSmsPackage;
        if (packageName == null) {
            return false;
        }
        if (userHandle == null) {
            userHandle = getIncomingUserHandle();
        }
        ComponentName component = getDefaultSmsApplicationAsUser(context, false, userHandle);
        if (component == null || (defaultSmsPackage = component.getPackageName()) == null) {
            return false;
        }
        String bluetoothPackageName = context.getResources().getString(17039427);
        if (!defaultSmsPackage.equals(packageName) && !bluetoothPackageName.equals(packageName)) {
            return false;
        }
        return true;
    }

    public static boolean isDefaultMmsApplication(Context context, String packageName) {
        return isDefaultMmsApplicationAsUser(context, packageName, getIncomingUserHandle());
    }

    public static boolean isDefaultMmsApplicationAsUser(Context context, String packageName, UserHandle userHandle) {
        String defaultMmsPackage;
        if (packageName == null) {
            return false;
        }
        if (userHandle == null) {
            userHandle = getIncomingUserHandle();
        }
        ComponentName component = getDefaultMmsApplicationAsUser(context, false, userHandle);
        if (component == null || (defaultMmsPackage = component.getPackageName()) == null) {
            return false;
        }
        String bluetoothPackageName = context.getResources().getString(17039427);
        if (!defaultMmsPackage.equals(packageName) && !bluetoothPackageName.equals(packageName)) {
            return false;
        }
        return true;
    }
}
