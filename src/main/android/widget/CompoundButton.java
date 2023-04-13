package android.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.BlendMode;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.p008os.Parcel;
import android.p008os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.RemotableViewMethod;
import android.view.View;
import android.view.ViewDebug;
import android.view.ViewHierarchyEncoder;
import android.view.ViewStructure;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.autofill.AutofillManager;
import android.view.autofill.AutofillValue;
import android.view.inspector.InspectionCompanion;
import android.view.inspector.PropertyMapper;
import android.view.inspector.PropertyReader;
import com.android.internal.C4057R;
/* loaded from: classes4.dex */
public abstract class CompoundButton extends Button implements Checkable {
    private boolean mBroadcasting;
    private BlendMode mButtonBlendMode;
    private Drawable mButtonDrawable;
    private ColorStateList mButtonTintList;
    private boolean mChecked;
    private boolean mCheckedFromResource;
    private CharSequence mCustomStateDescription;
    private boolean mHasButtonBlendMode;
    private boolean mHasButtonTint;
    private OnCheckedChangeListener mOnCheckedChangeListener;
    private OnCheckedChangeListener mOnCheckedChangeWidgetListener;
    private static final String LOG_TAG = CompoundButton.class.getSimpleName();
    private static final int[] CHECKED_STATE_SET = {16842912};

    /* loaded from: classes4.dex */
    public interface OnCheckedChangeListener {
        void onCheckedChanged(CompoundButton compoundButton, boolean z);
    }

    /* loaded from: classes4.dex */
    public final class InspectionCompanion implements android.view.inspector.InspectionCompanion<CompoundButton> {
        private int mButtonBlendModeId;
        private int mButtonId;
        private int mButtonTintId;
        private int mButtonTintModeId;
        private int mCheckedId;
        private boolean mPropertiesMapped = false;

        @Override // android.view.inspector.InspectionCompanion
        public void mapProperties(PropertyMapper propertyMapper) {
            this.mButtonId = propertyMapper.mapObject("button", 16843015);
            this.mButtonBlendModeId = propertyMapper.mapObject("buttonBlendMode", 3);
            this.mButtonTintId = propertyMapper.mapObject("buttonTint", 16843887);
            this.mButtonTintModeId = propertyMapper.mapObject("buttonTintMode", 16843888);
            this.mCheckedId = propertyMapper.mapBoolean("checked", 16843014);
            this.mPropertiesMapped = true;
        }

        @Override // android.view.inspector.InspectionCompanion
        public void readProperties(CompoundButton node, PropertyReader propertyReader) {
            if (!this.mPropertiesMapped) {
                throw new InspectionCompanion.UninitializedPropertyMapException();
            }
            propertyReader.readObject(this.mButtonId, node.getButtonDrawable());
            propertyReader.readObject(this.mButtonBlendModeId, node.getButtonTintBlendMode());
            propertyReader.readObject(this.mButtonTintId, node.getButtonTintList());
            propertyReader.readObject(this.mButtonTintModeId, node.getButtonTintMode());
            propertyReader.readBoolean(this.mCheckedId, node.isChecked());
        }
    }

    public CompoundButton(Context context) {
        this(context, null);
    }

    public CompoundButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CompoundButton(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public CompoundButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.mButtonTintList = null;
        this.mButtonBlendMode = null;
        this.mHasButtonTint = false;
        this.mHasButtonBlendMode = false;
        this.mCheckedFromResource = false;
        this.mCustomStateDescription = null;
        TypedArray a = context.obtainStyledAttributes(attrs, C4057R.styleable.CompoundButton, defStyleAttr, defStyleRes);
        saveAttributeDataForStyleable(context, C4057R.styleable.CompoundButton, attrs, a, defStyleAttr, defStyleRes);
        Drawable d = a.getDrawable(1);
        if (d != null) {
            lambda$setButtonIconAsync$1(d);
        }
        if (a.hasValue(3)) {
            this.mButtonBlendMode = Drawable.parseBlendMode(a.getInt(3, -1), this.mButtonBlendMode);
            this.mHasButtonBlendMode = true;
        }
        if (a.hasValue(2)) {
            this.mButtonTintList = a.getColorStateList(2);
            this.mHasButtonTint = true;
        }
        boolean checked = a.getBoolean(0, false);
        setChecked(checked);
        this.mCheckedFromResource = true;
        a.recycle();
        applyButtonTint();
    }

