package com.android.ims.rcs.uce.presence.pidfparser.omapres;

import com.android.ims.rcs.uce.presence.pidfparser.ElementBase;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;
/* loaded from: classes.dex */
public class ServiceDescription extends ElementBase {
    public static final String ELEMENT_NAME = "service-description";
    private Description mDescription;
    private ServiceId mServiceId;
    private Version mVersion;

    @Override // com.android.ims.rcs.uce.presence.pidfparser.ElementBase
    protected String initNamespace() {
        return OmaPresConstant.NAMESPACE;
    }

    @Override // com.android.ims.rcs.uce.presence.pidfparser.ElementBase
    protected String initElementName() {
        return ELEMENT_NAME;
    }

    public void setServiceId(ServiceId serviceId) {
        this.mServiceId = serviceId;
    }

    public ServiceId getServiceId() {
        return this.mServiceId;
    }

    public void setVersion(Version version) {
        this.mVersion = version;
    }

    public Version getVersion() {
        return this.mVersion;
    }

    public void setDescription(Description description) {
        this.mDescription = description;
    }

    public Description getDescription() {
        return this.mDescription;
    }

    @Override // com.android.ims.rcs.uce.presence.pidfparser.ElementBase
    public void serialize(XmlSerializer serializer) throws IOException {
        if (this.mServiceId == null && this.mVersion == null && this.mDescription == null) {
            return;
        }
        String namespace = getNamespace();
        String element = getElementName();
        serializer.startTag(namespace, element);
        ServiceId serviceId = this.mServiceId;
        if (serviceId != null) {
            serviceId.serialize(serializer);
        }
        Version version = this.mVersion;
        if (version != null) {
            version.serialize(serializer);
        }
        Description description = this.mDescription;
        if (description != null) {
            description.serialize(serializer);
        }
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
        do {
            if (eventType != 3 || !getNamespace().equals(parser.getNamespace()) || !getElementName().equals(parser.getName())) {
                if (eventType == 2) {
                    String tagName = parser.getName();
                    if (ServiceId.ELEMENT_NAME.equals(tagName)) {
                        ServiceId serviceId = new ServiceId();
                        serviceId.parse(parser);
                        this.mServiceId = serviceId;
                    } else if (Version.ELEMENT_NAME.equals(tagName)) {
                        Version version = new Version();
                        version.parse(parser);
                        this.mVersion = version;
                    } else if ("description".equals(tagName)) {
                        Description description = new Description();
                        description.parse(parser);
                        this.mDescription = description;
                    }
                }
                eventType = parser.next();
            } else {
                return;
            }
        } while (eventType != 1);
    }
}
