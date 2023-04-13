package android.content;

import android.net.Uri;
import android.util.Xml;
import java.io.IOException;
import java.io.InputStream;
import java.util.Stack;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
/* loaded from: classes.dex */
public class DefaultDataHandler implements ContentInsertHandler {
    private static final String ARG = "arg";
    private static final String COL = "col";
    private static final String DEL = "del";
    private static final String POSTFIX = "postfix";
    private static final String ROW = "row";
    private static final String SELECT = "select";
    private static final String URI_STR = "uri";
    private ContentResolver mContentResolver;
    private Stack<Uri> mUris = new Stack<>();
    private ContentValues mValues;

    @Override // android.content.ContentInsertHandler
    public void insert(ContentResolver contentResolver, InputStream in) throws IOException, SAXException {
        this.mContentResolver = contentResolver;
        Xml.parse(in, Xml.Encoding.UTF_8, this);
    }

    @Override // android.content.ContentInsertHandler
    public void insert(ContentResolver contentResolver, String in) throws SAXException {
        this.mContentResolver = contentResolver;
        Xml.parse(in, this);
    }

    private void parseRow(Attributes atts) throws SAXException {
        Uri uri;
        String uriStr = atts.getValue("uri");
        if (uriStr != null) {
            uri = Uri.parse(uriStr);
            if (uri == null) {
                throw new SAXException("attribute " + atts.getValue("uri") + " parsing failure");
            }
        } else if (this.mUris.size() > 0) {
            String postfix = atts.getValue(POSTFIX);
            if (postfix != null) {
                uri = Uri.withAppendedPath(this.mUris.lastElement(), postfix);
            } else {
                uri = this.mUris.lastElement();
            }
        } else {
            throw new SAXException("attribute parsing failure");
        }
        this.mUris.push(uri);
    }

    private Uri insertRow() {
        Uri u = this.mContentResolver.insert(this.mUris.lastElement(), this.mValues);
        this.mValues = null;
        return u;
    }

    @Override // org.xml.sax.ContentHandler
    public void startElement(String uri, String localName, String name, Attributes atts) throws SAXException {
        if (ROW.equals(localName)) {
            if (this.mValues != null) {
                if (this.mUris.empty()) {
                    throw new SAXException("uri is empty");
                }
                Uri nextUri = insertRow();
                if (nextUri == null) {
                    throw new SAXException("insert to uri " + this.mUris.lastElement().toString() + " failure");
                }
                this.mUris.pop();
                this.mUris.push(nextUri);
                parseRow(atts);
            } else if (atts.getLength() == 0) {
                Stack<Uri> stack = this.mUris;
                stack.push(stack.lastElement());
            } else {
                parseRow(atts);
            }
        } else if (COL.equals(localName)) {
            int attrLen = atts.getLength();
            if (attrLen != 2) {
                throw new SAXException("illegal attributes number " + attrLen);
            }
            String key = atts.getValue(0);
            String value = atts.getValue(1);
            if (key != null && key.length() > 0 && value != null && value.length() > 0) {
                if (this.mValues == null) {
                    this.mValues = new ContentValues();
                }
                this.mValues.put(key, value);
                return;
            }
            throw new SAXException("illegal attributes value");
        } else if (DEL.equals(localName)) {
            Uri u = Uri.parse(atts.getValue("uri"));
            if (u == null) {
                throw new SAXException("attribute " + atts.getValue("uri") + " parsing failure");
            }
            int attrLen2 = atts.getLength() - 2;
            if (attrLen2 <= 0) {
                if (attrLen2 == 0) {
                    this.mContentResolver.delete(u, atts.getValue(1), null);
                    return;
                } else {
                    this.mContentResolver.delete(u, null, null);
                    return;
                }
            }
            String[] selectionArgs = new String[attrLen2];
            for (int i = 0; i < attrLen2; i++) {
                selectionArgs[i] = atts.getValue(i + 2);
            }
            this.mContentResolver.delete(u, atts.getValue(1), selectionArgs);
        } else {
            throw new SAXException("unknown element: " + localName);
        }
    }

    @Override // org.xml.sax.ContentHandler
    public void endElement(String uri, String localName, String name) throws SAXException {
        if (ROW.equals(localName)) {
            if (this.mUris.empty()) {
                throw new SAXException("uri mismatch");
            }
            if (this.mValues != null) {
                insertRow();
            }
            this.mUris.pop();
        }
    }

    @Override // org.xml.sax.ContentHandler
    public void characters(char[] ch, int start, int length) throws SAXException {
    }

    @Override // org.xml.sax.ContentHandler
    public void endDocument() throws SAXException {
    }

    @Override // org.xml.sax.ContentHandler
    public void endPrefixMapping(String prefix) throws SAXException {
    }

    @Override // org.xml.sax.ContentHandler
    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
    }

    @Override // org.xml.sax.ContentHandler
    public void processingInstruction(String target, String data) throws SAXException {
    }

    @Override // org.xml.sax.ContentHandler
    public void setDocumentLocator(Locator locator) {
    }

    @Override // org.xml.sax.ContentHandler
    public void skippedEntity(String name) throws SAXException {
    }

    @Override // org.xml.sax.ContentHandler
    public void startDocument() throws SAXException {
    }

    @Override // org.xml.sax.ContentHandler
    public void startPrefixMapping(String prefix, String uri) throws SAXException {
    }
}
