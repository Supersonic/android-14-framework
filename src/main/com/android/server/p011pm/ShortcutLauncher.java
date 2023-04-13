package com.android.server.p011pm;

import android.content.pm.PackageInfo;
import android.content.pm.ShortcutInfo;
import android.content.pm.UserPackage;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.AtomicFile;
import android.util.Slog;
import android.util.Xml;
import com.android.internal.annotations.VisibleForTesting;
import com.android.modules.utils.TypedXmlPullParser;
import com.android.modules.utils.TypedXmlSerializer;
import com.android.server.p011pm.ShortcutService;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import libcore.io.IoUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParserException;
/* renamed from: com.android.server.pm.ShortcutLauncher */
/* loaded from: classes2.dex */
public class ShortcutLauncher extends ShortcutPackageItem {
    public final int mOwnerUserId;
    public final ArrayMap<UserPackage, ArraySet<String>> mPinnedShortcuts;

    @Override // com.android.server.p011pm.ShortcutPackageItem
    public boolean canRestoreAnyVersion() {
        return true;
    }

    public ShortcutLauncher(ShortcutUser shortcutUser, int i, String str, int i2, ShortcutPackageInfo shortcutPackageInfo) {
        super(shortcutUser, i2, str, shortcutPackageInfo == null ? ShortcutPackageInfo.newEmpty() : shortcutPackageInfo);
        this.mPinnedShortcuts = new ArrayMap<>();
        this.mOwnerUserId = i;
    }

    public ShortcutLauncher(ShortcutUser shortcutUser, int i, String str, int i2) {
        this(shortcutUser, i, str, i2, null);
    }

    @Override // com.android.server.p011pm.ShortcutPackageItem
    public int getOwnerUserId() {
        return this.mOwnerUserId;
    }

    public final void onRestoreBlocked() {
        ArrayList arrayList = new ArrayList(this.mPinnedShortcuts.keySet());
        this.mPinnedShortcuts.clear();
        for (int size = arrayList.size() - 1; size >= 0; size--) {
            ShortcutPackage packageShortcutsIfExists = this.mShortcutUser.getPackageShortcutsIfExists(((UserPackage) arrayList.get(size)).packageName);
            if (packageShortcutsIfExists != null) {
                packageShortcutsIfExists.refreshPinnedFlags();
            }
        }
    }

    @Override // com.android.server.p011pm.ShortcutPackageItem
    public void onRestored(int i) {
        if (i != 0) {
            onRestoreBlocked();
        }
    }

    public void pinShortcuts(int i, String str, List<String> list, boolean z) {
        ShortcutPackage packageShortcutsIfExists = this.mShortcutUser.getPackageShortcutsIfExists(str);
        if (packageShortcutsIfExists == null) {
            return;
        }
        UserPackage of = UserPackage.of(i, str);
        int size = list.size();
        if (size == 0) {
            this.mPinnedShortcuts.remove(of);
        } else {
            ArraySet<String> arraySet = this.mPinnedShortcuts.get(of);
            ArraySet<String> arraySet2 = new ArraySet<>();
            for (int i2 = 0; i2 < size; i2++) {
                String str2 = list.get(i2);
                ShortcutInfo findShortcutById = packageShortcutsIfExists.findShortcutById(str2);
                if (findShortcutById != null && (findShortcutById.isDynamic() || findShortcutById.isLongLived() || findShortcutById.isManifestShortcut() || ((arraySet != null && arraySet.contains(str2)) || z))) {
                    arraySet2.add(str2);
                }
            }
            this.mPinnedShortcuts.put(of, arraySet2);
        }
        packageShortcutsIfExists.refreshPinnedFlags();
    }

    public ArraySet<String> getPinnedShortcutIds(String str, int i) {
        return this.mPinnedShortcuts.get(UserPackage.of(i, str));
    }

    public boolean hasPinned(ShortcutInfo shortcutInfo) {
        ArraySet<String> pinnedShortcutIds = getPinnedShortcutIds(shortcutInfo.getPackage(), shortcutInfo.getUserId());
        return pinnedShortcutIds != null && pinnedShortcutIds.contains(shortcutInfo.getId());
    }

