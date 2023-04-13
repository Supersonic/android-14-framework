package com.android.server.firewall;

import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes.dex */
public class PortFilter implements Filter {
    public static final FilterFactory FACTORY = new FilterFactory("port") { // from class: com.android.server.firewall.PortFilter.1
        @Override // com.android.server.firewall.FilterFactory
        public Filter newFilter(XmlPullParser xmlPullParser) throws IOException, XmlPullParserException {
            int parseInt;
            String attributeValue = xmlPullParser.getAttributeValue(null, "equals");
            if (attributeValue != null) {
                try {
                    parseInt = Integer.parseInt(attributeValue);
                } catch (NumberFormatException unused) {
                    throw new XmlPullParserException("Invalid port value: " + attributeValue, xmlPullParser, null);
                }
            } else {
                parseInt = -1;
            }
            int i = parseInt;
            String attributeValue2 = xmlPullParser.getAttributeValue(null, "min");
            String attributeValue3 = xmlPullParser.getAttributeValue(null, "max");
            if (attributeValue2 != null || attributeValue3 != null) {
                if (attributeValue != null) {
                    throw new XmlPullParserException("Port filter cannot use both equals and range filtering", xmlPullParser, null);
                }
                if (attributeValue2 != null) {
                    try {
                        parseInt = Integer.parseInt(attributeValue2);
                    } catch (NumberFormatException unused2) {
                        throw new XmlPullParserException("Invalid minimum port value: " + attributeValue2, xmlPullParser, null);
                    }
                }
                if (attributeValue3 != null) {
                    try {
                        i = Integer.parseInt(attributeValue3);
                    } catch (NumberFormatException unused3) {
                        throw new XmlPullParserException("Invalid maximum port value: " + attributeValue3, xmlPullParser, null);
                    }
                }
            }
            return new PortFilter(parseInt, i);
        }
    };
    public final int mLowerBound;
    public final int mUpperBound;

    public PortFilter(int i, int i2) {
        this.mLowerBound = i;
        this.mUpperBound = i2;
    }

    @Override // com.android.server.firewall.Filter
    public boolean matches(IntentFirewall intentFirewall, ComponentName componentName, Intent intent, int i, int i2, String str, int i3) {
        int i4;
        int i5;
        Uri data = intent.getData();
        int port = data != null ? data.getPort() : -1;
        return port != -1 && ((i4 = this.mLowerBound) == -1 || i4 <= port) && ((i5 = this.mUpperBound) == -1 || i5 >= port);
    }
}
