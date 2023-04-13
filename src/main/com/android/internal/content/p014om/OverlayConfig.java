package com.android.internal.content.p014om;

import android.content.p001pm.PackagePartitions;
import android.p008os.Trace;
import android.util.ArrayMap;
import android.util.IndentingPrintWriter;
import android.util.Log;
import com.android.apex.ApexInfo;
import com.android.apex.XmlParser;
import com.android.internal.content.p014om.OverlayConfig;
import com.android.internal.content.p014om.OverlayConfigParser;
import com.android.internal.content.p014om.OverlayScanner;
import com.android.internal.util.Preconditions;
import com.android.internal.util.function.TriConsumer;
import java.io.File;
import java.io.FileInputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;
/* renamed from: com.android.internal.content.om.OverlayConfig */
/* loaded from: classes4.dex */
public class OverlayConfig {
    public static final int DEFAULT_PRIORITY = Integer.MAX_VALUE;
    static final String TAG = "OverlayConfig";
    private static OverlayConfig sInstance;
    private static final Comparator<OverlayConfigParser.ParsedConfiguration> sStaticOverlayComparator = new Comparator() { // from class: com.android.internal.content.om.OverlayConfig$$ExternalSyntheticLambda5
        @Override // java.util.Comparator
        public final int compare(Object obj, Object obj2) {
            return OverlayConfig.lambda$static$0((OverlayConfigParser.ParsedConfiguration) obj, (OverlayConfigParser.ParsedConfiguration) obj2);
        }
    };
    private final ArrayMap<String, Configuration> mConfigurations = new ArrayMap<>();

    /* renamed from: com.android.internal.content.om.OverlayConfig$PackageProvider */
    /* loaded from: classes4.dex */
    public interface PackageProvider {

        /* renamed from: com.android.internal.content.om.OverlayConfig$PackageProvider$Package */
        /* loaded from: classes4.dex */
        public interface Package {
            String getBaseApkPath();

            int getOverlayPriority();

            String getOverlayTarget();

            String getPackageName();

            int getTargetSdkVersion();

            boolean isOverlayIsStatic();
        }

        void forEachPackage(TriConsumer<Package, Boolean, File> triConsumer);
    }

    private static native String[] createIdmap(String str, String[] strArr, String[] strArr2, boolean z);

    /* renamed from: com.android.internal.content.om.OverlayConfig$Configuration */
    /* loaded from: classes4.dex */
    public static final class Configuration {
        public final int configIndex;
        public final OverlayConfigParser.ParsedConfiguration parsedConfig;

