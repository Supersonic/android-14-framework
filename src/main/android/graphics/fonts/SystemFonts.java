package android.graphics.fonts;

import android.graphics.FontListParser;
import android.graphics.Typeface;
import android.graphics.fonts.Font;
import android.graphics.fonts.FontFamily;
import android.text.FontConfig;
import android.util.ArrayMap;
import android.util.Log;
import android.util.SparseIntArray;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes.dex */
public final class SystemFonts {
    private static final String FONTS_XML = "/system/etc/fonts.xml";
    private static final Object LOCK = new Object();
    public static final String OEM_FONT_DIR = "/product/fonts/";
    private static final String OEM_XML = "/product/etc/fonts_customization.xml";
    public static final String SYSTEM_FONT_DIR = "/system/fonts/";
    private static final String TAG = "SystemFonts";
    private static Set<Font> sAvailableFonts;

    private SystemFonts() {
    }

    public static Set<Font> getAvailableFonts() {
        Set<Font> set;
        synchronized (LOCK) {
            if (sAvailableFonts == null) {
                sAvailableFonts = Font.getAvailableFonts();
            }
            set = sAvailableFonts;
        }
        return set;
    }

    public static void resetAvailableFonts() {
        synchronized (LOCK) {
            sAvailableFonts = null;
        }
    }

    private static ByteBuffer mmap(String fullPath) {
        try {
            FileInputStream file = new FileInputStream(fullPath);
            FileChannel fileChannel = file.getChannel();
            long fontSize = fileChannel.size();
            MappedByteBuffer map = fileChannel.map(FileChannel.MapMode.READ_ONLY, 0L, fontSize);
            file.close();
            return map;
        } catch (IOException e) {
            return null;
        }
    }

    private static void pushFamilyToFallback(FontConfig.FontFamily xmlFamily, ArrayMap<String, NativeFamilyListSet> fallbackMap, Map<String, ByteBuffer> cache) {
        FontConfig.Font[] fonts;
        String languageTags = xmlFamily.getLocaleList().toLanguageTags();
        int variant = xmlFamily.getVariant();
        ArrayList<FontConfig.Font> defaultFonts = new ArrayList<>();
        ArrayMap<String, ArrayList<FontConfig.Font>> specificFallbackFonts = new ArrayMap<>();
        for (FontConfig.Font font : xmlFamily.getFonts()) {
            String fallbackName = font.getFontFamilyName();
            if (fallbackName == null) {
                defaultFonts.add(font);
            } else {
                ArrayList<FontConfig.Font> fallback = specificFallbackFonts.get(fallbackName);
                if (fallback == null) {
                    fallback = new ArrayList<>();
                    specificFallbackFonts.put(fallbackName, fallback);
                }
                fallback.add(font);
            }
        }
        FontFamily defaultFamily = defaultFonts.isEmpty() ? null : createFontFamily(defaultFonts, languageTags, variant, false, cache);
        for (int i = 0; i < fallbackMap.size(); i++) {
            String name = fallbackMap.keyAt(i);
            NativeFamilyListSet familyListSet = fallbackMap.valueAt(i);
            int identityHash = System.identityHashCode(xmlFamily);
            if (familyListSet.seenXmlFamilies.get(identityHash, -1) == -1) {
                familyListSet.seenXmlFamilies.append(identityHash, 1);
                ArrayList<FontConfig.Font> fallback2 = specificFallbackFonts.get(name);
                if (fallback2 == null) {
                    if (defaultFamily != null) {
                        familyListSet.familyList.add(defaultFamily);
                    }
                } else {
                    FontFamily family = createFontFamily(fallback2, languageTags, variant, false, cache);
                    if (family != null) {
                        familyListSet.familyList.add(family);
                    } else if (defaultFamily != null) {
                        familyListSet.familyList.add(defaultFamily);
                    }
                }
            }
        }
    }

