package com.android.server.autofill;

import android.app.ActivityManagerInternal;
import android.app.ActivityThread;
import android.content.AutofillOptions;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.UserInfo;
import android.database.ContentObserver;
import android.graphics.Rect;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcelable;
import android.os.RemoteCallback;
import android.os.RemoteException;
import android.os.ResultReceiver;
import android.os.ShellCallback;
import android.os.SystemClock;
import android.os.UserHandle;
import android.provider.DeviceConfig;
import android.provider.Settings;
import android.service.autofill.FillEventHistory;
import android.service.autofill.UserData;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.LocalLog;
import android.util.Slog;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.util.TimeUtils;
import android.view.autofill.AutofillId;
import android.view.autofill.AutofillManager;
import android.view.autofill.AutofillManagerInternal;
import android.view.autofill.AutofillValue;
import android.view.autofill.IAutoFillManager;
import android.view.autofill.IAutoFillManagerClient;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.infra.GlobalWhitelistState;
import com.android.internal.infra.WhitelistHelper;
import com.android.internal.os.IResultReceiver;
import com.android.internal.util.DumpUtils;
import com.android.internal.util.Preconditions;
import com.android.internal.util.SyncResultReceiver;
import com.android.internal.util.jobs.XmlUtils;
import com.android.server.FgThread;
import com.android.server.LocalServices;
import com.android.server.SystemService;
import com.android.server.autofill.p007ui.AutoFillUI;
import com.android.server.infra.AbstractMasterSystemService;
import com.android.server.infra.FrameworkResourcesServiceNameResolver;
import com.android.server.infra.SecureSettingsServiceNameResolver;
import com.android.server.infra.ServiceNameResolver;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
/* loaded from: classes.dex */
public final class AutofillManagerService extends AbstractMasterSystemService<AutofillManagerService, AutofillManagerServiceImpl> {
    public static final Object sLock = AutofillManagerService.class;
    @GuardedBy({"sLock"})
    public static int sPartitionMaxCount = 10;
    @GuardedBy({"sLock"})
    public static int sVisibleDatasetsMaxCount;
    public final ActivityManagerInternal mAm;
    public final FrameworkResourcesServiceNameResolver mAugmentedAutofillResolver;
    public final AugmentedAutofillState mAugmentedAutofillState;
    @GuardedBy({"mLock"})
    public int mAugmentedServiceIdleUnbindTimeoutMs;
    @GuardedBy({"mLock"})
    public int mAugmentedServiceRequestTimeoutMs;
    public final AutofillCompatState mAutofillCompatState;
    public final BroadcastReceiver mBroadcastReceiver;
    public final DisabledInfoCache mDisabledInfoCache;
    public final FrameworkResourcesServiceNameResolver mFieldClassificationResolver;
    public final Object mFlagLock;
    public final LocalService mLocalService;
    @GuardedBy({"mFlagLock"})
    public boolean mPccClassificationEnabled;
    @GuardedBy({"mFlagLock"})
    public boolean mPccPreferProviderOverPcc;
    @GuardedBy({"mFlagLock"})
    public String mPccProviderHints;
    @GuardedBy({"mFlagLock"})
    public boolean mPccUseFallbackDetection;
    public final LocalLog mRequestsHistory;
    @GuardedBy({"mLock"})
    public int mSupportedSmartSuggestionModes;
    public final AutoFillUI mUi;
    public final LocalLog mUiLatencyHistory;
    public final LocalLog mWtfHistory;

    @Override // com.android.server.infra.AbstractMasterSystemService
    public String getServiceSettingsProperty() {
        return "autofill_service";
    }

