package com.android.ims.rcs.uce.presence.pidfparser.pidf;

import android.util.Log;
import com.android.ims.rcs.uce.presence.pidfparser.ElementBase;
import com.android.ims.rcs.uce.util.UceUtils;
import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;
/* loaded from: classes.dex */
public class Basic extends ElementBase {
    public static final String CLOSED = "closed";
    public static final String ELEMENT_NAME = "basic";
    private static final String LOG_TAG = UceUtils.getLogPrefix() + "Basic";
    public static final String OPEN = "open";
    private String mBasic;

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface BasicValue {
    }

    public Basic() {
    }

    public Basic(String value) {
        this.mBasic = value;
    }

    @Override // com.android.ims.rcs.uce.presence.pidfparser.ElementBase
    protected String initNamespace() {
        return PidfConstant.NAMESPACE;
    }

    @Override // com.android.ims.rcs.uce.presence.pidfparser.ElementBase
    protected String initElementName() {
        return ELEMENT_NAME;
    }

    public String getValue() {
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
        serializer.text(this.mBasic);
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
            String basicValue = parser.getText();
            if (OPEN.equals(basicValue)) {
                this.mBasic = OPEN;
            } else if (CLOSED.equals(basicValue)) {
                this.mBasic = CLOSED;
            } else {
                this.mBasic = null;
            }
        } else {
            Log.d(LOG_TAG, "The eventType is not TEXT=" + eventType);
        }
        moveToElementEndTag(parser, eventType);
    }
}