    @Override // android.widget.Checkable
    public void toggle() {
        setChecked(!this.mChecked);
    }

    @Override // android.view.View
    public boolean performClick() {
        toggle();
        boolean handled = super.performClick();
        if (!handled) {
            playSoundEffect(0);
        }
        return handled;
    }

    @Override // android.widget.Checkable
    @ViewDebug.ExportedProperty
    public boolean isChecked() {
        return this.mChecked;
    }

    protected CharSequence getButtonStateDescription() {
        if (isChecked()) {
            return getResources().getString(C4057R.string.checked);
        }
        return getResources().getString(C4057R.string.not_checked);
    }

    @Override // android.view.View
    public void setStateDescription(CharSequence stateDescription) {
        this.mCustomStateDescription = stateDescription;
        if (stateDescription == null) {
            setDefaultStateDescription();
        } else {
            super.setStateDescription(stateDescription);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setDefaultStateDescription() {
        if (this.mCustomStateDescription == null) {
            super.setStateDescription(getButtonStateDescription());
        }
    }

    @Override // android.widget.Checkable
    public void setChecked(boolean checked) {
        if (this.mChecked != checked) {
            this.mCheckedFromResource = false;
            this.mChecked = checked;
            refreshDrawableState();
            if (this.mBroadcasting) {
                setDefaultStateDescription();
                return;
            }
            this.mBroadcasting = true;
            OnCheckedChangeListener onCheckedChangeListener = this.mOnCheckedChangeListener;
            if (onCheckedChangeListener != null) {
                onCheckedChangeListener.onCheckedChanged(this, this.mChecked);
            }
            OnCheckedChangeListener onCheckedChangeListener2 = this.mOnCheckedChangeWidgetListener;
            if (onCheckedChangeListener2 != null) {
                onCheckedChangeListener2.onCheckedChanged(this, this.mChecked);
            }
            AutofillManager afm = (AutofillManager) this.mContext.getSystemService(AutofillManager.class);
            if (afm != null) {
                afm.notifyValueChanged(this);
            }
            this.mBroadcasting = false;
        }
        setDefaultStateDescription();
    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener listener) {
        this.mOnCheckedChangeListener = listener;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setOnCheckedChangeWidgetListener(OnCheckedChangeListener listener) {
        this.mOnCheckedChangeWidgetListener = listener;
    }

    @RemotableViewMethod(asyncImpl = "setButtonDrawableAsync")
    public void setButtonDrawable(int resId) {
        Drawable d;
        if (resId != 0) {
            d = getContext().getDrawable(resId);
        } else {
            d = null;
        }
        lambda$setButtonIconAsync$1(d);
    }

    public Runnable setButtonDrawableAsync(int resId) {
        final Drawable drawable = resId == 0 ? null : getContext().getDrawable(resId);
        return new Runnable() { // from class: android.widget.CompoundButton$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                CompoundButton.this.lambda$setButtonDrawableAsync$0(drawable);
            }
        };
    }

    /* renamed from: setButtonDrawable */
    public void lambda$setButtonIconAsync$1(Drawable drawable) {
        Drawable drawable2 = this.mButtonDrawable;
        if (drawable2 != drawable) {
            if (drawable2 != null) {
                drawable2.setCallback(null);
                unscheduleDrawable(this.mButtonDrawable);
            }
            this.mButtonDrawable = drawable;
            if (drawable != null) {
                drawable.setCallback(this);
                drawable.setLayoutDirection(getLayoutDirection());
                if (drawable.isStateful()) {
                    drawable.setState(getDrawableState());
                }
                drawable.setVisible(getVisibility() == 0, false);
                setMinHeight(drawable.getIntrinsicHeight());
                applyButtonTint();
            }
        }
    }

    @Override // android.widget.TextView, android.view.View
    public void onResolveDrawables(int layoutDirection) {
        super.onResolveDrawables(layoutDirection);
        Drawable drawable = this.mButtonDrawable;
        if (drawable != null) {
            drawable.setLayoutDirection(layoutDirection);
        }
    }

    public Drawable getButtonDrawable() {
        return this.mButtonDrawable;
    }

    @RemotableViewMethod(asyncImpl = "setButtonIconAsync")
    public void setButtonIcon(Icon icon) {
        lambda$setButtonIconAsync$1(icon == null ? null : icon.loadDrawable(getContext()));
    }

    public Runnable setButtonIconAsync(Icon icon) {
        final Drawable button = icon == null ? null : icon.loadDrawable(getContext());
        return new Runnable() { // from class: android.widget.CompoundButton$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                CompoundButton.this.lambda$setButtonIconAsync$1(button);
            }
        };
    }

    @RemotableViewMethod
    public void setButtonTintList(ColorStateList tint) {
        this.mButtonTintList = tint;
        this.mHasButtonTint = true;
        applyButtonTint();
    }

    public ColorStateList getButtonTintList() {
        return this.mButtonTintList;
    }

    public void setButtonTintMode(PorterDuff.Mode tintMode) {
        setButtonTintBlendMode(tintMode != null ? BlendMode.fromValue(tintMode.nativeInt) : null);
    }

    @RemotableViewMethod
    public void setButtonTintBlendMode(BlendMode tintMode) {
        this.mButtonBlendMode = tintMode;
        this.mHasButtonBlendMode = true;
        applyButtonTint();
    }

    public PorterDuff.Mode getButtonTintMode() {
        BlendMode blendMode = this.mButtonBlendMode;
        if (blendMode != null) {
            return BlendMode.blendModeToPorterDuffMode(blendMode);
        }
        return null;
    }

    public BlendMode getButtonTintBlendMode() {
        return this.mButtonBlendMode;
    }

    private void applyButtonTint() {
        Drawable drawable = this.mButtonDrawable;
        if (drawable != null) {
            if (this.mHasButtonTint || this.mHasButtonBlendMode) {
                Drawable mutate = drawable.mutate();
                this.mButtonDrawable = mutate;
                if (this.mHasButtonTint) {
                    mutate.setTintList(this.mButtonTintList);
                }
                if (this.mHasButtonBlendMode) {
                    this.mButtonDrawable.setTintBlendMode(this.mButtonBlendMode);
                }
                if (this.mButtonDrawable.isStateful()) {
                    this.mButtonDrawable.setState(getDrawableState());
                }
            }
        }
    }

    @Override // android.widget.Button, android.widget.TextView, android.view.View
    public CharSequence getAccessibilityClassName() {
        return CompoundButton.class.getName();
    }

    @Override // android.widget.TextView, android.view.View
    public void onInitializeAccessibilityEventInternal(AccessibilityEvent event) {
        super.onInitializeAccessibilityEventInternal(event);
        event.setChecked(this.mChecked);
    }

    @Override // android.widget.TextView, android.view.View
    public void onInitializeAccessibilityNodeInfoInternal(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfoInternal(info);
        info.setCheckable(true);
        info.setChecked(this.mChecked);
    }

    @Override // android.widget.TextView
    public int getCompoundPaddingLeft() {
        Drawable buttonDrawable;
        int padding = super.getCompoundPaddingLeft();
        if (!isLayoutRtl() && (buttonDrawable = this.mButtonDrawable) != null) {
            return padding + buttonDrawable.getIntrinsicWidth();
        }
        return padding;
    }

    @Override // android.widget.TextView
    public int getCompoundPaddingRight() {
        Drawable buttonDrawable;
        int padding = super.getCompoundPaddingRight();
        if (isLayoutRtl() && (buttonDrawable = this.mButtonDrawable) != null) {
            return padding + buttonDrawable.getIntrinsicWidth();
        }
        return padding;
    }

    @Override // android.widget.TextView
    public int getHorizontalOffsetForDrawables() {
        Drawable buttonDrawable = this.mButtonDrawable;
        if (buttonDrawable != null) {
            return buttonDrawable.getIntrinsicWidth();
        }
        return 0;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.widget.TextView, android.view.View
    public void onDraw(Canvas canvas) {
        int top;
        Drawable buttonDrawable = this.mButtonDrawable;
        if (buttonDrawable != null) {
            int verticalGravity = getGravity() & 112;
            int drawableHeight = buttonDrawable.getIntrinsicHeight();
            int drawableWidth = buttonDrawable.getIntrinsicWidth();
            switch (verticalGravity) {
                case 16:
                    int top2 = getHeight();
                    top = (top2 - drawableHeight) / 2;
                    break;
                case 80:
                    int top3 = getHeight();
                    top = top3 - drawableHeight;
                    break;
                default:
                    top = 0;
                    break;
            }
            int bottom = top + drawableHeight;
            int left = isLayoutRtl() ? getWidth() - drawableWidth : 0;
            int right = isLayoutRtl() ? getWidth() : drawableWidth;
            buttonDrawable.setBounds(left, top, right, bottom);
            Drawable background = getBackground();
            if (background != null) {
                background.setHotspotBounds(left, top, right, bottom);
            }
        }
        super.onDraw(canvas);
        if (buttonDrawable != null) {
            int scrollX = this.mScrollX;
            int scrollY = this.mScrollY;
            if (scrollX == 0 && scrollY == 0) {
                buttonDrawable.draw(canvas);
                return;
            }
            canvas.translate(scrollX, scrollY);
            buttonDrawable.draw(canvas);
            canvas.translate(-scrollX, -scrollY);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.widget.TextView, android.view.View
    public int[] onCreateDrawableState(int extraSpace) {
        int[] drawableState = super.onCreateDrawableState(extraSpace + 1);
        if (isChecked()) {
            mergeDrawableStates(drawableState, CHECKED_STATE_SET);
        }
        return drawableState;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.widget.TextView, android.view.View
    public void drawableStateChanged() {
        super.drawableStateChanged();
        Drawable buttonDrawable = this.mButtonDrawable;
        if (buttonDrawable != null && buttonDrawable.isStateful() && buttonDrawable.setState(getDrawableState())) {
            invalidateDrawable(buttonDrawable);
        }
    }

    @Override // android.widget.TextView, android.view.View
    public void drawableHotspotChanged(float x, float y) {
        super.drawableHotspotChanged(x, y);
        Drawable drawable = this.mButtonDrawable;
        if (drawable != null) {
            drawable.setHotspot(x, y);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.widget.TextView, android.view.View
    public boolean verifyDrawable(Drawable who) {
        return super.verifyDrawable(who) || who == this.mButtonDrawable;
    }

    @Override // android.widget.TextView, android.view.View
    public void jumpDrawablesToCurrentState() {
        super.jumpDrawablesToCurrentState();
        Drawable drawable = this.mButtonDrawable;
        if (drawable != null) {
            drawable.jumpToCurrentState();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes4.dex */
    public static class SavedState extends View.BaseSavedState {
        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() { // from class: android.widget.CompoundButton.SavedState.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.p008os.Parcelable.Creator
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
        boolean checked;

        SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            this.checked = ((Boolean) in.readValue(null)).booleanValue();
        }

        @Override // android.view.View.BaseSavedState, android.view.AbsSavedState, android.p008os.Parcelable
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeValue(Boolean.valueOf(this.checked));
        }

        public String toString() {
            return "CompoundButton.SavedState{" + Integer.toHexString(System.identityHashCode(this)) + " checked=" + this.checked + "}";
        }
    }

    @Override // android.widget.TextView, android.view.View
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);
        ss.checked = isChecked();
        return ss;
    }

    @Override // android.widget.TextView, android.view.View
    public void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        setChecked(ss.checked);
        requestLayout();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.widget.TextView, android.view.View
    public void encodeProperties(ViewHierarchyEncoder stream) {
        super.encodeProperties(stream);
        stream.addProperty("checked", isChecked());
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.widget.TextView, android.view.View
    public void onProvideStructure(ViewStructure structure, int viewFor, int flags) {
        super.onProvideStructure(structure, viewFor, flags);
        if (viewFor == 1) {
            structure.setDataIsSensitive(true ^ this.mCheckedFromResource);
        }
    }

    @Override // android.widget.TextView, android.view.View
    public void autofill(AutofillValue value) {
        if (isEnabled()) {
            if (!value.isToggle()) {
                Log.m104w(LOG_TAG, value + " could not be autofilled into " + this);
            } else {
                setChecked(value.getToggleValue());
            }
        }
    }

    @Override // android.widget.TextView, android.view.View
    public int getAutofillType() {
        return isEnabled() ? 2 : 0;
    }

    @Override // android.widget.TextView, android.view.View
    public AutofillValue getAutofillValue() {
        if (isEnabled()) {
            return AutofillValue.forToggle(isChecked());
        }
        return null;
    }
}
