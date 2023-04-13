package android.permission;

import android.Manifest;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.p001pm.ApplicationInfo;
import android.content.p001pm.Attribution;
import android.content.p001pm.PackageInfo;
import android.content.p001pm.PackageManager;
import android.content.res.Resources;
import android.icu.text.ListFormatter;
import android.location.LocationManager;
import android.media.AudioManager;
import android.p008os.Process;
import android.p008os.UserHandle;
import android.provider.DeviceConfig;
import android.telephony.TelephonyManager;
import android.util.ArrayMap;
import android.util.ArraySet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
/* loaded from: classes3.dex */
public class PermissionUsageHelper implements AppOpsManager.OnOpActiveChangedListener, AppOpsManager.OnOpStartedListener {
    private static final long DEFAULT_RECENT_TIME_MS = 15000;
    private static final long DEFAULT_RUNNING_TIME_MS = 5000;
    private static final String PROPERTY_CAMERA_MIC_ICONS_ENABLED = "camera_mic_icons_enabled";
    private static final String PROPERTY_LOCATION_INDICATORS_ENABLED = "location_indicators_enabled";
    private static final String PROPERTY_PERMISSIONS_HUB_2_ENABLED = "permissions_hub_2_enabled";
    private static final String RECENT_ACCESS_TIME_MS = "recent_access_time_ms";
    private static final String RUNNING_ACCESS_TIME_MS = "running_access_time_ms";
    private static final String SYSTEM_PKG = "android";
    private AppOpsManager mAppOpsManager;
    private final ArrayMap<Integer, ArrayList<AccessChainLink>> mAttributionChains = new ArrayMap<>();
    private Context mContext;
    private PackageManager mPkgManager;
    private ArrayMap<UserHandle, Context> mUserContexts;
    private static final List<String> LOCATION_OPS = List.of(AppOpsManager.OPSTR_COARSE_LOCATION, AppOpsManager.OPSTR_FINE_LOCATION);
    private static final List<String> MIC_OPS = List.of(AppOpsManager.OPSTR_PHONE_CALL_MICROPHONE, AppOpsManager.OPSTR_RECEIVE_AMBIENT_TRIGGER_AUDIO, AppOpsManager.OPSTR_RECORD_AUDIO);
    private static final List<String> CAMERA_OPS = List.of(AppOpsManager.OPSTR_PHONE_CALL_CAMERA, AppOpsManager.OPSTR_CAMERA);

    private static boolean shouldShowPermissionsHub() {
        return DeviceConfig.getBoolean("privacy", "permissions_hub_2_enabled", false);
    }

    private static boolean shouldShowIndicators() {
        return DeviceConfig.getBoolean("privacy", "camera_mic_icons_enabled", true) || shouldShowPermissionsHub();
    }

    private static boolean shouldShowLocationIndicator() {
        return DeviceConfig.getBoolean("privacy", "location_indicators_enabled", false);
    }

    private static long getRecentThreshold(Long now) {
        return now.longValue() - DeviceConfig.getLong("privacy", RECENT_ACCESS_TIME_MS, (long) DEFAULT_RECENT_TIME_MS);
    }

