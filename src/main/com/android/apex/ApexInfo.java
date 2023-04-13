package com.android.apex;

import java.io.IOException;
import javax.xml.datatype.DatatypeConfigurationException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes4.dex */
public class ApexInfo {
    private Boolean isActive;
    private Boolean isFactory;
    private Long lastUpdateMillis;
    private String moduleName;
    private String modulePath;
    private String preinstalledModulePath;
    private Boolean provideSharedApexLibs;
    private Long versionCode;
    private String versionName;

    public String getModuleName() {
        return this.moduleName;
    }

    boolean hasModuleName() {
        if (this.moduleName == null) {
            return false;
        }
        return true;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getModulePath() {
        return this.modulePath;
    }

    boolean hasModulePath() {
        if (this.modulePath == null) {
            return false;
        }
        return true;
    }

    public void setModulePath(String modulePath) {
        this.modulePath = modulePath;
    }

    public String getPreinstalledModulePath() {
        return this.preinstalledModulePath;
    }

    boolean hasPreinstalledModulePath() {
        if (this.preinstalledModulePath == null) {
            return false;
        }
        return true;
    }

    public void setPreinstalledModulePath(String preinstalledModulePath) {
        this.preinstalledModulePath = preinstalledModulePath;
    }

    public long getVersionCode() {
        Long l = this.versionCode;
        if (l == null) {
            return 0L;
        }
        return l.longValue();
    }

    boolean hasVersionCode() {
        if (this.versionCode == null) {
            return false;
        }
        return true;
    }

    public void setVersionCode(long versionCode) {
        this.versionCode = Long.valueOf(versionCode);
    }

    public String getVersionName() {
        return this.versionName;
    }

    boolean hasVersionName() {
        if (this.versionName == null) {
            return false;
        }
        return true;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public boolean getIsFactory() {
        Boolean bool = this.isFactory;
        if (bool == null) {
            return false;
        }
        return bool.booleanValue();
    }

    boolean hasIsFactory() {
        if (this.isFactory == null) {
            return false;
        }
        return true;
    }

    public void setIsFactory(boolean isFactory) {
        this.isFactory = Boolean.valueOf(isFactory);
    }

    public boolean getIsActive() {
        Boolean bool = this.isActive;
        if (bool == null) {
            return false;
        }
        return bool.booleanValue();
    }

    boolean hasIsActive() {
        if (this.isActive == null) {
            return false;
        }
        return true;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = Boolean.valueOf(isActive);
    }

    public long getLastUpdateMillis() {
        Long l = this.lastUpdateMillis;
        if (l == null) {
            return 0L;
        }
        return l.longValue();
    }

    boolean hasLastUpdateMillis() {
        if (this.lastUpdateMillis == null) {
            return false;
        }
        return true;
    }

    public void setLastUpdateMillis(long lastUpdateMillis) {
        this.lastUpdateMillis = Long.valueOf(lastUpdateMillis);
    }

    public boolean getProvideSharedApexLibs() {
        Boolean bool = this.provideSharedApexLibs;
        if (bool == null) {
            return false;
        }
        return bool.booleanValue();
    }

    boolean hasProvideSharedApexLibs() {
        if (this.provideSharedApexLibs == null) {
            return false;
        }
        return true;
    }

    public void setProvideSharedApexLibs(boolean provideSharedApexLibs) {
        this.provideSharedApexLibs = Boolean.valueOf(provideSharedApexLibs);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static ApexInfo read(XmlPullParser _parser) throws XmlPullParserException, IOException, DatatypeConfigurationException {
        ApexInfo _instance = new ApexInfo();
        String _raw = _parser.getAttributeValue(null, "moduleName");
        if (_raw != null) {
            _instance.setModuleName(_raw);
        }
        String _raw2 = _parser.getAttributeValue(null, "modulePath");
        if (_raw2 != null) {
            _instance.setModulePath(_raw2);
        }
        String _raw3 = _parser.getAttributeValue(null, "preinstalledModulePath");
        if (_raw3 != null) {
            _instance.setPreinstalledModulePath(_raw3);
        }
        String _raw4 = _parser.getAttributeValue(null, "versionCode");
        if (_raw4 != null) {
            long _value = Long.parseLong(_raw4);
            _instance.setVersionCode(_value);
        }
        String _raw5 = _parser.getAttributeValue(null, "versionName");
        if (_raw5 != null) {
            _instance.setVersionName(_raw5);
        }
        String _raw6 = _parser.getAttributeValue(null, "isFactory");
        if (_raw6 != null) {
            boolean _value2 = Boolean.parseBoolean(_raw6);
            _instance.setIsFactory(_value2);
        }
        String _raw7 = _parser.getAttributeValue(null, "isActive");
        if (_raw7 != null) {
            boolean _value3 = Boolean.parseBoolean(_raw7);
            _instance.setIsActive(_value3);
        }
        String _raw8 = _parser.getAttributeValue(null, "lastUpdateMillis");
        if (_raw8 != null) {
            long _value4 = Long.parseLong(_raw8);
            _instance.setLastUpdateMillis(_value4);
        }
        String _raw9 = _parser.getAttributeValue(null, "provideSharedApexLibs");
        if (_raw9 != null) {
            boolean _value5 = Boolean.parseBoolean(_raw9);
            _instance.setProvideSharedApexLibs(_value5);
        }
        XmlParser.skip(_parser);
        return _instance;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void write(XmlWriter _out, String _name) throws IOException {
        _out.print("<" + _name);
        if (hasModuleName()) {
            _out.print(" moduleName=\"");
            _out.print(getModuleName());
            _out.print("\"");
        }
        if (hasModulePath()) {
            _out.print(" modulePath=\"");
            _out.print(getModulePath());
            _out.print("\"");
        }
        if (hasPreinstalledModulePath()) {
            _out.print(" preinstalledModulePath=\"");
            _out.print(getPreinstalledModulePath());
            _out.print("\"");
        }
        if (hasVersionCode()) {
            _out.print(" versionCode=\"");
            _out.print(Long.toString(getVersionCode()));
            _out.print("\"");
        }
        if (hasVersionName()) {
            _out.print(" versionName=\"");
            _out.print(getVersionName());
            _out.print("\"");
        }
        if (hasIsFactory()) {
            _out.print(" isFactory=\"");
            _out.print(Boolean.toString(getIsFactory()));
            _out.print("\"");
        }
        if (hasIsActive()) {
            _out.print(" isActive=\"");
            _out.print(Boolean.toString(getIsActive()));
            _out.print("\"");
        }
        if (hasLastUpdateMillis()) {
            _out.print(" lastUpdateMillis=\"");
            _out.print(Long.toString(getLastUpdateMillis()));
            _out.print("\"");
        }
        if (hasProvideSharedApexLibs()) {
            _out.print(" provideSharedApexLibs=\"");
            _out.print(Boolean.toString(getProvideSharedApexLibs()));
            _out.print("\"");
        }
        _out.print(">\n");
        _out.print("</" + _name + ">\n");
    }
}
