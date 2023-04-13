package com.android.server.p011pm.pkg.component;

import android.R;
import android.content.pm.parsing.result.ParseInput;
import android.content.pm.parsing.result.ParseResult;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.text.TextUtils;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParserException;
/* renamed from: com.android.server.pm.pkg.component.ParsedApexSystemServiceUtils */
/* loaded from: classes2.dex */
public class ParsedApexSystemServiceUtils {
    public static ParseResult<ParsedApexSystemService> parseApexSystemService(Resources resources, XmlResourceParser xmlResourceParser, ParseInput parseInput) throws XmlPullParserException, IOException {
        ParsedApexSystemServiceImpl parsedApexSystemServiceImpl = new ParsedApexSystemServiceImpl();
        TypedArray obtainAttributes = resources.obtainAttributes(xmlResourceParser, R.styleable.AndroidManifestApexSystemService);
        try {
            String string = obtainAttributes.getString(0);
            if (TextUtils.isEmpty(string)) {
                return parseInput.error("<apex-system-service> does not have name attribute");
            }
            String string2 = obtainAttributes.getString(2);
            String string3 = obtainAttributes.getString(3);
            String string4 = obtainAttributes.getString(4);
            parsedApexSystemServiceImpl.setName(string).setMinSdkVersion(string3).setMaxSdkVersion(string4).setInitOrder(obtainAttributes.getInt(1, 0));
            if (!TextUtils.isEmpty(string2)) {
                parsedApexSystemServiceImpl.setJarPath(string2);
            }
            return parseInput.success(parsedApexSystemServiceImpl);
        } finally {
            obtainAttributes.recycle();
        }
    }
}
