package com.android.server.integrity.parser;

import android.util.Xml;
import com.android.modules.utils.TypedXmlPullParser;
import com.android.server.integrity.model.RuleMetadata;
import java.io.IOException;
import java.io.InputStream;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes.dex */
public class RuleMetadataParser {
    public static RuleMetadata parse(InputStream inputStream) throws XmlPullParserException, IOException {
        TypedXmlPullParser resolvePullParser = Xml.resolvePullParser(inputStream);
        String str = "";
        String str2 = "";
        while (true) {
            int next = resolvePullParser.next();
            if (next == 1) {
                return new RuleMetadata(str, str2);
            }
            if (next == 2) {
                String name = resolvePullParser.getName();
                name.hashCode();
                if (name.equals("P")) {
                    str = resolvePullParser.nextText();
                } else if (name.equals("V")) {
                    str2 = resolvePullParser.nextText();
                } else {
                    throw new IllegalStateException("Unknown tag in metadata: " + name);
                }
            }
        }
    }
}
