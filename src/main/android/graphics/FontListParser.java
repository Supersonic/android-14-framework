package android.graphics;

import android.graphics.fonts.FontCustomizationParser;
import android.graphics.fonts.FontStyle;
import android.graphics.fonts.FontVariationAxis;
import android.graphics.fonts.SystemFonts;
import android.p008os.LocaleList;
import android.text.FontConfig;
import android.util.ArraySet;
import android.util.Xml;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes.dex */
public class FontListParser {
    public static final String ATTR_FALLBACK_FOR = "fallbackFor";
    public static final String ATTR_INDEX = "index";
    private static final String ATTR_LANG = "lang";
    private static final String ATTR_NAME = "name";
    public static final String ATTR_POSTSCRIPT_NAME = "postScriptName";
    public static final String ATTR_STYLE = "style";
    public static final String ATTR_STYLEVALUE = "stylevalue";
    public static final String ATTR_TAG = "tag";
    private static final String ATTR_VARIANT = "variant";
    public static final String ATTR_WEIGHT = "weight";
    private static final Pattern FILENAME_WHITESPACE_PATTERN = Pattern.compile("^[ \\n\\r\\t]+|[ \\n\\r\\t]+$");
    public static final String STYLE_ITALIC = "italic";
    public static final String STYLE_NORMAL = "normal";
    private static final String TAG = "FontListParser";
    public static final String TAG_AXIS = "axis";
    private static final String TAG_FONT = "font";
    private static final String VARIANT_COMPACT = "compact";
    private static final String VARIANT_ELEGANT = "elegant";

    public static FontConfig parse(InputStream in) throws XmlPullParserException, IOException {
        XmlPullParser parser = Xml.newPullParser();
        parser.setInput(in, null);
        parser.nextTag();
        return readFamilies(parser, SystemFonts.SYSTEM_FONT_DIR, new FontCustomizationParser.Result(), null, 0L, 0, true);
    }

    public static FontConfig parse(String fontsXmlPath, String systemFontDir, String oemCustomizationXmlPath, String productFontDir, Map<String, File> updatableFontMap, long lastModifiedDate, int configVersion) throws IOException, XmlPullParserException {
        FontCustomizationParser.Result oemCustomization;
        InputStream is;
        InputStream is2;
        if (oemCustomizationXmlPath != null) {
            try {
                is = new FileInputStream(oemCustomizationXmlPath);
            } catch (IOException e) {
            }
            try {
                FontCustomizationParser.Result oemCustomization2 = FontCustomizationParser.parse(is, productFontDir, updatableFontMap);
                is.close();
                oemCustomization = oemCustomization2;
            } catch (IOException e2) {
                oemCustomization = new FontCustomizationParser.Result();
                is2 = new FileInputStream(fontsXmlPath);
                XmlPullParser parser = Xml.newPullParser();
                parser.setInput(is2, null);
                parser.nextTag();
                FontConfig readFamilies = readFamilies(parser, systemFontDir, oemCustomization, updatableFontMap, lastModifiedDate, configVersion, false);
                is2.close();
                return readFamilies;
            }
        } else {
            oemCustomization = new FontCustomizationParser.Result();
        }
        is2 = new FileInputStream(fontsXmlPath);
        try {
            XmlPullParser parser2 = Xml.newPullParser();
            parser2.setInput(is2, null);
            parser2.nextTag();
            FontConfig readFamilies2 = readFamilies(parser2, systemFontDir, oemCustomization, updatableFontMap, lastModifiedDate, configVersion, false);
            is2.close();
            return readFamilies2;
        } catch (Throwable th) {
            try {
                is2.close();
            } catch (Throwable th2) {
                th.addSuppressed(th2);
            }
            throw th;
        }
    }

