package com.android.server.p011pm;

import android.app.ActivityManager;
import android.app.ActivityManagerInternal;
import android.app.ActivityThread;
import android.app.usage.NetworkStats;
import android.app.usage.NetworkStatsManager;
import android.content.Context;
import android.content.p000pm.PackageManagerInternal;
import android.content.pm.IPackageManager;
import android.media.AudioFocusInfo;
import android.media.AudioManager;
import android.media.AudioRecordingConfiguration;
import android.media.IAudioService;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.telecom.TelecomManager;
import android.text.TextUtils;
import android.util.ArraySet;
import com.android.internal.util.ArrayUtils;
import com.android.server.LocalServices;
import com.android.server.p011pm.parsing.pkg.AndroidPackageInternal;
import com.android.server.p011pm.pkg.AndroidPackage;
import com.android.server.p011pm.pkg.PackageStateInternal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
/* renamed from: com.android.server.pm.AppStateHelper */
/* loaded from: classes2.dex */
public class AppStateHelper {
    public static final long ACTIVE_NETWORK_DURATION_MILLIS = TimeUnit.MINUTES.toMillis(10);
    public final Context mContext;

    public AppStateHelper(Context context) {
        this.mContext = context;
    }

    public static boolean isPackageLoaded(ActivityManager.RunningAppProcessInfo runningAppProcessInfo, String str) {
        return ArrayUtils.contains(runningAppProcessInfo.pkgList, str) || ArrayUtils.contains(runningAppProcessInfo.pkgDeps, str);
    }

    public final int getImportance(String str) {
        return ((ActivityManager) this.mContext.getSystemService(ActivityManager.class)).getPackageImportance(str);
    }

    public final boolean hasAudioFocus(String str) {
        try {
            List focusStack = IAudioService.Stub.asInterface(ServiceManager.getService("audio")).getFocusStack();
            int size = focusStack.size();
            return TextUtils.equals(str, size > 0 ? ((AudioFocusInfo) focusStack.get(size - 1)).getPackageName() : null);
        } catch (Exception unused) {
            return false;
        }
    }

    public final boolean hasVoiceCall() {
        try {
            int mode = ((AudioManager) this.mContext.getSystemService(AudioManager.class)).getMode();
            return mode == 2 || mode == 3;
        } catch (Exception unused) {
            return false;
        }
    }

    public final boolean isRecordingAudio(String str) {
        try {
            for (AudioRecordingConfiguration audioRecordingConfiguration : ((AudioManager) this.mContext.getSystemService(AudioManager.class)).getActiveRecordingConfigurations()) {
                if (TextUtils.equals(audioRecordingConfiguration.getClientPackageName(), str)) {
                    return true;
                }
            }
            return false;
        } catch (Exception unused) {
            return false;
        }
    }

    public final boolean isAppForeground(String str) {
        return getImportance(str) <= 125;
    }

    public boolean isAppTopVisible(String str) {
        return getImportance(str) <= 100;
    }

    public final boolean hasActiveAudio(String str) {
        return hasAudioFocus(str) || isRecordingAudio(str);
    }

    public final boolean hasActiveNetwork(List<String> list, int i) {
        IPackageManager packageManager = ActivityThread.getPackageManager();
        NetworkStatsManager networkStatsManager = (NetworkStatsManager) this.mContext.getSystemService(NetworkStatsManager.class);
        long currentTimeMillis = System.currentTimeMillis();
        try {
            NetworkStats querySummary = networkStatsManager.querySummary(i, null, currentTimeMillis - ACTIVE_NETWORK_DURATION_MILLIS, currentTimeMillis);
            NetworkStats.Bucket bucket = new NetworkStats.Bucket();
            while (querySummary.hasNextBucket()) {
                querySummary.getNextBucket(bucket);
                if (list.contains(packageManager.getNameForUid(bucket.getUid())) && (bucket.getRxPackets() > 0 || bucket.getTxPackets() > 0)) {
                    querySummary.close();
                    return true;
                }
            }
            querySummary.close();
            return false;
        } catch (Exception unused) {
            return false;
        }
    }

    public static boolean containsAny(String[] strArr, List<String> list) {
        int length = strArr.length;
        int size = list.size();
        int i = 0;
        int i2 = 0;
        while (i < length && i2 < size) {
            int compareTo = strArr[i].compareTo(list.get(i2));
            if (compareTo == 0) {
                return true;
            }
            if (compareTo < 0) {
                i++;
            } else {
                i2++;
            }
        }
        return false;
    }

