package com.android.server.display.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.datatype.DatatypeConfigurationException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes.dex */
public class DensityMapping {
    public List<Density> density;

    public List<Density> getDensity() {
        if (this.density == null) {
            this.density = new ArrayList();
        }
        return this.density;
    }

    public static DensityMapping read(XmlPullParser xmlPullParser) throws XmlPullParserException, IOException, DatatypeConfigurationException {
        int next;
        DensityMapping densityMapping = new DensityMapping();
        xmlPullParser.getDepth();
        while (true) {
            next = xmlPullParser.next();
            if (next == 1 || next == 3) {
                break;
            } else if (xmlPullParser.getEventType() == 2) {
                if (xmlPullParser.getName().equals("density")) {
                    densityMapping.getDensity().add(Density.read(xmlPullParser));
                } else {
                    XmlParser.skip(xmlPullParser);
                }
            }
        }
        if (next == 3) {
            return densityMapping;
        }
        throw new DatatypeConfigurationException("DensityMapping is not closed");
    }
}
