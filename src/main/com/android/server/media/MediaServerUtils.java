package com.android.server.media;

import android.content.Context;
import android.content.p000pm.PackageManagerInternal;
import android.os.Binder;
import android.os.UserHandle;
import android.text.TextUtils;
import com.android.server.LocalServices;
import java.io.PrintWriter;
/* loaded from: classes2.dex */
public class MediaServerUtils {
    public static void enforcePackageName(String str, int i) {
        if (i == 0 || i == 2000) {
            return;
        }
        if (TextUtils.isEmpty(str)) {
            throw new IllegalArgumentException("packageName may not be empty");
        }
        if (UserHandle.isSameApp(i, ((PackageManagerInternal) LocalServices.getService(PackageManagerInternal.class)).getPackageUid(str, 0L, UserHandle.getUserId(i)))) {
            return;
        }
        throw new IllegalArgumentException("packageName does not belong to the calling uid; pkg=" + str + ", uid=" + i);
    }

    public static boolean checkDumpPermission(Context context, String str, PrintWriter printWriter) {
        if (context.checkCallingOrSelfPermission("android.permission.DUMP") != 0) {
            printWriter.println("Permission Denial: can't dump " + str + " from from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid() + " due to missing android.permission.DUMP permission");
            return false;
        }
        return true;
    }
}
