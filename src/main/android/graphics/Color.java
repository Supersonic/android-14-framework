package android.graphics;

import android.graphics.ColorSpace;
import android.hardware.Camera;
import android.util.Half;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.function.DoubleUnaryOperator;
/* loaded from: classes.dex */
public class Color {
    public static final int BLACK = -16777216;
    public static final int BLUE = -16776961;
    public static final int CYAN = -16711681;
    public static final int DKGRAY = -12303292;
    public static final int GRAY = -7829368;
    public static final int GREEN = -16711936;
    public static final int LTGRAY = -3355444;
    public static final int MAGENTA = -65281;
    public static final int RED = -65536;
    public static final int TRANSPARENT = 0;
    public static final int WHITE = -1;
    public static final int YELLOW = -256;
    private static final HashMap<String, Integer> sColorNameMap;
    private final ColorSpace mColorSpace;
    private final float[] mComponents;

    private static native int nativeHSVToColor(int i, float[] fArr);

    private static native void nativeRGBToHSV(int i, int i2, int i3, float[] fArr);

    public Color() {
        this.mComponents = new float[]{0.0f, 0.0f, 0.0f, 1.0f};
        this.mColorSpace = ColorSpace.get(ColorSpace.Named.SRGB);
    }

    private Color(float r, float g, float b, float a) {
        this(r, g, b, a, ColorSpace.get(ColorSpace.Named.SRGB));
    }

    private Color(float r, float g, float b, float a, ColorSpace colorSpace) {
        this.mComponents = new float[]{r, g, b, a};
        this.mColorSpace = colorSpace;
    }

    private Color(float[] components, ColorSpace colorSpace) {
        this.mComponents = components;
        this.mColorSpace = colorSpace;
    }

    public ColorSpace getColorSpace() {
        return this.mColorSpace;
    }

    public ColorSpace.Model getModel() {
        return this.mColorSpace.getModel();
    }

    public boolean isWideGamut() {
        return getColorSpace().isWideGamut();
    }

    public boolean isSrgb() {
        return getColorSpace().isSrgb();
    }

    public int getComponentCount() {
        return this.mColorSpace.getComponentCount() + 1;
    }

    public long pack() {
        float[] fArr = this.mComponents;
        return pack(fArr[0], fArr[1], fArr[2], fArr[3], this.mColorSpace);
    }

    public Color convert(ColorSpace colorSpace) {
        ColorSpace.Connector connector = ColorSpace.connect(this.mColorSpace, colorSpace);
        float[] fArr = this.mComponents;
        float[] color = {fArr[0], fArr[1], fArr[2], fArr[3]};
        connector.transform(color);
        return new Color(color, colorSpace);
    }

    public int toArgb() {
        if (this.mColorSpace.isSrgb()) {
            float[] fArr = this.mComponents;
            return ((int) ((fArr[2] * 255.0f) + 0.5f)) | (((int) ((fArr[3] * 255.0f) + 0.5f)) << 24) | (((int) ((fArr[0] * 255.0f) + 0.5f)) << 16) | (((int) ((fArr[1] * 255.0f) + 0.5f)) << 8);
        }
        float[] fArr2 = this.mComponents;
        float[] color = {fArr2[0], fArr2[1], fArr2[2], fArr2[3]};
        ColorSpace.connect(this.mColorSpace).transform(color);
        return (((int) ((color[3] * 255.0f) + 0.5f)) << 24) | (((int) ((color[0] * 255.0f) + 0.5f)) << 16) | (((int) ((color[1] * 255.0f) + 0.5f)) << 8) | ((int) ((color[2] * 255.0f) + 0.5f));
    }

    public float red() {
        return this.mComponents[0];
    }

    public float green() {
        return this.mComponents[1];
    }

    public float blue() {
        return this.mComponents[2];
    }

    public float alpha() {
        float[] fArr = this.mComponents;
        return fArr[fArr.length - 1];
    }

    public float[] getComponents() {
        float[] fArr = this.mComponents;
        return Arrays.copyOf(fArr, fArr.length);
    }

    public float[] getComponents(float[] components) {
        if (components == null) {
            float[] fArr = this.mComponents;
            return Arrays.copyOf(fArr, fArr.length);
        }
        int length = components.length;
        float[] fArr2 = this.mComponents;
        if (length < fArr2.length) {
            throw new IllegalArgumentException("The specified array's length must be at least " + this.mComponents.length);
        }
        System.arraycopy(fArr2, 0, components, 0, fArr2.length);
        return components;
    }

