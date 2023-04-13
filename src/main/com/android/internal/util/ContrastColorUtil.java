package com.android.internal.util;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.graphics.drawable.VectorDrawable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.text.style.TextAppearanceSpan;
import android.util.Log;
import android.util.Pair;
import com.android.internal.C4057R;
import java.util.Arrays;
import java.util.WeakHashMap;
/* loaded from: classes3.dex */
public class ContrastColorUtil {
    private static final boolean DEBUG = false;
    private static final String TAG = "ContrastColorUtil";
    private static ContrastColorUtil sInstance;
    private static final Object sLock = new Object();
    private final int mGrayscaleIconMaxSize;
    private final ImageUtils mImageUtils = new ImageUtils();
    private final WeakHashMap<Bitmap, Pair<Boolean, Integer>> mGrayscaleBitmapCache = new WeakHashMap<>();

    public static ContrastColorUtil getInstance(Context context) {
        ContrastColorUtil contrastColorUtil;
        synchronized (sLock) {
            if (sInstance == null) {
                sInstance = new ContrastColorUtil(context);
            }
            contrastColorUtil = sInstance;
        }
        return contrastColorUtil;
    }

    private ContrastColorUtil(Context context) {
        this.mGrayscaleIconMaxSize = context.getResources().getDimensionPixelSize(C4057R.dimen.notification_grayscale_icon_max_size);
    }

    public boolean isGrayscaleIcon(Bitmap bitmap) {
        boolean result;
        int generationId;
        if (bitmap.getWidth() > this.mGrayscaleIconMaxSize || bitmap.getHeight() > this.mGrayscaleIconMaxSize) {
            return false;
        }
        Object obj = sLock;
        synchronized (obj) {
            Pair<Boolean, Integer> cached = this.mGrayscaleBitmapCache.get(bitmap);
            if (cached != null && cached.second.intValue() == bitmap.getGenerationId()) {
                return cached.first.booleanValue();
            }
            synchronized (this.mImageUtils) {
                result = this.mImageUtils.isGrayscale(bitmap);
                generationId = bitmap.getGenerationId();
            }
            synchronized (obj) {
                this.mGrayscaleBitmapCache.put(bitmap, Pair.create(Boolean.valueOf(result), Integer.valueOf(generationId)));
            }
            return result;
        }
    }

    public boolean isGrayscaleIcon(Drawable d) {
        if (d == null) {
            return false;
        }
        if (d instanceof BitmapDrawable) {
            BitmapDrawable bd = (BitmapDrawable) d;
            return bd.getBitmap() != null && isGrayscaleIcon(bd.getBitmap());
        } else if (!(d instanceof AnimationDrawable)) {
            return d instanceof VectorDrawable;
        } else {
            AnimationDrawable ad = (AnimationDrawable) d;
            int count = ad.getNumberOfFrames();
            return count > 0 && isGrayscaleIcon(ad.getFrame(0));
        }
    }

    public boolean isGrayscaleIcon(Context context, Icon icon) {
        if (icon == null) {
            return false;
        }
        switch (icon.getType()) {
            case 1:
                return isGrayscaleIcon(icon.getBitmap());
            case 2:
                return isGrayscaleIcon(context, icon.getResId());
            default:
                return false;
        }
    }

