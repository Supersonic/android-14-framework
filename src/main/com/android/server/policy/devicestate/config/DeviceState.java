package com.android.server.policy.devicestate.config;

import java.io.IOException;
import java.math.BigInteger;
import javax.xml.datatype.DatatypeConfigurationException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes2.dex */
public class DeviceState {
    public Conditions conditions;
    public Flags flags;
    public BigInteger identifier;
    public String name;

    public BigInteger getIdentifier() {
        return this.identifier;
    }

    public void setIdentifier(BigInteger bigInteger) {
        this.identifier = bigInteger;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String str) {
        this.name = str;
    }

    public Flags getFlags() {
        return this.flags;
    }

    public void setFlags(Flags flags) {
        this.flags = flags;
    }

    public Conditions getConditions() {
        return this.conditions;
    }

    public void setConditions(Conditions conditions) {
        this.conditions = conditions;
    }

    public static DeviceState read(XmlPullParser xmlPullParser) throws XmlPullParserException, IOException, DatatypeConfigurationException {
        int next;
        DeviceState deviceState = new DeviceState();
        xmlPullParser.getDepth();
        while (true) {
            next = xmlPullParser.next();
            if (next == 1 || next == 3) {
                break;
            } else if (xmlPullParser.getEventType() == 2) {
                String name = xmlPullParser.getName();
                if (name.equals("identifier")) {
                    deviceState.setIdentifier(new BigInteger(XmlParser.readText(xmlPullParser)));
                } else if (name.equals("name")) {
                    deviceState.setName(XmlParser.readText(xmlPullParser));
                } else if (name.equals("flags")) {
                    deviceState.setFlags(Flags.read(xmlPullParser));
                } else if (name.equals("conditions")) {
                    deviceState.setConditions(Conditions.read(xmlPullParser));
                } else {
                    XmlParser.skip(xmlPullParser);
                }
            }
        }
        if (next == 3) {
            return deviceState;
        }
        throw new DatatypeConfigurationException("DeviceState is not closed");
    }
}
