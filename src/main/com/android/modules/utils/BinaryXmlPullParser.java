package com.android.modules.utils;

import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Base64;
import com.android.internal.midi.MidiConstants;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Objects;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes5.dex */
public class BinaryXmlPullParser implements TypedXmlPullParser {
    private static final char[] HEX_DIGITS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', DateFormat.AM_PM, 'b', 'c', DateFormat.DATE, 'e', 'f'};
    private Attribute[] mAttributes;
    private String mCurrentName;
    private String mCurrentText;
    private FastDataInput mIn;
    private int mCurrentToken = 0;
    private int mCurrentDepth = 0;
    private int mAttributeCount = 0;

    @Override // org.xmlpull.v1.XmlPullParser
    public void setInput(InputStream is, String encoding) throws XmlPullParserException {
        if (encoding != null && !StandardCharsets.UTF_8.name().equalsIgnoreCase(encoding)) {
            throw new UnsupportedOperationException();
        }
        FastDataInput fastDataInput = this.mIn;
        if (fastDataInput != null) {
            fastDataInput.release();
            this.mIn = null;
        }
        this.mIn = obtainFastDataInput(is);
        this.mCurrentToken = 0;
        this.mCurrentDepth = 0;
        this.mCurrentName = null;
        this.mCurrentText = null;
        this.mAttributeCount = 0;
        this.mAttributes = new Attribute[8];
        int i = 0;
        while (true) {
            Attribute[] attributeArr = this.mAttributes;
            if (i >= attributeArr.length) {
                break;
            }
            attributeArr[i] = new Attribute();
            i++;
        }
        try {
            byte[] magic = new byte[4];
            this.mIn.readFully(magic);
            if (!Arrays.equals(magic, BinaryXmlSerializer.PROTOCOL_MAGIC_VERSION_0)) {
                throw new IOException("Unexpected magic " + bytesToHexString(magic));
            }
            if (peekNextExternalToken() == 0) {
                consumeToken();
            }
        } catch (IOException e) {
            throw new XmlPullParserException(e.toString());
        }
    }

    protected FastDataInput obtainFastDataInput(InputStream is) {
        return FastDataInput.obtain(is);
    }

    @Override // org.xmlpull.v1.XmlPullParser
    public void setInput(Reader in) throws XmlPullParserException {
        throw new UnsupportedOperationException();
    }

    @Override // org.xmlpull.v1.XmlPullParser
    public int next() throws XmlPullParserException, IOException {
        while (true) {
            int token = nextToken();
            switch (token) {
                case 1:
                case 2:
                case 3:
                    return token;
                case 4:
                    consumeAdditionalText();
                    String str = this.mCurrentText;
                    if (str != null && str.length() != 0) {
                        return 4;
                    }
                    break;
            }
        }
    }

    @Override // org.xmlpull.v1.XmlPullParser
    public int nextToken() throws XmlPullParserException, IOException {
        int token;
        if (this.mCurrentToken == 3) {
            this.mCurrentDepth--;
        }
        try {
            token = peekNextExternalToken();
            consumeToken();
        } catch (EOFException e) {
            token = 1;
        }
        switch (token) {
            case 2:
                peekNextExternalToken();
                this.mCurrentDepth++;
                break;
        }
        this.mCurrentToken = token;
        return token;
    }

    private int peekNextExternalToken() throws IOException, XmlPullParserException {
        while (true) {
            int token = peekNextToken();
            switch (token) {
                case 15:
                    consumeToken();
                default:
                    return token;
            }
        }
    }

    private int peekNextToken() throws IOException {
        return this.mIn.peekByte() & MidiConstants.STATUS_CHANNEL_MASK;
    }

