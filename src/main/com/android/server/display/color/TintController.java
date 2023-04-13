package com.android.server.display.color;

import android.content.Context;
import android.util.Slog;
import com.android.server.backup.BackupAgentTimeoutParameters;
import com.android.server.display.color.ColorDisplayService;
/* loaded from: classes.dex */
public abstract class TintController {
    public ColorDisplayService.TintValueAnimator mAnimator;
    public Boolean mIsActivated;

    public abstract int getLevel();

    public abstract float[] getMatrix();

    public long getTransitionDurationMilliseconds() {
        return BackupAgentTimeoutParameters.DEFAULT_QUOTA_EXCEEDED_TIMEOUT_MILLIS;
    }

    public abstract boolean isAvailable(Context context);

    public abstract void setMatrix(int i);

    public void setAnimator(ColorDisplayService.TintValueAnimator tintValueAnimator) {
        this.mAnimator = tintValueAnimator;
    }

    public void cancelAnimator() {
        ColorDisplayService.TintValueAnimator tintValueAnimator = this.mAnimator;
        if (tintValueAnimator != null) {
            tintValueAnimator.cancel();
        }
    }

    public void endAnimator() {
        ColorDisplayService.TintValueAnimator tintValueAnimator = this.mAnimator;
        if (tintValueAnimator != null) {
            tintValueAnimator.end();
            this.mAnimator = null;
        }
    }

    public void setActivated(Boolean bool) {
        this.mIsActivated = bool;
    }

    public boolean isActivated() {
        Boolean bool = this.mIsActivated;
        return bool != null && bool.booleanValue();
    }

    public boolean isActivatedStateNotSet() {
        return this.mIsActivated == null;
    }

    public static String matrixToString(float[] fArr, int i) {
        if (fArr == null || i <= 0) {
            StringBuilder sb = new StringBuilder();
            sb.append("Invalid arguments when formatting matrix to string, matrix is null: ");
            sb.append(fArr == null);
            sb.append(" columns: ");
            sb.append(i);
            Slog.e("ColorDisplayService", sb.toString());
            return "";
        }
        StringBuilder sb2 = new StringBuilder("");
        for (int i2 = 0; i2 < fArr.length; i2++) {
            if (i2 % i == 0) {
                sb2.append("\n      ");
            }
            sb2.append(String.format("%9.6f", Float.valueOf(fArr[i2])));
        }
        return sb2.toString();
    }
}