    /* renamed from: com.android.server.autofill.AutofillManagerService$1 */
    /* loaded from: classes.dex */
    public class C05261 extends BroadcastReceiver {
        public C05261() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if ("android.intent.action.CLOSE_SYSTEM_DIALOGS".equals(intent.getAction())) {
                if (Helper.sDebug) {
                    Slog.d("AutofillManagerService", "Close system dialogs");
                }
                synchronized (AutofillManagerService.this.mLock) {
                    AutofillManagerService.this.visitServicesLocked(new AbstractMasterSystemService.Visitor() { // from class: com.android.server.autofill.AutofillManagerService$1$$ExternalSyntheticLambda0
                        @Override // com.android.server.infra.AbstractMasterSystemService.Visitor
                        public final void visit(Object obj) {
                            ((AutofillManagerServiceImpl) obj).forceRemoveFinishedSessionsLocked();
                        }
                    });
                }
                AutofillManagerService.this.mUi.hideAll(null);
            }
        }
    }

    public AutofillManagerService(Context context) {
        super(context, new SecureSettingsServiceNameResolver(context, "autofill_service"), "no_autofill", 4);
        this.mRequestsHistory = new LocalLog(20);
        this.mUiLatencyHistory = new LocalLog(20);
        this.mWtfHistory = new LocalLog(50);
        this.mAutofillCompatState = new AutofillCompatState();
        this.mDisabledInfoCache = new DisabledInfoCache();
        this.mLocalService = new LocalService();
        C05261 c05261 = new C05261();
        this.mBroadcastReceiver = c05261;
        this.mAugmentedAutofillState = new AugmentedAutofillState();
        this.mFlagLock = new Object();
        this.mUi = new AutoFillUI(ActivityThread.currentActivityThread().getSystemUiContext());
        this.mAm = (ActivityManagerInternal) LocalServices.getService(ActivityManagerInternal.class);
        DeviceConfig.addOnPropertiesChangedListener("autofill", ActivityThread.currentApplication().getMainExecutor(), new DeviceConfig.OnPropertiesChangedListener() { // from class: com.android.server.autofill.AutofillManagerService$$ExternalSyntheticLambda0
            public final void onPropertiesChanged(DeviceConfig.Properties properties) {
                AutofillManagerService.this.lambda$new$0(properties);
            }
        });
        setLogLevelFromSettings();
        setMaxPartitionsFromSettings();
        setMaxVisibleDatasetsFromSettings();
        setDeviceConfigProperties();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.CLOSE_SYSTEM_DIALOGS");
        context.registerReceiver(c05261, intentFilter, null, FgThread.getHandler(), 2);
        FrameworkResourcesServiceNameResolver frameworkResourcesServiceNameResolver = new FrameworkResourcesServiceNameResolver(getContext(), 17039874);
        this.mAugmentedAutofillResolver = frameworkResourcesServiceNameResolver;
        frameworkResourcesServiceNameResolver.setOnTemporaryServiceNameChangedCallback(new ServiceNameResolver.NameResolverListener() { // from class: com.android.server.autofill.AutofillManagerService$$ExternalSyntheticLambda1
            @Override // com.android.server.infra.ServiceNameResolver.NameResolverListener
            public final void onNameResolved(int i, String str, boolean z) {
                AutofillManagerService.this.lambda$new$1(i, str, z);
            }
        });
        FrameworkResourcesServiceNameResolver frameworkResourcesServiceNameResolver2 = new FrameworkResourcesServiceNameResolver(getContext(), 17039884);
        this.mFieldClassificationResolver = frameworkResourcesServiceNameResolver2;
        if (Helper.sVerbose) {
            Slog.v("AutofillManagerService", "Resolving FieldClassificationService to serviceName: " + frameworkResourcesServiceNameResolver2.readServiceName(0));
        }
        frameworkResourcesServiceNameResolver2.setOnTemporaryServiceNameChangedCallback(new ServiceNameResolver.NameResolverListener() { // from class: com.android.server.autofill.AutofillManagerService$$ExternalSyntheticLambda2
            @Override // com.android.server.infra.ServiceNameResolver.NameResolverListener
            public final void onNameResolved(int i, String str, boolean z) {
                AutofillManagerService.this.lambda$new$2(i, str, z);
            }
        });
        if (this.mSupportedSmartSuggestionModes != 0) {
            List<UserInfo> supportedUsers = getSupportedUsers();
            for (int i = 0; i < supportedUsers.size(); i++) {
                int i2 = supportedUsers.get(i).id;
                getServiceForUserLocked(i2);
                this.mAugmentedAutofillState.setServiceInfo(i2, this.mAugmentedAutofillResolver.getServiceName(i2), this.mAugmentedAutofillResolver.isTemporary(i2));
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(DeviceConfig.Properties properties) {
        onDeviceConfigChange(properties.getKeyset());
    }

    @Override // com.android.server.infra.AbstractMasterSystemService
    public void registerForExtraSettingsChanges(ContentResolver contentResolver, ContentObserver contentObserver) {
        contentResolver.registerContentObserver(Settings.Global.getUriFor("autofill_logging_level"), false, contentObserver, -1);
        contentResolver.registerContentObserver(Settings.Global.getUriFor("autofill_max_partitions_size"), false, contentObserver, -1);
        contentResolver.registerContentObserver(Settings.Global.getUriFor("autofill_max_visible_datasets"), false, contentObserver, -1);
        contentResolver.registerContentObserver(Settings.Secure.getUriFor("selected_input_method_subtype"), false, contentObserver, -1);
    }

    @Override // com.android.server.infra.AbstractMasterSystemService
    public void onSettingsChanged(int i, String str) {
        str.hashCode();
        char c = 65535;
        switch (str.hashCode()) {
            case -1848997872:
                if (str.equals("autofill_max_visible_datasets")) {
                    c = 0;
                    break;
                }
                break;
            case -1299292969:
                if (str.equals("autofill_logging_level")) {
                    c = 1;
                    break;
                }
                break;
            case -1048937777:
                if (str.equals("autofill_max_partitions_size")) {
                    c = 2;
                    break;
                }
                break;
            case 1194058837:
                if (str.equals("selected_input_method_subtype")) {
                    c = 3;
                    break;
                }
                break;
        }
        switch (c) {
            case 0:
                setMaxVisibleDatasetsFromSettings();
                return;
            case 1:
                setLogLevelFromSettings();
                return;
            case 2:
                setMaxPartitionsFromSettings();
                return;
            case 3:
                handleInputMethodSwitch(i);
                return;
            default:
                Slog.w("AutofillManagerService", "Unexpected property (" + str + "); updating cache instead");
                synchronized (this.mLock) {
                    updateCachedServiceLocked(i);
                }
                return;
        }
    }

    public final void handleInputMethodSwitch(int i) {
        synchronized (this.mLock) {
            AutofillManagerServiceImpl peekServiceForUserLocked = peekServiceForUserLocked(i);
            if (peekServiceForUserLocked != null) {
                peekServiceForUserLocked.onSwitchInputMethod();
            }
        }
    }

    public final void onDeviceConfigChange(Set<String> set) {
        for (String str : set) {
            str.hashCode();
            char c = 65535;
            switch (str.hashCode()) {
                case -1681497033:
                    if (str.equals("pcc_classification_enabled")) {
                        c = 0;
                        break;
                    }
                    break;
                case -1644292860:
                    if (str.equals("prefer_provider_over_pcc")) {
                        c = 1;
                        break;
                    }
                    break;
                case -1546842390:
                    if (str.equals("augmented_service_idle_unbind_timeout")) {
                        c = 2;
                        break;
                    }
                    break;
                case -987506216:
                    if (str.equals("augmented_service_request_timeout")) {
                        c = 3;
                        break;
                    }
                    break;
                case 139432258:
                    if (str.equals("pcc_classification_hints")) {
                        c = 4;
                        break;
                    }
                    break;
                case 1168452547:
                    if (str.equals("compat_mode_allowed_packages")) {
                        c = 5;
                        break;
                    }
                    break;
                case 1169876393:
                    if (str.equals("pcc_use_fallback")) {
                        c = 6;
                        break;
                    }
                    break;
                case 1709136986:
                    if (str.equals("smart_suggestion_supported_modes")) {
                        c = 7;
                        break;
                    }
                    break;
            }
            switch (c) {
                case 0:
                case 1:
                case 2:
                case 3:
                case 4:
                case 6:
                case 7:
                    setDeviceConfigProperties();
                    break;
                case 5:
                    updateCachedServices();
                    break;
                default:
                    String str2 = this.mTag;
                    Slog.i(str2, "Ignoring change on " + str);
                    break;
            }
        }
    }

    /* renamed from: onAugmentedServiceNameChanged */
    public final void lambda$new$1(int i, String str, boolean z) {
        this.mAugmentedAutofillState.setServiceInfo(i, str, z);
        synchronized (this.mLock) {
            AutofillManagerServiceImpl peekServiceForUserLocked = peekServiceForUserLocked(i);
            if (peekServiceForUserLocked == null) {
                getServiceForUserLocked(i);
            } else {
                peekServiceForUserLocked.updateRemoteAugmentedAutofillService();
            }
        }
    }

    /* renamed from: onFieldClassificationServiceNameChanged */
    public final void lambda$new$2(int i, String str, boolean z) {
        synchronized (this.mLock) {
            AutofillManagerServiceImpl peekServiceForUserLocked = peekServiceForUserLocked(i);
            if (peekServiceForUserLocked == null) {
                getServiceForUserLocked(i);
            } else {
                peekServiceForUserLocked.updateRemoteFieldClassificationService();
            }
        }
    }

    /* JADX WARN: Can't rename method to resolve collision */
    @Override // com.android.server.infra.AbstractMasterSystemService
    public AutofillManagerServiceImpl newServiceLocked(int i, boolean z) {
        return new AutofillManagerServiceImpl(this, this.mLock, this.mUiLatencyHistory, this.mWtfHistory, i, this.mUi, this.mAutofillCompatState, z, this.mDisabledInfoCache);
    }

    @Override // com.android.server.infra.AbstractMasterSystemService
    public void onServiceRemoved(AutofillManagerServiceImpl autofillManagerServiceImpl, int i) {
        autofillManagerServiceImpl.destroyLocked();
        this.mDisabledInfoCache.remove(i);
        this.mAutofillCompatState.removeCompatibilityModeRequests(i);
    }

    @Override // com.android.server.infra.AbstractMasterSystemService
    public void onServiceEnabledLocked(AutofillManagerServiceImpl autofillManagerServiceImpl, int i) {
        addCompatibilityModeRequestsLocked(autofillManagerServiceImpl, i);
    }

    @Override // com.android.server.infra.AbstractMasterSystemService
    public void enforceCallingPermissionForManagement() {
        getContext().enforceCallingPermission("android.permission.MANAGE_AUTO_FILL", "AutofillManagerService");
    }

    @Override // com.android.server.SystemService
    public void onStart() {
        publishBinderService("autofill", new AutoFillManagerServiceStub());
        publishLocalService(AutofillManagerInternal.class, this.mLocalService);
    }

    @Override // com.android.server.SystemService
    public boolean isUserSupported(SystemService.TargetUser targetUser) {
        return targetUser.isFull() || targetUser.isProfile();
    }

    @Override // com.android.server.SystemService
    public void onUserSwitching(SystemService.TargetUser targetUser, SystemService.TargetUser targetUser2) {
        if (Helper.sDebug) {
            Slog.d("AutofillManagerService", "Hiding UI when user switched");
        }
        this.mUi.hideAll(null);
    }

    public int getSupportedSmartSuggestionModesLocked() {
        return this.mSupportedSmartSuggestionModes;
    }

    public void logRequestLocked(String str) {
        this.mRequestsHistory.log(str);
    }

    public boolean isInstantServiceAllowed() {
        return this.mAllowInstantService;
    }

    public void removeAllSessions(int i, IResultReceiver iResultReceiver) {
        Slog.i("AutofillManagerService", "removeAllSessions() for userId " + i);
        enforceCallingPermissionForManagement();
        synchronized (this.mLock) {
            if (i != -1) {
                AutofillManagerServiceImpl peekServiceForUserLocked = peekServiceForUserLocked(i);
                if (peekServiceForUserLocked != null) {
                    peekServiceForUserLocked.forceRemoveAllSessionsLocked();
                }
            } else {
                visitServicesLocked(new AbstractMasterSystemService.Visitor() { // from class: com.android.server.autofill.AutofillManagerService$$ExternalSyntheticLambda4
                    @Override // com.android.server.infra.AbstractMasterSystemService.Visitor
                    public final void visit(Object obj) {
                        ((AutofillManagerServiceImpl) obj).forceRemoveAllSessionsLocked();
                    }
                });
            }
        }
        try {
            iResultReceiver.send(0, new Bundle());
        } catch (RemoteException unused) {
        }
    }

    public void listSessions(int i, IResultReceiver iResultReceiver) {
        Slog.i("AutofillManagerService", "listSessions() for userId " + i);
        enforceCallingPermissionForManagement();
        Bundle bundle = new Bundle();
        final ArrayList<String> arrayList = new ArrayList<>();
        synchronized (this.mLock) {
            if (i != -1) {
                AutofillManagerServiceImpl peekServiceForUserLocked = peekServiceForUserLocked(i);
                if (peekServiceForUserLocked != null) {
                    peekServiceForUserLocked.listSessionsLocked(arrayList);
                }
            } else {
                visitServicesLocked(new AbstractMasterSystemService.Visitor() { // from class: com.android.server.autofill.AutofillManagerService$$ExternalSyntheticLambda5
                    @Override // com.android.server.infra.AbstractMasterSystemService.Visitor
                    public final void visit(Object obj) {
                        ((AutofillManagerServiceImpl) obj).listSessionsLocked(arrayList);
                    }
                });
            }
        }
        bundle.putStringArrayList("sessions", arrayList);
        try {
            iResultReceiver.send(0, bundle);
        } catch (RemoteException unused) {
        }
    }

    public void reset() {
        Slog.i("AutofillManagerService", "reset()");
        enforceCallingPermissionForManagement();
        synchronized (this.mLock) {
            visitServicesLocked(new AbstractMasterSystemService.Visitor() { // from class: com.android.server.autofill.AutofillManagerService$$ExternalSyntheticLambda3
                @Override // com.android.server.infra.AbstractMasterSystemService.Visitor
                public final void visit(Object obj) {
                    ((AutofillManagerServiceImpl) obj).destroyLocked();
                }
            });
            clearCacheLocked();
        }
    }

    public void setLogLevel(int i) {
        Slog.i("AutofillManagerService", "setLogLevel(): " + i);
        enforceCallingPermissionForManagement();
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            Settings.Global.putInt(getContext().getContentResolver(), "autofill_logging_level", i);
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:24:0x0068 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public final void setLogLevelFromSettings() {
        boolean z;
        int i = Settings.Global.getInt(getContext().getContentResolver(), "autofill_logging_level", AutofillManager.DEFAULT_LOGGING_LEVEL);
        boolean z2 = false;
        if (i != 0) {
            z = true;
            if (i == 4) {
                z2 = true;
            } else if (i == 2) {
                z = false;
                z2 = true;
            } else {
                Slog.w("AutofillManagerService", "setLogLevelFromSettings(): invalid level: " + i);
            }
            if (!z2 || Helper.sDebug) {
                Slog.d("AutofillManagerService", "setLogLevelFromSettings(): level=" + i + ", debug=" + z2 + ", verbose=" + z);
            }
            synchronized (this.mLock) {
                setLoggingLevelsLocked(z2, z);
            }
            return;
        }
        z = false;
        if (!z2) {
        }
        Slog.d("AutofillManagerService", "setLogLevelFromSettings(): level=" + i + ", debug=" + z2 + ", verbose=" + z);
        synchronized (this.mLock) {
        }
    }

    public int getLogLevel() {
        enforceCallingPermissionForManagement();
        synchronized (this.mLock) {
            if (Helper.sVerbose) {
                return 4;
            }
            return Helper.sDebug ? 2 : 0;
        }
    }

    public int getMaxPartitions() {
        int i;
        enforceCallingPermissionForManagement();
        synchronized (this.mLock) {
            i = sPartitionMaxCount;
        }
        return i;
    }

    public void setMaxPartitions(int i) {
        Slog.i("AutofillManagerService", "setMaxPartitions(): " + i);
        enforceCallingPermissionForManagement();
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            Settings.Global.putInt(getContext().getContentResolver(), "autofill_max_partitions_size", i);
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public final void setMaxPartitionsFromSettings() {
        int i = Settings.Global.getInt(getContext().getContentResolver(), "autofill_max_partitions_size", 10);
        if (Helper.sDebug) {
            Slog.d("AutofillManagerService", "setMaxPartitionsFromSettings(): " + i);
        }
        synchronized (sLock) {
            sPartitionMaxCount = i;
        }
    }

    public int getMaxVisibleDatasets() {
        int i;
        enforceCallingPermissionForManagement();
        synchronized (sLock) {
            i = sVisibleDatasetsMaxCount;
        }
        return i;
    }

    public void setMaxVisibleDatasets(int i) {
        Slog.i("AutofillManagerService", "setMaxVisibleDatasets(): " + i);
        enforceCallingPermissionForManagement();
        long clearCallingIdentity = Binder.clearCallingIdentity();
        try {
            Settings.Global.putInt(getContext().getContentResolver(), "autofill_max_visible_datasets", i);
        } finally {
            Binder.restoreCallingIdentity(clearCallingIdentity);
        }
    }

    public final void setMaxVisibleDatasetsFromSettings() {
        int i = Settings.Global.getInt(getContext().getContentResolver(), "autofill_max_visible_datasets", 0);
        if (Helper.sDebug) {
            Slog.d("AutofillManagerService", "setMaxVisibleDatasetsFromSettings(): " + i);
        }
        synchronized (sLock) {
            sVisibleDatasetsMaxCount = i;
        }
    }

    public final void setDeviceConfigProperties() {
        synchronized (this.mLock) {
            this.mAugmentedServiceIdleUnbindTimeoutMs = DeviceConfig.getInt("autofill", "augmented_service_idle_unbind_timeout", 0);
            this.mAugmentedServiceRequestTimeoutMs = DeviceConfig.getInt("autofill", "augmented_service_request_timeout", 5000);
            this.mSupportedSmartSuggestionModes = DeviceConfig.getInt("autofill", "smart_suggestion_supported_modes", 1);
            if (this.verbose) {
                String str = this.mTag;
                Slog.v(str, "setDeviceConfigProperties() for AugmentedAutofill: augmentedIdleTimeout=" + this.mAugmentedServiceIdleUnbindTimeoutMs + ", augmentedRequestTimeout=" + this.mAugmentedServiceRequestTimeoutMs + ", smartSuggestionMode=" + AutofillManager.getSmartSuggestionModeToString(this.mSupportedSmartSuggestionModes));
            }
        }
        synchronized (this.mFlagLock) {
            this.mPccClassificationEnabled = DeviceConfig.getBoolean("autofill", "pcc_classification_enabled", false);
            this.mPccPreferProviderOverPcc = DeviceConfig.getBoolean("autofill", "prefer_provider_over_pcc", true);
            this.mPccUseFallbackDetection = DeviceConfig.getBoolean("autofill", "pcc_use_fallback", true);
            this.mPccProviderHints = DeviceConfig.getString("autofill", "pcc_classification_hints", "");
            if (this.verbose) {
                String str2 = this.mTag;
                Slog.v(str2, "setDeviceConfigProperties() for PCC: mPccClassificationEnabled=" + this.mPccClassificationEnabled + ", mPccPreferProviderOverPcc=" + this.mPccPreferProviderOverPcc + ", mPccUseFallbackDetection=" + this.mPccUseFallbackDetection + ", mPccProviderHints=" + this.mPccProviderHints);
            }
        }
    }

    public final void updateCachedServices() {
        for (UserInfo userInfo : getSupportedUsers()) {
            synchronized (this.mLock) {
                updateCachedServiceLocked(userInfo.id);
            }
        }
    }

    public void calculateScore(String str, String str2, String str3, RemoteCallback remoteCallback) {
        enforceCallingPermissionForManagement();
        new FieldClassificationStrategy(getContext(), -2).calculateScores(remoteCallback, Arrays.asList(AutofillValue.forText(str2)), new String[]{str3}, new String[]{null}, str, null, null, null);
    }

    public Boolean getFullScreenMode() {
        enforceCallingPermissionForManagement();
        return Helper.sFullScreenMode;
    }

    public void setFullScreenMode(Boolean bool) {
        enforceCallingPermissionForManagement();
        Helper.sFullScreenMode = bool;
    }

    public void setTemporaryAugmentedAutofillService(int i, String str, int i2) {
        String str2 = this.mTag;
        Slog.i(str2, "setTemporaryAugmentedAutofillService(" + i + ") to " + str + " for " + i2 + "ms");
        enforceCallingPermissionForManagement();
        Objects.requireNonNull(str);
        if (i2 > 120000) {
            throw new IllegalArgumentException("Max duration is 120000 (called with " + i2 + ")");
        }
        this.mAugmentedAutofillResolver.setTemporaryService(i, str, i2);
    }

    public void resetTemporaryAugmentedAutofillService(int i) {
        enforceCallingPermissionForManagement();
        this.mAugmentedAutofillResolver.resetTemporaryService(i);
    }

    public boolean isDefaultAugmentedServiceEnabled(int i) {
        enforceCallingPermissionForManagement();
        return this.mAugmentedAutofillResolver.isDefaultServiceEnabled(i);
    }

    public boolean setDefaultAugmentedServiceEnabled(int i, boolean z) {
        String str = this.mTag;
        Slog.i(str, "setDefaultAugmentedServiceEnabled() for userId " + i + ": " + z);
        enforceCallingPermissionForManagement();
        synchronized (this.mLock) {
            AutofillManagerServiceImpl serviceForUserLocked = getServiceForUserLocked(i);
            if (serviceForUserLocked != null) {
                if (this.mAugmentedAutofillResolver.setDefaultServiceEnabled(i, z)) {
                    serviceForUserLocked.updateRemoteAugmentedAutofillService();
                    return true;
                } else if (this.debug) {
                    Slog.d("AutofillManagerService", "setDefaultAugmentedServiceEnabled(): already " + z);
                }
            }
            return false;
        }
    }

    public boolean setTemporaryDetectionService(int i, String str, int i2) {
        String str2 = this.mTag;
        Slog.i(str2, "setTemporaryDetectionService(" + i + ") to " + str + " for " + i2 + "ms");
        enforceCallingPermissionForManagement();
        Objects.requireNonNull(str);
        this.mFieldClassificationResolver.setTemporaryService(i, str, i2);
        return false;
    }

    public void resetTemporaryDetectionService(int i) {
        enforceCallingPermissionForManagement();
        this.mFieldClassificationResolver.resetTemporaryService(i);
    }

    public boolean requestSavedPasswordCount(int i, IResultReceiver iResultReceiver) {
        enforceCallingPermissionForManagement();
        synchronized (this.mLock) {
            AutofillManagerServiceImpl peekServiceForUserLocked = peekServiceForUserLocked(i);
            if (peekServiceForUserLocked != null) {
                peekServiceForUserLocked.requestSavedPasswordCount(iResultReceiver);
                return true;
            }
            if (Helper.sVerbose) {
                Slog.v("AutofillManagerService", "requestSavedPasswordCount(): no service for " + i);
            }
            return false;
        }
    }

    public final void setLoggingLevelsLocked(boolean z, boolean z2) {
        Helper.sDebug = z;
        android.view.autofill.Helper.sDebug = z;
        this.debug = z;
        Helper.sVerbose = z2;
        android.view.autofill.Helper.sVerbose = z2;
        this.verbose = z2;
    }

    public final void addCompatibilityModeRequestsLocked(AutofillManagerServiceImpl autofillManagerServiceImpl, int i) {
        this.mAutofillCompatState.reset(i);
        ArrayMap<String, Long> compatibilityPackagesLocked = autofillManagerServiceImpl.getCompatibilityPackagesLocked();
        if (compatibilityPackagesLocked == null || compatibilityPackagesLocked.isEmpty()) {
            return;
        }
        Map<String, String[]> allowedCompatModePackages = getAllowedCompatModePackages();
        int size = compatibilityPackagesLocked.size();
        for (int i2 = 0; i2 < size; i2++) {
            String keyAt = compatibilityPackagesLocked.keyAt(i2);
            if (allowedCompatModePackages == null || !allowedCompatModePackages.containsKey(keyAt)) {
                Slog.w("AutofillManagerService", "Ignoring not allowed compat package " + keyAt);
            } else {
                Long valueAt = compatibilityPackagesLocked.valueAt(i2);
                if (valueAt != null) {
                    this.mAutofillCompatState.addCompatibilityModeRequest(keyAt, valueAt.longValue(), allowedCompatModePackages.get(keyAt), i);
                }
            }
        }
    }

    public final String getAllowedCompatModePackagesFromDeviceConfig() {
        String string = DeviceConfig.getString("autofill", "compat_mode_allowed_packages", (String) null);
        return !TextUtils.isEmpty(string) ? string : getAllowedCompatModePackagesFromSettings();
    }

    public final String getAllowedCompatModePackagesFromSettings() {
        return Settings.Global.getString(getContext().getContentResolver(), "autofill_compat_mode_allowed_packages");
    }

    public final Map<String, String[]> getAllowedCompatModePackages() {
        return getAllowedCompatModePackages(getAllowedCompatModePackagesFromDeviceConfig());
    }

    public final void send(IResultReceiver iResultReceiver, int i) {
        try {
            iResultReceiver.send(i, (Bundle) null);
        } catch (RemoteException e) {
            Slog.w("AutofillManagerService", "Error async reporting result to client: " + e);
        }
    }

    public final void send(IResultReceiver iResultReceiver, Bundle bundle) {
        try {
            iResultReceiver.send(0, bundle);
        } catch (RemoteException e) {
            Slog.w("AutofillManagerService", "Error async reporting result to client: " + e);
        }
    }

    public final void send(IResultReceiver iResultReceiver, String str) {
        send(iResultReceiver, SyncResultReceiver.bundleFor(str));
    }

    public final void send(IResultReceiver iResultReceiver, String[] strArr) {
        send(iResultReceiver, SyncResultReceiver.bundleFor(strArr));
    }

    public final void send(IResultReceiver iResultReceiver, Parcelable parcelable) {
        send(iResultReceiver, SyncResultReceiver.bundleFor(parcelable));
    }

    public final void send(IResultReceiver iResultReceiver, boolean z) {
        send(iResultReceiver, z ? 1 : 0);
    }

    public final void send(IResultReceiver iResultReceiver, int i, int i2) {
        try {
            iResultReceiver.send(i, SyncResultReceiver.bundleFor(i2));
        } catch (RemoteException e) {
            Slog.w("AutofillManagerService", "Error async reporting result to client: " + e);
        }
    }

    public boolean isPccClassificationEnabled() {
        boolean z;
        synchronized (this.mFlagLock) {
            z = this.mPccClassificationEnabled;
        }
        return z;
    }

    public boolean preferProviderOverPcc() {
        boolean z;
        synchronized (this.mFlagLock) {
            z = this.mPccPreferProviderOverPcc;
        }
        return z;
    }

    public boolean shouldUsePccFallback() {
        boolean z;
        synchronized (this.mFlagLock) {
            z = this.mPccUseFallbackDetection;
        }
        return z;
    }

    public String getPccProviderHints() {
        String str;
        synchronized (this.mFlagLock) {
            str = this.mPccProviderHints;
        }
        return str;
    }

    @VisibleForTesting
    public static Map<String, String[]> getAllowedCompatModePackages(String str) {
        ArrayList arrayList;
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        ArrayMap arrayMap = new ArrayMap();
        TextUtils.SimpleStringSplitter simpleStringSplitter = new TextUtils.SimpleStringSplitter(':');
        simpleStringSplitter.setString(str);
        while (simpleStringSplitter.hasNext()) {
            String next = simpleStringSplitter.next();
            int indexOf = next.indexOf(91);
            if (indexOf == -1) {
                arrayList = null;
            } else if (next.charAt(next.length() - 1) != ']') {
                Slog.w("AutofillManagerService", "Ignoring entry '" + next + "' on '" + str + "'because it does not end on ']'");
            } else {
                String substring = next.substring(0, indexOf);
                arrayList = new ArrayList();
                String substring2 = next.substring(indexOf + 1, next.length() - 1);
                if (Helper.sVerbose) {
                    Slog.v("AutofillManagerService", "pkg:" + substring + ": block:" + next + ": urls:" + arrayList + ": block:" + substring2 + XmlUtils.STRING_ARRAY_SEPARATOR);
                }
                TextUtils.SimpleStringSplitter simpleStringSplitter2 = new TextUtils.SimpleStringSplitter(',');
                simpleStringSplitter2.setString(substring2);
                while (simpleStringSplitter2.hasNext()) {
                    arrayList.add(simpleStringSplitter2.next());
                }
                next = substring;
            }
            if (arrayList == null) {
                arrayMap.put(next, null);
            } else {
                String[] strArr = new String[arrayList.size()];
                arrayList.toArray(strArr);
                arrayMap.put(next, strArr);
            }
        }
        return arrayMap;
    }

    public static int getPartitionMaxCount() {
        int i;
        synchronized (sLock) {
            i = sPartitionMaxCount;
        }
        return i;
    }

    public static int getVisibleDatasetsMaxCount() {
        int i;
        synchronized (sLock) {
            i = sVisibleDatasetsMaxCount;
        }
        return i;
    }

    /* loaded from: classes.dex */
    public final class LocalService extends AutofillManagerInternal {
        public LocalService() {
        }

        public void onBackKeyPressed() {
            if (Helper.sDebug) {
                Slog.d("AutofillManagerService", "onBackKeyPressed()");
            }
            AutofillManagerService.this.mUi.hideAll(null);
            synchronized (AutofillManagerService.this.mLock) {
                ((AutofillManagerServiceImpl) AutofillManagerService.this.getServiceForUserLocked(UserHandle.getCallingUserId())).onBackKeyPressed();
            }
        }

        public AutofillOptions getAutofillOptions(String str, long j, int i) {
            int i2;
            AutofillManagerService autofillManagerService = AutofillManagerService.this;
            if (autofillManagerService.verbose) {
                i2 = 6;
            } else {
                i2 = autofillManagerService.debug ? 2 : 0;
            }
            AutofillOptions autofillOptions = new AutofillOptions(i2, autofillManagerService.mAutofillCompatState.isCompatibilityModeRequested(str, j, i));
            AutofillManagerService.this.mAugmentedAutofillState.injectAugmentedAutofillInfo(autofillOptions, i, str);
            injectDisableAppInfo(autofillOptions, i, str);
            return autofillOptions;
        }

        public boolean isAugmentedAutofillServiceForUser(int i, int i2) {
            synchronized (AutofillManagerService.this.mLock) {
                AutofillManagerServiceImpl autofillManagerServiceImpl = (AutofillManagerServiceImpl) AutofillManagerService.this.peekServiceForUserLocked(i2);
                if (autofillManagerServiceImpl != null) {
                    return autofillManagerServiceImpl.isAugmentedAutofillServiceForUserLocked(i);
                }
                return false;
            }
        }

        public final void injectDisableAppInfo(AutofillOptions autofillOptions, int i, String str) {
            autofillOptions.appDisabledExpiration = AutofillManagerService.this.mDisabledInfoCache.getAppDisabledExpiration(i, str);
            autofillOptions.disabledActivities = AutofillManagerService.this.mDisabledInfoCache.getAppDisabledActivities(i, str);
        }
    }

    /* loaded from: classes.dex */
    public static final class PackageCompatState {
        public final long maxVersionCode;
        public final String[] urlBarResourceIds;

        public PackageCompatState(long j, String[] strArr) {
            this.maxVersionCode = j;
            this.urlBarResourceIds = strArr;
        }

        public String toString() {
            return "maxVersionCode=" + this.maxVersionCode + ", urlBarResourceIds=" + Arrays.toString(this.urlBarResourceIds);
        }
    }

    /* loaded from: classes.dex */
    public static final class DisabledInfoCache {
        public final Object mLock = new Object();
        @GuardedBy({"mLock"})
        public final SparseArray<AutofillDisabledInfo> mCache = new SparseArray<>();

        public void remove(int i) {
            synchronized (this.mLock) {
                this.mCache.remove(i);
            }
        }

        public void addDisabledAppLocked(int i, String str, long j) {
            Objects.requireNonNull(str);
            synchronized (this.mLock) {
                getOrCreateAutofillDisabledInfoByUserIdLocked(i).putDisableAppsLocked(str, j);
            }
        }

        public void addDisabledActivityLocked(int i, ComponentName componentName, long j) {
            Objects.requireNonNull(componentName);
            synchronized (this.mLock) {
                getOrCreateAutofillDisabledInfoByUserIdLocked(i).putDisableActivityLocked(componentName, j);
            }
        }

        public boolean isAutofillDisabledLocked(int i, ComponentName componentName) {
            boolean isAutofillDisabledLocked;
            Objects.requireNonNull(componentName);
            synchronized (this.mLock) {
                AutofillDisabledInfo autofillDisabledInfo = this.mCache.get(i);
                isAutofillDisabledLocked = autofillDisabledInfo != null ? autofillDisabledInfo.isAutofillDisabledLocked(componentName) : false;
            }
            return isAutofillDisabledLocked;
        }

        public long getAppDisabledExpiration(int i, String str) {
            Long valueOf;
            Objects.requireNonNull(str);
            synchronized (this.mLock) {
                AutofillDisabledInfo autofillDisabledInfo = this.mCache.get(i);
                valueOf = Long.valueOf(autofillDisabledInfo != null ? autofillDisabledInfo.getAppDisabledExpirationLocked(str) : 0L);
            }
            return valueOf.longValue();
        }

        public ArrayMap<String, Long> getAppDisabledActivities(int i, String str) {
            ArrayMap<String, Long> appDisabledActivitiesLocked;
            Objects.requireNonNull(str);
            synchronized (this.mLock) {
                AutofillDisabledInfo autofillDisabledInfo = this.mCache.get(i);
                appDisabledActivitiesLocked = autofillDisabledInfo != null ? autofillDisabledInfo.getAppDisabledActivitiesLocked(str) : null;
            }
            return appDisabledActivitiesLocked;
        }

        public void dump(int i, String str, PrintWriter printWriter) {
            synchronized (this.mLock) {
                AutofillDisabledInfo autofillDisabledInfo = this.mCache.get(i);
                if (autofillDisabledInfo != null) {
                    autofillDisabledInfo.dumpLocked(str, printWriter);
                }
            }
        }

        public final AutofillDisabledInfo getOrCreateAutofillDisabledInfoByUserIdLocked(int i) {
            AutofillDisabledInfo autofillDisabledInfo = this.mCache.get(i);
            if (autofillDisabledInfo == null) {
                AutofillDisabledInfo autofillDisabledInfo2 = new AutofillDisabledInfo();
                this.mCache.put(i, autofillDisabledInfo2);
                return autofillDisabledInfo2;
            }
            return autofillDisabledInfo;
        }
    }

    /* loaded from: classes.dex */
    public static final class AutofillDisabledInfo {
        public ArrayMap<ComponentName, Long> mDisabledActivities;
        public ArrayMap<String, Long> mDisabledApps;

        public AutofillDisabledInfo() {
        }

        public void putDisableAppsLocked(String str, long j) {
            if (this.mDisabledApps == null) {
                this.mDisabledApps = new ArrayMap<>(1);
            }
            this.mDisabledApps.put(str, Long.valueOf(j));
        }

        public void putDisableActivityLocked(ComponentName componentName, long j) {
            if (this.mDisabledActivities == null) {
                this.mDisabledActivities = new ArrayMap<>(1);
            }
            this.mDisabledActivities.put(componentName, Long.valueOf(j));
        }

        public long getAppDisabledExpirationLocked(String str) {
            Long l;
            ArrayMap<String, Long> arrayMap = this.mDisabledApps;
            if (arrayMap == null || (l = arrayMap.get(str)) == null) {
                return 0L;
            }
            return l.longValue();
        }

        public ArrayMap<String, Long> getAppDisabledActivitiesLocked(String str) {
            ArrayMap<ComponentName, Long> arrayMap = this.mDisabledActivities;
            ArrayMap<String, Long> arrayMap2 = null;
            if (arrayMap != null) {
                int size = arrayMap.size();
                for (int i = 0; i < size; i++) {
                    ComponentName keyAt = this.mDisabledActivities.keyAt(i);
                    if (str.equals(keyAt.getPackageName())) {
                        if (arrayMap2 == null) {
                            arrayMap2 = new ArrayMap<>();
                        }
                        arrayMap2.put(keyAt.flattenToShortString(), Long.valueOf(this.mDisabledActivities.valueAt(i).longValue()));
                    }
                }
            }
            return arrayMap2;
        }

        public boolean isAutofillDisabledLocked(ComponentName componentName) {
            long j;
            Long l;
            if (this.mDisabledActivities != null) {
                j = SystemClock.elapsedRealtime();
                Long l2 = this.mDisabledActivities.get(componentName);
                if (l2 != null) {
                    if (l2.longValue() >= j) {
                        return true;
                    }
                    if (Helper.sVerbose) {
                        Slog.v("AutofillManagerService", "Removing " + componentName.toShortString() + " from disabled list");
                    }
                    this.mDisabledActivities.remove(componentName);
                }
            } else {
                j = 0;
            }
            String packageName = componentName.getPackageName();
            ArrayMap<String, Long> arrayMap = this.mDisabledApps;
            if (arrayMap == null || (l = arrayMap.get(packageName)) == null) {
                return false;
            }
            if (j == 0) {
                j = SystemClock.elapsedRealtime();
            }
            if (l.longValue() >= j) {
                return true;
            }
            if (Helper.sVerbose) {
                Slog.v("AutofillManagerService", "Removing " + packageName + " from disabled list");
            }
            this.mDisabledApps.remove(packageName);
            return false;
        }

        public void dumpLocked(String str, PrintWriter printWriter) {
            printWriter.print(str);
            printWriter.print("Disabled apps: ");
            ArrayMap<String, Long> arrayMap = this.mDisabledApps;
            if (arrayMap == null) {
                printWriter.println("N/A");
            } else {
                int size = arrayMap.size();
                printWriter.println(size);
                StringBuilder sb = new StringBuilder();
                long elapsedRealtime = SystemClock.elapsedRealtime();
                for (int i = 0; i < size; i++) {
                    long longValue = this.mDisabledApps.valueAt(i).longValue();
                    sb.append(str);
                    sb.append(str);
                    sb.append(i);
                    sb.append(". ");
                    sb.append(this.mDisabledApps.keyAt(i));
                    sb.append(": ");
                    TimeUtils.formatDuration(longValue - elapsedRealtime, sb);
                    sb.append('\n');
                }
                printWriter.println(sb);
            }
            printWriter.print(str);
            printWriter.print("Disabled activities: ");
            ArrayMap<ComponentName, Long> arrayMap2 = this.mDisabledActivities;
            if (arrayMap2 == null) {
                printWriter.println("N/A");
                return;
            }
            int size2 = arrayMap2.size();
            printWriter.println(size2);
            StringBuilder sb2 = new StringBuilder();
            long elapsedRealtime2 = SystemClock.elapsedRealtime();
            for (int i2 = 0; i2 < size2; i2++) {
                long longValue2 = this.mDisabledActivities.valueAt(i2).longValue();
                sb2.append(str);
                sb2.append(str);
                sb2.append(i2);
                sb2.append(". ");
                sb2.append(this.mDisabledActivities.keyAt(i2));
                sb2.append(": ");
                TimeUtils.formatDuration(longValue2 - elapsedRealtime2, sb2);
                sb2.append('\n');
            }
            printWriter.println(sb2);
        }
    }

    /* loaded from: classes.dex */
    public static final class AutofillCompatState {
        public final Object mLock = new Object();
        @GuardedBy({"mLock"})
        public SparseArray<ArrayMap<String, PackageCompatState>> mUserSpecs;

        public boolean isCompatibilityModeRequested(String str, long j, int i) {
            synchronized (this.mLock) {
                SparseArray<ArrayMap<String, PackageCompatState>> sparseArray = this.mUserSpecs;
                if (sparseArray == null) {
                    return false;
                }
                ArrayMap<String, PackageCompatState> arrayMap = sparseArray.get(i);
                if (arrayMap == null) {
                    return false;
                }
                PackageCompatState packageCompatState = arrayMap.get(str);
                if (packageCompatState == null) {
                    return false;
                }
                return j <= packageCompatState.maxVersionCode;
            }
        }

        public String[] getUrlBarResourceIds(String str, int i) {
            synchronized (this.mLock) {
                SparseArray<ArrayMap<String, PackageCompatState>> sparseArray = this.mUserSpecs;
                if (sparseArray == null) {
                    return null;
                }
                ArrayMap<String, PackageCompatState> arrayMap = sparseArray.get(i);
                if (arrayMap == null) {
                    return null;
                }
                PackageCompatState packageCompatState = arrayMap.get(str);
                if (packageCompatState == null) {
                    return null;
                }
                return packageCompatState.urlBarResourceIds;
            }
        }

        public void addCompatibilityModeRequest(String str, long j, String[] strArr, int i) {
            synchronized (this.mLock) {
                if (this.mUserSpecs == null) {
                    this.mUserSpecs = new SparseArray<>();
                }
                ArrayMap<String, PackageCompatState> arrayMap = this.mUserSpecs.get(i);
                if (arrayMap == null) {
                    arrayMap = new ArrayMap<>();
                    this.mUserSpecs.put(i, arrayMap);
                }
                arrayMap.put(str, new PackageCompatState(j, strArr));
            }
        }

        public void removeCompatibilityModeRequests(int i) {
            synchronized (this.mLock) {
                SparseArray<ArrayMap<String, PackageCompatState>> sparseArray = this.mUserSpecs;
                if (sparseArray != null) {
                    sparseArray.remove(i);
                    if (this.mUserSpecs.size() <= 0) {
                        this.mUserSpecs = null;
                    }
                }
            }
        }

        public void reset(int i) {
            synchronized (this.mLock) {
                SparseArray<ArrayMap<String, PackageCompatState>> sparseArray = this.mUserSpecs;
                if (sparseArray != null) {
                    sparseArray.delete(i);
                    int size = this.mUserSpecs.size();
                    if (size == 0) {
                        if (Helper.sVerbose) {
                            Slog.v("AutofillManagerService", "reseting mUserSpecs");
                        }
                        this.mUserSpecs = null;
                    } else if (Helper.sVerbose) {
                        Slog.v("AutofillManagerService", "mUserSpecs down to " + size);
                    }
                }
            }
        }

        public final void dump(String str, PrintWriter printWriter) {
            synchronized (this.mLock) {
                if (this.mUserSpecs == null) {
                    printWriter.println("N/A");
                    return;
                }
                printWriter.println();
                String str2 = str + "  ";
                for (int i = 0; i < this.mUserSpecs.size(); i++) {
                    int keyAt = this.mUserSpecs.keyAt(i);
                    printWriter.print(str);
                    printWriter.print("User: ");
                    printWriter.println(keyAt);
                    ArrayMap<String, PackageCompatState> valueAt = this.mUserSpecs.valueAt(i);
                    for (int i2 = 0; i2 < valueAt.size(); i2++) {
                        printWriter.print(str2);
                        printWriter.print(valueAt.keyAt(i2));
                        printWriter.print(": ");
                        printWriter.println(valueAt.valueAt(i2));
                    }
                }
            }
        }
    }

    /* loaded from: classes.dex */
    public static final class AugmentedAutofillState extends GlobalWhitelistState {
        @GuardedBy({"mGlobalWhitelistStateLock"})
        public final SparseArray<String> mServicePackages = new SparseArray<>();
        @GuardedBy({"mGlobalWhitelistStateLock"})
        public final SparseBooleanArray mTemporaryServices = new SparseBooleanArray();

        public final void setServiceInfo(int i, String str, boolean z) {
            synchronized (((GlobalWhitelistState) this).mGlobalWhitelistStateLock) {
                if (z) {
                    this.mTemporaryServices.put(i, true);
                } else {
                    this.mTemporaryServices.delete(i);
                }
                if (str != null) {
                    ComponentName unflattenFromString = ComponentName.unflattenFromString(str);
                    if (unflattenFromString == null) {
                        Slog.w("AutofillManagerService", "setServiceInfo(): invalid name: " + str);
                        this.mServicePackages.remove(i);
                    } else {
                        this.mServicePackages.put(i, unflattenFromString.getPackageName());
                    }
                } else {
                    this.mServicePackages.remove(i);
                }
            }
        }

        public void injectAugmentedAutofillInfo(AutofillOptions autofillOptions, int i, String str) {
            synchronized (((GlobalWhitelistState) this).mGlobalWhitelistStateLock) {
                SparseArray sparseArray = ((GlobalWhitelistState) this).mWhitelisterHelpers;
                if (sparseArray == null) {
                    return;
                }
                WhitelistHelper whitelistHelper = (WhitelistHelper) sparseArray.get(i);
                if (whitelistHelper != null) {
                    autofillOptions.augmentedAutofillEnabled = whitelistHelper.isWhitelisted(str);
                    autofillOptions.whitelistedActivitiesForAugmentedAutofill = whitelistHelper.getWhitelistedComponents(str);
                }
            }
        }

        public boolean isWhitelisted(int i, ComponentName componentName) {
            synchronized (((GlobalWhitelistState) this).mGlobalWhitelistStateLock) {
                if (super.isWhitelisted(i, componentName)) {
                    if (Build.IS_USER && this.mTemporaryServices.get(i)) {
                        String packageName = componentName.getPackageName();
                        if (!packageName.equals(this.mServicePackages.get(i))) {
                            Slog.w("AutofillManagerService", "Ignoring package " + packageName + " for augmented autofill while using temporary service " + this.mServicePackages.get(i));
                            return false;
                        }
                    }
                    return true;
                }
                return false;
            }
        }

        public void dump(String str, PrintWriter printWriter) {
            super.dump(str, printWriter);
            synchronized (((GlobalWhitelistState) this).mGlobalWhitelistStateLock) {
                if (this.mServicePackages.size() > 0) {
                    printWriter.print(str);
                    printWriter.print("Service packages: ");
                    printWriter.println(this.mServicePackages);
                }
                if (this.mTemporaryServices.size() > 0) {
                    printWriter.print(str);
                    printWriter.print("Temp services: ");
                    printWriter.println(this.mTemporaryServices);
                }
            }
        }
    }

    /* loaded from: classes.dex */
    public final class AutoFillManagerServiceStub extends IAutoFillManager.Stub {
        public AutoFillManagerServiceStub() {
        }

        public void addClient(IAutoFillManagerClient iAutoFillManagerClient, ComponentName componentName, int i, IResultReceiver iResultReceiver) {
            int i2;
            synchronized (AutofillManagerService.this.mLock) {
                int addClientLocked = ((AutofillManagerServiceImpl) AutofillManagerService.this.getServiceForUserLocked(i)).addClientLocked(iAutoFillManagerClient, componentName);
                i2 = addClientLocked != 0 ? 0 | addClientLocked : 0;
                if (Helper.sDebug) {
                    i2 |= 2;
                }
                if (Helper.sVerbose) {
                    i2 |= 4;
                }
            }
            AutofillManagerService.this.send(iResultReceiver, i2);
        }

        public void removeClient(IAutoFillManagerClient iAutoFillManagerClient, int i) {
            synchronized (AutofillManagerService.this.mLock) {
                AutofillManagerServiceImpl autofillManagerServiceImpl = (AutofillManagerServiceImpl) AutofillManagerService.this.peekServiceForUserLocked(i);
                if (autofillManagerServiceImpl != null) {
                    autofillManagerServiceImpl.removeClientLocked(iAutoFillManagerClient);
                } else if (Helper.sVerbose) {
                    Slog.v("AutofillManagerService", "removeClient(): no service for " + i);
                }
            }
        }

        public void setAuthenticationResult(Bundle bundle, int i, int i2, int i3) {
            synchronized (AutofillManagerService.this.mLock) {
                ((AutofillManagerServiceImpl) AutofillManagerService.this.getServiceForUserLocked(i3)).setAuthenticationResultLocked(bundle, i, i2, IAutoFillManager.Stub.getCallingUid());
            }
        }

        public void setHasCallback(int i, int i2, boolean z) {
            synchronized (AutofillManagerService.this.mLock) {
                ((AutofillManagerServiceImpl) AutofillManagerService.this.getServiceForUserLocked(i2)).setHasCallback(i, IAutoFillManager.Stub.getCallingUid(), z);
            }
        }

        public void startSession(IBinder iBinder, IBinder iBinder2, AutofillId autofillId, Rect rect, AutofillValue autofillValue, int i, boolean z, int i2, ComponentName componentName, boolean z2, IResultReceiver iResultReceiver) {
            long startSessionLocked;
            Objects.requireNonNull(iBinder, "activityToken");
            Objects.requireNonNull(iBinder2, "clientCallback");
            Objects.requireNonNull(autofillId, "autofillId");
            Objects.requireNonNull(componentName, "clientActivity");
            String packageName = componentName.getPackageName();
            Objects.requireNonNull(packageName);
            Preconditions.checkArgument(i == UserHandle.getUserId(IAutoFillManager.Stub.getCallingUid()), "userId");
            try {
                AutofillManagerService.this.getContext().getPackageManager().getPackageInfoAsUser(packageName, 0, i);
                int taskIdForActivity = AutofillManagerService.this.mAm.getTaskIdForActivity(iBinder, false);
                synchronized (AutofillManagerService.this.mLock) {
                    startSessionLocked = ((AutofillManagerServiceImpl) AutofillManagerService.this.getServiceForUserLocked(i)).startSessionLocked(iBinder, taskIdForActivity, IAutoFillManager.Stub.getCallingUid(), iBinder2, autofillId, rect, autofillValue, z, componentName, z2, AutofillManagerService.this.mAllowInstantService, i2);
                }
                int i3 = (int) startSessionLocked;
                int i4 = (int) (startSessionLocked >> 32);
                if (i4 != 0) {
                    AutofillManagerService.this.send(iResultReceiver, i3, i4);
                } else {
                    AutofillManagerService.this.send(iResultReceiver, i3);
                }
            } catch (PackageManager.NameNotFoundException e) {
                throw new IllegalArgumentException(packageName + " is not a valid package", e);
            }
        }

        public void getFillEventHistory(IResultReceiver iResultReceiver) throws RemoteException {
            FillEventHistory fillEventHistory;
            int callingUserId = UserHandle.getCallingUserId();
            synchronized (AutofillManagerService.this.mLock) {
                AutofillManagerServiceImpl autofillManagerServiceImpl = (AutofillManagerServiceImpl) AutofillManagerService.this.peekServiceForUserLocked(callingUserId);
                if (autofillManagerServiceImpl != null) {
                    fillEventHistory = autofillManagerServiceImpl.getFillEventHistory(IAutoFillManager.Stub.getCallingUid());
                } else {
                    if (Helper.sVerbose) {
                        Slog.v("AutofillManagerService", "getFillEventHistory(): no service for " + callingUserId);
                    }
                    fillEventHistory = null;
                }
            }
            AutofillManagerService.this.send(iResultReceiver, fillEventHistory);
        }

        public void getUserData(IResultReceiver iResultReceiver) throws RemoteException {
            UserData userData;
            int callingUserId = UserHandle.getCallingUserId();
            synchronized (AutofillManagerService.this.mLock) {
                AutofillManagerServiceImpl autofillManagerServiceImpl = (AutofillManagerServiceImpl) AutofillManagerService.this.peekServiceForUserLocked(callingUserId);
                if (autofillManagerServiceImpl != null) {
                    userData = autofillManagerServiceImpl.getUserData(IAutoFillManager.Stub.getCallingUid());
                } else {
                    if (Helper.sVerbose) {
                        Slog.v("AutofillManagerService", "getUserData(): no service for " + callingUserId);
                    }
                    userData = null;
                }
            }
            AutofillManagerService.this.send(iResultReceiver, userData);
        }

        public void getUserDataId(IResultReceiver iResultReceiver) throws RemoteException {
            UserData userData;
            int callingUserId = UserHandle.getCallingUserId();
            synchronized (AutofillManagerService.this.mLock) {
                AutofillManagerServiceImpl autofillManagerServiceImpl = (AutofillManagerServiceImpl) AutofillManagerService.this.peekServiceForUserLocked(callingUserId);
                if (autofillManagerServiceImpl != null) {
                    userData = autofillManagerServiceImpl.getUserData(IAutoFillManager.Stub.getCallingUid());
                } else {
                    if (Helper.sVerbose) {
                        Slog.v("AutofillManagerService", "getUserDataId(): no service for " + callingUserId);
                    }
                    userData = null;
                }
            }
            AutofillManagerService.this.send(iResultReceiver, userData != null ? userData.getId() : null);
        }

        public void setUserData(UserData userData) throws RemoteException {
            int callingUserId = UserHandle.getCallingUserId();
            synchronized (AutofillManagerService.this.mLock) {
                AutofillManagerServiceImpl autofillManagerServiceImpl = (AutofillManagerServiceImpl) AutofillManagerService.this.peekServiceForUserLocked(callingUserId);
                if (autofillManagerServiceImpl != null) {
                    autofillManagerServiceImpl.setUserData(IAutoFillManager.Stub.getCallingUid(), userData);
                } else if (Helper.sVerbose) {
                    Slog.v("AutofillManagerService", "setUserData(): no service for " + callingUserId);
                }
            }
        }

        public void isFieldClassificationEnabled(IResultReceiver iResultReceiver) throws RemoteException {
            boolean z;
            int callingUserId = UserHandle.getCallingUserId();
            synchronized (AutofillManagerService.this.mLock) {
                AutofillManagerServiceImpl autofillManagerServiceImpl = (AutofillManagerServiceImpl) AutofillManagerService.this.peekServiceForUserLocked(callingUserId);
                if (autofillManagerServiceImpl != null) {
                    z = autofillManagerServiceImpl.isFieldClassificationEnabled(IAutoFillManager.Stub.getCallingUid());
                } else {
                    if (Helper.sVerbose) {
                        Slog.v("AutofillManagerService", "isFieldClassificationEnabled(): no service for " + callingUserId);
                    }
                    z = false;
                }
            }
            AutofillManagerService.this.send(iResultReceiver, z);
        }

        public void getDefaultFieldClassificationAlgorithm(IResultReceiver iResultReceiver) throws RemoteException {
            String str;
            int callingUserId = UserHandle.getCallingUserId();
            synchronized (AutofillManagerService.this.mLock) {
                AutofillManagerServiceImpl autofillManagerServiceImpl = (AutofillManagerServiceImpl) AutofillManagerService.this.peekServiceForUserLocked(callingUserId);
                if (autofillManagerServiceImpl != null) {
                    str = autofillManagerServiceImpl.getDefaultFieldClassificationAlgorithm(IAutoFillManager.Stub.getCallingUid());
                } else {
                    if (Helper.sVerbose) {
                        Slog.v("AutofillManagerService", "getDefaultFcAlgorithm(): no service for " + callingUserId);
                    }
                    str = null;
                }
            }
            AutofillManagerService.this.send(iResultReceiver, str);
        }

        public void setAugmentedAutofillWhitelist(List<String> list, List<ComponentName> list2, IResultReceiver iResultReceiver) throws RemoteException {
            boolean z;
            int callingUserId = UserHandle.getCallingUserId();
            synchronized (AutofillManagerService.this.mLock) {
                AutofillManagerServiceImpl autofillManagerServiceImpl = (AutofillManagerServiceImpl) AutofillManagerService.this.peekServiceForUserLocked(callingUserId);
                if (autofillManagerServiceImpl != null) {
                    z = autofillManagerServiceImpl.setAugmentedAutofillWhitelistLocked(list, list2, IAutoFillManager.Stub.getCallingUid());
                } else {
                    if (Helper.sVerbose) {
                        Slog.v("AutofillManagerService", "setAugmentedAutofillWhitelist(): no service for " + callingUserId);
                    }
                    z = false;
                }
            }
            AutofillManagerService.this.send(iResultReceiver, z ? 0 : -1);
        }

        public void getAvailableFieldClassificationAlgorithms(IResultReceiver iResultReceiver) throws RemoteException {
            String[] strArr;
            int callingUserId = UserHandle.getCallingUserId();
            synchronized (AutofillManagerService.this.mLock) {
                AutofillManagerServiceImpl autofillManagerServiceImpl = (AutofillManagerServiceImpl) AutofillManagerService.this.peekServiceForUserLocked(callingUserId);
                if (autofillManagerServiceImpl != null) {
                    strArr = autofillManagerServiceImpl.getAvailableFieldClassificationAlgorithms(IAutoFillManager.Stub.getCallingUid());
                } else {
                    if (Helper.sVerbose) {
                        Slog.v("AutofillManagerService", "getAvailableFcAlgorithms(): no service for " + callingUserId);
                    }
                    strArr = null;
                }
            }
            AutofillManagerService.this.send(iResultReceiver, strArr);
        }

        public void getAutofillServiceComponentName(IResultReceiver iResultReceiver) throws RemoteException {
            ComponentName componentName;
            int callingUserId = UserHandle.getCallingUserId();
            synchronized (AutofillManagerService.this.mLock) {
                AutofillManagerServiceImpl autofillManagerServiceImpl = (AutofillManagerServiceImpl) AutofillManagerService.this.peekServiceForUserLocked(callingUserId);
                if (autofillManagerServiceImpl != null) {
                    componentName = autofillManagerServiceImpl.getServiceComponentName();
                } else {
                    if (Helper.sVerbose) {
                        Slog.v("AutofillManagerService", "getAutofillServiceComponentName(): no service for " + callingUserId);
                    }
                    componentName = null;
                }
            }
            AutofillManagerService.this.send(iResultReceiver, componentName);
        }

        public void restoreSession(int i, IBinder iBinder, IBinder iBinder2, IResultReceiver iResultReceiver) throws RemoteException {
            boolean z;
            int callingUserId = UserHandle.getCallingUserId();
            Objects.requireNonNull(iBinder, "activityToken");
            Objects.requireNonNull(iBinder2, "appCallback");
            synchronized (AutofillManagerService.this.mLock) {
                AutofillManagerServiceImpl autofillManagerServiceImpl = (AutofillManagerServiceImpl) AutofillManagerService.this.peekServiceForUserLocked(callingUserId);
                if (autofillManagerServiceImpl != null) {
                    z = autofillManagerServiceImpl.restoreSession(i, IAutoFillManager.Stub.getCallingUid(), iBinder, iBinder2);
                } else {
                    if (Helper.sVerbose) {
                        Slog.v("AutofillManagerService", "restoreSession(): no service for " + callingUserId);
                    }
                    z = false;
                }
            }
            AutofillManagerService.this.send(iResultReceiver, z);
        }

        public void updateSession(int i, AutofillId autofillId, Rect rect, AutofillValue autofillValue, int i2, int i3, int i4) {
            synchronized (AutofillManagerService.this.mLock) {
                AutofillManagerServiceImpl autofillManagerServiceImpl = (AutofillManagerServiceImpl) AutofillManagerService.this.peekServiceForUserLocked(i4);
                if (autofillManagerServiceImpl != null) {
                    autofillManagerServiceImpl.updateSessionLocked(i, IAutoFillManager.Stub.getCallingUid(), autofillId, rect, autofillValue, i2, i3);
                } else if (Helper.sVerbose) {
                    Slog.v("AutofillManagerService", "updateSession(): no service for " + i4);
                }
            }
        }

        public void setAutofillFailure(int i, List<AutofillId> list, int i2) {
            synchronized (AutofillManagerService.this.mLock) {
                AutofillManagerServiceImpl autofillManagerServiceImpl = (AutofillManagerServiceImpl) AutofillManagerService.this.peekServiceForUserLocked(i2);
                if (autofillManagerServiceImpl != null) {
                    autofillManagerServiceImpl.setAutofillFailureLocked(i, IAutoFillManager.Stub.getCallingUid(), list);
                } else if (Helper.sVerbose) {
                    Slog.v("AutofillManagerService", "setAutofillFailure(): no service for " + i2);
                }
            }
        }

        public void finishSession(int i, int i2, int i3) {
            synchronized (AutofillManagerService.this.mLock) {
                AutofillManagerServiceImpl autofillManagerServiceImpl = (AutofillManagerServiceImpl) AutofillManagerService.this.peekServiceForUserLocked(i2);
                if (autofillManagerServiceImpl != null) {
                    autofillManagerServiceImpl.finishSessionLocked(i, IAutoFillManager.Stub.getCallingUid(), i3);
                } else if (Helper.sVerbose) {
                    Slog.v("AutofillManagerService", "finishSession(): no service for " + i2);
                }
            }
        }

        public void cancelSession(int i, int i2) {
            synchronized (AutofillManagerService.this.mLock) {
                AutofillManagerServiceImpl autofillManagerServiceImpl = (AutofillManagerServiceImpl) AutofillManagerService.this.peekServiceForUserLocked(i2);
                if (autofillManagerServiceImpl != null) {
                    autofillManagerServiceImpl.cancelSessionLocked(i, IAutoFillManager.Stub.getCallingUid());
                } else if (Helper.sVerbose) {
                    Slog.v("AutofillManagerService", "cancelSession(): no service for " + i2);
                }
            }
        }

        public void disableOwnedAutofillServices(int i) {
            synchronized (AutofillManagerService.this.mLock) {
                AutofillManagerServiceImpl autofillManagerServiceImpl = (AutofillManagerServiceImpl) AutofillManagerService.this.peekServiceForUserLocked(i);
                if (autofillManagerServiceImpl != null) {
                    autofillManagerServiceImpl.disableOwnedAutofillServicesLocked(Binder.getCallingUid());
                } else if (Helper.sVerbose) {
                    Slog.v("AutofillManagerService", "cancelSession(): no service for " + i);
                }
            }
        }

        public void isServiceSupported(int i, IResultReceiver iResultReceiver) {
            boolean z;
            synchronized (AutofillManagerService.this.mLock) {
                z = !AutofillManagerService.this.isDisabledLocked(i);
            }
            AutofillManagerService.this.send(iResultReceiver, z);
        }

        public void isServiceEnabled(int i, String str, IResultReceiver iResultReceiver) {
            boolean equals;
            synchronized (AutofillManagerService.this.mLock) {
                equals = Objects.equals(str, ((AutofillManagerServiceImpl) AutofillManagerService.this.getServiceForUserLocked(i)).getServicePackageName());
            }
            AutofillManagerService.this.send(iResultReceiver, equals);
        }

        public void onPendingSaveUi(int i, IBinder iBinder) {
            Objects.requireNonNull(iBinder, "token");
            boolean z = true;
            if (i != 1 && i != 2) {
                z = false;
            }
            Preconditions.checkArgument(z, "invalid operation: %d", new Object[]{Integer.valueOf(i)});
            synchronized (AutofillManagerService.this.mLock) {
                AutofillManagerServiceImpl autofillManagerServiceImpl = (AutofillManagerServiceImpl) AutofillManagerService.this.peekServiceForUserLocked(UserHandle.getCallingUserId());
                if (autofillManagerServiceImpl != null) {
                    autofillManagerServiceImpl.onPendingSaveUi(i, iBinder);
                }
            }
        }

        public void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
            boolean z;
            if (DumpUtils.checkDumpPermission(AutofillManagerService.this.getContext(), "AutofillManagerService", printWriter)) {
                boolean z2 = false;
                if (strArr != null) {
                    boolean z3 = false;
                    z = true;
                    for (String str : strArr) {
                        str.hashCode();
                        char c = 65535;
                        switch (str.hashCode()) {
                            case 900765093:
                                if (str.equals("--ui-only")) {
                                    c = 0;
                                    break;
                                }
                                break;
                            case 1098711592:
                                if (str.equals("--no-history")) {
                                    c = 1;
                                    break;
                                }
                                break;
                            case 1333069025:
                                if (str.equals("--help")) {
                                    c = 2;
                                    break;
                                }
                                break;
                        }
                        switch (c) {
                            case 0:
                                z3 = true;
                                break;
                            case 1:
                                z = false;
                                break;
                            case 2:
                                printWriter.println("Usage: dumpsys autofill [--ui-only|--no-history]");
                                return;
                            default:
                                Slog.w("AutofillManagerService", "Ignoring invalid dump arg: " + str);
                                break;
                        }
                    }
                    z2 = z3;
                } else {
                    z = true;
                }
                if (z2) {
                    AutofillManagerService.this.mUi.dump(printWriter);
                    return;
                }
                boolean z4 = Helper.sDebug;
                boolean z5 = Helper.sVerbose;
                try {
                    Helper.sVerbose = true;
                    Helper.sDebug = true;
                    synchronized (AutofillManagerService.this.mLock) {
                        printWriter.print("sDebug: ");
                        printWriter.print(z4);
                        printWriter.print(" sVerbose: ");
                        printWriter.println(z5);
                        AutofillManagerService.this.dumpLocked("", printWriter);
                        AutofillManagerService.this.mAugmentedAutofillResolver.dumpShort(printWriter);
                        printWriter.println();
                        printWriter.print("Max partitions per session: ");
                        printWriter.println(AutofillManagerService.sPartitionMaxCount);
                        printWriter.print("Max visible datasets: ");
                        printWriter.println(AutofillManagerService.sVisibleDatasetsMaxCount);
                        if (Helper.sFullScreenMode != null) {
                            printWriter.print("Overridden full-screen mode: ");
                            printWriter.println(Helper.sFullScreenMode);
                        }
                        printWriter.println("User data constraints: ");
                        UserData.dumpConstraints("  ", printWriter);
                        AutofillManagerService.this.mUi.dump(printWriter);
                        printWriter.print("Autofill Compat State: ");
                        AutofillManagerService.this.mAutofillCompatState.dump("  ", printWriter);
                        printWriter.print("from device config: ");
                        printWriter.println(AutofillManagerService.this.getAllowedCompatModePackagesFromDeviceConfig());
                        if (AutofillManagerService.this.mSupportedSmartSuggestionModes != 0) {
                            printWriter.print("Smart Suggestion modes: ");
                            printWriter.println(AutofillManager.getSmartSuggestionModeToString(AutofillManagerService.this.mSupportedSmartSuggestionModes));
                        }
                        printWriter.print("Augmented Service Idle Unbind Timeout: ");
                        printWriter.println(AutofillManagerService.this.mAugmentedServiceIdleUnbindTimeoutMs);
                        printWriter.print("Augmented Service Request Timeout: ");
                        printWriter.println(AutofillManagerService.this.mAugmentedServiceRequestTimeoutMs);
                        if (z) {
                            printWriter.println();
                            printWriter.println("Requests history:");
                            printWriter.println();
                            AutofillManagerService.this.mRequestsHistory.reverseDump(fileDescriptor, printWriter, strArr);
                            printWriter.println();
                            printWriter.println("UI latency history:");
                            printWriter.println();
                            AutofillManagerService.this.mUiLatencyHistory.reverseDump(fileDescriptor, printWriter, strArr);
                            printWriter.println();
                            printWriter.println("WTF history:");
                            printWriter.println();
                            AutofillManagerService.this.mWtfHistory.reverseDump(fileDescriptor, printWriter, strArr);
                        }
                        printWriter.println("Augmented Autofill State: ");
                        AutofillManagerService.this.mAugmentedAutofillState.dump("  ", printWriter);
                    }
                } finally {
                    Helper.sDebug = z4;
                    Helper.sVerbose = z5;
                }
            }
        }

        /* JADX WARN: Multi-variable type inference failed */
        public void onShellCommand(FileDescriptor fileDescriptor, FileDescriptor fileDescriptor2, FileDescriptor fileDescriptor3, String[] strArr, ShellCallback shellCallback, ResultReceiver resultReceiver) {
            new AutofillManagerServiceShellCommand(AutofillManagerService.this).exec(this, fileDescriptor, fileDescriptor2, fileDescriptor3, strArr, shellCallback, resultReceiver);
        }
    }
}
