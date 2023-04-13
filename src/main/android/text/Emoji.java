package android.text;

import android.icu.lang.UCharacter;
/* loaded from: classes3.dex */
public class Emoji {
    public static int COMBINING_ENCLOSING_KEYCAP = 8419;
    public static int ZERO_WIDTH_JOINER = 8205;
    public static int VARIATION_SELECTOR_16 = 65039;
    public static int CANCEL_TAG = 917631;

    public static boolean isRegionalIndicatorSymbol(int codePoint) {
        return 127462 <= codePoint && codePoint <= 127487;
    }

    public static boolean isEmojiModifier(int codePoint) {
        return UCharacter.hasBinaryProperty(codePoint, 59);
    }

    public static boolean isEmojiModifierBase(int c) {
        if (c == 129309 || c == 129340) {
            return true;
        }
        return UCharacter.hasBinaryProperty(c, 60);
    }

    public static boolean isEmoji(int codePoint) {
        return UCharacter.hasBinaryProperty(codePoint, 57);
    }

    public static boolean isKeycapBase(int codePoint) {
        return (48 <= codePoint && codePoint <= 57) || codePoint == 35 || codePoint == 42;
    }

    public static boolean isTagSpecChar(int codePoint) {
        return 917536 <= codePoint && codePoint <= 917630;
    }
}
