package com.android.server.policy.devicestate.config;

import java.io.IOException;
import javax.xml.datatype.DatatypeConfigurationException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes2.dex */
public class LidSwitchCondition {
    public Boolean open;

    public boolean getOpen() {
        Boolean bool = this.open;
        if (bool == null) {
            return false;
        }
        return bool.booleanValue();
    }

    public void setOpen(boolean z) {
        this.open = Boolean.valueOf(z);
    }

    public static LidSwitchCondition read(XmlPullParser xmlPullParser) throws XmlPullParserException, IOException, DatatypeConfigurationException {
        int next;
        LidSwitchCondition lidSwitchCondition = new LidSwitchCondition();
        xmlPullParser.getDepth();
        while (true) {
            next = xmlPullParser.next();
            if (next == 1 || next == 3) {
                break;
            } else if (xmlPullParser.getEventType() == 2) {
                if (xmlPullParser.getName().equals("open")) {
                    lidSwitchCondition.setOpen(Boolean.parseBoolean(XmlParser.readText(xmlPullParser)));
                } else {
                    XmlParser.skip(xmlPullParser);
                }
            }
        }
        if (next == 3) {
            return lidSwitchCondition;
        }
        throw new DatatypeConfigurationException("LidSwitchCondition is not closed");
    }
}
