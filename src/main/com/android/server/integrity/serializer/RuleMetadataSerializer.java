package com.android.server.integrity.serializer;

import android.util.Xml;
import com.android.modules.utils.TypedXmlSerializer;
import com.android.server.integrity.model.RuleMetadata;
import java.io.IOException;
import java.io.OutputStream;
/* loaded from: classes.dex */
public class RuleMetadataSerializer {
    public static void serialize(RuleMetadata ruleMetadata, OutputStream outputStream) throws IOException {
        TypedXmlSerializer resolveSerializer = Xml.resolveSerializer(outputStream);
        serializeTaggedValue(resolveSerializer, "P", ruleMetadata.getRuleProvider());
        serializeTaggedValue(resolveSerializer, "V", ruleMetadata.getVersion());
        resolveSerializer.endDocument();
    }

    public static void serializeTaggedValue(TypedXmlSerializer typedXmlSerializer, String str, String str2) throws IOException {
        typedXmlSerializer.startTag((String) null, str);
        typedXmlSerializer.text(str2);
        typedXmlSerializer.endTag((String) null, str);
    }
}