    public void addPinnedShortcut(String str, int i, String str2, boolean z) {
        ArrayList arrayList;
        ArraySet<String> pinnedShortcutIds = getPinnedShortcutIds(str, i);
        if (pinnedShortcutIds != null) {
            arrayList = new ArrayList(pinnedShortcutIds.size() + 1);
            arrayList.addAll(pinnedShortcutIds);
        } else {
            arrayList = new ArrayList(1);
        }
        arrayList.add(str2);
        pinShortcuts(i, str, arrayList, z);
    }

    public boolean cleanUpPackage(String str, int i) {
        return this.mPinnedShortcuts.remove(UserPackage.of(i, str)) != null;
    }

    public void ensurePackageInfo() {
        PackageInfo packageInfoWithSignatures = this.mShortcutUser.mService.getPackageInfoWithSignatures(getPackageName(), getPackageUserId());
        if (packageInfoWithSignatures == null) {
            Slog.w("ShortcutService", "Package not found: " + getPackageName());
            return;
        }
        getPackageInfo().updateFromPackageInfo(packageInfoWithSignatures);
    }

    @Override // com.android.server.p011pm.ShortcutPackageItem
    public void saveToXml(TypedXmlSerializer typedXmlSerializer, boolean z) throws IOException {
        int size;
        if ((!z || getPackageInfo().isBackupAllowed()) && (size = this.mPinnedShortcuts.size()) != 0) {
            typedXmlSerializer.startTag((String) null, "launcher-pins");
            ShortcutService.writeAttr(typedXmlSerializer, "package-name", getPackageName());
            ShortcutService.writeAttr(typedXmlSerializer, "launcher-user", getPackageUserId());
            getPackageInfo().saveToXml(this.mShortcutUser.mService, typedXmlSerializer, z);
            for (int i = 0; i < size; i++) {
                UserPackage keyAt = this.mPinnedShortcuts.keyAt(i);
                if (!z || keyAt.userId == getOwnerUserId()) {
                    typedXmlSerializer.startTag((String) null, "package");
                    ShortcutService.writeAttr(typedXmlSerializer, "package-name", keyAt.packageName);
                    ShortcutService.writeAttr(typedXmlSerializer, "package-user", keyAt.userId);
                    ArraySet<String> valueAt = this.mPinnedShortcuts.valueAt(i);
                    int size2 = valueAt.size();
                    for (int i2 = 0; i2 < size2; i2++) {
                        ShortcutService.writeTagValue(typedXmlSerializer, "pin", valueAt.valueAt(i2));
                    }
                    typedXmlSerializer.endTag((String) null, "package");
                }
            }
            typedXmlSerializer.endTag((String) null, "launcher-pins");
        }
    }

    public static ShortcutLauncher loadFromFile(File file, ShortcutUser shortcutUser, int i, boolean z) {
        AtomicFile atomicFile;
        try {
            FileInputStream openRead = new AtomicFile(file).openRead();
            try {
                TypedXmlPullParser resolvePullParser = Xml.resolvePullParser(openRead);
                ShortcutLauncher shortcutLauncher = null;
                while (true) {
                    int next = resolvePullParser.next();
                    if (next == 1) {
                        return shortcutLauncher;
                    }
                    if (next == 2) {
                        int depth = resolvePullParser.getDepth();
                        String name = resolvePullParser.getName();
                        if (depth == 1 && "launcher-pins".equals(name)) {
                            shortcutLauncher = loadFromXml(resolvePullParser, shortcutUser, i, z);
                        } else {
                            ShortcutService.throwForInvalidTag(depth, name);
                        }
                    }
                }
            } catch (IOException | XmlPullParserException e) {
                Slog.e("ShortcutService", "Failed to read file " + atomicFile.getBaseFile(), e);
                return null;
            } finally {
                IoUtils.closeQuietly(openRead);
            }
        } catch (FileNotFoundException unused) {
            return null;
        }
    }