    public float getComponent(int component) {
        return this.mComponents[component];
    }

    public float luminance() {
        if (this.mColorSpace.getModel() != ColorSpace.Model.RGB) {
            throw new IllegalArgumentException("The specified color must be encoded in an RGB color space. The supplied color space is " + this.mColorSpace.getModel());
        }
        DoubleUnaryOperator eotf = ((ColorSpace.Rgb) this.mColorSpace).getEotf();
        double r = eotf.applyAsDouble(this.mComponents[0]);
        double g = eotf.applyAsDouble(this.mComponents[1]);
        double b = eotf.applyAsDouble(this.mComponents[2]);
        return saturate((float) ((0.2126d * r) + (0.7152d * g) + (0.0722d * b)));
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Color color = (Color) o;
        if (!Arrays.equals(this.mComponents, color.mComponents)) {
            return false;
        }
        return this.mColorSpace.equals(color.mColorSpace);
    }

    public int hashCode() {
        int result = Arrays.hashCode(this.mComponents);
        return (result * 31) + this.mColorSpace.hashCode();
    }

    public String toString() {
        float[] fArr;
        StringBuilder b = new StringBuilder("Color(");
        for (float c : this.mComponents) {
            b.append(c).append(", ");
        }
        b.append(this.mColorSpace.getName());
        b.append(')');
        return b.toString();
    }

    public static ColorSpace colorSpace(long color) {
        return ColorSpace.get((int) (63 & color));
    }

    public static float red(long color) {
        return (63 & color) == 0 ? ((float) ((color >> 48) & 255)) / 255.0f : Half.toFloat((short) ((color >> 48) & 65535));
    }

    public static float green(long color) {
        return (63 & color) == 0 ? ((float) ((color >> 40) & 255)) / 255.0f : Half.toFloat((short) ((color >> 32) & 65535));
    }

    public static float blue(long color) {
        return (63 & color) == 0 ? ((float) ((color >> 32) & 255)) / 255.0f : Half.toFloat((short) ((color >> 16) & 65535));
    }

    public static float alpha(long color) {
        return (63 & color) == 0 ? ((float) ((color >> 56) & 255)) / 255.0f : ((float) ((color >> 6) & 1023)) / 1023.0f;
    }

    public static boolean isSrgb(long color) {
        return colorSpace(color).isSrgb();
    }

    public static boolean isWideGamut(long color) {
        return colorSpace(color).isWideGamut();
    }

    public static boolean isInColorSpace(long color, ColorSpace colorSpace) {
        return ((int) (63 & color)) == colorSpace.getId();
    }

    public static int toArgb(long color) {
        if ((63 & color) == 0) {
            return (int) (color >> 32);
        }
        float r = red(color);
        float g = green(color);
        float b = blue(color);
        float a = alpha(color);
        float[] c = ColorSpace.connect(colorSpace(color)).transform(r, g, b);
        return ((int) ((c[2] * 255.0f) + 0.5f)) | (((int) ((a * 255.0f) + 0.5f)) << 24) | (((int) ((c[0] * 255.0f) + 0.5f)) << 16) | (((int) ((c[1] * 255.0f) + 0.5f)) << 8);
    }

    public static Color valueOf(int color) {
        float r = ((color >> 16) & 255) / 255.0f;
        float g = ((color >> 8) & 255) / 255.0f;
        float b = (color & 255) / 255.0f;
        float a = ((color >> 24) & 255) / 255.0f;
        return new Color(r, g, b, a, ColorSpace.get(ColorSpace.Named.SRGB));
    }

    public static Color valueOf(long color) {
        return new Color(red(color), green(color), blue(color), alpha(color), colorSpace(color));
    }

    public static Color valueOf(float r, float g, float b) {
        return new Color(r, g, b, 1.0f);
    }

    public static Color valueOf(float r, float g, float b, float a) {
        return new Color(saturate(r), saturate(g), saturate(b), saturate(a));
    }

    public static Color valueOf(float r, float g, float b, float a, ColorSpace colorSpace) {
        if (colorSpace.getComponentCount() > 3) {
            throw new IllegalArgumentException("The specified color space must use a color model with at most 3 color components");
        }
        return new Color(r, g, b, a, colorSpace);
    }

