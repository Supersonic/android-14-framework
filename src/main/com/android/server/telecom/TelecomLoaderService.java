package com.android.server.telecom;

import android.app.role.OnRoleHoldersChangedListener;
import android.app.role.RoleManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.telecom.DefaultDialerManager;
import android.telecom.PhoneAccountHandle;
import android.telecom.TelecomManager;
import android.telephony.SubscriptionManager;
import android.util.IntArray;
import android.util.Slog;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.telecom.ITelecomLoader;
import com.android.internal.telecom.ITelecomService;
import com.android.internal.telephony.SmsApplication;
import com.android.server.DeviceIdleInternal;
import com.android.server.LocalServices;
import com.android.server.SystemService;
import com.android.server.p011pm.UserManagerService;
import com.android.server.p011pm.permission.LegacyPermissionManagerInternal;
import java.util.ArrayList;
/* loaded from: classes2.dex */
public class TelecomLoaderService extends SystemService {
    public static final ComponentName SERVICE_COMPONENT = new ComponentName("com.android.server.telecom", "com.android.server.telecom.components.TelecomService");
    public final Context mContext;
    @GuardedBy({"mLock"})
    public IntArray mDefaultSimCallManagerRequests;
    public final Object mLock;
    @GuardedBy({"mLock"})
    public TelecomServiceConnection mServiceConnection;
    public InternalServiceRepository mServiceRepo;

    @Override // com.android.server.SystemService
    public void onStart() {
    }

