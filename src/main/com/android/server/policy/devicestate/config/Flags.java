package com.android.server.policy.devicestate.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.datatype.DatatypeConfigurationException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes2.dex */
public class Flags {
    public List<String> flag;

    public List<String> getFlag() {
        if (this.flag == null) {
            this.flag = new ArrayList();
        }
        return this.flag;
    }

    public static Flags read(XmlPullParser xmlPullParser) throws XmlPullParserException, IOException, DatatypeConfigurationException {
        int next;
        Flags flags = new Flags();
        xmlPullParser.getDepth();
        while (true) {
            next = xmlPullParser.next();
            if (next == 1 || next == 3) {
                break;
            } else if (xmlPullParser.getEventType() == 2) {
                if (xmlPullParser.getName().equals("flag")) {
                    flags.getFlag().add(XmlParser.readText(xmlPullParser));
                } else {
                    XmlParser.skip(xmlPullParser);
                }
            }
        }
        if (next == 3) {
            return flags;
        }
        throw new DatatypeConfigurationException("Flags is not closed");
    }
}
