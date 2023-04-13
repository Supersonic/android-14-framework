package android.audio.policy.configuration.V7_0;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.datatype.DatatypeConfigurationException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes.dex */
public class Volume {
    private DeviceCategory deviceCategory;
    private List<String> point;
    private String ref;
    private AudioStreamType stream;

    public List<String> getPoint() {
        if (this.point == null) {
            this.point = new ArrayList();
        }
        return this.point;
    }

    public AudioStreamType getStream() {
        return this.stream;
    }

    boolean hasStream() {
        if (this.stream == null) {
            return false;
        }
        return true;
    }

    public void setStream(AudioStreamType stream) {
        this.stream = stream;
    }

    public DeviceCategory getDeviceCategory() {
        return this.deviceCategory;
    }

    boolean hasDeviceCategory() {
        if (this.deviceCategory == null) {
            return false;
        }
        return true;
    }

    public void setDeviceCategory(DeviceCategory deviceCategory) {
        this.deviceCategory = deviceCategory;
    }

    public String getRef() {
        return this.ref;
    }

    boolean hasRef() {
        if (this.ref == null) {
            return false;
        }
        return true;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Volume read(XmlPullParser _parser) throws XmlPullParserException, IOException, DatatypeConfigurationException {
        int type;
        Volume _instance = new Volume();
        String _raw = _parser.getAttributeValue(null, "stream");
        if (_raw != null) {
            AudioStreamType _value = AudioStreamType.fromString(_raw);
            _instance.setStream(_value);
        }
        String _raw2 = _parser.getAttributeValue(null, "deviceCategory");
        if (_raw2 != null) {
            DeviceCategory _value2 = DeviceCategory.fromString(_raw2);
            _instance.setDeviceCategory(_value2);
        }
        String _raw3 = _parser.getAttributeValue(null, "ref");
        if (_raw3 != null) {
            _instance.setRef(_raw3);
        }
        _parser.getDepth();
        while (true) {
            type = _parser.next();
            if (type == 1 || type == 3) {
                break;
            } else if (_parser.getEventType() == 2) {
                String _tagName = _parser.getName();
                if (_tagName.equals("point")) {
                    _instance.getPoint().add(XmlParser.readText(_parser));
                } else {
                    XmlParser.skip(_parser);
                }
            }
        }
        if (type != 3) {
            throw new DatatypeConfigurationException("Volume is not closed");
        }
        return _instance;
    }
}
