package com.android.ims.rcs.uce.presence.pidfparser.pidf;

import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import com.android.ims.rcs.uce.presence.pidfparser.ElementBase;
import com.android.ims.rcs.uce.util.UceUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;
/* loaded from: classes.dex */
public class Presence extends ElementBase {
    private static final String ATTRIBUTE_NAME_ENTITY = "entity";
    public static final String ELEMENT_NAME = "presence";
    private static final String LOG_TAG = UceUtils.getLogPrefix() + "Presence";
    private String mEntity;
    private final List<Tuple> mTupleList = new ArrayList();
    private final List<Note> mNoteList = new ArrayList();

    public Presence() {
    }

    public Presence(Uri contact) {
        initEntity(contact);
    }

    private void initEntity(Uri contact) {
        this.mEntity = contact.toString();
    }

    public void setEntity(String entity) {
        this.mEntity = entity;
    }

    @Override // com.android.ims.rcs.uce.presence.pidfparser.ElementBase
    protected String initNamespace() {
        return PidfConstant.NAMESPACE;
    }

    @Override // com.android.ims.rcs.uce.presence.pidfparser.ElementBase
    protected String initElementName() {
        return ELEMENT_NAME;
    }

    public String getEntity() {
        return this.mEntity;
    }

    public void addTuple(Tuple tuple) {
        this.mTupleList.add(tuple);
    }

    public List<Tuple> getTupleList() {
        return Collections.unmodifiableList(this.mTupleList);
    }

    public void addNote(Note note) {
        this.mNoteList.add(note);
    }

    public List<Note> getNoteList() {
        return Collections.unmodifiableList(this.mNoteList);
    }

    @Override // com.android.ims.rcs.uce.presence.pidfparser.ElementBase
    public void serialize(XmlSerializer serializer) throws IOException {
        String namespace = getNamespace();
        String elementName = getElementName();
        serializer.startTag(namespace, elementName);
        serializer.attribute("", ATTRIBUTE_NAME_ENTITY, this.mEntity);
        for (Tuple tuple : this.mTupleList) {
            tuple.serialize(serializer);
        }
        for (Note note : this.mNoteList) {
            note.serialize(serializer);
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
        String attributeValue = parser.getAttributeValue("", ATTRIBUTE_NAME_ENTITY);
        this.mEntity = attributeValue;
        if (TextUtils.isEmpty(attributeValue)) {
            throw new XmlPullParserException("Entity uri of presence is empty");
        }
        int eventType = parser.next();
        do {
            if (eventType != 3 || !getNamespace().equals(parser.getNamespace()) || !getElementName().equals(parser.getName())) {
                if (eventType == 2) {
                    String tagName = parser.getName();
                    if (isTupleElement(eventType, tagName)) {
                        Tuple tuple = new Tuple();
                        try {
                            tuple.parse(parser);
                        } catch (XmlPullParserException e) {
                            e.printStackTrace();
                            Log.w(LOG_TAG, "parse: Exception occurred during Tuple parsing.");
                            tuple.setMalformed(true);
                        }
                        this.mTupleList.add(tuple);
                    } else if (isNoteElement(eventType, tagName)) {
                        Note note = new Note();
                        try {
                            note.parse(parser);
                        } catch (XmlPullParserException e2) {
                            e2.printStackTrace();
                            Log.w(LOG_TAG, "parse: Exception occurred during Note parsing.");
                        }
                        this.mNoteList.add(note);
                    }
                }
                eventType = parser.next();
            } else {
                return;
            }
        } while (eventType != 1);
    }

    private boolean isTupleElement(int eventType, String name) {
        return eventType == 2 && Tuple.ELEMENT_NAME.equals(name);
    }

    private boolean isNoteElement(int eventType, String name) {
        return eventType == 2 && Note.ELEMENT_NAME.equals(name);
    }
}