    /* JADX WARN: Incorrect condition in loop: B:4:0x002f */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public static FontConfig readFamilies(XmlPullParser parser, String fontDir, FontCustomizationParser.Result customization, Map<String, File> updatableFontMap, long lastModifiedDate, int configVersion, boolean allowNonExistingFile) throws XmlPullParserException, IOException {
        List<FontConfig.FontFamily> families = new ArrayList<>();
        List<FontConfig.NamedFamilyList> resultNamedFamilies = new ArrayList<>();
        List<FontConfig.Alias> aliases = new ArrayList<>(customization.getAdditionalAliases());
        Map<String, FontConfig.NamedFamilyList> oemNamedFamilies = customization.getAdditionalNamedFamilies();
        parser.require(2, null, "familyset");
        boolean firstFamily = true;
        while (firstFamily) {
            if (parser.getEventType() == 2) {
                String tag = parser.getName();
                if (tag.equals("family")) {
                    String name = parser.getAttributeValue(null, "name");
                    if (name == null) {
                        FontConfig.FontFamily family = readFamily(parser, fontDir, updatableFontMap, allowNonExistingFile);
                        if (family != null) {
                            families.add(family);
                            firstFamily = false;
                        }
                    } else {
                        FontConfig.NamedFamilyList namedFamilyList = readNamedFamily(parser, fontDir, updatableFontMap, allowNonExistingFile);
                        if (namedFamilyList != null) {
                            if (!oemNamedFamilies.containsKey(name)) {
                                resultNamedFamilies.add(namedFamilyList);
                            }
                            if (firstFamily) {
                                families.addAll(namedFamilyList.getFamilies());
                            }
                            firstFamily = false;
                        }
                    }
                } else if (tag.equals("family-list")) {
                    FontConfig.NamedFamilyList namedFamilyList2 = readNamedFamilyList(parser, fontDir, updatableFontMap, allowNonExistingFile);
                    if (namedFamilyList2 != null) {
                        if (!oemNamedFamilies.containsKey(namedFamilyList2.getName())) {
                            resultNamedFamilies.add(namedFamilyList2);
                        }
                        if (firstFamily) {
                            families.addAll(namedFamilyList2.getFamilies());
                        }
                        firstFamily = false;
                    }
                } else if (tag.equals("alias")) {
                    aliases.add(readAlias(parser));
                } else {
                    skip(parser);
                }
            }
        }
        resultNamedFamilies.addAll(oemNamedFamilies.values());
        Set<String> namedFamilies = new ArraySet<>();
        for (int i = 0; i < resultNamedFamilies.size(); i++) {
            String name2 = resultNamedFamilies.get(i).getName();
            if (name2 != null) {
                namedFamilies.add(name2);
            }
        }
        List<FontConfig.Alias> filtered = new ArrayList<>();
        for (int i2 = 0; i2 < aliases.size(); i2++) {
            FontConfig.Alias alias = aliases.get(i2);
            if (namedFamilies.contains(alias.getOriginal())) {
                filtered.add(alias);
            }
        }
        return new FontConfig(families, filtered, resultNamedFamilies, lastModifiedDate, configVersion);
    }

    private static boolean keepReading(XmlPullParser parser) throws XmlPullParserException, IOException {
        int next = parser.next();
        return (next == 3 || next == 1) ? false : true;
    }

    public static FontConfig.FontFamily readFamily(XmlPullParser parser, String fontDir, Map<String, File> updatableFontMap, boolean allowNonExistingFile) throws XmlPullParserException, IOException {
        String lang = parser.getAttributeValue("", ATTR_LANG);
        String variant = parser.getAttributeValue(null, "variant");
        String ignore = parser.getAttributeValue(null, "ignore");
        List<FontConfig.Font> fonts = new ArrayList<>();
        while (keepReading(parser)) {
            if (parser.getEventType() == 2) {
                String tag = parser.getName();
                if (tag.equals("font")) {
                    FontConfig.Font font = readFont(parser, fontDir, updatableFontMap, allowNonExistingFile);
                    if (font != null) {
                        fonts.add(font);
                    }
                } else {
                    skip(parser);
                }
            }
        }
        int intVariant = 0;
        if (variant != null) {
            if (variant.equals(VARIANT_COMPACT)) {
                intVariant = 1;
            } else if (variant.equals(VARIANT_ELEGANT)) {
                intVariant = 2;
            }
        }
        boolean skip = ignore != null && (ignore.equals("true") || ignore.equals("1"));
        if (skip || fonts.isEmpty()) {
            return null;
        }
        return new FontConfig.FontFamily(fonts, LocaleList.forLanguageTags(lang), intVariant);
    }

    private static void throwIfAttributeExists(String attrName, XmlPullParser parser) {
        if (parser.getAttributeValue(null, attrName) != null) {
            throw new IllegalArgumentException(attrName + " cannot be used in FontFamily inside  family or family-list with name attribute.");
        }
    }

    public static FontConfig.NamedFamilyList readNamedFamily(XmlPullParser parser, String fontDir, Map<String, File> updatableFontMap, boolean allowNonExistingFile) throws XmlPullParserException, IOException {
        String name = parser.getAttributeValue(null, "name");
        throwIfAttributeExists(ATTR_LANG, parser);
        throwIfAttributeExists("variant", parser);
        throwIfAttributeExists("ignore", parser);
        FontConfig.FontFamily family = readFamily(parser, fontDir, updatableFontMap, allowNonExistingFile);
        if (family == null) {
            return null;
        }
        return new FontConfig.NamedFamilyList(Collections.singletonList(family), name);
    }

