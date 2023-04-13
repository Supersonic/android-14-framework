package android.app;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.p008os.SystemProperties;
import android.util.MathUtils;
import android.util.Size;
import com.android.internal.graphics.ColorUtils;
import com.android.internal.graphics.cam.Cam;
import com.android.internal.graphics.palette.CelebiQuantizer;
import com.android.internal.graphics.palette.Palette;
import com.android.internal.graphics.palette.VariationalKMeansQuantizer;
import com.android.internal.util.ContrastColorUtil;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
/* loaded from: classes.dex */
public final class WallpaperColors implements Parcelable {
    private static final float DARK_PIXEL_CONTRAST = 5.5f;
    private static final float DARK_THEME_MEAN_LUMINANCE = 0.3f;
    private static final boolean DEBUG_DARK_PIXELS = false;
    public static final int HINT_FROM_BITMAP = 4;
    public static final int HINT_SUPPORTS_DARK_TEXT = 1;
    public static final int HINT_SUPPORTS_DARK_THEME = 2;
    private static final int MAX_BITMAP_SIZE = 112;
    private static final int MAX_WALLPAPER_EXTRACTION_AREA = 12544;
    private static final float MIN_COLOR_OCCURRENCE = 0.05f;
    private final Map<Integer, Integer> mAllColors;
    private int mColorHints;
    private final List<Color> mMainColors;
    private static final float BRIGHT_IMAGE_MEAN_LUMINANCE = SystemProperties.getInt("persist.wallpapercolors.threshold", 70) / 100.0f;
    private static final float MAX_DARK_AREA = SystemProperties.getInt("persist.wallpapercolors.max_dark_area", 5) / 100.0f;
    public static final Parcelable.Creator<WallpaperColors> CREATOR = new Parcelable.Creator<WallpaperColors>() { // from class: android.app.WallpaperColors.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public WallpaperColors createFromParcel(Parcel in) {
            return new WallpaperColors(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public WallpaperColors[] newArray(int size) {
            return new WallpaperColors[size];
        }
    };

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface ColorsHints {
    }

    public WallpaperColors(Parcel parcel) {
        this.mMainColors = new ArrayList();
        this.mAllColors = new HashMap();
        int count = parcel.readInt();
        for (int i = 0; i < count; i++) {
            int colorInt = parcel.readInt();
            Color color = Color.valueOf(colorInt);
            this.mMainColors.add(color);
        }
        int count2 = parcel.readInt();
        for (int i2 = 0; i2 < count2; i2++) {
            int colorInt2 = parcel.readInt();
            int population = parcel.readInt();
            this.mAllColors.put(Integer.valueOf(colorInt2), Integer.valueOf(population));
        }
        int i3 = parcel.readInt();
        this.mColorHints = i3;
    }

    public static WallpaperColors fromDrawable(Drawable drawable) {
        if (drawable == null) {
            throw new IllegalArgumentException("Drawable cannot be null");
        }
        Rect initialBounds = drawable.copyBounds();
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        if (width <= 0 || height <= 0) {
            width = 112;
            height = 112;
        }
        Size optimalSize = calculateOptimalSize(width, height);
        Bitmap bitmap = Bitmap.createBitmap(optimalSize.getWidth(), optimalSize.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas bmpCanvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, bitmap.getWidth(), bitmap.getHeight());
        drawable.draw(bmpCanvas);
        WallpaperColors colors = fromBitmap(bitmap);
        bitmap.recycle();
        drawable.setBounds(initialBounds);
        return colors;
    }

    public static WallpaperColors fromBitmap(Bitmap bitmap) {
        if (bitmap == null) {
            throw new IllegalArgumentException("Bitmap can't be null");
        }
        return fromBitmap(bitmap, 0.0f);
    }

    public static WallpaperColors fromBitmap(Bitmap bitmap, float dimAmount) {
        Palette palette;
        Objects.requireNonNull(bitmap, "Bitmap can't be null");
        int bitmapArea = bitmap.getWidth() * bitmap.getHeight();
        boolean shouldRecycle = false;
        if (bitmapArea > MAX_WALLPAPER_EXTRACTION_AREA) {
            shouldRecycle = true;
            Size optimalSize = calculateOptimalSize(bitmap.getWidth(), bitmap.getHeight());
            bitmap = Bitmap.createScaledBitmap(bitmap, optimalSize.getWidth(), optimalSize.getHeight(), false);
        }
        if (ActivityManager.isLowRamDeviceStatic()) {
            palette = Palette.from(bitmap, new VariationalKMeansQuantizer()).maximumColorCount(5).resizeBitmapArea(MAX_WALLPAPER_EXTRACTION_AREA).generate();
        } else {
            palette = Palette.from(bitmap, new CelebiQuantizer()).maximumColorCount(128).resizeBitmapArea(MAX_WALLPAPER_EXTRACTION_AREA).generate();
        }
        ArrayList<Palette.Swatch> swatches = new ArrayList<>(palette.getSwatches());
        swatches.sort(new Comparator() { // from class: android.app.WallpaperColors$$ExternalSyntheticLambda0
            @Override // java.util.Comparator
            public final int compare(Object obj, Object obj2) {
                return WallpaperColors.lambda$fromBitmap$0((Palette.Swatch) obj, (Palette.Swatch) obj2);
            }
        });
        int swatchesSize = swatches.size();
        Map<Integer, Integer> populationByColor = new HashMap<>();
        for (int i = 0; i < swatchesSize; i++) {
            Palette.Swatch swatch = swatches.get(i);
            int colorInt = swatch.getInt();
            populationByColor.put(Integer.valueOf(colorInt), Integer.valueOf(swatch.getPopulation()));
        }
        int hints = calculateDarkHints(bitmap, dimAmount);
        if (shouldRecycle) {
            bitmap.recycle();
        }
        return new WallpaperColors(populationByColor, hints | 4);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ int lambda$fromBitmap$0(Palette.Swatch a, Palette.Swatch b) {
        return b.getPopulation() - a.getPopulation();
    }

    public WallpaperColors(Color primaryColor, Color secondaryColor, Color tertiaryColor) {
        this(primaryColor, secondaryColor, tertiaryColor, 0);
        float[] tmpHsl = new float[3];
        ColorUtils.colorToHSL(primaryColor.toArgb(), tmpHsl);
        float luminance = tmpHsl[2];
        if (luminance < DARK_THEME_MEAN_LUMINANCE) {
            this.mColorHints = 2 | this.mColorHints;
        }
    }

    public WallpaperColors(Color primaryColor, Color secondaryColor, Color tertiaryColor, int colorHints) {
        if (primaryColor == null) {
            throw new IllegalArgumentException("Primary color should never be null.");
        }
        ArrayList arrayList = new ArrayList(3);
        this.mMainColors = arrayList;
        HashMap hashMap = new HashMap();
        this.mAllColors = hashMap;
        arrayList.add(primaryColor);
        hashMap.put(Integer.valueOf(primaryColor.toArgb()), 0);
        if (secondaryColor != null) {
            arrayList.add(secondaryColor);
            hashMap.put(Integer.valueOf(secondaryColor.toArgb()), 0);
        }
        if (tertiaryColor != null) {
            if (secondaryColor == null) {
                throw new IllegalArgumentException("tertiaryColor can't be specified when secondaryColor is null");
            }
            arrayList.add(tertiaryColor);
            hashMap.put(Integer.valueOf(tertiaryColor.toArgb()), 0);
        }
        this.mColorHints = colorHints;
    }

    public WallpaperColors(Map<Integer, Integer> colorToPopulation, int colorHints) {
        this.mAllColors = colorToPopulation;
        Map<Integer, Cam> colorToCam = new HashMap<>();
        for (Integer num : colorToPopulation.keySet()) {
            int color = num.intValue();
            colorToCam.put(Integer.valueOf(color), Cam.fromInt(color));
        }
        double[] hueProportions = hueProportions(colorToCam, colorToPopulation);
        Map<Integer, Double> colorToHueProportion = colorToHueProportion(colorToPopulation.keySet(), colorToCam, hueProportions);
        Map<Integer, Double> colorToScore = new HashMap<>();
        for (Map.Entry<Integer, Double> mapEntry : colorToHueProportion.entrySet()) {
            int color2 = mapEntry.getKey().intValue();
            double proportion = mapEntry.getValue().doubleValue();
            double score = score(colorToCam.get(Integer.valueOf(color2)), proportion);
            colorToScore.put(Integer.valueOf(color2), Double.valueOf(score));
        }
        ArrayList<Map.Entry<Integer, Double>> mapEntries = new ArrayList<>(colorToScore.entrySet());
        mapEntries.sort(new Comparator() { // from class: android.app.WallpaperColors$$ExternalSyntheticLambda1
            @Override // java.util.Comparator
            public final int compare(Object obj, Object obj2) {
                int compareTo;
                compareTo = ((Double) ((Map.Entry) obj2).getValue()).compareTo((Double) ((Map.Entry) obj).getValue());
                return compareTo;
            }
        });
        List<Integer> colorsByScoreDescending = new ArrayList<>();
        Iterator<Map.Entry<Integer, Double>> it = mapEntries.iterator();
        while (it.hasNext()) {
            Map.Entry<Integer, Double> colorToScoreEntry = it.next();
            colorsByScoreDescending.add(colorToScoreEntry.getKey());
        }
        List<Integer> mainColorInts = new ArrayList<>();
        for (Integer num2 : colorsByScoreDescending) {
            int color3 = num2.intValue();
            Cam cam = colorToCam.get(Integer.valueOf(color3));
            Iterator<Integer> it2 = mainColorInts.iterator();
            while (true) {
                if (it2.hasNext()) {
                    int otherColor = it2.next().intValue();
                    Cam otherCam = colorToCam.get(Integer.valueOf(otherColor));
                    if (hueDiff(cam, otherCam) < 15.0d) {
                        break;
                    }
                } else {
                    mainColorInts.add(Integer.valueOf(color3));
                    break;
                }
            }
        }
        List<Color> mainColors = new ArrayList<>();
        for (Integer num3 : mainColorInts) {
            int colorInt = num3.intValue();
            mainColors.add(Color.valueOf(colorInt));
        }
        this.mMainColors = mainColors;
        this.mColorHints = colorHints;
    }

    private static double hueDiff(Cam a, Cam b) {
        return 180.0f - Math.abs(Math.abs(a.getHue() - b.getHue()) - 180.0f);
    }

    private static double score(Cam cam, double proportion) {
        return cam.getChroma() + (100.0d * proportion);
    }

    private static Map<Integer, Double> colorToHueProportion(Set<Integer> colors, Map<Integer, Cam> colorToCam, double[] hueProportions) {
        Map<Integer, Double> colorToHueProportion = new HashMap<>();
        for (Integer num : colors) {
            int color = num.intValue();
            int hue = wrapDegrees(Math.round(colorToCam.get(Integer.valueOf(color)).getHue()));
            double proportion = 0.0d;
            for (int i = hue - 15; i < hue + 15; i++) {
                proportion += hueProportions[wrapDegrees(i)];
            }
            colorToHueProportion.put(Integer.valueOf(color), Double.valueOf(proportion));
        }
        return colorToHueProportion;
    }

    private static int wrapDegrees(int degrees) {
        if (degrees < 0) {
            return (degrees % 360) + 360;
        }
        if (degrees >= 360) {
            return degrees % 360;
        }
        return degrees;
    }

    private static double[] hueProportions(Map<Integer, Cam> colorToCam, Map<Integer, Integer> colorToPopulation) {
        double[] proportions = new double[360];
        double totalPopulation = 0.0d;
        for (Map.Entry<Integer, Integer> entry : colorToPopulation.entrySet()) {
            totalPopulation += entry.getValue().intValue();
        }
        for (Map.Entry<Integer, Integer> entry2 : colorToPopulation.entrySet()) {
            int color = entry2.getKey().intValue();
            int population = colorToPopulation.get(Integer.valueOf(color)).intValue();
            Cam cam = colorToCam.get(Integer.valueOf(color));
            int hue = wrapDegrees(Math.round(cam.getHue()));
            proportions[hue] = proportions[hue] + (population / totalPopulation);
        }
        return proportions;
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        List<Color> mainColors = getMainColors();
        int count = mainColors.size();
        dest.writeInt(count);
        for (int i = 0; i < count; i++) {
            Color color = mainColors.get(i);
            dest.writeInt(color.toArgb());
        }
        dest.writeInt(this.mAllColors.size());
        for (Map.Entry<Integer, Integer> colorEntry : this.mAllColors.entrySet()) {
            if (colorEntry.getKey() != null) {
                dest.writeInt(colorEntry.getKey().intValue());
                Integer population = colorEntry.getValue();
                int populationInt = population != null ? population.intValue() : 0;
                dest.writeInt(populationInt);
            }
        }
        dest.writeInt(this.mColorHints);
    }

    public Color getPrimaryColor() {
        return this.mMainColors.get(0);
    }

    public Color getSecondaryColor() {
        if (this.mMainColors.size() < 2) {
            return null;
        }
        return this.mMainColors.get(1);
    }

    public Color getTertiaryColor() {
        if (this.mMainColors.size() < 3) {
            return null;
        }
        return this.mMainColors.get(2);
    }

    public List<Color> getMainColors() {
        return Collections.unmodifiableList(this.mMainColors);
    }

    public Map<Integer, Integer> getAllColors() {
        return Collections.unmodifiableMap(this.mAllColors);
    }

    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        WallpaperColors other = (WallpaperColors) o;
        return this.mMainColors.equals(other.mMainColors) && this.mAllColors.equals(other.mAllColors) && this.mColorHints == other.mColorHints;
    }

    public int hashCode() {
        return (this.mMainColors.hashCode() * 31 * this.mAllColors.hashCode()) + this.mColorHints;
    }

    public int getColorHints() {
        return this.mColorHints;
    }

    private static int calculateDarkHints(Bitmap source, float dimAmount) {
        if (source == null) {
            return 0;
        }
        float dimAmount2 = MathUtils.saturate(dimAmount);
        int[] pixels = new int[source.getWidth() * source.getHeight()];
        double totalLuminance = 0.0d;
        int maxDarkPixels = (int) (pixels.length * MAX_DARK_AREA);
        int darkPixels = 0;
        source.getPixels(pixels, 0, source.getWidth(), 0, 0, source.getWidth(), source.getHeight());
        int dimmingLayerAlpha = (int) (255.0f * dimAmount2);
        int blackTransparent = ColorUtils.setAlphaComponent(-16777216, dimmingLayerAlpha);
        float[] tmpHsl = new float[3];
        for (int pixelColor : pixels) {
            ColorUtils.colorToHSL(pixelColor, tmpHsl);
            int alpha = Color.alpha(pixelColor);
            int compositeColors = ColorUtils.compositeColors(blackTransparent, pixelColor);
            double adjustedLuminance = ColorUtils.calculateLuminance(compositeColors);
            boolean satisfiesTextContrast = ContrastColorUtil.calculateContrast(pixelColor, -16777216) > 5.5d;
            if (!satisfiesTextContrast && alpha != 0) {
                darkPixels++;
            }
            totalLuminance += adjustedLuminance;
        }
        int hints = 0;
        double meanLuminance = totalLuminance / pixels.length;
        if (meanLuminance > BRIGHT_IMAGE_MEAN_LUMINANCE && darkPixels < maxDarkPixels) {
            hints = 0 | 1;
        }
        if (meanLuminance < 0.30000001192092896d) {
            return hints | 2;
        }
        return hints;
    }

    private static Size calculateOptimalSize(int width, int height) {
        int requestedArea = width * height;
        double scale = 1.0d;
        if (requestedArea > MAX_WALLPAPER_EXTRACTION_AREA) {
            scale = Math.sqrt(12544.0d / requestedArea);
        }
        int newWidth = (int) (width * scale);
        int newHeight = (int) (height * scale);
        if (newWidth == 0) {
            newWidth = 1;
        }
        if (newHeight == 0) {
            newHeight = 1;
        }
        return new Size(newWidth, newHeight);
    }

    public String toString() {
        StringBuilder colors = new StringBuilder();
        for (int i = 0; i < this.mMainColors.size(); i++) {
            colors.append(Integer.toHexString(this.mMainColors.get(i).toArgb())).append(" ");
        }
        return "[WallpaperColors: " + colors.toString() + "h: " + this.mColorHints + NavigationBarInflaterView.SIZE_MOD_END;
    }
}