    public static Color valueOf(float[] components, ColorSpace colorSpace) {
        if (components.length < colorSpace.getComponentCount() + 1) {
            throw new IllegalArgumentException("Received a component array of length " + components.length + " but the color model requires " + (colorSpace.getComponentCount() + 1) + " (including alpha)");
        }
        return new Color(Arrays.copyOf(components, colorSpace.getComponentCount() + 1), colorSpace);
    }

    public static long pack(int color) {
        return (color & 4294967295L) << 32;
    }

    public static long pack(float red, float green, float blue) {
        return pack(red, green, blue, 1.0f, ColorSpace.get(ColorSpace.Named.SRGB));
    }

    public static long pack(float red, float green, float blue, float alpha) {
        return pack(red, green, blue, alpha, ColorSpace.get(ColorSpace.Named.SRGB));
    }

    public static long pack(float red, float green, float blue, float alpha, ColorSpace colorSpace) {
        if (colorSpace.isSrgb()) {
            int i = (int) ((255.0f * blue) + 0.5f);
            int argb = i | (((int) ((red * 255.0f) + 0.5f)) << 16) | (((int) ((alpha * 255.0f) + 0.5f)) << 24) | (((int) ((green * 255.0f) + 0.5f)) << 8);
            return (argb & 4294967295L) << 32;
        }
        int id = colorSpace.getId();
        if (id == -1) {
            throw new IllegalArgumentException("Unknown color space, please use a color space returned by ColorSpace.get()");
        }
        if (colorSpace.getComponentCount() > 3) {
            throw new IllegalArgumentException("The color space must use a color model with at most 3 components");
        }
        short r = Half.toHalf(red);
        short g = Half.toHalf(green);
        short b = Half.toHalf(blue);
        int a = (int) ((Math.max(0.0f, Math.min(alpha, 1.0f)) * 1023.0f) + 0.5f);
        return ((65535 & b) << 16) | ((r & 65535) << 48) | ((g & 65535) << 32) | ((a & 1023) << 6) | (id & 63);
    }

    public static long convert(int color, ColorSpace colorSpace) {
        float r = ((color >> 16) & 255) / 255.0f;
        float g = ((color >> 8) & 255) / 255.0f;
        float b = (color & 255) / 255.0f;
        float a = ((color >> 24) & 255) / 255.0f;
        ColorSpace source = ColorSpace.get(ColorSpace.Named.SRGB);
        return convert(r, g, b, a, source, colorSpace);
    }

    public static long convert(long color, ColorSpace colorSpace) {
        float r = red(color);
        float g = green(color);
        float b = blue(color);
        float a = alpha(color);
        ColorSpace source = colorSpace(color);
        return convert(r, g, b, a, source, colorSpace);
    }

    public static long convert(float r, float g, float b, float a, ColorSpace source, ColorSpace destination) {
        float[] c = ColorSpace.connect(source, destination).transform(r, g, b);
        return pack(c[0], c[1], c[2], a, destination);
    }

    public static long convert(long color, ColorSpace.Connector connector) {
        float r = red(color);
        float g = green(color);
        float b = blue(color);
        float a = alpha(color);
        return convert(r, g, b, a, connector);
    }

    public static long convert(float r, float g, float b, float a, ColorSpace.Connector connector) {
        float[] c = connector.transform(r, g, b);
        return pack(c[0], c[1], c[2], a, connector.getDestination());
    }

    public static float luminance(long color) {
        ColorSpace colorSpace = colorSpace(color);
        if (colorSpace.getModel() != ColorSpace.Model.RGB) {
            throw new IllegalArgumentException("The specified color must be encoded in an RGB color space. The supplied color space is " + colorSpace.getModel());
        }
        DoubleUnaryOperator eotf = ((ColorSpace.Rgb) colorSpace).getEotf();
        double r = eotf.applyAsDouble(red(color));
        double g = eotf.applyAsDouble(green(color));
        double b = eotf.applyAsDouble(blue(color));
        return saturate((float) ((0.2126d * r) + (0.7152d * g) + (0.0722d * b)));
    }

    private static float saturate(float v) {
        if (v <= 0.0f) {
            return 0.0f;
        }
        if (v >= 1.0f) {
            return 1.0f;
        }
        return v;
    }

    public static int alpha(int color) {
        return color >>> 24;
    }

    public static int red(int color) {
        return (color >> 16) & 255;
    }

    public static int green(int color) {
        return (color >> 8) & 255;
    }

