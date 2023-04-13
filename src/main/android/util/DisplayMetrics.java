package android.util;

import android.content.res.FontScaleConverter;
import android.p008os.SystemProperties;
/* loaded from: classes3.dex */
public class DisplayMetrics {
    public static final int DENSITY_140 = 140;
    public static final int DENSITY_180 = 180;
    public static final int DENSITY_200 = 200;
    public static final int DENSITY_220 = 220;
    public static final int DENSITY_260 = 260;
    public static final int DENSITY_280 = 280;
    public static final int DENSITY_300 = 300;
    public static final int DENSITY_340 = 340;
    public static final int DENSITY_360 = 360;
    public static final int DENSITY_400 = 400;
    public static final int DENSITY_420 = 420;
    public static final int DENSITY_440 = 440;
    public static final int DENSITY_450 = 450;
    public static final int DENSITY_520 = 520;
    public static final int DENSITY_560 = 560;
    public static final int DENSITY_600 = 600;
    public static final int DENSITY_DEFAULT = 160;
    public static final float DENSITY_DEFAULT_SCALE = 0.00625f;
    @Deprecated
    public static int DENSITY_DEVICE = getDeviceDensity();
    public static final int DENSITY_DEVICE_STABLE = getDeviceDensity();
    public static final int DENSITY_HIGH = 240;
    public static final int DENSITY_LOW = 120;
    public static final int DENSITY_MEDIUM = 160;
    public static final int DENSITY_TV = 213;
    public static final int DENSITY_XHIGH = 320;
    public static final int DENSITY_XXHIGH = 480;
    public static final int DENSITY_XXXHIGH = 640;
    public float density;
    public int densityDpi;
    public FontScaleConverter fontScaleConverter;
    public int heightPixels;
    public float noncompatDensity;
    public int noncompatDensityDpi;
    public int noncompatHeightPixels;
    public float noncompatScaledDensity;
    public int noncompatWidthPixels;
    public float noncompatXdpi;
    public float noncompatYdpi;
    @Deprecated
    public float scaledDensity;
    public int widthPixels;
    public float xdpi;
    public float ydpi;

    public void setTo(DisplayMetrics o) {
        if (this == o) {
            return;
        }
        this.widthPixels = o.widthPixels;
        this.heightPixels = o.heightPixels;
        this.density = o.density;
        this.densityDpi = o.densityDpi;
        this.scaledDensity = o.scaledDensity;
        this.xdpi = o.xdpi;
        this.ydpi = o.ydpi;
        this.noncompatWidthPixels = o.noncompatWidthPixels;
        this.noncompatHeightPixels = o.noncompatHeightPixels;
        this.noncompatDensity = o.noncompatDensity;
        this.noncompatDensityDpi = o.noncompatDensityDpi;
        this.noncompatScaledDensity = o.noncompatScaledDensity;
        this.noncompatXdpi = o.noncompatXdpi;
        this.noncompatYdpi = o.noncompatYdpi;
        this.fontScaleConverter = o.fontScaleConverter;
    }

    public void setToDefaults() {
        this.widthPixels = 0;
        this.heightPixels = 0;
        int i = DENSITY_DEVICE;
        float f = i / 160.0f;
        this.density = f;
        this.densityDpi = i;
        this.scaledDensity = f;
        float f2 = i;
        this.xdpi = f2;
        float f3 = i;
        this.ydpi = f3;
        this.noncompatWidthPixels = 0;
        this.noncompatHeightPixels = 0;
        this.noncompatDensity = f;
        this.noncompatDensityDpi = i;
        this.noncompatScaledDensity = f;
        this.noncompatXdpi = f2;
        this.noncompatYdpi = f3;
        this.fontScaleConverter = null;
    }

    public boolean equals(Object o) {
        return (o instanceof DisplayMetrics) && equals((DisplayMetrics) o);
    }

    public boolean equals(DisplayMetrics other) {
        return equalsPhysical(other) && this.scaledDensity == other.scaledDensity && this.noncompatScaledDensity == other.noncompatScaledDensity;
    }

    public boolean equalsPhysical(DisplayMetrics other) {
        return other != null && this.widthPixels == other.widthPixels && this.heightPixels == other.heightPixels && this.density == other.density && this.densityDpi == other.densityDpi && this.xdpi == other.xdpi && this.ydpi == other.ydpi && this.noncompatWidthPixels == other.noncompatWidthPixels && this.noncompatHeightPixels == other.noncompatHeightPixels && this.noncompatDensity == other.noncompatDensity && this.noncompatDensityDpi == other.noncompatDensityDpi && this.noncompatXdpi == other.noncompatXdpi && this.noncompatYdpi == other.noncompatYdpi;
    }

    public int hashCode() {
        return this.widthPixels * this.heightPixels * this.densityDpi;
    }

    public String toString() {
        return "DisplayMetrics{density=" + this.density + ", width=" + this.widthPixels + ", height=" + this.heightPixels + ", scaledDensity=" + this.scaledDensity + ", xdpi=" + this.xdpi + ", ydpi=" + this.ydpi + "}";
    }

    private static int getDeviceDensity() {
        return SystemProperties.getInt("qemu.sf.lcd_density", SystemProperties.getInt("ro.sf.lcd_density", 160));
    }
}
