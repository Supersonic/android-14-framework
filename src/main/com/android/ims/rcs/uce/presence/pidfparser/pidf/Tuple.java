package com.android.ims.rcs.uce.presence.pidfparser.pidf;

import com.android.ims.rcs.uce.presence.pidfparser.ElementBase;
import com.android.ims.rcs.uce.presence.pidfparser.capabilities.ServiceCaps;
import com.android.ims.rcs.uce.presence.pidfparser.omapres.ServiceDescription;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;
/* loaded from: classes.dex */
public class Tuple extends ElementBase {
    private static final String ATTRIBUTE_NAME_TUPLE_ID = "id";
    public static final String ELEMENT_NAME = "tuple";
    private Contact mContact;
    private ServiceCaps mServiceCaps;
    private ServiceDescription mServiceDescription;
    private Status mStatus;
    private Timestamp mTimestamp;
    private static long sTupleId = 0;
    private static final Object LOCK = new Object();
    private List<Note> mNoteList = new ArrayList();
    private String mId = getTupleId();
    private boolean mMalformed = false;

    @Override // com.android.ims.rcs.uce.presence.pidfparser.ElementBase
    protected String initNamespace() {
        return PidfConstant.NAMESPACE;
    }

    @Override // com.android.ims.rcs.uce.presence.pidfparser.ElementBase
    protected String initElementName() {
        return ELEMENT_NAME;
    }

    public void setStatus(Status status) {
        this.mStatus = status;
    }

    public Status getStatus() {
        return this.mStatus;
    }

    public void setServiceDescription(ServiceDescription servDescription) {
        this.mServiceDescription = servDescription;
    }

    public ServiceDescription getServiceDescription() {
        return this.mServiceDescription;
    }

    public void setServiceCaps(ServiceCaps serviceCaps) {
        this.mServiceCaps = serviceCaps;
    }

    public ServiceCaps getServiceCaps() {
        return this.mServiceCaps;
    }

    public void setContact(Contact contact) {
        this.mContact = contact;
    }

    public Contact getContact() {
        return this.mContact;
    }

    public void addNote(Note note) {
        this.mNoteList.add(note);
    }

    public List<Note> getNoteList() {
        return Collections.unmodifiableList(this.mNoteList);
    }

    public void setTimestamp(Timestamp timestamp) {
        this.mTimestamp = timestamp;
    }

    public Timestamp getTimestamp() {
        return this.mTimestamp;
    }

    public void setMalformed(boolean malformed) {
        this.mMalformed = malformed;
    }

    public boolean getMalformed() {
        return this.mMalformed;
    }

    @Override // com.android.ims.rcs.uce.presence.pidfparser.ElementBase
    public void serialize(XmlSerializer serializer) throws IOException {
        String namespace = getNamespace();
        String elementName = getElementName();
        serializer.startTag(namespace, elementName);
        serializer.attribute("", ATTRIBUTE_NAME_TUPLE_ID, this.mId);
        this.mStatus.serialize(serializer);
        ServiceDescription serviceDescription = this.mServiceDescription;
        if (serviceDescription != null) {
            serviceDescription.serialize(serializer);
        }
        ServiceCaps serviceCaps = this.mServiceCaps;
        if (serviceCaps != null) {
            serviceCaps.serialize(serializer);
        }
        Contact contact = this.mContact;
        if (contact != null) {
            contact.serialize(serializer);
        }
        for (Note note : this.mNoteList) {
            note.serialize(serializer);
        }
        Timestamp timestamp = this.mTimestamp;
        if (timestamp != null) {
            timestamp.serialize(serializer);
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
        this.mId = parser.getAttributeValue("", ATTRIBUTE_NAME_TUPLE_ID);
        int eventType = parser.next();
        do {
            if (eventType != 3 || !getNamespace().equals(parser.getNamespace()) || !getElementName().equals(parser.getName())) {
                if (eventType == 2) {
                    String tagName = parser.getName();
                    if (Status.ELEMENT_NAME.equals(tagName)) {
                        Status status = new Status();
                        status.parse(parser);
                        this.mStatus = status;
                    } else if (ServiceDescription.ELEMENT_NAME.equals(tagName)) {
                        ServiceDescription serviceDescription = new ServiceDescription();
                        serviceDescription.parse(parser);
                        this.mServiceDescription = serviceDescription;
                    } else if (ServiceCaps.ELEMENT_NAME.equals(tagName)) {
                        ServiceCaps serviceCaps = new ServiceCaps();
                        serviceCaps.parse(parser);
                        this.mServiceCaps = serviceCaps;
                    } else if (Contact.ELEMENT_NAME.equals(tagName)) {
                        Contact contact = new Contact();
                        contact.parse(parser);
                        this.mContact = contact;
                    } else if (Note.ELEMENT_NAME.equals(tagName)) {
                        Note note = new Note();
                        note.parse(parser);
                        this.mNoteList.add(note);
                    } else if ("timestamp".equals(tagName)) {
                        Timestamp timestamp = new Timestamp();
                        timestamp.parse(parser);
                        this.mTimestamp = timestamp;
                    }
                }
                eventType = parser.next();
            } else {
                return;
            }
        } while (eventType != 1);
    }

    private String getTupleId() {
        String sb;
        synchronized (LOCK) {
            StringBuilder append = new StringBuilder().append("tid");
            long j = sTupleId;
            sTupleId = 1 + j;
            sb = append.append(j).toString();
        }
        return sb;
    }
}
