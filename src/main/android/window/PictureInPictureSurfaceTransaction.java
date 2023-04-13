package android.window;

import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.Rect;
import android.inputmethodservice.navigationbar.NavigationBarInflaterView;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.view.SurfaceControl;
import java.util.Arrays;
import java.util.Objects;
/* loaded from: classes4.dex */
public final class PictureInPictureSurfaceTransaction implements Parcelable {
    public static final Parcelable.Creator<PictureInPictureSurfaceTransaction> CREATOR = new Parcelable.Creator<PictureInPictureSurfaceTransaction>() { // from class: android.window.PictureInPictureSurfaceTransaction.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public PictureInPictureSurfaceTransaction createFromParcel(Parcel in) {
            return new PictureInPictureSurfaceTransaction(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.p008os.Parcelable.Creator
        public PictureInPictureSurfaceTransaction[] newArray(int size) {
            return new PictureInPictureSurfaceTransaction[size];
        }
    };
    private static final float NOT_SET = -1.0f;
    public final float mAlpha;
    public final float mCornerRadius;
    public final float[] mFloat9;
    public final PointF mPosition;
    public final float mRotation;
    public final float mShadowRadius;
    private boolean mShouldDisableCanAffectSystemUiFlags;
    private final Rect mWindowCrop;

    private PictureInPictureSurfaceTransaction(Parcel in) {
        this.mAlpha = in.readFloat();
        this.mPosition = (PointF) in.readTypedObject(PointF.CREATOR);
        float[] fArr = new float[9];
        this.mFloat9 = fArr;
        in.readFloatArray(fArr);
        this.mRotation = in.readFloat();
        this.mCornerRadius = in.readFloat();
        this.mShadowRadius = in.readFloat();
        this.mWindowCrop = (Rect) in.readTypedObject(Rect.CREATOR);
        this.mShouldDisableCanAffectSystemUiFlags = in.readBoolean();
    }

    private PictureInPictureSurfaceTransaction(float alpha, PointF position, float[] float9, float rotation, float cornerRadius, float shadowRadius, Rect windowCrop) {
        this.mAlpha = alpha;
        this.mPosition = position;
        if (float9 == null) {
            float[] fArr = new float[9];
            this.mFloat9 = fArr;
            Matrix.IDENTITY_MATRIX.getValues(fArr);
            this.mRotation = 0.0f;
        } else {
            this.mFloat9 = Arrays.copyOf(float9, 9);
            this.mRotation = rotation;
        }
        this.mCornerRadius = cornerRadius;
        this.mShadowRadius = shadowRadius;
        this.mWindowCrop = windowCrop == null ? null : new Rect(windowCrop);
    }

    public PictureInPictureSurfaceTransaction(PictureInPictureSurfaceTransaction other) {
        this(other.mAlpha, other.mPosition, other.mFloat9, other.mRotation, other.mCornerRadius, other.mShadowRadius, other.mWindowCrop);
        this.mShouldDisableCanAffectSystemUiFlags = other.mShouldDisableCanAffectSystemUiFlags;
    }

    public Matrix getMatrix() {
        Matrix matrix = new Matrix();
        matrix.setValues(this.mFloat9);
        return matrix;
    }

    public boolean hasCornerRadiusSet() {
        return this.mCornerRadius > 0.0f;
    }

    public boolean hasShadowRadiusSet() {
        return this.mShadowRadius > 0.0f;
    }

    public void setShouldDisableCanAffectSystemUiFlags(boolean shouldDisable) {
        this.mShouldDisableCanAffectSystemUiFlags = shouldDisable;
    }

    public boolean getShouldDisableCanAffectSystemUiFlags() {
        return this.mShouldDisableCanAffectSystemUiFlags;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof PictureInPictureSurfaceTransaction) {
            PictureInPictureSurfaceTransaction that = (PictureInPictureSurfaceTransaction) o;
            return Objects.equals(Float.valueOf(this.mAlpha), Float.valueOf(that.mAlpha)) && Objects.equals(this.mPosition, that.mPosition) && Arrays.equals(this.mFloat9, that.mFloat9) && Objects.equals(Float.valueOf(this.mRotation), Float.valueOf(that.mRotation)) && Objects.equals(Float.valueOf(this.mCornerRadius), Float.valueOf(that.mCornerRadius)) && Objects.equals(Float.valueOf(this.mShadowRadius), Float.valueOf(that.mShadowRadius)) && Objects.equals(this.mWindowCrop, that.mWindowCrop) && this.mShouldDisableCanAffectSystemUiFlags == that.mShouldDisableCanAffectSystemUiFlags;
        }
        return false;
    }

