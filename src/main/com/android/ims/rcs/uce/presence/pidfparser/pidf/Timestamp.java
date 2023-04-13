package com.android.ims.rcs.uce.presence.pidfparser.pidf;

import android.text.TextUtils;
import com.android.ims.rcs.uce.presence.pidfparser.ElementBase;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;
/* loaded from: classes.dex */
public class Timestamp extends ElementBase {
    public static final String ELEMENT_NAME = "timestamp";
    private String mTimestamp;

    public Timestamp() {
    }

    public Timestamp(String timestamp) {
        this.mTimestamp = timestamp;
    }

    @Override // com.android.ims.rcs.uce.presence.pidfparser.ElementBase
    protected String initNamespace() {
        return PidfConstant.NAMESPACE;
    }

    @Override // com.android.ims.rcs.uce.presence.pidfparser.ElementBase
    protected String initElementName() {
        return "timestamp";
    }

    public String getValue() {
        return this.mTimestamp;
    }

    @Override // com.android.ims.rcs.uce.presence.pidfparser.ElementBase
    public void serialize(XmlSerializer serializer) throws IOException {
        if (this.mTimestamp == null) {
            return;
        }
        String namespace = getNamespace();
        String element = getElementName();
        serializer.startTag(namespace, element);
        serializer.text(this.mTimestamp);
        serializer.endTag(namespace, element);
    }

    @Override // com.android.ims.rcs.uce.presence.pidfparser.ElementBase
    public void parse(XmlPullParser parser) throws IOException, XmlPullParserException {
        String namespace = parser.getNamespace();
        String name = parser.getName();
        if (!verifyParsingElement(namespace, name)) {
            throw new XmlPullParserException("Incorrect element: " + namespace + ", " + name);
        }
        int eventType = parser.next();
        if (eventType == 4) {
            String timestamp = parser.getText();
            if (!TextUtils.isEmpty(timestamp)) {
                this.mTimestamp = timestamp;
            }
        }
        moveToElementEndTag(parser, eventType);
    }
}
