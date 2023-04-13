package com.android.internal.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Objects;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes3.dex */
public class XmlPullParserWrapper implements XmlPullParser {
    private final XmlPullParser mWrapped;

    public XmlPullParserWrapper(XmlPullParser wrapped) {
        this.mWrapped = (XmlPullParser) Objects.requireNonNull(wrapped);
    }

    @Override // org.xmlpull.v1.XmlPullParser
    public void setFeature(String name, boolean state) throws XmlPullParserException {
        this.mWrapped.setFeature(name, state);
    }

    @Override // org.xmlpull.v1.XmlPullParser
    public boolean getFeature(String name) {
        return this.mWrapped.getFeature(name);
    }

    @Override // org.xmlpull.v1.XmlPullParser
    public void setProperty(String name, Object value) throws XmlPullParserException {
        this.mWrapped.setProperty(name, value);
    }

    @Override // org.xmlpull.v1.XmlPullParser
    public Object getProperty(String name) {
        return this.mWrapped.getProperty(name);
    }

    @Override // org.xmlpull.v1.XmlPullParser
    public void setInput(Reader in) throws XmlPullParserException {
        this.mWrapped.setInput(in);
    }

    @Override // org.xmlpull.v1.XmlPullParser
    public void setInput(InputStream inputStream, String inputEncoding) throws XmlPullParserException {
        this.mWrapped.setInput(inputStream, inputEncoding);
    }

    @Override // org.xmlpull.v1.XmlPullParser
    public String getInputEncoding() {
        return this.mWrapped.getInputEncoding();
    }

    @Override // org.xmlpull.v1.XmlPullParser
    public void defineEntityReplacementText(String entityName, String replacementText) throws XmlPullParserException {
        this.mWrapped.defineEntityReplacementText(entityName, replacementText);
    }

    @Override // org.xmlpull.v1.XmlPullParser
    public int getNamespaceCount(int depth) throws XmlPullParserException {
        return this.mWrapped.getNamespaceCount(depth);
    }

    @Override // org.xmlpull.v1.XmlPullParser
    public String getNamespacePrefix(int pos) throws XmlPullParserException {
        return this.mWrapped.getNamespacePrefix(pos);
    }

    @Override // org.xmlpull.v1.XmlPullParser
    public String getNamespaceUri(int pos) throws XmlPullParserException {
        return this.mWrapped.getNamespaceUri(pos);
    }

    @Override // org.xmlpull.v1.XmlPullParser
    public String getNamespace(String prefix) {
        return this.mWrapped.getNamespace(prefix);
    }

    @Override // org.xmlpull.v1.XmlPullParser
    public int getDepth() {
        return this.mWrapped.getDepth();
    }

    @Override // org.xmlpull.v1.XmlPullParser
    public String getPositionDescription() {
        return this.mWrapped.getPositionDescription();
    }

    @Override // org.xmlpull.v1.XmlPullParser
    public int getLineNumber() {
        return this.mWrapped.getLineNumber();
    }

    @Override // org.xmlpull.v1.XmlPullParser
    public int getColumnNumber() {
        return this.mWrapped.getColumnNumber();
    }

    @Override // org.xmlpull.v1.XmlPullParser
    public boolean isWhitespace() throws XmlPullParserException {
        return this.mWrapped.isWhitespace();
    }

    @Override // org.xmlpull.v1.XmlPullParser
    public String getText() {
        return this.mWrapped.getText();
    }

    @Override // org.xmlpull.v1.XmlPullParser
    public char[] getTextCharacters(int[] holderForStartAndLength) {
        return this.mWrapped.getTextCharacters(holderForStartAndLength);
    }

    @Override // org.xmlpull.v1.XmlPullParser
    public String getNamespace() {
        return this.mWrapped.getNamespace();
    }

    @Override // org.xmlpull.v1.XmlPullParser
    public String getName() {
        return this.mWrapped.getName();
    }

    @Override // org.xmlpull.v1.XmlPullParser
    public String getPrefix() {
        return this.mWrapped.getPrefix();
    }

    @Override // org.xmlpull.v1.XmlPullParser
    public boolean isEmptyElementTag() throws XmlPullParserException {
        return this.mWrapped.isEmptyElementTag();
    }

    @Override // org.xmlpull.v1.XmlPullParser
    public int getAttributeCount() {
        return this.mWrapped.getAttributeCount();
    }

    @Override // org.xmlpull.v1.XmlPullParser
    public String getAttributeNamespace(int index) {
        return this.mWrapped.getAttributeNamespace(index);
    }

    @Override // org.xmlpull.v1.XmlPullParser
    public String getAttributeName(int index) {
        return this.mWrapped.getAttributeName(index);
    }

    @Override // org.xmlpull.v1.XmlPullParser
    public String getAttributePrefix(int index) {
        return this.mWrapped.getAttributePrefix(index);
    }

    @Override // org.xmlpull.v1.XmlPullParser
    public String getAttributeType(int index) {
        return this.mWrapped.getAttributeType(index);
    }

    @Override // org.xmlpull.v1.XmlPullParser
    public boolean isAttributeDefault(int index) {
        return this.mWrapped.isAttributeDefault(index);
    }

    @Override // org.xmlpull.v1.XmlPullParser
    public String getAttributeValue(int index) {
        return this.mWrapped.getAttributeValue(index);
    }

    @Override // org.xmlpull.v1.XmlPullParser
    public String getAttributeValue(String namespace, String name) {
        return this.mWrapped.getAttributeValue(namespace, name);
    }

    @Override // org.xmlpull.v1.XmlPullParser
    public int getEventType() throws XmlPullParserException {
        return this.mWrapped.getEventType();
    }

    @Override // org.xmlpull.v1.XmlPullParser
    public int next() throws XmlPullParserException, IOException {
        return this.mWrapped.next();
    }

    @Override // org.xmlpull.v1.XmlPullParser
    public int nextToken() throws XmlPullParserException, IOException {
        return this.mWrapped.nextToken();
    }

    @Override // org.xmlpull.v1.XmlPullParser
    public void require(int type, String namespace, String name) throws XmlPullParserException, IOException {
        this.mWrapped.require(type, namespace, name);
    }

    @Override // org.xmlpull.v1.XmlPullParser
    public String nextText() throws XmlPullParserException, IOException {
        return this.mWrapped.nextText();
    }

    @Override // org.xmlpull.v1.XmlPullParser
    public int nextTag() throws XmlPullParserException, IOException {
        return this.mWrapped.nextTag();
    }
}