    public final void addLibraryDependency(final ArraySet<String> arraySet, List<String> list) {
        PackageManagerInternal packageManagerInternal = (PackageManagerInternal) LocalServices.getService(PackageManagerInternal.class);
        final ArrayList arrayList = new ArrayList();
        final ArrayList arrayList2 = new ArrayList();
        final ArrayList arrayList3 = new ArrayList();
        for (String str : list) {
            AndroidPackage androidPackage = packageManagerInternal.getAndroidPackage(str);
            if (androidPackage != null) {
                arrayList.addAll(androidPackage.getLibraryNames());
                String staticSharedLibraryName = androidPackage.getStaticSharedLibraryName();
                if (staticSharedLibraryName != null) {
                    arrayList2.add(staticSharedLibraryName);
                }
                String sdkLibraryName = androidPackage.getSdkLibraryName();
                if (sdkLibraryName != null) {
                    arrayList3.add(sdkLibraryName);
                }
            }
        }
        if (arrayList.isEmpty() && arrayList2.isEmpty() && arrayList3.isEmpty()) {
            return;
        }
        Collections.sort(arrayList);
        Collections.sort(arrayList3);
        Collections.sort(arrayList2);
        packageManagerInternal.forEachPackageState(new Consumer() { // from class: com.android.server.pm.AppStateHelper$$ExternalSyntheticLambda0
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                AppStateHelper.lambda$addLibraryDependency$0(arrayList, arrayList2, arrayList3, arraySet, (PackageStateInternal) obj);
            }
        });
    }

    public static /* synthetic */ void lambda$addLibraryDependency$0(ArrayList arrayList, ArrayList arrayList2, ArrayList arrayList3, ArraySet arraySet, PackageStateInternal packageStateInternal) {
        AndroidPackageInternal pkg = packageStateInternal.getPkg();
        if (pkg == null) {
            return;
        }
        if (containsAny(pkg.getUsesLibrariesSorted(), arrayList) || containsAny(pkg.getUsesOptionalLibrariesSorted(), arrayList) || containsAny(pkg.getUsesStaticLibrariesSorted(), arrayList2) || containsAny(pkg.getUsesSdkLibrariesSorted(), arrayList3)) {
            arraySet.add(pkg.getPackageName());
        }
    }

    public final boolean hasActiveNetwork(List<String> list) {
        return hasActiveNetwork(list, 1) || hasActiveNetwork(list, 0);
    }

    /* JADX WARN: Removed duplicated region for block: B:5:0x000a  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public boolean hasInteractingApp(List<String> list) {
        for (String str : list) {
            if (hasActiveAudio(str) || isAppTopVisible(str)) {
                return true;
            }
            while (r0.hasNext()) {
            }
        }
        return hasActiveNetwork(list);
    }

    public boolean hasForegroundApp(List<String> list) {
        for (String str : list) {
            if (isAppForeground(str)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasTopVisibleApp(List<String> list) {
        for (String str : list) {
            if (isAppTopVisible(str)) {
                return true;
            }
        }
        return false;
    }

    public boolean isInCall() {
        if (SystemProperties.getBoolean("debug.pm.gentle_update_test.is_in_call", false)) {
            return true;
        }
        return ((TelecomManager) this.mContext.getSystemService(TelecomManager.class)).isInCall() || hasVoiceCall();
    }

    public List<String> getDependencyPackages(List<String> list) {
        ArraySet<String> arraySet = new ArraySet<>();
        for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : ((ActivityManager) this.mContext.getSystemService(ActivityManager.class)).getRunningAppProcesses()) {
            for (String str : list) {
                if (isPackageLoaded(runningAppProcessInfo, str)) {
                    for (String str2 : runningAppProcessInfo.pkgList) {
                        arraySet.add(str2);
                    }
                }
            }
        }
        ActivityManagerInternal activityManagerInternal = (ActivityManagerInternal) LocalServices.getService(ActivityManagerInternal.class);
        for (String str3 : list) {
            arraySet.addAll(activityManagerInternal.getClientPackages(str3));
        }
        addLibraryDependency(arraySet, list);
        return new ArrayList(arraySet);
    }
}