    private static FontFamily createFontFamily(List<FontConfig.Font> fonts, String languageTags, int variant, boolean isDefaultFallback, Map<String, ByteBuffer> cache) {
        if (fonts.size() == 0) {
            return null;
        }
        FontFamily.Builder b = null;
        for (int i = 0; i < fonts.size(); i++) {
            FontConfig.Font fontConfig = fonts.get(i);
            String fullPath = fontConfig.getFile().getAbsolutePath();
            ByteBuffer buffer = cache.get(fullPath);
            try {
                if (buffer == null) {
                    if (cache.containsKey(fullPath)) {
                        continue;
                    } else {
                        buffer = mmap(fullPath);
                        cache.put(fullPath, buffer);
                        if (buffer == null) {
                            continue;
                        }
                    }
                }
                Font font = new Font.Builder(buffer, new File(fullPath), languageTags).setWeight(fontConfig.getStyle().getWeight()).setSlant(fontConfig.getStyle().getSlant()).setTtcIndex(fontConfig.getTtcIndex()).setFontVariationSettings(fontConfig.getFontVariationSettings()).build();
                if (b == null) {
                    b = new FontFamily.Builder(font);
                } else {
                    b.addFont(font);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        if (b == null) {
            return null;
        }
        return b.build(languageTags, variant, false, isDefaultFallback);
    }

    private static void appendNamedFamilyList(FontConfig.NamedFamilyList namedFamilyList, ArrayMap<String, ByteBuffer> bufferCache, ArrayMap<String, NativeFamilyListSet> fallbackListMap) {
        String familyName = namedFamilyList.getName();
        NativeFamilyListSet familyListSet = new NativeFamilyListSet();
        List<FontConfig.FontFamily> xmlFamilies = namedFamilyList.getFamilies();
        for (int i = 0; i < xmlFamilies.size(); i++) {
            FontConfig.FontFamily xmlFamily = xmlFamilies.get(i);
            FontFamily family = createFontFamily(xmlFamily.getFontList(), xmlFamily.getLocaleList().toLanguageTags(), xmlFamily.getVariant(), true, bufferCache);
            if (family == null) {
                return;
            }
            familyListSet.familyList.add(family);
            familyListSet.seenXmlFamilies.append(System.identityHashCode(xmlFamily), 1);
        }
        fallbackListMap.put(familyName, familyListSet);
    }

    public static FontConfig getSystemFontConfig(Map<String, File> updatableFontMap, long lastModifiedDate, int configVersion) {
        return getSystemFontConfigInternal(FONTS_XML, SYSTEM_FONT_DIR, OEM_XML, OEM_FONT_DIR, updatableFontMap, lastModifiedDate, configVersion);
    }

    public static FontConfig getSystemPreinstalledFontConfig() {
        return getSystemFontConfigInternal(FONTS_XML, SYSTEM_FONT_DIR, OEM_XML, OEM_FONT_DIR, null, 0L, 0);
    }

    static FontConfig getSystemFontConfigInternal(String fontsXml, String systemFontDir, String oemXml, String productFontDir, Map<String, File> updatableFontMap, long lastModifiedDate, int configVersion) {
        try {
            return FontListParser.parse(fontsXml, systemFontDir, oemXml, productFontDir, updatableFontMap, lastModifiedDate, configVersion);
        } catch (IOException e) {
            Log.m109e(TAG, "Failed to open/read system font configurations.", e);
            return new FontConfig(Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), 0L, 0);
        } catch (XmlPullParserException e2) {
            Log.m109e(TAG, "Failed to parse the system font configuration.", e2);
            return new FontConfig(Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), 0L, 0);
        }
    }

    public static Map<String, FontFamily[]> buildSystemFallback(FontConfig fontConfig) {
        return buildSystemFallback(fontConfig, new ArrayMap());
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static final class NativeFamilyListSet {
        public List<FontFamily> familyList;
        public SparseIntArray seenXmlFamilies;

        private NativeFamilyListSet() {
            this.familyList = new ArrayList();
            this.seenXmlFamilies = new SparseIntArray();
        }
    }

    public static Map<String, FontFamily[]> buildSystemFallback(FontConfig fontConfig, ArrayMap<String, ByteBuffer> outBufferCache) {
        ArrayMap<String, NativeFamilyListSet> fallbackListMap = new ArrayMap<>();
        List<FontConfig.NamedFamilyList> namedFamilies = fontConfig.getNamedFamilyLists();
        for (int i = 0; i < namedFamilies.size(); i++) {
            FontConfig.NamedFamilyList namedFamilyList = namedFamilies.get(i);
            appendNamedFamilyList(namedFamilyList, outBufferCache, fallbackListMap);
        }
        List<FontConfig.FontFamily> xmlFamilies = fontConfig.getFontFamilies();
        for (int i2 = 0; i2 < xmlFamilies.size(); i2++) {
            FontConfig.FontFamily xmlFamily = xmlFamilies.get(i2);
            pushFamilyToFallback(xmlFamily, fallbackListMap, outBufferCache);
        }
        Map<String, FontFamily[]> fallbackMap = new ArrayMap<>();
        for (int i3 = 0; i3 < fallbackListMap.size(); i3++) {
            String fallbackName = fallbackListMap.keyAt(i3);
            List<FontFamily> familyList = fallbackListMap.valueAt(i3).familyList;
            fallbackMap.put(fallbackName, (FontFamily[]) familyList.toArray(new FontFamily[0]));
        }
        return fallbackMap;
    }

    public static Map<String, Typeface> buildSystemTypefaces(FontConfig fontConfig, Map<String, FontFamily[]> fallbackMap) {
        ArrayMap<String, Typeface> result = new ArrayMap<>();
        Typeface.initSystemDefaultTypefaces(fallbackMap, fontConfig.getAliases(), result);
        return result;
    }
}
