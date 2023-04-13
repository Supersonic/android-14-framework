package android.content.p001pm;

import com.android.internal.util.XmlUtils;
import com.android.modules.utils.TypedXmlPullParser;
import com.android.modules.utils.TypedXmlSerializer;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;
/* renamed from: android.content.pm.XmlSerializerAndParser */
/* loaded from: classes.dex */
public interface XmlSerializerAndParser<T> {
    T createFromXml(TypedXmlPullParser typedXmlPullParser) throws IOException, XmlPullParserException;

    void writeAsXml(T t, TypedXmlSerializer typedXmlSerializer) throws IOException;

    default void writeAsXml(T item, XmlSerializer out) throws IOException {
        writeAsXml((XmlSerializerAndParser<T>) item, XmlUtils.makeTyped(out));
    }

    default T createFromXml(XmlPullParser parser) throws IOException, XmlPullParserException {
        return createFromXml(XmlUtils.makeTyped(parser));
    }
}
