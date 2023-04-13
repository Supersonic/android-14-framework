package com.android.internal.util.jobs;

import android.app.AppOpsManager;
import android.content.ComponentName;
import android.content.Context;
import android.os.Binder;
import android.os.Handler;
import android.text.TextUtils;
import com.android.server.p011pm.PackageManagerShellCommandDataLoader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Objects;
import java.util.function.Predicate;
/* loaded from: classes.dex */
public final class DumpUtils {
    public static final ComponentName[] CRITICAL_SECTION_COMPONENTS = {new ComponentName("com.android.systemui", "com.android.systemui.SystemUIService")};
    public static final boolean DEBUG = false;
    public static final String TAG = "DumpUtils";

    /* loaded from: classes.dex */
    public interface Dump {
        void dump(PrintWriter printWriter, String str);
    }

    public static /* synthetic */ boolean lambda$filterRecord$0(ComponentName.WithComponentName withComponentName) {
        return false;
    }

    public static void dumpAsync(Handler handler, final Dump dump, PrintWriter printWriter, final String str, long j) {
        final StringWriter stringWriter = new StringWriter();
        if (handler.runWithScissors(new Runnable() { // from class: com.android.internal.util.jobs.DumpUtils.1
            @Override // java.lang.Runnable
            public void run() {
                FastPrintWriter fastPrintWriter = new FastPrintWriter(stringWriter);
                dump.dump(fastPrintWriter, str);
                fastPrintWriter.close();
            }
        }, j)) {
            printWriter.print(stringWriter.toString());
        } else {
            printWriter.println("... timed out");
        }
    }

    public static void logMessage(PrintWriter printWriter, String str) {
        printWriter.println(str);
    }

