package android.service.credentials;

import android.Manifest;
import android.app.AppGlobals;
import android.app.admin.DevicePolicyManager;
import android.app.admin.PackagePolicy;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.p001pm.ApplicationInfo;
import android.content.p001pm.PackageManager;
import android.content.p001pm.ResolveInfo;
import android.content.p001pm.ServiceInfo;
import android.content.res.Resources;
import android.credentials.CredentialProviderInfo;
import android.p008os.Bundle;
import android.p008os.RemoteException;
import android.p008os.UserHandle;
import android.util.Log;
import android.util.Slog;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
/* loaded from: classes3.dex */
public final class CredentialProviderInfoFactory {
    private static final String TAG = "CredentialProviderInfoFactory";

    public static CredentialProviderInfo create(Context context, ComponentName serviceComponent, int userId, boolean isSystemProvider) throws PackageManager.NameNotFoundException {
        return create(context, getServiceInfoOrThrow(serviceComponent, userId), isSystemProvider, false, false);
    }

    public static CredentialProviderInfo create(Context context, ServiceInfo serviceInfo, boolean isSystemProvider, boolean disableSystemAppVerificationForTests, boolean isEnabled) throws SecurityException {
        verifyProviderPermission(serviceInfo);
        if (isSystemProvider && !isValidSystemProvider(context, serviceInfo, disableSystemAppVerificationForTests)) {
            Slog.m96e(TAG, "Provider is not a valid system provider: " + serviceInfo);
            throw new SecurityException("Provider is not a valid system provider: " + serviceInfo);
        }
        return populateMetadata(context, serviceInfo).setSystemProvider(isSystemProvider).setEnabled(isEnabled).build();
    }

    public static CredentialProviderInfo createForTests(ServiceInfo serviceInfo, CharSequence overrideLabel, boolean isSystemProvider, boolean isEnabled, List<String> capabilities) {
        return new CredentialProviderInfo.Builder(serviceInfo).setEnabled(isEnabled).setOverrideLabel(overrideLabel).setSystemProvider(isSystemProvider).addCapabilities(capabilities).build();
    }

    private static void verifyProviderPermission(ServiceInfo serviceInfo) throws SecurityException {
        if (Manifest.C0000permission.BIND_CREDENTIAL_PROVIDER_SERVICE.equals(serviceInfo.permission)) {
            return;
        }
        throw new SecurityException("Service does not require the expected permission : android.permission.BIND_CREDENTIAL_PROVIDER_SERVICE");
    }

    private static boolean isSystemProviderWithValidPermission(ServiceInfo serviceInfo, Context context) {
        Objects.requireNonNull(context, "context must not be null");
        try {
            ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(serviceInfo.packageName, PackageManager.ApplicationInfoFlags.m191of(1048576L));
            if (appInfo == null || context.checkPermission(Manifest.C0000permission.PROVIDE_DEFAULT_ENABLED_CREDENTIAL_SERVICE, -1, appInfo.uid) != 0) {
                Slog.m94i(TAG, "SYS permission failed for: " + serviceInfo.packageName);
                return false;
            }
            Slog.m94i(TAG, "SYS permission granted for: " + serviceInfo.packageName);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            Slog.m96e(TAG, "Error getting info for " + serviceInfo + ": " + e);
            return false;
        }
    }

    private static boolean isValidSystemProvider(Context context, ServiceInfo serviceInfo, boolean disableSystemAppVerificationForTests) {
        Objects.requireNonNull(context, "context must not be null");
        if (disableSystemAppVerificationForTests) {
            Bundle metadata = serviceInfo.metaData;
            if (metadata == null) {
                Slog.m96e(TAG, "isValidSystemProvider - metadata is null: " + serviceInfo);
                return false;
            }
            return metadata.getBoolean(CredentialProviderService.TEST_SYSTEM_PROVIDER_META_DATA_KEY);
        }
        return isSystemProviderWithValidPermission(serviceInfo, context);
    }

    private static CredentialProviderInfo.Builder populateMetadata(Context context, ServiceInfo serviceInfo) {
        Resources resources;
        Objects.requireNonNull(context, "context must not be null");
        CredentialProviderInfo.Builder builder = new CredentialProviderInfo.Builder(serviceInfo);
        PackageManager pm = context.getPackageManager();
        Bundle metadata = serviceInfo.metaData;
        if (metadata == null) {
            Log.m108i(TAG, "populateMetadata - metadata is null");
            return builder;
        }
        try {
            resources = pm.getResourcesForApplication(serviceInfo.applicationInfo);
        } catch (PackageManager.NameNotFoundException e) {
            Slog.m96e(TAG, e.getMessage());
        }
        if (metadata != null && resources != null) {
            builder.addCapabilities(populateProviderCapabilities(resources, metadata, serviceInfo));
            return builder;
        }
        Log.m108i(TAG, "populateMetadata - resources is null");
        return builder;
    }

