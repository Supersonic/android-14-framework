package com.android.server.p011pm;

import android.util.ArrayMap;
import android.util.ArraySet;
import com.android.server.p011pm.ShortcutService;
import java.io.PrintWriter;
/* renamed from: com.android.server.pm.ShortcutNonPersistentUser */
/* loaded from: classes2.dex */
public class ShortcutNonPersistentUser {
    public final ShortcutService mService;
    public final int mUserId;
    public final ArrayMap<String, String> mHostPackages = new ArrayMap<>();
    public final ArraySet<String> mHostPackageSet = new ArraySet<>();

    public ShortcutNonPersistentUser(ShortcutService shortcutService, int i) {
        this.mService = shortcutService;
        this.mUserId = i;
    }

    public int getUserId() {
        return this.mUserId;
    }

    public void setShortcutHostPackage(String str, String str2) {
        if (str2 != null) {
            this.mHostPackages.put(str, str2);
        } else {
            this.mHostPackages.remove(str);
        }
        this.mHostPackageSet.clear();
        for (int i = 0; i < this.mHostPackages.size(); i++) {
            this.mHostPackageSet.add(this.mHostPackages.valueAt(i));
        }
    }

    public boolean hasHostPackage(String str) {
        return this.mHostPackageSet.contains(str);
    }

    public void dump(PrintWriter printWriter, String str, ShortcutService.DumpFilter dumpFilter) {
        if (!dumpFilter.shouldDumpDetails() || this.mHostPackages.size() <= 0) {
            return;
        }
        printWriter.print(str);
        printWriter.print("Non-persistent: user ID:");
        printWriter.println(this.mUserId);
        printWriter.print(str);
        printWriter.println("  Host packages:");
        for (int i = 0; i < this.mHostPackages.size(); i++) {
            printWriter.print(str);
            printWriter.print("    ");
            printWriter.print(this.mHostPackages.keyAt(i));
            printWriter.print(": ");
            printWriter.println(this.mHostPackages.valueAt(i));
        }
        printWriter.println();
    }
}
