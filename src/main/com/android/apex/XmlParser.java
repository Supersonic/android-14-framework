package com.android.apex;

import java.io.IOException;
import java.io.InputStream;
import javax.xml.datatype.DatatypeConfigurationException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
/* loaded from: classes4.dex */
public class XmlParser {
    public static ApexInfoList readApexInfoList(InputStream in) throws XmlPullParserException, IOException, DatatypeConfigurationException {
        XmlPullParser _parser = XmlPullParserFactory.newInstance().newPullParser();
        _parser.setFeature("http://xmlpull.org/v1/doc/features.html#process-namespaces", true);
        _parser.setInput(in, null);
        _parser.nextTag();
        String _tagName = _parser.getName();
        if (!_tagName.equals("apex-info-list")) {
            return null;
        }
        ApexInfoList _value = ApexInfoList.read(_parser);
        return _value;
    }

    public static String readText(XmlPullParser _parser) throws XmlPullParserException, IOException {
        if (_parser.next() != 4) {
            return "";
        }
        String result = _parser.getText();
        _parser.nextTag();
        return result;
    }

    public static void skip(XmlPullParser _parser) throws XmlPullParserException, IOException {
        if (_parser.getEventType() != 2) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (_parser.next()) {
                case 2:
                    depth++;
                    break;
                case 3:
                    depth--;
                    break;
            }
        }
    }
}
