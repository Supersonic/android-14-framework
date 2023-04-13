package com.android.server;

import android.content.ComponentName;
import android.content.Context;
import android.os.CarrierAssociatedAppEntry;
import android.os.ISystemConfig;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.SparseArray;
import com.android.internal.util.ArrayUtils;
import com.android.server.SystemConfigService;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
/* loaded from: classes.dex */
public class SystemConfigService extends SystemService {
    public final Context mContext;
    public final ISystemConfig.Stub mInterface;

    /* renamed from: com.android.server.SystemConfigService$1 */
    /* loaded from: classes.dex */
    public class ISystemConfig$StubC02331 extends ISystemConfig.Stub {
        public ISystemConfig$StubC02331() {
        }

        public List<String> getDisabledUntilUsedPreinstalledCarrierApps() {
            SystemConfigService.this.mContext.enforceCallingOrSelfPermission("android.permission.READ_CARRIER_APP_INFO", "getDisabledUntilUsedPreInstalledCarrierApps requires READ_CARRIER_APP_INFO");
            return new ArrayList(SystemConfig.getInstance().getDisabledUntilUsedPreinstalledCarrierApps());
        }

        public Map getDisabledUntilUsedPreinstalledCarrierAssociatedApps() {
            SystemConfigService.this.mContext.enforceCallingOrSelfPermission("android.permission.READ_CARRIER_APP_INFO", "getDisabledUntilUsedPreInstalledCarrierAssociatedApps requires READ_CARRIER_APP_INFO");
            return (Map) SystemConfig.getInstance().getDisabledUntilUsedPreinstalledCarrierAssociatedApps().entrySet().stream().collect(Collectors.toMap(new Function() { // from class: com.android.server.SystemConfigService$1$$ExternalSyntheticLambda0
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    return (String) ((Map.Entry) obj).getKey();
                }
            }, new Function() { // from class: com.android.server.SystemConfigService$1$$ExternalSyntheticLambda1
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    List lambda$getDisabledUntilUsedPreinstalledCarrierAssociatedApps$1;
                    lambda$getDisabledUntilUsedPreinstalledCarrierAssociatedApps$1 = SystemConfigService.ISystemConfig$StubC02331.lambda$getDisabledUntilUsedPreinstalledCarrierAssociatedApps$1((Map.Entry) obj);
                    return lambda$getDisabledUntilUsedPreinstalledCarrierAssociatedApps$1;
                }
            }));
        }

        public static /* synthetic */ List lambda$getDisabledUntilUsedPreinstalledCarrierAssociatedApps$1(Map.Entry entry) {
            return (List) ((List) entry.getValue()).stream().map(new Function() { // from class: com.android.server.SystemConfigService$1$$ExternalSyntheticLambda2
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    String str;
                    str = ((CarrierAssociatedAppEntry) obj).packageName;
                    return str;
                }
            }).collect(Collectors.toList());
        }

        public Map getDisabledUntilUsedPreinstalledCarrierAssociatedAppEntries() {
            SystemConfigService.this.mContext.enforceCallingOrSelfPermission("android.permission.READ_CARRIER_APP_INFO", "getDisabledUntilUsedPreInstalledCarrierAssociatedAppEntries requires READ_CARRIER_APP_INFO");
            return SystemConfig.getInstance().getDisabledUntilUsedPreinstalledCarrierAssociatedApps();
        }

        public int[] getSystemPermissionUids(String str) {
            SystemConfigService.this.mContext.enforceCallingOrSelfPermission("android.permission.GET_RUNTIME_PERMISSIONS", "getSystemPermissionUids requires GET_RUNTIME_PERMISSIONS");
            ArrayList arrayList = new ArrayList();
            SparseArray<ArraySet<String>> systemPermissions = SystemConfig.getInstance().getSystemPermissions();
            for (int i = 0; i < systemPermissions.size(); i++) {
                ArraySet<String> valueAt = systemPermissions.valueAt(i);
                if (valueAt != null && valueAt.contains(str)) {
                    arrayList.add(Integer.valueOf(systemPermissions.keyAt(i)));
                }
            }
            return ArrayUtils.convertToIntArray(arrayList);
        }

        public List<ComponentName> getEnabledComponentOverrides(String str) {
            ArrayMap<String, Boolean> componentsEnabledStates = SystemConfig.getInstance().getComponentsEnabledStates(str);
            ArrayList arrayList = new ArrayList();
            if (componentsEnabledStates != null) {
                for (Map.Entry<String, Boolean> entry : componentsEnabledStates.entrySet()) {
                    if (Boolean.TRUE.equals(entry.getValue())) {
                        arrayList.add(new ComponentName(str, entry.getKey()));
                    }
                }
            }
            return arrayList;
        }

        public List<ComponentName> getDefaultVrComponents() {
            SystemConfigService.this.getContext().enforceCallingOrSelfPermission("android.permission.QUERY_ALL_PACKAGES", "Caller must hold android.permission.QUERY_ALL_PACKAGES");
            return new ArrayList(SystemConfig.getInstance().getDefaultVrComponents());
        }
    }

    public SystemConfigService(Context context) {
        super(context);
        this.mInterface = new ISystemConfig$StubC02331();
        this.mContext = context;
    }

    @Override // com.android.server.SystemService
    public void onStart() {
        publishBinderService("system_config", this.mInterface);
    }
}
