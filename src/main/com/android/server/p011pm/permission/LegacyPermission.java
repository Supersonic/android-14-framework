package com.android.server.p011pm.permission;

import android.content.pm.PermissionInfo;
import com.android.modules.utils.TypedXmlPullParser;
import com.android.modules.utils.TypedXmlSerializer;
import com.android.server.p011pm.DumpState;
import com.android.server.p011pm.PackageManagerService;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import libcore.util.EmptyArray;
/* renamed from: com.android.server.pm.permission.LegacyPermission */
/* loaded from: classes2.dex */
public final class LegacyPermission {
    public final int[] mGids;
    public final PermissionInfo mPermissionInfo;
    public final int mType;
    public final int mUid;

    public LegacyPermission(PermissionInfo permissionInfo, int i, int i2, int[] iArr) {
        this.mPermissionInfo = permissionInfo;
        this.mType = i;
        this.mUid = i2;
        this.mGids = iArr;
    }

    public LegacyPermission(String str, String str2, int i) {
        PermissionInfo permissionInfo = new PermissionInfo();
        this.mPermissionInfo = permissionInfo;
        permissionInfo.name = str;
        permissionInfo.packageName = str2;
        permissionInfo.protectionLevel = 2;
        this.mType = i;
        this.mUid = 0;
        this.mGids = EmptyArray.INT;
    }

    public PermissionInfo getPermissionInfo() {
        return this.mPermissionInfo;
    }

    public int getType() {
        return this.mType;
    }

    public static boolean read(Map<String, LegacyPermission> map, TypedXmlPullParser typedXmlPullParser) {
        if (typedXmlPullParser.getName().equals("item")) {
            String attributeValue = typedXmlPullParser.getAttributeValue((String) null, "name");
            String attributeValue2 = typedXmlPullParser.getAttributeValue((String) null, "package");
            String attributeValue3 = typedXmlPullParser.getAttributeValue((String) null, "type");
            if (attributeValue == null || attributeValue2 == null) {
                PackageManagerService.reportSettingsProblem(5, "Error in package manager settings: permissions has no name at " + typedXmlPullParser.getPositionDescription());
                return false;
            }
            boolean equals = "dynamic".equals(attributeValue3);
            LegacyPermission legacyPermission = map.get(attributeValue);
            if (legacyPermission == null || legacyPermission.mType != 1) {
                legacyPermission = new LegacyPermission(attributeValue.intern(), attributeValue2, equals ? 2 : 0);
            }
            legacyPermission.mPermissionInfo.protectionLevel = readInt(typedXmlPullParser, null, "protection", 0);
            PermissionInfo permissionInfo = legacyPermission.mPermissionInfo;
            permissionInfo.protectionLevel = PermissionInfo.fixProtectionLevel(permissionInfo.protectionLevel);
            if (equals) {
                legacyPermission.mPermissionInfo.icon = readInt(typedXmlPullParser, null, "icon", 0);
                legacyPermission.mPermissionInfo.nonLocalizedLabel = typedXmlPullParser.getAttributeValue((String) null, "label");
            }
            map.put(legacyPermission.mPermissionInfo.name, legacyPermission);
            return true;
        }
        return false;
    }

    public static int readInt(TypedXmlPullParser typedXmlPullParser, String str, String str2, int i) {
        return typedXmlPullParser.getAttributeInt(str, str2, i);
    }

    public void write(TypedXmlSerializer typedXmlSerializer) throws IOException {
        if (this.mPermissionInfo.packageName == null) {
            return;
        }
        typedXmlSerializer.startTag((String) null, "item");
        typedXmlSerializer.attribute((String) null, "name", this.mPermissionInfo.name);
        typedXmlSerializer.attribute((String) null, "package", this.mPermissionInfo.packageName);
        int i = this.mPermissionInfo.protectionLevel;
        if (i != 0) {
            typedXmlSerializer.attributeInt((String) null, "protection", i);
        }
        if (this.mType == 2) {
            typedXmlSerializer.attribute((String) null, "type", "dynamic");
            int i2 = this.mPermissionInfo.icon;
            if (i2 != 0) {
                typedXmlSerializer.attributeInt((String) null, "icon", i2);
            }
            CharSequence charSequence = this.mPermissionInfo.nonLocalizedLabel;
            if (charSequence != null) {
                typedXmlSerializer.attribute((String) null, "label", charSequence.toString());
            }
        }
        typedXmlSerializer.endTag((String) null, "item");
    }

    public boolean dump(PrintWriter printWriter, String str, Set<String> set, boolean z, boolean z2, DumpState dumpState) {
        if (str == null || str.equals(this.mPermissionInfo.packageName)) {
            if (set == null || set.contains(this.mPermissionInfo.name)) {
                if (!z2) {
                    if (dumpState.onTitlePrinted()) {
                        printWriter.println();
                    }
                    printWriter.println("Permissions:");
                }
                printWriter.print("  Permission [");
                printWriter.print(this.mPermissionInfo.name);
                printWriter.print("] (");
                printWriter.print(Integer.toHexString(System.identityHashCode(this)));
                printWriter.println("):");
                printWriter.print("    sourcePackage=");
                printWriter.println(this.mPermissionInfo.packageName);
                printWriter.print("    uid=");
                printWriter.print(this.mUid);
                printWriter.print(" gids=");
                printWriter.print(Arrays.toString(this.mGids));
                printWriter.print(" type=");
                printWriter.print(this.mType);
                printWriter.print(" prot=");
                printWriter.println(PermissionInfo.protectionToString(this.mPermissionInfo.protectionLevel));
                if (this.mPermissionInfo != null) {
                    printWriter.print("    perm=");
                    printWriter.println(this.mPermissionInfo);
                    int i = this.mPermissionInfo.flags;
                    if ((1073741824 & i) == 0 || (i & 2) != 0) {
                        printWriter.print("    flags=0x");
                        printWriter.println(Integer.toHexString(this.mPermissionInfo.flags));
                    }
                }
                if (Objects.equals(this.mPermissionInfo.name, "android.permission.READ_EXTERNAL_STORAGE")) {
                    printWriter.print("    enforced=");
                    printWriter.println(z);
                    return true;
                }
                return true;
            }
            return false;
        }
        return false;
    }
}
