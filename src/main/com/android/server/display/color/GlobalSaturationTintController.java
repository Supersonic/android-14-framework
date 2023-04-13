package com.android.server.display.color;

import android.content.Context;
import android.hardware.display.ColorDisplayManager;
import android.opengl.Matrix;
import android.util.Slog;
import java.util.Arrays;
/* loaded from: classes.dex */
public final class GlobalSaturationTintController extends TintController {
    public final float[] mMatrixGlobalSaturation = new float[16];

    @Override // com.android.server.display.color.TintController
    public int getLevel() {
        return 150;
    }

    @Override // com.android.server.display.color.TintController
    public float[] getMatrix() {
        float[] fArr = this.mMatrixGlobalSaturation;
        return Arrays.copyOf(fArr, fArr.length);
    }

    @Override // com.android.server.display.color.TintController
    public void setMatrix(int i) {
        if (i < 0) {
            i = 0;
        } else if (i > 100) {
            i = 100;
        }
        Slog.d("ColorDisplayService", "Setting saturation level: " + i);
        if (i == 100) {
            setActivated(Boolean.FALSE);
            Matrix.setIdentityM(this.mMatrixGlobalSaturation, 0);
            return;
        }
        setActivated(Boolean.TRUE);
        float f = i * 0.01f;
        float f2 = 1.0f - f;
        float f3 = 0.231f * f2;
        float[] fArr = {f3, 0.715f * f2, f2 * 0.072f};
        float[] fArr2 = this.mMatrixGlobalSaturation;
        fArr2[0] = f3 + f;
        float f4 = fArr[0];
        fArr2[1] = f4;
        fArr2[2] = f4;
        float f5 = fArr[1];
        fArr2[4] = f5;
        fArr2[5] = f5 + f;
        fArr2[6] = f5;
        float f6 = fArr[2];
        fArr2[8] = f6;
        fArr2[9] = f6;
        fArr2[10] = f6 + f;
    }

    @Override // com.android.server.display.color.TintController
    public boolean isAvailable(Context context) {
        return ColorDisplayManager.isColorTransformAccelerated(context);
    }
}
