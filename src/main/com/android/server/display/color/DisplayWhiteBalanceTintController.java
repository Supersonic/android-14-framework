package com.android.server.display.color;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.ColorSpace;
import android.hardware.display.ColorDisplayManager;
import android.hardware.display.DisplayManagerInternal;
import android.opengl.Matrix;
import android.util.Slog;
import android.view.SurfaceControl;
import com.android.internal.annotations.VisibleForTesting;
import java.io.PrintWriter;
/* loaded from: classes.dex */
public final class DisplayWhiteBalanceTintController extends TintController {
    public float[] mChromaticAdaptationMatrix;
    @VisibleForTesting
    int mCurrentColorTemperature;
    public float[] mCurrentColorTemperatureXYZ;
    @VisibleForTesting
    ColorSpace.Rgb mDisplayColorSpaceRGB;
    public final DisplayManagerInternal mDisplayManagerInternal;
    public Boolean mIsAvailable;
    public int mTemperatureDefault;
    @VisibleForTesting
    int mTemperatureMax;
    @VisibleForTesting
    int mTemperatureMin;
    public long mTransitionDuration;
    public final Object mLock = new Object();
    @VisibleForTesting
    float[] mDisplayNominalWhiteXYZ = new float[3];
    @VisibleForTesting
    boolean mSetUp = false;
    public final float[] mMatrixDisplayWhiteBalance = new float[16];
    public boolean mIsAllowed = true;

    @Override // com.android.server.display.color.TintController
    public int getLevel() {
        return 125;
    }

    public DisplayWhiteBalanceTintController(DisplayManagerInternal displayManagerInternal) {
        this.mDisplayManagerInternal = displayManagerInternal;
    }

    public void setUp(Context context, boolean z) {
        this.mSetUp = false;
        Resources resources = context.getResources();
        setAllowed(resources.getBoolean(17891616));
        ColorSpace.Rgb displayColorSpaceFromSurfaceControl = getDisplayColorSpaceFromSurfaceControl();
        if (displayColorSpaceFromSurfaceControl == null) {
            Slog.w("ColorDisplayService", "Failed to get display color space from SurfaceControl, trying res");
            displayColorSpaceFromSurfaceControl = getDisplayColorSpaceFromResources(resources);
            if (displayColorSpaceFromSurfaceControl == null) {
                Slog.e("ColorDisplayService", "Failed to get display color space from resources");
                return;
            }
        }
        if (!isColorMatrixValid(displayColorSpaceFromSurfaceControl.getTransform())) {
            Slog.e("ColorDisplayService", "Invalid display color space RGB-to-XYZ transform");
        } else if (!isColorMatrixValid(displayColorSpaceFromSurfaceControl.getInverseTransform())) {
            Slog.e("ColorDisplayService", "Invalid display color space XYZ-to-RGB transform");
        } else {
            String[] stringArray = resources.getStringArray(17236048);
            float[] fArr = new float[3];
            for (int i = 0; i < stringArray.length; i++) {
                fArr[i] = Float.parseFloat(stringArray[i]);
            }
            int integer = resources.getInteger(17694820);
            if (integer <= 0) {
                Slog.e("ColorDisplayService", "Display white balance minimum temperature must be greater than 0");
                return;
            }
            int integer2 = resources.getInteger(17694819);
            if (integer2 < integer) {
                Slog.e("ColorDisplayService", "Display white balance max temp must be greater or equal to min");
                return;
            }
            int integer3 = resources.getInteger(17694817);
            this.mTransitionDuration = resources.getInteger(17694824);
            synchronized (this.mLock) {
                this.mDisplayColorSpaceRGB = displayColorSpaceFromSurfaceControl;
                this.mDisplayNominalWhiteXYZ = fArr;
                this.mTemperatureMin = integer;
                this.mTemperatureMax = integer2;
                this.mTemperatureDefault = integer3;
                this.mSetUp = true;
            }
            setMatrix(integer3);
        }
    }

    @Override // com.android.server.display.color.TintController
    public float[] getMatrix() {
        return (this.mSetUp && isActivated()) ? this.mMatrixDisplayWhiteBalance : ColorDisplayService.MATRIX_IDENTITY;
    }

