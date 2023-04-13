package com.android.ims.rcs.uce.presence.pidfparser.omapres;

import android.text.TextUtils;
import com.android.ims.rcs.uce.presence.pidfparser.ElementBase;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;
/* loaded from: classes.dex */
public class Version extends ElementBase {
    public static final String ELEMENT_NAME = "version";
    private int mMajorVersion;
    private int mMinorVersion;

    public Version() {
    }

    public Version(int majorVersion, int minorVersion) {
        this.mMajorVersion = majorVersion;
        this.mMinorVersion = minorVersion;
    }

    @Override // com.android.ims.rcs.uce.presence.pidfparser.ElementBase
    protected String initNamespace() {
        return OmaPresConstant.NAMESPACE;
    }

    @Override // com.android.ims.rcs.uce.presence.pidfparser.ElementBase
    protected String initElementName() {
        return ELEMENT_NAME;
    }

    public String getValue() {
        StringBuilder builder = new StringBuilder();
        builder.append(this.mMajorVersion).append(".").append(this.mMinorVersion);
        return builder.toString();
    }

    @Override // com.android.ims.rcs.uce.presence.pidfparser.ElementBase
    public void serialize(XmlSerializer serializer) throws IOException {
        String namespace = getNamespace();
        String elementName = getElementName();
        serializer.startTag(namespace, elementName);
        serializer.text(getValue());
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
            String version = parser.getText();
            handleParsedVersion(version);
        }
        moveToElementEndTag(parser, eventType);
    }

    private void handleParsedVersion(String version) {
        String[] versionAry;
        if (!TextUtils.isEmpty(version) && (versionAry = version.split("\\.")) != null && versionAry.length == 2) {
            this.mMajorVersion = Integer.parseInt(versionAry[0]);
            this.mMinorVersion = Integer.parseInt(versionAry[1]);
        }
    }
}
