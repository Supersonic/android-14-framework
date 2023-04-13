package com.android.internal.util;

import android.app.slice.Slice;
import android.app.slice.SliceItem;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Base64;
import android.util.Xml;
import com.android.ims.ImsConfig;
import com.android.modules.utils.TypedXmlPullParser;
import com.android.modules.utils.TypedXmlSerializer;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ProtocolException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import libcore.util.HexEncoding;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;
/* loaded from: classes3.dex */
public class XmlUtils {
    private static final String STRING_ARRAY_SEPARATOR = ":";

    /* loaded from: classes3.dex */
    public interface ReadMapCallback {
        Object readThisUnknownObjectXml(TypedXmlPullParser typedXmlPullParser, String str) throws XmlPullParserException, IOException;
    }

    /* loaded from: classes3.dex */
    public interface WriteMapCallback {
        void writeUnknownObject(Object obj, String str, TypedXmlSerializer typedXmlSerializer) throws XmlPullParserException, IOException;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public static class ForcedTypedXmlSerializer extends XmlSerializerWrapper implements TypedXmlSerializer {
        public ForcedTypedXmlSerializer(XmlSerializer wrapped) {
            super(wrapped);
        }

        @Override // com.android.modules.utils.TypedXmlSerializer
        public XmlSerializer attributeInterned(String namespace, String name, String value) throws IOException {
            return attribute(namespace, name, value);
        }

        @Override // com.android.modules.utils.TypedXmlSerializer
        public XmlSerializer attributeBytesHex(String namespace, String name, byte[] value) throws IOException {
            return attribute(namespace, name, HexDump.toHexString(value));
        }

        @Override // com.android.modules.utils.TypedXmlSerializer
        public XmlSerializer attributeBytesBase64(String namespace, String name, byte[] value) throws IOException {
            return attribute(namespace, name, Base64.encodeToString(value, 2));
        }

        @Override // com.android.modules.utils.TypedXmlSerializer
        public XmlSerializer attributeInt(String namespace, String name, int value) throws IOException {
            return attribute(namespace, name, Integer.toString(value));
        }

        @Override // com.android.modules.utils.TypedXmlSerializer
        public XmlSerializer attributeIntHex(String namespace, String name, int value) throws IOException {
            return attribute(namespace, name, Integer.toString(value, 16));
        }

        @Override // com.android.modules.utils.TypedXmlSerializer
        public XmlSerializer attributeLong(String namespace, String name, long value) throws IOException {
            return attribute(namespace, name, Long.toString(value));
        }

        @Override // com.android.modules.utils.TypedXmlSerializer
        public XmlSerializer attributeLongHex(String namespace, String name, long value) throws IOException {
            return attribute(namespace, name, Long.toString(value, 16));
        }

        @Override // com.android.modules.utils.TypedXmlSerializer
        public XmlSerializer attributeFloat(String namespace, String name, float value) throws IOException {
            return attribute(namespace, name, Float.toString(value));
        }

        @Override // com.android.modules.utils.TypedXmlSerializer
        public XmlSerializer attributeDouble(String namespace, String name, double value) throws IOException {
            return attribute(namespace, name, Double.toString(value));
        }

        @Override // com.android.modules.utils.TypedXmlSerializer
        public XmlSerializer attributeBoolean(String namespace, String name, boolean value) throws IOException {
            return attribute(namespace, name, Boolean.toString(value));
        }
    }

