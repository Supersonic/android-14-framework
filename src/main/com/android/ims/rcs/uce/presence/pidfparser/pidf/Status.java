package com.android.ims.rcs.uce.presence.pidfparser.pidf;

import android.util.Log;
import com.android.ims.rcs.uce.presence.pidfparser.ElementBase;
import com.android.ims.rcs.uce.util.UceUtils;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;
/* loaded from: classes.dex */
public class Status extends ElementBase {
    public static final String ELEMENT_NAME = "status";
    private static final String LOG_TAG = UceUtils.getLogPrefix() + "Status";
    private Basic mBasic;

    @Override // com.android.ims.rcs.uce.presence.pidfparser.ElementBase
    protected String initNamespace() {
        return PidfConstant.NAMESPACE;
    }

    @Override // com.android.ims.rcs.uce.presence.pidfparser.ElementBase
    protected String initElementName() {
        return ELEMENT_NAME;
    }

    public void setBasic(Basic basic) {
        this.mBasic = basic;
    }

    public Basic getBasic() {
        return this.mBasic;
    }

    @Override // com.android.ims.rcs.uce.presence.pidfparser.ElementBase
    public void serialize(XmlSerializer serializer) throws IOException {
        if (this.mBasic == null) {
            return;
        }
        String namespace = getNamespace();
        String element = getElementName();
        serializer.startTag(namespace, element);
        this.mBasic.serialize(serializer);
        serializer.endTag(namespace, element);
    }

    @Override // com.android.ims.rcs.uce.presence.pidfparser.ElementBase
    public void parse(XmlPullParser parser) throws IOException, XmlPullParserException {
        String namespace = parser.getNamespace();
        String name = parser.getName();
        if (!verifyParsingElement(namespace, name)) {
            throw new XmlPullParserException("Incorrect element: " + namespace + ", " + name);
        }
        int eventType = parser.nextTag();
        if (eventType == 2) {
            Basic basic = new Basic();
            basic.parse(parser);
            this.mBasic = basic;
        } else {
            Log.d(LOG_TAG, "The eventType is not START_TAG=" + eventType);
        }
        moveToElementEndTag(parser, eventType);
    }
}
