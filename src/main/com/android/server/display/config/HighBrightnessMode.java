package com.android.server.display.config;

import java.io.IOException;
import java.math.BigDecimal;
import javax.xml.datatype.DatatypeConfigurationException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes.dex */
public class HighBrightnessMode {
    public Boolean allowInLowPowerMode_all;
    public Boolean enabled;
    public BigDecimal minimumHdrPercentOfScreen_all;
    public BigDecimal minimumLux_all;
    public RefreshRateRange refreshRate_all;
    public SdrHdrRatioMap sdrHdrRatioMap_all;
    public ThermalStatus thermalStatusLimit_all;
    public HbmTiming timing_all;
    public BigDecimal transitionPoint_all;

    public final BigDecimal getTransitionPoint_all() {
        return this.transitionPoint_all;
    }

    public final void setTransitionPoint_all(BigDecimal bigDecimal) {
        this.transitionPoint_all = bigDecimal;
    }

    public final BigDecimal getMinimumLux_all() {
        return this.minimumLux_all;
    }

    public final void setMinimumLux_all(BigDecimal bigDecimal) {
        this.minimumLux_all = bigDecimal;
    }

    public HbmTiming getTiming_all() {
        return this.timing_all;
    }

    public void setTiming_all(HbmTiming hbmTiming) {
        this.timing_all = hbmTiming;
    }

    public final RefreshRateRange getRefreshRate_all() {
        return this.refreshRate_all;
    }

    public final void setRefreshRate_all(RefreshRateRange refreshRateRange) {
        this.refreshRate_all = refreshRateRange;
    }

    public final ThermalStatus getThermalStatusLimit_all() {
        return this.thermalStatusLimit_all;
    }

    public final void setThermalStatusLimit_all(ThermalStatus thermalStatus) {
        this.thermalStatusLimit_all = thermalStatus;
    }

    public final boolean getAllowInLowPowerMode_all() {
        Boolean bool = this.allowInLowPowerMode_all;
        if (bool == null) {
            return false;
        }
        return bool.booleanValue();
    }

    public final void setAllowInLowPowerMode_all(boolean z) {
        this.allowInLowPowerMode_all = Boolean.valueOf(z);
    }

    public final BigDecimal getMinimumHdrPercentOfScreen_all() {
        return this.minimumHdrPercentOfScreen_all;
    }

    public final void setMinimumHdrPercentOfScreen_all(BigDecimal bigDecimal) {
        this.minimumHdrPercentOfScreen_all = bigDecimal;
    }

    public final SdrHdrRatioMap getSdrHdrRatioMap_all() {
        return this.sdrHdrRatioMap_all;
    }

    public final void setSdrHdrRatioMap_all(SdrHdrRatioMap sdrHdrRatioMap) {
        this.sdrHdrRatioMap_all = sdrHdrRatioMap;
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

    public static HighBrightnessMode read(XmlPullParser xmlPullParser) throws XmlPullParserException, IOException, DatatypeConfigurationException {
        int next;
        HighBrightnessMode highBrightnessMode = new HighBrightnessMode();
        String attributeValue = xmlPullParser.getAttributeValue(null, "enabled");
        if (attributeValue != null) {
            highBrightnessMode.setEnabled(Boolean.parseBoolean(attributeValue));
        }
        xmlPullParser.getDepth();
        while (true) {
            next = xmlPullParser.next();
            if (next == 1 || next == 3) {
                break;
            } else if (xmlPullParser.getEventType() == 2) {
                String name = xmlPullParser.getName();
                if (name.equals("transitionPoint")) {
                    highBrightnessMode.setTransitionPoint_all(new BigDecimal(XmlParser.readText(xmlPullParser)));
                } else if (name.equals("minimumLux")) {
                    highBrightnessMode.setMinimumLux_all(new BigDecimal(XmlParser.readText(xmlPullParser)));
                } else if (name.equals("timing")) {
                    highBrightnessMode.setTiming_all(HbmTiming.read(xmlPullParser));
                } else if (name.equals("refreshRate")) {
                    highBrightnessMode.setRefreshRate_all(RefreshRateRange.read(xmlPullParser));
                } else if (name.equals("thermalStatusLimit")) {
                    highBrightnessMode.setThermalStatusLimit_all(ThermalStatus.fromString(XmlParser.readText(xmlPullParser)));
                } else if (name.equals("allowInLowPowerMode")) {
                    highBrightnessMode.setAllowInLowPowerMode_all(Boolean.parseBoolean(XmlParser.readText(xmlPullParser)));
                } else if (name.equals("minimumHdrPercentOfScreen")) {
                    highBrightnessMode.setMinimumHdrPercentOfScreen_all(new BigDecimal(XmlParser.readText(xmlPullParser)));
                } else if (name.equals("sdrHdrRatioMap")) {
                    highBrightnessMode.setSdrHdrRatioMap_all(SdrHdrRatioMap.read(xmlPullParser));
                } else {
                    XmlParser.skip(xmlPullParser);
                }
            }
        }
        if (next == 3) {
            return highBrightnessMode;
        }
        throw new DatatypeConfigurationException("HighBrightnessMode is not closed");
    }
}
