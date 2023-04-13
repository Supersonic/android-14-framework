package com.android.server.compat.config;

import java.io.IOException;
import javax.xml.datatype.DatatypeConfigurationException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes.dex */
public class Change {
    public String description;
    public Boolean disabled;
    public Integer enableAfterTargetSdk;
    public Integer enableSinceTargetSdk;

    /* renamed from: id */
    public Long f1137id;
    public Boolean loggingOnly;
    public String name;
    public Boolean overridable;
    public String value;

    public long getId() {
        Long l = this.f1137id;
        if (l == null) {
            return 0L;
        }
        return l.longValue();
    }

    public void setId(long j) {
        this.f1137id = Long.valueOf(j);
    }

    public String getName() {
        return this.name;
    }

    public void setName(String str) {
        this.name = str;
    }

    public boolean getDisabled() {
        Boolean bool = this.disabled;
        if (bool == null) {
            return false;
        }
        return bool.booleanValue();
    }

    public void setDisabled(boolean z) {
        this.disabled = Boolean.valueOf(z);
    }

    public boolean getLoggingOnly() {
        Boolean bool = this.loggingOnly;
        if (bool == null) {
            return false;
        }
        return bool.booleanValue();
    }

    public void setLoggingOnly(boolean z) {
        this.loggingOnly = Boolean.valueOf(z);
    }

    public int getEnableAfterTargetSdk() {
        Integer num = this.enableAfterTargetSdk;
        if (num == null) {
            return 0;
        }
        return num.intValue();
    }

    public void setEnableAfterTargetSdk(int i) {
        this.enableAfterTargetSdk = Integer.valueOf(i);
    }

    public int getEnableSinceTargetSdk() {
        Integer num = this.enableSinceTargetSdk;
        if (num == null) {
            return 0;
        }
        return num.intValue();
    }

    public void setEnableSinceTargetSdk(int i) {
        this.enableSinceTargetSdk = Integer.valueOf(i);
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String str) {
        this.description = str;
    }

    public boolean getOverridable() {
        Boolean bool = this.overridable;
        if (bool == null) {
            return false;
        }
        return bool.booleanValue();
    }

    public void setOverridable(boolean z) {
        this.overridable = Boolean.valueOf(z);
    }

    public void setValue(String str) {
        this.value = str;
    }

    public static Change read(XmlPullParser xmlPullParser) throws XmlPullParserException, IOException, DatatypeConfigurationException {
        Change change = new Change();
        String attributeValue = xmlPullParser.getAttributeValue(null, "id");
        if (attributeValue != null) {
            change.setId(Long.parseLong(attributeValue));
        }
        String attributeValue2 = xmlPullParser.getAttributeValue(null, "name");
        if (attributeValue2 != null) {
            change.setName(attributeValue2);
        }
        String attributeValue3 = xmlPullParser.getAttributeValue(null, "disabled");
        if (attributeValue3 != null) {
            change.setDisabled(Boolean.parseBoolean(attributeValue3));
        }
        String attributeValue4 = xmlPullParser.getAttributeValue(null, "loggingOnly");
        if (attributeValue4 != null) {
            change.setLoggingOnly(Boolean.parseBoolean(attributeValue4));
        }
        String attributeValue5 = xmlPullParser.getAttributeValue(null, "enableAfterTargetSdk");
        if (attributeValue5 != null) {
            change.setEnableAfterTargetSdk(Integer.parseInt(attributeValue5));
        }
        String attributeValue6 = xmlPullParser.getAttributeValue(null, "enableSinceTargetSdk");
        if (attributeValue6 != null) {
            change.setEnableSinceTargetSdk(Integer.parseInt(attributeValue6));
        }
        String attributeValue7 = xmlPullParser.getAttributeValue(null, "description");
        if (attributeValue7 != null) {
            change.setDescription(attributeValue7);
        }
        String attributeValue8 = xmlPullParser.getAttributeValue(null, "overridable");
        if (attributeValue8 != null) {
            change.setOverridable(Boolean.parseBoolean(attributeValue8));
        }
        String readText = XmlParser.readText(xmlPullParser);
        if (readText != null) {
            change.setValue(readText);
        }
        return change;
    }
}
