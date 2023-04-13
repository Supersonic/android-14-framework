package com.android.server.display.config.layout;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import javax.xml.datatype.DatatypeConfigurationException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes.dex */
public class Layout {
    public List<Display> display;
    public BigInteger state;

    public BigInteger getState() {
        return this.state;
    }

    public void setState(BigInteger bigInteger) {
        this.state = bigInteger;
    }

    public List<Display> getDisplay() {
        if (this.display == null) {
            this.display = new ArrayList();
        }
        return this.display;
    }

    public static Layout read(XmlPullParser xmlPullParser) throws XmlPullParserException, IOException, DatatypeConfigurationException {
        int next;
        Layout layout = new Layout();
        xmlPullParser.getDepth();
        while (true) {
            next = xmlPullParser.next();
            if (next == 1 || next == 3) {
                break;
            } else if (xmlPullParser.getEventType() == 2) {
                String name = xmlPullParser.getName();
                if (name.equals("state")) {
                    layout.setState(new BigInteger(XmlParser.readText(xmlPullParser)));
                } else if (name.equals("display")) {
                    layout.getDisplay().add(Display.read(xmlPullParser));
                } else {
                    XmlParser.skip(xmlPullParser);
                }
            }
        }
        if (next == 3) {
            return layout;
        }
        throw new DatatypeConfigurationException("Layout is not closed");
    }
}
