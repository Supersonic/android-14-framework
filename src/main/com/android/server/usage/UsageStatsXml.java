package com.android.server.usage;

import android.util.Slog;
import android.util.Xml;
import com.android.internal.util.XmlUtils;
import java.io.IOException;
import java.io.InputStream;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes2.dex */
public class UsageStatsXml {
    public static void read(InputStream inputStream, IntervalStats intervalStats) throws IOException {
        XmlPullParser newPullParser = Xml.newPullParser();
        try {
            newPullParser.setInput(inputStream, "utf-8");
            XmlUtils.beginDocument(newPullParser, "usagestats");
            String attributeValue = newPullParser.getAttributeValue(null, "version");
            try {
                if (Integer.parseInt(attributeValue) == 1) {
                    UsageStatsXmlV1.read(newPullParser, intervalStats);
                    return;
                }
                Slog.e("UsageStatsXml", "Unrecognized version " + attributeValue);
                throw new IOException("Unrecognized version " + attributeValue);
            } catch (NumberFormatException e) {
                Slog.e("UsageStatsXml", "Bad version");
                throw new IOException(e);
            }
        } catch (XmlPullParserException e2) {
            Slog.e("UsageStatsXml", "Failed to parse Xml", e2);
            throw new IOException(e2);
        }
    }
}
