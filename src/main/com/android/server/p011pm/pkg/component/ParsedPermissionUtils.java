package com.android.server.p011pm.pkg.component;

import android.content.pm.PermissionInfo;
import android.content.pm.parsing.result.ParseInput;
import android.content.pm.parsing.result.ParseResult;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.os.Build;
import android.util.ArrayMap;
import android.util.EventLog;
import android.util.Slog;
import com.android.internal.R;
import com.android.server.p011pm.PackageManagerShellCommandDataLoader;
import com.android.server.p011pm.pkg.parsing.ParsingPackage;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import org.xmlpull.v1.XmlPullParserException;
/* renamed from: com.android.server.pm.pkg.component.ParsedPermissionUtils */
/* loaded from: classes2.dex */
public class ParsedPermissionUtils {
    /* JADX WARN: Removed duplicated region for block: B:65:0x01af  */
    /* JADX WARN: Removed duplicated region for block: B:67:0x01b4  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public static ParseResult<ParsedPermission> parsePermission(ParsingPackage parsingPackage, Resources resources, XmlResourceParser xmlResourceParser, boolean z, ParseInput parseInput) throws IOException, XmlPullParserException {
        TypedArray typedArray;
        ParsedPermissionImpl parsedPermissionImpl;
        ParseResult parseAllMetaData;
        String packageName = parsingPackage.getPackageName();
        ParsedPermissionImpl parsedPermissionImpl2 = new ParsedPermissionImpl();
        String str = "<" + xmlResourceParser.getName() + ">";
        TypedArray obtainAttributes = resources.obtainAttributes(xmlResourceParser, R.styleable.AndroidManifestPermission);
        try {
            ParseResult parseComponent = ParsedComponentUtils.parseComponent(parsedPermissionImpl2, str, parsingPackage, obtainAttributes, z, parseInput, 9, 5, 1, 0, 7, 2, 10);
            if (parseComponent.isError()) {
                ParseResult<ParsedPermission> error = parseInput.error(parseComponent);
                if (obtainAttributes != null) {
                    obtainAttributes.close();
                }
                return error;
            }
            typedArray = obtainAttributes;
            try {
                int i = typedArray.getInt(6, -1);
                if (i != -1 && i < Build.VERSION.SDK_INT) {
                    ParseResult<ParsedPermission> success = parseInput.success((Object) null);
                    typedArray.close();
                    return success;
                }
                if (typedArray.hasValue(12)) {
                    if (PackageManagerShellCommandDataLoader.PACKAGE.equals(packageName)) {
                        parsedPermissionImpl2.setBackgroundPermission(typedArray.getNonResourceString(12));
                    } else {
                        Slog.w("PackageParsing", packageName + " defines a background permission. Only the 'android' package can do that.");
                    }
                }
                parsedPermissionImpl2.setGroup(typedArray.getNonResourceString(4)).setRequestRes(typedArray.getResourceId(13, 0)).setProtectionLevel(typedArray.getInt(3, 0)).setFlags(typedArray.getInt(8, 0));
                int resourceId = typedArray.getResourceId(11, 0);
                if (resourceId != 0) {
                    parsedPermissionImpl = parsedPermissionImpl2;
                    if (resources.getResourceTypeName(resourceId).equals("array")) {
                        String[] stringArray = resources.getStringArray(resourceId);
                        if (stringArray != null) {
                            parsedPermissionImpl.setKnownCerts(stringArray);
                        }
                    } else {
                        String string = resources.getString(resourceId);
                        if (string != null) {
                            parsedPermissionImpl.setKnownCert(string);
                        }
                    }
                    if (parsedPermissionImpl.getKnownCerts().isEmpty()) {
                        Slog.w("PackageParsing", packageName + " defines a knownSigner permission but the provided knownCerts resource is null");
                    }
                } else {
                    parsedPermissionImpl = parsedPermissionImpl2;
                    String string2 = typedArray.getString(11);
                    if (string2 != null) {
                        parsedPermissionImpl.setKnownCert(string2);
                    }
                }
                if (isRuntime(parsedPermissionImpl) && PackageManagerShellCommandDataLoader.PACKAGE.equals(parsedPermissionImpl.getPackageName())) {
                    if ((parsedPermissionImpl.getFlags() & 4) != 0 && (parsedPermissionImpl.getFlags() & 8) != 0) {
                        throw new IllegalStateException("Permission cannot be both soft and hard restricted: " + parsedPermissionImpl.getName());
                    }
                    typedArray.close();
                    parsedPermissionImpl.setProtectionLevel(PermissionInfo.fixProtectionLevel(parsedPermissionImpl.getProtectionLevel()));
                    if ((getProtectionFlags(parsedPermissionImpl) & (-12353)) == 0 && getProtection(parsedPermissionImpl) != 2 && getProtection(parsedPermissionImpl) != 4) {
                        return parseInput.error("<permission> protectionLevel specifies a non-instant, non-appop, non-runtimeOnly flag but is not based on signature or internal type");
                    }
                    parseAllMetaData = ComponentParseUtils.parseAllMetaData(parsingPackage, resources, xmlResourceParser, str, parsedPermissionImpl, parseInput);
                    if (!parseAllMetaData.isError()) {
                        return parseInput.error(parseAllMetaData);
                    }
                    return parseInput.success((ParsedPermission) parseAllMetaData.getResult());
                }
                parsedPermissionImpl.setFlags(parsedPermissionImpl.getFlags() & (-5));
                parsedPermissionImpl.setFlags(parsedPermissionImpl.getFlags() & (-9));
                typedArray.close();
                parsedPermissionImpl.setProtectionLevel(PermissionInfo.fixProtectionLevel(parsedPermissionImpl.getProtectionLevel()));
                if ((getProtectionFlags(parsedPermissionImpl) & (-12353)) == 0) {
                }
                parseAllMetaData = ComponentParseUtils.parseAllMetaData(parsingPackage, resources, xmlResourceParser, str, parsedPermissionImpl, parseInput);
                if (!parseAllMetaData.isError()) {
                }
            } catch (Throwable th) {
                th = th;
                Throwable th2 = th;
                if (typedArray != null) {
                    try {
                        typedArray.close();
                    } catch (Throwable th3) {
                        th2.addSuppressed(th3);
                    }
                }
                throw th2;
            }
        } catch (Throwable th4) {
            th = th4;
            typedArray = obtainAttributes;
        }
    }

    public static ParseResult<ParsedPermission> parsePermissionTree(ParsingPackage parsingPackage, Resources resources, XmlResourceParser xmlResourceParser, boolean z, ParseInput parseInput) throws IOException, XmlPullParserException {
        ParsedPermissionImpl parsedPermissionImpl = new ParsedPermissionImpl();
        String str = "<" + xmlResourceParser.getName() + ">";
        TypedArray obtainAttributes = resources.obtainAttributes(xmlResourceParser, R.styleable.AndroidManifestPermissionTree);
        try {
            ParseResult parseComponent = ParsedComponentUtils.parseComponent(parsedPermissionImpl, str, parsingPackage, obtainAttributes, z, parseInput, 4, -1, 1, 0, 3, 2, 5);
            if (parseComponent.isError()) {
                return parseInput.error(parseComponent);
            }
            obtainAttributes.recycle();
            int indexOf = parsedPermissionImpl.getName().indexOf(46);
            if (indexOf > 0) {
                indexOf = parsedPermissionImpl.getName().indexOf(46, indexOf + 1);
            }
            if (indexOf < 0) {
                return parseInput.error("<permission-tree> name has less than three segments: " + parsedPermissionImpl.getName());
            }
            parsedPermissionImpl.setProtectionLevel(0).setTree(true);
            ParseResult parseAllMetaData = ComponentParseUtils.parseAllMetaData(parsingPackage, resources, xmlResourceParser, str, parsedPermissionImpl, parseInput);
            if (parseAllMetaData.isError()) {
                return parseInput.error(parseAllMetaData);
            }
            return parseInput.success((ParsedPermission) parseAllMetaData.getResult());
        } finally {
            obtainAttributes.recycle();
        }
    }

    public static ParseResult<ParsedPermissionGroup> parsePermissionGroup(ParsingPackage parsingPackage, Resources resources, XmlResourceParser xmlResourceParser, boolean z, ParseInput parseInput) throws IOException, XmlPullParserException {
        TypedArray typedArray;
        ParsedPermissionGroupImpl parsedPermissionGroupImpl = new ParsedPermissionGroupImpl();
        String str = "<" + xmlResourceParser.getName() + ">";
        TypedArray obtainAttributes = resources.obtainAttributes(xmlResourceParser, R.styleable.AndroidManifestPermissionGroup);
        try {
            ParseResult parseComponent = ParsedComponentUtils.parseComponent(parsedPermissionGroupImpl, str, parsingPackage, obtainAttributes, z, parseInput, 7, 4, 1, 0, 5, 2, 8);
            if (parseComponent.isError()) {
                ParseResult<ParsedPermissionGroup> error = parseInput.error(parseComponent);
                obtainAttributes.recycle();
                return error;
            }
            typedArray = obtainAttributes;
            try {
                parsedPermissionGroupImpl.setRequestDetailRes(typedArray.getResourceId(12, 0)).setBackgroundRequestRes(typedArray.getResourceId(9, 0)).setBackgroundRequestDetailRes(typedArray.getResourceId(10, 0)).setRequestRes(typedArray.getResourceId(11, 0)).setPriority(typedArray.getInt(3, 0)).setFlags(typedArray.getInt(6, 0));
                typedArray.recycle();
                ParseResult parseAllMetaData = ComponentParseUtils.parseAllMetaData(parsingPackage, resources, xmlResourceParser, str, parsedPermissionGroupImpl, parseInput);
                if (parseAllMetaData.isError()) {
                    return parseInput.error(parseAllMetaData);
                }
                return parseInput.success((ParsedPermissionGroup) parseAllMetaData.getResult());
            } catch (Throwable th) {
                th = th;
                typedArray.recycle();
                throw th;
            }
        } catch (Throwable th2) {
            th = th2;
            typedArray = obtainAttributes;
        }
    }

    public static boolean isRuntime(ParsedPermission parsedPermission) {
        return getProtection(parsedPermission) == 1;
    }

    public static boolean isAppOp(ParsedPermission parsedPermission) {
        return (parsedPermission.getProtectionLevel() & 64) != 0;
    }

    public static int getProtection(ParsedPermission parsedPermission) {
        return parsedPermission.getProtectionLevel() & 15;
    }

    public static int getProtectionFlags(ParsedPermission parsedPermission) {
        return parsedPermission.getProtectionLevel() & (-16);
    }

    public static boolean isMalformedDuplicate(ParsedPermission parsedPermission, ParsedPermission parsedPermission2) {
        return (parsedPermission == null || parsedPermission2 == null || parsedPermission.isTree() || parsedPermission2.isTree() || (parsedPermission.getProtectionLevel() == parsedPermission2.getProtectionLevel() && Objects.equals(parsedPermission.getGroup(), parsedPermission2.getGroup()))) ? false : true;
    }

    public static boolean declareDuplicatePermission(ParsingPackage parsingPackage) {
        List<ParsedPermission> permissions = parsingPackage.getPermissions();
        int size = permissions.size();
        if (size > 0) {
            ArrayMap arrayMap = new ArrayMap(size);
            for (int i = 0; i < size; i++) {
                ParsedPermission parsedPermission = permissions.get(i);
                String name = parsedPermission.getName();
                if (isMalformedDuplicate(parsedPermission, (ParsedPermission) arrayMap.get(name))) {
                    EventLog.writeEvent(1397638484, "213323615");
                    return true;
                }
                arrayMap.put(name, parsedPermission);
            }
        }
        return false;
    }
}
