package com.android.internal.graphics.palette;
/* loaded from: classes4.dex */
public final class Target {
    private static final float DEFAULT_CHROMA_MAX = 130.0f;
    private static final float DEFAULT_CHROMA_MIN = 0.0f;
    private static final float DEFAULT_CHROMA_TARGET = 30.0f;
    private static final float WEIGHT_CHROMA = 0.5f;
    private static final float WEIGHT_HUE = 0.2f;
    private static final float WEIGHT_POPULATION = 0.3f;
    private static final float WEIGHT_RELATIVE_LUMINANCE = 0.5f;
    private float mChromaMax;
    private float mChromaMin;
    private float mChromaTarget;
    private float mChromaWeight;
    private float mHueWeight;
    private float mPopulationWeight;
    private float mRelativeLuminanceWeight;
    private float mTargetHue;
    private float mTargetRelativeLuminance;

    Target() {
        this.mTargetRelativeLuminance = -1.0f;
        this.mChromaMax = DEFAULT_CHROMA_MAX;
        this.mChromaMin = 0.0f;
        this.mChromaTarget = 30.0f;
        this.mChromaWeight = 0.5f;
        this.mRelativeLuminanceWeight = 0.5f;
        this.mPopulationWeight = WEIGHT_POPULATION;
        this.mHueWeight = 0.2f;
    }

    Target(Target from) {
        this.mTargetRelativeLuminance = -1.0f;
        this.mTargetRelativeLuminance = from.mTargetRelativeLuminance;
        this.mChromaWeight = from.mChromaWeight;
        this.mRelativeLuminanceWeight = from.mRelativeLuminanceWeight;
        this.mPopulationWeight = from.mPopulationWeight;
        this.mHueWeight = from.mHueWeight;
        this.mChromaTarget = from.mChromaTarget;
        this.mChromaMin = from.mChromaMin;
        this.mChromaMax = from.mChromaMax;
    }

    public float getTargetRelativeLuminance() {
        return this.mTargetRelativeLuminance;
    }

    public float getTargetPerceptualLuminance() {
        return Contrast.yToLstar(this.mTargetRelativeLuminance);
    }

    public float getMinimumChroma() {
        return this.mChromaMin;
    }

    public float getTargetChroma() {
        return this.mChromaTarget;
    }

    public float getMaximumChroma() {
        return this.mChromaMax;
    }

    public float getTargetHue() {
        return this.mTargetHue;
    }

    public float getChromaWeight() {
        return this.mChromaWeight;
    }

    public float getLightnessWeight() {
        return this.mRelativeLuminanceWeight;
    }

    public float getPopulationWeight() {
        return this.mPopulationWeight;
    }

    public float getHueWeight() {
        return this.mHueWeight;
    }

    /* loaded from: classes4.dex */
    public static class Builder {
        private final Target mTarget;

        public Builder() {
            this.mTarget = new Target();
        }

        public Builder(Target target) {
            this.mTarget = new Target(target);
        }

        public Builder setMinimumChroma(float value) {
            this.mTarget.mChromaMin = value;
            return this;
        }

        public Builder setTargetChroma(float value) {
            this.mTarget.mChromaTarget = value;
            return this;
        }

        public Builder setMaximumChroma(float value) {
            this.mTarget.mChromaMax = value;
            return this;
        }

        public Builder setTargetRelativeLuminance(float value) {
            this.mTarget.mTargetRelativeLuminance = value;
            return this;
        }

        public Builder setTargetPerceptualLuminance(float value) {
            this.mTarget.mTargetRelativeLuminance = Contrast.lstarToY(value);
            return this;
        }

        public Builder setTargetHue(int hue) {
            this.mTarget.mTargetHue = hue;
            return this;
        }

        public Builder setContrastRatio(float value, float relativeLuminance) {
            float targetY;
            float lstar = Contrast.yToLstar(relativeLuminance);
            if (lstar < 50.0f) {
                targetY = Contrast.lighterY(relativeLuminance, value);
            } else {
                targetY = Contrast.darkerY(relativeLuminance, value);
            }
            this.mTarget.mTargetRelativeLuminance = targetY;
            return this;
        }

        public Builder setChromaWeight(float weight) {
            this.mTarget.mChromaWeight = weight;
            return this;
        }

        public Builder setLightnessWeight(float weight) {
            this.mTarget.mRelativeLuminanceWeight = weight;
            return this;
        }

        public Builder setPopulationWeight(float weight) {
            this.mTarget.mPopulationWeight = weight;
            return this;
        }

        public Target build() {
            return this.mTarget;
        }
    }
}
