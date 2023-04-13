package com.android.server.display.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.datatype.DatatypeConfigurationException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes.dex */
public class NitsMap {
    public String interpolation;
    public List<Point> point;

    public final List<Point> getPoint() {
        if (this.point == null) {
            this.point = new ArrayList();
        }
        return this.point;
    }

    public String getInterpolation() {
        return this.interpolation;
    }

    public void setInterpolation(String str) {
        this.interpolation = str;
    }

    public static NitsMap read(XmlPullParser xmlPullParser) throws XmlPullParserException, IOException, DatatypeConfigurationException {
        int next;
        NitsMap nitsMap = new NitsMap();
        String attributeValue = xmlPullParser.getAttributeValue(null, "interpolation");
        if (attributeValue != null) {
            nitsMap.setInterpolation(attributeValue);
        }
        xmlPullParser.getDepth();
        while (true) {
            next = xmlPullParser.next();
            if (next == 1 || next == 3) {
                break;
            } else if (xmlPullParser.getEventType() == 2) {
                if (xmlPullParser.getName().equals("point")) {
                    nitsMap.getPoint().add(Point.read(xmlPullParser));
                } else {
                    XmlParser.skip(xmlPullParser);
                }
            }
        }
        if (next == 3) {
            return nitsMap;
        }
        throw new DatatypeConfigurationException("NitsMap is not closed");
    }
}
