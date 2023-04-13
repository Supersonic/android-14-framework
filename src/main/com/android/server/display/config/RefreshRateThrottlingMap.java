package com.android.server.display.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.datatype.DatatypeConfigurationException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes.dex */
public class RefreshRateThrottlingMap {

    /* renamed from: id */
    public String f1140id;
    public List<RefreshRateThrottlingPoint> refreshRateThrottlingPoint;

    public final List<RefreshRateThrottlingPoint> getRefreshRateThrottlingPoint() {
        if (this.refreshRateThrottlingPoint == null) {
            this.refreshRateThrottlingPoint = new ArrayList();
        }
        return this.refreshRateThrottlingPoint;
    }

    public String getId() {
        return this.f1140id;
    }

    public void setId(String str) {
        this.f1140id = str;
    }

    public static RefreshRateThrottlingMap read(XmlPullParser xmlPullParser) throws XmlPullParserException, IOException, DatatypeConfigurationException {
        int next;
        RefreshRateThrottlingMap refreshRateThrottlingMap = new RefreshRateThrottlingMap();
        String attributeValue = xmlPullParser.getAttributeValue(null, "id");
        if (attributeValue != null) {
            refreshRateThrottlingMap.setId(attributeValue);
        }
        xmlPullParser.getDepth();
        while (true) {
            next = xmlPullParser.next();
            if (next == 1 || next == 3) {
                break;
            } else if (xmlPullParser.getEventType() == 2) {
                if (xmlPullParser.getName().equals("refreshRateThrottlingPoint")) {
                    refreshRateThrottlingMap.getRefreshRateThrottlingPoint().add(RefreshRateThrottlingPoint.read(xmlPullParser));
                } else {
                    XmlParser.skip(xmlPullParser);
                }
            }
        }
        if (next == 3) {
            return refreshRateThrottlingMap;
        }
        throw new DatatypeConfigurationException("RefreshRateThrottlingMap is not closed");
    }
}
