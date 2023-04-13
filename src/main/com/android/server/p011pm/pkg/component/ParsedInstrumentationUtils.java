package com.android.server.p011pm.pkg.component;

import android.content.pm.parsing.result.ParseInput;
import android.content.pm.parsing.result.ParseResult;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import com.android.internal.R;
import com.android.server.p011pm.pkg.parsing.ParsingPackage;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParserException;
/* renamed from: com.android.server.pm.pkg.component.ParsedInstrumentationUtils */
/* loaded from: classes2.dex */
public class ParsedInstrumentationUtils {
    public static ParseResult<ParsedInstrumentation> parseInstrumentation(ParsingPackage parsingPackage, Resources resources, XmlResourceParser xmlResourceParser, boolean z, ParseInput parseInput) throws IOException, XmlPullParserException {
        TypedArray typedArray;
        ParsedInstrumentationImpl parsedInstrumentationImpl = new ParsedInstrumentationImpl();
        String str = "<" + xmlResourceParser.getName() + ">";
        TypedArray obtainAttributes = resources.obtainAttributes(xmlResourceParser, R.styleable.AndroidManifestInstrumentation);
        try {
            ParseResult parseComponent = ParsedComponentUtils.parseComponent(parsedInstrumentationImpl, str, parsingPackage, obtainAttributes, z, parseInput, 7, -1, 1, 0, 6, 2, 8);
            if (parseComponent.isError()) {
                ParseResult<ParsedInstrumentation> error = parseInput.error(parseComponent);
                obtainAttributes.recycle();
                return error;
            }
            typedArray = obtainAttributes;
            try {
                parsedInstrumentationImpl.setTargetPackage(typedArray.getNonResourceString(3)).setTargetProcesses(typedArray.getNonResourceString(9)).setHandleProfiling(typedArray.getBoolean(4, false)).setFunctionalTest(typedArray.getBoolean(5, false));
                typedArray.recycle();
                ParseResult parseAllMetaData = ComponentParseUtils.parseAllMetaData(parsingPackage, resources, xmlResourceParser, str, parsedInstrumentationImpl, parseInput);
                if (parseAllMetaData.isError()) {
                    return parseInput.error(parseAllMetaData);
                }
                return parseInput.success((ParsedInstrumentation) parseAllMetaData.getResult());
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
