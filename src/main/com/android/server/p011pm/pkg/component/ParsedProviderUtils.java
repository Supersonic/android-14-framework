package com.android.server.p011pm.pkg.component;

import android.content.pm.PathPermission;
import android.content.pm.parsing.result.ParseInput;
import android.content.pm.parsing.result.ParseResult;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.os.PatternMatcher;
import android.util.Slog;
import com.android.internal.R;
import com.android.server.p011pm.pkg.parsing.ParsingPackage;
import com.android.server.p011pm.pkg.parsing.ParsingUtils;
import java.io.IOException;
import java.util.Objects;
import org.xmlpull.v1.XmlPullParserException;
/* renamed from: com.android.server.pm.pkg.component.ParsedProviderUtils */
/* loaded from: classes2.dex */
public class ParsedProviderUtils {
    public static ParseResult<ParsedProvider> parseProvider(String[] strArr, ParsingPackage parsingPackage, Resources resources, XmlResourceParser xmlResourceParser, int i, boolean z, String str, ParseInput parseInput) throws IOException, XmlPullParserException {
        TypedArray typedArray;
        int targetSdkVersion = parsingPackage.getTargetSdkVersion();
        String packageName = parsingPackage.getPackageName();
        ParsedProviderImpl parsedProviderImpl = new ParsedProviderImpl();
        String name = xmlResourceParser.getName();
        TypedArray obtainAttributes = resources.obtainAttributes(xmlResourceParser, R.styleable.AndroidManifestProvider);
        try {
            ParseResult parseMainComponent = ParsedMainComponentUtils.parseMainComponent(parsedProviderImpl, name, strArr, parsingPackage, obtainAttributes, i, z, str, parseInput, 17, 14, 18, 6, 1, 0, 15, 2, 8, 19, 21, 23);
            if (parseMainComponent.isError()) {
                ParseResult<ParsedProvider> error = parseInput.error(parseMainComponent);
                obtainAttributes.recycle();
                return error;
            }
            typedArray = obtainAttributes;
            try {
                String nonConfigurationString = typedArray.getNonConfigurationString(10, 0);
                parsedProviderImpl.setSyncable(typedArray.getBoolean(11, false)).setExported(typedArray.getBoolean(7, targetSdkVersion < 17));
                String nonConfigurationString2 = typedArray.getNonConfigurationString(3, 0);
                String nonConfigurationString3 = typedArray.getNonConfigurationString(4, 0);
                if (nonConfigurationString3 == null) {
                    nonConfigurationString3 = nonConfigurationString2;
                }
                if (nonConfigurationString3 == null) {
                    parsedProviderImpl.setReadPermission(parsingPackage.getPermission());
                } else {
                    parsedProviderImpl.setReadPermission(nonConfigurationString3);
                }
                String nonConfigurationString4 = typedArray.getNonConfigurationString(5, 0);
                if (nonConfigurationString4 != null) {
                    nonConfigurationString2 = nonConfigurationString4;
                }
                if (nonConfigurationString2 == null) {
                    parsedProviderImpl.setWritePermission(parsingPackage.getPermission());
                } else {
                    parsedProviderImpl.setWritePermission(nonConfigurationString2);
                }
                parsedProviderImpl.setGrantUriPermissions(typedArray.getBoolean(13, false)).setForceUriPermissions(typedArray.getBoolean(22, false)).setMultiProcess(typedArray.getBoolean(9, false)).setInitOrder(typedArray.getInt(12, 0)).setFlags(parsedProviderImpl.getFlags() | ComponentParseUtils.flag(1073741824, 16, typedArray));
                boolean z2 = typedArray.getBoolean(20, false);
                if (z2) {
                    parsedProviderImpl.setFlags(parsedProviderImpl.getFlags() | 1048576);
                    parsingPackage.setVisibleToInstantApps(true);
                }
                typedArray.recycle();
                if (parsingPackage.isSaveStateDisallowed() && Objects.equals(parsedProviderImpl.getProcessName(), packageName)) {
                    return parseInput.error("Heavy-weight applications can not have providers in main process");
                }
                if (nonConfigurationString == null) {
                    return parseInput.error("<provider> does not include authorities attribute");
                }
                if (nonConfigurationString.length() <= 0) {
                    return parseInput.error("<provider> has empty authorities attribute");
                }
                parsedProviderImpl.setAuthority(nonConfigurationString);
                return parseProviderTags(parsingPackage, name, resources, xmlResourceParser, z2, parsedProviderImpl, parseInput);
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

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Code restructure failed: missing block: B:28:0x005c, code lost:
        if (r0.equals("meta-data") == false) goto L17;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public static ParseResult<ParsedProvider> parseProviderTags(ParsingPackage parsingPackage, String str, Resources resources, XmlResourceParser xmlResourceParser, boolean z, ParsedProviderImpl parsedProviderImpl, ParseInput parseInput) throws XmlPullParserException, IOException {
        ParseResult parseGrantUriPermission;
        int depth = xmlResourceParser.getDepth();
        while (true) {
            int next = xmlResourceParser.next();
            char c = 1;
            if (next != 1 && (next != 3 || xmlResourceParser.getDepth() > depth)) {
                if (next == 2) {
                    String name = xmlResourceParser.getName();
                    name.hashCode();
                    switch (name.hashCode()) {
                        case -1814617695:
                            if (name.equals("grant-uri-permission")) {
                                c = 0;
                                break;
                            }
                            c = 65535;
                            break;
                        case -1115949454:
                            break;
                        case -1029793847:
                            if (name.equals("intent-filter")) {
                                c = 2;
                                break;
                            }
                            c = 65535;
                            break;
                        case -993141291:
                            if (name.equals("property")) {
                                c = 3;
                                break;
                            }
                            c = 65535;
                            break;
                        case 636171383:
                            if (name.equals("path-permission")) {
                                c = 4;
                                break;
                            }
                            c = 65535;
                            break;
                        default:
                            c = 65535;
                            break;
                    }
                    switch (c) {
                        case 0:
                            parseGrantUriPermission = parseGrantUriPermission(parsedProviderImpl, parsingPackage, resources, xmlResourceParser, parseInput);
                            break;
                        case 1:
                            parseGrantUriPermission = ParsedComponentUtils.addMetaData(parsedProviderImpl, parsingPackage, resources, xmlResourceParser, parseInput);
                            break;
                        case 2:
                            parseGrantUriPermission = ParsedMainComponentUtils.parseIntentFilter(parsedProviderImpl, parsingPackage, resources, xmlResourceParser, z, true, false, false, false, parseInput);
                            if (parseGrantUriPermission.isSuccess()) {
                                ParsedIntentInfoImpl parsedIntentInfoImpl = (ParsedIntentInfoImpl) parseGrantUriPermission.getResult();
                                parsedProviderImpl.setOrder(Math.max(parsedIntentInfoImpl.getIntentFilter().getOrder(), parsedProviderImpl.getOrder()));
                                parsedProviderImpl.addIntent(parsedIntentInfoImpl);
                                break;
                            }
                            break;
                        case 3:
                            parseGrantUriPermission = ParsedComponentUtils.addProperty(parsedProviderImpl, parsingPackage, resources, xmlResourceParser, parseInput);
                            break;
                        case 4:
                            parseGrantUriPermission = parsePathPermission(parsedProviderImpl, parsingPackage, resources, xmlResourceParser, parseInput);
                            break;
                        default:
                            parseGrantUriPermission = ParsingUtils.unknownTag(str, parsingPackage, xmlResourceParser, parseInput);
                            break;
                    }
                    if (parseGrantUriPermission.isError()) {
                        return parseInput.error(parseGrantUriPermission);
                    }
                }
            }
        }
        return parseInput.success(parsedProviderImpl);
    }

    public static ParseResult<ParsedProvider> parseGrantUriPermission(ParsedProviderImpl parsedProviderImpl, ParsingPackage parsingPackage, Resources resources, XmlResourceParser xmlResourceParser, ParseInput parseInput) {
        PatternMatcher patternMatcher;
        TypedArray obtainAttributes = resources.obtainAttributes(xmlResourceParser, R.styleable.AndroidManifestGrantUriPermission);
        try {
            String name = xmlResourceParser.getName();
            String nonConfigurationString = obtainAttributes.getNonConfigurationString(4, 0);
            if (nonConfigurationString != null) {
                patternMatcher = new PatternMatcher(nonConfigurationString, 3);
            } else {
                String nonConfigurationString2 = obtainAttributes.getNonConfigurationString(2, 0);
                if (nonConfigurationString2 != null) {
                    patternMatcher = new PatternMatcher(nonConfigurationString2, 2);
                } else {
                    String nonConfigurationString3 = obtainAttributes.getNonConfigurationString(1, 0);
                    if (nonConfigurationString3 != null) {
                        patternMatcher = new PatternMatcher(nonConfigurationString3, 1);
                    } else {
                        String nonConfigurationString4 = obtainAttributes.getNonConfigurationString(3, 0);
                        if (nonConfigurationString4 != null) {
                            patternMatcher = new PatternMatcher(nonConfigurationString4, 4);
                        } else {
                            String nonConfigurationString5 = obtainAttributes.getNonConfigurationString(0, 0);
                            patternMatcher = nonConfigurationString5 != null ? new PatternMatcher(nonConfigurationString5, 0) : null;
                        }
                    }
                }
            }
            if (patternMatcher != null) {
                parsedProviderImpl.addUriPermissionPattern(patternMatcher);
                parsedProviderImpl.setGrantUriPermissions(true);
            } else {
                Slog.w("PackageParsing", "Unknown element under <path-permission>: " + name + " at " + parsingPackage.getBaseApkPath() + " " + xmlResourceParser.getPositionDescription());
            }
            return parseInput.success(parsedProviderImpl);
        } finally {
            obtainAttributes.recycle();
        }
    }

    public static ParseResult<ParsedProvider> parsePathPermission(ParsedProviderImpl parsedProviderImpl, ParsingPackage parsingPackage, Resources resources, XmlResourceParser xmlResourceParser, ParseInput parseInput) {
        boolean z;
        PathPermission pathPermission;
        TypedArray obtainAttributes = resources.obtainAttributes(xmlResourceParser, R.styleable.AndroidManifestPathPermission);
        try {
            String name = xmlResourceParser.getName();
            String nonConfigurationString = obtainAttributes.getNonConfigurationString(0, 0);
            String nonConfigurationString2 = obtainAttributes.getNonConfigurationString(1, 0);
            if (nonConfigurationString2 == null) {
                nonConfigurationString2 = nonConfigurationString;
            }
            String nonConfigurationString3 = obtainAttributes.getNonConfigurationString(2, 0);
            if (nonConfigurationString3 != null) {
                nonConfigurationString = nonConfigurationString3;
            }
            if (nonConfigurationString2 != null) {
                nonConfigurationString2 = nonConfigurationString2.intern();
                z = true;
            } else {
                z = false;
            }
            if (nonConfigurationString != null) {
                nonConfigurationString = nonConfigurationString.intern();
                z = true;
            }
            if (!z) {
                Slog.w("PackageParsing", "No readPermission or writePermission for <path-permission>: " + name + " at " + parsingPackage.getBaseApkPath() + " " + xmlResourceParser.getPositionDescription());
                return parseInput.success(parsedProviderImpl);
            }
            String nonConfigurationString4 = obtainAttributes.getNonConfigurationString(7, 0);
            if (nonConfigurationString4 != null) {
                pathPermission = new PathPermission(nonConfigurationString4, 3, nonConfigurationString2, nonConfigurationString);
            } else {
                String nonConfigurationString5 = obtainAttributes.getNonConfigurationString(5, 0);
                if (nonConfigurationString5 != null) {
                    pathPermission = new PathPermission(nonConfigurationString5, 2, nonConfigurationString2, nonConfigurationString);
                } else {
                    String nonConfigurationString6 = obtainAttributes.getNonConfigurationString(4, 0);
                    if (nonConfigurationString6 != null) {
                        pathPermission = new PathPermission(nonConfigurationString6, 1, nonConfigurationString2, nonConfigurationString);
                    } else {
                        String nonConfigurationString7 = obtainAttributes.getNonConfigurationString(6, 0);
                        if (nonConfigurationString7 != null) {
                            pathPermission = new PathPermission(nonConfigurationString7, 4, nonConfigurationString2, nonConfigurationString);
                        } else {
                            String nonConfigurationString8 = obtainAttributes.getNonConfigurationString(3, 0);
                            pathPermission = nonConfigurationString8 != null ? new PathPermission(nonConfigurationString8, 0, nonConfigurationString2, nonConfigurationString) : null;
                        }
                    }
                }
            }
            if (pathPermission != null) {
                parsedProviderImpl.addPathPermission(pathPermission);
            } else {
                Slog.w("PackageParsing", "No path, pathPrefix, or pathPattern for <path-permission>: " + name + " at " + parsingPackage.getBaseApkPath() + " " + xmlResourceParser.getPositionDescription());
            }
            return parseInput.success(parsedProviderImpl);
        } finally {
            obtainAttributes.recycle();
        }
    }
}
