package android.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.shapes.RectShape;
import android.graphics.drawable.shapes.Shape;
import android.util.AttributeSet;
import android.util.PluralsMessageFormatter;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.inspector.InspectionCompanion;
import android.view.inspector.PropertyMapper;
import android.view.inspector.PropertyReader;
import com.android.internal.C4057R;
import java.util.HashMap;
/* loaded from: classes4.dex */
public class RatingBar extends AbsSeekBar {
    public static final String PLURALS_MAX = "max";
    public static final String PLURALS_RATING = "rating";
    private int mNumStars;
    private OnRatingBarChangeListener mOnRatingBarChangeListener;
    private int mProgressOnStartTracking;

    /* loaded from: classes4.dex */
    public interface OnRatingBarChangeListener {
        void onRatingChanged(RatingBar ratingBar, float f, boolean z);
    }

    /* loaded from: classes4.dex */
    public final class InspectionCompanion implements android.view.inspector.InspectionCompanion<RatingBar> {
        private int mIsIndicatorId;
        private int mNumStarsId;
        private boolean mPropertiesMapped = false;
        private int mRatingId;
        private int mStepSizeId;

        @Override // android.view.inspector.InspectionCompanion
        public void mapProperties(PropertyMapper propertyMapper) {
            this.mIsIndicatorId = propertyMapper.mapBoolean("isIndicator", 16843079);
            this.mNumStarsId = propertyMapper.mapInt("numStars", 16843076);
            this.mRatingId = propertyMapper.mapFloat(RatingBar.PLURALS_RATING, 16843077);
            this.mStepSizeId = propertyMapper.mapFloat("stepSize", 16843078);
            this.mPropertiesMapped = true;
        }

        @Override // android.view.inspector.InspectionCompanion
        public void readProperties(RatingBar node, PropertyReader propertyReader) {
            if (!this.mPropertiesMapped) {
                throw new InspectionCompanion.UninitializedPropertyMapException();
            }
            propertyReader.readBoolean(this.mIsIndicatorId, node.isIndicator());
            propertyReader.readInt(this.mNumStarsId, node.getNumStars());
            propertyReader.readFloat(this.mRatingId, node.getRating());
            propertyReader.readFloat(this.mStepSizeId, node.getStepSize());
        }
    }

    public RatingBar(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public RatingBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.mNumStars = 5;
        TypedArray a = context.obtainStyledAttributes(attrs, C4057R.styleable.RatingBar, defStyleAttr, defStyleRes);
        saveAttributeDataForStyleable(context, C4057R.styleable.RatingBar, attrs, a, defStyleAttr, defStyleRes);
        int numStars = a.getInt(0, this.mNumStars);
        setIsIndicator(a.getBoolean(3, !this.mIsUserSeekable));
        float rating = a.getFloat(1, -1.0f);
        float stepSize = a.getFloat(2, -1.0f);
        a.recycle();
        if (numStars > 0 && numStars != this.mNumStars) {
            setNumStars(numStars);
        }
        if (stepSize >= 0.0f) {
            setStepSize(stepSize);
        } else {
            setStepSize(0.5f);
        }
        if (rating >= 0.0f) {
            setRating(rating);
        }
        this.mTouchProgressOffset = 0.6f;
    }

    public RatingBar(Context context, AttributeSet attrs) {
        this(context, attrs, 16842876);
    }

    public RatingBar(Context context) {
        this(context, null);
    }

    public void setOnRatingBarChangeListener(OnRatingBarChangeListener listener) {
        this.mOnRatingBarChangeListener = listener;
    }

    public OnRatingBarChangeListener getOnRatingBarChangeListener() {
        return this.mOnRatingBarChangeListener;
    }

    public void setIsIndicator(boolean isIndicator) {
        this.mIsUserSeekable = !isIndicator;
        if (isIndicator) {
            setFocusable(16);
        } else {
            setFocusable(1);
        }
    }

    public boolean isIndicator() {
        return !this.mIsUserSeekable;
    }

