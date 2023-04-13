package android.audio.policy.configuration.V7_0;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.datatype.DatatypeConfigurationException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes.dex */
public class Routes {
    private List<Route> route;

    /* loaded from: classes.dex */
    public static class Route {
        private String sink;
        private String sources;
        private MixType type;

        public MixType getType() {
            return this.type;
        }

        boolean hasType() {
            if (this.type == null) {
                return false;
            }
            return true;
        }

        public void setType(MixType type) {
            this.type = type;
        }

        public String getSink() {
            return this.sink;
        }

        boolean hasSink() {
            if (this.sink == null) {
                return false;
            }
            return true;
        }

        public void setSink(String sink) {
            this.sink = sink;
        }

        public String getSources() {
            return this.sources;
        }

        boolean hasSources() {
            if (this.sources == null) {
                return false;
            }
            return true;
        }

        public void setSources(String sources) {
            this.sources = sources;
        }

        static Route read(XmlPullParser _parser) throws XmlPullParserException, IOException, DatatypeConfigurationException {
            Route _instance = new Route();
            String _raw = _parser.getAttributeValue(null, "type");
            if (_raw != null) {
                MixType _value = MixType.fromString(_raw);
                _instance.setType(_value);
            }
            String _raw2 = _parser.getAttributeValue(null, "sink");
            if (_raw2 != null) {
                _instance.setSink(_raw2);
            }
            String _raw3 = _parser.getAttributeValue(null, "sources");
            if (_raw3 != null) {
                _instance.setSources(_raw3);
            }
            XmlParser.skip(_parser);
            return _instance;
        }
    }

    public List<Route> getRoute() {
        if (this.route == null) {
            this.route = new ArrayList();
        }
        return this.route;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Routes read(XmlPullParser _parser) throws XmlPullParserException, IOException, DatatypeConfigurationException {
        int type;
        Routes _instance = new Routes();
        _parser.getDepth();
        while (true) {
            type = _parser.next();
            if (type == 1 || type == 3) {
                break;
            } else if (_parser.getEventType() == 2) {
                String _tagName = _parser.getName();
                if (_tagName.equals("route")) {
                    Route _value = Route.read(_parser);
                    _instance.getRoute().add(_value);
                } else {
                    XmlParser.skip(_parser);
                }
            }
        }
        if (type != 3) {
            throw new DatatypeConfigurationException("Routes is not closed");
        }
        return _instance;
    }
}