    private void consumeToken() throws IOException, XmlPullParserException {
        int event = this.mIn.readByte();
        int token = event & 15;
        int type = event & 240;
        switch (token) {
            case 0:
                this.mCurrentName = null;
                this.mCurrentText = null;
                if (this.mAttributeCount > 0) {
                    resetAttributes();
                    return;
                }
                return;
            case 1:
                this.mCurrentName = null;
                this.mCurrentText = null;
                if (this.mAttributeCount > 0) {
                    resetAttributes();
                    return;
                }
                return;
            case 2:
                this.mCurrentName = this.mIn.readInternedUTF();
                this.mCurrentText = null;
                if (this.mAttributeCount > 0) {
                    resetAttributes();
                    return;
                }
                return;
            case 3:
                this.mCurrentName = this.mIn.readInternedUTF();
                this.mCurrentText = null;
                if (this.mAttributeCount > 0) {
                    resetAttributes();
                    return;
                }
                return;
            case 4:
            case 5:
            case 7:
            case 8:
            case 9:
            case 10:
                this.mCurrentName = null;
                this.mCurrentText = this.mIn.readUTF();
                if (this.mAttributeCount > 0) {
                    resetAttributes();
                    return;
                }
                return;
            case 6:
                String readUTF = this.mIn.readUTF();
                this.mCurrentName = readUTF;
                this.mCurrentText = resolveEntity(readUTF);
                if (this.mAttributeCount > 0) {
                    resetAttributes();
                    return;
                }
                return;
            case 11:
            case 12:
            case 13:
            case 14:
            default:
                throw new IOException("Unknown token " + token + " with type " + type);
            case 15:
                Attribute attr = obtainAttribute();
                attr.name = this.mIn.readInternedUTF();
                attr.type = type;
                switch (type) {
                    case 16:
                    case 192:
                    case 208:
                        return;
                    case 32:
                        attr.valueString = this.mIn.readUTF();
                        return;
                    case 48:
                        attr.valueString = this.mIn.readInternedUTF();
                        return;
                    case 64:
                    case 80:
                        int len = this.mIn.readUnsignedShort();
                        byte[] res = new byte[len];
                        this.mIn.readFully(res);
                        attr.valueBytes = res;
                        return;
                    case 96:
                    case 112:
                        attr.valueInt = this.mIn.readInt();
                        return;
                    case 128:
                    case 144:
                        attr.valueLong = this.mIn.readLong();
                        return;
                    case 160:
                        attr.valueFloat = this.mIn.readFloat();
                        return;
                    case 176:
                        attr.valueDouble = this.mIn.readDouble();
                        return;
                    default:
                        throw new IOException("Unexpected data type " + type);
                }
        }
    }