    public static FontConfig.NamedFamilyList readNamedFamilyList(XmlPullParser parser, String fontDir, Map<String, File> updatableFontMap, boolean allowNonExistingFile) throws XmlPullParserException, IOException {
        String name = parser.getAttributeValue(null, "name");
        List<FontConfig.FontFamily> familyList = new ArrayList<>();
        while (keepReading(parser)) {
            if (parser.getEventType() == 2) {
                String tag = parser.getName();
                if (tag.equals("family")) {
                    throwIfAttributeExists("name", parser);
                    throwIfAttributeExists(ATTR_LANG, parser);
                    throwIfAttributeExists("variant", parser);
                    throwIfAttributeExists("ignore", parser);
                    FontConfig.FontFamily family = readFamily(parser, fontDir, updatableFontMap, allowNonExistingFile);
                    if (family != null) {
                        familyList.add(family);
                    }
                } else {
                    skip(parser);
                }
            }
        }
        if (familyList.isEmpty()) {
            return null;
        }
        return new FontConfig.NamedFamilyList(familyList, name);
    }

    private static FontConfig.Font readFont(XmlPullParser parser, String fontDir, Map<String, File> updatableFontMap, boolean allowNonExistingFile) throws XmlPullParserException, IOException {
        String postScriptName;
        String filePath;
        String originalPath;
        String varSettings;
        String indexStr = parser.getAttributeValue(null, ATTR_INDEX);
        int index = indexStr == null ? 0 : Integer.parseInt(indexStr);
        List<FontVariationAxis> axes = new ArrayList<>();
        String weightStr = parser.getAttributeValue(null, "weight");
        int weight = weightStr == null ? 400 : Integer.parseInt(weightStr);
        boolean equals = STYLE_ITALIC.equals(parser.getAttributeValue(null, "style"));
        String fallbackFor = parser.getAttributeValue(null, ATTR_FALLBACK_FOR);
        String postScriptName2 = parser.getAttributeValue(null, ATTR_POSTSCRIPT_NAME);
        StringBuilder filename = new StringBuilder();
        while (keepReading(parser)) {
            if (parser.getEventType() == 4) {
                filename.append(parser.getText());
            }
            if (parser.getEventType() == 2) {
                String tag = parser.getName();
                if (tag.equals(TAG_AXIS)) {
                    axes.add(readAxis(parser));
                } else {
                    skip(parser);
                }
            }
        }
        String sanitizedName = FILENAME_WHITESPACE_PATTERN.matcher(filename).replaceAll("");
        if (postScriptName2 == null) {
            postScriptName = sanitizedName.substring(0, sanitizedName.length() - 4);
        } else {
            postScriptName = postScriptName2;
        }
        String updatedName = findUpdatedFontFile(postScriptName, updatableFontMap);
        if (updatedName != null) {
            filePath = updatedName;
            originalPath = fontDir + sanitizedName;
        } else {
            filePath = fontDir + sanitizedName;
            originalPath = null;
        }
        if (axes.isEmpty()) {
            varSettings = "";
        } else {
            varSettings = FontVariationAxis.toFontVariationSettings((FontVariationAxis[]) axes.toArray(new FontVariationAxis[0]));
        }
        File file = new File(filePath);
        String originalPath2 = originalPath;
        if (!allowNonExistingFile && !file.isFile()) {
            return null;
        }
        return new FontConfig.Font(file, originalPath2 != null ? new File(originalPath2) : null, postScriptName, new FontStyle(weight, equals ? 1 : 0), index, varSettings, fallbackFor);
    }

    private static String findUpdatedFontFile(String psName, Map<String, File> updatableFontMap) {
        File updatedFile;
        if (updatableFontMap != null && (updatedFile = updatableFontMap.get(psName)) != null) {
            return updatedFile.getAbsolutePath();
        }
        return null;
    }

    private static FontVariationAxis readAxis(XmlPullParser parser) throws XmlPullParserException, IOException {
        String tagStr = parser.getAttributeValue(null, "tag");
        String styleValueStr = parser.getAttributeValue(null, ATTR_STYLEVALUE);
        skip(parser);
        return new FontVariationAxis(tagStr, Float.parseFloat(styleValueStr));
    }

    public static FontConfig.Alias readAlias(XmlPullParser parser) throws XmlPullParserException, IOException {
        int weight;
        String name = parser.getAttributeValue(null, "name");
        String toName = parser.getAttributeValue(null, "to");
        String weightStr = parser.getAttributeValue(null, "weight");
        if (weightStr == null) {
            weight = 400;
        } else {
            weight = Integer.parseInt(weightStr);
        }
        skip(parser);
        return new FontConfig.Alias(name, toName, weight);
    }

    public static void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        int depth = 1;
        while (depth > 0) {
            switch (parser.next()) {
                case 1:
                    return;
                case 2:
                    depth++;
                    break;
                case 3:
                    depth--;
                    break;
            }
        }
    }
}
