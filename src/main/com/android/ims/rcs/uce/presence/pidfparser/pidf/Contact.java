package com.android.ims.rcs.uce.presence.pidfparser.pidf;

import android.text.TextUtils;
import com.android.ims.rcs.uce.presence.pidfparser.ElementBase;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;
/* loaded from: classes.dex */
public class Contact extends ElementBase {
    public static final String ELEMENT_NAME = "contact";
    private String mContact;
    private Double mPriority;

    @Override // com.android.ims.rcs.uce.presence.pidfparser.ElementBase
    protected String initNamespace() {
        return PidfConstant.NAMESPACE;
    }

    @Override // com.android.ims.rcs.uce.presence.pidfparser.ElementBase
    protected String initElementName() {
        return ELEMENT_NAME;
    }

    public void setPriority(Double priority) {
        this.mPriority = priority;
    }

    public Double getPriority() {
        return this.mPriority;
    }

    public void setContact(String contact) {
        this.mContact = contact;
    }

    public String getContact() {
        return this.mContact;
    }

    @Override // com.android.ims.rcs.uce.presence.pidfparser.ElementBase
    public void serialize(XmlSerializer serializer) throws IOException {
        if (this.mContact == null) {
            return;
        }
        String namespace = getNamespace();
        String elementName = getElementName();
        serializer.startTag(namespace, elementName);
        Double d = this.mPriority;
        if (d != null) {
            serializer.attribute("", "priority", String.valueOf(d));
        }
        serializer.text(this.mContact);
        serializer.endTag(namespace, elementName);
    }

    @Override // com.android.ims.rcs.uce.presence.pidfparser.ElementBase
    public void parse(XmlPullParser parser) throws IOException, XmlPullParserException {
        String namespace = parser.getNamespace();
        String name = parser.getName();
        if (!verifyParsingElement(namespace, name)) {
            throw new XmlPullParserException("Incorrect element: " + namespace + ", " + name);
        }
        String priority = parser.getAttributeValue("", "priority");
        if (!TextUtils.isEmpty(priority)) {
            this.mPriority = Double.valueOf(Double.parseDouble(priority));
        }
        int eventType = parser.next();
        if (eventType == 4) {
            String contact = parser.getText();
            if (!TextUtils.isEmpty(contact)) {
                this.mContact = contact;
            }
        }
        moveToElementEndTag(parser, eventType);
    }
}
