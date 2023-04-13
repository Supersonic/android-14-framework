package android.content.res;

import android.system.OsConstants;
import android.util.TypedValue;
import com.android.internal.util.XmlUtils;
import dalvik.annotation.optimization.CriticalNative;
import dalvik.annotation.optimization.FastNative;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes.dex */
public final class XmlBlock implements AutoCloseable {
    private static final boolean DEBUG = false;
    private static final int ERROR_BAD_DOCUMENT = -OsConstants.EINVAL;
    private static final int ERROR_NULL_DOCUMENT = -2147483640;
    private final AssetManager mAssets;
    private final long mNative;
    private boolean mOpen;
    private int mOpenCount;
    final StringBlock mStrings;

    private static final native long nativeCreate(byte[] bArr, int i, int i2);

    private static final native long nativeCreateParseState(long j, int i);

    private static final native void nativeDestroy(long j);

    /* JADX INFO: Access modifiers changed from: private */
    public static final native void nativeDestroyParseState(long j);

    /* JADX INFO: Access modifiers changed from: private */
    @CriticalNative
    public static final native int nativeGetAttributeCount(long j);

    /* JADX INFO: Access modifiers changed from: private */
    @CriticalNative
    public static final native int nativeGetAttributeData(long j, int i);

    /* JADX INFO: Access modifiers changed from: private */
    @CriticalNative
    public static final native int nativeGetAttributeDataType(long j, int i);

    /* JADX INFO: Access modifiers changed from: private */
    @FastNative
    public static native int nativeGetAttributeIndex(long j, String str, String str2);

    /* JADX INFO: Access modifiers changed from: private */
    @CriticalNative
    public static final native int nativeGetAttributeName(long j, int i);

    /* JADX INFO: Access modifiers changed from: private */
    @CriticalNative
    public static final native int nativeGetAttributeNamespace(long j, int i);

    /* JADX INFO: Access modifiers changed from: private */
    @CriticalNative
    public static final native int nativeGetAttributeResource(long j, int i);

    /* JADX INFO: Access modifiers changed from: private */
    @CriticalNative
    public static final native int nativeGetAttributeStringValue(long j, int i);

    /* JADX INFO: Access modifiers changed from: private */
    @CriticalNative
    public static final native int nativeGetClassAttribute(long j);

    /* JADX INFO: Access modifiers changed from: private */
    @CriticalNative
    public static final native int nativeGetIdAttribute(long j);

    /* JADX INFO: Access modifiers changed from: private */
    @CriticalNative
    public static final native int nativeGetLineNumber(long j);

    @CriticalNative
    static final native int nativeGetName(long j);

    /* JADX INFO: Access modifiers changed from: private */
    @CriticalNative
    public static final native int nativeGetNamespace(long j);

    /* JADX INFO: Access modifiers changed from: private */
    @CriticalNative
    public static final native int nativeGetSourceResId(long j);

    private static final native long nativeGetStringBlock(long j);

    /* JADX INFO: Access modifiers changed from: private */
    @CriticalNative
    public static final native int nativeGetStyleAttribute(long j);

    /* JADX INFO: Access modifiers changed from: private */
    @CriticalNative
    public static final native int nativeGetText(long j);

    @CriticalNative
    static final native int nativeNext(long j);

    public XmlBlock(byte[] data) {
        this.mOpen = true;
        this.mOpenCount = 1;
        this.mAssets = null;
        long nativeCreate = nativeCreate(data, 0, data.length);
        this.mNative = nativeCreate;
        this.mStrings = new StringBlock(nativeGetStringBlock(nativeCreate), false);
    }

    public XmlBlock(byte[] data, int offset, int size) {
        this.mOpen = true;
        this.mOpenCount = 1;
        this.mAssets = null;
        long nativeCreate = nativeCreate(data, offset, size);
        this.mNative = nativeCreate;
        this.mStrings = new StringBlock(nativeGetStringBlock(nativeCreate), false);
    }

