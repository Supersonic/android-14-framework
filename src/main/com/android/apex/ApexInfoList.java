package com.android.apex;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.datatype.DatatypeConfigurationException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes4.dex */
public class ApexInfoList {
    private List<ApexInfo> apexInfo;

    public List<ApexInfo> getApexInfo() {
        if (this.apexInfo == null) {
            this.apexInfo = new ArrayList();
        }
        return this.apexInfo;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static ApexInfoList read(XmlPullParser _parser) throws XmlPullParserException, IOException, DatatypeConfigurationException {
        int type;
        ApexInfoList _instance = new ApexInfoList();
        _parser.getDepth();
        while (true) {
            type = _parser.next();
            if (type == 1 || type == 3) {
                break;
            } else if (_parser.getEventType() == 2) {
                String _tagName = _parser.getName();
                if (_tagName.equals("apex-info")) {
                    ApexInfo _value = ApexInfo.read(_parser);
                    _instance.getApexInfo().add(_value);
                } else {
                    XmlParser.skip(_parser);
                }
            }
        }
        if (type != 3) {
            throw new DatatypeConfigurationException("ApexInfoList is not closed");
        }
        return _instance;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void write(XmlWriter _out, String _name) throws IOException {
        _out.print("<" + _name);
        _out.print(">\n");
        _out.increaseIndent();
        for (ApexInfo value : getApexInfo()) {
            value.write(_out, "apex-info");
        }
        _out.decreaseIndent();
        _out.print("</" + _name + ">\n");
    }
}
