package com.android.server.p011pm.pkg.component;

import android.content.pm.parsing.result.ParseInput;
import android.content.pm.parsing.result.ParseResult;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.os.Build;
import android.util.ArraySet;
import com.android.internal.R;
import com.android.server.SystemConfig;
import com.android.server.p011pm.pkg.parsing.ParsingPackage;
import java.io.IOException;
import java.util.Set;
import org.xmlpull.v1.XmlPullParserException;
/* renamed from: com.android.server.pm.pkg.component.InstallConstraintsTagParser */
/* loaded from: classes2.dex */
public class InstallConstraintsTagParser {
    public static ParseResult<ParsingPackage> parseInstallConstraints(ParseInput parseInput, ParsingPackage parsingPackage, Resources resources, XmlResourceParser xmlResourceParser) throws XmlPullParserException, IOException {
        if (!SystemConfig.getInstance().getInstallConstraintsAllowlist().contains(parsingPackage.getPackageName())) {
            return parseInput.skip("install-constraints cannot be used by this package");
        }
        ParseResult<Set<String>> parseFingerprintPrefixes = parseFingerprintPrefixes(parseInput, resources, xmlResourceParser);
        if (parseFingerprintPrefixes.isSuccess()) {
            if (validateFingerprintPrefixes((Set) parseFingerprintPrefixes.getResult())) {
                return parseInput.success(parsingPackage);
            }
            return parseInput.skip("Install of this package is restricted on this device; device fingerprint does not start with one of the allowed prefixes");
        }
        return parseInput.skip(parseFingerprintPrefixes.getErrorMessage());
    }

    public static ParseResult<Set<String>> parseFingerprintPrefixes(ParseInput parseInput, Resources resources, XmlResourceParser xmlResourceParser) throws XmlPullParserException, IOException {
        ArraySet arraySet = new ArraySet();
        while (true) {
            int next = xmlResourceParser.next();
            if (next == 3) {
                if (arraySet.size() == 0) {
                    return parseInput.error("install-constraints must contain at least one constraint");
                }
                return parseInput.success(arraySet);
            } else if (next == 2) {
                if (xmlResourceParser.getName().equals("fingerprint-prefix")) {
                    ParseResult<String> readFingerprintPrefixValue = readFingerprintPrefixValue(parseInput, resources, xmlResourceParser);
                    if (readFingerprintPrefixValue.isSuccess()) {
                        arraySet.add((String) readFingerprintPrefixValue.getResult());
                        int next2 = xmlResourceParser.next();
                        if (next2 != 3) {
                            return parseInput.error("Expected end tag; instead got " + next2);
                        }
                    } else {
                        return parseInput.error(readFingerprintPrefixValue.getErrorMessage());
                    }
                } else {
                    return parseInput.error("Unexpected tag: " + xmlResourceParser.getName());
                }
            }
        }
    }

    public static ParseResult<String> readFingerprintPrefixValue(ParseInput parseInput, Resources resources, XmlResourceParser xmlResourceParser) {
        TypedArray obtainAttributes = resources.obtainAttributes(xmlResourceParser, R.styleable.AndroidManifestInstallConstraintsFingerprintPrefix);
        try {
            String string = obtainAttributes.getString(0);
            if (string == null) {
                return parseInput.error("Failed to specify prefix value");
            }
            return parseInput.success(string);
        } finally {
            obtainAttributes.recycle();
        }
    }

    public static boolean validateFingerprintPrefixes(Set<String> set) {
        String str = Build.FINGERPRINT;
        for (String str2 : set) {
            if (str.startsWith(str2)) {
                return true;
            }
        }
        return false;
    }
}