    public void setNumStars(int numStars) {
        if (numStars <= 0) {
            return;
        }
        this.mNumStars = numStars;
        requestLayout();
    }

    public int getNumStars() {
        return this.mNumStars;
    }

    public void setRating(float rating) {
        setProgress(Math.round(getProgressPerStar() * rating));
    }

    public float getRating() {
        return getProgress() / getProgressPerStar();
    }

    public void setStepSize(float stepSize) {
        if (stepSize <= 0.0f) {
            return;
        }
        float newMax = this.mNumStars / stepSize;
        int newProgress = (int) ((newMax / getMax()) * getProgress());
        setMax((int) newMax);
        setProgress(newProgress);
    }

    public float getStepSize() {
        return getNumStars() / getMax();
    }

    private float getProgressPerStar() {
        if (this.mNumStars > 0) {
            return (getMax() * 1.0f) / this.mNumStars;
        }
        return 1.0f;
    }

    @Override // android.widget.ProgressBar
    Shape getDrawableShape() {
        return new RectShape();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // android.widget.ProgressBar
    public void onProgressRefresh(float scale, boolean fromUser, int progress) {
        super.onProgressRefresh(scale, fromUser, progress);
        updateSecondaryProgress(progress);
        if (!fromUser) {
            dispatchRatingChange(false);
        }
    }

    private void updateSecondaryProgress(int progress) {
        float ratio = getProgressPerStar();
        if (ratio > 0.0f) {
            float progressInStars = progress / ratio;
            int secondaryProgress = (int) (Math.ceil(progressInStars) * ratio);
            setSecondaryProgress(secondaryProgress);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.widget.AbsSeekBar, android.widget.ProgressBar, android.view.View
    public synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (this.mSampleWidth > 0) {
            int width = this.mSampleWidth * this.mNumStars;
            setMeasuredDimension(resolveSizeAndState(width, widthMeasureSpec, 0), getMeasuredHeight());
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // android.widget.AbsSeekBar
    public void onStartTrackingTouch() {
        this.mProgressOnStartTracking = getProgress();
        super.onStartTrackingTouch();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // android.widget.AbsSeekBar
    public void onStopTrackingTouch() {
        super.onStopTrackingTouch();
        if (getProgress() != this.mProgressOnStartTracking) {
            dispatchRatingChange(true);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // android.widget.AbsSeekBar
    public void onKeyChange() {
        super.onKeyChange();
        dispatchRatingChange(true);
    }

    void dispatchRatingChange(boolean fromUser) {
        OnRatingBarChangeListener onRatingBarChangeListener = this.mOnRatingBarChangeListener;
        if (onRatingBarChangeListener != null) {
            onRatingBarChangeListener.onRatingChanged(this, getRating(), fromUser);
        }
    }

    @Override // android.widget.AbsSeekBar, android.widget.ProgressBar
    public synchronized void setMax(int max) {
        if (max <= 0) {
            return;
        }
        super.setMax(max);
    }

    @Override // android.widget.AbsSeekBar, android.widget.ProgressBar, android.view.View
    public CharSequence getAccessibilityClassName() {
        return RatingBar.class.getName();
    }

    @Override // android.widget.AbsSeekBar, android.widget.ProgressBar, android.view.View
    public void onInitializeAccessibilityNodeInfoInternal(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfoInternal(info);
        if (canUserSetProgress()) {
            info.addAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_SET_PROGRESS);
        }
        float scaledMax = getMax() * getStepSize();
        HashMap<String, Object> params = new HashMap<>();
        params.put(PLURALS_RATING, Float.valueOf(getRating()));
        params.put("max", Float.valueOf(scaledMax));
        info.setStateDescription(PluralsMessageFormatter.format(getContext().getResources(), params, C4057R.string.rating_label));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // android.widget.AbsSeekBar
    public boolean canUserSetProgress() {
        return super.canUserSetProgress() && !isIndicator();
    }
}
