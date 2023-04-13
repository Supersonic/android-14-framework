package com.android.internal.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.RemotableViewMethod;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.TextView;
import com.android.internal.C4057R;
import java.util.Locale;
@RemoteViews.RemoteView
/* loaded from: classes5.dex */
public class NotificationExpandButton extends FrameLayout {
    private int mDefaultPillColor;
    private int mDefaultTextColor;
    private boolean mExpanded;
    private int mHighlightPillColor;
    private int mHighlightTextColor;
    private ImageView mIconView;
    private int mNumber;
    private TextView mNumberView;
    private View mPillView;

    public NotificationExpandButton(Context context) {
        this(context, null, 0, 0);
    }

    public NotificationExpandButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0, 0);
    }

    public NotificationExpandButton(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public NotificationExpandButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.View
    public void onFinishInflate() {
        super.onFinishInflate();
        this.mPillView = findViewById(C4057R.C4059id.expand_button_pill);
        this.mNumberView = (TextView) findViewById(C4057R.C4059id.expand_button_number);
        this.mIconView = (ImageView) findViewById(C4057R.C4059id.expand_button_icon);
    }

    @Override // android.view.View
    public void getBoundsOnScreen(Rect outRect, boolean clipToParent) {
        ViewGroup parent = (ViewGroup) getParent();
        if (parent != null && parent.getId() == 16908992) {
            parent.getBoundsOnScreen(outRect, clipToParent);
        } else {
            super.getBoundsOnScreen(outRect, clipToParent);
        }
    }

    @Override // android.view.View
    public boolean pointInView(float localX, float localY, float slop) {
        ViewGroup parent = (ViewGroup) getParent();
        if (parent != null && parent.getId() == 16908992) {
            return true;
        }
        return super.pointInView(localX, localY, slop);
    }

    @Override // android.view.View
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setClassName(Button.class.getName());
    }

    @RemotableViewMethod
    public void setExpanded(boolean expanded) {
        this.mExpanded = expanded;
        updateExpandedState();
    }

    private void updateExpandedState() {
        int drawableId;
        int contentDescriptionId;
        if (this.mExpanded) {
            drawableId = C4057R.C4058drawable.ic_collapse_notification;
            contentDescriptionId = C4057R.string.expand_button_content_description_expanded;
        } else {
            drawableId = C4057R.C4058drawable.ic_expand_notification;
            contentDescriptionId = C4057R.string.expand_button_content_description_collapsed;
        }
        setContentDescription(this.mContext.getText(contentDescriptionId));
        this.mIconView.setImageDrawable(getContext().getDrawable(drawableId));
        updateNumber();
    }

    private void updateNumber() {
        CharSequence text;
        if (shouldShowNumber()) {
            if (this.mNumber >= 100) {
                text = getResources().getString(C4057R.string.unread_convo_overflow, 99);
            } else {
                text = String.format(Locale.getDefault(), "%d", Integer.valueOf(this.mNumber));
            }
            this.mNumberView.setText(text);
            this.mNumberView.setVisibility(0);
        } else {
            this.mNumberView.setVisibility(8);
        }
        updateColors();
    }

    private void updateColors() {
        if (shouldShowNumber()) {
            int i = this.mHighlightPillColor;
            if (i != 0) {
                this.mPillView.setBackgroundTintList(ColorStateList.valueOf(i));
            }
            this.mIconView.setColorFilter(this.mHighlightTextColor);
            int i2 = this.mHighlightTextColor;
            if (i2 != 0) {
                this.mNumberView.setTextColor(i2);
                return;
            }
            return;
        }
        int i3 = this.mDefaultPillColor;
        if (i3 != 0) {
            this.mPillView.setBackgroundTintList(ColorStateList.valueOf(i3));
        }
        this.mIconView.setColorFilter(this.mDefaultTextColor);
        int i4 = this.mDefaultTextColor;
        if (i4 != 0) {
            this.mNumberView.setTextColor(i4);
        }
    }

    private boolean shouldShowNumber() {
        return !this.mExpanded && this.mNumber > 1;
    }

    @RemotableViewMethod
    public void setDefaultTextColor(int color) {
        this.mDefaultTextColor = color;
        updateColors();
    }

    @RemotableViewMethod
    public void setDefaultPillColor(int color) {
        this.mDefaultPillColor = color;
        updateColors();
    }

    @RemotableViewMethod
    public void setHighlightTextColor(int color) {
        this.mHighlightTextColor = color;
        updateColors();
    }

    @RemotableViewMethod
    public void setHighlightPillColor(int color) {
        this.mHighlightPillColor = color;
        updateColors();
    }

    @RemotableViewMethod
    public void setNumber(int number) {
        if (this.mNumber != number) {
            this.mNumber = number;
            updateNumber();
        }
    }
}
