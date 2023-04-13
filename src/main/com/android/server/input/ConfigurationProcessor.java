package com.android.server.input;

import android.text.TextUtils;
import android.util.Slog;
import android.util.Xml;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.util.XmlUtils;
import com.android.modules.utils.TypedXmlPullParser;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/* loaded from: classes.dex */
public class ConfigurationProcessor {
    public static List<String> processExcludedDeviceNames(InputStream inputStream) throws Exception {
        ArrayList arrayList = new ArrayList();
        TypedXmlPullParser resolvePullParser = Xml.resolvePullParser(inputStream);
        XmlUtils.beginDocument(resolvePullParser, "devices");
        while (true) {
            XmlUtils.nextElement(resolvePullParser);
            if (!"device".equals(resolvePullParser.getName())) {
                return arrayList;
            }
            String attributeValue = resolvePullParser.getAttributeValue((String) null, "name");
            if (attributeValue != null) {
                arrayList.add(attributeValue);
            }
        }
    }

    @VisibleForTesting
    public static Map<String, Integer> processInputPortAssociations(InputStream inputStream) throws Exception {
        HashMap hashMap = new HashMap();
        TypedXmlPullParser resolvePullParser = Xml.resolvePullParser(inputStream);
        XmlUtils.beginDocument(resolvePullParser, "ports");
        while (true) {
            XmlUtils.nextElement(resolvePullParser);
            if (!"port".equals(resolvePullParser.getName())) {
                return hashMap;
            }
            String attributeValue = resolvePullParser.getAttributeValue((String) null, "input");
            String attributeValue2 = resolvePullParser.getAttributeValue((String) null, "display");
            if (TextUtils.isEmpty(attributeValue) || TextUtils.isEmpty(attributeValue2)) {
                Slog.wtf("ConfigurationProcessor", "Ignoring incomplete entry");
            } else {
                try {
                    hashMap.put(attributeValue, Integer.valueOf(Integer.parseUnsignedInt(attributeValue2)));
                } catch (NumberFormatException unused) {
                    Slog.wtf("ConfigurationProcessor", "Display port should be an integer");
                }
            }
        }
    }
}
