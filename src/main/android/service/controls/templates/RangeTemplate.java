package android.service.controls.templates;

import android.p008os.Bundle;
/* loaded from: classes3.dex */
public final class RangeTemplate extends ControlTemplate {
    private static final String KEY_CURRENT_VALUE = "key_current_value";
    private static final String KEY_FORMAT_STRING = "key_format_string";
    private static final String KEY_MAX_VALUE = "key_max_value";
    private static final String KEY_MIN_VALUE = "key_min_value";
    private static final String KEY_STEP_VALUE = "key_step_value";
    private static final int TYPE = 2;
    private final float mCurrentValue;
    private final CharSequence mFormatString;
    private final float mMaxValue;
    private final float mMinValue;
    private final float mStepValue;

    public RangeTemplate(String templateId, float minValue, float maxValue, float currentValue, float stepValue, CharSequence formatString) {
        super(templateId);
        this.mMinValue = minValue;
        this.mMaxValue = maxValue;
        this.mCurrentValue = currentValue;
        this.mStepValue = stepValue;
        if (formatString != null) {
            this.mFormatString = formatString;
        } else {
            this.mFormatString = "%.1f";
        }
        validate();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public RangeTemplate(Bundle b) {
        super(b);
        this.mMinValue = b.getFloat(KEY_MIN_VALUE);
        this.mMaxValue = b.getFloat(KEY_MAX_VALUE);
        this.mCurrentValue = b.getFloat(KEY_CURRENT_VALUE);
        this.mStepValue = b.getFloat(KEY_STEP_VALUE);
        this.mFormatString = b.getCharSequence(KEY_FORMAT_STRING, "%.1f");
        validate();
    }

    public float getMinValue() {
        return this.mMinValue;
    }

    public float getMaxValue() {
        return this.mMaxValue;
    }

    public float getCurrentValue() {
        return this.mCurrentValue;
    }

    public float getStepValue() {
        return this.mStepValue;
    }

    public CharSequence getFormatString() {
        return this.mFormatString;
    }

    @Override // android.service.controls.templates.ControlTemplate
    public int getTemplateType() {
        return 2;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // android.service.controls.templates.ControlTemplate
    public Bundle getDataBundle() {
        Bundle b = super.getDataBundle();
        b.putFloat(KEY_MIN_VALUE, this.mMinValue);
        b.putFloat(KEY_MAX_VALUE, this.mMaxValue);
        b.putFloat(KEY_CURRENT_VALUE, this.mCurrentValue);
        b.putFloat(KEY_STEP_VALUE, this.mStepValue);
        b.putCharSequence(KEY_FORMAT_STRING, this.mFormatString);
        return b;
    }

    private void validate() {
        if (Float.compare(this.mMinValue, this.mMaxValue) > 0) {
            throw new IllegalArgumentException(String.format("minValue=%f > maxValue=%f", Float.valueOf(this.mMinValue), Float.valueOf(this.mMaxValue)));
        }
        if (Float.compare(this.mMinValue, this.mCurrentValue) > 0) {
            throw new IllegalArgumentException(String.format("minValue=%f > currentValue=%f", Float.valueOf(this.mMinValue), Float.valueOf(this.mCurrentValue)));
        }
        if (Float.compare(this.mCurrentValue, this.mMaxValue) > 0) {
            throw new IllegalArgumentException(String.format("currentValue=%f > maxValue=%f", Float.valueOf(this.mCurrentValue), Float.valueOf(this.mMaxValue)));
        }
        if (this.mStepValue <= 0.0f) {
            throw new IllegalArgumentException(String.format("stepValue=%f <= 0", Float.valueOf(this.mStepValue)));
        }
    }
}
