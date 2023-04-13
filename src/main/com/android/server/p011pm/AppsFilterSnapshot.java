package com.android.server.p011pm;

import android.util.ArrayMap;
import android.util.SparseArray;
import com.android.internal.util.function.QuadFunction;
import com.android.server.p011pm.pkg.AndroidPackage;
import com.android.server.p011pm.pkg.PackageStateInternal;
import com.android.server.p011pm.snapshot.PackageDataSnapshot;
import java.io.PrintWriter;
/* renamed from: com.android.server.pm.AppsFilterSnapshot */
/* loaded from: classes2.dex */
public interface AppsFilterSnapshot {
    boolean canQueryPackage(AndroidPackage androidPackage, String str);

    void dumpQueries(PrintWriter printWriter, Integer num, DumpState dumpState, int[] iArr, QuadFunction<Integer, Integer, Integer, Boolean, String[]> quadFunction);

    SparseArray<int[]> getVisibilityAllowList(PackageDataSnapshot packageDataSnapshot, PackageStateInternal packageStateInternal, int[] iArr, ArrayMap<String, ? extends PackageStateInternal> arrayMap);

    boolean shouldFilterApplication(PackageDataSnapshot packageDataSnapshot, int i, Object obj, PackageStateInternal packageStateInternal, int i2);
}
