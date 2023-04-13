package android.util;

import android.p008os.SystemProperties;
import android.system.ErrnoException;
import android.system.Os;
import com.android.internal.util.ArtBinaryXmlPullParser;
import com.android.internal.util.ArtBinaryXmlSerializer;
import com.android.internal.util.FastXmlSerializer;
import com.android.internal.util.XmlUtils;
import com.android.modules.utils.BinaryXmlSerializer;
import com.android.modules.utils.TypedXmlPullParser;
import com.android.modules.utils.TypedXmlSerializer;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import libcore.util.XmlObjectFactory;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;
/* loaded from: classes3.dex */
public class Xml {
    public static String FEATURE_RELAXED = "http://xmlpull.org/v1/doc/features.html#relaxed";
    public static final boolean ENABLE_BINARY_DEFAULT = SystemProperties.getBoolean("persist.sys.binary_xml", true);

    private Xml() {
    }

    public static void parse(String xml, ContentHandler contentHandler) throws SAXException {
        try {
            XMLReader reader = XmlObjectFactory.newXMLReader();
            reader.setContentHandler(contentHandler);
            reader.parse(new InputSource(new StringReader(xml)));
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }

    public static void parse(Reader in, ContentHandler contentHandler) throws IOException, SAXException {
        XMLReader reader = XmlObjectFactory.newXMLReader();
        reader.setContentHandler(contentHandler);
        reader.parse(new InputSource(in));
    }

    public static void parse(InputStream in, Encoding encoding, ContentHandler contentHandler) throws IOException, SAXException {
        XMLReader reader = XmlObjectFactory.newXMLReader();
        reader.setContentHandler(contentHandler);
        InputSource source = new InputSource(in);
        source.setEncoding(encoding.expatName);
        reader.parse(source);
    }

    public static XmlPullParser newPullParser() {
        try {
            XmlPullParser parser = XmlObjectFactory.newXmlPullParser();
            parser.setFeature("http://xmlpull.org/v1/doc/features.html#process-docdecl", true);
            parser.setFeature("http://xmlpull.org/v1/doc/features.html#process-namespaces", true);
            return parser;
        } catch (XmlPullParserException e) {
            throw new AssertionError();
        }
    }

    public static TypedXmlPullParser newFastPullParser() {
        return XmlUtils.makeTyped(newPullParser());
    }

    public static TypedXmlPullParser newBinaryPullParser() {
        return new ArtBinaryXmlPullParser();
    }

    public static TypedXmlPullParser resolvePullParser(InputStream in) throws IOException {
        TypedXmlPullParser xml;
        byte[] magic = new byte[4];
        if (in instanceof FileInputStream) {
            try {
                Os.pread(((FileInputStream) in).getFD(), magic, 0, magic.length, 0L);
            } catch (ErrnoException e) {
                throw e.rethrowAsIOException();
            }
        } else {
            if (!in.markSupported()) {
                in = new BufferedInputStream(in);
            }
            in.mark(8);
            in.read(magic);
            in.reset();
        }
        if (Arrays.equals(magic, BinaryXmlSerializer.PROTOCOL_MAGIC_VERSION_0)) {
            xml = newBinaryPullParser();
        } else {
            xml = newFastPullParser();
        }
        try {
            xml.setInput(in, StandardCharsets.UTF_8.name());
            return xml;
        } catch (XmlPullParserException e2) {
            throw new IOException(e2);
        }
    }

    public static XmlSerializer newSerializer() {
        return XmlObjectFactory.newXmlSerializer();
    }

    public static TypedXmlSerializer newFastSerializer() {
        return XmlUtils.makeTyped(new FastXmlSerializer());
    }

    public static TypedXmlSerializer newBinarySerializer() {
        return new ArtBinaryXmlSerializer();
    }

    public static TypedXmlSerializer resolveSerializer(OutputStream out) throws IOException {
        TypedXmlSerializer xml;
        if (ENABLE_BINARY_DEFAULT) {
            xml = newBinarySerializer();
        } else {
            xml = newFastSerializer();
        }
        xml.setOutput(out, StandardCharsets.UTF_8.name());
        return xml;
    }

    public static void copy(XmlPullParser in, XmlSerializer out) throws XmlPullParserException, IOException {
        if (in.getEventType() == 0) {
            out.startDocument(in.getInputEncoding(), true);
        }
        while (true) {
            int token = in.nextToken();
            switch (token) {
                case 0:
                    out.startDocument(in.getInputEncoding(), true);
                    break;
                case 1:
                    out.endDocument();
                    return;
                case 2:
                    out.startTag(normalizeNamespace(in.getNamespace()), in.getName());
                    for (int i = 0; i < in.getAttributeCount(); i++) {
                        out.attribute(normalizeNamespace(in.getAttributeNamespace(i)), in.getAttributeName(i), in.getAttributeValue(i));
                    }
                    break;
                case 3:
                    out.endTag(normalizeNamespace(in.getNamespace()), in.getName());
                    break;
                case 4:
                    out.text(in.getText());
                    break;
                case 5:
                    out.cdsect(in.getText());
                    break;
                case 6:
                    out.entityRef(in.getName());
                    break;
                case 7:
                    out.ignorableWhitespace(in.getText());
                    break;
                case 8:
                    out.processingInstruction(in.getText());
                    break;
                case 9:
                    out.comment(in.getText());
                    break;
                case 10:
                    out.docdecl(in.getText());
                    break;
                default:
                    throw new IllegalStateException("Unknown token " + token);
            }
        }
    }

    private static String normalizeNamespace(String namespace) {
        if (namespace == null || namespace.isEmpty()) {
            return null;
        }
        return namespace;
    }

    /* loaded from: classes3.dex */
    public enum Encoding {
        US_ASCII("US-ASCII"),
        UTF_8("UTF-8"),
        UTF_16("UTF-16"),
        ISO_8859_1("ISO-8859-1");
        
        final String expatName;

        Encoding(String expatName) {
            this.expatName = expatName;
        }
    }

    public static Encoding findEncodingByName(String encodingName) throws UnsupportedEncodingException {
        Encoding[] values;
        if (encodingName == null) {
            return Encoding.UTF_8;
        }
        for (Encoding encoding : Encoding.values()) {
            if (encoding.expatName.equalsIgnoreCase(encodingName)) {
                return encoding;
            }
        }
        throw new UnsupportedEncodingException(encodingName);
    }

    public static AttributeSet asAttributeSet(XmlPullParser parser) {
        if (parser instanceof AttributeSet) {
            return (AttributeSet) parser;
        }
        return new XmlPullAttributes(parser);
    }
}