    public static ShortcutLauncher loadFromXml(TypedXmlPullParser typedXmlPullParser, ShortcutUser shortcutUser, int i, boolean z) throws IOException, XmlPullParserException {
        ShortcutLauncher shortcutLauncher = new ShortcutLauncher(shortcutUser, i, ShortcutService.parseStringAttribute(typedXmlPullParser, "package-name"), z ? i : ShortcutService.parseIntAttribute(typedXmlPullParser, "launcher-user", i));
        int depth = typedXmlPullParser.getDepth();
        ArraySet<String> arraySet = null;
        while (true) {
            int next = typedXmlPullParser.next();
            if (next == 1 || (next == 3 && typedXmlPullParser.getDepth() <= depth)) {
                break;
            } else if (next == 2) {
                int depth2 = typedXmlPullParser.getDepth();
                String name = typedXmlPullParser.getName();
                if (depth2 == depth + 1) {
                    name.hashCode();
                    if (name.equals("package-info")) {
                        shortcutLauncher.getPackageInfo().loadFromXml(typedXmlPullParser, z);
                    } else if (name.equals("package")) {
                        String parseStringAttribute = ShortcutService.parseStringAttribute(typedXmlPullParser, "package-name");
                        int parseIntAttribute = z ? i : ShortcutService.parseIntAttribute(typedXmlPullParser, "package-user", i);
                        ArraySet<String> arraySet2 = new ArraySet<>();
                        shortcutLauncher.mPinnedShortcuts.put(UserPackage.of(parseIntAttribute, parseStringAttribute), arraySet2);
                        arraySet = arraySet2;
                    }
                }
                if (depth2 == depth + 2) {
                    name.hashCode();
                    if (name.equals("pin")) {
                        if (arraySet == null) {
                            Slog.w("ShortcutService", "pin in invalid place");
                        } else {
                            arraySet.add(ShortcutService.parseStringAttribute(typedXmlPullParser, "value"));
                        }
                    }
                }
                ShortcutService.warnForInvalidTag(depth2, name);
            }
        }
        return shortcutLauncher;
    }

    public void dump(PrintWriter printWriter, String str, ShortcutService.DumpFilter dumpFilter) {
        printWriter.println();
        printWriter.print(str);
        printWriter.print("Launcher: ");
        printWriter.print(getPackageName());
        printWriter.print("  Package user: ");
        printWriter.print(getPackageUserId());
        printWriter.print("  Owner user: ");
        printWriter.print(getOwnerUserId());
        printWriter.println();
        getPackageInfo().dump(printWriter, str + "  ");
        printWriter.println();
        int size = this.mPinnedShortcuts.size();
        for (int i = 0; i < size; i++) {
            printWriter.println();
            UserPackage keyAt = this.mPinnedShortcuts.keyAt(i);
            printWriter.print(str);
            printWriter.print("  ");
            printWriter.print("Package: ");
            printWriter.print(keyAt.packageName);
            printWriter.print("  User: ");
            printWriter.println(keyAt.userId);
            ArraySet<String> valueAt = this.mPinnedShortcuts.valueAt(i);
            int size2 = valueAt.size();
            for (int i2 = 0; i2 < size2; i2++) {
                printWriter.print(str);
                printWriter.print("    Pinned: ");
                printWriter.print(valueAt.valueAt(i2));
                printWriter.println();
            }
        }
    }

    @Override // com.android.server.p011pm.ShortcutPackageItem
    public JSONObject dumpCheckin(boolean z) throws JSONException {
        return super.dumpCheckin(z);
    }

    @VisibleForTesting
    public ArraySet<String> getAllPinnedShortcutsForTest(String str, int i) {
        return new ArraySet<>(this.mPinnedShortcuts.get(UserPackage.of(i, str)));
    }

    @Override // com.android.server.p011pm.ShortcutPackageItem
    public File getShortcutPackageItemFile() {
        ShortcutUser shortcutUser = this.mShortcutUser;
        File file = new File(shortcutUser.mService.injectUserDataPath(shortcutUser.getUserId()), "launchers");
        return new File(file, getPackageName() + getPackageUserId() + ".xml");
    }
}
