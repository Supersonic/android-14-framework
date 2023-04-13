package com.android.server.compat.overrides;

import java.io.IOException;
import javax.xml.datatype.DatatypeConfigurationException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes.dex */
public class OverrideValue {
    public Boolean enabled;
    public String packageName;

    public String getPackageName() {
        return this.packageName;
    }

    public boolean hasPackageName() {
        return this.packageName != null;
    }

    public void setPackageName(String str) {
        this.packageName = str;
    }

    public boolean getEnabled() {
        Boolean bool = this.enabled;
        if (bool == null) {
            return false;
        }
        return bool.booleanValue();
    }

    public boolean hasEnabled() {
        return this.enabled != null;
    }

    public void setEnabled(boolean z) {
        this.enabled = Boolean.valueOf(z);
    }

    public static OverrideValue read(XmlPullParser xmlPullParser) throws XmlPullParserException, IOException, DatatypeConfigurationException {
        OverrideValue overrideValue = new OverrideValue();
        String attributeValue = xmlPullParser.getAttributeValue(null, "packageName");
        if (attributeValue != null) {
            overrideValue.setPackageName(attributeValue);
        }
        String attributeValue2 = xmlPullParser.getAttributeValue(null, "enabled");
        if (attributeValue2 != null) {
            overrideValue.setEnabled(Boolean.parseBoolean(attributeValue2));
        }
        XmlParser.skip(xmlPullParser);
        return overrideValue;
    }

    public void write(XmlWriter xmlWriter, String str) throws IOException {
        xmlWriter.print("<" + str);
        if (hasPackageName()) {
            xmlWriter.print(" packageName=\"");
            xmlWriter.print(getPackageName());
            xmlWriter.print("\"");
        }
        if (hasEnabled()) {
            xmlWriter.print(" enabled=\"");
            xmlWriter.print(Boolean.toString(getEnabled()));
            xmlWriter.print("\"");
        }
        xmlWriter.print(">\n");
        xmlWriter.print("</" + str + ">\n");
    }
}