    private static List<String> populateProviderCapabilities(Resources resources, Bundle metadata, ServiceInfo serviceInfo) {
        List<String> output = new ArrayList<>();
        String[] capabilities = new String[0];
        try {
            capabilities = resources.getStringArray(metadata.getInt(CredentialProviderService.CAPABILITY_META_DATA_KEY));
        } catch (Resources.NotFoundException e) {
            Slog.m96e(TAG, "Failed to get capabilities: " + e.getMessage());
        }
        if (capabilities == null || capabilities.length == 0) {
            Slog.m96e(TAG, "No capabilities found for provider:" + serviceInfo.packageName);
            return output;
        }
        for (String capability : capabilities) {
            if (capability.isEmpty()) {
                Slog.m96e(TAG, "Skipping empty capability");
            } else {
                Slog.m96e(TAG, "Capabilities found for provider: " + capability);
                output.add(capability);
            }
        }
        return output;
    }

    private static ServiceInfo getServiceInfoOrThrow(ComponentName serviceComponent, int userId) throws PackageManager.NameNotFoundException {
        try {
            ServiceInfo si = AppGlobals.getPackageManager().getServiceInfo(serviceComponent, 128L, userId);
            if (si != null) {
                return si;
            }
        } catch (RemoteException e) {
            Slog.m92v(TAG, e.getMessage());
        }
        throw new PackageManager.NameNotFoundException(serviceComponent.toString());
    }

