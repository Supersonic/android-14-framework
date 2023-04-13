package com.android.internal.util;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Objects;
import org.xmlpull.v1.XmlSerializer;
/* loaded from: classes3.dex */
public class XmlSerializerWrapper implements XmlSerializer {
    private final XmlSerializer mWrapped;

    public XmlSerializerWrapper(XmlSerializer wrapped) {
        this.mWrapped = (XmlSerializer) Objects.requireNonNull(wrapped);
    }

    @Override // org.xmlpull.v1.XmlSerializer
    public void setFeature(String name, boolean state) {
        this.mWrapped.setFeature(name, state);
    }

    @Override // org.xmlpull.v1.XmlSerializer
    public boolean getFeature(String name) {
        return this.mWrapped.getFeature(name);
    }

    @Override // org.xmlpull.v1.XmlSerializer
    public void setProperty(String name, Object value) {
        this.mWrapped.setProperty(name, value);
    }

    @Override // org.xmlpull.v1.XmlSerializer
    public Object getProperty(String name) {
        return this.mWrapped.getProperty(name);
    }

    @Override // org.xmlpull.v1.XmlSerializer
    public void setOutput(OutputStream os, String encoding) throws IOException {
        this.mWrapped.setOutput(os, encoding);
    }

    @Override // org.xmlpull.v1.XmlSerializer
    public void setOutput(Writer writer) throws IOException, IllegalArgumentException, IllegalStateException {
        this.mWrapped.setOutput(writer);
    }

    @Override // org.xmlpull.v1.XmlSerializer
    public void startDocument(String encoding, Boolean standalone) throws IOException {
        this.mWrapped.startDocument(encoding, standalone);
    }

    @Override // org.xmlpull.v1.XmlSerializer
    public void endDocument() throws IOException {
        this.mWrapped.endDocument();
    }

    @Override // org.xmlpull.v1.XmlSerializer
    public void setPrefix(String prefix, String namespace) throws IOException {
        this.mWrapped.setPrefix(prefix, namespace);
    }

    @Override // org.xmlpull.v1.XmlSerializer
    public String getPrefix(String namespace, boolean generatePrefix) {
        return this.mWrapped.getPrefix(namespace, generatePrefix);
    }

    @Override // org.xmlpull.v1.XmlSerializer
    public int getDepth() {
        return this.mWrapped.getDepth();
    }

    @Override // org.xmlpull.v1.XmlSerializer
    public String getNamespace() {
        return this.mWrapped.getNamespace();
    }

    @Override // org.xmlpull.v1.XmlSerializer
    public String getName() {
        return this.mWrapped.getName();
    }

    @Override // org.xmlpull.v1.XmlSerializer
    public XmlSerializer startTag(String namespace, String name) throws IOException {
        return this.mWrapped.startTag(namespace, name);
    }

    @Override // org.xmlpull.v1.XmlSerializer
    public XmlSerializer attribute(String namespace, String name, String value) throws IOException {
        return this.mWrapped.attribute(namespace, name, value);
    }

    @Override // org.xmlpull.v1.XmlSerializer
    public XmlSerializer endTag(String namespace, String name) throws IOException {
        return this.mWrapped.endTag(namespace, name);
    }

    @Override // org.xmlpull.v1.XmlSerializer
    public XmlSerializer text(String text) throws IOException {
        return this.mWrapped.text(text);
    }

    @Override // org.xmlpull.v1.XmlSerializer
    public XmlSerializer text(char[] buf, int start, int len) throws IOException {
        return this.mWrapped.text(buf, start, len);
    }

    @Override // org.xmlpull.v1.XmlSerializer
    public void cdsect(String text) throws IOException, IllegalArgumentException, IllegalStateException {
        this.mWrapped.cdsect(text);
    }

    @Override // org.xmlpull.v1.XmlSerializer
    public void entityRef(String text) throws IOException {
        this.mWrapped.entityRef(text);
    }

    @Override // org.xmlpull.v1.XmlSerializer
    public void processingInstruction(String text) throws IOException {
        this.mWrapped.processingInstruction(text);
    }

    @Override // org.xmlpull.v1.XmlSerializer
    public void comment(String text) throws IOException {
        this.mWrapped.comment(text);
    }

    @Override // org.xmlpull.v1.XmlSerializer
    public void docdecl(String text) throws IOException {
        this.mWrapped.docdecl(text);
    }

    @Override // org.xmlpull.v1.XmlSerializer
    public void ignorableWhitespace(String text) throws IOException {
        this.mWrapped.ignorableWhitespace(text);
    }

    @Override // org.xmlpull.v1.XmlSerializer
    public void flush() throws IOException {
        this.mWrapped.flush();
    }
}
