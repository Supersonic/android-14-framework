package android.audio.policy.configuration.V7_0;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.datatype.DatatypeConfigurationException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes.dex */
public class AudioPolicyConfiguration {
    private GlobalConfiguration globalConfiguration;
    private List<Modules> modules;
    private SurroundSound surroundSound;
    private Version version;
    private List<Volumes> volumes;

    public GlobalConfiguration getGlobalConfiguration() {
        return this.globalConfiguration;
    }

    boolean hasGlobalConfiguration() {
        if (this.globalConfiguration == null) {
            return false;
        }
        return true;
    }

    public void setGlobalConfiguration(GlobalConfiguration globalConfiguration) {
        this.globalConfiguration = globalConfiguration;
    }

    public List<Modules> getModules() {
        if (this.modules == null) {
            this.modules = new ArrayList();
        }
        return this.modules;
    }

    public List<Volumes> getVolumes() {
        if (this.volumes == null) {
            this.volumes = new ArrayList();
        }
        return this.volumes;
    }

    public SurroundSound getSurroundSound() {
        return this.surroundSound;
    }

    boolean hasSurroundSound() {
        if (this.surroundSound == null) {
            return false;
        }
        return true;
    }

    public void setSurroundSound(SurroundSound surroundSound) {
        this.surroundSound = surroundSound;
    }

    public Version getVersion() {
        return this.version;
    }

    boolean hasVersion() {
        if (this.version == null) {
            return false;
        }
        return true;
    }

    public void setVersion(Version version) {
        this.version = version;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static AudioPolicyConfiguration read(XmlPullParser _parser) throws XmlPullParserException, IOException, DatatypeConfigurationException {
        int type;
        AudioPolicyConfiguration _instance = new AudioPolicyConfiguration();
        String _raw = _parser.getAttributeValue(null, "version");
        if (_raw != null) {
            Version _value = Version.fromString(_raw);
            _instance.setVersion(_value);
        }
        _parser.getDepth();
        while (true) {
            type = _parser.next();
            if (type == 1 || type == 3) {
                break;
            } else if (_parser.getEventType() == 2) {
                String _tagName = _parser.getName();
                if (_tagName.equals("globalConfiguration")) {
                    GlobalConfiguration _value2 = GlobalConfiguration.read(_parser);
                    _instance.setGlobalConfiguration(_value2);
                } else if (_tagName.equals("modules")) {
                    Modules _value3 = Modules.read(_parser);
                    _instance.getModules().add(_value3);
                } else if (_tagName.equals("volumes")) {
                    Volumes _value4 = Volumes.read(_parser);
                    _instance.getVolumes().add(_value4);
                } else if (_tagName.equals("surroundSound")) {
                    SurroundSound _value5 = SurroundSound.read(_parser);
                    _instance.setSurroundSound(_value5);
                } else {
                    XmlParser.skip(_parser);
                }
            }
        }
        if (type != 3) {
            throw new DatatypeConfigurationException("AudioPolicyConfiguration is not closed");
        }
        return _instance;
    }
}
