package com.android.server.display.config;

import java.io.IOException;
import java.math.BigInteger;
import javax.xml.datatype.DatatypeConfigurationException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes.dex */
public class RefreshRateConfigs {
    public BigInteger defaultPeakRefreshRate;
    public BigInteger defaultRefreshRate;
    public BigInteger defaultRefreshRateInHbmHdr;
    public BigInteger defaultRefreshRateInHbmSunlight;
    public BlockingZoneConfig higherBlockingZoneConfigs;
    public BlockingZoneConfig lowerBlockingZoneConfigs;
    public RefreshRateZoneProfiles refreshRateZoneProfiles;

    public final BigInteger getDefaultRefreshRate() {
        return this.defaultRefreshRate;
    }

    public final void setDefaultRefreshRate(BigInteger bigInteger) {
        this.defaultRefreshRate = bigInteger;
    }

    public final BigInteger getDefaultPeakRefreshRate() {
        return this.defaultPeakRefreshRate;
    }

    public final void setDefaultPeakRefreshRate(BigInteger bigInteger) {
        this.defaultPeakRefreshRate = bigInteger;
    }

    public final RefreshRateZoneProfiles getRefreshRateZoneProfiles() {
        return this.refreshRateZoneProfiles;
    }

    public final void setRefreshRateZoneProfiles(RefreshRateZoneProfiles refreshRateZoneProfiles) {
        this.refreshRateZoneProfiles = refreshRateZoneProfiles;
    }

    public final BigInteger getDefaultRefreshRateInHbmHdr() {
        return this.defaultRefreshRateInHbmHdr;
    }

    public final void setDefaultRefreshRateInHbmHdr(BigInteger bigInteger) {
        this.defaultRefreshRateInHbmHdr = bigInteger;
    }

    public final BigInteger getDefaultRefreshRateInHbmSunlight() {
        return this.defaultRefreshRateInHbmSunlight;
    }

    public final void setDefaultRefreshRateInHbmSunlight(BigInteger bigInteger) {
        this.defaultRefreshRateInHbmSunlight = bigInteger;
    }

    public final BlockingZoneConfig getLowerBlockingZoneConfigs() {
        return this.lowerBlockingZoneConfigs;
    }

    public final void setLowerBlockingZoneConfigs(BlockingZoneConfig blockingZoneConfig) {
        this.lowerBlockingZoneConfigs = blockingZoneConfig;
    }

    public final BlockingZoneConfig getHigherBlockingZoneConfigs() {
        return this.higherBlockingZoneConfigs;
    }

    public final void setHigherBlockingZoneConfigs(BlockingZoneConfig blockingZoneConfig) {
        this.higherBlockingZoneConfigs = blockingZoneConfig;
    }

    public static RefreshRateConfigs read(XmlPullParser xmlPullParser) throws XmlPullParserException, IOException, DatatypeConfigurationException {
        int next;
        RefreshRateConfigs refreshRateConfigs = new RefreshRateConfigs();
        xmlPullParser.getDepth();
        while (true) {
            next = xmlPullParser.next();
            if (next == 1 || next == 3) {
                break;
            } else if (xmlPullParser.getEventType() == 2) {
                String name = xmlPullParser.getName();
                if (name.equals("defaultRefreshRate")) {
                    refreshRateConfigs.setDefaultRefreshRate(new BigInteger(XmlParser.readText(xmlPullParser)));
                } else if (name.equals("defaultPeakRefreshRate")) {
                    refreshRateConfigs.setDefaultPeakRefreshRate(new BigInteger(XmlParser.readText(xmlPullParser)));
                } else if (name.equals("refreshRateZoneProfiles")) {
                    refreshRateConfigs.setRefreshRateZoneProfiles(RefreshRateZoneProfiles.read(xmlPullParser));
                } else if (name.equals("defaultRefreshRateInHbmHdr")) {
                    refreshRateConfigs.setDefaultRefreshRateInHbmHdr(new BigInteger(XmlParser.readText(xmlPullParser)));
                } else if (name.equals("defaultRefreshRateInHbmSunlight")) {
                    refreshRateConfigs.setDefaultRefreshRateInHbmSunlight(new BigInteger(XmlParser.readText(xmlPullParser)));
                } else if (name.equals("lowerBlockingZoneConfigs")) {
                    refreshRateConfigs.setLowerBlockingZoneConfigs(BlockingZoneConfig.read(xmlPullParser));
                } else if (name.equals("higherBlockingZoneConfigs")) {
                    refreshRateConfigs.setHigherBlockingZoneConfigs(BlockingZoneConfig.read(xmlPullParser));
                } else {
                    XmlParser.skip(xmlPullParser);
                }
            }
        }
        if (next == 3) {
            return refreshRateConfigs;
        }
        throw new DatatypeConfigurationException("RefreshRateConfigs is not closed");
    }
}
