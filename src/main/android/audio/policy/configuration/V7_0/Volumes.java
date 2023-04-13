package android.audio.policy.configuration.V7_0;

import android.speech.tts.TextToSpeech;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.datatype.DatatypeConfigurationException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes.dex */
public class Volumes {
    private List<Reference> reference;
    private List<Volume> volume;

    public List<Volume> getVolume() {
        if (this.volume == null) {
            this.volume = new ArrayList();
        }
        return this.volume;
    }

    public List<Reference> getReference() {
        if (this.reference == null) {
            this.reference = new ArrayList();
        }
        return this.reference;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static Volumes read(XmlPullParser _parser) throws XmlPullParserException, IOException, DatatypeConfigurationException {
        int type;
        Volumes _instance = new Volumes();
        _parser.getDepth();
        while (true) {
            type = _parser.next();
            if (type == 1 || type == 3) {
                break;
            } else if (_parser.getEventType() == 2) {
                String _tagName = _parser.getName();
                if (_tagName.equals(TextToSpeech.Engine.KEY_PARAM_VOLUME)) {
                    Volume _value = Volume.read(_parser);
                    _instance.getVolume().add(_value);
                } else if (_tagName.equals("reference")) {
                    Reference _value2 = Reference.read(_parser);
                    _instance.getReference().add(_value2);
                } else {
                    XmlParser.skip(_parser);
                }
            }
        }
        if (type != 3) {
            throw new DatatypeConfigurationException("Volumes is not closed");
        }
        return _instance;
    }
}