    public boolean isGrayscaleIcon(Context context, int drawableResId) {
        if (drawableResId == 0) {
            return false;
        }
        try {
            return isGrayscaleIcon(context.getDrawable(drawableResId));
        } catch (Resources.NotFoundException e) {
            Log.m110e(TAG, "Drawable not found: " + drawableResId);
            return false;
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    public CharSequence invertCharSequenceColors(CharSequence charSequence) {
        Object resultSpan;
        if (charSequence instanceof Spanned) {
            Spanned ss = (Spanned) charSequence;
            Object[] spans = ss.getSpans(0, ss.length(), Object.class);
            SpannableStringBuilder builder = new SpannableStringBuilder(ss.toString());
            for (Object span : spans) {
                Object resultSpan2 = span;
                if (resultSpan2 instanceof CharacterStyle) {
                    resultSpan2 = ((CharacterStyle) span).getUnderlying();
                }
                if (resultSpan2 instanceof TextAppearanceSpan) {
                    Object processedSpan = processTextAppearanceSpan((TextAppearanceSpan) span);
                    if (processedSpan != resultSpan2) {
                        resultSpan = processedSpan;
                    } else {
                        resultSpan = span;
                    }
                } else if (resultSpan2 instanceof ForegroundColorSpan) {
                    ForegroundColorSpan originalSpan = (ForegroundColorSpan) resultSpan2;
                    int foregroundColor = originalSpan.getForegroundColor();
                    resultSpan = new ForegroundColorSpan(processColor(foregroundColor));
                } else {
                    resultSpan = span;
                }
                builder.setSpan(resultSpan, ss.getSpanStart(span), ss.getSpanEnd(span), ss.getSpanFlags(span));
            }
            return builder;
        }
        return charSequence;
    }

    private TextAppearanceSpan processTextAppearanceSpan(TextAppearanceSpan span) {
        ColorStateList colorStateList = span.getTextColor();
        if (colorStateList != null) {
            int[] colors = colorStateList.getColors();
            boolean changed = false;
            for (int i = 0; i < colors.length; i++) {
                if (ImageUtils.isGrayscale(colors[i])) {
                    if (!changed) {
                        colors = Arrays.copyOf(colors, colors.length);
                    }
                    colors[i] = processColor(colors[i]);
                    changed = true;
                }
            }
            if (changed) {
                return new TextAppearanceSpan(span.getFamily(), span.getTextStyle(), span.getTextSize(), new ColorStateList(colorStateList.getStates(), colors), span.getLinkTextColor());
            }
        }
        return span;
    }

    public static CharSequence clearColorSpans(CharSequence charSequence) {
        if (charSequence instanceof Spanned) {
            Spanned ss = (Spanned) charSequence;
            Object[] spans = ss.getSpans(0, ss.length(), Object.class);
            SpannableStringBuilder builder = new SpannableStringBuilder(ss.toString());
            for (Object span : spans) {
                Object resultSpan = span;
                if (resultSpan instanceof CharacterStyle) {
                    resultSpan = ((CharacterStyle) span).getUnderlying();
                }
                if (!(resultSpan instanceof TextAppearanceSpan)) {
                    if (!(resultSpan instanceof ForegroundColorSpan) && !(resultSpan instanceof BackgroundColorSpan)) {
                        resultSpan = span;
                    }
                } else {
                    TextAppearanceSpan originalSpan = (TextAppearanceSpan) resultSpan;
                    if (originalSpan.getTextColor() != null) {
                        resultSpan = new TextAppearanceSpan(originalSpan.getFamily(), originalSpan.getTextStyle(), originalSpan.getTextSize(), null, originalSpan.getLinkTextColor());
                    }
                }
                builder.setSpan(resultSpan, ss.getSpanStart(span), ss.getSpanEnd(span), ss.getSpanFlags(span));
            }
            return builder;
        }
        return charSequence;
    }

    private int processColor(int color) {
        return Color.argb(Color.alpha(color), 255 - Color.red(color), 255 - Color.green(color), 255 - Color.blue(color));
    }

    public static int findContrastColor(int color, int other, boolean findFg, double minRatio) {
        int fg = findFg ? color : other;
        int bg = findFg ? other : color;
        if (ColorUtilsFromCompat.calculateContrast(fg, bg) >= minRatio) {
            return color;
        }
        double[] lab = new double[3];
        ColorUtilsFromCompat.colorToLAB(findFg ? fg : bg, lab);
        double low = 0.0d;
        double high = lab[0];
        double a = lab[1];
        double b = lab[2];
        for (int i = 0; i < 15 && high - low > 1.0E-5d; i++) {
            double l = (low + high) / 2.0d;
            if (findFg) {
                fg = ColorUtilsFromCompat.LABToColor(l, a, b);
            } else {
                bg = ColorUtilsFromCompat.LABToColor(l, a, b);
            }
            if (ColorUtilsFromCompat.calculateContrast(fg, bg) > minRatio) {
                low = l;
            } else {
                high = l;
            }
        }
        return ColorUtilsFromCompat.LABToColor(low, a, b);
    }

    public static int findAlphaToMeetContrast(int color, int backgroundColor, double minRatio) {
        if (ColorUtilsFromCompat.calculateContrast(color, backgroundColor) >= minRatio) {
            return color;
        }
        int startAlpha = Color.alpha(color);
        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);
        int low = startAlpha;
        int high = 255;
        for (int i = 0; i < 15 && high - low > 0; i++) {
            int alpha = (low + high) / 2;
            int fg = Color.argb(alpha, r, g, b);
            if (ColorUtilsFromCompat.calculateContrast(fg, backgroundColor) > minRatio) {
                high = alpha;
            } else {
                low = alpha;
            }
        }
        int i2 = Color.argb(high, r, g, b);
        return i2;
    }

    public static int findContrastColorAgainstDark(int color, int other, boolean findFg, double minRatio) {
        int fg = findFg ? color : other;
        int bg = findFg ? other : color;
        if (ColorUtilsFromCompat.calculateContrast(fg, bg) >= minRatio) {
            return color;
        }
        float[] hsl = new float[3];
        ColorUtilsFromCompat.colorToHSL(findFg ? fg : bg, hsl);
        float low = hsl[2];
        float high = 1.0f;
        for (int i = 0; i < 15 && high - low > 1.0E-5d; i++) {
            float l = (low + high) / 2.0f;
            hsl[2] = l;
            if (findFg) {
                fg = ColorUtilsFromCompat.HSLToColor(hsl);
            } else {
                bg = ColorUtilsFromCompat.HSLToColor(hsl);
            }
            if (ColorUtilsFromCompat.calculateContrast(fg, bg) > minRatio) {
                high = l;
            } else {
                low = l;
            }
        }
        hsl[2] = high;
        return ColorUtilsFromCompat.HSLToColor(hsl);
    }

    public static int ensureTextContrastOnBlack(int color) {
        return findContrastColorAgainstDark(color, -16777216, true, 12.0d);
    }

    public static int ensureLargeTextContrast(int color, int bg, boolean isBgDarker) {
        if (isBgDarker) {
            return findContrastColorAgainstDark(color, bg, true, 3.0d);
        }
        return findContrastColor(color, bg, true, 3.0d);
    }

    public static int ensureTextContrast(int color, int bg, boolean isBgDarker) {
        return ensureContrast(color, bg, isBgDarker, 4.5d);
    }

    public static int ensureContrast(int color, int bg, boolean isBgDarker, double minRatio) {
        if (isBgDarker) {
            return findContrastColorAgainstDark(color, bg, true, minRatio);
        }
        return findContrastColor(color, bg, true, minRatio);
    }

    public static int ensureTextBackgroundColor(int color, int textColor, int hintColor) {
        return findContrastColor(findContrastColor(color, hintColor, false, 3.0d), textColor, false, 4.5d);
    }

    private static String contrastChange(int colorOld, int colorNew, int bg) {
        return String.format("from %.2f:1 to %.2f:1", Double.valueOf(ColorUtilsFromCompat.calculateContrast(colorOld, bg)), Double.valueOf(ColorUtilsFromCompat.calculateContrast(colorNew, bg)));
    }

    public static int resolveColor(Context context, int color, boolean defaultBackgroundIsDark) {
        int res;
        if (color == 0) {
            if (defaultBackgroundIsDark) {
                res = C4057R.color.notification_default_color_dark;
            } else {
                res = C4057R.color.notification_default_color_light;
            }
            return context.getColor(res);
        }
        return color;
    }

    public static int resolveContrastColor(Context context, int notificationColor, int backgroundColor) {
        return resolveContrastColor(context, notificationColor, backgroundColor, false);
    }

    public static int resolveContrastColor(Context context, int notificationColor, int backgroundColor, boolean isDark) {
        int resolvedColor = resolveColor(context, notificationColor, isDark);
        int color = ensureTextContrast(resolvedColor, backgroundColor, isDark);
        return color;
    }

    public static int changeColorLightness(int baseColor, int amount) {
        double[] result = ColorUtilsFromCompat.getTempDouble3Array();
        ColorUtilsFromCompat.colorToLAB(baseColor, result);
        result[0] = Math.max(Math.min(100.0d, result[0] + amount), 0.0d);
        return ColorUtilsFromCompat.LABToColor(result[0], result[1], result[2]);
    }

    public static int resolvePrimaryColor(Context context, int backgroundColor, boolean defaultBackgroundIsDark) {
        boolean useDark = shouldUseDark(backgroundColor, defaultBackgroundIsDark);
        if (useDark) {
            return context.getColor(C4057R.color.notification_primary_text_color_light);
        }
        return context.getColor(C4057R.color.notification_primary_text_color_dark);
    }

    public static int resolveSecondaryColor(Context context, int backgroundColor, boolean defaultBackgroundIsDark) {
        boolean useDark = shouldUseDark(backgroundColor, defaultBackgroundIsDark);
        if (useDark) {
            return context.getColor(C4057R.color.notification_secondary_text_color_light);
        }
        return context.getColor(C4057R.color.notification_secondary_text_color_dark);
    }

    public static int resolveDefaultColor(Context context, int backgroundColor, boolean defaultBackgroundIsDark) {
        boolean useDark = shouldUseDark(backgroundColor, defaultBackgroundIsDark);
        if (useDark) {
            return context.getColor(C4057R.color.notification_default_color_light);
        }
        return context.getColor(C4057R.color.notification_default_color_dark);
    }

    public static int getShiftedColor(int color, int amount) {
        double[] result = ColorUtilsFromCompat.getTempDouble3Array();
        ColorUtilsFromCompat.colorToLAB(color, result);
        if (result[0] >= 4.0d) {
            result[0] = Math.max(0.0d, result[0] - amount);
        } else {
            result[0] = Math.min(100.0d, result[0] + amount);
        }
        return ColorUtilsFromCompat.LABToColor(result[0], result[1], result[2]);
    }

    public static int getMutedColor(int color, float alpha) {
        int whiteScrim = ColorUtilsFromCompat.setAlphaComponent(-1, (int) (255.0f * alpha));
        return compositeColors(whiteScrim, color);
    }

    private static boolean shouldUseDark(int backgroundColor, boolean defaultBackgroundIsDark) {
        if (backgroundColor == 0) {
            return !defaultBackgroundIsDark;
        }
        return ColorUtilsFromCompat.calculateLuminance(backgroundColor) > 0.17912878474d;
    }

    public static double calculateLuminance(int backgroundColor) {
        return ColorUtilsFromCompat.calculateLuminance(backgroundColor);
    }

    public static double calculateContrast(int foregroundColor, int backgroundColor) {
        return ColorUtilsFromCompat.calculateContrast(foregroundColor, backgroundColor);
    }

    public static boolean satisfiesTextContrast(int backgroundColor, int foregroundColor) {
        return calculateContrast(foregroundColor, backgroundColor) >= 4.5d;
    }

    public static int compositeColors(int foreground, int background) {
        return ColorUtilsFromCompat.compositeColors(foreground, background);
    }

    public static boolean isColorLight(int backgroundColor) {
        return calculateLuminance(backgroundColor) > 0.5d;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes3.dex */
    public static class ColorUtilsFromCompat {
        private static final int MIN_ALPHA_SEARCH_MAX_ITERATIONS = 10;
        private static final int MIN_ALPHA_SEARCH_PRECISION = 1;
        private static final ThreadLocal<double[]> TEMP_ARRAY = new ThreadLocal<>();
        private static final double XYZ_EPSILON = 0.008856d;
        private static final double XYZ_KAPPA = 903.3d;
        private static final double XYZ_WHITE_REFERENCE_X = 95.047d;
        private static final double XYZ_WHITE_REFERENCE_Y = 100.0d;
        private static final double XYZ_WHITE_REFERENCE_Z = 108.883d;

        private ColorUtilsFromCompat() {
        }

        public static int compositeColors(int foreground, int background) {
            int bgAlpha = Color.alpha(background);
            int fgAlpha = Color.alpha(foreground);
            int a = compositeAlpha(fgAlpha, bgAlpha);
            int r = compositeComponent(Color.red(foreground), fgAlpha, Color.red(background), bgAlpha, a);
            int g = compositeComponent(Color.green(foreground), fgAlpha, Color.green(background), bgAlpha, a);
            int b = compositeComponent(Color.blue(foreground), fgAlpha, Color.blue(background), bgAlpha, a);
            return Color.argb(a, r, g, b);
        }

        private static int compositeAlpha(int foregroundAlpha, int backgroundAlpha) {
            return 255 - (((255 - backgroundAlpha) * (255 - foregroundAlpha)) / 255);
        }

        private static int compositeComponent(int fgC, int fgA, int bgC, int bgA, int a) {
            if (a == 0) {
                return 0;
            }
            return (((fgC * 255) * fgA) + ((bgC * bgA) * (255 - fgA))) / (a * 255);
        }

        public static int setAlphaComponent(int color, int alpha) {
            if (alpha < 0 || alpha > 255) {
                throw new IllegalArgumentException("alpha must be between 0 and 255.");
            }
            return (16777215 & color) | (alpha << 24);
        }

        public static double calculateLuminance(int color) {
            double[] result = getTempDouble3Array();
            colorToXYZ(color, result);
            return result[1] / XYZ_WHITE_REFERENCE_Y;
        }

        public static double calculateContrast(int foreground, int background) {
            if (Color.alpha(background) != 255) {
                Log.wtf(ContrastColorUtil.TAG, "background can not be translucent: #" + Integer.toHexString(background));
            }
            if (Color.alpha(foreground) < 255) {
                foreground = compositeColors(foreground, background);
            }
            double luminance1 = calculateLuminance(foreground) + 0.05d;
            double luminance2 = calculateLuminance(background) + 0.05d;
            return Math.max(luminance1, luminance2) / Math.min(luminance1, luminance2);
        }

        public static void colorToLAB(int color, double[] outLab) {
            RGBToLAB(Color.red(color), Color.green(color), Color.blue(color), outLab);
        }

        public static void RGBToLAB(int r, int g, int b, double[] outLab) {
            RGBToXYZ(r, g, b, outLab);
            XYZToLAB(outLab[0], outLab[1], outLab[2], outLab);
        }

        public static void colorToXYZ(int color, double[] outXyz) {
            RGBToXYZ(Color.red(color), Color.green(color), Color.blue(color), outXyz);
        }

        public static void RGBToXYZ(int r, int g, int b, double[] outXyz) {
            if (outXyz.length != 3) {
                throw new IllegalArgumentException("outXyz must have a length of 3.");
            }
            double sr = r / 255.0d;
            double sr2 = sr < 0.04045d ? sr / 12.92d : Math.pow((sr + 0.055d) / 1.055d, 2.4d);
            double sg = g / 255.0d;
            double sg2 = sg < 0.04045d ? sg / 12.92d : Math.pow((sg + 0.055d) / 1.055d, 2.4d);
            double sb = b / 255.0d;
            double sb2 = sb < 0.04045d ? sb / 12.92d : Math.pow((0.055d + sb) / 1.055d, 2.4d);
            outXyz[0] = ((0.4124d * sr2) + (0.3576d * sg2) + (0.1805d * sb2)) * XYZ_WHITE_REFERENCE_Y;
            outXyz[1] = ((0.2126d * sr2) + (0.7152d * sg2) + (0.0722d * sb2)) * XYZ_WHITE_REFERENCE_Y;
            outXyz[2] = ((0.0193d * sr2) + (0.1192d * sg2) + (0.9505d * sb2)) * XYZ_WHITE_REFERENCE_Y;
        }

        public static void XYZToLAB(double x, double y, double z, double[] outLab) {
            if (outLab.length != 3) {
                throw new IllegalArgumentException("outLab must have a length of 3.");
            }
            double x2 = pivotXyzComponent(x / XYZ_WHITE_REFERENCE_X);
            double y2 = pivotXyzComponent(y / XYZ_WHITE_REFERENCE_Y);
            double z2 = pivotXyzComponent(z / XYZ_WHITE_REFERENCE_Z);
            outLab[0] = Math.max(0.0d, (116.0d * y2) - 16.0d);
            outLab[1] = (x2 - y2) * 500.0d;
            outLab[2] = (y2 - z2) * 200.0d;
        }

        public static void LABToXYZ(double l, double a, double b, double[] outXyz) {
            double fy = (l + 16.0d) / 116.0d;
            double fx = (a / 500.0d) + fy;
            double fz = fy - (b / 200.0d);
            double tmp = Math.pow(fx, 3.0d);
            double xr = tmp > XYZ_EPSILON ? tmp : ((fx * 116.0d) - 16.0d) / XYZ_KAPPA;
            double yr = l > 7.9996247999999985d ? Math.pow(fy, 3.0d) : l / XYZ_KAPPA;
            double tmp2 = Math.pow(fz, 3.0d);
            double zr = tmp2 > XYZ_EPSILON ? tmp2 : ((116.0d * fz) - 16.0d) / XYZ_KAPPA;
            outXyz[0] = XYZ_WHITE_REFERENCE_X * xr;
            outXyz[1] = XYZ_WHITE_REFERENCE_Y * yr;
            outXyz[2] = XYZ_WHITE_REFERENCE_Z * zr;
        }

        public static int XYZToColor(double x, double y, double z) {
            double r = (((3.2406d * x) + ((-1.5372d) * y)) + ((-0.4986d) * z)) / XYZ_WHITE_REFERENCE_Y;
            double g = ((((-0.9689d) * x) + (1.8758d * y)) + (0.0415d * z)) / XYZ_WHITE_REFERENCE_Y;
            double b = (((0.0557d * x) + ((-0.204d) * y)) + (1.057d * z)) / XYZ_WHITE_REFERENCE_Y;
            return Color.rgb(constrain((int) Math.round((r > 0.0031308d ? (Math.pow(r, 0.4166666666666667d) * 1.055d) - 0.055d : r * 12.92d) * 255.0d), 0, 255), constrain((int) Math.round((g > 0.0031308d ? (Math.pow(g, 0.4166666666666667d) * 1.055d) - 0.055d : g * 12.92d) * 255.0d), 0, 255), constrain((int) Math.round(255.0d * (b > 0.0031308d ? (Math.pow(b, 0.4166666666666667d) * 1.055d) - 0.055d : b * 12.92d)), 0, 255));
        }

        public static int LABToColor(double l, double a, double b) {
            double[] result = getTempDouble3Array();
            LABToXYZ(l, a, b, result);
            return XYZToColor(result[0], result[1], result[2]);
        }

        private static int constrain(int amount, int low, int high) {
            return amount < low ? low : amount > high ? high : amount;
        }

        private static float constrain(float amount, float low, float high) {
            return amount < low ? low : amount > high ? high : amount;
        }

        private static double pivotXyzComponent(double component) {
            if (component > XYZ_EPSILON) {
                return Math.pow(component, 0.3333333333333333d);
            }
            return ((XYZ_KAPPA * component) + 16.0d) / 116.0d;
        }

        public static double[] getTempDouble3Array() {
            ThreadLocal<double[]> threadLocal = TEMP_ARRAY;
            double[] result = threadLocal.get();
            if (result == null) {
                double[] result2 = new double[3];
                threadLocal.set(result2);
                return result2;
            }
            return result;
        }

        public static int HSLToColor(float[] hsl) {
            float h = hsl[0];
            float s = hsl[1];
            float l = hsl[2];
            float c = (1.0f - Math.abs((l * 2.0f) - 1.0f)) * s;
            float m = l - (0.5f * c);
            float x = (1.0f - Math.abs(((h / 60.0f) % 2.0f) - 1.0f)) * c;
            int hueSegment = ((int) h) / 60;
            int r = 0;
            int g = 0;
            int b = 0;
            switch (hueSegment) {
                case 0:
                    r = Math.round((c + m) * 255.0f);
                    g = Math.round((x + m) * 255.0f);
                    b = Math.round(255.0f * m);
                    break;
                case 1:
                    r = Math.round((x + m) * 255.0f);
                    g = Math.round((c + m) * 255.0f);
                    b = Math.round(255.0f * m);
                    break;
                case 2:
                    r = Math.round(m * 255.0f);
                    g = Math.round((c + m) * 255.0f);
                    b = Math.round((x + m) * 255.0f);
                    break;
                case 3:
                    r = Math.round(m * 255.0f);
                    g = Math.round((x + m) * 255.0f);
                    b = Math.round((c + m) * 255.0f);
                    break;
                case 4:
                    r = Math.round((x + m) * 255.0f);
                    g = Math.round(m * 255.0f);
                    b = Math.round((c + m) * 255.0f);
                    break;
                case 5:
                case 6:
                    r = Math.round((c + m) * 255.0f);
                    g = Math.round(m * 255.0f);
                    b = Math.round((x + m) * 255.0f);
                    break;
            }
            return Color.rgb(constrain(r, 0, 255), constrain(g, 0, 255), constrain(b, 0, 255));
        }

        public static void colorToHSL(int color, float[] outHsl) {
            RGBToHSL(Color.red(color), Color.green(color), Color.blue(color), outHsl);
        }

        public static void RGBToHSL(int r, int g, int b, float[] outHsl) {
            float h;
            float s;
            float rf = r / 255.0f;
            float gf = g / 255.0f;
            float bf = b / 255.0f;
            float max = Math.max(rf, Math.max(gf, bf));
            float min = Math.min(rf, Math.min(gf, bf));
            float deltaMaxMin = max - min;
            float l = (max + min) / 2.0f;
            if (max == min) {
                s = 0.0f;
                h = 0.0f;
            } else {
                if (max == rf) {
                    h = ((gf - bf) / deltaMaxMin) % 6.0f;
                } else if (max == gf) {
                    h = ((bf - rf) / deltaMaxMin) + 2.0f;
                } else {
                    float h2 = rf - gf;
                    h = (h2 / deltaMaxMin) + 4.0f;
                }
                s = deltaMaxMin / (1.0f - Math.abs((2.0f * l) - 1.0f));
            }
            float h3 = (60.0f * h) % 360.0f;
            if (h3 < 0.0f) {
                h3 += 360.0f;
            }
            outHsl[0] = constrain(h3, 0.0f, 360.0f);
            outHsl[1] = constrain(s, 0.0f, 1.0f);
            outHsl[2] = constrain(l, 0.0f, 1.0f);
        }
    }
}
