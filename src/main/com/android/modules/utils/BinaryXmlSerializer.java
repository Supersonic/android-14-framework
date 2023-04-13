package com.android.modules.utils;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import org.xmlpull.v1.XmlSerializer;
/* loaded from: classes5.dex */
public class BinaryXmlSerializer implements TypedXmlSerializer {
    static final int ATTRIBUTE = 15;
    public static final byte[] PROTOCOL_MAGIC_VERSION_0 = {65, 66, 88, 0};
    static final int TYPE_BOOLEAN_FALSE = 208;
    static final int TYPE_BOOLEAN_TRUE = 192;
    static final int TYPE_BYTES_BASE64 = 80;
    static final int TYPE_BYTES_HEX = 64;
    static final int TYPE_DOUBLE = 176;
    static final int TYPE_FLOAT = 160;
    static final int TYPE_INT = 96;
    static final int TYPE_INT_HEX = 112;
    static final int TYPE_LONG = 128;
    static final int TYPE_LONG_HEX = 144;
    static final int TYPE_NULL = 16;
    static final int TYPE_STRING = 32;
    static final int TYPE_STRING_INTERNED = 48;
    private FastDataOutput mOut;
    private int mTagCount = 0;
    private String[] mTagNames;

    private void writeToken(int token, String text) throws IOException {
        if (text != null) {
            this.mOut.writeByte(token | 32);
            this.mOut.writeUTF(text);
            return;
        }
        this.mOut.writeByte(token | 16);
    }

    @Override // org.xmlpull.v1.XmlSerializer
    public void setOutput(OutputStream os, String encoding) throws IOException {
        if (encoding != null && !StandardCharsets.UTF_8.name().equalsIgnoreCase(encoding)) {
            throw new UnsupportedOperationException();
        }
        FastDataOutput obtainFastDataOutput = obtainFastDataOutput(os);
        this.mOut = obtainFastDataOutput;
        obtainFastDataOutput.write(PROTOCOL_MAGIC_VERSION_0);
        this.mTagCount = 0;
        this.mTagNames = new String[8];
    }

    protected FastDataOutput obtainFastDataOutput(OutputStream os) {
        return FastDataOutput.obtain(os);
    }

    @Override // org.xmlpull.v1.XmlSerializer
    public void setOutput(Writer writer) {
        throw new UnsupportedOperationException();
    }

    @Override // org.xmlpull.v1.XmlSerializer
    public void flush() throws IOException {
        FastDataOutput fastDataOutput = this.mOut;
        if (fastDataOutput != null) {
            fastDataOutput.flush();
        }
    }

    @Override // org.xmlpull.v1.XmlSerializer
    public void startDocument(String encoding, Boolean standalone) throws IOException {
        if (encoding != null && !StandardCharsets.UTF_8.name().equalsIgnoreCase(encoding)) {
            throw new UnsupportedOperationException();
        }
        if (standalone != null && !standalone.booleanValue()) {
            throw new UnsupportedOperationException();
        }
        this.mOut.writeByte(16);
    }

    @Override // org.xmlpull.v1.XmlSerializer
    public void endDocument() throws IOException {
        this.mOut.writeByte(17);
        flush();
        this.mOut.release();
        this.mOut = null;
    }

    @Override // org.xmlpull.v1.XmlSerializer
    public int getDepth() {
        return this.mTagCount;
    }

    @Override // org.xmlpull.v1.XmlSerializer
    public String getNamespace() {
        return "";
    }

    @Override // org.xmlpull.v1.XmlSerializer
    public String getName() {
        return this.mTagNames[this.mTagCount - 1];
    }

    @Override // org.xmlpull.v1.XmlSerializer
    public XmlSerializer startTag(String namespace, String name) throws IOException {
        if (namespace == null || namespace.isEmpty()) {
            int i = this.mTagCount;
            String[] strArr = this.mTagNames;
            if (i == strArr.length) {
                this.mTagNames = (String[]) Arrays.copyOf(strArr, i + (i >> 1));
            }
            String[] strArr2 = this.mTagNames;
            int i2 = this.mTagCount;
            this.mTagCount = i2 + 1;
            strArr2[i2] = name;
            this.mOut.writeByte(50);
            this.mOut.writeInternedUTF(name);
            return this;
        }
        throw illegalNamespace();
    }

    @Override // org.xmlpull.v1.XmlSerializer
    public XmlSerializer endTag(String namespace, String name) throws IOException {
        if (namespace == null || namespace.isEmpty()) {
            this.mTagCount--;
            this.mOut.writeByte(51);
            this.mOut.writeInternedUTF(name);
            return this;
        }
        throw illegalNamespace();
    }

    @Override // org.xmlpull.v1.XmlSerializer
    public XmlSerializer attribute(String namespace, String name, String value) throws IOException {
        if (namespace == null || namespace.isEmpty()) {
            this.mOut.writeByte(47);
            this.mOut.writeInternedUTF(name);
            this.mOut.writeUTF(value);
            return this;
        }
        throw illegalNamespace();
    }

    @Override // com.android.modules.utils.TypedXmlSerializer
    public XmlSerializer attributeInterned(String namespace, String name, String value) throws IOException {
        if (namespace == null || namespace.isEmpty()) {
            this.mOut.writeByte(63);
            this.mOut.writeInternedUTF(name);
            this.mOut.writeInternedUTF(value);
            return this;
        }
        throw illegalNamespace();
    }

    @Override // com.android.modules.utils.TypedXmlSerializer
    public XmlSerializer attributeBytesHex(String namespace, String name, byte[] value) throws IOException {
        if (namespace == null || namespace.isEmpty()) {
            this.mOut.writeByte(79);
            this.mOut.writeInternedUTF(name);
            this.mOut.writeShort(value.length);
            this.mOut.write(value);
            return this;
        }
        throw illegalNamespace();
    }

