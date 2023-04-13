package android.audio.policy.configuration.V7_0;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.datatype.DatatypeConfigurationException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes.dex */
public class Modules {
    private List<Module> module;

    /* loaded from: classes.dex */
    public static class Module {
        private AttachedDevices attachedDevices;
        private String defaultOutputDevice;
        private DevicePorts devicePorts;
        private HalVersion halVersion;
        private MixPorts mixPorts;
        private String name;
        private Routes routes;

        public AttachedDevices getAttachedDevices() {
            return this.attachedDevices;
        }

        boolean hasAttachedDevices() {
            if (this.attachedDevices == null) {
                return false;
            }
            return true;
        }

        public void setAttachedDevices(AttachedDevices attachedDevices) {
            this.attachedDevices = attachedDevices;
        }

        public String getDefaultOutputDevice() {
            return this.defaultOutputDevice;
        }

        boolean hasDefaultOutputDevice() {
            if (this.defaultOutputDevice == null) {
                return false;
            }
            return true;
        }

        public void setDefaultOutputDevice(String defaultOutputDevice) {
            this.defaultOutputDevice = defaultOutputDevice;
        }

        public MixPorts getMixPorts() {
            return this.mixPorts;
        }

        boolean hasMixPorts() {
            if (this.mixPorts == null) {
                return false;
            }
            return true;
        }

        public void setMixPorts(MixPorts mixPorts) {
            this.mixPorts = mixPorts;
        }

        public DevicePorts getDevicePorts() {
            return this.devicePorts;
        }

        boolean hasDevicePorts() {
            if (this.devicePorts == null) {
                return false;
            }
            return true;
        }

        public void setDevicePorts(DevicePorts devicePorts) {
            this.devicePorts = devicePorts;
        }

        public Routes getRoutes() {
            return this.routes;
        }

        boolean hasRoutes() {
            if (this.routes == null) {
                return false;
            }
            return true;
        }

        public void setRoutes(Routes routes) {
            this.routes = routes;
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

        public HalVersion getHalVersion() {
            return this.halVersion;
        }

        boolean hasHalVersion() {
            if (this.halVersion == null) {
                return false;
            }
            return true;
        }

        public void setHalVersion(HalVersion halVersion) {
            this.halVersion = halVersion;
        }

        static Module read(XmlPullParser _parser) throws XmlPullParserException, IOException, DatatypeConfigurationException {
            int type;
            Module _instance = new Module();
            String _raw = _parser.getAttributeValue(null, "name");
            if (_raw != null) {
                _instance.setName(_raw);
            }
            String _raw2 = _parser.getAttributeValue(null, "halVersion");
            if (_raw2 != null) {
                HalVersion _value = HalVersion.fromString(_raw2);
                _instance.setHalVersion(_value);
            }
            _parser.getDepth();
            while (true) {
                type = _parser.next();
                if (type == 1 || type == 3) {
                    break;
                } else if (_parser.getEventType() == 2) {
                    String _tagName = _parser.getName();
                    if (_tagName.equals("attachedDevices")) {
                        AttachedDevices _value2 = AttachedDevices.read(_parser);
                        _instance.setAttachedDevices(_value2);
                    } else if (_tagName.equals("defaultOutputDevice")) {
                        _instance.setDefaultOutputDevice(XmlParser.readText(_parser));
                    } else if (_tagName.equals("mixPorts")) {
                        MixPorts _value3 = MixPorts.read(_parser);
                        _instance.setMixPorts(_value3);
                    } else if (_tagName.equals("devicePorts")) {
                        DevicePorts _value4 = DevicePorts.read(_parser);
                        _instance.setDevicePorts(_value4);
                    } else if (_tagName.equals("routes")) {
                        Routes _value5 = Routes.read(_parser);
                        _instance.setRoutes(_value5);
                    } else {
                        XmlParser.skip(_parser);
                    }
                }
            }
            if (type != 3) {
                throw new DatatypeConfigurationException("Modules.Module is not closed");
            }
            return _instance;
        }
    }

    public List<Module> getModule() {
        if (this.module == null) {
            this.module = new ArrayList();
        }
        return this.module;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Modules read(XmlPullParser _parser) throws XmlPullParserException, IOException, DatatypeConfigurationException {
        int type;
        Modules _instance = new Modules();
        _parser.getDepth();
        while (true) {
            type = _parser.next();
            if (type == 1 || type == 3) {
                break;
            } else if (_parser.getEventType() == 2) {
                String _tagName = _parser.getName();
                if (_tagName.equals("module")) {
                    Module _value = Module.read(_parser);
                    _instance.getModule().add(_value);
                } else {
                    XmlParser.skip(_parser);
                }
            }
        }
        if (type != 3) {
            throw new DatatypeConfigurationException("Modules is not closed");
        }
        return _instance;
    }
}