    public static boolean checkDumpPermission(Context context, String str, PrintWriter printWriter) {
        if (context.checkCallingOrSelfPermission("android.permission.DUMP") != 0) {
            logMessage(printWriter, "Permission Denial: can't dump " + str + " from from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid() + " due to missing android.permission.DUMP permission");
            return false;
        }
        return true;
    }

    public static boolean checkUsageStatsPermission(Context context, String str, PrintWriter printWriter) {
        int callingUid = Binder.getCallingUid();
        if (callingUid == 0 || callingUid == 1000 || callingUid == 1067 || callingUid == 2000) {
            return true;
        }
        if (context.checkCallingOrSelfPermission("android.permission.PACKAGE_USAGE_STATS") != 0) {
            logMessage(printWriter, "Permission Denial: can't dump " + str + " from from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid() + " due to missing android.permission.PACKAGE_USAGE_STATS permission");
            return false;
        }
        AppOpsManager appOpsManager = (AppOpsManager) context.getSystemService(AppOpsManager.class);
        String[] packagesForUid = context.getPackageManager().getPackagesForUid(callingUid);
        if (packagesForUid != null) {
            for (String str2 : packagesForUid) {
                int noteOpNoThrow = appOpsManager.noteOpNoThrow(43, callingUid, str2);
                if (noteOpNoThrow == 0 || noteOpNoThrow == 3) {
                    return true;
                }
            }
        }
        logMessage(printWriter, "Permission Denial: can't dump " + str + " from from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid() + " due to android:get_usage_stats app-op not allowed");
        return false;
    }

    public static boolean checkDumpAndUsageStatsPermission(Context context, String str, PrintWriter printWriter) {
        return checkDumpPermission(context, str, printWriter) && checkUsageStatsPermission(context, str, printWriter);
    }

    public static boolean isPlatformPackage(String str) {
        return str != null && (str.equals(PackageManagerShellCommandDataLoader.PACKAGE) || str.startsWith("android.") || str.startsWith("com.android."));
    }

    public static boolean isPlatformPackage(ComponentName componentName) {
        return componentName != null && isPlatformPackage(componentName.getPackageName());
    }

    public static boolean isPlatformPackage(ComponentName.WithComponentName withComponentName) {
        return withComponentName != null && isPlatformPackage(withComponentName.getComponentName());
    }

    public static boolean isNonPlatformPackage(String str) {
        return (str == null || isPlatformPackage(str)) ? false : true;
    }

    public static boolean isNonPlatformPackage(ComponentName componentName) {
        return componentName != null && isNonPlatformPackage(componentName.getPackageName());
    }

    public static boolean isNonPlatformPackage(ComponentName.WithComponentName withComponentName) {
        return (withComponentName == null || isPlatformPackage(withComponentName.getComponentName())) ? false : true;
    }

    public static boolean isCriticalPackage(ComponentName componentName) {
        if (componentName == null) {
            return false;
        }
        int i = 0;
        while (true) {
            ComponentName[] componentNameArr = CRITICAL_SECTION_COMPONENTS;
            if (i >= componentNameArr.length) {
                return false;
            }
            if (componentName.equals(componentNameArr[i])) {
                return true;
            }
            i++;
        }
    }

    public static boolean isPlatformCriticalPackage(ComponentName.WithComponentName withComponentName) {
        return withComponentName != null && isPlatformPackage(withComponentName.getComponentName()) && isCriticalPackage(withComponentName.getComponentName());
    }

    public static boolean isPlatformNonCriticalPackage(ComponentName.WithComponentName withComponentName) {
        return (withComponentName == null || !isPlatformPackage(withComponentName.getComponentName()) || isCriticalPackage(withComponentName.getComponentName())) ? false : true;
    }

    public static <TRec extends ComponentName.WithComponentName> Predicate<TRec> filterRecord(final String str) {
        if (TextUtils.isEmpty(str)) {
            return new Predicate() { // from class: com.android.internal.util.jobs.DumpUtils$$ExternalSyntheticLambda0
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean lambda$filterRecord$0;
                    lambda$filterRecord$0 = DumpUtils.lambda$filterRecord$0((ComponentName.WithComponentName) obj);
                    return lambda$filterRecord$0;
                }
            };
        }
        if ("all".equals(str)) {
            return new Predicate() { // from class: com.android.internal.util.jobs.DumpUtils$$ExternalSyntheticLambda1
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    return Objects.nonNull((ComponentName.WithComponentName) obj);
                }
            };
        }
        if ("all-platform".equals(str)) {
            return new Predicate() { // from class: com.android.internal.util.jobs.DumpUtils$$ExternalSyntheticLambda2
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    return DumpUtils.isPlatformPackage((ComponentName.WithComponentName) obj);
                }
            };
        }
        if ("all-non-platform".equals(str)) {
            return new Predicate() { // from class: com.android.internal.util.jobs.DumpUtils$$ExternalSyntheticLambda3
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    return DumpUtils.isNonPlatformPackage((ComponentName.WithComponentName) obj);
                }
            };
        }
        if ("all-platform-critical".equals(str)) {
            return new Predicate() { // from class: com.android.internal.util.jobs.DumpUtils$$ExternalSyntheticLambda4
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    return DumpUtils.isPlatformCriticalPackage((ComponentName.WithComponentName) obj);
                }
            };
        }
        if ("all-platform-non-critical".equals(str)) {
            return new Predicate() { // from class: com.android.internal.util.jobs.DumpUtils$$ExternalSyntheticLambda5
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    return DumpUtils.isPlatformNonCriticalPackage((ComponentName.WithComponentName) obj);
                }
            };
        }
        final ComponentName unflattenFromString = ComponentName.unflattenFromString(str);
        if (unflattenFromString != null) {
            return new Predicate() { // from class: com.android.internal.util.jobs.DumpUtils$$ExternalSyntheticLambda6
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    boolean lambda$filterRecord$1;
                    lambda$filterRecord$1 = DumpUtils.lambda$filterRecord$1(unflattenFromString, (ComponentName.WithComponentName) obj);
                    return lambda$filterRecord$1;
                }
            };
        }
        final int parseIntWithBase = ParseUtils.parseIntWithBase(str, 16, -1);
        return new Predicate() { // from class: com.android.internal.util.jobs.DumpUtils$$ExternalSyntheticLambda7
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                boolean lambda$filterRecord$2;
                lambda$filterRecord$2 = DumpUtils.lambda$filterRecord$2(parseIntWithBase, str, (ComponentName.WithComponentName) obj);
                return lambda$filterRecord$2;
            }
        };
    }

    public static /* synthetic */ boolean lambda$filterRecord$1(ComponentName componentName, ComponentName.WithComponentName withComponentName) {
        return withComponentName != null && componentName.equals(withComponentName.getComponentName());
    }

    public static /* synthetic */ boolean lambda$filterRecord$2(int i, String str, ComponentName.WithComponentName withComponentName) {
        return (i != -1 && System.identityHashCode(withComponentName) == i) || withComponentName.getComponentName().flattenToString().toLowerCase().contains(str.toLowerCase());
    }
}
