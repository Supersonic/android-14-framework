package com.android.ims.rcs.uce.presence.pidfparser;

import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;
/* loaded from: classes.dex */
public abstract class ElementBase {
    private String mNamespace = initNamespace();
    private String mElementName = initElementName();

    protected abstract String initElementName();

    protected abstract String initNamespace();

    public abstract void parse(XmlPullParser xmlPullParser) throws IOException, XmlPullParserException;

    public abstract void serialize(XmlSerializer xmlSerializer) throws IOException;

    public String getNamespace() {
        return this.mNamespace;
    }

    public String getElementName() {
        return this.mElementName;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean verifyParsingElement(String namespace, String elementName) {
        if (!getNamespace().equals(namespace) || !getElementName().equals(elementName)) {
            return false;
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void moveToElementEndTag(XmlPullParser parser, int type) throws IOException, XmlPullParserException {
        int eventType = type;
        do {
            if (eventType != 3 || !getNamespace().equals(parser.getNamespace()) || !getElementName().equals(parser.getName())) {
                eventType = parser.next();
            } else {
                return;
            }
        } while (eventType != 1);
    }
}
