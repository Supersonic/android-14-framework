package android.audio.policy.configuration.V7_0;

import java.io.IOException;
import javax.xml.datatype.DatatypeConfigurationException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes.dex */
public class SurroundSound {
    private SurroundFormats formats;

    public SurroundFormats getFormats() {
        return this.formats;
    }

    boolean hasFormats() {
        if (this.formats == null) {
            return false;
        }
        return true;
    }

    public void setFormats(SurroundFormats formats) {
        this.formats = formats;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static SurroundSound read(XmlPullParser _parser) throws XmlPullParserException, IOException, DatatypeConfigurationException {
        int type;
        SurroundSound _instance = new SurroundSound();
        _parser.getDepth();
        while (true) {
            type = _parser.next();
            if (type == 1 || type == 3) {
                break;
            } else if (_parser.getEventType() == 2) {
                String _tagName = _parser.getName();
                if (_tagName.equals("formats")) {
                    SurroundFormats _value = SurroundFormats.read(_parser);
                    _instance.setFormats(_value);
                } else {
                    XmlParser.skip(_parser);
                }
            }
        }
        if (type != 3) {
            throw new DatatypeConfigurationException("SurroundSound is not closed");
        }
        return _instance;
    }
}