    public int hashCode() {
        return Objects.hash(Float.valueOf(this.mAlpha), this.mPosition, Integer.valueOf(Arrays.hashCode(this.mFloat9)), Float.valueOf(this.mRotation), Float.valueOf(this.mCornerRadius), Float.valueOf(this.mShadowRadius), this.mWindowCrop, Boolean.valueOf(this.mShouldDisableCanAffectSystemUiFlags));
    }

    @Override // android.p008os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.p008os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        out.writeFloat(this.mAlpha);
        out.writeTypedObject(this.mPosition, 0);
        out.writeFloatArray(this.mFloat9);
        out.writeFloat(this.mRotation);
        out.writeFloat(this.mCornerRadius);
        out.writeFloat(this.mShadowRadius);
        out.writeTypedObject(this.mWindowCrop, 0);
        out.writeBoolean(this.mShouldDisableCanAffectSystemUiFlags);
    }

    public String toString() {
        Matrix matrix = getMatrix();
        return "PictureInPictureSurfaceTransaction( alpha=" + this.mAlpha + " position=" + this.mPosition + " matrix=" + matrix.toShortString() + " rotation=" + this.mRotation + " cornerRadius=" + this.mCornerRadius + " shadowRadius=" + this.mShadowRadius + " crop=" + this.mWindowCrop + " shouldDisableCanAffectSystemUiFlags" + this.mShouldDisableCanAffectSystemUiFlags + NavigationBarInflaterView.KEY_CODE_END;
    }

    public static void apply(PictureInPictureSurfaceTransaction surfaceTransaction, SurfaceControl surfaceControl, SurfaceControl.Transaction tx) {
        Matrix matrix = surfaceTransaction.getMatrix();
        tx.setMatrix(surfaceControl, matrix, new float[9]);
        PointF pointF = surfaceTransaction.mPosition;
        if (pointF != null) {
            tx.setPosition(surfaceControl, pointF.f78x, surfaceTransaction.mPosition.f79y);
        }
        Rect rect = surfaceTransaction.mWindowCrop;
        if (rect != null) {
            tx.setWindowCrop(surfaceControl, rect);
        }
        if (surfaceTransaction.hasCornerRadiusSet()) {
            tx.setCornerRadius(surfaceControl, surfaceTransaction.mCornerRadius);
        }
        if (surfaceTransaction.hasShadowRadiusSet()) {
            tx.setShadowRadius(surfaceControl, surfaceTransaction.mShadowRadius);
        }
        float f = surfaceTransaction.mAlpha;
        if (f != -1.0f) {
            tx.setAlpha(surfaceControl, f);
        }
    }

    /* loaded from: classes4.dex */
    public static class Builder {
        private float[] mFloat9;
        private PointF mPosition;
        private float mRotation;
        private Rect mWindowCrop;
        private float mAlpha = -1.0f;
        private float mCornerRadius = -1.0f;
        private float mShadowRadius = -1.0f;

        public Builder setAlpha(float alpha) {
            this.mAlpha = alpha;
            return this;
        }

        public Builder setPosition(float x, float y) {
            this.mPosition = new PointF(x, y);
            return this;
        }

        public Builder setTransform(float[] float9, float rotation) {
            this.mFloat9 = Arrays.copyOf(float9, 9);
            this.mRotation = rotation;
            return this;
        }

        public Builder setCornerRadius(float cornerRadius) {
            this.mCornerRadius = cornerRadius;
            return this;
        }

        public Builder setShadowRadius(float shadowRadius) {
            this.mShadowRadius = shadowRadius;
            return this;
        }

        public Builder setWindowCrop(Rect windowCrop) {
            this.mWindowCrop = new Rect(windowCrop);
            return this;
        }

        public PictureInPictureSurfaceTransaction build() {
            return new PictureInPictureSurfaceTransaction(this.mAlpha, this.mPosition, this.mFloat9, this.mRotation, this.mCornerRadius, this.mShadowRadius, this.mWindowCrop);
        }
    }
}
