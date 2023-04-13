package android.audio.policy.configuration.V7_0;

import com.android.ims.ImsConfig;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.datatype.DatatypeConfigurationException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes.dex */
public class AttachedDevices {
    private List<String> item;

    public List<String> getItem() {
        if (this.item == null) {
            this.item = new ArrayList();
        }
        return this.item;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static AttachedDevices read(XmlPullParser _parser) throws XmlPullParserException, IOException, DatatypeConfigurationException {
        int type;
        AttachedDevices _instance = new AttachedDevices();
        _parser.getDepth();
        while (true) {
            type = _parser.next();
            if (type == 1 || type == 3) {
                break;
            } else if (_parser.getEventType() == 2) {
                String _tagName = _parser.getName();
                if (_tagName.equals(ImsConfig.EXTRA_CHANGED_ITEM)) {
                    String _raw = XmlParser.readText(_parser);
                    _instance.getItem().add(_raw);
                } else {
                    XmlParser.skip(_parser);
                }
            }
        }
        if (type != 3) {
            throw new DatatypeConfigurationException("AttachedDevices is not closed");
        }
        return _instance;
    }
}
