package com.android.server.graphics.fonts;

import android.graphics.fonts.FontUpdateRequest;
import android.text.TextUtils;
import android.util.ArraySet;
import android.util.Slog;
import android.util.Xml;
import com.android.modules.utils.TypedXmlPullParser;
import com.android.modules.utils.TypedXmlSerializer;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes.dex */
public class PersistentSystemFontConfig {

    /* loaded from: classes.dex */
    public static class Config {
        public long lastModifiedMillis;
        public final Set<String> updatedFontDirs = new ArraySet();
        public final List<FontUpdateRequest.Family> fontFamilies = new ArrayList();
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Code restructure failed: missing block: B:23:0x0059, code lost:
        if (r3.equals("family") == false) goto L14;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public static void loadFromXml(InputStream inputStream, Config config) throws XmlPullParserException, IOException {
        TypedXmlPullParser resolvePullParser = Xml.resolvePullParser(inputStream);
        while (true) {
            int next = resolvePullParser.next();
            boolean z = true;
            if (next == 1) {
                return;
            }
            if (next == 2) {
                int depth = resolvePullParser.getDepth();
                String name = resolvePullParser.getName();
                if (depth == 1) {
                    if (!"fontConfig".equals(name)) {
                        Slog.e("PersistentSystemFontConfig", "Invalid root tag: " + name);
                        return;
                    }
                } else if (depth == 2) {
                    name.hashCode();
                    switch (name.hashCode()) {
                        case -1540845619:
                            if (name.equals("lastModifiedDate")) {
                                z = false;
                                break;
                            }
                            z = true;
                            break;
                        case -1281860764:
                            break;
                        case -23402365:
                            if (name.equals("updatedFontDir")) {
                                z = true;
                                break;
                            }
                            z = true;
                            break;
                        default:
                            z = true;
                            break;
                    }
                    switch (z) {
                        case false:
                            config.lastModifiedMillis = parseLongAttribute(resolvePullParser, "value", 0L);
                            continue;
                        case true:
                            config.fontFamilies.add(FontUpdateRequest.Family.readFromXml(resolvePullParser));
                            continue;
                        case true:
                            config.updatedFontDirs.add(getAttribute(resolvePullParser, "value"));
                            continue;
                        default:
                            Slog.w("PersistentSystemFontConfig", "Skipping unknown tag: " + name);
                            continue;
                    }
                }
            }
        }
    }

    public static void writeToXml(OutputStream outputStream, Config config) throws IOException {
        TypedXmlSerializer resolveSerializer = Xml.resolveSerializer(outputStream);
        resolveSerializer.startDocument((String) null, Boolean.TRUE);
        resolveSerializer.startTag((String) null, "fontConfig");
        resolveSerializer.startTag((String) null, "lastModifiedDate");
        resolveSerializer.attribute((String) null, "value", Long.toString(config.lastModifiedMillis));
        resolveSerializer.endTag((String) null, "lastModifiedDate");
        for (String str : config.updatedFontDirs) {
            resolveSerializer.startTag((String) null, "updatedFontDir");
            resolveSerializer.attribute((String) null, "value", str);
            resolveSerializer.endTag((String) null, "updatedFontDir");
        }
        List<FontUpdateRequest.Family> list = config.fontFamilies;
        for (int i = 0; i < list.size(); i++) {
            resolveSerializer.startTag((String) null, "family");
            FontUpdateRequest.Family.writeFamilyToXml(resolveSerializer, list.get(i));
            resolveSerializer.endTag((String) null, "family");
        }
        resolveSerializer.endTag((String) null, "fontConfig");
        resolveSerializer.endDocument();
    }

    public static long parseLongAttribute(TypedXmlPullParser typedXmlPullParser, String str, long j) {
        String attributeValue = typedXmlPullParser.getAttributeValue((String) null, str);
        if (TextUtils.isEmpty(attributeValue)) {
            return j;
        }
        try {
            return Long.parseLong(attributeValue);
        } catch (NumberFormatException unused) {
            return j;
        }
    }

    public static String getAttribute(TypedXmlPullParser typedXmlPullParser, String str) {
        String attributeValue = typedXmlPullParser.getAttributeValue((String) null, str);
        return attributeValue == null ? "" : attributeValue;
    }
}