    @Override // com.android.modules.utils.TypedXmlSerializer
    public XmlSerializer attributeBytesBase64(String namespace, String name, byte[] value) throws IOException {
        if (namespace == null || namespace.isEmpty()) {
            this.mOut.writeByte(95);
            this.mOut.writeInternedUTF(name);
            this.mOut.writeShort(value.length);
            this.mOut.write(value);
            return this;
        }
        throw illegalNamespace();
    }

    @Override // com.android.modules.utils.TypedXmlSerializer
    public XmlSerializer attributeInt(String namespace, String name, int value) throws IOException {
        if (namespace == null || namespace.isEmpty()) {
            this.mOut.writeByte(111);
            this.mOut.writeInternedUTF(name);
            this.mOut.writeInt(value);
            return this;
        }
        throw illegalNamespace();
    }

    @Override // com.android.modules.utils.TypedXmlSerializer
    public XmlSerializer attributeIntHex(String namespace, String name, int value) throws IOException {
        if (namespace == null || namespace.isEmpty()) {
            this.mOut.writeByte(127);
            this.mOut.writeInternedUTF(name);
            this.mOut.writeInt(value);
            return this;
        }
        throw illegalNamespace();
    }

    @Override // com.android.modules.utils.TypedXmlSerializer
    public XmlSerializer attributeLong(String namespace, String name, long value) throws IOException {
        if (namespace == null || namespace.isEmpty()) {
            this.mOut.writeByte(143);
            this.mOut.writeInternedUTF(name);
            this.mOut.writeLong(value);
            return this;
        }
        throw illegalNamespace();
    }

    @Override // com.android.modules.utils.TypedXmlSerializer
    public XmlSerializer attributeLongHex(String namespace, String name, long value) throws IOException {
        if (namespace == null || namespace.isEmpty()) {
            this.mOut.writeByte(159);
            this.mOut.writeInternedUTF(name);
            this.mOut.writeLong(value);
            return this;
        }
        throw illegalNamespace();
    }

    @Override // com.android.modules.utils.TypedXmlSerializer
    public XmlSerializer attributeFloat(String namespace, String name, float value) throws IOException {
        if (namespace == null || namespace.isEmpty()) {
            this.mOut.writeByte(175);
            this.mOut.writeInternedUTF(name);
            this.mOut.writeFloat(value);
            return this;
        }
        throw illegalNamespace();
    }

    @Override // com.android.modules.utils.TypedXmlSerializer
    public XmlSerializer attributeDouble(String namespace, String name, double value) throws IOException {
        if (namespace == null || namespace.isEmpty()) {
            this.mOut.writeByte(191);
            this.mOut.writeInternedUTF(name);
            this.mOut.writeDouble(value);
            return this;
        }
        throw illegalNamespace();
    }

    @Override // com.android.modules.utils.TypedXmlSerializer
    public XmlSerializer attributeBoolean(String namespace, String name, boolean value) throws IOException {
        if (namespace == null || namespace.isEmpty()) {
            if (value) {
                this.mOut.writeByte(207);
                this.mOut.writeInternedUTF(name);
            } else {
                this.mOut.writeByte(223);
                this.mOut.writeInternedUTF(name);
            }
            return this;
        }
        throw illegalNamespace();
    }

    @Override // org.xmlpull.v1.XmlSerializer
    public XmlSerializer text(char[] buf, int start, int len) throws IOException {
        writeToken(4, new String(buf, start, len));
        return this;
    }

    @Override // org.xmlpull.v1.XmlSerializer
    public XmlSerializer text(String text) throws IOException {
        writeToken(4, text);
        return this;
    }

    @Override // org.xmlpull.v1.XmlSerializer
    public void cdsect(String text) throws IOException {
        writeToken(5, text);
    }

    @Override // org.xmlpull.v1.XmlSerializer
    public void entityRef(String text) throws IOException {
        writeToken(6, text);
    }

    @Override // org.xmlpull.v1.XmlSerializer
    public void processingInstruction(String text) throws IOException {
        writeToken(8, text);
    }

    @Override // org.xmlpull.v1.XmlSerializer
    public void comment(String text) throws IOException {
        writeToken(9, text);
    }

    @Override // org.xmlpull.v1.XmlSerializer
    public void docdecl(String text) throws IOException {
        writeToken(10, text);
    }

    @Override // org.xmlpull.v1.XmlSerializer
    public void ignorableWhitespace(String text) throws IOException {
        writeToken(7, text);
    }

    @Override // org.xmlpull.v1.XmlSerializer
    public void setFeature(String name, boolean state) {
        if ("http://xmlpull.org/v1/doc/features.html#indent-output".equals(name)) {
            return;
        }
        throw new UnsupportedOperationException();
    }

    @Override // org.xmlpull.v1.XmlSerializer
    public boolean getFeature(String name) {
        throw new UnsupportedOperationException();
    }

    @Override // org.xmlpull.v1.XmlSerializer
    public void setProperty(String name, Object value) {
        throw new UnsupportedOperationException();
    }

    @Override // org.xmlpull.v1.XmlSerializer
    public Object getProperty(String name) {
        throw new UnsupportedOperationException();
    }

    @Override // org.xmlpull.v1.XmlSerializer
    public void setPrefix(String prefix, String namespace) {
        throw new UnsupportedOperationException();
    }

    @Override // org.xmlpull.v1.XmlSerializer
    public String getPrefix(String namespace, boolean generatePrefix) {
        throw new UnsupportedOperationException();
    }

    private static IllegalArgumentException illegalNamespace() {
        throw new IllegalArgumentException("Namespaces are not supported");
    }
}
