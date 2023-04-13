package com.android.ims.rcs.uce.presence.pidfparser.capabilities;

import android.text.TextUtils;
import com.android.ims.rcs.uce.presence.pidfparser.ElementBase;
import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;
/* loaded from: classes.dex */
public class Duplex extends ElementBase {
    public static final String DUPLEX_FULL = "full";
    public static final String DUPLEX_HALF = "half";
    public static final String DUPLEX_RECEIVE_ONLY = "receive-only";
    public static final String DUPLEX_SEND_ONLY = "send-only";
    public static final String ELEMENT_NAME = "duplex";
    public static final String ELEMENT_NOT_SUPPORTED = "notsupported";
    public static final String ELEMENT_SUPPORTED = "supported";
    private final List<String> mSupportedTypeList = new ArrayList();
    private final List<String> mNotSupportedTypeList = new ArrayList();

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface DuplexType {
    }

    @Override // com.android.ims.rcs.uce.presence.pidfparser.ElementBase
    protected String initNamespace() {
        return CapsConstant.NAMESPACE;
    }

    @Override // com.android.ims.rcs.uce.presence.pidfparser.ElementBase
    protected String initElementName() {
        return ELEMENT_NAME;
    }

    public void addSupportedType(String type) {
        this.mSupportedTypeList.add(type);
    }

    public List<String> getSupportedTypes() {
        return Collections.unmodifiableList(this.mSupportedTypeList);
    }

    public void addNotSupportedType(String type) {
        this.mNotSupportedTypeList.add(type);
    }

    public List<String> getNotSupportedTypes() {
        return Collections.unmodifiableList(this.mNotSupportedTypeList);
    }

    @Override // com.android.ims.rcs.uce.presence.pidfparser.ElementBase
    public void serialize(XmlSerializer serializer) throws IOException {
        if (this.mSupportedTypeList.isEmpty() && this.mNotSupportedTypeList.isEmpty()) {
            return;
        }
        String namespace = getNamespace();
        String elementName = getElementName();
        serializer.startTag(namespace, elementName);
        for (String supportedType : this.mSupportedTypeList) {
            serializer.startTag(namespace, ELEMENT_SUPPORTED);
            serializer.startTag(namespace, supportedType);
            serializer.endTag(namespace, supportedType);
            serializer.endTag(namespace, ELEMENT_SUPPORTED);
        }
        for (String notSupportedType : this.mNotSupportedTypeList) {
            serializer.startTag(namespace, ELEMENT_NOT_SUPPORTED);
            serializer.startTag(namespace, notSupportedType);
            serializer.endTag(namespace, notSupportedType);
            serializer.endTag(namespace, ELEMENT_NOT_SUPPORTED);
        }
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
        do {
            if (eventType != 3 || !getNamespace().equals(parser.getNamespace()) || !getElementName().equals(parser.getName())) {
                if (eventType == 2) {
                    String tagName = parser.getName();
                    if (ELEMENT_SUPPORTED.equals(tagName)) {
                        String duplexType = getDuplexType(parser);
                        if (!TextUtils.isEmpty(duplexType)) {
                            addSupportedType(duplexType);
                        }
                    } else if (ELEMENT_NOT_SUPPORTED.equals(tagName)) {
                        String duplexType2 = getDuplexType(parser);
                        if (!TextUtils.isEmpty(duplexType2)) {
                            addNotSupportedType(duplexType2);
                        }
                    }
                }
                eventType = parser.next();
            } else {
                return;
            }
        } while (eventType != 1);
    }

    private String getDuplexType(XmlPullParser parser) throws IOException, XmlPullParserException {
        int eventType = parser.next();
        String name = parser.getName();
        if (eventType == 2) {
            if (DUPLEX_FULL.equals(name) || DUPLEX_HALF.equals(name) || DUPLEX_RECEIVE_ONLY.equals(name) || DUPLEX_SEND_ONLY.equals(name)) {
                return name;
            }
            return null;
        }
        return null;
    }
}
