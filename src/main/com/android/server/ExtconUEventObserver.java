package com.android.server;

import android.os.FileUtils;
import android.os.UEventObserver;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Slog;
import com.android.internal.annotations.GuardedBy;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
/* loaded from: classes.dex */
public abstract class ExtconUEventObserver extends UEventObserver {
    public final Map<String, ExtconInfo> mExtconInfos = new ArrayMap();

    public abstract void onUEvent(ExtconInfo extconInfo, UEventObserver.UEvent uEvent);

    public final void onUEvent(UEventObserver.UEvent uEvent) {
        ExtconInfo extconInfo = this.mExtconInfos.get(uEvent.get("DEVPATH"));
        if (extconInfo != null) {
            onUEvent(extconInfo, uEvent);
            return;
        }
        Slog.w("ExtconUEventObserver", "No match found for DEVPATH of " + uEvent + " in " + this.mExtconInfos);
    }

    public void startObserving(ExtconInfo extconInfo) {
        String devicePath = extconInfo.getDevicePath();
        if (devicePath == null) {
            Slog.wtf("ExtconUEventObserver", "Unable to start observing  " + extconInfo.getName() + " because the device path is null. This probably means the selinux policies need to be changed.");
            return;
        }
        this.mExtconInfos.put(devicePath, extconInfo);
        startObserving("DEVPATH=" + devicePath);
    }

    /* loaded from: classes.dex */
    public static final class ExtconInfo {
        public static ExtconInfo[] sExtconInfos;
        public static final Object sLock = new Object();
        public final HashSet<String> mDeviceTypes = new HashSet<>();
        public final String mName;

        @GuardedBy({"sLock"})
        public static void initExtconInfos() {
            if (sExtconInfos != null) {
                return;
            }
            File file = new File("/sys/class/extcon");
            File[] listFiles = file.listFiles();
            if (listFiles == null) {
                Slog.w("ExtconUEventObserver", file + " exists " + file.exists() + " isDir " + file.isDirectory() + " but listFiles returns null.This probably means the selinux policies need to be changed.");
                sExtconInfos = new ExtconInfo[0];
                return;
            }
            ArrayList arrayList = new ArrayList(listFiles.length);
            for (File file2 : listFiles) {
                arrayList.add(new ExtconInfo(file2.getName()));
            }
            sExtconInfos = (ExtconInfo[]) arrayList.toArray(new ExtconInfo[0]);
        }

        public static List<ExtconInfo> getExtconInfoForTypes(String[] strArr) {
            ExtconInfo[] extconInfoArr;
            synchronized (sLock) {
                initExtconInfos();
            }
            ArrayList arrayList = new ArrayList();
            for (ExtconInfo extconInfo : sExtconInfos) {
                int length = strArr.length;
                int i = 0;
                while (true) {
                    if (i >= length) {
                        break;
                    } else if (extconInfo.hasCableType(strArr[i])) {
                        arrayList.add(extconInfo);
                        break;
                    } else {
                        i++;
                    }
                }
            }
            return arrayList;
        }

        public boolean hasCableType(String str) {
            return this.mDeviceTypes.contains(str);
        }

        public ExtconInfo(String str) {
            String canonicalPath;
            this.mName = str;
            File[] listFilesOrEmpty = FileUtils.listFilesOrEmpty(new File("/sys/class/extcon", str), new FilenameFilter() { // from class: com.android.server.ExtconUEventObserver$ExtconInfo$$ExternalSyntheticLambda0
                @Override // java.io.FilenameFilter
                public final boolean accept(File file, String str2) {
                    boolean startsWith;
                    startsWith = str2.startsWith("cable.");
                    return startsWith;
                }
            });
            if (listFilesOrEmpty.length == 0) {
                Slog.d("ExtconUEventObserver", "Unable to list cables in /sys/class/extcon/" + str + ". This probably means the selinux policies need to be changed.");
            }
            for (File file : listFilesOrEmpty) {
                String str2 = null;
                try {
                    canonicalPath = file.getCanonicalPath();
                } catch (IOException e) {
                    e = e;
                }
                try {
                    this.mDeviceTypes.add(FileUtils.readTextFile(new File(file, "name"), 0, null).replace("\n", "").replace("\r", ""));
                } catch (IOException e2) {
                    e = e2;
                    str2 = canonicalPath;
                    Slog.w("ExtconUEventObserver", "Unable to read " + str2 + "/name. This probably means the selinux policies need to be changed.", e);
                }
            }
        }

        public String getName() {
            return this.mName;
        }

        public String getDevicePath() {
            try {
                File file = new File(TextUtils.formatSimple("/sys/class/extcon/%s", new Object[]{this.mName}));
                if (file.exists()) {
                    String canonicalPath = file.getCanonicalPath();
                    return canonicalPath.substring(canonicalPath.indexOf("/devices"));
                }
                return null;
            } catch (IOException e) {
                Slog.e("ExtconUEventObserver", "Could not get the extcon device path for " + this.mName, e);
                return null;
            }
        }

        public String getStatePath() {
            return TextUtils.formatSimple("/sys/class/extcon/%s/state", new Object[]{this.mName});
        }
    }

    public static boolean extconExists() {
        File file = new File("/sys/class/extcon");
        return file.exists() && file.isDirectory();
    }
}