    private static long getRunningThreshold(Long now) {
        return now.longValue() - DeviceConfig.getLong("privacy", RUNNING_ACCESS_TIME_MS, 5000L);
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    private static String getGroupForOp(String op) {
        char c;
        switch (op.hashCode()) {
            case -1671423430:
                if (op.equals(AppOpsManager.OPSTR_COARSE_LOCATION)) {
                    c = 5;
                    break;
                }
                c = 65535;
                break;
            case -210165041:
                if (op.equals(AppOpsManager.OPSTR_FINE_LOCATION)) {
                    c = 6;
                    break;
                }
                c = 65535;
                break;
            case 148526607:
                if (op.equals(AppOpsManager.OPSTR_PHONE_CALL_MICROPHONE)) {
                    c = 3;
                    break;
                }
                c = 65535;
                break;
            case 646711553:
                if (op.equals(AppOpsManager.OPSTR_RECEIVE_AMBIENT_TRIGGER_AUDIO)) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            case 1191287187:
                if (op.equals(AppOpsManager.OPSTR_RECORD_AUDIO)) {
                    c = 1;
                    break;
                }
                c = 65535;
                break;
            case 1390525066:
                if (op.equals(AppOpsManager.OPSTR_PHONE_CALL_CAMERA)) {
                    c = 4;
                    break;
                }
                c = 65535;
                break;
            case 1528082064:
                if (op.equals(AppOpsManager.OPSTR_CAMERA)) {
                    c = 2;
                    break;
                }
                c = 65535;
                break;
            default:
                c = 65535;
                break;
        }
        switch (c) {
            case 0:
            case 1:
                return Manifest.permission_group.MICROPHONE;
            case 2:
                return Manifest.permission_group.CAMERA;
            case 3:
            case 4:
                return op;
            case 5:
            case 6:
                return Manifest.permission_group.LOCATION;
            default:
                throw new IllegalArgumentException("Unknown app op: " + op);
        }
    }

    public PermissionUsageHelper(Context context) {
        this.mContext = context;
        this.mPkgManager = context.getPackageManager();
        this.mAppOpsManager = (AppOpsManager) context.getSystemService(AppOpsManager.class);
        ArrayMap<UserHandle, Context> arrayMap = new ArrayMap<>();
        this.mUserContexts = arrayMap;
        arrayMap.put(Process.myUserHandle(), this.mContext);
        String[] opStrs = {AppOpsManager.OPSTR_CAMERA, AppOpsManager.OPSTR_RECORD_AUDIO};
        this.mAppOpsManager.startWatchingActive(opStrs, context.getMainExecutor(), this);
        int[] ops = {26, 27};
        this.mAppOpsManager.startWatchingStarted(ops, this);
    }

    private Context getUserContext(UserHandle user) {
        if (!this.mUserContexts.containsKey(user)) {
            this.mUserContexts.put(user, this.mContext.createContextAsUser(user, 0));
        }
        return this.mUserContexts.get(user);
    }

    public void tearDown() {
        this.mAppOpsManager.stopWatchingActive(this);
        this.mAppOpsManager.stopWatchingStarted(this);
    }

    @Override // android.app.AppOpsManager.OnOpActiveChangedListener
    public void onOpActiveChanged(String op, int uid, String packageName, boolean active) {
    }

    @Override // android.app.AppOpsManager.OnOpActiveChangedListener
    public void onOpActiveChanged(String op, int uid, String packageName, String attributionTag, boolean active, int attributionFlags, int attributionChainId) {
        if (active) {
            return;
        }
        synchronized (this.mAttributionChains) {
            try {
                this.mAttributionChains.remove(Integer.valueOf(attributionChainId));
                int numChains = this.mAttributionChains.size();
                ArrayList<Integer> toRemove = new ArrayList<>();
                for (int i = 0; i < numChains; i++) {
                    int chainId = this.mAttributionChains.keyAt(i).intValue();
                    ArrayList<AccessChainLink> chain = this.mAttributionChains.valueAt(i);
                    int chainSize = chain.size();
                    int j = 0;
                    while (true) {
                        if (j < chainSize) {
                            AccessChainLink link = chain.get(j);
                            try {
                                if (!link.packageAndOpEquals(op, packageName, attributionTag, uid)) {
                                    j++;
                                } else {
                                    toRemove.add(Integer.valueOf(chainId));
                                    break;
                                }
                            } catch (Throwable th) {
                                th = th;
                                throw th;
                            }
                        }
                    }
                }
                this.mAttributionChains.removeAll(toRemove);
            } catch (Throwable th2) {
                th = th2;
            }
        }
    }

    @Override // android.app.AppOpsManager.OnOpStartedListener
    public void onOpStarted(int op, int uid, String packageName, String attributionTag, int flags, int result) {
    }

    @Override // android.app.AppOpsManager.OnOpStartedListener
    public void onOpStarted(int op, int uid, String packageName, String attributionTag, int flags, int result, int startedType, int attributionFlags, int attributionChainId) {
        if (startedType != 0) {
            if (attributionChainId == -1 || attributionFlags == 0) {
                return;
            }
            if ((attributionFlags & 8) == 0) {
                return;
            }
            synchronized (this.mAttributionChains) {
                addLinkToChainIfNotPresentLocked(AppOpsManager.opToPublicName(op), packageName, uid, attributionTag, attributionFlags, attributionChainId);
            }
        }
    }

    private void addLinkToChainIfNotPresentLocked(String op, String packageName, int uid, String attributionTag, int attributionFlags, int attributionChainId) {
        ArrayList<AccessChainLink> currentChain = this.mAttributionChains.computeIfAbsent(Integer.valueOf(attributionChainId), new Function() { // from class: android.permission.PermissionUsageHelper$$ExternalSyntheticLambda0
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return PermissionUsageHelper.lambda$addLinkToChainIfNotPresentLocked$0((Integer) obj);
            }
        });
        AccessChainLink link = new AccessChainLink(op, packageName, attributionTag, uid, attributionFlags);
        if (currentChain.contains(link)) {
            return;
        }
        int currSize = currentChain.size();
        if (currSize == 0 || link.isEnd() || !currentChain.get(currSize - 1).isEnd()) {
            currentChain.add(link);
        } else if (link.isStart()) {
            currentChain.add(0, link);
        } else if (currentChain.get(currentChain.size() - 1).isEnd()) {
            currentChain.add(currSize - 1, link);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ ArrayList lambda$addLinkToChainIfNotPresentLocked$0(Integer k) {
        return new ArrayList();
    }

    public List<PermissionGroupUsage> getOpUsageData(boolean isMicMuted) {
        PermissionUsageHelper permissionUsageHelper = this;
        List<PermissionGroupUsage> usages = new ArrayList<>();
        if (!shouldShowIndicators()) {
            return usages;
        }
        List<String> ops = new ArrayList<>(CAMERA_OPS);
        if (shouldShowLocationIndicator()) {
            ops.addAll(LOCATION_OPS);
        }
        if (!isMicMuted) {
            ops.addAll(MIC_OPS);
        }
        Map<String, List<OpUsage>> rawUsages = permissionUsageHelper.getOpUsages(ops);
        ArrayList<String> usedPermGroups = new ArrayList<>(rawUsages.keySet());
        AudioManager audioManager = (AudioManager) permissionUsageHelper.mContext.getSystemService(AudioManager.class);
        String str = AppOpsManager.OPSTR_PHONE_CALL_CAMERA;
        boolean contains = usedPermGroups.contains(AppOpsManager.OPSTR_PHONE_CALL_CAMERA);
        String str2 = AppOpsManager.OPSTR_PHONE_CALL_MICROPHONE;
        boolean hasPhoneCall = contains || usedPermGroups.contains(AppOpsManager.OPSTR_PHONE_CALL_MICROPHONE);
        if (hasPhoneCall && usedPermGroups.contains(Manifest.permission_group.MICROPHONE) && audioManager.getMode() == 3) {
            TelephonyManager telephonyManager = (TelephonyManager) permissionUsageHelper.mContext.getSystemService(TelephonyManager.class);
            List<OpUsage> permUsages = rawUsages.get(Manifest.permission_group.MICROPHONE);
            for (int usageNum = 0; usageNum < permUsages.size(); usageNum++) {
                if (telephonyManager.checkCarrierPrivilegesForPackage(permUsages.get(usageNum).packageName) == 1) {
                    usedPermGroups.remove(AppOpsManager.OPSTR_PHONE_CALL_CAMERA);
                    usedPermGroups.remove(AppOpsManager.OPSTR_PHONE_CALL_MICROPHONE);
                }
            }
        }
        ArrayMap<String, Map<String, String>> subAttributionLabelsMap = new ArrayMap<>();
        int permGroupNum = 0;
        while (permGroupNum < usedPermGroups.size()) {
            boolean isPhone = false;
            String permGroup = usedPermGroups.get(permGroupNum);
            ArrayMap<OpUsage, CharSequence> usagesWithLabels = permissionUsageHelper.getUniqueUsagesWithLabels(permGroup, rawUsages.get(permGroup));
            permissionUsageHelper.updateSubattributionLabelsMap(rawUsages.get(permGroup), subAttributionLabelsMap);
            if (permGroup.equals(str2)) {
                isPhone = true;
                permGroup = Manifest.permission_group.MICROPHONE;
            } else if (permGroup.equals(str)) {
                isPhone = true;
                permGroup = Manifest.permission_group.CAMERA;
            }
            int usageNum2 = 0;
            while (usageNum2 < usagesWithLabels.size()) {
                OpUsage usage = usagesWithLabels.keyAt(usageNum2);
                String attributionLabel = subAttributionLabelsMap.getOrDefault(usage.packageName, new ArrayMap()).getOrDefault(usage.attributionTag, null);
                usages.add(new PermissionGroupUsage(usage.packageName, usage.uid, usage.lastAccessTime, permGroup, usage.isRunning, isPhone, usage.attributionTag, attributionLabel, usagesWithLabels.valueAt(usageNum2)));
                usageNum2++;
                ops = ops;
                rawUsages = rawUsages;
                usedPermGroups = usedPermGroups;
                audioManager = audioManager;
                str = str;
                hasPhoneCall = hasPhoneCall;
                str2 = str2;
            }
            permGroupNum++;
            permissionUsageHelper = this;
        }
        return usages;
    }

    private void updateSubattributionLabelsMap(List<OpUsage> usages, ArrayMap<String, Map<String, String>> subAttributionLabelsMap) {
        if (usages == null || usages.isEmpty()) {
            return;
        }
        for (OpUsage usage : usages) {
            if (usage.attributionTag != null && !subAttributionLabelsMap.containsKey(usage.packageName)) {
                subAttributionLabelsMap.put(usage.packageName, getSubattributionLabelsForPackage(usage.packageName, usage.uid));
            }
        }
    }

    private ArrayMap<String, String> getSubattributionLabelsForPackage(String packageName, int uid) {
        Attribution[] attributionArr;
        ArrayMap<String, String> attributionLabelMap = new ArrayMap<>();
        UserHandle user = UserHandle.getUserHandleForUid(uid);
        if (!isSubattributionSupported(packageName, uid)) {
            return attributionLabelMap;
        }
        Context userContext = getUserContext(user);
        PackageInfo packageInfo = userContext.getPackageManager().getPackageInfo(packageName, PackageManager.PackageInfoFlags.m189of(2147487744L));
        Context pkgContext = userContext.createPackageContext(packageInfo.packageName, 0);
        for (Attribution attribution : packageInfo.attributions) {
            try {
                String resourceForLabel = pkgContext.getString(attribution.getLabel());
                attributionLabelMap.put(attribution.getTag(), resourceForLabel);
            } catch (Resources.NotFoundException e) {
            }
        }
        return attributionLabelMap;
    }

    private boolean isSubattributionSupported(String packageName, int uid) {
        try {
            if (!isLocationProvider(packageName)) {
                return false;
            }
            PackageManager userPkgManager = getUserContext(UserHandle.getUserHandleForUid(uid)).getPackageManager();
            ApplicationInfo appInfo = userPkgManager.getApplicationInfoAsUser(packageName, PackageManager.ApplicationInfoFlags.m191of(0L), UserHandle.getUserId(uid));
            if (appInfo == null) {
                return false;
            }
            return appInfo.areAttributionsUserVisible();
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    private boolean isLocationProvider(String packageName) {
        return ((LocationManager) Objects.requireNonNull((LocationManager) this.mContext.getSystemService(LocationManager.class))).isProviderPackage(packageName);
    }

    private Map<String, List<OpUsage>> getOpUsages(List<String> opNames) {
        long lastAccessTime;
        OpUsage proxyUsage;
        AppOpsManager.OpEntry opEntry;
        int opEntryNum;
        int numOpEntries;
        int opFlags;
        long now;
        try {
            List<AppOpsManager.PackageOps> ops = this.mAppOpsManager.getPackagesForOps((String[]) opNames.toArray(new String[opNames.size()]));
            long now2 = System.currentTimeMillis();
            long recentThreshold = getRecentThreshold(Long.valueOf(now2));
            long runningThreshold = getRunningThreshold(Long.valueOf(now2));
            int opFlags2 = 13;
            Map<String, Map<Integer, OpUsage>> usages = new ArrayMap<>();
            int numPkgOps = ops.size();
            for (int pkgOpNum = 0; pkgOpNum < numPkgOps; pkgOpNum++) {
                AppOpsManager.PackageOps pkgOps = ops.get(pkgOpNum);
                int uid = pkgOps.getUid();
                UserHandle.getUserHandleForUid(uid);
                String packageName = pkgOps.getPackageName();
                int numOpEntries2 = pkgOps.getOps().size();
                int opEntryNum2 = 0;
                while (opEntryNum2 < numOpEntries2) {
                    List<AppOpsManager.PackageOps> ops2 = ops;
                    AppOpsManager.OpEntry opEntry2 = pkgOps.getOps().get(opEntryNum2);
                    String op = opEntry2.getOpStr();
                    List<String> attributionTags = new ArrayList<>(opEntry2.getAttributedOpEntries().keySet());
                    int numAttrEntries = opEntry2.getAttributedOpEntries().size();
                    int numPkgOps2 = numPkgOps;
                    int numPkgOps3 = 0;
                    while (numPkgOps3 < numAttrEntries) {
                        List<String> attributionTags2 = attributionTags;
                        String attributionTag = attributionTags.get(numPkgOps3);
                        int numAttrEntries2 = numAttrEntries;
                        AppOpsManager.AttributedOpEntry attrOpEntry = opEntry2.getAttributedOpEntries().get(attributionTag);
                        long lastAccessTime2 = attrOpEntry.getLastAccessTime(opFlags2);
                        if (!attrOpEntry.isRunning()) {
                            lastAccessTime = lastAccessTime2;
                        } else {
                            long lastAccessTime3 = now2;
                            lastAccessTime = lastAccessTime3;
                        }
                        if (lastAccessTime < recentThreshold && !attrOpEntry.isRunning()) {
                            opEntry = opEntry2;
                            opFlags = opFlags2;
                            now = now2;
                            opEntryNum = opEntryNum2;
                            numOpEntries = numOpEntries2;
                        } else {
                            boolean isRunning = attrOpEntry.isRunning() || lastAccessTime >= runningThreshold;
                            AppOpsManager.OpEventProxyInfo proxy = attrOpEntry.getLastProxyInfo(opFlags2);
                            if (proxy != null && proxy.getPackageName() != null) {
                                OpUsage proxyUsage2 = new OpUsage(proxy.getPackageName(), proxy.getAttributionTag(), op, proxy.getUid(), lastAccessTime, isRunning, null);
                                proxyUsage = proxyUsage2;
                            } else {
                                proxyUsage = null;
                            }
                            opEntry = opEntry2;
                            String permGroupName = getGroupForOp(op);
                            opEntryNum = opEntryNum2;
                            numOpEntries = numOpEntries2;
                            OpUsage usage = new OpUsage(packageName, attributionTag, op, uid, lastAccessTime, isRunning, proxyUsage);
                            Integer packageAttr = Integer.valueOf(usage.getPackageIdHash());
                            if (!usages.containsKey(permGroupName)) {
                                ArrayMap<Integer, OpUsage> map = new ArrayMap<>();
                                map.put(packageAttr, usage);
                                usages.put(permGroupName, map);
                                opFlags = opFlags2;
                                now = now2;
                            } else {
                                Map<Integer, OpUsage> permGroupUsages = usages.get(permGroupName);
                                if (!permGroupUsages.containsKey(packageAttr)) {
                                    permGroupUsages.put(packageAttr, usage);
                                    opFlags = opFlags2;
                                    now = now2;
                                } else {
                                    opFlags = opFlags2;
                                    now = now2;
                                    if (usage.lastAccessTime > permGroupUsages.get(packageAttr).lastAccessTime) {
                                        permGroupUsages.put(packageAttr, usage);
                                    }
                                }
                            }
                        }
                        numPkgOps3++;
                        opFlags2 = opFlags;
                        now2 = now;
                        opEntry2 = opEntry;
                        opEntryNum2 = opEntryNum;
                        numOpEntries2 = numOpEntries;
                        attributionTags = attributionTags2;
                        numAttrEntries = numAttrEntries2;
                    }
                    opEntryNum2++;
                    ops = ops2;
                    numPkgOps = numPkgOps2;
                }
            }
            Map<String, List<OpUsage>> flattenedUsages = new ArrayMap<>();
            List<String> permGroups = new ArrayList<>(usages.keySet());
            for (int i = 0; i < permGroups.size(); i++) {
                String permGroupName2 = permGroups.get(i);
                flattenedUsages.put(permGroupName2, new ArrayList<>(usages.get(permGroupName2).values()));
            }
            return flattenedUsages;
        } catch (NullPointerException e) {
            return Collections.emptyMap();
        }
    }

    private CharSequence formatLabelList(List<CharSequence> labels) {
        return ListFormatter.getInstance().format(labels);
    }

    private ArrayMap<OpUsage, CharSequence> getUniqueUsagesWithLabels(String permGroup, List<OpUsage> usages) {
        ArrayMap<OpUsage, CharSequence> usagesAndLabels;
        OpUsage currentUsage;
        ArrayMap<OpUsage, CharSequence> usagesAndLabels2;
        PermissionUsageHelper permissionUsageHelper = this;
        String str = permGroup;
        List<OpUsage> list = usages;
        ArrayMap<OpUsage, CharSequence> usagesAndLabels3 = new ArrayMap<>();
        if (list != null) {
            if (usages.isEmpty()) {
                return usagesAndLabels3;
            }
            ArrayMap<Integer, OpUsage> allUsages = new ArrayMap<>();
            ArrayMap<Integer, OpUsage> mostRecentUsages = new ArrayMap<>();
            ArraySet<Integer> proxyPackages = new ArraySet<>();
            ArrayMap<OpUsage, ArrayList<CharSequence>> proxyLabels = new ArrayMap<>();
            ArrayMap<Integer, OpUsage> proxies = new ArrayMap<>();
            for (int i = 0; i < usages.size(); i++) {
                OpUsage usage = list.get(i);
                allUsages.put(Integer.valueOf(usage.getPackageIdHash()), usage);
                if (usage.proxy != null) {
                    proxies.put(Integer.valueOf(usage.proxy.getPackageIdHash()), usage);
                }
            }
            int usageNum = 0;
            while (usageNum < usages.size()) {
                OpUsage usage2 = list.get(usageNum);
                if (usage2 == null) {
                    usagesAndLabels2 = usagesAndLabels3;
                } else {
                    int usageAttr = usage2.getPackageIdHash();
                    if (!proxies.containsKey(Integer.valueOf(usageAttr)) && usage2.proxy != null && !Manifest.permission_group.MICROPHONE.equals(str)) {
                        proxyLabels.put(usage2, new ArrayList<>());
                        proxyPackages.add(Integer.valueOf(usage2.getPackageIdHash()));
                    }
                    int usageId = usage2.getPackageIdHash();
                    OpUsage lastMostRecent = mostRecentUsages.get(Integer.valueOf(usageId));
                    if (!permissionUsageHelper.shouldShowPackage(usage2.packageName)) {
                        usagesAndLabels2 = usagesAndLabels3;
                    } else {
                        if (lastMostRecent != null) {
                            usagesAndLabels2 = usagesAndLabels3;
                            if (usage2.lastAccessTime <= lastMostRecent.lastAccessTime) {
                            }
                        } else {
                            usagesAndLabels2 = usagesAndLabels3;
                        }
                        mostRecentUsages.put(Integer.valueOf(usageId), usage2);
                    }
                }
                usageNum++;
                list = usages;
                usagesAndLabels3 = usagesAndLabels2;
            }
            ArrayMap<OpUsage, CharSequence> usagesAndLabels4 = usagesAndLabels3;
            int numStart = 0;
            while (true) {
                int i2 = 0;
                if (numStart >= proxyLabels.size()) {
                    break;
                }
                OpUsage start = proxyLabels.keyAt(numStart);
                mostRecentUsages.remove(Integer.valueOf(start.getPackageIdHash()));
                OpUsage currentUsage2 = proxyLabels.keyAt(numStart);
                ArrayList<CharSequence> proxyLabelList = proxyLabels.get(currentUsage2);
                if (currentUsage2 == null) {
                    usagesAndLabels = usagesAndLabels4;
                } else if (proxyLabelList == null) {
                    usagesAndLabels = usagesAndLabels4;
                } else {
                    int iterNum = 0;
                    int maxUsages = allUsages.size();
                    while (currentUsage2.proxy != null) {
                        if (allUsages.containsKey(Integer.valueOf(currentUsage2.proxy.getPackageIdHash()))) {
                            currentUsage = allUsages.get(Integer.valueOf(currentUsage2.proxy.getPackageIdHash()));
                        } else {
                            OpUsage proxy = currentUsage2.proxy;
                            if (!permissionUsageHelper.shouldShowPackage(proxy.packageName)) {
                                break;
                            }
                            maxUsages++;
                            currentUsage = proxy;
                        }
                        if (currentUsage == null || iterNum == maxUsages || currentUsage.getPackageIdHash() == start.getPackageIdHash()) {
                            break;
                        }
                        proxyPackages.add(Integer.valueOf(currentUsage.getPackageIdHash()));
                        if (!currentUsage.packageName.equals(start.packageName) && permissionUsageHelper.shouldShowPackage(currentUsage.packageName)) {
                            try {
                                PackageManager userPkgManager = permissionUsageHelper.getUserContext(currentUsage.getUser()).getPackageManager();
                                ApplicationInfo appInfo = userPkgManager.getApplicationInfo(currentUsage.packageName, i2);
                                CharSequence appLabel = appInfo.loadLabel(userPkgManager);
                                if (!proxyLabelList.contains(appLabel)) {
                                    proxyLabelList.add(appLabel);
                                }
                            } catch (PackageManager.NameNotFoundException e) {
                            }
                        }
                        iterNum++;
                        currentUsage2 = currentUsage;
                        i2 = 0;
                    }
                    if (Manifest.permission_group.MICROPHONE.equals(str)) {
                        usagesAndLabels = usagesAndLabels4;
                    } else {
                        usagesAndLabels = usagesAndLabels4;
                        usagesAndLabels.put(start, proxyLabelList.isEmpty() ? null : permissionUsageHelper.formatLabelList(proxyLabelList));
                    }
                }
                numStart++;
                usagesAndLabels4 = usagesAndLabels;
            }
            ArrayMap<OpUsage, CharSequence> usagesAndLabels5 = usagesAndLabels4;
            synchronized (permissionUsageHelper.mAttributionChains) {
                int i3 = 0;
                while (i3 < permissionUsageHelper.mAttributionChains.size()) {
                    List<AccessChainLink> usageList = permissionUsageHelper.mAttributionChains.valueAt(i3);
                    int lastVisible = usageList.size() - 1;
                    if (!usageList.isEmpty() && usageList.get(lastVisible).isEnd()) {
                        if (usageList.get(0).isStart() && str.equals(getGroupForOp(usageList.get(0).usage.f335op)) && Manifest.permission_group.MICROPHONE.equals(str)) {
                            for (AccessChainLink link : usageList) {
                                proxyPackages.add(Integer.valueOf(link.usage.getPackageIdHash()));
                            }
                            AccessChainLink start2 = usageList.get(0);
                            AccessChainLink lastVisibleLink = usageList.get(lastVisible);
                            int lastVisible2 = lastVisible;
                            while (lastVisible2 > 0 && !permissionUsageHelper.shouldShowPackage(lastVisibleLink.usage.packageName)) {
                                lastVisible2--;
                                lastVisibleLink = usageList.get(lastVisible2);
                            }
                            String proxyLabel = null;
                            if (!lastVisibleLink.usage.packageName.equals(start2.usage.packageName)) {
                                try {
                                    PackageManager userPkgManager2 = permissionUsageHelper.getUserContext(lastVisibleLink.usage.getUser()).getPackageManager();
                                    try {
                                        ApplicationInfo appInfo2 = userPkgManager2.getApplicationInfo(lastVisibleLink.usage.packageName, 0);
                                        proxyLabel = appInfo2.loadLabel(userPkgManager2).toString();
                                    } catch (PackageManager.NameNotFoundException e2) {
                                    }
                                } catch (PackageManager.NameNotFoundException e3) {
                                }
                            }
                            usagesAndLabels5.put(start2.usage, proxyLabel);
                        }
                    }
                    i3++;
                    permissionUsageHelper = this;
                    str = permGroup;
                }
            }
            for (Integer num : mostRecentUsages.keySet()) {
                int packageHash = num.intValue();
                if (!proxyPackages.contains(Integer.valueOf(packageHash))) {
                    usagesAndLabels5.put(mostRecentUsages.get(Integer.valueOf(packageHash)), null);
                }
            }
            return usagesAndLabels5;
        }
        return usagesAndLabels3;
    }

    private boolean shouldShowPackage(String packageName) {
        return PermissionManager.shouldShowPackageForIndicatorCached(this.mContext, packageName);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public static class OpUsage {
        public final String attributionTag;
        public final boolean isRunning;
        public final long lastAccessTime;

        /* renamed from: op */
        public final String f335op;
        public final String packageName;
        public final OpUsage proxy;
        public final int uid;

        OpUsage(String packageName, String attributionTag, String op, int uid, long lastAccessTime, boolean isRunning, OpUsage proxy) {
            this.packageName = packageName;
            this.attributionTag = attributionTag;
            this.f335op = op;
            this.uid = uid;
            this.lastAccessTime = lastAccessTime;
            this.isRunning = isRunning;
            this.proxy = proxy;
        }

        public UserHandle getUser() {
            return UserHandle.getUserHandleForUid(this.uid);
        }

        public int getPackageIdHash() {
            return Objects.hash(this.packageName, Integer.valueOf(this.uid));
        }

        public int hashCode() {
            return Objects.hash(this.packageName, this.attributionTag, this.f335op, Integer.valueOf(this.uid), Long.valueOf(this.lastAccessTime), Boolean.valueOf(this.isRunning));
        }

        public boolean equals(Object obj) {
            if (obj instanceof OpUsage) {
                OpUsage other = (OpUsage) obj;
                return Objects.equals(this.packageName, other.packageName) && Objects.equals(this.attributionTag, other.attributionTag) && Objects.equals(this.f335op, other.f335op) && this.uid == other.uid && this.lastAccessTime == other.lastAccessTime && this.isRunning == other.isRunning;
            }
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public static class AccessChainLink {
        public final int flags;
        public final OpUsage usage;

        AccessChainLink(String op, String packageName, String attributionTag, int uid, int flags) {
            this.usage = new OpUsage(packageName, attributionTag, op, uid, System.currentTimeMillis(), true, null);
            this.flags = flags;
        }

        public boolean isEnd() {
            return (this.flags & 1) != 0;
        }

        public boolean isStart() {
            return (this.flags & 4) != 0;
        }

        public boolean equals(Object obj) {
            if (obj instanceof AccessChainLink) {
                AccessChainLink other = (AccessChainLink) obj;
                return other.flags == this.flags && packageAndOpEquals(other.usage.f335op, other.usage.packageName, other.usage.attributionTag, other.usage.uid);
            }
            return false;
        }

        public boolean packageAndOpEquals(String op, String packageName, String attributionTag, int uid) {
            return Objects.equals(op, this.usage.f335op) && Objects.equals(packageName, this.usage.packageName) && Objects.equals(attributionTag, this.usage.attributionTag) && uid == this.usage.uid;
        }
    }
}
