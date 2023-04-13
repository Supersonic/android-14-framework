package com.android.server.compat.overrides;

import java.io.IOException;
import javax.xml.datatype.DatatypeConfigurationException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes.dex */
public class RawOverrideValue {
    public Boolean enabled;
    public Long maxVersionCode;
    public Long minVersionCode;
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

    public long getMinVersionCode() {
        Long l = this.minVersionCode;
        if (l == null) {
            return 0L;
        }
        return l.longValue();
    }

    public boolean hasMinVersionCode() {
        return this.minVersionCode != null;
    }

    public void setMinVersionCode(long j) {
        this.minVersionCode = Long.valueOf(j);
    }

    public long getMaxVersionCode() {
        Long l = this.maxVersionCode;
        if (l == null) {
            return 0L;
        }
        return l.longValue();
    }

    public boolean hasMaxVersionCode() {
        return this.maxVersionCode != null;
    }

    public void setMaxVersionCode(long j) {
        this.maxVersionCode = Long.valueOf(j);
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

    public static RawOverrideValue read(XmlPullParser xmlPullParser) throws XmlPullParserException, IOException, DatatypeConfigurationException {
        RawOverrideValue rawOverrideValue = new RawOverrideValue();
        String attributeValue = xmlPullParser.getAttributeValue(null, "packageName");
        if (attributeValue != null) {
            rawOverrideValue.setPackageName(attributeValue);
        }
        String attributeValue2 = xmlPullParser.getAttributeValue(null, "minVersionCode");
        if (attributeValue2 != null) {
            rawOverrideValue.setMinVersionCode(Long.parseLong(attributeValue2));
        }
        String attributeValue3 = xmlPullParser.getAttributeValue(null, "maxVersionCode");
        if (attributeValue3 != null) {
            rawOverrideValue.setMaxVersionCode(Long.parseLong(attributeValue3));
        }
        String attributeValue4 = xmlPullParser.getAttributeValue(null, "enabled");
        if (attributeValue4 != null) {
            rawOverrideValue.setEnabled(Boolean.parseBoolean(attributeValue4));
        }
        XmlParser.skip(xmlPullParser);
        return rawOverrideValue;
    }

    public void write(XmlWriter xmlWriter, String str) throws IOException {
        xmlWriter.print("<" + str);
        if (hasPackageName()) {
            xmlWriter.print(" packageName=\"");
            xmlWriter.print(getPackageName());
            xmlWriter.print("\"");
        }
        if (hasMinVersionCode()) {
            xmlWriter.print(" minVersionCode=\"");
            xmlWriter.print(Long.toString(getMinVersionCode()));
            xmlWriter.print("\"");
        }
        if (hasMaxVersionCode()) {
            xmlWriter.print(" maxVersionCode=\"");
            xmlWriter.print(Long.toString(getMaxVersionCode()));
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
