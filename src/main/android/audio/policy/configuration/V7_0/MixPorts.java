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
public class MixPorts {
    private List<MixPort> mixPort;

    /* loaded from: classes.dex */
    public static class MixPort {
        private List<AudioInOutFlag> flags;
        private Gains gains;
        private Long maxActiveCount;
        private Long maxOpenCount;
        private String name;
        private List<AudioUsage> preferredUsage;
        private List<Profile> profile;
        private Role role;

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

        public String getName() {
            return this.name;
        }

        boolean hasName() {
            if (this.name == null) {
                return false;
            }
            return true;
        }

        public void setName(String name) {
            this.name = name;
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

        public List<AudioInOutFlag> getFlags() {
            if (this.flags == null) {
                this.flags = new ArrayList();
            }
            return this.flags;
        }

        boolean hasFlags() {
            if (this.flags == null) {
                return false;
            }
            return true;
        }

        public void setFlags(List<AudioInOutFlag> flags) {
            this.flags = flags;
        }

        public long getMaxOpenCount() {
            Long l = this.maxOpenCount;
            if (l == null) {
                return 0L;
            }
            return l.longValue();
        }

        boolean hasMaxOpenCount() {
            if (this.maxOpenCount == null) {
                return false;
            }
            return true;
        }

        public void setMaxOpenCount(long maxOpenCount) {
            this.maxOpenCount = Long.valueOf(maxOpenCount);
        }

        public long getMaxActiveCount() {
            Long l = this.maxActiveCount;
            if (l == null) {
                return 0L;
            }
            return l.longValue();
        }

        boolean hasMaxActiveCount() {
            if (this.maxActiveCount == null) {
                return false;
            }
            return true;
        }

        public void setMaxActiveCount(long maxActiveCount) {
            this.maxActiveCount = Long.valueOf(maxActiveCount);
        }

        public List<AudioUsage> getPreferredUsage() {
            if (this.preferredUsage == null) {
                this.preferredUsage = new ArrayList();
            }
            return this.preferredUsage;
        }

        boolean hasPreferredUsage() {
            if (this.preferredUsage == null) {
                return false;
            }
            return true;
        }

        public void setPreferredUsage(List<AudioUsage> preferredUsage) {
            this.preferredUsage = preferredUsage;
        }

        static MixPort read(XmlPullParser _parser) throws XmlPullParserException, IOException, DatatypeConfigurationException {
            int type;
            String[] split;
            String[] split2;
            MixPort _instance = new MixPort();
            String _raw = _parser.getAttributeValue(null, "name");
            if (_raw != null) {
                _instance.setName(_raw);
            }
            String _raw2 = _parser.getAttributeValue(null, Context.ROLE_SERVICE);
            if (_raw2 != null) {
                Role _value = Role.fromString(_raw2);
                _instance.setRole(_value);
            }
            String _raw3 = _parser.getAttributeValue(null, "flags");
            if (_raw3 != null) {
                List<AudioInOutFlag> _value2 = new ArrayList<>();
                for (String _token : _raw3.split("\\s+")) {
                    _value2.add(AudioInOutFlag.fromString(_token));
                }
                _instance.setFlags(_value2);
            }
            String _raw4 = _parser.getAttributeValue(null, "maxOpenCount");
            if (_raw4 != null) {
                long _value3 = Long.parseLong(_raw4);
                _instance.setMaxOpenCount(_value3);
            }
            String _raw5 = _parser.getAttributeValue(null, "maxActiveCount");
            if (_raw5 != null) {
                long _value4 = Long.parseLong(_raw5);
                _instance.setMaxActiveCount(_value4);
            }
            String _raw6 = _parser.getAttributeValue(null, "preferredUsage");
            if (_raw6 != null) {
                List<AudioUsage> _value5 = new ArrayList<>();
                for (String _token2 : _raw6.split("\\s+")) {
                    _value5.add(AudioUsage.fromString(_token2));
                }
                _instance.setPreferredUsage(_value5);
            }
            _parser.getDepth();
            while (true) {
                type = _parser.next();
                if (type == 1 || type == 3) {
                    break;
                } else if (_parser.getEventType() == 2) {
                    String _tagName = _parser.getName();
                    if (_tagName.equals(MediaFormat.KEY_PROFILE)) {
                        Profile _value6 = Profile.read(_parser);
                        _instance.getProfile().add(_value6);
                    } else if (_tagName.equals("gains")) {
                        Gains _value7 = Gains.read(_parser);
                        _instance.setGains(_value7);
                    } else {
                        XmlParser.skip(_parser);
                    }
                }
            }
            if (type != 3) {
                throw new DatatypeConfigurationException("MixPorts.MixPort is not closed");
            }
            return _instance;
        }
    }

    public List<MixPort> getMixPort() {
        if (this.mixPort == null) {
            this.mixPort = new ArrayList();
        }
        return this.mixPort;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static MixPorts read(XmlPullParser _parser) throws XmlPullParserException, IOException, DatatypeConfigurationException {
        int type;
        MixPorts _instance = new MixPorts();
        _parser.getDepth();
        while (true) {
            type = _parser.next();
            if (type == 1 || type == 3) {
                break;
            } else if (_parser.getEventType() == 2) {
                String _tagName = _parser.getName();
                if (_tagName.equals("mixPort")) {
                    MixPort _value = MixPort.read(_parser);
                    _instance.getMixPort().add(_value);
                } else {
                    XmlParser.skip(_parser);
                }
            }
        }
        if (type != 3) {
            throw new DatatypeConfigurationException("MixPorts is not closed");
        }
        return _instance;
    }
}
