package com.android.server.p011pm.pkg.component;

import android.content.pm.parsing.result.ParseInput;
import android.content.pm.parsing.result.ParseResult;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import com.android.internal.R;
import com.android.server.p011pm.pkg.parsing.ParsingPackage;
import com.android.server.p011pm.pkg.parsing.ParsingUtils;
import java.io.IOException;
import java.util.Objects;
import org.xmlpull.v1.XmlPullParserException;
/* renamed from: com.android.server.pm.pkg.component.ParsedServiceUtils */
/* loaded from: classes2.dex */
public class ParsedServiceUtils {
    public static ParseResult<ParsedService> parseService(String[] strArr, ParsingPackage parsingPackage, Resources resources, XmlResourceParser xmlResourceParser, int i, boolean z, String str, ParseInput parseInput) throws XmlPullParserException, IOException {
        TypedArray typedArray;
        ParsedServiceImpl parsedServiceImpl;
        int i2;
        int i3;
        int i4;
        String str2;
        ParseResult addMetaData;
        XmlResourceParser xmlResourceParser2;
        String str3;
        String packageName = parsingPackage.getPackageName();
        ParsedServiceImpl parsedServiceImpl2 = new ParsedServiceImpl();
        String name = xmlResourceParser.getName();
        TypedArray obtainAttributes = resources.obtainAttributes(xmlResourceParser, R.styleable.AndroidManifestService);
        String str4 = name;
        try {
            ParseResult parseMainComponent = ParsedMainComponentUtils.parseMainComponent(parsedServiceImpl2, name, strArr, parsingPackage, obtainAttributes, i, z, str, parseInput, 12, 7, 13, 4, 1, 0, 8, 2, 6, 15, 17, 20);
            if (parseMainComponent.isError()) {
                ParseResult<ParsedService> error = parseInput.error(parseMainComponent);
                obtainAttributes.recycle();
                return error;
            }
            typedArray = obtainAttributes;
            try {
                boolean hasValue = typedArray.hasValue(5);
                int i5 = 0;
                if (hasValue) {
                    parsedServiceImpl = parsedServiceImpl2;
                    parsedServiceImpl.setExported(typedArray.getBoolean(5, false));
                } else {
                    parsedServiceImpl = parsedServiceImpl2;
                }
                int i6 = 3;
                String nonConfigurationString = typedArray.getNonConfigurationString(3, 0);
                if (nonConfigurationString == null) {
                    nonConfigurationString = parsingPackage.getPermission();
                }
                parsedServiceImpl.setPermission(nonConfigurationString);
                boolean z2 = 1;
                int i7 = 2;
                parsedServiceImpl.setForegroundServiceType(typedArray.getInt(19, 0)).setFlags(parsedServiceImpl.getFlags() | ComponentParseUtils.flag(1, 9, typedArray) | ComponentParseUtils.flag(2, 10, typedArray) | ComponentParseUtils.flag(4, 14, typedArray) | ComponentParseUtils.flag(8, 18, typedArray) | ComponentParseUtils.flag(16, 21, typedArray) | ComponentParseUtils.flag(1073741824, 11, typedArray));
                boolean z3 = typedArray.getBoolean(16, false);
                if (z3) {
                    parsedServiceImpl.setFlags(parsedServiceImpl.getFlags() | 1048576);
                    parsingPackage.setVisibleToInstantApps(true);
                }
                typedArray.recycle();
                if (parsingPackage.isSaveStateDisallowed() && Objects.equals(parsedServiceImpl.getProcessName(), packageName)) {
                    return parseInput.error("Heavy-weight applications can not have services in main process");
                }
                int depth = xmlResourceParser.getDepth();
                while (true) {
                    int next = xmlResourceParser.next();
                    if (next != z2 && (next != i6 || xmlResourceParser.getDepth() > depth)) {
                        if (next == i7) {
                            String name2 = xmlResourceParser.getName();
                            name2.hashCode();
                            int i8 = -1;
                            switch (name2.hashCode()) {
                                case -1115949454:
                                    if (name2.equals("meta-data")) {
                                        i8 = i5;
                                        break;
                                    }
                                    break;
                                case -1029793847:
                                    if (name2.equals("intent-filter")) {
                                        i8 = z2;
                                        break;
                                    }
                                    break;
                                case -993141291:
                                    if (name2.equals("property")) {
                                        i8 = i7;
                                        break;
                                    }
                                    break;
                            }
                            switch (i8) {
                                case 0:
                                    i2 = depth;
                                    i3 = i7;
                                    i4 = z2;
                                    str2 = str4;
                                    addMetaData = ParsedComponentUtils.addMetaData(parsedServiceImpl, parsingPackage, resources, xmlResourceParser, parseInput);
                                    break;
                                case 1:
                                    str2 = str4;
                                    i2 = depth;
                                    i3 = i7;
                                    i4 = z2;
                                    addMetaData = ParsedMainComponentUtils.parseIntentFilter(parsedServiceImpl, parsingPackage, resources, xmlResourceParser, z3, true, false, false, false, parseInput);
                                    if (addMetaData.isSuccess()) {
                                        ParsedIntentInfoImpl parsedIntentInfoImpl = (ParsedIntentInfoImpl) addMetaData.getResult();
                                        parsedServiceImpl.setOrder(Math.max(parsedIntentInfoImpl.getIntentFilter().getOrder(), parsedServiceImpl.getOrder()));
                                        parsedServiceImpl.addIntent(parsedIntentInfoImpl);
                                        break;
                                    }
                                    break;
                                case 2:
                                    xmlResourceParser2 = xmlResourceParser;
                                    str3 = str4;
                                    addMetaData = ParsedComponentUtils.addProperty(parsedServiceImpl, parsingPackage, resources, xmlResourceParser2, parseInput);
                                    str2 = str3;
                                    i2 = depth;
                                    i3 = i7;
                                    i4 = z2;
                                    break;
                                default:
                                    xmlResourceParser2 = xmlResourceParser;
                                    str3 = str4;
                                    addMetaData = ParsingUtils.unknownTag(str3, parsingPackage, xmlResourceParser2, parseInput);
                                    str2 = str3;
                                    i2 = depth;
                                    i3 = i7;
                                    i4 = z2;
                                    break;
                            }
                            if (addMetaData.isError()) {
                                return parseInput.error(addMetaData);
                            }
                            depth = i2;
                            i7 = i3;
                            z2 = i4;
                            str4 = str2;
                            i5 = 0;
                            i6 = 3;
                        }
                    }
                }
                boolean z4 = z2;
                if (!hasValue) {
                    boolean z5 = parsedServiceImpl.getIntents().size() > 0 ? z4 : false;
                    if (z5) {
                        ParseResult deferError = parseInput.deferError(parsedServiceImpl.getName() + ": Targeting S+ (version 31 and above) requires that an explicit value for android:exported be defined when intent filters are present", 150232615L);
                        if (deferError.isError()) {
                            return parseInput.error(deferError);
                        }
                    }
                    parsedServiceImpl.setExported(z5);
                }
                return parseInput.success(parsedServiceImpl);
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
}
