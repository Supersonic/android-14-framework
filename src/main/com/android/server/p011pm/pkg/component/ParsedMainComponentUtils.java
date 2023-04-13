package com.android.server.p011pm.pkg.component;

import android.content.IntentFilter;
import android.content.pm.parsing.result.ParseInput;
import android.content.pm.parsing.result.ParseResult;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.util.Slog;
import com.android.internal.annotations.VisibleForTesting;
import com.android.server.p011pm.pkg.parsing.ParsingPackage;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParserException;
/* renamed from: com.android.server.pm.pkg.component.ParsedMainComponentUtils */
/* loaded from: classes2.dex */
public class ParsedMainComponentUtils {
    /* JADX WARN: Removed duplicated region for block: B:13:0x0059  */
    /* JADX WARN: Removed duplicated region for block: B:16:0x0064  */
    /* JADX WARN: Removed duplicated region for block: B:25:0x00a8  */
    /* JADX WARN: Removed duplicated region for block: B:28:0x00ae  */
    @VisibleForTesting(visibility = VisibleForTesting.Visibility.PACKAGE)
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public static <Component extends ParsedMainComponentImpl> ParseResult<Component> parseMainComponent(Component component, String str, String[] strArr, ParsingPackage parsingPackage, TypedArray typedArray, int i, boolean z, String str2, ParseInput parseInput, int i2, int i3, int i4, int i5, int i6, int i7, int i8, int i9, int i10, int i11, int i12, int i13) {
        ParseInput parseInput2;
        String nonConfigurationString;
        String nonResourceString;
        ParseResult<Component> parseComponent = ParsedComponentUtils.parseComponent(component, str, parsingPackage, typedArray, z, parseInput, i2, i3, i6, i7, i8, i9, i11);
        if (parseComponent.isError()) {
            return parseComponent;
        }
        if (i4 != -1) {
            component.setDirectBootAware(typedArray.getBoolean(i4, false));
            if (component.isDirectBootAware()) {
                parsingPackage.setPartiallyDirectBootAware(true);
                if (i5 != -1) {
                    component.setEnabled(typedArray.getBoolean(i5, true));
                }
                if (i10 == -1) {
                    if (parsingPackage.getTargetSdkVersion() >= 8) {
                        nonResourceString = typedArray.getNonConfigurationString(i10, 1024);
                    } else {
                        nonResourceString = typedArray.getNonResourceString(i10);
                    }
                    ParseResult<String> buildProcessName = ComponentParseUtils.buildProcessName(parsingPackage.getPackageName(), parsingPackage.getProcessName(), nonResourceString, i, strArr, parseInput);
                    if (buildProcessName.isError()) {
                        return parseInput.error(buildProcessName);
                    }
                    parseInput2 = parseInput;
                    component.setProcessName((String) buildProcessName.getResult());
                } else {
                    parseInput2 = parseInput;
                }
                if (i12 != -1) {
                    component.setSplitName(typedArray.getNonConfigurationString(i12, 0));
                }
                if (str2 != null && component.getSplitName() == null) {
                    component.setSplitName(str2);
                }
                if (i13 != -1 && (nonConfigurationString = typedArray.getNonConfigurationString(i13, 0)) != null) {
                    component.setAttributionTags(nonConfigurationString.split("\\|"));
                }
                return parseInput2.success(component);
            }
        }
        if (i5 != -1) {
        }
        if (i10 == -1) {
        }
        if (i12 != -1) {
        }
        if (str2 != null) {
            component.setSplitName(str2);
        }
        if (i13 != -1) {
            component.setAttributionTags(nonConfigurationString.split("\\|"));
        }
        return parseInput2.success(component);
    }

    public static ParseResult<ParsedIntentInfoImpl> parseIntentFilter(ParsedMainComponent parsedMainComponent, ParsingPackage parsingPackage, Resources resources, XmlResourceParser xmlResourceParser, boolean z, boolean z2, boolean z3, boolean z4, boolean z5, ParseInput parseInput) throws IOException, XmlPullParserException {
        int i;
        ParseResult<ParsedIntentInfoImpl> parseIntentInfo = ParsedIntentInfoUtils.parseIntentInfo(parsedMainComponent.getName(), parsingPackage, resources, xmlResourceParser, z2, z3, parseInput);
        if (parseIntentInfo.isError()) {
            return parseInput.error(parseIntentInfo);
        }
        ParsedIntentInfo parsedIntentInfo = (ParsedIntentInfo) parseIntentInfo.getResult();
        IntentFilter intentFilter = parsedIntentInfo.getIntentFilter();
        if (intentFilter.countActions() == 0 && z5) {
            Slog.w("PackageParsing", "No actions in " + xmlResourceParser.getName() + " at " + parsingPackage.getBaseApkPath() + " " + xmlResourceParser.getPositionDescription());
            return parseInput.success((Object) null);
        }
        if (z) {
            i = 1;
        } else {
            i = (z4 && ComponentParseUtils.isImplicitlyExposedIntent(parsedIntentInfo)) ? 2 : 0;
        }
        intentFilter.setVisibilityToInstantApp(i);
        return parseInput.success((ParsedIntentInfoImpl) parseIntentInfo.getResult());
    }
}
