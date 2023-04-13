package com.android.server.p011pm;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.ActivityInfo;
import android.content.pm.AuxiliaryResolveInfo;
import android.content.pm.InstantAppIntentFilter;
import android.content.pm.InstantAppRequest;
import android.content.pm.InstantAppRequestInfo;
import android.content.pm.InstantAppResolveInfo;
import android.metrics.LogMaker;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.UserHandle;
import android.p005os.IInstalld;
import android.text.TextUtils;
import android.util.Log;
import android.util.Slog;
import com.android.internal.logging.MetricsLogger;
import com.android.server.p011pm.InstantAppResolverConnection;
import com.android.server.p011pm.resolution.ComponentResolver;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
/* renamed from: com.android.server.pm.InstantAppResolver */
/* loaded from: classes2.dex */
public abstract class InstantAppResolver {
    public static final boolean DEBUG_INSTANT = Build.IS_DEBUGGABLE;
    public static MetricsLogger sMetricsLogger;

    public static MetricsLogger getLogger() {
        if (sMetricsLogger == null) {
            sMetricsLogger = new MetricsLogger();
        }
        return sMetricsLogger;
    }

    public static Intent sanitizeIntent(Intent intent) {
        Intent intent2 = new Intent(intent.getAction());
        Set<String> categories = intent.getCategories();
        if (categories != null) {
            for (String str : categories) {
                intent2.addCategory(str);
            }
        }
        intent2.setDataAndType(intent.getData() == null ? null : Uri.fromParts(intent.getScheme(), "", ""), intent.getType());
        intent2.addFlags(intent.getFlags());
        intent2.setPackage(intent.getPackage());
        return intent2;
    }

    public static InstantAppResolveInfo.InstantAppDigest parseDigest(Intent intent) {
        if (intent.getData() != null && !TextUtils.isEmpty(intent.getData().getHost())) {
            return new InstantAppResolveInfo.InstantAppDigest(intent.getData().getHost(), 5);
        }
        return InstantAppResolveInfo.InstantAppDigest.UNDEFINED;
    }

