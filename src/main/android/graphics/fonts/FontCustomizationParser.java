package android.graphics.fonts;

import android.graphics.FontListParser;
import android.text.FontConfig;
import android.util.Xml;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes.dex */
public class FontCustomizationParser {
    private static final String TAG = "FontCustomizationParser";

    /* loaded from: classes.dex */
    public static class Result {
        private final List<FontConfig.Alias> mAdditionalAliases;
        private final Map<String, FontConfig.NamedFamilyList> mAdditionalNamedFamilies;

        public Result() {
            this.mAdditionalNamedFamilies = Collections.emptyMap();
            this.mAdditionalAliases = Collections.emptyList();
        }

        public Result(Map<String, FontConfig.NamedFamilyList> additionalNamedFamilies, List<FontConfig.Alias> additionalAliases) {
            this.mAdditionalNamedFamilies = additionalNamedFamilies;
            this.mAdditionalAliases = additionalAliases;
        }

        public Map<String, FontConfig.NamedFamilyList> getAdditionalNamedFamilies() {
            return this.mAdditionalNamedFamilies;
        }

        public List<FontConfig.Alias> getAdditionalAliases() {
            return this.mAdditionalAliases;
        }
    }

    public static Result parse(InputStream in, String fontDir, Map<String, File> updatableFontMap) throws XmlPullParserException, IOException {
        XmlPullParser parser = Xml.newPullParser();
        parser.setInput(in, null);
        parser.nextTag();
        return readFamilies(parser, fontDir, updatableFontMap);
    }

    private static Result validateAndTransformToResult(List<FontConfig.NamedFamilyList> families, List<FontConfig.Alias> aliases) {
        HashMap<String, FontConfig.NamedFamilyList> namedFamily = new HashMap<>();
        for (int i = 0; i < families.size(); i++) {
            FontConfig.NamedFamilyList family = families.get(i);
            String name = family.getName();
            if (name != null) {
                if (namedFamily.put(name, family) != null) {
                    throw new IllegalArgumentException("new-named-family requires unique name attribute");
                }
            } else {
                throw new IllegalArgumentException("new-named-family requires name attribute or new-default-fallback-familyrequires fallackTarget attribute");
            }
        }
        return new Result(namedFamily, aliases);
    }

    private static Result readFamilies(XmlPullParser parser, String fontDir, Map<String, File> updatableFontMap) throws XmlPullParserException, IOException {
        List<FontConfig.NamedFamilyList> families = new ArrayList<>();
        List<FontConfig.Alias> aliases = new ArrayList<>();
        parser.require(2, null, "fonts-modification");
        while (parser.next() != 3) {
            if (parser.getEventType() == 2) {
                String tag = parser.getName();
                if (tag.equals("family")) {
                    readFamily(parser, fontDir, families, updatableFontMap);
                } else if (tag.equals("family-list")) {
                    readFamilyList(parser, fontDir, families, updatableFontMap);
                } else if (tag.equals("alias")) {
                    aliases.add(FontListParser.readAlias(parser));
                } else {
                    FontListParser.skip(parser);
                }
            }
        }
        return validateAndTransformToResult(families, aliases);
    }

    private static void readFamily(XmlPullParser parser, String fontDir, List<FontConfig.NamedFamilyList> out, Map<String, File> updatableFontMap) throws XmlPullParserException, IOException {
        String customizationType = parser.getAttributeValue(null, "customizationType");
        if (customizationType == null) {
            throw new IllegalArgumentException("customizationType must be specified");
        }
        if (customizationType.equals("new-named-family")) {
            FontConfig.NamedFamilyList fontFamily = FontListParser.readNamedFamily(parser, fontDir, updatableFontMap, false);
            if (fontFamily != null) {
                out.add(fontFamily);
                return;
            }
            return;
        }
        throw new IllegalArgumentException("Unknown customizationType=" + customizationType);
    }

    private static void readFamilyList(XmlPullParser parser, String fontDir, List<FontConfig.NamedFamilyList> out, Map<String, File> updatableFontMap) throws XmlPullParserException, IOException {
        String customizationType = parser.getAttributeValue(null, "customizationType");
        if (customizationType == null) {
            throw new IllegalArgumentException("customizationType must be specified");
        }
        if (customizationType.equals("new-named-family")) {
            FontConfig.NamedFamilyList fontFamily = FontListParser.readNamedFamilyList(parser, fontDir, updatableFontMap, false);
            if (fontFamily != null) {
                out.add(fontFamily);
                return;
            }
            return;
        }
        throw new IllegalArgumentException("Unknown customizationType=" + customizationType);
    }
}
