package com.android.server.display.config;

import java.io.IOException;
import java.math.BigInteger;
import javax.xml.datatype.DatatypeConfigurationException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes.dex */
public class AutoBrightness {
    public BigInteger brighteningLightDebounceMillis;
    public BigInteger darkeningLightDebounceMillis;
    public DisplayBrightnessMapping displayBrightnessMapping;
    public Boolean enabled;

    public final BigInteger getBrighteningLightDebounceMillis() {
        return this.brighteningLightDebounceMillis;
    }

    public final void setBrighteningLightDebounceMillis(BigInteger bigInteger) {
        this.brighteningLightDebounceMillis = bigInteger;
    }

    public final BigInteger getDarkeningLightDebounceMillis() {
        return this.darkeningLightDebounceMillis;
    }

    public final void setDarkeningLightDebounceMillis(BigInteger bigInteger) {
        this.darkeningLightDebounceMillis = bigInteger;
    }

    public final DisplayBrightnessMapping getDisplayBrightnessMapping() {
        return this.displayBrightnessMapping;
    }

    public final void setDisplayBrightnessMapping(DisplayBrightnessMapping displayBrightnessMapping) {
        this.displayBrightnessMapping = displayBrightnessMapping;
    }

    public boolean getEnabled() {
        Boolean bool = this.enabled;
        if (bool == null) {
            return false;
        }
        return bool.booleanValue();
    }

    public void setEnabled(boolean z) {
        this.enabled = Boolean.valueOf(z);
    }

    public static AutoBrightness read(XmlPullParser xmlPullParser) throws XmlPullParserException, IOException, DatatypeConfigurationException {
        int next;
        AutoBrightness autoBrightness = new AutoBrightness();
        String attributeValue = xmlPullParser.getAttributeValue(null, "enabled");
        if (attributeValue != null) {
            autoBrightness.setEnabled(Boolean.parseBoolean(attributeValue));
        }
        xmlPullParser.getDepth();
        while (true) {
            next = xmlPullParser.next();
            if (next == 1 || next == 3) {
                break;
            } else if (xmlPullParser.getEventType() == 2) {
                String name = xmlPullParser.getName();
                if (name.equals("brighteningLightDebounceMillis")) {
                    autoBrightness.setBrighteningLightDebounceMillis(new BigInteger(XmlParser.readText(xmlPullParser)));
                } else if (name.equals("darkeningLightDebounceMillis")) {
                    autoBrightness.setDarkeningLightDebounceMillis(new BigInteger(XmlParser.readText(xmlPullParser)));
                } else if (name.equals("displayBrightnessMapping")) {
                    autoBrightness.setDisplayBrightnessMapping(DisplayBrightnessMapping.read(xmlPullParser));
                } else {
                    XmlParser.skip(xmlPullParser);
                }
            }
        }
        if (next == 3) {
            return autoBrightness;
        }
        throw new DatatypeConfigurationException("AutoBrightness is not closed");
    }
}
