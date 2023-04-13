package com.android.server.display.config;

import java.io.IOException;
import java.math.BigInteger;
import javax.xml.datatype.DatatypeConfigurationException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes.dex */
public class RefreshRateRange {
    public BigInteger maximum;
    public BigInteger minimum;

    public final BigInteger getMinimum() {
        return this.minimum;
    }

    public final void setMinimum(BigInteger bigInteger) {
        this.minimum = bigInteger;
    }

    public final BigInteger getMaximum() {
        return this.maximum;
    }

    public final void setMaximum(BigInteger bigInteger) {
        this.maximum = bigInteger;
    }

    public static RefreshRateRange read(XmlPullParser xmlPullParser) throws XmlPullParserException, IOException, DatatypeConfigurationException {
        int next;
        RefreshRateRange refreshRateRange = new RefreshRateRange();
        xmlPullParser.getDepth();
        while (true) {
            next = xmlPullParser.next();
            if (next == 1 || next == 3) {
                break;
            } else if (xmlPullParser.getEventType() == 2) {
                String name = xmlPullParser.getName();
                if (name.equals("minimum")) {
                    refreshRateRange.setMinimum(new BigInteger(XmlParser.readText(xmlPullParser)));
                } else if (name.equals("maximum")) {
                    refreshRateRange.setMaximum(new BigInteger(XmlParser.readText(xmlPullParser)));
                } else {
                    XmlParser.skip(xmlPullParser);
                }
            }
        }
        if (next == 3) {
            return refreshRateRange;
        }
        throw new DatatypeConfigurationException("RefreshRateRange is not closed");
    }
}
