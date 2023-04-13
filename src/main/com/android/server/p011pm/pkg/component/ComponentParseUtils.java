package com.android.server.p011pm.pkg.component;

import android.content.IntentFilter;
import android.content.pm.parsing.FrameworkParsingPackageUtils;
import android.content.pm.parsing.result.ParseInput;
import android.content.pm.parsing.result.ParseResult;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.os.Bundle;
import android.text.TextUtils;
import com.android.server.p011pm.pkg.PackageUserState;
import com.android.server.p011pm.pkg.PackageUserStateUtils;
import com.android.server.p011pm.pkg.parsing.ParsingPackage;
import com.android.server.p011pm.pkg.parsing.ParsingUtils;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParserException;
/* renamed from: com.android.server.pm.pkg.component.ComponentParseUtils */
/* loaded from: classes2.dex */
public class ComponentParseUtils {
    public static boolean isImplicitlyExposedIntent(ParsedIntentInfo parsedIntentInfo) {
        IntentFilter intentFilter = parsedIntentInfo.getIntentFilter();
        return intentFilter.hasCategory("android.intent.category.BROWSABLE") || intentFilter.hasAction("android.intent.action.SEND") || intentFilter.hasAction("android.intent.action.SENDTO") || intentFilter.hasAction("android.intent.action.SEND_MULTIPLE");
    }

    public static <Component extends ParsedComponentImpl> ParseResult<Component> parseAllMetaData(ParsingPackage parsingPackage, Resources resources, XmlResourceParser xmlResourceParser, String str, Component component, ParseInput parseInput) throws XmlPullParserException, IOException {
        ParseResult<Bundle> unknownTag;
        int depth = xmlResourceParser.getDepth();
        while (true) {
            int next = xmlResourceParser.next();
            if (next == 1 || (next == 3 && xmlResourceParser.getDepth() <= depth)) {
                break;
            } else if (next == 2) {
                if ("meta-data".equals(xmlResourceParser.getName())) {
                    unknownTag = ParsedComponentUtils.addMetaData(component, parsingPackage, resources, xmlResourceParser, parseInput);
                } else {
                    unknownTag = ParsingUtils.unknownTag(str, parsingPackage, xmlResourceParser, parseInput);
                }
                if (unknownTag.isError()) {
                    return parseInput.error(unknownTag);
                }
            }
        }
        return parseInput.success(component);
    }

    public static ParseResult<String> buildProcessName(String str, String str2, CharSequence charSequence, int i, String[] strArr, ParseInput parseInput) {
        if ((i & 2) != 0 && !"system".contentEquals(charSequence)) {
            if (str2 != null) {
                str = str2;
            }
            return parseInput.success(str);
        }
        if (strArr != null) {
            for (int length = strArr.length - 1; length >= 0; length--) {
                String str3 = strArr[length];
                if (str3.equals(str) || str3.equals(str2) || str3.contentEquals(charSequence)) {
                    return parseInput.success(str);
                }
            }
        }
        if (charSequence == null || charSequence.length() <= 0) {
            return parseInput.success(str2);
        }
        return parseInput.success(TextUtils.safeIntern((String) buildCompoundName(str, charSequence, "process", parseInput).getResult()));
    }

    public static ParseResult<String> buildTaskAffinityName(String str, String str2, CharSequence charSequence, ParseInput parseInput) {
        if (charSequence == null) {
            return parseInput.success(str2);
        }
        if (charSequence.length() <= 0) {
            return parseInput.success((Object) null);
        }
        return buildCompoundName(str, charSequence, "taskAffinity", parseInput);
    }

    public static ParseResult<String> buildCompoundName(String str, CharSequence charSequence, String str2, ParseInput parseInput) {
        String charSequence2 = charSequence.toString();
        char charAt = charSequence2.charAt(0);
        if (str != null && charAt == ':') {
            if (charSequence2.length() < 2) {
                return parseInput.error("Bad " + str2 + " name " + charSequence2 + " in package " + str + ": must be at least two characters");
            }
            ParseResult validateName = FrameworkParsingPackageUtils.validateName(parseInput, charSequence2.substring(1), false, false);
            if (validateName.isError()) {
                return parseInput.error("Invalid " + str2 + " name " + charSequence2 + " in package " + str + ": " + validateName.getErrorMessage());
            }
            return parseInput.success(str + charSequence2);
        }
        if (!"system".equals(charSequence2)) {
            ParseResult validateName2 = FrameworkParsingPackageUtils.validateName(parseInput, charSequence2, true, false);
            if (validateName2.isError()) {
                return parseInput.error("Invalid " + str2 + " name " + charSequence2 + " in package " + str + ": " + validateName2.getErrorMessage());
            }
        }
        return parseInput.success(charSequence2);
    }

    public static int flag(int i, int i2, TypedArray typedArray) {
        if (typedArray.getBoolean(i2, false)) {
            return i;
        }
        return 0;
    }

    public static int flag(int i, int i2, boolean z, TypedArray typedArray) {
        if (typedArray.getBoolean(i2, z)) {
            return i;
        }
        return 0;
    }

    public static CharSequence getNonLocalizedLabel(ParsedComponent parsedComponent) {
        return parsedComponent.getNonLocalizedLabel();
    }

    public static int getIcon(ParsedComponent parsedComponent) {
        return parsedComponent.getIcon();
    }

    public static boolean isMatch(PackageUserState packageUserState, boolean z, boolean z2, ParsedMainComponent parsedMainComponent, long j) {
        return PackageUserStateUtils.isMatch(packageUserState, z, z2, parsedMainComponent.isEnabled(), parsedMainComponent.isDirectBootAware(), parsedMainComponent.getName(), j);
    }
}
