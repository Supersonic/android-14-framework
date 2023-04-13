package com.android.server.display.config.layout;

import java.io.IOException;
import java.math.BigInteger;
import javax.xml.datatype.DatatypeConfigurationException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes.dex */
public class Display {
    public BigInteger address;
    public String brightnessThrottlingMapId;
    public Boolean defaultDisplay;
    public String displayGroup;
    public Boolean enabled;
    public String position;
    public String refreshRateThermalThrottlingMapId;
    public String refreshRateZoneId;

    public BigInteger getAddress() {
        return this.address;
    }

    public void setAddress(BigInteger bigInteger) {
        this.address = bigInteger;
    }

    public String getPosition() {
        return this.position;
    }

    public void setPosition(String str) {
        this.position = str;
    }

    public String getBrightnessThrottlingMapId() {
        return this.brightnessThrottlingMapId;
    }

    public void setBrightnessThrottlingMapId(String str) {
        this.brightnessThrottlingMapId = str;
    }

    public String getRefreshRateThermalThrottlingMapId() {
        return this.refreshRateThermalThrottlingMapId;
    }

    public void setRefreshRateThermalThrottlingMapId(String str) {
        this.refreshRateThermalThrottlingMapId = str;
    }

    public boolean isEnabled() {
        Boolean bool = this.enabled;
        if (bool == null) {
            return false;
        }
        return bool.booleanValue();
    }

    public void setEnabled(boolean z) {
        this.enabled = Boolean.valueOf(z);
    }

    public boolean isDefaultDisplay() {
        Boolean bool = this.defaultDisplay;
        if (bool == null) {
            return false;
        }
        return bool.booleanValue();
    }

    public void setDefaultDisplay(boolean z) {
        this.defaultDisplay = Boolean.valueOf(z);
    }

    public String getRefreshRateZoneId() {
        return this.refreshRateZoneId;
    }

    public void setRefreshRateZoneId(String str) {
        this.refreshRateZoneId = str;
    }

    public String getDisplayGroup() {
        return this.displayGroup;
    }

    public void setDisplayGroup(String str) {
        this.displayGroup = str;
    }

    public static Display read(XmlPullParser xmlPullParser) throws XmlPullParserException, IOException, DatatypeConfigurationException {
        int next;
        Display display = new Display();
        String attributeValue = xmlPullParser.getAttributeValue(null, "enabled");
        if (attributeValue != null) {
            display.setEnabled(Boolean.parseBoolean(attributeValue));
        }
        String attributeValue2 = xmlPullParser.getAttributeValue(null, "defaultDisplay");
        if (attributeValue2 != null) {
            display.setDefaultDisplay(Boolean.parseBoolean(attributeValue2));
        }
        String attributeValue3 = xmlPullParser.getAttributeValue(null, "refreshRateZoneId");
        if (attributeValue3 != null) {
            display.setRefreshRateZoneId(attributeValue3);
        }
        String attributeValue4 = xmlPullParser.getAttributeValue(null, "displayGroup");
        if (attributeValue4 != null) {
            display.setDisplayGroup(attributeValue4);
        }
        xmlPullParser.getDepth();
        while (true) {
            next = xmlPullParser.next();
            if (next == 1 || next == 3) {
                break;
            } else if (xmlPullParser.getEventType() == 2) {
                String name = xmlPullParser.getName();
                if (name.equals("address")) {
                    display.setAddress(new BigInteger(XmlParser.readText(xmlPullParser)));
                } else if (name.equals("position")) {
                    display.setPosition(XmlParser.readText(xmlPullParser));
                } else if (name.equals("brightnessThrottlingMapId")) {
                    display.setBrightnessThrottlingMapId(XmlParser.readText(xmlPullParser));
                } else if (name.equals("refreshRateThermalThrottlingMapId")) {
                    display.setRefreshRateThermalThrottlingMapId(XmlParser.readText(xmlPullParser));
                } else {
                    XmlParser.skip(xmlPullParser);
                }
            }
        }
        if (next == 3) {
            return display;
        }
        throw new DatatypeConfigurationException("Display is not closed");
    }
}
