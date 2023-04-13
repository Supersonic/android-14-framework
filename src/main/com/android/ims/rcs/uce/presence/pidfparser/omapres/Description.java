package com.android.ims.rcs.uce.presence.pidfparser.omapres;

import android.text.TextUtils;
import com.android.ims.rcs.uce.presence.pidfparser.ElementBase;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;
/* loaded from: classes.dex */
public class Description extends ElementBase {
    public static final String ELEMENT_NAME = "description";
    private String mDescription;

    public Description() {
    }

    public Description(String description) {
        this.mDescription = description;
    }

    @Override // com.android.ims.rcs.uce.presence.pidfparser.ElementBase
    protected String initNamespace() {
        return OmaPresConstant.NAMESPACE;
    }

    @Override // com.android.ims.rcs.uce.presence.pidfparser.ElementBase
    protected String initElementName() {
        return "description";
    }

    public String getValue() {
        return this.mDescription;
    }

    @Override // com.android.ims.rcs.uce.presence.pidfparser.ElementBase
    public void serialize(XmlSerializer serializer) throws IOException {
        if (this.mDescription == null) {
            return;
        }
        String namespace = getNamespace();
        String elementName = getElementName();
        serializer.startTag(namespace, elementName);
        serializer.text(this.mDescription);
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
            String description = parser.getText();
            if (!TextUtils.isEmpty(description)) {
                this.mDescription = description;
            }
        }
        moveToElementEndTag(parser, eventType);
    }
}
