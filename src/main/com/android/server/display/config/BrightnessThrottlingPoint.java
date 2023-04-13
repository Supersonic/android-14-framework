package com.android.server.display.config;

import java.io.IOException;
import java.math.BigDecimal;
import javax.xml.datatype.DatatypeConfigurationException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes.dex */
public class BrightnessThrottlingPoint {
    public BigDecimal brightness;
    public ThermalStatus thermalStatus;

    public final ThermalStatus getThermalStatus() {
        return this.thermalStatus;
    }

    public final void setThermalStatus(ThermalStatus thermalStatus) {
        this.thermalStatus = thermalStatus;
    }

    public final BigDecimal getBrightness() {
        return this.brightness;
    }

    public final void setBrightness(BigDecimal bigDecimal) {
        this.brightness = bigDecimal;
    }

    public static BrightnessThrottlingPoint read(XmlPullParser xmlPullParser) throws XmlPullParserException, IOException, DatatypeConfigurationException {
        int next;
        BrightnessThrottlingPoint brightnessThrottlingPoint = new BrightnessThrottlingPoint();
        xmlPullParser.getDepth();
        while (true) {
            next = xmlPullParser.next();
            if (next == 1 || next == 3) {
                break;
            } else if (xmlPullParser.getEventType() == 2) {
                String name = xmlPullParser.getName();
                if (name.equals("thermalStatus")) {
                    brightnessThrottlingPoint.setThermalStatus(ThermalStatus.fromString(XmlParser.readText(xmlPullParser)));
                } else if (name.equals("brightness")) {
                    brightnessThrottlingPoint.setBrightness(new BigDecimal(XmlParser.readText(xmlPullParser)));
                } else {
                    XmlParser.skip(xmlPullParser);
                }
            }
        }
        if (next == 3) {
            return brightnessThrottlingPoint;
        }
        throw new DatatypeConfigurationException("BrightnessThrottlingPoint is not closed");
    }
}
