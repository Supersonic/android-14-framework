package com.android.server.policy.devicestate.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.datatype.DatatypeConfigurationException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes2.dex */
public class Conditions {
    public LidSwitchCondition lidSwitch;
    public List<SensorCondition> sensor;

    public LidSwitchCondition getLidSwitch() {
        return this.lidSwitch;
    }

    public void setLidSwitch(LidSwitchCondition lidSwitchCondition) {
        this.lidSwitch = lidSwitchCondition;
    }

    public List<SensorCondition> getSensor() {
        if (this.sensor == null) {
            this.sensor = new ArrayList();
        }
        return this.sensor;
    }

    public static Conditions read(XmlPullParser xmlPullParser) throws XmlPullParserException, IOException, DatatypeConfigurationException {
        int next;
        Conditions conditions = new Conditions();
        xmlPullParser.getDepth();
        while (true) {
            next = xmlPullParser.next();
            if (next == 1 || next == 3) {
                break;
            } else if (xmlPullParser.getEventType() == 2) {
                String name = xmlPullParser.getName();
                if (name.equals("lid-switch")) {
                    conditions.setLidSwitch(LidSwitchCondition.read(xmlPullParser));
                } else if (name.equals("sensor")) {
                    conditions.getSensor().add(SensorCondition.read(xmlPullParser));
                } else {
                    XmlParser.skip(xmlPullParser);
                }
            }
        }
        if (next == 3) {
            return conditions;
        }
        throw new DatatypeConfigurationException("Conditions is not closed");
    }
}
