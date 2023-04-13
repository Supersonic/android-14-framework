package com.android.server.display.config;

import java.io.IOException;
import javax.xml.datatype.DatatypeConfigurationException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes.dex */
public class RefreshRateThrottlingPoint {
    public RefreshRateRange refreshRateRange;
    public ThermalStatus thermalStatus;

    public final ThermalStatus getThermalStatus() {
        return this.thermalStatus;
    }

    public final void setThermalStatus(ThermalStatus thermalStatus) {
        this.thermalStatus = thermalStatus;
    }

    public final RefreshRateRange getRefreshRateRange() {
        return this.refreshRateRange;
    }

    public final void setRefreshRateRange(RefreshRateRange refreshRateRange) {
        this.refreshRateRange = refreshRateRange;
    }

    public static RefreshRateThrottlingPoint read(XmlPullParser xmlPullParser) throws XmlPullParserException, IOException, DatatypeConfigurationException {
        int next;
        RefreshRateThrottlingPoint refreshRateThrottlingPoint = new RefreshRateThrottlingPoint();
        xmlPullParser.getDepth();
        while (true) {
            next = xmlPullParser.next();
            if (next == 1 || next == 3) {
                break;
            } else if (xmlPullParser.getEventType() == 2) {
                String name = xmlPullParser.getName();
                if (name.equals("thermalStatus")) {
                    refreshRateThrottlingPoint.setThermalStatus(ThermalStatus.fromString(XmlParser.readText(xmlPullParser)));
                } else if (name.equals("refreshRateRange")) {
                    refreshRateThrottlingPoint.setRefreshRateRange(RefreshRateRange.read(xmlPullParser));
                } else {
                    XmlParser.skip(xmlPullParser);
                }
            }
        }
        if (next == 3) {
            return refreshRateThrottlingPoint;
        }
        throw new DatatypeConfigurationException("RefreshRateThrottlingPoint is not closed");
    }
}
