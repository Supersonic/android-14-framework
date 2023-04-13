package com.android.modules.utils;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes5.dex */
public interface TypedXmlPullParser extends XmlPullParser {
    boolean getAttributeBoolean(int i) throws XmlPullParserException;

    byte[] getAttributeBytesBase64(int i) throws XmlPullParserException;

    byte[] getAttributeBytesHex(int i) throws XmlPullParserException;

    double getAttributeDouble(int i) throws XmlPullParserException;

    float getAttributeFloat(int i) throws XmlPullParserException;

    int getAttributeInt(int i) throws XmlPullParserException;

    int getAttributeIntHex(int i) throws XmlPullParserException;

    long getAttributeLong(int i) throws XmlPullParserException;

    long getAttributeLongHex(int i) throws XmlPullParserException;

    default int getAttributeIndex(String namespace, String name) {
        boolean namespaceNull = namespace == null;
        int count = getAttributeCount();
        for (int i = 0; i < count; i++) {
            if ((namespaceNull || namespace.equals(getAttributeNamespace(i))) && name.equals(getAttributeName(i))) {
                return i;
            }
        }
        return -1;
    }

    default int getAttributeIndexOrThrow(String namespace, String name) throws XmlPullParserException {
        int index = getAttributeIndex(namespace, name);
        if (index == -1) {
            throw new XmlPullParserException("Missing attribute " + name);
        }
        return index;
    }

    default byte[] getAttributeBytesHex(String namespace, String name) throws XmlPullParserException {
        return getAttributeBytesHex(getAttributeIndexOrThrow(namespace, name));
    }

    default byte[] getAttributeBytesBase64(String namespace, String name) throws XmlPullParserException {
        return getAttributeBytesBase64(getAttributeIndexOrThrow(namespace, name));
    }

    default int getAttributeInt(String namespace, String name) throws XmlPullParserException {
        return getAttributeInt(getAttributeIndexOrThrow(namespace, name));
    }

    default int getAttributeIntHex(String namespace, String name) throws XmlPullParserException {
        return getAttributeIntHex(getAttributeIndexOrThrow(namespace, name));
    }

    default long getAttributeLong(String namespace, String name) throws XmlPullParserException {
        return getAttributeLong(getAttributeIndexOrThrow(namespace, name));
    }

    default long getAttributeLongHex(String namespace, String name) throws XmlPullParserException {
        return getAttributeLongHex(getAttributeIndexOrThrow(namespace, name));
    }

    default float getAttributeFloat(String namespace, String name) throws XmlPullParserException {
        return getAttributeFloat(getAttributeIndexOrThrow(namespace, name));
    }

    default double getAttributeDouble(String namespace, String name) throws XmlPullParserException {
        return getAttributeDouble(getAttributeIndexOrThrow(namespace, name));
    }

    default boolean getAttributeBoolean(String namespace, String name) throws XmlPullParserException {
        return getAttributeBoolean(getAttributeIndexOrThrow(namespace, name));
    }

    default byte[] getAttributeBytesHex(String namespace, String name, byte[] defaultValue) {
        int index = getAttributeIndex(namespace, name);
        if (index == -1) {
            return defaultValue;
        }
        try {
            return getAttributeBytesHex(index);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    default byte[] getAttributeBytesBase64(String namespace, String name, byte[] defaultValue) {
        int index = getAttributeIndex(namespace, name);
        if (index == -1) {
            return defaultValue;
        }
        try {
            return getAttributeBytesBase64(index);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    default int getAttributeInt(String namespace, String name, int defaultValue) {
        int index = getAttributeIndex(namespace, name);
        if (index == -1) {
            return defaultValue;
        }
        try {
            return getAttributeInt(index);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    default int getAttributeIntHex(String namespace, String name, int defaultValue) {
        int index = getAttributeIndex(namespace, name);
        if (index == -1) {
            return defaultValue;
        }
        try {
            return getAttributeIntHex(index);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    default long getAttributeLong(String namespace, String name, long defaultValue) {
        int index = getAttributeIndex(namespace, name);
        if (index == -1) {
            return defaultValue;
        }
        try {
            return getAttributeLong(index);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    default long getAttributeLongHex(String namespace, String name, long defaultValue) {
        int index = getAttributeIndex(namespace, name);
        if (index == -1) {
            return defaultValue;
        }
        try {
            return getAttributeLongHex(index);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    default float getAttributeFloat(String namespace, String name, float defaultValue) {
        int index = getAttributeIndex(namespace, name);
        if (index == -1) {
            return defaultValue;
        }
        try {
            return getAttributeFloat(index);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    default double getAttributeDouble(String namespace, String name, double defaultValue) {
        int index = getAttributeIndex(namespace, name);
        if (index == -1) {
            return defaultValue;
        }
        try {
            return getAttributeDouble(index);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    default boolean getAttributeBoolean(String namespace, String name, boolean defaultValue) {
        int index = getAttributeIndex(namespace, name);
        if (index == -1) {
            return defaultValue;
        }
        try {
            return getAttributeBoolean(index);
        } catch (Exception e) {
            return defaultValue;
        }
    }
}
