package com.android.server.display.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.datatype.DatatypeConfigurationException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes.dex */
public class ThresholdPoints {
    public List<ThresholdPoint> brightnessThresholdPoint;

    public final List<ThresholdPoint> getBrightnessThresholdPoint() {
        if (this.brightnessThresholdPoint == null) {
            this.brightnessThresholdPoint = new ArrayList();
        }
        return this.brightnessThresholdPoint;
    }

    public static ThresholdPoints read(XmlPullParser xmlPullParser) throws XmlPullParserException, IOException, DatatypeConfigurationException {
        int next;
        ThresholdPoints thresholdPoints = new ThresholdPoints();
        xmlPullParser.getDepth();
        while (true) {
            next = xmlPullParser.next();
            if (next == 1 || next == 3) {
                break;
            } else if (xmlPullParser.getEventType() == 2) {
                if (xmlPullParser.getName().equals("brightnessThresholdPoint")) {
                    thresholdPoints.getBrightnessThresholdPoint().add(ThresholdPoint.read(xmlPullParser));
                } else {
                    XmlParser.skip(xmlPullParser);
                }
            }
        }
        if (next == 3) {
            return thresholdPoints;
        }
        throw new DatatypeConfigurationException("ThresholdPoints is not closed");
    }
}