    public static float[] mul3x3(float[] fArr, float[] fArr2) {
        float f = fArr[3];
        float f2 = fArr2[1];
        float f3 = fArr[6];
        float f4 = fArr2[2];
        float f5 = fArr[1];
        float f6 = fArr2[0];
        float f7 = fArr[4];
        float f8 = fArr[7];
        float f9 = fArr[5];
        float f10 = fArr[8];
        float f11 = fArr[0];
        float f12 = fArr2[4];
        float f13 = (fArr2[3] * f11) + (f * f12);
        float f14 = fArr2[5];
        float f15 = fArr[1];
        float f16 = fArr2[3];
        float f17 = fArr[2];
        float f18 = f11 * fArr2[6];
        float f19 = fArr[3];
        float f20 = fArr2[7];
        float f21 = f18 + (f19 * f20);
        float f22 = fArr2[8];
        float f23 = fArr2[6];
        return new float[]{(fArr[0] * fArr2[0]) + (f * f2) + (f3 * f4), (f5 * f6) + (f2 * f7) + (f8 * f4), (fArr[2] * f6) + (fArr2[1] * f9) + (f4 * f10), f13 + (f3 * f14), (f15 * f16) + (f7 * f12) + (f8 * f14), (f16 * f17) + (f9 * fArr2[4]) + (f14 * f10), f21 + (f3 * f22), (f15 * f23) + (fArr[4] * f20) + (f8 * f22), (f17 * f23) + (fArr[5] * fArr2[7]) + (f10 * f22)};
    }

    @Override // com.android.server.display.color.TintController
    public void setMatrix(int i) {
        if (!this.mSetUp) {
            Slog.w("ColorDisplayService", "Can't set display white balance temperature: uninitialized");
            return;
        }
        if (i < this.mTemperatureMin) {
            Slog.w("ColorDisplayService", "Requested display color temperature is below allowed minimum");
            i = this.mTemperatureMin;
        } else if (i > this.mTemperatureMax) {
            Slog.w("ColorDisplayService", "Requested display color temperature is above allowed maximum");
            i = this.mTemperatureMax;
        }
        synchronized (this.mLock) {
            this.mCurrentColorTemperature = i;
            float[] cctToXyz = ColorSpace.cctToXyz(i);
            this.mCurrentColorTemperatureXYZ = cctToXyz;
            float[] chromaticAdaptation = ColorSpace.chromaticAdaptation(ColorSpace.Adaptation.BRADFORD, this.mDisplayNominalWhiteXYZ, cctToXyz);
            this.mChromaticAdaptationMatrix = chromaticAdaptation;
            float[] mul3x3 = mul3x3(this.mDisplayColorSpaceRGB.getInverseTransform(), mul3x3(chromaticAdaptation, this.mDisplayColorSpaceRGB.getTransform()));
            float max = Math.max(Math.max(mul3x3[0] + mul3x3[3] + mul3x3[6], mul3x3[1] + mul3x3[4] + mul3x3[7]), mul3x3[2] + mul3x3[5] + mul3x3[8]);
            Matrix.setIdentityM(this.mMatrixDisplayWhiteBalance, 0);
            for (int i2 = 0; i2 < mul3x3.length; i2++) {
                float f = mul3x3[i2] / max;
                mul3x3[i2] = f;
                if (!isColorMatrixCoeffValid(f)) {
                    Slog.e("ColorDisplayService", "Invalid DWB color matrix");
                    return;
                }
            }
            System.arraycopy(mul3x3, 0, this.mMatrixDisplayWhiteBalance, 0, 3);
            System.arraycopy(mul3x3, 3, this.mMatrixDisplayWhiteBalance, 4, 3);
            System.arraycopy(mul3x3, 6, this.mMatrixDisplayWhiteBalance, 8, 3);
            Slog.d("ColorDisplayService", "setDisplayWhiteBalanceTemperatureMatrix: cct = " + i + " matrix = " + TintController.matrixToString(this.mMatrixDisplayWhiteBalance, 16));
        }
    }

    @Override // com.android.server.display.color.TintController
    public boolean isAvailable(Context context) {
        if (this.mIsAvailable == null) {
            this.mIsAvailable = Boolean.valueOf(ColorDisplayManager.isDisplayWhiteBalanceAvailable(context));
        }
        return this.mIsAvailable.booleanValue();
    }

    @Override // com.android.server.display.color.TintController
    public long getTransitionDurationMilliseconds() {
        return this.mTransitionDuration;
    }

