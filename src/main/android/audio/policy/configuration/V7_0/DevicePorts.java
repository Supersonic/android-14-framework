package android.audio.policy.configuration.V7_0;

import android.content.Context;
import android.media.MediaFormat;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.datatype.DatatypeConfigurationException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes.dex */
public class DevicePorts {
    private List<DevicePort> devicePort;

    /* loaded from: classes.dex */
    public static class DevicePort {
        private Boolean _default;
        private String address;
        private List<String> encodedFormats;
        private Gains gains;
        private List<Profile> profile;
        private Role role;
        private String tagName;
        private String type;

        public List<Profile> getProfile() {
            if (this.profile == null) {
                this.profile = new ArrayList();
            }
            return this.profile;
        }

        public Gains getGains() {
            return this.gains;
        }

        boolean hasGains() {
            if (this.gains == null) {
                return false;
            }
            return true;
        }

        public void setGains(Gains gains) {
            this.gains = gains;
        }

        public String getTagName() {
            return this.tagName;
        }

        boolean hasTagName() {
            if (this.tagName == null) {
                return false;
            }
            return true;
        }

        public void setTagName(String tagName) {
            this.tagName = tagName;
        }

        public String getType() {
            return this.type;
        }

        boolean hasType() {
            if (this.type == null) {
                return false;
            }
            return true;
        }

        public void setType(String type) {
            this.type = type;
        }

        public Role getRole() {
            return this.role;
        }

        boolean hasRole() {
            if (this.role == null) {
                return false;
            }
            return true;
        }

        public void setRole(Role role) {
            this.role = role;
        }

        public String getAddress() {
            return this.address;
        }

        boolean hasAddress() {
            if (this.address == null) {
                return false;
            }
            return true;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public boolean get_default() {
            Boolean bool = this._default;
            if (bool == null) {
                return false;
            }
            return bool.booleanValue();
        }

        boolean has_default() {
            if (this._default == null) {
                return false;
            }
            return true;
        }

        public void set_default(boolean _default) {
            this._default = Boolean.valueOf(_default);
        }

        public List<String> getEncodedFormats() {
            if (this.encodedFormats == null) {
                this.encodedFormats = new ArrayList();
            }
            return this.encodedFormats;
        }

        boolean hasEncodedFormats() {
            if (this.encodedFormats == null) {
                return false;
            }
            return true;
        }

        public void setEncodedFormats(List<String> encodedFormats) {
            this.encodedFormats = encodedFormats;
        }

        static DevicePort read(XmlPullParser _parser) throws XmlPullParserException, IOException, DatatypeConfigurationException {
            int type;
            String[] split;
            DevicePort _instance = new DevicePort();
            String _raw = _parser.getAttributeValue(null, "tagName");
            if (_raw != null) {
                _instance.setTagName(_raw);
            }
            String _raw2 = _parser.getAttributeValue(null, "type");
            if (_raw2 != null) {
                _instance.setType(_raw2);
            }
            String _raw3 = _parser.getAttributeValue(null, Context.ROLE_SERVICE);
            if (_raw3 != null) {
                Role _value = Role.fromString(_raw3);
                _instance.setRole(_value);
            }
            String _raw4 = _parser.getAttributeValue(null, "address");
            if (_raw4 != null) {
                _instance.setAddress(_raw4);
            }
            String _raw5 = _parser.getAttributeValue(null, "default");
            if (_raw5 != null) {
                boolean _value2 = Boolean.parseBoolean(_raw5);
                _instance.set_default(_value2);
            }
            String _raw6 = _parser.getAttributeValue(null, "encodedFormats");
            if (_raw6 != null) {
                List<String> _value3 = new ArrayList<>();
                for (String _token : _raw6.split("\\s+")) {
                    _value3.add(_token);
                }
                _instance.setEncodedFormats(_value3);
            }
            _parser.getDepth();
            while (true) {
                type = _parser.next();
                if (type == 1 || type == 3) {
                    break;
                } else if (_parser.getEventType() == 2) {
                    String _tagName = _parser.getName();
                    if (_tagName.equals(MediaFormat.KEY_PROFILE)) {
                        Profile _value4 = Profile.read(_parser);
                        _instance.getProfile().add(_value4);
                    } else if (_tagName.equals("gains")) {
                        Gains _value5 = Gains.read(_parser);
                        _instance.setGains(_value5);
                    } else {
                        XmlParser.skip(_parser);
                    }
                }
            }
            if (type != 3) {
                throw new DatatypeConfigurationException("DevicePorts.DevicePort is not closed");
            }
            return _instance;
        }
    }

    public List<DevicePort> getDevicePort() {
        if (this.devicePort == null) {
            this.devicePort = new ArrayList();
        }
        return this.devicePort;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static DevicePorts read(XmlPullParser _parser) throws XmlPullParserException, IOException, DatatypeConfigurationException {
        int type;
        DevicePorts _instance = new DevicePorts();
        _parser.getDepth();
        while (true) {
            type = _parser.next();
            if (type == 1 || type == 3) {
                break;
            } else if (_parser.getEventType() == 2) {
                String _tagName = _parser.getName();
                if (_tagName.equals("devicePort")) {
                    DevicePort _value = DevicePort.read(_parser);
                    _instance.getDevicePort().add(_value);
                } else {
                    XmlParser.skip(_parser);
                }
            }
        }
        if (type != 3) {
            throw new DatatypeConfigurationException("DevicePorts is not closed");
        }
        return _instance;
    }
}
