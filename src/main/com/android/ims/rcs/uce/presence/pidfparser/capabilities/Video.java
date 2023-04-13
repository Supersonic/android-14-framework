package com.android.ims.rcs.uce.presence.pidfparser.capabilities;

import android.text.TextUtils;
import com.android.ims.rcs.uce.presence.pidfparser.ElementBase;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;
/* loaded from: classes.dex */
public class Video extends ElementBase {
    public static final String ELEMENT_NAME = "video";
    private boolean mSupported;

    public Video() {
    }

    public Video(boolean supported) {
        this.mSupported = supported;
    }

    @Override // com.android.ims.rcs.uce.presence.pidfparser.ElementBase
    protected String initNamespace() {
        return CapsConstant.NAMESPACE;
    }

    @Override // com.android.ims.rcs.uce.presence.pidfparser.ElementBase
    protected String initElementName() {
        return "video";
    }

    public boolean isVideoSupported() {
        return this.mSupported;
    }

    @Override // com.android.ims.rcs.uce.presence.pidfparser.ElementBase
    public void serialize(XmlSerializer serializer) throws IOException {
        String namespace = getNamespace();
        String elementName = getElementName();
        serializer.startTag(namespace, elementName);
        serializer.text(String.valueOf(isVideoSupported()));
        serializer.endTag(namespace, elementName);
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
            String isSupported = parser.getText();
            if (!TextUtils.isEmpty(isSupported)) {
                this.mSupported = Boolean.parseBoolean(isSupported);
            }
        }
        moveToElementEndTag(parser, eventType);
    }
}
