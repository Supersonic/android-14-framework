package com.android.ims.rcs.uce.presence.pidfparser.pidf;

import android.text.TextUtils;
import com.android.ims.rcs.uce.presence.pidfparser.ElementBase;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;
/* loaded from: classes.dex */
public class Note extends ElementBase {
    public static final String ELEMENT_NAME = "note";
    private String mNote;

    public Note() {
    }

    public Note(String note) {
        this.mNote = note;
    }

    @Override // com.android.ims.rcs.uce.presence.pidfparser.ElementBase
    protected String initNamespace() {
        return PidfConstant.NAMESPACE;
    }

    @Override // com.android.ims.rcs.uce.presence.pidfparser.ElementBase
    protected String initElementName() {
        return ELEMENT_NAME;
    }

    public String getNote() {
        return this.mNote;
    }

    @Override // com.android.ims.rcs.uce.presence.pidfparser.ElementBase
    public void serialize(XmlSerializer serializer) throws IOException {
        if (this.mNote == null) {
            return;
        }
        String namespace = getNamespace();
        String element = getElementName();
        serializer.startTag(namespace, element);
        serializer.text(this.mNote);
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
            String note = parser.getText();
            if (!TextUtils.isEmpty(note)) {
                this.mNote = note;
            }
        }
        moveToElementEndTag(parser, eventType);
    }
}