    /* JADX WARN: Removed duplicated region for block: B:33:0x008c  */
    /* JADX WARN: Removed duplicated region for block: B:34:0x00a6  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public static AuxiliaryResolveInfo doInstantAppResolutionPhaseOne(Computer computer, UserManagerService userManagerService, InstantAppResolverConnection instantAppResolverConnection, InstantAppRequest instantAppRequest) {
        Intent intent;
        String str;
        int i;
        int i2;
        long currentTimeMillis = System.currentTimeMillis();
        String str2 = instantAppRequest.token;
        if (DEBUG_INSTANT) {
            Log.d("PackageManager", "[" + str2 + "] Phase1; resolving");
        }
        Intent intent2 = instantAppRequest.origIntent;
        AuxiliaryResolveInfo auxiliaryResolveInfo = null;
        try {
            List<InstantAppResolveInfo> instantAppResolveInfoList = instantAppResolverConnection.getInstantAppResolveInfoList(buildRequestInfo(instantAppRequest));
            if (instantAppResolveInfoList == null || instantAppResolveInfoList.size() <= 0) {
                intent = intent2;
                str = "PackageManager";
                i = 2;
            } else {
                str = "PackageManager";
                i = 2;
                intent = intent2;
                try {
                    auxiliaryResolveInfo = filterInstantAppIntent(computer, userManagerService, instantAppResolveInfoList, intent2, instantAppRequest.resolvedType, instantAppRequest.userId, intent2.getPackage(), str2, instantAppRequest.hostDigestPrefixSecure);
                } catch (InstantAppResolverConnection.ConnectionException e) {
                    e = e;
                    int i3 = e.failure;
                    i2 = 1;
                    if (i3 == 1) {
                        i2 = i;
                    } else if (i3 == i) {
                        i2 = 3;
                    }
                    if (instantAppRequest.resolveForStart) {
                        logMetrics(899, currentTimeMillis, str2, i2);
                    }
                    if (DEBUG_INSTANT) {
                        if (i2 != i) {
                        }
                    }
                    if (auxiliaryResolveInfo == null) {
                    }
                }
            }
            i2 = 0;
        } catch (InstantAppResolverConnection.ConnectionException e2) {
            e = e2;
            intent = intent2;
            str = "PackageManager";
            i = 2;
        }
        if (instantAppRequest.resolveForStart && i2 == 0) {
            logMetrics(899, currentTimeMillis, str2, i2);
        }
        if (DEBUG_INSTANT && auxiliaryResolveInfo == null) {
            if (i2 != i) {
                Log.d(str, "[" + str2 + "] Phase1; bind timed out");
            } else {
                String str3 = str;
                if (i2 == 3) {
                    Log.d(str3, "[" + str2 + "] Phase1; call timed out");
                } else if (i2 != 0) {
                    Log.d(str3, "[" + str2 + "] Phase1; service connection error");
                } else {
                    Log.d(str3, "[" + str2 + "] Phase1; No results matched");
                }
            }
        }
        return (auxiliaryResolveInfo == null || (intent.getFlags() & IInstalld.FLAG_FREE_CACHE_DEFY_TARGET_FREE_BYTES) == 0) ? auxiliaryResolveInfo : new AuxiliaryResolveInfo(str2, false, createFailureIntent(intent, str2), (List) null, instantAppRequest.hostDigestPrefixSecure);
    }

    public static void doInstantAppResolutionPhaseTwo(final Context context, final Computer computer, final UserManagerService userManagerService, InstantAppResolverConnection instantAppResolverConnection, final InstantAppRequest instantAppRequest, final ActivityInfo activityInfo, Handler handler) {
        long currentTimeMillis = System.currentTimeMillis();
        final String str = instantAppRequest.token;
        if (DEBUG_INSTANT) {
            Log.d("PackageManager", "[" + str + "] Phase2; resolving");
        }
        final Intent intent = instantAppRequest.origIntent;
        final Intent sanitizeIntent = sanitizeIntent(intent);
        try {
            instantAppResolverConnection.getInstantAppIntentFilterList(buildRequestInfo(instantAppRequest), new InstantAppResolverConnection.PhaseTwoCallback() { // from class: com.android.server.pm.InstantAppResolver.1
                @Override // com.android.server.p011pm.InstantAppResolverConnection.PhaseTwoCallback
                public void onPhaseTwoResolved(List<InstantAppResolveInfo> list, long j) {
                    Intent intent2 = null;
                    if (list != null && list.size() > 0) {
                        Computer computer2 = Computer.this;
                        UserManagerService userManagerService2 = userManagerService;
                        Intent intent3 = intent;
                        AuxiliaryResolveInfo filterInstantAppIntent = InstantAppResolver.filterInstantAppIntent(computer2, userManagerService2, list, intent3, null, 0, intent3.getPackage(), str, instantAppRequest.hostDigestPrefixSecure);
                        if (filterInstantAppIntent != null) {
                            intent2 = filterInstantAppIntent.failureIntent;
                        }
                    }
                    Intent intent4 = intent2;
                    InstantAppRequest instantAppRequest2 = instantAppRequest;
                    Intent intent5 = instantAppRequest2.origIntent;
                    Intent intent6 = sanitizeIntent;
                    String str2 = instantAppRequest2.callingPackage;
                    String str3 = instantAppRequest2.callingFeatureId;
                    Bundle bundle = instantAppRequest2.verificationBundle;
                    String str4 = instantAppRequest2.resolvedType;
                    int i = instantAppRequest2.userId;
                    AuxiliaryResolveInfo auxiliaryResolveInfo = instantAppRequest2.responseObj;
                    Intent buildEphemeralInstallerIntent = InstantAppResolver.buildEphemeralInstallerIntent(intent5, intent6, intent4, str2, str3, bundle, str4, i, auxiliaryResolveInfo.installFailureActivity, str, false, auxiliaryResolveInfo.filters);
                    ActivityInfo activityInfo2 = activityInfo;
                    buildEphemeralInstallerIntent.setComponent(new ComponentName(activityInfo2.packageName, activityInfo2.name));
                    InstantAppResolver.logMetrics(900, j, str, instantAppRequest.responseObj.filters != null ? 0 : 1);
                    context.startActivity(buildEphemeralInstallerIntent);
                }
            }, handler, currentTimeMillis);
        } catch (InstantAppResolverConnection.ConnectionException e) {
            int i = e.failure == 1 ? 2 : 1;
            logMetrics(900, currentTimeMillis, str, i);
            if (DEBUG_INSTANT) {
                if (i == 2) {
                    Log.d("PackageManager", "[" + str + "] Phase2; bind timed out");
                    return;
                }
                Log.d("PackageManager", "[" + str + "] Phase2; service connection error");
            }
        }
    }

    public static Intent buildEphemeralInstallerIntent(Intent intent, Intent intent2, Intent intent3, String str, String str2, Bundle bundle, String str3, int i, ComponentName componentName, String str4, boolean z, List<AuxiliaryResolveInfo.AuxiliaryFilter> list) {
        Intent intent4;
        int flags = intent.getFlags();
        Intent intent5 = new Intent();
        intent5.setFlags(flags | 1073741824 | 8388608);
        if (str4 != null) {
            intent5.putExtra("android.intent.extra.INSTANT_APP_TOKEN", str4);
        }
        if (intent.getData() != null) {
            intent5.putExtra("android.intent.extra.INSTANT_APP_HOSTNAME", intent.getData().getHost());
        }
        intent5.putExtra("android.intent.extra.INSTANT_APP_ACTION", intent.getAction());
        intent5.putExtra("android.intent.extra.INTENT", intent2);
        if (z) {
            intent5.setAction("android.intent.action.RESOLVE_INSTANT_APP_PACKAGE");
        } else {
            if (intent3 != null || componentName != null) {
                if (componentName != null) {
                    try {
                        intent4 = new Intent();
                        intent4.setComponent(componentName);
                        if (list != null && list.size() == 1) {
                            intent4.putExtra("android.intent.extra.SPLIT_NAME", list.get(0).splitName);
                        }
                        intent4.putExtra("android.intent.extra.INTENT", intent);
                    } catch (RemoteException unused) {
                    }
                } else {
                    intent4 = intent3;
                }
                intent5.putExtra("android.intent.extra.INSTANT_APP_FAILURE", new IntentSender(ActivityManager.getService().getIntentSenderWithFeature(2, str, str2, (IBinder) null, (String) null, 1, new Intent[]{intent4}, new String[]{str3}, 1409286144, (Bundle) null, i)));
            }
            Intent intent6 = new Intent(intent);
            intent6.setLaunchToken(str4);
            try {
                intent5.putExtra("android.intent.extra.INSTANT_APP_SUCCESS", new IntentSender(ActivityManager.getService().getIntentSenderWithFeature(2, str, str2, (IBinder) null, (String) null, 0, new Intent[]{intent6}, new String[]{str3}, 1409286144, (Bundle) null, i)));
            } catch (RemoteException unused2) {
            }
            if (bundle != null) {
                intent5.putExtra("android.intent.extra.VERIFICATION_BUNDLE", bundle);
            }
            intent5.putExtra("android.intent.extra.CALLING_PACKAGE", str);
            if (list != null) {
                Bundle[] bundleArr = new Bundle[list.size()];
                int size = list.size();
                for (int i2 = 0; i2 < size; i2++) {
                    Bundle bundle2 = new Bundle();
                    AuxiliaryResolveInfo.AuxiliaryFilter auxiliaryFilter = list.get(i2);
                    InstantAppResolveInfo instantAppResolveInfo = auxiliaryFilter.resolveInfo;
                    bundle2.putBoolean("android.intent.extra.UNKNOWN_INSTANT_APP", instantAppResolveInfo != null && instantAppResolveInfo.shouldLetInstallerDecide());
                    bundle2.putString("android.intent.extra.PACKAGE_NAME", auxiliaryFilter.packageName);
                    bundle2.putString("android.intent.extra.SPLIT_NAME", auxiliaryFilter.splitName);
                    bundle2.putLong("android.intent.extra.LONG_VERSION_CODE", auxiliaryFilter.versionCode);
                    bundle2.putBundle("android.intent.extra.INSTANT_APP_EXTRAS", auxiliaryFilter.extras);
                    bundleArr[i2] = bundle2;
                    if (i2 == 0) {
                        intent5.putExtras(bundle2);
                        intent5.putExtra("android.intent.extra.VERSION_CODE", (int) auxiliaryFilter.versionCode);
                    }
                }
                intent5.putExtra("android.intent.extra.INSTANT_APP_BUNDLES", bundleArr);
            }
            intent5.setAction("android.intent.action.INSTALL_INSTANT_APP_PACKAGE");
        }
        return intent5;
    }

    public static InstantAppRequestInfo buildRequestInfo(InstantAppRequest instantAppRequest) {
        return new InstantAppRequestInfo(sanitizeIntent(instantAppRequest.origIntent), instantAppRequest.hostDigestPrefixSecure, UserHandle.of(instantAppRequest.userId), instantAppRequest.isRequesterInstantApp, instantAppRequest.token);
    }

    public static AuxiliaryResolveInfo filterInstantAppIntent(Computer computer, UserManagerService userManagerService, List<InstantAppResolveInfo> list, Intent intent, String str, int i, String str2, String str3, int[] iArr) {
        boolean z;
        InstantAppResolveInfo.InstantAppDigest parseDigest = parseDigest(intent);
        int[] digestPrefix = parseDigest.getDigestPrefix();
        byte[][] digestBytes = parseDigest.getDigestBytes();
        boolean z2 = intent.isWebIntent() || (digestPrefix.length > 0 && (intent.getFlags() & IInstalld.FLAG_FREE_CACHE_DEFY_TARGET_FREE_BYTES) == 0);
        boolean z3 = false;
        ArrayList arrayList = null;
        for (InstantAppResolveInfo instantAppResolveInfo : list) {
            if (z2 && instantAppResolveInfo.shouldLetInstallerDecide()) {
                Slog.d("PackageManager", "InstantAppResolveInfo with mShouldLetInstallerDecide=true when digest required; ignoring");
            } else {
                byte[] digestBytes2 = instantAppResolveInfo.getDigestBytes();
                if (digestPrefix.length > 0 && (z2 || digestBytes2.length > 0)) {
                    int length = digestPrefix.length - 1;
                    while (true) {
                        if (length < 0) {
                            z = false;
                            break;
                        } else if (Arrays.equals(digestBytes[length], digestBytes2)) {
                            z = true;
                            break;
                        } else {
                            length--;
                        }
                    }
                    if (!z) {
                    }
                }
                List<AuxiliaryResolveInfo.AuxiliaryFilter> computeResolveFilters = computeResolveFilters(computer, userManagerService, intent, str, i, str2, str3, instantAppResolveInfo);
                if (computeResolveFilters != null) {
                    if (computeResolveFilters.isEmpty()) {
                        z3 = true;
                    }
                    if (arrayList == null) {
                        arrayList = new ArrayList(computeResolveFilters);
                    } else {
                        arrayList.addAll(computeResolveFilters);
                    }
                }
            }
        }
        if (arrayList == null || arrayList.isEmpty()) {
            return null;
        }
        return new AuxiliaryResolveInfo(str3, z3, createFailureIntent(intent, str3), arrayList, iArr);
    }

    public static Intent createFailureIntent(Intent intent, String str) {
        Intent intent2 = new Intent(intent);
        intent2.setFlags(intent2.getFlags() | 512);
        intent2.setFlags(intent2.getFlags() & (-2049));
        intent2.setLaunchToken(str);
        return intent2;
    }

    public static List<AuxiliaryResolveInfo.AuxiliaryFilter> computeResolveFilters(Computer computer, UserManagerService userManagerService, Intent intent, String str, int i, String str2, String str3, InstantAppResolveInfo instantAppResolveInfo) {
        if (instantAppResolveInfo.shouldLetInstallerDecide()) {
            return Collections.singletonList(new AuxiliaryResolveInfo.AuxiliaryFilter(instantAppResolveInfo, (String) null, instantAppResolveInfo.getExtras()));
        }
        if (str2 == null || str2.equals(instantAppResolveInfo.getPackageName())) {
            List intentFilters = instantAppResolveInfo.getIntentFilters();
            if (intentFilters == null || intentFilters.isEmpty()) {
                if (intent.isWebIntent()) {
                    return null;
                }
                if (DEBUG_INSTANT) {
                    Log.d("PackageManager", "No app filters; go to phase 2");
                }
                return Collections.emptyList();
            }
            ComponentResolver.InstantAppIntentResolver instantAppIntentResolver = new ComponentResolver.InstantAppIntentResolver(userManagerService);
            for (int size = intentFilters.size() - 1; size >= 0; size--) {
                InstantAppIntentFilter instantAppIntentFilter = (InstantAppIntentFilter) intentFilters.get(size);
                List filters = instantAppIntentFilter.getFilters();
                if (filters != null && !filters.isEmpty()) {
                    for (int size2 = filters.size() - 1; size2 >= 0; size2--) {
                        IntentFilter intentFilter = (IntentFilter) filters.get(size2);
                        Iterator<IntentFilter.AuthorityEntry> authoritiesIterator = intentFilter.authoritiesIterator();
                        if ((authoritiesIterator != null && authoritiesIterator.hasNext()) || ((!intentFilter.hasDataScheme("http") && !intentFilter.hasDataScheme("https")) || !intentFilter.hasAction("android.intent.action.VIEW") || !intentFilter.hasCategory("android.intent.category.BROWSABLE"))) {
                            instantAppIntentResolver.addFilter(computer, new AuxiliaryResolveInfo.AuxiliaryFilter(intentFilter, instantAppResolveInfo, instantAppIntentFilter.getSplitName(), instantAppResolveInfo.getExtras()));
                        }
                    }
                }
            }
            List<AuxiliaryResolveInfo.AuxiliaryFilter> queryIntent = instantAppIntentResolver.queryIntent(computer, intent, str, false, i);
            if (!queryIntent.isEmpty()) {
                if (DEBUG_INSTANT) {
                    Log.d("PackageManager", "[" + str3 + "] Found match(es); " + queryIntent);
                }
                return queryIntent;
            }
            if (DEBUG_INSTANT) {
                Log.d("PackageManager", "[" + str3 + "] No matches found package: " + instantAppResolveInfo.getPackageName() + ", versionCode: " + instantAppResolveInfo.getVersionCode());
            }
            return null;
        }
        return null;
    }

    public static void logMetrics(int i, long j, String str, int i2) {
        getLogger().write(new LogMaker(i).setType(4).addTaggedData(901, new Long(System.currentTimeMillis() - j)).addTaggedData(903, str).addTaggedData(902, new Integer(i2)));
    }
}
