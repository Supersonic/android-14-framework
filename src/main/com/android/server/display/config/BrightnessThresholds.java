package com.android.server.display.config;

import java.io.IOException;
import java.math.BigDecimal;
import javax.xml.datatype.DatatypeConfigurationException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes.dex */
public class BrightnessThresholds {
    public ThresholdPoints brightnessThresholdPoints;
    public BigDecimal minimum;

    public final BigDecimal getMinimum() {
        return this.minimum;
    }

    public final void setMinimum(BigDecimal bigDecimal) {
        this.minimum = bigDecimal;
    }

    public final ThresholdPoints getBrightnessThresholdPoints() {
        return this.brightnessThresholdPoints;
    }

    public final void setBrightnessThresholdPoints(ThresholdPoints thresholdPoints) {
        this.brightnessThresholdPoints = thresholdPoints;
    }

    public static BrightnessThresholds read(XmlPullParser xmlPullParser) throws XmlPullParserException, IOException, DatatypeConfigurationException {
        int next;
        BrightnessThresholds brightnessThresholds = new BrightnessThresholds();
        xmlPullParser.getDepth();
        while (true) {
            next = xmlPullParser.next();
            if (next == 1 || next == 3) {
                break;
            } else if (xmlPullParser.getEventType() == 2) {
                String name = xmlPullParser.getName();
                if (name.equals("minimum")) {
                    brightnessThresholds.setMinimum(new BigDecimal(XmlParser.readText(xmlPullParser)));
                } else if (name.equals("brightnessThresholdPoints")) {
                    brightnessThresholds.setBrightnessThresholdPoints(ThresholdPoints.read(xmlPullParser));
                } else {
                    XmlParser.skip(xmlPullParser);
                }
            }
        }
        if (next == 3) {
            return brightnessThresholds;
        }
        throw new DatatypeConfigurationException("BrightnessThresholds is not closed");
    }
}
