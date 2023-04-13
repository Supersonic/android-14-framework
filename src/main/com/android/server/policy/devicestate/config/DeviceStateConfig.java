package com.android.server.policy.devicestate.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.datatype.DatatypeConfigurationException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes2.dex */
public class DeviceStateConfig {
    public List<DeviceState> deviceState;

    public List<DeviceState> getDeviceState() {
        if (this.deviceState == null) {
            this.deviceState = new ArrayList();
        }
        return this.deviceState;
    }

    public static DeviceStateConfig read(XmlPullParser xmlPullParser) throws XmlPullParserException, IOException, DatatypeConfigurationException {
        int next;
        DeviceStateConfig deviceStateConfig = new DeviceStateConfig();
        xmlPullParser.getDepth();
        while (true) {
            next = xmlPullParser.next();
            if (next == 1 || next == 3) {
                break;
            } else if (xmlPullParser.getEventType() == 2) {
                if (xmlPullParser.getName().equals("device-state")) {
                    deviceStateConfig.getDeviceState().add(DeviceState.read(xmlPullParser));
                } else {
                    XmlParser.skip(xmlPullParser);
                }
            }
        }
        if (next == 3) {
            return deviceStateConfig;
        }
        throw new DatatypeConfigurationException("DeviceStateConfig is not closed");
    }
}
