package com.android.modules.utils;

import java.io.IOException;
import org.xmlpull.v1.XmlSerializer;
/* loaded from: classes5.dex */
public interface TypedXmlSerializer extends XmlSerializer {
    XmlSerializer attributeBoolean(String str, String str2, boolean z) throws IOException;

    XmlSerializer attributeBytesBase64(String str, String str2, byte[] bArr) throws IOException;

    XmlSerializer attributeBytesHex(String str, String str2, byte[] bArr) throws IOException;

    XmlSerializer attributeDouble(String str, String str2, double d) throws IOException;

    XmlSerializer attributeFloat(String str, String str2, float f) throws IOException;

    XmlSerializer attributeInt(String str, String str2, int i) throws IOException;

    XmlSerializer attributeIntHex(String str, String str2, int i) throws IOException;

    XmlSerializer attributeInterned(String str, String str2, String str3) throws IOException;

    XmlSerializer attributeLong(String str, String str2, long j) throws IOException;

    XmlSerializer attributeLongHex(String str, String str2, long j) throws IOException;
}
