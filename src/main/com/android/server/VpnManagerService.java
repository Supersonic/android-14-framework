package com.android.server;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.INetd;
import android.net.IVpnManager;
import android.net.LinkProperties;
import android.net.Network;
import android.net.UnderlyingNetworkInfo;
import android.net.Uri;
import android.net.VpnProfileState;
import android.net.util.NetdService;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.INetworkManagementService;
import android.os.Looper;
import android.os.ParcelFileDescriptor;
import android.os.Process;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.os.UserManager;
import android.text.TextUtils;
import android.util.IndentingPrintWriter;
import android.util.Log;
import android.util.SparseArray;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.net.LegacyVpnInfo;
import com.android.internal.net.VpnConfig;
import com.android.internal.net.VpnProfile;
import com.android.internal.util.DumpUtils;
import com.android.net.module.util.PermissionUtils;
import com.android.server.connectivity.Vpn;
import com.android.server.connectivity.VpnProfileStore;
import com.android.server.net.LockdownVpnTracker;
import com.android.server.p011pm.UserManagerInternal;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.List;
/* loaded from: classes.dex */
public class VpnManagerService extends IVpnManager.Stub {
    public static final String TAG = VpnManagerService.class.getSimpleName();
    public final ConnectivityManager mCm;
    public final Context mContext;
    public final Dependencies mDeps;
    public final Handler mHandler;
    @VisibleForTesting
    protected final HandlerThread mHandlerThread;
    @GuardedBy({"mVpns"})
    public boolean mLockdownEnabled;
    @GuardedBy({"mVpns"})
    public LockdownVpnTracker mLockdownTracker;
    public final int mMainUserId;
    public final INetworkManagementService mNMS;
    public final INetd mNetd;
    public final Context mUserAllContext;
    public final UserManager mUserManager;
    public final VpnProfileStore mVpnProfileStore;
    @GuardedBy({"mVpns"})
    @VisibleForTesting
    protected final SparseArray<Vpn> mVpns = new SparseArray<>();
    public BroadcastReceiver mIntentReceiver = new BroadcastReceiver() { // from class: com.android.server.VpnManagerService.1
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            VpnManagerService.this.ensureRunningOnHandlerThread();
            String action = intent.getAction();
            int intExtra = intent.getIntExtra("android.intent.extra.user_handle", -10000);
            int intExtra2 = intent.getIntExtra("android.intent.extra.UID", -1);
            Uri data = intent.getData();
            String schemeSpecificPart = data != null ? data.getSchemeSpecificPart() : null;
            if ("com.android.server.action.LOCKDOWN_RESET".equals(action)) {
                VpnManagerService.this.onVpnLockdownReset();
            } else if (intExtra == -10000) {
            } else {
                if ("android.intent.action.USER_STARTED".equals(action)) {
                    VpnManagerService.this.onUserStarted(intExtra);
                } else if ("android.intent.action.USER_STOPPED".equals(action)) {
                    VpnManagerService.this.onUserStopped(intExtra);
                } else if ("android.intent.action.USER_ADDED".equals(action)) {
                    VpnManagerService.this.onUserAdded(intExtra);
                } else if ("android.intent.action.USER_REMOVED".equals(action)) {
                    VpnManagerService.this.onUserRemoved(intExtra);
                } else if ("android.intent.action.USER_UNLOCKED".equals(action)) {
                    VpnManagerService.this.onUserUnlocked(intExtra);
                } else if ("android.intent.action.PACKAGE_REPLACED".equals(action)) {
                    VpnManagerService.this.onPackageReplaced(schemeSpecificPart, intExtra2);
                } else if ("android.intent.action.PACKAGE_REMOVED".equals(action)) {
                    VpnManagerService.this.onPackageRemoved(schemeSpecificPart, intExtra2, intent.getBooleanExtra("android.intent.extra.REPLACING", false));
                } else if ("android.intent.action.PACKAGE_ADDED".equals(action)) {
                    VpnManagerService.this.onPackageAdded(schemeSpecificPart, intExtra2, intent.getBooleanExtra("android.intent.extra.REPLACING", false));
                } else {
                    String str = VpnManagerService.TAG;
                    Log.wtf(str, "received unexpected intent: " + action);
                }
            }
        }
    };
    public BroadcastReceiver mUserPresentReceiver = new BroadcastReceiver() { // from class: com.android.server.VpnManagerService.2
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            VpnManagerService.this.ensureRunningOnHandlerThread();
            VpnManagerService.this.updateLockdownVpn();
            context.unregisterReceiver(this);
        }
    };

    @VisibleForTesting
    /* loaded from: classes.dex */
    public static class Dependencies {
        public int getCallingUid() {
            return Binder.getCallingUid();
        }

        public HandlerThread makeHandlerThread() {
            return new HandlerThread("VpnManagerService");
        }

        public VpnProfileStore getVpnProfileStore() {
            return new VpnProfileStore();
        }

        public INetd getNetd() {
            return NetdService.getInstance();
        }

        public INetworkManagementService getINetworkManagementService() {
            return INetworkManagementService.Stub.asInterface(ServiceManager.getService("network_management"));
        }

        public Vpn createVpn(Looper looper, Context context, INetworkManagementService iNetworkManagementService, INetd iNetd, int i) {
            return new Vpn(looper, context, iNetworkManagementService, iNetd, i, new VpnProfileStore());
        }

        public LockdownVpnTracker createLockDownVpnTracker(Context context, Handler handler, Vpn vpn, VpnProfile vpnProfile) {
            return new LockdownVpnTracker(context, handler, vpn, vpnProfile);
        }

        public int getMainUserId() {
            return ((UserManagerInternal) LocalServices.getService(UserManagerInternal.class)).getMainUserId();
        }
    }

    public VpnManagerService(Context context, Dependencies dependencies) {
        this.mContext = context;
        this.mDeps = dependencies;
        HandlerThread makeHandlerThread = dependencies.makeHandlerThread();
        this.mHandlerThread = makeHandlerThread;
        makeHandlerThread.start();
        this.mHandler = makeHandlerThread.getThreadHandler();
        this.mVpnProfileStore = dependencies.getVpnProfileStore();
        this.mUserAllContext = context.createContextAsUser(UserHandle.ALL, 0);
        this.mCm = (ConnectivityManager) context.getSystemService(ConnectivityManager.class);
        this.mNMS = dependencies.getINetworkManagementService();
        this.mNetd = dependencies.getNetd();
        this.mUserManager = (UserManager) context.getSystemService(UserManager.class);
        this.mMainUserId = dependencies.getMainUserId();
        registerReceivers();
        log("VpnManagerService starting up");
    }

    public static VpnManagerService create(Context context) {
        return new VpnManagerService(context, new Dependencies());
    }

    public void systemReady() {
        updateLockdownVpn();
    }

    public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        if (DumpUtils.checkDumpPermission(this.mContext, TAG, printWriter)) {
            IndentingPrintWriter indentingPrintWriter = new com.android.internal.util.IndentingPrintWriter(printWriter, "  ");
            indentingPrintWriter.println("VPNs:");
            indentingPrintWriter.increaseIndent();
            synchronized (this.mVpns) {
                for (int i = 0; i < this.mVpns.size(); i++) {
                    indentingPrintWriter.println(this.mVpns.keyAt(i) + ": " + this.mVpns.valueAt(i).getPackage());
                    indentingPrintWriter.increaseIndent();
                    this.mVpns.valueAt(i).dump(indentingPrintWriter);
                    indentingPrintWriter.decreaseIndent();
                    indentingPrintWriter.println();
                }
                indentingPrintWriter.decreaseIndent();
            }
        }
    }

    public boolean prepareVpn(String str, String str2, int i) {
        enforceCrossUserPermission(i);
        synchronized (this.mVpns) {
            throwIfLockdownEnabled();
            Vpn vpn = this.mVpns.get(i);
            if (vpn != null) {
                return vpn.prepare(str, str2, 1);
            }
            return false;
        }
    }

    public void setVpnPackageAuthorization(String str, int i, int i2) {
        enforceCrossUserPermission(i);
        synchronized (this.mVpns) {
            Vpn vpn = this.mVpns.get(i);
            if (vpn != null) {
                vpn.setPackageAuthorization(str, i2);
            }
        }
    }

    public ParcelFileDescriptor establishVpn(VpnConfig vpnConfig) {
        ParcelFileDescriptor establish;
        int userId = UserHandle.getUserId(this.mDeps.getCallingUid());
        synchronized (this.mVpns) {
            throwIfLockdownEnabled();
            establish = this.mVpns.get(userId).establish(vpnConfig);
        }
        return establish;
    }

    public boolean addVpnAddress(String str, int i) {
        boolean addAddress;
        int userId = UserHandle.getUserId(this.mDeps.getCallingUid());
        synchronized (this.mVpns) {
            throwIfLockdownEnabled();
            addAddress = this.mVpns.get(userId).addAddress(str, i);
        }
        return addAddress;
    }

    public boolean removeVpnAddress(String str, int i) {
        boolean removeAddress;
        int userId = UserHandle.getUserId(this.mDeps.getCallingUid());
        synchronized (this.mVpns) {
            throwIfLockdownEnabled();
            removeAddress = this.mVpns.get(userId).removeAddress(str, i);
        }
        return removeAddress;
    }

    public boolean setUnderlyingNetworksForVpn(Network[] networkArr) {
        boolean underlyingNetworks;
        int userId = UserHandle.getUserId(this.mDeps.getCallingUid());
        synchronized (this.mVpns) {
            underlyingNetworks = this.mVpns.get(userId).setUnderlyingNetworks(networkArr);
        }
        return underlyingNetworks;
    }

    public boolean provisionVpnProfile(VpnProfile vpnProfile, String str) {
        boolean provisionVpnProfile;
        int userId = UserHandle.getUserId(this.mDeps.getCallingUid());
        synchronized (this.mVpns) {
            provisionVpnProfile = this.mVpns.get(userId).provisionVpnProfile(str, vpnProfile);
        }
        return provisionVpnProfile;
    }

    public void deleteVpnProfile(String str) {
        int userId = UserHandle.getUserId(this.mDeps.getCallingUid());
        synchronized (this.mVpns) {
            this.mVpns.get(userId).deleteVpnProfile(str);
        }
    }

    public final int getAppUid(String str, int i) {
        PackageManager packageManager = this.mContext.getPackageManager();
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            int packageUidAsUser = packageManager.getPackageUidAsUser(str, i);
            Binder.restoreCallingIdentity(clearCallingIdentity);
            return packageUidAsUser;
        } catch (PackageManager.NameNotFoundException unused) {
            Binder.restoreCallingIdentity(clearCallingIdentity);
            return -1;
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(clearCallingIdentity);
            throw th;
        }
    }

    public final void verifyCallingUidAndPackage(String str, int i) {
        if (getAppUid(str, UserHandle.getUserId(i)) == i) {
            return;
        }
        throw new SecurityException(str + " does not belong to uid " + i);
    }

    public String startVpnProfile(String str) {
        String startVpnProfile;
        int callingUid = Binder.getCallingUid();
        verifyCallingUidAndPackage(str, callingUid);
        int userId = UserHandle.getUserId(callingUid);
        synchronized (this.mVpns) {
            throwIfLockdownEnabled();
            startVpnProfile = this.mVpns.get(userId).startVpnProfile(str);
        }
        return startVpnProfile;
    }

    public void stopVpnProfile(String str) {
        int callingUid = Binder.getCallingUid();
        verifyCallingUidAndPackage(str, callingUid);
        int userId = UserHandle.getUserId(callingUid);
        synchronized (this.mVpns) {
            this.mVpns.get(userId).stopVpnProfile(str);
        }
    }

    public VpnProfileState getProvisionedVpnProfileState(String str) {
        VpnProfileState provisionedVpnProfileState;
        int callingUid = Binder.getCallingUid();
        verifyCallingUidAndPackage(str, callingUid);
        int userId = UserHandle.getUserId(callingUid);
        synchronized (this.mVpns) {
            provisionedVpnProfileState = this.mVpns.get(userId).getProvisionedVpnProfileState(str);
        }
        return provisionedVpnProfileState;
    }

    public void startLegacyVpn(VpnProfile vpnProfile) {
        if (Build.VERSION.DEVICE_INITIAL_SDK_INT >= 31 && VpnProfile.isLegacyType(vpnProfile.type)) {
            throw new UnsupportedOperationException("Legacy VPN is deprecated");
        }
        int userId = UserHandle.getUserId(this.mDeps.getCallingUid());
        ConnectivityManager connectivityManager = this.mCm;
        LinkProperties linkProperties = connectivityManager.getLinkProperties(connectivityManager.getActiveNetwork());
        if (linkProperties == null) {
            throw new IllegalStateException("Missing active network connection");
        }
        synchronized (this.mVpns) {
            throwIfLockdownEnabled();
            this.mVpns.get(userId).startLegacyVpn(vpnProfile, null, linkProperties);
        }
    }

    public LegacyVpnInfo getLegacyVpnInfo(int i) {
        LegacyVpnInfo legacyVpnInfo;
        enforceCrossUserPermission(i);
        synchronized (this.mVpns) {
            legacyVpnInfo = this.mVpns.get(i).getLegacyVpnInfo();
        }
        return legacyVpnInfo;
    }

    public VpnConfig getVpnConfig(int i) {
        enforceCrossUserPermission(i);
        synchronized (this.mVpns) {
            Vpn vpn = this.mVpns.get(i);
            if (vpn != null) {
                return vpn.getVpnConfig();
            }
            return null;
        }
    }

    public final boolean isLockdownVpnEnabled() {
        return this.mVpnProfileStore.get("LOCKDOWN_VPN") != null;
    }

    public boolean updateLockdownVpn() {
        if (this.mDeps.getCallingUid() != 1000 && this.mDeps.getCallingUid() != UserHandle.getUid(this.mMainUserId, 1000) && Binder.getCallingPid() != Process.myPid()) {
            logw("Lockdown VPN only available to system process or AID_SYSTEM on main user");
            return false;
        }
        synchronized (this.mVpns) {
            boolean isLockdownVpnEnabled = isLockdownVpnEnabled();
            this.mLockdownEnabled = isLockdownVpnEnabled;
            if (!isLockdownVpnEnabled) {
                setLockdownTracker(null);
                return true;
            }
            byte[] bArr = this.mVpnProfileStore.get("LOCKDOWN_VPN");
            if (bArr == null) {
                loge("Lockdown VPN configured but cannot be read from keystore");
                return false;
            }
            String str = new String(bArr);
            VpnProfileStore vpnProfileStore = this.mVpnProfileStore;
            VpnProfile decode = VpnProfile.decode(str, vpnProfileStore.get("VPN_" + str));
            if (decode == null) {
                loge("Lockdown VPN configured invalid profile " + str);
                setLockdownTracker(null);
                return true;
            }
            int userId = UserHandle.getUserId(this.mDeps.getCallingUid());
            Vpn vpn = this.mVpns.get(userId);
            if (vpn == null) {
                logw("VPN for user " + userId + " not ready yet. Skipping lockdown");
                return false;
            }
            setLockdownTracker(this.mDeps.createLockDownVpnTracker(this.mContext, this.mHandler, vpn, decode));
            return true;
        }
    }

    @GuardedBy({"mVpns"})
    public final void setLockdownTracker(LockdownVpnTracker lockdownVpnTracker) {
        LockdownVpnTracker lockdownVpnTracker2 = this.mLockdownTracker;
        this.mLockdownTracker = null;
        if (lockdownVpnTracker2 != null) {
            lockdownVpnTracker2.shutdown();
        }
        if (lockdownVpnTracker != null) {
            this.mLockdownTracker = lockdownVpnTracker;
            lockdownVpnTracker.init();
        }
    }

    @GuardedBy({"mVpns"})
    public final void throwIfLockdownEnabled() {
        if (this.mLockdownEnabled) {
            throw new IllegalStateException("Unavailable in lockdown mode");
        }
    }

    public final boolean startAlwaysOnVpn(int i) {
        synchronized (this.mVpns) {
            Vpn vpn = this.mVpns.get(i);
            if (vpn == null) {
                String str = TAG;
                Log.wtf(str, "User " + i + " has no Vpn configuration");
                return false;
            }
            return vpn.startAlwaysOnVpn();
        }
    }

    public boolean isAlwaysOnVpnPackageSupported(int i, String str) {
        enforceSettingsPermission();
        enforceCrossUserPermission(i);
        synchronized (this.mVpns) {
            Vpn vpn = this.mVpns.get(i);
            if (vpn == null) {
                logw("User " + i + " has no Vpn configuration");
                return false;
            }
            return vpn.isAlwaysOnPackageSupported(str);
        }
    }

    public boolean setAlwaysOnVpnPackage(int i, String str, boolean z, List<String> list) {
        enforceControlAlwaysOnVpnPermission();
        enforceCrossUserPermission(i);
        synchronized (this.mVpns) {
            if (isLockdownVpnEnabled()) {
                return false;
            }
            Vpn vpn = this.mVpns.get(i);
            if (vpn == null) {
                logw("User " + i + " has no Vpn configuration");
                return false;
            } else if (vpn.setAlwaysOnPackage(str, z, list)) {
                if (startAlwaysOnVpn(i)) {
                    return true;
                }
                vpn.setAlwaysOnPackage(null, false, null);
                return false;
            } else {
                return false;
            }
        }
    }

    public String getAlwaysOnVpnPackage(int i) {
        enforceControlAlwaysOnVpnPermission();
        enforceCrossUserPermission(i);
        synchronized (this.mVpns) {
            Vpn vpn = this.mVpns.get(i);
            if (vpn == null) {
                logw("User " + i + " has no Vpn configuration");
                return null;
            }
            return vpn.getAlwaysOnPackage();
        }
    }

    public boolean isVpnLockdownEnabled(int i) {
        enforceControlAlwaysOnVpnPermission();
        enforceCrossUserPermission(i);
        synchronized (this.mVpns) {
            Vpn vpn = this.mVpns.get(i);
            if (vpn == null) {
                logw("User " + i + " has no Vpn configuration");
                return false;
            }
            return vpn.getLockdown();
        }
    }

    public List<String> getVpnLockdownAllowlist(int i) {
        enforceControlAlwaysOnVpnPermission();
        enforceCrossUserPermission(i);
        synchronized (this.mVpns) {
            Vpn vpn = this.mVpns.get(i);
            if (vpn == null) {
                logw("User " + i + " has no Vpn configuration");
                return null;
            }
            return vpn.getLockdownAllowlist();
        }
    }

    @GuardedBy({"mVpns"})
    public final Vpn getVpnIfOwner() {
        return getVpnIfOwner(this.mDeps.getCallingUid());
    }

    @GuardedBy({"mVpns"})
    public final Vpn getVpnIfOwner(int i) {
        Vpn vpn = this.mVpns.get(UserHandle.getUserId(i));
        if (vpn == null) {
            return null;
        }
        UnderlyingNetworkInfo underlyingNetworkInfo = vpn.getUnderlyingNetworkInfo();
        if (underlyingNetworkInfo == null || underlyingNetworkInfo.getOwnerUid() != i) {
            return null;
        }
        return vpn;
    }

    public final void registerReceivers() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.USER_STARTED");
        intentFilter.addAction("android.intent.action.USER_STOPPED");
        intentFilter.addAction("android.intent.action.USER_ADDED");
        intentFilter.addAction("android.intent.action.USER_REMOVED");
        intentFilter.addAction("android.intent.action.USER_UNLOCKED");
        this.mUserAllContext.registerReceiver(this.mIntentReceiver, intentFilter, null, this.mHandler);
        this.mContext.createContextAsUser(UserHandle.of(this.mMainUserId), 0).registerReceiver(this.mUserPresentReceiver, new IntentFilter("android.intent.action.USER_PRESENT"), null, this.mHandler);
        IntentFilter intentFilter2 = new IntentFilter();
        intentFilter2.addAction("android.intent.action.PACKAGE_ADDED");
        intentFilter2.addAction("android.intent.action.PACKAGE_REPLACED");
        intentFilter2.addAction("android.intent.action.PACKAGE_REMOVED");
        intentFilter2.addDataScheme("package");
        this.mUserAllContext.registerReceiver(this.mIntentReceiver, intentFilter2, null, this.mHandler);
        IntentFilter intentFilter3 = new IntentFilter();
        intentFilter3.addAction("com.android.server.action.LOCKDOWN_RESET");
        this.mUserAllContext.registerReceiver(this.mIntentReceiver, intentFilter3, "android.permission.NETWORK_STACK", this.mHandler, 2);
    }

    public final void onUserStarted(int i) {
        if (this.mUserManager.getUserInfo(i) == null) {
            logw("Started user doesn't exist. UserId: " + i);
            return;
        }
        synchronized (this.mVpns) {
            if (this.mVpns.get(i) != null) {
                loge("Starting user already has a VPN");
                return;
            }
            this.mVpns.put(i, this.mDeps.createVpn(this.mHandler.getLooper(), this.mContext, this.mNMS, this.mNetd, i));
            if (i == this.mMainUserId && isLockdownVpnEnabled()) {
                updateLockdownVpn();
            }
        }
    }

    public final void onUserStopped(int i) {
        synchronized (this.mVpns) {
            Vpn vpn = this.mVpns.get(i);
            if (vpn == null) {
                loge("Stopped user has no VPN");
                return;
            }
            vpn.onUserStopped();
            this.mVpns.delete(i);
        }
    }

    public boolean isCallerCurrentAlwaysOnVpnApp() {
        boolean z;
        synchronized (this.mVpns) {
            Vpn vpnIfOwner = getVpnIfOwner();
            z = vpnIfOwner != null && vpnIfOwner.getAlwaysOn();
        }
        return z;
    }

    public boolean isCallerCurrentAlwaysOnVpnLockdownApp() {
        boolean z;
        synchronized (this.mVpns) {
            Vpn vpnIfOwner = getVpnIfOwner();
            z = vpnIfOwner != null && vpnIfOwner.getLockdown();
        }
        return z;
    }

    public final void onUserAdded(int i) {
        synchronized (this.mVpns) {
            int size = this.mVpns.size();
            for (int i2 = 0; i2 < size; i2++) {
                this.mVpns.valueAt(i2).onUserAdded(i);
            }
        }
    }

    public final void onUserRemoved(int i) {
        synchronized (this.mVpns) {
            int size = this.mVpns.size();
            for (int i2 = 0; i2 < size; i2++) {
                this.mVpns.valueAt(i2).onUserRemoved(i);
            }
        }
    }

    public final void onPackageReplaced(String str, int i) {
        if (TextUtils.isEmpty(str) || i < 0) {
            String str2 = TAG;
            Log.wtf(str2, "Invalid package in onPackageReplaced: " + str + " | " + i);
            return;
        }
        int userId = UserHandle.getUserId(i);
        synchronized (this.mVpns) {
            Vpn vpn = this.mVpns.get(userId);
            if (vpn == null) {
                return;
            }
            if (TextUtils.equals(vpn.getAlwaysOnPackage(), str)) {
                log("Restarting always-on VPN package " + str + " for user " + userId);
                vpn.startAlwaysOnVpn();
            }
        }
    }

    public final void onPackageRemoved(String str, int i, boolean z) {
        if (TextUtils.isEmpty(str) || i < 0) {
            String str2 = TAG;
            Log.wtf(str2, "Invalid package in onPackageRemoved: " + str + " | " + i);
            return;
        }
        int userId = UserHandle.getUserId(i);
        synchronized (this.mVpns) {
            Vpn vpn = this.mVpns.get(userId);
            if (vpn != null && !z) {
                if (TextUtils.equals(vpn.getAlwaysOnPackage(), str)) {
                    log("Removing always-on VPN package " + str + " for user " + userId);
                    vpn.setAlwaysOnPackage(null, false, null);
                }
                vpn.refreshPlatformVpnAppExclusionList();
            }
        }
    }

    public final void onPackageAdded(String str, int i, boolean z) {
        if (TextUtils.isEmpty(str) || i < 0) {
            String str2 = TAG;
            Log.wtf(str2, "Invalid package in onPackageAdded: " + str + " | " + i);
            return;
        }
        int userId = UserHandle.getUserId(i);
        synchronized (this.mVpns) {
            Vpn vpn = this.mVpns.get(userId);
            if (vpn != null && !z) {
                vpn.refreshPlatformVpnAppExclusionList();
            }
        }
    }

    public final void onUserUnlocked(int i) {
        synchronized (this.mVpns) {
            if (i == this.mMainUserId && isLockdownVpnEnabled()) {
                updateLockdownVpn();
            } else {
                startAlwaysOnVpn(i);
            }
        }
    }

    public final void onVpnLockdownReset() {
        synchronized (this.mVpns) {
            LockdownVpnTracker lockdownVpnTracker = this.mLockdownTracker;
            if (lockdownVpnTracker != null) {
                lockdownVpnTracker.reset();
            }
        }
    }

    public boolean setAppExclusionList(int i, String str, List<String> list) {
        boolean appExclusionList;
        enforceSettingsPermission();
        enforceCrossUserPermission(i);
        synchronized (this.mVpns) {
            Vpn vpn = this.mVpns.get(i);
            if (vpn != null) {
                appExclusionList = vpn.setAppExclusionList(str, list);
            } else {
                logw("User " + i + " has no Vpn configuration");
                throw new IllegalStateException("VPN for user " + i + " not ready yet. Skipping setting the list");
            }
        }
        return appExclusionList;
    }

    public List<String> getAppExclusionList(int i, String str) {
        enforceSettingsPermission();
        enforceCrossUserPermission(i);
        synchronized (this.mVpns) {
            Vpn vpn = this.mVpns.get(i);
            if (vpn != null) {
                return vpn.getAppExclusionList(str);
            }
            logw("User " + i + " has no Vpn configuration");
            return null;
        }
    }

    public void factoryReset() {
        enforceSettingsPermission();
        if (this.mUserManager.hasUserRestriction("no_network_reset") || this.mUserManager.hasUserRestriction("no_config_vpn")) {
            return;
        }
        int callingUserId = UserHandle.getCallingUserId();
        synchronized (this.mVpns) {
            String alwaysOnVpnPackage = getAlwaysOnVpnPackage(callingUserId);
            if (alwaysOnVpnPackage != null) {
                setAlwaysOnVpnPackage(callingUserId, null, false, null);
                setVpnPackageAuthorization(alwaysOnVpnPackage, callingUserId, -1);
            }
            if (this.mLockdownEnabled && callingUserId == this.mMainUserId) {
                long clearCallingIdentity = Binder.clearCallingIdentity();
                this.mVpnProfileStore.remove("LOCKDOWN_VPN");
                this.mLockdownEnabled = false;
                setLockdownTracker(null);
                Binder.restoreCallingIdentity(clearCallingIdentity);
            }
            VpnConfig vpnConfig = getVpnConfig(callingUserId);
            if (vpnConfig != null) {
                if (vpnConfig.legacy) {
                    prepareVpn("[Legacy VPN]", "[Legacy VPN]", callingUserId);
                } else {
                    setVpnPackageAuthorization(vpnConfig.user, callingUserId, -1);
                    prepareVpn(null, "[Legacy VPN]", callingUserId);
                }
            }
        }
    }

    public final void ensureRunningOnHandlerThread() {
        if (this.mHandler.getLooper().getThread() == Thread.currentThread()) {
            return;
        }
        throw new IllegalStateException("Not running on VpnManagerService thread: " + Thread.currentThread().getName());
    }

    public final void enforceControlAlwaysOnVpnPermission() {
        this.mContext.enforceCallingOrSelfPermission("android.permission.CONTROL_ALWAYS_ON_VPN", "VpnManagerService");
    }

    public final void enforceCrossUserPermission(int i) {
        if (i == UserHandle.getCallingUserId()) {
            return;
        }
        this.mContext.enforceCallingOrSelfPermission("android.permission.INTERACT_ACROSS_USERS_FULL", "VpnManagerService");
    }

    public final void enforceSettingsPermission() {
        PermissionUtils.enforceAnyPermissionOf(this.mContext, new String[]{"android.permission.NETWORK_SETTINGS", "android.permission.MAINLINE_NETWORK_STACK"});
    }

    public static void log(String str) {
        Log.d(TAG, str);
    }

    public static void logw(String str) {
        Log.w(TAG, str);
    }

    public static void loge(String str) {
        Log.e(TAG, str);
    }
}