    @Override // java.lang.AutoCloseable
    public void close() {
        synchronized (this) {
            if (this.mOpen) {
                this.mOpen = false;
                decOpenCountLocked();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void decOpenCountLocked() {
        int i = this.mOpenCount - 1;
        this.mOpenCount = i;
        if (i == 0) {
            nativeDestroy(this.mNative);
            AssetManager assetManager = this.mAssets;
            if (assetManager != null) {
                assetManager.xmlBlockGone(hashCode());
            }
        }
    }

    public XmlResourceParser newParser() {
        return newParser(0);
    }

    public XmlResourceParser newParser(int resId) {
        synchronized (this) {
            long j = this.mNative;
            if (j != 0) {
                return new Parser(nativeCreateParseState(j, resId), this);
            }
            return null;
        }
    }

    /* loaded from: classes.dex */
    public final class Parser implements XmlResourceParser {
        private final XmlBlock mBlock;
        long mParseState;
        private boolean mStarted = false;
        private boolean mDecNextDepth = false;
        private int mDepth = 0;
        private int mEventType = 0;

        Parser(long parseState, XmlBlock block) {
            this.mParseState = parseState;
            this.mBlock = block;
            block.mOpenCount++;
        }

        public int getSourceResId() {
            return XmlBlock.nativeGetSourceResId(this.mParseState);
        }

        @Override // org.xmlpull.v1.XmlPullParser
        public void setFeature(String name, boolean state) throws XmlPullParserException {
            if ("http://xmlpull.org/v1/doc/features.html#process-namespaces".equals(name) && state) {
                return;
            }
            if ("http://xmlpull.org/v1/doc/features.html#report-namespace-prefixes".equals(name) && state) {
                return;
            }
            throw new XmlPullParserException("Unsupported feature: " + name);
        }

        @Override // org.xmlpull.v1.XmlPullParser
        public boolean getFeature(String name) {
            return "http://xmlpull.org/v1/doc/features.html#process-namespaces".equals(name) || "http://xmlpull.org/v1/doc/features.html#report-namespace-prefixes".equals(name);
        }

        @Override // org.xmlpull.v1.XmlPullParser
        public void setProperty(String name, Object value) throws XmlPullParserException {
            throw new XmlPullParserException("setProperty() not supported");
        }

        @Override // org.xmlpull.v1.XmlPullParser
        public Object getProperty(String name) {
            return null;
        }

        @Override // org.xmlpull.v1.XmlPullParser
        public void setInput(Reader in) throws XmlPullParserException {
            throw new XmlPullParserException("setInput() not supported");
        }

        @Override // org.xmlpull.v1.XmlPullParser
        public void setInput(InputStream inputStream, String inputEncoding) throws XmlPullParserException {
            throw new XmlPullParserException("setInput() not supported");
        }

        @Override // org.xmlpull.v1.XmlPullParser
        public void defineEntityReplacementText(String entityName, String replacementText) throws XmlPullParserException {
            throw new XmlPullParserException("defineEntityReplacementText() not supported");
        }

        @Override // org.xmlpull.v1.XmlPullParser
        public String getNamespacePrefix(int pos) throws XmlPullParserException {
            throw new XmlPullParserException("getNamespacePrefix() not supported");
        }

        @Override // org.xmlpull.v1.XmlPullParser
        public String getInputEncoding() {
            return null;
        }

        @Override // org.xmlpull.v1.XmlPullParser
        public String getNamespace(String prefix) {
            throw new RuntimeException("getNamespace() not supported");
        }

        @Override // org.xmlpull.v1.XmlPullParser
        public int getNamespaceCount(int depth) throws XmlPullParserException {
            throw new XmlPullParserException("getNamespaceCount() not supported");
        }

        @Override // org.xmlpull.v1.XmlPullParser, android.util.AttributeSet
        public String getPositionDescription() {
            return "Binary XML file line #" + getLineNumber();
        }

        @Override // org.xmlpull.v1.XmlPullParser
        public String getNamespaceUri(int pos) throws XmlPullParserException {
            throw new XmlPullParserException("getNamespaceUri() not supported");
        }

        @Override // org.xmlpull.v1.XmlPullParser
        public int getColumnNumber() {
            return -1;
        }

        @Override // org.xmlpull.v1.XmlPullParser
        public int getDepth() {
            return this.mDepth;
        }

        @Override // org.xmlpull.v1.XmlPullParser
        public String getText() {
            int id = XmlBlock.nativeGetText(this.mParseState);
            if (id >= 0) {
                return getSequenceString(XmlBlock.this.mStrings.getSequence(id));
            }
            return null;
        }

        @Override // org.xmlpull.v1.XmlPullParser
        public int getLineNumber() {
            int lineNumber = XmlBlock.nativeGetLineNumber(this.mParseState);
            if (lineNumber == -2147483640) {
                throw new NullPointerException("Null document");
            }
            return lineNumber;
        }

        @Override // org.xmlpull.v1.XmlPullParser
        public int getEventType() throws XmlPullParserException {
            return this.mEventType;
        }

        @Override // org.xmlpull.v1.XmlPullParser
        public boolean isWhitespace() throws XmlPullParserException {
            return false;
        }

        @Override // org.xmlpull.v1.XmlPullParser
        public String getPrefix() {
            throw new RuntimeException("getPrefix not supported");
        }

        @Override // org.xmlpull.v1.XmlPullParser
        public char[] getTextCharacters(int[] holderForStartAndLength) {
            String txt = getText();
            if (txt == null) {
                return null;
            }
            holderForStartAndLength[0] = 0;
            holderForStartAndLength[1] = txt.length();
            char[] chars = new char[txt.length()];
            txt.getChars(0, txt.length(), chars, 0);
            return chars;
        }

        @Override // org.xmlpull.v1.XmlPullParser
        public String getNamespace() {
            int id = XmlBlock.nativeGetNamespace(this.mParseState);
            return id >= 0 ? getSequenceString(XmlBlock.this.mStrings.getSequence(id)) : "";
        }

        @Override // org.xmlpull.v1.XmlPullParser
        public String getName() {
            int id = XmlBlock.nativeGetName(this.mParseState);
            if (id >= 0) {
                return getSequenceString(XmlBlock.this.mStrings.getSequence(id));
            }
            return null;
        }

        @Override // android.content.res.XmlResourceParser, org.xmlpull.v1.XmlPullParser, android.util.AttributeSet
        public String getAttributeNamespace(int index) {
            int id = XmlBlock.nativeGetAttributeNamespace(this.mParseState, index);
            if (id == -2147483640) {
                throw new NullPointerException("Null document");
            }
            if (id >= 0) {
                return getSequenceString(XmlBlock.this.mStrings.getSequence(id));
            }
            if (id == -1) {
                return "";
            }
            throw new IndexOutOfBoundsException(String.valueOf(index));
        }

        @Override // org.xmlpull.v1.XmlPullParser, android.util.AttributeSet
        public String getAttributeName(int index) {
            int id = XmlBlock.nativeGetAttributeName(this.mParseState, index);
            if (id == -2147483640) {
                throw new NullPointerException("Null document");
            }
            if (id >= 0) {
                return getSequenceString(XmlBlock.this.mStrings.getSequence(id));
            }
            throw new IndexOutOfBoundsException(String.valueOf(index));
        }

        @Override // org.xmlpull.v1.XmlPullParser
        public String getAttributePrefix(int index) {
            throw new RuntimeException("getAttributePrefix not supported");
        }

        @Override // org.xmlpull.v1.XmlPullParser
        public boolean isEmptyElementTag() throws XmlPullParserException {
            return false;
        }

        @Override // org.xmlpull.v1.XmlPullParser, android.util.AttributeSet
        public int getAttributeCount() {
            if (this.mEventType == 2) {
                int count = XmlBlock.nativeGetAttributeCount(this.mParseState);
                if (count == -2147483640) {
                    throw new NullPointerException("Null document");
                }
                return count;
            }
            return -1;
        }

        @Override // org.xmlpull.v1.XmlPullParser, android.util.AttributeSet
        public String getAttributeValue(int index) {
            int id = XmlBlock.nativeGetAttributeStringValue(this.mParseState, index);
            if (id == -2147483640) {
                throw new NullPointerException("Null document");
            }
            if (id >= 0) {
                return getSequenceString(XmlBlock.this.mStrings.getSequence(id));
            }
            int t = XmlBlock.nativeGetAttributeDataType(this.mParseState, index);
            if (t == -2147483640) {
                throw new NullPointerException("Null document");
            }
            if (t == 0) {
                throw new IndexOutOfBoundsException(String.valueOf(index));
            }
            int v = XmlBlock.nativeGetAttributeData(this.mParseState, index);
            if (v == -2147483640) {
                throw new NullPointerException("Null document");
            }
            return TypedValue.coerceToString(t, v);
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
        public int nextToken() throws XmlPullParserException, IOException {
            return next();
        }

        @Override // org.xmlpull.v1.XmlPullParser, android.util.AttributeSet
        public String getAttributeValue(String namespace, String name) {
            int idx = XmlBlock.nativeGetAttributeIndex(this.mParseState, namespace, name);
            if (idx >= 0) {
                return getAttributeValue(idx);
            }
            return null;
        }

        @Override // org.xmlpull.v1.XmlPullParser
        public int next() throws XmlPullParserException, IOException {
            if (!this.mStarted) {
                this.mStarted = true;
                return 0;
            }
            long j = this.mParseState;
            if (j == 0) {
                return 1;
            }
            int ev = XmlBlock.nativeNext(j);
            if (ev == XmlBlock.ERROR_BAD_DOCUMENT) {
                throw new XmlPullParserException("Corrupt XML binary file");
            }
            if (this.mDecNextDepth) {
                this.mDepth--;
                this.mDecNextDepth = false;
            }
            switch (ev) {
                case 2:
                    this.mDepth++;
                    break;
                case 3:
                    this.mDecNextDepth = true;
                    break;
            }
            this.mEventType = ev;
            if (ev == 1) {
                close();
            }
            return ev;
        }

        @Override // org.xmlpull.v1.XmlPullParser
        public void require(int type, String namespace, String name) throws XmlPullParserException, IOException {
            if (type != getEventType() || ((namespace != null && !namespace.equals(getNamespace())) || (name != null && !name.equals(getName())))) {
                throw new XmlPullParserException("expected " + TYPES[type] + getPositionDescription());
            }
        }

        @Override // org.xmlpull.v1.XmlPullParser
        public String nextText() throws XmlPullParserException, IOException {
            if (getEventType() != 2) {
                throw new XmlPullParserException(getPositionDescription() + ": parser must be on START_TAG to read next text", this, null);
            }
            int eventType = next();
            if (eventType == 4) {
                String result = getText();
                if (next() != 3) {
                    throw new XmlPullParserException(getPositionDescription() + ": event TEXT it must be immediately followed by END_TAG", this, null);
                }
                return result;
            } else if (eventType == 3) {
                return "";
            } else {
                throw new XmlPullParserException(getPositionDescription() + ": parser must be on START_TAG or TEXT to read text", this, null);
            }
        }

        @Override // org.xmlpull.v1.XmlPullParser
        public int nextTag() throws XmlPullParserException, IOException {
            int eventType = next();
            if (eventType == 4 && isWhitespace()) {
                eventType = next();
            }
            if (eventType != 2 && eventType != 3) {
                throw new XmlPullParserException(getPositionDescription() + ": expected start or end tag", this, null);
            }
            return eventType;
        }

        @Override // android.util.AttributeSet
        public int getAttributeNameResource(int index) {
            int resourceNameId = XmlBlock.nativeGetAttributeResource(this.mParseState, index);
            if (resourceNameId == -2147483640) {
                throw new NullPointerException("Null document");
            }
            return resourceNameId;
        }

        @Override // android.util.AttributeSet
        public int getAttributeListValue(String namespace, String attribute, String[] options, int defaultValue) {
            int idx = XmlBlock.nativeGetAttributeIndex(this.mParseState, namespace, attribute);
            if (idx >= 0) {
                return getAttributeListValue(idx, options, defaultValue);
            }
            return defaultValue;
        }

        @Override // android.util.AttributeSet
        public boolean getAttributeBooleanValue(String namespace, String attribute, boolean defaultValue) {
            int idx = XmlBlock.nativeGetAttributeIndex(this.mParseState, namespace, attribute);
            if (idx >= 0) {
                return getAttributeBooleanValue(idx, defaultValue);
            }
            return defaultValue;
        }

        @Override // android.util.AttributeSet
        public int getAttributeResourceValue(String namespace, String attribute, int defaultValue) {
            int idx = XmlBlock.nativeGetAttributeIndex(this.mParseState, namespace, attribute);
            if (idx >= 0) {
                return getAttributeResourceValue(idx, defaultValue);
            }
            return defaultValue;
        }

        @Override // android.util.AttributeSet
        public int getAttributeIntValue(String namespace, String attribute, int defaultValue) {
            int idx = XmlBlock.nativeGetAttributeIndex(this.mParseState, namespace, attribute);
            if (idx >= 0) {
                return getAttributeIntValue(idx, defaultValue);
            }
            return defaultValue;
        }

        @Override // android.util.AttributeSet
        public int getAttributeUnsignedIntValue(String namespace, String attribute, int defaultValue) {
            int idx = XmlBlock.nativeGetAttributeIndex(this.mParseState, namespace, attribute);
            if (idx >= 0) {
                return getAttributeUnsignedIntValue(idx, defaultValue);
            }
            return defaultValue;
        }

        @Override // android.util.AttributeSet
        public float getAttributeFloatValue(String namespace, String attribute, float defaultValue) {
            int idx = XmlBlock.nativeGetAttributeIndex(this.mParseState, namespace, attribute);
            if (idx >= 0) {
                return getAttributeFloatValue(idx, defaultValue);
            }
            return defaultValue;
        }

        @Override // android.util.AttributeSet
        public int getAttributeListValue(int idx, String[] options, int defaultValue) {
            int t = XmlBlock.nativeGetAttributeDataType(this.mParseState, idx);
            if (t == -2147483640) {
                throw new NullPointerException("Null document");
            }
            int v = XmlBlock.nativeGetAttributeData(this.mParseState, idx);
            if (v == -2147483640) {
                throw new NullPointerException("Null document");
            }
            if (t == 3) {
                return XmlUtils.convertValueToList(XmlBlock.this.mStrings.getSequence(v), options, defaultValue);
            }
            return v;
        }

        @Override // android.util.AttributeSet
        public boolean getAttributeBooleanValue(int idx, boolean defaultValue) {
            int t = XmlBlock.nativeGetAttributeDataType(this.mParseState, idx);
            if (t == -2147483640) {
                throw new NullPointerException("Null document");
            }
            if (t >= 16 && t <= 31) {
                int v = XmlBlock.nativeGetAttributeData(this.mParseState, idx);
                if (v != -2147483640) {
                    return v != 0;
                }
                throw new NullPointerException("Null document");
            }
            return defaultValue;
        }

        @Override // android.util.AttributeSet
        public int getAttributeResourceValue(int idx, int defaultValue) {
            int t = XmlBlock.nativeGetAttributeDataType(this.mParseState, idx);
            if (t == -2147483640) {
                throw new NullPointerException("Null document");
            }
            if (t == 1) {
                int v = XmlBlock.nativeGetAttributeData(this.mParseState, idx);
                if (v == -2147483640) {
                    throw new NullPointerException("Null document");
                }
                return v;
            }
            return defaultValue;
        }

        @Override // android.util.AttributeSet
        public int getAttributeIntValue(int idx, int defaultValue) {
            int t = XmlBlock.nativeGetAttributeDataType(this.mParseState, idx);
            if (t == -2147483640) {
                throw new NullPointerException("Null document");
            }
            if (t >= 16 && t <= 31) {
                int v = XmlBlock.nativeGetAttributeData(this.mParseState, idx);
                if (v == -2147483640) {
                    throw new NullPointerException("Null document");
                }
                return v;
            }
            return defaultValue;
        }

        @Override // android.util.AttributeSet
        public int getAttributeUnsignedIntValue(int idx, int defaultValue) {
            int t = XmlBlock.nativeGetAttributeDataType(this.mParseState, idx);
            if (t == -2147483640) {
                throw new NullPointerException("Null document");
            }
            if (t >= 16 && t <= 31) {
                int v = XmlBlock.nativeGetAttributeData(this.mParseState, idx);
                if (v == -2147483640) {
                    throw new NullPointerException("Null document");
                }
                return v;
            }
            return defaultValue;
        }

        @Override // android.util.AttributeSet
        public float getAttributeFloatValue(int idx, float defaultValue) {
            int t = XmlBlock.nativeGetAttributeDataType(this.mParseState, idx);
            if (t == -2147483640) {
                throw new NullPointerException("Null document");
            }
            if (t == 4) {
                int v = XmlBlock.nativeGetAttributeData(this.mParseState, idx);
                if (v == -2147483640) {
                    throw new NullPointerException("Null document");
                }
                return Float.intBitsToFloat(v);
            }
            throw new RuntimeException("not a float!");
        }

        @Override // android.util.AttributeSet
        public String getIdAttribute() {
            int id = XmlBlock.nativeGetIdAttribute(this.mParseState);
            if (id == -2147483640) {
                throw new NullPointerException("Null document");
            }
            if (id >= 0) {
                return getSequenceString(XmlBlock.this.mStrings.getSequence(id));
            }
            return null;
        }

        @Override // android.util.AttributeSet
        public String getClassAttribute() {
            int id = XmlBlock.nativeGetClassAttribute(this.mParseState);
            if (id == -2147483640) {
                throw new NullPointerException("Null document");
            }
            if (id >= 0) {
                return getSequenceString(XmlBlock.this.mStrings.getSequence(id));
            }
            return null;
        }

        @Override // android.util.AttributeSet
        public int getIdAttributeResourceValue(int defaultValue) {
            return getAttributeResourceValue(null, "id", defaultValue);
        }

        @Override // android.util.AttributeSet
        public int getStyleAttribute() {
            int styleAttributeId = XmlBlock.nativeGetStyleAttribute(this.mParseState);
            if (styleAttributeId == -2147483640) {
                throw new NullPointerException("Null document");
            }
            return styleAttributeId;
        }

        private String getSequenceString(CharSequence str) {
            if (str == null) {
                throw new IllegalStateException("Retrieving a string from the StringPool of an XmlBlock should never fail");
            }
            return str.toString();
        }

        @Override // android.content.res.XmlResourceParser, java.lang.AutoCloseable
        public void close() {
            synchronized (this.mBlock) {
                long j = this.mParseState;
                if (j != 0) {
                    XmlBlock.nativeDestroyParseState(j);
                    this.mParseState = 0L;
                    this.mBlock.decOpenCountLocked();
                }
            }
        }

        protected void finalize() throws Throwable {
            close();
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public final CharSequence getPooledString(int id) {
            return XmlBlock.this.mStrings.getSequence(id);
        }
    }

    protected void finalize() throws Throwable {
        close();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public XmlBlock(AssetManager assets, long xmlBlock) {
        this.mOpen = true;
        this.mOpenCount = 1;
        this.mAssets = assets;
        this.mNative = xmlBlock;
        this.mStrings = new StringBlock(nativeGetStringBlock(xmlBlock), false);
    }
}
