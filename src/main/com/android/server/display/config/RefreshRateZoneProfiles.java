package com.android.server.display.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.datatype.DatatypeConfigurationException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes.dex */
public class RefreshRateZoneProfiles {
    public List<RefreshRateZone> refreshRateZoneProfile;

    public final List<RefreshRateZone> getRefreshRateZoneProfile() {
        if (this.refreshRateZoneProfile == null) {
            this.refreshRateZoneProfile = new ArrayList();
        }
        return this.refreshRateZoneProfile;
    }

    public static RefreshRateZoneProfiles read(XmlPullParser xmlPullParser) throws XmlPullParserException, IOException, DatatypeConfigurationException {
        int next;
        RefreshRateZoneProfiles refreshRateZoneProfiles = new RefreshRateZoneProfiles();
        xmlPullParser.getDepth();
        while (true) {
            next = xmlPullParser.next();
            if (next == 1 || next == 3) {
                break;
            } else if (xmlPullParser.getEventType() == 2) {
                if (xmlPullParser.getName().equals("refreshRateZoneProfile")) {
                    refreshRateZoneProfiles.getRefreshRateZoneProfile().add(RefreshRateZone.read(xmlPullParser));
                } else {
                    XmlParser.skip(xmlPullParser);
                }
            }
        }
        if (next == 3) {
            return refreshRateZoneProfiles;
        }
        throw new DatatypeConfigurationException("RefreshRateZoneProfiles is not closed");
    }
}