    public void dump(PrintWriter printWriter) {
        synchronized (this.mLock) {
            printWriter.println("    mSetUp = " + this.mSetUp);
            if (this.mSetUp) {
                printWriter.println("    mTemperatureMin = " + this.mTemperatureMin);
                printWriter.println("    mTemperatureMax = " + this.mTemperatureMax);
                printWriter.println("    mTemperatureDefault = " + this.mTemperatureDefault);
                printWriter.println("    mCurrentColorTemperature = " + this.mCurrentColorTemperature);
                printWriter.println("    mCurrentColorTemperatureXYZ = " + TintController.matrixToString(this.mCurrentColorTemperatureXYZ, 3));
                printWriter.println("    mDisplayColorSpaceRGB RGB-to-XYZ = " + TintController.matrixToString(this.mDisplayColorSpaceRGB.getTransform(), 3));
                printWriter.println("    mChromaticAdaptationMatrix = " + TintController.matrixToString(this.mChromaticAdaptationMatrix, 3));
                printWriter.println("    mDisplayColorSpaceRGB XYZ-to-RGB = " + TintController.matrixToString(this.mDisplayColorSpaceRGB.getInverseTransform(), 3));
                printWriter.println("    mMatrixDisplayWhiteBalance = " + TintController.matrixToString(this.mMatrixDisplayWhiteBalance, 4));
                printWriter.println("    mIsAllowed = " + this.mIsAllowed);
            }
        }
    }

    public float getLuminance() {
        synchronized (this.mLock) {
            float[] fArr = this.mChromaticAdaptationMatrix;
            if (fArr == null || fArr.length != 9) {
                return -1.0f;
            }
            return 1.0f / ((fArr[1] + fArr[4]) + fArr[7]);
        }
    }

    public void setAllowed(boolean z) {
        this.mIsAllowed = z;
    }

    public boolean isAllowed() {
        return this.mIsAllowed;
    }

    public final ColorSpace.Rgb makeRgbColorSpaceFromXYZ(float[] fArr, float[] fArr2) {
        return new ColorSpace.Rgb("Display Color Space", fArr, fArr2, 2.200000047683716d);
    }

    public final ColorSpace.Rgb getDisplayColorSpaceFromSurfaceControl() {
        SurfaceControl.CieXyz cieXyz;
        SurfaceControl.CieXyz cieXyz2;
        SurfaceControl.CieXyz cieXyz3;
        SurfaceControl.CieXyz cieXyz4;
        SurfaceControl.DisplayPrimaries displayNativePrimaries = this.mDisplayManagerInternal.getDisplayNativePrimaries(0);
        if (displayNativePrimaries == null || (cieXyz = displayNativePrimaries.red) == null || (cieXyz2 = displayNativePrimaries.green) == null || (cieXyz3 = displayNativePrimaries.blue) == null || (cieXyz4 = displayNativePrimaries.white) == null) {
            return null;
        }
        return makeRgbColorSpaceFromXYZ(new float[]{cieXyz.X, cieXyz.Y, cieXyz.Z, cieXyz2.X, cieXyz2.Y, cieXyz2.Z, cieXyz3.X, cieXyz3.Y, cieXyz3.Z}, new float[]{cieXyz4.X, cieXyz4.Y, cieXyz4.Z});
    }

    public final ColorSpace.Rgb getDisplayColorSpaceFromResources(Resources resources) {
        String[] stringArray = resources.getStringArray(17236049);
        float[] fArr = new float[9];
        float[] fArr2 = new float[3];
        for (int i = 0; i < 9; i++) {
            fArr[i] = Float.parseFloat(stringArray[i]);
        }
        for (int i2 = 0; i2 < 3; i2++) {
            fArr2[i2] = Float.parseFloat(stringArray[9 + i2]);
        }
        return makeRgbColorSpaceFromXYZ(fArr, fArr2);
    }

    public final boolean isColorMatrixCoeffValid(float f) {
        return (Float.isNaN(f) || Float.isInfinite(f)) ? false : true;
    }

    public final boolean isColorMatrixValid(float[] fArr) {
        if (fArr == null || fArr.length != 9) {
            return false;
        }
        for (float f : fArr) {
            if (!isColorMatrixCoeffValid(f)) {
                return false;
            }
        }
        return true;
    }
}
