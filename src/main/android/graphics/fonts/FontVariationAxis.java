package android.graphics.fonts;

import android.text.TextUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
/* loaded from: classes.dex */
public final class FontVariationAxis {
    private final float mStyleValue;
    private final int mTag;
    private final String mTagString;
    private static final Pattern TAG_PATTERN = Pattern.compile("[ -~]{4}");
    private static final Pattern STYLE_VALUE_PATTERN = Pattern.compile("-?(([0-9]+(\\.[0-9]+)?)|(\\.[0-9]+))");

    public FontVariationAxis(String tagString, float styleValue) {
        if (!isValidTag(tagString)) {
            throw new IllegalArgumentException("Illegal tag pattern: " + tagString);
        }
        this.mTag = makeTag(tagString);
        this.mTagString = tagString;
        this.mStyleValue = styleValue;
    }

    public int getOpenTypeTagValue() {
        return this.mTag;
    }

    public String getTag() {
        return this.mTagString;
    }

    public float getStyleValue() {
        return this.mStyleValue;
    }

    public String toString() {
        return "'" + this.mTagString + "' " + Float.toString(this.mStyleValue);
    }

    private static boolean isValidTag(String tagString) {
        return tagString != null && TAG_PATTERN.matcher(tagString).matches();
    }

    private static boolean isValidValueFormat(String valueString) {
        return valueString != null && STYLE_VALUE_PATTERN.matcher(valueString).matches();
    }

    public static int makeTag(String tagString) {
        char c1 = tagString.charAt(0);
        char c2 = tagString.charAt(1);
        char c3 = tagString.charAt(2);
        char c4 = tagString.charAt(3);
        return (c1 << 24) | (c2 << 16) | (c3 << '\b') | c4;
    }

    public static FontVariationAxis[] fromFontVariationSettings(String settings) {
        if (settings == null || settings.isEmpty()) {
            return null;
        }
        ArrayList<FontVariationAxis> axisList = new ArrayList<>();
        int length = settings.length();
        int i = 0;
        while (i < length) {
            char c = settings.charAt(i);
            if (!Character.isWhitespace(c)) {
                if ((c != '\'' && c != '\"') || length < i + 6 || settings.charAt(i + 5) != c) {
                    throw new IllegalArgumentException("Tag should be wrapped with double or single quote: " + settings);
                }
                String tagString = settings.substring(i + 1, i + 5);
                int i2 = i + 6;
                int endOfValueString = settings.indexOf(44, i2);
                if (endOfValueString == -1) {
                    endOfValueString = length;
                }
                try {
                    float value = Float.parseFloat(settings.substring(i2, endOfValueString));
                    axisList.add(new FontVariationAxis(tagString, value));
                    i = endOfValueString;
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Failed to parse float string: " + e.getMessage());
                }
            }
            i++;
        }
        if (axisList.isEmpty()) {
            return null;
        }
        return (FontVariationAxis[]) axisList.toArray(new FontVariationAxis[0]);
    }

    public static String toFontVariationSettings(FontVariationAxis[] axes) {
        if (axes == null) {
            return "";
        }
        return TextUtils.join(",", axes);
    }

    public static String toFontVariationSettings(List<FontVariationAxis> axes) {
        if (axes == null) {
            return "";
        }
        return TextUtils.join(",", axes);
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (o == null || !(o instanceof FontVariationAxis)) {
            return false;
        }
        FontVariationAxis axis = (FontVariationAxis) o;
        if (axis.mTag == this.mTag && axis.mStyleValue == this.mStyleValue) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(Integer.valueOf(this.mTag), Float.valueOf(this.mStyleValue));
    }
}
