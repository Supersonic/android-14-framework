package com.android.server.display.config;

import java.io.IOException;
import java.math.BigDecimal;
import javax.xml.datatype.DatatypeConfigurationException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes.dex */
public class ThresholdPoint {
    public BigDecimal percentage;
    public BigDecimal threshold;

    public final BigDecimal getThreshold() {
        return this.threshold;
    }

    public final void setThreshold(BigDecimal bigDecimal) {
        this.threshold = bigDecimal;
    }

    public final BigDecimal getPercentage() {
        return this.percentage;
    }

    public final void setPercentage(BigDecimal bigDecimal) {
        this.percentage = bigDecimal;
    }

    public static ThresholdPoint read(XmlPullParser xmlPullParser) throws XmlPullParserException, IOException, DatatypeConfigurationException {
        int next;
        ThresholdPoint thresholdPoint = new ThresholdPoint();
        xmlPullParser.getDepth();
        while (true) {
            next = xmlPullParser.next();
            if (next == 1 || next == 3) {
                break;
            } else if (xmlPullParser.getEventType() == 2) {
                String name = xmlPullParser.getName();
                if (name.equals("threshold")) {
                    thresholdPoint.setThreshold(new BigDecimal(XmlParser.readText(xmlPullParser)));
                } else if (name.equals("percentage")) {
                    thresholdPoint.setPercentage(new BigDecimal(XmlParser.readText(xmlPullParser)));
                } else {
                    XmlParser.skip(xmlPullParser);
                }
            }
        }
        if (next == 3) {
            return thresholdPoint;
        }
        throw new DatatypeConfigurationException("ThresholdPoint is not closed");
    }
}
