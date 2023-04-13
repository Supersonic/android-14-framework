package com.android.server.p011pm.permission;

import android.util.ArrayMap;
import android.util.ArraySet;
import com.android.internal.annotations.GuardedBy;
import com.android.internal.util.XmlUtils;
import com.android.modules.utils.TypedXmlPullParser;
import com.android.modules.utils.TypedXmlSerializer;
import com.android.server.p011pm.DumpState;
import com.android.server.p011pm.PackageManagerService;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.xmlpull.v1.XmlPullParserException;
/* renamed from: com.android.server.pm.permission.LegacyPermissionSettings */
/* loaded from: classes2.dex */
public class LegacyPermissionSettings {
    public final Object mLock;
    @GuardedBy({"mLock"})
    public final ArrayMap<String, LegacyPermission> mPermissions = new ArrayMap<>();
    @GuardedBy({"mLock"})
    public final ArrayMap<String, LegacyPermission> mPermissionTrees = new ArrayMap<>();

    public LegacyPermissionSettings(Object obj) {
        this.mLock = obj;
    }

    public List<LegacyPermission> getPermissions() {
        ArrayList arrayList;
        synchronized (this.mLock) {
            arrayList = new ArrayList(this.mPermissions.values());
        }
        return arrayList;
    }

    public List<LegacyPermission> getPermissionTrees() {
        ArrayList arrayList;
        synchronized (this.mLock) {
            arrayList = new ArrayList(this.mPermissionTrees.values());
        }
        return arrayList;
    }

    public void replacePermissions(List<LegacyPermission> list) {
        synchronized (this.mLock) {
            this.mPermissions.clear();
            int size = list.size();
            for (int i = 0; i < size; i++) {
                LegacyPermission legacyPermission = list.get(i);
                this.mPermissions.put(legacyPermission.getPermissionInfo().name, legacyPermission);
            }
        }
    }

    public void replacePermissionTrees(List<LegacyPermission> list) {
        synchronized (this.mLock) {
            this.mPermissionTrees.clear();
            int size = list.size();
            for (int i = 0; i < size; i++) {
                LegacyPermission legacyPermission = list.get(i);
                this.mPermissionTrees.put(legacyPermission.getPermissionInfo().name, legacyPermission);
            }
        }
    }

    public void readPermissions(TypedXmlPullParser typedXmlPullParser) throws IOException, XmlPullParserException {
        synchronized (this.mLock) {
            readPermissions(this.mPermissions, typedXmlPullParser);
        }
    }

    public void readPermissionTrees(TypedXmlPullParser typedXmlPullParser) throws IOException, XmlPullParserException {
        synchronized (this.mLock) {
            readPermissions(this.mPermissionTrees, typedXmlPullParser);
        }
    }

    public static void readPermissions(ArrayMap<String, LegacyPermission> arrayMap, TypedXmlPullParser typedXmlPullParser) throws IOException, XmlPullParserException {
        int depth = typedXmlPullParser.getDepth();
        while (true) {
            int next = typedXmlPullParser.next();
            if (next == 1) {
                return;
            }
            if (next == 3 && typedXmlPullParser.getDepth() <= depth) {
                return;
            }
            if (next != 3 && next != 4) {
                if (!LegacyPermission.read(arrayMap, typedXmlPullParser)) {
                    PackageManagerService.reportSettingsProblem(5, "Unknown element reading permissions: " + typedXmlPullParser.getName() + " at " + typedXmlPullParser.getPositionDescription());
                }
                XmlUtils.skipCurrentTag(typedXmlPullParser);
            }
        }
    }

    public void writePermissions(TypedXmlSerializer typedXmlSerializer) throws IOException {
        synchronized (this.mLock) {
            for (LegacyPermission legacyPermission : this.mPermissions.values()) {
                legacyPermission.write(typedXmlSerializer);
            }
        }
    }

    public void writePermissionTrees(TypedXmlSerializer typedXmlSerializer) throws IOException {
        synchronized (this.mLock) {
            for (LegacyPermission legacyPermission : this.mPermissionTrees.values()) {
                legacyPermission.write(typedXmlSerializer);
            }
        }
    }

    public static void dumpPermissions(PrintWriter printWriter, String str, ArraySet<String> arraySet, List<LegacyPermission> list, Map<String, Set<String>> map, boolean z, DumpState dumpState) {
        int size = list.size();
        boolean z2 = false;
        for (int i = 0; i < size; i++) {
            z2 = list.get(i).dump(printWriter, str, arraySet, z, z2, dumpState);
        }
        if (str == null && arraySet == null) {
            boolean z3 = true;
            for (Map.Entry<String, Set<String>> entry : map.entrySet()) {
                if (z3) {
                    if (dumpState.onTitlePrinted()) {
                        printWriter.println();
                    }
                    printWriter.println("AppOp Permissions:");
                    z3 = false;
                }
                printWriter.print("  AppOp Permission ");
                printWriter.print(entry.getKey());
                printWriter.println(com.android.internal.util.jobs.XmlUtils.STRING_ARRAY_SEPARATOR);
                for (String str2 : entry.getValue()) {
                    printWriter.print("    ");
                    printWriter.println(str2);
                }
            }
        }
    }
}