    public static int blue(int color) {
        return color & 255;
    }

    public static int rgb(int red, int green, int blue) {
        return (red << 16) | (-16777216) | (green << 8) | blue;
    }

    public static int rgb(float red, float green, float blue) {
        return ((int) ((255.0f * blue) + 0.5f)) | (((int) ((red * 255.0f) + 0.5f)) << 16) | (-16777216) | (((int) ((green * 255.0f) + 0.5f)) << 8);
    }

    public static int argb(int alpha, int red, int green, int blue) {
        return (alpha << 24) | (red << 16) | (green << 8) | blue;
    }

    public static int argb(float alpha, float red, float green, float blue) {
        return ((int) ((255.0f * blue) + 0.5f)) | (((int) ((alpha * 255.0f) + 0.5f)) << 24) | (((int) ((red * 255.0f) + 0.5f)) << 16) | (((int) ((green * 255.0f) + 0.5f)) << 8);
    }

    public static float luminance(int color) {
        ColorSpace.Rgb cs = (ColorSpace.Rgb) ColorSpace.get(ColorSpace.Named.SRGB);
        DoubleUnaryOperator eotf = cs.getEotf();
        double r = eotf.applyAsDouble(red(color) / 255.0d);
        double g = eotf.applyAsDouble(green(color) / 255.0d);
        double b = eotf.applyAsDouble(blue(color) / 255.0d);
        return (float) ((0.2126d * r) + (0.7152d * g) + (0.0722d * b));
    }

    public static int parseColor(String colorString) {
        if (colorString.charAt(0) == '#') {
            long color = Long.parseLong(colorString.substring(1), 16);
            if (colorString.length() == 7) {
                color |= -16777216;
            } else if (colorString.length() != 9) {
                throw new IllegalArgumentException("Unknown color");
            }
            return (int) color;
        }
        Integer color2 = sColorNameMap.get(colorString.toLowerCase(Locale.ROOT));
        if (color2 != null) {
            return color2.intValue();
        }
        throw new IllegalArgumentException("Unknown color");
    }

    public static void RGBToHSV(int red, int green, int blue, float[] hsv) {
        if (hsv.length < 3) {
            throw new RuntimeException("3 components required for hsv");
        }
        nativeRGBToHSV(red, green, blue, hsv);
    }

    public static void colorToHSV(int color, float[] hsv) {
        RGBToHSV((color >> 16) & 255, (color >> 8) & 255, color & 255, hsv);
    }

    public static int HSVToColor(float[] hsv) {
        return HSVToColor(255, hsv);
    }

    public static int HSVToColor(int alpha, float[] hsv) {
        if (hsv.length < 3) {
            throw new RuntimeException("3 components required for hsv");
        }
        return nativeHSVToColor(alpha, hsv);
    }

    static {
        HashMap<String, Integer> hashMap = new HashMap<>();
        sColorNameMap = hashMap;
        hashMap.put("black", -16777216);
        Integer valueOf = Integer.valueOf((int) DKGRAY);
        hashMap.put("darkgray", valueOf);
        Integer valueOf2 = Integer.valueOf((int) GRAY);
        hashMap.put("gray", valueOf2);
        Integer valueOf3 = Integer.valueOf((int) LTGRAY);
        hashMap.put("lightgray", valueOf3);
        hashMap.put("white", -1);
        hashMap.put("red", -65536);
        Integer valueOf4 = Integer.valueOf((int) GREEN);
        hashMap.put("green", valueOf4);
        hashMap.put("blue", -16776961);
        hashMap.put("yellow", -256);
        Integer valueOf5 = Integer.valueOf((int) CYAN);
        hashMap.put("cyan", valueOf5);
        Integer valueOf6 = Integer.valueOf((int) MAGENTA);
        hashMap.put("magenta", valueOf6);
        hashMap.put(Camera.Parameters.EFFECT_AQUA, valueOf5);
        hashMap.put("fuchsia", valueOf6);
        hashMap.put("darkgrey", valueOf);
        hashMap.put("grey", valueOf2);
        hashMap.put("lightgrey", valueOf3);
        hashMap.put("lime", valueOf4);
        hashMap.put("maroon", -8388608);
        hashMap.put("navy", -16777088);
        hashMap.put("olive", -8355840);
        hashMap.put("purple", -8388480);
        hashMap.put("silver", -4144960);
        hashMap.put("teal", -16744320);
    }
}
