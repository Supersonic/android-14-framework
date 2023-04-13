package android.audio.policy.configuration.V7_0;

import android.provider.Telephony;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.datatype.DatatypeConfigurationException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes.dex */
public class SurroundFormats {
    private List<Format> format;

    /* loaded from: classes.dex */
    public static class Format {
        private String name;
        private List<String> subformats;

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

        public List<String> getSubformats() {
            if (this.subformats == null) {
                this.subformats = new ArrayList();
            }
            return this.subformats;
        }

        boolean hasSubformats() {
            if (this.subformats == null) {
                return false;
            }
            return true;
        }

        public void setSubformats(List<String> subformats) {
            this.subformats = subformats;
        }

        static Format read(XmlPullParser _parser) throws XmlPullParserException, IOException, DatatypeConfigurationException {
            String[] split;
            Format _instance = new Format();
            String _raw = _parser.getAttributeValue(null, "name");
            if (_raw != null) {
                _instance.setName(_raw);
            }
            String _raw2 = _parser.getAttributeValue(null, "subformats");
            if (_raw2 != null) {
                List<String> _value = new ArrayList<>();
                for (String _token : _raw2.split("\\s+")) {
                    _value.add(_token);
                }
                _instance.setSubformats(_value);
            }
            XmlParser.skip(_parser);
            return _instance;
        }
    }

    public List<Format> getFormat() {
        if (this.format == null) {
            this.format = new ArrayList();
        }
        return this.format;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static SurroundFormats read(XmlPullParser _parser) throws XmlPullParserException, IOException, DatatypeConfigurationException {
        int type;
        SurroundFormats _instance = new SurroundFormats();
        _parser.getDepth();
        while (true) {
            type = _parser.next();
            if (type == 1 || type == 3) {
                break;
            } else if (_parser.getEventType() == 2) {
                String _tagName = _parser.getName();
                if (_tagName.equals(Telephony.CellBroadcasts.MESSAGE_FORMAT)) {
                    Format _value = Format.read(_parser);
                    _instance.getFormat().add(_value);
                } else {
                    XmlParser.skip(_parser);
                }
            }
        }
        if (type != 3) {
            throw new DatatypeConfigurationException("SurroundFormats is not closed");
        }
        return _instance;
    }
}
