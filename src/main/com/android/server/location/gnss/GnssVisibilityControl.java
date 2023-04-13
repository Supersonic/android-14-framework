package com.android.server.location.gnss;

import android.annotation.SuppressLint;
import android.app.AppOpsManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;
import com.android.internal.location.GpsNetInitiatedHandler;
import com.android.internal.notification.SystemNotificationChannels;
import com.android.internal.util.FrameworkStatsLog;
import com.android.server.backup.BackupAgentTimeoutParameters;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class GnssVisibilityControl {
    public static final boolean DEBUG = Log.isLoggable("GnssVisibilityControl", 3);
    public static final String[] NO_LOCATION_ENABLED_PROXY_APPS = new String[0];
    public final AppOpsManager mAppOps;
    public final Context mContext;
    public final Handler mHandler;
    public boolean mIsGpsEnabled;
    public final GpsNetInitiatedHandler mNiHandler;
    public final PackageManager mPackageManager;
    public final PowerManager.WakeLock mWakeLock;
    public ArrayMap<String, ProxyAppState> mProxyAppsState = new ArrayMap<>(5);
    public PackageManager.OnPermissionsChangedListener mOnPermissionsChangedListener = new PackageManager.OnPermissionsChangedListener() { // from class: com.android.server.location.gnss.GnssVisibilityControl$$ExternalSyntheticLambda4
        public final void onPermissionsChanged(int i) {
            GnssVisibilityControl.this.lambda$new$1(i);
        }
    };

    private native boolean native_enable_nfw_location_access(String[] strArr);

    /* loaded from: classes.dex */
    public static final class ProxyAppState {
        public boolean mHasLocationPermission;
        public boolean mIsLocationIconOn;

        public ProxyAppState(boolean z) {
            this.mHasLocationPermission = z;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$1(final int i) {
        runOnHandler(new Runnable() { // from class: com.android.server.location.gnss.GnssVisibilityControl$$ExternalSyntheticLambda7
            @Override // java.lang.Runnable
            public final void run() {
                GnssVisibilityControl.this.lambda$new$0(i);
            }
        });
    }

    public GnssVisibilityControl(Context context, Looper looper, GpsNetInitiatedHandler gpsNetInitiatedHandler) {
        this.mContext = context;
        this.mWakeLock = ((PowerManager) context.getSystemService("power")).newWakeLock(1, "GnssVisibilityControl");
        this.mHandler = new Handler(looper);
        this.mNiHandler = gpsNetInitiatedHandler;
        this.mAppOps = (AppOpsManager) context.getSystemService(AppOpsManager.class);
        this.mPackageManager = context.getPackageManager();
        runOnHandler(new Runnable() { // from class: com.android.server.location.gnss.GnssVisibilityControl$$ExternalSyntheticLambda5
            @Override // java.lang.Runnable
            public final void run() {
                GnssVisibilityControl.this.handleInitialize();
            }
        });
    }

    public void onGpsEnabledChanged(final boolean z) {
        if (this.mHandler.runWithScissors(new Runnable() { // from class: com.android.server.location.gnss.GnssVisibilityControl$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                GnssVisibilityControl.this.lambda$onGpsEnabledChanged$2(z);
            }
        }, BackupAgentTimeoutParameters.DEFAULT_QUOTA_EXCEEDED_TIMEOUT_MILLIS) || z) {
            return;
        }
        Log.w("GnssVisibilityControl", "Native call to disable non-framework location access in GNSS HAL may get executed after native_cleanup().");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$reportNfwNotification$3(String str, byte b, String str2, byte b2, String str3, byte b3, boolean z, boolean z2) {
        handleNfwNotification(new NfwNotification(str, b, str2, b2, str3, b3, z, z2));
    }

    public void reportNfwNotification(final String str, final byte b, final String str2, final byte b2, final String str3, final byte b3, final boolean z, final boolean z2) {
        runOnHandler(new Runnable() { // from class: com.android.server.location.gnss.GnssVisibilityControl$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                GnssVisibilityControl.this.lambda$reportNfwNotification$3(str, b, str2, b2, str3, b3, z, z2);
            }
        });
    }

    public void onConfigurationUpdated(GnssConfiguration gnssConfiguration) {
        final List<String> proxyApps = gnssConfiguration.getProxyApps();
        runOnHandler(new Runnable() { // from class: com.android.server.location.gnss.GnssVisibilityControl$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                GnssVisibilityControl.this.lambda$onConfigurationUpdated$4(proxyApps);
            }
        });
    }

    public final void handleInitialize() {
        listenForProxyAppsPackageUpdates();
    }

    public final void listenForProxyAppsPackageUpdates() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.PACKAGE_ADDED");
        intentFilter.addAction("android.intent.action.PACKAGE_REMOVED");
        intentFilter.addAction("android.intent.action.PACKAGE_REPLACED");
        intentFilter.addAction("android.intent.action.PACKAGE_CHANGED");
        intentFilter.addDataScheme("package");
        this.mContext.registerReceiverAsUser(new BroadcastReceiver() { // from class: com.android.server.location.gnss.GnssVisibilityControl.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (action == null) {
                    return;
                }
                char c = 65535;
                switch (action.hashCode()) {
                    case -810471698:
                        if (action.equals("android.intent.action.PACKAGE_REPLACED")) {
                            c = 0;
                            break;
                        }
                        break;
                    case 172491798:
                        if (action.equals("android.intent.action.PACKAGE_CHANGED")) {
                            c = 1;
                            break;
                        }
                        break;
                    case 525384130:
                        if (action.equals("android.intent.action.PACKAGE_REMOVED")) {
                            c = 2;
                            break;
                        }
                        break;
                    case 1544582882:
                        if (action.equals("android.intent.action.PACKAGE_ADDED")) {
                            c = 3;
                            break;
                        }
                        break;
                }
                switch (c) {
                    case 0:
                    case 1:
                    case 2:
                    case 3:
                        GnssVisibilityControl.this.handleProxyAppPackageUpdate(intent.getData().getEncodedSchemeSpecificPart(), action);
                        return;
                    default:
                        return;
                }
            }
        }, UserHandle.ALL, intentFilter, null, this.mHandler);
    }

    public final void handleProxyAppPackageUpdate(String str, String str2) {
        ProxyAppState proxyAppState = this.mProxyAppsState.get(str);
        if (proxyAppState == null) {
            return;
        }
        if (DEBUG) {
            Log.d("GnssVisibilityControl", "Proxy app " + str + " package changed: " + str2);
        }
        boolean shouldEnableLocationPermissionInGnssHal = shouldEnableLocationPermissionInGnssHal(str);
        if (proxyAppState.mHasLocationPermission != shouldEnableLocationPermissionInGnssHal) {
            Log.i("GnssVisibilityControl", "Proxy app " + str + " location permission changed. IsLocationPermissionEnabled: " + shouldEnableLocationPermissionInGnssHal);
            proxyAppState.mHasLocationPermission = shouldEnableLocationPermissionInGnssHal;
            updateNfwLocationAccessProxyAppsInGnssHal();
        }
    }

    /* renamed from: handleUpdateProxyApps */
    public final void lambda$onConfigurationUpdated$4(List<String> list) {
        if (isProxyAppListUpdated(list)) {
            if (list.isEmpty()) {
                if (this.mProxyAppsState.isEmpty()) {
                    return;
                }
                this.mPackageManager.removeOnPermissionsChangeListener(this.mOnPermissionsChangedListener);
                resetProxyAppsState();
                updateNfwLocationAccessProxyAppsInGnssHal();
                return;
            }
            if (this.mProxyAppsState.isEmpty()) {
                this.mPackageManager.addOnPermissionsChangeListener(this.mOnPermissionsChangedListener);
            } else {
                resetProxyAppsState();
            }
            for (String str : list) {
                this.mProxyAppsState.put(str, new ProxyAppState(shouldEnableLocationPermissionInGnssHal(str)));
            }
            updateNfwLocationAccessProxyAppsInGnssHal();
        }
    }

    public final void resetProxyAppsState() {
        for (Map.Entry<String, ProxyAppState> entry : this.mProxyAppsState.entrySet()) {
            ProxyAppState value = entry.getValue();
            if (value.mIsLocationIconOn) {
                this.mHandler.removeCallbacksAndMessages(value);
                ApplicationInfo proxyAppInfo = getProxyAppInfo(entry.getKey());
                if (proxyAppInfo != null) {
                    clearLocationIcon(value, proxyAppInfo.uid, entry.getKey());
                }
            }
        }
        this.mProxyAppsState.clear();
    }

    public final boolean isProxyAppListUpdated(List<String> list) {
        if (list.size() != this.mProxyAppsState.size()) {
            return true;
        }
        for (String str : list) {
            if (!this.mProxyAppsState.containsKey(str)) {
                return true;
            }
        }
        return false;
    }

    /* renamed from: handleGpsEnabledChanged */
    public final void lambda$onGpsEnabledChanged$2(boolean z) {
        if (DEBUG) {
            Log.d("GnssVisibilityControl", "handleGpsEnabledChanged, mIsGpsEnabled: " + this.mIsGpsEnabled + ", isGpsEnabled: " + z);
        }
        this.mIsGpsEnabled = z;
        if (!z) {
            disableNfwLocationAccess();
        } else {
            setNfwLocationAccessProxyAppsInGnssHal(getLocationPermissionEnabledProxyApps());
        }
    }

    public final void disableNfwLocationAccess() {
        setNfwLocationAccessProxyAppsInGnssHal(NO_LOCATION_ENABLED_PROXY_APPS);
    }

    /* loaded from: classes.dex */
    public static class NfwNotification {
        public final boolean mInEmergencyMode;
        public final boolean mIsCachedLocation;
        public final String mOtherProtocolStackName;
        public final byte mProtocolStack;
        public final String mProxyAppPackageName;
        public final byte mRequestor;
        public final String mRequestorId;
        public final byte mResponseType;

        public NfwNotification(String str, byte b, String str2, byte b2, String str3, byte b3, boolean z, boolean z2) {
            this.mProxyAppPackageName = str;
            this.mProtocolStack = b;
            this.mOtherProtocolStackName = str2;
            this.mRequestor = b2;
            this.mRequestorId = str3;
            this.mResponseType = b3;
            this.mInEmergencyMode = z;
            this.mIsCachedLocation = z2;
        }

        @SuppressLint({"DefaultLocale"})
        public String toString() {
            return String.format("{proxyAppPackageName: %s, protocolStack: %d, otherProtocolStackName: %s, requestor: %d, requestorId: %s, responseType: %s, inEmergencyMode: %b, isCachedLocation: %b}", this.mProxyAppPackageName, Byte.valueOf(this.mProtocolStack), this.mOtherProtocolStackName, Byte.valueOf(this.mRequestor), this.mRequestorId, getResponseTypeAsString(), Boolean.valueOf(this.mInEmergencyMode), Boolean.valueOf(this.mIsCachedLocation));
        }

        public final String getResponseTypeAsString() {
            byte b = this.mResponseType;
            return b != 0 ? b != 1 ? b != 2 ? "<Unknown>" : "ACCEPTED_LOCATION_PROVIDED" : "ACCEPTED_NO_LOCATION_PROVIDED" : "REJECTED";
        }

        public final boolean isRequestAccepted() {
            return this.mResponseType != 0;
        }

        public final boolean isLocationProvided() {
            return this.mResponseType == 2;
        }

        public final boolean isRequestAttributedToProxyApp() {
            return !TextUtils.isEmpty(this.mProxyAppPackageName);
        }

        public final boolean isEmergencyRequestNotification() {
            return this.mInEmergencyMode && !isRequestAttributedToProxyApp();
        }
    }

    /* renamed from: handlePermissionsChanged */
    public final void lambda$new$0(int i) {
        if (this.mProxyAppsState.isEmpty()) {
            return;
        }
        for (Map.Entry<String, ProxyAppState> entry : this.mProxyAppsState.entrySet()) {
            String key = entry.getKey();
            ApplicationInfo proxyAppInfo = getProxyAppInfo(key);
            if (proxyAppInfo != null && proxyAppInfo.uid == i) {
                boolean shouldEnableLocationPermissionInGnssHal = shouldEnableLocationPermissionInGnssHal(key);
                ProxyAppState value = entry.getValue();
                if (shouldEnableLocationPermissionInGnssHal != value.mHasLocationPermission) {
                    Log.i("GnssVisibilityControl", "Proxy app " + key + " location permission changed. IsLocationPermissionEnabled: " + shouldEnableLocationPermissionInGnssHal);
                    value.mHasLocationPermission = shouldEnableLocationPermissionInGnssHal;
                    updateNfwLocationAccessProxyAppsInGnssHal();
                    return;
                }
                return;
            }
        }
    }

    public final ApplicationInfo getProxyAppInfo(String str) {
        try {
            return this.mPackageManager.getApplicationInfo(str, 0);
        } catch (PackageManager.NameNotFoundException unused) {
            if (DEBUG) {
                Log.d("GnssVisibilityControl", "Proxy app " + str + " is not found.");
                return null;
            }
            return null;
        }
    }

    public final boolean shouldEnableLocationPermissionInGnssHal(String str) {
        return isProxyAppInstalled(str) && hasLocationPermission(str);
    }

    public final boolean isProxyAppInstalled(String str) {
        ApplicationInfo proxyAppInfo = getProxyAppInfo(str);
        return proxyAppInfo != null && proxyAppInfo.enabled;
    }

    public final boolean hasLocationPermission(String str) {
        return this.mPackageManager.checkPermission("android.permission.ACCESS_FINE_LOCATION", str) == 0;
    }

    public final void updateNfwLocationAccessProxyAppsInGnssHal() {
        if (this.mIsGpsEnabled) {
            setNfwLocationAccessProxyAppsInGnssHal(getLocationPermissionEnabledProxyApps());
        }
    }

    public final void setNfwLocationAccessProxyAppsInGnssHal(String[] strArr) {
        String arrays = Arrays.toString(strArr);
        Log.i("GnssVisibilityControl", "Updating non-framework location access proxy apps in the GNSS HAL to: " + arrays);
        if (native_enable_nfw_location_access(strArr)) {
            return;
        }
        Log.e("GnssVisibilityControl", "Failed to update non-framework location access proxy apps in the GNSS HAL to: " + arrays);
    }

    public final String[] getLocationPermissionEnabledProxyApps() {
        int i = 0;
        int i2 = 0;
        for (ProxyAppState proxyAppState : this.mProxyAppsState.values()) {
            if (proxyAppState.mHasLocationPermission) {
                i2++;
            }
        }
        String[] strArr = new String[i2];
        for (Map.Entry<String, ProxyAppState> entry : this.mProxyAppsState.entrySet()) {
            String key = entry.getKey();
            if (entry.getValue().mHasLocationPermission) {
                strArr[i] = key;
                i++;
            }
        }
        return strArr;
    }

    public boolean hasLocationPermissionEnabledProxyApps() {
        return getLocationPermissionEnabledProxyApps().length > 0;
    }

    public final void handleNfwNotification(NfwNotification nfwNotification) {
        boolean z = DEBUG;
        if (z) {
            Log.d("GnssVisibilityControl", "Non-framework location access notification: " + nfwNotification);
        }
        if (nfwNotification.isEmergencyRequestNotification()) {
            handleEmergencyNfwNotification(nfwNotification);
            return;
        }
        String str = nfwNotification.mProxyAppPackageName;
        ProxyAppState proxyAppState = this.mProxyAppsState.get(str);
        boolean isRequestAccepted = nfwNotification.isRequestAccepted();
        boolean isPermissionMismatched = isPermissionMismatched(proxyAppState, nfwNotification);
        logEvent(nfwNotification, isPermissionMismatched);
        if (!nfwNotification.isRequestAttributedToProxyApp()) {
            if (isRequestAccepted) {
                Log.e("GnssVisibilityControl", "ProxyAppPackageName field is not set. AppOps service not notified for notification: " + nfwNotification);
            } else if (z) {
                Log.d("GnssVisibilityControl", "Non-framework location request rejected. ProxyAppPackageName field is not set in the notification: " + nfwNotification + ". Number of configured proxy apps: " + this.mProxyAppsState.size());
            }
        } else if (proxyAppState == null) {
            Log.w("GnssVisibilityControl", "Could not find proxy app " + str + " in the value specified for config parameter: NFW_PROXY_APPS. AppOps service not notified for notification: " + nfwNotification);
        } else {
            ApplicationInfo proxyAppInfo = getProxyAppInfo(str);
            if (proxyAppInfo == null) {
                Log.e("GnssVisibilityControl", "Proxy app " + str + " is not found. AppOps service not notified for notification: " + nfwNotification);
                return;
            }
            if (nfwNotification.isLocationProvided()) {
                showLocationIcon(proxyAppState, nfwNotification, proxyAppInfo.uid, str);
                this.mAppOps.noteOpNoThrow(1, proxyAppInfo.uid, str);
            }
            if (isPermissionMismatched) {
                Log.w("GnssVisibilityControl", "Permission mismatch. Proxy app " + str + " location permission is set to " + proxyAppState.mHasLocationPermission + " and GNSS HAL enabled is set to " + this.mIsGpsEnabled + " but GNSS non-framework location access response type is " + nfwNotification.getResponseTypeAsString() + " for notification: " + nfwNotification);
            }
        }
    }

    public final boolean isPermissionMismatched(ProxyAppState proxyAppState, NfwNotification nfwNotification) {
        boolean isRequestAccepted = nfwNotification.isRequestAccepted();
        return (proxyAppState == null || !this.mIsGpsEnabled) ? isRequestAccepted : proxyAppState.mHasLocationPermission != isRequestAccepted;
    }

    public final void showLocationIcon(ProxyAppState proxyAppState, NfwNotification nfwNotification, int i, final String str) {
        boolean z = proxyAppState.mIsLocationIconOn;
        if (!z) {
            if (!updateLocationIcon(true, i, str)) {
                Log.w("GnssVisibilityControl", "Failed to show Location icon for notification: " + nfwNotification);
                return;
            }
            proxyAppState.mIsLocationIconOn = true;
        } else {
            this.mHandler.removeCallbacksAndMessages(proxyAppState);
        }
        if (DEBUG) {
            StringBuilder sb = new StringBuilder();
            sb.append("Location icon on. ");
            sb.append(z ? "Extending" : "Setting");
            sb.append(" icon display timer. Uid: ");
            sb.append(i);
            sb.append(", proxyAppPkgName: ");
            sb.append(str);
            Log.d("GnssVisibilityControl", sb.toString());
        }
        if (this.mHandler.postDelayed(new Runnable() { // from class: com.android.server.location.gnss.GnssVisibilityControl$$ExternalSyntheticLambda6
            @Override // java.lang.Runnable
            public final void run() {
                GnssVisibilityControl.this.lambda$showLocationIcon$5(str);
            }
        }, proxyAppState, 5000L)) {
            return;
        }
        clearLocationIcon(proxyAppState, i, str);
        Log.w("GnssVisibilityControl", "Failed to show location icon for the full duration for notification: " + nfwNotification);
    }

    /* renamed from: handleLocationIconTimeout */
    public final void lambda$showLocationIcon$5(String str) {
        ApplicationInfo proxyAppInfo = getProxyAppInfo(str);
        if (proxyAppInfo != null) {
            clearLocationIcon(this.mProxyAppsState.get(str), proxyAppInfo.uid, str);
        }
    }

    public final void clearLocationIcon(ProxyAppState proxyAppState, int i, String str) {
        updateLocationIcon(false, i, str);
        if (proxyAppState != null) {
            proxyAppState.mIsLocationIconOn = false;
        }
        if (DEBUG) {
            Log.d("GnssVisibilityControl", "Location icon off. Uid: " + i + ", proxyAppPkgName: " + str);
        }
    }

    public final boolean updateLocationIcon(boolean z, int i, String str) {
        if (z) {
            if (this.mAppOps.startOpNoThrow(41, i, str) != 0) {
                return false;
            }
            if (this.mAppOps.startOpNoThrow(42, i, str) != 0) {
                this.mAppOps.finishOp(41, i, str);
                return false;
            }
            return true;
        }
        this.mAppOps.finishOp(41, i, str);
        this.mAppOps.finishOp(42, i, str);
        return true;
    }

    public final void handleEmergencyNfwNotification(NfwNotification nfwNotification) {
        boolean z;
        boolean z2 = true;
        if (nfwNotification.isRequestAccepted()) {
            z = false;
        } else {
            Log.e("GnssVisibilityControl", "Emergency non-framework location request incorrectly rejected. Notification: " + nfwNotification);
            z = true;
        }
        if (this.mNiHandler.getInEmergency(128000L)) {
            z2 = z;
        } else {
            Log.w("GnssVisibilityControl", "Emergency state mismatch. Device currently not in user initiated emergency session. Notification: " + nfwNotification);
        }
        logEvent(nfwNotification, z2);
        if (nfwNotification.isLocationProvided()) {
            postEmergencyLocationUserNotification(nfwNotification);
        }
    }

    public final void postEmergencyLocationUserNotification(NfwNotification nfwNotification) {
        NotificationManager notificationManager = (NotificationManager) this.mContext.getSystemService("notification");
        if (notificationManager == null) {
            Log.w("GnssVisibilityControl", "Could not notify user of emergency location request. Notification: " + nfwNotification);
            return;
        }
        notificationManager.notifyAsUser(null, 0, createEmergencyLocationUserNotification(this.mContext), UserHandle.ALL);
    }

    public static Notification createEmergencyLocationUserNotification(Context context) {
        String string = context.getString(17040400);
        String string2 = context.getString(17040379);
        return new Notification.Builder(context, SystemNotificationChannels.NETWORK_STATUS).setSmallIcon(17303637).setWhen(0L).setOngoing(false).setAutoCancel(true).setColor(context.getColor(17170460)).setDefaults(0).setTicker(string + " (" + string2 + ")").setContentTitle(string).setContentText(string2).build();
    }

    public final void logEvent(NfwNotification nfwNotification, boolean z) {
        FrameworkStatsLog.write(131, nfwNotification.mProxyAppPackageName, nfwNotification.mProtocolStack, nfwNotification.mOtherProtocolStackName, nfwNotification.mRequestor, nfwNotification.mRequestorId, nfwNotification.mResponseType, nfwNotification.mInEmergencyMode, nfwNotification.mIsCachedLocation, z);
    }

    public final void runOnHandler(Runnable runnable) {
        this.mWakeLock.acquire(60000L);
        if (this.mHandler.post(runEventAndReleaseWakeLock(runnable))) {
            return;
        }
        this.mWakeLock.release();
    }

    public final Runnable runEventAndReleaseWakeLock(final Runnable runnable) {
        return new Runnable() { // from class: com.android.server.location.gnss.GnssVisibilityControl$$ExternalSyntheticLambda3
            @Override // java.lang.Runnable
            public final void run() {
                GnssVisibilityControl.this.lambda$runEventAndReleaseWakeLock$6(runnable);
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$runEventAndReleaseWakeLock$6(Runnable runnable) {
        try {
            runnable.run();
        } finally {
            this.mWakeLock.release();
        }
    }
}
