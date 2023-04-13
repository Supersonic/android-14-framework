package android.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.RemoteViews;
import com.android.internal.C4057R;
@RemoteViews.RemoteView
/* loaded from: classes4.dex */
public class RadioButton extends CompoundButton {
    public RadioButton(Context context) {
        this(context, null);
    }

    public RadioButton(Context context, AttributeSet attrs) {
        this(context, attrs, 16842878);
    }

    public RadioButton(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public RadioButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override // android.widget.CompoundButton, android.widget.Checkable
    public void toggle() {
        if (!isChecked()) {
            super.toggle();
        }
    }

    @Override // android.widget.CompoundButton, android.widget.Button, android.widget.TextView, android.view.View
    public CharSequence getAccessibilityClassName() {
        return RadioButton.class.getName();
    }

    @Override // android.view.View
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        if (getParent() instanceof RadioGroup) {
            RadioGroup radioGroup = (RadioGroup) getParent();
            if (radioGroup.getOrientation() == 0) {
                info.setCollectionItemInfo(AccessibilityNodeInfo.CollectionItemInfo.obtain(0, 1, radioGroup.getIndexWithinVisibleButtons(this), 1, false, isChecked()));
            } else {
                info.setCollectionItemInfo(AccessibilityNodeInfo.CollectionItemInfo.obtain(radioGroup.getIndexWithinVisibleButtons(this), 1, 0, 1, false, isChecked()));
            }
        }
    }

    @Override // android.widget.CompoundButton
    protected CharSequence getButtonStateDescription() {
        if (isChecked()) {
            return getResources().getString(C4057R.string.selected);
        }
        return getResources().getString(C4057R.string.not_selected);
    }
}