        public Configuration(OverlayConfigParser.ParsedConfiguration parsedConfig, int configIndex) {
            this.parsedConfig = parsedConfig;
            this.configIndex = configIndex;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ int lambda$static$0(OverlayConfigParser.ParsedConfiguration c1, OverlayConfigParser.ParsedConfiguration c2) {
        OverlayScanner.ParsedOverlayInfo o1 = c1.parsedInfo;
        OverlayScanner.ParsedOverlayInfo o2 = c2.parsedInfo;
        Preconditions.checkArgument(o1.isStatic && o2.isStatic, "attempted to sort non-static overlay");
        if (!o1.targetPackageName.equals(o2.targetPackageName)) {
            return o1.targetPackageName.compareTo(o2.targetPackageName);
        }
        int comparedPriority = o1.priority - o2.priority;
        return comparedPriority == 0 ? o1.path.compareTo(o2.path) : comparedPriority;
    }

    public OverlayConfig(final File rootDirectory, Supplier<OverlayScanner> scannerFactory, PackageProvider packageProvider) {
        ArrayList<OverlayConfigParser.OverlayPartition> partitions;
        ArrayList<OverlayScanner.ParsedOverlayInfo> partitionOverlayInfos;
        ArrayList<OverlayConfigParser.OverlayPartition> partitions2;
        ArrayList<OverlayConfigParser.OverlayPartition> partitions3;
        int m;
        int i = 1;
        Preconditions.checkArgument((scannerFactory == null) != (packageProvider == null), "scannerFactory and packageProvider cannot be both null or both non-null");
        if (rootDirectory == null) {
            partitions = new ArrayList<>(PackagePartitions.getOrderedPartitions(new Function() { // from class: com.android.internal.content.om.OverlayConfig$$ExternalSyntheticLambda0
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    return new OverlayConfigParser.OverlayPartition((PackagePartitions.SystemPartition) obj);
                }
            }));
        } else {
            partitions = new ArrayList<>(PackagePartitions.getOrderedPartitions(new Function() { // from class: com.android.internal.content.om.OverlayConfig$$ExternalSyntheticLambda1
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    return OverlayConfig.lambda$new$1(rootDirectory, (PackagePartitions.SystemPartition) obj);
                }
            }));
        }
        ArrayMap<Integer, List<String>> activeApexesPerPartition = getActiveApexes(partitions);
        Map<String, OverlayScanner.ParsedOverlayInfo> packageManagerOverlayInfos = packageProvider == null ? null : getOverlayPackageInfos(packageProvider);
        ArrayList<OverlayConfigParser.ParsedConfiguration> overlays = new ArrayList<>();
        int i2 = 0;
        int n = partitions.size();
        while (i2 < n) {
            OverlayConfigParser.OverlayPartition partition = partitions.get(i2);
            OverlayScanner scanner = scannerFactory == null ? null : scannerFactory.get();
            ArrayList<OverlayConfigParser.ParsedConfiguration> partitionOverlays = OverlayConfigParser.getConfigurations(partition, scanner, packageManagerOverlayInfos, activeApexesPerPartition.getOrDefault(Integer.valueOf(partition.type), Collections.emptyList()));
            if (partitionOverlays != null) {
                overlays.addAll(partitionOverlays);
                partitions2 = partitions;
            } else {
                if (scannerFactory != null) {
                    partitionOverlayInfos = new ArrayList<>(scanner.getAllParsedInfos());
                } else {
                    partitionOverlayInfos = new ArrayList<>(packageManagerOverlayInfos.values());
                    for (int j = partitionOverlayInfos.size() - i; j >= 0; j--) {
                        if (!partition.containsFile(partitionOverlayInfos.get(j).getOriginalPartitionPath())) {
                            partitionOverlayInfos.remove(j);
                        }
                    }
                }
                ArrayList<OverlayConfigParser.ParsedConfiguration> partitionConfigs = new ArrayList<>();
                int j2 = 0;
                int m2 = partitionOverlayInfos.size();
                while (j2 < m2) {
                    OverlayScanner.ParsedOverlayInfo p = partitionOverlayInfos.get(j2);
                    if (!p.isStatic) {
                        partitions3 = partitions;
                        m = m2;
                    } else {
                        partitions3 = partitions;
                        m = m2;
                        partitionConfigs.add(new OverlayConfigParser.ParsedConfiguration(p.packageName, true, false, partition.policy, p, null));
                    }
                    j2++;
                    partitions = partitions3;
                    m2 = m;
                }
                partitions2 = partitions;
                partitionConfigs.sort(sStaticOverlayComparator);
                overlays.addAll(partitionConfigs);
            }
            i2++;
            partitions = partitions2;
            i = 1;
        }
        int n2 = overlays.size();
        for (int i3 = 0; i3 < n2; i3++) {
            OverlayConfigParser.ParsedConfiguration config = overlays.get(i3);
            this.mConfigurations.put(config.packageName, new Configuration(config, i3));
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ OverlayConfigParser.OverlayPartition lambda$new$1(File rootDirectory, PackagePartitions.SystemPartition p) {
        return new OverlayConfigParser.OverlayPartition(new File(rootDirectory, p.getNonConicalFolder().getPath()), p);
    }

    public static OverlayConfig getZygoteInstance() {
        Trace.traceBegin(67108864L, "OverlayConfig#getZygoteInstance");
        try {
            return new OverlayConfig(null, new Supplier() { // from class: com.android.internal.content.om.OverlayConfig$$ExternalSyntheticLambda2
                @Override // java.util.function.Supplier
                public final Object get() {
                    return new OverlayScanner();
                }
            }, null);
        } finally {
            Trace.traceEnd(67108864L);
        }
    }

    public static OverlayConfig initializeSystemInstance(PackageProvider packageProvider) {
        Trace.traceBegin(67108864L, "OverlayConfig#initializeSystemInstance");
        try {
            sInstance = new OverlayConfig(null, null, packageProvider);
            Trace.traceEnd(67108864L);
            return sInstance;
        } catch (Throwable th) {
            Trace.traceEnd(67108864L);
            throw th;
        }
    }

    public static OverlayConfig getSystemInstance() {
        OverlayConfig overlayConfig = sInstance;
        if (overlayConfig == null) {
            throw new IllegalStateException("System instance not initialized");
        }
        return overlayConfig;
    }

    public Configuration getConfiguration(String packageName) {
        return this.mConfigurations.get(packageName);
    }

    public boolean isEnabled(String packageName) {
        Configuration config = this.mConfigurations.get(packageName);
        if (config == null) {
            return false;
        }
        return config.parsedConfig.enabled;
    }

    public boolean isMutable(String packageName) {
        Configuration config = this.mConfigurations.get(packageName);
        if (config == null) {
            return true;
        }
        return config.parsedConfig.mutable;
    }

    public int getPriority(String packageName) {
        Configuration config = this.mConfigurations.get(packageName);
        if (config == null) {
            return Integer.MAX_VALUE;
        }
        return config.configIndex;
    }

    private ArrayList<Configuration> getSortedOverlays() {
        ArrayList<Configuration> sortedOverlays = new ArrayList<>();
        int n = this.mConfigurations.size();
        for (int i = 0; i < n; i++) {
            sortedOverlays.add(this.mConfigurations.valueAt(i));
        }
        sortedOverlays.sort(Comparator.comparingInt(new ToIntFunction() { // from class: com.android.internal.content.om.OverlayConfig$$ExternalSyntheticLambda4
            @Override // java.util.function.ToIntFunction
            public final int applyAsInt(Object obj) {
                int i2;
                i2 = ((OverlayConfig.Configuration) obj).configIndex;
                return i2;
            }
        }));
        return sortedOverlays;
    }

    private static Map<String, OverlayScanner.ParsedOverlayInfo> getOverlayPackageInfos(PackageProvider packageManager) {
        final HashMap<String, OverlayScanner.ParsedOverlayInfo> overlays = new HashMap<>();
        packageManager.forEachPackage(new TriConsumer() { // from class: com.android.internal.content.om.OverlayConfig$$ExternalSyntheticLambda3
            @Override // com.android.internal.util.function.TriConsumer
            public final void accept(Object obj, Object obj2, Object obj3) {
                OverlayConfig.lambda$getOverlayPackageInfos$3(overlays, (OverlayConfig.PackageProvider.Package) obj, (Boolean) obj2, (File) obj3);
            }
        });
        return overlays;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ void lambda$getOverlayPackageInfos$3(HashMap overlays, PackageProvider.Package p, Boolean isSystem, File preInstalledApexPath) {
        if (p.getOverlayTarget() != null && isSystem.booleanValue()) {
            overlays.put(p.getPackageName(), new OverlayScanner.ParsedOverlayInfo(p.getPackageName(), p.getOverlayTarget(), p.getTargetSdkVersion(), p.isOverlayIsStatic(), p.getOverlayPriority(), new File(p.getBaseApkPath()), preInstalledApexPath));
        }
    }

    private static ArrayMap<Integer, List<String>> getActiveApexes(List<OverlayConfigParser.OverlayPartition> partitions) {
        ArrayMap<Integer, List<String>> result = new ArrayMap<>();
        for (OverlayConfigParser.OverlayPartition partition : partitions) {
            result.put(Integer.valueOf(partition.type), new ArrayList());
        }
        File apexInfoList = new File("/apex/apex-info-list.xml");
        if (apexInfoList.exists() && apexInfoList.canRead()) {
            try {
                FileInputStream stream = new FileInputStream(apexInfoList);
                List<ApexInfo> apexInfos = XmlParser.readApexInfoList(stream).getApexInfo();
                for (ApexInfo info : apexInfos) {
                    if (info.getIsActive()) {
                        Iterator<OverlayConfigParser.OverlayPartition> it = partitions.iterator();
                        while (true) {
                            if (it.hasNext()) {
                                OverlayConfigParser.OverlayPartition partition2 = it.next();
                                if (partition2.containsPath(info.getPreinstalledModulePath())) {
                                    result.get(Integer.valueOf(partition2.type)).add(info.getModuleName());
                                    break;
                                }
                            }
                        }
                    }
                }
                stream.close();
            } catch (Exception e) {
                Log.m104w(TAG, "Error reading apex-info-list: " + e);
            }
        }
        return result;
    }

    /* renamed from: com.android.internal.content.om.OverlayConfig$IdmapInvocation */
    /* loaded from: classes4.dex */
    public static class IdmapInvocation {
        public final boolean enforceOverlayable;
        public final ArrayList<String> overlayPaths = new ArrayList<>();
        public final String policy;

        IdmapInvocation(boolean enforceOverlayable, String policy) {
            this.enforceOverlayable = enforceOverlayable;
            this.policy = policy;
        }

        public String toString() {
            return getClass().getSimpleName() + String.format("{enforceOverlayable=%s, policy=%s, overlayPaths=[%s]}", Boolean.valueOf(this.enforceOverlayable), this.policy, String.join(", ", this.overlayPaths));
        }
    }

    public ArrayList<IdmapInvocation> getImmutableFrameworkOverlayIdmapInvocations() {
        ArrayList<IdmapInvocation> idmapInvocations = new ArrayList<>();
        ArrayList<Configuration> sortedConfigs = getSortedOverlays();
        int n = sortedConfigs.size();
        for (int i = 0; i < n; i++) {
            Configuration overlay = sortedConfigs.get(i);
            if (!overlay.parsedConfig.mutable && overlay.parsedConfig.enabled && "android".equals(overlay.parsedConfig.parsedInfo.targetPackageName)) {
                boolean enforceOverlayable = overlay.parsedConfig.parsedInfo.targetSdkVersion >= 29;
                IdmapInvocation invocation = null;
                if (!idmapInvocations.isEmpty()) {
                    IdmapInvocation last = idmapInvocations.get(idmapInvocations.size() - 1);
                    if (last.enforceOverlayable == enforceOverlayable && last.policy.equals(overlay.parsedConfig.policy)) {
                        invocation = last;
                    }
                }
                if (invocation == null) {
                    invocation = new IdmapInvocation(enforceOverlayable, overlay.parsedConfig.policy);
                    idmapInvocations.add(invocation);
                }
                invocation.overlayPaths.add(overlay.parsedConfig.parsedInfo.path.getAbsolutePath());
            }
        }
        return idmapInvocations;
    }

    public String[] createImmutableFrameworkIdmapsInZygote() {
        ArrayList<String> idmapPaths = new ArrayList<>();
        ArrayList<IdmapInvocation> idmapInvocations = getImmutableFrameworkOverlayIdmapInvocations();
        int n = idmapInvocations.size();
        for (int i = 0; i < n; i++) {
            IdmapInvocation invocation = idmapInvocations.get(i);
            String[] idmaps = createIdmap("/system/framework/framework-res.apk", (String[]) invocation.overlayPaths.toArray(new String[0]), new String[]{"public", invocation.policy}, invocation.enforceOverlayable);
            if (idmaps == null) {
                Log.m104w(TAG, "'idmap2 create-multiple' failed: no mutable=\"false\" overlays targeting \"android\" will be loaded");
                return new String[0];
            }
            idmapPaths.addAll(Arrays.asList(idmaps));
        }
        return (String[]) idmapPaths.toArray(new String[0]);
    }

    public void dump(PrintWriter writer) {
        IndentingPrintWriter ipw = new IndentingPrintWriter(writer);
        ipw.println("Overlay configurations:");
        ipw.increaseIndent();
        ArrayList<Configuration> configurations = new ArrayList<>(this.mConfigurations.values());
        configurations.sort(Comparator.comparingInt(new ToIntFunction() { // from class: com.android.internal.content.om.OverlayConfig$$ExternalSyntheticLambda6
            @Override // java.util.function.ToIntFunction
            public final int applyAsInt(Object obj) {
                int i;
                i = ((OverlayConfig.Configuration) obj).configIndex;
                return i;
            }
        }));
        for (int i = 0; i < configurations.size(); i++) {
            Configuration configuration = configurations.get(i);
            ipw.print(configuration.configIndex);
            ipw.print(", ");
            ipw.print(configuration.parsedConfig);
            ipw.println();
        }
        ipw.decreaseIndent();
        ipw.println();
    }
}
