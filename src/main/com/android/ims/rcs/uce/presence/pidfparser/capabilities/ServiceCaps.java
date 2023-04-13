package com.android.ims.rcs.uce.presence.pidfparser.capabilities;

import com.android.ims.rcs.uce.presence.pidfparser.ElementBase;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;
/* loaded from: classes.dex */
public class ServiceCaps extends ElementBase {
    public static final String ELEMENT_NAME = "servcaps";
    private final List<ElementBase> mElements = new ArrayList();

    @Override // com.android.ims.rcs.uce.presence.pidfparser.ElementBase
    protected String initNamespace() {
        return CapsConstant.NAMESPACE;
    }

    @Override // com.android.ims.rcs.uce.presence.pidfparser.ElementBase
    protected String initElementName() {
        return ELEMENT_NAME;
    }

    public void addElement(ElementBase element) {
        this.mElements.add(element);
    }

    public List<ElementBase> getElements() {
        return this.mElements;
    }

    @Override // com.android.ims.rcs.uce.presence.pidfparser.ElementBase
    public void serialize(XmlSerializer serializer) throws IOException {
        if (this.mElements.isEmpty()) {
            return;
        }
        String namespace = getNamespace();
        String elementName = getElementName();
        serializer.startTag(namespace, elementName);
        for (ElementBase element : this.mElements) {
            element.serialize(serializer);
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
                    if (Audio.ELEMENT_NAME.equals(tagName)) {
                        Audio audio = new Audio();
                        audio.parse(parser);
                        this.mElements.add(audio);
                    } else if ("video".equals(tagName)) {
                        Video video = new Video();
                        video.parse(parser);
                        this.mElements.add(video);
                    } else if (Duplex.ELEMENT_NAME.equals(tagName)) {
                        Duplex duplex = new Duplex();
                        duplex.parse(parser);
                        this.mElements.add(duplex);
                    }
                }
                eventType = parser.next();
            } else {
                return;
            }
        } while (eventType != 1);
    }
}