    /* loaded from: classes2.dex */
    public class TelecomServiceConnection implements ServiceConnection {
        public TelecomServiceConnection() {
        }

        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            PhoneAccountHandle simCallManager;
            try {
                ITelecomService createTelecomService = ITelecomLoader.Stub.asInterface(iBinder).createTelecomService(TelecomLoaderService.this.mServiceRepo);
                SmsApplication.getDefaultMmsApplication(TelecomLoaderService.this.mContext, false);
                ServiceManager.addService("telecom", createTelecomService.asBinder());
                synchronized (TelecomLoaderService.this.mLock) {
                    LegacyPermissionManagerInternal legacyPermissionManagerInternal = (LegacyPermissionManagerInternal) LocalServices.getService(LegacyPermissionManagerInternal.class);
                    if (TelecomLoaderService.this.mDefaultSimCallManagerRequests != null && (simCallManager = ((TelecomManager) TelecomLoaderService.this.mContext.getSystemService("telecom")).getSimCallManager()) != null) {
                        int size = TelecomLoaderService.this.mDefaultSimCallManagerRequests.size();
                        String packageName = simCallManager.getComponentName().getPackageName();
                        for (int i = size - 1; i >= 0; i--) {
                            int i2 = TelecomLoaderService.this.mDefaultSimCallManagerRequests.get(i);
                            TelecomLoaderService.this.mDefaultSimCallManagerRequests.remove(i);
                            legacyPermissionManagerInternal.grantDefaultPermissionsToDefaultSimCallManager(packageName, i2);
                        }
                    }
                }
            } catch (RemoteException unused) {
                Slog.w("TelecomLoaderService", "Failed linking to death.");
            }
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName componentName) {
            TelecomLoaderService.this.connectToTelecom();
        }
    }

    public TelecomLoaderService(Context context) {
        super(context);
        this.mLock = new Object();
        this.mContext = context;
        registerDefaultAppProviders();
    }

    @Override // com.android.server.SystemService
    public void onBootPhase(int i) {
        if (i == 550) {
            registerDefaultAppNotifier();
            registerCarrierConfigChangedReceiver();
            setupServiceRepository();
            connectToTelecom();
        }
    }

    public final void connectToTelecom() {
        synchronized (this.mLock) {
            TelecomServiceConnection telecomServiceConnection = this.mServiceConnection;
            if (telecomServiceConnection != null) {
                this.mContext.unbindService(telecomServiceConnection);
                this.mServiceConnection = null;
            }
            TelecomServiceConnection telecomServiceConnection2 = new TelecomServiceConnection();
            Intent intent = new Intent("com.android.ITelecomService");
            intent.setComponent(SERVICE_COMPONENT);
            if (this.mContext.bindServiceAsUser(intent, telecomServiceConnection2, 67108929, UserHandle.SYSTEM)) {
                this.mServiceConnection = telecomServiceConnection2;
            }
        }
    }

    public final void setupServiceRepository() {
        this.mServiceRepo = new InternalServiceRepository((DeviceIdleInternal) getLocalService(DeviceIdleInternal.class));
    }

    public final void registerDefaultAppProviders() {
        LegacyPermissionManagerInternal legacyPermissionManagerInternal = (LegacyPermissionManagerInternal) LocalServices.getService(LegacyPermissionManagerInternal.class);
        legacyPermissionManagerInternal.setSmsAppPackagesProvider(new LegacyPermissionManagerInternal.PackagesProvider() { // from class: com.android.server.telecom.TelecomLoaderService$$ExternalSyntheticLambda0
            @Override // com.android.server.p011pm.permission.LegacyPermissionManagerInternal.PackagesProvider
            public final String[] getPackages(int i) {
                String[] lambda$registerDefaultAppProviders$0;
                lambda$registerDefaultAppProviders$0 = TelecomLoaderService.this.lambda$registerDefaultAppProviders$0(i);
                return lambda$registerDefaultAppProviders$0;
            }
        });
        legacyPermissionManagerInternal.setDialerAppPackagesProvider(new LegacyPermissionManagerInternal.PackagesProvider() { // from class: com.android.server.telecom.TelecomLoaderService$$ExternalSyntheticLambda1
            @Override // com.android.server.p011pm.permission.LegacyPermissionManagerInternal.PackagesProvider
            public final String[] getPackages(int i) {
                String[] lambda$registerDefaultAppProviders$1;
                lambda$registerDefaultAppProviders$1 = TelecomLoaderService.this.lambda$registerDefaultAppProviders$1(i);
                return lambda$registerDefaultAppProviders$1;
            }
        });
        legacyPermissionManagerInternal.setSimCallManagerPackagesProvider(new LegacyPermissionManagerInternal.PackagesProvider() { // from class: com.android.server.telecom.TelecomLoaderService$$ExternalSyntheticLambda2
            @Override // com.android.server.p011pm.permission.LegacyPermissionManagerInternal.PackagesProvider
            public final String[] getPackages(int i) {
                String[] lambda$registerDefaultAppProviders$2;
                lambda$registerDefaultAppProviders$2 = TelecomLoaderService.this.lambda$registerDefaultAppProviders$2(i);
                return lambda$registerDefaultAppProviders$2;
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ String[] lambda$registerDefaultAppProviders$0(int i) {
        synchronized (this.mLock) {
            if (this.mServiceConnection == null) {
                return null;
            }
            ComponentName defaultSmsApplication = SmsApplication.getDefaultSmsApplication(this.mContext, true);
            if (defaultSmsApplication != null) {
                return new String[]{defaultSmsApplication.getPackageName()};
            }
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ String[] lambda$registerDefaultAppProviders$1(int i) {
        synchronized (this.mLock) {
            if (this.mServiceConnection == null) {
                return null;
            }
            String defaultDialerApplication = DefaultDialerManager.getDefaultDialerApplication(this.mContext);
            if (defaultDialerApplication != null) {
                return new String[]{defaultDialerApplication};
            }
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ String[] lambda$registerDefaultAppProviders$2(int i) {
        synchronized (this.mLock) {
            if (this.mServiceConnection == null) {
                if (this.mDefaultSimCallManagerRequests == null) {
                    this.mDefaultSimCallManagerRequests = new IntArray();
                }
                this.mDefaultSimCallManagerRequests.add(i);
                return null;
            }
            SubscriptionManager subscriptionManager = (SubscriptionManager) this.mContext.getSystemService(SubscriptionManager.class);
            if (subscriptionManager == null) {
                return null;
            }
            TelecomManager telecomManager = (TelecomManager) this.mContext.getSystemService("telecom");
            ArrayList arrayList = new ArrayList();
            for (int i2 : subscriptionManager.getActiveSubscriptionIdList()) {
                PhoneAccountHandle simCallManagerForSubscription = telecomManager.getSimCallManagerForSubscription(i2);
                if (simCallManagerForSubscription != null) {
                    arrayList.add(simCallManagerForSubscription.getComponentName().getPackageName());
                }
            }
            return (String[]) arrayList.toArray(new String[0]);
        }
    }

    public final void registerDefaultAppNotifier() {
        ((RoleManager) this.mContext.getSystemService(RoleManager.class)).addOnRoleHoldersChangedListenerAsUser(this.mContext.getMainExecutor(), new OnRoleHoldersChangedListener() { // from class: com.android.server.telecom.TelecomLoaderService$$ExternalSyntheticLambda3
            public final void onRoleHoldersChanged(String str, UserHandle userHandle) {
                TelecomLoaderService.this.lambda$registerDefaultAppNotifier$3(str, userHandle);
            }
        }, UserHandle.ALL);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$registerDefaultAppNotifier$3(String str, UserHandle userHandle) {
        updateSimCallManagerPermissions(userHandle.getIdentifier());
    }

    public final void registerCarrierConfigChangedReceiver() {
        this.mContext.registerReceiverAsUser(new BroadcastReceiver() { // from class: com.android.server.telecom.TelecomLoaderService.1
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals("android.telephony.action.CARRIER_CONFIG_CHANGED")) {
                    for (int i : UserManagerService.getInstance().getUserIds()) {
                        TelecomLoaderService.this.updateSimCallManagerPermissions(i);
                    }
                }
            }
        }, UserHandle.ALL, new IntentFilter("android.telephony.action.CARRIER_CONFIG_CHANGED"), null, null);
    }

    public final void updateSimCallManagerPermissions(int i) {
        LegacyPermissionManagerInternal legacyPermissionManagerInternal = (LegacyPermissionManagerInternal) LocalServices.getService(LegacyPermissionManagerInternal.class);
        PhoneAccountHandle simCallManager = ((TelecomManager) this.mContext.getSystemService("telecom")).getSimCallManager(i);
        if (simCallManager != null) {
            Slog.i("TelecomLoaderService", "updating sim call manager permissions for userId:" + i);
            legacyPermissionManagerInternal.grantDefaultPermissionsToDefaultSimCallManager(simCallManager.getComponentName().getPackageName(), i);
        }
    }
}