    private static List<ServiceInfo> getAvailableSystemServiceInfos(Context context, int userId, boolean disableSystemAppVerificationForTests) {
        Objects.requireNonNull(context, "context must not be null");
        List<ServiceInfo> services = new ArrayList<>();
        List<ResolveInfo> resolveInfos = new ArrayList<>();
        resolveInfos.addAll(context.getPackageManager().queryIntentServicesAsUser(new Intent(CredentialProviderService.SYSTEM_SERVICE_INTERFACE), PackageManager.ResolveInfoFlags.m188of(128L), userId));
        for (ResolveInfo resolveInfo : resolveInfos) {
            ServiceInfo serviceInfo = resolveInfo.serviceInfo;
            if (disableSystemAppVerificationForTests) {
                if (serviceInfo != null) {
                    services.add(serviceInfo);
                }
            } else {
                try {
                    ApplicationInfo appInfo = context.getPackageManager().getApplicationInfo(serviceInfo.packageName, PackageManager.ApplicationInfoFlags.m191of(1048576L));
                    if (appInfo != null && serviceInfo != null) {
                        services.add(serviceInfo);
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    Slog.m96e(TAG, "Error getting info for " + serviceInfo + ": " + e);
                } catch (SecurityException e2) {
                    Slog.m96e(TAG, "Error getting info for " + serviceInfo + ": " + e2);
                }
            }
        }
        return services;
    }

    public static List<CredentialProviderInfo> getAvailableSystemServices(Context context, int userId, boolean disableSystemAppVerificationForTests, Set<ComponentName> enabledServices) {
        Objects.requireNonNull(context, "context must not be null");
        List<CredentialProviderInfo> providerInfos = new ArrayList<>();
        for (ServiceInfo si : getAvailableSystemServiceInfos(context, userId, disableSystemAppVerificationForTests)) {
            try {
                CredentialProviderInfo cpi = create(context, si, true, disableSystemAppVerificationForTests, enabledServices.contains(si.getComponentName()));
                if (!cpi.isSystemProvider()) {
                    Slog.m96e(TAG, "Non system provider was in system provider list.");
                } else {
                    providerInfos.add(cpi);
                }
            } catch (SecurityException e) {
                Slog.m96e(TAG, "Failed to create CredentialProviderInfo: " + e);
            }
        }
        return providerInfos;
    }

    private static PackagePolicy getDeviceManagerPolicy(Context context, int userId) {
        Context newContext = context.createContextAsUser(UserHandle.m145of(userId), 0);
        try {
            DevicePolicyManager dpm = (DevicePolicyManager) newContext.getSystemService(DevicePolicyManager.class);
            return dpm.getCredentialManagerPolicy();
        } catch (SecurityException e) {
            Log.m110e(TAG, "Failed to get device policy: " + e);
            return null;
        }
    }

    public static CredentialProviderInfo getCredentialProviderFromPackageName(Context context, int userId, String packageName, int providerFilter, Set<ComponentName> enabledServices) {
        Objects.requireNonNull(context, "context must not be null");
        Objects.requireNonNull(packageName, "package name must not be null");
        Objects.requireNonNull(enabledServices, "enabledServices must not be null");
        for (CredentialProviderInfo credentialProviderInfo : getCredentialProviderServices(context, userId, providerFilter, enabledServices)) {
            if (credentialProviderInfo.getServiceInfo().packageName.equals(packageName)) {
                return credentialProviderInfo;
            }
        }
        return null;
    }

    public static List<CredentialProviderInfo> getCredentialProviderServices(Context context, int userId, int providerFilter, Set<ComponentName> enabledServices) {
        Objects.requireNonNull(context, "context must not be null");
        PackagePolicy pp = getDeviceManagerPolicy(context, userId);
        ProviderGenerator generator = new ProviderGenerator(context, pp, false, providerFilter);
        generator.addUserProviders(getUserProviders(context, userId, false, enabledServices));
        generator.addSystemProviders(getAvailableSystemServices(context, userId, false, enabledServices));
        return generator.getProviders();
    }

    public static List<CredentialProviderInfo> getCredentialProviderServicesForTesting(Context context, int userId, int providerFilter, Set<ComponentName> enabledServices) {
        Objects.requireNonNull(context, "context must not be null");
        PackagePolicy pp = getDeviceManagerPolicy(context, userId);
        ProviderGenerator generator = new ProviderGenerator(context, pp, true, providerFilter);
        generator.addUserProviders(getUserProviders(context, userId, true, enabledServices));
        generator.addSystemProviders(getAvailableSystemServices(context, userId, true, enabledServices));
        return generator.getProviders();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public static class ProviderGenerator {
        private final Context mContext;
        private final boolean mDisableSystemAppVerificationForTests;
        private final PackagePolicy mPp;
        private final int mProviderFilter;
        private final Map<String, CredentialProviderInfo> mServices = new HashMap();

        ProviderGenerator(Context context, PackagePolicy pp, boolean disableSystemAppVerificationForTests, int providerFilter) {
            this.mContext = context;
            this.mPp = pp;
            this.mDisableSystemAppVerificationForTests = disableSystemAppVerificationForTests;
            this.mProviderFilter = providerFilter;
        }

        private boolean isPackageAllowed(boolean isSystemProvider, String packageName) {
            PackagePolicy packagePolicy = this.mPp;
            if (packagePolicy == null) {
                return true;
            }
            if (isSystemProvider) {
                return packagePolicy.getPolicyType() == 2;
            }
            return packagePolicy.isPackageAllowed(packageName, new HashSet());
        }

        public List<CredentialProviderInfo> getProviders() {
            return new ArrayList(this.mServices.values());
        }

        public void addUserProviders(List<CredentialProviderInfo> providers) {
            for (CredentialProviderInfo cpi : providers) {
                if (!cpi.isSystemProvider()) {
                    addProvider(cpi);
                }
            }
        }

        public void addSystemProviders(List<CredentialProviderInfo> providers) {
            for (CredentialProviderInfo cpi : providers) {
                if (cpi.isSystemProvider()) {
                    addProvider(cpi);
                }
            }
        }

        private boolean isProviderAllowedWithFilter(CredentialProviderInfo cpi) {
            if (this.mProviderFilter == 0) {
                return true;
            }
            return cpi.isSystemProvider() ? this.mProviderFilter == 1 : this.mProviderFilter == 2;
        }

        private void addProvider(CredentialProviderInfo cpi) {
            String componentNameString = cpi.getServiceInfo().getComponentName().flattenToString();
            if (!isProviderAllowedWithFilter(cpi) || !isPackageAllowed(cpi.isSystemProvider(), cpi.getServiceInfo().packageName)) {
                return;
            }
            this.mServices.put(componentNameString, cpi);
        }
    }

    private static List<CredentialProviderInfo> getUserProviders(Context context, int userId, boolean disableSystemAppVerificationForTests, Set<ComponentName> enabledServices) {
        List<CredentialProviderInfo> services = new ArrayList<>();
        List<ResolveInfo> resolveInfos = context.getPackageManager().queryIntentServicesAsUser(new Intent(CredentialProviderService.SERVICE_INTERFACE), PackageManager.ResolveInfoFlags.m188of(128L), userId);
        for (ResolveInfo resolveInfo : resolveInfos) {
            ServiceInfo serviceInfo = resolveInfo.serviceInfo;
            if (serviceInfo == null) {
                Log.m108i(TAG, "No serviceInfo found for resolveInfo so skipping this provider");
            } else {
                try {
                    CredentialProviderInfo cpi = create(context, serviceInfo, false, disableSystemAppVerificationForTests, enabledServices.contains(serviceInfo.getComponentName()));
                    if (!cpi.isSystemProvider()) {
                        services.add(cpi);
                    }
                } catch (SecurityException e) {
                    Slog.m96e(TAG, "Error getting info for " + serviceInfo + ": " + e);
                } catch (Exception e2) {
                    Slog.m96e(TAG, "Error getting info for " + serviceInfo + ": " + e2);
                }
            }
        }
        return services;
    }
}