    public static TypedXmlSerializer makeTyped(XmlSerializer xml) {
        if (xml instanceof TypedXmlSerializer) {
            return (TypedXmlSerializer) xml;
        }
        return new ForcedTypedXmlSerializer(xml);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public static class ForcedTypedXmlPullParser extends XmlPullParserWrapper implements TypedXmlPullParser {
        public ForcedTypedXmlPullParser(XmlPullParser wrapped) {
            super(wrapped);
        }

        @Override // com.android.modules.utils.TypedXmlPullParser
        public byte[] getAttributeBytesHex(int index) throws XmlPullParserException {
            try {
                return HexDump.hexStringToByteArray(getAttributeValue(index));
            } catch (Exception e) {
                throw new XmlPullParserException("Invalid attribute " + getAttributeName(index) + ": " + e);
            }
        }

        @Override // com.android.modules.utils.TypedXmlPullParser
        public byte[] getAttributeBytesBase64(int index) throws XmlPullParserException {
            try {
                return Base64.decode(getAttributeValue(index), 2);
            } catch (Exception e) {
                throw new XmlPullParserException("Invalid attribute " + getAttributeName(index) + ": " + e);
            }
        }

        @Override // com.android.modules.utils.TypedXmlPullParser
        public int getAttributeInt(int index) throws XmlPullParserException {
            try {
                return Integer.parseInt(getAttributeValue(index));
            } catch (Exception e) {
                throw new XmlPullParserException("Invalid attribute " + getAttributeName(index) + ": " + e);
            }
        }

        @Override // com.android.modules.utils.TypedXmlPullParser
        public int getAttributeIntHex(int index) throws XmlPullParserException {
            try {
                return Integer.parseInt(getAttributeValue(index), 16);
            } catch (Exception e) {
                throw new XmlPullParserException("Invalid attribute " + getAttributeName(index) + ": " + e);
            }
        }

        @Override // com.android.modules.utils.TypedXmlPullParser
        public long getAttributeLong(int index) throws XmlPullParserException {
            try {
                return Long.parseLong(getAttributeValue(index));
            } catch (Exception e) {
                throw new XmlPullParserException("Invalid attribute " + getAttributeName(index) + ": " + e);
            }
        }

        @Override // com.android.modules.utils.TypedXmlPullParser
        public long getAttributeLongHex(int index) throws XmlPullParserException {
            try {
                return Long.parseLong(getAttributeValue(index), 16);
            } catch (Exception e) {
                throw new XmlPullParserException("Invalid attribute " + getAttributeName(index) + ": " + e);
            }
        }

        @Override // com.android.modules.utils.TypedXmlPullParser
        public float getAttributeFloat(int index) throws XmlPullParserException {
            try {
                return Float.parseFloat(getAttributeValue(index));
            } catch (Exception e) {
                throw new XmlPullParserException("Invalid attribute " + getAttributeName(index) + ": " + e);
            }
        }

        @Override // com.android.modules.utils.TypedXmlPullParser
        public double getAttributeDouble(int index) throws XmlPullParserException {
            try {
                return Double.parseDouble(getAttributeValue(index));
            } catch (Exception e) {
                throw new XmlPullParserException("Invalid attribute " + getAttributeName(index) + ": " + e);
            }
        }

        @Override // com.android.modules.utils.TypedXmlPullParser
        public boolean getAttributeBoolean(int index) throws XmlPullParserException {
            String value = getAttributeValue(index);
            if ("true".equalsIgnoreCase(value)) {
                return true;
            }
            if ("false".equalsIgnoreCase(value)) {
                return false;
            }
            throw new XmlPullParserException("Invalid attribute " + getAttributeName(index) + ": " + value);
        }
    }

    public static TypedXmlPullParser makeTyped(XmlPullParser xml) {
        if (xml instanceof TypedXmlPullParser) {
            return (TypedXmlPullParser) xml;
        }
        return new ForcedTypedXmlPullParser(xml);
    }

    public static void skipCurrentTag(XmlPullParser parser) throws XmlPullParserException, IOException {
        int outerDepth = parser.getDepth();
        while (true) {
            int type = parser.next();
            if (type != 1) {
                if (type == 3 && parser.getDepth() <= outerDepth) {
                    return;
                }
            } else {
                return;
            }
        }
    }

    public static final int convertValueToList(CharSequence value, String[] options, int defaultValue) {
        if (!TextUtils.isEmpty(value)) {
            for (int i = 0; i < options.length; i++) {
                if (value.equals(options[i])) {
                    return i;
                }
            }
        }
        return defaultValue;
    }

    public static final boolean convertValueToBoolean(CharSequence value, boolean defaultValue) {
        if (TextUtils.isEmpty(value)) {
            return defaultValue;
        }
        if (!value.equals("1") && !value.equals("true") && !value.equals("TRUE")) {
            return false;
        }
        return true;
    }

    public static final int convertValueToInt(CharSequence charSeq, int defaultValue) {
        if (TextUtils.isEmpty(charSeq)) {
            return defaultValue;
        }
        String nm = charSeq.toString();
        int sign = 1;
        int index = 0;
        int len = nm.length();
        int base = 10;
        if ('-' == nm.charAt(0)) {
            sign = -1;
            index = 0 + 1;
        }
        if ('0' == nm.charAt(index)) {
            if (index == len - 1) {
                return 0;
            }
            char c = nm.charAt(index + 1);
            if ('x' == c || 'X' == c) {
                index += 2;
                base = 16;
            } else {
                index++;
                base = 8;
            }
        } else if ('#' == nm.charAt(index)) {
            index++;
            base = 16;
        }
        return Integer.parseInt(nm.substring(index), base) * sign;
    }

    public static int convertValueToUnsignedInt(String value, int defaultValue) {
        if (TextUtils.isEmpty(value)) {
            return defaultValue;
        }
        return parseUnsignedIntAttribute(value);
    }

    public static int parseUnsignedIntAttribute(CharSequence charSeq) {
        String value = charSeq.toString();
        int index = 0;
        int len = value.length();
        int base = 10;
        if ('0' == value.charAt(0)) {
            if (0 == len - 1) {
                return 0;
            }
            char c = value.charAt(0 + 1);
            if ('x' == c || 'X' == c) {
                index = 0 + 2;
                base = 16;
            } else {
                index = 0 + 1;
                base = 8;
            }
        } else if ('#' == value.charAt(0)) {
            index = 0 + 1;
            base = 16;
        }
        return (int) Long.parseLong(value.substring(index), base);
    }

    public static final void writeMapXml(Map val, OutputStream out) throws XmlPullParserException, IOException {
        TypedXmlSerializer serializer = Xml.newFastSerializer();
        serializer.setOutput(out, StandardCharsets.UTF_8.name());
        serializer.startDocument(null, true);
        serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
        writeMapXml(val, (String) null, serializer);
        serializer.endDocument();
    }

    public static final void writeListXml(List val, OutputStream out) throws XmlPullParserException, IOException {
        TypedXmlSerializer serializer = Xml.newFastSerializer();
        serializer.setOutput(out, StandardCharsets.UTF_8.name());
        serializer.startDocument(null, true);
        serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
        writeListXml(val, null, serializer);
        serializer.endDocument();
    }

    public static final void writeMapXml(Map val, String name, TypedXmlSerializer out) throws XmlPullParserException, IOException {
        writeMapXml(val, name, out, null);
    }

    public static final void writeMapXml(Map val, String name, TypedXmlSerializer out, WriteMapCallback callback) throws XmlPullParserException, IOException {
        if (val == null) {
            out.startTag(null, "null");
            out.endTag(null, "null");
            return;
        }
        out.startTag(null, "map");
        if (name != null) {
            out.attribute(null, "name", name);
        }
        writeMapXml(val, out, callback);
        out.endTag(null, "map");
    }

    public static final void writeMapXml(Map val, TypedXmlSerializer out, WriteMapCallback callback) throws XmlPullParserException, IOException {
        if (val == null) {
            return;
        }
        Set<Map.Entry> s = val.entrySet();
        for (Map.Entry e : s) {
            writeValueXml(e.getValue(), (String) e.getKey(), out, callback);
        }
    }

    public static final void writeListXml(List val, String name, TypedXmlSerializer out) throws XmlPullParserException, IOException {
        if (val == null) {
            out.startTag(null, "null");
            out.endTag(null, "null");
            return;
        }
        out.startTag(null, Slice.HINT_LIST);
        if (name != null) {
            out.attribute(null, "name", name);
        }
        int N = val.size();
        for (int i = 0; i < N; i++) {
            writeValueXml(val.get(i), (String) null, out);
        }
        out.endTag(null, Slice.HINT_LIST);
    }

    public static final void writeSetXml(Set val, String name, TypedXmlSerializer out) throws XmlPullParserException, IOException {
        if (val == null) {
            out.startTag(null, "null");
            out.endTag(null, "null");
            return;
        }
        out.startTag(null, "set");
        if (name != null) {
            out.attribute(null, "name", name);
        }
        for (Object v : val) {
            writeValueXml(v, (String) null, out);
        }
        out.endTag(null, "set");
    }

    public static final void writeByteArrayXml(byte[] val, String name, TypedXmlSerializer out) throws XmlPullParserException, IOException {
        if (val == null) {
            out.startTag(null, "null");
            out.endTag(null, "null");
            return;
        }
        out.startTag(null, "byte-array");
        if (name != null) {
            out.attribute(null, "name", name);
        }
        int N = val.length;
        out.attributeInt(null, "num", N);
        out.text(HexEncoding.encodeToString(val).toLowerCase());
        out.endTag(null, "byte-array");
    }

    public static final void writeIntArrayXml(int[] val, String name, TypedXmlSerializer out) throws XmlPullParserException, IOException {
        if (val == null) {
            out.startTag(null, "null");
            out.endTag(null, "null");
            return;
        }
        out.startTag(null, "int-array");
        if (name != null) {
            out.attribute(null, "name", name);
        }
        int N = val.length;
        out.attributeInt(null, "num", N);
        for (int i : val) {
            out.startTag(null, ImsConfig.EXTRA_CHANGED_ITEM);
            out.attributeInt(null, "value", i);
            out.endTag(null, ImsConfig.EXTRA_CHANGED_ITEM);
        }
        out.endTag(null, "int-array");
    }

    public static final void writeLongArrayXml(long[] val, String name, TypedXmlSerializer out) throws XmlPullParserException, IOException {
        if (val == null) {
            out.startTag(null, "null");
            out.endTag(null, "null");
            return;
        }
        out.startTag(null, "long-array");
        if (name != null) {
            out.attribute(null, "name", name);
        }
        int N = val.length;
        out.attributeInt(null, "num", N);
        for (long j : val) {
            out.startTag(null, ImsConfig.EXTRA_CHANGED_ITEM);
            out.attributeLong(null, "value", j);
            out.endTag(null, ImsConfig.EXTRA_CHANGED_ITEM);
        }
        out.endTag(null, "long-array");
    }

    public static final void writeDoubleArrayXml(double[] val, String name, TypedXmlSerializer out) throws XmlPullParserException, IOException {
        if (val == null) {
            out.startTag(null, "null");
            out.endTag(null, "null");
            return;
        }
        out.startTag(null, "double-array");
        if (name != null) {
            out.attribute(null, "name", name);
        }
        int N = val.length;
        out.attributeInt(null, "num", N);
        for (double d : val) {
            out.startTag(null, ImsConfig.EXTRA_CHANGED_ITEM);
            out.attributeDouble(null, "value", d);
            out.endTag(null, ImsConfig.EXTRA_CHANGED_ITEM);
        }
        out.endTag(null, "double-array");
    }

    public static final void writeStringArrayXml(String[] val, String name, TypedXmlSerializer out) throws XmlPullParserException, IOException {
        if (val == null) {
            out.startTag(null, "null");
            out.endTag(null, "null");
            return;
        }
        out.startTag(null, "string-array");
        if (name != null) {
            out.attribute(null, "name", name);
        }
        int N = val.length;
        out.attributeInt(null, "num", N);
        for (String str : val) {
            out.startTag(null, ImsConfig.EXTRA_CHANGED_ITEM);
            out.attribute(null, "value", str);
            out.endTag(null, ImsConfig.EXTRA_CHANGED_ITEM);
        }
        out.endTag(null, "string-array");
    }

    public static final void writeBooleanArrayXml(boolean[] val, String name, TypedXmlSerializer out) throws XmlPullParserException, IOException {
        if (val == null) {
            out.startTag(null, "null");
            out.endTag(null, "null");
            return;
        }
        out.startTag(null, "boolean-array");
        if (name != null) {
            out.attribute(null, "name", name);
        }
        int N = val.length;
        out.attributeInt(null, "num", N);
        for (boolean z : val) {
            out.startTag(null, ImsConfig.EXTRA_CHANGED_ITEM);
            out.attributeBoolean(null, "value", z);
            out.endTag(null, ImsConfig.EXTRA_CHANGED_ITEM);
        }
        out.endTag(null, "boolean-array");
    }

    @Deprecated
    public static final void writeValueXml(Object v, String name, XmlSerializer out) throws XmlPullParserException, IOException {
        writeValueXml(v, name, makeTyped(out));
    }

    public static final void writeValueXml(Object v, String name, TypedXmlSerializer out) throws XmlPullParserException, IOException {
        writeValueXml(v, name, out, null);
    }

    private static final void writeValueXml(Object v, String name, TypedXmlSerializer out, WriteMapCallback callback) throws XmlPullParserException, IOException {
        if (v == null) {
            out.startTag(null, "null");
            if (name != null) {
                out.attribute(null, "name", name);
            }
            out.endTag(null, "null");
        } else if (v instanceof String) {
            out.startTag(null, "string");
            if (name != null) {
                out.attribute(null, "name", name);
            }
            out.text(v.toString());
            out.endTag(null, "string");
        } else if (v instanceof Integer) {
            out.startTag(null, SliceItem.FORMAT_INT);
            if (name != null) {
                out.attribute(null, "name", name);
            }
            out.attributeInt(null, "value", ((Integer) v).intValue());
            out.endTag(null, SliceItem.FORMAT_INT);
        } else if (v instanceof Long) {
            out.startTag(null, "long");
            if (name != null) {
                out.attribute(null, "name", name);
            }
            out.attributeLong(null, "value", ((Long) v).longValue());
            out.endTag(null, "long");
        } else if (v instanceof Float) {
            out.startTag(null, "float");
            if (name != null) {
                out.attribute(null, "name", name);
            }
            out.attributeFloat(null, "value", ((Float) v).floatValue());
            out.endTag(null, "float");
        } else if (v instanceof Double) {
            out.startTag(null, "double");
            if (name != null) {
                out.attribute(null, "name", name);
            }
            out.attributeDouble(null, "value", ((Double) v).doubleValue());
            out.endTag(null, "double");
        } else if (v instanceof Boolean) {
            out.startTag(null, "boolean");
            if (name != null) {
                out.attribute(null, "name", name);
            }
            out.attributeBoolean(null, "value", ((Boolean) v).booleanValue());
            out.endTag(null, "boolean");
        } else if (v instanceof byte[]) {
            writeByteArrayXml((byte[]) v, name, out);
        } else if (v instanceof int[]) {
            writeIntArrayXml((int[]) v, name, out);
        } else if (v instanceof long[]) {
            writeLongArrayXml((long[]) v, name, out);
        } else if (v instanceof double[]) {
            writeDoubleArrayXml((double[]) v, name, out);
        } else if (v instanceof String[]) {
            writeStringArrayXml((String[]) v, name, out);
        } else if (v instanceof boolean[]) {
            writeBooleanArrayXml((boolean[]) v, name, out);
        } else if (v instanceof Map) {
            writeMapXml((Map) v, name, out);
        } else if (v instanceof List) {
            writeListXml((List) v, name, out);
        } else if (v instanceof Set) {
            writeSetXml((Set) v, name, out);
        } else if (v instanceof CharSequence) {
            out.startTag(null, "string");
            if (name != null) {
                out.attribute(null, "name", name);
            }
            out.text(v.toString());
            out.endTag(null, "string");
        } else if (callback != null) {
            callback.writeUnknownObject(v, name, out);
        } else {
            throw new RuntimeException("writeValueXml: unable to write value " + v);
        }
    }

    public static final HashMap<String, ?> readMapXml(InputStream in) throws XmlPullParserException, IOException {
        TypedXmlPullParser parser = Xml.newFastPullParser();
        parser.setInput(in, StandardCharsets.UTF_8.name());
        return (HashMap) readValueXml(parser, new String[1]);
    }

    public static final ArrayList readListXml(InputStream in) throws XmlPullParserException, IOException {
        TypedXmlPullParser parser = Xml.newFastPullParser();
        parser.setInput(in, StandardCharsets.UTF_8.name());
        return (ArrayList) readValueXml(parser, new String[1]);
    }

    public static final HashSet readSetXml(InputStream in) throws XmlPullParserException, IOException {
        TypedXmlPullParser parser = Xml.newFastPullParser();
        parser.setInput(in, StandardCharsets.UTF_8.name());
        return (HashSet) readValueXml(parser, new String[1]);
    }

    public static final HashMap<String, ?> readThisMapXml(TypedXmlPullParser parser, String endTag, String[] name) throws XmlPullParserException, IOException {
        return readThisMapXml(parser, endTag, name, null);
    }

    public static final HashMap<String, ?> readThisMapXml(TypedXmlPullParser parser, String endTag, String[] name, ReadMapCallback callback) throws XmlPullParserException, IOException {
        HashMap<String, Object> map = new HashMap<>();
        int eventType = parser.getEventType();
        do {
            if (eventType == 2) {
                Object val = readThisValueXml(parser, name, callback, false);
                map.put(name[0], val);
            } else if (eventType == 3) {
                if (parser.getName().equals(endTag)) {
                    return map;
                }
                throw new XmlPullParserException("Expected " + endTag + " end tag at: " + parser.getName());
            }
            eventType = parser.next();
        } while (eventType != 1);
        throw new XmlPullParserException("Document ended before " + endTag + " end tag");
    }

    public static final ArrayMap<String, ?> readThisArrayMapXml(TypedXmlPullParser parser, String endTag, String[] name, ReadMapCallback callback) throws XmlPullParserException, IOException {
        ArrayMap<String, Object> map = new ArrayMap<>();
        int eventType = parser.getEventType();
        do {
            if (eventType == 2) {
                Object val = readThisValueXml(parser, name, callback, true);
                map.put(name[0], val);
            } else if (eventType == 3) {
                if (parser.getName().equals(endTag)) {
                    return map;
                }
                throw new XmlPullParserException("Expected " + endTag + " end tag at: " + parser.getName());
            }
            eventType = parser.next();
        } while (eventType != 1);
        throw new XmlPullParserException("Document ended before " + endTag + " end tag");
    }

    public static final ArrayList readThisListXml(TypedXmlPullParser parser, String endTag, String[] name) throws XmlPullParserException, IOException {
        return readThisListXml(parser, endTag, name, null, false);
    }

    private static final ArrayList readThisListXml(TypedXmlPullParser parser, String endTag, String[] name, ReadMapCallback callback, boolean arrayMap) throws XmlPullParserException, IOException {
        ArrayList list = new ArrayList();
        int eventType = parser.getEventType();
        do {
            if (eventType == 2) {
                Object val = readThisValueXml(parser, name, callback, arrayMap);
                list.add(val);
            } else if (eventType == 3) {
                if (parser.getName().equals(endTag)) {
                    return list;
                }
                throw new XmlPullParserException("Expected " + endTag + " end tag at: " + parser.getName());
            }
            eventType = parser.next();
        } while (eventType != 1);
        throw new XmlPullParserException("Document ended before " + endTag + " end tag");
    }

    public static final HashSet readThisSetXml(TypedXmlPullParser parser, String endTag, String[] name) throws XmlPullParserException, IOException {
        return readThisSetXml(parser, endTag, name, null, false);
    }

    private static final HashSet readThisSetXml(TypedXmlPullParser parser, String endTag, String[] name, ReadMapCallback callback, boolean arrayMap) throws XmlPullParserException, IOException {
        HashSet set = new HashSet();
        int eventType = parser.getEventType();
        do {
            if (eventType == 2) {
                Object val = readThisValueXml(parser, name, callback, arrayMap);
                set.add(val);
            } else if (eventType == 3) {
                if (parser.getName().equals(endTag)) {
                    return set;
                }
                throw new XmlPullParserException("Expected " + endTag + " end tag at: " + parser.getName());
            }
            eventType = parser.next();
        } while (eventType != 1);
        throw new XmlPullParserException("Document ended before " + endTag + " end tag");
    }

    public static final byte[] readThisByteArrayXml(TypedXmlPullParser parser, String endTag, String[] name) throws XmlPullParserException, IOException {
        int num = parser.getAttributeInt(null, "num");
        byte[] array = new byte[0];
        int eventType = parser.getEventType();
        do {
            if (eventType == 4) {
                if (num > 0) {
                    String values = parser.getText();
                    if (values == null || values.length() != num * 2) {
                        throw new XmlPullParserException("Invalid value found in byte-array: " + values);
                    }
                    array = HexEncoding.decode(values);
                }
            } else if (eventType == 3) {
                if (parser.getName().equals(endTag)) {
                    return array;
                }
                throw new XmlPullParserException("Expected " + endTag + " end tag at: " + parser.getName());
            }
            eventType = parser.next();
        } while (eventType != 1);
        throw new XmlPullParserException("Document ended before " + endTag + " end tag");
    }

    public static final int[] readThisIntArrayXml(TypedXmlPullParser parser, String endTag, String[] name) throws XmlPullParserException, IOException {
        int num = parser.getAttributeInt(null, "num");
        parser.next();
        int[] array = new int[num];
        int i = 0;
        int eventType = parser.getEventType();
        do {
            if (eventType == 2) {
                if (parser.getName().equals(ImsConfig.EXTRA_CHANGED_ITEM)) {
                    array[i] = parser.getAttributeInt(null, "value");
                } else {
                    throw new XmlPullParserException("Expected item tag at: " + parser.getName());
                }
            } else if (eventType == 3) {
                if (parser.getName().equals(endTag)) {
                    return array;
                }
                if (parser.getName().equals(ImsConfig.EXTRA_CHANGED_ITEM)) {
                    i++;
                } else {
                    throw new XmlPullParserException("Expected " + endTag + " end tag at: " + parser.getName());
                }
            }
            eventType = parser.next();
        } while (eventType != 1);
        throw new XmlPullParserException("Document ended before " + endTag + " end tag");
    }

    public static final long[] readThisLongArrayXml(TypedXmlPullParser parser, String endTag, String[] name) throws XmlPullParserException, IOException {
        int num = parser.getAttributeInt(null, "num");
        parser.next();
        long[] array = new long[num];
        int i = 0;
        int eventType = parser.getEventType();
        do {
            if (eventType == 2) {
                if (parser.getName().equals(ImsConfig.EXTRA_CHANGED_ITEM)) {
                    array[i] = parser.getAttributeLong(null, "value");
                } else {
                    throw new XmlPullParserException("Expected item tag at: " + parser.getName());
                }
            } else if (eventType == 3) {
                if (parser.getName().equals(endTag)) {
                    return array;
                }
                if (parser.getName().equals(ImsConfig.EXTRA_CHANGED_ITEM)) {
                    i++;
                } else {
                    throw new XmlPullParserException("Expected " + endTag + " end tag at: " + parser.getName());
                }
            }
            eventType = parser.next();
        } while (eventType != 1);
        throw new XmlPullParserException("Document ended before " + endTag + " end tag");
    }

    public static final double[] readThisDoubleArrayXml(TypedXmlPullParser parser, String endTag, String[] name) throws XmlPullParserException, IOException {
        int num = parser.getAttributeInt(null, "num");
        parser.next();
        double[] array = new double[num];
        int i = 0;
        int eventType = parser.getEventType();
        do {
            if (eventType == 2) {
                if (parser.getName().equals(ImsConfig.EXTRA_CHANGED_ITEM)) {
                    array[i] = parser.getAttributeDouble(null, "value");
                } else {
                    throw new XmlPullParserException("Expected item tag at: " + parser.getName());
                }
            } else if (eventType == 3) {
                if (parser.getName().equals(endTag)) {
                    return array;
                }
                if (parser.getName().equals(ImsConfig.EXTRA_CHANGED_ITEM)) {
                    i++;
                } else {
                    throw new XmlPullParserException("Expected " + endTag + " end tag at: " + parser.getName());
                }
            }
            eventType = parser.next();
        } while (eventType != 1);
        throw new XmlPullParserException("Document ended before " + endTag + " end tag");
    }

    public static final String[] readThisStringArrayXml(TypedXmlPullParser parser, String endTag, String[] name) throws XmlPullParserException, IOException {
        int num = parser.getAttributeInt(null, "num");
        parser.next();
        String[] array = new String[num];
        int i = 0;
        int eventType = parser.getEventType();
        do {
            if (eventType == 2) {
                if (parser.getName().equals(ImsConfig.EXTRA_CHANGED_ITEM)) {
                    array[i] = parser.getAttributeValue(null, "value");
                } else {
                    throw new XmlPullParserException("Expected item tag at: " + parser.getName());
                }
            } else if (eventType == 3) {
                if (parser.getName().equals(endTag)) {
                    return array;
                }
                if (parser.getName().equals(ImsConfig.EXTRA_CHANGED_ITEM)) {
                    i++;
                } else {
                    throw new XmlPullParserException("Expected " + endTag + " end tag at: " + parser.getName());
                }
            }
            eventType = parser.next();
        } while (eventType != 1);
        throw new XmlPullParserException("Document ended before " + endTag + " end tag");
    }

    public static final boolean[] readThisBooleanArrayXml(TypedXmlPullParser parser, String endTag, String[] name) throws XmlPullParserException, IOException {
        int num = parser.getAttributeInt(null, "num");
        parser.next();
        boolean[] array = new boolean[num];
        int i = 0;
        int eventType = parser.getEventType();
        do {
            if (eventType == 2) {
                if (parser.getName().equals(ImsConfig.EXTRA_CHANGED_ITEM)) {
                    array[i] = parser.getAttributeBoolean(null, "value");
                } else {
                    throw new XmlPullParserException("Expected item tag at: " + parser.getName());
                }
            } else if (eventType == 3) {
                if (parser.getName().equals(endTag)) {
                    return array;
                }
                if (parser.getName().equals(ImsConfig.EXTRA_CHANGED_ITEM)) {
                    i++;
                } else {
                    throw new XmlPullParserException("Expected " + endTag + " end tag at: " + parser.getName());
                }
            }
            eventType = parser.next();
        } while (eventType != 1);
        throw new XmlPullParserException("Document ended before " + endTag + " end tag");
    }

    public static final Object readValueXml(TypedXmlPullParser parser, String[] name) throws XmlPullParserException, IOException {
        int eventType = parser.getEventType();
        while (eventType != 2) {
            if (eventType == 3) {
                throw new XmlPullParserException("Unexpected end tag at: " + parser.getName());
            }
            if (eventType == 4) {
                throw new XmlPullParserException("Unexpected text: " + parser.getText());
            }
            eventType = parser.next();
            if (eventType == 1) {
                throw new XmlPullParserException("Unexpected end of document");
            }
        }
        return readThisValueXml(parser, name, null, false);
    }

    private static final Object readThisValueXml(TypedXmlPullParser parser, String[] name, ReadMapCallback callback, boolean arrayMap) throws XmlPullParserException, IOException {
        Object res;
        Object res2;
        int eventType;
        String valueName = parser.getAttributeValue(null, "name");
        String tagName = parser.getName();
        if (tagName.equals("null")) {
            res2 = null;
        } else if (tagName.equals("string")) {
            StringBuilder value = new StringBuilder();
            while (true) {
                int eventType2 = parser.next();
                if (eventType2 != 1) {
                    if (eventType2 == 3) {
                        if (parser.getName().equals("string")) {
                            name[0] = valueName;
                            return value.toString();
                        }
                        throw new XmlPullParserException("Unexpected end tag in <string>: " + parser.getName());
                    } else if (eventType2 == 4) {
                        value.append(parser.getText());
                    } else if (eventType2 == 2) {
                        throw new XmlPullParserException("Unexpected start tag in <string>: " + parser.getName());
                    }
                } else {
                    throw new XmlPullParserException("Unexpected end of document in <string>");
                }
            }
        } else {
            Object res3 = readThisPrimitiveValueXml(parser, tagName);
            if (res3 == null) {
                if (tagName.equals("byte-array")) {
                    Object res4 = readThisByteArrayXml(parser, "byte-array", name);
                    name[0] = valueName;
                    return res4;
                } else if (tagName.equals("int-array")) {
                    Object res5 = readThisIntArrayXml(parser, "int-array", name);
                    name[0] = valueName;
                    return res5;
                } else if (tagName.equals("long-array")) {
                    Object res6 = readThisLongArrayXml(parser, "long-array", name);
                    name[0] = valueName;
                    return res6;
                } else if (tagName.equals("double-array")) {
                    Object res7 = readThisDoubleArrayXml(parser, "double-array", name);
                    name[0] = valueName;
                    return res7;
                } else if (tagName.equals("string-array")) {
                    Object res8 = readThisStringArrayXml(parser, "string-array", name);
                    name[0] = valueName;
                    return res8;
                } else if (tagName.equals("boolean-array")) {
                    Object res9 = readThisBooleanArrayXml(parser, "boolean-array", name);
                    name[0] = valueName;
                    return res9;
                } else if (tagName.equals("map")) {
                    parser.next();
                    if (arrayMap) {
                        res = readThisArrayMapXml(parser, "map", name, callback);
                    } else {
                        res = readThisMapXml(parser, "map", name, callback);
                    }
                    name[0] = valueName;
                    return res;
                } else if (tagName.equals(Slice.HINT_LIST)) {
                    parser.next();
                    Object res10 = readThisListXml(parser, Slice.HINT_LIST, name, callback, arrayMap);
                    name[0] = valueName;
                    return res10;
                } else if (tagName.equals("set")) {
                    parser.next();
                    Object res11 = readThisSetXml(parser, "set", name, callback, arrayMap);
                    name[0] = valueName;
                    return res11;
                } else if (callback != null) {
                    Object res12 = callback.readThisUnknownObjectXml(parser, tagName);
                    name[0] = valueName;
                    return res12;
                } else {
                    throw new XmlPullParserException("Unknown tag: " + tagName);
                }
            }
            res2 = res3;
        }
        do {
            eventType = parser.next();
            if (eventType == 1) {
                throw new XmlPullParserException("Unexpected end of document in <" + tagName + ">");
            }
            if (eventType == 3) {
                if (parser.getName().equals(tagName)) {
                    name[0] = valueName;
                    return res2;
                }
                throw new XmlPullParserException("Unexpected end tag in <" + tagName + ">: " + parser.getName());
            } else if (eventType == 4) {
                throw new XmlPullParserException("Unexpected text in <" + tagName + ">: " + parser.getName());
            }
        } while (eventType != 2);
        throw new XmlPullParserException("Unexpected start tag in <" + tagName + ">: " + parser.getName());
    }

    private static final Object readThisPrimitiveValueXml(TypedXmlPullParser parser, String tagName) throws XmlPullParserException, IOException {
        if (tagName.equals(SliceItem.FORMAT_INT)) {
            return Integer.valueOf(parser.getAttributeInt(null, "value"));
        }
        if (tagName.equals("long")) {
            return Long.valueOf(parser.getAttributeLong(null, "value"));
        }
        if (tagName.equals("float")) {
            return Float.valueOf(parser.getAttributeFloat(null, "value"));
        }
        if (tagName.equals("double")) {
            return Double.valueOf(parser.getAttributeDouble(null, "value"));
        }
        if (tagName.equals("boolean")) {
            return Boolean.valueOf(parser.getAttributeBoolean(null, "value"));
        }
        return null;
    }

    public static final void beginDocument(XmlPullParser parser, String firstElementName) throws XmlPullParserException, IOException {
        int type;
        do {
            type = parser.next();
            if (type == 2) {
                break;
            }
        } while (type != 1);
        if (type != 2) {
            throw new XmlPullParserException("No start tag found");
        }
        if (!parser.getName().equals(firstElementName)) {
            throw new XmlPullParserException("Unexpected start tag: found " + parser.getName() + ", expected " + firstElementName);
        }
    }

    public static final void nextElement(XmlPullParser parser) throws XmlPullParserException, IOException {
        int type;
        do {
            type = parser.next();
            if (type == 2) {
                return;
            }
        } while (type != 1);
    }

    public static boolean nextElementWithin(XmlPullParser parser, int outerDepth) throws IOException, XmlPullParserException {
        while (true) {
            int type = parser.next();
            if (type != 1) {
                if (type == 3 && parser.getDepth() == outerDepth) {
                    return false;
                }
                if (type == 2 && parser.getDepth() == outerDepth + 1) {
                    return true;
                }
            } else {
                return false;
            }
        }
    }

    public static int readIntAttribute(XmlPullParser in, String name, int defaultValue) {
        if (in instanceof TypedXmlPullParser) {
            return ((TypedXmlPullParser) in).getAttributeInt(null, name, defaultValue);
        }
        String value = in.getAttributeValue(null, name);
        if (TextUtils.isEmpty(value)) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public static int readIntAttribute(XmlPullParser in, String name) throws IOException {
        if (in instanceof TypedXmlPullParser) {
            try {
                return ((TypedXmlPullParser) in).getAttributeInt(null, name);
            } catch (XmlPullParserException e) {
                throw new ProtocolException(e.getMessage());
            }
        }
        String value = in.getAttributeValue(null, name);
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e2) {
            throw new ProtocolException("problem parsing " + name + "=" + value + " as int");
        }
    }

    public static void writeIntAttribute(XmlSerializer out, String name, int value) throws IOException {
        if (out instanceof TypedXmlSerializer) {
            ((TypedXmlSerializer) out).attributeInt(null, name, value);
        } else {
            out.attribute(null, name, Integer.toString(value));
        }
    }

    public static long readLongAttribute(XmlPullParser in, String name, long defaultValue) {
        if (in instanceof TypedXmlPullParser) {
            return ((TypedXmlPullParser) in).getAttributeLong(null, name, defaultValue);
        }
        String value = in.getAttributeValue(null, name);
        if (TextUtils.isEmpty(value)) {
            return defaultValue;
        }
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public static long readLongAttribute(XmlPullParser in, String name) throws IOException {
        if (in instanceof TypedXmlPullParser) {
            try {
                return ((TypedXmlPullParser) in).getAttributeLong(null, name);
            } catch (XmlPullParserException e) {
                throw new ProtocolException(e.getMessage());
            }
        }
        String value = in.getAttributeValue(null, name);
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e2) {
            throw new ProtocolException("problem parsing " + name + "=" + value + " as long");
        }
    }

    public static void writeLongAttribute(XmlSerializer out, String name, long value) throws IOException {
        if (out instanceof TypedXmlSerializer) {
            ((TypedXmlSerializer) out).attributeLong(null, name, value);
        } else {
            out.attribute(null, name, Long.toString(value));
        }
    }

    public static float readFloatAttribute(XmlPullParser in, String name) throws IOException {
        if (in instanceof TypedXmlPullParser) {
            try {
                return ((TypedXmlPullParser) in).getAttributeFloat(null, name);
            } catch (XmlPullParserException e) {
                throw new ProtocolException(e.getMessage());
            }
        }
        String value = in.getAttributeValue(null, name);
        try {
            return Float.parseFloat(value);
        } catch (NumberFormatException e2) {
            throw new ProtocolException("problem parsing " + name + "=" + value + " as long");
        }
    }

    public static void writeFloatAttribute(XmlSerializer out, String name, float value) throws IOException {
        if (out instanceof TypedXmlSerializer) {
            ((TypedXmlSerializer) out).attributeFloat(null, name, value);
        } else {
            out.attribute(null, name, Float.toString(value));
        }
    }

    public static boolean readBooleanAttribute(XmlPullParser in, String name) {
        return readBooleanAttribute(in, name, false);
    }

    public static boolean readBooleanAttribute(XmlPullParser in, String name, boolean defaultValue) {
        if (in instanceof TypedXmlPullParser) {
            return ((TypedXmlPullParser) in).getAttributeBoolean(null, name, defaultValue);
        }
        String value = in.getAttributeValue(null, name);
        if (TextUtils.isEmpty(value)) {
            return defaultValue;
        }
        return Boolean.parseBoolean(value);
    }

    public static void writeBooleanAttribute(XmlSerializer out, String name, boolean value) throws IOException {
        if (out instanceof TypedXmlSerializer) {
            ((TypedXmlSerializer) out).attributeBoolean(null, name, value);
        } else {
            out.attribute(null, name, Boolean.toString(value));
        }
    }

    public static Uri readUriAttribute(XmlPullParser in, String name) {
        String value = in.getAttributeValue(null, name);
        if (value != null) {
            return Uri.parse(value);
        }
        return null;
    }

    public static void writeUriAttribute(XmlSerializer out, String name, Uri value) throws IOException {
        if (value != null) {
            out.attribute(null, name, value.toString());
        }
    }

    public static String readStringAttribute(XmlPullParser in, String name) {
        return in.getAttributeValue(null, name);
    }

    public static void writeStringAttribute(XmlSerializer out, String name, CharSequence value) throws IOException {
        if (value != null) {
            out.attribute(null, name, value.toString());
        }
    }

    public static byte[] readByteArrayAttribute(XmlPullParser in, String name) {
        if (in instanceof TypedXmlPullParser) {
            try {
                return ((TypedXmlPullParser) in).getAttributeBytesBase64(null, name);
            } catch (XmlPullParserException e) {
                return null;
            }
        }
        String value = in.getAttributeValue(null, name);
        if (TextUtils.isEmpty(value)) {
            return null;
        }
        return Base64.decode(value, 0);
    }

    public static void writeByteArrayAttribute(XmlSerializer out, String name, byte[] value) throws IOException {
        if (value != null) {
            if (out instanceof TypedXmlSerializer) {
                ((TypedXmlSerializer) out).attributeBytesBase64(null, name, value);
            } else {
                out.attribute(null, name, Base64.encodeToString(value, 0));
            }
        }
    }

    public static Bitmap readBitmapAttribute(XmlPullParser in, String name) {
        byte[] value = readByteArrayAttribute(in, name);
        if (value != null) {
            return BitmapFactory.decodeByteArray(value, 0, value.length);
        }
        return null;
    }

    @Deprecated
    public static void writeBitmapAttribute(XmlSerializer out, String name, Bitmap value) throws IOException {
        if (value != null) {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            value.compress(Bitmap.CompressFormat.PNG, 90, os);
            writeByteArrayAttribute(out, name, os.toByteArray());
        }
    }
}