    private void consumeAdditionalText() throws IOException, XmlPullParserException {
        String combinedText = this.mCurrentText;
        while (true) {
            int token = peekNextExternalToken();
            switch (token) {
                case 4:
                case 5:
                case 6:
                    consumeToken();
                    combinedText = combinedText + this.mCurrentText;
                    break;
                case 7:
                default:
                    this.mCurrentToken = 4;
                    this.mCurrentName = null;
                    this.mCurrentText = combinedText;
                    return;
                case 8:
                case 9:
                    consumeToken();
                    break;
            }
        }
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    static String resolveEntity(String entity) throws XmlPullParserException {
        char c;
        switch (entity.hashCode()) {
            case 3309:
                if (entity.equals("gt")) {
                    c = 1;
                    break;
                }
                c = 65535;
                break;
            case 3464:
                if (entity.equals("lt")) {
                    c = 0;
                    break;
                }
                c = 65535;
                break;
            case 96708:
                if (entity.equals("amp")) {
                    c = 2;
                    break;
                }
                c = 65535;
                break;
            case 3000915:
                if (entity.equals("apos")) {
                    c = 3;
                    break;
                }
                c = 65535;
                break;
            case 3482377:
                if (entity.equals("quot")) {
                    c = 4;
                    break;
                }
                c = 65535;
                break;
            default:
                c = 65535;
                break;
        }
        switch (c) {
            case 0:
                return "<";
            case 1:
                return ">";
            case 2:
                return "&";
            case 3:
                return "'";
            case 4:
                return "\"";
            default:
                if (entity.length() > 1 && entity.charAt(0) == '#') {
                    char c2 = (char) Integer.parseInt(entity.substring(1));
                    return new String(new char[]{c2});
                }
                throw new XmlPullParserException("Unknown entity " + entity);
        }
    }

    @Override // org.xmlpull.v1.XmlPullParser
    public void require(int type, String namespace, String name) throws XmlPullParserException, IOException {
        if (namespace != null && !namespace.isEmpty()) {
            throw illegalNamespace();
        }
        if (this.mCurrentToken != type || !Objects.equals(this.mCurrentName, name)) {
            throw new XmlPullParserException(getPositionDescription());
        }
    }

    @Override // org.xmlpull.v1.XmlPullParser
    public String nextText() throws XmlPullParserException, IOException {
        if (getEventType() != 2) {
            throw new XmlPullParserException(getPositionDescription());
        }
        int eventType = next();
        if (eventType == 4) {
            String result = getText();
            if (next() != 3) {
                throw new XmlPullParserException(getPositionDescription());
            }
            return result;
        } else if (eventType == 3) {
            return "";
        } else {
            throw new XmlPullParserException(getPositionDescription());
        }
    }

    @Override // org.xmlpull.v1.XmlPullParser
    public int nextTag() throws XmlPullParserException, IOException {
        int eventType = next();
        if (eventType == 4 && isWhitespace()) {
            eventType = next();
        }
        if (eventType != 2 && eventType != 3) {
            throw new XmlPullParserException(getPositionDescription());
        }
        return eventType;
    }

    private Attribute obtainAttribute() {
        int i = this.mAttributeCount;
        Attribute[] attributeArr = this.mAttributes;
        if (i == attributeArr.length) {
            int before = attributeArr.length;
            int after = (before >> 1) + before;
            this.mAttributes = (Attribute[]) Arrays.copyOf(attributeArr, after);
            for (int i2 = before; i2 < after; i2++) {
                this.mAttributes[i2] = new Attribute();
            }
        }
        Attribute[] attributeArr2 = this.mAttributes;
        int i3 = this.mAttributeCount;
        this.mAttributeCount = i3 + 1;
        return attributeArr2[i3];
    }

    private void resetAttributes() {
        for (int i = 0; i < this.mAttributeCount; i++) {
            this.mAttributes[i].reset();
        }
        this.mAttributeCount = 0;
    }

    @Override // com.android.modules.utils.TypedXmlPullParser
    public int getAttributeIndex(String namespace, String name) {
        if (namespace == null || namespace.isEmpty()) {
            for (int i = 0; i < this.mAttributeCount; i++) {
                if (Objects.equals(this.mAttributes[i].name, name)) {
                    return i;
                }
            }
            return -1;
        }
        throw illegalNamespace();
    }

    @Override // org.xmlpull.v1.XmlPullParser
    public String getAttributeValue(String namespace, String name) {
        int index = getAttributeIndex(namespace, name);
        if (index != -1) {
            return this.mAttributes[index].getValueString();
        }
        return null;
    }

    @Override // org.xmlpull.v1.XmlPullParser
    public String getAttributeValue(int index) {
        return this.mAttributes[index].getValueString();
    }

    @Override // com.android.modules.utils.TypedXmlPullParser
    public byte[] getAttributeBytesHex(int index) throws XmlPullParserException {
        return this.mAttributes[index].getValueBytesHex();
    }

    @Override // com.android.modules.utils.TypedXmlPullParser
    public byte[] getAttributeBytesBase64(int index) throws XmlPullParserException {
        return this.mAttributes[index].getValueBytesBase64();
    }

    @Override // com.android.modules.utils.TypedXmlPullParser
    public int getAttributeInt(int index) throws XmlPullParserException {
        return this.mAttributes[index].getValueInt();
    }

    @Override // com.android.modules.utils.TypedXmlPullParser
    public int getAttributeIntHex(int index) throws XmlPullParserException {
        return this.mAttributes[index].getValueIntHex();
    }

    @Override // com.android.modules.utils.TypedXmlPullParser
    public long getAttributeLong(int index) throws XmlPullParserException {
        return this.mAttributes[index].getValueLong();
    }

    @Override // com.android.modules.utils.TypedXmlPullParser
    public long getAttributeLongHex(int index) throws XmlPullParserException {
        return this.mAttributes[index].getValueLongHex();
    }

    @Override // com.android.modules.utils.TypedXmlPullParser
    public float getAttributeFloat(int index) throws XmlPullParserException {
        return this.mAttributes[index].getValueFloat();
    }

    @Override // com.android.modules.utils.TypedXmlPullParser
    public double getAttributeDouble(int index) throws XmlPullParserException {
        return this.mAttributes[index].getValueDouble();
    }

    @Override // com.android.modules.utils.TypedXmlPullParser
    public boolean getAttributeBoolean(int index) throws XmlPullParserException {
        return this.mAttributes[index].getValueBoolean();
    }

    @Override // org.xmlpull.v1.XmlPullParser
    public String getText() {
        return this.mCurrentText;
    }

    @Override // org.xmlpull.v1.XmlPullParser
    public char[] getTextCharacters(int[] holderForStartAndLength) {
        char[] chars = this.mCurrentText.toCharArray();
        holderForStartAndLength[0] = 0;
        holderForStartAndLength[1] = chars.length;
        return chars;
    }

    @Override // org.xmlpull.v1.XmlPullParser
    public String getInputEncoding() {
        return StandardCharsets.UTF_8.name();
    }

    @Override // org.xmlpull.v1.XmlPullParser
    public int getDepth() {
        return this.mCurrentDepth;
    }

    @Override // org.xmlpull.v1.XmlPullParser
    public String getPositionDescription() {
        return "Token " + this.mCurrentToken + " at depth " + this.mCurrentDepth;
    }

    @Override // org.xmlpull.v1.XmlPullParser
    public int getLineNumber() {
        return -1;
    }

    @Override // org.xmlpull.v1.XmlPullParser
    public int getColumnNumber() {
        return -1;
    }

    @Override // org.xmlpull.v1.XmlPullParser
    public boolean isWhitespace() throws XmlPullParserException {
        switch (this.mCurrentToken) {
            case 4:
            case 5:
                return !TextUtils.isGraphic(this.mCurrentText);
            case 6:
            default:
                throw new XmlPullParserException("Not applicable for token " + this.mCurrentToken);
            case 7:
                return true;
        }
    }

    @Override // org.xmlpull.v1.XmlPullParser
    public String getNamespace() {
        switch (this.mCurrentToken) {
            case 2:
            case 3:
                return "";
            default:
                return null;
        }
    }

    @Override // org.xmlpull.v1.XmlPullParser
    public String getName() {
        return this.mCurrentName;
    }

    @Override // org.xmlpull.v1.XmlPullParser
    public String getPrefix() {
        return null;
    }

    @Override // org.xmlpull.v1.XmlPullParser
    public boolean isEmptyElementTag() throws XmlPullParserException {
        switch (this.mCurrentToken) {
            case 2:
                try {
                    return peekNextExternalToken() == 3;
                } catch (IOException e) {
                    throw new XmlPullParserException(e.toString());
                }
            default:
                throw new XmlPullParserException("Not at START_TAG");
        }
    }

    @Override // org.xmlpull.v1.XmlPullParser
    public int getAttributeCount() {
        return this.mAttributeCount;
    }

    @Override // org.xmlpull.v1.XmlPullParser
    public String getAttributeNamespace(int index) {
        return "";
    }

    @Override // org.xmlpull.v1.XmlPullParser
    public String getAttributeName(int index) {
        return this.mAttributes[index].name;
    }

    @Override // org.xmlpull.v1.XmlPullParser
    public String getAttributePrefix(int index) {
        return null;
    }

    @Override // org.xmlpull.v1.XmlPullParser
    public String getAttributeType(int index) {
        return "CDATA";
    }

    @Override // org.xmlpull.v1.XmlPullParser
    public boolean isAttributeDefault(int index) {
        return false;
    }

    @Override // org.xmlpull.v1.XmlPullParser
    public int getEventType() throws XmlPullParserException {
        return this.mCurrentToken;
    }

    @Override // org.xmlpull.v1.XmlPullParser
    public int getNamespaceCount(int depth) throws XmlPullParserException {
        return 0;
    }

    @Override // org.xmlpull.v1.XmlPullParser
    public String getNamespacePrefix(int pos) throws XmlPullParserException {
        throw new UnsupportedOperationException();
    }

    @Override // org.xmlpull.v1.XmlPullParser
    public String getNamespaceUri(int pos) throws XmlPullParserException {
        throw new UnsupportedOperationException();
    }

    @Override // org.xmlpull.v1.XmlPullParser
    public String getNamespace(String prefix) {
        throw new UnsupportedOperationException();
    }

    @Override // org.xmlpull.v1.XmlPullParser
    public void defineEntityReplacementText(String entityName, String replacementText) throws XmlPullParserException {
        throw new UnsupportedOperationException();
    }

    @Override // org.xmlpull.v1.XmlPullParser
    public void setFeature(String name, boolean state) throws XmlPullParserException {
        throw new UnsupportedOperationException();
    }

    @Override // org.xmlpull.v1.XmlPullParser
    public boolean getFeature(String name) {
        throw new UnsupportedOperationException();
    }

    @Override // org.xmlpull.v1.XmlPullParser
    public void setProperty(String name, Object value) throws XmlPullParserException {
        throw new UnsupportedOperationException();
    }

    @Override // org.xmlpull.v1.XmlPullParser
    public Object getProperty(String name) {
        throw new UnsupportedOperationException();
    }

    private static IllegalArgumentException illegalNamespace() {
        throw new IllegalArgumentException("Namespaces are not supported");
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes5.dex */
    public static class Attribute {
        public String name;
        public int type;
        public byte[] valueBytes;
        public double valueDouble;
        public float valueFloat;
        public int valueInt;
        public long valueLong;
        public String valueString;

        private Attribute() {
        }

        public void reset() {
            this.name = null;
            this.valueString = null;
            this.valueBytes = null;
        }

        public String getValueString() {
            switch (this.type) {
                case 16:
                    return null;
                case 32:
                case 48:
                    return this.valueString;
                case 64:
                    return BinaryXmlPullParser.bytesToHexString(this.valueBytes);
                case 80:
                    return Base64.encodeToString(this.valueBytes, 2);
                case 96:
                    return Integer.toString(this.valueInt);
                case 112:
                    return Integer.toString(this.valueInt, 16);
                case 128:
                    return Long.toString(this.valueLong);
                case 144:
                    return Long.toString(this.valueLong, 16);
                case 160:
                    return Float.toString(this.valueFloat);
                case 176:
                    return Double.toString(this.valueDouble);
                case 192:
                    return "true";
                case 208:
                    return "false";
                default:
                    return null;
            }
        }

        public byte[] getValueBytesHex() throws XmlPullParserException {
            switch (this.type) {
                case 16:
                    return null;
                case 32:
                case 48:
                    try {
                        return BinaryXmlPullParser.hexStringToBytes(this.valueString);
                    } catch (Exception e) {
                        throw new XmlPullParserException("Invalid attribute " + this.name + ": " + e);
                    }
                case 64:
                case 80:
                    return this.valueBytes;
                default:
                    throw new XmlPullParserException("Invalid conversion from " + this.type);
            }
        }

        public byte[] getValueBytesBase64() throws XmlPullParserException {
            switch (this.type) {
                case 16:
                    return null;
                case 32:
                case 48:
                    try {
                        return Base64.decode(this.valueString, 2);
                    } catch (Exception e) {
                        throw new XmlPullParserException("Invalid attribute " + this.name + ": " + e);
                    }
                case 64:
                case 80:
                    return this.valueBytes;
                default:
                    throw new XmlPullParserException("Invalid conversion from " + this.type);
            }
        }

        public int getValueInt() throws XmlPullParserException {
            switch (this.type) {
                case 32:
                case 48:
                    try {
                        return Integer.parseInt(this.valueString);
                    } catch (Exception e) {
                        throw new XmlPullParserException("Invalid attribute " + this.name + ": " + e);
                    }
                case 96:
                case 112:
                    return this.valueInt;
                default:
                    throw new XmlPullParserException("Invalid conversion from " + this.type);
            }
        }

        public int getValueIntHex() throws XmlPullParserException {
            switch (this.type) {
                case 32:
                case 48:
                    try {
                        return Integer.parseInt(this.valueString, 16);
                    } catch (Exception e) {
                        throw new XmlPullParserException("Invalid attribute " + this.name + ": " + e);
                    }
                case 96:
                case 112:
                    return this.valueInt;
                default:
                    throw new XmlPullParserException("Invalid conversion from " + this.type);
            }
        }

        public long getValueLong() throws XmlPullParserException {
            switch (this.type) {
                case 32:
                case 48:
                    try {
                        return Long.parseLong(this.valueString);
                    } catch (Exception e) {
                        throw new XmlPullParserException("Invalid attribute " + this.name + ": " + e);
                    }
                case 128:
                case 144:
                    return this.valueLong;
                default:
                    throw new XmlPullParserException("Invalid conversion from " + this.type);
            }
        }

        public long getValueLongHex() throws XmlPullParserException {
            switch (this.type) {
                case 32:
                case 48:
                    try {
                        return Long.parseLong(this.valueString, 16);
                    } catch (Exception e) {
                        throw new XmlPullParserException("Invalid attribute " + this.name + ": " + e);
                    }
                case 128:
                case 144:
                    return this.valueLong;
                default:
                    throw new XmlPullParserException("Invalid conversion from " + this.type);
            }
        }

        public float getValueFloat() throws XmlPullParserException {
            switch (this.type) {
                case 32:
                case 48:
                    try {
                        return Float.parseFloat(this.valueString);
                    } catch (Exception e) {
                        throw new XmlPullParserException("Invalid attribute " + this.name + ": " + e);
                    }
                case 160:
                    return this.valueFloat;
                default:
                    throw new XmlPullParserException("Invalid conversion from " + this.type);
            }
        }

        public double getValueDouble() throws XmlPullParserException {
            switch (this.type) {
                case 32:
                case 48:
                    try {
                        return Double.parseDouble(this.valueString);
                    } catch (Exception e) {
                        throw new XmlPullParserException("Invalid attribute " + this.name + ": " + e);
                    }
                case 176:
                    return this.valueDouble;
                default:
                    throw new XmlPullParserException("Invalid conversion from " + this.type);
            }
        }

        public boolean getValueBoolean() throws XmlPullParserException {
            switch (this.type) {
                case 32:
                case 48:
                    if ("true".equalsIgnoreCase(this.valueString)) {
                        return true;
                    }
                    if ("false".equalsIgnoreCase(this.valueString)) {
                        return false;
                    }
                    throw new XmlPullParserException("Invalid attribute " + this.name + ": " + this.valueString);
                case 192:
                    return true;
                case 208:
                    return false;
                default:
                    throw new XmlPullParserException("Invalid conversion from " + this.type);
            }
        }
    }

    private static int toByte(char c) {
        if (c < '0' || c > '9') {
            if (c < 'A' || c > 'F') {
                if (c < 'a' || c > 'f') {
                    throw new IllegalArgumentException("Invalid hex char '" + c + "'");
                }
                return (c - 'a') + 10;
            }
            return (c - 'A') + 10;
        }
        return c - '0';
    }

    static String bytesToHexString(byte[] value) {
        int length = value.length;
        char[] buf = new char[length * 2];
        int bufIndex = 0;
        for (byte b : value) {
            int bufIndex2 = bufIndex + 1;
            char[] cArr = HEX_DIGITS;
            buf[bufIndex] = cArr[(b >>> 4) & 15];
            bufIndex = bufIndex2 + 1;
            buf[bufIndex2] = cArr[b & MidiConstants.STATUS_CHANNEL_MASK];
        }
        return new String(buf);
    }

    static byte[] hexStringToBytes(String value) {
        int length = value.length();
        if (length % 2 != 0) {
            throw new IllegalArgumentException("Invalid hex length " + length);
        }
        byte[] buffer = new byte[length / 2];
        for (int i = 0; i < length; i += 2) {
            buffer[i / 2] = (byte) ((toByte(value.charAt(i)) << 4) | toByte(value.charAt(i + 1)));
        }
        return buffer;
    }
}
